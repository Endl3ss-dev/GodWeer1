package org.privates.client.screen.figmagui;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.registry.Registry;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.lwjgl.glfw.GLFW;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.settings.impl.*;
import org.privates.client.screen.newgui.Builder;
import org.privates.client.utils.keyboard.Keyboard;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.other.SoundUtil;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.draw.ScissorExp;
import org.privates.client.utils.render.font.Fonts;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Getter
@Setter
public class FigmaSetting extends Builder {
    private final FigmaModule module;
    private final org.privates.client.managers.module.settings.Setting setting;
    private float alphaV = 1f, visibleV = 1f, toggleV = 0f, sliderV = 0f, sliderRandMinV = 0f, sliderRandMaxV = 1f;
    private boolean draggingSlider = false, inputString = false, inputBind = false, colorPickerOpen = false, draggingColorSquare = false, draggingHue = false, cpInit = false;
    private int draggingRandHandle = -1, itemIndex = 0;
    private String stringCache = "", lastModeSelection = "";
    private final List<Item> items = new ArrayList<>();
    private float labelScrollX = 0f, modeIndicatorLocalX = -1f, modeIndicatorLocalY = 0f, modeIndicatorW = 0f, modeIndicatorH = 0f, cpHue = 0f, cpSat = 1f, cpVal = 1f, cpOpenV = 0f, cpHueV = 0f, cpSatV = 1f, cpValV = 1f;
    private boolean labelScrollForward = true;
    private long labelScrollTick = 0L, labelScrollPauseUntil = 0L;

    public FigmaSetting(FigmaModule module, org.privates.client.managers.module.settings.Setting setting, float x, float y) {
        this.module = module; this.setting = setting; this.x = x; this.y = y;
        float fs = module.getPanel().getGui().fs();
        this.width = module.getWidth() - 8f * fs;
        this.height = getAnimatedHeight(fs);
        if (setting instanceof ItemSetting) Registry.ITEM.stream().filter(i -> i != Items.AIR).forEach(items::add);
    }

    private float clamp(float v, float mn, float mx) { return Math.max(mn, Math.min(mx, v)); }

    public float getAnimatedHeight(float fs) {
        visibleV = Mathf.fast(visibleV, (boolean) setting.getVisible().get() ? 1f : 0f, 12f);
        cpOpenV = Mathf.fast(cpOpenV, colorPickerOpen ? 1f : 0f, 12f);
        height = getBaseHeight(fs) * visibleV;
        return height;
    }

    private float getBaseHeight(float fs) {
        if (setting instanceof SliderSetting || setting instanceof SliderSettingRand) return 26f * fs;
        if (setting instanceof ColorSetting) return (12f + 54f * cpOpenV) * fs;
        if (setting instanceof ModeSetting mode) return getModeLikeHeight(fs, mode.values);
        if (setting instanceof ListSetting<?> list) return getModeLikeHeight(fs, toStringList(list.values));
        if (setting instanceof MultiBooleanSetting multi) {
            List<String> v = new ArrayList<>(); for (BooleanSetting b : multi.getValues()) v.add(b.getName());
            return getModeLikeHeight(fs, v);
        }
        return 12f * fs;
    }

    private float getModeLikeHeight(float fs, List<String> values) {
        float label = 10f * fs, btnH = 17f * fs, gap = 2f * fs, btnX = 0f, maxW = width <= 0f ? module.getWidth() - 8f * fs : width;
        int rows = 1;
        for (String s : values) {
            float bw = Fonts.SF_SEMIBOLD.getWidth(s, 9f * fs) + 8f * fs;
            if (btnX > 0f && btnX + bw > maxW) { rows++; btnX = 0f; }
            btnX += bw + gap;
        }
        return label + 3f * fs + rows * (btnH + gap);
    }

