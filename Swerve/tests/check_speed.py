#!/usr/bin/env python3
"""Регрессии модуля Speed (движение) и его точки внедрения в физику 1.21.4.

Проверка делится на три слоя, и только третий — про исходники.

1. Якорь ванильной физики (разбор байткода `minecraft-merged 1.21.4`).
   `LivingEntity.travelInAir` берёт приращение скорости из
   `getFrictionInfluencedSpeed(f)` = `getSpeed() * 0.216 / f^3` на земле и
   `getFlyingSpeed()` (= 0.02) в воздухе, а затем гасит его множителем `f * 0.91`.
   Всё это читается из jar'а при каждом запуске теста: если Mojang поменяет формулу
   или удалит метод, проверка должна сказать об этом прямо, а не молча подтвердить
   устаревшее рассуждение.

2. Числовая модель (тот же интегратор, перенесённый в Python).
   - Линейность: `v_∞(k) = k * v_∞(1)` — множитель ускорения масштабирует предельную
     скорость ровно, без сюрпризов от трения.
   - Монотонность и отсутствие перелёта: `v_n <= v_∞`, приращение за тик не больше
     одного шага ускорения. Это и есть требование «нет резких изменений скорости».
   - Несостоятельность старой схемы: домножение *уже проинтегрированной* скорости
     даёт знаменатель `k * f`; на блоках с высоким трением (лёд 0.98) при `k >= 1.03`
     он превышает 1 — ряд расходится. Модель это воспроизводит, поэтому проверка
     не пуста: в ней есть контроль, который падает на старом поведении.
   - Граница: смещение за тик во всех сценариях на порядок меньше ванильного лимита
     «moved too quickly» (d^2 > 100 блоков^2/тик^2), т.е. модуль не может выбить
     клиента телепортом даже на максимальном множителе.

3. Исходники: у модуля нет доступа к `deltaMovement` вообще (иначе слои 1 и 2
   не защищают ничего), шлюз авто-спринта — ванильный `canStartSprinting`,
   миксин одновладеет целью, настройки собраны и названы по-русски, обработчик
   обёрнут в try/catch с отключением модуля, а в новых русских комментариях и
   документе нет смешения кириллицы и латиницы внутри слова.

Чего проверка НЕ видит: поведение мода на реальном сервере (нет ни JDK, ни
запуска клиента), корректность применения миксинов Mixin'ом в рантайме и любые
свойства за пределами перечисленных формул.

Использование:
    python3 Swerve/tests/check_speed.py
    python3 Swerve/tests/check_speed.py --jar path/to/minecraft-merged.jar

Код возврата: 0 — чисто, 1 — есть находки, 2 — тест не выполнен (нет файла или jar'а).
"""
from __future__ import annotations

import argparse
import re
import sys
import zipfile
from pathlib import Path

TESTS_DIR = Path(__file__).resolve().parent

SPEED_JAVA = "src/main/java/swerve/modules/impl/movement/Speed.java"
MIXIN_JAVA = "src/main/java/swerve/mixins/client/MixinLivingEntity.java"
ACCESSOR_JAVA = "src/main/java/swerve/mixins/accessor/LocalPlayerAccessor.java"
DOC_MD = "docs/Speed.md"
MIXINS_JSON = "src/main/resources/swerve.mixins.json"

# Ванильный лимит перемещения за тик: сервер считает телепортом d^2 > 100.
VANILLA_TICK_LIMIT = 10.0
# Свобода манёвра, которую тест требует сохранить: смещение за тик обязано быть
# не ближе этого множителя к лимиту.
SAFETY_MARGIN = 4.0
# Диапазон трения блоков: 0.4 (песок душ/паучья сеть) .. 0.989 (синекий лёд с чарами).
FRICTION_SWEEP = (0.4, 0.5, 0.6, 0.8, 0.91, 0.98, 0.989)
SPRINT_MULTIPLIER = 1.3  # LivingEntity.SPEED_MODIFIER_SPRINTING: +0.3 ADD_MULTIPLIED_TOTAL


