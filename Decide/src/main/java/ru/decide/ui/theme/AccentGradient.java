/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.ui.theme;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.ui.theme.ThemeManager;
import ru.decide.utils.render.Draw;
import ru.decide.utils.render.font.Font;
import ru.decide.utils.render.font.Fonts;

@Environment(value=EnvType.CLIENT)
public final class AccentGradient {
    private AccentGradient() {
    }

    private static int rgba(int r, int g, int b, float alpha) {
        int a = Math.max(0, Math.min(255, Math.round(alpha)));
        return a <= 0 ? 0 : a << 24 | r << 16 | g << 8 | b;
    }

    private static int samplePalette(int[] palette, float t) {
        if (palette == null || palette.length == 0) {
            return 0;
        }
        if (palette.length == 1) {
            return palette[0];
        }
        float p = Math.max(0.0f, Math.min(1.0f, t)) * (float)(palette.length - 1);
        int i = (int)Math.floor(p);
        int j = Math.min(i + 1, palette.length - 1);
        return AccentGradient.rgbLerp(palette[i], palette[j], p - (float)i);
    }

    private static int rgbLerp(int c1, int c2, float t) {
        float p = Math.max(0.0f, Math.min(1.0f, t));
        int r = Math.round((float)(c1 >> 16 & 0xFF) + (float)((c2 >> 16 & 0xFF) - (c1 >> 16 & 0xFF)) * p);
        int g = Math.round((float)(c1 >> 8 & 0xFF) + (float)((c2 >> 8 & 0xFF) - (c1 >> 8 & 0xFF)) * p);
        int b = Math.round((float)(c1 & 0xFF) + (float)((c2 & 0xFF) - (c1 & 0xFF)) * p);
        return r << 16 | g << 8 | b;
    }

    private static int themeColor(float position, float alpha) {
        int a = Math.max(0, Math.min(255, Math.round(alpha)));
        if (a <= 0) {
            return 0;
        }
        int[] palette = ThemeManager.blendedPalette();
        int rgb = AccentGradient.samplePalette(palette, position);
        return a << 24 | rgb;
    }

    public static void msdfIcon(String fontName, String glyph, float x, float y, float size, float alpha) {
        AccentGradient.msdfIcon(fontName, glyph, x, y, size, alpha, 0.5f);
    }

    public static void msdfIcon(String fontName, String glyph, float x, float y, float size, float alpha, float position) {
        if (alpha <= 0.0f || glyph == null || glyph.isEmpty()) {
            return;
        }
        float t = position < 0.0f ? 0.0f : (position > 1.0f ? 1.0f : position);
        int color = AccentGradient.themeColor(t * 0.7f, alpha);
        Font font = switch (fontName) {
            case "montserrat-medium", "MONT" -> Fonts.sf_medium;
            case "small-pixel", "SMALL" -> Fonts.sf_bold;
            case "decidevisuals", "DV" -> Fonts.gui;
            default -> Fonts.sf_medium;
        };
        font.draw(glyph, x, y, size, color);
    }

    public static void fillHorizontal(float x, float y, float w, float h, float radii, float alphaInt) {
        AccentGradient.paletteFill(x, y, w, h, radii, 2, 1.0f, alphaInt);
    }

    public static void fillVertical(float x, float y, float w, float h, float radii, float alphaInt) {
        AccentGradient.paletteFill(x, y, w, h, radii, 1, 1.0f, alphaInt);
    }

    public static void overlayTrackShade(float x, float y, float w, float h, float radii, float alphaInt) {
        AccentGradient.paletteFill(x, y, w, h, radii, 2, 0.6f, alphaInt * 0.3f);
    }

    private static void paletteFill(float x, float y, float w, float h, float radii, int mode, float intensity, float alphaInt) {
        int[] nArray;
        if (w <= 0.0f || h <= 0.0f || alphaInt <= 0.0f) {
            return;
        }
        float alphaFactor = Math.min(1.0f, alphaInt / 255.0f * intensity);
        if (alphaFactor <= 0.0f) {
            return;
        }
        int alphaChannel = Math.round(alphaFactor * 255.0f);
        int cA = AccentGradient.themeColor(0.0f, alphaChannel);
        int cB = AccentGradient.themeColor(1.0f, alphaChannel);
        switch (mode) {
            case 1: {
                int[] nArray2 = new int[4];
                nArray2[0] = cA;
                nArray2[1] = cA;
                nArray2[2] = cB;
                nArray = nArray2;
                nArray2[3] = cB;
                break;
            }
            case 3: {
                int[] nArray3 = new int[4];
                nArray3[0] = cA;
                nArray3[1] = cB;
                nArray3[2] = cA;
                nArray = nArray3;
                nArray3[3] = cB;
                break;
            }
            default: {
                int[] nArray4 = new int[4];
                nArray4[0] = cA;
                nArray4[1] = cB;
                nArray4[2] = cB;
                nArray = nArray4;
                nArray4[3] = cA;
            }
        }
        int[] colors = nArray;
        Draw.gradientRect(x, y, w, h, colors, radii, radii, radii, radii);
    }

    public static void fillHorizontalLoop(float x, float y, float w, float h, float radii, float alphaInt) {
        AccentGradient.paletteFill(x, y, w, h, radii, 3, 1.0f, alphaInt);
    }

    public static void overlayKnobFade(float left, float right, float y, float h, float radii, float alphaInt) {
        if (right - left <= 0.5f || alphaInt <= 0.0f) {
            return;
        }
        float segLeft = left + (right - left) * 0.66666f;
        float segW = right - segLeft;
        if (segW <= 0.5f) {
            return;
        }
        int c = AccentGradient.rgba(0, 0, 0, alphaInt * 0.61f);
        Draw.rect(segLeft, y, segW, h, c, radii);
    }

    public static float categoryListIndexT(String categoryName) {
        if (categoryName == null) {
            return 0.0f;
        }
        String[] all = new String[]{"VISUALS", "HUD", "UTILITIES"};
        for (int i = 0; i < all.length; ++i) {
            if (!all[i].equals(categoryName)) continue;
            return (float)i / (float)Math.max(1, all.length - 1);
        }
        return 0.0f;
    }

    public static void fillKnob(float x, float y, float w, float h, float radii, float alphaInt) {
        AccentGradient.fillVertical(x, y, w, h, radii, alphaInt);
    }

    public static boolean usesDual() {
        return ThemeManager.current().gradientA() != ThemeManager.current().gradientB();
    }
}

