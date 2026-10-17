# Building

## Requirements

| Requirement | Why |
|---|---|
| **JDK 8** (the default), or **JDK 21** or **25** | Stryker runs on Java 8, 21 and 25; `TacoMain` refuses other versions for Stryker runs (`Java7ClassLibrary.checkForStryker`). Plain TACO is not checked. The build needs JDK 8 for dynalloy4 and jDynAlloy (their `source`/`target` 1.7 is rejected by javac 21 and 25); TACO itself builds with any of the three. |
| A **Java 7 class library** (`lib/java7/rt.jar`) | The JML parser (`lib/jml-release.jar`), the runtime assertion checker JML4C (`lib/stryker/jml4c.jar`) and the JDT parser of Stryker's variabilization read the JDK's class library, and cannot use that of Java 8 or later. `setupTACO.sh` downloads it (`scripts/fetch-java7-classlib.sh`, see [below](#the-java-7-class-library)). Stryker needs it; plain TACO runs without it. |
| **Ant** 1.9 or later, on the `PATH` | `setupTACO.sh` runs `ant` to build dynalloy4 and jDynAlloy; the scripts run `$ANT` or `ant`. Checked with Ant 1.9.16. |
| **dynalloy4 from its `stryker` branch** | MiniSat 2.2 for Apple Silicon and arm64 Linux, and the choice of SAT solver (`-Ddynalloy.solver`). `setupTACO.sh` builds it when TACO is on its `stryker` branch. |
| **x86_64 or arm64**, **macOS or Linux** | The SAT solver Stryker uses, MiniSat 2.2, is a native library in `alloy4.jar` for macOS (universal) and Linux (x86, x86_64, arm64). Windows: see [Windows](#windows). |
| `bash`, `git`, `curl`, `unzip` | Used by `setupTACO.sh` and the scripts. `python3` only for `scripts/convert-method-names.py`. |

Everything else (TACO's dependencies, JML, muJava, JML4C) is in `lib/`.

Checked on macOS 15 (Apple Silicon) with Azul Zulu 8u504 for arm64 and x64
(the latter under Rosetta), Oracle JDK 8u261 (x64), and Eclipse Temurin
21.0.12.1 and 25.0.4.1 for arm64 and x64; and on Linux (Debian 12, in the
[Docker environment](#docker)) on arm64 and amd64 with Zulu 8u504.

## Installing a JDK 8

Azul Zulu 8u504 is the build checked here. You need not make it your default
Java: the scripts find it (see
[How the scripts find the JDK](#how-the-scripts-find-the-jdk)), but
`setupTACO.sh` runs Ant with your `JAVA_HOME`, so point that at it while you run
the setup.

### macOS

Take the build for your Mac, `macosx_aarch64` on Apple Silicon (native) or
`macosx_x64` on an Intel Mac, and put it where macOS keeps your JDKs:

    curl -LO https://cdn.azul.com/zulu/bin/zulu8.96.0.205-ca-jdk8.0.504-macosx_aarch64.tar.gz
    tar -xzf zulu8.96.0.205-ca-jdk8.0.504-macosx_aarch64.tar.gz
    mkdir -p ~/Library/Java/JavaVirtualMachines
    mv zulu8.96.0.205-ca-jdk8.0.504-macosx_aarch64 ~/Library/Java/JavaVirtualMachines/zulu-8.jdk
    export JAVA_HOME=~/Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home

On Apple Silicon an x86_64 JDK 8 works too, under Rosetta, but more slowly; the
scripts then turn off tiered compilation (`-XX:-TieredCompilation`), with which
comitaco measured runs up to twice as slow there.

### Linux

    curl -LO https://cdn.azul.com/zulu/bin/zulu8.96.0.205-ca-jdk8.0.504-linux_x64.tar.gz
    sudo mkdir -p /usr/lib/jvm
    sudo tar -xzf zulu8.96.0.205-ca-jdk8.0.504-linux_x64.tar.gz -C /usr/lib/jvm
    export JAVA_HOME=/usr/lib/jvm/zulu8.96.0.205-ca-jdk8.0.504-linux_x64

On arm64 Linux use `linux_aarch64` instead of `linux_x64`. The scripts look in
`/usr/lib/jvm` by themselves. Ant from the distribution works, e.g. on Ubuntu
24.04 (Ant 1.10.14):

    sudo apt-get install -y ant git curl unzip

## JDK 21 or 25

Stryker also runs on JDK 21 and 25 (Eclipse Temurin 21.0.12.1 and 25.0.4.1
were checked), with the same Java 7 class library:

    curl -L -o temurin-21.tar.gz https://api.adoptium.net/v3/binary/latest/21/ga/mac/aarch64/jdk/hotspot/normal/eclipse
    tar -xzf temurin-21.tar.gz

(`25` instead of `21` for JDK 25; `linux` instead of `mac`, `x64` instead of
`aarch64`, for the other platforms.) The scripts prefer JDK 8; point
`STRYKER_JAVA_HOME` at the newer JDK's home to use it, e.g.
`export STRYKER_JAVA_HOME=$PWD/jdk-21.0.12.1+1/Contents/Home`.

Runs on JDK 21 and 25 need these JVM options, which the scripts, the Ant
`unittest` target and the shared IntelliJ run configuration add:

    --add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.util=ALL-UNNAMED --enable-native-access=ALL-UNNAMED

- `--add-opens`: TACO reads fields of counterexample objects through reflection,
  those of JDK classes too (the `serialVersionUID` of an exception, for
  example), which Java 17 and later only allow in opened packages.
- `--enable-native-access`: on newer JVMs dynalloy4 loads MiniSat 2.2 itself
  with `System.load` (see dynalloy4's `docs/NATIVE-LIBRARIES.md`); this keeps
  JDK 24 and later from warning about it.

On JDK 21 and 25 the build compiles for Java 8 (`release="8"`: javac 20 and
later cannot compile for Java 7); on JDK 8 it compiles for Java 7. The JML
tools accept both. The four smoke-test benchmarks gave the same reports on JDK
21 and 25 as on JDK 8 (one run each, 2026-10-18); other
benchmarks were not compared, and the order of TACO's Alloy declarations, which
decides which counterexample the solver finds, may differ between JDKs.
Other versions (11, 17, 27, ...) are not supported.

## The Java 7 class library

Stryker reads a Java 7 `rt.jar`: the one named by `-Dstryker.classlib=<path>`,
or `lib/java7/rt.jar` under the working directory (the scripts pass
`$STRYKER_CLASSLIB`, by default `lib/java7/rt.jar`). `lib/java7/` is
git-ignored. `setupTACO.sh` runs the script that creates it:

    scripts/fetch-java7-classlib.sh                  # download Azul Zulu 7u352, extract its rt.jar
    scripts/fetch-java7-classlib.sh --from <JDK 7 home>   # or copy it from a JDK 7 you have

The download is the Zulu 7u352 Linux x64 archive (68 MB; the classes in
`rt.jar` are the same on every platform); the script checks the archive's and
the extracted file's SHA-256, and does nothing if `lib/java7/rt.jar` already
exists.

## setupTACO.sh

Run it from inside `TACO/` (or from the folder that contains it):

    ./setupTACO.sh "" -h      # clone over HTTPS
    ./setupTACO.sh            # clone over SSH (needs a GitHub SSH key)

It:

1. clones `mffrias/TACO` if it is not run from (or next to) a TACO checkout;
2. clones `mffrias/dynalloy4` and `mffrias/jDynAlloy` next to TACO, each on
   the branch with the same name as TACO's current branch if it has one (TACO
   `stryker` → `stryker`), and on `master` otherwise; an existing `dynalloy4/`
   or `jDynAlloy/` folder is used as it is, with a warning if it is on another
   branch;
3. builds dynalloy4 (`ant clean build jar`), copies `dist/dynalloy4.jar` into
   `jDynAlloy/lib` and `TACO/lib`, then builds jDynAlloy the same way and copies
   `dist/jdynalloy.jar` into `TACO/lib`;
4. runs `scripts/fetch-java7-classlib.sh`.

It stops at the first step that fails: exit status 2 for a failed `git clone`,
5, 6 or 7 for `ant clean`, `build` or `jar`, 8 if the class library cannot be
fetched, and 1 for a `TACO/` folder without `lib/`.

With `full` as the first argument it also clones muJava++ and OJ, which TACO
does not need (`lib/mujava2018.jar` and `lib/openjava2018.jar` are bundled).
The builds bump the tracked `build.num` of dynalloy4 and jDynAlloy; restore it
with `git checkout build.num` in each.

After changing dynalloy4 or jDynAlloy, rebuild their jars the same way (or run
`setupTACO.sh` again: it reuses the existing checkouts).

## Building TACO

    scripts/build.sh            # the same as scripts/build.sh build

compiles `src/`, `tests/` (the subject programs) and `unittest/` (the drivers)
into `bin/`, copies resources (such as the precompiled `.djals` modules) next to
the classes, and writes `bin/runtime.classpath`, which
`scripts/run-benchmark.sh` uses. The script chooses the JDK like the others
and passes its arguments to Ant:

| Target | What it does |
|---|---|
| `build` (default) | Compile everything into `bin/` |
| `clean` | Delete `bin/` |
| `jar` | `dist/taco.jar`, with every jar in `lib/` merged in and `ar.edu.taco.TacoMain` as main class |
| `unittest` | Every driver under `unittest/` (hundreds of Stryker benchmarks; many hours). Don't. |

Without the script: `ant build` with `JAVA_HOME` set to a JDK 8, 21 or 25. The
build uses javac with `source`/`target` 1.7 and `release="8"` (Ant ignores
`release` on JDK 8, so that build is unchanged; on 21 and 25 it applies). It does
not compile against the Java 7 class library, so it does not catch uses of Java
8 APIs; TACO already uses some (`Constructor.getParameterCount`), so it needs
Java 8 at run time anyway.

`bin/` is also where the subjects' classes are, and Stryker reads them there:
keep IntelliJ's output there too.

## Choosing the SAT solver

dynalloy4's `stryker` branch picks the solver with `-Ddynalloy.solver`:
`auto` (default: MiniSat 2.2 where `alloy4.jar` has it and it loads, else
SAT4J, with a message), `minisat` or `sat4j`. The scripts pass
`$STRYKER_SOLVER`, by default `minisat`, and refuse a `lib/dynalloy4.jar` that
cannot select it. MiniSat 2.2 is much faster on larger problems, and
Stryker's results depend on which counterexamples the solver finds, so the
smoke test always uses it. Details: dynalloy4's `docs/SOLVERS.md`.

## How the scripts find the JDK

`scripts/common.sh` uses, in this order:

1. `$STRYKER_JAVA_HOME`, which must then be a usable JDK 8, 21 or 25;
2. `$JAVA_HOME`, if it is a usable JDK 8;
3. the first JDK 8, else the first 21, else the first 25, among `$JAVA_HOME`,
   `/Library/Java/JavaVirtualMachines/*/Contents/Home`,
   `~/Library/Java/JavaVirtualMachines/*/Contents/Home`, `/usr/lib/jvm/*`,
   `/opt/*jdk*` and `/opt/*java*`.

"Usable": `bin/java` and `bin/javac` exist, `java -version` says 1.8, 21 or 25,
and an arm64 JDK is only accepted if `lib/dynalloy4.jar` bundles MiniSat 2.2
for arm64 on that system (macOS or Linux), as the one from dynalloy4's
`stryker` branch does. So JDK 8 stays the default even if `JAVA_HOME` is a
newer JDK.

## IntelliJ IDEA

The repository has Marcelo's IntelliJ project (`.idea/`, `TACO.iml`):

- three modules: `TACO`, and `../dynalloy4/dynalloy4.iml` and
  `../jDynAlloy/jDynAlloy.iml`, the checkouts `setupTACO.sh` puts next to TACO.
  IntelliJ compiles dynalloy4 and jDynAlloy from those sources, so keep them on
  the right branches (`stryker` in both); the jars in
  `lib/` are not used there;
- `TACO.iml`: source folders `src`, `tests`, `unittest` (test) and `generated`
  (test), output `bin/`, `lib/java7` excluded;
- libraries: the jars of `lib/` (in one library named `antlr-4.3-complete1`,
  which also has `lib/stryker/` as a jar directory, so JML4C is on the class
  path; two smoke-test benchmarks gave the same results that way), `alloy4` from `../dynalloy4/lib/alloy4.jar`,
  `log4j` and `antlr` from `../jDynAlloy/lib/`, `jml-release`, and `mjc-1_3_2`,
  which points at a `lib/mjc-1_3_2.jar` that does not exist;
- the SDK is named `corretto-1.8`, language level 8;
- a shared run configuration, *Stryker: SinglyLinkedList contains, 1 bug*,
  with `-Xmx4g -Xss8m -Ddynalloy.solver=minisat`, the working directory set to
  the repository root, no class-path shortening, and `JDK_JAVA_OPTIONS` set to
  the [JDK 21 and 25 options](#jdk-21-or-25) (only Java 9 and later read it).

To use it: run `setupTACO.sh`, open the `TACO` folder, set the project SDK to
your JDK 8, 21 or 25 (*File > Project Structure > Project*), and build. For
drivers you run yourself, use the same VM options (add `-XX:-TieredCompilation`
for an x86_64 JDK under Rosetta), the repository root as working directory
(TACO and Stryker resolve `config/`, `tests/`, `bin/` and `lib/` against it)
and no class-path shortening (Stryker passes the JVM's class path on to JML4C).
These settings were checked outside the IDE, by running the same class path
and options from the command line; the project itself was not opened in
IntelliJ for these docs.

Eclipse is not supported: its project files are removed on the `cleanup`
branch, and IntelliJ is the IDE.

## Docker

An optional Linux environment (`docker/Dockerfile`: Debian 12, Azul Zulu 8u504,
the Java 7 class library and Ant 1.9.16, all checksum-verified), built for the
Docker host's architecture, `linux/amd64` or `linux/arm64` (native on Apple
Silicon). Your working copy is mounted at `/work`, so builds made inside and
outside are interchangeable:

    scripts/docker.sh scripts/smoke-test.sh --quick
    scripts/docker.sh scripts/run-benchmark.sh --reset icse.singlylinkedlist.set1.StrykerSinglyLinkedListContains1Bug25DTest
    scripts/docker.sh                       # a shell inside it

The first call builds the image (`stryker-jdk8-arm64` or `stryker-jdk8-amd64`);
`--rebuild` rebuilds it, and `STRYKER_DOCKER_PLATFORM=linux/amd64` picks the
other architecture (emulated). Commands run as your user. `lib/dynalloy4.jar`
and `lib/jdynalloy.jar` come from your working copy, so run `setupTACO.sh`
first (outside Docker). Runs use a 4 GB Java heap, so give Docker more memory
than that. Both images passed the smoke test on Apple Silicon (2026-10-18).

## Windows

Not supported natively: `alloy4.jar` has no MiniSat 2.2 for Windows (and no
64-bit Windows solvers at all), and the scripts are bash. WSL2 is Linux, so the
Linux instructions should apply there (not tried); keep the clone in the Linux
file system, since Stryker writes thousands of small files per run. Docker
Desktop with the WSL2 backend is the other option (not tried).

## Troubleshooting

| Symptom | Cause and fix |
|---|---|
| `error: a JDK 8 (or 21 or 25) is required but none was found.` | Install a JDK 8, or point `STRYKER_JAVA_HOME` at yours. |
| `STRYKER_JAVA_HOME=... is not a usable JDK 8, 21 or 25` | Another version, a JRE without `javac`, or an arm64 JDK with a `lib/dynalloy4.jar` that has no MiniSat 2.2 for arm64 (build dynalloy4 from its `stryker` branch). |
| `Stryker needs a Java 7 class library for the JML tools, and .../lib/java7/rt.jar does not exist` | Run `scripts/fetch-java7-classlib.sh`. Runs started from an IDE look for it under their working directory, which must be the repository root. |
| `lib/dynalloy4.jar cannot select MiniSat (-Ddynalloy.solver)` | `lib/dynalloy4.jar` was built from dynalloy4's master. Build it from `stryker` (`setupTACO.sh` does), or use `STRYKER_SOLVER=sat4j`. |
| `IllegalStateException: Stryker runs on Java 8, 21 or 25, but this is Java ...` | The run used another JVM, e.g. an IDE's default. |
| `Source option 7 is no longer supported` while `setupTACO.sh` builds dynalloy4 or jDynAlloy | `JAVA_HOME` is JDK 20 or later. Set it to a JDK 8. |
| `IllegalStateException: dynalloy.solver=minisat, but alloy4.jar has no MiniSat 2.2 for this platform (...)`, or, without the scripts, `DynAlloy: solving with SAT4J, because ...` | No usable MiniSat 2.2 for this JVM's platform: a JDK of another architecture, or dynalloy4 built from master on arm64. |
| `warning: tests/ has local changes` | A previous run wrote its fix into `tests/`. Use `--reset`. |
