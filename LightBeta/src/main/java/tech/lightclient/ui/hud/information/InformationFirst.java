package tech.lightclient.ui.hud.information;

import tech.lightclient.client.events.impl.render.EventRender;
import tech.lightclient.client.utils.player.PlayerUtils;
import tech.lightclient.client.utils.render.drag.api.Dragging;
import tech.lightclient.client.utils.render.main.text.FontRegistry;
import tech.lightclient.modules.impl.render.Interface;
import tech.lightclient.ui.hud.HudShadowRenderer;
import tech.lightclient.ui.hud.api.HudElement;

import java.awt.Color;
import java.util.Locale;

public class InformationFirst extends InformationBase implements HudElement {
    private static final float BAR_HEIGHT = 31.0f;
    private static final float OUTLINE = 0.9f;
    private static final float RADIUS = 10.5f;
    private static final float LEFT_BLOCK_WIDTH = 31.5f;
    private static final float CONTENT_LEFT_PADDING = 10.0f;
    private static final float CONTENT_RIGHT_PADDING = 12.0f;
    private static final float ENTRY_GAP = 11.0f;
    private static final float ICON_TEXT_GAP = 5.0f;
    private static final float TEXT_SIZE = 7.3f;
    private static final float LOGO_ICON_SIZE = 15.0f;
    private static final float INFO_ICON_SIZE = 9.1f;
    private static final float LOGO_ICON_NUDGE = 13.2f;
    private static final float INFO_ICON_NUDGE = 8.8f;

    private static final int OUTLINE_COLOR = rgba(54, 54, 60, 190);
    private static final int PANEL_TOP = rgba(33, 33, 39, 245);
    private static final int PANEL_BOTTOM = rgba(25, 25, 31, 245);
    private static final int LEFT_TOP = rgba(40, 40, 48, 255);
    private static final int LEFT_BOTTOM = rgba(32, 32, 40, 255);
    private static final int SEPARATOR = rgba(96, 96, 102, 120);
    private static final int TEXT_COLOR = rgba(255, 255, 255, 255);
    private static final int ICON_COLOR = rgba(245, 245, 245, 255);

    public InformationFirst(Dragging dragging) {
        super(dragging);
    }

    @Override
    public void render(EventRender.Screen.Hud event) {
        if (mc.player == null || mc.world == null) {
            dragging.setWidth(0f);
            dragging.setHeight(0f);
            return;
        }

        float scaleFactor = Interface.getHudScale(SETTINGS_SCOPE);
        float x = Interface.scalePos(SETTINGS_SCOPE, dragging.getX());
        float y = Interface.scalePos(SETTINGS_SCOPE, dragging.getY());

        String logoIcon = "x";
        String coordsIcon = "g";
        String speedIcon = "b";
        String coordsText = formatCoords();
        String speedText = formatBps();

        float coordsWidth = getEntryWidth(coordsIcon, coordsText);
        float speedWidth = getEntryWidth(speedIcon, speedText);
        float rectWidth = LEFT_BLOCK_WIDTH
                + CONTENT_LEFT_PADDING
                + coordsWidth
                + ENTRY_GAP
                + speedWidth
                + CONTENT_RIGHT_PADDING;

        drawPanel(event, x, y, rectWidth, BAR_HEIGHT);
        drawLeftBlock(event, x, y, logoIcon);

        float contentX = x + LEFT_BLOCK_WIDTH + CONTENT_LEFT_PADDING;
        contentX = renderEntry(event, coordsIcon, coordsText, contentX, y, BAR_HEIGHT);
        contentX += ENTRY_GAP;

        float dividerX = contentX - ENTRY_GAP * 0.5f;
        event.getRenderer().rect(dividerX, y + BAR_HEIGHT * 0.28f, 0.75f, BAR_HEIGHT * 0.44f, 0.3f, SEPARATOR);

        renderEntry(event, speedIcon, speedText, contentX, y, BAR_HEIGHT);

        width = rectWidth;
        height = BAR_HEIGHT;
        dragging.setWidth(width * scaleFactor);
        dragging.setHeight(height * scaleFactor);
    }

