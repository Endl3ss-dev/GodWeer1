#!/usr/bin/env python3
"""Прогон исполняемого теста логики ACD (`Swerve/tests/AcdHarness.java`).

Зачем отдельный скрипт: в песочнице и в CI без JDK компилятора нет, а тест логики
хранилища/классификатора/форматтера от Minecraft не зависит — его можно собрать и
запустить обычной `javac`+`java` без Loom, без Gradle и без клиента.

Правило честности: если `javac` или gson не найдены, скрипт возвращает 2 («не выполнен»)
и никогда не возвращает 0. «Ничего не проверено» и «всё хорошо» — разные коды возврата.

Поиск инструментов:
  javac — `--javac PATH`, иначе `javac` в PATH, иначе `$JAVA_HOME/bin/javac`;
  gson  — `--gson PATH`, иначе jar из gradle-кэша (`~/.gradle/caches/modules-2/files-2.1/
          com.google.code.gson/…`), иначе любой `gson-*.jar` в репозитории.

Использование:
    python3 Swerve/tests/check_acd_run.py
    python3 Swerve/tests/check_acd_run.py --javac /usr/lib/jvm/java-21/bin/javac
    python3 Swerve/tests/check_acd_run.py --gson /path/to/gson-2.11.0.jar --keep

Код возврата: 0 — харнесс прошёл, 1 — харнесс упал или javac сообщил ошибку,
2 — выполнить нельзя (нет компилятора или gson).
"""
from __future__ import annotations

import argparse
import glob
import os
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

TESTS_DIR = Path(__file__).resolve().parent
SWERVE = TESTS_DIR.parent
HARNESS = TESTS_DIR / "AcdHarness.java"
SOURCE_ROOT = SWERVE / "src/main/java"


def find_javac(explicit: str | None) -> str | None:
    if explicit:
        return explicit if os.path.isfile(explicit) and os.access(explicit, os.X_OK) else None
    found = shutil.which("javac")
    if found:
        return found
    home = os.environ.get("JAVA_HOME")
    if home:
        candidate = Path(home) / "bin" / "javac"
        if candidate.is_file():
            return str(candidate)
    return None


def find_gson(explicit: str | None) -> str | None:
    if explicit:
        return explicit if os.path.isfile(explicit) else None
    patterns = [
        os.path.expanduser("~/.gradle/caches/modules-2/files-2.1/com.google.code.gson/gson/*/*/gson-*.jar"),
        os.path.expanduser("~/.m2/repository/com/google/code/gson/gson/*/gson-*.jar"),
        str(SWERVE.parent / "**" / "gson-*.jar"),
    ]
    for pattern in patterns:
        for hit in sorted(glob.glob(pattern, recursive=True)):
            if "-sources" not in hit and "-javadoc" not in hit:
                return hit
    return None


def run(command: list[str], cwd: Path) -> subprocess.CompletedProcess[str]:
    return subprocess.run(command, cwd=str(cwd), capture_output=True, text=True, encoding="utf-8",
                          errors="replace", timeout=600)


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--javac", default=None, help="путь к javac")
    parser.add_argument("--gson", default=None, help="путь к jar gson")
    parser.add_argument("--keep", action="store_true", help="не удалять каталог с классами")
    parser.add_argument("--out", default=None, help="каталог для классов (по умолчанию временный)")
    args = parser.parse_args()

    if not HARNESS.is_file():
        print(f"не найден {HARNESS}")
        return 2
    javac = find_javac(args.javac)
    if javac is None:
        print("javac не найден (нужен JDK 21+). Харнесс НЕ ВЫПОЛНЕН — это не «всё хорошо».")
        print("проверка статикой: python3 Swerve/tests/check_acd.py  (компилятор не заменяет)")
        print("вручную: javac -cp <gson.jar> -encoding UTF-8 -d /tmp/acdout \\")
        print(f"          -sourcepath {SOURCE_ROOT} {HARNESS}")
        print("          java -cp /tmp/acdout:<gson.jar> swerve.acd.AcdHarness")
        return 2
    gson = find_gson(args.gson)
    if gson is None:
        print("gson не найден — AcdFormatter не скомпилируется. Харнесс НЕ ВЫПОЛНЕН.")
        print("путь можно дать явно: --gson /path/to/gson-2.11.0.jar")
        return 2

    out = Path(args.out) if args.out else Path(tempfile.mkdtemp(prefix="acd-harness-"))
    out.mkdir(parents=True, exist_ok=True)
    compile_cmd = [javac, "-nowarn", "-encoding", "UTF-8", "-cp", gson, "-d", str(out),
                   "-sourcepath", str(SOURCE_ROOT), str(HARNESS)]
    version = run([javac, "-version"], SWERVE)
    print("компилятор:", (version.stdout + version.stderr).strip().splitlines()[0] if version.stdout or version.stderr else javac)
    result = run(compile_cmd, SWERVE)
    if result.returncode != 0:
        # JDK 25 требует --release для старых целевых версий; пробуем ещё раз с ним
        retry = run(compile_cmd[:1] + ["--release", "21"] + compile_cmd[1:], SWERVE)
        if retry.returncode != 0:
            print("КОМПИЛЯЦИЯ УПАЛА (это реальный дефект, а не отсутствие окружения):")
            print((result.stdout + result.stderr)[-4000:])
            return 1
        result = retry
    print("компиляция: ок")

    java = Path(javac).with_name("java")
    if not java.is_file():
        java = Path("java")
    launched = run([str(java), "-Dfile.encoding=UTF-8", "-cp", f"{out}{os.pathsep}{gson}",
                    "swerve.acd.AcdHarness"], SWERVE)
    sys.stdout.write(launched.stdout)
    if launched.stderr.strip():
        sys.stdout.write(launched.stderr)
    if args.keep:
        print(f"классы сохранены: {out}")
    else:
        shutil.rmtree(out, ignore_errors=True)
    if launched.returncode == 0 and "провалено 0" in launched.stdout:
        print("харнесс пройден")
        return 0
    print("харнесс НЕ ПРОЙДЕН")
    return 1


if __name__ == "__main__":
    sys.exit(main())
