/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.ShulkerBoxBlockEntity
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.vehicle.BoatEntity
 *  net.minecraft.util.math.BlockPos
 */
package minced.module.movement;
import minced.core.EventHandler;

import java.util.function.BooleanSupplier;
import minced.core.SliderSetting;
import minced.core.ModeSetting;
import minced.core.EventUpdate;
import minced.core.Module;
import minced.enums.Combat;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.math.BlockPos;

public class HighJump
extends Module {
    ModeSetting field3268 = new ModeSetting(field3278, field3279, field3280, field3281);
    static String field3269;
    static MinecraftClient I = MinecraftClient.getInstance();
    static double field3270;
    static String field3271;
    static double field3272;
    static double field3273;
    static double field3274;
    SliderSetting field3275 = new SliderSetting(field3282, 2.0f, 1.0f, field3283, field3284);
    static String field3276;
    static String field3277;
    static String field3278;
    static String field3279;
    static String field3280;
    static String field3281;
    static String field3282;
    static float field3283;
    static float field3284;

    @EventHandler(2)
    public void method921(EventUpdate param1) {
        if (this.field3268.isMode(field3269)) {
            for (Entity v3 : HighJump.I.world.getEntities()) {
                if (!(v3 instanceof BoatEntity) || !(HighJump.I.player.distanceTo(v3) < 2.0f)) continue;
                HighJump.I.options.jumpKey.setPressed(false);
                if (!HighJump.I.player.isOnGround()) continue;
                HighJump.I.player.setVelocity(0.0, field3270, 0.0);
            }
        }
        if (this.field3268.isMode(field3271)) {
            BlockPos v2 = HighJump.I.player.getBlockPos();
            for (BlockPos v5 : BlockPos.iterate((BlockPos)v2.add(-3, -3, -3), (BlockPos)v2.add(3, 3, 3))) {
                BlockEntity v6 = HighJump.I.world.getBlockEntity(v5);
                if (!(v6 instanceof ShulkerBoxBlockEntity)) continue;
                ShulkerBoxBlockEntity v7 = (ShulkerBoxBlockEntity)v6;
                double v8 = HighJump.I.player.getX() - ((double)v5.getX() + field3272);
                double v10 = HighJump.I.player.getZ() - ((double)v5.getZ() + field3273);
                double v12 = Math.sqrt(v8 * v8 + v10 * v10);
                double v14 = Math.abs(HighJump.I.player.getY() - ((double)v5.getY() + field3274));
                if (!(v12 <= 1.0)) continue;
                int n = HighJump.I.player.getVelocity().y > 1.0 ? 30 : 2;
                if (!(v14 <= (double)n) || HighJump.I.player.fallDistance != 0.0f || !(v7.getAnimationProgress(1.0f) > 0.0f) || v7.getAnimationProgress(1.0f) == 1.0f) continue;
                HighJump.I.player.setVelocity(HighJump.I.player.getVelocity().x, (double)((Float)this.field3275.getValue()).floatValue(), HighJump.I.player.getVelocity().z);
                if (HighJump.I.currentScreen == null) continue;
                HighJump.I.currentScreen.close();
            }
        }
    }

    public HighJump() {
        super(field3276, field3277, Combat.Movement);
        this.addSettings(this.field3268, this.field3275);
    }

    static {
        field3276 = "High Jump";
        field3277 = "Позволяет прыгнуть на большую высоту";
        field3278 = "Мод";
        field3279 = "Grim Boat";
        field3280 = "Grim Boat";
        field3281 = "Grim Shulker";
        field3282 = "Высота прыжка";
        field3283 = 3.0f;
        field3284 = 0.05f;
        field3269 = "Grim Boat";
        field3270 = 2.12;
        field3271 = "Grim Shulker";
        field3272 = 0.5;
        field3273 = 0.5;
        field3274 = 0.5;
    }
}



