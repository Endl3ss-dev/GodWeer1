/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.hit.HitResult$Type
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.RaycastContext
 *  net.minecraft.world.RaycastContext$FluidHandling
 *  net.minecraft.world.RaycastContext$ShapeType
 */
package minced.core;

import java.util.Optional;
import minced.core.TriggerRaytrace;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

public final class BlockUtil {
    static MinecraftClient I = MinecraftClient.getInstance();
    static double field12541;
    static double field12542;

    public static boolean method3566(Entity param0, double param1, float param3, float param4) {
        LivingEntity v5;
        if (BlockUtil.I.player == null || param0 == null) {
            return false;
        }
        if (param0 instanceof LivingEntity && TriggerRaytrace.O(param3, param4, param1, (Entity)(v5 = (LivingEntity)param0), true)) {
            return true;
        }
        Vec3d v5s = BlockUtil.I.player.getEyePos();
        Box v6 = param0.getBoundingBox();
        if (v6.expand(field12541).contains((Vec3d)v5s)) {
            return true;
        }
        Vec3d v7 = BlockUtil.method3568(param4, param3).normalize();
        Vec3d v8 = v5s.add(v7.multiply(param1));
        Optional v9 = v6.raycast((Vec3d)v5s, v8);
        if (v9.isEmpty()) {
            return false;
        }
        return v5s.squaredDistanceTo((Vec3d)v9.get()) <= param1 * param1;
    }

    public static boolean method3567(Entity param0) {
        if (param0 == null || BlockUtil.I.player == null || BlockUtil.I.world == null) {
            return false;
        }
        BlockHitResult v1 = BlockUtil.I.world.raycast(new RaycastContext(BlockUtil.I.player.getEyePos(), param0.getEyePos(), RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, (Entity)BlockUtil.I.player));
        if (v1.getType() != HitResult.Type.BLOCK) {
            return false;
        }
        BlockHitResult v2 = v1;
        BlockState v3 = BlockUtil.I.world.getBlockState(v2.getBlockPos());
        return v3.isOf(Blocks.SHORT_GRASS) || v3.isOf(Blocks.TALL_GRASS) || v3.isOf(Blocks.FERN) || v3.isOf(Blocks.LARGE_FERN) || v3.isOf(Blocks.DEAD_BUSH) || v3.isOf(Blocks.SEAGRASS) || v3.isOf(Blocks.TALL_SEAGRASS) || v3.isOf(Blocks.KELP) || v3.isOf(Blocks.KELP_PLANT);
    }

    private static Vec3d method3568(float param0, float param1) {
        float v2 = (float)Math.toRadians(param1);
        float v3 = (float)Math.toRadians(param0);
        float v4 = (float)Math.cos(-v3);
        return new Vec3d(-Math.sin(v2) * (double)v4, -Math.sin(v3), Math.cos(v2) * (double)v4);
    }

    public static boolean method3569(Entity param0) {
        if (param0 == null || BlockUtil.I.player == null || BlockUtil.I.world == null) {
            return false;
        }
        return BlockUtil.I.world.raycast(new RaycastContext(BlockUtil.I.player.getEyePos(), param0.getEyePos(), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, (Entity)BlockUtil.I.player)).getType() == HitResult.Type.MISS;
    }

    private BlockUtil() {
    }

    public static boolean method3570(LivingEntity param0) {
        if (BlockUtil.I.player == null || param0 == null) {
            return false;
        }
        return param0.getBoundingBox().expand(field12542).contains(BlockUtil.I.player.getEyePos());
    }

    static {
        field12541 = 1.0E-4;
        field12542 = 1.0E-4;
    }
}



