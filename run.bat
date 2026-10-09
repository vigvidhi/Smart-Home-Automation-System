@echo off
title Smart Home Automation Simulator
echo Starting Smart Home Automation Simulator...
if exist "SmartHomeSimulator.jar" (
    start javaw -jar SmartHomeSimulator.jar
) else if exist "bin\com\smarthome\SmartHomeApp.class" (
    start javaw -cp bin com.smarthome.SmartHomeApp
) else (
    echo [ERROR] The application has not been built yet!
    echo Please run 'build_and_run.bat' first to compile and package the application.
    pause
)
