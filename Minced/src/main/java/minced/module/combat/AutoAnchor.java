/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Blocks
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.item.Items
 *  net.minecraft.util.Hand
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  sg.ec.t0
 */
package minced.module.combat;
import minced.core.EventHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import minced.core.SliderSetting;
import minced.core.EventUpdate;
import minced.core.Rotation;
import minced.core.InventoryUtil;
import minced.core.AimRotation;
import minced.core.Module;
import minced.enums.Combat;
import minced.mixin.accessor.IClientPlayerInteractionManagerMixin;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import sg.ec.t0;

public class AutoAnchor
extends Module {
    static MinecraftClient I = MinecraftClient.getInstance();
    static double field4291;
    static float field4292;
    int field4293 = -1;
    int field4294 = 0;
    long field4295 = 0L;
    boolean field4296 = false;
    SliderSetting field4297;
    boolean field4298 = false;
    BlockPos field4299 = null;
    BlockPos field4300 = null;
    List field4301;
    static double field4302;
    static float field4303;
    static float field4304;
    SliderSetting field4305 = new SliderSetting(field4308, field4309, 1.0f, field4310, 1.0f);
    static String field4306;
    static String field4307;
    static String field4308;
    static float field4309;
    static float field4310;
    static String field4311;
    static float field4312;
    static float field4313;
    static String field4314;
    static float field4315;
    static float field4316;
    static float field4317;
    SliderSetting field4318;
    static double field4319;
    static double field4320;
    static double field4321;

    private void method1096(BlockPos param1, int param2) {
        Vec3d v3 = AutoAnchor.I.player.getCameraPosVec(I.getRenderTickCounter().getTickDelta(true));
        Vec3d v4 = t0.cfr_renamed_214((Vec3d)v3, (Box)new Box(param1));
        Vec3d v5 = v4.subtract(v3);
        float v6 = (float)MathHelper.wrapDegrees((double)(Math.toDegrees(Math.atan2(v5.z, v5.x)) - field4291));
        float v7 = (float)(-Math.toDegrees(Math.atan2(v5.y, Math.hypot(v5.x, v5.z))));
        AimRotation.method367(new Rotation(v6, v7), field4292, 1, 7);
        this.field4293 = AutoAnchor.I.player.getInventory().selectedSlot;
        if (param2 < 9) {
            AutoAnchor.I.player.getInventory().selectedSlot = param2;
        } else {
            InventoryUtil.method3311(param2, this.field4293);
            AutoAnchor.I.player.getInventory().selectedSlot = this.field4293;
        }
        ((IClientPlayerInteractionManagerMixin)AutoAnchor.I.interactionManager).invokeSyncSelectedSlot();
        v5 = v5.negate();
        AutoAnchor.I.interactionManager.interactBlock(AutoAnchor.I.player, Hand.MAIN_HAND, new BlockHitResult(v4, Direction.getFacing((float)((float)v5.x), (float)((float)v5.y), (float)((float)v5.z)), param1, false));
        AutoAnchor.I.player.swingHand(Hand.MAIN_HAND);
        ++this.field4294;
        this.field4295 = System.currentTimeMillis();
        this.field4296 = true;
        if ((float)this.field4294 >= ((Float)this.field4297.getValue()).floatValue()) {
            this.field4298 = true;
            this.field4300 = this.field4299;
            this.field4301.add(this.field4299);
            this.field4299 = null;
            this.field4294 = 0;
        }
    }

    private void method1097(BlockPos param1) {
        Vec3d v2 = AutoAnchor.I.player.getCameraPosVec(I.getRenderTickCounter().getTickDelta(true));
        Vec3d v3 = t0.cfr_renamed_214((Vec3d)v2, (Box)new Box(param1));
        Vec3d v4 = v3.subtract(v2);
        float v5 = (float)MathHelper.wrapDegrees((double)(Math.toDegrees(Math.atan2(v4.z, v4.x)) - field4302));
        float v6 = (float)(-Math.toDegrees(Math.atan2(v4.y, Math.hypot(v4.x, v4.z))));
        AimRotation.method367(new Rotation(v5, v6), field4303, 1, 7);
        v4 = v4.negate();
        AutoAnchor.I.interactionManager.interactBlock(AutoAnchor.I.player, Hand.MAIN_HAND, new BlockHitResult(v3, Direction.getFacing((float)((float)v4.x), (float)((float)v4.y), (float)((float)v4.z)), param1, false));
        AutoAnchor.I.player.swingHand(Hand.MAIN_HAND);
    }

    @Override
    public void onEnable() {
        this.field4301.clear();
        this.field4299 = null;
        this.field4294 = 0;
        this.field4298 = false;
        this.field4300 = null;
        super.onEnable();
    }

    @EventHandler(2)
    private void method1098(EventUpdate param1) {
        long v4;
        if (this.field4296) {
            this.field4296 = false;
            if (this.field4293 != -1) {
                AutoAnchor.I.player.getInventory().selectedSlot = this.field4293;
                ((IClientPlayerInteractionManagerMixin)AutoAnchor.I.interactionManager).invokeSyncSelectedSlot();
                this.field4293 = -1;
            }
            return;
        }
        if (this.field4298 && this.field4300 != null) {
            this.method1097(this.field4300);
            this.field4298 = false;
            this.field4300 = null;
            return;
        }
        long v2 = System.currentTimeMillis();
        if (v2 - this.field4295 < (v4 = (long)(field4304 / ((Float)this.field4305.getValue()).floatValue()))) {
            return;
        }
        int v6 = InventoryUtil.method3336(Items.GLOWSTONE);
        if (v6 == -1) {
            return;
        }
        if (this.field4299 == null || !this.method1099(this.field4299)) {
            this.field4299 = this.method1100();
        }
        if (this.field4299 != null) {
            this.method1096(this.field4299, v6);
        }
    }

    private boolean method1099(BlockPos param1) {
        if (AutoAnchor.I.world.getBlockState(param1).getBlock() != Blocks.RESPAWN_ANCHOR) {
            return false;
        }
        return !this.field4301.contains(param1);
    }

    public AutoAnchor() {
        super(field4306, field4307, Combat.Combat);
        this.field4297 = new SliderSetting(field4311, field4312, 1.0f, field4313, 1.0f);
        this.field4318 = new SliderSetting(field4314, field4315, 1.0f, field4316, field4317);
        this.field4301 = new ArrayList();
        this.addSettings(this.field4305, this.field4297, this.field4318);
    }

    @Override
    public void onDisable() {
        this.field4301.clear();
        this.field4299 = null;
        this.field4294 = 0;
        this.field4298 = false;
        this.field4300 = null;
        this.field4293 = -1;
        this.field4296 = false;
        super.onDisable();
    }

    private BlockPos method1100() {
        BlockPos v1 = AutoAnchor.I.player.getBlockPos();
        double v2 = ((Float)this.field4318.getValue()).floatValue();
        BlockPos v4 = null;
        double v5 = v2 * v2;
        int v7 = (int)Math.ceil(v2);
        for (int v8 = -v7; v8 <= v7; ++v8) {
            for (int v9 = -v7; v9 <= v7; ++v9) {
                for (int v10 = -v7; v10 <= v7; ++v10) {
                    double v12;
                    BlockPos v11 = v1.add(v8, v9, v10);
                    if (AutoAnchor.I.world.getBlockState(v11).getBlock() != Blocks.RESPAWN_ANCHOR || this.field4301.contains(v11) || !((v12 = AutoAnchor.I.player.squaredDistanceTo((double)v11.getX() + field4319, (double)v11.getY() + field4320, (double)v11.getZ() + field4321)) < v5)) continue;
                    v5 = v12;
                    v4 = v11;
                }
            }
        }
        return v4;
    }

    static {
        field4306 = "Auto Anchor";
        field4307 = "Автоматически вставляет светокамень в якоря";
        field4308 = "Скорость";
        field4309 = 10.0f;
        field4310 = 50.0f;
        field4311 = "Макс. вставок";
        field4312 = 4.0f;
        field4313 = 10.0f;
        field4314 = "Радиус";
        field4315 = 5.0f;
        field4316 = 10.0f;
        field4317 = 0.5f;
        field4304 = 1000.0f;
        field4319 = 0.5;
        field4320 = 0.5;
        field4321 = 0.5;
        field4291 = 90.0;
        field4292 = 180.0f;
        field4302 = 90.0;
        field4303 = 180.0f;
    }
}



