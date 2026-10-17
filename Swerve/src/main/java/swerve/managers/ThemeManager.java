package swerve.managers;

import swerve.ui.menu.MenuTheme;

/**
 * Цвет темы клиента.
 *
 * <p>Источник цвета один для всего клиента: выбранная палитра GUI (меню).
 * HUD и визуальные модули берут акцент и второй цвет градиента отсюда,
 * поэтому интерфейс и визуал всегда в одной теме.
 */
public final class ThemeManager {

    private ThemeManager() {
    }

    /** Акцент темы (основной цвет палитры). */
    public static int getColor() {
        return MenuTheme.accent();
    }

    /** Второй цвет градиента темы. */
    public static int getColor(int index) {
        return index == 0 ? MenuTheme.accent() : MenuTheme.accent2();
    }
}
