package org.privates.client.utils.render.video;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.IResource;
import net.minecraft.util.Namespaced;
import net.minecraft.util.ResourceLocation;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.mojang.blaze3d.systems.RenderSystem;
import org.privates.client.api.interfaces.IMinecraft;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RectUtil;
import org.privates.client.utils.render.texture.TextureUtil;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Mp4Render implements IMinecraft {
    private final List<Integer> frames = new ArrayList<>();
    private final double frameRate;
    private long startTime;
    private boolean playing = false;
    private boolean loop = true;
    private int currentFrameIndex = 0;

    public Mp4Render(Namespaced mp4Namespaced, double frameRate) {
        this.frameRate = frameRate;
        loadFromMp4Resource(mp4Namespaced);
    }

    public Mp4Render(Namespaced mp4Namespaced) {
        this(mp4Namespaced, 30.0);
    }

    private void loadFromMp4Resource(Namespaced mp4Namespaced) {
        Minecraft mc = Minecraft.getInstance();
        String path = mp4Namespaced.toString().replace("texture/", "texture/");
        ResourceLocation location = new ResourceLocation("privates", path);
        try {
            IResource resource = mc.getResourceManager().getResource(location);
            if (resource == null) {
                throw new RuntimeException("Resource not found: " + location);
            }
            try (InputStream inputStream = resource.getInputStream()) {
                Mp4Decoder decoder = new Mp4Decoder();
                decoder.setFrameRate((int) frameRate);
                List<BufferedImage> videoFrames = decoder.decode(inputStream);

                for (BufferedImage frame : videoFrames) {
                    if (frame != null) {
                        frames.add(TextureUtil.loadTexture(frame));
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load MP4 from namespaced: " + mp4Namespaced, e);
        }

        if (frames.isEmpty()) {
            throw new RuntimeException("No frames decoded from MP4: " + mp4Namespaced);
        }
    }

    public void draw(MatrixStack stack, float x, float y, float width, float height, float alpha, boolean bloom) {
        if (frames.isEmpty()) return;

        if (!playing) {
            playing = true;
            startTime = System.currentTimeMillis();
        }

        bindTexture();

        RenderSystem.disableAlphaTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.shadeModel(7425);
        int color = ColorUtil.replAlpha(-1, (int) (alpha * 255));
        RectUtil.drawRect(stack, x, y, width, height, color, bloom, true);
        RenderSystem.shadeModel(7424);
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
    }

    public void draw(MatrixStack stack, float x, float y, float width, float height) {
        draw(stack, x, y, width, height, 1.0f, false);
    }

    private void bindTexture() {
        if (!playing || frames.isEmpty()) return;

        long elapsedTime = System.currentTimeMillis() - startTime;
        long frameDuration = (long) (1000.0 / frameRate);
        currentFrameIndex = (int) (elapsedTime / frameDuration);

        if (currentFrameIndex >= frames.size()) {
            if (loop) {
                currentFrameIndex = 0;
                startTime = System.currentTimeMillis();
            } else {
                currentFrameIndex = frames.size() - 1;
                playing = false;
            }
        }

        int textureID = frames.get(currentFrameIndex);
        net.mojang.blaze3d.platform.GlStateManager.bindTexture(textureID);
    }

    public void play() {
        playing = true;
        startTime = System.currentTimeMillis();
        currentFrameIndex = 0;
    }

    public void pause() {
        playing = false;
    }

    public void resume() {
        if (!playing) {
            playing = true;
            startTime = System.currentTimeMillis() - (currentFrameIndex * (long) (1000.0 / frameRate));
        }
    }

    public void stop() {
        playing = false;
        currentFrameIndex = 0;
        startTime = System.currentTimeMillis();
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public boolean isPlaying() {
        return playing;
    }

    public boolean isLoop() {
        return loop;
    }

    public int getCurrentFrame() {
        return currentFrameIndex;
    }

    public int getTotalFrames() {
        return frames.size();
    }

    public double getFrameRate() {
        return frameRate;
    }

    public void seekToFrame(int frameIndex) {
        if (frameIndex >= 0 && frameIndex < frames.size()) {
            currentFrameIndex = frameIndex;
            startTime = System.currentTimeMillis() - (long) (frameIndex * 1000.0 / frameRate);
        }
    }

    public void seekToPercent(double percent) {
        percent = Math.max(0.0, Math.min(1.0, percent));
        int targetFrame = (int) (percent * (frames.size() - 1));
        seekToFrame(targetFrame);
    }

    public double getProgress() {
        if (frames.isEmpty()) return 0.0;
        return (double) currentFrameIndex / (frames.size() - 1);
    }

    public void cleanup() {
        for (int textureId : frames) {
            net.mojang.blaze3d.platform.GlStateManager.deleteTexture(textureId);
        }
        frames.clear();
        playing = false;
    }
}