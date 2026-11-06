package tech.lightclient.client.utils.render.main.text;

import tech.lightclient.client.utils.render.main.core.Renderer2D;

import java.util.Objects;

final class RuntimeTtfTextRenderer implements FontRenderer {
    private static final float[] IDENTITY_TRANSFORM = new float[]{
            1f, 0f, 0f,
            0f, 1f, 0f,
            0f, 0f, 1f
    };

    private final Renderer2D renderer;
    private final RuntimeTtfFont font;

    RuntimeTtfTextRenderer(Renderer2D renderer, RuntimeTtfFont font) {
        this.renderer = Objects.requireNonNull(renderer, "renderer");
        this.font = Objects.requireNonNull(font, "font");
    }

    @Override
    public void drawText(float x, float y, float size, String text, int rgbaPremul) {
        drawText(x, y, size, text, rgbaPremul, "l", IDENTITY_TRANSFORM);
    }

    @Override
    public void drawText(float x, float y, float size, String text, int rgbaPremul, float[] transform) {
        drawText(x, y, size, text, rgbaPremul, "l", transform);
    }

    @Override
    public void drawText(float x, float y, float size, String text, int rgbaPremul, String alignKey) {
        drawText(x, y, size, text, rgbaPremul, alignKey, IDENTITY_TRANSFORM);
    }

    @Override
    public void drawText(float x, float y, float size, String text, int rgbaPremul, String alignKey, float[] transform) {
        drawInternal(x, y, size, text, rgbaPremul, rgbaPremul, alignKey, transform, false);
    }

    @Override
    public void drawGradientText(float x, float y, float size, String text, int startColor, int endColor) {
        drawGradientText(x, y, size, text, startColor, endColor, "l", IDENTITY_TRANSFORM);
    }

    @Override
    public void drawGradientText(float x, float y, float size, String text, int startColor, int endColor, String alignKey) {
        drawGradientText(x, y, size, text, startColor, endColor, alignKey, IDENTITY_TRANSFORM);
    }

    @Override
    public void drawGradientText(float x, float y, float size, String text, int startColor, int endColor, String alignKey, float[] transform) {
        drawInternal(x, y, size, text, startColor, endColor, alignKey, transform, true);
    }

    @Override
    public TextRenderer.TextMetrics measureText(String text, float size) {
        if (text == null || text.isEmpty() || size <= 0f) {
            return new TextRenderer.TextMetrics(0f, 0f);
        }

        float scale = size / Math.max(1.0e-6f, font.rasterSize());
        float maxWidth = 0f;
        float currentWidth = 0f;
        int lines = 1;

        for (int offset = 0; offset < text.length(); ) {
            int codepoint = text.codePointAt(offset);
            offset += Character.charCount(codepoint);
            if (codepoint == '\r') {
                continue;
            }
            if (codepoint == '\n') {
                maxWidth = Math.max(maxWidth, currentWidth);
                currentWidth = 0f;
                lines++;
                continue;
            }
            RuntimeTtfGlyph glyph = font.glyph(codepoint);
            if (glyph != null) {
                currentWidth += glyph.xAdvance() * scale;
            }
        }

        maxWidth = Math.max(maxWidth, currentWidth);
        return new TextRenderer.TextMetrics(maxWidth, lines * font.lineHeight() * scale);
    }

    private void drawInternal(float x, float y, float size, String text,
                              int startColor, int endColor, String alignKey,
                              float[] transform, boolean gradient) {
        if (text == null || text.isEmpty() || size <= 0f) {
            return;
        }

        float[] activeTransform = transform != null ? transform : IDENTITY_TRANSFORM;
        float scale = size / Math.max(1.0e-6f, font.rasterSize());
        float baseX = alignedX(x, text, size, alignKey);
        float penX = baseX;
        float penY = y;
        int glyphIndex = 0;
        int glyphCount = Math.max(1, text.codePointCount(0, text.length()) - 1);

        for (int offset = 0; offset < text.length(); ) {
            int codepoint = text.codePointAt(offset);
            offset += Character.charCount(codepoint);
            if (codepoint == '\r') {
                continue;
            }
            if (codepoint == '\n') {
                penX = baseX;
                penY += font.lineHeight() * scale;
                continue;
            }

            RuntimeTtfGlyph glyph = font.glyph(codepoint);
            if (glyph == null) {
                continue;
            }

            int color = gradient ? lerpColor(startColor, endColor, glyphIndex / (float) glyphCount) : startColor;
            if (glyph.width() > 0 && glyph.height() > 0) {
                float drawX = penX + glyph.xOffset() * scale;
                float drawY = penY - glyph.yOffset() * scale;
                renderer.drawTextureRegionDirect(
                        font.textureId(),
                        drawX,
                        drawY,
                        glyph.width() * scale,
                        glyph.height() * scale,
                        glyph.u0(),
                        glyph.v0(),
                        glyph.u1(),
                        glyph.v1(),
                        color,
                        activeTransform);
            }

            penX += glyph.xAdvance() * scale;
            glyphIndex++;
        }
    }

    private float alignedX(float x, String text, float size, String alignKey) {
        String key = alignKey == null ? "l" : alignKey;
        if ("c".equalsIgnoreCase(key)) {
            return x - measureText(text, size).width * 0.5f;
        }
        if ("r".equalsIgnoreCase(key)) {
            return x - measureText(text, size).width;
        }
        return x;
    }

    private static int lerpColor(int startColor, int endColor, float progress) {
        float clamped = Math.max(0f, Math.min(1f, progress));
        int startA = (startColor >>> 24) & 0xFF;
        int startR = (startColor >>> 16) & 0xFF;
        int startG = (startColor >>> 8) & 0xFF;
        int startB = startColor & 0xFF;
        int endA = (endColor >>> 24) & 0xFF;
        int endR = (endColor >>> 16) & 0xFF;
        int endG = (endColor >>> 8) & 0xFF;
        int endB = endColor & 0xFF;

        int a = Math.round(startA + (endA - startA) * clamped);
        int r = Math.round(startR + (endR - startR) * clamped);
        int g = Math.round(startG + (endG - startG) * clamped);
        int b = Math.round(startB + (endB - startB) * clamped);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
