/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  org.joml.Vector4f
 */
package minced.core;

import minced.core.ColorUtil;
import minced.core.ThemeColors;
import minced.core.RenderUtil;
import minced.enums.MAIN;
import org.joml.Vector4f;

public final class HudTheme {
    static float field7609;
    static float field7610;
    static float field7611;
    static float field7612;
    static float field7613;
    static float field7614;
    static String field7615;
    static int field7616;
    static float field7617;
    static float field7618;
    static float field7619;
    static float field7620;
    static float field7621;
    static float field7622;

    public static void method1871(float param0, float param1, float param2, float param3, float param4, float ... param5) {
        HudTheme.method1880(param0, param1, param2, param3, param4, param5);
    }

    public static void method1872(float param0, float param1, float param2, float param3, float param4) {
        HudTheme.method1880(param0, param1, param2, param3, param4, field7609);
    }

    public static int method1873(float param0) {
        return ColorUtil.method1853(-1, param0);
    }

    public static int method1874(int param0, float param1) {
        return ColorUtil.method1853(param0, param1);
    }

    public static int method1875(float param0) {
        return ColorUtil.method1853(ThemeColors.method3199(MAIN.MODULE_VISUAL), param0);
    }

    public static int method1876(float param0) {
        return ColorUtil.method1853(-1, field7610 * param0);
    }

    public static void method1877(float param0, float param1, float param2, float param3) {
        HudTheme.method1872(param0, param1, param2, param3, 1.0f);
    }

    private static Vector4f method1878(float ... param0) {
        if (param0 == null || param0.length == 0) {
            return new Vector4f(field7611, field7612, field7613, field7614);
        }
        if (param0.length == 1) {
            return new Vector4f(param0[0], param0[0], param0[0], param0[0]);
        }
        if (param0.length >= 4) {
            return new Vector4f(param0[0], param0[1], param0[2], param0[3]);
        }
        return new Vector4f(param0[0], param0[0], param0[0], param0[0]);
    }

    public static void method1879() {
        field7616 = ColorUtil.cfr_renamed_0(field7615);
    }

    private static void method1880(float param0, float param1, float param2, float param3, float param4, float ... param5) {
        if (param2 <= 0.0f || param3 <= 0.0f || param4 <= 0.0f) {
            return;
        }
        RenderUtil.method404(param0 - field7617, param1 - field7618, param2 + field7619, param3 + field7620, HudTheme.method1878(param5), ColorUtil.method1853(ThemeColors.method3199(MAIN.WINDOW_BG), param4), ColorUtil.method1853(ThemeColors.method3199(MAIN.WINDOW_BG), param4), ColorUtil.method1853(ThemeColors.method3199(MAIN.WINDOW_BG), param4), ColorUtil.method1853(ThemeColors.method3199(MAIN.WINDOW_BG), param4), param4, field7621);
        RenderUtil.method403(param0, param1, param2, param3, HudTheme.method1878(param5), ColorUtil.method1853(ThemeColors.method3199(MAIN.WINDOW_BG), param4), param4);
        RenderUtil.method400(param0, param1, param2, param3, HudTheme.method1878(param5), ColorUtil.method1853(ThemeColors.method3199(MAIN.MODULE_VISUAL), field7622 * param4));
    }

    public static void method1881(float param0, float param1, float param2, float param3, float param4, float ... param5) {
        HudTheme.method1880(param0, param1, param2, param3, param4, param5);
    }

    private HudTheme() {
    }

    static {
        field7616 = 0xAFF0A0A;
        field7609 = 5.0f;
        field7610 = 0.2f;
        field7617 = 6.0f;
        field7618 = 6.0f;
        field7619 = 12.0f;
        field7620 = 12.0f;
        field7621 = 6.0f;
        field7622 = 0.05f;
        field7611 = 8.0f;
        field7612 = 8.0f;
        field7613 = 8.0f;
        field7614 = 8.0f;
        field7615 = "0xFF0A0A0A";
        HudTheme.method1879();
    }
}