    @Override
    public void render(MatrixStack ms, float mouseX, float mouseY) {
        float fs = module.getPanel().getGui().fs();
        if (getAnimatedHeight(fs) < 0.4f * fs) return;
        Theme theme = Theme.getInstance();
        float a = alphaV * visibleV;
        if (setting instanceof BooleanSetting b) { drawBoolean(ms, fs, theme, b, a); return; }
        if (setting instanceof SliderSetting s) { drawSlider(ms, mouseX, fs, theme, s, a); return; }
        if (setting instanceof SliderSettingRand s) { drawSliderRand(ms, mouseX, fs, theme, s, a); return; }
        if (setting instanceof ModeSetting m) { drawModeLike(ms, fs, theme, m.getName(), m.values, m.getValue(), a); return; }
        if (setting instanceof ListSetting<?> l) { drawModeLike(ms, fs, theme, l.getName(), toStringList(l.values), String.valueOf(l.getValue()), a); return; }
        if (setting instanceof MultiBooleanSetting m) { drawMultiBoolean(ms, fs, theme, m, a); return; }
        if (setting instanceof StringSetting s) { drawString(ms, fs, theme, s, a); return; }
        if (setting instanceof BindSetting b) { drawBind(ms, fs, theme, b, a); return; }
        if (setting instanceof ColorSetting c) { drawColor(ms, mouseX, mouseY, fs, theme, c, a); return; }
        if (setting instanceof ItemSetting i) { drawItem(ms, fs, theme, i, a); return; }
    }

    private void drawBoolean(MatrixStack ms, float fs, Theme t, BooleanSetting s, float a) {
        toggleV = Mathf.fast(toggleV, s.getValue() ? 1f : 0f, 14f);
        drawAuto(ms, s.getName(), x, y + height / 2f - 5f * fs, width - 25f * fs, ColorUtil.multAlpha(t.textColor(), a), 10f * fs, fs, y, height);
        float tw = 21f * fs, th = 12f * fs, tx = x + width - tw, ty = y + height / 2f - th / 2f;
        int tr = ColorUtil.overCol(ColorUtil.multAlpha(ColorUtil.getColor(58, 24, 43), a), ColorUtil.multAlpha(t.clientColor(), a), toggleV);
        RenderUtil.Rounded.roundedRect(ms, tx, ty, tw, th, tr, Round.of(32f * fs));
        float dot = 8f * fs, dx = tx + 2f * fs + (tw - dot - 4f * fs) * toggleV;
        RenderUtil.Rounded.roundedRect(ms, dx, ty + (th - dot) / 2f, dot, dot, ColorUtil.multAlpha(ColorUtil.getColor(255, 255, 255), a), Round.of(dot / 2f));
    }

    private void drawSlider(MatrixStack ms, float mouseX, float fs, Theme t, SliderSetting s, float a) {
        float ns = 10f * fs; String value = fmt(s.getValue()); float vw = Fonts.SF_SEMIBOLD.getWidth(value, ns);
        drawAuto(ms, s.getName(), x, y, width - vw - 6f * fs, ColorUtil.multAlpha(t.textColor(), a), ns, fs, y, 11f * fs);
        Fonts.SF_SEMIBOLD.draw(ms, value, x + width - vw, y, ColorUtil.multAlpha(ColorUtil.getColor(255, 255, 255), a), ns);
        float tw = width, th = 3f * fs, tx = x, ty = y + ns + 6f * fs, p = clamp((s.getValue() - s.min) / (s.max - s.min), 0f, 1f);
        if (draggingSlider) { float np = clamp((mouseX - tx) / tw, 0f, 1f); s.set((float) Mathf.step(s.min + (s.max - s.min) * np, s.increment <= 0 ? 0.1f : s.increment)); p = np; }
        sliderV = Mathf.fast(sliderV, p, 18f); float fill = tw * sliderV;
        RenderUtil.Rounded.roundedRect(ms, tx, ty, tw, th, ColorUtil.multAlpha(ColorUtil.getColor(70, 70, 70), a), Round.of(0.5f * fs));
        RenderUtil.Rounded.roundedRect(ms, tx, ty, fill, th, ColorUtil.multAlpha(t.clientColor(), a), Round.of(0.5f * fs));
        float r = 4.5f * fs, cx = clamp(tx + fill, tx + r, tx + tw - r);
        RenderUtil.Rounded.roundedRect(ms, cx - r, ty + th / 2f - r, r * 2f, r * 2f, ColorUtil.multAlpha(t.clientColor(), a), Round.of(r));
    }

