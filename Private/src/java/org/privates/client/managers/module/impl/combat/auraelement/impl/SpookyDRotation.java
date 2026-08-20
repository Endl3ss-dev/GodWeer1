package org.privates.client.managers.module.impl.combat.auraelement.impl;


import lombok.experimental.UtilityClass;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import org.privates.client.api.interfaces.IMinecraft;
import org.privates.client.managers.component.impl.rotation.Rotation;
import org.privates.client.managers.component.impl.rotation.RotationComponent;
import org.privates.client.managers.module.impl.combat.AuraNew;
import org.privates.client.utils.math.Mathf;

import java.util.concurrent.ThreadLocalRandom;


@UtilityClass
public class SpookyDRotation implements IMinecraft {

    float tick = 0;
    public static final float MAX_PITCH = 90.0F;
    public static final float MIN_PITCH = -90.0F;
    private static final float COOLDOWN_THRESHOLD = 0.93F;
    public static final float CRIT_COOLDOWN_THRESHOLD = 0.5F;


    public void rotation(LivingEntity target, boolean isAttack, float attackDistance, boolean check) {
        Vector3d playerEyePos = mc.player.getEyePosition(mc.getRenderPartialTicks());

        float oscillY = (float) Math.cos(System.currentTimeMillis() / 240L);
        float offsetY = 0.046F * oscillY;

        float oscillZ = (float) Math.cos(System.currentTimeMillis() / 150L);
        float offsetZ = 0.06F * oscillZ;

        Vector3d eyePos = mc.player.getEyePosition(mc.getRenderPartialTicks());
        float timeVar = (float) Math.cos(System.currentTimeMillis() / 900L);
        float addyVact = 0.15F * timeVar;
        float timeVarZ = (float) Math.cos(System.currentTimeMillis() / 1100L);
        float addyVacZ = 0.15F * timeVarZ;
        float timeVarX = (float) Math.cos(System.currentTimeMillis() / 800L);
        float addyVacX = 0.25F * timeVarX;
        Vector3d directionVec = target.getPositionVec()
                .add(addyVacZ, MathHelper.clamp(eyePos.y - target.getPosY(), 0.0F, 1F + addyVact), addyVacX)
                .subtract(eyePos).normalize();


        boolean attack = false;
        if (isAttack) tick = 4;

        if (tick > 0) {
            attack = true;
            tick--;
        }


        float baseYaw = (float) Math.toDegrees(Math.atan2(-directionVec.x, directionVec.z));
        float basePitch = (float) MathHelper.clamp(
                -Math.toDegrees(Math.atan2(directionVec.y, Math.hypot(directionVec.x, directionVec.z))),
                MIN_PITCH, MAX_PITCH
        );

        float waveA = (float) Math.cos(System.currentTimeMillis() / 70D);
        float waveB = (float) Math.sin(System.currentTimeMillis() / 80D);


        float randomAttackShift = 0;
        if (attack) {
            randomAttackShift = ThreadLocalRandom.current().nextLong(-3, 3);

        }

        float yawJitter = ((float) (Math.random() * 2) * waveB) + waveA * (float) (Math.random() * 3);
        float pitchJitter = ((float) (Math.random() * 1) * waveA) + waveB * Mathf.smoothRandom(1, 3, 1);

        boolean attackCooldownReady = AuraNew.getInstance().cooldownFromLastSwing() > CRIT_COOLDOWN_THRESHOLD;
        float pitchChangeSpeed = Mathf.randomValue(3, 5);

        float yawChangeSpeed = Mathf.randomValue(35, 45);

        Rotation newRotation = new Rotation(baseYaw + yawJitter + randomAttackShift, basePitch + pitchJitter + randomAttackShift);

        RotationComponent.update(newRotation, yawChangeSpeed, pitchChangeSpeed, Mathf.randomValue(11, 16), Mathf.randomValue(5, 7), 0, 15, false);

    }


}
