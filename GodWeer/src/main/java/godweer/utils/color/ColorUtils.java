package godweer.utils.color;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

import java.awt.Color;

public final class ColorUtils {

    public static final Color GREEN = new Color(36, 218, 118);
    public static final Color YELLOW = new Color(255, 196, 67);
    public static final Color ORANGE = new Color(255, 134, 0);
    public static final Color RED = new Color(239, 72, 54);
    public static final Color BLUES = new Color(125, 217, 250);

    private static final int[] THEME_COLORS = {
            rgba(42, 115, 255, 255),
            rgba(122, 86, 255, 255),
            rgba(255, 86, 190, 255),
            rgba(86, 220, 255, 255)
    };

    private ColorUtils() {
    }

    public static int rgb(int r, int g, int b) {
        return 255 << 24 | r << 16 | g << 8 | b;
    }

    public static int rgba(int r, int g, int b, int a) {
        return a << 24 | r << 16 | g << 8 | b;
    }

    public static int rgba(double r, double g, double b, double a) {
        return rgba((int) r, (int) g, (int) b, (int) a);
    }

    public static float[] rgba(int color) {
        return new float[]{redf(color), greenf(color), bluef(color), alphaf(color)};
    }

    public static int red(int color) {
        return color >> 16 & 0xFF;
    }

    public static int green(int color) {
        return color >> 8 & 0xFF;
    }

    public static int blue(int color) {
        return color & 0xFF;
    }

    public static int alpha(int color) {
        return color >>> 24 & 0xFF;
    }

    public static int getRed(int color) {
        return red(color);
    }

    public static int getGreen(int color) {
        return green(color);
    }

    public static int getBlue(int color) {
        return blue(color);
    }

    public static int getAlpha(int color) {
        return alpha(color);
    }

    public static int r(int color) {
        return red(color);
    }

    public static int g(int color) {
        return green(color);
    }

    public static int b(int color) {
        return blue(color);
    }

    public static int a(int color) {
        return alpha(color);
    }

    public static float redf(int color) {
        return red(color) / 255.0f;
    }

    public static float greenf(int color) {
        return green(color) / 255.0f;
    }

    public static float bluef(int color) {
        return blue(color) / 255.0f;
    }

    public static float alphaf(int color) {
        return alpha(color) / 255.0f;
    }

    public static float[] getColorA(int color) {
        return rgba(color);
    }

    public static int getColor(int r, int g, int b, int a) {
        return clamp255(a) << 24 | clamp255(r) << 16 | clamp255(g) << 8 | clamp255(b);
    }

    public static int getColor(float r, float g, float b, float a) {
        return getColor((int) r, (int) g, (int) b, (int) a);
    }

    public static int getColor(int brightness) {
        return getColor(brightness, brightness, brightness, 255);
    }

    public static int getColor(int brightness, int a) {
        return getColor(brightness, brightness, brightness, a);
    }

    public static int setAlphaColor(int color, int a) {
        return (color & 0x00FFFFFF) | (clamp255(a) << 24);
    }

    public static int replAlpha(int color, int a) {
        return setAlphaColor(color, a);
    }

    public static int applyAlpha(int color, float alphaMul) {
        int a = alpha(color);
        int na = (int) (a * clamp01(alphaMul));
        return (color & 0x00FFFFFF) | (na << 24);
    }

    public static int applyOpacity(int color, float a) {
        return setAlphaColor(color, (int) (alpha(color) * a / 255.0f));
    }

    public static int multAlpha(int color, float alphaMul) {
        return setAlphaColor(color, (int) (alpha(color) * alphaMul));
    }

    public static int multDark(int color, float brightnessMul) {
        return getColor(
                (int) (red(color) * brightnessMul),
                (int) (green(color) * brightnessMul),
                (int) (blue(color) * brightnessMul),
                alpha(color)
        );
    }

    public static int reFactorColor(int color, float factor) {
        return multDark(color, factor);
    }

