package org.privates.client.managers.module.impl.combat.auraelement.impl;

import com.adl.nativeprotect.Native;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import org.privates.client.managers.component.impl.rotation.Rotation;
import org.privates.client.managers.module.impl.combat.AuraNew;
import org.privates.client.managers.module.impl.combat.auraelement.AbstractRotationElement;
import org.privates.client.utils.other.GCDUtils;
import org.privates.client.utils.other.PerlinNoise;

import java.util.concurrent.ThreadLocalRandom;


public class CustomRot extends AbstractRotationElement {


    private final PerlinNoise perlin = new PerlinNoise(System.currentTimeMillis());
    private final PerlinNoise simplexNoise = new PerlinNoise(System.currentTimeMillis() + 12345);
    private boolean initialRotationDirectionChosen = false;
    private boolean startFromRight = false;
    private float driftOffsetX = 0f;
    private float driftOffsetY = 0f;
    private long lastMicroMovementTime = 0L;
    private float microMovementPhase = 0f;
    private int flickDirection = 1;

    private int currentJitterPattern = 0;
    private long lastJitterPatternChange = 0L;
    private int lastJitterPattern = -1;
    public Vector3d lastTargetVelocity;

    private float jitterPhase = 0f;
    private float jitterRadius = 0f;
    private float jitterAngle = 0f;
    private int randomShape = 0;
    private float triangleProgress = 0f;
    private float cubicTime = 0f;

    private boolean isDeflecting = false;
    private long deflectionStartTime = 0L;
    private Vector2f deflectionTarget = Vector2f.ZERO;
    private Vector2f deflectionStart = Vector2f.ZERO;
    private float spin360Progress = 0f;
    private boolean isSpin360 = false;
    private float spin360StartYaw = 0f;

    private boolean isPullingBack = false;
    private long pullbackStartTime = 0L;
    private Vector2f pullbackTarget = Vector2f.ZERO;
    private Vector2f pullbackStart = Vector2f.ZERO;
    private long lastAttackTimeForPullback = 0L;

    @Native
    @Override
    public Vector2f updateRotation(AuraNew parentModule, LivingEntity target, Minecraft mc, Vector2f currentLerpRotation) {
        if (target == null || !parentModule.customRotationEnabled().getValue()) {
            return currentLerpRotation;
        }
        long currentTime = System.currentTimeMillis();

        updateLookUpBehavior(parentModule, currentTime, currentLerpRotation);

        if (!shouldRotate(parentModule, currentTime)) {
            // Отводка при выключении ротации - возврат в исходное состояние
            return applyDisableRotationReturn(parentModule, currentLerpRotation, currentTime);
        }

        Vector3d aimPoint = getCustomAimPoint(parentModule, target);
        Vector3d playerEyes = mc.player.getEyePosition(1.0F);
        Vector3d direction = aimPoint.subtract(playerEyes);
        float targetYaw = (float) Math.toDegrees(Math.atan2(direction.z, direction.x)) - 90.0f;
        float targetPitch = (float) -Math.toDegrees(Math.atan2(direction.y, Math.sqrt(direction.x * direction.x + direction.z * direction.z)));

        if (parentModule.randomRotationStart().getValue() && !initialRotationDirectionChosen) {
            startFromRight = random.nextBoolean();
            initialRotationDirectionChosen = true;
        }

        if (parentModule.randomRotationStart().getValue()) {
            float minOffset = parentModule.randomRotationOffset().getValue().getMinValue();
            float maxOffset = parentModule.randomRotationOffset().getValue().getMaxValue();
            float randomOffset = minOffset + random.nextFloat() * (maxOffset - minOffset);
            if (startFromRight) {
                targetYaw += randomOffset;
            } else {
                targetYaw -= randomOffset;
            }
        }

        Vector2f humanized = applyHumanization(parentModule, targetYaw, targetPitch, currentTime);
        targetYaw = humanized.x;
        targetPitch = humanized.y;

        if (parentModule.aimDrift().getValue()) {
            Vector2f drift = applyAimDrift(parentModule, currentTime);
            targetYaw += drift.x;
            targetPitch += drift.y;
        }

        if (parentModule.naturalSway().getValue()) {
            Vector2f sway = applyNaturalSway(parentModule, currentTime);
            targetYaw += sway.x;
            targetPitch += sway.y;
        }

        Vector2f microMovements = applyMicroMovements(parentModule, currentTime);
        targetYaw += microMovements.x;
        targetPitch += microMovements.y;

        if (parentModule.eyeTracking().getValue() && parentModule.advancedAntiCheat().getValue()) {
            Vector2f eyeOffset = applyEyeTracking(parentModule, currentTime);
            targetYaw += eyeOffset.x;
            targetPitch += eyeOffset.y;
        }

        if (parentModule.limitRotation().getValue()) {
            targetYaw = limitYaw(parentModule, targetYaw, currentLerpRotation.x);
            targetPitch = limitPitch(parentModule, targetPitch, currentLerpRotation.y);
        }

        Vector2f rotationSpeeds = calculateAdaptiveSpeed(parentModule, targetYaw, targetPitch, currentLerpRotation);

        Vector2f smoothed = applyAdvancedSmoothing(parentModule, targetYaw, targetPitch, currentLerpRotation, rotationSpeeds);

        Vector2f jitter = applyAdvancedJitter(parentModule, currentTime);
        smoothed = new Vector2f(smoothed.x + jitter.x, smoothed.y + jitter.y);

        if (parentModule.stepRotation().getValue()) {
            smoothed = applyStepRotation(parentModule, smoothed, currentLerpRotation, currentTime);
        }

        if (parentModule.jaggedRotation().getValue()) {
            smoothed = applyJaggedRotation(parentModule, smoothed, currentTime);
        }

        if (parentModule.limitMaxPitch().getValue()) {
            smoothed = applyMaxPitchLimit(parentModule, smoothed);
        }

        if (parentModule.legacyRotation().getValue()) {
            smoothed = applyLegacyRotation(parentModule, smoothed, currentLerpRotation);
        }

        if (parentModule.angleNormalization().getValue()) {
            smoothed = normalizeAngles(parentModule, smoothed, currentLerpRotation);
        }

        smoothed = applyLookUpOffset(smoothed, currentTime);

        if (parentModule.enablePullbackAfterHit().getValue()) {
            smoothed = applyPullbackAfterHit(parentModule, smoothed, currentLerpRotation, currentTime);
        }

        if (parentModule.useGCD().getValue()) {
            if (parentModule.strictGCD().getValue()) {
                smoothed = new Vector2f(GCDUtils.getSensitivity(smoothed.x), GCDUtils.getSensitivity(smoothed.y));
            } else {
                smoothed = new Vector2f(
                        GCDUtils.getSensitivity(smoothed.x + (random.nextFloat() - 0.5f) * 0.01f),
                        GCDUtils.getSensitivity(smoothed.y + (random.nextFloat() - 0.5f) * 0.01f)
                );
            }
        }

        if (parentModule.pixelPerfect().getValue() && parentModule.advancedAntiCheat().getValue()) {
            smoothed = applyPixelPerfect(smoothed);
        }

        parentModule.setLerpRotation(smoothed);

        String mode = parentModule.rotationUpdateMode().getValue();
        applyRotation(new Rotation(smoothed.x, smoothed.y), rotationSpeeds.x, rotationSpeeds.y, 0, 5);

        return smoothed;
    }


    @Override
    public void onEnable(AuraNew parentModule) {
        parentModule.combatStartTime = System.currentTimeMillis();
        parentModule.lastYawDiff = 0f;
        parentModule.lastPitchDiff = 0f;
        parentModule.bodyPartOffsets().clear();
        isLookingUpNow = false;
        lookUpStartTimeNow = 0;
        lookUpDurationNow = 0;
        lookUpPitchTarget = 0f;
        lookUpStartPitch = 0f;
        nextLookUpDelayNow = 0;
        lastLookUpTimeNow = 0;
        initializeBodyParts(parentModule);
        parentModule.currentBodyPart = 0;
        parentModule.dynamicAimPoint = null;
        parentModule.pauseEndTime = 0L;
        parentModule.lastReactionTime = 0L;
        parentModule.targetAcquiredTime = 0L;
        parentModule.isFlicking = false;
        parentModule.currentRotationMomentumYaw = 0f;
        parentModule.currentRotationMomentumPitch = 0f;
        parentModule.swayPhase = 0f;
        lastStepTime = 0L;
        stepTarget = Vector2f.ZERO;
        jaggedPhase = 0f;
        lastJaggedYaw = 0f;
        lastJaggedPitch = 0f;
        parentModule.breathingPhase = 0f;
        parentModule.rotationHistoryList.clear();
        initialRotationDirectionChosen = false;
        startFromRight = false;
        driftOffsetX = 0f;
        driftOffsetY = 0f;
        lastMicroMovementTime = 0L;
        microMovementPhase = 0f;
        flickDirection = 1;
        currentJitterPattern = 0;
        lastJitterPatternChange = 0L;
        lastJitterPattern = -1;
        jitterPhase = 0f;
        jitterRadius = 0f;
        jitterAngle = 0f;
        randomShape = 0;
        triangleProgress = 0f;
        cubicTime = 0f;
        isDeflecting = false;
        deflectionStartTime = 0L;
        deflectionTarget = Vector2f.ZERO;
        deflectionStart = Vector2f.ZERO;
        spin360Progress = 0f;
        isSpin360 = false;
        spin360StartYaw = 0f;
        initializeBodyParts(parentModule);
    }

