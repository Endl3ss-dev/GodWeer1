package net.skycore.client.gui;

import java.util.ArrayList;
import java.util.List;
import net.skycore.client.module.ClientModule;
import net.skycore.client.module.ModuleCategory;
import net.skycore.client.setting.BooleanSetting;
import net.skycore.client.setting.ColorSetting;
import net.skycore.client.setting.KeyBindSetting;
import net.skycore.client.setting.ModeSetting;
import net.skycore.client.setting.NumberSetting;
import net.skycore.client.setting.Setting;
import net.skycore.client.setting.StringSetting;
import net.skycore.client.util.BeginFrameHelper;

/**
 * Панель настроек модуля, портированная из Arcanic Visuals.
 *
 * <p>Повторяет {@code net.arcanic.visuals.gui.ModulePopup}: окно настроек
 * отдельного модуля с переключателем, ползунками, режимами, биндами, цветами
 * и строками ввода. Отображается внутри {@link ArcanicMenu} и использует
 * {@link BeginFrameHelper} для рендеринга.
 *
 * <p>Адаптировано из {@code net.arcanic.visuals.gui.ModulePopup}
 * (Arcanic Visuals, GPL-3.0).
 */
public final class SkyModuleSettings {

    private SkyModuleSettings() {
    }

    /** Один модуль с его настройками для отображения в меню. */
    public record ModuleEntry(ClientModule module, List<Setting<?>> visibleSettings) {
    }

    /** Все модули, сгруппированные по категориям. */
    public static List<ModuleEntry> modulesByCategory(ModuleCategory category) {
        List<ModuleEntry> out = new ArrayList<>();
        var mm = net.skycore.client.SkycoreClient.modules();
        for (ClientModule m : mm.getModules()) {
            if (m.getCategory() != category) continue;
            List<Setting<?>> vis = new ArrayList<>();
            for (Setting<?> s : m.getSettings()) {
                if (s.isVisible()) vis.add(s);
            }
            out.add(new ModuleEntry(m, vis));
        }
        out.sort((a, b) -> a.module().getName().compareToIgnoreCase(b.module().getName()));
        return out;
    }

