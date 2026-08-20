/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.util.math.MathHelper
 *  org.joml.Vector4f
 *  sLM.4ZXYEo
 *  sg.ec.l
 *  sg.ec.ПЙ$2
 *  sg.ec.pc
 *  sg.ec.pya
 *  sg.ec.ЧК$Ч
 */
package minced.core;

import java.lang.invoke.CallSite;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import minced.core.UserInfo;
import minced.core.ColorUtil;
import minced.core.ColorSetting;
import minced.core.MathUtil;
import minced.core.ColorListSetting;
import minced.core.ScissorUtil;
import minced.core.ThemePalette;
import minced.core.ColorPickerRenderer;
import minced.core.ColorPickerElement;
import minced.core.GuiAnimationUtil;
import minced.core.ChatUtil;
import minced.core.Easings;
import minced.core.AnimatedFloat;
import minced.core.Fonts;
import minced.core.RenderUtil;
import minced.core.Minced;
import minced.enums.MAIN;
import minced.manager.Theme;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.MathHelper;
import org.joml.Vector4f;
import sLM._4ZXYEo;
import sg.ec.l;
import sg.ec.pj;
import sg.ec.pc;
import sg.ec.pya;
import sg.ec.chk;

public class ThemeColors
extends GuiAnimationUtil
implements ColorPickerRenderer {
    static String н5п;
    static String н5Й;
    static String ннп;

    static float field11296;
    static String field11297;
    static List field11298;
    static List field11299;
    static EnumMap field11300;
    List field11301 = new ArrayList();
    AnimatedFloat field11302;
    Map field11303 = new LinkedHashMap();
    pj._2 field11304 = null;
    static boolean field11305;
    static AnimatedFloat field11306;
    LinkedHashMap field11307 = new LinkedHashMap();
    static String field11308;
    static String field11309;
    Theme field11310 = new Theme();
    pj._2 field11311 = null;
    static EnumMap field11312;
    static EnumMap I;
    AnimatedFloat field11313;
    static float field11314;
    static float field11315;
    static float field11316;
    static float field11317;
    AnimatedFloat field11318;
    float field11319;
    float field11320;
    static float field11321;
    float field11322 = 0.0f;
    static float field11323;
    float field11324 = field11323;
    float field11325 = 1.0f;
    static float field11326;
    AnimatedFloat l = new AnimatedFloat(0.0f, field11326, Easings.field11969);
    static float field11327;
    static float field11328;
    static float field11329;
    Map field11330;
    float field11331 = 0.0f;
    static float field11332;
    static float field11333;
    static float field11334;
    static float field11335;
    static String field11336;
    static float field11337;
    static float field11338;
    static float field11339;
    static float field11340;
    static float field11341;
    static float field11342;
    static float field11343;
    static float field11344;
    static float field11345;
    static float field11346;
    static float field11347;
    static String field11348;
    static float field11349;
    static float field11350;
    static float field11351;
    static float field11352;
    float field11353;
    static float field11354;
    static float field11355;
    static float field11356;
    static float field11357;
    static float field11358;
    float field11359;
    static float field11360;
    static float field11361;
    static float field11362;
    static float field11363;
    static float field11364;
    static float field11365;
    static float field11366;
    static String field11367;
    static String field11368;
    static float field11369;
    static float field11370;
    static String field11371;
    static float field11372;
    static String field11373;
    static float field11374;
    static float field11375;
    static float field11376;
    static float field11377;
    static float field11378;
    static float field11379;
    static float field11380;
    static float field11381;
    static float field11382;
    static float field11383;
    static float field11384;
    static String field11385;
    static float field11386;
    static float field11387;
    static String field11388;
    static float field11389;
    static float field11390;
    static float field11391;
    static float field11392;
    static float field11393;
    static float field11394;
    static float field11395;
    static float field11396;
    static float field11397;
    static float field11398;
    static float field11399;
    static float field11400;
    static float field11401;
    static float field11402;
    static float field11403;
    static float field11404;
    static float field11405;
    static float field11406;
    static float field11407;
    static int field11408;
    static int field11409;
    static float field11410;
    static float field11411;
    static float field11412;
    static float field11413;
    static float field11414;
    static float field11415;
    static float field11416;
    static float field11417;
    static float field11418;
    static float field11419;
    static float field11420;
    static float field11421;
    static float field11422;
    static float field11423;
    static float field11424;
    static float field11425;
    static float field11426;
    static float field11427;
    static float field11428;
    static String field11429;
    static float field11430;
    static float field11431;
    static float field11432;
    static float field11433;
    static float field11434;
    static float field11435;
    static float field11436;
    static float field11437;
    static float field11438;
    static float field11439;
    static String field11440;
    static float field11441;
    static float field11442;
    static float field11443;
    static float field11444;
    static double field11445;
    static float field11446;
    static double field11447;
    static float field11448;
    static float field11449;
    static float field11450;
    static float field11451;
    static float field11452;
    static String field11453;
    static String field11454;
    static float field11455;

    public float method3176() {
        Objects.requireNonNull(this);
        return field11296;
    }

    private void method3177() {
        ColorListSetting v1 = new ColorListSetting(field11297);
        field11298.add(v1);
        field11299.add(new pya(v1));
        for (MAIN mAIN : MAIN.values()) {
            field11300.put(mAIN, new ColorSetting(mAIN.method33(), true, 0));
        }
        for (ColorSetting v3 : (Iterable<ColorSetting>)(field11300.values())) {
            v3.setChangeListener(this::method3207);
        }
        this.method3216();
        for (MAIN mAIN : MAIN.values()) {
            this.field11301.add(new l((ColorSetting)field11300.get(mAIN)));
        }
    }

    public float method3178() {
        Objects.requireNonNull(this);
        return 1.0f;
    }

    public AnimatedFloat method3179() {
        return this.field11302;
    }

    public Map method3180() {
        return this.field11303;
    }

    public void method3181(String param1, int[] param2, boolean param3) {
        for (int v4 = 0; v4 < Math.min(param2.length, MAIN.values().length); ++v4) {
            ColorSetting v5 = (ColorSetting)field11300.get(MAIN.values()[v4]);
            Runnable v6 = v5.getChangeListener();
            v5.setChangeListener(null);
            v5.setValue(param2[v4]);
            v5.setChangeListener(v6);
        }
        this.field11304 = new pj._2(param1, param2, UserInfo.method1841(), param3);
        field11305 = false;
        field11306.setValue(1.0f);
    }

    public LinkedHashMap method3182() {
        return this.field11307;
    }

    public void method3183(String param1, int[] param2, boolean param3) {
        if (!this.field11303.containsKey(param1) && !this.field11307.containsKey(param1)) {
            param1 = field11308;
            param2 = (int[])(this.field11303.getOrDefault(param1, (int[])this.field11303.values().iterator().next()));
            param3 = false;
        }
        for (int v4 = 0; v4 < Math.min(param2.length, MAIN.values().length); ++v4) {
            ColorSetting v5 = (ColorSetting)field11300.get(MAIN.values()[v4]);
            Runnable v6 = v5.getChangeListener();
            v5.setChangeListener(null);
            v5.setValue(param2[v4]);
            v5.setChangeListener(v6);
        }
        this.field11304 = new pj._2(param1, param2, field11309, param3);
        field11305 = false;
        field11306.setValue(1.0f);
    }

    private void method3184() {
        this.field11310.method283().forEach(param1 -> {
            chk.ch v2 = this.field11310.cfr_renamed_0((String)param1);
            if (v2 != null && v2.Ц西() != null && v2.к() != null) {
                this.field11307.put(v2.Ц西(), v2.к());
            }
        });
    }

    private void method3185(int[] param1, String param2, boolean param3) {
        this.field11311 = this.field11304;
        MAIN[] v4 = MAIN.values();
        ColorPickerElement.method3219();
        int v62 = v4.length;
        for (int v7 = 0; v7 < v62; ++v7) {
            MAIN v8 = v4[v7];
            field11312.put(v8, (Integer)((ColorSetting)field11300.get(v8)).getValue());
        }
        int v9 = 0;
        while (v9 < Math.min(param1.length, v4.length)) {
            ColorSetting v62_311 = (ColorSetting)field11300.get(v4[v9]);
            Runnable v10 = v62_311.getChangeListener();
            v62_311.setChangeListener(null);
            v62_311.setValue(param1[v9]);
            v62_311.setChangeListener(v10);
            I.put(v4[v9], param1[v9]);
            ++v9;
        }
        field11305 = true;
        field11306.setValue(0.0f);
        field11306.setTarget(1.0f);
        this.field11304 = new pj._2(param2, param1, UserInfo.method1841(), param3);
    }

    public void method3186(pj._2 param1) {
        this.field11311 = param1;
    }

    public AnimatedFloat method3188() {
        return this.field11313;
    }

    private void method3189(DrawContext param1, float param2, float param3) {
        float v4 = this.method3256() + this.l() - field11314;
        float v5 = this.method3264() + field11315;
        float v6 = this.method3258() - field11316;
        float v7 = field11317;
        this.field11318.setTarget(this.field11319);
        this.field11320 = this.field11318.getValue();
        int v8 = this.field11303.size() + this.field11307.size();
        this.field11322 = (float)v8 * field11321 - 2.0f;
        if (this.field11322 > v6) {
            this.field11319 = MathHelper.clamp((float)this.field11319, (float)(-(this.field11322 - v6)), (float)0.0f);
            this.field11320 = MathHelper.clamp((float)this.field11320, (float)(-(this.field11322 - v6)), (float)0.0f);
        } else {
            this.field11320 = 0.0f;
            this.field11319 = 0.0f;
        }
        ScissorUtil.method2621(v5, v4, v6, v7);
        int v9 = this.method3205(param1, param2, param3, this.field11303, 0, v5, v4, false);
        this.method3205(param1, param2, param3, this.field11307, v9, v5, v4, false);
        ScissorUtil.method2620();
        v9 = this.method3205(param1, param2, param3, this.field11303, 0, v5, v4, true);
        this.method3205(param1, param2, param3, this.field11307, v9, v5, v4, true);
    }

    public ThemeColors() {
        this.field11318 = new AnimatedFloat(0.0f, field11327, Easings.field11969);
        this.field11302 = new AnimatedFloat(0.0f, field11328, Easings.field11952);
        this.field11313 = new AnimatedFloat(0.0f, field11329, Easings.field11952);
        this.field11330 = new HashMap();
        this.method3177();
        this.method3184();
    }

    public void method3190(float param1) {
        this.field11322 = param1;
    }

    @Override
    public void method3167(DrawContext param1, float param2, float param3, float param4) {
        for (l v6 : (Iterable<l>)(this.field11301)) {
            v6.х(param1, param2, param3, param4);
        }
    }

    @Override
    public void method3154(float param1, float param2, int param3) {
        float v18;
        float v17;
        float v7;
        float v6;
        float v522;
        for (pya v522_384 : (Iterable<pya>)(field11299)) {
            v522_384.cfr_renamed_136(param1, param2, param3);
        }
        float v4 = this.method3264() + field11332;
        float v5 = this.method3256() + this.l() - field11333;
        if (MathUtil.cfr_underscore(param1, param2, v4, v5, v6 = this.method3258() - field11334, v7 = field11335) && param3 == 0) {
            if (this.field11302.isAnimating()) {
                return;
            }
            String v8 = (String)((ColorListSetting)field11298.get(0)).getValue();
            if (v8 == null || v8.trim().isEmpty()) {
                ChatUtil.method3308(field11336, new Object[0]);
                return;
            }
            this.field11302.setValue(0.0f);
            this.field11302.setTarget(1.0f);
            this.method3213();
            return;
        }
        int v8 = 0;
        ArrayList v9 = new ArrayList();
        v9.addAll(this.field11303.entrySet());
        v9.addAll(this.field11307.entrySet());
        float v10 = this.method3256() + this.l() - field11337;
        float v11 = this.method3264() + field11338;
        float v12 = this.method3258() - field11339;
        float v13 = field11340;
        if (MathUtil.cfr_underscore(param1, param2, v11, v10, v12, v13)) {
            for (Map.Entry entry : (Iterable<java.util.Map.Entry>)(v9)) {
                v17 = v11 + field11341 * (float)v8 + field11342 + this.field11320;
                v18 = v10 + field11343;
                if (MathUtil.cfr_underscore(param1, param2, v17 - field11344, v18 - field11345, field11346, field11347)) {
                    boolean v19 = this.field11307.containsKey(entry.getKey());
                    if (param3 == 0) {
                        this.method3185((int[])entry.getValue(), (String)entry.getKey(), v19);
                    } else if (param3 == 1 && v19) {
                        String v20 = (String)entry.getKey();
                        String v21 = v20.replace(field11348, "");
                        this.field11310.method277(v21);
                        this.field11307.remove(v20);
                        if (this.field11304 != null && this.field11304.кн().equals(v20)) {
                            ArrayList v22 = new ArrayList(this.field11303.entrySet());
                            v22.addAll(this.field11307.entrySet());
                            int v23 = Math.max(0, v8 - 1);
                            if (v23 < v22.size()) {
                                Map.Entry v24 = (Map.Entry)v22.get(v23);
                                this.method3185((int[])v24.getValue(), (String)v24.getKey(), this.field11307.containsKey(v24.getKey()));
                            }
                        }
                    }
                    return;
                }
                ++v8;
            }
        }
        if (this.method3192()) {
            return;
        }
        this.method3264();
        float v15 = this.method3256() + field11349;
        this.method3258();
        v17 = this.l() - field11350;
        v18 = v15 + v17;
        float v19 = this.method3256() + field11351;
        for (l v21 : (Iterable<l>)(this.field11301)) {
            float v22 = this.method3264() + field11352;
            float v23 = v19 + this.field11353 + 1.0f;
            float v24 = this.method3258() - field11354;
            float v25 = v21.н() > (Float)0.0f ? v21.н() : field11355;
            float v26 = v23 - field11356;
            float v27 = v26 + v25;
            v21.cfr_renamed_168(v22);
            v21.С(v23);
            v21.ч(v24);
            if (param2 >= v15 && param2 <= v18 && v27 >= v15 && v26 <= v18 && MathUtil.cfr_underscore(param1, param2, v22, v23 - field11357, v24, v25 + field11358)) {
                v21.cfr_renamed_136(param1, param2, param3);
                break;
            }
            v19 += v25 + 1.0f;
        }
    }

    public Map method3191() {
        return this.field11330;
    }

    @Override
    public void method3157(char param1, int param2) {
        for (pya v4 : (Iterable<pya>)(field11299)) {
            v4.У(param1, param2);
        }
    }

    private boolean method3192() {
        return this.field11304 != null && !((Boolean)this.field11304.ЪО());
    }

    @Override
    public void method3193(float param1) {
        this.field11331 = param1;
    }

    @Override
    public float method3194() {
        return this.field11359;
    }

    public float method3195() {
        return this.field11322;
    }

    public AnimatedFloat method3196() {
        return this.field11318;
    }

    public void method3197(float param1) {
        this.field11319 = param1;
    }

    @Override
    public void method3161(DrawContext param1, float param2, float param3, float param4) {
        float v14;
        float v13;
        float v122;
        this.l.setTarget(this.field11359);
        this.field11353 = this.l.getValue();
        if (this.field11302.getTarget() == 1.0f && this.field11302.isFinished()) {
            this.field11302.setTarget(0.0f);
        }
        if (!this.field11302.isFinished()) {
            this.field11302.setTarget(this.field11302.getTarget());
        }
        if (field11305) {
            field11306.setTarget(1.0f);
            if (field11306.isFinished() && field11306.getValue() >= 1.0f) {
                field11305 = false;
            }
        }
        float v5 = this.method3256() + field11360;
        float v6 = this.l() - field11361;
        float cfr_ignored_0 = v5 + v6;
        if (this.field11331 > v6) {
            float v8 = -this.field11331 + v6 + field11362;
            if (v8 > 0.0f) {
                v8 = 0.0f;
            }
            this.field11359 = MathHelper.clamp((float)this.field11359, (float)v8, (float)0.0f);
            this.field11353 = MathHelper.clamp((float)this.field11353, (float)v8, (float)0.0f);
        } else {
            this.field11353 = 0.0f;
            this.field11359 = 0.0f;
        }
        RenderUtil.method390(this.method3264(), this.method3256(), this.method3258(), this.l(), field11363, ThemeColors.method3199(MAIN.HEADER), 1.0f);
        RenderUtil.method403(this.method3264(), this.method3256(), this.method3258(), field11364, new Vector4f(field11365, 0.0f, field11366, 0.0f), ThemeColors.method3199(MAIN.WINDOW_BG), 1.0f);
        float cfr_ignored_1 = Fonts.field12504[18].method2719(field11367) + Fonts.field12494[18].method2719(field11368);
        int v9 = (int)(this.method3264() + field11369);
        int v10 = (int)(this.method3256() + field11370);
        Fonts.field12504[20].method2711(param1, field11371, v9 - 1, (float)v10 + field11372, ThemeColors.method3199(MAIN.HEADER_TEXT));
        Fonts.I[16].method2711(param1, field11373, (float)v9 + field11374, (float)v10 - field11375, ThemeColors.method3199(MAIN.HEADER_TEXT));
        for (pya v122_542 : (Iterable<pya>)(field11299)) {
            v122_542.cfr_renamed_168(this.method3264() + field11376);
            v122_542.С(this.method3256() + this.l() - field11377);
            v122_542.ч(this.method3258() - field11378);
            v122_542.cfr_renamed_136(param1, param2, param3, param4);
        }
        float v11 = this.method3264() + field11379;
        boolean v15 = MathUtil.cfr_underscore(param2, param3, v11, v122 = this.method3256() + this.l() - field11380, v13 = this.method3258() - field11381, v14 = field11382);
        this.field11313.setTarget(v15 ? 1.0f : 0.0f);
        float v16 = this.field11313.getValue();
        float v17 = this.field11302.getValue();
        int v18 = ThemeColors.method3199(MAIN.BUTTON_INACTIVE);
        int v19 = ThemeColors.method3199(MAIN.INDICATOR);
        int v20 = ColorUtil.method1850(v18, v19, v16);
        int v21 = ColorUtil.method1850(v20, ThemeColors.method3199(MAIN.BUTTON_INACTIVE), v17);
        RenderUtil.method397(v11, v122, v13, v14, 2.0f, v21);
        RenderUtil.method392(v11, v122, v13, v14, field11383, field11384, ThemeColors.method3199(MAIN.OUTLINE), ThemeColors.method3214(MAIN.OUTLINE));
        float v22 = v11 + (v13 - Fonts.field12491[12].method2719(field11385)) / 2.0f;
        float v23 = v122 + field11386;
        int v24 = ColorUtil.method1853(ThemeColors.method3199(MAIN.TEXT_INACTIVE), field11387);
        int v25 = ThemeColors.method3199(MAIN.WINDOW_BG);
        int v26 = ColorUtil.method1850(v24, v25, v16);
        int v27 = ColorUtil.method1850(v26, ThemeColors.method3199(MAIN.TEXT_INACTIVE), v17);
        Fonts.field12491[12].method2711(param1, field11388, v22, v23, v27);
        float v28 = this.method3256() + field11389;
        float v29 = this.method3264();
        float v30 = this.method3258();
        float v31 = v5 + v6;
        ScissorUtil.method2621(v29, v5, v30, v6);
        for (l v33 : (Iterable<l>)(this.field11301)) {
            v33.cfr_renamed_168(this.method3264() + field11390);
            v33.С(v28 + this.field11353 + 1.0f);
            v33.ч(this.method3258() - field11391);
            float v34 = v33.н() > (Float)0.0f ? v33.н() : field11392;
            float v35 = v33.ч() - (Float)field11393;
            float v36 = v35 + v34;
            if (v36 >= v5 && v35 <= v31) {
                RenderUtil.method397(v33.С(), v33.ч() - field11394, v33.з() - field11395, v34, field11396, ColorUtil.method1850(ThemeColors.method3199(MAIN.FIELD), ThemeColors.method3199(MAIN.FIELD_INACTIVE), 1.0f));
                RenderUtil.method392(v33.С(), v33.ч() - field11397, v33.з() - field11398, v34, field11399, field11400, ThemeColors.method3199(MAIN.OUTLINE), ThemeColors.method3214(MAIN.OUTLINE));
                v33.cfr_renamed_136(param1, param2, param3, param4);
            }
            v28 += v34 + 1.0f;
        }
        this.field11331 = v28 - (this.method3256() + field11401);
        ScissorUtil.method2620();
        this.method3189(param1, param2, param3);
        if (this.field11331 > v6) {
            float v32 = Math.max(field11402, v6 / this.field11331 * this.l() - field11403);
            float v33 = -this.field11353 / (this.field11331 - v6) * (this.l() - field11404 - v32);
            RenderUtil.method397(this.method3264() + this.method3258() - field11405, this.method3256() + field11406 + v33, 2.0f, v32 - field11407, 1.0f, ThemeColors.method3199(MAIN.SCROLL_BAR));
        }
    }

    public float method3198() {
        return this.field11353;
    }

    public static int method3199(MAIN param0) {
        if (Minced.getInstance() != null && Minced.getInstance().getThemeManager() != null) {
            int n;
            switch (ThemePalette.field9501[param0.ordinal()]) {
                default: {
                    throw new MatchException(null, null);
                }
                case 1: 
                case 2: 
                case 3: 
                case 4: 
                case 5: 
                case 6: {
                    n = Minced.getInstance().getThemeManager().method276();
                    break;
                }
                case 7: 
                case 8: 
                case 9: {
                    n = ColorUtil.method1855(-1, 60);
                    break;
                }
                case 10: 
                case 11: 
                case 12: 
                case 13: {
                    n = field11408;
                    break;
                }
                case 14: 
                case 15: 
                case 16: 
                case 17: 
                case 18: 
                case 19: {
                    n = -1;
                    break;
                }
                case 20: 
                case 21: 
                case 22: 
                case 23: 
                case 24: {
                    n = field11409;
                }
            }
            return n;
        }
        if (field11305 && field11306.getValue() < 1.0f) {
            int v1 = (Integer)(field11312.getOrDefault(param0, (Integer)((ColorSetting)field11300.get(param0)).getValue()));
            int v2 = (Integer)(I.getOrDefault(param0, (Integer)((ColorSetting)field11300.get(param0)).getValue()));
            return ColorUtil.method1850(v1, v2, field11306.getValue());
        }
        return (Integer)((ColorSetting)field11300.get(param0)).getValue();
    }

    public pj._2 method3200() {
        return this.field11311;
    }

    @Override
    public float method3201() {
        return this.field11331;
    }

    @Override
    public void method3162(int param1, int param2, int param3) {
        for (pya v5 : (Iterable<pya>)(field11299)) {
            v5.У(param1, param2, param3);
        }
    }

    public void method3202(float param1) {
        this.field11353 = param1;
    }

    public float method3203() {
        return this.field11320;
    }

    public void method3204(float param1) {
        this.field11320 = param1;
    }

    private int method3205(DrawContext param1, float param2, float param3, Map param4, int param5, float param6, float param7, boolean param8) {
        for (Map.Entry v10 : (Iterable<java.util.Map.Entry>)(param4.entrySet())) {
            boolean v20;
            float v11 = param6 + field11410 * (float)param5 + field11411 + this.field11320;
            float v12 = param7 + field11412;
            float v13 = this.method3264() + field11413;
            float v14 = this.method3256() + this.l() - field11414;
            float v15 = this.method3258() - field11415;
            float v16 = field11416;
            float v17 = v13 + v15;
            boolean v18 = v11 + field11417 >= v13 && v11 - field11418 <= v17;
            boolean v19 = MathUtil.cfr_underscore(param2, param3, v13, v14, v15, v16);
            boolean bl = v20 = v18 && v19 && MathUtil.cfr_underscore(param2, param3, v11 - field11419, v12 - field11420, field11421, field11422);
            if (!param8) {
                if (v18) {
                    RenderUtil.method397(v11 - field11423, v12 - field11424, field11425, field11426, field11427, ((int[])v10.getValue())[MAIN.LOGO.ordinal()]);
                    if (this.field11304 != null && ((String)v10.getKey()).equals(this.field11304.кн())) {
                        float v21 = field11428;
                        RenderUtil.method397(v11 - v21, v12 - v21, v21 * 2.0f, v21 * 2.0f, v21 - 1.0f, ColorUtil.method1852(0, 0, 0, 255));
                    }
                }
            } else {
                AnimatedFloat v21 = (AnimatedFloat)(this.field11330.computeIfAbsent((String)v10.getKey(), param0 -> new AnimatedFloat(0.0f, field11439, Easings.field11952)));
                v21.setTarget(v20 ? 1.0f : 0.0f);
                float v22 = v21.getValue();
                if (v18 && v19 && v22 > 0.0f) {
                    String v23 = (String)v10.getKey();
                    String v24 = param4 == this.field11303 ? field11429 : UserInfo.method1841();
                    float v25 = Fonts.field12491[12].method2719(v23);
                    float v26 = Fonts.field12491[12].method2719((String)((Object)_4ZXYEo.cfr_renamed_278("makeConcatWithConstants", new Object[]{}, (String)v24)));
                    float v27 = Math.max(v25, v26);
                    float v28 = v11 - field11430 + 2.0f;
                    float v29 = v12 - field11431 - field11432;
                    RenderUtil.method390(v28 - 2.0f, v29 - field11433, v27 + field11434, field11435, 2.0f, ThemeColors.method3199(MAIN.HEADER), v22);
                    int v30 = ColorUtil.method1855(ThemeColors.method3199(MAIN.TEXT), (int)(v22 * field11436));
                    Fonts.field12491[12].method2711(param1, v23, v28, v29 - field11437, v30);
                    Fonts.field12491[12].method2711(param1, (String)((Object)_4ZXYEo.cfr_renamed_279("makeConcatWithConstants", new Object[]{}, (String)v24)), v28, v29 + field11438, v30);
                }
            }
            ++param5;
        }
        return param5;
    }

    private void method3207() {
        if (this.field11304 != null && (Boolean)this.field11304.ЪО()) {
            int[] v1 = new int[field11300.size()];
            MAIN[] v2 = MAIN.values();
            for (int v3 = 0; v3 < v2.length; ++v3) {
                v1[v3] = (Integer)((ColorSetting)field11300.get(v2[v3])).getValue();
            }
            String v3 = UserInfo.method1841();
            String v4 = this.field11304.кн().replace(field11440, "");
            this.field11304 = new pj._2(this.field11304.кн(), v1, v3, true);
            this.field11307.put(this.field11304.кн(), v1);
            this.field11310.cfr_renamed_0(v4, this.field11304, v3);
        }
    }

    @Override
    public AnimatedFloat method3208() {
        return this.l;
    }

    @Override
    public boolean method3165(float param1, float param2, int param3) {
        if (this.method3192()) {
            return false;
        }
        for (l v5 : (Iterable<l>)(this.field11301)) {
            if (!((Boolean)v5.У(param1, param2, param3))) continue;
            return true;
        }
        return false;
    }

    public Theme method3209() {
        return this.field11310;
    }

    public pj._2 method3210() {
        return this.field11304;
    }

    @Override
    public void method3211(float param1) {
        this.field11359 = param1;
    }

    public List cfr_renamed_4() {
        return this.field11301;
    }

    public void method3212(pj._2 param1) {
        this.field11304 = param1;
    }

    @Override
    public boolean method3164(double param1, double param3, double param5, double param7) {
        if (MathUtil.cfr_underscore((float)param1, (float)param3, this.method3264(), this.method3256(), this.method3258(), this.l())) {
            float v12;
            float v11;
            float v9 = this.method3256() + this.l() - field11441;
            float v10 = this.method3264() + field11442;
            if (MathUtil.cfr_underscore((float)param1, (float)param3, v10, v9, v11 = this.method3258() - field11443, v12 = field11444)) {
                if (this.field11322 > v11) {
                    boolean v14;
                    float v13 = this.field11319;
                    this.field11319 += (float)(param7 * field11445);
                    this.field11319 = MathHelper.clamp((float)this.field11319, (float)(-(this.field11322 - v11)), (float)0.0f);
                    boolean bl = v14 = this.field11319 != v13;
                    if (v14) {
                        ColorPickerElement.method3219();
                    }
                    return v14;
                }
            } else {
                boolean v16;
                float v13 = this.l() - field11446;
                float v14 = this.field11359;
                this.method3211((float)((double)this.method3194() + param7 * field11447));
                float v15 = -this.field11331 + v13 + field11448;
                if (v15 > 0.0f) {
                    v15 = 0.0f;
                }
                this.field11359 = MathHelper.clamp((float)this.field11359, (float)v15, (float)0.0f);
                boolean bl = v16 = this.field11359 != v14;
                if (v16) {
                    ColorPickerElement.method3219();
                }
                return v16;
            }
        }
        return super.method3164(param1, param3, param5, param7);
    }

    private void method3213() {
        String v1 = (String)((ColorListSetting)field11298.get(0)).getValue();
        if (v1 == null || v1.trim().isEmpty()) {
            return;
        }
        String v2 = _4ZXYEo.cfr_renamed_280("makeConcatWithConstants", new Object[]{}, (String)v1);
        if (this.field11303.containsKey(v2) || this.field11307.containsKey(v2)) {
            return;
        }
        String v3 = UserInfo.method1841();
        int[] v4 = new int[MAIN.values().length];
        MAIN[] v5 = MAIN.values();
        for (int v6 = 0; v6 < v5.length; ++v6) {
            v4[v6] = (Integer)((ColorSetting)field11300.get(v5[v6])).getValue();
        }
        this.field11311 = this.field11304;
        this.field11304 = new pj._2((String)((Object)v2), v4, v3, true);
        this.field11310.cfr_renamed_0(v1, this.field11304, v3);
        this.field11307.put(v2, v4);
        ((ColorListSetting)field11298.get(0)).method2238("");
        ((pya)field11299.get(0)).cfr_renamed_281();
    }

    public static float method3214(MAIN param0) {
        if (Minced.getInstance() != null && Minced.getInstance().getThemeManager() != null) {
            float f;
            switch (ThemePalette.field9501[param0.ordinal()]) {
                default: {
                    throw new MatchException(null, null);
                }
                case 1: 
                case 2: 
                case 3: 
                case 4: 
                case 5: 
                case 6: 
                case 14: 
                case 15: 
                case 16: 
                case 17: 
                case 18: 
                case 19: {
                    f = field11449;
                    break;
                }
                case 10: 
                case 11: 
                case 12: 
                case 13: {
                    f = field11450;
                    break;
                }
                case 20: 
                case 21: 
                case 22: 
                case 23: 
                case 24: {
                    f = field11451;
                    break;
                }
                case 7: 
                case 8: 
                case 9: {
                    f = field11452;
                }
            }
            return f;
        }
        if (field11305 && field11306.getValue() < 1.0f) {
            int v1 = (Integer)(field11312.getOrDefault(param0, (Integer)((ColorSetting)field11300.get(param0)).getValue()));
            int v2 = (Integer)(I.getOrDefault(param0, (Integer)((ColorSetting)field11300.get(param0)).getValue()));
            int v3 = ColorUtil.method1850(v1, v2, field11306.getValue());
            return v3 >> 24 & 0xFF;
        }
        return ((ColorSetting)field11300.get(param0)).method1861();
    }

    public float method3215() {
        return this.field11319;
    }

    private void method3216() {
        this.field11303.clear();
        this.field11303.putAll(pc.ш());
        String v1 = field11453;
        int[] v2 = (int[])this.field11303.get(v1);
        if (v2 == null && !this.field11303.isEmpty()) {
            Map.Entry v3 = (Map.Entry)(this.field11303.entrySet().iterator().next());
            v1 = (String)v3.getKey();
            v2 = (int[])v3.getValue();
        }
        this.field11304 = new pj._2(v1, v2, field11454, false);
        MAIN[] v4 = MAIN.values();
        for (int v5 = 0; v5 < Math.min(v2.length, v4.length); ++v5) {
            ((ColorSetting)field11300.get(v4[v5])).setValue(v2[v5]);
        }
    }

    public static void method3217() {
        field11300 = new EnumMap(MAIN.class);
        field11298 = new ArrayList();
        field11299 = new ArrayList();
        field11312 = new EnumMap(MAIN.class);
        I = new EnumMap(MAIN.class);
        field11306 = new AnimatedFloat(1.0f, field11455, Easings.field11969);
        field11305 = false;
    }

    @Override
    public void method3158(float param1, float param2, int param3) {
        for (l v5 : (Iterable<l>)(this.field11301)) {
            if (!v5.ch().method3233()) continue;
            v5.ch().method3240();
        }
    }

    static {
        field11305 = false;
        field11323 = 4.5f;
        field11326 = 10.0f;
        field11327 = 10.0f;
        field11328 = 6.0f;
        field11329 = 10.0f;
        field11297 = "Название";
        field11440 = "Имя: ";
        field11408 = -16119286;
        field11409 = -8750470;
        field11449 = 255.0f;
        field11450 = 245.0f;
        field11451 = 122.0f;
        field11452 = 60.0f;
        field11453 = "Имя: Синий минимализм";
        field11454 = "Unknown";
        field11314 = 57.0f;
        field11315 = 6.0f;
        field11316 = 12.0f;
        field11317 = 20.0f;
        field11321 = 10.0f;
        field11410 = 10.0f;
        field11411 = 4.0f;
        field11412 = 7.0f;
        field11413 = 6.0f;
        field11414 = 57.0f;
        field11415 = 12.0f;
        field11416 = 20.0f;
        field11417 = 4.5f;
        field11418 = 4.5f;
        field11419 = 4.5f;
        field11420 = 4.5f;
        field11421 = 9.0f;
        field11422 = 9.0f;
        field11423 = 4.5f;
        field11424 = 4.5f;
        field11425 = 9.0f;
        field11426 = 9.0f;
        field11427 = 3.5f;
        field11428 = 2.4750001f;
        field11429 = "tuskevich";
        н5п = "Автор: \u0001";
        field11430 = 4.5f;
        field11431 = 4.5f;
        field11432 = 12.0f;
        field11433 = 4.0f;
        field11434 = 4.0f;
        field11435 = 16.0f;
        field11436 = 255.0f;
        field11437 = 0.5f;
        н5Й = "Автор: \u0001";
        field11438 = 6.5f;
        field11360 = 27.0f;
        field11361 = 85.0f;
        field11362 = 10.0f;
        field11363 = 5.0f;
        field11364 = 22.5f;
        field11365 = 5.0f;
        field11366 = 5.0f;
        field11367 = "f";
        field11368 = "Themes";
        field11369 = 8.0f;
        field11370 = 9.0f;
        field11371 = "f";
        field11372 = 0.5f;
        field11373 = "Themes";
        field11374 = 12.5f;
        field11375 = 0.5f;
        field11376 = 2.5f;
        field11377 = 50.0f;
        field11378 = 5.0f;
        field11379 = 5.0f;
        field11380 = 22.5f;
        field11381 = 10.0f;
        field11382 = 17.0f;
        field11383 = 2.5f;
        field11384 = 0.6f;
        field11385 = "Создать";
        field11386 = 7.0f;
        field11387 = 0.48f;
        field11388 = "Создать";
        field11389 = 30.0f;
        field11390 = 5.0f;
        field11391 = 10.0f;
        field11392 = 14.0f;
        field11393 = 4.0f;
        field11394 = 4.0f;
        field11395 = 0.5f;
        field11396 = 3.0f;
        field11397 = 4.0f;
        field11398 = 0.5f;
        field11399 = 3.0f;
        field11400 = 0.6f;
        field11401 = 14.0f;
        field11402 = 20.0f;
        field11403 = 82.5f;
        field11404 = 80.0f;
        field11405 = 4.0f;
        field11406 = 26.0f;
        field11407 = 1.5f;
        field11332 = 5.0f;
        field11333 = 22.5f;
        field11334 = 10.0f;
        field11335 = 17.0f;
        field11336 = "Введите название темы!";
        field11337 = 57.0f;
        field11338 = 6.0f;
        field11339 = 12.0f;
        field11340 = 20.0f;
        field11341 = 10.0f;
        field11342 = 4.0f;
        field11343 = 7.0f;
        field11344 = 4.5f;
        field11345 = 4.5f;
        field11346 = 9.0f;
        field11347 = 9.0f;
        field11348 = "Имя: ";
        field11349 = 27.0f;
        field11350 = 85.0f;
        field11351 = 30.0f;
        field11352 = 5.0f;
        field11354 = 10.0f;
        field11355 = 14.0f;
        field11356 = 4.0f;
        field11357 = 4.0f;
        field11358 = 4.0f;
        ннп = "Имя: \u0001";
        field11308 = "Имя: Синий минимализм";
        field11309 = "AutoConfig";
        field11441 = 57.0f;
        field11442 = 6.0f;
        field11443 = 12.0f;
        field11444 = 20.0f;
        field11445 = 10.0;
        field11446 = 85.0f;
        field11447 = 10.0;
        field11448 = 10.0f;
        field11296 = 4.5f;
        field11439 = 15.0f;
        field11455 = 8.0f;
        ThemeColors.method3217();
    }
}



