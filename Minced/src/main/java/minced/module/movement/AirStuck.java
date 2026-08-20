/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
 */
package minced.module.movement;
import minced.core.EventHandler;

import minced.core.EventSwapWorld;
import minced.core.EventPacket;
import minced.core.EventMotion;
import minced.core.EventTravel;
import minced.core.Module;
import minced.enums.Combat;
import minced.enums.LOAD;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class AirStuck
extends Module {
    static MinecraftClient client = MinecraftClient.getInstance();
    static String MODULE_NAME;
    static String MODULE_DESCRIPTION;

    @EventHandler(2)
    public void onPacket(EventPacket param1) {
        if (AirStuck.client.player == null) {
            return;
        }
        if (param1.method2467() instanceof PlayerMoveC2SPacket) {
            param1.method2298(true);
        }
    }

    public AirStuck() {
        super(MODULE_NAME, MODULE_DESCRIPTION, Combat.Movement);
    }

    @EventHandler(2)
    public void onMotion(EventMotion param1) {
        param1.method2505(0.0);
        param1.method2298(true);
    }

    @EventHandler(2)
    public void onTravel(EventTravel param1) {
        if (param1.method2611() == AirStuck.client.player) {
            param1.method2298(true);
        }
    }

    @EventHandler(2)
    public void onWorldChange(EventSwapWorld param1) {
        if (param1.field8581 == LOAD.CHANGED) {
            this.toggle();
        }
    }

    static {
        MODULE_NAME = "Air Stuck";
        MODULE_DESCRIPTION = "Позволяет зависать в воздухе";
    }
}



