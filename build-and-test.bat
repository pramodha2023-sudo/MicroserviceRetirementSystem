@echo off
REM Microservice Retirement System - AWS Integration Test
REM This script sets up Maven and Java paths, then builds and tests

setlocal enabledelayedexpansion

echo ======================================
echo AWS Integration Test Setup
echo ======================================
echo.

REM Set clean PATH with Maven and Java
echo Setting up environment...
set MAVEN_HOME=p:\apache-maven-3.9.12
set PATH=%MAVEN_HOME%\bin;%PATH%

REM Change to project directory
cd /d p:\Games\MicroserviceRetirement-System

echo.
echo Step 1: Verifying Maven installation...
mvn --version
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Maven not found. Please check installation.
    pause
    exit /b 1
)

echo.
echo Step 2: Building project (this may take 30-60 seconds on first run)...
echo.
mvn clean install -DskipTests

if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Build failed!
    echo.
    echo Troubleshooting tips:
    echo 1. Check internet connection (maven downloads dependencies)
    echo 2. Check disk space
    echo 3. Check firewall settings
    pause
    exit /b 1
)

echo.
echo ======================================
echo BUILD SUCCESSFUL!
echo ======================================
echo.

echo Step 3: Running local test (no AWS credentials needed)...
echo.
java -cp target/classes com.cloudnative.retirement.aws.LocalTest

if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Test failed!
    pause
    exit /b 1
)

echo.
echo ======================================
echo ALL TESTS PASSED!
echo ======================================
echo.
echo You can now:
echo 1. Run unit tests:  mvn test
echo 2. Run examples:    java -cp target/classes com.cloudnative.retirement.aws.AWSIntegrationExample
echo 3. Set up AWS:      .\setup-aws.ps1
echo.
pause