# --------------------------------------------------------------------------- #
# Мелкие утилиты чтения Java
# --------------------------------------------------------------------------- #
def read(path: Path) -> str:
    if not path.is_file():
        print(f"FILE_NOT_FOUND: {path}", file=sys.stderr)
        raise SystemExit(2)
    return path.read_text(encoding="utf-8")


def strip_code(source: str) -> str:
    """Код без комментариев и строковых литералов — для структурных поисков."""
    out: list = []
    i = 0
    n = len(source)
    while i < n:
        ch = source[i]
        if ch == "/" and i + 1 < n and source[i + 1] == "/":
            j = source.find("\n", i)
            i = n if j < 0 else j
        elif ch == "/" and i + 1 < n and source[i + 1] == "*":
            j = source.find("*/", i + 2)
            i = n if j < 0 else j + 2
        elif ch == '"':
            i += 1
            while i < n and source[i] != '"':
                i += 2 if source[i] == "\\" else 1
            i += 1
        elif ch == "'":
            i += 1
            while i < n and source[i] != "'":
                i += 2 if source[i] == "\\" else 1
            i += 1
        else:
            out.append(ch)
            i += 1
    return "".join(out)


def comment_text(source: str) -> str:
    """Только комментарии — для проверки смешения кириллицы и латиницы."""
    chunks: list = []
    for line in source.splitlines():
        stripped = line.strip()
        if stripped.startswith("//"):
            chunks.append(stripped[2:])
        elif stripped.startswith("*"):
            chunks.append(stripped[1:])
        idx = line.find("//")
        if idx >= 0 and '"' not in line[:idx]:
            chunks.append(line[idx + 2:])
    return "\n".join(chunks)


def strip_comments_only(source: str) -> str:
    """Исходник без комментариев, но со строковыми литералами: цели миксинов записаны строками."""
    out = []
    i = 0
    end = len(source)
    while i < end:
        ch = source[i]
        if ch == "/" and i + 1 < end and source[i + 1] == "/":
            j = source.find("\n", i)
            i = end if j < 0 else j
        elif ch == "/" and i + 1 < end and source[i + 1] == "*":
            j = source.find("*/", i + 2)
            i = end if j < 0 else j + 2
        else:
            out.append(ch)
            i += 1
    return "".join(out)


def method_body(code: str, name: str) -> str:
    """Тело метода по имени (первое совпадение), от первой '{' до парной '}'."""
    match = re.search(r"\b" + re.escape(name) + r"\s*\(", code)
    if match is None:
        return ""
    brace = code.find("{", match.end())
    if brace < 0:
        return ""
    depth = 0
    for index in range(brace, len(code)):
        char = code[index]
        if char == "{":
            depth += 1
        elif char == "}":
            depth -= 1
            if depth == 0:
                return code[brace:index + 1]
    return ""


def statements(body: str) -> list:
    """Последовательность операторов тела — порядок проверок имеет значение.

    Границей считается `;` верхнего уровня и закрывающая скобка блока: без второй
    `if (x) { return; }` склеивается со следующим оператором, и порядок guard'ов
    читается неверно.
    """
    inner = body[1:-1] if body.startswith("{") and body.endswith("}") else body
    parts: list = []
    depth = 0
    current: list = []
    for char in inner:
        if char in "{(":
            depth += 1
        elif char in "})":
            depth -= 1
        boundary = depth <= 0 and (char == ";" or (char == "}" and depth == 0))
        if boundary:
            if char == "}":
                current.append(char)
            text = " ".join("".join(current).split())
            if text not in ("", "}"):
                parts.append(text)
            current = []
        else:
            current.append(char)
    tail = " ".join("".join(current).split())
    if tail:
        parts.append(tail)
    return parts


