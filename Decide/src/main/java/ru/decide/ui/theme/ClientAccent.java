/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.ui.theme;

import java.awt.Color;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.ui.theme.Theme;
import ru.decide.ui.theme.ThemeManager;

@Environment(value=EnvType.CLIENT)
public final class ClientAccent {
    private static final long SWITCH_MS = 500L;
    private static final int SHADE_COUNT = 7;
    private static final int PALETTE_K = 6;
    private static final int[] displayedPaletteSamples = new int[6];
    private static final int[][] transitionPalettes = new int[10][];
    private static final int[] fallbackPalette = new int[]{0xFFFFFF};
    private static final long RAINBOW_CYCLE_MS = 6000L;
    private static final int[] rainbowShades = new int[7];
    private static State state = State.THEMES;
    private static State transitionTarget = State.THEMES;
    private static boolean rainbowEnabled;
    private static boolean targetIsRainbow;
    private static float[] fromShades;
    private static int[] targetShades;
    private static float fromClosed;
    private static long switchStart;
    private static int[] fromPaletteSamples;
    private static int fromPaletteCount;
    private static int lastDisplayedCount;
    private static boolean hasDisplayedPalette;
    private static long frameId;
    private static long paletteFrameId;
    private static int[] framePalette;
    private static double huePhase;
    private static long hueLast;
    private static float frameHue;
    private static float shadesHue;
    private static float shadesSpread;
    private static float shadesSaturation;
    private static float rainbowSpeed;
    private static float rainbowSpread;
    private static float rainbowSaturation;

    private ClientAccent() {
    }

    public static void setRainbowEnabled(boolean enabled) {
        rainbowEnabled = enabled;
    }

    public static boolean isRainbowEnabled() {
        return rainbowEnabled;
    }

    public static void setRainbowSpeed(float speed) {
        rainbowSpeed = Math.max(0.1f, speed);
    }

    public static void setRainbowSaturation(float sat) {
        rainbowSaturation = ClientAccent.clamp01(sat);
    }

    public static void setRainbowSpread(float spread) {
        rainbowSpread = ClientAccent.clamp01(spread);
    }

    private static float wrap(float f) {
        return f - (float)Math.floor(f);
    }

    public static int toggleOn(float f) {
        return ClientAccent.shade(4, f);
    }

    private static int shade(int n, float f) {
        ClientAccent.refreshState();
        if (state == State.THEMES) {
            return ClientAccent.themeShade(n, f);
        }
        int a = Math.max(0, Math.min(255, Math.round(f)));
        if (a <= 0) {
            return 0;
        }
        if (state == State.RAINBOW) {
            return a << 24 | ClientAccent.rainbowShades()[n] & 0xFFFFFF;
        }
        float p = ClientAccent.progress();
        int target = (targetIsRainbow ? ClientAccent.rainbowShades() : targetShades)[n];
        if (p >= 1.0f) {
            return a << 24 | target & 0xFFFFFF;
        }
        int off = n * 3;
        int r = Math.round(fromShades[off] + ((float)(target >>> 16 & 0xFF) - fromShades[off]) * p);
        int g = Math.round(fromShades[off + 1] + ((float)(target >>> 8 & 0xFF) - fromShades[off + 1]) * p);
        int b = Math.round(fromShades[off + 2] + ((float)(target & 0xFF) - fromShades[off + 2]) * p);
        return a << 24 | r << 16 | g << 8 | b;
    }

    public static int gradientA(float f) {
        return ClientAccent.shade(5, f);
    }

    public static int gradientB(float f) {
        return ClientAccent.shade(6, f);
    }

    private static float clamp01(float f) {
        return f < 0.0f ? 0.0f : (f > 1.0f ? 1.0f : f);
    }

    public static int rgba(int color, float alpha) {
        return ThemeManager.rgba(color, alpha);
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
        return ThemeManager.mix(c1, c2, t);
    }

