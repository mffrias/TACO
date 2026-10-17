# Running TACO and Stryker

Set up first: [BUILDING.md](BUILDING.md). The working directory is always the
repository root: TACO and Stryker resolve `config/`, `tests/`, `bin/` and `lib/`
against it.

## Concepts

- **Subject**: a Java class with JML specifications under `tests/`, e.g.
  `tests/icse/singlylinkedlist/set1/SinglyLinkedListContains1Bug25D.java`.
- **Driver**: a JUnit 3 test class under `unittest/` (called a driver here, to
  tell it apart from the programs under `tests/`), a subclass of
  `ar.edu.taco.regresion.CollectionTestBase` or `GenericTestBase` that names the
  class (`getClassToCheck()`), sets configuration keys and calls `check`. A
  driver is how TACO is run; there is no usable command line.
- **`check(GENERIC_PROPERTIES, "<method>", expected)`** runs TACO on the method
  with `config/genericTest.properties` and the driver's keys, and asserts
  TACO's verdict: `true` if a counterexample is expected (SAT), `false` if not
  (UNSAT within the bounds). The method is named as in
  [METHOD-NAMING.md](METHOD-NAMING.md). Other entry points: `runAndCheck`,
  `simulate`, `notInstance` and `checkAndRunSpecIfFaulty`
  ([SPEC-OUTPUT.md](SPEC-OUTPUT.md)), all in `RegresionTestBase`.
- **A Stryker run**: a driver with `setConfigKeyAttemptToCorrectBug(true)`.
  If TACO finds a counterexample, Stryker searches for a fix
  ([ARCHITECTURE.md](ARCHITECTURE.md)).
- **Mutable lines**: Stryker only mutates statements marked
  `//mutGenLimit N` with N > 0 in the subject; those marks are the fault
  localization.

## Running TACO's own drivers

Build, then run a driver with JUnit:

    scripts/build.sh
    java -Xmx4g -Xss8m -cp "$(cat bin/runtime.classpath)" org.junit.runner.JUnitCore ar.edu.itba.forArielGodio.binarySearch.ForArielGodioBinarySeachBug01Test

