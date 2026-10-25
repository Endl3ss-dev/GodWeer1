#!/usr/bin/env python3
"""Static API check for Swerve against the real Minecraft 1.21.4 bytecode.

Why this exists: the sandbox (and CI without a Gradle cache) cannot always run
`./gradlew compileJava`. This script answers the question "does every Minecraft
symbol we reference actually exist in 1.21.4?" without a JDK, by reading the
merged Minecraft jar that Loom already downloaded.

Checks performed
  1. Imports of net.minecraft.* / com.mojang.blaze3d.* resolve to real classes.
  2. @Mixin targets resolve to real classes.
  3. Every mixin injector (Inject/Redirect/ModifyArg/ModifyReturnValue/
     WrapOperation/ModifyVariable/Accessor/Invoker) names a member that exists
     in the target class hierarchy, with a matching descriptor when given.
     `@Shadow` fields additionally must be declared by the target class itself:
     mixin does not resolve inherited fields and fails at load otherwise.
  4. Inject / ModifyReturnValue handler signatures bind to the target: the
     handler may capture any prefix of the target parameters but must end with
     CallbackInfo (void target) or CallbackInfoReturnable (non-void target).
     A mismatch compiles fine and then fails at mod load, so it is checked here.
  5. Every @At target descriptor (INVOKE/FIELD/NEW) resolves to a real member.
  6. Static calls on imported game classes (`Mth`, `Minecraft`, ...) name
     members that exist, nested types included.
  7. Every class listed in swerve.mixins.json exists, and every mixin file is
     listed (an unlisted mixin silently does nothing).
  8. Swerve sources: files match their declared class name; imports of other
     swerve.* classes resolve to a real file; static calls on swerve classes
     name a method that the class actually declares.
  9. Braces/parens balance per file (catches truncated files).

Not covered (stated so the report stays honest): instance-typed expressions
(`mc.player.foo()`), generic bounds, Java language-level errors (missing
`return`, unreachable code) and any runtime behaviour. Those still need
`./gradlew compileJava` and a launch test.

Usage (from anywhere):
    python3 Swerve/tests/check_mc_api.py
    python3 Swerve/tests/check_mc_api.py --jar path/to/minecraft.jar -v

Exit code 0 = clean, 1 = findings, 2 = the check could not run (no jar).
It is a static check, not a launch test: it cannot see runtime behaviour.
"""
from __future__ import annotations

import argparse
import json
import re
import struct
import sys
from pathlib import Path

SWERVE = Path(__file__).resolve().parents[1]
JAVA_ROOT = SWERVE / "src/main/java"
MIXIN_CONFIG = SWERVE / "src/main/resources/swerve.mixins.json"
# Loom keeps the merged Minecraft jar here; the folder name carries the mapping hash.
JAR_GLOB = ".gradle/loom-cache/minecraftMaven/net/minecraft/**/minecraft-merged-*.jar"

# Packages that live in the merged jar and therefore must resolve exactly.
GAME_PACKAGES = ("net.minecraft.", "com.mojang.blaze3d.")

# Mixin annotations whose `method` member names something inside the target class.
MEMBER_INJECTORS = (
    "Inject",
    "Redirect",
    "ModifyArg",
    "ModifyArgs",
    "ModifyArgReturnValue",
    "ModifyReturnValue",
    "ModifyExpressionValue",
    "ModifyReceiver",
    "ModifyVariable",
    "WrapOperation",
    "WrapWithCondition",
    "WrapMethod",
)

DESCRIPTOR_SIGNATURE = re.compile(r"^[\w$<>]+\(.*\).+$")
# One mixin method reference: a name plus an optional JVM descriptor. A ';'-separated
# list of them survives this regex, while a descriptor containing ';' stays in one piece.
METHOD_SPEC = re.compile(r"[\w$<>]+(?:\((?:[^()]*)\)[^\s;]*)?")


# --------------------------------------------------------------------------- #
# Class file parsing
# --------------------------------------------------------------------------- #
class ClassFile:
    """The three pieces of a class file this checker needs."""

    __slots__ = ("name", "super_name", "interfaces", "methods", "fields")

    def __init__(self, name, super_name, interfaces, methods, fields):
        self.name = name
        self.super_name = super_name
        self.interfaces = interfaces
        self.methods = methods  # set of (name, descriptor)
        self.fields = fields  # set of (name, descriptor)


