# CRITICAL FIX: Global package rename from 'model' to 'models'
# This fixes ~500 compilation errors from the domain reorganization

$ErrorActionPreference = "Stop"

Write-Host "Genesis Protocol Build Fixer - Package Rename" -ForegroundColor Cyan

# Define the pattern and replacement  
$oldPackage = "dev.aurakai.auraframefx.model."
$newPackage = "dev.aurakai.auraframefx.models."

# Find all Kotlin files
$kotlinFiles = Get-ChildItem -Path "app\src\main\java" -Filter "*.kt" -Recurse

Write-Host "Found $($kotlinFiles.Count) Kotlin files to process..."

$filesChanged = 0
$totalReplacements = 0

foreach ($file in $kotlinFiles) {
    $content = Get-Content $file.FullName -Raw -Encoding UTF8
    $originalContent = $content
    
    # Replace the package imports
    $content = $content -replace [regex]::Escape($oldPackage), $newPackage
    
    if ($content -ne $originalContent) {
        # Count how many replacements were made
        $matchCount = ([regex]::Matches($originalContent, [regex]::Escape($oldPackage))).Count
        $totalReplacements += $matchCount
        $filesChanged++
        
        # Write back to file
        Set-Content -Path $file.FullName -Value $content -Encoding UTF8 -NoNewline
        Write-Host "Fixed $($file.Name) - $matchCount replacements" -ForegroundColor Green
    }
}

Write-Host ""
Write-Host "COMPLETE!" -ForegroundColor Green
Write-Host "Files modified: $filesChanged"
Write-Host "Total package path fixes: $totalReplacements"
