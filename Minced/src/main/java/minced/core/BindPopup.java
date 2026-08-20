/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.DrawContext
 *  sg.ec.eh
 */
package minced.core;

import minced.core.ColorUtil;
import minced.core.MathUtil;
import minced.core.ColorPickerRenderer;
import minced.core.ThemeColors;
import minced.core.Easings;
import minced.core.AnimatedFloat;
import minced.core.Fonts;
import minced.core.RenderUtil;
import minced.core.Module;
import minced.enums.KeyCode;
import minced.enums.MAIN;
import minced.enums.TOGGLE;
import net.minecraft.client.gui.DrawContext;
import sg.ec.eh;

public class BindPopup
implements ColorPickerRenderer {
    boolean field11237;
    boolean field11238;
    Module field11239;
    static float field11240;
    AnimatedFloat field11241 = new AnimatedFloat(0.0f, field11240);
    static float field11242;
    AnimatedFloat field11243 = new AnimatedFloat(0.0f, field11242, Easings.field11952);
    static float field11244;
    AnimatedFloat cfr_renamed_4 = new AnimatedFloat(0.0f, field11244, Easings.field11952);
    eh field11245;
    static BindPopup field11246;
    static float field11247;
    static float field11248;
    static float field11249;
    static float field11250;
    static float field11251;
    static float field11252;
    static String field11253;
    static String field11254;
    static float field11255;
    static float field11256;
    static float field11257;
    static float field11258;
    static float field11259;
    static float field11260;
    static float field11261;
    static float field11262;
    static float field11263;
    static String field11264;
    static float field11265;
    static float field11266;
    static String field11267;
    static String field11268;
    static float field11269;
    static float field11270;
    static float field11271;
    static float field11272;
    static float field11273;
    static float field11274;
    static float field11275;
    static float field11276;
    static float field11277;
    static float field11278;
    static float field11279;
    static float field11280;
    static float field11281;
    static float field11282;
    static float field11283;
    static float field11284;
    static float field11285;
    static float field11286;
    static float field11287;
    static float field11288;
    static String field11289;
    static String field11290;
    static float field11291;
    static float field11292;
    static float field11293;
    static float field11294;
    static float field11295;

    public void O() {
        this.method3173(!this.field11237);
    }

    @Override
    public void method3155(int param1, int param2, int param3) {
        if (!this.field11237 || !this.field11238) {
            return;
        }
        if (param1 == 256 || param1 == 261) {
            this.field11239.setKey(-100);
            this.field11238 = false;
            return;
        }
        this.field11239.setKey(param1);
        this.field11238 = false;
    }

    public BindPopup(Module param1, eh param2) {
        this.field11239 = param1;
        this.field11245 = param2;
    }

    public static boolean method3168() {
        return field11246 != null && BindPopup.field11246.field11237;
    }

    @Override
    public boolean method3165(float param1, float param2, int param3) {
        float v23;
        float v25;
        float v8;
        float v7;
        float v6;
        if (!this.field11237) {
            return false;
        }
        float[] v4 = this.method3172();
        float v5 = v4[0];
        boolean v9 = MathUtil.cfr_underscore(param1, param2, v5, v6 = v4[1], v7 = v4[2], v8 = v4[3]);
        if (!v9) {
            this.method3173(false);
            return true;
        }
        if (param3 == 1) {
            this.method3173(false);
            return true;
        }
        float v10 = field11247;
        float v11 = v6 + v10;
        float v12 = field11248;
        float v13 = field11249;
        float v14 = (v7 - v13 * 2.0f) / 2.0f + field11250;
        float v15 = field11251;
        float v16 = v5 + v13 - field11252;
        float v17 = v16 + v14 + v15;
        boolean v18 = MathUtil.cfr_underscore(param1, param2, v16, v11, v14, v12);
        boolean v19 = MathUtil.cfr_underscore(param1, param2, v17, v11, v14, v12);
        if ((v18 || v19) && param3 == 0) {
            this.field11239.setToggleMode(v18 ? TOGGLE.TOGGLE : TOGGLE.HOLD);
            return true;
        }
        String v20 = this.field11238 ? field11253 : (this.field11239.getKey() != -100 ? KeyCode.getDisplayName(this.field11239.getKey()) : field11254);
        float v21 = Fonts.field12491[12].method2719(v20);
        float v22 = Math.max(field11255, v21 + field11256);
        float v24 = v5 + v7 - v22 - field11258;
        boolean v26 = MathUtil.cfr_underscore(param1, param2, v24, v25 = v6 + (v10 - (v23 = field11257)) / 2.0f, v22, v23);
        if (v26) {
            if (param3 == 0) {
                this.field11238 = true;
                return true;
            }
            if (param3 == 2) {
                this.field11239.setKey(-100);
                this.field11238 = false;
                return true;
            }
            if (this.field11238 && param3 >= 0 && param3 != 1) {
                this.field11239.setKey(param3);
                this.field11238 = false;
                return true;
            }
        }
        return true;
    }

    public static void method3169() {
        if (field11246 != null) {
            field11246.method3173(false);
        }
    }

    public boolean method3170() {
        return this.field11237;
    }

    @Override
    public void method3167(DrawContext param1, float param2, float param3, float param4) {
        int v462;
        int v452;
        int v44;
        int v43;
        int v42;
        int v41;
        int v26;
        int v25;
        this.field11241.setTarget(this.field11237 ? 1.0f : 0.0f);
        float v5 = this.field11241.getValue();
        if (v5 <= field11259) {
            return;
        }
        float[] v6 = this.method3172();
        float v7 = v6[0];
        float v8 = v6[1];
        float v9 = v6[2];
        float v10 = v6[3];
        float v11 = param4 * v5;
        int v12 = ThemeColors.method3199(MAIN.HEADER);
        int v13 = ThemeColors.method3199(MAIN.OUTLINE);
        int v14 = (int)(ThemeColors.method3214(MAIN.OUTLINE) * v11);
        int v15 = ColorUtil.method1853(ThemeColors.method3199(MAIN.WINDOW_TEXT), ThemeColors.method3214(MAIN.WINDOW_TEXT) / field11260 * v11);
        RenderUtil.method390(v7, v8, v9, v10, field11261, v12, v11);
        RenderUtil.method392(v7, v8, v9, v10, field11262, field11263, v13, v14);
        String v16 = field11264;
        float v17 = field11265;
        float v18 = v8 + (v17 - Fonts.field12491[13].method2718()) / 2.0f;
        Fonts.field12491[13].method2711(param1, v16, v7 + field11266, v18 + 1.0f, v15);
        String v19 = this.field11238 ? field11267 : (this.field11239.getKey() != -100 ? KeyCode.getDisplayName(this.field11239.getKey()) : field11268);
        float v20 = Fonts.field12491[12].method2719(v19);
        float v21 = Math.max(field11269, v20 + field11270);
        float v22 = field11271;
        float v23 = v7 + v9 - v21 - field11272;
        float v24 = v8 + (v17 - v22) / 2.0f + field11273;
        if (this.field11238 || this.field11239.getKey() != -100) {
            v25 = ColorUtil.method1853(ThemeColors.method3199(MAIN.INDICATOR), v11);
            v26 = ColorUtil.method1853(ThemeColors.method3199(MAIN.WINDOW_BG), v11);
        } else {
            v25 = ColorUtil.method1853(ThemeColors.method3199(MAIN.FIELD_INACTIVE), field11274 * v11);
            v26 = ColorUtil.method1853(ThemeColors.method3199(MAIN.TEXT_INACTIVE), field11275 * v11);
        }
        RenderUtil.method397(v23, v24, v21, v22, 2.0f, v25);
        RenderUtil.method392(v23, v24, v21, v22, 2.0f, field11276, v13, v14);
        float v27 = v23 + (v21 - v20) / 2.0f;
        float v28 = v24 + (v22 - Fonts.field12491[12].method2718()) / 2.0f + field11277;
        Fonts.field12491[12].method2711(param1, v19, v27, v28, v26);
        float v29 = v8 + v17;
        float v30 = field11278;
        float v31 = field11279;
        float v32 = (v9 - v31 * 2.0f) / 2.0f + field11280;
        float v33 = field11281;
        float v34 = v7 + v31 - field11282;
        float v35 = v34 + v32 + v33;
        boolean v36 = this.field11239.getToggleMode() == TOGGLE.TOGGLE;
        boolean v37 = MathUtil.cfr_underscore(param2, param3, v34, v29, v32, v30);
        boolean v38 = MathUtil.cfr_underscore(param2, param3, v35, v29, v32, v30);
        this.field11243.setTarget(v37 ? 1.0f : 0.0f);
        this.cfr_renamed_4.setTarget(v38 ? 1.0f : 0.0f);
        float v39 = this.field11243.getValue();
        float v40 = this.cfr_renamed_4.getValue();
        if (v36) {
            v41 = ColorUtil.method1853(ThemeColors.method3199(MAIN.INDICATOR), v11);
            v42 = ColorUtil.method1853(ThemeColors.method3199(MAIN.WINDOW_BG), v11);
        } else {
            v43 = ColorUtil.method1853(ThemeColors.method3199(MAIN.FIELD_INACTIVE), field11283 * v11);
            v44 = ColorUtil.method1853(ThemeColors.method3199(MAIN.INDICATOR), v11);
            v41 = ColorUtil.method1850(v43, v44, v39);
            v452 = ColorUtil.method1853(ThemeColors.method3199(MAIN.TEXT_INACTIVE), field11284 * v11);
            v462 = ColorUtil.method1853(ThemeColors.method3199(MAIN.WINDOW_BG), v11);
            v42 = ColorUtil.method1850(v452, v462, v39);
        }
        if (!v36) {
            v43 = ColorUtil.method1853(ThemeColors.method3199(MAIN.INDICATOR), v11);
            v44 = ColorUtil.method1853(ThemeColors.method3199(MAIN.WINDOW_BG), v11);
        } else {
            v452 = ColorUtil.method1853(ThemeColors.method3199(MAIN.FIELD_INACTIVE), field11285 * v11);
            v462 = ColorUtil.method1853(ThemeColors.method3199(MAIN.INDICATOR), v11);
            v43 = ColorUtil.method1850(v452, v462, v40);
            int v47 = ColorUtil.method1853(ThemeColors.method3199(MAIN.TEXT_INACTIVE), field11286 * v11);
            int v48 = ColorUtil.method1853(ThemeColors.method3199(MAIN.WINDOW_BG), v11);
            v44 = ColorUtil.method1850(v47, v48, v40);
        }
        RenderUtil.method397(v34, v29, v32, v30, 1.0f, v41);
        RenderUtil.method392(v34, v29, v32, v30, 1.0f, field11287, v13, v14);
        RenderUtil.method397(v35, v29, v32, v30, 1.0f, v43);
        RenderUtil.method392(v35, v29, v32, v30, 1.0f, field11288, v13, v14);
        String v452_273 = field11289;
        String v462_274 = field11290;
        Fonts.field12491[12].method2711(param1, v452_273, v34 + (v32 - Fonts.field12491[12].method2719(v452_273)) / 2.0f, v29 + (v30 - Fonts.field12491[12].method2718()) / 2.0f + field11291, v42);
        Fonts.field12491[12].method2711(param1, v462_274, v35 + (v32 - Fonts.field12491[12].method2719(v462_274)) / 2.0f, v29 + (v30 - Fonts.field12491[12].method2718()) / 2.0f + field11292, v44);
    }

    public static void method3171() {
        field11246 = null;
    }

    private float[] method3172() {
        float v1 = field11293;
        float v2 = field11294;
        float v3 = this.field11245.С() + (Float)this.field11245.з() + field11295;
        float v4 = this.field11245.ч() - (Float)1.0f;
        return new float[]{v3, v4, v1, v2};
    }

    public void method3173(boolean param1) {
        if (param1 && field11246 != this) {
            if (field11246 != null) {
                BindPopup.field11246.field11237 = false;
            }
            field11246 = this;
        } else if (!param1 && field11246 == this) {
            field11246 = null;
        }
        this.field11237 = param1;
        if (!param1) {
            this.field11238 = false;
        }
    }

    public boolean method3174(float param1, float param2) {
        if (!this.field11237) {
            return false;
        }
        float[] v3 = this.method3172();
        return MathUtil.cfr_underscore(param1, param2, v3[0], v3[1], v3[2], v3[3]);
    }

    public static void method3175() {
        if (field11246 != null) {
            BindPopup.field11246.field11241.setValue(0.0f);
            BindPopup.field11246.field11243.setValue(0.0f);
            BindPopup.field11246.cfr_renamed_4.setValue(0.0f);
            BindPopup.field11246.field11237 = false;
            BindPopup.field11246.field11238 = false;
            field11246 = null;
        }
    }

    static {
        field11240 = 12.0f;
        field11242 = 10.0f;
        field11244 = 10.0f;
        field11293 = 70.0f;
        field11294 = 35.0f;
        field11295 = 5.0f;
        field11259 = 0.01f;
        field11260 = 255.0f;
        field11261 = 3.0f;
        field11262 = 3.0f;
        field11263 = 0.6f;
        field11264 = "Клавиша";
        field11265 = 18.0f;
        field11266 = 5.0f;
        field11267 = "...";
        field11268 = "n/a";
        field11269 = 12.0f;
        field11270 = 8.0f;
        field11271 = 12.0f;
        field11272 = 4.0f;
        field11273 = 0.5f;
        field11274 = 0.08f;
        field11275 = 0.7f;
        field11276 = 0.5f;
        field11277 = 1.25f;
        field11278 = 13.0f;
        field11279 = 6.5f;
        field11280 = 1.5f;
        field11281 = 3.0f;
        field11282 = 3.0f;
        field11283 = 0.08f;
        field11284 = 0.7f;
        field11285 = 0.08f;
        field11286 = 0.7f;
        field11287 = 0.5f;
        field11288 = 0.5f;
        field11289 = "Toggle";
        field11290 = "Hold";
        field11291 = 1.25f;
        field11292 = 1.25f;
        field11247 = 18.0f;
        field11248 = 12.0f;
        field11249 = 6.5f;
        field11250 = 1.5f;
        field11251 = 3.0f;
        field11252 = 3.0f;
        field11253 = "...";
        field11254 = "n/a";
        field11255 = 14.0f;
        field11256 = 8.0f;
        field11257 = 12.0f;
        field11258 = 6.0f;
        BindPopup.method3171();
    }
}



