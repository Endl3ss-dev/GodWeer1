/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.PlayerEntity
 */
package minced.module.combat;
import minced.core.EventHandler;

import minced.core.EventAttack;
import minced.core.Minced;
import minced.core.Module;
import minced.enums.Combat;
import minced.module.combat.AttackAura;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

public class NoFriendDamage
extends Module {
    static String field4488 = "No Friend Damage";
    static String field4489 = "Отключает возможность наносить урон друзьям";

    public NoFriendDamage() {
        super(field4488, field4489, Combat.Combat);
    }

    @EventHandler(2)
    public void method1131(EventAttack param1) {
        AttackAura v2 = (AttackAura)Minced.getInstance().getModuleManager().method187(AttackAura.class);
        Entity v4 = param1.method2471();
        if (v4 instanceof PlayerEntity) {
            PlayerEntity v3 = (PlayerEntity)v4;
            if (Minced.getInstance().getFriendManager().cfr_renamed_4(v3.getGameProfile().getName()) && (!v2.isEnabled() || AttackAura.method861() != param1.method2471())) {
                param1.method2298(true);
            }
        }
    }
}



