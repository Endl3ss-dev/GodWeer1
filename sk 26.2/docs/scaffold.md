# Scaffold — Eagle, AutoPlace и Telly

`ScaffoldModule` в SkyCore работает в режимах `Eagle` (по умолчанию) и
`Telly` — порте одноимённого по поведению модуля Scaffold из CatLean.

## Настройки

| Настройка | Значение по умолчанию | Описание |
| --- | --- | --- |
| `Mode` | `Eagle` | Единственный режим модуля |
| `AutoPlace` | `false` | Автоматически устанавливать блок моста под игроком |
| `CPS` | `10` | Максимум автоматических установок в секунду, диапазон `1–20`; видна только при `AutoPlace=true` |
| `AutoSwitch` | `Silent` | Способ выбора ячейки со строительным блоком; видна только при `AutoPlace=true` |
| `Rotate` | `Normal` | Способ ротации к грани блока; видна только при `AutoPlace=true` |
| `ClientLock` | `true` | Показывать управляемую ротацию на экране; видна при `AutoPlace=true` и `Rotate` не равен `None` |

### `AutoSwitch`

Доступные значения:

- `Normal` — выбрать строительный блок в hotbar обычным способом и оставить его выбранным;
- `Silent` — временно отправить серверу выбранную ячейку, установить блок и вернуть исходную ячейку;
- `Inventory` — временно обменять строительный блок из основной inventory с текущей ячейкой,
  установить блок и вернуть содержимое обратно;
- `None` — не искать другую ячейку: используется только уже выбранный строительный блок
  в main hand или block item в offhand.

Если в основной inventory нет блока, `Inventory` использует доступный блок из hotbar.

### `Rotate`

Доступные значения:

- `Normal` — обычная мгновенная ротация к найденной грани;
- `Smooth` — плавная ротация с ограниченной скоростью;
- `Linear` — равномерное перемещение к цели фиксированным угловым шагом;
- `None` — ротация не меняется. Для установки требуется вручную навести crosshair
  на грань блока-опоры, после чего `AutoPlace` выполнит взаимодействие.

### `ClientLock`

- `true` — управляемая ротация отображается на экране игрока (прежнее поведение);
- `false` — ротация работает скрытно (silent, через free-look): камера остаётся
  полностью свободной, можно смотреть и идти вперёд, пока модуль строит сзади.

## Поведение `AutoPlace`

При включённом `AutoPlace` модуль ищет заменяемый блок непосредственно под игроком,
находит видимую грань соседнего твёрдого блока-опоры в пределах interaction reach и
использует выбранный строительный блок для установки. Интервал ограничивается
настройкой `CPS`; одновременно выполняется не более одной попытки за интервал.

### Строительство по одной оси

Пока игрок движется, мост строится только по доминирующей оси движения (`X` или
`Z`, с гистерезисом против случайных переключений на диагоналях):

- опора ищется только позади игрока вдоль этой оси (затем — блок под целью),
  боковые грани не используются;
- yaw ротации фиксируется на кардинальном направлении против движения,
  от точки установки зависит только pitch — камера не дёргается за каждой гранью;
- ввод движения пересчитывается относительно управляемой ротации, поэтому
  разворот головы не меняет мировую траекторию и мост остаётся ровным.

Ось определяется по клавишам движения относительно направления взгляда
(при silent-ротации — free-look), а не по повёрнутой голове, поэтому строить
можно и идя прямо вперёд. При этом спринт игнорируется: серверная ротация
развёрнута назад, и спринт против неё создавал бы только невалидные
спринт-пакеты.

Если игрок стоит на месте, используется прежний свободный поиск лучшей видимой
грани.

`Eagle` продолжает работать независимо от `AutoPlace`: если под игроком воздух,
модуль удерживает `options.keyShift`, чтобы игрок не сошёл с края. При отключении
`Scaffold` Shift отпускается, а активная ротация модуля прекращается.

## Telly mode

