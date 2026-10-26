#!/usr/bin/env python3
"""Контракт модуля AntiCheatDetector: интеграция, таблица правил, границы буфера.

Проверка статическая, JDK не нужен. Она закрывает ровно те классы поломок, которые
не видно ни из синтаксиса, ни из `check_mc_api`:

  1. Модуль зарегистрирован (`ModuleManager`), категория MISC, описание непустое.
  2. Каждый миксин, вызывающий `AcdCollector`, перечислен в `swerve.mixins.json`.
  3. Каждый пакетный источник `AcdCollector.on*(<игровой пакет>)` вызывается из миксина
     — иначе собор «есть, но не подключён», и наоборот: мёртвого хендлера быть не должно.
  4. Каждый ACD-хендлер в миксине обёрнут в try/catch, пишет в `Swerve.LOGGER` и гасит
     сбор через `AcdCollector.disableCollection` (домашнее правило миксинов).
  5. Арность вызовов `AcdCollector.<on*>(...)` совпадает с объявлением.
  6. Граница задачи (§5 документа): ни `cancel`, ни `@Redirect`/`@ModifyArg`/
     `@ModifyReturnValue`/`@ModifyVariable`, ни отправка пакетов, ни импорт боевых
     модулей — диагностика читает, но не вмешивается.
  7. Ссылки на членов пакетов `swerve.acd` (методы, константы, вложенные типы)
     существуют в объявляющем классе: компилятора здесь нет, выдуманный метод —
     самый вероятный дефект.
  8. Таблица правил: id уникальны, маркеры строчные и не короче трёх символов, у каждого
     правила есть маркеры, ни одно правило не недостижимо из-за более общего выше.
  9. Симуляция корпуса: независимый порт `matches()` + «первое совпадение выигрывает»
     обязан вернуть те же правила, что и обещает `tests/AcdHarness.java`, и не найти
     ничего на текстов, где правила быть не должно.
 10. Границы буфера: `MIN <= DEFAULT <= MAX` в `AcdStore`, у настройки модуля те же
     границы и то же значение по умолчанию; лимиты чата и буфера обмена положительны и
     совместимы с гардом `paginate`.
 11. Флаги `AcdFormatter.Options` используются и в текстовом, и в JSON-формате.
 12. Каждая настройка модуля добавлена в `addSettings` и прочитана; каждый
     `AcdStore.set*` имеет читающий оппонент (политика не должна быть декоративной).
 13. GUI: `ACT_ACTION` объявлен и обработан, ветка `ActionSetting` в `row()` стоит до
     общего фолбэка, открытие окна настроек НЕ зависит от `isEnable()` — это сценарий
     «выключил модуль → история и Copy All Logs остались».
 14. Исполняемый харнесс: существует, парсится, содержит не меньше 150 проверок,
     зовёт только существующих членов и честно перечисляет, что НЕ проверено.
 15. В файлах ACD и в документе нет чужой письменности (CJK/арабика) и нет прилипшей
     латиницы к русскому слову — тексты отчёта читают люди.
 16. `docs/AntiCheatDetector.md` есть, содержит обязательные разделы и раздел о границе.
 17. Имена pattern-переменных (`x instanceof T name`) не перекрывают параметры объёмлющего
     метода: Java запрещает это, и `javac` отвечает «variable is already defined» — ровно та
     поломка, которую пропустили все предыдущие проверки (проверено на реальном сборе).
 18. Служебные отметки `noteCollection*` не притворяются сетевыми событиями: категория —
     нейтральная, источник — константа, примечание объясняет природу записи. Правило
     родилось из первого прогона на живом сервере, где «Сеть» состояла из них целиком.

Чего проверка не видит: реальную загрузку миксинов, GLFW-буфер обмена, гонки потоков,
поведение в игре. Это проверяется прогоном харнесса и ручным чек-листом в документации.

Использование:
    python3 Swerve/tests/check_acd.py
    python3 Swerve/tests/check_acd.py --root Swerve

Код возврата: 0 — чисто, 1 — есть находки, 2 — структура репозитория не найдена.
"""
from __future__ import annotations

import argparse
import re
import sys
from pathlib import Path

findings: list[str] = []


def fail(message: str) -> None:
    findings.append(message)


def read(path: Path) -> str:
    return path.read_text(encoding="utf-8")


def strip_comments(source: str) -> str:
    """Код без комментариев и строк: для поиска структурных признаков."""
    out = re.sub(r"/\*.*?\*/", " ", source, flags=re.S)
    out = re.sub(r"//[^\n]*", " ", out)
    out = re.sub(r'"(?:\\.|[^"\\])*"', '""', out)
    return out


def strip_strings(source: str) -> str:
    return re.sub(r'"(?:\\.|[^"\\])*"', '""', source)


def balanced(source: str, start: int, opener: str = "(", closer: str = ")") -> str:
    """Содержимое парных скобок, начиная с индекса сразу после открывающей."""
    depth, index, out = 1, start, []
    while index < len(source) and depth:
        char = source[index]
        if char == opener:
            depth += 1
        elif char == closer:
            depth -= 1
            if depth == 0:
                break
        out.append(char)
        index += 1
    return "".join(out)


def strip_method_bodies(source: str, names: tuple[str, ...]) -> str:
    """Убирает тела тривиальных геттеров: `return поле;` не считается использованием политики."""
    out = source
    for name in names:
        match = re.search(r"(?:public|private|protected)[\w\s<>,]*\b" + re.escape(name)
                          + r"\s*\(\s*\)\s*\{", out)
        if not match:
            continue
        end = out.find("}", match.end())
        if end < 0:
            continue
        out = out[:match.start()] + out[end + 1:]
    return out


def region(source: str, start_marker: str, opener: str = "{", closer: str = "}") -> str:
    """Блок от первой `opener` после маркера до парной `closer` (учитывая вложенность)."""
    index = source.find(start_marker)
    if index < 0:
        return ""
    open_at = source.find(opener, index)
    if open_at < 0:
        return ""
    depth = 0
    for i in range(open_at, len(source)):
        if source[i] == opener:
            depth += 1
        elif source[i] == closer:
            depth -= 1
            if depth == 0:
                return source[open_at + 1:i]
    return ""


