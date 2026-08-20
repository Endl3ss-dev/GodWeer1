/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.RotationAxis
 *  sLM.4ZXYEo
 *  sg.ec.20
 */
package minced.core;

import minced.module.visual.Particles;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import minced.core.ColorUtil;
import minced.core.MathUtil;
import minced.core.ScissorUtil;
import minced.core.BindPopup;
import minced.core.ColorPickerElement;
import minced.core.BindPopupState;
import minced.core.Easings;
import minced.core.AnimatedFloat;
import minced.core.Fonts;
import minced.core.RenderUtil;
import minced.core.Minced;
import minced.enums.Bloom;
import minced.enums.Eternity;
import minced.manager.Theme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import sLM._4ZXYEo;
import sg.ec._20;

public class GuiThemePanel
{
    static String cfr_renamed_275;
    static String cfr_renamed_276;
    static String cfr_renamed_277;

    static float field922;
    static float field923;
    static float field924;
    static float field925;
    float field926;
    static float field927;
    static float field928;
    boolean field929 = true;
    AnimatedFloat field930;
    static float field931;
    static float field932;
    static float field933;
    static float field934;
    static float field935;
    static float field936;
    static float field937;
    static float field938;
    static float field939;
    boolean field940 = true;
    static float field941;
    static float field942;
    static float field943;
    static float field944;
    static float field945;
    float field946;
    float field947;
    float field948 = field987;
    float field949;
    static float field950;
    static float field951;
    static float field952;
    static float field953;
    static double field954;
    static double field955;
    static float field956;
    static float field957;
    AnimatedFloat field958;
    static String field959;
    static float field960;
    static float field961;
    static String field962;
    static float field963;
    static float field964;
    static float field965;
    static float field966;
    static float field967;
    static double field968;
    static float field969;
    static float field970;
    AnimatedFloat field971;
    AnimatedFloat field972;
    static String field973;
    static float field974;
    static float field975;
    static String field976;
    static float field977;
    static float field978;
    static float field979;
    static float field980;
    static float field981;
    static double field982;
    static float field983;
    static float field984;
    static float field985;
    static float field986;
    static float field987 = 5.0f;
    static float field988 = 5.0f;
    static float field989 = 12.0f;
    static float field990 = 12.0f;
    static float field991 = 10.0f;
    static float field992 = 10.0f;
    Map field993;
    Map field994;
    Map field995;
    Map field996;
    static float field997;
    static float field998;
    static float field999;
    static float field1000;
    static String field1001;
    static float field1002;
    static float field1003;
    static int field1004;
    static float field1005;
    static String field1006;
    static float field1007;
    static int field1008;
    static float field1009;
    static float field1010;
    static float field1011;
    static float field1012;
    static float field1013;
    static float field1014;
    static String field1015;
    static String field1016;
    static float field1017;
    static float field1018;
    static double field1019;
    static double field1020;
    static float field1021;
    static float field1022;
    static float field1023;
    static float field1024;
    static float field1025;
    static float field1026;
    static float field1027;
    static float field1028;
    static float field1029;
    static float field1030;
    static float field1031;
    static float field1032;
    static float field1033;
    static float field1034;
    static double field1035;
    static double field1036;
    static float field1037;
    static int field1038;
    static float field1039;
    static float field1040;
    static float field1041;
    static float field1042;
    static float field1043;
    static float field1044;

    public boolean method309(float param1, float param2, int param3, int param4) {
        float v122;
        float v19;
        int v17;
        if (!MathUtil.cfr_underscore(param1, param2, param3 + 104, param4 + 32, field922, field923)) {
            return false;
        }
        float v5 = param3 + 114;
        float v7 = v5 + field924 - field925;
        float v6 = param4 + 40;
        float v8 = v6 + this.field926;
        if (MathUtil.cfr_underscore(param1, param2, v7, v8 - 2.0f, field927, field928)) {
            this.field929 = !this.field929;
            return true;
        }
        float v9 = this.field930.getValue();
        int v10 = Math.max(0, (Eternity.values().length + 2) / 3);
        float v11 = (float)Math.max(0, v10 - 1) * field931;
        if (v9 > field932) {
            int v122_189 = 0;
            for (Eternity v16 : Eternity.values()) {
                v17 = v122_189 % 3;
                v19 = v5 + (float)(v17 * 110);
                int v18 = v122_189 / 3;
                float v20 = v6 + this.field926 + (float)(v18 * 59) + field933;
                if (MathUtil.cfr_underscore(param1, param2, v19, v20, field934, field935)) {
                    Minced.getInstance().getThemeManager().cfr_renamed_0(v16);
                    return true;
                }
                ++v122_189;
            }
        }
        if (MathUtil.cfr_underscore(param1, param2, v7, (v122 = v6 + this.field926 + (v11 + field936) * v9 + field937) - 2.0f, field938, field939)) {
            this.field940 = !this.field940;
            return true;
        }
        if (this.field940) {
            Theme v13 = Minced.getInstance().getThemeManager();
            v13.cfr_renamed_270().method2028();
            int v14 = this.method317();
            for (int v15 = 0; v15 < v14; ++v15) {
                int v16 = v15 % 8;
                float v18 = v5 + (float)v16 * field941;
                v17 = v15 / 8;
                v19 = v122 + (float)v17 * field942 + field943;
                if (!MathUtil.cfr_underscore(param1, param2, v18, v19, field944, field945)) {
                    continue;
                }
                if (v15 < Bloom.values().length) {
                    v13.cfr_renamed_0(Bloom.values()[v15]);
                    return true;
                }
                if (v15 < Bloom.values().length + v13.method286().size()) {
                    _20 v20 = (_20)v13.method286().get(v15 - Bloom.values().length);
                    v13.method288(v20.id());
                    return true;
                }
                v13.cfr_renamed_271();
                return true;
            }
        }
        return false;
    }

