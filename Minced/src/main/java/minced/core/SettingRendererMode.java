/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.DrawContext
 */
package minced.core;

import minced.core.ColorSetting;
import minced.core.SliderSetting;
import minced.core.SettingRenderer;
import minced.core.RenderUtil;
import minced.core.RecordClass234;
import net.minecraft.client.gui.DrawContext;

public class SettingRendererMode
implements SettingRenderer {
    @Override
    public float method2935(Setting p0) { return 0.0f; }

    @Override
    public float method2938(DrawContext p0, Setting p1, float p2, float p3, float p4, float p5, int p6, Object p7) { return 0.0f; }

    @Override
    public RecordClass234 method2939(Setting p0, float p1, float p2, float p3, float p4, float p5, int p6, SliderSetting p7, Object p8) { return null; }

    static float нЬ;

    static float field10219;
    static float field10220;
    static float field10221;
    static float field10222;
    static float field10223;
    static float field10224;

    public float method2953(ColorSetting param1) {
        return field10219;
    }

    public RecordClass234 method2954(ColorSetting param1, float param2, float param3, float param4, float param5, float param6, int param7, SliderSetting param8, Object param9) {
        return RecordClass234.empty();
    }

    public float method2955(DrawContext param1, ColorSetting param2, float param3, float param4, float param5, float param6, int param7, Object param8) {
        float v9 = field10220;
        this.method2937(param1, param2.getName(), param4, param5, v9, param6);
        RenderUtil.method397(this.method2936(param4, v9, field10221), param5 - 2.0f, field10222, field10223, 2.0f, (Integer)param2.getValue());
        return field10224;
    }

    static {
        нЬ = 0.0f;
        field10220 = 13.0f;
        field10221 = 159.5f;
        field10222 = 13.0f;
        field10223 = 7.0f;
        field10224 = 11.0f;
        field10219 = 11.0f;
    }
}



