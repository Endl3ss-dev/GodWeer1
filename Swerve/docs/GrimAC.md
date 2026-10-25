# GrimAC 2.3.67: что он видит в Swerve

Сервер, на котором предполагается работать клиенту, защищён **GrimAC ~2.3.67**.
Этот документ описывает, что именно эта версия проверяет в том коде, который пишет
Swerve (повороты, удары, использование предметов), и что изменится, если сервер
обновится. Всё проверено по исходникам конкретной сборки, а не по описаниям.

> **Лицензия.** Grim распространяется под GPL-3.0, а `Reach.java` несёт явную
> пометку «You may not copy the check unless you are licensed under GPL». Поэтому
> здесь только **выводы о поведении**: логика проверок не переносится в клиент,
> заимствуется только знание о том, как надо формировать пакеты, чтобы оно совпадало
> с ванильным клиентом.

## 1. Провенанс: что такое «2.3.67» в дереве исходников

Тегом эта версия не помечена (теги в репозитории есть только с `v2.3.71`), версия
живёт в `build.gradle.kts` строкой `version = "…"`, и релиз помечается коммитом
«Bump version», который **ставит следующую** версию. Отсюда:

| Релиз | Коммит, которому соответствует дерево | Дата |
| --- | --- | --- |
| **2.3.67** | `b0cd3262` («Fix fences hitbox», последняя запись `version = "2.3.67"`; сам номер снял `4c039e61`) | 2024-09-12 |
| 2.3.68 | `4c039e61` … `4f8357f5` | 2024-12-17 |
| 2.3.69 | `4f8357f5` | 2025-01-30 |
| 2.3.70 / .71 / .72 | `856a51e5` / `f88a114e` / `401abad5` | 2025-02-01 / 2025-04-17 / 2025-10-03 |
| 2.3.73 / 2.3.74 | теги `v2.3.73`, `v2.3.74` | 2026-03-09 / 2026-10-21 |

Как воспроизвести (у меня это лежит вне репозитория, в `/home/user/grim-2.3.67`):

```bash
git clone --filter=blob:none --no-checkout https://github.com/GrimAnticheat/Grim grim-src
cd grim-src
git log -S'version = "2.3.67"' --format='%h %ad %s' --date=short -- build.gradle.kts
git worktree add ../grim-2.3.67 b0cd3262
grep -n "version = " ../grim-2.3.67/build.gradle.kts
```

Путь к проверкам в 2.3.67 — `src/main/java/ac/grim/grimac/checks/impl/**` (308
java-файлов). Начиная с 2.3.72 дерево разбито на `common/`, `bukkit/`, `fabric/`.

## 2. Главный вывод: Grim не ловит «ауру» как таковую

В `manager/ConfigManager.java:181` (2.3.67) при дописывании `punishments.yml`
печатается собственный комментарий проекта: *«As of 2.2.2 these are just
placeholders, there are no Killaura/Aim/Autoclicker checks…»*. Проверок с именами
`KillAura*`, `AutoClicker*`, `AimDeduction*` в 2.3.67 действительно нет. Есть
только проверки **пакетной формы и физики**, и ровно в них Swerve может попасть:

