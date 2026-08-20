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

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ThreadLocalRandom;

public class SlothAiRotation extends AbstractRotationElement {

    private static class RotationState {
        float yaw, pitch;
        float deltaYaw, deltaPitch;
        float accelYaw, accelPitch;
        float jerkYaw, jerkPitch;
        long timestamp;

        RotationState(float yaw, float pitch, float dYaw, float dPitch,
                      float aYaw, float aPitch, float jYaw, float jPitch, long time) {
            this.yaw = yaw;
            this.pitch = pitch;
            this.deltaYaw = dYaw;
            this.deltaPitch = dPitch;
            this.accelYaw = aYaw;
            this.accelPitch = aPitch;
            this.jerkYaw = jYaw;
            this.jerkPitch = jPitch;
            this.timestamp = time;
        }
    }

    private final Deque<RotationState> history = new ArrayDeque<>(100);

    private float targetSensitivity;
    private float targetGCD;
    private boolean gcdInitialized = false;

    private boolean isInitialized = false;
    private long sequenceStartTime;
    private int ticksInSequence;

    private float attentionWavePhase;
    private float microTremorPhase;
    private float fatigueAccumulator;

    private float momentumYaw;
    private float momentumPitch;

    private int hesitationCounter;
    private boolean isHesitating;

    private float perlinOffsetX;
    private float perlinOffsetY;
    private float perlinTime;

    @Override
    public Vector2f updateRotation(AuraNew parentModule, LivingEntity target, Minecraft mc, Vector2f currentLerpRotation) {
        if (target == null || mc.player == null) {
            resetState();
            return currentLerpRotation;
        }

        long currentTime = System.currentTimeMillis();
        ThreadLocalRandom rng = ThreadLocalRandom.current();

        if (!isInitialized) {
            initializeRotation(currentLerpRotation, rng);
        }

        if (!gcdInitialized) {
            initializeGCD(mc, rng);
        }

        updateCombatSequence(currentTime);

        Vector3d aimPoint = calculateHumanAimPoint(target, currentTime, rng);
        Vector2f targetRotation = calculateTargetRotation(mc.player.getEyePosition(1.0f), aimPoint);

        applyHumanBehavior(targetRotation, currentTime, rng);

        Vector2f smoothedRotation = applyMultiLayerSmoothing(
                targetRotation,
                currentLerpRotation,
                currentTime,
                rng
        );

        smoothedRotation = applyPhysicalConstraints(smoothedRotation, currentLerpRotation, rng);

        smoothedRotation = applyGCDWithNaturalError(smoothedRotation, rng);

        RotationState newState = recordRotationState(smoothedRotation, currentLerpRotation, currentTime);

        float finalYaw = newState.yaw;
        float finalPitch = MathHelper.clamp(newState.pitch, -89.9f, 89.9f);

        Vector2f result = new Vector2f(finalYaw, finalPitch);

        float yawSpeed = calculateAdaptiveSpeed(Math.abs(newState.deltaYaw), 15.0f, 60.0f, rng);
        float pitchSpeed = calculateAdaptiveSpeed(Math.abs(newState.deltaPitch), 8.0f, 35.0f, rng);

        applyRotation(new Rotation(finalYaw, finalPitch), yawSpeed, pitchSpeed, 0, 4);
        parentModule.setLerpRotation(result);

        return result;
    }

    private void initializeRotation(Vector2f current, ThreadLocalRandom rng) {
        isInitialized = true;
        sequenceStartTime = System.currentTimeMillis();
        ticksInSequence = 0;

        attentionWavePhase = rng.nextFloat((float) Math.PI * 2);
        microTremorPhase = rng.nextFloat((float) Math.PI * 2);
        perlinTime = rng.nextFloat(1000.0f);

        momentumYaw = 0;
        momentumPitch = 0;

        history.clear();
        history.add(new RotationState(current.x, current.y, 0, 0, 0, 0, 0, 0, System.currentTimeMillis()));
    }

    private void initializeGCD(Minecraft mc, ThreadLocalRandom rng) {
        float mouseSens = (float) mc.gameSettings.mouseSensitivity;
        float actualSens = mouseSens * 0.6f + 0.2f;
        float gcdBase = actualSens * actualSens * actualSens * 1.2f;

        float variation = 0.98f + rng.nextFloat(0.04f);
        targetGCD = gcdBase * variation;
        targetSensitivity = mouseSens;
        gcdInitialized = true;
    }

