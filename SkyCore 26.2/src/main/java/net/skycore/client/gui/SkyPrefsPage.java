package net.skycore.client.gui;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.skycore.client.setting.BooleanSetting;
import net.skycore.client.setting.ModeSetting;
import net.skycore.client.setting.NumberSetting;
import net.skycore.client.setting.Setting;
import net.skycore.client.util.BeginFrameHelper;

/**
 * Страница настроек клиента, портированная из Arcanic Visuals.
 *
 * <p>Повторяет {@code net.arcanic.visuals.gui.PrefsPage}: группы настроек
 * (Основное, Звук, Вид, Поведение), каждая в своей карточке с иконкой,
 * заголовком, подзаголовком и списком полем. Настройки из
 * {@link SkyMenuPrefs} распределены по соответствующим группам.
 * Отображается внутри {@link ArcanicMenu} и использует {@link SkyPalette}
 * для цветов.
 *
 * <p>Адаптировано из {@code net.arcanic.visuals.gui.PrefsPage}
 * (Arcanic Visuals, GPL-3.0).
 */
public final class SkyPrefsPage {

    private SkyPrefsPage() {
    }

    /** Одна группа настроек: иконка, заголовок, подзаголовок, поля. */
    public record Group(String iconKey, String titleKey, String subtitleKey,
                         List<Setting<?>> settings) {
    }

    /** Все группы настроек. Настройки из {@link SkyMenuPrefs} распределены по группам. */
    public static List<Group> groups() {
        List<Group> out = new ArrayList<>();

        // Основное — язык
        out.add(new Group(
                "w.sliders",
                "Основное",
                "Язык интерфейса",
                List.of(SkyMenuPrefs.language)
        ));

        // Звук — звуки меню, громкость, звуки наведения/ползунков
        out.add(new Group(
                "w.headset",
                "Звук",
                "Щелчки и отклик меню",
                List.of(
                        SkyMenuPrefs.sounds,
                        SkyMenuPrefs.volume,
                        SkyMenuPrefs.hoverSounds,
                        SkyMenuPrefs.sliderSounds
                )
        ));

        // Вид — размытие, затемнение, свет, волны, подсказки
        out.add(new Group(
                "w.swatch",
                "Вид",
                "Фон, свет и подсказки",
                List.of(
                        SkyMenuPrefs.blur,
                        SkyMenuPrefs.dim,
                        SkyMenuPrefs.spotlight,
                        SkyMenuPrefs.ripples,
                        SkyMenuPrefs.tips,
                        SkyMenuPrefs.notify
                )
        ));

        // Поведение — анимации, скорость, размер, перетаскивание, позиция
        out.add(new Group(
                "w.bolt",
                "Поведение",
                "Анимации, размер и положение",
                List.of(
                        SkyMenuPrefs.animations,
                        SkyMenuPrefs.animSpeed,
                        SkyMenuPrefs.scale,
                        SkyMenuPrefs.drag,
                        SkyMenuPrefs.remember
                )
        ));

        // Косметика — крила, рюкзаки, шапки, плащ
        out.add(new Group(
                "w.bolt",
                "Косметика",
                "Крила, рюкзаки, шапки, плащ",
                List.of(
                        SkyCosmetics.instance().wings,
                        SkyCosmetics.instance().backpacks,
                        SkyCosmetics.instance().hats,
                        SkyCosmetics.instance().masks,
                        SkyCosmetics.instance().pets,
                        SkyCosmetics.instance().capeEnabled,
                        SkyCosmetics.instance().capeMovement,
                        SkyCosmetics.instance().capeGravity,
                        SkyCosmetics.instance().capeWind
                )
        ));

        return out;
    }

