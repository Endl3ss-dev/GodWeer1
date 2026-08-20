package fun.volon.api.utils.render;

import com.mojang.blaze3d.systems.RenderSystem;
import fun.volon.api.QClient;
import fun.volon.api.utils.color.ColorUtils;
import fun.volon.client.modules.impl.render.WorldTweaks;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderProgramKey;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import org.joml.Matrix4f;

public final class SkyShaderRenderer implements QClient {

    public static final SkyShaderRenderer INSTANCE = new SkyShaderRenderer();

    private SkyShaderRenderer() {
    }

    public void renderSky() {
        WorldTweaks worldTweaks = WorldTweaks.INSTANCE;
        if (worldTweaks == null || !worldTweaks.isShaderEnabled()) {
            return;
        }

        ShaderProgramKey shaderKey = switch (worldTweaks.getShaderMode()) {
            case "Caustic" -> ShaderUtils.skyCaustic;
            case "Space" -> ShaderUtils.skySpace;
            case "Summer" -> ShaderUtils.skySummer;
            case "Plasma" -> ShaderUtils.skyPlasma;
            case "Balatro" -> ShaderUtils.skyBalatro;
            default -> ShaderUtils.skyNebula;
        };
        ShaderProgram shader = mc.getShaderLoader().getOrCreateProgram(shaderKey);
        if (shader == null) {
            return;
        }

        int color = worldTweaks.getShaderColor();
        float red = ColorUtils.rgba(color)[0] / 255f;
        float green = ColorUtils.rgba(color)[1] / 255f;
        float blue = ColorUtils.rgba(color)[2] / 255f;
        float opacity = Math.max(0f, Math.min(1f, worldTweaks.getShaderOpacity()));

        GlUniform timeUniform = shader.getUniform("Time");
        if (timeUniform != null) timeUniform.set((System.currentTimeMillis() % 1_000_000L) / 1000f);

        GlUniform accentUniform = shader.getUniform("Accent");
        if (accentUniform != null) accentUniform.set(red, green, blue);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();
        RenderSystem.setShaderColor(1f, 1f, 1f, opacity);
        RenderSystem.setShader(shaderKey);

        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        drawSkybox(buffer, new Matrix4f(), 100f);
        BufferRenderer.drawWithGlobalProgram(buffer.end());

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
    }

    public void close() {
    }

    private void drawSkybox(VertexConsumer buffer, Matrix4f matrix, float size) {
        face(buffer, matrix, 1, -size, size, -size, -size, size, size, size, size, size, size, size, -size);
        face(buffer, matrix, 0, -size, -size, size, -size, -size, -size, size, -size, -size, size, -size, size);
        face(buffer, matrix, 2, size, size, -size, size, -size, -size, -size, -size, -size, -size, size, -size);
        face(buffer, matrix, 4, -size, size, size, -size, -size, size, size, -size, size, size, size, size);
        face(buffer, matrix, 3, -size, size, -size, -size, -size, -size, -size, -size, size, -size, size, size);
        face(buffer, matrix, 5, size, size, size, size, -size, size, size, -size, -size, size, size, -size);
    }

    private void face(VertexConsumer buffer, Matrix4f matrix, int face, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4) {
        int column = face % 3;
        int row = face / 3;
        float u1 = column / 3f;
        float u2 = (column + 1) / 3f;
        float v1 = row / 2f;
        float v2 = (row + 1) / 2f;
        buffer.vertex(matrix, x1, y1, z1).texture(u1, v1).color(255, 255, 255, 255);
        buffer.vertex(matrix, x2, y2, z2).texture(u1, v2).color(255, 255, 255, 255);
        buffer.vertex(matrix, x3, y3, z3).texture(u2, v2).color(255, 255, 255, 255);
        buffer.vertex(matrix, x4, y4, z4).texture(u2, v1).color(255, 255, 255, 255);
    }
}
