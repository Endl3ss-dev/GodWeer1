package net.skycore.client.gui.menu;

import net.skycore.client.util.BeginFrameHelper;
import net.skycore.client.util.InterBoldHelper;
import net.skycore.client.util.TextHeightHelper;

/**
 * Текст меню ClickGUI.
 *
 * <p>Порт {@code swerve.ui.menu.MenuText} (Swerve, порт Arcanic Visuals, GPL-3.0),
 * но без собственного MSDF-рендерера: в Swerve текст рисовался шейдером
 * {@code core/fonts} по атласам {@code arcmedium/arcsemibold/arcbold}. В SkyCore
 * уже есть готовая система шрифтов ({@link TextHeightHelper} + {@link BeginFrameHelper}),
 * причём с теми же тремя ступенями веса. Переносить второй параллельный рендерер
 * текста означало бы дублировать кэш глифов, блендинг и шейдерные пайплайны ради
 * внешнего сходства — поэтому начертания отображены на штатные Inter-шрифты клиента.
 *
 * <p>Метрики берутся у того же шрифта, которым рисуем: ширина строки, высота строки и
 * подъём над базовой линией. Иначе вёрстка меню «поехала» бы ровно на разницу
 * метрик двух рендереров.
 */
public final class MenuText {

    /** Начертания меню: три ступени, как в источнике. */
    public enum Weight {
        MEDIUM,
        SEMIBOLD,
        BOLD
    }

    private MenuText() {
    }

    /** Шрифт SkyCore для начертания меню. */
    public static TextHeightHelper font(Weight weight) {
        return switch (weight) {
            case MEDIUM -> InterBoldHelper.interMedium();
            case SEMIBOLD -> InterBoldHelper.interSemibold();
            case BOLD -> InterBoldHelper.interBold();
        };
    }

    /** Высота строки в пикселях для заданного размера. */
    public static float lineHeight(Weight weight, float px) {
        return font(weight).lineHeight(px);
    }

    /** Подъём над базовой линией: по нему текст центрируется внутри бокса. */
    public static float ascender(Weight weight, float px) {
        return font(weight).ascender(px);
    }

    /** Ширина строки в пикселях. */
    public static float width(String text, Weight weight, float px) {
        if (text == null || text.isEmpty()) {
            return 0.0F;
        }
        return font(weight).getWidth(text, px);
    }

    /**
     * Рисует строку; {@code y} — верх строки (не базовая линия), как и в источнике.
     */
    public static void draw(String text, float x, float y, Weight weight, float px, int color) {
        if (text == null || text.isEmpty() || (color >>> 24) == 0 || px <= 0.0F) {
            return;
        }
        BeginFrameHelper.text(x, y, px, text)
                .font(font(weight))
                .color(color)
                .draw();
    }

    /**
     * Обрезает строку по ширине, добавляя многоточие.
     *
     * <p>Идёт от полной строки вниз: меню содержит короткие подписи, поэтому
     * посимвольный проход дешевле бинарного поиска и не требует копий подстрок
     * сверх самой результатной.
     */
    public static String ellipsize(String text, Weight weight, float px, float maxWidth) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        if (maxWidth <= 0.0F) {
            return "";
        }
        if (width(text, weight, px) <= maxWidth) {
            return text;
        }
        int end = text.length();
        while (end > 1) {
            end--;
            String candidate = text.substring(0, end) + "…";
            if (width(candidate, weight, px) <= maxWidth) {
                return candidate;
            }
        }
        return text.substring(0, 1);
    }
}
