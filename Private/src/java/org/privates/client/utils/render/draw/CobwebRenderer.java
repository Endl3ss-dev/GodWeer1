package org.privates.client.utils.render.draw;

import net.minecraft.client.Minecraft;
import net.minecraft.client.shader.Framebuffer;
import net.mojang.blaze3d.platform.GlStateManager;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.privates.client.utils.render.shader.ShaderManager;

import java.util.concurrent.ConcurrentLinkedQueue;

public class CobwebRenderer {
    private static final Minecraft mc = Minecraft.getInstance();
    private static final ShaderManager cobweb = ShaderManager.cobweb;
    private static final ConcurrentLinkedQueue<Runnable> renderQueue = new ConcurrentLinkedQueue<>();
    private static Framebuffer inputFramebuffer = new Framebuffer(
            mc.getMainWindow().getScaledWidth(),
            mc.getMainWindow().getScaledHeight(),
            false, Minecraft.IS_RUNNING_ON_MAC);

    public static void registerRenderCall(Runnable runnable) {
        renderQueue.add(runnable);
    }

    public static void draw(float speed, float intensity) {
        if (renderQueue.isEmpty() || !mc.isGameFocused()) return;

        inputFramebuffer = createFrameBuffer(inputFramebuffer);
        if (inputFramebuffer == null) return;

        inputFramebuffer.framebufferClear(Minecraft.IS_RUNNING_ON_MAC);
        inputFramebuffer.bindFramebuffer(true);
        while (!renderQueue.isEmpty()) renderQueue.poll().run();
        inputFramebuffer.unbindFramebuffer();

        mc.getFramebuffer().bindFramebuffer(true);

        cobweb.load();
        cobweb.setUniformf("u_texel_size",
                1.0F / mc.getFramebuffer().framebufferWidth,
                1.0F / mc.getFramebuffer().framebufferHeight);
        cobweb.setUniformf("u_time", (System.currentTimeMillis() % 1000000) / 1000.0F);
        cobweb.setUniformf("u_speed", speed);
        cobweb.setUniformf("u_intensity", intensity);
        cobweb.setUniformi("u_diffuse_sampler", 0);
        cobweb.setUniformi("u_other_sampler", 20);

        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

        GL13.glActiveTexture(GL13.GL_TEXTURE20);
        inputFramebuffer.bindFramebufferTexture();
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        mc.getFramebuffer().bindFramebufferTexture();

        ShaderManager.draw();

        GlStateManager.disableBlend();
        cobweb.unload();

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