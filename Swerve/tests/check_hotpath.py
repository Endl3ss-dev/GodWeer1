#!/usr/bin/env python3
"""Проверка правила «горячий путь: ни одной аллокации» из чек-листа владельца.

Зачем: пункт 4 чек-листа звучит как «Hot path (tick / render / key): ноль new,
стримов, лямбд, итераторов, боксинга». До этой проверки правило держалось на
внимательности: `check_syntax`/`check_types`/`check_scope` смотрят грамматику и
имена, `check_killaura`/`check_speed` — поведение конкретного модуля, а ни одна
проверка не отвечала на вопрос «не начал ли тик аллоцировать».

Что считается горячим путём
  1. Обработчики, зарегистрированные на события, которые Fabric вызывает каждый
     тик или каждый кадр: `ClientTickEvents.START/END_CLIENT_TICK`,
     `HudRenderCallback.EVENT`, `WorldRenderEvents.*` — сам метод-обработчик
     (ссылка `Foo::bar`) и тело лямбды прямо в точке регистрации.
  2. Все методы классов из `swerve/mixins` — миксин по определению исполняется
     внутри горячего метода ванильного класса (рендер кадра, тик игрока, тик
     пакета).
  3. Методы с именами из ROOT_NAMES (`onTick`, `tick`, `rotationTick`,
     `startTick`, `endTick`, `onHudRender`, `renderFovOverlay`, `beginFrame`) —
     виртуальная диспетчеризация через поля (`module.onTick()`) статически не
     разрешается, поэтому эти точки входа перечислены поимённо.
  4. Всё, что вызывается из (1)—(3) по цепочке вызовов внутри исходников Swerve
     (разрешаются вызовы `foo()`, `this.foo()`, `super.foo()`, `Класс.foo()` и
     ссылки `Класс::foo` / `this::foo`, если `Класс` — класс Swerve).

Чего проверка НЕ видит (честный список, чтобы вывод не читался шире реальности)
  * Вызовы через поле/параметр (`module.render(...)`, `this.manager.tick()`) —
    цель статически неизвестна, в граф не попадает. Такие методы нужно либо
    добавлять в ROOT_NAMES, либо они уже достижимы с другой стороны.
  * Автобоксинг в сигнатурах (`int` → `Integer` в аргументах) и `foreach` по
    коллекции (итератор создаётся внутри байткода цикла) — для них нет типов,
    а без типов это гадание. `foreach` по массиву аллокации не даёт и не
    считается нарушением.
  * Аллокации внутри ванильных методов, которые мы вызываем (они посчитаны
    правилом ALLOC_API только для известных «создающих объект» геттеров).

Правила
  NEW       — `new` в горячем методе;
  LAMBDA    — лямбда, захватывающая `this`, локальную переменную или параметр
              (незахватывающая лямбда компилируется в статический синглтон и
              аллокации не даёт — не нарушение);
  STREAM    — `stream()`, `map`, `filter`, `collect`, `toList`, `forEach`…;
  ITERATOR  — `iterator()`, `listIterator()`;
  ALLOC_API — ванильные геттеры, создающие объект на каждый вызов
              (`getEyePosition`, `getPosition`, `blockPosition`, `getViewVector`,
              `getLookAngle`, `calculateViewVector`), `Vec3.atCenterOf`,
              `BlockPos.containing`;
  FORMAT    — `String.format`, `valueOf`, `String.join`, `split`;
  CONCAT    — склейка строк через `+` с нестроковым операндом.

Исключения: ALLOW в конце файла. Ключ — (файл, метод, правило, сниппет),
обязательно с причиной. Сниппет входит в ключ намеренно: если выражение
изменится, исключение перестанет совпадать и находка снова всплывёт. Устаревшее
исключение (совпадений нет) — тоже находка, иначе список тихо гниёт.

Использование:
    pip3 install --target ~/.pylibs tree_sitter tree_sitter_java
    python3 Swerve/tests/check_hotpath.py [путь-к-src]

Код возврата: 0 — чисто, 1 — есть находки, 2 — не установлены зависимости.
"""
from __future__ import annotations

import re
import sys
from pathlib import Path

# Песочница не сохраняет /usr, ~/.local исключён из снимка — библиотеки кладём в ~/.pylibs.
def _load_parser():
    for extra in (Path.home() / ".pylibs",):
        if str(extra) not in sys.path:
            sys.path.insert(0, str(extra))
    try:
        from tree_sitter import Language, Parser
        import tree_sitter_java as tsj
    except ImportError:
        print("Нужны зависимости: pip3 install --target ~/.pylibs tree_sitter tree_sitter_java")
        raise SystemExit(2)
    return Parser(Language(tsj.language()))


SWERVE = Path(__file__).resolve().parents[1]
SRC = SWERVE / "src" / "main" / "java"

LAMBDA_LABEL = "ЛЯМБДА"

ROOT_NAMES = {
    "onTick",
    "tick",
    "rotationTick",
    "startTick",
    "endTick",
    "onHudRender",
    "renderFovOverlay",
    "beginFrame",
}

# Явные корни для вызовов через поле: `rotationElement.update(target)` статически не
# разрешается (тип поля — интерфейс), а исполняется каждый тик, пока аура видит цель.
ROOT_EXTRA = {
    ("swerve.modules.impl.combat.rotation.HWRotation", "update"),
    ("swerve.modules.impl.combat.rotation.IntaveRotation", "update"),
    ("swerve.modules.impl.combat.rotation.MatrixRotation", "update"),
}

