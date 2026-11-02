"""Статическая проверка приводимости типов в исходниках Swerve (замена части javac).

Ловит ровно тот класс ошибок, который javac печатает как
  * «incompatible types: X cannot be converted to Y» — аргумент/присваивание/return;
  * «incompatible types: X cannot be converted to Y» на `instanceof`
    («inconvertible types»).

Что проверяется
  1. аргументы вызова против типов параметров объявленных методов;
  2. `instanceof` против статического типа левого операнда;
  3. инициализаторы локальных переменных и обычные присваивания;
  4. `return` против типа возврата метода.

Почему это нужно: в песочнице и в CI без JDK нельзя запустить `./gradlew compileJava`,
а такие ошибки (например, забытый `implements` у класса, который передают в параметр
интерфейсного типа) видны только компилятору — сам код синтаксически корректен.

Что НЕ проверяется (чтобы не выдавать ложных находок): типы из JDK и Minecraft,
генерики (сравниваются «сырые» имена), вывод типов (`var`, лямбды, тернарники разных
типов), перегрузки с одинаковой арностью и совместимыми аргументами, области
видимости внутри `switch`-паттернов JDK-классов.

Использование:
    pip3 install --break-system-packages tree_sitter tree_sitter_java
    python3 Swerve/tests/check_types.py [src-root]

Код возврата: 0 — чисто, 1 — есть находки, 2 — не установлены зависимости.
"""
from __future__ import annotations

import sys
from pathlib import Path

try:
    from tree_sitter import Language, Parser
    import tree_sitter_java as tsj
except ImportError:  # pragma: no cover
    print("Нужны зависимости: pip3 install --break-system-packages tree_sitter tree_sitter_java")
    sys.exit(2)

PARSER = Parser(Language(tsj.language()))

TYPE_DECLS = {"class_declaration", "interface_declaration", "enum_declaration", "record_declaration"}
METHODS = {"method_declaration", "constructor_declaration", "compact_constructor_declaration"}
PRIMITIVES = {"byte", "short", "char", "int", "long", "float", "double", "boolean"}
WIDENING = {
    "byte": {"short", "int", "long", "float", "double"},
    "short": {"int", "long", "float", "double"},
    "char": {"int", "long", "float", "double"},
    "int": {"long", "float", "double"},
    "long": {"float", "double"},
    "float": {"double"},
}
TYPE_NODES = ("type_identifier", "generic_type", "scoped_type_identifier", "integral_type",
              "floating_point_type", "boolean_type", "array_type", "void_type")


def text(node, src):
    return src[node.start_byte:node.end_byte].decode("utf-8", "replace")


def line_of(node, src):
    return src[:node.start_byte].count(b"\n") + 1


def kids(node, *types):
    return [c for c in node.children if c.type in types]


def walk(node, prune=()):
    """Обход поддерева; поддеревья типов из `prune` не раскрываются."""
    stack = [node]
    while stack:
        cur = stack.pop()
        yield cur
        if cur.type in prune:
            continue
        stack.extend(reversed(cur.children))


def normalize(raw):
    """`Map<String, List<Foo>>[]` -> `Map[]`: генерики убираются, массивы остаются."""
    if raw is None:
        return None
    t = raw.strip()
    for prefix in ("final ", "static "):
        while t.startswith(prefix):
            t = t[len(prefix):].strip()
    while t.startswith("@"):
        parts = t.split(None, 1)
        if len(parts) < 2:
            return None
        t = parts[1].strip()
    t = t.replace("...", "[]")
    out, depth = [], 0
    for ch in t:
        if ch == "<":
            depth += 1
        elif ch == ">":
            depth -= 1
        elif depth == 0:
            out.append(ch)
    result = "".join(out).replace(" ", "")
    return result or None


def type_params_of(node, src):
    params = set()
    declared = node.child_by_field_name("type_parameters")
    if declared is not None:
        for child in kids(declared, "type_parameter"):
            names = kids(child, "identifier")
            if names:
                params.add(text(names[0], src))
    return params


