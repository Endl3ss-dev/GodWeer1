package tech.lightclient.ui.hud.stafflist;

import tech.lightclient.client.ClientColors;
import tech.lightclient.client.events.impl.render.EventRender;
import tech.lightclient.client.utils.math.MathUtil;
import tech.lightclient.client.utils.render.drag.DraggingManager;
import tech.lightclient.client.utils.render.drag.api.Dragging;
import tech.lightclient.client.utils.render.main.text.FontRegistry;
import tech.lightclient.modules.impl.render.Interface;
import tech.lightclient.ui.hud.StaffEntry;
import tech.lightclient.ui.hud.api.HudElement;
import tech.lightclient.ui.hud.first.FirstHudTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StaffListFirst extends StaffListBase implements HudElement {
    private static final float STATUS_SIZE = 5.35f;
    private static final float NEAR_STATUS_SIZE = FirstHudTheme.ROW_TEXT_SIZE;
    private static final float FACE_SIZE = 8.7f;
    private static final int NEAR_COLOR = rgba(243, 207, 138, 255);
    private static final int ACTIVE_COLOR = rgba(155, 223, 178, 255);
    private static final int VANISH_COLOR = rgba(255, 142, 142, 255);

    public StaffListFirst(Dragging dragging) {
        super(dragging);
    }

    @Override
    public void render(EventRender.Screen.Hud event) {
        if (mc.player == null || mc.world == null) {
            return;
        }

        float startScale = Interface.isBounceAnimation() ? 0f : 0.8f;
        float scaleFactor = Interface.getHudScale(SETTINGS_SCOPE);
        float x = Interface.scalePos(SETTINGS_SCOPE, dragging.getX());
        float y = Interface.scalePos(SETTINGS_SCOPE, dragging.getY());
        applyDragDelta(x, y);

        if (System.currentTimeMillis() - lastScanTime >= 1000L) {
            scanStaff();
            lastScanTime = System.currentTimeMillis();
        }

        float dt = updateAnimTime();
        List<String> visible = new ArrayList<>();
        List<String> activeList = new ArrayList<>();
        List<String> keys = new ArrayList<>(staffEntries.keySet());
        keys.sort(Comparator.comparing(key -> staffEntries.get(key).name, String.CASE_INSENSITIVE_ORDER));

        float targetMaxWidth = getHeaderMinWidth();
        for (String key : keys) {
            StaffEntry entry = staffEntries.get(key);
            if (entry == null) {
                continue;
            }

            float anim = staffAnim.getOrDefault(key, 0f);
            anim = animate(anim, 1f, ITEM_ANIM_DURATION_MS, dt);
            if (anim <= 0.01f && !entry.online) {
                staffAnim.remove(key);
                staffY.remove(key);
                continue;
            }

            staffAnim.put(key, anim);
            activeList.add(key);
            visible.add(key);
            targetMaxWidth = Math.max(targetMaxWidth, getRowWidth(event, trimDisplayText(entry.displayText), entry));
        }

        for (String key : new ArrayList<>(staffAnim.keySet())) {
            if (activeList.contains(key)) {
                continue;
            }
            float anim = staffAnim.getOrDefault(key, 0f);
            anim = animate(anim, 0f, ITEM_ANIM_DURATION_MS, dt);
            if (anim <= 0.01f) {
                staffAnim.remove(key);
                staffY.remove(key);
                continue;
            }
            staffAnim.put(key, anim);
            visible.add(key);
            StaffEntry entry = staffEntries.get(key);
            if (entry != null) {
                targetMaxWidth = Math.max(targetMaxWidth, getRowWidth(event, trimDisplayText(entry.displayText), entry));
            }
        }

        if (Float.isNaN(width) || width == 0f) {
            width = getHeaderMinWidth();
        }
        width = animate(width, targetMaxWidth, ITEM_ANIM_DURATION_MS * 1.5f, dt);

        boolean chatOpen = MinecraftClient.getInstance().currentScreen instanceof ChatScreen;
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
        Map<String, Float> targetPositions = new HashMap<>();
        Map<String, Float> rowHeights = new HashMap<>();
        for (int i = 0; i < visible.size(); i++) {
            String key = visible.get(i);
            float anim = MathUtil.clamp(staffAnim.getOrDefault(key, 0f), 0f, 1f);
            float rowHeight = FirstHudTheme.ROW_HEIGHT * anim;
            targetPositions.put(key, targetY);
            rowHeights.put(key, rowHeight);
            targetY += rowHeight + (i == visible.size() - 1 ? 0f : FirstHudTheme.ROW_GAP * anim);
        }

        float containerHeight = Math.max(FirstHudTheme.HEADER_HEIGHT + FirstHudTheme.SIDE_PADDING, targetY - y + FirstHudTheme.SIDE_PADDING);
        float scale = Interface.animatedScale(startScale, alpha, hiding);
        float centerX = x + width * 0.5f;
        float centerY = y + containerHeight * 0.5f;
        event.getRenderer().pushScale(scale, scale, centerX, centerY);

        FirstHudTheme.drawPanel(event, SETTINGS_SCOPE, x, y, width, containerHeight, alpha);
        FirstHudTheme.drawHeader(event, x, y, width, alpha, "w", "Staffs");

        float maxBottom = y + FirstHudTheme.HEADER_HEIGHT;
        for (int i = 0; i < visible.size(); i++) {
            String key = visible.get(i);
            float anim = staffAnim.getOrDefault(key, 0f);
            if (anim <= 0.01f) {
                continue;
            }
            StaffEntry entry = staffEntries.get(key);
            if (entry == null) {
                continue;
            }

            Float currentY = staffY.get(key);
            float targetRowY = targetPositions.getOrDefault(key, listStartY);
            if (currentY == null) {
                currentY = targetRowY;
            }
            currentY = animate(currentY, targetRowY, 25f, dt);
            staffY.put(key, currentY);

            Text displayText = trimDisplayText(entry.displayText);
            float rowAlpha = Math.min(1f, anim) * alpha;
            float rowHeight = rowHeights.getOrDefault(key, FirstHudTheme.ROW_HEIGHT);
            String status = getStatusLabel(entry);
            float statusSize = getStatusSize(entry);
            int statusColor = getStatusColor(entry);
            float textY = currentY + rowHeight * 0.5f + FontRegistry.centeredBaselineOffset(FontRegistry.DELTA_HUD, 'H', FirstHudTheme.ROW_TEXT_SIZE) + 0.3f;
            float statusWidth = FontRegistry.DELTA_HUD.getWidth(status, statusSize);
            float statusX = x + width - FirstHudTheme.SIDE_PADDING - statusWidth;
            float faceX = x + FirstHudTheme.SIDE_PADDING;
            float faceY = currentY + (rowHeight - FACE_SIZE) * 0.5f;
            float dividerX = faceX + FACE_SIZE + 3.5f;
            float dividerY = currentY + rowHeight * 0.5f - 4f;
            float textX = dividerX + 4.4f;

            renderStaffHead(event, entry.name, faceX, faceY, FACE_SIZE, rowAlpha, 3.8f);
            event.getRenderer().rect(dividerX, dividerY, 0.8f, 8f, 0.3f, ClientColors.applyAlpha(FirstHudTheme.ROW_DIVIDER, rowAlpha));
            event.getRenderer().text(FontRegistry.DELTA_HUD, textX, textY, FirstHudTheme.ROW_TEXT_SIZE, displayText, ClientColors.applyAlpha(FirstHudTheme.TEXT_COLOR, rowAlpha));
            event.getRenderer().text(FontRegistry.DELTA_HUD, statusX, currentY + rowHeight * 0.5f + FontRegistry.centeredBaselineOffset(FontRegistry.DELTA_HUD, 'H', statusSize) + 0.2f, statusSize, status, ClientColors.applyAlpha(statusColor, rowAlpha));

            if (i < visible.size() - 1) {
                float separatorY = currentY + rowHeight + FirstHudTheme.ROW_GAP * 0.5f - 0.35f;
                event.getRenderer().rect(x + FirstHudTheme.SIDE_PADDING, separatorY, width - FirstHudTheme.SIDE_PADDING * 2f, 0.7f, 0.25f, ClientColors.applyAlpha(FirstHudTheme.SEPARATOR, rowAlpha));
            }
            maxBottom = Math.max(maxBottom, currentY + rowHeight);
        }

        event.getRenderer().popScale();

        height = Math.max(FirstHudTheme.HEADER_HEIGHT + FirstHudTheme.SIDE_PADDING, maxBottom - y + FirstHudTheme.SIDE_PADDING);
        dragging.setWidth(width * scaleFactor);
        dragging.setHeight(height * scaleFactor);
    }

    private float getHeaderMinWidth() {
        return FirstHudTheme.getHeaderMinWidth("Staffs", "w", 18f);
    }

    private float getRowWidth(EventRender.Screen.Hud event, Text displayText, StaffEntry entry) {
        String status = getStatusLabel(entry);
        float statusSize = getStatusSize(entry);
        return FirstHudTheme.SIDE_PADDING * 2f
                + FACE_SIZE
                + 8.7f
                + event.getRenderer().measureText(FontRegistry.DELTA_HUD, displayText, FirstHudTheme.ROW_TEXT_SIZE).width
                + FontRegistry.DELTA_HUD.getWidth(status, statusSize)
                + 18f;
    }

    private String getStatusLabel(StaffEntry entry) {
        if (!entry.online) {
            return "Vanish";
        }
        return isNearStaff(entry.name) ? "Near" : "Active";
    }

    private int getStatusColor(StaffEntry entry) {
        if (!entry.online) {
            return VANISH_COLOR;
        }
        return isNearStaff(entry.name) ? NEAR_COLOR : ACTIVE_COLOR;
    }

    private float getStatusSize(StaffEntry entry) {
        return entry.online && isNearStaff(entry.name) ? NEAR_STATUS_SIZE : STATUS_SIZE;
    }

    private boolean isNearStaff(String name) {
        if (name == null || mc.world == null || mc.player == null) {
            return false;
        }
        return mc.world.getPlayers().stream()
                .filter(player -> player != null && player.getName() != null)
                .anyMatch(player -> player.getName().getString().equalsIgnoreCase(name) && mc.player.distanceTo(player) <= 96f);
    }

    private void renderStaffHead(EventRender.Screen.Hud event, String name, float x, float y, float size, float alpha, float rounding) {
        Identifier skin = getStaffSkin(name);
        if (skin == null) {
            event.getRenderer().rect(x, y, size, size, rounding, ClientColors.applyAlpha(FirstHudTheme.PANEL_BG, alpha));
            return;
        }

        int color = ClientColors.applyAlpha(Color.WHITE.getRGB(), alpha);
        event.getRenderer().drawTextureRegionRounded(skin, x, y, size, size, 8f / 64f, 8f / 64f, 16f / 64f, 16f / 64f, color, rounding);
        event.getRenderer().drawTextureRegionRounded(skin, x, y, size, size, 40f / 64f, 8f / 64f, 48f / 64f, 16f / 64f, color, rounding);
    }

    private Identifier getStaffSkin(String name) {
        if (name == null || mc.getNetworkHandler() == null) {
            return null;
        }
        for (PlayerListEntry entry : mc.getNetworkHandler().getPlayerList()) {
            if (entry == null || entry.getProfile() == null || entry.getProfile().name() == null) {
                continue;
            }
            if (entry.getProfile().name().equalsIgnoreCase(name) && entry.getSkinTextures() != null
                    && entry.getSkinTextures().body().texturePath() != null) {
                return entry.getSkinTextures().body().texturePath();
            }
        }
        return null;
    }

    private static int rgba(int r, int g, int b, int a) {
        return new Color(r, g, b, a).getRGB();
    }
}
