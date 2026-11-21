package tech.lightclient.ui.hud;

import tech.lightclient.client.ClientColors;
import tech.lightclient.client.events.impl.client.UpdateEvent;
import tech.lightclient.client.events.impl.render.EventRender;
import tech.lightclient.client.utils.eventbus.EventHandler;
import tech.lightclient.client.utils.math.MathUtil;
import tech.lightclient.client.utils.render.animations.Easings;
import tech.lightclient.client.utils.render.drag.DraggingManager;
import tech.lightclient.client.utils.render.drag.api.Dragging;
import tech.lightclient.client.utils.render.gif.GifTexture;
import tech.lightclient.client.utils.render.animations.impl.SimpleLinearAnimation;
import tech.lightclient.client.utils.render.main.core.Renderer2D;
import tech.lightclient.client.utils.render.main.text.FontRegistry;
import tech.lightclient.modules.impl.render.Interface;
import tech.lightclient.ui.hud.api.HudElement;
import kz.regullar.optmedia.MediaInfo;
import kz.regullar.optmedia.NativeLoader;   
import kz.regullar.optmedia.PositionInfo;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import org.json.JSONArray;
import org.json.JSONObject;
import tech.lightclient.client.utils.render.main.text.FontObject;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public class MediaPlayer implements HudElement {
    public static final String SETTINGS_SCOPE = "MediaPlayer";
    public static final String SETTING_VARIANT = "variant";
    public static final String SETTING_SHOW_TEXT = "show_text";
    public static final String VARIANT_DEFAULT = "Дефолт";
    public static final String VARIANT_NEW = "Новый";
    public static float width = 220f;
    public static float height = 80f;

    final Dragging dragging;
    private final SimpleLinearAnimation animation = new SimpleLinearAnimation();

    private static volatile String trackName = null;
    private static volatile String artistsText = null;
    private static volatile String previousTrackName = null;
    private static volatile String previousArtistsText = null;
    private static volatile long totalTime = 1;
    private static volatile String lyricsKey = "";
    private static volatile String[] lyricsLines = new String[0];
    private static volatile LyricLine[] syncedLyricsLines = new LyricLine[0];
    private static int displayedLyricIndex = -1;
    private static int previousLyricIndex = -1;
    private static long lyricSwitchMs = 0L;
    private static final long LYRIC_TRANSITION_MS = 720L;

    private static volatile float progress = 0.0f;
    private static volatile long currentTime = 0;
    private static volatile long allTime = 0;
    private static volatile boolean isPlaying = false;
    private static volatile float currentNewRectWidth = 220f;
    private static float playPauseIconProgress = 0f;

    private static final float DEF_BTN_SIZE = 22f;
    private static final float DEF_BTN_ICON_SIZE = 14f;
    private static final float DEF_BTN_ICON_NUDGE = 11f;
    private static volatile float defPrevBtnX = 0f;
    private static volatile float defPrevBtnY = 0f;
    private static volatile float defNextBtnX = 0f;
    private static volatile float defNextBtnY = 0f;
    private static volatile float defScaleFactor = 1f;

    private static final ConcurrentMap<String, Float> charWidthCache = new ConcurrentHashMap<>(1024);

    private static volatile int lastThemeRgb = 0;

    private static float lastTitleSize = -1f;
    private static float lastArtistSize = -1f;
    private static final AtomicInteger COUNTER = new AtomicInteger(0);
    private static float lastTimeSize = -1f;
    private static float lastIconSize = -1f;

    private static final ScheduledExecutorService positionScheduler =
            Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "MediaPlayer-Position");
                t.setDaemon(true);
                t.setPriority(Thread.NORM_PRIORITY - 1);
                return t;
            });

    private static final ScheduledExecutorService metadataScheduler =
            Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "MediaPlayer-Metadata");
                t.setDaemon(true);
                t.setPriority(Thread.NORM_PRIORITY - 1);
                return t;
            });

    private static final ExecutorService imageDecoder =
            Executors.newSingleThreadExecutor(r -> {
                Thread t = new Thread(r, "MediaPlayer-ImageDecoder");
                t.setDaemon(true);
                t.setPriority(Thread.MIN_PRIORITY);
                return t;
            });

    private static final ExecutorService commandExecutor =
            Executors.newSingleThreadExecutor(r -> {
                Thread t = new Thread(r, "MediaPlayer-Commands");
                t.setDaemon(true);
                return t;
            });

    private static final AtomicBoolean metadataUpdateRunning = new AtomicBoolean(false);
    private static final AtomicBoolean positionUpdateRunning = new AtomicBoolean(false);
    private static final AtomicBoolean imageDecoding = new AtomicBoolean(false);
    private static final AtomicBoolean lyricsQueued = new AtomicBoolean(false);
    private static final HttpClient httpClient = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();

    private static boolean libraryInitialized = false;
    private static boolean monitoringStarted = false;

    private static final Identifier coverTextureLocation = Identifier.of("lightclient", "music_cover");
    private static volatile NativeImageBackedTexture coverTexture = null;
    private static final AtomicReference<byte[]> pendingAlbumArt = new AtomicReference<>(null);
    private static volatile boolean coverTextureRegistered = false;

    private static volatile String lastRenderedTrack = null;
    private static volatile long lastTextureUpdateTime = 0;
    private static final SimpleLinearAnimation trackSwapAnimation = new SimpleLinearAnimation(220);
    private static final SimpleLinearAnimation lyricsWidthAnimation = new SimpleLinearAnimation(360);
    private static final float NEW_RECT_WIDTH = 220f;
    private static final float NEW_LYRICS_WIDTH_ADD = 25f;
    private static final float NEW_RECT_HEIGHT = 110f;
    private static final float NEW_TEXT_RECT_HEIGHT = 178f;
    private static final float NEW_PROGRESS_BAR_HEIGHT = 4.5f;
    private static final float NEW_CONTROL_ICON_SIZE = 18f;
    private static final float NEW_CONTROL_HITBOX = 24f;
    private static final int MAX_LYRIC_CHARS = 34;
    private static final String PREVIOUS_ICON = "r";
    private static final String PAUSE_ICON = "m";
    private static final String NEXT_ICON = "n";
    private static final long MARQUEE_PAUSE_MS = 3000L;
    private static final float MARQUEE_SPEED_PX_PER_SEC = 28f;
    private static final float MARQUEE_GAP = 18f;


    private static void initializeLibrary() {
        if (!libraryInitialized) {
            try {
                NativeLoader.load();
                libraryInitialized = true;
                System.out.println("[MediaPlayer] Native library loaded");
            } catch (Exception e) {
                System.err.println("[MediaPlayer] Failed to load library: " + e.getMessage());
            }
        }
    }

    @EventHandler
    public void onTick(UpdateEvent e) {
        if (!libraryInitialized) {
            initializeLibrary();
            if (!libraryInitialized) return;
        }

        if (!monitoringStarted) {
            startMonitoring();
            monitoringStarted = true;
        }
    }

    public static boolean handleMouseClick(double rawMouseX, double rawMouseY, int button) {
        if (button != 0) {
            return false;
        }

        if (!libraryInitialized) {
            return false;
        }

        float hudScale = Interface.getHudScale(SETTINGS_SCOPE);
        float mx = (float) rawMouseX / Math.max(0.01f, hudScale);
        float my = (float) rawMouseY / Math.max(0.01f, hudScale);
        float btnSize = DEF_BTN_SIZE;

        String variant = DraggingManager.getHudMode(SETTINGS_SCOPE, SETTING_VARIANT, VARIANT_NEW);
        if (!VARIANT_DEFAULT.equals(variant)) {
            if (MathUtil.isHovered(mx, my, cmpPrevX, cmpControlY, CMP_CONTROL_SIZE, CMP_CONTROL_SIZE)) {
                executeMediaCommand(() -> NativeLoader.INSTANCE.SkipPrevious());
                return true;
            }
            if (MathUtil.isHovered(mx, my, cmpPauseX, cmpControlY, CMP_CONTROL_SIZE, CMP_CONTROL_SIZE)) {
                executeMediaCommand(() -> NativeLoader.INSTANCE.TogglePlayPause());
                return true;
            }
            if (MathUtil.isHovered(mx, my, cmpNextX, cmpControlY, CMP_CONTROL_SIZE, CMP_CONTROL_SIZE)) {
                executeMediaCommand(() -> NativeLoader.INSTANCE.SkipNext());
                return true;
            }
            return false;
        }

        if (MathUtil.isHovered(mx, my, defPrevBtnX, defPrevBtnY, btnSize, btnSize)) {
            executeMediaCommand(() -> NativeLoader.INSTANCE.SkipPrevious());
            return true;
        }
        if (MathUtil.isHovered(mx, my, defNextBtnX, defNextBtnY, btnSize, btnSize)) {
            executeMediaCommand(() -> NativeLoader.INSTANCE.SkipNext());
            return true;
        }
        return false;
    }

    private static void startMonitoring() {
        metadataScheduler.scheduleAtFixedRate(() -> {
            if (!metadataUpdateRunning.compareAndSet(false, true)) return;
            try {
                performMetadataUpdate();
            } finally {
                metadataUpdateRunning.set(false);
            }
        }, 0, 1500, TimeUnit.MILLISECONDS);

        positionScheduler.scheduleAtFixedRate(() -> {
            if (!positionUpdateRunning.compareAndSet(false, true)) return;
            try {
                performPositionUpdate();
            } finally {
                positionUpdateRunning.set(false);
            }
        }, 100, 400, TimeUnit.MILLISECONDS);
    }
    private static void performPositionUpdate() {
        if (trackName == null || trackName.isEmpty()) return;

        PositionInfo info = null;
        try {
            info = new PositionInfo();
            if (!NativeLoader.INSTANCE.GetCurrentPositionInfo(info)) return;

            long newCurrentTime = Math.max(0L, info.positionMs);
            long newAllTime = Math.max(0L, info.durationMs);
            long newDurationMs = info.durationMs > 0 ? info.durationMs : totalTime;
            boolean newPlaying = info.isPlaying();

            currentTime = newCurrentTime;
            allTime = newAllTime;
            progress = Math.min(1.0f, Math.max(0.0f, (float) newCurrentTime / (float) newDurationMs));
            isPlaying = newPlaying;

            if (newDurationMs != totalTime && newDurationMs > 0) {
                totalTime = newDurationMs;
            }

        } catch (Throwable ignored) {
        }
    }

    private static void scheduleImageDecoding() {
        if (!imageDecoding.compareAndSet(false, true)) return;

        imageDecoder.execute(() -> {
            try {
                byte[] albumArt = pendingAlbumArt.getAndSet(null);
                if (albumArt == null) return;

                NativeImage decodedImage = NativeImage.read(new ByteArrayInputStream(albumArt));

                MinecraftClient.getInstance().execute(() -> {
                    try {
                        TextureManager tm = MinecraftClient.getInstance().getTextureManager();
                        try {
                            if (coverTexture != null) {
                                tm.destroyTexture(coverTextureLocation);
                                coverTexture.close();
                                coverTexture = null;
                                coverTextureRegistered = false;
                            }
                        } catch (Exception ignored) {}
                        Supplier<String> nameSupplier = () -> "cover_texture_" + COUNTER.getAndIncrement();
                        coverTexture = new NativeImageBackedTexture(nameSupplier, decodedImage);
                        tm.registerTexture(coverTextureLocation, coverTexture);
                        coverTextureRegistered = true;
                    } catch (Exception e) {
                        System.err.println("[MediaPlayer] Texture update failed: " + e.getMessage());
                        try { decodedImage.close(); } catch (Exception ignored) {}
                        clearCoverTexture();
                    } finally {
                        imageDecoding.set(false);
                    }
                });
            } catch (Exception e) {
                System.err.println("[MediaPlayer] Image decode failed: " + e.getMessage());
                imageDecoding.set(false);
            }
        });
    }

    private static void performMetadataUpdate() {
        MediaInfo info = null;
        try {
            info = new MediaInfo();
            if (!NativeLoader.INSTANCE.GetCurrentMediaInfo(info)) {
                MinecraftClient.getInstance().execute(MediaPlayer::clearData);
                return;
            }

            String title = info.getTitle();
            if (title == null || title.isEmpty()) {
                MinecraftClient.getInstance().execute(MediaPlayer::clearData);
                return;
            }

            boolean trackChanged = info.hasTrackChanged();

            String newTrackName = title.toLowerCase();
            String newArtistsText = (info.getArtist() != null && !info.getArtist().isEmpty())
                    ? info.getArtist().toLowerCase()
                    : "Unknown artist";
            long newTotalTime = info.durationMs > 0 ? info.durationMs : 1L;
            requestLyrics(title, info.getArtist(), newTotalTime);

            boolean titleChanged = trackName == null || !newTrackName.equals(trackName);
            boolean artistChanged = artistsText == null || !newArtistsText.equals(artistsText);
            boolean metadataChanged = trackChanged || titleChanged || artistChanged;

            if (metadataChanged) {
                byte[] albumArt = info.getAlbumArt();
                if (albumArt != null && albumArt.length > 0) {
                    pendingAlbumArt.set(albumArt);
                    scheduleImageDecoding();
                } else {
                    MinecraftClient.getInstance().execute(MediaPlayer::clearCoverTexture);
                }
            }

            long newCurrentTime = Math.max(0L, info.positionMs);
            boolean newPlaying = info.isPlaying();

            MinecraftClient.getInstance().execute(() -> {
                if (metadataChanged) {
                    previousTrackName = trackName;
                    previousArtistsText = artistsText;
                    trackName = newTrackName;
                    artistsText = newArtistsText;
                    totalTime = newTotalTime;
                    trackSwapAnimation.setDuration(220);
                    trackSwapAnimation.setEasing(Easings.EASE_OUT_CUBIC);
                    trackSwapAnimation.show();
                }
                currentTime = newCurrentTime;
                progress = Math.min(1.0f, Math.max(0.0f, (float) newCurrentTime / (float) newTotalTime));
                isPlaying = newPlaying;
            });

        } catch (Throwable e) {
            System.err.println("[MediaPlayerRenderer] Metadata error: " + e.getMessage());
        } finally {
            if (info != null) {
                try {
                    NativeLoader.INSTANCE.FreeMediaInfo(info);
                } catch (Exception ignored) {}
            }
        }
    }

    private static void clearData() {
        trackName = null;
        artistsText = null;
        previousTrackName = null;
        previousArtistsText = null;
        lyricsKey = "";
        lyricsLines = new String[0];
        syncedLyricsLines = new LyricLine[0];
        displayedLyricIndex = -1;
        previousLyricIndex = -1;
        lyricSwitchMs = 0L;
        currentTime = 0;
        allTime = 0;
        totalTime = 1;
        progress = 0.0f;
        isPlaying = false;
        trackSwapAnimation.hide();
        clearCoverTexture();
    }

    private static void clearCoverTexture() {
        MinecraftClient.getInstance().execute(() -> {
            if (coverTexture != null) {
                try {
                    MinecraftClient.getInstance().getTextureManager()
                            .destroyTexture(coverTextureLocation);
                    coverTexture.close();
                } catch (Exception ignored) {}
                coverTexture = null;
                coverTextureRegistered = false;
            }
        });
    }

    public void render(EventRender.Screen.Hud event) {
        String variant = DraggingManager.getHudMode(SETTINGS_SCOPE, SETTING_VARIANT, VARIANT_NEW);
        if (VARIANT_DEFAULT.equals(variant)) {
            renderDefault(event);
        } else {
            renderCompact(event);
        }
    }

    // ------------------------------------------------------------------
    // Компактный плеер — перенос дизайна из LightReload (Figma MUSICPLAYER):
    // обложка, название/исполнитель, прогресс с таймкодами и кнопки
    // prev / pause / next. Шрифты, тени и жидкое стекло — проекта.
    // ------------------------------------------------------------------

    private static final float CMP_WIDTH = 210f;
    private static final float CMP_HEIGHT = 62f;
    private static final float CMP_COVER = 40f;
    private static final float CMP_ROUNDING = 6f;
    private static final float CMP_TEXT_X = 58f;
    private static final float CMP_TITLE_SIZE = 8f;
    private static final float CMP_ARTIST_SIZE = 7f;
    private static final float CMP_TIME_SIZE = 6.5f;
    private static final float CMP_CONTROL_SIZE = 16f;
    private static final float CMP_CONTROL_ICON = 8f;

    private static volatile float cmpPrevX = -1000f;
    private static volatile float cmpPauseX = -1000f;
    private static volatile float cmpNextX = -1000f;
    private static volatile float cmpControlY = -1000f;

    private void renderCompact(EventRender.Screen.Hud event) {
        float scaleFactor = Interface.getHudScale(SETTINGS_SCOPE);
        float x = Interface.scalePos(SETTINGS_SCOPE, dragging.getX());
        float y = Interface.scalePos(SETTINGS_SCOPE, dragging.getY());

        boolean isChatOpen = MinecraftClient.getInstance().currentScreen instanceof ChatScreen;
        boolean hasMedia = trackName != null && !trackName.isEmpty();

        boolean shouldShow = isChatOpen || hasMedia;
        animation.setDuration(Interface.getAlphaDurationMs());
        if (shouldShow) animation.show();
        else animation.hide();

        float alphaProgress = animation.getProgress();
        if (alphaProgress <= 0f && animation.get() == 0) {
            width = 0;
            height = 0;
            dragging.setWidth(0);
            dragging.setHeight(0);
            return;
        }

        float rectWidth = CMP_WIDTH;
        float rectHeight = CMP_HEIGHT;
        float coverX = x + 10f;
        float coverY = y + (rectHeight - CMP_COVER) / 2f;
        float textX = x + CMP_TEXT_X;
        float textWidthLimit = rectWidth - CMP_TEXT_X - 10f;

        Renderer2D render = event.getRenderer();
        float centerX = x + rectWidth * 0.5f;
        float centerY = y + rectHeight * 0.5f;
        render.pushScale(1f, 1f, centerX, centerY);
        try {
            // Панель: тень и жидкое стекло проекта + тонкая обводка нового HUD.
            HudShadowRenderer.render(render, SETTINGS_SCOPE, x, y, rectWidth, rectHeight, CMP_ROUNDING, alphaProgress, scaleFactor);
            Interface.renderHudBackground(render, x, y, rectWidth, rectHeight, CMP_ROUNDING, alphaProgress);
            if (!Interface.isHudBlurBackground()) {
                render.rect(x, y, rectWidth, rectHeight, CMP_ROUNDING,
                        ClientColors.applyAlpha(0xFF0C0C0F, 0.85f * alphaProgress));
            }
            render.rectOutline(x, y, rectWidth, rectHeight, CMP_ROUNDING,
                    ClientColors.applyAlpha(0xFFFFFFFF, 0.12f * alphaProgress), 1f);

            // Обложка трека (или логотип клиента, если музыки нет).
            if (hasMedia && coverTextureRegistered) {
                render.drawTextureRounded(coverTextureLocation, coverX, coverY, CMP_COVER, CMP_COVER,
                        ClientColors.applyAlpha(0xFFFFFFFF, alphaProgress), 4f);
            } else {
                GifTexture avatarGif = GifTexture.getCached(Identifier.of("light", "icon.png"));
                if (avatarGif == null) {
                    GifTexture.queueLoad(Identifier.of("light", "icon.png"));
                }
                Identifier frame = avatarGif != null ? avatarGif.getCurrentFrame() : null;
                if (frame != null) {
                    render.drawTextureRounded(frame, coverX, coverY, CMP_COVER, CMP_COVER,
                            ClientColors.applyAlpha(0xFFFFFFFF, alphaProgress), 4f);
                } else {
                    render.rect(coverX, coverY, CMP_COVER, CMP_COVER, 4f,
                            ClientColors.applyAlpha(new Color(25, 25, 30).getRGB(), alphaProgress));
                }
            }

            // Название и исполнитель — бегущей строкой, шрифт проекта.
            String titleText = hasMedia ? capitalize(trackName) : "Нексис клиент";
            String artistText = (hasMedia && artistsText != null && !artistsText.isEmpty())
                    ? capitalize(artistsText) : "Sterford";
            int titleColor = ClientColors.applyAlpha(ClientColors.TEXT.getRGB(), alphaProgress);
            int artistColor = ClientColors.applyAlpha(Color.lightGray.getRGB(), alphaProgress);

            drawMarqueeText(render, FontRegistry.DELTA_HUD, textX, y + 12f, textWidthLimit,
                    CMP_TITLE_SIZE, titleText, titleColor);
            drawMarqueeText(render, FontRegistry.DELTA_HUD, textX, y + 23f, textWidthLimit,
                    CMP_ARTIST_SIZE, artistText, artistColor);

            // Прогресс с таймкодами.
            String timeCurrent = hasMedia ? formatTime(currentTime) : "0:00";
            String timeTotal = hasMedia ? formatTime(allTime > 0 ? allTime : totalTime) : "0:00";
            float barX = x + 12f;
            float barY = y + rectHeight - 20f;
            float barWidth = rectWidth - 24f;
            int timeColor = ClientColors.applyAlpha(ClientColors.TEXT.getRGB(), alphaProgress);
            render.text(FontRegistry.DELTA_HUD, barX, barY - 9f, CMP_TIME_SIZE, timeCurrent, timeColor);
            float totalW = FontRegistry.DELTA_HUD.getWidth(timeTotal, CMP_TIME_SIZE);
            render.text(FontRegistry.DELTA_HUD, barX + barWidth - totalW, barY - 9f, CMP_TIME_SIZE, timeTotal, timeColor);

            int progressTrack = ClientColors.applyAlpha(0xFFFFFFFF, alphaProgress * 0.24f);
            int progressFill = ClientColors.applyAlpha(0xFFFFFFFF, alphaProgress * 0.78f);
            render.rect(barX, barY, barWidth, 3f, 1.5f, progressTrack);
            render.rect(barX, barY, barWidth * Math.max(0f, Math.min(1f, progress)), 3f, 1.5f, progressFill);

            // Кнопки prev / pause / next.
            float controlsY = y + rectHeight - CMP_CONTROL_SIZE - 4f;
            float prevX = centerX - 28f - CMP_CONTROL_SIZE / 2f;
            float pauseX = centerX - CMP_CONTROL_SIZE / 2f;
            float nextX = centerX + 28f - CMP_CONTROL_SIZE / 2f;
            cmpPrevX = prevX;
            cmpPauseX = pauseX;
            cmpNextX = nextX;
            cmpControlY = controlsY;

            float hudScale = Interface.getHudScale(SETTINGS_SCOPE);
            float mx = (float) MinecraftClient.getInstance().mouse.getX() / Math.max(0.01f, hudScale);
            float my = (float) MinecraftClient.getInstance().mouse.getY() / Math.max(0.01f, hudScale);

            drawControlGlyph(render, PREVIOUS_ICON, prevX, controlsY,
                    MathUtil.isHovered(mx, my, prevX, controlsY, CMP_CONTROL_SIZE, CMP_CONTROL_SIZE), alphaProgress);
            String playPauseGlyph = isPlaying ? PAUSE_ICON : "v";
            drawControlGlyph(render, playPauseGlyph, pauseX, controlsY,
                    MathUtil.isHovered(mx, my, pauseX, controlsY, CMP_CONTROL_SIZE, CMP_CONTROL_SIZE), alphaProgress);
            drawControlGlyph(render, NEXT_ICON, nextX, controlsY,
                    MathUtil.isHovered(mx, my, nextX, controlsY, CMP_CONTROL_SIZE, CMP_CONTROL_SIZE), alphaProgress);
        } finally {
            render.popScale();
        }

        width = rectWidth;
        height = rectHeight;
        dragging.setWidth(width * scaleFactor);
        dragging.setHeight(height * scaleFactor);
    }

    private void drawControlGlyph(Renderer2D render, String glyph, float btnX, float btnY, boolean hovered, float alphaProgress) {
        float glyphW = FontRegistry.ICONS_ASYNC.getWidth(glyph, CMP_CONTROL_ICON);
        float glyphBaseline = btnY + CMP_CONTROL_SIZE * 0.5f
                + FontRegistry.centeredBaselineOffset(FontRegistry.ICONS_ASYNC, glyph.charAt(0), CMP_CONTROL_ICON);
        int color = ClientColors.applyAlpha(hovered ? 0xFFFFFFFF : 0xFFAAB0C0, alphaProgress);
        render.text(FontRegistry.ICONS_ASYNC, btnX + (CMP_CONTROL_SIZE - glyphW) * 0.5f, glyphBaseline,
                CMP_CONTROL_ICON, glyph, color);
    }

    private void renderDefault(EventRender.Screen.Hud event) {
        float startScale = Interface.isBounceAnimation() ? 0f : 0.8f;

        float scaleFactor = Interface.getHudScale(SETTINGS_SCOPE);
        float x = Interface.scalePos(SETTINGS_SCOPE, dragging.getX());
        float y = Interface.scalePos(SETTINGS_SCOPE, dragging.getY());

        boolean isChatOpen = MinecraftClient.getInstance().currentScreen instanceof ChatScreen;
        boolean hasMedia = trackName != null && !trackName.isEmpty();

        boolean shouldShow = isChatOpen || hasMedia;
        animation.setDuration(Interface.getAlphaDurationMs());
        if (shouldShow) animation.show();
        else animation.hide();

        float alphaProgress = animation.getProgress();
        boolean hiding = animation.getTarget() == 0f;
        float scale = Interface.animatedScale(startScale, alphaProgress, hiding);

        if (alphaProgress <= 0f && animation.get() == 0) {
            width = 0;
            height = 0;
            dragging.setWidth(0);
            dragging.setHeight(0);
            return;
        }

        float rectWidth = 200f;
        float rectHeight = 48f;
        float coverSize = 32f;
        float coverX = x + 6f;
        float coverY = y + (rectHeight - coverSize) * 0.5f;
        float coverRound = 5f;

        float btnSize = DEF_BTN_SIZE;
        float btnIconSize = DEF_BTN_ICON_SIZE;
        float btnIconNudge = DEF_BTN_ICON_NUDGE;
        float btnGap = 1f;
        float btnRightPad = 6f;
        float nextBtnX = x + rectWidth - btnRightPad - btnSize;
        float nextBtnY = y + (rectHeight - btnSize) * 0.5f;
        float prevBtnX = nextBtnX - btnGap - btnSize;
        float prevBtnY = nextBtnY;

        float textX = coverX + coverSize + 8f;
        float textEndX = prevBtnX - 8f;
        float textClipW = textEndX - textX;
        float textSize = 11f;
        float textY = y + rectHeight * 0.5f + FontRegistry.centeredBaselineOffset(FontRegistry.DELTA_HUD, 'H', textSize);

        int panelColor = ClientColors.applyAlpha(ClientColors.BACKGROUND.getRGB(), alphaProgress);
        int textColor = ClientColors.applyAlpha(0xFFFFFFFF, alphaProgress);

        float centerX = x + rectWidth * 0.5f;
        float centerY = y + rectHeight * 0.5f;
        event.getRenderer().pushScale(scale, scale, centerX, centerY);
        try {

        float panelRounding = 10f;
        HudShadowRenderer.render(event.getRenderer(), SETTINGS_SCOPE, x, y, rectWidth, rectHeight, panelRounding, alphaProgress, Interface.getHudScale(SETTINGS_SCOPE));
        event.getRenderer().blur(x, y, rectWidth, rectHeight, panelRounding, alphaProgress);
        event.getRenderer().rect(x, y, rectWidth, rectHeight, panelRounding, panelColor);

        if (hasMedia && coverTextureRegistered) {
            event.getRenderer().drawTextureRounded(coverTextureLocation, coverX, coverY, coverSize, coverSize,
                    ClientColors.applyAlpha(0xFFFFFFFF, alphaProgress), coverRound);
        } else if (!hasMedia && isChatOpen) {
            GifTexture avatarGif = GifTexture.getCached(Identifier.of("light", "icon.png"));
            if (avatarGif == null) {
                GifTexture.queueLoad(Identifier.of("light", "icon.png"));
            }
            Identifier frame = avatarGif != null ? avatarGif.getCurrentFrame() : null;
            if (frame != null) {
                event.getRenderer().drawTextureRounded(frame, coverX, coverY, coverSize, coverSize,
                        ClientColors.applyAlpha(0xFFFFFFFF, alphaProgress), coverRound);
            } else {
                event.getRenderer().rect(coverX, coverY, coverSize, coverSize, coverRound,
                        ClientColors.applyAlpha(new Color(25, 25, 30).getRGB(), alphaProgress));
            }
        } else {
            event.getRenderer().rect(coverX, coverY, coverSize, coverSize, coverRound,
                    ClientColors.applyAlpha(new Color(25, 25, 30).getRGB(), alphaProgress));
        }

        String titleText = hasMedia ? capitalize(trackName) : "No track";
        String artistText = (hasMedia && artistsText != null && !artistsText.isEmpty()) ? capitalize(artistsText) : "";
        String displayText = artistText.isEmpty() ? titleText : titleText + " - " + artistText;

        float fullWidth = FontRegistry.DELTA_HUD.getWidth(displayText, textSize);
        event.getRenderer().pushClipRect((int) textX, (int) (y + 2), (int) textClipW, (int) (rectHeight - 4));

        if (fullWidth <= textClipW) {
            event.getRenderer().text(FontRegistry.DELTA_HUD, textX, textY, textSize, displayText, textColor);
        } else {
            float marqueeGap = 24f;
            float travel = fullWidth - textClipW + marqueeGap;
            long animationMs = Math.max(1L, (long) ((travel / 28f) * 1000f));
            long pauseMs = 2000L;
            long cycle = pauseMs + animationMs + pauseMs + animationMs;
            long time = System.currentTimeMillis() % cycle;
            float offset;
            if (time < pauseMs) {
                offset = 0f;
            } else {
                time -= pauseMs;
                if (time < animationMs) {
                    float t = time / (float) animationMs;
                    offset = -(t * t * (3f - 2f * t)) * travel;
                } else {
                    time -= animationMs;
                    if (time < pauseMs) {
                        offset = -travel;
                    } else {
                        time -= pauseMs;
                        float t = 1f - (time / (float) animationMs);
                        offset = -(t * t * (3f - 2f * t)) * travel;
                    }
                }
            }
            event.getRenderer().text(FontRegistry.DELTA_HUD, textX + offset, textY, textSize, displayText, textColor);
            event.getRenderer().text(FontRegistry.DELTA_HUD, textX + offset + fullWidth + marqueeGap, textY, textSize, displayText, textColor);
        }

        event.getRenderer().popClipRect();

        float hudScale = Interface.getHudScale(SETTINGS_SCOPE);
        float mx = (float) MinecraftClient.getInstance().mouse.getX() / Math.max(0.01f, hudScale);
        float my = (float) MinecraftClient.getInstance().mouse.getY() / Math.max(0.01f, hudScale);

        boolean prevHover = MathUtil.isHovered(mx, my, prevBtnX, prevBtnY, btnSize, btnSize);
        boolean nextHover = MathUtil.isHovered(mx, my, nextBtnX, nextBtnY, btnSize, btnSize);
        int prevColor = ClientColors.applyAlpha(prevHover ? 0xFFFFFFFF : 0xFFAAB0C0, alphaProgress);
        int nextColor = ClientColors.applyAlpha(nextHover ? 0xFFFFFFFF : 0xFFAAB0C0, alphaProgress);

        float prevIconW = FontRegistry.LIGHT_TTF.getWidth(PREVIOUS_ICON, btnIconSize);
        float prevIconY = prevBtnY + btnSize * 0.5f
                + FontRegistry.centeredBaselineOffset(FontRegistry.LIGHT_TTF, PREVIOUS_ICON.charAt(0), btnIconSize)
                + btnIconNudge;
        event.getRenderer().text(FontRegistry.LIGHT_TTF, prevBtnX + (btnSize - prevIconW) * 0.5f,
                prevIconY, btnIconSize, PREVIOUS_ICON, prevColor);

        float nextIconW = FontRegistry.LIGHT_TTF.getWidth(NEXT_ICON, btnIconSize);
        float nextIconY = nextBtnY + btnSize * 0.5f
                + FontRegistry.centeredBaselineOffset(FontRegistry.LIGHT_TTF, NEXT_ICON.charAt(0), btnIconSize)
                + btnIconNudge;
        event.getRenderer().text(FontRegistry.LIGHT_TTF, nextBtnX + (btnSize - nextIconW) * 0.5f,
                nextIconY, btnIconSize, NEXT_ICON, nextColor);

        defPrevBtnX = prevBtnX;
        defPrevBtnY = prevBtnY;
        defNextBtnX = nextBtnX;
        defNextBtnY = nextBtnY;

        float progBarY = y + rectHeight - 3f;
        float progBarH = 2f;
        float progBarX = x + 2f + 6f;
        float progBarW = rectWidth - 4f - 12f;
        int progTrackColor = ClientColors.applyAlpha(0xFFFFFFFF, (int)(alphaProgress * 50));
        int progFillColor = ClientColors.applyAlpha(0xFFFFFFFF, (int)(alphaProgress * 200));
        event.getRenderer().rect(progBarX , progBarY, progBarW, progBarH, progBarH * 0.5f, progTrackColor);
        event.getRenderer().rect(progBarX, progBarY, progBarW * Math.max(0f, Math.min(1f, progress)), progBarH, progBarH * 0.5f, progFillColor);

        } finally {
            event.getRenderer().popScale();
        }

        width = rectWidth;
        height = rectHeight;
        dragging.setWidth(width * scaleFactor);
        dragging.setHeight(height * scaleFactor);
    }

    private void renderNew(EventRender.Screen.Hud event) {
        float startScale = Interface.isBounceAnimation() ? 0f : 0.8f;

        float scaleFactor = Interface.getHudScale(SETTINGS_SCOPE);
        float x = Interface.scalePos(SETTINGS_SCOPE, dragging.getX());
        float y = Interface.scalePos(SETTINGS_SCOPE, dragging.getY());

        boolean isChatOpen = MinecraftClient.getInstance().currentScreen instanceof ChatScreen;
        boolean hasMedia = trackName != null && !trackName.isEmpty();

        boolean shouldShow = isChatOpen || hasMedia;
        animation.setDuration(Interface.getAlphaDurationMs());
        if (shouldShow) {
            animation.show();
        } else {
            animation.hide();
        }

        float alphaProgress = animation.getProgress();
        boolean hiding = animation.getTarget() == 0f;
        float scale = Interface.animatedScale(startScale, alphaProgress, hiding);

        if (alphaProgress <= 0f && animation.get() == 0) {
            width = 0;
            height = 0;
            dragging.setWidth(0);
            dragging.setHeight(0);
            return;
        }

        boolean showText = DraggingManager.getHudBoolean(SETTINGS_SCOPE, SETTING_SHOW_TEXT, false);
        boolean showLyrics = showText && hasRenderableLyrics();
        lyricsWidthAnimation.setDuration(360);
        if (showLyrics) {
            lyricsWidthAnimation.show();
        } else {
            lyricsWidthAnimation.hide();
        }

        float rectWidth = NEW_RECT_WIDTH + NEW_LYRICS_WIDTH_ADD * lyricsWidthAnimation.getProgress();
        currentNewRectWidth = rectWidth;
        float rectHeight = showLyrics ? NEW_TEXT_RECT_HEIGHT : NEW_RECT_HEIGHT;
        float coverSize = 56f;
        float coverX = x + 8f;
        float coverY = y + 8f;
        float coverRound = Interface.getHudRounding(SETTINGS_SCOPE, 6f);

        float textX = coverX + coverSize + 10f;
        float textY = coverY + 22;
        float textSize = 18f;
        float timeSize = 13f;

        int textColor = ClientColors.applyAlpha(ClientColors.TEXT.getRGB(), alphaProgress);
        int accentColor = ClientColors.applyAlpha(ClientColors.GRADIENT_START.getRGB(), alphaProgress);
        int mutedTextColor = ClientColors.applyAlpha(Color.lightGray.getRGB(), alphaProgress);
        int progressBgColor = ClientColors.applyAlpha(0xFF555555, alphaProgress);

        float centerX = x + rectWidth * 0.5f;
        float centerY = y + rectHeight * 0.5f;
        event.getRenderer().pushScale(scale, scale, centerX, centerY);
        try {

        float panelRounding = Interface.getHudRounding(SETTINGS_SCOPE, 10f);
        int headerColor = ClientColors.applyAlpha(new Color(8, 8, 8, 160).getRGB(), alphaProgress);
        int panelColor = ClientColors.applyAlpha(ClientColors.BACKGROUND.getRGB(), alphaProgress);


        
        HudShadowRenderer.render(event.getRenderer(), SETTINGS_SCOPE, x, y, rectWidth, rectHeight, panelRounding, alphaProgress, Interface.getHudScale(SETTINGS_SCOPE));
        event.getRenderer().blur(x, y, rectWidth, rectHeight, panelRounding, alphaProgress);
        event.getRenderer().rect(x, y, rectWidth, rectHeight, panelRounding, panelColor);

        GifTexture avatarGif = null;
        if (!hasMedia && isChatOpen) {
            if (avatarGif == null || avatarGif.getCurrentFrame() == null) {
                avatarGif = GifTexture.getCached(Identifier.of("light", "gif/avatar.gif"));
                if (avatarGif == null) {
                    GifTexture.queueLoad(Identifier.of("light", "gif/avatar.gif"));
                }
            }
            Identifier frame = avatarGif != null ? avatarGif.getCurrentFrame() : null;
            if (frame != null) {
                event.getRenderer().drawTextureRounded(frame, coverX, coverY, coverSize, coverSize, ClientColors.applyAlpha(0xFFFFFFFF, alphaProgress), coverRound);
            } else {
                event.getRenderer().rect(coverX, coverY, coverSize, coverSize, coverRound, ClientColors.applyAlpha(new Color(25, 25, 30).getRGB(), alphaProgress));
            }


            String titleText = "Нексис клиент";
            String artistText = "Sterford";

            drawMarqueeText(event.getRenderer(), FontRegistry.DELTA_HUD, textX, textY,
                    rectWidth -84f, textSize, titleText, textColor);
            drawMarqueeText(event.getRenderer(), FontRegistry.DELTA_HUD, textX, textY + 20,
                    rectWidth -84f, textSize - 1, artistText, mutedTextColor);

            float fakeProgress = (float)(System.currentTimeMillis() % 2000) / 2000f;
            drawNewControls(event, x, y, rectWidth, rectHeight, alphaProgress, "0:00", "14:88");
            drawNewProgress(event, x, y, rectWidth, rectHeight, fakeProgress, accentColor, progressBgColor);
            if (showLyrics) {
                drawLyricsDisplay(event, x, y, rectWidth, alphaProgress);
            }
        } else {
            if (coverTextureRegistered) {
                event.getRenderer().drawTextureRounded(coverTextureLocation, coverX, coverY, coverSize, coverSize, ClientColors.applyAlpha(0xFFFFFFFF, alphaProgress), coverRound);
            }

            String titleText = trackName != null ? capitalize(trackName) : "Нексис клиент";
            String artistText = artistsText != null ? capitalize(artistsText) : "Sterford";
            int titleColor = ClientColors.applyAlpha(ClientColors.TEXT.getRGB(), alphaProgress);
            int artistColor = ClientColors.applyAlpha(Color.lightGray.getRGB(), alphaProgress);

            drawMarqueeText(event.getRenderer(), FontRegistry.DELTA_HUD, textX, textY,
                    rectWidth -84f, textSize, titleText, titleColor);
            drawMarqueeText(event.getRenderer(), FontRegistry.DELTA_HUD, textX, textY + 20  ,
                    rectWidth -84f, textSize - 1, artistText, artistColor);

            if (trackSwapAnimation.isFinished()) {
                previousTrackName = null;
                previousArtistsText = null;
            }

            drawNewControls(event, x, y, rectWidth, rectHeight, alphaProgress, formatTime(currentTime), formatTime(allTime));
            drawNewProgress(event, x, y, rectWidth, rectHeight, progress, accentColor, progressBgColor);
            if (showLyrics) {
                drawLyricsDisplay(event, x, y, rectWidth, alphaProgress);
            }
        }

        } finally {
            event.getRenderer().popScale();
        }

        width = rectWidth;
        height = rectHeight;
        dragging.setWidth(width * scaleFactor);
        dragging.setHeight(height * scaleFactor);
    }

    private void drawLyricsDisplay(EventRender.Screen.Hud event, float x, float y, float rectWidth, float alphaProgress) {
        String[] lines = currentLyricsLines();
        int index = currentLyricsIndex(lines);
        drawTrackTextDisplay(event, x, y, rectWidth, alphaProgress, lines, index);
    }

    private void drawTrackTextDisplay(EventRender.Screen.Hud event, float x, float y, float rectWidth, float alphaProgress,
                                      String[] lines, int currentIndex) {
        if (lines == null || lines.length == 0) {
            return;
        }

        float textSize = 12f;
        float lineHeight = 13.2f;
        float centerLineY = y + 110f;
        float highlightH = 16f;
        float highlightY = centerLineY - 9.2f;
        float innerX = x + 10f;
        float innerW = rectWidth - 20f;
        float rounding = Interface.getHudRounding(SETTINGS_SCOPE, 4f);
        long now = System.currentTimeMillis();

        int targetIndex = resolveDisplayedLyricIndex(currentIndex);

        if (displayedLyricIndex < 0) {
            displayedLyricIndex = targetIndex;
            previousLyricIndex = targetIndex;
            lyricSwitchMs = now - 280L;
        } else if (displayedLyricIndex != targetIndex) {
            previousLyricIndex = displayedLyricIndex;
            displayedLyricIndex = targetIndex;
            lyricSwitchMs = now;
        }

        float raw = MathUtil.clamp((now - lyricSwitchMs) / (float) LYRIC_TRANSITION_MS, 0f, 1f);
        float transition = easeInOutCubic(raw);
        int direction = Integer.compare(displayedLyricIndex, previousLyricIndex);
        float offset = direction == 0 ? 0f : direction * lineHeight * (1f - transition);

        int mutedColor = ClientColors.applyAlpha(Color.lightGray.getRGB(), alphaProgress * 0.62f);
        int currentColor = ClientColors.applyAlpha(ClientColors.TEXT.getRGB(), alphaProgress);
        int highlightColor = ClientColors.applyAlpha(ClientColors.GRADIENT_START.getRGB(), alphaProgress * 0.32f);


        int clipX = (int) Math.floor(innerX);
        int clipY = (int) Math.floor(y + 78f);
        int clipW = (int) Math.ceil(innerW);
        int clipH = (int) Math.ceil(66f);
        event.getRenderer().pushClipRect(clipX, clipY, clipW, clipH);
        try {
            for (int slot = -2; slot <= 2; slot++) {
                int lineIndex = displayedLyricIndex + slot;
                String line = lyricAt(lines, lineIndex);
                if (line.isEmpty()) {
                    continue;
                }
                float lineY = centerLineY + slot * lineHeight + offset;
                float distance = Math.abs(lineY - centerLineY);
                float lineAlpha = MathUtil.clamp(1f - Math.max(0f, distance - lineHeight * 1.35f) / (lineHeight * 1.25f), 0f, 1f);
                int color = distance < lineHeight * 0.5f
                        ? ClientColors.applyAlpha(ClientColors.TEXT.getRGB(), alphaProgress * lineAlpha)
                        : ClientColors.applyAlpha(Color.lightGray.getRGB(), alphaProgress * 0.62f * lineAlpha);
                drawCenteredTrackLine(event, line, x, rectWidth, lineY, textSize, color);
            }
        } finally {
            event.getRenderer().popClipRect();
        }
    }

    private static int resolveDisplayedLyricIndex(int currentIndex) {
        if (displayedLyricIndex < 0 || currentIndex >= displayedLyricIndex) {
            return currentIndex;
        }
        return displayedLyricIndex;
    }

    private void drawCenteredTrackLine(EventRender.Screen.Hud event, String text, float x, float rectWidth, float y, float size, int color) {
        String line = fitText(FontRegistry.DELTA_HUD, text, size, rectWidth - 24f);
        float textWidth = FontRegistry.DELTA_HUD.getWidth(line, size);
        event.getRenderer().text(FontRegistry.DELTA_HUD, x + (rectWidth - textWidth) * 0.5f, y, size, line, color);
    }

    private String fitText(FontObject font, String text, float size, float maxWidth) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        if (font.getWidth(text, size) <= maxWidth) {
            return text;
        }
        String suffix = "...";
        String value = text;
        while (!value.isEmpty() && font.getWidth(value + suffix, size) > maxWidth) {
            value = value.substring(0, value.length() - 1);
        }
        return value.isEmpty() ? suffix : value + suffix;
    }

    private static void requestLyrics(String rawTitle, String rawArtist, long durationMs) {
        String title = sanitizeToken(rawTitle);
        String artist = sanitizeToken(rawArtist);
        if (title.isBlank() || artist.isBlank()) {
            lyricsKey = "";
            lyricsLines = new String[0];
            syncedLyricsLines = new LyricLine[0];
            return;
        }
        String key = artist + " :: " + title;
        if (key.equalsIgnoreCase(lyricsKey) || !lyricsQueued.compareAndSet(false, true)) {
            return;
        }
        lyricsKey = key;
        lyricsLines = new String[0];
        syncedLyricsLines = new LyricLine[0];
        metadataScheduler.execute(() -> {
            try {
                String url = "https://lrclib.net/api/search?track_name=" + URLEncoder.encode(title, StandardCharsets.UTF_8)
                        + "&artist_name=" + URLEncoder.encode(artist, StandardCharsets.UTF_8);
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("User-Agent", "Light")
                        .GET()
                        .build();
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                LyricsResult result = parseLyrics(response.body(), title, artist, durationMs);
                lyricsLines = result.lines();
                syncedLyricsLines = result.synced();
            } catch (Throwable ignored) {
                lyricsLines = new String[0];
                syncedLyricsLines = new LyricLine[0];
            } finally {
                lyricsQueued.set(false);
            }
        });
    }

    private static LyricsResult parseLyrics(String body, String title, String artist, long durationMs) {
        if (body == null || body.isBlank()) {
            return emptyLyrics(title, artist);
        }
        JSONArray array = new JSONArray(body);
        if (array.isEmpty()) {
            return emptyLyrics(title, artist);
        }

        JSONObject best = null;
        int bestScore = Integer.MIN_VALUE;
        long targetSeconds = durationMs > 0L ? Math.round(durationMs / 1000f) : 0L;
        for (int i = 0; i < array.length(); i++) {
            JSONObject candidate = array.optJSONObject(i);
            if (candidate == null || candidate.optString("syncedLyrics", "").isBlank()) {
                continue;
            }

            int score = 100;
            long candidateDuration = candidate.optLong("duration", 0L);
            if (targetSeconds > 0L && candidateDuration > 0L) {
                long diff = Math.abs(candidateDuration - targetSeconds);
                score -= (int) Math.min(80L, diff * 4L);
            }
            if (candidate.optString("trackName", "").equalsIgnoreCase(title)) {
                score += 12;
            }
            if (candidate.optString("artistName", "").toLowerCase().contains(artist.toLowerCase())) {
                score += 12;
            }
            if (score > bestScore) {
                bestScore = score;
                best = candidate;
            }
        }

        if (best == null) {
            return emptyLyrics(title, artist);
        }

        LyricLine[] synced = parseSyncedLyrics(best.optString("syncedLyrics", ""));
        if (synced.length > 0) {
            String[] lines = Arrays.stream(synced).map(LyricLine::text).toArray(String[]::new);
            return new LyricsResult(lines, synced);
        }
        return emptyLyrics(title, artist);
    }

    private static LyricsResult emptyLyrics(String title, String artist) {
        return new LyricsResult(new String[0], new LyricLine[0]);
    }

    private static LyricLine[] parseSyncedLyrics(String raw) {
        if (raw == null || raw.isBlank()) {
            return new LyricLine[0];
        }
        LyricLine[] rawLines = Arrays.stream(raw.replace("\r", "").split("\n"))
                .map(MediaPlayer::parseSyncedLyricLine)
                .filter(line -> line != null && !line.text().isBlank())
                .toArray(LyricLine[]::new);
        return splitSyncedLyricLines(rawLines);
    }

    private static LyricLine parseSyncedLyricLine(String line) {
        if (line == null || line.length() < 11 || line.charAt(0) != '[') {
            return null;
        }
        int close = line.indexOf(']');
        if (close <= 1) {
            return null;
        }
        String[] parts = line.substring(1, close).split(":");
        if (parts.length != 2) {
            return null;
        }
        try {
            long minutes = Long.parseLong(parts[0]);
            double seconds = Double.parseDouble(parts[1]);
            return new LyricLine(minutes * 60_000L + Math.round(seconds * 1000.0), line.substring(close + 1).trim());
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private static LyricLine[] splitSyncedLyricLines(LyricLine[] lines) {
        if (lines == null || lines.length == 0) {
            return new LyricLine[0];
        }
        List<LyricLine> result = new ArrayList<>();
        for (int i = 0; i < lines.length; i++) {
            LyricLine line = lines[i];
            List<String> parts = splitLyricText(line.text());
            if (parts.size() <= 1) {
                result.add(line);
                continue;
            }
            long nextTime = i + 1 < lines.length ? lines[i + 1].timeMs() : line.timeMs() + 2_000L;
            long step = Math.max(260L, (nextTime - line.timeMs()) / parts.size());
            for (int part = 0; part < parts.size(); part++) {
                result.add(new LyricLine(line.timeMs() + step * part, parts.get(part)));
            }
        }
        return result.toArray(LyricLine[]::new);
    }

    private static List<String> splitLyricText(String text) {
        if (text == null) {
            return List.of();
        }
        String clean = text.trim();
        if (clean.length() <= MAX_LYRIC_CHARS) {
            return List.of(clean);
        }
        List<String> result = new ArrayList<>();
        String rest = clean;
        while (rest.length() > MAX_LYRIC_CHARS) {
            int split = findLyricSplit(rest);
            if (split <= 0 || split >= rest.length() - 1) {
                break;
            }
            String part = rest.substring(0, split).trim();
            if (!part.isBlank()) {
                result.add(part);
            }
            rest = rest.substring(split).trim();
        }
        if (!rest.isBlank()) {
            result.add(rest);
        }
        return result.isEmpty() ? List.of(clean) : result;
    }

    private static int findLyricSplit(String text) {
        int target = Math.min(MAX_LYRIC_CHARS, text.length() - 1);
        int best = -1;
        int bestDistance = Integer.MAX_VALUE;
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (!Character.isWhitespace(ch) && ch != ',' && ch != '-' && ch != ';') {
                continue;
            }
            int distance = Math.abs(i - target);
            if (distance < bestDistance) {
                best = i + (Character.isWhitespace(ch) ? 0 : 1);
                bestDistance = distance;
            }
        }
        return best;
    }

    private static float easeInOutCubic(float value) {
        float clamped = MathUtil.clamp(value, 0f, 1f);
        return clamped < 0.5f
                ? 4f * clamped * clamped * clamped
                : 1f - (float) Math.pow(-2f * clamped + 2f, 3f) * 0.5f;
    }

    private static String[] currentLyricsLines() {
        LyricLine[] synced = syncedLyricsLines;
        if (synced != null && synced.length > 0) {
            return Arrays.stream(synced).map(LyricLine::text).toArray(String[]::new);
        }
        return new String[0];
    }

    private static int currentLyricsIndex(String[] lines) {
        if (lines == null || lines.length <= 1) {
            return 0;
        }
        LyricLine[] synced = syncedLyricsLines;
        if (synced != null && synced.length > 0) {
            long position = currentPositionForLyrics();
            int index = 0;
            for (int i = 0; i < synced.length; i++) {
                if (synced[i].timeMs() > position) {
                    break;
                }
                index = i;
            }
            return Math.max(0, Math.min(index, lines.length - 1));
        }
        return 0;
    }

    private static long currentPositionForLyrics() {
        return Math.max(0L, currentTime);
    }

    private static String lyricAt(String[] lines, int index) {
        return lines != null && index >= 0 && index < lines.length ? lines[index] : "";
    }

    private static boolean hasRenderableLyrics() {
        LyricLine[] synced = syncedLyricsLines;
        if (synced != null && synced.length > 0) {
            return true;
        }
        return false;
    }

    private static String sanitizeToken(String value) {
        if (value == null) {
            return "";
        }
        return value.replaceAll("\\s*\\(feat\\.[^)]+\\)", "")
                .replaceAll("\\s*\\[feat\\.[^]]+\\]", "")
                .replaceAll("\\s*-\\s*radio edit", "")
                .trim();
    }

    private void drawNewControls(EventRender.Screen.Hud event, float x, float y, float rectWidth, float rectHeight,
                                 float alphaProgress, String currentTimeText, String totalTimeText) {
        float timeSize = 16f;
        int timeColor = ClientColors.applyAlpha(ClientColors.TEXT.getRGB(), alphaProgress);
        float timeBaseline = y + rectHeight - 25f + FontRegistry.centeredBaselineOffset(FontRegistry.DELTA_HUD, '0', timeSize);

        event.getRenderer().text(FontRegistry.DELTA_HUD, x + 10f, timeBaseline, timeSize, currentTimeText, timeColor);

        float totalWidth = FontRegistry.DELTA_HUD.getWidth(totalTimeText, timeSize);
        event.getRenderer().text(FontRegistry.DELTA_HUD, x + rectWidth - 10f - totalWidth, timeBaseline, timeSize, totalTimeText, timeColor);

        float controlsCenterX = x + rectWidth * 0.5f;
        float iconBaseline = y + rectHeight - 25f + FontRegistry.centeredBaselineOffset(FontRegistry.ICONS_ASYNC, 'H', NEW_CONTROL_ICON_SIZE);

        drawControlIcon(event, PREVIOUS_ICON, getControlCenterX(x, rectWidth, 0), iconBaseline, alphaProgress);

        float targetProgress = isPlaying ? 1f : 0f;
        playPauseIconProgress += (targetProgress - playPauseIconProgress) * 0.14f;
        if (Math.abs(playPauseIconProgress - targetProgress) < 0.01f) {
            playPauseIconProgress = targetProgress;
        }

        float playAlpha = (1f - playPauseIconProgress) * alphaProgress;
        float pauseAlpha = playPauseIconProgress * alphaProgress;
        float playSize = NEW_CONTROL_ICON_SIZE * (0.82f + (1f - playPauseIconProgress) * 0.18f);
        float pauseSize = NEW_CONTROL_ICON_SIZE * (0.82f + playPauseIconProgress * 0.18f);
        float playCenterX = getControlCenterX(x, rectWidth, 1);
        float playWidth = FontRegistry.ICONS_ASYNC.getWidth("v", playSize);
        float pauseWidth = FontRegistry.ICONS_ASYNC.getWidth(PAUSE_ICON, pauseSize);
        float playBaseline = y + rectHeight - 25f + FontRegistry.centeredBaselineOffset(FontRegistry.ICONS_ASYNC, 'H', playSize);
        float pauseBaseline = y + rectHeight - 25f + FontRegistry.centeredBaselineOffset(FontRegistry.ICONS_ASYNC, 'H', pauseSize);
        if (playAlpha > 0.01f) {
            event.getRenderer().text(FontRegistry.ICONS_ASYNC, playCenterX - playWidth * 0.5f, playBaseline, playSize, "v", ClientColors.applyAlpha(ClientColors.TEXT.getRGB(), playAlpha));
        }
        if (pauseAlpha > 0.01f) {
            event.getRenderer().text(FontRegistry.ICONS_ASYNC, playCenterX - pauseWidth * 0.5f, pauseBaseline, pauseSize, PAUSE_ICON, ClientColors.applyAlpha(ClientColors.TEXT.getRGB(), pauseAlpha));
        }

        drawControlIcon(event, NEXT_ICON, getControlCenterX(x, rectWidth, 2), iconBaseline, alphaProgress);
    }

    private void drawControlIcon(EventRender.Screen.Hud event, String icon, float centerX, float baselineY, float alphaProgress) {
        float iconWidth = FontRegistry.ICONS_ASYNC.getWidth(icon, NEW_CONTROL_ICON_SIZE);
        int iconColor = ClientColors.applyAlpha(ClientColors.TEXT.getRGB(), alphaProgress);
        event.getRenderer().text(FontRegistry.ICONS_ASYNC, centerX - iconWidth * 0.5f, baselineY, NEW_CONTROL_ICON_SIZE, icon, iconColor);
    }

    private void drawMarqueeText(Renderer2D render,
                                 FontObject font,
                                 float x, float y, float clipWidth, float size,
                                 String text, int color) {
        if (text == null || text.isEmpty() || clipWidth <= 0f) {
            return;
        }

        float textWidth = font.getWidth(text, size);
        if (textWidth <= clipWidth) {
            render.text(font, x, y, size, text, color);
            return;
        }

        float overflow = textWidth - clipWidth;
        float offset = getMarqueeOffset(overflow);
        float lineHeight = font.getLineHeight(size);
        float clipY = y - lineHeight;
        float clipHeight = lineHeight * 1.9f;

        render.pushClipRect((int) Math.floor(x), (int) Math.floor(clipY),
                (int) Math.ceil(clipWidth), (int) Math.ceil(clipHeight));
        try {
            render.text(font, x - offset, y, size, text, color);

            if (offset > 0.5f) {
                render.text(font, x - offset + textWidth + MARQUEE_GAP, y, size, text, color);
            }
        } finally {
            render.popClipRect();
        }
    }

    private float getMarqueeOffset(float overflow) {
        float travel = overflow + MARQUEE_GAP;
        if (travel <= 0f) {
            return 0f;
        }

        long now = System.currentTimeMillis();
        long animationMs = Math.max(1L, (long) ((travel / MARQUEE_SPEED_PX_PER_SEC) * 1000f));
        long cycle = MARQUEE_PAUSE_MS + animationMs + MARQUEE_PAUSE_MS + animationMs;
        long time = now % cycle;

        if (time < MARQUEE_PAUSE_MS) {
            return 0f;
        }
        time -= MARQUEE_PAUSE_MS;

        if (time < animationMs) {
            return easeInOut((float) time / (float) animationMs) * travel;
        }
        time -= animationMs;

        if (time < MARQUEE_PAUSE_MS) {
            return travel;
        }
        time -= MARQUEE_PAUSE_MS;

        return (1f - easeInOut((float) time / (float) animationMs)) * travel;
    }

    private static float easeInOut(float value) {
        float clamped = MathUtil.clamp(value, 0f, 1f);
        return clamped * clamped * (3f - 2f * clamped);
    }

    private void drawNewProgress(EventRender.Screen.Hud event, float x, float y, float rectWidth, float rectHeight,
                                 float progressValue, int accentColor, int progressBgColor) {
        float progressBarX = x + 10f;
        float progressBarY = y + rectHeight - 11f;
        float progressBarWidth = rectWidth - 20f;
        float progressWidth = progressBarWidth * MathUtil.clamp(progressValue, 0f, 1f);

        event.getRenderer().rect(progressBarX, progressBarY, progressBarWidth, NEW_PROGRESS_BAR_HEIGHT, 2f, progressBgColor);
        event.getRenderer().rect(progressBarX, progressBarY, progressWidth, NEW_PROGRESS_BAR_HEIGHT, 2f, accentColor);
    }

    private static float getControlCenterX(float x, float rectWidth, int index) {
        float centerX = x + rectWidth * 0.5f;
        if (index == 0) {
            return centerX - 24f;
        }
        if (index == 2) {
            return centerX + 24f;
        }
        return centerX;
    }

    private static float getControlCenterY(float y, float rectHeight) {
        return y + rectHeight - 27f;
    }

    private static boolean isInsideControl(float mouseX, float mouseY, float x, float y, float rectWidth, float rectHeight, int index) {
        float centerX = getControlCenterX(x, rectWidth, index);
        float centerY = getControlCenterY(y, rectHeight);
        return MathUtil.isHovered(mouseX, mouseY,
                centerX - NEW_CONTROL_HITBOX * 0.5f,
                centerY - NEW_CONTROL_HITBOX * 0.5f,
                NEW_CONTROL_HITBOX,
                NEW_CONTROL_HITBOX);
    }

    private static boolean isInsideProgressBar(float mouseX, float mouseY, float x, float y, float rectWidth, float rectHeight) {
        return MathUtil.isHovered(mouseX, mouseY,
                x + 10f,
                y + rectHeight - 14f,
                rectWidth - 20f,
                NEW_PROGRESS_BAR_HEIGHT + 6f);
    }

    private static float getNewRectHeight() {
        return DraggingManager.getHudBoolean(SETTINGS_SCOPE, SETTING_SHOW_TEXT, false) && hasRenderableLyrics() ? NEW_TEXT_RECT_HEIGHT : NEW_RECT_HEIGHT;
    }

    private static void executeMediaCommand(Callable<Boolean> command) {
        commandExecutor.execute(() -> {
            try {
                command.call();
            } catch (Exception ignored) {
            }
        });
    }

    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    protected String truncateText(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength) + "...";
    }

    private static String formatTime(long millis) {
        long totalSeconds = millis / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    private record LyricLine(long timeMs, String text) {
    }

    private record LyricsResult(String[] lines, LyricLine[] synced) {
    }
}
