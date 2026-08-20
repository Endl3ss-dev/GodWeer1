/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Blocks
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.decoration.EndCrystalEntity
 *  net.minecraft.entity.effect.StatusEffects
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket$Full
 *  net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket$PositionAndOnGround
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.MathHelper
 */
package minced.module.combat;
import minced.core.EventHandler;

import java.util.concurrent.ThreadLocalRandom;
import minced.core.ModeSetting;
import minced.core.EventAttack;
import minced.core.Minced;
import minced.core.Module;
import minced.enums.Combat;
import minced.module.combat.AttackAura;
import minced.module.combat.TriggerBot;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;

public class PacketCriticals
extends Module
{
    ModeSetting field160 = new ModeSetting(field163, field164, field165, field166, field167, field168);
    static String field161;
    static String field162;
    static String field163;
    static String field164;
    static String field165;
    static String field166;
    static String field167;
    static String field168;
    static MinecraftClient I = MinecraftClient.getInstance();
    static String field169;
    static double field170;
    static String field171;
    static double field172;
    static String field173;
    static boolean field174;
    static float field175;
    static float field176;

    public ModeSetting method103() {
        return this.field160;
    }

    public PacketCriticals() {
        super(field161, field162, Combat.Combat);
        this.addSettings(this.field160);
    }

    @EventHandler(2)
    private void method104(EventAttack param1) {
        if (PacketCriticals.I.player == null || I.getNetworkHandler() == null) {
            return;
        }
        if (this.field160.isMode(field169)) {
            I.getNetworkHandler().sendPacket((Packet)new PlayerMoveC2SPacket.PositionAndOnGround(PacketCriticals.I.player.getX(), PacketCriticals.I.player.getY() - field170, PacketCriticals.I.player.getZ(), false, false));
            return;
        }
        if (this.field160.isMode(field171)) {
            if (this.method106() || PacketCriticals.I.player.hasStatusEffect(StatusEffects.LEVITATION)) {
                I.getNetworkHandler().sendPacket((Packet)new PlayerMoveC2SPacket.PositionAndOnGround(PacketCriticals.I.player.getX(), PacketCriticals.I.player.getY() - field172, PacketCriticals.I.player.getZ(), false, false));
            }
            return;
        }
        if (this.field160.isMode(field173) && !field174) {
            Entity v2 = param1.method2471();
            if (v2 == null || v2 instanceof EndCrystalEntity) {
                return;
            }
            this.method105();
        }
    }

    private void method105() {
        float v10;
        boolean v5;
        AttackAura v1 = Minced.getInstance().getModuleManager().method177();
        TriggerBot v2 = (TriggerBot)Minced.getInstance().getModuleManager().method187(TriggerBot.class);
        LivingEntity v3 = v1 != null && v1.isEnabled() ? AttackAura.method861() : null;
        LivingEntity v4 = v2 != null && v2.isEnabled() ? TriggerBot.field3635 : null;
        boolean bl = v5 = v3 != null || v4 != null;
        if (!v5) {
            return;
        }
        if (PacketCriticals.I.player.isOnGround()) {
            return;
        }
        double v6 = PacketCriticals.I.player.getY();
        if (v6 == (double)((int)v6)) {
            return;
        }
        boolean v8 = this.method106();
        boolean v9 = PacketCriticals.I.player.isInLava();
        if (!v8 && !v9) {
            return;
        }
        PacketCriticals.I.player.fallDistance = v10 = ThreadLocalRandom.current().nextFloat() * field175 + field176;
        I.getNetworkHandler().sendPacket((Packet)new PlayerMoveC2SPacket.Full(PacketCriticals.I.player.getX(), PacketCriticals.I.player.getY() - (double)v10, PacketCriticals.I.player.getZ(), PacketCriticals.I.player.getYaw(), PacketCriticals.I.player.getPitch(), false, false));
    }

    private boolean method106() {
        if (PacketCriticals.I.player == null || PacketCriticals.I.world == null) {
            return false;
        }
        Box v1 = PacketCriticals.I.player.getBoundingBox();
        for (BlockPos v3 : BlockPos.iterate((int)MathHelper.floor((double)v1.minX), (int)MathHelper.floor((double)v1.minY), (int)MathHelper.floor((double)v1.minZ), (int)MathHelper.floor((double)v1.maxX), (int)MathHelper.floor((double)v1.maxY), (int)MathHelper.floor((double)v1.maxZ))) {
            if (!PacketCriticals.I.world.getBlockState(v3).isOf(Blocks.COBWEB)) continue;
            return true;
        }
        return false;
    }

    public boolean method107() {
        return this.isEnabled() && I != null && PacketCriticals.I.player != null && (PacketCriticals.I.player.hasStatusEffect(StatusEffects.SLOW_FALLING) || this.method106());
    }

    static {
        field174 = false;
        field161 = "Packet Criticals";
        field162 = "Наносит критические удары без надобности прыгать";
        field163 = "Режим";
        field164 = "Post";
        field165 = "Post";
        field166 = "Old Holyworld";
        field167 = "KrystalMC";
        field168 = "Grim";
        field169 = "Old Holyworld";
        field170 = 1.0E-9;
        field171 = "KrystalMC";
        field172 = 1.0E-9;
        field173 = "Grim";
        field175 = 9.0E-7f;
        field176 = 1.0E-7f;
    }
}



