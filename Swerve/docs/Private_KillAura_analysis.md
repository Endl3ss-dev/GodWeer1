# KillAura в клиенте `Private` (1.16.5) — разбор перед портом в Swerve (1.21.4)

Документ описывает **только** код `Private`. Исходники Swerve не читались и не менялись
(по условию задачи клиент будет переписан). Все утверждения ниже сверены с файлами
репозитория; ссылки даны в формате `path:line`.

Корень: `Private/src/java/org/privates/client/`

---

## 0. Главное, что меняет план порта

1. **Модуль `KillAura.java` не подключён.** В `managers/module/ModuleManager.java:92`
   регистрируется `new AuraNew()`. Класса `KillAura` в списке модулей нет вообще
   (`grep KillAura ModuleManager.java` — пусто).
2. **Имена перепутаны местами.** У класса `KillAura` аннотация
   `@ModuleInfo(name = "AuraNew", ...)` (`KillAura.java:86`), у класса `AuraNew` —
   `@ModuleInfo(name = "Attack Aura", ...)` (`AuraNew.java:88`). То есть на экране
   модулей «Attack Aura» — это `AuraNew.java`, а «AuraNew» — мёртвый `KillAura.java`.
3. **В `KillAura` ротации отключены комментариями.** В `KillAura.initRotationElements()`
   (`KillAura.java:401-449`) присваивания `currentRotationElement` закомментированы,
   `updateRotation(...)` в `GameUpdateEvent` тоже (`KillAura.java:546`). Карта элементов
   создаётся и не используется.
4. **Реальный референс — `AuraNew.java` (1752 строки, 221 настройка).**
   Именно он: выбирает цель через `TargetComponent`, ведёт ротацию через
   `IRotationElement` (`AuraNew.java:981`), бьёт, синхронизируется с `ElytraTarget`
   и `Freezing`. `KillAura.java` (1035 строк, 170 настроек) — более старая/урезанная
   ветка, полезна только как второй вариант тех же формул.

**Вывод:** портировать нужно `AuraNew` + его зависимости; `KillAura` — как справочник
альтернативных веток логики (например, `smartHitbox`/`getVisibleTargetPoint`).

---

## 1. Карта архитектуры

```
AuraNew (модуль, COMBAT)
├── TargetComponent .......... выбор цели (общий на клиент, файл-синглтон)
│   └── Targets (модуль) ..... фильтры и режим сортировки (autoEnabled, allowDisable=false)
│   └── AntiBot .............. отсев ботов
├── IRotationElement ......... сменные алгоритмы ротации (14 подключено)
│   └── AbstractRotationElement (общая математика, aim-point, видимость)
│       └── CustomRot ........ главный элемент (1601 строка)
├── RotationComponent ........ применение ротации к игроку (тики/кадры), приоритеты
├── SmoothRotationComponent .. второй канал ротации (ускорение/замедление)
├── FreeLookComponent ........ точка возврата после AIM
├── ElytraAuraLogic .......... отдельная ветка ротации на элитре
├── HumanRotationTrainer ..... режим "Neural"/"Training"
├── utils/rotation/* ......... AuraUtil, RayTraceUtil, RotationUtil, SensUtil, GCD...
├── utils/math/PerfectDelay .. гейт по времени между ударами
├── utils/player/{PlayerUtil, InvUtil, MoveUtil}
├── common/impl/taskript/Script ... отложенные действия по тикам (рекойл, grim-ротация)
└── common/impl/viaversion/fixes/AttackOrder.java ... порядок swing/attack по версии протокола
```

---

## 2. Инвентарь файлов

| Файл | LOC | Роль | Состояние |
|---|---|---|---|
| `managers/module/impl/combat/AuraNew.java` | 1752 | боевой модуль «Attack Aura» | зарегистрирован |
| `managers/module/impl/combat/KillAura.java` | 1035 | старая ветка, ротации закомментированы | не зарегистрирован |
| `managers/module/impl/combat/auraelement/IRotationElement.java` | 14 | контракт элемента ротации | ок |
| `.../auraelement/AbstractRotationElement.java` | 220 | база: aim-point, видимость, лерп | ок |
| `.../auraelement/impl/CustomRot.java` | 1601 | основная ротация (режим «Custom») | ок |
| `.../auraelement/impl/*.java` (46 файлов) | ~12 900 | ротации под сервера/античиты | 14 подключены |
| `managers/component/impl/rotation/RotationComponent.java` | 324 | применение ротации, коррекция движения | ок |
| `managers/component/impl/rotation/SmoothRotationComponent.java` | 145 | второй канал ротации | ок |
| `managers/component/impl/rotation/Rotation.java` | 39 | пара yaw/pitch + yaw/pitch камеры | ок |
| `managers/component/impl/rotation/FreeLookComponent.java` | 43 | свободный взгляд / точка возврата | ок |
| `managers/component/impl/target/TargetComponent.java` | 168 | кэш сущностей, фильтр, сортировка | ок |
| `managers/module/impl/client/Targets.java` | 39 | настройки таргетов | ок |
| `managers/module/impl/combat/AntiBot.java` | 138 | детект ботов (Matrix и др.) | ок |
| `managers/module/impl/combat/ElytraAuraLogic.java` | 559 | ротация/удержание на элитре | ок |
| `managers/module/impl/combat/Criticals.java` | 304 | криты (слушает `AttackEvent`) | ок |
| `managers/module/impl/combat/HitBox.java` | 591 | расширение хитбокса (слушает `AttackEvent`) | ок |
| `utils/rotation/AuraUtil.java` | 176 | дистанция до AABB, FOV, падение/криты | ок |
| `utils/rotation/RayTraceUtil.java` | 150 | трассировка сущностей/блоков | ок |
| `utils/rotation/RotationUtil.java` | 119 | углы, дельты, вектор направления | ок |
| `utils/rotation/SensUtil.java` / `utils/other/GCDUtils.java` | 25 / — | квантование под мышь (GCD) | дублируют друг друга |
| `utils/rotation/{HumanRotationTrainer,StealthRotationSystem,RotationPlaner,JitterUtil}.java` | 538/567/82/115 | нейро-ротация, «скрытная», планировщик, джиттер | вспомогательные |
| `utils/math/PerfectDelay.java` | 49 | задержка между ударами | ок |
| `utils/server/ServerTPS.java` | 45 | TPS из `SUpdateTimePacket` | ок |
| `common/impl/taskript/Script.java` | — | шаги по тикам/мс | ок |
| `common/impl/viaversion/fixes/AttackOrder.java` | 19 | swing→attack для ≤1.8, иначе attack→swing | ок |

