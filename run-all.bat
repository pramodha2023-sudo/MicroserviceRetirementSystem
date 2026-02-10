@echo off
REM Master run script for Microservice Retirement System
REM Runs all components: build, tests, examples

setlocal enabledelayedexpansion

set JAVA_HOME=C:\Program Files\Java\jdk-17.0.18
set MAVEN_CMD=p:\apache-maven-3.9.12\bin\mvn.cmd
set JAR_FILE=p:\Games\MicroserviceRetirement-System\target\microservice-retirement-system-1.0.0.jar
set PROJECT_DIR=p:\Games\MicroserviceRetirement-System

echo ============================================================
echo   MICROSERVICE RETIREMENT SYSTEM - MASTER RUN
echo ============================================================
echo.

cd /d "%PROJECT_DIR%"

REM Check if jar exists
if not exist "%JAR_FILE%" (
    echo [*] JAR not found, building project...
    "%MAVEN_CMD%" clean package -DskipTests
    if %ERRORLEVEL% NEQ 0 (
        echo [ERROR] Build failed!
        exit /b 1
    )
)

REM Run local test
echo [1/3] Running Local Test (no AWS needed)...
echo.
java -cp "%JAR_FILE%" com.cloudnative.retirement.aws.LocalTest
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Local test failed!
    exit /b 1
)

echo.
echo [2/3] Running AWS Examples...
echo.
java -cp "%JAR_FILE%" com.cloudnative.retirement.aws.AWSIntegrationExample 5
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Examples failed!
    exit /b 1
)

echo.
echo [3/3] Running Unit Tests...
echo.
"%MAVEN_CMD%" test

echo.
echo ============================================================
echo   ALL TESTS COMPLETED SUCCESSFULLY!
echo ============================================================
echo.
echo Next steps:
echo   1. Run main simulator: java -cp "%JAR_FILE%" com.cloudnative.retirement.MicroserviceRetirementSimulator
echo   2. Set up AWS: .\setup-aws.ps1
echo   3. View documentation: AWS_INTEGRATION_GUIDE.md
echo.
pause
