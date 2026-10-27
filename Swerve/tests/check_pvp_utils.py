#!/usr/bin/env python3
"""Статические регрессии утилит PvP: TPS, квантование поворотов, коррекция WASD.

Это проверки тех поломок, которые компилятор не видит, а игрок чувствует: формула
в порядке синтаксиса, но бессмысленна по смыслу; константа, которая молча обнуляет
целую функцию; два почти идентичных метода, которые расходятся на одном краю диапазона.

Каждая проверка привязана к факту, полученному разбором байткода `minecraft-merged
1.21.4` (см. `Swerve/tests/mc_disasm.py`), а не к мнению о том, «как обычно бывает».

Что проверяется
  1. `ServerTPS`: пакет времени в 1.21.4 уходит раз в секунду (`MinecraftServer
     .tickChildren` вызывает `synchronizeTime` под `tickCount % 20 == 0`), значит
     порог «пауза, а не лаг» обязан быть НЕ МЕНЬШЕ 20 периодов пакета. Иначе
     задержка 2 с (10 TPS) отбрасывается и оценка навсегда залипает на 20, а
     «Синхронизация по TPS» в KillAura никогда не срабатывает.
  2. `ServerTPS`: формула пересчёта — `MAX_TPS * (период / задержка)`; численно
     проверяется, что 1 с → 20 TPS, 2 с → 10 TPS, 60 с (пауза) → без изменений.
  3. `GcdUtil`: шаг квантования выведен из ванильной формулы чувствительности
     (`s * 0.6 + 0.2`, куб, `* 8`, затем `* 0.15` из `Entity.turn`), а не из её
     «обратной» версии с `cbrt` — она тождественно сводится к значению ползунка.
  4. `MoveUtil.fixMovement`, `MoveUtil.fixMovementToTarget` и
     `ElytraFlightRotation.fixMovement` сравнивают углы ОДИНАКОВО: через
     `Mth.wrapDegrees`. Без оборачивания на переходе через ±180° выбирается худший
     кандидат (до 44.5° сноса курса).
  5. `ElytraFlightRotation.fixRotate` не делит на нулевой шаг: при чувствительности
     0 % ротация залипала (`round(d/0)*0 = 0`, цель никогда не достигается).
  6. `KillAura.rayTraceOk`: ветка «Улучшить хитбоксы» не отменяет «Не бить через
     блоки» — включение одной настройки не должно молча выключать другую.
  7. Три мёртвые настройки-обещания из KillAura по-прежнему подключены к логике
     (`syncTPS`, `noHitThroughBlocks`, `shieldBreaker` вызываются, а не только
     объявлены), и ни одна из удалённых веток не вернулась.

Чего проверка НЕ видит: реальную сеть (частота пакетов на лагающем сервере может
отличаться), поведение миксинов при загрузке и любые численные свойства вне
перечисленных формул.

Использование:
    python3 Swerve/tests/check_pvp_utils.py
    python3 Swerve/tests/check_pvp_utils.py --root Swerve

Код возврата: 0 — чисто, 1 — есть находки, 2 — структура репозитория не найдена.
"""
from __future__ import annotations

import argparse
import re
import sys
from pathlib import Path


def read(path: Path) -> str:
    if not path.is_file():
        print(f"FILE_NOT_FOUND: {path}", file=sys.stderr)
        raise SystemExit(2)
    return path.read_text(encoding="utf-8")


def strip_comments(source: str) -> str:
    """Убрать комментарии и строки: поиск по коду не должен ловить текст объяснений."""
    out = []
    index = 0
    length = len(source)
    while index < length:
        pair = source[index:index + 2]
        if pair == "//":
            end = source.find("\n", index)
            index = length if end < 0 else end
        elif pair == "/*":
            end = source.find("*/", index + 2)
            index = length if end < 0 else end + 2
        elif source[index] == '"':
            index += 1
            while index < length and source[index] != '"':
                index += 2 if source[index] == "\\" else 1
            index += 1
        else:
            out.append(source[index])
            index += 1
    return "".join(out)


def brace_block(source: str, opening: int) -> str:
    """Тело блока `{...}`, начиная с позиции символа `{`."""
    depth = 0
    for index in range(opening, len(source)):
        if source[index] == "{":
            depth += 1
        elif source[index] == "}":
            depth -= 1
            if depth == 0:
                return source[opening:index + 1]
    return source[opening:]


