package fun.volon.client.modules.impl.render.base.implement;

import net.minecraft.client.util.math.MatrixStack;
import fun.volon.Volon;
import fun.volon.api.events.implement.EventRender;
import fun.volon.api.utils.color.ColorUtils;
import fun.volon.api.utils.draggable.Draggable;
import fun.volon.api.utils.render.RenderUtils;
import fun.volon.api.utils.render.fonts.msdf.Font;
import fun.volon.api.utils.render.fonts.msdf.Fonts;
import fun.volon.client.modules.impl.render.base.InterfaceProcessing;

import java.awt.Color;

/**
 * WaterMarkV2 — компактный ватермарк без главной иконки.
 * Показывает: "Evel" (градиент темы) + иконка FPS + значение + иконка Ping + значение + иконка TPS + значение
 */
public class WaterMarkV2 extends InterfaceProcessing {

    private static final float RADIUS   = 4f;
    private static final float PAD_X    = 5f;
    private static final float HEIGHT   = 15f;
    private static final float GAP      = 3f;   // зазор между блоками
    private static final float ICON_GAP = 2f;

    private static final int TEXT_WHITE = new Color(255, 255, 255, 255).getRGB();
    private static final int TEXT_GRAY  = new Color(160, 160, 160, 255).getRGB();

    public WaterMarkV2(Draggable draggable) {
        super(draggable);
    }

    @Override
    public void onRender(EventRender.Default eventRender) {
        MatrixStack m = eventRender.getContext().getMatrices();
        float x = draggable.getX();
        float y = draggable.getY();

        Font brandFont  = Fonts.getFont("suisse", 14);
        Font statsFont  = Fonts.getFont("suisse", 12);
        Font iconFont   = Fonts.getFont("iconnew", 14);  // иконки FPS/ping/TPS

        if (brandFont == null || statsFont == null) return;

        int time   = (int) ((System.currentTimeMillis() % 2000) / 2000f * 360f);
        int accent = getAccent();
        int bgColor = ColorUtils.rgba(8, 8, 10, 200);

        // ─── Данные ───────────────────────────────────────────────────────────
        String brandText  = "Evel";
        String fpsIcon    = "p";   // иконка fps
        String pingIcon   = "y";   // иконка ping
        String tpsIcon    = "b";   // иконка tps

        int fps = mc != null ? mc.getCurrentFps() : 0;
        int ping = 0;
        if (mc != null && mc.player != null && mc.getNetworkHandler() != null) {
            var entry = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
            if (entry != null) ping = entry.getLatency();
        }
        float tps = getTps();

        String fpsText  = fps  + "fps";
        String pingText = ping + "ms";
        String tpsText  = formatOneDecimal(tps) + "tps";

        // ─── Ширина блока "Evel" ──────────────────────────────────────────────
        float brandW = brandFont.getStringWidth(brandText);

        // ─── Ширина блока статистики ──────────────────────────────────────────
        float statsW = PAD_X;
        // fps
        if (iconFont != null) statsW += iconFont.getStringWidth(fpsIcon) + ICON_GAP;
        statsW += statsFont.getStringWidth(fpsText) + GAP;
        // ping
        if (iconFont != null) statsW += iconFont.getStringWidth(pingIcon) + ICON_GAP;
        statsW += statsFont.getStringWidth(pingText) + GAP;
        // tps
        if (iconFont != null) statsW += iconFont.getStringWidth(tpsIcon) + ICON_GAP;
        statsW += statsFont.getStringWidth(tpsText);
        statsW += PAD_X;

        float brandBlockW = PAD_X + brandW + PAD_X;
        float totalW = brandBlockW + GAP + statsW;

        // ─── Фон общий ────────────────────────────────────────────────────────
        RenderUtils.drawRoundedRect(m, x, y, totalW, HEIGHT, RADIUS, bgColor);

        // Тонкая обводка цветом темы
        int outlineAlpha = ColorUtils.setAlphaColor(accent, 180);
        RenderUtils.drawRoundedRectOutline(m, x, y, totalW, HEIGHT, RADIUS, RADIUS, RADIUS, RADIUS, 0.6f, outlineAlpha);

        // ─── Разделитель ──────────────────────────────────────────────────────
        float divX = x + brandBlockW + GAP * 0.5f;
        RenderUtils.drawRoundedRect(m, divX, y + 3f, 0.5f, HEIGHT - 6f, 0f,
                ColorUtils.rgba(60, 60, 65, 180));

        // ─── Текст "Evel" (градиент темы) ─────────────────────────────────────
        float textY    = y + (HEIGHT - 7f) * 0.5f + 0.5f;
        float brandX   = x + PAD_X;
        brandFont.drawGradientStringHorizontal(m, brandText, brandX, textY,
                ColorUtils.getThemeColor(time),
                ColorUtils.getThemeColor(time + 120));

        // ─── Статистика ───────────────────────────────────────────────────────
        float iconY    = y + (HEIGHT - 7f) * 0.5f + 0.7f;
        float drawX    = x + brandBlockW + GAP + PAD_X;

        // FPS
        drawX = drawStatItem(m, iconFont, statsFont, drawX, textY, iconY,
                fpsIcon, String.valueOf(fps), "fps", accent, time);
        drawX += GAP;

        // Ping
        drawX = drawStatItem(m, iconFont, statsFont, drawX, textY, iconY,
                pingIcon, String.valueOf(ping), "ms", accent, time);
        drawX += GAP;

        // TPS — цвет зависит от значения
        int tpsAccent = getTpsColor(tps, accent);
        drawX = drawStatItem(m, iconFont, statsFont, drawX, textY, iconY,
                tpsIcon, formatOneDecimal(tps), "tps", tpsAccent, time);

        // ─── Draggable размеры ────────────────────────────────────────────────
        draggable.setWidth(totalW);
        draggable.setHeight(HEIGHT);
    }

