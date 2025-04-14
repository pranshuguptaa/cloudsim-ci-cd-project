@echo off
set JAVA_HOME=E:\jdk8
set PATH=%JAVA_HOME%\bin;%PATH%

if not exist classes mkdir classes

echo Compiling CloudSim source files...
javac -d classes sources/org/cloudbus/cloudsim/*/*.java sources/org/cloudbus/cloudsim/*/*/*.java

echo Compiling simulation...
javac -d classes -cp classes examples/mysim/MySimulation.java

if %ERRORLEVEL% EQU 0 (
    echo Running simulation...
    java -cp classes mysim.MySimulation
) else (
    echo Compilation failed!
    pause
) 