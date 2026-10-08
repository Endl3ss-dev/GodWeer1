package never.lose.utils.render.providers;

import net.minecraft.util.math.MathHelper;
import never.lose.NeverLose;
import never.lose.managers.Theme;
import never.lose.managers.ThemeManager;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

public final class ColorProvider {

    private static final Map<Integer, Color> COLOR_CACHE = new HashMap<>();

    private ColorProvider() {}

    public static Color asColor(int rgba) {
        return COLOR_CACHE.computeIfAbsent(rgba, key -> new Color(key, true));
    }

    public static int pack(int red, int green, int blue, int alpha) {
        return ((alpha & 0xFF) << 24) | ((red & 0xFF) << 16) | ((green & 0xFF) << 8) | (blue & 0xFF);
    }

    public static int[] unpack(int color) {
        return new int[]{color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF, color >> 24 & 0xFF};
    }

    public static float[] normalize(Color color) {
        return new float[]{color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f};
    }

    public static float[] normalize(int color) {
        int[] c = unpack(color);
        return new float[]{c[0] / 255f, c[1] / 255f, c[2] / 255f, c[3] / 255f};
    }

    public static int red(int c) {
        return c >> 16 & 0xFF;
    }

    public static int green(int c) {
        return c >> 8 & 0xFF;
    }

    public static int blue(int c) {
        return c & 0xFF;
    }

    public static int alpha(int c) {
        return c >> 24 & 0xFF;
    }

    private static Theme theme() {
        ThemeManager manager = NeverLose.getInstance() != null ? NeverLose.getInstance().getThemeManager() : null;
        return manager != null ? manager.getCurrentTheme() : null;
    }

    public static int getColorMain() {
        Theme t = theme();
        return t != null ? t.colorMain : 0xFF0B1120;
    }

    public static int getColorVisualModules() {
        Theme t = theme();
        return t != null ? t.colorVisualModules : 0xFF3C6EF5;
    }

    public static int getColorText() {
        Theme t = theme();
        return t != null ? t.colorText : 0xFFE8E8E8;
    }

    public static int getColorInactiveText() {
        Theme t = theme();
        return t != null ? t.colorInactiveText : 0xFF7E86A0;
    }

    public static int getColorHeaderBg() {
        Theme t = theme();
        return t != null ? t.colorHeaderBg : 0xFF0A0F1E;
    }

    public static int getColorHeaderText() {
        Theme t = theme();
        return t != null ? t.colorHeaderText : 0xFF3C6EF5;
    }

    public static int getColorSlider() {
        Theme t = theme();
        return t != null ? t.colorSlider : 0xFF3C6EF5;
    }

    public static int getColorSliderCircle() {
        Theme t = theme();
        return t != null ? t.colorSliderCircle : 0xFFFFFFFF;
    }

    public static int getColorSliderWindow() {
        Theme t = theme();
        return t != null ? t.colorSliderWindow : 0xFF232A45;
    }

    public static int getColorIndicator() {
        Theme t = theme();
        return t != null ? t.colorIndicator : 0xFF3C6EF5;
    }

    public static int getColorInactiveIndicator() {
        Theme t = theme();
        return t != null ? t.colorInactiveIndicator : 0xFF232A45;
    }

    public static int getColorButton() {
        Theme t = theme();
        return t != null ? t.colorButton : 0xFF3C6EF5;
    }

    public static int getColorInactiveButton() {
        Theme t = theme();
        return t != null ? t.colorInactiveButton : 0xFF232A45;
    }

    public static int getColorIcons() {
        Theme t = theme();
        return t != null ? t.colorIcons : 0xFF3C6EF5;
    }

    public static int getColorClient() {
        Theme t = theme();
        return t != null ? t.colorClient : 0xFF3C6EF5;
    }

    public static int getColorClickGui() {
        Theme t = theme();
        return t != null ? t.colorClickGui : 0xFF000000;
    }

    public static int getColorInterfaceBg() {
        Theme t = theme();
        return t != null ? t.colorInterfaceBg : 0xFF000000;
    }

    public static int interpolateColor(int from, int to, float amount) {
        amount = Math.min(1.0f, Math.max(0.0f, amount));
        int red = interpolateInt(red(from), red(to), amount);
        int green = interpolateInt(green(from), green(to), amount);
        int blue = interpolateInt(blue(from), blue(to), amount);
        int alpha = interpolateInt(alpha(from), alpha(to), amount);
        return alpha << 24 | red << 16 | green << 8 | blue;
    }

    private static int interpolateInt(int oldValue, int newValue, float interpolationValue) {
        return Math.round(oldValue + (newValue - oldValue) * interpolationValue);
    }

    public static int rgba(int r, int g, int b, double a) {
        return (int) a << 24 | r << 16 | g << 8 | b;
    }

    public static int rgba(int r, int g, int b, float a) {
        return (int) a << 24 | r << 16 | g << 8 | b;
    }

    public static int setAlpha(int color, int alpha) {
        return (MathHelper.clamp(alpha, 0, 255) << 24) | (color & 0x00FFFFFF);
    }

    public static int setAlpha(int color, double alpha) {
        return (MathHelper.clamp((int) alpha, 0, 255) << 24) | (color & 0x00FFFFFF);
    }

    public static int brighter(int color, float factor) {
        int r = Math.min(255, (int) (red(color) * factor));
        int g = Math.min(255, (int) (green(color) * factor));
        int b = Math.min(255, (int) (blue(color) * factor));
        return rgba(r, g, b, alpha(color));
    }
}
