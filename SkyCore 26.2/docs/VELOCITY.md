# Velocity — все обходы отбрасывания из клиентов GodWeer

Модуль `Velocity` (категория **MOVEMENT**) собирает в себе обходы отбрасывания из всех клиентов
репозитория, где есть модуль `Velocity`/`NoVelocity`/`VelocityControl`. Каждый вариант работает так
же, как в клиенте-источнике, и подписан его именем: `Клиент [Режим клиента]`.

## Источники и режимы (52)

| Режим в SkyCore | Клиент-источник | Файл-источник | Версия |
|---|---|---|---|
| `Wild [Vanilla]`, `Wild [Lag]`, `Wild [Funtime]` | Wild (`3`) | `modules/combat/Velocity.java` | 1.21.8 |
| `AlphaDLC [Cancel]` | AlphaDLC | `module/list/combat/Velocity.java` | 1.21.x |
| `LightBeta [Cancel]`, `LightBeta [Jump]`, `LightBeta [HwTest]` | LightBeta | `modules/impl/combat/NoVelocity.java` | 1.21.x |
| `Minced [Обычный]`, `Minced [Грим]` | Minced | `module/combat/NoVelocity.java` | 1.21.x |
| `Nuclear [Cancel]`, `Nuclear [Grim]`, `Nuclear [Grim Air]` | Nuclear | `module/impl/combat/Velocity.java` | 1.21.x |
| `Phobia [New Grim]`, `Phobia [Old Grim]`, `Phobia [Matrix]`, `Phobia [Vanilla]` | Phobia | `modulesystem/impl/rage/Velocity.java` | 1.21.11 |
| `Private [Matrix]`, `Private [Vulcan]`, `Private [Intave]`, `Private [AAC v4]`, `Private [NCP]`, `Private [Reverse]` | Private (Combat) | `module/impl/combat/Velocity.java` | 1.21.x |
| `PrivateMove [Grim]`, `[Grim Light]`, `[Grim Heavy]`, `[Grim Stable]`, `[Grim Advanced]`, `[Matrix]`, `[Vulcan]`, `[Intave]`, `[Verus]`, `[NCP]`, `[AAC]`, `[Spartan]`, `[Vanilla]`, `[Custom]`, `[Zero]`, `[Reduce]`, `[Jump Reset]` | Private (Movement) | `module/impl/movement/Velocity.java` | 1.21.x |
| `Releon [New Grim]`, `Releon [Old Grim]`, `Releon [Matrix]`, `Releon [Normal]` | Releon | `module/impl/combat/Velocity.java` | 1.21.11 |
| `Rockstar [Отмена]`, `Rockstar [Компенсация]`, `Rockstar [Изменение]` | Rockstar | `module/combat/Velocity.java` | 1.21.4 |
| `Sadnes [normal]`, `Sadnes [riding_spoof]`, `Sadnes [grim_standing]`, `Sadnes [jump_reset]` | Sadnes | `feature/impl/combat/VelocityControl.java` | 1.21.11 |
| `SkyCoreLegacy [Обычный]`, `SkyCoreLegacy [RW]` | старый SkyCore | `modules/impl/combat/NoVelocity.java` | 1.21.x |

Клиенты без velocity-модуля (`Arcanic`, `PatPat`, `Decide`, `Destra`, `Pouch`, `Prestige`, `Kimiko`,
`Swerve`, `SystemSRC 26.2`, `4`, `Новая папка (6)`) не переносились.

## Настройки

Один плоский список, у каждого обхода свои значения — общих слайдеров нет:

- `Режим` — все 52 варианта списком;
- `Minced: пропускать при активных спидах` (только `Minced [Грим]`);
- `Nuclear: вырубить после флага` (только режимы Nuclear);
- `PrivateMove: горизонтальный %` / `вертикальный %` (`Custom`, `Reduce`);
- `PrivateMove: шанс срабатывания %` (`Grim Advanced`, `Matrix`);
- `PrivateMove: задержка (мс)` / `уменьшение силы` (только `PrivateMove [Grim…]`);
- `PrivateMove: только на земле` (`Grim Light`, `Grim Stable`);
- `PrivateMove: блокировать взрывы` / `отладка` (все `PrivateMove`);
- `Rockstar: скорость по X / Y / Z` (только `Rockstar [Изменение]`);
- `Sadnes: от чего отменять` (`from_hits` / `from_explosions` / `from_wind_charge`), `задержка постановки`,
  `пауза при флаге (с)` (только режимы Sadnes);