    private Vector3d getCustomAimPoint(AuraNew parentModule, LivingEntity target) {
        if (!parentModule.enableMultipoint().getValue()) {
            return target.getPositionVec().add(0, target.getEyeHeight() * 0.5, 0);
        }

        if (parentModule.bodyPartOffsets().isEmpty()) initializeBodyParts(parentModule);
        if (parentModule.dynamicAimPoint == null) parentModule.dynamicAimPoint = target.getPositionVec();

        Vector3d targetBodyPartOffset = getTargetBodyPart(parentModule, target);
        Vector3d targetPoint = target.getPositionVec().add(targetBodyPartOffset.x, targetBodyPartOffset.y * target.getHeight(), targetBodyPartOffset.z);

        if (parentModule.smoothAimPointTransition().getValue()) {
            float minSpeed = parentModule.aimPointTransitionSpeed().getValue().getMinValue();
            float maxSpeed = parentModule.aimPointTransitionSpeed().getValue().getMaxValue();
            float transitionSpeed = minSpeed + random.nextFloat() * (maxSpeed - minSpeed);
            parentModule.dynamicAimPoint = parentModule.dynamicAimPoint.lerp(targetPoint, transitionSpeed);
            targetPoint = parentModule.dynamicAimPoint;
        }

        return targetPoint;
    }

    private long lastStepTime = 0L;
    private Vector2f stepTarget = Vector2f.ZERO;
    private float jaggedPhase = 0f;
    private float lastJaggedYaw = 0f;
    private float lastJaggedPitch = 0f;

    private Vector2f applyStepRotation(AuraNew parentModule, Vector2f target, Vector2f current, long currentTime) {
        if (!parentModule.stepRotation().getValue()) return target;

        long stepDelay = 50;
        if (currentTime - lastStepTime < stepDelay) {
            return stepTarget;
        }

        float minYawStep = parentModule.stepYaw().getValue().getMinValue();
        float maxYawStep = parentModule.stepYaw().getValue().getMaxValue();
        float minPitchStep = parentModule.stepPitch().getValue().getMinValue();
        float maxPitchStep = parentModule.stepPitch().getValue().getMaxValue();

        float yawStep = minYawStep + random.nextFloat() * (maxYawStep - minYawStep);
        float pitchStep = minPitchStep + random.nextFloat() * (maxPitchStep - minPitchStep);

        float yawDiff = MathHelper.wrapDegrees(target.x - current.x);
        float pitchDiff = target.y - current.y;

        float yawChange = 0;
        float pitchChange = 0;

        if (Math.abs(yawDiff) > yawStep) {
            yawChange = Math.copySign(yawStep, yawDiff);
        } else {
            yawChange = yawDiff;
        }

        if (Math.abs(pitchDiff) > pitchStep) {
            pitchChange = Math.copySign(pitchStep, pitchDiff);
        } else {
            pitchChange = pitchDiff;
        }

        stepTarget = new Vector2f(current.x + yawChange, current.y + pitchChange);
        lastStepTime = currentTime;

        return stepTarget;
    }

    private boolean isLookingUpNow = false;
    private long lookUpStartTimeNow = 0;
    private int lookUpDurationNow = 0;
    private float lookUpPitchTarget = 0f;
    private float lookUpStartPitch = 0f;
    private long nextLookUpDelayNow = 0;
    private long lastLookUpTimeNow = 0;

    private void updateLookUpBehavior(AuraNew parentModule, long currentTime, Vector2f currentRotation) {
        if (!parentModule.enableLookUp().getValue()) {
            isLookingUpNow = false;
            return;
        }

        if (!isLookingUpNow) {
            if (nextLookUpDelayNow == 0) {
                float minDelay = parentModule.minLookUpDelay().getValue().getMinValue();
                float maxDelay = parentModule.minLookUpDelay().getValue().getMaxValue();
                nextLookUpDelayNow = (long) (minDelay + random.nextFloat() * (maxDelay - minDelay));
                lastLookUpTimeNow = currentTime;
            }

            if (currentTime - lastLookUpTimeNow >= nextLookUpDelayNow) {
                startLookUp(parentModule, currentRotation);
            }
        } else {
            if (currentTime - lookUpStartTimeNow >= lookUpDurationNow) {
                isLookingUpNow = false;
                float minDelay = parentModule.minLookUpDelay().getValue().getMinValue();
                float maxDelay = parentModule.minLookUpDelay().getValue().getMaxValue();
                nextLookUpDelayNow = (long) (minDelay + random.nextFloat() * (maxDelay - minDelay));
                lastLookUpTimeNow = currentTime;
            }
        }
    }

    private void startLookUp(AuraNew parentModule, Vector2f currentRotation) {
        isLookingUpNow = true;
        lookUpStartTimeNow = System.currentTimeMillis();
        float minDuration = parentModule.minLookUpDuration().getValue().getMinValue();
        float maxDuration = parentModule.minLookUpDuration().getValue().getMaxValue();
        lookUpDurationNow = (int) (minDuration + random.nextFloat() * (maxDuration - minDuration));
        lookUpStartPitch = currentRotation.y;

        float minPitch = parentModule.minLookUpPitch().getValue().getMinValue();
        float maxPitch = parentModule.minLookUpPitch().getValue().getMaxValue();
        lookUpPitchTarget = minPitch + random.nextFloat() * (maxPitch - minPitch);
    }

    private Vector2f applyLookUpOffset(Vector2f rotation, long currentTime) {
        if (!isLookingUpNow) return rotation;

        long elapsed = currentTime - lookUpStartTimeNow;
        float progress = Math.min(1.0f, (float) elapsed / lookUpDurationNow);

        float smoothProgress;
        if (progress < 0.5f) {
            smoothProgress = easeInCubic(progress * 2f) * 0.5f;
        } else {
            smoothProgress = 0.5f + easeInCubic((progress - 0.5f) * 2f) * 0.5f;
        }

        float currentPitch;
        if (smoothProgress < 0.5f) {
            currentPitch = MathHelper.lerp(smoothProgress * 2f, lookUpStartPitch, lookUpPitchTarget);
        } else {
            currentPitch = MathHelper.lerp((smoothProgress - 0.5f) * 2f, lookUpPitchTarget, lookUpStartPitch);
        }

        return new Vector2f(rotation.x, currentPitch);
    }

    private float easeInCubic(float t) {
        return t * t * t;
    }

    private Vector2f applyJaggedRotation(AuraNew parentModule, Vector2f rotation, long currentTime) {
        if (!parentModule.jaggedRotation().getValue()) return rotation;

        float minIntensity = parentModule.jaggedIntensity().getValue().getMinValue();
        float maxIntensity = parentModule.jaggedIntensity().getValue().getMaxValue();
        float minFrequency = parentModule.jaggedFrequency().getValue().getMinValue();
        float maxFrequency = parentModule.jaggedFrequency().getValue().getMaxValue();

        float intensity = minIntensity + random.nextFloat() * (maxIntensity - minIntensity);
        float frequency = minFrequency + random.nextFloat() * (maxFrequency - minFrequency);

        jaggedPhase += frequency * 0.1f;

        float jaggedYaw = (float) (Math.sin(jaggedPhase * 3.7) * intensity * 2.0);
        float jaggedPitch = (float) (Math.cos(jaggedPhase * 2.3) * intensity * 1.0);

        float randomJitter = (random.nextFloat() - 0.5f) * intensity * 0.5f;
        jaggedYaw += randomJitter;
        jaggedPitch += randomJitter * 0.5f;

        float smoothingFactor = 0.3f;
        jaggedYaw = MathHelper.lerp(smoothingFactor, lastJaggedYaw, jaggedYaw);
        jaggedPitch = MathHelper.lerp(smoothingFactor, lastJaggedPitch, jaggedPitch);

        lastJaggedYaw = jaggedYaw;
        lastJaggedPitch = jaggedPitch;

        return new Vector2f(rotation.x + jaggedYaw, rotation.y + jaggedPitch);
    }

    private Vector2f applyMaxPitchLimit(AuraNew parentModule, Vector2f rotation) {
        if (!parentModule.limitMaxPitch().getValue()) return rotation;

        float minMaxPitch = parentModule.maxPitchLimit().getValue().getMinValue();
        float maxMaxPitch = parentModule.maxPitchLimit().getValue().getMaxValue();
        float minMinPitch = parentModule.minPitchLimit().getValue().getMinValue();
        float maxMinPitch = parentModule.minPitchLimit().getValue().getMaxValue();

        float maxPitch = minMaxPitch + random.nextFloat() * (maxMaxPitch - minMaxPitch);
        float minPitch = minMinPitch + random.nextFloat() * (maxMinPitch - minMinPitch);

        float limitedPitch = MathHelper.clamp(rotation.y, minPitch, maxPitch);

        return new Vector2f(rotation.x, limitedPitch);
    }


