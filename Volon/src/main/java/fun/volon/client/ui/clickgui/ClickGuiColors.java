package fun.volon.client.ui.clickgui;

import fun.volon.api.utils.color.ColorUtils;

public final class ClickGuiColors {
    private ClickGuiColors() {
    }

    public static int theme() {
        try {
            return ColorUtils.getThemeColor();
        } catch (Throwable ignored) {
            return ColorUtils.rgba(190, 143, 255, 255);
        }
    }

    public static int alpha(int color, float alpha) {
        return ColorUtils.applyAlpha(color, Math.max(0f, Math.min(1f, alpha)));
    }
}
