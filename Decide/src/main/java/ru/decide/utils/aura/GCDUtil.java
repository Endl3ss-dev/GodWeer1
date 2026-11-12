/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.utils.aura;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.utils.annotation.IMinecraft;

@Environment(value=EnvType.CLIENT)
public class GCDUtil
implements IMinecraft {
    public static float getSensitivity(float rot) {
        return GCDUtil.getDeltaMouse(rot) * GCDUtil.getGCDValue();
    }

    public static float getGCDValue() {
        return (float)((double)GCDUtil.getGCD() * 0.15);
    }

    public static float getGCD() {
        float f1 = (float)((Double)GCDUtil.mc.field_1690.method_42495().method_41753() * 0.6 + 0.2);
        return f1 * f1 * f1 * 8.0f;
    }

    public static float getDeltaMouse(float delta) {
        return Math.round(delta / GCDUtil.getGCDValue());
    }

    public static float applyGCD(float targetRotation, float currentRotation) {
        float sensitivity = ((Double)GCDUtil.mc.field_1690.method_42495().method_41753()).floatValue();
        float f = sensitivity * 0.6f + 0.2f;
        float f1 = f * f * f * 8.0f;
        float delta = targetRotation - currentRotation;
        float adjustedDelta = (float)Math.round(delta / (f1 * 0.15f)) * (f1 * 0.15f);
        return currentRotation + adjustedDelta;
    }
}

