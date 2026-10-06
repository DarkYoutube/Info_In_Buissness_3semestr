#!/usr/bin/env bash
set -e
rm -rf out
mkdir -p out
javac -encoding UTF-8 -d out $(find src -name "*.java")
java -cp out ru.edu.pr01.SelfCheck
