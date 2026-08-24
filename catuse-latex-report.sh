#!/usr/bin/env bash
# Compiles a CatUSE specification and writes a LaTeX study report next to
# it: the CatUSE source, its desugared plain MLM-USE form, and a PlantUML
# class diagram. See org.tzi.use.tools.catuselatex.CatUSELatexReport.
#
# Usage: ./catuse-latex-report.sh <input.use> [outputDir]
#
# The resulting .tex calls the `plantuml` executable via \write18 to
# render the diagram, so build it with: pdflatex -shell-escape report.tex
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
CP_FILE="$SCRIPT_DIR/use-core/target/catuselatex-classpath.txt"

if [ ! -d "$SCRIPT_DIR/use-core/target/classes" ]; then
    mvn -q -pl use-core -f "$SCRIPT_DIR/pom.xml" compile
fi

if [ ! -f "$CP_FILE" ]; then
    mvn -q -pl use-core -f "$SCRIPT_DIR/pom.xml" dependency:build-classpath \
        -Dmdep.outputFile="$CP_FILE"
fi

exec java -cp "$SCRIPT_DIR/use-core/target/classes:$(cat "$CP_FILE")" \
    org.tzi.use.tools.catuselatex.CatUSELatexReport "$@"