def split_args(text: str) -> list[str]:
    """Разбор списка аргументов по запятым верхнего уровня."""
    parts: list[str] = []
    depth = 0
    current: list[str] = []
    index = 0
    while index < len(text):
        char = text[index]
        if char == '"':
            end = index + 1
            while end < len(text):
                if text[end] == "\\":
                    end += 2
                    continue
                if text[end] == '"':
                    break
                end += 1
            current.append(text[index:end + 1])
            index = end + 1
            continue
        if char in "([{":
            depth += 1
        elif char in ")]}":
            depth -= 1
        if char == "," and depth == 0:
            parts.append("".join(current).strip())
            current = []
            index += 1
            continue
        current.append(char)
        index += 1
    if "".join(current).strip():
        parts.append("".join(current).strip())
    return parts


LITERALS = re.compile(r'"((?:\\.|[^"\\])*)"')


def literals(text: str) -> list[str]:
    return [m.group(1) for m in LITERALS.finditer(text)]


# --------------------------------------------------------------------------- #
# 1, 12 — модуль и его регистрация
# --------------------------------------------------------------------------- #
def check_registration(root: Path, module_src: str) -> None:
    manager = read(root / "src/main/java/swerve/managers/ModuleManager.java")
    if "AntiCheatDetector()" not in manager:
        fail("ModuleManager: модуль AntiCheatDetector не зарегистрирован")
    if "import swerve.modules.impl.misc.AntiCheatDetector;" not in manager:
        fail("ModuleManager: нет импорта AntiCheatDetector (регистрация по простому имени не соберётся)")
    if 'super("AntiCheatDetector"' not in module_src:
        fail("модуль обязан называться ровно AntiCheatDetector — имя из задания")
    if "ModuleCategory.MISC" not in module_src:
        fail("модуль не в категории MISC")
    if "NOTES" in module_src:
        pass  # заметок быть не должно: всё — в javadoc и docs/


# --------------------------------------------------------------------------- #
# 2, 3, 4, 5 — миксины и точки входа сборщика
# --------------------------------------------------------------------------- #
PACKET_TYPES = re.compile(r"Clientbound\w+Packet|DisconnectionDetails|Packet<\?>")


def collector_sources(collector: str) -> dict[str, int]:
    """Публичные static-обработчики AcdCollector: имя → арность."""
    out: dict[str, int] = {}
    for match in re.finditer(r"public static (?:void|String|int|boolean) (\w+)\(([^)]*)\)", collector):
        params = match.group(2).strip()
        arity = 0 if not params else len(split_args(params))
        out[match.group(1)] = arity
    return out


def check_mixins(root: Path, collector: str) -> None:
    handlers_dir = root / "src/main/java/swerve/mixins/client"
    sources = {p.name: read(p) for p in sorted(handlers_dir.glob("Mixin*.java"))}
    listed_raw = read(root / "src/main/resources/swerve.mixins.json")
    listed = set(re.findall(r'"(?:[a-z][\w.]*\.)?(Mixin\w+)"', listed_raw))

    packet_handlers = {
        name: arity for name, arity in collector_sources(collector).items()
        if name.startswith("on")
        and PACKET_TYPES.search(re.search(r"public static (?:void|String|int|boolean) %s\(([^)]*)\)" % name, collector).group(1))
    }
    if len(packet_handlers) < 8:
        fail(f"AcdCollector: пакетных источников найдено {len(packet_handlers)}, ожидалось не меньше 8")

    called: set[str] = set()
    for name, source in sources.items():
        if "AcdCollector" not in source:
            continue
        if name[:-5] not in listed:
            fail(f"{name}: миксин использует AcdCollector, но не перечислен в swerve.mixins.json")
        for match in re.finditer(r"AcdCollector\.(\w+)\s*\(", source):
            called.add(match.group(1))
        body = strip_comments(source)
        for handler in re.finditer(r"(?:private|public|protected)[^;{]*\bswerve\$acd\w*\s*\([^)]*\)\s*\{", body):
            start = body.index(handler.group(0)) + len(handler.group(0))
            depth, end = 1, start
            while end < len(body) and depth:
                if body[end] == "{":
                    depth += 1
                elif body[end] == "}":
                    depth -= 1
                end += 1
            inner = body[start:end]
            label = handler.group(0).split("swerve$")[1].split("(")[0]
            if "try" not in inner:
                fail(f"{name}: хендлер {label} не обёрнут в try/catch — исключение убьёт игру")
            if "catch" not in inner:
                fail(f"{name}: хендлер {label} не ловит исключения")
            if "Swerve.LOGGER" not in inner and "disableCollection" not in inner:
                fail(f"{name}: хендлер {label} не пишет ошибку в лог и не выключает сбор")
            if "disableCollection" in inner and "catch" not in inner:
                fail(f"{name}: хендлер {label} вызывает disableCollection вне catch")

    missing = sorted(set(packet_handlers) - called)
    if missing:
        fail(f"источники не подключены ни к одному миксину: {', '.join(missing)}")

    # арность вызовов из миксинов
    all_arity = collector_sources(collector)
    for name, source in sources.items():
        for call in re.finditer(r"AcdCollector\.(\w+)\s*\(", source):
            method = call.group(1)
            if method not in all_arity:
                continue
            start = call.end()
            depth, end, args = 1, start, ""
            while end < len(source) and depth:
                char = source[end]
                if char in "([{":
                    depth += 1
                elif char in ")]}":
                    depth -= 1
                    if depth == 0:
                        break
                args += char
                end += 1
            given = 0 if not args.strip() else len(split_args(args))
            if given != all_arity[method]:
                fail(f"{name}: вызов AcdCollector.{method} с {given} аргументами, объявлено {all_arity[method]}")


