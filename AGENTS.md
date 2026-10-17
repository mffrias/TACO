# AGENTS.md

Notes for coding agents (and people in a hurry) working on this repository:
TACO, a bounded verifier for JML-annotated Java, and, on the `stryker` branch,
Stryker, which repairs faulty methods with it. Human-oriented docs are in
[README.md](README.md) and [docs/](docs/).

## The three repositories

    Java + JML ──TACO──▶ JDynAlloy ──jDynAlloy──▶ DynAlloy ──dynalloy4──▶ Alloy ──▶ SAT

[mffrias/jDynAlloy](https://github.com/mffrias/jDynAlloy) and
[mffrias/dynalloy4](https://github.com/mffrias/dynalloy4) are used as
`lib/jdynalloy.jar` and `lib/dynalloy4.jar`, built by `setupTACO.sh` from
checkouts next to this one (`../jDynAlloy`, `../dynalloy4`). They are not in git:
never commit them. This branch needs the `stryker` branches of both
(`setupTACO.sh` takes the branch named like TACO's). A change in
either of them means rebuilding its jar (dynalloy4 first, then jDynAlloy, which
bundles it) and copying it into `lib/`.

## Environment (hard constraints)

- **JDK 8** (default), **21** or **25**. Not 7, 11, 17 or 27: Stryker runs refuse
  other versions (`Java7ClassLibrary.checkForStryker`). dynalloy4 and
  jDynAlloy build only on JDK 8.
- **A Java 7 class library** at `lib/java7/rt.jar` (git-ignored; `setupTACO.sh` or
  `scripts/fetch-java7-classlib.sh` creates it): the JML parser, JML4C and the
  variabilization's JDT parser cannot use the class library of Java 8 or later.
  Stryker fails without it; plain TACO runs.
- On JDK 21 and 25, runs need `--add-opens=java.base/java.lang=ALL-UNNAMED
  --add-opens=java.base/java.util=ALL-UNNAMED --enable-native-access=ALL-UNNAMED`
  (`java21plus_opts` in `scripts/common.sh`; the IntelliJ run configuration sets
  `JDK_JAVA_OPTIONS`).
- **x86_64 or arm64, macOS or Linux** (MiniSat 2.2 through JNI, bundled in
  dynalloy4's `alloy4.jar`). Windows only through WSL2 or Docker (untested).
- **Working directory = repository root**: `config/`, `tests/`, `bin/` and
  `lib/` are resolved against it.
- Ant 1.9 or later (`$ANT` or `ant` on the `PATH`). The scripts find the JDK
  (JDK 8 first, then 21, then 25), or use `STRYKER_JAVA_HOME`. If Docker is there
  instead of a JDK, prefix any command with `scripts/docker.sh`.

## Commands

TACO is run from the JUnit test classes under `unittest/`, called drivers here
(to tell them apart from the programs under `tests/`, which TACO checks).

```bash
./setupTACO.sh "" -h                              # once: clone and build dynalloy4, jDynAlloy, fetch the Java 7 rt.jar
scripts/build.sh                                  # compile src/, tests/, unittest/ into bin/
scripts/run-benchmark.sh --reset <DriverClass>    # one Stryker benchmark
scripts/smoke-test.sh                             # after any change to TACO or Stryker (about a minute)
scripts/smoke-test.sh --quick                     # about 20 s
```

`run-benchmark.sh` exit status: `0` fix, `1` no fix, `2` usage or environment
error, `3` build failed, `4` no Stryker verdict, `124` timeout. Read the
`STRYKER RESULT` block or the exit status, **not** JUnit's `OK`: drivers pass
whether or not a fix is found.

The smoke test checks that four benchmarks still give exactly the expected
results (fix, which fix, TACO calls, pruned mutants). If a change is meant to
alter the search, update `EXPECTED` in `scripts/smoke-test.sh` and say so in
the commit message.

Do **not** run `ant unittest` / `scripts/build.sh unittest`: it runs every
driver, hundreds of Stryker benchmarks included (many hours).

## Things that will bite you

- A successful run **overwrites the subject file under `tests/`** with the fix.
  Never commit that. Use `--reset` or `git checkout -- tests`.
- **One Stryker run per JVM and one run at a time per checkout**: controllers
  are singletons with static state, and runs share `tests/`, `output/`,
  `generated/` and the `StrykerLast*` files.
- `tests/` holds the **subject programs**, not tests; the JUnit drivers are in
  `unittest/`. `benchmarks/` is not compiled.
- Drivers name the method as `<package>_<Class>_<method>(<types>)`
  ([docs/METHOD-NAMING.md](docs/METHOD-NAMING.md)). Stryker builds and parses
  these names only in `src/ar/edu/taco/stryker/MethodToCheck.java`. Inside
  TACO, overloads share their program name (DynAlloy tells them apart only by
  their number of parameters), and instance calls into the built-in modules
  keep the bare method name.
- Mutable statements are the ones marked `//mutGenLimit N` (N > 0).
- Whatever rewrites the JML4C batch files (`adaptSiblingsFileToJML4C`,
  `RacTimeoutInstrumenter`) must not add or remove lines: `MuJavaController`
  maps JML4C's error lines back to mutants by line number.
- `OpenJMLController` does runtime assertion checking with **JML4C**, not
  OpenJML (historical name); `DarwinistController` is TACO validation plus
  feedback.
- Results are not deterministic for every benchmark: treat a "no fix" as "not
  found in this run". See [docs/KNOWN-LIMITATIONS.md](docs/KNOWN-LIMITATIONS.md)
  before "fixing" behaviour that looks wrong: it may change experimental
  results.
- `TacoMain.isCheckAndRunSpecAssertionSupport` is static and never reset: after
  a `checkAndRunSpecIfFaulty` every TACO run in that JVM is a spec-output run
  ([docs/SPEC-OUTPUT.md](docs/SPEC-OUTPUT.md)).
- IntelliJ compiles dynalloy4 and jDynAlloy from `../dynalloy4` and
  `../jDynAlloy`, not from `lib/`; keep those checkouts on the right branches.

## Code map

- `src/ar/edu/taco/TacoMain.java`: `run`, the pipeline, then Stryker when
  `attemptToCorrectBug=true` and TACO finds a bug.
- `src/ar/edu/taco/engine/`: the stages (`JmlStage` … `AlloyStage`,
  `SnapshotStage`, `JUnitStage`, `StrykerStage`).
- `src/ar/edu/taco/stryker/`: Stryker; `api/impl/` has the controllers
  (`UnskippableMuJavaController` → `MuJavaController` → `OpenJMLController` →
  `DarwinistController`), `WeedQueue`, variabilization (`VariablizationData`)
  and the message types (`input/`).
- `src/ar/edu/taco/Java7ClassLibrary.java`: the Java 7 class library and the
  version check.
- `unittest/ar/edu/taco/regresion/RegresionTestBase.java`: what drivers call
  (`check`, `checkAndRunSpecIfFaulty`, the `setConfigKey...` methods).
- How it fits together: [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md).

## Conventions

- Java 7 syntax: no lambdas or other Java 8 language features (the build
  compiles with `source` 1.7). The build does not check for Java 8 APIs.
- Keep changes behaviour-preserving unless asked otherwise; when a change
  alters the search, say so and re-run the smoke test, comparing TACO calls and
  pruning.
- Comments and docs in English; existing Spanish comments stay.
- Open points about intent: [docs/OPEN-QUESTIONS.md](docs/OPEN-QUESTIONS.md).
  Ask rather than guess.
