package org.privates.client.managers.module.impl.combat;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import org.privates.client.managers.component.impl.rotation.Rotation;
import org.privates.client.managers.component.impl.rotation.RotationComponent;
import org.privates.client.managers.module.impl.player.FakeLag;
import org.privates.client.utils.other.GCDUtils;
import org.privates.lib.util.time.StopWatch;

import java.util.concurrent.ThreadLocalRandom;

public class ElytraAuraLogic {

    private static double speed = 0.0;
    private static long lastSpeedUpdate = 0;
    private static Vector3d smoothedVelocity = Vector3d.ZERO;
    private static Vector3d lastPredictPoint = null;

    private static final double MOVING_THRESHOLD = 0.05;
    private static final double VELOCITY_SMOOTHING = 0.3;

    private static boolean smartModeActive = false;
    private static boolean isRetreatActive = false;
    private static long retreatStartTime = 0;
    private static long retreatDuration = 0;
    private static Vector2f retreatRotation = Vector2f.ZERO;
    private static Vector2f returnStartRotation = Vector2f.ZERO;
    private static boolean hasPassedPredict = false;
    private static Vector3d lastPlayerPos = Vector3d.ZERO;
    private static Vector3d targetVelocityHistory = Vector3d.ZERO;
    private static int velocityTicks = 0;
    private static final StopWatch retreatTimer = new StopWatch();

    private static long lastTargetHitTime = 0;
    private static float lastTargetHealth = 0;
    private static boolean targetWasHit = false;

    public static Vector3d visualRetreatPoint = null;
    public static Vector3d visualPredictPoint = null;
    public static Vector3d visualReturnPoint = null;

    public static void updateElytraRotation(AuraNew aura, LivingEntity target, Minecraft mc, Vector2f currentRotation, String targetMode) {
        if (target == null || mc.player == null) return;

        updateTargetSpeed(target);
        updateTargetResolver(target);
        checkTargetHit(target, aura);

        float predictDistance = aura.elytraCustom().getValue() ? aura.elytraCustomPredictDistance().getValue() : aura.getElytraPredictDistance();
        if (predictDistance <= 0) {
            predictDistance = 4.7f;
        }

        if (targetMode.equals("Кастомизированный")) {
            handleCustomMode(aura, target, predictDistance, mc, currentRotation);
        } else {
            handleNormalMode(aura, target, predictDistance, mc, currentRotation);
        }
    }

    private static void checkTargetHit(LivingEntity target, AuraNew aura) {
        if (target == null) return;

        float currentHealth = target.getHealth();
        long currentTime = System.currentTimeMillis();

        if (currentHealth < lastTargetHealth && currentTime - aura.lastAttackTime < 500) {
            targetWasHit = true;
            lastTargetHitTime = currentTime;
        } else if (currentTime - lastTargetHitTime > 1000) {
            targetWasHit = false;
        }

        lastTargetHealth = currentHealth;
    }

    private static void handleNormalMode(AuraNew aura, LivingEntity target, float predictDistance, Minecraft mc, Vector2f currentRotation) {
        smartModeActive = false;
        isRetreatActive = false;
        hasPassedPredict = false;

        Vector3d aimPoint;
        if (aura.adaptivePredict().getValue()) {
            aimPoint = calculateAdaptivePredictPoint(target, predictDistance, mc, aura, false);
        } else {
            aimPoint = calculateStaticPredictPoint(target, predictDistance, mc, false);
        }

        lastPredictPoint = aimPoint;
        visualPredictPoint = aimPoint;

        Vector3d playerEyes = mc.player.getEyePosition(1.0F);
        Vector3d playerPos = mc.player.getPositionVec();
        Vector3d targetPos = target.getPositionVec();

        double distanceToTarget = playerPos.distanceTo(targetPos);
        double distanceToPredict = playerEyes.distanceTo(aimPoint);

        Vector3d direction = aimPoint.subtract(playerEyes);
        float targetYaw = (float) Math.toDegrees(Math.atan2(direction.z, direction.x)) - 90.0f;
        float targetPitch = (float) -Math.toDegrees(Math.atan2(direction.y, Math.sqrt(direction.x * direction.x + direction.z * direction.z)));

        float currentYaw = mc.player.rotationYaw;
        float yawDifference = Math.abs(MathHelper.wrapDegrees(targetYaw - currentYaw));

        boolean isBehind = yawDifference > 90.0f;
        boolean shouldRotate = !isBehind || (distanceToPredict > predictDistance + 0.3);

        if (shouldRotate) {
            float yaw = GCDUtils.getSensitivity(targetYaw);
            float pitch = GCDUtils.getSensitivity(MathHelper.clamp(targetPitch, -90.0f, 90.0f));
            RotationComponent.update(new Rotation(yaw, pitch), 360, 180, 0, 5);

            aura.setLerpRotation(new Vector2f(yaw, pitch));
        }
    }

