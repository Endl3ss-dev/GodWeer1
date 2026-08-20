/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.DrawContext
 *  sLM.4ZXYEo
 */
package minced.core;

import java.lang.invoke.CallSite;
import java.util.List;
import minced.core.ColorUtil;
import minced.core.MultiBooleanSetting;
import minced.core.BooleanSetting;
import minced.core.SliderSetting;
import minced.core.Setting;
import minced.core.SettingRenderer;
import minced.core.SettingRendererSlider;
import minced.core.Fonts;
import minced.core.RecordClass234;
import net.minecraft.client.gui.DrawContext;
import sLM._4ZXYEo;

public class SettingRendererMulti
implements SettingRenderer {
    @Override
    public float method2935(Setting p0) { return 0.0f; }

    @Override
    public float method2938(DrawContext p0, Setting p1, float p2, float p3, float p4, float p5, int p6, Object p7) { return 0.0f; }

    @Override
    public RecordClass234 method2939(Setting p0, float p1, float p2, float p3, float p4, float p5, int p6, SliderSetting p7, Object p8) { return null; }

    static String ъБ;

    static float field10269;
    SettingRendererSlider field10270;
    static float field10271;
    static float field10272;

    public float method2968(DrawContext param1, MultiBooleanSetting param2, float param3, float param4, float param5, float param6, int param7, Object param8) {
        long v9 = ((List)param2.getValue()).stream().filter(param0 -> (Boolean)((Setting)param0).getValue()).count();
        String v11 = _4ZXYEo.Щ("makeConcatWithConstants", new Object[]{}, (long)v9, (int)((List)param2.getValue()).size());
        float v12 = Fonts.I[12].method2719((String)((Object)v11));
        float v13 = this.method2936(param4, v12, field10269);
        this.method2937(param1, param2.getName(), param4, param5, v12, param6);
        Fonts.I[12].method2711(param1, (String)((Object)v11), v13, param5, ColorUtil.method1853(-1, param6));
        String[] v14 = (String[])((List)param2.getValue()).stream().map(param0 -> (String)((Setting)param0).getName()).toArray(String[]::new);
        boolean[] v15 = new boolean[v14.length];
        for (int v16 = 0; v16 < v15.length; ++v16) {
            v15[v16] = (Boolean)param2.method1899(v16).getValue();
        }
        return this.field10270.method2946(param1, v14, v15, param2.field7736, param4, param5 + field10271, param6, param7);
    }

    public RecordClass234 method2972(MultiBooleanSetting param1, float param2, float param3, float param4, float param5, float param6, int param7, SliderSetting param8, Object param9) {
        String[] v10 = (String[])((List)param1.getValue()).stream().map(param0 -> (String)((Setting)param0).getName()).toArray(String[]::new);
        int v11 = this.field10270.method2949(v10, param3, param4 + field10272, param5, param6);
        if (v11 >= 0) {
            BooleanSetting v12 = param1.method1899(v11);
            v12.setValue((Boolean)v12.getValue() == false);
            return RecordClass234.consumed();
        }
        return RecordClass234.empty();
    }

    public SettingRendererMulti(SettingRendererSlider param1) {
        this.field10270 = param1;
    }

    public float method2973(MultiBooleanSetting param1) {
        String[] v2 = (String[])((List)param1.getValue()).stream().map(param0 -> (String)((Setting)param0).getName()).toArray(String[]::new);
        return SettingRendererSlider.method2948(v2);
    }

    static {
        ъБ = "\u0001/\u0001";
        field10269 = 159.5f;
        field10271 = 8.0f;
        field10272 = 8.0f;
    }
}