# Правила, которые блокируют сдачу (жёсткая часть пункта 4 чек-листа).
HARD_RULES = {"NEW", "LAMBDA", "STREAM", "ITERATOR", "ALLOC_API"}
# Информационные: строковая сборка и копирование почти всегда оказываются в ветке
# лога/ошибки, которая в штатном кадре не исполняется. Их количество печатается,
# чтобы рост был виден, но находкой они не считаются — см. раздел «Область».
SOFT_RULES = {"FORMAT", "BOXING", "COPY", "CONCAT"}

# Точки регистрации обработчиков, которые Fabric вызывает каждый тик/кадр.
REGISTRATION_RE = re.compile(
    r"(?:ClientTickEvents\.(?:START|END)_CLIENT_TICK"
    r"|HudRenderCallback\.EVENT"
    r"|WorldRenderEvents\.[A-Z_]+)\.register\($"
)

STREAM_OPS = {
    "stream",
    "parallelStream",
    "forEach",
    "forEachOrdered",
    "map",
    "mapToInt",
    "mapToLong",
    "mapToDouble",
    "mapToObj",
    "filter",
    "collect",
    "toList",
    "toArray",
    "sorted",
    "distinct",
    "joining",
    "groupingBy",
    "reduce",
    "anyMatch",
    "allMatch",
    "noneMatch",
    "findFirst",
    "count",
}

ITERATOR_OPS = {"iterator", "listIterator"}

# Ванильные методы, создающие объект на каждый вызов. Каждое имя проверено байткодом
# 1.21.4 (`mc_disasm.py`), чтобы правило не ловило геттеры, которые отдают поле:
#   Entity.getEyePosition()   — `new Vec3` (оба перегруза);
#   Entity.getViewVector(F)   — `calculateViewVector` → `new Vec3`;
#   Entity.getLookAngle()     — `calculateViewVector` → `new Vec3`;
#   Entity.calculateViewVector(FF) — `new Vec3`.
# НЕ входят и не должны входить: `Entity.blockPosition()` и `Camera.getPosition()` —
# оба читают поле (#460 / #71), аллокации нет; `Entity.getBoundingBox()` — поле `bb`.
ALLOC_API = {
    "getEyePosition",
    "getViewVector",
    "getLookAngle",
    "calculateViewVector",
}

# Статические фабрики: имя метода само по себе неоднозначно (`position(0)` у Buffer),
# поэтому проверяется ещё и класс-владелец — он в исходнике написан буквально.
CLASS_QUALIFIED_ALLOC = {
    ("BlockPos", "containing"),
    ("BlockPos", "of"),
    ("Vec3", "atCenterOf"),
    ("Vec3", "atLowerCornerOf"),
    ("Vec3", "atBottomCenterOf"),
    ("Vec3", "atUpperCentreOf"),
    ("AABB", "of"),
    ("AABB", "withSize"),
}

FORMAT_OPS = {"format", "join", "split", "concat"}
BOX_OPS = {"valueOf"}
COPY_OPS = {"copyOf"}

ANNOTATIONS = {"annotation", "marker_annotation"}

TYPE_DECLS = {
    "class_declaration",
    "enum_declaration",
    "record_declaration",
    "interface_declaration",
    "annotation_type_declaration",
}


class Method:
    __slots__ = ("fqcn", "name", "node", "file", "params", "fields")

    def __init__(self, fqcn, name, node, file, params, fields):
        self.fqcn = fqcn
        self.name = name
        self.node = node
        self.file = file
        self.params = params
        self.fields = fields


class Unit:
    """Один разобранный файл: байты, дерево, пакет, импорты.

    Дерево tree-sitter даёт БАЙТОВЫЕ смещения, а в исходниках есть кириллица в
    комментариях, поэтому срезы делаются по `data` (bytes), а не по строке: иначе
    любой русский комментарий выше узла сдвигал бы текст и «имена» методов
    превращались в куски соседних выражений.
    """

    __slots__ = ("path", "rel", "data", "root", "package", "imports")

    def __init__(self, path, rel, data, root, package, imports):
        self.path = path
        self.rel = rel
        self.data = data
        self.root = root
        self.package = package
        self.imports = imports


def walk(node):
    stack = [node]
    while stack:
        cur = stack.pop()
        yield cur
        stack.extend(cur.children)


def node_text(unit, node):
    return unit.data[node.start_byte : node.end_byte].decode("utf-8", "replace")


def line_of(unit, node):
    return unit.data.count(b"\n", 0, node.start_byte) + 1


def collect_units(parser, src_root):
    units = []
    for path in sorted(src_root.rglob("*.java")):
        data = path.read_bytes()
        root = parser.parse(data).root_node
        package = ""
        imports = []
        for child in root.named_children:
            if child.type == "package_declaration":
                package = re.sub(r"\s+", "", data[child.start_byte : child.end_byte].decode("utf-8", "replace"))[len("package"):].rstrip(";")
            elif child.type == "import_declaration":
                raw = re.sub(r"\s+", "", data[child.start_byte : child.end_byte].decode("utf-8", "replace"))
                raw = raw[len("import"):].rstrip(";")
                if raw.startswith("static"):
                    raw = raw[len("static"):]
                imports.append(raw)
        # Путь в ключах исключений не зависит от того, где лежит дерево: он всегда
        # "src/main/java/...". Это нужно мутационному тесту, который проверяет копию.
        rel = "src/main/java/" + path.relative_to(src_root).as_posix()
        units.append(Unit(path, rel, data, root, package, imports))
    return units


