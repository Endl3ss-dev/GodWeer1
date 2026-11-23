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
| `Timing` | `GRIM` | CatLean's rotation-action kinds: `SEQUENTIAL` places right after the submit, `VANILLA` shows the rotation and places in the same tick, `GRIM` is silent and places on the next tick. |
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

The only service-boundary differences to the CatLean original (all marked
in the code): CatLean's RotationManager actions run through SkyCore's
`RotationManager.updateExact`, `WorldUtil`'s fall-distance/input values
are tracked locally, SkyCore has no separate AirPlace module so the
`AirPlace` setting also covers its bypass flag, and the decompiled
container swaps' `button -1` is replaced by the vanilla selected slot.