    private Vector3d calculateStrafeDirection(LivingEntity target) {
        Vector3d motion = target.getMotion();
        double motionXZ = Math.sqrt(motion.x * motion.x + motion.z * motion.z);
        if (motionXZ < 0.01) return Vector3d.ZERO;

        float yaw = (float) Math.toDegrees(Math.atan2(motion.z, motion.x)) - 90.0f;
        float perpYaw = yaw + 90.0f;

        double perpX = Math.cos(Math.toRadians(perpYaw + 90.0f));
        double perpZ = Math.sin(Math.toRadians(perpYaw + 90.0f));

        return new Vector3d(perpX * motionXZ * 0.1, 0, perpZ * motionXZ * 0.1);
    }

    private Vector3d getTargetBodyPart(AuraNew parentModule, LivingEntity target) {
        switch (parentModule.aimPointMode().getValue()) {
            case "Ближайшая":
                return findClosestBodyPart(parentModule, target);
            case "Циклический":
                parentModule.currentBodyPart = (parentModule.currentBodyPart + 1) % parentModule.bodyPartOffsets().size();
                break;
            case "Случайный":
                parentModule.currentBodyPart = ThreadLocalRandom.current().nextInt(parentModule.bodyPartOffsets().size());
                break;
            case "Взвешенный":
                return findWeightedBodyPart(parentModule, target);
            case "Прогнозирующий":
                return findPredictiveBodyPart(parentModule, target);
            case "Динамический":
            default:
                if (random.nextDouble() < 0.1) {
                    parentModule.currentBodyPart = (parentModule.currentBodyPart + 1) % parentModule.bodyPartOffsets().size();
                }
                break;
        }
        Vector3d offset = parentModule.bodyPartOffsets().get(parentModule.currentBodyPart);
        return applyBiases(parentModule, offset);
    }

    private Vector3d findClosestBodyPart(AuraNew parentModule, LivingEntity target) {
        Vector3d eyePos = mc.player.getEyePosition(1.0f);
        Vector3d bestPart = Vector3d.ZERO;
        double bestDist = Double.MAX_VALUE;

        for (Vector3d partOffset : parentModule.bodyPartOffsets()) {
            Vector3d worldPos = target.getPositionVec().add(partOffset.x, partOffset.y * target.getHeight(), partOffset.z);
            double dist = eyePos.distanceTo(worldPos);
            if (dist < bestDist) {
                bestDist = dist;
                bestPart = partOffset;
            }
        }
        return applyBiases(parentModule, bestPart);
    }

    private Vector3d findWeightedBodyPart(AuraNew parentModule, LivingEntity target) {
        Vector3d eyePos = mc.player.getEyePosition(1.0f);
        double totalWeight = 0.0;
        double[] weights = new double[parentModule.bodyPartOffsets().size()];

        for (int i = 0; i < parentModule.bodyPartOffsets().size(); i++) {
            Vector3d partOffset = parentModule.bodyPartOffsets().get(i);
            Vector3d worldPos = target.getPositionVec().add(partOffset.x, partOffset.y * target.getHeight(), partOffset.z);
            double dist = eyePos.distanceTo(worldPos);
            double visibilityFactor = parentModule.isPointVisible(worldPos) ? 2.0 : 1.0;
            weights[i] = (1.0 / (dist + 1.0)) * visibilityFactor;
            totalWeight += weights[i];
        }

        double randValue = random.nextDouble() * totalWeight;
        double cumulative = 0.0;

        for (int i = 0; i < weights.length; i++) {
            cumulative += weights[i];
            if (randValue <= cumulative) {
                parentModule.currentBodyPart = i;
                return applyBiases(parentModule, parentModule.bodyPartOffsets().get(i));
            }
        }

        return applyBiases(parentModule, parentModule.bodyPartOffsets().get(0));
    }

    private Vector3d findPredictiveBodyPart(AuraNew parentModule, LivingEntity target) {
        Vector3d targetMotion = target.getMotion();
        double verticalMotion = targetMotion.y;

        int predictedIndex;
        if (verticalMotion > 0.1) {
            predictedIndex = (int) (parentModule.bodyPartOffsets().size() * 0.75);
        } else if (verticalMotion < -0.1) {
            predictedIndex = (int) (parentModule.bodyPartOffsets().size() * 0.25);
        } else {
            predictedIndex = parentModule.bodyPartOffsets().size() / 2;
        }

        parentModule.currentBodyPart = MathHelper.clamp(predictedIndex, 0, parentModule.bodyPartOffsets().size() - 1);
        return applyBiases(parentModule, parentModule.bodyPartOffsets().get(parentModule.currentBodyPart));
    }

    private Vector3d applyBiases(AuraNew parentModule, Vector3d offset) {
        double x = offset.x;
        double y = offset.y;
        double z = offset.z;

        if (!parentModule.verticalAimBias().is("Нет")) {
            y = applyVerticalBias(parentModule, y);
        }

        if (parentModule.horizontalBias().getValue()) {
            double horizontalOffset = applyHorizontalBias(parentModule);
            x += horizontalOffset;
        }

        return new Vector3d(x, y, z);
    }

    private double applyVerticalBias(AuraNew parentModule, double currentY) {
        double targetY;
        switch (parentModule.verticalAimBias().getValue()) {
            case "Голова":
                targetY = 0.95;
                break;
            case "Верхняя грудь":
                targetY = 0.75;
                break;
            case "Грудь":
                targetY = 0.6;
                break;
            case "Живот":
                targetY = 0.4;
                break;
            case "Ноги":
                targetY = 0.15;
                break;
            case "Адаптивное":
                if (parentModule.target() != null) {
                    double targetHealth = parentModule.target().getHealth() / parentModule.target().getMaxHealth();
                    targetY = targetHealth < 0.3 ? 0.95 : (targetHealth < 0.6 ? 0.6 : 0.4);
                } else {
                    targetY = currentY;
                }
                break;
            default:
                return currentY;
        }
        float minStrength = parentModule.verticalBiasStrength().getValue().getMinValue();
        float maxStrength = parentModule.verticalBiasStrength().getValue().getMaxValue();
        float strength = minStrength + random.nextFloat() * (maxStrength - minStrength);
        return MathHelper.lerp(strength, currentY, targetY);
    }

    private double applyHorizontalBias(AuraNew parentModule) {
        float minStrength = parentModule.horizontalBiasStrength().getValue().getMinValue();
        float maxStrength = parentModule.horizontalBiasStrength().getValue().getMaxValue();
        double strength = minStrength + random.nextFloat() * (maxStrength - minStrength);

        switch (parentModule.horizontalBiasMode().getValue()) {
            case "Центр":
                return 0.0;
            case "Левая сторона":
                return -strength * 0.3;
            case "Правая сторона":
                return strength * 0.3;
            case "Случайная сторона":
                return (random.nextBoolean() ? 1 : -1) * strength * 0.3;
            case "Циклическое":
                parentModule.horizontalBiasDirection *= -1;
                return parentModule.horizontalBiasDirection * strength * 0.3;
            default:
                return 0.0;
        }
    }

    private void initializeBodyParts(AuraNew parentModule) {
        parentModule.bodyPartOffsets().clear();
        int parts = parentModule.bodyParts().getValue().intValue();
        for (int i = 0; i < parts; i++) {
            double heightPercent = (double) i / (parts - 1);
            double xOffset = (random.nextDouble() - 0.5) * 0.3;
            double zOffset = (random.nextDouble() - 0.5) * 0.3;
            parentModule.bodyPartOffsets().add(new Vector3d(xOffset, heightPercent, zOffset));
        }
    }