# --------------------------------------------------------------------------- #
# 6 — граница: только наблюдение
# --------------------------------------------------------------------------- #
FORBIDDEN = (
    ("ci.cancel()", "отмена обработчика: менять поведение серверных пакетов запрещено"),
    ("cancel()", "отмена события: модуль не имеет права вмешиваться"),
    ("@Redirect", "перехват метода меняет поведение клиента"),
    ("@ModifyArg", "подмена аргумента меняет поведение клиента"),
    ("@ModifyVariable", "изменение локальной переменной меняет поведение клиента"),
    ("@ModifyConstant", "подмена константы меняет поведение клиента"),
    ("connection.send(", "отправка пакета на сервер запрещена"),
    ("getSendPackets", "вмешательство в исходящие пакеты запрещено"),
    ("setClipboard(getClipboard", "автоподстановка из буфера обмена не нужна"),
)


def check_boundary(root: Path, files: dict[str, str]) -> None:
    for path, source in files.items():
        code = strip_comments(source)
        for needle, why in FORBIDDEN:
            if needle in code:
                fail(f"{path}: найдено «{needle}» — {why}")
        if "swerve.modules.impl." in code and path.startswith("acd/"):
            fail(f"{path}: пакет swerve.acd не должен зависеть от боевых модулей")
    # в миксинах ACD-хендлеры не должны трогать чужие модули
    for name, source in files.items():
        if "mixins/client" not in name:
            continue
        for block in re.findall(r"swerve\$acd\w*[\s\S]{0,400}?\n    \}", strip_comments(source)):
            if re.search(r"(KillAura|Speed|ElytraFlight|NoWeb|Velocity|Gui)\b", block):
                fail(f"{name}: ACD-хендлер ссылается на боевой модуль — это изменение его поведения")


# --------------------------------------------------------------------------- #
# 7, 14 — существование членов (замена компилятору для выдуманных API)
# --------------------------------------------------------------------------- #
MEMBER = re.compile(r"\b(Acd[A-Za-z]+)\.([a-zA-Z_][A-Za-z0-9_]*)\s*(\()?", )
DECL = re.compile(
    r"(?:public|protected|private|static|final|synchronized|abstract|\s)+"
    r"(?:<[^>]+>\s+)?[\w.<>\[\]?]+\s+(\w+)\s*\(([^)]*)\)\s*(?:throws[^{]*)?\{")
FIELD = re.compile(r"(?:public|private|protected)[^;(=]*?\b(\w+)\s*(?:=|;)")
NESTED = re.compile(r"(?:public|private|protected)?\s*(?:static\s+)?(?:final\s+)?(?:class|record|enum)\s+(\w+)")
ENUM_CONST = re.compile(r"^\s{4}([A-Z][A-Z0-9_]*)\s*(?:\(|,|;)", re.M)


def members_of(acd_dir: Path) -> dict[str, dict[str, set]]:
    out: dict[str, dict[str, set]] = {}
    for path in sorted(acd_dir.glob("Acd*.java")):
        source = read(path)
        code = strip_comments(source)
        methods: dict[str, set[int]] = {}
        for match in DECL.finditer(code):
            params = match.group(2).strip()
            declared = [] if not params else split_args(params)
            arity = len(declared)
            # varargs принимают «не меньше N», а не «ровно N»
            if declared and "..." in declared[-1]:
                arity = -arity
            methods.setdefault(match.group(1), set()).add(arity)
        fields = set(FIELD.findall(code)) | set(ENUM_CONST.findall(code))
        out[path.stem] = {"methods": methods, "fields": fields, "nested": set(NESTED.findall(code))}
    return out


# то, что есть у любого enum/Object и не объявлено в исходнике
IMPLICIT = {"values", "valueOf", "ordinal", "name", "toString", "equals", "hashCode", "getClass", "clone"}


def check_symbols(files: dict[str, str], index: dict[str, dict[str, set]]) -> None:
    for path, source in files.items():
        code = strip_strings(strip_comments(source))
        for match in MEMBER.finditer(code):
            owner, member, called = match.group(1), match.group(2), match.group(3)
            if owner not in index:
                continue
            entry = index[owner]
            methods, fields = entry["methods"], entry["fields"]
            if member in fields or member in entry["nested"] or member in IMPLICIT:
                continue
            if member in methods:
                if called:
                    start = match.end()
                    depth, end, args = 1, start, ""
                    while end < len(code) and depth:
                        char = code[end]
                        if char in "([{":
                            depth += 1
                        elif char in ")]}":
                            depth -= 1
                            if depth == 0:
                                break
                        args += char
                        end += 1
                    given = 0 if not args.strip() else len(split_args(args))
                    exact = {a for a in methods[member] if a >= 0}
                    varargs = {-a - 1 for a in methods[member] if a < 0}
                    if given in exact:
                        continue
                    if any(given >= floor for floor in varargs):
                        continue
                    fail(f"{path}: {owner}.{member} вызван с {given} аргументами, "
                         f"объявлены арности {sorted(exact)}"
                         + (f", varargs от {sorted(varargs)}" if varargs else ""))
                continue
            fail(f"{path}: у {owner} нет члена «{member}» (выдуманный API соберётся только с компилятором)")


# --------------------------------------------------------------------------- #
# 8, 9 — таблица правил и её поведение
# --------------------------------------------------------------------------- #
def string_arrays(source: str) -> dict[str, list[str]]:
    out: dict[str, list[str]] = {}
    for match in re.finditer(r"String\[\]\s+(\w+)\s*=\s*\{([^}]*)\}", source, flags=re.S):
        out[match.group(1)] = literals(match.group(2))
    return out


