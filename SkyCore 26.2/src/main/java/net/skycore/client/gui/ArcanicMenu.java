package net.skycore.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.skycore.client.module.ClientModule;
import net.skycore.client.module.ModuleCategory;
import net.skycore.client.util.BeginFrameHelper;
import net.skycore.client.util.CurrentGuiGraphicsExtractorHelper;

/**
 * Порт главного меню Arcanic Visuals под инфраструктуру SkyCore 26.2.
 *
 * <p>Воспроизводит размеры, расположение, задний фон и углы главного окна
 * ArcanicMenu один в один. Цвета берутся из {@link SkyPalette}, фоновый
 * эффект — из {@link SkyMenuBackdrop}. Поддерживает страницы: главная панель,
 * настройки меню (PrefsPage), настройки модулей (ModuleSettings), косметика
 * (SkyCosmetics).
 *
 * <p>Адаптировано из {@code net.arcanic.visuals.gui.ArcanicMenu}
 * (Arcanic Visuals, GPL-3.0).
 */
public final class ArcanicMenu extends Screen {

    /** Основная панель меню (как {@code panel()} в Arcanic). */
    private static final float PANEL_W = 1010.0F;
    private static final float PANEL_H = 640.0F;
    private static final float PANEL_MIN_W_MARGIN = 80.0F;
    private static final float PANEL_MIN_H_MARGIN = 210.0F;
    private static final float PANEL_Y_OFFSET = -28.0F + 78.0F;
    private static final float PANEL_RADIUS = 12.0F;

    private float u = 1.0F; // текущий масштаб GUI
    private int pw, ph, px, py; // размеры и позиция основной панели в экранных пикселях
    private long openedAt;
    private boolean closing;

    /** Текущая страница контента: null = главная, "prefs" = настройки меню,
     * "modules:<cat>" = настройки модулей категории, "module:<name>" = настройки одного модуля,
     * "cosmetics" = косметика. */
    private String content = null;

    /** Модуль, чьи настройки показываются (для popup-окна настроек модуля). */
    private ClientModule popMod = null;
    private int popX, popY, popW, popH;

