#!/usr/bin/env bash
# Builds the project into bin/ with JDK 8 and Ant.
# Usage: scripts/build.sh [ant targets...]   (default target: build)
set -eo pipefail
. "$(dirname "$0")/common.sh"

require_jdk
echo "Using the JDK at $JAVA_HOME"
cd "$STRYKER_ROOT"
run_ant "$@"
