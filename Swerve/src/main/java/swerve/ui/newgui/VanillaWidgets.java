package swerve.ui.newgui;

import com.mojang.blaze3d.vertex.PoseStack;

import swerve.utils.color.ColorUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/**
 * Отрисовка ванильных виджетов (кнопка, ползунок, флажок) в стиле GUI клиента.
 *
 * <p>Ванильные экраны настроек рисуют кнопки спрайтами {@code widgets/button*}, ползунки —
 * спрайтами {@code widgets/slider*}, а флажки — квадратами 9x9. Здесь те же виджеты
 * собираются из скруглений {@link GuiRender}: тёмная плитка в тоне фона клиентского GUI,
 * акцент темы на наведении, капсульная заливка прогресса и круглый бегунок.
 *
 * <p>Геометрия считается по фактическим размерам виджета ({@code getWidth()/getHeight()}),
 * поэтому одна и та же отрисовка подходит и для строк списка настроек, и для кнопок
 * главного меню, и для низких виджетов с нестандартной высотой.
 *
 * <p>Вызывается только из миксинов ванильных виджетов — здесь нет состояния и нет
 * ссылок на конкретные экраны.
 */
public final class VanillaWidgets {

    /** Основной текст на плитках. */
    private static final int TEXT = 0xFFF3F5FC;
    /** Текст выключенного виджета. */
    private static final int TEXT_OFF = 0xFF868D9E;
    /** Бегунок ползунка и головка переключателя. */
    private static final int KNOB = 0xFFF8FAFF;

    private VanillaWidgets() {
    }

    /** Сглаживание значения к цели по реальному времени между кадрами. */
    public static float ease(float current, float target, long now, long last) {
        if (last == 0L) {
            // первый кадр виджета: без «наезда» из нуля, но и без скачка подсветки
            return target;
        }
        float dt = Math.min(0.1f, (now - last) / 1_000_000_000.0f);
        return current + (target - current) * (1.0f - (float) Math.exp(-18.0f * dt));
    }

    public static float ease(float current, boolean target, long now, long last) {
        return ease(current, target ? 1.0f : 0.0f, now, last);
    }

    // ---- палитра

    /** Акцент клиента (переключается темой). */
    public static int accent() {
        return GuiTheme.clientColor();
    }

    /** Подложка виджета: тон фона клиентского GUI, чуть светлее самих панелей. */
    public static int plate(int alpha) {
        return GuiStyle.hsv(GuiStyle.backgroundHue(), GuiStyle.backgroundSaturation() / 100.0f * 0.55f,
                GuiStyle.backgroundBrightness() / 255.0f * 1.8f, alpha);
    }

    private static int plateTop(int alpha) {
        return ColorUtils.multDark(plate(alpha), 1.22f);
    }

    private static int plateBottom(int alpha) {
        return ColorUtils.multDark(plate(alpha), 0.78f);
    }

    /** Акцент, подмешанный к белому — для текста и верхней кромки заливки. */
    private static int accentLight(float amount) {
        return ColorUtils.interpolateColor(accent(), 0xFFFFFFFF, amount);
    }

    private static int white(int alpha) {
        return ColorUtils.setAlphaColor(0xFFFFFF, alpha);
    }

    private static int withAlpha(int color, float alphaMul, int baseAlpha) {
        return ColorUtils.setAlphaColor(color, (int) (baseAlpha * clamp01(alphaMul)));
    }

    /** Радиус плитки: почти капсула, но на высоких виджетах остаётся скруглением. */
    private static float radius(float height) {
        return Math.min(height * 0.5f, 9.0f);
    }

    private static float clamp01(float value) {
        return value < 0.0f ? 0.0f : (value > 1.0f ? 1.0f : value);
    }

    // ---- кнопка

