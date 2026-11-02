#!/usr/bin/env python3
"""Дизассемблер байткода Minecraft: чтение реальных фактов о 1.21.4 без JDK.

Зачем: в песочнице (и в CI без Gradle-кэша) нет ни `javap`, ни компилятора, поэтому
утверждения вида «ванильный `Entity.turn` умножает дельту на 0.15», «крит требует
`getAttackStrengthScale() > 0.9`» или «`handleInteract` меряет дистанцию как
`entityInteractionRange + 3.0`» нельзя ни скомпилировать, ни проверить офлайн.
Единственный честный источник истины — байткод в `minecraft-merged` jar, который
Loom уже скачал в `.gradle`. Именно его и читает этот скрипт.

Что умеет
  * `--list`   — методы класса (с флагами и дескрипторами);
  * `--method` — байткод метода с разбором constant pool;
  * `--grep`   — только те методы, где в байткоде встречается подстрока;
  * `--find`   — имена классов jar'а по подстроке;
  * `--desc`, `--exact`, `--lines`, `--fields` — фильтры.

Формат близок к `javap -c`: `invokevirtual #12 // Method net/minecraft/.../foo:(I)V`.

Чего здесь НЕТ: декомпиляции в Java-подобный код, типов, анализа стека и обработчиков
исключений. Это инструмент для проверки конкретных фактов, а не замена декомпилятора.
Линейная расшифровка может «поплыть» после `tableswitch`/`jsr` — для методов, где это
важно, смотрите смещения руками.

Использование (из любого каталога):
    python3 Swerve/tests/mc_disasm.py --class net.minecraft.world.entity.Entity --method turn
    python3 Swerve/tests/mc_disasm.py --class net.minecraft.world.entity.player.Player --list
    python3 Swerve/tests/mc_disasm.py --class net.minecraft.client.MouseHandler --grep sensitivity

Код возврата: 0 — найдено и распечатано, 1 — класс или метод не найдены,
2 — jar не найден (нужна хотя бы одна Gradle-сборка, чтобы Loom его скачал).
"""
from __future__ import annotations

import argparse
import re
import struct
import sys
import zipfile
from pathlib import Path

SWERVE = Path(__file__).resolve().parents[1]

# --------------------------------------------------------------------------- #
# Constant pool
# --------------------------------------------------------------------------- #
UTF8, INTEGER, FLOAT, LONG, DOUBLE = 1, 3, 4, 5, 6
CLASS, STRING, FIELDREF, METHODREF, IMETHODREF, NAMEANDTYPE = 7, 8, 9, 10, 11, 12
METHOD_HANDLE, METHOD_TYPE, DYNAMIC, INVOKE_DYNAMIC, MODULE, PACKAGE = 15, 16, 17, 18, 19, 20


