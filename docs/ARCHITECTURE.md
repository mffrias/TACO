# Architecture

How TACO checks a method, and how Stryker repairs one. Class names refer to
`src/ar/edu/taco/...` unless stated otherwise; Stryker's classes (under
`stryker/` and `engine/StrykerStage.java`) have Javadoc with more detail.

## 1. The TACO pipeline

A driver (a JUnit class under `unittest/`, see [RUNNING.md](RUNNING.md)) calls
`TacoMain.run(configFile, overridingProperties)`. The configuration is
`config/genericTest.properties` with the driver's keys on top
(`TacoConfigurator`; the key `DynJAlloyConfig.Class` tells jDynAlloy to use it).
Then, in order:

| Stage | Input → output |
|---|---|
| JML parsing (`jml.JmlParser`, JML2/MultiJava in `lib/jml-release.jar`) | the classes in `classes` or `relevantClasses`, plus `classToCheck`, read from `jmlParser.sourcePathStr` (`tests/`). If a class does not compile, `run` returns null. |
| `engine.JmlStage` | simplifies the Java + JML (writes `output/<pkg>/<Class>.java`) |
| `engine.SimpleJmlStage` | simplified Java + JML → JDynAlloy modules (`simplejml.JavaToJDynAlloyManager`) |
| `engine.JfslStage` | JFSL annotations, on the same modules |
| `engine.JDynAlloyPrinterStage` | writes the modules as `output/**/*.djals` |
| `engine.PrecompiledModules` | adds the built-in modules (`src/ar/edu/taco/engine/precompiledmodules/*.djals`: `java.lang.Object`, arrays, integers, ...) |
| `engine.JDynAlloyParsingStage` | parses extra JDynAlloy files, if configured |
| `engine.JDynAlloyStage` (jDynAlloy) | bindings, relevancy analysis and slicing (`relevancyAnalisys`), modifies clauses, then JDynAlloy → DynAlloy (`JDynAlloyTranslator`, which also unfolds recursion); writes `output/output.dals` |
| `engine.DynalloyStage` (dynalloy4) | DynAlloy → Alloy, with TACO's plugins (scope inference, the `check` command and scopes, symmetry breaking, tight bounds from `xml/`, Java arithmetic): `output/output.als`, checking `check_<method>` |
| `engine.AlloyStage` (dynalloy4, Alloy, Kodkod) | solves the first command with MiniSat 2.2 or SAT4J |
| `snapshot.SnapshotStage`, `engine.JUnitStage` | if SAT and `generateUnitTestCase` (or `attemptToCorrectBug`): the counterexample as objects, then as a JUnit test under `generated/ar/edu/generated/junit/` |
| `engine.JavaTraceStage` | if `buildJavaTrace`: the counterexample's trace in Java terms |
| `engine.StrykerStage` | if `attemptToCorrectBug` and SAT: Stryker (section 2) |

`run` returns a `TacoAnalysisResult`: `isSAT()` means a counterexample was
found, within the bounds (`objectScope`, `type_scopes`, `int.bitwidth`,
`dynalloy.toAlloy.loopUnroll`). `TacoMain.run` checks the Java version and the
Java 7 class library only for Stryker runs (`Java7ClassLibrary.checkForStryker`).

For a counterexample, "SAT" means "the method is wrong for some input within
the bounds"; "UNSAT" means "correct within the bounds". Stryker uses the words
the same way.

What `inputToFix` and `checkAndRunSpecIfFaulty` change in this pipeline is in
[SPEC-OUTPUT.md](SPEC-OUTPUT.md); how the method is named, in
[METHOD-NAMING.md](METHOD-NAMING.md).

## 2. Where Stryker hooks in

After a SAT check with `attemptToCorrectBug=true`, `TacoMain` takes the JUnit
test that `JUnitStage` wrote, rewrites it so that Stryker can run it against
any class and method (`TacoMain.editTestFileToCompile`: the class is loaded
from a given class path, exceptions are rethrown), writes it under
`output/ar/edu/output/junit/`, compiles it, loads it, and stores it as the first
entry of the counterexample pool (`StrykerStage.junitInputs` and `junitFiles`,
room for 10000, `StrykerStage.MAX_COUNTEREXAMPLES`). Then
`StrykerStage.execute()` runs the repair and blocks until it ends.

