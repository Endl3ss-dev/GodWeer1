package org.privates.client.managers.module.impl.combat.auraelement.impl;

import com.adl.nativeprotect.Native;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import org.privates.client.managers.component.impl.rotation.Rotation;
import org.privates.client.managers.module.impl.combat.AuraNew;
import org.privates.client.managers.module.impl.combat.auraelement.AbstractRotationElement;
import org.privates.client.utils.chat.ChatUtil;
import org.privates.client.utils.other.GCDUtils;
import org.privates.client.utils.other.PerlinNoise;

import java.util.concurrent.ThreadLocalRandom;


public class HumanizedRot extends AbstractRotationElement {

    private enum JitterType {CIRCULAR, CHAOTIC, WAVY, TECHNICAL}

    private enum AimState {TRACKING, POST_HIT_FLICK, UPWARD_FLICK, BIG_FLICK, SPINNING, GLIDING, SNAPPING}

    private enum NexusRotationPhase {APPROACHING, LOCKED, ATTACKING, RECOVERING, IDLE}

    private enum NexusJitterPattern {WAVE, CHAOTIC, VERTICAL}

    private enum RotationStyle {NEXUS}

    private long lastStyleSwitchTime;
    private static final long STYLE_SWITCH_DELAY = 8000L;

    private AimState focus_currentState = AimState.TRACKING;
    private JitterType focus_currentJitterType = JitterType.TECHNICAL;
    private long focus_lastJitterSwitchTime;
    private long focus_stateEndTime;
    private final PerlinNoise focus_chaosNoise = new PerlinNoise(System.currentTimeMillis());
    private final PerlinNoise focus_techNoise1 = new PerlinNoise(System.currentTimeMillis() * 2L);
    private final PerlinNoise focus_techNoise2 = new PerlinNoise(System.currentTimeMillis() * 3L);
    private Vector3d focus_visualAimPoint;
    private Vector3d focus_targetAimPoint;
    private long focus_lastAimPointSwitchTime;
    private float focus_momentumYaw;
    private float focus_momentumPitch;
    private float focus_lastYaw;
    private float focus_lastPitch;
    private Vector2f focus_flickTargetRotation;
    private static final long FOCUS_AIM_POINT_SWITCH_DELAY = 300L;
    private static final long FOCUS_JITTER_SWITCH_DELAY = 4000L;

    private boolean focus_isRecovering = false;
    private long focus_recoveryStartTime;
    private long focus_recoveryDuration;
    private Vector2f focus_recoveryStartRotation;
    private Vector2f focus_recoveryTargetRotation;
    private float focus_recoverySmoothness;
    private float focus_recoverySpeed;

    private long nexus_lastNs = 0L;
    private NexusRotationPhase nexus_currentPhase = NexusRotationPhase.IDLE;
    private NexusJitterPattern nexus_currentJitterPattern = NexusJitterPattern.WAVE;
    private float nexus_lastYawSpeed = 0.0f;
    private float nexus_lastPitchSpeed = 0.0f;
    private float nexus_overshootYaw = 0.0f;
    private float nexus_overshootPitch = 0.0f;
    private boolean nexus_hasOvershot = false;
    private float nexus_momentumYaw = 0.0f;
    private float nexus_momentumPitch = 0.0f;
    private int nexus_hesitationTicks = 0;
    private boolean nexus_isHesitating = false;
    private float nexus_hesitationSlowdown = 1.0f;
    private float nexus_postAttackYawDrift = 0.0f;
    private float nexus_postAttackPitchDrift = 0.0f;
    private int nexus_postAttackTicks = 0;
    private int nexus_postAttackDuration = 0;
    private boolean nexus_inPostAttack = false;
    private float nexus_preAttackTension = 0.0f;
    private int nexus_preAttackTicks = 0;
    private int nexus_attackCounter = 0;
    private float nexus_fatigueMultiplier = 1.0f;
    private int nexus_jitterPatternTicks = 0;
    private int nexus_nextJitterPatternChange = 0;
    private float nexus_jitterWavePhase = 0.0f;
    private float nexus_jitterOffsetX = 0.0f;
    private float nexus_jitterOffsetY = 0.0f;
    private float nexus_jitterTargetX = 0.0f;
    private float nexus_jitterTargetY = 0.0f;
    private float nexus_jitterSmoothness = 0.0f;
    private float nexus_jitterAmplitude = 0.0f;
    private float nexus_jitterFrequency = 0.0f;
    private float nexus_jitterVerticalBias = 0.0f;
    private int nexus_tickCounter = 0;

    private boolean nexus_isRecovering = false;
    private long nexus_recoveryStartTime;
    private long nexus_recoveryDuration;
    private Vector2f nexus_recoveryStartRotation;
    private Vector2f nexus_recoveryTargetRotation;
    private float nexus_recoverySmoothness;
    private float nexus_recoverySpeed;


