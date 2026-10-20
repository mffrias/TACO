# Fixes for master

The `stryker` branches of the three repositories (TACO, jDynAlloy, dynalloy4)
fix bugs that are in Marcelo Frias's masters too. This page lists them for
review when the branches are merged, one entry per fix: the bug and how it
shows, its cause in master, the commit that fixes it, and how the fix was
checked. Some fixes are a choice among several reasonable ones; those are
marked **our choice** and give the alternative. The others restore what the
code was meant to do.

Line numbers are those of the masters: TACO e5760d76, jDynAlloy e8c6b06,
dynalloy4 a2ff826. Commits are on the `stryker` branch of the repository
named. Unless stated otherwise, checks ran on macOS (Apple Silicon) with Zulu
8u504 arm64 and MiniSat 2.2. "The smoke test" is TACO's
`scripts/smoke-test.sh`: four repair benchmarks whose exact results are known
(fix found, which fix, TACO calls, pruned mutants; see
[RUNNING.md](RUNNING.md#checking-a-change)).

The `stryker` branches also add things that are not fixes (Stryker's scripts,
the smoke test, Docker, JDK 21 and 25, Javadoc); they are listed at the end,
in [What else the branches add](#8-what-else-the-branches-add).

| # | Fix | Repository, commit | |
|---|---|---|---|
| N1 | The counterexample test is written under one name and looked up under another | TACO f44f3369 | |
| N2 | The generated test calls the method by its qualified name | TACO a7308702 | |
| N3b | Calls into the built-in modules get the class prefix | TACO 507ecc4b | our choice |
| N4 | With a fixed input no program matches | jDynAlloy 9bb5b49 | |
| N5 | Overloads are taken for recursion | jDynAlloy d2b09e9, TACO 7b560282 | our choice |
| N7 | No symmetry breaking | TACO 779cf0bc | |
| N8 | Scope inference misses the method's specification | TACO bdb4cdc6 | |
| N9 | Stryker's own names for the method under analysis | TACO afac4a68, f16b9821, 7510b10f | |
| N10 | The final-state test looks the method up by its qualified name | TACO c6580424 | |
| M1 | An invariant broken through a callee is not detected | jDynAlloy 0f920f1 | |
| S1 | The controllers' queues: the search never ends without a fix | TACO 1b660955, 1be07539 | |
| S2 | Helper classes renamed when a batch is adapted for JML4C | TACO 02422f46 | |
| S3 | A mutant without a RAC verdict makes RAC fail | TACO 52bd753b | |
| S4 | A stale index map after a new batch | TACO ae2807ed | |
| S5 | Fresh checkouts and unusual paths | TACO 29174468 | |
| S6 | The counterexample pool fills up, and the run hangs | TACO 3c677dc0 | our choice |
| S7 | A TACO error during feedback prunes the whole search | TACO 7f354b7c | our choice |
| S8 | A counterexample that cannot become a test hangs the run | TACO 6b8653bf | |
| S9 | A feedback jump applied twice | TACO 7d5d3285 | |
| S10 | The feedback position of a line with no mutations left | TACO d2620144 | |
| S11 | Counterexample tests that do not compile | TACO be5e99b2, c95cdbc5 | |
| S12 | The counterexample snapshot has no compilation units | TACO 2ce38a21 | |
| S13 | Feedback checks translated without the fixed input | TACO d1dfb116, 5f66fdfe | |
| S14 | Classes whose names contain underscores | TACO e36d1c74 | our choice |
| S15 | `PrintStream` objects in counterexamples | TACO eebe0963 | |
| S16 | The JML tools on JDK 8 | TACO a554e17d | our choice |
| S17 | RAC timeouts need `Thread.stop` | TACO a911e102, 869c6f57 | our choice |
| B1 | A clean build fails: an import of a test subject | TACO 39f34dd6 | |
| B2 | Class files from `tests/` shadow the subjects | TACO 7a1bc583 | |
| B3 | A clean build fails outside macOS: source encoding | TACO d393b17c | |
| B4 | `setupTACO.sh` exits with the wrong status, goes on after a failed clone | TACO 167ca8df | |
| B5 | The muJava++ and OpenJava jars | TACO 4edb54ec | our choice |
| D1 | The solver cannot be chosen; SAT4J is too slow for Stryker | dynalloy4 08c2fd8, 40c1beb | our choice |
| D2 | No MiniSat 2.2 for arm64 (macOS, Linux) | dynalloy4 f06eb89, 3b4507b | our choice |
| D3 | Stale solver libraries in the temporary directory | dynalloy4 0138709 | |
| D4 | MiniSat 2.2 does not load on Java 17 and later | dynalloy4 eddee24 | |

## 1. The new method naming

Since TACO 955795ac and jDynAlloy cad278a (2026-10-16), the method under
analysis is `<package>_<Class>_<method>(<types>)` and its program
`<declaring class>_<method>`. Marcelo's e5760d76 (TACO) and e8c6b06 (jDynAlloy)
fixed instance calls (item 3) and non-recursive methods unfolded as recursive
(item 6). The fixes below are the rest; the numbers are those of the list sent
to Marcelo. Several follow the pattern that cad278a removed from jDynAlloy:
code that builds `<signature>_<program id>` and compares it with
`methodToCheck`, where the program id already starts with the class.

### N1. The counterexample test is written under one name and looked up under another

**How it shows.** With a driver that uses the new names:
- `checkAndRunSpecIfFaulty` fails in its second step with
  `ClassNotFoundException:
  ar.edu.output.junit.AddLoopTest_forArielGodio_addLoop_bug01_AddLoop_addLoop_0`
  (`ForArielGodioAddLoopBug01Test` with its name converted; the same for
  `AverageTest` and `FuncTest`).
- Stryker never gets its counterexample: `FileNotFoundException` for
  `generated/ar/edu/generated/junit/<Class>Test_<method>_0.java`, so every
  repair ends in "No fix was found".

It only shows when something reads the test back after TACO wrote it; a plain
`check` passes.

**Cause.** `src/ar/edu/taco/junit/UnitTestBuilder.java`: 955795ac removed `+
"Test"` from the class name the test is *written* under (line 64), but not
from `getOutputClassName()` (lines 1791-1795), which
`getOutputClassFilename()` (1781) and so `JUnitStage.getJunitFileName()` and
`TacoMain.outputJunitFile` (TacoMain.java:524-526) use to *find* it.
`RegresionTestBase` reads `outputJunitFile` in `checkAndRunSpecIfFaulty` (line
382).

**Fix.** TACO stryker f44f3369: `getOutputClassName()` drops `"Test"` the same
way (one line).

**Verified.** Without it: all 4 smoke-test benchmarks and 83 of a sample of
101 converted Stryker drivers stop at the missing file, and the spec-output
drivers (names converted) fail as above. With it (and fix 2): the tests are
found and loaded; see [Overall](#6-overall).

### N2. The generated test calls the method under test by its qualified name

**How it shows.** The generated counterexample test contains
`getAccessibleMethod("ximena.MutRecursive", "ximena_MutRecursive_isEven",
true)`. That method looks the name up among the class's Java methods, finds
nothing, and the test cannot run it. In Stryker every runtime check of a
candidate fails ("FAILED METHODDDD FOR NO REASON"), so no candidate ever
reaches TACO.

**Cause.** `UnitTestBuilder.getMethodInvocationStatements`, lines 1763-1764:
it writes `recoveredInformation.getMethodToCheck()`, which since 955795ac is
the qualified name.

**Fix.** TACO stryker a7308702: use the Java name of the method it already
resolved, `methodToCheck.getName()` (one line).

**Verified.** With fix 1 only, every RAC check fails and no benchmark finds a
fix. With 1 and 2, three of the four smoke-test benchmarks give their
reference reports exactly; the fourth needs fix 4.

### N3b. Calls into the built-in modules get the class prefix too

**How it shows.** A `System.out.println(...)` in a method TACO translates
stops the translation: jDynAlloy reports "Program not found" for
`java_io_PrintStream::java_io_PrintStream_println`. Seen in
`ForArielGodioAbsoluteBug01Test` (its `main` prints) and in
`girish.github.heapbenchmarks.StrykerBinomialHeapTest`, one of the Stryker
drivers. Any call into a built-in module (the JDK classes TACO models) fails
the same way.

**Cause.** `src/ar/edu/taco/simplejml/ExpressionVisitor.java:571-572`
(`visitMethodCallExpression`): since e5760d76, which fixed item 3, every call
is `<receiver type>_<method>`. That is right for the classes TACO translates,
whose programs `MethodDeclarationSolver` names `<class>_<method>`
(MethodDeclarationSolver.java:104). The built-in modules declare their
programs without the prefix (e.g. `JPrintStream.java:123-125`, program
`println` of `java_io_PrintStream`, and the precompiled `.djals` modules), and
jDynAlloy binds calls to them by the earlier rule (`JBindingKey`:
`<signature>_<program>` for static programs, the bare name for instance
programs).

**Fix (our choice).** TACO stryker 507ecc4b: a call gets the prefix when its
receiver is one of the classes TACO translates (`classToCheck`, and the
`classes`, or `relevantClasses`, it parses); calls into any other module keep
the rule from before e5760d76: `Class_method` for static calls, the bare name
otherwise. *Alternative:* give the built-in modules' programs the prefix too
(the `simplejml/builtin` classes, the precompiled `.djals` files, and
`JBindingKey`), so that every program follows one rule.

**Verified.** `ForArielGodioAbsoluteBug01Test` with its names converted passes
both its tests, with the same three SAT verdicts as before 955795ac. The
girish driver gets through TACO with no binding error, and Stryker searches to
the end. The smoke test is unchanged (its classes call only their own
methods).

### N4. With an input to fix, no program matches, so the input is never applied

**How it shows.** Stryker's feedback checks (TACO with the counterexample's
input fixed) always come back SAT, so the feedback prunes nothing: the
smoke-test benchmark `StrykerSinglyLinkedListContains2Bug4Dx10DTest` still
finds its fix, but with 24 TACO calls and 0 mutants pruned instead of 13 and
11. The same matching decides where `checkAndRunSpecIfFaulty`'s fixed input
goes.

**Cause.** jDynAlloy
`src/ar/edu/jdynalloy/xlator/JDynAlloyXlatorVisitor.java:1825-1829`:
`getFullyQualifiedMethodName()` returns `<classToCheck>_<methodToCheck>`. With
an input to fix, `JDynAlloyVisitor.visit(JSpecCase)`
(`ast/JDynAlloyVisitor.java:335-347`) translates no `requires` and gives the
input only to the program whose id equals that name; since `methodToCheck`
already has the class, no program matches, the input is dropped along with
every `requires`.

**Fix.** jDynAlloy stryker 9bb5b49: `getFullyQualifiedMethodName()` returns
`methodToCheck`, the same comparison as cad278a's in `RelevancyAnalysisUtils`
and `ModifiesSolverManager`.

**Verified.** With it, `Contains2Bug4Dx10D` gives its reference report again
(fix `[ 8 1 ]`, 13 TACO calls, 11 pruned); the other three benchmarks are the
same with and without it.

### N5. Overloads are taken for recursion

**How it shows.** With the two-constructor `ximena.MutRecursive` of TACO
37a140a4 (`MutRecursive()` calls `new MutRecursive(0)`),
`isEvenTest.test_Constructor` fails in DynAlloy with a `NullPointerException`
at `ProgramTranslator.visit(InvokeProgram)` (dynalloy4
ProgramTranslator.java:198). The JDynAlloy shows why: `MutRecursive()` was
unfolded as a recursive program, and its call became `call
ximena_MutRecursive_MutRecursive_unfold_0[...]` with the arguments of the
other constructor, which matches no program. Marcelo's current test
(`ximena.isEvenTest`) avoids the overload; any two overloads where one calls
the other hit this.

**Cause.** Overloads share their program id (VAlloyVisitor has not added an
index to it since jDynAlloy's first commit, 5b33641), and two places identify
a program by its id alone:
- jDynAlloy `src/ar/edu/jdynalloy/xlator/RecursionUnfolderVisitor.java`: the
  call graph it is given (`CallGraphVisitor`, one node per program id) makes
  `MutRecursive()` → `MutRecursive(int)` a self-loop, so line 54 takes it for
  recursion; `retrieveProgram` (lines 112-123) returns the first program with
  the id, and the call test at line 164 uses the same graph.
- TACO `src/ar/edu/taco/dynalloy/ProgramInliner.java:23-25`, which inlines
  every call for scope inference (`DynAlloyProgramScopeInferencePlugin`),
  takes the first program with the called id. Once the unfolder is fixed, it
  resolved `MutRecursive()`'s call to `MutRecursive()` itself and inlined it
  forever: `StackOverflowError` at ProgramInliner.java:26.

**Fix (our choice).** Programs are told apart by name and parameter types, as
`JBindingKey` does, and calls by name and number of arguments, which is how
DynAlloy resolves them:
- jDynAlloy stryker d2b09e9: `RecursionUnfolderVisitor` builds its own call
  graph from the same modules, with nodes `<program id>(<parameter types>)`. A
  call gets an edge to every program with its id and number of arguments (so
  two overloads with the same arity both count), and `retrieveProgram` uses
  the same key. The id-keyed call graph is still what pruning and the modifies
  table use.
- TACO stryker 7b560282: `ProgramInliner` takes the program with the call's id
  and number of arguments, falling back to the id alone.
*Alternative:* keep overloads apart in the names themselves (program ids with
the parameter types, or VAlloy's index suffix back), and leave both lookups as
they are.

**Verified.** With that two-constructor `MutRecursive`: `test_Constructor`
checks `MutRecursive()` UNSAT, as expected. With the unfolder fix alone it
overflows the stack in `ProgramInliner`, so both parts are needed. With the
current subject (nested recursion through `MutRecursiveAux.m`), `isEvenTest`
gives the same verdicts and the same counterexample (n = 2, see [Found, not
fixed](#7-found-not-fixed)) as without the fix;
`ForArielGodioBinarySeachBug01Test` still passes; and the smoke test is
unchanged.

### N7. No symmetry breaking, so UNSAT checks are much slower

**How it shows.** With `useJavaSBP`, the models have no symmetry-breaking
predicates. Verdicts do not change, but UNSAT proofs get much slower: in the
smoke-test benchmark `StrykerBinomialHeapFindMinimum1Bug11DTest` the final
check of the fix takes 267 s instead of 22 s (the model has 2 facts instead of
13: the `minP`, `FReach` and field-splitting facts are gone; the CNF is even
smaller, 808 primary variables instead of 955).

**Cause.** `src/ar/edu/taco/alloy/sbp/JDynAlloyClassHierarchy.java:414-415`
(`RootNodesHelper`): the root nodes are the parameters of the method under
analysis, found by comparing `sigId + "_" + program.getProgramId()` with
`methodToCheck` without types. Doubled class prefix, no match, no root nodes,
and `SymmBreakPredPlugin` returns the model unchanged ("If there are no root
nodes we generate no axioms").

**Fix.** TACO stryker 779cf0bc: compare `program.getProgramId()` as it is (one
line).

**Verified.** The benchmark's model has its 13 facts again; the whole run
takes 31 s (slowest TACO call 20.7 s, as before 955795ac) and its report is
the reference one.

### N8. Scope inference does not see the method's specification

**How it shows.** Nothing measurable so far: on the drivers tried the inferred
scopes and the CNF are identical with and without the fix. It is in the same
family as 7, and 877 of the 880 Stryker drivers use `inferScope`, so it is
fixed too.

**Cause.** `src/ar/edu/taco/dynalloy/DynalloyToAlloyManager.java:96`: the
formulas given to DynAlloy for TACO's scope inference
(`DynAlloyProgramScopeInferencePlugin`) are the invariants plus the
specification of the method whose `<signature>_<program id>` equals the
assertion id without `check_`. Doubled class prefix, so only the invariants
are passed, without the method's `requires` and `ensures` (before 955795ac
they matched).

**Fix.** TACO stryker bdb4cdc6: compare `progDecl.getProgramId()` (one line).

**Verified.** `ForArielGodioBinarySeachBug01Test`, and
`edu.utep.ds2.AverageTest` and `ForArielGodioAddLoopBug01Test` with their
names converted: the same scopes, CNF and verdicts with and without it; the
smoke test gives its reference reports with it.

---

### N9. Stryker's own names for the method under analysis

**How it shows.** Master's Stryker still expects the method under analysis to
be named `<method>_<index>` (`"contains_0"`), the form drivers used before
TACO 3ae43be3 (2022). With `<method>(<types>)` or the new form, Stryker takes
a wrong method name, and every feedback check it asks TACO for names the
method `<method>_0`, which TACO rejects ("The method to check was not found").
The variabilized method of a feedback check also has one more parameter
(`customvar_<n>`) per replaced expression, so its name changes at every step.
The Stryker drivers were still in the `_0` form.

**Cause.** `src/ar/edu/taco/engine/StrykerStage.java:121-122` and
`TacoMain.editTestFileToCompile` (`TacoMain.java:724-725`) take the method
name as everything before the last `_`; `DarwinistController.java:547` and
`OpenJMLController.java:311` put `input.getMethod() + "_0"` in the feedback
request.

**Fix.** TACO afac4a68 adds `src/ar/edu/taco/stryker/MethodToCheck.java`,
the only place in Stryker that reads and builds these names (the plain method
name, the parameter types, and the name of a variabilized method with its
`customvar` types, which `VariablizationData` now records); f16b9821 moves it
to `<package>_<Class>_<method>(<types>)`. 7510b10f converts the 881 Stryker
drivers with the new `scripts/convert-method-names.py` (906 calls; one of
them Marcelo had converted the same way in e5760d76).

**Verified.** Every converted name matches the signature of the compiled
class (reflection over `bin/`), and a second run of the script changes
nothing. The smoke test and the 101-driver sample run with these names (see
[Overall](#6-overall)).

### N10. The final-state test looks the method up by its qualified name

**How it shows.** With a spec-output driver that uses the new names, the
second TACO run of `checkAndRunSpecIfFaulty` prints
`java.lang.RuntimeException: DYNJALLOY ERROR!
forArielGodio.absolute.bug01.Absolute.forArielGodio_absolute_bug01_Absolute_absoluteInt()`
(`ForArielGodioAbsoluteBug01Test` with its names converted; `AverageTest`
twice), and no final-state test (`<Class>Test_<method>_<n>`) is written. The
driver still passes: `JUnitStageFinalState` wraps the exception in a
`TacoException` (JUnitStageFinalState.java:90-92), which `TacoMain.run`
prints before going on.

**Cause.** `src/ar/edu/taco/junit/UnitTestBuilderFinalState.java`, a copy of
`UnitTestBuilder`: `createUnitTest` compares each Java method's name with
`recoveredInformation.getMethodToCheck()` (line 83), which since 955795ac is
the qualified name, tests for a constructor the same way (90), and calls
`getDeclaredMethod` with that name (94), which throws `NoSuchMethodException`
(98-99). `getParametersInitializationStatements` (1051) and the call the test
would write (1911-1912) use the same name. 955795ac changed the lookup in
`UnitTestBuilder` (lines 80-97) but not in this copy.

**Fix.** TACO stryker c6580424: the lookup of `UnitTestBuilder` (the method
by `<package>_<Class>_<method>`, the constructor and the parameter types by
the name after the last `_`), and the call by the Java name, as N2 does.

**Verified.** The four spec-output drivers, with their names converted: the
same verdicts and results as without the fix (all pass); the `DYNJALLOY
ERROR`s are gone (one in `ForArielGodioAbsoluteBug01Test`, two in
`AverageTest`), and their three final-state tests are written, compile and
run. `ForArielGodioAddLoopBug01Test` and `FuncTest` write none, since their
specification runs are UNSAT, as before. The smoke test gives the same
results.

## 2. Invariants and callees

### M1. An invariant broken through a callee is not detected

**How it shows.** With `int a`, `invariant a >= 0` and
`setNeg() { a = -1; }`, TACO finds the violation in `direct() { a = -1; }`
(SAT) but not in `viaCall() { setNeg(); }` (UNSAT). Any method that breaks an
invariant only through the methods it calls is reported correct, and Stryker
can accept such a candidate as a fix.

**Cause.** jDynAlloy re-checks a class's invariants in the postcondition only
if the method under analysis may modify one of the fields they mention
(`JDynAlloyXlatorVisitor.isModifiable_object_invariants` and
`isModifiable_class_invariants`, `xlator/JDynAlloyXlatorVisitor.java:1702-1742`).
The modifies table they read has only the fields each program assigns itself:
`xlator/ModifiesTableBuilder.java:100-108` has the part that adds the callees'
fields commented out since jDynAlloy's first commit, 5b33641 (2022), "This has
to be added back once the naming convention is fixed".

**Fix.** jDynAlloy 0f920f1 restores the loop, as it is in comitaco's
jDynAlloy (`lib/jdynalloy2018.jar`, whose sources are inside the jar; the
published Stryker results come from it).

**Verified.** `viaCall()` and `direct()` are both SAT, as on comitaco.
Against the commit before it (d2b09e9),
with TACO `stryker` 7b560282: the smoke test gives the same full reports on
JDK 8 (arm64 and x86_64), 21 and 25 and in both Docker images; 89 of
Marcelo's drivers, with their names converted (the spec-output ones
included), give the same verdicts and the same SAT problems; and 101 Stryker
drivers, run in pairs for 60 s each, have the same outcomes and reports. One
verdict changes. `roops.core.objects.TreeSetTest`'s check of
`TreeSet.remove(int)` was UNSAT and is SAT now, and rightly so: on its
counterexample, a five-node tree that satisfies the invariant, `remove(0)`
leaves a node with two black children of black heights 2 and 1
(`deleteEntry` and `fixAfterDeletion` never update `blackHeight`). `remove`
assigns no field itself, so before this fix its invariants were never
re-checked. The driver expects no counterexample there, so its
`test_removeTest` now fails, as its `test_insertTest` already did for `add`.
In 7 other drivers some SAT problems differ in size, with the same verdicts.

## 3. Stryker

Master has Stryker's code (`src/ar/edu/taco/stryker/`, `engine/StrykerStage.java`
and Stryker's parts of `TacoMain`), from before the fixes that Luciano Zemin's
comitaco (the repository the published Stryker results come from) made
later. These entries port them, or fix what only showed here; the commit
messages have the details. Most touch only Stryker's code. S14 and S15 also
change how plain TACO turns a counterexample into a test
(`generateUnitTestCase`), and S16 the class library its JML parser reads when
`lib/java7/rt.jar` is there.

### S1. The controllers' queues: the search never ends without a fix

**How it shows.** A repair that finds no fix never finishes; feedback
requests wait behind TACO runs; waiting for the result keeps a CPU core busy.

**Cause.** Each controller has its own queue, with no notion of work in
flight, so nothing can tell that the search space is exhausted
(`StrykerStage.java:79`, `MuJavaController.java:120-144`,
`OpenJMLController.java:85`, `DarwinistController.java:96-105`), and
`SimpleStrykerAPI` polls in a busy loop.

**Fix.** TACO 1b660955 ports comitaco's `WeedQueue`: one object with the
three queues, where a work item stays in flight until its consumer
acknowledges it, and the run ends when nothing is queued or in flight.
Feedback goes straight to `MuJavaController`. 1be07539 fixes a race in that
count (an item taken but not yet in flight was invisible, and the run could
end with "No fix was found" while work was pending; seen once in about 30
runs).

**Verified.** With a delay injected in `take()`, the old count ended 5 of 5
runs early and the new one found the fix in all of them; runs that the race
did not hit give the same results. The smoke test.

### S2. Helper classes renamed when a batch is adapted for JML4C

**How it shows.** A subject with a helper class in its package
(`BinomialHeapNode` next to `BinomialHeap`) gets batches that do not compile.

**Cause.** `MuJavaController.java:796-798`: every line that mentions the class
under analysis is moved to the new package, the helper's lines too.

**Fix.** TACO 02422f46 (from comitaco): leave the helper class's lines alone.

**Verified.** The smoke test.

### S3. A mutant without a RAC verdict makes RAC fail

**How it shows.** `NoSuchElementException` in `OpenJMLController` when a
mutant neither passed nor failed runtime assertion checking (for example,
with no counterexamples to run it on).

**Cause.** `OpenJMLController.java:291` logs
`failedMethods.keySet().iterator().next()` without checking that there is one.

**Fix.** TACO 52bd753b (from comitaco): log it as an "Unidentified Mutation".

**Verified.** The smoke test.

### S4. A stale index map after a new batch

**How it shows.** `NullPointerException` in `UnskippableMuJavaController`
after a new sibling batch is built.

**Cause.** `UnskippableMuJavaController.java:761` reads the father's index map
before the rebuild; the batch size is 1000 (`:58`).

**Fix.** TACO ae2807ed (from comitaco): read the map after the rebuild, and
batches of 100.

**Verified.** The smoke test.

### S5. Fresh checkouts and unusual paths

**How it shows.** "No fix was found", or a run that never finishes, on a fresh
clone; when `java.io.tmpdir` has a directory starting with `a` (as macOS's
`/var/folders/a3/...`); with a space in the repository path; or with
`generated` anywhere in it.

**Cause.** `TacoMain.editTestFileToCompile` (`TacoMain.java:719-722`)
writes the counterexample test into `output/ar/edu/output/junit`, which a
fresh clone does not have; `MuJavaController.java:988` finds the batch's
package at the first `/a` of the whole path; `MuJavaController.java:1039`
starts JML4C with `Runtime.exec(String)`, which splits the command at spaces;
`TacoMain.java:614` and `721` (and `DarwinistController`) replace `generated`
with `output` anywhere in the path.

**Fix.** TACO 29174468 (from comitaco): create the directory, take the package
from the directory before `aOpenJMLInWrap<i>`, start JML4C with an argument
list, and rewrite only below the working directory.

**Verified.** Taken from comitaco ade396bb; here, the re-validation runs from
fresh clones and in the Docker images.

### S6. The counterexample pool fills up, and the run hangs

**How it shows.** After 50 counterexamples a repair hangs forever (a
`NullPointerException` kills `MuJavaController`'s thread after "File name
does not correspond to any stored input! Broken invariant!").

**Cause.** `TacoMain.java:605-606` allocates 50 slots; once they are full,
`DarwinistController` stops storing counterexamples but still asks for
feedback with them.

**Fix (our choice).** TACO 3c677dc0: 10000 slots
(`StrykerStage.MAX_COUNTEREXAMPLES`), feedback only with a counterexample that
is in the pool, and a full pool is logged. *Alternative:* a pool that grows.

**Verified.** With the capacity forced to 1, the two singly-linked-list smoke
benchmarks hung before and find their fixes now; below 50 nothing changes.

### S7. A TACO error during feedback prunes the whole search

**How it shows.** "No fix was found" after one candidate when every feedback
check fails, e.g. in `...Contains3Bug7Dx10Dx20D`, whose counterexample holds
an exception object whose `serialVersionUID` does not fit in Alloy's
integers.

**Cause.** `DarwinistController.java:228-230` treats an exception from TACO
during variabilization as UNSAT and goes on, so every statement looks
unfixable.

**Fix (our choice).** TACO 7f354b7c: stop the feedback at that step, as a SAT
verdict would. *Alternative:* skip the failing step and go on with the next
statement.

**Verified.** That benchmark finds a fix (with S8).

### S8. A counterexample that cannot become a test hangs the run

**How it shows.** A repair that never finishes after "DYNJALLOY ERROR!
Possibly the class does not provide a constructor...".

**Cause.** `DarwinistController.java:527`: `generateJunitFile` is called
outside the `try`, so its exception ends the controller's thread with its work
item unacknowledged.

**Fix.** TACO 6b8653bf (from comitaco): catch it and go on without feedback
for that candidate.

**Verified.** `...Contains3Bug7Dx10Dx20D` finishes.

### S9. A feedback jump applied twice

**How it shows.** Siblings that nothing ruled out are skipped, when the
sibling a feedback jump led to was itself skipped (a duplicate, or not
compilable).

**Cause.** `MuJavaController.java:1192`: the next sibling is computed again
from the skipped one with the same feedback position and jump.

**Fix.** TACO 7d5d3285 (from comitaco): continue from a skipped sibling with a
plain increment.

**Verified.** No occurrence in 17 multi-bug benchmarks (results unchanged).

### S10. The feedback position of a line with no mutations left

**How it shows.** The same candidate queued again indefinitely.

**Cause.** `MuJavaController.java:201`: `indexOf` gives -1 for a line with no
mutations left in that father.

**Fix.** TACO d2620144 (from comitaco): count the father's mutable lines below
the feedback line instead.

**Verified.** Not observed in 18 multi-bug benchmarks (results unchanged).

### S11. Counterexample tests that do not compile

**How it shows.** Every IntroClass benchmark reports "No fix was found"; so
does any subject whose method has a parameter named `c`, `co`, `urls`, ...
Separately, every counterexample test fails to compile ("incompatible types:
CAP#1 cannot be converted to ..."). Both are silent apart from javac's
message.

**Cause.** `TacoMain.editTestFileToCompile` rewrites the test with locals
(`TacoMain.java:772-794`: `urls`, `c`, `co`, ...) that clash with the
parameters it declares; and since 4f232266 it declares the instance as
`<ClassToCheck> instance = null;` (`TacoMain.java:788`) and assigns it
`Constructor<?>.newInstance(...)`, an `Object`.

**Fix.** TACO be5e99b2 (from comitaco): prefix the template's locals with
`stryker`, and log a test that does not compile; c95cdbc5: declare the
instance as `Object` again, as comitaco does (the test reaches it by
reflection).

**Verified.** The IntroClass smoke benchmark (median) finds its fix with 1
TACO call, as on comitaco.

### S12. The counterexample snapshot has no compilation units

**How it shows.** "Could not turn TACO's counterexample into a test:
NullPointerException": the counterexample is lost, and so is the feedback for
that candidate. `...Contains1Bug25D` needs 2 TACO calls instead of comitaco's 3
(a different search).

**Cause.** `DarwinistController.java:601` passes `null` compilation units to
the snapshot (since f9957ba2); and `TacoMain.run` now parses one file at a
time, so the parser holds only the last file's units.

**Fix.** TACO 2ce38a21: `TacoMain` keeps the units of its last run, and
`DarwinistController` passes those of the run that found the counterexample.

**Verified.** `...Contains1Bug25D` makes 3 TACO calls, as on comitaco (the
smoke test).

### S13. Feedback checks translated without the fixed input

**How it shows.** Stryker's feedback prunes nothing.

**Cause.** When `inputToFix` was reused for `checkAndRunSpecIfFaulty`, the
translation for Stryker was turned off: `TacoMain.java:422-424`
(`PrecompiledModules(inputToFix)` commented out since 6c3a52f9) and
`DynalloyStage.java:94-97` ("If Stryker is going to be used this needs to be
uncommented"). And `DynalloyStage.java:122` passes the input to DynAlloy also
in Stryker's checks, where dynalloy4's spec-output index widening (b58e74b)
was not meant to apply (Marcelo confirmed).

**Fix.** TACO d1dfb116: turn both on again when the input is not a
spec-output one (`isCheckAndRunSpecAssertionSupport` false); 5f66fdfe: pass
the input to DynAlloy only when not translating for Stryker.

**Verified.** `...Contains2Bug4Dx10D` prunes 11 mutants, as on comitaco (with
N4); Marcelo's 4 spec-output drivers give the same verdicts, SAT problems and
generated tests with and without 5f66fdfe.

### S14. Classes whose names contain underscores

**How it shows.** No counterexample test for any IntroClass subject
(`introclass_<id>_<nnn>`): `ClassNotFoundException`, and "No fix was found".

**Cause.** `SnapshotBuilder.inferTypeOfExpression` (`SnapshotBuilder.java:1141`)
turns every underscore of a signature into a dot.

**Fix (our choice).** TACO e36d1c74: first look the signature up among the
class under analysis and the classes TACO translates; the guess remains the
fallback. *Alternative:* names that keep the underscores apart (an escape in
TACO's signature names).

**Verified.** The IntroClass smoke benchmark finds its fix with 1 TACO call.

### S15. `PrintStream` objects in counterexamples

**How it shows.** A counterexample that stores `System.out` in a field (TACO's
models have `PrintStream` atoms because of `System.out`) cannot be rebuilt:
`new PrintStream((OutputStream) null)` throws, and the counterexample is lost.

**Cause.** `UnitTestBuilder.java:615` and `1134-1135` call the first public
constructor with default arguments; `SnapshotBuilder.java:1403-1411` builds
an uninitialized instance.

**Fix.** TACO eebe0963 (from comitaco): `System.out` for those objects, in
the test and in the snapshot.

**Verified.** On comitaco, the Java 8 solver picked such a counterexample in
`...Contains1Bug25D`; the smoke test here.

### S16. The JML tools on JDK 8

**How it shows.** On JDK 8, Stryker never finishes: JML4C cannot compile any
mutant batch (330 of 330 in `...Contains1Bug25D`), and JML2 stops type
checking at `java.lang.CharSequence`. The JML tools read Java 7 class files
only.

**Cause.** `JmlParser.java:111` and `MuJavaController.java:1005-1041` give the
JML tools the running JDK's class library.

**Fix (our choice).** TACO a554e17d: a Java 7 `rt.jar` for the JML tools
(`Java7ClassLibrary`; `scripts/fetch-java7-classlib.sh` downloads Zulu 7's and
checks its SHA-256), and Stryker refuses to start without it.
*Alternative:* run Stryker on JDK 7, as comitaco did until 2026.

**Verified.** The smoke test on JDK 8, 21 and 25.

### S17. RAC timeouts need `Thread.stop`

**How it shows.** On Java 20 and later, a mutant that loops makes every later
RAC run time out, and the benchmark reports "No fix was found"; on 27,
`NoSuchMethodError`.

**Cause.** `OpenJMLController.java:221` and `DarwinistController.java:780`
end a timed-out run with `Thread.stop()`.

**Fix (our choice).** TACO a911e102 (from comitaco): cooperative cancellation
through checks that `RacTimeoutInstrumenter` inserts on existing lines (JML4C's
error lines must not move), with `Thread.stop()` as a fallback where it
exists; 869c6f57 handles its absence on 27. *Alternative:* run RAC in a
separate process.

**Verified.** 8314 checks inserted into 1473 classes without moving a line; a
looping variant times out on the same mutants as before; the smoke test on
JDK 8, 21 and 25.

## 4. Building and setting up

### B1. A clean build fails: an import of a test subject

**How it shows.** `ant clean build` fails on every JDK: "package escj.test20B
does not exist" (IntelliJ compiles everything in one pass and does not
notice).

**Cause.** `TacoMain.java:49`: `import escj.test20B.A;`, unused, from a
subject under `tests/`.

**Fix.** TACO 39f34dd6: remove it.

**Verified.** A clean `ant build` on JDK 8.

### B2. Class files from `tests/` shadow the subjects

**How it shows.** The second run of a benchmark ends at once with "No fix was
found" ("Index: 1, Size: 1").

**Cause.** `build.xml:60`: the build copies every non-Java file from `tests/`
into `bin/`, including the `.class` that feedback leaves next to the
variabilized subject; being newer, it is not recompiled.

**Fix.** TACO 7a1bc583 (from comitaco): do not copy `*.class` or `*_temp*`.

**Verified.** The re-validation runs each smoke benchmark twice in the same
clone (the smoke test, then a full report), with the same results.

### B3. A clean build fails outside macOS: source encoding

**How it shows.** "unmappable character for encoding ASCII" in a minimal Linux
or Docker image.

**Cause.** `build.xml:111-121`: the `javac` tasks have no `encoding`, and some
sources are not ASCII.

**Fix.** TACO d393b17c: `encoding="UTF-8"`, as comitaco has.

**Verified.** Clean builds in both Docker images and in Ubuntu 24.04; the
macOS classes are byte for byte the same, on JDK 8, 21 and 25.

### B4. `setupTACO.sh` exits with the wrong status, goes on after a failed clone

**How it shows.** A failed Ant step exits with 2 instead of 5, 6 or 7; a
failed `git clone` goes unnoticed and the script builds in the wrong folder.

**Cause.** `setupTACO.sh:109`: `exit exitCode` (no `$`); lines 123-137 do
not check `git clone`.

**Fix.** TACO 167ca8df.

**Verified.** With local mirrors of the three repositories: a fake failing
Ant gives 5, 6 and 7, a failed clone 2, before anything is built.

### B5. The muJava++ and OpenJava jars

**How it shows.** Stryker's 2018 code, which these fixes bring, was built and
validated with the 2018 muJava++ and OpenJava, not with master's
`lib/mujava++.jar` (2017).

**Cause.** `lib/mujava++.jar`, and its entries in `build.xml`.

**Fix (our choice).** TACO 4edb54ec: `lib/mujava2018.jar` and
`lib/openjava2018.jar`, from comitaco. *Alternative:* build them from
saiema/MuJava and saiema/OJ-with-Java-1.6.

**Verified.** The smoke test.

## 5. dynalloy4

### D1. The solver cannot be chosen; SAT4J is too slow for Stryker

**How it shows.** Every analysis uses SAT4J. Stryker confirms each candidate
with a TACO check that must come out UNSAT, and SAT4J can take very long on
those: in the BinomialHeap smoke benchmark the check of the right fix did not
finish in 15 minutes (the whole repair takes about 30 s with MiniSat 2.2).
The published results were obtained with MiniSat 2.2.

**Cause.** `src/ar/uba/dc/rfm/dynalloy/analyzer/AlloyAnalyzer.java:127`:
`build_A4Options` sets SAT4J (since df5f497, 2024), and `analyzeCommand`
(line 66) ignores the options it is passed.

**Fix (our choice).** dynalloy4 08c2fd8: the JVM property `dynalloy.solver`
(`sat4j`, `minisat`); 40c1beb makes `auto` the default: MiniSat 2.2 if
`alloy4.jar` has it for the platform and it loads, SAT4J otherwise, saying so.
*Alternative:* honour the options passed to `analyzeCommand` and let TACO
choose.

**Verified.** Each value on macOS (arm64 and x86_64 JDK 8, JDK 21) and on
arm64 Linux; the smoke test.

### D2. No MiniSat 2.2 for arm64 (macOS, Linux)

**How it shows.** On a native arm64 JVM (Apple Silicon, arm64 Linux) MiniSat
2.2 does not load; on macOS it needed an x86_64 JVM under Rosetta.

**Cause.** `lib/alloy4.jar`: `x86-mac/libminisat220.jnilib` is x86_64 only,
and there is no `aarch64-linux/libminisat220.so`.

**Fix (our choice).** dynalloy4 f06eb89: a universal macOS library (the
x86_64 slice byte for byte the original, plus arm64); 3b4507b: an arm64 Linux
library. `scripts/build-minisat220.sh` builds both from pinned sources
(MiniSat 2.2.0 and Kodkod's JNI glue) and reproduces the committed jar.
*Alternative:* use Alloy 6's solvers.

**Verified.** 300 random 3-SAT instances: the same answers and models as the
original library; the smoke test on both architectures (same reports).

### D3. Stale solver libraries in the temporary directory

**How it shows.** After a run with one `alloy4.jar`, runs with another one
that share the temporary directory (any IDE run) keep loading the old
libraries: "UnsatisfiedLinkError: kodkod.engine.satlab.MiniSat220.make()J" on
arm64 after an x86_64 run.

**Cause.** `AlloyJNILibraryPath.java:122-125`: Alloy's `Util.copy` never
replaces an existing file.

**Fix.** dynalloy4 0138709: once per JVM, replace every extracted file that
differs from the jar's (renamed over the old one).

**Verified.** With the x86_64 library left in the directory: the
`UnsatisfiedLinkError` before, the fix found now; a second run leaves the
files alone.

### D4. MiniSat 2.2 does not load on Java 17 and later

**How it shows.** `UnsatisfiedLinkError: 'long
kodkod.engine.satlab.MiniSat220.make()'` unless the JVM gets
`-Djava.library.path`.

**Cause.** `AlloyJNILibraryPath.java:57`: the library directory is added to
`ClassLoader.usr_paths` by reflection, which newer JVMs hide; the failure is
ignored.

**Fix.** dynalloy4 eddee24: when that fails, load the library with
`System.load` from the class loader that loads Kodkod.

**Verified.** Temurin 17, 21 (x86_64 and arm64) and 27 load MiniSat 2.2; JDK 8
still takes the old route; the smoke test on JDK 21 and 25.

## 6. Overall

TACO `stryker` 7b560282, jDynAlloy `stryker` 0f920f1 and dynalloy4 `stryker`
3b4507b, from fresh clones, 2026-10-18 (run as TACO f275479a and jDynAlloy
aeb53c4: the same code, before a correction to a comment of
`scripts/smoke-test.sh` and to jDynAlloy's commit message):

- **Smoke test**: passes, with the same full reports as before the new
  naming, on Zulu 8u504 arm64, Oracle 8u261 x86_64, Temurin 21 and 25 arm64,
  and in both Docker images (Linux arm64 and amd64). The four benchmarks give
  comitaco's values (fix, mutation indexes, pruned mutants), except that
  `Contains2Bug4Dx10D` makes 13 TACO calls against comitaco's 14
  (`scripts/smoke-test.sh` explains why). BinomialHeap takes 37 s on JDK 8
  arm64; without N7 it took 290 s there.
- **Marcelo's drivers**: 89 of them, the ones whose calls
  `scripts/convert-method-names.py` can convert (most of his `runAndCheck`
  drivers inherit `getClassToCheck()` and are not converted), and the four
  spec-output ones: 39 pass, 41 fail and 9 time out after 300 s, the same with
  and without M1. All four spec-output drivers pass, with the verdicts they had
  before the new naming, except `llm.iter0.FuncTest`, whose subject changed
  with it.
- **101 converted Stryker drivers** (1 to 3 per folder, 60 s each): 19 fixes
  found, 11 searches exhausted, 60 timeouts, 11 runs without a Stryker
  verdict. None of those 11 is about naming: in 6, TACO's verdict on the
  original method is not the one the driver expects (one of them,
  `pldi.StrykerBinomialHeapInsertBug50x33Test`, is UNSAT in 2.5 s, and also
  UNSAT without N7, or without symmetry breaking at all, after about 250 s);
  in one, TACO finds no bug, as the driver expects; and 4 fail as before the
  new naming, or have no contract. With N1, N2 and N4 only, the same sample
  found 13 fixes; with the masters' naming code, none.
- No "Program not found", missing counterexample test, failed runtime check
  or "The method to check was not found" in any of these runs.

## 7. Found, not fixed

Checked with small classes on the `stryker` branches, on the masters, on this
branch before the new naming (on master 06fb33fb, with jDynAlloy 4691c3e), and
on comitaco (its 2018 jars, JDK 7), with the method names in each one's form.

### F1. Two overloads with the same number of parameters in one analysis

With `f(int)` and `f(Object)` and a method `k()` that calls both, the DynAlloy
has two programs `scratchover_Over_f` of the same arity, and the translation
to Alloy fails ("Name cannot be resolved; possible incorrect
function/predicate call"). Either overload alone, or a method that calls only
one of them, works (the relevancy analysis leaves the other one out).

**A regression in master's line**, older than the new naming: on comitaco
`k()` checks fine. jDynAlloy's first commit, 5b33641 (2022), changed
`VAlloyVisitor`'s unique program name from `<signature>_<program>_<index>` to
the program name alone (`xlator/VAlloyVisitor.java:78` and `100-101`); the
same failure shows before the new naming. N5 does not change it.
The mechanism is dynalloy4's parser, which keeps one program per name and
number of parameters and drops the earlier one silently
(`parser/DynalloyParser.java:201`). When the overloads' parameter types
overlap (`f(A)` and `f(B)`), the translation goes through and both calls run
the same overload, so a method whose specification Java violates is reported
correct (checked on 2026-10-20; comitaco finds the counterexample).
*Options:* the parameter types in the program names (N5's alternative), or
the index back; in dynalloy4, refuse a second program with the same key.

### F2. `assignable` clauses add no frame conditions

With a class of two fields and a method `assignable a`, comitaco's
postcondition has `thiz.b = thiz.b'`; master's, and ours, have nothing about
`b`.

**A regression in master's line**, older than the new naming.
`modifies/ReplaceModifiesModuleVisitor.java:429-431` (unchanged since 2018)
picks the method under analysis with
`methodToCheck.startsWith(<signature>_<program>_)`. That matched comitaco's
`methodToCheck`, `<Class>_<method>_<index>`, but not `<method>(<types>)`,
which TACO 3ae43be3 (2022) introduced, nor the new form. Not seen to matter:
JML's own checker, in TACO, rejects assignments and calls that the clause does
not allow, by field name ("Field "b" is not assignable by method ...").
*Option:* match `<program>(` there.

### F3. `f(null)` with Java arithmetic

With `useJavaArithmetic` and those two overloads, a call `f(null)` is an
"Ambiguous call" for jDynAlloy's binding: `int` values are objects
(`JavaPrimitiveIntegerValue`) there, and `null` fits both. Not checked on
comitaco.

### F4. Drivers

- Most of Marcelo's own drivers still use the earlier names (`"contains_0"`,
  `"absoluteInt(int)"`): "The method to check was not found". With
  `scripts/convert-method-names.py --all`, 133 of their calls convert; the
  rest name methods or classes that no longer exist, or are overloaded.
- `ximena.isEvenTest.test_isEven` expects no counterexample, but TACO finds a
  real one: `isEven(n)` returns `MutRecursiveAux.m(n) == 1`, `m(n)` is
  `n + 1`, so `isEven(2)` is false.
- In the 101-driver sample, 6 Stryker drivers expect a counterexample in the
  original method and TACO finds none (see [Overall](#6-overall)).

The entries below were found by the Javadoc pass (read from the code) and
checked on 2026-10-20 on the `stryker` branches (Zulu 8u504 arm64, MiniSat)
and on comitaco (its 2018 jars, JDK 7), not on the masters, each with a
small class and driver (scratch, not committed); every UNSAT was checked for
vacuity, and an entry says when one was vacuous. **Old** means comitaco is
wrong too; **regression**, comitaco is right. Line numbers are the masters'.

### F5. A postfix `++` or `--` outside a comparison loses its update

`return this.n++;` returns `n` but never increments it, and in
`if (isPos(i++))` the call gets `i` but `i` stays. So `ensures n == \old(n) + 1`
gets a spurious counterexample, and `ensures n == \old(n)`, which Java
violates, is reported correct. `int x = this.n++;` is right. **Old.**
*Cause:* `jml/expression/ESExpressionVisitor.java:1028` puts the update in the
postfix list, which only `ESBlockVisitor.visitExpressionStatement` reads;
`visitIfStatement` (154-169), `visitReturnStatement` (288-301) and the visits
of `assert`, `throw` and JML variable definitions never do. *Options:* append
the postfix statements in those visits as `visitExpressionStatement` does, or
write the update right after the temporary copy.

### F6. Both branches of `c ? a : b` run

The statements of both branches come before the `if` that picks the value:
`b ? bump() : 0` with `b` false still runs `bump()`, and
`next != null ? next.get() : 0` dereferences a null `next` (a spurious
`NullPointerException`). A method whose specification Java violates can be
reported correct. **Old.** *Cause:* `ESExpressionVisitor.java:728` and 731
visit both branches into the statements that precede the `if` at 737.
*Options:* collect each branch's statements apart and put them inside its
branch, or turn the conditional into an `if` statement earlier.

### F7. The right side of an assignment runs before the left

`me().v = g();` calls `g` before `me`, and `a[f()] = g();` calls `g` before
`f`; Java evaluates the left side first (JLS 15.26.1). `a[k] = k++;` writes
`a[k + 1]`. Wrong verdicts both ways: spurious counterexamples, and
specifications that only the wrong order meets reported correct. **Old.**
*Cause:* `ESExpressionVisitor.java:895` visits the right side before the left
(900). *Options:* visit the left side's subexpressions first, or copy the
receiver and the index into temporaries before the right side.

### F8. `Boolean.equals` is inverted

In TACO's built-in model of `java.lang.Boolean`, `equals(obj)` returns false
when `obj` is a `Boolean` and compares the values otherwise, so `a.equals(a)`
is false. Wrong verdicts both ways. **Old.** *Cause:*
`simplejml/builtin/JBoolean.java:168-172`, a type test without the `not` that
`java_lang_Integer.djals` has. `JInteger.java:177-180` has the same test but
is never used: `engine/PrecompiledModules` also adds the parsed
`java_lang_Integer.djals`, later and under the same module id, and jDynAlloy's
linker keeps the later one; there is no `.djals` for `Boolean`. *Options:*
negate the test, or add a `java_lang_Boolean.djals` (and drop the built-in
`JInteger`).

### F9. With `checkNullDereference`, a static call to another class can "throw" a `NullPointerException`

`Util.twice(x)` becomes `if isEmptyOrNull[Util] then throw
NullPointerException else call ...`: the class's signature stands for the
receiver, and it is empty when no `Util` object is in the scope (the analysis
of `Caller` has `exactly 0 Util`). So a correct method gets a spurious
counterexample, and one whose `ensures` Java violates is reported correct (the
"exception" skips the `ensures`). **A regression of master's 955795ac**
(2026-10-16): comitaco writes the call with no receiver and no guard. *Cause:*
`simplejml/ExpressionVisitor.java:546-558` gives static calls a receiver too
(the class signature, or `thiz`), so jDynAlloy's `JProgramCall.isStatic()`
(`ast/JProgramCall.java:109-132`, decided by the shape of the arguments) is
false for them, and `simplejml/NullDerefVisitor.java:208` guards that receiver.
*Options:* mark the call static from the method's information, or never guard
a class-signature receiver.

### F10. `signals_only` in a lightweight specification case

`ensures \result == k; signals_only IllegalArgumentException;` without
`normal_behavior` becomes `(throw' = null) and instanceOf[throw',
IllegalArgumentExceptionLit]`, which never holds, so every method with such a
case gets a counterexample. And a lightweight case written after an
`exceptional_behavior` case becomes `not (throw' = null) implies ...`, so a
method whose normal result is wrong is reported correct. **Old.** *Cause:*
`simplejml/JmlExpressionVisitor.java:2302-2313`, under a flag that is true by
default (`JmlBaseExpressionVisitor.java:170`), cleared at
`JmlExpressionVisitor.java:1453` and set again only at 1613. *Options:*
translate `signals_only` as `throw' != null implies (instanceOf ... or ...)`,
and reset the flag at every specification case.

### F11. Pure-method calls in specifications

`jml/SpecMethodCallRemoverVisitor` replaces calls to pure methods in
specifications by new fields and assumptions, in maps that are never cleared,
and one instance serves every class of a run. So a class visited later gets
the fields and assumptions of the classes before it: with `A`'s invariant
calling `okA()`, `B` gets `assume ... == this.okA()`, which does not compile
(stryker: `NullPointerException`; comitaco: `TacoException`); with an `ok()`
in both classes, `B2`'s methods are reported correct vacuously. With `B2`
visited first, the result is right. Also, breaking an invariant that calls a
pure method is never detected: `c = -1` against `invariant pos()` is reported
correct, because the final state's assumption reuses the field that holds the
call's initial value. **Old.** *Cause:* `SpecMethodCallRemoverVisitor.java:139`
and 538 fill the maps, which are read at 161, 265-291 and 394-422 (410-422 for
the final state); one instance per run (`ASTSimplifierManager.java:79`,
`engine/JmlStage.java:68` and 136). *Options:* clear the maps for each class,
or key the entries by class; a field of its own for the final state's value.

### F12. `normal_behavior` does not forbid exceptions

A method whose `normal_behavior` case Java violates by throwing (a
`NullPointerException`) is reported correct. **Old.** *Cause:*
`JmlExpressionVisitor.java:1417-1422`. *Option:* add `throw' = null` to the
postcondition of a `normal_behavior` case.

### F13. The fixed input matches objects with `equals`

jDynAlloy's formula for an input to fix (Stryker's feedback checks and
`checkAndRunSpecIfFaulty`) keeps the values it meets in a `HashMap`, so two
distinct objects of a class that overrides `equals` are stated to be the same
object (`b = a`): a method returning `a == b ? 1 : 0` with
`ensures \result == 0` is reported correct for two distinct, equal objects,
while for a class without `equals` the counterexample is found. **Old.**
*Cause:* jDynAlloy `ast/JDynAlloyVisitor.java:399`, used at 491 (parameters)
and 592 (values reached through fields); the `==` test at 520 never holds for
two keys of one map. *Option:* an `IdentityHashMap`, with primitive and boxed
values kept out of it.

### F14. Recursion unfolding can lose the recursive program

`RecursionUnfolderVisitor` reassigns `n` in its loop over the programs
reachable from the recursive one, and then rebuilds "the original" from the
last one: a recursive `r(n)` that also calls a helper is replaced by a second
copy of the helper when the `HashSet` yields the helper last, and the analysis
fails with a `NullPointerException` (in the scope inference, or in dynalloy4).
**A regression of jDynAlloy cad278a** (2026-10-16); comitaco is right.
*Cause:* jDynAlloy `xlator/RecursionUnfolderVisitor.java:56-58`, then 88-99.
*Option:* loop with a variable of its own and rebuild the visited program.

### F15. Negative values in a fixed input

With Java arithmetic, a fixed input with a negative value reached through a
field (`thiz.f = -5`) gets a literal module whose fact names
`pred_java_primitive_integer_value_literal_Minus5`, while the literal
predicates are `..._literal_minus_5`: Alloy stops ("The name ... cannot be
found"). A negative literal written in a `requires` is not affected (its
module already exists). **Old.** *Cause:* jDynAlloy
`xlator/JDynAlloyTranslator.java:209-211` (`int`) and 227-229 (`long`); the
names are made at `src/ar/edu/taco/simplejml/builtin/JavaPrimitiveIntegerValue.java:214`
and `JavaPrimitiveLongValue.java:240` there. *Options:* reuse the module made
for the literal, or name the predicate `minus_<n>`.

### F16. The modifies table keeps one entry per program name

`xlator/ModifiesTableBuilder` keys the table by program name, and each program
visited starts its entry anew, so of overloads only the last one visited
counts. With M1, a method that calls `g(0)` and `g()`, of which only `g()`
breaks the invariant `x >= 0`, is reported correct when `g(int)` comes last:
the postcondition does not re-check the invariant (7 of 16 variants of names
and order). **A regression**: comitaco names the overloads apart (`g_0`,
`g_1`); on master, without M1, no callee is looked at. *Cause:* jDynAlloy
`ModifiesTableBuilder.java:72-73`. *Options:* key the table by name and
parameter types, or add to the entry instead of replacing it (which re-checks
more invariants: sound).

### F17. An overloaded method under analysis

When the method under analysis has an overload of another arity, TACO may
check the other one, silently: jDynAlloy writes an assertion
`check_<program>` for each overload, all with the same name, and dynalloy4
keeps one (`util/DynalloySpecBuffer.java:58`); jDynAlloy's `FormulaWrapper`
keeps one pre- and postcondition predicate per name
(`xlator/FormulaWrapper.java:109`); F16's table does the same. With
`set() { x = -1; set(0); }` and `set(int v) {}`, the analysis of `set()` is
UNSAT and checks `set(int)`. In 20 such methods, 9 checked the wrong overload
(6 UNSAT, 3 Alloy errors) and 2 failed. Not checked on comitaco (its drivers
could not pick that overload). *Option:* the parameter types in the program
names, as for F1.

### F18. Crashes seen, not looked into

- `modularReasoning`: `NullPointerException` in `JDynAlloyMutator.visit(JHavoc)`,
  called from `PruneVisitor` (stryker; comitaco for one of three callers).
- A `do`-`while` with a `loop_invariant`: `ClassCastException`, on stryker in
  `BreakRemoverSimplifier.visitJmlLoopStatement`, on comitaco in
  `BlockStatementsVisitor.visitJmlLoopStatement`.

## 8. What else the branches add

Not fixes, but on the same branches:

- TACO: Stryker's scripts (`scripts/build.sh`, `run-benchmark.sh`,
  `smoke-test.sh` and an optional pre-push hook, `docker.sh`,
  `fetch-java7-classlib.sh`, `convert-method-names.py`); the Docker image
  (arm64 and amd64); Stryker on JDK 21 and 25 (71f7adbc, 049147b8); an arm64
  JDK 8 when MiniSat has an arm64 slice, tiered compilation off only under
  Rosetta; IntelliJ's output in `bin/` and a shared run configuration;
  Javadoc for the Stryker classes; the 2018 ROOPS-format BinomialHeap
  variants (`benchmarks/`); `setupTACO.sh` building dynalloy4 and jDynAlloy
  from the branch named like TACO's, and fetching the Java 7 class library;
  `.gitignore` entries for the two jars it builds (371cc4d1).
- dynalloy4: `scripts/build-minisat220.sh`, which rebuilds the MiniSat 2.2
  libraries of `alloy4.jar`.
- The `cleanup` branch of TACO removes stale files (IntelliJ's build output,
  leftovers, Stryker drivers whose classes or methods no longer exist), and
  the `docs` branches of the three repositories have this documentation.
