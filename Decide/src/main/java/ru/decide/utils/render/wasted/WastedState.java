/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_243
 *  net.minecraft.class_3532
 */
package ru.decide.utils.render.wasted;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_243;
import net.minecraft.class_3532;

@Environment(value=EnvType.CLIENT)
public final class WastedState {
    public static final long FADE_IN_MS = 900L;
    public static final long TEXT_DELAY_MS = 1100L;
    private static boolean active;
    private static long startedAtMs;
    private static long durationMs;
    private static class_243 anchor;
    private static float startYaw;
    private static boolean detached;

    private WastedState() {
    }

    public static void begin(class_243 deathPosition, float yaw, long duration) {
        active = true;
        startedAtMs = System.currentTimeMillis();
        durationMs = Math.max(1000L, duration);
        anchor = deathPosition;
        startYaw = yaw;
        detached = false;
    }

    public static void stop() {
        active = false;
        detached = false;
    }

    public static void setDetached(boolean value) {
        detached = value;
    }

    public static boolean isDetached() {
        return detached && active;
    }

    public static boolean isActive() {
        if (active && WastedState.elapsedMs() >= durationMs) {
            active = false;
        }
        return active;
    }

    public static long elapsedMs() {
        return System.currentTimeMillis() - startedAtMs;
    }

    public static float progress() {
        return class_3532.method_15363((float)((float)WastedState.elapsedMs() / (float)durationMs), (float)0.0f, (float)1.0f);
    }

    public static float strength() {
        float rise = class_3532.method_15363((float)((float)WastedState.elapsedMs() / 900.0f), (float)0.0f, (float)1.0f);
        return rise * rise * (3.0f - 2.0f * rise);
    }

    public static class_243 anchor() {
        return anchor;
    }

    public static float startYaw() {
        return startYaw;
    }

    public static float flash() {
        float t = (float)WastedState.elapsedMs() / 320.0f;
        if (t >= 1.0f) {
            return 0.0f;
        }
        float fall = 1.0f - t;
        return fall * fall;
    }

    public static float radialBlur() {
        float t = (float)WastedState.elapsedMs() / 1400.0f;
        if (t >= 1.0f) {
            return 0.0f;
        }
        float fall = 1.0f - t;
        return fall * fall;
    }

    public static float textAlpha() {
        long elapsed = WastedState.elapsedMs();
        if (elapsed < 1100L) {
            return 0.0f;
        }
        float in = class_3532.method_15363((float)((float)(elapsed - 1100L) / 550.0f), (float)0.0f, (float)1.0f);
        float out = 1.0f - class_3532.method_15363((float)((WastedState.progress() - 0.85f) / 0.15f), (float)0.0f, (float)1.0f);
        return in * in * (3.0f - 2.0f * in) * out;
    }

    static {
        anchor = class_243.field_1353;
    }
}