    @Override
    @Native
    public Vector2f updateRotation(AuraNew parentModule, LivingEntity target, Minecraft mc, Vector2f currentLerpRotation) {
        if (target == null || mc.player == null) {
            return currentLerpRotation;
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastStyleSwitchTime > STYLE_SWITCH_DELAY + ThreadLocalRandom.current().nextLong(-1000, 1000)) {
            lastStyleSwitchTime = currentTime;
            resetState();
        }


        return updateFocusRotation(parentModule, target, mc, currentLerpRotation);
    }


    private Vector2f updateFocusRotation(AuraNew parentModule, LivingEntity target, Minecraft mc, Vector2f currentLerpRotation) {
        long currentTime = System.currentTimeMillis();

        if (focus_isRecovering) {
            return handleFocusRecovery(currentTime, currentLerpRotation, parentModule);
        }

        if (currentTime - focus_lastJitterSwitchTime > FOCUS_JITTER_SWITCH_DELAY + ThreadLocalRandom.current().nextLong(-500, 500)) {
            focus_currentJitterType = JitterType.values()[ThreadLocalRandom.current().nextInt(JitterType.values().length)];
            focus_lastJitterSwitchTime = currentTime;
        }

        if (focus_currentState != AimState.TRACKING && currentTime >= focus_stateEndTime) {
            focus_currentState = AimState.TRACKING;
        }

        if (focus_currentState == AimState.TRACKING) {
            if (ThreadLocalRandom.current().nextFloat() < 0.015f) {
                focus_currentState = AimState.GLIDING;
                focus_stateEndTime = currentTime + ThreadLocalRandom.current().nextLong(150, 300);
            }
        }

        Vector3d playerEyes = mc.player.getEyePosition(1.0f);
        Vector2f targetRotation;

        if (focus_currentState == AimState.SNAPPING) {
            focus_targetAimPoint = selectNewAimPoint(target, 0.75f, 0.0f);
            Vector3d direction = focus_targetAimPoint.subtract(playerEyes);
            float yaw = (float) (Math.toDegrees(Math.atan2(direction.z, direction.x)) - 90.0f);
            float pitch = (float) -Math.toDegrees(Math.atan2(direction.y, Math.sqrt(direction.x * direction.x + direction.z * direction.z)));
            targetRotation = new Vector2f(yaw, pitch);
        } else if (focus_currentState == AimState.POST_HIT_FLICK || focus_currentState == AimState.UPWARD_FLICK || focus_currentState == AimState.BIG_FLICK || focus_currentState == AimState.SPINNING) {
            targetRotation = focus_flickTargetRotation;
        } else {
            if (focus_targetAimPoint == null || currentTime - focus_lastAimPointSwitchTime > (FOCUS_AIM_POINT_SWITCH_DELAY + ThreadLocalRandom.current().nextLong(-50, 50))) {
                focus_targetAimPoint = selectNewAimPoint(target);
                focus_lastAimPointSwitchTime = currentTime;
                if (focus_visualAimPoint == null) {
                    focus_visualAimPoint = focus_targetAimPoint;
                }
            }

            focus_visualAimPoint = focus_visualAimPoint.lerp(focus_targetAimPoint, 0.18f + ThreadLocalRandom.current().nextFloat() * 0.05f);
            Vector3d finalAimPoint = applyFocusJitter(focus_visualAimPoint, target);

            AxisAlignedBB targetBox = target.getBoundingBox().grow(0.1);
            finalAimPoint = new Vector3d(
                    MathHelper.clamp(finalAimPoint.x, targetBox.minX, targetBox.maxX),
                    MathHelper.clamp(finalAimPoint.y, targetBox.minY, targetBox.maxY),
                    MathHelper.clamp(finalAimPoint.z, targetBox.minZ, targetBox.maxZ)
            );

            if (focus_currentState == AimState.GLIDING) {
                Vector3d slideVector = finalAimPoint.subtract(playerEyes).normalize().rotateYaw((float) Math.toRadians(15f));
                finalAimPoint = finalAimPoint.add(slideVector.scale(0.5d));
            }

            Vector3d direction = finalAimPoint.subtract(playerEyes);
            float yaw = (float) (Math.toDegrees(Math.atan2(direction.z, direction.x)) - 90.0f);
            float pitch = (float) -Math.toDegrees(Math.atan2(direction.y, Math.sqrt(direction.x * direction.x + direction.z * direction.z)));
            targetRotation = new Vector2f(yaw, pitch);
        }

        float yawDiff = MathHelper.wrapDegrees(targetRotation.x - currentLerpRotation.x);
        float pitchDiff = targetRotation.y - currentLerpRotation.y;

        float momentumFactor = 0.04f + ThreadLocalRandom.current().nextFloat() * 0.01f;
        float momentumDecay = 0.80f + ThreadLocalRandom.current().nextFloat() * 0.1f;
        focus_momentumYaw = MathHelper.lerp(1.0f - momentumDecay, focus_momentumYaw, yawDiff * momentumFactor);
        focus_momentumPitch = MathHelper.lerp(1.0f - momentumDecay, focus_momentumPitch, pitchDiff * momentumFactor);

        yawDiff += focus_momentumYaw;
        pitchDiff += focus_momentumPitch;

        float smoothness;
        if (focus_currentState == AimState.SNAPPING) {
            smoothness = 0.75f;
        } else if (focus_currentState == AimState.POST_HIT_FLICK || focus_currentState == AimState.UPWARD_FLICK) {
            smoothness = 0.22f + ThreadLocalRandom.current().nextFloat() * 0.06f;
        } else {
            smoothness = 0.35f + ThreadLocalRandom.current().nextFloat() * 0.1f;
        }

        float adaptiveSmoothness = smoothness + (Math.abs(yawDiff) / 180f) * 0.1f;

        float newYaw = currentLerpRotation.x + yawDiff * adaptiveSmoothness;
        float newPitch = currentLerpRotation.y + pitchDiff * adaptiveSmoothness;

        float deltaYaw = Math.abs(MathHelper.wrapDegrees(newYaw - focus_lastYaw));
        float deltaPitch = Math.abs(newPitch - focus_lastPitch);

        float yawSpeed = 70f + ThreadLocalRandom.current().nextFloat() * 20f;
        ChatUtil.addText(yawSpeed);

        float pitchSpeed = 30f + ThreadLocalRandom.current().nextFloat() * 20f;

        if (deltaYaw > 5f) pitchSpeed *= 0.15f;
        if (deltaPitch > 2.5f) yawSpeed *= 0.15f;

        newYaw = GCDUtils.getSensitivity(newYaw);
        newPitch = GCDUtils.getSensitivity(MathHelper.clamp(newPitch, -90f, 90f));

        this.focus_lastYaw = newYaw;
        this.focus_lastPitch = newPitch;

        Vector2f result = new Vector2f(newYaw, newPitch);

        applyRotation(new Rotation(newYaw, newPitch), yawSpeed, pitchSpeed, 0, 4);
        parentModule.setLerpRotation(result);

        return result;
    }