class ConstantPool:
    """Полный constant pool: Class/NameAndType/Ref-записи, а не только Utf8."""

    __slots__ = ("utf8", "classes", "name_and_type", "refs", "strings", "numbers", "kinds", "end")

    def __init__(self, data: bytes, offset: int):
        (count,) = struct.unpack_from(">H", data, offset)
        offset += 2
        self.utf8: list = [None] * count
        self.classes: dict = {}
        self.name_and_type: dict = {}
        self.refs: dict = {}
        self.strings: dict = {}
        self.numbers: dict = {}
        self.kinds: dict = {}
        index = 1
        while index < count:
            tag = data[offset]
            offset += 1
            if tag == UTF8:
                (length,) = struct.unpack_from(">H", data, offset)
                offset += 2
                self.utf8[index] = data[offset:offset + length].decode("utf-8", "replace")
                offset += length
            elif tag == CLASS:
                (name_index,) = struct.unpack_from(">H", data, offset)
                self.classes[index] = name_index
                offset += 2
            elif tag == STRING:
                (string_index,) = struct.unpack_from(">H", data, offset)
                self.strings[index] = self.utf8[string_index]
                offset += 2
            elif tag == INTEGER:
                self.numbers[index] = struct.unpack_from(">i", data, offset)[0]
                offset += 4
            elif tag == FLOAT:
                self.numbers[index] = struct.unpack_from(">f", data, offset)[0]
                offset += 4
            elif tag == LONG:
                self.numbers[index] = struct.unpack_from(">q", data, offset)[0]
                offset += 8
                index += 1
            elif tag == DOUBLE:
                self.numbers[index] = struct.unpack_from(">d", data, offset)[0]
                offset += 8
                index += 1
            elif tag in (FIELDREF, METHODREF, IMETHODREF):
                (class_index, nt_index) = struct.unpack_from(">HH", data, offset)
                self.refs[index] = (class_index, nt_index)
                self.kinds[index] = {FIELDREF: "field", METHODREF: "method",
                                     IMETHODREF: "interface method"}[tag]
                offset += 4
            elif tag == NAMEANDTYPE:
                (name_index, desc_index) = struct.unpack_from(">HH", data, offset)
                self.name_and_type[index] = (name_index, desc_index)
                offset += 4
            elif tag == METHOD_HANDLE:
                offset += 3
            elif tag == METHOD_TYPE:
                offset += 2
            elif tag in (DYNAMIC, INVOKE_DYNAMIC):
                offset += 4
            elif tag in (MODULE, PACKAGE):
                offset += 2
            else:
                raise ValueError(f"unknown constant pool tag {tag} at index {index}")
            index += 1
        self.end = offset

    def class_name(self, index: int) -> str:
        raw = self.classes.get(index, index)
        return self.utf8[raw] if isinstance(raw, int) else str(raw)

    def ref(self, index: int) -> tuple:
        """(kind, owner, 'name:descriptor') for a field/method reference."""
        class_index, nt_index = self.refs[index]
        name_index, desc_index = self.name_and_type[nt_index]
        return (self.kinds.get(index, "ref"), self.class_name(class_index),
                f"{self.utf8[name_index]}:{self.utf8[desc_index]}")

    def describe(self, index: int) -> str:
        if index in self.refs:
            kind, owner, member = self.ref(index)
            return f"// {kind} {owner}.{member}"
        if index in self.strings:
            return f'// String "{self.strings[index]}"'
        if index in self.classes:
            return f"// class {self.class_name(index)}"
        if index in self.name_and_type:
            name_index, desc_index = self.name_and_type[index]
            return f"// {self.utf8[name_index]}:{self.utf8[desc_index]}"
        if index in self.numbers:
            return f"// {self.numbers[index]!r}"
        if index < len(self.utf8) and self.utf8[index]:
            return f"// {self.utf8[index]}"
        return ""


class MethodInfo:
    __slots__ = ("name", "descriptor", "flags", "code", "lines", "attributes")

    def __init__(self):
        self.name = ""
        self.descriptor = ""
        self.flags = 0
        self.code = b""
        self.lines: list = []
        self.attributes: list = []


def _read_attributes(data: bytes, offset: int, pool: ConstantPool, info) -> int:
    """Walk one attribute table; keep Code bytes and LineNumberTable when present."""
    (count,) = struct.unpack_from(">H", data, offset)
    offset += 2
    for _ in range(count):
        (name_index,) = struct.unpack_from(">H", data, offset)
        (length,) = struct.unpack_from(">I", data, offset + 2)
        body = offset + 6
        name = pool.utf8[name_index]
        info.attributes.append(name)
        if name == "Code":
            # JVMS 4.7.3: max_stack u2, max_locals u2, code_length u4, code[],
            # exception_table_length u2 и по 8 байт на запись, attributes_count u2.
            (code_length,) = struct.unpack_from(">I", data, body + 4)
            code_start = body + 8
            info.code = data[code_start:code_start + code_length]
            cursor = code_start + code_length
            (exception_count,) = struct.unpack_from(">H", data, cursor)
            cursor += 2 + exception_count * 8
            (attribute_count,) = struct.unpack_from(">H", data, cursor)
            cursor += 2
            for _ in range(attribute_count):
                (attribute_name,) = struct.unpack_from(">H", data, cursor)
                (attribute_len,) = struct.unpack_from(">I", data, cursor + 2)
                if pool.utf8[attribute_name] == "LineNumberTable":
                    (line_count,) = struct.unpack_from(">H", data, cursor + 6)
                    scan = cursor + 8
                    for _ in range(line_count):
                        (start_pc, line) = struct.unpack_from(">HH", data, scan)
                        info.lines.append((start_pc, line))
                        scan += 4
                cursor += 6 + attribute_len
        offset = body + length
    return offset


