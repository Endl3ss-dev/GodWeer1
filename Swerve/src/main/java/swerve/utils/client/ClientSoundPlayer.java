package swerve.utils.client;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class ClientSoundPlayer {

    private static final String SOUNDS_PATH = "/assets/swerve/sounds/";

    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor(runnable -> {
        Thread thread = new Thread(runnable, "swerve-ClientSounds");
        thread.setDaemon(true);
        return thread;
    });

    private ClientSoundPlayer() {
    }

    public static void playSound(String fileName, double volume, float pitch) {
        if (fileName == null || fileName.isBlank()) {
            return;
        }
        EXECUTOR.execute(() -> playInternal(fileName, volume, pitch));
    }

    private static void playInternal(String fileName, double volume, float pitch) {
        try (InputStream inputStream = ClientSoundPlayer.class.getResourceAsStream(SOUNDS_PATH + fileName)) {
            if (inputStream == null) {
                return;
            }

            try (BufferedInputStream bufferedInput = new BufferedInputStream(inputStream);
                 AudioInputStream baseStream = AudioSystem.getAudioInputStream(bufferedInput);
                 AudioInputStream pitchedStream = resampleStream(baseStream, pitch)) {
                Clip clip = AudioSystem.getClip();
                clip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        clip.close();
                    }
                });
                clip.open(pitchedStream);
                setVolume(clip, volume);
                clip.start();
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ignored) {
        }
    }

    private static AudioInputStream resampleStream(AudioInputStream originalStream, float pitch) throws IOException {
        AudioFormat originalFormat = originalStream.getFormat();
        byte[] audioBytes = originalStream.readAllBytes();
        float clampedPitch = Math.max(0.5f, Math.min(2.0f, pitch));
        float newSampleRate = originalFormat.getSampleRate() * clampedPitch;

        AudioFormat newFormat = new AudioFormat(
                newSampleRate,
                originalFormat.getSampleSizeInBits(),
                originalFormat.getChannels(),
                true,
                originalFormat.isBigEndian()
        );

        return new AudioInputStream(
                new ByteArrayInputStream(audioBytes),
                newFormat,
                audioBytes.length / newFormat.getFrameSize()
        );
    }

    private static void setVolume(Clip clip, double volume) {
        if (!clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            return;
        }

        double clampedVolume = Math.max(0.0, Math.min(1.0, volume));
        FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        float decibels = (float) (Math.log10(clampedVolume <= 0.0 ? 0.0001 : clampedVolume) * 20.0);
        volumeControl.setValue(Math.max(volumeControl.getMinimum(), Math.min(volumeControl.getMaximum(), decibels)));
    }
}
