# ElytraFlight — перенос из AlphaDLC

Основной файл: `src/main/java/swerve/modules/impl/movement/ElytraFlight.java`.
Модуль зарегистрирован в `ModuleManager`, категория `MOVEMENT`.

## Сохранённое поведение

- Единственная настройка: **«Задержка феера»**, 750 мс, диапазон 100–2000 мс, шаг 50.
- Элитры в нагрудном слоте: прыжок с земли, затем пакет начала полёта, локальное начало планирования и ракета.
- WASD задаёт направление относительно камеры: 0/±45/±90/±135/180°.
- Горизонтальная скорость — 1; движение с Space/Shift — 0.5 по горизонтали и ±0.5 по вертикали, pitch ∓25°.
- Только Space/Shift — вертикальная скорость ±1, pitch ∓90°. Без ввода — нулевая скорость.
- При включённой KillAura с целью модуль не меняет скорость и повороты, но продолжает использовать ракеты.
- Свободная камера, GCD-коррекция, возврат поворота и коррекция ввода перенесены из зависимостей исходника.
- Сохранены приоритеты одновременных клавиш: вперёд перед назад, влево перед вправо, Space перед Shift.
- Использование ракет повторяет `InventoryUtil.swapAndUseHvH`: руки → хотбар → инвентарь; пакетное переключение без смены локального выбранного слота. В резервной ветке ракеты остаются в слоте 8 — это поведение исходника, не ошибка переноса.

## Адаптация зависимостей

| AlphaDLC | Swerve |
| --- | --- |
| `EventPlayerUpdate`, `ClientPlayerEntityMixin.tick HEAD` | `MixinLocalPlayer.tick HEAD` → `ElytraFlight.onPlayerUpdate()` |
| `EventTick`, `MinecraftClientMixin.tick HEAD` | Fabric `START_CLIENT_TICK` → `ElytraFlightRotation.tick()` |
| `MoveInputEvent`, `KeyboardInputMixin` | Переиспользуемый `MoveInputEvent`, `MixinKeyboardInput.tick TAIL` |
| `RotationComponent`, `FreeLookComponent`, `GCDFixer` | `utils/movement/ElytraFlightRotation` (задача ElytraFlight: 360/360/360/360, timeout 0, priority 3) |
| `LookEvent`, `MouseMixin` | Перехват `LocalPlayer.turn` в `MixinMouseHandler`, после ванильной обработки чувствительности мыши |
| `RotationEvent`, `CameraMixin` | Перехват `Camera.setRotation` в `MixinCamera.setup` |
| `StopWatch` | Метка `System.currentTimeMillis()`, сравнение строго `>` |
| `InventoryUtil.swapAndUseHvH`, `NetworkUtils` | `utils/item/HvHItemUse`, стандартное соединение Minecraft |
| `ServerManager.serverSprinting` | `LocalPlayerAccessor.wasSprinting` — последнее отправленное ванилью состояние спринта; отдельного Sprint в Swerve нет |
| `Instance.get(KillAura.class)` | `ModuleManager.get(KillAura.class)`, добавлен getter цели |

Swerve фактически использует **Mojang mappings**, Minecraft 1.21.4. Mappings и зависимости проекта не изменены. При потере/замене локального игрока контроллер свободной камеры сбрасывается, чтобы не переносить захват мыши в другую игровую сессию. Камера наблюдения за другой сущностью не перехватывается.

## Осознанное отклонение

`ElytraFlightRotation.fixRotate` (свободная камера и поворот элиты) квантует дельту
как `round(дельта / шаг) * шаг`, где шаг — `getGCDValue()`. При чувствительности 0 %
шаг равен нулю: `round(x/0)*0 == 0.0` по обеим осям, `rotate()` не догоняет цель,
`resetting` залипает, свободная камера и поворот элиты встают. В метод добавлен выход
«без квантования» при неположительном или `NaN` шаге — единственная правка, которую
можно внести, не трогая сверяемые блоки.

Саму формулу `getGCDValue()` **не меняли**: она сверяется с `GCDFixer` AlphaDLC тестом
паритета, и расхождение с ванильной сеткой (свёрнутая форма даёт не GCD, а значение
ползунка) перенесено из исходника намеренно. Разобрать это против
`Entity.turn`/`MouseHandler.turnPlayer` и решить, что важнее — паритет с AlphaDLC или
ванильная сетка GCD — отдельная задача; выводы записаны в `docs/PvPMath.md`
(строка 3 таблицы и §3.3).

## Проверки

`python Swerve/tests/check_elytra_flight_parity.py` (Python 3, Java/Javac 21).
Тест извлекает математические блоки непосредственно из исходников обоих клиентов и выполняет их в отдельном Java-harness: 1664 комбинации движения/камеры/ауры, 101 значение GCD, 325 коррекций ввода. Это не тест Minecraft-физики, сети или применения миксинов.

При переносе отдельно прошла компиляция всех добавленных/изменённых Java-файлов компилятором ECJ с target 21 против локального Minecraft 1.21.4 JAR и классов Swerve. Проверены дескрипторы точек внедрения по байткоду Minecraft. Полная Gradle/Loom-сборка и запуск игры в среде переноса не выполнены: загрузка дистрибутива Gradle недоступна.

Перед выпуском: `./gradlew build` / `.\gradlew.bat build`, затем проверить в игре взлёт, все направления и диагонали, одновременные клавиши, зависание, ракеты из обеих рук/хотбара/инвентаря и отсутствие ракет, кулдаун, слот-8 fallback, обе третьеличные камеры, KillAura с целью/без цели, отключение модуля, смерть/перезаход и совместную работу со SpatialGui/Speed.
