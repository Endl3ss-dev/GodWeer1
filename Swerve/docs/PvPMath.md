# Математика и тайминги PvP: GCD, поворот, дистанция, TPS

Документ держит то, что нельзя вывести из исходников Swerve: **что именно делает
ванилла 1.21.4** и какие числа из этого следуют. Каждый факт получен разбором
байткода `minecraft-merged` (инструмент — `Swerve/tests/mc_disasm.py`), а не памятью
о чужих клиентах. Если факт перестаёт совпадать с игрой — править надо отсюда.

## 1. Как проверять

```bash
# поворот и его множитель
python3 Swerve/tests/mc_disasm.py --class net.minecraft.world.entity.Entity --method turn --exact
# кадрирование пакетов времени (частота для трекера TPS)
python3 Swerve/tests/mc_disasm.py --class net.minecraft.server.MinecraftServer --grep synchronizeTime
# дистанция удара на сервере
python3 Swerve/tests/mc_disasm.py --class net.minecraft.server.network.ServerGamePacketListenerImpl --method handleInteract --exact
python3 Swerve/tests/mc_disasm.py --class net.minecraft.world.entity.player.Player --method canInteractWithEntity

# таблица Opcode'ов сама сверена с байткодом: 0 неизвестных операторов
python3 Swerve/tests/mc_disasm.py --selftest 100000
#   классов: 5744, методов с кодом: 59968, с неизвестным Opcode: 0, с рассинхроном в конце: 0
```

## 2. Проверенные факты 1.21.4

| # | Факт | Откуда |
| --- | --- | --- |
| 1 | `Entity.turn(double x, double y)`: `yaw += (float)x * 0.15F`, `pitch += (float)y * 0.15F`, питч зажимается в `[-90; 90]`; yaw **не** нормализуется; множитель применяется к дельте, приведённой к `float` | байткод `Entity.turn` (`ldc 0.15000000596046448`, `fmul`) |
| 2 | `MouseHandler.turnPlayer`: `d = sensitivity*0.6 + 0.2`, `e = d*d*d`, `f = e*8.0`, далее `player.turn(dx*f, dy*f)` | байткод `turnPlayer` |
| 3 | Следовательно шаг одного «щелчка» мыши = `((sens*0.6+0.2)^3 * 8) * 0.15` — это и есть `GcdUtil.getGCDValue()`; свёрнутая «обратная» форма с `cbrt(sens/0.15/8)` тождественно равна значению ползунка и квантует **не** по GCD | п.1 × п.2, см. также `GCDFixer` в AlphaDLC |
| 4 | `Player.attack`: урон `= ATTACK_DAMAGE * (0.2 + scale*0.8)`, `scale = getAttackStrengthScale(0.5F)`; крит (×1.5) только при `scale > 0.9` **и** `fallDistance > 0`, `!onGround`, `!onClimbable`, `!isInWater`, без слепоты, не пассажир, цель — `LivingEntity`, не в спринте | байткод `Player.attack` |
| 5 | `getAttackStrengthScale(t) = clamp((attackStrengthTicker + t) / getCurrentItemAttackStrengthDelay(), 0, 1)`, `delay = 20 / ATTACK_SPEED` — скорость атаки оружия, а не фиксированные миллисекунды | байткод `Player` |
| 6 | `attackStrengthTicker` сбрасывается **внутри** `Player.attack` — сразу после расчёта шкалы | байткод `Player.attack`, `resetAttackStrengthTicker` |
| 7 | Серверная дистанция удара: `handleInteract` → `player.canInteractWithEntity(target.getBoundingBox(), 3.0)`, а это `box.distanceToSqr(eye) < (entityInteractionRange() + 3.0)^2`, т.е. расстояние **до хитбокса**, а не до позиции | байткод `ServerGamePacketListenerImpl.handleInteract`, `Player.canInteractWithEntity` |
| 8 | Пакет времени (`ClientboundSetTimePacket`) уходит **раз в секунду**: `MinecraftServer.tickChildren` вызывает `synchronizeTime` под условием `tickCount % 20 == 0` | байткод `MinecraftServer.tickChildren` |

Практические следствия для Swerve: п.7 — почему `AuraUtils.strictDistance` и
`KillAura.attackDistance` считают от глаз до ближайшей точки AABB с потолком
`entityInteractionRange() + 3.0`; п.4–6 — почему гейт 1.9 построен на
`getAttackStrengthScale`, а не на миллисекундах; п.1–2 — почему `FreeLook.turn`
обязан сам умножать дельту на `0.15`.

