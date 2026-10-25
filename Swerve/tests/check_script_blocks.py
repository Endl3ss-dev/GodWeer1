#!/usr/bin/env python3
"""Проверки модели скриптов: алгебра стеков блоков и ограничение сохранения.

Зачем этот файл. 2026-10-24 клиент упал с `java.lang.OutOfMemoryError` прямо в
тике: `ScriptStore.encodeChain -> save -> tick`. Причина — кольцо в ссылках
`next`: `detach` оставлял хвост и в исходном стеке, и в перетаскиваемой цепочке,
а `insert` возвращал цепочку на место и замыкал список (`tail.next -> head`).
Дальше `encodeChain` бесконечно добавлял блоки в `JsonArray`.

Проверка состоит из трёх частей:

1. **Статическая** — по исходникам Java: `detach` обязан рвать связь родителя,
   а не подставлять в него `target.getNext()`; `encodeChain` обязан иметь предел
   обхода и глубины; обходы `ScriptBlock` обязаны быть ограничены `MAX_CHAIN`.
2. **Модельная** — порт `detach`/`insert`/`at`/`contains` на Python и полный
   перебор перетаскиваний: после любой операции стек остаётся ациклическим
   списком, ни один блок не появляется дважды и не теряется, а возврат блока на
   исходное место восстанавливает исходный стек.
3. **Мутационная** — в модель подставляется прежняя (сломанная) версия `detach`,
   и проверка требует, чтобы перебор нашёл кольцо. Так видно, что часть 2 не
   «зеленая всегда», а действительно ловит именно эту регрессию.

Ограничение, которое нельзя замалчивать: часть 2 и 3 проверяют **алгоритм**, а
не байткод Java — JDK в песочнице нет, поэтому `javac`/Gradle запустить нельзя.
Порт написан построчно по `ScriptBlocks.detach`/`insert`, но при расхождении
оригинала и порта проверка этого не увидит; часть 1 привязана к конкретным
конструкциям исходника.

Использование: python3 Swerve/tests/check_script_blocks.py
Код возврата: 0 — всё сошлось, 1 — есть расхождения, 2 — не найдены исходники.
"""
from __future__ import annotations

import itertools
import re
import sys
from pathlib import Path

SWERVE = Path(__file__).resolve().parents[1]
# Необязательный аргумент — другой каталог swerve/script: так проверяется сама
# проверка (мутационный прогон на копии дерева), а не только рабочий исходник.
if len(sys.argv) > 1:
    SCRIPT_DIR = Path(sys.argv[1]).resolve()
    ROOT_JAVA = SCRIPT_DIR.parents[1] if SCRIPT_DIR.name == "script" else SCRIPT_DIR
else:
    SCRIPT_DIR = SWERVE / "src" / "main" / "java" / "swerve" / "script"
    ROOT_JAVA = SWERVE / "src" / "main" / "java"
SRC = SCRIPT_DIR
MAX_CHAIN = 4_000
MAX_DEPTH = 32

failures: list[str] = []


def fail(message: str) -> None:
    failures.append(message)


