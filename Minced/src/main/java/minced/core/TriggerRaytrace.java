/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.entity.Entity
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
import minced.core.CancellableEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

public class TriggerRaytrace
implements CancellableEvent {
    static MinecraftClient I = MinecraftClient.getInstance();
    static double field8567;

    public static boolean O(float param0, float param1, double param2, Entity param4, boolean param5) {
        Box v11;
        Vec3d v8;
        Vec3d v9;
        if (TriggerRaytrace.I.player == null || TriggerRaytrace.I.world == null || param4 == null) {
            return false;
        }
        float v6 = param5 ? 1.0f : I.getRenderTickCounter().getTickDelta(true);
        Vec3d v7 = TriggerRaytrace.I.player.getCameraPosVec(v6);
        BlockHitResult v10 = TriggerRaytrace.I.world.raycast(new RaycastContext(v7, v9 = v7.add((v8 = TriggerRaytrace.I.player.getRotationVector(param1, param0)).multiply(param2)), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, (Entity)TriggerRaytrace.I.player));
        if (v10.getType() != HitResult.Type.MISS) {
            v9 = v10.getPos();
        }
        return (v11 = param4.getBoundingBox()).contains(v7) || v11.raycast(v7, v9).isPresent();
    }

    public static boolean method2399(Entity param0, double param1) {
        BlockHitResult v9;
        double v6;
        Vec3d v4;
        if (param0 == null || TriggerRaytrace.I.world == null || TriggerRaytrace.I.player == null) {
            return false;
        }
        Vec3d v3 = TriggerRaytrace.I.player.getEyePos();
        if (v3.squaredDistanceTo(v4 = param0.getBoundingBox().getCenter()) > param1 * param1) {
            return false;
        }
        Optional v5 = param0.getBoundingBox().raycast(v3, v4);
        if (v5.isPresent()) {
            v6 = v3.distanceTo((Vec3d)v5.get());
            RaycastContext v8 = new RaycastContext(v3, v4, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, (Entity)TriggerRaytrace.I.player);
            v9 = TriggerRaytrace.I.world.raycast(v8);
            if (v9.getType() == HitResult.Type.MISS) {
                return true;
            }
        } else {
            return param0.getBoundingBox().contains(v3);
        }
        double v10 = v9.getPos().distanceTo(v3);
        return v10 >= v6 - field8567;
    }

    public static boolean method2400(float param0, float param1, double param2, Entity param4) {
        if (param4 == null) {
            return false;
        }
        Vec3d v5 = TriggerRaytrace.I.player.getCameraPosVec(I.getRenderTickCounter().getTickDelta(true));
        Vec3d v6 = TriggerRaytrace.I.player.getRotationVector(param1, param0);
        Vec3d v7 = v5.add(v6.multiply(param2));
        Box v8 = param4.getBoundingBox();
        return v8.contains(v5) || v8.raycast(v5, v7).isPresent();
    }

    public static boolean O(float param0, float param1, double param2, Entity param4) {
        Vec3d v5 = TriggerRaytrace.I.player.getCameraPosVec(I.getRenderTickCounter().getTickDelta(true));
        Vec3d v6 = TriggerRaytrace.I.player.getRotationVector(param1, param0);
        Vec3d v7 = v5.add(v6.multiply(param2));
        Box v8 = param4.getBoundingBox();
        return v8.contains(v5) || v8.raycast(v5, v7).isPresent();
    }

    public static boolean method2401(float param0, float param1, double param2, Entity param4, boolean param5) {
        Vec3d v11;
        RaycastContext v12;
        boolean v10;
        Vec3d v6 = TriggerRaytrace.I.player.getCameraPosVec(I.getRenderTickCounter().getTickDelta(true));
        Vec3d v7 = TriggerRaytrace.I.player.getRotationVector(param1, param0);
        Vec3d v8 = v6.add(v7.multiply(param2));
        Box v9 = param4.getBoundingBox();
        boolean bl = v10 = v9.contains(v6) || v9.raycast(v6, v8).isPresent();
        if (!v10) {
            return false;
        }
        return !param5 || TriggerRaytrace.I.world.raycast(v12 = new RaycastContext(v6, v11 = v9.contains(v6) ? v6 : (Vec3d)v9.raycast(v6, v8).get(), RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, (Entity)TriggerRaytrace.I.player)).getType() == HitResult.Type.MISS;
    }

    static {
        field8567 = 0.05;
    }
}