def _read_constant_pool(data, offset):
    """Return (pool, new_offset); pool indexes to (tag, value) for what we need."""
    (count,) = struct.unpack_from(">H", data, offset)
    offset += 2
    pool = [None] * count
    index = 1
    while index < count:
        tag = data[offset]
        offset += 1
        if tag == 1:  # Utf8
            (length,) = struct.unpack_from(">H", data, offset)
            offset += 2
            pool[index] = ("utf8", data[offset:offset + length].decode("utf-8", "replace"))
            offset += length
        elif tag == 7:  # Class
            (name_index,) = struct.unpack_from(">H", data, offset)
            offset += 2
            pool[index] = ("class", name_index)
        elif tag == 8:  # String
            (string_index,) = struct.unpack_from(">H", data, offset)
            offset += 2
            pool[index] = ("string", string_index)
        elif tag in (15,):  # MethodHandle
            offset += 3
            pool[index] = ("mh", None)
        elif tag in (16, 19, 20):  # MethodType, Module, Package
            offset += 2
            pool[index] = ("ref", None)
        elif tag in (3, 4):  # Integer, Float
            offset += 4
            pool[index] = ("num", None)
        elif tag in (5, 6):  # Long, Double take two slots
            offset += 8
            pool[index] = ("num", None)
            index += 1
        elif tag in (9, 10, 11, 12, 17, 18):  # Field/Method/IMethod/NameAndType/Dynamic/InvokeDynamic
            offset += 4
            pool[index] = ("ref", None)
        else:
            raise ValueError(f"unknown constant pool tag {tag}")
        index += 1
    return pool, offset


def _utf8(pool, index):
    if index <= 0 or index >= len(pool) or pool[index] is None:
        return None
    tag, value = pool[index]
    if tag == "utf8":
        return value
    if tag == "class":
        return _utf8(pool, value)
    return None


def _skip_attributes(data, offset):
    (count,) = struct.unpack_from(">H", data, offset)
    offset += 2
    for _ in range(count):
        (name_index,) = struct.unpack_from(">H", data, offset)
        (length,) = struct.unpack_from(">I", data, offset + 2)
        offset += 6 + length
    return offset


def parse_class_file(data):
    if data[:4] != b"\xca\xfe\xba\xbe":
        raise ValueError("not a class file")
    pool, offset = _read_constant_pool(data, 8)
    offset += 2  # access flags
    (this_class,) = struct.unpack_from(">H", data, offset)
    offset += 2
    (super_class,) = struct.unpack_from(">H", data, offset)
    offset += 2
    (interface_count,) = struct.unpack_from(">H", data, offset)
    offset += 2
    interfaces = []
    for _ in range(interface_count):
        (index,) = struct.unpack_from(">H", data, offset)
        interfaces.append(_utf8(pool, index))
        offset += 2

    members = {}
    for kind in ("fields", "methods"):
        (count,) = struct.unpack_from(">H", data, offset)
        offset += 2
        entries = set()
        for _ in range(count):
            offset += 2  # access flags
            (name_index,) = struct.unpack_from(">H", data, offset)
            (desc_index,) = struct.unpack_from(">H", data, offset + 2)
            offset += 4
            entries.add((_utf8(pool, name_index), _utf8(pool, desc_index)))
            offset = _skip_attributes(data, offset)
        members[kind] = entries

    return ClassFile(
        _utf8(pool, this_class),
        _utf8(pool, super_class),
        [name for name in interfaces if name],
        members["methods"],
        members["fields"],
    )


class ApiIndex:
    """All game classes from the merged jar, with hierarchy-aware lookups."""

    def __init__(self, jar_path):
        import zipfile

        self.classes = {}
        with zipfile.ZipFile(jar_path) as jar:
            for entry in jar.namelist():
                if not entry.endswith(".class") or entry.startswith("META-INF/"):
                    continue
                try:
                    info = parse_class_file(jar.read(entry))
                except Exception:
                    continue
                if info.name:
                    self.classes[info.name] = info
        self._method_cache = {}

    def binary_name(self, internal_name):
        return internal_name.replace("/", ".")

    def has_class(self, binary_name):
        return binary_name.replace(".", "/") in self.classes

    def chain(self, binary_name):
        """The class plus all its supertypes (bounded, since the jar is closed)."""
        internal = binary_name.replace(".", "/")
        seen = set()
        stack = [internal]
        while stack:
            current = stack.pop()
            if current in seen:
                continue
            seen.add(current)
            info = self.classes.get(current)
            if info is None:
                continue
            if info.super_name:
                stack.append(info.super_name)
            stack.extend(info.interfaces)
        return [self.classes[name] for name in seen if name in self.classes]

    def find_methods(self, binary_name, name, descriptor=None):
        key = (binary_name, name, descriptor)
        cached = self._method_cache.get(key)
        if cached is not None:
            return cached
        found = []
        for info in self.chain(binary_name):
            for method_name, method_desc in info.methods:
                if method_name != name:
                    continue
                if descriptor is not None and method_desc != descriptor:
                    continue
                found.append((info.name, method_name, method_desc))
        self._method_cache[key] = found
        return found

    def find_field(self, binary_name, name, descriptor=None):
        for info in self.chain(binary_name):
            for field_name, field_desc in info.fields:
                if field_name != name:
                    continue
                if descriptor is not None and field_desc != descriptor:
                    continue
                return True
        return False

    def declares_field(self, binary_name, name, descriptor=None):
        """Field declared by this exact class, ignoring superclasses.

        `@Shadow` on a field resolves against the target class only: an inherited
        field is invisible to it and the mixin fails at load with
        "`@Shadow` field X was not located in the target class".
        """
        info = self.classes.get(binary_name.replace(".", "/"))
        if info is None:
            return False
        for field_name, field_desc in info.fields:
            if field_name != name:
                continue
            if descriptor is not None and field_desc != descriptor:
                continue
            return True
        return False

