#!/usr/bin/env bash
# Runs one Stryker repair benchmark (a JUnit driver class under unittest/)
# and reports whether a fix was found.
#
# Usage: scripts/run-benchmark.sh [options] <fully.qualified.TestClass>
#
# Options:
#   --reset          restore tests/ from git before running, and remove what
#                    earlier runs left there (Stryker overwrites the subject
#                    file with the fix it finds, and leaves compiled classes)
#   --variant FILE   temporarily replace the subject class with FILE, a drop-in
#                    variant such as the ones in benchmarks/ (its package and
#                    class name say which file in tests/ it replaces); the
#                    original is restored after the run
#   --no-build       skip the incremental build before running
#   --timeout SECS   stop the run after SECS seconds
#   --keep-temp      keep the per-run temp directory (mutants, batches)
#   -h, --help       show this help
#
# Environment:
#   STRYKER_JAVA_HOME  JDK 8, 21 or 25 to use (otherwise auto-detected: JDK 8,
#                      then 21, then 25; an arm64 one, on Apple Silicon or
#                      Linux, needs lib/dynalloy4.jar from the stryker branch
#                      of mffrias/dynalloy4, else use an x86_64 build)
#   STRYKER_CLASSLIB   the Java 7 rt.jar for the JML tools (default:
#                      lib/java7/rt.jar, see scripts/fetch-java7-classlib.sh)
#   STRYKER_SOLVER     minisat (default) or sat4j; minisat needs dynalloy4 from
#                      the stryker branch of mffrias/dynalloy4
#   STRYKER_JVM_OPTS   JVM options (default: -Xmx4g -Xss8m, plus
#                      -XX:-TieredCompilation for an x86_64 JDK under Rosetta).
#                      On JDK 21 and 25 the run also gets the options in
#                      java21plus_opts (scripts/common.sh).
#
# Exit status: 0 fix found, 1 no fix found, 2 usage/environment error,
#              3 build failed, 4 the run crashed, 124 timed out.
set -eo pipefail
. "$(dirname "$0")/common.sh"

usage() {
    sed -n '2,/^set -eo/p' "$0" | sed '$d' | sed 's/^# \{0,1\}//'
    exit "${1:-0}"
}

reset=false
build=true
timeout_secs=""
keep_temp=false
variant=""
test_class=""

while [ $# -gt 0 ]; do
    case "$1" in
        --reset) reset=true ;;
        --variant) shift; variant="${1:-}"; [ -n "$variant" ] || usage 2 ;;
        --no-build) build=false ;;
        --timeout) shift; timeout_secs="${1:-}"; [ -n "$timeout_secs" ] || usage 2 ;;
        --keep-temp) keep_temp=true ;;
        -h|--help) usage 0 ;;
        -*) echo "unknown option: $1" >&2; usage 2 ;;
        *) [ -z "$test_class" ] || usage 2; test_class="$1" ;;
    esac
    shift
done
[ -n "$test_class" ] || usage 2
if [ -n "$variant" ]; then
    [ -f "$variant" ] || die "variant file not found: $variant"
    variant="$(cd "$(dirname "$variant")" && pwd)/$(basename "$variant")"
fi

require_jdk
require_classlib
require_solver
cd "$STRYKER_ROOT"

test_file="unittest/$(echo "$test_class" | tr . /).java"
[ -f "$test_file" ] || die "no driver found at $test_file"

if $reset; then
    git checkout -- tests
    # Also remove what earlier runs left in tests/ (compiled classes, *_temp backups).
    git clean -fqX -- tests
elif [ -n "$(git status --porcelain -- tests 2>/dev/null)" ]; then
    echo "warning: tests/ has local changes (maybe a fix written by a previous run); use --reset to restore it" >&2
fi

variant_target=""
run_tmp=""
cleanup() {
    if [ -n "$variant_target" ]; then
        git checkout -- "$variant_target"
        echo "Restored $variant_target"
    fi
    if [ -n "$run_tmp" ]; then
        if $keep_temp; then
            echo "Temp files kept in $run_tmp"
        else
            rm -rf "$run_tmp"
        fi
    fi
}
trap cleanup EXIT

