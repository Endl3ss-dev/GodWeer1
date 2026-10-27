package net.skycore.client.gui.workspace;

import net.skycore.client.gui.menu.MenuAnim;
import net.skycore.client.gui.menu.MenuDraw;
import net.skycore.client.gui.menu.MenuText.Weight;
import net.skycore.client.module.KeyBinds;
import net.skycore.client.setting.ActionSetting;
import net.skycore.client.setting.BooleanSetting;
import net.skycore.client.setting.ColorSetting;
import net.skycore.client.setting.KeyBindSetting;
import net.skycore.client.setting.ModeSetting;
import net.skycore.client.setting.MultiSelectSetting;
import net.skycore.client.setting.NumberSetting;
import net.skycore.client.setting.Setting;
import net.skycore.client.setting.StringSetting;
import net.skycore.client.util.SelectAndSaveHelper;

import java.util.List;
import java.util.Locale;

/**
 * Строки настроек панели Workspace.
 *
 * <p>Анатомия взята из {@code Phobia/system/screensystem/clickgui/settingcomponent/*}:
 * настройки живут прямо в панели справа и меняются по выбранному модулю, а не в
 * отдельном всплывающем окне. Реализация переписана под модель настроек SkyCore,
 * где типов девять (включая {@link MultiSelectSetting} и {@link ActionSetting},
 * которых в источнике нет), и под требования клиента к кадру:
 * <ul>
 *   <li>ни одной лямбды, конкатенации строк или {@code new} на кадр: геометрия
 *       считается из констант и заранее известных высот, хит-области складываются
 *       в массивы экрана через {@link Sink};</li>
 *   <li>анимации — общий {@link MenuAnim} с целочисленными ключами ({@link #key});</li>
 *   <li>строка не рисуется, если её прямоугольник вне видимой части панели.</li>
 * </ul>
 */
final class WorkspaceSettings {

    // ---- типы хит-областей строк
    static final int HIT_BOOL = 1;
    static final int HIT_SLIDER = 2;
    static final int HIT_MODE = 3;
    static final int HIT_MULTI = 4;
    static final int HIT_STRING = 5;
    static final int HIT_COLOR = 6;
    static final int HIT_BIND = 7;
    static final int HIT_ACTION = 8;

    /** Ключи анимаций строк: 4 значения на строку, максимум 96 строк. */
    private static final int KEY_BASE = 9_000;
    private static final int KEY_STRIDE = 4;

    /** Приёмник хит-областей (реализуется экраном, без лямбд в кадре). */
    interface Sink {
        void hit(int type, int a, int b, float x, float y, float w, float h);
    }

    private WorkspaceSettings() {
    }

    static int key(int row) {
        return KEY_BASE + row * KEY_STRIDE;
    }

    // ---- геометрия

    /** Высота строки настройки при заданной ширине. */
    static float height(WorkspaceLayout l, Setting<?> setting, float width) {
        if (setting instanceof NumberSetting) {
            return l.s(54.0F);
        }
        if (setting instanceof MultiSelectSetting multi) {
            int perRow = Math.max(1, (int) ((width + l.s(6.0F)) / (l.s(74.0F) + l.s(6.0F))));
            int rows = (multi.getModes().size() + perRow - 1) / perRow;
            return l.s(20.0F) + rows * l.s(23.0F);
        }
        if (setting instanceof ActionSetting) {
            return l.s(34.0F);
        }
        return l.s(34.0F);
    }

    /** Суммарная высота видимых строк с отступами. */
    static float contentHeight(WorkspaceLayout l, List<Setting<?>> settings, float width) {
        float total = 0.0F;
        for (int i = 0; i < settings.size(); i++) {
            Setting<?> setting = settings.get(i);
            if (!setting.isVisible()) {
                continue;
            }
            total += height(l, setting, width) + l.s(6.0F);
        }
        return total;
    }