# --------------------------------------------------------------------------- #
# Source scanning
# --------------------------------------------------------------------------- #
ANNOTATION = re.compile(r"@(?:\w+\.)*(\w+)\s*\(")
IMPORT = re.compile(r"^import\s+(static\s+)?([\w.]+(?:\.\*)?)\s*;", re.M)
PACKAGE = re.compile(r"^package\s+([\w.]+)\s*;", re.M)
CLASS_DECL = re.compile(r"\b(?:public\s+|final\s+|abstract\s+|strictfp\s+)*(?:class|interface|enum|record|@interface)\s+(\w+)")
MIXIN_TARGET = re.compile(r"@Mixin\s*\(([^)]*)\)", re.S)
TARGET_DESCRIPTOR = re.compile(r"L([\w/$]+);([\w$<>]+)(\([^)]*\)[^\s\"]+|:[^\s\"]+)")


def strip_comments(source):
    """Remove // and /* */ comments while keeping string literals intact."""
    out = []
    index = 0
    length = len(source)
    while index < length:
        char = source[index]
        if char == '"':
            out.append(char)
            index += 1
            while index < length:
                out.append(source[index])
                if source[index] == "\\":
                    index += 1
                    if index < length:
                        out.append(source[index])
                elif source[index] == '"':
                    index += 1
                    break
                index += 1
            continue
        if char == "'":
            out.append(char)
            index += 1
            while index < length:
                out.append(source[index])
                if source[index] == "\\":
                    index += 1
                    if index < length:
                        out.append(source[index])
                elif source[index] == "'":
                    index += 1
                    break
                index += 1
            continue
        if source.startswith("//", index):
            while index < length and source[index] != "\n":
                index += 1
            continue
        if source.startswith("/*", index):
            end = source.find("*/", index + 2)
            index = length if end < 0 else end + 2
            continue
        out.append(char)
        index += 1
    return "".join(out)


def _skip_literal(text, index):
    """Index just past the string/char literal that starts at `index`."""
    quote = text[index]
    index += 1
    while index < len(text):
        if text[index] == "\\":
            index += 2
            continue
        if text[index] == quote:
            return index + 1
        index += 1
    return index


def _split_top_level(text):
    """Split on commas that are not nested in (), {} or [] and not inside literals."""
    parts = []
    depth = 0
    current = []
    index = 0
    while index < len(text):
        char = text[index]
        if char in "\"'":
            end = _skip_literal(text, index)
            current.append(text[index:end])
            index = end
            continue
        if char in "([{":
            depth += 1
        elif char in ")]}":
            depth -= 1
        if char == "," and depth == 0:
            parts.append("".join(current))
            current = []
            index += 1
            continue
        current.append(char)
        index += 1
    if current:
        parts.append("".join(current))
    return [part.strip() for part in parts if part.strip()]


def annotation_blocks(source):
    """Yield (annotation_name, raw_arguments) for every annotation with arguments."""
    for match in ANNOTATION.finditer(source):
        start = match.end() - 1  # sits on '('
        depth = 0
        index = start
        while index < len(source):
            char = source[index]
            if char in "\"'":
                index = _skip_literal(source, index)
                continue
            if char in "([{":
                depth += 1
            elif char in ")]}":
                depth -= 1
                if depth == 0:
                    yield match.group(1), source[start + 1:index], index + 1
                    break
            index += 1


NAMED_ARGUMENT = re.compile(r"^(\w+)\s*=(?!=)\s*(.*)$", re.S)
LITERALS = re.compile(r'"([^"\\]*(?:\\.[^"\\]*)*)"')


def literals(raw):
    """Every string literal in a raw argument value."""
    return LITERALS.findall(raw)


