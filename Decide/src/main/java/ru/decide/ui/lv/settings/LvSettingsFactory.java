/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.ui.lv.settings;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.module.api.Module;
import ru.decide.module.api.settings.Setting;
import ru.decide.module.api.settings.impl.BindSetting;
import ru.decide.module.api.settings.impl.BooleanSetting;
import ru.decide.module.api.settings.impl.BooleanSettingHud;
import ru.decide.module.api.settings.impl.ButtonSetting;
import ru.decide.module.api.settings.impl.ColorSetting;
import ru.decide.module.api.settings.impl.DelimiterSetting;
import ru.decide.module.api.settings.impl.DragSetting;
import ru.decide.module.api.settings.impl.ListSetting;
import ru.decide.module.api.settings.impl.ModeSetting;
import ru.decide.module.api.settings.impl.ModeSettingHud;
import ru.decide.module.api.settings.impl.MultiBooleanSetting;
import ru.decide.module.api.settings.impl.PixelGridSetting;
import ru.decide.module.api.settings.impl.SliderSetting;
import ru.decide.module.api.settings.impl.StringSetting;
import ru.decide.ui.fonts.FontsLV;
import ru.decide.ui.lv.settings.LvBindSetting;
import ru.decide.ui.lv.settings.LvBoolSetting;
import ru.decide.ui.lv.settings.LvButtonRowSetting;
import ru.decide.ui.lv.settings.LvColorSetting;
import ru.decide.ui.lv.settings.LvDragSetting;
import ru.decide.ui.lv.settings.LvMultiSelectSetting;
import ru.decide.ui.lv.settings.LvPixelGridSetting;
import ru.decide.ui.lv.settings.LvRenderHelper;
import ru.decide.ui.lv.settings.LvSelectSetting;
import ru.decide.ui.lv.settings.LvSeparatorSetting;
import ru.decide.ui.lv.settings.LvSliderSetting;
import ru.decide.ui.lv.settings.LvTextSetting;
import ru.decide.ui.lv.settings.SettingWidget;
import ru.decide.ui.theme.ThemeManager;
import ru.decide.utils.render.Draw;

@Environment(value=EnvType.CLIENT)
public final class LvSettingsFactory {
    private LvSettingsFactory() {
    }

    public static SettingWidget create(Setting<?> setting) {
        if (setting instanceof SliderSetting) {
            SliderSetting sliderSetting = (SliderSetting)setting;
            return new LvSliderSetting(sliderSetting);
        }
        if (setting instanceof BooleanSetting) {
            BooleanSetting booleanSetting = (BooleanSetting)setting;
            return new LvBoolSetting(booleanSetting);
        }
        if (setting instanceof ModeSetting) {
            ModeSetting modeSetting = (ModeSetting)setting;
            return new LvSelectSetting(modeSetting);
        }
        if (setting instanceof MultiBooleanSetting) {
            MultiBooleanSetting multiBooleanSetting = (MultiBooleanSetting)setting;
            return new LvMultiSelectSetting(multiBooleanSetting);
        }
        if (setting instanceof ColorSetting) {
            ColorSetting colorSetting = (ColorSetting)setting;
            return new LvColorSetting(colorSetting);
        }
        if (setting instanceof BindSetting) {
            BindSetting bindSetting = (BindSetting)setting;
            return new LvBindSetting(bindSetting);
        }
        if (setting instanceof StringSetting) {
            StringSetting stringSetting = (StringSetting)setting;
            return new LvTextSetting(stringSetting);
        }
        if (setting instanceof DelimiterSetting) {
            DelimiterSetting delimiterSetting = (DelimiterSetting)setting;
            return new LvSeparatorSetting(delimiterSetting);
        }
        if (setting instanceof ButtonSetting) {
            ButtonSetting buttonSetting = (ButtonSetting)setting;
            return new LvButtonRowSetting(buttonSetting);
        }
        if (setting instanceof DragSetting) {
            DragSetting dragSetting = (DragSetting)setting;
            return new LvDragSetting(dragSetting);
        }
        if (setting instanceof PixelGridSetting) {
            PixelGridSetting pixelGridSetting = (PixelGridSetting)setting;
            return new LvPixelGridSetting(pixelGridSetting);
        }
        if (setting instanceof BooleanSettingHud) {
            BooleanSettingHud booleanSettingHud = (BooleanSettingHud)setting;
            return new LvBoolHudRow(booleanSettingHud);
        }
        if (setting instanceof ModeSettingHud) {
            ModeSettingHud modeSettingHud = (ModeSettingHud)setting;
            return new LvSelectHudRow(modeSettingHud);
        }
        if (setting instanceof ListSetting) {
            ListSetting listSetting = (ListSetting)setting;
            return new LvListRowSetting(listSetting);
        }
        return null;
    }

