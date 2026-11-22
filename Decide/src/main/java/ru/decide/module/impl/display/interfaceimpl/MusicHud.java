/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.redstones.mediaplayerinfo.IMediaSession
 *  dev.redstones.mediaplayerinfo.MediaInfo
 *  dev.redstones.mediaplayerinfo.MediaPlayerInfo
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_3532
 */
package ru.decide.module.impl.display.interfaceimpl;

import dev.redstones.mediaplayerinfo.IMediaSession;
import dev.redstones.mediaplayerinfo.MediaInfo;
import dev.redstones.mediaplayerinfo.MediaPlayerInfo;
import java.awt.Color;
import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_3532;
import ru.decide.manager.event_impl.MousePressEvent;
import ru.decide.module.api.settings.impl.DragSetting;
import ru.decide.module.impl.display.InterFace;
import ru.decide.module.impl.display.interfaceimpl.element;
import ru.decide.theme.ThemeColor;
import ru.decide.utils.animation.Animation;
import ru.decide.utils.animation.Easings;
import ru.decide.utils.colors.ColorUtil;
import ru.decide.utils.render.MediaTextureUtil;
import ru.decide.utils.render.RenderUtil;
import ru.decide.utils.render.Scissor;
import ru.decide.utils.render.font.Font;
import ru.decide.utils.render.font.Fonts;
import ru.decide.utils.taskript.StopWatch;