    /**
     * Кнопка: плитка с градиентом, акцентная подсветка на наведении и обводка фокуса.
     *
     * @param hover  0..1 сглаженное наведение (см. {@link #ease})
     * @param active ванильный флаг {@code active}: выключенная кнопка приглушается
     * @param alpha  ванильный {@code AbstractWidget.alpha}: общий множитель прозрачности
     */
    public static void button(GuiGraphics graphics, int x, int y, int w, int h, Component message,
                              float hover, boolean active, boolean focused, float alpha) {
        float a = clamp01(alpha);
        if (w <= 0 || h <= 0 || a <= 0.02f) {
            return;
        }
        float r = radius(h);
        int ac = accent();
        PoseStack pose = graphics.pose();

        if (active && hover > 0.01f) {
            GuiRender.drawGlow(pose, x, y, w, h, r, 3.0f, 3, withAlpha(ac, a, (int) (70.0f * hover)));
        }

        GuiRender.drawGradientRound(pose, x, y, w, h, r,
                active ? withAlpha(plateTop(242), a, 242) : withAlpha(plate(200), a, 200),
                active ? withAlpha(plateBottom(242), a, 242) : withAlpha(plate(200), a, 200),
                active ? withAlpha(plateTop(242), a, 242) : withAlpha(plate(200), a, 200),
                active ? withAlpha(plateBottom(242), a, 242) : withAlpha(plate(200), a, 200));

        if (active && hover > 0.01f) {
            // мягкая акцентная вуаль поверх плитки
            GuiRender.roundedRect(pose, x, y, w, h, r, withAlpha(ac, a, (int) (42.0f * hover)));
            // светлая кромка сверху — «стеклянный» блик
            GuiRender.roundedRect(pose, x + r * 0.55f, y + 1.0f, w - r * 1.1f, 1.0f, 0.5f,
                    withAlpha(0xFFFFFF, a, (int) (26.0f * hover)));
        }

        int outline;
        if (focused) {
            outline = withAlpha(ac, a, 235);
        } else if (active) {
            outline = ColorUtils.interpolateColor(withAlpha(0xFFFFFF, a, 34), withAlpha(ac, a, 175), hover);
        } else {
            outline = withAlpha(0xFFFFFF, a, 18);
        }
        GuiRender.roundedOutline(pose, x + 0.5f, y + 0.5f, w - 1.0f, h - 1.0f, 1.0f, outline,
                Math.max(0.5f, r - 0.5f));

        int textColor;
        if (!active) {
            textColor = withAlpha(TEXT_OFF, a, 210);
        } else {
            textColor = ColorUtils.interpolateColor(withAlpha(TEXT, a, 255), withAlpha(accentLight(0.55f), a, 255),
                    hover * 0.9f);
        }
        String label = message == null ? "" : message.getString();
        text(graphics, label, x + 6, y, w - 12, h, textColor, true);
    }

    // ---- ползунок

    /**
     * Ползунок: капсульная плитка, заливка прогресса акцентом и круглый бегунок.
     *
     * <p>Подпись ванильного ползунка уже содержит значение ({@code "Дальность: 12"}),
     * поэтому текст просто центрируется поверх плитки.
     */
    public static void slider(GuiGraphics graphics, int x, int y, int w, int h, Component message, double value,
                              float hover, boolean active, boolean editable, float alpha) {
        float a = clamp01(alpha);
        if (w <= 0 || h <= 0 || a <= 0.02f) {
            return;
        }
        float frac = clamp01((float) value);
        float r = radius(h);
        int ac = accent();
        boolean live = active && editable;
        PoseStack pose = graphics.pose();

        GuiRender.drawGradientRound(pose, x, y, w, h, r,
                withAlpha(plateTop(236), a, 236), withAlpha(plateBottom(236), a, 236),
                withAlpha(plateTop(236), a, 236), withAlpha(plateBottom(236), a, 236));

        // бегунок: центр ходит по всей ширине с отступом в половину высоты
        float inset = h * 0.5f;
        float thumb = Math.max(2.5f, h * 0.33f * (1.0f + hover * 0.08f));
        float cx = x + inset + (w - inset * 2.0f) * frac;

        if (frac > 0.001f) {
            float fillW = Math.max(h * 0.9f, cx - x + thumb);
            int left = withAlpha(accentLight(0.18f), a, live ? 160 : 110);
            int right = withAlpha(ac, a, live ? 132 : 88);
            GuiRender.drawGradientRound(pose, x, y, fillW, h, r, left, right, left, right);
        }

        int outline;
        if (live) {
            outline = ColorUtils.interpolateColor(withAlpha(0xFFFFFF, a, 34), withAlpha(ac, a, 190), hover);
        } else {
            outline = withAlpha(0xFFFFFF, a, 18);
        }
        GuiRender.roundedOutline(pose, x + 0.5f, y + 0.5f, w - 1.0f, h - 1.0f, 1.0f, outline,
                Math.max(0.5f, r - 0.5f));

        float cy = y + h * 0.5f;
        if (live && hover > 0.01f) {
            GuiRender.drawGlow(pose, cx - thumb, cy - thumb, thumb * 2.0f, thumb * 2.0f, thumb, 2.0f, 3,
                    withAlpha(ac, a, (int) (90.0f * hover)));
        }
        GuiRender.roundedRect(pose, cx - thumb, cy - thumb, thumb * 2.0f, thumb * 2.0f, thumb,
                withAlpha(KNOB, a, live ? 255 : 190));
        GuiRender.roundedOutline(pose, cx - thumb, cy - thumb, thumb * 2.0f, thumb * 2.0f, 1.0f,
                live ? withAlpha(ac, a, 200) : withAlpha(0xFFFFFF, a, 40), thumb);

        // Подпись ванильного ползунка — "Имя: значение". Разносим части по краям:
        // иначе бегунок закрывает середину строки, а прогресс — её начало.
        String label = message == null ? "" : message.getString();
        int textColor = live
                ? ColorUtils.interpolateColor(withAlpha(TEXT, a, 255), withAlpha(accentLight(0.7f), a, 255), hover)
                : withAlpha(TEXT_OFF, a, 225);
        // отступ = половина высоты (центр бегунка в крайнем положении) + радиус бегунка + зазор
        int pad = Math.max(6, Math.round(h * 0.5f + h * 0.33f) + 3);
        int split = label.lastIndexOf(": ");
        if (split <= 0 || split >= label.length() - 2 || w - pad * 2 < 48) {
            draw(graphics, label, x + pad, y, w - pad * 2, h, textColor, CENTER);
            return;
        }
        String name = label.substring(0, split);
        String number = label.substring(split + 2);
        int numberWidth = Math.min(width(number) + 2, Math.max(20, (w - pad * 2) / 2));
        draw(graphics, name, x + pad, y, Math.max(0, w - pad * 2 - numberWidth - 6), h, textColor, LEFT);
        draw(graphics, number, x + w - pad - numberWidth, y, numberWidth, h,
                live ? withAlpha(KNOB, a, 250) : textColor, RIGHT);
    }

