# Scaffold — порт MotherHack (NCP / NCPStrict / Eagle / CatLean)

> Заменяет прежний Telly-only Scaffold. Старый edge-mode, Telly-контроллер и
> free-look камеры Scaffold удалены вместе с модулем. Актуальные файлы:
> `ScaffoldModule.java` (порт), `ScaffoldGeometry.java` (геометрия для Coach),
> `ScaffoldCoachModule.java` (отдельный тренажёр), `docs/scaffold-coach.md`.
> Исторический Telly-документ: `docs/telly-scaffold.md`.

## Что перенесено

Порт сделан с усовершенствованной итерации MotherHack Scaffold
(`Новый текстовый документ (8).txt`, 1345 строк), а не с более старого
`MotherHack/src/main/java/fun/motherhack/modules/impl/movement/Scaffold.java`.

Сохранены:

- режимы `NCP`, `NCPStrict` (по умолчанию), `Eagle`, `CatLean`;
- имена, порядок и значения по умолчанию всех настроек источника
  (`Mode`, `Switch`, `Rotate`, `LockY`, `AutoJump`, `WorkWhileSneaking`, `Tower`,
  `SafeWalk`, `OnlyNotHoldingSpace`, `NCPRotate`, `SmoothSpeed`, `PlaceDelay`,
  `MinDelay`, `MaxDelay`, `LockYDelay`, `StaticPitch`, `FixedPitch`, `GrimPitchLimit`,
  `RandomizeDelay`, `MinPlaceDelay`, `MaxPlaceDelay`, `BuildTrust`, `TrustBlocks`,
  `LastMomentPlace`, `NoServerRotate`, `KeepHeight`, `Delay`, `Switch`/`Tower`/`SafeWalk`
  для CatLean, `AllowShift`, `Legit`, `SaveAngles`, `Telly`, `AirPlace`);
- `BLACKLIST` из 18 блоков;
- SafeWalk NCP с шагом 0.05, tower-импульс `0.42`/`-0.28` через таймер 1500 мс,
  рандомизированные задержки `MinDelay..MaxDelay` и `+20 мс` до 40 «trust»-блоков,
  `LastMomentPlace` с симуляцией гравитации (`vy -= 0.08; vy *= 0.98`, до 10 тиков,
  порог `ticks <= 3`), `shouldPlaceLastMoment` по смещению от центра блока;
- ротации `Normal`, `Smooth`, `Packet`, `Linear`, `Snap`, `Grim+Matrix`, `None`,
  `getRotationsWithGrimMatrix` с `StaticPitch`/`GrimPitchLimit`;
- `checkNearBlocks`/`checkNearBlocksExtended` (13 оффсетов), дистанция 4.2,
  бонус `-0.5` за `Direction.UP`, случайная точка `0.05..0.95` на грани;
- CatLean: перебор куба по reach, выбор грани с минимальной разницей yaw,
  `ClickSlot`-свап для Inventory, packet-ротация для Silent, `Legit`/`SaveAngles`.

## Отличия платформы (26.2 / SkyCore)

| MotherHack 1.21.4 | SkyCore 26.2 |
|---|---|
| `PlayerMoveC2SPacket.*` | `ServerboundMovePlayerPacket.Rot` / `.Full` |
| `ClientCommandC2SPacket.Mode.PRESS/RELEASE_SHIFT_KEY` | `options.keyShift.setDown(true/false)` — в 26.2 у player-command пакета больше нет shift-действий |
| `mc.interactionManager.interactBlock` | `mc.gameMode.useItemOn` |
| `mc.player.setVelocity` | `mc.player.setDeltaMovement` |
| `mc.itemUseCooldown` | `MinecraftGetRightClickDelayAccessor.skycore$getRightClickDelay()` |
| `mc.world.getBlockCollisions(...).hasNext()` | `mc.level.noCollision(...)` (инвертировано) |
| `isReplaceable()` | `canBeReplaced()` |
| `mc.player.age` | `mc.player.tickCount` |
| `EnumSetting<Mode>` + `setVisible` | `ModeSetting` (строки) + `visibleWhen` |
| Orbit `@EventHandler(EventPlayerTick)` | `PlayerTickEndEvent` через `EventBus` |
| `RotationChanger` + `addRotation` | `RotationManager.updateExact(Rotation, 5000, 2)` |
| `addPacketRotation` | прямой `ServerboundMovePlayerPacket.Full` с текущими координатами |
| `e.setPacket(...)` для Snap | прямой `Full`-пакет в момент установки (`PacketSendEvent` в SkyCore не умеет заменять пакет) |
| `setBodyYaw`/`setHeadYaw` | поля `yBodyRot`/`yHeadRot` |

`NCPRotate` и CatLean `Switch` названы `NCPRotate` и `CatLean Switch`, потому что
исходные `Rotate` и `Switch` уже заняты в плоском списке настроек SkyCore
(движок настроек адресует их по имени).

## Унаследованное поведение источника (не исправлено — требование «точь-в-точь»)

- `needSneak(Block)` всегда возвращает `false`, ветка PRESS/RELEASE shift мёртвая;
- `placeBlock(int)` нигде не вызывается (все пути идут через `placeBlockNCP` /
  `placeBlockWithOrder`);
- `prePlace` всегда вызывается с `false`, поэтому ветки `Switch.Inventory/Normal`
  недостижимы, а `Switch` фактически не переключает слот;
- `NoServerRotate` объявлен, но в логике не читается;
- `lastMomentPlaced`/`lastMomentTick` только записываются;
- `shouldPlaceLastMoment` использует обычный `canBeReplaced()` вместо проверки коллизий;
- `Snap` в SkyCore не может подменить уже сформированный пакет плоскости — вместо этого
  отправляется отдельный movement-пакет перед установкой;
- CatLean `Inventory`-свап всегда считает `blockSlot + 36`, так как слот ищется только в
  hotbar.

## Сосуществование

- `Scaffold Coach` остался отдельным singleplayer-тренажёром; общие raycast-хелперы
  вынесены в `ScaffoldGeometry` и используются только Coach'ем.
- `.scafftrain` запись/отчёт сохранены; `apply/undo` для нового Scaffold недоступны,
  так как в порте нет Telly `Turn/Return Speed`. Команда сообщает об этом.
- Free-look камеры Scaffold удалён: хуки `CameraSyncCameraRotationMixin`,
  `MouseHandlerHandleSilentTurnMixin` и `LocalPlayerPreUpdateMixin` больше не обращаются
  к Scaffold.

## Ограничения и проверка

Компиляция и игровой прогон в среде агента не выполнялись (нет JDK/Gradle/игры).
Проверено статически: имена пакетов 26.2 сверены по игровому jar
(`ServerboundMovePlayerPacket.Rot`/`.Full`, отсутствие `PRESS_SHIFT_KEY` в
`ServerboundPlayerCommandPacket.Action`), API настроек/событий/ротаций — по исходникам
SkyCore.

Проверять на своём сервере или там, где автоматизация разрешена, начиная над площадкой:
Eagle → NCP → NCPStrict с `BuildTrust`/`LastMomentPlace` → CatLean по каждому типу
переключения слота. `NCPStrict` по умолчанию использует `Grim+Matrix` со `StaticPitch 80`.