    private Vector2f handleFocusRecovery(long currentTime, Vector2f currentRotation, AuraNew parentModule) {
        long elapsed = currentTime - focus_recoveryStartTime;

        if (elapsed >= focus_recoveryDuration) {
            focus_isRecovering = false;
            return currentRotation;
        }

        float progress = (float) elapsed / focus_recoveryDuration;
        float easedProgress = easeInOutCubic(progress);

        float yaw = MathHelper.lerp(easedProgress, focus_recoveryStartRotation.x, focus_recoveryTargetRotation.x);
        float pitch = MathHelper.lerp(easedProgress, focus_recoveryStartRotation.y, focus_recoveryTargetRotation.y);

        float microShake = (ThreadLocalRandom.current().nextFloat() - 0.5f) * focus_recoverySmoothness * 0.5f;
        yaw += microShake;
        pitch += microShake * 0.6f;

        yaw = GCDUtils.getSensitivity(yaw);
        pitch = GCDUtils.getSensitivity(MathHelper.clamp(pitch, -90f, 90f));

        Vector2f result = new Vector2f(yaw, pitch);
        applyRotation(new Rotation(yaw, pitch), focus_recoverySpeed, focus_recoverySpeed * 0.8f, 0, 4);
        parentModule.setLerpRotation(result);

        return result;
    }

