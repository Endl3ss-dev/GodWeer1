package net.skycore.client.gui;

/**
 * Рендеринг иконок меню, портированное из Arcanic Visuals.
 *
 * <p>Сопоставляет ключи иконок (например, {@code w.sliders}, {@code w.headset},
 * {@code w.swatch}, {@code w.bolt}) с символами из шрифтов lucide.ttf и
 * arcanic_glyphs.ttf, скопированных из Arcanic в
 * {@code assets/skycore/font/}. Рисование выполняется через
 * {@link net.skycore.client.util.BeginFrameHelper#text}.
 *
 * <p>Адаптировано из {@code net.arcanic.visuals.render.SectionIcons} и
 * {@code net.arcanic.visuals.render.Px.icon} (Arcanic Visuals, GPL-3.0).
 */
public final class SkyGlyph {

    private SkyGlyph() {
    }

    /** Коды иконок Lucide (приватная используемая область Unicode). */
    private static final java.util.Map<String, String> ICONS = new java.util.HashMap<>();

    static {
        // Настройки / предпочтения
        ICONS.put("w.sliders", "\uE900");
        ICONS.put("w.headset", "\uE901");
        ICONS.put("w.swatch", "\uE902");
        ICONS.put("w.bolt", "\uE903");
        ICONS.put("w.bookmark", "\uE904");
        ICONS.put("w.folder", "\uE905");
        ICONS.put("w.cloud", "\uE906");
        ICONS.put("w.save", "\uE907");
        ICONS.put("w.refresh", "\uE908");
        ICONS.put("w.send", "\uE909");
        ICONS.put("w.chev-right", "\uE90A");
        ICONS.put("w.plus", "\uE90B");

        // Аркановые символы
        ICONS.put("sparkles", "\uF000");
        ICONS.put("pipette", "\uF001");
        ICONS.put("user", "\uF002");
        ICONS.put("x", "\uF003");
        ICONS.put("play", "\uF004");
        ICONS.put("players", "\uF005");
        ICONS.put("user-round", "\uF006");
        ICONS.put("wand-sparkles", "\uF007");
        ICONS.put("presets-s", "\uF008");
        ICONS.put("trash", "\uF009");
    }

    /**
     * Рисует иконку.
     *
     * @param gfx  извлекатель состояния GUI (в контексте enter2D)
     * @param icon ключ иконки (например, {@code w.sliders})
     * @param x    X позиция
     * @param y    Y позиция
     * @param size размер в пикселях
     * @param color цвет иконки (ARGB)
     */
    public static void draw(net.minecraft.client.gui.GuiGraphicsExtractor gfx,
                            String icon, int x, int y, int size, int color) {
        String ch = ICONS.getOrDefault(icon, "?");
        net.skycore.client.util.BeginFrameHelper.text(x, y, size, ch)
                .color(color)
                .draw();
    }
}