    private void drawSliderRand(MatrixStack ms, float mouseX, float fs, Theme t, SliderSettingRand s, float a) {
        float ns = 10f * fs; String value = fmt(s.getValue().getMinValue()) + " - " + fmt(s.getValue().getMaxValue()); float vw = Fonts.SF_SEMIBOLD.getWidth(value, ns);
        drawAuto(ms, s.getName(), x, y, width - vw - 6f * fs, ColorUtil.multAlpha(t.textColor(), a), ns, fs, y, 11f * fs);
        Fonts.SF_SEMIBOLD.draw(ms, value, x + width - vw, y, ColorUtil.multAlpha(ColorUtil.getColor(255, 255, 255), a), ns);
        float tw = width, th = 3f * fs, tx = x, ty = y + ns + 6f * fs;
        float p1 = clamp((s.getValue().getMinValue() - s.min) / (s.max - s.min), 0f, 1f), p2 = clamp((s.getValue().getMaxValue() - s.min) / (s.max - s.min), 0f, 1f);
        if (draggingRandHandle != -1) {
            float np = clamp((mouseX - tx) / tw, 0f, 1f), nv = (float) Mathf.step(s.min + (s.max - s.min) * np, s.increment <= 0 ? 0.1f : s.increment);
            SliderSettingRand.RangeValue rv = s.getValue();
            if (draggingRandHandle == 0) rv.setMinValue(Math.min(nv, rv.getMaxValue())); else rv.setMaxValue(Math.max(nv, rv.getMinValue()));
            s.set(rv); p1 = clamp((s.getValue().getMinValue() - s.min) / (s.max - s.min), 0f, 1f); p2 = clamp((s.getValue().getMaxValue() - s.min) / (s.max - s.min), 0f, 1f);
        }
        sliderRandMinV = Mathf.fast(sliderRandMinV, p1, 18f); sliderRandMaxV = Mathf.fast(sliderRandMaxV, p2, 18f);
        RenderUtil.Rounded.roundedRect(ms, tx, ty, tw, th, ColorUtil.multAlpha(ColorUtil.getColor(70, 70, 70), a), Round.of(0.5f * fs));
        RenderUtil.Rounded.roundedRect(ms, tx + tw * sliderRandMinV, ty, tw * (sliderRandMaxV - sliderRandMinV), th, ColorUtil.multAlpha(t.clientColor(), a), Round.of(0.5f * fs));
        float r = 4.3f * fs, c1 = clamp(tx + tw * sliderRandMinV, tx + r, tx + tw - r), c2 = clamp(tx + tw * sliderRandMaxV, tx + r, tx + tw - r);
        RenderUtil.Rounded.roundedRect(ms, c1 - r, ty + th / 2f - r, r * 2f, r * 2f, ColorUtil.multAlpha(t.clientColor(), a), Round.of(r));
        RenderUtil.Rounded.roundedRect(ms, c2 - r, ty + th / 2f - r, r * 2f, r * 2f, ColorUtil.multAlpha(t.clientColor(), a), Round.of(r));
    }

    private void drawModeLike(MatrixStack ms, float fs, Theme t, String name, List<String> values, String active, float a) {
        float ls = 10f * fs, bh = 17f * fs, ts = 9f * fs, gap = 2f * fs;
        drawAuto(ms, name, x, y, width, ColorUtil.multAlpha(ColorUtil.getColor(177, 177, 177), a), ls, fs, y, 11f * fs);
        float bx = x, by = y + ls + 3f * fs, ax = x, ay = by, aw = 18f * fs;
        for (String s : values) {
            float bw = Fonts.SF_SEMIBOLD.getWidth(s, ts) + 8f * fs;
            if (bx > x && bx + bw > x + width) { bx = x; by += bh + gap; }
            if (s.equalsIgnoreCase(active)) { ax = bx; ay = by; aw = bw; }
            bx += bw + gap;
        }
        float tx = ax - x, ty = ay - y;
        if (modeIndicatorLocalX < 0f) { modeIndicatorLocalX = tx; modeIndicatorLocalY = ty; modeIndicatorW = aw; modeIndicatorH = bh; }
        else if (!active.equalsIgnoreCase(lastModeSelection)) {
            modeIndicatorLocalX = Mathf.fast(modeIndicatorLocalX, tx, 12f); modeIndicatorLocalY = Mathf.fast(modeIndicatorLocalY, ty, 12f);
            modeIndicatorW = Mathf.fast(modeIndicatorW, aw, 12f); modeIndicatorH = Mathf.fast(modeIndicatorH, bh, 12f);
        } else { modeIndicatorLocalX = tx; modeIndicatorLocalY = ty; modeIndicatorW = aw; modeIndicatorH = bh; }
        lastModeSelection = active;
        RenderUtil.Rounded.roundedRect(ms, x + modeIndicatorLocalX, y + modeIndicatorLocalY, modeIndicatorW, modeIndicatorH, ColorUtil.multAlpha(t.clientColor(), a), Round.of(4f * fs));
        bx = x; by = y + ls + 3f * fs;
        for (String s : values) {
            float bw = Fonts.SF_SEMIBOLD.getWidth(s, ts) + 8f * fs;
            if (bx > x && bx + bw > x + width) { bx = x; by += bh + gap; }
            RenderUtil.Rounded.roundedRect(ms, bx, by, bw, bh, ColorUtil.multAlpha(ColorUtil.getColor(64, 64, 64, 166), a), Round.of(4f * fs));
            int tc = s.equalsIgnoreCase(active) ? ColorUtil.multAlpha(ColorUtil.getColor(255, 255, 255), a) : ColorUtil.multAlpha(ColorUtil.getColor(177, 177, 177), a);
            Fonts.SF_SEMIBOLD.draw(ms, s, bx + 4f * fs, by + (bh - ts) / 2f - 0.5f * fs, tc, ts);
            bx += bw + gap;
        }
    }

