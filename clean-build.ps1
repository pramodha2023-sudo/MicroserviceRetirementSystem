# CLEAN BUILD AND TEST - No PATH Issues
# Run this: .\clean-build.ps1

Write-Host ""
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "  CLEAN BUILD - Microservice Retirement System" -ForegroundColor Cyan
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host ""

# Use full paths - don't rely on PATH variable
$mvn = "p:\apache-maven-3.9.12\bin\mvn.cmd"
$projectDir = "p:\Games\MicroserviceRetirement-System"

Write-Host "Checking Maven..." -ForegroundColor Yellow
if (!(Test-Path $mvn)) {
    Write-Host "ERROR: Maven not found at $mvn" -ForegroundColor Red
    exit 1
}
Write-Host "✓ Maven ready" -ForegroundColor Green
Write-Host ""

Write-Host "Changing to project directory..." -ForegroundColor Yellow
Set-Location $projectDir
Write-Host "✓ In: $projectDir" -ForegroundColor Green
Write-Host ""

Write-Host "============================================================" -ForegroundColor Yellow
Write-Host "BUILDING PROJECT"
Write-Host "============================================================" -ForegroundColor Yellow
Write-Host ""

# Run Maven with full path (skips problematic PATH)
& $mvn clean install -DskipTests -q

if ($LASTEXITCODE -ne 0) {
    Write-Host ""
    Write-Host "Build command output:" -ForegroundColor Red
    & $mvn clean install -DskipTests
    Write-Host ""
    Write-Host "ERROR: Build failed!" -ForegroundColor Red
    exit 1
}

Write-Host "✓ Build successful!" -ForegroundColor Green
Write-Host ""

Write-Host "============================================================" -ForegroundColor Yellow
Write-Host "RUNNING TEST"
Write-Host "============================================================" -ForegroundColor Yellow
Write-Host ""

java -cp target/classes com.cloudnative.retirement.aws.LocalTest

Write-Host ""
Write-Host "============================================================" -ForegroundColor Green
Write-Host "  SUCCESS! All tests passed!" -ForegroundColor Green
Write-Host "============================================================" -ForegroundColor Green
Write-Host ""
