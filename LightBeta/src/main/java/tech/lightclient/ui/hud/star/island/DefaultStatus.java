package tech.lightclient.ui.hud.star.island;

import tech.lightclient.client.ClientColors;
import tech.lightclient.client.events.impl.render.EventRender;
import tech.lightclient.client.utils.render.main.core.Renderer2D;
import tech.lightclient.client.utils.render.main.text.FontRegistry;
import tech.lightclient.ui.hud.star.StarUtil;

public class DefaultStatus extends IslandStatus {
    public static final String NAME = "Light Beta";

    /** Светло-серое свечение под логотипом. */
    private static final int LOGO_GLOW = 0xFFC8C8C8;

    public DefaultStatus() {
        super("default", "Вотермарка");
    }

    @Override
    public void draw(EventRender.Screen.Hud event, Renderer2D r, IslandElement island, float x, float y, float alpha) {
        width = 20.0f + FontRegistry.DELTA_HUD.getWidth(NAME, 7.0f);
        height = 15.0f;

        float cx = x - 6.0f + 10.0f * animation.getValue() + 3.5f + 3.0f - 2.0f;
        float cy = y + height / 2.0f - 1.0f;

        // Свечение фона от логотипа.
        float size = 15.0f;
        r.drawTexture(StarUtil.id("images/etc/glow.png"),
                cx - size / 2.0f, cy - size / 2.0f, size, size,
                ClientColors.applyAlpha(LOGO_GLOW, 0.35f * alpha));

        // Логотип клиента. Увеличен для чёткой видимости, но с отступом от краёв острова.
        float logoSize = 11.0f;
        r.drawTexture(StarUtil.id("images/etc/island_logo.png"),
                cx - logoSize / 2.0f, cy - logoSize / 2.0f, logoSize, logoSize, StarUtil.white(alpha));

        r.text(FontRegistry.DELTA_HUD, x + 25.0f - 10.0f * animation.getValue(),
                StarUtil.textBaseline(FontRegistry.DELTA_HUD, 'H', y + 1.5f, 12.0f, 7.0f),
                7.0f, NAME, StarUtil.text(alpha));
    }

    @Override
    public int getColorArgb() {
        return LOGO_GLOW;
    }

    @Override
    public boolean canShow() {
        return true;
    }
}
