package tech.lightclient.client.utils.render.main.text;

final class RuntimeTtfGlyph {
    private final int codepoint;
    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final float xOffset;
    private final float yOffset;
    private final float xAdvance;
    private final float u0;
    private final float v0;
    private final float u1;
    private final float v1;

    RuntimeTtfGlyph(int codepoint, int x, int y, int width, int height,
                    float xOffset, float yOffset, float xAdvance,
                    float u0, float v0, float u1, float v1) {
        this.codepoint = codepoint;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.xAdvance = xAdvance;
        this.u0 = u0;
        this.v0 = v0;
        this.u1 = u1;
        this.v1 = v1;
    }

    int codepoint() {
        return codepoint;
    }

    int x() {
        return x;
    }

    int y() {
        return y;
    }

    int width() {
        return width;
    }

    int height() {
        return height;
    }

    float xOffset() {
        return xOffset;
    }

    float yOffset() {
        return yOffset;
    }

    float xAdvance() {
        return xAdvance;
    }

    float u0() {
        return u0;
    }

    float v0() {
        return v0;
    }

    float u1() {
        return u1;
    }

    float v1() {
        return v1;
    }
}