**Миксинов в снапшоте нет.** `src/resources/mixins.excellent.json` объявляет пакет
`org.privates.client.mixin.mixins` и один миксин `render.MixinStuckInBodyLayer`, но
каталога `mixin` в репозитории нет (`find -type d | grep -c mixin` → 0). См. раздел 9.

---

## 3. Поток исполнения

### 3.1 Ивенты, которые слушает аура

`AuraNew`:

| Ивент | Приоритет | Что делает |
|---|---|---|
| `PacketEvent` | default | `CHeldItemChangePacket`/`CAnimateHandPacket` → `perfectDelay.reset(550/450 мс)` (+рандом в «Спукитайм»), в «Фантайм - новая» ставит `shouldSnapBack` |
| `MoveEvent` | default | считает `canCrit` по падению за тик |
| `UpdateEvent` | default | `updateTarget()` + `updateAttack()` — основная боевая петля |
| `GameUpdateEvent` | `LOWEST` (-500) | ротация: `Neural`/`Training`/элитра/`currentRotationElement.updateRotation(...)` |
| `MoveInputEvent` | default | sprint-reset через `event.setForward(0)`, autoJump |
| `WorldLoadEvent`, `WorldChangeEvent` | default | `reset()` + переинициализация элемента |
| `Render2DEvent`, `Render3DLastEvent` | default | предупреждение LonyGrief+RayTrace, отрисовка элитра-линий |
| `AttackEvent` | — | **аура его не публикует**; слушают `Criticals`, `HitBox`, `NeuroSelf`, `KillSound` и др. |

`EventPriority` (`api/events/orbit/EventPriority.java`): `HIGHEST=500 ... LOWEST=-500`,
список слушателей сортируется по убыванию приоритета, значит `LOWEST` выполняется
**последним** — аура ставит финальную ротацию после модулей движения/фрикама.

### 3.2 Порядок внутри тика

1. `UpdateEvent` → `updateTarget()` → `TargetComponent.getTarget(range)`.
2. → `updateAttack()`: `checkReturn()` → `shieldBreaker()` → условие удара.
3. `GameUpdateEvent` (последним): `currentRotationElement.updateRotation(...)`, внутри —
   `RotationComponent.update(new Rotation(yaw, pitch), yawSpeed, pitchSpeed, 0, 5)`.
4. `RotationComponent` применяет поворот к `mc.player.rotationYaw/Pitch` (режим «Тики»)
   либо в `Render2DEvent` с частотой кадров (режим «FPS»).
5. `MoveInputEvent` → `RotationComponent` правит ввод (`fixMovement`/`fixMovement2`/«Грубая»),
   чтобы игрок не терял направление движения при повороте.

---

## 4. Логика атаки (`AuraNew.updateAttack`, `:1247`)

### 4.1 Гейты перед ударом

```java
checkReturn()          // «не бить когда ешь», «бить только с оружием»
shieldBreaker()        // смена на топор → удар → возврат слота
shouldAttack() && rayTrace() && AuraUtil.getStrictDistance(target) < attackDistance()
```

* `getStrictDistance` (`AuraUtil:78`) — расстояние от **глаз** до ближайшей точки AABB цели
  (не `getDistance` по центрам). Аналог `attackRange` в ваниле не используется.
* `attackDistance()` (`:1599`) — `max(gameMode.extendedReach() ? 6 : 3, attackRange)`;
  на элитре — `attackRange` при `onlyCrits`.
* `rayTrace()` (`:1578`) — `RayTraceUtil.rayTraceEntity(rotationYaw, rotationPitch, dist, target)`;
  отключается настройкой `enableRayTrace`; на элитре — `true`.
* `checkReturn()` (`:1550`) — `isUsingItem` + чекбокс «Не бить когда ешь»,
  `isHoldingWeapon()` (меч/топор) + «Бить только с оружием».

### 4.2 Темп ударов

* `PerfectDelay` (`:1482` в `shouldAttack`): `perfectDelay.cooldownComplete(cooldown)`,
  где `cooldown = getDynamicCooldown()` (`:1223`): 250 мс (пусто) / 550 мс (меч) /
  850 мс (топор) / 450 мс (прочее), либо `customSwordCooldown`.
* Внутри `attackEntity` (`:1346`) дополнительный гейт:
  `attackMode.is("1.9") && now - lastAttackTime < cooldown → return`.
