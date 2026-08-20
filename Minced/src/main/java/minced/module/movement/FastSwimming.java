/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.Blocks
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.util.math.BlockPos
 */
package minced.module.movement;
import minced.core.EventHandler;

import java.util.function.BooleanSupplier;
import minced.core.EventUpdate;
import minced.core.Rotation;
import minced.core.AimRotation;
import minced.core.Module;
import minced.enums.Combat;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;

public class FastSwimming
extends Module {
    static MinecraftClient I = MinecraftClient.getInstance();
    static float _0;
    static float cfr_renamed_383;
    static float field6570;
    static float field6571;
    static float field6572;
    static float field6573;
    static String field6574;
    static String _1;

    @EventHandler(2)
    public void method1550(EventUpdate param1) {
        int v7;
        if (FastSwimming.I.player == null || FastSwimming.I.world == null) {
            return;
        }
        boolean v2 = FastSwimming.I.options.jumpKey.isPressed();
        boolean v3 = FastSwimming.I.options.sneakKey.isPressed();
        boolean v4 = true;
        boolean v5 = true;
        boolean v6 = false;
        for (v7 = 0; v7 < 10; ++v7) {
            if (this.method1551(0, v7, 0) != Blocks.WATER && this.method1551(0, v7, 0) != Blocks.WATER_CAULDRON) {
                v4 = false;
            }
            if (this.method1551(0, -v7, 0) == Blocks.WATER || this.method1551(0, -v7, 0) == Blocks.WATER_CAULDRON) continue;
            v5 = false;
        }
        for (v7 = 0; v7 < 3; ++v7) {
            if (this.method1551(0, v7, 0) == Blocks.WATER || this.method1551(0, v7, 0) == Blocks.WATER_CAULDRON) continue;
            v6 = true;
        }
        if (FastSwimming.I.player.isSubmergedInWater() && (FastSwimming.I.options.jumpKey.isPressed() || FastSwimming.I.options.sneakKey.isPressed()) && (v4 || v5) && !v6) {
            FastSwimming.I.player.setSprinting(true);
            if (v2) {
                AimRotation.method369(new Rotation(FastSwimming.I.player.getYaw(), _0), cfr_renamed_383, field6570, 0, 6);
            } else if (v3) {
                AimRotation.method369(new Rotation(FastSwimming.I.player.getYaw(), field6571), field6572, field6573, 0, 6);
            }
        }
    }

    public FastSwimming() {
        super(field6574, _1, Combat.Movement);
    }

    private Block method1551(int param1, int param2, int param3) {
        if (FastSwimming.I.player == null || FastSwimming.I.world == null) {
            return Blocks.AIR;
        }
        BlockPos v4 = FastSwimming.I.player.getBlockPos();
        BlockPos v5 = v4.add(param1, param2, param3);
        return FastSwimming.I.world.getBlockState(v5).getBlock();
    }

    static {
        field6574 = "Fast Swimming";
        _1 = "Позволяет ускорять ваше движение по Y в воде";
        _0 = -90.0f;
        cfr_renamed_383 = 180.0f;
        field6570 = 180.0f;
        field6571 = 90.0f;
        field6572 = 180.0f;
        field6573 = 180.0f;
    }
}



