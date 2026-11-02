#!/usr/bin/env python3
"""Проверка синтаксиса Java-исходников Swerve разбором (без JDK).

Зачем: `./gradlew compileJava` в песочнице и в CI недоступен, а `check_mc_api.py`
считает только баланс скобок. «Лишняя» `;`, незакрытая строка, `return` вне метода,
обрезанный на середине файл или перенос `}` в другом месте — всё это компилируется
в тихую поломку или в ошибку javac, которую здесь некому поймать. Разбор
`tree-sitter-java` находит такие места точно и построчно.

Что проверяется
  1. В дереве разбора нет узлов ERROR и MISSING (то есть файл — корректный Java 21
     по грамматике: пакет, импорты, классы, методы, операторы).
  2. У каждого файла есть `package` и ровно один публичный тип с именем файла.
  3. Файл не обрезан: последняя `}` класса закрывается (следствие пункта 1, но
     проверяется отдельно — ради понятного сообщения).

Чего проверка НЕ делает: типов, разрешения имён, generics, перегрузок — это
`check_types.py` и `check_scope.py`, а остальное видно только настоящему javac.

Использование:
    pip3 install --target ~/.pylibs tree_sitter tree_sitter_java   # песочница: /usr не сохраняется
    # либо обычно: pip3 install --break-system-packages tree_sitter tree_sitter_java
    python3 Swerve/tests/check_syntax.py [путь-к-src]

Код возврата: 0 — чисто, 1 — есть находки, 2 — не установлены зависимости.
"""
from __future__ import annotations

import sys
from pathlib import Path

# Песочница не сохраняет /usr: пакеты, поставленные через pip --break-system-packages,
# исчезают при пересоздании окружения, а ~/.local исключён из снимка. Поэтому
# дополнительно ищем библиотеки в ~/.pylibs — туда их ставит
#   pip3 install --target ~/.pylibs tree_sitter tree_sitter_java
# и этот каталог переживает пересоздание песочницы.
def _load_parser():
    for extra in (), Path.home() / ".pylibs":
        if extra and str(extra) not in sys.path:
            sys.path.insert(0, str(extra))
        try:
            from tree_sitter import Language, Parser
            import tree_sitter_java as tsj
        except ImportError:
            continue
        return Parser(Language(tsj.language()))
    print("Нужны зависимости: pip3 install --target ~/.pylibs tree_sitter tree_sitter_java")
    print("(или в системный python: pip3 install --break-system-packages tree_sitter tree_sitter_java)")
    sys.exit(2)



PARSER = _load_parser()
SWERVE = Path(__file__).resolve().parents[1]


TYPE_NODES = ("class_declaration", "interface_declaration", "enum_declaration",
              "record_declaration", "annotation_type_declaration")


def findings_for(path: Path, source: bytes) -> list:
    tree = PARSER.parse(source)
    findings = []
    package_seen = False
    type_names = set()
    if tree.root_node.has_error:
        # Дерево с ошибкой: имена и объявления внутри него ненадёжны, поэтому
        # сообщаем только про синтаксис — его и нужно поймать в первую очередь.
        stack = [tree.root_node]
        while stack:
            node = stack.pop()
            if node.type == "ERROR" or node.is_missing:
                line = source[:node.start_byte].count(b"\n") + 1
                if node.is_missing:
                    # У отсутствующего узла нулевая длина: показываем контекст, иначе
                    # сообщение выглядит как «найдено пустое место» без объяснений.
                    snippet = source[node.start_byte:min(len(source), node.start_byte + 50)]
                    text = snippet.decode("utf-8", "replace").replace("\n", "\\n").lstrip()
                    findings.append(f"{path}:{line}: ожидалось {node.type!r}, перед {text!r}")
                else:
                    snippet = source[node.start_byte:min(node.end_byte, node.start_byte + 70)]
                    text = snippet.decode("utf-8", "replace").replace("\n", "\\n").strip()
                    findings.append(f"{path}:{line}: разбор не сходится: {text!r}")
            stack.extend(node.children)
        # Один пропущенный `}` дерево отмечает несколькими узлами сразу: не плодим дубли.
        return list(dict.fromkeys(findings))

    for node in tree.root_node.children:
        if node.type == "package_declaration":
            package_seen = True
        elif node.type in TYPE_NODES:
            name = node.child_by_field_name("name")
            if name is not None:
                type_names.add(source[name.start_byte:name.end_byte].decode("utf-8", "replace"))

    if not package_seen:
        findings.append(f"{path}: нет объявления package")
    # package-info.java — это аннотации на пакет, типа в файле нет по замыслу.
    if path.stem != "package-info" and path.stem not in type_names:
        findings.append(f"{path}: имя файла не совпадает ни с одним объявленным типом "
                        f"{sorted(type_names)}")
    text = source.decode("utf-8", "replace")
    if text.count("{") != text.count("}"):
        findings.append(f"{path}: фигурных скобок {text.count('{')} против {text.count('}')} "
                        "— файл похож на обрезанный")
    return findings


def main() -> int:
    root = Path(sys.argv[1]) if len(sys.argv) > 1 else SWERVE / "src/main/java"
    if not root.is_dir():
        print(f"STRUCTURE_NOT_FOUND: {root}", file=sys.stderr)
        return 2

    total = 0
    checked = 0
    for path in sorted(root.rglob("*.java")):
        checked += 1
        source = path.read_bytes()
        try:
            shown = path.relative_to(SWERVE.parent)
        except ValueError:
            shown = path
        for finding in findings_for(shown, source):
            print("найдено:", finding)
            total += 1
    print(f"файлов: {checked}, находок: {total}")
    return 1 if total else 0


if __name__ == "__main__":
    sys.exit(main())
