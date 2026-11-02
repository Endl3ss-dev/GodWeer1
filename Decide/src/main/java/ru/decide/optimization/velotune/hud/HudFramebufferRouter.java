/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_276
 */
package ru.decide.optimization.velotune.hud;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_276;

@Environment(value=EnvType.CLIENT)
public final class HudFramebufferRouter {
    private static class_276 captureTarget;
    private static boolean screenCapture;

    private HudFramebufferRouter() {
    }

    public static void beginCapture(class_276 target) {
        captureTarget = target;
        screenCapture = false;
    }

    public static void beginScreenCapture(class_276 target) {
        captureTarget = target;
        screenCapture = true;
    }

    public static void endCapture() {
        captureTarget = null;
        screenCapture = false;
    }

    public static class_276 captureTarget() {
        return captureTarget;
    }

    public static boolean isScreenCapture() {
        return captureTarget != null && screenCapture;
    }
}

