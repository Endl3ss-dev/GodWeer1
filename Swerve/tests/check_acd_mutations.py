#!/usr/bin/env python3
"""Мутационный прогон для `check_acd.py`: проверка обязана падать на поломанном коде.

Статический тест с выводом «найдено: 0» ничего не стоит, пока не показано, что он
находит дефект. Здесь во ВРЕМЕННЫЙ каталог копируются файлы, которые читает
`check_acd.py`, вносится ровно одна регрессия, и требуется хотя бы одна находка с
нужным текстом. Репозиторий не трогается.

Первым делом проверяется контроль: на исходном дереве находков быть не должно — иначе
«все мутации пойманы» означало бы просто «тест ругается на всё подряд».

Часть мутаций — это дефекты, которые реально были найдены и исправлены при порте
(незакрытый миксин в `swerve.mixins.json`, вызов несуществующего метода, декоративная
настройка, маркер правила, который всегда подхватывает более общее правило, молчаливая
потеря хвоста при выводе в чат).

Использование:
    python3 Swerve/tests/check_acd_mutations.py

Код возврата: 0 — все мутации найдены, 1 — есть пропущенные, 2 — тест не выполнен.
"""
from __future__ import annotations

import re
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

TESTS_DIR = Path(__file__).resolve().parent
SWERVE = TESTS_DIR.parent
CHECK = TESTS_DIR / "check_acd.py"

ACD = "src/main/java/swerve/acd"
FILES = [
    f"{ACD}/AcdCategory.java",
    f"{ACD}/AcdSeverity.java",
    f"{ACD}/AcdConfidence.java",
    f"{ACD}/AcdEvent.java",
    f"{ACD}/AcdRule.java",
    f"{ACD}/AcdRules.java",
    f"{ACD}/AcdClassifier.java",
    f"{ACD}/AcdReport.java",
    f"{ACD}/AcdStore.java",
    f"{ACD}/AcdCollector.java",
    f"{ACD}/AcdFormatter.java",
    f"{ACD}/AcdExporter.java",
    "src/main/java/swerve/modules/impl/misc/AntiCheatDetector.java",
    "src/main/java/swerve/managers/ModuleManager.java",
    "src/main/java/swerve/ui/menu/MenuScreen.java",
    "src/main/java/swerve/mixins/client/MixinClientPacketListener.java",
    "src/main/java/swerve/mixins/client/MixinClientCommonPacketListenerImpl.java",
    "src/main/resources/swerve.mixins.json",
    "tests/AcdHarness.java",
    "docs/AntiCheatDetector.md",
]

MODULE = "src/main/java/swerve/modules/impl/misc/AntiCheatDetector.java"
MIXIN = "src/main/java/swerve/mixins/client/MixinClientPacketListener.java"
COMMON_MIXIN = "src/main/java/swerve/mixins/client/MixinClientCommonPacketListenerImpl.java"
STORE = f"{ACD}/AcdStore.java"
RULES = f"{ACD}/AcdRules.java"
FORMATTER = f"{ACD}/AcdFormatter.java"
EXPORTER = f"{ACD}/AcdExporter.java"
COLLECTOR = f"{ACD}/AcdCollector.java"
HARNESS = "tests/AcdHarness.java"
DOC = "docs/AntiCheatDetector.md"

