/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.module.impl.render.emotions;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class EmoteTiming {
    private EmoteTiming() {
    }

    public static double frame(double tick, long last, long now, boolean paused) {
        if (paused) {
            return tick;
        }
        double delta = Math.max(0.0, Math.min(0.99999, (double)(now - last) * 2.0E-8));
        return tick + delta;
    }

    public static float intro(double tick) {
        double t = Math.max(0.0, Math.min(1.0, tick / 4.0));
        return (float)(t * t * (3.0 - 2.0 * t));
    }

    public static float angle(float from, float to, float weight) {
        return from + (float)Math.IEEEremainder(to - from, Math.PI * 2) * weight;
    }
}