def parse_table(rules_src: str) -> list[dict]:
    arrays = string_arrays(rules_src)
    body = region(rules_src, "AcdRule[] TABLE")
    rules: list[dict] = []
    for match in re.finditer(r"AcdRule\.(of|excluding|conjunct)\s*\(", body):
        kind = match.group(1)
        start = match.end()
        depth, end = 1, start
        while end < len(body) and depth:
            char = body[end]
            if char in "([{":
                depth += 1
            elif char in ")]}":
                depth -= 1
                if depth == 0:
                    break
            end += 1
        args = split_args(body[start:end])
        if len(args) < 4:
            fail(f"AcdRules: у правила {kind} слишком мало аргументов ({len(args)})")
            continue
        marker_args = args[4:] if kind == "of" else args[4:6]
        lists: list[list[str]] = []
        for arg in marker_args:
            arg = arg.strip()
            named = re.fullmatch(r"([A-Z_][A-Z0-9_]*)", arg)
            if named and named.group(1) in arrays:
                lists.append(list(arrays[named.group(1)]))
            elif arg.startswith("new String"):
                lists.append(literals(region(arg, "", "{", "}")))
            else:
                lists.append(literals(arg))
        while len(lists) < 2:
            lists.append([])
        if kind == "of":
            any_of, all_of, none_of = [m for group in lists for m in group], [], []
        elif kind == "conjunct":
            any_of, all_of, none_of = [], lists[0], lists[1] if len(lists) > 1 else []
        else:
            any_of, all_of, none_of = lists[0], [], lists[1] if len(lists) > 1 else []
        rules.append({
            "id": literals(args[0])[0] if literals(args[0]) else "?",
            "kind": kind,
            "target": args[1].split(".")[-1],
            "severity": args[2].split(".")[-1],
            "reason": " ".join(literals(args[3])),
            "anyOf": any_of,
            "allOf": all_of if kind == "conjunct" else [],
            "noneOf": none_of,
        })
    return rules


def matches(text: str, rule: dict) -> bool:
    if not text:
        return False
    if any(marker not in text for marker in rule["allOf"]):
        return False
    if rule["anyOf"] and not any(marker in text for marker in rule["anyOf"]):
        return False
    return not any(marker in text for marker in rule["noneOf"])


def first_match(text: str, rules: list[dict]) -> dict | None:
    lowered = text.lower()
    if not lowered:
        return None
    for rule in rules:
        if matches(lowered, rule):
            return rule
    return None


# Ожидания совпадают с AcdHarness.classify(): менять их можно только вместе с документацией.
CORPUS: list[tuple[str, str | None]] = [
    ("Player was flagged for Speed by GrimAC", "ac:name"),
    ("[Grim] Vl: 12 (Reach) flag", "ac:name"),
    ("you have a violation of the rules", "flag:violation"),
    ("no violation was found", None),
    ("you did not violate anything", None),
    ("Player moved too quickly!", "move:speed"),
    ("Flying is not enabled here", "move:flight"),
    ("Matrix: flight detected", "move:flight"),
    ("You were teleported back by the server", "move:setback"),
    ("teleport potion effect expired", None),
    ("You are banned for cheating", "kick:ban"),
    ("banned items list updated", None),
    ("Timed out", "net:timeout"),
    ("connection lost", "net:timeout"),
    ("античит обнаружил подозрительное движение", "ac:name"),
    ("подозрительный разброс углов", "flag:violation"),
    ("hello everyone, how are you", None),
    ("flags of our nation are welcome", None),
    ("the matrix has you", None),
    ("speed limit near spawn", None),
    ("hawk spotted in the sky", None),
    ("please read the rules and be nice", None),
    ("You have been kicked", None),
    ("bad packets from your client", "packet:bad"),
    ("§a§lGrimAC§r flagged you for Reach", "ac:name"),
    ("KARHU flagged Rotation", "ac:name"),
    ("check moves flagged by verus", "ac:name"),
    ("неизвестное сообщение о погоде", None),
]


def check_rules(rules_src: str, rules: list[dict]) -> None:
    if not rules:
        fail("AcdRules: таблица TABLE не разобрана — проверка правил бессмысленна")
        return
    ids = [rule["id"] for rule in rules]
    if len(set(ids)) != len(ids):
        fail(f"AcdRules: одинаковые id правил: {ids}")
    for rule in rules:
        markers = rule["anyOf"] + rule["allOf"] + rule["noneOf"]
        if not rule["anyOf"] and not rule["allOf"]:
            fail(f"правило {rule['id']}: нет ни одного обязательного маркера")
        for marker in markers:
            if len(marker) < 3:
                fail(f"правило {rule['id']}: маркер «{marker}» короче трёх символов — шум")
            if marker != marker.lower():
                fail(f"правило {rule['id']}: маркер «{marker}» не в нижнем регистре — никогда не совпадёт")
            if "§" in marker:
                fail(f"правило {rule['id']}: маркер содержит код формата")
        if not rule["reason"].strip():
            fail(f"правило {rule['id']}: пустое объяснение — в отчёте не будет причины")
        elif matches(rule["reason"].lower(), rule):
            fail(f"правило {rule['id']}: формулировка причины содержит собственный маркер")
        if rule["severity"] not in {"INFO", "NOTICE", "WARNING", "CRITICAL"}:
            fail(f"правило {rule['id']}: неизвестная важность {rule['severity']}")
    if "String[]" not in rules_src:
        fail("AcdRules: маркеры не найдены в исходнике")
    for earlier_index, earlier in enumerate(rules):
        for later_index in range(earlier_index + 1, len(rules)):
            later = rules[later_index]
            if earlier["noneOf"] or earlier["allOf"]:
                continue
            for candidate in later["anyOf"]:
                for earlier_marker in earlier["anyOf"]:
                    if earlier_marker in candidate:
                        fail(f"правило {later['id']}: маркер «{candidate}» бессмыслен — текст, где он есть, "
                             f"раньше подхватит {earlier['id']} по «{earlier_marker}»")
            for candidate in later["allOf"]:
                for earlier_marker in earlier["anyOf"]:
                    if earlier_marker in candidate:
                        fail(f"правило {later['id']}: «{candidate}» никогда не решает — "
                             f"{earlier['id']} срабатывает раньше по «{earlier_marker}»")


