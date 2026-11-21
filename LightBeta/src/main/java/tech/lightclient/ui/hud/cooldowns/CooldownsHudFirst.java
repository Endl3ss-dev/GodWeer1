package tech.lightclient.ui.hud.cooldowns;

import tech.lightclient.client.ClientColors;
import tech.lightclient.client.events.impl.render.EventRender;
import tech.lightclient.client.utils.render.drag.DraggingManager;
import tech.lightclient.client.utils.render.drag.api.Dragging;
import tech.lightclient.client.utils.render.main.text.FontRegistry;
import tech.lightclient.modules.impl.render.Interface;
import tech.lightclient.ui.hud.CooldownsHud;
import tech.lightclient.ui.hud.api.HudElement;
import tech.lightclient.ui.hud.first.FirstHudTheme;
import net.minecraft.client.gui.screen.ChatScreen;

import java.util.ArrayList;
import java.util.List;

public class CooldownsHudFirst extends CooldownsHud implements HudElement {
    private static final float ITEM_SIZE = FirstHudTheme.scale(8.15f);
    private static final float ITEM_GAP = FirstHudTheme.scale(3.25f);
    private static final float DIVIDER_HEIGHT = FirstHudTheme.scale(8f);
    private static final float DIVIDER_WIDTH = FirstHudTheme.scale(0.8f);
    private static final float DIVIDER_RADIUS = FirstHudTheme.scale(0.3f);
    private static final float TEXT_GAP = FirstHudTheme.scale(4.4f);
    private static final float TEXT_Y_OFFSET = FirstHudTheme.scale(0.3f);
    private static final float SEPARATOR_OFFSET = FirstHudTheme.scale(0.35f);
    private static final float SEPARATOR_HEIGHT = FirstHudTheme.scale(0.7f);
    private static final float SEPARATOR_RADIUS = FirstHudTheme.scale(0.25f);
    private static final float ROW_WIDTH_GAP = FirstHudTheme.scale(8.45f);
    private static final float ROW_WIDTH_PADDING = FirstHudTheme.scale(16f);

    public CooldownsHudFirst(Dragging dragging) {
        super(dragging, false);
    }

