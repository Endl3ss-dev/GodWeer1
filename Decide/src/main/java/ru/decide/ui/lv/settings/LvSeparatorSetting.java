/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.ui.lv.settings;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.module.api.settings.impl.DelimiterSetting;
import ru.decide.ui.fonts.FontsLV;
import ru.decide.ui.lv.settings.SettingWidget;
import ru.decide.ui.theme.ThemeManager;
import ru.decide.utils.render.Draw;

@Environment(value=EnvType.CLIENT)
public class LvSeparatorSetting
extends SettingWidget {
    public static final float HEIGHT = 18.0f;
    private final DelimiterSetting backend;

    public LvSeparatorSetting(DelimiterSetting delimiterSetting) {
        this.backend = delimiterSetting;
    }

    @Override
    public String name() {
        return this.backend.getName();
    }

    @Override
    public float height() {
        return 18.0f;
    }

    @Override
    public void render(float f, float f2, float f3, float f4) {
        float f5;
        String string = this.backend.getName();
        float f6 = 6.0f;
        float f7 = FontsLV.MONTSERRAT_MEDIUM.width(string, 6.0f);
        float f52 = Math.max(8.0f, f3 - 24.0f);
        if (f7 > f5) {
            f7 = f52;
        }
        float f8 = f + f3 / 2.0f;
        float f9 = f8 - f7 / 2.0f;
        float f10 = f2 + 9.0f - f6 / 2.0f;
        float f11 = f2 + 9.0f;
        float f12 = 6.0f;
        float f13 = 4.0f;
        int n = ThemeManager.accentSoft(210.0f * f4);
        int n2 = ThemeManager.accentSoft(55.0f * f4);
        float f14 = f + f13;
        float f15 = Math.max(0.0f, f9 - f12 - f14);
        float f16 = f9 + f7 + f12;
        float f17 = Math.max(0.0f, f + f3 - f13 - f16);
        if (f15 > 1.0f) {
            Draw.rect(f14, f11, f15, 1.0f, n2, 0.5f);
        }
        if (f17 > 1.0f) {
            Draw.rect(f16, f11, f17, 1.0f, n2, 0.5f);
        }
        FontsLV.MONTSERRAT_MEDIUM.draw(string, f9, f10, f6, n);
    }

    @Override
    public boolean isVisible() {
        return this.backend.getVisible().get();
    }

    @Override
    public boolean click(float f, float f2, float f3, float f4, float f5) {
        return false;
    }

    @Override
    public float preferredWidth() {
        return FontsLV.MONTSERRAT_MEDIUM.width(this.backend.getName(), 6.0f) + 48.0f;
    }
}

