package tech.lightclient.ui.hud.information;

import tech.lightclient.client.ClientColors;
import tech.lightclient.client.events.impl.render.EventRender;
import tech.lightclient.client.utils.render.drag.api.Dragging;
import tech.lightclient.client.utils.render.main.text.FontRegistry;
import tech.lightclient.modules.impl.render.Interface;
import tech.lightclient.ui.hud.HudShadowRenderer;
import tech.lightclient.ui.hud.api.HudElement;

public class InformationTwo extends InformationBase implements HudElement {

    public InformationTwo(Dragging dragging) {
        super(dragging);
    }

    @Override
    public void render(EventRender.Screen.Hud event) {
        if (mc.player == null || mc.world == null) {
            dragging.setWidth(0f);
            dragging.setHeight(0f);
            return;
        }

        String iconText = "M";

        float scaleFactor = Interface.getInterfaceScale();
        float x = Interface.scalePos(dragging.getX());
        float y = Interface.scalePos(dragging.getY());

        String text = "XYZ: " + mc.player.getBlockX() + ", " + mc.player.getBlockY() + ", " + mc.player.getBlockZ();
        float textSize = 16f;
        float iconSize = 20f;
        float paddingX = 7f;
        float iconSpacing = 3f;
        float rectHeight = 29.5f;

        float iconWidth = FontRegistry.ICONS_ASYNC.getWidth(iconText, iconSize);
        float textWidth = FontRegistry.DELTA_HUD.getWidth(text, textSize);
        float rectWidth = iconWidth + iconSpacing + textWidth + paddingX * 2f;

        int panelColor = ClientColors.BACKGROUND.getRGB();
        int textColor = ClientColors.TEXT.getRGB();
        int iconColor = ClientColors.ICON.getRGB();

        float rounding = Interface.getHudRounding(SETTINGS_SCOPE, 9f);
        HudShadowRenderer.render(event.getRenderer(), SETTINGS_SCOPE, x, y, rectWidth, rectHeight, rounding, 1f, Interface.getHudScale(SETTINGS_SCOPE));
        Interface.renderHudBackground(event.getRenderer(), x, y, rectWidth, rectHeight, rounding, 1f);
        if (!Interface.isHudBlurBackground()) event.getRenderer().rect(x, y, rectWidth, rectHeight, rounding, panelColor);

        float textX = x + paddingX + iconWidth + iconSpacing;
        float iconX = x + paddingX - 1;
        float iconY = y + rectHeight * 0.5f + FontRegistry.centeredBaselineOffset(FontRegistry.ICONS_ASYNC, 'H', iconSize) + 0.5f;
        float textY = y + rectHeight * 0.5f + FontRegistry.centeredBaselineOffset(FontRegistry.DELTA_HUD, 'H', textSize) + 0.25f;
        event.getRenderer().text(FontRegistry.ICONS_ASYNC, iconX, iconY, iconSize, iconText, iconColor);
        event.getRenderer().text(FontRegistry.DELTA_HUD, textX, textY, textSize, text, textColor);

        width = rectWidth;
        height = rectHeight;
        dragging.setWidth(width * scaleFactor);
        dragging.setHeight(height * scaleFactor);
    }
}
