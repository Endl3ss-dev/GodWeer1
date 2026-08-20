package org.privates.client.utils.render.draw;

import net.minecraft.util.math.vector.Quaternion;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.mojang.blaze3d.platform.GlStateManager;
import net.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.privates.client.api.interfaces.IMinecraft;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.framebuffer.CustomFramebuffer;
import org.privates.client.utils.render.shader.ShaderManager;

public class LiquidGlassRenderer implements IMinecraft {

    private static CustomFramebuffer backgroundBuffer;

    public static void init() {
        backgroundBuffer = new CustomFramebuffer(false);
        backgroundBuffer.setLinear();
    }

    public static void captureBackground() {
        if (backgroundBuffer == null) {
            init();
        }

        mc.getFramebuffer().bindFramebufferTexture();
        backgroundBuffer.setup(true);
        RenderUtil.start();
        GlStateManager.disableBlend();
        CustomFramebuffer.drawQuads(mc.getMainWindow().getScaledWidth(),
                mc.getMainWindow().getScaledHeight());
        RenderUtil.stop();
        backgroundBuffer.stop();
    }

    public static void drawLiquidGlass(MatrixStack matrix, float x, float y,
                                       float width, float height, float radius) {
        drawLiquidGlass(matrix, x, y, width, height, radius,
                5000.0f, 0.5f, ColorUtil.rgba(255, 255, 255, 30));
    }

    public static void drawLiquidGlass(MatrixStack matrix, float x, float y,
                                       float width, float height, float radius,
                                       float lensStrength, float blurStrength, int color) {
        if (backgroundBuffer == null) {
            init();
        }

        ShaderManager.liquidGlassShader.load();
        ShaderManager.liquidGlassShader.setUniformf("size", width, height);
        ShaderManager.liquidGlassShader.setUniformf("round", radius);
        ShaderManager.liquidGlassShader.setUniformf("lensStrength", lensStrength);
        ShaderManager.liquidGlassShader.setUniformf("blurStrength", blurStrength);
        ShaderManager.liquidGlassShader.setUniformf("glassColor", ColorUtil.getRGBAf(color));
        ShaderManager.liquidGlassShader.setUniformf("position", x, y);
        ShaderManager.liquidGlassShader.setUniformf("screenSize",
                (float) mc.getMainWindow().getScaledWidth(),
                (float) mc.getMainWindow().getScaledHeight());
        ShaderManager.liquidGlassShader.setUniformi("bgTexture", 0);

        GlStateManager.activeTexture(GL13.GL_TEXTURE0);
        backgroundBuffer.bindFramebufferTexture();

        RenderUtil.start();
        RenderUtil.resetColor();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        matrix.push();
        matrix.translate((x + width / 2f), (y + height / 2f), 0);
        matrix.rotate(new Quaternion(180, 0, 0, true));
        matrix.translate(-(x + width / 2f), -(y + height / 2f), 0);
        CustomFramebuffer.drawQuads(matrix, x, y, width, height);
        matrix.pop();

        RenderUtil.stop();
        ShaderManager.liquidGlassShader.unload();
    }

    public static void cleanup() {
        if (backgroundBuffer != null) {
            backgroundBuffer.deleteFramebuffer();
            backgroundBuffer = null;
        }
    }
}