    /**
     * Рисует список модулей категории (браузер модулей).
     * Каждый модуль — строка с именем, описанием, переключателем и иконкой настроек.
     *
     * @param menu  текущее меню ArcanicMenu
     * @param category категория модулей
     * @param x     X начала панели
     * @param y     Y начала панели
     * @param w     ширина панели
     * @param h     высота панели
     * @param sc    оффсет скролла
     * @param top   верхняя граница скролла
     * @param bottom нижняя граница скролла
     */
    public static void drawCategory(ArcanicMenu menu, ModuleCategory category,
                                     int x, int y, int w, int h, float sc, int top, int bottom) {
        var gfx = net.skycore.client.util.CurrentGuiGraphicsExtractorHelper.currentGuiGraphicsExtractor();
        List<ModuleEntry> entries = modulesByCategory(category);

        int pad = menu.S(18);
        int cw = w - pad * 2;
        int rowH = menu.S(44);
        int gap = menu.S(8);

        boolean lt = menu.light();
        int bg = lt ? 0xFFFFFFFF : SkyPalette.card();
        BeginFrameHelper.rect(x, y, w, h)
                .color(bg)
                .radius(menu.S(13))
                .blur(menu.S(14), 0.55F)
                .draw();
        BeginFrameHelper.rect(x, y, w, h)
                .color(lt ? mixAlpha(SkyPalette.text(), 26) : 0x18FFFFFF)
                .border(Math.max(0.5f, menu.scale()), lt ? mixAlpha(SkyPalette.text(), 26) : 0x18FFFFFF)
                .radius(menu.S(13))
                .draw();

        // Заголовок категории
        BeginFrameHelper.text(x + pad, y + menu.S(15), menu.S(15), category.toString())
                .color(SkyPalette.text())
                .draw();
        BeginFrameHelper.text(x + pad, y + menu.S(36), menu.S(11), entries.size() + " модулей")
                .color(lt ? SkyPalette.muted() : 0xFF757D8A)
                .draw();

        // Разделитель
        BeginFrameHelper.rect(x + pad, y + menu.S(52), cw, Math.max(1, menu.S(1)))
                .color(lt ? mixAlpha(SkyPalette.text(), 18) : 0x12FFFFFF)
                .draw();

        // Список модулей с скроллом
        float total = entries.size() * (rowH + gap) + menu.S(8);
        float scrolled = menu.scrolled("cat:" + category, total, y, y + h);
        gfx.enableScissor(x, top, x + w, bottom);
        gfx.pose().pushMatrix();
        gfx.pose().translate(0, -scrolled);

        int cy = y + menu.S(62);
        for (ModuleEntry entry : entries) {
            ClientModule m = entry.module();
            boolean rowHover = menu.hover(x, cy, cw, rowH);

            int plate = rowHover ? (lt ? 0x20FFFFFF : 0x20000000) : (lt ? 0x10000000 : SkyPalette.card() | 0xFF000000);
            BeginFrameHelper.rect(x, cy, cw, rowH)
                    .color(plate)
                    .radius(menu.S(8))
                    .draw();

            // Имя модуля
            BeginFrameHelper.text(x + pad, cy + menu.S(10), menu.S(13), m.getName())
                    .color(SkyPalette.text())
                    .draw();
            // Описание
            BeginFrameHelper.text(x + pad, cy + menu.S(26), menu.S(10), m.getDescription())
                    .color(lt ? SkyPalette.muted() : 0xFF757D8A)
                    .draw();

            // Переключатель модуля
            float mOn = m.isEnabled() ? 1 : 0;
            int tgW = menu.S(34);
            int tgH = menu.S(20);
            drawToggle(menu, gfx, x + cw - tgW - menu.S(10), cy + (rowH - tgH) / 2, tgW, tgH, mOn, rowHover ? 1 : 0);

            // Кнопка настроек (иконка шестерёнки)
            int btnW = menu.S(28);
            int btnX = x + cw - btnW - menu.S(10) - tgW - menu.S(10);
            boolean btnHover = rowHover && menu.hover(btnX, cy, btnW, rowH);
            int btnColor = btnHover ? SkyPalette.accent() : mixAlpha(SkyPalette.text(), 18);
            SkyGlyph.draw(gfx, "w.cog", btnX + 6, cy + (rowH - 16) / 2, 16, btnColor);
            if (btnHover) {
                menu.clipped(btnX, cy, btnW, rowH, sc, top, bottom, () -> {
                    menu.setContent("module:" + m.getName());
                    menu.openModulePopup(m, btnX, cy, btnW, rowH);
                }, null);
            }

            cy += rowH + gap;
        }

        gfx.pose().popMatrix();
        gfx.disableScissor();
        gfx.nextStratum();

        if (total > h) {
            menu.edges(gfx, x + menu.S(1), w - menu.S(2), top, bottom, scrolled, bg);
            menu.scrollbar(gfx, x + w - menu.S(7), top + menu.S(8), bottom - menu.S(8), (int) total, scrolled, "cat:" + category);
        }
    }

