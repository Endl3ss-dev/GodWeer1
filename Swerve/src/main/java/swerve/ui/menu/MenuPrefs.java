package swerve.ui.menu;

/**
 * Настройки меню (порт MenuPrefs из Arcanic Visuals, GPL-3.0).
 *
 * <p>Хранятся статически, как в оригинале: окно меню — единственное место, где они меняются,
 * а читаются они и анимациями, и звуком.
 */
public final class MenuPrefs {

    private MenuPrefs() {
    }

    // ---- звук
    /** Звуки меню. */
    public static boolean sounds = true;
    /** Громкость звуков меню, 0 - 100. */
    public static float volume = 70f;
    /** Звук при наведении на элемент. */
    public static boolean hoverSounds = true;
    /** Щелчки ползунков. */
    public static boolean sliderSounds = true;

    // ---- вид
    /** Размытие мира под меню. */
    public static boolean blur = true;
    /** Затемнение мира, 0 - 90. */
    public static float dim = 40f;

    // ---- поведение
    /** Плавные переходы. */
    public static boolean animations = true;
    /** Множитель скорости анимаций, 0.5 - 2. */
    public static float animSpeed = 1f;
    /** Размер меню, 70 - 130 (в процентах). */
    public static float scale = 100f;

    /** Масштаб меню в долях: 1.0 - как в оригинале. */
    public static float menuScale() {
        return scale / 100f;
    }
}
