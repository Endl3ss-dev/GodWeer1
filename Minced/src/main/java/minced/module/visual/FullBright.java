/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.entity.effect.StatusEffectInstance
 *  net.minecraft.entity.effect.StatusEffects
 */
package minced.module.visual;
import minced.core.EventHandler;

import minced.core.EventUpdate;
import minced.core.Module;
import minced.enums.Combat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class FullBright
extends Module {
    static MinecraftClient I = MinecraftClient.getInstance();
    static String field3307;
    static String field3308;
    static int field3309;

    @Override
    public void onDisable() {
        FullBright.I.player.removeStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION).getEffectType());
        super.onDisable();
    }

    public FullBright() {
        super(field3307, field3308, Combat.Visuals);
    }

    @EventHandler(2)
    public void method925(EventUpdate param1) {
        FullBright.I.player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, field3309, 1));
    }

    static {
        field3307 = "Full Bright";
        field3308 = "Освещает мир вокруг";
        field3309 = 9999999;
    }
}



