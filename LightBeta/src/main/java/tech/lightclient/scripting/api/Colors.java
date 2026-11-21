package tech.lightclient.scripting.api;

/**
 * Colour helpers exposed to scripts as {@code Colors}. A colour is a plain {@code Int}
 * in {@code 0xAARRGGBB} form, exactly as in the rest of the API.
 */
public final class Colors {

    public static final int WHITE = 0xFFFFFFFF;
    public static final int BLACK = 0xFF000000;
    public static final int RED = 0xFFFF5555;
    public static final int GREEN = 0xFF55FF55;
    public static final int BLUE = 0xFF5555FF;
    public static final int CYAN = 0xFF55FFFF;
    public static final int YELLOW = 0xFFFFFF55;
    public static final int ORANGE = 0xFFFFAA00;
    public static final int PURPLE = 0xFFAA00AA;
    public static final int PINK = 0xFFFF55FF;
    public static final int GRAY = 0xFFAAAAAA;
    public static final int DARK_GRAY = 0xFF555555;

    public static int rgb(int r, int g, int b) {
        return rgba(r, g, b, 255);
    }

    public static int rgba(int r, int g, int b, int a) {
        return (clamp(a) << 24) | (clamp(r) << 16) | (clamp(g) << 8) | clamp(b);
    }

    public static int alpha(int argb) { return (argb >>> 24) & 0xFF; }

    public static int red(int argb) { return (argb >> 16) & 0xFF; }

    public static int green(int argb) { return (argb >> 8) & 0xFF; }

    public static int blue(int argb) { return argb & 0xFF; }

    public static int withAlpha(int argb, int a) {
        return (clamp(a) << 24) | (argb & 0x00FFFFFF);
    }

    /** Dims a colour by scaling its alpha. */
    public static int fade(int argb, float factor) {
        return withAlpha(argb, Math.round(alpha(argb) * clamp01(factor)));
    }

    public static int mix(int a, int b, float t) {
        float f = clamp01(t);
        return rgba(
                Math.round(red(a) + (red(b) - red(a)) * f),
                Math.round(green(a) + (green(b) - green(a)) * f),
                Math.round(blue(a) + (blue(b) - blue(a)) * f),
                Math.round(alpha(a) + (alpha(b) - alpha(a)) * f)
        );
    }

    /** Colour from hue/saturation/brightness, all 0..1. */
    public static int hsb(float hue, float saturation, float brightness) {
        return 0xFF000000 | (java.awt.Color.HSBtoRGB(hue, clamp01(saturation), clamp01(brightness)) & 0x00FFFFFF);
    }

    private static int clamp(int v) {
        return Math.max(0, Math.min(255, v));
    }

    private static float clamp01(float v) {
        return Math.max(0f, Math.min(1f, v));
    }

    private Colors() {}
}
