/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.util.math.MathHelper
 *  sLM.4ZXYEo
 */
package minced.core;

import java.awt.Color;
import minced.core.MathUtil;
import minced.core.ThemeColors;
import minced.enums.MAIN;
import net.minecraft.util.math.MathHelper;
import sLM._4ZXYEo;

public class ColorUtil {
    static String cfr_renamed_119;
    static String cfr_renamed_120;

    static float field7559;
    static int field7560;
    static float field7561 = 255.0f;
    static float field7562 = 255.0f;
    static float field7563 = 255.0f;
    static float field7564 = 255.0f;
    static float field7565;
    static float field7566;
    static float field7567;
    static float field7568;
    static float field7569;
    static String field7570;
    static String field7571;
    static String field7572;
    static float field7573;
    static long field7574;
    static float field7575;

    public static int method1845(int param0, float param1) {
        param1 = MathHelper.clamp((float)param1, (float)0.0f, (float)1.0f);
        int v2 = MathHelper.clamp((int)((int)((float)ColorUtil.method1854(param0) * param1)), (int)0, (int)255);
        int v3 = MathHelper.clamp((int)((int)((float)ColorUtil.method1846(param0) * param1)), (int)0, (int)255);
        int v4 = MathHelper.clamp((int)((int)((float)ColorUtil.method1847(param0) * param1)), (int)0, (int)255);
        return ColorUtil.method1852(v2, v3, v4, ColorUtil.cfr_renamed_0(param0));
    }

    public static int method1846(int param0) {
        return param0 >> 8 & 0xFF;
    }

    public static int method1847(int param0) {
        return param0 & 0xFF;
    }

    public static int method1848(int param0, float param1) {
        int v2 = Math.min(255, Math.max(0, (int)(param1 * field7559)));
        return param0 & field7560 | v2 << 24;
    }

    public static float[] method1849(int param0) {
        return new float[]{(float)ColorUtil.method1854(param0) / field7561, (float)ColorUtil.method1846(param0) / field7562, (float)ColorUtil.method1847(param0) / field7563, (float)ColorUtil.cfr_renamed_0(param0) / field7564};
    }

    public static int method1850(int param0, int param1, float param2) {
        double v3 = MathHelper.clamp((float)param2, (float)0.0f, (float)1.0f);
        return ColorUtil.method1852(MathUtil.cfr_underscore(ColorUtil.method1854(param0), ColorUtil.method1854(param1), v3), MathUtil.cfr_underscore(ColorUtil.method1846(param0), ColorUtil.method1846(param1), v3), MathUtil.cfr_underscore(ColorUtil.method1847(param0), ColorUtil.method1847(param1), v3), MathUtil.cfr_underscore(ColorUtil.cfr_renamed_0(param0), ColorUtil.cfr_renamed_0(param1), v3));
    }

    public static int cfr_renamed_0(int param0) {
        return param0 >> 24 & 0xFF;
    }

    public static int method1851(int param0, float param1) {
        float[] v2 = ColorUtil.method1849(param0);
        float[] v3 = Color.RGBtoHSB((int)(v2[0] * field7565), (int)(v2[1] * field7566), (int)(v2[2] * field7567), null);
        v3[2] = v3[2] * param1;
        v3[2] = Math.max(0.0f, Math.min(1.0f, v3[2]));
        return ColorUtil.method1853(Color.HSBtoRGB(v3[0], v3[1], v3[2]), v2[3] * field7568);
    }

    public static int method1852(int param0, int param1, int param2, int param3) {
        return MathHelper.clamp((int)param3, (int)0, (int)255) << 24 | MathHelper.clamp((int)param0, (int)0, (int)255) << 16 | MathHelper.clamp((int)param1, (int)0, (int)255) << 8 | MathHelper.clamp((int)param2, (int)0, (int)255);
    }