    public static void beginFrame() {
        long now = System.currentTimeMillis();
        if (hueLast != 0L) {
            long dt = now - hueLast;
            if (dt < 0L) {
                dt = 0L;
            } else if (dt > 200L) {
                dt = 200L;
            }
            huePhase += (double)((float)dt * rainbowSpeed) / 6000.0;
            huePhase -= Math.floor(huePhase);
        }
        hueLast = now;
        frameHue = (float)huePhase;
        ++frameId;
        paletteFrameId = Long.MIN_VALUE;
    }

    public static int accent(float f) {
        return ClientAccent.shade(0, f);
    }

    public static int accentSoft(float f) {
        return ClientAccent.shade(2, f);
    }

    public static int accentBright(float f) {
        return ClientAccent.shade(1, f);
    }

    public static int accentFill(float f) {
        return ClientAccent.shade(3, f);
    }

    public static int[] currentPalette() {
        int[] palette = ClientAccent.blendedClientPalette();
        if (palette != null && palette.length != 0) {
            return palette;
        }
        ClientAccent.fallbackPalette[0] = ClientAccent.accent(255.0f) & 0xFFFFFF;
        return fallbackPalette;
    }

    private static float[] blendedShades() {
        if (state == State.THEMES) {
            return ClientAccent.unpack(ClientAccent.sampleThemeShades());
        }
        float p = ClientAccent.progress();
        int[] target = targetIsRainbow ? ClientAccent.rainbowShades() : targetShades;
        float[] out = new float[21];
        for (int i = 0; i < 7; ++i) {
            int off = i * 3;
            out[off] = fromShades[off] + ((float)(target[i] >>> 16 & 0xFF) - fromShades[off]) * p;
            out[off + 1] = fromShades[off + 1] + ((float)(target[i] >>> 8 & 0xFF) - fromShades[off + 1]) * p;
            out[off + 2] = fromShades[off + 2] + ((float)(target[i] & 0xFF) - fromShades[off + 2]) * p;
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
        float p = ClientAccent.clamp01(t) * (float)(palette.length - 1);
        int i = (int)p;
        if (i > palette.length - 2) {
            i = palette.length - 2;
        }
        return ClientAccent.mixRgb(palette[i], palette[i + 1], p - (float)i);
    }

    public static int rainbowFlow(float offset, float alpha) {
        float h = ClientAccent.wrap(frameHue + offset);
        return ClientAccent.rgba(Color.HSBtoRGB(h, rainbowSaturation, 1.0f) & 0xFFFFFF, alpha);
    }

    public static float rainbowBaseHue() {
        return frameHue;
    }

    public static int gradientColor(float position, float alpha) {
        int[] palette = ClientAccent.currentPalette();
        if (palette.length <= 1) {
            return ClientAccent.rgba(palette[0] & 0xFFFFFF, alpha);
        }
        float p = ClientAccent.clamp01(position) * (float)(palette.length - 1);
        int i = (int)Math.floor(p);
        if (i > palette.length - 2) {
            i = palette.length - 2;
        }
        return ClientAccent.rgba(ClientAccent.mixRgb(palette[i], palette[i + 1], p - (float)i), alpha);
    }

    public static int[] shadesFromCustom(int primary, int secondary, boolean useSecond) {
        int p = primary & 0xFFFFFF;
        int s = useSecond ? secondary & 0xFFFFFF : p;
        return new int[]{p, ClientAccent.lighten(p, 0.65f, 1.15f, 0.1f), ClientAccent.lighten(p, 0.45f, 1.25f, 0.15f), ClientAccent.darken(p, 1.05f, 0.78f), ClientAccent.darken(p, 1.1f, 0.55f), p, s};
    }

    private static int[] rainbowShades() {
        float spread = rainbowSpread;
        float sat = rainbowSaturation;
        float h = frameHue;
        if (h == shadesHue && spread == shadesSpread && sat == shadesSaturation) {
            return rainbowShades;
        }
        int c1 = Color.HSBtoRGB(h, sat, 1.0f) & 0xFFFFFF;
        int c2 = Color.HSBtoRGB(ClientAccent.wrap(h + spread), sat, 1.0f) & 0xFFFFFF;
        ClientAccent.rainbowShades[0] = c1;
        ClientAccent.rainbowShades[1] = Color.HSBtoRGB(h, ClientAccent.clamp01(sat * 0.7f), 1.0f) & 0xFFFFFF;
        ClientAccent.rainbowShades[2] = Color.HSBtoRGB(h, ClientAccent.clamp01(sat * 0.5f), 1.0f) & 0xFFFFFF;
        ClientAccent.rainbowShades[3] = Color.HSBtoRGB(h, sat, 0.8f) & 0xFFFFFF;
        ClientAccent.rainbowShades[4] = Color.HSBtoRGB(h, sat, 0.6f) & 0xFFFFFF;
        ClientAccent.rainbowShades[5] = c1;
        ClientAccent.rainbowShades[6] = c2;
        shadesHue = h;
        shadesSpread = spread;
        shadesSaturation = sat;
        return rainbowShades;
    }

    public static int gradientStops() {
        return Math.max(1, ClientAccent.currentPalette().length);
    }

    private static float currentClosed() {
        if (state == State.RAINBOW) {
            return 1.0f;
        }
        if (state != State.TRANSITION) {
            return 0.0f;
        }
        float f = transitionTarget == State.RAINBOW ? 1.0f : 0.0f;
        return fromClosed + (f - fromClosed) * ClientAccent.progress();
    }

    private static int[] desiredPalette() {
        if (rainbowEnabled) {
            return ClientAccent.generateRainbowPalette();
        }
        return ThemeManager.blendedPalette();
    }

    private static int[] generateRainbowPalette() {
        int count = 6;
        int[] pal = new int[count];
        for (int i = 0; i < count; ++i) {
            float t = count <= 1 ? 0.0f : (float)i / (float)(count - 1);
            float h = ClientAccent.wrap(frameHue + t * rainbowSpread);
            pal[i] = Color.HSBtoRGB(h, rainbowSaturation, 1.0f) & 0xFFFFFF;
        }
        return pal;
    }

    private static void beginTransition(int[] target, boolean rainbow) {
        fromShades = ClientAccent.blendedShades();
        fromClosed = ClientAccent.currentClosed();
        if (hasDisplayedPalette) {
            fromPaletteSamples = (int[])displayedPaletteSamples.clone();
        } else {
            fromPaletteSamples = new int[6];
            ClientAccent.resamplePalette(ClientAccent.desiredPalette(), fromPaletteSamples);
        }
        fromPaletteCount = lastDisplayedCount;
        targetShades = target;
        targetIsRainbow = rainbow;
        switchStart = System.currentTimeMillis();
        state = State.TRANSITION;
    }

    private static int[] sampleThemeShades() {
        return new int[]{ThemeManager.accent(255.0f) & 0xFFFFFF, ThemeManager.accentBright(255.0f) & 0xFFFFFF, ThemeManager.accentSoft(255.0f) & 0xFFFFFF, ThemeManager.accentFill(255.0f) & 0xFFFFFF, ThemeManager.toggleOn(255.0f) & 0xFFFFFF, ThemeManager.gradientA(255.0f) & 0xFFFFFF, ThemeManager.gradientB(255.0f) & 0xFFFFFF};
    }

    private static void refreshState() {
        State desired;
        State state = desired = rainbowEnabled ? State.RAINBOW : State.THEMES;
        if (ClientAccent.state == desired) {
            return;
        }
        if (ClientAccent.state == State.TRANSITION && transitionTarget == desired) {
            if (ClientAccent.progress() >= 1.0f) {
                ClientAccent.state = desired;
            }
        } else {
            boolean rainbow;
            int[] target;
            if (desired == State.RAINBOW) {
                target = ClientAccent.rainbowShades();
                rainbow = true;
            } else {
                target = ClientAccent.sampleThemeShades();
                rainbow = false;
            }
            ClientAccent.beginTransition(target, rainbow);
            transitionTarget = desired;
        }
    }

    public static int[] blendedClientPalette() {
        if (paletteFrameId == frameId && framePalette != null) {
            return framePalette;
        }
        ClientAccent.refreshState();
        int[] computed = ClientAccent.computeClientPalette();
        ClientAccent.resamplePalette(computed, displayedPaletteSamples);
        hasDisplayedPalette = true;
        lastDisplayedCount = Math.max(1, computed.length);
        framePalette = computed;
        paletteFrameId = frameId;
        return computed;
    }

    public static float closedFactor() {
        ClientAccent.refreshState();
        return ClientAccent.currentClosed();
    }

    private static int[] computeClientPalette() {
        int[] target = ClientAccent.desiredPalette();
        if (state == State.TRANSITION && fromPaletteSamples != null) {
            float f;
            float p = ClientAccent.progress();
            if (f < 1.0f) {
                int n = Math.max(Math.max(1, fromPaletteCount), target.length);
                int[] out = transitionPalettes[n];
                if (out == null) {
                    out = new int[n];
                    ClientAccent.transitionPalettes[n] = out;
                }
                for (int i = 0; i < n; ++i) {
                    float t = n <= 1 ? 0.0f : (float)i / (float)(n - 1);
                    out[i] = ClientAccent.mixRgb(ClientAccent.samplePalette(fromPaletteSamples, t), ClientAccent.samplePalette(target, t), p);
                }
                return out;
            }
        }
        return target;
    }

    private static void resamplePalette(int[] source, int[] dest) {
        for (int i = 0; i < dest.length; ++i) {
            float t = dest.length <= 1 ? 0.0f : (float)i / (float)(dest.length - 1);
            dest[i] = ClientAccent.samplePalette(source, t);
        }
    }

    public static boolean isModeTransitioning() {
        ClientAccent.refreshState();
        return state == State.TRANSITION && ClientAccent.progress() < 1.0f;
    }

    public static int accentOpaque() {
        return 0xFF000000 | ClientAccent.shade(0, 255.0f) & 0xFFFFFF;
    }

    private static int themeShade(int n, float f) {
        return switch (n) {
            case 0 -> ThemeManager.accent(f);
            case 1 -> ThemeManager.accentBright(f);
            case 2 -> ThemeManager.accentSoft(f);
            case 3 -> ThemeManager.accentFill(f);
            case 4 -> ThemeManager.toggleOn(f);
            case 5 -> ThemeManager.gradientA(f);
            default -> ThemeManager.gradientB(f);
        };
    }

    private static int mixRgb(int c1, int c2, float t) {
        float p = ClientAccent.clamp01(t);
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

    private static float progress() {
        float p = (float)(System.currentTimeMillis() - switchStart) / 500.0f;
        if (p <= 0.0f) {
            return 0.0f;
        }
        return p >= 1.0f ? 1.0f : p * p * (3.0f - 2.0f * p);
    }

    private static int lighten(int color, float satMul, float briMul, float briAdd) {
        float[] hsb = new float[3];
        Color.RGBtoHSB(color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF, hsb);
        return Color.HSBtoRGB(hsb[0], ClientAccent.clamp01(hsb[1] * satMul), ClientAccent.clamp01(hsb[2] * briMul + briAdd)) & 0xFFFFFF;
    }

    private static int darken(int color, float satMul, float briMul) {
        float[] hsb = new float[3];
        Color.RGBtoHSB(color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF, hsb);
        return Color.HSBtoRGB(hsb[0], ClientAccent.clamp01(hsb[1] * satMul), hsb[2] * briMul) & 0xFFFFFF;
    }

    static {
        fromShades = ClientAccent.unpack(Theme.DECIDEVISUALS.shades());
        targetShades = Theme.DECIDEVISUALS.shades();
        switchStart = System.currentTimeMillis() - 10000L;
        fromPaletteCount = 1;
        lastDisplayedCount = 1;
        paletteFrameId = Long.MIN_VALUE;
        huePhase = 0.0;
        hueLast = 0L;
        shadesHue = Float.NaN;
        shadesSpread = Float.NaN;
        shadesSaturation = Float.NaN;
        rainbowSpeed = 1.0f;
        rainbowSpread = 0.18f;
        rainbowSaturation = 0.85f;
    }

    @Environment(value=EnvType.CLIENT)
    public static enum State {
        THEMES,
        RAINBOW,
        TRANSITION;

    }
}

