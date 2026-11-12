/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_332
 */
package ru.decide.ui.lv;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_332;
import ru.decide.ui.fonts.FontsLV;
import ru.decide.ui.theme.AccentGradient;
import ru.decide.ui.theme.ThemeManager;
import ru.decide.utils.colors.ColorUtil;
import ru.decide.utils.render.Draw;
import ru.decide.utils.render.Scissor;

@Environment(value=EnvType.CLIENT)
public final class LvEventsRenderer {
    private static final float ROW_H = 31.0f;
    private static final float GAP = 3.0f;
    private static final float CONTENT_HEIGHT = 280.0f;
    private static final float TAB_BAR_H = 18.0f;
    private static final float BOTTOM_PAD = 18.0f;
    private static final float DETAIL_ANIM_MS = 400.0f;
    private static final long OPEN_FADE_MS = 240L;
    private final List<Row> rows = new ArrayList<Row>();
    private final List<MineRow> mineRows = new ArrayList<MineRow>();
    private float scroll;
    private float scrollTarget;
    private float contentH;
    private long openMs = System.currentTimeMillis();
    private boolean transitioning = false;
    private float fadeOutTime = 0.0f;
    private Tab tab = Tab.EVENTS;
    private Tab pendingTab = null;
    private float tabFade = 1.0f;
    private float eventsTabT = 1.0f;
    private float minesTabT = 0.0f;
    private final float[] eventsTabRect = new float[4];
    private final float[] minesTabRect = new float[4];

    private static float clamp(float f, float f2, float f3) {
        return Math.max(f2, Math.min(f3, f));
    }

    public void open() {
        this.scroll = 0.0f;
        this.scrollTarget = 0.0f;
        this.openMs = System.currentTimeMillis();
        this.rows.clear();
        this.mineRows.clear();
    }

    private void renderContent(class_332 m, float x, float y, float panelW, float alpha, float dt) {
        float f13;
        float f6 = x + 117.0f;
        float f7 = y + 5.0f;
        float f8 = panelW - 122.0f;
        float f9 = 280.0f;
        this.updateTabTransition(dt);
        float f12 = alpha * this.openFadeAlpha() * LvEventsRenderer.smooth01(this.tabFade);
        this.renderTabs(m, x, f7, f12);
        float contentTop = f13 = f7 + 18.0f + 4.0f;
        if (this.tab == Tab.MINES) {
            this.renderMines(m, f6, contentTop, f8, f9 - (contentTop - f7), f12, dt);
        } else {
            this.renderEvents(m, f6, contentTop, f8, f9 - (contentTop - f7), f12, dt);
        }
    }

    private void renderTabs(class_332 m, float x, float y, float alpha) {
        float f4 = 5.5f;
        float f5 = 7.0f;
        float f6 = 12.0f;
        float f7 = 4.0f;
        float f8 = y + 4.0f;
        float f9 = FontsLV.MONTSERRAT_MEDIUM.width("\u0421\u043e\u0431\u044b\u0442\u0438\u044f", f4) + f5 * 2.0f;
        float f10 = FontsLV.MONTSERRAT_MEDIUM.width("\u0428\u0430\u0445\u0442\u044b", f4) + f5 * 2.0f;
        float f11 = x + 4.0f + 117.0f;
        float f12 = f11 + f9 + f7;
        this.drawTab(m, f11, f8, f9, f6, "\u0421\u043e\u0431\u044b\u0442\u0438\u044f", this.eventsTabT, alpha, this.eventsTabRect);
        this.drawTab(m, f12, f8, f10, f6, "\u0428\u0430\u0445\u0442\u044b", this.minesTabT, alpha, this.minesTabRect);
    }