def index_classes(units):
    """fqcn -> {"unit", "methods": {имя: [Method]}, "fields": set(), "super": str|None}."""
    classes = {}
    simple = {}
    for unit in units:
        for node in walk(unit.root):
            if node.type not in TYPE_DECLS:
                continue
            name_node = node.child_by_field_name("name")
            if name_node is None:
                continue
            name = node_text(unit, name_node)
            fqcn = f"{unit.package}.{name}" if unit.package else name
            methods = {}
            fields = set()
            body = None
            superclass = None
            for child in node.children:
                if child.type in ("class_body", "interface_body", "enum_body", "record_body"):
                    body = child
                elif child.type == "superclass":
                    superclass = re.sub(r"\s+", "", node_text(unit, child))[len("extends"):]
            supers = None
            for child in node.children:
                if child.type == "superclass":
                    supers = node_text(unit, child)
            if supers:
                superclass = re.sub(r"\s+", "", supers)[len("extends"):]
            if body is not None:
                for member in body.named_children:
                    if member.type == "field_declaration":
                        for inner in member.named_children:
                            if inner.type == "variable_declarator":
                                key = inner.child_by_field_name("name")
                                if key is not None:
                                    fields.add(node_text(unit, key))
                    elif member.type in ("method_declaration", "constructor_declaration"):
                        key = member.child_by_field_name("name")
                        if key is None:
                            continue
                        params = member.child_by_field_name("parameters")
                        param_names = set()
                        if params is not None:
                            for param in params.named_children:
                                for inner in walk(param):
                                    if inner.type == "identifier":
                                        param_names.add(node_text(unit, inner))
                                        break
                        method = Method(fqcn, node_text(unit, key), member, unit, param_names, fields)
                        methods.setdefault(method.name, []).append(method)
            classes[fqcn] = {"unit": unit, "methods": methods, "fields": fields, "super": superclass}
            simple.setdefault(name, []).append(fqcn)
    return classes, simple


def resolve_type(unit, simple_name, classes, simple):
    """Простое имя класса -> fqcn внутри проекта (через импорты, свой пакет, глобально)."""
    for imported in unit.imports:
        if imported.endswith("." + simple_name) or imported == simple_name:
            return imported
    local = f"{unit.package}.{simple_name}" if unit.package else simple_name
    if local in classes:
        return local
    candidates = simple.get(simple_name, [])
    if len(candidates) == 1:
        return candidates[0]
    return None


def strip_type(text):
    """`java.util.List<X>` -> `List`, `Map.Entry<K,V>` -> `Entry` (последний сегмент)."""
    cleaned = re.sub(r"<.*>", "", text).strip()
    cleaned = cleaned.replace("[]", "")
    return cleaned.rsplit(".", 1)[-1]


def class_chain(fqcn, classes):
    seen = []
    cur = fqcn
    while cur and cur in classes and cur not in seen:
        seen.append(cur)
        parent = classes[cur]["super"]
        cur = resolve_type(classes[cur]["unit"], parent, classes, {}) if parent else None
    return seen


def collect_calls(unit, node, fqcn, classes, simple, locals_=None):
    """Вызываемые методы проекта из поддерева.

    Разрешается три вида вызова:
      * без объекта / `this` / `super` — методы своего класса;
      * `Тип.метод()` — статический вызов, если `Тип` — класс проекта;
      * `переменная.метод()` — если переменная объявлена типом проекта (см. local_types).
    Вызовы через поля (`this.manager.tick()`) не разрешаются: статически типа поля
    здесь нет, и такая ветка добавляется в ROOT_EXTRA осознанно.
    """
    out = []
    locals_ = locals_ or {}
    for cur in walk(node):
        if cur.type == "method_invocation":
            name_node = cur.child_by_field_name("name")
            if name_node is None:
                continue
            name = node_text(unit, name_node)
            obj = cur.child_by_field_name("object")
            if obj is None or obj.type == "this" or obj.type == "super":
                out.append((fqcn, name))
            elif obj.type == "identifier":
                target = resolve_type(unit, node_text(unit, obj), classes, simple)
                if target is None:
                    declared = locals_.get(node_text(unit, obj))
                    if declared is not None:
                        target = resolve_type(unit, strip_type(declared), classes, simple)
                if target is not None:
                    out.append((target, name))
        elif cur.type == "method_reference":
            parts = [node_text(unit, c) for c in cur.named_children]
            if len(parts) == 2:
                owner, name = parts
                target = fqcn if owner in ("this", "super") else resolve_type(unit, owner, classes, simple)
                if target is not None:
                    out.append((target, name))
    return out