    /**
     * Рисует настройки одного модуля (popup).
     *
     * @param menu  текущее меню ArcanicMenu
     * @param module модуль, настройки которого рисуем
     * @param x     X начала панели
     * @param y     Y начала панели
     * @param w     ширина панели
     * @param h     высота панели
     * @param sc    оффсет скролла
     * @param top   верхняя граница скролла
     * @param bottom нижняя граница скролла
     */
    public static void drawModule(ArcanicMenu menu, ClientModule module,
                                   int x, int y, int w, int h, float sc, int top, int bottom) {
        var gfx = net.skycore.client.util.CurrentGuiGraphicsExtractorHelper.currentGuiGraphicsExtractor();
        List<Setting<?>> list = new ArrayList<>();
        for (Setting<?> s : module.getSettings()) if (s.isVisible()) list.add(s);

        int pad = menu.S(18), cw = w - pad * 2, head = menu.S(66);
        int inner = head;
        for (Setting<?> s : list) inner += rowH(s, cw, menu);
        inner += menu.S(8) + menu.S(40) + menu.S(8);

        boolean lt = menu.light();
        int bg = lt ? 0xFFFFFFFF : SkyPalette.card();
        BeginFrameHelper.rect(x, y, w, h)
                .color(bg)
                .radius(menu.S(13))
                .blur(menu.S(14), 0.55F)
                .draw();
        BeginFrameHelper.rect(x, y, w, h)
                .color(lt ? mixAlpha(SkyPalette.text(), 26) : 0x18FFFFFF)
                .border(Math.max(0.5f, menu.scale()), lt ? mixAlpha(SkyPalette.text(), 26) : 0x18FFFFFF)
                .radius(menu.S(13))
                .draw();

        // Заголовок модуля: имя, описание, переключатель
        int tgW = menu.S(34);
        BeginFrameHelper.text(x + pad, y + menu.S(15), menu.S(15), module.getName())
                .color(SkyPalette.text())
                .draw();
        BeginFrameHelper.text(x + pad, y + menu.S(36), menu.S(11), module.getDescription())
                .color(lt ? SkyPalette.muted() : 0xFF757D8A)
                .draw();

        // Переключатель модуля
        float mOn = module.isEnabled() ? 1 : 0;
        boolean tgHv = menu.hover(x + cw - tgW, y + menu.S(12), tgW, menu.S(30));
        drawToggle(menu, gfx, x + cw - tgW, y + menu.S(12), tgW, menu.S(30), mOn, tgHv ? 1 : 0);

        // Разделитель
        BeginFrameHelper.rect(x + pad, y + head - menu.S(4), cw, Math.max(1, menu.S(1)))
                .color(lt ? mixAlpha(SkyPalette.text(), 18) : 0x12FFFFFF)
                .draw();

        // Список настроек с скроллом
        float scrolled = menu.scrolled("pm:" + module.getName(), inner, y, y + h);
        gfx.enableScissor(x, top, x + w, bottom);
        gfx.pose().pushMatrix();
        gfx.pose().translate(0, -scrolled);

        int cy = y + head;
        for (Setting<?> s : list) {
            cy += drawSettingRow(menu, gfx, x + pad, cy, cw, s, scrolled, top, bottom) + menu.S(8);
        }

        gfx.pose().popMatrix();
        gfx.disableScissor();
        gfx.nextStratum();

        if (inner > h) {
            menu.edges(gfx, x + menu.S(1), w - menu.S(2), top, bottom, scrolled, bg);
            menu.scrollbar(gfx, x + w - menu.S(7), top + menu.S(8), bottom - menu.S(8), (int) inner, scrolled, "pm:" + module.getName());
        }
    }

    private static int rowH(Setting<?> s, int cw, ArcanicMenu menu) {
        if (s instanceof BooleanSetting) return menu.S(28);
        if (s instanceof NumberSetting) return menu.S(28);
        if (s instanceof ModeSetting) return menu.S(34);
        if (s instanceof KeyBindSetting) return menu.S(40);
        if (s instanceof StringSetting) return menu.S(34);
        if (s instanceof ColorSetting) return menu.S(34);
        if (s instanceof net.skycore.client.setting.ActionSetting) return menu.S(42);
        if (s instanceof net.skycore.client.setting.MultiSelectSetting) return menu.S(34);
        return menu.S(28);
    }

    private static int drawSettingRow(ArcanicMenu menu, net.minecraft.client.gui.GuiGraphicsExtractor gfx,
                                       int x, int y, int w, Setting<?> s, float sc, int top, int bottom) {
        if (s instanceof BooleanSetting bs) return drawBool(menu, gfx, x, y, w, bs, sc, top, bottom);
        if (s instanceof NumberSetting ns) return drawNumber(menu, gfx, x, y, w, ns);
        if (s instanceof ModeSetting ms) return drawMode(menu, gfx, x, y, w, ms);
        if (s instanceof KeyBindSetting ks) return drawKeyBind(menu, gfx, x, y, w, ks);
        if (s instanceof StringSetting ss) return drawString(menu, gfx, x, y, w, ss);
        if (s instanceof ColorSetting cs) return drawColor(menu, gfx, x, y, w, cs);
        if (s instanceof net.skycore.client.setting.ActionSetting as) return drawAction(menu, gfx, x, y, w, as);
        if (s instanceof net.skycore.client.setting.MultiSelectSetting mss) return drawMulti(menu, gfx, x, y, w, mss);
        return menu.S(28);
    }

    // ---------- индивидуальные строки ----------

