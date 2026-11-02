package net.skycore.client.gui.workspace;

/**
 * Геометрия меню Workspace.
 *
 * <p>Класс намеренно не импортирует ничего из Minecraft: вся раскладка — чистая
 * математика над размерами экрана и множителем масштаба. Это даёт две вещи:
 * <ul>
 *   <li>раскладку можно посчитать и проверить без запуска игры (и без
 *       {@code Minecraft.getInstance()}, который в момент инициализации может быть null);</li>
 *   <li>отрисовка не содержит арифметики — она только читает готовые поля, поэтому
 *       в кадре нет ни одной лишней операции.</li>
 * </ul>
 *
 * <p>Схема окна (анатомия взята из {@code Phobia/system/screensystem/clickgui} —
 * однопанельное рабочее пространство, а не набор карточек с всплывающим окном
 * настроек):
 *
 * <pre>
 * ┌──────────────────────────── окно ────────────────────────────┐
 * │ ┌─ сайдбар ─┐  ┌───────────── контент ─────────────────────┐ │
 * │ │ логотип   │  │ шапка: поиск        палитра  закрыть      │ │
 * │ │ Категории │  ├─────────────────────────┬─────────────────┤ │
 * │ │  • Combat │  │ карточки модулей        │ панель настроек │ │
 * │ │  • ...    │  │ (1–2 колонки)           │ выбранного      │ │
 * │ │           │  │                         │ модуля          │ │
 * │ │ палитра   │  │                         │                 │ │
 * │ └───────────┘  └─────────────────────────┴─────────────────┘ │
 * └──────────────────────────────────────────────────────────────┘
 * </pre>
 *
 * <p>Единица {@code u} считается той же формулой, что и в остальных меню клиента
 * ({@code height / 1080}, {@code width / 1060}, множитель «Размер меню»), поэтому
 * Workspace не «прыгает» относительно портированного меню при смене разрешения.
 */
public final class WorkspaceLayout {

    /** Базовый размер окна в «единицах» меню. */
    private static final float DESIGN_W = 1000.0F;
    private static final float DESIGN_H = 660.0F;

    /** Ширина сайдбара и высота шапки в единицах. */
    public static final float SIDEBAR_W = 196.0F;
    public static final float HEADER_H = 40.0F;
    public static final float PANE_W = 312.0F;

    /** Отступы и радиусы в единицах. */
    public static final float PAD = 14.0F;
    public static final float GAP = 10.0F;
    public static final float CARD_H = 52.0F;
    public static final float CARD_GAP = 8.0F;
    public static final float RADIUS = 14.0F;

    /** Ширина модульной колонки, при которой карточки переносятся в одну колонку. */
    private static final float TWO_COLUMN_MIN = 340.0F;

    public float u = 1.0F;

    public float px;
    public float py;
    public float pw;
    public float ph;

    public float sideX;
    public float sideY;
    public float sideW;
    public float sideH;

    public float contentX;
    public float contentW;

    public float headerY;
    public float headerH;

    public float listX;
    public float listY;
    public float listW;
    public float listH;

    public float paneX;
    public float paneY;
    public float paneW;
    public float paneH;

    /** Ширина экрана в GUI-пикселях на момент последнего {@link #measure}. */
    public float screenW;
    public float screenH;

    /** Пересчитывает всю раскладку. В кадре вызывается один раз. */
    public void measure(float screenWidth, float screenHeight, float menuScale) {
        this.screenW = screenWidth;
        this.screenH = screenHeight;

        // та же формула, что у MenuScreen: единица не зависит от того, какое меню открыто
        float unit = Math.min(screenHeight / 1080.0F, screenWidth / 1060.0F) * menuScale * 0.86F;
        this.u = Math.max(0.45F, Math.min(2.2F, unit));

        this.pw = Math.min(s(DESIGN_W), screenWidth - s(60.0F));
        this.ph = Math.min(s(DESIGN_H), screenHeight - s(70.0F));
        this.px = Math.round((screenWidth - this.pw) * 0.5F);
        this.py = Math.round((screenHeight - this.ph) * 0.5F);

        this.sideX = this.px + s(8.0F);
        this.sideY = this.py + s(8.0F);
        this.sideW = s(SIDEBAR_W);
        this.sideH = this.ph - s(16.0F);

        this.contentX = this.sideX + this.sideW + s(12.0F);
        this.contentW = this.px + this.pw - s(14.0F) - this.contentX;

        this.headerY = this.py + s(14.0F);
        this.headerH = s(HEADER_H);

        this.listX = this.contentX;
        this.listY = this.headerY + this.headerH + s(10.0F);
        this.listH = this.py + this.ph - s(16.0F) - this.listY;
        // правая колонка фиксированной ширины, но не больше половины контента:
        // на узком окне настройки не должны съесть список модулей
        float paneWidth = Math.min(s(PANE_W), this.contentW * 0.52F);
        this.paneW = paneWidth;
        this.paneX = this.contentX + this.contentW - paneWidth;
        this.paneY = this.listY;
        this.paneH = this.listH;
        this.listW = this.contentW - paneWidth - s(12.0F);
    }

    /** Масштабирование единиц в GUI-пиксели. */
    public float s(float value) {
        return value * this.u;
    }

    /** Округлённая в пиксели единица — для линий и границ. */
    public int line() {
        return Math.max(1, Math.round(this.u));
    }

    /** Колонок в списке модулей: две на широком окне, одна на узком. */
    public int columns() {
        return this.listW >= s(TWO_COLUMN_MIN) ? 2 : 1;
    }

    public float cardWidth() {
        int columns = columns();
        return (this.listW - s(CARD_GAP) * (columns - 1)) / columns;
    }

    public float cardHeight() {
        return s(CARD_H);
    }

    public float cardGap() {
        return s(CARD_GAP);
    }

    /** Высота видимой части списка (под шапкой категории). */
    public float listViewHeight(float categoryHeader) {
        return Math.max(0.0F, this.listH - categoryHeader);
    }

    /** Прямоугольник карточки по её порядковому номеру в колонке. */
    public void cardAt(int index, float scroll, float[] out) {
        int columns = columns();
        int column = index % columns;
        int row = index / columns;
        float width = cardWidth();
        out[0] = this.listX + column * (width + s(CARD_GAP));
        out[1] = this.listY + s(38.0F) + row * (cardHeight() + cardGap()) - scroll;
        out[2] = width;
        out[3] = cardHeight();
    }

    /** Полная высота списка модулей с прокруткой. */
    public float listContentHeight(int count) {
        int columns = columns();
        int rows = (count + columns - 1) / columns;
        return rows <= 0 ? 0.0F : rows * (cardHeight() + cardGap()) - cardGap();
    }
}
