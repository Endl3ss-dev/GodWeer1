# DecideVisuals — Gradle-проект (Fabric 1.21.11)

Декомпилированный и разложенный исходник равноимённого клиентского мода Minecraft.
Это **ready src** Gradle-проект на Fabric Loom для Minecraft **1.21.11** и Java 21.

> **Важно.** Мод содержит толстый `clickgui`/рендер и почти весь код опирается на
> `net.minecraft.*`. Полная компиляция и запуск возможны **только** в окружении
> Fabric-разработки (Loom скачает Minecraft + Yarn mappings и переотобразит классы).
> `./gradlew build` сам это сделает при наличии сети и JDK 21.

## Структура

```
src/main/java/ru/decide   — все Java-классы мода (decompiled CFR)
src/main/resources/
  ├── fabric.mod.json       — метаданные Fabric-мода (id: decide, entrypoint Client)
  ├── mixin.json            — список миксинов
  ├── client.accesswidener  — access widener
  ├── assets/decide/…       — текстуры, шейдеры, звуки, нативные DLL
  ├── META-INF/jars/…       — вложенные моды/библиотеки
  └── win32-x86-64/…        — discord-rpc.dll
build.gradle / settings.gradle / gradle.properties — конфигурация сборки
gradlew / gradlew.bat / gradle/wrapper              — Gradle wrapper (8.10)
```

## Быстрый старт

Требуется JDK 21 (`java -version`). Gradle ставится автоматически через wrapper.

```bash
# Проверить раскладку исходников и ресурсов (без сети, без скачивания MC)
./gradlew verifyLayout

# Полная Fabric-сборка (скачает Minecraft, mapping, справедливо для maven-central)
./gradlew build          # результат в build/libs/decidevisuals-<ver>.jar
```

Чтобы **запустить** мод, положите собранный jar в папку `mods/` Fabric-инсталляции
Minecraft 1.21.11 с Fabric Loader 0.18.4+ и Fabric API.

## Аудит безопасности (кратко)

Проведён анализ Java-кода и нативных DLL. Вредоносных бэкдоров, webhook-бота,
эксфильтрации данных о ПК **не обнаружено**. Сетевая активность ограничена
легитимными функциями (см. README `SECURITY.md` не создан; выводы в отчёте).

- **Сеть**: `lexoravisuals.fun` (синхронизация косметики: сервер + никнейм,
  без чувствительных данных), `lrclib.net` (тексты песен), `discord` (Rich Presence),
  `minotar.net` (скины), `dsc.gg`/`t.me`/`DecideVisuals.fun` (кнопки в меню).
- **Нативные DLL**: `discord-rpc.dll` (официальный), `OptMedia.dll` (WinRT
  медиа-мониторинг, Rust), `DecideVocal.dll` (анализ вокала). Сетевых API в импортах нет.
- **Данные о ПК наружу не отправляются.** Сырой ввод мыши (`WindowsRawInputBuffer`)
  используется для плавности прицела на локальной машине, в сеть не уходит.

## Заметки по декомпиляции

Исходники получены декомпилятором **CFR 0.152**; единственный файл, где CFR не смог
восстановить поток управления (`EntityEsp.java` — два метода со `** GOTO`), передекомпилирован
декомпилятором **Vineflower** и заменён полностью корректной версией. По всей кодовой базе
(746 файлов) артефактов декомпиляции (`goto lbl` и т.п.) больше нет.
Имена Minecraft-классов остались обфусцированными с протогени (например `class_310` =
`MinecraftClient`), поскольку в jar используются официальные имена, а Loom отобразит их
обратно через Yarn. Файл `ShaderData.java` содержит шейдеры, закодированные строкой — они
декодируются и используются в рантайме (не вредоносны).