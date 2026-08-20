/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.util.Identifier
 *  sLM.4ZXYEo
 */
package minced.core;

import java.lang.invoke.CallSite;
import minced.core.UserInfo;
import minced.core.ColorUtil;
import minced.core.MathUtil;
import minced.core.SettingRenderer;
import minced.core.Fonts;
import minced.core.RenderUtil;
import minced.core.Minced;
import minced.enums.Combat;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import sLM._4ZXYEo;

public class GuiHeader
implements SettingRenderer {
    @Override
    public float method2935(Setting p0) { return 0.0f; }

    @Override
    public float method2938(DrawContext p0, Setting p1, float p2, float p3, float p4, float p5, int p6, Object p7) { return 0.0f; }

    @Override
    public RecordClass234 method2939(Setting p0, float p1, float p2, float p3, float p4, float p5, int p6, SliderSetting p7, Object p8) { return null; }

    static String cfr_renamed_252;

    static String field10273 = "a";
    static float field10274 = 16.0f;
    static float field10275 = 14.0f;
    static float field10276 = 8.0f;
    static float field10277 = 12.0f;
    static String field10278 = "a";
    static String field10279 = "Minced";
    static float field10280 = 11.5f;
    static String field10281 = "Minecraft 1.21.4";
    static float field10282 = 19.0f;
    static int field10283 = -8750470;
    static int field10284;
    static int field10285;
    static String field10286;
    static float field10287;
    static String field10288;
    static String field10289;
    static String field10290;
    static float field10291;
    static int field10292;
    static String field10293;
    static float field10294;
    static String field10295 = "minced";
    static String field10296 = "images/interface/avatar.png";
    static float field10297;
    static float field10298;
    static float field10299;
    static float field10300;
    static float field10301;
    static float field10302;
    static float field10303;
    static float field10304;
    static int field10305;
    static float field10306;
    static float field10307;
    static float field10308;
    static float field10309;
    static int field10310;
    static float field10311;
    static float field10312;
    static float field10313;
    static float field10314;
    static int field10315;
    static float field10316;
    static float field10317;
    static float field10318;
    static int field10319;
    static float field10320;
    static float field10321;

    public void method2974(DrawContext param1, int param2, int param3, float param4, float param5, boolean param6, Combat param7) {
        int v8 = ColorUtil.method1853(Minced.getInstance().getThemeManager().method279(), param4);
        RenderUtil.method391(param2, param3 + 3, Fonts.field12496[32].method2719(field10273) + field10274, Fonts.field12496[32].method2718() + field10275, field10276, v8, v8, v8, v8, param4, field10277);
        Fonts.field12496[32].method2711(param1, field10278, param2 + 8, param3 + 11, v8);
        Fonts.I[15].method2711(param1, field10279, param2 + 29, (float)param3 + field10280, ColorUtil.method1853(-1, param4));
        Fonts.I[12].method2711(param1, field10281, param2 + 29, (float)param3 + field10282, ColorUtil.method1853(field10283, param4));
        int v9 = param6 ? ColorUtil.method1853(field10284, param4) : ColorUtil.method1853(field10285, param4);
        Fonts.field12502[16].method2711(param1, field10286, param2 + 88, (float)param3 + field10287, v9);
        int v10 = ColorUtil.method1853(Minced.getInstance().getThemeManager().method279(), param5);
        String v11 = param6 ? field10288 : param7.method169();
        String v12 = param6 ? field10289 : param7.method168();
        String v13 = param6 ? field10290 : param7.method170();
        Fonts.field12502[18].method2711(param1, v11, param2 + 115, (float)param3 + field10291, v10);
        Fonts.I[15].method2711(param1, v12, param2 + 127, param3 + 10, ColorUtil.method1853(-1, param5));
        Fonts.I[12].method2711(param1, v13, param2 + 115, param3 + 21, ColorUtil.method1853(field10292, param5));
        String v14 = UserInfo.method1841() == null || UserInfo.method1841().isBlank() ? field10293 : UserInfo.method1841();
        String v15 = _4ZXYEo.cfr_renamed_251("makeConcatWithConstants", new Object[]{}, (String)UserInfo.method1843());
        float v16 = Fonts.I[12].method2719((String)((Object)v15));
        float v17 = Fonts.I[13].method2719(v14);
        float v18 = (float)param2 + field10294;
        RenderUtil.method389(Identifier.of((String)field10295, (String)field10296), null, v18 - field10297, param3 + 8, field10298, field10299, field10300, param4);
        Fonts.I[13].method2711(param1, v14, v18 - Math.max(v17, v16) - field10301, (float)param3 + field10302, ColorUtil.method1853(-1, param4));
        Fonts.I[12].method2711(param1, (String)((Object)v15), v18 - v16 - field10303, (float)param3 + field10304, ColorUtil.method1853(field10305, param4));
        RenderUtil.method397(v18 - field10306, param3 + 19, field10307, field10308, field10309, ColorUtil.method1853(field10310, param4));
        float v19 = param4 * field10311;
        RenderUtil.method394(param1, param2, (float)param3 + field10312, field10313, field10314, ColorUtil.method1853(field10315, v19));
        RenderUtil.method394(param1, (float)param2 + field10316, param3, field10317, field10318, ColorUtil.method1853(field10319, v19));
    }

    public boolean method2975(float param1, float param2, int param3, int param4) {
        return MathUtil.cfr_underscore(param1, param2, param3 + 87, param4 + 13, field10320, field10321);
    }

    static {
        field10284 = -8750470;
        field10285 = -14079703;
        field10286 = "e";
        field10287 = 14.5f;
        field10288 = "g";
        field10289 = "Customize";
        field10290 = "Категория имеет в себе все элементы для кастомизации клиента";
        field10291 = 10.0f;
        field10292 = -8750470;
        field10293 = "Minced";
        cfr_renamed_252 = "UID: \u0001";
        field10294 = 448.5f;
        field10297 = 24.0f;
        field10298 = 16.0f;
        field10299 = 16.0f;
        field10300 = 7.0f;
        field10301 = 27.0f;
        field10302 = 11.5f;
        field10303 = 27.0f;
        field10304 = 19.5f;
        field10305 = -8750470;
        field10306 = 13.0f;
        field10307 = 5.0f;
        field10308 = 5.0f;
        field10309 = 1.5f;
        field10310 = 65280;
        field10311 = 0.05f;
        field10312 = 32.0f;
        field10313 = 448.5f;
        field10314 = 0.5f;
        field10315 = -8750470;
        field10316 = 104.0f;
        field10317 = 0.5f;
        field10318 = 272.5f;
        field10319 = -8750470;
        field10320 = 9.0f;
        field10321 = 8.0f;
    }
}



