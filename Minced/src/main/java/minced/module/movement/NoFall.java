/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.Items
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket$Full
 *  net.minecraft.util.Hand
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.hit.HitResult$Type
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.RaycastContext
 *  net.minecraft.world.RaycastContext$FluidHandling
 *  net.minecraft.world.RaycastContext$ShapeType
 *  sg.ec.Чы$Ч
 */
package minced.module.movement;
import minced.core.EventHandler;

import minced.core.EventInput;
import minced.core.ModeSetting;
import minced.core.EventVelocity;
import minced.core.EventUpdate;
import minced.core.EventMotion;
import minced.core.InventoryUtil;
import minced.core.Module;
import minced.enums.Combat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import sg.ec.chy;

public class NoFall
extends Module {
    public static double FALL = 1.0;
    ModeSetting O = new ModeSetting(field4577, field4578, field4579, field4580);
    static String field4573;
    static MinecraftClient I = MinecraftClient.getInstance();
    boolean field4574;
    static String field4575;
    static String field4576;
    static String field4577;
    static String field4578;
    static String field4579;
    static String field4580;
    static String field4581;
    static float field4582;
    boolean field4583;
    static String field4584;
    static String field4585;
    static float field4586;
    static double field4587;
    static double cfr_renamed_391;
    static String field4588;

    @EventHandler(2)
    public void method1146(EventInput param1) {
        if (!this.O.isMode(field4573)) {
            return;
        }
        if (NoFall.I.player == null || !this.field4574) {
            return;
        }
        param1.method1974(true);
        this.field4574 = false;
    }

    public NoFall() {
        super(field4575, field4576, Combat.Movement);
        this.addSettings(this.O);
    }

    @EventHandler(2)
    public void O(EventUpdate param1) {
        if (NoFall.I.player == null || NoFall.I.world == null) {
            return;
        }
        if (this.O.isMode(field4581)) {
            this.field4583 = !NoFall.I.player.isOnGround() && NoFall.I.player.fallDistance > field4582;
        }
    }

    @EventHandler(2)
    public void method1147(EventMotion param1) {
        if (NoFall.I.player == null) {
            return;
        }
        if (this.O.isMode(field4584)) {
            if (!this.field4583 || !NoFall.I.player.isOnGround()) {
                return;
            }
            param1.method2298(true);
            NoFall.I.player.networkHandler.sendPacket((Packet)new PlayerMoveC2SPacket.Full(NoFall.I.player.getX(), NoFall.I.player.getY(), NoFall.I.player.getZ(), NoFall.I.player.getYaw(), NoFall.I.player.getPitch(), NoFall.I.player.isSneaking(), true));
            this.field4583 = false;
            return;
        }
        if (this.O.isMode(field4585)) {
            if (NoFall.I.player.fallDistance <= field4586) {
                return;
            }
            Vec3d v2 = NoFall.I.player.getPos();
            Vec3d v3 = v2.add(0.0, field4587, 0.0);
            BlockHitResult v4 = NoFall.I.world.raycast(new RaycastContext(v2, v3, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, (Entity)NoFall.I.player));
            int v5 = InventoryUtil.method3336(Items.WATER_BUCKET);
            if (v4 != null && v4.getType() == HitResult.Type.BLOCK && v5 >= 0) {
                int v6 = NoFall.I.player.getInventory().selectedSlot;
                Vec3d v7 = v4.getPos();
                Vec3d v8 = NoFall.I.player.getEyePos();
                double v9 = v7.x - v8.x;
                double v11 = v7.y - v8.y;
                double v13 = v7.z - v8.z;
                double v15 = Math.sqrt(v9 * v9 + v13 * v13);
                float v17 = (float)(-Math.toDegrees(Math.atan2(v11, v15)));
                float v18 = (float)MathHelper.wrapDegrees((double)(Math.toDegrees(Math.atan2(v13, v9)) - cfr_renamed_391));
                param1.method2506(v18);
                param1.method2512(v17);
                InventoryUtil.method3313(v5);
                NoFall.I.interactionManager.interactItem((PlayerEntity)NoFall.I.player, Hand.MAIN_HAND);
                InventoryUtil.method3313(v6);
            }
        }
    }

    @EventHandler(2)
    public void method1148(EventVelocity param1) {
        if (!this.O.isMode(field4588)) {
            return;
        }
        if (param1.method2393() == chy.ch.FALL) {
            this.field4574 = true;
        }
    }

    static {
        field4575 = "No Fall";
        field4576 = "Предотвращает получение урона при падении";
        field4577 = "Мод";
        field4578 = "GrimJump";
        field4579 = "GrimJump";
        field4580 = "MLG";
        field4581 = "GrimJump";
        field4582 = 2.5f;
        field4584 = "GrimJump";
        field4585 = "MLG";
        field4586 = 5.0f;
        field4587 = -15.0;
        cfr_renamed_391 = 90.0;
        field4588 = "GrimJump";
        field4573 = "GrimJump";
    }
}



