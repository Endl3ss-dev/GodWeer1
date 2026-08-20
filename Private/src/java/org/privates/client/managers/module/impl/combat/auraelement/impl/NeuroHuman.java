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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class NeuroHuman extends AbstractRotationElement {

    private long sessionStart = 0;
    private long lastAttackTime = 0;
    private long lastParameterShift = 0;
    private final long lastMicroCorrection = 0;
    private final long lastBreathSync = 0;
    private final long lastChaosBurst = 0;
    private final long lastMemorySync = 0;
    private final long lastDriftCorrection = 0;
    private final long lastSpiralReset = 0;
    private final long lastJitterBurst = 0;
    private final long lastVerticalOscillation = 0;
    private long lastTargetingError = 0;
    private final long lastDeflectionTrigger = 0;
    private long lastPostFlick = 0;

    private float spiralPhase = 0f;
    private float spiralSpeed = 0.047f;
    private final float spiralRadius = 0.38f;
    private final float spiralVerticalFactor = 0.67f;

    private float jitterPhase = 0f;
    private final float jitterSpeed = 0.19f;
    private float jitterIntensity = 0.21f;

    private float breathPhase = 0f;
    private final float breathSpeed = 0.0018f;
    private final float breathIntensity = 0.07f;

    private float driftPhaseX = 0f;
    private float driftPhaseY = 0f;
    private final float driftSpeedX = 0.023f;
    private final float driftSpeedY = 0.019f;
    private float driftIntensity = 0.11f;

    private float chaosPhase = 0f;
    private final float chaosSpeed = 0.17f;
    private final float chaosIntensity = 0.08f;

    private float microShakePhase = 0f;
    private final float microShakeSpeed = 0.31f;
    private final float microShakeIntensity = 0.04f;

    private float targetingErrorX = 0f;
    private float targetingErrorY = 0f;

    private float memoryYaw = 0f;
    private float memoryPitch = 0f;
    private final float memoryWeight = 0.11f;

    private boolean postFlickActive = false;
    private float postFlickYawOffset = 0f;
    private final float postFlickPitchBase = -83.9f;
    private final float postFlickOscillation = 5.7f;

    private final boolean deflectionActive = false;
    private final float deflectionProgress = 0f;
    private final Vector3d deflectionTarget = null;

    private final List<Float> recentSpiralSpeeds = new ArrayList<>();
    private final List<Float> recentRadii = new ArrayList<>();
    private final List<Float> recentJitterIntensities = new ArrayList<>();

    private int tickCounter = 0;
    private int microTick9 = 0;
    private int microTick14 = 0;
    private int microTick23 = 0;
    private int microTick37 = 0;
    private int microTick41 = 0;

    @Override
    public Vector2f updateRotation(AuraNew aura, LivingEntity target, Minecraft mc, Vector2f current) {
        if (target == null || mc.player == null) return current;

        tickCounter++;
        long now = System.currentTimeMillis();

        if (sessionStart == 0) {
            sessionStart = now;
            initializeChaos();
        }

        if (postFlickActive && now - lastPostFlick < 320) {
            float elapsed = now - lastPostFlick;
            float y = current.x + postFlickYawOffset;
            float p = postFlickPitchBase + (float) Math.sin(elapsed * 0.031) * postFlickOscillation;
            send(y, p, aura);
            return new Vector2f(y, p);
        }
        if (postFlickActive && now - lastPostFlick > 520) postFlickActive = false;

        updatePhases(now);
        updateMicroCorrections(tickCounter);
        updateParameterDrift(now);
        updateTargetingError(now);

        Vector3d aim = calculateComplexAim(target, current, mc);

        float ty = calculateYaw(aim, mc.player.getEyePosition(1f));
        float tp = calculatePitch(aim, mc.player.getEyePosition(1f));

        float yd = MathHelper.wrapDegrees(ty - current.x);
        float pd = tp - current.y;

        float speed = calculateDynamicSpeed(now, yd);

        float ny = current.x + yd * speed;
        float np = current.y + pd * speed * 0.78f;

        applyMemoryCorrection(ny, np, current);
        applyFinalMicros(ny, np, tickCounter);

        ny = GCDUtils.getSensitivity(ny);
        np = GCDUtils.getSensitivity(np);
        np = MathHelper.clamp(np, -89.9f, 89.9f);

        send(ny, np, aura);
        return new Vector2f(ny, np);
    }

    private void initializeChaos() {
        spiralPhase = ThreadLocalRandom.current().nextFloat() * 6.28f;
        jitterPhase = ThreadLocalRandom.current().nextFloat() * 6.28f;
        breathPhase = ThreadLocalRandom.current().nextFloat() * 6.28f;
        driftPhaseX = ThreadLocalRandom.current().nextFloat() * 6.28f;
        driftPhaseY = ThreadLocalRandom.current().nextFloat() * 6.28f;
        chaosPhase = ThreadLocalRandom.current().nextFloat() * 6.28f;
        microShakePhase = ThreadLocalRandom.current().nextFloat() * 6.28f;
    }

    private void updatePhases(long now) {
        spiralPhase += spiralSpeed + (tickCounter % 7 == 0 ? 0.0007f : 0f);
        jitterPhase += jitterSpeed;
        breathPhase += breathSpeed;
        driftPhaseX += driftSpeedX;
        driftPhaseY += driftSpeedY;
        chaosPhase += chaosSpeed;
        microShakePhase += microShakeSpeed;
    }

    private void updateMicroCorrections(int tick) {
        if (tick % 9 == 4) {
            microTick9++;
        }
        if (tick % 14 == 7) {
            microTick14++;
        }
        if (tick % 23 == 11) {
            microTick23++;
        }
        if (tick % 37 == 19) {
            microTick37++;
        }
        if (tick % 41 == 22) {
            microTick41++;
        }
    }

    private void updateParameterDrift(long now) {
        if (now - lastParameterShift > 2847 + (now & 1023)) {
            lastParameterShift = now;
            spiralSpeed += ThreadLocalRandom.current().nextFloat() * 0.0008f - 0.0004f;
            jitterIntensity += ThreadLocalRandom.current().nextFloat() * 0.03f - 0.015f;
            driftIntensity += ThreadLocalRandom.current().nextFloat() * 0.02f - 0.01f;
            spiralSpeed = MathHelper.clamp(spiralSpeed, 0.03f, 0.09f);
            jitterIntensity = MathHelper.clamp(jitterIntensity, 0.12f, 0.38f);
        }
    }

    private void updateTargetingError(long now) {
        if (now - lastTargetingError > 180 + ThreadLocalRandom.current().nextInt(240)) {
            targetingErrorX = ThreadLocalRandom.current().nextFloat() * 0.08f - 0.04f;
            targetingErrorY = ThreadLocalRandom.current().nextFloat() * 0.06f - 0.03f;
            lastTargetingError = now;
        }
    }

    private Vector3d calculateComplexAim(LivingEntity t, Vector2f cur, Minecraft mc) {
        // БАЗА — жёстко зажата между 0.62 и 0.91 от высоты хитбокса
        double maxAimHeight = t.getHeight() * 0.91;  // выше этого — никогда
        double minAimHeight = t.getHeight() * 0.62;  // ниже — редко, только при лаге

        double breathOffset = Math.sin(breathPhase) * breathIntensity * 0.07;
        double targetError = targetingErrorY;

        double rawHeight = t.getEyeHeight() * 0.69 + breathOffset + targetError;
        double finalHeightOffset = MathHelper.clamp(rawHeight, minAimHeight, maxAimHeight);

        Vector3d base = t.getPositionVec()
                .add(0, finalHeightOffset, 0)
                .add(t.getMotion().scale(4.3));

        Vector3d right = Vector3d.fromPitchYaw(0, cur.x - 90);
        Vector3d up = Vector3d.fromPitchYaw(cur.y - 90, cur.x);

        // Все остальные оффсеты — как были
        Vector3d spiral = right.scale(Math.cos(spiralPhase) * spiralRadius)
                .add(up.scale(Math.sin(spiralPhase) * spiralRadius * spiralVerticalFactor));

        Vector3d jitter = right.scale((Math.sin(jitterPhase * 1.7) + Math.cos(jitterPhase * 0.9)) * jitterIntensity)
                .add(up.scale((Math.cos(jitterPhase * 1.7) + Math.sin(jitterPhase * 0.8)) * jitterIntensity * 0.67f));

        Vector3d drift = right.scale(Math.sin(driftPhaseX) * driftIntensity)
                .add(up.scale(Math.cos(driftPhaseY) * driftIntensity * 0.6f));

        Vector3d chaos = right.scale(Math.sin(chaosPhase * 2.1) * chaosIntensity)
                .add(up.scale(Math.cos(chaosPhase * 1.6) * chaosIntensity * 0.7f));

        Vector3d micro = right.scale(Math.sin(microShakePhase) * microShakeIntensity)
                .add(up.scale(Math.cos(microShakePhase * 1.3) * microShakeIntensity * 0.7f));

        return base.add(spiral).add(jitter).add(drift).add(chaos).add(micro);
    }

    private float calculateDynamicSpeed(long now, float yawDiff) {
        float base = now - lastAttackTime < 380 ? 0.17f : 0.38f;
        if (Math.abs(yawDiff) > 80) base = 0.61f;
        return base;
    }

    private void applyMemoryCorrection(float ny, float np, Vector2f cur) {
        if (tickCounter % 37 == 19) {
            memoryYaw = cur.x;
            memoryPitch = cur.y;
        }
        if (tickCounter % 41 == 22) {
            ny += (memoryYaw - ny) * memoryWeight;
            np += (memoryPitch - np) * memoryWeight * 0.9f;
        }
    }

    private void applyFinalMicros(float ny, float np, int tick) {
        if (tick % 9 == 4) {
            ny += 0.47f;
            np += 0.29f;
        }
        if (tick % 14 == 7) {
            ny -= 0.43f;
            np -= 0.27f;
        }
        if (tick % 23 == 11) {
            ny += 0.38f;
            np += 0.24f;
        }
        if (tick % 37 == 19) {
            ny -= 0.31f;
            np -= 0.19f;
        }
    }

    private float calculateYaw(Vector3d aim, Vector3d eye) {
        double dx = aim.x - eye.x;
        double dz = aim.z - eye.z;
        return (float) (Math.atan2(dz, dx) * 180 / Math.PI - 90);
    }

    private float calculatePitch(Vector3d aim, Vector3d eye) {
        double dx = aim.x - eye.x;
        double dy = aim.y - eye.y;
        double dz = aim.z - eye.z;
        double dist = Math.sqrt(dx * dx + dz * dz);
        return (float) (-Math.atan2(dy, dist) * 180 / Math.PI);
    }

    private void send(float y, float p, AuraNew a) {
        a.setLerpRotation(new Vector2f(y, p));
        applyRotation(new Rotation(y, p), 180, 90, 0, 5);
    }

    public void onAttack(LivingEntity t) {
        lastAttackTime = System.currentTimeMillis();
        lastPostFlick = lastAttackTime;
        if ((lastAttackTime & 7) < 3) {
            postFlickActive = true;
            postFlickYawOffset = ((lastAttackTime & 1) == 0 ? 12.1f : -11.4f);
        }
    }

    @Override
    public void onEnable(AuraNew a) {
        tickCounter = 0;
        sessionStart = 0;
        lastAttackTime = 0;
        postFlickActive = false;
        initializeChaos();
    }
}