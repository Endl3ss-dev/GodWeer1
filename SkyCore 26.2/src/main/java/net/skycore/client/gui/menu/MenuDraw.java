package net.skycore.client.gui.menu;

import net.skycore.client.gui.menu.MenuText.Weight;
import net.skycore.client.util.BeginFrameHelper;
import net.skycore.client.util.InterBoldHelper;
import net.skycore.client.util.TextHeightHelper;

/**
 * Примитивы отрисовки меню ClickGUI.
 *
 * <p>Порт {@code swerve.ui.menu.Px} (Swerve, порт Arcanic Visuals, GPL-3.0), переписанный
 * на отрисовщик SkyCore. Разница принципиальная и осознанная:
 *
 * <ul>
 *   <li>В Swerve всё рисовалось спрайтами из собственного атласа
 *       ({@code atlas.png}, {@code glow.png}, {@code chamfer.png}) плюс четыре
 *       вспомогательных шейдера ({@code gradient6}, {@code fonts}, {@code menubg}).
 *       SkyCore рисует закруглённые прямоугольники, границы и тени штатными
 *       пайплайнами ({@code GuiPipelines.RECT}, {@code GLASS_SHADOW},
 *       {@code BLUR_RECT}) через {@link BeginFrameHelper}. Поэтому {@code round},
 *       {@code outline} и {@code shadow} выражаются одним прямоугольником с радиусом,
 *       границей и тенью — без атласа, без кэша RenderType и без лишних квадов.</li>
 *   <li>Координаты — в GUI-пикселях, а не в пикселях окна: масштаб GUI уже применён
 *       движком, и пересчёт через {@code window.getWidth()} в 26.2 не нужен.</li>
 *   <li>Иконки берутся из иконочных шрифтов клиента: {@code csgui-icons} для
 *       именованных глифов ({@code IconGlyphs}) и {@code clickgui-icons} для
 *       буквенных ({@code LegacyIconGlyphs}). Атлас {@code kit/icons_<size>.png}
 *       из Swerve не переносится.</li>
 * </ul>
 *
 * <p>Все цвета проходят через {@link #fade}: это позволяет гасить целые блоки меню
 * одной строкой ({@code fade = ...}) при анимации появления, как в источнике.
 */
public final class MenuDraw {

    /** Общий множитель прозрачности: применяется ко всем примитивам. */
    public static float fade = 1.0F;

    private MenuDraw() {
    }

    // ---- служебное

    /** Гасит цвет на {@link #fade}. */
    private static int f(int color) {
        return fade >= 1.0F ? color : am(color, fade);
    }

    private static boolean visible(float w, float h, int color) {
        return w > 0.0F && h > 0.0F && (color >>> 24) != 0;
    }

    // ---- прямоугольники

    public static void rect(float x, float y, float w, float h, int color) {
        color = f(color);
        if (!visible(w, h, color)) {
            return;
        }
        BeginFrameHelper.rect(x, y, w, h).color(color).hudGlow(false).draw();
    }

    public static void round(float x, float y, float w, float h, float radius, int color) {
        color = f(color);
        if (!visible(w, h, color)) {
            return;
        }
        BeginFrameHelper.rect(x, y, w, h)
                .radius(Math.max(0.0F, Math.min(radius, Math.min(w, h) * 0.5F)))
                .color(color)
                .hudGlow(false)
                .draw();
    }

    /** Скруглённый прямоугольник с горизонтальным градиентом. */
    public static void roundGrad(float x, float y, float w, float h, float radius, int left, int right) {
        left = f(left);
        right = f(right);
        if (w <= 0.0F || h <= 0.0F || ((left >>> 24) == 0 && (right >>> 24) == 0)) {
            return;
        }
        BeginFrameHelper.rect(x, y, w, h)
                .radius(Math.max(0.0F, Math.min(radius, Math.min(w, h) * 0.5F)))
                .color(left, right, right, left)
                .hudGlow(false)
                .draw();
    }

    /** Скруглённый прямоугольник с вертикальным градиентом. */
    public static void roundGradV(float x, float y, float w, float h, float radius, int top, int bottom) {
        top = f(top);
        bottom = f(bottom);
        if (w <= 0.0F || h <= 0.0F || ((top >>> 24) == 0 && (bottom >>> 24) == 0)) {
            return;
        }
        BeginFrameHelper.rect(x, y, w, h)
                .radius(Math.max(0.0F, Math.min(radius, Math.min(w, h) * 0.5F)))
                .color(top, top, bottom, bottom)
                .hudGlow(false)
                .draw();
    }

    /** Обводка скруглённого прямоугольника толщиной 1. */
    /**
     * Толщина обводки по умолчанию.
     *
     * <p>0.8, а не 1.0: весь остальной клиент рисует рамки в диапазоне 0.5–0.8
     * ({@code MouseClickedHelper}, {@code TextDescriptionLayout}), и на 1.0 обводка
     * карточек выглядела заметно толще, чем в остальном интерфейсе.
     */
    public static final float BORDER = 0.8F;