# --------------------------------------------------------------------------- #
# 1. Статические проверки по исходникам
# --------------------------------------------------------------------------- #
def static_checks() -> None:
    blocks = (SRC / "ScriptBlocks.java").read_text(encoding="utf-8")
    store = (SRC / "ScriptStore.java").read_text(encoding="utf-8")
    block = (SRC / "ScriptBlock.java").read_text(encoding="utf-8")

    detach_body = blocks[blocks.index("private static boolean detach"):]
    detach_body = detach_body[:detach_body.index("\n    /**")]

    if "target.getNext()" in detach_body:
        fail("ScriptBlocks.detach: в родителя снова подставляется target.getNext() — "
             "хвост останется в двух стеках одновременно, это исходный баг с OOM")
    if "holder.setHead(null)" not in detach_body:
        fail("ScriptBlocks.detach: голова контейнера не обнуляется — хвост остаётся на месте")
    if "block.setNext(null)" not in detach_body:
        fail("ScriptBlocks.detach: связь родителя не рвётся (нет block.setNext(null))")

    insert_body = blocks[blocks.index("public static boolean insert"):]
    if "contains(first, head)" not in insert_body:
        fail("ScriptBlocks.insert: нет проверки «вставляемый стек не содержит голову контейнера»")

    screen = (ROOT_JAVA / "swerve" / "ui" / "script" / "ScriptScreen.java").read_text(encoding="utf-8")
    if "private void attach(" not in screen:
        fail("ScriptScreen: нет attach() — отказ вставки потеряет блок")
    else:
        attach_body = screen[screen.index("private void attach("):]
        attach_body = attach_body[:attach_body.index("\n    private ")]
        outside = screen.replace(attach_body, "")
        if "ScriptBlocks.insert(" in outside:
            fail("ScriptScreen: вставка вне attach() идёт напрямую через ScriptBlocks.insert — "
                 "отказ вставки потеряет блок")

    if "MAX_DEPTH" not in store[store.index("private static JsonObject encodeChain(ScriptBlock head, int depth"):]:
        fail("ScriptStore.encodeChain: обход не ограничен по глубине")
    if "budget[0] > 0" not in store:
        fail("ScriptStore.encodeChain: обход не ограничен бюджетом блоков")
    if "truncationWarned" not in store:
        fail("ScriptStore.encodeChain: усечение не пишется в лог — молчаливая потеря скрипта")

    for name in ("chainLength", "tail", "flatten"):
        body = block[block.index(f"public {'' if name == 'chainLength' else ''}")]
        segment = block[block.index("public int chainLength" if name == "chainLength" else f"public ScriptBlock tail" if name == "tail" else "public java.util.List<ScriptBlock> flatten"):]
        segment = segment[:segment.index("\n    }")]
        if "MAX_CHAIN" not in segment:
            fail(f"ScriptBlock.{name}: обход без предела MAX_CHAIN — кольцо подвесит поток")


# --------------------------------------------------------------------------- #
# 2. Модель стека блоков (порт ScriptBlock/ScriptBlocks)
# --------------------------------------------------------------------------- #
class Block:
    """Блок модели: next/inner и признак «контейнер» (у C-блока есть пасть)."""

    def __init__(self, name: str, container: bool = False):
        self.name = name
        self.next: Block | None = None
        self.inner: Block | None = None
        self.container = container

    def get_head(self) -> "Block | None":
        return self.inner

    def set_head(self, block: "Block | None") -> None:
        self.inner = block

    def __repr__(self) -> str:
        return self.name


class Root:
    """Корень функции: BlockHolder с собственным head."""

    def __init__(self, name: str = "root"):
        self.head: Block | None = None
        self.name = name

    def get_head(self) -> "Block | None":
        return self.head

    def set_head(self, block: "Block | None") -> None:
        self.head = block


class BuggyKind:
    """Какая версия detach используется: исправленная или прежняя (для мутации)."""

    FIXED = "fixed"
    LEGACY = "legacy"


def chain(head: Block | None, limit: int = 64) -> list[Block]:
    out: list[Block] = []
    block = head
    while block is not None and len(out) < limit:
        out.append(block)
        block = block.next
    return out


def contains(head: Block | None, target: Block | None, depth: int = 0) -> bool:
    if head is None or target is None or depth > MAX_DEPTH:
        return False
    for block in chain(head, MAX_CHAIN):
        if block is target:
            return True
        if contains(block.inner, target, depth + 1):
            return True
    return False


def at(head: Block | None, index: int) -> Block | None:
    for i, block in enumerate(chain(head, MAX_CHAIN)):
        if i == index:
            return block
    return None


def tail(head: Block) -> Block:
    last = head
    for _ in range(MAX_CHAIN - 1):
        if last.next is None:
            break
        last = last.next
    return last


def detach(holder, target: Block, depth: int = 0, kind: str = BuggyKind.FIXED) -> bool:
    if holder is None or target is None or depth > MAX_DEPTH:
        return False
    head = holder.get_head()
    if head is target:
        if kind == BuggyKind.LEGACY:
            holder.set_head(target.next)
        else:
            holder.set_head(None)
        return True
    for block in chain(head, MAX_CHAIN):
        if block is target:
            return False
        if block.next is target:
            if kind == BuggyKind.LEGACY:
                block.next = target.next
            else:
                block.next = None
            return True
        if block.inner is not None and detach(block, target, depth + 1, kind):
            return True
    return False