| Проверка | Что требует | Цена нарушения в 2.3.67 (конфиг по умолчанию) |
| --- | --- | --- |
| `Reach` (`impl/combat/Reach.java`) | луч из глаз игрока **по текущему взгляду** обязан пересекать хитбокс, и расстояние до пересечения ≤ `PLAYER_ENTITY_INTERACTION_RANGE` (3.0) + 0.0005 + 0.03 | `block-impossible-hits: true` → пакет удара **отменяется**; группа `Reach` = `5:5 [alert]` + `5:5 [webhook]` + `5:5 [proxy]`, т.е. **5 нарушений за 300 с → алерт персоналу и в Discord** |
| `Post` (`impl/post/PostCheck.java`) | действие (`INTERACT_ENTITY`, `USE_ITEM`, `ANIMATION`, `PLAYER_DIGGING`, `HELD_ITEM_CHANGE`, …) обязано идти **до** пакета движения этого тика | `flagAndAlert`, без отмены; группа `Post` = `20:20 [alert]`, `40:40 [webhook]` |
| `BadPacketsH` | между двумя ударами обязан быть хотя бы один `ANIMATION` (свинг): на клиенте >1.8 `sentAnimation` стартует с `true`, дальше каждое `INTERACT(ATTACK)` требует «свинг с прошлого удара» | `flagAndAlert()` **и отмена пакета удара** (`event.setCancelled(true)`) |
| `BadPacketsV` | нельзя бить, пока идёт `USE_ITEM` (еда/блок-щит): `isSlowedByUsingItem()` | отмена пакета удара + флаг (группа `BadPackets`: `20:20 [alert]`, `40:40 [webhook]`) |
| `BadPacketsM` | `INTERACT_AT` затем `INTERACT` (правый клик по сущности) | отмена пакета; на удары не влияет (у удара действие `ATTACK`, без вектора) |
| `BadPacketsT` | вектор `INTERACT_AT` обязан лежать в пределах хитбокса (±0.3001 по X/Z, −0.0001…1.8001 по Y, ×атрибут `scale`) | только правый клик/взаимодействие; для `ATTACK` не применяется |
| `AimModulo360` | **yaw в пакетах должен накапливаться, как в ванилле, а не приводиться к (-180;180]**: флаг при `|Δyaw| > 320°` и `|Δyaw_прошлый| < 30°`, если сам `yaw ∈ (-360;360)` | `flag()` (группа `Combat` = `20:40 [alert]`); `decay 0.005` почти не снимает VL |
| `AimDuplicateLook` | пакет с поворотом не может повторять прежние yaw **и** pitch: ванильный клиент в этом случае шлёт `Pos`/`StatusOnly` без поворота | `flagAndAlert()` без `reward()` → VL копится |
| `Baritone` (`impl/baritone/Baritone.java`) | недопустимо «дрожать» питчем меньше 1° при нулевом yaw: `GrimMath.gcd` дельт обязан быть ≥ `MINIMUM_DIVISOR = (0.2³·8)·0.15 − 1e-3 = 0.0086°` | 8 тиков подряд → `flagAndAlert("Divisor …")`, группа `Misc` = `10:5 [alert]`, `20:10 [webhook]` |
| `TimerCheck`/`NegativeTimerCheck` | темп пакетов движения (1.9+ без idle-пакета считается по транзакциям) | группа `Simulation` = `100:40 [alert]` (порог высокий), плюс `setbackvl` → **телепорт-откат** |

Механика наказаний (чтобы оценивать риск честно): `Check.flag()` увеличивает VL,
`PunishmentManager` группирует проверки по подстроке имени (`Reach`, `Post`,
`BadPackets`, `Aim`, …) и считает **количество нарушений за последние
`remove-violations-after: 300` секунд**; команда вида `20:20 [alert]` = «при 20
нарушениях, далее каждые 20». Отмена пакетов идёт только если проверка включена
(`isEnabled`) и нет права `grim.exempt.*`.

## 3. Разбор поведения по шагам (2.3.67)

### 3.1 `Reach`: считается луч, а не «расстояние до хитбокса»

```java
// checks/impl/combat/Reach.java (2.3.67), checkReach(...)
targetBox.expand(threshold);                                  // 0.0005
if (!didLastLastMovementIncludePosition || version >= 1.9)
    targetBox.expand(player.getMovementThreshold());          // 0.03 — для нас есть
// кандидаты направления взгляда:
//   look(xRot, yRot), look(lastXRot, yRot), и для 1.9+ ещё look(lastXRot, lastYRot)
minDistance = min по всем кандидатам и по всем высотам глаз пересечения луча с боксом
if (minDistance == MAX) → "Missed hitbox"
else if (minDistance > PLAYER_ENTITY_INTERACTION_RANGE) → флаг "N.NNNNN blocks"
```

Следствия для Swerve:

1. Лимит — **3.0 (+≈0.03)** от глаз до *ближайшей точки пересечения луча со взглядом*,
   а не `range + 3.0` до хитбокса, как проверяет сама ванилла
   (`Player.canInteractWithEntity`, см. `docs/PvPMath.md` §2 п.7). Наш
   `KillAura.attackDistance()` (`KillAura.java:773-779`) ограничен
   `min(entityInteractionRange()+3, max(3, «Дистанция»))`, а ползунок «Дистанция»
   разрешён до **6.0** (`KillAura.java:119`). Значит любое значение выше ~3.03 на
   Grim = отменённые удары + алерт персоналу на пятых подряд.
2. Проверка щадит запаздывание взгляда (три кандидата направления) — поворот,
   применённый в начале тика, читается корректно.