    private void drawTab(class_332 m, float x, float y, float w, float h, String text, float t, float alpha, float[] rect) {
        float f7 = LvEventsRenderer.smooth01(t);
        Draw.rect(x, y, w, h, ColorUtil.getColor(255, 255, 255, (int)(16.0f * alpha * (1.0f - f7))), 4.0f);
        if (f7 > 0.001f) {
            AccentGradient.fillVertical(x, y, w, h, 4.0f, 210.0f * alpha * f7);
        }
        float f8 = 5.5f;
        float f9 = FontsLV.MONTSERRAT_MEDIUM.width(text, f8);
        float f10 = (130.0f + 125.0f * f7) * alpha;
        FontsLV.MONTSERRAT_MEDIUM.draw(text, x + (w - f9) * 0.5f, y + h * 0.5f - f8 * 0.5f, f8, ColorUtil.getColor(255, 255, 255, (int)f10));
        rect[0] = x;
        rect[1] = y;
        rect[2] = w;
        rect[3] = h;
    }

    private void renderScrollBar(float x, float y, float w, float h, float alpha, float contentBase) {
        float trackX = x + w - 2.5f;
        float trackY = y + 3.0f;
        float trackH = h - 6.0f;
        float ratio = LvEventsRenderer.clamp(trackH / Math.max(this.contentH + 18.0f, trackH), 0.0f, 1.0f);
        float handleH = LvEventsRenderer.clamp(trackH * ratio, 12.0f, trackH);
        float maxTravel = trackH - handleH;
        float progress = LvEventsRenderer.clamp(this.scroll / Math.max(contentBase, 1.0f), 0.0f, 1.0f);
        float handleY = trackY + maxTravel * progress;
        Draw.rect(trackX, trackY, 1.25f, trackH, ColorUtil.getColor(255, 255, 255, (int)(20.0f * alpha)));
        AccentGradient.fillVertical(trackX, handleY, 1.25f, handleH, 1.0f, 165.0f * alpha);
    }

    public void render(class_332 m, float x, float y, float panelWidth, float alpha, float dt) {
        float f6 = 1.0f - (float)Math.exp(-dt * 14.0f);
        this.scroll += (this.scrollTarget - this.scroll) * f6;
        if (Math.abs(this.scrollTarget - this.scroll) < 0.05f) {
            this.scroll = this.scrollTarget;
        }
        if (this.transitioning) {
            if (this.fadeOutTime > 0.0f) {
                this.fadeOutTime -= dt;
                float f7 = alpha * Math.max(0.0f, this.fadeOutTime / 0.15f);
                this.renderContent(m, x, y, panelWidth, f7, dt);
            }
        } else {
            this.renderContent(m, x, y, panelWidth, alpha, dt);
        }
    }

    public boolean click(float mx, float my) {
        if (LvEventsRenderer.hit(this.eventsTabRect, mx, my)) {
            this.setTab(Tab.EVENTS);
            return true;
        }
        if (LvEventsRenderer.hit(this.minesTabRect, mx, my)) {
            this.setTab(Tab.MINES);
            return true;
        }
        if (this.tab == Tab.MINES) {
            for (MineRow r : this.mineRows) {
                if (!r.contains(mx, my)) continue;
                return true;
            }
            return false;
        }
        for (Row r : this.rows) {
            if (!r.contains(mx, my)) continue;
            return true;
        }
        return false;
    }

    public void showMines() {
        this.setTab(Tab.MINES);
    }

    public void showEvents() {
        this.setTab(Tab.EVENTS);
    }

    public void scroll(float amount, float h) {
        float f2 = Math.max(0.0f, this.contentH - h + 18.0f);
        this.scrollTarget = LvEventsRenderer.clamp(this.scrollTarget - amount * 16.0f, 0.0f, f2);
    }

    private float openFadeAlpha() {
        float f = LvEventsRenderer.clamp((float)(System.currentTimeMillis() - this.openMs) / 240.0f, 0.0f, 1.0f);
        return f * f * (3.0f - 2.0f * f);
    }

    private void renderMines(class_332 m, float x, float top, float w, float h, float alpha, float dt) {
        this.mineRows.clear();
        Scissor.enable(x, top, w, h, 2.0f);
        float y = top + 5.0f - this.scroll;
        y = this.renderMineList(m, x, y, w, h, alpha, dt);
        this.contentH = Math.max(0.0f, y - (top + 5.0f - this.scroll));
        float max = Math.max(0.0f, this.contentH - h + 18.0f);
        this.scrollTarget = LvEventsRenderer.clamp(this.scrollTarget, 0.0f, max);
        this.scroll = LvEventsRenderer.clamp(this.scroll, 0.0f, max);
        Scissor.disable();
        this.renderScrollBar(x, top, w, h, alpha, max);
    }

