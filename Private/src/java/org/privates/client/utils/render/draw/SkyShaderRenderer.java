package org.privates.client.utils.render.draw;

import net.minecraft.client.Minecraft;
import net.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.opengl.GL11;
import org.privates.client.utils.render.shader.ShaderManager;

public class SkyShaderRenderer {
    private static final Minecraft mc = Minecraft.getInstance();
    private static ShaderManager nebulaShader;
    private static ShaderManager cosmicShader;
    private static ShaderManager wavesShader;
    private static ShaderManager auroraShader;

    public static void drawNebula(float time, float intensity) {
        if (nebulaShader == null) {
            nebulaShader = ShaderManager.skyNebula;
        }
        draw(nebulaShader, time, intensity);
    }

    public static void drawCosmic(float time, float intensity) {
        if (cosmicShader == null) {
            cosmicShader = ShaderManager.skyCosmic;
        }
        draw(cosmicShader, time, intensity);
    }

    public static void drawWaves(float time, float intensity) {
        if (wavesShader == null) {
            wavesShader = ShaderManager.skyWaves;
        }
        draw(wavesShader, time, intensity);
    }

    public static void drawAurora(float time, float intensity) {
        if (auroraShader == null) {
            auroraShader = ShaderManager.skyAurora;
        }
        draw(auroraShader, time, intensity);
    }

    private static void draw(ShaderManager shader, float time, float intensity) {
        if (shader == null) return;

        // Save current matrices
        RenderSystem.pushMatrix();
        RenderSystem.loadIdentity();
        
        // Setup orthographic projection for 2D overlay
        RenderSystem.matrixMode(GL11.GL_PROJECTION);
        RenderSystem.pushMatrix();
        RenderSystem.loadIdentity();
        RenderSystem.ortho(0, mc.getMainWindow().getScaledWidth(), 
                          mc.getMainWindow().getScaledHeight(), 0, -1, 1);
        RenderSystem.matrixMode(GL11.GL_MODELVIEW);
        
        // Setup blend for overlay
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.disableAlphaTest();
        RenderSystem.enableTexture();
        
        shader.load();
        setupUniforms(shader, time, intensity);

        shader.setUniformi("u_diffuse_sampler", 0);

        // Bind the main framebuffer texture
        mc.getFramebuffer().bindFramebufferTexture();

        // Draw fullscreen quad in screen space
        int width = mc.getMainWindow().getScaledWidth();
        int height = mc.getMainWindow().getScaledHeight();
        
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0, 1);
        GL11.glVertex2f(0, 0);
        GL11.glTexCoord2f(0, 0);
        GL11.glVertex2f(0, height);
        GL11.glTexCoord2f(1, 0);
        GL11.glVertex2f(width, height);
        GL11.glTexCoord2f(1, 1);
        GL11.glVertex2f(width, 0);
        GL11.glEnd();

        shader.unload();
        
        // Restore GL state
        RenderSystem.enableAlphaTest();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        
        // Restore matrices
        RenderSystem.matrixMode(GL11.GL_PROJECTION);
        RenderSystem.popMatrix();
        RenderSystem.matrixMode(GL11.GL_MODELVIEW);
        RenderSystem.popMatrix();
    }


    private static void setupUniforms(ShaderManager shader, float time, float intensity) {
        shader.setUniformf("u_texel_size",
                1.0F / (float) mc.getFramebuffer().framebufferWidth,
                1.0F / (float) mc.getFramebuffer().framebufferHeight);
        shader.setUniformf("u_time", time);
        shader.setUniformf("u_intensity", intensity);
    }
}
