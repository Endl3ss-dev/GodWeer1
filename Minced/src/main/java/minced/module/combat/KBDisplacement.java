/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket
 *  net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket$Mode
 *  net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket$LookAndOnGround
 *  net.minecraft.util.Hand
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 */
package minced.module.combat;
import minced.core.EventHandler;

import minced.core.BooleanSetting;
import minced.core.EventUpdate;
import minced.core.EventAttack;
import minced.core.Module;
import minced.enums.Combat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class KBDisplacement
extends Module {
    static MinecraftClient I = MinecraftClient.getInstance();
    BooleanSetting field1439 = new BooleanSetting(field1444, false);
    boolean field1440;
    boolean field1441;
    static String field1442;
    static String field1443;
    static String field1444;
    Entity field1445;
    int field1446;
    boolean field1447;
    static float field1448;
    static double field1449;
    static float field1450;

    @EventHandler(2)
    public void method498(EventUpdate param1) {
        if (KBDisplacement.I.player == null || KBDisplacement.I.world == null) {
            return;
        }
        if (!((Boolean)this.field1439.getValue()).booleanValue()) {
            this.method504();
            this.method499();
        }
        if (this.field1440) {
            this.field1440 = false;
            if (KBDisplacement.I.player.networkHandler != null) {
                KBDisplacement.I.player.networkHandler.sendPacket((Packet)new PlayerMoveC2SPacket.LookAndOnGround(KBDisplacement.I.player.getYaw(), KBDisplacement.I.player.getPitch(), KBDisplacement.I.player.isOnGround(), KBDisplacement.I.player.horizontalCollision));
            }
        }
        if (this.field1441) {
            this.field1441 = false;
            if (KBDisplacement.I.player.networkHandler != null) {
                KBDisplacement.I.player.networkHandler.sendPacket((Packet)new ClientCommandC2SPacket((Entity)KBDisplacement.I.player, ClientCommandC2SPacket.Mode.START_SPRINTING));
            }
        }
    }

    public KBDisplacement() {
        super(field1442, field1443, Combat.Combat);
        this.addSettings(this.field1439);
    }

    private void method499() {
        if (this.field1445 == null || KBDisplacement.I.interactionManager == null) {
            return;
        }
        if (!this.field1445.isAlive() || !this.method501()) {
            this.field1445 = null;
            this.field1446 = 0;
            return;
        }
        if (this.field1446 > 0) {
            --this.field1446;
            return;
        }
        Entity v1 = this.field1445;
        this.field1445 = null;
        this.field1447 = true;
        KBDisplacement.I.interactionManager.attackEntity((PlayerEntity)KBDisplacement.I.player, v1);
        KBDisplacement.I.player.swingHand(Hand.MAIN_HAND);
        this.field1447 = false;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.field1440 = false;
        this.field1441 = false;
        this.field1445 = null;
        this.field1446 = 0;
        this.field1447 = false;
    }

    private float[] method500(Entity param1) {
        Vec3d v2 = param1.getPos().subtract(KBDisplacement.I.player.getPos());
        float v3 = MathHelper.wrapDegrees((float)((float)Math.toDegrees(Math.atan2(v2.z, v2.x)) - field1448));
        float v4 = KBDisplacement.I.player.getPitch();
        if (Math.random() < field1449) {
            v4 += field1450;
        }
        return new float[]{v3, v4};
    }

    private boolean method501() {
        if (KBDisplacement.I.player == null) {
            return false;
        }
        boolean v1 = KBDisplacement.I.player.input != null && KBDisplacement.I.player.input.movementForward > 0.0f;
        boolean v2 = !KBDisplacement.I.player.horizontalCollision;
        boolean v3 = !KBDisplacement.I.player.isSneaking();
        boolean v4 = !KBDisplacement.I.player.isTouchingWater();
        boolean v5 = !KBDisplacement.I.player.isInLava() && !KBDisplacement.I.player.isSubmergedInWater();
        boolean v6 = KBDisplacement.I.player.getHungerManager().getFoodLevel() > 6 || KBDisplacement.I.player.getAbilities() != null && KBDisplacement.I.player.getAbilities().allowFlying;
        return v1 && v2 && v3 && v4 && v5 && v6;
    }

    private void method502(Entity param1) {
        if (KBDisplacement.I.player == null || KBDisplacement.I.player.networkHandler == null) {
            return;
        }
        float[] v2 = this.method500(param1);
        KBDisplacement.I.player.networkHandler.sendPacket((Packet)new PlayerMoveC2SPacket.LookAndOnGround(v2[0], v2[1], KBDisplacement.I.player.isOnGround(), KBDisplacement.I.player.horizontalCollision));
        this.field1440 = true;
    }

    @EventHandler(2)
    public void method503(EventAttack param1) {
        if (KBDisplacement.I.player == null || KBDisplacement.I.world == null || param1.method2471() == null) {
            return;
        }
        if (param1.method2297()) {
            return;
        }
        Entity v2 = param1.method2471();
        if (((Boolean)this.field1439.getValue()).booleanValue()) {
            if (KBDisplacement.I.player.isSprinting()) {
                KBDisplacement.I.player.networkHandler.sendPacket((Packet)new ClientCommandC2SPacket((Entity)KBDisplacement.I.player, ClientCommandC2SPacket.Mode.STOP_SPRINTING));
            }
            KBDisplacement.I.player.networkHandler.sendPacket((Packet)new ClientCommandC2SPacket((Entity)KBDisplacement.I.player, ClientCommandC2SPacket.Mode.START_SPRINTING));
            if (KBDisplacement.I.player.isSprinting()) {
                this.field1441 = true;
            }
        } else {
            boolean v3 = KBDisplacement.I.player.isSprinting();
            this.method504();
            if (!this.field1447 && !v3 && this.method501()) {
                this.field1445 = v2;
                this.field1446 = 1;
                param1.method2298(true);
                return;
            }
            if (!KBDisplacement.I.player.isSprinting()) {
                return;
            }
        }
        this.method502(v2);
    }

    private void method504() {
        KBDisplacement.I.options.sprintKey.setPressed(true);
        if (!KBDisplacement.I.player.isSprinting() && this.method501()) {
            KBDisplacement.I.player.setSprinting(true);
        }
    }

    static {
        field1442 = "KBDisplacement";
        field1443 = "Manipulates sprint/look around the attack to alter knockback direction";
        field1444 = "Фейк-спринт";
        field1448 = 90.0f;
        field1449 = 0.01;
        field1450 = 0.5f;
    }
}



