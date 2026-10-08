package never.lose.managers;

import never.lose.utils.color.ClientColors;

import java.awt.Color;

public final class ThemeManager {

    private final Theme currentTheme = new Theme("Default", 0xFF3C6EF5, 0xFF0D0D10);

    public Theme getCurrentTheme() {
        return currentTheme;
    }

    public Color getPrimaryColor() {
        return new Color(currentTheme.colorClient, true);
    }

    public void setPrimaryColor(Color color) {
        if (color != null) {
            currentTheme.setAccent(color.getRGB());
        }
    }

    public Color getColor(int index) {
        return new Color(currentTheme.getColorTheme(index), true);
    }

    public static Color interpolateColor(Color first, Color second, float progress) {
        progress = Math.max(0f, Math.min(1f, progress));
        int r = Math.round(first.getRed() + (second.getRed() - first.getRed()) * progress);
        int g = Math.round(first.getGreen() + (second.getGreen() - first.getGreen()) * progress);
        int b = Math.round(first.getBlue() + (second.getBlue() - first.getBlue()) * progress);
        int a = Math.round(first.getAlpha() + (second.getAlpha() - first.getAlpha()) * progress);
        return new Color(r, g, b, a);
    }

    public Color fallback() {
        return ClientColors.MAIN_COLOR;
    }
}