    private void drawPanel(EventRender.Screen.Hud event, float x, float y, float width, float height) {
        float radius = Interface.getHudRounding(SETTINGS_SCOPE, RADIUS);
        HudShadowRenderer.render(event.getRenderer(), SETTINGS_SCOPE, x, y, width, height, radius, 1f, Interface.getHudScale(SETTINGS_SCOPE));
        Interface.renderHudBackground(event.getRenderer(), x, y, width, height, radius, 1f);
        event.getRenderer().rect(x, y, width, height, radius, OUTLINE_COLOR);
        if (!Interface.isHudBlurBackground()) {
            event.getRenderer().gradient(
                    x + OUTLINE,
                    y + OUTLINE,
                    width - OUTLINE * 2f,
                    height - OUTLINE * 2f,
                    radius - 0.75f,
                    PANEL_TOP, PANEL_TOP, PANEL_BOTTOM, PANEL_BOTTOM
            );
        }
    }

    private void drawLeftBlock(EventRender.Screen.Hud event, float x, float y, String logoIcon) {
        float radius = Interface.getHudRounding(SETTINGS_SCOPE, RADIUS);
        event.getRenderer().gradient(
                x + OUTLINE,
                y + OUTLINE,
                LEFT_BLOCK_WIDTH - OUTLINE,
                BAR_HEIGHT - OUTLINE * 2f,
                radius - 0.75f, 0f, 0f, radius - 0.75f,
                LEFT_TOP, LEFT_TOP, LEFT_BOTTOM, LEFT_BOTTOM
        );

        float separatorX = x + LEFT_BLOCK_WIDTH;
        event.getRenderer().rect(separatorX, y + 6f, 0.75f, BAR_HEIGHT - 12f, 0.3f, SEPARATOR);

        float iconBaseline = y + BAR_HEIGHT * 0.5f
                + FontRegistry.centeredBaselineOffset(FontRegistry.LIGHT_TTF, logoIcon.charAt(0), LOGO_ICON_SIZE)
                + LOGO_ICON_NUDGE;
        float iconX = x + 1.5f + (LEFT_BLOCK_WIDTH - FontRegistry.LIGHT_TTF.getWidth(logoIcon, LOGO_ICON_SIZE)) * 0.5f;
        event.getRenderer().text(FontRegistry.LIGHT_TTF, iconX, iconBaseline, LOGO_ICON_SIZE, logoIcon, ICON_COLOR);
    }

    private float renderEntry(EventRender.Screen.Hud event, String icon, String text, float x, float y, float rowHeight) {
        float iconBaseline = y + rowHeight * 0.5f
                + FontRegistry.centeredBaselineOffset(FontRegistry.LIGHT_TTF, icon.charAt(0), INFO_ICON_SIZE)
                + INFO_ICON_NUDGE;
        event.getRenderer().text(FontRegistry.LIGHT_TTF, x, iconBaseline, INFO_ICON_SIZE, icon, ICON_COLOR);
        x += FontRegistry.LIGHT_TTF.getWidth(icon, INFO_ICON_SIZE) + ICON_TEXT_GAP;

        float textBaseline = y + rowHeight * 0.5f
                + FontRegistry.centeredBaselineOffset(FontRegistry.DELTA_HUD, 'H', TEXT_SIZE)
                + 0.55f;
        event.getRenderer().text(FontRegistry.DELTA_HUD, x, textBaseline, TEXT_SIZE, text, TEXT_COLOR);
        return x + FontRegistry.DELTA_HUD.getWidth(text, TEXT_SIZE);
    }

    private float getEntryWidth(String icon, String text) {
        return FontRegistry.LIGHT_TTF.getWidth(icon, INFO_ICON_SIZE)
                + ICON_TEXT_GAP
                + FontRegistry.DELTA_HUD.getWidth(text, TEXT_SIZE);
    }

    private String formatCoords() {
        int x = mc.player.getBlockX();
        int y = mc.player.getBlockY();
        int z = mc.player.getBlockZ();
        return "x" + x + " y" + y + " z" + String.format(Locale.US, "%,d", z);
    }

    private String formatBps() {
        double bps = PlayerUtils.getBPS();
        return String.format(Locale.US, "%.1f", bps).replace('.', ',') + "bps";
    }

    private static int rgba(int r, int g, int b, int a) {
        return new Color(r, g, b, a).getRGB();
    }
}
