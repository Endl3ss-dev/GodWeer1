/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.utils.math;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public class Interpolator {
    public static <T extends Number> T lerp(T input, T target, double step) {
        double start = input.doubleValue();
        double end = target.doubleValue();
        double result = start + step * (end - start);
        if (input instanceof Integer) {
            return (T)Integer.valueOf((int)Math.round(result));
        }
        if (input instanceof Double) {
            return (T)Double.valueOf(result);
        }
        if (input instanceof Float) {
            return (T)Float.valueOf((float)result);
        }
        if (input instanceof Long) {
            return (T)Long.valueOf(Math.round(result));
        }
        if (input instanceof Short) {
            return (T)Short.valueOf((short)Math.round(result));
        }
        if (input instanceof Byte) {
            return (T)Byte.valueOf((byte)Math.round(result));
        }
        throw new IllegalArgumentException("Unsupported type: " + input.getClass().getSimpleName());
    }
}

