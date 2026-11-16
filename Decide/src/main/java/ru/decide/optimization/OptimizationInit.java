/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
 *  net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
 *  net.minecraft.class_304
 *  net.minecraft.class_304$class_11900
 *  net.minecraft.class_310
 *  net.minecraft.class_3675$class_307
 *  net.minecraft.class_437
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package ru.decide.optimization;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.class_304;
import net.minecraft.class_310;
import net.minecraft.class_3675;
import net.minecraft.class_437;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.decide.optimization.ConfigScreen;
import ru.decide.optimization.ModConfig;
import ru.decide.optimization.RenderManager;

@Environment(value=EnvType.CLIENT)
public class OptimizationInit {
    public static final Logger LOGGER = LoggerFactory.getLogger((String)"achrone-framesync");
    private static class_304 configKeyBinding;
    private static boolean renderSystemInitialized;

    private OptimizationInit() {
    }

    public static void init() {
        configKeyBinding = KeyBindingHelper.registerKeyBinding((class_304)new class_304("Achrone Optimization Config", class_3675.class_307.field_1668, 296, class_304.class_11900.field_62556));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            OptimizationInit.initializeRenderSystemSafely();
            while (configKeyBinding.method_1436()) {
                if (client.field_1755 != null) continue;
                client.method_1507((class_437)new ConfigScreen(null));
            }
        });
    }

    private static void initializeRenderSystemSafely() {
        if (renderSystemInitialized) {
            return;
        }
        class_310 client = class_310.method_1551();
        if (client == null || client.method_22683() == null) {
            return;
        }
        try {
            ModConfig config = ModConfig.getInstance();
            RenderManager.getInstance().initialize(config.getEffectiveHz(), config.enableRenderLimit);
            renderSystemInitialized = true;
            LOGGER.info("Render system initialized with {} Hz", (Object)config.getEffectiveHz());
        }
        catch (Exception e) {
            LOGGER.error("Failed to initialize render system: {}", (Object)e.getMessage());
        }
    }
}

