#!/bin/bash

# This script will download all repositories used by TACO and leave a comitaco project ready to load into an IDE*
# Usage is
# ./setupTACO.sh [full] [-h]
# If full is specified it will also download mujava++ and OJ repositories, if not, then the mujava2018.jar and openjava2018.jar inside TACO/lib will be used instead
# With -h as the second argument (e.g. ./setupTACO.sh "" -h) it clones over HTTPS instead of SSH.
# Exit status: 0 done, 1 a TACO folder without lib/, 2 a git clone failed, 5, 6 or 7 ant clean, build
# or jar failed, 8 the Java 7 class library could not be fetched.
# (*) If you use Eclipse take into account this issue (https://bugs.eclipse.org/bugs/show_bug.cgi?id=574362) that prevent the usage of java 7 or older
#     with the latest version of Eclipse (4.20+).

#set -x

BUILD_FILE="build.xml"
COMITACO="TACO"
COMITACO_NAME="taco"
DYNALLOY="dynalloy4"
DYNALLOY_NAME="dynalloy4"
JDYNALLOY="jDynAlloy"
JDYNALLOY_NAME="jdynalloy"
CURRENT_DIRECTORY=$(pwd)



if [[ "$1" == "full" ]]; then
    downloadMUJAVA=1
else
    downloadMUJAVA=0
fi

if [[ "$2" == "-h" ]]; then
	URL="https://github.com/"
else
	URL="git@github.com:"
fi


function adjustWhereIAm() {
  local isMe=0
  if [ -e "build.xml" ]; then
    isMyBuild "${COMITACO_NAME}" isMe
    if [[ "$isMe" -eq "1" ]]; then
      cd ..
    else
      echo "ERROR: I'm inside a folder with a build.xml which does not belong to TACO, I'm lost!"
      exit 1
    fi
  fi
  for buildFile in $(find . -maxdepth 2 -name "$BUILD_FILE"); do
    local buildFileFolder=$(dirname "$buildFile" | xargs -I {} basename {})
    cd "$buildFileFolder"
    isMyBuild "${COMITACO_NAME}" isMe
    if [[ "$isMe" -eq "1" ]]; then
      COMITACO="$buildFileFolder"
    fi
    isMyBuild "${DYNALLOY_NAME}" isMe
    if [[ "$isMe" -eq "1" ]]; then
      DYNALLOY="$buildFileFolder"
    fi
    isMyBuild "${JDYNALLOY_NAME}" isMe
    if [[ "$isMe" -eq "1" ]]; then
      JDYNALLOY="$buildFileFolder"
    fi
    cd ..
  done
}

# This function takes a string and will assume there is a build.xml in the current directory
# Then it will return (in the second argument) if it could find the following string
# <property name="app.id" value="FIRST_ARGUMENT"
function isMyBuild() {
  local result=0
  local stringToSearch="$1"
  if $(grep -qE "<property name=\"app\.id\" value=\"${stringToSearch}\"" "$BUILD_FILE"); then
    result=1
  fi
  eval "$2='$result'"
}


adjustWhereIAm

printf "Summary\nCURRENT DIRECTORY:$(pwd)\nTACO FOLDER:${COMITACO}\nDYNALLOY4 FOLDER:${DYNALLOY}\nJDYNALLOY FOLDER:${JDYNALLOY}\n"

COMITACO_LIB="${COMITACO}/lib"
DYNALLOY_BUILD="${DYNALLOY}/build.xml"
JDYNALLOY_BUILD="${JDYNALLOY}/build.xml"

# This function will run the following
# * ant clean
# * ant build
# * ant jar
# it will check the exit code at each step
# This function will exit the script if an error was found.
function runAnt() {
  antCommand "clean" 5
  antCommand "build" 6
  antCommand "jar" 7
}

# This will run ant followed by a specific command
# command : the command to run
# exitCode : the exit code to use if the previous command failed
function antCommand() {
  local command="$1"
  local exitCode="$2"
  ant "$command"
  local ecode="$?"
  if [[ ! "$ecode" -eq "0" ]]; then
    echo "ERROR: ant ${command} FAILED!"
    exit "$exitCode"
  fi
}

# This will clone a repository, and exit the script if git fails
# repository : the repository, e.g. mffrias/TACO
# any further arguments are passed to git clone, e.g. -b stryker
function cloneRepository() {
  local repository="$1"
  shift
  if ! git clone "$@" "${URL}${repository}.git"; then
    echo "ERROR: git clone ${URL}${repository}.git FAILED!"
    exit 2
  fi
}


