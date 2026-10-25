# NoWeb

Основной файл: `Swerve/src/main/java/swerve/modules/impl/movement/NoWeb.java`
Миксин: `Swerve/src/main/java/swerve/mixins/world/MixinWebBlock.java` (на `net.minecraft.world.level.block.WebBlock`)
Категория: `MOVEMENT`
Регистрация: `swerve.managers.ModuleManager` → `register(new NoWeb())`

## 1. Поведение

Модуль устраняет замедление от паутины для локального игрока. Доступно два режима.

### Режим «Обход» (по умолчанию)
- На `WebBlock.entityInside` вызывается миксин с `cancellable = true`.
- Если цель — локальный игрок и все guard'ы пройдены, вызывается `entity.resetFallDistance()`
  (при включённой настройке «Гасить урон падения»), после чего ванильный метод отменяется
  через `ci.cancel()`.
- `stuckSpeedMultiplier` не устанавливается → `Entity.move()` не умножает движение и
  **не обнуляет** `deltaMovement`. Полностью сохраняются инерция, гравитация, прыжок, спринт.
- Паутина физически перестаёт замедлять, визуально остаётся, ломается как обычно.

### Режим «Компенсация»
- Ванила отрабатывает полностью (`stuckSpeedMultiplier` ставится в `(0.25, 0.05, 0.25)` или
  `(0.5, 0.25, 0.5)` при эффекте `WEAVING`).
- На `RETURN` того же `entityInside` модуль пересчитывает `deltaMovement`:
  `delta = target / multiplier` по каждой оси; направление XZ — из нормализованного вектора ввода
  с клавиш `keyUp/keyDown/keyLeft/keyRight` и yaw игрока; вертикаль — `keyJump`/`keyShift`.
- Итоговая компонента ограничена сверху `MAX_TICK_SPEED = 7.5 б/т`, чтобы не словить ванильный
  `moved too quickly` (порог сервера `d² > 100`, т.е. ~10 б/т).

### Настройки (значения по умолчанию)
- `Режим` = `Обход` (`Обход` / `Компенсация`).
- `Скорость XZ` = `0.05` (видно только в «Компенсации»).
- `Скорость вверх Y` = `0.06` (видно только в «Компенсации»).
- `Скорость вниз Y` = `0.175` (видно только в «Компенсации»).
- `Только с вводом` = `true` (видно только в «Компенсации»).
- `Гасить урон падения` = `true`.
- `Не трогать в транспорте` = `true`.
- `Не трогать на элитрах` = `true`.

## 2. Физика 1.21.4

Факты получены разбором байткода `minecraft-merged 1.21.4`.

1. `WebBlock.entityInside(state, level, pos, entity)` создаёт `Vec3 multiplier = (0.25, 0.05, 0.25)`;
   если `entity instanceof LivingEntity && hasEffect(MobEffects.WEAVING)` — `(0.5, 0.25, 0.5)`;
   затем `entity.makeStuckInBlock(state, multiplier)`.
2. `Entity.makeStuckInBlock` вызывает `resetFallDistance()` и записывает `stuckSpeedMultiplier = multiplier`.
3. `Player.makeStuckInBlock` оборачивает super в `if (!abilities.flying)` — **в креатив-полёте
   паутина игрока не замедляет вообще**, и `stuckSpeedMultiplier` не ставится.
4. `Entity.move(type, movement)`:
   если `stuckSpeedMultiplier.lengthSqr() > 1.0E-7`, то `movement = movement.multiply(stuckSpeedMultiplier)`,
   `stuckSpeedMultiplier = Vec3.ZERO`, **`setDeltaMovement(Vec3.ZERO)`** — скорость обнуляется, а не умножается.
5. Порядок в тике: `LocalPlayer.tick → LivingEntity.tick → aiStep → travel → Entity.move` (**здесь**
   потребляется множитель прошлого тика) → в конце `aiStep`: `applyEffectsFromBlocks → checkInsideBlocks →
   BlockState.entityInside → WebBlock.entityInside → Player.makeStuckInBlock` (**здесь** ставится
   множитель для следующего тика).
6. Следствие: наша запись в `deltaMovement` в обработчике `entityInside` будет использована в `travel()`
   следующего тика, а затем ванила умножит её на множитель и обнулит. Поэтому формула «фактическая = target»
   даёт `delta = target / multiplier`.

## 3. Адаптация из AlphaDLC

| AlphaDLC (Yarn) | Swerve (Mojang 1.21.4) |
| --- | --- |
| `CobwebBlock.onEntityCollision` | `WebBlock.entityInside` |
| `MinecraftClient.getInstance().player` | `Minecraft.getInstance().player` (`LocalPlayer`) |
| `EventCobweb` + Guava `EventBus` | прямой вызов `module.onCobwebHead/onCobwebReturn` из миксина |
| `MoveUtil.setMotion(speed)` + yaw-таблица | точный `delta = target / multiplier` по нормализованному вводу |
| `player.input.playerInput.forward()` и т.п. | `Minecraft.options.keyUp/keyDown/keyLeft/keyRight.isDown()` |
| `player.getYaw()` | `LivingEntity.getYRot()` |
| `getVelocity()/setVelocity()` | `getDeltaMovement()/setDeltaMovement(...)` |
| `World` / `BlockPos` (Yarn) | `Level` / `net.minecraft.core.BlockPos` |
| `Vec3d` / `Box` / `MathHelper` | `Vec3` / `AABB` / `Mth` |
| `isPressed()` | `isDown()` |
| `isGliding()` | `isFallFlying()` |

## 4. Исправленные дефекты референса

