package org.privates.client.utils.render.draw;

import net.minecraft.client.Minecraft;
import net.minecraft.client.shader.Framebuffer;
import net.mojang.blaze3d.platform.GlStateManager;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.privates.client.utils.render.shader.ShaderManager;

import java.util.concurrent.ConcurrentLinkedQueue;

public class HandItemShaderRenderer {
    private static final Minecraft mc = Minecraft.getInstance();
    private static final ConcurrentLinkedQueue<Runnable> renderQueue = new ConcurrentLinkedQueue<>();
    private static Framebuffer inputFramebuffer = new Framebuffer(
            mc.getMainWindow().getScaledWidth(),
            mc.getMainWindow().getScaledHeight(),
            false, Minecraft.IS_RUNNING_ON_MAC);

    public static void registerRenderCall(Runnable runnable) {
        renderQueue.add(runnable);
    }

    public static void draw(ShaderManager shader, float intensity, float param1, float param2, boolean isCobweb) {
        if (renderQueue.isEmpty() || !mc.isGameFocused() || shader == null) return;

        inputFramebuffer = createFrameBuffer(inputFramebuffer);
        if (inputFramebuffer == null) return;

        inputFramebuffer.framebufferClear(Minecraft.IS_RUNNING_ON_MAC);
        inputFramebuffer.bindFramebuffer(true);
        while (!renderQueue.isEmpty()) renderQueue.poll().run();
        inputFramebuffer.unbindFramebuffer();

        mc.getFramebuffer().bindFramebuffer(true);

        shader.load();
        shader.setUniformf("u_texel_size",
                1.0F / mc.getFramebuffer().framebufferWidth,
                1.0F / mc.getFramebuffer().framebufferHeight);
        shader.setUniformf("u_time", (System.currentTimeMillis() % 1000000) / 1000.0F);

        if (isCobweb) {
            shader.setUniformf("u_speed", param1);
            shader.setUniformf("u_intensity", param2 * intensity);
        } else {
            shader.setUniformf("u_nebula_density", param1 * intensity);
        }

        shader.setUniformi("u_diffuse_sampler", 0);
        shader.setUniformi("u_other_sampler", 20);

        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

        GL13.glActiveTexture(GL13.GL_TEXTURE20);
        inputFramebuffer.bindFramebufferTexture();
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        mc.getFramebuffer().bindFramebufferTexture();

        ShaderManager.draw();

        GlStateManager.disableBlend();
        shader.unload();

        GL13.glActiveTexture(GL13.GL_TEXTURE20);
        GlStateManager.bindTexture(0);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GlStateManager.bindTexture(0);

        renderQueue.clear();
    }

    private static Framebuffer createFrameBuffer(Framebuffer fb) {
        int w = mc.getMainWindow().getScaledWidth();
        int h = mc.getMainWindow().getScaledHeight();
        if (fb == null || fb.framebufferWidth != w || fb.framebufferHeight != h) {
            if (fb != null) fb.deleteFramebuffer();
            try {
                return new Framebuffer(w, h, false, Minecraft.IS_RUNNING_ON_MAC);
            } catch (Exception e) {
                return null;
            }
        }
        return fb;
    }
}
