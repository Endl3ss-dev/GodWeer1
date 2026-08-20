package org.privates.client.managers.module.impl.player;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Namespaced;
import net.minecraft.util.text.StringTextComponent;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.utils.animation.Animation;
import org.privates.client.utils.animation.util.Easings;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.BlurRenderer2;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.font.Fonts;
import org.privates.client.utils.render.video.Mp4Render;

import java.awt.*;

import static org.privates.client.api.interfaces.IMinecraft.mc;

public class VideoGui extends Screen {

    private Mp4Render videoRender;
    private final Animation openAnimation = new Animation();
    private final Animation closeAnimation = new Animation();
    private final Animation fadeAnimation = new Animation();
    private boolean closing = false;
    private boolean videoLoaded = false;

    public VideoGui() {
        super(new StringTextComponent("VIDEO GUI"));
    }

    @Override
    protected void init() {
        super.init();
        this.openAnimation.run(1.0, 0.4, Easings.EXPO_OUT);
        this.closeAnimation.set(0.0);
        this.fadeAnimation.set(1.0);
        this.closing = false;

        loadVideo();
    }

    private void loadVideo() {
        try {
            Namespaced mp4Namespaced = new Namespaced("texture/privatess.mp4");
            videoRender = new Mp4Render(mp4Namespaced, 30.0);
            videoRender.setLoop(true);
            videoRender.play();
            videoLoaded = true;
        } catch (Exception e) {
            System.err.println("Failed to load MP4: " + e.getMessage());
            videoLoaded = false;
        }
    }

    @Override
    public void onClose() {
        if (this.closing) return;
        this.closing = true;
        this.closeAnimation.run(1.0, 0.3, Easings.EXPO_IN);
        this.openAnimation.run(0.0, 0.3, Easings.EXPO_IN);
        this.fadeAnimation.run(0.0, 0.3, Easings.EXPO_IN);

        if (videoRender != null) {
            videoRender.cleanup();
            videoRender = null;
        }
    }

    private void updateAnimations() {
        openAnimation.update();
        closeAnimation.update();
        fadeAnimation.update();

        if (closing && closeAnimation.isFinished() && openAnimation.isFinished() && fadeAnimation.isFinished()) {
            mc.displayScreen(null);
        }
    }

    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        RenderUtil.drawBackground(matrix);

        updateAnimations();

        Theme theme = Theme.getInstance();
        int clientColor = theme.clientColor();

        float easedAnim = openAnimation.get() * (1.0f - closeAnimation.get());
        float fadeAlpha = fadeAnimation.get() * easedAnim;
        if (easedAnim <= 0.01f) return;

        float videoWidth = 800;
        float videoHeight = 450;
        float centerX = this.width / 2.0f;
        float centerY = this.height / 2.0f;
        float videoX = centerX - videoWidth / 2;
        float videoY = centerY - videoHeight / 2;

        matrix.push();
        matrix.translate(centerX, centerY, 0);
        matrix.scale(easedAnim, easedAnim, 1);
        matrix.translate(-centerX, -centerY, 0);

        BlurRenderer2.registerRenderCall(() -> RenderUtil.Rounded.roundedRect(matrix, videoX, videoY, videoWidth, videoHeight, ColorUtil.replAlpha(-1, (int) (150 * fadeAlpha)), Round.of(12)));
        BlurRenderer2.draw(6);

        if (videoLoaded && videoRender != null) {
            videoRender.draw(matrix, videoX, videoY, videoWidth, videoHeight, fadeAlpha, false);
        } else {
            Fonts.SF_BOLD.draw(matrix, "MP4 failed to load", videoX + videoWidth / 2f - Fonts.SF_BOLD.getWidth("MP4 failed to load", 16f) / 2f, videoY + videoHeight / 2f, ColorUtil.replAlpha(clientColor, (int) (255 * fadeAlpha)), 16f);
            Fonts.SF_BOLD.draw(matrix, "Put privatess.mp4 in assets/privates/textures/", videoX + videoWidth / 2f - Fonts.SF_BOLD.getWidth("Put privatess.mp4 in assets/privates/textures/", 12f) / 2f, videoY + videoHeight / 2f + 25, ColorUtil.replAlpha(Color.GRAY.getRGB(), (int) (255 * fadeAlpha)), 12f);
        }

        if (videoLoaded && videoRender != null) {
            String status = videoRender.isPlaying() ? "Playing" : "Paused";
            String info = String.format("Progress: %.1f%% - %s", videoRender.getProgress() * 100, status);
            Fonts.SF_BOLD.draw(matrix, info, videoX, videoY + videoHeight + 10, ColorUtil.replAlpha(clientColor, (int) (255 * fadeAlpha)), 12f);
        }

        matrix.pop();
        super.render(matrix, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (videoLoaded && videoRender != null) {
            float videoWidth = 800;
            float videoHeight = 450;
            float centerX = this.width / 2.0f;
            float centerY = this.height / 2.0f;
            float videoX = centerX - videoWidth / 2;
            float videoY = centerY - videoHeight / 2;

            if (mouseX >= videoX && mouseX <= videoX + videoWidth && mouseY >= videoY && mouseY <= videoY + videoHeight) {
                if (videoRender.isPlaying()) {
                    videoRender.pause();
                } else {
                    videoRender.resume();
                }
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}