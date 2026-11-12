/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.ui.lv;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.ui.theme.AccentGradient;
import ru.decide.ui.theme.ThemeManager;
import ru.decide.ui.util.Position;
import ru.decide.utils.render.Draw;

@Environment(value=EnvType.CLIENT)
public final class LvScrollBar {
    private static final float BAR_W = 1.5f;
    private static final float ANIM_RATE = 16.0f;
    private boolean dragging;
    private float grabOffset;
    private float hoverT;
    private float dragT;
    private long lastNs = System.nanoTime();
    private boolean visible;
    private float gTrackX;
    private float gTrackY;
    private float gTrackH;
    private float gThumbY;
    private float gThumbH;
    private float gHitW;

    public void release() {
        this.dragging = false;
    }

    public float render(float x, float y, float trackH, float viewH, float contentH, float scroll, float alpha) {
        long now = System.nanoTime();
        float dt = Math.min(0.1f, (float)(now - this.lastNs) / 1.0E9f);
        this.lastNs = now;
        float maxScroll = Math.max(0.0f, contentH - viewH);
        boolean bl = this.visible = maxScroll > 0.5f;
        if (!this.visible) {
            this.dragging = false;
            this.hoverT += (0.0f - this.hoverT) * this.follow(dt);
            this.dragT += (0.0f - this.dragT) * this.follow(dt);
            return scroll;
        }
        float thumbH = Math.max(12.0f, trackH * (viewH / contentH));
        float scrollable = Math.max(1.0f, trackH - thumbH);
        if (this.dragging) {
            float raw = LvScrollBar.clamp(Position.mouseY() - this.grabOffset, y, y + scrollable);
            scroll = (raw - y) / scrollable * maxScroll;
        }
        scroll = LvScrollBar.clamp(scroll, 0.0f, maxScroll);
        float thumbY = y + scrollable * (scroll / maxScroll);
        float mx = Position.mouseX();
        float my = Position.mouseY();
        boolean hover = mx >= x - 4.0f && mx <= x + 1.5f + 4.0f && my >= thumbY - 2.0f && my <= thumbY + thumbH + 2.0f;
        this.hoverT += ((hover ? 1.0f : 0.0f) - this.hoverT) * this.follow(dt);
        this.dragT += ((this.dragging ? 1.0f : 0.0f) - this.dragT) * this.follow(dt);
        float ht = Math.max(this.hoverT, this.dragT);
        int trackAlpha = LvScrollBar.clamp255((20.0f + 18.0f * ht) * alpha);
        Draw.rect(x, y, 1.5f, trackH, ThemeManager.rgba(0xFFFFFF, trackAlpha), 0.75f);
        float fillAlpha = (160.0f + 95.0f * ht) * Math.max(0.0f, Math.min(1.0f, alpha)) * 255.0f;
        AccentGradient.fillVertical(x, thumbY, 1.5f, thumbH, 0.75f, fillAlpha);
        this.gTrackX = x;
        this.gTrackY = y;
        this.gTrackH = trackH;
        this.gThumbY = thumbY;
        this.gThumbH = thumbH;
        this.gHitW = 1.5f;
        return scroll;
    }

    public boolean isVisible() {
        return this.visible;
    }

    public boolean isDragging() {
        return this.dragging;
    }

    public boolean tryGrab(float mx, float my) {
        if (!this.visible) {
            return false;
        }
        if (mx < this.gTrackX - 4.0f || mx > this.gTrackX + this.gHitW + 4.0f) {
            return false;
        }
        if (my >= this.gThumbY - 2.0f && my <= this.gThumbY + this.gThumbH + 2.0f) {
            this.dragging = true;
            this.grabOffset = my - this.gThumbY;
            return true;
        }
        if (my >= this.gTrackY && my <= this.gTrackY + this.gTrackH) {
            this.dragging = true;
            this.grabOffset = this.gThumbH * 0.5f;
            return true;
        }
        return false;
    }

    private static float clamp(float v, float min, float max) {
        return Math.max(min, Math.min(max, v));
    }

    private static int clamp255(float f) {
        return Math.max(0, Math.min(255, Math.round(f)));
    }

    private float follow(float dt) {
        return 1.0f - (float)Math.exp(-dt * 16.0f);
    }
}

