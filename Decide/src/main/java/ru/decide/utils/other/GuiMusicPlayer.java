/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.utils.other;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class GuiMusicPlayer {
    private static final String RESOURCE = "/assets/decide/sound/gui/gui_bg.wav";
    private static final int FADE_MS = 500;
    private static final int FADE_STEPS = 60;
    private static volatile Clip currentClip = null;
    private static volatile float targetVolume = 0.0f;
    private static volatile int generation = 0;

    private GuiMusicPlayer() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void start(float volume) {
        Class<GuiMusicPlayer> clazz = GuiMusicPlayer.class;
        synchronized (GuiMusicPlayer.class) {
            if (currentClip != null && currentClip.isRunning()) {
                // ** MonitorExit[var1_1] (shouldn't be in output)
                return;
            }
            // ** MonitorExit[var1_1] (shouldn't be in output)
            int startGeneration = ++generation;
            targetVolume = volume;
            CompletableFuture.runAsync(() -> {
                Clip clip = null;
                AudioInputStream stream = null;
                try {
                    InputStream is = GuiMusicPlayer.class.getResourceAsStream(RESOURCE);
                    if (is == null) {
                        return;
                    }
                    stream = AudioSystem.getAudioInputStream(new BufferedInputStream(is));
                    clip = AudioSystem.getClip();
                    clip.open(stream);
                    Class<GuiMusicPlayer> clazz = GuiMusicPlayer.class;
                    synchronized (GuiMusicPlayer.class) {
                        if (startGeneration != generation) {
                            clip.close();
                            // ** MonitorExit[var5_9] (shouldn't be in output)
                            return;
                        }
                        currentClip = clip;
                        // ** MonitorExit[var5_9] (shouldn't be in output)
                        GuiMusicPlayer.applyVolume(clip, 0.0f);
                        clip.loop(-1);
                        clip.start();
                        GuiMusicPlayer.fadeIn(clip, volume);
                        return;
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                finally {
                    try {
                        if (stream != null) {
                            stream.close();
                        }
                    }
                    catch (Exception exception) {}
                }
            });
            return;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void stop() {
        Class<GuiMusicPlayer> clazz = GuiMusicPlayer.class;
        synchronized (GuiMusicPlayer.class) {
            ++generation;
            Clip clip = currentClip;
            if (clip != null) {
                clip.stop();
                clip.close();
                currentClip = null;
            }
            // ** MonitorExit[var0] (shouldn't be in output)
            return;
        }
    }

    public static void setVolume(float volume) {
        targetVolume = volume;
        Clip clip = currentClip;
        if (clip != null && clip.isOpen()) {
            GuiMusicPlayer.applyVolume(clip, volume);
        }
    }

    private static void fadeIn(Clip clip, float target) throws InterruptedException {
        long stepMs = 8L;
        for (int i = 1; i <= 60 && clip.isOpen(); ++i) {
            float t = (float)i / 60.0f;
            float eased = 1.0f - (1.0f - t) * (1.0f - t);
            GuiMusicPlayer.applyVolume(clip, eased * target);
            Thread.sleep(stepMs);
        }
        if (clip.isOpen()) {
            GuiMusicPlayer.applyVolume(clip, target);
        }
    }

    private static void applyVolume(Clip clip, float volume) {
        if (!clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            return;
        }
        FloatControl gain = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
        float dB = (float)(Math.log10(volume <= 1.0E-4f ? (double)1.0E-4f : (double)volume) * 20.0);
        gain.setValue(Math.max(gain.getMinimum(), Math.min(gain.getMaximum(), dB)));
    }
}