def check_corpus(rules: list[dict]) -> None:
    if not rules:
        return
    for text, expected in CORPUS:
        got = first_match(text, rules)
        got_id = got["id"] if got else None
        if got_id != expected:
            fail(f"корпус: «{text}» → {got_id}, ожидалось {expected}")


# --------------------------------------------------------------------------- #
# 10 — границы буфера и лимиты
# --------------------------------------------------------------------------- #
def const_ints(source: str) -> dict[str, int]:
    out: dict[str, int] = {}
    for match in re.finditer(r"(?:int|long)\s+(\w+)\s*=\s*(-?[\d_]+)(?:L|l)?\s*;", source):
        out[match.group(1)] = int(match.group(2).replace("_", ""))
    return out


def check_limits(root: Path, store_src: str, module_src: str, acd_dir: Path) -> None:
    store = const_ints(store_src)
    minimal, maximal, default = store.get("MIN_CAPACITY"), store.get("MAX_CAPACITY"), store.get("DEFAULT_CAPACITY")
    if None in (minimal, maximal, default):
        fail("AcdStore: границы ёмкости не найдены")
    else:
        if not 0 < minimal <= default <= maximal:
            fail(f"AcdStore: границы ёмкости рассогласованы: {minimal}/{default}/{maximal}")
    for name in ("MAX_RAW_CHARS", "MAX_MESSAGE_CHARS"):
        value = const_ints(read(acd_dir / "AcdEvent.java")).get(name)
        if not value or value < 64:
            fail(f"AcdEvent.{name} = {value}: слишком мало для нормального сообщения")
    scan = const_ints(read(acd_dir / "AcdClassifier.java"))
    if not scan.get("MAX_SNIPPET_CHARS", 0) <= const_ints(read(acd_dir / "AcdEvent.java")).get("MAX_MESSAGE_CHARS", 0) \
            + const_ints(read(acd_dir / "AcdClassifier.java")).get("MAX_SNIPPET_CHARS", 0):
        fail("AcdClassifier: длинна фрагмента не согласована с ограничением описания")
    exporter = const_ints(read(acd_dir / "AcdExporter.java"))
    for name in ("MAX_CLIPBOARD_CHARS", "MAX_CHAT_LINE_CHARS", "MAX_CHAT_MESSAGES"):
        if exporter.get(name, 0) <= 0:
            fail(f"AcdExporter.{name} обязан быть положительным")
    if exporter.get("MAX_CHAT_LINE_CHARS", 0) < 16:
        fail("AcdExporter.MAX_CHAT_LINE_CHARS меньше гарда paginate (16): вывод в чат молча ничего не покажет")
    for name in ("MIN_CHAT_LINES", "MAX_CHAT_LINES", "DEFAULT_CHAT_LINES"):
        if name not in exporter:
            fail(f"AcdExporter.{name} не найден")
    if not exporter.get("MIN_CHAT_LINES", 1) <= exporter.get("DEFAULT_CHAT_LINES", 0) <= exporter.get("MAX_CHAT_LINES", 0):
        fail("AcdExporter: лимиты строк чата рассогласованы")

    # настройка модуля обязана совпадать с ограничителями хранилища
    setting = re.search(r'FloatSetting\("Размер истории",\s*([\d_]+)F,\s*([^,]+),\s*([^,]+),', module_src)
    if not setting:
        fail("модуль: настройка «Размер истории» не найдена")
    else:
        default_value = int(setting.group(1).replace("_", ""))
        if "AcdStore.MIN_CAPACITY" not in setting.group(2) or "AcdStore.MAX_CAPACITY" not in setting.group(3):
            fail("модуль: границы «Размер истории» заданы числами, а не константами AcdStore — они разъедутся")
        if default_value != default:
            fail(f"модуль: значение по умолчанию {default_value} не равно AcdStore.DEFAULT_CAPACITY {default}")
    if "AcdStore.DEFAULT_CAPACITY" in module_src and default is None:
        fail("AcdStore.DEFAULT_CAPACITY не найден, хотя модуль на него ссылается")


