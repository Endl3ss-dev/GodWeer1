package net.skycore.client.gui.menu;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.skycore.client.SkycoreClient;
import net.skycore.client.config.ConfigManager;
import net.skycore.client.gui.menu.MenuText.Weight;
import net.skycore.client.module.ClientModule;
import net.skycore.client.module.KeyBinds;
import net.skycore.client.module.ModuleCategory;
import net.skycore.client.module.ModuleManager;
import net.skycore.client.setting.ActionSetting;
import net.skycore.client.setting.BooleanSetting;
import net.skycore.client.setting.ColorSetting;
import net.skycore.client.setting.KeyBindSetting;
import net.skycore.client.setting.ModeSetting;
import net.skycore.client.setting.MultiSelectSetting;
import net.skycore.client.setting.NumberSetting;
import net.skycore.client.setting.Setting;
import net.skycore.client.setting.StringSetting;
import net.skycore.client.util.BeginFrameHelper;
import net.skycore.client.util.CurrentGuiGraphicsExtractorHelper;
import net.skycore.client.util.IconGlyphs;
import net.skycore.client.util.LegacyIconGlyphs;

import java.awt.Color;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Locale;

/**
 * Меню клиента — ClickGUI.
 *
 * <p>Порт {@code swerve.ui.menu.MenuScreen} из клиента Swerve (сам он — порт
 * Arcanic Visuals, GPL-3.0) в SkyCore 26.2.
 *
 * <p>Что перенесено: компоновка (плашка бренда, панель, вкладки категорий, поиск,
 * карточки модулей в две колонки, всплывающее окно настроек, нижний док, страница
 * настроек меню с палитрами), поведение (наведение, назначение клавиш, тоглы,
 * ползунки с перетаскиванием, сегменты режимов, выпадающие списки, чипы
 * множественного выбора, ввод строк, выбор цвета), анимации (пружины и тайм-твины,
 * «влёт» карточек, появление док-подписей) и звуковой отклик.
 *
 * <p>Что адаптировано под принимающий клиент:
 * <ul>
 *   <li>модель данных — {@link ClientModule} и {@link Setting} вместо
 *       {@code Module}/{@code swerve.modules.api.setting.*};
 *   <li>категорий пять ({@link ModuleCategory}), поэтому и вкладок пять;
 *   <li>отрисовка — {@link MenuDraw} поверх {@link BeginFrameHelper} (см. его javadoc);
 *   <li>текст — штатные Inter-шрифты клиента, иконки — его иконочные шрифты;
 *   <li>настройки меню сохраняются ({@link MenuPrefs}); в источнике они жили в статике;
 *   <li>строк настроек больше: поддержаны {@link ColorSetting} и {@link MultiSelectSetting},
 *       которых в модели Swerve не было, зато нет косметики и скриптов Swerve —
 *       этот функционал в SkyCore отсутствует, и «мёртвых» кнопок здесь не осталось.
 * </ul>
 *
 * <p>Координаты — GUI-пиксели. Всё рисуется внутри {@code enter2D}/{@code beginFrame},
 * как принято в клиенте: без этого {@link BeginFrameHelper} бросает исключение, а
 * очередь отрисовки не сбрасывается.
 */
public final class MenuScreen extends Screen {

    private enum Page {
        MODULES,
        SETTINGS
    }

    private static final ModuleCategory[] TABS = {
            ModuleCategory.COMBAT,
            ModuleCategory.MOVEMENT,
            ModuleCategory.RENDER,
            ModuleCategory.PLAYER,
            ModuleCategory.MISC,
    };

    private static final String[] TAB_TITLES = {
            "Бой",
            "Движение",
            "Визуал",
            "Игрок",
            "Утилиты",
    };

    /** Сколько настроек резервируется на модуль в упакованном индексе анимаций. */
    static final int SET_PER_MODULE = 32;
    /** Сколько пунктов списка/сегментов резервируется в младших разрядах payload. */
    static final int ITEM_STRIDE = 16;

    // ---- действия кликов
    static final int ACT_NONE = 0;
    static final int ACT_TAB = 1;
    static final int ACT_THEME = 2;
    static final int ACT_PALETTE = 3;
    static final int ACT_SEARCH = 4;
    static final int ACT_SEARCH_CLEAR = 5;
    static final int ACT_MODULE_TOGGLE = 6;
    static final int ACT_MODULE_OPEN = 7;
    static final int ACT_MODULE_KEY = 8;
    static final int ACT_MODULE_KEY_RESET = 9;
    static final int ACT_DOCK = 10;
    static final int ACT_POPUP_TOGGLE = 11;
    static final int ACT_POPUP_KEY = 12;
    static final int ACT_POPUP_KEY_RESET = 13;
    static final int ACT_SCROLL = 14;
    static final int ACT_SLIDER = 15;
    static final int ACT_MODE = 16;
    static final int ACT_MODE_ITEM = 17;
    static final int ACT_BOOL = 18;
    static final int ACT_LIST_ITEM = 19;
    static final int ACT_BIND_SETTING = 20;
    static final int ACT_PREF_BOOL = 24;
    static final int ACT_PREF_SLIDER = 25;
    static final int ACT_POPOVER_ITEM = 23;
    static final int ACT_ACTION = 31;
    static final int ACT_STRING = 32;
    static final int ACT_STRING_RESET = 33;
    static final int ACT_COLOR = 34;
    static final int ACT_PICKER_SV = 35;
    static final int ACT_PICKER_HUE = 36;
    static final int ACT_PICKER_ALPHA = 37;
    static final int ACT_PICKER_HEX = 38;
    static final int ACT_PICKER_CLOSE = 39;

    // ---- идентификаторы наведения (целые числа — без строк в кадре)
    private static final int HOT_THEME = 1;
    private static final int HOT_SEARCH = 2;
    private static final int HOT_TAB = 100;
    private static final int HOT_CARD = 200;
    private static final int HOT_GEAR = 1000;
    private static final int HOT_SET = 2000;
    private static final int HOT_SEG = 4000;
    private static final int HOT_POPOVER = 6000;
    static final int HOT_BIND = 7000;
    private static final int HOT_PALETTE = 8000;
    private static final int HOT_DOCK = 9000;
    private static final int HOT_PREF = 9500;

    private static final int MAX_REGIONS = 512;
    private static final int MAX_VISIBLE = 128;

    private static final int SCROLL_POPUP = MenuAnim.POPUP_SCROLL;
    private static final int SCROLL_PREFS = 6;
    private static final int SCROLL_SEARCH = 5;

    // ---- настройки меню (страница «Настройки»)
    private static final int PREF_SOUNDS = 0;
    private static final int PREF_VOLUME = 1;
    private static final int PREF_HOVER_SOUNDS = 2;
    private static final int PREF_SLIDER_SOUNDS = 3;
    private static final int PREF_BLUR = 4;
    private static final int PREF_DIM = 5;
    private static final int PREF_ANIMATIONS = 6;
    private static final int PREF_ANIM_SPEED = 7;
    private static final int PREF_SCALE = 8;
    private static final int PREF_GRADIENT = 9;
    private static final int PREFS_COUNT = 10;

    private static final int PREFS_PALETTE = 0;
    private static final int PREFS_SOUND = 1;
    private static final int PREFS_VIEW = 2;

    private static final float[] PREF_MIN = {0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.5F, 70.0F, 0.0F};
    private static final float[] PREF_MAX = {0.0F, 100.0F, 0.0F, 0.0F, 0.0F, 90.0F, 0.0F, 2.0F, 130.0F, 0.0F};
    private static final float[] PREF_STEP = {0.0F, 5.0F, 0.0F, 0.0F, 0.0F, 5.0F, 0.0F, 0.1F, 5.0F, 0.0F};

    private record PrefGroup(String title, String subtitle, int[] rows) {
    }

    private static final PrefGroup[] PREFS = {
            new PrefGroup("Палитра", "Цветовые схемы меню", new int[0]),
            new PrefGroup("Звук", "Щелчки и отклик меню",
                    new int[]{PREF_SOUNDS, PREF_VOLUME, PREF_HOVER_SOUNDS, PREF_SLIDER_SOUNDS}),
            new PrefGroup("Вид", "Фон и затемнение мира", new int[]{PREF_BLUR, PREF_DIM}),
            new PrefGroup("Поведение", "Анимации и размер меню",
                    new int[]{PREF_ANIMATIONS, PREF_ANIM_SPEED, PREF_SCALE}),
    };

    // ---- состояние
    private final MenuAnim anim = new MenuAnim();
    private final ModuleManager modules;
    private final ConfigManager config;

    private final float[] regionX = new float[MAX_REGIONS];
    private final float[] regionY = new float[MAX_REGIONS];
    private final float[] regionW = new float[MAX_REGIONS];
    private final float[] regionH = new float[MAX_REGIONS];
    private final int[] regionAction = new int[MAX_REGIONS];
    private final int[] regionAlt = new int[MAX_REGIONS];
    private final int[] regionPayload = new int[MAX_REGIONS];
    private int regionCount;

    private final ClientModule[] visible = new ClientModule[MAX_VISIBLE];
    private int visibleCount;

    private final float[] scroll = new float[MenuAnim.SCROLL_SIZE];
    private final float[] scrollTotal = new float[MenuAnim.SCROLL_SIZE];
    private final float[] tabX = new float[8];
    private final float[] tabW = new float[8];
    private final float[] colYBuf = new float[4];
    private final float[] cardX = new float[MAX_VISIBLE];
    private final float[] cardY = new float[MAX_VISIBLE];
    private final float[] cardH = new float[MAX_VISIBLE];
    private final float[] sliderX = new float[MenuAnim.MAX_SETTINGS];
    private final float[] sliderW = new float[MenuAnim.MAX_SETTINGS];
    private final float[] modeDropX = new float[MenuAnim.MAX_SETTINGS];
    private final float[] modeDropY = new float[MenuAnim.MAX_SETTINGS];
    private final float[] modeDropW = new float[MenuAnim.MAX_SETTINGS];
    private final float[] modeDropH = new float[MenuAnim.MAX_SETTINGS];

    private static Page page = Page.MODULES;
    private static ModuleCategory category = ModuleCategory.COMBAT;

    private float u = 1.0F;
    private float mx;
    private float my;
    private long now = System.currentTimeMillis();
    private final long openedAt = System.currentTimeMillis();
    private long contentAt = System.currentTimeMillis();
    private boolean closing;
    private long closingAt;
    private String search = "";
    private boolean searchFocused;

    ClientModule popMod;
    ClientModule popShown;
    ClientModule binding;
    private Setting<?> bindingSetting;
    private int bindingSettingIndex = -1;

    ModeSetting pop;
    private long popAt;

    private ColorSetting colorSetting;
    private float pickerHue;
    private float pickerSaturation;
    private float pickerValue;
    private float pickerAlpha;
    private boolean pickerHexFocused;
    private String pickerHex = "";
    private float pickerX;
    private float pickerY;
    private float pickerW;
    private float pickerH;

    private Setting<String> editingString;
    private String stringBuffer = "";

    private int hot;
    private int lastHot;

    float px;
    float py;
    float pw;
    float ph;
    private float cx0;
    private float cw0;
    private float top;
    private float bottom;

    float ancX;
    float ancY;
    float ancW;
    float ancH;
    float pmX;
    float pmY;
    float pmW;
    float pmH;

    boolean compact;

    private boolean dragging;
    private int dragAction = ACT_NONE;
    private int dragPayload;
    private boolean configDirty;

    private final ModulePopup popup = new ModulePopup(this);

