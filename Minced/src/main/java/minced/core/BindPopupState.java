/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.DrawContext
 *  sg.ec.chn
 */
package minced.core;

import minced.core.ColorUtil;
import minced.core.MathUtil;
import minced.core.BooleanSetting;
import minced.core.ColorPickerRenderer;
import minced.core.ThemeColors;
import minced.core.Easings;
import minced.core.AnimatedFloat;
import minced.core.Fonts;
import minced.core.RenderUtil;
import minced.enums.KeyCode;
import minced.enums.MAIN;
import minced.enums.TOGGLE;
import net.minecraft.client.gui.DrawContext;
import sg.ec.chn;

public class BindPopupState
implements ColorPickerRenderer {
    boolean field11597;
    static BindPopupState field11598;
    boolean field11599;
    static float field11600;
    static float field11601;
    static float field11602;
    chn field11603;
    static float field11604;
    static float field11605;
    AnimatedFloat field11606 = new AnimatedFloat(0.0f, field11656);
    AnimatedFloat field11607 = new AnimatedFloat(0.0f, field11657, Easings.field11952);
    AnimatedFloat field11608 = new AnimatedFloat(0.0f, field11658, Easings.field11952);
    static float field11609;
    static float field11610;
    static float field11611;
    static float field11612;
    static float field11613;
    static float field11614;
    BooleanSetting field11615;
    static String field11616;
    static String field11617;
    static float field11618;
    static float field11619;
    static float field11620;
    static float field11621;
    static float field11622;
    static float field11623;
    static float field11624;
    static float field11625;
    static float field11626;
    static String field11627;
    static float field11628;
    static float field11629;
    static String field11630;
    static String field11631;
    static float field11632;
    static float field11633;
    static float field11634;
    static float field11635;
    static float field11636;
    static float field11637;
    static float field11638;
    static float field11639;
    static float field11640;
    static float field11641;
    static float field11642;
    static float field11643;
    static float field11644;
    static float field11645;
    static float field11646;
    static float field11647;
    static float field11648;
    static float field11649;
    static float field11650;
    static float field11651;
    static String field11652;
    static String field11653;
    static float field11654;
    static float field11655;
    static float field11656;
    static float field11657;
    static float field11658;

    public boolean method3271() {
        return this.field11597;
    }

    public boolean method3272(float param1, float param2) {
        if (!this.field11597) {
            return false;
        }
        float[] v3 = this.method3275();
        return MathUtil.cfr_underscore(param1, param2, v3[0], v3[1], v3[2], v3[3]);
    }

    public void method3273(boolean param1) {
        if (param1 && field11598 != this) {
            if (field11598 != null) {
                BindPopupState.field11598.field11597 = false;
            }
            field11598 = this;
        } else if (!param1 && field11598 == this) {
            field11598 = null;
        }
        this.field11597 = param1;
        if (!param1) {
            this.field11599 = false;
        }
    }

    public static void method3274() {
        if (field11598 != null) {
            field11598.method3273(false);
        }
    }

    private float[] method3275() {
        float v1 = field11600;
        float v2 = field11601;
        float v3 = field11602;
        float v4 = this.field11603.С() + (Float)this.field11603.з() - v3 - v1 - field11604;
        float v5 = this.field11603.ч() - (Float)field11605;
        return new float[]{v4, v5, v1, v2};
    }

    public static void method3276() {
        if (field11598 != null) {
            BindPopupState.field11598.field11606.setValue(0.0f);
            BindPopupState.field11598.field11607.setValue(0.0f);
            BindPopupState.field11598.field11608.setValue(0.0f);
            BindPopupState.field11598.field11597 = false;
            BindPopupState.field11598.field11599 = false;
            field11598 = null;
        }
    }

    public static boolean method3277() {
        return field11598 != null && BindPopupState.field11598.field11597;
    }

    @Override
    public boolean method3165(float param1, float param2, int param3) {
        float v23;
        float v25;
        float v8;
        float v7;
        float v6;
        if (!this.field11597) {
            return false;
        }
        float[] v4 = this.method3275();
        float v5 = v4[0];
        boolean v9 = MathUtil.cfr_underscore(param1, param2, v5, v6 = v4[1], v7 = v4[2], v8 = v4[3]);
        if (!v9) {
            this.method3273(false);
            return true;
        }
        if (param3 == 1) {
            this.method3273(false);
            return true;
        }
        float v10 = field11609;
        float v11 = v6 + v10;
        float v12 = field11610;
        float v13 = field11611;
        float v14 = (v7 - v13 * 2.0f) / 2.0f + field11612;
        float v15 = field11613;
        float v16 = v5 + v13 - field11614;
        float v17 = v16 + v14 + v15;
        boolean v18 = MathUtil.cfr_underscore(param1, param2, v16, v11, v14, v12);
        boolean v19 = MathUtil.cfr_underscore(param1, param2, v17, v11, v14, v12);
        if ((v18 || v19) && param3 == 0) {
            this.field11615.method1904(v18 ? TOGGLE.TOGGLE : TOGGLE.HOLD);
            return true;
        }
        String v20 = this.field11599 ? field11616 : (this.field11615.method1903() != -100 ? KeyCode.getDisplayName(this.field11615.method1903()) : field11617);
        float v21 = Fonts.field12491[12].method2719(v20);
        float v22 = Math.max(field11618, v21 + field11619);
        float v24 = v5 + v7 - v22 - field11621;
        boolean v26 = MathUtil.cfr_underscore(param1, param2, v24, v25 = v6 + (v10 - (v23 = field11620)) / 2.0f, v22, v23);
        if (v26) {
            if (param3 == 0) {
                this.field11599 = true;
                return true;
            }
            if (param3 == 2) {
                this.field11615.cfr_renamed_0(-100);
                this.field11599 = false;
                return true;
            }
            if (this.field11599 && param3 >= 0 && param3 != 1) {
                this.field11615.cfr_renamed_0(param3);
                this.field11599 = false;
                return true;
            }
        }
        return true;
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
        this.field11606.setTarget(this.field11597 ? 1.0f : 0.0f);
        float v5 = this.field11606.getValue();
        if (v5 <= field11622) {
            return;
        }
        float[] v6 = this.method3275();
        float v7 = v6[0];
        float v8 = v6[1];
        float v9 = v6[2];
        float v10 = v6[3];
        float v11 = param4 * v5;
        int v12 = ThemeColors.method3199(MAIN.MAIN);
        int v13 = ThemeColors.method3199(MAIN.OUTLINE);
        int v14 = (int)(ThemeColors.method3214(MAIN.OUTLINE) * v11);
        int v15 = ColorUtil.method1853(ThemeColors.method3199(MAIN.WINDOW_TEXT), ThemeColors.method3214(MAIN.WINDOW_TEXT) / field11623 * v11);
        RenderUtil.method390(v7, v8, v9, v10, field11624, v12, v11);
        RenderUtil.method392(v7, v8, v9, v10, field11625, field11626, v13, v14);
        String v16 = field11627;
        float v17 = field11628;
        float v18 = v8 + (v17 - Fonts.field12491[13].method2718()) / 2.0f;
        Fonts.field12491[13].method2711(param1, v16, v7 + field11629, v18 + 1.0f, v15);
        String v19 = this.field11599 ? field11630 : (this.field11615.method1903() != -100 ? KeyCode.getDisplayName(this.field11615.method1903()) : field11631);
        float v20 = Fonts.field12491[12].method2719(v19);
        float v21 = Math.max(field11632, v20 + field11633);
        float v22 = field11634;
        float v23 = v7 + v9 - v21 - field11635;
        float v24 = v8 + (v17 - v22) / 2.0f + field11636;
        if (this.field11599 || this.field11615.method1903() != -100) {
            v25 = ColorUtil.method1853(ThemeColors.method3199(MAIN.INDICATOR), v11);
            v26 = ColorUtil.method1853(ThemeColors.method3199(MAIN.WINDOW_BG), v11);
        } else {
            v25 = ColorUtil.method1853(ThemeColors.method3199(MAIN.FIELD_INACTIVE), field11637 * v11);
            v26 = ColorUtil.method1853(ThemeColors.method3199(MAIN.TEXT_INACTIVE), field11638 * v11);
        }
        RenderUtil.method397(v23, v24, v21, v22, 2.0f, v25);
        RenderUtil.method392(v23, v24, v21, v22, 2.0f, field11639, v13, v14);
        float v27 = v23 + (v21 - v20) / 2.0f;
        float v28 = v24 + (v22 - Fonts.field12491[12].method2718()) / 2.0f + field11640;
        Fonts.field12491[12].method2711(param1, v19, v27, v28, v26);
        float v29 = v8 + v17;
        float v30 = field11641;
        float v31 = field11642;
        float v32 = (v9 - v31 * 2.0f) / 2.0f + field11643;
        float v33 = field11644;
        float v34 = v7 + v31 - field11645;
        float v35 = v34 + v32 + v33;
        boolean v36 = this.field11615.method1901() == TOGGLE.TOGGLE;
        boolean v37 = MathUtil.cfr_underscore(param2, param3, v34, v29, v32, v30);
        boolean v38 = MathUtil.cfr_underscore(param2, param3, v35, v29, v32, v30);
        this.field11607.setTarget(v37 ? 1.0f : 0.0f);
        this.field11608.setTarget(v38 ? 1.0f : 0.0f);
        float v39 = this.field11607.getValue();
        float v40 = this.field11608.getValue();
        if (v36) {
            v41 = ColorUtil.method1853(ThemeColors.method3199(MAIN.INDICATOR), v11);
            v42 = ColorUtil.method1853(ThemeColors.method3199(MAIN.WINDOW_BG), v11);
        } else {
            v43 = ColorUtil.method1853(ThemeColors.method3199(MAIN.FIELD_INACTIVE), field11646 * v11);
            v44 = ColorUtil.method1853(ThemeColors.method3199(MAIN.INDICATOR), v11);
            v41 = ColorUtil.method1850(v43, v44, v39);
            v452 = ColorUtil.method1853(ThemeColors.method3199(MAIN.TEXT_INACTIVE), field11647 * v11);
            v462 = ColorUtil.method1853(ThemeColors.method3199(MAIN.WINDOW_BG), v11);
            v42 = ColorUtil.method1850(v452, v462, v39);
        }
        if (!v36) {
            v43 = ColorUtil.method1853(ThemeColors.method3199(MAIN.INDICATOR), v11);
            v44 = ColorUtil.method1853(ThemeColors.method3199(MAIN.WINDOW_BG), v11);
        } else {
            v452 = ColorUtil.method1853(ThemeColors.method3199(MAIN.FIELD_INACTIVE), field11648 * v11);
            v462 = ColorUtil.method1853(ThemeColors.method3199(MAIN.INDICATOR), v11);
            v43 = ColorUtil.method1850(v452, v462, v40);
            int v47 = ColorUtil.method1853(ThemeColors.method3199(MAIN.TEXT_INACTIVE), field11649 * v11);
            int v48 = ColorUtil.method1853(ThemeColors.method3199(MAIN.WINDOW_BG), v11);
            v44 = ColorUtil.method1850(v47, v48, v40);
        }
        RenderUtil.method397(v34, v29, v32, v30, 1.0f, v41);
        RenderUtil.method392(v34, v29, v32, v30, 1.0f, field11650, v13, v14);
        RenderUtil.method397(v35, v29, v32, v30, 1.0f, v43);
        RenderUtil.method392(v35, v29, v32, v30, 1.0f, field11651, v13, v14);
        String v452_295 = field11652;
        String v462_296 = field11653;
        Fonts.field12491[12].method2711(param1, v452_295, v34 + (v32 - Fonts.field12491[12].method2719(v452_295)) / 2.0f, v29 + (v30 - Fonts.field12491[12].method2718()) / 2.0f + field11654, v42);
        Fonts.field12491[12].method2711(param1, v462_296, v35 + (v32 - Fonts.field12491[12].method2719(v462_296)) / 2.0f, v29 + (v30 - Fonts.field12491[12].method2718()) / 2.0f + field11655, v44);
    }

    public BindPopupState(BooleanSetting param1, chn param2) {
        this.field11615 = param1;
        this.field11603 = param2;
    }

    @Override
    public void method3155(int param1, int param2, int param3) {
        if (!this.field11597 || !this.field11599) {
            return;
        }
        if (param1 == 256 || param1 == 261) {
            this.field11615.cfr_renamed_0(-100);
            this.field11599 = false;
            return;
        }
        this.field11615.cfr_renamed_0(param1);
        this.field11599 = false;
    }

    public static void method3278() {
        field11598 = null;
    }

    public void method3279() {
        this.method3273(!this.field11597);
    }

    static {
        field11656 = 12.0f;
        field11657 = 10.0f;
        field11658 = 10.0f;
        field11600 = 70.0f;
        field11601 = 35.0f;
        field11602 = 15.0f;
        field11604 = 8.0f;
        field11605 = 10.0f;
        field11622 = 0.01f;
        field11623 = 255.0f;
        field11624 = 3.0f;
        field11625 = 3.0f;
        field11626 = 0.6f;
        field11627 = "Клавиша";
        field11628 = 18.0f;
        field11629 = 5.0f;
        field11630 = "...";
        field11631 = "n/a";
        field11632 = 12.0f;
        field11633 = 8.0f;
        field11634 = 12.0f;
        field11635 = 4.0f;
        field11636 = 0.5f;
        field11637 = 0.08f;
        field11638 = 0.7f;
        field11639 = 0.5f;
        field11640 = 1.25f;
        field11641 = 13.0f;
        field11642 = 6.5f;
        field11643 = 1.5f;
        field11644 = 3.0f;
        field11645 = 3.0f;
        field11646 = 0.08f;
        field11647 = 0.7f;
        field11648 = 0.08f;
        field11649 = 0.7f;
        field11650 = 0.5f;
        field11651 = 0.5f;
        field11652 = "Toggle";
        field11653 = "Hold";
        field11654 = 1.25f;
        field11655 = 1.25f;
        field11609 = 18.0f;
        field11610 = 12.0f;
        field11611 = 6.5f;
        field11612 = 1.5f;
        field11613 = 3.0f;
        field11614 = 3.0f;
        field11616 = "...";
        field11617 = "n/a";
        field11618 = 14.0f;
        field11619 = 8.0f;
        field11620 = 12.0f;
        field11621 = 6.0f;
        BindPopupState.method3278();
    }
}



