"""Проверка «имя используется, но в этой области видимости не объявлено».

Ловит регрессии вида «удалил объявление локальной переменной, а использования
остались» — ровно то, что javac печатает как 'cannot find symbol'. Проверка не
заменяет javac (нет типов и разрешения перегрузок), но бесплатно ловит самый
частый класс поломок при рефакторинге и не требует JDK.

Использование:
    pip3 install --break-system-packages tree_sitter tree_sitter_java
    python3 Swerve/tests/check_scope.py Swerve/src/main/java

Код возврата: 0 — чисто, 1 — есть находки, 2 — не установлены зависимости.
"""
import os
import sys

try:
    from tree_sitter import Language, Parser
    import tree_sitter_java as tsj
except ImportError:
    print("Нужны зависимости: pip3 install --break-system-packages tree_sitter tree_sitter_java")
    sys.exit(2)

parser = Parser(Language(tsj.language()))

SCOPE_NODES = {
    "class_body", "interface_body", "enum_body", "record_body",
    "method_declaration", "constructor_declaration", "lambda_expression",
    "block", "switch_block", "switch_block_statement_group",
    "for_statement", "enhanced_for_statement", "try_with_resources_statement",
    # record-компоненты (String hash, Drink drink) — это поля записи
    "record_declaration",
}
SKIP_ANCESTORS = {
    "import_declaration", "package_declaration", "annotation", "marker_annotation",
    "scoped_identifier", "scoped_type_identifier", "type_parameter",
}
SKIP_PARENTS = {
    "field_access", "method_invocation", "object_creation_expression", "method_reference",
    "marker_annotation", "annotation", "scoped_identifier", "label",
}


def walk(n):
    stack = [n]
    while stack:
        cur = stack.pop()
        yield cur
        stack.extend(cur.children)


def decl_names(node):
    """Имена, объявляемые данным узлом (без захода в дочерние области)."""
    out = []
    t = node.type
    if t == "record_declaration":
        for c in node.children:
            if c.type == "identifier":
                out.append(c.text.decode())
                break
        for c in node.children:
            if c.type == "formal_parameters":
                for p in c.children:
                    if p.type == "formal_parameter":
                        for g in p.children:
                            if g.type == "identifier":
                                out.append(g.text.decode())
                                break
    elif t in ("method_declaration", "constructor_declaration"):
        for c in node.children:
            if c.type == "identifier":
                out.append(c.text.decode())
                break
        for c in node.children:
            if c.type == "formal_parameters":
                for p in c.children:
                    if p.type in ("formal_parameter", "spread_parameter"):
                        for g in p.children:
                            if g.type == "identifier":
                                out.append(g.text.decode())
                                break
                        # varargs: тип ... имя
                        for g in p.children:
                            if g.type == "variable_declarator":
                                for h in g.children:
                                    if h.type == "identifier":
                                        out.append(h.text.decode())
                                        break
    elif t == "lambda_expression":
        for c in node.children:
            if c.type == "identifier":
                out.append(c.text.decode())
            elif c.type == "inferred_parameters":
                for g in c.children:
                    if g.type == "identifier":
                        out.append(g.text.decode())
            elif c.type == "formal_parameters":
                for p in c.children:
                    if p.type == "formal_parameter":
                        for g in p.children:
                            if g.type == "identifier":
                                out.append(g.text.decode())
                                break
    elif t == "enhanced_for_statement":
        # for (Тип имя : выражение) — имя это второй identifier до ':'
        seen_type = False
        for c in node.children:
            if c.type == ":":
                break
            if c.type == "identifier":
                out.append(c.text.decode())
    else:
        for c in node.children:
            if c.type == "variable_declarator":
                for g in c.children:
                    if g.type == "identifier":
                        out.append(g.text.decode())
                        break
            elif c.type in ("catch_formal_parameter", "type_pattern", "resource",
                            "instanceof_expression", "receiver_parameter"):
                for g in c.children:
                    if g.type == "identifier":
                        out.append(g.text.decode())
            elif c.type == "type_identifier" and t in ("catch_formal_parameter",):
                continue
    return out


class Scope:
    __slots__ = ("names",)

    def __init__(self):
        self.names = set()


def field_names(root):
    """Поля класса: объявлены вне порядка использования, собираем заранее."""
    names = set()
    for n in walk(root):
        if n.type in ("field_declaration", "constant_declaration", "enum_constant"):
            for c in n.children:
                if c.type == "variable_declarator":
                    for g in c.children:
                        if g.type == "identifier":
                            names.add(g.text.decode())
                            break
                elif c.type == "identifier":
                    names.add(c.text.decode())
                    break
    return names


def check(path):
    src = open(path, "rb").read()
    root = parser.parse(src).root_node
    problems = []
    scopes = []
    fields = field_names(root)

    def resolve(name):
        if name in fields:
            return True
        for sc in reversed(scopes):
            if name in sc.names:
                return True
        return False

    def visit(node):
        push = node.type in SCOPE_NODES
        if push:
            sc = Scope()
            sc.names.update(decl_names(node))
            scopes.append(sc)
        else:
            # объявления внутри простых узлов (например, локальная переменная) —
            # в текущую область
            if scopes:
                scopes[-1].names.update(decl_names(node))

        if node.type == "identifier":
            name = node.text.decode()
            if not name[:1].isupper():  # имена типов не проверяем
                skip = False
                cur = node.parent
                guard = 0
                while cur is not None and guard < 12:
                    guard += 1
                    if cur.type in SKIP_ANCESTORS:
                        skip = True
                        break
                    cur = cur.parent
                if not skip:
                    parent = node.parent
                    if parent is not None and parent.type in SKIP_PARENTS:
                        first = parent.children[0] if parent.children else None
                        if first is not node:
                            skip = True
                if not skip and not resolve(name):
                    problems.append((node.start_point[0] + 1, name))

        for c in node.children:
            visit(c)

        if push:
            scopes.pop()

    visit(root)
    return problems


if __name__ == "__main__":
    bad = 0
    paths = sys.argv[1:]
    if len(paths) == 1 and os.path.isdir(paths[0]):
        found = []
        for r, _, fs in os.walk(paths[0]):
            found.extend(os.path.join(r, f) for f in fs if f.endswith(".java"))
        paths = sorted(found)
    for path in paths:
        for line, name in check(path):
            print(f"{path}:{line}: '{name}' используется, но не объявлен")
            bad += 1
    print(f"найдено: {bad}")
    sys.exit(1 if bad else 0)
