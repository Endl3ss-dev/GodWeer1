/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_243
 *  net.minecraft.class_3532
 *  org.joml.Matrix4f
 */
package ru.decide.utils.math;

import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import lombok.Generated;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_243;
import net.minecraft.class_3532;
import org.joml.Matrix4f;
import ru.decide.utils.annotation.IMinecraft;
import ru.decide.utils.math.Interpolator;

@Environment(value=EnvType.CLIENT)
public final class MathUtil
implements IMinecraft {
    private static final double[] TRIG_TABLE = new double[65536];
    private static float[] lastSmoothRandom;
    private static float[] lastSmoothTarget;
    private static long lastSmoothTime;
    public static double PI2;
    private static final Random RANDOM;
    public static final Matrix4f lastProjMat;
    public static final Matrix4f lastModMat;
    public static final Matrix4f lastWorldSpaceMatrix;

    public static int floorNearestMulN(int x, int n) {
        return n * (int)Math.floor((double)x / (double)n);
    }

    public static double deltaTime() {
        return mc.method_47599() > 0 ? 1.0 / (double)mc.method_47599() : 1.0;
    }

    public static float invertScaleValue(float value, float minInput, float maxInput, float minOutput, float maxOutput) {
        if (maxInput - minInput == 0.0f) {
            throw new IllegalArgumentException("\u0414\u0438\u0430\u043f\u0430\u0437\u043e\u043d \u0432\u0445\u043e\u0434\u043d\u044b\u0445 \u0437\u043d\u0430\u0447\u0435\u043d\u0438\u0439 \u043d\u0435 \u043c\u043e\u0436\u0435\u0442 \u0431\u044b\u0442\u044c \u0440\u0430\u0432\u0435\u043d \u043d\u0443\u043b\u044e.");
        }
        float scaledValue = (maxInput - value) / (maxInput - minInput) * (maxOutput - minOutput) + minOutput;
        return Math.max(minOutput, Math.min(maxOutput, scaledValue));
    }

    public static float smoothRandom(float min, float max, float smoothness) {
        long currentTime;
        if (lastSmoothRandom == null) {
            lastSmoothRandom = new float[]{(min + max) / 2.0f};
            lastSmoothTarget = new float[]{MathUtil.randomValue(min, max)};
        }
        if ((double)((currentTime = System.currentTimeMillis()) - lastSmoothTime) > 250.0 + Math.random() * 300.0) {
            MathUtil.lastSmoothTarget[0] = MathUtil.randomValue(min, max);
            lastSmoothTime = currentTime;
        }
        lastSmoothRandom[0] = lastSmoothRandom[0] + (lastSmoothTarget[0] - lastSmoothRandom[0]) * smoothness;
        return lastSmoothRandom[0];
    }

    public static class_243 cosSin(int i, int size, double width) {
        int index = Math.min(i, size);
        float cos = (float)(Math.cos((double)index * PI2 / (double)size) * width);
        float sin = (float)(-Math.sin((double)index * PI2 / (double)size) * width);
        return new class_243((double)cos, 0.0, (double)sin);
    }

    public static float wrapLerp(float step, float input, float target) {
        return input + step * class_3532.method_15393((float)(target - input));
    }

    public static double easeInOutQuadWave(double value) {
        value = (value > 0.5 ? 1.0 - value : value) * 2.0;
        value = value < 0.5 ? 2.0 * value * value : 1.0 - Math.pow(-2.0 * value + 2.0, 2.0) / 2.0;
        double d = value;
        value = value > 1.0 ? 1.0 : (value < 0.0 ? 0.0 : value);
        return value;
    }

    public static float wrapAngleTo180_float(float p_76142_0_) {
        float f;
        p_76142_0_ %= 360.0f;
        if (f >= 180.0f) {
            p_76142_0_ -= 360.0f;
        }
        if (p_76142_0_ < -180.0f) {
            p_76142_0_ += 360.0f;
        }
        return p_76142_0_;
    }

    public static double sin(double radians) {
        int index = (int)(radians * 10430.378350470453) & 0xFFFF;
        return TRIG_TABLE[index];
    }

    public static double cos(double radians) {
        int index = (int)(radians * 10430.378350470453 + 16384.0) & 0xFFFF;
        return TRIG_TABLE[index];
    }

    public static float random1(float min, float max) {
        return (float)(Math.random() * (double)(max - min) + (double)min);
    }

    public static float random(double min, double max) {
        return (float)(min + (max - min) * Math.random());
    }

    public static float randomSin(double min, double max, double time) {
        return (float)(Math.sin((double)System.currentTimeMillis() / time) * (double)MathUtil.randomLerp((float)min, (float)max));
    }

    public static float randomCos(double min, double max, double time) {
        return (float)(Math.cos((double)System.currentTimeMillis() / time) * (double)MathUtil.randomLerp((float)min, (float)max));
    }

    public static float randomGaussian(double min, double max) {
        double mean = (min + max) * 0.5;
        double deviation = (max - min) / 6.0;
        double value = mean + RANDOM.nextGaussian() * deviation;
        return (float)Math.max(min, Math.min(max, value));
    }

    public static float randomGaussianSmooth(double min, double max, double time) {
        double mean = (min + max) * 0.5;
        double deviation = (max - min) / 6.0;
        double noise = RANDOM.nextGaussian() * deviation;
        double smooth = Math.sin((double)System.currentTimeMillis() / time);
        double value = mean + noise * smooth;
        return (float)Math.max(min, Math.min(max, value));
    }

    public static double lerp(double delta, double start, double end) {
        return start + delta * (end - start);
    }

    public static float lerp(float delta, float start, float end) {
        return start + delta * (end - start);
    }

    public static float fast(float end, float start, float multiple) {
        return (1.0f - class_3532.method_15363((float)((float)(MathUtil.deltaTime() * (double)multiple)), (float)0.0f, (float)1.0f)) * end + class_3532.method_15363((float)((float)(MathUtil.deltaTime() * (double)multiple)), (float)0.0f, (float)1.0f) * start;
    }

    public static double clamps(double min, double max, double value) {
        return Math.max(min, Math.min(max, value));
    }

    public static float randomLerp(float min, float max) {
        return Interpolator.lerp(Float.valueOf(max), Float.valueOf(min), new SecureRandom().nextFloat()).floatValue();
    }

    public static double getRandomNumberBetween(double min, double max) {
        return Math.random() * (max - min) + min;
    }

    public static float interpolate(double oldValue, double newValue, double interpolationValue) {
        return (float)(oldValue + (newValue - oldValue) * interpolationValue);
    }

    public static class_243 interpolate(class_243 prevPos, class_243 pos) {
        return new class_243(MathUtil.interpolate(prevPos.field_1352, pos.field_1352), MathUtil.interpolate(prevPos.field_1351, pos.field_1351), MathUtil.interpolate(prevPos.field_1350, pos.field_1350));
    }

    public static double interpolate(double prev, double orig) {
        return MathUtil.lerp((double)mc.method_61966().method_60637(false), prev, orig);
    }

    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    public static float clamp(float value, float min, float max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

    public static int randomInt(int min, int max) {
        return min + (int)(Math.random() * (double)(max - min + 1));
    }

    public static double randomValue(double min, double max) {
        MathUtil.validateRange(min, max);
        return min + ThreadLocalRandom.current().nextDouble() * (max - min);
    }

    public static float randomValue(float min, float max) {
        MathUtil.validateRange(min, max);
        return min + ThreadLocalRandom.current().nextFloat() * (max - min);
    }

    private static void validateRange(double min, double max) {
        if (max < min) {
            throw new IllegalArgumentException("max \u043d\u0435 \u043c\u043e\u0436\u0435\u0442 \u0431\u044b\u0442\u044c \u043c\u0435\u043d\u044c\u0448\u0435 min.");
        }
    }

    public static float textScrolling(float textWidth) {
        int speed = 6000;
        return (float)class_3532.method_15350((double)((double)(System.currentTimeMillis() % (long)speed) * Math.PI / (double)speed), (double)0.0, (double)1.0) * textWidth;
    }

    public static int clampMin(int value, int min) {
        return value < min ? min : value;
    }

    public static float clampMin(float value, float min) {
        return value < min ? min : value;
    }

    public static double clampMin(double value, double min) {
        return value < min ? min : value;
    }

    public static boolean isInRegion(int mouseX, int mouseY, float x, float y, float width, float height) {
        return (float)mouseX >= x && (float)mouseY >= y && (float)mouseX <= x + width && (float)mouseY <= y + height;
    }

    public static boolean isHovered(float mouseX, float mouseY, float x, float y, float width, float height) {
        return mouseX > x && mouseX < x + width && mouseY > y && mouseY < y + height;
    }

    public static float step(float value, float step) {
        return (float)Math.round(value / step) * step;
    }

    public static double round(double value, int increment) {
        double num = Math.pow(10.0, increment);
        return (double)Math.round(value * num) / num;
    }

    public static float round(float value, int decimals) {
        float multiplier = (float)Math.pow(10.0, decimals);
        return (float)Math.round(value * multiplier) / multiplier;
    }

    public static float round(float value, float increment) {
        return (float)Math.round(value / increment) * increment;
    }

    public static double getScaleFactor() {
        return mc.method_22683().method_4495();
    }

    @Generated
    private MathUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    static {
        lastSmoothTime = 0L;
        PI2 = Math.PI * 2;
        RANDOM = new Random();
        lastProjMat = new Matrix4f();
        lastModMat = new Matrix4f();
        lastWorldSpaceMatrix = new Matrix4f();
        for (int i = 0; i < 65536; ++i) {
            MathUtil.TRIG_TABLE[i] = Math.sin((double)i * (Math.PI * 2) / 65536.0);
        }
    }
}

