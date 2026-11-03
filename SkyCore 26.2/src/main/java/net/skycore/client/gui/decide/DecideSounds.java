package net.skycore.client.gui.decide;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;

public final class DecideSounds {
    private static final String PREFIX = "/assets/skycore/sound/gui/";
    private static final Map<String, DecodedPcm> PCM_CACHE = new ConcurrentHashMap<>();
    private static final ExecutorService AUDIO_EXEC = Executors.newFixedThreadPool(2, r -> {
        Thread t = new Thread(r, "Skycore-DecideGuiSound");
        t.setDaemon(true);
        return t;
    });
    private static volatile boolean audioUnavailable;
    private static long lastTick;
    private static long lastType;
    /** A line buffer stays allocated until the clip closes: long files are truncated instead. */
    private static final int MAX_CLIP_BYTES = 22050 * 2 * 2 / 2;
    private static final int MAX_PENDING = 24;
    private static final java.util.concurrent.atomic.AtomicInteger PENDING =
            new java.util.concurrent.atomic.AtomicInteger();

    private DecideSounds() {
    }

    private record DecodedPcm(AudioFormat format, byte[] pcmBytes) {
    }

    private static boolean pass(long last, long delay) {
        return System.currentTimeMillis() - last >= delay;
    }

    public static void play(String name, float volume) {
        play(name, volume, 1.0f);
    }

    public static void play(String name, float volume, float pitch) {
        if (audioUnavailable || volume <= 0.005f || name == null || name.isEmpty()) {
            return;
        }
        if (PENDING.get() >= MAX_PENDING) {
            return;
        }
        float clampedVol = Math.max(0.0f, Math.min(1.0f, volume));
        float clampedPitch = Math.max(0.5f, Math.min(2.0f, pitch));
        PENDING.incrementAndGet();
        try {
            AUDIO_EXEC.execute(() -> {
                try {
                    playBlocking(name, clampedVol, clampedPitch);
                } finally {
                    PENDING.decrementAndGet();
                }
            });
        } catch (RuntimeException rejected) {
            PENDING.decrementAndGet();
        }
    }