def string_values(raw):
    """String literals of an argument value, joining `"a" + "b"` concatenations.

    Mixin annotations wrap long descriptors over several lines with `+`, so a
    per-literal read would see fragments. Arrays (`{"a", "b"}`) yield one value
    per element.
    """
    raw = raw.strip()
    if raw.startswith("{") and raw.endswith("}"):
        values = []
        for part in _split_top_level(raw[1:-1]):
            values.extend(string_values(part))
        return values
    found = LITERALS.findall(raw)
    remainder = LITERALS.sub("", raw).replace("+", "").strip()
    if found and not remainder:
        return ["".join(found)]
    return found


def annotation_args(raw):
    """[(name_or_None, raw_value)] for one annotation body, nested values kept whole."""
    result = []
    for part in _split_top_level(raw):
        match = NAMED_ARGUMENT.match(part)
        if match:
            result.append((match.group(1), match.group(2).strip()))
        else:
            result.append((None, part))
    return result


def named_arg(args, name):
    """The raw value of the named argument, or None."""
    for key, value in args:
        if key == name:
            return value
    return None


def positional_arg(args):
    for key, value in args:
        if key is None:
            return value
    return None


PRIMITIVE_TYPES = {
    "void": "V", "boolean": "Z", "byte": "B", "char": "C", "short": "S",
    "int": "I", "long": "J", "float": "F", "double": "D",
}
JAVA_LANG_TYPES = {
    "Object": "java/lang/Object", "String": "java/lang/String",
    "Boolean": "java/lang/Boolean", "Integer": "java/lang/Integer",
    "Float": "java/lang/Float", "Double": "java/lang/Double",
    "Long": "java/lang/Long", "Throwable": "java/lang/Throwable",
    # Mixin handlers routinely capture the exception a target method takes; without
    # these the whole handler was silently skipped instead of verified.
    "Exception": "java/lang/Exception", "RuntimeException": "java/lang/RuntimeException",
    "Error": "java/lang/Error", "IllegalStateException": "java/lang/IllegalStateException",
    "IllegalArgumentException": "java/lang/IllegalArgumentException",
    "NullPointerException": "java/lang/NullPointerException",
    "Class": "java/lang/Class", "Runnable": "java/lang/Runnable", "Thread": "java/lang/Thread",
    "Number": "java/lang/Number", "Character": "java/lang/Character",
}
CALLBACK_TYPES = {
    "CallbackInfo": "Lorg/spongepowered/asm/mixin/injection/callback/CallbackInfo;",
    "CallbackInfoReturnable": "Lorg/spongepowered/asm/mixin/injection/callback/CallbackInfoReturnable;",
}
# Modifiers and the return type come first; the handler name follows. `$` is legal in
# Java identifiers and Swerve uses it for every mixin handler.
HANDLER_FOLLOWS = re.compile(r"(?:[\w.$\[\]]+\s+)+?([\w$]+)\s*\(([^()]*)\)\s*(?:throws[^{;]*)?\{")


def java_type_to_descriptor(type_text, imports):
    """JVM descriptor for a source-level type; None when the type cannot be resolved."""
    text = type_text.strip()
    for prefix in ("final ", "volatile ", "transient "):
        if text.startswith(prefix):
            text = text[len(prefix):].strip()
    dimensions = 0
    while text.endswith("[]"):
        dimensions += 1
        text = text[:-2].strip()
    if text in PRIMITIVE_TYPES:
        base = PRIMITIVE_TYPES[text]
    else:
        base = None
        candidate = text.split("<")[0].strip()
        if candidate in JAVA_LANG_TYPES:
            base = "L" + JAVA_LANG_TYPES[candidate] + ";"
        elif candidate in CALLBACK_TYPES:
            base = CALLBACK_TYPES[candidate]
        elif candidate in imports:
            base = "L" + imports[candidate].replace(".", "/") + ";"
        elif "." in candidate:
            # Nested type written as Outer.Inner: resolve Outer through the imports and
            # join the rest with `$`, which is how the JVM names inner classes.
            outer, *nested = candidate.split(".")
            if outer in imports:
                base = "L" + imports[outer].replace(".", "/") + "$" + "$".join(nested) + ";"
            elif outer in JAVA_LANG_TYPES:
                base = "L" + JAVA_LANG_TYPES[outer] + "$" + "$".join(nested) + ";"
            else:
                base = "L" + candidate.replace(".", "/") + ";"
    if base is None:
        return None
    return "[" * dimensions + base


