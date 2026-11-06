package tech.lightclient.ui.hud.star.impl;

import java.util.List;

import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.util.Identifier;

import tech.lightclient.client.ClientColors;
import tech.lightclient.client.events.impl.render.EventRender;
import tech.lightclient.client.utils.render.main.core.Renderer2D;
import tech.lightclient.client.utils.render.main.text.FontRegistry;
import tech.lightclient.modules.api.Function;
import tech.lightclient.modules.impl.render.Interface;
import tech.lightclient.ui.hud.CustomBossBarHud;
import tech.lightclient.ui.hud.NotificationsOverlay;
import tech.lightclient.ui.hud.star.HudElement;
import tech.lightclient.ui.hud.star.StarUtil;

/**
 * Нотификации в стиле нового HUD: центрированные «пиллы» с иконкой в
 * акцентном боксе. Таймер истечения — тающий по кругу индикатор проекта.
 */
public class NotifyElement extends HudElement {
    private static final float PILL_H = 18.0f;
    private static final float GAP_Y = 3.0f;
    private static final float PAD_X = 7.0f;
    private static final float ICON_PAD = 5.0f;
    private static final float ICON_BOX = 10.0f;
    private static final float ICON_SIZE = 7.0f;
    private static final float TEXT_SIZE = 7.0f;
    private static final float ROUNDING = 5.3f;
    private static final float RING_RADIUS = 7.5f;

    private static final String PREFIX_ENABLED = "Активировано ";
    private static final String PREFIX_DISABLED = "Деактивировано ";

    public NotifyElement(Function module) {
        super(module, "Star Нотификации", "images/hud/notify.png", 0.0f, 30.0f);
    }

    @Override
    protected void update(EventRender.Screen.Hud event) {
        List<NotificationsOverlay.Notification> notifications = NotificationsOverlay.activeNotifications();
        float totalHeight = PILL_H;
        for (NotificationsOverlay.Notification notification : notifications) {
            float anim = updateAnimation(notification, notifications);
            totalHeight += (PILL_H + GAP_Y) * anim;
        }
        width = 60.0f;
        height = Math.max(PILL_H, totalHeight);
        float viewportWidth = mc.getWindow().getScaledWidth();
        x = viewportWidth / 2.0f - width / 2.0f;
        y = Interface.scalePos(scope(), dragging.getY()) + getBossBarOffset();
        setSelfVisible(showNotifications());
    }

    @Override
    protected void draw(EventRender.Screen.Hud event, float alpha) {
        List<NotificationsOverlay.Notification> notifications = NotificationsOverlay.activeNotifications();
        if (notifications.isEmpty()) {
            return;
        }
        Renderer2D r = event.getRenderer();
        float viewportWidth = mc.getWindow().getScaledWidth();
        float offsetY = 0.0f;

        for (NotificationsOverlay.Notification notification : notifications) {
            float anim = updateAnimation(notification, notifications);
            if (anim <= 0.02f) {
                continue;
            }
            float rowAlpha = alpha * anim;
            String text = notification.getText();
            float textWidth = FontRegistry.DELTA_HUD.getWidth(text, TEXT_SIZE);
            float pillWidth = ICON_PAD + ICON_BOX + 6.0f + textWidth + PAD_X;
            float slide = (1.0f - anim) * -6.0f;
            float pillX = viewportWidth / 2.0f - pillWidth / 2.0f;
            float pillY = y + offsetY + slide;

            drawClientRect(r, scope(), pillX, pillY, pillWidth, PILL_H, ROUNDING, rowAlpha, dragging.isDragging());

            r.pushRoundedClipRect((int) pillX, (int) pillY, (int) Math.ceil(pillWidth), (int) Math.ceil(PILL_H),
                    ROUNDING, ROUNDING, ROUNDING, ROUNDING);
            try {
                // Акцентный бокс под иконкой.
                float iconBoxX = pillX + ICON_PAD;
                float iconBoxY = pillY + (PILL_H - ICON_BOX) / 2.0f;
                r.rect(iconBoxX, iconBoxY, ICON_BOX, ICON_BOX, 3.2f,
                        ClientColors.applyAlpha(ClientColors.GRADIENT_START.getRGB(), 0.22f * rowAlpha));

                Identifier icon = iconOf(notification);
                if (icon != null) {
                    r.drawTexture(icon, iconBoxX + (ICON_BOX - ICON_SIZE) / 2.0f,
                            iconBoxY + (ICON_BOX - ICON_SIZE) / 2.0f, ICON_SIZE, ICON_SIZE,
                            StarUtil.text(rowAlpha));
                }

                // Таймер проекта: тающий по кругу индикатор вокруг иконки.
                int ringColor = notification.getColorArgb();
                r.circleOutline(iconBoxX + ICON_BOX / 2.0f, iconBoxY + ICON_BOX / 2.0f, RING_RADIUS,
                        -90.0f, notification.getProgress(),
                        ClientColors.applyAlpha(ringColor, rowAlpha), 1.0f);

                // Текст: префикс цветом текста, остальное — акцентом клиента.
                drawStyledText(r, pillX + ICON_PAD + ICON_BOX + 6.0f, pillY, text, rowAlpha);
            } finally {
                r.popClipRect();
            }

            offsetY += (PILL_H + GAP_Y) * anim;
        }
    }