def numeric_constants(code: str) -> dict:
    """Имена и значения числовых `static final` констант класса (с вычислением выражений).

    Нужно, чтобы проверка читала `RESYNC_NS = PACKET_PERIOD_NS * (long) MAX_TPS`
    как реальное число: константы связаны, и проверяется именно соотношение,
    а не набор цифр в исходнике.
    """
    values: dict = {}
    pattern = re.compile(
        r"static\s+final\s+(?:long|int|float|double)\s+(\w+)\s*=\s*([^;]+);")
    for _round in range(3):  # выражения могут ссылаться на константы выше по файлу
        for name, raw in pattern.findall(code):
            expression = re.sub(r"\((?:long|int|float|double)\)", "", raw)
            expression = re.sub(r"\b\d[\d_]*[FLDfld]?\b", lambda m: m.group(0).rstrip("FLDfld").replace("_", ""), expression)
            for known, value in values.items():
                expression = re.sub(r"\b" + known + r"\b", repr(float(value)), expression)
            if not re.fullmatch(r"[0-9eE.+\-*/()\s]+", expression):
                continue
            try:
                values[name] = float(eval(expression, {"__builtins__": {}}, {}))
            except Exception:
                continue
    return values


def constant_value(constants: dict, code: str, name: str) -> float | None:
    if name in constants:
        return constants[name]
    match = re.search(r"\b" + re.escape(name) + r"\s*=\s*([\d_.]+)", code)
    return float(match.group(1).replace("_", "")) if match else None


# --------------------------------------------------------------------------- #
# 1–2. ServerTPS: период пакета, порог паузы и сама формула
# --------------------------------------------------------------------------- #
def check_server_tps(java: Path, report: list) -> None:
    path = java / "utils/network/ServerTPS.java"
    source = read(path)
    code = strip_comments(source)

    constants = numeric_constants(code)
    period = constant_value(constants, code, "PACKET_PERIOD_NS")
    resync = constant_value(constants, code, "RESYNC_NS")
    if period is None or resync is None:
        report.append(f"{path.name}: не найдены PACKET_PERIOD_NS/RESYNC_NS — трекер TPS "
                      "снова измеряет пакет, которого в 1.21.4 раз в тик нет")
        return
    if period <= 0:
        report.append(f"{path.name}: PACKET_PERIOD_NS = {period:g}, должен быть положительным")
        return
    # Порог обязан покрывать минимальный измеримый TPS: 20 периодов пакета = 1 TPS.
    if resync < 20 * period:
        report.append(f"{path.name}: RESYNC_NS = {resync:g} < 20 * PACKET_PERIOD_NS "
                      f"({20 * period:g}) — задержки, соответствующие лагу, отбрасываются "
                      "как пауза, и tps навсегда остаётся 20")

    if not re.search(r"MAX_TPS\s*\*", code):
        report.append(f"{path.name}: в формуле нет множителя MAX_TPS — один пакет времени "
                      "это 20 тиков, без него оценка занижена в 20 раз")

    # Численная модель текущей формулы: ловит «формула поехала», а не «текст поменяли».
    match = re.search(r"float\s+raw\s*=\s*([^;]+);", code)
    if match is None:
        report.append(f"{path.name}: не найден расчёт raw — нечего проверять численно")
        return
    formula = match.group(1)
    if "PACKET_PERIOD_NS" not in formula and "1.0E9" not in formula:
        report.append(f"{path.name}: расчёт TPS не привязан к PACKET_PERIOD_NS "
                      "(или 1e9 нс) — константа периода может разойтись с формулой")
        return
    tps_at = model_tps(formula, period, resync)
    if tps_at is None:
        report.append(f"{path.name}: расчёт raw не разбирается автоматом: {formula!r}")
        return
    nominal = tps_at(period)
    laggy = tps_at(period * 2)
    paused = tps_at(resync * 6)
    if nominal is None:
        report.append(f"{path.name}: штатный интервал пакета ({period:g} нс) отбрасывается "
                      "как пауза — оценка не обновится никогда")
    elif abs(nominal - 20.0) > 0.05:
        report.append(f"{path.name}: пакет раз в {period:g} нс должен давать 20 TPS, "
                      f"получили {nominal:g}")
    if laggy is None:
        report.append(f"{path.name}: отсчёт с удвоенным интервалом (сервер на 10 TPS) "
                      "отбрасывается как пауза — лаг не измерим никогда, «Синхронизация "
                      "по TPS» мёртвая")
    elif abs(laggy - 10.0) > 0.05:
        report.append(f"{path.name}: сервер на 10 TPS должен читаться как 10.0, получили "
                      f"{laggy:g} — коррекция темпа удара работает неверно")
    if paused is not None:
        report.append(f"{path.name}: задержка {resync * 6:g} нс обязана считаться паузой и "
                      f"НЕ менять оценку (получили {paused:g})")