def _skip_attributes(data: bytes, offset: int) -> int:
    (count,) = struct.unpack_from(">H", data, offset)
    offset += 2
    for _ in range(count):
        (length,) = struct.unpack_from(">I", data, offset + 2)
        offset += 6 + length
    return offset


def parse_class(data: bytes) -> tuple:
    """Return (pool, this_name, super_name, methods, fields)."""
    if data[:4] != b"\xca\xfe\xba\xbe":
        raise ValueError("not a class file")
    pool = ConstantPool(data, 8)
    offset = pool.end
    offset += 2  # access flags
    (this_class, super_class, interface_count) = struct.unpack_from(">HHH", data, offset)
    offset += 6
    for _ in range(interface_count):
        offset += 2
    fields = []
    (field_count,) = struct.unpack_from(">H", data, offset)
    offset += 2
    for _ in range(field_count):
        (flags, name_index, desc_index) = struct.unpack_from(">HHH", data, offset)
        fields.append((flags, pool.utf8[name_index], pool.utf8[desc_index]))
        offset = _skip_attributes(data, offset + 6)
    methods = []
    (method_count,) = struct.unpack_from(">H", data, offset)
    offset += 2
    for _ in range(method_count):
        (flags, name_index, desc_index) = struct.unpack_from(">HHH", data, offset)
        info = MethodInfo()
        info.flags = flags
        info.name = pool.utf8[name_index]
        info.descriptor = pool.utf8[desc_index]
        offset = _read_attributes(data, offset + 6, pool, info)
        methods.append(info)
    return pool, pool.class_name(this_class), pool.class_name(super_class) if super_class else None, \
        methods, fields


# --------------------------------------------------------------------------- #
# Bytecode table
# --------------------------------------------------------------------------- #
# Строится из диапазонов JVMS (глава 6.5), а не из рукописного hex-списка:
# сдвиг на один Opcode в таблице молча сдвигает всю расшифровку метода.
# layout: u1 = 1 байт, c8/c16 = индекс constant pool (u1/u2), i16 = signed short,
# b16/o32 = смещение перехода, inc/cim/cidy/cmn/wid/sw = особый формат.
OPS: dict = {}


def _define(start_opcode: int, names: str, layout: str = "") -> None:
    entries = names.split()
    for offset, name in enumerate(entries):
        opcode = start_opcode + offset
        if opcode in OPS:
            raise AssertionError(f"duplicate opcode 0x{opcode:02x}: {name} vs {OPS[opcode][0]}")
        OPS[opcode] = (name, layout)


