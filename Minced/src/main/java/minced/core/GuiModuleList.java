/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.util.math.MathHelper
 *  sLM.4ZXYEo
 */
package minced.core;

import java.util.Comparator;
import java.util.List;
import minced.core.ColorUtil;
import minced.core.BindSetting;
import minced.core.ColorSetting;
import minced.core.MathUtil;
import minced.core.MultiBooleanSetting;
import minced.core.BooleanSetting;
import minced.core.SliderSetting;
import minced.core.Setting;
import minced.core.ColorListSetting;
import minced.core.ModeSetting;
import minced.core.ScissorUtil;
import minced.core.SettingRenderer;
import minced.core.SettingRendererBoolean;
import minced.core.SettingRendererBind;
import minced.core.SettingRendererSlider;
import minced.core.SettingRendererMode;
import minced.core.SettingRendererMulti;
import minced.core.SettingRendererColor;
import minced.core.SettingRendererGroup;
import minced.core.BindPopup;
import minced.core.ColorPickerElement;
import minced.core.BindPopupState;
import minced.core.Fonts;
import minced.core.RenderUtil;
import minced.core.Minced;
import minced.core.Module;
import minced.core.RecordClass234;
import minced.enums.KeyCode;
import minced.enums.Combat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.MathHelper;
import sLM._4ZXYEo;

