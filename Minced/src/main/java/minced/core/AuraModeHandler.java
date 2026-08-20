/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.LivingEntity
 */
package minced.core;

import minced.module.combat.AttackAura;
import net.minecraft.entity.LivingEntity;

public interface AuraModeHandler {
    default public boolean isReadyToAttack(AttackAura param1, LivingEntity param2) {
        return false;
    }

    default public void updateRotation(AttackAura var1, LivingEntity var2) {}

    default public void onDeactivate(AttackAura param1) {
    }

    default public String getName() { return ""; }

    default public void onAttack(AttackAura param1, LivingEntity param2) {
    }

    default public void onActivate(AttackAura param1) {
    }

    default public void reset(AttackAura param1) {
    }

    default public void beforeAttack(AttackAura param1, LivingEntity param2) {
    }
}

