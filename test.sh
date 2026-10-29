#!/bin/sh
set -eu

script_dir=$(CDPATH= cd "$(dirname "$0")" && pwd)
cd "$script_dir"

if ! command -v javac >/dev/null 2>&1 || ! command -v java >/dev/null 2>&1; then
    printf '%s\n' 'A JDK 17 or newer installation is required (java and javac on PATH).' >&2
    exit 1
fi
compiler_version=$(javac -version 2>&1)
compiler_major=${compiler_version#javac }
compiler_major=${compiler_major%%.*}
compiler_major=${compiler_major%%-*}
case "$compiler_major" in
    ''|*[!0-9]*)
        printf '%s\n' "Cannot identify compiler version: $compiler_version" >&2
        exit 1
        ;;
esac
if [ "$compiler_major" -lt 17 ]; then
    printf '%s\n' "JDK 17 or newer is required; found $compiler_version." >&2
    exit 1
fi

build_dir=$(mktemp -d "${TMPDIR:-/tmp}/daa-assignment3-test.XXXXXX")
trap 'rm -rf "$build_dir"' 0
trap 'exit 1' HUP INT TERM

# Newer JDKs warn that Java 8 target options are obsolete; keep source lint strict.
javac --release 8 -Xlint:all,-options -Werror -d "$build_dir" src/*.java tests/*.java
java -ea -Xss256k -cp "$build_dir" Assignment3Test
