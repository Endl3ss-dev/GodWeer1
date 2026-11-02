package net.skycore.client.gui.workspace;

import net.skycore.client.gui.menu.MenuDraw;
import net.skycore.client.gui.menu.MenuPrefs;
import net.skycore.client.gui.menu.MenuText.Weight;
import net.skycore.client.util.BeginFrameHelper;

/**
 * Составные примитивы меню Workspace.
 *
 * <p>Каждый метод — одна законченная деталь интерфейса (окно, карточка, тумблер,
 * чип, скроллбар). Так сделано в источнике анатомии
 * ({@code Phobia/system/screensystem/clickgui}, компоненты
 * {@code SidebarComponent}, {@code ModuleComponent}, {@code settingcomponent/*}), но
 * отрисовка переписана на {@link MenuDraw} и {@link BeginFrameHelper} клиента:
 * <ul>
 *   <li>нет собственного атласа и шейдеров — всё штатными пайплайнами SkyCore;</li>
 *   <li>нет {@code new} в кадре: методы только читают переданные значения;</li>
 *   <li>цвета приходят из {@link WorkspaceTheme}, а не из статической таблицы.</li>
 * </ul>
 */
final class WorkspaceDraw {

    private WorkspaceDraw() {
    }

    /** Окно: тень, подложка (с размытием — по настройке меню) и волосяная граница. */
    static void window(WorkspaceLayout l, WorkspaceTheme t) {
        float radius = l.s(WorkspaceLayout.RADIUS);
        MenuDraw.shadow(l.px, l.py + l.s(10.0F), l.pw, l.ph, radius, l.s(30.0F), t.shadow);
        if (MenuPrefs.blur) {
            BeginFrameHelper.rect(l.px, l.py, l.pw, l.ph)
                    .radius(radius)
                    // альфа ниже 0xF0, иначе размытие под окном не видно
                    .color(t.light ? 0xE6F7F7FA : MenuDraw.a(t.window, 226))
                    .blur(l.s(24.0F), MenuDraw.fade)
                    .hudGlow(false)
                    .draw();
        } else {
            MenuDraw.round(l.px, l.py, l.pw, l.ph, radius, t.window);
        }
        MenuDraw.outline(l.px, l.py, l.pw, l.ph, radius, t.light ? t.border : MenuDraw.a(0xFFFFFF, 22));
    }

    /** Сайдбар: отдельная подложка внутри окна, со своей границей. */
    static void sidebar(WorkspaceLayout l, WorkspaceTheme t) {
        MenuDraw.round(l.sideX, l.sideY, l.sideW, l.sideH, l.s(11.0F), t.sidebar);
        MenuDraw.outline(l.sideX, l.sideY, l.sideW, l.sideH, l.s(11.0F),
                t.light ? t.border : MenuDraw.a(0xFFFFFF, 14));
    }

    /** Панель контента (список модулей и панель настроек). */
    static void pane(float x, float y, float w, float h, float radius, WorkspaceTheme t) {
        MenuDraw.round(x, y, w, h, radius, t.card);
        MenuDraw.outline(x, y, w, h, radius, t.border);
    }

    /** Волосяной разделитель на всю ширину блока. */
    static void divider(WorkspaceLayout l, float x, float y, float w, WorkspaceTheme t) {
        MenuDraw.rect(x, y, w, l.line(), t.border);
    }

    /**
     * Тумблер: дорожка и круглый бегунок.
     *
     * <p>{@code on} — уже сглаженное значение 0…1, поэтому включение не «щёлкает»,
     * как в источнике, где дорожка перекрашивалась мгновенно.
     */
    static void toggle(WorkspaceLayout l, WorkspaceTheme t, float x, float y, float w, float h,
                       float on, float hover) {
        float radius = h * 0.5F;
        int off = t.light ? 0xFFDCDCE5 : MenuDraw.a(0xFFFFFF, 26);
        if (MenuPrefs.gradient) {
            MenuDraw.pill(x, y, w, h, MenuDraw.mix(off, t.accent, on), MenuDraw.mix(off, t.accent2, on));
        } else {
            MenuDraw.round(x, y, w, h, radius, MenuDraw.mix(off, t.accent, on));
        }
        if (hover > 0.01F && on < 0.99F) {
            MenuDraw.round(x, y, w, h, radius, MenuDraw.a(t.accent, (int) (26 * hover * (1.0F - on))));
        }
        float margin = Math.max(2.0F, Math.round(h * 0.14F));
        float size = h - margin * 2.0F;
        float knobX = x + margin + (w - margin * 2.0F - size) * on;
        MenuDraw.circle(knobX + size * 0.5F, y + h * 0.5F, size * 0.5F,
                MenuDraw.mix(t.knob, t.onAccent, on));
    }