    public static void outline(float x, float y, float w, float h, float radius, int color) {
        outline(x, y, w, h, radius, BORDER, color);
    }

    /** Обводка скруглённого прямоугольника заданной толщины. */
    public static void outline(float x, float y, float w, float h, float radius, float thickness, int color) {
        color = f(color);
        if (w <= 0.0F || h <= 0.0F || (color >>> 24) == 0 || thickness <= 0.0F) {
            return;
        }
        BeginFrameHelper.rect(x, y, w, h)
                .radius(Math.max(0.0F, Math.min(radius, Math.min(w, h) * 0.5F)))
                .color(0)
                .border(thickness, color)
                .hudGlow(false)
                .draw();
    }

    public static void circle(float cx, float cy, float radius, int color) {
        if (radius <= 0.0F) {
            return;
        }
        round(cx - radius, cy - radius, radius * 2.0F, radius * 2.0F, radius, color);
    }

    public static void ring(float cx, float cy, float radius, float thickness, int color) {
        if (radius <= 0.0F) {
            return;
        }
        outline(cx - radius, cy - radius, radius * 2.0F, radius * 2.0F, radius, thickness, color);
    }

    /** Горизонтальный градиент без скругления. */
    public static void hgrad(float x, float y, float w, float h, int left, int right) {
        left = f(left);
        right = f(right);
        if (w <= 0.0F || h <= 0.0F || ((left >>> 24) == 0 && (right >>> 24) == 0)) {
            return;
        }
        BeginFrameHelper.rect(x, y, w, h)
                .color(left, right, right, left)
                .hudGlow(false)
                .draw();
    }

    /** Вертикальный градиент без скругления. */
    public static void vgrad(float x, float y, float w, float h, int top, int bottom) {
        top = f(top);
        bottom = f(bottom);
        if (w <= 0.0F || h <= 0.0F || ((top >>> 24) == 0 && (bottom >>> 24) == 0)) {
            return;
        }
        BeginFrameHelper.rect(x, y, w, h)
                .color(top, top, bottom, bottom)
                .hudGlow(false)
                .draw();
    }

    /** «Таблетка» с горизонтальным градиентом: радиус всегда равен половине высоты. */
    public static void pill(float x, float y, float w, float h, int left, int right) {
        roundGrad(x, y, w, h, h * 0.5F, left, right);
    }

    /**
     * Мягкая тень под прямоугольником: сам прямоугольник не заливается.
     *
     * <p>Радиус тени берётся тот же, что у элемента, иначе на скруглениях
     * появлялась бы прямоугольная «подложка».
     */
    public static void shadow(float x, float y, float w, float h, float radius, float blur, int color) {
        color = f(color);
        if (w <= 0.0F || h <= 0.0F || (color >>> 24) == 0 || blur <= 0.0F) {
            return;
        }
        BeginFrameHelper.rect(x, y, w, h)
                .radius(Math.max(0.0F, Math.min(radius, Math.min(w, h) * 0.5F)))
                .color(0)
                .shadow(color, blur)
                .hudGlow(false)
                .draw();
    }

    /** Свечение вокруг заливки: тень того же прямоугольника под ним. */
    public static void glow(float x, float y, float w, float h, float radius, int color) {
        shadow(x, y, w, h, radius, Math.max(4.0F, Math.min(w, h) * 0.35F), color);
    }

    // ---- области отсечения

    public static void scissor(float x, float y, float w, float h) {
        BeginFrameHelper.pushScissor(x, y, Math.max(0.0F, w), Math.max(0.0F, h));
    }

    public static void scissorEnd() {
        BeginFrameHelper.popScissor();
    }

    // ---- текст

    public static float textWidth(String text, Weight weight, float px) {
        return MenuText.width(text, weight, px);
    }

    /**
     * Верх строки для вертикального центрирования внутри бокса {@code [boxY, boxY + boxH]}.
     *
     * <p>Источник центрировал по высоте заглавной буквы собственного MSDF-шрифта.
     * Здесь центрируем по высоте строки того шрифта, которым рисуем.
     */
    public static float centeredY(float boxY, float boxH, Weight weight, float px) {
        return boxY + (boxH - MenuText.lineHeight(weight, px)) * 0.5F;
    }

    public static void text(String text, float x, float y, Weight weight, float px, int color) {
        MenuText.draw(text, x, y, weight, px, f(color));
    }

    /** Текст, выровненный по центру бокса по вертикали и по левому краю. */
    public static void textMid(String text, float x, float boxY, float boxH, Weight weight, float px, int color) {
        text(text, x, centeredY(boxY, boxH, weight, px), weight, px, color);
    }

    public static void textRight(String text, float right, float y, Weight weight, float px, int color) {
        text(text, right - textWidth(text, weight, px), y, weight, px, color);
    }

    public static void textCenter(String text, float cx, float y, Weight weight, float px, int color) {
        text(text, cx - textWidth(text, weight, px) * 0.5F, y, weight, px, color);
    }

