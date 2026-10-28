[CmdletBinding()]
param(
    [string]$JdkHome = $env:JAVA_HOME,
    [string]$ModsDirectory
)
$ErrorActionPreference = 'Stop'
$project = Split-Path -Parent $PSScriptRoot
if (-not $JdkHome -or -not (Test-Path (Join-Path $JdkHome 'bin/javac.exe'))) {
    $candidate = @('C:\Program Files\Eclipse Adoptium', 'C:\Program Files\Java', 'C:\Program Files\Microsoft') |
        Where-Object { Test-Path -LiteralPath $_ } |
        ForEach-Object { Get-ChildItem -LiteralPath $_ -Directory -Filter '*25*' } |
        Where-Object { Test-Path (Join-Path $_.FullName 'bin/javac.exe') } |
        Select-Object -First 1
    if (-not $candidate) {
        throw 'Full JDK 25 not found. Pass -JdkHome "C:\...\jdk-25..." (not its parent directory).'
    }
    $JdkHome = $candidate.FullName
}
$version = & (Join-Path $JdkHome 'bin/javac.exe') -version 2>&1
if ($LASTEXITCODE -ne 0 -or "$version" -notmatch '^javac 25(?:[.\s]|$)') {
    throw "Full JDK 25 is required. Found: $version"
}
Write-Host "Using JDK: $JdkHome"
$env:JAVA_HOME = $JdkHome
Push-Location $project
try {
    # Override any machine-specific Gradle Java home without modifying global settings.
    & .\gradlew.bat "-Dorg.gradle.java.home=$JdkHome" --no-daemon clean build
    if ($LASTEXITCODE -ne 0) { throw "Gradle build failed (exit $LASTEXITCODE). No JAR copied." }
    $jars = @(Get-ChildItem -Path 'build/libs' -Filter '*.jar' |
        Where-Object { $_.Name -notmatch '-(sources|javadoc|dev)\.jar$' })
    if ($jars.Count -ne 1) { throw 'Expected exactly one distributable JAR in build/libs.' }
    Write-Host "Built: $($jars[0].FullName)"
    if ($ModsDirectory) {
        if (-not (Test-Path -LiteralPath $ModsDirectory -PathType Container)) {
            throw "Mods folder does not exist: $ModsDirectory"
        }
        # Do not remove unrelated or older mods silently.
        Copy-Item -LiteralPath $jars[0].FullName -Destination $ModsDirectory -Force
        Write-Host "Copied to: $ModsDirectory"
        Write-Warning 'Keep only one SkyCore version in this launcher profile. Requires matching Minecraft/Fabric.'
    }
} finally {
    Pop-Location
}
