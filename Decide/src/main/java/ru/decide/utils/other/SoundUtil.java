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
import java.util.concurrent.CopyOnWriteArrayList;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public class SoundUtil {
    private static final CopyOnWriteArrayList<Clip> CLIPS_LIST = new CopyOnWriteArrayList();

    public static void playSound_wav(String location, float volume) {
        SoundUtil.playSound_wav(location, volume, 1.0f);
    }

    public static void playSound_wav(String location, float volume, float pitch) {
        CompletableFuture.runAsync(() -> {
            try {
                SoundUtil.cleanUpClips();
                String resourcePath = "/assets/decide/sound/" + location + ".wav";
                InputStream inputStream = SoundUtil.class.getResourceAsStream(resourcePath);
                if (inputStream == null) {
                    return;
                }
                try (AudioInputStream stream = AudioSystem.getAudioInputStream(new BufferedInputStream(inputStream));){
                    Clip clip = AudioSystem.getClip();
                    if (Math.abs(pitch - 1.0f) < 0.001f) {
                        clip.open(stream);
                    } else {
                        SoundUtil.openPitched(clip, stream, pitch);
                    }
                    if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                        FloatControl volumeControl = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
                        float volumeVal = Math.max(0.0f, Math.min(1.0f, volume));
                        float dB = (float)(Math.log10(volumeVal <= 0.0f ? 1.0E-4 : (double)volumeVal) * 20.0);
                        volumeControl.setValue(dB);
                    }
                    clip.addLineListener(event -> {
                        if (event.getType() == LineEvent.Type.STOP) {
                            clip.close();
                            CLIPS_LIST.remove(clip);
                        }
                    });
                    CLIPS_LIST.add(clip);
                    clip.start();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static void openPitched(Clip clip, AudioInputStream stream, float pitch) throws Exception {
        int frameSize;
        float p = Math.max(0.5f, Math.min(2.0f, pitch));
        AudioInputStream pcm = stream;
        AudioFormat src = stream.getFormat();
        if (src.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
            pcm = AudioSystem.getAudioInputStream(AudioFormat.Encoding.PCM_SIGNED, stream);
            src = pcm.getFormat();
        }
        byte[] data = pcm.readAllBytes();
        if (pcm != stream) {
            pcm.close();
        }
        if ((frameSize = src.getFrameSize()) <= 0) {
            clip.open(src, data, 0, data.length);
            return;
        }
        int inFrames = data.length / frameSize;
        int outFrames = (int)((float)inFrames / p);
        if (outFrames <= 0) {
            clip.open(src, data, 0, data.length);
            return;
        }
        byte[] out = new byte[outFrames * frameSize];
        for (int i = 0; i < outFrames; ++i) {
            int srcFrame = Math.min(inFrames - 1, (int)((float)i * p));
            System.arraycopy(data, srcFrame * frameSize, out, i * frameSize, frameSize);
        }
        clip.open(src, out, 0, out.length);
    }

    private static void cleanUpClips() {
        CLIPS_LIST.removeIf(clip -> !clip.isOpen());
    }
}

