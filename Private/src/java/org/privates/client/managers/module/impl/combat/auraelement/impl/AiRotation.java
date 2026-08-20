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

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;


public class AiRotation extends AbstractRotationElement {

    private final AiRotationConfig config;
    private Vector3d currentAimPoint = null;
    private long lastUpdateTime = 0;
    private float accumulatedYawMomentum = 0f;
    private float accumulatedPitchMomentum = 0f;

    private String currentJitterType;
    private float currentYawSpeed, currentPitchSpeed, currentMovementAggressiveness, currentMovementStrength;
    private float currentMomentum, currentJitterStrength;
    private int currentAimPointIndex;
    private boolean currentDidOvershoot;

    private LivingEntity lastTarget = null;
    private Vector3d lastTargetPos = null;
    private boolean shouldRecordHit = false;
    private long lastAttackRecordTime = 0;

    public AiRotation() {
        this.config = new AiRotationConfig();
        this.config.load();
    }

    @Override
    public Vector2f updateRotation(AuraNew parentModule, LivingEntity target, Minecraft mc, Vector2f currentLerpRotation) {
        if (target == null || mc.player == null) return currentLerpRotation;

        long currentTime = System.currentTimeMillis();
        if (lastUpdateTime == 0) lastUpdateTime = currentTime;
        float deltaTime = (currentTime - lastUpdateTime) / 1000.0f;
        lastUpdateTime = currentTime;

        if (target != lastTarget) {
            lastTarget = target;
            shouldRecordHit = false;
        }

        float distance = mc.player.getDistance(target);
        String contextKey = getContextKey(distance, target);
        AiRotationConfig.ContextualProfile profile = config.getProfile().contextualProfiles.computeIfAbsent(contextKey, AiRotationConfig.ContextualProfile::new);

        initializeTickParameters(profile);

        Vector3d targetPoint = getAdaptiveAimPoint(target, profile);
        if (currentAimPoint == null) currentAimPoint = target.getEyePosition(1.0f);
        currentAimPoint = currentAimPoint.lerp(targetPoint, 0.25f);

        Vector3d playerEyes = mc.player.getEyePosition(1.0f);
        Vector3d direction = currentAimPoint.subtract(playerEyes);

        float targetYaw = (float) Math.toDegrees(Math.atan2(direction.z, direction.x)) - 90.0f;
        float targetPitch = (float) -Math.toDegrees(Math.atan2(direction.y, Math.sqrt(direction.x * direction.x + direction.z * direction.z)));

        if (ThreadLocalRandom.current().nextFloat() < profile.overshootChance) {
            targetYaw += (ThreadLocalRandom.current().nextFloat() - 0.5f) * 2f * profile.overshootStrength;
            targetPitch += (ThreadLocalRandom.current().nextFloat() - 0.5f) * profile.overshootStrength;
            currentDidOvershoot = true;
        } else {
            currentDidOvershoot = false;
        }

        Vector2f jitterOffset = getJitterOffset(currentTime);
        targetYaw += jitterOffset.x;
        targetPitch += jitterOffset.y;

        float yawDiff = MathHelper.wrapDegrees(targetYaw - currentLerpRotation.x);
        float pitchDiff = targetPitch - currentLerpRotation.y;

        accumulatedYawMomentum = MathHelper.lerp(currentMomentum, accumulatedYawMomentum, yawDiff * 0.15f);
        accumulatedPitchMomentum = MathHelper.lerp(currentMomentum, accumulatedPitchMomentum, pitchDiff * 0.12f);
        yawDiff += accumulatedYawMomentum;
        pitchDiff += accumulatedPitchMomentum;

        float yawStep = calculateMovementStep(yawDiff);
        float pitchStep = calculateMovementStep(pitchDiff);

        float maxYawStep = currentYawSpeed * deltaTime;
        float maxPitchStep = currentPitchSpeed * deltaTime;
        yawStep = MathHelper.clamp(yawStep, -maxYawStep, maxYawStep);
        pitchStep = MathHelper.clamp(pitchStep, -maxPitchStep, maxPitchStep);

        float newYaw = currentLerpRotation.x + yawStep;
        float newPitch = MathHelper.clamp(currentLerpRotation.y + pitchStep, -89.9f, 89.9f);

        newYaw = GCDUtils.getSensitivity(newYaw);
        newPitch = GCDUtils.getSensitivity(newPitch);
        Vector2f result = new Vector2f(newYaw, newPitch);

        float targetVelocity = 0f;
        if (lastTargetPos != null && deltaTime > 0) {
            targetVelocity = (float) (target.getPositionVec().distanceTo(lastTargetPos) / deltaTime);
        }
        lastTargetPos = target.getPositionVec();

        recordRotationSample(distance, Math.abs(yawDiff) + Math.abs(pitchDiff), targetVelocity);

        applyRotation(new Rotation(newYaw, newPitch), currentYawSpeed, currentPitchSpeed, 0, 5);
        parentModule.setLerpRotation(result);

        return result;
    }