@Environment(value=EnvType.CLIENT)
public class MusicHud
implements element {
    private static final MediaInfo EMPTY = new MediaInfo("\u041d\u0435\u0442 \u0442\u0440\u0435\u043a\u0430", "\u2014", new byte[0], 0L, 0L, false);
    private static float S = 1.0f;
    private ExecutorService executor;
    private final StopWatch lastMedia = new StopWatch();
    private final Animation openAnimation = new Animation();
    private volatile MediaInfo mediaInfo = EMPTY;
    private volatile IMediaSession session;
    private float progressWidth;
    private float playX;
    private float playY;
    private float playW;
    private float playH;
    private float prevX;
    private float prevY;
    private float prevW;
    private float prevH;
    private float nextX;
    private float nextY;
    private float nextW;
    private float nextH;

    private synchronized ExecutorService executor() {
        if (this.executor == null || this.executor.isShutdown()) {
            this.executor = Executors.newSingleThreadExecutor(r -> {
                Thread t = new Thread(r, "white-media-hud");
                t.setDaemon(true);
                return t;
            });
        }
        return this.executor;
    }

    public void onTick() {
        if (MusicHud.mc.field_1724 == null || MusicHud.mc.field_1687 == null) {
            return;
        }
        if (MusicHud.mc.field_1724.field_6012 % 5 != 0) {
            return;
        }
        this.executor().submit(() -> {
            try {
                IMediaSession current;
                this.session = current = (IMediaSession)MediaPlayerInfo.Instance.getMediaSessions().stream().max(Comparator.comparingInt(s -> s.getMedia().getPlaying() ? 1 : 0)).orElse(null);
                if (current == null) {
                    return;
                }
                MediaInfo info = current.getMedia();
                if (info.getTitle().isEmpty() && info.getArtist().isEmpty()) {
                    return;
                }
                this.mediaInfo = info;
                this.lastMedia.reset();
                MediaTextureUtil.updateArtwork(info.getArtworkPng());
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        });
    }

    public boolean onMouseClick(MousePressEvent event) {
        if (event.getAction() != 1 || event.getButton() != 0) {
            return false;
        }
        if (this.openAnimation.get() <= 0.01f) {
            return false;
        }
        double mx = event.getMouseX();
        double my = event.getMouseY();
        IMediaSession active = this.session;
        if (active == null) {
            return false;
        }
        if (MusicHud.inRect(mx, my, this.prevX, this.prevY, this.prevW, this.prevH)) {
            active.previous();
            return true;
        }
        if (MusicHud.inRect(mx, my, this.playX, this.playY, this.playW, this.playH)) {
            active.playPause();
            return true;
        }
        if (MusicHud.inRect(mx, my, this.nextX, this.nextY, this.nextW, this.nextH)) {
            active.next();
            return true;
        }
        return false;
    }

    @Override
    public void onRender(DragSetting dragSetting, InterFace interFace) {
        boolean hasMedia = !this.lastMedia.finished(2000.0);
        this.openAnimation.update();
        this.openAnimation.run(1.0, 0.12f, Easings.SINE_OUT, true);
        float alpha = this.openAnimation.get();
        if (alpha <= 0.01f) {
            dragSetting.active = false;
            return;
        }
        dragSetting.active = true;
        S = InterFace.getInstance().sizeHud.getValue().floatValue();
        float WIDTH = 79.0f * S;
        float HEIGHT = 41.0f * S;
        float PAD = 4.0f * S;
        float RADIUS = 5.0f * S;
        float ART_SIZE = 19.0f * S;
        float ART_RADIUS = 4.0f * S;
        MediaInfo info = this.mediaInfo;
        float x = dragSetting.position.x;
        float y = dragSetting.position.y;
        RenderUtil.Render2D.glow(x, y, WIDTH, HEIGHT, ColorUtil.replAlpha(ColorUtil.getColor(0), 0.1f * alpha), RADIUS, 12.0f, 1.0f);
        RenderUtil.Blur.blur(x, y, WIDTH, HEIGHT, alpha, RADIUS, ColorUtil.replAlpha(ColorUtil.background(), Math.min(InterFace.getInstance().alphaHUD.getValue().floatValue() * alpha + 0.35f, 1.0f)));
        float artX = x + PAD;
        float artY = y + PAD;
        if (MediaTextureUtil.hasArtwork()) {
            RenderUtil.Images.texture(MediaTextureUtil.ARTWORK_ID, artX, artY, ART_SIZE, ART_SIZE, 1.0f, ART_RADIUS, ColorUtil.getColor(255, alpha));
        } else {
            RenderUtil.Render2D.rect(artX, artY, ART_SIZE, ART_SIZE, ColorUtil.multAlpha(new Color(30, 30, 36).getRGB(), alpha), ART_RADIUS);
            Fonts.icon.drawCentered("M", artX + ART_SIZE / 2.0f, artY + ART_SIZE / 2.0f - 1.75f * S, 3.75f * S, ThemeColor.getHudColor(alpha));
        }
        float textX = artX + ART_SIZE + 5.0f * S;
        float textW = x + WIDTH - PAD - textX;
        Font titleFont = Fonts.sf_regular;
        Font metaFont = Fonts.sf_regular;
        String title = info.getTitle().isEmpty() ? "\u041d\u0435\u0442 \u0442\u0440\u0435\u043a\u0430" : info.getTitle();
        String artist = info.getArtist().isEmpty() ? "\u2014" : info.getArtist();
        Scissor.enable(textX, artY, textW, ART_SIZE);
        titleFont.drawFadingText(title, textX, artY + 2.0f * S, textW, ColorUtil.getColor(255, alpha), 7.0f * S);
        metaFont.drawFadingText(artist, textX, artY + 10.0f * S, textW, ColorUtil.getColor(180, alpha), 6.0f * S);
        Scissor.disable();
        long duration = Math.max(1L, info.getDuration());
        long position = class_3532.method_53062((long)info.getPosition(), (long)0L, (long)duration);
        String posText = MusicHud.formatDuration(position);
        String durText = MusicHud.formatDuration(duration);
        float timeSize = 6.0f * S;
        float progressRowY = artY + ART_SIZE + 2.0f * S;
        float posW = metaFont.getWidth(posText, timeSize);
        float durW = metaFont.getWidth(durText, timeSize);
        int timeColor = ColorUtil.getColor(150, alpha);
        metaFont.draw(posText, x + PAD, progressRowY + 0.5f * S, timeSize, timeColor);
        metaFont.draw(durText, x + WIDTH - PAD - durW, progressRowY + 0.5f * S, timeSize, timeColor);
        float barX = x + PAD + posW + 2.5f * S;
        float barW = x + WIDTH - PAD - durW - 2.5f * S - barX;
        float barY = progressRowY + 3.25f * S;
        float barH = 1.25f * S;
        float targetProgress = barW * ((float)position / (float)duration);
        this.progressWidth = class_3532.method_16439((float)0.18f, (float)this.progressWidth, (float)class_3532.method_15363((float)targetProgress, (float)0.0f, (float)barW));
        int trackColor = ColorUtil.multAlpha(new Color(0).getRGB(), alpha * 0.1f);
        RenderUtil.Render2D.rect(barX, barY, barW, barH, trackColor, barH / 2.0f);
        if (this.progressWidth > 0.5f) {
            int[] progressColors = new int[]{ColorUtil.multAlpha(ColorUtil.getClientColor1(1), alpha), ColorUtil.multAlpha(ColorUtil.getClientColor1(1), alpha), ColorUtil.multAlpha(ColorUtil.getClientColor1(1), alpha), ColorUtil.multAlpha(ColorUtil.getClientColor1(1), alpha)};
            RenderUtil.Render2D.gradientRect(barX, barY, this.progressWidth, barH, progressColors, barH / 2.0f);
        }
        float controlsY = y + HEIGHT - PAD - 5.0f * S;
        float centerX = x + WIDTH / 2.0f;
        float controlSpacing = 10.0f * S;
        int controlColor = ThemeColor.getHudColor(alpha);
        int controlColor2 = ThemeColor.getHudColor(alpha * 0.6f);
        this.prevW = 6.0f * S;
        this.prevH = 5.0f * S;
        this.prevX = centerX - controlSpacing - this.prevW / 2.0f;
        this.prevY = controlsY;
        this.playW = 6.0f * S;
        this.playH = 5.0f * S;
        this.playX = centerX - this.playW / 2.0f;
        this.playY = controlsY;
        this.nextW = 6.0f * S;
        this.nextH = 5.0f * S;
        this.nextX = centerX + controlSpacing - this.nextW / 2.0f;
        this.nextY = controlsY;
        Font icon = Fonts.icon;
        float iconSize = 4.5f * S;
        icon.drawCentered("O", this.prevX + this.prevW / 2.0f, controlsY + 0.75f * S, iconSize, controlColor2);
        icon.drawCentered(info.getPlaying() ? "V" : "M", this.playX + this.playW / 2.0f, controlsY + 0.75f * S, iconSize, controlColor);
        icon.drawCentered("N", this.nextX + this.nextW / 2.0f, controlsY + 0.75f * S, iconSize, controlColor2);
        dragSetting.size.set(WIDTH, HEIGHT);
    }

    public synchronized void shutdown() {
        if (this.executor != null) {
            this.executor.shutdownNow();
        }
        MediaTextureUtil.reset();
    }

    private static boolean inRect(double mx, double my, float x, float y, float w, float h) {
        return mx >= (double)x && mx <= (double)(x + w) && my >= (double)y && my <= (double)(y + h);
    }

    private static String formatDuration(long seconds) {
        long mins = seconds / 60L;
        long secs = seconds % 60L;
        return String.format("%d:%02d", mins, secs);
    }
}

