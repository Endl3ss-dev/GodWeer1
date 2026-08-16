package never.lose.utils.render.renderers.impl;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.render.VertexFormats;
import never.lose.utils.render.builders.states.QuadColorState;
import never.lose.utils.render.builders.states.QuadRadiusState;
import never.lose.utils.render.builders.states.SizeState;
import never.lose.utils.render.renderers.IRenderer;
import never.lose.utils.render.RenderConst;
import org.joml.Matrix4f;

public record BuiltTexture(
        SizeState size,
        QuadRadiusState radius,
        QuadColorState color,
        float smoothness,
        float u, float v,
        float texWidth, float texHeight,
        int textureId
) implements IRenderer {

    @Override
    public void render(Matrix4f matrix, float x, float y, float z) {
        ShaderProgram shader = RenderConst.TEXTURE_SHADER;
        if (shader == null || this.textureId == 0) return;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.setShaderTexture(0, this.textureId);
        RenderSystem.setShader(() -> shader);

        float width  = this.size.width();
        float height = this.size.height();

        shader.getUniform("Size").set(width, height);
        shader.getUniform("Radius").set(radius.radius1(), radius.radius2(), radius.radius3(), radius.radius4());
        shader.getUniform("Smoothness").set(this.smoothness);

        BufferBuilder builder = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        builder.vertex(matrix, x,          y,          z).texture(this.u,                 this.v).color(this.color.color1());
        builder.vertex(matrix, x,          y + height, z).texture(this.u,                 this.v + this.texHeight).color(this.color.color2());
        builder.vertex(matrix, x + width,  y + height, z).texture(this.u + this.texWidth, this.v + this.texHeight).color(this.color.color3());
        builder.vertex(matrix, x + width,  y,          z).texture(this.u + this.texWidth, this.v).color(this.color.color4());

        BufferRenderer.drawWithGlobalProgram(builder.end());

        RenderSystem.setShaderTexture(0, 0);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }
}