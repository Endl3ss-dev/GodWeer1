package never.lose.utils.render.renderers.impl;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.render.VertexFormats;
import never.lose.utils.render.builders.states.QuadRadiusState;
import never.lose.utils.render.builders.states.SizeState;
import never.lose.utils.render.renderers.IRenderer;
import org.joml.Matrix4f;

import java.awt.Color;

public record BuiltSmoke(
        SizeState size,
        QuadRadiusState radius,
        Color color,
        float smoothness,
        float time
) implements IRenderer {

    @Override
    public void render(Matrix4f matrix, float x, float y, float z) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

        float width = this.size.width();
        float height = this.size.height();
        int rgb = this.color.getRGB();

        BufferBuilder builder = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        builder.vertex(matrix, x, y, z).color(rgb);
        builder.vertex(matrix, x, y + height, z).color(rgb);
        builder.vertex(matrix, x + width, y + height, z).color(rgb);
        builder.vertex(matrix, x + width, y, z).color(rgb);

        BufferRenderer.drawWithGlobalProgram(builder.end());

        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }
}