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
24. **`switch`** (`simplejml/BlockStatementsVisitor`, `visitSwitchStatement`): every group is
    translated and runs, in order; the selector is translated but not used, and the case labels
    are ignored. Is `switch` meant to be supported?
25. **The receiver in the pre-state** (`simplejml/helpers/ExpressionSolver`): for `this`, it
    gives the primed `thiz'` when the visitor is a `JmlExpressionVisitor` in the pre-state, the
    opposite of `JmlExpressionVisitor.visitThisExpression` and of
    `JmlExpressionSolver.buildInstantVariable`; and `JmlExpressionVisitor` overrides
    `visitMethodCallExpression` without calling it. Is that branch reached, and is it right?
26. **`JavaSignatures`** (`simplejml/helpers`) maps `java_util_HashSet`, `java_util_HashMap` and
    `java_util_LinkedList` to `java_util_Set`, `Map` and `List`, but its only user,
    `CTypeAdapter`, looks it up by the simple name (`HashSet`), which is never a key, so the
    mapping has no effect. Should the keys or the lookup change, or is the mapping no longer
    wanted (`java_util_HashSet` now has its own precompiled module)?
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
35. **Two scopes for one analysis** (`alloy/bound/UBoundPlugin.java:36-47`, `transform`): with
    `inferScope`, it sets each `type_scopes` signature's scope to its `type_scopes` value, after
    `DynAlloyAppendCommandPlugin` has written the `check` command with the inferred scope (the
    larger of that value and the input plus program scope), and it changes the `InferredScope`
    singleton in place. Should the command get the `type_scopes` values too, or the bounds the
    inferred scope?
36. **`_var_` prefixes** (`alloy/sk/SkPredArgument.java:67-70`): a prefix that contains `_var_`
    is cut before it, so the argument's name is no longer a `QF` field and the fact built from
    it would name a missing field. Why is it cut, and do such prefixes occur (the models seen
    have `l0`, `l2`, ...)?
37. **Constraints across two states** (`simplejml/JmlExpressionVisitor.java`,
    `visitJmlConstraint`): a constraint is translated in one state that flips to the post-state
    at the first `\old`, so `\old(x) <= x` gives `x <= x'` but `x >= \old(x)` gives `x >= x`
    (`setInstant(instant)` sets the state to itself, and `visitJmlOldExpression` sets the
    post-state instead of restoring the previous one). Intended?
38. **The fixed input in heavyweight spec cases** (`JmlExpressionVisitor`,
    `visitJmlGenericSpecCase`): the input to fix is added only to lightweight spec cases that
    have a `requires`; `normal_behavior` and `exceptional_behavior` spec cases, and spec cases
    without `requires`, never get it. Intended? (It matters only if the TACO-side `requires` of
    item 12 is still needed.)
39. **Model fields** (`JmlExpressionVisitor.visitJmlFieldDeclaration`): `JDynAlloyASTVisitor`
    visits model fields with itself, so this override and the model-field lists of
    `JmlClassDeclarationResult` seem never reached. Can they go?
40. **Unused classes in simplejml/**: nothing uses `ExpressionVisitorWithNewParamsInMethodCall`
    (an older call translation that gives static calls no receiver, which `ArgEncoder` now
    rejects), `JDynAlloyPureMethodVisitor` (empty), `MethodCallExpressionVisitor` (its only
    override does what the inherited one does), and `ResultVariableNamesCollectorVisitor` and
    `VariableNameCollectorVisitor` (used only by two private methods of `JmlExpressionVisitor`,
    one never called and one called only from commented-out code). Can they go?
41. **Two `java_lang_Integer` modules** (`simplejml/builtin/JInteger.java`):
    `engine.PrecompiledModules` adds the module this class builds and, under the same key,
    `java_lang_Integer.djals` (or `java_lang_Integer_JavaPrimitiveIntegerValue.djals`), which
    declares a module of the same name. The analysis uses the parsed one: it is added later, and
    jDynAlloy's `DynAlloyLinker` keeps the later of two modules with the same id (checked by
    running TACO, 2026-10-20; docs/FIXES-FOR-MASTER.md, F8). Is the Java-built one still needed?
42. **`System.currentTimeMillis()`** (`simplejml/builtin/JSystem.java`): does a static call bind to
    this program? TACO names the call `java_lang_System_currentTimeMillis`; the program's first
    parameter is `thiz`, and `JBindingKey` keys such programs by their bare name.
43. **The `Class` module's names** (`simplejml/builtin/JClass.java`): the module is
    `java_lang_Class` and the signature `Class`, but jDynAlloy's docs/LANGUAGE.md says the two must
    match. `JSignatureFactory.buildClassSingleton` extends `java_lang_Class` with the qualified
    name, which TACO always uses, while the exception builders hard-code `Class`. Which name is
    intended? (It matters only with `useClassSingletons`, false in every configuration.)
44. **`JObjectArray`** (`simplejml/builtin/JObjectArray.java`): nothing references it; the
    object-array module comes from `java_lang_ObjectArray.djals` or
    `java_lang_AlloyIntObjectArray.djals`. Can it go?
45. **Divisions that only call markers** (`simplejml/builtin/JMLAuxiliaryConstantsFactory.java`,
    lines 843, 1095, 1127): in JML contracts, the `long`, `char`/`int` and `char`/`long` divisions
    call marker predicates with empty bodies, while the `int` division and the `%` builders call
    the real predicates. Is anything else meant to constrain those quotients?
46. **The `contains` field of the JML collection models**
    (`simplejml/builtin/models/JJMLObjectSequence.java:100` vs 173, 202, 235;
    `models/JJMLObjectSet.java:110` vs 123, 152, 184): the field is declared only when
    `JMLObjectSequenceToAlloySequence` / `JMLObjectSetToAlloySet` is on, but the programs use it
    only when the option is off (when it is on they use `thiz` itself). Is the condition inverted?
47. **Every pass's result written to `output/`** (`jml/ASTSimplifierManager.java`, `simplify`):
    the result of each simplification pass is written to `output/<pkg>/<Class>.java`, each write
    overwriting the last; the list of written files is thrown away, and `JmlStage` writes the
    final result again. Why are the intermediate writes there?
48. **`\old` in the arguments of assumed calls** (`jml/SpecMethodCallRemoverVisitor.java`,
    `visitJmlMethodDeclaration`, lines 381 and 525): for a method call in a postcondition, copies
    of the arguments with `\old` replaced (`newArgsForMethodCall`) are built and never used; the
    assumed call is a clone of the original, `\old` included. Should it use the copies?
49. **The default `return` appended to every non-void body**
    (`jml/ReturnStatementWrapperVisitor.java`): which later step needs it? A body that compiles
    already ends in a return or a throw, though earlier passes can change that (an unrolled
    `while (true)` leaves a reachable end).
50. **`MethodCallNonPrimedVariableReplacementVisitor`** (`jml/`): nothing creates it, its visits
    are incomplete, and its name speaks of method calls while its code only handles `\old`. Is it
    still needed, and what was it meant to replace?
51. **Branch labels** (`jml/utils/LabelUtils.java`): `ifLabelCount` and `whileLabelCount` are
    never incremented, so every label is `if_1` (and `while_10000`). Were they meant to be
    distinct?
