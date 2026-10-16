package swerve.ui.menu;

import swerve.utils.client.ClientSoundPlayer;

/**
 * Звуки меню (порт Arcanic Visuals, GPL-3.0): те же .ogg из assets/swerve/sounds/ui.
 *
 * <p>Проигрывание уходит в отдельный поток ClientSoundPlayer, главный поток не блокируется.
 * Повтор одного звука в короткий промежуток отсекается, чтобы гонка мыши не превращалась в треск.
 */
public final class MenuSound {

    private MenuSound() {
    }

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

    private static final String[] FILES = {
            "ui/click.ogg",
            "ui/hover.ogg",
            "ui/tick.ogg",
            "ui/type.ogg",
            "ui/pop.ogg",
            "ui/reset.ogg",
            "ui/select.ogg",
            "ui/charge.ogg",
            "ui/drain.ogg",
            "ui/grab.ogg",
            "ui/drop.ogg",
            "ui/open.ogg",
            "ui/close.ogg",
            "ui/toggle_on.ogg",
            "ui/toggle_off.ogg",
            "ui/page.ogg",
            "ui/tab.ogg",
    };

    private static final long[] LAST = new long[FILES.length];

    public static void play(int sound) {
        play(sound, 1f, 1f);
    }

    public static void play(int sound, float pitch, float volume) {
        if (!MenuPrefs.sounds || MenuPrefs.volume <= 0) {
            return;
        }
        if (sound == HOVER && !MenuPrefs.hoverSounds) {
            return;
        }
        if (sound == TICK && !MenuPrefs.sliderSounds) {
            return;
        }
        long now = System.currentTimeMillis();
        long gap = gap(sound);
        if (now - LAST[sound] < gap) {
            return;
        }
        LAST[sound] = now;
        ClientSoundPlayer.playSound(FILES[sound], volume * base(sound) * (MenuPrefs.volume / 100f), pitch);
    }

    private static long gap(int sound) {
        return switch (sound) {
            case HOVER -> 45;
            case TICK -> 26;
            case TYPE -> 18;
            default -> 35;
        };
    }

    private static double base(int sound) {
        return switch (sound) {
            case HOVER -> .35;
            case TICK -> .5;
            case TYPE -> .45;
            case OPEN, CLOSE -> .8;
            default -> .7;
        };
    }
}