    private static void handleCustomMode(AuraNew aura, LivingEntity target, float predictDistance, Minecraft mc, Vector2f currentRotation) {
        smartModeActive = true;

        boolean shouldRetreatOnHit = aura.elytraCustomRetreatOnHit().getValue();
        boolean useHealthCheck = aura.elytraCustomRetreatHealthCheck().getValue();
        boolean onlyIfTargetOnGround = aura.elytraCustomRetreatOnlyGrounded().getValue();
        boolean onlyIfTargetNoElytra = aura.elytraCustomRetreatOnlyNoElytra().getValue();

        Vector3d aimPoint;
        if (aura.elytraCustomAdaptivePredict().getValue()) {
            aimPoint = calculateAdaptivePredictPoint(target, predictDistance, mc, aura, true);
        } else {
            aimPoint = calculateStaticPredictPoint(target, predictDistance, mc, true);
        }

        lastPredictPoint = aimPoint;
        visualPredictPoint = aimPoint;

        Vector3d playerPos = mc.player.getPositionVec();
        Vector3d playerEyes = mc.player.getEyePosition(1.0F);

        if (!hasPassedPredict && aura.elytraCustomDetectPass().getValue()) {
            Vector3d toPredict = aimPoint.subtract(playerPos);
            Vector3d playerVelocity = mc.player.getMotion();

            if (playerVelocity.length() > 0.01) {
                double dotProduct = toPredict.normalize().dotProduct(playerVelocity.normalize());

                if (dotProduct < aura.elytraCustomPassThreshold().getValue()) {
                    hasPassedPredict = true;
                }
            }
        }

        if (shouldRetreatOnHit && !isRetreatActive) {
            boolean shouldStartRetreat = false;

            if (useHealthCheck) {
                shouldStartRetreat = targetWasHit && (System.currentTimeMillis() - lastTargetHitTime < 200);
            } else {
                shouldStartRetreat = aura.lastAttackTime > 0 && System.currentTimeMillis() - aura.lastAttackTime < 100;
            }

            if (shouldStartRetreat) {
                boolean canRetreat = true;

                if (onlyIfTargetOnGround && canRetreat) {
                    canRetreat = target.isOnGround();
                }

                if (onlyIfTargetNoElytra && canRetreat) {
                    canRetreat = !target.isElytraFlying();
                }

                if (canRetreat) {
                    startCustomRetreat(aura, mc, currentRotation);
                }
            }
        }

        if (isRetreatActive) {
            handleCustomRetreat(aura, mc, currentRotation, aimPoint);
        } else {
            Vector3d direction = aimPoint.subtract(playerEyes);
            float targetYaw = (float) Math.toDegrees(Math.atan2(direction.z, direction.x)) - 90.0f;
            float targetPitch = (float) -Math.toDegrees(Math.atan2(direction.y, Math.sqrt(direction.x * direction.x + direction.z * direction.z)));

            float currentYaw = mc.player.rotationYaw;
            float yawDifference = Math.abs(MathHelper.wrapDegrees(targetYaw - currentYaw));

            boolean isBehind = yawDifference > 90.0f;

            if (!hasPassedPredict || !aura.elytraCustomDetectPass().getValue()) {
                double distanceToPredict = playerEyes.distanceTo(aimPoint);
                boolean shouldRotate = !isBehind || (distanceToPredict > predictDistance + 0.3);

                if (shouldRotate) {
                    float yawSpeed = aura.elytraCustomYawSpeed().getValue();
                    float pitchSpeed = aura.elytraCustomPitchSpeed().getValue();

                    float yaw = GCDUtils.getSensitivity(targetYaw);
                    float pitch = GCDUtils.getSensitivity(MathHelper.clamp(targetPitch, -90.0f, 90.0f));
                    RotationComponent.update(new Rotation(yaw, pitch), yawSpeed, pitchSpeed, 0, 5);

                    aura.setLerpRotation(new Vector2f(yaw, pitch));
                }
            } else {
                float yawSpeed = aura.elytraCustomYawSpeed().getValue();
                float pitchSpeed = aura.elytraCustomPitchSpeed().getValue();

                float yaw = GCDUtils.getSensitivity(targetYaw);
                float pitch = GCDUtils.getSensitivity(MathHelper.clamp(targetPitch, -90.0f, 90.0f));
                RotationComponent.update(new Rotation(yaw, pitch), yawSpeed, pitchSpeed, 0, 5);

                aura.setLerpRotation(new Vector2f(yaw, pitch));
            }
        }
    }

