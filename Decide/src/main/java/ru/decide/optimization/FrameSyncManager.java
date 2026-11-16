/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_310
 */
package ru.decide.optimization;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;
import ru.decide.optimization.FrameSyncHandler;
import ru.decide.optimization.ModConfig;
import ru.decide.optimization.OptimizationInit;

@Environment(value=EnvType.CLIENT)
public class FrameSyncManager {
    private static FrameSyncManager instance;
    private boolean enabled;

    public static void applyLwjglTweaks() {
        System.setProperty("org.lwjgl.util.NoChecks", "true");
        System.setProperty("org.lwjgl.util.Debug", "false");
    }

    private FrameSyncManager() {
    }

    public static FrameSyncManager getInstance() {
        if (instance == null) {
            instance = new FrameSyncManager();
        }
        return instance;
    }

    public static void init() {
        ModConfig config = ModConfig.getInstance();
        int hz = config.autoDetectMonitorHz ? FrameSyncManager.detectMonitorHz() : config.targetMonitorHz;
        FrameSyncHandler.init(hz);
        OptimizationInit.LOGGER.info("FrameSyncManager initialized with {} Hz", (Object)hz);
    }

    public static void setEnabled(boolean enabled) {
        FrameSyncManager.getInstance().enabled = enabled;
        if (enabled) {
            ModConfig config = ModConfig.getInstance();
            int hz = config.autoDetectMonitorHz ? FrameSyncManager.detectMonitorHz() : config.targetMonitorHz;
            FrameSyncHandler.init(hz);
        }
        OptimizationInit.LOGGER.info("FrameSyncManager {}", (Object)(enabled ? "enabled" : "disabled"));
    }

    public static boolean isEnabled() {
        return FrameSyncManager.getInstance().enabled;
    }

    private static int detectMonitorHz() {
        try {
            class_310 mc = class_310.method_1551();
            if (mc != null && mc.method_22683() != null) {
                return FrameSyncHandler.detectMonitorHz();
            }
        }
        catch (Exception e) {
            OptimizationInit.LOGGER.debug("Client not ready for monitor detection: {}", (Object)e.getMessage());
        }
        return 60;
    }
}

