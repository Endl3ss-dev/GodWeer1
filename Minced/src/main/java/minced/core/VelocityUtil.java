/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.entity.effect.StatusEffects
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket
 *  net.minecraft.network.packet.s2c.play.ExplosionS2CPacket
 *  net.minecraft.world.World
 *  sg.ec.Чи$Ч
 */
package minced.core;

import minced.core.EventVelocity;
import minced.core.EventPacket;
import minced.core.ClientState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.world.World;
import sg.ec.chi2;

public class VelocityUtil {
    static MinecraftClient I = MinecraftClient.getInstance();
    boolean field12543;
    boolean field12544;
    boolean field12545;
    boolean field12546;
    boolean field12547;
    boolean field12548;
    ClientState field12549 = new ClientState();

    @EventHandler(2)
    public void method3571(EventPacket param1) {
        if (VelocityUtil.I.player != null && VelocityUtil.I.world != null) {
            boolean v2;
            boolean bl = v2 = this.field12543 || this.field12544 || this.field12545 || this.field12546 || this.field12547;
            if (!this.method3572()) {
                if (param1.method2467() instanceof ExplosionS2CPacket) {
                    this.field12545 = true;
                }
                if (!v2) {
                    EntityStatusS2CPacket v4;
                    Packet v3 = param1.method2467();
                    if (v3 instanceof EntityStatusS2CPacket && (v4 = (EntityStatusS2CPacket)v3).getStatus() == 2 && v4.getEntity((World)VelocityUtil.I.world) == VelocityUtil.I.player) {
                        this.field12548 = true;
                    }
                } else if (VelocityUtil.I.player.hurtTime > 0) {
                    this.field12548 = false;
                    this.method3579();
                }
            }
        }
    }

    private boolean method3572() {
        if (VelocityUtil.I.player == null) {
            return false;
        }
        return VelocityUtil.I.player.hasStatusEffect(StatusEffects.POISON) || VelocityUtil.I.player.hasStatusEffect(StatusEffects.WITHER) || VelocityUtil.I.player.hasStatusEffect(StatusEffects.INSTANT_DAMAGE);
    }

    public boolean method3573() {
        return this.field12543;
    }

    public boolean method3574() {
        return this.field12548;
    }

    @EventHandler(2)
    public void method3575(EventVelocity param1) {
        switch (chi2.ch.n4[param1.method2393().ordinal()]) {
            case 1: {
                this.field12543 = true;
                break;
            }
            case 2: {
                this.field12544 = true;
                break;
            }
            case 3: {
                this.field12546 = true;
                break;
            }
            case 4: {
                this.field12547 = true;
            }
        }
        this.field12548 = false;
    }

    public void method3576() {
        this.field12548 = false;
        this.method3579();
        this.field12549.method3114();
    }

    public boolean method3577() {
        return this.field12544;
    }

    public ClientState method3578() {
        return this.field12549;
    }

    private void method3579() {
        this.field12543 = false;
        this.field12545 = false;
        this.field12544 = false;
        this.field12546 = false;
        this.field12547 = false;
    }

    public boolean method3580() {
        return this.field12547;
    }

    public boolean method3581() {
        return this.field12546;
    }

    public boolean method3582() {
        return this.field12545;
    }

    public boolean method3583(long param1) {
        if (this.field12548) {
            if (this.field12549.method3113(param1)) {
                this.field12548 = false;
                this.field12549.method3114();
                return true;
            }
        } else {
            this.field12549.method3114();
        }
        return false;
    }
}



