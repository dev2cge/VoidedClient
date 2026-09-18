param(
    [Parameter(Mandatory = $true)][string]$Jar,
    [string]$Checksums = "SHA256SUMS.txt"
)

$resolvedJar = (Resolve-Path $Jar).Path
$name = Split-Path $resolvedJar -Leaf
$actual = (Get-FileHash $resolvedJar -Algorithm SHA256).Hash.ToLowerInvariant()
$line = Get-Content $Checksums | Where-Object { $_ -match "\s$name$" } | Select-Object -First 1

if (-not $line) { throw "No checksum entry found for $name" }
$expected = ($line -split '\s+')[0].ToLowerInvariant()
if ($actual -ne $expected) { throw "Checksum mismatch for $name`nExpected: $expected`nActual:   $actual" }

Write-Host "Verified $name" -ForegroundColor Green
Write-Host "SHA-256: $actual"
