package swerve.ui.menu;

import swerve.managers.ModuleManager;
import swerve.modules.Module;
import swerve.modules.ModuleCategory;
import swerve.modules.api.setting.BindSetting;
import swerve.modules.api.setting.BooleanSetting;
import swerve.modules.api.setting.FloatSetting;
import swerve.modules.api.setting.ListSetting;
import swerve.modules.api.setting.ModeSetting;
import swerve.modules.api.setting.Setting;
import swerve.modules.api.setting.TextSetting;
import swerve.ui.menu.MenuText.Weight;
import swerve.utils.input.KeyBoardUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import org.lwjgl.glfw.GLFW;

import java.util.List;

/**
 * Меню клиента — порт Arcanic Visuals (GPL-3.0) на 1.21.4.
 *
 * <p>Всё рисуется в реальных пикселях окна: {@link Px#begin(GuiGraphics)} масштабирует позу,
 * поэтому координаты не зависят от guiScale. Клики собираются в примитивные массивы
 * (без лямбд и списков в кадре), анимации адресуются целочисленными индексами.
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
            ModuleCategory.INTERFACE,
    };

    private static final String[] TAB_ICONS = {"sword", "running", "sparkles", "user-circle", "wrench", "monitor"};

    private static final String[] TAB_TITLES = {"Бой", "Движение", "Визуал", "Игрок", "Утилиты", "Интерфейс"};

    private static final String[] SETTINGS_TABS = {"Настройки"};

    /** Док: модули и настройки оформления. */
    private static final String[][] DOCK = {
            {"modules-s", "Модули"},
            {"cosmetics-s", "Косметика"},
            {"hud-s", "HUD"},
            {"sliders", "Настройки"},
    };

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
    static final int ACT_ANIMATIONS = 21;
    static final int ACT_SOUNDS = 22;
    static final int ACT_POPOVER_ITEM = 23;
    static final int ACT_PREF_BOOL = 24;
    static final int ACT_PREF_SLIDER = 25;

    // идентификаторы наведения (целые числа — без строк в кадре)
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
    private static final int MAX_VISIBLE = 64;

    // ---- состояние
    private final MenuAnim anim = new MenuAnim();
    private final int[] regionX = new int[MAX_REGIONS];
    private final int[] regionY = new int[MAX_REGIONS];
    private final int[] regionW = new int[MAX_REGIONS];
    private final int[] regionH = new int[MAX_REGIONS];
    private final int[] regionAction = new int[MAX_REGIONS];
    private final int[] regionAlt = new int[MAX_REGIONS];
    private final int[] regionPayload = new int[MAX_REGIONS];
    private int regionCount;

    private final Module[] visible = new Module[MAX_VISIBLE];
    private int visibleCount;

    private final float[] scroll = new float[MenuAnim.SCROLL_SIZE];
    private final int[] tabX = new int[8];
    private final int[] tabW = new int[8];
    private final int[] colYBuf = new int[4];
    private final int[] cardX = new int[MAX_VISIBLE];
    private final int[] cardY = new int[MAX_VISIBLE];
    private final int[] cardH = new int[MAX_VISIBLE];

    private static ModuleCategory category = ModuleCategory.COMBAT;
    private static Page page = Page.MODULES;
    /** Категория, зафиксированная страницей дока (косметика / HUD). */
    private static boolean categoryLocked;

    private float u = 1;
    private double mx;
    double my;
    private long now = System.currentTimeMillis();
    private long openedAt = System.currentTimeMillis();
    private long contentAt = System.currentTimeMillis();
    private boolean closing;
    private String search = "";
    private boolean searchFocused;

    Module popMod;
    Module popShown;
    Module binding;
    private BindSetting bindingSetting;
    private int bindingSettingIndex = -1;

    private ModeSetting pop;
    private int popX;
    private int popY;
    private int popW;
    private long popAt;

    private int hot;
    private int lastHot;

    int px;
    int py;
    int pw;
    int ph;
    private int cx0;
    private int cw0;
    private int top;
    private int bottom;

    int ancX;
    int ancY;
    int ancW;
    int ancH;
    int pmX;
    int pmY;
    int pmW;
    int pmH;

    boolean compact;

    private final ModulePopup popup = new ModulePopup(this);

    public MenuScreen() {
        super(Component.literal("Swerve"));
        MenuSound.play(MenuSound.OPEN);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    int S(double v) {
        return (int) Math.round(v * u);
    }

    static MenuTheme.Palette T() {
        return MenuTheme.p();
    }

    static boolean light() {
        return MenuTheme.p().light();
    }

    static int acc() {
        return MenuTheme.accent() | 0xFF000000;
    }

    static int accText() {
        return light() ? MenuTheme.accent() : MenuTheme.accent2();
    }

    int lt(int lightColour, int darkColour) {
        return light() ? lightColour : darkColour;
    }

    private int hair() {
        return Px.a(T().text(), light() ? 22 : 14);
    }

    private int inset(boolean hv) {
        return light() ? Px.a(0x1B1530, hv ? 14 : 8) : Px.mix(T().bg() | 0xFF000000, 0xFF000000, hv ? .05f : .22f);
    }

    int plate() {
        return light() ? 0xFFFFFFFF : Px.mix(T().bg() | 0xFF000000, 0xFFFFFFFF, .025f);
    }

    boolean hover(int x, int y, int w, int h) {
        return mx >= x && mx < x + w && my >= y && my < y + h;
    }

    float an(int key, float target, float speed) {
        return anim.to(key, target, speed);
    }

    float tw(int key, float target, int ms) {
        return anim.tween(key, target, ms);
    }

    static float ease(float t) {
        return MenuAnim.easeOutCubic(Math.max(0, Math.min(1, t)));
    }

    boolean blocked() {
        return pop != null;
    }

    boolean under() {
        return blocked();
    }

    private void region(int x, int y, int w, int h, int action, int payload) {
        region(x, y, w, h, action, ACT_NONE, payload);
    }

    private void region(int x, int y, int w, int h, int action, int alt, int payload) {
        if (regionCount >= MAX_REGIONS) {
            return;
        }
        regionX[regionCount] = x;
        regionY[regionCount] = y;
        regionW[regionCount] = w;
        regionH[regionCount] = h;
        regionAction[regionCount] = action;
        regionAlt[regionCount] = alt;
        regionPayload[regionCount] = payload;
        regionCount++;
    }

    void clipped(int x, int y, int w, int h, float sc, int t, int b, int action, int payload) {
        clipped(x, y, w, h, sc, t, b, action, ACT_NONE, payload);
    }

    void clipped(int x, int y, int w, int h, float sc, int t, int b, int action, int alt, int payload) {
        int y0 = (int) Math.max(t, y - sc);
        int y1 = (int) Math.min(b, y - sc + h);
        if (y1 > y0) {
            region(x, y0, w, y1 - y0, action, alt, payload);
        }
    }

    static void glyph(GuiGraphics g, String name, int x, int y, int size, int line) {
        Px.icon(g, Px.hasIcon("a." + name) ? "a." + name : name, x, y, size, line);
    }

    void hot(int key) {
        hot = key;
    }

    void toggle(GuiGraphics g, int x, int y, int w, int h, float on, float hv) {
        boolean lt = light();
        int th = S(20) / 2 * 2;
        int tw = Math.round(th * 1.5f) / 2 * 2;
        int tx = x + w - tw;
        int ty = y + (h - th) / 2;
        int r = th / 2;
        int off = lt ? Px.mix(0xFFE4E2EB, 0xFFD9D6E2, hv) : Px.a(0xFFFFFF, 24 + Math.round(14 * hv));
        Px.round(g, tx, ty, tw, th, r, Px.mix(off, acc(), on));
        int m = Math.max(2, Math.round(th * .15f));
        int ks = (th - m * 2) / 2 * 2;
        int kx = tx + m + Math.round((tw - m * 2 - ks) * on);
        Px.round(g, kx, ty + m, ks, ks, ks / 2, Px.mix(lt ? 0xFFFFFFFF : 0xFF757D8A, lt ? 0xFFFFFFFF : 0xFF12141C, on));
    }

    private int keycap(GuiGraphics g, String t, int x, int y, int fs) {
        int w = Px.textWidth(t, Weight.SEMIBOLD, fs) + S(12);
        int h = fs + S(9);
        Px.round(g, x, y, w, h, S(6), Px.a(T().text(), light() ? 10 : 9));
        Px.outline(g, x, y, w, h, S(6), Px.a(T().text(), light() ? 26 : 20));
        Px.text(g, t, x + S(6), y + (h - fs) / 2, Weight.SEMIBOLD, fs, T().muted());
        return w;
    }

    private int keycapW(String t, int fs) {
        return Px.textWidth(t, Weight.SEMIBOLD, fs) + S(12);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float pt) {
        draw(g, mouseX, mouseY, pt);
    }

    private void draw(GuiGraphics g, int mouseX, int mouseY, float pt) {
        super.render(g, mouseX, mouseY, pt);
        anim.frame();
        regionCount = 0;
        now = System.currentTimeMillis();
        mx = Px.px(mouseX);
        my = Px.px(mouseY);
        int sw = Px.width();
        int sh = Px.height();
        u = (float) Math.max(.45, Math.min(2.2, Math.min(sh / 1080.0, sw / 1060.0) * MenuTheme.menuScale * .86));
        float open = an(MenuAnim.OPEN, closing ? 0 : 1, closing ? 18 : 8);
        if (closing && open < .02f) {
            super.onClose();
            return;
        }
        float e = ease(open);

        pw = Math.min(S(1010), sw - S(80));
        ph = Math.min(S(640), sh - S(210));
        px = (sw - pw) / 2;
        py = (sh - ph - S(28) + S(78)) / 2;

        Px.begin(g);
        float keep = Px.fade;
        Px.rect(g, 0, 0, sw, sh, Px.a(0x05040A, (int) (255 * open)));
        Px.fade = keep * Math.min(1, open * 1.3f);
        brand(g, sw / 2, py - S(58));
        g.pose().pushPose();
        Px.translate(g, 0, Math.round((1 - e) * S(18)));
        panel(g);
        dock(g, sw / 2, py + ph);
        g.pose().popPose();
        popup.modulePopup(g);
        if (pop != null) {
            popover(g);
        }
        Px.fade = keep;
        Px.end(g);

        // мягкий щелчок, когда курсор наткнулся на новое
        if (hot != 0 && hot != lastHot && now - openedAt > 250 && !closing) {
            MenuSound.play(MenuSound.HOVER, 1 + (hot % 7) * .015f, 1);
        }
        lastHot = hot;
        hot = null;
    }

    private void brand(GuiGraphics g, int cx, int y) {
        float t = (now - openedAt) / 1000f;
        float a = ease(t / .35f);
        int ls = S(28);
        int fs = S(21);
        int gap = S(10);
        String w1 = "Swerve";
        String w2 = " Client";
        int w1w = Px.textWidth(w1, Weight.BOLD, fs);
        int w2w = Px.textWidth(w2, Weight.MEDIUM, fs);
        int total = ls + gap + w1w + w2w;
        int x = cx - total / 2;
        float k = Px.fade;
        Px.fade = k * a;
        int dy = Math.round((1 - a) * S(6));
        int px0 = x - S(18);
        int py0 = y + dy - S(10);
        int pw0 = total + S(38);
        int ph0 = ls + S(20);
        boolean lt = light();
        Px.shadow(g, px0, py0 + S(4), pw0, ph0, S(16), Px.a(0, lt ? 30 : 90));
        Px.round(g, px0, py0, pw0, ph0, ph0 / 2, lt ? 0xF2FFFFFF : 0xF00C0D12);
        Px.glow(g, x - S(8), py0 + S(2), ls + S(16), ph0 - S(4), Px.a(MenuTheme.accent(), lt ? 50 : 90));
        Px.outline(g, px0, py0, pw0, ph0, ph0 / 2, lt ? Px.a(T().text(), 24) : 0x1CFFFFFF);
        int hl = Math.max(1, S(1));
        int half = (pw0 - ph0) / 2;
        Px.hgrad(g, px0 + ph0 / 2, py0, half, hl, Px.a(MenuTheme.accent2(), 0), Px.a(MenuTheme.accent2(), lt ? 70 : 130));
        Px.hgrad(g, px0 + ph0 / 2 + half, py0, half, hl, Px.a(MenuTheme.accent2(), lt ? 70 : 130), Px.a(MenuTheme.accent2(), 0));
        // знак клиента: ромб в цвет акцента
        int mx0 = x + ls / 2;
        int my0 = y + dy + ls / 2;
        int mr = S(9);
        Px.round(g, mx0 - mr, my0 - mr, mr * 2, mr * 2, mr / 2, acc());
        Px.outline(g, mx0 - mr, my0 - mr, mr * 2, mr * 2, mr / 2, Px.a(0xFFFFFFFF, 90));
        int ty = y + dy + (ls - fs) / 2;
        Px.text(g, w1, x + ls + gap, ty, Weight.BOLD, fs, lt ? 0xFF15151A : 0xFFF4F6FA);
        Px.text(g, w2, x + ls + gap + w1w, ty, Weight.MEDIUM, fs, lt ? 0xFF696978 : 0xFF8F97A4);
        Px.fade = k;
    }

    private void panel(GuiGraphics g) {
        boolean lt = light();
        int r = S(12);
        Px.shadow(g, px, py + S(16), pw, ph, S(46), Px.a(0, lt ? 40 : 160));
        Px.round(g, px, py, pw, ph, r, lt ? 0xF8F8F7FA : (T().bg() & 0xFFFFFF) | 0xF2000000);
        Px.outline(g, px, py, pw, ph, r, Px.a(T().text(), lt ? 28 : 20));
        int pad = S(36);
        int hy = py + S(26);
        int hh = S(40);
        cx0 = px + pad;
        cw0 = pw - pad * 2;
        header(g, cx0, hy, cw0, hh);
        int lineY = hy + hh + S(18);
        Px.rect(g, cx0, lineY, cw0, Math.max(1, S(1)), hair());
        top = lineY + S(18);
        bottom = py + ph - S(44);
        if (page == Page.MODULES) {
            rows(g);
        } else {
            settingsPage(g);
        }
    }

    // ---- шапка: вкладки, поиск, кнопка темы
    private void header(GuiGraphics g, int x, int y, int w, int h) {
        String[] labels;
        int active;
        if (page == Page.MODULES) {
            labels = TAB_TITLES;
            active = -1;
            if (search.isEmpty()) {
                for (int i = 0; i < TABS.length; i++) {
                    if (TABS[i] == category) {
                        active = i;
                    }
                }
            }
        } else {
            labels = SETTINGS_TABS;
            active = 0;
        }
        int many = labels.length > 6;
        int fs = S(many ? 13 : 14);
        int is = S(many ? 14 : 15);
        int ip = S(many ? 9 : 12);
        int gap = S(many ? 2 : 4);
        int tg = S(many ? 8 : 8);
        int sx = x - ip;
        int ph2 = S(32) / 2 * 2;
        int py2 = y + (h - ph2) / 2;
        int[] xs = tabX;
        int[] ws = tabW;
        int tx = sx;
        for (int i = 0; i < labels.length; i++) {
            ws[i] = ip + is + tg + Px.textWidth(labels[i], Weight.SEMIBOLD, fs) + ip;
            xs[i] = tx;
            tx += ws[i] + gap;
        }
        int stripW = tx - gap - sx;
        if (active >= 0) {
            float plx = tw(MenuAnim.PLATE_X, xs[active], 260);
            float plw = tw(MenuAnim.PLATE_W, ws[active], 260);
            Px.round(g, Math.round(plx), py2, Math.round(plw), ph2, S(11), light() ? Px.a(0x1B1530, 14) : 0xA01A1C24);
            Px.outline(g, Math.round(plx), py2, Math.round(plw), ph2, S(11), light() ? Px.a(0x1B1530, 20) : 0x14FFFFFF);
        }
        for (int i = 0; i < labels.length; i++) {
            boolean on = i == active;
            boolean hv = !under() && hover(xs[i], y, ws[i], h);
            if (hv) {
                hot(HOT_TAB + i);
            }
            float hk = an(MenuAnim.TAB_HOVER + i, hv ? 1 : 0, 18);
            float ak = tw(MenuAnim.TAB_ACTIVE + i, on ? 1 : 0, 220);
            int c = Px.mix(Px.mix(lt(T().muted(), 0xFFB0B8C4), T().text(), hk * .6f), T().text(), ak);
            String icon = page == Page.MODULES ? TAB_ICONS[i] : "sliders";
            glyph(g, icon, xs[i] + ip, py2 + (ph2 - is) / 2, is, Px.mix(c, accText(), ak * .9f));
            Px.text(g, labels[i], xs[i] + ip + is + tg, py2 + (ph2 - fs) / 2, Weight.SEMIBOLD, fs, c);
            if (!under() && labels.length > 1) {
                region(xs[i], y, ws[i], h, ACT_TAB, i);
            }
        }

        // справа: кнопка темы, затем поиск
        int bs = h;
        int bx = x + w - bs;
        themeButton(g, bx, y, bs);
        int sw = Math.max(S(170), Math.min(S(250), bx - S(12) - (sx + stripW + S(18))));
        int spx = bx - S(10) - sw;
        if (page == Page.MODULES) {
            searchPill(g, spx, y, sw, h);
        }
    }

    private void tabClick(int i) {
        if (page == Page.MODULES) {
            if (category != TABS[i] || !search.isEmpty()) {
                category = TABS[i];
                search = "";
                searchFocused = false;
                content(MenuSound.TAB);
            }
        }
    }

    private void content(int sound) {
        contentAt = now;
        popMod = null;
        pop = null;
        if (sound >= 0) {
            MenuSound.play(sound);
        }
    }

    private void searchPill(GuiGraphics g, int x, int y, int w, int h) {
        boolean over = !under() && hover(x, y, w, h);
        if (over) {
            hot(HOT_SEARCH);
        }
        float f = tw(MenuAnim.SFOCUS, searchFocused ? 1 : 0, 180);
        float hv = an(MenuAnim.SHOVER, over ? 1 : 0, 18);
        Px.round(g, x, y, w, h, S(7), light() ? Px.a(0x1B1530, (int) (8 + 5 * hv)) : inset(hv > .5f));
        Px.outline(g, x, y, w, h, S(7),
                Px.mix(Px.a(T().text(), light() ? (int) (16 + 10 * hv) : (int) (12 + 8 * hv)), acc(), f));
        glyph(g, "search-s", x + S(15), y + (h - S(15)) / 2, S(15), Px.mix(T().dim(), accText(), f));
        boolean empty = search.isEmpty();
        Px.text(g, Px.ellipsize(empty ? "Поиск" : search, Weight.MEDIUM, S(14), w - S(80)),
                x + S(40), y + (h - S(14)) / 2, Weight.MEDIUM, S(14), empty ? T().dim() : T().text());
        if (searchFocused && (now - openedAt) / 530 % 2 == 0) {
            Px.rect(g,
                    x + S(40) + (empty ? 0 : Px.textWidth(search, Weight.MEDIUM, S(14))) + S(1),
                    y + S(11), Math.max(1, S(1.5)), h - S(22), accText());
        }
        if (!empty) {
            boolean xh = !under() && hover(x + w - S(34), y, S(34), h);
            glyph(g, "x-s", x + w - S(26), y + (h - S(13)) / 2, S(13), xh ? T().text() : T().muted());
            if (!under()) {
                region(x + w - S(34), y, S(34), h, ACT_SEARCH_CLEAR, 0);
            }
        } else if (!searchFocused) {
            keycap(g, "Ctrl F", x + w - S(12) - keycapW("Ctrl F", S(10)), y + (h - S(19)) / 2, S(10));
        }
        if (!under()) {
            region(x, y, w - (empty ? 0 : S(34)), h, ACT_SEARCH, 0);
        }
    }

    private void themeButton(GuiGraphics g, int x, int y, int s) {
        boolean hv = !under() && hover(x, y, s, s);
        if (hv) {
            hot(HOT_THEME);
        }
        float hk = an(MenuAnim.THEME_HOVER, hv ? 1 : 0, 18);
        float k = tw(MenuAnim.THEME_KEY, MenuTheme.palette == MenuTheme.LIGHT ? 1 : 0, 420);
        Px.circle(g, x + s / 2, y + s / 2, s / 2, light() ? Px.a(0x1B1530, (int) (9 + 7 * hk)) : Px.a(0xFFFFFF, (int) (6 + 8 * hk)));
        Px.ring(g, x + s / 2, y + s / 2, s / 2, Px.a(T().text(), light() ? (int) (16 + 10 * hk) : (int) (12 + 10 * hk)));
        int gs = S(17);
        int c = Px.mix(T().muted(), T().text(), hk);
        for (int i = 0; i < 2; i++) {
            float vis = i == 0 ? 1 - k : k;
            if (vis < .02f) {
                continue;
            }
            float kf = Px.fade;
            Px.fade = kf * vis;
            g.pose().pushPose();
            Px.translate(g, x + s / 2f, y + s / 2f);
            if (vis < .999f) {
                Px.rotate2d(g, (float) ((i == 0 ? k : k - 1) * Math.PI * .6));
                float sc = .6f + .4f * vis;
                Px.scale(g, sc, sc);
            }
            glyph(g, i == 0 ? "moon-s" : "sun-s", -gs / 2, -gs / 2, gs, c);
            g.pose().popPose();
            Px.fade = kf;
        }
        if (!under()) {
            region(x, y, s, s, ACT_THEME, 0);
        }
    }

    float scrolled(int context, int total, int t, int b) {
        float max = Math.max(0, total - (b - t));
        float want = Math.max(0, Math.min(max, scroll[context]));
        scroll[context] = want;
        return an(MenuAnim.SCROLL_BASE + context, want, 15);
    }

    int baseColour() {
        return light() ? 0xFFF8F8F7 : (T().bg() | 0xFF000000);
    }

    void edges(GuiGraphics g, int x, int w, int t, int b, float sc, int colour) {
        int fh = S(24);
        int max = 240;
        if (sc > .5f) {
            Px.vgrad(g, x, t, w, fh, Px.a(colour, (int) (Math.min(1, sc / S(24)) * max)), Px.a(colour, 0));
        }
        Px.vgrad(g, x, b - fh, w, fh, Px.a(colour, 0), Px.a(colour, max));
    }

    void scrollbar(GuiGraphics g, int x, int t, int b, int total, float sc, int context) {
        int view = b - t;
        if (total <= view) {
            return;
        }
        scrollTotal[context] = total;
        int th = Math.max(S(40), view * view / total);
        int ty = t + Math.round((view - th) * Math.min(1, sc / (total - view)));
        boolean hv = !blocked() && hover(x - S(8), t, S(20), view);
        float hk = an(MenuAnim.SCROLL_BASE + MenuAnim.SCROLL_BAR + context, hv ? 1 : 0, 18);
        int wdt = S(3) + Math.round(S(3) * hk);
        Px.round(g, x + S(2) - wdt / 2, ty, wdt, th, wdt / 2,
                light() ? Px.mix(Px.a(T().text(), 46), Px.a(T().text(), 90), hk) : Px.mix(0x30FFFFFF, 0x60FFFFFF, hk));
        if (!blocked()) {
            region(x - S(8), t, S(20), view, ACT_SCROLL, context);
        }
    }

    // ---- список модулей
    private void collectVisible() {
        visibleCount = 0;
        String q = search.toLowerCase(java.util.Locale.ROOT);
        List<Module> pool = ModuleManager.getModules();
        for (int i = 0, size = pool.size(); i < size && visibleCount < MAX_VISIBLE; i++) {
            Module m = pool.get(i);
            if (search.isEmpty()) {
                if (m.getCategory() != category) {
                    continue;
                }
            } else {
                String name = m.getName().toLowerCase(java.util.Locale.ROOT);
                String desc = m.getDescription().toLowerCase(java.util.Locale.ROOT);
                if (!name.contains(q) && !desc.contains(q)) {
                    continue;
                }
            }
            visible[visibleCount++] = m;
        }
    }

    private void rows(GuiGraphics g) {
        collectVisible();
        if (visibleCount == 0) {
            float t = ease((now - contentAt) / 300f);
            float k = Px.fade;
            Px.fade = k * t;
            int cy = top + (bottom - top) / 2 - S(46);
            glyph(g, "search-s", cx0 + cw0 / 2 - S(15), cy, S(30), T().dim());
            Px.textCenter(g, "Ничего не найдено", cx0 + cw0 / 2, cy + S(46), Weight.SEMIBOLD, S(15), T().text());
            Px.textCenter(g, "Попробуй другое слово", cx0 + cw0 / 2, cy + S(70), Weight.MEDIUM, S(12), T().dim());
            Px.fade = k;
            return;
        }
        compact = true;
        int gap = S(12);
        int cols = cw0 >= S(1100) ? 3 : 2;
        int cw = (cw0 - gap * (cols - 1)) / cols;
        int count = visibleCount;
        int[] colY = colYBuf;
        colY[0] = 0;
        colY[1] = 0;
        colY[2] = 0;
        colY[3] = 0;
        int[] cx = cardX;
        int[] cy = cardY;
        int[] ch = cardH;
        for (int i = 0; i < count; i++) {
            int c = 0;
            for (int j = 1; j < cols; j++) {
                if (colY[j] < colY[c]) {
                    c = j;
                }
            }
            cx[i] = cx0 + c * (cw + gap);
            cy[i] = top + S(2) + colY[c];
            ch[i] = headH();
            colY[c] += ch[i] + gap;
        }
        int total = 0;
        for (int v : colY) {
            total = Math.max(total, v);
        }
        total += S(4);
        int context = search.isEmpty() ? category.ordinal() : 6;
        float sc = scrolled(context, total, top, bottom);
        g.enableScissor(cx0 - S(12), top, cx0 + cw0 + S(12), bottom);
        g.pose().pushPose();
        Px.translate(g, 0, -Math.round(sc));
        for (int i = 0; i < count; i++) {
            card(g, visible[i], cx[i], cy[i], cw, ch[i], Math.round(sc), i);
        }
        g.pose().popPose();
        g.disableScissor();
        compact = false;
        edges(g, cx0 - S(12), cw0 + S(24), top, bottom, sc, baseColour());
        scrollbar(g, cx0 + cw0 + S(14), top, bottom, total, sc, context);
    }

    private int headH() {
        return S(58);
    }

    private void card(GuiGraphics g, Module m, int x, int y, int w, int h, float sc, int i) {
        float sy = y - sc;
        if (popMod == m) {
            ancX = x;
            ancY = Math.round(sy);
            ancW = w;
            ancH = h;
        }
        if (sy + h < top - S(20) || sy > bottom + S(20)) {
            return;
        }
        float app = ease((now - contentAt - Math.min(i, 12) * 22L) / 300f);
        boolean lt = light();
        int pad = S(18);
        int head = headH();
        int r = S(12);
        boolean hv = !under() && my >= top && my < bottom && hover(x, Math.round(sy), w, head);
        int mi = moduleIndex(m);
        if (hv) {
            hot(HOT_CARD + mi);
        }
        float hk = an(MenuAnim.MODULE_BASE + mi * MenuAnim.MOD_STRIDE + MenuAnim.MOD_HOVER, hv ? 1 : 0, 18);
        float on = tw(MenuAnim.MODULE_BASE + mi * MenuAnim.MOD_STRIDE + MenuAnim.MOD_ON, m.isEnable() ? 1 : 0, 180);
        float k = Px.fade;
        Px.fade = k * app;
        g.pose().pushPose();
        Px.translate(g, 0, Math.round((1 - app) * S(8)));
        float sel = tw(MenuAnim.MODULE_BASE + mi * MenuAnim.MOD_STRIDE + MenuAnim.MOD_SELECTED, popMod == m ? 1 : 0, 200);
        Px.round(g, x, y, w, h, r, Px.mix(plate(), lt ? 0xFFFFFFFF : Px.mix(T().bg() | 0xFF000000, 0xFFFFFFFF, .055f), hk));
        Px.outline(g, x, y, w, h, r, Px.mix(lt ? Px.a(T().text(), 22) : 0x18FFFFFF, Px.a(MenuTheme.accent(), 200), sel));

        // заголовок: название и описание слева, клавиша и тумблер справа
        int ckS = S(34) / 2 * 2;
        int ckX = x + w - pad - ckS;
        boolean waiting = binding == m;
        String kl = waiting ? "Нажми клавишу" : m.getKey() >= 0 ? KeyBoardUtils.getBindName(m.getKey()) : "";
        int kfs = S(11);
        int kh = S(24) / 2 * 2;
        int kmin = S(34) / 2 * 2;
        int kwT = kl.isEmpty() ? kmin : Math.max(kmin, Px.textWidth(kl, Weight.SEMIBOLD, kfs) + S(18));
        int kw = Math.round(tw(MenuAnim.MODULE_BASE + mi * MenuAnim.MOD_STRIDE + MenuAnim.MOD_KEY_WIDTH, kwT, 220));
        int kx = ckX - S(10) - kw;
        int ky = y + (head - kh) / 2;
        boolean hasP = hasSettings(m);
        int gs = Math.round(kh * 1.2f) / 2 * 2;
        int gx = kx - S(6) - gs;
        int textW = (hasP ? gx : kx) - S(10) - (x + pad);
        int nfs = S(15);
        String name = Px.ellipsize(m.getName(), Weight.SEMIBOLD, nfs, textW);
        int nameC = Px.mix(Px.mix(T().muted(), T().text(), .55f), T().text(), Math.max(on, hk));
        int ny = y + S(12);
        if (search.isEmpty()) {
            Px.text(g, name, x + pad, ny, Weight.SEMIBOLD, nfs, nameC);
        } else {
            nameLit(g, name, x + pad, ny, nfs, nameC);
        }
        String desc = Px.ellipsize(search.isEmpty() ? m.getDescription() : m.getCategory().getName(),
                Weight.MEDIUM, S(12), textW);
        Px.text(g, desc, x + pad, ny + S(20), Weight.MEDIUM, S(12), T().dim());
        boolean khv = hv && hover(kx, Math.round(sy + (ky - y)), kw, kh);
        keyChip(g, kl, kx, ky, kw, kh, kfs, waiting, khv, mi);
        toggle(g, ckX, y, ckS, head, on, hk);
        if (hasP) {
            // кнопка раскрытия: контурная «таблетка» со стрелкой вниз
            boolean gh = hv && hover(gx, Math.round(sy + (ky - y)), gs, kh);
            if (gh) {
                hot(HOT_GEAR + mi);
            }
            float gk = an(MenuAnim.MODULE_BASE + mi * MenuAnim.MOD_STRIDE + MenuAnim.MOD_GEAR, gh ? 1 : 0, 18);
            float act = Math.max(gk, sel);
            if (act > .01f) {
                Px.round(g, gx, ky, gs, kh, kh / 2, Px.a(lt ? 0x1B1530 : 0xFFFFFF, (int) (12 * gk + 6 * sel)));
            }
            Px.outline(g, gx, ky, gs, kh, kh / 2, Px.mix(lt ? Px.a(T().text(), 26) : 0x18FFFFFF, Px.a(MenuTheme.accent(), 190), sel));
            int cs = S(12);
            g.pose().pushPose();
            Px.translate(g, gx + gs / 2f, ky + kh / 2f);
            Px.rotate2d(g, (float) (Math.PI / 2 * sel));
            glyph(g, "chev-right", -cs / 2, -cs / 2, cs, Px.mix(lt(T().muted(), 0xFF757D8A), accText(), act));
            g.pose().popPose();
        }
        g.pose().popPose();
        Px.fade = k;
        if (under() || app < .3f) {
            return;
        }
        clipped(x, y, (hasP ? gx : kx) - S(4) - x, head, sc, top, bottom,
                ACT_MODULE_TOGGLE, hasP ? ACT_MODULE_OPEN : ACT_NONE, mi);
        if (hasP) {
            clipped(gx - S(3), ky - S(3), gs + S(6), kh + S(6), sc, top, bottom, ACT_MODULE_OPEN, ACT_MODULE_OPEN, mi);
        }
        clipped(ckX - S(4), y, x + w - ckX + S(4), head, sc, top, bottom, ACT_MODULE_TOGGLE, ACT_NONE, mi);
        clipped(kx - S(3), ky - S(3), kw + S(6), kh + S(6), sc, top, bottom,
                ACT_MODULE_KEY, ACT_MODULE_KEY_RESET, mi);
    }

    private void keyChip(GuiGraphics g, String t, int x, int y, int w, int h, int fs, boolean waiting, boolean hv, int mi) {
        boolean lt = light();
        float wk = tw(MenuAnim.MODULE_BASE + mi * MenuAnim.MOD_STRIDE + MenuAnim.MOD_KEY_WAIT, waiting ? 1 : 0, 200);
        float pulse = (float) (Math.sin(now / 220.0) * .5 + .5) * wk;
        int bg = Px.mix(
                Px.a(lt ? 0x1B1530 : 0xFFFFFF, hv ? 12 : 0),
                Px.a(MenuTheme.accent(), (int) ((lt ? 26 : 38) + 22 * pulse)),
                wk);
        if ((bg >>> 24) != 0) {
            Px.round(g, x, y, w, h, h / 2, bg);
        }
        Px.outline(g, x, y, w, h, h / 2,
                Px.mix(lt ? Px.a(T().text(), 24) : 0x18FFFFFF, Px.a(MenuTheme.accent(), (int) (160 + 90 * pulse)), wk));
        g.enableScissor(x, y, x + w, y + h);
        float kf = Px.fade;
        if (t.isEmpty()) {
            int gs = S(20);
            int gx = x + (w - gs) / 2;
            int gy = y + (h - gs) / 2;
            Px.icon(g, "a.keyboard-s~", gx, gy, gs, Px.a(MenuTheme.accent(), hv ? 120 : 70));
            Px.icon(g, "a.keyboard-s", gx, gy, gs, hv ? T().text() : T().muted());
        } else if (waiting) {
            Px.fade = kf * wk;
            Px.textCenter(g, t, x + w / 2, y + (h - fs) / 2, Weight.SEMIBOLD, fs, accText());
            Px.fade = kf;
        } else {
            Px.textCenter(g, t, x + w / 2, y + (h - fs) / 2, Weight.SEMIBOLD, fs,
                    hv ? T().text() : Px.mix(T().muted(), T().text(), .45f));
        }
        g.disableScissor();
    }

    private void nameLit(GuiGraphics g, String name, int x, int y, int fs, int c) {
        String lower = name.toLowerCase(java.util.Locale.ROOT);
        String q = search.toLowerCase(java.util.Locale.ROOT);
        int i = lower.indexOf(q);
        if (i < 0) {
            Px.text(g, name, x, y, Weight.MEDIUM, fs, c);
            return;
        }
        String a = name.substring(0, i);
        String b = name.substring(i, Math.min(name.length(), i + search.length()));
        String r = name.substring(Math.min(name.length(), i + search.length()));
        Px.text(g, a, x, y, Weight.MEDIUM, fs, c);
        x += Px.textWidth(a, Weight.MEDIUM, fs);
        Px.text(g, b, x, y, Weight.SEMIBOLD, fs, accText());
        x += Px.textWidth(b, Weight.SEMIBOLD, fs);
        Px.text(g, r, x, y, Weight.MEDIUM, fs, c);
    }

    private void openPopup(Module m) {
        if (popMod == m) {
            popMod = null;
            MenuSound.play(MenuSound.POP, .8f, .6f);
            return;
        }
        if (popShown != m) {
            anim.set(MenuAnim.POPUP, 0);
        }
        popMod = m;
        binding = null;
        bindingSetting = null;
        pop = null;
        scroll[7] = 0f;
        anim.set(MenuAnim.SCROLL_BASE + 7, 0);
        MenuSound.play(MenuSound.POP);
    }

    // ---- настройки меню (группа: иконка, заголовок, подзаголовок, строки)
    private static final int PREF_SOUNDS = 0;
    private static final int PREF_HOVER_SOUNDS = 1;
    private static final int PREF_ANIMATIONS = 2;
    private static final int PREF_SCALE = 3;
    private static final int PREFS_SOUND = 0;
    private static final int PREFS_THEME = 1;
    private static final int PREFS_BEHAVIOR = 2;
    private static final int PREF_SCROLL = 9;
    private static final float PREF_SCALE_MIN = .7f;
    private static final float PREF_SCALE_MAX = 1.4f;

    private record PrefGroup(String icon, String title, String subtitle, int[] rows) {
    }

    private static final PrefGroup[] PREFS = {
            new PrefGroup("w.headset", "Звук", "Щелчки и отклик меню", new int[]{PREF_SOUNDS, PREF_HOVER_SOUNDS}),
            new PrefGroup("w.swatch", "Вид", "Цветовые схемы меню", new int[0]),
            new PrefGroup("w.bolt", "Поведение", "Анимации и размер меню", new int[]{PREF_ANIMATIONS, PREF_SCALE}),
    };

    private final int[] prefColY = new int[2];
    private final int[] prefGx = new int[3];
    private final int[] prefGy = new int[3];
    private final int[] prefGh = new int[3];
    private final float[] prefSliderX = new float[8];
    private final float[] prefSliderW = new float[8];

    // ---- док страниц
    private void dock(GuiGraphics g, int cx, int edge) {
        int bs = S(44);
        int gap = S(8);
        int pad = S(6);
        int n = DOCK.length;
        int w = n * bs + (n - 1) * gap + pad * 2;
        int h = bs + pad * 2;
        int x = cx - w / 2;
        int y = edge - h / 2;
        boolean lt = light();
        Px.round(g, x, y, w, h, h / 2, lt ? 0xFFFFFFFF : Px.mix(T().bg() | 0xFF000000, 0xFFFFFFFF, .025f));
        Px.outline(g, x, y, w, h, h / 2, Px.a(T().text(), lt ? 30 : 22));
        int activeIdx;
        if (page == Page.SETTINGS) {
            activeIdx = 3;
        } else if (categoryLocked) {
            activeIdx = category == ModuleCategory.RENDER ? 1 : 2;
        } else {
            activeIdx = 0;
        }
        int ty = y + pad;
        float tx = tw(MenuAnim.DOCK_X, x + pad + activeIdx * (bs + gap), 280);
        int txi = Math.round(tx);
        Px.circle(g, txi + bs / 2, ty + bs / 2, bs / 2, acc());
        for (int i = 0; i < n; i++) {
            int bx = x + pad + i * (bs + gap);
            boolean hv = !under() && hover(bx, ty, bs, bs);
            if (hv) {
                hot(HOT_DOCK + i);
            }
            float hk = an(MenuAnim.DOCK_HOVER + i, hv ? 1 : 0, 18);
            float lit = Math.max(0, 1 - Math.abs(tx - bx) / (float) (bs + gap));
            if (hk > .01f) {
                Px.circle(g, bx + bs / 2, ty + bs / 2, bs / 2, Px.a(T().text(), (int) (12 * hk * (1 - lit))));
            }
            int gs = S(20);
            glyph(g, DOCK[i][0], bx + (bs - gs) / 2, ty + (bs - gs) / 2, gs,
                    Px.mix(Px.mix(lt(0xFF8E89A0, 0xFF857E98), T().text(), hk), 0xFFFFFFFF, lit));
            if (hk > .02f) {
                // подпись под доком
                String t = DOCK[i][1];
                int tw2 = Px.textWidth(t, Weight.MEDIUM, S(12)) + S(20);
                int th2 = S(26);
                int lx = bx + bs / 2 - tw2 / 2;
                int ly = y + h + S(8) - Math.round((1 - hk) * S(4));
                float kf = Px.fade;
                Px.fade = kf * hk;
                Px.round(g, lx, ly, tw2, th2, th2 / 2, lt ? 0xFFFFFFFF : Px.mix(T().bg() | 0xFF000000, 0xFFFFFFFF, .06f));
                Px.outline(g, lx, ly, tw2, th2, th2 / 2, Px.a(T().text(), lt ? 26 : 22));
                Px.textCenter(g, t, bx + bs / 2, ly + (th2 - S(12)) / 2, Weight.MEDIUM, S(12), T().text());
                Px.fade = kf;
            }
            if (!under()) {
                region(bx, ty, bs, bs, ACT_DOCK, i);
            }
        }
    }

    private void dockClick(int i) {
        Page to = i == 3 ? Page.SETTINGS : Page.MODULES;
        if (i == 1) {
            category = ModuleCategory.RENDER;
            categoryLocked = true;
        } else if (i == 2) {
            category = ModuleCategory.INTERFACE;
            categoryLocked = true;
        } else if (i == 0) {
            categoryLocked = false;
        }
        if (page == to && (i == 0 || i == 3)) {
            return;
        }
        page = to;
        search = "";
        searchFocused = false;
        content(MenuSound.PAGE);
    }

    // ---- страница настроек меню (порт PrefsPage из Arcanic: группы в две колонки)
    private void settingsPage(GuiGraphics g) {
        int gap = S(12);
        int cw = (cw0 - gap) / 2;
        int n = PREFS.length;
        int[] colY = prefColY;
        colY[0] = 0;
        colY[1] = 0;
        int[] gx = prefGx;
        int[] gy = prefGy;
        int[] gh = prefGh;
        for (int i = 0; i < n; i++) {
            int c = colY[0] <= colY[1] ? 0 : 1;
            gx[i] = cx0 + c * (cw + gap);
            gy[i] = top + S(2) + colY[c];
            gh[i] = groupH(i, cw);
            colY[c] += gh[i] + gap;
        }
        int total = Math.max(colY[0], colY[1]) + S(4);
        float sc = Math.round(scrolled(PREF_SCROLL, total, top, bottom));
        g.enableScissor(cx0 - S(12), top, cx0 + cw0 + S(12), bottom);
        g.pose().pushPose();
        Px.translate(g, 0, -sc);
        for (int i = 0; i < n; i++) {
            float app = ease((now - contentAt - i * 40L) / 300f);
            float k = Px.fade;
            Px.fade = k * app;
            g.pose().pushPose();
            Px.translate(g, 0, Math.round((1 - app) * S(8)));
            prefGroup(g, i, gx[i], gy[i], cw, gh[i], sc);
            g.pose().popPose();
            Px.fade = k;
        }
        g.pose().popPose();
        g.disableScissor();
        edges(g, cx0 - S(12), cw0 + S(24), top, bottom, sc, baseColour());
        scrollbar(g, cx0 + cw0 + S(14), top, bottom, total, sc, PREF_SCROLL);
    }

    /** Высота карточки группы настроек. */
    private int groupH(int group, int cw) {
        int h = S(64);
        if (group == PREFS_THEME) {
            return h + S(76) + S(10);
        }
        int[] rows = PREFS[group].rows;
        for (int i = 0; i < rows.length; i++) {
            h += prefRowH(rows[i]);
        }
        return h + S(10);
    }

    private static int prefRowH(int pref) {
        return pref == PREF_SCALE ? S(64) : S(40);
    }

    /** Карточка группы: иконка, заголовок, подзаголовок и строки настроек. */
    private void prefGroup(GuiGraphics g, int group, int x, int y, int w, int h, float sc) {
        boolean lt = light();
        int pad = S(18);
        int R = S(12);
        Px.round(g, x, y, w, h, R, plate());
        Px.outline(g, x, y, w, h, R, lt ? Px.a(T().text(), 22) : 0x18FFFFFF);
        int ib = S(34);
        Px.round(g, x + pad, y + S(15), ib, ib, S(10), Px.a(MenuTheme.accent(), lt ? 22 : 34));
        glyph(g, PREFS[group].icon, x + pad + (ib - S(17)) / 2, y + S(15) + (ib - S(17)) / 2, S(17), accText());
        Px.text(g, PREFS[group].title, x + pad + ib + S(12), y + S(16), Weight.SEMIBOLD, S(15), T().text());
        Px.text(g, PREFS[group].subtitle, x + pad + ib + S(12), y + S(36), Weight.MEDIUM, S(12), lt(T().dim(), 0xFF757D8A));
        Px.rect(g, x + pad, y + S(62), w - pad * 2, Math.max(1, S(1)), lt ? Px.a(T().text(), 18) : 0x12FFFFFF);
        int ry = y + S(64);
        if (group == PREFS_THEME) {
            paletteStrip(g, x + pad, ry, w - pad * 2, sc);
            return;
        }
        int[] rows = PREFS[group].rows;
        for (int i = 0; i < rows.length; i++) {
            prefRow(g, rows[i], x + pad, ry, w - pad * 2, prefRowH(rows[i]), sc, top, bottom);
            ry += prefRowH(rows[i]);
        }
    }

    /** Строка настройки меню: подпись слева, значение справа. */
    private void prefRow(GuiGraphics g, int pref, int x, int y, int w, int h, float sc, int t, int b) {
        boolean hv = !blocked() && my >= t && my < b && hover(x, Math.round(y - sc), w, h);
        if (hv) {
            hot(HOT_PREF + pref);
        }
        int animBase = MenuAnim.PREFS_BASE + pref * 2;
        float hk = an(animBase, hv ? 1 : 0, 18);
        switch (pref) {
            case PREF_SOUNDS -> {
                Px.text(g, "Звуки меню", x, y + (h - S(14)) / 2, Weight.MEDIUM, S(14), T().text());
                toggle(g, x + w - S(44), y + (h - S(24)) / 2, S(44), S(24), MenuTheme.sounds ? 1 : 0, hk);
                if (!blocked()) {
                    region(x, y, w, h, ACT_PREF_BOOL, pref);
                }
            }
            case PREF_HOVER_SOUNDS -> {
                Px.text(g, "Звуки наведения", x, y + (h - S(14)) / 2, Weight.MEDIUM, S(14), T().text());
                toggle(g, x + w - S(44), y + (h - S(24)) / 2, S(44), S(24), MenuTheme.hoverSounds ? 1 : 0, hk);
                if (!blocked()) {
                    region(x, y, w, h, ACT_PREF_BOOL, pref);
                }
            }
            case PREF_ANIMATIONS -> {
                Px.text(g, "Анимации", x, y + (h - S(14)) / 2, Weight.MEDIUM, S(14), T().text());
                toggle(g, x + w - S(44), y + (h - S(24)) / 2, S(44), S(24), MenuTheme.animations ? 1 : 0, hk);
                if (!blocked()) {
                    region(x, y, w, h, ACT_PREF_BOOL, pref);
                }
            }
            case PREF_SCALE -> {
                Px.text(g, "Размер меню", x, y + S(6), Weight.MEDIUM, S(14), T().text());
                float v = MenuTheme.menuScale;
                float vk = an(animBase + 1, hk > .5f ? 1 : 0, 16);
                Px.textRight(g, Math.round(v * 100) + "%", x + w, y + S(6), Weight.SEMIBOLD, S(13),
                        Px.mix(accText(), lt(T().text(), 0xFFFFFFFF), vk));
                int th = Math.max(2, S(3));
                int kr = S(6);
                int ty = y + h - S(22);
                float f = (v - PREF_SCALE_MIN) / (PREF_SCALE_MAX - PREF_SCALE_MIN);
                int kx = x + kr + Math.round((w - kr * 2) * f);
                prefSliderX[pref] = x + kr;
                prefSliderW[pref] = w - kr * 2;
                Px.round(g, x, ty, w, th, th / 2, light() ? 0xFFDEDDE6 : Px.a(0xFFFFFF, 24));
                Px.round(g, x, ty, Math.max(th, kx - x), th, th / 2, acc());
                float kh = an(animBase + 1, hv ? .6f : 0, 20);
                int outer = kr + Math.round(S(1.5f) * kh);
                int inner = Math.max(1, Math.round(outer * .34f));
                Px.circle(g, kx, ty + th / 2, outer, acc());
                Px.circle(g, kx, ty + th / 2, inner, light() ? 0xFFFFFFFF : 0xFF12141C);
                if (!blocked()) {
                    int y0 = (int) Math.max(t, ty - S(14) - sc);
                    int y1 = (int) Math.min(b, ty + S(20) - sc);
                    if (y1 > y0) {
                        region(x - S(10), y0, w + S(20), y1 - y0, ACT_PREF_SLIDER, pref);
                    }
                }
            }
            default -> {
            }
        }
    }

    /** Полоса палитр: квадрат каждой темы в рамке, выбранная подсвечена акцентом. */
    private void paletteStrip(GuiGraphics g, int x, int y, int w, float sc) {
        int n = MenuTheme.PALETTES.length;
        int cell = S(46);
        int gap = S(10);
        int perRow = Math.max(1, (w + gap) / (cell + gap));
        for (int i = 0; i < n; i++) {
            int c = i % perRow;
            int r = i / perRow;
            int cx = x + c * (cell + gap);
            int cy = y + r * (cell + gap);
            if (cy + cell > bottom + S(20)) {
                continue;
            }
            MenuTheme.Palette p = MenuTheme.PALETTES[i];
            boolean sel = MenuTheme.palette == i;
            boolean hv = !blocked() && hover(cx, cy, cell, cell);
            if (hv) {
                hot(HOT_PALETTE + i);
            }
            Px.round(g, cx, cy, cell, cell, S(10), p.bg() | 0xFF000000);
            Px.round(g, cx, cy, cell, cell, S(10), p.a() | 0xFF000000);
            Px.outline(g, cx, cy, cell, cell, S(10), sel ? Px.a(MenuTheme.accent(), 220) : p.border());
            if (sel) {
                Px.outline(g, cx - S(2), cy - S(2), cell + S(4), cell + S(4), S(12), Px.a(MenuTheme.accent(), 120));
            }
            if (!blocked()) {
                region(cx, cy, cell, cell, ACT_PALETTE, i);
            }
        }
    }


    // ---- окно настроек модуля (всплывающее)
    int valueBox(GuiGraphics g, String v, int right, int y, boolean accent, boolean hv) {
        int bw = Math.max(S(compact ? 44 : 52), Px.textWidth(v, Weight.MEDIUM, S(13)) + S(compact ? 18 : 22));
        int bh = S(compact ? 26 : 30);
        int bx = right - bw;
        Px.round(g, bx, y, bw, bh, S(5), accent ? Px.a(MenuTheme.accent(), light() ? 26 : 40) : inset(hv));
        Px.outline(g, bx, y, bw, bh, S(5), accent ? acc() : Px.a(T().text(), light() ? 30 : 16));
        Px.textCenter(g, v, bx + bw / 2, y + (bh - S(13)) / 2, Weight.MEDIUM, S(13), accent ? accText() : T().text());
        return bw;
    }

    private boolean segmented(ModeSetting md, int w) {
        int n = md.getMods().size();
        if (n < 2 || n > 3) {
            return false;
        }
        int seg = (w - S(8)) / n;
        for (int i = 0; i < n; i++) {
            if (Px.textWidth(md.getMods().get(i), Weight.MEDIUM, S(12)) > seg - S(18)) {
                return false;
            }
        }
        return true;
    }

    private static String numText(FloatSetting n, double v) {
        String s = n.getIncrement() >= 1
                ? String.valueOf((long) Math.round(v))
                : String.format(java.util.Locale.ROOT, n.getIncrement() >= .1 ? "%.1f" : "%.2f", v);
        return s;
    }

    int rowH(Setting s, int w) {
        if (s instanceof FloatSetting) {
            return S(compact ? 66 : 76);
        }
        if (s instanceof ModeSetting md && segmented(md, w)) {
            return S(compact ? 70 : 82);
        }
        if (s instanceof ListSetting) {
            return S(compact ? 66 : 76);
        }
        return S(compact ? 40 : 46);
    }

    int settings(GuiGraphics g, Module m, int x, int y, int w, float sc, int t, int b, boolean live) {
        int mi = moduleIndex(m);
        List<Setting> list = m.getSettings();
        for (int i = 0; i < list.size(); i++) {
            Setting s = list.get(i);
            if (!s.visible()) {
                continue;
            }
            int h = rowH(s, w);
            if (y - sc + h >= t && y - sc <= b) {
                row(g, m, s, x, y, w, h, sc, t, b, live, mi, i);
            }
            y += h;
        }
        return y;
    }

    private void row(GuiGraphics g, Module m, Setting s, int x, int y, int w, int h, float sc, int t, int b,
                     boolean live, int mi, int si) {
        int siGlobal = settingIndex(mi, si);
        int animBase = MenuAnim.SETTING_BASE + siGlobal * MenuAnim.SET_STRIDE;
        boolean hv = live && my >= t && my < b && hover(x, Math.round(y - sc), w, h);
        if (hv) {
            hot(HOT_SET + siGlobal);
        }
        float hk = an(animBase + MenuAnim.SET_ROW, hv ? 1 : 0, 18);
        int label = T().text();
        String name = Px.ellipsize(s.name(), Weight.MEDIUM, S(14), w - S(110));
        if (s instanceof BooleanSetting bo) {
            Px.text(g, name, x, y + (h - S(14)) / 2, Weight.MEDIUM, S(14), label);
            float on = tw(animBase + MenuAnim.SET_BOOL, bo.get() ? 1 : 0, 200);
            toggle(g, x + w - S(44), y + (h - S(24)) / 2, S(44), S(24), on, hk);
            if (live) {
                clipped(x, y, w, h, sc, t, b, ACT_BOOL, siGlobal);
            }
        } else if (s instanceof FloatSetting n) {
            // числовая строка: имя, значение, дорожка с бегунком и границы
            int fsN = compact ? S(13) : S(14);
            int ly = y + S(10);
            Px.text(g, name, x, ly, Weight.MEDIUM, fsN, label);
            float vk = an(animBase + MenuAnim.SET_NUM, hk > .5f ? 1 : 0, 16);
            Px.textRight(g, numText(n, n.get()), x + w, ly, Weight.SEMIBOLD, S(13),
                    Px.mix(accText(), lt(T().text(), 0xFFFFFFFF), vk));
            int th = Math.max(2, S(3));
            int kr = S(6);
            int tx = x;
            int tw2 = w;
            int ty = y + h - S(compact ? 24 : 28);
            int rs = S(10);
            int rc = lt(T().dim(), 0xFF5A616C);
            Px.text(g, numText(n, n.getMin()), tx, ty + S(9), Weight.MEDIUM, rs, rc);
            Px.textRight(g, numText(n, n.getMax()), tx + tw2, ty + S(9), Weight.MEDIUM, rs, rc);
            float f = (n.get() - n.getMin()) / Math.max(.0001f, n.getMax() - n.getMin());
            int kx = tx + kr + Math.round((tw2 - kr * 2) * f);
            int kcy = ty + th / 2;
            sliderX[siGlobal] = tx + kr;
            sliderW[siGlobal] = tw2 - kr * 2;
            Px.round(g, tx, ty, tw2, th, th / 2, light() ? 0xFFDEDDE6 : Px.a(0xFFFFFF, 24));
            Px.round(g, tx, ty, Math.max(th, kx - tx), th, th / 2, acc());
            float kh = an(animBase + MenuAnim.SET_NUM_KNOB, hv ? .6f : 0, 20);
            int outer = kr + Math.round(S(1.5f) * kh);
            int inner = Math.max(1, Math.round(outer * .34f));
            Px.circle(g, kx, kcy, outer, acc());
            Px.circle(g, kx, kcy, inner, light() ? 0xFFFFFFFF : 0xFF12141C);
            if (live) {
                int y0 = (int) Math.max(t, ty - S(14) - sc);
                int y1 = (int) Math.min(b, ty + S(20) - sc);
                if (y1 > y0) {
                    region(tx - S(10), y0, tw2 + S(20), y1 - y0, ACT_SLIDER, siGlobal);
                }
            }
        } else if (s instanceof ModeSetting md) {
            if (segmented(md, w)) {
                Px.text(g, name, x, y + S(12), Weight.MEDIUM, S(14), label);
                int sh = S(compact ? 28 : 32);
                int sy = y + h - sh - S(compact ? 8 : 10);
                int n = md.getMods().size();
                int in = S(4);
                float segW = (w - in * 2) / (float) n;
                Px.round(g, x, sy, w, sh, S(6), lt(0xFFEFEFF4, inset(false)));
                Px.outline(g, x, sy, w, sh, S(6), Px.a(T().text(), light() ? 20 : 14));
                float thumb = tw(animBase + MenuAnim.SET_SEGMENT, md.getIndex(), 240);
                int t0 = x + in + Math.round(segW * thumb);
                int t1 = x + in + Math.round(segW * (thumb + 1));
                Px.round(g, t0, sy + in, t1 - t0, sh - in * 2, S(4), acc());
                for (int i = 0; i < n; i++) {
                    String mode = md.getMods().get(i);
                    float a = Math.max(0, 1 - Math.abs(thumb - i));
                    int s0 = x + in + Math.round(segW * i);
                    int s1 = x + in + Math.round(segW * (i + 1));
                    boolean sh2 = live && hover(s0, Math.round(sy - sc), s1 - s0, sh);
                    if (sh2) {
                        hot(HOT_SEG + siGlobal * 16 + i);
                    }
                    Px.textCenter(g, mode, (s0 + s1) / 2, sy + (sh - S(12)) / 2,
                            a > .5f ? Weight.SEMIBOLD : Weight.MEDIUM, S(12),
                            Px.mix(sh2 ? T().text() : T().muted(), 0xFFFFFFFF, a));
                    if (live && md.getIndex() != i) {
                        clipped(s0, sy, s1 - s0, sh, sc, t, b, ACT_MODE_ITEM, siGlobal * 16 + i);
                    }
                }
            } else {
                Px.text(g, name, x, y + (h - S(14)) / 2, Weight.MEDIUM, S(14), label);
                String v = md.getCurrent();
                int vw = Math.min(w / 2, Px.textWidth(v, Weight.MEDIUM, S(13)) + S(42));
                int vh = S(30);
                int vx = x + w - vw;
                int vy = y + (h - vh) / 2;
                boolean open = pop == md;
                Px.round(g, vx, vy, vw, vh, S(5), open ? Px.a(MenuTheme.accent(), light() ? 22 : 36) : inset(hk > .5f));
                Px.outline(g, vx, vy, vw, vh, S(5), open ? acc() : Px.a(T().text(), light() ? 30 : 16));
                Px.text(g, Px.ellipsize(v, Weight.MEDIUM, S(13), vw - S(40)), vx + S(12), vy + (vh - S(13)) / 2,
                        Weight.MEDIUM, S(13), T().text());
                float turn = tw(animBase + MenuAnim.SET_DROPDOWN, open ? 1 : 0, 200);
                int cs = S(12);
                g.pose().pushPose();
                Px.translate(g, vx + vw - S(18), vy + vh / 2f);
                Px.rotate2d(g, (float) (Math.PI / 2 - Math.PI * turn));
                glyph(g, "chev-right", -cs / 2, -cs / 2, cs, Px.mix(lt(T().muted(), 0xFF757D8A), accText(), turn));
                g.pose().popPose();
                if (live) {
                    clipped(x, y, w, h, sc, t, b, ACT_MODE, siGlobal);
                }
            }
        } else if (s instanceof ListSetting ls) {
            Px.text(g, name, x, y + S(10), Weight.MEDIUM, S(14), label);
            List<BooleanSetting> options = ls.getSettings();
            int ox = x;
            int oy = y + S(30);
            for (int i = 0; i < options.size(); i++) {
                BooleanSetting option = options.get(i);
                String on = option.name();
                int bw = Px.textWidth(on, Weight.MEDIUM, S(12)) + S(20);
                boolean ohv = live && hover(ox, oy - sc, bw, S(26));
                Px.round(g, ox, oy, bw, S(26), S(6), option.get() ? Px.a(MenuTheme.accent(), light() ? 26 : 40) : inset(ohv));
                Px.outline(g, ox, oy, bw, S(26), S(6), option.get() ? acc() : Px.a(T().text(), light() ? 30 : 16));
                Px.textCenter(g, on, ox + bw / 2, oy + (S(26) - S(12)) / 2, Weight.MEDIUM, S(12),
                        option.get() ? accText() : T().text());
                if (live) {
                    clipped(ox, oy, bw, S(26), sc, t, b, ACT_LIST_ITEM, siGlobal * 16 + i);
                }
                ox += bw + S(8);
                if (ox > x + w - S(40)) {
                    ox = x;
                    oy += S(32);
                }
            }
        } else if (s instanceof BindSetting bs) {
            Px.text(g, name, x, y + (h - S(14)) / 2, Weight.MEDIUM, S(14), label);
            boolean cap = bindingSetting == bs;
            String kt = cap ? "Нажми клавишу" : bs.getKey() >= 0 ? KeyBoardUtils.getBindName(bs.getKey()) : "—";
            valueBox(g, kt, x + w, y + (h - S(26)) / 2, cap, hv > .5f);
            if (live) {
                clipped(x, y, w, h, sc, t, b, ACT_BIND_SETTING, siGlobal);
            }
        } else if (s instanceof TextSetting ts) {
            Px.text(g, name, x, y + (h - S(14)) / 2, Weight.MEDIUM, S(14), label);
            valueBox(g, ts.get(), x + w, y + (h - S(26)) / 2, false, hv > .5f);
        }
    }

    private void popover(GuiGraphics g) {
        ModeSetting md = pop;
        float a = ease((now - popAt) / 170f);
        int ih = S(38);
        int rows = Math.min(md.getMods().size(), 8);
        int h = ih * rows + S(12);
        int x = popX;
        int y = Math.min(popY, Px.height() - h - S(12));
        int max = Math.max(0, (md.getMods().size() - rows) * ih);
        float msc = Math.max(0, Math.min(max, scroll[8]));
        scroll[8] = msc;
        float k = Px.fade;
        Px.fade = k * a;
        g.pose().pushPose();
        if (a < .999f) {
            Px.translate(g, x + popW, y);
            Px.scale(g, .95f + .05f * a, .95f + .05f * a);
            Px.translate(g, -(x + popW), -y + (1 - a) * -S(6));
        }
        boolean lt = light();
        Px.shadow(g, x, y + S(6), popW, h, S(18), Px.a(0, lt ? 30 : 70));
        Px.round(g, x, y, popW, h, S(12), lt ? 0xFFFFFFFF : 0xF20C0D12);
        Px.outline(g, x, y, popW, h, S(12), lt ? Px.a(T().text(), 26) : 0x18FFFFFF);
        g.enableScissor(x, y + S(6), x + popW, y + h - S(6));
        for (int i = 0; i < md.getMods().size(); i++) {
            String mode = md.getMods().get(i);
            int iy = Math.round(y + S(6) + i * ih - msc);
            if (iy + ih < y || iy > y + h) {
                continue;
            }
            boolean hv = hover(x + S(6), iy, popW - S(12), ih) && my > y && my < y + h;
            if (hv) {
                hot(HOT_POPOVER + i);
            }
            float hk = an(MenuAnim.SCROLL_BASE + MenuAnim.SCROLL_POPOVER + (i % 8), hv ? 1 : 0, 20);
            if (hk > .01f) {
                Px.round(g, x + S(6), iy, popW - S(12), ih, S(5), Px.a(T().text(), (int) ((lt ? 12 : 14) * hk)));
            }
            boolean cur = md.is(mode);
            if (cur) {
                glyph(g, "check", x + S(16), iy + (ih - S(14)) / 2, S(14), accText());
            }
            Px.text(g, Px.ellipsize(mode, Weight.MEDIUM, S(13), popW - S(56)), x + S(40), iy + (ih - S(13)) / 2,
                    cur ? Weight.SEMIBOLD : Weight.MEDIUM, S(13), cur ? T().text() : Px.mix(T().muted(), T().text(), hk));
            int y0 = Math.max(iy, y + S(6));
            int y1 = Math.min(iy + ih, y + h - S(6));
            if (y1 > y0) {
                region(x + S(6), y0, popW - S(12), y1 - y0, ACT_POPOVER_ITEM, i);
            }
        }
        g.disableScissor();
        g.pose().popPose();
        Px.fade = k;
    }

    // ---- индексы модулей и настроек
    int moduleIndex(Module m) {
        List<Module> all = ModuleManager.getModules();
        for (int i = 0, size = all.size(); i < size; i++) {
            if (all.get(i) == m) {
                return i;
            }
        }
        return 0;
    }

    static int settingIndex(int moduleIndex, int settingIndex) {
        return Math.min(MenuAnim.MAX_SETTINGS - 1, moduleIndex * 16 + settingIndex);
    }

    private static boolean hasSettings(Module m) {
        List<Setting> list = m.getSettings();
        for (int i = 0, size = list.size(); i < size; i++) {
            if (list.get(i).visible()) {
                return true;
            }
        }
        return false;
    }

    private static Setting settingByIndex(Module m, int index) {
        List<Setting> list = m.getSettings();
        return index >= 0 && index < list.size() ? list.get(index) : null;
    }

    // ---- ввод

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        double x = Px.px(mouseX);
        double y = Px.px(mouseY);
        if (binding != null && button >= 2) {
            binding.setKey(KeyBoardUtils.createMouseBind(button - 1));
            binding = null;
            MenuSound.play(MenuSound.SELECT);
            return true;
        }
        if (bindingSetting != null && button >= 2) {
            bindingSetting.setKey(KeyBoardUtils.createMouseBind(button - 1));
            bindingSetting = null;
            MenuSound.play(MenuSound.SELECT);
            return true;
        }
        if (pop != null) {
            for (int i = regionCount - 1; i >= 0; i--) {
                if (regionAction[i] == ACT_POPOVER_ITEM && inRegion(i, x, y)) {
                    runAction(regionAction[i], regionPayload[i]);
                    return true;
                }
            }
            pop = null;
            MenuSound.play(MenuSound.POP, .8f, .6f);
            return true;
        }
        for (int i = regionCount - 1; i >= 0; i--) {
            if (!inRegion(i, x, y)) {
                continue;
            }
            int action = regionAction[i];
            if (action == ACT_SCROLL) {
                dragging = true;
                dragAction = action;
                dragPayload = regionPayload[i];
                MenuSound.play(MenuSound.GRAB, 1, .6f);
                return true;
            }
            if (button == 0) {
                searchFocused = false;
                runAction(action, regionPayload[i]);
                return true;
            }
            if (button == 1 && regionAlt[i] != ACT_NONE) {
                runAction(regionAlt[i], regionPayload[i]);
                return true;
            }
        }
        searchFocused = false;
        binding = null;
        bindingSetting = null;
        if (popMod != null) {
            popMod = null;
            MenuSound.play(MenuSound.POP, .8f, .6f);
        }
        return true;
    }

    private boolean dragging;
    private int dragAction;
    private int dragPayload;

    private boolean inRegion(int i, double x, double y) {
        return x >= regionX[i] && x < regionX[i] + regionW[i] && y >= regionY[i] && y < regionY[i] + regionH[i];
    }

    private void runAction(int action, int payload) {
        switch (action) {
            case ACT_TAB -> tabClick(payload);
            case ACT_THEME -> {
                MenuTheme.setPalette(MenuTheme.palette == MenuTheme.LIGHT ? MenuTheme.NIGHT : MenuTheme.LIGHT);
                MenuSound.play(MenuSound.SELECT);
            }
            case ACT_PALETTE -> {
                MenuTheme.setPalette(payload);
                MenuSound.play(MenuSound.SELECT);
            }
            case ACT_SEARCH -> {
                searchFocused = true;
                MenuSound.play(MenuSound.CLICK, 1.2f, .6f);
            }
            case ACT_SEARCH_CLEAR -> {
                search = "";
                content(MenuSound.CLICK);
            }
            case ACT_MODULE_TOGGLE -> {
                Module m = moduleByIndex(payload);
                if (m != null) {
                    m.toggle();
                    MenuSound.play(m.isEnable() ? MenuSound.CHARGE : MenuSound.DRAIN);
                }
            }
            case ACT_MODULE_OPEN -> {
                Module m = moduleByIndex(payload);
                if (m != null) {
                    openPopup(m);
                }
            }
            case ACT_MODULE_KEY -> {
                Module m = moduleByIndex(payload);
                if (m != null) {
                    binding = binding == m ? null : m;
                    MenuSound.play(MenuSound.CLICK);
                }
            }
            case ACT_DOCK -> dockClick(payload);
            case ACT_POPUP_TOGGLE -> {
                Module m = moduleByIndex(payload);
                if (m != null) {
                    m.toggle();
                    MenuSound.play(m.isEnable() ? MenuSound.CHARGE : MenuSound.DRAIN);
                }
            }
            case ACT_POPUP_KEY -> {
                Module m = moduleByIndex(payload);
                if (m != null) {
                    binding = binding == m ? null : m;
                    MenuSound.play(MenuSound.CLICK);
                }
            }
            case ACT_POPUP_KEY_RESET -> {
                Module m = moduleByIndex(payload);
                if (m != null) {
                    m.setKey(-1);
                    binding = null;
                    MenuSound.play(MenuSound.RESET);
                }
            }
            case ACT_BOOL -> {
                Setting s = settingByGlobal(payload);
                if (s instanceof BooleanSetting bo) {
                    bo.setState(!bo.get());
                    MenuSound.play(bo.get() ? MenuSound.TOGGLE_ON : MenuSound.TOGGLE_OFF);
                }
            }
            case ACT_SLIDER -> {
                // значение выставится в mouseDragged по текущей позиции курсора
                dragAction = ACT_SLIDER;
                dragPayload = payload;
                dragging = true;
                applySlider(payload, mx);
            }
            case ACT_MODE -> {
                Setting s = settingByGlobal(payload);
                if (s instanceof ModeSetting md) {
                    pop = pop == md ? null : md;
                    if (pop != null) {
                        popW = Math.max(S(200), S(44));
                        popX = cx0 + cw0 - popW;
                        popY = (int) (my + S(10));
                        popAt = now;
                        MenuSound.play(MenuSound.POP);
                    }
                }
            }
            case ACT_MODE_ITEM -> {
                int si = payload / 16;
                int mi = payload % 16;
                Setting s = settingByGlobal(si);
                if (s instanceof ModeSetting md) {
                    if (mi < md.getMods().size()) {
                        md.set(md.getMods().get(mi));
                        MenuSound.play(MenuSound.CLICK);
                    }
                }
                pop = null;
            }
            case ACT_POPOVER_ITEM -> {
                if (pop != null && payload < pop.getMods().size()) {
                    pop.set(pop.getMods().get(payload));
                    MenuSound.play(MenuSound.CLICK);
                }
                pop = null;
            }
            case ACT_LIST_ITEM -> {
                int si = payload / 16;
                int oi = payload % 16;
                Setting s = settingByGlobal(si);
                if (s instanceof ListSetting ls && oi < ls.getSettings().size()) {
                    BooleanSetting option = ls.getSettings().get(oi);
                    option.setState(!option.get());
                    MenuSound.play(option.get() ? MenuSound.TOGGLE_ON : MenuSound.TOGGLE_OFF);
                }
            }
            case ACT_BIND_SETTING -> {
                Setting s = settingByGlobal(payload);
                if (s instanceof BindSetting bs) {
                    bindingSetting = bindingSetting == bs ? null : bs;
                    binding = null;
                    MenuSound.play(MenuSound.CLICK);
                }
            }
            case ACT_PREF_BOOL -> {
                switch (payload) {
                    case PREF_SOUNDS -> MenuTheme.sounds = !MenuTheme.sounds;
                    case PREF_HOVER_SOUNDS -> MenuTheme.hoverSounds = !MenuTheme.hoverSounds;
                    case PREF_ANIMATIONS -> MenuTheme.animations = !MenuTheme.animations;
                    default -> {
                    }
                }
                MenuSound.play(MenuSound.CLICK);
            }
            case ACT_PREF_SLIDER -> {
                dragAction = ACT_PREF_SLIDER;
                dragPayload = payload;
                dragging = true;
                applyPrefSlider(payload, mx);
            }
            case ACT_ANIMATIONS -> {
                MenuTheme.animations = !MenuTheme.animations;
                MenuSound.play(MenuSound.CLICK);
            }
            case ACT_SOUNDS -> {
                MenuTheme.sounds = !MenuTheme.sounds;
                MenuSound.play(MenuSound.CLICK);
            }
            default -> {
            }
        }
    }

    private Module moduleByIndex(int index) {
        List<Module> all = ModuleManager.getModules();
        return index >= 0 && index < all.size() ? all.get(index) : null;
    }

    private Setting settingByGlobal(int global) {
        List<Module> all = ModuleManager.getModules();
        int mi = global / 16;
        int si = global % 16;
        if (mi >= all.size()) {
            return null;
        }
        return settingByIndex(all.get(mi), si);
    }

    private void applySlider(int global, double at) {
        Setting s = settingByGlobal(global);
        if (!(s instanceof FloatSetting n)) {
            return;
        }
        // положение дорожки берётся из последней отрисованной строки настройки
        float before = n.get();
        float f = (float) ((at - sliderX[global]) / Math.max(1, sliderW[global]));
        f = Math.max(0, Math.min(1, f));
        float v = n.getMin() + (n.getMax() - n.getMin()) * f;
        v = Math.round(v / Math.max(.0001f, n.getIncrement())) * n.getIncrement();
        n.setValue(v);
        if (n.get() != before) {
            MenuSound.play(MenuSound.TICK, .75f + (float) f * .8f, .55f);
        }
    }

    private void applyPrefSlider(int pref, double at) {
        if (pref != PREF_SCALE) {
            return;
        }
        float f = (float) ((at - prefSliderX[pref]) / Math.max(1, prefSliderW[pref]));
        f = Math.max(0, Math.min(1, f));
        float v = PREF_SCALE_MIN + (PREF_SCALE_MAX - PREF_SCALE_MIN) * f;
        v = Math.round(v * 20f) / 20f;
        if (v != MenuTheme.menuScale) {
            MenuTheme.menuScale = v;
            MenuSound.play(MenuSound.TICK, .8f + (float) f * .6f, .5f);
        }
    }

    private final float[] sliderX = new float[MenuAnim.MAX_SETTINGS];
    private final float[] sliderW = new float[MenuAnim.MAX_SETTINGS];

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        double x = Px.px(mouseX);
        double y = Px.px(mouseY);
        if (dragAction == ACT_SLIDER) {
            applySlider(dragPayload, x);
            return true;
        }
        if (dragAction == ACT_PREF_SLIDER) {
            applyPrefSlider(dragPayload, x);
            return true;
        }
        if (dragAction == ACT_SCROLL) {
            int context = dragPayload;
            int view = bottom - top;
            int total = scrollTotal[context];
            if (total > view) {
                int th = Math.max(S(40), view * view / total);
                float v = (float) Math.max(0, Math.min(1, (y - top - th / 2.0) / Math.max(1, view - th))) * (total - view);
                scroll[context] = v;
                anim.set(MenuAnim.SCROLL_BASE + context, v);
            }
            return true;
        }
        return true;
    }

    final int[] scrollTotal = new int[MenuAnim.SCROLL_SIZE];

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (dragAction == ACT_SCROLL) {
            MenuSound.play(MenuSound.DROP, 1, .5f);
        }
        dragging = false;
        dragAction = ACT_NONE;
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (pop != null) {
            scroll[8] = scroll[8] - (float) scrollY * S(38);
            return true;
        }
        if (popMod != null && mx >= pmX && mx < pmX + pmW && my >= pmY && my < pmY + pmH) {
            scroll[7] = scroll[7] - (float) scrollY * S(60);
            return true;
        }
        int context = page == Page.MODULES ? (search.isEmpty() ? category.ordinal() : 6) : PREF_SCROLL;
        scroll[context] = scroll[context] - (float) scrollY * S(76);
        return true;
    }

    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        if (binding != null) {
            if (key == GLFW.GLFW_KEY_ESCAPE) {
                binding = null;
                return true;
            }
            if (key == GLFW.GLFW_KEY_BACKSPACE || key == GLFW.GLFW_KEY_DELETE) {
                binding.setKey(-1);
                binding = null;
                MenuSound.play(MenuSound.RESET);
                return true;
            }
            binding.setKey(key);
            binding = null;
            MenuSound.play(MenuSound.SELECT);
            return true;
        }
        if (bindingSetting != null) {
            if (key == GLFW.GLFW_KEY_ESCAPE) {
                bindingSetting = null;
                return true;
            }
            if (key == GLFW.GLFW_KEY_BACKSPACE || key == GLFW.GLFW_KEY_DELETE) {
                bindingSetting.setKey(-1);
                bindingSetting = null;
                MenuSound.play(MenuSound.RESET);
                return true;
            }
            bindingSetting.setKey(key);
            bindingSetting = null;
            MenuSound.play(MenuSound.SELECT);
            return true;
        }
        if (key == GLFW.GLFW_KEY_ESCAPE) {
            if (pop != null) {
                pop = null;
                return true;
            }
            if (popMod != null) {
                popMod = null;
                MenuSound.play(MenuSound.POP, .8f, .6f);
                return true;
            }
            if (searchFocused || !search.isEmpty()) {
                searchFocused = false;
                if (!search.isEmpty()) {
                    search = "";
                    content(MenuSound.CLICK);
                }
                return true;
            }
            onClose();
            return true;
        }
        if (key == GLFW.GLFW_KEY_F && (modifiers & GLFW.GLFW_MOD_CONTROL) != 0) {
            if (page != Page.MODULES) {
                page = Page.MODULES;
                content(-1);
            }
            searchFocused = true;
            MenuSound.play(MenuSound.CLICK, 1.2f, .6f);
            return true;
        }
        if (!searchFocused && page == Page.MODULES && search.isEmpty()
                && (key == GLFW.GLFW_KEY_LEFT || key == GLFW.GLFW_KEY_RIGHT) && popMod == null) {
            int i = 0;
            for (int j = 0; j < TABS.length; j++) {
                if (TABS[j] == category) {
                    i = j;
                }
            }
            tabClick(Math.floorMod(i + (key == GLFW.GLFW_KEY_RIGHT ? 1 : -1), TABS.length));
            return true;
        }
        if (searchFocused) {
            if (key == GLFW.GLFW_KEY_BACKSPACE && !search.isEmpty()) {
                search = search.substring(0, search.length() - 1);
                MenuSound.play(MenuSound.TYPE, .9f, .3f);
                if (search.isEmpty()) {
                    contentAt = now;
                }
                return true;
            }
            if (key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_KP_ENTER) {
                collectVisible();
                if (visibleCount > 0 && !search.isEmpty()) {
                    Module m = visible[0];
                    if (hasSettings(m)) {
                        openPopup(m);
                    }
                }
                searchFocused = false;
                return true;
            }
            return true;
        }
        return super.keyPressed(key, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        String s = String.valueOf(codePoint);
        if (s.isEmpty() || codePoint < 32 || pop != null || binding != null || bindingSetting != null) {
            return false;
        }
        if (!searchFocused) {
            if (!Character.isLetterOrDigit(codePoint)) {
                return false;
            }
            if (page != Page.MODULES) {
                page = Page.MODULES;
                content(-1);
            }
            popMod = null;
            searchFocused = true;
        }
        if (search.length() < 40) {
            if (search.isEmpty()) {
                contentAt = now;
            }
            search += s;
            MenuSound.play(MenuSound.TYPE, 1.2f, .35f);
        }
        return true;
    }

    @Override
    public void onClose() {
        if (!closing) {
            closing = true;
            MenuSound.play(MenuSound.CLOSE);
        } else {
            super.onClose();
        }
    }

    @Override
    public void renderBackground(GuiGraphics g, int mouseX, int mouseY, float pt) {
        if (Minecraft.getInstance().level == null) {
            super.renderBackground(g, mouseX, mouseY, pt);
        }
    }
}
