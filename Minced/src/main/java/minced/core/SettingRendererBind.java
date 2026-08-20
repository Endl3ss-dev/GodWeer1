/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.DrawContext
 *  sLM.4ZXYEo
 */
package minced.core;

import minced.core.ColorUtil;
import minced.core.BindSetting;
import minced.core.MathUtil;
import minced.core.SliderSetting;
import minced.core.SettingRenderer;
import minced.core.Fonts;
import minced.core.RecordClass234;
import minced.enums.KeyCode;
import net.minecraft.client.gui.DrawContext;
import sLM._4ZXYEo;

public class SettingRendererBind
implements SettingRenderer {
    @Override
    public float method2935(Setting p0) { return 0.0f; }

    @Override
    public float method2938(DrawContext p0, Setting p1, float p2, float p3, float p4, float p5, int p6, Object p7) { return 0.0f; }

    @Override
    public RecordClass234 method2939(Setting p0, float p1, float p2, float p3, float p4, float p5, int p6, SliderSetting p7, Object p8) { return null; }

    static float Ж;
    static String cfr_renamed_248;

    static String field10165;
    static String field10166;
    static String field10167;
    static float field10168;
    static float field10169;
    static float field10170;
    static float field10171;
    static float field10172;
    static float field10173;
    static float field10174;
    static float field10175;
    static float field10176;
    static float field10177;
    static float field10178;
    static float field10179;

    private String cfr_renamed_0(BindSetting param1, Object param2) {
        if (param2 != param1) {
            return (Integer)param1.getValue() != -1 ? KeyCode.getDisplayName((Integer)param1.getValue()) : field10165;
        }
        if ((Integer)param1.getValue() == -1) {
            return field10166;
        }
        return _4ZXYEo.фд("makeConcatWithConstants", new Object[]{}, (String)KeyCode.getDisplayName((Integer)param1.getValue()));
    }

    private String method2943(int param1) {
        return param1 == -1 || param1 == -100 ? field10167 : KeyCode.getDisplayName(param1);
    }

    public float cfr_renamed_0(BindSetting param1) {
        return field10168;
    }

    public RecordClass234 cfr_renamed_0(BindSetting param1, float param2, float param3, float param4, float param5, float param6, int param7, SliderSetting param8, Object param9) {
        if (param7 == 0 && MathUtil.cfr_underscore(param5, param6, param3 - field10169, param4 - 2.0f, field10170, field10171)) {
            return RecordClass234.withBinding(param1);
        }
        if (param7 == 2 && MathUtil.cfr_underscore(param5, param6, param3 - field10172, param4 - 2.0f, field10173, field10174)) {
            return RecordClass234.withBinding(param1);
        }
        if (param7 != 0 && param9 == param1) {
            param1.setValue(param7);
            return RecordClass234.clearBinding();
        }
        return RecordClass234.empty();
    }

    public float cfr_renamed_0(DrawContext param1, BindSetting param2, float param3, float param4, float param5, float param6, int param7, Object param8) {
        String v9 = this.cfr_renamed_0(param2, param8);
        float v10 = Fonts.I[12].method2719(v9);
        float v11 = this.method2936(param4, v10, field10175) + v10 / 2.0f + field10176 - Fonts.I[12].method2719(v9) / 2.0f;
        this.method2937(param1, param2.getName(), param4, param5, v10, param6);
        int v12 = (Integer)param2.getValue() != -1 ? param7 : ColorUtil.method1853(-1, field10177 * param6);
        Fonts.I[12].method2711(param1, v9, v11, param5 + field10178, v12);
        return field10179;
    }

    static {
        Ж = 0.0f;
        field10175 = 159.5f;
        field10176 = 0.5f;
        field10177 = 0.48f;
        field10178 = 0.5f;
        field10179 = 11.0f;
        field10169 = 8.0f;
        field10170 = 159.5f;
        field10171 = 7.0f;
        field10172 = 8.0f;
        field10173 = 159.5f;
        field10174 = 7.0f;
        field10168 = 11.0f;
        field10165 = "n/a";
        field10166 = "Binding...";
        cfr_renamed_248 = "Bind - \u0001, Rebind...";
        field10167 = "n/a";
    }
}