def _build_ops() -> None:
    _define(0x00, "nop aconst_null iconst_m1 iconst_0 iconst_1 iconst_2 iconst_3 iconst_4"
                  " iconst_5 lconst_0 lconst_1 fconst_0 fconst_1 fconst_2 dconst_0 dconst_1")
    _define(0x10, "bipush", "u1")
    _define(0x11, "sipush", "i16")
    _define(0x12, "ldc", "c8")
    _define(0x13, "ldc_w ldc2_w", "c16")
    _define(0x15, "iload lload fload dload aload", "u1")
    _define(0x1a, "iload_0 iload_1 iload_2 iload_3 lload_0 lload_1 lload_2 lload_3"
                  " fload_0 fload_1 fload_2 fload_3 dload_0 dload_1 dload_2 dload_3"
                  " aload_0 aload_1 aload_2 aload_3")
    _define(0x2e, "iaload laload faload daload aaload baload caload saload")
    _define(0x36, "istore lstore fstore dstore astore", "u1")
    _define(0x3b, "istore_0 istore_1 istore_2 istore_3 lstore_0 lstore_1 lstore_2 lstore_3"
                  " fstore_0 fstore_1 fstore_2 fstore_3 dstore_0 dstore_1 dstore_2 dstore_3"
                  " astore_0 astore_1 astore_2 astore_3")
    _define(0x4f, "iastore lastore fastore dastore aastore bastore castore sastore")
    _define(0x57, "pop pop2 dup dup_x1 dup_x2 dup2 dup2_x1 dup2_x2 swap")
    _define(0x60, "iadd ladd fadd dadd isub lsub fsub dsub imul lmul fmul dmul"
                  " idiv ldiv fdiv ddiv irem lrem frem drem ineg lneg fneg dneg")
    _define(0x78, "ishl lshl ishr lshr iushr lushr iand land ior lor ixor lxor")
    _define(0x84, "iinc", "inc")
    _define(0x85, "i2l i2f i2d l2i l2f l2d f2i f2l f2d d2i d2l d2f i2b i2c i2s")
    _define(0x94, "lcmp fcmpl fcmpg dcmpl dcmpg")
    _define(0x99, "ifeq ifne iflt ifge ifgt ifle if_icmpeq if_icmpne if_icmplt if_icmpge"
                  " if_icmpgt if_icmple if_acmpeq if_acmpne", "b16")
    _define(0xa7, "goto jsr", "b16")
    _define(0xa9, "ret", "u1")
    _define(0xaa, "tableswitch lookupswitch", "sw")
    _define(0xac, "ireturn lreturn freturn dreturn areturn return")
    _define(0xb2, "getstatic putstatic getfield putfield invokevirtual invokespecial"
                  " invokestatic", "c16")
    _define(0xb9, "invokeinterface", "cim")
    _define(0xba, "invokedynamic", "cidy")
    _define(0xbb, "new", "c16")
    _define(0xbc, "newarray", "u1")
    _define(0xbd, "anewarray", "c16")
    _define(0xbe, "arraylength athrow")
    _define(0xc0, "checkcast instanceof", "c16")
    _define(0xc2, "monitorenter monitorexit")
    _define(0xc4, "wide", "wid")
    _define(0xc5, "multianewarray", "cmn")
    _define(0xc6, "ifnull ifnonnull", "b16")
    _define(0xc8, "goto_w jsr_w", "o32")


_build_ops()
# 0xca breakpoint и 0xfe/0xff impdep1/impdep2 в JVMS есть, но javac их не emits;
# остальные «дыры» (0x1f, 0xca..0xfd) зарезервированы — для них печатается unknown_0xNN.
for _code, _name in ((0xca, "breakpoint"), (0xfe, "impdep1"), (0xff, "impdep2")):
    OPS[_code] = (_name, "")


FLAGS = ((0x0001, "public"), (0x0002, "private"), (0x0004, "protected"), (0x0008, "static"),
         (0x0010, "final"), (0x0020, "synchronized"), (0x0040, "bridge"), (0x0080, "varargs"),
         (0x0100, "native"), (0x0200, "interface"), (0x0400, "abstract"), (0x0800, "strict"),
         (0x1000, "synthetic"))


def flag_text(flags: int) -> str:
    return ",".join(label for bit, label in FLAGS if flags & bit)