    private void drawMultiBoolean(MatrixStack ms, float fs, Theme t, MultiBooleanSetting s, float a) {
        float ls = 10f * fs, bh = 17f * fs, ts = 9f * fs, gap = 2f * fs;
        drawAuto(ms, s.getName(), x, y, width, ColorUtil.multAlpha(ColorUtil.getColor(177, 177, 177), a), ls, fs, y, 11f * fs);
        float bx = x, by = y + ls + 3f * fs;
        for (BooleanSetting b : s.getValues()) {
            float bw = Fonts.SF_SEMIBOLD.getWidth(b.getName(), ts) + 8f * fs;
            if (bx > x && bx + bw > x + width) { bx = x; by += bh + gap; }
            int bg = b.getValue() ? ColorUtil.multAlpha(t.clientColor(), a) : ColorUtil.multAlpha(ColorUtil.getColor(64, 64, 64, 166), a);
            int tc = b.getValue() ? ColorUtil.multAlpha(ColorUtil.getColor(255, 255, 255), a) : ColorUtil.multAlpha(ColorUtil.getColor(177, 177, 177), a);
            RenderUtil.Rounded.roundedRect(ms, bx, by, bw, bh, bg, Round.of(4f * fs));
            Fonts.SF_SEMIBOLD.draw(ms, b.getName(), bx + 4f * fs, by + (bh - ts) / 2f - 0.5f * fs, tc, ts);
            bx += bw + gap;
        }
    }

    private void drawString(MatrixStack ms, float fs, Theme t, StringSetting s, float a) {
        String shown = inputString ? stringCache + (((System.currentTimeMillis() / 300) % 2 == 0) ? "_" : "") : s.getValue();
        drawAuto(ms, s.getName(), x, y + height / 2f - 5f * fs, width * 0.35f, ColorUtil.multAlpha(t.textColor(), a), 10f * fs, fs, y, height);
        float bw = Math.max(20f * fs, Math.min(width * 0.6f, Fonts.SF_SEMIBOLD.getWidth(shown, 9f * fs) + 6f * fs)), bx = x + width - bw, by = y + fs;
        RenderUtil.Rounded.roundedRect(ms, bx, by, bw, 10f * fs, ColorUtil.multAlpha(ColorUtil.getColor(64, 64, 64, 166), a), Round.of(3f * fs));
        drawClip(ms, shown, bx + 3f * fs, by + 1.4f * fs, bw - 4f * fs, ColorUtil.multAlpha(ColorUtil.getColor(255, 255, 255), a), 8.5f * fs, fs);
    }

    private void drawBind(MatrixStack ms, float fs, Theme t, BindSetting s, float a) {
        String keyText = inputBind ? "..." : (s.getValue() < 0 ? "None" : Keyboard.keyName(s.getValue()));
        drawAuto(ms, s.getName(), x, y + height / 2f - 5f * fs, width * 0.62f, ColorUtil.multAlpha(t.textColor(), a), 10f * fs, fs, y, height);
        float bw = Math.max(26f * fs, Fonts.SF_SEMIBOLD.getWidth(keyText, 9f * fs) + 8f * fs), bx = x + width - bw, by = y + 0.8f * fs;
        RenderUtil.Rounded.roundedRect(ms, bx, by, bw, 10.4f * fs, ColorUtil.multAlpha(ColorUtil.getColor(64, 64, 64, 166), a), Round.of(3f * fs));
        float ks = 8.6f * fs, kw = Fonts.SF_SEMIBOLD.getWidth(keyText, ks);
        Fonts.SF_SEMIBOLD.draw(ms, keyText, bx + bw / 2f - kw / 2f, by + 10.4f * fs / 2f - ks / 2f, ColorUtil.multAlpha(ColorUtil.getColor(255, 255, 255), a), ks);
    }

