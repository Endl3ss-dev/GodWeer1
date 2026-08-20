/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket
 *  net.minecraft.util.math.MathHelper
 */
package minced.core;

import minced.core.CancellableEvent;
import minced.core.EventPacket;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.util.math.MathHelper;

public class TpsUtil
implements CancellableEvent {
    long field9122;
    static float field9123;
    static float field9124;
    static float field9125;
    static float field9126;
    static float field9127;

    @EventHandler(2)
    private void method2597(EventPacket param1) {
        if (param1.method2467() instanceof WorldTimeUpdateS2CPacket) {
            long v2 = System.nanoTime() - this.field9122;
            float v4 = field9123;
            float v5 = v4 * (field9124 / (float)v2);
            float v6 = MathHelper.clamp((float)v5, (float)0.0f, (float)v4);
            field9125 = (float)Math.round(v6 * 2.0f) / 2.0f;
            field9126 = v6 - v4;
            this.field9122 = System.nanoTime();
        }
    }

    public static void method2598() {
        field9125 = field9127;
        field9126 = 0.0f;
    }

    public static float method2599() {
        return field9125;
    }

    public static float method2600() {
        return field9126;
    }

    static {
        field9125 = 20.0f;
        field9126 = -0.021669388f;
        field9123 = 20.0f;
        field9124 = 1.0E9f;
        field9127 = 20.0f;
        TpsUtil.method2598();
    }
}