    private Vector2f updateNexusRotation(AuraNew parentModule, LivingEntity target, Minecraft mc, Vector2f currentLerpRotation) {
        long nowNs = System.nanoTime();
        float dt = nexus_lastNs == 0L ? 0.016f : MathHelper.clamp((nowNs - nexus_lastNs) / 1_000_000_000f, 0.008f, 0.05f);
        nexus_lastNs = nowNs;

        nexus_tickCounter++;

        long currentTime = System.currentTimeMillis();

        if (nexus_isRecovering) {
            return handleNexusRecovery(currentTime, currentLerpRotation, parentModule);
        }

        nexus_updateJitterPattern();
        nexus_updateHumanizedEffects();
        nexus_updateFatigue();

        Vector3d eyePosition = mc.player.getEyePosition(mc.getRenderPartialTicks());
        Vector3d targetCenter = target.getPositionVec().add(0, target.getEyeHeight() * 0.65, 0);

        float entityRadius = Math.max(target.getWidth(), target.getHeight()) * 0.5f;
        Vector2f jitterOffset = nexus_calculateJitterOffset(dt, entityRadius);

        Vector3d targetWithJitter = targetCenter.add(jitterOffset.x, jitterOffset.y, 0);
        Vector3d directionVec = targetWithJitter.subtract(eyePosition).normalize();

        float requiredYaw = (float) Math.toDegrees(Math.atan2(-directionVec.x, directionVec.z));
        float requiredPitch = (float) MathHelper.clamp(-Math.toDegrees(Math.atan2(directionVec.y, Math.hypot(directionVec.x, directionVec.z))), -90F, 90F);

        float targetYaw = requiredYaw;
        float targetPitch = requiredPitch;

        float attackProgress = parentModule.cooldownFromLastSwing();
        nexus_updateRotationPhase(attackProgress, mc.player.getDistance(target));

        boolean shouldAim = attackProgress > 0.78f && !nexus_inPostAttack;

        if (attackProgress > 0.65f && attackProgress < 0.85f && !nexus_inPostAttack) {
            nexus_preAttackTicks++;
            nexus_preAttackTension = Math.min(nexus_preAttackTicks * 0.008f, 0.12f);
            targetYaw += (ThreadLocalRandom.current().nextFloat() - 0.5f) * nexus_preAttackTension;
            targetPitch += (ThreadLocalRandom.current().nextFloat() - 0.5f) * nexus_preAttackTension * 0.8f;
        } else {
            nexus_preAttackTicks = 0;
            nexus_preAttackTension = 0.0f;
        }

        if (shouldAim && !nexus_hasOvershot && !nexus_inPostAttack && ThreadLocalRandom.current().nextFloat() < 0.12f) {
            float yawDist = Math.abs(MathHelper.wrapDegrees(targetYaw - currentLerpRotation.x));
            float pitchDist = Math.abs(targetPitch - currentLerpRotation.y);
            float speedFactor = (nexus_lastYawSpeed + nexus_lastPitchSpeed) / 2.0f;
            if ((yawDist > 2.5f || pitchDist > 1.8f) && speedFactor > 5.0f) {
                nexus_overshootYaw = (ThreadLocalRandom.current().nextFloat() - 0.5f) * (0.9f + speedFactor * 0.05f);
                nexus_overshootPitch = (ThreadLocalRandom.current().nextFloat() - 0.5f) * (0.7f + speedFactor * 0.04f);
                nexus_hasOvershot = true;
            }
        }

        if (nexus_hasOvershot) {
            float overshootDecay = 1f - (float) Math.exp(-5.5f * dt);
            nexus_overshootYaw *= (1.0f - overshootDecay);
            nexus_overshootPitch *= (1.0f - overshootDecay);
            targetYaw += nexus_overshootYaw;
            targetPitch += nexus_overshootPitch;
            if (Math.abs(nexus_overshootYaw) < 0.04f && Math.abs(nexus_overshootPitch) < 0.04f) {
                nexus_hasOvershot = false;
            }
        }

        if (nexus_inPostAttack) {
            nexus_updatePostAttack();
            targetYaw += nexus_postAttackYawDrift;
            targetPitch += nexus_postAttackPitchDrift;
        }

        Vector2f finalTargetRotation;
        if (shouldAim && !nexus_inPostAttack) {
            finalTargetRotation = new Vector2f(targetYaw, targetPitch);
        } else if (nexus_inPostAttack) {
            finalTargetRotation = new Vector2f(currentLerpRotation.x + nexus_postAttackYawDrift, currentLerpRotation.y + nexus_postAttackPitchDrift);
        } else {
            finalTargetRotation = currentLerpRotation;
        }

        Vector2f finalRotation = nexus_interpolateRotation(currentLerpRotation, finalTargetRotation, dt);

        float momentumDecay = 1f - (float) Math.exp(-6.5f * dt);
        nexus_momentumYaw *= (1.0f - momentumDecay);
        nexus_momentumPitch *= (1.0f - momentumDecay);

        finalRotation = new Vector2f(finalRotation.x + nexus_momentumYaw, MathHelper.clamp(finalRotation.y + nexus_momentumPitch, -90.0f, 90.0f));

        parentModule.setLerpRotation(finalRotation);
        applyRotation(new Rotation(finalRotation.x, finalRotation.y), 360, 100, 1, 3);

        return finalRotation;
    }

    private Vector2f handleNexusRecovery(long currentTime, Vector2f currentRotation, AuraNew parentModule) {
        long elapsed = currentTime - nexus_recoveryStartTime;

        if (elapsed >= nexus_recoveryDuration) {
            nexus_isRecovering = false;
            return currentRotation;
        }

        float progress = (float) elapsed / nexus_recoveryDuration;
        float easedProgress = easeInOutCubic(progress);

        float yaw = MathHelper.lerp(easedProgress, nexus_recoveryStartRotation.x, nexus_recoveryTargetRotation.x);
        float pitch = MathHelper.lerp(easedProgress, nexus_recoveryStartRotation.y, nexus_recoveryTargetRotation.y);

        float microShake = (ThreadLocalRandom.current().nextFloat() - 0.5f) * nexus_recoverySmoothness * 0.5f;
        yaw += microShake;
        pitch += microShake * 0.6f;

        yaw = GCDUtils.getSensitivity(yaw);
        pitch = GCDUtils.getSensitivity(MathHelper.clamp(pitch, -90f, 90f));

        Vector2f result = new Vector2f(yaw, pitch);
        applyRotation(new Rotation(yaw, pitch), nexus_recoverySpeed, nexus_recoverySpeed * 0.8f, 0, 4);
        parentModule.setLerpRotation(result);

        return result;
    }