def disassemble(pool: ConstantPool, code: bytes, lines: list) -> list:
    """Linear disassembly with pool comments and source-line markers."""
    out: list = []
    by_pc = dict(lines)
    pc = 0
    size = len(code)
    while pc < size:
        opcode = code[pc]
        name, layout = OPS.get(opcode, (f"unknown_0x{opcode:02x}", ""))
        text = f"  {pc:4d}: {name}"
        consumed = 1
        try:
            if layout == "u1":
                text += f" {code[pc + 1]}"
                consumed = 2
            elif layout == "c8":
                index = code[pc + 1]
                comment = pool.describe(index)
                text += f" #{index}" + (f" {comment}" if comment else "")
                consumed = 2
            elif layout in ("c16", "i16"):
                (value,) = struct.unpack_from(">H", code, pc + 1)
                if layout == "c16":
                    comment = pool.describe(value)
                    text += f" #{value}" + (f" {comment}" if comment else "")
                else:
                    text += f" {struct.unpack('>h', struct.pack('>H', value))[0]}"
                consumed = 3
            elif layout == "b16":
                (delta,) = struct.unpack_from(">h", code, pc + 1)
                text += f" -> {pc + delta}"
                consumed = 3
            elif layout == "o32":
                (delta,) = struct.unpack_from(">i", code, pc + 1)
                text += f" -> {pc + delta}"
                consumed = 5
            elif layout == "inc":
                (local, delta) = struct.unpack_from(">Bb", code, pc + 1)
                text += f" local{local}, +{delta}"
                consumed = 3
            elif layout == "cim":
                (index, count) = struct.unpack_from(">HB", code, pc + 1)
                text += f" #{index} {pool.describe(index)}, count={count}"
                consumed = 5
            elif layout == "cidy":
                (index,) = struct.unpack_from(">H", code, pc + 1)
                text += f" #{index} {pool.describe(index)}"
                consumed = 5
            elif layout == "cmn":
                (index, dims) = struct.unpack_from(">HB", code, pc + 1)
                text += f" #{index} {pool.describe(index)}, dims={dims}"
                consumed = 4
            elif layout == "wid":
                (sub, local) = struct.unpack_from(">BH", code, pc + 1)
                sub_name = OPS.get(sub, ("op", ""))[0]
                if sub == 0x84:
                    (delta,) = struct.unpack_from(">h", code, pc + 3)
                    text = f"  {pc:4d}: wide {sub_name} local{local}, +{delta}"
                    consumed = 6
                else:
                    text = f"  {pc:4d}: wide {sub_name} local{local}"
                    consumed = 4
            elif layout == "sw":
                # JVMS 3.10: данные выровнены по 4 относительно самого Opcode'а, а
                # порядок полей — сначала default, и только потом low/high или npairs.
                pad = (4 - (pc + 1) % 4) % 4
                base = pc + 1 + pad
                default = struct.unpack_from(">i", code, base)[0]
                if name == "tableswitch":
                    low, high = struct.unpack_from(">ii", code, base + 4)
                    text += f" low={low} high={high} default -> {pc + default}"
                    for key in range(low, min(high, low + 32) + 1):
                        (target,) = struct.unpack_from(">i", code, base + 12 + (key - low) * 4)
                        text += f"\n        case {key}: -> {pc + target}"
                    consumed = 1 + pad + 12 + max(0, high - low + 1) * 4
                else:
                    (npairs,) = struct.unpack_from(">i", code, base + 4)
                    text += f" npairs={npairs} default -> {pc + default}"
                    for pair in range(min(max(npairs, 0), 32)):
                        key, target = struct.unpack_from(">ii", code, base + 8 + pair * 8)
                        text += f"\n        case {key}: -> {pc + target}"
                    consumed = 1 + pad + 8 + max(npairs, 0) * 8
        except (struct.error, IndexError):
            text += "  <усечено>"
        if pc in by_pc:
            text = f"    // строка {by_pc[pc]}\n{text}"
        out.append(text)
        pc += max(consumed, 1)
    return out


# --------------------------------------------------------------------------- #
# Jar access
# --------------------------------------------------------------------------- #
def find_jar(explicit):
    if explicit:
        path = Path(explicit)
        return path if path.is_file() else None
    cache = SWERVE / ".gradle/loom-cache/minecraftMaven"
    candidates = [p for p in sorted(cache.glob("net/minecraft/*/*/*.jar"))
                  if "-sources" not in p.name] if cache.is_dir() else []
    return candidates[0] if candidates else None