    private void drawColor(MatrixStack ms, float mouseX, float mouseY, float fs, Theme t, ColorSetting s, float a) {
        drawAuto(ms, s.getName(), x, y + fs, width * 0.72f, ColorUtil.multAlpha(t.textColor(), a), 10f * fs, fs, y, 12f * fs);
        float sw = 14f * fs, sx = x + width - sw, sy = y + fs;
        RenderUtil.Rounded.roundedRect(ms, sx, sy, sw, 10f * fs, ColorUtil.multAlpha(s.getValue(), a), Round.of(3f * fs));
        RenderUtil.Rounded.roundedOutline(ms, sx, sy, sw, 10f * fs, 0.7f * fs, ColorUtil.multAlpha(ColorUtil.getColor(255, 255, 255, 110), a), Round.of(3f * fs));
        if (cpOpenV < 0.01f) return;
        if (!cpInit) { float[] hsv = Color.RGBtoHSB((s.getValue() >> 16) & 255, (s.getValue() >> 8) & 255, s.getValue() & 255, null); cpHue = hsv[0]; cpSat = hsv[1]; cpVal = hsv[2]; cpInit = true; }
        cpHueV = Mathf.fast(cpHueV, cpHue, 14f);
        cpSatV = Mathf.fast(cpSatV, cpSat, 14f);
        cpValV = Mathf.fast(cpValV, cpVal, 14f);
        float px = x, py = y + 14f * fs, pw = width, ph = 50f * fs * cpOpenV;
        RenderUtil.Rounded.roundedRect(ms, px, py, pw, ph, ColorUtil.multAlpha(ColorUtil.getColor(18, 18, 18, 235), a * cpOpenV), Round.of(4f * fs));
        ScissorExp.push();
        ScissorExp.setFromComponentCoordinates(px, py, pw, ph);
        float qx = px + 4f * fs, qy = py + 4f * fs, qw = pw - 8f * fs, qh = 30f * fs;
        int hueCol = Color.HSBtoRGB(cpHueV, 1f, 1f);
        RenderUtil.Rounded.roundedRect(ms, qx, qy, qw, qh,
                ColorUtil.multAlpha(ColorUtil.getColor(0, 0, 0), a * cpOpenV),
                ColorUtil.multAlpha(ColorUtil.getColor(255, 255, 255), a * cpOpenV),
                ColorUtil.multAlpha(ColorUtil.getColor(0, 0, 0), a * cpOpenV),
                ColorUtil.multAlpha(hueCol, a * cpOpenV),
                Round.of(3.5f * fs));
        if (draggingColorSquare) { cpSat = clamp((mouseX - qx) / qw, 0f, 1f); cpVal = 1f - clamp((mouseY - qy) / qh, 0f, 1f); s.set(Color.HSBtoRGB(cpHue, cpSat, cpVal)); }
        float hx = px + 4f * fs, hy = qy + qh + 5f * fs, hw = pw - 8f * fs, hh = 6f * fs;
        if (draggingHue) { cpHue = clamp((mouseX - hx) / hw, 0f, 1f); s.set(Color.HSBtoRGB(cpHue, cpSat, cpVal)); }
        int hs = Math.max(12, (int) (hw / (2f * fs))); float hcw = hw / hs;
        for (int i = 0; i < hs; i++) { float h = (float) i / (hs - 1); RenderUtil.Rounded.roundedRect(ms, hx + i * hcw, hy, hcw + 0.45f, hh, ColorUtil.multAlpha(Color.HSBtoRGB(h, 1f, 1f), a * cpOpenV), Round.of(0)); }
        float pxs = qx + cpSatV * qw, pys = qy + (1f - cpValV) * qh;
        RenderUtil.Rounded.roundedOutline(ms, pxs - 2f * fs, pys - 2f * fs, 4f * fs, 4f * fs, 0.8f * fs, ColorUtil.multAlpha(ColorUtil.getColor(255, 255, 255), a * cpOpenV), Round.of(2f * fs));
        float hpos = hx + cpHueV * hw;
        RenderUtil.Rounded.roundedOutline(ms, hpos - 1.2f * fs, hy - 1.2f * fs, 2.4f * fs, hh + 2.4f * fs, 0.8f * fs, ColorUtil.multAlpha(ColorUtil.getColor(255, 255, 255), a * cpOpenV), Round.of(fs));
        ScissorExp.unset();
        ScissorExp.pop();
    }

