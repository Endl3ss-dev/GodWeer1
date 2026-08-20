package org.privates.client.managers.module.impl.combat.auraelement.impl;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import org.privates.client.managers.component.impl.rotation.Rotation;
import org.privates.client.managers.module.impl.combat.AuraNew;
import org.privates.client.managers.module.impl.combat.auraelement.AbstractRotationElement;
import org.privates.client.utils.chat.ChatUtil;
import org.privates.client.utils.other.GCDUtils;

import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;


public class NeuroRotation extends AbstractRotationElement {

    private static class TrainingPattern {
        float distanceToTarget;
        float angleDelta;
        float targetVelocity;
        float yawSpeed;
        float pitchSpeed;
        float movementAggressiveness;
        float movementStrength;
        float momentum;
        float jitterStrength;
        String jitterType;
        int aimPointIndex;
        boolean didOvershoot;
        boolean wasSuccessfulHit;
    }

    private enum JitterType {
        CIRCULAR, WAVE, SQUARE, CHAOTIC, SPIRAL
    }

    private enum MouseAxisPriority {
        YAW_FAST, PITCH_FAST, BALANCED
    }

    private static class JitterState {
        JitterType type;
        float phase;
        float amplitude;
        float frequency;
        Vector2f lastOffset;
        int patternTicks;
        long creationTime;
        float interpolationProgress;
        Vector2f targetOffset;
        Vector2f startOffset;

        JitterState(JitterType type) {
            this.type = type;
            this.phase = ThreadLocalRandom.current().nextFloat() * (float) Math.PI * 2;
            this.amplitude = 0.3f + ThreadLocalRandom.current().nextFloat() * 0.7f;
            this.frequency = 0.8f + ThreadLocalRandom.current().nextFloat() * 1.2f;
            this.lastOffset = new Vector2f(0, 0);
            this.patternTicks = 0;
            this.creationTime = System.currentTimeMillis();
            this.interpolationProgress = 0;
            this.startOffset = new Vector2f(0, 0);
            this.targetOffset = generateNextOffset();
        }

        Vector2f generateNextOffset() {
            float offsetX = (ThreadLocalRandom.current().nextFloat() - 0.5f) * 2.0f;
            float offsetY = (ThreadLocalRandom.current().nextFloat() - 0.5f) * 2.0f;
            return new Vector2f(offsetX, offsetY);
        }
    }

    private static class NexusRotationSchema {
        Vector2f nexusRotation;
        Vector2f targetRotation;
        Vector2f playerRotation;
        float convergenceSpeed;
        float offsetStrength;
        float freedomRadius;
        long lastUpdate;
        boolean isLocked;

        NexusRotationSchema() {
            this.nexusRotation = new Vector2f(0, 0);
            this.targetRotation = new Vector2f(0, 0);
            this.playerRotation = new Vector2f(0, 0);
            this.convergenceSpeed = 0.45f + ThreadLocalRandom.current().nextFloat() * 0.25f;
            this.offsetStrength = 0.25f + ThreadLocalRandom.current().nextFloat() * 0.15f;
            this.freedomRadius = 3.0f + ThreadLocalRandom.current().nextFloat() * 4.0f;
            this.lastUpdate = System.currentTimeMillis();
            this.isLocked = false;
        }

        void updateNexus(Vector2f target, Vector2f current, float deltaTime) {
            float distanceToTarget = (float) Math.sqrt(
                    Math.pow(MathHelper.wrapDegrees(target.x - nexusRotation.x), 2) +
                            Math.pow(target.y - nexusRotation.y, 2)
            );

            float distanceToPlayer = (float) Math.sqrt(
                    Math.pow(MathHelper.wrapDegrees(current.x - nexusRotation.x), 2) +
                            Math.pow(current.y - nexusRotation.y, 2)
            );

            if (distanceToTarget > 15.0f) {
                convergenceSpeed = 0.65f + ThreadLocalRandom.current().nextFloat() * 0.2f;
                isLocked = true;
            } else if (distanceToTarget < 5.0f) {
                convergenceSpeed = 0.35f + ThreadLocalRandom.current().nextFloat() * 0.15f;
                isLocked = false;
            }

            float dynamicConvergence = convergenceSpeed;
            if (distanceToPlayer > freedomRadius) {
                dynamicConvergence += 0.15f;
            }

            float yawDiff = MathHelper.wrapDegrees(target.x - nexusRotation.x);
            float pitchDiff = target.y - nexusRotation.y;

            nexusRotation = new Vector2f(
                    nexusRotation.x + yawDiff * dynamicConvergence,
                    nexusRotation.y + pitchDiff * dynamicConvergence
            );

            targetRotation = target;
            playerRotation = current;
        }

