/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_276
 *  net.minecraft.class_6367
 */
package ru.decide.optimization.velotune.hud;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_276;
import net.minecraft.class_6367;
import ru.decide.optimization.velotune.hud.HudFramebufferRouter;
import ru.decide.optimization.velotune.perf.TextRefreshThrottle;

@Environment(value=EnvType.CLIENT)
public final class ScreenFramebufferCache {
    private static class_276 framebuffer;
    private static Object owner;
    private static boolean active;
    private static boolean valid;
    private static boolean captureFrame;
    private static boolean renderingCapture;
    private static long lastCaptureNanos;
    private static volatile boolean invalidated;

    private ScreenFramebufferCache() {
    }

    public static void beginFrame(Object candidate, int width, int height, long now, int rate) {
        active = true;
        if (owner != candidate) {
            owner = candidate;
            valid = false;
            invalidated = true;
        }
        ScreenFramebufferCache.ensureFramebuffer(width, height);
        captureFrame = invalidated || TextRefreshThrottle.shouldRefresh(valid, now - lastCaptureNanos, rate);
    }

    public static void disable() {
        active = false;
        captureFrame = false;
        renderingCapture = false;
        HudFramebufferRouter.endCapture();
    }

    public static boolean active() {
        return active && framebuffer != null;
    }

    public static boolean shouldCaptureFrame() {
        return ScreenFramebufferCache.active() && captureFrame;
    }

    public static void beginRenderCapture() {
        if (!ScreenFramebufferCache.shouldCaptureFrame()) {
            return;
        }
        renderingCapture = true;
        HudFramebufferRouter.beginScreenCapture(framebuffer);
    }

    public static boolean isRenderingCapture() {
        return renderingCapture;
    }

    public static void finishRenderCapture(long now) {
        if (!renderingCapture) {
            return;
        }
        HudFramebufferRouter.endCapture();
        renderingCapture = false;
        captureFrame = false;
        invalidated = false;
        valid = true;
        lastCaptureNanos = now;
    }

    public static void composite(class_276 target) {
        if (valid && framebuffer != null && target != framebuffer) {
            framebuffer.method_68445(target.method_71639());
        }
    }

    public static void invalidate() {
        invalidated = true;
    }

    private static void ensureFramebuffer(int width, int height) {
        if (framebuffer != null && ScreenFramebufferCache.framebuffer.field_1482 == width && ScreenFramebufferCache.framebuffer.field_1481 == height) {
            return;
        }
        if (framebuffer != null) {
            framebuffer.method_1238();
        }
        framebuffer = new class_6367("VeloTune GUI Cache", width, height, true);
        valid = false;
        invalidated = true;
    }

    static {
        invalidated = true;
    }
}

