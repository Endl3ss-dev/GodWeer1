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
import ru.decide.module.api.settings.impl.BooleanSetting;
import ru.decide.ui.fonts.FontsLV;
import ru.decide.ui.lv.settings.LvRenderHelper;
import ru.decide.ui.lv.settings.SettingWidget;
import ru.decide.ui.theme.AccentGradient;
import ru.decide.ui.theme.ThemeManager;
import ru.decide.ui.util.anim.Decelerate;
import ru.decide.ui.util.anim.Direction;
import ru.decide.utils.colors.ColorUtil;
import ru.decide.utils.render.Draw;

@Environment(value=EnvType.CLIENT)
public class LvBoolSetting
extends SettingWidget {
    private final BooleanSetting backend;
    private final Decelerate anim;
    public static final float PW = 19.1f;
    public static final float PH = 10.4f;
    public static final float PR = 4.8f;
    public static final float PAD = 4.0f;

    public LvBoolSetting(BooleanSetting booleanSetting) {
        this.backend = booleanSetting;
        this.anim = new Decelerate();
        this.anim.setMs(120);
        this.anim.setValue(1.0);
        this.anim.setDirection(booleanSetting.getValue() != false ? Direction.FORWARDS : Direction.BACKWARDS);
        this.anim.counter.setTime(System.currentTimeMillis() - 10000L);
    }

    @Override
    public String name() {
        return this.backend.getName();
    }

    public boolean getValue() {
        return this.backend.getValue();
    }

    public void setValue(boolean bl) {
        this.backend.set(bl);
    }

    @Override
    public float height() {
        return 16.0f;
    }

    @Override
    public void render(float f, float f2, float f3, float f4) {
        float f5 = f + f3 - 19.1f - 4.0f;
        float f6 = f2 + 2.8f;
        LvRenderHelper.drawName(this.backend.getName(), f, f2, f5 - (f + 6.0f) - 4.0f, f4);
        this.anim.setDirection(this.backend.getValue() != false ? Direction.FORWARDS : Direction.BACKWARDS);
        float f7 = this.anim.getOutput().floatValue();
        LvBoolSetting.drawToggle(f5, f6, f7, f4);
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
        float f6 = f + f3 - 19.1f - 4.0f;
        float f7 = f2 + 2.8f;
        if (f4 >= f6 && f4 <= f6 + 19.1f && f5 >= f7 && f5 <= f7 + 10.4f) {
            this.backend.set(this.backend.getValue() == false);
            return true;
        }
        return false;
    }

    public static void drawToggle(float f, float f2, float f3, float f4, float f5, float f6) {
        float f7 = f4 * 0.4615385f;
        int n = LvBoolSetting.rgba(96, 99, 105, 165.0f * f6 * (1.0f - f5));
        int n2 = LvBoolSetting.rgba(78, 81, 87, 165.0f * f6 * (1.0f - f5));
        int n3 = LvBoolSetting.rgba(120, 123, 129, 150.0f * f6);
        int n4 = ColorUtil.interpolateColor(n3, ThemeManager.accentBright(150.0f * f6), f5);
        if (f5 < 0.999f) {
            Draw.gradientRect(f + 0.5f, f2 + 0.5f, f3 - 1.0f, f4 - 1.0f, new int[]{n, n2, n2, n}, f7);
        }
        if (f5 > 0.001f) {
            AccentGradient.fillHorizontal(f + 0.5f, f2 + 0.5f, f3 - 1.0f, f4 - 1.0f, f7, 175.0f * f6 * f5);
        }
        Draw.outline(f, f2, f3, f4, 0.5f, n4, f7);
        float f8 = f4 * 0.8076923f;
        float f9 = f4 * 0.125f;
        float f10 = f + f9;
        float f11 = f + f3 - f8 - f9;
        float f12 = f10 + (f11 - f10) * f5;
        float f13 = f2 + (f4 - f8) * 0.5f;
        int n5 = LvBoolSetting.rgba(239, 252, 255, 255.0f * f6);
        int n6 = LvBoolSetting.rgba(242, 250, 255, 255.0f * f6);
        Draw.gradientRect(f12, f13, f8, f8, new int[]{n5, n5, n6, n5}, f8 * 0.5f);
        Draw.outline(f12, f13, f8, f8, 0.5f, LvBoolSetting.rgba(255, 255, 255, 210.0f * f6), f8 * 0.5f);
    }

    public static void drawToggle(float f, float f2, float f3, float f4) {
        LvBoolSetting.drawToggle(f, f2, 19.1f, 10.4f, f3, f4);
    }

    @Override
    public float preferredWidth() {
        return 6.0f + FontsLV.MONTSERRAT_MEDIUM.width(this.backend.getName(), 6.5f) + 6.0f + 19.1f + 4.0f;
    }
}

