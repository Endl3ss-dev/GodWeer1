package net.skycore.client.gui.decide;

import java.io.BufferedInputStream;
import java.io.InputStream;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

/**
 * Port of Decide's {@code ru.decide.utils.other.GuiMusicPlayer}: the ClickGUI keeps a looping
 * background track with a short fade in and fade out.
 *
 * <p>Everything is best-effort: a missing file, an unavailable mixer or a closed line never throws
 * into the render thread — the track simply stays silent, so a broken audio device cannot crash the
 * client while the menu is open.
 */
public final class DecideMusic {
    private static final String RESOURCE = "/assets/skycore/sound/gui/gui_bg.wav";
    private static final int FADE_STEPS = 24;
    private static final long FADE_STEP_MS = 18L;

    private static Clip currentClip;
    private static float targetVolume;
    private static int generation;
    private static boolean unavailable;

    private DecideMusic() {
    }

    public static synchronized void start(float volume) {
        if (unavailable) {
            return;
        }
        if (currentClip != null && currentClip.isRunning()) {
            targetVolume = volume;
            applyVolume(currentClip, volume);
            return;
        }
        final int startGeneration = ++generation;
        targetVolume = volume;
        Thread thread = new Thread(() -> openAndLoop(startGeneration, volume), "Skycore-DecideGuiMusic");
        thread.setDaemon(true);
        thread.start();
    }

    private static void openAndLoop(int startGeneration, float volume) {
        AudioInputStream stream = null;
        try {
            InputStream raw = DecideMusic.class.getResourceAsStream(RESOURCE);
            if (raw == null) {
                unavailable = true;
                return;
            }
            stream = AudioSystem.getAudioInputStream(new BufferedInputStream(raw));
            Clip clip = AudioSystem.getClip();
            clip.open(stream);
            synchronized (DecideMusic.class) {
                if (startGeneration != generation) {
                    clip.close();
                    return;
                }
                currentClip = clip;
            }
            applyVolume(clip, 0.0f);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
            fade(clip, volume, true);
        } catch (Throwable failure) {
            unavailable = true;
        } finally {
            closeQuietly(stream);
        }
    }

    public static synchronized void stop() {
        ++generation;
        Clip clip = currentClip;
        currentClip = null;
        if (clip == null) {
            return;
        }
        Thread thread = new Thread(() -> {
            fade(clip, 0.0f, false);
            try {
                clip.stop();
            } catch (Throwable ignored) {
            }
            try {
                clip.close();
            } catch (Throwable ignored) {
            }
        }, "Skycore-DecideGuiMusic-stop");
        thread.setDaemon(true);
        thread.start();
    }

    public static synchronized void setVolume(float volume) {
        targetVolume = Math.max(0.0f, Math.min(1.0f, volume));
        Clip clip = currentClip;
        if (clip != null && clip.isOpen()) {
            applyVolume(clip, targetVolume);
        }
    }

    private static void fade(Clip clip, float target, boolean clampStart) {
        for (int i = 0; i <= FADE_STEPS; i++) {
            if (clampStart ? !clip.isOpen() : currentClip != null) {
                return;
            }
            float t = (float) i / (float) FADE_STEPS;
            float eased = 1.0f - (1.0f - t) * (1.0f - t);
            applyVolume(clip, clampStart ? eased * target : (1.0f - eased) * targetVolume);
            try {
                Thread.sleep(FADE_STEP_MS);
            } catch (InterruptedException interrupted) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    private static void applyVolume(Clip clip, float volume) {
        if (clip == null || !clip.isOpen()) {
            return;
        }
        try {
            if (!clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                return;
            }
            FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float safe = Math.max(1.0E-4f, Math.min(1.0f, volume));
            float db = (float) (20.0 * Math.log10(safe));
            gain.setValue(Math.max(gain.getMinimum(), Math.min(gain.getMaximum(), db)));
        } catch (Throwable ignored) {
        }
    }

    private static void closeQuietly(AudioInputStream stream) {
        if (stream == null) {
            return;
        }
        try {
            stream.close();
        } catch (Throwable ignored) {
        }
    }
}
