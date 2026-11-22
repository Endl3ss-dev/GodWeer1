package tech.lightclient.ui.hud.star;

import net.minecraft.util.Identifier;

import tech.lightclient.client.ClientColors;
import tech.lightclient.client.utils.render.main.core.Renderer2D;
import tech.lightclient.client.utils.render.main.text.FontObject;
import tech.lightclient.client.utils.render.main.text.FontRegistry;

public final class StarUtil {
    public static final float GOLD = 0xFFFFDC51;
    public static final int RED = 0xFFFF3B3B;
    public static final int PURPLE = 0xFF7300FF;

    private StarUtil() {
    }

    public static Identifier id(String path) {
        return Identifier.of("light", path);
    }

    public static int applyAlpha(int argb, float alpha) {
        return ClientColors.applyAlpha(argb, alpha);
    }

    public static int white(float alpha) {
        return ClientColors.applyAlpha(0xFFFFFFFF, alpha);
    }

    public static int text(float alpha) {
        return ClientColors.applyAlpha(ClientColors.TEXT.getRGB(), alpha);
    }

    public static int background(float alpha) {
        return ClientColors.applyAlpha(ClientColors.BACKGROUND.getRGB(), alpha);
    }

    public static int accent(float alpha) {
        return ClientColors.applyAlpha(ClientColors.GRADIENT_START.getRGB(), alpha);
    }

    public static float textBaseline(FontObject font, char probe, float topY, float boxHeight, float size) {
        return topY + boxHeight * 0.5f + FontRegistry.centeredBaselineOffset(font, probe, size);
    }

    public static String trim(FontObject font, String text, float maxWidth, float size) {
        if (text == null || text.isEmpty() || font.getWidth(text, size) <= maxWidth) {
            return text == null ? "" : text;
        }
        String result = text;
        while (result.length() > 2 && font.getWidth(result + "...", size) > maxWidth) {
            result = result.substring(0, result.length() - 1);
        }
        return result + "...";
    }

    public static String formatNumber(float value) {
        if (value >= 999.95f) {
            return "?";
        }
        if (value == Math.floor(value)) {
            return String.valueOf((int) value);
        }
        return String.format("%.1f", value).replace(",", ".");
    }

    public static String currentTime() {
        java.time.LocalTime now = java.time.LocalTime.now();
        return String.format("%02d:%02d", now.getHour(), now.getMinute());
    }
}
