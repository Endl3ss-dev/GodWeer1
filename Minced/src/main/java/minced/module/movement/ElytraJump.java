/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EquipmentSlot
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket
 *  net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket$Mode
 *  net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket
 *  net.minecraft.util.math.Vec3d
 */
package minced.module.movement;
import minced.core.EventHandler;

import java.util.function.BooleanSupplier;
import minced.core.BooleanSetting;
import minced.core.EventUpdate;
import minced.core.EventMotion;
import minced.core.InventoryUtil;
import minced.core.Module;
import minced.enums.Combat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.util.math.Vec3d;

public class ElytraJump
extends Module {
    static double cfr_renamed_136;
    static double ь;

    static MinecraftClient I = MinecraftClient.getInstance();
    static float field5548;
    static float field5549;
    BooleanSetting field5550 = new BooleanSetting(field5558, false);
    static double field5551;
    static float field5552;
    static double field5553;
    static double field5554;
    static double field5555;
    static String field5556;
    static String field5557;
    static String field5558;

    private boolean method1296() {
        return ElytraJump.I.player.getEquippedStack(EquipmentSlot.CHEST).getItem() == Items.ELYTRA;
    }

    @EventHandler(2)
    public void method1297(EventMotion param1) {
        if (!this.method1302() || !ElytraJump.I.player.isGliding()) {
            return;
        }
        if (ElytraJump.I.player.getPitch() < field5548 || ElytraJump.I.player.getPitch() > field5549) {
            param1.method2512(0.0f);
            ElytraJump.I.player.setPitch(0.0f);
        }
        ElytraJump.I.player.headYaw = ElytraJump.I.player.getYaw();
        ElytraJump.I.player.prevHeadYaw = ElytraJump.I.player.getYaw();
        ElytraJump.I.player.bodyYaw = ElytraJump.I.player.getYaw();
        ElytraJump.I.player.prevBodyYaw = ElytraJump.I.player.getYaw();
    }

    private void method1298() {
    }

    @Override
    public void onEnable() {
        if (((Boolean)this.field5550.getValue()).booleanValue() && this.method1302()) {
            this.method1300();
        }
        super.onEnable();
    }

    private void method1299() {
        boolean v3;
        Vec3d v1 = ElytraJump.I.player.getVelocity();
        boolean v2 = v1.y > field5551 || ElytraJump.I.player.fallDistance > field5552;
        boolean bl = v3 = Math.abs(v1.x) <= field5553 && Math.abs(v1.z) <= field5554;
        if (!v2 || !v3) {
            return;
        }
        double v4 = v1.y + field5555;
        ElytraJump.I.player.setVelocity(0.0, v4, 0.0);
    }

    private void method1300() {
        int v1 = InventoryUtil.method3330(Items.ELYTRA, 0, 35);
        if (v1 != -1) {
            this.method1304(v1);
        }
    }

    @EventHandler(2)
    public void method1301(EventUpdate param1) {
        if (!this.method1302()) {
            return;
        }
        if (!ElytraJump.I.player.getAbilities().flying && ElytraJump.I.player.isOnGround() && !ElytraJump.I.player.isTouchingWater() && !ElytraJump.I.player.isInLava() && this.method1296() && !ElytraJump.I.options.jumpKey.isPressed()) {
            ElytraJump.I.player.jump();
        }
        if (!(ElytraJump.I.player.getAbilities().flying || ElytraJump.I.player.isOnGround() || ElytraJump.I.player.isTouchingWater() || ElytraJump.I.player.isInLava() || ElytraJump.I.player.isGliding() || !this.method1303())) {
            ElytraJump.I.player.networkHandler.sendPacket((Packet)new ClientCommandC2SPacket((Entity)ElytraJump.I.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
            ElytraJump.I.player.startGliding();
        }
        if (ElytraJump.I.player.isOnGround() || ElytraJump.I.player.isTouchingWater() || ElytraJump.I.player.isInLava()) {
            ElytraJump.I.options.jumpKey.setPressed(false);
        }
        if (ElytraJump.I.player.hurtTime > 0 && ((Boolean)this.field5550.getValue()).booleanValue() && this.method1296()) {
            this.method1298();
            return;
        }
        if (this.method1296()) {
            ElytraJump.I.options.jumpKey.setPressed(true);
            if (ElytraJump.I.player.isGliding()) {
                this.method1299();
            }
        } else if (((Boolean)this.field5550.getValue()).booleanValue()) {
            this.method1300();
        } else {
            ElytraJump.I.options.jumpKey.setPressed(false);
        }
    }

    @Override
    public void onDisable() {
        if (this.method1302()) {
            ElytraJump.I.options.jumpKey.setPressed(false);
            if (((Boolean)this.field5550.getValue()).booleanValue() && this.method1296()) {
                this.method1298();
            }
        }
        super.onDisable();
    }

    private boolean method1302() {
        return ElytraJump.I.player != null && ElytraJump.I.world != null;
    }

    private boolean method1303() {
        if (!this.method1296()) {
            return false;
        }
        ItemStack v1 = ElytraJump.I.player.getEquippedStack(EquipmentSlot.CHEST);
        return !ElytraJump.I.player.isTouchingWater() && !ElytraJump.I.player.isInLava() && !ElytraJump.I.player.hasVehicle() && v1.getItem() == Items.ELYTRA && v1.getDamage() < v1.getMaxDamage() - 1;
    }

    private void method1304(int param1) {
        if (ElytraJump.I.player == null || ElytraJump.I.interactionManager == null) {
            return;
        }
        InventoryUtil.method3317(param1, 6);
        ElytraJump.I.player.networkHandler.sendPacket((Packet)new CloseHandledScreenC2SPacket(ElytraJump.I.player.currentScreenHandler.syncId));
    }

    public ElytraJump() {
        super(field5556, field5557, Combat.Movement);
        this.addSettings(this.field5550);
    }

    static {
        cfr_renamed_136 = 0.0;
        ь = 0.0;
        field5556 = "Elytra Jump";
        field5557 = "Вертикальный полёт на элитре под Grim";
        field5558 = "Авто-свап элитры";
        field5548 = -30.0f;
        field5549 = 30.0f;
        field5551 = 0.08;
        field5552 = 0.1f;
        field5553 = 0.01;
        field5554 = 0.01;
        field5555 = 0.03062;
    }
}



