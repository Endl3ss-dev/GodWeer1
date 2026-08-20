/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.util.Window
 *  net.minecraft.util.math.Vec2f
 */
package minced.core;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import net.minecraft.util.math.Vec2f;

public class WindowScaleUtil {
    static double field10067;
    static double field10068;
    static double field10069;

    public static Vec2f method2888(float param0, float param1) {
        MinecraftClient v2 = MinecraftClient.getInstance();
        Window v3 = v2 != null ? v2.getWindow() : null;
        double v4 = v3 != null ? v3.getScaleFactor() : field10067;
        float v6 = (float)((double)param0 * (v4 / field10068));
        float v7 = (float)((double)param1 * (v4 / field10069));
        return new Vec2f(v6, v7);
    }

    static {
        field10067 = 2.0;
        field10068 = 2.0;
        field10069 = 2.0;
    }
}