def model_tps(formula: str, period: int, resync: int):
    """Возвращает функцию delay -> tps (None, если отсчёт отбрасывается как пауза)."""
    text = (formula.replace("MAX_TPS", "20.0")
                    .replace("PACKET_PERIOD_NS", str(period))
                    .replace("1.0E9F", "1.0e9")
                    .replace("1.0E9", "1.0e9")
                    .replace("(float)", "")
                    .replace("F", "")).strip()
    if "delay" not in text:
        return None
    try:
        compiled = compile(text, "<tps-formula>", "eval")
    except (SyntaxError, ValueError):
        return None

    def evaluate(delay: int):
        if delay <= 0 or delay > resync:
            return None
        try:
            raw = eval(compiled, {"__builtins__": {}}, {"delay": float(delay)})
        except Exception:
            return None
        return round(max(0.0, min(20.0, round(raw * 10.0) / 10.0)), 4)

    return evaluate


# --------------------------------------------------------------------------- #
# 3. GcdUtil: шаг квантования из ванильной формулы
# --------------------------------------------------------------------------- #
def check_gcd(java: Path, report: list) -> None:
    path = java / "utils/rotation/GcdUtil.java"
    code = strip_comments(read(path))
    if "0.6" not in code or "0.2" not in code:
        report.append(f"{path.name}: нет множителей 0.6/0.2 — шаг больше не совпадает с "
                      "ванильным (MouseHandler.turnPlayer: sensitivity*0.6+0.2, куб, *8)")
    if re.search(r"/\s*0\.15\s*/\s*8", code):
        report.append(f"{path.name}: появилась «обратная» формула с /0.15/8 — она тождественно "
                      "сводится к значению ползунка чувствительности и квантует не по GCD")
    if not re.search(r"pow\s*\(", code) and not re.search(r"\*\s*[\w.]+\s*\*\s*[\w.]+", code):
        report.append(f"{path.name}: шаг не возводится в куб — это не ванильный GCD")
    if "0.15" not in code:
        report.append(f"{path.name}: забыт множитель 0.15 из Entity.turn — квантование "
                      "будет в 6.67 раза крупнее реального шага мыши")


# --------------------------------------------------------------------------- #
# 4. Сравнение углов в трёх копиях коррекции ввода
# --------------------------------------------------------------------------- #
def check_wrapped_difference(path: Path, code: str, method: str, report: list) -> None:
    """Строка, вычисляющая разницу углов, обязана оборачивать её в Mth.wrapDegrees.

    Проверяется именно выражение разницы, а не наличие слова в теле метода: иначе
    «wrapDegrees» от targetAngle/testAngle маскирует незащищённое сравнение.
    """
    match = re.search(r"static void " + re.escape(method) + r"\([^)]*\)\s*\{", code)
    if match is None:
        report.append(f"{path.name}: метод {method} не найден")
        return
    body = brace_block(code, code.index("{", match.end() - 1))
    difference = re.search(r"(?:float|double)\s+difference\s*=\s*([^;]+);", body, re.DOTALL)
    if difference is None:
        report.append(f"{path.name}: {method} — не найдено сравнение углов (difference): "
                      "проверка не может подтвердить корректность выбора направления")
        return
    line = difference.group(1)
    if "wrapDegrees" not in line:
        report.append(f"{path.name}: {method} сравнивает разницу углов без Mth.wrapDegrees — "
                      "на переходе через ±180° выбирается худший кандидат (снос курса до 45°)")


