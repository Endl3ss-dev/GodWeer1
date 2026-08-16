package never.lose.utils.render.builders.impl;

import net.minecraft.client.texture.AbstractTexture;
import never.lose.utils.render.builders.AbstractBuilder;
import never.lose.utils.render.builders.states.QuadColorState;
import never.lose.utils.render.builders.states.QuadRadiusState;
import never.lose.utils.render.builders.states.SizeState;
import never.lose.utils.render.renderers.impl.BuiltTexture;

public final class TextureBuilder extends AbstractBuilder<BuiltTexture> {

    private SizeState       size;
    private QuadRadiusState radius;
    private QuadColorState  color;
    private float           smoothness;
    private float           u, v, texWidth, texHeight;
    private int             textureId;

    public TextureBuilder size(SizeState size)           { this.size = size;            return this; }
    public TextureBuilder radius(QuadRadiusState radius) { this.radius = radius;         return this; }
    public TextureBuilder color(QuadColorState color)    { this.color = color;           return this; }
    public TextureBuilder smoothness(float smoothness)   { this.smoothness = smoothness; return this; }

    public TextureBuilder texture(float u, float v, float tw, float th, AbstractTexture texture) {
        return texture(u, v, tw, th, texture.getGlId());
    }

    public TextureBuilder texture(float u, float v, float tw, float th, int id) {
        this.u = u; this.v = v; this.texWidth = tw; this.texHeight = th; this.textureId = id;
        return this;
    }

    @Override
    protected BuiltTexture _build() {
        return new BuiltTexture(size, radius, color, smoothness, u, v, texWidth, texHeight, textureId);
    }

    @Override
    protected void reset() {
        this.size       = SizeState.NONE;
        this.radius     = QuadRadiusState.NO_ROUND;
        this.color      = QuadColorState.WHITE;
        this.smoothness = 1.0f;
        this.u          = 0; this.v = 0; this.texWidth = 0; this.texHeight = 0; this.textureId = 0;
    }
}