echo "Downloading repositories..."
if [ -e "${COMITACO}" ]; then
  echo "${COMITACO} folder found, checking if ${COMITACO}/lib exists..."
  if [ ! -e "${COMITACO_LIB}" ]; then
    echo "ERROR: ${COMITACO} folder exists in current directory but no ${COMITACO}/lib folder found"
    exit 1
  fi
  echo "comitaco/TACO will not be downloaded"
else
  cloneRepository mffrias/TACO
fi
# dynalloy4 is built from the branch with the same name as TACO's current branch, if dynalloy4 has
# one (TACO's stryker branch needs dynalloy4's stryker branch), and from master otherwise.
TACO_BRANCH=$(git -C "${COMITACO}" rev-parse --abbrev-ref HEAD 2>/dev/null)
if [[ -n "$TACO_BRANCH" ]] && git ls-remote --exit-code --heads ${URL}mffrias/dynalloy4.git "$TACO_BRANCH" > /dev/null 2>&1; then
  DYNALLOY_BRANCH="$TACO_BRANCH"
else
  DYNALLOY_BRANCH="master"
fi
echo "dynalloy4 branch: ${DYNALLOY_BRANCH} (TACO is on '${TACO_BRANCH}')"
if [ ! -e "${DYNALLOY}" ]; then
  cloneRepository mffrias/dynalloy4 -b "${DYNALLOY_BRANCH}"
else
  echo "${DYNALLOY} folder found, checking..."
  if [ ! -e "${DYNALLOY_BUILD}" ]; then
    echo "ERROR: missing ${DYNALLOY_BUILD}"
  fi
  dynalloyBranch=$(git -C "${DYNALLOY}" rev-parse --abbrev-ref HEAD 2>/dev/null)
  if [[ "$dynalloyBranch" != "${DYNALLOY_BRANCH}" ]]; then
    echo "WARNING: ${DYNALLOY} is on branch '${dynalloyBranch}', not ${DYNALLOY_BRANCH} (git -C ${DYNALLOY} checkout ${DYNALLOY_BRANCH})"
  fi
  if $(grep -qE "<project.*name=\"${DYNALLOY_NAME}>\"" "${DYNALLOY_BUILD}"); then
    echo "ERROR: ${DYNALLOY_BUILD} does not appear to be valid"
  fi
fi
if [ ! -e "${JDYNALLOY}" ]; then
  cloneRepository mffrias/jDynAlloy
else
  echo "${JDYNALLOY} folder found, checking..."
  if [ ! -e "${JDYNALLOY_BUILD}" ]; then
    echo "ERROR: missing ${JDYNALLOY_BUILD}"
  fi
  if $(grep -qE "<property.*name=\"app\.id\" value=\"${JDYNALLOY_NAME}>\"" "${JDYNALLOY_BUILD}"); then
    echo "ERROR: ${JDYNALLOY_BUILD} does not appear to be valid"
  fi
fi

if [[ "$downloadMUJAVA" -eq "1" ]]; then
  cloneRepository saiema/MuJava
  cloneRepository saiema/OJ-with-Java-1.6
  cd OJ-with-Java-1.6
  git checkout develop
  cd ..
fi
echo "Done"
echo "Compiling dynalloy4.jar..."
cd "$DYNALLOY"
runAnt
cp "dist/${DYNALLOY_NAME}.jar" "../${COMITACO_LIB}"
cp "dist/${DYNALLOY_NAME}.jar" "../${JDYNALLOY}/lib"
cd ..
echo "Done"
echo "Compiling jdynalloy.jar..."
cd "$JDYNALLOY"
runAnt
cp "dist/${JDYNALLOY_NAME}.jar" "../${COMITACO_LIB}"
cd ..
echo "Done"
# On JDK 8 and later the JML tools need a Java 7 class library (lib/java7/rt.jar); the script
# downloads Azul Zulu 7 and checks its checksum, and does nothing if the file is already there.
echo "Fetching the Java 7 class library..."
if ! (cd "$COMITACO" && scripts/fetch-java7-classlib.sh); then
  echo "ERROR: could not fetch the Java 7 class library (see ${COMITACO}/scripts/fetch-java7-classlib.sh)"
  exit 8
fi
echo "Done"
echo "Now you can create a project for ${COMITACO}, use JDK 8, all jars inside lib folder should be used"

