/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2960
 */
package ru.decide.utils.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2960;
import ru.decide.Client;
import ru.decide.theme.ThemeColor;
import ru.decide.utils.colors.ColorUtil;
import ru.decide.utils.render.BlurPipeline;
import ru.decide.utils.render.Render2D;

@Environment(value=EnvType.CLIENT)
public final class Draw {
    private static final float GLASS_DISTORTION = 125.0f;
    private static final float GLASS_WAVE_SIZE = 75.0f;

    private Draw() {
    }

    private static Render2D r2d() {
        return Client.get().render2D();
    }

    public static void flush() {
        Draw.r2d().flushAll();
    }

    public static void blur(float x, float y, float width, float height, float alpha, int tintColor) {
        Draw.blur(x, y, width, height, alpha, 0.0f, 0.0f, 0.0f, 0.0f, tintColor);
    }

    public static void blur(float x, float y, float width, float height, float alpha, float radius, int tintColor) {
        Draw.blur(x, y, width, height, alpha, radius, radius, radius, radius, tintColor);
    }

    public static void blur(float x, float y, float width, float height, float alpha, float topLeft, float topRight, float bottomRight, float bottomLeft, int tintColor) {
        float[] radii = new float[]{topLeft, topRight, bottomRight, bottomLeft};
        BlurPipeline pipeline = Draw.r2d().getBlurPipeline();
        if (ThemeColor.getDistortion()) {
            pipeline.drawGlassBlur(x, y, width, height, alpha, radii, tintColor, 125.0f, 75.0f, 0.0f, 0.0f);
        } else {
            pipeline.drawBlur(x, y, width, height, alpha, radii, tintColor);
        }
    }

    public static void glass(float x, float y, float width, float height, float alpha, float topLeft, float topRight, float bottomRight, float bottomLeft, int tintColor) {
        float[] radii = new float[]{topLeft, topRight, bottomRight, bottomLeft};
        Draw.r2d().getBlurPipeline().drawGlassBlur(x, y, width, height, alpha, radii, tintColor, 125.0f, 75.0f, 0.0f, 0.0f);
    }

    public static void rect(float x, float y, float width, float height, int color) {
        Draw.rect(x, y, width, height, color, 0.0f);
    }

    public static void rect(float x, float y, float width, float height, int color, float radius) {
        Draw.rect(x, y, width, height, color, radius, radius, radius, radius);
    }

    public static void rect(float x, float y, float width, float height, int color, float topLeft, float topRight, float bottomRight, float bottomLeft) {
        int[] colors = ColorUtil.solid(color);
        float[] radii = new float[]{topLeft, topRight, bottomRight, bottomLeft};
        Draw.r2d().getRectPipeline().drawRect(x, y, width, height, colors, radii);
    }

    public static void gradientRect(float x, float y, float width, float height, int[] colors, float radius) {
        Draw.gradientRect(x, y, width, height, colors, radius, radius, radius, radius);
    }

    public static void gradientRect(float x, float y, float width, float height, int[] colors, float topLeft, float topRight, float bottomRight, float bottomLeft) {
        float[] radii = new float[]{topLeft, topRight, bottomRight, bottomLeft};
        Draw.r2d().getRectPipeline().drawRect(x, y, width, height, colors, radii);
    }

    public static void glow(float x, float y, float width, float height, int color, float radius, float glowSize, float strength) {
        Draw.glow(x, y, width, height, color, radius, radius, radius, radius, glowSize, strength, 1.0f);
    }

    public static void glow(float x, float y, float width, float height, int color, float radius, float glowSize, float strength, float softness) {
        Draw.glow(x, y, width, height, color, radius, radius, radius, radius, glowSize, strength, softness);
    }

    public static void glow(float x, float y, float width, float height, int color, float topLeft, float topRight, float bottomRight, float bottomLeft, float glowSize, float strength, float softness) {
        float[] radii = new float[]{topLeft, topRight, bottomRight, bottomLeft};
        Draw.r2d().getRectPipeline().drawGlow(x, y, width, height, color, radii, glowSize, strength, softness);
    }

    public static void outline(float x, float y, float width, float height, float thickness, int color) {
        Draw.outline(x, y, width, height, thickness, color, 0.0f, 0.0f, 0.0f, 0.0f);
    }

    public static void outline(float x, float y, float width, float height, float thickness, int color, float radius) {
        Draw.outline(x, y, width, height, thickness, color, radius, radius, radius, radius);
    }

    public static void outline(float x, float y, float width, float height, float thickness, int color, float topLeft, float topRight, float bottomRight, float bottomLeft) {
        int[] colors = ColorUtil.solid8(color);
        float[] thicknesses = new float[]{thickness, thickness, thickness, thickness, thickness, thickness, thickness, thickness};
        float[] radii = new float[]{topLeft, topRight, bottomRight, bottomLeft};
        Draw.r2d().getOutlinePipeline().drawOutline(x, y, width, height, colors, thicknesses, radii, 1.0f);
    }