    private Vector2f calculateAdaptiveSpeed(AuraNew parentModule, float targetYaw, float targetPitch, Vector2f currentLerpRotation) {
        float minYawSpeed = parentModule.yawSpeed().getValue().getMinValue();
        float maxYawSpeed = parentModule.yawSpeed().getValue().getMaxValue();
        float minPitchSpeed = parentModule.pitchSpeed().getValue().getMinValue();
        float maxPitchSpeed = parentModule.pitchSpeed().getValue().getMaxValue();

        float yawSpeed = minYawSpeed + random.nextFloat() * (maxYawSpeed - minYawSpeed);
        float pitchSpeed = minPitchSpeed + random.nextFloat() * (maxPitchSpeed - minPitchSpeed);

        float yawDelta = Math.abs(MathHelper.wrapDegrees(targetYaw - currentLerpRotation.x));
        float pitchDelta = Math.abs(targetPitch - currentLerpRotation.y);

        if (parentModule.fpsAdaptation().getValue()) {
            int currentFps = Minecraft.getDebugFPS();
            if (currentFps <= 0) currentFps = 60;
            if (currentFps > 240) currentFps = 240;

            float lowThreshold = parentModule.fpsLowThreshold().getValue();
            float highThreshold = parentModule.fpsHighThreshold().getValue();

            if (currentFps < lowThreshold) {
                float minSpeedMult = parentModule.fpsLowSpeedMultiplier().getValue().getMinValue();
                float maxSpeedMult = parentModule.fpsLowSpeedMultiplier().getValue().getMaxValue();
                float speedMultiplier = minSpeedMult + random.nextFloat() * (maxSpeedMult - minSpeedMult);
                yawSpeed *= speedMultiplier;
                pitchSpeed *= speedMultiplier;
            } else if (currentFps > highThreshold) {
                float minSpeedMult = parentModule.fpsHighSpeedMultiplier().getValue().getMinValue();
                float maxSpeedMult = parentModule.fpsHighSpeedMultiplier().getValue().getMaxValue();
                float speedMultiplier = minSpeedMult + random.nextFloat() * (maxSpeedMult - minSpeedMult);
                yawSpeed *= speedMultiplier;
                pitchSpeed *= speedMultiplier;
            }
        }

        if (parentModule.adaptiveSpeed().getValue()) {
            float minAccel = parentModule.accelerationFactor().getValue().getMinValue();
            float maxAccel = parentModule.accelerationFactor().getValue().getMaxValue();
            float acceleration = minAccel + random.nextFloat() * (maxAccel - minAccel);
            yawSpeed *= 1.0f + (yawDelta / 180.0f) * (acceleration - 1.0f);
            pitchSpeed *= 1.0f + (pitchDelta / 90.0f) * (acceleration - 1.0f);
        }

        if (parentModule.angleBasedSpeed().getValue()) {
            float minSmallMultiplier = parentModule.smallAngleMultiplier().getValue().getMinValue();
            float maxSmallMultiplier = parentModule.smallAngleMultiplier().getValue().getMaxValue();
            float minLargeMultiplier = parentModule.largeAngleMultiplier().getValue().getMinValue();
            float maxLargeMultiplier = parentModule.largeAngleMultiplier().getValue().getMaxValue();

            if (yawDelta < 30.0f) {
                float multiplier = minSmallMultiplier + random.nextFloat() * (maxSmallMultiplier - minSmallMultiplier);
                yawSpeed *= multiplier;
            } else if (yawDelta > 90.0f) {
                float multiplier = minLargeMultiplier + random.nextFloat() * (maxLargeMultiplier - minLargeMultiplier);
                yawSpeed *= multiplier;
            }

            if (pitchDelta < 15.0f) {
                float multiplier = minSmallMultiplier + random.nextFloat() * (maxSmallMultiplier - minSmallMultiplier);
                pitchSpeed *= multiplier;
            } else if (pitchDelta > 45.0f) {
                float multiplier = minLargeMultiplier + random.nextFloat() * (maxLargeMultiplier - minLargeMultiplier);
                pitchSpeed *= multiplier;
            }
        }

        if (parentModule.distanceBasedSpeed().getValue() && parentModule.target() != null) {
            float distance = mc.player.getDistance(parentModule.target());
            float minCloseMultiplier = parentModule.closeRangeMultiplier().getValue().getMinValue();
            float maxCloseMultiplier = parentModule.closeRangeMultiplier().getValue().getMaxValue();
            float minFarMultiplier = parentModule.farRangeMultiplier().getValue().getMinValue();
            float maxFarMultiplier = parentModule.farRangeMultiplier().getValue().getMaxValue();

            if (distance < 2.5f) {
                float multiplier = minCloseMultiplier + random.nextFloat() * (maxCloseMultiplier - minCloseMultiplier);
                yawSpeed *= multiplier;
                pitchSpeed *= multiplier;
            } else if (distance > 5.0f) {
                float multiplier = minFarMultiplier + random.nextFloat() * (maxFarMultiplier - minFarMultiplier);
                yawSpeed *= multiplier;
                pitchSpeed *= multiplier;
            }
        }

        if (parentModule.targetVelocityMatching().getValue() && parentModule.target() != null) {
            Vector3d targetVel = parentModule.target().getMotion();
            float minFactor = parentModule.velocityMatchFactor().getValue().getMinValue();
            float maxFactor = parentModule.velocityMatchFactor().getValue().getMaxValue();
            double factor = minFactor + random.nextFloat() * (maxFactor - minFactor);
            double angularVel = targetVel.length() * factor;
            yawSpeed *= (1.0 + angularVel);
            pitchSpeed *= (1.0 + angularVel * 0.5);
        }


        yawSpeed *= parentModule.fatigueMultiplier;
        pitchSpeed *= parentModule.fatigueMultiplier;

        // Фикс Скорости Ударов - ускорение ротации когда цель в радиусе атаки
        if (parentModule.hitSpeedFix().getValue() && parentModule.target() != null) {
            float distance = mc.player.getDistance(parentModule.target());
            float attackRange = parentModule.attackRange().getValue();

            // Если цель в радиусе атаки, ускоряем ротацию
            if (distance <= attackRange) {
                float hitFixYawDelta = Math.abs(MathHelper.wrapDegrees(targetYaw - currentLerpRotation.x));
                float hitFixPitchDelta = Math.abs(targetPitch - currentLerpRotation.y);
                float maxHitFixDelta = Math.max(hitFixYawDelta, hitFixPitchDelta);

                // Если угол больше чем угол активации - применяем ускорение
                if (maxHitFixDelta > parentModule.hitFixActivationAngle().getValue()) {
                    float minMultiplier = parentModule.hitSpeedMultiplier().getValue().getMinValue();
                    float maxMultiplier = parentModule.hitSpeedMultiplier().getValue().getMaxValue();
                    float speedMultiplier = minMultiplier + random.nextFloat() * (maxMultiplier - minMultiplier);

                    yawSpeed *= speedMultiplier;
                    pitchSpeed *= speedMultiplier;
                }
            }
        }

        return new Vector2f(yawSpeed, pitchSpeed);
    }

    private Vector2f applyAdvancedSmoothing(AuraNew parentModule, float targetYaw, float targetPitch, Vector2f currentLerpRotation, Vector2f speeds) {
        float minSmoothness = parentModule.rotationSmoothness().getValue().getMinValue();
        float maxSmoothness = parentModule.rotationSmoothness().getValue().getMaxValue();
        float smoothness = minSmoothness + random.nextFloat() * (maxSmoothness - minSmoothness);

        if (parentModule.fpsAdaptation().getValue()) {
            int currentFps = Minecraft.getDebugFPS();
            if (currentFps <= 0) currentFps = 60;
            if (currentFps > 240) currentFps = 240;

            float lowThreshold = parentModule.fpsLowThreshold().getValue();
            float highThreshold = parentModule.fpsHighThreshold().getValue();

            if (currentFps < lowThreshold) {
                float minSmoothMult = parentModule.fpsLowSmoothnessMultiplier().getValue().getMinValue();
                float maxSmoothMult = parentModule.fpsLowSmoothnessMultiplier().getValue().getMaxValue();
                float smoothnessMultiplier = minSmoothMult + random.nextFloat() * (maxSmoothMult - minSmoothMult);
                smoothness *= smoothnessMultiplier;
            } else if (currentFps > highThreshold) {
                float minSmoothMult = parentModule.fpsHighSmoothnessMultiplier().getValue().getMinValue();
                float maxSmoothMult = parentModule.fpsHighSmoothnessMultiplier().getValue().getMaxValue();
                float smoothnessMultiplier = minSmoothMult + random.nextFloat() * (maxSmoothMult - minSmoothMult);
                smoothness *= smoothnessMultiplier;
            }
        }

        if (parentModule.dynamicSmoothness().getValue()) {
            float yawDiff = Math.abs(MathHelper.wrapDegrees(targetYaw - currentLerpRotation.x));
            float factor = MathHelper.clamp(yawDiff / 90f, 0, 1);
            float minSmall = parentModule.smallAngleSmoothness().getValue().getMinValue();
            float maxSmall = parentModule.smallAngleSmoothness().getValue().getMaxValue();
            float minLarge = parentModule.largeAngleSmoothness().getValue().getMinValue();
            float maxLarge = parentModule.largeAngleSmoothness().getValue().getMaxValue();

            float smallSmooth = minSmall + random.nextFloat() * (maxSmall - minSmall);
            float largeSmooth = minLarge + random.nextFloat() * (maxLarge - minLarge);
            float dynamicMultiplier = MathHelper.lerp(factor, smallSmooth, largeSmooth);
            smoothness *= dynamicMultiplier;
        }

        // Фикс Скорости Ударов - снижение плавности для более быстрой ротации
        if (parentModule.hitSpeedFix().getValue() && parentModule.target() != null) {
            float distance = mc.player.getDistance(parentModule.target());
            float attackRange = parentModule.attackRange().getValue();

            if (distance <= attackRange) {
                float hitFixYawDelta = Math.abs(MathHelper.wrapDegrees(targetYaw - currentLerpRotation.x));
                float hitFixPitchDelta = Math.abs(targetPitch - currentLerpRotation.y);
                float maxHitFixDelta = Math.max(hitFixYawDelta, hitFixPitchDelta);

                if (maxHitFixDelta > parentModule.hitFixActivationAngle().getValue()) {
                    float minReduction = parentModule.hitSmoothnessReduction().getValue().getMinValue();
                    float maxReduction = parentModule.hitSmoothnessReduction().getValue().getMaxValue();
                    float reduction = minReduction + random.nextFloat() * (maxReduction - minReduction);

                    smoothness *= (1.0f - reduction);
                }
            }
        }

        float interpolationFactor = calculateInterpolationFactor(parentModule, smoothness);
        float minStrength = parentModule.rotationSmoothingStrength().getValue().getMinValue();
        float maxStrength = parentModule.rotationSmoothingStrength().getValue().getMaxValue();
        float smoothingStrength = minStrength + random.nextFloat() * (maxStrength - minStrength);

        float yawDiff = MathHelper.wrapDegrees(targetYaw - currentLerpRotation.x);
        float pitchDiff = targetPitch - currentLerpRotation.y;

        float minAccel = parentModule.rotationAcceleration().getValue().getMinValue();
        float maxAccel = parentModule.rotationAcceleration().getValue().getMaxValue();
        float minDecel = parentModule.rotationDeceleration().getValue().getMinValue();
        float maxDecel = parentModule.rotationDeceleration().getValue().getMaxValue();

        float acceleration = minAccel + random.nextFloat() * (maxAccel - minAccel);
        float deceleration = minDecel + random.nextFloat() * (maxDecel - minDecel);

        if (Math.abs(yawDiff) > Math.abs(parentModule.lastYawDiff)) {
            interpolationFactor *= acceleration;
        } else {
            interpolationFactor *= deceleration;
        }

        if (Math.abs(pitchDiff) > Math.abs(parentModule.lastPitchDiff)) {
            interpolationFactor *= acceleration;
        } else {
            interpolationFactor *= deceleration;
        }

        float yawStep = yawDiff * interpolationFactor * smoothingStrength;
        float pitchStep = pitchDiff * interpolationFactor * smoothingStrength;


        if (Math.abs(yawStep) > Math.abs(yawDiff)) yawStep = yawDiff;
        if (Math.abs(pitchStep) > Math.abs(pitchDiff)) pitchStep = pitchDiff;

        float newYaw = currentLerpRotation.x + yawStep;
        float newPitch = currentLerpRotation.y + pitchStep;

        parentModule.lastYawDiff = yawDiff;
        parentModule.lastPitchDiff = pitchDiff;

        return new Vector2f(newYaw, MathHelper.clamp(newPitch, -90f, 90f));
    }