# --------------------------------------------------------------------------- #
# 11, 12 — настройки и флаги
# --------------------------------------------------------------------------- #
def check_settings_wiring(root: Path, module_src: str, acd_files: dict[str, str]) -> None:
    fields = re.findall(r"private final (?:Boolean|Float|Mode|List|Action|Text)Setting (\w+)", module_src)
    if not fields:
        fail("модуль: настройки не найдены")
        return
    add_start = module_src.find("addSettings(")
    add_block = balanced(module_src, add_start + len("addSettings(")) if add_start >= 0 else ""
    kinds = {name: kind for kind, name in re.findall(r"private final (\w+Setting) (\w+)", module_src)}
    listed = set(part.strip().split(".")[-1] for part in split_args(add_block))
    for match in re.finditer(r"new ListSetting\(", module_src):
        listed.update(re.findall(r"this\.(\w+)", balanced(module_src, match.end())))
    for field in fields:
        if field not in listed:
            fail(f"модуль: настройка {field} не добавлена в addSettings (не попадёт ни в GUI, ни в конфиг)")
        if kinds.get(field) == "ActionSetting":
            # кнопка «читается» кликом: достаточно, что к ней привязано существующее действие
            hook = re.search(r"ActionSetting[^;]*\b" + field + r"\b[^;]*?\.action\((?:this::|\w+::)?(\w+)\)",
                             module_src, flags=re.S)
            if not hook:
                fail(f"модуль: у кнопки {field} нет .action(...) — кликабельная строка мертва")
            elif not re.search(r"(?:private|public|static)[^;{]*\b" + hook.group(1) + r"\s*\(", module_src):
                fail(f"модуль: кнопка {field} ссылается на метод {hook.group(1)}, которого нет")
            continue
        if kinds.get(field) == "ListSetting":
            # контейнер: значимы его элементы, а не сам список
            decl = re.search(r"ListSetting " + field + r"\s*=\s*new ListSetting\(", module_src)
            if not decl:
                fail(f"модуль: объявление списка {field} не найдено — проверка элементов невозможна")
                continue
            refs = re.findall(r"this\.(\w+)", balanced(module_src, decl.end()))
            if not refs:
                fail(f"модуль: список {field} не ссылается ни на одну настройку")
            for ref in refs:
                if f"this.{ref}.get()" not in module_src and f"this.{ref}[" not in module_src:
                    fail(f"модуль: элемент {ref} списка {field} нигде не читается — настройка мертва")
            continue
        usage = re.compile(r"this\." + field + r"\.(get|getCurrent|getIndex|isState|getSettings|label)\b")
        if not usage.search(module_src):
            fail(f"модуль: настройка {field} нигде не читается — декоративная")
    names = re.findall(r'(?:Boolean|Float|Mode|List|Action|Text)Setting\("([^"]+)"', module_src)
    if len(set(names)) != len(names):
        duplicate = [n for n in names if names.count(n) > 1]
        fail(f"модуль: одинаковые имена настроек {set(duplicate)} — в конфиге они перезапишут друг друга")
    for name in names:
        if not re.search(r"[А-Яа-я]", name):
            fail(f"модуль: имя настройки «{name}» не по-русски (домашнее правило)")
    if "visible(" in module_src:
        pass  # явная привязка к режиму не обязательна, если режимов-переключателей нет

    # каждый выставленный политики-сеттер должен кем-то читаться
    store_src = acd_files["AcdStore.java"]
    collector_src = acd_files["AcdCollector.java"]
    # тела собственных геттеров не считаются: «return поле;» — это не использование политики
    code = strip_method_bodies(strip_comments(store_src), ("announces", "scansChat", "severityFloor",
                                                            "isCollecting", "stopHook")) \
        + strip_comments(acd_files.get("AcdCollector.java", ""))
    for setter in set(re.findall(r"AcdStore\.(set\w+)\(", module_src)):
        name = setter[3:]
        candidates = [name, name[0].lower() + name[1:], f"is{name[0].upper()}{name[1:]}",
                      name[0].lower() + name[1:] + "s"]
        seen = 0
        for candidate in candidates:
            # объявление поля и тело самого сеттера чтением не считаются
            for match in re.finditer(r"\b" + re.escape(candidate) + r"\b", code):
                line_start = code.rfind("\n", 0, match.start()) + 1
                line = code[line_start:code.find("\n", match.start())]
                if re.match(r"\s*(?:private|public|protected|static|volatile)[\w\s<>,]*\b"
                            + re.escape(candidate) + r"\s*(?:\(\s*\))?\s*[=;{]", line):
                    continue  # объявление поля или геттера — не чтение
                if "void " + setter in code[max(0, line_start - 200):line_start]:
                    continue
                seen += 1
        if seen < 1:
            fail(f"модуль вызывает AcdStore.{setter}, но это значение нигде не читают — настройка мертва")

    formatter = acd_files["AcdFormatter.java"]
    for needle, why in (('heuristicCount()', 'сводка по эвристикам'),
                        ('Внимание', 'предупреждение о том, что часть записей — догадка'),
                        ('записей нет', 'объяснение пустого отчёта')):
        if needle not in formatter:
            fail(f"AcdFormatter: в текстовом отчёте пропало {why} («{needle}»)")
    options = re.search(r"record Options\(([^)]*)\)", formatter)
    if not options:
        fail("AcdFormatter: Options не найден")
        return
    for flag in re.findall(r"boolean (\w+)", options.group(1)):
        if formatter.count(f"{flag}()") < 2:
            fail(f"AcdFormatter.Options.{flag} применяется меньше двух раз — флаг не работает в одном из форматов")


# --------------------------------------------------------------------------- #
# 13 — GUI и доступность экспорта при выключенном модуле
# --------------------------------------------------------------------------- #
def check_gui(root: Path) -> None:
    screen = read(root / "src/main/java/swerve/ui/menu/MenuScreen.java")
    code = strip_comments(screen)
    if "ACT_ACTION = " not in code:
        fail("MenuScreen: константа ACT_ACTION не объявлена")
    if code.count("ACT_ACTION") < 3:
        fail("MenuScreen: ACT_ACTION объявлена, но не обрабатывается (кнопка мертва)")
    row_body = region(code, "private void row(GuiGraphics g, Module m, Setting s,")
    branch = re.search(r"instanceof ActionSetting (\w+)\)\s*\{", row_body)
    if not branch:
        fail("MenuScreen: row() не рисует ActionSetting")
    else:
        variable = branch.group(1)
        tail = row_body[branch.end():]
        if f"{variable}." not in tail:
            fail("MenuScreen: настройка кнопки в row() получена и не использована")
        if "action.label()" not in row_body and f"{variable}.label()" not in tail:
            fail("MenuScreen: row() не печатает подпись кнопки")
    action_row = row_body.find("instanceof ActionSetting")
    text_row = row_body.find("instanceof TextSetting")
    if action_row < 0 or text_row < 0 or action_row > text_row:
        fail("MenuScreen: ветка ActionSetting должна стоять до общего фолбэка TextSetting")
    if ".trigger()" not in code:
        fail("MenuScreen: клик по ACT_ACTION не вызывает trigger()")
    popup = region(code, "private void openPopup(Module m) {")
    if "isEnable()" in popup:
        fail("MenuScreen: openPopup зависит от isEnable() — история станет недоступна после выключения")
    open_case = code[code.find("case ACT_MODULE_OPEN"):code.find("case ACT_MODULE_OPEN") + 400]
    if "isEnable()" in open_case:
        fail("MenuScreen: открытие окна настроек зависит от isEnable() — сценарий «выключил и забрал логи» сломан")
    action_case = code[code.find("case ACT_ACTION"):code.find("case ACT_ACTION") + 400]
    if not action_case:
        fail("MenuScreen: случай ACT_ACTION в runAction не найден")
    if "trigger()" not in action_case:
        fail("MenuScreen: ACT_ACTION не дёргает настройку")


