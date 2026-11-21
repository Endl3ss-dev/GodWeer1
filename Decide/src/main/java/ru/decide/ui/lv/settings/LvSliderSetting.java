/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.ui.lv.settings;

import java.awt.Color;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.module.api.settings.impl.SliderSetting;
import ru.decide.ui.fonts.FontsLV;
import ru.decide.ui.lv.settings.LvRenderHelper;
import ru.decide.ui.lv.settings.SettingWidget;
import ru.decide.ui.theme.ThemeManager;
import ru.decide.ui.util.Position;
import ru.decide.utils.other.GuiSounds;
import ru.decide.utils.render.Draw;

@Environment(value=EnvType.CLIENT)
public class LvSliderSetting
extends SettingWidget {
    private final SliderSetting backend;
    private boolean dragging;
    private String lastSoundText;
    private float visProgress = -1.0f;
    private long lastNs = System.nanoTime();
    private final float defaultValue;
    public static final float ROW_H = 22.0f;
    public static final float BAR_H = 3.0f;
    public static final float BAR_TOP = 16.0f;

    public LvSliderSetting(SliderSetting sliderSetting) {
        this.backend = sliderSetting;
        this.defaultValue = sliderSetting.getValue().floatValue();
    }

    @Override
    public String name() {
        return this.backend.getName();
    }

    @Override
    public float height() {
        return 22.0f;
    }

    private static float clamp01(float f) {
        return f < 0.0f ? 0.0f : (f > 1.0f ? 1.0f : f);
    }

    @Override
    public void render(float f, float f2, float f3, float f4) {
        long l = System.nanoTime();
        float f6 = Math.min(0.1f, (float)(l - this.lastNs) / 1.0E9f);
        this.lastNs = l;
        float f7 = f + 6.0f;
        float f8 = f3 - 12.0f;
        float f9 = f2 + 16.0f;
        if (this.dragging) {
            float f5 = Position.mouseX();
            float f10 = LvSliderSetting.clamp01((f5 - f7) / f8);
            this.backend.set(Float.valueOf(this.backend.min + f10 * (this.backend.max - this.backend.min)));
            String string = this.formatValue(this.backend.getValue().floatValue());
            if (!string.equals(this.lastSoundText)) {
                this.lastSoundText = string;
                GuiSounds.sliderTick(this.getProgress());
            }
        }
        float f11 = 6.0f;
        String string = this.formatValue(this.backend.getValue().floatValue());
        float f12 = FontsLV.MONTSERRAT_MEDIUM.width(string, f11);
        float f13 = f + f3 - 6.0f - f12;
        FontsLV.MONTSERRAT_MEDIUM.draw(string, f13, f2 + 5.0f + 0.25f, f11, LvSliderSetting.rgba(255, 255, 255, 238.0f * f4));
        LvRenderHelper.drawName(this.backend.getName(), f, f2, f13 - (f + 6.0f) - 6.0f, f4);
        float f14 = this.getProgress();
        if (this.visProgress < 0.0f) {
            this.visProgress = f14;
        }
        this.visProgress += (f14 - this.visProgress) * (1.0f - (float)Math.exp(-f6 * 18.0f));
        if (Math.abs(f14 - this.visProgress) < 0.0015f) {
            this.visProgress = f14;
        }
        float f15 = f7 + f8 * LvSliderSetting.clamp01(this.visProgress);
        float f16 = 1.5f;
        Draw.rect(f7, f9, f8, 3.0f, LvSliderSetting.rgba(16, 16, 16, 64.0f * f4), f16);
        if (f15 - f7 > 0.6f) {
            int n = ThemeManager.gradientA(215.0f * f4);
            int n2 = ThemeManager.gradientB(215.0f * f4);
            Draw.gradientRect(f7, f9, f15 - f7, 3.0f, new int[]{n, n2, n2, n}, f16);
            Draw.outline(f7, f9, f15 - f7, 3.0f, 0.5f, ThemeManager.accentBright(150.0f * f4), f16);
        }
        float f17 = 6.5f;
        float f18 = 4.0f;
        float f19 = 2.0f;
        float f20 = Math.max(f7, Math.min(f7 + f8 - f17, f15 - f17 * 0.5f));
        float f21 = f9 + 1.5f - f18 * 0.5f;
        Draw.rect(f20, f21, f17, f18, LvSliderSetting.rgba(255, 255, 255, 245.0f * f4), f19);
        Draw.outline(f20 - 0.5f, f21 - 0.5f, f17 + 1.0f, f18 + 1.0f, 0.5f, LvSliderSetting.rgba(16, 16, 16, 128.0f * f4), f19);
    }

    private static int rgba(int n, int n2, int n3, float f) {
        int n4 = Math.max(0, Math.min(255, Math.round(f)));
        return n4 <= 0 ? 0 : new Color(n, n2, n3, n4).getRGB();
    }

    @Override
    public boolean isVisible() {
        return this.backend.getVisible().get();
    }

    @Override
    public boolean click(float f, float f2, float f3, float f4, float f5) {
        float f6 = f + 6.0f;
        float f7 = f3 - 12.0f;
        float f8 = f2 + 16.0f;
        if (f4 >= f6 - 4.0f && f4 <= f6 + f7 + 4.0f && f5 >= f8 - 5.0f && f5 <= f8 + 3.0f + 5.0f) {
            this.dragging = true;
            GuiSounds.sliderGrab();
            float f9 = LvSliderSetting.clamp01((f4 - f6) / f7);
            this.backend.set(Float.valueOf(this.backend.min + f9 * (this.backend.max - this.backend.min)));
            this.lastSoundText = this.formatValue(this.backend.getValue().floatValue());
            return true;
        }
        return false;
    }

    @Override
    public void releaseDrag() {
        if (this.dragging) {
            this.dragging = false;
            GuiSounds.sliderRelease();
        }
    }

    @Override
    public boolean middleClick(float f, float f2, float f3, float f4, float f5) {
        float f6 = f + 6.0f;
        float f7 = f3 - 12.0f;
        float f8 = f2 + 16.0f;
        if (f4 >= f6 - 4.0f && f4 <= f6 + f7 + 4.0f && f5 >= f8 - 5.0f && f5 <= f8 + 3.0f + 5.0f) {
            this.dragging = false;
            this.backend.set(Float.valueOf(this.defaultValue));
            GuiSounds.sliderGrab();
            return true;
        }
        return false;
    }

    @Override
    public float preferredWidth() {
        float f = FontsLV.MONTSERRAT_MEDIUM.width(this.backend.getName(), 6.5f);
        float f2 = FontsLV.MONTSERRAT_MEDIUM.width(this.formatValue(this.backend.max), 6.0f);
        return Math.max(100.0f, 6.0f + f + 6.0f + f2 + 6.0f);
    }

    private float getProgress() {
        float f = this.backend.max - this.backend.min;
        return f <= 0.0f ? 0.0f : LvSliderSetting.clamp01((this.backend.getValue().floatValue() - this.backend.min) / f);
    }

    private boolean isInteger() {
        boolean bl = Math.abs(this.backend.min - (float)Math.round(this.backend.min)) < 1.0E-4f;
        boolean bl2 = Math.abs(this.backend.max - (float)Math.round(this.backend.max)) < 1.0E-4f;
        boolean bl3 = Math.abs(this.backend.increment - (float)Math.round(this.backend.increment)) < 1.0E-4f;
        return bl && bl2 && bl3;
    }

    private String formatValue(float f) {
        if (this.isInteger()) {
            return String.format("%.0f", Float.valueOf(f));
        }
        float f2 = this.backend.increment;
        int n = 1;
        if (f2 > 0.0f) {
            float f3 = f2;
            for (n = 0; n < 3 && Math.abs(f3 - (float)Math.round(f3)) > 1.0E-4f; ++n) {
                f3 *= 10.0f;
            }
            n = Math.max(n, 1);
        }
        return String.format("%." + n + "f", Float.valueOf(f));
    }
}