    /**
     * Рисует строки списка.
     *
     * @param y          верх списка без прокрутки
     * @param scroll     текущая прокрутка
     * @param viewTop    верх видимой части
     * @param viewBottom низ видимой части
     * @param mouseX     курсор в GUI-пикселях (для наведения)
     * @param mouseY     курсор в GUI-пикселях
     * @param editingRow строка с активным вводом строки (-1)
     * @param editBuffer буфер ввода для {@code editingRow}
     * @param bindingRow строка с перехватом клавиши (-1)
     */
    static void render(WorkspaceLayout l, WorkspaceTheme t, MenuAnim anim, List<Setting<?>> settings,
                       float x, float y, float width, float scroll, float viewTop, float viewBottom,
                       float mouseX, float mouseY, int editingRow, String editBuffer, int bindingRow,
                       boolean live, Sink sink) {
        float cursor = y - scroll;
        int row = 0;
        for (int index = 0; index < settings.size(); index++) {
            Setting<?> setting = settings.get(index);
            if (!setting.isVisible()) {
                continue;
            }
            float height = height(l, setting, width);
            if (cursor + height >= viewTop - l.s(2.0F) && cursor <= viewBottom + l.s(2.0F)) {
                boolean hovered = live && mouseY >= viewTop && mouseY <= viewBottom
                        && mouseY >= cursor && mouseY <= cursor + height
                        && mouseX >= x - l.s(4.0F) && mouseX <= x + width + l.s(4.0F);
                drawRow(l, t, anim, setting, row, x, cursor, width, height,
                        hovered, row == editingRow, editBuffer, row == bindingRow, live, sink);
            }
            cursor += height + l.s(6.0F);
            row++;
        }
    }

    /** Индекс видимой строки по индексу настройки в списке модуля. */
    static int rowOf(List<Setting<?>> settings, int settingIndex) {
        int row = 0;
        for (int i = 0; i < settings.size() && i < settingIndex; i++) {
            if (settings.get(i).isVisible()) {
                row++;
            }
        }
        return row;
    }

    // ---- одна строка

