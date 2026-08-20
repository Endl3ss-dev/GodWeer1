/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.entity.Entity
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec2f
 *  net.minecraft.util.math.Vec3d
 */
package minced.core;

import java.util.function.Function;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public final class RotationTargetUtil {
    static double field7944 = 0.15;
    static MinecraftClient I = MinecraftClient.getInstance();
    static double field7945;
    static double field7946;
    static double field7947;
    static String field7948;
    static double field7949;
    static double field7950;
    static double field7951;
    static double field7952;

    public static float method1975() {
        double v0 = field7944;
        float step = (float)((double)RotationTargetUtil.method1980() * v0);
        return Float.isFinite(step) && step > 0.0f ? step : 0.15f;
    }

    public static Vec2f method1976(double param0, double param2, double param4) {
        double v6 = param0 - RotationTargetUtil.I.player.getX();
        double v8 = param2 - RotationTargetUtil.I.player.getEyeY();
        double v10 = param4 - RotationTargetUtil.I.player.getZ();
        double v12 = MathHelper.sqrt((float)((float)(v6 * v6 + v10 * v10)));
        float v14 = (float)(MathHelper.atan2((double)v10, (double)v6) * field7945 - field7946);
        float v15 = (float)(-MathHelper.atan2((double)v8, (double)v12) * field7947);
        return new Vec2f(v14, v15);
    }

    public static float I(float param0) {
        return Math.round(param0 / RotationTargetUtil.method1975());
    }

    public static Vec2f method1977(Vec3d param0) {
        return RotationTargetUtil.method1976(param0.x, param0.y, param0.z);
    }

    public static Vec3d method1978(Vec3d param0, Entity param1) {
        return RotationTargetUtil.method1982(param0, param1.getBoundingBox());
    }

    private RotationTargetUtil() {
        throw new UnsupportedOperationException(field7948);
    }

    public static Vec3d method1979(Entity param0) {
        Vec3d v1 = RotationTargetUtil.I.player.getCameraPosVec(I.getRenderTickCounter().getTickDelta(true));
        return RotationTargetUtil.method1978(v1, param0).subtract(v1);
    }

    public static float method1980() {
        double v0 = (Double)RotationTargetUtil.I.options.getMouseSensitivity().getValue();
        return (float)(Math.pow(v0 * field7949 + field7950, field7951) * field7952);
    }

    public static float method1981(float param0) {
        return RotationTargetUtil.I(param0) * RotationTargetUtil.method1975();
    }

    public static Vec3d method1982(Vec3d param0, Box param1) {
        return new Vec3d(Math.clamp(param0.getX(), param1.minX, param1.maxX), Math.clamp(param0.getY(), param1.minY, param1.maxY), Math.clamp(param0.getZ(), param1.minZ, param1.maxZ));
    }

    static {
        field7949 = 0.6f;
        field7950 = 0.2f;
        field7951 = 3.0;
        field7952 = 8.0;
        field7945 = 57.29577951308232;
        field7946 = 90.0;
        field7947 = 57.29577951308232;
        field7948 = "This is a utility class and cannot be instantiated";
    }
}



