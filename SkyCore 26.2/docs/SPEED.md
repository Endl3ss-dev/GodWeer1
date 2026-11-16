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

## Speed v4 — отдельный модуль (Nuclear «Old Grim Bypass»)

`net.skycore.client.module.movement.SpeedV4Module`, имя в GUI — `Speed v4`, категория Movement,
регистрируется в `ModuleManager`, подписывается в `.ac speed` как `Speed v4 (Old Grim)`.

Полная замена обеих предыдущих ревизий этого модуля («Matrix Silent» с lowhop/спуфом земли и
«Matrix Smooth Silent» с мягким стягиванием): от них не осталось ни настроек, ни логики.
Отличие от семейства Matrix по замыслу источника — старый Grim злой на длинное окно буста и на
продолжение буста после отката, поэтому hop ровно ванильный (0.42), буст живёт только два
первых воздушных тика, а дальше модуль сам тормозит раньше, чем это заметит предсказание.

| Настройка | Диапазон | По умолчанию | Из источника |
|---|---|---|---|
| `Speed` | 0.27…0.38, шаг 0.001 | 0.319 | `speed` |
| `Hop Y` | 0.40…0.42, шаг 0.0001 | 0.42 | `hopY` (0.41999998688697815 = `float 0.42f`) |
| `Timer` | 1.0…1.06, шаг 0.001 | 1.0 (выключен) | `timer`, только тик `airTicks == 1 && hopped` |
| `Авто-прыжок` | bool | вкл | `autojump` («Авто прыжок»), условие `groundTicks >= 1` |
| `Damage Boost` | bool | вкл | `damageBoost` (+13 % при `hurtTime` 1…5) |
| `Grim Mode` | bool | вкл | `grimMode`; выкл = ровный `base` без лестницы |
| `Air Friction` | 0.85…1.0, шаг 0.001 | 0.91 | было захардкожено (`* 0.91` за окном) |
| `Setback Cooldown` | 0…16 тиков | 8 | было захардкожено (`setbackCool = 8`) |
| `Setback Speed` | 0.10…0.40 | 0.21 | было захардкожено (`setSpeed(0.21f)` в кулдауне) |

Лестница `Grim Mode` (тики после отрыва): `1` — `base * 1.07` + `Timer`; `2` — `base * 1.02`;
`3…5` — `base`; `6…9` — `base * 0.97`; `10+` — `base * 0.89` и принудительное трение
`Air Friction`.

Что изменено против источника сознательно:

- **`Air Friction` перенесён после `setSpeed`.** В источнике множитель 0.91 применялся к
  `deltaMovement` до `MoveUtil.setSpeed(...)`, а `setSpeed` перезаписывает `x`/`z` целиком —
  строки `setDeltaMovement(mx * 0.91, ...)` были мёртвым кодом. Здесь трение считается после
  установки скорости (замысел автора): за окном выходит `0.89 * 0.91 ≈ 0.81 * base`.
- `mc.timer.timerSpeed = tmr` (он писался на каждом тике, включая `tmr = 1.0`) → `SpeedTimer`:
  нейтральное 1.0 освобождает таймер, а не перехватывает его у `Timer`-модуля.
- `onSetback()` вызывается из `PacketReceiveEvent` + `ClientboundPlayerPositionPacket` (в
  источнике — «пока можно вручную или потом привяжем к пакету»), метод оставлен public.
  Счётчик сетов ведёт `AntiCheatDetector`.
- В кулдауне `setSpeed` только при реальном движении — иначе `setSpeed(0.21)` на месте стирал
  инерцию сразу после отката.
- `hasEffect(...) + getEffect(...)` → один `getEffect(...)` с проверкой на `null`;
  `MobEffects.MOVEMENT_SPEED` / `MOVEMENT_SLOWDOWN` → `MobEffects.SPEED` / `SLOWNESS` (26.2).
- Проверка `if (apply > 0)` сохранена как в источнике, хотя ни одна ветка не оставляет `apply`
  нулевым (минимум `Speed` = 0.27).
