/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.textures.GpuTexture
 *  com.mojang.blaze3d.textures.GpuTextureView
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_276
 *  net.minecraft.class_310
 */
package ru.decide.utils.render;

import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_276;
import net.minecraft.class_310;
import ru.decide.module.impl.render.Optimizer;
import ru.decide.theme.ThemeColor;
import ru.decide.utils.render.DrawBatcher;
import ru.decide.utils.render.KawaseBlurPipeline;

@Environment(value=EnvType.CLIENT)
public class ScreenBlur {
    private static final KawaseBlurPipeline pipeline = new KawaseBlurPipeline();
    private static GpuTextureView blurredView = null;
    private static int cachedWidth = 0;
    private static int cachedHeight = 0;
    private static boolean usedThisFrame = false;
    private static boolean usedLastFrame = true;

    public static void markUsed() {
        usedThisFrame = true;
    }

    public static void frame() {
        boolean wanted = usedThisFrame || usedLastFrame;
        usedLastFrame = usedThisFrame;
        usedThisFrame = false;
        if (!wanted) {
            blurredView = null;
            return;
        }
        int strength = ThemeColor.getBlur();
        ScreenBlur.doCapture(3, 2.0f);
    }

    public static void capture() {
        usedLastFrame = true;
        int strength = ThemeColor.getBlur();
        ScreenBlur.doCapture(3, 2.0f);
    }

    public static void capture(int size) {
        usedLastFrame = true;
        ScreenBlur.doCapture(size, size);
    }

    private static void doCapture(int iterations, float offset) {
        class_276 fb;
        class_310 mc = class_310.method_1551();
        if (mc == null) {
            return;
        }
        if (Optimizer.isNoBlur()) {
            blurredView = null;
            return;
        }
        if (Optimizer.active()) {
            iterations = Optimizer.blurSteps();
            offset = Optimizer.blurOffset();
        }
        if ((fb = mc.method_1522()) == null) {
            return;
        }
        DrawBatcher.flushPending();
        try {
            GpuTexture colorAttachment = fb.method_30277();
            GpuTextureView colorAttachmentView = fb.method_71639();
            if (colorAttachment == null || colorAttachmentView == null) {
                blurredView = null;
                return;
            }
            int w = fb.field_1482;
            int h = fb.field_1481;
            if (w <= 0 || h <= 0) {
                blurredView = null;
                return;
            }
            blurredView = pipeline.blur(colorAttachment, colorAttachmentView, w, h, iterations, offset);
            if (blurredView != null) {
                cachedWidth = w;
                cachedHeight = h;
            }
        }
        catch (Exception e) {
            blurredView = null;
        }
    }

    public static boolean isReady() {
        return blurredView != null;
    }

    public static GpuTextureView getBlurredView() {
        return blurredView;
    }

    public static int getCachedWidth() {
        return cachedWidth;
    }

    public static int getCachedHeight() {
        return cachedHeight;
    }

    public static void invalidate() {
        blurredView = null;
    }

    public static void close() {
        blurredView = null;
        pipeline.close();
    }
}