def skip_trivia(source, index):
    """Advance past whitespace, comments and further annotations."""
    length = len(source)
    while index < length:
        char = source[index]
        if char in " \t\r\n":
            index += 1
        elif source.startswith("//", index):
            newline = source.find("\n", index)
            index = length if newline < 0 else newline + 1
        elif source.startswith("/*", index):
            end = source.find("*/", index + 2)
            index = length if end < 0 else end + 2
        elif char == "@":
            index += re.match(r"@[\w.]+", source[index:]).end()
            probe = index
            while probe < length and source[probe] in " \t\r\n":
                probe += 1
            if probe < length and source[probe] == "(":
                depth = 0
                while probe < length:
                    current = source[probe]
                    if current in "\"'":
                        probe = _skip_literal(source, probe)
                        continue
                    if current == "(":
                        depth += 1
                    elif current == ")":
                        depth -= 1
                        if depth == 0:
                            probe += 1
                            break
                    probe += 1
                index = probe
        else:
            break
    return index


def handler_after(source, offset):
    """(method_name, parameter_list) of the method declared right after an annotation."""
    match = HANDLER_FOLLOWS.match(source, skip_trivia(source, offset))
    if match is None:
        return None, None
    return match.group(1), match.group(2)


def parameter_descriptors(parameters, imports):
    """Descriptor list for a source parameter list, or None if any type is unknown."""
    stripped = parameters.strip()
    if not stripped:
        return []
    descriptors = []
    for part in _split_top_level(stripped):
        part = re.sub(r"^\s*(@[\w.]+(?:\([^)]*\))?\s*)+", "", part).strip()
        part = part.replace("final ", "").strip()
        part = part.rstrip(".")  # varargs: Foo... name
        if "..." in part:
            part = part.replace("...", "[] ")
        pieces = part.rsplit(None, 1)
        if len(pieces) != 2:
            return None
        descriptor = java_type_to_descriptor(pieces[0], imports)
        if descriptor is None:
            return None
        descriptors.append(descriptor)
    return descriptors


def split_descriptor(spec):
    """'render(Lfoo;I)V' -> ('render', '(Lfoo;I)V'); 'tick' -> ('tick', None)."""
    if spec is None:
        return None, None
    if spec.endswith(";"):  # FIELD reference: name:Ltype;
        return spec, None
    if DESCRIPTOR_SIGNATURE.match(spec):
        name, _, descriptor = spec.partition("(")
        return name, "(" + descriptor
    return spec, None


# --------------------------------------------------------------------------- #
# Findings
# --------------------------------------------------------------------------- #
class Report:
    def __init__(self):
        self.findings = []
        self.stats = {}

    def count(self, key, amount=1):
        self.stats[key] = self.stats.get(key, 0) + amount

    def error(self, path, message):
        self.findings.append(("error", str(path), message))

    def warn(self, path, message):
        self.findings.append(("warn", str(path), message))

    def print(self, verbose):
        errors = [item for item in self.findings if item[0] == "error"]
        warns = [item for item in self.findings if item[0] == "warn"]
        for severity, path, message in errors:
            print(f"ERROR {rel(path)}: {message}")
        if verbose:
            for severity, path, message in warns:
                print(f"warn  {rel(path)}: {message}")
        print(f"\n{len(errors)} error(s), {len(warns)} warning(s)")
        return 0 if not errors else 1


def rel(path):
    try:
        return Path(path).relative_to(SWERVE.parent).as_posix()
    except ValueError:
        return str(path)


# --------------------------------------------------------------------------- #
# Checks
# --------------------------------------------------------------------------- #
def check_imports(path, imports, api, report):
    for imported in imports:
        for prefix in GAME_PACKAGES:
            if imported.startswith(prefix) and not imported.endswith(".*"):
                if not api.has_class(imported):
                    report.error(path, f"import {imported} does not exist in the 1.21.4 jar")


