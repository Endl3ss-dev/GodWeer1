/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.render.RenderTickCounter
 *  net.minecraft.client.render.RenderTickCounter$Dynamic
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.SlotChangedStateC2SPacket
 *  net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket
 *  net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket
 *  net.minecraft.util.math.MathHelper
 */
package minced.module.movement;
import minced.core.EventHandler;

import minced.core.BindSetting;
import minced.core.BooleanSetting;
import minced.core.SliderSetting;
import minced.core.ModeSetting;
import minced.core.EventUpdate;
import minced.core.EventPacket;
import minced.core.EventMotion;
import minced.core.ClientState;
import minced.core.RenderTickAccess;
import minced.core.EventKey;
import minced.core.Module;
import minced.enums.Combat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.SlotChangedStateC2SPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.MathHelper;

public class Timer
extends Module {
    static float cfr_renamed_401;

    ModeSetting field2304 = new ModeSetting(field2336, field2337, field2338, field2339);
    static String field2305;
    BindSetting field2306 = new BindSetting(field2340, () -> this.field2304.isMode(field2329));
    boolean field2307;
    static MinecraftClient I = MinecraftClient.getInstance();
    static String field2308;
    static float field2309;
    static SliderSetting field2310;
    static float field2311;
    ClientState field2312;
    static double field2313;
    static double field2314;
    static double field2315;
    static float field2316;
    static float field2317;
    static long field2318;
    static float field2319;
    static float field2320;
    static String field2321;
    static String field2322;
    BooleanSetting cfr_renamed_0 = new BooleanSetting(field2341, true);
    static float field2323;
    static String field2324;
    static float field2325;
    SliderSetting field2326;
    static float field2327;
    static String field2328;
    static String field2329;
    static String field2330;
    static String field2331;
    static String field2332;
    static String field2333;
    static String field2334;
    static String field2335;
    static String field2336;
    static String field2337;
    static String field2338;
    static String field2339;
    static String field2340;
    static String field2341;
    static String field2342;
    BooleanSetting field2343 = new BooleanSetting(field2342, false, () -> !this.field2304.isMode(field2330));
    static String field2344;
    static float field2345;
    static float field2346;
    static float field2347;
    static float field2348;
    SliderSetting field2349 = new SliderSetting(field2344, field2345, field2346, field2347, field2348, () -> (Boolean)this.field2343.getValue());
    static String field2350;
    static float field2351;
    static float field2352;
    static float field2353;
    static Timer field2354;
    static String field2355;
    static float field2356;
    static float field2357;
    static SliderSetting field2358;
    static String field2359;
    static float field2360;
    static float field2361;
    static String field2362;
    static double field2363;

    @EventHandler(2)
    public void method618(EventKey param1) {
        if (!this.isEnabled() || !this.field2304.isMode(field2305)) {
            return;
        }
        if (!param1.method3594() && param1.method3599() && (Integer)this.field2306.getValue() >= 0 && param1.method3595() == ((Integer)this.field2306.getValue()).intValue()) {
            this.field2307 = true;
        }
    }

    private void method619() {
        this.cfr_renamed_0(1.0f);
    }

    private void cfr_renamed_0(float param1) {
        RenderTickCounter v3 = I.getRenderTickCounter();
        if (v3 instanceof RenderTickCounter.Dynamic) {
            RenderTickCounter.Dynamic v2 = (RenderTickCounter.Dynamic)v3;
            ((RenderTickAccess)v2).setSpeedMultiplier(param1);
        }
    }

    public void method620() {
        if (this.field2304.isMode(field2308)) {
            field2311 = field2309 / ((Float)field2310.getValue()).floatValue();
            this.field2307 = false;
        }
    }

    @Override
    public void onEnable() {
        this.method620();
        this.method619();
        this.field2312.method3114();
        if (Timer.I.player != null) {
            field2313 = Timer.I.player.getX();
            field2314 = Timer.I.player.getY();
            field2315 = Timer.I.player.getZ();
            field2316 = Timer.I.player.getYaw();
            field2317 = Timer.I.player.getPitch();
        }
        super.onEnable();
    }

    @EventHandler(2)
    public void method621(EventUpdate param1) {
        if (!this.isEnabled() || Timer.I.player == null) {
            return;
        }
        if (this.field2312.method3113(field2318)) {
            this.method620();
            this.field2312.method3114();
        }
        if (!Timer.I.player.isOnGround() && !this.field2307) {
            field2311 += field2319;
            field2311 = MathHelper.clamp((float)field2311, (float)0.0f, (float)(field2320 / (this.field2304.isMode(field2321) ? 1.0f : ((Float)field2310.getValue()).floatValue())));
        }
        if (this.field2304.isMode(field2322) && !this.field2307) {
            this.method619();
            return;
        }
        this.cfr_renamed_0(((Float)field2310.getValue()).floatValue());
        if (!((Boolean)this.cfr_renamed_0.getValue()).booleanValue() || ((Float)field2310.getValue()).floatValue() <= 1.0f) {
            return;
        }
        if (field2311 < field2323 / ((Float)field2310.getValue()).floatValue()) {
            field2311 += this.field2304.isMode(field2324) ? field2325 : ((Float)this.field2326.getValue()).floatValue();
            field2311 = MathHelper.clamp((float)field2311, (float)0.0f, (float)(field2327 / (this.field2304.isMode(field2328) ? 1.0f : ((Float)field2310.getValue()).floatValue())));
        } else {
            this.method625();
        }
    }

    public static float method622() {
        return field2311;
    }

    private boolean method623() {
        if (Timer.I.player == null) {
            return false;
        }
        return field2313 == Timer.I.player.getX() && field2314 == Timer.I.player.getY() && field2315 == Timer.I.player.getZ() && field2316 == Timer.I.player.getYaw() && field2317 == Timer.I.player.getPitch();
    }

    private void method624(EventPacket param1) {
        if (param1.method2467() instanceof PlayerPositionLookS2CPacket && this.field2307) {
            this.method625();
        }
    }

    public void method625() {
        this.cfr_renamed_4(false);
        this.method619();
    }

    private void method626(EventPacket param1) {
        EntityVelocityUpdateS2CPacket v2;
        if (Timer.I.player == null) {
            return;
        }
        Packet v3 = param1.method2467();
        if (v3 instanceof EntityVelocityUpdateS2CPacket && (v2 = (EntityVelocityUpdateS2CPacket)v3).getEntityId() == Timer.I.player.getId()) {
            this.method625();
        }
    }

    @EventHandler(2)
    public void I(EventPacket param1) {
        if (!this.isEnabled() || !this.field2304.isMode(field2331)) {
            return;
        }
        if (param1.method2465()) {
            this.method635(param1);
            return;
        }
        if (param1.method2466()) {
            this.method624(param1);
            this.method626(param1);
        }
    }

    @EventHandler(2)
    public void method630(EventMotion param1) {
        if (!this.isEnabled() || !this.field2304.isMode(field2333)) {
            return;
        }
        this.method633(param1.method2514(), param1.method2515(), param1.method2518(), param1.method2508(), param1.method2513());
    }

    public Timer() {
        super(field2334, field2335, Combat.Movement);
        this.field2326 = new SliderSetting(field2350, 1.0f, field2351, field2352, field2353, () -> !this.field2304.isMode(field2332));
        this.field2312 = new ClientState();
        field2354 = this;
        this.addSettings(this.field2304, field2310, this.field2306, this.cfr_renamed_0, this.field2343, this.field2349, this.field2326);
    }

    public static void method631(int param0) {
    }

    public static void method632() {
        field2311 = 0.0f;
        field2358 = field2310 = new SliderSetting(field2355, 2.0f, 1.0f, field2356, field2357);
    }

    @Override
    public void onDisable() {
        this.method620();
        this.method619();
        this.field2312.method3114();
        super.onDisable();
    }

    public void method633(float param1, float param2, double param3, double param5, double param7) {
        if (this.method623()) {
            field2311 = this.field2304.isMode(field2359) ? (field2311 -= field2360) : (field2311 -= ((Float)this.field2326.getValue()).floatValue() + field2361);
        } else if (((Boolean)this.field2343.getValue()).booleanValue() && !this.field2304.isMode(field2362)) {
            field2311 -= ((Float)this.field2349.getValue()).floatValue();
        }
        field2311 = MathHelper.clamp((float)field2311, (float)0.0f, (float)((float)Math.floor(field2363)));
        field2313 = param3;
        field2314 = param5;
        field2315 = param7;
        field2316 = param1;
        field2317 = param2;
    }

    private void method635(EventPacket param1) {
        if (param1.method2467() instanceof SlotChangedStateC2SPacket) {
            param1.method2298(true);
        }
    }

    static {
        cfr_renamed_401 = 0.0f;
        field2311 = 50.0f;
        field2313 = 11.70436483435483;
        field2314 = -60.0;
        field2315 = 114.94032852945483;
        field2316 = 62014.508f;
        field2317 = 90.0f;
        field2334 = "Timer";
        field2335 = "Ускоряет время обновления игры";
        field2336 = "Режим";
        field2337 = "Matrix";
        field2338 = "Matrix";
        field2339 = "Grim";
        field2340 = "Бинд Grim";
        field2341 = "Смарт";
        field2342 = "Подъём при движении";
        field2344 = "Значение подъёма";
        field2345 = 0.02f;
        field2346 = 0.01f;
        field2347 = 0.5f;
        field2348 = 0.01f;
        field2350 = "Накопление тиков";
        field2351 = 0.15f;
        field2352 = 3.0f;
        field2353 = 0.1f;
        field2305 = "Grim";
        field2333 = "Grim";
        field2331 = "Grim";
        field2318 = 25000L;
        field2319 = 0.1f;
        field2320 = 100.0f;
        field2321 = "Grim";
        field2322 = "Grim";
        field2323 = 100.0f;
        field2324 = "Grim";
        field2325 = 0.05f;
        field2327 = 100.0f;
        field2328 = "Grim";
        field2359 = "Grim";
        field2360 = 0.05f;
        field2361 = 0.4f;
        field2362 = "Grim";
        field2363 = 100.0;
        field2308 = "Grim";
        field2309 = 100.0f;
        field2332 = "Grim";
        field2330 = "Grim";
        field2329 = "Grim";
        field2355 = "Скорость";
        field2356 = 10.0f;
        field2357 = 0.01f;
        Timer.method632();
    }
}



