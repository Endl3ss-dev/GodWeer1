/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 */
package minced.module.player;
import minced.core.EventHandler;

import java.util.function.BooleanSupplier;
import minced.core.SliderSetting;
import minced.core.EventBlockBreak;
import minced.core.Module;
import minced.enums.Combat;
import minced.mixin.accessor.IClientPlayerInteractionManagerMixin;
import net.minecraft.client.MinecraftClient;

public class FastBreak
extends Module {
    static MinecraftClient I = MinecraftClient.getInstance();
    SliderSetting field5657 = new SliderSetting(field5660, field5661, field5662, 1.0f, field5663);
    static String field5658 = "Fast Break";
    static String field5659 = "Позволяет быстрее ломать блоки";
    static String field5660 = "Скорость";
    static float field5661 = 0.8f;
    static float field5662 = 0.1f;
    static float field5663 = 0.1f;

    @EventHandler(2)
    public void cfr_underscore(EventBlockBreak param1) {
        if (FastBreak.I.player.isCreative()) {
            return;
        }
        ((IClientPlayerInteractionManagerMixin)FastBreak.I.interactionManager).setblockBreakingCooldown(0);
        if (((IClientPlayerInteractionManagerMixin)FastBreak.I.interactionManager).getcurrentBreakingProgress() > ((Float)this.field5657.getValue()).floatValue()) {
            ((IClientPlayerInteractionManagerMixin)FastBreak.I.interactionManager).setcurrentBreakingProgress(1.0f);
        }
    }

    public FastBreak() {
        super(field5658, field5659, Combat.Player);
        this.addSettings(this.field5657);
    }
}