def has_early_return_before(statements_list: list, needle: str, guard: str) -> bool:
    """`guard` встречается раньше первого вхождения `needle` — то есть реально бережёт."""
    guard_index = next((i for i, s in enumerate(statements_list) if guard in s), None)
    needle_index = next((i for i, s in enumerate(statements_list) if needle in s), None)
    return guard_index is not None and needle_index is not None and guard_index < needle_index


# --------------------------------------------------------------------------- #
# Слой 1: якорь по байткоду
# --------------------------------------------------------------------------- #
def vanilla_anchor(report: list, report_skip: list) -> dict:
    """Вытащить константы travelInAir/getFrictionInfluencedSpeed из jar'а."""
    try:
        sys.path.insert(0, str(TESTS_DIR))
        import mc_disasm  # type: ignore
    except Exception as error:  # noqa: BLE001 - тест обязан сказать «не выполнено»
        report_skip.append(f"нет доступа к mc_disasm ({error})")
        return {}
    jar = mc_disasm.find_jar("")
    if jar is None or not Path(str(jar)).is_file():
        report_skip.append("не найден minecraft-merged jar (нужна сборка Gradle/Loom)")
        return {}

    from mc_disasm import parse_class, disassemble  # type: ignore

    anchor = {"jar": str(jar)}

    def bodies(class_name: str) -> dict:
        with zipfile.ZipFile(str(jar)) as archive:
            entry = class_name.replace(".", "/") + ".class"
            if entry not in archive.namelist():
                report.append(f"в jar нет класса {class_name}")
                return {}
            pool, name, super_name, methods, fields = parse_class(archive.read(entry))
            result: dict = {}
            for info in methods:
                if not info.code:
                    continue
                result[info.name] = (info, "\n".join(disassemble(pool, info.code, info.lines)))
            return result

    living = bodies("net.minecraft.world.entity.LivingEntity")
    if not living:
        report_skip.append("не удалось разобрать LivingEntity")
        return anchor

    info = living.get("getFrictionInfluencedSpeed")
    if info is None:
        report.append("в LivingEntity нет getFrictionInfluencedSpeed — точка внедрения устарела")
    else:
        method, text = info
        if method.descriptor != "(F)F":
            report.append(f"getFrictionInfluencedSpeed имеет дескриптор {method.descriptor}, ожидался (F)F")
        for token in ("onGround", "getSpeed", "0.216", "getFlyingSpeed"):
            if token not in text:
                report.append(f"формула ускорения изменилась: в getFrictionInfluencedSpeed нет «{token}»")
        if "invokedynamic" in text:
            report.append("getFrictionInfluencedSpeed стал polymorphic — чтение формулы недостоверно")
        anchor["accel_expression"] = text

    method, text = living.get("travelInAir", (None, ""))
    if method is None:
        report.append("в LivingEntity нет travelInAir — модель движения перестроена")
    else:
        for token, meaning in (("Block.getFriction", "трение берётся из блока под ногами"),
                              ("0.9100000262260437", "аварийное трение 0.91 вне земли"),
                              ("handleRelativeFrictionAndCalculateMovement", "ускорение входит через handleRelative...")):
            if token not in text:
                report.append(f"travelInAir: не найдено «{token}» ({meaning})")
        anchor["air_friction"] = 0.91

    method, text = living.get("getFlyingSpeed", (None, ""))
    if method is None or "0.019999999552965164" not in text:
        report.append("getFlyingSpeed: базовое воздушное ускорение 0.02 не найдено")
    else:
        anchor["air_accel"] = 0.02

    # Трение блока по умолчанию (0.6) и лимит «moved too quickly» — из BlockBehaviour.
    block = bodies("net.minecraft.world.level.block.state.BlockBehaviour$Properties")
    method, text = block.get("<init>", (None, ""))
    if method is None or "0.6000000238418579" not in text:
        report.append("BlockBehaviour$Properties: трение по умолчанию 0.6 не подтверждено")
    else:
        anchor["default_friction"] = 0.6
    return anchor