    @Override
    public void render(EventRender.Screen.Hud event) {
        if (!prepare()) {
            return;
        }

        float startScale = Interface.isBounceAnimation() ? 0f : 0.8f;
        float scaleFactor = Interface.getHudScale(SETTINGS_SCOPE);
        float x = Interface.scalePos(SETTINGS_SCOPE, dragging.getX());
        float y = Interface.scalePos(SETTINGS_SCOPE, dragging.getY());
        float dt = updateAnimTime();

        boolean chatOpen = mc.currentScreen instanceof ChatScreen;
        List<CooldownEntry> allEntries = collectCooldowns();
        updateVisibility(allEntries);

        List<CooldownEntry> visible = new ArrayList<>();
        float targetWidth = getHeaderMinWidth();
        for (CooldownEntry entry : allEntries) {
            visible.add(entry);
            targetWidth = Math.max(targetWidth, getRowWidth(entry));
        }

        if (Float.isNaN(width) || width == 0f) {
            width = getHeaderMinWidth();
        }
        width = animate(width, targetWidth, 300f, dt);

        boolean shouldShow = DraggingManager.getHudBoolean(SETTINGS_SCOPE, SETTING_ALWAYS_SHOW, false) || chatOpen || !visible.isEmpty();
        animation.setDuration(Interface.getAlphaDurationMs());
        if (shouldShow) {
            animation.show();
        } else {
            animation.hide();
        }

        float alpha = animation.getProgress();
        boolean hiding = animation.getTarget() == 0f;
        if (alpha <= 0f && animation.get() == 0) {
            width = 0f;
            height = 0f;
            dragging.setWidth(0f);
            dragging.setHeight(0f);
            return;
        }

        float listStartY = y + FirstHudTheme.HEADER_HEIGHT + FirstHudTheme.ROW_GAP;
        float contentHeight = visible.isEmpty()
                ? FirstHudTheme.HEADER_HEIGHT + FirstHudTheme.SIDE_PADDING
                : FirstHudTheme.HEADER_HEIGHT + FirstHudTheme.SIDE_PADDING + visible.size() * FirstHudTheme.ROW_HEIGHT + Math.max(0, visible.size() - 1) * FirstHudTheme.ROW_GAP;

        float scale = Interface.animatedScale(startScale, alpha, hiding);
        float centerX = x + width * 0.5f;
        float centerY = y + contentHeight * 0.5f;

        event.getRenderer().pushScale(scale, scale, centerX, centerY);

        FirstHudTheme.drawPanel(event, SETTINGS_SCOPE, x, y, width, contentHeight, alpha);
        FirstHudTheme.drawHeader(event, x, y, width, alpha, "f", "Cooldowns");

        float maxBottom = y + FirstHudTheme.HEADER_HEIGHT;
        for (int i = 0; i < visible.size(); i++) {
            CooldownEntry entry = visible.get(i);
            float rowY = listStartY + i * (FirstHudTheme.ROW_HEIGHT + FirstHudTheme.ROW_GAP);
            float rowCenterY = rowY + FirstHudTheme.ROW_HEIGHT * 0.5f;
            float itemX = x + FirstHudTheme.SIDE_PADDING;
            float itemY = rowY + (FirstHudTheme.ROW_HEIGHT - ITEM_SIZE) * 0.5f;
            float dividerX = itemX + ITEM_SIZE + ITEM_GAP;
            float dividerY = rowCenterY - DIVIDER_HEIGHT * 0.5f;
            float textX = dividerX + TEXT_GAP;
            String time = formatSeconds(entry.seconds());
            float timeWidth = FontRegistry.DELTA_HUD.getWidth(time, FirstHudTheme.ROW_TEXT_SIZE);
            float timeX = x + width - FirstHudTheme.SIDE_PADDING - timeWidth;
            float textY = rowCenterY + FontRegistry.centeredBaselineOffset(FontRegistry.DELTA_HUD, 'H', FirstHudTheme.ROW_TEXT_SIZE) + TEXT_Y_OFFSET;

            renderVanillaItem(entry.stack(), itemX, itemY, ITEM_SIZE, scaleFactor);
            event.getRenderer().rect(
                    dividerX,
                    dividerY,
                    DIVIDER_WIDTH,
                    DIVIDER_HEIGHT,
                    DIVIDER_RADIUS,
                    ClientColors.applyAlpha(FirstHudTheme.ROW_DIVIDER, alpha)
            );
            event.getRenderer().text(
                    FontRegistry.DELTA_HUD,
                    textX,
                    textY,
                    FirstHudTheme.ROW_TEXT_SIZE,
                    entry.stack().getName(),
                    ClientColors.applyAlpha(FirstHudTheme.TEXT_COLOR, alpha)
            );
            event.getRenderer().text(
                    FontRegistry.DELTA_HUD,
                    timeX,
                    textY,
                    FirstHudTheme.ROW_TEXT_SIZE,
                    time,
                    ClientColors.applyAlpha(FirstHudTheme.BRIGHT_MUTED_TEXT_COLOR, alpha)
            );

            if (i < visible.size() - 1) {
                float separatorY = rowY + FirstHudTheme.ROW_HEIGHT + FirstHudTheme.ROW_GAP * 0.5f - SEPARATOR_OFFSET;
                event.getRenderer().rect(
                        x + FirstHudTheme.SIDE_PADDING,
                        separatorY,
                        width - FirstHudTheme.SIDE_PADDING * 2f,
                        SEPARATOR_HEIGHT,
                        SEPARATOR_RADIUS,
                        ClientColors.applyAlpha(FirstHudTheme.SEPARATOR, alpha)
                );
            }

            maxBottom = Math.max(maxBottom, rowY + FirstHudTheme.ROW_HEIGHT);
        }

        event.getRenderer().popScale();

        height = Math.max(FirstHudTheme.HEADER_HEIGHT + FirstHudTheme.SIDE_PADDING, maxBottom - y + FirstHudTheme.SIDE_PADDING);
        dragging.setWidth(width * scaleFactor);
        dragging.setHeight(height * scaleFactor);
    }

    private float getHeaderMinWidth() {
        return FirstHudTheme.getHeaderMinWidth("Cooldowns", "f", 20f);
    }

    private float getRowWidth(CooldownEntry entry) {
        String time = formatSeconds(entry.seconds());
        return FirstHudTheme.SIDE_PADDING * 2f
                + ITEM_SIZE
                + ROW_WIDTH_GAP
                + FontRegistry.DELTA_HUD.getWidth(entry.stack().getName(), FirstHudTheme.ROW_TEXT_SIZE)
                + FontRegistry.DELTA_HUD.getWidth(time, FirstHudTheme.ROW_TEXT_SIZE)
                + ROW_WIDTH_PADDING;
    }
}
