/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 */
package minced.module.movement;
import minced.core.EventHandler;

import java.util.function.BooleanSupplier;
import minced.core.EventUpdate;
import minced.core.Module;
import minced.enums.Combat;
import net.minecraft.client.MinecraftClient;

public class AirJump
extends Module {
    static String MODULE_NAME = "Air Jump";
    static String MODULE_DESCRIPTION = "Позволяет выполнять многократные прыжки в воздухе";
    boolean jumpKeyPreviouslyPressed = false;
    static MinecraftClient client = MinecraftClient.getInstance();

    public AirJump() {
        super(MODULE_NAME, MODULE_DESCRIPTION, Combat.Movement);
    }

    @EventHandler(2)
    public void onUpdate(EventUpdate param1) {
        boolean v2 = AirJump.client.options.jumpKey.isPressed();
        if (v2 && !this.jumpKeyPreviouslyPressed) {
            AirJump.client.player.jump();
        }
        this.jumpKeyPreviouslyPressed = v2;
    }
}



