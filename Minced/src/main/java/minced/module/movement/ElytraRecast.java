/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EquipmentSlot
 *  net.minecraft.item.Items
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket
 *  net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket$Mode
 */
package minced.module.movement;
import minced.core.EventHandler;

import minced.core.EventInput;
import minced.core.Module;
import minced.enums.Combat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;

public class ElytraRecast
extends Module {
    static String field4560 = "Elytra Recast";
    static String field4561 = "Автоматически прожимает кнопку пробела, когда вы на элитре";
    static MinecraftClient I = MinecraftClient.getInstance();

    public ElytraRecast() {
        super(field4560, field4561, Combat.Movement);
    }

    @EventHandler(2)
    public void method1143(EventInput param1) {
        if (ElytraRecast.I.player.getEquippedStack(EquipmentSlot.CHEST).getItem().equals(Items.ELYTRA)) {
            if (ElytraRecast.I.player.isOnGround()) {
                param1.method1974(true);
            } else if (!ElytraRecast.I.player.isGliding()) {
                ElytraRecast.I.player.networkHandler.sendPacket((Packet)new ClientCommandC2SPacket((Entity)ElytraRecast.I.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
                ElytraRecast.I.player.startGliding();
            }
        }
    }
}



