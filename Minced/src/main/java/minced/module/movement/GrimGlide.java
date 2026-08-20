/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.util.math.Vec3d
 */
package minced.module.movement;
import minced.core.EventHandler;

import minced.core.EventMotion;
import minced.core.ChatUtil;
import minced.core.EventFireworkBoost;
import minced.core.Minced;
import minced.core.Module;
import minced.enums.Combat;
import minced.module.combat.AttackAura;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;

public class GrimGlide
extends Module {
    static double 衣;
    static long cfr_renamed_385;

    static MinecraftClient I = MinecraftClient.getInstance();
    long field4428;
    static long field4429;
    static double field4430;
    static double field4431;
    static double field4432;
    static double field4433;
    static double field4434;
    static String field4435;
    static String field4436;
    static String field4437;

    @EventHandler(2)
    public void method1114(EventMotion param1) {
        if (GrimGlide.I.player == null || GrimGlide.I.world == null || !GrimGlide.I.player.isGliding()) {
            return;
        }
        if (System.currentTimeMillis() - this.field4428 <= field4429) {
            return;
        }
        if (this.method1115()) {
            return;
        }
        Vec3d v2 = GrimGlide.I.player.getPos();
        float v3 = GrimGlide.I.player.getYaw();
        double v4 = GrimGlide.I.player.age % 2 == 0 ? field4430 : field4431;
        double v6 = -Math.sin(Math.toRadians(v3)) * v4;
        double v8 = Math.cos(Math.toRadians(v3)) * v4;
        GrimGlide.I.player.setVelocity(v6, GrimGlide.I.player.getVelocity().y, v8);
        if (GrimGlide.I.player.age % 2 == 0) {
            GrimGlide.I.player.setPosition(v2.getX() + v6, v2.getY(), v2.getZ() + v8);
        }
    }

    private boolean method1115() {
        Vec3d v5;
        if (GrimGlide.I.player == null || GrimGlide.I.world == null || !GrimGlide.I.player.isGliding()) {
            return false;
        }
        AttackAura v1 = (AttackAura)Minced.getInstance().getModuleManager().method187(AttackAura.class);
        if (v1 == null || !v1.isEnabled()) {
            return false;
        }
        LivingEntity v2 = AttackAura.method861();
        if (v2 == null || !v2.isGliding()) {
            return false;
        }
        Vec3d v3 = v2.getVelocity();
        Vec3d v4 = new Vec3d(v3.x, 0.0, v3.z);
        if (v4.lengthSquared() < field4432) {
            v5 = v2.getRotationVector();
            v4 = new Vec3d(v5.x, 0.0, v5.z);
        }
        if (v4.lengthSquared() < field4433) {
            return false;
        }
        v4 = v4.normalize();
        v5 = new Vec3d(GrimGlide.I.player.getX() - v2.getX(), 0.0, GrimGlide.I.player.getZ() - v2.getZ());
        return v5.dotProduct(v4) > field4434;
    }

    @Override
    public void onDisable() {
        this.field4428 = 0L;
        super.onDisable();
    }

    @EventHandler(2)
    public void method1116(EventFireworkBoost param1) {
        this.field4428 = System.currentTimeMillis();
    }

    public GrimGlide() {
        super(field4435, field4436, Combat.Movement);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        ChatUtil.method3308(field4437, new Object[0]);
    }

    static {
        衣 = 0.0;
        cfr_renamed_385 = 0L;
        field4435 = "Grim Glide";
        field4436 = "Позволяет передвигаться на элитре без фейерверков";
        field4437 = "Если вы будете долго использовать этот модуль на сервере Really World, то вас может кикнуть!";
        field4429 = 150L;
        field4430 = 0.079;
        field4431 = 0.088;
        field4432 = 1.0E-6;
        field4433 = 1.0E-6;
        field4434 = 0.35;
    }
}



