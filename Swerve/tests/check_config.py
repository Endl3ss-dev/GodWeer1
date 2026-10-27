#!/usr/bin/env python3
"""Контракт конфига Swerve: сохранение и загрузка читают одни и те же ключи.

Проверка статическая и не требует JDK. Она отвечает на вопрос «не разъехались ли
две половины одного формата», который javac не видит вообще:

  1. `encodeGui`/`applyGui` и `encodeMenu`/`applyMenu` пишут и читают один набор ключей.
  2. Все поля `MenuPrefs` (public static boolean/float) попадают в файл.
  3. Все настройки из `GuiConfig` попадают в файл.
  4. Каждый класс из `modules/api/setting` обработан и в `SettingCodec.encode`, и в `decode`
     (кнопка `ActionSetting` — тоже: её ветка есть в обоих методах и возвращает «не сохранять»).
  5. Восстановление модулей идёт через `setEnabledQuiet`, а `markDirty` вызывается
     только для пользовательских тоглов (`if (notify)`), не для загрузки.
  6. Меню помечает конфиг изменённым во всех точках правки и сбрасывает его на закрытии.
  7. `ConfigManager.init()` вызывается после `ModuleManager.init()`.
  8. Ключи модулей (простое имя класса) уникальны, файл каждого модуля существует.
  9. Внутри одного модуля нет двух настроек с одинаковым именем (имя — ключ в файле),
     включая унаследованные настройки HUD-элементов.
 10. `ConfigManager` не тянет `net.minecraft.*`: конфиг грузится до готовности игры.

Чего проверка не видит: поведение во время выполнения (гонки, IO-ошибки, права на
каталог). Для этого есть ручной прогон харнесса — см. `Swerve/docs/Config.md`.

Использование:
    python3 Swerve/tests/check_config.py
    python3 Swerve/tests/check_config.py --root Swerve

Код возврата: 0 — чисто, 1 — есть находки, 2 — структура репозитория не найдена.
"""
from __future__ import annotations

import argparse
import re
import sys
from pathlib import Path

SETTING_CLASSES = (
    "ActionSetting",
    "BooleanSetting",
    "FloatSetting",
    "ModeSetting",
    "TextSetting",
    "BindSetting",
    "ListSetting",
)

findings: list[str] = []


def fail(message: str) -> None:
    findings.append(message)


def read(path: Path) -> str:
    return path.read_text(encoding="utf-8")


def method_body(source: str, signature: str) -> str | None:
    """Тело метода по началу сигнатуры: от первой '{' до парной '}'."""
    start = source.find(signature)
    if start < 0:
        return None
    open_brace = source.find("{", start)
    if open_brace < 0:
        return None
    depth = 0
    for index in range(open_brace, len(source)):
        char = source[index]
        if char == "{":
            depth += 1
        elif char == "}":
            depth -= 1
            if depth == 0:
                return source[open_brace + 1:index]
    return None


def require_body(source: str, signature: str, where: str) -> str:
    body = method_body(source, signature)
    if body is None:
        fail(f"{where}: не найден метод '{signature}'")
        return ""
    return body


def keys(source: str, pattern: str) -> set[str]:
    return {match.group(1) for match in re.finditer(pattern, source)}


def check_key_symmetry(config: str) -> None:
    gui_write = require_body(config, "private static JsonObject encodeGui()", "ConfigManager")
    gui_read = require_body(config, "private static void applyGui(", "ConfigManager")
    menu_write = require_body(config, "private static JsonObject encodeMenu()", "ConfigManager")
    menu_read = require_body(config, "private static void applyMenu(", "ConfigManager")

    written = keys(gui_write, r'addProperty\("([^"]+)"')
    read_keys = keys(gui_read, r'applyFloat\([^;]*?"([^"]+)"\)')
    if written != read_keys:
        fail(f"gui: пишется {sorted(written)}, читается {sorted(read_keys)}")

    written = keys(menu_write, r'addProperty\("([^"]+)"')
    read_keys = keys(menu_read, r'(?:readBoolean|readFloat|readInt)\(menu,\s*"([^"]+)"')
    if written != read_keys:
        fail(f"menu: пишется {sorted(written)}, читается {sorted(read_keys)}")


