package swerve.managers;

import swerve.utils.color.ColorUtils;
import swerve.utils.color.Theme;

import java.util.List;

public final class ThemeManager {

    /**
     * Темы клиента.
     *
     * <p>Первые двенадцать — акценты палитр ClickGUI (меню): у них совпадают основной цвет
     * и второй цвет градиента, поэтому HUD и меню читаются как одна тема.
     * Дальше — старые однотонные акценты.
     */
    private static final List<Theme> THEMES = List.of(
            theme("Обсидиан", 0xFF8B5CF6, 0xFFB79CFF),
            theme("Аметист", 0xFFA855F7, 0xFFE9D5FF),
            theme("Полночь", 0xFF3B82F6, 0xFF93C5FD),
            theme("Закат", 0xFFF97316, 0xFFFDBA74),
            theme("Изумруд", 0xFF10B981, 0xFF6EE7B7),
            theme("Светлая", 0xFF7C3AED, 0xFFA78BFA),
            theme("Аврора", 0xFF8B5CF6, 0xFF22D3EE),
            theme("Матрица", 0xFF22C55E, 0xFF86EFAC),
            theme("Синтвейв", 0xFFEC4899, 0xFFA855F7),
            theme("Призма", 0xFFA78BFA, 0xFFF0ABFC),
            theme("Вихрь", 0xFF38BDF8, 0xFF818CF8),
            theme("Неон", 0xFF22D3EE, 0xFFF472B6),
            theme("Lavender", 190, 143, 255),
            theme("Blood", 230, 50, 57),
            theme("Ocean", 95, 113, 191),
            theme("Rose", 255, 120, 190),
            theme("Diamond", 125, 217, 250),
            theme("Sakura", 255, 175, 195),
            theme("Snow", 245, 245, 255),
            theme("Midnight", 25, 35, 75),
            theme("Teal", 50, 200, 200),
            theme("Amethyst", 150, 95, 215),
            theme("Wine", 140, 30, 60),
            theme("Lavender2", 200, 160, 240),
            theme("Slate", 90, 100, 130),
            theme("Crimson", 200, 40, 50),
            theme("Aqua", 50, 220, 240),
            theme("Forest", 50, 140, 70),
            theme("Ivory", 255, 245, 220),
            theme("Jade", 0, 180, 140),
            theme("Royal", 50, 70, 180),
            theme("Violet", 140, 70, 210),
            theme("Frost", 180, 220, 250)
    );

    private static Theme current = THEMES.get(0);
    private static int currentIndex;

    private ThemeManager() {
    }

    /** Однотонная тема: акцент и его затемнённая копия. */
    private static Theme theme(String name, int red, int green, int blue) {
        int base = ColorUtils.rgba(red, green, blue, 255);
        return new Theme(name, base, ColorUtils.darken(base, 0.35f));
    }

    /** Тема из двух цветов: акцент и второй цвет градиента. */
    private static Theme theme(String name, int accent, int accent2) {
        return new Theme(name, accent, accent2);
    }

    public static List<Theme> getThemes() {
        return THEMES;
    }

    public static Theme getCurrent() {
        return current;
    }

    /** Индекс текущей темы — для подсветки выбранного свотча. */
    public static int getCurrentIndex() {
        return currentIndex;
    }

    public static void setCurrent(Theme theme) {
        if (theme != null && THEMES.contains(theme)) {
            current = theme;
            currentIndex = THEMES.indexOf(theme);
        }
    }

    /** Выбор темы по индексу (список тем в меню). */
    public static void setCurrent(int index) {
        if (index >= 0 && index < THEMES.size()) {
            current = THEMES.get(index);
            currentIndex = index;
        }
    }

    public static int getColor() {
        return current.isRainbow() ? ColorUtils.getThemeColor() : current.getColors()[0];
    }

    public static int getColor(int index) {
        return current.getColor(index);
    }
}
