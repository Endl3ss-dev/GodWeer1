package godweer.managers;

import godweer.utils.color.ColorUtils;
import godweer.utils.color.Theme;

import java.util.Collections;
import java.util.List;

public final class ThemeManager {

    private static final List<Theme> THEMES = List.of(
            theme("Lavender", 190, 143, 255),
            theme("Blood", 230, 50, 57),
            theme("Ocean", 95, 113, 191),
            theme("Emerald", 60, 220, 140),
            theme("Rose", 255, 120, 190),
            theme("Gold", 252, 192, 88),
            theme("Diamond", 125, 217, 250),
            theme("Sun", 252, 231, 88),
            theme("Mint", 135, 235, 185),
            theme("Sakura", 255, 175, 195),
            theme("Neon", 255, 0, 128),
            theme("Sunset", 255, 105, 95),
            theme("Snow", 245, 245, 255),
            theme("Midnight", 25, 35, 75),
            theme("Coral", 255, 130, 120),
            theme("Teal", 50, 200, 200),
            theme("Amethyst", 150, 95, 215),
            theme("Wine", 140, 30, 60),
            theme("Lime", 160, 220, 60),
            theme("Lavender2", 200, 160, 240),
            theme("Peach", 255, 200, 160),
            theme("Plum", 180, 80, 140),
            theme("Slate", 90, 100, 130),
            theme("Crimson", 200, 40, 50),
            theme("Aqua", 50, 220, 240),
            theme("Cinnamon", 200, 120, 70),
            theme("Forest", 50, 140, 70),
            theme("Honey", 240, 180, 30),
            theme("Ivory", 255, 245, 220),
            theme("Jade", 0, 180, 140),
            theme("Magenta", 220, 50, 180),
            theme("Olive", 160, 160, 50),
            theme("Royal", 50, 70, 180),
            theme("Tomato", 255, 90, 70),
            theme("Violet", 140, 70, 210),
            theme("Bamboo", 120, 180, 90),
            theme("Frost", 180, 220, 250),
            theme("Glow", 255, 240, 100)
    );

    private static final List<Theme> VIEW = Collections.unmodifiableList(THEMES);

    private static Theme current = THEMES.get(0);

    private ThemeManager() {
    }

    private static Theme theme(String name, int r, int g, int b) {
        int base = ColorUtils.rgba(r, g, b, 255);
        return new Theme(name, base, ColorUtils.darken(base, 0.35f));
    }

    public static List<Theme> getThemes() {
        return VIEW;
    }

    public static Theme getCurrent() {
        return current;
    }

    public static void setCurrent(Theme theme) {
        if (theme != null) {
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
