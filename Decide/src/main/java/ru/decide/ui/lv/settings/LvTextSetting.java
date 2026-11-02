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
import ru.decide.module.api.settings.impl.StringSetting;
import ru.decide.ui.fonts.FontsLV;
import ru.decide.ui.lv.settings.LvRenderHelper;
import ru.decide.ui.lv.settings.SettingWidget;
import ru.decide.ui.theme.ThemeManager;
import ru.decide.utils.other.ClipboardPaste;
import ru.decide.utils.render.Draw;

@Environment(value=EnvType.CLIENT)
public class LvTextSetting
extends SettingWidget {
    private static final float FIELD_W = 92.0f;
    private static final int MAX_LENGTH = 64;
    private static LvTextSetting focused;
    private final StringSetting backend;

    public LvTextSetting(StringSetting stringSetting) {
        this.backend = stringSetting;
    }

    @Override
    public String name() {
        return this.backend.getName();
    }

    public void typeChar(int n) {
        String string;
        if (!Character.isISOControl(n) && (string = this.backend.getValue()).length() < 64) {
            this.backend.set(string + new String(Character.toChars(n)));
        }
    }

    @Override
    public boolean typeKey(int n) {
        if (ClipboardPaste.pasteCombo(n)) {
            this.backend.set(ClipboardPaste.append(this.backend.getValue(), 64));
            return true;
        }
        switch (n) {
            case 256: 
            case 257: 
            case 335: {
                focused = null;
                return true;
            }
            case 259: {
                String string = this.backend.getValue();
                if (!string.isEmpty()) {
                    this.backend.set(string.substring(0, string.length() - 1));
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public float height() {
        return 16.0f;
    }

    @Override
    public void render(float f, float f2, float f3, float f4) {
        String string;
        float f5 = 12.0f;
        float f6 = f + f3 - 92.0f - 4.0f;
        float f7 = f2 + (16.0f - f5) * 0.5f;
        boolean bl2 = this.isFocused();
        LvRenderHelper.drawName(this.backend.getName(), f, f2, f6 - (f + 6.0f) - 4.0f, f4);
        Draw.rect(f6, f7, 92.0f, f5, new Color(0, 0, 0, (int)(60.0f * f4)).getRGB(), 3.0f);
        if (bl2) {
            Draw.outline(f6, f7, 92.0f, f5, 0.6f, ThemeManager.accentBright(190.0f * f4), 3.0f);
        }
        boolean bl = (string = this.backend.getValue()) == null || string.isEmpty();
        String string2 = bl ? "..." : string;
        float f8 = 82.0f;
        String string3 = string2;
        while (string3.length() > 1 && FontsLV.MONTSERRAT_MEDIUM.width(string3, 6.0f) > f8) {
            string3 = string3.substring(1);
        }
        int n = bl ? new Color(255, 255, 255, (int)(110.0f * f4)).getRGB() : ThemeManager.accentSoft(220.0f * f4);
        FontsLV.MONTSERRAT_MEDIUM.draw(string3, f6 + 5.0f, f7 + 2.5f, 6.0f, n);
        if (bl2 && System.currentTimeMillis() / 500L % 2L == 0L) {
            float f9 = f6 + 5.0f + (bl ? 0.0f : FontsLV.MONTSERRAT_MEDIUM.width(string3, 6.0f)) + 0.5f;
            Draw.rect(f9, f7 + 2.0f, 0.8f, f5 - 4.0f, ThemeManager.accentBright(220.0f * f4), 0.0f);
        }
    }

    @Override
    public boolean isVisible() {
        return this.backend.getVisible().get();
    }

    @Override
    public boolean click(float f, float f2, float f3, float f4, float f5) {
        float f6 = 12.0f;
        float f7 = f + f3 - 92.0f - 4.0f;
        float f8 = f2 + (16.0f - f6) * 0.5f;
        if (f4 >= f7 && f4 <= f7 + 92.0f && f5 >= f8 && f5 <= f8 + f6) {
            focused = this.isFocused() ? null : this;
            return true;
        }
        if (this.isFocused()) {
            focused = null;
        }
        return false;
    }

    public boolean isFocused() {
        return focused == this;
    }

    public static void unfocusAll() {
        focused = null;
    }

    @Override
    public float preferredWidth() {
        return 6.0f + FontsLV.MONTSERRAT_MEDIUM.width(this.backend.getName(), 6.5f) + 6.0f + 92.0f + 8.0f;
    }
}

