package never.lose.utils.render.renderers.impl;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.render.VertexFormats;
import never.lose.utils.render.msdf.MsdfFont;
import never.lose.utils.render.providers.ColorProvider;
import never.lose.utils.render.renderers.IRenderer;
import never.lose.utils.render.RenderConst;
import org.joml.Matrix4f;

public record BuiltText(
        MsdfFont font,
        String text,
        float size,
        float thickness,
        int color,
        float smoothness,
        float spacing,
        int outlineColor,
        float outlineThickness
) implements IRenderer {

    @Override
    public void render(Matrix4f matrix, float x, float y, float z) {
        ShaderProgram shader = RenderConst.MSDF_FONT_SHADER;
        if (shader == null || font == null) return;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.setShaderTexture(0, font.getTextureId());
        RenderSystem.setShader(() -> shader);

        boolean outlineEnabled = outlineThickness > 0f;
        shader.getUniform("Range").set(font.getAtlas().range());
        shader.getUniform("Thickness").set(thickness);
        shader.getUniform("Smoothness").set(smoothness);
        shader.getUniform("Outline").set(outlineEnabled ? 1 : 0);

        if (outlineEnabled) {
            shader.getUniform("OutlineThickness").set(outlineThickness);
            float[] oc = ColorProvider.normalize(outlineColor);
            shader.getUniform("OutlineColor").set(oc[0], oc[1], oc[2], oc[3]);
        }

        BufferBuilder builder = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        font.applyGlyphs(matrix, builder, text, size,
                (thickness + outlineThickness * 0.5f) * 0.5f * size, spacing,
                x, y + font.getMetrics().baselineHeight() * size, z, color);

        var buffer = builder.endNullable();
        if (buffer != null) BufferRenderer.drawWithGlobalProgram(buffer);

        RenderSystem.setShaderTexture(0, 0);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

    public void render(Matrix4f matrix, BufferBuilder builder, float x, float y, float z) {
        if (font == null) return;
        font.applyGlyphs(matrix, builder, text, size,
                (thickness + outlineThickness * 0.5f) * 0.5f * size, spacing,
                x, y + font.getMetrics().baselineHeight() * size, z, color);
    }

    public void renderGradient(Matrix4f matrix, float x, float y, float z) {
        ShaderProgram shader = RenderConst.MSDF_FONT_SHADER;
        if (shader == null || font == null) return;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.setShaderTexture(0, font.getTextureId());
        RenderSystem.setShader(() -> shader);

        boolean outlineEnabled = outlineThickness > 0f;
        shader.getUniform("Range").set(font.getAtlas().range());
        shader.getUniform("Thickness").set(thickness);
        shader.getUniform("Smoothness").set(smoothness);
        shader.getUniform("Outline").set(outlineEnabled ? 1 : 0);

        if (outlineEnabled) {
            shader.getUniform("OutlineThickness").set(outlineThickness);
            float[] oc = ColorProvider.normalize(outlineColor);
            shader.getUniform("OutlineColor").set(oc[0], oc[1], oc[2], oc[3]);
        }

        BufferBuilder builder = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        font.applyGlyphsGradient(matrix, builder, text, size,
                (thickness + outlineThickness * 0.5f) * 0.5f * size, spacing,
                x, y + font.getMetrics().baselineHeight() * size, z);

        BufferRenderer.drawWithGlobalProgram(builder.end());

        RenderSystem.setShaderTexture(0, 0);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }
}