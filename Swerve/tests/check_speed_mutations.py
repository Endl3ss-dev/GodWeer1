#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""Мутационный прогон для `check_speed.py`: проверка сама должна быть под проверкой.

Смысл один: `найдено: 0` у статического теста ничего не значит, пока не показано,
что он падает на поломанном коде. Здесь в НЕЗАВИСИМУЮ копию файлов (репозиторий не
трогается — всё живёт во временном каталоге) вносится ровно одна регрессия, и
требуется, чтобы `check_speed.py` её нашёл.

Отдельно проверяется контроль: на исходном дереве находков обязано быть ноль.
Без этого «все мутации пойманы» превращается в «тест ругается на всё подряд».

Часть мутаций — это ровно те дефекты, которые были в прежней реализации Speed
(подмена `deltaMovement`, `setSprinting(false)`, ползунок шире предела, двойное
трение как последствие), то есть тест сторожит и от возврата старого поведения.

Использование:
    python3 Swerve/tests/check_speed_mutations.py

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
REPO = SWERVE.parent
CHECK = TESTS_DIR / "check_speed.py"

SPEED_JAVA = "src/main/java/swerve/modules/impl/movement/Speed.java"
MIXIN_JAVA = "src/main/java/swerve/mixins/client/MixinLivingEntity.java"
ACCESSOR_JAVA = "src/main/java/swerve/mixins/accessor/LocalPlayerAccessor.java"
DOC_MD = "docs/Speed.md"

FILES = [SPEED_JAVA, MIXIN_JAVA, ACCESSOR_JAVA, DOC_MD]

# (описание поломки, файл, что найти, на что заменить; "" — удалить фрагмент)
MUTATIONS = [
    ("вернули подмену deltaMovement вместо работы с ускорением", SPEED_JAVA,
     "        return acceleration * factor;",
     "        player.setDeltaMovement(acceleration * factor, 0.0D, 0.0D);\n"
     "        return acceleration * factor;"),
    ("убрали проверку включения модуля", SPEED_JAVA,
     "        if (!isEnable()) {\n            return acceleration;\n        }\n", ""),
    ("убрали защиту от NaN/Inf в ускорении", SPEED_JAVA,
     "if (acceleration == 0.0F || !Float.isFinite(acceleration)) {",
     "if (acceleration == 0.0F) {"),
    ("убрали сравнение с локальным игроком", SPEED_JAVA,
     "if (player == null || entity != player) {", "if (player == null) {"),
    ("ползунок разрешили шире предела в коде", SPEED_JAVA,
     "MIN_MULTIPLIER, MAX_MULTIPLIER, 0.01F", "1.0F, 3.0F, 0.01F"),
    ("пропал шлюз «Только вперёд»", SPEED_JAVA,
     "        if (this.onlyForward.get()) {\n"
     "            ClientInput input = player.input;\n"
     "            if (input == null || !input.hasForwardImpulse()) {\n"
     "                return acceleration;\n"
     "            }\n"
     "        }\n", ""),
    ("авто-спринт снова ставится без ванильного шлюза", SPEED_JAVA,
     "if (((LocalPlayerAccessor) player).swerve$canStartSprinting()) {", "if (true) {"),
    ("модуль снова гасит чужой спринт", SPEED_JAVA,
     "        if (((LocalPlayerAccessor) player).swerve$canStartSprinting()) {",
     "        player.setSprinting(false);\n"
     "        if (((LocalPlayerAccessor) player).swerve$canStartSprinting()) {"),
    ("имя настройки перестало быть русским", SPEED_JAVA,
     'new BooleanSetting("Только на земле", false)', 'new BooleanSetting("Ground only", false)'),
    ("настройка объявлена, но не подключена", SPEED_JAVA,
     "addSettings(multiplier, onlyGround, onlyForward, autoSprint);",
     "addSettings(multiplier, onlyForward, autoSprint);"),
    ("в миксине проверка типа идёт после поиска модуля", MIXIN_JAVA,
     "        if (!((Object) this instanceof LocalPlayer player)) {\n"
     "            return original;\n"
     "        }\n"
     "        Speed speed = ModuleManager.get(Speed.class);",
     "        Speed speed = ModuleManager.get(Speed.class);\n"
     "        if (!((Object) this instanceof LocalPlayer player)) {\n"
     "            return original;\n"
     "        }"),
    ("из catch убрали отключение модуля", MIXIN_JAVA,
     "            speed.setEnabled(false);\n", ""),
    ("миксин переехал на другую цель", MIXIN_JAVA,
     'method = "getFrictionInfluencedSpeed"', 'method = "getFlyingSpeed"'),
    ("у аксессора пропал @Invoker", ACCESSOR_JAVA,
     '    @Invoker("canStartSprinting")\n', ""),
    ("из документа убрали раздел «НЕ ПРОВЕРЕНО»", DOC_MD,
     "## НЕ ПРОВЕРЕНО", "## Ограничения"),
    ("в документе появилось обещание обхода античита", DOC_MD,
     "## 1. Поведение", "## 1. Поведение\n\nМодуль собран так, чтобы Grim не флагуется.\n"),
    ("в русский текст документа вкралось латинское слово", DOC_MD, "@glue", ""),
    ("в русский текст документа вкрался иероглиф", DOC_MD, "@alien", ""),
]


