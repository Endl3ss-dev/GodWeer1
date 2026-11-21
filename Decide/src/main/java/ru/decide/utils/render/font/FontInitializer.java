/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package ru.decide.utils.render.font;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.decide.Client;
import ru.decide.utils.render.font.FontRenderer;

@Environment(value=EnvType.CLIENT)
public class FontInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"client/FontInitializer");
    private static boolean registered = false;
    private static boolean initialized = false;

    public static void register() {
        if (registered) {
            return;
        }
        registered = true;
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!initialized && client.method_1478() != null && client.method_22683() != null) {
                try {
                    FontRenderer fontRenderer = Client.get().render2D().getFontRenderer();
                    if (fontRenderer != null && !fontRenderer.isInitialized()) {
                        fontRenderer.initialize();
                        initialized = true;
                        LOGGER.info("Fonts initialized successfully");
                    }
                }
                catch (Exception e) {
                    LOGGER.error("Failed to initialize fonts", (Throwable)e);
                }
            }
        });
    }

    public static boolean isInitialized() {
        return initialized;
    }
}