* `getMsCooldown()` (`AuraNew.java:1189`) — из атрибута `ATTACK_SPEED`:
  `(1/speed)*1000*(1-0.2)`, для 4.0/4.4/4.8 → 450 мс, минимум 450,
  «байпас CPS» добавляет +50 мс каждые 7-й удар (`cpsBypassTrigger`).
* Режим «1.8» (`updateAttack:1315`): CPS 8–20 с разбросом `randomLerp(0.8, 1.2)`,
  интервал `1000/(cps*variation)`, вызов из того же тика.
* `cooldownComplete()` (`:1574`) — `getCooledAttackStrength(1.5F) >= 0.93F`.

### 4.3 Криты

* `shouldAttack()` (`AuraNew.java:1482`) — матрица ветвей:
  `onlyCrits` → `shouldCritical() && fallDistance>0 && canCrit && isBlockAboveHead`;
  `smartCrits` → добавляет «прыжок с земли под блоком»:
  `isBlockAboveHead && !jump && !canCrit && isOnGround && collidedVertically`;
  `randomCrits` → +30 % шанс.
* `canCrit` считается в `MoveEvent` (`:933`): `!event.isToGround() && from.y > to.y && nextFallDistance != 0`.
* `isBestMomentToHit` (`AuraNew.java:1154`) — «лучший момент»:
  `convenientFallOffset()` (учитывает `motion.y < -0.0784` и `ticksOnGround > 6`),
  `getYCapacityOnPlayerPos(2)` (сколько места над/под игроком по 4 лучам),
  затем список извинений: вода/лава/web/лестница/элитра/лошадь/слепота/левитация/
  slow falling/полёт/`y < -64`.
* `shouldCritical()` (`:1561`) — инверсия списка запретов (левитация, вода, элитра,
  лестница, пассажир, web). Не путать с `shouldAttack()` — это разные методы.

### 4.4 Sprint reset (`updateAttack:1260`)

| Режим | Действие |
|---|---|
| `Обычный` | `p = 1` → в `MoveInputEvent` один тик `setForward(0)` |
| `Легитный` | случайная задержка 15–50 мс, отпускание forward на 1–2 тика |
| `Пакетный (RAGE)` | `CEntityActionPacket(STOP_SPRINTING)` + `(START_SPRINTING)` |
| `Обычный (RAGE)` | `setSprinting(false)` → `setSprinting(true)` |

Перед ударом при спринте (не в RAGE-режимах) стоит `if (mc.player.isServerSprintState()) return;` —
то есть удар откладывается, пока сервер не подтвердил снятие спринта.

### 4.5 Щит

`shieldBreaker()` (`:1537`): если у цели `getActiveItemStack() == SHIELD`, есть топор
(`InvUtil.getAxeSlot()`) и `rayTrace()` — `SWAP`-клик на слот топора, удар, `SWAP` обратно
(`ClickType.SWAP`, `InvUtil.clickSlot`, пакетный режим).

### 4.6 Порядок swing/attack

`AttackOrder.java:16` — для протокола ≤ 1.8 сначала `swingArm`, затем
`playerController.attackEntity`; для 1.9+ наоборот. В `AuraNew.attackEntity` (`AuraNew.java:1426`)
используется ванильный порядок `attackEntity` → `swingArm` (фикс применяется в Via-слое).

---

## 5. Таргетинг

`TargetComponent` (`managers/component/impl/target/TargetComponent.java`):

* кэш `entityList` + пересборка в один поток при изменении `getCountLoadedEntities()`;
* `getTarget(range)` держит текущую цель, пока она валидна и в радиусе (`:103`);
* `isValid` (`:120`): не `FakePlayer`, `Health>0 && isAlive`, не `mc.player`,
  не `ArmorStand`, «сквозь стены» (`canEntityBeSeen`), «Невидимые», игроки — `AntiBot`,
  друзья (`friendManager`), «Голые» (`getTotalArmorValue()`), «Тиммейты» (`isOnSameTeam`),
  животные/мобы по чекбоксам;
* сортировка (`:143`) по режиму `Targets.sortMode()`:
  `Адаптивный` (броня → FOV → дистанция → HP), `Дистанция`, `Здоровье`, `Наводка` (FOV).
  Все — `Comparator`-цепочки на каждом тике (для порта это горячий путь, надо переписать
  без стримов/лямбд).

`Targets` (`managers/module/impl/client/Targets.java`): `autoEnabled = true`,
`allowDisable = false` → модуль всегда включён, настройки общие для всех аур.

---

## 6. Ротации

### 6.1 Два канала

| Компонент | Роль | Отличие |
|---|---|---|
| `RotationComponent` | основной | приоритеты (`currentPriority`), таймаут AIM→RESET, режимы «Тики»/«FPS»/«Продвинутая», возврат в `FreeLook` |
| `SmoothRotationComponent` | второй | без FPS-ветки, отдельные ускорение/замедление, вызывает `MoveUtil.fixMovement` |

`RotationComponent.update(...)` (`:221`) — публичный статический вход:
`update(target, yawSpeed, pitchSpeed, yawReturnSpeed, pitchReturnSpeed, timeout, priority, clientRotation)`.

* Приоритет: `if (instance.currentPriority() > priority) return;` — больший приоритет «сильнее».
* `updateRotation` (`:274`): `yawDelta = wrapDegrees(target.yaw - player.rotationYaw)`,
  шаг `min(|delta|, speed)`, затем **квантование** `SensUtil.getSens(...)` (GCD), pitch в `[-90,90]`.