# --------------------------------------------------------------------------- #
# Слой 2: числовая модель
# --------------------------------------------------------------------------- #
def simulate(accel: float, drag: float, ticks: int = 4000) -> list:
    """Ванический интегратор travelInAir: смещение за тик d_n = d_(n-1)*drag + accel."""
    out: list = []
    velocity = 0.0
    for _ in range(ticks):
        displacement = velocity * drag + accel
        out.append(displacement)
        velocity = displacement
    return out


def numeric_model(report: list, anchor: dict) -> None:
    air_friction = anchor.get("air_friction", 0.91)
    air_accel = anchor.get("air_accel", 0.02)
    base_speed = 0.1  # Attributes.MOVEMENT_SPEED для игрока (проверено: Player.createPlayerAttributes)

    max_seen = 0.0
    for friction in FRICTION_SWEEP:
        drag = friction * air_friction
        if drag >= 1.0:
            report.append(f"модель: при трении {friction} знаменатель >= 1, sweep некорректен")
            continue
        accel = base_speed * (0.216 / (friction ** 3))
        terminal = accel / (1.0 - drag)
        for factor in (1.0, 1.01, 1.08, 1.15, 1.3, 1.6):
            series = simulate(accel * factor, drag)
            scaled = [value * factor for value in simulate(accel, drag)]
            # 2.1 линейность
            worst = max(abs(a - b) for a, b in zip(series, scaled))
            if worst > 1e-9:
                report.append(f"множитель не линеен: трение {friction}, k={factor}, расхождение {worst:.3e}")
            # 2.2 монотонность и отсутствие перелёта
            for index in range(1, len(series)):
                if series[index] < series[index - 1] - 1e-12:
                    report.append(f"разгон немонотонен: трение {friction}, k={factor}, тик {index}")
                    break
            overshoot = max(series) - terminal * factor
            if overshoot > 1e-9:
                report.append(f"перелёт предельной скорости на {overshoot:.3e}: трение {friction}, k={factor}")
            # 2.3 приращение за тик не больше одного шага ускорения
            steps = [series[0]] + [series[i] - series[i - 1] for i in range(1, len(series))]
            if max(steps) > accel * factor + 1e-9:
                report.append(f"прыжок скорости за тик больше шага ускорения: трение {friction}, k={factor}")
            max_seen = max(max_seen, max(series))

    # 2.4 контроль: старая схема (домножение проинтегрированной скорости) расходится
    diverged = False
    for friction in FRICTION_SWEEP:
        drag = friction * air_friction
        accel = base_speed * (0.216 / (friction ** 3))
        series = []
        velocity = 0.0
        for _ in range(600):
            velocity = (velocity * 1.15) * drag + accel
            series.append(velocity)
        if series[-1] > VANILLA_TICK_LIMIT:
            diverged = True
    if not diverged:
        report.append("контроль не сработал: старая схема не расходится — модель пора пересматривать")

    # 2.5 воздух: удельный член 0.02 тоже масштабируется, предельная скорость в воздухе
    for factor in (1.0, 1.6):
        terminal = (air_accel * factor) / (1.0 - air_friction)
        max_seen = max(max_seen, terminal)
    if max_seen * VANILLA_TICK_LIMIT > VANILLA_TICK_LIMIT ** 2 / SAFETY_MARGIN:
        report.append(f"смещение за тик {max_seen:.3f} подошло к ванильному лимиту {VANILLA_TICK_LIMIT}")
    if max_seen < 0.05:
        report.append(f"модель даёт бессмысленный потолок {max_seen:.3f} блоков/тик — sweep сломан")


