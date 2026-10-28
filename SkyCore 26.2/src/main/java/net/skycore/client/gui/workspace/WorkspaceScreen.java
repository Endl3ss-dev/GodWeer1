package net.skycore.client.gui.workspace;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.skycore.client.SkycoreClient;
import net.skycore.client.bot.BedWarsBot;
import net.skycore.client.bot.BotConfig;
import net.skycore.client.bot.BotAppearance;
import net.skycore.client.bot.BotScript;
import net.skycore.client.config.ConfigManager;
import net.skycore.client.gui.menu.MenuAnim;
import net.skycore.client.gui.menu.MenuDraw;
import net.skycore.client.gui.menu.MenuPalette;
import net.skycore.client.gui.menu.MenuPrefs;
import net.skycore.client.gui.menu.MenuSound;
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
import net.skycore.client.util.SelectAndSaveHelper;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;

import org.lwjgl.glfw.GLFW;

/**
 * Workspace — новое меню клиента.
 *
 * <p>Это не порт целиком: анатомия взята из лучшего, что нашлось в папке, и собрана
 * заново под SkyCore. Что именно и почему:
 *
 * <ul>
 *   <li><b>Анатомия</b> — {@code Phobia/system/screensystem/clickgui} (одно окно,
 *       сайдбар категорий, карточки модулей и панель настроек справа). Из всех
 *       клиентов в папке это единственная схема, которая масштабируется на девяносто
 *       модулей и девять типов настроек без «прыгающих» попапов; Arcanic-схема
 *       (карточки + всплывающее окно) уже перенесена в {@code gui.menu.MenuScreen} и
 *       именно она надоела, поэтому повторять её смысла нет.</li>
 *   <li><b>Поиск</b> — как в {@code Phobia/SearchComponent} и {@code ArcanicMenu}:
 *       ищет по всем категориям, подсвечивает совпадение, Ctrl+F ставит фокус.</li>
 *   <li><b>Палитра</b> — своя: берутся те же двадцать палитр клиента, что и в
 *       {@code MenuPalette}, поэтому смена темы не рассинхронизирует меню с HUD.</li>
 *   <li><b>Кадр</b> — целиком по правилам SkyCore: целочисленные ключи анимаций,
 *       массивы хит-областей вместо {@code Runnable}/{@code HashMap<String,…>},
 *       никаких строковых ключей, лямбд и боксинга в отрисовке.</li>
 * </ul>
 *
 * <p>Это основное меню клиента: бинд {@code ClickGUI} открывает его, если в настройке
 * {@code Interface} не выбран старый экран явно. Старые варианты ({@code Swerve} и
 * {@code Skycore}) остаются — в них есть разделы (конфиги, косметика), которых здесь
 * пока нет, и отбирать их у пользователя нельзя; выбрать их можно прямо здесь,
 * в карточке модуля {@code ClickGUI}.
 */
public final class WorkspaceScreen extends Screen {

    // ---- хит-области уровня экрана
    private static final int HIT_NONE = 0;
    private static final int HIT_CATEGORY = 100;
    private static final int HIT_SEARCH = 101;
    private static final int HIT_PALETTE_BUTTON = 102;
    private static final int HIT_CLOSE = 103;
    private static final int HIT_CARD = 104;
    private static final int HIT_CARD_TOGGLE = 105;
    private static final int HIT_CARD_KEY = 106;
    private static final int HIT_PANE_TOGGLE = 107;
    private static final int HIT_PANE_SCROLL = 108;
    private static final int HIT_LOOK_PALETTE = 110;
    private static final int HIT_LOOK_BLUR = 111;
    private static final int HIT_LOOK_GRADIENT = 112;
    private static final int HIT_LOOK_ANIMATIONS = 113;
    private static final int HIT_LOOK_SCALE = 114;
    private static final int HIT_LOOK_CLOSE = 115;
    private static final int HIT_MODE_ITEM = 116;
    /** Пункт «Бот» в сайдбаре: не категория модулей, поэтому отдельный тип. */
    private static final int HIT_BOT_TAB = 117;

    private static final int MAX_HITS = 640;
    private static final int MAX_VISIBLE = 160;

    /** Ключи анимаций карточек: 4 на модуль, вне диапазонов панели/сайдбара/настроек. */
    private static final int CARD_ANIM_BASE = 3_000;

    private static final ModuleCategory[] TABS = {
            ModuleCategory.COMBAT,
            ModuleCategory.MOVEMENT,
            ModuleCategory.RENDER,
            ModuleCategory.PLAYER,
            ModuleCategory.MISC,
    };

    private static final String[] TAB_TITLES = {"Бой", "Движение", "Визуал", "Игрок", "Утилиты"};
    private static final String[] TAB_TITLES_EN = {"Combat", "Movement", "Visual", "Player", "Misc"};

    private final ModuleManager modules;
    private final ConfigManager config;

    private final WorkspaceLayout layout = new WorkspaceLayout();
    private final WorkspaceTheme theme = new WorkspaceTheme();
    private final MenuAnim anim = new MenuAnim();
    private final WorkspaceColorPicker picker = new WorkspaceColorPicker();
    private final WorkspaceBotPanel botPanel = new WorkspaceBotPanel();

    private final IdentityHashMap<ClientModule, Integer> moduleIndex = new IdentityHashMap<>();
    private final int[] categoryCount = new int[5];
    private final ClientModule[] visible = new ClientModule[MAX_VISIBLE];
    private int visibleCount;

    // ---- хит-области кадра
    private final float[] hx = new float[MAX_HITS];
    private final float[] hy = new float[MAX_HITS];
    private final float[] hw = new float[MAX_HITS];
    private final float[] hh = new float[MAX_HITS];
    private final int[] hitType = new int[MAX_HITS];
    private final int[] hitA = new int[MAX_HITS];
    private final int[] hitB = new int[MAX_HITS];
    private int hitCount;

    private final float[] cardRect = new float[4];

    // ---- состояние
    private ModuleCategory category = ModuleCategory.COMBAT;
    private ClientModule selected;

    /**
     * Открыта ли вкладка «Бот».
     *
     * <p>Бот — не категория модулей ({@link ModuleCategory}), а отдельный раздел,
     * поэтому переключение хранится своим флагом, а не значением {@link #category}:
     * иначе {@code getByCategory} получил бы несуществующую категорию, а карточка
     * модуля {@code ClickGUI} потеряла бы выбор раздела.
     */
    private boolean botView;

    /** Прокрутка колонок вкладки «Бот» — своя, чтобы не сбивать прокрутку модулей. */
    private float botLeftScroll;
    private float botLeftScrollTarget;
    private float botRightScroll;
    private float botRightScrollTarget;

    /** Поле ввода вкладки «Бот», в котором стоит курсор (-1 — нет). */
    private int botEditField = -1;
    private String botEditBuffer = "";
    /** Настройки бота менялись: файл пишется один раз при закрытии меню. */
    private boolean botDirty;

    private String search = "";
    private boolean searchFocused;

    private float listScroll;
    private float listScrollTarget;
    private float paneScroll;
    private float paneScrollTarget;
    private float listTotal;
    private float paneTotal;

    private boolean lookOpen;
    private boolean lookWasOpen;

    private int editingRow = -1;
    private String editBuffer = "";

    private int bindingRow = -1;
    private ClientModule bindingModule;

    private int popRow = -1;
    private float popX;
    private float popY;
    private float popW;

    private int dragType = HIT_NONE;
    private int dragRow = -1;
    private float dragX;
    private float dragW;

    private float mouseX;
    private float mouseY;
    private int hot;
    private int lastHot;

    private long openedAt = System.currentTimeMillis();
    private boolean closing;
    private long closingAt;
    private boolean configDirty;

    private final WorkspaceSettings.Sink sink = new WorkspaceSettings.Sink() {
        @Override
        public void hit(int type, int a, int b, float x, float y, float w, float h) {
            if (botView && type >= WorkspaceBotPanel.HIT_START && type <= WorkspaceBotPanel.HIT_TEST_ERROR) {
                float top = layout.listY + layout.s(38.0F);
                float bottom = layout.listY + layout.listH - layout.s(8.0F);
                float clippedTop = Math.max(y, top);
                float clippedBottom = Math.min(y + h, bottom);
                if (clippedBottom > clippedTop) {
                    addHit(type, a, b, x, clippedTop, w, clippedBottom - clippedTop);
                }
            } else {
                addHit(type, a, b, x, y, w, h);
            }
        }
    };

    public WorkspaceScreen(ModuleManager modules, ConfigManager config) {
        super(Component.literal("Skycore Workspace"));
        if (modules == null) {
            throw new IllegalArgumentException("WorkspaceScreen requires a ModuleManager");
        }
        this.modules = modules;
        this.config = config;
        MenuPrefs.sync();

        List<ClientModule> all = modules.getModules();
        for (int i = 0; i < all.size(); i++) {
            this.moduleIndex.put(all.get(i), i);
        }
        for (int i = 0; i < TABS.length; i++) {
            this.categoryCount[i] = modules.getByCategory(TABS[i]).size();
        }
        MenuSound.play(MenuSound.OPEN);
    }

    @Override
    protected void init() {
        // палитра — часть оформления и хранится между запусками (как в MenuScreen.init)
        MenuPalette.restore(MenuPrefs.palette);
        this.hitCount = 0;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    // ---- кадр

    @Override
    public void extractRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float partialTick) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.anim.frame();

