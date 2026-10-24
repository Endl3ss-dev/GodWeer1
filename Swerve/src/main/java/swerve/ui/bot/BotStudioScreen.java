package swerve.ui.bot;

import swerve.bot.BotLog;
import swerve.bot.BotManager;
import swerve.bot.BotPreset;
import swerve.bot.BotProfile;
import swerve.bot.BotRunner;
import swerve.bot.BotStep;
import swerve.bot.BotStepType;
import swerve.bot.BotStore;
import swerve.ui.menu.MenuSound;
import swerve.ui.menu.MenuText.Weight;
import swerve.ui.menu.MenuTheme;
import swerve.ui.menu.Px;
import swerve.utils.client.NotificationManager;
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
 * <p>Раскладка повторяет идею Wild: рабочее место вместо списка настроек.
 * Слева спрятана выдвижная панель профилей — она появляется, когда курсор
 * уходит к левому краю, и закрывается, когда курсор уезжает вправо. Центр —
 * карточка подключения и поведение, собранное из крупных «плашек»: их можно
 * перетаскивать мышью, вкладывать в циклы и условия, менять порядок. Справа —
 * каталог шагов с поиском и журнал с вкладками «Чат», «Команды» и «Полный
 * вывод».
 *
 * <p>Почему так, а не деревом вложенных виджетов: поведение — плоский список
 * шагов, а вложенность задаётся парными шагами (повтор…конец повтора). Такой
 * список переживает ручную правку в `bots.json`, не ломается при копировании
 * профиля и одинаково понимается и движком, и студией.
 *
 * <p>Вся геометрия в реальных пикселях окна ({@link Px#begin(GuiGraphics)}),
 * поэтому вёрстка не зависит от guiScale, а шрифт остаётся крупным.
 */
public final class BotStudioScreen extends Screen {

    // ---- цвета состояний
    private static final int C_OK = 0xFF57C97A;
    private static final int C_WARN = 0xFFE0B44C;
    private static final int C_ERROR = 0xFFE0574C;
    private static final int C_LINK = 0xFF6FA8FF;

    // ---- действия кликов
    private static final int A_NONE = 0;
    private static final int A_CLOSE = 1;
    private static final int A_PROFILE = 2;
    private static final int A_RUN = 3;
    private static final int A_NEW = 4;
    private static final int A_DUP = 5;
    private static final int A_DEL = 6;
    private static final int A_CAT = 7;
    private static final int A_ADD = 8;
    private static final int A_PLATE = 9;
    private static final int A_STEP_ON = 10;
    private static final int A_STEP_DEL = 11;
    private static final int A_STEP_UP = 12;
    private static final int A_STEP_DOWN = 13;
    private static final int A_VALUE = 14;
    private static final int A_FIELD = 15;
    private static final int A_TOGGLE = 16;
    private static final int A_CLEAR_LOG = 17;
    private static final int A_RUN_SELECTED = 18;
    private static final int A_LOG_TAB = 19;
    private static final int A_PRESET = 20;
    private static final int A_SEARCH = 21;
    private static final int A_RUN_ALL = 22;
    private static final int A_STOP_ALL = 23;
    private static final int A_ENABLE_ALL = 24;
    private static final int A_SEARCH_CLEAR = 25;
    private static final int A_PIN = 26;

    private static final int FIELD_STRIDE = 8;
    private static final int MAX_HITS = 1024;

    /** Что сейчас редактируется. */
    private enum Edit {
        NONE,
        VALUE,
        NAME,
        HOST,
        PORT,
        NICK,
        PASSWORD,
        STEP_DELAY,
        RECONNECT_DELAY
    }

    /** Цвет плашки по категории шага. */
    private static int categoryColor(BotStepType.Category category) {
        return switch (category) {
            case CONNECT -> 0xFF5B8DEF;
            case AUTH -> 0xFFE0A34C;
            case CHAT -> 0xFF4CBF7A;
            case ACTION -> 0xFF8B7BFF;
            case INVENTORY -> 0xFF3FB8B0;
            case LOGIC -> 0xFFE06A9B;
            case MISC -> 0xFF8C93A8;
        };
    }

    private final Screen parent;
    private BotProfile selected;
    private BotStepType.Category category = BotStepType.Category.CONNECT;

    // ---- геометрия окна (реальные пиксели)
    private float u = 1f;
    private int px;
    private int py;
    private int pw;
    private int ph;
    private int headerH;
    private int bodyY;
    private int bodyH;
    private int cardH;
    private int rightX;
    private int rightW;
    private int catalogH;
    private int logY;
    private int logH;
    private int drawerW;
    private int drawerDrawX;
    private int drawerX;
    private int drawerY;
    private int drawerH;

    // ---- выдвижная панель профилей
    private float drawerPos;
    private float drawerVel;
    private long lastFrameNanos;
    private boolean drawerPinned;
    private float drawerCloseDelay;

    // ---- списки и прокрутка
    private float scrollProfiles;
    private float scrollBlocks;
    private float scrollCatalog;
    private float scrollLogChat;
    private float scrollLogCommand;
    private float scrollLogOutput;
    private int profilesContentH;
    private int blocksContentH;
    private int catalogContentH;
    private int logContentH;
    private int profilesListY;
    private int profilesListH;
    private int blocksX;
    private int blocksY;
    private int blocksW;
    private int blocksH;
    private int catalogListY;
    private int catalogListH;
    private int logListY;
    private int logListH;

    // ---- журнал: вкладка
    private BotLog.Channel logTab = BotLog.Channel.CHAT;

    // ---- поиск по каталогу
    private final StringBuilder search = new StringBuilder();
    private boolean searchFocused;

    // ---- каталог
    private final List<BotStepType> catalog = new ArrayList<>();

    // ---- попадания текущего кадра
    private final int[] hitX = new int[MAX_HITS];
    private final int[] hitY = new int[MAX_HITS];
    private final int[] hitW = new int[MAX_HITS];
    private final int[] hitH = new int[MAX_HITS];
    private final int[] hitAction = new int[MAX_HITS];
    private final int[] hitArg = new int[MAX_HITS];
    private int hitCount;

    // ---- раскладка плашек текущего кадра
    private static final int MAX_PLATES = 512;
    private final int[] plateX = new int[MAX_PLATES];
    private final int[] plateY = new int[MAX_PLATES];
    private final int[] plateW = new int[MAX_PLATES];
    private final int[] plateH = new int[MAX_PLATES];
    private final int[] plateIndex = new int[MAX_PLATES];
    private int plateCount;
    private int hoverPlate = -1;

    // ---- перетаскивание плашек
    private BotStep pressedStep;
    private int pressedIndex = -1;
    private double pressX;
    private double pressY;
    private boolean dragging;
    private BotStep dragStep;
    private int dragOrigin = -1;
    private int dropIndex = -1;

    // ---- редактирование значений
    private Edit editing = Edit.NONE;
    private int editStep = -1;
    private int editSlot = -1;
    private final StringBuilder edit = new StringBuilder();
    private boolean editFresh;

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
        return Math.max(1, (int) Math.round(value * this.u));
    }

    // ------------------------------------------------------------- отрисовка

    @Override
    public void renderBackground(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        Minecraft mc = Minecraft.getInstance();
        g.fill(0, 0, mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight(), 0xE00A0A10);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        super.render(g, mouseX, mouseY, partialTick);

        this.hitCount = 0;
        this.plateCount = 0;
        this.hoverPlate = -1;
        layout();

        double mx = Px.px(mouseX);
        double my = Px.px(mouseY);

        animateDrawer(mx, my);

        Px.begin(g);
        try {
            frame(g);
            header(g, mx, my);
            center(g, mx, my);
            rightColumn(g, mx, my);
            // Панель профилей поверх контента — как выдвижной ящик.
            ScissorUtils.push();
            ScissorUtils.set(this.px, Px.height() - (this.py + this.ph), this.pw, this.ph);
            profilesDrawer(g, mx, my);
            ScissorUtils.pop();
            if (this.dragging && this.dragStep != null) {
                dragGhost(g, mx, my);
            }
        } finally {
            Px.end(g);
        }

        savePeriodically();
    }

    private void layout() {
        int sw = Px.width();
        int sh = Px.height();

        // Окно занимает почти весь экран: текст крупный, места хватает.
        this.pw = Math.min(sw - S(16), S(1560));
        this.ph = Math.min(sh - S(16), S(966));
        this.u = Math.max(0.70f, Math.min(1.45f, Math.min(this.pw / 1560f, this.ph / 966f)));
        this.px = (sw - this.pw) / 2;
        this.py = (sh - this.ph) / 2;

        this.headerH = S(76);
        this.bodyY = this.py + this.headerH;
        this.bodyH = this.ph - this.headerH - S(14);

        this.rightW = S(392);
        this.rightX = this.px + this.pw - S(16) - this.rightW;
        this.cardH = S(214);

        this.catalogH = Math.round(this.bodyH * 0.52f);
        this.logY = this.bodyY + this.catalogH + S(12);
        this.logH = this.bodyY + this.bodyH - this.logY;

        this.drawerW = S(324);
        this.drawerX = S(12);
        this.drawerY = this.bodyY;
        this.drawerH = this.bodyH;

        this.profilesListY = this.drawerY + S(86);
        this.profilesListH = this.drawerY + this.drawerH - S(70) - this.profilesListY;

        this.blocksX = this.px + S(16);
        this.blocksY = this.bodyY + this.cardH + S(12) + S(104);
        this.blocksW = this.rightX - S(14) - this.blocksX;
        this.blocksH = this.bodyY + this.bodyH - this.blocksY - S(10);

        this.catalogListY = this.bodyY + S(44) + S(48) + S(78);
        this.catalogListH = this.bodyY + this.catalogH - this.catalogListY - S(10);

        this.logListY = this.logY + S(50);
        this.logListH = this.logY + this.logH - this.logListY - S(10);
    }

    private void animateDrawer(double mx, double my) {
        long now = System.nanoTime();
        float dt = this.lastFrameNanos == 0 ? 0.016f
                : Math.min(0.05f, (now - this.lastFrameNanos) / 1_000_000_000f);
        this.lastFrameNanos = now;

        boolean nearEdge = mx < this.px + S(30) && my > this.bodyY - S(20);
        boolean insideDrawer = this.drawerPos > 0.35f
                && mx < this.px + S(18) + this.drawerW * Math.min(1f, this.drawerPos) + S(26)
                && my > this.drawerY - S(10) && my < this.drawerY + this.drawerH + S(10);

        if (nearEdge || insideDrawer || this.drawerPinned) {
            this.drawerCloseDelay = 0f;
        } else {
            this.drawerCloseDelay += dt;
        }

        float target = (nearEdge || insideDrawer || this.drawerPinned) ? 1f : 0f;
        if (target == 0f && this.drawerCloseDelay < 0.28f) {
            target = 1f; // небольшая задержка, чтобы панель не мигала на выходе курсора
        }

        // Пружина: недодемпфированная, поэтому панель чуть проскакивает и
        // возвращается — тот самый bounce.
        float accel = (target - this.drawerPos) * 240f - this.drawerVel * 17f;
        this.drawerVel += accel * dt;
        this.drawerPos += this.drawerVel * dt;
        if (this.drawerPos > 1.06f) {
            this.drawerPos = 1.06f;
            if (this.drawerVel > 0f) {
                this.drawerVel = 0f;
            }
        }
        if (this.drawerPos < -0.04f) {
            this.drawerPos = -0.04f;
            if (this.drawerVel < 0f) {
                this.drawerVel = 0f;
            }
        }
    }

    private void frame(GuiGraphics g) {
        Px.shadow(g, this.px, this.py, this.pw, this.ph, S(30), Px.a(0xFF000000, 120));
        Px.round(g, this.px, this.py, this.pw, this.ph, S(22), Px.a(MenuTheme.card(), 250));
        Px.outline(g, this.px, this.py, this.pw, this.ph, S(22), Px.a(MenuTheme.border(), 220));
        Px.rect(g, this.px + S(16), this.py + this.headerH - S(1), this.pw - S(32), S(1),
                Px.a(MenuTheme.text(), 18));
    }

    private void header(GuiGraphics g, double mx, double my) {
        int cy = this.py + this.headerH / 2;
        int icon = S(34);
        int ix = this.px + S(38);
        Px.round(g, ix - icon / 2, cy - icon / 2, icon, icon, S(11), Px.a(MenuTheme.accent(), 38));
        Px.icon(g, "server", ix - S(11), cy - S(11), S(22), MenuTheme.accent());

        int tx = ix + icon / 2 + S(14);
        Px.text(g, "Студия ботов", tx, cy - S(24), Weight.BOLD, S(22), MenuTheme.text());
        Px.text(g, "headless-клиенты · протокол 1.21.4 · "
                        + BotStore.bots().size() + " профилей", tx, cy + S(1),
                Weight.MEDIUM, S(13), MenuTheme.muted());

        int running = BotManager.runningCount();
        String label = running == 0 ? "боты остановлены" : "работают: " + running;
        int fs = S(13);
        int w = Px.textWidth(label, Weight.SEMIBOLD, fs) + S(40);
        int h = S(34);
        int bx = this.px + this.pw - S(72) - w;
        int by = cy - h / 2;
        boolean live = running > 0;
        Px.round(g, bx, by, w, h, h / 2, Px.a(live ? C_OK : MenuTheme.text(), live ? 34 : 14));
        Px.outline(g, bx, by, w, h, h / 2, Px.a(live ? C_OK : MenuTheme.border(), live ? 110 : 90));
        if (live) {
            Px.circle(g, bx + S(18), cy, S(5), Px.a(C_OK, 70));
        }
        Px.circle(g, bx + S(18), cy, S(3), live ? C_OK : MenuTheme.dim());
        Px.text(g, label, bx + S(30), cy - fs / 2, Weight.SEMIBOLD, fs,
                live ? MenuTheme.text() : MenuTheme.muted());

        int size = S(38);
        int x = this.px + this.pw - S(20) - size;
        int y = cy - size / 2;
        boolean hv = inside(mx, my, x, y, size, size);
        Px.round(g, x, y, size, size, S(12), Px.a(hv ? C_ERROR : MenuTheme.text(), hv ? 40 : 12));
        Px.icon(g, "x", x + S(10), y + S(10), S(18), hv ? C_ERROR : MenuTheme.muted());
        hit(x, y, size, size, A_CLOSE, 0);
    }

    // ---- центр: подключение + поведение

    private void center(GuiGraphics g, double mx, double my) {
        connectionCard(g, mx, my);
        behaviorPanel(g, mx, my);
    }

    private void connectionCard(GuiGraphics g, double mx, double my) {
        int x = this.px + S(16);
        int y = this.bodyY;
        int w = this.rightX - S(14) - x;
        panel(g, x, y, w, this.cardH);

        int headY = y + S(18);
        Px.icon(g, "sliders", x + S(18), headY + S(1), S(18), MenuTheme.accent());
        Px.text(g, "Подключение", x + S(44), headY, Weight.SEMIBOLD, S(16), MenuTheme.text());

        if (this.selected == null) {
            Px.textCenter(g, "Создайте профиль: потяните курсор к левому краю окна",
                    x + w / 2, y + this.cardH / 2 - S(8), Weight.MEDIUM, S(14), MenuTheme.muted());
            Px.textCenter(g, "панель «Профили» выедет слева",
                    x + w / 2, y + this.cardH / 2 + S(14), Weight.MEDIUM, S(12), MenuTheme.dim());
            return;
        }

        BotRunner runner = BotManager.runner(this.selected);
        BotConnectionInfo info = connectionInfo(runner);
        int fs = S(12);
        Px.text(g, info.text, x + S(44) + Px.textWidth("Подключение", Weight.SEMIBOLD, S(16)) + S(14),
                headY + S(3), Weight.MEDIUM, fs, info.color);

        boolean running = BotManager.isRunning(this.selected);
        String label = running ? "Остановить" : "Запустить";
        int bw = Px.textWidth(label, Weight.SEMIBOLD, S(14)) + S(66);
        int bh = S(38);
        int bx = x + w - S(18) - bw;
        int by = headY - S(6);
        boolean hv = inside(mx, my, bx, by, bw, bh);
        Px.round(g, bx, by, bw, bh, S(12), Px.a(running ? C_ERROR : C_OK, hv ? 78 : 52));
        Px.outline(g, bx, by, bw, bh, S(12), Px.a(running ? C_ERROR : C_OK, hv ? 200 : 140));
        Px.icon(g, running ? "stop" : "play", bx + S(14), by + S(11), S(16),
                running ? C_ERROR : C_OK);
        Px.text(g, label, bx + S(40), by + S(11), Weight.SEMIBOLD, S(14),
                running ? C_ERROR : MenuTheme.text());
        hit(bx, by, bw, bh, A_RUN_SELECTED, 0);

        int pad = S(18);
        int innerW = w - pad * 2;
        int gap = S(12);
        int row1 = y + S(52);
        int row2 = row1 + S(84);

        int wName = Math.round(innerW * 0.28f);
        int wHost = Math.round(innerW * 0.44f);
        int wPort = innerW - wName - wHost - gap * 2;
        field(g, "Имя профиля", this.selected.getName(), Edit.NAME,
                x + pad, row1, wName, mx, my);
        field(g, "Сервер", this.selected.getHost(), Edit.HOST,
                x + pad + wName + gap, row1, wHost, mx, my);
        field(g, "Порт", String.valueOf(this.selected.getPort()), Edit.PORT,
                x + pad + wName + wHost + gap * 2, row1, wPort, mx, my);

        int wNick = Math.round(innerW * 0.20f);
        int wPass = Math.round(innerW * 0.20f);
        int wDelay = Math.round(innerW * 0.15f);
        int wRecon = Math.round(innerW * 0.15f);
        int wToggle = (innerW - wNick - wPass - wDelay - wRecon - gap * 4) / 2;
        int fx = x + pad;
        field(g, "Ник в игре", this.selected.getNickname(), Edit.NICK, fx, row2, wNick, mx, my);
        field(g, "Пароль", this.selected.getPassword(), Edit.PASSWORD,
                fx + wNick + gap, row2, wPass, mx, my);
        field(g, "Пауза шага, с", trim(this.selected.getStepDelay()), Edit.STEP_DELAY,
                fx + wNick + wPass + gap * 2, row2, wDelay, mx, my);
        field(g, "Переподкл., с", trim(this.selected.getReconnectDelay()), Edit.RECONNECT_DELAY,
                fx + wNick + wPass + wDelay + gap * 3, row2, wRecon, mx, my);

        int tx = fx + wNick + wPass + wDelay + wRecon + gap * 4;
        toggle(g, "Автовход", this.selected.isAutoReconnect(), tx, row2, wToggle, 0, mx, my);
        toggle(g, "Повтор", this.selected.isLoopForever(), tx + wToggle + gap, row2, wToggle, 1, mx, my);
    }

    /** Строка состояния рядом с заголовком карточки. */
    private record BotConnectionInfo(String text, int color) {
    }

    private BotConnectionInfo connectionInfo(BotRunner runner) {
        if (runner == null) {
            return new BotConnectionInfo("не запущен", MenuTheme.dim());
        }
        var connection = runner.connection();
        return switch (connection.state()) {
            case PLAY -> new BotConnectionInfo(String.format("в игре · %.1f %.1f %.1f · ♥ %.0f",
                    connection.x(), connection.y(), connection.z(), connection.health()), C_OK);
            case CONNECTING, LOGIN, CONFIGURATION -> new BotConnectionInfo(
                    "подключение… (" + connection.state().displayName() + ")", C_WARN);
            case ERROR -> new BotConnectionInfo("ошибка: " + connection.lastError(), C_ERROR);
            default -> new BotConnectionInfo("не запущен", MenuTheme.dim());
        };
    }

    private void field(GuiGraphics g, String caption, String value, Edit kind,
                       int x, int y, int w, double mx, double my) {
        Px.text(g, caption, x + S(2), y, Weight.MEDIUM, S(12), MenuTheme.muted());

        int by = y + S(20);
        int bh = S(44);
        boolean hovered = inside(mx, my, x, by, w, bh);
        boolean active = this.editing == kind;

        String shown = active ? this.edit.toString() : value;
        int fs = S(15);
        Px.round(g, x, by, w, bh, S(11), Px.a(MenuTheme.control(), active ? 245 : 200));
        Px.outline(g, x, by, w, bh, S(11),
                Px.a(active ? MenuTheme.accent() : MenuTheme.border(),
                        active ? 220 : hovered ? 160 : 110));
        Px.text(g, Px.ellipsize(shown, Weight.MEDIUM, fs, w - S(22)), x + S(11),
                by + (bh - fs) / 2, Weight.MEDIUM, fs,
                shown.isEmpty() ? MenuTheme.dim() : MenuTheme.text());

        if (active) {
            int caret = x + S(11) + Math.min(w - S(18), Px.textWidth(shown, Weight.MEDIUM, fs));
            Px.rect(g, caret, by + S(11), S(2), bh - S(22), MenuTheme.accent());
        }

        hit(x, by, w, bh, A_FIELD, kind.ordinal());
    }

    private void toggle(GuiGraphics g, String caption, boolean on, int x, int y, int w, int index,
                        double mx, double my) {
        Px.text(g, caption, x + S(2), y, Weight.MEDIUM, S(12), MenuTheme.muted());
        int by = y + S(20);
        int bh = S(44);
        boolean hovered = inside(mx, my, x, by, w, bh);
        Px.round(g, x, by, w, bh, S(11), Px.a(MenuTheme.control(), 200));
        Px.outline(g, x, by, w, bh, S(11), Px.a(hovered ? MenuTheme.text() : MenuTheme.border(),
                hovered ? 160 : 110));

        int sw = S(38);
        int sh = S(22);
        int sx = x + S(10);
        int sy = by + (bh - sh) / 2;
        Px.round(g, sx, sy, sw, sh, sh / 2, Px.a(on ? C_OK : MenuTheme.text(), on ? 225 : 50));
        Px.circle(g, on ? sx + sw - sh / 2 : sx + sh / 2, sy + sh / 2, S(7),
                on ? Px.onAccent(C_OK) : MenuTheme.muted());

        Px.text(g, on ? "вкл" : "выкл", sx + sw + S(10), by + (bh - S(12)) / 2,
                Weight.SEMIBOLD, S(12), on ? MenuTheme.text() : MenuTheme.muted());
        hit(x, by, w, bh, A_TOGGLE, index);
    }

    // ---- поведение: крупные плашки

    private void behaviorPanel(GuiGraphics g, double mx, double my) {
        int x = this.px + S(16);
        int y = this.bodyY + this.cardH + S(12);
        int w = this.rightX - S(14) - x;
        int h = this.bodyY + this.bodyH - y;
        panel(g, x, y, w, h);

        int headY = y + S(18);
        Px.icon(g, "list", x + S(18), headY + S(1), S(18), MenuTheme.accent());
        Px.text(g, "Поведение", x + S(44), headY, Weight.SEMIBOLD, S(16), MenuTheme.text());

        List<BotStep> steps = this.selected == null ? Collections.emptyList() : this.selected.getSteps();
        BotRunner runner = BotManager.runner(this.selected);
        String info = (runner != null && runner.isRunning())
                ? "шаг " + Math.min(steps.size(), runner.currentStep() + 1) + " / " + steps.size()
                : steps.size() + " шагов";
        Px.text(g, info, x + S(44) + Px.textWidth("Поведение", Weight.SEMIBOLD, S(16)) + S(14),
                headY + S(3), Weight.MEDIUM, S(12), MenuTheme.dim());

        // переключатель «все шаги включены»
        int bs = S(34);
        int bx = x + w - S(18) - bs;
        int by = headY - S(7);
        boolean allOn = steps.isEmpty();
        int enabled = 0;
        for (int i = 0, size = steps.size(); i < size; i++) {
            if (steps.get(i).isEnabled()) {
                enabled++;
            }
        }
        allOn = !steps.isEmpty() && enabled == steps.size();
        boolean hv = inside(mx, my, bx, by, bs, bs);
        Px.round(g, bx, by, bs, bs, S(10), Px.a(allOn ? C_OK : MenuTheme.text(), hv ? 46 : 20));
        Px.icon(g, allOn ? "eye" : "eye-off", bx + S(8), by + S(8), S(18),
                allOn ? C_OK : MenuTheme.muted());
        hit(bx, by, bs, bs, A_ENABLE_ALL, 0);

        // шаблоны поведения
        int presetY = y + S(50);
        int presetH = S(34);
        int presetX = x + S(18);
        BotPreset[] presets = BotPreset.values();
        for (int i = 0; i < presets.length; i++) {
            BotPreset preset = presets[i];
            String label = preset.title();
            int fs = S(12);
            int wBox = Px.textWidth(label, Weight.SEMIBOLD, fs) + S(28);
            if (presetX + wBox > x + w - S(18)) {
                break;
            }
            boolean hovered = inside(mx, my, presetX, presetY, wBox, presetH);
            Px.round(g, presetX, presetY, wBox, presetH, S(10),
                    Px.a(MenuTheme.accent(), hovered ? 54 : 26));
            Px.outline(g, presetX, presetY, wBox, presetH, S(10),
                    Px.a(MenuTheme.accent(), hovered ? 170 : 80));
            Px.icon(g, "plus", presetX + S(8), presetY + S(10), S(14), MenuTheme.accent());
            Px.text(g, label, presetX + S(26), presetY + (presetH - fs) / 2, Weight.SEMIBOLD, fs,
                    hovered ? MenuTheme.text() : MenuTheme.muted());
            hit(presetX, presetY, wBox, presetH, A_PRESET, i);
            presetX += wBox + S(8);
        }

        // подсказка справа от шаблонов
        Px.textRight(g, "тяни плашку мышью · клик по значению — правка",
                x + w - S(18), presetY + S(11), Weight.MEDIUM, S(11), MenuTheme.dim());

        blocks(g, mx, my);
    }

    private void blocks(GuiGraphics g, double mx, double my) {
        List<BotStep> steps = this.selected == null ? Collections.emptyList() : this.selected.getSteps();

        if (this.selected == null || steps.isEmpty()) {
            Px.textCenter(g, this.selected == null ? "Выберите профиль"
                            : "Пусто: добавьте шаг из каталога справа или шаблон выше",
                    this.blocksX + this.blocksW / 2, this.blocksY + S(40),
                    Weight.MEDIUM, S(15), MenuTheme.muted());
            this.blocksContentH = 0;
            return;
        }

        int gap = S(6);
        int headerH = S(58);
        int plateH = S(52);
        int closerH = S(36);
        int indent = S(30);

        ScissorUtils.push();
        ScissorUtils.set(this.blocksX, Px.height() - (this.blocksY + this.blocksH),
                this.blocksW, this.blocksH);

        int depth = 0;
        int prevDepth = -1;
        boolean prevCloser = false;

        // раскладка: сначала считаем высоту, чтобы прокрутка знала размер
        int total = S(8);
        int d = 0;
        int lastDepth = -1;
        for (int i = 0, size = steps.size(); i < size; i++) {
            BotStep step = steps.get(i);
            BotStepType type = step.getType();
            if (type != null && type.closesBlock()) {
                d = Math.max(0, d - 1);
            }
            total += (type != null && type.closesBlock() ? closerH
                    : type != null && type.opensBlock() ? headerH : plateH) + gap;
            if (type != null && type.opensBlock()) {
                d++;
            }
            lastDepth = d;
        }
        this.blocksContentH = total + Math.max(0, lastDepth) * S(4);
        int y = this.blocksY - Math.round(clampScroll(this.scrollBlocks, this.blocksContentH, this.blocksH));

        for (int i = 0, size = steps.size(); i < size; i++) {
            BotStep step = steps.get(i);
            BotStepType type = step.getType();
            if (type == null) {
                continue;
            }
            boolean closer = type.closesBlock();
            if (closer) {
                depth = Math.max(0, depth - 1);
            }
            int h = closer ? closerH : type.opensBlock() ? headerH : plateH;
            int plateX = this.blocksX + S(10) + depth * indent;
            int plateW = this.blocksW - S(20) - depth * indent;
            boolean first = prevDepth != depth || prevCloser || i == 0;

            if (y + h >= this.blocksY - gap && y <= this.blocksY + this.blocksH) {
                drawPlate(g, step, i, type, plateX, y, plateW, h, depth,
                        first, closer, mx, my);
                if (inside(mx, my, plateX, y, plateW, h) && this.hoverPlate < 0) {
                    this.hoverPlate = i;
                }
                if (this.plateCount < MAX_PLATES) {
                    this.plateX[this.plateCount] = plateX;
                    this.plateY[this.plateCount] = y;
                    this.plateW[this.plateCount] = plateW;
                    this.plateH[this.plateCount] = h;
                    this.plateIndex[this.plateCount] = i;
                    this.plateCount++;
                }
            }

            y += h + gap;
            prevDepth = depth;
            prevCloser = closer;
            if (type.opensBlock()) {
                depth++;
            }
        }

        // индикатор вставки при перетаскивании
        if (this.dragging && this.dropIndex >= 0) {
            int indicatorY = insertionY(this.dropIndex, steps, gap);
            if (indicatorY >= this.blocksY - S(10) && indicatorY <= this.blocksY + this.blocksH) {
                Px.round(g, this.blocksX + S(14), indicatorY - S(2), this.blocksW - S(28), S(5), S(3),
                        MenuTheme.accent());
                Px.circle(g, this.blocksX + S(14), indicatorY + S(1), S(5), MenuTheme.accent());
            }
        }

        ScissorUtils.pop();
    }

    /** Y индикатора вставки для позиции {@code index}. */
    private int insertionY(int index, List<BotStep> steps, int gap) {
        if (this.plateCount == 0) {
            return this.blocksY;
        }
        for (int i = 0; i < this.plateCount; i++) {
            if (this.plateIndex[i] == index) {
                return this.plateY[i] - gap / 2;
            }
        }
        int last = this.plateCount - 1;
        return this.plateY[last] + this.plateH[last] + gap / 2;
    }

    private void drawPlate(GuiGraphics g, BotStep step, int index, BotStepType type,
                           int x, int y, int w, int h, int depth,
                           boolean first, boolean closer, double mx, double my) {
        BotStepType.Category cat = type.category();
        int color = categoryColor(cat);
        boolean hovered = inside(mx, my, x, y, w, h);
        boolean active = this.dragging && this.dropIndex == index;

        int body = closer
                ? Px.a(MenuTheme.text(), 18)
                : Px.mix(MenuTheme.card() | 0xFF000000, color, hovered ? 0.30f : 0.22f);
        int border = Px.a(closer ? MenuTheme.border() : color,
                closer ? 120 : active ? 235 : hovered ? 160 : 95);

        // соединительный «язычок»: заполняет зазор и держит плашки вместе
        if (!first) {
            Px.round(g, x + S(22), y - S(6), S(58), S(20), S(9), body);
        }
        Px.round(g, x, y, w, h, S(12), body);
        Px.outline(g, x, y, w, h, S(12), border);

        if (depth > 0) {
            // вертикальная направляющая вложенности
            Px.rect(g, x - S(16), y, S(3), h, Px.a(color, 90));
        }

        int fs = S(15);
        int cy = y + (h - fs) / 2;

        if (closer) {
            Px.text(g, type.describe(new String[0]), x + S(24), cy, Weight.SEMIBOLD, S(13),
                    MenuTheme.dim());
            hit(x, y, w, h, A_PLATE, index);
            return;
        }

        // номер шага
        Px.text(g, String.valueOf(index + 1), x + S(14), cy, Weight.SEMIBOLD, S(12),
                Px.a(MenuTheme.text(), 120));

        hit(x, y, w, h, A_PLATE, index);

        // включатель
        int dotX = x + S(38);
        int dotY = y + h / 2;
        boolean on = step.isEnabled();
        Px.round(g, dotX - S(5), dotY - S(9), S(10), S(18), S(5),
                Px.a(on ? C_OK : MenuTheme.text(), on ? 220 : 40));
        Px.circle(g, dotX, on ? dotY - S(4) : dotY + S(4), S(3),
                on ? Px.onAccent(C_OK) : MenuTheme.muted());
        hit(dotX - S(10), y + S(4), S(20), h - S(8), A_STEP_ON, index);

        // текст шага со значениями
        int cx = x + S(58);
        int limit = x + w - S(96);
        boolean truncated = false;

        for (int word = 0; word < type.paramCount() + 1; word++) {
            String word_ = type.word(word);
            if (!word_.isEmpty()) {
                int tw = Px.textWidth(word_, Weight.MEDIUM, fs);
                if (cx + tw > limit) {
                    truncated = true;
                    break;
                }
                Px.text(g, word_, cx, cy, Weight.MEDIUM, fs, Px.a(MenuTheme.text(), 210));
                cx += tw + S(4);
            }
            if (word < type.paramCount()) {
                String value = step.get(word);
                int vw = Math.min(Px.textWidth(value, Weight.SEMIBOLD, S(14)), Math.max(S(8), limit - cx));
                if (cx + vw > limit) {
                    truncated = true;
                    break;
                }
                int chipW = Math.max(S(26), vw + S(16));
                int chipH = h - S(16);
                boolean chipHover = inside(mx, my, cx, y + S(8), chipW, chipH);
                boolean chipActive = this.editing == Edit.VALUE
                        && this.editStep == index && this.editSlot == word;
                Px.round(g, cx, y + S(8), chipW, chipH, S(8),
                        Px.a(color, chipActive ? 110 : chipHover ? 78 : 52));
                Px.outline(g, cx, y + S(8), chipW, chipH, S(8),
                        Px.a(color, chipActive ? 235 : chipHover ? 180 : 110));
                String shown = chipActive ? this.edit.toString() : value;
                Px.text(g, Px.ellipsize(shown, Weight.SEMIBOLD, S(14), chipW - S(14)),
                        cx + S(7), y + (h - S(14)) / 2, Weight.SEMIBOLD, S(14),
                        chipActive ? MenuTheme.text() : Px.mix(MenuTheme.text(), color, 0.35f));
                if (chipActive) {
                    int caret = cx + S(7) + Math.min(chipW - S(14),
                            Px.textWidth(shown, Weight.SEMIBOLD, S(14)));
                    Px.rect(g, caret, y + S(11), S(2), chipH - S(6), color);
                }
                hit(cx, y + S(8), chipW, chipH, A_VALUE, index * FIELD_STRIDE + word);
                cx += chipW + S(6);
            }
        }
        if (truncated) {
            Px.text(g, "…", Math.min(cx, limit), cy, Weight.MEDIUM, fs, MenuTheme.dim());
        }

        // кнопки строки — только у наведённой плашки
        if (hovered) {
            int bs = S(26);
            int by = y + (h - bs) / 2;
            int bx = x + w - S(8) - bs;
            boolean hvDel = inside(mx, my, bx, by, bs, bs);
            Px.round(g, bx, by, bs, bs, S(8), Px.a(C_ERROR, hvDel ? 80 : 30));
            Px.icon(g, "x", bx + S(6), by + S(6), S(14), hvDel ? C_ERROR : MenuTheme.muted());
            hit(bx, by, bs, bs, A_STEP_DEL, index);

            bx -= bs + S(6);
            boolean hvDown = inside(mx, my, bx, by, bs, bs);
            Px.round(g, bx, by, bs, bs, S(8), Px.a(MenuTheme.text(), hvDown ? 46 : 18));
            Px.icon(g, "chev-down", bx + S(6), by + S(6), S(14), MenuTheme.muted());
            hit(bx, by, bs, bs, A_STEP_DOWN, index);

            bx -= bs + S(6);
            boolean hvUp = inside(mx, my, bx, by, bs, bs);
            Px.round(g, bx, by, bs, bs, S(8), Px.a(MenuTheme.text(), hvUp ? 46 : 18));
            Px.icon(g, "chev-up", bx + S(6), by + S(6), S(14), MenuTheme.muted());
            hit(bx, by, bs, bs, A_STEP_UP, index);
        }
    }

    private void dragGhost(GuiGraphics g, double mx, double my) {
        int w = S(430);
        int h = S(52);
        int x = (int) mx - S(40);
        int y = (int) my - h / 2;
        int color = categoryColor(this.dragStep.getType().category());
        Px.fade = 0.85f;
        Px.round(g, x, y, w, h, S(12), Px.mix(MenuTheme.card() | 0xFF000000, color, 0.32f));
        Px.outline(g, x, y, w, h, S(12), Px.a(color, 220));
        Px.text(g, Px.ellipsize(this.dragStep.describe(), Weight.SEMIBOLD, S(14), w - S(40)),
                x + S(18), y + (h - S(14)) / 2, Weight.SEMIBOLD, S(14), MenuTheme.text());
        Px.fade = 1f;
    }

    // ---- правая колонка: каталог и журнал

    private void rightColumn(GuiGraphics g, double mx, double my) {
        catalogPanel(g, mx, my);
        logPanel(g, mx, my);
    }

    private void catalogPanel(GuiGraphics g, double mx, double my) {
        int x = this.rightX;
        int y = this.bodyY;
        int w = this.rightW;
        panel(g, x, y, w, this.catalogH);

        int headY = y + S(16);
        Px.icon(g, "apps", x + S(16), headY + S(1), S(16), MenuTheme.accent());
        Px.text(g, "Каталог шагов", x + S(40), headY, Weight.SEMIBOLD, S(15), MenuTheme.text());
        Px.textRight(g, catalogSize() + " шагов", x + w - S(16), headY + S(2),
                Weight.MEDIUM, S(12), MenuTheme.dim());

        // поиск
        int sx = x + S(14);
        int sw = w - S(28);
        int sy = y + S(44);
        int sh = S(42);
        boolean focused = this.searchFocused;
        Px.round(g, sx, sy, sw, sh, S(11), Px.a(MenuTheme.control(), focused ? 245 : 195));
        Px.outline(g, sx, sy, sw, sh, S(11), Px.a(focused ? MenuTheme.accent() : MenuTheme.border(),
                focused ? 220 : 110));
        Px.icon(g, "search", sx + S(12), sy + S(12), S(16), focused ? MenuTheme.accent() : MenuTheme.muted());
        String query = this.search.toString();
        int fs = S(14);
        String shown = query.isEmpty() && !focused ? "Поиск шага: слово или команда" : query;
        Px.text(g, Px.ellipsize(shown, Weight.MEDIUM, fs, sw - S(56)), sx + S(36),
                sy + (sh - fs) / 2, Weight.MEDIUM, fs,
                query.isEmpty() && !focused ? MenuTheme.dim() : MenuTheme.text());
        if (focused) {
            int caret = sx + S(36) + Math.min(sw - S(50), Px.textWidth(query, Weight.MEDIUM, fs));
            Px.rect(g, caret, sy + S(11), S(2), sh - S(22), MenuTheme.accent());
        }
        if (!query.isEmpty()) {
            int cbx = sx + sw - S(32);
            boolean hv = inside(mx, my, cbx, sy, S(28), sh);
            Px.icon(g, "x", cbx + S(7), sy + S(13), S(14), hv ? MenuTheme.text() : MenuTheme.dim());
            hit(cbx, sy, S(28), sh, A_SEARCH_CLEAR, 0);
        }
        hit(sx, sy, sw, sh, A_SEARCH, 0);

        // категории — сетка 4×2
        int pad = S(14);
        int gap = S(6);
        int chipW = (w - pad * 2 - gap * 3) / 4;
        int chipH = S(32);
        BotStepType.Category[] categories = BotStepType.Category.values();
        for (int i = 0; i < categories.length; i++) {
            int col = i % 4;
            int row = i / 4;
            int cx = x + pad + col * (chipW + gap);
            int cy = y + S(44) + S(48) + row * (chipH + gap);
            boolean activeCat = categories[i] == this.category && query.isEmpty();
            boolean hv = inside(mx, my, cx, cy, chipW, chipH);
            Px.round(g, cx, cy, chipW, chipH, S(9),
                    Px.a(activeCat ? categoryColor(categories[i]) : MenuTheme.text(),
                            activeCat ? 66 : hv ? 26 : 12));
            if (activeCat) {
                Px.outline(g, cx, cy, chipW, chipH, S(9), Px.a(categoryColor(categories[i]), 180));
            }
            Px.textCenter(g, categories[i].displayName(), cx + chipW / 2, cy + (chipH - S(12)) / 2,
                    activeCat ? Weight.SEMIBOLD : Weight.MEDIUM, S(12),
                    activeCat ? MenuTheme.text() : MenuTheme.muted());
            hit(cx, cy, chipW, chipH, A_CAT, i);
        }

        // список шагов
        ScissorUtils.push();
        ScissorUtils.set(x, Px.height() - (this.catalogListY + this.catalogListH), w, this.catalogListH);

        int rowH = S(38);
        this.catalogContentH = this.catalog.size() * rowH;
        int baseY = this.catalogListY - Math.round(clampScroll(this.scrollCatalog,
                this.catalogContentH, this.catalogListH));
        int fsRow = S(13);

        for (int i = 0, size = this.catalog.size(); i < size; i++) {
            BotStepType type = this.catalog.get(i);
            int ry = baseY + i * rowH;
            if (ry + rowH < this.catalogListY || ry > this.catalogListY + this.catalogListH) {
                continue;
            }
            int rx = x + S(12);
            int rw = w - S(24);
            int rh = rowH - S(4);
            boolean hv = inside(mx, my, rx, ry, rw, rh);
            int color = categoryColor(type.category());
            Px.round(g, rx, ry, rw, rh, S(9), Px.a(hv ? color : MenuTheme.text(), hv ? 44 : 14));
            Px.round(g, rx + S(6), ry + S(8), S(4), rh - S(16), S(2), Px.a(color, 200));
            Px.text(g, Px.ellipsize(preview(type), Weight.MEDIUM, fsRow, rw - S(52)),
                    rx + S(18), ry + (rh - fsRow) / 2, Weight.MEDIUM, fsRow,
                    hv ? MenuTheme.text() : Px.a(MenuTheme.text(), 200));
            Px.icon(g, "plus", rx + rw - S(24), ry + (rh - S(14)) / 2, S(14),
                    hv ? color : MenuTheme.dim());
            hit(rx, ry, rw, rh, A_ADD, i);
        }
        ScissorUtils.pop();

        if (this.catalog.isEmpty()) {
            Px.textCenter(g, "Ничего не найдено", x + w / 2, this.catalogListY + S(18),
                    Weight.MEDIUM, S(13), MenuTheme.dim());
        }
    }

    private String preview(BotStepType type) {
        String[] values = new String[type.paramCount()];
        for (int i = 0; i < values.length; i++) {
            values[i] = type.param(i) == null ? "" : type.param(i).defaultValue();
        }
        return type.describe(values);
    }

    private int catalogSize() {
        if (!this.search.toString().isBlank()) {
            return this.catalog.size();
        }
        int count = 0;
        for (BotStepType type : BotStepType.values()) {
            if (type.category() == this.category) {
                count++;
            }
        }
        return count;
    }

    private void logPanel(GuiGraphics g, double mx, double my) {
        int x = this.rightX;
        int y = this.logY;
        int w = this.rightW;
        int h = this.logH;
        panel(g, x, y, w, h);

        BotLog log = BotManager.logFor(this.selected);
        List<BotLog.Entry> entries = entriesFor(log, this.logTab);

        int headY = y + S(16);
        Px.icon(g, "scroll", x + S(16), headY + S(1), S(16), MenuTheme.accent());
        Px.text(g, "Журнал", x + S(40), headY, Weight.SEMIBOLD, S(15), MenuTheme.text());
        Px.textRight(g, entries.size() + " записей", x + w - S(56), headY + S(2),
                Weight.MEDIUM, S(11), MenuTheme.dim());

        int bs = S(26);
        int bx = x + w - S(14) - bs;
        int by = headY - S(5);
        boolean hv = inside(mx, my, bx, by, bs, bs);
        Px.round(g, bx, by, bs, bs, S(8), Px.a(MenuTheme.text(), hv ? 40 : 14));
        Px.icon(g, "broom", bx + S(5), by + S(5), S(16), hv ? MenuTheme.text() : MenuTheme.muted());
        hit(bx, by, bs, bs, A_CLEAR_LOG, 0);

        // вкладки
        BotLog.Channel[] tabs = {BotLog.Channel.CHAT, BotLog.Channel.COMMAND, BotLog.Channel.OUTPUT};
        int tabY = y + S(38);
        int tabH = S(32);
        int tabX = x + S(12);
        for (int i = 0; i < tabs.length; i++) {
            BotLog.Channel tab = tabs[i];
            String label = tab.displayName();
            int fs = S(12);
            int tw = Px.textWidth(label, Weight.SEMIBOLD, fs) + S(24);
            boolean activeTab = this.logTab == tab;
            boolean hovered = inside(mx, my, tabX, tabY, tw, tabH);
            Px.round(g, tabX, tabY, tw, tabH, S(9),
                    Px.a(activeTab ? MenuTheme.accent() : MenuTheme.text(),
                            activeTab ? 60 : hovered ? 26 : 10));
            if (activeTab) {
                Px.outline(g, tabX, tabY, tw, tabH, S(9), Px.a(MenuTheme.accent(), 170));
            }
            Px.text(g, label, tabX + S(12), tabY + (tabH - fs) / 2,
                    activeTab ? Weight.SEMIBOLD : Weight.MEDIUM, fs,
                    activeTab ? MenuTheme.text() : MenuTheme.muted());
            hit(tabX, tabY, tw, tabH, A_LOG_TAB, i);
            tabX += tw + S(6);
        }

        ScissorUtils.push();
        ScissorUtils.set(x, Px.height() - (this.logListY + this.logListH), w, this.logListH);

        int rowH = S(28);
        this.logContentH = entries.size() * rowH;
        float scroll = switch (this.logTab) {
            case CHAT -> this.scrollLogChat;
            case COMMAND -> this.scrollLogCommand;
            default -> this.scrollLogOutput;
        };
        int baseY = this.logListY - Math.round(clampScroll(scroll, this.logContentH, this.logListH));
        int fs = S(12);

        for (int i = 0, size = entries.size(); i < size; i++) {
            BotLog.Entry entry = entries.get(i);
            int ry = baseY + i * rowH;
            if (ry + rowH < this.logListY || ry > this.logListY + this.logListH) {
                continue;
            }
            int color = switch (entry.level()) {
                case OK -> C_OK;
                case WARN -> C_WARN;
                case ERROR -> C_ERROR;
                default -> this.logTab == BotLog.Channel.CHAT ? MenuTheme.text()
                        : this.logTab == BotLog.Channel.COMMAND ? C_LINK : MenuTheme.muted();
            };
            Px.circle(g, x + S(20), ry + rowH / 2, S(3), color);
            Px.text(g, time(entry.time()), x + S(30), ry + (rowH - S(11)) / 2,
                    Weight.MEDIUM, S(11), MenuTheme.dim());
            Px.text(g, Px.ellipsize(entry.text(), Weight.MEDIUM, fs, w - S(112)),
                    x + S(94), ry + (rowH - fs) / 2, Weight.MEDIUM, fs, color);
        }
        ScissorUtils.pop();

        if (entries.isEmpty()) {
            Px.textCenter(g, emptyLogHint(), x + w / 2, this.logListY + S(24),
                    Weight.MEDIUM, S(13), MenuTheme.dim());
        }
    }

    private String emptyLogHint() {
        if (this.selected == null) {
            return "Профиль не выбран";
        }
        return switch (this.logTab) {
            case CHAT -> "Сообщения сервера появятся здесь";
            case COMMAND -> "Что бот отправил: команды и сообщения";
            default -> "Соединение, шаги и ошибки";
        };
    }

    private List<BotLog.Entry> entriesFor(BotLog log, BotLog.Channel channel) {
        if (this.selected == null) {
            return Collections.emptyList();
        }
        return switch (channel) {
            case CHAT -> log.snapshot(BotLog.Channel.CHAT);
            case COMMAND -> log.snapshot(BotLog.Channel.COMMAND);
            default -> log.snapshotOutputOnly();
        };
    }

    private static String time(long millis) {
        LocalTime local = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalTime();
        return String.format("%02d:%02d:%02d", local.getHour(), local.getMinute(), local.getSecond());
    }

    // ---- выдвижная панель профилей

    private void profilesDrawer(GuiGraphics g, double mx, double my) {
        int offset = Math.round((1f - this.drawerPos) * (this.drawerW + S(40)));
        int x = this.px + S(10) - offset;
        this.drawerDrawX = x;
        int y = this.drawerY;
        int w = this.drawerW;
        int h = this.drawerH;

        if (this.drawerPos < 0.02f) {
            // «ручка» у левого края: подсказка, что панель существует
            int hx = this.px - S(2);
            int hy = y + h / 2 - S(60);
            int hw = S(14);
            int hh = S(120);
            boolean hv = inside(mx, my, this.px - S(30), hy, S(46), hh);
            Px.round(g, hx, hy, hw, hh, S(7), Px.a(MenuTheme.accent(), hv ? 180 : 90));
            Px.icon(g, "users", hx - S(20), hy + hh / 2 - S(9), S(18), MenuTheme.accent());
            if (hv) {
                Px.textCenter(g, "Профили", hx - S(11), hy + hh / 2 + S(14),
                        Weight.SEMIBOLD, S(10), MenuTheme.accent());
            }
            hit(this.px - S(30), hy, S(46), hh, A_PIN, 0);
            return;
        }

        Px.shadow(g, x, y, w, h, S(24), Px.a(0xFF000000, 120));
        Px.round(g, x, y, w, h, S(18), Px.a(MenuTheme.bg(), 248));
        Px.outline(g, x, y, w, h, S(18), Px.a(MenuTheme.accent(), 90));

        int headY = y + S(20);
        Px.icon(g, "users", x + S(18), headY + S(1), S(18), MenuTheme.accent());
        Px.text(g, "Профили", x + S(44), headY, Weight.SEMIBOLD, S(16), MenuTheme.text());
        Px.textRight(g, BotStore.bots().size() + " / 64", x + w - S(56), headY + S(3),
                Weight.MEDIUM, S(12), MenuTheme.dim());

        // закрепить/отпустить панель: без закрепа она уезжает, когда курсор уходит вправо
        int pinSize = S(28);
        int pinX = x + w - S(14) - pinSize;
        int pinY = headY - S(6);
        boolean hvPin = inside(mx, my, pinX, pinY, pinSize, pinSize);
        Px.round(g, pinX, pinY, pinSize, pinSize, S(9),
                Px.a(this.drawerPinned ? MenuTheme.accent() : MenuTheme.text(), hvPin ? 46 : 14));
        Px.icon(g, this.drawerPinned ? "link" : "unlink", pinX + S(6), pinY + S(6), S(16),
                this.drawerPinned ? MenuTheme.accent() : MenuTheme.muted());
        hit(pinX, pinY, pinSize, pinSize, A_PIN, 0);

        // запустить всех / остановить всех
        int bs = S(30);
        int bx = x + w - S(18) - bs;
        int by = headY + S(28);
        boolean hvStop = inside(mx, my, bx, by, bs, bs);
        Px.round(g, bx, by, bs, bs, S(9), Px.a(C_ERROR, hvStop ? 70 : 28));
        Px.icon(g, "stop", bx + S(7), by + S(7), S(16), C_ERROR);
        hit(bx, by, bs, bs, A_STOP_ALL, 0);

        bx -= bs + S(6);
        boolean hvRun = inside(mx, my, bx, by, bs, bs);
        Px.round(g, bx, by, bs, bs, S(9), Px.a(C_OK, hvRun ? 70 : 28));
        Px.icon(g, "play", bx + S(7), by + S(7), S(16), C_OK);
        hit(bx, by, bs, bs, A_RUN_ALL, 0);

        Px.text(g, "запустить всех / остановить", bx + bs + S(8) + Px.textWidth("", Weight.MEDIUM, S(11)),
                by + S(9), Weight.MEDIUM, S(11), MenuTheme.dim());

        ScissorUtils.push();
        ScissorUtils.set(x, Px.height() - (this.profilesListY + this.profilesListH),
                w, this.profilesListH);

        List<BotProfile> bots = BotStore.bots();
        int rowH = S(76);
        this.profilesContentH = bots.size() * rowH;
        int baseY = this.profilesListY - Math.round(clampScroll(this.scrollProfiles,
                this.profilesContentH, this.profilesListH));

        for (int i = 0, size = bots.size(); i < size; i++) {
            BotProfile profile = bots.get(i);
            int ry = baseY + i * rowH;
            if (ry + rowH < this.profilesListY || ry > this.profilesListY + this.profilesListH) {
                continue;
            }
            profileRow(g, profile, i, ry, rowH, mx, my);
        }
        ScissorUtils.pop();

        if (bots.isEmpty()) {
            Px.textCenter(g, "Профилей пока нет", x + w / 2, this.profilesListY + S(24),
                    Weight.MEDIUM, S(13), MenuTheme.muted());
            Px.textCenter(g, "Нажмите «Создать» ниже", x + w / 2, this.profilesListY + S(46),
                    Weight.MEDIUM, S(11), MenuTheme.dim());
        }

        // кнопки внизу панели
        int pad = S(14);
        int gap = S(8);
        int bh = S(38);
        int byy = y + h - bh - S(16);
        int wNew = Math.round((w - pad * 2 - gap * 2) * 0.5f);
        int wSmall = (w - pad * 2 - gap * 2 - wNew) / 2;
        int bxx = x + pad;

        boolean hvNew = inside(mx, my, bxx, byy, wNew, bh);
        Px.round(g, bxx, byy, wNew, bh, S(11), Px.a(MenuTheme.accent(), hvNew ? 78 : 50));
        Px.outline(g, bxx, byy, wNew, bh, S(11), Px.a(MenuTheme.accent(), hvNew ? 200 : 130));
        Px.icon(g, "plus", bxx + S(12), byy + S(11), S(16), MenuTheme.accent());
        Px.text(g, "Создать", bxx + S(34), byy + S(12), Weight.SEMIBOLD, S(13), MenuTheme.text());
        hit(bxx, byy, wNew, bh, A_NEW, 0);

        int b2 = bxx + wNew + gap;
        boolean hvDup = inside(mx, my, b2, byy, wSmall, bh) && this.selected != null;
        Px.round(g, b2, byy, wSmall, bh, S(11), Px.a(MenuTheme.text(), hvDup ? 34 : 16));
        Px.icon(g, "copy", b2 + (wSmall - S(16)) / 2, byy + S(11), S(16),
                this.selected == null ? MenuTheme.dim() : (hvDup ? MenuTheme.text() : MenuTheme.muted()));
        if (this.selected != null) {
            hit(b2, byy, wSmall, bh, A_DUP, 0);
        }

        int b3 = b2 + wSmall + gap;
        boolean hvDel = inside(mx, my, b3, byy, wSmall, bh) && this.selected != null;
        Px.round(g, b3, byy, wSmall, bh, S(11), Px.a(C_ERROR, hvDel ? 60 : 18));
        Px.icon(g, "trash", b3 + (wSmall - S(16)) / 2, byy + S(11), S(16),
                this.selected == null ? MenuTheme.dim() : (hvDel ? C_ERROR : MenuTheme.muted()));
        if (this.selected != null) {
            hit(b3, byy, wSmall, bh, A_DEL, 0);
        }
    }

    private void profileRow(GuiGraphics g, BotProfile profile, int index, int y, int rowH,
                            double mx, double my) {
        boolean active = profile == this.selected;
        int x = this.drawerDrawX + S(12);
        int w = this.drawerW - S(24);
        int h = rowH - S(8);
        boolean hovered = inside(mx, my, x, y + S(4), w, h);

        Px.round(g, x, y + S(4), w, h, S(12),
                Px.a(active ? MenuTheme.accent() : MenuTheme.text(), active ? 54 : hovered ? 26 : 12));
        if (active) {
            Px.outline(g, x, y + S(4), w, h, S(12), Px.a(MenuTheme.accent(), 160));
        }

        boolean running = BotManager.isRunning(profile);
        int dotColor = running ? C_OK : (BotManager.isOnline(profile) ? C_LINK : MenuTheme.dim());
        if (running) {
            Px.circle(g, x + S(22), y + S(28), S(8), Px.a(C_OK, 60));
        }
        Px.circle(g, x + S(22), y + S(28), S(4), dotColor);

        int nameW = w - S(96);
        Px.text(g, Px.ellipsize(profile.getName(), Weight.SEMIBOLD, S(15), nameW),
                x + S(38), y + S(18), Weight.SEMIBOLD, S(15), MenuTheme.text());
        Px.text(g, Px.ellipsize(profile.getNickname() + " · " + profile.address(),
                        Weight.MEDIUM, S(11), nameW),
                x + S(38), y + S(38), Weight.MEDIUM, S(11), MenuTheme.muted());
        Px.text(g, profile.getSteps().size() + " шагов", x + S(38), y + S(54),
                Weight.MEDIUM, S(10), MenuTheme.dim());

        int bs = S(34);
        int bx = x + w - bs - S(10);
        int by = y + S(4) + (h - bs) / 2;
        boolean hvRun = inside(mx, my, bx, by, bs, bs);
        Px.round(g, bx, by, bs, bs, S(10), Px.a(running ? C_ERROR : C_OK, hvRun ? 90 : 46));
        Px.icon(g, running ? "stop" : "play", bx + S(9), by + S(9), S(16),
                running ? C_ERROR : C_OK);
        hit(bx, by, bs, bs, A_RUN, index);

        hit(x, y + S(4), w, h, A_PROFILE, index);
    }

    // ---- вспомогательное

    private void panel(GuiGraphics g, int x, int y, int w, int h) {
        Px.round(g, x, y, w, h, S(16), Px.a(MenuTheme.bg(), 220));
        Px.outline(g, x, y, w, h, S(16), Px.a(MenuTheme.border(), 170));
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

    private int hitIndexAt(double x, double y) {
        for (int i = this.hitCount - 1; i >= 0; i--) {
            if (inside(x, y, this.hitX[i], this.hitY[i], this.hitW[i], this.hitH[i])) {
                return i;
            }
        }
        return -1;
    }

    private static boolean inside(double x, double y, int rx, int ry, int rw, int rh) {
        return x >= rx && x < rx + rw && y >= ry && y < ry + rh;
    }

    private static float clampScroll(float scroll, int content, int view) {
        float max = Math.max(0, content - view);
        return Math.max(0, Math.min(max, scroll));
    }

    private static String trim(float value) {
        return String.valueOf(Math.round(value * 10f) / 10f);
    }

    // ---------------------------------------------------------------- ввод

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        double mx = Px.px(mouseX);
        double my = Px.px(mouseY);

        int index = hitIndexAt(mx, my);
        if (index < 0) {
            commitEdit();
            this.searchFocused = false;
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
                    this.scrollBlocks = 0;
                    MenuSound.play(MenuSound.SELECT);
                }
            }
            case A_RUN -> {
                commitEdit();
                List<BotProfile> bots = BotStore.bots();
                if (arg >= 0 && arg < bots.size()) {
                    this.selected = bots.get(arg);
                    toggleRun(this.selected);
                }
            }
            case A_RUN_SELECTED -> {
                commitEdit();
                if (this.selected != null) {
                    toggleRun(this.selected);
                }
            }
            case A_RUN_ALL -> {
                commitEdit();
                for (BotProfile profile : BotStore.bots()) {
                    BotManager.start(profile);
                }
                MenuSound.play(MenuSound.TOGGLE_ON, 1f, 0.5f);
            }
            case A_STOP_ALL -> {
                commitEdit();
                for (BotProfile profile : BotStore.bots()) {
                    BotManager.stop(profile);
                }
                MenuSound.play(MenuSound.TOGGLE_OFF, 1f, 0.5f);
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
            case A_PRESET -> {
                if (this.selected != null) {
                    BotPreset[] presets = BotPreset.values();
                    if (arg >= 0 && arg < presets.length) {
                        int added = presets[arg].apply(this.selected);
                        this.scrollBlocks = Math.max(0,
                                this.blocksContentH - this.blocksH);
                        MenuSound.play(MenuSound.DROP, 1.05f, 0.45f);
                        NotificationManager.info("Добавлено " + added + " шагов",
                                presets[arg].title());
                    }
                }
            }
            case A_CAT -> {
                BotStepType.Category[] categories = BotStepType.Category.values();
                if (arg >= 0 && arg < categories.length) {
                    this.category = categories[arg];
                    this.search.setLength(0);
                    this.searchFocused = false;
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
                        this.scrollBlocks = Math.max(0, this.blocksContentH - this.blocksH + S(60));
                        MenuSound.play(MenuSound.DROP, 1.05f, 0.45f);
                    }
                }
            }
            case A_SEARCH -> {
                commitEdit();
                this.searchFocused = true;
                MenuSound.play(MenuSound.CLICK, 1.1f, 0.35f);
            }
            case A_SEARCH_CLEAR -> {
                this.search.setLength(0);
                rebuildCatalog();
                MenuSound.play(MenuSound.POP, 0.95f, 0.4f);
            }
            case A_PLATE -> {
                commitEdit();
                List<BotStep> steps = stepList();
                if (arg >= 0 && arg < steps.size()) {
                    this.pressedStep = steps.get(arg);
                    this.pressedIndex = arg;
                    this.pressX = mx;
                    this.pressY = my;
                }
            }
            case A_STEP_ON -> {
                commitEdit();
                List<BotStep> steps = stepList();
                if (arg >= 0 && arg < steps.size()) {
                    steps.get(arg).toggle();
                    BotStore.markDirty();
                    MenuSound.play(MenuSound.TOGGLE_OFF, 1.05f, 0.4f);
                }
            }
            case A_STEP_DEL -> {
                commitEdit();
                List<BotStep> steps = stepList();
                if (arg >= 0 && arg < steps.size()) {
                    this.selected.removeStep(steps.get(arg));
                    BotStore.markDirty();
                    MenuSound.play(MenuSound.DROP, 0.85f, 0.5f);
                }
            }
            case A_STEP_UP, A_STEP_DOWN -> {
                commitEdit();
                List<BotStep> steps = stepList();
                if (arg >= 0 && arg < steps.size()) {
                    this.selected.move(steps.get(arg), action == A_STEP_UP ? -1 : 1);
                    BotStore.markDirty();
                    MenuSound.play(MenuSound.POP, 1.05f, 0.4f);
                }
            }
            case A_ENABLE_ALL -> {
                commitEdit();
                List<BotStep> steps = stepList();
                boolean allOn = true;
                for (int i = 0, size = steps.size(); i < size; i++) {
                    if (!steps.get(i).isEnabled()) {
                        allOn = false;
                        break;
                    }
                }
                for (int i = 0, size = steps.size(); i < size; i++) {
                    steps.get(i).setEnabled(!allOn);
                }
                BotStore.markDirty();
                MenuSound.play(MenuSound.TOGGLE_ON, 1.05f, 0.45f);
            }
            case A_VALUE -> {
                int stepIndex = arg / FIELD_STRIDE;
                int slot = arg % FIELD_STRIDE;
                startValueEdit(stepIndex, slot);
            }
            case A_FIELD -> {
                // В hit передаётся ordinal из Edit, поэтому обратное преобразование точное.
                Edit[] kinds = Edit.values();
                if (arg >= 0 && arg < kinds.length && kinds[arg] != Edit.NONE) {
                    startFieldEdit(kinds[arg]);
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
            case A_LOG_TAB -> {
                commitEdit();
                BotLog.Channel[] tabs = {BotLog.Channel.CHAT, BotLog.Channel.COMMAND,
                        BotLog.Channel.OUTPUT};
                if (arg >= 0 && arg < tabs.length) {
                    this.logTab = tabs[arg];
                    MenuSound.play(MenuSound.TAB, 1.1f, 0.4f);
                }
            }
            case A_PIN -> {
                commitEdit();
                this.drawerPinned = !this.drawerPinned;
                if (this.drawerPinned) {
                    this.drawerCloseDelay = 0f;
                }
                MenuSound.play(MenuSound.CLICK, 1.05f, 0.4f);
            }
            case A_CLEAR_LOG -> {
                BotLog log = BotManager.logFor(this.selected);
                log.clear();
                MenuSound.play(MenuSound.POP, 0.95f, 0.4f);
            }
            default -> {
                // A_NONE
            }
        }
        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.pressedStep == null) {
            return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        }
        double mx = Px.px(mouseX);
        double my = Px.px(mouseY);

        if (!this.dragging && Math.hypot(mx - this.pressX, my - this.pressY) > S(10)) {
            this.dragging = true;
            this.dragStep = this.pressedStep;
            this.dragOrigin = this.pressedIndex;
            List<BotStep> steps = stepList();
            if (this.dragOrigin >= 0 && this.dragOrigin < steps.size()) {
                steps.remove(this.dragOrigin);
            }
            BotStore.markDirty();
            MenuSound.play(MenuSound.GRAB, 1f, 0.45f);
        }

        if (this.dragging) {
            updateDropIndex(mx, my);
        }
        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (this.dragging && this.dragStep != null) {
            commitDrop();
            return true;
        }
        if (this.pressedStep != null) {
            // клик по плашке без движения — это выбор строки, не перетаскивание
            this.pressedStep = null;
            this.pressedIndex = -1;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private void updateDropIndex(double mx, double my) {
        this.dropIndex = -1;
        List<BotStep> steps = stepList();
        if (steps.isEmpty()) {
            this.dropIndex = 0;
            return;
        }
        if (!inside(mx, my, this.blocksX, this.blocksY - S(20), this.blocksW, this.blocksH + S(40))) {
            return;
        }
        for (int i = 0; i < this.plateCount; i++) {
            int center = this.plateY[i] + this.plateH[i] / 2;
            if (my < center) {
                this.dropIndex = this.plateIndex[i];
                return;
            }
        }
        this.dropIndex = steps.size();
    }

    private void commitDrop() {
        BotStep step = this.dragStep;
        int target = this.dropIndex;
        int origin = this.dragOrigin;

        this.dragging = false;
        this.dragStep = null;
        this.dropIndex = -1;
        this.dragOrigin = -1;
        this.pressedStep = null;
        this.pressedIndex = -1;

        List<BotStep> steps = stepList();
        if (step == null) {
            return;
        }
        if (target < 0) {
            // бросили мимо рабочей области — возвращаем на место
            steps.add(Math.max(0, Math.min(steps.size(), origin)), step);
            MenuSound.play(MenuSound.POP, 0.9f, 0.5f);
            return;
        }
        steps.add(Math.max(0, Math.min(steps.size(), target)), step);
        BotStore.markDirty();
        MenuSound.play(MenuSound.DROP, 1f, 0.5f);
    }

    private List<BotStep> stepList() {
        return this.selected == null ? Collections.emptyList() : this.selected.getSteps();
    }

    private void toggleRun(BotProfile profile) {
        if (BotManager.isRunning(profile)) {
            BotManager.stop(profile);
            MenuSound.play(MenuSound.TOGGLE_OFF, 1f, 0.5f);
            return;
        }

        // Один и тот же ник на сервере дважды невозможен: сервер выкинет одного
        // из двоих, и правки уходили бы «не в того» бота. Предупреждаем заранее.
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.player.getGameProfile().getName()
                .equalsIgnoreCase(profile.getNickname())) {
            NotificationManager.warning("Ник бота совпадает с вашим",
                    "Смените ник в профиле: " + profile.getName());
        }

        BotManager.start(profile);
        MenuSound.play(MenuSound.TOGGLE_ON, 1.05f, 0.5f);
    }

    private void rebuildCatalog() {
        this.catalog.clear();
        String query = this.search.toString().trim().toLowerCase();
        for (BotStepType type : BotStepType.values()) {
            if (!query.isEmpty()) {
                if (matches(type, query)) {
                    this.catalog.add(type);
                }
                continue;
            }
            if (type.category() == this.category) {
                this.catalog.add(type);
            }
        }
    }

    private static boolean matches(BotStepType type, String query) {
        if (type.name().toLowerCase().contains(query)
                || type.category().displayName().toLowerCase().contains(query)) {
            return true;
        }
        for (int i = 0; i < type.paramCount() + 1; i++) {
            if (type.word(i).toLowerCase().contains(query)) {
                return true;
            }
        }
        for (int i = 0; i < type.paramCount(); i++) {
            BotStepType.Param param = type.param(i);
            if (param != null && (param.defaultValue().toLowerCase().contains(query)
                    || param.title().toLowerCase().contains(query))) {
                return true;
            }
        }
        return false;
    }

    // ---- правка значений

    private void startValueEdit(int stepIndex, int slot) {
        commitEdit();
        List<BotStep> steps = stepList();
        if (stepIndex < 0 || stepIndex >= steps.size()) {
            return;
        }
        BotStep step = steps.get(stepIndex);
        if (slot < 0 || slot >= step.valueCount()) {
            return;
        }
        this.editing = Edit.VALUE;
        this.editStep = stepIndex;
        this.editSlot = slot;
        this.edit.setLength(0);
        this.edit.append(step.get(slot));
        this.editFresh = true;
        MenuSound.play(MenuSound.CLICK, 1.1f, 0.35f);
    }

    private void startFieldEdit(Edit kind) {
        commitEdit();
        if (this.selected == null) {
            return;
        }
        this.editing = kind;
        this.editStep = -1;
        this.editSlot = -1;
        this.edit.setLength(0);
        this.edit.append(currentFieldValue(kind));
        this.editFresh = true;
        MenuSound.play(MenuSound.CLICK, 1.1f, 0.35f);
    }

    private String currentFieldValue(Edit kind) {
        if (this.selected == null) {
            return "";
        }
        return switch (kind) {
            case NAME -> this.selected.getName();
            case HOST -> this.selected.getHost();
            case PORT -> String.valueOf(this.selected.getPort());
            case NICK -> this.selected.getNickname();
            case PASSWORD -> this.selected.getPassword();
            case STEP_DELAY -> trim(this.selected.getStepDelay());
            case RECONNECT_DELAY -> trim(this.selected.getReconnectDelay());
            default -> "";
        };
    }

    private void commitEdit() {
        if (this.editing == Edit.NONE) {
            return;
        }
        String value = this.edit.toString().trim();
        Edit kind = this.editing;
        int stepIndex = this.editStep;
        int slot = this.editSlot;
        this.editing = Edit.NONE;
        this.editStep = -1;
        this.editSlot = -1;

        if (kind == Edit.VALUE) {
            List<BotStep> steps = stepList();
            if (stepIndex >= 0 && stepIndex < steps.size()) {
                BotStep target = steps.get(stepIndex);
                BotStepType.Param param = target.getType() == null ? null
                        : target.getType().param(slot);
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
                    BotProfile other = BotStore.find(value);
                    this.selected.setName(other == null || other == this.selected
                            ? value : BotStore.uniqueName(value));
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
            case PASSWORD -> {
                this.selected.setPassword(value);
                BotStore.markDirty();
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
                // SEARCH и NONE сюда не попадают
            }
        }
    }

    // ---- клавиши и прокрутка

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.searchFocused) {
            switch (keyCode) {
                case GLFW.GLFW_KEY_ESCAPE -> {
                    if (this.search.length() > 0) {
                        this.search.setLength(0);
                        rebuildCatalog();
                    } else {
                        this.searchFocused = false;
                    }
                    return true;
                }
                case GLFW.GLFW_KEY_ENTER, GLFW.GLFW_KEY_KP_ENTER -> {
                    this.searchFocused = false;
                    return true;
                }
                case GLFW.GLFW_KEY_BACKSPACE -> {
                    if (this.search.length() > 0) {
                        this.search.setLength(this.search.length() - 1);
                        rebuildCatalog();
                    }
                    return true;
                }
                case GLFW.GLFW_KEY_DELETE -> {
                    this.search.setLength(0);
                    rebuildCatalog();
                    return true;
                }
                default -> {
                    return true;
                }
            }
        }

        if (this.editing != Edit.NONE) {
            switch (keyCode) {
                case GLFW.GLFW_KEY_ENTER, GLFW.GLFW_KEY_KP_ENTER -> {
                    commitEdit();
                    return true;
                }
                case GLFW.GLFW_KEY_ESCAPE -> {
                    this.editing = Edit.NONE;
                    this.edit.setLength(0);
                    this.editFresh = false;
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
                default -> {
                    return true;
                }
            }
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
        if (this.searchFocused) {
            if (codePoint >= 32 && codePoint != 127 && this.search.length() < 48) {
                this.search.append(codePoint);
                rebuildCatalog();
                this.searchFocused = true;
                MenuSound.play(MenuSound.TYPE, 1.05f, 0.25f);
            }
            return true;
        }
        if (this.editing == Edit.NONE) {
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
        float step = (float) (scrollY * S(34));

        if (this.drawerPos > 0.5f && inside(mx, my, this.drawerDrawX, this.profilesListY,
                this.drawerW, this.profilesListH)) {
            this.scrollProfiles = clampScroll(this.scrollProfiles - step,
                    this.profilesContentH, this.profilesListH);
            return true;
        }
        if (inside(mx, my, this.blocksX, this.blocksY, this.blocksW, this.blocksH)) {
            this.scrollBlocks = clampScroll(this.scrollBlocks - step,
                    this.blocksContentH, this.blocksH);
            return true;
        }
        if (inside(mx, my, this.rightX, this.catalogListY, this.rightW, this.catalogListH)) {
            this.scrollCatalog = clampScroll(this.scrollCatalog - step,
                    this.catalogContentH, this.catalogListH);
            return true;
        }
        if (inside(mx, my, this.rightX, this.logListY, this.rightW, this.logListH)) {
            switch (this.logTab) {
                case CHAT -> this.scrollLogChat = clampScroll(this.scrollLogChat - step,
                        this.logContentH, this.logListH);
                case COMMAND -> this.scrollLogCommand = clampScroll(this.scrollLogCommand - step,
                        this.logContentH, this.logListH);
                default -> this.scrollLogOutput = clampScroll(this.scrollLogOutput - step,
                        this.logContentH, this.logListH);
            }
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
        if (this.parent == null) {
            super.onClose();
            return;
        }
        Minecraft.getInstance().setScreen(this.parent);
    }
}