def check_mixins(path, source, imports, api, report):
    simple_to_binary = {}
    for imported in imports:
        if not imported.endswith(".*"):
            simple_to_binary[imported.rsplit(".", 1)[-1]] = imported
    package_match = PACKAGE.search(source)
    local_package = package_match.group(1) if package_match else ""

    mixin_classes = []
    for match in MIXIN_TARGET.finditer(source):
        for target in re.findall(r"([\w.$]+)\s*\.\s*class", match.group(1)):
            simple = target.rsplit(".", 1)[-1]
            binary = simple_to_binary.get(simple) or f"{local_package}.{target}"
            if not binary.startswith(GAME_PACKAGES):
                continue  # @Mixin on a Swerve class or an unavailable dependency
            if not api.has_class(binary):
                report.error(path, f"@Mixin target {binary} does not exist in the 1.21.4 jar")
                continue
            mixin_classes.append(binary)

    if not mixin_classes:
        return

    short = ", ".join(sorted(binary.rsplit(".", 1)[-1] for binary in mixin_classes))
    simple_imports = {imported.rsplit(".", 1)[-1]: imported for imported in imports
                      if not imported.endswith(".*")}
    for annotation, raw, offset in annotation_blocks(source):
        args = annotation_args(raw)

        if annotation in MEMBER_INJECTORS:
            value = named_arg(args, "method")
            if value is None:
                continue
            for spec in string_values(value):
                for method_spec in METHOD_SPEC.findall(spec):
                    name, descriptor = split_descriptor(method_spec)
                    if not any(api.find_methods(binary, name, descriptor) for binary in mixin_classes):
                        report.error(path, f"@{annotation} method '{method_spec}' not found in {short}")

            check_callback_signature(path, annotation, value, offset, source, mixin_classes,
                                     api, simple_imports, short, report)

        elif annotation in ("Accessor", "Invoker"):
            value = positional_arg(args) or named_arg(args, "value")
            for member in string_values(value or ""):
                if annotation == "Invoker":
                    if not any(api.find_methods(binary, member) for binary in mixin_classes):
                        report.error(path, f"@Invoker method '{member}' not found in {short}")
                elif not any(api.find_field(binary, member) for binary in mixin_classes):
                    report.error(path, f"@Accessor field '{member}' not found in {short}")

        elif annotation == "At":
            targets = [named_arg(args, "target")] if named_arg(args, "target") else []
            if not targets:
                positional = positional_arg(args)
                if positional and "L" in positional:
                    targets = [positional]
            for target in targets:
                for literal in string_values(target):
                    for owner, member, descriptor in TARGET_DESCRIPTOR.findall(literal):
                        binary = api.binary_name(owner)
                        if not binary.startswith(GAME_PACKAGES):
                            continue
                        if not api.has_class(binary):
                            report.error(path, f"@At target owner {binary} does not exist")
                            continue
                        if descriptor.startswith(":"):
                            field_desc = descriptor[1:] or None
                            if not api.find_field(binary, member, field_desc):
                                report.error(path, f"@At field {binary}.{member}{descriptor} does not exist")
                        elif not api.find_methods(binary, member, descriptor):
                            report.error(path, f"@At method {binary}.{member}{descriptor} does not exist")

    check_shadow_fields(path, strip_comments(source), mixin_classes, api, short, report)


SHADOW = re.compile(r"@Shadow\b")
FIELD_NAME = re.compile(r"([A-Za-z_$][\w$]*)\s*;\s*$")


def check_shadow_fields(path, source, mixin_classes, api, short, report):
    """`@Shadow` fields must be declared by the target class itself.

    Mixin resolves shadowed *fields* against the target class only (shadowed *methods*
    are also looked up in superclasses).  A shadow on an inherited field therefore
    compiles and then fails at load with
    "`@Shadow` field alpha was not located in the target class ...".
    Such a field must be read through an `@Accessor` on the declaring class instead.
    """
    for match in SHADOW.finditer(source):
        tail = source[match.end():]
        stops = [index for index in (tail.find(";"), tail.find("{"), tail.find("(")) if index >= 0]
        stop = min(stops) if stops else -1
        if stop < 0 or tail[stop] != ";":
            continue  # shadowed method (abstract or with a body) — not a field
        name = FIELD_NAME.search(tail[:stop] + ";")
        if not name:
            continue
        field = name.group(1)
        if any(api.declares_field(binary, field) for binary in mixin_classes):
            report.count("shadow fields verified")
        else:
            report.error(path, f"@Shadow field '{field}' is not declared by {short}; "
                               "mixin resolves shadowed fields in the target class only — "
                               "use an @Accessor on the declaring class")


STATIC_CALL = re.compile(r"\b([A-Z]\w*)\s*\.\s*(\w+)\s*\(")


def check_game_members(path, source, imports, api, report):
    """Verify `Imported.member(...)` for game classes imported by simple name.

    A capitalised receiver that matches an import is a type, not a variable by
    Swerve's naming, so an unknown member there is a mapping error. Inherited
    members are accepted (the lookup walks the whole hierarchy).
    """
    simple_to_binary = {}
    for imported in imports:
        if imported.endswith(".*") or not imported.startswith(GAME_PACKAGES):
            continue
        simple_to_binary[imported.rsplit(".", 1)[-1]] = imported
    if not simple_to_binary:
        return
    for match in STATIC_CALL.finditer(source):
        simple, member = match.group(1), match.group(2)
        binary = simple_to_binary.get(simple)
        if binary is None or not api.has_class(binary):
            continue
        report.count("static references checked")
        if api.find_methods(binary, member):
            continue
        # `new Outer.Inner(...)` reads as a call on the outer type; a nested class
        # named like that is legal and simply has no method of its own name.
        if member[:1].isupper() and api.has_class(f"{binary}${member}"):
            report.count("nested types resolved")
            continue
        report.error(path, f"{simple}.{member}(...) does not exist in {binary} (1.21.4)")


