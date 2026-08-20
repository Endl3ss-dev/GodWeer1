package fun.volon.client.modules.impl.render.base.implement;

import dev.redstones.mediaplayerinfo.IMediaSession;
import dev.redstones.mediaplayerinfo.MediaInfo;
import dev.redstones.mediaplayerinfo.MediaPlayerInfo;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import fun.volon.Volon;
import fun.volon.api.events.implement.EventRender;
import fun.volon.api.utils.color.ColorUtils;
import fun.volon.api.utils.draggable.Draggable;
import fun.volon.api.utils.render.RenderUtils;
import fun.volon.api.utils.render.fonts.msdf.Font;
import fun.volon.api.utils.render.fonts.msdf.Fonts;
import fun.volon.client.modules.impl.render.base.InterfaceProcessing;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * NowPlaying HUD — показывает текущую музыку с компьютера через Windows media sessions.
 * Дизайн: [обложка] [Исполнитель] [Название] | [время]
 */
public class NowPlaying extends InterfaceProcessing {

    private static final long POLL_INTERVAL_MS = 500L;
    private static final float HUD_HEIGHT = 16f;
    private static final float ICON_PLATE_W = 16f;
    private static final float ICON_PLATE_GAP = 2.5f;
    private static final float DIVIDER_W = 0.8f;
    private static final float PAD = 4f;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "NowPlaying-Poller");
        t.setDaemon(true);
        return t;
    });

    private final AtomicReference<String> artist = new AtomicReference<>("No media");
    private final AtomicReference<String> title  = new AtomicReference<>("Nothing is playing");
    private final AtomicReference<Long> positionSec = new AtomicReference<>(0L);
    private final AtomicReference<Long> durationSec = new AtomicReference<>(0L);
    private final AtomicReference<byte[]> pendingArtwork = new AtomicReference<>(null);
    private final AtomicBoolean artworkDirty = new AtomicBoolean(false);

    // Управляется только из render-потока
    private Identifier artworkTexture = null;
    private byte[] lastArtworkPng = null;

    private float animatedProgress = 0f;

    public NowPlaying(Draggable draggable) {
        super(draggable);
        scheduler.scheduleAtFixedRate(this::pollMedia, 0, POLL_INTERVAL_MS, TimeUnit.MILLISECONDS);
    }

    // ── Polling (background thread) ──────────────────────────────────────────

    private void pollMedia() {
        try {
            List<IMediaSession> sessions = MediaPlayerInfo.INSTANCE.getMediaSessions();
            IMediaSession session = sessions.stream()
                    .filter(this::hasMedia)
                    .filter(s -> s.getMedia().isPlaying())
                    .findFirst()
                    .orElseGet(() -> sessions.stream().filter(this::hasMedia).findFirst().orElse(null));

            if (session == null) { clearMedia(); return; }

            MediaInfo media = session.getMedia();
            artist.set(truncate(media.getArtist(), 24));
            title.set(truncate(media.getTitle(), 32));
            positionSec.set(Math.max(0L, media.getPosition()));
            durationSec.set(Math.max(0L, media.getDuration()));

            byte[] art = media.getArtworkPng();
            if (!Arrays.equals(art, lastArtworkPng)) {
                pendingArtwork.set(art);
                artworkDirty.set(true);
            }
        } catch (Throwable ignored) {
            clearMedia();
        }
    }

    private boolean hasMedia(IMediaSession s) {
        if (s == null || s.getMedia() == null) return false;
        MediaInfo m = s.getMedia();
        return m.getArtist() != null && !m.getArtist().isBlank()
            && m.getTitle()  != null && !m.getTitle().isBlank();
    }

    private void clearMedia() {
        artist.set("No media");
        title.set("Nothing is playing");
        positionSec.set(0L);
        durationSec.set(0L);
        pendingArtwork.set(null);
        artworkDirty.set(true);
    }

    // ── Artwork texture update (render thread) ────────────────────────────────

    private void updateArtworkTexture() {
        if (!artworkDirty.compareAndSet(true, false)) return;
        byte[] png = pendingArtwork.get();
        lastArtworkPng = png;

        if (artworkTexture != null && mc != null) {
            mc.getTextureManager().destroyTexture(artworkTexture);
            artworkTexture = null;
        }

        if (png == null || png.length < 8) return;

        try {
            NativeImage img = NativeImage.read(new ByteArrayInputStream(png));
            NativeImageBackedTexture tex = new NativeImageBackedTexture(img);
            artworkTexture = Identifier.of("volon", "nowplaying_art_" + System.currentTimeMillis());
            if (mc != null) mc.getTextureManager().registerTexture(artworkTexture, tex);
        } catch (Throwable ignored) {
            artworkTexture = null;
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private String truncate(String s, int max) {
        if (s == null || s.isBlank()) return "";
        String t = s.trim();
        return t.length() > max ? t.substring(0, max - 1) + "…" : t;
    }

    private String formatTime(long seconds) {
        long m = seconds / 60, s = seconds % 60;
        return m + ":" + String.format("%02d", s);
    }

    private int getThemeColor() {
        if (Volon.INSTANCE == null || Volon.INSTANCE.themeStorage == null) return ColorUtils.getThemeColor();
        var theme = Volon.INSTANCE.themeStorage.getThemes().getTheme();
        if (theme == null) return ColorUtils.getThemeColor();
        if ("Rainbow".equals(theme.getName())) return ColorUtils.getThemeColor();
        return theme.color != null && theme.color.length > 0 ? theme.color[0] : ColorUtils.getThemeColor();
    }

    // ── Render ────────────────────────────────────────────────────────────────

    @Override
    public void onRender(EventRender.Default eventRender) {
        updateArtworkTexture();

        MatrixStack matrices = eventRender.getContext().getMatrices();
        float x = draggable.getX(), y = draggable.getY();

        Font font13 = Fonts.getFont("suisse", 13);
        Font font14 = Fonts.getFont("suisse", 14);
        Font iconFont = Fonts.getFont("iconnew", 14);
        if (font13 == null || font14 == null) return;

        int themeColor = getThemeColor();
        int white = 0xFFFFFFFF;
        int gray  = ColorUtils.rgba(180, 180, 180, 255);

        String artistStr = artist.get();
        String titleStr  = title.get();
        long pos = positionSec.get();
        long dur = durationSec.get();
        String timeStr = formatTime(pos);

        float targetProg = dur > 0 ? (float) pos / dur : 0f;
        animatedProgress += (targetProg - animatedProgress) * 0.08f;

        float artistW = font14.getStringWidth(artistStr);
        float titleW  = font13.getStringWidth(" " + titleStr);
        float timeW   = font13.getStringWidth(timeStr);
        float mainW   = PAD + artistW + titleW + PAD + DIVIDER_W + PAD + timeW + PAD;

        float iconPlateX = x;
        float mainX = x + ICON_PLATE_W + ICON_PLATE_GAP;

        // ── Иконка-плашка (обложка или нота) ─────────────────────────────────
        drawHudBackground(matrices, iconPlateX, y, ICON_PLATE_W, HUD_HEIGHT, themeColor);
        if (artworkTexture != null) {
            float pad = 1.5f;
            float sz  = ICON_PLATE_W - pad * 2;
            RenderUtils.drawImage(matrices, artworkTexture, iconPlateX + pad, y + pad, sz, sz, 0xFFFFFFFF);
        } else if (iconFont != null) {
            String glyph = "m";
            float gw = iconFont.getStringWidth(glyph);
            iconFont.drawGradientStringHorizontal(matrices, glyph,
                    iconPlateX + (ICON_PLATE_W - gw) * 0.5f, y + 6.4f,
                    ColorUtils.getThemeColor(0), ColorUtils.getThemeColor(120));
        }

        // ── Основной блок ─────────────────────────────────────────────────────
        drawHudBackground(matrices, mainX, y, mainW, HUD_HEIGHT, themeColor);

        // Прогресс-бар
        if (dur > 0) {
            float barH = 1.5f, barY = y + HUD_HEIGHT - barH;
            float barW = mainW * animatedProgress;
            RenderUtils.drawRoundedRect(matrices, mainX, barY, mainW, barH, 0.75f,
                    ColorUtils.rgba(40, 40, 40, 120));
            if (barW > 1f)
                RenderUtils.drawGradientRect(matrices, mainX, barY, barW, barH, 0.75f,
                        themeColor, ColorUtils.getThemeColor(120), true);
        }

        // Разделитель
        float divX = mainX + PAD + artistW + titleW + PAD;
        RenderUtils.drawRoundedRect(matrices, divX, y + 3f, DIVIDER_W, HUD_HEIGHT - 6f, 0.4f,
                ColorUtils.rgba(80, 80, 80, 180));

        // Текст
        float textY = y + 6.5f, drawX = mainX + PAD;
        font14.drawString(matrices, artistStr, drawX, textY, white);
        drawX += artistW;
        font13.drawString(matrices, " " + titleStr, drawX, textY + 0.5f, gray);
        font13.drawString(matrices, timeStr, divX + DIVIDER_W + PAD, textY + 0.5f, themeColor);

        draggable.setWidth(ICON_PLATE_W + ICON_PLATE_GAP + mainW);
        draggable.setHeight(HUD_HEIGHT);

        super.onRender(eventRender);
    }

    public void shutdown() {
        scheduler.shutdownNow();
        if (artworkTexture != null && mc != null) {
            mc.getTextureManager().destroyTexture(artworkTexture);
            artworkTexture = null;
        }
    }
}
