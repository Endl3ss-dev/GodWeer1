/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package ru.decide.optimization.velotune;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.decide.optimization.velotune.perf.ConfigManager;
import ru.decide.optimization.velotune.perf.PerformanceGovernor;
import ru.decide.optimization.velotune.perf.VeloTuneConfig;

@Environment(value=EnvType.CLIENT)
public final class VeloTuneManager {
    public static final Logger LOGGER = LoggerFactory.getLogger((String)"VeloTune");
    private static volatile VeloTuneConfig config = new VeloTuneConfig();
    private static final PerformanceGovernor GOVERNOR = new PerformanceGovernor();

    public static void init() {
        config = ConfigManager.load();
        GOVERNOR.configure(config);
        if (!VeloTuneManager.config.enabled) {
            LOGGER.warn("VeloTune is globally disabled in config/velotune.json");
        }
        LOGGER.info("VeloTune 0.4.1 Extreme active: target={} FPS, entities<={} blocks, vertical cutoff={} blocks, bones={} Hz, name-tag text={} Hz, HUD/screens={} Hz, raw input={}, mouse={} Hz (0=unlimited)", new Object[]{VeloTuneManager.config.adaptive.targetFps, VeloTuneManager.config.extreme.entityRenderDistance, VeloTuneManager.config.extreme.verticalCullDepth, VeloTuneManager.config.extreme.playerPoseUpdatesPerSecond, VeloTuneManager.config.extreme.nameTagTextUpdatesPerSecond, VeloTuneManager.config.extreme.hudUpdatesPerSecond, VeloTuneManager.config.extreme.rawInputBuffer, VeloTuneManager.config.extreme.mouseUpdatesPerSecond});
    }

    public static VeloTuneConfig config() {
        return config;
    }

    public static PerformanceGovernor governor() {
        return GOVERNOR;
    }

    public static void saveConfig() {
        config.sanitize();
        GOVERNOR.configure(config);
        ConfigManager.save(config);
    }

    public static boolean enabled() {
        return VeloTuneManager.config.enabled;
    }
}

