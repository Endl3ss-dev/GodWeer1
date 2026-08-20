/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.entity.LivingEntity
 */
package minced.module.movement;
import minced.core.EventHandler;

import java.util.function.BooleanSupplier;
import minced.core.EventUpdate;
import minced.core.Minced;
import minced.core.Module;
import minced.enums.Combat;
import minced.module.combat.AttackAura;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;

public class ElytraWhatsapp
extends Module {
    static String field4622 = "Elytra Whatsapp";
    static String field4623 = "Позволяет зависать в воздухе на элитре, упрощая таргетирование цели";
    boolean field4624;
    static MinecraftClient I = MinecraftClient.getInstance();
    static double field4625;
    static double field4626;
    static double field4627;

    public ElytraWhatsapp() {
        super(field4622, field4623, Combat.Movement);
    }

    @EventHandler(2)
    public void method1161(EventUpdate param1) {
        AttackAura v2 = Minced.getInstance().getModuleManager().method177();
        LivingEntity v3 = AttackAura.method861();
        if (v3 == null) {
            if (!this.field4624) {
                ElytraWhatsapp.I.player.setNoGravity(false);
                this.field4624 = true;
            }
        } else {
            boolean v4;
            this.field4624 = false;
            double currentDistance = v2.method732();
            double previousDistance = v2.method783();
            boolean bl = v4 = currentDistance >= field4625 || currentDistance != previousDistance && currentDistance == 0.0;
            if (ElytraWhatsapp.I.player.isGliding() && ElytraWhatsapp.I.player.getEyePos().distanceTo(v3.getBoundingBox().getCenter().add(0.0, (v3.getY() - v3.prevY) * field4626, 0.0)) < field4627 && !v4) {
                ElytraWhatsapp.I.player.setVelocity(0.0, 0.0, 0.0);
                ElytraWhatsapp.I.player.setNoGravity(true);
            } else {
                ElytraWhatsapp.I.player.setNoGravity(false);
            }
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (ElytraWhatsapp.I.player != null) {
            this.field4624 = false;
            ElytraWhatsapp.I.player.setNoGravity(false);
        }
    }

    static {
        field4625 = 20.0;
        field4626 = 2.0;
        field4627 = 2.4;
    }
}