# --------------------------------------------------------------------------- #
# 14 — исполняемый харнесс
# --------------------------------------------------------------------------- #
def check_harness(root: Path, index: dict[str, dict[str, set]]) -> None:
    path = root / "tests/AcdHarness.java"
    if not path.is_file():
        fail("tests/AcdHarness.java отсутствует: логика хранилища не исполняется нигде")
        return
    source = read(path)
    if not source.startswith("package swerve.acd;"):
        fail("AcdHarness обязан лежать в пакете swerve.acd — иначе не увидит package-private API")
    checks = len(re.findall(r"\bcheck\(", source)) - 1  # минус само объявление хелпера
    if checks < 150:
        fail(f"AcdHarness: всего {checks} проверок, ожидалось не меньше 150")
    print(f"справка: проверок в харнессе — {checks}")
    if "НЕ ПРОВЕРЕНО" not in source:
        fail("AcdHarness: в шапке не перечислено, что осталось непроверенным")
    if "System.exit(1)" not in source:
        fail("AcdHarness: при провале нет ненулевого кода возврата — прогон можно было бы проигнорировать")
    if source.count("{") - source.count("}") not in (-1, 0, 1):
        fail("AcdHarness: фигурные скобки не сбалансированы")
    if source.count("(") - source.count(")") not in (-1, 0, 1):
        fail("AcdHarness: круглые скобки не сбалансированы")
    if "*/" in source.split("package swerve.acd;")[1].split("public final class")[0].replace(
            "/**", "").replace("*/", ""):
        fail("AcdHarness: в javadoc попал «*/» — блок комментария оборвётся посередине")
    check_symbols({"tests/AcdHarness.java": source}, index)
    try:
        from tree_sitter import Language, Parser  # noqa: PLC0415
        import tree_sitter_java as tsj  # noqa: PLC0415
    except ImportError:
        print("справка: tree-sitter недоступен — разбор харнесса пропущен")
        return
    parser = Parser(Language(tsj.language()))
    tree = parser.parse(source.encode("utf-8"))

    def walk(node):
        if node.type == "ERROR" or node.is_missing:
            yield node.start_point[0] + 1
        for child in node.children:
            yield from walk(child)

    errors = list(walk(tree.root_node))
    if errors:
        fail(f"AcdHarness: разбор сломался на строках {sorted(set(errors))[:5]} — компилятор увидит то же")


# --------------------------------------------------------------------------- #
# 15, 16 — тексты и документация
# --------------------------------------------------------------------------- #
ALIEN = re.compile(r"[\u3000-\u9fff\uf900-\ufaff\uac00-\ud7af\u0600-\u06ff]")
GLUE = re.compile(r"[а-яА-Я]{2,}[A-Za-z]{3,}|[A-Za-z]{3,}[а-яА-Я]{2,}")


def check_text_hygiene(files: dict[str, str]) -> None:
    for path, source in files.items():
        for number, line in enumerate(source.splitlines(), 1):
            if ALIEN.search(line):
                fail(f"{path}:{number}: чужая письменность в строке {line.strip()[:60]!r}")
            for line_clean in [line]:
                if GLUE.search(line_clean):
                    fail(f"{path}:{number}: склейка латиницы и кириллицы: {line_clean.strip()[:60]!r}")


def check_docs(root: Path) -> None:
    doc = root / "docs/AntiCheatDetector.md"
    if not doc.is_file():
        fail("docs/AntiCheatDetector.md отсутствует")
        return
    text = read(doc)
    if not re.search(r"^#{1,3}\s*НЕ ПРОВЕРЕНО\s*$", text, flags=re.M):
        fail("docs/AntiCheatDetector.md: нет заголовка «НЕ ПРОВЕРЕНО» (анкор по заголовку, а не по слову в тексте)")
    for needle in ("античит", "GrimAC", "Copy All Logs"):
        if needle.lower() not in text.lower():
            fail(f"docs/AntiCheatDetector.md: нет обязательного упоминания «{needle}»")
    if "не реализован" not in text:
        fail("docs/AntiCheatDetector.md: вывод «чего модуль не делает» не сформулирован явно")
    if not re.search(r"^#{1,3}\s*.*границ", text, flags=re.I | re.M):
        fail("docs/AntiCheatDetector.md: нет раздела о границе применимости (§5)")
    if "Copy All Logs" not in text:
        fail("docs/AntiCheatDetector.md: не описан обязательный сценарий с Copy All Logs")
    if len(text.splitlines()) < 60:
        fail("docs/AntiCheatDetector.md: документ тоньше, чем требует домашний формат")


# --------------------------------------------------------------------------- #
# 17 — паттерн-переменная не имеет права перекрывать параметр метода
# --------------------------------------------------------------------------- #
METHOD_HEAD = re.compile(r"(?:private|public|protected|static|final|synchronized|abstract|default)"
                         r"[\w\s<>,\[\].?]*?\b(\w+)\s*\(([^)]*)\)\s*(?:throws[\w., ]*)?\{")
PATTERN_VAR = re.compile(r"instanceof\s+[A-Za-z_][\w.<>\[\]]*\s+(\w+)")


def method_block(text: str, open_at: int) -> str:
    """Тело метода: от `{` с индексом open_at до парной `}`."""
    depth, index = 1, open_at + 1
    while index < len(text) and depth:
        if text[index] == "{":
            depth += 1
        elif text[index] == "}":
            depth -= 1
        index += 1
    return text[open_at + 1:index - 1]


def check_name_collisions(files: dict[str, str]) -> None:
    for path, source in files.items():
        code = strip_comments(source)
        for match in METHOD_HEAD.finditer(code):
            params = set()
            for arg in match.group(2).split(","):
                parts = arg.strip().rsplit(None, 1)
                if len(parts) == 2 and re.fullmatch(r"\w+", parts[1]):
                    params.add(parts[1])
            if not params:
                continue
            body = method_block(code, match.end() - 1)
            for var in PATTERN_VAR.finditer(body):
                if var.group(1) in params:
                    fail(f"{path}: {match.group(1)}() — pattern-переменная «{var.group(1)}» совпадает "
                         f"с именем параметра: javac ответит «variable is already defined»")


