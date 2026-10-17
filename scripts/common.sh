# Shared helpers for the scripts in this directory. Source it, don't run it.

STRYKER_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

die() {
    echo "error: $*" >&2
    exit 2
}

# Prints the architecture the JVM in the JDK home $1 runs as (its os.arch: x86_64, aarch64, ...).
jvm_arch() {
    "$1/bin/java" -XshowSettings:properties -version 2>&1 | sed -n 's/^ *os\.arch = //p'
}

# Succeeds if the MiniSat 2.2 library that lib/dynalloy4.jar bundles for macOS has an arm64
# slice, as the one from the stryker branch of mffrias/dynalloy4 does. Earlier ones are x86_64
# only.
bundled_minisat_has_arm64() {
    unzip -p "$STRYKER_ROOT/lib/dynalloy4.jar" x86-mac/libminisat220.jnilib 2>/dev/null \
        | file - | grep -q arm64
}

# Succeeds if lib/dynalloy4.jar bundles MiniSat 2.2 for arm64 Linux (aarch64-linux), as the one
# from the stryker branch of mffrias/dynalloy4 does.
bundled_minisat_has_aarch64_linux() {
    unzip -l "$STRYKER_ROOT/lib/dynalloy4.jar" aarch64-linux/libminisat220.so > /dev/null 2>&1
}

# Prints 8, 21 or 25 if $1 is the home of a JDK 8, 21 or 25 that can run Stryker on this machine,
# or fails. An arm64 JDK can only load MiniSat, the SAT solver Stryker runs with, if
# lib/dynalloy4.jar bundles it for arm64 on that system (macOS or Linux); otherwise, on a Mac,
# the JDK must be an x86_64 build, which runs under Rosetta.
jdk_version() {
    local home="$1" version
    [ -x "$home/bin/java" ] && [ -x "$home/bin/javac" ] || return 1
    version="$("$home/bin/java" -version 2>&1 \
        | sed -n -e 's/.*version "1\.\(8\)[._].*/\1/p' -e 's/.*version "\(2[15]\)[."].*/\1/p' | head -1)"
    [ -n "$version" ] || return 1
    if [ "$(jvm_arch "$home")" = "aarch64" ]; then
        if [ "$(uname -s)" = "Darwin" ]; then
            bundled_minisat_has_arm64 || return 1
        else
            bundled_minisat_has_aarch64_linux || return 1
        fi
    fi
    echo "$version"
}

