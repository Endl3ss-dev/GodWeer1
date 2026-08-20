/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 */
package minced.core;

import java.awt.Color;

public class HexColor
{
    static String field9426;
    static float field9427;
    static float field9428;

    public static Color method2749(String param0) {
        if (param0.startsWith(field9426)) {
            param0 = param0.substring(1);
        }
        return new Color(Integer.parseInt(param0, 16));
    }

    public static int method2750(Color param0, float param1) {
        int v2 = (int)(param1 * field9427);
        return new Color(param0.getRed(), param0.getGreen(), param0.getBlue(), v2).getRGB();
    }

    public static int method2751(String param0, float param1) {
        return HexColor.method2750(HexColor.method2749(param0), param1);
    }

    public static int method2752(int param0, float param1) {
        int v2 = param0 >> 16 & 0xFF;
        int v3 = param0 >> 8 & 0xFF;
        int v4 = param0 & 0xFF;
        int v5 = Math.round(param1 * field9428);
        return new Color(v2, v3, v4, v5).getRGB();
    }

    static {
        field9427 = 255.0f;
        field9428 = 255.0f;
        field9426 = "#";
    }
}



