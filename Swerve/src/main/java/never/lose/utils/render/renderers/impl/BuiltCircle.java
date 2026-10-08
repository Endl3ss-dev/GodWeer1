package never.lose.utils.render.renderers.impl;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import never.lose.utils.render.renderers.IRenderer;
import org.joml.Matrix4f;

import java.awt.Color;

public record BuiltCircle(
        float width,
        float height,
        float r,
        float thickness,
        float start,
        float end,
        Color c1,
        Color c2
) implements IRenderer {

    @Override
    public void render(Matrix4f matrix, float x, float y, float z) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

        BufferBuilder builder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        builder.vertex(matrix, x, y, z).color(c1.getRGB());
        builder.vertex(matrix, x, y + height, z).color(c1.getRGB());
        builder.vertex(matrix, x + width, y + height, z).color(c2.getRGB());
        builder.vertex(matrix, x + width, y, z).color(c2.getRGB());

        BufferRenderer.drawWithGlobalProgram(builder.end());

        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }
}