    private static void startCustomRetreat(AuraNew aura, Minecraft mc, Vector2f currentRotation) {
        isRetreatActive = true;
        retreatStartTime = System.currentTimeMillis();
        retreatDuration = ThreadLocalRandom.current().nextLong(
                aura.elytraCustomRetreatMinTime().getValue().longValue(),
                aura.elytraCustomRetreatMaxTime().getValue().longValue()
        );

        float minYaw = aura.elytraCustomRetreatYawMin().getValue();
        float maxYaw = aura.elytraCustomRetreatYawMax().getValue();
        float minPitch = aura.elytraCustomRetreatPitchMin().getValue();
        float maxPitch = aura.elytraCustomRetreatPitchMax().getValue();

        float randomYawOffset = ThreadLocalRandom.current().nextFloat(minYaw, maxYaw);
        float randomPitchOffset = ThreadLocalRandom.current().nextFloat(minPitch, maxPitch);

        float targetYaw = currentRotation.x + randomYawOffset;
        float targetPitch = MathHelper.clamp(currentRotation.y + randomPitchOffset, -90f, 90f);

        retreatRotation = new Vector2f(targetYaw, targetPitch);
        returnStartRotation = currentRotation;

        visualRetreatPoint = calculateVisualRetreatPoint(mc, retreatRotation);

        if (aura.elytraCustomUseFakeLag().getValue()) {
            FakeLag fakeLag = FakeLag.getInstance();
            if (fakeLag != null && !fakeLag.isEnabled()) {
                fakeLag.toggle();
            }
        }

        retreatTimer.reset();
    }