    private void updateCombatSequence(long currentTime) {
        ticksInSequence++;

        long sequenceDuration = currentTime - sequenceStartTime;

        if (sequenceDuration > 5000) {
            sequenceStartTime = currentTime;
            ticksInSequence = 0;
            fatigueAccumulator = Math.min(fatigueAccumulator + 0.015f, 0.12f);
        }
    }

    private Vector3d calculateHumanAimPoint(LivingEntity target, long currentTime, ThreadLocalRandom rng) {
        Vector3d basePos = target.getPositionVec();
        float targetHeight = target.getHeight();

        attentionWavePhase += 0.01f + rng.nextFloat(-0.002f, 0.002f);
        float heightBias = 0.6f + (float) Math.sin(attentionWavePhase) * 0.1f;

        microTremorPhase += 0.06f + rng.nextFloat(-0.015f, 0.015f);
        float tremorX = (float) Math.sin(microTremorPhase * 2.1) * 0.04f;
        float tremorY = (float) Math.cos(microTremorPhase * 1.6) * 0.03f;
        float tremorZ = (float) Math.sin(microTremorPhase * 2.8) * 0.04f;

        perlinTime += 0.006f;
        perlinOffsetX += ((float) improvedNoise(perlinTime, 0, 0) - 0.5f) * 0.012f;
        perlinOffsetY += ((float) improvedNoise(0, perlinTime, 0) - 0.5f) * 0.008f;
        perlinOffsetX *= 0.94f;
        perlinOffsetY *= 0.95f;

        float anticipationFactor = 0;
        Vector3d velocity = target.getMotion();
        double velocityMag = Math.sqrt(velocity.x * velocity.x + velocity.z * velocity.z);
        if (velocityMag > 0.08) {
            anticipationFactor = (float) (velocityMag * 0.25);
            anticipationFactor *= (0.85f + rng.nextFloat(0.3f));
        }

        return basePos.add(
                tremorX + perlinOffsetX * 0.5f + velocity.x * anticipationFactor,
                targetHeight * heightBias + tremorY + perlinOffsetY * 0.5f,
                tremorZ + velocity.z * anticipationFactor
        );
    }

    private Vector2f calculateTargetRotation(Vector3d eyePos, Vector3d targetPos) {
        Vector3d delta = targetPos.subtract(eyePos);
        double horizDist = Math.sqrt(delta.x * delta.x + delta.z * delta.z);

        float yaw = (float) Math.toDegrees(Math.atan2(delta.z, delta.x)) - 90.0f;
        float pitch = (float) -Math.toDegrees(Math.atan2(delta.y, horizDist));

        return new Vector2f(yaw, pitch);
    }

    private void applyHumanBehavior(Vector2f target, long currentTime, ThreadLocalRandom rng) {
        if (!isHesitating && rng.nextFloat() < 0.02f) {
            isHesitating = true;
            hesitationCounter = rng.nextInt(2, 6);
        }

        if (isHesitating) {
            float hesitationNoise = (rng.nextFloat() - 0.5f) * 0.6f;
            target.x += hesitationNoise;
            target.y += hesitationNoise * 0.4f;

            hesitationCounter--;
            if (hesitationCounter <= 0) {
                isHesitating = false;
            }
        }

        float breathingOffset = (float) Math.sin(currentTime * 0.0006) * 0.12f;
        target.x += breathingOffset;
        target.y += breathingOffset * 0.5f;
    }

    private Vector2f applyMultiLayerSmoothing(Vector2f target, Vector2f current, long currentTime, ThreadLocalRandom rng) {
        float yawDiff = MathHelper.wrapDegrees(target.x - current.x);
        float pitchDiff = target.y - current.y;

        float fatigueFactor = 1.0f - fatigueAccumulator;

        float baseSmoothYaw = 0.25f + rng.nextFloat(0.06f);
        float baseSmoothPitch = 0.2f + rng.nextFloat(0.05f);

        float distanceBasedYaw = Math.min(Math.abs(yawDiff) / 90.0f, 1.0f);
        float distanceBasedPitch = Math.min(Math.abs(pitchDiff) / 45.0f, 1.0f);

        float adaptiveYaw = baseSmoothYaw * (0.6f + distanceBasedYaw * 0.7f);
        float adaptivePitch = baseSmoothPitch * (0.6f + distanceBasedPitch * 0.7f);

        adaptiveYaw *= fatigueFactor;
        adaptivePitch *= fatigueFactor;

        if (isHesitating) {
            adaptiveYaw *= 0.5f;
            adaptivePitch *= 0.5f;
        }

        float smoothYawDelta = yawDiff * adaptiveYaw;
        float smoothPitchDelta = pitchDiff * adaptivePitch;

        float momentumDecay = 0.85f + rng.nextFloat(0.06f);
        momentumYaw = momentumYaw * momentumDecay + smoothYawDelta * 0.12f;
        momentumPitch = momentumPitch * momentumDecay + smoothPitchDelta * 0.1f;

        smoothYawDelta += momentumYaw * 0.3f;
        smoothPitchDelta += momentumPitch * 0.3f;

        return new Vector2f(
                current.x + smoothYawDelta,
                current.y + smoothPitchDelta
        );
    }