* `idleTicks > timeout` → `RESET` → плавный возврат в `FreeLookComponent` (`:158`).
* `MoveInputEvent` (`:56`): режимы коррекции движения — `Незаметная` (`fixMovement`),
  `Сфокусированная` (`fixMovement2`), `Грубая` (`fixMovementToTarget` — перебор 8 вариантов).

### 6.2 Элементы ротации

Контракт `IRotationElement`: `updateRotation(parent, target, mc, currentLerpRotation)`,
`onEnable(parent)`, `onDisable(parent)`; база `AbstractRotationElement` даёт
`calculateSmartRotation`, `getVisibleTargetPoint` (12 точек AABB + `isPointVisible`),
`wrapLerp`, `applyGaussianJitter`, `applyRotation`.

В каталоге `auraelement/impl` 46 файлов (~12.9k строк). В `AuraNew` подключено **14**:

```
Custom→CustomRot            Test→SlothAiRotation      TestNew→SmoothNewRot
Повторяюшая→RepetitiveRot   RW→RWRotation             Intave→IntaveRot
Matrix→MatrixRot            HW→HWRot                  LonyGrief→UniAcRot
СпукиТайм→SmoothNewRot      HVH→ReallyWorldRot        Smooth→SmoothUpdateRot
Nova→NovaRot                Neuro Self→NeuroSelfRot
```

Обратите внимание: **имя настройки и имя класса не совпадают** (`СпукиТайм` — это
`SmoothNewRot`, `HVH` — `ReallyWorldRot`). Остальные 32 файла (`HolyWorldNew`,
`HumanizedRot`, `WellMineRot`, `AiRotation`, `NeuroRotation`, `FunTime*` и т.д.) —
либо подключены в мёртвом `KillAura`, либо не подключены нигде. Список режимов
в `componentMode` (`AuraNew.java:195`) и карта элементов тоже расходятся
(в списке есть `Фантайм`, `СкупиДуел`, `Neural`, `Training`, которых в карте нет —
они обрабатываются отдельными ветками).

### 6.3 Конвейер `CustomRot.updateRotation` (`:59-181`) — порядок стадий

1. `updateLookUpBehavior` — редкий отвод взгляда вверх (уникальная «фишка»).
2. `shouldRotate` / `applyDisableRotationReturn` — паузы, реакции, усталость.
3. `getCustomAimPoint` → мультипоинт: `initializeBodyParts` (N точек по высоте + разброс
   x/z ±0.15), выбор `aimPointMode`: `Динамический` (10 % шаг), `Ближайшая`,
   `Циклический`, `Случайный`, `Взвешенный` (вес `1/(d+1) × видимость`),
   `Прогнозирующий` (по `motion.y`); затем `smoothAimPointTransition` (lerp к точке).
4. `applyBiases`: вертикальный (`Голова/Верхняя грудь/Грудь/Живот/Ноги/Адаптивное` по HP)
   и горизонтальный (`Центр/Левая/Правая/Случайная/Циклическое`).
5. `randomRotationStart` — стартовый перелёт влево/вправо.
6. `applyHumanization` — шум ±factor, `combatMode`/`stealthMode` при `advancedAntiCheat`.
7. `applyAimDrift`, `applyNaturalSway`, `applyMicroMovements`, `applyEyeTracking`.
8. `limitRotation` (лимит дельты за тик).
9. `calculateAdaptiveSpeed` — база `yawSpeed/pitchSpeed` + FPS-адаптация + `adaptiveSpeed`
   + `angleBasedSpeed` + `distanceBasedSpeed` + `targetVelocityMatching` + `fatigueMultiplier`
   + **`hitSpeedFix`** (ускорение, когда цель в радиусе удара).
10. `applyAdvancedSmoothing` — интерполяция (`Линейная/Плавная/Экспоненциальная/Синусоидальная/
    Кубическая/Квадратичная/Elastic/Bounce`), ускорение/замедление по знаку дельты.
11. `applyAdvancedJitter` — 8 типов (Случайный, Синус, Косинус, Спираль, Perlin, Simplex,
    Комбинированный, Волновой), запрет повторов паттернов.
12. `applyStepRotation`, `applyJaggedRotation`, `applyMaxPitchLimit`, `applyLegacyRotation`.
13. `normalizeAngles` (переход через 360°).
14. `applyLookUpOffset`, `applyPullbackAfterHit` (отвод после удара).
15. **GCD**: `strictGCD` — `getSensitivity(x)`, иначе `getSensitivity(x ± 0.005)`.
16. `applyPixelPerfect` (при `advancedAntiCheat`).
17. `parent.setLerpRotation(smoothed)` → `RotationComponent.update(new Rotation(...), speeds, 0, 5)`.

Внутри почти каждая стадия берёт **случайное значение из диапазона** настройки
(`min + random*(max-min)`) — то есть настройки это `SliderSettingRand` (два значения).
Для порта: это `FloatSetting`-пары или свой `RangeSetting`, иначе теряется вся «рандомность».

### 6.4 Квантование под мышь (GCD)

`SensUtil` (и его дубль `GCDUtils`):

```java
gcdValue     = (sens*0.6 + 0.2)^3 * 8 * 0.15
deltaMouse   = round(delta / gcdValue)
getSens(delta) = deltaMouse * gcdValue
```

Это ограничение поворота сеткой реальной чувствительности — обязательный элемент
для «непалевной» ротации; в 1.21.4 формула та же, меняется только доступ к настройке
(`mc.options.sensitivity().get()`).

