/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.ui.lv;

import java.awt.Color;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.ui.fonts.FontsLV;
import ru.decide.ui.theme.ThemeManager;
import ru.decide.utils.render.Draw;

@Environment(value=EnvType.CLIENT)
public final class LvRectUtil {
    private static final float DEFAULT_CORNER_RADIUS = 7.0f;
    private static final float COLOR_MOVE_SPEED = 4.1666668E-4f;
    private static float colorOffset = 0.0f;
    private static long lastColorOffsetMs = 0L;
    private static boolean colorMovementActive = true;

    private LvRectUtil() {
    }

    public static float getColorOffset() {
        long now = System.currentTimeMillis();
        if (lastColorOffsetMs == 0L) {
            lastColorOffsetMs = now;
            return colorOffset;
        }
        long dt = Math.min(Math.max(0L, now - lastColorOffsetMs), 100L);
        lastColorOffsetMs = now;
        if (colorMovementActive) {
            colorOffset = LvRectUtil.normalizeOffset(colorOffset + (float)dt * 4.1666668E-4f);
        }
        return colorOffset;
    }

    private static float normalizeOffset(float f) {
        return !Float.isFinite(f) ? 0.0f : f - (float)Math.floor(f);
    }

    public static void rect(float x, float y, float w, float h, int color) {
        Draw.rect(x, y, w, h, color);
    }

    public static void rect(float x, float y, float w, float h, float radius, int color) {
        Draw.rect(x, y, w, h, color, radius);
    }

    public static void roundedRect(float x, float y, float w, float h, float r, int color) {
        Draw.rect(x, y, w, h, color, r);
    }

    public static void glow(float x, float y, float w, float h, int color, float radius, float glowSize, float strength) {
        Draw.glow(x, y, w, h, color, radius, glowSize, strength);
    }

    public static void outline(float x, float y, float w, float h, float thickness, int color) {
        Draw.outline(x, y, w, h, thickness, color);
    }

    public static void outline(float x, float y, float w, float h, float thickness, int color, float radius) {
        Draw.outline(x, y, w, h, thickness, color, radius);
    }

    public static void gradientRect(float x, float y, float w, float h, int[] colors, float radius) {
        Draw.gradientRect(x, y, w, h, colors, radius);
    }

    public static void gradientRect(float x, float y, float w, float h, int[] colors, float tl, float tr, float br, float bl) {
        Draw.gradientRect(x, y, w, h, colors, tl, tr, br, bl);
    }

    public static void gradientRectFromTheme(float x, float y, float w, float h, float radius, float alpha) {
        int cA = ThemeManager.gradientA(alpha);
        int cB = ThemeManager.gradientB(alpha);
        Draw.gradientRect(x, y, w, h, new int[]{cA, cB, cB, cA}, radius);
    }

    public static void drawClientRect(float x, float y, float w, float h) {
        LvRectUtil.drawClientRect(x, y, w, h, 7.0f, 1.0f);
    }

    public static void drawClientRect(float x, float y, float w, float h, float alpha) {
        LvRectUtil.drawClientRect(x, y, w, h, 7.0f, alpha);
    }

    public static void drawClientRect(float x, float y, float w, float h, float radius, float alpha) {
        LvRectUtil.drawClientRect(x, y, w, h, radius, alpha, 0.0f);
    }

    public static void drawClientRect(float x, float y, float w, float h, float radius, float alpha, float extraRadius) {
        float clampedAlpha = Math.max(0.0f, Math.min(1.0f, alpha));
        if (clampedAlpha <= 0.0f || w <= 0.0f || h <= 0.0f) {
            return;
        }
        float r = Math.max(0.0f, Math.min(radius + extraRadius, Math.min(w, h) * 0.5f));
        int primaryColor = ThemeManager.accent(255.0f);
        int secondColor = ThemeManager.gradientB(255.0f);
        float glowStrength = clampedAlpha * 0.5f;
        if (glowStrength > 0.004f) {
            int glowColor = ThemeManager.accent(Math.round(glowStrength * 255.0f));
            Draw.glow(x, y, w, h, glowColor, r, 10.0f, 0.55f, 0.8f);
        }
        Draw.blur(x, y, w, h, clampedAlpha, r, 0);
        float glassAlpha = clampedAlpha * 0.3f;
        int glassTint = ThemeManager.accent(Math.round(glassAlpha * 255.0f));
        Draw.glass(x, y, w, h, glassAlpha, r, r, r, r, glassTint);
        Draw.glassOutline(x, y, w, h, 0.65f, r, clampedAlpha * 0.35f, 0.2f);
    }

