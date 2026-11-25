# Puts the local checkout back onto the PR branch and throws away the broken
# cherry-pick leftovers (conflict markers in build.gradle, scaffold.md,
# ScaffoldModule.java, ScaffoldModuleTest.java).
#
# WHY THIS IS NEEDED: `git checkout -- .` and `git checkout HEAD -- <file>` do
# NOT clear "UU" (unmerged) entries, so the markers survived them. Only a hard
# checkout / reset does.
#
# WHAT IT WRITES: the working tree and the local branch <branch> are reset to
# origin/<branch>. No commits, no new branches, nothing outside the repo.
# Before resetting it prints the SHA you are leaving behind (reflog keeps it
# too), so the current state can be restored with: git reset --hard <sha>
#
# Usage (from the "sk 26.2" folder):
#   powershell -ExecutionPolicy Bypass -File tools\fixcherry\reset-to-branch.ps1
$ErrorActionPreference = 'Stop'
Set-Location (Split-Path -Parent (Split-Path -Parent (Split-Path -Parent $PSCommandPath)))

$branch    = 'arena/b360845a-godweer1'
$remote    = 'origin'
$remoteRef = "$remote/$branch"

Write-Host "repo: $(Get-Location)"
git rev-parse --is-inside-work-tree | Out-Null

# Show what is about to be thrown away.
$oldHead = (git rev-parse HEAD).Trim()
Write-Host "current HEAD: $oldHead  (restore with: git reset --hard $oldHead)"
$status = @(git status --porcelain)
Write-Host ("changed/conflicted paths: " + $status.Count)
$status | ForEach-Object { Write-Host "  $_" }

git fetch $remote $branch
if ($LASTEXITCODE -ne 0) { throw "git fetch $remote $branch failed" }

$localOnly = @(git rev-list "$remoteRef..$branch" 2>$null)
if ($localOnly.Count -gt 0) {
    Write-Host "`nWARNING: the local branch has $($localOnly.Count) commit(s) not on $remoteRef :"
    $localOnly | ForEach-Object { Write-Host "  $_ ($(git log -1 --format=%s $_))" }
    Write-Host "They will NOT survive the reset. Press Ctrl+C to stop, or Enter to continue."
    Read-Host | Out-Null
}

$gitDir = (git rev-parse --git-dir).Trim()
if (Test-Path (Join-Path $gitDir 'CHERRY_PICK_HEAD')) { git cherry-pick --quit }
if (Test-Path (Join-Path $gitDir 'MERGE_HEAD'))       { git merge --quit }

# The actual fix: a hard switch clears the unmerged entries.
git switch -f -C $branch $remoteRef
if ($LASTEXITCODE -ne 0) { throw "git switch -f -C $branch $remoteRef failed" }

Write-Host "`n--- git status ---"
git status --short
Write-Host "`n--- checks ---"
$markers = @(Select-String -Path build.gradle,docs\scaffold.md -Pattern '<<<<<<<|^>>>>>>>' -ErrorAction SilentlyContinue)
Write-Host ("conflict markers    : " + $markers.Count)
Write-Host ("testFreezeClip task : " + [bool](Select-String -Path build.gradle -Pattern 'testFreezeClip' -Quiet))
Write-Host ("testTestBridge task : " + [bool](Select-String -Path build.gradle -Pattern 'testTestBridge' -Quiet))
git log --oneline -1
Write-Host "`nNow run: .\gradlew runClient"
