# How drivers name the method to check

A driver names the method TACO checks in the second argument of `check`:

```java
check(GENERIC_PROPERTIES, "icse_singlylinkedlist_set1_SinglyLinkedListContains1Bug25D_contains(java_lang_Object)", true);
```

This naming is TACO master's since 955795ac and jDynAlloy master's since
cad278a (Marcelo Frias, 2026-10-16). On this branch it runs end to end, with
fixes that are not in the masters yet, here and in jDynAlloy's `stryker`
branch: see [Status](#status).

## The form

    <package>_<Class>_<method>(<type>, <type>, ...)

- `<package>_<Class>` is the class under analysis, `getClassToCheck()` of the
  driver, with its dots as underscores (`icse.singlylinkedlist.set1.SinglyLinkedListContains1Bug25D`);
- `<method>` is the method's name; for a constructor, the class's simple name
  (`ximena_MutRecursive_MutRecursive()`);
- the types are the parameters' declared types as the JML parser prints them,
  dots as underscores: primitive types as they are (`int`, `int[]`),
  `java_lang_Object`, and a nested class as `pkg_Outer_Inner`. Static methods
  list their parameters like any other (TACO adds the receiver itself).

TACO removes the spaces and turns any remaining dots into underscores
(`TacoConfigurator.getMethodToCheck`), and when it compares the types with the
declaration it turns the underscores back into dots, so **a type whose name
contains an underscore cannot be named this way**.

Inside TACO, the program of each method is named `<declaring class>_<method>`
(`MethodDeclarationSolver`), and the Alloy assertion
`check_<classToCheck>_<method>`. A call is named `<receiver type>_<method>`
when the call is static or its receiver is one of the classes TACO translates
(`classToCheck` and the relevant classes); instance calls into the built-in
modules (TACO's models of JDK classes, such as `java_io_PrintStream`) keep the
bare method name, which is what those modules declare (`ExpressionVisitor`).
Overloads share their program name. jDynAlloy's bindings tell them apart by
their parameter types, and DynAlloy by their number of parameters, so two
overloads with the same number of parameters cannot both be used in one
analysis ([KNOWN-LIMITATIONS.md](KNOWN-LIMITATIONS.md#the-new-method-naming)).

Stryker reads and builds these names in one class,
`src/ar/edu/taco/stryker/MethodToCheck.java`: the plain method name (for
mutation and for the counterexample tests), the parameter types, and new names
when variabilization adds parameters (`MethodToCheck.of`). A change in how TACO
names methods only has to be made there.

## History

| Form | Example | Since |
|---|---|---|
| overload index | `"contains_0"` | the original drivers (before 2022) |
| method and types, the class added by TACO | `"contains(java.lang.Object)"` | 3ae43be3 (2022-12-28) |
| class, method and types | `"icse_..._SinglyLinkedListContains1Bug25D_contains(java_lang_Object)"` | 955795ac (TACO) and cad278a (jDynAlloy), 2026-10-16 |

## Converting drivers

`scripts/convert-method-names.py` rewrites the `check(...)` calls of drivers
to the new form. It reads the method's declaration in the subject
(`tests/<package>/<Class>.java`) to get the parameter types, and changes only
the string literal:

    scripts/convert-method-names.py --dry-run unittest/icse/singlylinkedlist/set1/StrykerSinglyLinkedListContains1Bug25DTest.java
    scripts/convert-method-names.py              # every Stryker driver
    scripts/convert-method-names.py --all        # every driver

A call is left alone, and reported, when the class or method is not found,
the method is overloaded, or a type cannot be resolved. On this branch it
converted 906 calls in 881 Stryker drivers (7510b10f): 880 `name_N` calls and 26
that listed only the types. Marcelo had converted one of them the same way
(`roops.core.objects.BinomialHeapTest`, e5760d76). 18 calls in 17 drivers were
not converted, because their method or class no longer exists; the `cleanup`
branch removes those drivers and methods. Marcelo's own (non-Stryker) drivers were left as they
were; most still use the earlier forms.

## Status

On 2026-10-18, with TACO `stryker` (7b560282, on master e5760d76), jDynAlloy
`stryker` (0f920f1, on master e8c6b06) and dynalloy4 `stryker` (3b4507b):

- the smoke test (`scripts/smoke-test.sh`) passes, and gives the same full
  reports as before the new naming, on JDK 8 (arm64 and x86_64), 21 and 25,
  and in both Docker images;
- Marcelo's spec-output drivers pass with their names converted
  ([SPEC-OUTPUT.md](SPEC-OUTPUT.md));
- on a sample of 101 converted Stryker drivers (1 to 3 per folder, 60 s
  each, four at a time), Stryker found 19 fixes; 11 searches ended without a
  fix and 60 timed out. 11 runs ended without a Stryker verdict, none for
  naming reasons: in 6, TACO's verdict on the original method is not the one
  the driver expects (5 the same with the masters' code; the sixth gives the
  same verdict without item 7, after 254 s instead of 2.5 s); in one TACO
  finds no bug, as the driver expects; and 4 fail as they did before the new
  naming, or have no contract. With the masters' TACO and jDynAlloy code
  (none of the fixes below) the same sample gave no fix.

The whole check, and every fix, are in
[FIXES-FOR-MASTER.md](FIXES-FOR-MASTER.md#6-overall).

What the naming change needed besides itself, numbered as reported to the
maintainer:

| # | Problem | Fixed in |
|---|---|---|
| 1 | The counterexample test was written under one name and looked up under another (`UnitTestBuilder.getOutputClassName`): Stryker never got a counterexample, and `checkAndRunSpecIfFaulty` failed | TACO `stryker` f44f3369 |
| 2 | The generated test called the method by its qualified name (`getAccessibleMethod`), so every runtime assertion check failed | TACO `stryker` a7308702 |
| 3 | Instance calls kept the bare name, which jDynAlloy could not bind | TACO master e5760d76 |
| 3b | Since then calls into the built-in modules got the prefix too: `System.out.println(...)` gave `Program not found` | TACO `stryker` 507ecc4b |
| 4 | With a fixed input no program matched (`getFullyQualifiedMethodName()` added the class again), so the input was never applied and Stryker's feedback pruned nothing | jDynAlloy `stryker` 9bb5b49 |
| 5 | Overloads share their name: the recursion unfolder and the scope inference took one for the other (`MutRecursive()` calling `new MutRecursive(0)`: a `NullPointerException` in DynAlloy, then a `StackOverflowError`) | jDynAlloy `stryker` d2b09e9, TACO `stryker` 7b560282 |
| 6 | A method under analysis that is not recursive was unfolded as if it were, and lost its program under `inferScope` | jDynAlloy master e8c6b06 |
| 7 | Symmetry breaking (`useJavaSBP`) did not find the method under analysis, so it added nothing, and UNSAT checks were much slower (the BinomialHeap smoke benchmark's last check: 267 s instead of 22 s) | TACO `stryker` 779cf0bc |
| 8 | TACO's scope inference did not get the method's specification (`DynalloyToAlloyManager`; no effect seen) | TACO `stryker` bdb4cdc6 |

3b and 5 are choices about names: calls into the built-in modules keep their
earlier names, and overloads are told apart by their parameters. The
alternatives, prefixing the built-in modules' programs too and putting the
parameter types in the program names, are with the maintainer
([OPEN-QUESTIONS.md](OPEN-QUESTIONS.md#method-naming)).

Still not working:

- **Drivers in the earlier forms** (`"absoluteInt(int)"`, `"contains_0"`):
  `The method to check was not found`. Most of Marcelo's own drivers are
  still in those forms.
- **Two overloads with the same number of parameters in one analysis**, a
  regression of jDynAlloy 5b33641 (2022), older than the new naming
  ([KNOWN-LIMITATIONS.md](KNOWN-LIMITATIONS.md#the-new-method-naming)).

All the fixes, those for the naming included, are described one by one in
[FIXES-FOR-MASTER.md](FIXES-FOR-MASTER.md).

`ximena.isEvenTest.test_isEven` fails, but correctly: `isEven(n)` returns
`MutRecursiveAux.m(n) == 1`, and `m(n)` is `n + 1`, so `isEven(2)` is false;
TACO's counterexample is n = 2.
