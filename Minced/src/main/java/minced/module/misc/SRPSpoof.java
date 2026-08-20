/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.common.ResourcePackStatusC2SPacket
 *  net.minecraft.network.packet.c2s.common.ResourcePackStatusC2SPacket$Status
 *  net.minecraft.network.packet.s2c.common.ResourcePackSendS2CPacket
 */
package minced.module.misc;
import minced.core.EventHandler;

import minced.core.EventPacket;
import minced.core.Module;
import minced.enums.Combat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.common.ResourcePackStatusC2SPacket;
import net.minecraft.network.packet.s2c.common.ResourcePackSendS2CPacket;

public class SRPSpoof
extends Module {
    static String field5637 = "SRP Spoof";
    static String field5638 = "Подменяет данные о том, что установлен серверный ресурс-пак";
    static MinecraftClient I = MinecraftClient.getInstance();

    public SRPSpoof() {
        super(field5637, field5638, Combat.Miscellaneous);
    }

    @EventHandler(2)
    public void O(EventPacket param1) {
        if (param1.method2467() instanceof ResourcePackSendS2CPacket && param1.method2466()) {
            param1.method2298(true);
            SRPSpoof.I.player.networkHandler.sendPacket((Packet)new ResourcePackStatusC2SPacket(SRPSpoof.I.player.getUuid(), ResourcePackStatusC2SPacket.Status.ACCEPTED));
            SRPSpoof.I.player.networkHandler.sendPacket((Packet)new ResourcePackStatusC2SPacket(SRPSpoof.I.player.getUuid(), ResourcePackStatusC2SPacket.Status.SUCCESSFULLY_LOADED));
        }
    }
}