`Mode` = **Telly** is the CatLean Scaffold module
(`su.catlean.client.features.modules.rage.Scaffold`, source in `ctln/`)
ported method by method: the support is searched over the same 14
neighbour offsets with the same direction order, the hit point is clamped
into the support block next to the player (`Clamp`), rotations are
GCD-quantized against the last server rotation, and the placement fires
with the CatLean callback timing (SEQUENTIAL/VANILLA in the same tick,
GRIM on the next one). `AutoPlace`, `CPS`, `Rotate` and `ClientLock` do
not apply to this mode; the block itself is picked by `AutoSwitch`
(`Normal`/`Silent`/`Inventory`/`None` — CatLean's `switch`), which now
stays visible.

| Setting | Default | Description |
|---|---|---|
| `Timing` | `GRIM` | CatLean's rotation-action kinds: `SEQUENTIAL` places right after the submit, `VANILLA` shows the rotation and places in the same tick, `GRIM` is silent and places on the next tick. `CUSTOM` is the fourth value — see below. In SkyCore `SEQUENTIAL` and `CUSTOM` send the rotation as a packet before the use packet: Scaffold runs on `PlayerTickEndEvent`, i.e. before `sendPosition` of the same tick, so without it the server would receive the placement first. |
| `Tower` | `true` | Holding jump in air keeps the vertical velocity at `0.42` with the periodic slight dip CatLean applies. |
| `SafeWalk`/`AllowShift` | `false` | Safewalk holds shift while the target cell is empty; `AllowShift` permits building while sneaking. |
| `Legit` | `true` | Do not place while a shrunk collision box still touches the ground below. |
| `SaveAngles` | `true` | Keep the last CatLean rotation while there is nothing to place. |
| `Telly`/`KeepHeight` | `false` | Hold the fixed bridge height; the `Telly` setting also waits while jump is held before the player has started falling. (Unrelated to the mode name.) |
| `Clamp` | `true` | Clamp the hit point into the support block; otherwise use the face centre. |
| `AirPlace` | `false` | Place into the cell under the player even without a support face. |
| `GrimBlockFly` | `false` | Buffer incoming packets for up to 500 ms (chat/time/sound/health/bossbar pass through, position/disconnect flush and pass); also wraps the placement in offhand-swap packets. |
| `CatLeanRotate` | `true` | Master switch for all Telly rotations (CatLean's `rotate`; renamed to avoid clashing with Eagle's `Rotate`). |
| `GrimSilent` | `false` | Wrap the placement in raw position-rotation packets sent around it. |

### `CUSTOM` timing

`CUSTOM` keeps the CatLean search, block switching and tower logic, but splits
the rotation and the placement into explicit stages and adds the checks the
original does not make. It exists because the other timings share one problem on
a moving player: the placement is built from a hit point the player has already
left, and the cell under the player changes every tick, so a late or refused
placement leaves a hole in the bridge. `CUSTOM` validates the hit before it is
used, sends the rotation in its own tick, and repeats a placement the client
world does not show.

| Setting | Default | Description |
|---|---|---|
| `CustomVisible` | `true` | `true` — the rotation is shown on the screen and the server rotation follows the player's own head. `false` — silent rotation through `RotationManager`, the camera stays free (needs `CatLeanRotate`). |
| `CustomRotateDelay` | `0` | Extra ticks of waiting after the angles reached the hit point before the placement stage (0–4). `0` starts the placement stage on the tick the head is within 1° of the hit point. |
| `CustomPlaceDelay` | `1` | Ticks between the last rotation tick and the placement (0–4). `0` and `1` both place on the next tick: the rotation packet has to be on the wire in an earlier tick (see below). |
| `CustomYawStep` | `180` | Maximum yaw change per tick in the rotation stage (30–180°). `180` reaches any yaw in one tick; lower values spread it over several ticks. |
| `CustomPitchStep` | `180` | The same limit for pitch (30–180°). |
| `CustomReachCheck` | `true` | Reject the hit if it is farther than the block interaction range. CatLean places on the first neighbouring block without such a check, which the server rejects. |
| `CustomRayCheck` | `true` | Reject the hit if the line of sight from the eyes does not reach the support block. |
| `CustomRetry` | `true` | While the client world still shows the target cell empty after our placement, place into it again instead of walking into the hole. At most three attempts per cell, so a server that keeps refusing the placement is not spammed. |
| `CustomRetryDelay` | `2` | Minimum ticks between the original placement and a retry (1–4; visible only with `CustomRetry`). |

Sequence of one placement: search → validation → rotation stage (limited steps
until within 1° of the hit point, plus `CustomRotateDelay`) → `CustomPlaceDelay`
ticks → block into the hand → rotation packet (silent) → use packet → the cell
is tracked until the client world shows a block there; if it stays empty, the
placement is repeated after `CustomRetryDelay`, at most three times per cell.
A new target is only searched when nothing is pending, so the module never
overwrites a cell it has just asked the server to fill. `SaveAngles` does not
override an in-flight `CUSTOM` rotation; it only keeps the last rotation while
the module is idle.

`CUSTOM` cannot make managed scaffolding invisible to a server: it is still a
client placing blocks under the player. What it fixes is narrower — the use
packet no longer follows a rotation the player has not sent yet, out-of-reach
and unlit placements are dropped instead of sent, and a lost placement is
repeated instead of leaving a hole. `CustomPlaceDelay = 0` reproduces the CatLean
`SEQUENTIAL` behaviour (rotation and placement in one tick) with the validation
on and with the explicit packet order described above.

The order matters because Scaffold runs on `PlayerTickEndEvent`
(`LocalPlayerPreUpdateMixin`, injected at `AbstractClientPlayer#tick`), before
`LocalPlayer#sendPosition` of the same tick. A rotation submitted from that
handler is only serialized **after** the handler returns, i.e. after the use
packet sent inside it. The port therefore sends the angles explicitly, in this
order:

1. the block is put into the hand (`AutoSwitch` swap / carried-item packet),
2. the rotation packet (`ServerboundMovePlayerPacket.PosRot`),
3. the use packet.

On `SEQUENTIAL` and `VANILLA` that is done inside `submitTellyPlacement`; a
`VANILLA` placement additionally claims the visible rotation so the head is
where the packet says. `GRIM` keeps its own order: the rotation is claimed now
and the use packet is deferred to the next player update, when this tick's
movement packet has already carried it. `CUSTOM` places one tick after the
rotation by default (`CustomPlaceDelay = 1`); with `0` it uses the same explicit
order as `SEQUENTIAL`.

The only service-boundary differences to the CatLean original (all marked
in the code): CatLean's RotationManager actions run through SkyCore's
`RotationManager.updateExact`, `WorldUtil`'s fall-distance/input values
are tracked locally, SkyCore has no separate AirPlace module so the
`AirPlace` setting also covers its bypass flag, and the decompiled
container swaps' `button -1` is replaced by the vanilla selected slot.

## Зависший контейнер (InvSaver и похожие модули)

Модуль, который закрывает экран контейнера без пакета закрытия (например,
`InvSaver`-подобный «closer магазина»), оставляет `player.containerMenu`
указателем на чужое меню, хотя экран уже закрыт. В этом состоянии
`AutoSwitch → Inventory` отправлял `handleContainerInput` с `containerId`
контейнера, которого игрок уже не видит, поэтому блок брался не из того
инвентаря и не появлялся, а `useItemOn` и свап расходились по разным
контейнерам — визуально это и есть «Scaffold не ставит блоки».

Теперь `Inventory`-своп выполняется только при
`player.containerMenu == player.inventoryMenu`. Если меню чужое,
`AutoSwitch → Inventory` откатывается на hotbar-путь (`ServerboundSetCarriedItemPacket`),
который не зависит от состояния контейнера, и возвращает слот после установки.
Установка при этом не пропускается: `AutoPlace` (Eagle) и оба режима Telly,
включая silent-свопы, продолжают ставить блоки. Сторонние модули это не
включает и не выключает — оно лишь не даёт им сломать установку.

Сам `InvSaver` в `sk 26.2` отсутствует: модуля с таким именем нет ни в одном
пакете `net.skycore.client.module`. Поэтому исключения «для некоторых
модулей» добавлены не как список имён модулей, а как проверка состояния
контейнера — она работает для любого модуля, закрывающего экран без пакета.