- `SkyCoreLegacy: прыгать` (только `SkyCoreLegacy [RW]`).

## Как это подключено

- `combat/velocity/VelocityHandler` — контракт одного источника: `onVelocityPacket`, `onInboundPacket`,
  `onOutboundPacket`, `onTick`, `onInput`, `reset`. Состояние обходов не смешивается: при смене режима
  предыдущий обход получает `reset()`.
- `combat/velocity/VelocityAction` — одноразовая просьба к пакету скорости: `cancel()` (вернуть
  прежнюю скорость), `scale(x, y, z)`, `set(x, y, z)`. Объект один на модуль, в пути пакета нет аллокаций.
- `mixin/ClientPacketListenerVelocityMixin` — `handleSetEntityMotion` с двух сторон: до ванильного
  обработчика запоминается текущая скорость, после — применяется решение обхода. Пакет **не**
  отменяется на сети: в 26.2 у `ClientboundSetEntityMotionPacket` только аксессоры `id()`/`getVelocity*()`,
  а поля, которые правили старые клиенты, закрыты. Для локального игрока результат тот же.
- `mixin/ConnectionVelocityMixin` — `channelRead0` отдаёт входящие пакеты модулю: взрывы
  (`LightBeta`, `Private`, `Sadnes`), пинги (`Phobia`, `Releon`), подтверждения транзакций
  (`Nuclear`, `Minced`), обновления блоков и урон (`Sadnes`, `SkyCoreLegacy`).
- Исходящие пакеты — через `PacketSendEvent` из `ConnectionPrepareViaNativeConnectMixin`
  (событие публикуется, пока включён хотя бы один игровой модуль; `Velocity` — MOVEMENT, поэтому
  условие выполняется).
- `PlayerTickEndEvent` → `onTick` (аналог `UpdateEvent` источников), `PlayerInputEvent` →
  `onInput` (аналог `MoveInputEvent`/`InputEvent`), `PlayerInputEvent.setJump(false)` заменяет отмену
  `JumpEvent` в `PrivateMove [Jump Reset]`.

## Осознанные отличия (и почему)

1. `Phobia` — в источнике сравнения режимов в двух местах написаны без пробела (`"NewGrim"`/`"OldGrim"`
   против значений `"New Grim"`/`"Old Grim"`), поэтому там ветки никогда не выполнялись. Опечатка
   сохранена: режимы Phobia ведут себя как в исходном клиенте. Рабочая версия — `Releon` (ядро то же).
2. `Private [Vulcan]` — источник отменяет телепорт с конкретным `teleportId`; в 26.2 аксессор
   идентификатора у `ClientboundPlayerPositionPacket` не подтверждён, поэтому отменяется первый
   телепорт после подтверждения (расхождение максимум в один пакет).
3. `Sadnes [grim_standing]` — источник отменяет тик игрока; в SkyCore такого события нет, вместо
   этого на время постановки блока обнуляется ввод. `DeferredPacketQueue` перенесён локальной
   очередью с повторной обработкой пакета на клиентском потоке.
4. `Sadnes: from_wind_charge` — отделить ветряной заряд от обычного взрыва можно только по частицам
   взрыва; API частиц в 26.2 не подтверждён, поэтому настройка сохранена, но пока не влияет на
   поведение (`from_explosions` отменяет взрывы целиком).
5. `Rockstar [Компенсация]` — вместо `player.jump()` используется ванильный расчёт вертикальной
   скорости прыжка `0.42 + getJumpBoostPower()`.
6. `SkyCoreLegacy [RW]` — `EventDamage` старого клиента заменён на `ClientboundEntityEventPacket`
   с идентификатором 2; типы урона — `DamageTypes.FALL/ARROW/ENDER_PEARL/CACTUS`.
7. `PrivateMove [Grim]` — проверка `ViaUtil.allowedBypass()` (версия 1.17+) всегда выполняется
   в 26.2, поэтому снята вместе с сообщением об ошибке. `super.tick()` из ветки `Компенсация`
   Rockstar опущен: у модулей SkyCore базового тика нет.
8. `Wild [Lag]` отменяет **все** исходящие пакеты — это поведение источника, клиент «замирает».

## Проверки

- Синтаксис всех новых файлов проверен tree-sitter (`tree_sitter_java`), ошибок нет.
- `tools/check_bot_ui.py` — 12 тестов, OK (регрессия по bot-UI не затронута).
- Компиляция `javac`/Gradle в этом окружении не выполнялась: JDK и доступ к maven-репозиториям
  отсутствуют, поэтому сборку нужно прогнать на машине с Loom.
