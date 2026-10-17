package swerve.managers;

import swerve.utils.color.ColorUtils;
import swerve.utils.color.Theme;

import java.util.List;

public final class ThemeManager {

    private static final List<Theme> THEMES = List.of(
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

    private ThemeManager() {
    }

    private static Theme theme(String name, int red, int green, int blue) {
        int base = ColorUtils.rgba(red, green, blue, 255);
        return new Theme(name, base, ColorUtils.darken(base, 0.35f));
    }

    public static List<Theme> getThemes() {
        return THEMES;
    }

    public static Theme getCurrent() {
        return current;
    }

    public static void setCurrent(Theme theme) {
        if (theme != null && THEMES.contains(theme)) {
            current = theme;
        }
    }

    public static int getColor() {
        return current.isRainbow() ? ColorUtils.getThemeColor() : current.getColors()[0];
    }

    public static int getColor(int index) {
        return current.getColor(index);
    }
}