    private float renderMineList(class_332 m, float x, float y, float w, float h, float alpha, float dt) {
        float yy = y;
        for (int i = 0; i < 3; ++i) {
            int n = i + 1;
            float cy = yy + (float)i * 34.0f;
            this.renderMineRow(m, x, cy, w, alpha, n, i);
            this.mineRows.add(new MineRow(x, cy, w, 31.0f));
            yy = cy + 31.0f + 3.0f;
        }
        return yy;
    }

    private void renderMineRow(class_332 m, float x, float y, float w, float alpha, int anarchy, int idx) {
        Draw.rect(x, y, w, 31.0f, ColorUtil.getColor(0, 0, 0, (int)(48.0f * alpha)), 5.0f);
        float f5 = x + 4.0f;
        float f6 = y + 5.0f;
        float f7 = 31.0f;
        float f8 = 21.0f;
        int[] rarity = new int[]{150, 150, 165};
        int[] darker = LvEventsRenderer.darker(rarity);
        int n = LvEventsRenderer.rgba(rarity[0], rarity[1], rarity[2], 175.0f * alpha);
        int n2 = LvEventsRenderer.rgba(darker[0], darker[1], darker[2], 175.0f * alpha);
        int n3 = LvEventsRenderer.mixColor(n, n2, 0.5f);
        Draw.gradientRect(f5, f6, f7, f8, new int[]{n, n3, n2, n3}, 4.0f);
        Draw.rect(f5 + f7 * 0.5f - 5.0f, f6 + f8 * 0.5f - 5.0f, 10.0f, 10.0f, ColorUtil.getColor(255, 255, 255, (int)(235.0f * alpha)), 3.0f);
        float f9 = f5 + f7 + 7.0f;
        String cmd = "/an" + anarchy;
        float f10 = FontsLV.MONTSERRAT_MEDIUM.width(cmd, 5.0f);
        FontsLV.MONTSERRAT_MEDIUM.draw(cmd, x + w - f10 - 6.0f, y + 6.0f, 5.0f, ThemeManager.accentBright(200.0f * alpha));
        String remaining = "\u041e\u0431\u043d\u043e\u0432\u043b\u044f\u0435\u0442\u0441\u044f";
        float f11 = FontsLV.MONTSERRAT_MEDIUM.width(remaining, 5.0f);
        FontsLV.MONTSERRAT_MEDIUM.draw(remaining, x + w - f11 - 6.0f, y + 17.0f, 5.0f, ColorUtil.getColor(255, 255, 255, (int)(120.0f * alpha)));
        String server = "\u0410\u043d\u0430\u0440\u0445\u0438\u044f-" + anarchy;
        FontsLV.MONTSERRAT_MEDIUM.draw(server, f9, y + 5.0f, 6.5f, ColorUtil.getColor(255, 255, 255, (int)(225.0f * alpha)));
        FontsLV.MONTSERRAT_MEDIUM.draw("\u041e\u0431\u044b\u0447\u043d\u044b\u0439", f9, y + 17.0f, 5.0f, ColorUtil.getColor(255, 255, 255, (int)(200.0f * alpha)));
    }