def target_signatures(value, mixin_classes, api):
    """[(name, [param descriptors], return descriptor)] for every matching target."""
    signatures = []
    for spec in string_values(value):
        for method_spec in METHOD_SPEC.findall(spec):
            name, descriptor = split_descriptor(method_spec)
            for binary in mixin_classes:
                for _owner, found_name, found_desc in api.find_methods(binary, name, descriptor):
                    params, _, returns = found_desc.partition(")")
                    signatures.append((found_name, _split_params(params[1:]), returns))
    return signatures


def _split_params(raw):
    params = []
    index = 0
    while index < len(raw):
        start = index
        while index < len(raw) and raw[index] == "[":
            index += 1
        if index < len(raw) and raw[index] == "L":
            end = raw.index(";", index) + 1
            params.append(raw[start:end])
            index = end
        else:
            params.append(raw[start:index + 1])
            index += 1
    return params


CALLBACK_INFO = "Lorg/spongepowered/asm/mixin/injection/callback/CallbackInfo;"
CALLBACK_INFO_RETURNABLE = "Lorg/spongepowered/asm/mixin/injection/callback/CallbackInfoReturnable;"


def _captures_prefix(handler, params):
    """Mixin lets a handler capture any prefix of the target's parameters.

    `@Inject` handlers must end with CallbackInfo (void target) or
    CallbackInfoReturnable (non-void target); everything before it is an optional
    prefix of the target parameters, matched position by position.
    """
    if not handler:
        return False
    if handler[-1] not in (CALLBACK_INFO, CALLBACK_INFO_RETURNABLE):
        return False
    captured = handler[:-1]
    return params[:len(captured)] == captured


def check_callback_signature(path, annotation, value, offset, source, mixin_classes,
                             api, imports, short, report):
    """The handler must be bindable to the target method, or Mixin fails at load time.

    That failure compiles cleanly and only appears as a crash on game start, which
    is exactly the class of bug this check exists to catch. Types that cannot be
    resolved from source are skipped instead of guessed.
    """
    if annotation not in ("Inject", "ModifyReturnValue"):
        return
    name, parameters = handler_after(source, offset)
    if name is None:
        return
    handler = parameter_descriptors(parameters, imports)
    if handler is None:
        report.count("callback signatures skipped (unresolved types)")
        return
    signatures = target_signatures(value, mixin_classes, api)
    if not signatures:
        return

    if annotation == "Inject":
        for _target, params, returns in signatures:
            callback = CALLBACK_INFO if returns == "V" else CALLBACK_INFO_RETURNABLE
            report.count("callback signatures considered")
            if _captures_prefix(handler, params) and handler[-1] == callback:
                report.count("callback signatures verified")
                return
        # @Local / custom capture parameters are legal after the prefix; do not guess.
        for _target, params, _returns in signatures:
            captured = handler[:-1]
            if len(captured) > len(params) and captured[:len(params)] == params:
                report.count("callback signatures verified (extra capture params)")
                return
        expected = [params + [CALLBACK_INFO if returns == "V" else CALLBACK_INFO_RETURNABLE]
                    for _target, params, returns in signatures]
        report.error(path, f"@{annotation} handler {name}({parameters}) cannot bind to {short}; "
                           f"one valid form is {expected[0]}")
    else:
        for _target, params, returns in signatures:
            report.count("callback signatures considered")
            if handler[:1] == [returns] and _captures_prefix(params[:0] + handler[1:] + [CALLBACK_INFO], params):
                report.count("callback signatures verified")
                return
            if handler[:1] == [returns] and params[:len(handler) - 1] == handler[1:]:
                report.count("callback signatures verified")
                return
        report.error(path, f"@{annotation} handler {name}({parameters}) does not match target "
                           f"signature in {short}")


def check_mixin_config(source_files, report):
    if not MIXIN_CONFIG.exists():
        report.error(MIXIN_CONFIG, "mixin config missing")
        return
    config = json.loads(MIXIN_CONFIG.read_text(encoding="utf-8"))
    package = config.get("package", "")
    listed = set()
    for key in ("mixins", "client", "server"):
        for entry in config.get(key, []):
            listed.add(f"{package}.{entry}".replace("..", "."))
    existing = set()
    for path in source_files:
        text = path.read_text(encoding="utf-8")
        if "@Mixin" not in text:
            continue
        match = PACKAGE.search(text)
        name_match = CLASS_DECL.search(strip_comments(text))
        if not match or not name_match:
            continue
        existing.add(f"{match.group(1)}.{name_match.group(1)}")
    for missing_file in sorted(listed - existing):
        report.error(MIXIN_CONFIG, f"{missing_file} is listed but has no source file with @Mixin")
    for unlisted in sorted(existing - listed):
        report.error(MIXIN_CONFIG, f"{unlisted} has @Mixin but is not registered in swerve.mixins.json")


