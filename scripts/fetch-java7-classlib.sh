#!/usr/bin/env bash
# Puts a Java 7 class library at lib/java7/rt.jar. Stryker needs it on JDK 8,
# 21 and 25: the JML parser and JML4C read the JDK's class library and cannot use
# the one of Java 8 or later, so they read this Java 7 rt.jar instead (see
# src/ar/edu/taco/Java7ClassLibrary.java). Plain TACO runs without it.
# lib/java7/ is git-ignored.
#
# Usage: scripts/fetch-java7-classlib.sh [--from JDK7_HOME]
#
#   (no option)       download Azul Zulu 7u352 (the Linux x64 archive, 68 MB;
#                     its classes are the same on every platform), check its
#                     checksum and extract only jre/lib/rt.jar (61 MB)
#   --from JDK7_HOME  copy rt.jar from a JDK 7 you already have instead
#   -h, --help        show this help
#
# Does nothing if lib/java7/rt.jar already exists (delete it to fetch again).
set -eo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

die() {
    echo "error: $*" >&2
    exit 2
}

usage() {
    sed -n '2,/^set -eo/p' "$0" | sed '$d' | sed 's/^# \{0,1\}//'
    exit "${1:-0}"
}

ZULU=zulu7.56.0.11-ca-jdk7.0.352-linux_x64
ZULU_URL="https://cdn.azul.com/zulu/bin/$ZULU.tar.gz"
ZULU_SHA256=8a7387c1ed151474301b6553c6046f865dc6c1e1890bcf106acc2780c55727c8
RT_JAR_SHA256=b7e1f8d3248ab22e4f4c313211938b7abb3d0736a36de230bda4f9248e887f6e

from=""
while [ $# -gt 0 ]; do
    case "$1" in
        --from) shift; from="${1:-}"; [ -n "$from" ] || usage 2 ;;
        -h|--help) usage 0 ;;
        *) echo "unknown option: $1" >&2; usage 2 ;;
    esac
    shift
done

dest="$ROOT/lib/java7/rt.jar"
if [ -f "$dest" ]; then
    echo "$dest already exists."
    exit 0
fi

sha256() {
    if command -v sha256sum > /dev/null; then
        sha256sum "$1" | cut -d' ' -f1
    else
        shasum -a 256 "$1" | cut -d' ' -f1
    fi
}

mkdir -p "$(dirname "$dest")"

if [ -n "$from" ]; then
    rt="$from/jre/lib/rt.jar"
    [ -f "$rt" ] || rt="$from/lib/rt.jar"    # a JRE instead of a JDK
    [ -f "$rt" ] || die "no rt.jar under $from (expected jre/lib/rt.jar)"
    # Byte 8 of a class file is its major version: 51 is Java 7.
    major="$(unzip -p "$rt" java/lang/Object.class | od -An -tu1 | awk 'NR == 1 {print $8}')"
    [ "$major" = 51 ] || die "$rt is not a Java 7 class library"
    cp "$rt" "$dest"
    echo "Copied $rt to $dest"
    exit 0
fi

command -v curl > /dev/null || die "curl is needed to download the class library"
tmp="$(mktemp -d "${TMPDIR:-/tmp}/java7-classlib.XXXXXX")"
trap 'rm -rf "$tmp"' EXIT

echo "Downloading $ZULU_URL (68 MB)..."
curl -fL --progress-bar -o "$tmp/zulu7.tar.gz" "$ZULU_URL"
[ "$(sha256 "$tmp/zulu7.tar.gz")" = "$ZULU_SHA256" ] || die "checksum mismatch for $ZULU_URL"

tar -xzf "$tmp/zulu7.tar.gz" -C "$tmp" "$ZULU/jre/lib/rt.jar"
[ "$(sha256 "$tmp/$ZULU/jre/lib/rt.jar")" = "$RT_JAR_SHA256" ] || die "unexpected rt.jar in $ZULU_URL"
mv "$tmp/$ZULU/jre/lib/rt.jar" "$dest"
echo "Installed the Java 7 class library at $dest"
