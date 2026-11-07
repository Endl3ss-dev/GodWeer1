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

## Speed v4 — отдельный модуль (Nuclear «Matrix Smooth Silent»)

`net.skycore.client.module.movement.SpeedV4Module`, имя в GUI — `Speed v4`, категория Movement,
`INSTANCE`-модуль, регистрируется в `ModuleManager`. Это полная замена первой версии порта
(«Matrix Silent» с lowhop, спуфом земли и лестницей таймера): от неё не осталось ни настроек,
ни логики — вместо жёсткой подмены трения и спуфа `onGround` здесь мягкое сглаживание скорости.

Переносится не `Speed.java` из репозитория Nuclear (он уже покрыт режимом `Nuclear [Matrix]` в
модуле `Speed`), а его вариация «Matrix Smooth Silent»: буст только в первый воздушный тик,
hop с пониженной Y и `Smooth Friction`, которое стягивает фактический модуль к целевой скорости,
вместо того чтобы дописывать сверху.

| Настройка | Диапазон | По умолчанию | Из источника |
|---|---|---|---|
| `Speed` | 0.26…0.42, шаг 0.001 | 0.322 | `speed` |
| `Hop Y` | 0.38…0.42, шаг 0.001 | 0.405 | `hopY` |
| `Timer` | 1.0…1.08, шаг 0.001 | 1.0 (выключен) | `timerBoost`, только тик `airTicks == 1` |
| `Авто-прыжок` | bool | вкл | `autojump` («Авто прыжок») |
| `Damage Boost` | bool | вкл | `damageBoost` (+14 % при `hurtTime` 1…6) |
| `Smooth Friction` | bool | вкл | `smooth` (срабатывает с `airTicks > 2`) |
| `Smooth Epsilon` | 0…0.1 | 0.02 | было захардкожено (`apply + 0.02`) |
| `Setback Cooldown` | 0…12 тиков | 6 | было захардкожено (`setbackCool = 6`) |
| `Setback Speed` | 0.10…0.40 | 0.23 | было захардкожено (`setSpeed(0.23f)` в кулдауне) |

Как это читается в коде: `airTicks` считается по `player.onGround()`, причём прыжок в тике на
земле ещё не переводит игрока в воздух (флаг пересчитывает `move()`), поэтому буст приходит
следующим тиком с `airTicks == 1` — как и в источнике. `Smooth Friction` полезна не «на всякий
случай»: `SpeedAction.setSpeed` (как и `MoveUtil.setSpeed` источника) нормализует ввод в ±1 по
обеим осям, и диагональ даёт `speed * sqrt(2)` — именно этот хвост и срезается до `apply`.

Что изменено против источника сознательно:

- `mc.timer.timerSpeed` → `SpeedTimer`. Нейтральное 1.0 таймер не перехватывает, а освобождает,
  иначе модуль заглушал бы `Timer` на каждом тике.
- Сет сервера ловится по-настоящему: `PacketReceiveEvent` + `ClientboundPlayerPositionPacket`
  вызывает `notifySetback()` (в источнике был только public-метод с пометкой «пока без packet
  detect», сброс предполагался ручным). Метод остался public — его можно дёргать и вручную.
  Счётчик сетов ведёт `AntiCheatDetector`, а `.ac speed` подписывает сегмент как
  `Speed v4 (Matrix Smooth Silent)` — иначе тики со спидом попали бы в базлайн.
- Во время кулдауна `setSpeed` вызывается только если игрок реально движется: на месте он
  обнулил бы инерцию ровно после отката, где её нужно удержать.
- `hasEffect(...) + getEffect(...)` → один `getEffect(...)` с проверкой на `null`;
  `MobEffects.MOVEMENT_SPEED` / `MOVEMENT_SLOWDOWN` → `MobEffects.SPEED` / `SLOWNESS` (имена 26.2).
- `lastMotionX` / `lastMotionZ` из источника удалены — записывались, но не читались.