class Method:

    def __init__(self, name, params, returns, varargs, owner):
        self.name = name
        self.params = params
        self.returns = returns
        self.varargs = varargs
        self.owner = owner


class ClassInfo:

    def __init__(self, name, kind):
        self.name = name
        self.kind = kind          # class_declaration / interface_declaration / ...
        self.supers = set()
        self.methods = {}
        self.fields = {}
        self.type_params = set()

    @property
    def is_interface(self):
        return self.kind == "interface_declaration"


class Scope:
    """Объявления имён с позицией: при выводе типа берётся ближайшее предыдущее."""

    def __init__(self):
        self.entries = {}   # name -> [(start_byte, type)]

    def add(self, name, type_name, position, shadow_marks=None):
        if name is None:
            return
        self.entries.setdefault(name, []).append((position, type_name))

    def lookup(self, name, position):
        found = self.entries.get(name)
        if not found:
            return None
        best = None
        for start, type_name in found:
            if start <= position and (best is None or start >= best[0]):
                best = (start, type_name)
        return None if best is None else best[1]


class Checker:

    def __init__(self, root: Path):
        self.root = root
        self.classes: dict[str, ClassInfo] = {}
        self.files: dict[Path, bytes] = {}
        self.trees: dict[Path, object] = {}
        self.owner_of: dict[Path, dict[tuple, str]] = {}
        self.findings: list[str] = []

    # ---------- разбор ----------

    def load(self):
        for path in sorted(self.root.rglob("*.java")):
            src = path.read_bytes()
            self.files[path] = src
            self.trees[path] = PARSER.parse(src)
        for path, tree in self.trees.items():
            self.collect(path, tree.root_node)

    def collect(self, path, root):
        src = self.files[path]
        owners = {}
        # Карта «узел -> ближайший внешний тип»: `this` и вызовы без получателя
        # должны разрешаться в свой класс, а не в первый тип файла.
        stack = [(root, None)]
        while stack:
            node, owner = stack.pop()
            if node.type in TYPE_DECLS:
                name_node = node.child_by_field_name("name")
                owner = text(name_node, src) if name_node is not None else owner
            owners[(node.start_byte, node.end_byte)] = owner
            if node.type not in ("import_declaration", "package_declaration"):
                stack.extend((c, owner) for c in node.children)
        self.owner_of[path] = owners

        for node in walk(root):
            if node.type not in TYPE_DECLS:
                continue
            name_node = node.child_by_field_name("name")
            if name_node is None:
                continue
            name = text(name_node, src)
            info = self.classes.get(name)
            if info is None:
                info = ClassInfo(name, node.type)
                self.classes[name] = info
            info.type_params |= type_params_of(node, src)

            sup = node.child_by_field_name("superclass")
            if sup is not None:
                # узел superclass включает слово `extends` — берём вложенный тип
                sup_type = sup.child_by_field_name("type")
                if sup_type is None:
                    named = kids(sup, *TYPE_NODES)
                    sup_type = named[0] if named else None
                if sup_type is not None:
                    info.supers.add(normalize(text(sup_type, src)))
            for child in node.children:
                if child.type in ("super_interfaces", "extends_interfaces", "interfaces"):
                    listed = kids(child, "type_list")
                    containers = listed if listed else [child]
                    for container in containers:
                        for item in kids(container, *TYPE_NODES):
                            info.supers.add(normalize(text(item, src)))

            body = node.child_by_field_name("body")
            if body is None:
                continue
            members = []
            for child in body.children:
                if child.type == "enum_body_declarations":
                    members.extend(child.children)
                else:
                    members.append(child)

            for member in members:
                if member.type in METHODS:
                    is_ctor = member.type != "method_declaration"
                    params_node = member.child_by_field_name("parameters")
                    params, varargs = [], False
                    if params_node is not None:
                        for param in params_node.children:
                            if param.type == "formal_parameter":
                                params.append(self.decl_type(param, src))
                            elif param.type == "spread_parameter":
                                base = self.decl_type(param, src)
                                params.append(base + "[]" if base else None)
                                varargs = True
                    returns = "void"
                    if not is_ctor:
                        rt = member.child_by_field_name("type")
                        returns = normalize(text(rt, src)) if rt is not None else None
                    mname = name if is_ctor else text(member.child_by_field_name("name"), src)
                    info.methods.setdefault(mname, []).append(
                        Method(mname, params, returns, varargs, name))
                elif member.type == "field_declaration":
                    ftype = member.child_by_field_name("type")
                    if ftype is None:
                        continue
                    raw = normalize(text(ftype, src))
                    for decl in kids(member, "variable_declarator"):
                        fname = decl.child_by_field_name("name")
                        if fname is not None:
                            info.fields[text(fname, src)] = raw

    @staticmethod
    def decl_type(param, src):
        t = param.child_by_field_name("type")
        return normalize(text(t, src)) if t is not None else None

    def owner(self, path, node):
        return self.owner_of.get(path, {}).get((node.start_byte, node.end_byte))

    # ---------- отношения типов ----------

    def supers_of(self, name, seen=None):
        seen = set() if seen is None else seen
        if name in seen or name not in self.classes:
            return seen
        seen.add(name)
        for sup in self.classes[name].supers:
            if sup:
                self.supers_of(sup, seen)
        return seen

    def assignable(self, src, dst):
        """True/False, либо None если ответа заведомо не знаем (JDK-типы и т.п.)."""
        if src is None or dst is None:
            return None
        src, dst = normalize(src), normalize(dst)
        if src is None or dst is None:
            return None
        if src == "null" or src == dst:
            return True
        if src.endswith("[]") or dst.endswith("[]"):
            if src.endswith("[]") and dst.endswith("[]"):
                return self.assignable(src[:-2], dst[:-2])
            return True if dst == "Object" else False
        if src in PRIMITIVES or dst in PRIMITIVES:
            if src == dst:
                return True
            if src in PRIMITIVES and dst in PRIMITIVES:
                return dst in WIDENING.get(src, set())
            return None if src not in PRIMITIVES else False
        if dst == "Object":
            return True
        if src in self.classes and dst in self.classes:
            return dst in self.supers_of(src)
        return None

    def related(self, left, right):
        """Для instanceof: достаточно связи в любую сторону."""
        if left is None or right is None:
            return None
        down = self.assignable(right, left)
        up = self.assignable(left, right)
        if down is True or up is True:
            return True
        if down is False and up is False:
            return False
        return None

    # ---------- области видимости ----------

    def scope_of(self, node, src):
        scope = Scope()
        if node.type in METHODS:
            params = node.child_by_field_name("parameters")
            if params is not None:
                for param in params.children:
                    if param.type in ("formal_parameter", "spread_parameter"):
                        name_node = param.child_by_field_name("name")
                        base = self.decl_type(param, src)
                        if param.type == "spread_parameter":
                            base = base + "[]" if base else None
                        if name_node is not None:
                            scope.add(text(name_node, src), base, param.start_byte)

        body = node.child_by_field_name("body")
        if body is None:
            body = node
        for sub in walk(body, prune=TYPE_DECLS):
            if sub.type == "local_variable_declaration":
                t = sub.child_by_field_name("type")
                raw = normalize(text(t, src)) if t is not None else None
                if raw == "var":
                    raw = None
                for decl in kids(sub, "variable_declarator"):
                    name_node = decl.child_by_field_name("name")
                    if name_node is not None:
                        scope.add(text(name_node, src), raw, decl.start_byte)
            elif sub.type == "enhanced_for_statement":
                t = sub.child_by_field_name("type")
                raw = normalize(text(t, src)) if t is not None else None
                name_node = sub.child_by_field_name("name")
                if raw != "var" and name_node is not None:
                    scope.add(text(name_node, src), raw, sub.start_byte)
            elif sub.type == "catch_formal_parameter":
                t = sub.child_by_field_name("type")
                name_node = sub.child_by_field_name("name")
                if t is not None and name_node is not None:
                    scope.add(text(name_node, src), normalize(text(t, src)), sub.start_byte)
            elif sub.type == "instanceof_expression":
                # `s instanceof FloatSetting n` — n объявлен прямо здесь
                name_node = sub.child_by_field_name("name")
                right = sub.child_by_field_name("right")
                if name_node is not None and right is not None:
                    scope.add(text(name_node, src), normalize(text(right, src)), sub.start_byte)
            elif sub.type == "type_pattern":
                t = sub.child_by_field_name("type")
                name_node = sub.child_by_field_name("name")
                if t is not None and name_node is not None:
                    scope.add(text(name_node, src), normalize(text(t, src)), sub.start_byte)
            elif sub.type == "lambda_expression":
                params = sub.child_by_field_name("parameters")
                if params is not None:
                    for param in kids(params, "identifier", "formal_parameter", "inferred_parameters"):
                        if param.type == "identifier":
                            scope.add(text(param, src), None, param.start_byte)
        return scope

    # ---------- типы выражений ----------

    def field_type(self, class_name, field, depth=0):
        if class_name is None or depth > 12:
            return None
        info = self.classes.get(class_name)
        if info is None:
            return None
        if field in info.fields:
            return info.fields[field]
        for sup in info.supers:
            found = self.field_type(sup, field, depth + 1)
            if found is not None:
                return found
        return None

    def type_of(self, node, ctx, depth=0):
        if node is None or depth > 8:
            return None
        src, scope = ctx["src"], ctx["scope"]
        current = ctx["class"]
        t = node.type

        if t == "identifier":
            name = text(node, src)
            found = scope.lookup(name, node.start_byte)
            if found is not None or name in scope.entries:
                return found
            return self.field_type(current, name)
        if t == "this":
            return current
        if t == "parenthesized_expression":
            inner = [c for c in node.children if c.type not in ("(", ")")]
            return self.type_of(inner[0], ctx, depth + 1) if inner else None
        if t == "object_creation_expression":
            ty = node.child_by_field_name("type")
            return normalize(text(ty, src)) if ty is not None else None
        if t == "cast_expression":
            ty = node.child_by_field_name("type")
            return normalize(text(ty, src)) if ty is not None else None
        if t in ("string_literal", "text_block"):
            return "String"
        if t in ("decimal_integer_literal", "hex_integer_literal", "octal_integer_literal",
                 "binary_integer_literal"):
            return "long" if text(node, src).lower().endswith("l") else "int"
        if t in ("decimal_floating_point_literal", "hex_floating_point_literal"):
            return "float" if text(node, src).lower().endswith("f") else "double"
        if t in ("true", "false"):
            return "boolean"
        if t == "field_access":
            obj = node.child_by_field_name("object")
            field = node.child_by_field_name("field")
            if field is None:
                return None
            obj_type = self.type_of(obj, ctx, depth + 1) if obj is not None else current
            return self.field_type(obj_type, text(field, src))
        if t == "method_invocation":
            return self.method_return(node, ctx, depth + 1)
        if t == "array_access":
            base = self.type_of(node.child_by_field_name("array"), ctx, depth + 1)
            return base[:-2] if base and base.endswith("[]") else None
        if t == "array_creation_expression":
            ty = node.child_by_field_name("type")
            if ty is None:
                return None
            # `type` в tree-sitter включает уже написанные скобки, а размерностей
            # может быть несколько (`new float[n][m]`), поэтому база чистится,
            # а число `[]` берётся по всему выражению.
            base = normalize(text(ty, src)) or ""
            while base.endswith("[]"):
                base = base[:-2]
            return base + "[]" * self.array_dims(node, src)
        return None

    @staticmethod
    def array_dims(node, src):
        """Число измерений в `new T[...][...]`: скобки в размерах и в
        инициализаторе `{...}` не считаются."""
        raw = text(node, src)
        dims, depth = 0, 0
        for ch in raw:
            if ch == "(":
                depth += 1
            elif ch == ")":
                depth -= 1
            elif ch == "{":
                break
            elif ch == "[" and depth == 0:
                dims += 1
        return dims


    def methods_named(self, class_name, name, seen=None):
        seen = set() if seen is None else seen
        if class_name in seen or class_name not in self.classes:
            return []
        seen.add(class_name)
        info = self.classes[class_name]
        found = list(info.methods.get(name, []))
        for sup in info.supers:
            found.extend(self.methods_named(sup, name, seen))
        return found

    def method_return(self, node, ctx, depth=0):
        src = ctx["src"]
        name_node = node.child_by_field_name("name")
        if name_node is None:
            return None
        name = text(name_node, src)
        obj = node.child_by_field_name("object")
        if obj is None:
            owner = ctx["class"]
        else:
            owner = self.type_of(obj, ctx, depth + 1)
            if owner is None and obj.type == "identifier" and text(obj, src) in self.classes:
                owner = text(obj, src)
        if owner is None or owner not in self.classes:
            return None

        args = node.child_by_field_name("arguments")
        arity = 0 if args is None else len([c for c in args.children if c.type not in ("(", ")", ",")])
        for candidate in self.methods_named(owner, name):
            if candidate.returns is None:
                continue
            if len(candidate.params) == arity or (candidate.varargs and arity >= len(candidate.params) - 1):
                return candidate.returns
        return None

    # ---------- проверки ----------

    def report(self, path, node, message):
        src = self.files[path]
        try:
            shown = path.relative_to(self.root)
        except ValueError:
            shown = path
        self.findings.append(f"{shown}:{line_of(node, src)}: {message}")

    def check_file(self, path):
        src = self.files[path]
        tree = self.trees[path]
        for node in walk(tree.root_node):
            if node.type not in METHODS and node.type != "static_initializer":
                continue
            current = self.owner(path, node)
            ctx = {
                "src": src,
                "scope": self.scope_of(node, src),
                "class": current,
            }
            return_type = None
            if node.type == "method_declaration":
                rt = node.child_by_field_name("type")
                return_type = normalize(text(rt, src)) if rt is not None else None
            self.check_body(node, ctx, return_type, path)

    def check_body(self, node, ctx, return_type, path):
        body = node.child_by_field_name("body")
        root = body if body is not None else node

        # Вызовы, instanceof, присваивания: чужие типы проверяются отдельно,
        # поэтому вложенные объявления классов не раскрываем.
        for sub in walk(root, prune=TYPE_DECLS):
            if sub.type == "method_invocation":
                self.check_call(sub, ctx, path)
            elif sub.type == "instanceof_expression":
                self.check_instanceof(sub, ctx, path)
            elif sub.type == "local_variable_declaration":
                self.check_local(sub, ctx, path)
            elif sub.type == "assignment_expression":
                self.check_assignment(sub, ctx, path)

        # return принадлежит лямбде или анонимному классу — тип возврата другой.
        if return_type is None:
            return
        for sub in walk(root, prune=TYPE_DECLS | {"lambda_expression"}):
            if sub.type != "return_statement":
                continue
            expr = [c for c in sub.children if c.type not in ("return", ";")]
            if not expr:
                continue
            found = self.type_of(expr[0], ctx)
            if self.assignable(found, return_type) is False:
                self.report(path, sub, f"return: {found} не приводится к {return_type}")

    def check_call(self, node, ctx, path):
        src = ctx["src"]
        name_node = node.child_by_field_name("name")
        obj = node.child_by_field_name("object")
        args_node = node.child_by_field_name("arguments")
        if name_node is None:
            return
        name = text(name_node, src)
        args = [] if args_node is None else [c for c in args_node.children
                                             if c.type not in ("(", ")", ",")]

        if obj is None:
            owner = ctx["class"]
        else:
            owner = self.type_of(obj, ctx)
            if owner is None and obj.type == "identifier" and text(obj, src) in self.classes:
                owner = text(obj, src)
        if owner is None or owner not in self.classes:
            return

        candidates = []
        for candidate in self.methods_named(owner, name):
            count = len(candidate.params)
            if candidate.varargs:
                if len(args) >= count - 1:
                    candidates.append(candidate)
            elif len(args) == count:
                candidates.append(candidate)
        if not candidates:
            return

        first_mismatch = None
        for candidate in candidates:
            mismatch = None
            for index, arg in enumerate(args):
                if candidate.varargs and index >= len(candidate.params) - 1:
                    if not candidate.params:
                        break
                    expected = candidate.params[-1]
                    if expected and expected.endswith("[]"):
                        expected = expected[:-2]
                else:
                    if index >= len(candidate.params):
                        break
                    expected = candidate.params[index]
                if expected is None:
                    continue
                actual = self.type_of(arg, ctx)
                if actual is None:
                    continue
                if self.assignable(actual, expected) is False:
                    mismatch = (actual, expected, index)
                    break
            if mismatch is None:
                return      # нашлась подходящая перегрузка
            if first_mismatch is None:
                first_mismatch = mismatch

        actual, expected, index = first_mismatch
        self.report(path, node, f"{name}(): аргумент {index + 1} — {actual}, ожидается {expected}")

    def check_instanceof(self, node, ctx, path):
        src = ctx["src"]
        left = node.child_by_field_name("left")
        right = node.child_by_field_name("right")
        if right is None:
            right = node.child_by_field_name("type")
        if left is None or right is None:
            return
        left_type = self.type_of(left, ctx)
        right_type = normalize(text(right, src))
        if left_type is None or left_type in PRIMITIVES:
            return
        if self.related(left_type, right_type) is False:
            self.report(path, node,
                        f"instanceof: {left_type} не связан с {right_type} (inconvertible types)")

    def check_local(self, node, ctx, path):
        src = ctx["src"]
        ty = node.child_by_field_name("type")
        declared = normalize(text(ty, src)) if ty is not None else None
        if declared is None or declared == "var":
            return
        for decl in kids(node, "variable_declarator"):
            value = decl.child_by_field_name("value")
            if value is None:
                continue
            actual = self.type_of(value, ctx)
            if self.assignable(actual, declared) is False:
                name_node = decl.child_by_field_name("name")
                name = text(name_node, src) if name_node is not None else "?"
                self.report(path, node, f"{name}: {actual} не приводится к {declared}")

    def check_assignment(self, node, ctx, path):
        left = node.child_by_field_name("left")
        right = node.child_by_field_name("right")
        if left is None or right is None:
            return
        target = self.type_of(left, ctx)
        actual = self.type_of(right, ctx)
        if self.assignable(actual, target) is False:
            src = ctx["src"]
            self.report(path, node,
                        f"присваивание: {actual} не приводится к {target} ({text(left, src)})")


def main(argv):
    default = Path(__file__).resolve().parents[1] / "src/main/java"
    root = Path(argv[1]) if len(argv) > 1 else default
    if not root.is_dir():
        print(f"нет каталога {root}")
        return 2

    checker = Checker(root)
    checker.load()
    for path in checker.trees:
        checker.check_file(path)

    seen = set()
    for finding in checker.findings:
        if finding in seen:
            continue
        seen.add(finding)
        print(finding)

    print(f"файлов: {len(checker.trees)}, классов: {len(checker.classes)}, находок: {len(seen)}")
    return 1 if seen else 0


if __name__ == "__main__":
    sys.exit(main(sys.argv))