# (что ломает, файл, что найти, на что заменить; "" — удалить, ожидаемое слово в находке)
MUTATIONS: list[tuple[str, str, str, str, str]] = [
    ("модуль снят с регистрации", "src/main/java/swerve/managers/ModuleManager.java",
     "register(new AntiCheatDetector());", "", "не зарегистрирован"),
    ("нет импорта модуля в реестре", "src/main/java/swerve/managers/ModuleManager.java",
     "import swerve.modules.impl.misc.AntiCheatDetector;", "", "нет импорта"),
    ("миксин common-пакетов не вписан в конфиг", "src/main/resources/swerve.mixins.json",
     '"client.MixinClientCommonPacketListenerImpl",', "", "не перечислен"),
    ("источник движения отключён в миксине", MIXIN,
     "AcdCollector.onMotion(packet);", "", "не подключены"),
    ("хендлер остался без try/catch", MIXIN,
     "try {\n            AcdCollector.onLogin(packet);", "{\n            AcdCollector.onLogin(packet);",
     "try/catch"),
    ("хендлер не выключает сбор при падении", COMMON_MIXIN,
     'AcdCollector.disableCollection("onPacketError", failure);', "", "не пишет ошибку в лог"),
    ("вызов с неверной арностью", MIXIN,
     "AcdCollector.onLogin(packet);", "AcdCollector.onLogin();", "аргументами"),
    ("вмешательство в обработку пакета", MIXIN,
     "AcdCollector.onSystemChat(packet);", "ci.cancel();\n            AcdCollector.onSystemChat(packet);",
     "отмена"),
    ("@Redirect вместо @Inject", COMMON_MIXIN,
     '@Inject(method = "handleDisconnect(Lnet/minecraft/network/protocol/common/ClientboundDisconnectPacket;)V"',
     '@Redirect(method = "handleDisconnect(Lnet/minecraft/network/protocol/common/ClientboundDisconnectPacket;)V"',
     "перехват метода"),
    ("acd зависит от боевого модуля", STORE,
     "package swerve.acd;", "package swerve.acd;\n\nimport swerve.modules.impl.combat.KillAura;",
     "боевых модулей"),
    ("обращение к выдуманному методу хранилища", MODULE,
     "AcdStore.setCapacity(want);", "AcdStore.setCapasity(want);", "выдуманный API"),
    ("обращение к выдуманной константе важности", f"{ACD}/AcdCategory.java",
     'MOVEMENT_DIAGNOSTIC("Движение", AcdSeverity.NOTICE)', 'MOVEMENT_DIAGNOSTIC("Движение", AcdSeverity.NOTICE_X)',
     "нет члена"),
    ("маркер правила в верхнем регистре", RULES,
     '"grim ac", "grim",', '"grim ac", "Grim",', "нижнем регистре"),
    ("слишком короткий маркер", RULES,
     '"flagged", "violation",', '"ac", "flagged", "violation",', "короче трёх"),
    ("дублировать id правила", RULES,
     'AcdRule.excluding("flag:violation"', 'AcdRule.excluding("ac:name"', "одинаковые id"),
    ("вернуть маркер, который всегда перекрывает более общий", RULES,
     '"flagged", "violation", "violated", "нарушение", "подозрительн",',
     '"flagged", "violation", "violated", "нарушение", "флаг античита", "подозрительн",', "бессмыслен"),
    ("править таблицу так, чтобы корпус разошёлся", RULES,
     '"flagged", "violation", "violated", "нарушение", "подозрительн",',
     '"flagged", "violation", "violated", "moved too quickly", "подозрительн",', "корпус"),
    ("ёмкость буфера ниже минимума", STORE,
     "public static final int MAX_CAPACITY = 20_000;", "public static final int MAX_CAPACITY = 20;",
     "рассогласованы"),
    ("границы настройки заданы числами, а не константами", MODULE,
     "AcdStore.MIN_CAPACITY, AcdStore.MAX_CAPACITY, 50F", "50F, 90F, 50F", "разъедутся"),
    ("лимит строк чата выше потолка", EXPORTER,
     "public static final int DEFAULT_CHAT_LINES = 12;", "public static final int DEFAULT_CHAT_LINES = 90;",
     "рассогласованы"),
    ("фильтр важности перестал влиять на запись", MODULE,
     "AcdStore.setSeverityFloor(AcdSeverity.values()[this.severityFloor.getIndex()]);", "",
     "не читается"),
    ("список категорий убрали из настроек", MODULE,
     "this.verbosity, this.scanChat, this.announce, this.chatLines, this.exportWindow,",
     "this.verbosity, this.scanChat, this.announce, this.chatLines,", "не добавлена"),
    ("политика склейки ставится, но не читается", STORE,
     "long window = mergeWindowMs;", "long window = 0;", "нигде не читают"),
    ("флаг «показывать важное в чате» снова декоративный", COLLECTOR,
     "if (added && AcdStore.announces()", "if (added && false", "нигде не читают"),
    ("кнопка в GUI перестала дёргать действие", "src/main/java/swerve/ui/menu/MenuScreen.java",
     "pressed.trigger();\n                    MenuSound.play(MenuSound.CLICK);",
     "MenuSound.play(MenuSound.CLICK);", "не дёргает"),
    ("имя pattern-переменной перекрывает параметр метода (реальная ошибка javac)",
     "src/main/java/swerve/ui/menu/MenuScreen.java",
     "if (s instanceof ActionSetting pressed) {\n                    pressed.trigger();",
     "if (s instanceof ActionSetting action) {\n                    action.trigger();",
     "совпадает с именем параметра"),
    ("ветка отрисовки кнопки сломана", "src/main/java/swerve/ui/menu/MenuScreen.java",
     "} else if (s instanceof ActionSetting action) {", "} else if (s instanceof ActionSetting unused) {",
     "не использована"),
    ("окно настроек закрыли для выключенного модуля", "src/main/java/swerve/ui/menu/MenuScreen.java",
     "private void openPopup(Module m) {", "private void openPopup(Module m) {\n        if (!m.isEnable()) {\n            return;\n        }",
     "история станет недоступна"),
    ("харнесс зовёт несуществующий метод", HARNESS,
     "AcdStore.statusLine().contains(\"записей 0 из 100\")",
     "AcdStore.statusLineX().contains(\"записей 0 из 100\")", "нет члена"),
    ("иероглиф в русском тексте", STORE,
     "public static final int MIN_CAPACITY = 50;", "// \u5c40\u90e8\n    public static final int MIN_CAPACITY = 50;",
     "чужая письменность"),
    ("в отчёте пропало объяснение эвристик", FORMATTER,
     'builder.append("\\nВнимание: ")', 'builder.append("")', "Внимание"),
    ("из документа убран раздел о границе", DOC,
     "## 5. Граница применимости: это диагностика, а не обход", "## 5. Раздел про выгрузку",
     "нет раздела о границе"),
    ("из документа убран раздел «НЕ ПРОВЕРЕНО»", DOC,
     "## НЕ ПРОВЕРЕНО", "## Примечания", "НЕ ПРОВЕРЕНО"),
]


