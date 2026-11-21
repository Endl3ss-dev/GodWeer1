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
import ru.decide.module.api.settings.impl.ButtonSetting;
import ru.decide.ui.fonts.FontsLV;
import ru.decide.ui.lv.settings.SettingWidget;
import ru.decide.ui.theme.ThemeManager;
import ru.decide.ui.util.Position;
import ru.decide.utils.colors.ColorUtil;
import ru.decide.utils.render.Draw;

@Environment(value=EnvType.CLIENT)
public class LvButtonRowSetting
extends SettingWidget {
    public static final float HEIGHT = 18.0f;
    private final ButtonSetting backend;
    private float hoverT;
    private float pressT;
    private long lastNs = System.nanoTime();

    public LvButtonRowSetting(ButtonSetting buttonSetting) {
        this.backend = buttonSetting;
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
        long l = System.nanoTime();
        float dt = Math.min(0.1f, (float)(l - this.lastNs) / 1.0E9f);
        this.lastNs = l;
        float lerp = 1.0f - (float)Math.exp(-dt * 16.0f);
        boolean hover = Position.mouseX() >= f && Position.mouseX() <= f + f3 && Position.mouseY() >= f2 && Position.mouseY() <= f2 + this.height();
        this.hoverT += ((hover ? 1.0f : 0.0f) - this.hoverT) * lerp;
        this.pressT += (this.backend.pressAnim.get() - this.pressT) * lerp;
        float accent = Math.max(this.hoverT, this.pressT);
        Draw.rect(f, f2, f3, 18.0f, LvButtonRowSetting.rgba(0, 0, 0, 55.0f * f4), 4.0f);
        int n = ThemeManager.accentSoft(115.0f * accent * f4);
        int n2 = ColorUtil.interpolateColor(LvButtonRowSetting.rgba(0, 0, 0, 55.0f * f4), n, Math.min(1.0f, accent));
        Draw.rect(f + 0.5f, f2 + 0.5f, f3 - 1.0f, 17.0f, n2, 4.0f);
        Draw.outline(f, f2, f3, 18.0f, 0.5f, ColorUtil.interpolateColor(LvButtonRowSetting.rgba(255, 255, 255, 30.0f * f4), ThemeManager.accentBright(90.0f * f4), Math.min(1.0f, accent)), 4.0f);
        String string = this.backend.getName();
        float tw = FontsLV.MONTSERRAT_MEDIUM.width(string, 6.0f);
        FontsLV.MONTSERRAT_MEDIUM.draw(string, f + (f3 - tw) * 0.5f, f2 + 5.0f, 6.0f, LvButtonRowSetting.rgba(255, 255, 255, (int)((150.0f + 90.0f * accent) * f4)));
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
        if (f4 >= f && f4 <= f + f3 && f5 >= f2 && f5 <= f2 + this.height()) {
            this.backend.press();
            return true;
        }
        return false;
    }

    @Override
    public float preferredWidth() {
        return FontsLV.MONTSERRAT_MEDIUM.width(this.backend.getName(), 6.0f) + 48.0f;
    }
}

