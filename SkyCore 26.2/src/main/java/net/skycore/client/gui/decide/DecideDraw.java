package net.skycore.client.gui.decide;

import net.minecraft.resources.Identifier;
import net.skycore.client.util.BeginFrameHelper;
import net.skycore.client.util.TextAlignment;
import net.skycore.client.util.TextHeightHelper;

public final class DecideDraw {
    public static final Identifier TEX_BACKGROUND = Identifier.parse("skycore:textures/decide/frame/background.png");
    public static final Identifier TEX_RECT_GUI = Identifier.parse("skycore:textures/decide/frame/rectgui.png");
    public static final Identifier TEX_RECT_THEME = Identifier.parse("skycore:textures/decide/frame/rectthemegui.png");
    public static final Identifier TEX_LOGO = Identifier.parse("skycore:textures/decide/frame/logo.png");
    public static final Identifier TEX_TOP_GLOW = Identifier.parse("skycore:textures/decide/effects/top_glow.png");
    public static final Identifier TEX_MARKER = Identifier.parse("skycore:textures/decide/hud/marker.png");

    private DecideDraw() {
    }

    public static float clamp(float v, float min, float max) {
        return Math.max(min, Math.min(max, v));
    }

    public static int clampByte(int v) {
        return Math.max(0, Math.min(255, v));
    }

    public static int getColor(int gray) {
        int g = clampByte(gray);
        return 0xFF000000 | (g << 16) | (g << 8) | g;
    }

    public static int getColor(int gray, float alpha01) {
        int g = clampByte(gray);
        int a = clampByte(Math.round(clamp(alpha01, 0.0f, 1.0f) * 255.0f));
        return (a << 24) | (g << 16) | (g << 8) | g;
    }

    public static int getColor(int r, int g, int b, float alpha01) {
        int a = clampByte(Math.round(clamp(alpha01, 0.0f, 1.0f) * 255.0f));
        return (a << 24) | (clampByte(r) << 16) | (clampByte(g) << 8) | clampByte(b);
    }

    public static int replAlpha(int color, float alpha01) {
        int a = clampByte(Math.round(clamp(alpha01, 0.0f, 1.0f) * 255.0f));
        return (color & 0x00FFFFFF) | (a << 24);
    }

    public static int multAlpha(int color, float factor) {
        int a = (color >>> 24) & 0xFF;
        int na = clampByte(Math.round(a * clamp(factor, 0.0f, 1.0f)));
        return (color & 0x00FFFFFF) | (na << 24);
    }