    /**
     * HSB-затемнение без java.awt: преобразование HSB -> RGB линейно по яркости,
     * поэтому масштабирование V эквивалентно умножению всех каналов на тот же множитель.
     * Ограничение V <= 1 повторяется через клампинг множителя по максимальному каналу.
     */
    public static int darken(int color, float factor) {
        int r = red(color);
        int g = green(color);
        int bl = blue(color);
        int max = r > g ? (r > bl ? r : bl) : (g > bl ? g : bl);
        if (max == 0) {
            return color;
        }
        float limit = 255.0f / max;
        float scale = factor < 0.0f ? 0.0f : (factor > limit ? limit : factor);
        return getColor((int) (r * scale + 0.5f), (int) (g * scale + 0.5f), (int) (bl * scale + 0.5f), alpha(color));
    }

    public static int overCol(int c1, int c2, float pc01) {
        float inv = 1.0f - pc01;
        return getColor(
                (int) (red(c1) * inv + red(c2) * pc01),
                (int) (green(c1) * inv + green(c2) * pc01),
                (int) (blue(c1) * inv + blue(c2) * pc01),
                (int) (alpha(c1) * inv + alpha(c2) * pc01)
        );
    }

    public static int overCol(int c1, int c2) {
        return overCol(c1, c2, 0.5f);
    }

    public static float lerp(float a, float b, float f) {
        return a + f * (b - a);
    }

    public static int interpolateInt(int oldValue, int newValue, double value) {
        return (int) Math.round(oldValue + (newValue - oldValue) * value);
    }

    public static float interpolateFloat(float oldValue, float newValue, double value) {
        return (float) (oldValue + (newValue - oldValue) * value);
    }

    public static int interpolate(int color1, int color2, double amount) {
        double t = clamp01((float) amount);
        return getColor(
                interpolateInt(red(color1), red(color2), t),
                interpolateInt(green(color1), green(color2), t),
                interpolateInt(blue(color1), blue(color2), t),
                interpolateInt(alpha(color1), alpha(color2), t)
        );
    }

    public static int interpolateColor(int color1, int color2, float amount) {
        return interpolate(color1, color2, amount);
    }

    public static Color interpolateColorC(Color color1, Color color2, float amount) {
        float t = clamp01(amount);
        return new Color(
                interpolateInt(color1.getRed(), color2.getRed(), t),
                interpolateInt(color1.getGreen(), color2.getGreen(), t),
                interpolateInt(color1.getBlue(), color2.getBlue(), t),
                interpolateInt(color1.getAlpha(), color2.getAlpha(), t)
        );
    }

    public static Color interpolateColorHue(Color color1, Color color2, float amount) {
        float t = clamp01(amount);
        float[] hsb1 = Color.RGBtoHSB(color1.getRed(), color1.getGreen(), color1.getBlue(), null);
        float[] hsb2 = Color.RGBtoHSB(color2.getRed(), color2.getGreen(), color2.getBlue(), null);
        Color result = Color.getHSBColor(
                interpolateFloat(hsb1[0], hsb2[0], t),
                interpolateFloat(hsb1[1], hsb2[1], t),
                interpolateFloat(hsb1[2], hsb2[2], t)
        );
        return new Color(
                result.getRed(),
                result.getGreen(),
                result.getBlue(),
                interpolateInt(color1.getAlpha(), color2.getAlpha(), t)
        );
    }

    public static Color interpolateTwoColors(int speed, int index, Color start, Color end, boolean trueColor) {
        int angle = speed == 0 ? index % 360 : (int) ((System.currentTimeMillis() / speed + index) % 360);
        angle = (angle >= 180 ? 360 - angle : angle) * 2;
        return trueColor ? interpolateColorHue(start, end, angle / 360.0f) : interpolateColorC(start, end, angle / 360.0f);
    }

    public static Color interpolateTwoColors(int speed, int index, Color start, Color end) {
        return interpolateTwoColors(speed, index, start, end, false);
    }

    public static int gradient(int color1, int color2, float amount) {
        return interpolate(color1, color2, clamp01(amount));
    }

    public static int gradient(int speed, int index, int... colors) {
        int angle = (int) ((System.currentTimeMillis() / speed + index) % 360);
        angle = (angle > 180 ? 360 - angle : angle) + 180;
        int colorIndex = (int) (angle / 360.0f * colors.length);
        if (colorIndex == colors.length) {
            colorIndex--;
        }
        int color1 = colors[colorIndex];
        int color2 = colors[colorIndex == colors.length - 1 ? 0 : colorIndex + 1];
        return interpolateColor(color1, color2, angle / 360.0f * colors.length - colorIndex);
    }