`StrykerStage` fixes the mutation operators (PRVOL_SMART, PRVOR_REFINED,
PRVOU_REFINED, AODS, AODU, AOIS, AOIU, AORB, AORS, AORU, ASRS, COD, COI, COR,
LOD, LOR, ROR, SOR) and hands over to `stryker.StrykerInitialStage`, which uses
`stryker.api.impl.SimpleStrykerAPI`: `start()` starts four singleton
controllers, each with one thread, and `fixBug(...)` enqueues the first task
and polls every 500 ms until the run ends.

```
                 TacoMain.run ── TACO check SAT + attemptToCorrectBug
                      │
                 StrykerStage ─ StrykerInitialStage ─ SimpleStrykerAPI
                      │ initial task
                      ▼
           UnskippableMuJavaController ── left-hand-side mutations that
              │                 │          feedback can never prune
    new bases │ (q1)            │ validate-only candidates (q2)
              ▼                 ▼
 ┌─────────────────────┐ mutants (q2) ┌──────────────────────┐ passes (q3) ┌──────────────────────┐
 │ MuJavaController    │ ───────────▶ │ OpenJMLController    │ ──────────▶ │ DarwinistController  │
 │ generate & search,  │              │ RAC on counter-      │             │ TACO on candidate,   │
 │ compute feedback    │ ◀─────────── │ examples (JML4C)     │             │ final RAC, fix found │
 └─────────────────────┘  RAC failed: └──────────────────────┘             └──────────┬───────────┘
            ▲             feedback request (q1)                                       │
            └──────────── TACO SAT: new counterexample + feedback request (q1) ───────┘
```

