# Specified output and fixed inputs

Two features let TACO analyse a method for one given input instead of all
inputs: **`checkAndRunSpecIfFaulty`**, Marcelo Frias's, which computes what the
specification says the output should be for a counterexample's input; and
**`inputToFix`**, the fixed input that both it and Stryker's feedback use. This
page describes them from the code (2026-10-17); what the code does not settle is
in [OPEN-QUESTIONS.md](OPEN-QUESTIONS.md).

The drivers that use `checkAndRunSpecIfFaulty` still name their methods the
short way (`"absoluteInt(int)"`), which the new naming does not find (`The
method to check was not found`; [METHOD-NAMING.md](METHOD-NAMING.md#status)).
With their names converted (not committed), the first four drivers listed
below pass on this branch (2026-10-18), with the same verdicts as before the
new naming, except `llm.iter0.FuncTest`, whose subject changed with it. Before
the new naming they also gave the same verdicts, SAT problems and generated
tests with and without this branch's change to the fixed input (5f66fdfe).

## checkAndRunSpecIfFaulty

A driver calls it instead of `check`:

```java
checkAndRunSpecIfFaulty(GENERIC_PROPERTIES, "absoluteInt(int)");
```

(`unittest/ar/edu/itba/forArielGodio/absolute/ForArielGodioAbsoluteBug01Test.java`.)
It is `RegresionTestBase.checkAndRunSpecIfFaulty`, which:

1. **Checks the method** like `check`, but asserts nothing: it sets
   `generateCheck=true`, `generateRun=false`,
   `include_simulation_program_declaration=false` and
   `hardcode_input_and_run_spec=true` (a key that nothing reads), and runs
   `new TacoMain(null).run(...)`. With `generateUnitTestCase` (on by default in
   the drivers) a SAT check writes a counterexample test under
   `generated/ar/edu/generated/junit/`.
2. **If the check was SAT**, takes that test's input: copies the test to
   `output/ar/edu/output/junit/` (with its own older copy of
   `editTestFileToCompile`), compiles it, loads it, and reads its public field
   `theData`, a `HashMap<String, Object>` with the receiver under `"thiz"` and
   one entry per parameter (built by the test's `getInstance()`).
3. **Runs TACO again with that input fixed**: `new TacoMain(theData)`, after
   setting the static flag `TacoMain.isCheckAndRunSpecAssertionSupport = true`,
   with the same properties. In that run:
   - every method body is replaced by `return <default value>`
     (`jml.block.methodBodySimplifier`, added by `JmlStage` through
     `ASTSimplifierManager`): the code is not executed, only the specification
     is used;
   - the input is added as a precondition, on the TACO side
     (`JmlExpressionVisitor`, only to spec cases that already have a
     `requires`) and on the jDynAlloy side (see [inputToFix](#inputtofix));
   - DynAlloy builds the assertion `pre => not post` without the program
     (`isCheckAndAfterRunSpec`, see dynalloy4's `docs/ARCHITECTURE.md`), so a
     "counterexample" of this check is a pre-state and post-state that satisfy
     the contract: an output the specification allows for that input;
   - two tests are written under `generated/ar/edu/generated/junit/`: the
     initial state (`<Class>_<method>_<n>`, by `UnitTestBuilder`) and the final
     state (`<Class>Test_<method>_<n>`, by `UnitTestBuilderFinalState`, which
     declares the final receiver, parameters and `result`, and calls the
     method on the final receiver with the initial parameters, with no
     assertions).
4. Returns nothing to the driver; the driver passes unless an exception is
   thrown.

The static flag is never reset, so every later `TacoMain.run` in the same JVM,
another test method of the same driver included, runs in this mode (method
bodies stubbed, no program in the assertion).

Drivers that call it: `ar.edu.itba.forArielGodio.absolute.ForArielGodioAbsoluteBug01Test`,
`ar.edu.itba.forArielGodio.addLoop.ForArielGodioAddLoopBug01Test`,
`llm.iter0.FuncTest`, `edu.utep.ds2.AverageTest` (twice),
`roops.core.objects.AvlTreeTest` and `roops.core.objects.BinomialHeapTest`.
Running one (with its method named the new way) is like running any driver
([RUNNING.md](RUNNING.md#running-tacos-own-drivers)); the tests it produces are in
`generated/ar/edu/generated/junit/`.

## inputToFix

`TacoMain`'s constructor argument (`new TacoMain(theData)`; `null` for a
normal run) is the input to fix, a `HashMap<String, Object>` as above. Two
callers create it:

- **Stryker's feedback**: `DarwinistController.getTacoMainWithFixedInput` takes
  `theData` from a stored counterexample test and runs TACO on the variabilized
  candidate with that input fixed, with `attemptToCorrectBug` and
  `generateUnitTestCase` off ([ARCHITECTURE.md](ARCHITECTURE.md#5-feedback-variabilization)).
- **checkAndRunSpecIfFaulty**, step 3 above.

What it changes:

| Where | Effect |
|---|---|
| `MethodDeclarationSolver` | set to null unless `methodToCheck` contains the method's name (a substring test) |
| `engine.PrecompiledModules(inputToFix)` | for Stryker runs only, adds the Java-arithmetic literal modules for the input's int, long and float values |
| jDynAlloy, `JDynAlloyVisitor.visit(JSpecCase)` | with an input, no `requires` is translated; the program whose id equals `getFullyQualifiedMethodName()` gets the input instead (`processInputToFix`): parameters equal to literals, the heap's field values, and which objects are or are not the same |
| `engine.DynalloyStage` | `translatingForStryker = inputToFix != null && !isCheckAndAfterRunSpec`: Stryker's fixed-input checks negate the post (`(pre and program) => not post`), so SAT means "some value of the variabilized parts makes this input satisfy the contract". The input itself is passed on to DynAlloy only when not translating for Stryker (5f66fdfe) |
| dynalloy4, `DynalloyXlatorVisitor` | widens the index range of the module's variables in the post's invariant predicates from `[b, b]` to `[b, b + 1]` |

This needs jDynAlloy's `stryker` branch. On jDynAlloy master,
`getFullyQualifiedMethodName()` (`JDynAlloyXlatorVisitor`) prefixes the class to
`methodToCheck`, which under the new naming already has it, so no program
matches: with an input to fix, every `requires` is dropped and the input is
never applied, and Stryker's feedback checks are always SAT (no pruning).
jDynAlloy `stryker` (9bb5b49) compares `methodToCheck` as it is.

## History

| Commit | What |
|---|---|
| TACO 8978c816, dynalloy4 43970f8 (2025-10-28) | `checkAndRunSpecIfFaulty` and `isCheckAndAfterRunSpec` |
| TACO 6c3a52f9, dynalloy4 b58e74b (2026-06-21) | method bodies stubbed; the index widening for a fixed input |
| TACO 4f232266, dynalloy4 9deb72a (2026-08-31) | fixes in computing the specified output |
| TACO ad8baa34 (2026-09-07) | the final-state test |
| TACO d1dfb116, 5f66fdfe (stryker, 2026-10-16/17) | Stryker's fixed-input translation restored; the fixed input passed to DynAlloy only when not translating for Stryker |
| TACO f44f3369, jDynAlloy 9bb5b49 (stryker, 2026-10-18) | under the new naming: the counterexample test found again (step 2), and the fixed input applied |
| TACO c6580424 (stryker, 2026-10-20) | under the new naming: the final-state test written again ([FIXES-FOR-MASTER.md](FIXES-FOR-MASTER.md), N10) |
