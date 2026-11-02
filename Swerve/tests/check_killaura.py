#!/usr/bin/env python3
"""Статические регрессии KillAura (баги, найденные в игре).

Проверка не заменяет запуск клиента: она ловит именно те поломки, которые
не видно компилятору, но видно игроку. Каждая проверка соответствует пункту
задачи и падает, если правку откатили или продублировали старую ветку.

Что проверяется
  1. Камера свободного обзора повторяет ванильный множитель Entity.turn (0.15),
     иначе мышь крутит камеру в 6.67 раза быстрее.
  2. Удар 1.9 ждёт полную перезарядку через getAttackStrengthScale, а не
     захардкоженные миллисекунды Private.
  3. «Только криты» не имеют наземных обходов: critWindow требует !onGround,
     в KillAura нет verticalCollision/smart-критов и случайного пропуска гейта.
  4. Режимы 1.8 и 1.9 различаются: 1.8 — CPS без перезарядки и swing→attack,
     1.9 — перезарядка и attack→swing.
  5. Ручной режим существует отдельной настройкой и выключает автобой.
  6. Ни один удар не обходит гейты: attackEntity вызывается только из
     updateAttack/breakShield, отложенных ударов нет.
  7. Пакетный PerfectDelay удалён: свои же swing-пакеты больше не откладывают
     следующий удар.

Использование:
    python3 Swerve/tests/check_killaura.py

Код возврата: 0 — чисто, 1 — есть находки.
"""
from __future__ import annotations

import json
import re
import sys
from pathlib import Path

SWERVE = Path(__file__).resolve().parents[1]
JAVA = SWERVE / "src/main/java/swerve"
KILL_AURA = JAVA / "modules/impl/combat/KillAura.java"
FREE_LOOK = JAVA / "utils/rotation/FreeLook.java"
MIXIN_CONFIG = SWERVE / "src/main/resources/swerve.mixins.json"

PROBLEMS: list[str] = []


def fail(message: str) -> None:
    PROBLEMS.append(message)


def read(path: Path) -> str:
    if not path.is_file():
        fail(f"нет файла {path.relative_to(SWERVE)}")
        return ""
    return path.read_text(encoding="utf-8")


def strip_comments(text: str) -> str:
    """Удаляет комментарии, чтобы примеры «как было сломано» не считались кодом."""
    text = re.sub(r"/\*.*?\*/", " ", text, flags=re.S)
    return re.sub(r"//[^\n]*", " ", text)


def compact(text: str) -> str:
    """Убирает комментарии и пробелы: проверки не зависят от форматирования."""
    return re.sub(r"\s+", " ", strip_comments(text))


def method_body(source: str, signature: str) -> str:
    """Тело метода по началу сигнатуры (без вложенных методов в этом файле нет)."""
    start = source.find(signature)
    if start < 0:
        return ""
    brace = source.find("{", start)
    if brace < 0:
        return ""
    depth = 0
    for index in range(brace, len(source)):
        char = source[index]
        if char == "{":
            depth += 1
        elif char == "}":
            depth -= 1
            if depth == 0:
                return source[brace:index + 1]
    return ""


def check_camera(free_look: str) -> None:
    if "VANILLA_TURN_SCALE = 0.15" not in compact(free_look):
        fail("FreeLook: множитель Entity.turn (0.15) не задан — камера снова будет в 6.67 раза быстрее")
    body = compact(method_body(free_look, "public static void turn("))
    if not body:
        fail("FreeLook.turn: метод не найден")
        return
    if body.count("VANILLA_TURN_SCALE") < 2:
        fail("FreeLook.turn: множитель 0.15 применён не к обеим осям")
    if "MAX_FOLLOW_DEG_PER_SECOND" not in compact(free_look):
        fail("FreeLook.follow: нет потолка угловой скорости догона камеры")


def check_cooldown(kill_aura: str, compact_aura: str) -> None:
    ready = compact(method_body(kill_aura, "private boolean attackReady("))
    if not ready:
        fail("KillAura.attackReady: метод не найден")
        return
    if "getAttackStrengthScale(0.5F)" not in ready:
        fail("KillAura.attackReady: 1.9 не использует ванильную шкалу перезарядки")
    if "1.0F" not in ready:
        fail("KillAura.attackReady: полная перезарядка не требуется (нужно >= 1.0F)")
    for legacy in ("dynamicCooldown", "baseCooldown", "COOLDOWN_DYNAMIC", "PerfectDelay"):
        if legacy in compact_aura:
            fail(f"KillAura: остался старый кулдаун/таймер {legacy}")


