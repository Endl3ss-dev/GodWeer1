package tech.lightclient.ui.hud.first;

import tech.lightclient.client.ClientColors;
import tech.lightclient.client.events.impl.render.EventRender;
import tech.lightclient.client.utils.render.main.text.FontRegistry;
import tech.lightclient.modules.impl.render.Interface;
import tech.lightclient.ui.hud.HudShadowRenderer;

import java.awt.Color;

public final class FirstHudTheme {
    private static final float BASE_SCALE = 1.5f;

    public static final float PANEL_RADIUS = scaled(10.125f);
    public static final float HEADER_HEIGHT = scaled(20.5f);
    public static final float ROW_HEIGHT = scaled(12.375f);
    public static final float ROW_GAP = scaled(2.25f);
    public static final float SIDE_PADDING = scaled(6.75f);
    public static final float TITLE_SIZE = scaled(6.3f);
    public static final float ROW_TEXT_SIZE = scaled(5.8f);
    public static final float HEADER_ICON_SIZE = scaled(7.2f);
    public static final float OUTLINE = scaled(0.75f);
    public static final float HEADER_TEXT_INSET = scaled(2.5f);
    public static final float HEADER_TITLE_Y_OFFSET = scaled(-2.5f);
    public static final float HEADER_ICON_RIGHT_INSET = scaled(9.0f);
    public static final float HEADER_ICON_Y_OFFSET = scaled(-2.5f);
    public static final float HEADER_MIN_RIGHT_SPACE = scaled(6.8f);

    public static final int PANEL_BG = rgba(29, 29, 31, 236);
    public static final int HEADER_TOP = rgba(34, 34, 36, 248);
    public static final int HEADER_BOTTOM = rgba(31, 31, 33, 248);
    public static final int OUTLINE_COLOR = rgba(56, 56, 60, 170);
    public static final int DIVIDER = rgba(62, 62, 66, 110);
    public static final int SEPARATOR = rgba(96, 96, 102, 74);
    public static final int TEXT_COLOR = rgba(255, 255, 255, 255);
    public static final int MUTED_TEXT_COLOR = rgba(145, 145, 145, 255);
    public static final int BRIGHT_MUTED_TEXT_COLOR = rgba(214, 214, 214, 255);
    public static final int ICON_COLOR = rgba(245, 245, 245, 255);
    public static final int ROW_DIVIDER = rgba(92, 92, 98, 110);

    private FirstHudTheme() {
    }

    public static void drawPanel(EventRender.Screen.Hud event, float x, float y, float width, float height, float alpha) {
        drawPanel(event, null, x, y, width, height, alpha);
    }

    public static void drawPanel(EventRender.Screen.Hud event, String scope, float x, float y, float width, float height, float alpha) {
        if (scope != null) {
            HudShadowRenderer.render(event.getRenderer(), scope, x, y, width, height, PANEL_RADIUS, alpha, Interface.getHudScale(scope));
        }
        Interface.renderHudBackground(event.getRenderer(), x, y, width, height, PANEL_RADIUS, alpha);
        event.getRenderer().rect(x, y, width, height, PANEL_RADIUS, ClientColors.applyAlpha(OUTLINE_COLOR, alpha));
        if (!Interface.isHudBlurBackground()) {
            event.getRenderer().rect(
                    x + OUTLINE,
                    y + OUTLINE,
                    width - OUTLINE * 2f,
                    height - OUTLINE * 2f,
                    PANEL_RADIUS - OUTLINE,
                    ClientColors.applyAlpha(PANEL_BG, alpha)
            );
        }
    }

    public static void drawHeader(EventRender.Screen.Hud event, float x, float y, float width, float alpha, String icon, String title) {
        event.getRenderer().gradient(
                x + OUTLINE,
                y + OUTLINE,
                width - OUTLINE * 2f,
                HEADER_HEIGHT - OUTLINE,
                PANEL_RADIUS - OUTLINE, PANEL_RADIUS - OUTLINE, 0f, 0f,
                ClientColors.applyAlpha(HEADER_TOP, alpha),
                ClientColors.applyAlpha(HEADER_TOP, alpha),
                ClientColors.applyAlpha(HEADER_BOTTOM, alpha),
                ClientColors.applyAlpha(HEADER_BOTTOM, alpha)
        );
        event.getRenderer().rect(
                x + SIDE_PADDING,
                y + HEADER_HEIGHT,
                width - SIDE_PADDING * 2f,
                OUTLINE,
                Math.max(0.3f, OUTLINE * 0.4f),
                ClientColors.applyAlpha(DIVIDER, alpha)
        );

        float textX = x + SIDE_PADDING + HEADER_TEXT_INSET;
        float textY = y + HEADER_HEIGHT * 0.5f - HEADER_TITLE_Y_OFFSET;
        float iconX = x + width - SIDE_PADDING - HEADER_ICON_RIGHT_INSET;
        float iconY = y + HEADER_HEIGHT * 0.5f - HEADER_ICON_Y_OFFSET;
        event.getRenderer().text(
                FontRegistry.DELTA_HUD,
                textX,
                textY,
                TITLE_SIZE,
                title,
                ClientColors.applyAlpha(TEXT_COLOR, alpha)
        );
        event.getRenderer().text(
                FontRegistry.LIGHT_TTF,
                iconX,
                iconY,
                HEADER_ICON_SIZE,
                icon,
                ClientColors.applyAlpha(ICON_COLOR, alpha)
        );
    }

    public static float getHeaderMinWidth(String title, String icon, float extraPadding) {
        return SIDE_PADDING * 2f
                + FontRegistry.DELTA_HUD.getWidth(title, TITLE_SIZE)
                + HEADER_MIN_RIGHT_SPACE
                + extraPadding;
    }

    public static float scale(float value) {
        return scaled(value);
    }

    private static float scaled(float value) {
        return value * BASE_SCALE;
    }

    private static int rgba(int r, int g, int b, int a) {
        return new Color(r, g, b, a).getRGB();
    }
}