        Vector2f calculateFreedomOffset(Vector2f current) {
            float yawDiff = MathHelper.wrapDegrees(current.x - nexusRotation.x);
            float pitchDiff = current.y - nexusRotation.y;

            float distance = (float) Math.sqrt(yawDiff * yawDiff + pitchDiff * pitchDiff);

            if (distance > freedomRadius) {
                float clampFactor = freedomRadius / distance;
                yawDiff *= clampFactor;
                pitchDiff *= clampFactor;
            }

            return new Vector2f(yawDiff, pitchDiff);
        }
    }

    private static class SmoothnessPair {
        float yawSmoothness;
        float pitchSmoothness;
        long useCount;
        long lastUsed;

        SmoothnessPair(float yaw, float pitch) {
            this.yawSmoothness = yaw;
            this.pitchSmoothness = pitch;
            this.useCount = 0;
            this.lastUsed = 0;
        }
    }

    private final List<TrainingPattern> trainingPatterns = new ArrayList<>();
    private final Deque<RotationState> stateHistory = new ArrayDeque<>(10);
    private final List<SmoothnessPair> usedSmoothness = new ArrayList<>();
    private final Set<String> usedPatternHashes = new HashSet<>();

    private Vector3d lastTargetPosition = null;
    private Vector2f lastRotation = null;
    private long lastUpdateTime = 0L;
    private float currentMomentum = 0.0f;
    private int attackCycle = 0;
    private float adaptiveSpeed = 1.0f;
    private Vector3d currentAimPoint = null;
    private long lastAimPointChange = 0L;

    private JitterState currentJitter = null;
    private NexusRotationSchema nexusSchema = new NexusRotationSchema();
    private MouseAxisPriority currentAxisPriority = MouseAxisPriority.BALANCED;

    private TrainingPattern currentPattern = null;
    private long patternStartTime = 0L;
    private long patternDuration = 0L;

    private long lastDebugTime = 0L;
    private int debugCounter = 0;

    private boolean isTransitioningAimPoint = false;
    private Vector2f aimPointTransitionJitter = new Vector2f(0, 0);
    private float transitionProgress = 0.0f;

    private float lastYawSpeed = 0.0f;
    private float lastPitchSpeed = 0.0f;

    public NeuroRotation() {
        loadTrainingData();
        initializeJitter();
    }

    private void initializeJitter() {
        JitterType randomType = JitterType.values()[ThreadLocalRandom.current().nextInt(JitterType.values().length)];
        currentJitter = new JitterState(randomType);
        System.out.println("[JITTER] Initialized with type: " + randomType);
    }