def check_only_crits(compact_aura: str) -> None:
    crit = compact(method_body(compact_aura.replace("&& ", "&& "), "private boolean critWindow("))
    # Ищем по компактной копии, поэтому сигнатура тоже компактная.
    crit = compact(method_body(compact_aura, "private boolean critWindow("))
    if not crit:
        fail("KillAura.critWindow: метод не найден")
        return
    if "!player.onGround()" not in crit:
        fail("KillAura.critWindow: нет обязательного условия !onGround — возможен удар с земли")
    for forbidden in ("verticalCollision", "smartCrits", "canSmartCrit"):
        if forbidden in compact_aura:
            fail(f"KillAura: найден наземный обход критов ({forbidden})")

    should = compact(method_body(compact_aura, "private boolean shouldAttack("))
    if not should:
        fail("KillAura.shouldAttack: метод не найден")
        return
    normalised = should.replace(" ", "")
    if "onlyCrits.get()" not in normalised:
        fail("KillAura.shouldAttack: не проверяет настройку «Только криты»")
    if "critWindow(" not in normalised:
        fail("KillAura.shouldAttack: не требует окно крита")
        return
    before = normalised.split("critWindow(", 1)[0]
    # Единственный допустимый ранний выход — guard выключенной настройки.
    if "returntrue" in before.replace("if(!onlyCrits.get()){returntrue;}", ""):
        fail("KillAura.shouldAttack: возвращает true до проверки окна крита")
    for bypass in ("isFallFlying()", "isInLiquid("):
        if bypass in before:
            fail(f"KillAura.shouldAttack: удар разрешён до критов через {bypass}")


def check_modes(compact_aura: str) -> None:
    interval = compact(method_body(compact_aura, "private boolean clickIntervalReady("))
    if "ATTACK_18" not in interval or "dynamicCps" not in interval:
        fail("KillAura.clickIntervalReady: режим 1.8 не ограничен CPS")
    attack = compact(method_body(compact_aura, "private void attackEntity("))
    if not attack:
        fail("KillAura.attackEntity: метод не найден")
        return
    if "ATTACK_18" not in attack:
        fail("KillAura.attackEntity: режимы 1.8/1.9 не различаются порядком удара")
    if "getAttackStrengthScale(0.5F)" not in attack:
        fail("KillAura.attackEntity: в 1.9 нет страховки полной перезарядки")


def check_manual_and_gates(compact_aura: str) -> None:
    if "manualAttack" not in compact_aura:
        fail("KillAura: нет отдельной настройки ручных ударов")
        return
    update = compact(method_body(compact_aura, "private void updateAttack("))
    if "if(manualAttack.get())" not in update.replace(" ", ""):
        fail("KillAura.updateAttack: ручной режим не выключает автобой")
    if update.count("attackEntity(") != 1:
        fail("KillAura.updateAttack: удар вызывается в обход общего гейта")
    on_tick = compact(method_body(compact_aura, "public void onTick("))
    if "attackEntity(" in on_tick:
        fail("KillAura.onTick: отложенный удар в обход гейтов вернулся")
    if "pendingCritTicks" in compact_aura:
        fail("KillAura: остался pendingCritTicks (удар в обход гейтов)")


def check_packet_delay() -> None:
    if (JAVA / "utils/time/PerfectDelay.java").exists():
        fail("utils/time/PerfectDelay.java: пакетная задержка ударов должна быть удалена")
    if (JAVA / "events/network/PacketSendEvent.java").exists():
        fail("events/network/PacketSendEvent.java: событие больше не используется")
    if (JAVA / "mixins/network/MixinConnection.java").exists():
        fail("mixins/network/MixinConnection.java: перехват sendPacket больше не нужен")
    try:
        config = json.loads(MIXIN_CONFIG.read_text(encoding="utf-8"))
    except Exception as error:  # noqa: BLE001 - сообщение важнее типа
        fail(f"swerve.mixins.json не читается: {error}")
        return
    if "network.MixinConnection" in config.get("client", []):
        fail("swerve.mixins.json: остался network.MixinConnection")


def main() -> int:
    kill_aura = read(KILL_AURA)
    free_look = read(FREE_LOOK)
    compact_aura = compact(kill_aura)

    if not kill_aura or not free_look:
        for problem in PROBLEMS:
            print(problem)
        return 1

    check_camera(free_look)
    check_cooldown(kill_aura, compact_aura)
    check_only_crits(compact_aura)
    check_modes(compact_aura)
    check_manual_and_gates(compact_aura)
    check_packet_delay()

    for problem in PROBLEMS:
        print(problem)
    print(f"найдено: {len(PROBLEMS)}")
    return 1 if PROBLEMS else 0


if __name__ == "__main__":
    sys.exit(main())
