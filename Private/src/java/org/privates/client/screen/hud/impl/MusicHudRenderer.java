package org.privates.client.screen.hud.impl;

import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.util.ResourceLocation;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.managers.events.input.MousePressEvent;
import org.privates.client.managers.events.render.Render2DEvent;
import org.privates.client.managers.module.settings.impl.DragSetting;
import org.privates.client.screen.hud.AbstractHud;
import org.privates.client.screen.hud.IMousePress;
import org.privates.client.screen.hud.IRenderer;
import org.privates.client.utils.animation.Animation;
import org.privates.client.utils.animation.util.Easings;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.draw.ScissorUtil;
import org.privates.client.utils.render.font.Fonts;
import org.privates.client.utils.render.font.StripFont;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class MusicHudRenderer extends AbstractHud implements IRenderer, IMousePress {
    private final DragSetting drag;
    private final StripFont stripFont = new StripFont();
    private final Animation pauseOverlay = new Animation();
    private final MediaBridge mediaBridge = new MediaBridge();
    private final AtomicBoolean updating = new AtomicBoolean(false);

    private String currentTrackTitle = "";
    private String artist = "";
    private long position;
    private long duration;
    private boolean paused;
    private boolean sessionEmpty = true;
    private long lastUpdate = 0L;
    private long lastSnapshotTime = 0L;
    private float smoothProgress = 0f;
    private float smoothPosition = 0f;

    private byte[] artworkBytes;
    private byte[] lastArtworkBytes;
    private ResourceLocation artworkTexture;

    public MusicHudRenderer(DragSetting drag) {
        this.drag = drag;
    }

    @Override
    public void render(Render2DEvent event) {
        MatrixStack matrix = event.getMatrix();
        float x = drag.position.x;
        float y = drag.position.y;
        float width = 95f;
        float height = 45f;

        if (x == 0f && y == 0f) {
            drag.position.set(8f, mc.getMainWindow().getScaledHeight() - height - 8f);
            x = drag.position.x;
            y = drag.position.y;
        }

        if (System.currentTimeMillis() - lastUpdate >= 1000L) {
            lastUpdate = System.currentTimeMillis();
            requestUpdate();
        }

        pauseOverlay.update();
        pauseOverlay.run(paused ? 1f : 0f, 0.2f, Easings.SINE_OUT, true);

        float round = 5f;
        RenderUtil.CustomStyled.draw(matrix, x, y, width, height, Round.of(round), true, theme().blurStrength().getValue().floatValue());
        RenderUtil.Rounded.smooth(matrix, x, y, width, height, ColorUtil.getColor(0, 0, 0, 35), Round.of(round));

        if (!sessionEmpty) {
            renderArtwork(x, y, event);
        }

        if (sessionEmpty) {
            Fonts.SF_SEMIBOLD.drawCenter(matrix, "Ожидаю...", x + width / 2f, y + height / 2f - 3.5f, ColorUtil.getColor(220, 220, 220), 7f);
            drag.size.set(width, height);
            return;
        }

        String title = currentTrackTitle == null || currentTrackTitle.isEmpty() ? "Нет данных" : currentTrackTitle;
        String artistText = artist == null ? "" : artist;
        float textX = x + 35f;
        float titleMaxW = width - 40f;

        stripFont.draw(Fonts.SF_BOLD, matrix, title, textX, y + 5f, titleMaxW, ColorUtil.getColor(255, 255, 255), 6.5f, 4.5f);
        if (!artistText.isEmpty()) {
            stripFont.draw(Fonts.SF_SEMIBOLD, matrix, artistText, textX, y + 14f, titleMaxW, ColorUtil.getColor(177, 177, 177), 5f, 4.5f);
        }

        float livePosition = getLivePositionSeconds();
        float liveDuration = Math.max(0f, (float) duration);
        float targetProgress = getProgress(livePosition, liveDuration);
        smoothProgress = Mathf.fast(smoothProgress, targetProgress, 16f);
        smoothPosition = Mathf.fast(smoothPosition, livePosition, 16f);

        float progressX = x + 35f;
        float progressY = y + height - 15f;
        float progressW = width - 40f;
        RenderUtil.Rounded.smooth(matrix, progressX, progressY, progressW, 3f, ColorUtil.getColor(45, 45, 45), Round.of(2f));
        RenderUtil.Rounded.smooth(matrix, progressX, progressY, progressW * smoothProgress, 3f, theme().clientColor(), Round.of(2f));

        String posText = formatDurationSmooth(smoothPosition);
        String durText = formatDurationSmooth(liveDuration);
        Fonts.SF_SEMIBOLD.draw(matrix, posText, textX, y + 23f, ColorUtil.getColor(177, 177, 177), 5f);
        Fonts.SF_SEMIBOLD.draw(matrix, durText, x + width - 5f - Fonts.SF_SEMIBOLD.getWidth(durText, 5f), y + 23f, ColorUtil.getColor(177, 177, 177), 5f);

        float controlsY = y + height - 8f;
        drawControlButton(matrix, x + width / 2f - 12f, controlsY - 3f, 6f, 6f, "<<", ColorUtil.getColor(220, 220, 220));
        drawControlButton(matrix, x + width / 2f - 3f, controlsY - 3f, 6f, 6f, paused ? ">" : "||", ColorUtil.getColor(255, 255, 255));
        drawControlButton(matrix, x + width / 2f + 6f, controlsY - 3f, 6f, 6f, ">>", ColorUtil.getColor(220, 220, 220));

        if (pauseOverlay.get() > 0.01f) {
            int overlay = ColorUtil.multAlpha(ColorUtil.getColor(0, 0, 0, 140), pauseOverlay.get());
            RenderUtil.Rounded.smooth(matrix, x, y, width, height, overlay, Round.of(round));
            RenderUtil.Rounded.smooth(matrix, x + width / 2f - 10f, y + height / 2f - 10f, 20f, 20f, ColorUtil.multAlpha(ColorUtil.getColor(0, 0, 0, 200), pauseOverlay.get()), Round.of(10f));
            Fonts.SF_BOLD.drawCenter(matrix, ">", x + width / 2f, y + height / 2f - 3f, ColorUtil.multAlpha(ColorUtil.getColor(255, 255, 255), pauseOverlay.get()), 10f);
        }

        drag.size.set(width, height);
    }

    private void drawControlButton(MatrixStack matrix, float x, float y, float w, float h, String text, int color) {
        RenderUtil.Rounded.smooth(matrix, x - 1f, y - 1f, w + 2f, h + 2f, ColorUtil.getColor(0, 0, 0, 50), Round.of(2f));
        Fonts.SF_BOLD.drawCenter(matrix, text, x + w / 2f, y + 0.4f, color, 4.8f);
    }

    private void renderArtwork(float x, float y, Render2DEvent eventDisplay) {
        float artworkSize = 27f;
        float artworkX = x + 5f;
        float artworkY = y + 5f;

        ScissorUtil.enableScissor(artworkX, artworkY, artworkSize, artworkSize);

        if (artworkBytes != null && artworkBytes.length > 0) {
            if (lastArtworkBytes == null || !Arrays.equals(artworkBytes, lastArtworkBytes)) {
                try {
                    if (artworkTexture != null) {
                        mc.getTextureManager().deleteTexture(artworkTexture);
                    }
                    NativeImage nativeImage = NativeImage.read(new ByteArrayInputStream(artworkBytes));
                    DynamicTexture dynamicTexture = new DynamicTexture(nativeImage);
                    dynamicTexture.setBlurMipmap(false, false);
                    artworkTexture = mc.getTextureManager().getDynamicTextureLocation("music_hud_artwork", dynamicTexture);
                    lastArtworkBytes = artworkBytes.clone();
                } catch (Exception ignored) {
                    artworkTexture = null;
                }
            }

            if (artworkTexture != null) {
                RenderUtil.drawImage(eventDisplay.getMatrix(), artworkTexture, artworkX, artworkY, artworkSize, artworkSize, true, ColorUtil.getColor(255, 255, 255));
                RenderUtil.Rounded.smooth(eventDisplay.getMatrix(), artworkX, artworkY, artworkSize, artworkSize, ColorUtil.getColor(0, 0, 0, 15), Round.of(4f));
            }
        } else if (artworkTexture != null) {
            mc.getTextureManager().deleteTexture(artworkTexture);
            artworkTexture = null;
            lastArtworkBytes = null;
        }

        ScissorUtil.disableScissor();
    }

    private void requestUpdate() {
        if (!updating.compareAndSet(false, true)) {
            return;
        }
        Thread thread = new Thread(() -> {
            try {
                MediaSnapshot snapshot = mediaBridge.read();
                if (snapshot == null || snapshot.empty) {
                    sessionEmpty = true;
                    currentTrackTitle = "";
                    artist = "";
                    position = 0L;
                    duration = 0L;
                    artworkBytes = null;
                    paused = false;
                    return;
                }

                sessionEmpty = false;
                currentTrackTitle = snapshot.title;
                artist = snapshot.artist;
                position = snapshot.position;
                duration = snapshot.duration;
                artworkBytes = snapshot.artwork;
                paused = snapshot.paused;
                lastSnapshotTime = System.currentTimeMillis();
            } finally {
                updating.set(false);
            }
        }, "music-hud-updater");
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void mousePress(MousePressEvent event) {
        if (sessionEmpty || event.getKey() != 0) {
            return;
        }

        float x = drag.position.x;
        float y = drag.position.y;
        float width = 95f;
        float height = 45f;
        float mouseX = (float) event.getMouseX();
        float mouseY = (float) event.getMouseY();

        if (isHovered(mouseX, mouseY, x + width / 2f - 12f, y + height - 11f, 8f, 8f)) {
            mediaBridge.previous();
            return;
        }
        if (isHovered(mouseX, mouseY, x + width / 2f - 3f, y + height - 11f, 8f, 8f)) {
            if (paused) {
                mediaBridge.play();
                paused = false;
            } else {
                mediaBridge.playPause();
                paused = true;
            }
            return;
        }
        if (isHovered(mouseX, mouseY, x + width / 2f + 6f, y + height - 11f, 8f, 8f)) {
            mediaBridge.next();
            return;
        }
        if (paused && isHovered(mouseX, mouseY, x + width / 2f - 10f, y + height / 2f - 10f, 20f, 20f)) {
            mediaBridge.play();
            paused = false;
        }
    }

    private boolean isHovered(float mouseX, float mouseY, float x, float y, float width, float height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    private String formatDuration(long sec) {
        if (sec <= 0L) {
            return "00:00";
        }
        long hours = sec / 3600L;
        long remaining = sec % 3600L;
        long minutes = remaining / 60L;
        long seconds = remaining % 60L;
        if (hours > 0L) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        }
        return String.format("%02d:%02d", minutes, seconds);
    }

    private float getProgress(float pos, float dur) {
        return dur > 0f ? Math.max(0f, Math.min(1f, pos / dur)) : 0f;
    }

    private float getLivePositionSeconds() {
        if (sessionEmpty) {
            return 0f;
        }
        float base = Math.max(0f, (float) position);
        if (!paused && lastSnapshotTime > 0L) {
            long now = System.currentTimeMillis();
            float delta = Math.max(0f, (now - lastSnapshotTime) / 1000f);
            base += delta;
        }
        if (duration > 0L) {
            base = Math.min(base, (float) duration);
        }
        return base;
    }

    private String formatDurationSmooth(float secFloat) {
        float clamped = Math.max(0f, secFloat);
        long totalSeconds = (long) clamped;
        int tenth = Math.min(9, (int) ((clamped - totalSeconds) * 10f));
        long hours = totalSeconds / 3600L;
        long remaining = totalSeconds % 3600L;
        long minutes = remaining / 60L;
        long seconds = remaining % 60L;
        if (hours > 0L) {
            return String.format("%d:%02d:%02d.%d", hours, minutes, seconds, tenth);
        }
        return String.format("%02d:%02d.%d", minutes, seconds, tenth);
    }

    private record MediaSnapshot(boolean empty, String title, String artist, long position, long duration,
                                 byte[] artwork, String owner, boolean paused) {
        private MediaSnapshot(boolean empty, String title, String artist, long position, long duration, byte[] artwork, String owner, boolean paused) {
            this.empty = empty;
            this.title = title == null ? "" : title;
            this.artist = artist == null ? "" : artist;
            this.position = Math.max(0L, position);
            this.duration = Math.max(0L, duration);
            this.artwork = artwork;
            this.owner = owner == null ? "" : owner;
            this.paused = paused;
        }
    }

    private static final class MediaBridge {
        private long nextInitRetryTime;
        private boolean available;
        private Object instance;
        private Method getMediaSessions;
        private String lastInitError = "";
        private ClassLoader mediaClassLoader;

        private Object activeSession;
        private Method playPause;
        private Method next;
        private Method previous;
        private Method play;

        private MediaBridge() {
        }

        private void ensureInit() {
            if (available) {
                return;
            }
            long now = System.currentTimeMillis();
            if (now < nextInitRetryTime) {
                return;
            }
            try {
                tryInitWithLoader(MusicHudRenderer.class.getClassLoader());
                if (!available) {
                    ClassLoader external = buildExternalMediaLoader();
                    if (external != null) {
                        tryInitWithLoader(external);
                    }
                }
            } catch (Throwable t) {
                this.available = false;
                this.instance = null;
                this.getMediaSessions = null;
                this.lastInitError = t.getClass().getSimpleName();
                this.nextInitRetryTime = now + 5000L;
            }
            if (!available) {
                this.nextInitRetryTime = now + 5000L;
            }
        }

        private void tryInitWithLoader(ClassLoader loader) {
            try {
                Class<?> mediaInfoClass = Class.forName("dev.redstones.mediaplayerinfo.MediaPlayerInfo", true, loader);
                Object inst = mediaInfoClass.getField("Instance").get(null);
                Method getter = mediaInfoClass.getMethod("getMediaSessions");
                if (inst != null && getter != null) {
                    this.instance = inst;
                    this.getMediaSessions = getter;
                    this.available = true;
                    this.lastInitError = "";
                    this.mediaClassLoader = loader;
                }
            } catch (Throwable t) {
                this.available = false;
                this.lastInitError = t.getClass().getSimpleName();
            }
        }

        private ClassLoader buildExternalMediaLoader() {
            try {
                List<URL> urls = new ArrayList<>();
                String cp = System.getProperty("java.class.path", "");
                String[] entries = cp.split(File.pathSeparator);
                for (String entry : entries) {
                    String low = entry.toLowerCase();
                    if (low.contains("media-player-info") && low.endsWith(".jar")) {
                        Path p = Paths.get(entry);
                        if (Files.exists(p)) {
                            urls.add(p.toUri().toURL());
                        }
                    }
                }

                Path userHome = Paths.get(System.getProperty("user.home", ""));
                List<Path> scanRoots = Arrays.asList(
                        userHome.resolve(".m2").resolve("repository").resolve("org").resolve("jetbrains").resolve("kotlin"),
                        userHome.resolve(".gradle").resolve("wrapper").resolve("dists")
                );

                addJarIfFound(urls, scanRoots, "kotlin-stdlib-", true);
                addJarIfFound(urls, scanRoots, "kotlin-stdlib-common-", true);
                addJarIfFound(urls, scanRoots, "kotlin-stdlib-jdk7-", true);
                addJarIfFound(urls, scanRoots, "kotlin-stdlib-jdk8-", true);
                addJarIfFound(urls, scanRoots, "kotlinx-serialization-core-jvm-", true);
                addJarIfFound(urls, scanRoots, "kotlinx-serialization-json-jvm-", true);

                if (urls.isEmpty()) {
                    return null;
                }
                return new URLClassLoader(urls.toArray(new URL[0]), ClassLoader.getPlatformClassLoader());
            } catch (Throwable ignored) {
                return null;
            }
        }

        private void addJarIfFound(List<URL> urls, List<Path> roots, String prefix, boolean skipSourcesAndDocs) {
            for (Path root : roots) {
                if (!Files.exists(root)) {
                    continue;
                }
                try {
                    Path best = Files.walk(root, 8)
                            .filter(p -> Files.isRegularFile(p))
                            .filter(p -> p.getFileName().toString().startsWith(prefix))
                            .filter(p -> p.getFileName().toString().endsWith(".jar"))
                            .filter(p -> !skipSourcesAndDocs || (!p.getFileName().toString().contains("-sources") && !p.getFileName().toString().contains("-javadoc")))
                            .sorted((a, b) -> b.getFileName().toString().compareToIgnoreCase(a.getFileName().toString()))
                            .findFirst()
                            .orElse(null);
                    if (best != null) {
                        URL url = best.toUri().toURL();
                        if (!urls.contains(url)) {
                            urls.add(url);
                        }
                    }
                } catch (Throwable ignored) {
                }
            }
        }

        private MediaSnapshot read() {
            ensureInit();
            if (!available) {
                return new MediaSnapshot(true, "", "", 0L, 0L, null, "", false);
            }
            try {
                Object listObj = getMediaSessions.invoke(instance);
                if (!(listObj instanceof List<?> sessions)) {
                    return new MediaSnapshot(true, "", "", 0L, 0L, null, "", false);
                }
                if (sessions.isEmpty()) {
                    activeSession = null;
                    return new MediaSnapshot(true, "", "", 0L, 0L, null, "", false);
                }

                Object session = sessions.get(0);
                activeSession = session;

                Class<?> sessionClass = session.getClass();
                Object media = sessionClass.getMethod("getMedia").invoke(session);
                String owner = toOwnerTag((String) sessionClass.getMethod("getOwner").invoke(session));
                boolean paused = false;
                try {
                    paused = (Boolean) sessionClass.getMethod("isPaused").invoke(session);
                } catch (Throwable ignored) {
                }

                cacheActions(sessionClass);

                if (media == null) {
                    return new MediaSnapshot(false, "Нет данных", "", 0L, 0L, null, owner, paused);
                }

                Class<?> mediaClass = media.getClass();
                String title = asString(mediaClass.getMethod("getTitle").invoke(media), "Нет данных");
                String artist = asString(mediaClass.getMethod("getArtist").invoke(media), "");
                long position = normalizeTimeValue(asLong(mediaClass.getMethod("getPosition").invoke(media)));
                long duration = normalizeTimeValue(asLong(mediaClass.getMethod("getDuration").invoke(media)));
                byte[] artwork = null;
                try {
                    artwork = (byte[]) mediaClass.getMethod("getArtworkPng").invoke(media);
                } catch (Throwable ignored) {
                }
                return new MediaSnapshot(false, title, artist, position, duration, artwork, owner, paused);
            } catch (Throwable ignored) {
                activeSession = null;
                return new MediaSnapshot(true, "", "", 0L, 0L, null, "", false);
            }
        }

        private void cacheActions(Class<?> sessionClass) {
            try {
                playPause = sessionClass.getMethod("playPause");
            } catch (Throwable ignored) {
                playPause = null;
            }
            try {
                next = sessionClass.getMethod("next");
            } catch (Throwable ignored) {
                next = null;
            }
            try {
                previous = sessionClass.getMethod("previous");
            } catch (Throwable ignored) {
                previous = null;
            }
            try {
                play = sessionClass.getMethod("play");
            } catch (Throwable ignored) {
                play = null;
            }
        }

        private void playPause() {
            invoke(playPause);
        }

        private void next() {
            invoke(next);
        }

        private void previous() {
            invoke(previous);
        }

        private void play() {
            invoke(play);
        }

        private void invoke(Method method) {
            ensureInit();
            if (activeSession == null || method == null) {
                return;
            }
            try {
                method.invoke(activeSession);
            } catch (Throwable ignored) {
            }
        }

        private static String asString(Object obj, String def) {
            return obj == null ? def : String.valueOf(obj);
        }

        private static long asLong(Object obj) {
            if (obj instanceof Number) {
                return ((Number) obj).longValue();
            }
            return 0L;
        }

        private static long normalizeTimeValue(long value) {
            if (value <= 0L) {
                return 0L;
            }
            if (value > 10_000L) {
                return value / 1000L;
            }
            return value;
        }

        private static String toOwnerTag(String owner) {
            if (owner == null) {
                return "";
            }
            String lower = owner.toLowerCase();
            if (lower.contains("spotify")) return "SPOTIFY";
            if (lower.contains("yandex")) return "YANDEX";
            if (lower.contains("telegram") || lower.contains("ayugram")) return "TELEGRAM";
            if (lower.contains("vk") || lower.contains("vkontakte")) return "VK";
            if (lower.contains("edge") || lower.contains("chrome") || lower.contains("opera") || lower.contains("firefox"))
                return "BROWSER";
            return owner.length() > 10 ? owner.substring(0, 10).toUpperCase() : owner.toUpperCase();
        }
    }
}