    public static int multDark(int color, float factor) {
        float f = clamp(factor, 0.0f, 1.0f);
        int a = (color >>> 24) & 0xFF;
        int r = clampByte(Math.round(((color >>> 16) & 0xFF) * f));
        int g = clampByte(Math.round(((color >>> 8) & 0xFF) * f));
        int b = clampByte(Math.round((color & 0xFF) * f));
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    public static int overCol(int c1, int c2, float t) {
        float f = clamp(t, 0.0f, 1.0f);
        int a1 = (c1 >>> 24) & 0xFF;
        int r1 = (c1 >>> 16) & 0xFF;
        int g1 = (c1 >>> 8) & 0xFF;
        int b1 = c1 & 0xFF;
        int a2 = (c2 >>> 24) & 0xFF;
        int r2 = (c2 >>> 16) & 0xFF;
        int g2 = (c2 >>> 8) & 0xFF;
        int b2 = c2 & 0xFF;
        int a = clampByte(Math.round(a1 + (a2 - a1) * f));
        int r = clampByte(Math.round(r1 + (r2 - r1) * f));
        int g = clampByte(Math.round(g1 + (g2 - g1) * f));
        int b = clampByte(Math.round(b1 + (b2 - b1) * f));
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    public static void rgbToHsb(int r, int g, int b, float[] out) {
        int cmax = Math.max(r, Math.max(g, b));
        int cmin = Math.min(r, Math.min(g, b));
        float brightness = cmax / 255.0f;
        float saturation = cmax != 0 ? (float) (cmax - cmin) / (float) cmax : 0.0f;
        float hue;
        if (saturation == 0.0f) {
            hue = 0.0f;
        } else {
            float redc = (float) (cmax - r) / (float) (cmax - cmin);
            float greenc = (float) (cmax - g) / (float) (cmax - cmin);
            float bluec = (float) (cmax - b) / (float) (cmax - cmin);
            if (r == cmax) {
                hue = bluec - greenc;
            } else if (g == cmax) {
                hue = 2.0f + redc - bluec;
            } else {
                hue = 4.0f + greenc - redc;
            }
            hue /= 6.0f;
            if (hue < 0.0f) {
                hue += 1.0f;
            }
        }
        out[0] = hue;
        out[1] = saturation;
        out[2] = brightness;
    }

    public static int hsbToRgb(float hue, float saturation, float brightness) {
        int r = 0;
        int g = 0;
        int b = 0;
        if (saturation <= 0.0f) {
            r = g = b = clampByte(Math.round(brightness * 255.0f));
        } else {
            float h = (hue - (float) Math.floor(hue)) * 6.0f;
            float f = h - (float) Math.floor(h);
            float p = brightness * (1.0f - saturation);
            float q = brightness * (1.0f - saturation * f);
            float t = brightness * (1.0f - saturation * (1.0f - f));
            switch ((int) h) {
                case 0 -> {
                    r = Math.round(brightness * 255.0f);
                    g = Math.round(t * 255.0f);
                    b = Math.round(p * 255.0f);
                }
                case 1 -> {
                    r = Math.round(q * 255.0f);
                    g = Math.round(brightness * 255.0f);
                    b = Math.round(p * 255.0f);
                }
                case 2 -> {
                    r = Math.round(p * 255.0f);
                    g = Math.round(brightness * 255.0f);
                    b = Math.round(t * 255.0f);
                }
                case 3 -> {
                    r = Math.round(p * 255.0f);
                    g = Math.round(q * 255.0f);
                    b = Math.round(brightness * 255.0f);
                }
                case 4 -> {
                    r = Math.round(t * 255.0f);
                    g = Math.round(p * 255.0f);
                    b = Math.round(brightness * 255.0f);
                }
                default -> {
                    r = Math.round(brightness * 255.0f);
                    g = Math.round(p * 255.0f);
                    b = Math.round(q * 255.0f);
                }
            }
        }
        return 0xFF000000 | (clampByte(r) << 16) | (clampByte(g) << 8) | clampByte(b);
    }

    public static void rect(float x, float y, float w, float h, int color, float radius) {
        if (((color >>> 24) & 0xFF) == 0 || w <= 0.0f || h <= 0.0f) {
            return;
        }
        BeginFrameHelper.rect(x, y, w, h).color(color).radius(radius).hudGlow(false).draw();
    }

    public static void gradientHRect(float x, float y, float w, float h, int leftColor, int rightColor,
                                     float tl, float tr, float br, float bl) {
        if (w <= 0.0f || h <= 0.0f) {
            return;
        }
        if (((leftColor >>> 24) & 0xFF) == 0 && ((rightColor >>> 24) & 0xFF) == 0) {
            return;
        }
        BeginFrameHelper.rect(x, y, w, h)
                .color(leftColor, rightColor, rightColor, leftColor)
                .radius(tl, tr, br, bl)
                .hudGlow(false)
                .draw();
    }

    public static void outline(float x, float y, float w, float h, float thickness, int color, float radius) {
        if (((color >>> 24) & 0xFF) == 0 || w <= 0.0f || h <= 0.0f || thickness <= 0.0f) {
            return;
        }
        BeginFrameHelper.rect(x, y, w, h)
                .color(0)
                .border(Math.max(0.5f, thickness), color)
                .radius(radius)
                .hudGlow(false)
                .draw();
    }

    public static void glow(float x, float y, float w, float h, int color, float radius, float blurRadius) {
        if (((color >>> 24) & 0xFF) == 0 || blurRadius <= 0.0f) {
            return;
        }
        float gw = Math.max(0.2f, w);
        float gh = Math.max(0.2f, h);
        BeginFrameHelper.rect(x, y, gw, gh)
                .color(0)
                .shadow(blurRadius, color)
                .radius(radius)
                .hudGlow(false)
                .draw();
    }

    public static void blur(float x, float y, float w, float h, float opacity, float radius, int tintColor) {
        if (opacity <= 0.01f || w <= 0.0f || h <= 0.0f) {
            return;
        }
        BeginFrameHelper.rect(x, y, w, h)
                .color(tintColor)
                .radius(radius)
                .blur(14.0f, clamp(opacity, 0.0f, 1.0f))
                .hudGlow(false)
                .draw();
    }

    public static void texture(Identifier id, float x, float y, float w, float h, int color) {
        if (((color >>> 24) & 0xFF) == 0 || w <= 0.0f || h <= 0.0f) {
            return;
        }
        BeginFrameHelper.texture(x, y, w, h, id).color(color).draw();
    }

    public static void managedTexture(Identifier id, float x, float y, float w, float h, int color) {
        if (((color >>> 24) & 0xFF) == 0 || w <= 0.0f || h <= 0.0f) {
            return;
        }
        BeginFrameHelper.texture(x, y, w, h, id).managed().color(color).draw();
    }

    public static float textWidth(TextHeightHelper font, String text, float size) {
        if (text == null || text.isEmpty() || size <= 0.0f) {
            return 0.0f;
        }
        return font.getWidth(text, size);
    }

    public static float lineHeight(TextHeightHelper font, float size) {
        return font.lineHeight(size);
    }

    public static void text(TextHeightHelper font, String text, float x, float y, float size, int color) {
        if (text == null || text.isEmpty() || ((color >>> 24) & 0xFF) == 0 || size <= 0.0f) {
            return;
        }
        float baselineFix = 0.95f * size - font.ascender(size);
        BeginFrameHelper.text(x, y + baselineFix, size, text)
                .font(font)
                .color(color)
                .align(TextAlignment.LEFT)
                .draw();
    }

    public static void textCentered(TextHeightHelper font, String text, float cx, float y, float size, int color) {
        if (text == null || text.isEmpty() || ((color >>> 24) & 0xFF) == 0 || size <= 0.0f) {
            return;
        }
        float baselineFix = 0.95f * size - font.ascender(size);
        BeginFrameHelper.text(cx, y + baselineFix, size, text)
                .font(font)
                .color(color)
                .align(TextAlignment.CENTER)
                .draw();
    }

    public static void textFading(TextHeightHelper font, String text, float x, float y, float maxWidth, int color, float size) {
        if (text == null || text.isEmpty() || ((color >>> 24) & 0xFF) == 0 || size <= 0.0f || maxWidth <= 1.0f) {
            return;
        }
        float fullW = textWidth(font, text, size);
        if (fullW <= maxWidth) {
            text(font, text, x, y, size, color);
            return;
        }
        BeginFrameHelper.pushScissor(x, y - size * 0.35f, maxWidth, size * 1.75f);
        try {
            text(font, text, x, y, size, color);
        } finally {
            BeginFrameHelper.popScissor();
        }
    }

    public static void textFadingReverse(TextHeightHelper font, String text, float x, float y, float maxWidth, int color, float size) {
        if (text == null || text.isEmpty() || ((color >>> 24) & 0xFF) == 0 || size <= 0.0f || maxWidth <= 1.0f) {
            return;
        }
        float fullW = textWidth(font, text, size);
        if (fullW <= maxWidth) {
            text(font, text, x, y, size, color);
            return;
        }
        BeginFrameHelper.pushScissor(x, y - size * 0.35f, maxWidth, size * 1.75f);
        try {
            text(font, text, x + (maxWidth - fullW), y, size, color);
        } finally {
            BeginFrameHelper.popScissor();
        }
    }

    /**
     * Precomputes wrapped lines once per (module, width, size) so the render hot path never allocates Strings.
     */
    public static String[] wrapLines(TextHeightHelper font, String text, float maxWidth, float size) {
        if (text == null || text.isEmpty()) {
            return EMPTY_LINES;
        }
        if (maxWidth <= 4.0f || textWidth(font, text, size) <= maxWidth) {
            return new String[]{text};
        }
        String[] words = text.split(" ");
        java.util.ArrayList<String> lines = new java.util.ArrayList<>(4);
        StringBuilder cur = new StringBuilder();
        for (String w : words) {
            if (cur.isEmpty()) {
                cur.append(w);
            } else {
                String candidate = cur + " " + w;
                if (textWidth(font, candidate, size) <= maxWidth) {
                    cur.append(' ').append(w);
                } else {
                    lines.add(cur.toString());
                    cur.setLength(0);
                    cur.append(w);
                }
            }
        }
        if (!cur.isEmpty()) {
            lines.add(cur.toString());
        }
        return lines.toArray(EMPTY_LINES);
    }

    private static final String[] EMPTY_LINES = new String[0];

    public static void drawWrappedLines(TextHeightHelper font, String[] lines, float x, float y, float size, int color) {
        if (lines == null || lines.length == 0 || ((color >>> 24) & 0xFF) == 0) {
            return;
        }
        float lineH = font.lineHeight(size);
        for (int i = 0; i < lines.length; i++) {
            text(font, lines[i], x, y + i * lineH, size, color);
        }
    }
}
