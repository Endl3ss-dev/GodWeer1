/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.item.Items
 */
package minced.module.player;
import minced.core.EventHandler;

import minced.core.BooleanSetting;
import minced.core.SliderSetting;
import minced.core.EventUpdate;
import minced.core.ServerDetector;
import minced.core.Module;
import minced.enums.Combat;
import minced.mixin.accessor.IMinecraftClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;

public class FastExp
extends Module {
    static String field4322 = "Fast Exp";
    static String field4323 = "Позволяет быстро бросать пузырьки опыта";
    static String field4324 = "Только без пвп режима";
    BooleanSetting field4325 = new BooleanSetting(field4324, true);
    static String field4326 = "Задержка";
    static float field4327 = 3.0f;
    SliderSetting field4328 = new SliderSetting(field4326, 1.0f, 0.0f, field4327, 1.0f);
    static MinecraftClient I = MinecraftClient.getInstance();

    public FastExp() {
        super(field4322, field4323, Combat.Player);
        this.addSettings(this.field4325, this.field4328);
    }

    @EventHandler(2)
    public void method1101(EventUpdate param1) {
        if (((Boolean)this.field4325.getValue()).booleanValue() && ServerDetector.method2682()) {
            return;
        }
        if (FastExp.I.player.getMainHandStack().getItem() == Items.EXPERIENCE_BOTTLE) {
            ((IMinecraftClient)I).itemUseCooldown(((Float)this.field4328.getValue()).intValue());
        }
    }
}