    public void onPreAttack(LivingEntity target) {
        {
            if (focus_currentState != AimState.TRACKING) {
                focus_currentState = AimState.SNAPPING;
                focus_stateEndTime = System.currentTimeMillis() + 80;
            }
        }
    }

    public void onPostAttack(LivingEntity target, Vector2f currentRotation) {
        if (ThreadLocalRandom.current().nextFloat() > 0.5f) {
        }
    }

    private void startFocusRecovery(Vector2f currentRotation) {
        focus_isRecovering = true;
        focus_recoveryStartTime = System.currentTimeMillis();
        focus_recoveryStartRotation = currentRotation;

        float recoveryAngle = 45 + ThreadLocalRandom.current().nextFloat() * 55;
        float direction = ThreadLocalRandom.current().nextBoolean() ? 1 : -1;

        float targetYaw;
        float targetPitch;

        if (ThreadLocalRandom.current().nextFloat() < 0.12f) {
            targetYaw = currentRotation.x + (ThreadLocalRandom.current().nextFloat() - 0.5f) * 30;
            targetPitch = -85 + ThreadLocalRandom.current().nextFloat() * 10;
        } else {
            targetYaw = MathHelper.wrapDegrees(currentRotation.x + (recoveryAngle * direction));
            targetPitch = currentRotation.y + (ThreadLocalRandom.current().nextFloat() - 0.5f) * 40;
        }

        focus_recoveryTargetRotation = new Vector2f(targetYaw, MathHelper.clamp(targetPitch, -90, 90));
        focus_recoveryDuration = 250 + ThreadLocalRandom.current().nextLong(150);
        focus_recoverySmoothness = 0.15f + ThreadLocalRandom.current().nextFloat() * 0.25f;
        focus_recoverySpeed = 40 + ThreadLocalRandom.current().nextFloat() * 40;
    }

    private void startNexusRecovery(Vector2f currentRotation) {
        nexus_isRecovering = true;
        nexus_recoveryStartTime = System.currentTimeMillis();
        nexus_recoveryStartRotation = currentRotation;

        float recoveryAngle = 45 + ThreadLocalRandom.current().nextFloat() * 55;
        float direction = ThreadLocalRandom.current().nextBoolean() ? 1 : -1;

        float targetYaw;
        float targetPitch;

        if (ThreadLocalRandom.current().nextFloat() < 0.12f) {
            targetYaw = currentRotation.x + (ThreadLocalRandom.current().nextFloat() - 0.5f) * 30;
            targetPitch = -85 + ThreadLocalRandom.current().nextFloat() * 10;
        } else {
            targetYaw = MathHelper.wrapDegrees(currentRotation.x + (recoveryAngle * direction));
            targetPitch = currentRotation.y + (ThreadLocalRandom.current().nextFloat() - 0.5f) * 40;
        }

        nexus_recoveryTargetRotation = new Vector2f(targetYaw, MathHelper.clamp(targetPitch, -90, 90));
        nexus_recoveryDuration = 250 + ThreadLocalRandom.current().nextLong(150);
        nexus_recoverySmoothness = 0.15f + ThreadLocalRandom.current().nextFloat() * 0.25f;
        nexus_recoverySpeed = 40 + ThreadLocalRandom.current().nextFloat() * 40;
    }

    private float easeInOutCubic(float t) {
        return t < 0.5f ? 4 * t * t * t : 1 - (float) Math.pow(-2 * t + 2, 3) / 2;
    }

    private Vector3d applyFocusJitter(Vector3d aimPoint, LivingEntity target) {
        long time = System.currentTimeMillis();
        float jitterSize = Math.min(target.getWidth() * 0.3f, 0.25f);
        float speed = 0.0025f + ThreadLocalRandom.current().nextFloat() * 0.001f;
        float jitterX = 0, jitterY = 0, jitterZ = 0;

        switch (focus_currentJitterType) {
            case CIRCULAR:
                float angle = time * speed * 2f;
                jitterX = (float) Math.cos(angle) * jitterSize;
                jitterZ = (float) Math.sin(angle) * jitterSize;
                jitterY = (float) Math.sin(angle * 1.5f) * jitterSize * 0.2f;
                break;
            case WAVY:
                float wave = time * speed;
                jitterX = (float) Math.sin(wave) * jitterSize;
                jitterY = (float) Math.cos(wave * 0.7f) * jitterSize * 0.3f;
                break;
            case CHAOTIC:
                float nTime = time * 0.0007f;
                jitterX = (float) ((focus_chaosNoise.noise(nTime, 0.1, 0.2) - 0.5f) * 2f * jitterSize);
                jitterY = (float) ((focus_chaosNoise.noise(0.2, nTime, 0.1) - 0.5f) * 2f * jitterSize * 0.4f);
                jitterZ = (float) ((focus_chaosNoise.noise(0.1, 0.2, nTime) - 0.5f) * 2f * jitterSize);
                break;
            case TECHNICAL:
                float tTime = time * 0.0004f;
                float tTime2 = time * 0.001f;
                jitterX = (float) ((focus_techNoise1.noise(tTime, 0.3, 0.4) - 0.5f) * jitterSize);
                jitterZ = (float) ((focus_techNoise1.noise(0.4, 0.3, tTime) - 0.5f) * jitterSize);
                jitterY = (float) ((focus_techNoise2.noise(tTime2, tTime2, 0.1) - 0.5f) * jitterSize * 0.5f);
                break;
        }
        return aimPoint.add(jitterX, jitterY, jitterZ);
    }