def prepare(root: Path) -> int:
    for relative in FILES:
        source = SWERVE / relative
        if not source.is_file():
            print(f"не найден файл, нужный для мутаций: {relative}")
            return 2
        target = root / relative
        target.parent.mkdir(parents=True, exist_ok=True)
        shutil.copyfile(source, target)
    return 0


def run_checker(root: Path) -> tuple[int, str]:
    process = subprocess.run([sys.executable, str(CHECK), "--root", str(root)],
                             capture_output=True, text=True, encoding="utf-8", errors="replace",
                             timeout=300)
    return process.returncode, process.stdout + process.stderr


def main() -> int:
    base = Path(tempfile.mkdtemp(prefix="acd-mutations-"))
    try:
        if prepare(base / "clean") == 2:
            return 2
        # контроль: на исходных файлах находков быть не должно
        code, output = run_checker(base / "clean")
        if code != 0:
            print("КОНТРОЛЬНЫЙ ПРОГОН НЕ ЧИСТ — мутации бессмысленны:")
            print(output[-3000:])
            return 2
        print("контроль: исходное дерево чисто")

        caught = 0
        missed: list[str] = []
        for index, (description, relative, old, new, needle) in enumerate(MUTATIONS, 1):
            root = base / f"m{index}"
            if prepare(root) == 2:
                return 2
            path = root / relative
            text = path.read_text(encoding="utf-8")
            if text.count(old) != 1:
                missed.append(f"{description}: якорь не найден ({text.count(old)}x) — мутация не внесена")
                continue
            text = text.replace(old, new, 1) if new else text.replace(old, "", 1)
            path.write_text(text, encoding="utf-8")
            code, output = run_checker(root)
            if code == 1 and needle in output:
                caught += 1
            else:
                missed.append(f"{description}: {output.strip().splitlines()[-1] if output.strip() else 'нет вывода'}")
        shutil.rmtree(base, ignore_errors=True)
        print(f"мутаций: {len(MUTATIONS)}, поймано: {caught}, пропущено: {len(missed)}")
        for item in missed:
            print("ПРОПУЩЕНО:", item)
        return 1 if missed else 0
    except subprocess.TimeoutExpired:
        print("проверка не завершилась за отведённое время")
        return 2
    finally:
        shutil.rmtree(base, ignore_errors=True)


if __name__ == "__main__":
    sys.exit(main())