    private float calculateInterpolationFactor(AuraNew parentModule, float smoothness) {
        switch (parentModule.interpolationType().getValue()) {
            case "Линейная":
                return smoothness;
            case "Плавная":
                return smoothness * smoothness * (3.0f - 2.0f * smoothness);
            case "Экспоненциальная":
                return 1.0f - (float) Math.pow(1.0f - smoothness, 2);
            case "Синусоидальная":
                return (float) (Math.sin(smoothness * Math.PI / 2));
            case "Кубическая":
                return smoothness * smoothness * smoothness;
            case "Квадратичная":
                return smoothness * smoothness;
            case "Elastic":
                if (smoothness == 0 || smoothness == 1) return smoothness;
                float p = 0.3f;
                return (float) (Math.pow(2, -10 * smoothness) * Math.sin((smoothness - p / 4) * (2 * Math.PI) / p) + 1);
            case "Bounce":
                if (smoothness < 1 / 2.75) {
                    return 7.5625f * smoothness * smoothness;
                } else if (smoothness < 2 / 2.75) {
                    smoothness -= 1.5f / 2.75f;
                    return 7.5625f * smoothness * smoothness + 0.75f;
                } else if (smoothness < 2.5 / 2.75) {
                    smoothness -= 2.25f / 2.75f;
                    return 7.5625f * smoothness * smoothness + 0.9375f;
                } else {
                    smoothness -= 2.625f / 2.75f;
                    return 7.5625f * smoothness * smoothness + 0.984375f;
                }
            default:
                return smoothness;
        }
    }


    private Vector2f applyHumanization(AuraNew parentModule, float yaw, float pitch, long currentTime) {
        if (!parentModule.humanize().getValue()) return new Vector2f(yaw, pitch);

        float minPower = parentModule.humanizePower().getValue().getMinValue();
        float maxPower = parentModule.humanizePower().getValue().getMaxValue();
        float factor = (minPower + random.nextFloat() * (maxPower - minPower)) * parentModule.fatigueMultiplier;

        float humanYaw = yaw + (random.nextFloat() - 0.5f) * factor;
        float humanPitch = pitch + (random.nextFloat() - 0.5f) * factor * 0.5f;

        if (parentModule.contextAware().getValue() && parentModule.advancedAntiCheat().getValue()) {
            if (parentModule.combatMode().getValue() && parentModule.attackCount > 3) {
                humanYaw += (random.nextFloat() - 0.5f) * factor * 0.5f;
            }
            if (parentModule.stealthMode().getValue()) {
                humanYaw += (random.nextFloat() - 0.5f) * factor * 1.5f;
                humanPitch += (random.nextFloat() - 0.5f) * factor * 0.75f;
            }
        }

        return new Vector2f(humanYaw, humanPitch);
    }

    private Vector2f applyAdvancedJitter(AuraNew parentModule, long currentTime) {
        if (!parentModule.enableJitter().getValue()) return Vector2f.ZERO;

        LivingEntity target = parentModule.target();
        if (target == null) return Vector2f.ZERO;

        Vector3d playerPos = mc.player.getEyePosition(1.0F);
        Vector3d targetPos = target.getPositionVec().add(0, target.getEyeHeight() * 0.72, 0);
        double distance = playerPos.distanceTo(targetPos);

        float minStrength = parentModule.jitterStrength().getValue().getMinValue();
        float maxStrength = parentModule.jitterStrength().getValue().getMaxValue();
        float baseStrength = minStrength + random.nextFloat() * (maxStrength - minStrength);

        float minRadius = parentModule.jitterRadius().getValue().getMinValue();
        float maxRadius = parentModule.jitterRadius().getValue().getMaxValue();
        float radiusPercent = (minRadius + random.nextFloat() * (maxRadius - minRadius)) / 100f;

        float minSmoothness = parentModule.jitterSmoothness().getValue().getMinValue();
        float maxSmoothness = parentModule.jitterSmoothness().getValue().getMaxValue();
        float smoothness = minSmoothness + random.nextFloat() * (maxSmoothness - minSmoothness);

        double bodyWidth = target.getWidth() * radiusPercent;
        double jitterRadiusValue = bodyWidth * 0.6;
        double angularRadius = Math.toDegrees(Math.atan2(jitterRadiusValue, distance));

        float minSpeed = parentModule.jitterSpeed().getValue().getMinValue();
        float maxSpeed = parentModule.jitterSpeed().getValue().getMaxValue();
        float speed = minSpeed + random.nextFloat() * (maxSpeed - minSpeed);

        float targetJitterX = 0, targetJitterY = 0;

        String jitterMode = parentModule.jitterType().getValue();

        switch (jitterMode) {
            case "Синус":
                spiralPhase += 0.022f * speed;
                targetJitterX = (float) (Math.sin(spiralPhase) * angularRadius);
                targetJitterY = (float) (Math.cos(spiralPhase) * angularRadius * 0.6);
                break;

            case "Спираль":
                spiralPhase += 0.04f * speed;
                spiralRadiusPhase += 0.04f * speed * 3.63f;
                float dynamicRadius = (float) (angularRadius * (0.6f + Math.sin(spiralRadiusPhase) * 0.4f));
                targetJitterX = (float) (Math.cos(spiralPhase) * dynamicRadius);
                targetJitterY = (float) (Math.sin(spiralPhase) * dynamicRadius * 0.6);
                break;

            case "Круговой":
                circularPhase += 0.02f * speed;
                targetJitterX = (float) (Math.cos(circularPhase) * angularRadius);
                targetJitterY = (float) (Math.sin(circularPhase) * angularRadius * 0.6);
                break;

            case "Волновой":
                wavePhase += 0.015f * speed;
                float wave1 = (float) Math.sin(wavePhase) * (float) angularRadius * 0.8f;
                float wave2 = (float) Math.cos(wavePhase * 1.2) * (float) angularRadius * 0.6f;
                float wave3 = (float) Math.sin(wavePhase * 0.8) * (float) angularRadius * 0.4f;
                targetJitterX = wave1 + wave2;
                targetJitterY = (wave1 + wave3) * 0.6f;
                break;

            case "Хаотичный":
                long chaoticUpdateInterval = 500 + (long) (Math.random() * 700);
                if (currentTime - lastChaoticUpdate > chaoticUpdateInterval) {
                    targetChaoticYaw = (Math.random() - 0.5) * 2 * angularRadius * 0.8;
                    targetChaoticPitch = (Math.random() - 0.5) * 2 * angularRadius * 0.5;
                    lastChaoticUpdate = currentTime;
                }
                float chaoticLerp = 0.06f + (float) (Math.random() * 0.05);
                chaoticOffsetYaw = MathHelper.lerp(chaoticLerp, chaoticOffsetYaw, (float) targetChaoticYaw);
                chaoticOffsetPitch = MathHelper.lerp(chaoticLerp, chaoticOffsetPitch, (float) targetChaoticPitch);
                targetJitterX = chaoticOffsetYaw;
                targetJitterY = chaoticOffsetPitch;
                break;

            case "Мультиволновой":
                if (currentTime - lastMultiUpdate > 80) {
                    targetMultiSpeed = 0.015 + Math.random() * 0.025;
                    targetMultiAmplitude = (0.5 + Math.random() * 0.6);
                    lastMultiUpdate = currentTime;
                }
                float multiLerp = 0.04f;
                multiSpeed = MathHelper.lerp(multiLerp, multiSpeed, (float) targetMultiSpeed);
                multiAmplitude = MathHelper.lerp(multiLerp, multiAmplitude, (float) targetMultiAmplitude);
                multiPhase += multiSpeed * speed;
                if (multiPhase > Math.PI * 2) multiPhase -= (float) (Math.PI * 2);

                float multi1 = (float) Math.sin(multiPhase) * (float) angularRadius * multiAmplitude;
                float multi2 = (float) Math.cos(multiPhase * 1.2) * (float) angularRadius * multiAmplitude * 0.6f;
                float multi3 = (float) Math.sin(multiPhase * 0.8) * (float) angularRadius * multiAmplitude * 0.4f;

                targetJitterX = (multi1 + multi2);
                targetJitterY = (multi1 + multi3) * 0.6f;
                break;

            case "Комбинированный":
                complexPhase += 0.03f * speed;
                complexRadiusPhase += 0.012f * speed;
                float complexRad = (float) (angularRadius * (0.5f + Math.sin(complexRadiusPhase) * 0.5f));
                float verticalOscillation = (float) Math.sin(currentTime * 0.001f * speed) * (float) angularRadius * 0.15f;

                targetJitterX = (float) (Math.cos(complexPhase) * complexRad);
                targetJitterY = (float) (Math.sin(complexPhase) * complexRad * 0.6f + verticalOscillation);
                break;
        }

        currentJitterX = MathHelper.lerp(1.0f - smoothness, currentJitterX, targetJitterX);
        currentJitterY = MathHelper.lerp(1.0f - smoothness, currentJitterY, targetJitterY);

        float jitterX = currentJitterX * baseStrength;
        float jitterY = currentJitterY * baseStrength;

        if (parentModule.jitterYawOnly().getValue()) {
            jitterY = 0;
        } else if (parentModule.jitterPitchOnly().getValue()) {
            jitterX = 0;
        }

        return new Vector2f(jitterX, jitterY);
    }