    public static List<SettingWidget> build(Module module) {
        LvTextSetting.unfocusAll();
        ArrayList<SettingWidget> list = new ArrayList<SettingWidget>();
        for (Setting<?> setting : module.getSettings()) {
            SettingWidget settingWidget = LvSettingsFactory.create(setting);
            if (settingWidget == null) continue;
            list.add(settingWidget);
        }
        return list;
    }

    @Environment(value=EnvType.CLIENT)
    private static final class LvBoolHudRow
    extends SettingWidget {
        private final BooleanSettingHud backend;

        private LvBoolHudRow(BooleanSettingHud booleanSettingHud) {
            this.backend = booleanSettingHud;
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
        public void render(float f, float f2, float f3, float f4) {
            String string = this.backend.getValue() != false ? "\u0412\u043a\u043b" : "\u0412\u044b\u043a\u043b";
            float f5 = FontsLV.MONTSERRAT_MEDIUM.width(string, 6.0f) + 10.0f;
            float f6 = f + f3 - f5 - 4.0f;
            float f7 = f2 + 2.0f;
            LvRenderHelper.drawName(this.backend.getName(), f, f2, f6 - (f + 6.0f) - 4.0f, f4);
            Draw.rect(f6, f7, f5, 12.0f, this.backend.getValue() != false ? ThemeManager.accentSoft(160.0f * f4) : ThemeManager.accentSoft(50.0f * f4), 3.0f);
            FontsLV.MONTSERRAT_MEDIUM.draw(string, f6 + 5.0f, f7 + 3.25f, 6.0f, ThemeManager.accentSoft(220.0f * f4));
        }

        @Override
        public boolean isVisible() {
            return this.backend.getVisible().get();
        }

        @Override
        public boolean click(float f, float f2, float f3, float f4, float f5) {
            this.backend.set(this.backend.getValue() == false);
            return true;
        }

        @Override
        public float preferredWidth() {
            return 6.0f + FontsLV.MONTSERRAT_MEDIUM.width(this.backend.getName(), 6.5f) + 6.0f + 32.0f + 8.0f;
        }
    }

    @Environment(value=EnvType.CLIENT)
    private static final class LvSelectHudRow
    extends SettingWidget {
        private final ModeSettingHud backend;

        private LvSelectHudRow(ModeSettingHud modeSettingHud) {
            this.backend = modeSettingHud;
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
        public void render(float f, float f2, float f3, float f4) {
            String string = String.valueOf(this.backend.getValue());
            float f5 = FontsLV.MONTSERRAT_MEDIUM.width(string, 6.0f) + 10.0f;
            float f6 = f + f3 - f5 - 4.0f;
            float f7 = f2 + 2.0f;
            LvRenderHelper.drawName(this.backend.getName(), f, f2, f6 - (f + 6.0f) - 4.0f, f4);
            Draw.rect(f6, f7, f5, 12.0f, ThemeManager.accentSoft(50.0f * f4), 3.0f);
            FontsLV.MONTSERRAT_MEDIUM.draw(string, f6 + 5.0f, f7 + 3.25f, 6.0f, ThemeManager.accentSoft(220.0f * f4));
        }

        @Override
        public boolean isVisible() {
            return this.backend.getVisible().get();
        }

        @Override
        public boolean click(float f, float f2, float f3, float f4, float f5) {
            int index = (this.backend.getIndex() + 1) % this.backend.values.size();
            this.backend.set(this.backend.values.get(index));
            return true;
        }

        @Override
        public float preferredWidth() {
            return 6.0f + FontsLV.MONTSERRAT_MEDIUM.width(this.backend.getName(), 6.5f) + 6.0f + FontsLV.MONTSERRAT_MEDIUM.width(String.valueOf(this.backend.getValue()), 6.0f) + 10.0f + 8.0f;
        }
    }

    @Environment(value=EnvType.CLIENT)
    private static final class LvListRowSetting
    extends SettingWidget {
        private final ListSetting<?> backend;

        private LvListRowSetting(ListSetting<?> listSetting) {
            this.backend = listSetting;
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
        public void render(float f, float f2, float f3, float f4) {
            String string = String.valueOf(this.backend.getValue());
            float f5 = FontsLV.MONTSERRAT_MEDIUM.width(string, 6.0f) + 10.0f;
            float f6 = f + f3 - f5 - 4.0f;
            float f7 = f2 + 2.0f;
            LvRenderHelper.drawName(this.backend.getName(), f, f2, f6 - (f + 6.0f) - 4.0f, f4);
            Draw.rect(f6, f7, f5, 12.0f, ThemeManager.accentSoft(50.0f * f4), 3.0f);
            FontsLV.MONTSERRAT_MEDIUM.draw(string, f6 + 5.0f, f7 + 3.25f, 6.0f, ThemeManager.accentSoft(220.0f * f4));
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
            return 6.0f + FontsLV.MONTSERRAT_MEDIUM.width(this.backend.getName(), 6.5f) + 6.0f + FontsLV.MONTSERRAT_MEDIUM.width(String.valueOf(this.backend.getValue()), 6.0f) + 10.0f + 8.0f;
        }
    }
}

