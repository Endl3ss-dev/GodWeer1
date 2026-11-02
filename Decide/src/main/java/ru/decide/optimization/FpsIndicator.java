/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2561
 *  net.minecraft.class_310
 *  net.minecraft.class_332
 */
package ru.decide.optimization;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_332;
import ru.decide.optimization.ModConfig;
import ru.decide.optimization.RenderManager;

@Environment(value=EnvType.CLIENT)
public class FpsIndicator {
    private static final class_310 client = class_310.method_1551();
    private static long lastUpdateTime = 0L;
    private static int frameCount = 0;
    private static double currentFps = 0.0;

    private FpsIndicator() {
    }

    public static void updateFps() {
        ++frameCount;
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastUpdateTime >= 1000L) {
            currentFps = (double)frameCount * 1000.0 / (double)(currentTime - lastUpdateTime);
            frameCount = 0;
            lastUpdateTime = currentTime;
        }
    }

    public static void render(class_332 context) {
        ModConfig config = ModConfig.getInstance();
        if (config.showFpsIndicator && !FpsIndicator.client.field_1690.field_1842 && RenderManager.getInstance().isInitialized()) {
            int effectiveLimit = config.getEffectiveHz();
            boolean unlimited = !config.enableRenderLimit || config.isUnlimitedFps();
            String fpsText = unlimited ? String.format("FPS: %.1f (Unlimited)", currentFps) : String.format("FPS: %.1f / %d Hz", currentFps, effectiveLimit);
            int color = unlimited ? 0xFFFFFF : FpsIndicator.getFpsColor(currentFps, effectiveLimit);
            int x = context.method_51421() - FpsIndicator.client.field_1772.method_1727(fpsText) - 10;
            int y = 10;
            context.method_25294(x - 2, y - 2, x + FpsIndicator.client.field_1772.method_1727(fpsText) + 2, y + 9 + 2, Integer.MIN_VALUE);
            context.method_27535(FpsIndicator.client.field_1772, (class_2561)class_2561.method_43470((String)fpsText), x, y, color);
        }
    }

    private static int getFpsColor(double currentFps, int targetFps) {
        double ratio = currentFps / (double)targetFps;
        if (ratio >= 0.9) {
            return 65280;
        }
        return ratio >= 0.7 ? 0xFFFF00 : 0xFF0000;
    }
}