    private String getContextKey(float distance, LivingEntity target) {
        String range = distance < 3.5f ? "CLOSE" : (distance < 6.0f ? "MID" : "FAR");
        double vel = target.getMotion().length() * 20;
        String speed = vel < 1.0 ? "STATIC" : (vel < 5.0 ? "MOVING" : "SPRINTING");
        return range + "_" + speed;
    }

    private void initializeTickParameters(AiRotationConfig.ContextualProfile profile) {
        currentYawSpeed = randomize(profile.yawSpeedMean, profile.yawSpeedVariance);
        currentPitchSpeed = randomize(profile.pitchSpeedMean, profile.pitchSpeedVariance);
        currentMovementAggressiveness = randomize(profile.movementAggressivenessMean, profile.movementAggressivenessVariance);
        currentMovementStrength = randomize(profile.movementStrengthMean, profile.movementStrengthVariance);
        currentMomentum = randomize(profile.momentumMean, profile.momentumVariance);
        currentJitterStrength = randomize(profile.jitterStrengthMean, profile.jitterStrengthVariance);
        currentJitterType = selectWeightedRandom(profile.jitterPatternWeights);
    }

    private Vector3d getAdaptiveAimPoint(LivingEntity target, AiRotationConfig.ContextualProfile profile) {
        currentAimPointIndex = Integer.parseInt(selectWeightedRandom(profile.aimPointWeights.entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey().toString(), Map.Entry::getValue))));

        double heightPercent;
        switch (currentAimPointIndex) {
            case 0:
                heightPercent = 0.92;
                break;
            case 1:
                heightPercent = 0.65;
                break;
            case 2:
                heightPercent = 0.25;
                break;
            default:
                heightPercent = 0.65;
        }
        return target.getPositionVec().add(0, target.getHeight() * heightPercent, 0);
    }

    private float calculateMovementStep(float difference) {
        float normalizedDifference = Math.signum(difference) * MathHelper.clamp(Math.abs(difference) / 180f, 0, 1);
        float easedProgress = (float) Math.pow(Math.abs(normalizedDifference), 1.0f / (1.0f + currentMovementAggressiveness * 2.0f));
        return difference * easedProgress * MathHelper.clamp(currentMovementStrength, 0.1f, 1.0f);
    }

    private Vector2f getJitterOffset(long currentTime) {
        float time = currentTime * 0.001f * 5.0f;
        float yaw = 0f, pitch = 0f;

        switch (currentJitterType) {
            case "SINE":
                yaw = (float) Math.sin(time * 1.5) * currentJitterStrength;
                pitch = (float) Math.cos(time * 1.5) * currentJitterStrength * 0.6f;
                break;
            case "PERLIN":
                yaw = ((float) perlinNoise(time, 10.0) - 0.5f) * 2f * currentJitterStrength;
                pitch = ((float) perlinNoise(time, 20.0) - 0.5f) * currentJitterStrength;
                break;
            case "RANDOM_SPIKE":
                if (ThreadLocalRandom.current().nextInt(20) == 0) {
                    yaw = (ThreadLocalRandom.current().nextFloat() - 0.5f) * 4f * currentJitterStrength;
                }
                break;
        }
        return new Vector2f(yaw, pitch);
    }

    private double perlinNoise(double x, double y) {
        double floorX = Math.floor(x);
        double floorY = Math.floor(y);
        double sX = x - floorX;
        double sY = y - floorY;

        double n0 = dotGridGradient(floorX, floorY, x, y);
        double n1 = dotGridGradient(floorX + 1, floorY, x, y);
        double n2 = dotGridGradient(floorX, floorY + 1, x, y);
        double n3 = dotGridGradient(floorX + 1, floorY + 1, x, y);

        double ix0 = MathHelper.lerp(sX, n0, n1);
        double ix1 = MathHelper.lerp(sX, n2, n3);

        return MathHelper.lerp(sY, ix0, ix1);
    }

    private double dotGridGradient(double ix, double iy, double x, double y) {
        double dx = x - ix;
        double dy = y - iy;

        double random = 2920.f * Math.sin(ix * 21942.f + iy * 171324.f + 8912.f) * Math.cos(ix * 23157.f * iy * 217832.f + 9758.f);
        double gradX = Math.cos(random);
        double gradY = Math.sin(random);

        return (dx * gradX + dy * gradY);
    }

    private void recordRotationSample(float distance, float angleDelta, float targetVelocity) {
        AiRotationConfig.RotationSample sample = new AiRotationConfig.RotationSample();
        sample.timestamp = System.currentTimeMillis();
        sample.distanceToTarget = distance;
        sample.angleDelta = angleDelta;
        sample.targetVelocity = targetVelocity;
        sample.yawSpeed = currentYawSpeed;
        sample.pitchSpeed = currentPitchSpeed;
        sample.movementAggressiveness = currentMovementAggressiveness;
        sample.movementStrength = currentMovementStrength;
        sample.momentum = currentMomentum;
        sample.jitterStrength = currentJitterStrength;
        sample.jitterType = currentJitterType;
        sample.aimPointIndex = currentAimPointIndex;
        sample.didOvershoot = currentDidOvershoot;
        sample.wasSuccessfulHit = shouldRecordHit;

        if (shouldRecordHit) {
            config.updateStatistics(true);
            shouldRecordHit = false;
        }
        config.addSample(sample);
    }

    public void recordAttackEvent() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastAttackRecordTime < 100) return;
        lastAttackRecordTime = currentTime;
        shouldRecordHit = true;
    }

    public void learnFromSamples() {
        AiRotationConfig.AIRotationProfile mainProfile = config.getProfile();
        List<AiRotationConfig.RotationSample> successfulSamples = config.getSamples().stream()
                .filter(s -> s.wasSuccessfulHit).collect(Collectors.toList());

        if (successfulSamples.size() < mainProfile.generalSettings.minSamplesForLearning) return;

        for (AiRotationConfig.RotationSample sample : successfulSamples) {
            String contextKey = getContextKey(sample.distanceToTarget, sample.targetVelocity);
            AiRotationConfig.ContextualProfile profile = mainProfile.contextualProfiles.computeIfAbsent(contextKey, AiRotationConfig.ContextualProfile::new);
            float learningRate = mainProfile.generalSettings.learningRate;

            profile.yawSpeedMean = lerp(profile.yawSpeedMean, sample.yawSpeed, learningRate);
            profile.pitchSpeedMean = lerp(profile.pitchSpeedMean, sample.pitchSpeed, learningRate);
            profile.movementAggressivenessMean = lerp(profile.movementAggressivenessMean, sample.movementAggressiveness, learningRate);
            profile.movementStrengthMean = lerp(profile.movementStrengthMean, sample.movementStrength, learningRate);
            profile.momentumMean = lerp(profile.momentumMean, sample.momentum, learningRate);
            profile.jitterStrengthMean = lerp(profile.jitterStrengthMean, sample.jitterStrength, learningRate);

            updateWeight(profile.jitterPatternWeights, sample.jitterType, learningRate);
            updateWeight(profile.aimPointWeights, sample.aimPointIndex, learningRate);

            if (sample.didOvershoot) {
                profile.overshootChance = lerp(profile.overshootChance, profile.overshootChance + 0.01f, learningRate);
            } else {
                profile.overshootChance = lerp(profile.overshootChance, profile.overshootChance - 0.005f, learningRate);
            }
            profile.overshootChance = MathHelper.clamp(profile.overshootChance, 0.01f, 0.25f);
        }

        for (AiRotationConfig.ContextualProfile profile : mainProfile.contextualProfiles.values()) {
            if (ThreadLocalRandom.current().nextFloat() < mainProfile.generalSettings.mutationChance) {
                profile.movementAggressivenessMean *= (1.0f + (ThreadLocalRandom.current().nextFloat() - 0.5f) * 2f * mainProfile.generalSettings.mutationStrength);
            }
        }

        mainProfile.lastModifiedTime = System.currentTimeMillis();
        config.setProfile(mainProfile);
        config.save();
        config.clearSamples();
    }

    private String getContextKey(float distance, float targetVelocity) {
        String range = distance < 3.5f ? "CLOSE" : (distance < 6.0f ? "MID" : "FAR");
        String speed = targetVelocity < 1.0 ? "STATIC" : (targetVelocity < 5.0 ? "MOVING" : "SPRINTING");
        return range + "_" + speed;
    }

    private void updateWeight(Map<String, Float> weights, String key, float rate) {
        if (key == null || !weights.containsKey(key)) return;
        weights.forEach((k, v) -> weights.put(k, lerp(v, k.equals(key) ? v + 0.1f : v - 0.05f, rate)));
        normalizeWeights(weights);
    }

    private void updateWeight(Map<Integer, Float> weights, int key, float rate) {
        if (!weights.containsKey(key)) return;
        weights.forEach((k, v) -> weights.put(k, lerp(v, k == key ? v + 0.1f : v - 0.05f, rate)));
        normalizeWeights(weights);
    }

    private <T> void normalizeWeights(Map<T, Float> weights) {
        float min = 0.1f;
        weights.replaceAll((k, v) -> Math.max(v, min));
        double sum = weights.values().stream().mapToDouble(f -> f).sum();
        if (sum == 0) return;
        weights.replaceAll((k, v) -> (float) (v / sum));
    }

    private float lerp(float a, float b, float t) {
        return a + t * (b - a);
    }

    private float randomize(float mean, float variance) {
        return mean + (ThreadLocalRandom.current().nextFloat() - 0.5f) * 2f * variance;
    }

    private String selectWeightedRandom(Map<String, Float> weights) {
        double totalWeight = weights.values().stream().mapToDouble(f -> f).sum();
        if (totalWeight <= 0) return weights.keySet().stream().findFirst().orElse(null);
        double random = ThreadLocalRandom.current().nextDouble() * totalWeight;
        for (Map.Entry<String, Float> entry : weights.entrySet()) {
            random -= entry.getValue();
            if (random <= 0) return entry.getKey();
        }
        return weights.keySet().stream().findFirst().orElse(null);
    }

    @Override
    public void onEnable(AuraNew parentModule) {
        currentAimPoint = null;
        lastUpdateTime = 0;
        accumulatedYawMomentum = 0f;
        accumulatedPitchMomentum = 0f;
        lastTarget = null;
        shouldRecordHit = false;
        config.load();
    }

    @Override
    public void onDisable(AuraNew parentModule) {
        learnFromSamples();
        config.save();
    }

    public void saveSession() {
        learnFromSamples();
        config.save();
    }

    public void clearAllData() {
        config.clearSamples();
        config.resetStatistics();
        config.createDefaultProfile();
        config.save();
    }

    public AiRotationConfig.AIStatistics getStatistics() {
        return config.getStatistics();
    }
}