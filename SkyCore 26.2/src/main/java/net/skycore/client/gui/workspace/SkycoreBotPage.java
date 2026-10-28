package net.skycore.client.gui.workspace;

import net.skycore.client.bot.BedWarsBot;
import net.skycore.client.bot.BotAppearance;
import net.skycore.client.bot.BotConfig;
import net.skycore.client.bot.BotScript;
import net.skycore.client.gui.menu.MenuAnim;
import net.skycore.client.gui.menu.MenuDraw;
import net.skycore.client.gui.menu.MenuSound;
import net.skycore.client.util.RenderGuiHelper;

/** Embedded Skycore UI adapter. Reuses the existing bot renderer and offline model;
 * no second screen, window, network client or configuration is created.
 * Geometry and color tokens come from the receiving Skycore UI, not Workspace.
 */
public final class SkycoreBotPage {
    private static final int MAX_HITS = 128;
    private final WorkspaceLayout layout = new WorkspaceLayout();
    private final WorkspaceTheme theme = new WorkspaceTheme();
    private final WorkspaceBotPanel panel = new WorkspaceBotPanel();
    private final MenuAnim anim = new MenuAnim();
    private final float[] hx = new float[MAX_HITS], hy = new float[MAX_HITS],
            hw = new float[MAX_HITS], hh = new float[MAX_HITS];
    private final int[] types = new int[MAX_HITS], args = new int[MAX_HITS], options = new int[MAX_HITS];
    private int hitCount;
    private int editing = -1;
    private String buffer = "";
    private float leftScroll, rightScroll, leftTarget, rightTarget;
    private boolean dragging, dirty;
    private float logX, logY, logW, logH;
    public void logButton(float x, float y, float w, float h) { logX=x; logY=y; logW=w; logH=h; }
    public boolean clickLog(double x, double y, int button) {
        if (button != 0 || x < logX || x >= logX + logW || y < logY || y >= logY + logH) return false;
        toggleLog(); return true;
    }
    private final WorkspaceSettings.Sink sink = this::hit;

    public void render(RenderGuiHelper host, float x, float y, float unit, float opacity,
                       float mouseX, float mouseY, boolean live) {
        // Fill the existing 976 x 642 content region, with no Workspace shell/sidebar.
        layout.u = unit;
        layout.contentX = x;
        layout.contentW = 976 * unit;
        layout.listX = x;
        layout.listY = layout.paneY = y;
        layout.listH = layout.paneH = 642 * unit;
        layout.paneW = 350 * unit;
        layout.listW = layout.contentW - layout.paneW - 12 * unit;
        layout.paneX = x + layout.contentW - layout.paneW;
        layout.py = y;
        layout.ph = layout.listH;
        theme.window = host.elevatedColor();
        theme.card = host.surfaceColor();
        theme.inset = host.fieldColor();
        theme.insetHover = host.fieldActiveColor();
        theme.text = host.primaryColor();
        theme.muted = host.secondaryColor();
        theme.dim = host.tertiaryColor();
        theme.accent = theme.accent2 = host.accentColor() | 0xFF000000;
        theme.onAccent = MenuDraw.onAccent(theme.accent);
        theme.track = host.trackColor();
        theme.border = MenuDraw.a(theme.text, 24);
        theme.borderStrong = MenuDraw.a(theme.accent, 140);
        theme.knob = theme.text;
        theme.danger = 0xFFFF5C72;
        // Infer contrast from the actual receiving surface, independent of MenuPalette.
        int base = theme.window;
        theme.light = (((base >> 16) & 255) * 299 + ((base >> 8) & 255) * 587
                + (base & 255) * 114) > 150000;
        anim.frame();
        leftTarget = clamp(leftTarget, panel.leftTotal(), layout.listH - layout.s(44));
        rightTarget = clamp(rightTarget, panel.rightTotal(), layout.paneH - layout.s(46));
        leftScroll += (leftTarget - leftScroll) * 0.35F;
        rightScroll += (rightTarget - rightScroll) * 0.35F;
        hitCount = 0;
        float keep = MenuDraw.fade;
        MenuDraw.fade = keep * opacity;
        try {
            panel.render(layout, theme, anim, mouseX, mouseY, live, editing, buffer,
                    leftScroll, rightScroll, sink);
            panel.notification(layout, theme);
        } finally {
            MenuDraw.fade = keep;
        }
    }

    private void hit(int type, int arg, int option, float x, float y, float w, float h) {
        float top = Math.max(y, layout.listY + (type == WorkspaceBotPanel.HIT_PARTY || type == WorkspaceBotPanel.HIT_HOME ? 0 : layout.s(38)));
        float bottom = Math.min(y + h, layout.listY + layout.listH - layout.s(8));
        if (hitCount >= MAX_HITS || bottom <= top) return;
        hx[hitCount] = x; hy[hitCount] = top; hw[hitCount] = w; hh[hitCount] = bottom - top;
        types[hitCount] = type; args[hitCount] = arg; options[hitCount++] = option;
    }

    public void toggleLog() { commit(); panel.toggleLog(); leftTarget = leftScroll = 0; }

    public boolean contains(double x, double y) {
        return x >= layout.listX && x < layout.contentX + layout.contentW
                && y >= layout.listY && y < layout.listY + layout.listH;
    }

