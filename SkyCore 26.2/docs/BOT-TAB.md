# BedWars bot — Skycore UI and Workspace prototype

Updated: 2026-10-28. This is an interactive **offline UI demo**, not a network bot.
No server connection, registration, inventory click or party command is sent.
The server remains fixed to `mc.musteryworld.net`.

## Skycore UI integration (2026-10-28)

The Bot tab is now also embedded in **Interface → Skycore**, alongside Autobuy,
Configs and Cosmetics. This is the original `RenderGuiHelper` menu, not Workspace
and not the Swerve menu. Open Render / ClickGUI settings, set Interface to Skycore,
close and reopen ClickGUI, then choose Bot in its navigation dock. The dock works
in all four orientations. The unused module search is replaced by a DEMO label.

`gui/workspace/SkycoreBotPage` adapts the shared bot renderer to Skycore content
geometry and receiving `ThemeManager` colors. It does not create a new Screen or
model. Input hit areas are clipped, navigation/header regions take priority, and
edits are committed/saved when leaving the tab or closing Skycore UI. Menu fade is
restored after bot rendering. Workspace access remains for backwards compatibility;
its model/config is the same, not a second bot fleet. All network actions remain stubs.

## Workspace access (also retained)

ClickGUI → Interface: Workspace → Бот / Bot in the sidebar.
The UI uses Workspace layout, palette, fonts and drawing primitives. Both columns
scroll independently. Start begins a demo session; Scenario step advances it manually.
Stop returns all bots to idle. Nothing advances automatically in the client tick.

The scenario describes server cells 8 → 11, party commands, category hotbar slot 3
and cells 11 → 10, waiting for Eye of Ender, team wool colors and votes 12 / 14.
Resource vote answers are Virtual / Classic; PvP answers are PvP 1.8 / PvP 1.12. The UI shows no team colors until a team-selection menu snapshot is received.
The menu reader is still a stub: demo steps never manufacture team colors.

## Per-bot cards and customization

Each bot has an avatar tile, nickname, status, border and controls:

- Color: cycle through four card accents.
- Size: compact / expanded card.
- Alerts: enable / disable that bot's demo result notifications.
- Test !: simulate a connection failure for that bot (requires a demo session).

These settings affect **local cards only**, not Minecraft skins or server identity.
Appearance is stored by bot slot (1…8) in `skycore/bot.json`.
A session snapshots its names, owner and count. Connection fields cannot be edited
until it stops, avoiding a mismatch between status arrays, names and member counts.

## Party panel

Shows the demo leader, invited owner and members. The first bot becomes leader on
the party-create step; remaining bots appear on party-accept. The owner remains
marked invited: the demo does not claim that the human accepted an invitation.
Before party creation and after stopping, the party is empty. All party data is
explicitly labeled DEMO and will need a real event source later.

## Notifications

A six-second non-modal banner displays demo step results, validation errors,
simulated failures and settings-save failures. These are not reports of real
server actions. Test errors also appear in the journal; later demo steps update
statuses again. Successful step notifications respect per-bot alert preferences.
Global validation / save errors are always visible. Registration passwords are
masked in the journal. The local configuration itself is plain JSON, not encrypted.

## Implementation

- `bot/BotScript`: fixed endpoint, slot/vote descriptions and wool reference colors.
- `bot/BotStage`, `bot/BotStatus`: scenario steps and display states.
- `bot/BotConfig`: configuration and cached nickname strings.
- `bot/BotAppearance`: per-slot card appearance and alert preferences.
- `bot/BedWarsBot`: session snapshot, demo state, party projection, journal and banner.
- `gui/workspace/WorkspaceBotPanel`: rendering and hit regions.
- `gui/workspace/WorkspaceScreen`: navigation, input, clipping and persistence.
- `SkycoreClient`: config load and empty controller tick hook.

UI hit types are 130…138. Card animation keys are in the 5000 range, separate from
Workspace card and setting keys. Hit regions are clipped to the visible content
area so scrolled-out controls cannot intercept header/sidebar clicks.

## Review fixes

- Restore votes by normalizing a cell number, not by treating it as a vote index.
- Snapshot running session names/count/owner; lock identity editing while running.
- Limit nick prefix to 15 ASCII nickname characters, leaving room for suffix 1…8.
- Validate nonempty owner/password before starting the demo.
- Do not reset an input buffer when clicking the same active field.
- Disable hidden module search shortcuts in bot view; commit module edits on entry.
- Clip bot hit regions, keep smooth scrolling in both directions.
- Route both server selection clicks into the preview scenario.
- Mask registration passwords in logs.
- Save bot changes separately from the module profile.

## Build repair and Windows use

The original project had wrapper scripts but no `gradle/wrapper` directory.
Restored the wrapper JAR from the official Gradle v9.7.1 GitHub tag. The distribution
is pinned to 9.7.1 with SHA-256 from the official release asset metadata:
`acd53f1edaf02f1a8ff99879f8a34b302661a057d9b063ae9e35b552f804d20a`.
Removed the machine-specific Java path pointing at the Adoptium *parent directory*.
Use a full JDK 25 and its actual installation folder.