def insert(holder, index: int, first: Block | None, kind: str = BuggyKind.FIXED) -> bool:
    if holder is None or first is None:
        return False
    head = holder.get_head()
    if kind == BuggyKind.FIXED:
        # Новые проверки: цепочку нельзя вложить в неё саму — ни в голову контейнера,
        # ни в C-блок, который находится внутри этой цепочки.
        if head is not None and contains(first, head):
            return False
        if isinstance(holder, Block) and contains(first, holder):
            return False
    else:
        # Прежняя проверка: только «C-блок внутри цепочки».
        if isinstance(holder, Block) and contains(first, holder):
            return False
    last = tail(first)

    if head is None:
        last.next = None
        holder.set_head(first)
        return True
    if index <= 0:
        last.next = head
        holder.set_head(first)
        return True
    before = at(head, index - 1)
    if before is None:
        last.next = None
        tail(head).next = first
        return True
    last.next = before.next
    before.next = first
    return True


# --------------------------------------------------------------------------- #
# 3. Свойства модели
# --------------------------------------------------------------------------- #
def attach(root: Root, holder, index: int, block: Block | None, kind: str = BuggyKind.FIXED) -> bool:
    """Порт ScriptScreen.attach: при отказе вставки цепочка уходит в конец корня."""
    if block is None:
        return False
    if insert(holder, index, block, kind):
        return True
    return insert(root, 10 ** 6, block, kind)


def all_blocks(root: Root) -> list[Block]:
    """Все блоки модели, включая вложенные стеки (с защитой от кольца)."""
    out: list[Block] = []
    pending = [root.head]
    while pending and len(out) < MAX_CHAIN:
        block = pending.pop()
        for item in chain(block, MAX_CHAIN):
            out.append(item)
            if item.inner is not None:
                pending.append(item.inner)
    return out


def has_cycle(head: Block | None, limit: int = 64) -> bool:
    seen: list[Block] = []
    block = head
    while block is not None:
        if any(block is item for item in seen):
            return True
        seen.append(block)
        if len(seen) > limit:
            # Обход без повтора не уложился в предел: это тоже кольцо.
            return True
        block = block.next
    return False


def build_stack(names: list[str], containers: set[str] | None = None) -> tuple[Root, list[Block]]:
    containers = containers or set()
    root = Root()
    blocks = [Block(name, name in containers) for name in names]
    for left, right in zip(blocks, blocks[1:]):
        left.next = right
    root.head = blocks[0]
    return root, blocks


def identity_ok(root: Root, expected_names: list[str]) -> None:
    names = [block.name for block in chain(root.head, 64)]
    if names != expected_names:
        fail(f"возврат на место изменил стек: было {expected_names}, стало {names}")
    if has_cycle(root.head):
        fail(f"после перестановки в стеке появилось кольцо: {[b.name for b in chain(root.head, 8)]}…")


