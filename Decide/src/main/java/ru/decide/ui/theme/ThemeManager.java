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
import ru.decide.ui.theme.Theme;

@Environment(value=EnvType.CLIENT)
public final class ThemeManager {
    private static final long SWITCH_MS = 500L;
    private static final int SHADE_COUNT = 7;
    private static Theme current = Theme.DECIDEVISUALS;
    private static int[] currentShades = Theme.DECIDEVISUALS.shades();
    private static float[] fromShades = ThemeManager.unpack(Theme.DECIDEVISUALS.shades());
    private static int[] fromPalette = Theme.DECIDEVISUALS.palette();
    private static long switchStart = System.currentTimeMillis() - 10000L;
    private static Runnable onChange = () -> {};

    private ThemeManager() {
    }

    public static void setOnChange(Runnable callback) {
        onChange = callback;
    }

    public static void set(Theme theme) {
        if (theme != null && theme != current) {
            fromShades = ThemeManager.blendedShades();
            fromPalette = ThemeManager.blendedPalette();
            current = theme;
            currentShades = theme.shades();
            switchStart = System.currentTimeMillis();
            onChange.run();
        }
    }

    public static Theme current() {
        return current;
    }

    public static int toggleOn(float alpha) {
        return ThemeManager.shade(4, alpha);
    }

    private static int rgbLerp(int c1, int c2, float t) {
        float p = t < 0.0f ? 0.0f : (t > 1.0f ? 1.0f : t);
        int r1 = c1 >> 16 & 0xFF;
        int g1 = c1 >> 8 & 0xFF;
        int b1 = c1 & 0xFF;
        int r2 = c2 >> 16 & 0xFF;
        int g2 = c2 >> 8 & 0xFF;
        int b2 = c2 & 0xFF;
        int r = Math.round((float)r1 + (float)(r2 - r1) * p);
        int g = Math.round((float)g1 + (float)(g2 - g1) * p);
        int b = Math.round((float)b1 + (float)(b2 - b1) * p);
        return r << 16 | g << 8 | b;
    }

    private static int shade(int index, float alpha) {
        int a = Math.max(0, Math.min(255, Math.round(alpha)));
        if (a <= 0) {
            return 0;
        }
        int target = currentShades[index];
        float p = ThemeManager.progress();
        if (p >= 1.0f) {
            return a << 24 | target & 0xFFFFFF;
        }
        int off = index * 3;
        int r = Math.round(fromShades[off] + ((float)(target >>> 16 & 0xFF) - fromShades[off]) * p);
        int g = Math.round(fromShades[off + 1] + ((float)(target >>> 8 & 0xFF) - fromShades[off + 1]) * p);
        int b = Math.round(fromShades[off + 2] + ((float)(target & 0xFF) - fromShades[off + 2]) * p);
        return a << 24 | r << 16 | g << 8 | b;
    }

    public static int gradientA(float alpha) {
        return ThemeManager.shade(5, alpha);
    }

    public static int gradientB(float alpha) {
        return ThemeManager.shade(6, alpha);
    }

    public static int rgba(int color, float alpha) {
        int a = Math.max(0, Math.min(255, Math.round(alpha)));
        return a <= 0 ? 0 : a << 24 | color & 0xFFFFFF;
    }

    private static float[] unpack(int[] shades) {
        float[] rgb = new float[21];
        for (int i = 0; i < 7; ++i) {
            rgb[i * 3] = shades[i] >>> 16 & 0xFF;
            rgb[i * 3 + 1] = shades[i] >>> 8 & 0xFF;
            rgb[i * 3 + 2] = shades[i] & 0xFF;
        }
        return rgb;
    }

    public static int mix(int c1, int c2, float t) {
        float p = t < 0.0f ? 0.0f : (t > 1.0f ? 1.0f : t);
        int a1 = c1 >>> 24 & 0xFF;
        int r1 = c1 >>> 16 & 0xFF;
        int g1 = c1 >>> 8 & 0xFF;
        int b1 = c1 & 0xFF;
        int a2 = c2 >>> 24 & 0xFF;
        int r2 = c2 >>> 16 & 0xFF;
        int g2 = c2 >>> 8 & 0xFF;
        int b2 = c2 & 0xFF;
        int a = Math.round((float)a1 + (float)(a2 - a1) * p);
        int r = Math.round((float)r1 + (float)(r2 - r1) * p);
        int g = Math.round((float)g1 + (float)(g2 - g1) * p);
        int b = Math.round((float)b1 + (float)(b2 - b1) * p);
        return a <= 0 ? 0 : a << 24 | r << 16 | g << 8 | b;
    }

    public static int accent(float alpha) {
        return ThemeManager.shade(0, alpha);
    }

    public static int accentSoft(float alpha) {
        return ThemeManager.shade(2, alpha);
    }

    public static int accentBright(float alpha) {
        return ThemeManager.shade(1, alpha);
    }

    public static int accentFill(float alpha) {
        return ThemeManager.shade(3, alpha);
    }

    private static float[] blendedShades() {
        float p = ThemeManager.progress();
        float[] out = new float[21];
        int[] cur = currentShades;
        for (int i = 0; i < 7; ++i) {
            int off = i * 3;
            out[off] = fromShades[off] + ((float)(cur[i] >>> 16 & 0xFF) - fromShades[off]) * p;
            out[off + 1] = fromShades[off + 1] + ((float)(cur[i] >>> 8 & 0xFF) - fromShades[off + 1]) * p;
            out[off + 2] = fromShades[off + 2] + ((float)(cur[i] & 0xFF) - fromShades[off + 2]) * p;
        }
        return out;
    }

    private static int samplePalette(int[] palette, float t) {
        if (palette == null || palette.length == 0) {
            return 0;
        }
        if (palette.length == 1) {
            return palette[0] & 0xFFFFFF;
        }
        float p = (t < 0.0f ? 0.0f : (t > 1.0f ? 1.0f : t)) * (float)(palette.length - 1);
        int i = (int)Math.floor(p);
        int j = Math.min(i + 1, palette.length - 1);
        return ThemeManager.rgbLerp(palette[i], palette[j], p - (float)i);
    }

    public static int[] blendedPalette() {
        int[] target = current.palette();
        float p = ThemeManager.progress();
        if (p >= 1.0f) {
            return target;
        }
        int n = Math.max(target.length, fromPalette.length);
        int[] out = new int[n];
        for (int i = 0; i < n; ++i) {
            float t = n <= 1 ? 0.0f : (float)i / (float)(n - 1);
            out[i] = ThemeManager.rgbLerp(ThemeManager.samplePalette(fromPalette, t), ThemeManager.samplePalette(target, t), p);
        }
        return out;
    }

    public static int accentOpaque() {
        return 0xFF000000 | ThemeManager.shade(0, 255.0f) & 0xFFFFFF;
    }

    private static float progress() {
        float p = (float)(System.currentTimeMillis() - switchStart) / 500.0f;
        if (p <= 0.0f) {
            return 0.0f;
        }
        return p >= 1.0f ? 1.0f : p * p * (3.0f - 2.0f * p);
    }
}

