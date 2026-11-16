package tech.lightclient.ui.hud.star.island;

import java.util.List;

import tech.lightclient.client.ClientColors;
import tech.lightclient.client.events.impl.render.EventRender;
import tech.lightclient.client.utils.render.main.core.Renderer2D;
import tech.lightclient.client.utils.render.main.text.FontRegistry;
import tech.lightclient.ui.hud.NotificationsOverlay;
import tech.lightclient.ui.hud.star.StarUtil;

public class NotificationStatus extends IslandStatus {
    /** Светло-серое свечение, используется для логотипа. */
    private static final int LOGO_GLOW = 0xFFC8C8C8;

    private int glowColor = LOGO_GLOW;

    public NotificationStatus() {
        super("alerts", "Уведомления");
    }

    @Override
    public void draw(EventRender.Screen.Hud event, Renderer2D r, IslandElement island, float x, float y, float alpha) {
        List<NotificationsOverlay.Notification> notifications = NotificationsOverlay.activeNotifications();
        if (notifications.isEmpty()) {
            return;
        }

        NotificationsOverlay.Notification active = notifications.get(notifications.size() - 1);
        width = 20.0f + FontRegistry.DELTA_HUD.getWidth(active.getText(), 7.0f);
        height = 15.0f;

        // Свечение фона зависит от самого свежего уведомления.
        glowColor = active.isLogo() ? LOGO_GLOW : active.getColorArgb();

        for (NotificationsOverlay.Notification notification : notifications) {
            float showing = notification.getShowing().update(notification == active ? 1.0f : 0.0f);
            if (showing <= 0.001f) {
                continue;
            }
            float shift = 10.0f * animation.getValue() * showing;
            float cx = x - 6.0f + shift + 3.5f;
            float cy = y + 4.0f + 3.5f;
            float indicatorAlpha = alpha * showing;

            if (notification.isLogo()) {
                drawGlow(r, cx, cy, LOGO_GLOW, indicatorAlpha);
                r.drawTexture(StarUtil.id("images/etc/island_logo.png"),
                        cx - 3.5f, cy - 3.5f, 7.0f, 7.0f, StarUtil.white(indicatorAlpha));
            } else {
                int color = notification.getColorArgb();
                drawGlow(r, cx, cy, color, indicatorAlpha);
                // Обводка внутрь: внешний край совпадает с прежним кругом (r=3.5),
                // а полоса контура уходит внутрь. Таймер тает против часовой стрелки от верхней точки.
                r.circleOutline(cx, cy, 3.5f, -90.0f, notification.getProgress(),
                        ClientColors.applyAlpha(color, indicatorAlpha), 1.0f);
            }

            r.text(FontRegistry.DELTA_HUD, x + 25.0f - shift,
                    StarUtil.textBaseline(FontRegistry.DELTA_HUD, 'H', y + 1.5f, 12.0f, 7.0f),
                    7.0f, notification.getText(), StarUtil.text(indicatorAlpha));
        }
    }

    private void drawGlow(Renderer2D r, float cx, float cy, int color, float alpha) {
        float size = 15.0f;
        r.drawTexture(StarUtil.id("images/etc/glow.png"),
                cx - size / 2.0f, cy - size / 2.0f, size, size,
                ClientColors.applyAlpha(color, 0.35f * alpha));
    }

    @Override
    public int getColorArgb() {
        return glowColor;
    }

    @Override
    public boolean canShow() {
        return NotificationsOverlay.hasActiveNotification();
    }
}
