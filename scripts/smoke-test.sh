#!/usr/bin/env bash
# Runs a few reference benchmarks and checks that Stryker still produces
# exactly the expected results: whether a fix is found, which fix, the number
# of TACO calls and of pruned mutants. Not every benchmark gives the same
# results in every run, but these ones have so far, so a difference most likely
# means the search now behaves differently. Run it after changing TACO or
# Stryker.
#
# Usage: scripts/smoke-test.sh [--quick]
#   --quick   only the two singly-linked-list benchmarks (about 20 s instead
#             of about a minute)
#
# The expected values are those of comitaco (tag converged-2026-10), with
# MiniSat 2.2, except that Contains2Bug4Dx10D makes one TACO call fewer: this
# TACO returns a different first counterexample, and one candidate that
# comitaco has to check with TACO already fails RAC here. The runs always use
# MiniSat 2.2 (with SAT4J the results differ). tests/ must have no local changes
# (Stryker writes fixes into it); it is restored at the end. If you change the
# search behaviour on purpose, update EXPECTED below and say so in the commit
# message.
set -eo pipefail
. "$(dirname "$0")/common.sh"

usage() {
    sed -n '2,/^set -eo/p' "$0" | sed '$d' | sed 's/^# \{0,1\}//'
    exit "${1:-0}"
}

quick=false
case "${1:-}" in
    "") ;;
    --quick) quick=true ;;
    -h|--help) usage 0 ;;
    *) echo "unknown option: $1" >&2; usage 2 ;;
esac

# driver | expected exit status of run-benchmark.sh (0 fix, 1 no fix) | report lines that must appear (;-separated)
EXPECTED=(
    "icse.singlylinkedlist.set1.StrykerSinglyLinkedListContains1Bug25DTest|0|LineMutationIndexes: [ 2 ];TACO Calls: 3"
    "icse.singlylinkedlist.set1.StrykerSinglyLinkedListContains2Bug4Dx10DTest|0|LineMutationIndexes: [ 8 1 ];TACO Calls: 13;Amount of Mutants pruned: 11"
)
if ! $quick; then
    EXPECTED+=(
        "icse.binomialheap.set1.StrykerBinomialHeapFindMinimum1Bug11DTest|0|LineMutationIndexes: [ 13 ];TACO Calls: 13"
        "ase2016.introclass.median.StrykerIntroClassMedian_0cdfa335_003Test|0|LineMutationIndexes: [ 1 ];TACO Calls: 1"
    )
fi

STRYKER_SOLVER=minisat
require_jdk
require_classlib
require_solver
cd "$STRYKER_ROOT"
if [ -n "$(git status --porcelain -- tests)" ]; then
    die "tests/ has local changes. Commit or stash them first: the benchmarks overwrite subject files there."
fi
trap 'git checkout -- tests' EXIT

log=smoke-test.log
: > "$log"
failures=0
for entry in "${EXPECTED[@]}"; do
    driver="${entry%%|*}"
    rest="${entry#*|}"
    expected_status="${rest%%|*}"
    expected_lines="${rest#*|}"

    start=$(date +%s)
    set +e
    scripts/run-benchmark.sh --reset --timeout 600 "$driver" >> "$log" 2>&1
    status=$?
    set -e
    elapsed=$(( $(date +%s) - start ))

    problem=""
    if [ "$status" -ne "$expected_status" ]; then
        problem="exit status $status, expected $expected_status"
    else
        IFS=';' read -r -a lines <<< "$expected_lines"
        for line in ${lines[@]+"${lines[@]}"}; do
            # A report line must end with the expected text ("TACO Calls: 3" must not match "...: 30").
            if ! awk -v s="$line" '{ sub(/[ \t\r]+$/, "") }
                    length($0) >= length(s) && substr($0, length($0) - length(s) + 1) == s { found = 1 }
                    END { exit !found }' StrykerLastReports.txt 2>/dev/null; then
                problem="report lacks \"$line\""
                break
            fi
        done
    fi

    if [ -z "$problem" ]; then
        printf 'PASS  %-72s %4ss\n' "$driver" "$elapsed"
    else
        printf 'FAIL  %-72s %4ss  %s\n' "$driver" "$elapsed" "$problem"
        failures=$((failures + 1))
    fi
done

if [ "$failures" -gt 0 ]; then
    echo "$failures benchmark(s) changed behaviour. Output of every run: $log"
    exit 1
fi
echo "All benchmarks behave as expected."