def check_menu_prefs_covered(root: Path, config: str) -> None:
    prefs = read(root / "src/main/java/swerve/ui/menu/MenuPrefs.java")
    fields = keys(prefs, r"public static\s+(?:boolean|float)\s+(\w+)\s*=")
    body = require_body(config, "private static JsonObject encodeMenu()", "ConfigManager")
    missing = sorted(field for field in fields if f'"{field}"' not in body)
    if missing:
        fail(f"MenuPrefs: поля не сохраняются — {missing}")


def check_gui_covered(root: Path, config: str) -> None:
    gui = read(root / "src/main/java/swerve/ui/newgui/GuiConfig.java")
    getters = keys(gui, r"public static FloatSetting (\w+)\(\)")
    encode = require_body(config, "private static JsonObject encodeGui()", "ConfigManager")
    apply = require_body(config, "private static void applyGui(", "ConfigManager")
    for getter in sorted(getters):
        call = f"GuiConfig.{getter}()"
        if call not in encode or call not in apply:
            fail(f"GuiConfig.{getter}() отсутствует в сохранении или загрузке")


def check_setting_codec(root: Path) -> None:
    codec = read(root / "src/main/java/swerve/config/SettingCodec.java")
    encode = require_body(codec, "public static JsonElement encode(", "SettingCodec")
    decode = require_body(codec, "public static void decode(", "SettingCodec")
    directory = root / "src/main/java/swerve/modules/api/setting"
    present = {path.stem for path in directory.glob("*.java")} - {"Setting"}
    if present != set(SETTING_CLASSES):
        fail(f"SettingCodec: изменился набор настроек — {sorted(present)}; обнови SETTING_CLASSES в тесте")
    for name in sorted(present):
        if f"instanceof {name} " not in encode:
            fail(f"SettingCodec.encode: нет ветки для {name}")
        if f"instanceof {name} " not in decode:
            fail(f"SettingCodec.decode: нет ветки для {name}")


def check_own_config_modules(root: Path, config: str) -> None:
    """Модуль со своим файлом настроек не должен дублироваться в общем конфиге."""
    encode = require_body(config, "private static JsonObject encodeModules()", "ConfigManager")
    apply_modules = require_body(config, "private static void applyModules(", "ConfigManager")
    if "usesOwnConfig()" not in encode or "usesOwnConfig()" not in apply_modules:
        fail("ConfigManager: сохранение и загрузка обязаны учитывать Module.usesOwnConfig()")

    java = root / "src/main/java/swerve"
    for path in java.rglob("*.java"):
        source = read(path)
        match = re.search(r"public boolean usesOwnConfig\(\)\s*\{\s*return true;", source)
        if not match:
            continue
        if "FabricLoader.getInstance().getConfigDir()" not in source:
            fail(f"{path.name}: usesOwnConfig() = true, но своего файла настроек нет")
        module = read(java / "modules/Module.java")
        if "public boolean usesOwnConfig()" not in module:
            fail("Module: нет метода usesOwnConfig()")


def check_quiet_restore(root: Path) -> None:
    config = read(root / "src/main/java/swerve/config/ConfigManager.java")
    apply_modules = require_body(config, "private static void applyModules(", "ConfigManager")
    if "setEnabledQuiet(" not in apply_modules:
        fail("ConfigManager: загрузка обязана включать модули через setEnabledQuiet (без уведомлений)")
    if "setEnabled(" in apply_modules.replace("setEnabledQuiet(", ""):
        fail("ConfigManager: загрузка не должна звать обычный setEnabled")

    module = read(root / "src/main/java/swerve/modules/Module.java")
    enabled = require_body(module, "public void setEnabled(boolean state)", "Module")
    quiet = require_body(module, "public void setEnabledQuiet(boolean state)", "Module")
    if "applyEnabled(state, true)" not in enabled:
        fail("Module.setEnabled должен вызывать applyEnabled(state, true)")
    if "applyEnabled(state, false)" not in quiet:
        fail("Module.setEnabledQuiet должен вызывать applyEnabled(state, false)")

    apply_enabled = require_body(module, "private void applyEnabled(", "Module")
    notify_at = apply_enabled.find("if (notify)")
    dirty_at = apply_enabled.find("ConfigManager.markDirty()")
    if dirty_at < 0:
        fail("Module.applyEnabled: нет ConfigManager.markDirty()")
    elif notify_at < 0 or dirty_at < notify_at:
        fail("Module.applyEnabled: markDirty должен стоять под if (notify) — загрузка не пишет файл")


