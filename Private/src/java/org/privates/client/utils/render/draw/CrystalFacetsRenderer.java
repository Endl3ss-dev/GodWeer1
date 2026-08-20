package org.privates.client.utils.render.draw;

import net.minecraft.client.Minecraft;
import net.minecraft.client.shader.Framebuffer;
import net.mojang.blaze3d.platform.GlStateManager;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.privates.client.utils.render.shader.ShaderManager;

import java.util.concurrent.ConcurrentLinkedQueue;

public class CrystalFacetsRenderer {
    private static final Minecraft mc = Minecraft.getInstance();
    private static final ShaderManager crystalFacets = ShaderManager.crystalFacets;
    private static final ConcurrentLinkedQueue<Runnable> renderQueue = new ConcurrentLinkedQueue<>();
    private static Framebuffer inputFramebuffer = new Framebuffer(mc.getMainWindow().getScaledWidth(), mc.getMainWindow().getScaledHeight(), false, Minecraft.IS_RUNNING_ON_MAC);

    public static void registerRenderCall(Runnable runnable) {
        renderQueue.add(runnable);
    }

    public static void draw(float facetSize) {
        if (renderQueue.isEmpty() || !mc.isGameFocused() || facetSize <= 0) return;

        inputFramebuffer = createFrameBuffer(inputFramebuffer);

        inputFramebuffer.framebufferClear(Minecraft.IS_RUNNING_ON_MAC);
        inputFramebuffer.bindFramebuffer(true);

        while (!renderQueue.isEmpty()) {
            renderQueue.poll().run();
        }

        inputFramebuffer.unbindFramebuffer();

        mc.getFramebuffer().bindFramebuffer(true);

        crystalFacets.load();
        setupUniforms(facetSize);

        crystalFacets.setUniformi("u_diffuse_sampler", 0);
        crystalFacets.setUniformi("u_other_sampler", 20);

        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

        GL13.glActiveTexture(GL13.GL_TEXTURE20);
        inputFramebuffer.bindFramebufferTexture();
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        mc.getFramebuffer().bindFramebufferTexture();

        ShaderManager.draw();

        GlStateManager.disableBlend();
        crystalFacets.unload();

        GL13.glActiveTexture(GL13.GL_TEXTURE20);
        GlStateManager.bindTexture(0);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GlStateManager.bindTexture(0);

        renderQueue.clear();
    }

    private static void setupUniforms(float facetSize) {
        crystalFacets.setUniformf("u_texel_size",
                1.0F / (float) mc.getFramebuffer().framebufferWidth,
                1.0F / (float) mc.getFramebuffer().framebufferHeight);
        crystalFacets.setUniformf("u_time", (System.currentTimeMillis() % 100000) / 1000.0F);
        crystalFacets.setUniformf("u_facet_size", facetSize);
    }

    private static Framebuffer createFrameBuffer(Framebuffer framebuffer) {
        int scaledWidth = mc.getMainWindow().getScaledWidth();
        int scaledHeight = mc.getMainWindow().getScaledHeight();

        if (framebuffer == null || framebuffer.framebufferWidth != scaledWidth || framebuffer.framebufferHeight != scaledHeight) {
            if (framebuffer != null) {
                framebuffer.deleteFramebuffer();
            }
            try {
                return new Framebuffer(scaledWidth, scaledHeight, false, Minecraft.IS_RUNNING_ON_MAC);
            } catch (Exception ex) {
                return null;
            }
        }
        return framebuffer;
    }
}