def inject(text: str, kind: str):
    """Внести посторонний символ в первую попавшуюся кириллическую строку прозы.

    Специально не привязано к конкретной фразе: правки текста документа не должны
    превращать мутацию в «сломанный прогон».
    """
    word = re.search(r"[\u0400-\u04FF]{6,}", text)
    if word is None:
        return None
    add = "x" if kind == "@glue" else "\u4e2d"
    return text[:word.end()] + add + text[word.end():]


def prepare(root: Path) -> int:
    missing = 0
    for relative in FILES:
        source = SWERVE / relative
        if not source.is_file():
            print("нет файла:", source, file=sys.stderr)
            missing += 1
            continue
        target = root / relative
        target.parent.mkdir(parents=True, exist_ok=True)
        shutil.copyfile(source, target)
    return missing


def run_checker(root: Path):
    process = subprocess.run(
        [sys.executable, str(CHECK), "--root", str(root)],
        capture_output=True, text=True, cwd=str(REPO), timeout=600)
    lines = (process.stdout + process.stderr).strip().splitlines()
    return process.returncode, lines


def main() -> int:
    if not CHECK.is_file():
        print("не найден", CHECK, file=sys.stderr)
        return 2

    with tempfile.TemporaryDirectory(prefix="swerve-speed-") as tmp:
        root = Path(tmp) / "Swerve"
        root.mkdir(parents=True)
        if prepare(root):
            return 2

        code, lines = run_checker(root)
        if code != 0:
            print("КОНТРОЛЬНЫЙ ЗАПУСК НЕ ЧИСТ — мутации проверять бессмысленно:")
            for line in lines:
                print("   ", line)
            return 2
        print("контроль (без мутаций): найдено: 0")

        missed = []
        for description, relative, old, new in MUTATIONS:
            path = root / relative
            text = path.read_text(encoding="utf-8")
            if old.startswith("@"):
                mutated = inject(text, old)
            elif old in text:
                mutated = text.replace(old, new, 1)
            else:
                mutated = None
            if mutated is None:
                missed.append(description + " — фрагмент не найден (сломанный прогон)")
                print("СЛОМАН:", description)
                continue
            path.write_text(mutated, encoding="utf-8")
            code, out = run_checker(root)
            found = [line for line in out if line.startswith("найдено:") and line != "найдено: 0"]
            caught = code == 1 and bool(found)
            if not caught:
                missed.append(description)
            print(f"{'ПОЙМАНО' if caught else 'ПРОПУЩЕНО':>10} — {description}")
            print(f"             → {found[0] if found else '; '.join(out[-2:])}")
            path.write_text(text, encoding="utf-8")

        print(f"\nмутаций: {len(MUTATIONS)}, пропущено: {len(missed)}")
        for description in missed:
            print("  не найдено:", description)
        return 1 if missed else 0


if __name__ == "__main__":
    sys.exit(main())