# --------------------------------------------------------------------------- #
# Слой 3: исходники
# --------------------------------------------------------------------------- #
def check_speed_module(root: Path, report: list) -> None:
    source = read(root / SPEED_JAVA)
    code = strip_code(source)

    forbidden = ["setDeltaMovement", "getDeltaMovement", "addDeltaMovement", "Vec3",
                 "horizontalCollision", "verticalCollision", "applyFriction", "getFriction",
                 "setSprinting(false)", "MathHelper", "setNoPhysics", "teleportTo"]
    for token in forbidden:
        if token in code:
            report.append(f"Speed.java снова трогает интегрированное движение: «{token}»")

    accel = method_body(code, "onAcceleration")
    if not accel:
        report.append("в Speed.java нет метода onAcceleration — миксин не к чему подключаться")
        return
    body_statements = statements(accel)
    required_guards = ["isEnable", "Float.isFinite(acceleration)", "player == null",
                       "entity != player", "getAbilities().flying", "isPassenger()",
                       "onlyGround.get()", "onGround()", "onlyForward.get()", "hasForwardImpulse"]
    for guard in required_guards:
        if guard not in accel:
            report.append(f"onAcceleration: потеряна защита «{guard}»")
    if not has_early_return_before(body_statements, "acceleration * factor", "Minecraft.getInstance()"):
        report.append("onAcceleration: доступ к Minecraft.getInstance() раньше проверки множителя")
    if "setSprinting" in accel:
        report.append("onAcceleration не должен трогать спринт — это состояние, а не ускорение")
    if "return acceleration * factor" not in " ".join(accel.split()):
        report.append("onAcceleration: итог не собирается домножением ускорения")

    tick = method_body(code, "onTick")
    if "swerve$canStartSprinting()" not in tick:
        report.append("onTick: авто-спринт не опирается на ванильный canStartSprinting")
    if "setSprinting(true)" not in tick:
        report.append("onTick: авто-спринт нигде не включает спринт")
    if "setSprinting(false)" in tick:
        report.append("onTick: модуль гасит спринт — это состояние игрока, а не право модуля")
    if not has_early_return_before(statements(tick), "setSprinting(true)", "player == null"):
        report.append("onTick: нет нулевой проверки игрока перед setSprinting")

    # Настройки: состав, имена, границы, подключение
    settings = re.findall(r'new\s+(FloatSetting|BooleanSetting|ModeSetting)\s*\(\s*"([^"]+)"', source)
    names = [name for _, name in settings]
    if len(names) != len(set(names)):
        report.append(f"Speed.java: дубли имён настроек — {sorted(names)}")
    for name in names:
        if not re.search(r"[\u0400-\u04FF]", name):
            report.append(f"Speed.java: имя настройки «{name}» не по-русски")
    if "Множитель" not in names or "Авто-спринт" not in names:
        report.append(f"Speed.java: ожидаемые настройки «Множитель»/«Авто-спринт» не найдены — {names}")
    add = re.search(r"addSettings\s*\(([^)]*)\)", code, re.S)
    if add is None:
        report.append("Speed.java: addSettings не вызывается")
    else:
        wired = re.findall(r"\b([A-Za-z_][A-Za-z0-9_]*)\b", add.group(1))
        fields = re.findall(r"private final (?:Float|Boolean|Mode)Setting\s+(\w+)", code)
        missing = [name for name in fields if name not in wired]
        if missing:
            report.append(f"Speed.java: настройки не переданы в addSettings: {missing}")
        extra = [name for name in wired if name not in fields and name not in ("this",)]
        if extra:
            report.append(f"Speed.java: в addSettings передано неизвестное: {extra}")
    for removed in ("preserveY", "respectCollision", "friction", "maxSpeed", "mode"):
        if re.search(r"\b" + removed + r"\b", code):
            report.append(f"Speed.java: мёртвая настройка «{removed}» вернулась")

    match = re.search(r"MAX_MULTIPLIER\s*=\s*([0-9.Ff]+)\s*;", code)
    if match is None:
        report.append("Speed.java: нет константы MAX_MULTIPLIER")
    else:
        top = float(match.group(1).rstrip("fF"))
        setting = re.search(r'new FloatSetting\(\s*"Множитель"\s*,\s*([0-9.Ff]+)\s*,\s*([0-9.FfA-Z_]+)\s*,\s*([0-9.FfA-Z_]+)\s*,\s*([0-9.Ff]+)\s*\)', source)
        if setting is None:
            report.append("Speed.java: настройка «Множитель» не найдена или имеет другой порядок аргументов")
        else:
            const = dict(re.findall(r"static final float (\w+)\s*=\s*([0-9.Ff]+)\s*;", code))

            def num(token):
                token = token.strip()
                return float(const.get(token, token).rstrip("fF"))

            default, low, high, step = (num(x) for x in setting.groups())
            if high != top:
                report.append(f"Speed.java: «Множитель» допускает {high}, а предел в коде {top}")
            if low > 1.0 or not (low <= default <= high):
                report.append(f"Speed.java: границы «Множитель» [{low}, {high}] не содержат значение по умолчанию {default}")
            if not (0.0 < step <= 0.05):
                report.append(f"Speed.java: шаг «Множитель» {step} неудобен для ползунка")
        if top >= VANILLA_TICK_LIMIT:
            report.append(f"Speed.java: предел множителя {top} выводит смещение за тик за ванильный лимит")


