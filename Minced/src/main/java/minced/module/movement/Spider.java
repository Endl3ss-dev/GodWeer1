/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket$LookAndOnGround
 *  net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket
 *  net.minecraft.util.Hand
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.hit.HitResult$Type
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.RaycastContext
 *  net.minecraft.world.RaycastContext$FluidHandling
 *  net.minecraft.world.RaycastContext$ShapeType
 */
package minced.module.movement;
import minced.core.EventHandler;

import minced.core.ModeSetting;
import minced.core.EventUpdate;
import minced.core.Module;
import minced.enums.Combat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.RaycastContext;

public class Spider
extends Module {
    static MinecraftClient I = MinecraftClient.getInstance();
    long field4664 = 0L;
    static long field4665;
    static double field4666;
    int field4667 = -1;
    int field4668 = -1;
    static double field4669;
    static String field4670;
    static String field4671;
    static String field4672;
    static String field4673 = "Вода";
    static String field4674;
    static String field4675;
    ModeSetting field4676 = new ModeSetting(field4672, field4673, field4674, field4675);
    static String field4677;
    static String field4678;

    private void method1176() {
        BlockPos v10;
        Vec3d v7;
        Vec3d v8;
        long v1;
        if (!Spider.I.player.horizontalCollision) {
            return;
        }
        if (Spider.I.player.isOnGround()) {
            Spider.I.player.jump();
        }
        if ((v1 = System.currentTimeMillis()) - this.field4664 < field4665) {
            return;
        }
        ItemStack v3 = Spider.I.player.getOffHandStack();
        if (!v3.isOf(Items.PLAYER_HEAD)) {
            return;
        }
        float v4 = Spider.I.player.getYaw();
        float v5 = Spider.I.player.getPitch();
        Vec3d v6 = Spider.I.player.getEyePos();
        BlockHitResult v9 = Spider.I.world.raycast(new RaycastContext(v6, v8 = v6.add((v7 = this.method1178(v5, v4)).multiply(field4666)), RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, (Entity)Spider.I.player));
        if (v9.getType() == HitResult.Type.BLOCK && Spider.I.world.getBlockState(v10 = v9.getBlockPos()).isSolidBlock((BlockView)Spider.I.world, v10)) {
            Spider.I.player.networkHandler.sendPacket((Packet)new PlayerMoveC2SPacket.LookAndOnGround(v4, v5, Spider.I.player.isOnGround(), Spider.I.player.horizontalCollision));
            Spider.I.player.swingHand(Hand.OFF_HAND);
            Spider.I.interactionManager.interactBlock(Spider.I.player, Hand.OFF_HAND, v9);
            Spider.I.player.fallDistance = 0.0f;
            this.field4664 = v1;
        }
    }

    private int method1177() {
        for (int v1 = 0; v1 < 9; ++v1) {
            ItemStack v2 = Spider.I.player.getInventory().getStack(v1);
            if (v2.isEmpty() || !v2.isOf(Items.WATER_BUCKET)) continue;
            return v1;
        }
        return -1;
    }

    @Override
    public void onDisable() {
        if (I != null && Spider.I.player != null && this.field4667 != -1 && (this.field4668 == -1 || Spider.I.player.getInventory().selectedSlot == this.field4668)) {
            Spider.I.player.getInventory().selectedSlot = this.field4667;
            Spider.I.player.networkHandler.sendPacket((Packet)new UpdateSelectedSlotC2SPacket(this.field4667));
        }
        this.field4667 = -1;
        this.field4668 = -1;
        this.field4664 = 0L;
        super.onDisable();
    }

    private Vec3d method1178(float param1, float param2) {
        float v3 = (float)Math.toRadians(param1);
        float v4 = (float)Math.toRadians(-param2);
        float v5 = (float)Math.cos(v4);
        float v6 = (float)Math.sin(v4);
        float v7 = (float)Math.cos(v3);
        float v8 = (float)Math.sin(v3);
        return new Vec3d((double)(v6 * v7), (double)(-v8), (double)(v5 * v7));
    }

    private void method1179() {
        if (!Spider.I.player.horizontalCollision) {
            return;
        }
        int v1 = this.method1177();
        if (v1 == -1) {
            return;
        }
        if (!Spider.I.player.getMainHandStack().isOf(Items.WATER_BUCKET)) {
            if (this.field4667 == -1) {
                this.field4667 = Spider.I.player.getInventory().selectedSlot;
            }
            this.field4668 = v1;
            Spider.I.player.getInventory().selectedSlot = v1;
            Spider.I.player.networkHandler.sendPacket((Packet)new UpdateSelectedSlotC2SPacket(v1));
        }
        if (!Spider.I.player.horizontalCollision) {
            return;
        }
        Spider.I.interactionManager.interactItem((PlayerEntity)Spider.I.player, Hand.MAIN_HAND);
        Spider.I.player.setVelocity(Spider.I.player.getVelocity().x, field4669, Spider.I.player.getVelocity().z);
    }

    public Spider() {
        super(field4670, field4671, Combat.Movement);
        this.addSettings(this.field4676);
    }

    @EventHandler(2)
    public void method1180(EventUpdate param1) {
        if (I == null || Spider.I.player == null || Spider.I.interactionManager == null) {
            return;
        }
        String v2 = (String)this.field4676.getValue();
        int v3 = -1;
        switch (v2.hashCode()) {
            case 32120296: {
                if (!v2.equals(field4677)) break;
                v3 = 0;
                break;
            }
            case 1056824: {
                if (!v2.equals(field4678)) break;
                v3 = 1;
            }
        }
        switch (v3) {
            case 0: {
                this.method1179();
                break;
            }
            case 1: {
                this.method1176();
            }
        }
    }

    static {
        field4670 = "Spider";
        field4671 = "Позволяет различными способами взбираться на стены";
        field4672 = "Режим";
        field4674 = "Вода";
        field4675 = "Шар";
        field4677 = "Вода";
        field4678 = "Шар";
        field4669 = 0.36;
        field4665 = 150L;
        field4666 = 4.0;
    }
}



