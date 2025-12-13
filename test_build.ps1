Write-Host "🚀 Testing KSP Build..." -ForegroundColor Cyan
Set-Location "C:\Final\A.u.r.a.K.a.i-Reactive_Intelligence-"

Write-Host "`n📦 Running KSP Debug Kotlin..." -ForegroundColor Yellow
$output = & .\gradlew.bat :app:kspDebugKotlin --console=plain 2>&1

# Check for success
if ($output -match "BUILD SUCCESSFUL") {
    Write-Host "`n✅ BUILD SUCCESS!" -ForegroundColor Green
    Write-Host "All KSP fixes working!" -ForegroundColor Green
    exit 0
} else {
    Write-Host "`n❌ Build failed. Last 30 lines:" -ForegroundColor Red
    $output | Select-Object -Last 30
    exit 1
}
