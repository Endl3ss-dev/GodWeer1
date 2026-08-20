/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 */
package minced.core;

import java.util.concurrent.ThreadLocalRandom;
import minced.core.Rotation;
import minced.core.AimRotation;
import minced.core.Minced;
import minced.mixin.accessor.ILivingEntityMixin;
import minced.module.combat.AttackAura;
import minced.module.movement.ElytraTarget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class AuraTargetBuilder {
    static String field10391 = "Builder";
    static MinecraftClient I = MinecraftClient.getInstance();
    static double field10392;
    static double field10393;
    static double field10394;
    static double field10395;
    static double field10396;
    static double field10397;
    static double field10398;
    static double field10399;
    static long field10400;
    static double field10401;
    static double field10402;
    static double field10403;
    static double field10404;
    static double field10405;
    static double field10406;
    static double field10407;
    static double field10408;
    static double field10409;
    static double field10410;
    static double field10411;
    static double field10412;
    static double field10413;
    static double field10414;
    static float field10415;
    static float field10416;
    static float field10417;
    static float field10418;
    static float field10419;
    static double field10420;
    static double field10421;
    static double field10422;
    static double field10423;
    static double field10424;
    static double field10425;
    static double field10426;
    static double field10427;
    static double field10428;
    static double field10429;
    static float field10430;
    static float field10431;
    static long field10432;
    static double field10433;
    static double field10434;
    static double field10435;
    static double field10436;
    static double field10437;
    static double field10438;
    static double field10439;
    static double field10440;
    static double field10441;
    static double field10442;
    static double field10443;
    static double field10444;
    static double field10445;
    static double field10446;

    private ElytraTarget method3022() {
        return (ElytraTarget)Minced.getInstance().getModuleManager().method187(ElytraTarget.class);
    }

    public boolean method3023(AttackAura param1, LivingEntity param2) {
        Vec3d v4;
        double v82;
        Vec3d v7;
        Vec3d v52;
        if (param1.method799().isMode(field10391)) {
            return this.method3024(param1, param2);
        }
        ElytraTarget v3 = this.method3022();
        if (v3 == null || !v3.isEnabled() || !AuraTargetBuilder.I.player.isGliding()) {
            return false;
        }
        if (param2.isGliding()) {
            v52 = Vec3d.fromPolar((float)((float)((ILivingEntityMixin)param2).getServerPitch()), (float)((float)((ILivingEntityMixin)param2).getServerYaw()));
            Vec3d v6 = Vec3d.fromPolar((float)((float)((ILivingEntityMixin)param2).getServerPitch()), (float)((float)((ILivingEntityMixin)param2).getServerYaw())).normalize();
            v7 = v52.lengthSquared() > field10392 ? v52.normalize() : v6;
            v82 = v6.dotProduct(v7);
            double v10 = Math.acos(MathHelper.clamp((double)v82, (double)field10393, (double)1.0));
            double v12 = Math.min(1.0, v10 / Math.toRadians(field10394)) * field10395;
            Vec3d v14 = v7.lerp(v6, v12).normalize();
            double v15 = v52.length();
            v4 = param2.getEyePos().add(v14.multiply(v15 * (double)((Float)v3.field4737.getValue()).floatValue()));
            v4 = v4.add(v14.normalize().multiply((double)((Float)v3.field4737.getValue()).floatValue()));
        } else {
            v52 = param2.getPos().add(0.0, (double)param2.getHeight() * field10396, 0.0);
            Vec3d v6 = AuraTargetBuilder.I.player.getEyePos().subtract(v52);
            v7 = v6.lengthSquared() > field10397 ? v6.normalize() : Vec3d.ZERO;
            v82 = Math.max(field10398, (double)((Float)v3.field4737.getValue()).floatValue() * field10399);
            v4 = v52.add(v7.multiply(v82));
        }
        long v52t = System.currentTimeMillis() / field10400;
        v7 = new Vec3d(Math.sin((double)v52t * field10401) * field10402 + ThreadLocalRandom.current().nextDouble(field10403, field10404), Math.sin((double)v52t * field10405) * field10406 + ThreadLocalRandom.current().nextDouble(field10407, field10408), Math.cos((double)v52t * field10409) * field10410 + ThreadLocalRandom.current().nextDouble(field10411, field10412));
        Vec3d v82b = v4.subtract(AuraTargetBuilder.I.player.getEyePos()).add(v7.multiply(field10413));
        float v9 = (float)MathHelper.wrapDegrees((double)(Math.toDegrees(Math.atan2(v82b.z, v82b.x)) - field10414));
        float v10 = (float)(-Math.toDegrees(Math.atan2(v82b.y, Math.hypot(v82b.x, v82b.z))));
        AimRotation.method376(new Rotation(v9, v10), field10415, field10416, field10417, field10418, 1, 12, false);
        return true;
    }

    private boolean method3024(AttackAura param1, LivingEntity param2) {
        Vec3d v11;
        Vec3d v132;
        Vec3d v122;
        if (AuraTargetBuilder.I.player == null || !AuraTargetBuilder.I.player.isGliding()) {
            return false;
        }
        ElytraTarget v3 = this.method3022();
        float v4 = I.getRenderTickCounter().getTickDelta(true);
        double v5 = param2.getLerpedPos(v4).subtract(new Vec3d(param2.lastRenderX, param2.lastRenderY, param2.lastRenderZ)).length();
        float v7 = ((Float)param1.method817().getValue()).floatValue();
        boolean v8 = v3 != null && v3.isEnabled() && param2.isGliding() && v5 >= (double)v7;
        float v9 = MathHelper.clamp((float)(((Float)param1.method842().getValue()).floatValue() / field10419), (float)0.0f, (float)1.0f);
        Vec3d v10 = param2.getEyePos().lerp(param2.getPos().add(0.0, (double)param2.getHeight() * field10420, 0.0), (double)v9);
        if (v8) {
            v122 = Vec3d.fromPolar((float)((float)((ILivingEntityMixin)param2).getServerPitch()), (float)((float)((ILivingEntityMixin)param2).getServerYaw()));
            v132 = Vec3d.fromPolar((float)((float)((ILivingEntityMixin)param2).getServerPitch()), (float)((float)((ILivingEntityMixin)param2).getServerYaw())).normalize();
            Vec3d v14 = v122.lengthSquared() > field10421 ? v122.normalize() : v132;
            double v15 = v132.dotProduct(v14);
            double v17 = Math.acos(MathHelper.clamp((double)v15, (double)field10422, (double)1.0));
            double v19 = MathHelper.clamp((double)((double)((Float)param1.method878().getValue()).floatValue() / field10423), (double)0.0, (double)1.0);
            double v21 = Math.min(1.0, v17 / Math.toRadians(field10424)) * field10425 * v19;
            Vec3d v23 = v14.lerp(v132, v21).normalize();
            double v24 = v122.length();
            double v26 = ((Float)param1.method904().getValue()).floatValue();
            if (((Boolean)param1.method900().getValue()).booleanValue() && v3 != null) {
                v26 += (double)((Float)v3.field4737.getValue()).floatValue();
            }
            v11 = v10.add(v23.multiply(v24 * v26));
            v11 = v11.add(v23.normalize().multiply(v26));
        } else if (param2.isGliding()) {
            v11 = v10;
        } else {
            v122 = AuraTargetBuilder.I.player.getEyePos().subtract(v10);
            v132 = v122.lengthSquared() > field10426 ? v122.normalize() : Vec3d.ZERO;
            double v14 = v3 != null ? Math.max(field10427, (double)((Float)v3.field4737.getValue()).floatValue() * field10428) : field10429;
            v11 = v10.add(v132.multiply(v14));
        }
        float v122f = MathHelper.clamp((float)(((Float)param1.method849().getValue()).floatValue() / field10430), (float)0.0f, (float)field10431);
        long v132t = System.currentTimeMillis() / field10432;
        Vec3d v15 = new Vec3d((Math.sin((double)v132t * field10433) * field10434 + ThreadLocalRandom.current().nextDouble(field10435, field10436)) * (double)v122f, (Math.sin((double)v132t * field10437) * field10438 + ThreadLocalRandom.current().nextDouble(field10439, field10440)) * (double)v122f, (Math.cos((double)v132t * field10441) * field10442 + ThreadLocalRandom.current().nextDouble(field10443, field10444)) * (double)v122f);
        Vec3d v16 = v11.subtract(AuraTargetBuilder.I.player.getEyePos()).add(v15.multiply(field10445 * (double)v122f));
        float v17 = (float)MathHelper.wrapDegrees((double)(Math.toDegrees(Math.atan2(v16.z, v16.x)) - field10446));
        float v18 = (float)(-Math.toDegrees(Math.atan2(v16.y, Math.hypot(v16.x, v16.z))));
        float v19 = ((Float)param1.method825().getValue()).floatValue();
        float v20 = ((Float)param1.method877().getValue()).floatValue();
        float v21 = ((Float)param1.method917().getValue()).floatValue();
        float v22 = ((Float)param1.method893().getValue()).floatValue();
        int v23 = MathHelper.clamp((int)Math.round(((Float)param1.method876().getValue()).floatValue()), (int)1, (int)80);
        int v24 = MathHelper.clamp((int)Math.round(((Float)param1.method796().getValue()).floatValue()), (int)0, (int)5);
        boolean v25 = (Boolean)param1.minced_core_ClsClass271_l().getValue();
        AimRotation.method376(new Rotation(v17, v18), v19, v20, v21, v22, v23, v24, v25);
        return true;
    }

    static {
        field10392 = 0.001;
        field10393 = -1.0;
        field10394 = 90.0;
        field10395 = 0.5;
        field10396 = 0.6;
        field10397 = 1.0E-6;
        field10398 = 0.8;
        field10399 = 0.5;
        field10400 = 1000L;
        field10401 = 1.8;
        field10402 = 0.04;
        field10403 = -0.01;
        field10404 = 0.01;
        field10405 = 2.2;
        field10406 = 0.03;
        field10407 = -0.0075;
        field10408 = 0.0075;
        field10409 = 1.8;
        field10410 = 0.04;
        field10411 = -0.01;
        field10412 = 0.01;
        field10413 = 4.0;
        field10414 = 90.0;
        field10415 = 360.0f;
        field10416 = 360.0f;
        field10417 = 360.0f;
        field10418 = 360.0f;
        field10419 = 100.0f;
        field10420 = 0.5;
        field10421 = 0.001;
        field10422 = -1.0;
        field10423 = 100.0;
        field10424 = 90.0;
        field10425 = 0.5;
        field10426 = 1.0E-6;
        field10427 = 0.8;
        field10428 = 0.5;
        field10429 = 0.8;
        field10430 = 100.0f;
        field10431 = 1.5f;
        field10432 = 1000L;
        field10433 = 1.8;
        field10434 = 0.04;
        field10435 = -0.01;
        field10436 = 0.01;
        field10437 = 2.2;
        field10438 = 0.03;
        field10439 = -0.0075;
        field10440 = 0.0075;
        field10441 = 1.8;
        field10442 = 0.04;
        field10443 = -0.01;
        field10444 = 0.01;
        field10445 = 4.0;
        field10446 = 90.0;
    }
}



