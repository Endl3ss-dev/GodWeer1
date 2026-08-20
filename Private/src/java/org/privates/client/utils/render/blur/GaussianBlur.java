package org.privates.client.utils.render.blur;

import net.minecraft.client.Minecraft;
import net.minecraft.client.shader.Framebuffer;
import net.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.opengl.GL11;

public class GaussianBlur {
    private static final Minecraft mc = Minecraft.getInstance();
    private static Framebuffer framebuffer;
    private static Framebuffer blurBuffer1;
    private static Framebuffer blurBuffer2;
    private static final int blurProgram = -1;
    private static int currentIterations = 1;

    public static void startBlur() {
        int width = mc.getMainWindow().getScaledWidth();
        int height = mc.getMainWindow().getScaledHeight();

        if (framebuffer == null || framebuffer.framebufferWidth != width || framebuffer.framebufferHeight != height) {
            if (framebuffer != null) framebuffer.deleteFramebuffer();
            framebuffer = new Framebuffer(width, height, false, Minecraft.IS_RUNNING_ON_MAC);
        }

        framebuffer.bindFramebuffer(false);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }

    public static void endBlur(float radius, int iterations) {
        currentIterations = iterations;
        mc.getFramebuffer().bindFramebuffer(false);

        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

        // Simple blur implementation using multiple passes
        for (int i = 0; i < iterations; i++) {
            GL11.glPushMatrix();
            GL11.glTranslatef(0.5f * i, 0.5f * i, 0);

            RenderSystem.bindTexture(framebuffer.framebufferTexture);
            drawTexturedRect(0, 0, mc.getMainWindow().getScaledWidth(), mc.getMainWindow().getScaledHeight());

            GL11.glPopMatrix();
        }

        RenderSystem.disableBlend();
    }

    private static void drawTexturedRect(float x, float y, float width, float height) {
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0, 1);
        GL11.glVertex2f(x, y);
        GL11.glTexCoord2f(0, 0);
        GL11.glVertex2f(x, y + height);
        GL11.glTexCoord2f(1, 0);
        GL11.glVertex2f(x + width, y + height);
        GL11.glTexCoord2f(1, 1);
        GL11.glVertex2f(x + width, y);
        GL11.glEnd();
    }

    public static void cleanup() {
        if (framebuffer != null) {
            framebuffer.deleteFramebuffer();
            framebuffer = null;
        }
        if (blurBuffer1 != null) {
            blurBuffer1.deleteFramebuffer();
            blurBuffer1 = null;
        }
        if (blurBuffer2 != null) {
            blurBuffer2.deleteFramebuffer();
            blurBuffer2 = null;
        }
    }
}