    private void renderEvents(class_332 m, float x, float top, float w, float h, float alpha, float dt) {
        this.rows.clear();
        Scissor.enable(x, top, w, h, 2.0f);
        float y = top + 5.0f - this.scroll;
        y = this.renderEventList(m, x, y, w, h, alpha, dt);
        if (this.rows.isEmpty()) {
            String empty = "\u0421\u043e\u0431\u044b\u0442\u0438\u0439 \u043f\u043e\u043a\u0430 \u043d\u0435\u0442";
            float tw = FontsLV.MONTSERRAT_MEDIUM.width(empty, 6.5f);
            FontsLV.MONTSERRAT_MEDIUM.draw(empty, x + (w - tw) * 0.5f, top + h * 0.5f, 6.5f, ColorUtil.getColor(255, 255, 255, (int)(115.0f * alpha)));
        }
        this.contentH = Math.max(0.0f, y - (top + 5.0f - this.scroll));
        float max = Math.max(0.0f, this.contentH - h + 18.0f);
        this.scrollTarget = LvEventsRenderer.clamp(this.scrollTarget, 0.0f, max);
        this.scroll = LvEventsRenderer.clamp(this.scroll, 0.0f, max);
        Scissor.disable();
        this.renderScrollBar(x, top, w, h, alpha, max);
    }

    private float renderEventList(class_332 m, float x, float y, float w, float h, float alpha, float dt) {
        float yy = y;
        for (int i = 0; i < 4; ++i) {
            float cy = yy + (float)i * 34.0f;
            this.renderEventRow(m, x, cy, w, alpha, i);
            this.rows.add(new Row(x, cy, w, 31.0f));
            yy = cy + 31.0f + 3.0f;
        }
        return yy;
    }

    private void renderEventRow(class_332 m, float x, float y, float w, float alpha, int idx) {
        Draw.rect(x, y, w, 31.0f, ColorUtil.getColor(0, 0, 0, (int)(48.0f * alpha)), 5.0f);
        float f5 = x + 4.0f;
        float f6 = y + 5.0f;
        float f7 = 31.0f;
        float f8 = 21.0f;
        int[] c = new int[]{140, 140, 150};
        int[] darker = LvEventsRenderer.darker(c);
        int n = LvEventsRenderer.rgba(c[0], c[1], c[2], 175.0f * alpha);
        int n2 = LvEventsRenderer.rgba(darker[0], darker[1], darker[2], 175.0f * alpha);
        int n3 = LvEventsRenderer.mixColor(n, n2, 0.5f);
        Draw.gradientRect(f5, f6, f7, f8, new int[]{n, n3, n2, n3}, 4.0f);
        Draw.rect(f5 + f7 * 0.5f - 5.0f, f6 + f8 * 0.5f - 5.0f, 10.0f, 10.0f, ColorUtil.getColor(255, 255, 255, (int)(235.0f * alpha)), 3.0f);
        float f9 = f5 + f7 + 7.0f;
        String cmd = "/an" + (idx + 1);
        float f10 = FontsLV.MONTSERRAT_MEDIUM.width(cmd, 5.0f);
        FontsLV.MONTSERRAT_MEDIUM.draw(cmd, x + w - f10 - 6.0f, y + 6.0f, 5.0f, ThemeManager.accentBright(200.0f * alpha));
        String status = "\u0418\u0434\u0451\u0442";
        float f11 = FontsLV.MONTSERRAT_MEDIUM.width(status, 5.0f);
        FontsLV.MONTSERRAT_MEDIUM.draw(status, x + w - f11 - 6.0f, y + 17.0f, 5.0f, ColorUtil.getColor(255, 255, 255, (int)(120.0f * alpha)));
        String name = idx == 0 ? "\u0422\u0435\u043c\u0430\u0442\u0438\u0447\u0435\u0441\u043a\u043e\u0435 \u0441\u043e\u0431\u044b\u0442\u0438\u0435" : (idx == 1 ? "\u0412\u0443\u043b\u043a\u0430\u043d" : (idx == 2 ? "\u041c\u0430\u044f\u043a" : "\u041c\u0435\u0442\u0435\u043e\u0440"));
        FontsLV.MONTSERRAT_MEDIUM.draw(name, f9, y + 5.0f, 6.5f, ColorUtil.getColor(255, 255, 255, (int)(225.0f * alpha)));
        String rarity = "\u041e\u0431\u044b\u0447\u043d\u044b\u0439";
        FontsLV.MONTSERRAT_MEDIUM.draw(rarity, f9, y + 17.0f, 5.0f, ColorUtil.getColor(255, 255, 255, (int)(200.0f * alpha)));
    }

