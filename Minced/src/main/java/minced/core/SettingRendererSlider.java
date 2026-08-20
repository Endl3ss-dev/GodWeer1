/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.DrawContext
 *  sLM.4ZXYEo
 */
package minced.core;

import java.lang.invoke.CallSite;
import java.util.Objects;
import minced.core.ColorUtil;
import minced.core.MathUtil;
import minced.core.SliderSetting;
import minced.core.SliderFormatter;
import minced.core.ModeSetting;
import minced.core.Animation;
import minced.core.SettingRenderer;
import minced.core.Fonts;
import minced.core.RenderUtil;
import minced.core.RecordClass234;
import net.minecraft.client.gui.DrawContext;
import sLM._4ZXYEo;

public class SettingRendererSlider
implements SettingRenderer {
    @Override
    public float method2935(Setting p0) { return 0.0f; }

    @Override
    public float method2938(DrawContext p0, Setting p1, float p2, float p3, float p4, float p5, int p6, Object p7) { return 0.0f; }

    @Override
    public RecordClass234 method2939(Setting p0, float p1, float p2, float p3, float p4, float p5, int p6, SliderSetting p7, Object p8) { return null; }

    static float щ;
    static float б;
    static float о;
    static float cfr_renamed_249;
    static float cfr_renamed_221;
    static String cfr_renamed_250;

    static float field10180;
    static float field10181;
    static float field10182;
    static float field10183;
    static float field10184;
    static float field10185;
    static int field10186;
    static float field10187;
    static float field10188;
    static float field10189;
    static float field10190;
    static float field10191;
    static float field10192;
    static float field10193;
    static float field10194;
    static float field10195;
    static float field10196;
    static float field10197;
    static int field10198;
    static float field10199;
    static float field10200;
    static float field10201;
    static float field10202;
    static float field10203;
    static float field10204;
    static float field10205;
    static float field10206;
    static float field10207;
    static float field10208;
    static float field10209;
    static float field10210;
    static float field10211;
    static float field10212;
    static float field10213;
    static float field10214;
    static float field10215;
    static float field10216;
    static float field10217;
    static float field10218;

    public RecordClass234 method2944(ModeSetting param1, float param2, float param3, float param4, float param5, float param6, int param7, SliderSetting param8, Object param9) {
        String[] stringArray = param1.options;
        String string = (String)param1.getValue();
        Objects.requireNonNull(param1);
        if (this.method2945(stringArray, string, param1::setValue, param3, param4 + field10180, param5, param6)) {
            return RecordClass234.consumed();
        }
        return RecordClass234.empty();
    }

    boolean method2945(String[] param1, String param2, SliderFormatter param3, float param4, float param5, float param6, float param7) {
        float v8 = 0.0f;
        float v9 = 0.0f;
        for (String v13 : param1) {
            float v14 = SettingRendererSlider.method2950(v13);
            if (v8 > 0.0f && v8 + v14 >= field10181) {
                v8 = 0.0f;
                v9 += field10182;
            }
            if (MathUtil.cfr_underscore(param6, param7, param4 + v8, param5 + v9, v14, field10183)) {
                param3.method1966(v13);
                return true;
            }
            v8 += v14 + 2.0f;
        }
        return false;
    }

    float method2946(DrawContext param1, String[] param2, boolean[] param3, Animation[] param4, float param5, float param6, float param7, int param8) {
        float v9 = 0.0f;
        float v10 = 0.0f;
        for (int v11 = 0; v11 < param2.length; ++v11) {
            String v12 = param2[v11];
            boolean v13 = param3[v11];
            float v14 = SettingRendererSlider.method2950(v12);
            if (v9 > 0.0f && v9 + v14 >= field10184) {
                v9 = 0.0f;
                v10 += field10185;
            }
            float v15 = param5 + v9;
            float v16 = param6 + v10;
            param4[v11].animate(v13 ? 1.0f : 0.0f);
            float v17 = param4[v11].getAnimatedValue();
            int v18 = ColorUtil.method1850(ColorUtil.method1853(field10186, field10187 * param7), param8, v17);
            RenderUtil.method397(v15, v16, v14, field10188, 2.0f, v18);
            Fonts.I[11].method2711(param1, v12, v15 + field10189, v16 + field10190, ColorUtil.method1853(-1, (field10191 + field10192 * v17) * param7));
            v9 += v14 + 2.0f;
        }
        return field10193 + v10 + field10194 + field10195;
    }

    float method2947(DrawContext param1, String[] param2, String param3, Animation[] param4, float param5, float param6, float param7, int param8) {
        float v9 = 0.0f;
        float v10 = 0.0f;
        for (int v11 = 0; v11 < param2.length; ++v11) {
            String v12 = param2[v11];
            boolean v13 = v12.equalsIgnoreCase(param3);
            float v14 = SettingRendererSlider.method2950(v12);
            if (v9 > 0.0f && v9 + v14 >= field10196) {
                v9 = 0.0f;
                v10 += field10197;
            }
            float v15 = param5 + v9;
            float v16 = param6 + v10;
            param4[v11].animate(v13 ? 1.0f : 0.0f);
            float v17 = param4[v11].getAnimatedValue();
            int v18 = ColorUtil.method1850(ColorUtil.method1853(field10198, field10199 * param7), param8, v17);
            RenderUtil.method397(v15, v16, v14, field10200, 2.0f, v18);
            Fonts.I[11].method2711(param1, v12, v15 + field10201, v16 + field10202, ColorUtil.method1853(-1, (field10203 + field10204 * v17) * param7));
            v9 += v14 + 2.0f;
        }
        return field10205 + v10 + field10206 + field10207;
    }

    static float method2948(String[] param0) {
        float v1 = 0.0f;
        float v2 = 0.0f;
        for (String v6 : param0) {
            float v7 = SettingRendererSlider.method2950(v6);
            if (v1 > 0.0f && v1 + v7 >= field10208) {
                v1 = 0.0f;
                v2 += field10209;
            }
            v1 += v7 + 2.0f;
        }
        return field10210 + v2 + field10211 + field10212;
    }

    int method2949(String[] param1, float param2, float param3, float param4, float param5) {
        float v6 = 0.0f;
        float v7 = 0.0f;
        for (int v8 = 0; v8 < param1.length; ++v8) {
            float v9 = SettingRendererSlider.method2950(param1[v8]);
            if (v6 > 0.0f && v6 + v9 >= field10213) {
                v6 = 0.0f;
                v7 += field10214;
            }
            if (MathUtil.cfr_underscore(param4, param5, param2 + v6, param3 + v7, v9, field10215)) {
                return v8;
            }
            v6 += v9 + 2.0f;
        }
        return -1;
    }

    static float method2950(String param0) {
        return Fonts.I[11].method2719(param0) + field10216;
    }

    public float method2951(ModeSetting param1) {
        return SettingRendererSlider.method2948(param1.options);
    }

    public float method2952(DrawContext param1, ModeSetting param2, float param3, float param4, float param5, float param6, int param7, Object param8) {
        String v9 = _4ZXYEo.фА("makeConcatWithConstants", new Object[]{}, (int)(param2.getSelectedIndex() + 1), (int)param2.options.length);
        float v10 = Fonts.I[12].method2719((String)((Object)v9));
        float v11 = this.method2936(param4, v10, field10217);
        this.method2937(param1, param2.getName(), param4, param5, v10, param6);
        Fonts.I[12].method2711(param1, (String)((Object)v9), v11, param5, ColorUtil.method1853(-1, param6));
        return this.method2947(param1, param2.options, (String)param2.getValue(), param2.optionAnimations, param4, param5 + field10218, param6, param7);
    }

    static {
        щ = 0.0f;
        б = 0.0f;
        о = 0.0f;
        cfr_renamed_249 = 0.0f;
        cfr_renamed_221 = 0.0f;
        cfr_renamed_250 = "\u0001/\u0001";
        field10217 = 159.5f;
        field10218 = 8.0f;
        field10180 = 8.0f;
        field10196 = 130.0f;
        field10197 = 14.0f;
        field10198 = -14342875;
        field10199 = 0.35f;
        field10200 = 12.0f;
        field10201 = 3.5f;
        field10202 = 5.0f;
        field10203 = 0.35f;
        field10204 = 0.65f;
        field10205 = 8.0f;
        field10206 = 12.0f;
        field10207 = 4.0f;
        field10184 = 130.0f;
        field10185 = 14.0f;
        field10186 = -14342875;
        field10187 = 0.35f;
        field10188 = 12.0f;
        field10189 = 3.5f;
        field10190 = 5.0f;
        field10191 = 0.35f;
        field10192 = 0.65f;
        field10193 = 8.0f;
        field10194 = 12.0f;
        field10195 = 4.0f;
        field10181 = 130.0f;
        field10182 = 14.0f;
        field10183 = 12.0f;
        field10213 = 130.0f;
        field10214 = 14.0f;
        field10215 = 12.0f;
        field10208 = 130.0f;
        field10209 = 14.0f;
        field10210 = 8.0f;
        field10211 = 12.0f;
        field10212 = 4.0f;
        field10216 = 6.5f;
    }
}