    /** Чип: маленькая плашка с текстом; возвращает свою ширину. */
    static float chip(WorkspaceLayout l, WorkspaceTheme t, String text, float x, float y, float h,
                      boolean active, boolean hovered, Weight weight, float font) {
        float pad = l.s(8.0F);
        float width = MenuDraw.textWidth(text, weight, font) + pad * 2.0F;
        int background = active
                ? MenuDraw.a(t.accent, t.light ? 40 : 56)
                : (hovered ? t.insetHover : t.inset);
        MenuDraw.round(x, y, width, h, h * 0.5F, background);
        MenuDraw.outline(x, y, width, h, h * 0.5F,
                active ? MenuDraw.a(t.accent, 150) : t.border);
        MenuDraw.textCenterMid(text, x + width * 0.5F, y, h, weight, font,
                active ? (t.light ? t.accent : t.accent2) : t.stateText(true));
        return width;
    }

    /** Ключ-чип (назначение клавиши): моно-вид, по центру текста. */
    static float keyChip(WorkspaceLayout l, WorkspaceTheme t, String text, float right, float y, float h,
                         boolean waiting, boolean hovered) {
        float font = l.s(9.5F);
        Weight weight = Weight.MEDIUM;
        float pad = l.s(7.0F);
        float width = Math.max(l.s(22.0F), MenuDraw.textWidth(text, weight, font) + pad * 2.0F);
        float x = right - width;
        int background = waiting ? MenuDraw.a(t.accent, 46) : (hovered ? t.insetHover : t.inset);
        MenuDraw.round(x, y, width, h, h * 0.5F, background);
        MenuDraw.outline(x, y, width, h, h * 0.5F, waiting ? MenuDraw.a(t.accent, 170) : t.border);
        MenuDraw.textCenterMid(text, x + width * 0.5F, y, h, weight, font,
                waiting ? (t.light ? t.accent : t.accent2) : t.muted);
        return width;
    }

    /**
     * Скроллбар: тонкая дорожка с бегунком.
     *
     * @param total полная высота содержимого
     * @param view  высота видимой части
     * @return {@code true}, если мышь над дорожкой (для подсветки)
     */
    static boolean scrollbar(WorkspaceLayout l, WorkspaceTheme t, float x, float y, float view, float total,
                             float scroll, float mouseX, float mouseY) {
        if (total <= view + 1.0F) {
            return false;
        }
        float thickness = Math.max(2.0F, l.s(3.0F));
        float trackX = x - thickness * 0.5F;
        boolean hovered = mouseX >= trackX - l.s(6.0F) && mouseX <= trackX + thickness + l.s(6.0F)
                && mouseY >= y && mouseY <= y + view;
        float thumb = Math.max(l.s(28.0F), view * view / total);
        float travel = Math.max(1.0F, total - view);
        float thumbY = y + (view - thumb) * Math.min(1.0F, Math.max(0.0F, scroll / travel));
        MenuDraw.round(trackX, y, thickness, view, thickness * 0.5F, MenuDraw.a(t.text, t.light ? 14 : 12));
        MenuDraw.round(trackX, thumbY, thickness, thumb, thickness * 0.5F,
                MenuDraw.a(t.text, hovered ? 120 : 70));
        return hovered;
    }

    /** Иконка категории из иконочного шрифта клиента. */
    static void categoryIcon(String glyph, float cx, float cy, float size, int color) {
        MenuDraw.iconCs(glyph, cx, cy, size, color);
    }

    /** Иконка из «буквенного» набора (стрелки, крестик, поиск). */
    static void letterIcon(String glyph, float cx, float cy, float size, int color) {
        MenuDraw.iconLetters(glyph, cx, cy, size, color);
    }
}
