#!/usr/bin/env python3
"""Test bot for the SkyCore Test Bridge (module 'Test Bridge' must be ON).

The bridge is a plain line protocol on 127.0.0.1:25599, deliberately easy
for both humans and AI drivers:

    STATE                      -> one-line JSON snapshot
    MODULE Scaffold ON         -> OK Scaffold=ON
    SETTING Scaffold Mode Telly
    KEY forward DOWN / KEY forward UP / KEYS_UP
    LOOK 180 45
    SCREENSHOT bridge_start    -> OK <path to png>
    CHAT /gamemode creative

Usage:
    python bot.py scaffold [--distance 15] [--time 60] [--mode Telly]
    python bot.py repl         (interactive console: type bridge commands)

The scaffold scenario: enables Scaffold, walks forward and watches the
state stream. PASS when the player travelled >= --distance blocks without
falling more than 4 blocks below the start height; FAIL otherwise.
Prepare the world yourself first: singleplayer flat world or a local
offline-mode Paper, stand at the start of the bridge line on a solid
block, face the walking direction.
"""

import argparse
import json
import math
import socket
import sys
import time


class Bridge:
    def __init__(self, host="127.0.0.1", port=25599):
        self.sock = socket.create_connection((host, port), timeout=15)
        self.file = self.sock.makefile("r", encoding="utf-8")
        self.banner = self.file.readline().strip()

    def cmd(self, line):
        self.sock.sendall((line.strip() + "\n").encode("utf-8"))
        return self.file.readline().strip()

    def state(self):
        return json.loads(self.cmd("STATE"))

    def close(self):
        try:
            self.sock.sendall(b"QUIT\n")
        except OSError:
            pass
        self.sock.close()


def scenario_scaffold(bridge, distance, timeout, mode):
    state = bridge.state()
    if not state.get("inWorld"):
        print("FAIL: player is not in a world (inWorld=false)")
        return 1
    print("start state:", json.dumps(state, ensure_ascii=False))
    print(bridge.cmd(f"SETTING Scaffold Mode {mode}"))
    print(bridge.cmd("MODULE Scaffold ON"))
    print(bridge.cmd("SCREENSHOT scenario_start"))
    start = bridge.state()
    start_x, start_y, start_z = start["x"], start["y"], start["z"]

    print(bridge.cmd("KEY forward DOWN"))
    deadline = time.time() + timeout
    result, reason = 1, "timeout"
    try:
        while time.time() < deadline:
            time.sleep(0.25)
            s = bridge.state()
            if not s.get("inWorld"):
                reason = "left the world"
                break
            travelled = math.hypot(s["x"] - start_x, s["z"] - start_z)
            if s["y"] < start_y - 4:
                reason = f"fell (y={s['y']:.1f} < {start_y - 4:.1f})"
                break
            if travelled >= distance:
                result, reason = 0, f"travelled {travelled:.1f} blocks"
                break
    finally:
        print(bridge.cmd("KEYS_UP"))
        print(bridge.cmd("SCREENSHOT scenario_end"))
        print(bridge.cmd("MODULE Scaffold OFF"))
    end = bridge.state()
    print("end state:", json.dumps(end, ensure_ascii=False))
    print(("PASS" if result == 0 else "FAIL") + f": {reason}")
    return result


def repl(bridge):
    print("banner:", bridge.banner)
    print("type bridge commands, Ctrl+C to exit")
    try:
        while True:
            line = input("> ")
            if line.strip():
                print(bridge.cmd(line))
    except (KeyboardInterrupt, EOFError):
        pass
    return 0


def main():
    parser = argparse.ArgumentParser(description="SkyCore Test Bridge bot")
    parser.add_argument("command", choices=["scaffold", "repl"])
    parser.add_argument("--host", default="127.0.0.1")
    parser.add_argument("--port", type=int, default=25599)
    parser.add_argument("--distance", type=float, default=15.0)
    parser.add_argument("--time", type=float, default=60.0)
    parser.add_argument("--mode", default="Telly", help="Scaffold mode to test")
    args = parser.parse_args()

    try:
        bridge = Bridge(args.host, args.port)
    except OSError as e:
        print(f"cannot connect to Test Bridge on {args.host}:{args.port}: {e}")
        print("is the client running with module 'Test Bridge' enabled?")
        return 2
    try:
        print("banner:", bridge.banner)
        if args.command == "scaffold":
            return scenario_scaffold(bridge, args.distance, args.time, args.mode)
        return repl(bridge)
    finally:
        bridge.close()


if __name__ == "__main__":
    sys.exit(main())