    public static void gradientOutline(float x, float y, float width, float height, float thickness, int colorTL, int colorTR, int colorBR, int colorBL, float radius) {
        Draw.gradientOutline(x, y, width, height, thickness, colorTL, colorTR, colorBR, colorBL, radius, radius, radius, radius);
    }

    public static void gradientOutline(float x, float y, float width, float height, float thickness, int colorTL, int colorTR, int colorBR, int colorBL, float topLeft, float topRight, float bottomRight, float bottomLeft) {
        int[] colors = new int[]{colorBL, ColorUtil.interpolate(colorBL, colorTL, 0.5), colorTL, ColorUtil.interpolate(colorTL, colorTR, 0.5), colorTR, ColorUtil.interpolate(colorTR, colorBR, 0.5), colorBR, ColorUtil.interpolate(colorBR, colorBL, 0.5)};
        float[] thicknesses = new float[]{thickness, thickness, thickness, thickness, thickness, thickness, thickness, thickness};
        float[] radii = new float[]{topLeft, topRight, bottomRight, bottomLeft};
        Draw.r2d().getOutlinePipeline().drawOutline(x, y, width, height, colors, thicknesses, radii, 1.0f);
    }

    public static void glassOutline(float x, float y, float width, float height, float thickness, float radius, float alpha, float shine) {
        Draw.glassOutline(x, y, width, height, thickness, radius, radius, radius, radius, alpha, shine);
    }

    public static void glassOutline(float x, float y, float width, float height, float thickness, float topLeft, float topRight, float bottomRight, float bottomLeft, float alpha, float shine) {
        float base = Math.max(0.0f, alpha);
        float hot = Math.max(0.0f, shine);
        int[] colors = new int[]{Draw.glassColor(base * 0.42f), Draw.glassColor(base * (0.55f + hot * 0.3f)), Draw.glassColor(base * (0.28f + hot * 0.1f)), Draw.glassColor(base * 0.24f), Draw.glassColor(base * (0.34f + hot * 0.16f)), Draw.glassColor(base * (0.48f + hot * 0.24f)), Draw.glassColor(base * 0.3f), Draw.glassColor(base * (0.62f + hot * 0.38f))};
        float[] thicknesses = new float[]{thickness, thickness + hot * 0.18f, thickness * 0.86f, thickness * 0.78f, thickness * 0.9f, thickness + hot * 0.16f, thickness * 0.84f, thickness + hot * 0.24f};
        float[] radii = new float[]{topLeft, topRight, bottomRight, bottomLeft};
        Draw.r2d().getOutlinePipeline().drawOutline(x, y, width, height, colors, thicknesses, radii, 1.35f);
    }

    private static int glassColor(float alpha) {
        return ColorUtil.getColor(255, Math.min(alpha, 1.0f));
    }

    public static void texture(class_2960 id, float x, float y, float width, float height, int color) {
        Draw.texture(id, x, y, width, height, 0.0f, 0.0f, 1.0f, 1.0f, color, 1.0f, 0.0f);
    }

    public static void texture(class_2960 id, float x, float y, float width, float height, float smoothness, float radius, int color) {
        Draw.texture(id, x, y, width, height, 0.0f, 0.0f, 1.0f, 1.0f, color, smoothness, radius);
    }

    public static void texture(class_2960 id, float x, float y, float width, float height, float u0, float v0, float u1, float v1, int color, float smoothness, float radius) {
        int[] colors = new int[]{color, color, color, color};
        float[] radii = new float[]{radius, radius, radius, radius};
        Draw.r2d().getTexturePipeline().drawTexture(id, x, y, width, height, u0, v0, u1, v1, colors, radii, smoothness);
    }

    public static void glowTexture(class_2960 id, float x, float y, float width, float height, int color) {
        int[] colors = new int[]{color, color, color, color};
        float[] radii = new float[]{0.0f, 0.0f, 0.0f, 0.0f};
        Draw.r2d().getTexturePipeline().drawGlowTexture(id, x, y, width, height, 0.0f, 0.0f, 1.0f, 1.0f, colors, radii, 1.0f, 0.0f);
    }

    public static void textureRotated(class_2960 id, float x, float y, float width, float height, float angleDegrees, int color) {
        int[] colors = new int[]{color, color, color, color};
        float[] radii = new float[]{0.0f, 0.0f, 0.0f, 0.0f};
        Draw.r2d().getTexturePipeline().drawTexture(id, x, y, width, height, 0.0f, 0.0f, 1.0f, 1.0f, colors, radii, 1.0f, angleDegrees);
    }

    public static float worldSizeForPixels(float pixels, double distance, float fovDegrees, int screenHeight) {
        if (distance < 0.01 || screenHeight <= 0) {
            return 0.0f;
        }
        double visibleHeight = 2.0 * distance * Math.tan(Math.toRadians(fovDegrees) / 2.0);
        return (float)((double)pixels * visibleHeight / (double)screenHeight);
    }
}