with `java` from a JDK 8 (on JDK 21 and 25 add the
[options in BUILDING.md](BUILDING.md#jdk-21-or-25)). JUnit's `OK` means TACO's
verdicts were the expected ones. TACO prints the Alloy Analyzer's progress
(solver, variables, clauses, outcome). It writes its intermediate files under
`output/` (`*.djals`, `output.dals`, `output.als`) and, with
`generateUnitTestCase`, a JUnit test per counterexample under
`generated/ar/edu/generated/junit/`.

`ForArielGodioBinarySeachBug01Test` passes (SAT, about 10 s).

## Running one Stryker benchmark

    scripts/run-benchmark.sh [options] <fully.qualified.DriverClass>

| Option | Meaning |
|---|---|
| `--reset` | Restore `tests/` from git before running, and remove what earlier runs left there. Use it whenever you re-run a benchmark: a successful run leaves the fix in the subject file. |
| `--variant FILE` | Temporarily replace a subject class with `FILE` (see [BinomialHeap ROOPS variants](#binomialheap-roops-variants)). |
| `--timeout SECS` | Stop the run after `SECS` seconds. |
| `--no-build` | Skip the incremental build. |
| `--keep-temp` | Keep the run's temp directory (mutants, batches). |

Environment variables: `STRYKER_JAVA_HOME` (the JDK 8, 21 or 25 to use),
`STRYKER_CLASSLIB` (the Java 7 `rt.jar`, default `lib/java7/rt.jar`),
`STRYKER_SOLVER` (`minisat`, the default, or `sat4j`) and `STRYKER_JVM_OPTS`
(default `-Xmx4g -Xss8m`, plus `-XX:-TieredCompilation` for an x86_64 JDK under
Rosetta; on JDK 21 and 25 the run also gets the options of
[BUILDING.md](BUILDING.md#jdk-21-or-25)).

Exit status: `0` fix found, `1` no fix found (search space exhausted), `2`
usage or environment error, `3` build failed, `4` the run ended without a
Stryker verdict (a crash, or TACO found no bug), `124` timed out.

Every script also runs in the [Docker environment](BUILDING.md#docker):
prefix it with `scripts/docker.sh`.

Good first runs (Apple Silicon, Zulu 8u504, MiniSat 2.2; times of 2026-10-18):

| Driver | Expected |
|---|---|
| `icse.singlylinkedlist.set1.StrykerSinglyLinkedListContains1Bug25DTest` | fix `[ 2 ]` in about 10 s, 3 TACO calls |
| `icse.singlylinkedlist.set1.StrykerSinglyLinkedListContains2Bug4Dx10DTest` | fix `[ 8 1 ]` in about 10 s, 13 TACO calls, 11 mutants pruned |
| `icse.binomialheap.set1.StrykerBinomialHeapFindMinimum1Bug11DTest` | fix `[ 13 ]` in about 30 s, 13 TACO calls |
| `ase2016.introclass.median.StrykerIntroClassMedian_0cdfa335_003Test` | fix `[ 1 ]` in about 5 s, 1 TACO call |

These are the smoke test's benchmarks. Run one benchmark per JVM: the
controllers are singletons (the script does this for you).

### Several benchmarks

Each ICSE data-structure suite has `Commands-<N>Bug.txt` files listing its
drivers by number of injected bugs (and `set*/SetCommands-<N>Bug.txt` per set):

```bash
while read -r driver; do
  scripts/run-benchmark.sh --reset --timeout 3600 "$driver" > "results-$driver.log" 2>&1
  echo "$driver exit=$?"
done < unittest/icse/singlylinkedlist/Commands-1Bug.txt
```

Runs from the same checkout must not overlap: they share `tests/`, `output/`,
`generated/` and the report files. Use separate clones to run in parallel.
Results can vary from run to run (see
[KNOWN-LIMITATIONS.md](KNOWN-LIMITATIONS.md)), so repeat a run before reading
much into a "no fix".

## Reading the results

`run-benchmark.sh` ends with a `STRYKER RESULT` block. The JUnit `OK` line
does not say whether a fix was found: the driver only asserts TACO's verdict on
the original method.

| File (repository root) | Contents |
|---|---|
| `StrykerLastFixFound.java` | The repaired class |
| `StrykerLastReports.txt` | For each fix found: the mutant file, the mutation indexes of the solution (`LineMutationIndexes`), and statistics (time, TACO calls and time, generated, duplicate, non-compilable and pruned mutants, candidates per stage) |
| `stryker-console.log` | Everything the run printed, including `No fix was found` |
| `stryker.log` | Log of the MuJava, OpenJML (RAC) and Darwinist controllers |
| `tests/...` | The subject, overwritten with the fix. Restore with `--reset` or `git checkout -- tests` |
| `generated/`, `output/` | TACO's counterexample tests and intermediate files of the last analysis |

The script clears the report files before each run, and gives each run its
own temp directory (thousands of mutant files), deleted afterwards unless
`--keep-temp`.

## Benchmark suites

880 drivers run Stryker (`setConfigKeyAttemptToCorrectBug(true)`; one more,
`ar.edu.taco.skunk.TreeSetTest`, has it commented out):

| Drivers in `unittest/` | Count | Contents |
|---|---|---|
| `icse/{singlylinkedlist,nodecachinglinkedlist,bintree,binomialheap}` | 365 | data structures with 1 to 6 injected bugs, in five sets (`set1`-`set5`); file names encode the bug count and lines, e.g. `Contains2Bug4Dx10D` |
| `ase2016/introclass/{grade,median,smallest}` | 197 | IntroClass student programs, with JML contracts |
| `ase2016/{singlylinkedlist,nodecachinglinkedlist,bintree,binomialheap}` | 41 | data-structure cases |
| `pldi` | 97 | data structures with long chains of injected bugs |
| `examples/gcd` | 126 | gcd variants |
| `roops/core/objects` | 9 | ROOPS classes, used with the variants below |
| `ar/edu/...` (mostly `ar/edu/taco/regresion/stryker`, 39) | 42 | small examples and regression cases |
| `girish`, `krueger`, `wg1_9` | 3 | single examples |

The rest of `unittest/` is TACO's own regression suite.

## BinomialHeap ROOPS variants

`benchmarks/binomialheap-roops-variants/set1..set5/` holds ICSE BinomialHeap
subjects rewritten as drop-in replacements for
`roops.core.objects.BinomialHeap`, so they can be analysed with the ROOPS
drivers and their tight bounds (`xml/`). Run them with `--variant`, choosing
the driver by method:

| Variant file name starts with | Driver |
|---|---|
| `BinomialHeapExtractMin` | `roops.core.objects.BinomialHeapTestExtractMin` |
| `BinomialHeapFindMinimum` | `roops.core.objects.BinomialHeapTestFindMin` |
| `BinomialHeapInsert` | `roops.core.objects.BinomialHeapTestInsert` |

    scripts/run-benchmark.sh --variant benchmarks/binomialheap-roops-variants/set1/BinomialHeapFindMinimum1Bug11D.java roops.core.objects.BinomialHeapTestFindMin

The script copies the variant over `tests/roops/core/objects/BinomialHeap.java`,
rebuilds, runs, and restores the original.

## Writing your own benchmark

1. **Subject.** Put the class under `tests/<package>/`, with a JML contract for
   the method (`requires`/`ensures`, and class invariants if relevant). Mark
   the statements Stryker may change with `//mutGenLimit 1`; mark others
   `//mutGenLimit 0` or leave them unmarked.
2. **Driver.** Copy a driver of a similar subject (e.g. under
   `unittest/icse/singlylinkedlist/set1/`) and adapt it:

   ```java
   package mypackage;

   import ar.edu.taco.regresion.CollectionTestBase;
   import ar.uba.dc.rfm.dynalloy.visualization.VizException;

   public class StrykerMyListContainsTest extends CollectionTestBase {
       @Override
       protected String getClassToCheck() {
           return "mypackage.MyList";
       }

       public void test_containsTest() throws VizException {
           setConfigKeyRelevantClasses("mypackage.MyList,mypackage.MyListNode");
           setConfigKeyTypeScopes("mypackage.MyList:1,mypackage.MyListNode:5");
           setConfigKeyLoopUnroll(5);
           setConfigKeyGenerateUnitTestCase(true);
           setConfigKeyAttemptToCorrectBug(true);
           // ...the other keys of the driver you copied
           check(GENERIC_PROPERTIES, "mypackage_MyList_contains(java_lang_Object)", true);
       }
   }
   ```

   `true`: TACO must find a counterexample in the original method.
3. **Run** it: `scripts/run-benchmark.sh mypackage.StrykerMyListContainsTest`.

### Configuration keys

Drivers set keys with the `setConfigKey...` methods of
`unittest/ar/edu/taco/regresion/RegresionTestBase.java`, on top of
`config/genericTest.properties`. The ones Stryker drivers use most:

| Driver method | Key | Meaning |
|---|---|---|
| `setConfigKeyAttemptToCorrectBug` | `attemptToCorrectBug` | Run Stryker when TACO finds a bug |
| `setConfigKeyGenerateUnitTestCase` | `generateUnitTestCase` | Turn counterexamples into JUnit tests |
| `setConfigKeyRelevantClasses` | `relevantClasses` | Classes included in the analysis |
| `setConfigKeyRelevancyAnalysis` | `relevancyAnalisys` (sic) | Compute the relevant classes |
| `setConfigKeyTypeScopes` | `type_scopes` | Objects per class, `Class:N,...` |
| `setConfigKeyObjectScope` | `objectScope` | Default object scope (3) |
| `setConfigKeyInferScope` | `inferScope` | Infer scopes |
| `setConfigKeyLoopUnroll` | `dynalloy.toAlloy.loopUnroll` | Loop and recursion unrolling bound (3) |
| `setConfigKeyIntBithwidth` | `int.bitwidth` | Integer bit width (4) |
| `setConfigKeyUseJavaArithmetic` | `useJavaArithmetic` | Model Java integer arithmetic, overflow included |
| `setConfigKeyCheckNullDereference` | `checkNullDereference` | Null dereferences are errors |
| `setConfigKeyCheckArithmeticException` | `checkArithmeticException` | Arithmetic exceptions are errors |
| `setConfigKeySkolemizeInstanceInvariant`, `...InstanceAbstraction` | same names | Encoding options |
| `setConfigKeyRemoveQuantifiers` | `dynalloy.toAlloy.removeQuantifiers` | Encoding option; symmetry breaking and the Java-arithmetic skolemization only act with it (they work on the fields of its signature `QF`) |
| `setConfigKeyUseJavaSBP` | `useJavaSBP` | Symmetry-breaking predicates (only with `dynalloy.toAlloy.removeQuantifiers`) |
| `setConfigKeyUseTightUpperBounds` | `useTightUpperBounds` | Precomputed bounds from `xml/` (ROOPS classes only; needs `useJavaSBP`) |
| `setConfigKeyMaxStrykerMethodsPerFile` | `maxStrykerMethodForFile` | No effect |

## Checking a change

There is no CI. After changing TACO or Stryker, run the smoke test:

    scripts/smoke-test.sh           # about a minute
    scripts/smoke-test.sh --quick   # about 20 s, the two singly-linked-list benchmarks

It runs the four benchmarks of [the table above](#running-one-stryker-benchmark)
with MiniSat 2.2 and checks the exact outcome: fix found or not, which fix
(`LineMutationIndexes`), the number of TACO calls and of pruned mutants. They
have given the same numbers in every run so far, on macOS and Linux and on JDK
8, 21 and 25, so a `FAIL` most likely means the search behaves differently.
If that is intended, update `EXPECTED` in `scripts/smoke-test.sh` and say so in
the commit message. Every run's output goes to `smoke-test.log`. It refuses to
run if `tests/` has local changes, and restores `tests/` at the end.

The expected values are comitaco's, except `Contains2Bug4Dx10D`: 13 TACO calls
here against comitaco's 14 (a different first counterexample).

### Optional pre-push hook

    git config core.hooksPath .githooks

From then on `git push` runs `scripts/smoke-test.sh --quick` when the pushed
commits touch code (`src/`, `lib/`, `tests/`, `unittest/`, `scripts/`,
`build.xml`), and aborts the push if a benchmark changed. Documentation-only
pushes are not checked. It lets the push through, with a warning, when no
usable JDK, Java 7 class library or MiniSat-capable dynalloy4 is set up, or
`tests/` has local changes. Skip it once with `git push --no-verify`.

## Running without the scripts

What `run-benchmark.sh` does, by hand, from the repository root:

    scripts/build.sh
    mkdir -p /tmp/stryker-run
    java -Djava.io.tmpdir=/tmp/stryker-run -Dstryker.classlib=lib/java7/rt.jar -Ddynalloy.solver=minisat -Xmx4g -Xss8m -cp "$(cat bin/runtime.classpath)" org.junit.runner.JUnitCore icse.singlylinkedlist.set1.StrykerSinglyLinkedListContains1Bug25DTest

with `java` from a JDK 8. Add `-XX:-TieredCompilation` for an x86_64 JDK
under Rosetta, and the [JDK 21 and 25 options](BUILDING.md#jdk-21-or-25) on
those. Then restore the subject: `git checkout -- tests`.

`ar.edu.taco.TacoMain` has a command line (`-cf <config> -c <class> -m
<method>`), but it appends `_0` to the method name, which the current naming
does not accept; use drivers.
