/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.utils.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.utils.animation.Animation;
import ru.decide.utils.animation.Easings;
import ru.decide.utils.colors.ColorUtil;
import ru.decide.utils.render.font.Font;

@Environment(value=EnvType.CLIENT)
public class RollingText {
    private final float slide;
    private String value = "";
    private String prev = "";
    private final Animation animation = new Animation();

    public RollingText() {
        this(4.0f);
    }

    public RollingText(float slide) {
        this.slide = slide;
    }

    public void set(String next) {
        if (next == null || next.equals(this.value)) {
            return;
        }
        this.prev = this.value;
        this.value = next;
        this.animation.set(0.0);
        this.animation.run(1.0, 0.18, Easings.QUAD_OUT);
    }

    public String get() {
        return this.value;
    }

    public float width(Font font, float size) {
        return font.getWidth(this.value, size);
    }

    public void draw(Font font, float x, float y, float size, int color) {
        this.animation.update();
        float t = this.animation.get();
        float cx = x;
        for (int i = 0; i < this.value.length(); ++i) {
            String old;
            String ch = this.value.substring(i, i + 1);
            int j = this.prev.length() - (this.value.length() - i);
            String string = old = j >= 0 && j < this.prev.length() ? this.prev.substring(j, j + 1) : null;
            if (t >= 1.0f || ch.equals(old)) {
                font.draw(ch, cx, y, size, color);
            } else {
                font.draw(ch, cx, y + this.slide * (1.0f - t), size, ColorUtil.multAlpha(color, t));
                if (old != null) {
                    font.draw(old, cx, y - this.slide * t, size, ColorUtil.multAlpha(color, 1.0f - t));
                }
            }
            cx += font.getWidth(ch, size);
        }
    }
}