From the repository root in PowerShell:

```powershell
git fetch origin
git switch arena/21c92b96-godweer1
if ($LASTEXITCODE -ne 0) { throw 'Cannot switch branch; check local changes.' }
git pull --ff-only origin arena/21c92b96-godweer1
if ($LASTEXITCODE -ne 0) { throw 'Pull failed.' }
Set-Location '.\SkyCore 26.2'
powershell -NoProfile -ExecutionPolicy Bypass -File .\tools\Build-SkyCore.ps1
```

The script uses JAVA_HOME or discovers an installed JDK 25 under the usual
Adoptium / Java / Microsoft installation folders. An explicit -JdkHome is also
supported. It validates javac major version 25, stops on a failed Gradle build and never copies stale output. The mod JAR
is in `build/libs`; do not install the `-sources.jar`. To copy the result into an
existing launcher profile's mods directory, supply the optional argument:

```powershell
.\tools\Build-SkyCore.ps1 -JdkHome $env:JAVA_HOME -ModsDirectory 'D:\YourMinecraftProfile\mods'
```

No old mods are deleted automatically. Keep only one SkyCore version in that profile.
Use Minecraft 26.2 and matching Fabric Loader/API. To run a development client instead:

```powershell
.\gradlew.bat "-Dorg.gradle.java.home=$env:JAVA_HOME" runClient
```

`git fetch/switch/pull` updates source only; it does not compile or replace a JAR
in an existing launcher installation. The previous branch name pointed elsewhere.

## Verification and limitations

`python tools/check_bot_ui.py` runs eleven **static** regression checks: Java syntax
(tree-sitter), internal panel call arities, vote-loading normalization, fixed
endpoint/slots, password masking wrapper ZIP integrity/configuration, and Skycore navigation/input/palette integration.
Requires `pip install tree-sitter tree-sitter-java`.

These checks do NOT replace javac or in-game testing. Full Gradle compilation,
PowerShell execution and Minecraft rendering have not been verified in the sandbox
(no full JDK/compiler; dependency hosts are restricted).

### Local manual acceptance checks

1. Open Bot in both light and dark palettes; resize the game and change menu scale.
2. Set 8 bots; scroll cards, change each color/size/alerts, close and reopen the GUI.
3. Restart Minecraft and verify appearance and vote choices persisted.
4. Start: names/count remain stable and connection fields are locked; Stop unlocks.
5. Advance through party creation/invite/accept; leader/members change as documented.
6. Test ! changes only the selected card to ERROR, labels notification DEMO, logs it.
7. With that bot's alerts off, Test ! still logs but does not show a bot result banner.
8. Check empty owner/password validation, secret masking in log, clipped controls and
   independence of module settings/search from bot view.

## Sources

Own implementation on existing SkyCore Workspace primitives (no external bot code).
Official wrapper: https://github.com/gradle/gradle/blob/v9.7.1/gradle/wrapper/gradle-wrapper.jar
Official distribution: https://github.com/gradle/gradle-distributions/releases/tag/v9.7.1

## Timeline and readability fixes

- Connection is displayed as step 1 (IDLE is not a displayed numbered step).
- A two-second time-based sine animates both the current marker radius and its halo;
  disabling menu animations freezes the pulse at a stable size.
- The rail is one continuous track; its filled length interpolates to the current
  step using a frame-rate-aware spring. It does not advance without completed steps.
- Vote buttons contain full answers, not inventory slot IDs; internal IDs 12/14 stay
  unchanged for the future menu reader. Unavailable votes say Later.
- Panel text uses the same Inter Medium face as Skycore module names (forWeight(500)),
  with larger small text and matching measurements/ellipsis, without global font edits.
- Team colors come only from acceptTeamMenuColors during TEAM_COLORS with a recognized
  team-menu title. Values are validated, deduplicated and copied; the snapshot persists
  for the session and is cleared on start/stop. No snapshots are currently supplied
  because the network/menu reader is not implemented. Empty state is explicit.


## Обновление управления и голосований

- Время суток: День / Вечер / Ночь → ячейки 11 / 13 / 15.
- Время игры: 20 / 30 / 50 минут → ячейки 11 / 13 / 15. Выбор сохраняется в bot.json.
- Модель игрового хотбара содержит 9 слотов с пустыми позициями 2 и 8; оба предпросмотра хотбаров скрыты из UI.
- Заголовок сценария: «Порядок шагов бота», белый. Убрана приписка «зафиксирован».
- Log в верхней панели открывает журнал с кнопкой «Очистить логи». Party слева сверху открывает состав группы; «Назад» возвращает основной вид.
- В карточках вместо «Размер» и «Тест» доступна индивидуальная остановка. Общая остановка сохранена. Остановленная карточка не активируется следующим ручным шагом.
- Убраны видимые надписи Demo, нижний предпросмотр диапазона ников, встроенные блоки журнала/пати и кнопка ручного шага.
- Сетевые подключения, чтение игровых меню и управление настоящими ботами **не реализованы**. Названия кнопок не означают готовность серверной логики.
- Проверка: 12 статических тестов; это не javac и не визуальная проверка Minecraft.
