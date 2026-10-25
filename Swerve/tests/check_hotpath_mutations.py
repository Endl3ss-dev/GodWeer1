#!/usr/bin/env python3
"""Мутационный тест к check_hotpath.py: проверка ловит регрессии, а не молчит.

Зачем: «найдено: 0» само по себе ничего не доказывает — тот же ноль печатает и
проверка, которая ничего не ищет. Скрипт берёт полную копию `src/main/java`,
вносит по одной заведомой регрессии, гоняет на копии `check_hotpath.py` и
требует, чтобы проверка упала с кодом 1 и назвала именно то правило, которое
должно сработать. Отдельной мутацией идёт контроль: без правок копия обязана
дать 0 находок, иначе весь прогон сравнивает не то.

Мутации делятся на два типа, и оба обязательны:
  * прямая аллокация в известном горячем методе (правило видно сразу);
  * аллокация в методе, который горячим становится ТОЛЬКО по цепочке вызовов
    (проверяет, что достижимость действительно работает, а не декоративна);
  * плюс отрицательный контроль — аллокация в методе, который горячим не является
    (например, в конструкторе модуля): проверка обязана остаться зелёной, иначе
    она ловит всё подряд и её исключения бессмысленны.

Использование:
    python3 Swerve/tests/check_hotpath_mutations.py [--keep]

Код возврата: 0 — все мутации пойманы и контроль чист, 1 — есть промахи,
2 — не установлены зависимости или не найден src.
"""
from __future__ import annotations

import re
import shutil
import subprocess
import sys
from pathlib import Path

SWERVE = Path(__file__).resolve().parents[1]
SRC = SWERVE / "src" / "main" / "java"
WORK = SWERVE / "build" / "hotpath-mutations"
CHECKER = SWERVE / "tests" / "check_hotpath.py"


def copy_tree() -> Path:
    """Полная копия дерева исходников во временный каталог внутри build/."""
    root = WORK / "src" / "main" / "java"
    if WORK.exists():
        shutil.rmtree(WORK)
    shutil.copytree(SRC, root)
    return root


def run_checker(root: Path) -> tuple[int, str]:
    result = subprocess.run(
        [sys.executable, str(CHECKER), str(root)],
        capture_output=True,
        text=True,
    )
    return result.returncode, result.stdout + result.stderr


def patch(root: Path, rel: str, old: str, new: str) -> None:
    path = root / rel
    text = path.read_text(encoding="utf-8")
    if old not in text:
        raise SystemExit(f"мутация не применилась: фрагмент не найден в {rel}")
    path.write_text(text.replace(old, new, 1), encoding="utf-8")


# --------------------------------------------------------------------------- #
# Мутации: (имя, файл, что меняем, ожидаемое правило или None для «должно быть
# чисто», описание).
# --------------------------------------------------------------------------- #
MUTATIONS = [
    (
        "M1 прямая аллокация в ротации",
        "swerve/modules/impl/combat/rotation/IntaveRotation.java",
        "        double targetX = target.getX();",
        "        net.minecraft.world.phys.Vec3 junk = new net.minecraft.world.phys.Vec3(0.0D, 0.0D, 0.0D);\n"
        "        double targetX = target.getX();",
        "NEW",
        "вернули создание Vec3 в update() элемента ротации",
    ),
    (
        "M2 глаза через getEyePosition",
        "swerve/utils/combat/AuraUtils.java",
        "        double[] view = VIEW;\n        viewVector(player, view);",
        "        net.minecraft.world.phys.Vec3 eye = player.getEyePosition(1.0F);\n"
        "        double[] view = VIEW;\n        viewVector(player, view);",
        "ALLOC_API",
        "в fovFromCamera вернули ванильный геттер, создающий Vec3",
    ),
    (
        "M3 поток в тике ауры",
        "swerve/modules/impl/combat/KillAura.java",
        "        scan.reset();",
        "        scan.reset();\n        minecraft.level.players().stream().count();",
        "STREAM",
        "в поиске цели появился stream()",
    ),
    (
        "M4 захватывающая лямбда в тике",
        "swerve/utils/rotation/RotationController.java",
        "        idleTicks++;",
        "        Runnable junk = () -> Swerve.LOGGER.debug(\"{}\", idleTicks);\n        idleTicks++;",
        "LAMBDA",
        "в tick() контроллера ротации появилась лямбда, захватившая поле",
    ),
    (
        "M5 достижимость через цепочку вызовов",
        "swerve/utils/combat/AuraUtils.java",
        "    public static double strictDistance(Player player, Entity target) {\n        AABB box = target.getBoundingBox();",
        "    public static double strictDistance(Player player, Entity target) {\n"
        "        StringBuilder junk = new StringBuilder(4);\n"
        "        junk.append('x');\n"
        "        AABB box = target.getBoundingBox();",
        "NEW",
        "аллокация внесена в strictDistance, который не является точкой входа: "
        "его горячим делает только вызов из KillAura",
    ),
    (
        "M6 отрицательный контроль (не горячий метод)",
        "swerve/modules/impl/combat/KillAura.java",
        "    public KillAura() {",
        "    public KillAura() {\n        java.util.List<String> junk = new java.util.ArrayList<>();\n        junk.add(\"x\");",
        None,
        "аллокация в конструкторе модуля: конструктор не исполняется в тике, "
        "проверка обязана остаться зелёной",
    ),
    (
        "M7 превышение бюджета исключения",
        "swerve/utils/item/DrinkUtil.java",
        "    private static String decodeHash(String base64) {",
        "    private static String decodeHash(String base64) {\n        StringBuilder junk = new StringBuilder(2);",
        "NEW",
        "в методе с исключением на одну аллокацию появилась вторая (другой сниппет)",
    ),
    (
        "M8 устаревшее исключение",
        "swerve/modules/impl/combat/AutoPotion.java",
        "new ServerboundMovePlayerPacket.Rot(",
        "packetFactory(",
        "УСТАРЕВШИЕ",
        "исключение ссылается на сниппет, которого больше нет: список обязан требовать чистки",
    ),
]


def main(argv) -> int:
    keep = "--keep" in argv
    if not SRC.is_dir():
        print(f"нет каталога исходников: {SRC}")
        return 2

    failures = 0
    root = copy_tree()
    code, output = run_checker(root)
    if code != 0:
        print("КОНТРОЛЬ ПРОВАЛЕН: немодифицированная копия даёт находки")
        print(output)
        failures += 1
    else:
        print("контроль: копия без правок чиста")

    for name, rel, old, new, expected, description in MUTATIONS:
        root = copy_tree()
        patch(root, rel, old, new)
        code, output = run_checker(root)
        if expected is None:
            caught = code == 0
            verdict = "ПОЙМАНО (ложное срабатывание)" if not caught else "чисто, как и требовалось"
        else:
            caught = code == 1 and expected in output
            verdict = "ПОЙМАНО" if caught else "ПРОПУЩЕНО"
        if not caught:
            failures += 1
        print(f"[{'!' if not caught else '+'}] {name}: {verdict}")
        print(f"    {description}")
        if not caught:
            print(output)

    if not keep and WORK.exists():
        shutil.rmtree(WORK, ignore_errors=True)

    total = len(MUTATIONS)
    print(f"\nмутаций: {total}, промахов: {failures}")
    return 0 if failures == 0 else 1


if __name__ == "__main__":
    raise SystemExit(main(sys.argv[1:]))