    private static void playBlocking(String name, float volume, float pitch) {
        try {
            DecodedPcm pcm = PCM_CACHE.computeIfAbsent(name, DecideSounds::loadPcm);
            if (pcm == null || pcm.pcmBytes.length == 0) {
                return;
            }
            byte[] data = Math.abs(pitch - 1.0f) > 0.015f ? resample16Bit(pcm.pcmBytes, pcm.format, pitch) : pcm.pcmBytes;
            if (data.length > MAX_CLIP_BYTES) {
                byte[] trimmed = new byte[MAX_CLIP_BYTES];
                System.arraycopy(data, 0, trimmed, 0, MAX_CLIP_BYTES);
                data = trimmed;
            }
            Clip clip = AudioSystem.getClip();
            clip.addLineListener(ev -> {
                if (ev.getType() == LineEvent.Type.STOP) {
                    try {
                        ev.getLine().close();
                    } catch (RuntimeException ignored) {
                    }
                }
            });
            try {
                clip.open(pcm.format, data, 0, data.length);
            } catch (Throwable failure) {
                try {
                    clip.close();
                } catch (Throwable ignored) {
                }
                throw failure;
            }
            if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                float db = (float) (20.0 * Math.log10(Math.max(1.0E-4, volume)));
                gain.setValue(Math.max(gain.getMinimum(), Math.min(gain.getMaximum(), db)));
            }
            clip.start();
        } catch (Throwable t) {
            if (t instanceof IllegalArgumentException || t instanceof SecurityException) {
                audioUnavailable = true;
            }
        }
    }

    private static DecodedPcm loadPcm(String name) {
        String resourcePath = PREFIX + name + ".wav";
        try (InputStream raw = DecideSounds.class.getResourceAsStream(resourcePath)) {
            if (raw == null) {
                return null;
            }
            byte[] fileBytes = raw.readAllBytes();
            try (AudioInputStream in = AudioSystem.getAudioInputStream(new ByteArrayInputStream(fileBytes))) {
                AudioFormat base = in.getFormat();
                AudioFormat pcmFormat = new AudioFormat(
                        AudioFormat.Encoding.PCM_SIGNED,
                        base.getSampleRate(),
                        16,
                        Math.max(1, base.getChannels()),
                        Math.max(1, base.getChannels()) * 2,
                        base.getSampleRate(),
                        false
                );
                try (AudioInputStream converted = AudioSystem.getAudioInputStream(pcmFormat, in)) {
                    return new DecodedPcm(pcmFormat, converted.readAllBytes());
                }
            }
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static byte[] resample16Bit(byte[] src, AudioFormat format, float pitch) {
        int channels = Math.max(1, format.getChannels());
        int frameBytes = channels * 2;
        int srcFrames = src.length / frameBytes;
        if (srcFrames <= 2) {
            return src;
        }
        int dstFrames = Math.max(1, Math.round(srcFrames / pitch));
        byte[] dst = new byte[dstFrames * frameBytes];
        for (int i = 0; i < dstFrames; i++) {
            float srcPos = i * pitch;
            int idx0 = Math.min(srcFrames - 1, (int) srcPos);
            int idx1 = Math.min(srcFrames - 1, idx0 + 1);
            float frac = srcPos - idx0;
            for (int c = 0; c < channels; c++) {
                int o0 = (idx0 * channels + c) * 2;
                int o1 = (idx1 * channels + c) * 2;
                short s0 = (short) ((src[o0] & 0xFF) | (src[o0 + 1] << 8));
                short s1 = (short) ((src[o1] & 0xFF) | (src[o1 + 1] << 8));
                int mixed = Math.round(s0 + (s1 - s0) * frac);
                mixed = Math.max(Short.MIN_VALUE, Math.min(Short.MAX_VALUE, mixed));
                int outOff = (i * channels + c) * 2;
                dst[outOff] = (byte) (mixed & 0xFF);
                dst[outOff + 1] = (byte) ((mixed >>> 8) & 0xFF);
            }
        }
        return dst;
    }

    public static void open() {
        play("gui_open", 0.45f);
    }

    public static void close() {
        play("gui_close", 0.45f);
    }

    public static void editor() {
        play("gui_anime", 0.4f);
    }

    public static void category(int index, int total) {
        float t = total <= 1 ? 0.0f : (float) index / (float) (total - 1);
        play("gui_category", 0.45f, 0.97f + 0.08f * t);
    }

    public static void theme(int index, int total) {
        float t = total <= 1 ? 0.0f : (float) index / (float) (total - 1);
        play("gui_theme", 0.45f, 0.95f + 0.14f * t);
    }

    public static void expand(boolean opened) {
        play(opened ? "gui_module_open" : "gui_module_close", 0.45f);
    }

    public static void toggle(boolean value) {
        play(value ? "gui_boolean_enable" : "gui_boolean_disable", 0.4f);
    }

    public static void chip(int index, int total) {
        float t = total <= 1 ? 0.0f : (float) index / (float) (total - 1);
        play("gui_mode_multi", 0.45f, 0.95f + 0.15f * t);
    }

    public static void chipMulti(boolean value) {
        play(value ? "gui_boolean2_enable" : "gui_boolean2_disable", 0.4f);
    }

    public static void button() {
        play("gui_open_button", 0.4f);
    }

    public static void sliderGrab() {
        play("gui_click", 0.35f);
    }

    public static void sliderTick(float percent) {
        if (!pass(lastTick, 22L)) {
            return;
        }
        lastTick = System.currentTimeMillis();
        play("gui_slider", 0.35f, 0.9f + 0.35f * DecideDraw.clamp(percent, 0.0f, 1.0f));
    }

    public static void sliderRelease() {
        play("gui_slider", 0.3f, 1.25f);
    }

    public static void bindStart() {
        play("gui_binding", 0.45f);
    }

    public static void bindSet() {
        play("gui_bind", 0.4f);
    }

    public static void bindReset() {
        play("gui_clear", 0.4f);
    }

    public static void editStart() {
        play("gui_click", 0.4f);
    }

    public static void editCommit() {
        play("gui_bind", 0.35f);
    }

    public static void editCancel() {
        play("gui_clear", 0.35f);
    }

    public static void type() {
        if (!pass(lastType, 25L)) {
            return;
        }
        lastType = System.currentTimeMillis();
        float jitter = ThreadLocalRandom.current().nextFloat() * 0.12f - 0.06f;
        play("gui_key_click", 0.35f, 1.0f + jitter);
    }

    public static void erase() {
        if (!pass(lastType, 25L)) {
            return;
        }
        lastType = System.currentTimeMillis();
        play("gui_key_click", 0.3f, 0.88f);
    }

    public static void searchClear() {
        play("gui_clear", 0.4f);
    }

    public static void picker(boolean opened) {
        play(opened ? "gui_multi_open" : "gui_multi_close", 0.4f);
    }

    public static void colorTick(float percent) {
        if (!pass(lastTick, 28L)) {
            return;
        }
        lastTick = System.currentTimeMillis();
        play("gui_slider", 0.3f, 0.9f + 0.35f * DecideDraw.clamp(percent, 0.0f, 1.0f));
    }

    public static void scroll() {
        if (!pass(lastTick, 55L)) {
            return;
        }
        lastTick = System.currentTimeMillis();
        play("gui_scroll", 0.35f);
    }
}