3. «Улучшить хитбоксы» (`AuraUtils.visibleTargetPoint`) безопасен ровно потому, что
   берёт первую видимую точку на грани: расстояние по лучу до неё ≈ минимум. Целься
   аура в *дальний* угол — `minDistance` вырастет и попадёт в флаг.

### 3.2 `Post`: порядок пакетов внутри тика — наша главная проблема

Grim считает ванильным порядок «действие, потом движение этого тика». Проверено по
байткоду 1.21.4 (`Swerve/tests/mc_disasm.py`):

| Что | Где в `Minecraft.tick()` |
| --- | --- |
| обработка клавиш/ЛКМ → `Minecraft.startAttack` → `MultiPlayerGameMode.attack` (шлёт `ServerboundInteractPacket`) → затем `LocalPlayer.swing` (`ServerboundSwingPacket`) | секция `"Keybindings"`, pc 357 |
| `ClientLevel.tickEntities()` перебирает `EntityTickList` (pc 14) → `LocalPlayer.tick()` → `super.tick()` → `aiStep()` → `ClientInput.tick()` (pc 155 в `aiStep`) → `sendShiftKeyState` → `ServerboundPlayerInputPacket` → `sendPosition()` (ветки `PosRot`/`Pos`/`Rot`/`StatusOnly`) | секция `"level"`, pc 440…631 |
| `ServerboundClientTickEndPacket` | pc 776 |
| **`END_CLIENT_TICK` Fabric** — здесь крутится `ModuleManager.tick()` и, значит, `KillAura.onTick()` | после `return` |

Итог: удар, отправленный в `END_CLIENT_TICK`, приходит серверу **после** пакета
движения этого тика (и после tick-end пакета) — это ровно то, что ловит `Post`.
Граница разрешения неопределённости — транзакция: `GrimPlayer.pollData()` шлёт её
«примерно раз в два тика» (`lastTransSent + 80 < now`) и дополнительно при
репликации сущностей; `Reach.enable-post-packet` по умолчанию `false`. То есть флаг
вероятностный, но при систематическом «действие после движения» он копится: 20
нарушений за 5 минут = алерт, 40 = webhook.

Тот же порядок на новых версиях Grim стал отдельной проверкой `PacketOrderO`
(«Sent packets after movement before the expected client tick end», experimental) —
наш случай (пакет *после* tick-end) она не ловит, а вот `PacketOrderI`
(«combat/use/release/digging packets in an invalid tick order», experimental) и
`Post` ловят.

Вывод-рекомендация (в коде пока не выполнена): `attackEntity`/`updateAttack` из
`KillAura.onTick()` должны уехать в тот же путь, где уже живёт
`KillAura.rotationTick` (`START_CLIENT_TICK`, `managers/ModuleManager.java:98`) —
тогда порядок пакетов станет ванильным: `INTERACT → ANIMATION → input → flying`.
Побочный плюс: поворот, применённый в начале тика, и удар будут относиться к одному
снимку, а не «удар по позавчерашнему взгляду».

### 3.3 `BadPacketsH`: свинг обязателен, и его порядок важен

Для клиента 1.21.4 `sentAnimation` инициализируется `true`, и дальше проверка
требует «между ударами был хотя бы один `ANIMATION`». Our
`KillAura.attackEntity` (`KillAura.java:680-690`) шлёт свинг в обоих режимах:

* «1.9»: `gameMode.attack()` → `player.swing()` = `INTERACT → ANIMATION` —
  **совпадает с ванилей** (проверено: `Minecraft.startAttack` вызывает
  `MultiPlayerGameMode.attack` на pc 146 и только затем `LocalPlayer.swing` на pc 245);
* «1.8»: `player.swing()` → `gameMode.attack()` — для 2.3.67 безопасно (свинг перед
  ударом удовлетворяет `sentAnimation`), но на Grim ≥ 2.3.72 попадает под
  `PacketOrderB` «Did not swing for attack» (**не experimental**): после `INTERACT`
  он ждёт `ANIMATION` немедленно, а находит пакет движения → флаг
  `[post-attack]`.

Рекомендация: привязывать **порядок пакетов** к версии клиента, а не к режиму ауры;
режим «1.8» должен менять только кулдаун/темп, но не очерёдность `swing`.

### 3.4 `BadPacketsV`: удар во время еды отменяется

