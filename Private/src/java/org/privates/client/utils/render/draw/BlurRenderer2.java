package org.privates.client.utils.render.draw;

import net.minecraft.client.Minecraft;
import net.minecraft.client.shader.Framebuffer;
import net.mojang.blaze3d.platform.GlStateManager;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL30;
import org.privates.client.utils.render.shader.ShaderManager;

import java.nio.FloatBuffer;
import java.util.HashMap;

public class BlurRenderer2 {
    private static final Minecraft mc = Minecraft.getInstance();
    private static final ShaderManager blur2 = ShaderManager.blur2;
    private static Runnable runnable;
    private static final HashMap<Integer, FloatBuffer> kernelCache = new HashMap<>();
    private static final Framebuffer inFrameBuffer = new Framebuffer(mc.getMainWindow().getScaledWidth(), mc.getMainWindow().getScaledHeight(), false, true);
    private static final Framebuffer outFrameBuffer = new Framebuffer(mc.getMainWindow().getScaledWidth(), mc.getMainWindow().getScaledHeight(), false, true);

    private static float blurColorR = 1f, blurColorG = 1f, blurColorB = 1f, blurColorA = 0.8f;

    public static void setBlurColor(int colorARGB) {
        blurColorA = ((colorARGB >> 24) & 0xFF) / 255.0f;
        blurColorR = ((colorARGB >> 16) & 0xFF) / 255.0f;
        blurColorG = ((colorARGB >> 8) & 0xFF) / 255.0f;
        blurColorB = (colorARGB & 0xFF) / 255.0f;
    }

    public static void registerRenderCall(Runnable runnabl) {
        runnable = runnabl;
    }

    public static void draw(int radius) {
        if (!mc.isGameFocused()) return;

        setupBuffer(inFrameBuffer);
        setupBuffer(outFrameBuffer);

        inFrameBuffer.framebufferClear(Minecraft.getInstance().isOnExecutionThread());
        inFrameBuffer.bindFramebuffer(true);

        runnable.run();

        outFrameBuffer.framebufferClear(Minecraft.getInstance().isOnExecutionThread());
        outFrameBuffer.bindFramebuffer(true);

        blur2.load();
        blur2.setUniformf("radius", radius);
        blur2.setUniformi("sampler1", 0);
        blur2.setUniformi("sampler2", 20);
        blur2.setUniformfb("kernel", getKernel(radius));
        blur2.setUniformf("texelSize", 1.0F / (float) mc.getMainWindow().getScaledWidth(),
                1.0F / (float) mc.getMainWindow().getScaledHeight());
        blur2.setUniformf("direction", 2.0F, 0.0F);
        blur2.setUniformf("blurColor", blurColorR, blurColorG, blurColorB, blurColorA);

        GlStateManager.disableBlend();
        GlStateManager.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

        mc.getFramebuffer().bindFramebufferTexture();
        applyTextureFilter();
        ShaderManager.draw();

        mc.getFramebuffer().bindFramebuffer(true);

        blur2.setUniformf("direction", 0.0F, 2.0F);

        outFrameBuffer.bindFramebufferTexture();
        applyTextureFilter();

        GL30.glActiveTexture(GL30.GL_TEXTURE20);
        inFrameBuffer.bindFramebufferTexture();
        applyTextureFilter();
        GL30.glActiveTexture(GL30.GL_TEXTURE0);

        ShaderManager.draw();

        blur2.unload();
        inFrameBuffer.unbindFramebufferTexture();
        GlStateManager.disableBlend();

        resetTextureFilter();
    }

    private static void applyTextureFilter() {
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
    }

    private static void resetTextureFilter() {
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
    }

    private static FloatBuffer getKernel(int radius) {
        FloatBuffer buffer = kernelCache.get(radius);
        if (buffer == null) {
            buffer = BufferUtils.createFloatBuffer(radius);
            float[] kernel = new float[radius];
            float sigma = radius / 2.5F;
            float twoSigmaSquare = 2.0F * sigma * sigma;
            float sigmaRoot = (float) Math.sqrt(twoSigmaSquare * Math.PI);
            float total = 0.0F;

            for (int i = 0; i < radius; i++) {
                float distance = i * i;
                kernel[i] = (float) Math.exp(-distance / twoSigmaSquare) / sigmaRoot;
                total += i > 0 ? kernel[i] * 2.0F : kernel[i];
            }

            for (int i = 0; i < radius; i++) {
                kernel[i] /= total;
            }

            buffer.put(kernel);
            buffer.flip();
            kernelCache.put(radius, buffer);
        }
        return buffer;
    }

    private static Framebuffer setupBuffer(Framebuffer frameBuffer) {
        int screenWidth = mc.getMainWindow().getWidth();
        int screenHeight = mc.getMainWindow().getHeight();

        if (frameBuffer.framebufferWidth != screenWidth / 2 || frameBuffer.framebufferHeight != screenHeight / 2) {
            frameBuffer.resize(screenWidth / 2, screenHeight / 2, false);
        } else {
            frameBuffer.framebufferClear(false);
        }

        return frameBuffer;
    }
}