    /**
     * Рисует страницу настроек.
     *
     * @param menu   текущее меню ArcanicMenu
     * @param cx0    X начала области контента
     * @param cw0    ширина области контента
     * @param top    верхняя граница области скролла
     * @param bottom нижняя граница области скролла
     * @param sc     текущий оффсет скролла
     */
    public static void draw(ArcanicMenu menu,
                            int cx0, int cw0, int top, int bottom, float sc) {
        List<Group> groups = groups();
        int gap = menu.S(12), cw = (cw0 - gap) / 2;
        int[] colY = new int[2];
        int[] gx = new int[groups.size()], gy = new int[groups.size()], gh = new int[groups.size()];

        // Распределение групп по двум колонкам
        for (int i = 0; i < groups.size(); i++) {
            int c = colY[0] <= colY[1] ? 0 : 1;
            gx[i] = cx0 + c * (cw + gap);
            gy[i] = top + menu.S(2) + colY[c];
            gh[i] = groupH(cw, groups.get(i));
            colY[c] += gh[i] + gap;
        }
        int total = Math.max(colY[0], colY[1]) + menu.S(4);

        var gfx = net.skycore.client.util.CurrentGuiGraphicsExtractorHelper.currentGuiGraphicsExtractor();

        // Рисуем через BeginFrameHelper (в контексте enter2D уже активен)
        for (int i = 0; i < groups.size(); i++) {
            Group gr = groups.get(i);
            float app = menu.ease((menu.now() - menu.contentAt() - i * 40L) / 300f);
            int x = gx[i], y = gy[i], w = cw, h = gh[i], R = menu.S(12), pad = menu.S(18);
            boolean lt = menu.light();

            // Карточка группы
            int plateColor = lt ? 0x10000000 : SkyPalette.card();
            BeginFrameHelper.rect(x, y, w, h)
                    .color(plateColor)
                    .radius(R)
                    .draw();
            BeginFrameHelper.rect(x, y, w, h)
                    .color(lt ? mixAlpha(SkyPalette.text(), 22) : 0x18FFFFFF)
                    .border(Math.max(0.5f, menu.scale()), lt ? mixAlpha(SkyPalette.text(), 22) : 0x18FFFFFF)
                    .radius(R)
                    .draw();

            // Иконка группы в мягком квадрате
            int ib = menu.S(34);
            int iconBg = lt ? mixAlpha(SkyPalette.accent(), 22) : mixAlpha(SkyPalette.accent(), 34);
            BeginFrameHelper.rect(x + pad, y + menu.S(15), ib, ib)
                    .color(iconBg)
                    .radius(menu.S(10))
                    .draw();
            SkyGlyph.draw(gfx, gr.iconKey(),
                    x + pad + (ib - menu.S(17)) / 2,
                    y + menu.S(15) + (ib - menu.S(17)) / 2,
                    menu.S(17), menu.accText());

            // Заголовок и подзаголовок
            BeginFrameHelper.text(x + pad + ib + menu.S(12), y + menu.S(16), menu.S(15), gr.titleKey())
                    .color(SkyPalette.text())
                    .draw();
            BeginFrameHelper.text(x + pad + ib + menu.S(12), y + menu.S(36), menu.S(12), gr.subtitleKey())
                    .color(SkyPalette.muted())
                    .draw();

            // Разделитель под заголовком
            BeginFrameHelper.rect(x + pad, y + menu.S(62), w - pad * 2, Math.max(1, menu.S(1)))
                    .color(lt ? mixAlpha(SkyPalette.text(), 18) : 0x12FFFFFF)
                    .draw();

            // Поля настроек группы
            int rowY = y + menu.S(64);
            for (Setting<?> s : gr.settings) {
                if (!s.isVisible()) continue;
                drawSettingRow(menu, gfx, x, rowY, w - pad * 2, s);
                rowY += rowH(s, menu) + menu.S(8);
            }
        }

        // Края панели и ползунок скролла
        menu.edges(gfx, cx0 - menu.S(12), cw0 + menu.S(24), top, bottom, sc, menu.baseColour());
        menu.scrollbar(gfx, cx0 + cw0 + menu.S(14), top, bottom, total, sc, "prefs");
    }

    private static void drawSettingRow(ArcanicMenu menu, net.minecraft.client.gui.GuiGraphicsExtractor gfx,
                                        int x, int y, int w, Setting<?> s) {
        boolean lt = menu.light();
        int h = rowH(s, menu);
        int plate = lt ? 0x10000000 : SkyPalette.card() | 0xFF000000;
        BeginFrameHelper.rect(x, y, w, h)
                .color(plate)
                .radius(menu.S(8))
                .draw();
        BeginFrameHelper.text(x, y + 4, menu.S(11), s.getName())
                .color(SkyPalette.text())
                .draw();

        // Значение на правой стороне строки
        String valueText = valueText(s);
        if (valueText != null && !valueText.isEmpty()) {
            BeginFrameHelper.text(x + w - menu.S(60), y + h - menu.S(14), menu.S(10), valueText)
                    .color(SkyPalette.muted())
                    .draw();
        }

        // Чекбокс для Boolean
        if (s instanceof BooleanSetting bs) {
            int cb = menu.S(14);
            int cbX = x + w - cb - menu.S(8);
            int cbY = y + (h - cb) / 2;
            BeginFrameHelper.rect(cbX, cbY, cb, cb)
                    .color(bs.get() ? SkyPalette.accent() : mixAlpha(SkyPalette.text(), 22))
                    .radius(menu.S(4))
                    .draw();
            if (bs.get()) {
                SkyGlyph.draw(gfx, "w.plus", cbX + 2, cbY + 2, cb - 4, 0xFFFFFFFF);
            }
        }
    }

    private static String valueText(Setting<?> s) {
        if (s instanceof ModeSetting ms) return ms.get();
        if (s instanceof NumberSetting ns) return String.format("%.1f%s", ns.get(), ns.getSuffix());
        if (s instanceof net.skycore.client.setting.KeyBindSetting ks) {
            int code = ks.get();
            if (code == net.skycore.client.setting.KeyBindSetting.UNBOUND) return "None";
            return net.skycore.client.module.KeyBinds.name(code);
        }
        if (s instanceof net.skycore.client.setting.StringSetting ss) return ss.get();
        if (s instanceof net.skycore.client.setting.ColorSetting cs)
            return String.format("#%06X", cs.get() & 0xFFFFFF);
        return null;
    }

    private static int rowH(Setting<?> s, ArcanicMenu menu) {
        if (s instanceof BooleanSetting) return menu.S(28);
        if (s instanceof NumberSetting) return menu.S(28);
        if (s instanceof ModeSetting) return menu.S(30);
        if (s instanceof net.skycore.client.setting.KeyBindSetting) return menu.S(30);
        if (s instanceof net.skycore.client.setting.StringSetting) return menu.S(30);
        if (s instanceof net.skycore.client.setting.ColorSetting) return menu.S(30);
        if (s instanceof net.skycore.client.setting.ActionSetting) return menu.S(34);
        if (s instanceof net.skycore.client.setting.MultiSelectSetting) return menu.S(30);
        return menu.S(28);
    }

    private static int groupH(int cw, Group gr) {
        int rows = 0;
        for (Setting<?> s : gr.settings) {
            if (s.isVisible()) rows++;
        }
        // Заголовок + иконка + разделитель + поля
        return 64 + 1 + rows * 36 + 12;
    }

    private static int mixAlpha(int color, int alpha) {
        return (alpha << 24) | (color & 0xFFFFFF);
    }
}
