#!/usr/bin/env bash
# Runs a command in the optional Docker environment (Linux x86_64 with a
# JDK 8 and the Java 7 class library, see docker/Dockerfile), with this
# repository mounted at /work. An alternative to installing a JDK 8 locally;
# the other scripts work the same inside it.
#
# Usage: scripts/docker.sh [--rebuild] [command [args...]]
#
#   scripts/docker.sh scripts/run-benchmark.sh --reset <DriverClass>
#   scripts/docker.sh scripts/smoke-test.sh
#   scripts/docker.sh                    # interactive shell
#
# The image (default name stryker-jdk8, override with STRYKER_DOCKER_IMAGE)
# is built from docker/Dockerfile on first use; --rebuild rebuilds it.
# STRYKER_JVM_OPTS and STRYKER_SOLVER are passed through.
set -eo pipefail

root="$(cd "$(dirname "$0")/.." && pwd)"
image="${STRYKER_DOCKER_IMAGE:-stryker-jdk8}"

if [ "${1:-}" = "-h" ] || [ "${1:-}" = "--help" ]; then
    sed -n '2,/^set -eo/p' "$0" | sed '$d' | sed 's/^# \{0,1\}//'
    exit 0
fi

command -v docker > /dev/null || { echo "error: docker is not installed" >&2; exit 2; }

rebuild=false
if [ "${1:-}" = "--rebuild" ]; then
    rebuild=true
    shift
fi
if $rebuild || ! docker image inspect "$image" > /dev/null 2>&1; then
    echo "Building the $image image (first use only)..." >&2
    docker build --platform linux/amd64 -t "$image" "$root/docker"
fi

tty_flags=()
if [ -t 0 ] && [ -t 1 ]; then
    tty_flags=(-it)
fi
env_flags=()
for var in STRYKER_JVM_OPTS STRYKER_SOLVER; do
    if [ -n "${!var:-}" ]; then
        env_flags+=(-e "$var")
    fi
done
if [ $# -eq 0 ]; then
    set -- bash
fi
# In a linked worktree (git worktree add), .git is a file that points into the
# main repository's .git directory. Mount that directory at the same path, so
# that git (used by --reset and the smoke test) works inside the container.
git_flags=()
if [ -f "$root/.git" ]; then
    common="$(cd "$root" && cd "$(git rev-parse --git-common-dir)" && pwd -P)"
    git_flags=(-v "$common:$common")
fi

# Run as the calling user, so files written into the repository (bin/,
# output/, reports, fixes in tests/) are not owned by root.
exec docker run --rm --platform linux/amd64 ${tty_flags[@]+"${tty_flags[@]}"} \
    --user "$(id -u):$(id -g)" -e HOME=/tmp ${env_flags[@]+"${env_flags[@]}"} \
    -v "$root:/work" ${git_flags[@]+"${git_flags[@]}"} -w /work "$image" "$@"
