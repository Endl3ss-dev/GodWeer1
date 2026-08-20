/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.entity.vehicle.BoatEntity
 *  net.minecraft.util.Hand
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 */
package minced.module.movement;
import minced.core.EventHandler;

import minced.core.BooleanSetting;
import minced.core.EventUpdate;
import minced.core.ClientState;
import minced.core.Rotation;
import minced.core.AimRotation;
import minced.core.Module;
import minced.enums.Combat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class BedrockClip
extends Module {
    static double field4053;
    static MinecraftClient I = MinecraftClient.getInstance();
    static double field4054;
    BooleanSetting field4055 = new BooleanSetting(field4071, true);
    static double field4056;
    boolean field4057 = false;
    static double field4058;
    static double field4059;
    static float field4060;
    static float field4061;
    static float field4062;
    ClientState field4063;
    static long field4064;
    boolean field4065 = false;
    static double field4066;
    ClientState field4067 = new ClientState();
    static long field4068;
    static String field4069;
    static String field4070;
    static String field4071;

    @EventHandler(2)
    public void method1054(EventUpdate param1) {
        BoatEntity v2 = null;
        double v3 = field4053;
        for (Entity v6 : BedrockClip.I.world.getEntities()) {
            double v7;
            if (!(v6 instanceof BoatEntity) || !((v7 = BedrockClip.I.player.squaredDistanceTo(v6)) < field4054) || !(v7 < v3)) continue;
            v3 = v7;
            v2 = (BoatEntity)v6;
        }
        if (v2 != null) {
            double v5 = BedrockClip.I.player.distanceTo(v2);
            if (((Boolean)this.field4055.getValue()).booleanValue() && v5 < field4056 && !BedrockClip.I.player.hasVehicle() && !this.field4057) {
                Vec3d v7 = v2.getPos().add(0.0, (double)v2.getHeight() / field4058, 0.0).subtract(BedrockClip.I.player.getEyePos());
                float v8 = (float)MathHelper.wrapDegrees((double)(Math.toDegrees(Math.atan2(v7.z, v7.x)) - field4059));
                float v9 = (float)(-Math.toDegrees(Math.atan2(v7.y, Math.hypot(v7.x, v7.z))));
                AimRotation.method367(new Rotation(v8, v9), field4060, 10, 6);
                float v10 = Math.abs(MathHelper.wrapDegrees((float)(v8 - BedrockClip.I.player.getYaw())));
                float v11 = Math.abs(v9 - BedrockClip.I.player.getPitch());
                if (v10 < field4061 && v11 < field4062 && this.field4063.method3113(field4064)) {
                    BedrockClip.I.interactionManager.interactEntity((PlayerEntity)BedrockClip.I.player, (Entity)v2, Hand.MAIN_HAND);
                    this.field4063.method3114();
                }
                this.field4065 = true;
            } else {
                this.field4065 = false;
            }
            if (BedrockClip.I.player.hasVehicle() && BedrockClip.I.player.getVehicle() == v2 && !this.field4057) {
                BedrockClip.I.player.setPos(BedrockClip.I.player.getX(), BedrockClip.I.player.getY() - field4066, BedrockClip.I.player.getZ());
                BedrockClip.I.player.stopRiding();
                BedrockClip.I.player.noClip = true;
                BedrockClip.I.player.fallDistance = 0.0f;
                this.field4057 = true;
                this.field4067.method3114();
            }
            if (this.field4057 && this.field4067.method3113(field4068)) {
                this.field4057 = false;
                BedrockClip.I.player.noClip = false;
            }
        }
    }

    @Override
    public void onDisable() {
        BedrockClip.I.player.noClip = false;
        this.field4057 = false;
        this.field4065 = false;
        super.onDisable();
    }

    public BedrockClip() {
        super(field4069, field4070, Combat.Movement);
        this.field4063 = new ClientState();
        this.addSettings(this.field4055);
    }

    static {
        field4069 = "Bedrock Clip";
        field4070 = "Автоматически садится в лодку и телепортирует вниз для клипа через бедрок";
        field4071 = "Авто посадка";
        field4053 = Double.MAX_VALUE;
        field4054 = 6.25;
        field4056 = 3.0;
        field4058 = 2.0;
        field4059 = 90.0;
        field4060 = 180.0f;
        field4061 = 15.0f;
        field4062 = 15.0f;
        field4064 = 300L;
        field4066 = 135.0;
        field4068 = 1000L;
    }
}



