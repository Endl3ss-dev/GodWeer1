/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  sg.ec.Ср$Ч
 */
package minced.core;

import minced.core.EventKey;
import minced.core.Minced;
import net.minecraft.client.MinecraftClient;
import sg.ec.sr;

public class ClientTicker
{
    static MinecraftClient I = MinecraftClient.getInstance();
    static String field730;

    @EventHandler(2)
    public void method172(EventKey param1) {
        if (ClientTicker.I.player == null || param1.method3599() || ClientTicker.I.world == null) {
            return;
        }
        for (sr.ch v3 : Minced.getInstance().getMacroManager().method1708()) {
            String v4;
            if (v3.l() != param1.method3595() || param1.method3594() || (v4 = v3.Д5()) == null || v4.isEmpty()) continue;
            if (v4.startsWith(field730)) {
                ClientTicker.I.player.networkHandler.sendChatCommand(v4.substring(1));
                continue;
            }
            ClientTicker.I.player.networkHandler.sendChatMessage(v4);
        }
    }

    static {
        field730 = "/";
    }
}