def check_menu_hooks(root: Path) -> None:
    screen = read(root / "src/main/java/swerve/ui/menu/MenuScreen.java")
    hooks = {
        "private void runAction(": "ConfigManager.markDirty()",
        "private void applySlider(": "ConfigManager.markDirty()",
        "private void applyPrefSlider(": "ConfigManager.markDirty()",
        "public void onClose()": "ConfigManager.saveIfDirty()",
    }
    for signature, marker in hooks.items():
        body = require_body(screen, signature, "MenuScreen")
        if body and marker not in body:
            fail(f"MenuScreen: в '{signature.strip()}' нет {marker}")


def check_init_order(root: Path) -> None:
    swerve = read(root / "src/main/java/swerve/Swerve.java")
    modules_at = swerve.find("ModuleManager.init();")
    config_at = swerve.find("ConfigManager.init();")
    if modules_at < 0 or config_at < 0:
        fail("Swerve: нет вызова ModuleManager.init() или ConfigManager.init()")
    elif config_at < modules_at:
        fail("Swerve: ConfigManager.init() должен идти после ModuleManager.init()")


def registered_modules(manager: str) -> list[str]:
    return [match.group(1) for match in re.finditer(r"register\(new (\w+)\(\)\)", manager)]


def check_module_keys(root: Path) -> None:
    manager = read(root / "src/main/java/swerve/managers/ModuleManager.java")
    names = registered_modules(manager)
    if len(names) < 15:
        fail(f"ModuleManager: найдено всего {len(names)} регистраций — проверь парсер теста")
    duplicates = sorted({name for name in names if names.count(name) > 1})
    if duplicates:
        fail(f"ModuleManager: повторная регистрация — {duplicates}")

    sources = {path.stem for path in (root / "src/main/java").rglob("*.java")}
    for name in names:
        if name not in sources:
            fail(f"ModuleManager: для модуля {name} нет исходника")
    simple = {Path(name).name for name in names}
    if len(simple) != len(names):
        fail("ModuleManager: простые имена классов модулей не уникальны — ключи конфига совпадут")


def check_setting_names(root: Path) -> None:
    java = root / "src/main/java/swerve"
    manager = read(java / "managers/ModuleManager.java")
    hud_base = read(java / "ui/hud/HudElement.java")
    base_pattern = r'new\s+(?:BooleanSetting|FloatSetting|ModeSetting|TextSetting|BindSetting|ListSetting)\s*\(\s*"([^"]+)"'
    base_names = keys(hud_base, base_pattern)

    files = {path.stem: path for path in java.rglob("*.java")}
    for module in registered_modules(manager):
        source = read(files[module])
        names = list(re.finditer(base_pattern, source))
        own = [match.group(1) for match in names]
        if "extends HudElement" in source:
            own = own + sorted(base_names)
        duplicates = sorted({name for name in own if own.count(name) > 1})
        if duplicates:
            fail(f"{module}: настройки с одинаковым именем {duplicates} — в файле останется одно значение")


def check_no_game_imports(root: Path) -> None:
    for path in (root / "src/main/java/swerve/config").glob("*.java"):
        for line in read(path).splitlines():
            if line.startswith("import net.minecraft."):
                fail(f"{path.name}: конфиг не должен зависеть от {line.strip()}")


def main() -> int:
    parser = argparse.ArgumentParser(description="Проверка контракта конфига Swerve")
    parser.add_argument("--root", default=None, help="корень репозитория (по умолчанию — рядом с тестом)")
    args = parser.parse_args()

    root = Path(args.root).resolve() if args.root else Path(__file__).resolve().parents[1]
    if not (root / "src/main/java/swerve/config/ConfigManager.java").is_file():
        print(f"не найден {root}/src/main/java/swerve/config/ConfigManager.java")
        return 2

    config = read(root / "src/main/java/swerve/config/ConfigManager.java")

    check_key_symmetry(config)
    check_menu_prefs_covered(root, config)
    check_gui_covered(root, config)
    check_setting_codec(root)
    check_own_config_modules(root, config)
    check_quiet_restore(root)
    check_menu_hooks(root)
    check_init_order(root)
    check_module_keys(root)
    check_setting_names(root)
    check_no_game_imports(root)

    for finding in findings:
        print("FAIL:", finding)
    print(f"найдено: {len(findings)}")
    return 1 if findings else 0


if __name__ == "__main__":
    sys.exit(main())
