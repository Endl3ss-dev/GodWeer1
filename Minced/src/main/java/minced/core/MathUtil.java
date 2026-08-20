/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.entity.Entity
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec2f
 *  net.minecraft.util.math.Vec3d
 */
package minced.core;

import java.math.BigDecimal;
import java.math.RoundingMode;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class MathUtil {
    static double field7600;
    static double field7601;
    static double field7602;
    static double field7603;
    static double field7604;
    static MinecraftClient I = MinecraftClient.getInstance();
    static double field7605;
    static float field7606;
    static double field7607;
    static float field7608;

    public static Vec3d cfr_underscore(Vec3d param0, Vec3d param1, float param2) {
        return new Vec3d((double)MathUtil.method1866((float)param0.getX(), (float)param1.getX(), param2), (double)MathUtil.method1866((float)param0.getY(), (float)param1.getY(), param2), (double)MathUtil.method1866((float)param0.getZ(), (float)param1.getZ(), param2));
    }

    public static double method1862(double param0, double param2) {
        double v4 = (double)Math.round(param0 / param2) * param2;
        return (double)Math.round(v4 * field7600) / field7601;
    }

    public static float cfr_underscore(float param0, float param1) {
        double v2 = field7602;
        double v4 = 1.0 / Math.sqrt(field7603 * v2 * (double)(param1 * param1));
        return (float)(v4 * Math.exp((double)(-(param0 * param0)) / (field7604 * (double)(param1 * param1))));
    }

    public static float cfr_underscore(float param0, float param1, double param2) {
        return (float)((double)param0 + (double)(param1 - param0) * param2);
    }

    public static int cfr_underscore(int param0, int param1, double param2) {
        return (int)((double)param0 + (double)(param1 - param0) * param2);
    }

    public static float method1863(float param0, float param1) {
        return MathUtil.method1864(param0, param1);
    }

    public static double cfr_underscore(double param0, double param2) {
        double v4 = (double)Math.round(param0 / param2) * param2;
        BigDecimal v6 = new BigDecimal(v4);
        v6 = v6.setScale(2, RoundingMode.HALF_UP);
        return v6.doubleValue();
    }

    public static double cfr_underscore() {
        return I.getCurrentFps() > 5 ? (double)(1.0f / (float)I.getCurrentFps()) : field7605;
    }

    public static double cfr_underscore(double param0, double param2, double param4) {
        return param0 + (param2 - param0) * param4;
    }

    public static float cfr_underscore(float param0, float param1, float param2) {
        float v3 = MathHelper.clamp((float)param2, (float)0.0f, (float)1.0f);
        float v4 = MathHelper.wrapDegrees((float)(param1 - param0));
        if (Math.abs(v4) < field7606) {
            return param1;
        }
        return MathHelper.wrapDegrees((float)(param0 + v4 * v3));
    }

    public static boolean cfr_underscore(float param0, float param1, float param2, float param3, float param4, float param5) {
        return param0 > param2 && param0 < param2 + param4 && param1 > param3 && param1 < param3 + param5;
    }

    public static float method1864(float param0, float param1) {
        return (float)(Math.random() * (double)(param1 - param0) + (double)param0);
    }

    public static double method1865(double param0, double param2, double param4) {
        return param0 + (param2 - param0) * MathHelper.clamp((double)(MathUtil.cfr_underscore() * param4), (double)0.0, (double)1.0);
    }

    public static float method1866(float param0, float param1, float param2) {
        return (1.0f - MathHelper.clamp((float)((float)(MathUtil.cfr_underscore() * (double)param2)), (float)0.0f, (float)1.0f)) * param0 + MathHelper.clamp((float)((float)(MathUtil.cfr_underscore() * (double)param2)), (float)0.0f, (float)1.0f) * param1;
    }

    public static double method1867(double param0, double param2) {
        double v4 = (double)Math.round(param0 / param2) * param2;
        BigDecimal v6 = new BigDecimal(v4);
        v6 = v6.setScale(2, RoundingMode.HALF_UP);
        return v6.doubleValue();
    }

    public static double cfr_underscore(Entity param0, int param1) {
        double v2 = param0.getX() - param0.prevX;
        double v4 = param0.getY() - param0.prevY;
        double v6 = param0.getZ() - param0.prevZ;
        double v8 = Math.sqrt(v2 * v2 + v4 * v4 + v6 * v6) * field7607;
        return MathUtil.cfr_underscore(v8, (double)param1);
    }

    public static float method1868(float param0, float param1) {
        float v2 = (float)Math.round(param0 / param1) * param1;
        BigDecimal v3 = BigDecimal.valueOf(v2);
        v3 = v3.setScale(2, RoundingMode.HALF_UP);
        return v3.floatValue();
    }

    public static Vec2f cfr_underscore(Entity param0) {
        Vec3d v1 = param0.getPos().subtract(MathUtil.I.player.getPos());
        double v2 = Math.hypot(v1.x, v1.z);
        return new Vec2f((float)Math.toDegrees(Math.atan2(v1.z, v1.x)) - field7608, (float)(-Math.toDegrees(Math.atan2(v1.y, v2))));
    }

    public static float method1869(float param0, float param1, float param2) {
        return param1 + (param0 - param1) * param2;
    }

    public static float method1870(float param0, float param1, float param2) {
        return (float)((double)param0 + (double)(param1 - param0) * MathHelper.clamp((double)(MathUtil.cfr_underscore() * (double)param2), (double)0.0, (double)1.0));
    }

    static {
        field7607 = 20.0;
        field7602 = Math.PI;
        field7603 = 2.0;
        field7604 = 2.0;
        field7606 = 0.5f;
        field7608 = 90.0f;
        field7600 = 100.0;
        field7601 = 100.0;
        field7605 = 0.016f;
    }
}