    private Vector3d selectNewAimPoint(LivingEntity target) {
        float heightPercent = 0.3f + ThreadLocalRandom.current().nextFloat() * 0.6f;
        float randomWidthVariation = (ThreadLocalRandom.current().nextFloat() - 0.5f) * target.getWidth() * 0.7f;
        return selectNewAimPoint(target, heightPercent, randomWidthVariation);
    }

    private Vector3d selectNewAimPoint(LivingEntity target, float heightPercent, float widthOffset) {
        float finalHeight = target.getEyeHeight() * heightPercent;
        return target.getPositionVec().add(widthOffset, finalHeight, widthOffset);
    }

    private void nexus_updateJitterPattern() {
        nexus_jitterPatternTicks++;

        if (nexus_jitterPatternTicks >= nexus_nextJitterPatternChange) {
            NexusJitterPattern[] patterns = NexusJitterPattern.values();
            nexus_currentJitterPattern = patterns[ThreadLocalRandom.current().nextInt(patterns.length)];
            nexus_jitterPatternTicks = 0;
            nexus_nextJitterPatternChange = ThreadLocalRandom.current().nextInt(3, 13) * 20;

            nexus_jitterSmoothness = 0.12f + ThreadLocalRandom.current().nextFloat() * 0.18f;
            nexus_jitterAmplitude = 0.15f + ThreadLocalRandom.current().nextFloat() * 0.25f;
            nexus_jitterFrequency = 0.6f + ThreadLocalRandom.current().nextFloat() * 0.8f;
            nexus_jitterVerticalBias = 0.5f + ThreadLocalRandom.current().nextFloat() * 0.5f;
        }
        if (nexus_tickCounter % 3 == 0) {
            nexus_jitterSmoothness = MathHelper.clamp(nexus_jitterSmoothness + (ThreadLocalRandom.current().nextFloat() - 0.5f) * 0.02f, 0.1f, 0.35f);
            nexus_jitterAmplitude = MathHelper.clamp(nexus_jitterAmplitude + (ThreadLocalRandom.current().nextFloat() - 0.5f) * 0.03f, 0.1f, 0.45f);
            nexus_jitterFrequency = MathHelper.clamp(nexus_jitterFrequency + (ThreadLocalRandom.current().nextFloat() - 0.5f) * 0.05f, 0.4f, 1.6f);
        }
    }

    private Vector2f nexus_calculateJitterOffset(float dt, float entityRadius) {
        switch (nexus_currentJitterPattern) {
            case WAVE:
                nexus_jitterWavePhase += dt * nexus_jitterFrequency * 2.5f;
                float waveX = (float) Math.sin(nexus_jitterWavePhase) * entityRadius * nexus_jitterAmplitude;
                float waveY = (float) Math.cos(nexus_jitterWavePhase * 0.7f) * entityRadius * nexus_jitterAmplitude * nexus_jitterVerticalBias;
                nexus_jitterTargetX = waveX;
                nexus_jitterTargetY = waveY;
                break;
            case CHAOTIC:
                if (nexus_tickCounter % 4 == 0) {
                    float randomAngle = ThreadLocalRandom.current().nextFloat() * (float) Math.PI * 2.0f;
                    float randomDistance = ThreadLocalRandom.current().nextFloat() * entityRadius * nexus_jitterAmplitude;
                    nexus_jitterTargetX = (float) Math.cos(randomAngle) * randomDistance;
                    nexus_jitterTargetY = (float) Math.sin(randomAngle) * randomDistance * nexus_jitterVerticalBias;
                    if (ThreadLocalRandom.current().nextFloat() < 0.15f) {
                        nexus_jitterTargetX *= 1.3f;
                        nexus_jitterTargetY *= 1.3f;
                    }
                }
                break;
            case VERTICAL:
                nexus_jitterWavePhase += dt * nexus_jitterFrequency * 3.0f;
                float verticalOscillation = (float) Math.sin(nexus_jitterWavePhase) * entityRadius * nexus_jitterAmplitude;
                nexus_jitterTargetX = verticalOscillation * 0.3f;
                nexus_jitterTargetY = verticalOscillation * nexus_jitterVerticalBias;
                break;
        }

        float smoothFactor = 1.0f - (float) Math.exp(-nexus_jitterSmoothness * 50.0f * dt);
        nexus_jitterOffsetX += (nexus_jitterTargetX - nexus_jitterOffsetX) * smoothFactor;
        nexus_jitterOffsetY += (nexus_jitterTargetY - nexus_jitterOffsetY) * smoothFactor;

        return new Vector2f(nexus_jitterOffsetX, nexus_jitterOffsetY);
    }

