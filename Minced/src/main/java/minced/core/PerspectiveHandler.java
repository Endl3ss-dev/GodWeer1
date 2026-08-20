/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 */
package minced.core;

import minced.core.CancellableEvent;
import minced.core.EventGameJoin;
import minced.core.Minced;
import minced.manager.Account;
import net.minecraft.client.MinecraftClient;

public class PerspectiveHandler
implements CancellableEvent {
    static MinecraftClient I = MinecraftClient.getInstance();

    @EventHandler(2)
    public void method2555(EventGameJoin param1) {
        if (PerspectiveHandler.I.player == null) {
            return;
        }
        Account v2 = Minced.getInstance().getAccountManager();
        if (v2 == null) {
            return;
        }
        String v3 = PerspectiveHandler.I.player.getName().getString();
        v2.updateLastServerAddress(v3, param1.method2398());
    }
}



