package never.lose.utils.render;

import never.lose.utils.render.providers.ColorProvider;

import java.awt.Color;

public final class Draw {

    private Draw() {}

    public static void round(float x, float y, float width, float height, float radius, int color) {
        Color c = ColorProvider.asColor(color);
        Render.round(x, y, width, height, radius, radius, radius, radius, 1f, c, c, c, c);
    }

    public static void round(float x, float y, float width, float height, float tl, float bl, float br, float tr, int color) {
        Color c = ColorProvider.asColor(color);
        Render.round(x, y, width, height, tl, bl, br, tr, 1f, c, c, c, c);
    }

    public static void round(float x, float y, float width, float height, float radius, int c1, int c2, int c3, int c4) {
        Render.round(x, y, width, height, radius, radius, radius, radius, 1f,
                ColorProvider.asColor(c1), ColorProvider.asColor(c2),
                ColorProvider.asColor(c3), ColorProvider.asColor(c4));
    }

    public static void blur(float x, float y, float width, float height, float radius, int color, float intensity) {
        Color c = ColorProvider.asColor(color);
        Render.blur(x, y, width, height, radius, radius, radius, radius, 1f, intensity, c, c, c, c);
    }

    public static void blur(float x, float y, float width, float height, float tl, float bl, float br, float tr, int color, float intensity) {
        Color c = ColorProvider.asColor(color);
        Render.blur(x, y, width, height, tl, bl, br, tr, 1f, intensity, c, c, c, c);
    }

    public static void text(String font, String text, float x, float y, int color, float size) {
        Render.text(text, x, y, font, size, ColorProvider.asColor(color));
    }

    public static void text(String font, String text, float x, float y, int color, float size, float fadeoutStart, float fadeoutEnd, float maxWidth) {
        if (width(font, text, size) <= maxWidth) {
            text(font, text, x, y, color, size);
            return;
        }
        String shortened = text;
        while (shortened.length() > 1 && width(font, shortened + "...", size) > maxWidth) {
            shortened = shortened.substring(0, shortened.length() - 1);
        }
        text(font, shortened + "...", x, y, color, size);
    }

    public static float width(String font, String text, float size) {
        return Render.textWidth(text, font, size);
    }

    public static void circle(float centerX, float centerY, float radius, int color) {
        float diameter = radius * 2f;
        Color c = ColorProvider.asColor(color);
        Render.round(centerX - radius, centerY - radius, diameter, diameter,
                radius, radius, radius, radius, 1f, c, c, c, c);
    }
}
