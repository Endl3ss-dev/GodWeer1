package never.lose.utils.render.renderers.impl;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.SimpleFramebuffer;
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

public record BuiltBlur(
        SizeState size,
        QuadRadiusState radius,
        QuadColorState color,
        float smoothness,
        float blurRadius
) implements IRenderer {

    private static final Supplier<SimpleFramebuffer> TEMP_FBO_SUPPLIER = Suppliers
            .memoize(() -> new SimpleFramebuffer(1920, 1024, false, false));

    @Override
    public void render(Matrix4f matrix, float x, float y, float z) {
        ShaderProgram shader = RenderConst.BLUR_SHADER;
        Framebuffer mainFbo = MinecraftClient.getInstance().getFramebuffer();
        if (shader == null || mainFbo == null) return;

        SimpleFramebuffer fbo = TEMP_FBO_SUPPLIER.get();
        if (fbo.textureWidth != mainFbo.textureWidth || fbo.textureHeight != mainFbo.textureHeight) {
            fbo.resize(mainFbo.textureWidth, mainFbo.textureHeight, false);
        }

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();

        fbo.beginWrite(false);
        mainFbo.draw(fbo.textureWidth, fbo.textureHeight);
        fbo.endWrite();
        mainFbo.beginWrite(false);

        RenderSystem.setShaderTexture(0, fbo.getColorAttachment());
        RenderSystem.setShader(() -> shader);

        float width  = this.size.width();
        float height = this.size.height();
        shader.getUniform("Size").set(width, height);
        shader.getUniform("Radius").set(radius.radius1(), radius.radius2(), radius.radius3(), radius.radius4());
        shader.getUniform("Smoothness").set(smoothness);
        shader.getUniform("BlurRadius").set(blurRadius);

        BufferBuilder builder = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        builder.vertex(matrix, x,         y,          z).color(color.color1());
        builder.vertex(matrix, x,         y + height, z).color(color.color2());
        builder.vertex(matrix, x + width, y + height, z).color(color.color3());
        builder.vertex(matrix, x + width, y,          z).color(color.color4());

        BufferRenderer.drawWithGlobalProgram(builder.end());

        RenderSystem.setShaderTexture(0, 0);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }
}