    public void scrollbarRelease() {
    }

    public boolean scrollbarGrab(float mx, float my) {
        return false;
    }

    public void finishTransition() {
        this.transitioning = false;
        this.fadeOutTime = 0.0f;
    }

    private static int[] darker(int[] color) {
        return new int[]{Math.round((float)color[0] * 0.42f), Math.round((float)color[1] * 0.42f), Math.round((float)color[2] * 0.42f)};
    }

    private static int rgba(int r, int g, int b, float alpha) {
        int a = Math.max(0, Math.min(255, Math.round(alpha)));
        return a == 0 ? 0 : a << 24 | r << 16 | g << 8 | b;
    }

    private static int mixColor(int c1, int c2, float t) {
        int r = Math.round((float)(c1 >> 16 & 0xFF) + (float)((c2 >> 16 & 0xFF) - (c1 >> 16 & 0xFF)) * t);
        int g = Math.round((float)(c1 >> 8 & 0xFF) + (float)((c2 >> 8 & 0xFF) - (c1 >> 8 & 0xFF)) * t);
        int b = Math.round((float)(c1 & 0xFF) + (float)((c2 & 0xFF) - (c1 & 0xFF)) * t);
        int a = Math.round((float)(c1 >> 24 & 0xFF) + (float)((c2 >> 24 & 0xFF) - (c1 >> 24 & 0xFF)) * t);
        return a << 24 | r << 16 | g << 8 | b;
    }

    private void setTab(Tab t) {
        Tab current;
        Tab tab = current = this.pendingTab != null ? this.pendingTab : this.tab;
        if (t != current) {
            this.pendingTab = t;
        }
    }

    private void updateTabTransition(float dt) {
        float target = this.pendingTab != null ? 0.0f : 1.0f;
        this.tabFade += (target - this.tabFade) * (1.0f - (float)Math.exp(-dt * 16.0f));
        if (this.pendingTab != null && this.tabFade <= 0.04f) {
            this.tab = this.pendingTab;
            this.pendingTab = null;
            this.scroll = 0.0f;
            this.scrollTarget = 0.0f;
            this.tabFade = 0.0f;
        }
        Tab effective = this.pendingTab != null ? this.pendingTab : this.tab;
        float f = 1.0f - (float)Math.exp(-dt * 14.0f);
        this.eventsTabT += ((effective == Tab.EVENTS ? 1.0f : 0.0f) - this.eventsTabT) * f;
        this.minesTabT += ((effective == Tab.MINES ? 1.0f : 0.0f) - this.minesTabT) * f;
    }

    private static float smooth01(float f) {
        f = LvEventsRenderer.clamp(f, 0.0f, 1.0f);
        return f * f * (3.0f - 2.0f * f);
    }

    private static boolean hit(float[] r, float x, float y) {
        return x >= r[0] && x <= r[0] + r[2] && y >= r[1] && y <= r[1] + r[3];
    }

    public static String formatTime(int n) {
        if (n <= 0) {
            return "\u0418\u0434\u0451\u0442";
        }
        int m = n / 60;
        int s = n % 60;
        return m + "\u043c\u0438\u043d. " + s + "\u0441\u0435\u043a.";
    }

    @Environment(value=EnvType.CLIENT)
    private static enum Tab {
        EVENTS,
        MINES;

    }

    @Environment(value=EnvType.CLIENT)
    private static final class MineRow {
        final float x;
        final float y;
        final float w;
        final float h;

        MineRow(float x, float y, float w, float h) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }

        boolean contains(float mx, float my) {
            return mx >= this.x && mx <= this.x + this.w && my >= this.y && my <= this.y + this.h;
        }
    }

    @Environment(value=EnvType.CLIENT)
    private static final class Row {
        final float x;
        final float y;
        final float w;
        final float h;

        Row(float x, float y, float w, float h) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }

        boolean contains(float mx, float my) {
            return mx >= this.x && mx <= this.x + this.w && my >= this.y && my <= this.y + this.h;
        }
    }
}

