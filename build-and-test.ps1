# Microservice Retirement System - AWS Integration Test
# PowerShell version - avoids PATH issues by using full paths

Write-Host ""
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "  AWS Integration Test - Microservice Retirement System" -ForegroundColor Cyan
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host ""

$mavenPath = "p:\apache-maven-3.9.12\bin\mvn.cmd"
$projectDir = "p:\Games\MicroserviceRetirement-System"

# Verify Maven exists
if (!(Test-Path $mavenPath)) {
    Write-Host "ERROR: Maven not found at $mavenPath" -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}

Write-Host "Step 1: Verifying Maven installation..." -ForegroundColor Yellow
& $mavenPath --version
Write-Host ""

if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: Maven check failed!" -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}

Write-Host "Step 2: Building project..." -ForegroundColor Yellow
Write-Host "(This may take 30-60 seconds on first run)" -ForegroundColor Gray
Write-Host ""

Set-Location $projectDir
& $mavenPath clean install -DskipTests

if ($LASTEXITCODE -ne 0) {
    Write-Host ""
    Write-Host "ERROR: Build failed!" -ForegroundColor Red
    Write-Host ""
    Write-Host "Troubleshooting:" -ForegroundColor Yellow
    Write-Host "1. Check internet connection" -ForegroundColor Gray
    Write-Host "2. Check disk space (need ~500MB)" -ForegroundColor Gray
    Write-Host "3. Check if antivirus is blocking" -ForegroundColor Gray
    Read-Host "Press Enter to exit"
    exit 1
}

Write-Host ""
Write-Host "============================================================" -ForegroundColor Green
Write-Host "  BUILD SUCCESSFUL!" -ForegroundColor Green
Write-Host "============================================================" -ForegroundColor Green
Write-Host ""

Write-Host "Step 3: Running local test (no AWS credentials needed)..." -ForegroundColor Yellow
Write-Host ""

java -cp target/classes com.cloudnative.retirement.aws.LocalTest

if ($LASTEXITCODE -ne 0) {
    Write-Host ""
    Write-Host "ERROR: Test failed!" -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}

Write-Host ""
Write-Host "============================================================" -ForegroundColor Green
Write-Host "  ALL TESTS PASSED! SUCCESS!" -ForegroundColor Green
Write-Host "============================================================" -ForegroundColor Green
Write-Host ""

Write-Host "Next Steps:" -ForegroundColor Yellow
Write-Host "  1. Run unit tests:     mvn test" -ForegroundColor Gray
Write-Host "  2. Run examples:       java -cp target/classes com.cloudnative.retirement.aws.AWSIntegrationExample" -ForegroundColor Gray
Write-Host "  3. Set up AWS (opt):   .\setup-aws.ps1" -ForegroundColor Gray
Write-Host ""

Write-Host "Documentation:" -ForegroundColor Yellow
Write-Host "  - AWS_QUICK_START.md" -ForegroundColor Gray
Write-Host "  - AWS_INTEGRATION_GUIDE.md" -ForegroundColor Gray
Write-Host "  - AWS_SERVICES_OVERVIEW.md" -ForegroundColor Gray
Write-Host ""