1. **Магическая таблица yaw-диапазонов** → заменена на нормализацию вектора ввода и расчёт
   `delta = target / multiplier`. Диагональ не быстрее прямой; работает при любом yaw и спринте.
2. **Жёсткая запись Y** (`1.219` / `-3.5` / `0`) → явные настройки `Скорость вверх/вниз Y`;
   если ни `jump`, ни `shift` не нажаты — Y не переписывается (сохраняется текущая `deltaMovement.y`,
   которую ванила всё равно обнулит в `move()` для своего такта).
3. **Креатив-полёт** → отфильтрован через `player.getAbilities().flying`; в режиме «Обход» всё равно
   безопасно, т.к. ванила сама не ставит множитель.
4. **`MobEffects.WEAVING`** → множитель определяется в рантайме по той же логике, что в ванила.
5. **Потеря `resetFallDistance()`** при отмене → явно вызывается перед `ci.cancel()` и в «Компенсации».
6. **Null-guard'ы** → `Minecraft.getInstance().player == null || .level == null` и `entity == player`.
7. **Изоляция исключений** → `try/catch` в миксине, `Swerve.LOGGER.error(...)` + `module.setEnabled(false)`.
8. **Ветка `isGliding() → -1.5`** → заменена фильтром «Не трогать на элитрах» (по умолчанию `true`).

## 5. Сервер и античиты

Разбор `ServerGamePacketListenerImpl.handleMovePlayer` (1.21.4):

- Rubberband только при `d² > 100` за тик (~10 б/т) или при попытке влезть в коллизию блока.
- Сервер пересимулирует `ServerPlayer.move(MoverType.PLAYER, delta)` — у него свой `stuckSpeedMultiplier`,
  установленный серверной `WebBlock.entityInside`. При рассинхроне в консоль пишется `moved wrongly!`,
  но позиция клиента всё равно принимается через `absMoveTo(...)`.
- Освобождают от лога только `isChangingDimension()`, `isSleeping()`, `gameMode.isCreative()`, `SPECTATOR`.
- Предиктивные античиты (Grim-класс) симулируют ту же физику с множителем `(0.25, 0.05, 0.25)`.
  Любой полный обход для них — рассинхрон предсказания → флаг.

**Честный вывод:** «полный обход паутины без единого флага на предиктивном античите» физически
недостижим. Режим «Компенсация» существует именно для того, чтобы подобрать фактические значения
XZ/Y под порог конкретного сервера; подобрать «универсально безопасные» значения заранее нельзя.

## 6. Проверки

Проверено:
- Сигнатуры и расположение в раздевалке — по байткоду `minecraft-merged 1.21.4`:
  `WebBlock.entityInside(BlockState, Level, BlockPos, Entity)V` (protected),
  `Entity.makeStuckInBlock`, `Entity.move`, `Player.makeStuckInBlock`, `Player.getAbilities`,
  `LivingEntity.hasEffect(Holder)`, `MobEffects.WEAVING`.
- Точка внедрения: `@At("HEAD")` с `cancellable = true` и `@At("RETURN")` на одном и том же методе
  с полной сигнатурой параметров.

Не проверено:
- Полная Gradle/Loom-сборка и запуск клиента (нет среды).
- Поведение сторонних античит-плагинов (NCP, AAC, Grim, Vulcan, Matrix) — требует их конфигурации.
- Поведение в оффхенд-кейсах: быстрая смена `MobEffects.WEAVING` в середине движения, паутина в воде
  (physical.water + web пересекаются по `applyEffectsFromBlocks`), паутина внутри `WorldBorder`.

## 7. Взаимодействие с другими модулями Swerve

- `Speed` больше не пишет `deltaMovement`: после переработки (см. `docs/Speed.md`) он
  масштабирует ванильное ускорение в `travel()`. Добавка «Компенсации» под его множитель не
  попадает, то есть эффекты складываются, а не перемножаются, и перезаписи наших значений
  XZ/Y на другом такте больше нет. Отключать `Speed` не требуется.
- `ElytraFlight` работает только при `isFallFlying()` → наши guard'ы «Не трогать на элитрах» ему не мешают.
- `KillAura`/`SexAura` управляют поворотами, а не скоростью → пересечений нет.

## 8. Чек-лист ручной проверки перед выпуском

- [ ] Вход/выход из паутины на ходу (ходьба, спринт, бег назад).
- [ ] Диагонали (W+A, W+D, S+A, S+D) — скорость одинаковая, не быстрее прямой.
- [ ] Прыжок в паутине (режим «Обход» — обычный прыжок; «Компенсация» — `Скорость вверх Y`).
- [ ] Падение сквозь столб паутины с высоты — проверить, что урона нет при «Гасить урон падения».
- [ ] Sneak внутри паутины («Компенсация» — проседание со `Скорость вниз Y`).
- [ ] Паутина, стоящая в воде.
- [ ] Креатив-полёт сквозь паутину — не должно быть рывков («Обход»: безопасно; «Компенсация»: no-op).
- [ ] Полёт на элитрах сквозь паутину.
- [ ] Езда в транспорте (лошадь, лодка) сквозь паутину.
- [ ] Эффект `WEAVING` активен — скорость соответствует настройкам (компенсация учитывает другой множитель).
- [ ] Смена измерения / перезаход на сервер — модуль не падает, состояние не течёт.
- [ ] Выключение модуля прямо внутри паутины — ванильное поведение восстанавливается на следующем тике.
- [ ] Одновременная работа со `Speed` — ускорение и компенсация складываются, рассинхрона с сервером нет.
