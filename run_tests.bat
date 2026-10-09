@echo off
title Smart Home Automation Simulator - Automated Test Suite
echo ====================================================================
echo      RUNNING SYSTEM INTEGRATION & VERIFICATION TEST SUITE
echo ====================================================================
echo.
if exist "bin\com\smarthome\test\SystemIntegrationTestSuite.class" (
    java -cp bin com.smarthome.test.SystemIntegrationTestSuite
) else if exist "SmartHomeSimulator.jar" (
    java -cp SmartHomeSimulator.jar com.smarthome.test.SystemIntegrationTestSuite
) else (
    echo [ERROR] Tests have not been compiled yet!
    echo Please run 'build_and_run.bat' first.
)
echo.
pause