def check_move_util(java: Path, report: list) -> None:
    path = java / "utils/player/MoveUtil.java"
    source = strip_comments(read(path))
    for method in ("fixMovement", "fixMovementToTarget"):
        check_wrapped_difference(path, source, method, report)

    elytra = java / "utils/movement/ElytraFlightRotation.java"
    elytra_source = strip_comments(read(elytra))
    check_wrapped_difference(elytra, elytra_source, "fixMovement", report)


# --------------------------------------------------------------------------- #
# 5. Защита от нулевого шага квантования
# --------------------------------------------------------------------------- #
def check_rotate_guard(java: Path, report: list) -> None:
    path = java / "utils/movement/ElytraFlightRotation.java"
    source = strip_comments(read(path))
    match = re.search(r"static float fixRotate\(float[^)]*\)\s*\{", source)
    if match is None:
        report.append(f"{path.name}: fixRotate не найден")
        return
    body = brace_block(source, source.index("{", match.end() - 1))
    division = body.find("/ gcd")
    guard = re.search(r"gcd\s*<=?\s*0|!\s*\(\s*gcd\s*>\s*0", body)
    if division >= 0 and (guard is None or guard.start() > division):
        report.append(f"{path.name}: деление на gcd идёт до проверки шага — при чувствительности "
                      "0% шаг равен 0, round(d/0)*0 обнуляет поворот, и ротация залипает")


# --------------------------------------------------------------------------- #
# 6–7. KillAura: настройки не отменяют друг друга и не остались мёртвыми
# --------------------------------------------------------------------------- #
def check_killaura_settings(java: Path, report: list) -> None:
    path = java / "modules/impl/combat/KillAura.java"
    source = strip_comments(read(path))

    match = re.search(r"private boolean rayTraceOk\([^)]*\)\s*\{", source)
    if match is None:
        report.append(f"{path.name}: rayTraceOk не найден")
        return
    body = brace_block(source, source.index("{", match.end() - 1))
    smart = re.search(r"if\s*\(\s*smartHitbox\.get\(\)[^{]*\{", body)
    if smart is None:
        report.append(f"{path.name}: ветка «Улучшить хитбоксы» пропала из rayTraceOk")
    else:
        branch = brace_block(body, body.index("{", smart.end() - 1))
        if "noHitThroughBlocks" not in branch:
            report.append(f"{path.name}: «Улучшить хитбоксы» игнорирует «Не бить через блоки» "
                          "— при включённых обеих настройках аура бьёт сквозь стену")

    for setting, usage in (
        ("syncTPS", r"applyTpsScale\s*\("),
        ("noHitThroughBlocks", r"noHitThroughBlocks\.get\(\)"),
        ("shieldBreaker", r"shieldBreaker\.get\(\)\s*&&\s*breakShield"),
    ):
        declared = re.search(r"Setting\s+" + setting + r"\b", source)
        used = re.search(usage, source)
        if declared and not used:
            report.append(f"{path.name}: настройка {setting} объявлена, но не влияет на поведение")

    for removed in ("pendingCritTicks", "canCrit", "dynamicCooldown", "lastRequiredFallDistance"):
        if re.search(r"\b" + removed + r"\b", source):
            report.append(f"{path.name}: удалённое поле {removed} вернулось — вместе с ним "
                          "вернутся и обходы гейтов удара")


def main() -> int:
    parser = argparse.ArgumentParser(description="регрессии утилит PvP клиента Swerve")
    parser.add_argument("--root", default=None, help="каталог Swerve (по умолчанию — рядом с тестом)")
    args = parser.parse_args()

    root = Path(args.root) if args.root else Path(__file__).resolve().parents[1]
    java = root / "src/main/java/swerve"
    if not java.is_dir():
        print(f"STRUCTURE_NOT_FOUND: {java}", file=sys.stderr)
        return 2

    report: list = []
    check_server_tps(java, report)
    check_gcd(java, report)
    check_move_util(java, report)
    check_rotate_guard(java, report)
    check_killaura_settings(java, report)

    for line in report:
        print("найдено:", line)
    print(f"найдено: {len(report)}")
    return 1 if report else 0


if __name__ == "__main__":
    sys.exit(main())
