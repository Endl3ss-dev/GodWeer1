package swerve.ui.newgui;

import swerve.managers.ThemeManager;
import swerve.utils.color.ColorUtils;

// Цветовая схема newgui: дефолты перенесены из модуля Theme исходного клиента,
// акцентный цвет берётся из ThemeManager Swerve (переключается из панели Client).
public final class GuiTheme {

    private GuiTheme() {
    }

    public static int clientColor() {
        return ThemeManager.getColor();
    }

    public static int textColor() {
        return ColorUtils.getColor(129, 146, 252, 255);
    }

    public static int enabledColor() {
        // Включённые элементы под цвет темы (иконка и текст активного модуля).
        return clientColor();
    }

    public static int disabledColor() {
        return ColorUtils.getColor(255, 255, 255, 255);
    }

    public static int secondaryColor() {
        return ColorUtils.getColor(45, 60, 85, 255);
    }

    public static float blurStrength() {
        return GuiConfig.blurStrength().get();
    }
}