    public static int method1853(int param0, float param1) {
        return ColorUtil.method1855(param0, (int)(MathHelper.clamp((float)param1, (float)0.0f, (float)1.0f) * field7569));
    }

    public static int method1854(int param0) {
        return param0 >> 16 & 0xFF;
    }

    public static int cfr_renamed_0(String param0) {
        int v5;
        int v4;
        Object v1 = param0.trim();
        if (((String)v1).startsWith(field7570)) {
            v1 = ((String)v1).substring(1);
        }
        if (((String)v1).startsWith(field7571) || ((String)v1).startsWith(field7572)) {
            v1 = ((String)v1).substring(2);
        }
        if (((String)v1).length() == 3 || ((String)v1).length() == 4) {
            char c = ((String)v1).charAt(0);
            char c2 = ((String)v1).charAt(1);
            v4 = ((String)v1).charAt(2);
            v5 = ((String)v1).length() == 4 ? (int)((String)v1).charAt(3) : 70;
            v1 = _4ZXYEo.фС("makeConcatWithConstants", new Object[]{}, (char)c, (char)c, (char)c2, (char)c2, (char)v4, (char)v4, (char)v5, (char)v5);
        }
        if (((String)v1).length() == 6) {
            int n = Integer.parseInt(((String)v1).substring(0, 2), 16);
            int n2 = Integer.parseInt(((String)v1).substring(2, 4), 16);
            v4 = Integer.parseInt(((String)v1).substring(4, 6), 16);
            return ColorUtil.method1852(n, n2, v4, 255);
        }
        if (((String)v1).length() == 8) {
            int n = Integer.parseInt(((String)v1).substring(0, 2), 16);
            int n3 = Integer.parseInt(((String)v1).substring(2, 4), 16);
            v4 = Integer.parseInt(((String)v1).substring(4, 6), 16);
            v5 = Integer.parseInt(((String)v1).substring(6, 8), 16);
            return ColorUtil.method1852(n, n3, v4, v5);
        }
        throw new IllegalArgumentException((String)((Object)_4ZXYEo.фж("makeConcatWithConstants", new Object[]{}, (String)param0)));
    }

    public static int O(int param0) {
        return ColorUtil.method1857(ThemeColors.method3199(MAIN.MODULE_VISUAL), ColorUtil.method1851(ThemeColors.method3199(MAIN.MODULE_VISUAL), field7573), param0, 10);
    }

    public static int method1855(int param0, int param1) {
        return ColorUtil.method1852(ColorUtil.method1854(param0), ColorUtil.method1846(param0), ColorUtil.method1847(param0), param1);
    }

    public static int method1856(int param0, int param1) {
        return ColorUtil.method1852(Math.min(255, ColorUtil.method1854(param0) + param1), Math.min(255, ColorUtil.method1846(param0) + param1), Math.min(255, ColorUtil.method1847(param0) + param1), ColorUtil.cfr_renamed_0(param0));
    }

    public static int method1857(int param0, int param1, int param2, int param3) {
        int v4 = (int)((System.currentTimeMillis() / (long)param0 + (long)param1) % field7574);
        v4 = (v4 > 180 ? 360 - v4 : v4) + 180;
        return ColorUtil.method1850(param2, param3, (float)(v4 - 180) / field7575);
    }

    public static int method1858(int param0, int param1, int param2) {
        return ColorUtil.method1852(param0, param1, param2, 255);
    }

    static {
        field7559 = 255.0f;
        field7560 = 0xFFFFFF;
        field7573 = 0.3f;
        field7569 = 255.0f;
        field7565 = 255.0f;
        field7566 = 255.0f;
        field7567 = 255.0f;
        field7568 = 255.0f;
        field7570 = "#";
        field7571 = "0x";
        field7572 = "0X";
        cfr_renamed_119 = "\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001";
        cfr_renamed_120 = "Unsupported hex format: \u0001";
        field7574 = 360L;
        field7575 = 180.0f;
    }
}