    private static void handleCustomRetreat(AuraNew aura, Minecraft mc, Vector2f currentRotation, Vector3d aimPoint) {
        long elapsed = System.currentTimeMillis() - retreatStartTime;

        if (elapsed < retreatDuration) {
            float progress = (float) elapsed / (float) retreatDuration;

            String interpolation = aura.elytraCustomRetreatInterpolation().getValue();
            switch (interpolation) {
                case "Линейная":
                    break;
                case "Плавная":
                    progress = (float) Math.sin(progress * Math.PI / 2);
                    break;
                case "Экспоненциальная":
                    progress = (float) Math.pow(progress, 2);
                    break;
                case "Синусоидальная":
                    progress = (float) (1 - Math.cos(progress * Math.PI / 2));
                    break;
            }

            float yaw = MathHelper.lerp(progress, returnStartRotation.x, retreatRotation.x);
            float pitch = MathHelper.lerp(progress, returnStartRotation.y, retreatRotation.y);

            yaw = GCDUtils.getSensitivity(yaw);
            pitch = GCDUtils.getSensitivity(MathHelper.clamp(pitch, -90.0f, 90.0f));

            float retreatYawSpeed = aura.elytraCustomRetreatYawSpeed().getValue();
            float retreatPitchSpeed = aura.elytraCustomRetreatPitchSpeed().getValue();

            RotationComponent.update(new Rotation(yaw, pitch), retreatYawSpeed, retreatPitchSpeed, 0, 5);
            aura.setLerpRotation(new Vector2f(yaw, pitch));

        } else {
            long totalDuration = retreatDuration;

            if (aura.elytraCustomUseFakeLag().getValue()) {
                long fakeLagDuration = aura.elytraCustomFakeLagDuration().getValue().longValue();
                totalDuration += fakeLagDuration;

                if (elapsed >= retreatDuration && elapsed < totalDuration) {
                    FakeLag fakeLag = FakeLag.getInstance();
                    if (fakeLag != null && !fakeLag.isEnabled()) {
                        fakeLag.toggle();
                    }

                    float yaw = GCDUtils.getSensitivity(retreatRotation.x);
                    float pitch = GCDUtils.getSensitivity(MathHelper.clamp(retreatRotation.y, -90.0f, 90.0f));

                    float retreatYawSpeed = aura.elytraCustomRetreatYawSpeed().getValue();
                    float retreatPitchSpeed = aura.elytraCustomRetreatPitchSpeed().getValue();

                    RotationComponent.update(new Rotation(yaw, pitch), retreatYawSpeed, retreatPitchSpeed, 0, 5);
                    aura.setLerpRotation(new Vector2f(yaw, pitch));
                    return;
                }

                if (elapsed >= totalDuration) {
                    FakeLag fakeLag = FakeLag.getInstance();
                    if (fakeLag != null && fakeLag.isEnabled()) {
                        fakeLag.toggle();
                    }
                }
            }

            if (elapsed >= totalDuration) {
                visualReturnPoint = aimPoint;

                Vector3d playerEyes = mc.player.getEyePosition(1.0F);
                Vector3d direction = aimPoint.subtract(playerEyes);
                float targetYaw = (float) Math.toDegrees(Math.atan2(direction.z, direction.x)) - 90.0f;
                float targetPitch = (float) -Math.toDegrees(Math.atan2(direction.y, Math.sqrt(direction.x * direction.x + direction.z * direction.z)));

                float yaw = GCDUtils.getSensitivity(targetYaw);
                float pitch = GCDUtils.getSensitivity(MathHelper.clamp(targetPitch, -90.0f, 90.0f));

                float returnYawSpeed = aura.elytraCustomReturnYawSpeed().getValue();
                float returnPitchSpeed = aura.elytraCustomReturnPitchSpeed().getValue();

                RotationComponent.update(new Rotation(yaw, pitch), returnYawSpeed, returnPitchSpeed, 0, 5);
                aura.setLerpRotation(new Vector2f(yaw, pitch));

                isRetreatActive = false;
                retreatStartTime = 0;
                retreatDuration = 0;
                visualRetreatPoint = null;
                visualReturnPoint = null;
            }
        }
    }

    private static Vector3d calculateVisualRetreatPoint(Minecraft mc, Vector2f rotation) {
        Vector3d playerPos = mc.player.getPositionVec();
        double distance = 5.0;

        double yawRad = Math.toRadians(rotation.x + 90);
        double pitchRad = Math.toRadians(rotation.y);

        double x = -Math.sin(yawRad) * Math.cos(pitchRad);
        double y = -Math.sin(pitchRad);
        double z = Math.cos(yawRad) * Math.cos(pitchRad);

        return playerPos.add(x * distance, y * distance, z * distance);
    }

