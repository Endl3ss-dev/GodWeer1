/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket
 */
package minced.module.misc;
import minced.core.EventHandler;

import minced.core.EventPacket;
import minced.core.Module;
import minced.enums.Combat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;

public class InventoryPlus
extends Module {
    static String field4571 = "Inventory Plus";
    static String field4572 = "Добовляет дополнительные 4 слота в инвентаре, увеличивая его вместимость";
    static MinecraftClient I = MinecraftClient.getInstance();

    public InventoryPlus() {
        super(field4571, field4572, Combat.Miscellaneous);
    }

    @EventHandler(2)
    public void method1145(EventPacket param1) {
        if (InventoryPlus.I.player == null) {
            return;
        }
        if (param1.method2467() instanceof CloseHandledScreenC2SPacket && param1.method2465()) {
            param1.method2298(true);
        }
    }
}



