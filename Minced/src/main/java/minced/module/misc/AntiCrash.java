/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.s2c.play.ExplosionS2CPacket
 *  net.minecraft.network.packet.s2c.play.ParticleS2CPacket
 */
package minced.module.misc;
import minced.core.EventHandler;

import java.lang.reflect.Field;
import java.util.function.Function;
import minced.core.EventPacket;
import minced.core.ClientState;
import minced.core.ChatUtil;
import minced.core.Module;
import minced.enums.Combat;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;

public class AntiCrash
extends Module
{
    static String field2244 = "AntiCrash";
    static String field2245 = "Защищает от краша через пакеты";
    ClientState field2246 = new ClientState();
    static String field2247;
    static String field2248;
    static String field2249;
    static String field2250;
    static double field2251;
    static double field2252;
    static double field2253;
    static double field2254;
    static long field2255;
    static String field2256;
    static String field2257;
    static String field2258;
    static String field2259;
    static String field2260;
    static String field2261;
    static String field2262;
    static String field2263;
    static double field2264;
    static double field2265;
    static double field2266;
    static double field2267;
    static double field2268;
    static double field2269;
    static double field2270;
    static long field2271;
    static String field2272;

    private float method607(Object param1, String param2) {
        try {
            Field v3 = param1.getClass().getDeclaredField(param2);
            v3.setAccessible(true);
            return v3.getFloat(param1);
        }
        catch (NoSuchFieldException | IllegalAccessException v4) {
            // Field unreadable under this mapping: report a neutral value so the packet is not blocked.
            return 0.0f;
        }
    }

    public AntiCrash() {
        super(field2244, field2245, Combat.Miscellaneous);
    }

    @EventHandler(2)
    public void method608(EventPacket param1) {
        if (!param1.method2466()) {
            return;
        }
        Packet v42 = param1.method2467();
        if (v42 instanceof ExplosionS2CPacket) {
            ExplosionS2CPacket v2 = (ExplosionS2CPacket)v42;
            double v42_72 = this.method609(v2, field2247);
            double v6 = this.method609(v2, field2248);
            double v8 = this.method609(v2, field2249);
            float v10 = this.method607(v2, field2250);
            if (Math.abs(v42_72) > field2251 || Math.abs(v6) > field2252 || Math.abs(v8) > field2253 || (double)v10 > field2254) {
                if (this.field2246.method3113(field2255)) {
                    ChatUtil.method3308(field2256, new Object[0]);
                    this.field2246.method3114();
                }
                param1.method2298(true);
            }
        } else {
            v42 = param1.method2467();
            if (v42 instanceof ParticleS2CPacket) {
                ParticleS2CPacket v3 = (ParticleS2CPacket)v42;
                double v42_87 = this.method609(v3, field2257);
                double v6 = this.method609(v3, field2258);
                double v8 = this.method609(v3, field2259);
                float v10 = this.method607(v3, field2260);
                double v11 = this.method609(v3, field2261);
                double v13 = this.method609(v3, field2262);
                double v15 = this.method609(v3, field2263);
                if (Math.abs(v42_87) > field2264 || Math.abs(v6) > field2265 || Math.abs(v8) > field2266 || (double)v10 > field2267 || Math.abs(v11) > field2268 || Math.abs(v13) > field2269 || Math.abs(v15) > field2270) {
                    if (this.field2246.method3113(field2271)) {
                        ChatUtil.method3308(field2272, new Object[0]);
                        this.field2246.method3114();
                    }
                    param1.method2298(true);
                }
            }
        }
    }

    private double method609(Object param1, String param2) {
        try {
            Field v3 = param1.getClass().getDeclaredField(param2);
            v3.setAccessible(true);
            return v3.getDouble(param1);
        }
        catch (NoSuchFieldException | IllegalAccessException v4) {
            // Field unreadable under this mapping: report a neutral value so the packet is not blocked.
            return 0.0;
        }
    }

    static {
        field2247 = "x";
        field2248 = "y";
        field2249 = "z";
        field2250 = "power";
        field2251 = 1.0E9;
        field2252 = 1.0E9;
        field2253 = 1.0E9;
        field2254 = 1.0E9;
        field2255 = 1000L;
        field2256 = "ExplosionS2CPacket canceled";
        field2257 = "x";
        field2258 = "y";
        field2259 = "z";
        field2260 = "speed";
        field2261 = "offsetX";
        field2262 = "offsetY";
        field2263 = "offsetZ";
        field2264 = 1.0E9;
        field2265 = 1.0E9;
        field2266 = 1.0E9;
        field2267 = 1.0E9;
        field2268 = 1.0E9;
        field2269 = 1.0E9;
        field2270 = 1.0E9;
        field2271 = 1000L;
        field2272 = "ParticleS2CPacket canceled";
    }
}