public class GuiModuleList
implements SettingRenderer {
    @Override
    public float method2935(Setting p0) { return 0.0f; }

    @Override
    public float method2938(DrawContext p0, Setting p1, float p2, float p3, float p4, float p5, int p6, Object p7) { return 0.0f; }

    @Override
    public RecordClass234 method2939(Setting p0, float p1, float p2, float p3, float p4, float p5, int p6, SliderSetting p7, Object p8) { return null; }

    static float cfr_renamed_246;
    static String cfr_renamed_247;

    SettingRendererColor field10086;
    static float field10087;
    float field10088 = field10087;
    static float field10089;
    float field10090 = field10089;
    SettingRendererBoolean field10091 = new SettingRendererBoolean();
    SettingRendererBind field10092 = new SettingRendererBind();
    SettingRendererGroup field10093 = new SettingRendererGroup();
    SettingRendererSlider field10094 = new SettingRendererSlider();
    SettingRendererMulti field10095 = new SettingRendererMulti(this.field10094);
    SettingRendererMode field10096 = new SettingRendererMode();
    static float field10097;
    static float field10098;
    static float field10099;
    static float field10100;
    static float field10101;
    static float field10102;
    static float field10103;
    static float field10104;
    static float field10105;
    static float field10106;
    static float field10107;
    static float field10108;
    static float field10109;
    float field10110;
    static float field10111;
    static float field10112;
    static float field10113;
    static float field10114;
    static double field10115;
    static double field10116;
    static float field10117;
    static float field10118;
    static float field10119;
    static float field10120;
    static float field10121;
    static float field10122;
    static float field10123;
    static float field10124;
    static float field10125;
    static float field10126;
    static float field10127;
    static float field10128;
    static float field10129;
    static float field10130;
    static float field10131;
    static float field10132;
    static float field10133;
    static float field10134;
    static float field10135;
    static float field10136;
    static float field10137;
    static float field10138;
    static String field10139;
    static float field10140;
    static float field10141;
    static float field10142;
    static float field10143;
    static float field10144;
    static float field10145;
    static float field10146;

    public void method2927() {
        this.field10086.method2981();
    }

    public GuiModuleList() {
        this.field10086 = new SettingRendererColor();
    }

    public void method2928() {
        this.field10088 = field10097;
        this.field10090 = field10098;
    }

    private float method2929(DrawContext param1, Setting param2, float param3, float param4, float param5, float param6, int param7, Object param8) {
        if (param2 instanceof BooleanSetting) {
            BooleanSetting v9 = (BooleanSetting)param2;
            return this.field10091.method2941(param1, v9, param3, param4, param5, param6, param7, param8);
        }
        if (param2 instanceof BindSetting) {
            BindSetting v9 = (BindSetting)param2;
            return this.field10092.cfr_renamed_0(param1, v9, param3, param4, param5, param6, param7, param8);
        }
        if (param2 instanceof SliderSetting) {
            SliderSetting v9 = (SliderSetting)param2;
            return this.field10093.method2985(param1, v9, param3, param4, param5, param6, param7, param8);
        }
        if (param2 instanceof ModeSetting) {
            ModeSetting v9 = (ModeSetting)param2;
            return this.field10094.method2952(param1, v9, param3, param4, param5, param6, param7, param8);
        }
        if (param2 instanceof MultiBooleanSetting) {
            MultiBooleanSetting v9 = (MultiBooleanSetting)param2;
            return this.field10095.method2968(param1, v9, param3, param4, param5, param6, param7, param8);
        }
        if (param2 instanceof ColorSetting) {
            ColorSetting v9 = (ColorSetting)param2;
            return this.field10096.method2955(param1, v9, param3, param4, param5, param6, param7, param8);
        }
        if (param2 instanceof ColorListSetting) {
            ColorListSetting v9 = (ColorListSetting)param2;
            return this.field10086.method2976(param1, v9, param3, param4, param5, param6, param7, param8);
        }
        this.field10091.method2937(param1, param2.getName(), param4, param5, 0.0f, param6);
        return field10099;
    }

    public RecordClass234 cfr_renamed_4(float param1, float param2, int param3, int param4, int param5, Combat param6, String param7, SliderSetting param8, Object param9) {
        if (!MathUtil.cfr_underscore(param1, param2, param4 + 104, param5 + 32, field10100, field10101)) {
            return RecordClass234.empty();
        }
        List v10 = this.cfr_renamed_4(param6, param7);
        int v11 = param4 + 114;
        int v12 = param5 + 40;
        float v13 = 0.0f;
        float v14 = 0.0f;
        for (int v15 = 0; v15 < v10.size(); ++v15) {
            Module v16 = (Module)v10.get(v15);
            List v17 = this.cfr_renamed_4(v16);
            float v18 = this.cfr_renamed_4(v17);
            boolean v19 = v15 != v10.size() - 1 && v14 < v13;
            int v20 = (int)((float)v11 + (v19 ? field10102 : 0.0f));
            int v21 = (int)((float)(v12 - 5) + this.field10090 + (v19 ? v14 : v13));
            int v22 = v20 + 8;
            int v23 = v21 + 8;
            if (MathUtil.cfr_underscore(param1, param2, v20, v21, field10103, field10104)) {
                if (param3 == 0) {
                    v16.toggle();
                    return RecordClass234.consumed();
                }
                if (param3 == 2) {
                    return RecordClass234.withBinding(v16);
                }
                return RecordClass234.empty();
            }
            float v24 = 0.0f;
            for (Setting v26 : (Iterable<Setting>)(v17)) {
                float v27;
                RecordClass234 v28 = this.method2930(v26, v20, v22, v27 = (float)(v23 + 20) + v24, param1, param2, param3, param8, param9);
                if (v28.handled() || v28.clearBindingRequested() || v28.newBinding() != null || v28.newSlider() != null) {
                    return v28;
                }
                v24 += this.method2934(v26);
            }
            if (v19) {
                v14 += v18 + field10105;
                continue;
            }
            v13 += v18 + field10106;
        }
        return RecordClass234.empty();
    }

    private RecordClass234 method2930(Setting param1, float param2, float param3, float param4, float param5, float param6, int param7, SliderSetting param8, Object param9) {
        if (param1 instanceof BooleanSetting) {
            BooleanSetting v10 = (BooleanSetting)param1;
            return this.field10091.method2940(v10, param2, param3, param4, param5, param6, param7, param8, param9);
        }
        if (param1 instanceof BindSetting) {
            BindSetting v10 = (BindSetting)param1;
            return this.field10092.cfr_renamed_0(v10, param2, param3, param4, param5, param6, param7, param8, param9);
        }
        if (param1 instanceof SliderSetting) {
            SliderSetting v10 = (SliderSetting)param1;
            return this.field10093.method2986(v10, param2, param3, param4, param5, param6, param7, param8, param9);
        }
        if (param1 instanceof ModeSetting) {
            ModeSetting v10 = (ModeSetting)param1;
            return this.field10094.method2944(v10, param2, param3, param4, param5, param6, param7, param8, param9);
        }
        if (param1 instanceof MultiBooleanSetting) {
            MultiBooleanSetting v10 = (MultiBooleanSetting)param1;
            return this.field10095.method2972(v10, param2, param3, param4, param5, param6, param7, param8, param9);
        }
        if (param1 instanceof ColorSetting) {
            ColorSetting v10 = (ColorSetting)param1;
            return this.field10096.method2954(v10, param2, param3, param4, param5, param6, param7, param8, param9);
        }
        if (param1 instanceof ColorListSetting) {
            ColorListSetting v10 = (ColorListSetting)param1;
            return this.field10086.method2978(v10, param2, param3, param4, param5, param6, param7, param8, param9);
        }
        return RecordClass234.empty();
    }

    private List cfr_renamed_4(Module param1) {
        return param1.getSettings().stream().filter(param0 -> (Boolean)((Setting)param0).isVisible()).toList();
    }

    public boolean method2931(float param1, float param2, double param3, double param5, int param7, int param8) {
        if (!MathUtil.cfr_underscore(param1, param2, param7 + 104, param8 + 32, field10107, field10108)) {
            return false;
        }
        this.field10088 = Math.min(this.field10088 + (float)(param5 > 0.0 ? 16 : -16), field10109);
        ColorPickerElement.method3219();
        BindPopup.method3169();
        BindPopupState.method3274();
        return true;
    }

    public void cfr_renamed_4(DrawContext param1, int param2, int param3, float param4, Combat param5, String param6, Object param7) {
        List v8 = this.cfr_renamed_4(param5, param6);
        float v9 = param2 + 114;
        float v10 = param3 + 40;
        float v11 = 0.0f;
        float v12 = 0.0f;
        if (this.field10088 != this.field10090) {
            MinecraftClient v13 = MinecraftClient.getInstance();
            this.field10088 = MathHelper.clamp((float)this.field10088, (float)Math.min(-(v10 + this.field10110 + this.field10090 - this.field10090 - ((float)v13.getWindow().getScaledHeight() / 2.0f + field10111) - 1.0f), 0.0f), (float)field10112);
            this.field10090 = MathUtil.method1870(this.field10090, this.field10088, field10113);
        }
        ScissorUtil.method2621(v9, v10 - field10114, field10115, field10116);
        for (int v13 = 0; v13 < v8.size(); ++v13) {
            Module v14 = (Module)v8.get(v13);
            List v15 = this.cfr_renamed_4(v14);
            float v16 = this.cfr_renamed_4(v15);
            boolean v17 = v13 != v8.size() - 1 && v12 < v11;
            float v18 = v9 + (v17 ? field10117 : 0.0f);
            float v19 = v10 - field10118 + this.field10090 + (v17 ? v12 : v11);
            if (v19 + v16 > v10 - field10119 && v19 < v10 + field10120 + field10121) {
                this.cfr_renamed_4(param1, v14, v15, v18, v19, v16, param4, param7);
            }
            if (v17) {
                v12 += v16 + field10122;
                continue;
            }
            v11 += v16 + field10123;
        }
        this.field10110 = Math.max(v11, v12);
        ScissorUtil.method2620();
    }

    private List cfr_renamed_4(Combat param1, String param22) {
        String v3 = param22.toLowerCase();
        return Minced.getInstance().getModuleManager().method178().stream().filter(param2 -> v3.isEmpty() ? param2.getCategory() == param1 : param2.getName().toLowerCase().contains(v3)).sorted(Comparator.comparing(Module::getName)).toList();
    }

    public boolean method2932(int param1, int param2) {
        return this.field10086.method2979(param1, param2);
    }

    private float cfr_renamed_4(List param1) {
        if (param1.isEmpty()) {
            return field10124;
        }
        float v2 = field10125;
        for (Setting v4 : (Iterable<Setting>)(param1)) {
            v2 += this.method2934(v4);
        }
        return v2;
    }

    private void cfr_renamed_4(DrawContext param1, Module param2, List param3, float param4, float param5, float param6, float param7, Object param8) {
        boolean v9 = param2.isEnabled();
        param2.getToggleAnimation().setTarget(v9 ? 1.0f : 0.0f);
        float v10 = param2.getToggleAnimation().getValue() * param7;
        int v11 = Minced.getInstance().getThemeManager().method279();
        int v12 = ColorUtil.method1853(ColorUtil.method1845(v11, field10126 * param7), param7);
        int v13 = ColorUtil.method1853(v11, v10);
        int v14 = (int)(param4 + field10127);
        int v15 = (int)(param5 + field10128);
        float v16 = (v9 ? field10129 : field10130) * param7;
        int v17 = ColorUtil.method1853(v9 ? v11 : -1, v16);
        RenderUtil.method390(param4, param5, field10131, param6 + 1.0f, field10132, v17, v16);
        if (v9) {
            RenderUtil.method397(param4, v15 - 3, field10133, field10134, field10135, v13);
        }
        int v18 = ColorUtil.method1853(ColorUtil.method1845(-1, field10136 + v10 * field10137), param7);
        Fonts.I[14].method2711(param1, param2.getName(), v14, (float)v15 + field10138, v18);
        if (param8 == param2) {
            String v19 = param2.getKey() == -100 ? field10139 : _4ZXYEo.ЦХ("makeConcatWithConstants", new Object[]{}, (String)KeyCode.getDisplayName(param2.getKey()));
            float v20 = Fonts.I[11].method2719(v19);
            float v21 = (float)v14 + field10140 - field10141 - v20;
            int v22 = ColorUtil.method1853(v11, param7);
            Fonts.I[11].method2711(param1, v19, v21, (float)v15 + 2.0f, v22);
        }
        if (!param3.isEmpty()) {
            RenderUtil.method394(param1, v14, v15 + 12, field10142, field10143, ColorUtil.method1853(-1, (field10144 + v10 * field10145) * param7));
        }
        float v19 = 0.0f;
        for (Setting v21 : (Iterable<Setting>)(param3)) {
            float v22 = (float)(v15 + 20) + v19;
            v19 += this.method2929(param1, v21, param4, v14, v22, param7, v12, param8);
        }
    }

    public boolean method2933(char param1, int param2) {
        return this.field10086.method2980(param1, param2);
    }

    private float method2934(Setting param1) {
        if (param1 instanceof SliderSetting) {
            SliderSetting v2 = (SliderSetting)param1;
            return this.field10093.method2984(v2);
        }
        if (param1 instanceof ModeSetting) {
            ModeSetting v2 = (ModeSetting)param1;
            return this.field10094.method2951(v2);
        }
        if (param1 instanceof MultiBooleanSetting) {
            MultiBooleanSetting v2 = (MultiBooleanSetting)param1;
            return this.field10095.method2973(v2);
        }
        if (param1 instanceof BooleanSetting) {
            BooleanSetting v2 = (BooleanSetting)param1;
            return this.field10091.method2942(v2);
        }
        if (param1 instanceof BindSetting) {
            BindSetting v2 = (BindSetting)param1;
            return this.field10092.cfr_renamed_0(v2);
        }
        if (param1 instanceof ColorSetting) {
            ColorSetting v2 = (ColorSetting)param1;
            return this.field10096.method2953(v2);
        }
        if (param1 instanceof ColorListSetting) {
            ColorListSetting v2 = (ColorListSetting)param1;
            return this.field10086.method2983(v2);
        }
        return field10146;
    }

    static {
        cfr_renamed_246 = 0.0f;
        field10087 = 5.0f;
        field10089 = 5.0f;
        field10111 = 136.25f;
        field10112 = 5.0f;
        field10113 = 14.0f;
        field10114 = 7.0f;
        field10115 = 448.5;
        field10116 = 239.5;
        field10117 = 164.5f;
        field10118 = 5.0f;
        field10119 = 80.0f;
        field10120 = 272.5f;
        field10121 = 20.0f;
        field10122 = 5.0f;
        field10123 = 5.0f;
        field10100 = 344.5f;
        field10101 = 240.5f;
        field10102 = 164.5f;
        field10103 = 159.5f;
        field10104 = 28.0f;
        field10105 = 5.0f;
        field10106 = 5.0f;
        field10107 = 344.5f;
        field10108 = 240.5f;
        field10109 = 5.0f;
        field10097 = 5.0f;
        field10098 = 5.0f;
        field10126 = 0.6f;
        field10127 = 8.0f;
        field10128 = 8.0f;
        field10129 = 0.04f;
        field10130 = 0.02f;
        field10131 = 159.5f;
        field10132 = 4.0f;
        field10133 = 0.5f;
        field10134 = 10.0f;
        field10135 = 0.25f;
        field10136 = 0.45f;
        field10137 = 0.45f;
        field10138 = 0.5f;
        field10139 = "Binding...";
        cfr_renamed_247 = "Bind - \u0001, Rebind...";
        field10140 = 159.5f;
        field10141 = 16.0f;
        field10142 = 144.0f;
        field10143 = 0.5f;
        field10144 = 0.05f;
        field10145 = 0.05f;
        field10099 = 11.0f;
        field10124 = 19.0f;
        field10125 = 27.0f;
        field10146 = 11.0f;
    }
}