    public boolean click(double x, double y, int button) {
        if (!contains(x, y)) { commit(); return false; }
        if (button != 0) return true;
        int index = -1;
        for (int i = hitCount - 1; i >= 0; i--) {
            if (x >= hx[i] && x < hx[i] + hw[i] && y >= hy[i] && y < hy[i] + hh[i]) {
                index = i; break;
            }
        }
        if (index < 0) { commit(); return true; }
        int type = types[index], arg = args[index], option = options[index];
        if (type != WorkspaceBotPanel.HIT_STRING || editing != arg) commit();
        BedWarsBot bot = BedWarsBot.INSTANCE;
        switch (type) {
            case WorkspaceBotPanel.HIT_HOME -> { panel.home(); leftTarget = leftScroll = 0; }
            case WorkspaceBotPanel.HIT_PARTY -> { panel.toggleParty(); leftTarget = leftScroll = 0; }
            case WorkspaceBotPanel.HIT_LOG -> panel.toggleLog();
            case WorkspaceBotPanel.HIT_STOP_ONE -> bot.stopOne(arg);
            case WorkspaceBotPanel.HIT_START -> bot.start();
            case WorkspaceBotPanel.HIT_STOP -> bot.stop();
            case WorkspaceBotPanel.HIT_STEP -> bot.previewStep();
            case WorkspaceBotPanel.HIT_CLEAR_LOG -> bot.clearLog();
            case WorkspaceBotPanel.HIT_STRING -> {
                if (!bot.isRunning() && editing != arg) {
                    editing = arg;
                    buffer = switch (arg) {
                        case WorkspaceBotPanel.FIELD_PREFIX -> BotConfig.nickPrefix;
                        case WorkspaceBotPanel.FIELD_PASSWORD -> BotConfig.password;
                        default -> BotConfig.ownerNick;
                    };
                }
            }
            case WorkspaceBotPanel.HIT_SLIDER -> {
                if (!bot.isRunning()) { dragging = true; countAt(x); }
            }
            case WorkspaceBotPanel.HIT_VOTE -> {
                BotConfig.setVoteCell(arg, BotScript.voteCellAt(arg, option));
                dirty = true;
            }
            case WorkspaceBotPanel.HIT_APPEARANCE -> {
                if (option == 0) BotAppearance.cycleColor(arg);
                if (option == 1) BotAppearance.toggleCompact(arg);
                if (option == 2) BotAppearance.toggleNotifications(arg);
                dirty = true;
            }
            case WorkspaceBotPanel.HIT_TEST_ERROR -> bot.previewError(arg);
            default -> { }
        }
        MenuSound.play(MenuSound.CLICK);
        return true;
    }

    private void countAt(double x) {
        float start = layout.paneX + layout.s(14);
        float width = Math.max(1, layout.paneW - layout.s(28));
        float ratio = Math.max(0, Math.min(1, (float) (x - start) / width));
        BotConfig.setBotCount(BotScript.MIN_BOTS + Math.round(ratio * (BotScript.MAX_BOTS - BotScript.MIN_BOTS)));
        dirty = true;
    }

    public boolean drag(double x) {
        if (!dragging) return false;
        countAt(x);
        return true;
    }

    public boolean release() {
        boolean wasDragging = dragging;
        dragging = false;
        return wasDragging;
    }

    public boolean scroll(double x, double y, double amount) {
        if (!contains(x, y)) return false;
        commit();
        if (x >= layout.paneX) rightTarget = clamp(rightTarget - (float) amount * layout.s(38), panel.rightTotal(), layout.paneH - layout.s(46));
        else leftTarget = clamp(leftTarget - (float) amount * layout.s(38), panel.leftTotal(), layout.listH - layout.s(44));
        return true;
    }

    public boolean key(int key) {
        if (editing < 0) return false;
        if (key == 256) { editing = -1; buffer = ""; return true; }
        if (key == 257 || key == 335) { commit(); return true; }
        if (key == 259 && !buffer.isEmpty()) {
            int end = buffer.offsetByCodePoints(buffer.length(), -1);
            buffer = buffer.substring(0, end);
        }
        return true;
    }

    public boolean type(String text) {
        if (editing < 0) return false;
        int max = editing == WorkspaceBotPanel.FIELD_PASSWORD ? 32 : 15;
        if (editing == WorkspaceBotPanel.FIELD_OWNER) max = 16;
        if (buffer.length() + text.length() <= max) buffer += text;
        return true;
    }

    private void commit() {
        if (editing < 0) return;
        switch (editing) {
            case WorkspaceBotPanel.FIELD_PREFIX -> BotConfig.setNickPrefix(buffer);
            case WorkspaceBotPanel.FIELD_PASSWORD -> BotConfig.setPassword(buffer);
            case WorkspaceBotPanel.FIELD_OWNER -> BotConfig.setOwnerNick(buffer);
            default -> { }
        }
        dirty = true;
        editing = -1;
        buffer = "";
    }

    /** Called on navigation away and closing; clears stale regions and drag capture. */
    public void leave() {
        commit();
        release();
        hitCount = 0;
        if (dirty) { BotConfig.save(); dirty = false; }
    }

    private static float clamp(float value, float total, float view) {
        return Math.max(0, Math.min(value, Math.max(0, total - view)));
    }
}