    private void drawItem(MatrixStack ms, float fs, Theme t, ItemSetting s, float a) {
        String n = s.getValue() == null ? "None" : s.getValue().getTranslationKey().replace("item.minecraft.", "").replace('_', ' ');
        drawAuto(ms, s.getName(), x, y + height / 2f - 5f * fs, width * 0.35f, ColorUtil.multAlpha(t.textColor(), a), 10f * fs, fs, y, height);
        float bw = Math.max(30f * fs, Math.min(width * 0.58f, Fonts.SF_SEMIBOLD.getWidth(n, 8.5f * fs) + 8f * fs)), bx = x + width - bw, by = y + fs;
        RenderUtil.Rounded.roundedRect(ms, bx, by, bw, 10f * fs, ColorUtil.multAlpha(ColorUtil.getColor(64, 64, 64, 166), a), Round.of(3f * fs));
        drawClip(ms, n, bx + 3f * fs, by + 1.4f * fs, bw - 4f * fs, ColorUtil.multAlpha(ColorUtil.getColor(255, 255, 255), a), 8.5f * fs, fs);
    }

    private void drawClip(MatrixStack ms, String text, float tx, float ty, float mw, int c, float size, float fs) {
        if (mw <= 0f) return;
        ScissorExp.push(); ScissorExp.setFromComponentCoordinates(tx, ty - fs, mw, size + 2f * fs);
        Fonts.SF_SEMIBOLD.draw(ms, text, tx, ty, c, size); ScissorExp.unset(); ScissorExp.pop();
    }

    private void drawAuto(MatrixStack ms, String text, float tx, float ty, float mw, int c, float size, float fs, float cy, float ch) {
        if (mw <= 0f) return;
        float tw = Fonts.SF_SEMIBOLD.getWidth(text, size);
        if (tw > mw) {
            long now = System.currentTimeMillis();
            if (now > labelScrollPauseUntil && now - labelScrollTick > 18L) {
                float sp = 0.55f * fs, mn = -(tw - mw);
                if (labelScrollForward) { labelScrollX -= sp; if (labelScrollX <= mn) { labelScrollX = mn; labelScrollForward = false; labelScrollPauseUntil = now + 900L; } }
                else { labelScrollX += sp; if (labelScrollX >= 0f) { labelScrollX = 0f; labelScrollForward = true; labelScrollPauseUntil = now + 900L; } }
                labelScrollTick = now;
            }
        } else labelScrollX = 0f;
        ScissorExp.push(); ScissorExp.setFromComponentCoordinates(tx, cy, mw, ch);
        Fonts.SF_SEMIBOLD.draw(ms, text, tx + labelScrollX, ty, c, size); ScissorExp.unset(); ScissorExp.pop();
    }

