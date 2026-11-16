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
import ru.decide.module.api.settings.impl.BindSetting;
import ru.decide.ui.fonts.FontsLV;
import ru.decide.ui.lv.settings.LvRenderHelper;
import ru.decide.ui.lv.settings.SettingWidget;
import ru.decide.utils.math.Keyboard;

@Environment(value=EnvType.CLIENT)
public class LvBindSetting
extends SettingWidget {
    private final BindSetting backend;
    private boolean listening;

    public LvBindSetting(BindSetting bindSetting) {
        this.backend = bindSetting;
    }

    @Override
    public String name() {
        return this.backend.getName();
    }

    public void setKey(int n) {
        this.backend.set(n);
    }

    @Override
    public float height() {
        return 16.0f;
    }

    @Override
    public void render(float f, float f2, float f3, float f4) {
        String string = this.listening ? "Press key..." : this.keyDisplay();
        float f5 = FontsLV.MONTSERRAT_MEDIUM.width(string, 6.0f) + 10.0f;
        float f6 = f + f3 - f5 - 4.0f;
        float f7 = f2 + 2.0f;
        LvRenderHelper.drawName(this.backend.getName(), f, f2, f6 - (f + 6.0f) - 4.0f, f4);
        LvRenderHelper.drawBtn(f6, f7, f5, 12.0f, string, f4);
    }

    @Override
    public boolean isVisible() {
        return this.backend.getVisible().get();
    }

    @Override
    public boolean click(float f, float f2, float f3, float f4, float f5) {
        String string = this.listening ? "Press key..." : this.keyDisplay();
        float f6 = FontsLV.MONTSERRAT_MEDIUM.width(string, 6.0f) + 10.0f;
        float f7 = f + f3 - f6 - 4.0f;
        float f8 = f2 + 2.0f;
        if (f4 >= f7 && f4 <= f7 + f6 && f5 >= f8 && f5 <= f8 + 12.0f) {
            this.listening = !this.listening;
            return true;
        }
        return false;
    }

    public void setListening(boolean bl) {
        this.listening = bl;
    }

    public boolean isListening() {
        return this.listening;
    }

    @Override
    public float preferredWidth() {
        String string = this.keyDisplay();
        return 6.0f + FontsLV.MONTSERRAT_MEDIUM.width(this.backend.getName(), 6.5f) + 6.0f + FontsLV.MONTSERRAT_MEDIUM.width(string, 6.0f) + 10.0f + 8.0f;
    }

    private String keyDisplay() {
        int n = this.backend.get();
        return n <= 0 ? "None" : Keyboard.keyName(n);
    }
}