`KillAura.checkReturn` (`KillAura.java:579-582`) спрашивает настройку
«Не бить когда ешь», которая по умолчанию **выключена** (`KillAura.java:157`).
Для Grim это не предпочтение, а требование: при `isSlowedByUsingItem()` любой
`INTERACT(ATTACK)` отменяется и флагуется. Значит на сервере с Grim связка
AutoGApple/AutoHeal + KillAura обязана пропускать удары, пока идёт `USE_ITEM`
(иначе теряем и лечение, и DPS: удары не проходят, а алерты растут).

### 3.5 `AimModulo360`/`AimDuplicateLook`: что у нас в порядке и что нет

`deltaXRot = player.xRot - player.lastXRot` — **без** нормализации
(`events/packets/CheckManagerListener.java:711-717`), поэтому:

* безопасно: `RotationController.rotate` (`utils/rotation/RotationController.java:201-210`)
  и `ElytraFlightRotation.rotate` (`utils/movement/ElytraFlightRotation.java:78-84`)
  пишут `player.setYRot(player.getYRot() + Δ)`, т.е. копят угол как ванилла;
  `SexAura` (`modules/impl/combat/SexAura.java:146-147`) — тоже дельтой;
* риск: `AutoPotion` в режиме «Пакетные» (или без `clientLock`) шлёт
  собственный `ServerboundMovePlayerPacket.Rot` с **абсолютными** углами
  (`modules/impl/combat/AutoPotion.java:270-276`, вызывается из `throwPotion`,
  `:247-268`). Это (а) синхронный прыжок yaw на ~360° при переходе через шов →
  `AimModulo360`; (б) пакет поворота, который дублирует прежние углы (если цель уже
  в этом направлении) → `AimDuplicateLook`; (в) `Rot` как «flying» сбрасывает
  очередь `Post`, и идущий сразу за ним `USE_ITEM` оказывается «после движения» →
  `Post`.
  Ванильный клиент так не делает никогда: `LocalPlayer.sendPosition()` шлёт поворот
  только если `getYRot() - yRotLast` или `getXRot() - xRotLast` **не равны нулю**
  (сравнение точное, `dconst_0`/`dcmpl`, без эпсилон) — дубликата углов в
  последовательных пакетах там не бывает.
* риск точности: `Baritone` срабатывает, если pitch меняется дробными шагами,
  не кратными `0.0086°`, при нулевом Δyaw ≥ 8 тиков. Всё, что квантуется через
  `GcdUtil.getSensitivity`, под него не попадает; «сырые» доводки — `rotationYaw = startYaw + wrapDegrees(targetYaw - startYaw) * eased`
  (`AutoPotion.java:232`) и сглаживание `SexAura` — могут.

### 3.6 Что Grim **не** проверяет в 2.3.67 (важно для решений по GCD)

`AimProcessor` вычисляет модус делителя дельт и конвертирует его в «чувствительность»
(`AimProcessor.convertToSensitivity`), но в 2.3.67 результат читают только
`Baritone` (порог `MINIMUM_DIVISOR`), `DuplicateRotPlace` (нужен `USE_ITEM`/place) и
API `GrimPlayer.getHorizontalSensitivity()`. Проверки «игрок квантует не по тому
шагу» нет. Значит известная деградация `ElytraFlightRotation.getGCDValue()`
(свёрнутая форма тождественна значению ползунка — `docs/PvPMath.md` §3.3) **не**
флагуется Grim 2.3.67; она остаётся вопросом паритета с AlphaDLC и других
анти-читов, а не Grim.

## 4. Что изменилось в 2.3.68 → 2.3.74 и касается нас

