/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.DrawContext
 */
package minced.core;

import java.util.EnumMap;
import minced.core.ColorUtil;
import minced.core.MathUtil;
import minced.core.SettingRenderer;
import minced.core.Easings;
import minced.core.AnimatedFloat;
import minced.core.Fonts;
import minced.core.RenderUtil;
import minced.core.Minced;
import minced.enums.Combat;
import net.minecraft.client.gui.DrawContext;

public class GuiSidebar
implements SettingRenderer {
    @Override
    public float method2935(Setting p0) { return 0.0f; }

    @Override
    public float method2938(DrawContext p0, Setting p1, float p2, float p3, float p4, float p5, int p6, Object p7) { return 0.0f; }

    @Override
    public RecordClass234 method2939(Setting p0, float p1, float p2, float p3, float p4, float p5, int p6, SliderSetting p7, Object p8) { return null; }

    boolean field10225;
    static float field10226;
    static float field10227;
    static float field10228;
    static float field10229;
    StringBuilder field10230;
    static float field10231;
    static float field10232;
    static float field10233;
    static float field10234;
    static float field10235;
    static float field10236;
    static float field10237;
    static float field10238;
    static float field10239;
    static float field10240;
    static String field10241;
    static float field10242;
    static float field10243;
    static float field10244;
    static float field10245;
    static float field10246;
    static double field10247;
    static double field10248;
    static float field10249;
    static float field10250;
    static float field10251;
    static String field10252;
    static float field10253;
    static float field10254;
    static float field10255;
    EnumMap field10256 = new EnumMap(Combat.class);
    static float field10257;
    static float field10258;
    static float field10259;
    static float field10260;
    static float field10261;
    static float field10262;
    static float field10263;
    static float field10264;
    static float field10265;
    static float field10266;
    static float field10267;
    static float field10268;

    public void method2956() {
        this.field10225 = false;
    }

    public Combat method2957(float param1, float param2, int param3, int param4) {
        float v5 = (float)param4 + field10226;
        for (Combat v9 : Combat.values()) {
            if (MathUtil.cfr_underscore(param1, param2, param3 + 8, v5, field10227, field10228)) {
                return v9;
            }
            v5 += field10229;
        }
        return null;
    }

    public void method2958() {
        this.field10230.setLength(0);
        this.field10225 = false;
    }

    public boolean method2959(char param1) {
        if (!this.field10225) {
            return false;
        }
        if (this.field10230.length() < 10 && !Character.isISOControl(param1)) {
            this.field10230.append(param1);
            return true;
        }
        return false;
    }

    public boolean method2960(float param1, float param2, int param3, int param4) {
        float v5 = (float)param4 + field10231 - field10232;
        boolean v6 = MathUtil.cfr_underscore(param1, param2, param3 + 8, v5, field10233, field10234);
        if (v6) {
            this.field10225 = true;
        }
        return v6;
    }

    public boolean method2961(int param1) {
        if (!this.field10225) {
            return false;
        }
        if (param1 == 259 && !this.field10230.isEmpty()) {
            this.field10230.deleteCharAt(this.field10230.length() - 1);
            return true;
        }
        if (param1 == 256) {
            this.field10225 = false;
            return true;
        }
        return false;
    }

    private void method2962(DrawContext param1, int param2, int param3, float param4) {
        float v5 = param2 + 8;
        float v6 = (float)param3 + field10235 - field10236;
        RenderUtil.method397(v5, v6, field10237, field10238, field10239, ColorUtil.method1853(-1, field10240 * param4));
        String v7 = this.field10230.isEmpty() ? field10241 : this.field10230.toString();
        int v8 = ColorUtil.method1853(-1, field10242 * param4);
        Fonts.I[13].method2711(param1, v7, v5 + field10243, v6 + field10244, v8);
        if (this.field10225) {
            float v9 = v5 + (this.field10230.isEmpty() ? field10245 : field10246 + Fonts.I[13].method2719(this.field10230.toString()));
            float v10 = (float)((Math.sin((double)System.currentTimeMillis() / field10247) + 1.0) / field10248);
            RenderUtil.method394(param1, (float)Math.floor(v9), v6 + field10249, field10250, field10251, ColorUtil.method1853(-1, v10 * param4));
        }
        Fonts.field12502[16].method2711(param1, field10252, v5 + field10253, v6 + field10254, v8);
    }

    public void method2963(DrawContext param1, int param2, int param3, float param4, Combat param5) {
        this.method2964(param1, param2, param3, param4, param5);
        this.method2962(param1, param2, param3, param4);
    }

    private void method2964(DrawContext param12, int param2, int param3, float param4, Combat param5) {
        float v6 = (float)param3 + field10255;
        for (Combat v10 : Combat.values()) {
            boolean v11 = v10 == param5;
            AnimatedFloat v12 = (AnimatedFloat)(this.field10256.computeIfAbsent(v10, param1 -> new AnimatedFloat(v11 ? 1.0f : 0.0f, field10267, Easings.field11969)));
            v12.setTarget(v11 ? 1.0f : 0.0f);
            float v13 = v12.getValue();
            float v14 = v13 * param4;
            if (v14 > field10257) {
                RenderUtil.method397(param2 + 8, v6, field10258, field10259, field10260, ColorUtil.method1855(Minced.getInstance().getThemeManager().method279(), (int)(field10261 * v14)));
            }
            int v15 = ColorUtil.method1850(ColorUtil.method1853(-1, field10262 * param4), ColorUtil.method1853(Minced.getInstance().getThemeManager().method279(), param4), v13);
            int v16 = ColorUtil.method1850(ColorUtil.method1853(-1, field10263 * param4), ColorUtil.method1853(-1, param4), v13);
            Fonts.field12502[16].method2711(param12, v10.method169(), param2 + 13, v6 + field10264, v15);
            Fonts.I[14].method2711(param12, v10.method168(), param2 + 28, v6 + field10265, v16);
            v6 += field10266;
        }
    }

    public boolean method2966() {
        return this.field10225;
    }

    public GuiSidebar(Combat param1) {
        this.field10230 = new StringBuilder();
        for (Combat v5 : Combat.values()) {
            this.field10256.put(v5, new AnimatedFloat(v5 == param1 ? 1.0f : 0.0f, field10268, Easings.field11969));
        }
    }

    public String method2967() {
        return this.field10230.toString();
    }

    static {
        field10268 = 12.0f;
        field10255 = 41.0f;
        field10257 = 0.01f;
        field10258 = 88.0f;
        field10259 = 17.0f;
        field10260 = 5.0f;
        field10261 = 18.0f;
        field10262 = 0.28f;
        field10263 = 0.32f;
        field10264 = 7.0f;
        field10265 = 6.5f;
        field10266 = 21.0f;
        field10235 = 272.5f;
        field10236 = 24.0f;
        field10237 = 88.0f;
        field10238 = 17.0f;
        field10239 = 5.0f;
        field10240 = 0.02f;
        field10241 = "Search";
        field10242 = 0.48f;
        field10243 = 6.0f;
        field10244 = 7.0f;
        field10245 = 5.5f;
        field10246 = 7.5f;
        field10247 = 320.0;
        field10248 = 2.0;
        field10249 = 6.0f;
        field10250 = 0.5f;
        field10251 = 6.0f;
        field10252 = "l";
        field10253 = 76.0f;
        field10254 = 7.0f;
        field10226 = 41.0f;
        field10227 = 88.0f;
        field10228 = 17.0f;
        field10229 = 21.0f;
        field10231 = 272.5f;
        field10232 = 24.0f;
        field10233 = 88.0f;
        field10234 = 17.0f;
        field10267 = 12.0f;
    }
}