    @Override
    public void mouse(float mx, float my, int button, int action) {
        if (visibleV < 0.1f) return;
        float fs = module.getPanel().getGui().fs();
        if (inputBind && setting instanceof BindSetting bind && action == GLFW.GLFW_PRESS && button >= 2 && button <= 4) {
            bind.set(button);
            inputBind = false;
            SoundUtil.playSound("bind_succes.wav", 0.1f);
            return;
        }
        if (setting instanceof BooleanSetting b) {
            float tw = 21f * fs, th = 12f * fs, tx = x + width - tw, ty = y + height / 2f - th / 2f;
            if (action == GLFW.GLFW_PRESS && button == 0 && isHovered(mx, my, tx, ty, tw, th)) { b.set(!b.getValue()); SoundUtil.playSound(b.getValue() ? "mode_pick_on.wav" : "mode_pick.wav", 0.1f); }
            return;
        }
        if (setting instanceof SliderSetting) {
            float tw = width, th = 3f * fs, tx = x, ty = y + 16f * fs;
            if (button == 0 && action == GLFW.GLFW_PRESS && isHovered(mx, my, tx, ty - 4f * fs, tw, th + 8f * fs)) {
                draggingSlider = true;
                SoundUtil.playSound("select.wav", 0.08f);
            }
            if (button == 0 && action == GLFW.GLFW_RELEASE) draggingSlider = false;
            return;
        }
        if (setting instanceof SliderSettingRand s) {
            float tw = width, tx = x, ty = y + 16f * fs;
            float p1 = clamp((s.getValue().getMinValue() - s.min) / (s.max - s.min), 0f, 1f), p2 = clamp((s.getValue().getMaxValue() - s.min) / (s.max - s.min), 0f, 1f);
            float d1 = tx + tw * p1, d2 = tx + tw * p2;
            if (button == 0 && action == GLFW.GLFW_PRESS) {
                if (Math.abs(mx - d1) <= 6f * fs && Math.abs(my - (ty + 1.5f * fs)) <= 6f * fs) {
                    draggingRandHandle = 0;
                    SoundUtil.playSound("select.wav", 0.08f);
                } else if (Math.abs(mx - d2) <= 6f * fs && Math.abs(my - (ty + 1.5f * fs)) <= 6f * fs) {
                    draggingRandHandle = 1;
                    SoundUtil.playSound("select.wav", 0.08f);
                }
            }
            if (button == 0 && action == GLFW.GLFW_RELEASE) draggingRandHandle = -1;
            return;
        }
        if (setting instanceof ModeSetting m) { clickMode(mx, my, button, action, toStringList(m.values), m::set); return; }
        if (setting instanceof ListSetting<?> l) { clickMode(mx, my, button, action, toStringList(l.values), v -> l.setAsObject(v)); return; }
        if (setting instanceof MultiBooleanSetting m) {
            if (button == 0 && action == GLFW.GLFW_PRESS) {
                float bh = 17f * fs, ts = 9f * fs, gap = 2f * fs, bx = x, by = y + 13f * fs;
                for (BooleanSetting b : m.getValues()) {
                    float bw = Fonts.SF_SEMIBOLD.getWidth(b.getName(), ts) + 8f * fs;
                    if (bx > x && bx + bw > x + width) { bx = x; by += bh + gap; }
                    if (isHovered(mx, my, bx, by, bw, bh)) { b.set(!b.getValue()); SoundUtil.playSound(b.getValue() ? "mode_pick_on.wav" : "mode_pick.wav", 0.1f); break; }
                    bx += bw + gap;
                }
            }
            return;
        }
        if (setting instanceof StringSetting s) {
            float bw = Math.max(20f * fs, Math.min(width * 0.6f, Fonts.SF_SEMIBOLD.getWidth(inputString ? stringCache : s.getValue(), 9f * fs) + 6f * fs)), bx = x + width - bw, by = y + fs;
            if (button == 0 && action == GLFW.GLFW_PRESS && isHovered(mx, my, bx, by, bw, 10f * fs)) { inputString = true; stringCache = s.getValue(); }
            else if (button == 0 && action == GLFW.GLFW_PRESS && !isHovered(mx, my, bx, by, bw, 10f * fs)) { if (inputString) s.set(stringCache); inputString = false; }
            return;
        }
        if (setting instanceof BindSetting s) {
            float bw = Math.max(26f * fs, Fonts.SF_SEMIBOLD.getWidth(inputBind ? "..." : (s.getValue() < 0 ? "None" : Keyboard.keyName(s.getValue())), 9f * fs) + 8f * fs), bx = x + width - bw, by = y + 0.8f * fs;
            if (button == 0 && action == GLFW.GLFW_PRESS && isHovered(mx, my, bx, by, bw, 10.4f * fs)) { inputBind = !inputBind; SoundUtil.playSound("select.wav", 0.08f); }
            return;
        }
        if (setting instanceof ColorSetting s) {
            float sw = 14f * fs, sx = x + width - sw, sy = y + fs;
            if (button == 0 && action == GLFW.GLFW_PRESS && isHovered(mx, my, sx, sy, sw, 10f * fs)) { colorPickerOpen = !colorPickerOpen; if (colorPickerOpen) cpInit = false; else { draggingColorSquare = false; draggingHue = false; } SoundUtil.playSound("select.wav", 0.08f); return; }
            if (!colorPickerOpen) return;
            float px = x, py = y + 14f * fs, qx = px + 4f * fs, qy = py + 4f * fs, qw = width - 8f * fs, qh = 30f * fs, hx = px + 4f * fs, hy = qy + qh + 5f * fs, hw = width - 8f * fs, hh = 6f * fs;
            if (button == 0 && action == GLFW.GLFW_PRESS) {
                if (isHovered(mx, my, qx, qy, qw, qh)) { draggingColorSquare = true; cpSat = clamp((mx - qx) / qw, 0f, 1f); cpVal = 1f - clamp((my - qy) / qh, 0f, 1f); s.set(Color.HSBtoRGB(cpHue, cpSat, cpVal)); SoundUtil.playSound("select.wav", 0.08f); return; }
                if (isHovered(mx, my, hx, hy, hw, hh)) { draggingHue = true; cpHue = clamp((mx - hx) / hw, 0f, 1f); s.set(Color.HSBtoRGB(cpHue, cpSat, cpVal)); SoundUtil.playSound("select.wav", 0.08f); return; }
                if (!isHovered(mx, my, px, py, width, 50f * fs)) { colorPickerOpen = false; draggingColorSquare = false; draggingHue = false; }
            }
            if (button == 0 && action == GLFW.GLFW_RELEASE) { draggingColorSquare = false; draggingHue = false; }
            return;
        }
        if (setting instanceof ItemSetting s) {
            float bw = Math.max(30f * fs, Math.min(width * 0.58f, Fonts.SF_SEMIBOLD.getWidth(s.getValue() == null ? "None" : s.getValue().getTranslationKey(), 8.5f * fs) + 8f * fs)), bx = x + width - bw, by = y + fs;
            if (button == 0 && action == GLFW.GLFW_PRESS && isHovered(mx, my, bx, by, bw, 10f * fs) && !items.isEmpty()) { if (s.getValue() != null) { int i = items.indexOf(s.getValue()); itemIndex = i < 0 ? 0 : i; } itemIndex = (itemIndex + 1) % items.size(); s.set(items.get(itemIndex)); SoundUtil.playSound("select.wav", 0.08f); }
            else if (button == 1 && action == GLFW.GLFW_PRESS && isHovered(mx, my, bx, by, bw, 10f * fs) && !items.isEmpty()) { if (s.getValue() != null) { int i = items.indexOf(s.getValue()); itemIndex = i < 0 ? 0 : i; } itemIndex = (itemIndex - 1 + items.size()) % items.size(); s.set(items.get(itemIndex)); SoundUtil.playSound("select.wav", 0.08f); }
        }
    }