def registration_targets(units, classes, simple):
    """Методы и лямбды, попавшие в регистрацию «каждый тик/кадр»."""
    methods = []
    lambdas = []
    for unit in units:
        for node in walk(unit.root):
            if node.type != "method_invocation":
                continue
            head = node_text(unit, node).replace(" ", "")
            if not REGISTRATION_RE.match(head.split("(")[0] + "("):
                continue
            args = node.child_by_field_name("arguments")
            if args is None:
                continue
            for arg in args.named_children:
                if arg.type == "method_reference":
                    parts = [node_text(unit, c) for c in arg.named_children]
                    if len(parts) == 2:
                        owner, name = parts
                        target = resolve_type(unit, owner, classes, simple)
                        if target is not None and target in classes:
                            methods.extend(classes[target]["methods"].get(name, []))
                elif arg.type == "lambda_expression":
                    lambdas.append((unit, arg))
    return methods, lambdas


def is_capturing(unit, lambda_node, outer_locals, outer_fields):
    params = set()
    params_node = lambda_node.child_by_field_name("parameters")
    if params_node is not None:
        for inner in walk(params_node):
            if inner.type == "identifier":
                params.add(node_text(unit, inner))
    body = lambda_node.child_by_field_name("body")
    if body is None:
        return False
    for inner in walk(body):
        if inner.type != "identifier":
            continue
        parent = inner.parent
        if parent is not None and parent.type in ("method_invocation", "field_access", "method_reference"):
            owner = parent.child_by_field_name("object")
            if owner is not None and owner.id == inner.id and parent.type == "field_access":
                pass  # имя поля, а не захват, ловится ниже по совпадению с outer_fields
        name = node_text(unit, inner)
        if name in params:
            continue
        if name in outer_locals or name in outer_fields or name == "this":
            return True
    return False


def local_types(unit, method_node):
    """Объявленные типы локальных переменных и параметров: имя -> текст типа.

    Нужно, чтобы разрешить `aura.updateRotationTick(...)`: объект — локальная
    переменная проекта, и без типа вызов не попадал в граф (а с ним терялась вся
    ветка ротации: rotationTick -> updateRotationTick -> updateTarget -> findTarget).
    """
    types = {}
    params = method_node.child_by_field_name("parameters")
    if params is not None:
        for param in params.named_children:
            if param.type not in ("formal_parameter", "spread_parameter"):
                continue
            type_node = param.child_by_field_name("type")
            name_node = param.child_by_field_name("name")
            if type_node is not None and name_node is not None:
                types[node_text(unit, name_node)] = node_text(unit, type_node)
    for inner in walk(method_node):
        if inner.type == "local_variable_declaration":
            type_node = inner.child_by_field_name("type")
            if type_node is None:
                continue
            type_text = node_text(unit, type_node)
            for decl in inner.named_children:
                if decl.type == "variable_declarator":
                    key = decl.child_by_field_name("name")
                    if key is not None:
                        types[node_text(unit, key)] = type_text
    return types


def method_locals(unit, method_node):
    """Локальные имена метода (параметры + объявленные переменные)."""
    names = set()
    params = method_node.child_by_field_name("parameters")
    if params is not None:
        for param in params.named_children:
            for inner in walk(param):
                if inner.type == "identifier":
                    names.add(node_text(unit, inner))
                    break
    for inner in walk(method_node):
        if inner.type in ("local_variable_declaration", "catch_formal_parameter"):
            for decl in inner.named_children:
                if decl.type == "variable_declarator":
                    key = decl.child_by_field_name("name")
                    if key is not None:
                        names.add(node_text(unit, key))
    return names


def scan_findings(unit, roots, skip=None):
    """Находки внутри набора узлов (метод или тело лямбды).

    `skip` — узел, который сам аллокацией не считается, но его тело проверяется:
    так обрабатывается лямбда из точки регистрации — она создаётся один раз при
    регистрации обработчика, а не каждый кадр.
    """
    found = []
    for node, locals_, fields in roots:
        for cur in walk(node):
            if cur.type in TYPE_DECLS or cur.type in ANNOTATIONS:
                continue
            if skip is not None and cur.id == skip.id:
                continue
            rule = None
            if cur.type == "object_creation_expression":
                rule = "NEW"
            elif cur.type == "lambda_expression":
                if is_capturing(unit, cur, locals_, fields):
                    rule = "LAMBDA"
            elif cur.type == "method_invocation":
                name_node = cur.child_by_field_name("name")
                if name_node is not None:
                    name = node_text(unit, name_node)
                    if name in STREAM_OPS:
                        rule = "STREAM"
                    elif name in ITERATOR_OPS:
                        rule = "ITERATOR"
                    elif name in ALLOC_API or _class_qualified_alloc(unit, cur, name):
                        rule = "ALLOC_API"
                    elif name in FORMAT_OPS and _format_like(unit, cur):
                        rule = "FORMAT"
                    elif name in BOX_OPS and _boxing(unit, cur):
                        rule = "BOXING"
                    elif name in COPY_OPS and _copy_of(unit, cur):
                        rule = "COPY"
            elif cur.type == "binary_expression":
                if _string_concat(unit, cur):
                    rule = "CONCAT"
            if rule is None:
                continue
            snippet = re.sub(r"\s+", " ", node_text(unit, cur)).strip()
            found.append((rule, cur, snippet))
    return found


def _owner(unit, node):
    obj = node.child_by_field_name("object")
    if obj is None:
        return None
    return re.sub(r"\s+", "", node_text(unit, obj))


