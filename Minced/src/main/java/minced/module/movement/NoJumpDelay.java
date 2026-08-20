/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 */
package minced.module.movement;
import minced.core.EventHandler;

import minced.core.EventUpdate;
import minced.core.Module;
import minced.enums.Combat;
import minced.mixin.accessor.ILivingEntityMixin;
import net.minecraft.client.MinecraftClient;

public class NoJumpDelay
extends Module {
    static MinecraftClient I = MinecraftClient.getInstance();
    static String field4369;
    static String field4370;

    @EventHandler(2)
    public void method1107(EventUpdate param1) {
        ((ILivingEntityMixin)NoJumpDelay.I.player).setJumpCooldown(0);
    }

    public NoJumpDelay() {
        super(field4369, field4370, Combat.Movement);
    }

    static {
        field4369 = "No Jump Delay";
        field4370 = "Убирает задержку между прыжками, позволяя прыгать быстрее";
    }
}