    public void method310(DrawContext param1, int param2, int param3, float param4, float param5, float param6) {
        this.field946 = param5;
        this.field947 = param6;
        float v7 = param2 + 114;
        float v8 = param3 + 40;
        float v9 = this.field926;
        int v10 = ColorUtil.method1853(Minced.getInstance().getThemeManager().method279(), param4);
        if (this.field948 != this.field926) {
            MinecraftClient v11 = MinecraftClient.getInstance();
            this.field948 = MathHelper.clamp((float)this.field948, (float)Math.min(-(v8 + this.field949 + this.field926 - this.field926 - ((float)v11.getWindow().getScaledHeight() / 2.0f + field950) - 1.0f), 0.0f), (float)field951);
            this.field926 = MathUtil.method1870(this.field926, this.field948, field952);
        }
        ScissorUtil.method2621(v7, v8 - field953, field954, field955);
        float v11 = v7 + field956 - field957;
        float v12 = v8 + v9;
        this.field958.setTarget(this.field929 ? 1.0f : 0.0f);
        this.field930.setTarget(this.field929 ? 1.0f : 0.0f);
        float v13 = this.field930.getValue();
        Fonts.I[14].method2711(param1, field959, v7 + field960, v12 - field961, ColorUtil.method1853(-1, param4));
        Fonts.field12502[16].method2711(param1, field962, v7, v12, v10);
        this.method313(param1, v11, v12, this.field958.getValue() * field963, v10);
        int v14 = Eternity.values().length;
        int v15 = Math.max(0, (v14 + 2) / 3);
        float v16 = (float)Math.max(0, v15 - 1) * field964;
        float v17 = (v16 + field965) * v13;
        if (v13 > field966) {
            ScissorUtil.method2621(v7, v8 + v9 + field967, field968, v17);
            int v18 = 0;
            for (Eternity v22 : Eternity.values()) {
                int v23 = v18 % 3;
                int v24 = v18 / 3;
                float v25 = v7 + (float)(v23 * 110);
                float v26 = v8 + v9 + (float)(v24 * 59) + field969;
                this.method315(param1, v22, v25, v26, Minced.getInstance().getThemeManager().cfr_renamed_272() == v22, param4 * v13);
                ++v18;
            }
            ScissorUtil.method2620();
        }
        float v18 = v8 + v9 + v17 + field970;
        this.field971.setTarget(this.field940 ? 1.0f : 0.0f);
        this.field972.setTarget(this.field940 ? 1.0f : 0.0f);
        float v19 = this.field972.getValue();
        Fonts.I[14].method2711(param1, field973, v7 + field974, v18 - field975, ColorUtil.method1853(-1, param4));
        Fonts.field12502[16].method2711(param1, field976, v7, v18, v10);
        this.method313(param1, v11, v18, this.field971.getValue() * field977, v10);
        Theme v20 = Minced.getInstance().getThemeManager();
        v20.cfr_renamed_270().method2028();
        int v21 = Bloom.values().length + v20.method286().size() + 1;
        int v22 = Math.max(0, (v21 + 7) / 8);
        float v23 = (float)Math.max(0, v22 - 1) * field978;
        float v24 = (v23 + field979) * v19;
        if (v19 > field980) {
            ScissorUtil.method2621(v7, v18 + field981, field982, v24);
            for (int v25 = 0; v25 < v21; ++v25) {
                Bloom v30;
                int v26 = v25 % 8;
                int v27 = v25 / 8;
                float v28 = v7 + (float)v26 * field983;
                int v29 = (int)(v18 + (float)v27 * field984 + field985);
                if (v25 < Bloom.values().length) {
                    v30 = Bloom.values()[v25];
                    this.method316(param1, v30.field1079, this.method314(v30.name()), v28, v29, !v20.cfr_renamed_91() && v20.cfr_renamed_273() == v30, param4 * v19, (String)((Object)_4ZXYEo.фЦ("makeConcatWithConstants", new Object[]{}, (String)v30.name())));
                    continue;
                }
                if (v25 < Bloom.values().length + v20.method286().size()) {
                    _20 v31 = (_20)v20.method286().get(v25 - Bloom.values().length);
                    this.method316(param1, v31.texture(), v31.displayName(), v28, v29, v20.cfr_renamed_91() && v31.id().equals(v20.method289()), param4 * v19, (String)((Object)_4ZXYEo.фф("makeConcatWithConstants", new Object[]{}, (String)v31.id())));
                    continue;
                }
                this.method311(param1, v28, v29, param4 * v19);
            }
            ScissorUtil.method2620();
        }
        this.field949 = v17 + v24 + field986;
        ScissorUtil.method2620();
    }