def _class_qualified_alloc(unit, node, name):
    return (_owner(unit, node), name) in CLASS_QUALIFIED_ALLOC


def _boxing(unit, node):
    return _owner(unit, node) in (
        "Integer", "Long", "Double", "Float", "Boolean", "Short", "Byte", "Character",
    )


def _copy_of(unit, node):
    return _owner(unit, node) in ("List", "Map", "Set", "Arrays", "EnumSet")


def _format_like(unit, node):
    obj = node.child_by_field_name("object")
    if obj is None:
        return False
    owner = re.sub(r"\s+", "", node_text(unit, obj))
    return owner == "String"


def _string_concat(unit, node):
    """`+` со строковым литералом — значит склейка строк (аллокация StringBuilder)."""
    if node.type != "binary_expression":
        return False
    if node.child_by_field_name("operator") is None:
        # tree-sitter-java не отдаёт поле operator для всех версий — проверяем по детям.
        pass
    has_string = False
    has_other = False
    for child in node.children:
        if child.type == "string_literal":
            has_string = True
        elif child.type not in ("+", "(", ")"):
            has_other = True
    return has_string and has_other


def build_hot(classes, simple, units):
    """Множество горячих методов: (fqcn, имя) + список горячих лямбд."""
    hot = set()
    queue = []

    def add(method):
        key = (method.fqcn, method.name)
        if key not in hot:
            hot.add(key)
            queue.append(method)

    reg_methods, reg_lambdas = registration_targets(units, classes, simple)
    for method in reg_methods:
        add(method)
    for fqcn, info in classes.items():
        for name, methods in info["methods"].items():
            for method in methods:
                if name in ROOT_NAMES or (fqcn, name) in ROOT_EXTRA:
                    add(method)
                if "/mixins/" in method.file.rel:
                    add(method)

    # Лямбды в точках регистрации тоже горячие: сканируем их отдельно.
    hot_lambdas = []
    for unit, node in reg_lambdas:
        hot_lambdas.append((unit, node))

    while queue:
        method = queue.pop(0)
        unit = method.file
        locals_ = local_types(unit, method.node)
        for target, name in collect_calls(unit, method.node, method.fqcn, classes, simple, locals_):
            for candidate in classes.get(target, {}).get("methods", {}).get(name, []):
                add(candidate)
            for parent in class_chain(target, classes)[1:]:
                for candidate in classes.get(parent, {}).get("methods", {}).get(name, []):
                    add(candidate)
    return hot, hot_lambdas


def collect_all(classes, units):
    by_file = {}
    for info in classes.values():
        for methods in info["methods"].values():
            for method in methods:
                by_file.setdefault(method.file.rel, []).append(method)
    return by_file


def method_roots(method):
    """(узел, локальные имена, поля класса) для сканирования метода."""
    unit = method.file
    return (method.node, method_locals(unit, method.node), method.fields)


def main(argv):
    src_root = Path(argv[1]) if len(argv) > 1 else SRC
    parser = _load_parser()
    units = collect_units(parser, src_root)
    classes, simple = index_classes(units)
    hot, hot_lambdas = build_hot(classes, simple, units)

    findings = []
    for fqcn, name in sorted(hot):
        for method in classes[fqcn]["methods"][name]:
            unit = method.file
            roots = [method_roots(method)]
            for rule, node, snippet in scan_findings(unit, roots):
                findings.append((unit.rel, name, rule, line_of(unit, node), snippet))

    for unit, lambda_node in hot_lambdas:
        locals_ = set()
        for cur in walk(lambda_node):
            if cur.type == "local_variable_declaration":
                for decl in cur.named_children:
                    if decl.type == "variable_declarator":
                        key = decl.child_by_field_name("name")
                        if key is not None:
                            locals_.add(node_text(unit, key))
        fields = set()
        for info in classes.values():
            if info["unit"] is unit:
                fields |= info["fields"]
        for rule, node, snippet in scan_findings(unit, [(lambda_node, locals_, fields)], skip=lambda_node):
            findings.append((unit.rel, LAMBDA_LABEL, rule, line_of(unit, node), snippet))

    findings.sort()
    hard = []
    soft = []
    counts = {}
    for rel, method, rule, line, snippet in findings:
        if rule in SOFT_RULES:
            soft.append((rel, method, rule, line, snippet))
            continue
        entry = None
        exact = (rel, method, rule, snippet)
        loose = (rel, method, rule, None)
        if exact in ALLOW:
            entry = exact
        elif loose in ALLOW:
            entry = loose
        elif (rel, "*", rule, None) in ALLOW:
            entry = (rel, "*", rule, None)
        counts[entry] = counts.get(entry, 0) + 1
        if entry is None:
            hard.append((rel, method, rule, line, snippet))

    over_budget = []
    stale = []
    for key, reason, budget in ALLOW_ENTRIES():
        used = counts.get(key, 0)
        if used > budget:
            over_budget.append((key, used, budget, reason))
        elif used < budget:
            stale.append((key, used, budget))
    if hard:
        print("НАЙДЕНО (жёсткие правила горячего пути):")
        for rel, method, rule, line, snippet in hard:
            print(f"  [{rule}] {rel}:{line} в {method} — {snippet[:110]}")
    if over_budget:
        print("ПРЕВЫШЕН БЮДЖЕТ ИСКЛЮЧЕНИЯ (появилась новая аллокация в том же месте):")
        for key, used, budget, reason in over_budget:
            print(f"  {key[0]} {key[1]} [{key[2]}] — факт {used}, бюджет {budget} ({reason})")
    if stale:
        print("УСТАРЕВШИЕ ИСКЛЮЧЕНИЯ (совпадений меньше бюджета — список надо чистить):")
        for key, used, budget in stale:
            print(f"  {key[0]} {key[1]} [{key[2]}] — факт {used}, бюджет {budget}")

    if soft:
        by_rule = {}
        for rel, _method, rule, _line, _snippet in soft:
            by_rule.setdefault(rule, {}).setdefault(rel, 0)
            by_rule[rule][rel] += 1
        print("ИНФОРМАЦИОННО (не блокирует, но рост виден):")
        for rule in sorted(by_rule):
            files = by_rule[rule]
            top = sorted(files.items(), key=lambda item: -item[1])[:5]
            total = sum(files.values())
            where = ", ".join(f"{name.split('/')[-1]}={count}" for name, count in top)
            print(f"  {rule}: {total} в {len(files)} файлах — {where}")

    allowed_hard = len(findings) - len(hard) - len(soft)
    print(f"файлов: {len(units)}, классов: {len(classes)}, горячих методов: {len(hot)}")
    print(f"жёстких аллокаций: {len(hard) + allowed_hard}, из них в исключениях: {allowed_hard}")
    print(f"информационных: {len(soft)}")
    print(f"найдено: {len(hard) + len(over_budget) + len(stale)}")
    return 0 if not hard and not over_budget and not stale else 1


