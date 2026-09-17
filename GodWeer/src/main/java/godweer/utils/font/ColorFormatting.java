package godweer.utils.font;

import godweer.utils.color.ColorUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Разметка цвета внутри строки: {@code ${rgb(255,0,0)}}, {@code ${rgba(255,0,0,128)}} и {@code ${reset}}.
 * Порт {@code org.privates.client.utils.render.color.ColorFormatting} из Private.
 */
public final class ColorFormatting {

    public static final Pattern PATTERN = Pattern.compile(
            "\\$\\{(rgba|rgb)\\((\\d{1,3}),(\\d{1,3}),(\\d{1,3})(?:,(\\d{1,3}))?\\)}|\\$\\{reset}",
            Pattern.CASE_INSENSITIVE);

    private static final String MARKER_START = "${";

    private ColorFormatting() {
    }

    public static String getColor(int red, int green, int blue) {
        return MARKER_START + "rgb(" + red + "," + green + "," + blue + ")}";
    }

    public static String getColor(int red, int green, int blue, int alpha) {
        return MARKER_START + "rgba(" + red + "," + green + "," + blue + "," + alpha + ")}";
    }

    public static String getColor(int color) {
        return getColor(ColorUtils.red(color), ColorUtils.green(color), ColorUtils.blue(color), ColorUtils.alpha(color));
    }

    public static int overColor(int firstColor, int secondColor, float factorPercent) {
        return ColorUtils.overCol(firstColor, secondColor, factorPercent);
    }

    public static String reset() {
        return MARKER_START + "reset}";
    }

    public static String typeRGB() {
        return "rgb";
    }

    public static String typeRGBA() {
        return "rgba";
    }

    // Быстрая проверка без Regex: вызывается на каждой строке перед отрисовкой и замером ширины.
    public static boolean hasFormatting(String text) {
        return text != null && text.indexOf(MARKER_START) >= 0;
    }

    public static String removeFormatting(String text) {
        if (text == null) {
            return null;
        }
        if (!hasFormatting(text)) {
            return text;
        }
        return PATTERN.matcher(text).replaceAll("");
    }

    public static String replaceColor(String text, int red, int green, int blue) {
        return PATTERN.matcher(text).replaceAll(Matcher.quoteReplacement(getColor(red, green, blue)));
    }

    public static String replaceColor(String text, int red, int green, int blue, int alpha) {
        return PATTERN.matcher(text).replaceAll(Matcher.quoteReplacement(getColor(red, green, blue, alpha)));
    }

    /**
     * Цвет из группы мэтчера. Возвращает {@code fallback}, если маркер нераспознан.
     */
    public static int parseColor(Matcher matcher, int fallback) {
        try {
            int red = Integer.parseInt(matcher.group(2));
            int green = Integer.parseInt(matcher.group(3));
            int blue = Integer.parseInt(matcher.group(4));

            int baseAlpha = ColorUtils.alpha(fallback);
            if (baseAlpha == 0) {
                baseAlpha = 255;
            }

            String alphaGroup = matcher.group(5);
            int alpha = alphaGroup == null ? baseAlpha : Integer.parseInt(alphaGroup);

            return ColorUtils.getColor(red, green, blue, alpha);
        } catch (RuntimeException exception) {
            return fallback;
        }
    }
}