def selftest(archive: zipfile.ZipFile, limit: int) -> int:
    """Расшифровать байткод каждого метода из `limit` классов и не встретить unknown.

    Таблица операций — единственное место, где у этого инструмента есть рукописная
    knowledge-часть. Один неверный Opcode сдвигает расшифровку всего метода, и вывод
    выглядит правдоподобно, но врёт. Проверка по реальным классам это исключает:
    если бы код читался со сдвигом, потоки почти гарантированно упирались бы в
    неизвестный Opcode или в несовпадение длины кода.
    """
    entries = [name for name in archive.namelist()
               if name.endswith(".class") and "$" not in name]
    # Детерминированный «случайный» отбор: один и тот же набор при каждом запуске.
    step = max(1, len(entries) // max(1, limit))
    chosen = entries[::step][:limit]
    classes = methods = unknown = misaligned = 0
    examples = []
    for name in chosen:
        try:
            pool, _, _, class_methods, _ = parse_class(archive.read(name))
        except Exception:
            continue
        classes += 1
        for info in class_methods:
            if not info.code:
                continue
            methods += 1
            body = disassemble(pool, info.code, [])
            bad = [line for line in body if ": unknown_0x" in line]
            if bad:
                unknown += 1
                if len(examples) < 5:
                    examples.append(f"{name}: {bad[0].strip()}")
            last = body[-1] if body else ""
            # Последняя инструкция должна касаться конца массива кода: сдвиг читается
            # по «усечено» или по смещению, не доходя до code_length.
            if "<усечено>" in last:
                misaligned += 1
                if len(examples) < 5:
                    examples.append(f"{name}: усечено в конце {info.name}")
    print(f"классов: {classes}, методов с кодом: {methods}, "
          f"с неизвестным Opcode: {unknown}, с рассинхроном в конце: {misaligned}")
    for example in examples:
        print("  пример:", example)
    return 1 if (unknown or misaligned) else 0


def main() -> int:
    parser = argparse.ArgumentParser(description="читает байткод minecraft-merged jar без JDK")
    parser.add_argument("--jar", help="путь к minecraft-merged jar (по умолчанию — кэш Loom)")
    parser.add_argument("--class", dest="cls", help="имя класса (точки или слеши)")
    parser.add_argument("--method", help="имя метода (подстрока, если не --exact)")
    parser.add_argument("--desc", help="фильтр дескриптора, например (FF)V")
    parser.add_argument("--list", action="store_true", help="список методов класса")
    parser.add_argument("--fields", action="store_true", help="со --list: показать и поля")
    parser.add_argument("--grep", help="печатать только методы, где есть эта подстрока")
    parser.add_argument("--find", help="список классов jar'а по подстроке")
    parser.add_argument("--exact", action="store_true", help="точное совпадение --method")
    parser.add_argument("--lines", action="store_true", help="подписывать номера строк")
    parser.add_argument("--selftest", type=int, metavar="N", default=0,
                        help="проверить таблицу Opcode'ов на N случайных классах jar'а")
    args = parser.parse_args()

    jar = find_jar(args.jar)
    if jar is None:
        print("JAR_NOT_FOUND: нужна сборка Gradle (Loom скачает minecraft-merged jar) "
              "или явный --jar", file=sys.stderr)
        return 2

    with zipfile.ZipFile(jar) as archive:
        if args.selftest:
            return selftest(archive, args.selftest)
        if args.find:
            pattern = re.compile(args.find)
            names = sorted(entry[:-6].replace("/", ".")
                           for entry in archive.namelist()
                           if entry.endswith(".class") and pattern.search(entry))
            for name in names[:200]:
                print(name)
            if len(names) > 200:
                print(f"... всего {len(names)}")
            return 0 if names else 1

        if not args.cls:
            print("нужен --class (или --find)", file=sys.stderr)
            return 1

        entry = args.cls.replace(".", "/") + ".class"
        if entry not in archive.namelist():
            print(f"CLASS_NOT_FOUND: {args.cls}", file=sys.stderr)
            return 1
        pool, name, super_name, methods, fields = parse_class(archive.read(entry))
        print(f"// {name}" + (f" extends {super_name}" if super_name else ""))

        if args.list:
            for info in sorted(methods, key=lambda m: (m.name, m.descriptor)):
                print(f"  method {info.descriptor} {info.name}  [{flag_text(info.flags)}]")
            if args.fields:
                for flags, field_name, desc in sorted(fields, key=lambda f: f[1]):
                    print(f"  field  {desc} {field_name}  [{flag_text(flags)}]")
            return 0

        selected = [info for info in methods
                    if (args.exact and info.name == args.method
                        or not args.exact and args.method and args.method in info.name
                        or not args.method)
                    and (not args.desc or args.desc in info.descriptor)]
        if not selected:
            print(f"METHOD_NOT_FOUND: {args.method!r} в {name}", file=sys.stderr)
            return 1

        printed = 0
        for info in selected:
            body = disassemble(pool, info.code, info.lines if args.lines else [])
            if args.grep and not any(args.grep.lower() in line.lower() for line in body):
                continue
            printed += 1
            print(f"\n  {info.descriptor} {name}.{info.name}  [{flag_text(info.flags)}]")
            if not info.code:
                print("    <нет кода: abstract/native>")
                continue
            for line in body:
                print(line)
        if args.grep and printed == 0:
            print(f"GREP_NOT_FOUND: {args.grep!r} в методах {name}", file=sys.stderr)
            return 1
    return 0


if __name__ == "__main__":
    sys.exit(main())
