#!/usr/bin/env bash
cd "$(dirname "$0")"
SQLITE_JAR=$(ls lib/sqlite-jdbc-*.jar 2>/dev/null | head -1)
if [ -z "$SQLITE_JAR" ]; then
  echo "ERROR: SQLite connector jar not found in lib/. See README."
  exit 1
fi
mkdir -p out
echo "Compiling..."
javac -d out -cp "$SQLITE_JAR" -sourcepath src \
  src/Main.java src/models/*.java src/managers/*.java \
  src/utils/*.java src/gui/*.java src/gui/components/*.java || { echo "Compilation failed"; exit 1; }
echo "Starting application..."
java -cp "out:$SQLITE_JAR" Main
