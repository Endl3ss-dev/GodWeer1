/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.screen.editor;

import java.util.HashMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.lang.Lang;
import ru.decide.screen.editor.EditorButton;
import ru.decide.screen.editor.Rect;
import ru.decide.utils.animation.satoshi.Animation;
import ru.decide.utils.animation.satoshi.Direction;
import ru.decide.utils.animation.satoshi.EaseInOutQuad;
import ru.decide.utils.colors.ColorUtil;
import ru.decide.utils.render.RenderUtil;
import ru.decide.utils.render.font.Fonts;

@Environment(value=EnvType.CLIENT)
public final class EditorWidgets {
    private final Map<String, Animation> anims = new HashMap<String, Animation>();
    private final Map<String, float[]> smoothVals = new HashMap<String, float[]>();
    private float mouseX;
    private float mouseY;

    public void setMouse(float mouseX, float mouseY) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }

    public float mouseX() {
        return this.mouseX;
    }

    public float mouseY() {
        return this.mouseY;
    }

    public Animation anim(String key) {
        return this.anims.computeIfAbsent(key, k -> new EaseInOutQuad(300, 1.0));
    }

    public float rowHover(String key, Rect bounds) {
        Animation animation = this.anim(key + ":hover");
        animation.setDirection(bounds.contains(this.mouseX, this.mouseY) ? Direction.FORWARDS : Direction.BACKWARDS);
        return animation.getOutput();
    }

    public float smooth(String key, float target) {
        float[] value = this.smoothVals.computeIfAbsent(key, k -> new float[]{target});
        value[0] = value[0] + (target - value[0]) * 0.2f;
        return value[0];
    }

    public void drawRowBackground(Rect b, float hover, float alpha) {
        RenderUtil.Render2D.glow(b.x(), b.y(), b.width(), b.height(), ColorUtil.getColor(0, 0.06f * alpha), 4.0f, 6.0f, 1.0f);
        RenderUtil.Render2D.rect(b.x(), b.y(), b.width(), b.height(), ColorUtil.getColor(40, 0.15f * alpha), 4.0f);
        RenderUtil.Render2D.outline(b.x(), b.y(), b.width(), b.height(), 0.5f, ColorUtil.replAlpha(ColorUtil.client(), alpha * hover), 4.0f);
    }

    public void drawToggleRow(Rect b, String label, boolean value, String key, float alpha) {
        if (b.width() <= 0.0f || alpha <= 0.01f) {
            return;
        }
        float hover = this.rowHover(key, b);
        this.drawRowBackground(b, hover, alpha);
        Fonts.sf_regular.draw(label, b.x() + 6.0f, b.y() + 3.2f, 6.0f, ColorUtil.getColor(255, alpha * (0.5f + 0.5f * hover)));
        Animation state = this.anim(key + ":on");
        state.setDirection(value ? Direction.FORWARDS : Direction.BACKWARDS);
        float on = state.getOutput();
        RenderUtil.Render2D.rect(b.x() + b.width() - 6.0f - 11.0f, b.y() + 3.5f, 11.0f, 6.0f, ColorUtil.overCol(ColorUtil.getColor(0, 0.2f * alpha), ColorUtil.replAlpha(ColorUtil.client(), alpha * (0.5f + 0.5f * hover)), on), 3.0f);
        RenderUtil.Render2D.rect(b.x() + b.width() - 6.0f - 11.0f + 1.0f + 4.5f * on, b.y() + 4.5f, 4.0f, 4.0f, ColorUtil.getColor(255, alpha * (0.2f + 0.4f * on + 0.4f * hover)), 2.0f);
    }

    public void drawButton(EditorButton button, float alpha) {
        float hover = button.hoverValue();
        float press = button.pressValue();
        float accent = Math.max(hover, press);
        RenderUtil.Render2D.glow(button.x(), button.y(), button.width(), button.height(), ColorUtil.getColor(0, 0.15f * alpha), 5.0f, 10.0f, 1.0f);
        RenderUtil.Blur.blur(button.x(), button.y(), button.width(), button.height(), alpha, 5.0f, ColorUtil.multAlpha(ColorUtil.multDark(ColorUtil.background(), 0.6f), alpha));
        if (button.accent()) {
            RenderUtil.Render2D.rect(button.x(), button.y(), button.width(), button.height(), ColorUtil.replAlpha(ColorUtil.client(), alpha * (0.2f + 0.2f * accent)), 5.0f);
        }
        RenderUtil.Render2D.outline(button.x(), button.y(), button.width(), button.height(), 0.5f, ColorUtil.replAlpha(ColorUtil.client(), alpha * (button.accent() ? 0.5f + 0.5f * accent : accent)), 5.0f);
        Fonts.sf_regular.drawCentered(Lang.tr(button.label()), button.x() + button.width() / 2.0f, button.y() + button.height() / 2.0f - 3.8f, 6.5f, ColorUtil.getColor(255, alpha * (0.6f + 0.4f * accent)));
    }
}