    private Vector2f applyPhysicalConstraints(Vector2f target, Vector2f previous, ThreadLocalRandom rng) {
        RotationState lastState = history.peekLast();
        if (lastState == null) return target;

        float currentDeltaYaw = MathHelper.wrapDegrees(target.x - previous.x);
        float currentDeltaPitch = target.y - previous.y;

        float currentAccelYaw = currentDeltaYaw - lastState.deltaYaw;
        float currentAccelPitch = currentDeltaPitch - lastState.deltaPitch;

        float currentJerkYaw = currentAccelYaw - lastState.accelYaw;
        float currentJerkPitch = currentAccelPitch - lastState.accelPitch;

        float jerkLimitYaw = 0.4f + rng.nextFloat(0.2f);
        float jerkLimitPitch = 0.3f + rng.nextFloat(0.15f);

        if (Math.abs(currentJerkYaw) > jerkLimitYaw) {
            float clampedJerk = Math.copySign(jerkLimitYaw, currentJerkYaw);
            clampedJerk *= (0.9f + rng.nextFloat(0.1f));
            currentAccelYaw = lastState.accelYaw + clampedJerk;
            currentDeltaYaw = lastState.deltaYaw + currentAccelYaw;
        }

        if (Math.abs(currentJerkPitch) > jerkLimitPitch) {
            float clampedJerk = Math.copySign(jerkLimitPitch, currentJerkPitch);
            clampedJerk *= (0.9f + rng.nextFloat(0.1f));
            currentAccelPitch = lastState.accelPitch + clampedJerk;
            currentDeltaPitch = lastState.deltaPitch + currentAccelPitch;
        }

        float newYaw = previous.x + currentDeltaYaw;
        float newPitch = previous.y + currentDeltaPitch;

        return new Vector2f(newYaw, newPitch);
    }

    private Vector2f applyGCDWithNaturalError(Vector2f rotation, ThreadLocalRandom rng) {
        float preGcdYaw = rotation.x;
        float preGcdPitch = rotation.y;

        float errorYaw = (rng.nextFloat() - 0.5f) * targetGCD * 0.1f;
        float errorPitch = (rng.nextFloat() - 0.5f) * targetGCD * 0.08f;

        preGcdYaw += errorYaw;
        preGcdPitch += errorPitch;

        float gcdYaw = GCDUtils.getSensitivity(preGcdYaw);
        float gcdPitch = GCDUtils.getSensitivity(preGcdPitch);

        if (rng.nextFloat() < 0.1f) {
            gcdYaw += targetGCD * (rng.nextBoolean() ? 1 : -1) * (rng.nextFloat() * 0.3f);
        }
        if (rng.nextFloat() < 0.08f) {
            gcdPitch += targetGCD * (rng.nextBoolean() ? 1 : -1) * (rng.nextFloat() * 0.25f);
        }

        return new Vector2f(
                GCDUtils.getSensitivity(gcdYaw),
                GCDUtils.getSensitivity(gcdPitch)
        );
    }

    private RotationState recordRotationState(Vector2f rotation, Vector2f previous, long currentTime) {
        RotationState lastState = history.peekLast();

        float deltaYaw = MathHelper.wrapDegrees(rotation.x - previous.x);
        float deltaPitch = rotation.y - previous.y;

        float accelYaw = 0;
        float accelPitch = 0;
        float jerkYaw = 0;
        float jerkPitch = 0;

        if (lastState != null) {
            accelYaw = deltaYaw - lastState.deltaYaw;
            accelPitch = deltaPitch - lastState.deltaPitch;

            jerkYaw = accelYaw - lastState.accelYaw;
            jerkPitch = accelPitch - lastState.accelPitch;
        }

        RotationState newState = new RotationState(
                rotation.x, rotation.y,
                deltaYaw, deltaPitch,
                accelYaw, accelPitch,
                jerkYaw, jerkPitch,
                currentTime
        );

        history.addLast(newState);
        if (history.size() > 100) {
            history.removeFirst();
        }

        return newState;
    }