---

## 7. Утилиты, важные для порта

| Утилита | Метод | Что делает |
|---|---|---|
| `AuraUtil` | `getClosestVec(vec, AABB)` | кламп точки в AABB (кратчайшая точка) |
| | `getStrictDistance(entity)` | длина `eye → ближайшая точка AABB` |
| | `getClosestTargetPoint(...)` | перебор сетки 0.1 с трассировкой (дорого, но точно) |
| | `calculateRotation(target)` | `(rawYaw, rawPitch, yawDelta, pitchDelta)` |
| | `calculateFOVFromCamera(target)` | `hypot(yawDelta, pitchDelta)` от камеры |
| | `isPlayerFalling(onlyCrit, onlySpace)` | готовность к криту |
| `RayTraceUtil` | `rayTraceEntity(yaw, pitch, dist, entity)` | попадает ли луч в AABB |
| | `calculateRayTrace(...)` | сущность vs блок, с `ignoreBlocks` |
| | `isViewEntity(...)` | `ProjectileHelper.rayTraceEntities` |
| | `getVectorForRotation(pitch, yaw)` | вектор взгляда |
| `RotationUtil` | `calculate(Entity)` / `(from,to)` / `(vec)` | углы на цель |
| | `getYawDifference/getPitchDifference` | дельты до центра |
| | `getDirectionVector(yaw,pitch)`, `calculateFov` | по косинусу |
| `PerfectDelay` | `reset(delay)`, `cooldownComplete()` | таймер с обходом `NoSlow`/`AutoTotem` |
| `ServerTPS` | `getTPS()` | TPS из пакета времени (в `KillAura` — `syncTPS`) |
| `Script` | `addTickStep(n, action)` | очередь действий по тикам (рекойл, grim-ротация) |
| `MoveUtil` | `fixMovement`, `fixMovement2` | коррекция ввода под ротацию |

---

## 8. События

20 классов в `managers/events/player/` + `other/`, `render/`, `world/`.

| Ивент | Поля | Кто слушает (по ауре) |
|---|---|---|
| `UpdateEvent` | singleton | `AuraNew` (цель+удар), `RotationComponent`, `SmoothRotationComponent` |
| `GameUpdateEvent` | singleton | `AuraNew` (ротация, `LOWEST`), `AntiBot` |
| `MoveEvent` | from/to/motion/toGround/aabb | `AuraNew` (`canCrit`) |
| `MoveInputEvent` | forward/strafe/jump | `AuraNew` (sprint reset), `RotationComponent` (коррекция) |
| `PacketEvent` | action/packet | `AuraNew` (сброс `PerfectDelay`), `ServerTPS`, `AntiBot` |
| `AttackEvent` | `Entity target`, cancellable | `Criticals`, `HitBox`, `NeuroSelf`, `KillSound`, ... |
| `RotationEvent` | yaw/pitch/partialTicks | общий канал ротации |
| `PlayerLookEvent` | `Vector2f rotation` | подмена взгляда |
| `MotionEvent` | onGround | `Criticals` |
| `WorldLoadEvent` / `WorldChangeEvent` | — | сброс состояния |

**Ни один из этих ивентов в снапшоте не публикуется** — `new AttackEvent`, `new UpdateEvent`
и т.п. в коде нет (кроме объявлений). Публикация была в миксинах/нативной части, которых
в репозитории нет.

---

## 9. Миксины: чего не хватает и что они давали

Каталог `org.privates.client.mixin.mixins` **отсутствует**, `mixins.excellent.json`
объявляет только `render.MixinStuckInBodyLayer` (файла тоже нет). При этом код ауры
обращается к членам, которых нет в ванильном 1.16.5:

| Обращение в коде | Откуда должно приходить |
|---|---|
| `mc.player.nextFallDistance` (`AuraNew:938`, `KillAura:512`) | миксин в `Entity`/`PlayerEntity` — «прогноз» падения за следующий тик |
| `mc.player.validDistance(target, range, true)` (`AuraNew:1110`) | миксин-утилита дистанции (учитывает хитбокс/расширение) |
| публикация `UpdateEvent`/`GameUpdateEvent`/`MoveEvent` | миксины в `Minecraft.tick`, `PlayerEntity.travel`, `NetworkManager` |
| публикация `AttackEvent` | миксин в `PlayerControllerMP.attackEntity` |
| `RotationEvent`/`PlayerLookEvent` (подмена взгляда) | миксины в `Entity.updateRidden`/`EntityPlayerSP` |
| `PlayerSS.collide/getBlock` (`AuraUtil:47`) | утилита на уровне мира/миксинов |

**Для порта это самое важное:** половина «магии» ауры — это не код модуля, а патчи
сущности и публикация ивентов. В Swerve их придётся воспроизвести заново под 1.21.4
(`Player.tick`, `LocalPlayer.sendPosition`, `MultiPlayerGameMode.attack`, `ClientPacketListener`).

---

## 10. Маппинг 1.16.5 → 1.21.4

Проверено по байткоду `minecraft-merged…1.21.4.jar` (официальные маппинги, те же имена,
что в Swerve) с помощью `Swerve/tests/check_mc_api.py`.