    private static int drawBool(ArcanicMenu menu, net.minecraft.client.gui.GuiGraphicsExtractor gfx,
                                 int x, int y, int w, BooleanSetting s, float sc, int top, int bottom) {
        int h = menu.S(28);
        boolean lt = menu.light();
        boolean hv = menu.hover(x, y, w, h);
        int plate = lt ? 0x10000000 : SkyPalette.card() | 0xFF000000;
        BeginFrameHelper.rect(x, y, w, h)
                .color(plate)
                .radius(menu.S(8))
                .draw();
        BeginFrameHelper.text(x, y + 4, menu.S(11), s.getName())
                .color(SkyPalette.text())
                .draw();
        // Чекбокс справа
        int cb = menu.S(14);
        int cbX = x + w - cb - menu.S(8);
        int cbY = y + (h - cb) / 2;
        boolean checked = s.get();
        int cbColor = checked ? SkyPalette.accent() : mixAlpha(SkyPalette.text(), 22);
        BeginFrameHelper.rect(cbX, cbY, cb, cb)
                .color(cbColor)
                .radius(menu.S(4))
                .draw();
        if (checked) {
            SkyGlyph.draw(gfx, "w.plus", cbX + 2, cbY + 2, cb - 4, 0xFFFFFFFF);
        }
        if (hv) menu.clipped(x, y, w, h, sc, top, bottom, () -> {
            s.set(!s.get());
            net.skycore.client.SkycoreClient.config().save();
        }, null);
        return h;
    }

    private static int drawNumber(ArcanicMenu menu, net.minecraft.client.gui.GuiGraphicsExtractor gfx,
                                   int x, int y, int w, NumberSetting s) {
        int h = menu.S(28);
        BeginFrameHelper.text(x, y + 4, menu.S(11), s.getName())
                .color(SkyPalette.text())
                .draw();
        BeginFrameHelper.text(x + w - menu.S(60), y + 4, menu.S(11), String.format("%.1f%s", s.get(), s.getSuffix()))
                .color(SkyPalette.muted())
                .draw();
        // Прогресс-бар ползунка
        double pct = s.ratio(); // 0..1
        int barW = w - menu.S(80);
        int barX = x;
        int barY = y + h - menu.S(6);
        int barH = menu.S(4);
        BeginFrameHelper.rect(barX, barY, barW, barH)
                .color(mixAlpha(SkyPalette.text(), 12))
                .radius(barH / 2)
                .draw();
        int fillW = (int) (barW * pct);
        BeginFrameHelper.rect(barX, barY, fillW, barH)
                .color(SkyPalette.accent())
                .radius(barH / 2)
                .draw();
        return h;
    }

    private static int drawMode(ArcanicMenu menu, net.minecraft.client.gui.GuiGraphicsExtractor gfx,
                                 int x, int y, int w, ModeSetting s) {
        int h = menu.S(34);
        BeginFrameHelper.text(x, y + 4, menu.S(11), s.getName())
                .color(SkyPalette.text())
                .draw();
        BeginFrameHelper.text(x, y + h - menu.S(14), menu.S(10), s.get())
                .color(SkyPalette.muted())
                .draw();
        // Стрелка выбора режима справа
        SkyGlyph.draw(gfx, "w.chevron_down", x + w - menu.S(16), y + h / 2 - 8, 16, mixAlpha(SkyPalette.text(), 40));
        return h;
    }

    private static int drawKeyBind(ArcanicMenu menu, net.minecraft.client.gui.GuiGraphicsExtractor gfx,
                                    int x, int y, int w, KeyBindSetting s) {
        int h = menu.S(40);
        BeginFrameHelper.text(x, y + 4, menu.S(11), s.getName())
                .color(SkyPalette.text())
                .draw();
        String label = s.get() == KeyBindSetting.UNBOUND ? "None" : net.skycore.client.module.KeyBinds.name(s.get());
        int labelW = menu.S(50);
        int labelX = x + w - labelW - menu.S(8);
        int labelY = y + h - menu.S(14);
        BeginFrameHelper.text(labelX, labelY, menu.S(10), label)
                .color(SkyPalette.muted())
                .draw();
        // Чекбокс бинда (клик для перехвата клавиши)
        int cb = menu.S(14);
        int cbX = x + w - cb - menu.S(8);
        int cbY = y + (h - cb) / 2;
        BeginFrameHelper.rect(cbX, cbY, cb, cb)
                .color(mixAlpha(SkyPalette.text(), 22))
                .radius(menu.S(4))
                .draw();
        SkyGlyph.draw(gfx, "w.keyboard", cbX + 3, cbY + 3, cb - 6, mixAlpha(SkyPalette.text(), 60));
        return h;
    }

