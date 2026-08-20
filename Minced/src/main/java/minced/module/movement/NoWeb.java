/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Blocks
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket
 *  net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket$Action
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.MathHelper
 *  sg.ec.chO
 */
package minced.module.movement;
import minced.core.EventHandler;

import java.util.function.BooleanSupplier;
import minced.core.BooleanSetting;
import minced.core.SliderSetting;
import minced.core.ModeSetting;
import minced.core.EventBlockRayTrace;
import minced.core.EventUpdate;
import minced.core.TimerUtil;
import minced.core.Module;
import minced.enums.Combat;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import sg.ec.chO;

public class NoWeb
extends Module {
    ModeSetting field2364 = new ModeSetting(field2376, field2377, field2378, field2379);
    static String field2365;
    static String field2366;
    static String field2367;
    static MinecraftClient I = MinecraftClient.getInstance();
    static String field2368;
    SliderSetting field2369;
    SliderSetting field2370;
    SliderSetting field2371;
    static String field2372;
    SliderSetting field2373 = new SliderSetting(field2380, field2381, 1.0f, field2382, 1.0f, () -> this.field2364.isMode(field2365));
    static String field2374;
    static String field2375;
    static String field2376;
    static String field2377;
    static String field2378;
    static String field2379;
    static String field2380;
    static float field2381;
    static float field2382;
    static String field2383;
    BooleanSetting field2384 = new BooleanSetting(field2383, false);
    static String field2385;
    static float field2386;
    static float field2387;
    static String field2388;
    static float field2389;
    static float field2390;
    static float field2391;
    static String field2392;
    static float field2393;
    static float field2394;
    static float field2395;
    static float field2396;
    static String field2397;

    @EventHandler(2)
    public void method639(EventUpdate param1) {
        if (NoWeb.I.player == null || NoWeb.I.world == null || NoWeb.I.player.input == null) {
            return;
        }
        Box v2 = NoWeb.I.player.getBoundingBox();
        boolean v3 = false;
        for (BlockPos v5 : BlockPos.iterate((int)MathHelper.floor((double)v2.minX), (int)MathHelper.floor((double)v2.minY), (int)MathHelper.floor((double)v2.minZ), (int)MathHelper.floor((double)v2.maxX), (int)MathHelper.floor((double)v2.maxY), (int)MathHelper.floor((double)v2.maxZ))) {
            if (!NoWeb.I.world.getBlockState(v5).isOf(Blocks.COBWEB)) continue;
            v3 = true;
            break;
        }
        if (!v3) {
            return;
        }
        if (v3 && this.field2364.isMode(field2368)) {
            double v4 = 0.0;
            if (NoWeb.I.options.jumpKey.isPressed()) {
                v4 = ((Float)this.field2369.getValue()).floatValue();
            }
            if (NoWeb.I.options.sneakKey.isPressed()) {
                v4 = ((Float)this.field2370.getValue()).floatValue();
            }
            boolean hasHorizontalInput = NoWeb.I.player.input.movementForward != 0.0f
                    || NoWeb.I.player.input.movementSideways != 0.0f;
            NoWeb.I.player.setVelocity(
                    hasHorizontalInput ? NoWeb.I.player.getVelocity().x : 0.0,
                    v4,
                    hasHorizontalInput ? NoWeb.I.player.getVelocity().z : 0.0);
            TimerUtil.method3546(((Float)this.field2371.getValue()).floatValue());
        }
    }

    @EventHandler(2)
    public void method640(chO param1) {
        if (this.field2364.isMode(field2372)) {
            return;
        }
        if (param1.cfr_renamed_0() == NoWeb.I.player) {
            param1.必(true);
            int v2 = 0;
            while ((float)v2 < ((Float)this.field2373.getValue()).floatValue()) {
                NoWeb.I.player.networkHandler.sendPacket((Packet)new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, param1.А(), Direction.UP));
                ++v2;
            }
        }
    }

    public NoWeb() {
        super(field2374, field2375, Combat.Movement);
        this.field2371 = new SliderSetting(field2385, field2386, 0.0f, 1.0f, field2387, () -> this.field2364.isMode(field2366));
        this.field2369 = new SliderSetting(field2388, field2389, field2390, 1.0f, field2391, () -> this.field2364.isMode(field2367));
        this.field2370 = new SliderSetting(field2392, field2393, field2394, field2395, field2396, () -> this.field2364.isMode(field2397));
        this.addSettings(this.field2364, this.field2384, this.field2371, this.field2369, this.field2370);
    }

    @EventHandler(2)
    public void method642(EventBlockRayTrace param1) {
        if (!((Boolean)this.field2384.getValue()).booleanValue()) {
            return;
        }
        if (NoWeb.I.player == null || NoWeb.I.world == null) {
            return;
        }
        if (param1.method2327() != null && param1.method2327().isOf(Blocks.COBWEB)) {
            param1.method2298(true);
        }
    }

    static {
        field2374 = "No Web";
        field2375 = "Позволяет быстро перемещаться в паутине";
        field2376 = "Мод";
        field2377 = "Custom";
        field2378 = "Vanilla";
        field2379 = "Custom";
        field2380 = "Кол-во пакетов";
        field2381 = 3.0f;
        field2382 = 10.0f;
        field2383 = "Ломать сквозь паутину";
        field2385 = "Скорость XZ";
        field2386 = 0.22f;
        field2387 = 0.01f;
        field2388 = "Скорость вверх по Y";
        field2389 = 0.95f;
        field2390 = 0.5f;
        field2391 = 0.01f;
        field2392 = "Скорость вниз по Y";
        field2393 = -0.95f;
        field2394 = -1.0f;
        field2395 = -0.5f;
        field2396 = 0.01f;
        field2372 = "Custom";
        field2368 = "Custom";
        field2397 = "Custom";
        field2367 = "Custom";
        field2366 = "Custom";
        field2365 = "Vanilla";
    }
}