        float open = this.anim.to(MenuAnim.OPEN, this.closing ? 0.0F : 1.0F, this.closing ? 16.0F : 9.0F);
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
            this.hitCount = 0;
            this.hot = 0;
            draw(open);
        } finally {
            BeginFrameHelper.flush();
            CurrentGuiGraphicsExtractorHelper.exit2D();
        }

        if (this.hot != 0 && this.hot != this.lastHot && System.currentTimeMillis() - this.openedAt > 260L
                && !this.closing) {
            MenuSound.play(MenuSound.HOVER, 1.0F + (this.hot % 7) * 0.015F, 1.0F);
        }
        this.lastHot = this.hot;
    }

    private void draw(float open) {
        this.layout.measure(this.width, this.height, MenuPrefs.menuScale());
        this.theme.sync();
        if (!this.botView) {
            // на вкладке бота список модулей не рисуется — фильтрация не нужна
            collectVisible();
        }

        int dim = (int) (255.0F * MenuPrefs.dim / 100.0F * open);
        if (MenuPrefs.blur) {
            BeginFrameHelper.rect(0.0F, 0.0F, this.width, this.height)
                    .color(MenuDraw.a(0x05040A, Math.max(dim, (int) (24.0F * open))))
                    .blur(this.layout.s(18.0F), Math.min(1.0F, open))
                    .hudGlow(false)
                    .draw();
        } else if (dim > 0) {
            MenuDraw.rect(0.0F, 0.0F, this.width, this.height, MenuDraw.a(0x05040A, dim));
        }

        float keep = MenuDraw.fade;
        MenuDraw.fade = keep * Math.min(1.0F, open * 1.35F);
        try {
            WorkspaceDraw.window(this.layout, this.theme);
            WorkspaceDraw.sidebar(this.layout, this.theme);
            sidebar(this.layout);
            header(this.layout);
            if (this.botView) {
                botPage(this.layout);
            } else {
                moduleList(this.layout);
                settingsPane(this.layout);
            }

            if (this.botView) this.botPanel.notification(this.layout, this.theme);
            if (this.lookOpen) {
                lookPopover(this.layout);
            }
            if (this.popRow >= 0) {
                modePopover(this.layout);
            }
            picker.render(this.layout, this.theme, this.sink, !this.closing);

            if ((this.bindingModule != null || this.bindingRow >= 0) && !this.closing) {
                bindOverlay(this.layout);
            }
        } finally {
            // общий множитель прозрачности общий на весь клиент — вернуть его обязательно
            MenuDraw.fade = keep;
        }
    }

    // ---- сайдбар

    private void sidebar(WorkspaceLayout l) {
        float x = l.sideX + l.s(12.0F);
        float width = l.sideW - l.s(24.0F);

        // логотип и подпись
        float logoSize = l.s(22.0F);
        float logoY = l.sideY + l.s(14.0F);
        MenuDraw.round(x, logoY, logoSize, logoSize, l.s(7.0F), MenuDraw.a(this.theme.accent, 34));
        MenuDraw.iconLetters(LegacyIconGlyphs.SKYCORE_LOGO, x + logoSize * 0.5F, logoY + logoSize * 0.5F,
                l.s(14.0F), this.theme.accent2);
        MenuDraw.textMid("Skycore", x + logoSize + l.s(8.0F), logoY, logoSize * 0.62F, Weight.BOLD,
                l.s(12.5F), this.theme.text);
        MenuDraw.textMid("Workspace", x + logoSize + l.s(8.0F), logoY + logoSize * 0.52F, logoSize * 0.5F,
                Weight.MEDIUM, l.s(9.0F), this.theme.dim);

        float itemY = logoY + logoSize + l.s(18.0F);
        float itemHeight = l.s(30.0F);

        for (int i = 0; i < TABS.length; i++) {
            boolean active = !this.botView && this.search.isEmpty() && TABS[i] == this.category;
            boolean hovered = inside(x, itemY, width, itemHeight);
            if (hovered) {
                this.hot = 200 + i;
            }
            float hover = this.anim.to(200 + i, hovered ? 1.0F : 0.0F, 18.0F);
            float on = this.anim.to(210 + i, active ? 1.0F : 0.0F, 16.0F);

            int background = MenuDraw.mix(MenuDraw.a(this.theme.text, hovered ? 16 : 0),
                    MenuDraw.a(this.theme.accent, this.theme.light ? 34 : 46), on);
            if ((background >>> 24) != 0) {
                MenuDraw.round(x, itemY, width, itemHeight, l.s(8.0F), background);
            }
            if (on > 0.02F) {
                MenuDraw.round(x, itemY + l.s(7.0F), Math.max(2.0F, l.s(2.5F)),
                        itemHeight - l.s(14.0F), l.s(1.5F), MenuDraw.a(this.theme.accent, (int) (230 * on)));
            }

            int textColor = MenuDraw.mix(
                    MenuDraw.mix(this.theme.muted, this.theme.text, hover),
                    this.theme.light ? this.theme.accent : this.theme.accent2, on);
            MenuDraw.iconCs(icon(TABS[i]), x + l.s(14.0F), itemY + itemHeight * 0.5F, l.s(13.0F), textColor);
            MenuDraw.textMid(L(TAB_TITLES[i], TAB_TITLES_EN[i]), x + l.s(28.0F), itemY, itemHeight,
                    Weight.SEMIBOLD, l.s(11.5F), textColor);

            String count = Integer.toString(this.categoryCount[i]);
            float countFont = l.s(9.0F);
            float badgeW = Math.max(l.s(16.0F), MenuDraw.textWidth(count, Weight.MEDIUM, countFont) + l.s(10.0F));
            MenuDraw.round(x + width - badgeW, itemY + (itemHeight - l.s(14.0F)) * 0.5F, badgeW, l.s(14.0F),
                    l.s(7.0F), MenuDraw.a(this.theme.text, this.theme.light ? 14 : 12));
            MenuDraw.textCenterMid(count, x + width - badgeW * 0.5F, itemY + (itemHeight - l.s(14.0F)) * 0.5F,
                    l.s(14.0F), Weight.MEDIUM, countFont, this.theme.dim);

            if (!this.closing) {
                addHit(HIT_CATEGORY, i, 0, x, itemY, width, itemHeight);
            }
            itemY += itemHeight + l.s(4.0F);
        }

        // ---- вкладка «Бот»: не категория модулей, поэтому отделена разделителем
        itemY += l.s(6.0F);
        WorkspaceDraw.divider(l, x, itemY, width, this.theme);
        itemY += l.s(9.0F);

        boolean botHovered = inside(x, itemY, width, itemHeight);
        if (botHovered) {
            this.hot = 260;
        }
        float botHover = this.anim.to(260, botHovered ? 1.0F : 0.0F, 18.0F);
        float botOn = this.anim.to(261, this.botView ? 1.0F : 0.0F, 16.0F);

        int botBackground = MenuDraw.mix(MenuDraw.a(this.theme.text, botHovered ? 16 : 0),
                MenuDraw.a(this.theme.accent, this.theme.light ? 34 : 46), botOn);
        if ((botBackground >>> 24) != 0) {
            MenuDraw.round(x, itemY, width, itemHeight, l.s(8.0F), botBackground);
        }
        if (botOn > 0.02F) {
            MenuDraw.round(x, itemY + l.s(7.0F), Math.max(2.0F, l.s(2.5F)),
                    itemHeight - l.s(14.0F), l.s(1.5F), MenuDraw.a(this.theme.accent, (int) (230 * botOn)));
        }

        int botTextColor = MenuDraw.mix(
                MenuDraw.mix(this.theme.muted, this.theme.text, botHover),
                this.theme.light ? this.theme.accent : this.theme.accent2, botOn);
        MenuDraw.iconCs(IconGlyphs.AUTOBUY, x + l.s(14.0F), itemY + itemHeight * 0.5F, l.s(13.0F), botTextColor);
        MenuDraw.textMid(L("Бот", "Bot"), x + l.s(28.0F), itemY, itemHeight, Weight.SEMIBOLD, l.s(11.5F),
                botTextColor);

        // бейдж: сколько ботов запущено, иначе — заданное количество
        String botBadge = BedWarsBot.INSTANCE.isRunning()
                ? BedWarsBot.INSTANCE.activeBotsText()
                : BotConfig.botCountText();
        float botBadgeFont = l.s(9.0F);
        float botBadgeW = Math.max(l.s(16.0F),
                MenuDraw.textWidth(botBadge, Weight.MEDIUM, botBadgeFont) + l.s(10.0F));
        MenuDraw.round(x + width - botBadgeW, itemY + (itemHeight - l.s(14.0F)) * 0.5F, botBadgeW, l.s(14.0F),
                l.s(7.0F), MenuDraw.a(this.theme.text, this.theme.light ? 14 : 12));
        MenuDraw.textCenterMid(botBadge, x + width - botBadgeW * 0.5F,
                itemY + (itemHeight - l.s(14.0F)) * 0.5F, l.s(14.0F), Weight.MEDIUM, botBadgeFont,
                this.theme.dim);

        if (!this.closing) {
            addHit(HIT_BOT_TAB, 0, 0, x, itemY, width, itemHeight);
        }

        // нижний блок: палитра и подсказка
        float buttonHeight = l.s(30.0F);
        float buttonY = l.sideY + l.sideH - buttonHeight - l.s(12.0F);
        boolean paletteHovered = inside(x, buttonY, width, buttonHeight);
        if (paletteHovered) {
            this.hot = 240;
        }
        float paletteHover = this.anim.to(240, paletteHovered || this.lookOpen ? 1.0F : 0.0F, 18.0F);
        MenuDraw.round(x, buttonY, width, buttonHeight, l.s(8.0F),
                MenuDraw.mix(this.theme.inset, this.theme.insetHover, paletteHover));
        MenuDraw.outline(x, buttonY, width, buttonHeight, l.s(8.0F),
                this.lookOpen ? MenuDraw.a(this.theme.accent, 150) : this.theme.border);
        MenuDraw.iconLetters(LegacyIconGlyphs.THEMES, x + l.s(14.0F), buttonY + buttonHeight * 0.5F, l.s(12.0F),
                this.theme.accent2);
        MenuDraw.textMid(L("Палитра", "Palette"), x + l.s(26.0F), buttonY, buttonHeight, Weight.SEMIBOLD,
                l.s(10.5F), this.theme.text);
        MenuDraw.textRight(MenuDraw.ellipsize(MenuPalette.base().name(), Weight.MEDIUM, l.s(9.0F),
                        width - l.s(38.0F)),
                x + width - l.s(10.0F), MenuDraw.centeredY(buttonY, buttonHeight, Weight.MEDIUM, l.s(9.0F)),
                Weight.MEDIUM, l.s(9.0F), this.theme.dim);
        if (!this.closing) {
            addHit(HIT_PALETTE_BUTTON, 0, 0, x, buttonY, width, buttonHeight);
        }

        String version = versionLabel();
        MenuDraw.textCenterMid(version.isEmpty() ? "Minecraft 26.2" : "Minecraft 26.2 · " + version,
                l.sideX + l.sideW * 0.5F,
                buttonY - l.s(18.0F), l.s(14.0F), Weight.MEDIUM, l.s(8.5F), MenuDraw.a(this.theme.dim, 170));
    }

    // ---- шапка

    private void header(WorkspaceLayout l) {
        float x = l.contentX;
        float y = l.headerY;
        float height = l.headerH;
        float rightEdge = l.contentX + l.contentW;

        // чип Esc и кнопка палитры (палитра есть и в сайдбаре — здесь для быстрого доступа)
        float escW = l.s(38.0F);
        float escX = rightEdge - escW;
        boolean escHovered = inside(escX, y, escW, height);
        MenuDraw.round(escX, y, escW, height, l.s(8.0F),
                escHovered ? this.theme.insetHover : this.theme.inset);
        MenuDraw.outline(escX, y, escW, height, l.s(8.0F), this.theme.border);
        MenuDraw.textCenterMid("ESC", escX + escW * 0.5F, y, height, Weight.SEMIBOLD, l.s(9.5F),
                escHovered ? this.theme.text : this.theme.muted);
        if (!this.closing) {
            addHit(HIT_CLOSE, 0, 0, escX, y, escW, height);
        }

        if (this.botView) {
            // поиск ищет по модулям и их настройкам, а на вкладке бота их нет:
            // вместо поля показываем подпись раздела, чтобы шапка не пустовала
            botHeaderTitle(l, x, y, height, rightEdge - escW - l.s(10.0F));
            return;
        }

        float searchW = Math.max(l.s(150.0F), l.listW - l.s(8.0F));
        float searchX = x;
        boolean searchHovered = inside(searchX, y, searchW, height);
        if (searchHovered) {
            this.hot = 250;
        }
        float focus = this.anim.to(250, this.searchFocused ? 1.0F : 0.0F, 18.0F);
        float hover = this.anim.to(251, searchHovered ? 1.0F : 0.0F, 18.0F);
        MenuDraw.round(searchX, y, searchW, height, l.s(8.0F),
                MenuDraw.mix(this.theme.inset, this.theme.insetHover, Math.max(focus, hover * 0.6F)));
        MenuDraw.outline(searchX, y, searchW, height, l.s(8.0F),
                focus > 0.02F ? MenuDraw.a(this.theme.accent, (int) (60 + 120 * focus)) : this.theme.border);
        MenuDraw.iconCs(IconGlyphs.SEARCH, searchX + l.s(14.0F), y + height * 0.5F, l.s(12.0F),
                this.searchFocused ? this.theme.accent2 : this.theme.muted);
        float font = l.s(10.5F);
        String shown = this.search;
        if (shown.isEmpty()) {
            shown = L("Поиск по модулям и настройкам", "Search modules and settings");
        }
        MenuDraw.textMid(MenuDraw.ellipsize(shown, Weight.MEDIUM, font, searchW - l.s(56.0F)),
                searchX + l.s(26.0F), y, height, Weight.MEDIUM, font,
                this.search.isEmpty() ? this.theme.dim : this.theme.text);
        if (this.searchFocused) {
            float caretX = searchX + l.s(26.0F)
                    + MenuDraw.textWidth(this.search, Weight.MEDIUM, font) + l.s(1.0F);
            MenuDraw.rect(caretX, y + l.s(10.0F), Math.max(1.0F, l.s(1.0F)), height - l.s(20.0F),
                    this.theme.accent);
        }
        if (!this.search.isEmpty()) {
            MenuDraw.textRight(Integer.toString(this.visibleCount), searchX + searchW - l.s(10.0F),
                    y + (height - l.s(10.0F)) * 0.5F, Weight.MEDIUM, l.s(9.0F), this.theme.dim);
        }
        if (!this.closing) {
            addHit(HIT_SEARCH, 0, 0, searchX, y, searchW, height);
        }
    }

    // ---- вкладка «Бот»

    /**
     * Подпись раздела в шапке вместо поиска.
     *
     * <p>Поиск в Workspace ищет по модулям и их настройкам; на вкладке бота модулей
     * нет, поэтому поле ввода здесь выглядело бы как неработающий элемент — а мёртвых
     * контролов в меню клиента быть не должно (см. {@code docs/GUI-SELECTION.md}).
     */
    private void botHeaderTitle(WorkspaceLayout l, float x, float y, float height, float rightLimit) {
        String title = L("Бот BedWars", "BedWars bot");
        MenuDraw.textMid(title, x, y, height, Weight.BOLD, l.s(12.5F), this.theme.text);
        String hint = L("визуальная часть, логика — заглушки", "visual only, logic is stubbed");
        float hintX = x + MenuDraw.textWidth(title, Weight.BOLD, l.s(12.5F)) + l.s(10.0F);
        MenuDraw.textMid(MenuDraw.ellipsize(hint, Weight.MEDIUM, l.s(9.0F),
                        Math.max(l.s(40.0F), rightLimit - hintX)),
                hintX, y, height, Weight.MEDIUM, l.s(9.0F), this.theme.dim);
    }

    /**
     * Содержимое вкладки «Бот».
     *
     * <p>Геометрия та же, что у списка модулей и панели настроек: левая колонка
     * ({@code listX/listW}) и правая ({@code paneX/paneW}). Поэтому вкладка не
     * «прыгает» относительно остальных разделов и прокрутка колесом работает по тем
     * же областям, что и раньше.
     */
    private void botPage(WorkspaceLayout l) {
        this.botLeftScrollTarget = Math.max(0, this.botLeftScrollTarget);
        this.botRightScrollTarget = Math.max(0, this.botRightScrollTarget);
        // плавная прокрутка — тот же подход, что у списка модулей и панели настроек
        this.botLeftScroll += (this.botLeftScrollTarget - this.botLeftScroll) * 0.35F;
        if (Math.abs(this.botLeftScrollTarget - this.botLeftScroll) < 0.5F) {
            this.botLeftScroll = this.botLeftScrollTarget;
        }
        this.botRightScroll += (this.botRightScrollTarget - this.botRightScroll) * 0.35F;
        if (Math.abs(this.botRightScrollTarget - this.botRightScroll) < 0.5F) {
            this.botRightScroll = this.botRightScrollTarget;
        }

        boolean live = !this.closing && !this.lookOpen && this.popRow < 0 && !this.picker.isOpen();
        this.botPanel.render(l, this.theme, this.anim, this.mouseX, this.mouseY, live,
                this.botEditField, this.botEditBuffer, this.botLeftScroll, this.botRightScroll, this.sink);

        // итоговые высоты панель считает при отрисовке — ограничиваем прокрутку по ним
        float leftView = l.listH - l.s(38.0F);
        float rightView = l.paneH - l.s(46.0F);
        this.botLeftScrollTarget = clampScroll(this.botLeftScrollTarget, this.botPanel.leftTotal(), leftView);
        this.botRightScrollTarget = clampScroll(this.botRightScrollTarget, this.botPanel.rightTotal(), rightView);
        this.botLeftScroll = clampScroll(this.botLeftScroll, this.botPanel.leftTotal(), leftView);
        this.botRightScroll = clampScroll(this.botRightScroll, this.botPanel.rightTotal(), rightView);
    }

    /** Действия вкладки «Бот». Возвращает {@code true}, если тип относится к боту. */
    private boolean runBotHit(int type, int a, int b) {
        if (BedWarsBot.INSTANCE.isRunning() && (type == WorkspaceBotPanel.HIT_STRING
                || type == WorkspaceBotPanel.HIT_SLIDER)) return true;
        switch (type) {
            case WorkspaceBotPanel.HIT_APPEARANCE -> {
                if (a < 0 || a >= BotScript.MAX_BOTS) return true;
                if (b == 0) BotAppearance.cycleColor(a);
                if (b == 1) BotAppearance.toggleCompact(a);
                if (b == 2) BotAppearance.toggleNotifications(a);
                this.botDirty = true;
                MenuSound.play(MenuSound.SELECT);
            }
            case WorkspaceBotPanel.HIT_TEST_ERROR -> BedWarsBot.INSTANCE.previewError(a);
            case WorkspaceBotPanel.HIT_START -> {
                BedWarsBot.INSTANCE.start();
                MenuSound.play(MenuSound.TOGGLE_ON);
            }
            case WorkspaceBotPanel.HIT_STOP -> {
                BedWarsBot.INSTANCE.stop();
                MenuSound.play(MenuSound.TOGGLE_OFF);
            }
            case WorkspaceBotPanel.HIT_STEP -> {
                BedWarsBot.INSTANCE.previewStep();
                MenuSound.play(MenuSound.TICK);
            }
            case WorkspaceBotPanel.HIT_CLEAR_LOG -> {
                BedWarsBot.INSTANCE.clearLog();
                MenuSound.play(MenuSound.RESET, 1.0F, 0.5F);
            }
            case WorkspaceBotPanel.HIT_STRING -> {
                if (this.botEditField != a) {
                    this.botEditField = a;
                    this.botEditBuffer = botFieldValue(a);
                }
                MenuSound.play(MenuSound.SELECT);
            }
            case WorkspaceBotPanel.HIT_SLIDER -> applyBotCount(this.mouseX);
            case WorkspaceBotPanel.HIT_VOTE -> {
                int cell = b == 1 ? BotScript.VOTE_CELL_B : BotScript.VOTE_CELL_A;
                if (BotConfig.voteCell(a) != cell) {
                    BotConfig.setVoteCell(a, cell);
                    this.botDirty = true;
                    MenuSound.play(MenuSound.SELECT);
                }
            }
            default -> {
                return false;
            }
        }
        return true;
    }

    /** Текущее значение поля ввода вкладки «Бот». */
    private static String botFieldValue(int field) {
        return switch (field) {
            case WorkspaceBotPanel.FIELD_PREFIX -> BotConfig.nickPrefix;
            case WorkspaceBotPanel.FIELD_PASSWORD -> BotConfig.password;
            case WorkspaceBotPanel.FIELD_OWNER -> BotConfig.ownerNick;
            default -> "";
        };
    }

    /** Записывает буфер ввода в настройки бота. */
    private void commitBotString() {
        if (this.botEditField < 0) {
            return;
        }
        switch (this.botEditField) {
            case WorkspaceBotPanel.FIELD_PREFIX -> BotConfig.setNickPrefix(this.botEditBuffer);
            case WorkspaceBotPanel.FIELD_PASSWORD -> BotConfig.setPassword(this.botEditBuffer);
            case WorkspaceBotPanel.FIELD_OWNER -> BotConfig.setOwnerNick(this.botEditBuffer);
            default -> {
            }
        }
        this.botDirty = true;
        this.botEditField = -1;
        this.botEditBuffer = "";
    }

    /** Число ботов по положению курсора на ползунке. */
    private void applyBotCount(double mouseX) {
        WorkspaceLayout l = this.layout;
        float x = l.paneX + l.s(14.0F);
        float width = l.paneW - l.s(28.0F);
        if (width <= 1.0F) {
            return;
        }
        float ratio = (float) ((mouseX - x) / width);
        ratio = Math.max(0.0F, Math.min(1.0F, ratio));
        int span = BotScript.MAX_BOTS - BotScript.MIN_BOTS;
        int count = BotScript.MIN_BOTS + Math.round(ratio * span);
        if (count != BotConfig.nicknameCount()) {
            BotConfig.setBotCount(count);
            this.botDirty = true;
            MenuSound.play(MenuSound.TICK, 1.0F, 0.6F);
        }
    }

    // ---- список модулей

    private void moduleList(WorkspaceLayout l) {
        float x = l.listX;
        float y = l.listY;
        float width = l.listW;
        float height = l.listH;
        float radius = l.s(11.0F);

        MenuDraw.round(x, y, width, height, radius, this.theme.card);
        MenuDraw.outline(x, y, width, height, radius, this.theme.border);

        // шапка списка
        float headHeight = l.s(38.0F);
        float pad = l.s(14.0F);
        String title = this.search.isEmpty()
                ? L(TAB_TITLES[this.category.ordinal()], TAB_TITLES_EN[this.category.ordinal()])
                : L("Результаты поиска", "Search results");
        MenuDraw.textMid(title, x + pad, y, headHeight, Weight.BOLD, l.s(12.5F), this.theme.text);
        String subtitle = this.search.isEmpty()
                ? L("Модули раздела", "Category modules")
                : "\"" + MenuDraw.ellipsize(this.search, Weight.MEDIUM, l.s(9.0F), width * 0.4F) + "\"";
        MenuDraw.textMid(subtitle,
                x + pad + MenuDraw.textWidth(title, Weight.BOLD, l.s(12.5F)) + l.s(8.0F), y, headHeight,
                Weight.MEDIUM, l.s(9.0F), this.theme.dim);

        // счётчик включённых
        int enabled = 0;
        for (int i = 0; i < this.visibleCount; i++) {
            if (this.visible[i].isEnabled()) {
                enabled++;
            }
        }
        MenuDraw.textRight(enabled + "/" + this.visibleCount, x + width - pad, y + (headHeight - l.s(10.0F)) * 0.5F,
                Weight.MEDIUM, l.s(9.5F), this.theme.dim);

        WorkspaceDraw.divider(l, x + pad, y + headHeight - l.line(), width - pad * 2.0F, this.theme);

        float viewTop = y + headHeight;
        float viewBottom = y + height - l.s(6.0F);
        float viewHeight = Math.max(0.0F, viewBottom - viewTop);
        this.listTotal = l.listContentHeight(this.visibleCount);

        // плавная прокрутка
        this.listScroll += (this.listScrollTarget - this.listScroll) * 0.35F;
        if (Math.abs(this.listScrollTarget - this.listScroll) < 0.5F) {
            this.listScroll = this.listScrollTarget;
        }

        if (this.visibleCount == 0) {
            this.listTotal = 0.0F;
            float centreX = x + width * 0.5F;
            float centreY = viewTop + viewHeight * 0.42F;
            MenuDraw.iconCs(IconGlyphs.SEARCH, centreX, centreY, l.s(22.0F), this.theme.dim);
            MenuDraw.textCenter(L("Ничего не найдено", "Nothing found"), centreX, centreY + l.s(22.0F),
                    Weight.SEMIBOLD, l.s(11.5F), this.theme.text);
            MenuDraw.textCenter(L("Попробуйте другое слово", "Try another word"), centreX,
                    centreY + l.s(38.0F), Weight.MEDIUM, l.s(9.5F), this.theme.dim);
            return;
        }

        MenuDraw.scissor(x + l.s(1.0F), viewTop, width - l.s(2.0F), viewHeight);
        try {
            for (int i = 0; i < this.visibleCount; i++) {
                l.cardAt(i, this.listScroll, this.cardRect);
                float cardY = this.cardRect[1];
                if (cardY + this.cardRect[3] < viewTop - l.s(2.0F) || cardY > viewBottom + l.s(2.0F)) {
                    continue;
                }
                card(this.visible[i], i, this.cardRect[0], cardY, this.cardRect[2], this.cardRect[3],
                        viewTop, viewBottom);
            }
        } finally {
            MenuDraw.scissorEnd();
        }

        WorkspaceDraw.scrollbar(l, this.theme, x + width - l.s(6.0F), viewTop, viewHeight, this.listTotal,
                this.listScroll, this.mouseX, this.mouseY);
        if (!this.closing && this.listTotal > viewHeight) {
            addHit(HIT_PANE_SCROLL, 0, 0, x + width - l.s(10.0F), viewTop, l.s(10.0F), viewHeight);
        }
    }

    private void card(ClientModule module, int index, float x, float y, float width, float height,
                      float viewTop, float viewBottom) {
        boolean hovered = !this.closing && this.mouseY >= viewTop && this.mouseY <= viewBottom
                && inside(x, y, width, height);
        if (hovered) {
            this.hot = 400 + index;
        }
        int mi = indexOf(module);
        boolean selectedModule = module == this.selected;
        int animKey = CARD_ANIM_BASE + mi * 4;
        float hover = this.anim.to(animKey, hovered ? 1.0F : 0.0F, 20.0F);
        float selectedAnim = this.anim.to(animKey + 1, selectedModule ? 1.0F : 0.0F, 20.0F);
        float on = this.anim.to(animKey + 2, module.isEnabled() ? 1.0F : 0.0F, 18.0F);

        int background = MenuDraw.mix(this.theme.card, this.theme.cardHover, hover);
        background = MenuDraw.mix(background, this.theme.cardActive, selectedAnim);
        MenuDraw.round(x, y, width, height, this.layout.s(9.0F), background);
        int borderColor = MenuDraw.mix(this.theme.border, this.theme.borderStrong, Math.max(selectedAnim, hover * 0.4F));
        MenuDraw.outline(x, y, width, height, this.layout.s(9.0F), borderColor);
        if (on > 0.02F) {
            MenuDraw.round(x, y + this.layout.s(9.0F), Math.max(2.0F, this.layout.s(2.5F)),
                    height - this.layout.s(18.0F), this.layout.s(1.5F),
                    MenuDraw.a(this.theme.accent, (int) (220 * on)));
        }

        float pad = this.layout.s(11.0F);
        float toggleW = this.layout.s(28.0F);
        float toggleH = this.layout.s(15.0F);
        float toggleX = x + width - pad - toggleW;
        float toggleY = y + pad;

        String name = SelectAndSaveHelper.text(module.getName());
        float nameFont = this.layout.s(11.5F);
        float nameWidth = toggleX - this.layout.s(8.0F) - (x + pad);
        drawHighlightedName(name, x + pad, y + pad - this.layout.s(1.0F), nameWidth, nameFont,
                MenuDraw.mix(this.theme.muted, this.theme.text, Math.max(on, Math.max(hover, selectedAnim))));

        MenuDraw.text(MenuDraw.ellipsize(safe(SelectAndSaveHelper.text(module.getDescription())),
                        Weight.MEDIUM, this.layout.s(8.5F), nameWidth),
                x + pad, y + pad + this.layout.s(13.0F), Weight.MEDIUM, this.layout.s(8.5F), this.theme.dim);

        WorkspaceDraw.toggle(this.layout, this.theme, toggleX, toggleY, toggleW, toggleH, on,
                hovered ? 1.0F : 0.0F);

        // нижняя строка: клавиша и число настроек
        float chipY = y + height - this.layout.s(17.0F);
        boolean hasSettings = hasVisibleSettings(module);
        String keyText = module.getKeyBind() < 0 ? "—" : KeyBinds.name(module.getKeyBind());
        WorkspaceDraw.keyChip(this.layout, this.theme, keyText, x + width - pad, chipY,
                this.layout.s(14.0F), this.bindingModule == module, hovered);
        if (hasSettings) {
            settingsBadge(x + pad, chipY + this.layout.s(2.0F), module.getSettings().size());
        }

        if (!this.closing) {
            addHit(HIT_CARD, index, 0, x, y, width, height);
            addHit(HIT_CARD_TOGGLE, index, 0, toggleX - this.layout.s(4.0F), toggleY - this.layout.s(3.0F),
                    toggleW + this.layout.s(8.0F), toggleH + this.layout.s(6.0F));
            addHit(HIT_CARD_KEY, index, 0, x + width - pad - this.layout.s(34.0F), chipY - this.layout.s(2.0F),
                    this.layout.s(34.0F), this.layout.s(16.0F));
        }
    }

    /** Значок «у модуля есть настройки». */
    private void settingsBadge(float x, float y, int settings) {
        float font = this.layout.s(8.5F);
        String text = Integer.toString(settings);
        float width = Math.max(this.layout.s(14.0F),
                MenuDraw.textWidth(text, Weight.MEDIUM, font) + this.layout.s(10.0F));
        float height = this.layout.s(12.0F);
        MenuDraw.round(x, y, width, height, height * 0.5F, MenuDraw.a(this.theme.text, this.theme.light ? 12 : 10));
        MenuDraw.textCenterMid(text, x + width * 0.5F, y, height, Weight.MEDIUM, font, this.theme.dim);
    }

    /** Имя модуля с подсветкой совпадения поиска. */
    private void drawHighlightedName(String name, float x, float y, float maxWidth, float font, int color) {
        if (this.search.isEmpty()) {
            MenuDraw.text(MenuDraw.ellipsize(name, Weight.SEMIBOLD, font, maxWidth), x, y, Weight.SEMIBOLD,
                    font, color);
            return;
        }
        int match = indexOfIgnoreCase(name, this.search);
        if (match < 0) {
            MenuDraw.text(MenuDraw.ellipsize(name, Weight.SEMIBOLD, font, maxWidth), x, y, Weight.SEMIBOLD,
                    font, color);
            return;
        }
        int end = Math.min(name.length(), match + this.search.length());
        String head = name.substring(0, match);
        String hit = name.substring(match, end);
        String tail = name.substring(end);
        float headWidth = MenuDraw.textWidth(head, Weight.SEMIBOLD, font);
        float hitWidth = MenuDraw.textWidth(hit, Weight.SEMIBOLD, font);
        MenuDraw.text(head, x, y, Weight.SEMIBOLD, font, color);
        MenuDraw.text(hit, x + headWidth, y, Weight.SEMIBOLD, font, this.theme.accent2);
        // подчёркивание идёт под базовой линией текста, а не под его верхом
        MenuDraw.rect(x + headWidth, y + font * 1.05F, Math.max(1.0F, hitWidth),
                Math.max(1.0F, this.layout.s(1.0F)), MenuDraw.a(this.theme.accent2, 90));
        MenuDraw.text(MenuDraw.ellipsize(tail, Weight.SEMIBOLD, font, maxWidth - headWidth - hitWidth),
                x + headWidth + hitWidth, y, Weight.SEMIBOLD, font, color);
    }

    // ---- панель настроек

    private void settingsPane(WorkspaceLayout l) {
        float x = l.paneX;
        float y = l.paneY;
        float width = l.paneW;
        float height = l.paneH;
        float radius = l.s(11.0F);
        float pad = l.s(14.0F);

        MenuDraw.round(x, y, width, height, radius, this.theme.card);
        MenuDraw.outline(x, y, width, height, radius, this.theme.border);

        if (this.selected == null) {
            MenuDraw.iconCs(IconGlyphs.MISC, x + width * 0.5F, y + height * 0.42F, l.s(22.0F), this.theme.dim);
            MenuDraw.textCenter(L("Выберите модуль", "Select a module"), x + width * 0.5F,
                    y + height * 0.42F + l.s(22.0F), Weight.SEMIBOLD, l.s(11.5F), this.theme.text);
            MenuDraw.textCenter(L("Настройки появятся здесь", "Its settings appear here"), x + width * 0.5F,
                    y + height * 0.42F + l.s(38.0F), Weight.MEDIUM, l.s(9.5F), this.theme.dim);
            return;
        }

        ClientModule module = this.selected;
        float headHeight = l.s(64.0F);
        String name = SelectAndSaveHelper.text(module.getName());

        MenuDraw.textMid(MenuDraw.ellipsize(name, Weight.BOLD, l.s(12.5F), width - pad * 2.0F - l.s(40.0F)),
                x + pad, y + l.s(10.0F), l.s(20.0F), Weight.BOLD, l.s(12.5F), this.theme.text);

        float descFont = l.s(9.0F);
        String description = safe(SelectAndSaveHelper.text(module.getDescription()));
        MenuDraw.text(MenuDraw.ellipsize(description, Weight.MEDIUM, descFont, width - pad * 2.0F),
                x + pad, y + l.s(32.0F), Weight.MEDIUM, descFont, this.theme.dim);
        MenuDraw.text(MenuDraw.ellipsize(L("Клик колесом по карточке — назначить клавишу",
                        "Middle click a card to bind a key"), Weight.MEDIUM, descFont,
                        width - pad * 2.0F),
                x + pad, y + l.s(44.0F), Weight.MEDIUM, descFont, MenuDraw.a(this.theme.dim, 150));

        // тумблер модуля
        float toggleW = l.s(32.0F);
        float toggleH = l.s(17.0F);
        float toggleX = x + width - pad - toggleW;
        float toggleY = y + l.s(12.0F);
        float on = this.anim.to(700, module.isEnabled() ? 1.0F : 0.0F, 18.0F);
        boolean toggleHovered = inside(toggleX - l.s(4.0F), toggleY - l.s(4.0F), toggleW + l.s(8.0F),
                toggleH + l.s(8.0F));
        WorkspaceDraw.toggle(l, this.theme, toggleX, toggleY, toggleW, toggleH, on, toggleHovered ? 1.0F : 0.0F);
        if (!this.closing) {
            addHit(HIT_PANE_TOGGLE, 0, 0, toggleX - l.s(4.0F), toggleY - l.s(4.0F), toggleW + l.s(8.0F),
                    toggleH + l.s(8.0F));
        }

        WorkspaceDraw.divider(l, x + pad, y + headHeight - l.line(), width - pad * 2.0F, this.theme);

        List<Setting<?>> settings = module.getSettings();
        float viewTop = y + headHeight;
        float viewBottom = y + height - l.s(8.0F);
        float contentX = x + pad;
        float contentWidth = width - pad * 2.0F;
        this.paneTotal = WorkspaceSettings.contentHeight(l, settings, contentWidth);

        this.paneScroll += (this.paneScrollTarget - this.paneScroll) * 0.35F;
        if (Math.abs(this.paneScrollTarget - this.paneScroll) < 0.5F) {
            this.paneScroll = this.paneScrollTarget;
        }

        if (settings.isEmpty()) {
            MenuDraw.textCenter(L("У модуля нет настроек", "This module has no settings"),
                    x + width * 0.5F, viewTop + (viewBottom - viewTop) * 0.4F, Weight.MEDIUM, l.s(10.0F),
                    this.theme.dim);
            return;
        }

        boolean live = !this.closing && !this.lookOpen && this.popRow < 0 && !picker.isOpen()
                && this.bindingModule == null;
        MenuDraw.scissor(x + l.s(1.0F), viewTop, width - l.s(2.0F), Math.max(0.0F, viewBottom - viewTop));
        try {
            WorkspaceSettings.render(l, this.theme, this.anim, settings, contentX,
                    viewTop + l.s(6.0F), contentWidth, this.paneScroll, viewTop, viewBottom,
                    this.mouseX, this.mouseY, this.editingRow, this.editBuffer, this.bindingRow, live, this.sink);
        } finally {
            MenuDraw.scissorEnd();
        }

        WorkspaceDraw.scrollbar(l, this.theme, x + width - l.s(6.0F), viewTop, viewBottom - viewTop,
                this.paneTotal, this.paneScroll, this.mouseX, this.mouseY);
    }

    // ---- поповеры

    private void lookPopover(WorkspaceLayout l) {
        float width = l.s(258.0F);
        float height = l.s(214.0F);
        float x = l.sideX + l.sideW - l.s(6.0F);
        float y = Math.min(l.sideY + l.sideH - l.s(52.0F) - height, this.height - height - l.s(8.0F));
        y = Math.max(l.s(8.0F), y);

        MenuDraw.shadow(x, y + l.s(8.0F), width, height, l.s(12.0F), l.s(24.0F), this.theme.shadow);
        MenuDraw.round(x, y, width, height, l.s(12.0F),
                this.theme.light ? 0xFFFFFFFF : MenuDraw.a(this.theme.window, 252));
        MenuDraw.outline(x, y, width, height, l.s(12.0F), this.theme.border);

        float pad = l.s(12.0F);
        MenuDraw.textMid(L("Оформление", "Appearance"), x + pad, y + l.s(6.0F), l.s(20.0F), Weight.SEMIBOLD,
                l.s(11.0F), this.theme.text);
        MenuDraw.textRight(L("закрыть", "close"), x + width - pad, y + l.s(11.0F), Weight.MEDIUM, l.s(8.5F),
                this.theme.dim);
        if (!this.closing) {
            addHit(HIT_LOOK_CLOSE, 0, 0, x + width - l.s(44.0F), y + l.s(6.0F), l.s(38.0F), l.s(18.0F));
        }

        // сетка палитр: 4 колонки
        int columns = 4;
        float chipGap = l.s(5.0F);
        float chipWidth = (width - pad * 2.0F - chipGap * (columns - 1)) / columns;
        float chipHeight = l.s(20.0F);
        float gridY = y + l.s(30.0F);
        for (int i = 0; i < MenuPalette.PALETTES.length; i++) {
            int column = i % columns;
            int row = i / columns;
            float cx = x + pad + column * (chipWidth + chipGap);
            float cy = gridY + row * (chipHeight + chipGap);
            if (cy + chipHeight > y + height - l.s(52.0F)) {
                break;
            }
            boolean active = i == Math.floorMod(MenuPalette.palette, MenuPalette.PALETTES.length);
            boolean hovered = inside(cx, cy, chipWidth, chipHeight);
            MenuPalette.Palette palette = MenuPalette.PALETTES[i];
            MenuDraw.round(cx, cy, chipWidth, chipHeight, l.s(6.0F),
                    active ? MenuDraw.a(this.theme.accent, this.theme.light ? 34 : 46)
                            : (hovered ? this.theme.insetHover : this.theme.inset));
            MenuDraw.outline(cx, cy, chipWidth, chipHeight, l.s(6.0F),
                    active ? MenuDraw.a(this.theme.accent, 160) : this.theme.border);
            float dot = l.s(9.0F);
            MenuDraw.round(cx + l.s(5.0F), cy + (chipHeight - dot) * 0.5F, dot, dot, dot * 0.5F,
                    palette.a() | 0xFF000000);
            MenuDraw.round(cx + l.s(5.0F) + dot * 0.6F, cy + (chipHeight - dot) * 0.5F, dot * 0.6F, dot,
                    0.0F, palette.b() | 0xFF000000);
            MenuDraw.textMid(MenuDraw.ellipsize(palette.name(), Weight.MEDIUM, l.s(8.5F),
                            chipWidth - dot - l.s(14.0F)),
                    cx + dot + l.s(10.0F), cy, chipHeight, Weight.MEDIUM, l.s(8.5F), this.theme.text);
            if (!this.closing) {
                addHit(HIT_LOOK_PALETTE, i, 0, cx, cy, chipWidth, chipHeight);
            }
        }

        // переключатели
        float rowY = y + height - l.s(46.0F);
        float toggleW = l.s(26.0F);
        float toggleH = l.s(14.0F);
        preference(l, x + pad, rowY, width - pad * 2.0F, L("Размытие", "Blur"), MenuPrefs.blur,
                HIT_LOOK_BLUR, toggleW, toggleH);
        preference(l, x + pad, rowY + l.s(16.0F), width - pad * 2.0F, L("Градиенты", "Gradients"),
                MenuPrefs.gradient, HIT_LOOK_GRADIENT, toggleW, toggleH);
        // масштаб меню
        float scaleY = rowY + l.s(32.0F);
        float scaleFont = l.s(9.0F);
        MenuDraw.textMid(L("Размер", "Scale"), x + pad, scaleY, l.s(12.0F), Weight.MEDIUM, scaleFont,
                this.theme.muted);
        float sliderX = x + pad + l.s(58.0F);
        float sliderW = width - pad * 2.0F - l.s(58.0F);
        float trackH = Math.max(2.0F, l.s(2.5F));
        MenuDraw.round(sliderX, scaleY + l.s(6.0F) - trackH * 0.5F, sliderW, trackH, trackH * 0.5F,
                this.theme.track);
        float ratio = (MenuPrefs.scale - 70.0F) / 60.0F;
        MenuDraw.round(sliderX, scaleY + l.s(6.0F) - trackH * 0.5F, Math.max(trackH, sliderW * ratio), trackH,
                trackH * 0.5F, this.theme.accent);
        MenuDraw.circle(sliderX + sliderW * ratio, scaleY + l.s(6.0F), l.s(4.0F), this.theme.knob);
        MenuDraw.textRight(Math.round(MenuPrefs.scale) + "%", x + width - pad, scaleY, Weight.MEDIUM,
                scaleFont, this.theme.muted);
        if (!this.closing) {
            addHit(HIT_LOOK_SCALE, 0, 0, sliderX, scaleY - l.s(4.0F), sliderW, l.s(16.0F));
        }
    }

    private void preference(WorkspaceLayout l, float x, float y, float width, String label, boolean value,
                            int hit, float toggleW, float toggleH) {
        float font = l.s(9.0F);
        MenuDraw.textMid(label, x, y, l.s(12.0F), Weight.MEDIUM, font, this.theme.muted);
        float toggleX = x + width - toggleW;
        float toggleY = y + (l.s(12.0F) - toggleH) * 0.5F;
        float on = this.anim.to(760 + hit, value ? 1.0F : 0.0F, 18.0F);
        WorkspaceDraw.toggle(l, this.theme, toggleX, toggleY, toggleW, toggleH, on, 0.0F);
        if (!this.closing) {
            addHit(hit, 0, 0, x, y - l.s(2.0F), width, l.s(16.0F));
        }
    }

    private void modePopover(WorkspaceLayout l) {
        if (this.selected == null) {
            this.popRow = -1;
            return;
        }
        Setting<?> setting = settingForRow(this.popRow);
        if (!(setting instanceof ModeSetting mode)) {
            this.popRow = -1;
            return;
        }
        List<String> modes = mode.getModes();
        float rowHeight = l.s(22.0F);
        float width = this.popW;
        float height = modes.size() * rowHeight + l.s(8.0F);
        float x = this.popX;
        float y = Math.min(this.popY + l.s(22.0F), l.py + l.ph - height - l.s(8.0F));

        MenuDraw.shadow(x, y + l.s(6.0F), width, height, l.s(9.0F), l.s(18.0F), this.theme.shadow);
        MenuDraw.round(x, y, width, height, l.s(9.0F),
                this.theme.light ? 0xFFFFFFFF : MenuDraw.a(this.theme.window, 253));
        MenuDraw.outline(x, y, width, height, l.s(9.0F), this.theme.border);
        for (int i = 0; i < modes.size(); i++) {
            String label = SelectAndSaveHelper.text(modes.get(i));
            float rowY = y + l.s(4.0F) + i * rowHeight;
            boolean active = mode.is(modes.get(i));
            boolean hovered = inside(x + l.s(4.0F), rowY, width - l.s(8.0F), rowHeight);
            if (hovered || active) {
                MenuDraw.round(x + l.s(4.0F), rowY, width - l.s(8.0F), rowHeight, l.s(6.0F),
                        active ? MenuDraw.a(this.theme.accent, this.theme.light ? 30 : 44) : this.theme.insetHover);
            }
            MenuDraw.textMid(MenuDraw.ellipsize(label, Weight.MEDIUM, l.s(10.0F), width - l.s(24.0F)),
                    x + l.s(10.0F), rowY, rowHeight, Weight.MEDIUM, l.s(10.0F),
                    active ? (this.theme.light ? this.theme.accent : this.theme.accent2) : this.theme.text);
            if (!this.closing) {
                addHit(HIT_MODE_ITEM, i, 0, x + l.s(4.0F), rowY, width - l.s(8.0F), rowHeight);
            }
        }
    }

    private void bindOverlay(WorkspaceLayout l) {
        float width = l.s(240.0F);
        float height = l.s(58.0F);
        float x = (this.width - width) * 0.5F;
        float y = l.py + l.ph * 0.5F - height * 0.5F;
        MenuDraw.shadow(x, y + l.s(8.0F), width, height, l.s(12.0F), l.s(24.0F), this.theme.shadow);
        MenuDraw.round(x, y, width, height, l.s(12.0F),
                this.theme.light ? 0xFFFFFFFF : MenuDraw.a(this.theme.window, 254));
        MenuDraw.outline(x, y, width, height, l.s(12.0F), MenuDraw.a(this.theme.accent, 160));
        MenuDraw.textCenter(L("Нажмите клавишу", "Press a key"), x + width * 0.5F, y + l.s(14.0F),
                Weight.SEMIBOLD, l.s(11.5F), this.theme.text);
        MenuDraw.textCenter(L("Esc или ПКМ — сбросить", "Esc or RMB to clear"), x + width * 0.5F,
                y + l.s(32.0F), Weight.MEDIUM, l.s(9.0F), this.theme.dim);
    }

    // ---- ввод

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        this.mouseX = (float) event.x();
        this.mouseY = (float) event.y();
        int button = event.button();

        // перехват клавиши забирает любой клик
        if (this.bindingModule != null || this.bindingRow >= 0) {
            if (button == 1) {
                applyBinding(-1);
            } else {
                applyBinding(KeyBinds.MOUSE_BASE + Math.max(0, button));
            }
            return true;
        }

        if (this.closing) {
            return true;
        }

        int index = findHit(event.x(), event.y());

        // сперва поповеры: клик внутри них не должен «протекать» в меню
        if (this.picker.isOpen()) {
            if (index >= 0 && isPickerHit(this.hitType[index])) {
                int type = this.hitType[index];
                if (type == WorkspaceColorPicker.HIT_SV || type == WorkspaceColorPicker.HIT_HUE
                        || type == WorkspaceColorPicker.HIT_ALPHA) {
                    this.dragType = type;
                }
                runHit(type, this.hitA[index], this.hitB[index]);
                return true;
            }
            if (!this.picker.contains(event.x(), event.y())) {
                this.picker.close();
                MenuSound.play(MenuSound.RESET, 1.0F, 0.4F);
                return true;
            }
            return true;
        }

        if (this.popRow >= 0) {
            if (index >= 0 && this.hitType[index] == HIT_MODE_ITEM) {
                runHit(HIT_MODE_ITEM, this.hitA[index], this.hitB[index]);
                return true;
            }
            this.popRow = -1;
            MenuSound.play(MenuSound.DROP, 1.0F, 0.5F);
        }

        if (this.lookOpen) {
            if (index >= 0 && isLookHit(this.hitType[index])) {
                runHit(this.hitType[index], this.hitA[index], this.hitB[index]);
                return true;
            }
            this.lookOpen = false;
            MenuSound.play(MenuSound.DROP, 1.0F, 0.5F);
            return true;
        }

        if (index < 0) {
            this.searchFocused = false;
            commitString();
            commitBotString();
            return false;
        }

        this.searchFocused = false;
        int type = this.hitType[index];
        int a = this.hitA[index];
        int b = this.hitB[index];

        // клик по любому элементу, кроме поля ввода бота, завершает ввод в этом поле
        if (this.botEditField >= 0 && !(type == WorkspaceBotPanel.HIT_STRING && a == this.botEditField)) {
            commitBotString();
        }

        if (button == 1) {
            // правая кнопка: переключить модуль/сбросить бинд, настройки не трогаем
            if (type == HIT_CARD) {
                // через runHit: он проверяет, что индекс ещё в пределах видимого списка
                runHit(HIT_CARD_TOGGLE, a, 0);
                return true;
            }
            if (type == WorkspaceSettings.HIT_BIND) {
                Setting<?> setting = settingForRow(a);
                if (setting instanceof KeyBindSetting key) {
                    key.set(-1);
                    markDirty();
                    MenuSound.play(MenuSound.RESET);
                }
                return true;
            }
            return true;
        }
        if (button == 2) {
            if (type == HIT_CARD) {
                runHit(HIT_CARD_KEY, a, 0);
                return true;
            }
            return true;
        }
        if (button != 0) {
            return true;
        }

        if (type == WorkspaceSettings.HIT_SLIDER) {
            this.dragType = type;
            this.dragRow = a;
            this.dragX = this.hx[index];
            this.dragW = this.hw[index];
            MenuSound.play(MenuSound.GRAB);
            runHit(type, a, b);
            return true;
        }
        if (type == WorkspaceBotPanel.HIT_SLIDER) {
            // ползунок числа ботов: тип свой, поэтому не конфликтует с ползунком настройки
            this.dragType = type;
            MenuSound.play(MenuSound.GRAB);
            runHit(type, a, b);
            return true;
        }
        if (type == WorkspaceSettings.HIT_MULTI || type == WorkspaceSettings.HIT_ACTION) {
            runHit(type, a, b);
            return true;
        }
        runHit(type, a, b);
        return true;
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (this.dragType != HIT_NONE) {
            int released = this.dragType;
            this.dragType = HIT_NONE;
            this.dragRow = -1;
            if (released == WorkspaceBotPanel.HIT_SLIDER) {
                // ползунок бота пишет в skycore/bot.json: флаг «грязно» уже выставлен
                // в applyBotCount, а общий конфиг модулей трогать не нужно
                saveBotConfig();
            } else {
                markDirty();
            }
            MenuSound.play(MenuSound.DROP, 1.0F, 0.5F);
            return true;
        }
        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        this.mouseX = (float) event.x();
        this.mouseY = (float) event.y();
        if (this.dragType == WorkspaceSettings.HIT_SLIDER) {
            applySlider(this.dragRow, event.x(), this.dragX, this.dragW);
            return true;
        }
        if (this.dragType == WorkspaceBotPanel.HIT_SLIDER) {
            applyBotCount(event.x());
            return true;
        }
        if (this.dragType == WorkspaceColorPicker.HIT_SV) {
            this.picker.dragSv(this.layout, event.x(), event.y());
            return true;
        }
        if (this.dragType == WorkspaceColorPicker.HIT_HUE) {
            this.picker.dragHue(this.layout, event.x());
            return true;
        }
        if (this.dragType == WorkspaceColorPicker.HIT_ALPHA) {
            this.picker.dragAlpha(this.layout, event.x());
            return true;
        }
        if (this.dragType == HIT_LOOK_SCALE) {
            applyScale(event.x());
            return true;
        }
        return super.mouseDragged(event, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontal, double vertical) {
        // поповеры перекрывают списки: колесо не должно крутить то, что под ними
        if (this.picker.isOpen() || this.lookOpen || this.popRow >= 0) {
            return true;
        }
        float delta = (float) vertical * this.layout.s(38.0F);
        if (mouseX >= this.layout.paneX && mouseX <= this.layout.paneX + this.layout.paneW
                && mouseY >= this.layout.paneY && mouseY <= this.layout.paneY + this.layout.paneH) {
            if (this.botView) {
                this.botRightScrollTarget -= delta;
                return true;
            }
            this.paneScrollTarget = clampScroll(this.paneScrollTarget - delta, this.paneTotal,
                    this.layout.paneH - this.layout.s(64.0F));
            return true;
        }
        if (mouseX >= this.layout.listX && mouseX <= this.layout.listX + this.layout.listW
                && mouseY >= this.layout.listY && mouseY <= this.layout.listY + this.layout.listH) {
            if (this.botView) {
                this.botLeftScrollTarget -= delta;
                return true;
            }
            this.listScrollTarget = clampScroll(this.listScrollTarget - delta, this.listTotal,
                    this.layout.listH - this.layout.s(38.0F));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontal, vertical);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        int key = event.key();

        if (this.bindingModule != null || this.bindingRow >= 0) {
            applyBinding(key == GLFW.GLFW_KEY_ESCAPE ? -1 : key);
            return true;
        }

        if (this.picker.hexFocused()) {
            if (key == GLFW.GLFW_KEY_ESCAPE) {
                this.picker.blurHex();
                return true;
            }
            if (key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_KP_ENTER) {
                this.picker.commitHex();
                this.picker.blurHex();
                markDirty();
                return true;
            }
            if (key == GLFW.GLFW_KEY_BACKSPACE) {
                this.picker.backspaceHex();
                return true;
            }
        }

        if (this.botEditField >= 0) {
            if (key == GLFW.GLFW_KEY_ESCAPE) {
                this.botEditField = -1;
                this.botEditBuffer = "";
                return true;
            }
            if (key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_KP_ENTER) {
                commitBotString();
                MenuSound.play(MenuSound.SELECT);
                return true;
            }
            if (key == GLFW.GLFW_KEY_BACKSPACE && !this.botEditBuffer.isEmpty()) {
                this.botEditBuffer = this.botEditBuffer.substring(0, this.botEditBuffer.length() - 1);
                return true;
            }
        }

        if (this.editingRow >= 0) {
            if (key == GLFW.GLFW_KEY_ESCAPE) {
                this.editingRow = -1;
                this.editBuffer = "";
                return true;
            }
            if (key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_KP_ENTER) {
                commitString();
                return true;
            }
            if (key == GLFW.GLFW_KEY_BACKSPACE && !this.editBuffer.isEmpty()) {
                this.editBuffer = this.editBuffer.substring(0, this.editBuffer.length() - 1);
                return true;
            }
        }

        if (key == GLFW.GLFW_KEY_ESCAPE) {
            if (this.lookOpen) {
                this.lookOpen = false;
                return true;
            }
            if (this.popRow >= 0) {
                this.popRow = -1;
                return true;
            }
            if (this.picker.isOpen()) {
                this.picker.close();
                return true;
            }
            if (this.searchFocused && !this.search.isEmpty()) {
                this.search = "";
                return true;
            }
            close();
            return true;
        }

        if (!this.botView && key == GLFW.GLFW_KEY_F && isControlDown()) {
            this.searchFocused = true;
            MenuSound.play(MenuSound.SELECT);
            return true;
        }

        if (this.searchFocused && key == GLFW.GLFW_KEY_BACKSPACE && !this.search.isEmpty()) {
            this.search = this.search.substring(0, this.search.length() - 1);
            this.listScrollTarget = 0.0F;
            MenuSound.play(MenuSound.TYPE);
            return true;
        }

        return super.keyPressed(event);
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        if (!event.isAllowedChatCharacter()) {
            return false;
        }
        String typed = event.codepointAsString();

        if (this.picker.hexFocused()) {
            for (int i = 0; i < typed.length(); i++) {
                this.picker.typeHex(typed.charAt(i));
            }
            return true;
        }
        if (this.botEditField >= 0) {
            // префикс ника и ник ограничены 16 символами, пароль — 32 (см. BotConfig.sanitize)
            int max = this.botEditField == WorkspaceBotPanel.FIELD_PASSWORD ? 32 : 16;
            if (this.botEditBuffer.length() + typed.length() <= max) {
                this.botEditBuffer = this.botEditBuffer + typed;
                MenuSound.play(MenuSound.TYPE);
            }
            return true;
        }
        if (this.editingRow >= 0) {
            Setting<?> setting = settingForRow(this.editingRow);
            int max = setting instanceof StringSetting string ? string.getMaxLength() : 32;
            if (this.editBuffer.length() + typed.length() <= max) {
                this.editBuffer = this.editBuffer + typed;
                MenuSound.play(MenuSound.TYPE);
            }
            return true;
        }
        if (this.searchFocused) {
            if (this.search.length() < 48) {
                this.search = this.search + typed;
                this.listScrollTarget = 0.0F;
                MenuSound.play(MenuSound.TYPE);
            }
            return true;
        }
        return false;
    }

    @Override
    public void onClose() {
        close();
    }

    private void close() {
        if (this.closing) {
            return;
        }
        commitString();
        commitBotString();
        MenuPrefs.save();
        saveConfig();
        saveBotConfig();
        this.closing = true;
        this.closingAt = System.currentTimeMillis();
        this.lookOpen = false;
        this.popRow = -1;
        this.picker.close();
        MenuSound.play(MenuSound.CLOSE);
    }

    // ---- действия

    private void runHit(int type, int a, int b) {
        switch (type) {
            case HIT_CATEGORY -> {
                this.category = TABS[Math.floorMod(a, TABS.length)];
                this.botView = false;
                this.search = "";
                this.searchFocused = false;
                this.listScrollTarget = 0.0F;
                commitBotString();
                MenuSound.play(MenuSound.TAB);
            }
            case HIT_BOT_TAB -> {
                if (!this.botView) {
                    commitString();
                    this.botView = true;
                    this.search = "";
                    this.searchFocused = false;
                    this.popRow = -1;
                    this.botLeftScrollTarget = 0.0F;
                    this.botRightScrollTarget = 0.0F;
                    MenuSound.play(MenuSound.TAB);
                }
            }
            case HIT_SEARCH -> {
                this.searchFocused = true;
                MenuSound.play(MenuSound.SELECT);
            }
            case HIT_PALETTE_BUTTON -> {
                this.lookOpen = !this.lookOpen;
                MenuSound.play(this.lookOpen ? MenuSound.POP : MenuSound.DROP, 1.0F, 0.7F);
            }
            case HIT_LOOK_CLOSE -> {
                this.lookOpen = false;
                MenuSound.play(MenuSound.DROP, 1.0F, 0.5F);
            }
            case HIT_CLOSE -> close();
            case HIT_CARD -> {
                if (a >= 0 && a < this.visibleCount) {
                    selectModule(this.visible[a]);
                }
            }
            case HIT_CARD_TOGGLE -> {
                if (a >= 0 && a < this.visibleCount) {
                    toggleModule(this.visible[a]);
                }
            }
            case HIT_CARD_KEY -> {
                if (a >= 0 && a < this.visibleCount) {
                    this.bindingModule = this.visible[a];
                    this.bindingRow = -1;
                    MenuSound.play(MenuSound.SELECT);
                }
            }
            case HIT_PANE_TOGGLE -> {
                if (this.selected != null) {
                    toggleModule(this.selected);
                }
            }
            case HIT_LOOK_PALETTE -> {
                MenuPalette.setPalette(a);
                MenuPrefs.palette = Math.floorMod(a, MenuPalette.PALETTES.length);
                MenuSound.play(MenuSound.SELECT);
            }
            case HIT_LOOK_BLUR -> {
                MenuPrefs.blur = !MenuPrefs.blur;
                MenuSound.play(MenuPrefs.blur ? MenuSound.TOGGLE_ON : MenuSound.TOGGLE_OFF);
            }
            case HIT_LOOK_GRADIENT -> {
                MenuPrefs.gradient = !MenuPrefs.gradient;
                MenuSound.play(MenuPrefs.gradient ? MenuSound.TOGGLE_ON : MenuSound.TOGGLE_OFF);
            }
            case HIT_LOOK_SCALE -> {
                this.dragType = HIT_LOOK_SCALE;
                applyScale(this.mouseX);
            }
            case HIT_MODE_ITEM -> applyModeItem(a);
            case WorkspaceSettings.HIT_BOOL -> {
                Setting<?> setting = settingForRow(a);
                if (setting instanceof BooleanSetting bool) {
                    bool.toggle();
                    MenuSound.play(bool.get() ? MenuSound.TOGGLE_ON : MenuSound.TOGGLE_OFF);
                    markDirty();
                }
            }
            case WorkspaceSettings.HIT_SLIDER -> {
                int index = findHit(this.mouseX, this.mouseY);
                if (index >= 0 && this.hitType[index] == WorkspaceSettings.HIT_SLIDER) {
                    this.dragX = this.hx[index];
                    this.dragW = this.hw[index];
                }
                applySlider(a, this.mouseX, this.dragX, this.dragW);
            }
            case WorkspaceSettings.HIT_MODE -> openModePopover(a);
            case WorkspaceSettings.HIT_MULTI -> {
                Setting<?> setting = settingForRow(a);
                if (setting instanceof MultiSelectSetting multi && b >= 0 && b < multi.getModes().size()) {
                    multi.toggle(multi.getModes().get(b));
                    MenuSound.play(MenuSound.CLICK);
                    markDirty();
                }
            }
            case WorkspaceSettings.HIT_STRING -> {
                Setting<?> setting = settingForRow(a);
                if (setting instanceof StringSetting string) {
                    this.editingRow = a;
                    this.editBuffer = string.get() == null ? "" : string.get();
                    MenuSound.play(MenuSound.SELECT);
                }
            }
            case WorkspaceSettings.HIT_COLOR -> {
                Setting<?> setting = settingForRow(a);
                if (setting instanceof ColorSetting color) {
                    int index = findHit(this.mouseX, this.mouseY);
                    float anchorX = this.mouseX + this.layout.s(12.0F);
                    float anchorY = this.mouseY - this.layout.s(20.0F);
                    if (index >= 0) {
                        anchorX = this.hx[index] + this.hw[index] + this.layout.s(10.0F);
                        anchorY = this.hy[index];
                    }
                    this.picker.open(this.layout, color, anchorX, anchorY);
                    MenuSound.play(MenuSound.POP);
                }
            }
            case WorkspaceSettings.HIT_BIND -> {
                this.bindingRow = a;
                this.bindingModule = null;
                MenuSound.play(MenuSound.SELECT);
            }
            case WorkspaceSettings.HIT_ACTION -> {
                Setting<?> setting = settingForRow(a);
                if (setting instanceof ActionSetting action) {
                    action.press();
                    MenuSound.play(MenuSound.CLICK);
                }
            }
            case WorkspaceColorPicker.HIT_SV -> this.picker.dragSv(this.layout, this.mouseX, this.mouseY);
            case WorkspaceColorPicker.HIT_HUE -> this.picker.dragHue(this.layout, this.mouseX);
            case WorkspaceColorPicker.HIT_ALPHA -> this.picker.dragAlpha(this.layout, this.mouseX);
            case WorkspaceColorPicker.HIT_HEX -> this.picker.focusHex();
            case WorkspaceColorPicker.HIT_CLOSE -> {
                this.picker.close();
                MenuSound.play(MenuSound.CLICK);
            }
            case WorkspaceColorPicker.HIT_PRESET -> {
                this.picker.pickPreset(a);
                MenuSound.play(MenuSound.SELECT);
                markDirty();
            }
            default -> runBotHit(type, a, b);
        }
    }

    private void selectModule(ClientModule module) {
        if (module == null) {
            return;
        }
        boolean same = module == this.selected;
        this.selected = module;
        this.paneScroll = 0.0F;
        this.paneScrollTarget = 0.0F;
        this.editingRow = -1;
        this.editBuffer = "";
        this.popRow = -1;
        if (!same) {
            MenuSound.play(MenuSound.SELECT);
        }
    }

    private void toggleModule(ClientModule module) {
        if (module == null) {
            return;
        }
        module.toggle();
        MenuSound.play(module.isEnabled() ? MenuSound.TOGGLE_ON : MenuSound.TOGGLE_OFF);
        markDirty();
    }

    private void openModePopover(int row) {
        Setting<?> setting = settingForRow(row);
        if (!(setting instanceof ModeSetting)) {
            return;
        }
        int index = findHit(this.mouseX, this.mouseY);
        this.popRow = row;
        this.popW = Math.max(this.layout.s(120.0F), this.layout.paneW - this.layout.s(28.0F));
        if (index >= 0) {
            this.popX = this.layout.paneX + this.layout.s(14.0F);
            this.popY = this.hy[index];
        } else {
            this.popX = this.layout.paneX + this.layout.s(14.0F);
            this.popY = this.layout.paneY + this.layout.s(70.0F);
        }
        MenuSound.play(MenuSound.POP);
    }

    private void applyModeItem(int index) {
        Setting<?> setting = settingForRow(this.popRow);
        if (!(setting instanceof ModeSetting mode)) {
            this.popRow = -1;
            return;
        }
        List<String> modes = mode.getModes();
        if (index >= 0 && index < modes.size()) {
            mode.set(modes.get(index));
            MenuSound.play(MenuSound.SELECT);
            markDirty();
        }
        this.popRow = -1;
    }

    private void applySlider(int row, double mouseX, float x, float width) {
        Setting<?> setting = settingForRow(row);
        if (setting instanceof NumberSetting number && width > 1.0F) {
            number.setRatio((mouseX - x) / width);
            markDirty();
        }
    }

    private void applyScale(double mouseX) {
        float width = this.layout.s(258.0F) - this.layout.s(24.0F) - this.layout.s(58.0F);
        float x = this.layout.sideX + this.layout.sideW - this.layout.s(6.0F) + this.layout.s(12.0F)
                + this.layout.s(58.0F);
        float ratio = Math.max(0.0F, Math.min(1.0F, (float) ((mouseX - x) / Math.max(1.0F, width))));
        MenuPrefs.scale = 70.0F + ratio * 60.0F;
        MenuPrefs.sync();
        MenuSound.play(MenuSound.TICK);
    }

    private void applyBinding(int key) {
        if (this.bindingModule != null) {
            if (key >= 0 && KeyBinds.isPrimaryMouseButton(key)) {
                return;
            }
            this.bindingModule.setKeyBind(key);
            this.bindingModule = null;
            MenuSound.play(MenuSound.SELECT);
            markDirty();
            return;
        }
        if (this.bindingRow >= 0) {
            Setting<?> setting = settingForRow(this.bindingRow);
            if (setting instanceof KeyBindSetting bind) {
                bind.set(key);
                MenuSound.play(MenuSound.SELECT);
                markDirty();
            }
            this.bindingRow = -1;
        }
    }

    private void commitString() {
        if (this.editingRow < 0) {
            return;
        }
        Setting<?> setting = settingForRow(this.editingRow);
        if (setting instanceof StringSetting string) {
            string.set(this.editBuffer);
        }
        this.editingRow = -1;
        this.editBuffer = "";
        markDirty();
    }

    // ---- данные

    private void collectVisible() {
        this.visibleCount = 0;
        String query = this.search.trim().toLowerCase(Locale.ROOT);
        if (query.isEmpty()) {
            List<ClientModule> list = this.modules.getByCategory(this.category);
            for (int i = 0; i < list.size() && this.visibleCount < MAX_VISIBLE; i++) {
                this.visible[this.visibleCount++] = list.get(i);
            }
            return;
        }
        List<ClientModule> all = this.modules.getModules();
        for (int i = 0; i < all.size() && this.visibleCount < MAX_VISIBLE; i++) {
            ClientModule module = all.get(i);
            if (matches(module, query)) {
                this.visible[this.visibleCount++] = module;
            }
        }
    }

    private boolean matches(ClientModule module, String query) {
        if (containsIgnoreCase(module.getName(), query)
                || containsIgnoreCase(SelectAndSaveHelper.text(module.getName()), query)
                || containsIgnoreCase(module.getDescription(), query)) {
            return true;
        }
        List<Setting<?>> settings = module.getSettings();
        for (int i = 0; i < settings.size(); i++) {
            if (containsIgnoreCase(settings.get(i).getName(), query)) {
                return true;
            }
        }
        return false;
    }

    private int indexOf(ClientModule module) {
        Integer index = this.moduleIndex.get(module);
        return index == null ? 0 : index;
    }

    private Setting<?> settingForRow(int row) {
        if (this.selected == null || row < 0) {
            return null;
        }
        List<Setting<?>> settings = this.selected.getSettings();
        int visibleRow = 0;
        for (int i = 0; i < settings.size(); i++) {
            Setting<?> setting = settings.get(i);
            if (!setting.isVisible()) {
                continue;
            }
            if (visibleRow == row) {
                return setting;
            }
            visibleRow++;
        }
        return null;
    }

    private static boolean hasVisibleSettings(ClientModule module) {
        List<Setting<?>> settings = module.getSettings();
        for (int i = 0; i < settings.size(); i++) {
            if (settings.get(i).isVisible()) {
                return true;
            }
        }
        return false;
    }

    // ---- хит-области

    private void addHit(int type, int a, int b, float x, float y, float w, float h) {
        if (this.hitCount >= MAX_HITS) {
            return;
        }
        this.hx[this.hitCount] = x;
        this.hy[this.hitCount] = y;
        this.hw[this.hitCount] = w;
        this.hh[this.hitCount] = h;
        this.hitType[this.hitCount] = type;
        this.hitA[this.hitCount] = a;
        this.hitB[this.hitCount] = b;
        this.hitCount++;
    }

    /** Последняя добавленная область выигрывает — так поповеры перекрывают меню. */
    private int findHit(double x, double y) {
        for (int i = this.hitCount - 1; i >= 0; i--) {
            if (x >= this.hx[i] && x <= this.hx[i] + this.hw[i]
                    && y >= this.hy[i] && y <= this.hy[i] + this.hh[i]) {
                return i;
            }
        }
        return -1;
    }

    private static boolean isPickerHit(int type) {
        return type >= WorkspaceColorPicker.HIT_SV && type <= WorkspaceColorPicker.HIT_PRESET;
    }

    private static boolean isLookHit(int type) {
        return type >= HIT_LOOK_PALETTE && type <= HIT_LOOK_SCALE;
    }

    private boolean inside(float x, float y, float w, float h) {
        return this.mouseX >= x && this.mouseX <= x + w && this.mouseY >= y && this.mouseY <= y + h;
    }

    // ---- сохранение

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
            SkycoreClient.LOGGER.warn("Failed to save Skycore config after Workspace change", failure);
        }
    }

    /**
     * Записывает настройки бота.
     *
     * <p>Отдельный файл {@code skycore/bot.json} и отдельный флаг: бот не является
     * модулем, поэтому его настройки не относятся к {@link ConfigManager} и не должны
     * заставлять его перезаписывать пользовательский профиль.
     */
    private void saveBotConfig() {
        if (!this.botDirty) {
            return;
        }
        this.botDirty = false;
        try {
            BotConfig.save();
        } catch (RuntimeException failure) {
            SkycoreClient.LOGGER.warn("Failed to save bot settings after Workspace change", failure);
        }
    }

    // ---- утилиты

    private static String icon(ModuleCategory category) {
        return switch (category) {
            case COMBAT -> IconGlyphs.COMBAT;
            case MOVEMENT -> IconGlyphs.MOVEMENT;
            case RENDER -> IconGlyphs.RENDER;
            case PLAYER -> IconGlyphs.PLAYER;
            case MISC -> IconGlyphs.MISC;
        };
    }

    /** Строка без {@code null}: описания модулей и значения настроек могут быть пустыми. */
    private static String safe(String value) {
        return value == null ? "" : value;
    }

    /** Строка интерфейса на языке клиента (тот же переключатель, что у остальных экранов). */
    private static String L(String russian, String english) {
        return SelectAndSaveHelper.isRussian() ? russian : english;
    }

    /**
     * Подпись версии в сайдбаре.
     *
     * <p>Версия берётся из метаданных мода ({@code fabric.mod.json}), а не пишется
     * строкой в экране: иначе при смене {@code version} в {@code build.gradle} подпись
     * осталась бы старой. Вне Fabric (юнит-проверки, предпросмотр) подпись просто
     * скрывается — падать из-за декоративной строки экран не должен.
     */
    private static String versionLabel() {
        try {
            return net.fabricmc.loader.api.FabricLoader.getInstance()
                    .getModContainer(SkycoreClient.MOD_ID)
                    .map(container -> container.getMetadata().getVersion().getFriendlyString())
                    .orElse("");
        } catch (RuntimeException | LinkageError unavailable) {
            return "";
        }
    }

    private boolean isControlDown() {
        if (this.minecraft == null || this.minecraft.getWindow() == null) {
            return false;
        }
        long window = this.minecraft.getWindow().handle();
        return GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_CONTROL) == GLFW.GLFW_PRESS
                || GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT_CONTROL) == GLFW.GLFW_PRESS;
    }

    private static float clampScroll(float value, float total, float view) {
        float max = Math.max(0.0F, total - Math.max(1.0F, view));
        return Math.max(0.0F, Math.min(max, value));
    }

    static boolean containsIgnoreCase(String text, String query) {
        return indexOfIgnoreCase(text, query) >= 0;
    }

    static int indexOfIgnoreCase(String text, String query) {
        if (text == null || query == null || query.isEmpty() || text.length() < query.length()) {
            return -1;
        }
        int last = text.length() - query.length();
        for (int i = 0; i <= last; i++) {
            boolean match = true;
            for (int j = 0; j < query.length(); j++) {
                char a = text.charAt(i + j);
                char b = query.charAt(j);
                if (a != b && Character.toLowerCase(a) != Character.toLowerCase(b)) {
                    match = false;
                    break;
                }
            }
            if (match) {
                return i;
            }
        }
        return -1;
    }
}
