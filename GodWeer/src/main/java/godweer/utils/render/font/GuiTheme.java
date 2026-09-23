package godweer.utils.render.font;

import godweer.managers.ThemeManager;
import godweer.utils.color.ColorUtils;

public final class GuiTheme {

    private static final GuiTheme INSTANCE = new GuiTheme();

    private GuiTheme() {
    }

    public static GuiTheme getInstance() {
        return INSTANCE;
    }

    public int clientColor() {
        return ThemeManager.getColor();
    }

    public int textColor() {
        return ColorUtils.rgba(255, 255, 255, 255);
    }

    public int disabledColor() {
        return ColorUtils.rgba(180, 180, 180, 255);
    }

    public int enabledColor() {
        return ColorUtils.rgba(255, 255, 255, 255);
    }

    public int secondaryColor() {
        return ColorUtils.rgba(30, 30, 35, 200);
    }

    public int blurStrength() {
        return 10;
    }

    public int liquidGlassStrength() {
        return 50;
    }
}
