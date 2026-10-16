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

# Succeeds if $1 is the home of a JDK 8 that can run Stryker on this machine. On a Mac, an arm64
# JDK can only load MiniSat, the SAT solver Stryker runs with, if lib/dynalloy4.jar bundles it
# for arm64; otherwise the JDK must be an x86_64 build, which runs under Rosetta.
is_usable_jdk8() {
    local home="$1"
    [ -x "$home/bin/java" ] && [ -x "$home/bin/javac" ] || return 1
    "$home/bin/java" -version 2>&1 | grep -q 'version "1\.8[._]' || return 1
    if [ "$(uname -s)" = "Darwin" ] && [ "$(jvm_arch "$home")" = "aarch64" ] \
        && ! bundled_minisat_has_arm64; then
        return 1
    fi
}

# Prints the home of the JDK 8 to use, or fails: $STRYKER_JAVA_HOME if set (it must be usable),
# else $JAVA_HOME if it is usable, else the first usable JDK 8 found in the usual places
# (macOS: /Library/Java/JavaVirtualMachines and its ~/Library counterpart; Linux: /usr/lib/jvm,
# /opt).
find_jdk() {
    if [ -n "${STRYKER_JAVA_HOME:-}" ]; then
        is_usable_jdk8 "$STRYKER_JAVA_HOME" || return 1
        echo "$STRYKER_JAVA_HOME"
        return 0
    fi
    if [ -n "${JAVA_HOME:-}" ] && is_usable_jdk8 "$JAVA_HOME"; then
        echo "$JAVA_HOME"
        return 0
    fi
    local home
    for home in /Library/Java/JavaVirtualMachines/*/Contents/Home \
        "$HOME"/Library/Java/JavaVirtualMachines/*/Contents/Home \
        /usr/lib/jvm/* /opt/*jdk* /opt/*java*; do
        if [ -d "$home" ] && is_usable_jdk8 "$home"; then
            echo "$home"
            return 0
        fi
    done
    return 1
}

# Sets and exports JAVA_HOME to a usable JDK 8 (see find_jdk), or exits with instructions.
require_jdk() {
    if ! JAVA_HOME="$(find_jdk)"; then
        if [ -n "${STRYKER_JAVA_HOME:-}" ]; then
            die "STRYKER_JAVA_HOME=$STRYKER_JAVA_HOME is not a usable JDK 8
(on Apple Silicon, an arm64 JDK needs the MiniSat for arm64 that dynalloy4.jar
from the stryker branch of mffrias/dynalloy4 bundles; with an older
lib/dynalloy4.jar, use an x86_64 JDK 8, which runs under Rosetta)."
        fi
        die "a JDK 8 is required but none was found.
Install one (e.g. Azul Zulu 8) and, if the scripts don't find it, point
STRYKER_JAVA_HOME at it. On Apple Silicon, an arm64 JDK needs lib/dynalloy4.jar
from the stryker branch of mffrias/dynalloy4 (MiniSat for arm64); with an older
one, install the x86_64 build, which runs under Rosetta."
    fi
    export JAVA_HOME
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

# JVM options for a Stryker run when STRYKER_JVM_OPTS is not set. Tiered compilation is turned
# off, as it was by default on Java 7: on comitaco, runs with it took up to twice as long under
# Rosetta, with the same results.
default_jvm_opts() {
    echo "-Xmx4g -Xss8m -XX:-TieredCompilation"
}

# Runs Ant 1.9 or later: $ANT if set, else ant on the PATH.
run_ant() {
    local ant="${ANT:-ant}"
    command -v "$ant" > /dev/null || die "Ant is required (1.9 or later, see README.md); install it or set ANT"
    "$ant" -noclasspath -f "$STRYKER_ROOT/build.xml" "$@"
}
