/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_310
 *  org.lwjgl.glfw.GLFW
 *  org.lwjgl.glfw.GLFWVidMode
 */
package ru.decide.optimization;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import ru.decide.optimization.FrameSyncManager;
import ru.decide.optimization.OptimizationInit;

@Environment(value=EnvType.CLIENT)
public final class FrameSyncHandler {
    private static final long NANOS = 1000000000L;
    private static boolean drawThisFrame = true;
    private static int monitorHz = 60;
    private static long frameInterval = 16666666L;
    private static long lastRenderTime = System.nanoTime();

    private FrameSyncHandler() {
    }

    public static void init(int hz) {
        if (hz <= 0) {
            hz = 60;
        }
        monitorHz = hz;
        frameInterval = 1000000000L / (long)hz;
        lastRenderTime = System.nanoTime();
        drawThisFrame = true;
        OptimizationInit.LOGGER.info("FrameSyncHandler initialized with {} Hz, interval={}ns", (Object)hz, (Object)frameInterval);
    }

    public static void tick() {
        if (!FrameSyncManager.isEnabled()) {
            return;
        }
        int hz = FrameSyncHandler.detectMonitorHz();
        if (hz <= 0) {
            hz = 60;
        }
        if (hz != monitorHz) {
            monitorHz = hz;
            frameInterval = 1000000000L / (long)hz;
            OptimizationInit.LOGGER.info("FrameSyncHandler Hz updated to {}", (Object)hz);
        }
    }

    public static boolean beginFrame() {
        if (!FrameSyncManager.isEnabled()) {
            drawThisFrame = true;
            return true;
        }
        long now = System.nanoTime();
        if (now - lastRenderTime < frameInterval) {
            drawThisFrame = false;
            return false;
        }
        lastRenderTime = now;
        drawThisFrame = true;
        return true;
    }

    public static boolean allowDraw() {
        return drawThisFrame;
    }

    public static int detectMonitorHz() {
        try {
            GLFWVidMode mode;
            class_310 mc = class_310.method_1551();
            long monitor = 0L;
            if (mc != null && mc.method_22683() != null) {
                monitor = GLFW.glfwGetWindowMonitor((long)mc.method_22683().method_4490());
            }
            if (monitor == 0L) {
                monitor = GLFW.glfwGetPrimaryMonitor();
            }
            if (monitor != 0L && (mode = GLFW.glfwGetVideoMode((long)monitor)) != null && mode.refreshRate() > 0) {
                return mode.refreshRate();
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return 60;
    }
}

