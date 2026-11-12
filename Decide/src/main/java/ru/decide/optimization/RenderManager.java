/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.optimization;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.optimization.OptimizationInit;
import ru.decide.optimization.RenderLimiter;

@Environment(value=EnvType.CLIENT)
public class RenderManager {
    private static RenderManager instance;
    private RenderLimiter renderLimiter;
    private boolean initialized = false;

    private RenderManager() {
    }

    public static RenderManager getInstance() {
        if (instance == null) {
            instance = new RenderManager();
        }
        return instance;
    }

    public void initialize(int monitorHz, boolean enabled) {
        if (this.initialized) {
            OptimizationInit.LOGGER.warn("RenderManager already initialized!");
        } else {
            this.renderLimiter = new RenderLimiter(monitorHz);
            this.renderLimiter.setEnabled(enabled);
            this.initialized = true;
            OptimizationInit.LOGGER.info("RenderManager initialized with {} Hz, enabled: {}", (Object)monitorHz, (Object)enabled);
        }
    }

    public boolean shouldRender() {
        return this.initialized && this.renderLimiter != null ? this.renderLimiter.canRender() : true;
    }

    public void updateSettings(int monitorHz, boolean enabled) {
        if (!this.initialized) {
            this.initialize(monitorHz, enabled);
        } else {
            this.renderLimiter.setMonitorHz(monitorHz);
            this.renderLimiter.setEnabled(enabled);
        }
    }

    public RenderLimiter getRenderLimiter() {
        return this.renderLimiter;
    }

    public boolean isInitialized() {
        return this.initialized;
    }
}

