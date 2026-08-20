/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.DrawContext
 */
package minced.core;

import java.util.Locale;
import minced.core.ColorUtil;
import minced.core.MathUtil;
import minced.core.SliderSetting;
import minced.core.SettingRenderer;
import minced.core.Fonts;
import minced.core.RenderUtil;
import minced.core.RecordClass234;
import net.minecraft.client.gui.DrawContext;

public class SettingRendererGroup
implements SettingRenderer {
    @Override
    public float method2935(Setting p0) { return 0.0f; }

    @Override
    public float method2938(DrawContext p0, Setting p1, float p2, float p3, float p4, float p5, int p6, Object p7) { return 0.0f; }

    @Override
    public RecordClass234 method2939(Setting p0, float p1, float p2, float p3, float p4, float p5, int p6, SliderSetting p7, Object p8) { return null; }

    static float cfr_renamed_168;
    static float cfr_underscore;

    static float field10356;
    static float field10357;
    static float field10358;
    static float field10359;
    static float field10360;
    static float field10361;
    static float field10362;
    static float field10363;
    static float field10364;
    static float field10365;
    static float field10366;
    static float field10367;
    static float field10368;
    static float field10369;
    static float field10370;
    static float field10371;
    static float field10372;
    static float field10373;
    static float field10374;
    static float field10375;
    static float field10376;
    static float field10377;
    static float field10378;
    static String field10379;

    public float method2984(SliderSetting param1) {
        return field10356;
    }

    public float method2985(DrawContext param1, SliderSetting param2, float param3, float param4, float param5, float param6, int param7, Object param8) {
        String v9 = this.method2987(((Float)param2.getValue()).floatValue());
        float v10 = Fonts.I[12].method2719(v9);
        float v11 = this.method2936(param4, v10, field10357);
        this.method2937(param1, param2.getName(), param4, param5, v10, param6);
        Fonts.I[12].method2711(param1, v9, v11, param5, ColorUtil.method1853(-1, param6));
        param2.field7747.animate(this.method2988(param2));
        float v12 = field10358 * param2.field7747.getAnimatedValue();
        RenderUtil.method397(param3 + field10359, param5 + field10360, field10361, field10362, 1.0f, ColorUtil.method1853(-1, field10363 * param6));
        RenderUtil.method397(param3 + field10364, param5 + field10365, v12 + 1.0f, field10366, 1.0f, param7);
        RenderUtil.method397(param3 + v12 + field10367, param5 + field10368, field10369, field10370, field10371, ColorUtil.method1853(-1, param6));
        return field10372;
    }

    public RecordClass234 method2986(SliderSetting param1, float param2, float param3, float param4, float param5, float param6, int param7, SliderSetting param8, Object param9) {
        if (param7 == 0 && MathUtil.cfr_underscore(param5, param6, param2 + field10373, param4 + field10374, field10375, field10376)) {
            return RecordClass234.withSlider(param1, param2 + field10377);
        }
        return RecordClass234.empty();
    }

    private String method2987(float param1) {
        if (Math.abs(param1 - (float)Math.round(param1)) < field10378) {
            return String.valueOf(Math.round(param1));
        }
        return String.format(Locale.US, field10379, Float.valueOf(param1));
    }

    private float method2988(SliderSetting param1) {
        return (((Float)param1.getValue()).floatValue() - param1.field7742) / (param1.field7743 - param1.field7742);
    }

    static {
        cfr_renamed_168 = 0.0f;
        cfr_underscore = 0.0f;
        field10357 = 159.5f;
        field10358 = 142.0f;
        field10359 = 7.5f;
        field10360 = 8.0f;
        field10361 = 145.0f;
        field10362 = 3.0f;
        field10363 = 0.04f;
        field10364 = 7.5f;
        field10365 = 8.0f;
        field10366 = 3.0f;
        field10367 = 7.0f;
        field10368 = 7.0f;
        field10369 = 5.0f;
        field10370 = 5.0f;
        field10371 = 1.5f;
        field10372 = 18.0f;
        field10373 = 7.0f;
        field10374 = 8.0f;
        field10375 = 145.0f;
        field10376 = 4.0f;
        field10377 = 7.5f;
        field10356 = 18.0f;
        field10378 = 0.001f;
        field10379 = "%.2f";
    }
}




