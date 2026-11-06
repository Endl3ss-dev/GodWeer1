/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_3532
 */
package ru.decide.module.impl.render.targetesp;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_3532;

@Environment(value=EnvType.CLIENT)
public final class TargetEspMath {
    private TargetEspMath() {
    }

    public static float approach(float current, float target, float delta) {
        if (current < target) {
            return Math.min(current + delta, target);
        }
        return Math.max(current - delta, target);
    }

    public static float easeOutCubic(float value) {
        float clamped = class_3532.method_15363((float)value, (float)0.0f, (float)1.0f);
        float inv = 1.0f - clamped;
        return 1.0f - inv * inv * inv;
    }
}

