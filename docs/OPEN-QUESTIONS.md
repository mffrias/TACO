# Open questions

Points where the code does not say what was intended, collected for the
maintainer (Marcelo Frias) instead of being guessed in the other pages.
dynalloy4's own questions are in its `docs/OPEN-QUESTIONS.md`. Line numbers are
those of the stryker branch, 2026-10-17 (the method naming ones, 2026-10-18).

## Method naming

1. **Calls into the built-in modules.** e5760d76 names every call
   `<receiver type>_<method>`, while the built-in modules (`simplejml/builtin`,
   the precompiled `.djals`) declare their programs without the prefix. This
   branch prefixes only static calls and calls to the classes TACO translates
   (507ecc4b; [METHOD-NAMING.md](METHOD-NAMING.md#status), item 3b). Or should
   the built-in modules' programs get the prefix too?
2. **Overloads.** Two methods or constructors with the same name get the same
   program name. This branch tells them apart by their parameter types in the
   recursion unfolder and by their number of arguments in the scope
   inference (jDynAlloy d2b09e9, TACO 7b560282; item 5), which still leaves
   two overloads with the same number of parameters unusable in one analysis
   (they worked on comitaco, whose jDynAlloy named programs
   `<signature>_<program>_<index>`). Should program names include the
   parameter types instead, or the index again?
3. **jDynAlloy's remaining prefixes.** `JBindingKey` (line 67) keys static
   programs as `<signature>_<program>`, the rule the built-in modules follow.
   Both `ReplaceModifiesModuleVisitor`s test `startsWith(<signature>_<program>_)`,
   which the new names never match; they stopped matching with 3ae43be3
   (2022), and comitaco still generates the frame conditions
   ([KNOWN-LIMITATIONS.md](KNOWN-LIMITATIONS.md#analysis)). Should they match
   again (`<program>(`), given that JML's checker already enforces
   `assignable` by field name?
4. **Your drivers.** Should the non-Stryker drivers (the spec-output ones
   included) be converted to the new naming, with `scripts/convert-method-names.py --all`
   or by you? Two are converted (`ximena.isEvenTest`,
   `ForArielGodioBinarySeachBug01Test`).
5. **`MethodDeclarationSolver` line 86** keeps the input to fix only if
   `methodToCheck.contains(<method name>)`, a substring test. Intended, or an
   exact match?
6. **`InformationRecoveryVisitor`**: the constructor branch (lines 137-160)
   compares the bare constructor name with the qualified `methodToCheck`, which
   can never be equal. Intended?
7. **The command line** (`TacoMain.main`) appends `_0` to the method name. Should
   it take the new form?

## Specified output (`checkAndRunSpecIfFaulty`)

8. The key `hardcode_input_and_run_spec` is set
   (`RegresionTestBase.java:104`) but nothing reads it. Was it meant to replace
   the static flag `TacoMain.isCheckAndRunSpecAssertionSupport`, and should the
   flag be reset after the second run? As it is, every later run in the same JVM
   runs in spec-output mode.
9. Should `checkAndRunSpecIfFaulty` assert something, or return the second
   run's result to the driver?
10. `methodBodySimplifier` replaces every method body, of every class, with
    `return <default>`. Is that right for pure methods used in specifications?
11. The final-state test (`UnitTestBuilderFinalState`) calls the method on the
    final receiver, with the initial parameters, and asserts nothing. Should it
    call the method, and should it assert the outputs?
12. With an input to fix, jDynAlloy drops every `requires`, the callees'
    included, and adds the input instead. Is the TACO-side `requires` with the
    input (`JmlExpressionVisitor.java:1543-1548`) still needed, and is dropping
    the callees' preconditions intended?
13. `edu.utep.ds2.AverageTest.test_absMethodTest` checks `calculateAverage`,
    while its commented-out line checks `abs(int)`. A copy-paste slip?

## Other

14. `ximena.isEvenTest.test_isEven` expects no counterexample, but TACO finds
    a real one: `isEven(n)` returns `MutRecursiveAux.m(n) == 1`, and `m(n)` is
    `n + 1`, so `isEven(2)` is false (as in
    [FIXES-FOR-MASTER.md](FIXES-FOR-MASTER.md), F4). Is the subject or the
    expectation the one to change?
15. The IntelliJ library `mjc-1_3_2` points at `lib/mjc-1_3_2.jar`, which is not
    in the repository. Can it go?
16. `ant unittest` runs no driver: its class path (`build.classpath`) lacks
    `bin/`, so every driver fails with `ClassNotFoundException`. With `bin/` it
    would run every driver under `unittest/`, hundreds of Stryker benchmarks
    included (many hours). Should it get `bin/` on its class path, leave the
    Stryker benchmarks out, or go?
17. The README's references list J. P. Galeotti's PhD thesis as "Software
    Verification using Alloy" (Universidad de Buenos Aires, 2010). That title
    comes from the Mathematics Genealogy Project, not from UBA's library: is it
    right?
18. Should other DynAlloy or TACO papers be listed in the README's references?

## From the Javadoc pass

Points the Javadoc marks "Unclear:" (2026-10-18; line numbers of the `docs` branch).

19. **Constructor parameters** (`junit/InformationRecoveryVisitor.java:137`): besides item 6,
    the constructor branch compares the parameter types without turning underscores into dots
    (line 150), unlike the method branch (line 122). So no constructor's parameter names are
    recovered, and `SnapshotBuilder.createSnapshot` would fail (`IndexOutOfBoundsException`,
    line 200) for a constructor with parameters. Intended?
20. **Counterexample tests of constructors** (`junit/UnitTestBuilder.java:214-230`): the test
    declares `instance = null` and the parameters but calls nothing, and the
    `getAccessibleConstructor` helper that `JUnitPrettyPrinter` writes is never called. Should
    the test call the constructor?
21. **The final-state test under the new naming**: answered; it was not written (the lookup
    by the qualified name), which TACO stryker c6580424 fixes (docs/FIXES-FOR-MASTER.md, N10).
    Kept so that the numbers of the items below stay the same.
22. **The final-state snapshot** (`snapshot/SnapshotBuilder.java:313-412`,
    `createSnapshotFinalState`): for a constructor it reads the receiver in the initial state
    but stores it as `thiz_1`, and the static fields in the final state; for a method, the
    static fields in the initial state; and its `catch` (lines 405-408) retries a parameter with
    the same expression. Which state should each come from?
23. **Literals of a fixed input** (`engine/PrecompiledModules.java`, the
    `PrecompiledModules(inputToFix)` constructor): it adds the literal modules of the input map's
    own values only, not of the fields of the objects in it, for which jDynAlloy's
    `processInputToFix` creates literals later, during the translation. Do those literals reach
    the model? (Not checked.)
27. **Passes that find nothing to do** (`jml/loop/LSBlockVisitor`, `WhileBlockVisitor`):
    `ASTSimplifierManager` runs them after `ForRemoverVisitor` and `WhileRemoverSimplifier` have
    already removed the `for` and `while` loops they handle, so in the current pipeline both
    only copy the AST. Are they still needed, in that order?
28. **More iterations than the loop bound** (`jml/loop/WhileRemoverSimplifier`): a `while` loop
    is unrolled `loopUnroll` times and followed by `assert !c`, so an input that needs more
    iterations fails that `assert`, which jDynAlloy turns into `java_lang_AssertionErrorLit`: a
    counterexample, unless the specification allows an `AssertionError`. Is that intended, or
    should such inputs be left outside the bounds (as an `assume` would)?
29. **A pass that changes nothing** (`jml/literal/LiteralBlockVisitor`): its expression visitor,
    `LiteralExpressionVisitor`, overrides nothing, so the pass changes no literal and only
    copies the AST (dropping `x;` statements); the commented-out code turned string and char
    literals into `String.buildInstance(...)` and `Character.buildInstance(...)` calls. Is it
    still needed?
30. **`FieldParameterExpressionVisitor`** (`jml/FieldParameter/`): nothing in the three
    repositories refers to it, its `visitMethodCallExpression` pushes no result for the call,
    and its two branches do the same thing. What was it for, and can it go?
31. **Allocation counts overwritten** (`dynalloy/DynAlloyScopeInferencePlugin.java:20-27`,
    `transform`): after `inferScope()`, every allocation count in the program plugin's
    `ObjectCreationCounter` is overwritten with the inferred scope of its type (77f36e19), but
    nothing in TACO or jDynAlloy reads that counter afterwards, and the local map
    `inferredAllocsPerType` is never used. Is it meant to feed something, or can it go?
32. **`DynAlloyProgramScopeInferecePlugin`** (misspelled, `dynalloy/`): nothing creates it, and
    this package's `ObjectCreationCollector` and `ObjectCreationCounter` are used only by it (the
    live `DynAlloyProgramScopeInferencePlugin` uses jDynAlloy's copies). Is it kept on purpose,
    or can the three go?
33. **Scopes of primitive types** (`infer/ScopeInference.java`, `consolidate_scope`): should
    the `type_scopes` entries for `int`, `long`, `char` and `float` count in the inferred scope,
    as the classes' entries do? It looks each entry up by turning every underscore of the
    signature id into a dot, so `JavaPrimitiveIntegerValue` never matches `int`; `UBoundPlugin`,
    when it runs, does apply them.
34. **`JDynAlloyFunPredCollector`** (`infer/`): `infer_alloy_bitwidth` runs it on every module,
    but the test that used its result is commented out (the code now tests
    `useJavaArithmetic`), so the result is ignored. Is it still needed?
