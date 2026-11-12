# Velocity — все обходы отбрасывания из клиентов GodWeer

Модуль `Velocity` (категория **MOVEMENT**) собирает обходы отбрасывания из всех клиентов
репозитория, где есть модуль `Velocity`/`NoVelocity`/`VelocityControl`. Каждый вариант работает так
же, как в клиенте-источнике, и подписан его именем: `Клиент [Режим клиента]`.

## Источники и режимы (55)

| Режим в SkyCore | Клиент-источник | Файл-источник | Версия |
|---|---|---|---|
| `Wild [Vanilla]`, `Wild [Lag]`, `Wild [Funtime]` | Wild (`3`) | `modules/combat/Velocity.java` | 1.21.8 |
| `AlphaDLC [Cancel]` | AlphaDLC | `module/list/combat/Velocity.java` | 1.21.x |
| `LightBeta [Cancel]`, `LightBeta [Jump]`, `LightBeta [HwTest]` | LightBeta | `modules/impl/combat/NoVelocity.java` | 1.21.x |
| `Minced [Обычный]`, `Minced [Грим]` | Minced | `module/combat/NoVelocity.java` | 1.21.x |
| `Nuclear [Cancel]`, `Nuclear [Grim]`, `Nuclear [Grim Air]` | Nuclear | `module/impl/combat/Velocity.java` | 1.21.x |
| `Phobia [New Grim]`, `Phobia [Old Grim]`, `Phobia [Matrix]`, `Phobia [Vanilla]` | Phobia | `modulesystem/impl/rage/Velocity.java` | 1.21.11 |
| `Private [Packet]`*, `[Matrix]`, `[Vulcan]`, `[Intave]`, `[AAC Push]`*, `[AAC v4]`, `[NCP]`, `[Reverse]`, `[Grim]`* | Private (Combat) | `module/impl/combat/Velocity.java` | 1.21.x |
| `PrivateMove [Grim]`, `[Grim Light]`, `[Grim Heavy]`, `[Grim Stable]`, `[Grim Advanced]`, `[Matrix]`, `[Vulcan]`, `[Intave]`, `[Verus]`, `[NCP]`, `[AAC]`, `[Spartan]`, `[Vanilla]`, `[Custom]`, `[Zero]`, `[Reduce]`, `[Jump Reset]` | Private (Movement) | `module/impl/movement/Velocity.java` | 1.21.x |
| `Releon [New Grim]`, `Releon [Old Grim]`, `Releon [Matrix]`, `Releon [Normal]` | Releon | `module/impl/combat/Velocity.java` | 1.21.11 |
| `Rockstar [Отмена]`, `Rockstar [Компенсация]`, `Rockstar [Изменение]` | Rockstar | `module/combat/Velocity.java` | 1.21.4 |
| `Sadnes [normal]`, `Sadnes [riding_spoof]`, `Sadnes [grim_standing]`, `Sadnes [jump_reset]` | Sadnes | `feature/impl/combat/VelocityControl.java` | 1.21.11 |
| `SkyCoreLegacy [Обычный]`, `SkyCoreLegacy [RW]` | старый SkyCore | `modules/impl/combat/NoVelocity.java` | 1.21.x |

\* — ветки исходного `switch` у Private (Combat), которых нет в его списке режимов. Перенесены
отдельными режимами, чтобы не терять код; слайдеры «Горизонталь %»/«Вертикаль %» в источнике
использует только `Packet`.

Клиенты без модуля-обхода отбрасывания: `Arcanic`, `Decide`, `Destra`, `Kimiko`, `PatPat`,
`DeobfPatPat`, `Pouch` (у него отмена скорости сделана общим миксином без режимов),
`Prestige`, `Swerve` (там только сборщик ACD), `SystemSRC 26.2`, `4`, `Новая папка (6)`.

Отдельный модуль Rockstar `Knockback Tweaks` — про отправку ударов (фальшивый спринт, look-пакет),
а не про приём скорости; в Velocity не входит.

## Настройки

Один плоский список, у каждого обхода свои значения — общих слайдеров нет:

- `Режим` — все 55 вариантов списком;
- `Minced: пропускать при активных спидах` (только `Minced [Грим]`);
- `Nuclear: вырубить после флага` (только режимы Nuclear);
- `Private: горизонталь %` / `вертикаль %` (только `Private [Packet]`);
- `PrivateMove: горизонтальный %` / `вертикальный %` (`Custom`, `Reduce`);
- `PrivateMove: шанс срабатывания %` (`Grim Advanced`, `Matrix`);
- `PrivateMove: задержка (мс)` / `уменьшение силы` (только `PrivateMove [Grim…]`);
- `PrivateMove: только на земле` (`Grim Light`, `Grim Stable`);
- `PrivateMove: блокировать взрывы` / `отладка` (все `PrivateMove`);
- `Rockstar: скорость по X / Y / Z` (только `Rockstar [Изменение]`);
- `Sadnes: от чего отменять` (`from_hits` / `from_explosions` / `from_wind_charge`), `задержка постановки`,
  `пауза при флаге (с)` (только режимы Sadnes);
- `Velocity: отладка в чат` — печатает в чат, отменён или пропущен каждый пакет скорости и с какими
  значениями (для проверки в игре, что обход реально перехватывает пакет);
- `SkyCoreLegacy: прыгать` (только `SkyCoreLegacy [RW]`).

## Как это подключено

