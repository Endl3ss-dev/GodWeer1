package swerve.ui.bot;

import swerve.bot.BotLog;
import swerve.bot.BotManager;
import swerve.bot.BotProfile;
import swerve.bot.BotRunner;
import swerve.bot.BotStep;
import swerve.bot.BotStepType;
import swerve.bot.BotStore;
import swerve.ui.menu.MenuSound;
import swerve.ui.menu.MenuText.Weight;
import swerve.ui.menu.MenuTheme;
import swerve.ui.menu.Px;
import swerve.utils.render.ScissorUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import org.lwjgl.glfw.GLFW;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Студия ботов — большое окно настройки headless-клиентов.
 *
 * <p>Устроено как рабочее место: слева профили, в центре поведение выбранного
 * бота, справа каталог шагов и журнал. Профиль — это «куда подключаться» и
 * «что делать»; шаги собираются как конструктор: у каждого типа фиксированный
 * набор полей, значение поля правится кликом прямо в строке.
 *
 * <p>Запуск не требует мира: бот — отдельное TCP-соединение в фоновом потоке.
 * Поэтому окно можно закрыть, а бот продолжит работать; его состояние видно по
 * точке в списке профилей и по журналу.
 *
 * <p>Геометрия в реальных пикселях окна ({@link Px#begin(GuiGraphics)}), как в
 * остальных экранах Swerve, поэтому масштаб интерфейса игры на вёрстку не влияет.
 */
public final class BotStudioScreen extends Screen {

    // ---- цвета уровней журнала
    private static final int C_OK = 0xFF4CBF56;
    private static final int C_WARN = 0xFFE0B44C;
    private static final int C_ERROR = 0xFFE05A5A;

    // ---- действия (то, что вернёт клик)
    private static final int A_NONE = 0;
    private static final int A_CLOSE = 1;
    private static final int A_PROFILE = 2;
    private static final int A_RUN = 3;
    private static final int A_NEW = 4;
    private static final int A_DUP = 5;
    private static final int A_DEL = 6;
    private static final int A_CAT = 7;
    private static final int A_ADD = 8;
    private static final int A_STEP = 9;
    private static final int A_STEP_ON = 10;
    private static final int A_STEP_DEL = 11;
    private static final int A_STEP_UP = 12;
    private static final int A_STEP_DOWN = 13;
    private static final int A_VALUE = 14;
    private static final int A_FIELD = 15;
    private static final int A_TOGGLE = 16;
    private static final int A_CLEAR_LOG = 17;
    private static final int A_RUN_SELECTED = 18;

    private static final int FIELD_STRIDE = 8;
    private static final int MAX_HITS = 768;
    private static final int MAX_LOG_LINES = 400;

    /** Редактируемые поля профиля. */
    private enum Field {
        STEP_VALUE,
        NAME,
        HOST,
        PORT,
        NICK,
        STEP_DELAY,
        RECONNECT_DELAY
    }

    private final Screen parent;
    private BotProfile selected;
    private BotStepType.Category category = BotStepType.Category.CONNECT;

    // ---- геометрия
    private float u = 1f;
    private int px;
    private int py;
    private int pw;
    private int ph;
    private int headerH;
    private int leftX;
    private int leftY;
    private int leftW;
    private int leftH;
    private int centerX;
    private int centerW;
    private int rightX;
    private int rightY;
    private int rightW;
    private int rightH;
    private int settingsH;
    private int stepsY;
    private int stepsH;
    private int catalogH;
    private int logY;
    private int logH;

    // ---- прокрутка и размеры содержимого
    private float scrollProfiles;
    private float scrollSteps;
    private float scrollCatalog;
    private float scrollLog;
    private int profilesContentH;
    private int stepsContentH;
    private int catalogContentH;
    private int logContentH;
    private int profilesListY;
    private int profilesListH;
    private int stepsListY;
    private int stepsListH;
    private int catalogListY;
    private int catalogListH;
    private int logListY;
    private int logListH;
    private int stepRowH;
    private int profileRowH;

    // ---- попадания текущего кадра
    private final int[] hitX = new int[MAX_HITS];
    private final int[] hitY = new int[MAX_HITS];
    private final int[] hitW = new int[MAX_HITS];
    private final int[] hitH = new int[MAX_HITS];
    private final int[] hitAction = new int[MAX_HITS];
    private final int[] hitArg = new int[MAX_HITS];
    private int hitCount;

    // ---- редактирование значения
    private Field field = Field.STEP_VALUE;
    private int fieldStep = -1;
    private int fieldSlot = -1;
    private boolean editing;
    private boolean editFresh;
    private final StringBuilder edit = new StringBuilder();

    // ---- каталог шагов выбранной категории
    private final List<BotStepType> catalog = new ArrayList<>();

    private long lastSaveMs;

    public BotStudioScreen(Screen parent) {
        super(Component.literal("Студия ботов"));
        this.parent = parent;
        List<BotProfile> bots = BotStore.bots();
        this.selected = bots.isEmpty() ? null : bots.get(0);
        rebuildCatalog();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private int S(double value) {
        return (int) Math.round(value * this.u);
    }

    // ---------------------------------------------------------------- отрисовка

    @Override
    public void renderBackground(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        Minecraft mc = Minecraft.getInstance();
        g.fill(0, 0, mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight(), 0xC4000000);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        super.render(g, mouseX, mouseY, partialTick);

        this.hitCount = 0;
        layout();

        double mx = Px.px(mouseX);
        double my = Px.px(mouseY);

        Px.begin(g);
        try {
            frame(g, mx, my);
            header(g, mx, my);
            profilesPanel(g, mx, my);
            settingsPanel(g, mx, my);
            stepsPanel(g, mx, my);
            catalogPanel(g, mx, my);
            logPanel(g, mx, my);
        } finally {
            Px.end(g);
        }

        savePeriodically();
    }

    private void layout() {
        int sw = Px.width();
        int sh = Px.height();

        this.u = Math.max(0.62f, Math.min(1.6f, Math.min(sw / 1500.0f, sh / 900.0f)));

        this.pw = Math.min(sw - S(32), S(1460));
        this.ph = Math.min(sh - S(32), S(872));
        this.px = (sw - this.pw) / 2;
        this.py = (sh - this.ph) / 2;

        this.headerH = S(62);
        int pad = S(16);
        int gap = S(14);

        int bodyY = this.py + this.headerH;
        int bodyH = this.ph - this.headerH - pad;

        this.leftX = this.px + pad;
        this.leftY = bodyY;
        this.leftW = S(280);
        this.leftH = bodyH;

        this.rightW = S(322);
        this.centerX = this.leftX + this.leftW + gap;
        this.centerW = this.pw - pad * 2 - this.leftW - this.rightW - gap * 2;

        this.rightX = this.centerX + this.centerW + gap;
        this.rightY = bodyY;
        this.rightH = bodyH;
        this.catalogH = Math.min(S(392), Math.round(bodyH * 0.52f));
        this.logY = this.rightY + this.catalogH + gap;
        this.logH = this.rightY + this.rightH - this.logY;

        this.settingsH = S(146);
        this.stepsY = bodyY + this.settingsH + gap;
        this.stepsH = bodyY + bodyH - this.stepsY;

        // внутренние области списков
        this.profileRowH = S(58);
        this.profilesListY = this.leftY + S(46);
        this.profilesListH = this.leftH - S(46) - S(52);

        this.stepRowH = S(32);
        this.stepsListY = this.stepsY + S(44);
        this.stepsListH = this.stepsH - S(44) - S(8);

        // чипы категорий: 7 штук в две колонки, 4 ряда по (24 + 6)
        this.catalogListY = this.rightY + S(170);
        this.catalogListH = Math.max(S(24), this.rightY + this.catalogH - this.catalogListY - S(8));

        this.logListY = this.logY + S(40);
        this.logListH = this.logH - S(40) - S(8);
    }

    private void frame(GuiGraphics g, double mx, double my) {
        Px.shadow(g, this.px, this.py, this.pw, this.ph, S(26), Px.a(0xFF000000, 90));
        Px.round(g, this.px, this.py, this.pw, this.ph, S(20), Px.a(MenuTheme.card(), 246));
        Px.outline(g, this.px, this.py, this.pw, this.ph, S(20), Px.a(MenuTheme.border(), 220));

        // тонкая линия под шапкой
        Px.rect(g, this.px + S(16), this.py + this.headerH - S(1),
                this.pw - S(32), S(1), Px.a(MenuTheme.text(), 16));
    }

    private void header(GuiGraphics g, double mx, double my) {
        int cy = this.py + this.headerH / 2;
        int iconSize = S(26);
        int cx = this.px + S(32);
        Px.round(g, cx - iconSize / 2, cy - iconSize / 2, iconSize, iconSize, S(9), Px.a(MenuTheme.accent(), 34));
        Px.icon(g, "server", cx - S(9), cy - S(9), S(18), MenuTheme.accent());

        int tx = cx + iconSize / 2 + S(12);
        Px.text(g, "Студия ботов", tx, cy - S(18), Weight.SEMIBOLD, S(17), MenuTheme.text());
        Px.text(g, "headless-клиенты · протокол 1.21.4", tx, cy + S(2), Weight.MEDIUM, S(10), MenuTheme.muted());

        // счётчик запущенных ботов
        int running = BotManager.runningCount();
        String label = running == 0 ? "нет активных ботов" : "активных: " + running;
        int fs = S(11);
        int w = Px.textWidth(label, Weight.MEDIUM, fs) + S(26);
        int h = S(24);
        int bx = this.px + this.pw - S(64) - w;
        int by = cy - h / 2;
        boolean live = running > 0;
        Px.round(g, bx, by, w, h, h / 2, Px.a(live ? MenuTheme.accent() : MenuTheme.text(), live ? 30 : 14));
        Px.outline(g, bx, by, w, h, h / 2, Px.a(live ? MenuTheme.accent() : MenuTheme.border(), live ? 90 : 120));
        Px.circle(g, bx + S(12), cy, S(3), live ? MenuTheme.accent() : MenuTheme.muted());
        Px.text(g, label, bx + S(20), cy - fs / 2, Weight.MEDIUM, fs, live ? MenuTheme.text() : MenuTheme.muted());

        // закрыть
        int size = S(28);
        int x = this.px + this.pw - S(18) - size;
        int y = cy - size / 2;
        boolean hv = inside(mx, my, x, y, size, size);
        Px.round(g, x, y, size, size, S(9), Px.a(hv ? MenuTheme.text() : MenuTheme.border(), hv ? 28 : 12));
        Px.icon(g, "x", x + S(7), y + S(7), S(14), hv ? MenuTheme.text() : MenuTheme.muted());
        hit(x, y, size, size, A_CLOSE, 0);
    }

    // ---- левая колонка: профили

    private void profilesPanel(GuiGraphics g, double mx, double my) {
        panel(g, this.leftX, this.leftY, this.leftW, this.leftH);

        int headY = this.leftY + S(16);
        Px.icon(g, "users", this.leftX + S(14), headY + S(1), S(13), MenuTheme.accent());
        Px.text(g, "Профили", this.leftX + S(34), headY, Weight.SEMIBOLD, S(12), MenuTheme.text());

        List<BotProfile> bots = BotStore.bots();
        String count = bots.size() + " / 64";
        Px.textRight(g, count, this.leftX + this.leftW - S(14), headY, Weight.MEDIUM, S(10), MenuTheme.dim());

        ScissorUtils.push();
        ScissorUtils.set(this.leftX, Px.height() - (this.profilesListY + this.profilesListH),
                this.leftW, this.profilesListH);

        this.profilesContentH = bots.size() * this.profileRowH;
        int baseY = this.profilesListY - Math.round(clampScroll(this.scrollProfiles, this.profilesContentH, this.profilesListH));

        for (int i = 0, size = bots.size(); i < size; i++) {
            BotProfile profile = bots.get(i);
            int y = baseY + i * this.profileRowH;
            if (y + this.profileRowH < this.profilesListY || y > this.profilesListY + this.profilesListH) {
                continue;
            }
            profileRow(g, profile, i, y, mx, my);
        }
        ScissorUtils.pop();

        if (bots.isEmpty()) {
            Px.textCenter(g, "Пока нет профилей", this.leftX + this.leftW / 2,
                    this.profilesListY + S(30), Weight.MEDIUM, S(11), MenuTheme.dim());
            Px.textCenter(g, "Создайте первого бота", this.leftX + this.leftW / 2,
                    this.profilesListY + S(48), Weight.MEDIUM, S(10), MenuTheme.dim());
        }

        // нижние кнопки
        int pad = S(12);
        int gap = S(6);
        int by = this.leftY + this.leftH - S(40);
        int bh = S(28);
        int w1 = (this.leftW - pad * 2 - gap * 2) * 45 / 100;
        int w2 = (this.leftW - pad * 2 - gap * 2 - w1) / 2;
        int bx = this.leftX + pad;

        boolean hvNew = inside(mx, my, bx, by, w1, bh);
        Px.round(g, bx, by, w1, bh, S(9), Px.a(MenuTheme.accent(), hvNew ? 62 : 40));
        Px.outline(g, bx, by, w1, bh, S(9), Px.a(MenuTheme.accent(), hvNew ? 170 : 110));
        Px.icon(g, "plus", bx + S(9), by + S(8), S(12), MenuTheme.accent());
        Px.text(g, "Создать", bx + S(25), by + S(7), Weight.SEMIBOLD, S(10),
                Px.mix(MenuTheme.text(), MenuTheme.accent(), 0.35f));
        hit(bx, by, w1, bh, A_NEW, 0);

        int bx2 = bx + w1 + gap;
        boolean hvDup = inside(mx, my, bx2, by, w2, bh) && this.selected != null;
        Px.round(g, bx2, by, w2, bh, S(9), Px.a(MenuTheme.text(), hvDup ? 22 : 12));
        Px.icon(g, "copy", bx2 + (w2 - S(13)) / 2, by + S(7), S(13),
                this.selected == null ? MenuTheme.dim() : (hvDup ? MenuTheme.text() : MenuTheme.muted()));
        if (this.selected != null) {
            hit(bx2, by, w2, bh, A_DUP, 0);
        }

        int bx3 = bx2 + w2 + gap;
        boolean hvDel = inside(mx, my, bx3, by, w2, bh) && this.selected != null;
        Px.round(g, bx3, by, w2, bh, S(9), Px.a(C_ERROR, hvDel ? 40 : 14));
        Px.icon(g, "trash", bx3 + (w2 - S(13)) / 2, by + S(7), S(13),
                this.selected == null ? MenuTheme.dim() : (hvDel ? C_ERROR : MenuTheme.muted()));
        if (this.selected != null) {
            hit(bx3, by, w2, bh, A_DEL, 0);
        }
    }

    private void profileRow(GuiGraphics g, BotProfile profile, int index, int y, double mx, double my) {
        boolean active = profile == this.selected;
        boolean hovered = inside(mx, my, this.leftX + S(8), y + S(3),
                this.leftW - S(16), this.profileRowH - S(6));
        int x = this.leftX + S(8);
        int w = this.leftW - S(16);
        int h = this.profileRowH - S(6);

        int fill = active ? Px.a(MenuTheme.accent(), 30) : Px.a(MenuTheme.text(), hovered ? 14 : 8);
        Px.round(g, x, y + S(3), w, h, S(10), fill);
        if (active) {
            Px.outline(g, x, y + S(3), w, h, S(10), Px.a(MenuTheme.accent(), 110));
            Px.round(g, x, y + S(3) + S(8), S(3), h - S(16), S(2), MenuTheme.accent());
        }

        boolean running = BotManager.isRunning(profile);
        boolean online = BotManager.isOnline(profile);
        int dot = running ? MenuTheme.accent() : online ? C_OK : MenuTheme.dim();
        if (running) {
            Px.circle(g, x + S(16), y + S(20), S(4), Px.a(MenuTheme.accent(), 60));
        }
        Px.circle(g, x + S(16), y + S(20), S(2), dot);

        Px.text(g, Px.ellipsize(profile.getName(), Weight.SEMIBOLD, S(12), w - S(74)),
                x + S(27), y + S(13), Weight.SEMIBOLD, S(12), MenuTheme.text());
        Px.text(g, Px.ellipsize(profile.getNickname() + " · " + profile.address(), Weight.MEDIUM, S(9), w - S(74)),
                x + S(27), y + S(29), Weight.MEDIUM, S(9), MenuTheme.muted());
        Px.text(g, profile.getSteps().size() + " шагов", x + S(27), y + S(41), Weight.MEDIUM, S(8), MenuTheme.dim());

        // Сначала фон строки, потом кнопка: hitAt идёт с конца, поэтому
        // кнопка запуска перекрывает выбор профиля, а не наоборот.
        hit(x, y + S(3), w, h, A_PROFILE, index);

        int bs = S(24);
        int bx = x + w - bs - S(8);
        int by = y + S(3) + (h - bs) / 2;
        boolean hvRun = inside(mx, my, bx, by, bs, bs);
        Px.round(g, bx, by, bs, bs, S(8),
                Px.a(running ? C_ERROR : MenuTheme.accent(), hvRun ? 60 : 34));
        Px.icon(g, running ? "stop" : "play", bx + S(6), by + S(6), S(12),
                running ? C_ERROR : MenuTheme.accent());
        hit(bx, by, bs, bs, A_RUN, index);
    }

    // ---- центральная часть: настройки профиля

    private void settingsPanel(GuiGraphics g, double mx, double my) {
        panel(g, this.centerX, this.leftY, this.centerW, this.settingsH);

        int headY = this.leftY + S(14);
        Px.icon(g, "sliders", this.centerX + S(14), headY + S(1), S(13), MenuTheme.accent());
        Px.text(g, "Подключение", this.centerX + S(34), headY, Weight.SEMIBOLD, S(12), MenuTheme.text());

        if (this.selected == null) {
            Px.textCenter(g, "Создайте профиль слева, чтобы настроить подключение",
                    this.centerX + this.centerW / 2, this.leftY + this.settingsH / 2 - S(6),
                    Weight.MEDIUM, S(11), MenuTheme.dim());
            return;
        }

        // кнопка запуска профиля
        boolean running = BotManager.isRunning(this.selected);
        String label = running ? "Остановить" : "Запустить";
        int fs = S(10);
        int bw = Px.textWidth(label, Weight.SEMIBOLD, fs) + S(50);
        int bh = S(26);
        int bx = this.centerX + this.centerW - S(14) - bw;
        int by = headY - S(6);
        boolean hv = inside(mx, my, bx, by, bw, bh);
        Px.round(g, bx, by, bw, bh, S(9), Px.a(running ? C_ERROR : MenuTheme.accent(), hv ? 66 : 42));
        Px.outline(g, bx, by, bw, bh, S(9), Px.a(running ? C_ERROR : MenuTheme.accent(), hv ? 180 : 120));
        Px.icon(g, running ? "stop" : "play", bx + S(10), by + S(7), S(12),
                running ? C_ERROR : MenuTheme.accent());
        Px.text(g, label, bx + S(28), by + S(8), Weight.SEMIBOLD, fs,
                running ? C_ERROR : Px.mix(MenuTheme.text(), MenuTheme.accent(), 0.4f));
        hit(bx, by, bw, bh, A_RUN_SELECTED, 0);

        int pad = S(14);
        int innerW = this.centerW - pad * 2;
        int gap = S(10);

        int row1Y = this.leftY + S(40);
        int wName = Math.round(innerW * 0.30f);
        int wHost = Math.round(innerW * 0.46f);
        int wPort = innerW - wName - wHost - gap * 2;
        fieldBox(g, "Имя профиля", this.selected.getName(), Field.NAME, -1, -1,
                this.centerX + pad, row1Y, wName, mx, my);
        fieldBox(g, "Сервер", this.selected.getHost(), Field.HOST, -1, -1,
                this.centerX + pad + wName + gap, row1Y, wHost, mx, my);
        fieldBox(g, "Порт", String.valueOf(this.selected.getPort()), Field.PORT, -1, -1,
                this.centerX + pad + wName + wHost + gap * 2, row1Y, wPort, mx, my);

        int row2Y = row1Y + S(50);
        int wNick = Math.round(innerW * 0.24f);
        int wDelay = Math.round(innerW * 0.18f);
        int wRecon = Math.round(innerW * 0.18f);
        int wToggle = (innerW - wNick - wDelay - wRecon - gap * 3) / 2;
        int x2 = this.centerX + pad;

        fieldBox(g, "Ник в игре", this.selected.getNickname(), Field.NICK, -1, -1,
                x2, row2Y, wNick, mx, my);
        fieldBox(g, "Пауза между шагами, с", String.valueOf(this.selected.getStepDelay()),
                Field.STEP_DELAY, -1, -1, x2 + wNick + gap, row2Y, wDelay, mx, my);
        fieldBox(g, "Переподключение, с", String.valueOf(this.selected.getReconnectDelay()),
                Field.RECONNECT_DELAY, -1, -1, x2 + wNick + wDelay + gap * 2, row2Y, wRecon, mx, my);

        int tx = x2 + wNick + wDelay + wRecon + gap * 3;
        toggle(g, "Автовход", this.selected.isAutoReconnect(), tx, row2Y, wToggle, 0, mx, my);
        toggle(g, "Повтор шагов", this.selected.isLoopForever(),
                tx + wToggle + gap, row2Y, wToggle, 1, mx, my);
    }

    private void fieldBox(GuiGraphics g, String caption, String value, Field kind, int step, int slot,
                          int x, int y, int w, double mx, double my) {
        Px.text(g, caption, x + S(2), y, Weight.MEDIUM, S(9), MenuTheme.muted());

        int by = y + S(14);
        int bh = S(30);
        boolean hovered = inside(mx, my, x, by, w, bh);
        boolean active = this.editing && this.field == kind && this.fieldStep == step && this.fieldSlot == slot;

        String shown = active ? this.edit.toString() : value;
        if (active && shown.isEmpty()) {
            shown = "";
        }

        Px.round(g, x, by, w, bh, S(9), Px.a(MenuTheme.control(), active ? 235 : 190));
        Px.outline(g, x, by, w, bh, S(9),
                Px.a(active ? MenuTheme.accent() : MenuTheme.border(), active ? 200 : hovered ? 150 : 110));

        int fs = S(11);
        Px.text(g, Px.ellipsize(shown, Weight.MEDIUM, fs, w - S(16)), x + S(8), by + (bh - fs) / 2,
                Weight.MEDIUM, fs, shown.isEmpty() ? MenuTheme.dim() : MenuTheme.text());

        if (active) {
            int caretX = x + S(8) + Math.min(w - S(12),
                    Px.textWidth(shown, Weight.MEDIUM, fs));
            Px.rect(g, caretX, by + S(7), S(1), bh - S(14), MenuTheme.accent());
        }

        hit(x, by, w, bh, A_FIELD, kind.ordinal());
    }

    private void toggle(GuiGraphics g, String caption, boolean on, int x, int y, int w, int index,
                        double mx, double my) {
        Px.text(g, caption, x + S(2), y, Weight.MEDIUM, S(9), MenuTheme.muted());
        int by = y + S(14);
        int bh = S(30);
        boolean hovered = inside(mx, my, x, by, w, bh);
        Px.round(g, x, by, w, bh, S(9), Px.a(MenuTheme.control(), 190));
        Px.outline(g, x, by, w, bh, S(9), Px.a(hovered ? MenuTheme.text() : MenuTheme.border(), hovered ? 150 : 110));

        int sw = S(26);
        int sh = S(14);
        int sx = x + S(8);
        int sy = by + (bh - sh) / 2;
        Px.round(g, sx, sy, sw, sh, sh / 2, Px.a(on ? MenuTheme.accent() : MenuTheme.text(), on ? 210 : 40));
        Px.circle(g, on ? sx + sw - sh / 2 : sx + sh / 2, sy + sh / 2, S(5),
                on ? Px.onAccent(MenuTheme.accent()) : MenuTheme.muted());

        Px.text(g, on ? "включено" : "выключено", sx + sw + S(8), by + (bh - S(9)) / 2,
                Weight.MEDIUM, S(9), on ? MenuTheme.text() : MenuTheme.muted());

        hit(x, by, w, bh, A_TOGGLE, index);
    }

    // ---- шаги поведения

    private void stepsPanel(GuiGraphics g, double mx, double my) {
        panel(g, this.centerX, this.stepsY, this.centerW, this.stepsH);

        int headY = this.stepsY + S(15);
        Px.icon(g, "list", this.centerX + S(14), headY + S(1), S(13), MenuTheme.accent());
        Px.text(g, "Поведение", this.centerX + S(34), headY, Weight.SEMIBOLD, S(12), MenuTheme.text());

        List<BotStep> steps = this.selected == null ? Collections.emptyList() : this.selected.getSteps();
        BotRunner runner = BotManager.runner(this.selected);
        String info = runner != null && runner.isRunning()
                ? "шаг " + Math.min(steps.size(), runner.currentStep() + 1) + " / " + steps.size()
                : steps.size() + " шагов";
        Px.text(g, info, this.centerX + S(34) + Px.textWidth("Поведение", Weight.SEMIBOLD, S(12)) + S(10),
                headY + S(1), Weight.MEDIUM, S(9), MenuTheme.dim());

        Px.textRight(g, "клик по значению — правка", this.centerX + this.centerW - S(14),
                headY + S(1), Weight.MEDIUM, S(9), MenuTheme.dim());

        if (this.selected == null) {
            Px.textCenter(g, "Выберите профиль, чтобы собрать поведение",
                    this.centerX + this.centerW / 2, this.stepsY + this.stepsH / 2,
                    Weight.MEDIUM, S(11), MenuTheme.dim());
            this.stepsContentH = 0;
            return;
        }

        if (steps.isEmpty()) {
            Px.textCenter(g, "Здесь появятся шаги бота",
                    this.centerX + this.centerW / 2, this.stepsY + this.stepsH / 2 - S(8),
                    Weight.MEDIUM, S(11), MenuTheme.muted());
            Px.textCenter(g, "Добавьте шаг из каталога справа — например, «подключиться к серверу»",
                    this.centerX + this.centerW / 2, this.stepsY + this.stepsH / 2 + S(10),
                    Weight.MEDIUM, S(9), MenuTheme.dim());
            this.stepsContentH = 0;
            return;
        }

        ScissorUtils.push();
        ScissorUtils.set(this.centerX, Px.height() - (this.stepsListY + this.stepsListH),
                this.centerW, this.stepsListH);

        this.stepsContentH = steps.size() * this.stepRowH + S(8);
        float scrolled = clampScroll(this.scrollSteps, this.stepsContentH, this.stepsListH);
        int baseY = this.stepsListY - Math.round(scrolled);

        int depth = 0;
        for (int i = 0, size = steps.size(); i < size; i++) {
            BotStep step = steps.get(i);
            BotStepType type = step.getType();
            if (type == null) {
                continue;
            }
            if (type.closesBlock() && depth > 0) {
                depth--;
            }
            int rowY = baseY + i * this.stepRowH;
            if (rowY + this.stepRowH >= this.stepsListY && rowY <= this.stepsListY + this.stepsListH) {
                stepRow(g, steps, step, i, depth, rowY, mx, my);
            }
            if (type.opensBlock()) {
                depth++;
            }
        }
        ScissorUtils.pop();
    }

    private void stepRow(GuiGraphics g, List<BotStep> steps, BotStep step, int index, int depth, int y,
                         double mx, double my) {
        int x = this.centerX + S(10) + depth * S(18);
        int w = this.centerW - S(20) - depth * S(18);
        int h = this.stepRowH - S(4);
        boolean hovered = inside(mx, my, x, y, w, h);
        boolean selectedRow = this.fieldStep == index && this.editing;

        if (depth > 0) {
            // направляющая вложенности
            int lineX = this.centerX + S(10) + (depth - 1) * S(18) + S(6);
            Px.rect(g, lineX, y, S(1), this.stepRowH, Px.a(MenuTheme.accent(), 46));
        }

        Px.round(g, x, y, w, h, S(8), Px.a(MenuTheme.text(), hovered || selectedRow ? 16 : 8));
        hit(x, y, w, h, A_STEP, index);

        // номер шага
        Px.text(g, String.valueOf(index + 1), x + S(8), y + (h - S(9)) / 2,
                Weight.MEDIUM, S(9), MenuTheme.dim());

        // включатель шага
        int dotX = x + S(24);
        int dotY = y + h / 2;
        boolean on = step.isEnabled();
        Px.round(g, dotX - S(3), dotY - S(6), S(6), S(12), S(3),
                Px.a(on ? MenuTheme.accent() : MenuTheme.muted(), on ? 200 : 70));
        Px.circle(g, dotX, on ? dotY - S(3) : dotY + S(3), S(2), on ? Px.onAccent(MenuTheme.accent()) : MenuTheme.muted());
        hit(dotX - S(6), y + S(2), S(12), h - S(4), A_STEP_ON, index);

        // текст шага со значениями-чипами
        int cx = x + S(38);
        int limit = x + w - S(76);
        int fs = S(10);
        int cy = y + (h - fs) / 2;
        boolean truncated = false;

        if (step.getType() != null) {
            BotStepType type = step.getType();
            for (int word = 0; word < type.paramCount() + 1; word++) {
                String text = type.word(word);
                if (!text.isEmpty()) {
                    int tw = Px.textWidth(text, Weight.MEDIUM, fs);
                    if (cx + tw > limit) {
                        truncated = true;
                        break;
                    }
                    Px.text(g, text, cx, cy, Weight.MEDIUM, fs, MenuTheme.muted());
                    cx += tw + S(3);
                }
                if (word < type.paramCount()) {
                    String value = step.get(word);
                    int vw = Math.min(Px.textWidth(value, Weight.SEMIBOLD, fs), limit - cx - S(4));
                    if (vw < 0) {
                        truncated = true;
                        break;
                    }
                    int chipW = Math.max(S(18), vw + S(10));
                    int chipH = h - S(8);
                    boolean chipHover = inside(mx, my, cx, y + S(4), chipW, chipH);
                    boolean chipActive = this.editing && this.field == Field.STEP_VALUE
                            && this.fieldStep == index && this.fieldSlot == word;
                    int chipFill = chipActive ? Px.a(MenuTheme.accent(), 70)
                            : Px.a(MenuTheme.accent(), chipHover ? 44 : 26);
                    Px.round(g, cx, y + S(4), chipW, chipH, S(6), chipFill);
                    Px.outline(g, cx, y + S(4), chipW, chipH, S(6),
                            Px.a(MenuTheme.accent(), chipActive ? 220 : chipHover ? 150 : 70));
                    String shown = chipActive ? this.edit.toString() : value;
                    Px.text(g, Px.ellipsize(shown, Weight.SEMIBOLD, fs, Math.max(S(6), chipW - S(10))),
                            cx + S(5), cy, Weight.SEMIBOLD, fs,
                            chipActive ? MenuTheme.text() : Px.mix(MenuTheme.text(), MenuTheme.accent(), 0.35f));
                    if (chipActive) {
                        Px.rect(g, cx + S(5) + Math.min(chipW - S(10),
                                Px.textWidth(shown, Weight.SEMIBOLD, fs)), y + S(6), S(1), chipH - S(4),
                                MenuTheme.accent());
                    }
                    hit(cx, y + S(4), chipW, chipH, A_VALUE, index * FIELD_STRIDE + word);
                    cx += chipW + S(4);
                }
            }
        }

        if (truncated) {
            Px.text(g, "…", Math.min(cx, limit), cy, Weight.MEDIUM, fs, MenuTheme.dim());
        }

        // кнопки строки (появляются при наведении)
        if (hovered) {
            int bs = S(18);
            int by = y + (h - bs) / 2;
            int bx = x + w - S(4) - bs;
            boolean hvDel = inside(mx, my, bx, by, bs, bs);
            Px.round(g, bx, by, bs, bs, S(5), Px.a(C_ERROR, hvDel ? 60 : 26));
            Px.icon(g, "x", bx + S(4), by + S(4), S(10), hvDel ? C_ERROR : MenuTheme.muted());
            hit(bx, by, bs, bs, A_STEP_DEL, index);

            bx -= bs + S(4);
            boolean hvDown = inside(mx, my, bx, by, bs, bs);
            Px.round(g, bx, by, bs, bs, S(5), Px.a(MenuTheme.text(), hvDown ? 34 : 14));
            Px.icon(g, "chev-down", bx + S(4), by + S(4), S(10), MenuTheme.muted());
            hit(bx, by, bs, bs, A_STEP_DOWN, index);

            bx -= bs + S(4);
            boolean hvUp = inside(mx, my, bx, by, bs, bs);
            Px.round(g, bx, by, bs, bs, S(5), Px.a(MenuTheme.text(), hvUp ? 34 : 14));
            Px.icon(g, "chev-up", bx + S(4), by + S(4), S(10), MenuTheme.muted());
            hit(bx, by, bs, bs, A_STEP_UP, index);
        }
    }

    // ---- правая колонка: каталог

    private void catalogPanel(GuiGraphics g, double mx, double my) {
        panel(g, this.rightX, this.rightY, this.rightW, this.catalogH);

        int headY = this.rightY + S(15);
        Px.icon(g, "apps", this.rightX + S(14), headY + S(1), S(13), MenuTheme.accent());
        Px.text(g, "Каталог шагов", this.rightX + S(34), headY, Weight.SEMIBOLD, S(12), MenuTheme.text());
        Px.textRight(g, catalogSize() + " шт.", this.rightX + this.rightW - S(14), headY + S(1),
                Weight.MEDIUM, S(9), MenuTheme.dim());

        // категории: две колонки чипов
        int pad = S(12);
        int gap = S(6);
        int chipW = (this.rightW - pad * 2 - gap) / 2;
        int chipH = S(24);
        BotStepType.Category[] categories = BotStepType.Category.values();
        for (int i = 0; i < categories.length; i++) {
            int col = i % 2;
            int row = i / 2;
            int x = this.rightX + pad + col * (chipW + gap);
            int y = this.rightY + S(42) + row * (chipH + gap);
            boolean activeCat = categories[i] == this.category;
            boolean hv = inside(mx, my, x, y, chipW, chipH);
            Px.round(g, x, y, chipW, chipH, S(7),
                    Px.a(activeCat ? MenuTheme.accent() : MenuTheme.text(), activeCat ? 40 : hv ? 20 : 10));
            if (activeCat) {
                Px.outline(g, x, y, chipW, chipH, S(7), Px.a(MenuTheme.accent(), 130));
            }
            Px.circle(g, x + S(10), y + chipH / 2, S(2.5f),
                    activeCat ? MenuTheme.accent() : MenuTheme.muted());
            Px.text(g, categories[i].displayName(), x + S(17), y + (chipH - S(9)) / 2,
                    activeCat ? Weight.SEMIBOLD : Weight.MEDIUM, S(9),
                    activeCat ? MenuTheme.text() : MenuTheme.muted());
            hit(x, y, chipW, chipH, A_CAT, categories[i].ordinal());
        }

        // список шагов категории
        ScissorUtils.push();
        ScissorUtils.set(this.rightX, Px.height() - (this.catalogListY + this.catalogListH),
                this.rightW, this.catalogListH);

        int rowH = S(24);
        this.catalogContentH = this.catalog.size() * rowH;
        int baseY = this.catalogListY - Math.round(clampScroll(this.scrollCatalog, this.catalogContentH,
                this.catalogListH));

        for (int i = 0, size = this.catalog.size(); i < size; i++) {
            int y = baseY + i * rowH;
            if (y + rowH < this.catalogListY || y > this.catalogListY + this.catalogListH) {
                continue;
            }
            BotStepType type = this.catalog.get(i);
            int x = this.rightX + pad;
            int w = this.rightW - pad * 2;
            int h = rowH - S(3);
            boolean hv = inside(mx, my, x, y, w, h);
            Px.round(g, x, y, w, h, S(6), Px.a(MenuTheme.text(), hv ? 22 : 8));
            if (hv) {
                Px.icon(g, "plus", x + S(5), y + (h - S(10)) / 2, S(10), MenuTheme.accent());
            } else {
                Px.circle(g, x + S(10), y + h / 2, S(1.5f), MenuTheme.dim());
            }
            Px.text(g, Px.ellipsize(preview(type), Weight.MEDIUM, S(9), w - S(28)),
                    x + S(18), y + (h - S(9)) / 2, Weight.MEDIUM, S(9),
                    hv ? MenuTheme.text() : MenuTheme.muted());
            hit(x, y, w, h, A_ADD, i);
        }
        ScissorUtils.pop();
    }

    private String preview(BotStepType type) {
        String[] values = new String[type.paramCount()];
        for (int i = 0; i < values.length; i++) {
            values[i] = type.param(i) == null ? "" : type.param(i).defaultValue();
        }
        return type.describe(values);
    }

    private int catalogSize() {
        int count = 0;
        for (BotStepType type : BotStepType.values()) {
            if (type.category() == this.category) {
                count++;
            }
        }
        return count;
    }

    // ---- журнал

    private void logPanel(GuiGraphics g, double mx, double my) {
        panel(g, this.rightX, this.logY, this.rightW, this.logH);

        int headY = this.logY + S(15);
        Px.icon(g, "scroll", this.rightX + S(14), headY + S(1), S(13), MenuTheme.accent());
        Px.text(g, "Журнал", this.rightX + S(34), headY, Weight.SEMIBOLD, S(12), MenuTheme.text());

        BotLog log = BotManager.logFor(this.selected);
        List<BotLog.Entry> entries = log.snapshot();
        Px.textRight(g, entries.size() + "", this.rightX + this.rightW - S(42), headY + S(1),
                Weight.MEDIUM, S(9), MenuTheme.dim());

        int bs = S(18);
        int bx = this.rightX + this.rightW - S(12) - bs;
        int by = headY - S(2);
        boolean hv = inside(mx, my, bx, by, bs, bs);
        Px.round(g, bx, by, bs, bs, S(5), Px.a(MenuTheme.text(), hv ? 30 : 12));
        Px.icon(g, "broom", bx + S(3), by + S(3), S(12), hv ? MenuTheme.text() : MenuTheme.muted());
        hit(bx, by, bs, bs, A_CLEAR_LOG, 0);

        ScissorUtils.push();
        ScissorUtils.set(this.rightX, Px.height() - (this.logListY + this.logListH),
                this.rightW, this.logListH);

        int rowH = S(22);
        this.logContentH = entries.size() * rowH;
        int baseY = this.logListY - Math.round(clampScroll(this.scrollLog, this.logContentH, this.logListH));

        int fs = S(9);
        for (int i = 0, size = Math.min(entries.size(), MAX_LOG_LINES); i < size; i++) {
            BotLog.Entry entry = entries.get(i);
            int y = baseY + i * rowH;
            if (y + rowH < this.logListY || y > this.logListY + this.logListH) {
                continue;
            }
            int color = switch (entry.level()) {
                case OK -> C_OK;
                case WARN -> C_WARN;
                case ERROR -> C_ERROR;
                default -> MenuTheme.muted();
            };
            Px.circle(g, this.rightX + S(16), y + rowH / 2, S(2), color);
            Px.text(g, time(entry.time()), this.rightX + S(24), y + (rowH - fs) / 2,
                    Weight.MEDIUM, fs, MenuTheme.dim());
            Px.text(g, Px.ellipsize(entry.text(), Weight.MEDIUM, fs, this.rightW - S(78)),
                    this.rightX + S(64), y + (rowH - fs) / 2, Weight.MEDIUM, fs, color);
        }
        ScissorUtils.pop();

        if (entries.isEmpty()) {
            Px.textCenter(g, "Здесь появятся события бота",
                    this.rightX + this.rightW / 2, this.logListY + S(20),
                    Weight.MEDIUM, S(10), MenuTheme.dim());
        }
    }

    private static String time(long millis) {
        LocalTime local = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalTime();
        return String.format("%02d:%02d:%02d", local.getHour(), local.getMinute(), local.getSecond());
    }

    // ---- панели и попадания

    private void panel(GuiGraphics g, int x, int y, int w, int h) {
        Px.round(g, x, y, w, h, S(14), Px.a(MenuTheme.bg(), 210));
        Px.outline(g, x, y, w, h, S(14), Px.a(MenuTheme.border(), 160));
    }

    private void hit(int x, int y, int w, int h, int action, int arg) {
        if (this.hitCount >= MAX_HITS) {
            return;
        }
        this.hitX[this.hitCount] = x;
        this.hitY[this.hitCount] = y;
        this.hitW[this.hitCount] = w;
        this.hitH[this.hitCount] = h;
        this.hitAction[this.hitCount] = action;
        this.hitArg[this.hitCount] = arg;
        this.hitCount++;
    }

    private static boolean inside(double x, double y, int rx, int ry, int rw, int rh) {
        return x >= rx && x < rx + rw && y >= ry && y < ry + rh;
    }

    private int hitAt(double x, double y) {
        for (int i = this.hitCount - 1; i >= 0; i--) {
            if (inside(x, y, this.hitX[i], this.hitY[i], this.hitW[i], this.hitH[i])) {
                return i;
            }
        }
        return -1;
    }

    private static float clampScroll(float scroll, int content, int view) {
        float max = Math.max(0, content - view);
        return Math.max(0, Math.min(max, scroll));
    }

    // ---------------------------------------------------------------- ввод

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        double mx = Px.px(mouseX);
        double my = Px.px(mouseY);

        int index = hitAt(mx, my);
        if (index < 0) {
            commitEdit();
            return super.mouseClicked(mouseX, mouseY, button);
        }
        if (button != 0) {
            return true;
        }

        int action = this.hitAction[index];
        int arg = this.hitArg[index];

        switch (action) {
            case A_CLOSE -> onClose();
            case A_PROFILE -> {
                commitEdit();
                List<BotProfile> bots = BotStore.bots();
                if (arg >= 0 && arg < bots.size()) {
                    this.selected = bots.get(arg);
                    this.scrollSteps = 0;
                    this.scrollLog = 0;
                    MenuSound.play(MenuSound.SELECT);
                }
            }
            case A_RUN -> {
                commitEdit();
                List<BotProfile> bots = BotStore.bots();
                if (arg >= 0 && arg < bots.size()) {
                    BotProfile profile = bots.get(arg);
                    this.selected = profile;
                    toggleRun(profile);
                }
            }
            case A_RUN_SELECTED -> {
                commitEdit();
                if (this.selected != null) {
                    toggleRun(this.selected);
                }
            }
            case A_NEW -> {
                BotProfile created = BotStore.create();
                if (created != null) {
                    this.selected = created;
                    MenuSound.play(MenuSound.OPEN, 1.05f, 0.5f);
                }
            }
            case A_DUP -> {
                if (this.selected != null) {
                    BotStore.duplicate(this.selected);
                    MenuSound.play(MenuSound.OPEN, 1.1f, 0.45f);
                }
            }
            case A_DEL -> {
                if (this.selected != null) {
                    BotStore.remove(this.selected);
                    List<BotProfile> bots = BotStore.bots();
                    this.selected = bots.isEmpty() ? null : bots.get(0);
                    MenuSound.play(MenuSound.DROP, 0.9f, 0.5f);
                }
            }
            case A_CAT -> {
                BotStepType.Category[] categories = BotStepType.Category.values();
                if (arg >= 0 && arg < categories.length) {
                    this.category = categories[arg];
                    this.scrollCatalog = 0;
                    rebuildCatalog();
                    MenuSound.play(MenuSound.TAB, 1.1f, 0.4f);
                }
            }
            case A_ADD -> {
                if (this.selected != null && arg >= 0 && arg < this.catalog.size()) {
                    commitEdit();
                    if (this.selected.getSteps().size() < 500) {
                        this.selected.addStep(new BotStep(this.catalog.get(arg)));
                        BotStore.markDirty();
                        // прокручиваем к новому шагу
                        this.scrollSteps = Math.max(0, this.selected.getSteps().size() * this.stepRowH
                                - this.stepsListH + this.stepRowH);
                        MenuSound.play(MenuSound.DROP, 1.05f, 0.45f);
                    }
                }
            }
            case A_STEP -> {
                commitEdit();
                List<BotStep> steps = this.stepList();
                if (arg >= 0 && arg < steps.size()) {
                    this.field = Field.STEP_VALUE;
                    this.fieldStep = arg;
                    this.fieldSlot = -1;
                    MenuSound.play(MenuSound.CLICK, 1.1f, 0.3f);
                }
            }
            case A_STEP_ON -> {
                commitEdit();
                List<BotStep> steps = this.stepList();
                if (arg >= 0 && arg < steps.size()) {
                    steps.get(arg).toggle();
                    BotStore.markDirty();
                    MenuSound.play(MenuSound.TOGGLE_OFF, 1.05f, 0.4f);
                }
            }
            case A_STEP_DEL -> {
                commitEdit();
                List<BotStep> steps = this.stepList();
                if (arg >= 0 && arg < steps.size()) {
                    this.selected.removeStep(steps.get(arg));
                    BotStore.markDirty();
                    MenuSound.play(MenuSound.DROP, 0.85f, 0.5f);
                }
            }
            case A_STEP_UP, A_STEP_DOWN -> {
                commitEdit();
                List<BotStep> steps = this.stepList();
                if (arg >= 0 && arg < steps.size()) {
                    this.selected.move(steps.get(arg), action == A_STEP_UP ? -1 : 1);
                    BotStore.markDirty();
                    MenuSound.play(MenuSound.POP, 1.05f, 0.4f);
                }
            }
            case A_VALUE -> {
                int stepIndex = arg / FIELD_STRIDE;
                int slot = arg % FIELD_STRIDE;
                startEdit(Field.STEP_VALUE, stepIndex, slot);
            }
            case A_FIELD -> {
                Field[] fields = Field.values();
                if (arg >= 0 && arg < fields.length) {
                    startEdit(fields[arg], -1, -1);
                }
            }
            case A_TOGGLE -> {
                commitEdit();
                if (this.selected != null) {
                    if (arg == 0) {
                        this.selected.setAutoReconnect(!this.selected.isAutoReconnect());
                    } else {
                        this.selected.setLoopForever(!this.selected.isLoopForever());
                    }
                    BotStore.markDirty();
                    MenuSound.play(MenuSound.TOGGLE_ON, 1.05f, 0.45f);
                }
            }
            case A_CLEAR_LOG -> {
                BotLog log = BotManager.logFor(this.selected);
                log.clear();
                this.scrollLog = 0;
                MenuSound.play(MenuSound.POP, 0.95f, 0.4f);
            }
            default -> {
                // A_NONE
            }
        }
        return true;
    }

    private List<BotStep> stepList() {
        return this.selected == null ? Collections.emptyList() : this.selected.getSteps();
    }

    private void toggleRun(BotProfile profile) {
        if (BotManager.isRunning(profile)) {
            BotManager.stop(profile);
            MenuSound.play(MenuSound.TOGGLE_OFF, 1f, 0.5f);
        } else {
            BotManager.start(profile);
            MenuSound.play(MenuSound.TOGGLE_ON, 1.05f, 0.5f);
        }
    }

    private void rebuildCatalog() {
        this.catalog.clear();
        for (BotStepType type : BotStepType.values()) {
            if (type.category() == this.category) {
                this.catalog.add(type);
            }
        }
    }

    // ---- правка значений

    private void startEdit(Field kind, int step, int slot) {
        commitEdit();
        this.field = kind;
        this.fieldStep = step;
        this.fieldSlot = slot;
        this.editing = true;
        this.editFresh = true;
        this.edit.setLength(0);
        this.edit.append(currentValue(kind, step, slot));
        MenuSound.play(MenuSound.CLICK, 1.1f, 0.35f);
    }

    private String currentValue(Field kind, int step, int slot) {
        if (kind == Field.STEP_VALUE) {
            List<BotStep> steps = this.stepList();
            if (step >= 0 && step < steps.size()) {
                return steps.get(step).get(slot);
            }
            return "";
        }
        if (this.selected == null) {
            return "";
        }
        return switch (kind) {
            case NAME -> this.selected.getName();
            case HOST -> this.selected.getHost();
            case PORT -> String.valueOf(this.selected.getPort());
            case NICK -> this.selected.getNickname();
            case STEP_DELAY -> String.valueOf(this.selected.getStepDelay());
            case RECONNECT_DELAY -> String.valueOf(this.selected.getReconnectDelay());
            default -> "";
        };
    }

    private void commitEdit() {
        if (!this.editing) {
            return;
        }
        String value = this.edit.toString().trim();
        Field kind = this.field;
        int step = this.fieldStep;
        int slot = this.fieldSlot;
        this.editing = false;

        if (kind == Field.STEP_VALUE) {
            List<BotStep> steps = this.stepList();
            if (step >= 0 && step < steps.size()) {
                BotStep target = steps.get(step);
                BotStepType.Param param = target.getType() == null ? null : target.getType().param(slot);
                if (value.isEmpty() && param != null && param.kind() == BotStepType.Kind.NUMBER) {
                    value = "0";
                }
                target.set(slot, value);
                BotStore.markDirty();
            }
            return;
        }

        if (this.selected == null) {
            return;
        }
        switch (kind) {
            case NAME -> {
                if (!value.isEmpty()) {
                    String unique = BotStore.uniqueName(value);
                    BotProfile other = BotStore.find(value);
                    this.selected.setName(other == null || other == this.selected ? value : unique);
                    BotStore.markDirty();
                }
            }
            case HOST -> {
                if (!value.isEmpty()) {
                    this.selected.setHost(value);
                    BotStore.markDirty();
                }
            }
            case PORT -> {
                try {
                    this.selected.setPort(Integer.parseInt(value));
                    BotStore.markDirty();
                } catch (NumberFormatException ignored) {
                    // некорректный ввод просто не применяется
                }
            }
            case NICK -> {
                if (!value.isEmpty()) {
                    this.selected.setNickname(value);
                    BotStore.markDirty();
                }
            }
            case STEP_DELAY -> {
                try {
                    this.selected.setStepDelay(Float.parseFloat(value.replace(',', '.')));
                    BotStore.markDirty();
                } catch (NumberFormatException ignored) {
                    // оставляем прежнее значение
                }
            }
            case RECONNECT_DELAY -> {
                try {
                    this.selected.setReconnectDelay(Float.parseFloat(value.replace(',', '.')));
                    BotStore.markDirty();
                } catch (NumberFormatException ignored) {
                    // оставляем прежнее значение
                }
            }
            default -> {
                // других полей нет
            }
        }
    }

    private void cancelEdit() {
        this.editing = false;
        this.edit.setLength(0);
        this.editFresh = false;
    }

    // ---- клавиши и прокрутка

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.editing) {
            switch (keyCode) {
                case GLFW.GLFW_KEY_ENTER, GLFW.GLFW_KEY_KP_ENTER -> {
                    commitEdit();
                    return true;
                }
                case GLFW.GLFW_KEY_ESCAPE -> {
                    cancelEdit();
                    return true;
                }
                case GLFW.GLFW_KEY_BACKSPACE -> {
                    if (this.edit.length() > 0) {
                        this.edit.setLength(this.edit.length() - 1);
                    }
                    return true;
                }
                case GLFW.GLFW_KEY_DELETE -> {
                    this.edit.setLength(0);
                    return true;
                }
                case GLFW.GLFW_KEY_LEFT, GLFW.GLFW_KEY_RIGHT, GLFW.GLFW_KEY_TAB -> {
                    return true;
                }
                default -> {
                    // остальные клавиши уходят в charTyped
                }
            }
            return true;
        }

        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            onClose();
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_S && (modifiers & GLFW.GLFW_MOD_CONTROL) != 0) {
            BotStore.save();
            MenuSound.play(MenuSound.SELECT, 1.1f, 0.5f);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (!this.editing) {
            return super.charTyped(codePoint, modifiers);
        }
        if (codePoint < 32 || codePoint == 127) {
            return true;
        }
        // первое нажатие заменяет значение целиком, как в Scratch
        if (this.editFresh) {
            this.edit.setLength(0);
            this.editFresh = false;
        }
        if (this.edit.length() < 128) {
            this.edit.append(codePoint);
            MenuSound.play(MenuSound.TYPE, 1.05f, 0.25f);
        }
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        double mx = Px.px(mouseX);
        double my = Px.px(mouseY);
        float step = (float) (scrollY * S(28));

        if (inside(mx, my, this.leftX, this.profilesListY, this.leftW, this.profilesListH)) {
            this.scrollProfiles = clampScroll(this.scrollProfiles - step, this.profilesContentH, this.profilesListH);
            return true;
        }
        if (inside(mx, my, this.centerX, this.stepsListY, this.centerW, this.stepsListH)) {
            this.scrollSteps = clampScroll(this.scrollSteps - step, this.stepsContentH, this.stepsListH);
            return true;
        }
        if (inside(mx, my, this.rightX, this.catalogListY, this.rightW, this.catalogListH)) {
            this.scrollCatalog = clampScroll(this.scrollCatalog - step, this.catalogContentH, this.catalogListH);
            return true;
        }
        if (inside(mx, my, this.rightX, this.logListY, this.rightW, this.logListH)) {
            this.scrollLog = clampScroll(this.scrollLog - step, this.logContentH, this.logListH);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    private void savePeriodically() {
        long now = System.currentTimeMillis();
        if (now - this.lastSaveMs < 4_000L) {
            return;
        }
        this.lastSaveMs = now;
        BotStore.saveIfDirty();
    }

    @Override
    public void onClose() {
        commitEdit();
        BotStore.saveIfDirty();

        // Прокрутка журнала по умолчанию внизу: при следующем открытии снова
        // показываем свежие события, а не начало списка.
        this.scrollLog = Math.max(0, this.logContentH - this.logListH);

        if (this.parent == null) {
            super.onClose();
            return;
        }
        Minecraft.getInstance().setScreen(this.parent);
    }
}
