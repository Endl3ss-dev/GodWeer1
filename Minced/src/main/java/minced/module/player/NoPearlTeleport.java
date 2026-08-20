/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket
 *  net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket
 *  net.minecraft.util.math.Vec3d
 */
package minced.module.player;
import minced.core.EventHandler;

import java.util.function.BooleanSupplier;
import minced.core.BooleanSetting;
import minced.core.SliderSetting;
import minced.core.EventPacket;
import minced.core.Module;
import minced.enums.Combat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.Vec3d;

public class NoPearlTeleport
extends Module {
    static MinecraftClient I = MinecraftClient.getInstance();
    BooleanSetting field4562 = new BooleanSetting(field4566, true);
    SliderSetting field4563 = new SliderSetting(field4567, field4568, field4569, field4570, 1.0f, this.field4562::getValue);
    static String field4564;
    static String field4565;
    static String field4566;
    static String field4567;
    static float field4568;
    static float field4569;
    static float field4570;

    @EventHandler(2)
    private void method1144(EventPacket param1) {
        PlayerPositionLookS2CPacket v2;
        if (!param1.method2466() || NoPearlTeleport.I.player == null) {
            return;
        }
        Packet v3 = param1.method2467();
        if (v3 instanceof PlayerPositionLookS2CPacket) {
            v2 = (PlayerPositionLookS2CPacket)v3;
            if (((Boolean)this.field4562.getValue()).booleanValue()) {
                Vec3d v3s = v2.change().position();
                if (NoPearlTeleport.I.player.getPos().distanceTo((Vec3d)v3s) > (double)((Float)this.field4563.getValue()).floatValue()) {
                    return;
                }
            }
        } else {
            return;
        }
        param1.method2298(true);
        NoPearlTeleport.I.player.networkHandler.sendPacket((Packet)new TeleportConfirmC2SPacket(v2.teleportId()));
    }

    public NoPearlTeleport() {
        super(field4564, field4565, Combat.Player);
        this.addSettings(this.field4562, this.field4563);
    }

    static {
        field4564 = "No Pearl Teleport";
        field4565 = "Отменяет телепорт от перла";
        field4566 = "Проверка дистанции";
        field4567 = "Макс. дистанция";
        field4568 = 64.0f;
        field4569 = 8.0f;
        field4570 = 256.0f;
    }
}



