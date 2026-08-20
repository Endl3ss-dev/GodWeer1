/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.DrawContext
 */
package minced.core;

import minced.core.ColorUtil;
import minced.core.MathUtil;
import minced.core.BooleanSetting;
import minced.core.SliderSetting;
import minced.core.SettingRenderer;
import minced.core.RenderUtil;
import minced.core.RecordClass234;
import net.minecraft.client.gui.DrawContext;

public class SettingRendererBoolean
implements SettingRenderer {
    @Override
    public float method2935(Setting p0) { return 0.0f; }

    @Override
    public float method2938(DrawContext p0, Setting p1, float p2, float p3, float p4, float p5, int p6, Object p7) { return 0.0f; }

    @Override
    public RecordClass234 method2939(Setting p0, float p1, float p2, float p3, float p4, float p5, int p6, SliderSetting p7, Object p8) { return null; }

    static float cfr_renamed_216;

    static float field10147;
    static float field10148;
    static float field10149;
    static float field10150;
    static float field10151;
    static float field10152;
    static float field10153;
    static float field10154;
    static float field10155;
    static float field10156;
    static float field10157;
    static float field10158;
    static float field10159;
    static float field10160;
    static float field10161;
    static float field10162;
    static float field10163;
    static float field10164;

    public RecordClass234 method2940(BooleanSetting param1, float param2, float param3, float param4, float param5, float param6, int param7, SliderSetting param8, Object param9) {
        if (param7 == 0 && MathUtil.cfr_underscore(param5, param6, param3 - field10147, param4 - field10148, field10149, field10150)) {
            param1.setValue((Boolean)param1.getValue() == false);
            return RecordClass234.consumed();
        }
        return RecordClass234.empty();
    }

    public float method2941(DrawContext param1, BooleanSetting param2, float param3, float param4, float param5, float param6, int param7, Object param8) {
        float v9 = field10151;
        this.method2937(param1, param2.getName(), param4, param5, v9, param6);
        float v10 = this.method2936(param4, v9, field10152);
        param2.field7741.animate((Boolean)param2.getValue() != false ? 1.0f : 0.0f);
        float v11 = param2.field7741.getAnimatedValue();
        int v12 = ColorUtil.method1850(ColorUtil.method1853(-1, field10153 * param6), param7, v11);
        RenderUtil.method397(v10 - 1.0f, param5 - field10154, field10155, field10156, field10157, v12);
        RenderUtil.method397(v10 + v11 * field10158, param5 - field10159, field10160, field10161, 2.0f, ColorUtil.method1853(-1, field10162 * param6));
        return field10163;
    }

    public float method2942(BooleanSetting param1) {
        return field10164;
    }

    static {
        cfr_renamed_216 = 0.0f;
        field10151 = 10.0f;
        field10152 = 159.5f;
        field10153 = 0.24f;
        field10154 = 2.5f;
        field10155 = 12.0f;
        field10156 = 8.0f;
        field10157 = 3.0f;
        field10158 = 4.0f;
        field10159 = 1.5f;
        field10160 = 6.0f;
        field10161 = 6.0f;
        field10162 = 0.72f;
        field10163 = 11.0f;
        field10147 = 8.0f;
        field10148 = 1.5f;
        field10149 = 159.5f;
        field10150 = 6.0f;
        field10164 = 11.0f;
    }
}