    private Vector2f nexus_interpolateRotation(Vector2f current, Vector2f target, float dt) {
        float yawDiff = MathHelper.wrapDegrees(target.x - current.x);
        float pitchDiff = target.y - current.y;

        float totalDiff = (float) Math.hypot(yawDiff, pitchDiff);
        if (totalDiff < 0.01f) return current;

        float absYawDiff = Math.abs(yawDiff);
        float absPitchDiff = Math.abs(pitchDiff);

        float yawPitchBalance = (absYawDiff > absPitchDiff) ? 0.65f + ThreadLocalRandom.current().nextFloat() * 0.2f : 0.35f + ThreadLocalRandom.current().nextFloat() * 0.2f;
        float baseSpeed = 12.5f + ThreadLocalRandom.current().nextFloat() * 6.5f;
        float dynamicMultiplier = 1.0f + Math.min(totalDiff * 0.065f, 8.5f);

        float yawSpeedTarget = baseSpeed * dynamicMultiplier * (absYawDiff / totalDiff);
        float pitchSpeedTarget = baseSpeed * dynamicMultiplier * (absPitchDiff / totalDiff);

        if (absYawDiff > absPitchDiff) pitchSpeedTarget *= yawPitchBalance;
        else yawSpeedTarget *= yawPitchBalance;

        float speedVariation = 0.9f + ThreadLocalRandom.current().nextFloat() * 0.25f;
        yawSpeedTarget *= speedVariation * nexus_hesitationSlowdown * nexus_fatigueMultiplier * (nexus_inPostAttack ? 0.65f : 1.0f);
        pitchSpeedTarget *= speedVariation * nexus_hesitationSlowdown * nexus_fatigueMultiplier * (nexus_inPostAttack ? 0.65f : 1.0f);

        float speedSmoothFactor = 1.0f - (float) Math.exp(-6.0f * dt);
        float yawSpeed = nexus_lastYawSpeed + (yawSpeedTarget - nexus_lastYawSpeed) * speedSmoothFactor;
        float pitchSpeed = nexus_lastPitchSpeed + (pitchSpeedTarget - nexus_lastPitchSpeed) * speedSmoothFactor;
        nexus_lastYawSpeed = yawSpeed;
        nexus_lastPitchSpeed = pitchSpeed;

        float yawMaxDelta = yawSpeed * dt * 60.0f * (0.88f + ThreadLocalRandom.current().nextFloat() * 0.24f);
        float pitchMaxDelta = pitchSpeed * dt * 60.0f * (0.88f + ThreadLocalRandom.current().nextFloat() * 0.24f);

        float yawChange = MathHelper.clamp(yawDiff, -yawMaxDelta, yawMaxDelta);
        float pitchChange = MathHelper.clamp(pitchDiff, -pitchMaxDelta, pitchMaxDelta);

        if (ThreadLocalRandom.current().nextFloat() < 0.03f) {
            float stutter = 1.0f - ThreadLocalRandom.current().nextFloat() * 0.4f;
            yawChange *= stutter;
            pitchChange *= stutter;
        }

        float microAdjustment = 0.92f + ThreadLocalRandom.current().nextFloat() * 0.16f;
        yawChange *= microAdjustment;
        pitchChange *= microAdjustment;

        nexus_momentumYaw += yawChange * (0.06f + ThreadLocalRandom.current().nextFloat() * 0.03f);
        nexus_momentumPitch += pitchChange * (0.06f + ThreadLocalRandom.current().nextFloat() * 0.03f);

        return new Vector2f(current.x + yawChange, MathHelper.clamp(current.y + pitchChange, -90.0f, 90.0f));
    }

    private void nexus_updateHumanizedEffects() {
        if (!nexus_isHesitating && ThreadLocalRandom.current().nextFloat() < 0.003f) {
            nexus_isHesitating = true;
            nexus_hesitationTicks = 0;
        }
        if (nexus_isHesitating) {
            nexus_hesitationTicks++;
            if (nexus_hesitationTicks < ThreadLocalRandom.current().nextInt(2, 8)) {
                nexus_hesitationSlowdown = 0.3f + ThreadLocalRandom.current().nextFloat() * 0.4f;
            } else {
                nexus_isHesitating = false;
                nexus_hesitationSlowdown = 1.0f;
            }
        }
    }

    private void nexus_updateFatigue() {
        if (nexus_attackCounter > 0) {
            nexus_fatigueMultiplier = 1.0f - Math.min(nexus_attackCounter * 0.007f, 0.14f);
            nexus_attackCounter--;
        } else {
            nexus_fatigueMultiplier = 1.0f;
        }
    }