- `combat/velocity/VelocityHandler` — контракт одного источника: `onVelocityPacket`, `onInboundPacket`,
  `onOutboundPacket`, `onTick`, `onInput`, `reset`. Состояние обходов не смешивается: при смене режима
  предыдущий обход получает `reset()`.
- `combat/velocity/VelocityAction` — одноразовая просьба к пакету скорости: `cancel()` (пакет не
  применяется), `scale(x, y, z)` (пакетная скорость с множителями), `set(x, y, z)` (своя скорость).
  Объект один на модуль, в пути пакета нет аллокаций.
- `mixin/ClientPacketListenerVelocityMixin` — `@Inject(at = HEAD, cancellable = true)` в
  `handleSetEntityMotion`: пакет **отменяется целиком**, как в клиентах-источниках, поэтому ванильный
  `Entity#lerpMotion` не выполняется вообще. Обходы, которым нужно изменить скорость (в источниках они
  правили поля `motionX/motionY/motionZ`), выставляют её сами (`VelocityAction#scale/set`) — эффект
  тот же, что от правки пакета. Решение принимается только на клиентском потоке: если пакет пришёл
  из сети, ванильный `PacketUtils.ensureRunningOnSameThread` сначала перебрасывает его в очередь
  клиента, и инъекция срабатывает повторно уже в тике.
- `mixin/ConnectionVelocityMixin` — `channelRead0` отдаёт входящие пакеты модулю: взрывы
  (`LightBeta`, `Private`, `Sadnes`), пинги (`Phobia`, `Releon`), ack-пакеты (`Nuclear`),
  обновления блоков и урон (`Sadnes`, `SkyCoreLegacy`), телепорты (`Private [Vulcan]`).
- Исходящие пакеты — через `PacketSendEvent` из `ConnectionPrepareViaNativeConnectMixin`
  (событие публикуется, пока включён хотя бы один игровой модуль; `Velocity` — MOVEMENT).
- `PlayerTickEndEvent` → `onTick` (аналог `UpdateEvent` источников), `PlayerInputEvent` →
  `onInput` (аналог `MoveInputEvent`/`InputEvent`), `PlayerInputEvent.setJump(false)` заменяет отмену
  `JumpEvent` в `PrivateMove [Jump Reset]`.

## Соответствие имён 26.2 (проверено по декомпиляции Minecraft 26.2)

| Что в источниках (1.21.x) | Что в 26.2 |
|---|---|
| `SEntityVelocityPacket.getMotionX()` | `ClientboundSetEntityMotionPacket#movement()` |
| `SConfirmTransactionPacket` | пакета нет; задерживаются `ClientboundPingPacket` (сервер ждёт pong) |
| `CommonPingS2CPacket` / `ClientboundPingPacket` | `net.minecraft.network.protocol.common.ClientboundPingPacket` |
| `C2SPongPacket` | `net.minecraft.network.protocol.common.ServerboundPongPacket` |
| `player.inPowderSnow` | `player.isInPowderSnow` |
| `SPlayerPositionLookPacket#getTeleportId()` | `ClientboundPlayerPositionPacket#id()` |
| `CConfirmTeleportPacket` | `ServerboundAcceptTeleportationPacket` |
| `CPlayerPacket(true)` | `ServerboundMovePlayerPacket.StatusOnly(true, …)` |
| `player.ticksExisted` | `player.tickCount` |
| `EventDamage` | `ClientboundDamageEventPacket` + `ClientboundEntityEventPacket` (id 2) |

## Осознанные отличия (и почему)

1. `Phobia` — в источнике сравнения режимов в двух местах написаны без пробела (`"NewGrim"`/`"OldGrim"`
   против значений `"New Grim"`/`"Old Grim"`), поэтому там ветки никогда не выполнялись. Опечатка
   сохранена: режимы Phobia ведут себя как в исходном клиенте. Рабочая версия того же ядра — `Releon`.
2. `Nuclear [Grim]` — вместо подтверждений транзакций считаются `ClientboundPingPacket`: в 26.2 пакета
   подтверждения транзакций нет, а пинг — ровно тот пакет, на который сервер ждёт ответ клиента
   (его же отменяют `Phobia [Old Grim]` и `Releon [Old Grim]`).
3. `Sadnes [grim_standing]` — источник отменяет тик игрока; в SkyCore такого события нет, вместо
   этого на время постановки блока обнуляется ввод. `DeferredPacketQueue` перенесён локальной
   очередью с повторной обработкой пакета на клиентском потоке.
4. `Rockstar [Компенсация]` — вместо `player.jump()` ванильный расчёт вертикальной скорости прыжка
   `0.42 + getJumpBoostPower()`.
5. `PrivateMove [Grim]` — проверка `ViaUtil.allowedBypass()` (версия 1.17+) в 26.2 всегда выполняется.
6. `Wild [Lag]` отменяет **все** исходящие пакеты — поведение источника, клиент «замирает».
7. `Minced` — настройка «Прыгать» в источнике видна только для режима `Легит`, которого нет в списке
   режимов Minced, поэтому она не переносилась (мёртвая ветка).

## Проверки

- Первая сборка пользователя дала 10 ошибок (пакеты `ClientboundPingPacket`, `ServerboundPongPacket`,
  `ClientboundContainerAckPacket`, поле `inPowderSnow`) — исправлено по декомпилированным исходникам
  Minecraft 26.2.
- Импорты и члены API всех новых файлов сверены с деревом исходников 26.2; синтаксис проверен
  tree-sitter, ошибок нет.
- `tools/check_bot_ui.py` — 12 тестов, OK.
- Полная сборка на машине пользователя после правок: `gradlew build`.
