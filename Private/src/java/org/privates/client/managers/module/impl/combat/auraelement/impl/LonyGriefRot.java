package org.privates.client.managers.module.impl.combat.auraelement.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import org.privates.client.managers.component.impl.rotation.Rotation;
import org.privates.client.managers.module.impl.combat.AuraNew;
import org.privates.client.managers.module.impl.combat.auraelement.AbstractRotationElement;
import org.privates.client.utils.other.GCDUtils;

import java.util.concurrent.ThreadLocalRandom;


public class LonyGriefRot extends AbstractRotationElement {

    private Vector3d currentAimPoint = null;
    private long sessionStart = 0;

    private float spiralPhase = 0f;
    private float spiralRadiusPhase = (float) Math.PI;

    private float jitterPhase = 0f;
    private float jitterRadius = 0f;
    private float jitterSpeed = 0f;

    private boolean isDeflecting = false;
    private float deflectionProgress = 0f;
    private Vector3d deflectionTargetPoint = null;
    private float lastCooldown = 1.0f;

    private float baseSpiralSpeed;
    private float baseRadius;
    private float verticalOscillationSpeed;
    private float verticalOscillationRange;
    private float smoothness;

    @Override

    public Vector2f updateRotation(AuraNew parentModule, LivingEntity target, Minecraft mc, Vector2f currentLerpRotation) {
        if (target == null || mc.player == null) return currentLerpRotation;

        long currentTime = System.currentTimeMillis();

        if (sessionStart == 0) {
            sessionStart = currentTime;
            randomizeParameters();
        }


        float currentCooldown = mc.player.getCooledAttackStrength(0.0f);
        if (currentCooldown < 0.1f && lastCooldown > 0.9f) {
            triggerDeflection(mc, target, currentLerpRotation);
        }
        lastCooldown = currentCooldown;

        spiralPhase += baseSpiralSpeed;
        spiralRadiusPhase += baseSpiralSpeed * 3.63f;
        jitterPhase += jitterSpeed;

        float dynamicRadius = baseRadius * (0.6f + (float) (Math.sin(spiralRadiusPhase) * 0.4f));
        float verticalOffset = (float) Math.sin(currentTime * verticalOscillationSpeed) * verticalOscillationRange;
        Vector3d targetBasePosition = target.getPositionVec().add(0, target.getHeight() * (0.45 + verticalOffset), 0);

        Vector3d right = Vector3d.fromPitchYaw(0, currentLerpRotation.x - 90);
        Vector3d up = Vector3d.fromPitchYaw(currentLerpRotation.y - 90, currentLerpRotation.x);

        Vector3d spiralOffset = right.scale(Math.cos(spiralPhase) * dynamicRadius)
                .add(up.scale(Math.sin(spiralPhase) * dynamicRadius));

        float jitterRadiusMultiplier = jitterRadius * target.getWidth();
        Vector3d jitterOffset = right.scale(Math.cos(jitterPhase) * jitterRadiusMultiplier)
                .add(up.scale(Math.sin(jitterPhase) * jitterRadiusMultiplier));

        Vector3d finalAimTarget = targetBasePosition.add(spiralOffset).add(jitterOffset);

        if (isDeflecting && deflectionProgress < 1.0f) {
            deflectionProgress = Math.min(1.0f, deflectionProgress + 0.08f);

            float smoothProgress = (float) (1.0f - Math.pow(1.0f - deflectionProgress, 3));

            finalAimTarget = finalAimTarget.lerp(deflectionTargetPoint, smoothProgress);

            if (deflectionProgress >= 1.0f) {
                isDeflecting = false;
                deflectionProgress = 0f;
                deflectionTargetPoint = null;
            }
        }

        if (currentAimPoint == null) {
            currentAimPoint = finalAimTarget;
        } else {
            float lerpFactor = isDeflecting ? 0.35f : 0.18f;
            currentAimPoint = currentAimPoint.lerp(finalAimTarget, lerpFactor);
        }
        Vector3d playerEyes = mc.player.getEyePosition(1.0f);
        Vector3d direction = currentAimPoint.subtract(playerEyes);

        float targetYaw = (float) Math.toDegrees(Math.atan2(direction.z, direction.x)) - 90.0f;
        float targetPitch = (float) -Math.toDegrees(Math.atan2(direction.y, Math.sqrt(direction.x * direction.x + direction.z * direction.z)));

        float yawDiff = MathHelper.wrapDegrees(targetYaw - currentLerpRotation.x);
        float pitchDiff = targetPitch - currentLerpRotation.y;

        float smoothFactor = isDeflecting ? 0.5f : (1.3f - smoothness);
        float newYaw = currentLerpRotation.x + yawDiff * smoothFactor;
        float newPitch = currentLerpRotation.y + pitchDiff * smoothFactor;

        newYaw = GCDUtils.getSensitivity(newYaw);
        newPitch = GCDUtils.getSensitivity(MathHelper.clamp(newPitch, -89.9f, 89.9f));

        Vector2f result = new Vector2f(newYaw, newPitch);
        applyRotation(new Rotation(newYaw, newPitch), 75, 34, 0, 4);
        parentModule.setLerpRotation(result);
        return result;
    }

    private void randomizeParameters() {
        baseSpiralSpeed = randomizeValue(0.04f, 0.08f);
        baseRadius = randomizeValue(0.2f, 0.45f);
        verticalOscillationSpeed = randomizeValue(0.001f, 0.0015f);
        verticalOscillationRange = randomizeValue(0.15f, 0.25f);
        smoothness = randomizeValue(0.85f, 0.95f);
        jitterRadius = randomizeValue(0.45f, 0.60f);
        jitterSpeed = randomizeValue(0.18f, 0.28f);
    }

    private void triggerDeflection(Minecraft mc, LivingEntity target, Vector2f currentRotation) {
        isDeflecting = true;
        deflectionProgress = 0f;

        float deflectionAngle = randomizeValue(65f, 90f);
        float direction = ThreadLocalRandom.current().nextBoolean() ? 1f : -1f;
        float finalYaw = currentRotation.x + (deflectionAngle * direction);

        float finalPitch = currentRotation.y;
        if (ThreadLocalRandom.current().nextFloat() < 0.25f) {
            float verticalAngle = randomizeValue(1f, 20f);
            float verticalDirection = ThreadLocalRandom.current().nextBoolean() ? 1f : -1f;
            finalPitch += verticalAngle * verticalDirection;
        }

        finalPitch = MathHelper.clamp(finalPitch, -89.9f, 89.9f);

        float distanceToTarget = mc.player.getDistance(target);
        Vector3d playerEyes = mc.player.getEyePosition(1f);

        Vector3d newDirectionVector = Vector3d.fromPitchYaw(finalPitch, finalYaw);

        this.deflectionTargetPoint = playerEyes.add(newDirectionVector.scale(distanceToTarget));
    }


    private float randomizeValue(float min, float max) {
        return min + ThreadLocalRandom.current().nextFloat() * (max - min);
    }

    @Override
    public void onEnable(AuraNew parentModule) {
        currentAimPoint = null;
        sessionStart = 0;
        spiralPhase = 0f;
        spiralRadiusPhase = (float) Math.PI;
        jitterPhase = 0f;
        isDeflecting = false;
        deflectionProgress = 0f;
        deflectionTargetPoint = null;
        lastCooldown = 1.0f;
        randomizeParameters();
    }

    @Override
    public void onDisable(AuraNew parentModule) {
        currentAimPoint = null;
        sessionStart = 0;
        isDeflecting = false;
        deflectionProgress = 0f;
        deflectionTargetPoint = null;
    }
}