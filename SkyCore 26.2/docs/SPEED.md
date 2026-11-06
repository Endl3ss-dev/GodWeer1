# Speed: перенос спидов из клиентов-источников

Модуль: `Speed` (`net.skycore.client.module.movement.SpeedModule`), категория Movement.
Имя режима = клиент-источник + имя режима источника: `Nuclear [Collision]`, `Phobia [Grim]`, …

## Что уже перенесено (1:1 с источником)

| Источник | Режимы | Файл источника |
|---|---|---|
| Nuclear | Collision, Matrix, MetaHvH, HolyWorld | `Nuclear/.../module/impl/movement/Speed.java` (106) |
| Rockstar | Сущности | `Rockstar/.../module/movement/Speed.java` (125) |
| Phobia | Vanilla, Grim, FunTime, Funtime Elytra, HolyWorld | `Phobia/.../modulesystem/impl/movement/Speed.java` (151) |
| AlphaDLC | RW, Collision | `AlphaDLC/.../module/list/movement/Speed.java` (183) |
| Releon | Vanilla, Grim, ReallyWorld, HolyWorld, FunTime, SpookyTime | `Releon/.../api/module/impl/movement/Speed.java` (388) |
| SkyCore (legacy) | RW, Collision | `SkyCore/.../modules/impl/movement/Speed.java` (273) |
| Swerve | Множитель | `Swerve/.../modules/impl/movement/Speed.java` (125) |

Настройки источников сохранены вместе с диапазонами и видимостью (скрыты, пока не выбран режим клиента).

## Осталось перенести

| Источник | Строк | Что нужно дополнительно |
|---|---|---|
| Private | 252 | MoveUtil.setSpeed/forward/speed/speedSqrt, `CPlayerPacket.PositionPacket` |
| Wild (`3`) | 239 | `MovementPhysics.process/handle`, цель `AttackAura.textureRun` |
| Sadnes | 443 | MoveEvent (подмена скорости до интеграции), `guiScale`-таймер, sequenced-пакеты (`PacketSender`), `RotationComponent`, elytra-свап через `PlayerInventoryUtil`/`InventoryTasks` |
| LightBeta | 532 | `SwapInventory`/`InventoryTasks` (фазовый свап элитры), `RotationTask`, `TimerController` |
| Minced | 868 | ещё не прочитан |

## Технические решения (26.2)

- `mc.timer.timerSpeed` → `SpeedTimer` поверх `ResetSpeedHelper` (его читает `DeltaTrackerTimerCriticalTickDurationMixin`).
  У Releon приоритет как в `DeltaTrackerTimerMixin` источника: активный фазовый цикл перекрывает таймер.
- `jumpMovementFactor` (1.8) → `SpeedFlyingSpeed` + `PlayerGetFlyingSpeedMixin`
  (`Player#getFlyingSpeed()` — та самая величина, которую 1.8 брала из jumpMovementFactor).
- `PlayerTravelEvent` источников (pre-travel) → `LivingEntitySpeedTravelMixin` (`travel` HEAD) → `SpeedModule#onTravelPre`.
- `mc.player.jump()` → `jumpFromGround()`; `addVelocity` → `push(x,y,z)`; `startGliding` → `StartFallFlyingHelper.startFallFlying()`; `stopGliding` → `stopFallFlying()`.
- Свап элитры (Phobia Funtime Elytra) — только старт полёта: сам свап брони появится вместе с переносом LightBeta/Sadnes-подсистем.