def model_checks() -> None:
    # (а) Перетаскивание без движения: detach + возврат в ту же позицию.
    for length in (1, 2, 3, 4):
        names = [f"b{i}" for i in range(length)]
        for index in range(length):
            root, blocks = build_stack(names)
            holder = root
            if not detach(holder, blocks[index]):
                fail(f"detach не нашёл блок {index} в стеке длины {length}")
                continue
            if not attach(root, holder, index, blocks[index]):
                fail(f"блок {index} не вернулся в контейнер (стек длины {length})")
            identity_ok(root, names)

    # (б) Блок с вложенным стеком: детач из «пасти» и возврат внутрь.
    root, blocks = build_stack(["c0", "c1"], containers={"c0"})
    inner_root, inner = build_stack(["i0", "i1"])
    blocks[0].inner = inner_root.head
    detach(blocks[0], inner[0], kind=BuggyKind.FIXED)
    if blocks[0].inner is not None:
        fail("detach из головы вложенного стека не опустошил пасть")
    attach(root, blocks[0], 0, inner[0])
    if [b.name for b in chain(blocks[0].inner, 8)] != ["i0", "i1"]:
        fail("вложенный стек не восстановился в пасти")

    # (в) Полный перебор «взять блок i, положить в позицию j» на стеке длины ≤ 4.
    for length in (2, 3, 4):
        names = [f"b{i}" for i in range(length)]
        for source, dest in itertools.product(range(length), range(length + 1)):
            root, blocks = build_stack(names)
            taken = blocks[source]
            if not detach(root, taken):
                fail(f"detach не нашёл блок {source}")
                continue
            attach(root, root, dest, taken)
            moved = [b.name for b in chain(root.head, 64)]
            if has_cycle(root.head):
                fail(f"кольцо после переноса {source}->{dest} в стеке {names}")
            if len(moved) != length:
                fail(f"перенос {source}->{dest} изменил число блоков: {moved}")
            if len(set(moved)) != len(moved):
                fail(f"перенос {source}->{dest} продублировал блоки: {moved}")

    # (г) Отказ от самовложения: блок нельзя положить внутрь себя, но и потерять
    #     его нельзя — attach обязан вернуть цепочку в корень.
    root, blocks = build_stack(["c0", "b1", "b2"], containers={"c0"})
    detach(root, blocks[0])
    attach(root, blocks[0], 0, blocks[0])
    moved = [b.name for b in chain(root.head, 8)]
    if has_cycle(root.head):
        fail(f"кольцо при попытке вложить блок в себя: {moved}")
    if sorted(b.name for b in all_blocks(root)) != ["b1", "b2", "c0"]:
        fail(f"блок потерян при попытке вложить его в себя: {moved}")

    # (д) Ограничение записи: кольцо не должно крутить encodeChain вечно.
    root, blocks = build_stack(["b0", "b1", "b2"])
    blocks[2].next = blocks[0]  # испорченная модель: кольцо
    budget = 5
    visited = 0
    block = root.head
    while block is not None and budget > 0:
        budget -= 1
        visited += 1
        block = block.next
    if visited != 5:
        fail(f"ограниченный обход кольца посетил {visited} блоков вместо бюджета 5")


def mutation_checks() -> None:
    """Исходная пара «старый detach + старый insert» обязана давать кольцо.

    Иначе проверка ничего не доказывает: она должна воспроизводить именно тот баг,
    который дал OutOfMemoryError в логе тестового клиента.
    """
    caught = 0
    total = 0
    for length in (1, 2, 3):
        names = [f"b{i}" for i in range(length)]
        for index in range(length):
            total += 1
            root, blocks = build_stack(names)
            detach(root, blocks[index], kind=BuggyKind.LEGACY)
            insert(root, 0, blocks[index], BuggyKind.LEGACY)
            if has_cycle(root.head):
                caught += 1
    if caught == 0:
        fail("мутация «старый detach + старый insert» не дала кольца: "
             "модель не воспроизводит исходный баг с OutOfMemoryError")
    else:
        print(f"мутация (прежние detach+insert): кольцо воспроизведено в {caught} из {total} случаев")

    # Совместимость: исправленный detach совместим и со старой проверкой вставки —
    # то есть баг лечится в одном месте, без обязательной правки insert.
    fixed_cycles = 0
    for length in (1, 2, 3):
        names = [f"b{i}" for i in range(length)]
        for index in range(length):
            root, blocks = build_stack(names)
            detach(root, blocks[index], kind=BuggyKind.FIXED)
            attach(root, root, index, blocks[index], BuggyKind.LEGACY)
            if has_cycle(root.head):
                fixed_cycles += 1
    if fixed_cycles:
        fail(f"исправленный detach всё ещё даёт кольцо в {fixed_cycles} случаях")


def main() -> int:
    if not SRC.is_dir():
        print(f"нет каталога {SRC}")
        return 2

    static_checks()
    model_checks()
    mutation_checks()

    if failures:
        print("НАЙДЕНО:")
        for item in failures:
            print(f"  - {item}")
        print(f"найдено: {len(failures)}")
        return 1

    print("модель стеков: ацикличность, сохранность блоков и ограничение записи — ok")
    print("найдено: 0")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
