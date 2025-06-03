# TACO
### Translation of Annotated Code

[TACO](https://github.com/mffrias/TACO) is a tool that translates a **Java** program annotated with [JML](https://www.cs.ucf.edu/~leavens/JML/index.shtml) specifications into a [DynAlloy](https://doi.org/10.1007/978-3-540-45236-2_37) model. Properties described by the JML annotations can then be verified using a SAT Solver.

## Requirements

As for the moment, TACO requires [Java 7](https://www.oracle.com/java/technologies/javase/javase7-archive-downloads.html) and [Apache Ant 1.9.16](https://ant.apache.org/bindownload.cgi).
> [!TIP]
> Apache Ant 1.9 can also be installed using [Homebrew](https://brew.sh). Ensure that Ant 1.9 is linked before executing `./setupTACO.sh`.

TACO depends on [jdynalloy](https://github.com/mffrias/jDynAlloy) and [muJava++](https://github.com/saiema/MuJava). These dependencies and their dependencies are all downloaded by the provided script `setupTACO.sh`.

## Installation

### Windows
#### Setting Up Dependencies

> [!NOTE]
> Setup within [WSL2](https://ubuntu.com/desktop/wsl)

- GitHub Cli
  - `sudo apt update`
  - `sudo apt upgrade`
  - `sudo apt install gh`
  - After installation:
    - `gh auth login -w`

- Setup SSH
  - Generate SSH Key
    - `ssh-keygen -t ed25519 -C "your_email@example.com"`
      - enter for default directory, enter password if
    - `eval "$(ssh-agent -s)"`
    - `ssh-add ~/.ssh/id_ed25519`

- Change back to home
  - `cd ~`

- Install Unzip
  - `sudo apt-get install unzip`

- Install Zip
  - `sudo apt-get install zip`

- Install SDKMan
  - `curl -s "https://get.sdkman.io" | bash`

  - Then run:
    - `source "$HOME/.sdkman/bin/sdkman-init.sh"`

- Install Java 7
  - `sdk install java 7.0.352-zulu`

- Copy `java` to `/usr/bin/`
  - Java is located at: `.sdkman/candidates/java/7.0.352-zulu/bin/java`

  - Copy with:
    - `cp .sdkman/candidates/java/7.0.352-zulu/bin/java /usr/bin/`

- Install Ant 1.9
  - `sdk install ant 1.9.15`

- Intellij Installation
  - `wget https://download.jetbrains.com/idea/ideaIC-2024.2.4.tar.gz`

  - Untar/unzip with:
    - `tar -xvf ideaIC-2024.2.4.tar.gz`

- Close terminal and open new terminal

#### TACO Setup

- [GitHub](https://github.com/mffrias/TACO):
  - Go to `Code` and copy link

- In `Terminal` run:
  - `git clone https://github.com/mffrias/TACO`
  - `cd TACO`
  - `./setupTACO.sh`

- Change into Intellij Directory
- go back to home directory `cd ~`
  - `cd idea-IC-whatever-numbers`, use tab to autocomplete
  - `cd bin`

  - Open Intellij:
    - `./idea`

- Setup Project
  - Create new project

  - Find the location you would like to create it and give it a name

  - JDK version 1.7
  - Create project

  - Top Left (Hamburger 4 lines) -> Project Structure ->
  Modules -> + (Plus Sign) -> Insert Modules -> Import Module ->
  Create from Existing Sources -> Select TACO directory

### Unix/Linux
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

In the near future, a usable terminal option will be developed.
