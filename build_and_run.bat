@echo off
setlocal enabledelayedexpansion
title Smart Home Automation Simulator - Build and Run

echo ====================================================================
echo     SMART HOME AUTOMATION SIMULATOR - AUTOMATED DEPLOYMENT
echo ====================================================================
echo.

:: 1. Check if javac is available
where javac >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [INFO] 'javac' not found in system PATH. Searching standard JDK directories...
    set "FOUND_JDK="
    for /d %%D in ("C:\Program Files\Java\jdk*" "C:\Program Files\Eclipse Adoptium\jdk*" "C:\Program Files\BellSoft\*" "C:\Program Files (x86)\Java\jdk*") do (
        if exist "%%D\bin\javac.exe" (
            set "FOUND_JDK=%%D"
            set "PATH=%%D\bin;!PATH!"
            goto :jdk_found
        )
    )
    
    echo.
    echo [ERROR] Java Development Kit (JDK) compiler 'javac' was not found!
    echo Please make sure JDK 17 or higher is installed on your computer.
    echo Download JDK free from: https://adoptium.net/ or https://www.oracle.com/java/technologies/downloads/
    echo.
    echo If JDK is already installed, add its 'bin' directory to your Windows PATH environment variable.
    pause
    exit /b 1
)

:jdk_found
echo [OK] Java Compiler located:
javac -version
echo.

:: 2. Create bin output directory
if not exist "bin" (
    echo [INFO] Creating 'bin' directory...
    mkdir bin
)

:: 3. Compile all source files
echo [INFO] Compiling all Java packages...
javac -d bin -sourcepath src ^
    src/com/smarthome/interfaces/*.java ^
    src/com/smarthome/exceptions/*.java ^
    src/com/smarthome/model/*.java ^
    src/com/smarthome/automation/*.java ^
    src/com/smarthome/gui/*.java ^
    src/com/smarthome/test/*.java ^
    src/com/smarthome/*.java

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Compilation failed! Please check error messages above.
    pause
    exit /b 1
)
echo [OK] All Java source files compiled successfully.
echo.

:: 4. Package into Executable JAR
echo [INFO] Packaging into standalone executable 'SmartHomeSimulator.jar'...
jar cvfm SmartHomeSimulator.jar manifest.txt -C bin . >nul

if %ERRORLEVEL% EQU 0 (
    echo [OK] 'SmartHomeSimulator.jar' generated successfully!
) else (
    echo [WARNING] JAR packaging encountered an issue, but class files in 'bin' are ready.
)
echo.

:: 5. Launch the Application
echo ====================================================================
echo   LAUNCHING SMART HOME AUTOMATION SIMULATOR DASHBOARD...
echo ====================================================================
echo.

if exist "SmartHomeSimulator.jar" (
    java -jar SmartHomeSimulator.jar
) else (
    java -cp bin com.smarthome.SmartHomeApp
)

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [NOTICE] Application terminated. Press any key to close this window.
    pause
)
