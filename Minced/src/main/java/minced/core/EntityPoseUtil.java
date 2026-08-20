/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityPose
 *  net.minecraft.entity.effect.StatusEffects
 *  net.minecraft.entity.passive.AbstractHorseEntity
 */
package minced.core;

import minced.core.AuraModeHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.AbstractHorseEntity;

public class EntityPoseUtil
implements AuraModeHandler {
    static MinecraftClient I = MinecraftClient.getInstance();
    static double field11159;

    public boolean method3146() {
        if (EntityPoseUtil.I.player == null || EntityPoseUtil.I.world == null) {
            return false;
        }
        if (EntityPoseUtil.I.player.isInLava() || EntityPoseUtil.I.player.hasVehicle() && EntityPoseUtil.I.player.getVehicle() instanceof AbstractHorseEntity || EntityPoseUtil.I.player.isClimbing() || EntityPoseUtil.I.player.hasStatusEffect(StatusEffects.BLINDNESS) || EntityPoseUtil.I.player.hasStatusEffect(StatusEffects.SLOW_FALLING) || EntityPoseUtil.I.player.getAbilities().flying) {
            return false;
        }
        if (EntityPoseUtil.I.player.isOnGround() && EntityPoseUtil.I.player.getPose() == EntityPose.GLIDING) {
            return false;
        }
        if (EntityPoseUtil.I.player.isOnGround() && !EntityPoseUtil.I.world.isSpaceEmpty((Entity)EntityPoseUtil.I.player, EntityPoseUtil.I.player.getBoundingBox().expand(0.0, field11159, 0.0))) {
            return false;
        }
        return !EntityPoseUtil.I.player.isSubmergedInWater() && (!EntityPoseUtil.I.player.isTouchingWater() || !EntityPoseUtil.I.player.isOnGround());
    }

    static {
        field11159 = 0.1;
    }
}



