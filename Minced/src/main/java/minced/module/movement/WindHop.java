/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.Blocks
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.entity.Entity
 *  net.minecraft.item.Items
 *  net.minecraft.network.packet.Packet
 *
 *  net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket
 *  net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket
 *  net.minecraft.util.Hand
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.hit.HitResult$Type
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Position
 *  net.minecraft.world.RaycastContext
 *  net.minecraft.world.RaycastContext$FluidHandling
 *  net.minecraft.world.RaycastContext$ShapeType
 */
package minced.module.movement;
import minced.core.EventHandler;

import minced.core.BindSetting;
import minced.core.BooleanSetting;
import minced.core.ModeSetting;
import minced.core.EventJump;
import minced.core.EventUpdate;
import minced.core.EventKey;
import minced.core.Module;
import minced.enums.Combat;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Position;
import net.minecraft.world.RaycastContext;

public class WindHop
extends Module {
    static MinecraftClient I = MinecraftClient.getInstance();
    ModeSetting field2273 = new ModeSetting(field2293, field2294, field2295, field2296);
    static String field2274;
    static String field2275;
    BindSetting field2276 = new BindSetting(field2297, () -> this.field2273.isMode(cfr_renamed_406));
    boolean field2277 = false;
    static double field2278;
    static double field2279;
    static float field2280;
    static double field2281;
    static float field2282;
    static String field2283;
    static double field2284;
    static double field2285;
    static float field2286;
    static double field2287;
    BooleanSetting field2288;
    static double field2289;
    BooleanSetting field2290 = new BooleanSetting(field2298, true);
    static String field2291;
    static String field2292;
    static String field2293;
    static String field2294;
    static String field2295;
    static String field2296;
    static String field2297;
    static String field2298;
    static String field2299;
    static String cfr_renamed_406;
    static float field2300;
    static float field2301;
    static float field2302;
    static float field2303;

    @EventHandler(2)
    public void method610(EventJump param1) {
        if (WindHop.I.player != null && this.field2273.isMode(field2274)) {
            this.method612();
        }
    }

    @EventHandler(2)
    public void method611(EventKey param1) {
        if (this.field2273.isMode(field2275) && ((Integer)this.field2276.getValue()).intValue() == param1.method3595() && !param1.method3599() && !param1.method3594()) {
            this.field2277 = true;
        }
    }

    private void method612() {
        float v7;
        Hand v5;
        if (WindHop.I.world == null || WindHop.I.player == null || WindHop.I.interactionManager == null) {
            return;
        }
        int v1 = this.method613();
        if (v1 == -1) {
            return;
        }
        boolean v2 = v1 == 40;
        boolean v3 = v1 < 9;
        int v4 = WindHop.I.player.getInventory().selectedSlot;
        Hand hand = v5 = v2 ? Hand.OFF_HAND : Hand.MAIN_HAND;
        if (v3 && !v2 && v1 != v4) {
            WindHop.I.player.networkHandler.sendPacket((Packet)new UpdateSelectedSlotC2SPacket(v1));
        }
        float v6 = WindHop.I.player.getYaw();
        if (!WindHop.I.player.isOnGround() && this.method615(0.0, field2278, 0.0) == Blocks.AIR && WindHop.I.player.getVelocity().y > field2279) {
            v7 = field2280;
            for (int v8 = 0; v8 < 360; v8 += 45) {
                float v10;
                BlockHitResult v9 = WindHop.I.world.raycast(new RaycastContext(WindHop.I.player.getEyePos(), WindHop.I.player.getEyePos().add(WindHop.I.player.getRotationVector(v7, (float)v8).multiply(field2281)), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, (Entity)WindHop.I.player));
                if (v9.getType() != HitResult.Type.BLOCK) continue;
                v6 = v10 = this.method617(WindHop.I.player.getYaw(), v8);
                break;
            }
        } else {
            v7 = field2282;
        }
        WindHop.I.player.networkHandler.sendPacket((Packet)new PlayerInteractItemC2SPacket(v5, 0, v6, v7));
        WindHop.I.player.swingHand(v5);
        if (v3 && !v2 && v1 != v4) {
            WindHop.I.player.networkHandler.sendPacket((Packet)new UpdateSelectedSlotC2SPacket(v4));
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.field2277 = false;
    }

    private int method613() {
        if (WindHop.I.player == null) {
            return -1;
        }
        if (WindHop.I.player.getOffHandStack().getItem() == Items.WIND_CHARGE) {
            return 40;
        }
        for (int v1 = 0; v1 < 9; ++v1) {
            if (WindHop.I.player.getInventory().getStack(v1).getItem() != Items.WIND_CHARGE) continue;
            return v1;
        }
        return -1;
    }

    @EventHandler(2)
    public void method614(EventUpdate param1) {
        if (WindHop.I.player == null || WindHop.I.world == null) {
            return;
        }
        int v2 = this.method613();
        if (v2 == -1) {
            return;
        }
        if (this.field2273.isMode(field2283)) {
            if (this.field2277) {
                this.method612();
                this.field2277 = false;
            }
            return;
        }
        boolean v3 = false;
        if (!WindHop.I.player.isOnGround() && this.method615(0.0, field2284, 0.0) == Blocks.AIR && WindHop.I.player.getVelocity().y > field2285) {
            float v4 = field2286;
            for (int v5 = 0; v5 < 360; v5 += 45) {
                BlockHitResult v6 = WindHop.I.world.raycast(new RaycastContext(WindHop.I.player.getEyePos(), WindHop.I.player.getEyePos().add(WindHop.I.player.getRotationVector(v4, (float)v5).multiply(field2287)), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, (Entity)WindHop.I.player));
                if (v6.getType() != HitResult.Type.BLOCK) continue;
                v3 = true;
                break;
            }
        } else if (((Boolean)this.field2288.getValue()).booleanValue()) {
            boolean bl = v3 = this.method615(0.0, field2289, 0.0) != Blocks.AIR && WindHop.I.player.fallDistance > 2.0f;
        }
        if (v3 && (WindHop.I.options.jumpKey.isPressed() || ((Boolean)this.field2290.getValue()).booleanValue()) && v2 != -1) {
            this.method612();
        }
        if (WindHop.I.player.isOnGround() && ((Boolean)this.field2290.getValue()).booleanValue() && v2 != -1) {
            WindHop.I.player.jump();
        }
    }

    private Block method615(double param1, double param3, double param5) {
        if (WindHop.I.player == null || WindHop.I.world == null) {
            return Blocks.AIR;
        }
        BlockPos v7 = BlockPos.ofFloored((Position)WindHop.I.player.getPos().add(param1, param3, param5));
        return WindHop.I.world.getBlockState(v7).getBlock();
    }

    public WindHop() {
        super(field2291, field2292, Combat.Movement);
        this.field2288 = new BooleanSetting(field2299, true);
        this.addSettings(this.field2273, this.field2276, this.field2290, this.field2288);
    }

    private float method617(float param1, float param2) {
        float v3;
        for (v3 = param1 + param2; v3 > field2300; v3 -= field2301) {
        }
        while (v3 < field2302) {
            v3 += field2303;
        }
        return v3;
    }

    static {
        field2291 = "Wind Hop";
        field2292 = "Использует ветряной заряд для прыжка";
        field2293 = "Режим";
        field2294 = "Автоматический";
        field2295 = "Автоматический";
        field2296 = "По бинду";
        field2297 = "Бинд";
        field2298 = "Автоматический прыжок";
        field2299 = "Предиктить прыжок";
        field2275 = "По бинду";
        field2274 = "Автоматический";
        field2283 = "По бинду";
        field2284 = -2.0;
        field2285 = 0.4f;
        field2286 = 75.0f;
        field2287 = 1.5;
        field2289 = -1.0;
        field2278 = -2.0;
        field2279 = 0.4f;
        field2280 = 75.0f;
        field2281 = 1.5;
        field2282 = 90.0f;
        field2300 = 180.0f;
        field2301 = 360.0f;
        field2302 = -180.0f;
        field2303 = 360.0f;
        cfr_renamed_406 = "По бинду";
    }
}