    // ---- переключатель (ванильный Checkbox)

    /**
     * Флажок как капсульный переключатель: дорожка и скользящая головка.
     *
     * @param knob 0..1 положение головки (анимируется отдельно от наведения)
     */
    public static void checkbox(GuiGraphics graphics, int x, int y, int w, int h, Component message, boolean selected,
                                float knob, float hover, boolean active, float alpha) {
        float a = clamp01(alpha);
        if (w <= 0 || h <= 0 || a <= 0.02f) {
            return;
        }
        float pillH = Math.min(h - 2.0f, 16.0f);
        if (pillH < 6.0f) {
            pillH = Math.max(4.0f, h - 2.0f);
        }
        float pillW = Math.round(pillH * 2.1f);
        float pr = pillH * 0.5f;
        float py = y + (h - pillH) * 0.5f;
        int ac = accent();
        PoseStack pose = graphics.pose();

        // дорожка: всегда подложка, включённое состояние проявляется вместе с головкой
        GuiRender.drawGradientRound(pose, x, py, pillW, pillH, pr,
                withAlpha(plateTop(240), a, 240), withAlpha(plateBottom(240), a, 240),
                withAlpha(plateTop(240), a, 240), withAlpha(plateBottom(240), a, 240));

        float on = clamp01(knob);
        if (on > 0.01f) {
            int left = withAlpha(accentLight(0.16f), a, (int) (238.0f * on));
            int right = withAlpha(ac, a, (int) (205.0f * on));
            GuiRender.drawGradientRound(pose, x, py, pillW, pillH, pr, left, right, left, right);
        }

        int outline = ColorUtils.interpolateColor(withAlpha(0xFFFFFF, a, 30), withAlpha(ac, a, 210),
                Math.max(on, hover * 0.8f));
        GuiRender.roundedOutline(pose, x + 0.5f, py + 0.5f, pillW - 1.0f, pillH - 1.0f, 1.0f, outline,
                Math.max(0.5f, pr - 0.5f));

        float travel = pillW - pillH;
        float kx = x + pr + travel * on;
        float kr = Math.max(1.5f, pr - 2.5f);
        GuiRender.roundedRect(pose, kx - kr, py + pr - kr, kr * 2.0f, kr * 2.0f, kr,
                withAlpha(KNOB, a, active ? 255 : 200));

        // подпись справа от дорожки, по левому краю — как в ванильном чекбоксе
        String label = message == null ? "" : message.getString();
        int textX = x + Math.round(pillW) + 7;
        int textColor = active
                ? ColorUtils.interpolateColor(withAlpha(TEXT, a, 255), withAlpha(accentLight(0.6f), a, 255), hover * 0.7f)
                : withAlpha(TEXT_OFF, a, 215);
        text(graphics, label, textX, y, Math.max(0, w - (textX - x)), h, textColor, false);
    }

    // ---- текст

    /** Выравнивание текста виджета. */
    private static final int LEFT = 0;
    private static final int CENTER = 1;
    private static final int RIGHT = 2;

    /** Ширина строки основным шрифтом игры. */
    public static int width(String label) {
        return Minecraft.getInstance().font.width(label == null ? "" : label);
    }

    /**
     * Текст виджета с обрезкой по доступной ширине.
     *
     * @param centered {@code true} — по центру области, {@code false} — по левому краю
     */
    public static void text(GuiGraphics graphics, String label, int x, int y, int w, int h, int color,
                            boolean centered) {
        draw(graphics, label, x, y, w, h, color, centered ? CENTER : LEFT);
    }

    /** Текст по левому/правому краю или по центру области {@code [x, x + w]}. */
    private static void draw(GuiGraphics graphics, String label, int x, int y, int w, int h, int color, int align) {
        if (label == null || label.isEmpty() || w <= 0) {
            return;
        }
        Font font = Minecraft.getInstance().font;
        String cut = cut(font, label, w);
        int textWidth = font.width(cut);
        int tx = switch (align) {
            case CENTER -> x + (w - textWidth) / 2;
            case RIGHT -> x + w - textWidth;
            default -> x;
        };
        int ty = y + (h - font.lineHeight) / 2 + 1;
        graphics.drawString(font, cut, tx, ty, color, true);
    }

    /** Обрезка строки до ширины с многоточием. */
    private static String cut(Font font, String label, int width) {
        if (font.width(label) <= width) {
            return label;
        }
        String dots = "...";
        int room = width - font.width(dots);
        if (room <= 0) {
            return font.plainSubstrByWidth(label, width);
        }
        return font.plainSubstrByWidth(label, room) + dots;
    }
}