def source_exists(fqcn):
    """True when the import names a source file, allowing nested types (a.b.Outer.Inner)."""
    parts = fqcn.split(".")
    for cut in range(len(parts), 0, -1):
        candidate = JAVA_ROOT / ("/".join(parts[:cut]) + ".java")
        if candidate.exists():
            return True
    return False


def is_enum(path):
    return re.search(r"\benum\s+\w+", strip_comments(path.read_text(encoding="utf-8"))) is not None


def check_swerve_sources(source_files, report):
    declared = {}
    method_names = {}
    for path in source_files:
        if path.stem == "package-info":
            continue
        text = path.read_text(encoding="utf-8")
        cleaned = strip_comments(text)
        match = PACKAGE.search(cleaned)
        name_match = CLASS_DECL.search(cleaned)
        if not match or not name_match:
            report.error(path, "no package/class declaration")
            continue
        fqcn = f"{match.group(1)}.{name_match.group(1)}"
        if fqcn in declared:
            report.error(path, f"duplicate class {fqcn} also declared in {rel(declared[fqcn])}")
        declared[fqcn] = path
        method_names[fqcn] = set(re.findall(r"\b(\w+)\s*\(", cleaned))
        if path.stem != name_match.group(1):
            report.error(path, f"file name does not match declared class {name_match.group(1)}")
        for opener, closer, label in (("{", "}", "braces"), ("(", ")", "parens")):
            if cleaned.count(opener) != cleaned.count(closer):
                report.error(path, f"unbalanced {label} ({cleaned.count(opener)} vs {cleaned.count(closer)})")

    declared_names = {fqcn.rsplit(".", 1)[-1]: fqcn for fqcn in declared}
    for path in source_files:
        text = strip_comments(path.read_text(encoding="utf-8"))
        for _static, imported in IMPORT.findall(text):
            if imported.endswith(".*") or not imported.startswith("swerve."):
                continue
            if imported not in declared and not source_exists(imported):
                report.error(path, f"import {imported} has no matching source file")
        # `SomeSwerveType.member(...)` must name a member that type declares itself.
        for owner, member in set(re.findall(r"\b([A-Z]\w*)\.(\w+)\s*\(", text)):
            fqcn = declared_names.get(owner)
            if fqcn is None or owner == path.stem:
                continue
            if member in ("values", "valueOf") and is_enum(declared[fqcn]):
                continue
            if member not in method_names[fqcn]:
                report.error(path, f"{owner}.{member}(...) is not declared in {rel(declared[fqcn])}")


# --------------------------------------------------------------------------- #
def find_jar(explicit):
    if explicit:
        return Path(explicit)
    matches = sorted(SWERVE.glob(JAR_GLOB))
    matches = [path for path in matches if path.stat().st_size > 1_000_000]
    return matches[0] if matches else None


def main():
    parser = argparse.ArgumentParser(description="Static 1.21.4 API check for Swerve")
    parser.add_argument("--jar", help="path to the merged Minecraft jar")
    parser.add_argument("-v", "--verbose", action="store_true", help="also print warnings")
    args = parser.parse_args()

    jar = find_jar(args.jar)
    if jar is None or not jar.exists():
        print("Cannot run: merged Minecraft jar not found (expected under "
              "Swerve/.gradle/loom-cache/minecraftMaven/). Pass --jar explicitly.")
        return 2

    api = ApiIndex(jar)
    if not api.classes:
        print(f"Cannot run: no classes parsed from {jar}")
        return 2

    source_files = sorted(JAVA_ROOT.rglob("*.java"))
    report = Report()
    for path in source_files:
        text = path.read_text(encoding="utf-8")
        imports = [target for _static, target in IMPORT.findall(text)]
        report.count("imports checked", len(imports))
        check_imports(path, imports, api, report)
        check_game_members(path, strip_comments(text), imports, api, report)
        if "@Mixin" in text:
            report.count("mixin classes checked")
            check_mixins(path, text, imports, api, report)
    check_mixin_config(source_files, report)
    check_swerve_sources(source_files, report)

    print(f"Checked {len(source_files)} source files against {len(api.classes)} game classes ({jar.name})")
    for key, value in sorted(report.stats.items()):
        print(f"  {key}: {value}")
    return report.print(args.verbose) if report.findings else 0


if __name__ == "__main__":
    sys.exit(main())