# --------------------------------------------------------------------------- #
# 18 — служебные отметки сборки: категория, источник и оговорка
# --------------------------------------------------------------------------- #
def check_lifecycle_markers(collector_src: str) -> None:
    """`noteCollectionStarted/Stopped` — про сам детектор, а не про сеть.

    Первый прогон на живом сервере дал `NETWORK_DIAGNOSTIC=6` из шести записей, и все
    шесть — «включён сбор» / «сбор выключен». Счётчик категории врал: ни одного
    сетевого события в нём не было. Правило держит это на месте: категория —
    нейтральная корзина, источник — именованная константа (а не строка, которую
    можно напечатать в одном месте и рассогласовать в другом), и к каждой отметке
    приложено объяснение её природы.
    """
    code = strip_comments(collector_src)
    methods = re.findall(r"public static void (noteCollection\w+)\(", code)
    if len(methods) < 2:
        fail("AcdCollector: потеряна хотя бы одна служебная отметка сборки "
             f"(найдено {len(methods)}: {methods}) — по ним видно, какой промежуток вообще наблюдался")
        return
    for name in methods:
        signature = f"public static void {name}("
        body = region(code, signature)
        if not body:
            fail(f"AcdCollector.{name}: не удалось разобрать тело")
            continue
        if "AcdCategory.NETWORK_DIAGNOSTIC" in body:
            fail(f"AcdCollector.{name}: отметка о собственной сборке записана в категорию "
                 "«Сеть» — счётчик NETWORK_DIAGNOSTIC начнёт врать о соединениях")
        if "AcdCategory.UNKNOWN_EVENT" not in body:
            fail(f"AcdCollector.{name}: отметка не в нейтральной категории; ни одна из девяти "
                 "категорий не описывает «модуль включился», выдумывать десятую нельзя")
        if "SRC_SELF" not in body:
            fail(f"AcdCollector.{name}: источник отметки задан строкой вместо SRC_SELF — "
                 "в отчёте такие записи перестанут собираться в один источник")
        if "LIFECYCLE_NOTE" not in body:
            fail(f"AcdCollector.{name}: к отметке не приложено примечание LIFECYCLE_NOTE — "
                 "читатель «Unknown» будет гадать, что это за событие")
    # источник должен быть назван тем же именем, что фигурирует в отчёте
    if 'String SRC_SELF = "AntiCheatDetector"' not in collector_src:
        fail("AcdCollector: SRC_SELF должен равняться «AntiCheatDetector» — по этой строке "
             "записи ищут в выгрузке и в GUI")


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--root", default=None, help="корень репозитория (по умолчанию — рядом с тестом)")
    args = parser.parse_args()
    root = Path(args.root).resolve() if args.root else Path(__file__).resolve().parents[1]
    acd_dir = root / "src/main/java/swerve/acd"
    if not acd_dir.is_dir():
        print(f"не найден {acd_dir}")
        return 2
    module_path = root / "src/main/java/swerve/modules/impl/misc/AntiCheatDetector.java"
    collector_path = acd_dir / "AcdCollector.java"
    if not module_path.is_file() or not collector_path.is_file():
        print("не найдены модуль или AcdCollector")
        return 2

    module_src = read(module_path)
    collector_src = read(collector_path)
    acd_files = {path.name: read(path) for path in sorted(acd_dir.glob("*.java"))}
    mixins = {
        f"mixins/client/{path.name}": read(path)
        for path in sorted((root / "src/main/java/swerve/mixins/client").glob("Mixin*.java"))
        if "Acd" in read(path)
    }
    index = members_of(acd_dir)

    check_registration(root, module_src)
    check_mixins(root, collector_src)
    acd_by_path = {f"acd/{name}": text for name, text in acd_files.items()}
    checked_files = {**acd_by_path, "modules/impl/misc/AntiCheatDetector.java": module_src, **mixins}
    check_boundary(root, checked_files)
    check_symbols(checked_files, index)
    rules = parse_table(acd_files["AcdRules.java"])
    check_rules(acd_files["AcdRules.java"], rules)
    check_corpus(rules)
    check_limits(root, acd_files["AcdStore.java"], module_src, acd_dir)
    check_settings_wiring(root, module_src, acd_files)
    check_gui(root)
    check_harness(root, index)
    check_lifecycle_markers(collector_src)
    check_name_collisions({**acd_by_path, "modules/impl/misc/AntiCheatDetector.java": module_src,
                           "ui/menu/MenuScreen.java": read(root / "src/main/java/swerve/ui/menu/MenuScreen.java"),
                           "mixins/client/MixinClientPacketListener.java": read(
                               root / "src/main/java/swerve/mixins/client/MixinClientPacketListener.java"),
                           "mixins/client/MixinClientCommonPacketListenerImpl.java": read(
                               root / "src/main/java/swerve/mixins/client/MixinClientCommonPacketListenerImpl.java")})
    hygiene = {**acd_files, "modules/impl/misc/AntiCheatDetector.java": module_src,
               "tests/AcdHarness.java": read(root / "tests/AcdHarness.java")}
    if (root / "docs/AntiCheatDetector.md").is_file():
        hygiene["docs/AntiCheatDetector.md"] = read(root / "docs/AntiCheatDetector.md")
    check_text_hygiene(hygiene)
    check_docs(root)

    print(f"правил в таблице: {len(rules)}, корпус: {len(CORPUS)} случаев, "
          f"файлов ACD: {len(acd_files)}, миксинов с ACD: {len(mixins)}")
    for finding in findings:
        print("FAIL:", finding)
    print(f"найдено: {len(findings)}")
    return 1 if findings else 0


if __name__ == "__main__":
    sys.exit(main())