| 1.16.5 (Private) | 1.21.4 (Swerve) | Проверено |
|---|---|---|
| `MathHelper` | `net.minecraft.util.Mth` | да |
| `MathHelper.wrapDegrees` | `Mth.wrapDegrees(float/double/int)` | да (3 перегрузки) |
| `mc.player.rotationYaw/Pitch` | `player.getYRot()/getXRot()` + `setYRot/setXRot` | да |
| `mc.player.getPositionVec()` | `player.position()` | да |
| `mc.player.getMotion()` | `player.getDeltaMovement()` | да |
| `mc.player.getEyePosition(pt)` | `player.getEyePosition(pt)` (`Entity`) | да |
| `mc.player.getEyeHeight()` | `getEyeHeight(Pose)` | да |
| `mc.player.isOnGround()` | `player.onGround()` | да |
| `mc.player.getDistance(e)` | `player.distanceTo(e)` | да |
| `mc.player.canEntityBeSeen(e)` | `LivingEntity.hasLineOfSight(Entity)` (+ варианты с `ClipContext`) | да |
| `mc.player.getCooledAttackStrength(f)` | `Player.getAttackStrengthScale(float)` | да |
| `mc.player.getHeldItemMainhand()` | `LivingEntity.getMainHandItem()` | да |
| `mc.player.isHandActive()` | `LivingEntity.isUsingItem()` | да |
| `mc.player.getActiveItemStack()` | `getUseItem()` | — |
| `mc.player.inventory.currentItem` | `player.getInventory().getSelectedSlot()` | — |
| `mc.playerController.attackEntity(p,e)` | `mc.gameMode.attack(p,e)` (`MultiPlayerGameMode.attack`) | да |
| `mc.playerController.extendedReach()` | `mc.gameMode.isAlwaysFlying()`? / `hasInfiniteItems()` — пересмотреть | — |
| `mc.player.swingArm(Hand.MAIN_HAND)` | `player.swing(InteractionHand.MAIN_HAND)` | — |
| `mc.world.getEntitiesWithinAABB` | `level.getEntitiesOfClass(Class, AABB, Predicate)` | да |
| `mc.world.rayTraceBlocks(RayTraceContext)` | `level.clip(new ClipContext(...))` → `BlockHitResult` | да (`BlockGetter.clip`) |
| `RayTraceContext.BlockMode` | `ClipContext.Block` | да (`ClipContext` есть) |
| `EntityRayTraceResult` | `EntityHitResult` | да |
| `ProjectileHelper.rayTraceEntities` | `ProjectileUtil.getEntityHitResult(...)` (есть 3 перегрузки, в т.ч. с `float`) | да |
| `CPlayerPacket.PositionRotationPacket` | `ServerboundMovePlayerPacket.PosRot` | да |
| `CEntityActionPacket(STOP/START_SPRINTING)` | `ServerboundPlayerCommandPacket` (SPRINT) + `ServerboundPlayerActionPacket` | — |
| `Effects.X` | `MobEffects.X` | да |
| `Attributes.ATTACK_SPEED` | `Attributes.ATTACK_SPEED` | да |
| `mc.getRenderPartialTicks()` | `mc.getDeltaTracker().getGameTimeDeltaPartialTick(false)` | да |
| `AxisAlignedBB.grow/expand` | `AABB.inflate/expandTowards` | — |
| `mc.gameRenderer.getActiveRenderInfo()` | `mc.gameRenderer.getMainCamera()` | — |
| `mc.gameSettings.mouseSensitivity` | `mc.options.sensitivity().get()` | — |
| `mc.player.getAttributeValue(...)` | `player.getAttributeValue(Attribute)` | — |
| `mc.getDebugFPS()` | `mc.getFps()` | — |

`—` = требуется отдельная проверка при реализации (не проверял в этой задаче).

Отдельно: `Vector3d/Vector2f/AxisAlignedBB/MathHelper` из 1.16.5 → `Vec3/org.joml.Vector2f/
AABB/Mth` в 1.21.4. `org.joml` уже используется в Private (`Rotation.camera()` возвращает
`Vector2f` из joml) — это упрощает перенос математики.

---

## 11. Что придётся писать с нуля в Swerve

1. **Публикация ивентов** (`Update/GameUpdate/Move/MoveInput/Packet/Attack`) — в Swerve свой
   `EventBus` и свой набор ивентов (`events/`), аура должна слушать их, а не Private'овские.
2. **`nextFallDistance` и `validDistance`** — либо миксин, либо пересчёт из
   `getDeltaMovement()/fallDistance` (в 1.21.4 падение считается иначе: `fallDistance`
   обнуляется в `checkFallDamage`, а `nextFallDistance` не существует).
3. **Дистанция удара** — в 1.21.4 ванильный reach 3 блока задаётся атрибутом
   `Attributes.ENTITY_INTERACTION_RANGE` (плюс `BLOCK_INTERACTION_RANGE`), а не `3D/6D`
   из `playerController`. Формулу `attackDistance()` надо переписать на атрибуты.
4. **GCD** — читать чувствительность из `mc.options`, формула та же.
5. **`RotHitBox`/`HitBox`** — расширение хитбокса в 1.21.4 требует миксина в
   `Entity.getBoundingBox()`/`getPickRadius`, без этого аура будет «не доставать».
6. **`AttackOrder`** — в 1.21.4 Via-слой другой; при игре на 1.8-серверах порядок
   swing/attack надо реализовать в своём пакетном слое.
7. **`RotationComponent`** — переписать под Swerve-ивенты, убрав `BaritoneAPI` и
   `Instance`-локтор (в Swerve модули/сервисы достаются иначе).
