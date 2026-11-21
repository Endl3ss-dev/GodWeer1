package tech.lightclient.ui.hud.star;

import tech.lightclient.client.events.impl.render.EventRender;
import tech.lightclient.client.utils.render.main.core.Renderer2D;
import tech.lightclient.client.utils.render.main.text.FontObject;
import tech.lightclient.client.utils.render.main.text.FontRegistry;
import net.minecraft.util.Identifier;

import tech.lightclient.modules.api.Function;

public abstract class HudListElement extends HudElement {
    public static final float MIN_WIDTH = 92.0f;
    /** Зазор между шапкой и отдельным прямоугольником со строками. */
    public static final float GAP = 3.0f;
    /** Зазор между строками — вдвое меньше зазора под шапкой. */
    public static final float ROW_GAP = GAP / 2.0f;
    /** Вертикальное смещение первой строки относительно верха элемента. */
    protected static final float ROWS_TOP = HEADER_HEIGHT + GAP;

    protected HudListElement(Function module, String scope, String iconPath, float defaultX, float defaultY) {
        super(module, scope, iconPath, defaultX, defaultY);
    }

    @Override
    protected void draw(EventRender.Screen.Hud event, float alpha) {
        Renderer2D r = event.getRenderer();

        // Шапка — самостоятельный прямоугольник. Текст центрируется по HEADER_HEIGHT,
        // поэтому отступы сверху и снизу одинаковы, а жирная линия снизу убрана.
        drawClientRect(r, scope(), x, y, width, HEADER_HEIGHT, rounding(), alpha, dragging.isDragging());
        r.pushRoundedClipRect((int) x, (int) y, (int) Math.ceil(width), (int) Math.ceil(HEADER_HEIGHT),
                rounding(), rounding(), rounding(), rounding());
        try {
            FontObject titleFont = FontRegistry.DELTA_HUD;
            float titleSize = 7.0f;
            float baseline = StarUtil.textBaseline(titleFont, 'H', y, HEADER_HEIGHT, titleSize);
            r.text(titleFont, x + 7.0f, baseline, titleSize, title(),
                    StarUtil.text(alpha));

            Identifier icon = StarUtil.id(iconPath());
            float iconSize = 8.7f;
            float iconX = x + width - iconSize - 6.5f;
            float iconY = y + (HEADER_HEIGHT - iconSize) / 2.0f;

            // Яркое радиальное свечение под иконкой шапки — цветом темы клиента.
            float glowRadius = iconSize * 0.85f;
            drawIconGlow(r, iconX + iconSize / 2.0f, iconY + iconSize / 2.0f, glowRadius, alpha);

            r.drawTexture(icon, iconX, iconY, iconSize, iconSize,
                    accent(alpha));
        } finally {
            r.popClipRect();
        }

        // Каждая строка рисуется собственным прямоугольником — см. drawRows.
        // Шапка и строки, а также строки между собой не соединяются.
        drawRows(event, alpha);
    }

    /** Рисует прямоугольник-подложку одной строки списка тех же габаритов, что и шапка. */
    protected void drawRow(Renderer2D r, float rowY, float rowHeight, float alpha) {
        drawClientRect(r, scope(), x, rowY, width, rowHeight, rounding(), alpha, dragging.isDragging());
    }

    protected abstract void drawRows(EventRender.Screen.Hud event, float alpha);

    protected String title() {
        return scope();
    }
}