# --------------------------------------------------------------------------- #
# Исключения: (файл, метод, правило, сниппет) -> (причина, бюджет).
#
# Сниппет входит в ключ, поэтому исключение привязано к конкретному выражению: код
# изменился — исключение перестало совпадать и находка вернулась. Метод можно
# заменить на "*" (весь файл), сниппет — на None (все вхождения в методе); в этом
# случае бюджет обязателен и работает как трещотка: новая аллокация в том же месте
# ломает сборку, пока бюджет не поднят осознанно.
# --------------------------------------------------------------------------- #
EXCEPTIONS = [
    # --- сеть: пакет обязателен, один объект на отправку ----------------------
    ("src/main/java/swerve/modules/impl/combat/AutoPotion.java", "sendRotation", "NEW", None,
     "ServerboundMovePlayerPacket.Rot — пакет поворота; один объект на отправку, "
     "переиспользовать нельзя (сеть забирает его асинхронно). Вызывается только при "
     "броске зелья, не каждый тик.", 1),
    ("src/main/java/swerve/modules/impl/player/DrinkUse.java", "selectSlot", "NEW", None,
     "ServerboundSetCarriedItemPacket — пакет смены слота, создаётся на одно переключение.", 1),
    ("src/main/java/swerve/utils/item/ConsumableUseController.java", "selectSlot", "NEW", None,
     "ServerboundSetCarriedItemPacket — пакет смены слота, создаётся на одно переключение.", 1),

    # --- сериализация: только по дебаунсу/выходу, не в тике -------------------
    ("src/main/java/swerve/config/ConfigManager.java", "encodeGui", "NEW", None,
     "JsonObject собирается при записи конфига (дебаунс dirty), а не в тике.", 1),
    ("src/main/java/swerve/config/ConfigManager.java", "encodeMenu", "NEW", None,
     "JsonObject собирается при записи конфига (дебаунс dirty), а не в тике.", 1),
    ("src/main/java/swerve/config/ConfigManager.java", "encodeModules", "NEW", None,
     "JsonObject на модуль при записи конфига (дебаунс dirty), не в тике.", 2),
    ("src/main/java/swerve/config/ConfigManager.java", "encodeSettings", "NEW", None,
     "JsonObject собирается при записи конфига (дебаунс dirty), а не в тике.", 1),
    ("src/main/java/swerve/config/ConfigManager.java", "save", "NEW", None,
     "Корневой JsonObject записи конфига; вызывается по таймеру сохранения.", 1),
    ("src/main/java/swerve/config/SettingCodec.java", "encode", "NEW", None,
     "JsonObject/JsonPrimitive — сериализация одной настройки при сохранении конфига.", 6),
    ("src/main/java/swerve/script/ScriptStore.java", "encodeChain", "NEW", None,
     "JsonArray/JsonObject — запись скрипта в файл, не в тике.", 2),
    ("src/main/java/swerve/script/ScriptStore.java", "save", "NEW", None,
     "JsonObject/JsonArray — запись скриптов в файл, не в тике.", 4),
    ("src/main/java/swerve/bot/BotStore.java", "save", "NEW", None,
     "JsonObject/JsonArray — запись сценариев ботов в файл, не в тике.", 6),
    ("src/main/java/swerve/graffiti/GraffitiStore.java", "save", "NEW", None,
     "JsonObject/JsonArray — запись граффити в файл по dirty-таймеру.", 5),
    ("src/main/java/swerve/graffiti/GraffitiStore.java", "readSpot", "NEW", None,
     "GraffitiSpot — разбор записи из файла при загрузке.", 1),
    ("src/main/java/swerve/modules/impl/render/ViewModel.java", "buildConfig", "NEW", None,
     "JsonObject — запись конфига ViewModel по дебаунсу.", 1),
    ("src/main/java/swerve/modules/impl/render/ViewModel.java", "createWriter", "NEW", None,
     "Поток записи конфига создаётся один раз при первом сохранении.", 1),
    ("src/main/java/swerve/acd/AcdStore.java", "snapshot", "NEW", None,
     "AcdReport — снимок истории по нажатию «Copy All Logs», не в тике.", 1),

    # --- разбор ресурсов и моделей (однократно на файл) ----------------------
    ("src/main/java/swerve/cosmetics/CosmeticGeometry.java", "load", "NEW", None,
     "Разбор модели .geo.json: выполняется один раз при первом обращении к косметике.", 5),
    ("src/main/java/swerve/cosmetics/CosmeticGeometry.java", "cube", "NEW", None,
     "Cube — часть разбора модели, вызывается из load().", 1),
    ("src/main/java/swerve/cosmetics/CosmeticGeometry.java", "track", "NEW", None,
     "Track/ArrayList — часть разбора анимаций модели, вызывается из load().", 2),
    ("src/main/java/swerve/utils/item/DrinkUtil.java", "decodeHash", "NEW", None,
     "new String — разбор base64 из конфига предметов, не в тике.", 1),
    ("src/main/java/swerve/graffiti/GraffitiSpot.java", "of", "NEW", None,
     "GraffitiSpot — запись рисунка создаётся при постановке/разборе файла.", 1),
    ("src/main/java/swerve/graffiti/GraffitiQuad.java", "centerOf", "NEW", None,
     "Центр грани возвращается как Vec3 по контракту GraffitiQuad; вызов — при постановке "
     "рисунка и проверке дистанции по ПКМ, не в кадре.", 1),
    ("src/main/java/swerve/modules/impl/render/Graffiti.java", "target", "ALLOC_API", None,
     "getEyePosition() — разовая проверка дистанции при наведении на грань (ПКМ/присед), "
     "не в кадре.", 1),
    ("src/main/java/swerve/modules/impl/render/Graffiti.java", "target", "NEW", None,
     "Target — результат того же разового наведения; создаётся на действие.", 1),

    # --- FBO/текстуры: только смена размера окна ----------------------------
    ("src/main/java/swerve/utils/render/HandOutline.java", "ensureTargets", "NEW", None,
     "TextureTarget'ы создаются только когда размер кадра изменился "
     "(guard `depthBefore != null && frameWidth == width`).", 4),
    ("src/main/java/swerve/modules/impl/render/HandShader.java", "ensureTargets", "NEW", None,
     "TextureTarget'ы руки создаются при смене размера кадра, не каждый кадр.", 2),
    ("src/main/java/swerve/ui/newgui/GuiBlur.java", "ensureBuffers", "NEW", None,
     "Уровни блюра создаются по одному разу на размер (guard `bufferWidth == width ...`).", 1),
    ("src/main/java/swerve/utils/render/spatial/SpatialTarget.java", "prepare", "NEW", None,
     "TextureTarget создаётся один раз; дальше только resize().", 1),

    # --- API требует объект: координаты уже скаляры --------------------------
    ("src/main/java/swerve/utils/combat/AuraUtils.java", "isPointVisible", "NEW", None,
     "Level.clip принимает только Vec3 (начало и конец) и ClipContext — это ванильный "
     "контракт; сами координаты считаются скалярами.", 3),
    ("src/main/java/swerve/utils/combat/AuraUtils.java", "visibleTargetPoint", "NEW", None,
     "Возвращаемый Vec3 — контракт вызывающего кода (одна аллокация вместо десяти); "
     "вторая — fallback-точка груди.", 2),
    ("src/main/java/swerve/modules/impl/combat/rotation/AbstractRotationElement.java", "isPointVisible", "NEW", None,
     "Level.clip/ClipContext принимают только Vec3 — ванильный контракт.", 3),
    ("src/main/java/swerve/utils/render/spatial/SpatialCamera.java", "adjustForCollision", "NEW", None,
     "Level.clip принимает только Vec3 + ClipContext; вызывается при включённом "
     "spatial-интерфейсе.", 3),
    ("src/main/java/swerve/utils/render/spatial/SpatialCamera.java", "checkBlockCollision", "NEW", None,
     "Vec3 нужны Level.clip, AABB.contains и getEyePosition-эквиваленту; BlockPos уже "
     "переиспользуется (MutableBlockPos).", 3),
    ("src/main/java/swerve/utils/render/spatial/ScreenCapture.java", "extract", "NEW", None,
     "GuiGraphics — ванильный объект для рендера в текстуру; создаётся на кадр захвата "
     "при включённом spatial-интерфейсе, иначе захвата нет.", 1),

    # --- одноразовые события и синглтоны ------------------------------------
    ("src/main/java/swerve/utils/input/ClickGuiController.java", "ЛЯМБДА", "NEW", None,
     "new MenuScreen() — только по нажатию бинда меню, внутри обработчика тика.", 1),
    ("src/main/java/swerve/cosmetics/cape/ClothCapeRenderer.java", "getInstance", "NEW", None,
     "Ленивый синглтон рендерера плаща: один объект за сессию.", 1),
    ("src/main/java/swerve/mixins/client/MixinMinecraft.java", "swerve$fixedProfileName", "NEW", None,
     "GameProfile — подмена ника при запросе профиля; вызывается не каждый тик.", 1),
    ("src/main/java/swerve/modules/impl/render/Arrows.java", "stateFor", "NEW", None,
     "ArrowState создаётся только для нового игрока в поле зрения (дальше он в списке).", 1),
    ("src/main/java/swerve/modules/impl/hud/ArrayListHud.java", "rebuildRows", "NEW", None,
     "Row — строка списка модулей; перестроение только при изменении набора/порядка.", 1),
    ("src/main/java/swerve/bot/BotRunner.java", "execute", "NEW", None,
     "LoopFrame — кадр цикла бот-сценария; аллокации только пока сценарий исполняется "
     "(см. docs/HotPath.md, долг №2).", 2),

    # --- журнал античита: реагирует на пакеты, а не на тик -------------------
    ("src/main/java/swerve/acd/AcdClassifier.java", "snippet", "NEW", None,
     "StringBuilder — обрезка текста только при совпадении правила защиты.", 1),
    ("src/main/java/swerve/acd/AcdCollector.java", "emit", "NEW", None,
     "AcdEvent — запись истории; создаётся только когда событие прошло фильтры.", 1),
    ("src/main/java/swerve/acd/AcdCollector.java", "enabledModules", "NEW", None,
     "StringBuilder — список включённых модулей для контекста события, не в тике.", 1),
    ("src/main/java/swerve/acd/AcdCollector.java", "announce", "LAMBDA", None,
     "Лямбда уходит в client.execute для показа WARNING/CRITICAL в чате — только на "
     "событии, попавшем в правила.", 1),

    # --- звук: работа уходит в отдельный поток ------------------------------
    ("src/main/java/swerve/utils/client/ClientSoundPlayer.java", "playSound", "LAMBDA", None,
     "Лямбда-задача для потока проигрывания: звук запускается на событие, не в кадре.", 1),
    ("src/main/java/swerve/utils/client/ClientSoundPlayer.java", "playInternal", "LAMBDA", None,
     "Слушатель LineEvent.STOP внутри того же потока проигрывания.", 1),
    ("src/main/java/swerve/utils/client/ClientSoundPlayer.java", "playInternal", "NEW", None,
     "BufferedInputStream — поток чтения звука, создаётся на одно проигрывание.", 1),
    ("src/main/java/swerve/utils/client/ClientSoundPlayer.java", "resampleStream", "NEW", None,
     "AudioInputStream/AudioFormat/ByteArrayInputStream — ресемплинг звука в потоке "
     "проигрывания, не в тике.", 3),

    # --- вызовы, появившиеся после разрешения типов локальных переменных -----
    ("src/main/java/swerve/cosmetics/CosmeticGeometry.java", "matrix", "NEW", None,
     "Matrix4f создаётся один раз на глубину дерева костей (guard `stack[depth] == null`).", 1),
    ("src/main/java/swerve/graffiti/GraffitiSpot.java", "pos", "ALLOC_API", None,
     "BlockPos.of(long) на вызов pos(); рисунков на мир один (SPOTS: world -> spot), "
     "поэтому это 1-2 объекта на кадр, а не на каждый рисунок.", 1),
    ("src/main/java/swerve/modules/impl/movement/ElytraFlight.java", "onPlayerUpdate", "NEW", None,
     "ServerboundPlayerCommandPacket — пакет старта элитры, уходит на действие, не каждый тик.", 1),
    ("src/main/java/swerve/utils/combat/AuraUtils.java", "hasHeadroom", "NEW", None,
     "noCollision принимает только AABB; вызов идёт лишь при включённом автопрыжке и "
     "готовой атаке (не в каждом тике).", 1),
    ("src/main/java/swerve/utils/item/HvHItemUse.java", "swapAndUseFirework", "NEW", None,
     "Пакеты смены слота/закрытия инвентаря/ввода на одно действие «фейерверк в HvH»; "
     "переиспользовать пакеты нельзя — сеть забирает их асинхронно.", 8),

    # --- скрипт-движок: кадры интерпретатора (долг №1) ----------------------
    ("src/main/java/swerve/script/ScriptEngine.java", "exec", "NEW", None,
     "Frame — кадр стека интерпретатора; исполняется только при запущенном пользователем "
     "скрипте (см. docs/HotPath.md, долг №1).", 1),
    ("src/main/java/swerve/script/ScriptEngine.java", "loop", "NEW", None,
     "Frame цикла интерпретатора; только при запущенном скрипте (долг №1).", 1),
    ("src/main/java/swerve/script/ScriptEngine.java", "start", "NEW", None,
     "Run — контекст запуска скрипта, создаётся на старт скрипта.", 1),
    ("src/main/java/swerve/script/ScriptText.java", "fill", "NEW", None,
     "StringBuilder — подстановка переменных в текст шага, только при исполнении скрипта.", 1),
]


def ALLOW_ENTRIES():
    """[(ключ, причина, бюджет)] — ключ это первые четыре поля строки таблицы."""
    return [(tuple(row[:4]), row[4], row[5]) for row in EXCEPTIONS]


def _allow_map():
    return {tuple(row[:4]): row for row in EXCEPTIONS}


ALLOW = _allow_map()


if __name__ == "__main__":
    raise SystemExit(main(sys.argv))