8. **Стримы и лямбды** — `TargetComponent.find` и `findPriorityTarget` в `KillAura`
   построены на `stream().sort()`, в горячем пути это недопустимо: нужен один проход
   с компаратором.

---

## 12. Риски и подводные камни

* **Дубли и мёртвый код.** `KillAura.java` и `AuraNew.java` — форки одного файла;
  `GCDUtils` и `SensUtil` — одна формула дважды; `utils/rotation/aura/Rotation.java` —
  ещё одна (третья) копия GCD. При порте нельзя тащить оба/все три.
* **Мёртвые ветви: `componentMode.is(...)` для значений, которых нет в списке режимов.**
  Список режимов (`AuraNew.java:195`):
  `Custom, Smooth, HVH, Фантайм, LonyGrief, СкупиДуел, СпукиТайм, HW, RW, Intave,
  Matrix, Повторяюшая, TestNew, Neural, Training, Neuro Self`.

  Проверяется в коде, но в списке отсутствует:
  `ХолиВорлд`, `Снапы`, `Вулкан`, `Грим`, `Фантайм - снап`, `Фантайм - новая`,
  `Спукитайм`, `Легитная`, `Нейро`, `трррр`, `fdh`, `fgh`, `Сhfgh`.

  Отдельный случай: `componentMode.is("Спукитайм")` (`AuraNew.java:779,785`) —
  в списке `СпукиТайм` (заглавная `Т`), строки не равны → ветка не срабатывает никогда.
  При этом `СпукиТайм` в списке есть, но нигде не проверяется.

  Итог: **12 из 16** значений списка ни разу не проверяются, а ~13 проверяемых строк
  недостижимы. Значит `updateAttack`/`attackEntity` реально работают только в ветках
  `Custom`/`Smooth`; «Фантайм - новая», «ХолиВорлд» и «Снапы» — мёртвый код.
  В `KillAura.java` то же самое (`Grim 1.16.5`, `Vulcan + Grim`, `SmotionElytra`, ...).
  **При порте это нельзя переносить как есть** — либо чинить имена, либо выкидывать ветки.
* **Логика, зависящая от сервера.** `PlayerUtil.isHoly()` (бренд сервера), `isFuntime()`,
  `ViaUtil.allowedBypass()` — переопределяют поведение; в Swerve этих проверок нет.
* **Побочные эффекты в сеттерах.** `AuraNew.toggle()` переинициализирует элементы ротации,
  `onDisable()` дёргает `humanRotationTrainer.flushModel()` — при перезаходе в мир
  (`WorldChangeEvent`) состояние сбрасывается полностью.
* **`SecureRandom` в горячем пути.** `randomLerp` в `AuraNew.java:1215` создаёт
  `new SecureRandom()` **на каждый вызов** — при 20 т/с и десятках вызовов это дорого;
  в `KillAura` то же место использует общее поле. При порте — одно поле/`ThreadLocalRandom`.
* **`Math.pow` в расчёте углов** (`AuraUtil.calculateRotation`) — при 20 т/с терпимо,
  но лучше `Math.hypot`.

---

## 13. Что не покрыто этим разбором (честно)

* **Миксины** — в снапшоте отсутствуют, поэтому их точное поведение (сдвиг границы AABB,
  момент публикации `AttackEvent`, подмена взгляда) восстановить нельзя; в разделе 9
  перечислено только то, на что опирается код.
* **Классы в `common/` и нативная часть** (`com.adl.nativeprotect.Native`,
  `Excellent.start()`, загрузчик событий на `LambdaMetafactory`) — читал поверхностно,
  к логике ауры они не относятся.
* **32 из 46 элементов ротации** не разбирались детально (только связь с `KillAura`).
  Если в задаче будет конкретный серверный режим — его файл нужно читать отдельно.
* **Таблица маппинга** проверена по байткоду только там, где стоит «да»; строки без
  отметки — кандидаты, требующие проверки при реализации.
* Поведение в игре не проверялось: запуск Minecraft в этой среде невозможен.

---

## 14. Рекомендуемый порядок порта (когда придёт задача)

1. Ивенты + их публикация (без них аура мертва).
2. `RotationComponent` + `FreeLookComponent` + GCD — база для любой ротации.
3. `TargetComponent` без стримов + `AntiBot` + друзья.
4. Каркас `AuraNew` (гейты, темп, криты, sprint reset, щит) — 1.21.4-атрибуты дистанции.
5. `CustomRot` по стадиям из раздела 6.3, начиная с aim-point и скорости.
6. Остальные элементы ротации — по мере необходимости, начиная с `SmoothNewRot`/`SmoothUpdateRot`.
7. `ElytraAuraLogic` — последним (самая изолированная ветка).

---

## 15. Статус порта в Swerve (шаг «ротации Intave/Matrix/HW»)

Портировано и подключено к модулю `KillAura`:

| Private (1.16.5) | Swerve (1.21.4) |
|---|---|
| `RotationComponent` | `utils/rotation/RotationController.java` (AIM/RESET/IDLE, приоритет, GCD-шаг) |
| `FreeLookComponent` | `utils/rotation/FreeLook.java` + redirect `LocalPlayer.turn` в `MixinMouseHandler`, камера — `MixinCamera` |
| `SensUtil` / `GCDUtils` | `utils/rotation/GcdUtil.java` |
| `Rotation` | `utils/rotation/Rotation.java` |
| `MoveUtil.fixMovement/fixMovement2`, `fixMovementToTarget` | `utils/player/MoveUtil.java`, режимы «Коррекция движения» |
| `IRotationElement` / `AbstractRotationElement` | `modules/impl/combat/rotation/RotationElement.java`, `AbstractRotationElement.java` |
| `IntaveRot` / `MatrixRot` / `HWRot` | `IntaveRotation` / `MatrixRotation` / `HWRotation` (скорости, тайминги и шум 1:1) |

