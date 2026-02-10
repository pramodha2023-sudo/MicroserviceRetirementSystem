@echo off
REM Quick start script for AWS Integration testing

cd /d p:\Games\MicroserviceRetirement-System

echo ======================================
echo AWS Integration - Quick Test
echo ======================================
echo.

REM Set Maven path
set PATH=p:\apache-maven-3.9.12\bin;%PATH%

echo Step 1: Building project...
echo.
mvn clean install

if %ERRORLEVEL% NEQ 0 (
    echo Build failed!
    pause
    exit /b 1
)

echo.
echo Build successful!
echo.

echo Step 2: Running local test (no AWS needed)...
echo.
java -cp target/classes com.cloudnative.retirement.aws.LocalTest

if %ERRORLEVEL% NEQ 0 (
    echo Test failed!
    pause
    exit /b 1
)

echo.
echo ======================================
echo Success! All tests passed!
echo ======================================
pause
