package tech.lightclient.ui.hud.keybinds;

import tech.lightclient.client.ClientColors;
import tech.lightclient.client.events.impl.render.EventRender;
import tech.lightclient.client.utils.render.drag.DraggingManager;
import tech.lightclient.client.utils.render.drag.api.Dragging;
import tech.lightclient.client.utils.render.main.text.FontRegistry;
import tech.lightclient.modules.impl.render.Interface;
import tech.lightclient.ui.hud.api.HudElement;
import tech.lightclient.ui.hud.first.FirstHudTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KeyBindsFirst extends KeyBindsBase implements HudElement {
    private static final float CATEGORY_ICON_SIZE = 8f;
    private static final float CATEGORY_ICON_Y_OFFSET = 7.2f;
    private static final int CATEGORY_SEPARATOR = rgba(94, 94, 100, 118);

    public KeyBindsFirst(Dragging dragging) {
        super(dragging);
    }

    @Override
    public void render(EventRender.Screen.Hud event) {
        float startScale = Interface.isBounceAnimation() ? 0f : 0.8f;
        float scaleFactor = Interface.getHudScale(SETTINGS_SCOPE);
        float x = Interface.scalePos(SETTINGS_SCOPE, dragging.getX());
        float y = Interface.scalePos(SETTINGS_SCOPE, dragging.getY());
        applyDragDelta(x, y);

        float dt = updateAnimTime();
        boolean chatOpen = MinecraftClient.getInstance().currentScreen instanceof ChatScreen;
        List<BindEntry> allEntries = collectAllEntries();
        List<BindEntry> visible = new ArrayList<>();
        List<BindEntry> activeList = new ArrayList<>();

        float targetMaxWidth = getHeaderMinWidth();
        for (BindEntry entry : allEntries) {
            boolean active = entry.active;
            float anim = bindAnim.getOrDefault(entry.key, 0f);
            anim = animate(anim, active ? 1f : 0f, ITEM_ANIM_DURATION_MS, dt);
            if (anim <= 0.01f && !active) {
                bindAnim.remove(entry.key);
                bindY.remove(entry.key);
                continue;
            }
            bindAnim.put(entry.key, anim);
            if (active) {
                activeList.add(entry);
            }
            if (anim > 0.01f) {
                targetMaxWidth = Math.max(targetMaxWidth, getRowWidth(entry));
                visible.add(entry);
            }
        }

        if (Float.isNaN(width) || width == 0f) {
            width = getHeaderMinWidth();
        }
        width = animate(width, targetMaxWidth, ITEM_ANIM_DURATION_MS * 1.5f, dt);

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
        float targetY = listStartY;
        Map<Object, Float> targetPositions = new HashMap<>();
        Map<Object, Float> rowHeights = new HashMap<>();
        for (BindEntry entry : visible) {
            float anim = Math.max(0f, Math.min(1f, bindAnim.getOrDefault(entry.key, 0f)));
            float rowHeight = FirstHudTheme.ROW_HEIGHT * anim;
            targetPositions.put(entry.key, targetY);
            rowHeights.put(entry.key, rowHeight);
            targetY += rowHeight + (visible.indexOf(entry) == visible.size() - 1 ? 0f : FirstHudTheme.ROW_GAP * anim);
        }

        float containerHeight = Math.max(FirstHudTheme.HEADER_HEIGHT + FirstHudTheme.SIDE_PADDING, targetY - y + FirstHudTheme.SIDE_PADDING);
        float scale = Interface.animatedScale(startScale, alpha, hiding);
        float centerX = x + width * 0.5f;
        float centerY = y + containerHeight * 0.5f;
        event.getRenderer().pushScale(scale, scale, centerX, centerY);

        FirstHudTheme.drawPanel(event, SETTINGS_SCOPE, x, y, width, containerHeight, alpha);
        FirstHudTheme.drawHeader(event, x, y, width, alpha, "k", "KeyBinds");

        float maxBottom = y + FirstHudTheme.HEADER_HEIGHT;
        for (int i = 0; i < visible.size(); i++) {
            BindEntry entry = visible.get(i);
            float anim = bindAnim.getOrDefault(entry.key, 0f);
            if (anim <= 0.01f) {
                continue;
            }

            Float currentY = bindY.get(entry.key);
            float targetRowY = targetPositions.getOrDefault(entry.key, listStartY);
            if (currentY == null) {
                currentY = targetRowY;
            }
            currentY = animate(currentY, targetRowY, 25f, dt);
            bindY.put(entry.key, currentY);

            float rowAlpha = Math.min(1f, anim) * alpha;
            float rowHeight = rowHeights.getOrDefault(entry.key, FirstHudTheme.ROW_HEIGHT);
            float textY = currentY + rowHeight * 0.5f + FontRegistry.centeredBaselineOffset(FontRegistry.DELTA_HUD, 'H', FirstHudTheme.ROW_TEXT_SIZE) + 0.3f;
            String keyName = getKeyName(entry.bind);
            float rightWidth = FontRegistry.DELTA_HUD.getWidth(keyName, FirstHudTheme.ROW_TEXT_SIZE);
            float rightX = x + width - FirstHudTheme.SIDE_PADDING - rightWidth;
            float iconX = x + FirstHudTheme.SIDE_PADDING;
            float iconY = currentY + rowHeight * 0.5f + FontRegistry.centeredBaselineOffset(FontRegistry.LIGHT_TTF, entry.categoryIcon.charAt(0), CATEGORY_ICON_SIZE) + CATEGORY_ICON_Y_OFFSET;
            float iconWidth = FontRegistry.LIGHT_TTF.getWidth(entry.categoryIcon, CATEGORY_ICON_SIZE);
            float separatorX = iconX + iconWidth + 4.2f;
            float separatorY = currentY + rowHeight * 0.5f - 4.1f;
            float aliasX = separatorX + 4.6f;
            event.getRenderer().text(FontRegistry.LIGHT_TTF, iconX, iconY, CATEGORY_ICON_SIZE, entry.categoryIcon, ClientColors.applyAlpha(FirstHudTheme.ICON_COLOR, rowAlpha));
            event.getRenderer().rect(separatorX, separatorY, 0.8f, 8.2f, 0.3f, ClientColors.applyAlpha(CATEGORY_SEPARATOR, rowAlpha));
            event.getRenderer().text(FontRegistry.DELTA_HUD, aliasX, textY, FirstHudTheme.ROW_TEXT_SIZE, entry.alias, ClientColors.applyAlpha(FirstHudTheme.TEXT_COLOR, rowAlpha));
            event.getRenderer().text(FontRegistry.DELTA_HUD, rightX, textY, FirstHudTheme.ROW_TEXT_SIZE, keyName, ClientColors.applyAlpha(Color.WHITE.getRGB(), rowAlpha));

            if (i < visible.size() - 1) {
                float rowSeparatorY = currentY + rowHeight + FirstHudTheme.ROW_GAP * 0.5f - 0.35f;
                event.getRenderer().rect(x + FirstHudTheme.SIDE_PADDING, rowSeparatorY, width - FirstHudTheme.SIDE_PADDING * 2f, 0.7f, 0.25f, ClientColors.applyAlpha(FirstHudTheme.SEPARATOR, rowAlpha));
            }
            maxBottom = Math.max(maxBottom, currentY + rowHeight);
        }

        event.getRenderer().popScale();

        height = Math.max(FirstHudTheme.HEADER_HEIGHT + FirstHudTheme.SIDE_PADDING, maxBottom - y + FirstHudTheme.SIDE_PADDING);
        dragging.setWidth(width * scaleFactor);
        dragging.setHeight(height * scaleFactor);
    }

    private float getHeaderMinWidth() {
        return FirstHudTheme.getHeaderMinWidth("KeyBinds", "k", 20f);
    }

    private float getRowWidth(BindEntry entry) {
        String keyName = getKeyName(entry.bind);
        return FirstHudTheme.SIDE_PADDING * 2f
                + FontRegistry.LIGHT_TTF.getWidth(entry.categoryIcon, CATEGORY_ICON_SIZE)
                + 8.8f
                + FontRegistry.DELTA_HUD.getWidth(entry.alias, FirstHudTheme.ROW_TEXT_SIZE)
                + FontRegistry.DELTA_HUD.getWidth(keyName, FirstHudTheme.ROW_TEXT_SIZE)
                + 20f;
    }

    private static int rgba(int r, int g, int b, int a) {
        return new Color(r, g, b, a).getRGB();
    }
}
