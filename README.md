# TACO - Final Threading
### Translation of Annotated Code

[TACO](https://github.com/mffrias/TACO) is a tool that translates a **Java** program annotated with [JML](https://www.cs.ucf.edu/~leavens/JML/index.shtml) specifications into a [DynAlloy](https://doi.org/10.1007/978-3-540-45236-2_37) model. Properties described by the JML annotations can then be verified using a SAT Solver.

## Requirements

As for the moment, TACO requires [Java 7](https://www.oracle.com/java/technologies/javase/javase7-archive-downloads.html) and [Apache Ant 1.9.16](https://ant.apache.org/bindownload.cgi).
> [!TIP]
> Apache Ant 1.9 can also be installed using [Homebrew](https://brew.sh). Ensure that Ant 1.9 is linked before executing `./setupTACO.sh`.

TACO depends on [jdynalloy](https://github.com/mffrias/jDynAlloy) and [muJava++](https://github.com/saiema/MuJava). These dependencies and their dependencies are all downloaded by the provided script `setupTACO.sh`.

## Installation

Download this repository and execute `./setupTACO.sh`^. This script will download the following repositories:

 * This one if this script was downloaded elsewhere or it can't find TACO's `build.xml` file.
 * JDynAlloy.
 * [dynalloy4](https://github.com/mffrias/dynalloy4) (a dependency for JDynAlloy).
 * muJava++^^ (only if the script is run with the `full` argument).
 * [OJ](https://github.com/saiema/OJ-with-Java-1.6) (a dependency of muJava++, only if the script is run with the `full` argument).
 
After downloading all repositories, the script will compile **dynalloy4**, use the compiled jar file to compile **JDynAlloy**, and it will copy both compiled jars into **TACO**'s `lib` folder.

_(^) You may need to give the script execution permissions via executing `chmod +x setupTACO.sh`._

_(^^) TACO already has a `mujava++.jar` included, unless needed there is no need to download muJava++ and OJ._

## Importing TACO into Eclipse

**As a result of an [issue with Eclipse](https://bugs.eclipse.org/bugs/show_bug.cgi?id=574362), versions 4.20+ will not support TACO**

Create a new project from existing files, select Java 7 for both compilation and execution, and all jar files inside the `lib` folder (disregard jar files inside `lib/stryker` folder).

## Importing TACO into IntelliJ

Follow the same instructions as for Eclipse. Set the `tests` folder as a `Source` folder.

## Running TACO

For the moment TACO works by using tests (JUnit tests). Several examples of these can be found in the `unittest` source folder.

Each unit test sets specific configuration parameters via helper methods before executing a verification using the `check(...)` method. For example:

    public void test_example() throws VizException {
       setConfigKeyRelevantClasses("your.package.ClassName");
       setConfigKeyParallelTOStep(5, 10, 2, 8); //Enables parallel TO strategy
       check(GENERIC_PROPERTIES, "yourMethodName", true);

This configuration enables TACO to perform multiple verification attempts in parallel, using different Time Out values. This is especially useful when the solver struggles with a fixed Time Out.
* `initialTime`: initial time-out in seconds (e.g., 5)
* `maxTime`: maximum time-out limit (e.g., 10)
* `step`: how much to increase the time-out per parallel thread (e.g., 2)
* `threads`: how many parallel attempts to run (e.g., 8)

With `setConfigKeyParallelTOStep(5, 10, 2, 8)`, TACO will launch 8 verification attempts in parallel with time-outs ranging from 5s to 10s in 2-second increments.

## Running Tests via JAR Files

To execute JUnit tests directly from the terminal, you can use the compiled TACO .jar file.

* Step 1: Build the JAR file

  Run the following command to build the JAR file using Apache Ant: `ant build` and `ant jar`

  This will generate the file .jar inside the `dist/` directory. Make sure all required dependencies are in place by running the setup: `./setupTACO.sh`.

* Step 2: Prepare the test runner

  Create a script called `jrunJUnit.sh` with the following contents:


   `#!/bin/bash`

    Replace with the fully qualified class name of the test you want to run
  `fileToRun="your.package.TestClassName"`

   `testToRun="yourTestName"`


    Replace the following path with the absolute path to your FinalThreading project directory
   `PROJECT_DIR="/path/to/your/FinalThreading"`

 `java -cp .:${PROJECT_DIR}/dist/taco.jar:${PROJECT_DIR}/lib/junit-4.8.2.jar:${PROJECT_DIR}/bin:${PROJECT_DIR}/lib/dynalloy4.jar:${PROJECT_DIR}/lib/alloyRunner.jar:${PROJECT_DIR}/lib/antlr-4.3-complete.jar:${PROJECT_DIR}/lib/commons-collections-3.2.1.jar:${PROJECT_DIR}/lib/commons-configuration-1.6.jar:${PROJECT_DIR}/lib/commons-lang-2.4.jar:${PROJECT_DIR}/lib/commons-logging-1.1.1.jar:${PROJECT_DIR}/lib/edu.mit.csail.sdg.annotations_0.2.5.jar:${PROJECT_DIR}/lib/guava-16.0.1.jar:${PROJECT_DIR}/lib/javassist.jar:${PROJECT_DIR}/lib/jdynalloy.jar:${PROJECT_DIR}/lib/jml-release.jar:${PROJECT_DIR}/lib/log4j-1.2.15.jar:${PROJECT_DIR}/lib/mujava++.jar:${PROJECT_DIR}/lib/objenesis-2.6.jar:${PROJECT_DIR}/lib/org.hamcrest.core_1.3.0.v201303031735.jar:${PROJECT_DIR}/lib/recoder.jar:${PROJECT_DIR}/lib/reflections-0.9.9-RC1.jar 
  org.junit.runner.JUnitCore ${fileToRun} |& tee results${testToRun}.txt`

 * Step 3: Then give the script execution permissions:
  
   `chmod +x jrunJUnit.sh`

 * Step 4: Run the test from the PROJECT_DIR directory
 
   `./jrunJUnit.sh`