# Prints the home of the JDK to use, or fails: $STRYKER_JAVA_HOME if set (it must be a usable
# JDK 8, 21 or 25), else $JAVA_HOME if it is a usable JDK 8, else the first JDK 8, then the first
# JDK 21, then the first JDK 25, among $JAVA_HOME and the usual places (macOS:
# /Library/Java/JavaVirtualMachines and its ~/Library counterpart; Linux: /usr/lib/jvm, /opt). So
# JDK 8 stays the default even when $JAVA_HOME is a newer JDK.
find_jdk() {
    if [ -n "${STRYKER_JAVA_HOME:-}" ]; then
        jdk_version "$STRYKER_JAVA_HOME" > /dev/null || return 1
        echo "$STRYKER_JAVA_HOME"
        return 0
    fi
    local candidates=() versions=() dir i wanted
    if [ -n "${JAVA_HOME:-}" ]; then
        if [ "$(jdk_version "$JAVA_HOME")" = 8 ]; then
            echo "$JAVA_HOME"
            return 0
        fi
        candidates+=("$JAVA_HOME")
    fi
    for dir in /Library/Java/JavaVirtualMachines/*/Contents/Home \
        "$HOME"/Library/Java/JavaVirtualMachines/*/Contents/Home \
        /usr/lib/jvm/* /opt/*jdk* /opt/*java*; do
        [ -d "$dir" ] && candidates+=("$dir")
    done
    for i in ${candidates[@]+"${!candidates[@]}"}; do
        versions[$i]="$(jdk_version "${candidates[$i]}" || true)"
    done
    for wanted in 8 21 25; do
        for i in ${candidates[@]+"${!candidates[@]}"}; do
            if [ "${versions[$i]}" = "$wanted" ]; then
                echo "${candidates[$i]}"
                return 0
            fi
        done
    done
    return 1
}

# Sets and exports JAVA_HOME to a usable JDK 8, 21 or 25 (see find_jdk) and STRYKER_JAVA_VERSION
# to its version, or exits with instructions.
require_jdk() {
    if ! JAVA_HOME="$(find_jdk)"; then
        if [ -n "${STRYKER_JAVA_HOME:-}" ]; then
            die "STRYKER_JAVA_HOME=$STRYKER_JAVA_HOME is not a usable JDK 8, 21 or 25
(an arm64 JDK, on Apple Silicon or Linux, needs the MiniSat for arm64 that
dynalloy4.jar from the stryker branch of mffrias/dynalloy4 bundles; with an
older lib/dynalloy4.jar, use an x86_64 JDK, under Rosetta on a Mac)."
        fi
        die "a JDK 8 (or 21 or 25) is required but none was found.
Install one (e.g. Azul Zulu 8) and, if the scripts don't find it, point
STRYKER_JAVA_HOME at it. An arm64 JDK (Apple Silicon, Linux) needs
lib/dynalloy4.jar from the stryker branch of mffrias/dynalloy4 (MiniSat for
arm64); with an older one, on a Mac, install the x86_64 build, which runs under
Rosetta."
    fi
    export JAVA_HOME
    STRYKER_JAVA_VERSION="$(jdk_version "$JAVA_HOME")"
    export STRYKER_JAVA_VERSION
}

# Sets and exports STRYKER_CLASSLIB to the Java 7 class library (rt.jar) that the JML tools
# need: $STRYKER_CLASSLIB if set, else lib/java7/rt.jar, which scripts/fetch-java7-classlib.sh
# creates. Exits with instructions if it does not exist.
require_classlib() {
    STRYKER_CLASSLIB="${STRYKER_CLASSLIB:-$STRYKER_ROOT/lib/java7/rt.jar}"
    [ -f "$STRYKER_CLASSLIB" ] || die "Stryker needs a Java 7 class library for the JML tools, and
$STRYKER_CLASSLIB does not exist. Run scripts/fetch-java7-classlib.sh once,
or set STRYKER_CLASSLIB to a Java 7 rt.jar."
    export STRYKER_CLASSLIB
}

# Sets and exports STRYKER_SOLVER: minisat (the default) or sat4j. With minisat, also checks that
# lib/dynalloy4.jar supports -Ddynalloy.solver; one built from a dynalloy4 without it ignores the
# option and solves with SAT4J, on which some benchmarks take far longer.
require_solver() {
    STRYKER_SOLVER="${STRYKER_SOLVER:-minisat}"
    case "$STRYKER_SOLVER" in
        sat4j) ;;
        minisat)
            unzip -p "$STRYKER_ROOT/lib/dynalloy4.jar" ar/uba/dc/rfm/dynalloy/analyzer/AlloyAnalyzer.class 2>/dev/null \
                | LC_ALL=C grep -aq dynalloy.solver \
                || die "lib/dynalloy4.jar cannot select MiniSat (-Ddynalloy.solver), so it would solve with
SAT4J. Build dynalloy4 and jDynAlloy from the stryker branch of mffrias/dynalloy4
(as setupTACO.sh does) and copy the jars into lib/, or set STRYKER_SOLVER=sat4j." ;;
        *) die "STRYKER_SOLVER must be minisat or sat4j, not $STRYKER_SOLVER" ;;
    esac
    export STRYKER_SOLVER
}

# Succeeds if the JVM in the JDK home $1 runs under Rosetta: an x86_64 JVM on an Apple Silicon
# Mac. (hw.optional.arm64 tells the machine apart even from a shell that itself runs under Rosetta.)
under_rosetta() {
    [ "$(uname -s)" = "Darwin" ] && [ "$(sysctl -n hw.optional.arm64 2>/dev/null)" = "1" ] \
        && [ "$(jvm_arch "$1")" = "x86_64" ]
}

# JVM options for a Stryker run on the JDK in $JAVA_HOME when STRYKER_JVM_OPTS is not set. Under
# Rosetta, tiered compilation is turned off, as it was by default on Java 7: on comitaco, runs with
# it took up to twice as long there, with the same results. A native JVM is faster with it.
default_jvm_opts() {
    if under_rosetta "$JAVA_HOME"; then
        echo "-Xmx4g -Xss8m -XX:-TieredCompilation"
    else
        echo "-Xmx4g -Xss8m"
    fi
}

# JVM options a Stryker run needs on JDK 21 and 25, whatever STRYKER_JVM_OPTS says. TACO reads
# fields of counterexample objects through reflection, those of JDK classes included (the
# serialVersionUID of an exception, for example), which Java 17 and later only allow in packages
# opened to it. --enable-native-access lets DynAlloy load MiniSat with System.load without the
# warning that JDK 24 and later print.
java21plus_opts() {
    if [ "${STRYKER_JAVA_VERSION:-}" = 21 ] || [ "${STRYKER_JAVA_VERSION:-}" = 25 ]; then
        echo "--add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.util=ALL-UNNAMED --enable-native-access=ALL-UNNAMED"
    fi
}

# Runs Ant 1.9 or later: $ANT if set, else ant on the PATH.
run_ant() {
    local ant="${ANT:-ant}"
    command -v "$ant" > /dev/null || die "Ant is required (1.9 or later, see README.md); install it or set ANT"
    "$ant" -noclasspath -f "$STRYKER_ROOT/build.xml" "$@"
}
