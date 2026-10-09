package swerve.utils.render.msdf;

import com.mojang.blaze3d.vertex.VertexConsumer;

import org.joml.Matrix4f;

public final class MsdfGlyph {

    private final int code;
    private final float minU;
    private final float maxU;
    private final float minV;
    private final float maxV;
    private final float advance;
    private final float topPosition;
    private final float width;
    private final float height;
    private final boolean space;

    public MsdfGlyph(int unicode, float advance,
                     float planeLeft, float planeTop, float planeRight, float planeBottom,
                     float atlasLeft, float atlasTop, float atlasRight, float atlasBottom,
                     float atlasWidth, float atlasHeight) {
        this.code = unicode;
        this.advance = advance;
        this.space = Character.isSpaceChar(unicode);

        if (atlasLeft != 0.0f || atlasRight != 0.0f || atlasTop != 0.0f || atlasBottom != 0.0f) {
            this.minU = atlasLeft / atlasWidth;
            this.maxU = atlasRight / atlasWidth;
            this.minV = 1.0f - atlasTop / atlasHeight;
            this.maxV = 1.0f - atlasBottom / atlasHeight;
        } else {
            this.minU = 0.0f;
            this.maxU = 0.0f;
            this.minV = 0.0f;
            this.maxV = 0.0f;
        }

        if (planeLeft != 0.0f || planeRight != 0.0f || planeTop != 0.0f || planeBottom != 0.0f) {
            this.width = planeRight - planeLeft;
            this.height = planeTop - planeBottom;
            this.topPosition = planeTop;
        } else {
            this.width = 0.0f;
            this.height = 0.0f;
            this.topPosition = 0.0f;
        }
    }

    public float apply(Matrix4f matrix, VertexConsumer consumer, float size,
                       float x, float y, float z, int argb) {
        if (this.width == 0.0f || this.height == 0.0f) {
            return getAdvance(size);
        }

        int alpha = (argb >>> 24) & 0xFF;
        int red = (argb >> 16) & 0xFF;
        int green = (argb >> 8) & 0xFF;
        int blue = argb & 0xFF;

        float top = y - this.topPosition * size - 1.0f;
        float w = this.width * size;
        float h = this.height * size;

        consumer.addVertex(matrix, x, top, z).setUv(this.minU, this.minV).setColor(red, green, blue, alpha);
        consumer.addVertex(matrix, x, top + h, z).setUv(this.minU, this.maxV).setColor(red, green, blue, alpha);
        consumer.addVertex(matrix, x + w, top + h, z).setUv(this.maxU, this.maxV).setColor(red, green, blue, alpha);
        consumer.addVertex(matrix, x + w, top, z).setUv(this.maxU, this.minV).setColor(red, green, blue, alpha);

        return getAdvance(size);
    }

    // Метрика намеренно перенесена 1:1 из исходного клиента: для видимых глифов шаг берётся
    // от ширины плоскости при (size - 1), а advance учитывается только у пробельных символов.
    // Замена на честный advance * size сломает совпадение с уже настроенными в UI координатами.
    public float getAdvance(float size) {
        return this.width * (size - 1.0f) + (this.space ? this.advance * size : 0.0f);
    }

    public float getWidth(float size) {
        return getAdvance(size);
    }

    // Сырые метрики msdf-атласа для newgui: тот клиент рендерит от верхнего края текста
    // и шагает на advance * size — конвенции, отличные от FontRenderer.
    public float getSourceAdvance() {
        return this.advance;
    }

    public float getTopPosition() {
        return this.topPosition;
    }

    public float getPlaneWidth() {
        return this.width;
    }

    public float getPlaneHeight() {
        return this.height;
    }

    public float getMinU() {
        return this.minU;
    }

    public float getMaxU() {
        return this.maxU;
    }

    public float getMinV() {
        return this.minV;
    }

    public float getMaxV() {
        return this.maxV;
    }

    public int getCharCode() {
        return this.code;
    }
}
