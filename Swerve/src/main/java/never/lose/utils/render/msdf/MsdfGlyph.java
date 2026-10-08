package never.lose.utils.render.msdf;

import net.minecraft.client.render.VertexConsumer;
import org.joml.Matrix4f;

public final class MsdfGlyph {

    private final int   code;
    private final float minU, maxU, minV, maxV;
    private final float advance, topPosition, width, height;

    public MsdfGlyph(FontData.GlyphData data, float atlasWidth, float atlasHeight) {
        this.code    = data.unicode();
        this.advance = data.advance();

        FontData.BoundsData atlas = data.atlasBounds();
        if (atlas != null) {
            this.minU = atlas.left()   / atlasWidth;
            this.maxU = atlas.right()  / atlasWidth;
            this.minV = 1f - atlas.top()    / atlasHeight;
            this.maxV = 1f - atlas.bottom() / atlasHeight;
        } else {
            this.minU = this.maxU = this.minV = this.maxV = 0f;
        }

        FontData.BoundsData plane = data.planeBounds();
        if (plane != null) {
            this.width       = plane.right() - plane.left();
            this.height      = plane.top()   - plane.bottom();
            this.topPosition = plane.top();
        } else {
            this.width = this.height = this.topPosition = 0f;
        }
    }

    public float apply(Matrix4f matrix, VertexConsumer consumer,
                       float size, float x, float y, float z, int color) {
        y -= this.topPosition * size;
        float w = this.width  * size;
        float h = this.height * size;
        consumer.vertex(matrix, x,     y,     z).texture(minU, minV).color(color);
        consumer.vertex(matrix, x,     y + h, z).texture(minU, maxV).color(color);
        consumer.vertex(matrix, x + w, y + h, z).texture(maxU, maxV).color(color);
        consumer.vertex(matrix, x + w, y,     z).texture(maxU, minV).color(color);
        return this.advance * size;
    }

    public float getWidth(float size) { return this.advance * size; }
    public int   getCharCode()        { return code;                }
}