    public static void textCenterMid(String text, float cx, float boxY, float boxH, Weight weight, float px,
                                     int color) {
        text(text, cx - textWidth(text, weight, px) * 0.5F, centeredY(boxY, boxH, weight, px), weight, px, color);
    }

    public static String ellipsize(String text, Weight weight, float px, float maxWidth) {
        return MenuText.ellipsize(text, weight, px, maxWidth);
    }

    // ---- иконки

    /** Глиф именованного иконочного шрифта ({@code csgui-icons}). */
    public static void iconCs(String glyph, float x, float y, float size, int color) {
        drawIcon(glyph, x, y, size, color, InterBoldHelper.csGuiIcons());
    }

    /** Глиф буквенного иконочного шрифта ({@code clickgui-icons}). */
    public static void iconLetters(String glyph, float x, float y, float size, int color) {
        drawIcon(glyph, x, y, size, color, InterBoldHelper.clickGuiIcons());
    }

    /**
     * Иконка, отцентрованная по точке.
     *
     * <p>Текст рисуется так, что базовая линия лежит в {@code y + ascender(size)}, а
     * сам значок занимает примерно ±0.5em вокруг середины высоты строки. Поэтому
     * верх квада для визуального центра {@code cy} считается как
     * {@code cy + size/2 - ascender(size)}. Центрирование по {@code lineHeight}
     * ошибочно: у clickgui-icons он равен 2em, из-за чего иконки уезжали вверх.
     */
    public static void iconCs(String glyph, float cx, float cy, float size, int color) {
        iconCentered(glyph, cx, cy, size, color, InterBoldHelper.csGuiIcons());
    }

    /** Буквенная иконка по центру точки; см. {@link #iconCs}. */
    public static void iconLetters(String glyph, float cx, float cy, float size, int color) {
        iconCentered(glyph, cx, cy, size, color, InterBoldHelper.clickGuiIcons());
    }

    private static void iconCentered(String glyph, float cx, float cy, float size, int color, TextHeightHelper font) {
        drawIcon(glyph, cx - font.getWidth(glyph, size) * 0.5F, cy + size * 0.5F - font.ascender(size),
                size, color, font);
    }

    /** Иконка, отцентрованная в боксе (обёртка над {@link #iconCs}). */
    public static void iconCsCentered(String glyph, float boxX, float boxY, float boxW, float boxH, float size,
                                      int color) {
        iconCs(glyph, boxX + boxW * 0.5F, boxY + boxH * 0.5F, size, color);
    }

    /** Буквенная иконка, отцентрованная в боксе. */
    public static void iconLettersCentered(String glyph, float boxX, float boxY, float boxW, float boxH, float size,
                                           int color) {
        iconLetters(glyph, boxX + boxW * 0.5F, boxY + boxH * 0.5F, size, color);
    }

    private static void drawIcon(String glyph, float x, float y, float size, int color, TextHeightHelper font) {
        color = f(color);
        if (glyph == null || glyph.isEmpty() || size <= 0.0F || (color >>> 24) == 0) {
            return;
        }
        BeginFrameHelper.text(x, y, size, glyph).font(font).color(color).draw();
    }

    /** Текстура (например, будущие иконки предметов); координаты — GUI-пиксели. */

    // ---- цвета

    public static int a(int color, int alpha) {
        return (Math.max(0, Math.min(255, alpha)) << 24) | (color & 0xFFFFFF);
    }

    /** Тот же цвет с альфой, заданной в долях (0..1). */

    /** Умножает существующую альфу на коэффициент. */
    public static int am(int color, float factor) {
        return a(color, (int) ((color >>> 24) * Math.max(0.0F, Math.min(1.0F, factor))));
    }

    /**
     * Контрастный цвет поверх заливки: тёмный на светлом акценте, белый на тёмном.
     *
     * <p>Нужен там, где текст лежит на заливке акцентом (сегменты режима, док,
     * включённый тумблер): на светлых палитрах белый текст не читается.
     */
    public static int onAccent(int color) {
        int r = color >> 16 & 0xFF;
        int g = color >> 8 & 0xFF;
        int b = color & 0xFF;
        int luminance = (r * 299 + g * 587 + b * 114) / 1000;
        return luminance > 150 ? 0xFF12141C : 0xFFFFFFFF;
    }

    public static int mix(int from, int to, float t) {
        if (t <= 0.0F) {
            return from;
        }
        if (t >= 1.0F) {
            return to;
        }
        int aa = from >>> 24 & 0xFF;
        int ar = from >> 16 & 0xFF;
        int ag = from >> 8 & 0xFF;
        int ab = from & 0xFF;
        int ba = to >>> 24 & 0xFF;
        int br = to >> 16 & 0xFF;
        int bg = to >> 8 & 0xFF;
        int bb = to & 0xFF;
        return (int) (aa + (ba - aa) * t) << 24
                | (int) (ar + (br - ar) * t) << 16
                | (int) (ag + (bg - ag) * t) << 8
                | (int) (ab + (bb - ab) * t);
    }

    /** Полностью непрозрачный вариант цвета палитры. */

}
