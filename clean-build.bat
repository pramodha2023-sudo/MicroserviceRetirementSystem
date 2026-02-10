@echo off
REM CLEAN BUILD - No PATH Issues
REM This script uses full paths to avoid PATH variable conflicts

setlocal enabledelayedexpansion

echo.
echo ============================================================
echo   CLEAN BUILD - Microservice Retirement System
echo ============================================================
echo.

set MAVEN_CMD=p:\apache-maven-3.9.12\bin\mvn.cmd
set PROJECT_DIR=p:\Games\MicroserviceRetirement-System

if not exist "%MAVEN_CMD%" (
    echo ERROR: Maven not found at %MAVEN_CMD%
    exit /b 1
)

echo Checking Maven...
"%MAVEN_CMD%" --version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Maven check failed!
    exit /b 1
)
echo [OK] Maven ready
echo.

echo Changing to project directory...
cd /d "%PROJECT_DIR%"
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Cannot change to project directory
    exit /b 1
)
echo [OK] In: %PROJECT_DIR%
echo.

echo ============================================================
echo BUILDING PROJECT
echo ============================================================
echo.

"%MAVEN_CMD%" clean install -DskipTests

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ERROR: Build failed!
    exit /b 1
)

echo.
echo [OK] Build successful!
echo.

echo ============================================================
echo RUNNING TEST
echo ============================================================
echo.

java -cp target/classes com.cloudnative.retirement.aws.LocalTest

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ERROR: Test failed!
    exit /b 1
)

echo.
echo ============================================================
echo   SUCCESS! All tests passed!
echo ============================================================
echo.