    public GuiThemePanel() {
        this.field926 = field988;
        this.field958 = new AnimatedFloat(1.0f, field989, Easings.field11969);
        this.field971 = new AnimatedFloat(1.0f, field990, Easings.field11969);
        this.field930 = new AnimatedFloat(1.0f, field991, Easings.field11969);
        this.field972 = new AnimatedFloat(1.0f, field992, Easings.field11969);
        this.field993 = new HashMap();
        this.field994 = new HashMap();
        this.field995 = new HashMap();
        this.field996 = new HashMap();
    }

    private void method311(DrawContext param1, float param2, float param3, float param4) {
        int v5 = ColorUtil.method1853(-1, field997 * param4);
        RenderUtil.method397(param2, param3, field998, field999, field1000, v5);
        String v6 = field1001;
        float v7 = Fonts.I[16].method2719(v6);
        float v8 = Fonts.I[16].method2718();
        Fonts.I[16].method2711(param1, v6, param2 + field1002 - v7 / 2.0f, param3 + field1003 - v8 / 2.0f, ColorUtil.method1853(field1004, param4));
    }

    private AnimatedFloat method312(Map param1, String param2) {
        AnimatedFloat v3 = (AnimatedFloat)param1.get(param2);
        if (v3 == null) {
            v3 = new AnimatedFloat(0.0f, field1005, Easings.field11969);
            param1.put(param2, v3);
        }
        return v3;
    }

    private void method313(DrawContext param1, float param2, float param3, float param4, int param5) {
        String v6 = field1006;
        float v7 = Fonts.field12502[16].method2719(v6);
        float v8 = Fonts.field12502[16].method2718();
        float v9 = field1007;
        param1.getMatrices().push();
        param1.getMatrices().translate(param2 + v7 / 2.0f, param3 - v9 + v8 / 2.0f, 0.0f);
        param1.getMatrices().multiply(RotationAxis.POSITIVE_Z.rotationDegrees(param4));
        Fonts.field12502[16].method2711(param1, v6, -v7 / 2.0f, -v8 / 2.0f + v9, param5);
        param1.getMatrices().pop();
    }

    private String method314(String param1) {
        if (param1 == null || param1.isEmpty()) {
            return param1;
        }
        return _4ZXYEo.cfr_renamed_274("makeConcatWithConstants", new Object[]{}, (String)param1.substring(0, 1).toUpperCase(), (String)param1.substring(1).toLowerCase());
    }

    private void method315(DrawContext param1, Eternity param2, float param3, float param4, boolean param5, float param6) {
        int v7 = field1008 | param2.field1081;
        RenderUtil.method397(param3, param4, field1009, field1010, field1011, ColorUtil.method1855(v7, (int)((param5 ? field1012 : field1013) * field1014 * param6)));
        int v8 = v7 >> 16 & 0xFF;
        int v9 = v7 >> 8 & 0xFF;
        int v10 = v7 & 0xFF;
        RenderUtil.method388(param1.getMatrices(), Identifier.of((String)field1015, (String)field1016), param3 + field1017, param4 + field1018, field1019, field1020, new Color(v8, v9, v10, (int)(field1021 * param6)));
    }

