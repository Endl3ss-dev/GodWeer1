/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.DrawContext
 */
package minced.core;

import minced.core.ColorUtil;
import minced.core.SliderSetting;
import minced.core.Setting;
import minced.core.ScissorUtil;
import minced.core.Fonts;
import minced.core.RecordClass234;
import net.minecraft.client.gui.DrawContext;

public interface SettingRenderer {
    public float method2935(Setting var1);

    default public float method2936(float param1, float param2, float param3) {
        return param1 + param3 - 15.0f - param2;
    }

    default public void method2937(DrawContext param1, String param2, float param3, float param4, float param5, float param6) {
        float v7 = 140.0f - param5;
        float v8 = Fonts.I[12].method2719(param2);
        int v9 = ColorUtil.method1853(-1, param6);
        if (v8 > v7) {
            ScissorUtil.method2621(param3, param4 - 5.0f, v7, 10.0);
            Fonts.I[12].method2711(param1, param2, param3, param4, v9);
            ScissorUtil.method2620();
        } else {
            Fonts.I[12].method2711(param1, param2, param3, param4, v9);
        }
    }

    public float method2938(DrawContext var1, Setting var2, float var3, float var4, float var5, float var6, int var7, Object var8);

    public RecordClass234 method2939(Setting var1, float var2, float var3, float var4, float var5, float var6, int var7, SliderSetting var8, Object var9);
}



