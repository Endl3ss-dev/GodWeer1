/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.Entity$RemovalReason
 *  net.minecraft.entity.ItemEntity
 *  net.minecraft.entity.decoration.EndCrystalEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.entity.projectile.ProjectileUtil
 *  net.minecraft.item.Item
 *  net.minecraft.item.Items
 *  net.minecraft.util.Hand
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.hit.EntityHitResult
 *  net.minecraft.util.hit.HitResult$Type
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.RaycastContext
 *  net.minecraft.world.RaycastContext$FluidHandling
 *  net.minecraft.world.RaycastContext$ShapeType
 *  net.minecraft.world.World
 */
package minced.module.misc;
import minced.core.EventHandler;

import minced.core.BooleanSetting;
import minced.core.EventUpdate;
import minced.core.EventAttack;
import minced.core.Module;
import minced.enums.Combat;
import minced.mixin.accessor.IMinecraftClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class CrystalOptimizer
extends Module {
    static MinecraftClient I = MinecraftClient.getInstance();
    BooleanSetting field6724 = new BooleanSetting(field6728, true);
    EndCrystalEntity field6725 = null;
    static String field6726;
    static String field6727;
    static String field6728;
    static double field6729;
    static double field6730;
    static double field6731;
    static double field6732;

    private EndCrystalEntity method1611() {
        for (Entity v2 : CrystalOptimizer.I.world.getEntities()) {
            EndCrystalEntity v3;
            if (!this.method1615(v2) || !this.method1619(v3 = (EndCrystalEntity)v2) || !this.method1618(v3) || ((Boolean)this.field6724.getValue()).booleanValue() && this.method1617(v3.getBlockPos())) continue;
            return v3;
        }
        return null;
    }

    @EventHandler(2)
    public void method1612(EventUpdate param1) {
        if (CrystalOptimizer.I.player != null && CrystalOptimizer.I.world != null) {
            this.field6725 = this.method1611();
            if (this.field6725 != null && CrystalOptimizer.I.options.useKey.isPressed()) {
                ((IMinecraftClient)I).itemUseCooldown(0);
                this.method1614(this.field6725);
            }
        }
    }

    public EndCrystalEntity method1613() {
        return this.field6725;
    }

    private void method1614(EndCrystalEntity param1) {
        CrystalOptimizer.I.interactionManager.attackEntity((PlayerEntity)CrystalOptimizer.I.player, (Entity)param1);
        CrystalOptimizer.I.player.swingHand(Hand.MAIN_HAND);
    }

    private boolean method1615(Entity param1) {
        return param1 instanceof EndCrystalEntity;
    }

    public CrystalOptimizer() {
        super(field6726, field6727, Combat.Miscellaneous);
        this.addSettings(this.field6724);
    }

    private boolean method1617(BlockPos param1) {
        if (CrystalOptimizer.I.world == null) {
            return false;
        }
        double v2 = field6729;
        Box v4 = new Box((double)param1.getX() - v2, (double)param1.getY() - v2, (double)param1.getZ() - v2, (double)param1.getX() + v2 + 1.0, (double)param1.getY() + v2 + field6730, (double)param1.getZ() + v2 + 1.0);
        for (Entity v6 : CrystalOptimizer.I.world.getOtherEntities(null, v4)) {
            ItemEntity v7;
            Item v8;
            if (!(v6 instanceof ItemEntity) || (v8 = (v7 = (ItemEntity)v6).getStack().getItem()) != Items.NETHERITE_HELMET && v8 != Items.NETHERITE_CHESTPLATE && v8 != Items.NETHERITE_LEGGINGS && v8 != Items.NETHERITE_BOOTS && v8 != Items.DIAMOND_HELMET && v8 != Items.DIAMOND_CHESTPLATE && v8 != Items.DIAMOND_LEGGINGS && v8 != Items.DIAMOND_BOOTS && v8 != Items.NETHERITE_SWORD && v8 != Items.DIAMOND_SWORD && v8 != Items.NETHERITE_PICKAXE && v8 != Items.DIAMOND_PICKAXE && v8 != Items.NETHERITE_SHOVEL && v8 != Items.DIAMOND_SHOVEL && v8 != Items.NETHERITE_AXE && v8 != Items.DIAMOND_AXE && v8 != Items.TOTEM_OF_UNDYING && v8 != Items.END_CRYSTAL && v8 != Items.ENCHANTED_GOLDEN_APPLE && v8 != Items.GOLDEN_APPLE && v8 != Items.ENDER_PEARL && v8 != Items.TRIDENT && v8 != Items.CROSSBOW && v8 != Items.ELYTRA) continue;
            return true;
        }
        return false;
    }

    private boolean method1618(EndCrystalEntity param12) {
        Vec3d v5;
        BlockPos v2 = param12.getBlockPos();
        BlockPos v3 = v2.down();
        Vec3d v4 = CrystalOptimizer.I.player.getEyePos();
        EntityHitResult v6 = ProjectileUtil.getEntityCollision((World)CrystalOptimizer.I.world, (Entity)CrystalOptimizer.I.player, (Vec3d)v4, (Vec3d)(v5 = v4.add(CrystalOptimizer.I.player.getRotationVec(1.0f).multiply(field6731))), (Box)new Box(v4, v5).expand(1.0), param1 -> param1 == param12);
        if (v6 != null) {
            return true;
        }
        BlockHitResult v7 = CrystalOptimizer.I.world.raycast(new RaycastContext(v4, v5, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, (Entity)CrystalOptimizer.I.player));
        if (v7.getType() == HitResult.Type.BLOCK) {
            BlockPos v8 = v7.getBlockPos();
            return v8.equals((Object)v3);
        }
        return false;
    }

    private boolean method1619(EndCrystalEntity param1) {
        double v2 = CrystalOptimizer.I.player.distanceTo((Entity)param1);
        return v2 <= field6732;
    }

    @EventHandler(2)
    public void method1620(EventAttack param1) {
        Entity v3 = param1.method2471();
        if (v3 instanceof EndCrystalEntity) {
            EndCrystalEntity v2 = (EndCrystalEntity)v3;
            v2.remove(Entity.RemovalReason.DISCARDED);
        }
    }

    static {
        field6726 = "CrystalOptimizer";
        field6727 = "Оптимизация ломания кристаллов: при наведении и зажатом ПКМ бьёт кристалл без кулдауна использования";
        field6728 = "Не бить при дропе рядом";
        field6732 = 3.0;
        field6731 = 3.0;
        field6729 = 3.0;
        field6730 = 2.0;
    }
}