    private float spiralPhase = 0f;
    private float spiralRadiusPhase = 0f;
    private float circularPhase = 0f;
    private float wavePhase = 0f;
    private float multiPhase = 0f;
    private float multiSpeed = 0.02f;
    private float multiAmplitude = 0.8f;
    private long lastMultiUpdate = 0;
    private double targetMultiSpeed = 0.02;
    private double targetMultiAmplitude = 0.8;
    private float complexPhase = 0f;
    private float complexRadiusPhase = 0f;
    private float chaoticOffsetYaw = 0f;
    private float chaoticOffsetPitch = 0f;
    private double targetChaoticYaw = 0;
    private double targetChaoticPitch = 0;
    private long lastChaoticUpdate = 0;
    private float currentJitterX = 0f;
    private float currentJitterY = 0f;

    private void updateJitterPattern(AuraNew parentModule, long currentTime) {
        float minSpeed = parentModule.jitterPatternChangeSpeed().getValue().getMinValue();
        float maxSpeed = parentModule.jitterPatternChangeSpeed().getValue().getMaxValue();
        float patternChangeSpeed = minSpeed + random.nextFloat() * (maxSpeed - minSpeed);
        if (patternChangeSpeed <= 0) return;

        long interval = (long) (1000.0f / patternChangeSpeed);
        if (currentTime - lastJitterPatternChange >= interval) {
            int newPattern;
            if (parentModule.jitterNoRepeatPatterns().getValue()) {
                do {
                    newPattern = random.nextInt(7);
                } while (newPattern == lastJitterPattern && newPattern == currentJitterPattern);
                lastJitterPattern = currentJitterPattern;
            } else {
                newPattern = random.nextInt(7);
            }
            currentJitterPattern = newPattern;
            lastJitterPatternChange = currentTime;
            jitterPhase = 0f;
            jitterAngle = 0f;
            triangleProgress = 0f;
            cubicTime = 0f;
        }
    }

    private void syncJitterWithMultipoint(AuraNew parentModule) {
        if (currentJitterPattern != lastJitterPattern) {
            int targetBodyPart = (currentJitterPattern * parentModule.bodyPartOffsets().size()) / 7;
            parentModule.currentBodyPart = MathHelper.clamp(targetBodyPart, 0, parentModule.bodyPartOffsets().size() - 1);
        }
    }

    private Vector2f applyWaveJitter(float time, float strength) {
        float wave1 = (float) Math.sin(time * 1.5) * strength * 0.4f;
        float wave2 = (float) Math.sin(time * 3.7) * strength * 0.3f;
        float wave3 = (float) Math.sin(time * 7.3) * strength * 0.2f;
        float jitterX = wave1 + wave2 + wave3;
        float jitterY = (float) ((Math.cos(time * 2.1) * strength * 0.3f) + (Math.cos(time * 5.5) * strength * 0.2f));
        return new Vector2f(jitterX, jitterY);
    }

    private Vector2f applyChaoticJitter(float time, float strength) {
        jitterPhase += 0.1f;
        float chaos1 = (float) (Math.sin(time + jitterPhase) * Math.cos(time * 1.3 + jitterPhase));
        float chaos2 = (float) (Math.cos(time * 0.7 + jitterPhase) * Math.sin(time * 1.7 + jitterPhase));
        float chaos3 = (float) perlin.noise(time + jitterPhase, 0, 0.2);
        float jitterX = (chaos1 + chaos3 * 0.5f) * strength;
        float jitterY = (chaos2 + chaos3 * 0.3f) * strength * 0.5f;
        return new Vector2f(jitterX, jitterY);
    }

    private Vector2f applyCubicJitter(float time, float strength) {
        cubicTime += 0.05f;
        float t = (float) ((Math.sin(cubicTime) + 1) / 2);
        float cubic = t * t * t - 3 * t * t + 3 * t;
        float jitterX = cubic * strength * (float) Math.cos(time * 2);
        float jitterY = cubic * strength * (float) Math.sin(time * 2) * 0.5f;
        return new Vector2f(jitterX, jitterY);
    }

    private Vector2f applyCircularJitter(float time, float strength) {
        jitterAngle += 0.1f;
        float radius = strength * (0.5f + 0.5f * (float) Math.sin(time * 0.5));
        float jitterX = (float) Math.cos(jitterAngle) * radius;
        float jitterY = (float) Math.sin(jitterAngle) * radius * 0.5f;
        return new Vector2f(jitterX, jitterY);
    }

    private Vector2f applyRandomShapesJitter(float time, float strength) {
        if (random.nextFloat() < 0.05f) {
            randomShape = random.nextInt(4);
        }

        switch (randomShape) {
            case 0:
                return applyCircularJitter(time, strength);
            case 1:
                return applyTriangleJitter(time, strength);
            case 2:
                float square = ((int) (time * 2) % 2 == 0 ? 1 : -1) * strength;
                return new Vector2f(square, square * 0.5f);
            case 3:
                return applyWaveJitter(time, strength);
            default:
                return Vector2f.ZERO;
        }
    }

    private Vector2f applyHumanizedJitter(float time, float strength) {
        float micro1 = (random.nextFloat() - 0.5f) * strength * 0.3f;
        float micro2 = (random.nextFloat() - 0.5f) * strength * 0.15f;
        float perlinX = (float) perlin.noise(time * 0.3, 0, 0.1) * strength * 0.4f;
        float perlinY = (float) perlin.noise(0, time * 0.3, 0.1) * strength * 0.2f;
        float smoothWave = (float) Math.sin(time * 0.8) * strength * 0.2f;
        float jitterX = micro1 + perlinX + smoothWave;
        float jitterY = micro2 + perlinY + smoothWave * 0.5f;
        return new Vector2f(jitterX, jitterY);
    }

    private Vector2f applyTriangleJitter(float time, float strength) {
        triangleProgress += 0.03f;
        if (triangleProgress > 3.0f) triangleProgress = 0f;

        float jitterX, jitterY;
        if (triangleProgress < 1.0f) {
            jitterX = triangleProgress * strength;
            jitterY = triangleProgress * strength * 0.5f;
        } else if (triangleProgress < 2.0f) {
            float t = triangleProgress - 1.0f;
            jitterX = (1.0f - t) * strength - t * strength;
            jitterY = (1.0f - t * 0.5f) * strength * 0.5f;
        } else {
            float t = triangleProgress - 2.0f;
            jitterX = -strength + t * strength;
            jitterY = (0.5f - t * 0.5f) * strength;
        }
        return new Vector2f(jitterX, jitterY);
    }