    private float updateAnimation(NotificationsOverlay.Notification notification,
                                  List<NotificationsOverlay.Notification> notifications) {
        boolean fading = notification.getProgress() <= 0.0f;
        return notification.getShowing().update(fading ? 0.0f : 1.0f);
    }

    private void drawStyledText(Renderer2D r, float textX, float pillY, String text, float rowAlpha) {
        float baseline = StarUtil.textBaseline(FontRegistry.DELTA_HUD, 'H', pillY, PILL_H, TEXT_SIZE);
        String prefix = null;
        if (text.startsWith(PREFIX_ENABLED)) {
            prefix = PREFIX_ENABLED;
        } else if (text.startsWith(PREFIX_DISABLED)) {
            prefix = PREFIX_DISABLED;
        }
        if (prefix == null) {
            r.text(FontRegistry.DELTA_HUD, textX, baseline, TEXT_SIZE, text, StarUtil.text(rowAlpha));
            return;
        }
        String suffix = text.substring(prefix.length());
        r.text(FontRegistry.DELTA_HUD, textX, baseline, TEXT_SIZE, prefix, StarUtil.text(rowAlpha));
        float px = textX + FontRegistry.DELTA_HUD.getWidth(prefix, TEXT_SIZE);
        r.text(FontRegistry.DELTA_HUD, px, baseline, TEXT_SIZE, suffix, accent(rowAlpha));
    }

    private Identifier iconOf(NotificationsOverlay.Notification notification) {
        return switch (notification.getKind()) {
            case ON -> StarUtil.id("images/hud/on.png");
            case OFF -> StarUtil.id("images/hud/off.png");
            case COOLDOWN -> StarUtil.id("images/hud/warning.png");
            case PICKUP -> StarUtil.id("icon.png");
            default -> StarUtil.id("images/hud/notify.png");
        };
    }

    private boolean showNotifications() {
        return !NotificationsOverlay.activeNotifications().isEmpty()
                || mc.currentScreen instanceof ChatScreen;
    }

    @Override
    public boolean show() {
        return showNotifications();
    }

    @Override
    public String settingName() {
        return "Нотификации";
    }

    private float getBossBarOffset() {
        float scaleFactor = Interface.getHudScale(scope());
        if (CustomBossBarHud.shouldUseCustomBossBar()) {
            return CustomBossBarHud.getOccupiedHeight() / Math.max(scaleFactor, 0.0001f);
        }
        if (mc.inGameHud == null || mc.inGameHud.getBossBarHud() == null) {
            return 0.0f;
        }
        int bossBarCount = mc.inGameHud.getBossBarHud().bossBars.size();
        if (bossBarCount <= 0) {
            return 0.0f;
        }
        return (bossBarCount * 19.0f + 8.0f) / Math.max(scaleFactor, 0.0001f);
    }
}
