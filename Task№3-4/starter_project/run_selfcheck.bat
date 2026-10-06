@echo off
if exist out rmdir /s /q out
mkdir out
for /r src %%f in (*.java) do echo %%f>>sources.txt
javac -encoding UTF-8 -d out @sources.txt
del sources.txt
java -cp out ru.edu.pr01.SelfCheck