    private static void updateTargetSpeed(LivingEntity target) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastSpeedUpdate >= 50) {
            double dx = target.getPosX() - target.prevPosX;
            double dy = target.getPosY() - target.prevPosY;
            double dz = target.getPosZ() - target.prevPosZ;

            Vector3d currentVelocity = new Vector3d(dx, dy, dz);
            smoothedVelocity = smoothedVelocity.scale(1.0 - VELOCITY_SMOOTHING)
                    .add(currentVelocity.scale(VELOCITY_SMOOTHING));

            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
            speed = distance * 20.0;
            lastSpeedUpdate = currentTime;
        }
    }

    private static void updateTargetResolver(LivingEntity target) {
        Vector3d currentVelocity = new Vector3d(
                target.getPosX() - target.prevPosX,
                target.getPosY() - target.prevPosY,
                target.getPosZ() - target.prevPosZ
        );

        if (velocityTicks < 20) {
            targetVelocityHistory = targetVelocityHistory.add(currentVelocity);
            velocityTicks++;
        } else {
            targetVelocityHistory = targetVelocityHistory.scale(0.95).add(currentVelocity.scale(0.05));
        }
    }

    private static Vector3d calculateAdaptivePredictPoint(LivingEntity target, float maxPredictDistance, Minecraft mc, AuraNew aura, boolean useCustom) {
        Vector3d targetPos = target.getPositionVec();
        Vector3d targetCenter = targetPos.add(0, target.getHeight() / 2.0, 0);

        double velocityMagnitude = smoothedVelocity.length() * 20.0;

        double movingThreshold = useCustom ? aura.elytraCustomMovingThreshold().getValue() : MOVING_THRESHOLD;

        if (velocityMagnitude < movingThreshold) {
            return targetCenter;
        }

        double speedFactor = MathHelper.clamp(velocityMagnitude / 30.0, 0.0, 1.0);
        float adaptiveDistance = maxPredictDistance * (float) speedFactor;

        if (adaptiveDistance < 1.0f && velocityMagnitude > movingThreshold) {
            adaptiveDistance = maxPredictDistance * 0.5f;
        }

        double accelerationX = (target.getPosX() - target.prevPosX) - (target.prevPosX - target.lastTickPosX);
        double accelerationY = (target.getPosY() - target.prevPosY) - (target.prevPosY - target.lastTickPosY);
        double accelerationZ = (target.getPosZ() - target.prevPosZ) - (target.prevPosZ - target.lastTickPosZ);
        Vector3d acceleration = new Vector3d(accelerationX, accelerationY, accelerationZ);

        double ticksAhead = adaptiveDistance / Math.max(velocityMagnitude / 20.0, 0.1);
        ticksAhead = MathHelper.clamp(ticksAhead, 1.0, 20.0);

        Vector3d velocityComponent = smoothedVelocity.scale(ticksAhead);

        double accelMultiplier = useCustom ? aura.elytraCustomAccelerationFactor().getValue() : 0.5;
        Vector3d accelerationComponent = acceleration.scale(accelMultiplier * ticksAhead * ticksAhead);

        Vector3d predictOffset = velocityComponent.add(accelerationComponent);

        Vector3d playerPos = mc.player.getPositionVec();

        if (isPlayerChasingTarget(playerPos, targetCenter, smoothedVelocity, mc, useCustom ? aura : null)) {
            double chaseMultiplier = useCustom ? aura.elytraCustomChaseMultiplier().getValue() : 1.3;
            predictOffset = predictOffset.scale(chaseMultiplier);

            Vector3d perpendicular = calculatePerpendicularOffset(smoothedVelocity);
            double perpMultiplier = useCustom ? aura.elytraCustomPerpendicularOffset().getValue() : 1.5;
            predictOffset = predictOffset.add(perpendicular.scale(perpMultiplier));
        }

        double maxOffsetMultiplier = useCustom ? aura.elytraCustomMaxOffsetMultiplier().getValue() : 1.5;
        double predictMagnitude = predictOffset.length();
        if (predictMagnitude > adaptiveDistance * maxOffsetMultiplier) {
            predictOffset = predictOffset.normalize().scale(adaptiveDistance * maxOffsetMultiplier);
        }

        return targetCenter.add(predictOffset);
    }

    private static Vector3d calculateStaticPredictPoint(LivingEntity target, float predictDistance, Minecraft mc, boolean useCustom) {
        Vector3d targetPos = target.getPositionVec();
        Vector3d targetCenter = targetPos.add(0, target.getHeight() / 2.0, 0);

        Vector3d targetVelocity = new Vector3d(
                target.getPosX() - target.prevPosX,
                target.getPosY() - target.prevPosY,
                target.getPosZ() - target.prevPosZ
        );

        double velocityMagnitude = targetVelocity.length();

        if (velocityMagnitude > MOVING_THRESHOLD) {
            Vector3d normalizedVelocity = targetVelocity.normalize();
            Vector3d predictOffset = normalizedVelocity.scale(predictDistance);

            Vector3d playerPos = mc.player.getPositionVec();
            if (isPlayerChasingTarget(playerPos, targetCenter, targetVelocity, mc, null)) {
                predictOffset = predictOffset.scale(1.3);

                Vector3d perpendicular = calculatePerpendicularOffset(targetVelocity);
                predictOffset = predictOffset.add(perpendicular.scale(1.5));
            }

            return targetCenter.add(predictOffset);
        } else {
            Vector3d lookVec = target.getLookVec().normalize();
            return targetCenter.add(lookVec.scale(predictDistance * 0.3));
        }
    }

    private static boolean isPlayerChasingTarget(Vector3d playerPos, Vector3d targetPos, Vector3d targetVelocity, Minecraft mc, AuraNew aura) {
        if (targetVelocity.length() < MOVING_THRESHOLD) {
            return false;
        }

        Vector3d playerToTarget = targetPos.subtract(playerPos).normalize();
        Vector3d targetDirection = targetVelocity.normalize();

        Vector3d playerVelocity = mc.player.getMotion();
        if (playerVelocity.length() < MOVING_THRESHOLD) {
            return false;
        }
        Vector3d playerDirection = playerVelocity.normalize();

        double playerToTargetThreshold = aura != null ? aura.elytraCustomPlayerToTargetThreshold().getValue() : 0.7;
        double chaseDirectionThreshold = aura != null ? aura.elytraCustomChaseDirectionThreshold().getValue() : 0.7;

        double dotPlayerToTarget = playerDirection.dotProduct(playerToTarget);
        double dotPlayerToTargetVelocity = playerDirection.dotProduct(targetDirection);

        return dotPlayerToTarget > playerToTargetThreshold && dotPlayerToTargetVelocity > chaseDirectionThreshold;
    }

    private static Vector3d calculatePerpendicularOffset(Vector3d velocity) {
        if (velocity.length() < MOVING_THRESHOLD) {
            return Vector3d.ZERO;
        }

        Vector3d horizontal = new Vector3d(velocity.x, 0, velocity.z);
        if (horizontal.length() < 0.01) {
            return Vector3d.ZERO;
        }

        Vector3d perpendicular = new Vector3d(-horizontal.z, 0, horizontal.x).normalize();
        return perpendicular;
    }

    public static Vector3d getPredictPoint(LivingEntity target, float predictDistance, boolean adaptive, Minecraft mc, AuraNew aura) {
        if (target == null) return null;
        updateTargetSpeed(target);
        updateTargetResolver(target);
        float dist = predictDistance > 0 ? predictDistance : 4.7f;
        return adaptive ? calculateAdaptivePredictPoint(target, dist, mc, aura, false) : calculateStaticPredictPoint(target, dist, mc, false);
    }

    public static Vector3d getLastPredictPoint() {
        return lastPredictPoint;
    }

    public static double getCurrentTargetSpeed() {
        return speed;
    }

    public static boolean isTargetMoving() {
        return speed > MOVING_THRESHOLD;
    }

    public static float getDefaultPredictDistance() {
        return 4.7f;
    }

    public static void reset() {
        speed = 0.0;
        lastSpeedUpdate = 0;
        smoothedVelocity = Vector3d.ZERO;
        lastPredictPoint = null;
        smartModeActive = false;
        isRetreatActive = false;
        retreatStartTime = 0;
        retreatDuration = 0;
        retreatRotation = Vector2f.ZERO;
        returnStartRotation = Vector2f.ZERO;
        hasPassedPredict = false;
        lastPlayerPos = Vector3d.ZERO;
        targetVelocityHistory = Vector3d.ZERO;
        velocityTicks = 0;
        visualRetreatPoint = null;
        visualPredictPoint = null;
        visualReturnPoint = null;
        lastTargetHitTime = 0;
        lastTargetHealth = 0;
        targetWasHit = false;
    }
}