# Freeze Clip

`FreezeClipModule` reproduces the "jump, freeze in the air, then pass
through the wall" trick for barriers that cannot be built on or crossed
physically.

| Setting | Default | Description |
|---|---|---|
| `Delay` | `1000` ms | How long to hang frozen before clipping (0–3000 ms). Pressing jump again while frozen clips immediately. |
| `Distance` | `3.0` | Maximum wall thickness scanned for the exit point (1.0–5.0 blocks). |
| `Auto` | `true` | Trigger automatically when jumping into a wall with forward held. Off: enabling the module mid-air freezes at once. |

## Sequence

1. Airborne near a wall (Auto) or module enabled mid-air (Auto off) —
   the player freezes: position and velocity are pinned every tick.
2. After `Delay` (or a second jump press) the module scans forward in
   5 cm steps for the first spot where the player box no longer collides
   and teleports there.
3. If no exit exists within `Distance`, the clip is cancelled and the
   player just drops.

This is an outright position clip: strict movement anticheats can see
and reject it, and there is no ground check behind the wall — if there
is void behind it, you fall. For the physical ways through slab
barriers see `slab-pass.md`.