## 3. Исправлено в этой итерации

### 3.1 `ServerTPS`: лаг не измерялся никогда (п.8)

Задержка между пакетами времени — ~1 с на здоровом сервере. Порог «это пауза, а не
лаг» был равен ровно одной секунде, то есть отбрасывался **любой** отсчёт медленнее
20 TPS: сервер на 10 TPS (пакет раз в 2 с) → выборок нет → `tps` навсегда 20 →
«Синхронизация по TPS» в KillAura не двигала интервал ни разу. Теперь период пакета
и порог — именованные константы, порог = 20 периодов (1 TPS — нижняя граница
измеримого; медленнее — мир стоит, темп надо держать обычный). Формула не менялась:
`tps = MAX_TPS * (PACKET_PERIOD_NS / delay)` верна именно потому, что один пакет
равен двадцати тикам.

### 3.2 `MoveUtil`: сравнение углов без оборачивания

`fixMovement`/`fixMovementToTarget` выбирали «ближайшее» из восьми направлений по
`|angle - testAngle|` без `Mth.wrapDegrees` (как в оригинале Private). На переходе
через ±180° это инвертирует выбор: при угле на цель 179.9° кандидат −179.9°
(разница 0.2°) считался далёким на 359.8° и проигрывал диагонали в 44.9°.
Перебор по сетке yaw/ввода: **расхождение с корректным выбором в 3.15 % состояний,
максимальная ошибка курса 44.5°** — игрока сносило в сторону ровно тогда, когда
цель находится «за спиной». Копия в `ElytraFlightRotation.fixMovement` оборачивала
сразу; теперь все три метода сравнивают одинаково, и это закреплено проверкой
`check_pvp_utils.py` (строка `difference` обязана содержать `wrapDegrees`).

### 3.3 `ElytraFlightRotation.fixRotate`: залипание при чувствительности 0 %

Шаг `getGCDValue()` обнуляется вместе с ползунком чувствительности (0 % —
разрешённое значение). `round(delta/0)*0` даёт `0.0` по обеим осям: `rotate()`
никогда не достигает цели, `resetting` остаётся `true`, свободная камера и поворот
элиты встают. Добавлен выход «без квантования» при `gcd <= 0` (и при `NaN`).
Формула `getGCDValue()` **не** тронута — она сверяется с AlphaDLC в
`check_elytra_flight_parity.py`, и расхождение с ванилью перенесено туда сознательно
(см. п.3 таблицы выше): менять её в рамках этой правки означало бы сломать
задокументированное обещание «порт 1-в-1». Отдельная задача — решить, что важнее:
паритет с AlphaDLC или ванильная сетка GCD.

### 3.4 `KillAura.rayTraceOk`: «Улучшить хитбоксы» отменяли «Не бить через блоки»

Ветка `smartHitbox` возвращала результат сразу, после проверки дистанции, и
`noHitThroughBlocks` в ней не участвовал. Включение одной настройки молча
выключало другую: аура била сквозь стену. Проверка стены добавлена в эту ветку;
цена — обычно один `clip` сверх уже сделанных, потому что
`AuraUtils.hasVisiblePoint` останавливается на первой открытой точке.

## 4. Что осталось непроверенным

- Компиляция: `javac`/`./gradlew compileJava` в среде недоступны (JDK и Maven
  Central недоступны, загрузка дистрибутива Temurin упирается в `curl` 35 на
  редирект release-ассетов). Вместо неё — `check_mc_api.py` (символы, дескрипторы и
  привязка миксинов против реального байткода), `check_syntax.py` (разбор всех
  файлов), `check_types.py`/`check_scope.py` (типы и области видимости).
- Поведение в игре: квантование поворотов, темп удара на лагающем сервере и доводка
  камеры надо проверять запуском клиента.
- `AuraUtils.visibleTargetPoint` аллоцирует `Vec3` на каждую из 9 точек, хотя
  комментарий в классе обещает «без аллокаций в тике». Оставлено как есть: правка
  требует либо изменяемого `ClipContext`, либо смены контракта метода — вне рамок.
- Хранение `HudManager.OVERLAYS`: `registerOverlay` защищён от дублей через
  `contains`, а лямбда-ссылка `KillAura::renderFovOverlay` — новый объект на каждый
  `new KillAura()`. Пока `ModuleManager.init()` вызывается один раз, дублей нет; при
  любой «перезагрузке модулей» слой удвоится.