    private void nexus_initiatePostAttack() {
        if (!nexus_inPostAttack) {
            nexus_inPostAttack = true;
            nexus_postAttackTicks = 0;
            nexus_postAttackDuration = ThreadLocalRandom.current().nextInt(3, 9);
            nexus_attackCounter += ThreadLocalRandom.current().nextInt(7, 14);
            float driftStrength = 0.1f * (1.0f + ThreadLocalRandom.current().nextFloat() * 0.45f);
            nexus_postAttackYawDrift = (ThreadLocalRandom.current().nextFloat() - 0.5f) * 1.8f * driftStrength;
            nexus_postAttackPitchDrift = (ThreadLocalRandom.current().nextFloat() - 0.5f) * 1.3f * driftStrength;
        }
    }

    private void nexus_updatePostAttack() {
        nexus_postAttackTicks++;
        if (nexus_postAttackTicks >= nexus_postAttackDuration) {
            nexus_inPostAttack = false;
            nexus_postAttackYawDrift = 0.0f;
            nexus_postAttackPitchDrift = 0.0f;
            return;
        }
        float decayFactor = 1.0f - ((float) nexus_postAttackTicks / nexus_postAttackDuration);
        nexus_postAttackYawDrift *= decayFactor * 0.94f;
        nexus_postAttackPitchDrift *= decayFactor * 0.94f;
    }

    private void nexus_updateRotationPhase(float attackProgress, float distance) {
        if (nexus_inPostAttack) nexus_currentPhase = NexusRotationPhase.RECOVERING;
        else if (attackProgress > 0.85f) nexus_currentPhase = NexusRotationPhase.ATTACKING;
        else if (attackProgress > 0.5f || distance < 2.5) nexus_currentPhase = NexusRotationPhase.LOCKED;
        else if (attackProgress > 0.2f) nexus_currentPhase = NexusRotationPhase.APPROACHING;
        else nexus_currentPhase = NexusRotationPhase.IDLE;
    }

    @Override
    public void onEnable(AuraNew parentModule) {
        resetState();
    }

    @Override
    public void onDisable(AuraNew parentModule) {
        resetState();
    }

    private void resetState() {
        if (mc.player != null) {
            focus_lastYaw = mc.player.rotationYaw;
            focus_lastPitch = mc.player.rotationPitch;
        }
        focus_currentState = AimState.TRACKING;
        focus_currentJitterType = JitterType.TECHNICAL;
        focus_lastJitterSwitchTime = 0L;
        focus_stateEndTime = 0L;
        focus_visualAimPoint = null;
        focus_targetAimPoint = null;
        focus_lastAimPointSwitchTime = 0L;
        focus_momentumYaw = 0f;
        focus_momentumPitch = 0f;
        focus_flickTargetRotation = null;
        focus_isRecovering = false;
        focus_recoveryStartTime = 0L;
        focus_recoveryDuration = 0L;
        focus_recoveryStartRotation = null;
        focus_recoveryTargetRotation = null;
        focus_recoverySmoothness = 0f;
        focus_recoverySpeed = 0f;

        nexus_lastNs = 0L;
        nexus_currentPhase = NexusRotationPhase.IDLE;
        nexus_lastYawSpeed = 0.0f;
        nexus_lastPitchSpeed = 0.0f;
        nexus_overshootYaw = 0.0f;
        nexus_overshootPitch = 0.0f;
        nexus_hasOvershot = false;
        nexus_momentumYaw = 0.0f;
        nexus_momentumPitch = 0.0f;
        nexus_hesitationTicks = 0;
        nexus_isHesitating = false;
        nexus_hesitationSlowdown = 1.0f;
        nexus_postAttackYawDrift = 0.0f;
        nexus_postAttackPitchDrift = 0.0f;
        nexus_postAttackTicks = 0;
        nexus_postAttackDuration = 0;
        nexus_inPostAttack = false;
        nexus_preAttackTension = 0.0f;
        nexus_preAttackTicks = 0;
        nexus_attackCounter = 0;
        nexus_fatigueMultiplier = 1.0f;
        nexus_jitterPatternTicks = 0;
        nexus_nextJitterPatternChange = 0;
        nexus_currentJitterPattern = NexusJitterPattern.WAVE;
        nexus_jitterWavePhase = 0.0f;
        nexus_jitterOffsetX = 0.0f;
        nexus_jitterOffsetY = 0.0f;
        nexus_jitterTargetX = 0.0f;
        nexus_jitterTargetY = 0.0f;
        nexus_tickCounter = 0;
        nexus_isRecovering = false;
        nexus_recoveryStartTime = 0L;
        nexus_recoveryDuration = 0L;
        nexus_recoveryStartRotation = null;
        nexus_recoveryTargetRotation = null;
        nexus_recoverySmoothness = 0f;
        nexus_recoverySpeed = 0f;
    }

    public boolean shouldCritical() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return false;
        float requiredFallDistance = 0.0f + ThreadLocalRandom.current().nextFloat() * 0.1f;
        return mc.player.fallDistance > requiredFallDistance && !mc.player.isOnGround() && !mc.player.isOnLadder() && !mc.player.isInWater() && !mc.player.isPotionActive(Effects.BLINDNESS) && !mc.player.isPassenger();
    }
}