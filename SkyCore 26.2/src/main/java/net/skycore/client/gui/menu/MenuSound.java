package net.skycore.client.gui.menu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance.Attenuation;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

/**
 * Звуки меню ClickGUI.
 *
 * <p>Порт {@code swerve.ui.menu.MenuSound} (Swerve, порт Arcanic Visuals, GPL-3.0):
 * те же семнадцать событий, те же паузы между повторами и та же зависимость от
 * громкости меню.
 *
 * <p>Отличие от источника: там звук читался из classpath файлом через
 * {@code javax.sound.sampled.Clip} в отдельном потоке. Здесь события зарегистрированы
 * в {@code assets/skycore/sounds.json} и проигрываются штатным звуковым движком игры
 * ({@code SimpleSoundInstance}), как это уже делает {@code PlayModeHelper}. Так звук
 * меню подчиняется ползунку громкости Minecraft и не открывает собственный аудиопоток
 * на каждый клик.
 */
public final class MenuSound {

    public static final int CLICK = 0;
    public static final int HOVER = 1;
    public static final int TICK = 2;
    public static final int TYPE = 3;
    public static final int POP = 4;
    public static final int RESET = 5;
    public static final int SELECT = 6;
    public static final int CHARGE = 7;
    public static final int DRAIN = 8;
    public static final int GRAB = 9;
    public static final int DROP = 10;
    public static final int OPEN = 11;
    public static final int CLOSE = 12;
    public static final int TOGGLE_ON = 13;
    public static final int TOGGLE_OFF = 14;
    public static final int PAGE = 15;
    public static final int TAB = 16;

    /** Идентификаторы событий в {@code skycore:sounds.json}. */
    private static final Identifier[] EVENTS = {
            Identifier.parse("skycore:ui.click"),
            Identifier.parse("skycore:ui.hover"),
            Identifier.parse("skycore:ui.tick"),
            Identifier.parse("skycore:ui.type"),
            Identifier.parse("skycore:ui.pop"),
            Identifier.parse("skycore:ui.reset"),
            Identifier.parse("skycore:ui.select"),
            Identifier.parse("skycore:ui.charge"),
            Identifier.parse("skycore:ui.drain"),
            Identifier.parse("skycore:ui.grab"),
            Identifier.parse("skycore:ui.drop"),
            Identifier.parse("skycore:ui.open"),
            Identifier.parse("skycore:ui.close"),
            Identifier.parse("skycore:ui.toggle_on"),
            Identifier.parse("skycore:ui.toggle_off"),
            Identifier.parse("skycore:ui.page"),
            Identifier.parse("skycore:ui.tab"),
    };

    /**
     * Момент последнего проигрывания каждого звука.
     *
     * <p>Гонка мыши по строкам не должна превращаться в треск, поэтому повтор одного
     * и того же звука отсекается по времени. Часы — {@code System.nanoTime()}, чтобы
     * перевод системного времени не «замораживал» звук на час.
     */
    private static final long[] LAST_NANOS = new long[EVENTS.length];

    private MenuSound() {
    }

    public static void play(int sound) {
        play(sound, 1.0F, 1.0F);
    }

    public static void play(int sound, float pitch, float volumeScale) {
        if (!MenuPrefs.sounds || MenuPrefs.volume <= 0.0F) {
            return;
        }
        if (sound < 0 || sound >= EVENTS.length) {
            return;
        }
        if (sound == HOVER && !MenuPrefs.hoverSounds) {
            return;
        }
        if (sound == TICK && !MenuPrefs.sliderSounds) {
            return;
        }

        long now = System.nanoTime();
        long gapNanos = gapMillis(sound) * 1_000_000L;
        if (now - LAST_NANOS[sound] < gapNanos) {
            return;
        }
        LAST_NANOS[sound] = now;

        float volume = baseVolume(sound) * volumeScale * (MenuPrefs.volume / 100.0F);
        if (volume <= 0.0F) {
            return;
        }
        playEvent(EVENTS[sound], volume, pitch);
    }

    private static void playEvent(Identifier event, float volume, float pitch) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null || minecraft.getSoundManager() == null) {
            return;
        }
        float clampedVolume = Math.max(0.0F, Math.min(1.0F, volume));
        float clampedPitch = Math.max(0.5F, Math.min(2.0F, pitch));
        RandomSource random = SoundInstance.createUnseededRandom();
        SimpleSoundInstance instance = new SimpleSoundInstance(
                event,
                SoundSource.MASTER,
                clampedVolume,
                clampedPitch,
                random,
                false,
                0,
                Attenuation.NONE,
                0.0,
                0.0,
                0.0,
                true);
        minecraft.getSoundManager().play(instance);
    }

    private static long gapMillis(int sound) {
        return switch (sound) {
            case HOVER -> 45L;
            case TICK -> 26L;
            case TYPE -> 18L;
            default -> 35L;
        };
    }

    private static float baseVolume(int sound) {
        return switch (sound) {
            case HOVER -> 0.35F;
            case TICK -> 0.5F;
            case TYPE -> 0.45F;
            case OPEN, CLOSE -> 0.8F;
            default -> 0.7F;
        };
    }
}
