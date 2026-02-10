# Quick start script for AWS Integration testing
# Run: .\run-test.ps1

Write-Host "======================================" -ForegroundColor Cyan
Write-Host "AWS Integration - Quick Test" -ForegroundColor Cyan
Write-Host "======================================" -ForegroundColor Cyan
Write-Host ""

# Navigate to project
Set-Location "p:\Games\MicroserviceRetirement-System"

# Add Maven to PATH
$env:Path = "p:\apache-maven-3.9.12\bin;$($env:Path)"

# Verify Maven
Write-Host "Checking Maven installation..." -ForegroundColor Yellow
$mvnVersion = & mvn --version 2>&1
if ($LASTEXITCODE -eq 0) {
    Write-Host "✓ Maven found: $($mvnVersion | Select-Object -First 1)" -ForegroundColor Green
} else {
    Write-Host "✗ Maven not found!" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "Step 1: Building project..." -ForegroundColor Yellow
Write-Host ""

& mvn clean install

if ($LASTEXITCODE -ne 0) {
    Write-Host "✗ Build failed!" -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}

Write-Host ""
Write-Host "✓ Build successful!" -ForegroundColor Green
Write-Host ""

Write-Host "Step 2: Running local test (no AWS needed)..." -ForegroundColor Yellow
Write-Host ""

& java -cp target/classes com.cloudnative.retirement.aws.LocalTest

if ($LASTEXITCODE -ne 0) {
    Write-Host ""
    Write-Host "✗ Test failed!" -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}

Write-Host ""
Write-Host "======================================" -ForegroundColor Green
Write-Host "Success! All tests passed!" -ForegroundColor Green
Write-Host "======================================" -ForegroundColor Green
