package org.privates.client.managers.module.impl.combat.auraelement.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.vector.Vector2f;
import org.privates.client.managers.module.impl.combat.AuraNew;
import org.privates.client.managers.module.impl.combat.auraelement.AbstractRotationElement;

/**
 * Strict adapter for original Expensive SpookyTime logic.
 * No additional smoothing/anti-spike/custom behavior.
 */
public class SpookyTimeRot extends AbstractRotationElement {

    @Override
    public Vector2f updateRotation(AuraNew parentModule, LivingEntity target, Minecraft mc, Vector2f currentLerpRotation) {
        if (target == null || mc.player == null) {
            return currentLerpRotation;
        }

        SpookyTimeRotation.applyRotation(target, false, (float) parentModule.attackDistance(), false);

        Vector2f result = new Vector2f(mc.player.rotationYaw, mc.player.rotationPitch);
        parentModule.setLerpRotation(result);
        return result;
    }
}