    private static int drawString(ArcanicMenu menu, net.minecraft.client.gui.GuiGraphicsExtractor gfx,
                                   int x, int y, int w, StringSetting s) {
        int h = menu.S(34);
        BeginFrameHelper.text(x, y + 4, menu.S(11), s.getName())
                .color(SkyPalette.text())
                .draw();
        BeginFrameHelper.text(x, y + h - menu.S(14), menu.S(10), s.get())
                .color(SkyPalette.muted())
                .draw();
        return h;
    }

    private static int drawColor(ArcanicMenu menu, net.minecraft.client.gui.GuiGraphicsExtractor gfx,
                                  int x, int y, int w, ColorSetting s) {
        int h = menu.S(34);
        BeginFrameHelper.text(x, y + 4, menu.S(11), s.getName())
                .color(SkyPalette.text())
                .draw();
        BeginFrameHelper.text(x, y + h - menu.S(14), menu.S(10), String.format("#%06X", s.get() & 0xFFFFFF))
                .color(SkyPalette.muted())
                .draw();
        // Цветной квадратик-превью справа
        int sw = menu.S(20);
        int swX = x + w - sw - menu.S(8);
        int swY = y + h / 2 - sw / 2;
        int color = s.get() | 0xFF000000;
        BeginFrameHelper.rect(swX, swY, sw, sw)
                .color(color)
                .radius(menu.S(4))
                .draw();
        BeginFrameHelper.rect(swX, swY, sw, sw)
                .color(0xFF000000)
                .border(1.0F, 0xFF000000)
                .radius(menu.S(4))
                .draw();
        return h;
    }

    private static int drawAction(ArcanicMenu menu, net.minecraft.client.gui.GuiGraphicsExtractor gfx,
                                   int x, int y, int w, net.skycore.client.setting.ActionSetting s) {
        int h = menu.S(42);
        boolean hv = menu.hover(x, y, w, h);
        int plate = hv ? mixAlpha(SkyPalette.text(), 12) : mixAlpha(SkyPalette.text(), 6);
        BeginFrameHelper.rect(x, y, w, h)
                .color(plate)
                .radius(menu.S(8))
                .draw();
        BeginFrameHelper.text(x, y + 4, menu.S(11), s.getActionLabel())
                .color(SkyPalette.text())
                .draw();
        if (hv) menu.clipped(x, y, w, h, 0, 0, Integer.MAX_VALUE, s::press, null);
        return h;
    }

    private static int drawMulti(ArcanicMenu menu, net.minecraft.client.gui.GuiGraphicsExtractor gfx,
                                  int x, int y, int w, net.skycore.client.setting.MultiSelectSetting s) {
        int h = menu.S(34);
        BeginFrameHelper.text(x, y + 4, menu.S(11), s.getName())
                .color(SkyPalette.text())
                .draw();
        // Выбранные значения
        var selected = s.get();
        String joined = selected.isEmpty() ? "None" : String.join(", ", selected);
        if (joined.length() > 22) joined = joined.substring(0, 19) + "...";
        BeginFrameHelper.text(x, y + h - menu.S(14), menu.S(10), joined)
                .color(SkyPalette.muted())
                .draw();
        return h;
    }

    private static int drawToggle(ArcanicMenu menu, net.minecraft.client.gui.GuiGraphicsExtractor gfx,
                                   int x, int y, int w, int h, float on, float hover) {
        int plate = on > 0 ? SkyPalette.accent() : mixAlpha(SkyPalette.text(), 12);
        BeginFrameHelper.rect(x, y, w, h)
                .color(plate)
                .radius(h / 2)
                .draw();
        int knob = menu.S(14);
        int knobX = x + (on > 0.5f ? w - knob - menu.S(2) : menu.S(2));
        int knobY = y + (h - knob) / 2;
        BeginFrameHelper.rect(knobX, knobY, knob, knob)
                .color(0xFFFFFFFF)
                .radius(knob / 2)
                .draw();
        menu.clipped(x, y, w, h, 0, 0, Integer.MAX_VALUE, () -> {
            // Toggle handled by caller (module toggle)
        }, null);
    }

    private static int mixAlpha(int color, int alpha) {
        return (alpha << 24) | (color & 0xFFFFFF);
    }
}
