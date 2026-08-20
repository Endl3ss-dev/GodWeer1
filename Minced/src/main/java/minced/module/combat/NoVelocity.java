/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
 *  net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket
 *  net.minecraft.network.packet.s2c.play.ExplosionS2CPacket
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 */
package minced.module.combat;
import minced.core.EventHandler;

import minced.core.BooleanSetting;
import minced.core.EventInput;
import minced.core.ModeSetting;
import minced.core.EventVelocity;
import minced.core.EventUpdate;
import minced.core.EventPacket;
import minced.core.VelocityUtil;
import minced.core.Minced;
import minced.core.Module;
import minced.enums.Combat;
import minced.module.movement.Speed;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class NoVelocity
extends Module {
    ModeSetting field6123 = new ModeSetting(field6136, field6137, field6138, field6139);
    static String field6124;
    static String field6125;
    static MinecraftClient I = MinecraftClient.getInstance();
    Vec3d field6126;
    int field6127;
    Vec3d field6128;
    static double field6129;
    static String field6130;
    static String field6131;
    static String field6132;
    BooleanSetting field6133;
    static String field6134;
    static String field6135;
    static String field6136;
    static String field6137;
    static String field6138;
    static String field6139;
    static String field6140;
    BooleanSetting field6141 = new BooleanSetting(field6140, true, () -> this.field6123.isMode(field6130));
    static String field6142;
    VelocityUtil field6143;

    @EventHandler(2)
    private void cfr_renamed_0(EventVelocity param1) {
    }

    @EventHandler(2)
    public void method1458(EventUpdate param1) {
        if (!this.field6123.isMode(field6125) || NoVelocity.I.player == null) {
            if (!this.field6126.equals((Object)Vec3d.ZERO)) {
                this.field6126 = Vec3d.ZERO;
            }
            return;
        }
        if (this.field6127 > 0) {
            if (this.field6126.equals((Object)Vec3d.ZERO)) {
                this.field6126 = NoVelocity.I.player.getVelocity();
            }
            NoVelocity.I.player.setVelocity(0.0, NoVelocity.I.player.getVelocity().y, 0.0);
        } else if (!this.field6126.equals((Object)Vec3d.ZERO)) {
            NoVelocity.I.player.setVelocity(this.field6126);
            this.field6126 = Vec3d.ZERO;
        }
    }

    private double method1459() {
        double v1 = NoVelocity.I.player.getYaw();
        double v3 = -this.field6128.x;
        double v5 = -this.field6128.z;
        double v7 = Math.toDegrees(Math.atan2(v5, v3)) - field6129;
        return MathHelper.wrapDegrees((double)(v7 - v1));
    }

    @EventHandler(2)
    public void method1460(EventInput param1) {
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.field6127 = 0;
        this.field6126 = Vec3d.ZERO;
    }

    @EventHandler(2)
    public void method1462(EventPacket param1) {
        EntityVelocityUpdateS2CPacket v2;
        Object v3;
        if (this.field6123.isMode(field6131) && NoVelocity.I.player != null && (v3 = param1.method2467()) instanceof EntityVelocityUpdateS2CPacket && (v2 = (EntityVelocityUpdateS2CPacket)v3).getEntityId() == NoVelocity.I.player.getId()) {
            param1.method2298(true);
        }
        if (this.field6123.isMode(field6132) && NoVelocity.I.player != null) {
            if (param1.method2466()) {
                Packet v4 = param1.method2467();
                boolean v2_bool = v4 instanceof ExplosionS2CPacket || (v4 instanceof EntityVelocityUpdateS2CPacket && ((EntityVelocityUpdateS2CPacket)v4).getEntityId() == NoVelocity.I.player.getId());
                if (v2_bool) {
                    if (((Boolean)this.field6133.getValue()).booleanValue() && (v3 = (Speed)Minced.getInstance().getModuleManager().method187(Speed.class)) != null && ((Module)v3).isEnabled()) {
                        return;
                    }
                    this.field6127 = 2;
                    param1.method2298(true);
                }
            } else if (param1.method2465() && param1.method2467() instanceof PlayerMoveC2SPacket && this.field6127 > 0) {
                --this.field6127;
            }
        }
    }

    public NoVelocity() {
        super(field6134, field6135, Combat.Combat);
        this.field6133 = new BooleanSetting(field6142, true, () -> this.field6123.isMode(field6124));
        this.field6128 = Vec3d.ZERO;
        this.field6143 = new VelocityUtil();
        this.field6126 = Vec3d.ZERO;
        this.addSettings(this.field6123, this.field6141, this.field6133);
    }

    static {
        field6134 = "No Velocity";
        field6135 = "Не позволяет разным условиям в мире откидывать вас";
        field6136 = "Мод";
        field6137 = "Обычный";
        field6138 = "Обычный";
        field6139 = "Грим";
        field6140 = "Прыгать";
        field6142 = "Пропускать при активных спидах";
        field6131 = "Обычный";
        field6132 = "Грим";
        field6125 = "Грим";
        field6129 = 90.0;
        field6124 = "Грим";
        field6130 = "Легит";
    }
}