    public static int[] genGradientForText(int color1, int color2, int length) {
        int[] gradient = new int[length];
        for (int i = 0; i < length; i++) {
            gradient[i] = interpolate(color1, color2, (double) i / (length - 1));
        }
        return gradient;
    }

    public static int rainbow(int speed, int index, float saturation, float brightness, float opacity) {
        int angle = (int) ((System.currentTimeMillis() / speed + index) % 360);
        int color = Color.HSBtoRGB(angle / 360.0f, saturation, brightness);
        return getColor(red(color), green(color), blue(color), (int) (clamp01(opacity) * 255));
    }

    public static Color astolfo(float yDist, float yTotal, float saturation, float speedt) {
        float speed = 1800.0f;
        float hue = (System.currentTimeMillis() % (int) speed) + (yTotal - yDist) * speedt;
        while (hue > speed) {
            hue -= speed;
        }
        hue /= speed;
        if (hue > 1.0f) {
            hue = 1.0f - (hue - 1.0f);
        }
        hue += 1.0f;
        return Color.getHSBColor(hue, saturation, 1.0f);
    }

    public static Color random() {
        return new Color(Color.HSBtoRGB(
                (float) Math.random(),
                (float) (0.75f + Math.random() / 4.0f),
                (float) (0.75f + Math.random() / 4.0f)
        ));
    }

    public static int getThemeColor() {
        return THEME_COLORS[0];
    }

    public static int getThemeColor(int index) {
        return THEME_COLORS[Math.floorMod(index, THEME_COLORS.length)];
    }

    public static int getThemeStaticColor() {
        return THEME_COLORS[0];
    }

    public static int themeGradient(int speed, int index, float darkenFactor) {
        int theme = getThemeColor();
        return gradient(speed, index, theme, darken(theme, darkenFactor));
    }

    public static void setThemeColor(int index, int color) {
        if (index >= 0 && index < THEME_COLORS.length) {
            THEME_COLORS[index] = color;
        }
    }

    public static int getThemeColorCount() {
        return THEME_COLORS.length;
    }

    public static int toColor(String hexColor) {
        if (hexColor == null || hexColor.length() != 7 || !hexColor.startsWith("#")) {
            return 0xFF000000;
        }
        try {
            return 0xFF000000 | Integer.parseInt(hexColor.substring(1), 16);
        } catch (NumberFormatException e) {
            return 0xFF000000;
        }
    }

    public static int hexToRgb(String hex) {
        String value = hex.startsWith("#") ? hex.substring(1) : hex;
        if (value.length() != 6) {
            throw new IllegalArgumentException("Недопустимый формат HEX: " + hex);
        }
        return rgb(
                Integer.parseInt(value.substring(0, 2), 16),
                Integer.parseInt(value.substring(2, 4), 16),
                Integer.parseInt(value.substring(4, 6), 16)
        );
    }

    public static MutableComponent gradient(String message, int first, int end) {
        MutableComponent text = Component.empty();
        int length = message.length();
        for (int i = 0; i < length; i++) {
            int color = interpolateColor(first, end, (float) i / length);
            text.append(Component.literal(String.valueOf(message.charAt(i)))
                    .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(color))));
        }
        return text;
    }

    public static Component replace(Component original, String find, String replaceWith) {
        if (original == null || find == null || replaceWith == null) {
            return original;
        }
        return Component.literal(original.getString().replace(find, replaceWith));
    }

    public static void setShaderColor(int color) {
        setShaderColor(color, alphaf(color));
    }

    public static void setShaderColor(int color, float a) {
        RenderSystem.setShaderColor(redf(color), greenf(color), bluef(color), a);
    }

    public static void setShaderColor(Color color) {
        Color value = color == null ? Color.WHITE : color;
        RenderSystem.setShaderColor(
                value.getRed() / 255.0f,
                value.getGreen() / 255.0f,
                value.getBlue() / 255.0f,
                value.getAlpha() / 255.0f
        );
    }

    private static int clamp255(int value) {
        return value < 0 ? 0 : (value > 255 ? 255 : value);
    }

    private static float clamp01(float value) {
        return value < 0.0f ? 0.0f : (value > 1.0f ? 1.0f : value);
    }
}