    public static void drawClientShape(float x, float y, float w, float h, float alpha) {
        LvRectUtil.drawClientRect(x, y, w, h, 7.0f, alpha);
    }

    public static void drawClientShape(float x, float y, float w, float h, float radius, float alpha) {
        LvRectUtil.drawClientRect(x, y, w, h, radius, alpha);
    }

    public static void drawClientSector(float x, float y, float w, float h, float startAngle, float sweep, float radius, float alpha) {
        float clampedAlpha = Math.max(0.0f, Math.min(1.0f, alpha));
        if (clampedAlpha <= 0.0f || w <= 0.0f || h <= 0.0f) {
            return;
        }
        Draw.blur(x, y, w, h, clampedAlpha, radius, 0);
        int tint = ThemeManager.accent(Math.round(clampedAlpha * 0.3f * 255.0f));
        Draw.glass(x, y, w, h, clampedAlpha * 0.3f, radius, radius, radius, radius, tint);
    }

    public static void drawClientRectNoGlow(float x, float y, float w, float h, float radius, float alpha) {
        float clampedAlpha = Math.max(0.0f, Math.min(1.0f, alpha));
        if (clampedAlpha <= 0.0f || w <= 0.0f || h <= 0.0f) {
            return;
        }
        float r = Math.max(0.0f, Math.min(radius, Math.min(w, h) * 0.5f));
        Draw.blur(x, y, w, h, clampedAlpha, r, 0);
        int tint = ThemeManager.accent(Math.round(clampedAlpha * 0.3f * 255.0f));
        Draw.glass(x, y, w, h, clampedAlpha * 0.3f, r, r, r, r, tint);
    }

    public static void drawPanelBg(float x, float y, float w, float h, float alpha) {
        LvRectUtil.drawPanelBg(x, y, w, h, 7.0f, 7.0f, 7.0f, 7.0f, alpha);
    }

    public static void drawPanelBg(float x, float y, float w, float h, float radius, float alpha) {
        LvRectUtil.drawPanelBg(x, y, w, h, radius, radius, radius, radius, alpha);
    }

    public static void drawPanelBg(float x, float y, float w, float h, float tl, float tr, float br, float bl, float alpha) {
        int a = Math.max(0, Math.min(255, Math.round(40.0f * alpha)));
        if (a > 0) {
            Draw.rect(x, y, w, h, new Color(0, 0, 0, a).getRGB(), tl, tr, br, bl);
        }
    }

    public static float effectiveCornerRadius(float radius, float w, float maxRef) {
        float cap = Math.min(w, maxRef) * 0.5f;
        return Math.min(cap, radius);
    }

    public static float cornerEdgeInset(float radius, float thickness) {
        if (radius <= thickness) {
            return 0.0f;
        }
        float d = radius - thickness;
        return radius - (float)Math.sqrt(Math.max(0.0f, radius * radius - d * d));
    }

    public static void drawDropBackground(float x, float y, float w, float h, float alpha) {
        LvRectUtil.drawClientRect(x, y, w, h, 2.0f, alpha);
    }

    public static void drawString(String text, float x, float y, float size, int color) {
        FontsLV.MONTSERRAT_MEDIUM.draw(text, x, y, size, color);
    }

    public static float stringWidth(String text, float size) {
        return FontsLV.MONTSERRAT_MEDIUM.width(text, size);
    }

    public static void drawCenteredString(String text, float x, float y, float size, int color) {
        FontsLV.MONTSERRAT_MEDIUM.drawCentered(text, x, y, size, color);
    }

    public static void drawIcon(String glyph, float x, float y, float size, int color) {
        FontsLV.DECIDE.msdf(glyph, x, y, size, color);
    }

    public static float iconWidth(String glyph, float size) {
        return FontsLV.DECIDE.msdfWidth(glyph, size);
    }
}

