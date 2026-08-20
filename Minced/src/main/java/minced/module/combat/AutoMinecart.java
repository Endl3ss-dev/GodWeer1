/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Blocks
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.entity.projectile.ArrowEntity
 *  net.minecraft.item.Item
 *  net.minecraft.item.Items
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket$LookAndOnGround
 *  net.minecraft.screen.slot.SlotActionType
 *  net.minecraft.util.Hand
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.hit.HitResult$Type
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Position
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.RaycastContext
 *  net.minecraft.world.RaycastContext$FluidHandling
 *  net.minecraft.world.RaycastContext$ShapeType
 */
package minced.module.combat;
import minced.core.EventHandler;

import minced.core.BooleanSetting;
import minced.core.SliderSetting;
import minced.core.EventUpdate;
import minced.core.EventAddEntity;
import minced.core.Rotation;
import minced.core.AimRotation;
import minced.core.Module;
import minced.enums.Combat;
import minced.mixin.accessor.IClientPlayerInteractionManagerMixin;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.RaycastContext;

public class AutoMinecart
extends Module {
    static MinecraftClient I = MinecraftClient.getInstance();
    BlockPos field4746;
    boolean field4747;
    boolean field4748;
    int field4749;
    static double field4750;
    static double field4751;
    static double field4752;
    static String field4753;
    static String field4754;
    static String field4755;
    static float field4756;
    static float field4757;
    static float field4758;
    SliderSetting field4759 = new SliderSetting(field4755, field4756, 1.0f, field4757, field4758);
    static String field4760;
    BooleanSetting field4761 = new BooleanSetting(field4760, true);
    int field4762 = -1;
    int field4763 = -1;
    int field4764 = -1;
    int field4765 = -1;
    int field4766 = -1;
    int field4767;
    static double field4768;
    static double field4769;
    int field4770;
    static double field4771;
    static double field4772;
    static double field4773;
    static double field4774;
    static double field4775;
    static float field4776;

    @Override
    public void onDisable() {
        super.onDisable();
        if (AutoMinecart.I.player != null && AutoMinecart.I.interactionManager != null) {
            if (this.field4746 != null || this.field4747 || this.field4748) {
                AutoMinecart.I.player.getInventory().selectedSlot = this.field4749;
                ((IClientPlayerInteractionManagerMixin)AutoMinecart.I.interactionManager).invokeSyncSelectedSlot();
            }
            this.method1196();
        }
        this.method1197();
    }

    private BlockHitResult method1190(ArrowEntity param1) {
        if (AutoMinecart.I.player == null || AutoMinecart.I.world == null) {
            return null;
        }
        Vec3d v2 = param1.getPos();
        Vec3d v3 = param1.getVelocity();
        for (int v4 = 0; v4 <= 150; ++v4) {
            Vec3d v5 = v2;
            v2 = v2.add(v3);
            Vec3d v6 = v3;
            v6 = param1.isTouchingWater() || AutoMinecart.I.world.getBlockState(BlockPos.ofFloored((Position)v5)).isOf(Blocks.WATER) ? v6.multiply(field4750) : v6.multiply(field4751);
            if (!param1.hasNoGravity()) {
                v6 = new Vec3d(v6.x, v6.y - field4752, v6.z);
            }
            v3 = v6;
            RaycastContext v7 = new RaycastContext(v5, v2, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, (Entity)AutoMinecart.I.player);
            BlockHitResult v8 = AutoMinecart.I.world.raycast(v7);
            if (v8.getType() == HitResult.Type.BLOCK) {
                return v8;
            }
            if (v2.y <= 0.0) break;
        }
        return null;
    }

    public AutoMinecart() {
        super(field4753, field4754, Combat.Combat);
        this.addSettings(this.field4759, this.field4761);
    }

    @EventHandler(2)
    private void method1191(EventUpdate param1) {
        if (AutoMinecart.I.player == null || AutoMinecart.I.world == null || AutoMinecart.I.interactionManager == null) {
            this.method1197();
            return;
        }
        if (this.field4747) {
            this.field4747 = false;
            AutoMinecart.I.player.getInventory().selectedSlot = this.field4749;
            ((IClientPlayerInteractionManagerMixin)AutoMinecart.I.interactionManager).invokeSyncSelectedSlot();
            this.method1196();
            return;
        }
        if (this.field4746 == null) {
            return;
        }
        double v2 = ((Float)this.field4759.getValue()).floatValue();
        double v4 = v2 * v2;
        if (AutoMinecart.I.player.getCameraPosVec(I.getRenderTickCounter().getTickDelta(true)).squaredDistanceTo(this.field4746.toCenterPos()) > v4) {
            this.field4746 = null;
            this.field4748 = false;
            this.field4747 = true;
            return;
        }
        if (this.field4748) {
            if (!AutoMinecart.I.world.getBlockState(this.field4746).isReplaceable()) {
                this.field4746 = null;
                this.field4748 = false;
                this.field4747 = true;
                return;
            }
            BlockPos v6 = this.field4746.down();
            if (!AutoMinecart.I.world.getBlockState(v6).isSolidBlock((BlockView)AutoMinecart.I.world, v6)) {
                this.field4746 = null;
                this.field4748 = false;
                this.field4747 = true;
                return;
            }
            this.method1195(this.field4746.toCenterPos());
            AutoMinecart.I.player.getInventory().selectedSlot = this.field4767;
            ((IClientPlayerInteractionManagerMixin)AutoMinecart.I.interactionManager).invokeSyncSelectedSlot();
            Vec3d v7 = new Vec3d((double)v6.getX() + field4768, (double)v6.getY() + 1.0, (double)v6.getZ() + field4769);
            AutoMinecart.I.interactionManager.interactBlock(AutoMinecart.I.player, Hand.MAIN_HAND, new BlockHitResult(v7, Direction.UP, v6, false));
            AutoMinecart.I.player.swingHand(Hand.MAIN_HAND);
            this.field4767 = this.field4770;
            this.field4748 = false;
        } else {
            this.method1195(this.field4746.toCenterPos());
            AutoMinecart.I.player.getInventory().selectedSlot = this.field4767;
            ((IClientPlayerInteractionManagerMixin)AutoMinecart.I.interactionManager).invokeSyncSelectedSlot();
            Vec3d v6 = this.field4746.toCenterPos().add(0.0, field4771, 0.0);
            AutoMinecart.I.interactionManager.interactBlock(AutoMinecart.I.player, Hand.MAIN_HAND, new BlockHitResult(v6, Direction.UP, this.field4746, false));
            AutoMinecart.I.player.swingHand(Hand.MAIN_HAND);
            this.field4747 = true;
            this.field4746 = null;
        }
    }

    private int method1192(Item param1, int param2, boolean param3) {
        int v5;
        if (AutoMinecart.I.player == null || AutoMinecart.I.interactionManager == null) {
            return -1;
        }
        int v4 = -1;
        for (v5 = 9; v5 < 36; ++v5) {
            if (AutoMinecart.I.player.getInventory().getStack(v5).getItem() != param1) continue;
            v4 = v5;
            break;
        }
        if (v4 == -1) {
            return -1;
        }
        v5 = -1;
        for (int v6 = 0; v6 < 9; ++v6) {
            if (v6 == param2 || !AutoMinecart.I.player.getInventory().getStack(v6).isEmpty()) continue;
            v5 = v6;
            break;
        }
        if (v5 == -1) {
            v5 = param2 == 8 ? 7 : 8;
        }
        AutoMinecart.I.interactionManager.clickSlot(AutoMinecart.I.player.currentScreenHandler.syncId, v4, v5, SlotActionType.SWAP, (PlayerEntity)AutoMinecart.I.player);
        if (param3) {
            this.field4763 = v4;
            this.field4764 = v5;
        } else {
            this.field4765 = v4;
            this.field4766 = v5;
        }
        return v5;
    }

    private BlockPos method1193(BlockPos param1) {
        if (AutoMinecart.I.world == null) {
            return null;
        }
        int[][] v2 = new int[][]{{0, 0}, {1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
        for (int v3 = 0; v3 <= 6; ++v3) {
            int v4 = param1.getY() - v3;
            for (int[] v8 : v2) {
                BlockPos v10;
                BlockPos v9 = new BlockPos(param1.getX() + v8[0], v4, param1.getZ() + v8[1]);
                if (!AutoMinecart.I.world.getBlockState(v9).isReplaceable() || !AutoMinecart.I.world.getBlockState(v10 = v9.down()).isSolidBlock((BlockView)AutoMinecart.I.world, v10)) continue;
                return v9;
            }
        }
        return null;
    }

    @EventHandler(2)
    public void method1194(EventAddEntity param1) {
        Direction v7;
        Vec3d v8;
        Entity v4;
        ArrowEntity v3;
        if (AutoMinecart.I.player == null || AutoMinecart.I.world == null) {
            return;
        }
        if (this.field4746 != null) {
            return;
        }
        Entity v2 = param1.method2497();
        if (v2 instanceof ArrowEntity) {
            v3 = (ArrowEntity)v2;
            v4 = v3.getOwner();
            if (v4 != null && v4 != AutoMinecart.I.player) {
                return;
            }
        } else {
            return;
        }
        if (v4 == null && v3.squaredDistanceTo((Entity)AutoMinecart.I.player) > field4772) {
            return;
        }
        if (v3.getId() == this.field4762) {
            return;
        }
        if (v3.getVelocity().lengthSquared() < field4773) {
            return;
        }
        BlockHitResult v5 = this.method1190(v3);
        if (v5 == null || v5.getType() != HitResult.Type.BLOCK) {
            return;
        }
        Vec3d v6 = v5.getPos();
        BlockPos v9 = BlockPos.ofFloored((Position)v6.add(v8 = new Vec3d((double)(v7 = v5.getSide()).getOffsetX(), (double)v7.getOffsetY(), (double)v7.getOffsetZ()).multiply(field4774)));
        BlockPos v10 = this.method1193(v9);
        if (v10 == null) {
            return;
        }
        double v11 = ((Float)this.field4759.getValue()).floatValue();
        double v13 = v11 * v11;
        if (AutoMinecart.I.player.getCameraPosVec(I.getRenderTickCounter().getTickDelta(true)).squaredDistanceTo(v10.toCenterPos()) > v13) {
            return;
        }
        int v15 = this.method1198(Items.RAIL);
        int v16 = this.method1198(Items.TNT_MINECART);
        if (v15 == -1 && ((Boolean)this.field4761.getValue()).booleanValue()) {
            v15 = this.method1192(Items.RAIL, v16, true);
        }
        if (v16 == -1 && ((Boolean)this.field4761.getValue()).booleanValue()) {
            v16 = this.method1192(Items.TNT_MINECART, v15, false);
        }
        if (v15 == -1 || v16 == -1) {
            return;
        }
        this.field4746 = v10;
        this.field4767 = v15;
        this.field4770 = v16;
        this.field4748 = true;
        this.field4749 = AutoMinecart.I.player.getInventory().selectedSlot;
        this.field4762 = v3.getId();
    }

    private void method1195(Vec3d param1) {
        Vec3d v2 = AutoMinecart.I.player.getCameraPosVec(I.getRenderTickCounter().getTickDelta(true));
        Vec3d v3 = param1.subtract(v2);
        float v4 = (float)MathHelper.wrapDegrees((double)(Math.toDegrees(Math.atan2(v3.z, v3.x)) - field4775));
        float v5 = (float)(-Math.toDegrees(Math.atan2(v3.y, Math.hypot(v3.x, v3.z))));
        AimRotation.method367(new Rotation(v4, v5), field4776, 5, 12);
        AutoMinecart.I.player.networkHandler.sendPacket((Packet)new PlayerMoveC2SPacket.LookAndOnGround(AutoMinecart.I.player.getYaw(), AutoMinecart.I.player.getPitch(), AutoMinecart.I.player.isOnGround(), AutoMinecart.I.player.horizontalCollision));
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.method1197();
    }

    private void method1196() {
        if (AutoMinecart.I.player == null || AutoMinecart.I.interactionManager == null) {
            return;
        }
        int v1 = AutoMinecart.I.player.currentScreenHandler.syncId;
        if (this.field4763 != -1) {
            AutoMinecart.I.interactionManager.clickSlot(v1, this.field4763, this.field4764, SlotActionType.SWAP, (PlayerEntity)AutoMinecart.I.player);
            this.field4763 = -1;
        }
        if (this.field4765 != -1) {
            AutoMinecart.I.interactionManager.clickSlot(v1, this.field4765, this.field4766, SlotActionType.SWAP, (PlayerEntity)AutoMinecart.I.player);
            this.field4765 = -1;
        }
    }

    private void method1197() {
        this.field4746 = null;
        this.field4747 = false;
        this.field4748 = false;
        this.field4763 = -1;
        this.field4764 = -1;
        this.field4765 = -1;
        this.field4766 = -1;
    }

    private int method1198(Item param1) {
        if (AutoMinecart.I.player == null) {
            return -1;
        }
        for (int v2 = 0; v2 < 9; ++v2) {
            if (AutoMinecart.I.player.getInventory().getStack(v2).getItem() != param1) continue;
            return v2;
        }
        return -1;
    }

    static {
        field4753 = "Auto Minecart";
        field4754 = "Ставит рельсу и TNT-вагонетку туда, куда прилетит стрела";
        field4755 = "Дистанция";
        field4756 = 4.5f;
        field4757 = 6.0f;
        field4758 = 0.1f;
        field4760 = "Перекладывать с инвентаря";
        field4772 = 36.0;
        field4773 = 0.01;
        field4774 = 0.02;
        field4768 = 0.5;
        field4769 = 0.5;
        field4771 = 0.5;
        field4775 = 90.0;
        field4776 = 180.0f;
        field4750 = 0.6f;
        field4751 = 0.99f;
        field4752 = 0.05;
    }
}



