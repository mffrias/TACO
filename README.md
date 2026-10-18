# TACO
### Translation of Annotated COde

TACO checks Java methods against their [JML](https://www.cs.ucf.edu/~leavens/JML/index.shtml)
specifications within bounds: a bounded number of objects of each class, of
loop iterations and recursive calls, and a bounded integer bit width. It
translates the annotated code into a [DynAlloy](https://doi.org/10.1007/978-3-540-45236-2_37)
model, then into Alloy, and lets a SAT solver look for an execution that
violates the contract. If there is one, TACO reports it as a counterexample and
can turn it into a JUnit test. TACO is run from the JUnit test classes under
`unittest/` (called drivers here, to tell them apart from the programs under
`tests/`, which are what TACO checks).

This branch, `stryker`, adds **Stryker**, which repairs a faulty method: it
mutates the statements you mark as suspicious, discards mutants that fail on
the counterexamples found so far (runtime assertion checking with JML4C), asks
TACO to verify the survivors, and uses TACO's verdicts on partially
"variabilized" programs to prune the search. When TACO verifies a candidate
within the bounds, Stryker reports it as a fix.

## The three repositories

    Java + JML ──TACO──▶ JDynAlloy ──jDynAlloy──▶ DynAlloy ──dynalloy4──▶ Alloy ──Alloy Analyzer──▶ SAT

| Repository | What it does |
|---|---|
| [mffrias/TACO](https://github.com/mffrias/TACO) (this one) | Parses Java + JML (JML2/MultiJava), simplifies it and translates it into JDynAlloy modules, runs the whole pipeline, turns counterexamples into JUnit tests; Stryker (on this branch). |
| [mffrias/jDynAlloy](https://github.com/mffrias/jDynAlloy) | Translates JDynAlloy, an object-oriented layer over DynAlloy, into DynAlloy: bindings, relevancy analysis, modifies clauses, recursion unfolding, and the translation itself. |
| [mffrias/dynalloy4](https://github.com/mffrias/dynalloy4) | Translates DynAlloy into Alloy (unrolling loops), and runs the Alloy Analyzer, which solves with MiniSat 2.2 or SAT4J. |

TACO uses the other two as jars, `lib/jdynalloy.jar` and `lib/dynalloy4.jar`,
which `setupTACO.sh` builds from their repositories. They are not in git. This
branch needs the `stryker` branches of both: dynalloy4's chooses the SAT solver
and has MiniSat 2.2 for Apple Silicon and arm64 Linux, and jDynAlloy's has
fixes for the new method naming and for invariants broken through callees.
[docs/FIXES-FOR-MASTER.md](docs/FIXES-FOR-MASTER.md) describes every fix on
the `stryker` branches of the three repositories.

## Quick start

macOS or Linux with git, curl, Ant (1.9 or later) and a JDK 8 (see
[docs/BUILDING.md](docs/BUILDING.md), also for JDK 21 and 25, Docker and
IntelliJ):

    git clone https://github.com/mffrias/TACO.git
    cd TACO
    git checkout stryker
    export JAVA_HOME=/path/to/your/jdk8
    ./setupTACO.sh "" -h
    scripts/build.sh
    scripts/smoke-test.sh --quick

`setupTACO.sh` clones dynalloy4 and jDynAlloy next to `TACO/`, builds them
(each from its branch with the same name as TACO's, `stryker`), copies the
jars into `lib/`, and downloads the Java 7 class library that Stryker's JML
tools read (`lib/java7/rt.jar`, 61 MB). `"" -h` makes it clone over HTTPS;
without arguments it uses SSH. `scripts/smoke-test.sh --quick` runs two repair
benchmarks and checks that they give the expected results.

## What a run looks like

TACO runs from drivers (there is no usable command line). A driver names the
class and method, sets configuration keys, and calls `check(...)`:

    scripts/run-benchmark.sh --reset icse.singlylinkedlist.set1.StrykerSinglyLinkedListContains1Bug25DTest

runs one Stryker benchmark and ends with a `STRYKER RESULT` block (fix found
or not, the repaired class in `StrykerLastFixFound.java`, statistics in
`StrykerLastReports.txt`). See [docs/RUNNING.md](docs/RUNNING.md).

## Documentation

| Page | Contents |
|---|---|
| [docs/BUILDING.md](docs/BUILDING.md) | Requirements, `setupTACO.sh`, JDK 8/21/25, the Java 7 class library, the SAT solver, building, IntelliJ, Docker, Windows |
| [docs/RUNNING.md](docs/RUNNING.md) | Drivers, running TACO and Stryker, the scripts, reading the results, the smoke test |
| [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) | The TACO pipeline and where Stryker hooks in; how a repair run works |
| [docs/METHOD-NAMING.md](docs/METHOD-NAMING.md) | How drivers name the method to check, and the status of the new naming |
| [docs/SPEC-OUTPUT.md](docs/SPEC-OUTPUT.md) | `checkAndRunSpecIfFaulty`: computing the output the specification allows; the fixed input (`inputToFix`) |
| [docs/KNOWN-LIMITATIONS.md](docs/KNOWN-LIMITATIONS.md) | What does not work, or works in surprising ways |
| [docs/OPEN-QUESTIONS.md](docs/OPEN-QUESTIONS.md) | Questions for the maintainer about intent |
| [docs/FIXES-FOR-MASTER.md](docs/FIXES-FOR-MASTER.md) | Every fix on the `stryker` branches of TACO, jDynAlloy and dynalloy4: the bug, its cause in master, the commit, how it was checked |
| [AGENTS.md](AGENTS.md) | Notes for coding agents (and people) changing this repository |

## Branches

- `master`: Marcelo Frias's line of development (TACO without Stryker).
- `stryker`: Stryker, ported from comitaco, Luciano Zemin's repository
  ([github.com/zeminlu/comitaco](https://github.com/zeminlu/comitaco),
  currently private; access on request), on top of master, with JDK 8/21/25
  support, scripts, a smoke test, Docker and IntelliJ settings.
- `cleanup`: removes stale files (IntelliJ's build output, leftovers, stale
  Stryker drivers); independent of `stryker`.
- `docs`: this documentation, on top of `stryker`.

## References / how to cite

DynAlloy:

- M. F. Frias, C. G. López Pombo, G. A. Baum, N. M. Aguirre, T. Maibaum.
  “Taking Alloy to the Movies.” LNCS, 2003, pp. 678–697.
  DOI [10.1007/978-3-540-45236-2_37](https://doi.org/10.1007/978-3-540-45236-2_37).
- M. F. Frias, J. P. Galeotti, C. G. López Pombo, N. M. Aguirre. “DynAlloy:
  Upgrading Alloy with Actions.” ICSE 2005, pp. 442–451.
  DOI [10.1145/1062455.1062535](https://doi.org/10.1145/1062455.1062535).

TACO:

- J. P. Galeotti, N. Rosner, C. G. López Pombo, M. F. Frias. “Analysis of
  Invariants for Efficient Bounded Verification.” ISSTA 2010, pp. 25–36.
  DOI [10.1145/1831708.1831712](https://doi.org/10.1145/1831708.1831712).
- J. P. Galeotti, N. Rosner, C. G. López Pombo, M. F. Frias. “TACO: Efficient
  SAT-Based Bounded Verification Using Symmetry Breaking and Tight Bounds.”
  IEEE TSE 39(9):1283–1307, 2013.
  DOI [10.1109/TSE.2013.15](https://doi.org/10.1109/TSE.2013.15).
- J. P. Galeotti. Software Verification using Alloy. PhD thesis, Universidad de
  Buenos Aires, 2010.

Built on TACO:

- Stryker: L. Zemin, S. Gutiérrez Brida, S. Bermúdez, S. Perez De Rosso,
  N. Aguirre, A. Mili, A. Jaoua, M. F. Frias. “Stryker: Scaling
  Specification-Based Program Repair by Pruning Infeasible Mutants with SAT.”
  [arXiv:1910.14011](https://arxiv.org/abs/1910.14011), 2019.
- L. Zemin. Using Specifications to Overcome the Limitations of Test-Based
  Automated Program Repair. PhD thesis, ITBA, 2026.
- MuchoTACO, parallel TACO, not part of this repository yet: N. Rosner,
  J. P. Galeotti, S. Bermúdez, G. Marucci Blas, S. Perez De Rosso, L. Pizzagalli,
  L. Zemin, M. F. Frias. “Parallel Bounded Analysis in Code with Rich Invariants
  by Refinement of Field Bounds.” ISSTA 2013, pp. 23–33.
  DOI [10.1145/2483760.2483770](https://doi.org/10.1145/2483760.2483770).

## License

GNU GPL: the sources say version 2 or later in their headers, and `COPYING`
is version 3. The licenses of the bundled third-party code are in `LICENSES/`.