def check_mixin(root: Path, report: list) -> None:
    source = read(root / MIXIN_JAVA)
    annotations = strip_comments_only(source)
    code = strip_code(source)
    if 'method = "getFrictionInfluencedSpeed"' not in annotations:
        report.append("MixinLivingEntity: нет @ModifyReturnValue на getFrictionInfluencedSpeed")
        return
    if annotations.count("getFrictionInfluencedSpeed") != 1:
        report.append("MixinLivingEntity: цель getFrictionInfluencedSpeed встречается больше одного раза")
    if '@At("RETURN")' not in annotations:
        report.append("MixinLivingEntity: хук не на RETURN — значение атрибута будет подменено не полностью")
    handler = method_body(code, "swerve$speedAcceleration")
    if not handler:
        report.append("MixinLivingEntity: обработчик swerve$speedAcceleration не найден")
        return
    first = statements(handler)[:1]
    if not first or "LocalPlayer" not in first[0] or "return original" not in first[0]:
        report.append("MixinLivingEntity: проверка «это не местный игрок» не идёт первой строкой обработчика")
    for token in ("try {", "catch (Exception", "Swerve.LOGGER.error", "setEnabled(false)", "return original"):
        if token not in handler:
            report.append(f"MixinLivingEntity: в обработчике нет «{token}»")
    if "ModuleManager.get(Speed.class)" not in handler:
        report.append("MixinLivingEntity: обработчик не берёт модуль через ModuleManager.get(Speed.class)")

    # Одновладение целью: никто больше не должен трогать тот же метод.
    owners = []
    for path in (root / "src/main/java/swerve/mixins").rglob("*.java"):
        text = strip_comments_only(path.read_text(encoding="utf-8"))
        if "getFrictionInfluencedSpeed" in text:
            owners.append(path.name)
    if owners != ["MixinLivingEntity.java"]:
        report.append(f"цель getFrictionInfluencedSpeed объявлена в нескольких миксинах: {sorted(owners)}")


def check_accessor(root: Path, report: list) -> None:
    source = read(root / ACCESSOR_JAVA)
    if '@Invoker("canStartSprinting")' not in source:
        report.append("LocalPlayerAccessor: нет @Invoker(\"canStartSprinting\") — авто-спринт снова наугад")
    if "boolean swerve$canStartSprinting();" not in " ".join(source.split()):
        report.append("LocalPlayerAccessor: объявление swerve$canStartSprinting() отсутствует")


