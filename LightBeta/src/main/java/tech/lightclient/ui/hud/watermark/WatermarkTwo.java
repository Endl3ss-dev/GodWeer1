package tech.lightclient.ui.hud.watermark;

import com.adl.nativeprotect.User;

import tech.lightclient.LightClient;
import tech.lightclient.client.ClientColors;
import tech.lightclient.client.events.impl.render.EventRender;
import tech.lightclient.client.utils.render.drag.DraggingManager;
import tech.lightclient.client.utils.render.drag.api.Dragging;
import tech.lightclient.client.utils.render.main.text.FontRegistry;
import tech.lightclient.modules.impl.render.Interface;
import tech.lightclient.ui.hud.HudShadowRenderer;
import tech.lightclient.ui.hud.api.HudElement;

import java.util.List;

public class WatermarkTwo extends WatermarkBase implements HudElement {

    public WatermarkTwo(Dragging dragging) {
        super(dragging);
    }

    @Override
    public void render(EventRender.Screen.Hud event) {
        float scaleFactor = Interface.getHudScale(SETTINGS_SCOPE);
        float x = Interface.scalePos(SETTINGS_SCOPE, dragging.getX());
        float y = Interface.scalePos(SETTINGS_SCOPE, dragging.getY());

        String user = User.getInstance().profile("username") == null || User.getInstance().profile("username").isEmpty() ? "unknown" : User.getInstance().profile("username");
        List<WatermarkEntry> entries = buildVisibleEntries(user);
        List<WatermarkLine> lines = toLines(entries);
        List<RenderLine> renderLines = buildRenderLines(lines);

        float iconSize = 18f;
        float rectHeight = 29f;
        float spacing = 1f;
        float iconTextPadding = 1.5f;
        float contentPaddingLeft = 5f;
        float contentPaddingRight = 6f;
        float separatorWidth = 1.5f;
        float separatorHeight = rectHeight * 0.4f;
        float separatorMargin = 6f;

        float logoIconSize = 24f;
        String logoIcon = "L";
        float logoIconW = FontRegistry.ICONS_NEXIS.getWidth(logoIcon, logoIconSize);
        float logoIconHOffset = logoIconSize;

        float contentWidth = contentPaddingLeft + logoIconW + spacing + separatorMargin + separatorWidth + separatorMargin;
        for (int i = 0; i < renderLines.size(); i++) {
            RenderLine line = renderLines.get(i);
            if (i > 0) contentWidth += separatorMargin + separatorWidth + separatorMargin;
            contentWidth += FontRegistry.WEXSIDE_MENU_ICONS.getWidth(line.iconText, line.iconSize) + iconTextPadding + line.text.maxWidth;
        }
        contentWidth += contentPaddingRight;

        float totalWidth = contentWidth;

        float currentX = x;
        float currentY = y;

        boolean centered = DraggingManager.getHudBoolean(SETTINGS_SCOPE, SETTING_CENTERING, false);
        if (centered) {
            float scaledW = (float) mc.getWindow().getWidth() / scaleFactor;
            currentX = (scaledW - totalWidth) * 0.5f;
            float targetY = 13f + getBossBarOffset(scaleFactor);
            animatedCenteredY = animateTowards(animatedCenteredY, targetY, 0.2f);
            currentY = animatedCenteredY;
            dragging.setX(currentX * scaleFactor);
            dragging.setY(currentY * scaleFactor);
        } else {
            animatedCenteredY = Float.NaN;
        }

        int panelColor = ClientColors.applyAlpha(ClientColors.BACKGROUND.getRGB(), 0.74f);
        int separatorColor = ClientColors.applyAlpha(0xFFC3C3C3, 0.5f);

        HudShadowRenderer.render(event.getRenderer(), SETTINGS_SCOPE, currentX, currentY, contentWidth, rectHeight, 10, 1f, Interface.getHudScale(SETTINGS_SCOPE));
        Interface.renderHudBackground(event.getRenderer(), currentX, currentY, contentWidth, rectHeight, 10, 1f);
        if (!Interface.isHudBlurBackground()) event.getRenderer().rect(currentX, currentY, contentWidth, rectHeight, 10, panelColor);
        float baselineY = currentY + (rectHeight - iconSize) / 2 + FontRegistry.centeredBaselineOffset(FontRegistry.DELTA_HUD, 'H', 17f) + 9.5f;
        float cursorX = currentX + contentPaddingLeft;

        float logoIconX = cursorX + 2;
        float logoIconY = currentY + (rectHeight - logoIconHOffset) / 2 + FontRegistry.centeredBaselineOffset(FontRegistry.ICONS_NEXIS, 'H', logoIconSize);
        event.getRenderer().text(FontRegistry.ICONS_NEXIS, logoIconX, logoIconY + 12, logoIconSize, logoIcon, ClientColors.ICON.getRGB());
        cursorX += logoIconW + spacing;

        cursorX += separatorMargin;
        event.getRenderer().rect(cursorX, currentY + (rectHeight - separatorHeight) / 1.8f, separatorWidth, separatorHeight, 0, separatorColor);
        cursorX += separatorWidth + separatorMargin;

        for (int i = 0; i < renderLines.size(); i++) {
            RenderLine line = renderLines.get(i);
            if (i > 0) {
                cursorX += separatorMargin;
                event.getRenderer().rect(cursorX, currentY + (rectHeight - separatorHeight) / 1.8f, separatorWidth, separatorHeight, 0, separatorColor);
                cursorX += separatorWidth + separatorMargin;
            }
            event.getRenderer().text(FontRegistry.WEXSIDE_MENU_ICONS, cursorX, baselineY + line.iconYOffset, line.iconSize, line.iconText, ClientColors.ICON.getRGB());
            cursorX += FontRegistry.WEXSIDE_MENU_ICONS.getWidth(line.iconText, line.iconSize) + iconTextPadding;
            renderAnimatedText(event, cursorX, baselineY, line.text);
            cursorX += line.text.maxWidth;
        }

        width = totalWidth;
        height = rectHeight;

        dragging.setWidth(width * scaleFactor);
        dragging.setHeight(height * scaleFactor);
    }
}

