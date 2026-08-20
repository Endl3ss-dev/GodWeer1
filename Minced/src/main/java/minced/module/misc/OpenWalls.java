/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.util.Hand
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Position
 *  net.minecraft.util.math.Vec3d
 */
package minced.module.misc;
import minced.core.EventHandler;

import java.util.HashSet;
import java.util.Set;
import minced.core.EventUpdate;
import minced.core.Module;
import minced.enums.Combat;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;

public class OpenWalls
extends Module {
    static MinecraftClient I = MinecraftClient.getInstance();
    static String field2221;
    static String field2222;
    Set field2223 = new HashSet();
    static double field2224;
    static double field2225;
    static double field2226;
    static double field2227;

    public Vec3d cfr_underscore(float param1) {
        if (OpenWalls.I.player == null) {
            return Vec3d.ZERO;
        }
        double v2 = MathHelper.lerp((double)param1, (double)OpenWalls.I.player.prevX, (double)OpenWalls.I.player.getX());
        double v4 = MathHelper.lerp((double)param1, (double)OpenWalls.I.player.prevY, (double)OpenWalls.I.player.getY()) + (double)OpenWalls.I.player.getEyeHeight(OpenWalls.I.player.getPose());
        double v6 = MathHelper.lerp((double)param1, (double)OpenWalls.I.player.prevZ, (double)OpenWalls.I.player.getZ());
        return new Vec3d(v2, v4, v6);
    }

    public OpenWalls() {
        super(field2221, field2222, Combat.Miscellaneous);
    }

    @EventHandler(2)
    public void method602(EventUpdate param1) {
        if (OpenWalls.I.player == null || OpenWalls.I.world == null || OpenWalls.I.interactionManager == null) {
            return;
        }
        if (!OpenWalls.I.options.useKey.isPressed() || OpenWalls.I.player.isSneaking()) {
            return;
        }
        Vec3d v2 = new Vec3d(0.0, 0.0, field2224).rotateY(-((float)Math.toRadians(OpenWalls.I.player.getYaw()))).rotateX((float)Math.toRadians(OpenWalls.I.player.getPitch()));
        this.field2223.clear();
        for (int v3 = 1; v3 < 50; ++v3) {
            BlockPos v4 = BlockPos.ofFloored((Position)this.cfr_underscore(I.getRenderTickCounter().getTickDelta(true)).add(v2.multiply((double)v3)));
            if (this.field2223.contains(v4)) continue;
            this.field2223.add(v4);
            BlockEntity v5 = OpenWalls.I.world.getBlockEntity(v4);
            if (v5 == null) continue;
            Vec3d v6 = new Vec3d((double)v4.getX() + field2225, (double)v4.getY() + field2226, (double)v4.getZ() + field2227);
            Vec3d v7 = OpenWalls.I.player.getEyePos();
            Vec3d v8 = v6.subtract(v7);
            v8 = v8.negate();
            Direction v9 = Direction.getFacing((float)((float)v8.x), (float)((float)v8.y), (float)((float)v8.z));
            OpenWalls.I.interactionManager.interactBlock(OpenWalls.I.player, Hand.MAIN_HAND, new BlockHitResult(v6, v9, v4, false));
            return;
        }
    }

    static {
        field2221 = "Open Walls";
        field2222 = "Позволяет открывать контейнеры через стены";
        field2224 = 0.1;
        field2225 = 0.5;
        field2226 = 0.5;
        field2227 = 0.5;
    }
}