    /** Рисует иконку + числовое значение + суффикс. Возвращает новый drawX. */
    private float drawStatItem(MatrixStack m, Font iconFont, Font statsFont,
                               float x, float textY, float iconY,
                               String icon, String value, String suffix,
                               int accent, int time) {
        float curX = x;
        if (iconFont != null) {
            iconFont.drawGradientStringHorizontal(m, icon, curX, iconY,
                    ColorUtils.getThemeColor(time), ColorUtils.getThemeColor(time + 120));
            curX += iconFont.getStringWidth(icon) + ICON_GAP;
        }
        statsFont.drawString(m, value, curX, textY, TEXT_WHITE);
        curX += statsFont.getStringWidth(value);
        statsFont.drawString(m, suffix, curX - 0.5f, textY, accent);
        curX += statsFont.getStringWidth(suffix);
        return curX;
    }

    private static int getAccent() {
        if (Volon.INSTANCE == null || Volon.INSTANCE.themeStorage == null) return ColorUtils.getThemeColor();
        var theme = Volon.INSTANCE.themeStorage.getThemes().getTheme();
        if (theme == null) return ColorUtils.getThemeColor();
        if ("Rainbow".equals(theme.getName())) return ColorUtils.getThemeColor();
        return theme.color != null && theme.color.length > 0 ? theme.color[0] : ColorUtils.getThemeColor();
    }

    private static float getTps() {
        if (Volon.INSTANCE == null || Volon.INSTANCE.tpsCalc == null) return 20f;
        return Math.max(0f, Math.min(20f, Volon.INSTANCE.tpsCalc.getTPS()));
    }

    /** Зелёный ≥19, жёлтый ≥15, красный <15 */
    private static int getTpsColor(float tps, int accent) {
        if (tps >= 19f) return new Color(80, 220, 100, 255).getRGB();
        if (tps >= 15f) return new Color(240, 200, 60, 255).getRGB();
        return new Color(230, 70, 70, 255).getRGB();
    }

    private static String formatOneDecimal(float value) {
        int scaled = Math.round(value * 10f);
        return (scaled / 10) + "." + Math.abs(scaled % 10);
    }
}
