# Test Bridge — automated module testing with your real client

Unlike a Mineflayer bot, the test subject here is **your actual SkyCore
client**: a small in-client module (`Test Bridge`, category Misc) opens a
line protocol on `127.0.0.1:25599`, and an external driver (script or AI)
walks the player, toggles modules, reads state and takes screenshots.

## Setup

1. Build and start the client: `.\gradlew.bat runClient`
2. Join a test world: singleplayer flat world, or a local offline-mode
   Paper server. (Do **not** automate public servers — anti-bot systems
   and server rules; put the Grim plugin on your local Paper to
   approximate production conditions.)
3. Enable the module **Test Bridge** in the GUI (Misc category).
4. Run the bot from this folder:
   - `python bot.py scaffold` — Scaffold scenario (see below)
   - `python bot.py repl` — interactive console, type protocol commands

Python 3 with stdlib only; nothing to install.

## Protocol (one command per line, answers are one line)

| Command | Answer |
|---|---|
| `PING` | `PONG` |
| `STATE` | one-line JSON: position, yaw/pitch, hp, food, onGround, enabled modules, screenOpen |
| `MODULES` | `OK Name=ON,Other=OFF,...` |
| `MODULE <name> ON\|OFF\|TOGGLE` | `OK Name=ON` |
| `SETTING <module> <setting> <value>` | `OK` (bool/number/mode settings) |
| `KEY forward\|back\|left\|right\|jump\|sneak\|sprint\|attack\|use DOWN\|UP` | `OK` |
| `KEY <name> TAP [millis]` | `OK` (auto-release) |
| `KEYS_UP` | release all keys |
| `LOOK <yaw> <pitch>` | `OK` |
| `CHAT <text>` / `CHAT /command` | `OK` |
| `SCREENSHOT <name>` | `OK <gameDir>/testbridge/shots/<name>.png` |
| `QUIT` | closes the connection |

Errors look like `ERR ...`. Module and setting names are matched
case-insensitively; module names with spaces ("Test Bridge") work.

## Scaffold scenario

`python bot.py scaffold [--distance 15] [--time 60] [--mode Telly]`

Prepare: stand in the test world at the start of the bridge line, on a
solid block with air ahead/below where the bridge should go, facing the
walking direction, blocks in the hotbar/inventory. The bot sets the
Scaffold mode, enables it, holds `forward`, and reports PASS once the
player travelled the requested distance without falling 4+ blocks, with
screenshots before/after under `run/testbridge/shots/`.

## For AI drivers

The protocol is intentionally boring text: connect, send a command,
read one line. `STATE` after every action tells you where the player
ended up; `SCREENSHOT` gives you eyes. Suggested loop: `STATE` →
decide → `KEY`/`LOOK`/`MODULE` → `STATE` → `SCREENSHOT` to verify.

Safety: the socket is loopback-only, but anything running locally can
drive the player while the module is on. Disable `Test Bridge` when you
are done testing.