    private void loadTrainingData() {
        try {
            String[] possiblePaths = {
                    "workspace/ai_neuro_core/training_data.json",
                    "ai_neuro_core/training_data.json",
                    "training_data.json",
                    "C:/Users/privat/Desktop/excellent-client/workspace/ai_neuro_core/training_data.json"
            };

            File dataFile = null;
            for (String pathStr : possiblePaths) {
                File file = new File(pathStr);
                if (file.exists()) {
                    dataFile = file;
                    System.out.println("Found training_data.json at: " + file.getAbsolutePath());
                    break;
                }
            }

            if (dataFile == null || !dataFile.exists()) {
                System.err.println("training_data.json not found! Tried paths:");
                for (String path : possiblePaths) {
                    System.err.println("  - " + new File(path).getAbsolutePath());
                }
                return;
            }

            Gson gson = new Gson();
            JsonArray jsonArray = gson.fromJson(new FileReader(dataFile), JsonArray.class);

            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject obj = jsonArray.get(i).getAsJsonObject();
                TrainingPattern pattern = new TrainingPattern();

                pattern.distanceToTarget = obj.get("distanceToTarget").getAsFloat();
                pattern.angleDelta = obj.get("angleDelta").getAsFloat();
                pattern.targetVelocity = obj.get("targetVelocity").getAsFloat();
                pattern.yawSpeed = obj.get("yawSpeed").getAsFloat();
                pattern.pitchSpeed = obj.get("pitchSpeed").getAsFloat();
                pattern.movementAggressiveness = obj.get("movementAggressiveness").getAsFloat();
                pattern.movementStrength = obj.get("movementStrength").getAsFloat();
                pattern.momentum = obj.get("momentum").getAsFloat();
                pattern.jitterStrength = obj.get("jitterStrength").getAsFloat();
                pattern.jitterType = obj.get("jitterType").getAsString();
                pattern.aimPointIndex = obj.get("aimPointIndex").getAsInt();
                pattern.didOvershoot = obj.get("didOvershoot").getAsBoolean();
                pattern.wasSuccessfulHit = obj.get("wasSuccessfulHit").getAsBoolean();

                trainingPatterns.add(pattern);
            }

            System.out.println("Successfully loaded " + trainingPatterns.size() + " training patterns from: " + dataFile.getAbsolutePath());
        } catch (Exception e) {
            System.err.println("Failed to load training data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public Vector2f updateRotation(AuraNew parentModule, LivingEntity target, Minecraft mc, Vector2f currentLerpRotation) {
        if (target == null || mc.player == null) {
            return currentLerpRotation;
        }

        if (trainingPatterns.isEmpty()) {
            return getFallbackRotation(target, mc, currentLerpRotation);
        }

        long currentTime = System.currentTimeMillis();
        float deltaTime = lastUpdateTime > 0 ? (currentTime - lastUpdateTime) / 1000.0f : 0.016f;
        lastUpdateTime = currentTime;

        Vector3d targetPos = target.getPositionVec();
        Vector3d playerPos = mc.player.getPositionVec();
        float currentDistance = (float) playerPos.distanceTo(targetPos);

        Vector3d direction = targetPos.add(0, target.getEyeHeight() * 0.65, 0).subtract(mc.player.getEyePosition(1.0f));
        float targetYaw = (float) Math.toDegrees(Math.atan2(direction.z, direction.x)) - 90.0f;
        float targetPitch = (float) -Math.toDegrees(Math.atan2(direction.y, Math.sqrt(direction.x * direction.x + direction.z * direction.z)));
        float currentAngleDelta = (float) Math.sqrt(
                Math.pow(MathHelper.wrapDegrees(targetYaw - currentLerpRotation.x), 2) +
                        Math.pow(targetPitch - currentLerpRotation.y, 2)
        );

        if (currentPattern == null || currentTime - patternStartTime > patternDuration) {
            currentPattern = selectBestPattern(currentDistance, currentAngleDelta);
            patternStartTime = currentTime;
            patternDuration = 50 + ThreadLocalRandom.current().nextLong(100);

            printPatternDebug(currentPattern, currentDistance, currentAngleDelta);
        }

        float[] aimHeights = {0.9f, 0.65f, 0.45f};
        float aimHeight = aimHeights[Math.min(currentPattern.aimPointIndex, aimHeights.length - 1)];

        if (currentAimPoint == null || currentTime - lastAimPointChange > 400 + ThreadLocalRandom.current().nextInt(200)) {
            Vector3d oldAimPoint = currentAimPoint;

            float horizontalOffset = (ThreadLocalRandom.current().nextFloat() - 0.5f) * target.getWidth() * 0.3f;
            float depthOffset = (ThreadLocalRandom.current().nextFloat() - 0.5f) * target.getWidth() * 0.3f;
            currentAimPoint = target.getPositionVec().add(horizontalOffset, target.getEyeHeight() * aimHeight, depthOffset);
            lastAimPointChange = currentTime;

            if (oldAimPoint != null) {
                isTransitioningAimPoint = true;
                transitionProgress = 0.0f;
                aimPointTransitionJitter = generateAimPointTransitionJitter();
                System.out.println("[AIM TRANSITION] Started with jitter: " + aimPointTransitionJitter);
            }
        } else {
            float horizontalOffset = (ThreadLocalRandom.current().nextFloat() - 0.5f) * target.getWidth() * 0.3f;
            float depthOffset = (ThreadLocalRandom.current().nextFloat() - 0.5f) * target.getWidth() * 0.3f;
            Vector3d targetPoint = target.getPositionVec().add(horizontalOffset, target.getEyeHeight() * aimHeight, depthOffset);
            currentAimPoint = currentAimPoint.lerp(targetPoint, 0.15f);
        }

        if (isTransitioningAimPoint) {
            transitionProgress += deltaTime * 2.5f;
            if (transitionProgress >= 1.0f) {
                isTransitioningAimPoint = false;
                transitionProgress = 0.0f;
                aimPointTransitionJitter = new Vector2f(0, 0);
            }
        }

        Vector3d playerEyes = mc.player.getEyePosition(1.0f);
        direction = currentAimPoint.subtract(playerEyes);

        float calculatedYaw = (float) Math.toDegrees(Math.atan2(direction.z, direction.x)) - 90.0f;
        float calculatedPitch = (float) -Math.toDegrees(Math.atan2(direction.y, Math.sqrt(direction.x * direction.x + direction.z * direction.z)));

        Vector2f targetRotation = new Vector2f(calculatedYaw, calculatedPitch);
        nexusSchema.updateNexus(targetRotation, currentLerpRotation, deltaTime);

        Vector2f freedomOffset = nexusSchema.calculateFreedomOffset(currentLerpRotation);

        float nexusYaw = nexusSchema.nexusRotation.x;
        float nexusPitch = nexusSchema.nexusRotation.y;

        calculatedYaw = nexusYaw + freedomOffset.x * nexusSchema.offsetStrength;
        calculatedPitch = nexusPitch + freedomOffset.y * nexusSchema.offsetStrength;

        Vector2f jitterOffset = calculateAdvancedJitter(deltaTime, currentPattern.jitterStrength);
        calculatedYaw += jitterOffset.x;
        calculatedPitch += jitterOffset.y;

        if (isTransitioningAimPoint) {
            float transitionFactor = 1.0f - (float) Math.pow(1.0f - transitionProgress, 2);
            calculatedYaw += aimPointTransitionJitter.x * (1.0f - transitionFactor);
            calculatedPitch += aimPointTransitionJitter.y * (1.0f - transitionFactor);
        }

        float yawDiff = MathHelper.wrapDegrees(calculatedYaw - currentLerpRotation.x);
        float pitchDiff = calculatedPitch - currentLerpRotation.y;

        float absYawSpeed = Math.abs(yawDiff);
        float absPitchSpeed = Math.abs(pitchDiff);

        float yawSlowdown = 1.0f;
        float pitchSlowdown = 1.0f;

        if (absYawSpeed > absPitchSpeed * 1.3f) {
            currentAxisPriority = MouseAxisPriority.YAW_FAST;
            pitchSlowdown = 0.15f + ThreadLocalRandom.current().nextFloat() * 0.15f;
        } else if (absPitchSpeed > absYawSpeed * 1.3f) {
            currentAxisPriority = MouseAxisPriority.PITCH_FAST;
            yawSlowdown = 0.15f + ThreadLocalRandom.current().nextFloat() * 0.15f;
        } else {
            currentAxisPriority = MouseAxisPriority.BALANCED;
        }

        if (currentPattern.didOvershoot && Math.abs(yawDiff) > 3.0f) {
            float overshootFactor = 1.08f + ThreadLocalRandom.current().nextFloat() * 0.07f;
            yawDiff *= overshootFactor;
            pitchDiff *= overshootFactor;
        }

        currentMomentum = currentMomentum * (1.0f - currentPattern.momentum) +
                (yawDiff + pitchDiff) * currentPattern.momentum * 0.5f;

        SmoothnessPair smoothness = generateUniqueSmoothness();

        float finalYawSmoothness = smoothness.yawSmoothness * yawSlowdown;
        float finalPitchSmoothness = smoothness.pitchSmoothness * pitchSlowdown;

        float newYaw = currentLerpRotation.x + yawDiff * finalYawSmoothness * adaptiveSpeed;
        float newPitch = currentLerpRotation.y + pitchDiff * finalPitchSmoothness * adaptiveSpeed;

        newYaw += currentMomentum * 0.08f;
        newPitch += currentMomentum * 0.04f;

        newYaw = GCDUtils.getSensitivity(newYaw);
        newPitch = GCDUtils.getSensitivity(MathHelper.clamp(newPitch, -90f, 90f));

        Vector2f result = new Vector2f(newYaw, newPitch);

        RotationState state = new RotationState(
                result, target.getPositionVec(),
                yawDiff, pitchDiff,
                currentPattern.yawSpeed, currentPattern.pitchSpeed, currentTime
        );
        stateHistory.addLast(state);
        if (stateHistory.size() > 10) {
            stateHistory.removeFirst();
        }

        applyRotation(new Rotation(newYaw, newPitch), currentPattern.yawSpeed, currentPattern.pitchSpeed, 0, 3);
        parentModule.setLerpRotation(result);

        if (currentTime - lastDebugTime > 100) {
            printRotationDebug(calculatedYaw, calculatedPitch, yawDiff, pitchDiff, jitterOffset, smoothness, yawSlowdown, pitchSlowdown);
            lastDebugTime = currentTime;
        }

        lastRotation = result;
        lastTargetPosition = target.getPositionVec();
        attackCycle = (attackCycle + 1) % 100;
        adaptiveSpeed = 0.92f + (float) Math.sin(attackCycle * 0.08f) * 0.12f;

        return result;
    }

    private Vector2f generateAimPointTransitionJitter() {
        float strength = 2.0f + ThreadLocalRandom.current().nextFloat() * 3.0f;
        return new Vector2f(
                (ThreadLocalRandom.current().nextFloat() - 0.5f) * strength,
                (ThreadLocalRandom.current().nextFloat() - 0.5f) * strength * 0.7f
        );
    }

    private Vector2f calculateAdvancedJitter(float deltaTime, float strength) {
        currentJitter.patternTicks++;

        if (currentJitter.patternTicks > 25 + ThreadLocalRandom.current().nextInt(20)) {
            JitterType oldType = currentJitter.type;
            JitterType newType;
            do {
                newType = JitterType.values()[ThreadLocalRandom.current().nextInt(JitterType.values().length)];
            } while (newType == oldType);

            currentJitter = new JitterState(newType);
            System.out.println("[JITTER] Switched from " + oldType + " to " + newType);
        }

        currentJitter.phase += deltaTime * currentJitter.frequency * (2.0f + ThreadLocalRandom.current().nextFloat() * 0.5f);

        currentJitter.interpolationProgress += deltaTime * 3.0f;
        if (currentJitter.interpolationProgress >= 1.0f) {
            currentJitter.interpolationProgress = 0.0f;
            currentJitter.startOffset = currentJitter.targetOffset;
            currentJitter.targetOffset = currentJitter.generateNextOffset();
        }

        float smoothProgress = (float) (1.0 - Math.cos(currentJitter.interpolationProgress * Math.PI)) / 2.0f;

        Vector2f baseOffset = calculateJitterPattern(currentJitter.type, currentJitter.phase, currentJitter.amplitude);

        Vector2f interpolatedOffset = new Vector2f(
                MathHelper.lerp(smoothProgress, currentJitter.startOffset.x, currentJitter.targetOffset.x),
                MathHelper.lerp(smoothProgress, currentJitter.startOffset.y, currentJitter.targetOffset.y)
        );

        float jitterX = baseOffset.x * strength + interpolatedOffset.x * 0.3f;
        float jitterY = baseOffset.y * strength + interpolatedOffset.y * 0.3f;

        if (ThreadLocalRandom.current().nextFloat() < 0.08f) {
            float bounceStrength = 0.5f + ThreadLocalRandom.current().nextFloat();
            jitterX += (ThreadLocalRandom.current().nextFloat() - 0.5f) * bounceStrength;
            jitterY += (ThreadLocalRandom.current().nextFloat() - 0.5f) * bounceStrength * 0.7f;
        }

        currentJitter.lastOffset = new Vector2f(jitterX, jitterY);
        return currentJitter.lastOffset;
    }

    private Vector2f calculateJitterPattern(JitterType type, float phase, float amplitude) {
        float x = 0.0f;
        float y = 0.0f;

        switch (type) {
            case CIRCULAR:
                x = (float) Math.cos(phase) * amplitude * 1.5f;
                y = (float) Math.sin(phase) * amplitude * 1.5f;
                break;

            case WAVE:
                x = (float) Math.sin(phase * 1.3f) * amplitude * 1.8f;
                y = (float) Math.sin(phase * 0.7f + Math.PI / 4) * amplitude * 1.2f;
                break;

            case SQUARE:
                float squareX = Math.signum((float) Math.sin(phase));
                float squareY = Math.signum((float) Math.cos(phase));
                x = squareX * amplitude * 1.4f;
                y = squareY * amplitude * 1.4f;
                break;

            case CHAOTIC:
                x = (float) (Math.sin(phase * 2.7f) * Math.cos(phase * 1.3f)) * amplitude * 2.0f;
                y = (float) (Math.cos(phase * 1.9f) * Math.sin(phase * 0.8f)) * amplitude * 1.5f;
                x += (ThreadLocalRandom.current().nextFloat() - 0.5f) * amplitude * 0.5f;
                y += (ThreadLocalRandom.current().nextFloat() - 0.5f) * amplitude * 0.5f;
                break;

            case SPIRAL:
                float radius = (float) ((phase % (Math.PI * 4)) / (Math.PI * 4));
                x = (float) Math.cos(phase) * radius * amplitude * 2.0f;
                y = (float) Math.sin(phase) * radius * amplitude * 2.0f;
                break;
        }

        return new Vector2f(x, y);
    }

    private SmoothnessPair generateUniqueSmoothness() {
        float baseYaw = 0.3f + ThreadLocalRandom.current().nextFloat() * 0.5f;
        float basePitch = 0.3f + ThreadLocalRandom.current().nextFloat() * 0.5f;

        int attempts = 0;

        while (attempts < 50) {
            boolean isSimilar = false;
            for (SmoothnessPair used : usedSmoothness) {
                if (Math.abs(used.yawSmoothness - baseYaw) < 0.05f &&
                        Math.abs(used.pitchSmoothness - basePitch) < 0.05f) {
                    isSimilar = true;
                    break;
                }
            }

            if (!isSimilar) {
                break;
            }

            baseYaw = 0.3f + ThreadLocalRandom.current().nextFloat() * 0.5f;
            basePitch = 0.3f + ThreadLocalRandom.current().nextFloat() * 0.5f;
            attempts++;
        }

        SmoothnessPair pair = new SmoothnessPair(baseYaw, basePitch);
        pair.lastUsed = System.currentTimeMillis();
        pair.useCount++;

        usedSmoothness.add(pair);
        if (usedSmoothness.size() > 100) {
            usedSmoothness.remove(0);
        }

        return pair;
    }

    private void printPatternDebug(TrainingPattern pattern, float currentDistance, float currentAngleDelta) {
        System.out.println("========== NEURO ROTATION PATTERN DEBUG ==========");
        System.out.println("Pattern Selected: #" + (++debugCounter));
        System.out.println("Current Distance: " + String.format("%.2f", currentDistance) + " blocks");
        System.out.println("Current Angle Delta: " + String.format("%.2f", currentAngleDelta) + " degrees");
        System.out.println("--------------------------------------------------");
        System.out.println("Pattern Distance Target: " + String.format("%.2f", pattern.distanceToTarget));
        System.out.println("Pattern Angle Delta: " + String.format("%.2f", pattern.angleDelta));
        System.out.println("Pattern Target Velocity: " + String.format("%.2f", pattern.targetVelocity));
        System.out.println("--------------------------------------------------");
        System.out.println("Yaw Speed: " + String.format("%.2f", pattern.yawSpeed) + " deg/s");
        System.out.println("Pitch Speed: " + String.format("%.2f", pattern.pitchSpeed) + " deg/s");
        System.out.println("Movement Aggressiveness: " + String.format("%.3f", pattern.movementAggressiveness));
        System.out.println("Movement Strength: " + String.format("%.3f", pattern.movementStrength));
        System.out.println("Momentum: " + String.format("%.3f", pattern.momentum));
        System.out.println("--------------------------------------------------");
        System.out.println("Jitter Type: " + pattern.jitterType);
        System.out.println("Jitter Strength: " + String.format("%.3f", pattern.jitterStrength));
        System.out.println("Aim Point Index: " + pattern.aimPointIndex + " (Height: " + getAimHeightName(pattern.aimPointIndex) + ")");
        System.out.println("--------------------------------------------------");
        System.out.println("Did Overshoot: " + pattern.didOvershoot);
        System.out.println("Was Successful Hit: " + pattern.wasSuccessfulHit);
        System.out.println("Current Jitter Type: " + currentJitter.type);
        System.out.println("Nexus Convergence: " + String.format("%.3f", nexusSchema.convergenceSpeed));
        System.out.println("Nexus Freedom Radius: " + String.format("%.2f", nexusSchema.freedomRadius));
        System.out.println("Nexus Locked: " + nexusSchema.isLocked);
        System.out.println("==================================================\n");
    }

    private void printRotationDebug(float yaw, float pitch, float yawDiff, float pitchDiff, Vector2f jitter, SmoothnessPair smoothness, float yawSlow, float pitchSlow) {
        System.out.println("---------- ROTATION STATE ----------");
        System.out.println("Target Yaw: " + String.format("%.2f", yaw));
        System.out.println("Target Pitch: " + String.format("%.2f", pitch));
        System.out.println("Yaw Difference: " + String.format("%.2f", yawDiff));
        System.out.println("Pitch Difference: " + String.format("%.2f", pitchDiff));
        System.out.println("Jitter X: " + String.format("%.3f", jitter.x));
        System.out.println("Jitter Y: " + String.format("%.3f", jitter.y));
        System.out.println("Yaw Smoothness: " + String.format("%.3f", smoothness.yawSmoothness));
        System.out.println("Pitch Smoothness: " + String.format("%.3f", smoothness.pitchSmoothness));
        System.out.println("Yaw Slowdown: " + String.format("%.3f", yawSlow));
        System.out.println("Pitch Slowdown: " + String.format("%.3f", pitchSlow));
        System.out.println("Adaptive Speed: " + String.format("%.3f", adaptiveSpeed));
        System.out.println("Current Momentum: " + String.format("%.3f", currentMomentum));
        System.out.println("Jitter Type: " + currentJitter.type);
        System.out.println("Mouse Priority: " + currentAxisPriority);
        System.out.println("Nexus Rotation: [" + String.format("%.2f", nexusSchema.nexusRotation.x) + ", " + String.format("%.2f", nexusSchema.nexusRotation.y) + "]");
        System.out.println("Target Rotation: [" + String.format("%.2f", nexusSchema.targetRotation.x) + ", " + String.format("%.2f", nexusSchema.targetRotation.y) + "]");
        System.out.println("Nexus Distance: " + String.format("%.2f", Math.sqrt(
                Math.pow(MathHelper.wrapDegrees(nexusSchema.targetRotation.x - nexusSchema.nexusRotation.x), 2) +
                        Math.pow(nexusSchema.targetRotation.y - nexusSchema.nexusRotation.y, 2)
        )));
        System.out.println("Aim Transition: " + (isTransitioningAimPoint ? String.format("%.1f%%", transitionProgress * 100) : "None"));
        System.out.println("------------------------------------\n");
    }

    private String getAimHeightName(int index) {
        return switch (index) {
            case 0 -> "HEAD";
            case 1 -> "CHEST";
            case 2 -> "BODY";
            default -> "UNKNOWN";
        };
    }

    private TrainingPattern selectBestPattern(float currentDistance, float currentAngleDelta) {
        if (trainingPatterns.isEmpty()) {
            return createDefaultPattern();
        }

        List<TrainingPattern> candidates = new ArrayList<>();
        float distanceTolerance = 1.5f;
        float angleTolerance = 30.0f;

        for (TrainingPattern pattern : trainingPatterns) {
            float distanceDiff = Math.abs(pattern.distanceToTarget - currentDistance);
            float angleDiff = Math.abs(pattern.angleDelta - currentAngleDelta);

            if (distanceDiff < distanceTolerance && angleDiff < angleTolerance) {
                String patternHash = generatePatternHash(pattern);
                if (!usedPatternHashes.contains(patternHash)) {
                    candidates.add(pattern);
                }
            }
        }

        if (candidates.isEmpty()) {
            System.out.println("[WARN] No unique matching patterns found, clearing history");
            usedPatternHashes.clear();
            for (TrainingPattern pattern : trainingPatterns) {
                float distanceDiff = Math.abs(pattern.distanceToTarget - currentDistance);
                float angleDiff = Math.abs(pattern.angleDelta - currentAngleDelta);
                if (distanceDiff < distanceTolerance && angleDiff < angleTolerance) {
                    candidates.add(pattern);
                }
            }
        }

        if (candidates.isEmpty()) {
            return trainingPatterns.get(ThreadLocalRandom.current().nextInt(trainingPatterns.size()));
        }

        TrainingPattern selected = candidates.get(ThreadLocalRandom.current().nextInt(candidates.size()));
        String selectedHash = generatePatternHash(selected);
        usedPatternHashes.add(selectedHash);

        if (usedPatternHashes.size() > 50) {
            Iterator<String> iterator = usedPatternHashes.iterator();
            for (int i = 0; i < 10 && iterator.hasNext(); i++) {
                iterator.next();
                iterator.remove();
            }
        }

        System.out.println("[INFO] Found " + candidates.size() + " matching patterns, selected unique pattern");
        return selected;
    }

    private String generatePatternHash(TrainingPattern pattern) {
        return String.format("%.2f_%.2f_%.2f_%.2f_%d_%s",
                pattern.distanceToTarget,
                pattern.angleDelta,
                pattern.yawSpeed,
                pattern.pitchSpeed,
                pattern.aimPointIndex,
                pattern.jitterType);
    }

    private TrainingPattern createDefaultPattern() {
        ChatUtil.addText("[WARN] Creating default pattern (no training data loaded)");
        TrainingPattern pattern = new TrainingPattern();
        pattern.distanceToTarget = 3.0f;
        pattern.angleDelta = 10.0f;
        pattern.targetVelocity = 0.0f;
        pattern.yawSpeed = 180.0f;
        pattern.pitchSpeed = 90.0f;
        pattern.movementAggressiveness = 0.5f;
        pattern.movementStrength = 0.7f;
        pattern.momentum = 0.4f;
        pattern.jitterStrength = 0.5f;
        pattern.jitterType = "SINE";
        pattern.aimPointIndex = 1;
        pattern.didOvershoot = false;
        pattern.wasSuccessfulHit = false;
        return pattern;
    }

    private Vector2f getFallbackRotation(LivingEntity target, Minecraft mc, Vector2f currentLerpRotation) {
        System.out.println("[FALLBACK] Using fallback rotation (no training patterns available)");
        Vector3d targetPos = target.getPositionVec().add(0, target.getEyeHeight() * 0.65, 0);
        Vector3d playerEyes = mc.player.getEyePosition(1.0f);
        Vector3d direction = targetPos.subtract(playerEyes);

        float yaw = (float) Math.toDegrees(Math.atan2(direction.z, direction.x)) - 90.0f;
        float pitch = (float) -Math.toDegrees(Math.atan2(direction.y, Math.sqrt(direction.x * direction.x + direction.z * direction.z)));

        float yawDiff = MathHelper.wrapDegrees(yaw - currentLerpRotation.x);
        float pitchDiff = pitch - currentLerpRotation.y;

        float newYaw = currentLerpRotation.x + yawDiff * 0.4f;
        float newPitch = currentLerpRotation.y + pitchDiff * 0.4f;

        newYaw = GCDUtils.getSensitivity(newYaw);
        newPitch = GCDUtils.getSensitivity(MathHelper.clamp(newPitch, -90f, 90f));

        return new Vector2f(newYaw, newPitch);
    }

    @Override
    public void onEnable(AuraNew parentModule) {
        System.out.println("\n========== NEURO ROTATION ENABLED ==========");
        System.out.println("Loaded Patterns: " + trainingPatterns.size());
        System.out.println("Jitter System: Advanced Multi-Pattern");
        System.out.println("Rotation Schema: Nexus with Dynamic Freedom");
        System.out.println("Mouse Realism: Axis Priority with Slowdown");
        System.out.println("Pattern Memory: Anti-Repeat System");
        System.out.println("Aim Point Transition: Jitter Bypass");
        System.out.println("===========================================\n");

        stateHistory.clear();
        lastTargetPosition = null;
        lastRotation = null;
        lastUpdateTime = 0L;
        currentMomentum = 0.0f;
        attackCycle = 0;
        adaptiveSpeed = 1.0f;
        currentAimPoint = null;
        lastAimPointChange = 0L;
        currentPattern = null;
        patternStartTime = 0L;
        patternDuration = 0L;
        debugCounter = 0;
        lastDebugTime = 0L;

        nexusSchema = new NexusRotationSchema();
        currentAxisPriority = MouseAxisPriority.BALANCED;
        usedSmoothness.clear();
        usedPatternHashes.clear();

        initializeJitter();

        isTransitioningAimPoint = false;
        aimPointTransitionJitter = new Vector2f(0, 0);
        transitionProgress = 0.0f;

        lastYawSpeed = 0.0f;
        lastPitchSpeed = 0.0f;
    }

    @Override
    public void onDisable(AuraNew parentModule) {
        super.onDisable(parentModule);
        System.out.println("\n========== NEURO ROTATION DISABLED ==========");
        System.out.println("Total Patterns Used: " + debugCounter);
        System.out.println("Unique Smoothness Pairs: " + usedSmoothness.size());
        System.out.println("Pattern Hash Memory: " + usedPatternHashes.size());
        System.out.println("Final Jitter Type: " + (currentJitter != null ? currentJitter.type : "None"));
        System.out.println("Final Nexus State: " + (nexusSchema.isLocked ? "LOCKED" : "FREE"));
        System.out.println("============================================\n");

        stateHistory.clear();
        lastTargetPosition = null;
        lastRotation = null;
        currentAimPoint = null;
        currentPattern = null;
        currentJitter = null;
        usedSmoothness.clear();
        usedPatternHashes.clear();
        nexusSchema = null;
    }

    private static class RotationState {
        final Vector2f rotation;
        final Vector3d targetPosition;
        final float yawDelta;
        final float pitchDelta;
        final float yawSpeed;
        final float pitchSpeed;
        final long timestamp;

        RotationState(Vector2f rotation, Vector3d targetPosition, float yawDelta, float pitchDelta,
                      float yawSpeed, float pitchSpeed, long timestamp) {
            this.rotation = rotation;
            this.targetPosition = targetPosition;
            this.yawDelta = yawDelta;
            this.pitchDelta = pitchDelta;
            this.yawSpeed = yawSpeed;
            this.pitchSpeed = pitchSpeed;
            this.timestamp = timestamp;
        }
    }
}