if [ -n "$variant" ]; then
    variant_package="$(sed -n 's/^[[:space:]]*package[[:space:]]*\([A-Za-z0-9_.]*\)[[:space:]]*;.*/\1/p' "$variant" | head -1)"
    variant_class="$(sed -n 's/^[[:space:]]*public[[:space:]]*class[[:space:]]*\([A-Za-z0-9_]*\).*/\1/p' "$variant" | head -1)"
    [ -n "$variant_class" ] || die "could not find a public class in $variant"
    target="tests/$(echo "$variant_package" | tr . /)/$variant_class.java"
    [ -f "$target" ] || die "$variant declares $variant_package.$variant_class, but $target does not exist"
    variant_target="$target"
    cp "$variant" "$variant_target"
    echo "Using variant $variant as $variant_target"
fi

# The subject classes in tests/ are compiled into bin/ too, so (re)build after
# --reset or --variant changed them.
if $build || $reset || [ -n "$variant" ] || [ ! -f bin/runtime.classpath ]; then
    run_ant -q build > /dev/null || { echo "build failed; run scripts/build.sh to see why" >&2; exit 3; }
fi

# Results of this run only: Stryker appends to these files.
rm -f StrykerLastReports.txt StrykerLastFixFound.java stryker.log stryker-console.log

run_tmp="$(mktemp -d "${TMPDIR:-/tmp}/stryker.XXXXXX")"

echo "Running $test_class with Java $STRYKER_JAVA_VERSION at $JAVA_HOME and $STRYKER_SOLVER"
jvm_opts="${STRYKER_JVM_OPTS:-$(default_jvm_opts)} $(java21plus_opts)"
# shellcheck disable=SC2086
"$JAVA_HOME/bin/java" -Djava.io.tmpdir="$run_tmp" -Dstryker.classlib="$STRYKER_CLASSLIB" \
    -Ddynalloy.solver="$STRYKER_SOLVER" $jvm_opts \
    -cp "$(cat bin/runtime.classpath)" org.junit.runner.JUnitCore "$test_class" > >(tee stryker-console.log) 2>&1 &
java_pid=$!
if [ -n "$timeout_secs" ]; then
    # The watchdog must not hold our stdout: a leftover `sleep` would keep a
    # pipe like `run-benchmark.sh ... | tail` open until the timeout expires.
    ( sleep "$timeout_secs" && kill "$java_pid" ) > /dev/null 2>&1 &
    watchdog_pid=$!
fi
set +e
wait "$java_pid"
junit_status=$?
set -e
if [ -n "${watchdog_pid:-}" ]; then
    pkill -P "$watchdog_pid" 2>/dev/null || true
    kill "$watchdog_pid" 2>/dev/null || true
    wait "$watchdog_pid" 2>/dev/null || true
fi

echo
echo "================================ STRYKER RESULT ================================"
if grep -q "Fix Filename" StrykerLastReports.txt 2>/dev/null; then
    echo "FIX FOUND. Repaired class: StrykerLastFixFound.java  Full report: StrykerLastReports.txt"
    grep -E "Total Time|TACO Calls|generated Mutants|Mutants pruned" StrykerLastReports.txt | head -4
    [ -n "$variant_target" ] || echo "Note: the subject file in tests/ now contains the fix; use --reset before re-running."
    result=0
elif [ -n "$timeout_secs" ] && [ "$junit_status" -eq 143 ]; then
    echo "TIMED OUT before a fix was found."
    result=124
elif grep -qs "No fix was found" stryker-console.log stryker.log; then
    echo "NO FIX FOUND (the search space was exhausted). Logs: stryker-console.log, stryker.log"
    result=1
else
    echo "The run ended without a Stryker verdict (exit status $junit_status). Check stryker-console.log and stryker.log."
    result=4
fi
if [ "$junit_status" -ne 0 ] && [ "$result" -le 1 ]; then
    echo "(The JUnit driver itself failed, usually because TACO's verdict on the original method was not the expected one.)"
fi
exit "$result"
