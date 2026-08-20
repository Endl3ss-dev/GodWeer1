/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.shape.VoxelShape
 */
package minced.core;

import minced.core.EventInput;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;

public final class TimerUtil {
    static MinecraftClient I = MinecraftClient.getInstance();
    static float field12466;
    static float field12467;
    static float field12468;
    static float field12469;
    static float field12470;
    static float field12471;
    static float field12472;
    static float field12473;
    static String field12474;
    static double field12475;
    static double field12476;
    static double field12477;
    static float field12478;
    static float field12479;
    static float field12480;
    static float field12481;
    static float field12482;

    public static void method3543(EventInput param0, float param1) {
        float v2 = param0.method1969();
        float v3 = param0.method1971();
        double v4 = MathHelper.wrapDegrees((double)Math.toDegrees(TimerUtil.method3548(TimerUtil.I.player.getYaw(), v2, -v3)));
        if (v2 == 0.0f && v3 == 0.0f) {
            return;
        }
        float v6 = 0.0f;
        float v7 = 0.0f;
        float v8 = field12466;
        for (float v9 = field12467; v9 <= 1.0f; v9 += 1.0f) {
            for (float v10 = field12468; v10 <= 1.0f; v10 += 1.0f) {
                double v11;
                double v13;
                if (v10 == 0.0f && v9 == 0.0f || !((v13 = Math.abs(MathHelper.wrapDegrees((double)(v4 - (v11 = MathHelper.wrapDegrees((double)Math.toDegrees(TimerUtil.method3548(param1, v9, -v10)))))))) < (double)v8)) continue;
                v8 = (float)v13;
                v6 = v9;
                v7 = v10;
            }
        }
        param0.method1973(v6);
        param0.method1972(v7);
    }

    public static boolean method3544(float param0) {
        if (TimerUtil.I.player.getY() < 0.0) {
            return false;
        }
        Box v1 = TimerUtil.I.player.getBoundingBox().offset(0.0, (double)(-param0), 0.0);
        return ((VoxelShape)TimerUtil.I.world.getCollisions((Entity)TimerUtil.I.player, v1).iterator().next()).isEmpty();
    }

    public static double method3545(boolean param0) {
        float v1 = TimerUtil.I.player.getYaw();
        if (TimerUtil.I.player.forwardSpeed < 0.0f) {
            v1 += field12469;
        }
        float v2 = 1.0f;
        if (TimerUtil.I.player.forwardSpeed < 0.0f) {
            v2 = field12470;
        } else if (TimerUtil.I.player.forwardSpeed > 0.0f) {
            v2 = field12471;
        }
        if (TimerUtil.I.player.sidewaysSpeed > 0.0f) {
            v1 -= field12472 * v2;
        }
        if (TimerUtil.I.player.sidewaysSpeed < 0.0f) {
            v1 += field12473 * v2;
        }
        return param0 ? Math.toRadians(v1) : (double)v1;
    }

    public static void method3546(double param0) {
        double v2 = TimerUtil.method3545(true);
        TimerUtil.I.player.setVelocity(-Math.sin(v2) * param0, TimerUtil.I.player.getVelocity().y, Math.cos(v2) * param0);
    }

    private TimerUtil() {
        throw new UnsupportedOperationException(field12474);
    }

    public static double method3547(LivingEntity param0) {
        if (param0 == null) {
            return 0.0;
        }
        double v1 = Math.sqrt(Math.pow(param0.getX() - param0.prevX, field12475) + Math.pow(param0.getZ() - param0.prevZ, field12476));
        return v1 * field12477;
    }

    public static double method3548(float param0, double param1, double param3) {
        if (param1 < 0.0) {
            param0 += field12478;
        }
        float v5 = 1.0f;
        if (param1 < 0.0) {
            v5 = field12479;
        } else if (param1 > 0.0) {
            v5 = field12480;
        }
        if (param3 > 0.0) {
            param0 -= field12481 * v5;
        }
        if (param3 < 0.0) {
            param0 += field12482 * v5;
        }
        return Math.toRadians(param0);
    }

    public static boolean method3549() {
        return (double)TimerUtil.I.player.input.movementSideways != 0.0 || (double)TimerUtil.I.player.input.movementForward != 0.0;
    }

    static {
        field12466 = Float.MAX_VALUE;
        field12467 = -1.0f;
        field12468 = -1.0f;
        field12478 = 180.0f;
        field12479 = -0.5f;
        field12480 = 0.5f;
        field12481 = 90.0f;
        field12482 = 90.0f;
        field12469 = 180.0f;
        field12470 = -0.5f;
        field12471 = 0.5f;
        field12472 = 90.0f;
        field12473 = 90.0f;
        field12475 = 2.0;
        field12476 = 2.0;
        field12477 = 20.0;
        field12474 = "This is a utility class and cannot be instantiated";
    }
}



