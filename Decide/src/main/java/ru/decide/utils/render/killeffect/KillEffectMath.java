/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_3532
 */
package ru.decide.utils.render.killeffect;

import java.awt.Color;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_3532;

@Environment(value=EnvType.CLIENT)
public final class KillEffectMath {
    private KillEffectMath() {
    }

    public static float smoothStep(float edge0, float edge1, float x) {
        float v = class_3532.method_15363((float)((x - edge0) / (edge1 - edge0)), (float)0.0f, (float)1.0f);
        return v * v * (3.0f - 2.0f * v);
    }

    public static float easeOutCubic(float t) {
        float inv = 1.0f - class_3532.method_15363((float)t, (float)0.0f, (float)1.0f);
        return 1.0f - inv * inv * inv;
    }

    public static float easeOutExpo(float t) {
        float v = Math.min(t, 1.0f);
        return v == 1.0f ? 1.0f : 1.0f - (float)Math.pow(2.0, -10.0f * v);
    }

    public static int withAlpha(int argb, float alpha01) {
        int a = Math.round(class_3532.method_15363((float)alpha01, (float)0.0f, (float)1.0f) * 255.0f);
        return argb & 0xFFFFFF | a << 24;
    }

    public static int shiftHue(int argb, float degrees) {
        int r = argb >> 16 & 0xFF;
        int g = argb >> 8 & 0xFF;
        int b = argb & 0xFF;
        float[] hsb = Color.RGBtoHSB(r, g, b, null);
        hsb[0] = (hsb[0] + degrees / 360.0f) % 1.0f;
        if (hsb[0] < 0.0f) {
            hsb[0] = hsb[0] + 1.0f;
        }
        int rgb = Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]) & 0xFFFFFF;
        return argb & 0xFF000000 | rgb;
    }
}

