# Master run script for Microservice Retirement System
# Runs all components: build, tests, examples

$env:JAVA_HOME = "C:\Program Files\Java\jdk-17.0.18"
$mvnCmd = "p:\apache-maven-3.9.12\bin\mvn.cmd"
$jarFile = "p:\Games\MicroserviceRetirement-System\target\microservice-retirement-system-1.0.0.jar"
$projectDir = "p:\Games\MicroserviceRetirement-System"

Write-Host ""
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "  MICROSERVICE RETIREMENT SYSTEM - MASTER RUN" -ForegroundColor Cyan
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host ""

Set-Location $projectDir

# Check if jar exists
if (!(Test-Path $jarFile)) {
    Write-Host "[*] JAR not found, building project..." -ForegroundColor Yellow
    & $mvnCmd clean package -DskipTests
    if ($LASTEXITCODE -ne 0) {
        Write-Host "[ERROR] Build failed!" -ForegroundColor Red
        exit 1
    }
}

# Run local test
Write-Host "[1/3] Running Local Test (no AWS needed)..." -ForegroundColor Yellow
Write-Host ""
java -cp $jarFile com.cloudnative.retirement.aws.LocalTest
if ($LASTEXITCODE -ne 0) {
    Write-Host ""
    Write-Host "[ERROR] Local test failed!" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "[2/3] Running AWS Examples..." -ForegroundColor Yellow
Write-Host ""
java -cp $jarFile com.cloudnative.retirement.aws.AWSIntegrationExample 5
if ($LASTEXITCODE -ne 0) {
    Write-Host ""
    Write-Host "[ERROR] Examples failed!" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "[3/3] Running Unit Tests..." -ForegroundColor Yellow
Write-Host ""
& $mvnCmd test

Write-Host ""
Write-Host "============================================================" -ForegroundColor Green
Write-Host "  ALL TESTS COMPLETED SUCCESSFULLY!" -ForegroundColor Green
Write-Host "============================================================" -ForegroundColor Green
Write-Host ""

Write-Host "Next steps:" -ForegroundColor Yellow
Write-Host "  1. Run main simulator:" -ForegroundColor Gray
Write-Host "     java -cp $jarFile com.cloudnative.retirement.MicroserviceRetirementSimulator" -ForegroundColor Gray
Write-Host "  2. Set up AWS:" -ForegroundColor Gray
Write-Host "     .\setup-aws.ps1" -ForegroundColor Gray
Write-Host "  3. View documentation:" -ForegroundColor Gray
Write-Host "     AWS_INTEGRATION_GUIDE.md" -ForegroundColor Gray
Write-Host ""