    private static void drawRow(WorkspaceLayout l, WorkspaceTheme t, MenuAnim anim, Setting<?> setting,
                                int row, float x, float y, float width, float height,
                                boolean hovered, boolean editing, String editBuffer, boolean binding,
                                boolean live, Sink sink) {
        float font = l.s(11.0F);
        float hover = anim.to(key(row) + 1, hovered && live ? 1.0F : 0.0F, 18.0F);
        if (hover > 0.02F) {
            MenuDraw.round(x - l.s(6.0F), y - l.s(2.0F), width + l.s(12.0F), height + l.s(4.0F), l.s(7.0F),
                    MenuDraw.a(t.accent, (int) (20.0F * hover)));
        }

        String label = safe(SelectAndSaveHelper.text(setting.getName()));

        if (setting instanceof MultiSelectSetting multi) {
            MenuDraw.textMid(MenuDraw.ellipsize(label, Weight.MEDIUM, font, width), x, y, l.s(20.0F),
                    Weight.MEDIUM, font, t.text);
            float chipFont = l.s(9.5F);
            float chipHeight = l.s(17.0F);
            float chipY = y + l.s(20.0F);
            float cursorX = x;
            for (int i = 0; i < multi.getModes().size(); i++) {
                String mode = multi.getModes().get(i);
                String text = safe(SelectAndSaveHelper.text(mode));
                float chipWidth = MenuDraw.textWidth(text, Weight.MEDIUM, chipFont) + l.s(16.0F);
                if (cursorX + chipWidth > x + width && cursorX > x) {
                    cursorX = x;
                    chipY += l.s(23.0F);
                }
                boolean on = multi.isSelected(mode);
                MenuDraw.round(cursorX, chipY, chipWidth, chipHeight, chipHeight * 0.5F,
                        on ? MenuDraw.a(t.accent, t.light ? 40 : 56) : t.inset);
                MenuDraw.outline(cursorX, chipY, chipWidth, chipHeight, chipHeight * 0.5F,
                        on ? MenuDraw.a(t.accent, 150) : t.border);
                MenuDraw.textCenterMid(text, cursorX + chipWidth * 0.5F, chipY, chipHeight, Weight.MEDIUM,
                        chipFont, on ? (t.light ? t.accent : t.accent2) : t.muted);
                if (live) {
                    sink.hit(HIT_MULTI, row, i, cursorX, chipY, chipWidth, chipHeight);
                }
                cursorX += chipWidth + l.s(6.0F);
            }
            return;
        }

        if (setting instanceof ActionSetting action) {
            String text = safe(SelectAndSaveHelper.text(action.getActionLabel()));
            float boxHeight = l.s(26.0F);
            float boxY = y + (height - boxHeight) * 0.5F;
            MenuDraw.round(x, boxY, width, boxHeight, l.s(8.0F), t.inset);
            MenuDraw.outline(x, boxY, width, boxHeight, l.s(8.0F), t.border);
            MenuDraw.textCenterMid(MenuDraw.ellipsize(text, Weight.SEMIBOLD, font, width - l.s(20.0F)),
                    x + width * 0.5F, boxY, boxHeight, Weight.SEMIBOLD, font,
                    t.light ? t.accent : t.accent2);
            if (live) {
                sink.hit(HIT_ACTION, row, 0, x, boxY, width, boxHeight);
            }
            return;
        }

        float controlHeight = l.s(20.0F);
        float controlY = y;
        float labelMax = width - l.s(90.0F);
        MenuDraw.textMid(MenuDraw.ellipsize(label, Weight.MEDIUM, font, Math.max(l.s(40.0F), labelMax)),
                x, controlY, controlHeight, Weight.MEDIUM, font, t.text);

        if (setting instanceof BooleanSetting bool) {
            float toggleWidth = l.s(30.0F);
            float toggleHeight = l.s(16.0F);
            float toggleX = x + width - toggleWidth;
            float toggleY = controlY + (controlHeight - toggleHeight) * 0.5F;
            float on = anim.to(key(row), bool.get() ? 1.0F : 0.0F, 20.0F);
            WorkspaceDraw.toggle(l, t, toggleX, toggleY, toggleWidth, toggleHeight, on, hover);
            if (live) {
                sink.hit(HIT_BOOL, row, 0, toggleX - l.s(8.0F), toggleY - l.s(5.0F), toggleWidth + l.s(16.0F),
                        toggleHeight + l.s(10.0F));
            }
            return;
        }

        if (setting instanceof NumberSetting number) {
            String value = numberText(number);
            MenuDraw.textRight(value, x + width, controlY, Weight.SEMIBOLD, font, t.text);
            float sliderTop = controlY + controlHeight + l.s(4.0F);
            float trackHeight = Math.max(2.0F, l.s(3.0F));
            float trackY = sliderTop + l.s(5.0F) - trackHeight * 0.5F;
            MenuDraw.round(x, trackY, width, trackHeight, trackHeight * 0.5F, t.track);
            float ratio = anim.to(key(row) + 2, (float) number.ratio(), 26.0F);
            float filled = Math.max(trackHeight, width * ratio);
            int fill = MenuDraw.mix(t.accent, t.accent2, ratio);
            MenuDraw.round(x, trackY, filled, trackHeight, trackHeight * 0.5F, fill);
            float knobRadius = l.s(4.5F);
            float knobX = x + filled;
            MenuDraw.circle(knobX, trackY + trackHeight * 0.5F, knobRadius, t.knob);
            MenuDraw.ring(knobX, trackY + trackHeight * 0.5F, knobRadius + l.s(1.5F), l.s(1.5F),
                    MenuDraw.a(t.accent2, 170));
            if (live) {
                sink.hit(HIT_SLIDER, row, 0, x, sliderTop - l.s(7.0F), width, l.s(24.0F));
            }
            return;
        }

        if (setting instanceof ModeSetting mode) {
            String value = safe(SelectAndSaveHelper.text(mode.get()));
            MenuDraw.textRight(MenuDraw.ellipsize(value, Weight.SEMIBOLD, font,
                            Math.max(l.s(30.0F), width - l.s(96.0F))),
                    x + width, controlY, Weight.SEMIBOLD, font, t.light ? t.accent : t.accent2);
            if (live) {
                sink.hit(HIT_MODE, row, 0, x, controlY, width, controlHeight);
            }
            return;
        }

        if (setting instanceof StringSetting string) {
            String value = safe(editing && editBuffer != null ? editBuffer : string.get());
            float boxWidth = Math.min(l.s(150.0F), Math.max(l.s(60.0F), width - l.s(96.0F)));
            float boxX = x + width - boxWidth;
            float boxHeight = l.s(20.0F);
            float boxY = controlY + (controlHeight - boxHeight) * 0.5F;
            MenuDraw.round(boxX, boxY, boxWidth, boxHeight, l.s(6.0F), editing ? t.insetHover : t.inset);
            MenuDraw.outline(boxX, boxY, boxWidth, boxHeight, l.s(6.0F),
                    editing ? MenuDraw.a(t.accent, 170) : t.border);
            String shown = MenuDraw.ellipsize(value.isEmpty() ? "—" : value, Weight.MEDIUM, font,
                    boxWidth - l.s(12.0F));
            MenuDraw.textMid(shown, boxX + l.s(6.0F), boxY, boxHeight, Weight.MEDIUM, font,
                    value.isEmpty() ? t.dim : t.text);
            if (editing) {
                float caretX = boxX + l.s(6.0F)
                        + Math.min(boxWidth - l.s(12.0F), MenuDraw.textWidth(value, Weight.MEDIUM, font))
                        + l.s(1.0F);
                MenuDraw.rect(caretX, boxY + l.s(4.0F), Math.max(1.0F, l.s(1.0F)), boxHeight - l.s(8.0F),
                        t.accent);
            }
            if (live) {
                sink.hit(HIT_STRING, row, 0, boxX, boxY, boxWidth, boxHeight);
            }
            return;
        }

        if (setting instanceof ColorSetting color) {
            Integer stored = color.get();
            int argb = stored == null ? 0xFFFFFFFF : stored;
            float swatch = l.s(16.0F);
            float swatchX = x + width - swatch;
            float swatchY = controlY + (controlHeight - swatch) * 0.5F;
            MenuDraw.round(swatchX, swatchY, swatch, swatch, l.s(5.0F), argb);
            MenuDraw.outline(swatchX, swatchY, swatch, swatch, l.s(5.0F), MenuDraw.a(t.text, 60));
            String hex = String.format(Locale.ROOT, "%06X", argb & 0xFFFFFF);
            float hexFont = l.s(9.5F);
            MenuDraw.textRight(hex, swatchX - l.s(6.0F),
                    controlY + (controlHeight - l.s(10.0F)) * 0.5F, Weight.MEDIUM, hexFont, t.muted);
            if (live) {
                sink.hit(HIT_COLOR, row, 0, swatchX - l.s(56.0F), swatchY - l.s(5.0F), swatch + l.s(56.0F),
                        swatch + l.s(10.0F));
            }
            return;
        }

        if (setting instanceof KeyBindSetting key) {
            String text = binding ? "..." : key.get() == null || key.get() < 0 ? "—" : KeyBinds.name(key.get());
            WorkspaceDraw.keyChip(l, t, text, x + width, controlY + (controlHeight - l.s(18.0F)) * 0.5F,
                    l.s(18.0F), binding, hovered);
            if (live) {
                sink.hit(HIT_BIND, row, 0, x, controlY, width, controlHeight);
            }
            return;
        }

        // неизвестный тип: только подпись и пассивная область, чтобы строка не «пропадала»
        MenuDraw.textRight("—", x + width, controlY, Weight.MEDIUM, font, t.dim);
        if (live) {
            sink.hit(HIT_ACTION, row, 0, x, controlY, width, controlHeight);
        }
    }

    /** Строка без {@code null}: значения строковых настроек и подписи могут быть пустыми. */
    private static String safe(String value) {
        return value == null ? "" : value;
    }

    // ---- значения

    /** Текст числовой настройки: целые без дробной части, дробные — с двумя знаками. */
    static String numberText(NumberSetting setting) {
        double value = setting.get();
        String text;
        if (setting.getStep() >= 1.0 || Math.abs(value - Math.rint(value)) < 1.0E-6) {
            text = Long.toString(Math.round(value));
        } else {
            text = String.format(Locale.ROOT, "%.2f", value);
        }
        String suffix = setting.getSuffix();
        return suffix == null || suffix.isEmpty() ? text : text + suffix;
    }
}
