@echo off
echo ========================================
echo  Cafeteria Preorder System (SQLite)
echo ========================================
echo.
cd /d "%~dp0"

set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.13.11-hotspot
set JAVAC_CMD=%JAVA_HOME%\bin\javac
if not exist "%JAVAC_CMD%.exe" ( set JAVA_HOME=C:\Program Files\Java\jdk-21& set JAVAC_CMD=%JAVA_HOME%\bin\javac )
if not exist "%JAVAC_CMD%.exe" ( set JAVA_HOME=C:\Program Files\Java\jdk-17& set JAVAC_CMD=%JAVA_HOME%\bin\javac )
if not exist "%JAVAC_CMD%.exe" (
    echo ERROR: Java JDK not found! Install JDK 17+ and update this path.
    pause & exit /b 1
)
set JAVA_CMD=%JAVA_HOME%\bin\java

for %%f in (lib\sqlite-jdbc-*.jar) do set SQLITE_JAR=%%f
if "%SQLITE_JAR%"=="" (
    echo ERROR: SQLite connector jar not found in lib\
    echo Download sqlite-jdbc-x.x.x.jar into the lib\ folder ^(see README^).
    pause & exit /b 1
)

if not exist "out" mkdir out
echo Compiling...
"%JAVAC_CMD%" -d out -cp "%SQLITE_JAR%" -sourcepath src src/Main.java src/models/*.java src/managers/*.java src/utils/*.java src/gui/*.java src/gui/components/*.java
if %ERRORLEVEL% NEQ 0 ( echo Compilation failed! & pause & exit /b 1 )

echo Starting application...
"%JAVA_CMD%" -cp "out;%SQLITE_JAR%" Main
pause
