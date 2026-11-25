# Resolves the leftover cherry-pick of 744c16ea ("Add Freeze Clip module") on the
# local checkout: removes the conflict markers from build.gradle, keeps BOTH test
# tasks (testFreezeClip and testTestBridge), clears the cherry-pick state and
# stages the file. It does not commit anything.
#
# Usage (from the "sk 26.2" folder):
#   powershell -ExecutionPolicy Bypass -File tools\fixcherry\fix-cherry-pick.ps1
$ErrorActionPreference = 'Stop'
Set-Location (Split-Path -Parent (Split-Path -Parent (Split-Path -Parent $PSCommandPath)))

$gradle = 'build.gradle'
if (-not (Test-Path $gradle)) { throw "build.gradle not found in $(Get-Location)" }

# Preferred text: the version of the file on this branch (no conflict markers).
$canonical = @'
tasks.register('testFreezeClip', JavaExec) {
    dependsOn 'auraTestClasses'
    classpath = sourceSets.auraTest.runtimeClasspath
    mainClass = 'net.skycore.client.module.movement.FreezeClipModuleTest'
}
tasks.register('testTestBridge', JavaExec) {
    dependsOn 'auraTestClasses'
    classpath = sourceSets.auraTest.runtimeClasspath
    mainClass = 'net.skycore.client.module.misc.TestBridgeModuleTest'
}
tasks.named('check') { dependsOn 'testAuraClicks', 'testRockstarAura', 'testScaffold', 'testSlabPass', 'testFreezeClip', 'testTestBridge' }
'@

$raw = Get-Content -Raw -Encoding UTF8 $gradle
if ($raw -notmatch '<<<<<<<') {
    Write-Host "build.gradle: no conflict markers, left untouched."
} else {
    # Take the version git already has on this branch when the file is clean there.
    $head = (git show HEAD:build.gradle 2>$null) -join "`n"
    if ($LASTEXITCODE -eq 0 -and $head -and $head -notmatch '<<<<<<<') {
        [System.IO.File]::WriteAllText((Resolve-Path $gradle), ($head + "`n"), (New-Object System.Text.UTF8Encoding($false)))
        Write-Host "build.gradle: restored from HEAD (no conflict markers)."
    } else {
        $lines = $raw -split "`r?`n"
        $out = New-Object System.Collections.Generic.List[string]
        $inConflict = $false
        foreach ($line in $lines) {
            if ($line -match '^<<<<<<<') { $inConflict = $true; continue }
            if ($line -match '^=======' -and $inConflict) { continue }
            if ($line -match '^>>>>>>>' -and $inConflict) { $inConflict = $false; continue }
            if ($inConflict) { continue }
            $out.Add($line)
        }
        $text = ($out -join "`r`n")
        $text = $text -replace "tasks\.named\('check'\)\s*\{\s*dependsOn[^\r\n]*\}", "tasks.named('check') { dependsOn 'testAuraClicks', 'testRockstarAura', 'testScaffold', 'testSlabPass', 'testFreezeClip', 'testTestBridge' }"
        if ($text -notmatch "tasks\.register\('testTestBridge'") {
            $marker = "tasks.named('check')"
            $text = $text.Replace($marker, ("tasks.register('testTestBridge', JavaExec) {`r`n    dependsOn 'auraTestClasses'`r`n    classpath = sourceSets.auraTest.runtimeClasspath`r`n    mainClass = 'net.skycore.client.module.misc.TestBridgeModuleTest'`r`n}`r`n" + $marker))
        }
        [System.IO.File]::WriteAllText((Resolve-Path $gradle), $text, (New-Object System.Text.UTF8Encoding($false)))
        Write-Host "build.gradle: conflict markers removed, both test tasks kept."
    }
}

$gitDir = (git rev-parse --git-dir).Trim()
if (Test-Path (Join-Path $gitDir 'CHERRY_PICK_HEAD')) {
    git cherry-pick --quit
    Write-Host "cherry-pick state cleared."
}

git add $gradle
Write-Host "`n--- git status ---"
git status --short
Write-Host "`n--- checks ---"
Write-Host ("conflict markers left : " + ((Select-String -Path $gradle -Pattern '<<<<<<<|=======|>>>>>>>' -AllMatches | Measure-Object).Count))
Write-Host ("testFreezeClip present : " + [bool](Select-String -Path $gradle -Pattern 'testFreezeClip' -Quiet))
Write-Host ("testTestBridge present : " + [bool](Select-String -Path $gradle -Pattern 'testTestBridge' -Quiet))
Write-Host "`nNow run: .\gradlew runClient"
