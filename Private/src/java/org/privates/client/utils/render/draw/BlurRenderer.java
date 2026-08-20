package org.privates.client.utils.render.draw;

import net.minecraft.client.Minecraft;
import net.minecraft.client.shader.Framebuffer;
import net.mojang.blaze3d.platform.GlStateManager;
import org.privates.client.utils.render.shader.ShaderManager;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import java.nio.FloatBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;

public class BlurRenderer {
    private static final Minecraft mc = Minecraft.getInstance();
    private static final ShaderManager blur = ShaderManager.blur;
    private static final ConcurrentLinkedQueue<Runnable> renderQueue = new ConcurrentLinkedQueue<>();
    private static Framebuffer inputFramebuffer = new Framebuffer(mc.getMainWindow().getScaledWidth(), mc.getMainWindow().getScaledHeight(), false, Minecraft.IS_RUNNING_ON_MAC);
    private static Framebuffer outputFramebuffer = new Framebuffer(mc.getMainWindow().getScaledWidth(), mc.getMainWindow().getScaledHeight(), false, Minecraft.IS_RUNNING_ON_MAC);
    private static GaussianKernel gaussianKernel = new GaussianKernel(0);

    public static void registerRenderCall(Runnable runnable) {
        renderQueue.add(runnable);
    }

    public static void draw(int radius) {
        if (renderQueue.isEmpty() || !mc.isGameFocused() || radius <= 0 || radius > 127) return;

        inputFramebuffer = createFrameBuffer(inputFramebuffer);
        outputFramebuffer = createFrameBuffer(outputFramebuffer);

        inputFramebuffer.framebufferClear(Minecraft.IS_RUNNING_ON_MAC);
        inputFramebuffer.bindFramebuffer(true);

        while (!renderQueue.isEmpty()) {
            renderQueue.poll().run();
        }

        inputFramebuffer.unbindFramebuffer();

        outputFramebuffer.framebufferClear(Minecraft.IS_RUNNING_ON_MAC);
        outputFramebuffer.bindFramebuffer(true);

        blur.load();
        setupUniforms(radius, 1.0f, 0.0f);

        blur.setUniformi("u_diffuse_sampler", 0);
        blur.setUniformi("u_other_sampler", 20);

        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

        GL13.glActiveTexture(GL13.GL_TEXTURE20);
        inputFramebuffer.bindFramebufferTexture();
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        mc.getFramebuffer().bindFramebufferTexture();

        ShaderManager.draw();

        outputFramebuffer.unbindFramebuffer();
        blur.unload();

        mc.getFramebuffer().bindFramebuffer(true);

        blur.load();
        setupUniforms(radius, 0.0f, 1.0f);

        GL13.glActiveTexture(GL13.GL_TEXTURE20);
        inputFramebuffer.bindFramebufferTexture();
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        outputFramebuffer.bindFramebufferTexture();

        ShaderManager.draw();

        GlStateManager.disableBlend();
        blur.unload();

        GL13.glActiveTexture(GL13.GL_TEXTURE20);
        GlStateManager.bindTexture(0);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GlStateManager.bindTexture(0);

        renderQueue.clear();
    }

    private static void setupUniforms(int radius, float dirX, float dirY) {
        blur.setUniformf("u_texel_size", 1.0F / (float) mc.getFramebuffer().framebufferWidth,
                1.0F / (float) mc.getFramebuffer().framebufferHeight);
        blur.setUniformf("u_direction", dirX, dirY);
        blur.setUniformf("u_radius", radius);

        if (gaussianKernel.getSize() != radius) {
            gaussianKernel = new GaussianKernel(radius);
            gaussianKernel.compute();

            final FloatBuffer buffer = BufferUtils.createFloatBuffer(radius);
            buffer.put(gaussianKernel.getKernel());
            buffer.flip();

            blur.setUniformfb("u_kernel", buffer);
        }
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