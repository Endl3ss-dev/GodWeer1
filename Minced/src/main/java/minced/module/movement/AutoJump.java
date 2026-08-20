/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.entity.effect.StatusEffects
 */
package minced.module.movement;
import minced.core.EventHandler;

import java.util.function.BooleanSupplier;
import minced.core.MultiBooleanSetting;
import minced.core.BooleanSetting;
import minced.core.EventUpdate;
import minced.core.Minced;
import minced.core.Module;
import minced.enums.Combat;
import minced.module.combat.AttackAura;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffects;

public class AutoJump
extends Module {
    static String field4420;
    static String field4421;
    static String field4422;
    static MultiBooleanSetting field4423;
    static MinecraftClient I = MinecraftClient.getInstance();
    static String field4424;
    static String field4425;
    static String field4426;
    static String field4427;

    public static void method1112() {
        field4423 = new MultiBooleanSetting(field4420, new BooleanSetting(field4421, false), new BooleanSetting(field4422, true));
    }

    @EventHandler(2)
    public void method1113(EventUpdate param1) {
        AttackAura v3;
        if (!AutoJump.I.player.isOnGround() || AutoJump.I.options.jumpKey.isPressed()) {
            return;
        }
        boolean v2 = false;
        if (field4423.method1898(field4424).booleanValue() && (v3 = (AttackAura)Minced.getInstance().getModuleManager().method187(AttackAura.class)).isEnabled() && AttackAura.method861() != null) {
            v2 = true;
        }
        if (field4423.method1898(field4425).booleanValue() && AutoJump.I.player.hasStatusEffect(StatusEffects.SLOWNESS)) {
            v2 = true;
        }
        if (v2) {
            AutoJump.I.player.jump();
        }
    }

    public AutoJump() {
        super(field4426, field4427, Combat.Movement);
        this.addSettings(field4423);
    }

    static {
        field4426 = "Auto Jump";
        field4427 = "Автоматически прыгает";
        field4424 = "Активна Attack Aura";
        field4425 = "Активно зелье замедления";
        field4420 = "Прыгать если";
        field4421 = "Активна Attack Aura";
        field4422 = "Активно зелье замедления";
        AutoJump.method1112();
    }
}



