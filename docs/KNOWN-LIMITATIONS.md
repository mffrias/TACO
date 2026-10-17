# Known limitations

Behaviours to be aware of when running or changing TACO and Stryker on this
branch. Many of them affect the search, and so experimental results: don't
"fix" them without re-running benchmarks and saying so. Each item says whether
it was seen in a run or read from the code; observations made on comitaco (the
repository Stryker was ported from) are marked as such. Checked on 2026-10-17;
the method naming and analysis items on 2026-10-18.

## The new method naming

- **It needs the `stryker` branches.** With the masters' TACO and jDynAlloy
  code (e5760d76, e8c6b06), that is, without the fixes of this branch and of
  jDynAlloy's `stryker`, Stryker finds no fixes: the counterexample tests are
  not found, the generated tests cannot call the method, and fixed inputs are
  not applied. Calls into the built-in modules (`System.out.println`) and
  overloads where one calls the other fail on the masters too (seen; the list
  and the fixes are in [METHOD-NAMING.md](METHOD-NAMING.md#status)).
- **Two overloads with the same number of parameters cannot both be used in
  one analysis.** They get the same program name and arity, and the
  translation to Alloy fails (`Name cannot be resolved; possible incorrect
  function/predicate call`). Either one alone, or a method that calls only one
  of them, works, because the relevancy analysis leaves the other out. Seen,
  the same before and after the new naming; on comitaco it works, so it is a
  regression of jDynAlloy 5b33641 (2022), which dropped the overload index
  from program names ([FIXES-FOR-MASTER.md](FIXES-FOR-MASTER.md#7-found-not-fixed)).
- **Drivers in the earlier forms** (`"contains_0"`, `"absoluteInt(int)"`) are
  not found (`The method to check was not found`); most of Marcelo's own
  drivers still use them (seen).

## Analysis

- **On jDynAlloy master, an invariant broken through a callee is not
  detected**: its modifies table has only the fields each method assigns
  itself. jDynAlloy's `stryker` branch counts the callees' fields again, as
  comitaco's jDynAlloy does (0f920f1;
  [FIXES-FOR-MASTER.md](FIXES-FOR-MASTER.md#m1-an-invariant-broken-through-a-callee-is-not-detected)).
- **`assignable` clauses add no frame conditions.** jDynAlloy's
  `ReplaceModifiesModuleVisitor` would add `x.g = x.g'` for the fields a
  method may not assign, but its test for the method under analysis cannot
  match the new names (read from the code). Nothing came of it before the new
  naming either: with a class of two fields and a method `assignable a`, the
  postcondition has nothing about the other field, before and after, while
  comitaco's has `thiz.b = thiz.b'`. So it is a regression, since TACO
  3ae43be3 (2022) named the method `<method>(<types>)`. JML's own checker
  rejects, by field name, assignments and calls that the clause does not
  allow (`Field "b" is not assignable by method ...`), so this was not seen
  to matter (seen).
- **`f(null)` is ambiguous with `useJavaArithmetic`** when `f` has an `int`
  and an object overload: jDynAlloy's binding stops with `Ambiguous call`,
  because `int` values are objects (`JavaPrimitiveIntegerValue`) there (seen).

## Environment

- **Java 8, 21 or 25 for Stryker.** `TacoMain.run` checks the version only for
  Stryker runs (`Java7ClassLibrary.checkForStryker`); plain TACO is not checked,
  but needs Java 8 APIs (`SnapshotBuilder` uses
  `Constructor.getParameterCount`), so it does not run on Java 7 either (read
  from the code). dynalloy4 and jDynAlloy build only on JDK 8 (seen).
- **Results on Java 21 and 25 are close to Java 8's, but not identical.** Here
  they were compared only on the four smoke-test benchmarks (the same reports,
  one run each, seen). On comitaco, with one run each of 40 benchmarks from
  every family, 38 gave the same report on JDK 21 as on JDK 8, and 36 on JDK 25,
  and results varied more from run to run on the newer JDKs, because the order
  of TACO's Alloy declarations depends on identity hash codes (comitaco's
  [docs/BUILDING.md, "JDK 21 or 25"](https://github.com/zeminlu/comitaco/blob/master/docs/BUILDING.md#jdk-21-or-25-optional)).
  Those measurements apply to the same Stryker code as this branch's.
- **The JDK's build changes counterexamples.** Oracle 8u261 and Zulu 8u504 give
  a different first counterexample for the BinomialHeap smoke benchmark (same
  fix and counts, seen). Compare runs on the same JDK.
- **An x86_64 JVM under Rosetta is slower with tiered compilation**, so the
  scripts turn it off there (measured on comitaco).
- **x86_64 or arm64, macOS or Linux.** MiniSat 2.2 comes from dynalloy4's
  `alloy4.jar`; arm64 needs dynalloy4's `stryker` branch. No native Windows:
  no Windows MiniSat 2.2, the scripts are bash, and Stryker uses `/` and the
  file separator as a regular expression (`OpenJMLController`, `MuJavaController`,
  read from the code).
- **The working directory must be the repository root**: `config/`, `tests/`,
  `bin/`, `lib/java7/rt.jar` and `lib/stryker/jml4c.jar` are resolved against it.
- **`ForArielGodioBubbleSortBug02Test` fails on Linux** (amd64 and arm64:
  `Unable to find a class for forArielGodio/bubbleSort/bug02/BubbleSort`) and
  passes on macOS (seen before the new naming; not investigated).

## Setting up and building

- The builds of dynalloy4 and jDynAlloy change their tracked `build.num`, and
  `jDynAlloy/lib/dynalloy4.jar` is left untracked there (seen).
- **`ant unittest` runs no driver**: its class path lacks `bin/`, so every driver
  fails with `ClassNotFoundException` (seen). Even if it did, it would run every
  driver, hundreds of Stryker benchmarks included. The target is Marcelo's; see
  [OPEN-QUESTIONS.md](OPEN-QUESTIONS.md#other).
- The build does not compile against the Java 7 class library, so it does not
  catch uses of Java 8 APIs (read from `build.xml`).
- IntelliJ: the library `mjc-1_3_2` points at a `lib/mjc-1_3_2.jar` that does
  not exist (read from `.idea/libraries/`).

## Running

- **A successful run overwrites the subject file under `tests/`** with the fix
  and strips most `//mutGenLimit` marks. Restore with `--reset` or
  `git checkout -- tests`.
- **The JUnit result of a driver says nothing about the repair**: it only
  asserts TACO's verdict on the original method. Use the `STRYKER RESULT` block,
  `StrykerLastReports.txt`, or the console line `No fix was found`.
- **One repair per JVM, one run at a time per checkout**: the controllers are
  singletons, run state is in static fields of `StrykerStage`, and runs share
  `tests/`, `output/`, `generated/` and the report files.
- **After `checkAndRunSpecIfFaulty`, every TACO run in that JVM is a spec-output
  run** (`TacoMain.isCheckAndRunSpecAssertionSupport` is never reset; see
  [SPEC-OUTPUT.md](SPEC-OUTPUT.md)).
- **An unexpected exception can make a run never end.** The loops of
  `DarwinistController`, `MuJavaController` (except around feedback) and
  `UnskippableMuJavaController` catch only `InterruptedException`; any other
  exception ends that controller's thread without acknowledging its work item,
  and the run waits forever (`OpenJMLController` catches `Exception` and goes
  on). Use `run-benchmark.sh --timeout`. A counterexample that cannot be turned
  into a test is handled: that candidate just gets no feedback (read from the
  code).
- **After a fix is found, a controller thread can keep spinning**
  (`WeedQueue.clearQueues` never finishes while a work item is in flight);
  harmless when the JVM exits at the end, as with the scripts. On fast runs the
  shutdown can also print an `InterruptedException` and a
  `NullPointerException` in `MuJavaController.fatherize`, after the fix was
  reported (seen on a native arm64 JVM).
- **Temp files are never deleted by Stryker**: thousands of mutant files in
  `java.io.tmpdir` per run. `run-benchmark.sh` gives each run its own temp
  directory and deletes it.
- **A counterexample in which two parameters are the same object can be lost**:
  `UnitTestBuilder` declares each parameter with its declared type and reuses
  an already declared object without a cast, so such a test may not compile
  (read from the code; seen on comitaco).
- **A `PrintStream` in a counterexample** (TACO models `System.out`) is
  `System.out` in the tests (`SnapshotBuilder.createNewInstance`,
  `UnitTestBuilder`), but `SnapshotBuilder.instantiate`, which builds object
  values from their first constructor with default arguments, fails for it
  (`Null output stream`) and leaves the value null (read from the code; seen
  once, in a run with the new naming). On comitaco `instantiate` goes through
  `createNewInstance`.
- **The rewritten counterexample test uses some fixed names** for its
  parameters and locals (`fileClasspath`, `className`, `methodName`, `clazz`,
  `instance`, `method`, `e`, `requiredData`); a method under repair with a
  parameter of one of those names would not compile there. Its other locals
  start with `stryker` (read from the code; no subject has such a parameter).
- **`maxStrykerMethodForFile` has no effect**: `MuJavaController` stores it but
  never uses it; the batch size is the constant 100 (read from the code).
- **`TacoMain`'s command line** fails (it appends `_0` to the method name; seen).
- **`unittest/examples/gcd`**: on comitaco its batch files do not compile
  (`The nested type Gcd cannot hide an enclosing type`) and the runs make no
  progress; the code is the same here, and the one gcd driver run here ended
  without a Stryker verdict, before and after the new naming (seen).
- **`FloatTest`** (`unittest/ar/edu/taco/regresion/stryker/FloatTest.java`) still
  uses `_0` names, and master's floating-point commit edf6892a is not in this
  branch (read from the code; on comitaco it fails).

## Search behaviour

- **Runtime assertion checking tries at most 9 counterexamples per mutant**,
  round-robin from a static cursor shared by all mutants (`OpenJMLController`).
  Only the final check before accepting a fix (`lastRACAnalysis`) runs all of
  them (read from the code).
- **RAC timeouts count as passes**: 3 s per input in `OpenJMLController`, 5 s in
  `lastRACAnalysis`. The run is cancelled through `RacTimeout.check()` calls at
  the start of every method and loop body of the JML4C batch; a run stuck where
  there are none (a helper class, the JDK) is stopped with `Thread.stop()` after
  2 more seconds on Java 19 and earlier, and abandoned on later ones (read from
  the code; the smoke benchmarks gave the same results either way).
- **The counterexample pool holds 10000** (`StrykerStage.MAX_COUNTEREXAMPLES`).
  If it fills up (a warning is logged), or a counterexample's test class fails
  to load, that counterexample is not stored and its candidate gets no
  feedback; RAC and TACO still decide what counts as a fix (read from the code).
- **Results can vary between runs, and pruning can remove a real fix.** Which
  counterexamples RAC tries depends on timing (the shared cursor, while other
  threads add counterexamples), and the counterexamples themselves can change
  with the order of TACO's declarations. On comitaco, single-bug benchmarks
  found a fix in some runs and not in others, with the feedback computed from
  some inputs pruning the fix; the cause is not known. Treat a "no fix" as "not
  found in this run". The smoke-test benchmarks have given the same results in
  every run.
- **A TACO error during variabilization stops the feedback** at that step, like
  a SAT verdict (read from the code).
- **If no mutable statement can be variabilized at all**, the feedback is
  marked UNSAT without calling TACO; its skip index then points at the
  bottom-most line, so no statement is skipped, but the remaining right-hand-side
  mutations of that line can be (read from the code).
- **Feedback after a failed final RAC check uses the first counterexample**:
  candidates from `OpenJMLController` don't carry the failing input, and
  `lastRACAnalysis` records it only when the check ends in an exception. This
  only reduces pruning (read from the code).
- **Variabilization verdicts are cached by file content**, not by the input
  (`VariablizedSATVerdicts`): a cached SAT may hide pruning that another input
  would allow (read from the code).
- **The right- and left-hand sides of a statement are variabilized separately**
  (`VariablizationData`; read from the code).

## Code

- **Naming**: `OpenJMLController` does RAC with JML4C, not OpenJML;
  `DarwinistController` is TACO validation plus feedback; "variablize" and
  "computate" are the code's spellings.
- **Unused code**: `ClassLoaderTools`; `StrykerASTVisitor` and
  `StrykerDuplicateVariablesVanisherASTVisitor` (used only by
  `StrykerJavaFileInstrumenter` methods nobody calls); the
  `else if (unvariablizable)` branch of `DarwinistController.computateFeedback`.
- **Concurrency**: `StrykerStage`'s static state is shared by several threads
  without synchronization; many exceptions are caught and ignored.
- **Stale comments**: `TacoMain` says the pool has "room for 50 tests", and
  `VariablizationData` says the JDT parser reads `<java.home>/lib/rt.jar` (it
  reads the Java 7 class library).
- **Fixed on comitaco and present here**: the queue-termination race in
  `WeedQueue`, a `.class` file left in `tests/` breaking the next run, the
  IntroClass `c` parameter clash, the 50-counterexample pool, TACO errors in
  variabilization counted as UNSAT, two feedback-traversal bugs, and three path
  problems (a temp directory with a folder starting with `a`, a repository path
  with a space, `generated` or `output` in the repository path).