Порядок тика: цель и ротация — `START_CLIENT_TICK` (до тика игрока, пакет позиции
уходит с новыми углами), удар — `END_CLIENT_TICK`. Смена режима ротации — как
`initRotationElements()`: старый элемент `onDisable`, новый `onEnable`.

Сознательно не портировано: остальные 13 значений `componentMode` (Custom/Smooth/
Фантайм/СпукиТайм/…), `ElytraAuraLogic`, `sprintReset`, `lockOnTarget`/
`targetFollow` — они относятся к Custom-ротации и её настройкам.

## 16. Правки после первого запуска в игре (жалобы на «половину функционала»)

Найдены и закрыты расхождения с `AuraNew`, из-за которых модуль почти не бил:

| Что было | Что стало (как в Private) |
|---|---|
| гейт крита отменял удар в спринте, но спринт никто не гасил → удары не проходили никогда | портирован `Sprint Reset`: `Обычный` (forward = 0 на тик, поле `p`), `Легитный` (`sprintTicks` 1..2 + отпускание через 15..50 мс) |
| кулдаун = `RATE_LIMIT_19_MS 500` + `getAttackStrengthScale >= 0.93` (наша выдумка, блокировала серию ударов) | `perfectDelay.cooldownComplete(getDynamicCooldown())` + проверка внутри `attackEntity`: меч 550, топор 850, пустая рука 250, прочее 450; режим «Кастомный меч» с ползунком |
| `isInLiquid` возвращал `false` в `shouldAttack` (запрет удара в воде) | в воде удар разрешён (`return true`), как в оригинале |
| `shouldCritical()` смешивал блокеры и fallDistance | разделено: `critConditionsClear()` (левитация/жидкость/элитра/лестница/пассажир/паутина) + `fallDistance > 0 && canCrit && headroom` по веткам `onlyCrits`/`smartCrits`/`randomCrits` |
| `smartDistance` (из мёртвого legacy KillAura) жёстко обрезал дистанцию до 3.0 | настройка удалена: в `AuraNew` её нет |
| приоритет ротаций «залипал» (Intave 4 → Matrix/HW 3), тело переставало крутиться после смены режима | `RotationController.resetPriority()` при смене элемента |
| камера никогда не смотрела на цель (молчаливая ротация) | новый режим `Ротации = Видимая` (по умолчанию): тело и камера вместе; `Intave`/`Matrix`/`HW` остались молчаливыми |

Отличие от Private по умолчанию: `Только криты` выключено (в Private `true`, но тогда
модуль молчит, пока игрок не в падении). Включённая настройка работает как в оригинале.

## 17. Второй круг правок по фидбеку (чувствительность камеры + криты)

### 17.1 «При поворотах камеры с включённой аурой чувствительность больше»

Причина — режим «Видимая» был по умолчанию: камера жёстко равна телу, а тело
каждый тик доворачивала аура (Intave — до 95°/тик). Движение мыши и шаг ротации
складывались, поэтому обзор «уезжал» сильнее, чем должен.

Исправление: поведение камеры вынесено в отдельную настройку **«Камера»**:

| Значение | Что делает |
|---|---|
| `Догоняет` (по умолчанию) | ротация молчаливая: тело крутится, камера полностью за мышью, чувствительность ванильная; если мышь не двигалась ≥250 мс, камера плавно (8/с) доворачивается к углам тела — видно, куда бьёт аура |
| `Свободная` | чистый silent из Private: камера никогда не догоняет |
| `Всегда на цели` | прежняя «Видимая»: камера = тело |

Дополнительно:
* свободный обзор включается на любом живом запросе ротации (а не только на первом),
  иначе после переключения режима камера оставалась приклеенной к телу;
* `stopRotation()` сливает углы тела с камерой, поэтому выключение ауры/потеря цели
  не дёргает обзор.

Режимы `Ротации` снова содержат только `Intave` / `Matrix` / `HW` — «Видимая» из
списка ротаций убрана (это была настройка камеры, а не алгоритм наведения).

### 17.2 «Только криты не работают»

Две причины:
1. гейт крита требовал мой `canCrit`, который считается в конце тика и в момент
   прыжка почти всегда ложен — удар не проходил вообще;
2. никто не подпрыгивал: `AuraNew.onInput` умеет `autoJump`, а в Swerve этой
   ветки не было, поэтому «Только криты» молчали, пока игрок не прыгнет сам.

Исправление:
* `critWindow()` повторяет условия ванильного `Player.attack`: `fallDistance > 0`,
  не на земле, не на лестнице, не в воде, без слепоты, не пассажир, **не в спринте**,
  плюс `isBlockAboveHead` из Private;
* `Только криты` снова включён по умолчанию (как в Private);
* портирован **«Авто прыжок»** (включён по умолчанию, в Private — выкл.): прыжок
  подаётся через `MoveInputEvent.jump`, когда есть цель и игрок на земле не в воде;
* сброс спринта теперь выполняется ДО гейта крита (в Private он живёт внутри гейта,
  но там гейт не проверяет спринт) — иначе «не в спринте» и сброс спринта
  блокировали друг друга и удар не проходил никогда.
