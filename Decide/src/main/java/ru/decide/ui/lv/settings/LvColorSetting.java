/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_332
 */
package ru.decide.ui.lv.settings;

import java.awt.Color;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_332;
import ru.decide.module.api.settings.impl.ColorSetting;
import ru.decide.ui.fonts.FontsLV;
import ru.decide.ui.lv.settings.LvRenderHelper;
import ru.decide.ui.lv.settings.SettingWidget;
import ru.decide.ui.theme.ThemeManager;
import ru.decide.ui.util.Position;
import ru.decide.ui.util.anim.Decelerate;
import ru.decide.ui.util.anim.Direction;
import ru.decide.utils.other.GuiSounds;
import ru.decide.utils.render.Draw;

@Environment(value=EnvType.CLIENT)
public class LvColorSetting
extends SettingWidget {
    private static final float PAD = 6.0f;
    private static final float SV_W = 70.0f;
    private static final float SV_H = 50.0f;
    private static final float BAR_H = 4.0f;
    private static final float GAP = 4.0f;
    private static final float OV_W = 82.0f;
    private static final float OV_H = 78.0f;
    private final ColorSetting backend;
    private boolean open;
    private final Decelerate anim;
    private float hue;
    private float sat;
    private float bright;
    private float alphaPct;
    private float dispHue;
    private float dispSat;
    private float dispVal;
    private float dispAlpha;
    private float svVelX;
    private float svVelY;
    private long lastNs = System.nanoTime();
    private int drag;
    private boolean dragSv;
    private boolean dragHue;
    private boolean dragAlpha;

    public LvColorSetting(ColorSetting colorSetting) {
        this.backend = colorSetting;
        this.anim = new Decelerate();
        this.anim.setMs(220);
        this.anim.setValue(1.0);
        this.anim.setDirection(Direction.BACKWARDS);
        this.anim.counter.setTime(System.currentTimeMillis() - 10000L);
        this.parseBackend();
    }

    private void parseBackend() {
        float[] fs = LvColorSetting.unpack(this.backend.getValue());
        this.hue = fs[0];
        this.sat = fs[1];
        this.bright = fs[2];
        this.alphaPct = fs[3];
    }

    private static float clamp01(float f) {
        return f < 0.0f ? 0.0f : (f > 1.0f ? 1.0f : f);
    }

    private static int clamp255(float f) {
        return Math.max(0, Math.min(255, Math.round(f)));
    }

    private static float[] unpack(int n) {
        float f = (float)(n >>> 24 & 0xFF) / 255.0f;
        float f2 = (float)(n >> 16 & 0xFF) / 255.0f;
        float f3 = (float)(n >> 8 & 0xFF) / 255.0f;
        float f4 = (float)(n & 0xFF) / 255.0f;
        float[] fs = Color.RGBtoHSB((int)f2, (int)f3, (int)f4, null);
        return new float[]{fs[0], fs[1], fs[2], f};
    }

    private int currentArgb() {
        int n = LvColorSetting.clamp255(this.alphaPct * 255.0f);
        return n << 24 | 0xFFFFFF & Color.HSBtoRGB(this.hue, this.sat, this.bright);
    }

    @Override
    public String name() {
        return this.backend.getName();
    }

    @Override
    public float height() {
        return 16.0f;
    }

    @Override
    public void renderOverlay(class_332 drawContext, float f, float f2, float f3, float f4) {
        float f5 = this.anim.getOutput().floatValue();
        if (f5 <= 0.01f) {
            return;
        }
        if (this.drag != 0) {
            this.updateDrag(Position.mouseX(), Position.mouseY(), f, f2, f3);
        }
        float f6 = f5 * f4;
        long l = System.nanoTime();
        float f7 = Math.min(0.05f, (float)(l - this.lastNs) / 1.0E9f);
        this.lastNs = l;
        float f8 = 1.0f - (float)Math.exp(-f7 * 16.0f);
        this.dispHue += (this.hue - this.dispHue) * f8;
        this.dispAlpha += (this.alphaPct - this.dispAlpha) * f8;
        float f9 = 200.0f;
        float f10 = 22.0f;
        this.svVelX += ((this.sat - this.dispSat) * f9 - this.svVelX * f10) * f7;
        this.svVelY += ((this.bright - this.dispVal) * f9 - this.svVelY * f10) * f7;
        this.dispSat += this.svVelX * f7;
        this.dispVal += this.svVelY * f7;
        float f12 = f + f3 - 82.0f - 4.0f;
        float f13 = f2 + 16.0f + 1.0f;
        LvRenderHelper.drawDropBackground(f12, f13, 82.0f, 78.0f, f6);
        float f14 = f12 + 6.0f;
        float f15 = f13 + 6.0f;
        int n = Color.HSBtoRGB(this.hue, 1.0f, 1.0f);
        int n2 = n & 0xFFFFFF | (int)(255.0f * f6) << 24;
        int n3 = (int)(255.0f * f6) << 24 | 0xFFFFFF;
        int n4 = (int)(255.0f * f6) << 24;
        Draw.gradientRect(f14, f15, 70.0f, 50.0f, new int[]{n3, n2, n2, n3}, 2.0f);
        Draw.gradientRect(f14, f15, 70.0f, 50.0f, new int[]{n4, n4, -16777216, -16777216}, 2.0f);
        float f16 = f14 + 70.0f * this.dispSat;
        float f17 = f15 + 50.0f * (1.0f - this.dispVal);
        int n5 = (int)(220.0f * f6) << 24 | 0xFFFFFF;
        Draw.outline(f16 - 3.0f, f17 - 3.0f, 5.0f, 5.0f, 1.0f, n5, 3.0f);
        float f18 = f15 + 50.0f + 4.0f;
        this.renderHueBar(f14, f18, 70.0f, 4.0f, f6);
        Draw.rect(f14 + 70.0f * this.dispHue - 1.0f, f18 - 1.0f, 2.0f, 6.0f, n5, 1.0f);
        float f19 = f18 + 4.0f + 4.0f;
        this.renderAlphaBar(f14, f19, 70.0f, 4.0f, f6);
        Draw.rect(f14 + 70.0f * this.dispAlpha - 1.0f, f19 - 1.0f, 2.0f, 6.0f, n5, 1.0f);
    }

    @Override
    public void render(float f, float f2, float f3, float f4) {
        if (this.drag == 0) {
            this.parseBackend();
        }
        int n = this.backend.getValue();
        String string = String.format("#%06X", n & 0xFFFFFF);
        float f5 = FontsLV.MONTSERRAT_MEDIUM.width(string, 5.5f);
        float f6 = 8.0f;
        float f7 = f6 + 4.0f + f5 + 10.0f;
        float f8 = f + f3 - f7 - 4.0f;
        float f9 = f2 + 2.0f;
        LvRenderHelper.drawName(this.backend.getName(), f, f2, f8 - (f + 6.0f) - 4.0f, f4);
        LvRenderHelper.drawPanelBg(f8, f9, f7, 12.0f, 3.0f, f4);
        int n2 = n & 0xFFFFFF | (int)((float)(n >>> 24 & 0xFF) * f4) << 24;
        Draw.rect(f8 + 3.0f, f9 + (12.0f - f6) * 0.5f, f6, f6, n2, 2.0f);
        FontsLV.MONTSERRAT_MEDIUM.draw(string, f8 + 3.0f + f6 + 4.0f, f9 + 3.0f, 5.5f, ThemeManager.accentSoft(200.0f * f4));
    }

    @Override
    public boolean isVisible() {
        return this.backend.getVisible().get();
    }

    @Override
    public boolean click(float f, float f2, float f3, float f4, float f5) {
        int n = this.backend.getValue();
        String string = String.format("#%06X", n & 0xFFFFFF);
        float f6 = FontsLV.MONTSERRAT_MEDIUM.width(string, 5.5f);
        float f7 = 12.0f + f6 + 10.0f;
        float f8 = f + f3 - f7 - 4.0f;
        float f9 = f2 + 2.0f;
        if (f4 >= f8 && f4 <= f8 + f7 && f5 >= f9 && f5 <= f9 + 12.0f) {
            if (this.open) {
                this.closeOverlay();
            } else {
                this.openPicker();
            }
            return true;
        }
        return false;
    }

    @Override
    public void closeOverlay() {
        if (this.open) {
            this.open = false;
            this.drag = 0;
            this.dragSv = false;
            this.dragHue = false;
            this.dragAlpha = false;
            this.anim.setDirection(Direction.BACKWARDS);
            this.anim.counter.resetCounter();
        }
    }

    @Override
    public boolean isOverlayOpen() {
        return this.open;
    }

    @Override
    public void releaseDrag() {
        this.drag = 0;
        this.dragSv = false;
        this.dragHue = false;
        this.dragAlpha = false;
    }

    @Override
    public boolean clickOverlay(float f, float f2, float f3, float f4, float f5) {
        float f6 = this.anim.getOutput().floatValue();
        if (!this.open || f6 <= 0.1f) {
            return false;
        }
        float f7 = 82.0f;
        float f8 = 78.0f;
        float f9 = f + f3 - f7 - 4.0f;
        float f10 = f2 + 16.0f + 1.0f;
        if (f4 < f9 || f4 > f9 + f7 || f5 < f10 || f5 > f10 + f8) {
            return false;
        }
        float f11 = f9 + 6.0f;
        float f12 = f10 + 6.0f;
        float f13 = f12 + 50.0f + 4.0f;
        float f14 = f13 + 4.0f + 4.0f;
        if (f4 >= f11 && f4 <= f11 + 70.0f && f5 >= f12 && f5 <= f12 + 50.0f) {
            this.sat = LvColorSetting.clamp01((f4 - f11) / 70.0f);
            this.bright = LvColorSetting.clamp01(1.0f - (f5 - f12) / 50.0f);
            this.drag = 1;
            this.dragSv = true;
            this.commit();
            return true;
        }
        if (f4 >= f11 && f4 <= f11 + 70.0f && f5 >= f13 - 2.0f && f5 <= f13 + 4.0f + 2.0f) {
            this.hue = LvColorSetting.clamp01((f4 - f11) / 70.0f);
            this.drag = 2;
            this.dragHue = true;
            this.commit();
            return true;
        }
        if (f4 >= f11 && f4 <= f11 + 70.0f && f5 >= f14 - 2.0f && f5 <= f14 + 4.0f + 2.0f) {
            this.alphaPct = LvColorSetting.clamp01((f4 - f11) / 70.0f);
            this.drag = 3;
            this.dragAlpha = true;
            this.commit();
            return true;
        }
        return true;
    }

    @Override
    public boolean hasOverlay() {
        return this.anim.getOutput().floatValue() > 0.01f;
    }

    private void updateDrag(float f, float f2, float f3, float f4, float f5) {
        float f6 = f3 + f5 - 82.0f - 4.0f;
        float f7 = f4 + 16.0f + 1.0f;
        float f8 = f6 + 6.0f;
        float f9 = f7 + 6.0f;
        if (this.drag == 1) {
            this.sat = LvColorSetting.clamp01((f - f8) / 70.0f);
            this.bright = LvColorSetting.clamp01(1.0f - (f2 - f9) / 50.0f);
        } else if (this.drag == 2) {
            this.hue = LvColorSetting.clamp01((f - f8) / 70.0f);
        } else if (this.drag == 3) {
            this.alphaPct = LvColorSetting.clamp01((f - f8) / 70.0f);
        }
        this.commit();
    }

    private void openPicker() {
        this.open = true;
        int n = this.backend.getValue();
        float[] fArray = new float[3];
        Color.RGBtoHSB(n >>> 16 & 0xFF, n >>> 8 & 0xFF, n & 0xFF, fArray);
        this.hue = fArray[0];
        this.sat = fArray[1];
        this.bright = fArray[2];
        this.alphaPct = (float)(n >>> 24 & 0xFF) / 255.0f;
        this.dispHue = this.hue;
        this.dispSat = this.sat;
        this.dispVal = this.bright;
        this.dispAlpha = this.alphaPct;
        this.svVelX = 0.0f;
        this.svVelY = 0.0f;
        this.lastNs = System.nanoTime();
        GuiSounds.expand(true);
        this.anim.setDirection(Direction.FORWARDS);
        this.anim.counter.resetCounter();
    }

    private void renderHueBar(float f, float f2, float f3, float f4, float f5) {
        int n = 12;
        for (int i = 0; i < n; ++i) {
            float f7 = (float)i / (float)n;
            float f8 = (float)(i + 1) / (float)n;
            int n2 = 0xFFFFFF & Color.HSBtoRGB((f7 + f8) * 0.5f, 1.0f, 1.0f);
            Draw.rect(f + (float)i * (f3 / (float)n), f2, f3 / (float)n + 1.0f, f4, n2, 0.0f);
        }
    }

    private void renderAlphaBar(float f, float f2, float f3, float f4, float f5) {
        int n = 0xFFFFFF & Color.HSBtoRGB(this.hue, this.sat, this.bright);
        int n2 = 16;
        float f6 = f3 / (float)n2;
        for (int i = 0; i < n2; ++i) {
            float f7 = (float)(i + 1) / (float)n2;
            int n3 = LvColorSetting.clamp255(f7 * 255.0f) << 24 | n;
            Draw.rect(f + (float)i * f6, f2, f6 + 0.5f, f4, n3, 0.0f);
        }
    }

    private void commit() {
        this.backend.set(this.currentArgb());
    }

    private static int rgba(int n, int n2, int n3, float f) {
        int n4 = Math.max(0, Math.min(255, Math.round(f)));
        return n4 <= 0 ? 0 : new Color(n, n2, n3, n4).getRGB();
    }

    @Override
    public float preferredWidth() {
        float f = FontsLV.MONTSERRAT_MEDIUM.width(String.format("#%06X", this.backend.getValue() & 0xFFFFFF), 5.5f);
        return 6.0f + FontsLV.MONTSERRAT_MEDIUM.width(this.backend.getName(), 6.5f) + 6.0f + 8.0f + 4.0f + f + 10.0f + 8.0f;
    }
}