    public ArcanicMenu() {
        super(Component.literal("Arcanic"));
        this.openedAt = System.nanoTime();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float pt) {
        CurrentGuiGraphicsExtractorHelper.enter2D(guiGraphicsExtractor, Minecraft.getInstance().getDeltaTracker());
        try {
            BeginFrameHelper.beginFrame();

            int sw = Minecraft.getInstance().getWindow().getWidth();
            int sh = Minecraft.getInstance().getWindow().getHeight();

            // Масштаб как в Arcanic: u = clamp(min(sh/1080, sw/1060) * menuScale * 0.86, 0.45, 2.2)
            float menuScale = (float) SkyPalette.menuScale;
            float scaleRaw = Math.min(sh / 1080.0F, sw / 1060.0F) * menuScale * 0.86F;
            u = Math.max(0.45F, Math.min(2.2F, scaleRaw));

            // Размеры панели как в Arcanic
            int swScaled = Math.round(sw * u);
            int shScaled = Math.round(sh * u);
            pw = Math.min(Math.round(PANEL_W * u), swScaled - Math.round(PANEL_MIN_W_MARGIN * u));
            ph = Math.min(Math.round(PANEL_H * u), shScaled - Math.round(PANEL_MIN_H_MARGIN * u));
            px = (swScaled - pw) / 2;
            py = (shScaled - ph - Math.round(PANEL_Y_OFFSET * u)) / 2;

            // Анимация открытия
            float open = 1.0F;
            if (closing) {
                open = Math.max(0.0F, 1.0F - (float) (System.nanoTime() - openedAt) / 1_000_000_000.0F * 18.0F);
                if (open < 0.02F) {
                    closing = false;
                }
            }
            float e = ease(open);

            // --- Нижнее облоко / анимированный фон меню (как в Arcanic MenuBackdrop) ---
            SkyMenuBackdrop.draw(open);

            // --- Задний затемнённый фон (как в Arcanic draw(): Px.rect(0,0,sw,sh, ...)) ---
            float dim = 0.40F;
            int bgAlpha = Math.round(255.0F * dim * open);
            int bgRgb = SkyPalette.bg() & 0xFFFFFF;
            BeginFrameHelper.rect(0, 0, sw, sh)
                    .color((bgAlpha << 24) | bgRgb)
                    .draw();

            // --- Тень под панелью ---
            BeginFrameHelper.rect(px, py + Math.round(16.0F * u), pw, ph)
                    .color(0x40000000)
                    .radius(Math.round(PANEL_RADIUS * u))
                    .blur(Math.round(46.0F * u), e)
                    .draw();

            // --- Сама панель (как в Arcanic: Px.round с радиусом S(12), с backdrop blur / frosted glass) ---
            int panelColor = SkyPalette.card();
            BeginFrameHelper.rect(px, py, pw, ph)
                    .color(panelColor)
                    .radius(Math.round(PANEL_RADIUS * u))
                    .blur(Math.round(14.0F * u), 0.55F)
                    .draw();

            // --- Обводка панели (волосная, как в Arcanic Px.outline) ---
            int borderColor = SkyPalette.line();
            BeginFrameHelper.rect(px, py, pw, ph)
                    .color(borderColor)
                    .border(Math.max(0.5F, u), borderColor)
                    .radius(Math.round(PANEL_RADIUS * u))
                    .draw();

            // --- Контент страницы ---
            if ("prefs".equals(content)) {
                SkyMenuPrefs.apply(); // применяет настройки меню к SkyPalette (анимации, масштаб)
                SkyPrefsPage.draw(this, px, pw, py, ph, 0);
            } else if (content != null && content.startsWith("module:")) {
                String modName = content.substring("module:".length());
                var mm = net.skycore.client.SkycoreClient.modules();
                mm.getModules().stream()
                        .filter(m -> m.getName().equals(modName))
                        .findFirst()
                        .ifPresent(m -> SkyModuleSettings.drawModule(this, m, px, py, pw, ph, 0, py, py + ph));
            } else if (content != null && content.startsWith("modules:")) {
                String catName = content.substring("modules:".length());
                ModuleCategory cat = switch (catName) {
                    case "Combat" -> ModuleCategory.COMBAT;
                    case "Movement" -> ModuleCategory.MOVEMENT;
                    case "Render" -> ModuleCategory.RENDER;
                    case "Player" -> ModuleCategory.PLAYER;
                    case "Misc" -> ModuleCategory.MISC;
                    default -> ModuleCategory.MISC;
                };
                SkyModuleSettings.drawCategory(this, cat, px, py, pw, ph, 0, py, py + ph);
            } else if ("cosmetics".equals(content)) {
                drawCosmeticsPage(guiGraphicsExtractor);
            }

            // --- Popup настроек отдельного модуля ---
            if (popMod != null) {
                SkyModuleSettings.drawModule(this, popMod, popX, popY, popW, popH, 0, popY, popY + popH);
            }

            BeginFrameHelper.flush();
        } finally {
            CurrentGuiGraphicsExtractorHelper.exit2D();
        }
    }