    private void clickMode(float mx, float my, int btn, int act, List<String> vals, java.util.function.Consumer<String> setter) {
        if (btn != 0 || act != GLFW.GLFW_PRESS) return;
        float fs = module.getPanel().getGui().fs(), ls = 10f * fs, bh = 17f * fs, ts = 9f * fs, gap = 2f * fs, bx = x, by = y + ls + 3f * fs;
        for (String s : vals) {
            float bw = Fonts.SF_SEMIBOLD.getWidth(s, ts) + 8f * fs;
            if (bx > x && bx + bw > x + width) { bx = x; by += bh + gap; }
            if (isHovered(mx, my, bx, by, bw, bh)) { setter.accept(s); SoundUtil.playSound("mode_pick.wav", 0.1f); break; }
            bx += bw + gap;
        }
    }

    private List<String> toStringList(List<?> values) { List<String> out = new ArrayList<>(); for (Object v : values) out.add(String.valueOf(v)); return out; }
    private String fmt(float v) { String s = String.format(Locale.US, "%.2f", v); return s.endsWith("00") ? s.substring(0, s.length() - 3) : (s.endsWith("0") ? s.substring(0, s.length() - 1) : s); }

    @Override
    public void key(int key) {
        if (inputBind && setting instanceof BindSetting b) { boolean clear = key == GLFW.GLFW_KEY_ESCAPE || key == GLFW.GLFW_KEY_DELETE; b.set(clear ? -1 : key); inputBind = false; SoundUtil.playSound(clear ? "bind_remove.wav" : "bind_succes.wav", 0.1f); return; }
        if (inputString && setting instanceof StringSetting s) {
            if (key == GLFW.GLFW_KEY_ESCAPE) { inputString = false; stringCache = ""; }
            else if (key == GLFW.GLFW_KEY_ENTER) { s.set(stringCache); inputString = false; }
            else if (key == GLFW.GLFW_KEY_BACKSPACE && !stringCache.isEmpty()) stringCache = stringCache.substring(0, stringCache.length() - 1);
        }
    }

    @Override
    public void charTyped(char c) {
        if (!(inputString && setting instanceof StringSetting s)) return;
        if (c < 32 || c == 127) return;
        if (s.isOnlyNumber()) {
            if (Character.isDigit(c) || (c == '.' && !stringCache.contains(".")) || (c == '-' && stringCache.isEmpty())) stringCache += c;
        } else stringCache += c;
    }
}