| Component (`stryker/api/impl/`) | Job |
|---|---|
| `UnskippableMuJavaController` | First stage: the single-line PRVOL_SMART (left-hand side) mutations that are not a change of the left-hand side's tail, which feedback can never prune. Once its own queue has been idle for 2 s it sends the original program and every combination to `MuJavaController` as new bases, and every combination to `OpenJMLController` as a validate-only candidate. |
| `MuJavaController` | The search: generates mutants with muJava (`lib/mujava2018.jar`), walks the mutation space (section 4), batches mutants into files that JML4C compiles, and, on its own thread, has feedback computed when asked to (`DarwinistController.computateFeedback`). |
| `OpenJMLController` | Runtime assertion checking with **JML4C** (the name is historical; OpenJML is not used): runs each candidate on up to 9 stored counterexamples, taken round-robin from a cursor shared by all candidates, and stops at the first failure. A 3-second timeout counts as a pass. Passing candidates go to `DarwinistController`; failing ones trigger a feedback request. |
| `DarwinistController` | Validation with TACO. SAT: the candidate is wrong; its counterexample joins the pool (unless it is a duplicate or the pool is full) and, if it did, feedback is requested. UNSAT: the candidate is checked by RAC on **all** stored counterexamples (`lastRACAnalysis`, 5-second timeout counting as a pass); if it passes, `fixFound` writes the report and, with `finishOnFirstFix` (on), stops the run. |
| `WeedQueue` (`StrykerStage.weedQueue`) | The three queues and the termination rule (section 3). |
| `VariablizationData`, `stryker.StrykerVariablizerVisitor`, `VariablizedSATVerdicts`, `stryker.LoopUnrollTransformation` | Variabilization (section 5). |
| `input/` | The messages: `MuJavaInput` (q1), `OpenJMLInput`/`OpenJMLInputWrapper` (q2), `DarwinistInput` (q3), and `MuJavaFeedback`, the search state each candidate carries. |
| `RacTimeout`, `RacTimeoutInstrumenter` | Stop RAC runs that time out: every method and loop body of the JML4C batch starts with `RacTimeout.check()`, on an existing line (JML4C's error lines are mapped back to mutants by line number). |
| `stryker.MethodToCheck` | Reads and builds the `methodToCheck` names ([METHOD-NAMING.md](METHOD-NAMING.md)). |

## 3. Queues and termination

Each queue item counts from the moment it is enqueued until its consumer calls
`ack`; taking it does not remove it from the count (`take()` returns the same
in-flight item until it is acknowledged). `ack` returns the number of items not
yet acknowledged in the three queues; when it reaches 0 there is nothing left
to do, every controller shuts down, and `StrykerInitialStage` logs
`No fix was found`. Unskippable's own queue is not counted. A run also ends at
the first fix.

## 4. The mutation space

Only statements marked `//mutGenLimit N` with N > 0 are mutated; N is how many
times the statement may still be mutated, decremented at each generation. These
marks are the fault localization: there is none other. `MuJavaController`
keeps, for each candidate, a vector of mutation indexes, one per mutable line
(`MuJavaFeedback.lineMutationIndexes`; position 0 is the bottom-most line, and
a value v > 0 selects one of that line's mutations, `row[row.length - v]`). Children of a candidate
(`fatherize`) mutate it further; duplicates are recognised by the MD5 of the
mutant and dropped. A batch holds up to 100 mutants, compiled together by
JML4C into one file (`<tmpdir>/<millis>-<n>/.../aOpenJMLInWrap<i>/<Class>.java`).

## 5. Feedback (variabilization)

When a candidate fails RAC or TACO, `DarwinistController.computateFeedback`
(run on `MuJavaController`'s thread) asks
which mutable lines can still be part of a fix, using the failing
counterexample as a fixed input:

1. copies the candidate into a `sequential/` folder, numbers the mutable lines
   (`insertMutIDs`), removes `//@decreasing`, unrolls `while` loops;
2. replaces, bottom-up from the last mutable line, the right-hand side of the
   line (and then, for `t.f = ...`, its left-hand side) with a fresh parameter
   (`customvar`), keeping the earlier replacements, and asks TACO, with the input
   fixed (`DarwinistController.getTacoMainWithFixedInput`, see
   [SPEC-OUTPUT.md](SPEC-OUTPUT.md#inputtofix)), whether some value of those
   parameters makes the method correct for that input;
3. UNSAT: no value works, so changing only the lines below cannot fix the
   candidate; it goes on to the next line up. SAT (or a TACO error): it stops.
   The lines below the last UNSAT one can be skipped
   (`skipUntilMutID = lastVariablizedMutID - 1`), and the search jumps over the
   candidates that only change them.

Verdicts are cached by the content of the variabilized file
(`VariablizedSATVerdicts`), not by the input. The `methodToCheck` of each such
TACO call is rebuilt with the added parameters' types
(`MethodToCheck.of`).

## 6. Files a run writes

| Where | What |
|---|---|
| `tests/<pkg>/<Class>.java` | Each candidate is copied over the subject, compiled in place and restored from `<file>_temp`; after a fix the fix stays there. Restore with `git checkout -- tests` (the scripts' `--reset`). |
| `generated/ar/edu/generated/junit/` | TACO's counterexample tests |
| `output/` | TACO's intermediate files (`*.djals`, `output.dals`, `output.als`) and the rewritten counterexample tests (`output/ar/edu/output/junit/`) |
| `StrykerLastReports.txt`, `StrykerLastFixFound.java` | The report of each fix found (truncated when a run starts) and the repaired class |
| `stryker.log` | Log of the MuJava, OpenJML and Darwinist controllers (WARN and above; `config/log4j.xml`) |
| `<java.io.tmpdir>/...` | Thousands of mutant and batch files, never deleted by Stryker; `scripts/run-benchmark.sh` gives each run its own temp folder and deletes it |

## 7. Threads and state

The controllers are singletons with one thread each, and their static state
(and `StrykerStage`'s) is never reset: one repair per JVM.
`DarwinistController.computateFeedback` and `validateCandidate` are both
synchronized on that singleton, so feedback and validation never run TACO at
the same time; RAC runs on daemon
`stryker-rac` threads (`RacTimeout`), which are interrupted on timeout and, if
they do not stop within 2 s, stopped with `Thread.stop()` (Java 19 and earlier)
or abandoned. muJava's `Configuration` is set in `RegresionTestBase.check()`.