    public MenuScreen(ModuleManager modules, ConfigManager config) {
        super(Component.literal("Skycore Menu"));
        if (modules == null) {
            throw new IllegalArgumentException("MenuScreen requires a ModuleManager");
        }
        this.modules = modules;
        this.config = config;
        MenuPrefs.sync();
        MenuSound.play(MenuSound.OPEN);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    // ---- доступ для окна настроек модуля

    float S(double value) {
        return (float) (value * this.u);
    }

    /** Ширина экрана в GUI-пикселях (доступ для окна настроек). */
    float screenWidth() {
        return this.width;
    }

    /** Высота экрана в GUI-пикселях (доступ для окна настроек). */
    float screenHeight() {
        return this.height;
    }

    static boolean light() {
        return MenuPalette.p().light();
    }

    static int acc() {
        return MenuPalette.accent() | 0xFF000000;
    }

    static int acc2() {
        return MenuPalette.accent2() | 0xFF000000;
    }

    static int accText() {
        return light() ? MenuPalette.accent() : MenuPalette.accent2();
    }

    int lt(int lightColor, int darkColor) {
        return light() ? lightColor : darkColor;
    }

    float an(int key, float target, float speed) {
        return this.anim.to(key, target, speed);
    }

    float tw(int key, float target, int ms) {
        return this.anim.tween(key, target, ms);
    }

    static float ease(float t) {
        return MenuAnim.easeOutCubic(Math.max(0.0F, Math.min(1.0F, t)));
    }

    boolean hover(float x, float y, float w, float h) {
        return this.mx >= x && this.mx < x + w && this.my >= y && this.my < y + h;
    }

    boolean blocked() {
        return this.pop != null || this.colorSetting != null || this.editingString != null;
    }

    void hot(int key) {
        this.hot = key;
    }

    /** Акцентная заливка: при включённом «Градиенте» перетекает из акцента во второй цвет. */
    void accentFill(float x, float y, float w, float h, float radius) {
        if (MenuPrefs.gradient) {
            MenuDraw.roundGrad(x, y, w, h, radius, acc(), acc2());
        } else {
            MenuDraw.round(x, y, w, h, radius, acc());
        }
    }

    /** Полупрозрачная акцентная подложка с градиентом (плашки групп, чипы, поля значений). */
    void accentWash(float x, float y, float w, float h, float radius, int alpha) {
        if (MenuPrefs.gradient) {
            MenuDraw.roundGrad(x, y, w, h, radius, MenuDraw.a(acc(), alpha), MenuDraw.a(acc2(), alpha));
        } else {
            MenuDraw.round(x, y, w, h, radius, MenuDraw.a(acc(), alpha));
        }
    }

    /** Градиентная кромка вдоль верхней грани окна: ярко к центру, тускло к краям. */
    void gradHairline(float x, float y, float w, float radius) {
        if (!MenuPrefs.gradient || w <= radius * 2.0F) {
            return;
        }
        int height = Math.max(1, Math.round(S(1.0F)));
        float half = (w - radius * 2.0F) * 0.5F;
        int peak = light() ? 60 : 120;
        MenuDraw.hgrad(x + radius, y, half, height, MenuDraw.a(acc2(), 0), MenuDraw.a(acc2(), peak));
        MenuDraw.hgrad(x + radius + half, y, w - radius * 2.0F - half, height,
                MenuDraw.a(acc2(), peak), MenuDraw.a(acc2(), 0));
    }

    int plate() {
        return light() ? 0xFFFFFFFF : MenuDraw.mix(MenuPalette.bg() | 0xFF000000, 0xFFFFFFFF, 0.025F);
    }

    int inset(boolean hovered) {
        if (light()) {
            return MenuDraw.a(0x1B1530, hovered ? 14 : 8);
        }
        return MenuDraw.mix(MenuPalette.bg() | 0xFF000000, 0xFF000000, hovered ? 0.05F : 0.22F);
    }

    int hair() {
        return MenuDraw.a(MenuPalette.text(), light() ? 22 : 14);
    }

    int baseColour() {
        return light() ? 0xFFF8F8F7 : (MenuPalette.bg() | 0xFF000000);
    }

    // ---- регионы кликов

    void region(float x, float y, float w, float h, int action, int payload) {
        region(x, y, w, h, action, ACT_NONE, payload);
    }

    void region(float x, float y, float w, float h, int action, int alt, int payload) {
        if (this.regionCount >= MAX_REGIONS || w <= 0.0F || h <= 0.0F) {
            return;
        }
        this.regionX[this.regionCount] = x;
        this.regionY[this.regionCount] = y;
        this.regionW[this.regionCount] = w;
        this.regionH[this.regionCount] = h;
        this.regionAction[this.regionCount] = action;
        this.regionAlt[this.regionCount] = alt;
        this.regionPayload[this.regionCount] = payload;
        this.regionCount++;
    }

    /** Регион, обрезанный по видимой части списка: клики вне области прокрутки не ловятся. */
    void clipped(float x, float y, float w, float h, float scrollAmount, float listTop, float listBottom,
                 int action, int payload) {
        clipped(x, y, w, h, scrollAmount, listTop, listBottom, action, ACT_NONE, payload);
    }

    void clipped(float x, float y, float w, float h, float scrollAmount, float listTop, float listBottom,
                 int action, int alt, int payload) {
        float y0 = Math.max(listTop, y - scrollAmount);
        float y1 = Math.min(listBottom, y - scrollAmount + h);
        if (y1 > y0) {
            region(x, y0, w, y1 - y0, action, alt, payload);
        }
    }

    // ---- прокрутка

    float scrolled(int context, float total, float listTop, float listBottom) {
        float view = listBottom - listTop;
        float max = Math.max(0.0F, total - view);
        float want = Math.max(0.0F, Math.min(max, this.scroll[context]));
        this.scroll[context] = want;
        this.scrollTotal[context] = total;
        return an(MenuAnim.SCROLL_BASE + context, want, 15.0F);
    }

    void edges(float x, float w, float listTop, float listBottom, float scrollAmount, int colour) {
        float fadeHeight = S(24);
        if (scrollAmount > 0.5F) {
            MenuDraw.vgrad(x, listTop, w, fadeHeight,
                    MenuDraw.a(colour, (int) (Math.min(1.0F, scrollAmount / Math.max(1.0F, S(24))) * 150)),
                    MenuDraw.a(colour, 0));
        }
        MenuDraw.vgrad(x, listBottom - S(22), w, S(22), MenuDraw.a(colour, 0), MenuDraw.a(colour, 210));
    }

    void scrollbar(float x, float listTop, float listBottom, float total, float scrollAmount, int context) {
        float view = listBottom - listTop;
        if (total <= view || view <= 0.0F) {
            return;
        }
        float thumbHeight = Math.max(S(36), view * view / total);
        float travel = Math.max(1.0F, total - view);
        float thumbY = listTop + (view - thumbHeight) * Math.min(1.0F, scrollAmount / travel);
        boolean hovered = !blocked() && hover(x - S(6), listTop, S(16), view);
        float hoverAmount = an(MenuAnim.SCROLL_BASE + MenuAnim.SCROLL_BAR + context, hovered ? 1.0F : 0.0F, 18.0F);
        float thumbWidth = S(3) + S(3) * hoverAmount;
        MenuDraw.round(x + S(2) - thumbWidth * 0.5F, thumbY, thumbWidth, thumbHeight, thumbWidth * 0.5F,
                light()
                        ? MenuDraw.mix(MenuDraw.a(MenuPalette.text(), 46), MenuDraw.a(MenuPalette.text(), 90), hoverAmount)
                        : MenuDraw.mix(0x30FFFFFF, 0x60FFFFFF, hoverAmount));
        if (!blocked()) {
            region(x - S(6), listTop, S(16), view, ACT_SCROLL, context);
        }
    }

    // ---- жизненный цикл экрана

    @Override
    protected void init() {
        // палитра — часть оформления, хранится между запусками
        MenuPalette.restore(MenuPrefs.palette);
        this.top = 0.0F;
        this.bottom = 0.0F;
        this.regionCount = 0;
    }

    @Override
    public void tick() {
        this.now = System.currentTimeMillis();
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float partialTick) {
        this.now = System.currentTimeMillis();
        this.mx = mouseX;
        this.my = mouseY;
        this.anim.frame();

        float open = an(MenuAnim.OPEN, this.closing ? 0.0F : 1.0F, this.closing ? 16.0F : 10.0F);
        if (this.closing && open < 0.02F) {
            if (System.currentTimeMillis() - this.closingAt > 40L && this.minecraft != null
                    && this.minecraft.gui.screen() == this) {
                this.minecraft.setScreenAndShow(null);
            }
            return;
        }

        CurrentGuiGraphicsExtractorHelper.enter2D(extractor, this.minecraft.getDeltaTracker());
        BeginFrameHelper.beginFrame();
        try {
            this.regionCount = 0;
            this.hot = 0;
            this.draw(open);
        } finally {
            // flush обязан выполниться до выхода из 2D-контекста, иначе кадр теряется
            BeginFrameHelper.flush();
            CurrentGuiGraphicsExtractorHelper.exit2D();
        }

        if (this.hot != 0 && this.hot != this.lastHot && this.now - this.openedAt > 250L && !this.closing) {
            MenuSound.play(MenuSound.HOVER, 1.0F + (this.hot % 7) * 0.015F, 1.0F);
        }
        this.lastHot = this.hot;
    }

    // ---- отрисовка

    private void draw(float open) {
        float e = ease(open);
        float screenWidth = this.width;
        float screenHeight = this.height;

        this.u = Math.max(0.45F, Math.min(2.2F,
                Math.min(screenHeight / 1080.0F, screenWidth / 1060.0F)
                        * MenuPrefs.menuScale() * 0.86F));

        this.pw = Math.min(S(1010), screenWidth - S(80));
        this.ph = Math.min(S(640), screenHeight - S(210));
        this.px = (screenWidth - this.pw) * 0.5F;
        this.py = (screenHeight - this.ph - S(28) + S(78)) * 0.5F;

        float keep = MenuDraw.fade;

        // фон: затемнение и размытие того, что за меню
        int dimAlpha = (int) (255.0F * MenuPrefs.dim / 100.0F * open);
        if (MenuPrefs.blur) {
            // размытие работает как подложка: полупрозрачная заливка + blur по кадру
            BeginFrameHelper.rect(0.0F, 0.0F, screenWidth, screenHeight)
                    // минимальная заливка нужна, иначе полупрозрачный прямоугольник
                    // не рисуется и размытие пропадает даже при dim = 0
                    .color(MenuDraw.a(0x05040A, Math.max(dimAlpha, (int) (24.0F * open))))
                    .blur(S(18), Math.min(1.0F, open))
                    .hudGlow(false)
                    .draw();
        } else if (dimAlpha > 0) {
            MenuDraw.rect(0.0F, 0.0F, screenWidth, screenHeight, MenuDraw.a(0x05040A, dimAlpha));
        }

        MenuDraw.fade = keep * Math.min(1.0F, open * 1.3F);
        this.brand(screenWidth * 0.5F, this.py - S(58));

        float slide = (1.0F - e) * S(16);
        this.panel(slide);
        this.dock(screenWidth * 0.5F, this.py + this.ph, slide);
        this.popup.modulePopup();

        if (this.pop != null) {
            this.popover();
        }
        if (this.colorSetting != null) {
            this.colorPicker();
        }
        MenuDraw.fade = keep;

    }

    private void brand(float centerX, float y) {
        float t = (this.now - this.openedAt) / 1000.0F;
        float alpha = ease(t / 0.35F);
        float logoSize = S(28);
        float fontSize = S(21);
        float secondFontSize = S(21);
        float gap = S(10);

        String first = "Skycore";
        String second = " Menu";
        float firstW = MenuDraw.textWidth(first, Weight.BOLD, fontSize);
        float secondW = MenuDraw.textWidth(second, Weight.MEDIUM, secondFontSize);
        float total = logoSize + gap + firstW + secondW;
        float x = centerX - total * 0.5F;

        float keep = MenuDraw.fade;
        MenuDraw.fade = keep * alpha;

        float dy = (1.0F - alpha) * S(6);
        float plateX = x - S(18);
        float plateY = y + dy - S(10);
        float plateW = total + S(38);
        float plateH = logoSize + S(20);
        float radius = plateH * 0.5F;

        MenuDraw.shadow(plateX, plateY + S(4), plateW, plateH, radius, S(14),
                MenuDraw.a(0, light() ? 30 : 90));
        MenuDraw.round(plateX, plateY, plateW, plateH, radius,
                light() ? MenuDraw.a(0xFFFFFF, 242) : MenuDraw.a(MenuPalette.bg(), 240));
        MenuDraw.glow(x - S(8), plateY + S(2), logoSize + S(16), plateH - S(4), radius,
                MenuDraw.a(MenuPalette.accent(), light() ? 50 : 90));
        MenuDraw.outline(plateX, plateY, plateW, plateH, radius, MenuDraw.a(MenuPalette.text(), light() ? 24 : 26));
        gradHairline(plateX + radius, plateY, plateW - radius, radius);
        MenuDraw.iconCsCentered(IconGlyphs.STAR, x - S(4), plateY, logoSize + S(8), plateH, logoSize,
                MenuDraw.a(MenuPalette.accent(), light() ? 220 : 255));

        float textY = plateY + (plateH - MenuText.lineHeight(Weight.BOLD, fontSize)) * 0.5F;
        MenuDraw.text(first, x + logoSize + gap, textY, Weight.BOLD, fontSize,
                MenuPalette.text());
        MenuDraw.text(second, x + logoSize + gap + firstW, textY, Weight.MEDIUM, secondFontSize,
                MenuPalette.muted());

        MenuDraw.fade = keep;
    }

    private void panel(float slide) {
        float radius = S(12);
        boolean light = light();

        MenuDraw.shadow(this.px, this.py + S(14) + slide, this.pw, this.ph, radius, S(40),
                MenuDraw.a(0, light ? 40 : 160));

        float x = this.px;
        float y = this.py + slide;
        if (MenuPrefs.blur) {
            BeginFrameHelper.rect(x, y, this.pw, this.ph)
                    .radius(radius)
                    // альфа ниже 0xF0, иначе размытие под панелью не видно
                    .color(light ? 0xE6F8F7FA : MenuDraw.a(MenuPalette.bg(), 214))
                    .blur(S(30), MenuDraw.fade)
                    .hudGlow(false)
                    .draw();
        } else {
            MenuDraw.round(x, y, this.pw, this.ph, radius,
                    light ? 0xF8F8F7FA : MenuDraw.a(MenuPalette.bg(), 242));
        }
        MenuDraw.outline(x, y, this.pw, this.ph, radius, MenuDraw.a(MenuPalette.text(), light ? 28 : 20));
        gradHairline(x, y, this.pw, radius);

        float pad = S(36);
        float headerY = y + S(26);
        float headerH = S(40);
        this.cx0 = x + pad;
        this.cw0 = this.pw - pad * 2.0F;
        this.header(this.cx0, headerY, this.cw0, headerH);

        float lineY = headerY + headerH + S(18);
        MenuDraw.rect(this.cx0, lineY, this.cw0, Math.max(1.0F, S(1)), this.hair());
        this.top = lineY + S(18);
        this.bottom = y + this.ph - S(44);

        if (page == Page.MODULES) {
            this.rows();
        } else {
            this.settingsPage();
        }
    }

    private void header(float x, float y, float w, float h) {
        String[] labels = page == Page.MODULES ? TAB_TITLES : new String[]{"Настройки"};
        int active = -1;
        if (page == Page.MODULES && this.search.isEmpty()) {
            for (int i = 0; i < TABS.length; i++) {
                if (TABS[i] == category) {
                    active = i;
                }
            }
        } else if (page == Page.SETTINGS) {
            active = 0;
        }

        float fontSize = S(13);
        float iconSize = S(13);
        float iconPad = S(9);
        float iconGap = S(7);
        float gap = S(3);
        float startX = x - iconPad;
        float plateH = S(30);
        float plateY = y + (h - plateH) * 0.5F;

        float tx = startX;
        for (int i = 0; i < labels.length; i++) {
            this.tabW[i] = iconPad + iconSize + iconGap + MenuDraw.textWidth(labels[i], Weight.SEMIBOLD, fontSize)
                    + iconPad;
            this.tabX[i] = tx;
            tx += this.tabW[i] + gap;
        }
        float stripW = tx - gap - startX;

        if (active >= 0) {
            float plateX = tw(MenuAnim.PLATE_X, this.tabX[active], 260);
            float plateWidth = tw(MenuAnim.PLATE_W, this.tabW[active], 260);
            if (MenuPrefs.gradient) {
                accentWash(plateX, plateY, plateWidth, plateH, S(10), light() ? 16 : 42);
            } else {
                MenuDraw.round(plateX, plateY, plateWidth, plateH, S(10),
                        light() ? MenuDraw.a(0x1B1530, 14) : 0xA01A1C24);
            }
            MenuDraw.outline(plateX, plateY, plateWidth, plateH, S(10),
                    light() ? MenuDraw.a(0x1B1530, 20) : 0x14FFFFFF);
        }

        for (int i = 0; i < labels.length; i++) {
            boolean on = i == active;
            boolean hovered = !blocked() && hover(this.tabX[i], y, this.tabW[i], h);
            if (hovered) {
                hot(HOT_TAB + i);
            }
            float hoverAmount = an(MenuAnim.TAB_HOVER + i, hovered ? 1.0F : 0.0F, 18.0F);
            float activeAmount = tw(MenuAnim.TAB_ACTIVE + i, on ? 1.0F : 0.0F, 220);
            int colour = MenuDraw.mix(
                    MenuDraw.mix(lt(MenuPalette.muted(), 0xFFB0B8C4), MenuPalette.text(), hoverAmount * 0.6F),
                    MenuPalette.text(), activeAmount);
            if (page == Page.MODULES) {
                MenuDraw.iconCs(IconGlyphs.category(TABS[i]), this.tabX[i] + iconPad + iconSize * 0.5F,
                        plateY + plateH * 0.5F, iconSize, MenuDraw.mix(colour, accText(), activeAmount * 0.9F));
            } else {
                MenuDraw.iconCs(IconGlyphs.CONFIGS, this.tabX[i] + iconPad + iconSize * 0.5F,
                        plateY + plateH * 0.5F, iconSize, MenuDraw.mix(colour, accText(), activeAmount * 0.9F));
            }
            MenuDraw.text(labels[i], this.tabX[i] + iconPad + iconSize + iconGap,
                    plateY + (plateH - MenuText.lineHeight(Weight.SEMIBOLD, fontSize)) * 0.5F,
                    Weight.SEMIBOLD, fontSize, colour);
            if (!blocked() && labels.length > 1) {
                region(this.tabX[i], y, this.tabW[i], h, ACT_TAB, i);
            }
        }

        float buttonSize = h;
        float buttonX = x + w - buttonSize;
        this.themeButton(buttonX, y, buttonSize);

        if (page == Page.MODULES) {
            float searchWidth = Math.max(S(150),
                    Math.min(S(230), buttonX - S(10) - (startX + stripW + S(16))));
            this.searchPill(buttonX - S(10) - searchWidth, y, searchWidth, h);
        }
    }

    private void searchPill(float x, float y, float w, float h) {
        boolean over = !blocked() && hover(x, y, w, h);
        if (over) {
            hot(HOT_SEARCH);
        }
        float focus = tw(MenuAnim.SFOCUS, this.searchFocused ? 1.0F : 0.0F, 180);
        float hoverAmount = an(MenuAnim.SHOVER, over ? 1.0F : 0.0F, 18.0F);
        MenuDraw.round(x, y, w, h, S(7), light()
                ? MenuDraw.a(0x1B1530, (int) (8 + 5 * hoverAmount))
                : inset(hoverAmount > 0.5F));
        MenuDraw.outline(x, y, w, h, S(7),
                MenuDraw.mix(MenuDraw.a(MenuPalette.text(), light()
                        ? (int) (16 + 10 * hoverAmount)
                        : (int) (12 + 8 * hoverAmount)), acc(), focus));

        float iconSize = S(13);
        MenuDraw.iconCs(IconGlyphs.SEARCH, x + S(11) + iconSize * 0.5F, y + h * 0.5F, iconSize,
                MenuDraw.mix(MenuPalette.dim(), accText(), focus));

        boolean empty = this.search.isEmpty();
        String text = empty ? "Поиск" : this.search;
        float fontSize = S(12);
        MenuDraw.text(MenuDraw.ellipsize(text, Weight.MEDIUM, fontSize, w - S(66)), x + S(30),
                y + (h - MenuText.lineHeight(Weight.MEDIUM, fontSize)) * 0.5F,
                Weight.MEDIUM, fontSize, empty ? MenuPalette.dim() : MenuPalette.text());

        if (this.searchFocused && (this.now - this.openedAt) / 530L % 2L == 0L) {
            float caretX = x + S(30) + (empty ? 0.0F : MenuDraw.textWidth(this.search, Weight.MEDIUM, fontSize))
                    + S(1);
            MenuDraw.rect(caretX, y + S(9), Math.max(1.0F, S(1.2F)), h - S(18), accText());
        }

        if (!empty) {
            boolean clearHover = !blocked() && hover(x + w - S(26), y, S(26), h);
            float clearSize = S(12);
            MenuDraw.iconLetters(LegacyIconGlyphs.TRASH, x + w - S(20) + clearSize * 0.5F, y + h * 0.5F, clearSize,
                    clearHover ? MenuPalette.text() : MenuPalette.muted());
            if (!blocked()) {
                region(x + w - S(26), y, S(26), h, ACT_SEARCH_CLEAR, 0);
            }
        }

        if (!blocked()) {
            region(x, y, w - (empty ? 0.0F : S(28)), h, ACT_SEARCH, 0);
        }
    }

    private void themeButton(float x, float y, float size) {
        boolean hovered = !blocked() && hover(x, y, size, size);
        if (hovered) {
            hot(HOT_THEME);
        }
        float hoverAmount = an(MenuAnim.THEME_HOVER, hovered ? 1.0F : 0.0F, 18.0F);
        float lightKey = tw(MenuAnim.THEME_KEY, MenuPalette.palette == MenuPalette.LIGHT ? 1.0F : 0.0F, 420);

        float centreX = x + size * 0.5F;
        float centreY = y + size * 0.5F;
        MenuDraw.circle(centreX, centreY, size * 0.5F, light()
                ? MenuDraw.a(0x1B1530, (int) (9 + 7 * hoverAmount))
                : MenuDraw.a(0xFFFFFF, (int) (6 + 8 * hoverAmount)));
        MenuDraw.ring(centreX, centreY, size * 0.5F, 1.0F, MenuDraw.a(MenuPalette.text(),
                light() ? (int) (16 + 10 * hoverAmount) : (int) (12 + 10 * hoverAmount)));

        float iconSize = S(13);
        int colour = MenuDraw.mix(MenuPalette.muted(), MenuPalette.text(), hoverAmount);
        MenuDraw.iconLettersCentered(LegacyIconGlyphs.THEMES, x, y, size, size, iconSize, colour);

        // подсказка о текущем режиме: точка-индикатор светлой палитры
        if (lightKey > 0.02F) {
            float dot = S(4);
            MenuDraw.circle(x + size - dot * 1.6F, y + dot * 1.6F, dot * 0.5F,
                    MenuDraw.a(acc(), (int) (220 * lightKey)));
        }

        if (!blocked()) {
            region(x, y, size, size, ACT_THEME, 0);
        }
    }

    // ---- список модулей

    private void collectVisible() {
        this.visibleCount = 0;
        String query = this.search;
        List<ClientModule> pool = this.modules.getModules();
        for (int i = 0, size = pool.size(); i < size && this.visibleCount < MAX_VISIBLE; i++) {
            ClientModule module = pool.get(i);
            if (query.isEmpty()) {
                if (module.getCategory() != category) {
                    continue;
                }
            } else if (!containsIgnoreCase(module.getName(), query)
                    && !containsIgnoreCase(module.getDescription(), query)
                    && !containsIgnoreCase(categoryTitle(module.getCategory()), query)) {
                continue;
            }
            this.visible[this.visibleCount++] = module;
        }
    }

    /** Returns the localized ClickGUI tab title for a module category. */
    private static String categoryTitle(ModuleCategory moduleCategory) {
        for (int i = 0; i < TABS.length; i++) {
            if (TABS[i] == moduleCategory) {
                return TAB_TITLES[i];
            }
        }
        return moduleCategory.displayName();
    }

    private static boolean containsIgnoreCase(String text, String query) {
        int length = text.length();
        int queryLength = query.length();
        if (queryLength == 0) {
            return true;
        }
        if (queryLength > length) {
            return false;
        }
        for (int i = 0; i <= length - queryLength; i++) {
            if (text.regionMatches(true, i, query, 0, queryLength)) {
                return true;
            }
        }
        return false;
    }

    private static int indexOfIgnoreCase(String text, String query) {
        int length = text.length();
        int queryLength = query.length();
        if (queryLength == 0) {
            return 0;
        }
        if (queryLength > length) {
            return -1;
        }
        for (int i = 0; i <= length - queryLength; i++) {
            if (text.regionMatches(true, i, query, 0, queryLength)) {
                return i;
            }
        }
        return -1;
    }

    private void rows() {
        collectVisible();
        if (this.visibleCount == 0) {
            float appear = ease((this.now - this.contentAt) / 300.0F);
            float keep = MenuDraw.fade;
            MenuDraw.fade = keep * appear;
            float centreX = this.cx0 + this.cw0 * 0.5F;
            float centreY = this.top + (this.bottom - this.top) * 0.5F - S(30);
            MenuDraw.iconCsCentered(IconGlyphs.SEARCH, centreX - S(14), centreY, S(28), S(28), S(24),
                    MenuPalette.dim());
            MenuDraw.textCenter("Ничего не найдено", centreX,
                    centreY + S(36), Weight.SEMIBOLD, S(14), MenuPalette.text());
            MenuDraw.textCenter("Попробуй другое слово", centreX, centreY + S(56),
                    Weight.MEDIUM, S(11), MenuPalette.dim());
            MenuDraw.fade = keep;
            return;
        }

        float gap = S(10);
        int columns = 2;
        float columnWidth = (this.cw0 - gap * (columns - 1)) / columns;
        float headHeight = headHeight();

        float[] columnY = this.colYBuf;
        for (int i = 0; i < columnY.length; i++) {
            columnY[i] = 0.0F;
        }

        for (int i = 0; i < this.visibleCount; i++) {
            int column = 0;
            for (int j = 1; j < columns; j++) {
                if (columnY[j] < columnY[column]) {
                    column = j;
                }
            }
            this.cardX[i] = this.cx0 + column * (columnWidth + gap);
            this.cardY[i] = this.top + S(2) + columnY[column];
            this.cardH[i] = headHeight;
            columnY[column] += this.cardH[i] + gap;
        }

        float total = 0.0F;
        for (float value : columnY) {
            total = Math.max(total, value);
        }
        total += S(4);

        int context = this.search.isEmpty() ? category.ordinal() : SCROLL_SEARCH;
        float scrollAmount = scrolled(context, total, this.top, this.bottom);

        MenuDraw.scissor(this.cx0 - S(10), this.top, this.cw0 + S(20), this.bottom - this.top);
        try {
            for (int i = 0; i < this.visibleCount; i++) {
                this.card(this.visible[i], this.cardX[i], this.cardY[i], columnWidth,
                        this.cardH[i], scrollAmount, i);
            }
        } finally {
            MenuDraw.scissorEnd();
        }

        edges(this.cx0 - S(10), this.cw0 + S(20), this.top, this.bottom, scrollAmount, baseColour());
        scrollbar(this.cx0 + this.cw0 + S(10), this.top, this.bottom, total, scrollAmount, context);
    }

    private float headHeight() {
        return S(52);
    }

    private void card(ClientModule module, float x, float y, float w, float h, float scrollAmount, int index) {
        // y приходит без прокрутки (так же считаются регионы), рисуем со сдвигом.
        float unscrolledY = y;
        y -= scrollAmount;
        if (this.popMod == module) {
            this.ancX = x;
            this.ancY = y;
            this.ancW = w;
            this.ancH = h;
        }
        if (y + h < this.top - S(20) || y > this.bottom + S(20)) {
            return;
        }

        float appear = ease((this.now - this.contentAt - Math.min(index, 12) * 22L) / 300.0F);
        if (appear <= 0.01F) {
            return;
        }

        float pad = S(14);
        float radius = S(10);
        int mi = moduleIndex(module);

        boolean hovered = !blocked() && this.my >= this.top && this.my < this.bottom
                && hover(x, y, w, h);
        if (hovered) {
            hot(HOT_CARD + mi);
        }

        float keep = MenuDraw.fade;
        MenuDraw.fade = keep * appear;
        float offsetY = (1.0F - appear) * S(7);

        float hoverAmount = an(MenuAnim.MODULE_BASE + mi * MenuAnim.MOD_STRIDE + MenuAnim.MOD_HOVER,
                hovered ? 1.0F : 0.0F, 18.0F);
        float on = tw(MenuAnim.MODULE_BASE + mi * MenuAnim.MOD_STRIDE + MenuAnim.MOD_ON,
                module.isEnabled() ? 1.0F : 0.0F, 180);
        float selected = tw(MenuAnim.MODULE_BASE + mi * MenuAnim.MOD_STRIDE + MenuAnim.MOD_SELECTED,
                this.popMod == module ? 1.0F : 0.0F, 200);

        MenuDraw.round(x, y + offsetY, w, h, radius,
                MenuDraw.mix(plate(), light() ? 0xFFFFFFFF
                        : MenuDraw.mix(MenuPalette.bg() | 0xFF000000, 0xFFFFFFFF, 0.055F), hoverAmount));
        MenuDraw.outline(x, y + offsetY, w, h, radius,
                MenuDraw.mix(MenuDraw.a(MenuPalette.text(), light() ? 14 : 16),
                        MenuDraw.a(MenuPalette.accent(), 130), selected));

        // справа: тумблер, клавиша, кнопка раскрытия
        float toggleSize = Math.round(S(30));
        float toggleX = x + w - pad - toggleSize;
        boolean waiting = this.binding == module;
        String keyLabel = waiting ? "Клавиша" : module.getKeyBind() >= 0 ? KeyBinds.name(module.getKeyBind()) : "";
        float keyFont = S(10);
        float keyHeight = S(22);
        float keyMin = S(30);
        float keyTarget = keyLabel.isEmpty() ? keyMin
                : Math.max(keyMin, MenuDraw.textWidth(keyLabel, Weight.SEMIBOLD, keyFont) + S(14));
        float keyWidth = tw(MenuAnim.MODULE_BASE + mi * MenuAnim.MOD_STRIDE + MenuAnim.MOD_KEY_WIDTH,
                keyTarget, 220);
        float keyX = toggleX - S(8) - keyWidth;
        float keyY = y + offsetY + (h - keyHeight) * 0.5F;

        boolean hasSettings = !module.getSettings().isEmpty();
        float gearSize = Math.round(keyHeight * 1.15F);
        float gearX = keyX - S(5) - gearSize;

        float textWidth = (hasSettings ? gearX : keyX) - S(8) - (x + pad);
        float nameFont = S(13);
        String name = MenuDraw.ellipsize(module.getName(), Weight.SEMIBOLD, nameFont, textWidth);
        int nameColour = MenuDraw.mix(MenuDraw.mix(MenuPalette.muted(), MenuPalette.text(), 0.55F),
                MenuPalette.text(), Math.max(on, hoverAmount));
        float nameY = y + offsetY + S(10);
        if (this.search.isEmpty()) {
            MenuDraw.text(name, x + pad, nameY, Weight.SEMIBOLD, nameFont, nameColour);
        } else {
            this.nameLit(name, x + pad, nameY, nameFont, nameColour);
        }

        float descFont = S(10);
        String description = MenuDraw.ellipsize(this.search.isEmpty() ? module.getDescription() : category.displayName(),
                Weight.MEDIUM, descFont, textWidth);
        MenuDraw.text(description, x + pad, nameY + S(17), Weight.MEDIUM, descFont, MenuPalette.dim());

        boolean keyHovered = hovered && hover(keyX, keyY, keyWidth, keyHeight);
        this.keyChip(keyLabel, keyX, keyY, keyWidth, keyHeight, keyFont, waiting, keyHovered, mi);
        this.toggle(toggleX, y + offsetY, toggleSize, h, on, hoverAmount);

        if (hasSettings) {
            boolean gearHovered = hovered && hover(gearX, keyY, gearSize, keyHeight);
            if (gearHovered) {
                hot(HOT_GEAR + mi);
            }
            float gearAmount = an(MenuAnim.MODULE_BASE + mi * MenuAnim.MOD_STRIDE + MenuAnim.MOD_GEAR,
                    gearHovered ? 1.0F : 0.0F, 18.0F);
            float activeAmount = Math.max(gearAmount, selected);
            if (activeAmount > 0.01F) {
                MenuDraw.round(gearX, keyY, gearSize, keyHeight, keyHeight * 0.5F,
                        MenuDraw.a(MenuPalette.text(), (int) (12 * gearAmount + 6 * selected)));
            }
            MenuDraw.outline(gearX, keyY, gearSize, keyHeight, keyHeight * 0.5F,
                    MenuDraw.mix(light() ? MenuDraw.a(MenuPalette.text(), 26) : 0x18FFFFFF,
                            MenuDraw.a(MenuPalette.accent(), 190), selected));
            MenuDraw.iconLettersCentered(LegacyIconGlyphs.CHEVRON, gearX, keyY, gearSize, keyHeight, S(10),
                    MenuDraw.mix(lt(MenuPalette.muted(), 0xFF757D8A), accText(), activeAmount));
        }

        MenuDraw.fade = keep;

        if (blocked() || appear < 0.3F) {
            return;
        }

        clipped(x, unscrolledY, (hasSettings ? gearX : keyX) - S(4) - x, h, scrollAmount, this.top, this.bottom,
                ACT_MODULE_TOGGLE, hasSettings ? ACT_MODULE_OPEN : ACT_NONE, mi);
        if (hasSettings) {
            clipped(gearX - S(3), keyY - S(3), gearSize + S(6), keyHeight + S(6), scrollAmount, this.top,
                    this.bottom, ACT_MODULE_OPEN, ACT_MODULE_OPEN, mi);
        }
        clipped(toggleX - S(4), unscrolledY, x + w - toggleX + S(4), h, scrollAmount, this.top, this.bottom,
                ACT_MODULE_TOGGLE, ACT_NONE, mi);
        clipped(keyX - S(3), keyY - S(3), keyWidth + S(6), keyHeight + S(6), scrollAmount, this.top, this.bottom,
                ACT_MODULE_KEY, ACT_MODULE_KEY_RESET, mi);
    }

    private void keyChip(String text, float x, float y, float w, float h, float fontSize, boolean waiting,
                         boolean hovered, int moduleIndex) {
        float waitAmount = tw(MenuAnim.MODULE_BASE + moduleIndex * MenuAnim.MOD_STRIDE + MenuAnim.MOD_KEY_WAIT,
                waiting ? 1.0F : 0.0F, 200);
        float pulse = (float) (Math.sin(this.now / 220.0) * 0.5 + 0.5) * waitAmount;

        int background = MenuDraw.mix(
                MenuDraw.a(MenuPalette.text(), hovered ? 12 : 0),
                MenuDraw.a(MenuPalette.accent(), (int) ((light() ? 26 : 38) + 22 * pulse)),
                waitAmount);
        if ((background >>> 24) != 0) {
            MenuDraw.round(x, y, w, h, h * 0.5F, background);
        }
        MenuDraw.outline(x, y, w, h, h * 0.5F,
                MenuDraw.mix(light() ? MenuDraw.a(MenuPalette.text(), 24) : 0x18FFFFFF,
                        MenuDraw.a(MenuPalette.accent(), (int) (160 + 90 * pulse)), waitAmount));

        MenuDraw.scissor(x, y, w, h);
        try {
            if (text.isEmpty()) {
                MenuDraw.iconCsCentered(IconGlyphs.BIND, x, y, w, h, S(12),
                        hovered ? MenuPalette.text() : MenuDraw.a(MenuPalette.muted(), 170));
            } else if (waiting) {
                float keep = MenuDraw.fade;
                MenuDraw.fade = keep * waitAmount;
                MenuDraw.textCenterMid(text, x + w * 0.5F, y, h, Weight.SEMIBOLD, fontSize, accText());
                MenuDraw.fade = keep;
            } else {
                MenuDraw.textCenterMid(text, x + w * 0.5F, y, h, Weight.SEMIBOLD, fontSize,
                        hovered ? MenuPalette.text() : MenuDraw.mix(MenuPalette.muted(), MenuPalette.text(), 0.45F));
            }
        } finally {
            MenuDraw.scissorEnd();
        }
    }

    private void nameLit(String name, float x, float y, float px, int colour) {
        int index = indexOfIgnoreCase(name, this.search);
        if (index < 0) {
            MenuDraw.text(name, x, y, Weight.SEMIBOLD, px, colour);
            return;
        }
        String head = name.substring(0, index);
        String match = name.substring(index, Math.min(name.length(), index + this.search.length()));
        String tail = name.substring(Math.min(name.length(), index + this.search.length()));
        MenuDraw.text(head, x, y, Weight.MEDIUM, px, colour);
        x += MenuDraw.textWidth(head, Weight.MEDIUM, px);
        MenuDraw.text(match, x, y, Weight.SEMIBOLD, px, accText());
        x += MenuDraw.textWidth(match, Weight.SEMIBOLD, px);
        MenuDraw.text(tail, x, y, Weight.MEDIUM, px, colour);
    }

    /** Тумблер: дорожка и ручка. */
    void toggle(float x, float y, float w, float h, float on, float hoverAmount) {
        boolean light = light();
        float trackHeight = Math.round(S(18));
        float trackWidth = Math.round(trackHeight * 1.55F);
        float trackX = x + w - trackWidth - S(2);
        float trackY = y + (h - trackHeight) * 0.5F;
        float radius = trackHeight * 0.5F;

        int off = light
                ? MenuDraw.mix(0xFFE4E2EB, 0xFFD9D6E2, hoverAmount)
                : MenuDraw.a(0xFFFFFF, 24 + Math.round(14 * hoverAmount));
        if (MenuPrefs.gradient) {
            MenuDraw.pill(trackX, trackY, trackWidth, trackHeight,
                    MenuDraw.mix(off, acc(), on), MenuDraw.mix(off, acc2(), on));
        } else {
            MenuDraw.round(trackX, trackY, trackWidth, trackHeight, radius, MenuDraw.mix(off, acc(), on));
        }

        float margin = Math.max(2.0F, Math.round(trackHeight * 0.15F));
        float knobSize = trackHeight - margin * 2.0F;
        float knobX = trackX + margin + (trackWidth - margin * 2.0F - knobSize) * on;
        int knobOn = MenuDraw.onAccent(acc());
        MenuDraw.round(knobX, trackY + margin, knobSize, knobSize, knobSize * 0.5F,
                MenuDraw.mix(light ? 0xFFFFFFFF : 0xFF757D8A, knobOn, on));
    }

    /** Поле значения («таблетка»), как в источнике. */
    float valueBox(String value, float right, float y, boolean accent, boolean hovered) {
        float fontSize = S(11);
        float width = Math.max(S(this.compact ? 40 : 46), MenuDraw.textWidth(value, Weight.MEDIUM, fontSize)
                + S(this.compact ? 14 : 16));
        float height = S(this.compact ? 24 : 26);
        float x = right - width;
        if (accent) {
            accentWash(x, y, width, height, S(5), light() ? 26 : 40);
        } else {
            MenuDraw.round(x, y, width, height, S(5), inset(hovered));
        }
        MenuDraw.outline(x, y, width, height, S(5),
                accent ? acc() : MenuDraw.a(MenuPalette.text(), light() ? 30 : 16));
        MenuDraw.textCenterMid(value, x + width * 0.5F, y, height, Weight.MEDIUM, fontSize,
                accent ? accText() : MenuPalette.text());
        return width;
    }

    // ---- страница настроек меню

    private void settingsPage() {
        float gap = S(10);
        float columnWidth = (this.cw0 - gap) * 0.5F;
        float[] columnY = new float[2];

        float[] groupX = new float[PREFS.length];
        float[] groupY = new float[PREFS.length];
        float[] groupH = new float[PREFS.length];

        for (int i = 0; i < PREFS.length; i++) {
            int column = columnY[0] <= columnY[1] ? 0 : 1;
            groupX[i] = this.cx0 + column * (columnWidth + gap);
            groupY[i] = this.top + S(2) + columnY[column];
            groupH[i] = groupHeight(i, columnWidth);
            columnY[column] += groupH[i] + gap;
        }

        float total = Math.max(columnY[0], columnY[1]) + S(4);
        float scrollAmount = scrolled(SCROLL_PREFS, total, this.top, this.bottom);

        MenuDraw.scissor(this.cx0 - S(10), this.top, this.cw0 + S(20), this.bottom - this.top);
        try {
            for (int i = 0; i < PREFS.length; i++) {
                float appear = ease((this.now - this.contentAt - i * 40L) / 300.0F);
                if (appear <= 0.01F) {
                    continue;
                }
                float keep = MenuDraw.fade;
                MenuDraw.fade = keep * appear;
                this.prefGroup(i, groupX[i], groupY[i] - scrollAmount, columnWidth, groupH[i],
                        scrollAmount, this.top, this.bottom, appear > 0.3F);
                MenuDraw.fade = keep;
            }
        } finally {
            MenuDraw.scissorEnd();
        }

        edges(this.cx0 - S(10), this.cw0 + S(20), this.top, this.bottom, scrollAmount, baseColour());
        scrollbar(this.cx0 + this.cw0 + S(10), this.top, this.bottom, total, scrollAmount, SCROLL_PREFS);
    }

    private float groupHeight(int group, float columnWidth) {
        float height = S(56);
        if (group == PREFS_PALETTE) {
            return height + paletteHeightPx(columnWidth - S(28)) + S(8) + prefRowHeight(PREF_GRADIENT) + S(10);
        }
        int[] rows = PREFS[group].rows();
        for (int row : rows) {
            height += prefRowHeight(row);
        }
        return height + S(8);
    }

    private float prefRowHeight(int pref) {
        return PREF_STEP[pref] > 0.0F ? S(60) : S(36);
    }

    private void prefGroup(int group, float x, float y, float w, float h, float scrollAmount,
                           float listTop, float listBottom, boolean live) {
        float pad = S(14);
        float radius = S(10);
        MenuDraw.round(x, y, w, h, radius, plate());
        MenuDraw.outline(x, y, w, h, radius, light() ? MenuDraw.a(MenuPalette.text(), 22) : 0x18FFFFFF);

        float iconBox = S(28);
        accentWash(x + pad, y + S(12), iconBox, iconBox, S(8), light() ? 22 : 34);
        MenuDraw.iconCsCentered(group == PREFS_PALETTE ? IconGlyphs.COSMETICA
                        : group == PREFS_SOUND ? IconGlyphs.BIND
                        : group == PREFS_VIEW ? IconGlyphs.RENDER
                        : IconGlyphs.CONFIGS,
                x + pad, y + S(12), iconBox, iconBox, S(13), accText());

        MenuDraw.text(PREFS[group].title(), x + pad + iconBox + S(10), y + S(12), Weight.SEMIBOLD, S(13),
                MenuPalette.text());
        MenuDraw.text(PREFS[group].subtitle(), x + pad + iconBox + S(10), y + S(30), Weight.MEDIUM, S(10),
                lt(MenuPalette.dim(), 0xFF757D8A));

        MenuDraw.rect(x + pad, y + S(54), Math.max(1.0F, w - pad * 2.0F), Math.max(1.0F, S(1)),
                light() ? MenuDraw.a(MenuPalette.text(), 18) : 0x12FFFFFF);

        float rowY = y + S(56);
        float rowWidth = w - pad * 2.0F;
        if (group == PREFS_PALETTE) {
            this.paletteStrip(x + pad, rowY, rowWidth, scrollAmount, listTop, listBottom, live);
            rowY += paletteHeightPx(rowWidth) + S(8);
            this.prefRow(PREF_GRADIENT, x + pad, rowY, rowWidth, prefRowHeight(PREF_GRADIENT), scrollAmount,
                    listTop, listBottom, live);
            return;
        }

        int[] rows = PREFS[group].rows();
        for (int row : rows) {
            float height = prefRowHeight(row);
            this.prefRow(row, x + pad, rowY, rowWidth, height, scrollAmount, listTop, listBottom, live);
            rowY += height;
        }
    }

    private int palettePerRow(float width) {
        return Math.max(1, (int) ((width + S(8)) / (S(38) + S(8))));
    }

    private float paletteHeightPx(float width) {
        int perRow = palettePerRow(width);
        int rows = (MenuPalette.PALETTES.length + perRow - 1) / perRow;
        return rows * (S(38) + S(8)) - S(8);
    }

    private void paletteStrip(float x, float y, float w, float scrollAmount, float listTop, float listBottom,
                              boolean live) {
        int count = MenuPalette.PALETTES.length;
        float cell = S(38);
        float gap = S(8);
        int perRow = palettePerRow(w);
        for (int i = 0; i < count; i++) {
            int column = i % perRow;
            int row = i / perRow;
            float cellX = x + column * (cell + gap);
            float cellY = y + row * (cell + gap);
            if (cellY + cell > listBottom + S(20)) {
                continue;
            }
            MenuPalette.Palette palette = MenuPalette.PALETTES[i];
            boolean selected = MenuPalette.palette == i;
            boolean hovered = live && hover(cellX, cellY, cell, cell);
            if (hovered) {
                hot(HOT_PALETTE + i);
            }
            MenuDraw.round(cellX, cellY, cell, cell, S(8), palette.bg() | 0xFF000000);
            if (MenuPrefs.gradient) {
                MenuDraw.roundGrad(cellX + S(5), cellY + cell - S(11), cell - S(10), S(6), S(3),
                        palette.a() | 0xFF000000, palette.b() | 0xFF000000);
            } else {
                MenuDraw.round(cellX + S(5), cellY + cell - S(11), cell - S(10), S(6), S(3),
                        palette.a() | 0xFF000000);
            }
            MenuDraw.outline(cellX, cellY, cell, cell, S(8),
                    selected ? MenuDraw.a(MenuPalette.accent(), 220) : palette.border());
            if (selected) {
                MenuDraw.outline(cellX - S(2), cellY - S(2), cell + S(4), cell + S(4), S(10),
                        MenuDraw.a(MenuPalette.accent(), 120));
            }
            if (live) {
                clipped(cellX, cellY, cell, cell, scrollAmount, listTop, listBottom, ACT_PALETTE, i);
            }
        }
    }

    private void prefRow(int pref, float x, float y, float w, float h, float scrollAmount,
                         float listTop, float listBottom, boolean live) {
        boolean hovered = live && this.my >= listTop && this.my < listBottom
                && hover(x, y - scrollAmount, w, h);
        if (hovered) {
            hot(HOT_PREF + pref);
        }
        float hoverAmount = an(MenuAnim.PREFS_BASE + pref * 4 + MenuAnim.SET_ROW,
                hovered ? 1.0F : 0.0F, 18.0F);
        float fontSize = S(12);
        String label = switch (pref) {
            case PREF_SOUNDS -> "Звуки меню";
            case PREF_VOLUME -> "Громкость";
            case PREF_HOVER_SOUNDS -> "Звук наведения";
            case PREF_SLIDER_SOUNDS -> "Щелчки ползунков";
            case PREF_BLUR -> "Размытие фона";
            case PREF_DIM -> "Затемнение мира";
            case PREF_ANIMATIONS -> "Анимации";
            case PREF_ANIM_SPEED -> "Скорость анимаций";
            case PREF_SCALE -> "Размер меню";
            case PREF_GRADIENT -> "Градиенты";
            default -> "Настройка";
        };

        if (PREF_STEP[pref] > 0.0F) {
            float labelY = y - scrollAmount + S(8);
            MenuDraw.text(label, x, labelY, Weight.MEDIUM, fontSize, MenuPalette.text());
            float value = prefValue(pref);
            String valueText = pref == PREF_VOLUME ? Math.round(value) + "%"
                    : pref == PREF_SCALE ? Math.round(value) + "%"
                    : String.format(Locale.ROOT, "%.1f", value) + "x";
            MenuDraw.textRight(valueText, x + w, labelY, Weight.SEMIBOLD, S(11),
                    MenuDraw.mix(accText(), lt(MenuPalette.text(), 0xFFFFFFFF), hoverAmount > 0.5F ? 1.0F : 0.0F));

            float trackY = y - scrollAmount + S(30);
            float trackHeight = Math.max(2.0F, S(3));
            float knobRadius = S(5);
            float ratio = (value - PREF_MIN[pref]) / Math.max(0.0001F, PREF_MAX[pref] - PREF_MIN[pref]);
            boolean draggingThis = this.dragAction == ACT_PREF_SLIDER && this.dragPayload == pref;
            float shown = draggingThis ? ratio : an(MenuAnim.PREFS_BASE + pref * 4 + MenuAnim.SET_NUM_POS, ratio, 22.0F);
            float knobX = x + knobRadius + (w - knobRadius * 2.0F) * shown;

            MenuDraw.round(x, trackY, w, trackHeight, trackHeight * 0.5F,
                    MenuDraw.a(MenuPalette.text(), light() ? 26 : 24));
            if (MenuPrefs.gradient) {
                MenuDraw.roundGrad(x, trackY, Math.max(trackHeight, knobX - x), trackHeight, trackHeight * 0.5F,
                        acc(), acc2());
            } else {
                MenuDraw.round(x, trackY, Math.max(trackHeight, knobX - x), trackHeight, trackHeight * 0.5F, acc());
            }
            accentFill(knobX - knobRadius, trackY + trackHeight * 0.5F - knobRadius, knobRadius * 2.0F,
                    knobRadius * 2.0F, knobRadius);

            if (live) {
                this.prefSliderX[pref] = x + knobRadius;
                this.prefSliderW[pref] = w - knobRadius * 2.0F;
                float regionTop = Math.max(listTop, trackY - S(12));
                float regionBottom = Math.min(listBottom, trackY + S(16));
                if (regionBottom > regionTop) {
                    region(x - S(6), regionTop, w + S(12), regionBottom - regionTop, ACT_PREF_SLIDER, pref);
                }
            }
        } else {
            MenuDraw.textMid(label, x, y - scrollAmount, h, Weight.MEDIUM, fontSize, MenuPalette.text());
            boolean value = prefBool(pref);
            float on = tw(MenuAnim.PREFS_BASE + pref * 4 + MenuAnim.SET_BOOL, value ? 1.0F : 0.0F, 200);
            this.toggle(x + w - S(40), y - scrollAmount, S(40), h, on, hoverAmount);
            if (live) {
                clipped(x, y, w, h, scrollAmount, listTop, listBottom, ACT_PREF_BOOL, pref);
            }
        }
    }

    private boolean prefBool(int pref) {
        return switch (pref) {
            case PREF_SOUNDS -> MenuPrefs.sounds;
            case PREF_HOVER_SOUNDS -> MenuPrefs.hoverSounds;
            case PREF_SLIDER_SOUNDS -> MenuPrefs.sliderSounds;
            case PREF_BLUR -> MenuPrefs.blur;
            case PREF_ANIMATIONS -> MenuPrefs.animations;
            case PREF_GRADIENT -> MenuPrefs.gradient;
            default -> false;
        };
    }

    private float prefValue(int pref) {
        return switch (pref) {
            case PREF_VOLUME -> MenuPrefs.volume;
            case PREF_DIM -> MenuPrefs.dim;
            case PREF_ANIM_SPEED -> MenuPrefs.animSpeed;
            case PREF_SCALE -> MenuPrefs.scale;
            default -> 0.0F;
        };
    }

    private void applyPref(int pref, float value) {
        float clamped = Math.max(PREF_MIN[pref], Math.min(PREF_MAX[pref], value));
        if (PREF_STEP[pref] > 0.0F) {
            clamped = PREF_MIN[pref] + Math.round((clamped - PREF_MIN[pref]) / PREF_STEP[pref]) * PREF_STEP[pref];
            clamped = Math.max(PREF_MIN[pref], Math.min(PREF_MAX[pref], clamped));
        }
        switch (pref) {
            case PREF_VOLUME -> MenuPrefs.volume = clamped;
            case PREF_DIM -> MenuPrefs.dim = clamped;
            case PREF_ANIM_SPEED -> {
                MenuPrefs.animSpeed = clamped;
                MenuPrefs.sync();
            }
            case PREF_SCALE -> MenuPrefs.scale = clamped;
            default -> {
            }
        }
    }

    // ---- строки настроек модуля

    float rowHeight(Setting<?> setting, float width) {
        if (setting instanceof NumberSetting) {
            return S(this.compact ? 56 : 62);
        }
        if (setting instanceof ModeSetting mode && segmented(mode, width)) {
            return S(this.compact ? 56 : 62);
        }
        if (setting instanceof MultiSelectSetting multi) {
            return S(this.compact ? 52 : 58) + (chipRows(multi, width) - 1) * S(26);
        }
        return S(this.compact ? 34 : 38);
    }

    private boolean segmented(ModeSetting mode, float width) {
        int count = mode.getModes().size();
        if (count < 2 || count > 3) {
            return false;
        }
        float segment = (width - S(6)) / count;
        for (int i = 0; i < count; i++) {
            if (MenuDraw.textWidth(mode.getModes().get(i), Weight.MEDIUM, S(10)) > segment - S(12)) {
                return false;
            }
        }
        return true;
    }

    private int chipRows(MultiSelectSetting setting, float width) {
        float chipFont = S(10);
        int rows = 1;
        float offset = 0.0F;
        List<String> modes = setting.getModes();
        for (int i = 0; i < modes.size(); i++) {
            float chipWidth = MenuDraw.textWidth(modes.get(i), Weight.MEDIUM, chipFont) + S(16);
            if (offset > 0.0F && offset + chipWidth > width) {
                rows++;
                offset = chipWidth + S(6);
            } else {
                offset += chipWidth + S(6);
            }
        }
        return rows;
    }

    private static String numberText(NumberSetting setting, double value) {
        double step = setting.getStep();
        String text;
        if (step >= 1.0) {
            text = String.valueOf(Math.round(value));
        } else if (step >= 0.1) {
            text = String.format(Locale.ROOT, "%.1f", value);
        } else {
            text = String.format(Locale.ROOT, "%.2f", value);
        }
        String suffix = setting.getSuffix();
        return suffix == null || suffix.isEmpty() ? text : text + suffix;
    }

    /** Рисует строки настроек модуля; возвращает Y после последней строки. */
    float settings(float x, float y, float w, float scrollAmount, float listTop, float listBottom, boolean live) {
        ClientModule module = this.popMod != null ? this.popMod : this.popShown;
        if (module == null) {
            return y;
        }
        int mi = moduleIndex(module);
        List<Setting<?>> list = module.getSettings();
        for (int i = 0; i < list.size(); i++) {
            Setting<?> setting = list.get(i);
            if (!setting.isVisible()) {
                continue;
            }
            float height = rowHeight(setting, w);
            if (y - scrollAmount + height >= listTop - S(8) && y - scrollAmount <= listBottom + S(8)) {
                this.settingRow(module, setting, x, y, w, height, scrollAmount, listTop, listBottom, live, mi, i);
            }
            y += height;
        }
        return y;
    }

    private void settingRow(ClientModule module, Setting<?> setting, float x, float y, float w, float h,
                            float scrollAmount, float listTop, float listBottom, boolean live, int mi, int si) {
        if (si >= SET_PER_MODULE) {
            live = false;
        }
        int global = settingIndex(mi, si);
        int animBase = MenuAnim.SETTING_BASE + global * MenuAnim.SET_STRIDE;

        boolean hovered = live && this.my >= listTop && this.my < listBottom
                && hover(x, y - scrollAmount, w, h);
        if (hovered) {
            hot(HOT_SET + global);
        }
        float hoverAmount = an(animBase + MenuAnim.SET_ROW, hovered ? 1.0F : 0.0F, 18.0F);
        float fontSize = S(this.compact ? 10 : 11);
        String name = MenuDraw.ellipsize(setting.getName(), Weight.MEDIUM, fontSize, w - S(90));
        int labelColour = MenuPalette.text();
        float boxY = y - scrollAmount;

        if (setting instanceof BooleanSetting booleanSetting) {
            MenuDraw.textMid(name, x, boxY, h, Weight.MEDIUM, fontSize, labelColour);
            float on = tw(animBase + MenuAnim.SET_BOOL, booleanSetting.get() ? 1.0F : 0.0F, 200);
            this.toggle(x + w - S(38), boxY, S(38), h, on, hoverAmount);
            if (live) {
                clipped(x, y, w, h, scrollAmount, listTop, listBottom, ACT_BOOL, global);
            }
            return;
        }

        if (setting instanceof NumberSetting number) {
            float labelY = boxY + S(6);
            MenuDraw.text(name, x, labelY, Weight.MEDIUM, fontSize, labelColour);
            float valueAmount = an(animBase + MenuAnim.SET_NUM, hovered ? 1.0F : 0.0F, 16.0F);
            MenuDraw.textRight(numberText(number, number.get()), x + w, labelY, Weight.SEMIBOLD, S(10),
                    MenuDraw.mix(accText(), lt(MenuPalette.text(), 0xFFFFFFFF), valueAmount > 0.5F ? 1.0F : 0.0F));

            float trackY = boxY + h - S(this.compact ? 20 : 24);
            float trackHeight = Math.max(2.0F, S(3));
            float knobRadius = S(5);
            double ratio = number.ratio();
            boolean draggingThis = this.dragAction == ACT_SLIDER && this.dragPayload == global;
            float shown = draggingThis ? (float) ratio : an(animBase + MenuAnim.SET_NUM_POS, (float) ratio, 22.0F);
            float knobX = x + knobRadius + (w - knobRadius * 2.0F) * shown;

            MenuDraw.text(numberText(number, number.getMin()), x, trackY + S(7), Weight.MEDIUM, S(8),
                    lt(MenuPalette.dim(), 0xFF5A616C));
            MenuDraw.textRight(numberText(number, number.getMax()), x + w, trackY + S(7), Weight.MEDIUM, S(8),
                    lt(MenuPalette.dim(), 0xFF5A616C));

            MenuDraw.round(x, trackY, w, trackHeight, trackHeight * 0.5F,
                    MenuDraw.a(MenuPalette.text(), light() ? 26 : 24));
            if (MenuPrefs.gradient) {
                MenuDraw.roundGrad(x, trackY, Math.max(trackHeight, knobX - x), trackHeight, trackHeight * 0.5F,
                        acc(), acc2());
            } else {
                MenuDraw.round(x, trackY, Math.max(trackHeight, knobX - x), trackHeight, trackHeight * 0.5F, acc());
            }
            float knobHover = an(animBase + MenuAnim.SET_NUM_KNOB, hovered ? 0.6F : 0.0F, 20.0F);
            float outer = knobRadius + S(1.5F) * knobHover;
            accentFill(knobX - outer, trackY + trackHeight * 0.5F - outer, outer * 2.0F, outer * 2.0F, outer);

            if (live) {
                this.sliderX[global] = x + knobRadius;
                this.sliderW[global] = w - knobRadius * 2.0F;
                float regionTop = Math.max(listTop, trackY - S(12));
                float regionBottom = Math.min(listBottom, trackY + S(16));
                if (regionBottom > regionTop) {
                    region(x - S(6), regionTop, w + S(12), regionBottom - regionTop, ACT_SLIDER, global);
                }
            }
            return;
        }

        if (setting instanceof ModeSetting mode) {
            if (segmented(mode, w)) {
                MenuDraw.text(name, x, boxY + S(8), Weight.MEDIUM, fontSize, labelColour);
                float segmentHeight = S(this.compact ? 22 : 24);
                float segmentY = boxY + h - segmentHeight - S(this.compact ? 6 : 8);
                int count = mode.getModes().size();
                float inner = S(3);
                float segmentWidth = (w - inner * 2.0F) / count;
                MenuDraw.round(x, segmentY, w, segmentHeight, S(5), lt(0xFFEFEFF4, inset(false)));
                MenuDraw.outline(x, segmentY, w, segmentHeight, S(5),
                        MenuDraw.a(MenuPalette.text(), light() ? 20 : 14));
                int selectedIndex = mode.getModes().indexOf(mode.get());
                float thumb = tw(animBase + MenuAnim.SET_SEGMENT, selectedIndex, 240);
                float thumbStart = x + inner + segmentWidth * thumb;
                float thumbEnd = x + inner + segmentWidth * (thumb + 1.0F);
                accentFill(thumbStart, segmentY + inner, thumbEnd - thumbStart, segmentHeight - inner * 2.0F, S(4));
                int onThumb = MenuDraw.onAccent(acc());
                for (int i = 0; i < count; i++) {
                    String label = mode.getModes().get(i);
                    float proximity = Math.max(0.0F, 1.0F - Math.abs(thumb - i));
                    float start = x + inner + segmentWidth * i;
                    float end = x + inner + segmentWidth * (i + 1);
                    boolean segHovered = live && hover(start, segmentY, end - start, segmentHeight);
                    if (segHovered) {
                        hot(HOT_SEG + global * ITEM_STRIDE + i);
                    }
                    MenuDraw.textCenterMid(label, (start + end) * 0.5F, segmentY, segmentHeight,
                            proximity > 0.5F ? Weight.SEMIBOLD : Weight.MEDIUM, S(10),
                            MenuDraw.mix(segHovered ? MenuPalette.text() : MenuPalette.muted(), onThumb, proximity));
                    if (live && selectedIndex != i) {
                        clipped(start, segmentY, end - start, segmentHeight, scrollAmount, listTop, listBottom,
                                ACT_MODE_ITEM, global * ITEM_STRIDE + i);
                    }
                }
            } else {
                MenuDraw.textMid(name, x, boxY, h, Weight.MEDIUM, fontSize, labelColour);
                String value = mode.get();
                float valueFont = S(10);
                float valueWidth = Math.min(w * 0.55F, MenuDraw.textWidth(value, Weight.MEDIUM, valueFont) + S(26));
                float valueHeight = S(24);
                float valueX = x + w - valueWidth;
                float valueY = boxY + (h - valueHeight) * 0.5F;

                this.modeDropX[global] = valueX;
                this.modeDropY[global] = valueY;
                this.modeDropW[global] = valueWidth;
                this.modeDropH[global] = valueHeight;

                boolean open = this.pop == mode;
                MenuDraw.round(valueX, valueY, valueWidth, valueHeight, S(5),
                        open ? MenuDraw.a(MenuPalette.accent(), light() ? 22 : 36) : inset(hoverAmount > 0.5F));
                MenuDraw.outline(valueX, valueY, valueWidth, valueHeight, S(5),
                        open ? acc() : MenuDraw.a(MenuPalette.text(), light() ? 30 : 16));
                MenuDraw.text(MenuDraw.ellipsize(value, Weight.MEDIUM, valueFont, valueWidth - S(22)), valueX + S(8),
                        valueY + (valueHeight - MenuText.lineHeight(Weight.MEDIUM, valueFont)) * 0.5F,
                        Weight.MEDIUM, valueFont, MenuPalette.text());
                float turn = tw(animBase + MenuAnim.SET_DROPDOWN, open ? 1.0F : 0.0F, 200);
                MenuDraw.iconLettersCentered(LegacyIconGlyphs.CHEVRON, valueX + valueWidth - S(16), valueY, S(12),
                        valueHeight, S(9), MenuDraw.mix(lt(MenuPalette.muted(), 0xFF757D8A), accText(), turn));
                if (live) {
                    clipped(x, y, w, h, scrollAmount, listTop, listBottom, ACT_MODE, global);
                }
            }
            return;
        }

        if (setting instanceof MultiSelectSetting multi) {
            MenuDraw.text(name, x, boxY + S(6), Weight.MEDIUM, fontSize, labelColour);
            float chipFont = S(10);
            float chipHeight = S(22);
            float offsetX = x;
            float offsetY = boxY + S(24);
            List<String> modes = multi.getModes();
            for (int i = 0; i < modes.size(); i++) {
                String option = modes.get(i);
                float chipWidth = MenuDraw.textWidth(option, Weight.MEDIUM, chipFont) + S(16);
                if (offsetX > x && offsetX + chipWidth > x + w) {
                    offsetX = x;
                    offsetY += S(26);
                }
                boolean chipHovered = live && hover(offsetX, offsetY, chipWidth, chipHeight);
                boolean selected = multi.isSelected(option);
                if (selected) {
                    accentWash(offsetX, offsetY, chipWidth, chipHeight, S(5), light() ? 26 : 40);
                } else {
                    MenuDraw.round(offsetX, offsetY, chipWidth, chipHeight, S(5), inset(chipHovered));
                }
                MenuDraw.outline(offsetX, offsetY, chipWidth, chipHeight, S(5),
                        selected ? acc() : MenuDraw.a(MenuPalette.text(), light() ? 30 : 16));
                MenuDraw.textCenterMid(option, offsetX + chipWidth * 0.5F, offsetY, chipHeight, Weight.MEDIUM,
                        chipFont, selected ? accText() : MenuPalette.text());
                if (live) {
                    clipped(offsetX, offsetY, chipWidth, chipHeight, scrollAmount, listTop, listBottom,
                            ACT_LIST_ITEM, global * ITEM_STRIDE + i);
                }
                offsetX += chipWidth + S(6);
            }
            return;
        }

        if (setting instanceof KeyBindSetting keyBind) {
            MenuDraw.textMid(name, x, boxY, h, Weight.MEDIUM, fontSize, labelColour);
            boolean waiting = this.bindingSetting == keyBind;
            String label = waiting ? "Клавиша" : keyBind.get() >= 0 ? KeyBinds.name(keyBind.get()) : "—";
            this.valueBox(label, x + w, boxY + (h - S(24)) * 0.5F, waiting, hovered);
            if (live) {
                clipped(x, y, w, h, scrollAmount, listTop, listBottom, ACT_BIND_SETTING, ACT_STRING_RESET, global);
            }
            return;
        }

        if (setting instanceof ActionSetting action) {
            MenuDraw.textMid(name, x, boxY, h, Weight.MEDIUM, fontSize, labelColour);
            this.valueBox(action.getActionLabel(), x + w, boxY + (h - S(24)) * 0.5F, hovered, hovered);
            if (live) {
                clipped(x, y, w, h, scrollAmount, listTop, listBottom, ACT_ACTION, global);
            }
            return;
        }

        if (setting instanceof ColorSetting color) {
            MenuDraw.textMid(name, x, boxY, h, Weight.MEDIUM, fontSize, labelColour);
            float swatch = S(22);
            float swatchX = x + w - swatch;
            float swatchY = boxY + (h - swatch) * 0.5F;
            int value = color.get();
            if (color.hasAlpha() && (value >>> 24) != 255) {
                this.checker(swatchX, swatchY, swatch, swatch);
            }
            MenuDraw.round(swatchX, swatchY, swatch, swatch, S(4), value | (color.hasAlpha() ? 0 : 0xFF000000));
            MenuDraw.outline(swatchX, swatchY, swatch, swatch, S(4),
                    hovered ? acc() : MenuDraw.a(MenuPalette.text(), light() ? 30 : 16));
            MenuDraw.textRight("#" + String.format(Locale.ROOT, "%08X", value), swatchX - S(6),
                    boxY + (h - MenuText.lineHeight(Weight.MEDIUM, S(9))) * 0.5F, Weight.MEDIUM, S(9),
                    MenuPalette.muted());
            if (live) {
                clipped(x, y, w, h, scrollAmount, listTop, listBottom, ACT_COLOR, global);
            }
            return;
        }

        if (setting instanceof StringSetting string) {
            MenuDraw.textMid(name, x, boxY, h, Weight.MEDIUM, fontSize, labelColour);
            boolean editing = this.editingString == setting;
            String text = editing ? this.stringBuffer : string.get();
            String shown = text.isEmpty() ? "—" : MenuDraw.ellipsize(text, Weight.MEDIUM, S(10), w * 0.5F);
            this.valueBox(shown, x + w, boxY + (h - S(24)) * 0.5F, editing, hovered);
            if (live) {
                clipped(x, y, w, h, scrollAmount, listTop, listBottom, ACT_STRING, ACT_STRING_RESET, global);
            }
        }
    }

    private void checker(float x, float y, float w, float h) {
        float cell = Math.max(2.0F, S(4));
        MenuDraw.round(x, y, w, h, S(4), 0xFF2A2A32);
        int columns = (int) Math.ceil(w / cell);
        int rows = (int) Math.ceil(h / cell);
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                if (((row + column) & 1) == 0) {
                    MenuDraw.rect(x + column * cell, y + row * cell,
                            Math.min(cell, w - column * cell), Math.min(cell, h - row * cell), 0xFF44444E);
                }
            }
        }
    }

    // ---- всплывающие окна

    private void openPopup(ClientModule module) {
        if (this.popMod == module) {
            this.popMod = null;
            MenuSound.play(MenuSound.POP, 0.8F, 0.6F);
            return;
        }
        if (this.popShown != module) {
            this.anim.set(MenuAnim.POPUP, 0.0F);
        }
        this.popMod = module;
        this.binding = null;
        this.bindingSetting = null;
        this.pop = null;
        this.scroll[SCROLL_POPUP] = 0.0F;
        this.anim.set(MenuAnim.SCROLL_BASE + SCROLL_POPUP, 0.0F);
        MenuSound.play(MenuSound.POP);
    }

    private void popover() {
        ModeSetting mode = this.pop;
        float appear = ease((this.now - this.popAt) / 170.0F);
        int global = this.popOverGlobal;
        if (global < 0) {
            this.pop = null;
            return;
        }
        float boxX = this.modeDropX[global];
        float boxY = this.modeDropY[global];
        float boxW = this.modeDropW[global];
        float boxH = this.modeDropH[global];

        List<String> modes = mode.getModes();
        float itemHeight = S(24);
        float radius = S(7);
        float width = Math.max(boxW, S(110));
        float height = modes.size() * itemHeight + S(6);
        float x = Math.min(boxX, this.width - width - S(6));
        float y = boxY + boxH + S(4);
        if (y + height > this.height - S(6)) {
            y = boxY - height - S(4);
        }

        float keep = MenuDraw.fade;
        MenuDraw.fade = keep * Math.min(1.0F, appear * 1.4F);
        MenuDraw.shadow(x, y + S(4), width, height, radius, S(12), MenuDraw.a(0, 80));
        MenuDraw.round(x, y, width, height, radius, light() ? 0xFFFFFFFF : 0xF4171A22);
        MenuDraw.outline(x, y, width, height, radius, light() ? MenuDraw.a(MenuPalette.text(), 24) : 0x1CFFFFFF);

        String selected = mode.get();
        for (int i = 0; i < modes.size(); i++) {
            String option = modes.get(i);
            float itemY = y + S(3) + i * itemHeight;
            boolean hovered = hover(x + S(3), itemY, width - S(6), itemHeight);
            if (hovered) {
                hot(HOT_POPOVER + i);
            }
            float hoverAmount = an(MenuAnim.SCROLL_BASE + MenuAnim.SCROLL_POPOVER + i, hovered ? 1.0F : 0.0F, 18.0F);
            boolean isSelected = option.equals(selected);
            if (hoverAmount > 0.01F || isSelected) {
                MenuDraw.round(x + S(3), itemY, width - S(6), itemHeight, S(5),
                        MenuDraw.mix(inset(hoverAmount > 0.5F),
                                MenuDraw.a(MenuPalette.accent(), light() ? 24 : 36), isSelected ? 1.0F : 0.0F));
            }
            MenuDraw.textMid(option, x + S(10), itemY, itemHeight, isSelected ? Weight.SEMIBOLD : Weight.MEDIUM,
                    S(10), isSelected ? accText() : MenuPalette.text());
            region(x + S(3), itemY, width - S(6), itemHeight, ACT_POPOVER_ITEM, i);
        }
        MenuDraw.fade = keep;
    }

    private int popOverGlobal = -1;

    // ---- выбор цвета

    private void openColorPicker(ColorSetting setting) {
        this.colorSetting = setting;
        int value = setting.get();
        this.pickerAlpha = (value >>> 24) / 255.0F;
        float[] hsb = Color.RGBtoHSB(value >> 16 & 0xFF, value >> 8 & 0xFF, value & 0xFF, null);
        this.pickerHue = hsb[0];
        this.pickerSaturation = hsb[1];
        this.pickerValue = hsb[2];
        this.pickerHex = String.format(Locale.ROOT, "%08X", value);
        this.pickerHexFocused = false;
        MenuSound.play(MenuSound.POP);
    }

    private int pickerColor() {
        int rgb = Color.HSBtoRGB(this.pickerHue, this.pickerSaturation, this.pickerValue);
        int alpha = Math.round(Math.max(0.0F, Math.min(1.0F, this.pickerAlpha)) * 255.0F);
        return alpha << 24 | rgb & 0xFFFFFF;
    }

    private void colorPicker() {
        ColorSetting setting = this.colorSetting;
        float width = S(150);
        float height = S(178);
        float x = Math.max(S(6), Math.min(this.width - width - S(6), this.mx - width * 0.5F));
        float y = Math.max(S(6), Math.min(this.height - height - S(6), this.my + S(16)));
        this.pickerX = x;
        this.pickerY = y;
        this.pickerW = width;
        this.pickerH = height;

        float pad = S(10);
        float radius = S(9);
        int value = this.pickerColor();

        MenuDraw.shadow(x, y + S(4), width, height, radius, S(16), MenuDraw.a(0, 90));
        MenuDraw.round(x, y, width, height, radius, light() ? 0xFFFFFFFF : 0xF4171A22);
        MenuDraw.outline(x, y, width, height, radius, light() ? MenuDraw.a(MenuPalette.text(), 24) : 0x1CFFFFFF);

        // насыщенность/яркость
        float svX = x + pad;
        float svY = y + pad;
        float svW = width - pad * 2.0F;
        float svH = S(74);
        int pure = Color.HSBtoRGB(this.pickerHue, 1.0F, 1.0F) | 0xFF000000;
        MenuDraw.round(svX, svY, svW, svH, S(6), pure);
        MenuDraw.roundGrad(svX, svY, svW, svH, S(6), 0xFFFFFFFF, 0x00FFFFFF);
        MenuDraw.roundGradV(svX, svY, svW, svH, S(6), 0x00000000, 0xFF000000);
        float cursorX = svX + svW * this.pickerSaturation;
        float cursorY = svY + svH * (1.0F - this.pickerValue);
        MenuDraw.ring(cursorX, cursorY, S(4), 1.5F, 0xFFFFFFFF);
        MenuDraw.ring(cursorX, cursorY, S(3), 1.0F, 0xFF000000);
        region(svX, svY, svW, svH, ACT_PICKER_SV, 0);

        // оттенок
        float hueY = svY + svH + S(8);
        float strip = S(8);
        for (int i = 0; i < 24; i++) {
            float from = i / 24.0F;
            float to = (i + 1) / 24.0F;
            int left = Color.HSBtoRGB(from, 1.0F, 1.0F) | 0xFF000000;
            int right = Color.HSBtoRGB(to, 1.0F, 1.0F) | 0xFF000000;
            MenuDraw.hgrad(svX + svW * from, hueY, svW * (to - from) + 0.5F, strip, left, right);
        }
        MenuDraw.ring(svX + svW * this.pickerHue, hueY + strip * 0.5F, S(4), 1.5F, 0xFFFFFFFF);
        region(svX, hueY - S(3), svW, strip + S(6), ACT_PICKER_HUE, 0);

        // альфа
        float alphaY = hueY + strip + S(8);
        this.checker(svX, alphaY, svW, strip);
        MenuDraw.hgrad(svX, alphaY, svW, strip, 0x00FFFFFF | pure & 0xFFFFFF, pure);
        MenuDraw.ring(svX + svW * this.pickerAlpha, alphaY + strip * 0.5F, S(4), 1.5F, 0xFFFFFFFF);
        region(svX, alphaY - S(3), svW, strip + S(6), ACT_PICKER_ALPHA, 0);

        // образец и hex
        float sampleSize = S(20);
        float sampleY = alphaY + strip + S(8);
        if (setting.hasAlpha()) {
            this.checker(svX, sampleY, sampleSize, sampleSize);
        }
        MenuDraw.round(svX, sampleY, sampleSize, sampleSize, S(4),
                setting.hasAlpha() ? value : value | 0xFF000000);
        MenuDraw.outline(svX, sampleY, sampleSize, sampleSize, S(4), MenuDraw.a(MenuPalette.text(), 30));

        String hex = this.pickerHexFocused ? this.pickerHex : String.format(Locale.ROOT, "%08X", value);
        float hexX = svX + sampleSize + S(6);
        float hexW = width - pad * 2.0F - sampleSize - S(8);
        float hexH = sampleSize;
        MenuDraw.round(hexX, sampleY, hexW, hexH, S(4),
                this.pickerHexFocused ? MenuDraw.a(MenuPalette.accent(), 30) : inset(false));
        MenuDraw.outline(hexX, sampleY, hexW, hexH, S(4),
                this.pickerHexFocused ? acc() : MenuDraw.a(MenuPalette.text(), 18));
        MenuDraw.textMid(hex, hexX + S(6), sampleY, hexH, Weight.MEDIUM, S(10), MenuPalette.text());
        region(hexX, sampleY, hexW, hexH, ACT_PICKER_HEX, 0);

        // кнопки
        float buttonY = sampleY + sampleSize + S(8);
        float buttonW = (width - pad * 2.0F - S(6)) * 0.5F;
        float buttonH = S(20);
        boolean resetHover = hover(svX, buttonY, buttonW, buttonH);
        MenuDraw.round(svX, buttonY, buttonW, buttonH, S(5), inset(resetHover));
        MenuDraw.textCenterMid("Сброс", svX + buttonW * 0.5F, buttonY, buttonH, Weight.MEDIUM, S(10),
                MenuPalette.text());
        region(svX, buttonY, buttonW, buttonH, ACT_PICKER_CLOSE, 1);

        float closeX = svX + buttonW + S(6);
        boolean closeHover = hover(closeX, buttonY, buttonW, buttonH);
        MenuDraw.round(closeX, buttonY, buttonW, buttonH, S(5), accentWashColor(closeHover));
        MenuDraw.textCenterMid("Готово", closeX + buttonW * 0.5F, buttonY, buttonH, Weight.SEMIBOLD, S(10),
                accText());
        region(closeX, buttonY, buttonW, buttonH, ACT_PICKER_CLOSE, 0);
    }

    private int accentWashColor(boolean hovered) {
        return MenuDraw.a(acc(), hovered ? 90 : 60);
    }

    // ---- нижний док

    private void dock(float centreX, float edge, float slide) {
        String[] labels = {"Модули", "Настройки"};
        float buttonSize = S(44);
        float gap = S(8);
        float pad = S(6);
        int count = labels.length;
        float width = count * buttonSize + (count - 1) * gap + pad * 2.0F;
        float height = buttonSize + pad * 2.0F;
        float x = centreX - width * 0.5F;
        float y = edge + slide - height * 0.5F;

        MenuDraw.shadow(x, y + S(3), width, height, height * 0.5F, S(14), MenuDraw.a(0, light() ? 30 : 90));
        if (MenuPrefs.blur) {
            BeginFrameHelper.rect(x, y, width, height)
                    .radius(height * 0.5F)
                    .color(light() ? 0xE6FFFFFF : MenuDraw.a(MenuPalette.bg(), 214))
                    .blur(S(18), MenuDraw.fade)
                    .hudGlow(false)
                    .draw();
        } else {
            MenuDraw.round(x, y, width, height, height * 0.5F,
                    light() ? 0xFFFFFFFF : MenuDraw.mix(MenuPalette.bg() | 0xFF000000, 0xFFFFFFFF, 0.025F));
        }
        MenuDraw.outline(x, y, width, height, height * 0.5F,
                MenuDraw.a(MenuPalette.text(), light() ? 30 : 22));

        int activeIndex = page == Page.SETTINGS ? 1 : 0;
        float targetX = x + pad + activeIndex * (buttonSize + gap);
        float thumbX = tw(MenuAnim.DOCK_X, targetX, 280);
        accentFill(thumbX, y + pad, buttonSize, buttonSize, buttonSize * 0.5F);
        int onCircle = MenuDraw.onAccent(acc());

        for (int i = 0; i < count; i++) {
            float buttonX = x + pad + i * (buttonSize + gap);
            boolean hovered = !blocked() && hover(buttonX, y + pad, buttonSize, buttonSize);
            if (hovered) {
                hot(HOT_DOCK + i);
            }
            float hoverAmount = an(MenuAnim.DOCK_HOVER + i, hovered ? 1.0F : 0.0F, 18.0F);
            float lit = Math.max(0.0F, 1.0F - Math.abs(thumbX - buttonX) / (buttonSize + gap));
            if (hoverAmount > 0.01F && lit < 0.99F) {
                MenuDraw.circle(buttonX + buttonSize * 0.5F, y + pad + buttonSize * 0.5F, buttonSize * 0.5F,
                        MenuDraw.a(MenuPalette.text(), (int) (12 * hoverAmount * (1.0F - lit))));
            }
            int iconColour = MenuDraw.mix(
                    MenuDraw.mix(MenuDraw.mix(MenuPalette.muted(), MenuPalette.dim(), 0.5F), MenuPalette.text(),
                            hoverAmount),
                    0xFFFFFFFF, lit);
            if (i == activeIndex) {
                iconColour = onCircle;
            }
            // «Модули» — иконка списка модулей, «Настройки» — ползунки.
            // Раньше здесь стояли CONFIGS (иконка конфигов) и STAR (избранное).
            float iconSize = S(20);
            if (i == 0) {
                MenuDraw.iconCsCentered(IconGlyphs.MISC, buttonX, y + pad, buttonSize, buttonSize, iconSize,
                        iconColour);
            } else {
                MenuDraw.iconLettersCentered(LegacyIconGlyphs.FILTERS, buttonX, y + pad, buttonSize, buttonSize,
                        iconSize, iconColour);
            }

            if (hoverAmount > 0.02F) {
                String label = labels[i];
                float labelFont = S(10);
                float labelWidth = MenuDraw.textWidth(label, Weight.MEDIUM, labelFont) + S(16);
                float labelHeight = S(22);
                float labelX = buttonX + buttonSize * 0.5F - labelWidth * 0.5F;
                float labelY = y + height + S(6) - (1.0F - hoverAmount) * S(4);
                float keep = MenuDraw.fade;
                MenuDraw.fade = keep * hoverAmount;
                MenuDraw.round(labelX, labelY, labelWidth, labelHeight, labelHeight * 0.5F,
                        light() ? 0xFFFFFFFF
                                : MenuDraw.mix(MenuPalette.bg() | 0xFF000000, 0xFFFFFFFF, 0.06F));
                MenuDraw.outline(labelX, labelY, labelWidth, labelHeight, labelHeight * 0.5F,
                        MenuDraw.a(MenuPalette.text(), light() ? 26 : 22));
                MenuDraw.textCenterMid(label, buttonX + buttonSize * 0.5F, labelY, labelHeight, Weight.MEDIUM,
                        labelFont, MenuPalette.text());
                MenuDraw.fade = keep;
            }

            if (!blocked()) {
                region(buttonX, y + pad, buttonSize, buttonSize, ACT_DOCK, i);
            }
        }
    }

    private void dockClick(int index) {
        Page target = index == 1 ? Page.SETTINGS : Page.MODULES;
        if (page == target) {
            return;
        }
        page = target;
        this.search = "";
        this.searchFocused = false;
        content(MenuSound.PAGE);
    }

    void content(int sound) {
        this.contentAt = this.now;
        this.popMod = null;
        this.pop = null;
        this.popOverGlobal = -1;
        this.binding = null;
        this.bindingSetting = null;
        if (sound >= 0) {
            MenuSound.play(sound);
        }
    }

    // ---- индексы

    int moduleIndex(ClientModule module) {
        List<ClientModule> pool = this.modules.getModules();
        int index = pool.indexOf(module);
        if (index < 0) {
            return 0;
        }
        return Math.min(index, MenuAnim.MAX_MODULES - 1);
    }

    static int settingIndex(int moduleIndex, int settingIndex) {
        int global = moduleIndex * SET_PER_MODULE + settingIndex;
        return Math.min(global, MenuAnim.MAX_SETTINGS - 1);
    }


    // ---- ввод

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        this.mx = (float) event.x();
        this.my = (float) event.y();
        int button = event.button();

        // назначение клавиши перехватывает любой клик мышью
        if (this.binding != null || this.bindingSetting != null) {
            int mouseKey = KeyBinds.MOUSE_BASE + Math.max(0, button);
            this.applyBinding(mouseKey);
            return true;
        }

        if (this.pop != null) {
            int index = this.findRegion(event.x(), event.y());
            if (index >= 0 && this.regionAction[index] == ACT_POPOVER_ITEM) {
                this.runAction(ACT_POPOVER_ITEM, this.regionPayload[index]);
                return true;
            }
            this.pop = null;
            this.popOverGlobal = -1;
            MenuSound.play(MenuSound.DROP, 1.0F, 0.6F);
        }

        if (this.colorSetting != null) {
            int index = this.findRegion(event.x(), event.y());
            if (index >= 0 && (this.regionAction[index] == ACT_PICKER_SV
                    || this.regionAction[index] == ACT_PICKER_HUE
                    || this.regionAction[index] == ACT_PICKER_ALPHA
                    || this.regionAction[index] == ACT_PICKER_HEX
                    || this.regionAction[index] == ACT_PICKER_CLOSE)) {
                this.dragAction = this.regionAction[index];
                this.dragPayload = this.regionPayload[index];
                this.dragging = this.regionAction[index] == ACT_PICKER_SV
                        || this.regionAction[index] == ACT_PICKER_HUE
                        || this.regionAction[index] == ACT_PICKER_ALPHA;
                this.runAction(this.regionAction[index], this.regionPayload[index]);
                return true;
            }
            this.colorSetting = null;
            this.pickerHexFocused = false;
            MenuSound.play(MenuSound.RESET, 1.0F, 0.4F);
        }

        int index = this.findRegion(event.x(), event.y());
        if (index < 0) {
            this.searchFocused = false;
            this.commitStringEdit();
            return false;
        }

        this.searchFocused = false;
        int action = this.regionAction[index];
        int alt = this.regionAlt[index];
        int payload = this.regionPayload[index];
        if (button == 1) {
            // правая кнопка — только альтернативное действие (например, открыть
            // настройки модуля); если его нет, клик не должен ничего переключать
            if (alt == ACT_NONE) {
                return true;
            }
            action = alt;
        } else if (button != 0) {
            // средняя и остальные кнопки в меню ничего не включают
            return true;
        }
        // Непрерывные контролы: нажатие взводит перетаскивание, иначе ползунок
        // двигался бы только по одиночным кликам.
        if (button == 0 && (action == ACT_SLIDER || action == ACT_PREF_SLIDER)) {
            this.dragAction = action;
            this.dragPayload = payload;
            this.dragging = true;
            MenuSound.play(MenuSound.GRAB);
        }
        this.runAction(action, payload);
        return true;
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        boolean wasDragging = this.dragging;
        this.dragging = false;
        this.dragAction = ACT_NONE;
        if (wasDragging) {
            this.markDirty();
            MenuSound.play(MenuSound.DROP, 1.0F, 0.5F);
            return true;
        }
        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        if (!this.dragging) {
            return super.mouseDragged(event, dragX, dragY);
        }
        this.mx = (float) event.x();
        this.my = (float) event.y();
        this.runAction(this.dragAction, this.dragPayload);
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontal, double vertical) {
        if (this.pop != null || this.colorSetting != null) {
            return true;
        }
        if (mouseX < this.cx0 - S(10) || mouseX > this.cx0 + this.cw0 + S(10)
                || mouseY < this.top || mouseY > this.bottom) {
            return super.mouseScrolled(mouseX, mouseY, horizontal, vertical);
        }
        int context = this.scrollContext();
        float view = this.bottom - this.top;
        float max = Math.max(0.0F, this.scrollTotal[context] - view);
        this.scroll[context] = Math.max(0.0F, Math.min(max,
                this.scroll[context] - (float) vertical * S(46)));
        return true;
    }

    private int scrollContext() {
        if (this.popMod != null || this.popShown != null) {
            return SCROLL_POPUP;
        }
        if (page == Page.SETTINGS) {
            return SCROLL_PREFS;
        }
        return this.search.isEmpty() ? category.ordinal() : SCROLL_SEARCH;
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        int key = event.key();

        if (this.binding != null || this.bindingSetting != null) {
            this.applyBinding(key == 256 ? -1 : key);
            return true;
        }

        if (this.pickerHexFocused) {
            if (key == 256) {
                this.pickerHexFocused = false;
                return true;
            }
            if (key == 257) {
                this.commitPickerHex();
                return true;
            }
            if (key == 259 && !this.pickerHex.isEmpty()) {
                this.pickerHex = this.pickerHex.substring(0, this.pickerHex.length() - 1);
                return true;
            }
        }

        if (this.editingString != null) {
            if (key == 256) {
                this.editingString = null;
                this.stringBuffer = "";
                return true;
            }
            if (key == 257) {
                this.commitStringEdit();
                return true;
            }
            if (key == 259 && !this.stringBuffer.isEmpty()) {
                this.stringBuffer = this.stringBuffer.substring(0, this.stringBuffer.length() - 1);
                return true;
            }
        }

        if (key == 256) {
            if (this.pop != null) {
                this.pop = null;
                this.popOverGlobal = -1;
                MenuSound.play(MenuSound.DROP, 1.0F, 0.6F);
                return true;
            }
            if (this.colorSetting != null) {
                this.colorSetting = null;
                return true;
            }
            if (this.searchFocused && !this.search.isEmpty()) {
                this.search = "";
                return true;
            }
            if (this.popMod != null) {
                this.popMod = null;
                MenuSound.play(MenuSound.CLOSE, 0.9F, 0.7F);
                return true;
            }
            this.close();
            return true;
        }

        if (key == 257) {
            this.commitStringEdit();
            return true;
        }

        if (this.searchFocused) {
            if (key == 259 && !this.search.isEmpty()) {
                this.search = this.search.substring(0, this.search.length() - 1);
                this.scroll[this.scrollContext()] = 0.0F;
                MenuSound.play(MenuSound.TYPE);
                return true;
            }
            if (key == 262 || key == 263 || key == 268 || key == 269 || key == 265 || key == 264) {
                return true;
            }
        }

        // Ctrl+F — фокус поиска
        if (key == GLFW.GLFW_KEY_F && this.isControlDown()) {
            this.searchFocused = true;
            MenuSound.play(MenuSound.SELECT);
            return true;
        }

        return super.keyPressed(event);
    }

    private boolean isControlDown() {
        if (this.minecraft == null || this.minecraft.getWindow() == null) {
            return false;
        }
        long window = this.minecraft.getWindow().handle();
        return GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_CONTROL) == 1
                || GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT_CONTROL) == 1;
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        if (!event.isAllowedChatCharacter()) {
            return false;
        }
        String text = event.codepointAsString();

        if (this.pickerHexFocused) {
            for (int i = 0; i < text.length(); i++) {
                char character = text.charAt(i);
                if (Character.digit(character, 16) >= 0 && this.pickerHex.length() < 8) {
                    this.pickerHex = this.pickerHex + Character.toUpperCase(character);
                }
            }
            return true;
        }

        if (this.editingString != null) {
            int max = this.editingString instanceof StringSetting string ? string.getMaxLength()
                    : this.stringBuffer.length() + text.length();
            if (this.stringBuffer.length() + text.length() <= max) {
                this.stringBuffer = this.stringBuffer + text;
                MenuSound.play(MenuSound.TYPE);
            }
            return true;
        }

        if (this.searchFocused) {
            if (this.search.length() < 48) {
                this.search = this.search + text;
                this.scroll[this.scrollContext()] = 0.0F;
                MenuSound.play(MenuSound.TYPE);
            }
            return true;
        }
        return false;
    }

    @Override
    public void onClose() {
        this.close();
    }

    private void close() {
        if (this.closing) {
            return;
        }
        this.commitStringEdit();
        MenuPrefs.save();
        this.saveConfig();
        this.closing = true;
        this.closingAt = System.currentTimeMillis();
        MenuSound.play(MenuSound.CLOSE);
    }

    private void commitStringEdit() {
        if (this.editingString == null) {
            return;
        }
        this.editingString.set(this.stringBuffer);
        this.editingString = null;
        this.stringBuffer = "";
        this.markDirty();
    }

    private void commitPickerHex() {
        if (this.colorSetting == null || this.pickerHex.isEmpty()) {
            this.pickerHexFocused = false;
            return;
        }
        try {
            int value = (int) Long.parseLong(this.pickerHex, 16);
            if (this.pickerHex.length() <= 6) {
                value |= 0xFF000000;
            }
            this.applyPickerColor(value);
        } catch (NumberFormatException ignored) {
            // ввод неполный — цвет не меняем, курсор остаётся в поле
            return;
        }
        this.pickerHexFocused = false;
    }

    private void applyPickerColor(int value) {
        this.pickerAlpha = (value >>> 24) / 255.0F;
        float[] hsb = Color.RGBtoHSB(value >> 16 & 0xFF, value >> 8 & 0xFF, value & 0xFF, null);
        this.pickerHue = hsb[0];
        this.pickerSaturation = hsb[1];
        this.pickerValue = hsb[2];
        if (this.colorSetting != null) {
            this.colorSetting.set(value);
            this.markDirty();
        }
    }

    private void applyBinding(int key) {
        if (this.binding != null) {
            if (key >= 0 && KeyBinds.isPrimaryMouseButton(key)) {
                return;
            }
            this.binding.setKeyBind(key);
            this.binding = null;
            MenuSound.play(MenuSound.SELECT);
            this.markDirty();
            return;
        }
        if (this.bindingSetting instanceof KeyBindSetting setting) {
            setting.set(key);
            this.bindingSetting = null;
            this.bindingSettingIndex = -1;
            MenuSound.play(MenuSound.SELECT);
            this.markDirty();
        }
    }

    private int findRegion(double x, double y) {
        for (int i = this.regionCount - 1; i >= 0; i--) {
            if (x >= this.regionX[i] && x < this.regionX[i] + this.regionW[i]
                    && y >= this.regionY[i] && y < this.regionY[i] + this.regionH[i]) {
                return i;
            }
        }
        return -1;
    }

    // ---- выполнение действий

    private void runAction(int action, int payload) {
        switch (action) {
            case ACT_TAB -> {
                if (page == Page.MODULES) {
                    ModuleCategory target = TABS[Math.floorMod(payload, TABS.length)];
                    if (category != target || !this.search.isEmpty()) {
                        category = target;
                        this.search = "";
                        this.searchFocused = false;
                        content(MenuSound.TAB);
                    }
                }
            }
            case ACT_THEME -> {
                boolean toLight = MenuPalette.palette != MenuPalette.LIGHT;
                MenuPalette.setPalette(toLight ? MenuPalette.LIGHT : MenuPalette.NIGHT);
                MenuPrefs.palette = MenuPalette.palette;
                MenuSound.play(MenuSound.TOGGLE_ON);
                this.markDirty();
            }
            case ACT_PALETTE -> {
                MenuPalette.setPalette(payload);
                MenuPrefs.palette = MenuPalette.palette;
                MenuSound.play(MenuSound.CLICK);
                this.markDirty();
            }
            case ACT_SEARCH -> {
                this.searchFocused = true;
                MenuSound.play(MenuSound.SELECT);
            }
            case ACT_SEARCH_CLEAR -> {
                this.search = "";
                this.searchFocused = false;
                MenuSound.play(MenuSound.RESET);
            }
            case ACT_MODULE_TOGGLE -> {
                ClientModule module = this.moduleByIndex(payload);
                if (module != null) {
                    module.toggle();
                    MenuSound.play(module.isEnabled() ? MenuSound.CHARGE : MenuSound.DRAIN);
                    this.markDirty();
                }
            }
            case ACT_MODULE_OPEN -> {
                ClientModule module = this.moduleByIndex(payload);
                if (module != null) {
                    this.openPopup(module);
                    this.pop = null;
                    this.popOverGlobal = -1;
                }
            }
            case ACT_MODULE_KEY -> {
                ClientModule module = this.moduleByIndex(payload);
                if (module != null) {
                    this.binding = module;
                    MenuSound.play(MenuSound.SELECT);
                }
            }
            case ACT_MODULE_KEY_RESET -> {
                ClientModule module = this.moduleByIndex(payload);
                if (module != null) {
                    module.setKeyBind(-1);
                    MenuSound.play(MenuSound.RESET);
                    this.markDirty();
                }
            }
            case ACT_DOCK -> this.dockClick(payload);
            case ACT_POPUP_TOGGLE -> {
                ClientModule module = this.moduleByIndex(payload);
                if (module != null) {
                    module.toggle();
                    MenuSound.play(module.isEnabled() ? MenuSound.CHARGE : MenuSound.DRAIN);
                    this.markDirty();
                }
            }
            case ACT_POPUP_KEY -> {
                ClientModule module = this.moduleByIndex(payload);
                if (module != null) {
                    this.binding = module;
                    MenuSound.play(MenuSound.SELECT);
                }
            }
            case ACT_POPUP_KEY_RESET -> {
                ClientModule module = this.moduleByIndex(payload);
                if (module != null) {
                    module.setKeyBind(-1);
                    MenuSound.play(MenuSound.RESET);
                    this.markDirty();
                }
            }
            case ACT_SCROLL -> {
                // прокрутка колесом обрабатывается в mouseScrolled; клик по полосе — на страницу
                int context = payload;
                if (context >= 0 && context < this.scroll.length) {
                    this.scroll[context] = Math.max(0.0F, this.scroll[context] - (this.bottom - this.top) * 0.8F);
                    MenuSound.play(MenuSound.TICK);
                }
            }
            case ACT_SLIDER -> this.applySlider(payload, this.mx);
            case ACT_MODE -> {
                Setting<?> setting = this.settingByGlobal(payload);
                if (setting instanceof ModeSetting mode) {
                    this.pop = mode;
                    this.popOverGlobal = payload;
                    this.popAt = this.now;
                    MenuSound.play(MenuSound.POP, 1.0F, 0.7F);
                }
            }
            case ACT_MODE_ITEM -> {
                int global = payload / ITEM_STRIDE;
                int index = Math.floorMod(payload, ITEM_STRIDE);
                Setting<?> setting = this.settingByGlobal(global);
                if (setting instanceof ModeSetting mode && index < mode.getModes().size()) {
                    mode.set(mode.getModes().get(index));
                    MenuSound.play(MenuSound.SELECT);
                    this.markDirty();
                }
            }
            case ACT_BOOL -> {
                Setting<?> setting = this.settingByGlobal(payload);
                if (setting instanceof BooleanSetting booleanSetting) {
                    booleanSetting.toggle();
                    MenuSound.play(booleanSetting.get() ? MenuSound.TOGGLE_ON : MenuSound.TOGGLE_OFF);
                    this.markDirty();
                }
            }
            case ACT_LIST_ITEM -> {
                int global = payload / ITEM_STRIDE;
                int index = Math.floorMod(payload, ITEM_STRIDE);
                Setting<?> setting = this.settingByGlobal(global);
                if (setting instanceof MultiSelectSetting multi && index < multi.getModes().size()) {
                    multi.toggle(multi.getModes().get(index));
                    MenuSound.play(MenuSound.CLICK);
                    this.markDirty();
                }
            }
            case ACT_BIND_SETTING -> {
                Setting<?> setting = this.settingByGlobal(payload);
                if (setting instanceof KeyBindSetting keyBind) {
                    this.bindingSetting = keyBind;
                    this.bindingSettingIndex = payload;
                    MenuSound.play(MenuSound.SELECT);
                }
            }
            case ACT_STRING_RESET -> {
                Setting<?> setting = this.settingByGlobal(payload);
                if (setting != null) {
                    setting.reset();
                    MenuSound.play(MenuSound.RESET);
                    this.markDirty();
                }
            }
            case ACT_ACTION -> {
                Setting<?> setting = this.settingByGlobal(payload);
                if (setting instanceof ActionSetting actionSetting) {
                    actionSetting.press();
                    MenuSound.play(MenuSound.CLICK);
                }
            }
            case ACT_STRING -> {
                Setting<?> setting = this.settingByGlobal(payload);
                if (setting instanceof StringSetting string) {
                    this.editingString = string;
                    this.stringBuffer = string.get();
                    MenuSound.play(MenuSound.SELECT);
                }
            }
            case ACT_COLOR -> {
                Setting<?> setting = this.settingByGlobal(payload);
                if (setting instanceof ColorSetting color) {
                    this.openColorPicker(color);
                }
            }
            case ACT_PREF_BOOL -> this.togglePref(payload);
            case ACT_PREF_SLIDER -> this.applyPrefSlider(payload, this.mx);
            case ACT_POPOVER_ITEM -> {
                if (this.pop != null) {
                    List<String> modes = this.pop.getModes();
                    if (payload >= 0 && payload < modes.size()) {
                        this.pop.set(modes.get(payload));
                        MenuSound.play(MenuSound.SELECT);
                        this.markDirty();
                    }
                }
                this.pop = null;
                this.popOverGlobal = -1;
            }
            case ACT_PICKER_SV -> {
                if (this.colorSetting != null) {
                    this.pickerSaturation = clamp01((float) ((this.mx - this.pickerX - S(10)) / (this.pickerW - S(20))));
                    this.pickerValue = 1.0F - clamp01((float) ((this.my - this.pickerY - S(10)) / S(74)));
                    this.applyPickerColor(this.pickerColor());
                }
            }
            case ACT_PICKER_HUE -> {
                if (this.colorSetting != null) {
                    this.pickerHue = clamp01((float) ((this.mx - this.pickerX - S(10)) / (this.pickerW - S(20))));
                    this.applyPickerColor(this.pickerColor());
                    MenuSound.play(MenuSound.TICK);
                }
            }
            case ACT_PICKER_ALPHA -> {
                if (this.colorSetting != null) {
                    this.pickerAlpha = clamp01((float) ((this.mx - this.pickerX - S(10)) / (this.pickerW - S(20))));
                    this.applyPickerColor(this.pickerColor());
                    MenuSound.play(MenuSound.TICK);
                }
            }
            case ACT_PICKER_HEX -> this.pickerHexFocused = true;
            case ACT_PICKER_CLOSE -> {
                if (payload == 1 && this.colorSetting != null) {
                    this.colorSetting.reset();
                    this.applyPickerColor(this.colorSetting.get());
                    MenuSound.play(MenuSound.RESET);
                } else {
                    this.colorSetting = null;
                    this.pickerHexFocused = false;
                    MenuSound.play(MenuSound.CLICK);
                }
            }
            default -> {
            }
        }
    }

    private static float clamp01(float value) {
        return Math.max(0.0F, Math.min(1.0F, value));
    }

    private void togglePref(int pref) {
        switch (pref) {
            case PREF_SOUNDS -> MenuPrefs.sounds = !MenuPrefs.sounds;
            case PREF_HOVER_SOUNDS -> MenuPrefs.hoverSounds = !MenuPrefs.hoverSounds;
            case PREF_SLIDER_SOUNDS -> MenuPrefs.sliderSounds = !MenuPrefs.sliderSounds;
            case PREF_BLUR -> MenuPrefs.blur = !MenuPrefs.blur;
            case PREF_ANIMATIONS -> {
                MenuPrefs.animations = !MenuPrefs.animations;
                MenuPrefs.sync();
            }
            case PREF_GRADIENT -> MenuPrefs.gradient = !MenuPrefs.gradient;
            default -> {
            }
        }
        MenuSound.play(prefBool(pref) ? MenuSound.TOGGLE_ON : MenuSound.TOGGLE_OFF);
        MenuPrefs.save();
    }

    private void applySlider(int global, double at) {
        Setting<?> setting = this.settingByGlobal(global);
        if (!(setting instanceof NumberSetting number)) {
            return;
        }
        float start = this.sliderX[global];
        float width = Math.max(1.0F, this.sliderW[global]);
        number.setRatio((float) ((at - start) / width));
        MenuSound.play(MenuSound.TICK);
        this.markDirty();
    }

    private void applyPrefSlider(int pref, double at) {
        // позиция дорожки совпадает с левым краем строки, ширина — вся строка группы
        int order = prefOrderInGroup(pref);
        float rowWidth = this.prefSliderW[order];
        float startX = this.prefSliderX[order];
        if (rowWidth <= 0.0F) {
            return;
        }
        float ratio = (float) ((at - startX) / rowWidth);
        this.applyPref(pref, PREF_MIN[pref] + clamp01(ratio) * (PREF_MAX[pref] - PREF_MIN[pref]));
        MenuSound.play(MenuSound.TICK);
        MenuPrefs.save();
    }

    private final float[] prefSliderX = new float[PREFS_COUNT];
    private final float[] prefSliderW = new float[PREFS_COUNT];

    private int prefOrderInGroup(int pref) {
        return pref;
    }

    private ClientModule moduleByIndex(int index) {
        List<ClientModule> pool = this.modules.getModules();
        if (index < 0 || index >= pool.size()) {
            return null;
        }
        return pool.get(index);
    }

    private Setting<?> settingByGlobal(int global) {
        if (global < 0) {
            return null;
        }
        int moduleIndex = global / SET_PER_MODULE;
        int settingIndex = global % SET_PER_MODULE;
        ClientModule module = this.popMod != null ? this.popMod : this.popShown;
        if (module == null) {
            return null;
        }
        if (moduleIndex != moduleIndex(module)) {
            return null;
        }
        List<Setting<?>> list = module.getSettings();
        int visible = 0;
        for (Setting<?> setting : list) {
            if (!setting.isVisible()) {
                continue;
            }
            if (visible == settingIndex) {
                return setting;
            }
            visible++;
        }
        return null;
    }

    private void markDirty() {
        this.configDirty = true;
    }

    private void saveConfig() {
        if (!this.configDirty || this.config == null) {
            return;
        }
        this.configDirty = false;
        try {
            this.config.save();
        } catch (RuntimeException failure) {
            SkycoreClient.LOGGER.warn("Failed to save Skycore config after menu change", failure);
        }
    }
}
