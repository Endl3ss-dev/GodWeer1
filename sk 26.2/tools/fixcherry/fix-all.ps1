# One command that brings a broken local checkout back to a runnable client.
#
#   powershell -ExecutionPolicy Bypass -File tools\fixcherry\fix-all.ps1
#
# Steps:
#   1. fetch origin and hard-switch the local branch to it
#      (git checkout -- . does NOT clear unmerged "UU" entries - only -f does)
#   2. stop stale Gradle daemons
#   3. verify no conflict markers and that both auraTest tasks exist
#   4. run .\gradlew runClient
#
# Nothing is committed. The SHA you leave behind is printed so the old state
# can be restored with: git reset --hard <sha>
$ErrorActionPreference = 'Stop'
$SK = Split-Path -Parent (Split-Path -Parent (Split-Path -Parent $PSCommandPath))
Set-Location $SK
$branch = 'arena/b360845a-godweer1'

function Step($n, $text) { Write-Host "`n[$n] $text" -ForegroundColor Cyan }
function Die($text) { Write-Host "`nFAILED: $text" -ForegroundColor Red; exit 1 }

Write-Host "project: $SK"

# ---------------------------------------------------------------- 1. git
Step 1 'git: fetch and switch to the PR branch'
$old = (git rev-parse HEAD).Trim()
Write-Host "  leaving HEAD $old (restore: git reset --hard $old)"
$gitDir = (git rev-parse --git-dir).Trim()
if (Test-Path (Join-Path $gitDir 'CHERRY_PICK_HEAD')) { git cherry-pick --quit }
if (Test-Path (Join-Path $gitDir 'MERGE_HEAD'))       { git merge --quit }
git fetch origin $branch
if ($LASTEXITCODE -ne 0) { Die "git fetch origin $branch" }
$localOnly = @(git rev-list "origin/$branch..HEAD" 2>$null)
if ($localOnly.Count -gt 0) {
    Write-Host "  note: $($localOnly.Count) local commit(s) are not on origin/$branch and will be dropped:" -ForegroundColor Yellow
    $localOnly | ForEach-Object { Write-Host "    $_ $(git log -1 --format=%s $_)" }
}
git switch -f -C $branch "origin/$branch"
if ($LASTEXITCODE -ne 0) { Die "git switch -f -C $branch origin/$branch" }
git status --short

# ---------------------------------------------------------------- 2. daemons
Step 2 'gradle: stop stale daemons'
& .\gradlew --stop 2>&1 | ForEach-Object { "  $_" }
$env:JAVA_TOOL_OPTIONS = '-Dfile.encoding=UTF-8'
$env:GRADLE_OPTS = '-Dfile.encoding=UTF-8'

# ---------------------------------------------------------------- 3. checks
Step 3 'verify sources'
$bad = @()
Get-ChildItem -Recurse -Include *.java,*.gradle,*.md -File |
    Where-Object { $_.FullName -notmatch '\\build\\|\\.gradle\\' } |
    ForEach-Object { if (Select-String -Path $_.FullName -Pattern '^<<<<<<< |^>>>>>>> ' -Quiet) { $bad += $_.FullName } }
if ($bad.Count -gt 0) { $bad | ForEach-Object { Write-Host "  markers in $_" }; Die 'conflict markers present' }
Write-Host '  conflict markers: 0'
$check = Select-String -Path build.gradle -Pattern 'testFreezeClip' -Quiet
$bridge = Select-String -Path build.gradle -Pattern 'testTestBridge' -Quiet
Write-Host ("  testFreezeClip task : " + [bool]$check)
Write-Host ("  testTestBridge task : " + [bool]$bridge)
if (-not $check -or -not $bridge) { Die 'build.gradle is missing a test task' }
$rt = Select-String -Path 'src\main\java\net\skycore\client\module\misc\TestBridgeModule.java' -Pattern 'getMainRenderTarget' -Quiet
if ($rt) { Die 'TestBridgeModule still calls Minecraft#getMainRenderTarget' }
Write-Host '  TestBridge render target: GameRenderer'

# ---------------------------------------------------------------- 4. run
Step 4 'launching the client'
& .\gradlew runClient
exit $LASTEXITCODE