    private void drawCosmeticsPage(net.minecraft.client.gui.GuiGraphicsExtractor gfx) {
        // Страница косметики — будет детализировано отдельно
        int x = px, y = py, w = pw;
        SkyCosmetics.Category[] cats = SkyCosmetics.Category.values();
        int colW = (w - SkyCosmetics.SLOTS.length * 0) / cats.length;
        for (int i = 0; i < cats.length; i++) {
            SkyCosmetics.Category cat = cats[i];
            int cx = x + i * colW;
            int cy = y + SkyCosmetics.SLOTS.length * 0 + 50;
            int cw = colW - 20;
            int ch = 120;
            boolean lt = light();
            int plate = lt ? 0x10000000 : SkyPalette.card() | 0xFF000000;
            BeginFrameHelper.rect(cx, cy, cw, ch)
                    .color(plate)
                    .radius(S(12))
                    .draw();
            SkyGlyph.draw(gfx, SkyCosmetics.iconKey(cat), cx + S(16), cy + S(16), S(32), accText());
            BeginFrameHelper.text(cx + S(56), cy + S(16), S(14), cat.label)
                    .color(SkyPalette.text())
                    .draw();
            BeginFrameHelper.text(cx + S(56), cy + S(36), S(11), SkyCosmetics.get(cat))
                    .color(SkyPalette.muted())
                    .draw();
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    /** Плавное затухание (easeOutCubic). */
    public static float ease(float t) {
        t = Math.max(0.0F, Math.min(1.0F, t));
        return 1.0F - (1.0F - t) * (1.0F - t) * (1.0F - t);
    }

    /** Масштаб значения в экранные пиксели (как S() в Arcanic). */
    public int S(float v) {
        return Math.max(1, Math.round(v * u));
    }

    /** Светлая ли текущая тема. */
    public boolean light() {
        return SkyPalette.PALETTES[Math.floorMod(SkyPalette.palette, SkyPalette.PALETTES.length)].light();
    }

    /** Текущее время в наносекундах (как now в Arcanic). */
    public long now() {
        return System.nanoTime();
    }

    /** Время начала показа текущего контента (заглушка для анимации появления). */
    public long contentAt() {
        return openedAt;
    }

    /** Цвет акцентного текста. */
    public int accText() {
        return SkyPalette.accent() | 0xFF000000;
    }

    /** Базовый цвет для краёв панели. */
    public int baseColour() {
        return SkyPalette.card();
    }

    public void startClose() {
        this.closing = true;
        this.openedAt = System.nanoTime();
    }

    /** Текущий масштаб GUI (как u в Arcanic). */
    public float scale() {
        return u;
    }

    /** Ширина основной панели в экранных пикселях. */
    public int panelW() {
        return pw;
    }

    /** Высота основной панели в экранных пикселях. */
    public int panelH() {
        return ph;
    }

    /** X-позиция основной панели. */
    public int panelX() {
        return px;
    }

    /** Y-позиция основной панели. */
    public int panelY() {
        return py;
    }

    /** Переключить страницу контента. */
    public void setContent(String content) {
        this.content = content;
        this.openedAt = System.nanoTime();
    }

    /** Текущая страница контента. */
    public String content() {
        return content;
    }

    /**
     * Рисует края панели (тень/обводка по краям скроллируемой области).
     */
    public void edges(GuiGraphics g, int x, int w, int top, int bottom, float sc, int colour) {
        // Реализуется при полной прорисовке скроллируемого контента
    }

    /**
     * Рисует ползунок скролла.
     */
    public void scrollbar(GuiGraphics g, int x, int top, int bottom, int total, float sc, String id) {
        // Реализуется при полной прорисовке скроллируемого контента
    }

    /**
     * Проверяет, находится ли курсор над прямоугольником.
     */
    public boolean hover(int x, int y, int w, int h) {
        return false; // будет реализовано с отслеживанием мыши
    }

    /** Горячий элемент (для звука наведения). */
    public void hot(String id) {
        // Заглушка
    }

    /** Анимация значения. */
    public float an(String id, float target, int speed) {
        return target; // Заглушка
    }

    /** Твиан значения. */
    public float tween(String id, float from, float to, int speed) {
        return to; // Заглушка
    }

    /** Отрисовать иконку. */
    public void glyph(GuiGraphics g, String icon, int x, int y, int size, int color) {
        SkyGlyph.draw(g, icon, x, y, size, color);
    }

    /** Подсвеченная иконка. */
    public void icon(GuiGraphics g, String icon, int x, int y, int size, boolean hover) {
        SkyGlyph.draw(g, icon, x, y, size, hover ? accText() : SkyPalette.muted());
    }

    /** Кнопка-переключатель. */
    public void toggle(GuiGraphics g, int x, int y, int w, int h, float on, float hover) {
        // Заглушка
    }

    /** Поле ввода значения. */
    public void valueBox(GuiGraphics g, String text, int x, int y, boolean cap, boolean hover) {
        // Заглушка
    }

    /**
     * Ограниченная область для клика.
     */
    public void clipped(int x, int y, int w, int h, float sc, int top, int bottom,
                        Runnable click, Runnable right) {
        // Заглушка — будет реализовано с отслеживанием мыши
    }

    /** Подсвеченная кнопка переключателя. */
    public void toggle(GuiGraphics g, int x, int y, int w, int h, float on) {
        toggle(g, x, y, w, h, on, 0);
    }

    /** Текущий Y курсора (заглушка). */
    public int my() {
        return 0;
    }

    /** Блокировка ввода (заглушка). */
    public boolean blocked() {
        return false;
    }

    /** Подсвечен курсор (заглушка). */
    public boolean under() {
        return false;
    }

    /** Смещение скролла для области настроек (аналог scrolled в Arcanic). */
    public float scrolled(String id, float total, int top, int bottom) {
        // Заглушка — вернёт 0 (без скролла). Реализуется с отслеживанием колеса мыши.
        return 0.0F;
    }

    // ---------- Модули и бинды ----------

    /** Открыть popup настроек модуля. */
    public void openModulePopup(ClientModule module, int x, int y, int w, int h) {
        this.popMod = module;
        this.popX = x;
        this.popY = y;
        this.popW = w;
        this.popH = h;
    }

    /** Закрыть popup настроек модуля. */
    public void closeModulePopup() {
        this.popMod = null;
    }

    /** Обработать нажатие клавиши (бинд модуля). */
    public void onKey(int code) {
        SkyKeys.pressed(code);
    }
}