    private float calculateAdaptiveSpeed(float delta, float min, float max, ThreadLocalRandom rng) {
        float normalized = MathHelper.clamp(delta / 30.0f, 0, 1);

        float baseSpeed = MathHelper.lerp(normalized, min, max);
        float variation = 0.9f + rng.nextFloat(0.2f);

        return baseSpeed * variation;
    }

    private double improvedNoise(double x, double y, double z) {
        int X = (int) Math.floor(x) & 255;
        int Y = (int) Math.floor(y) & 255;
        int Z = (int) Math.floor(z) & 255;

        x -= Math.floor(x);
        y -= Math.floor(y);
        z -= Math.floor(z);

        double u = fade(x);
        double v = fade(y);
        double w = fade(z);

        int A = p[X] + Y;
        int AA = p[A] + Z;
        int AB = p[A + 1] + Z;
        int B = p[X + 1] + Y;
        int BA = p[B] + Z;
        int BB = p[B + 1] + Z;

        return lerp(w, lerp(v, lerp(u, grad(p[AA], x, y, z),
                                grad(p[BA], x - 1, y, z)),
                        lerp(u, grad(p[AB], x, y - 1, z),
                                grad(p[BB], x - 1, y - 1, z))),
                lerp(v, lerp(u, grad(p[AA + 1], x, y, z - 1),
                                grad(p[BA + 1], x - 1, y, z - 1)),
                        lerp(u, grad(p[AB + 1], x, y - 1, z - 1),
                                grad(p[BB + 1], x - 1, y - 1, z - 1))));
    }

    private double fade(double t) {
        return t * t * t * (t * (t * 6 - 15) + 10);
    }

    private double lerp(double t, double a, double b) {
        return a + t * (b - a);
    }

    private double grad(int hash, double x, double y, double z) {
        int h = hash & 15;
        double u = h < 8 ? x : y;
        double v = h < 4 ? y : h == 12 || h == 14 ? x : z;
        return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
    }

    private static final int[] p = new int[512];

    static {
        int[] permutation = {151, 160, 137, 91, 90, 15, 131, 13, 201, 95, 96, 53, 194, 233, 7, 225, 140, 36, 103, 30, 69, 142,
                8, 99, 37, 240, 21, 10, 23, 190, 6, 148, 247, 120, 234, 75, 0, 26, 197, 62, 94, 252, 219, 203, 117, 35, 11, 32, 57, 177, 33,
                88, 237, 149, 56, 87, 174, 20, 125, 136, 171, 168, 68, 175, 74, 165, 71, 134, 139, 48, 27, 166, 77, 146, 158, 231, 83, 111,
                229, 122, 60, 211, 133, 230, 220, 105, 92, 41, 55, 46, 245, 40, 244, 102, 143, 54, 65, 25, 63, 161, 1, 216, 80, 73, 209, 76,
                132, 187, 208, 89, 18, 169, 200, 196, 135, 130, 116, 188, 159, 86, 164, 100, 109, 198, 173, 186, 3, 64, 52, 217, 226, 250,
                124, 123, 5, 202, 38, 147, 118, 126, 255, 82, 85, 212, 207, 206, 59, 227, 47, 16, 58, 17, 182, 189, 28, 42, 223, 183, 170,
                213, 119, 248, 152, 2, 44, 154, 163, 70, 221, 153, 101, 155, 167, 43, 172, 9, 129, 22, 39, 253, 19, 98, 108, 110, 79, 113,
                224, 232, 178, 185, 112, 104, 218, 246, 97, 228, 251, 34, 242, 193, 238, 210, 144, 12, 191, 179, 162, 241, 81, 51, 145,
                235, 249, 14, 239, 107, 49, 192, 214, 31, 181, 199, 106, 157, 184, 84, 204, 176, 115, 121, 50, 45, 127, 4, 150, 254, 138,
                236, 205, 93, 222, 114, 67, 29, 24, 72, 243, 141, 128, 195, 78, 66, 215, 61, 156, 180};

        for (int i = 0; i < 256; i++) {
            p[256 + i] = p[i] = permutation[i];
        }
    }

    private void resetState() {
        isInitialized = false;
        gcdInitialized = false;
        history.clear();
        ticksInSequence = 0;
        fatigueAccumulator = 0;
        momentumYaw = 0;
        momentumPitch = 0;
        isHesitating = false;
        perlinOffsetX = 0;
        perlinOffsetY = 0;
    }

    @Override
    public void onEnable(AuraNew parentModule) {
        resetState();
    }

    @Override
    public void onDisable(AuraNew parentModule) {
        resetState();
    }
}