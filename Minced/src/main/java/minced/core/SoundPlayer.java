/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  sLM.4ZXYEo
 */
package minced.core;

import java.io.BufferedInputStream;
import java.util.Objects;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import minced.core.Minced;
import minced.module.misc.ToggleSounds;
import net.minecraft.client.MinecraftClient;
import sLM._4ZXYEo;

public class SoundPlayer {
    static String cfr_renamed_162;

    static MinecraftClient I = MinecraftClient.getInstance();
    static float field8072;
    static double field8073;

    public static void method2069(String param0) {
        if (Minced.getInstance().getModuleManager().method187(ToggleSounds.class).isEnabled() && SoundPlayer.I.player.getWorld() != null) {
            try {
                AudioInputStream v1 = AudioSystem.getAudioInputStream(new BufferedInputStream(Objects.requireNonNull(SoundPlayer.class.getResourceAsStream("/assets/minced/sounds/" + param0 + ".wav"))));
                AudioFormat v2 = v1.getFormat();
                AudioFormat v3 = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, v2.getSampleRate(), 16, v2.getChannels(), v2.getChannels() * 2, v2.getSampleRate(), false);
                AudioInputStream v4 = AudioSystem.getAudioInputStream(v3, v1);
                Clip v5 = AudioSystem.getClip();
                v5.open(v4);
                SoundPlayer.method2070(v5, ((Float)ToggleSounds.field4050.getValue()).floatValue() / field8072);
                v5.start();
                if (v4 != null) {
                    v4.close();
                }
                if (v1 != null) {
                    v1.close();
                }
            }
            catch (UnsupportedAudioFileException | LineUnavailableException | java.io.IOException v6) {
                // Playback is best-effort: a missing or unplayable sound must not break module toggling.
            }
        }
    }

    private static void method2070(Clip param0, double param1) {
        if (param1 < 0.0) {
            param1 = 0.0;
        }
        if (param1 > 1.0) {
            param1 = 1.0;
        }
        FloatControl v3 = (FloatControl)param0.getControl(FloatControl.Type.MASTER_GAIN);
        float v4 = v3.getMinimum();
        float v5 = v3.getMaximum();
        if (param1 == 0.0) {
            v3.setValue(v4);
            return;
        }
        float v6 = (float)(field8073 * Math.log10(param1));
        if (v6 < v4) {
            v6 = v4;
        }
        if (v6 > v5) {
            v6 = v5;
        }
        v3.setValue(v6);
    }

    static {
        cfr_renamed_162 = "/assets/minced/sounds/\u0001.wav";
        field8072 = 100.0f;
        field8073 = 20.0;
    }
}