    private void method316(DrawContext param1, Identifier param2, String param3, float param4, float param5, boolean param6, float param7, String param8) {
        AnimatedFloat v9 = this.method312(this.field993, param8);
        AnimatedFloat v10 = this.method312(this.field994, param8);
        v9.setTarget(param6 ? 1.0f : 0.0f);
        boolean v11 = MathUtil.cfr_underscore(this.field946, this.field947, param4, param5, field1022, field1023);
        v10.setTarget(v11 ? 1.0f : 0.0f);
        float v12 = v9.getValue();
        float v13 = v10.getValue();
        int v14 = Minced.getInstance().getThemeManager().method279();
        int v15 = ColorUtil.method1855(v14, (int)(field1024 * param7 * v12));
        int v16 = ColorUtil.method1853(-1, (field1025 + field1026 * v13) * param7 * (1.0f - v12));
        RenderUtil.method397(param4, param5, field1027, field1028, field1029, v16);
        RenderUtil.method397(param4, param5, field1030, field1031, field1032, v15);
        RenderUtil.method388(param1.getMatrices(), param2, param4 + field1033, param5 + field1034, field1035, field1036, new Color(255, 255, 255, (int)(field1037 * param7)));
        float v17 = Fonts.I[10].method2719(param3);
        int v18 = ColorUtil.method1850(field1038, -1, v12);
        Fonts.I[10].method2711(param1, param3, param4 + field1039 + field1040 - v17 / 2.0f, param5 + field1041, ColorUtil.method1853(v18, param7));
    }

    private int method317() {
        Theme v1 = Minced.getInstance().getThemeManager();
        return Bloom.values().length + v1.method286().size() + 1;
    }

    public boolean method318(float param1, float param2, double param3, double param5, int param7, int param8) {
        if (!MathUtil.cfr_underscore(param1, param2, param7 + 104, param8 + 32, field1042, field1043)) {
            return false;
        }
        this.field948 = Math.min(this.field948 + (float)(param5 > 0.0 ? 15 : -15), field1044);
        ColorPickerElement.method3219();
        BindPopup.method3169();
        BindPopupState.method3274();
        return true;
    }

    static {
        field1005 = 12.0f;
        field950 = 136.25f;
        field951 = 5.0f;
        field952 = 14.0f;
        field953 = 7.0f;
        field954 = 448.5;
        field955 = 239.5;
        field956 = 448.5f;
        field957 = 128.5f;
        field959 = "Colors";
        field960 = 9.0f;
        field961 = 0.5f;
        field962 = "x";
        field963 = 90.0f;
        field964 = 59.0f;
        field965 = 54.0f;
        field966 = 0.001f;
        field967 = 10.0f;
        field968 = 448.5;
        field969 = 10.0f;
        field970 = 19.0f;
        field973 = "Particles";
        field974 = 9.0f;
        field975 = 0.5f;
        field976 = "b";
        field977 = 90.0f;
        field978 = 41.5f;
        field979 = 36.5f;
        field980 = 0.001f;
        field981 = 10.0f;
        field982 = 448.5;
        field983 = 41.5f;
        field984 = 41.5f;
        field985 = 10.0f;
        cfr_renamed_275 = "p_\u0001";
        cfr_renamed_276 = "c_\u0001";
        field986 = 37.0f;
        field922 = 344.5f;
        field923 = 240.5f;
        field924 = 448.5f;
        field925 = 128.5f;
        field927 = 8.0f;
        field928 = 12.0f;
        field931 = 59.0f;
        field932 = 0.5f;
        field933 = 10.0f;
        field934 = 105.0f;
        field935 = 54.0f;
        field936 = 54.0f;
        field937 = 18.0f;
        field938 = 8.0f;
        field939 = 12.0f;
        field941 = 41.5f;
        field942 = 41.5f;
        field943 = 10.0f;
        field944 = 36.5f;
        field945 = 36.5f;
        field1042 = 344.5f;
        field1043 = 240.5f;
        field1044 = 5.0f;
        field1008 = -16777216;
        field1009 = 105.0f;
        field1010 = 54.0f;
        field1011 = 3.0f;
        field1012 = 0.08f;
        field1013 = 0.03f;
        field1014 = 255.0f;
        field1015 = "minced";
        field1016 = "images/interface/color.png";
        field1017 = 9.5f;
        field1018 = 7.0f;
        field1019 = 96.0;
        field1020 = 48.0;
        field1021 = 255.0f;
        field1022 = 36.5f;
        field1023 = 36.5f;
        field1024 = 20.0f;
        field1025 = 0.02f;
        field1026 = 0.04f;
        field1027 = 36.5f;
        field1028 = 36.5f;
        field1029 = 3.0f;
        field1030 = 36.5f;
        field1031 = 36.5f;
        field1032 = 3.0f;
        field1033 = 10.5f;
        field1034 = 7.0f;
        field1035 = 15.5;
        field1036 = 15.5;
        field1037 = 220.0f;
        field1038 = -8750470;
        field1039 = 0.7f;
        field1040 = 18.25f;
        field1041 = 27.0f;
        field997 = 0.02f;
        field998 = 36.5f;
        field999 = 36.5f;
        field1000 = 3.0f;
        field1001 = "+";
        field1002 = 18.25f;
        field1003 = 18.0f;
        field1004 = -8750470;
        cfr_renamed_277 = "\u0001\u0001";
        field1006 = "z";
        field1007 = 3.0f;
    }
}