def check_docs_and_hygiene(root: Path, report: list) -> None:
    doc_path = root / DOC_MD
    if not doc_path.is_file():
        report.append(f"нет документа {DOC_MD}")
        return
    doc = doc_path.read_text(encoding="utf-8")
    headings = re.findall(r"^##\s+\d+\.\s+(.+)$", doc, re.M)
    required = ["Поведение", "Физика", "Проверки", "Взаимодействие"]
    for name in required:
        if not any(name in heading for heading in headings):
            report.append(f"docs/Speed.md: нет раздела про «{name}» (есть: {headings})")
    if len(headings) < 6:
        report.append(f"docs/Speed.md: разделов {len(headings)} — ожидается структура из 8")
    if re.search(r"^#{1,3}\s*НЕ ПРОВЕРЕНО", doc, re.M) is None:
        report.append("docs/Speed.md: нет честного списка непроверенного (НЕ ПРОВЕРЕНО)")
    if re.search(r"(не флагу|без флаго|обхож[а-я]* Grim|Grim не)", doc, re.I):
        report.append("docs/Speed.md: формулировка про обход античита недопустима — нужен разбор риска")

    targets = {doc_path: None, root / SPEED_JAVA: "java", root / MIXIN_JAVA: "java",
               root / ACCESSOR_JAVA: "java"}
    glue = re.compile(r"[\u0400-\u04FF][A-Za-z]|[A-Za-z][\u0400-\u04FF]")
    # Собственный опыт: в русские тексты регулярно проскакивают английские слова и
    # символы посторонних письменностей (CJK). Их не ловит ни один компилятор.
    alien = re.compile(r"[\u3000-\u9fff\uf900-\ufaff\uac00-\ud7af\u0600-\u06ff\u4e00-\u9fff]")
    for path, kind in targets.items():
        text = path.read_text(encoding="utf-8")
        prose = comment_text(text) if kind == "java" else text
        if kind == "java":
            prose = re.sub(r'"[^"]*"', "", prose)
        else:
            prose = re.sub(r"```.*?```", "", prose, flags=re.S)
            prose = re.sub(r"`[^`]*`", "", prose)
            prose = re.sub(r"\b[\w./$-]+\.(java|json|md|jar|py)\b", "", prose)
        for line in prose.splitlines():
            cleaned = re.sub(r"https?://\S+", "", line)
            cleaned = re.sub(r"\b[A-Za-z][A-Za-z0-9_$]*(?=[(),;.:])", "", cleaned)
            hit = glue.search(cleaned)
            if hit is not None:
                report.append(f"{path.name}: смешение кириллицы и латиницы в «{hit.group(0)}» — строка: {line.strip()[:80]}")
                continue
            stray = alien.search(cleaned)
            if stray is not None:
                report.append(f"{path.name}: посторонняя письменность «{stray.group(0)}» — строка: {line.strip()[:80]}")


# --------------------------------------------------------------------------- #
def main() -> int:
    parser = argparse.ArgumentParser(description="регрессии модуля Speed")
    parser.add_argument("--root", default=None, help="каталог Swerve (по умолчанию — рядом с тестом)")
    parser.add_argument("--jar", default=None, help="minecraft-merged jar (по умолчанию — из loom-кэша)")
    args = parser.parse_args()

    root = Path(args.root) if args.root else TESTS_DIR.parent
    if not (root / "src/main/java/swerve").is_dir():
        print(f"STRUCTURE_NOT_FOUND: {root / 'src/main/java/swerve'}", file=sys.stderr)
        return 2

    report: list = []
    skip: list = []
    if args.jar:
        # Явный --jar пока не поддерживается разбором: читаем из loom-кэша.
        skip.append("--jar игнорируется, используется автоматический поиск jar'а")
    anchor = vanilla_anchor(report, skip)

    if anchor and not skip:
        numeric_model(report, anchor)

    check_speed_module(root, report)
    check_mixin(root, report)
    check_accessor(root, report)
    check_docs_and_hygiene(root, report)

    if skip and not report:
        for line in skip:
            print("пропущено:", line)
        print("тест не выполнен: численный слой недоступен")
        return 2

    for line in report:
        print("найдено:", line)
    print(f"найдено: {len(report)}")
    return 1 if report else 0


if __name__ == "__main__":
    sys.exit(main())