    private Vector2f applyMicroMovements(AuraNew parentModule, long currentTime) {
        if (!parentModule.enableMicroMovements().getValue()) return Vector2f.ZERO;

        float minStrength = parentModule.microMovementStrength().getValue().getMinValue();
        float maxStrength = parentModule.microMovementStrength().getValue().getMaxValue();
        float strength = (minStrength + random.nextFloat() * (maxStrength - minStrength)) * parentModule.fatigueMultiplier;

        float minFrequency = parentModule.microMovementFrequency().getValue().getMinValue();
        float maxFrequency = parentModule.microMovementFrequency().getValue().getMaxValue();
        float frequency = minFrequency + random.nextFloat() * (maxFrequency - minFrequency);

        if (currentTime - lastMicroMovementTime > 50) {
            microMovementPhase += frequency;
            lastMicroMovementTime = currentTime;
        }

        float microYaw = 0, microPitch = 0;

        switch (parentModule.microMovementPattern().getValue()) {
            case "Хаотичный":
                microYaw = (float) (Math.sin(microMovementPhase) * Math.cos(microMovementPhase * 1.3) * strength);
                microPitch = (float) (Math.cos(microMovementPhase * 0.7) * Math.sin(microMovementPhase * 1.7) * strength * 0.5);
                break;
            case "Круговой":
                microYaw = (float) (Math.cos(microMovementPhase) * strength);
                microPitch = (float) (Math.sin(microMovementPhase) * strength * 0.5);
                break;
            case "Восьмерка":
                microYaw = (float) (Math.sin(microMovementPhase) * strength);
                microPitch = (float) (Math.sin(microMovementPhase * 2) * strength * 0.5);
                break;
            case "Зигзаг":
                microYaw = (microMovementPhase % 1.0 < 0.5 ? 1 : -1) * strength;
                microPitch = (float) (Math.sin(microMovementPhase * 4) * strength * 0.3);
                break;
            case "Perlin":
                microYaw = (float) (perlin.noise(microMovementPhase, 0, 0.2) * strength);
                microPitch = (float) (perlin.noise(0, microMovementPhase, 0.2) * strength * 0.5);
                break;
        }

        return new Vector2f(microYaw, microPitch);
    }

    private Vector2f applyAimDrift(AuraNew parentModule, long currentTime) {
        float minStrength = parentModule.driftStrength().getValue().getMinValue();
        float maxStrength = parentModule.driftStrength().getValue().getMaxValue();
        float strength = minStrength + random.nextFloat() * (maxStrength - minStrength);

        float minSpeed = parentModule.driftSpeed().getValue().getMinValue();
        float maxSpeed = parentModule.driftSpeed().getValue().getMaxValue();
        float speed = minSpeed + random.nextFloat() * (maxSpeed - minSpeed);

        float time = currentTime * 0.001f * speed;

        driftOffsetX += (float) (perlin.noise(time, 0, 0.05) * strength * 0.01);
        driftOffsetY += (float) (perlin.noise(0, time, 0.05) * strength * 0.005);

        driftOffsetX *= 0.98f;
        driftOffsetY *= 0.98f;

        driftOffsetX = MathHelper.clamp(driftOffsetX, -strength, strength);
        driftOffsetY = MathHelper.clamp(driftOffsetY, -strength * 0.5f, strength * 0.5f);

        return new Vector2f(driftOffsetX, driftOffsetY);
    }


    private Vector2f applyNaturalSway(AuraNew parentModule, long currentTime) {
        float minStrength = parentModule.swayStrength().getValue().getMinValue();
        float maxStrength = parentModule.swayStrength().getValue().getMaxValue();
        float strength = minStrength + random.nextFloat() * (maxStrength - minStrength);

        float minFrequency = parentModule.swayFrequency().getValue().getMinValue();
        float maxFrequency = parentModule.swayFrequency().getValue().getMaxValue();
        float frequency = minFrequency + random.nextFloat() * (maxFrequency - minFrequency);

        float time = currentTime * 0.001f * frequency;

        parentModule.swayPhase = time;

        float swayYaw = (float) (Math.sin(time * 0.7) * strength * 0.5 +
                Math.sin(time * 1.3) * strength * 0.3);
        float swayPitch = (float) (Math.cos(time * 0.5) * strength * 0.3 +
                Math.cos(time * 1.7) * strength * 0.2);

        return new Vector2f(swayYaw, swayPitch);
    }


    private Vector2f applyEyeTracking(AuraNew parentModule, long currentTime) {
        if (parentModule.target() == null) return Vector2f.ZERO;

        float minLagTime = parentModule.eyeTrackingLag().getValue().getMinValue();
        float maxLagTime = parentModule.eyeTrackingLag().getValue().getMaxValue();
        float lagTime = minLagTime + random.nextFloat() * (maxLagTime - minLagTime);

        long timeSinceAcquired = currentTime - parentModule.targetAcquiredTime;

        if (timeSinceAcquired < lagTime) {
            float progress = timeSinceAcquired / lagTime;
            float lagFactor = 1.0f - progress;
            return new Vector2f(
                    (random.nextFloat() - 0.5f) * lagFactor * 2.0f,
                    (random.nextFloat() - 0.5f) * lagFactor
            );
        }

        return Vector2f.ZERO;
    }


    private Vector2f applyLegacyRotation(AuraNew parentModule, Vector2f target, Vector2f current) {
        float minYawStep = parentModule.legacyYawStep().getValue().getMinValue();
        float maxYawStep = parentModule.legacyYawStep().getValue().getMaxValue();
        float minPitchStep = parentModule.legacyPitchStep().getValue().getMinValue();
        float maxPitchStep = parentModule.legacyPitchStep().getValue().getMaxValue();

        float yawStep = minYawStep + random.nextFloat() * (maxYawStep - minYawStep);
        float pitchStep = minPitchStep + random.nextFloat() * (maxPitchStep - minPitchStep);

        float yawDiff = MathHelper.wrapDegrees(target.x - current.x);
        float pitchDiff = target.y - current.y;

        float yawChange = MathHelper.clamp(yawDiff, -yawStep, yawStep);
        float pitchChange = MathHelper.clamp(pitchDiff, -pitchStep, pitchStep);

        if (parentModule.smoothLegacy().getValue()) {
            yawChange *= 0.7f + random.nextFloat() * 0.3f;
            pitchChange *= 0.7f + random.nextFloat() * 0.3f;
        }

        return new Vector2f(current.x + yawChange, current.y + pitchChange);
    }


    private Vector2f normalizeAngles(AuraNew parentModule, Vector2f rotation, Vector2f previousRotation) {
        float yaw = rotation.x;
        float pitch = rotation.y;

        if (parentModule.smoothAngleWrapping().getValue()) {
            float yawDiff = MathHelper.wrapDegrees(yaw - previousRotation.x);
            if (Math.abs(yawDiff) > 180) {
                yaw = previousRotation.x + yawDiff;
            }
        }

        yaw = MathHelper.wrapDegrees(yaw);
        pitch = MathHelper.clamp(pitch, -89.9f, 89.9f);

        return new Vector2f(yaw, pitch);
    }

    private Vector2f applyPixelPerfect(Vector2f rotation) {
        float sensitivity = (float) mc.gameSettings.mouseSensitivity;
        float gcd = (float) (sensitivity * 0.6 + 0.2);
        float coefficient = gcd * gcd * gcd * 1.2f;

        float yaw = rotation.x;
        float pitch = rotation.y;

        yaw = Math.round(yaw / coefficient) * coefficient;
        pitch = Math.round(pitch / coefficient) * coefficient;

        return new Vector2f(yaw, pitch);
    }


    private boolean shouldRotate(AuraNew parentModule, long currentTime) {
        long timeSinceAttack = currentTime - parentModule.lastAttackTime;

        switch (parentModule.rotationTiming().getValue()) {
            case "Постоянная":
                return true;
            case "При ударе":
                return timeSinceAttack <= 50;
            case "До и после удара":
                if (parentModule.randomRotationStart().getValue()) {
                    if (!initialRotationDirectionChosen && parentModule.perfectDelay().cooldownComplete()) {
                        float minBefore = parentModule.rotationBeforeHit().getValue().getMinValue();
                        float maxBefore = parentModule.rotationBeforeHit().getValue().getMaxValue();
                        long timeBeforeAttack = 350 - (long) (minBefore + random.nextFloat() * (maxBefore - minBefore));
                        if (timeSinceAttack >= timeBeforeAttack) {
                            return true;
                        }
                    }
                }
                float minAfter = parentModule.rotationAfterHit().getValue().getMinValue();
                float maxAfter = parentModule.rotationAfterHit().getValue().getMaxValue();
                float minBefore = parentModule.rotationBeforeHit().getValue().getMinValue();
                float maxBefore = parentModule.rotationBeforeHit().getValue().getMaxValue();

                long afterTime = (long) (minAfter + random.nextFloat() * (maxAfter - minAfter));
                long beforeTime = (long) (minBefore + random.nextFloat() * (maxBefore - minBefore));

                return timeSinceAttack <= afterTime ||
                        (parentModule.perfectDelay().cooldownComplete() && timeSinceAttack >= (350 - beforeTime));
            case "Nexus":
                if (!parentModule.perfectDelay().cooldownComplete()) {
                    return timeSinceAttack <= 100;
                }
                return timeSinceAttack >= 250 || timeSinceAttack <= 100;
            default:
                return true;
        }
    }

