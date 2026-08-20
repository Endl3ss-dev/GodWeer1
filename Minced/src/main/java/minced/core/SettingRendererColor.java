/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.DrawContext
 *  sLM.4ZXYEo
 */
package minced.core;

import minced.core.ColorUtil;
import minced.core.MathUtil;
import minced.core.SliderSetting;
import minced.core.ColorListSetting;
import minced.core.ScissorUtil;
import minced.core.SettingRenderer;
import minced.core.Fonts;
import minced.core.RenderUtil;
import minced.core.RecordClass234;
import net.minecraft.client.gui.DrawContext;
import sLM._4ZXYEo;

public class SettingRendererColor
implements SettingRenderer {
    @Override
    public float method2935(Setting p0) { return 0.0f; }

    @Override
    public float method2938(DrawContext p0, Setting p1, float p2, float p3, float p4, float p5, int p6, Object p7) { return 0.0f; }

    @Override
    public RecordClass234 method2939(Setting p0, float p1, float p2, float p3, float p4, float p5, int p6, SliderSetting p7, Object p8) { return null; }

    static float k2;
    static float У;
    static float Л;
    static String cfr_renamed_253;

    ColorListSetting field10322;
    static float field10323;
    static float field10324;
    static float field10325;
    long field10326 = 0L;
    static long field10327;
    boolean field10328 = true;
    static float field10329;
    static float field10330;
    static float field10331;
    static float field10332;
    static float field10333;
    static float field10334;
    static float field10335;
    static float field10336;
    static float field10337;
    static float field10338;
    static float field10339;
    static float field10340;
    static String field10341;
    static float field10342;
    static float field10343;
    static float field10344;
    static double field10345;
    static float field10346;
    static float field10347;
    static float field10348;
    static float field10349;
    static float field10350;
    static float field10351;
    static float field10352;
    static float field10353;
    static float field10354;
    static float field10355;

    public float method2976(DrawContext param1, ColorListSetting param2, float param3, float param4, float param5, float param6, int param7, Object param8) {
        boolean v9 = this.field10322 == param2;
        float v10 = field10323;
        float v11 = param3 + field10324;
        float v12 = param5 + field10325;
        String v13 = param2.method2237();
        if (v13 == null) {
            v13 = "";
        }
        long v14 = System.currentTimeMillis();
        if (v9 && v14 - this.field10326 > field10327) {
            this.field10328 = !this.field10328;
            this.field10326 = v14;
        }
        this.method2937(param1, param2.getName(), param4, param5, 0.0f, param6);
        param2.field8333.animate(v9 ? 1.0f : 0.0f);
        float v16 = param2.field8333.getAnimatedValue();
        RenderUtil.method397(v11, v12, v10, field10329, field10330, ColorUtil.method1853(-1, (field10331 + v16 * field10332) * param6));
        int v17 = ColorUtil.method1850(-1, param7, v16);
        float v18 = (field10333 + v16 * field10334) * param6 * field10335;
        RenderUtil.method392(v11, v12, v10, field10336, field10337, field10338, v17, v18);
        int v19 = (int)(v10 - field10339);
        int v20 = (int)(v12 + (field10340 - Fonts.I[12].method2718()) / 2.0f + 1.0f);
        boolean v21 = v13.isEmpty();
        String v22 = v21 && !v9 ? field10341 : v13;
        int v23 = v21 && !v9 ? ColorUtil.method1853(-1, field10342 * param6) : ColorUtil.method1853(-1, field10343 * param6);
        float v24 = Fonts.I[12].method2719(v13);
        float v25 = v9 && v24 > (float)v19 ? v24 - (float)v19 : 0.0f;
        float v26 = v11 + field10344 - v25;
        ScissorUtil.method2621(v11 + 1.0f, v12 + 1.0f, v10 - 2.0f, field10345);
        Fonts.I[12].method2711(param1, v22, v26, v20, v23);
        ScissorUtil.method2620();
        if (v9 && this.field10328) {
            float v27 = v11 + field10346 + v24 - v25;
            RenderUtil.method397(v27, v12 + field10347, field10348, field10349, 0.0f, ColorUtil.method1853(-1, param6));
        }
        return field10350;
    }

    private void method2977() {
        this.field10328 = true;
        this.field10326 = System.currentTimeMillis();
    }

    public RecordClass234 method2978(ColorListSetting param1, float param2, float param3, float param4, float param5, float param6, int param7, SliderSetting param8, Object param9) {
        float v10 = field10351;
        float v11 = param2 + field10352;
        float v12 = param4 + field10353;
        if (param7 == 0 && MathUtil.cfr_underscore(param5, param6, v11, v12, v10, field10354)) {
            this.field10322 = param1;
            this.method2977();
            return RecordClass234.consumed();
        }
        if (param7 == 0 && this.field10322 == param1) {
            this.field10322 = null;
        }
        return RecordClass234.empty();
    }

    public boolean method2979(int param1, int param2) {
        if (this.field10322 == null) {
            return false;
        }
        if (param1 == 256 || param1 == 257) {
            this.field10322 = null;
            return true;
        }
        if (param1 == 259) {
            String v3 = this.field10322.method2237();
            if (v3 != null && !v3.isEmpty()) {
                this.field10322.method2238(v3.substring(0, v3.length() - 1));
                this.method2977();
            }
            return true;
        }
        return true;
    }

    public boolean method2980(char param1, int param2) {
        if (this.field10322 == null) {
            return false;
        }
        if (!Character.isISOControl(param1)) {
            String v3 = this.field10322.method2237();
            if (v3 == null) {
                v3 = "";
            }
            if (v3.length() < 64) {
                this.field10322.method2238((String)((Object)_4ZXYEo.ф8("makeConcatWithConstants", new Object[]{}, (String)v3, (char)param1)));
                this.method2977();
            }
        }
        return true;
    }

    public void method2981() {
        this.field10322 = null;
    }

    public boolean method2982() {
        return this.field10322 != null;
    }

    public float method2983(ColorListSetting param1) {
        return field10355;
    }

    static {
        k2 = 0.0f;
        У = 0.0f;
        Л = 0.0f;
        field10323 = 145.5f;
        field10324 = 7.0f;
        field10325 = 6.5f;
        field10327 = 500L;
        field10329 = 12.0f;
        field10330 = 2.5f;
        field10331 = 0.03f;
        field10332 = 0.03f;
        field10333 = 0.15f;
        field10334 = 0.85f;
        field10335 = 255.0f;
        field10336 = 12.0f;
        field10337 = 2.5f;
        field10338 = 0.5f;
        field10339 = 8.0f;
        field10340 = 12.0f;
        field10341 = "...";
        field10342 = 0.25f;
        field10343 = 0.75f;
        field10344 = 4.0f;
        field10345 = 10.0;
        field10346 = 4.0f;
        field10347 = 2.5f;
        field10348 = 0.5f;
        field10349 = 7.0f;
        field10350 = 22.0f;
        field10351 = 145.5f;
        field10352 = 7.0f;
        field10353 = 6.0f;
        field10354 = 12.0f;
        field10355 = 22.0f;
        cfr_renamed_253 = "\u0001\u0001";
    }
}




