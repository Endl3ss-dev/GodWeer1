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
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_332;
import ru.decide.module.api.settings.impl.BooleanSetting;
import ru.decide.module.api.settings.impl.MultiBooleanSetting;
import ru.decide.ui.fonts.FontsLV;
import ru.decide.ui.lv.settings.LvRenderHelper;
import ru.decide.ui.lv.settings.SettingWidget;
import ru.decide.ui.theme.AccentGradient;
import ru.decide.ui.theme.ThemeManager;
import ru.decide.ui.util.anim.Decelerate;
import ru.decide.ui.util.anim.Direction;
import ru.decide.utils.other.GuiSounds;

@Environment(value=EnvType.CLIENT)
public class LvMultiSelectSetting
extends SettingWidget {
    private static final float ITEM_H = 14.0f;
    private static final float PAD = 3.0f;
    private static final float TEXT_SIZE = 5.5f;
    private final MultiBooleanSetting backend;
    private boolean open;
    private final Decelerate dropAnim;
    private final List<BooleanSetting> entries = new ArrayList<BooleanSetting>();

    public LvMultiSelectSetting(MultiBooleanSetting multiBooleanSetting) {
        this.backend = multiBooleanSetting;
        this.entries.addAll(multiBooleanSetting.getValues());
        this.dropAnim = new Decelerate();
        this.dropAnim.setMs(200);
        this.dropAnim.setValue(1.0);
        this.dropAnim.setDirection(Direction.BACKWARDS);
        this.dropAnim.counter.setTime(System.currentTimeMillis() - 10000L);
    }

    @Override
    public String name() {
        return this.backend.getName();
    }

    private static int clamp(int n, int n2, int n3) {
        return n < n2 ? n2 : (n > n3 ? n3 : n);
    }

    @Override
    public float height() {
        return 16.0f;
    }

    @Override
    public void renderOverlay(class_332 drawContext, float f, float f2, float f3, float f4) {
        float f9 = this.dropAnim.getOutput().floatValue();
        if (!(f9 <= 0.01f)) {
            float f10 = f9 * f4;
            List<BooleanSetting> list = this.entries;
            float f11 = this.dropWidth();
            String string = this.label();
            float f12 = FontsLV.MONTSERRAT_MEDIUM.width(string, 6.0f) + 10.0f;
            float f13 = f + f3 - f12 - 4.0f + f12 - f11;
            float f14 = f2 + 16.0f + 1.0f;
            float f15 = (float)list.size() * 12.0f + 6.0f;
            LvRenderHelper.drawDropBackground(f13, f14, f11, f15, f10);
            for (int i = 0; i < list.size(); ++i) {
                BooleanSetting booleanSetting = list.get(i);
                float f16 = f14 + 3.0f + (float)i * 12.0f;
                String string2 = booleanSetting.getName();
                boolean bl = booleanSetting.getValue();
                int n = bl ? ThemeManager.accentSoft(230.0f * f10) : new Color(255, 255, 255, (int)(140.0f * f10)).getRGB();
                FontsLV.MONTSERRAT_MEDIUM.draw(string2, f13 + 6.0f, f16 + 2.5f, 5.5f, n);
                if (!bl) continue;
                AccentGradient.fillVertical(f13 + f11 - 8.0f, f16 + 6.0f - 1.0f, 2.0f, 2.0f, 1.0f, 200.0f * f10);
            }
        }
    }

    @Override
    public void render(float f, float f2, float f3, float f4) {
        String string = this.label();
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
        String string = this.label();
        float f6 = FontsLV.MONTSERRAT_MEDIUM.width(string, 6.0f) + 10.0f;
        float f7 = f + f3 - f6 - 4.0f;
        float f8 = f2 + 2.0f;
        if (f4 >= f7 && f4 <= f7 + f6 && f5 >= f8 && f5 <= f8 + 12.0f) {
            this.open = !this.open;
            GuiSounds.expand(this.open);
            this.dropAnim.setDirection(this.open ? Direction.FORWARDS : Direction.BACKWARDS);
            this.dropAnim.counter.resetCounter();
            return true;
        }
        return false;
    }

    private String label() {
        return this.selectedCount() + " of " + this.entries.size();
    }

    private int selectedCount() {
        int n = 0;
        for (BooleanSetting booleanSetting : this.entries) {
            if (!booleanSetting.getValue().booleanValue()) continue;
            ++n;
        }
        return n;
    }

    private float dropWidth() {
        float f = 0.0f;
        for (BooleanSetting booleanSetting : this.entries) {
            f = Math.max(f, FontsLV.MONTSERRAT_MEDIUM.width(booleanSetting.getName(), 5.5f));
        }
        return f + 20.0f;
    }

    @Override
    public void closeOverlay() {
        if (this.open) {
            this.open = false;
            GuiSounds.expand(false);
            this.dropAnim.setDirection(Direction.BACKWARDS);
            this.dropAnim.counter.resetCounter();
        }
    }

    @Override
    public boolean isOverlayOpen() {
        return this.open;
    }

    @Override
    public boolean clickOverlay(float f, float f2, float f3, float f4, float f5) {
        float f6 = this.dropAnim.getOutput().floatValue();
        if (!this.open || f6 <= 0.1f) {
            return false;
        }
        List<BooleanSetting> list = this.entries;
        float f7 = this.dropWidth();
        String string = this.label();
        float f8 = FontsLV.MONTSERRAT_MEDIUM.width(string, 6.0f) + 10.0f;
        float f9 = f + f3 - f8 - 4.0f + f8 - f7;
        float f10 = f2 + 16.0f + 1.0f;
        float f11 = (float)list.size() * 12.0f + 6.0f;
        if (f4 < f9 || f4 > f9 + f7 || f5 < f10 || f5 > f10 + f11) {
            return false;
        }
        for (int i = 0; i < list.size(); ++i) {
            BooleanSetting booleanSetting;
            float f12 = f10 + 3.0f + (float)i * 12.0f;
            if (!(f5 >= f12) || !(f5 <= f12 + 12.0f)) continue;
            booleanSetting.set((booleanSetting = list.get(i)).getValue() == false);
            GuiSounds.expand(true);
            return true;
        }
        return true;
    }

    @Override
    public boolean hasOverlay() {
        return this.dropAnim.getOutput().floatValue() > 0.01f;
    }

    @Override
    public float preferredWidth() {
        return 6.0f + FontsLV.MONTSERRAT_MEDIUM.width(this.backend.getName(), 6.5f) + 6.0f + FontsLV.MONTSERRAT_MEDIUM.width(this.label(), 6.0f) + 10.0f + 8.0f;
    }
}