    private float limitYaw(AuraNew parentModule, float yaw, float prevYaw) {
        float minMaxChange = parentModule.maxYaw().getValue().getMinValue();
        float maxMaxChange = parentModule.maxYaw().getValue().getMaxValue();
        float maxChange = minMaxChange + random.nextFloat() * (maxMaxChange - minMaxChange);

        float delta = MathHelper.wrapDegrees(yaw - prevYaw);

        if (Math.abs(delta) > maxChange) {
            if (parentModule.smoothLimit().getValue()) {
                float excess = Math.abs(delta) - maxChange;
                float smoothFactor = MathHelper.clamp(excess / maxChange, 0.0f, 0.5f);
                maxChange += excess * smoothFactor;
            }
            yaw = prevYaw + Math.copySign(maxChange, delta);
        }

        return MathHelper.wrapDegrees(yaw);
    }

    private float limitPitch(AuraNew parentModule, float pitch, float prevPitch) {
        float minMaxChange = parentModule.maxPitch().getValue().getMinValue();
        float maxMaxChange = parentModule.maxPitch().getValue().getMaxValue();
        float maxChange = minMaxChange + random.nextFloat() * (maxMaxChange - minMaxChange);

        float delta = pitch - prevPitch;

        if (Math.abs(delta) > maxChange) {
            if (parentModule.smoothLimit().getValue()) {
                float excess = Math.abs(delta) - maxChange;
                float smoothFactor = MathHelper.clamp(excess / maxChange, 0.0f, 0.5f);
                maxChange += excess * smoothFactor;
            }
            pitch = prevPitch + Math.copySign(maxChange, delta);
        }

        return MathHelper.clamp(pitch, -89.9F, 89.9F);
    }

    @Override
    public void onDisable(AuraNew parentModule) {
        initialRotationDirectionChosen = false;
        startFromRight = false;
        driftOffsetX = 0f;
        driftOffsetY = 0f;
        lastMicroMovementTime = 0L;
        microMovementPhase = 0f;
        flickDirection = 1;
        currentJitterPattern = 0;
        lastJitterPatternChange = 0L;
        lastStepTime = 0L;
        stepTarget = Vector2f.ZERO;
        isLookingUpNow = false;
        lookUpStartTimeNow = 0;
        lookUpDurationNow = 0;
        nextLookUpDelayNow = 0;
        jaggedPhase = 0f;
        lastJaggedYaw = 0f;
        lastJaggedPitch = 0f;
        lastJitterPattern = -1;
        jitterPhase = 0f;
        jitterRadius = 0f;
        jitterAngle = 0f;
        randomShape = 0;
        triangleProgress = 0f;
        cubicTime = 0f;
        isDeflecting = false;
        deflectionStartTime = 0L;
        deflectionTarget = Vector2f.ZERO;
        deflectionStart = Vector2f.ZERO;
        spin360Progress = 0f;
        isSpin360 = false;
        spin360StartYaw = 0f;
    }

    private Vector2f applyDisableRotationReturn(AuraNew parentModule, Vector2f currentLerpRotation, long currentTime) {
        // Возвращаем взгляд в исходное состояние (0, 0) при выключении ротации
        float targetYaw = 0f;
        float targetPitch = 0f;

        float yawDiff = MathHelper.wrapDegrees(targetYaw - currentLerpRotation.x);
        float pitchDiff = targetPitch - currentLerpRotation.y;

        // Получаем скорость возврата
        float minSpeed = parentModule.disableRotationReturnSpeed().getValue().getMinValue();
        float maxSpeed = parentModule.disableRotationReturnSpeed().getValue().getMaxValue();
        float returnSpeed = minSpeed + random.nextFloat() * (maxSpeed - minSpeed);

        // Нормализуем скорость (360 = полный оборот за 1 сек)
        float speedFactor = returnSpeed / 360f;

        // Получаем плавность возврата
        float minSmoothness = parentModule.disableRotationReturnSmoothness().getValue().getMinValue();
        float maxSmoothness = parentModule.disableRotationReturnSmoothness().getValue().getMaxValue();
        float smoothness = minSmoothness + random.nextFloat() * (maxSmoothness - minSmoothness);

        // Интерполируем с плавностью
        float newYaw = MathHelper.lerp(smoothness, currentLerpRotation.x, currentLerpRotation.x + yawDiff * speedFactor);
        float newPitch = MathHelper.lerp(smoothness, currentLerpRotation.y, currentLerpRotation.y + pitchDiff * speedFactor);

        return new Vector2f(newYaw, newPitch);
    }

    private Vector2f applyPullbackAfterHit(AuraNew parentModule, Vector2f smoothed, Vector2f currentLerpRotation, long currentTime) {
        if (parentModule.lastAttackTime != lastAttackTimeForPullback && parentModule.lastAttackTime > 0) {
            lastAttackTimeForPullback = parentModule.lastAttackTime;
            isPullingBack = true;
            pullbackStartTime = currentTime;
            pullbackStart = new Vector2f(currentLerpRotation.x, currentLerpRotation.y);

            float yawOffset = 0f;
            float pitchOffset = 0f;

            if (parentModule.pullbackYawEnabled().getValue()) {
                float minYaw = parentModule.pullbackYawDegrees().getValue().getMinValue();
                float maxYaw = parentModule.pullbackYawDegrees().getValue().getMaxValue();
                yawOffset = minYaw + random.nextFloat() * (maxYaw - minYaw);
            }

            if (parentModule.pullbackPitchEnabled().getValue()) {
                float minPitch = parentModule.pullbackPitchDegrees().getValue().getMinValue();
                float maxPitch = parentModule.pullbackPitchDegrees().getValue().getMaxValue();
                pitchOffset = minPitch + random.nextFloat() * (maxPitch - minPitch);
            }

            String direction = parentModule.pullbackDirection().getValue();
            switch (direction) {
                case "Случайная":
                    int randomDir = random.nextInt(8);
                    switch (randomDir) {
                        case 0:
                            yawOffset = -Math.abs(yawOffset);
                            break;
                        case 1:
                            yawOffset = Math.abs(yawOffset);
                            break;
                        case 2:
                            pitchOffset = -Math.abs(pitchOffset);
                            break;
                        case 3:
                            pitchOffset = Math.abs(pitchOffset);
                            break;
                        case 4:
                            yawOffset = -Math.abs(yawOffset);
                            pitchOffset = -Math.abs(pitchOffset);
                            break;
                        case 5:
                            yawOffset = Math.abs(yawOffset);
                            pitchOffset = -Math.abs(pitchOffset);
                            break;
                        case 6:
                            yawOffset = -Math.abs(yawOffset);
                            pitchOffset = Math.abs(pitchOffset);
                            break;
                        case 7:
                            yawOffset = Math.abs(yawOffset);
                            pitchOffset = Math.abs(pitchOffset);
                            break;
                    }
                    break;
                case "Влево":
                    yawOffset = -Math.abs(yawOffset);
                    pitchOffset = 0;
                    break;
                case "Вправо":
                    yawOffset = Math.abs(yawOffset);
                    pitchOffset = 0;
                    break;
                case "Вверх":
                    yawOffset = 0;
                    pitchOffset = -Math.abs(pitchOffset);
                    break;
                case "Вниз":
                    yawOffset = 0;
                    pitchOffset = Math.abs(pitchOffset);
                    break;
                case "Влево-Вверх":
                    yawOffset = -Math.abs(yawOffset);
                    pitchOffset = -Math.abs(pitchOffset);
                    break;
                case "Вправо-Вверх":
                    yawOffset = Math.abs(yawOffset);
                    pitchOffset = -Math.abs(pitchOffset);
                    break;
                case "Влево-Вниз":
                    yawOffset = -Math.abs(yawOffset);
                    pitchOffset = Math.abs(pitchOffset);
                    break;
                case "Вправо-Вниз":
                    yawOffset = Math.abs(yawOffset);
                    pitchOffset = Math.abs(pitchOffset);
                    break;
            }

            pullbackTarget = new Vector2f(
                    pullbackStart.x + yawOffset,
                    MathHelper.clamp(pullbackStart.y + pitchOffset, -90f, 90f)
            );
        }

        if (isPullingBack) {
            long elapsed = currentTime - pullbackStartTime;
            long pullbackDuration = 150;

            if (elapsed >= pullbackDuration) {
                isPullingBack = false;
                return smoothed;
            }

            float progress = (float) elapsed / pullbackDuration;
            float easedProgress = (float) (1.0 - Math.pow(1.0 - progress, 3));

            float minSpeed = parentModule.pullbackSpeedMultiplier().getValue().getMinValue();
            float maxSpeed = parentModule.pullbackSpeedMultiplier().getValue().getMaxValue();
            float speedMultiplier = minSpeed + random.nextFloat() * (maxSpeed - minSpeed);

            easedProgress *= speedMultiplier;
            easedProgress = MathHelper.clamp(easedProgress, 0f, 1f);

            float yawDelta = MathHelper.wrapDegrees(pullbackTarget.x - pullbackStart.x);
            float pitchDelta = pullbackTarget.y - pullbackStart.y;

            float currentYaw = pullbackStart.x + yawDelta * easedProgress;
            float currentPitch = pullbackStart.y + pitchDelta * easedProgress;

            return new Vector2f(currentYaw, currentPitch);
        }

        return smoothed;
    }
}