| Изменение | Коммит/PR | Эффект для Swerve |
| --- | --- | --- |
| `Missed hitbox` вынесен в отдельную проверку `Hitboxes` | `1e1a176b7` (2024-12-30) | раньше промах по хитбоксу тонул в общем флаге `Reach`, теперь — самостоятельная проверка со своим VL/алертом |
| Для клиентов 1.21.2+ (есть tick-end) **убрана** поблажка +0.03 в `Reach` | `f6513b553`, `c1e070e7b` (2024-12-30) | наш лимит на новых версиях ≈3.0005 вместо ≈3.03: «Дистанция» нужно держать на 3.0 и не разгонять |
| Полный набор `PacketOrder A…P` | #1678 (в 2.3.72) | `PacketOrderB` (свинг для удара) — стабильная, бьёт по режиму «1.8»; `I/J/M/O` — experimental, включаются `experimental-checks: true` |
| Освобождения в `AimModulo360` (посадка в транспорт, взаимодействие с лошадью) | `83fba052a`, `5ca408267` | ложные срабатывания по убыванию; условие 320/30 не менялось с 2.3.67 |
| 2.3.73: фикс регрессии `Reach` (не флаговал клиентов 1.15–1.20.1 и 1.21.5–1.21.8), фикс FP `Reach` с happy ghast, фикс эксплуатации «откатная скорость» для обхода проверок движения | релиз v2.3.73 | на 1.21.4 регрессия нас не касалась; «откат» — больше нельзя переиспользовать скорость, полученную при телепорте отката, для обхода проверок движения |
| 2.3.73: `Elytra A/B/C/F`, `PositionBreakB`, `MultiActions C/D` помечены стабильными | релиз v2.3.73 | проверки элитры включены по умолчанию → наш `ElytraFlight` попадает под них при обновлении сервера |
| 2.3.73: фикс злоупотребления пакетами элитры на 1.8-серверах, фикс глайдера в левой руке | релиз v2.3.73 | касается `ElytraFlight` (ракета/начало полёта) |
| 2.3.74 | v2.3.74 | только доставка сообщений/шейдинг, к проверкам отношения не имеет |

## 5. Что из этого следует для Swerve (список задач, ещё **не** выполнен)

Приоритет — по произведению «вероятность × цена» на 2.3.67:

1. **Порядок пакета удара.** Перенести генерацию удара из `END_CLIENT_TICK`
   (`ModuleManager.tick` → `KillAura.onTick`) в `START_CLIENT_TICK` рядом с
   `rotationTick`, чтобы получилось `INTERACT → ANIMATION → input → flying`.
   Закрывает `Post` (2.3.67) и `PacketOrderI/O` (новые).
2. **Удары во время еды.** На Grim это не настройка вкуса: либо держать
   «Не бить когда ешь» включённой, либо пропускать удар при
   `player.isUsingItem()` безусловно. Иначе `BadPacketsV` режет удары.
3. **Ползунок «Дистанция».** На Grim 2.3.67 практически безопасный максимум ≈3.03,
   на новых — 3.0. Текущий максимум 6.0 имеет смысл только против ванильной
   серверной проверки (`range + 3`); стоит либо ограничить, либо честно подписать в
   описании настройки «на Grim>3.0 удары отменяются».
4. **`AutoPotion` «Пакетные».** Три риска сразу (`AimModulo360`, `AimDuplicateLook`,
   `Post`). Вариант: накапливать yaw (как `RotationController`), не дублировать
   углы и не вставлять `Rot`-пакеты в середину тика — либо вообще отказаться от
   пакетной ротации в пользу `clientLock`-пути.
5. **Порядок свинга** привязать к версии клиента (см. §3.3), режим «1.8» оставить
   как модель кулдауна.
6. **Мелочь, но полезно:** при `gcd == 0` и при «сырых» доводках pitch держать
   квантование обеих осей (защита от `Baritone`).

## 6. Чего я не проверял

* Это разбор **исходников**, а не живое тестирование: ни один флаг здесь не получен
  прогоном клиента против сервера. Реальный сервер может стоять на другой сборке
  (кастомный `config.yml`/`punishments.yml`, `experimental-checks: true`, ViaVersion,
  Paper/Folia-специфика, аддоны Grim), и это меняет пороги и набор проверок.
* Проверки, которых у нас нет в поле зрения (NoSlow, FastBreak, Vehicle,
  GroundSpoof, Explosion/Knockback), я перечислил только как «есть»; их условия под
  конкретные модули Swerve не разобраны.
* Синхронизация времени/TPS и `TimerCheck`: как наша коррекция темпа по TPS
  (`docs/PvPMath.md` §3.1) соотносится с Grim-таймером, не проверялось — надо
  смотреть `TimerCheck`/`NegativeTimerCheck` отдельной итерацией.
* 2.3.67 не знает про `ServerboundClientTickEndPacket` (в дереве нет ни одного
  упоминания `TICK_END`) — на этой версии tick-end пакет нам ни помогает, ни мешает;
  на 2.3.69+ помогает `canSkipTicks()`/`supportsEndTick()`.
