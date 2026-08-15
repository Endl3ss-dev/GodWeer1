package antileak.base.client.modules.impl.combat.components.rotations;

import antileak.base.api.QClient;
import antileak.base.api.storages.implement.RotationStorage;
import antileak.base.api.utils.rotate.Rotation;
import antileak.base.client.modules.impl.combat.Aura;
import antileak.base.client.modules.impl.combat.components.RotationsSystem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class SpookyTimeRots extends RotationsSystem implements QClient {

    private LivingEntity trackedTarget;

    private float currentYaw;
    private float currentPitch;

    private float velocityYaw;
    private float velocityPitch;

    private float swayValue;
    private float swayTarget;

    private double aimPointX;
    private double aimPointY;
    private double aimPointZ;

    private double swayPhaseOffset;

    private int hitPhase;
    private int hitTimer;
    private float pitchBeforeHit;

    private long firstSeenTime;
    private int reactionMs;
    private boolean reactionComplete;

    private float lastSentYaw;
    private float lastSentPitch;

    private float smoothYaw;
    private float smoothPitch;

    private boolean lostTarget;
    private long lostTargetTime;
    private float holdYaw;
    private float holdPitch;
    private float naturalYaw;
    private float naturalPitch;

    private static final long HOLD_MS = 50L;
    private static final long RETURN_MS = 500L;
    private static final float MAX_PITCH_DELTA = 18.0f;
    private float prevPitch;

    private boolean focusRotation;

    public void reset() {
        trackedTarget = null;
        velocityYaw = velocityPitch = 0.0F;
        aimPointX = aimPointY = aimPointZ = 0.0;
        hitPhase = hitTimer = 0;
        swayPhaseOffset = Math.random() * 12000.0;
        lostTarget = false;
        lostTargetTime = 0L;

        if (RotationStorage.instance != null) {
            RotationStorage.instance.stopRotation();
        }

        if (mc.player != null) {
            currentYaw = mc.player.getYaw();
            currentPitch = mc.player.getPitch();
            lastSentYaw = currentYaw;
            lastSentPitch = currentPitch;
            smoothYaw = currentYaw;
            smoothPitch = currentPitch;
            prevPitch = currentPitch;
        } else {
            currentYaw = currentPitch = 0.0F;
            lastSentYaw = lastSentPitch = 0.0F;
            smoothYaw = smoothPitch = 0.0F;
            prevPitch = 0.0F;
        }
    }

    private static float spEase(float t) {
        t = MathHelper.clamp(t, 0.0F, 1.0F);
        return t * (0.5F + 0.5F * t);
    }

    private float[] spAngleStep(float wantYaw, float wantPitch) {
        float rawDeltaYaw = MathHelper.wrapDegrees(wantYaw - currentYaw);
        float rawDeltaPitch = wantPitch - currentPitch;

        float yawLimit = Math.min(Math.abs(rawDeltaYaw), 74.0F + (float) (Math.random() * 1.03));
        float pitchLimit = Math.min(Math.abs(rawDeltaPitch), 32.33F);

        float newYaw = stepAxis(currentYaw, rawDeltaYaw, yawLimit);
        float newPitch = stepAxis(currentPitch, rawDeltaPitch, pitchLimit);

        newPitch = MathHelper.clamp(newPitch, -89.0F, 90.0F);
        return new float[]{newYaw, newPitch};
    }

    private float stepAxis(float current, float delta, float limit) {
        boolean reached = Math.abs(delta) >= limit;
        float maxStep = reached
                ? 65.0F + (float) (Math.random() * 35.0F)
                : 7.7F + (float) (Math.random() * 4.4F);

        float total = Math.abs(delta);
        if (total < 1e-4F) return current;

        float scale = Math.min(total, maxStep) / total;
        if (!reached) scale = spEase(scale);

        return current + delta * scale;
    }

    private float calcSway() {
        if (Math.random() < 0.025) {
            swayTarget = ((float) Math.random() - 0.5f) * 1.25f;
        }

        swayValue += (swayTarget - swayValue) * 0.06f;

        long ms = (long) ((System.currentTimeMillis() + (long) swayPhaseOffset) % 12000L);
        double angle = (ms / 1200.0) * Math.PI * 2.0;

        return (float) (Math.sin(angle) * swayValue);
    }

    private float calcGcd() {
        double s = mc.options.getMouseSensitivity().getValue() * 0.6 + 0.2;
        return (float) (s * s * s * 1.2);
    }

    private void applyGcd(float[] out) {
        float gcd = calcGcd();

        out[0] = lastSentYaw +
                Math.round((out[0] - lastSentYaw) / gcd) * gcd;

        out[1] = lastSentPitch +
                Math.round((out[1] - lastSentPitch) / gcd) * gcd;
    }

    private void pickAimPoint(LivingEntity e) {
        Box bb = e.getBoundingBox();
        double w = bb.maxX - bb.minX;
        double h = bb.maxY - bb.minY;
        double d = bb.maxZ - bb.minZ;
        aimPointX = (Math.random() - 0.5) * w * 0.12;
        aimPointY = (Math.random() - 0.5) * h * 0.11;
        aimPointZ = (Math.random() - 0.5) * d * 0.12;
    }

    public void onAttack() {
        hitPhase = 1;
        hitTimer = 0;
        pitchBeforeHit = currentPitch;
    }

    private float measureAngle(LivingEntity e) {
        if (mc.player == null) return 0.0F;
        Vec3d eyes = mc.player.getEyePos();
        Vec3d mid = e.getBoundingBox().getCenter();
        Vec3d delta = mid.subtract(eyes);
        float needYaw = (float) Math.toDegrees(Math.atan2(delta.z, delta.x)) - 90.0F;
        float needPitch = (float) -Math.toDegrees(Math.atan2(delta.y, delta.horizontalLength()));
        return Math.abs(MathHelper.wrapDegrees(needYaw - mc.player.getYaw()))
                + Math.abs(needPitch - mc.player.getPitch());
    }

    private int computeReaction(float angle) {
        if (angle > 130.0F) return 140 + (int) (Math.random() * 90);
        if (angle > 70.0F) return 90 + (int) (Math.random() * 60);
        if (angle > 30.0F) return 45 + (int) (Math.random() * 35);
        return 12 + (int) (Math.random() * 20);
    }

    private float smoothStep(float x) {
        x = MathHelper.clamp(x, 0.0F, 1.0F);
        return x * x * (3.0F - 2.0F * x);
    }

    private float accelCurve(float x) {
        x = MathHelper.clamp(x, 0.0F, 1.0F);
        return 1.0F - (1.0F - x) * (1.0F - x);
    }

    private void sendRotation(float y, float p, float yawSpeed, float pitchSpeed,
                              float yawReturnSpeed, float pitchReturnSpeed) {
        lastSentYaw = y;
        lastSentPitch = p;
        RotationStorage.update(new Rotation(y, p),
                yawSpeed, pitchSpeed,
                yawReturnSpeed, pitchReturnSpeed,
                1, 1, Aura.clientLook.isState());
    }

    private boolean tickLostTarget() {
        if (!lostTarget) return false;

        long elapsed = System.currentTimeMillis() - lostTargetTime;

        if (elapsed < HOLD_MS) {
            float[] out = {holdYaw, holdPitch};
            applyGcd(out);
            sendRotation(out[0], out[1], 135f, 135f, 135f, 135f);
            return true;
        }

        long returnElapsed = elapsed - HOLD_MS;
        if (returnElapsed < RETURN_MS) {
            float t = (float) returnElapsed / RETURN_MS;
            float eased = smoothStep(t);
            float y = holdYaw + MathHelper.wrapDegrees(naturalYaw - holdYaw) * eased;
            float p = holdPitch + (naturalPitch - holdPitch) * eased;
            p = MathHelper.clamp(p, -89.0F, 89.0F);

            float[] out = {y, p};
            applyGcd(out);
            sendRotation(out[0], out[1], 105f, 90f, 105f, 90f);
            return true;
        }

        lostTarget = false;
        return false;
    }

    @Override
    public void updateRotations(LivingEntity target) {
        if (mc.player == null) return;

        if (target == null) {
            if (!lostTarget && trackedTarget != null) {
                lostTarget = true;
                lostTargetTime = System.currentTimeMillis();
                holdYaw = lastSentYaw;
                holdPitch = lastSentPitch;
                trackedTarget = null;
            }
            tickLostTarget();
            return;
        }

        lostTarget = false;

        if (trackedTarget != target) {
            trackedTarget = target;
            currentYaw = mc.player.getYaw();
            currentPitch = mc.player.getPitch();
            lastSentYaw = currentYaw;
            lastSentPitch = currentPitch;
            smoothYaw = currentYaw;
            smoothPitch = currentPitch;
            prevPitch = currentPitch;
            velocityYaw = velocityPitch = 0.0F;
            naturalYaw = currentYaw;
            naturalPitch = currentPitch;

            pickAimPoint(target);
            hitPhase = hitTimer = 0;
            swayPhaseOffset = Math.random() * 12000.0;

            float angleDiff = measureAngle(target);
            reactionMs = computeReaction(angleDiff);
            firstSeenTime = System.currentTimeMillis();
            reactionComplete = false;
        }

        if (!reactionComplete) {
            long elapsed = System.currentTimeMillis() - firstSeenTime;
            if (elapsed < reactionMs) {
                float gcd = calcGcd();
                float jY = ((float) Math.random() - 0.5F) * 0.22F;
                float jP = ((float) Math.random() - 0.5F) * 0.14F;
                float outY = lastSentYaw + jY;
                float outP = MathHelper.clamp(lastSentPitch + jP, -89.0F, 89.0F);
                outY -= (outY - lastSentYaw) % gcd;
                outP -= (outP - lastSentPitch) % gcd;
                sendRotation(outY, outP, 240f, 180f, 135f, 135f);
                return;
            }
            reactionComplete = true;
        }

        if (hitPhase > 0) {
            hitTimer++;
            int upDuration = 25;
            int downDuration = 20;
            float targetPitchUp = -89.0F;

            if (hitPhase == 1) {
                float t = MathHelper.clamp(hitTimer / (float) upDuration, 0.0F, 1.0F);
                currentPitch = MathHelper.lerp(accelCurve(t), pitchBeforeHit, targetPitchUp);
                if (hitTimer >= upDuration) {
                    hitPhase = 2;
                    hitTimer = 0;
                }
            } else if (hitPhase == 2) {
                float t = MathHelper.clamp(hitTimer / (float) downDuration, 0.0F, 1.0F);
                currentPitch = MathHelper.lerp(smoothStep(t), targetPitchUp, pitchBeforeHit);
                if (hitTimer >= downDuration) {
                    hitPhase = 0;
                    hitTimer = 0;
                }
            }

            prevPitch = currentPitch;

            float outY = currentYaw + calcSway();
            float outP = MathHelper.clamp(currentPitch, -89.0F, 89.0F);
            float[] out = {outY, outP};
            applyGcd(out);
            sendRotation(out[0], out[1], 270f, 210f, 150f, 135f);
            return;
        }

        if (Math.random() < 0.015) pickAimPoint(target);

        Vec3d eyePos = mc.player.getEyePos();
        Vec3d targetCenter = getPredictedPoint(target, target.getBoundingBox().getCenter());

        Vec3d targetVel = new Vec3d(
                target.getX() - target.prevX,
                target.getY() - target.prevY,
                target.getZ() - target.prevZ
        );
        int predictTicks = 2;
        Vec3d predictedCenter = targetCenter.add(targetVel.multiply(predictTicks));
        Vec3d aimPos = predictedCenter.add(aimPointX, aimPointY, aimPointZ);
        Vec3d direction = aimPos.subtract(eyePos);

        float wantYaw = (float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(direction.z, direction.x)) - 90.0);
        float wantPitch = (float) -Math.toDegrees(Math.atan2(direction.y, direction.horizontalLength()));

        float yawDeltaRaw = MathHelper.wrapDegrees(wantYaw - currentYaw);
        float pitchDeltaRaw = wantPitch - currentPitch;

        velocityYaw += yawDeltaRaw * 0.095f;
        velocityPitch += pitchDeltaRaw * 0.075f;

        velocityYaw *= 0.82f;
        velocityPitch *= 0.78f;

        velocityYaw = MathHelper.clamp(velocityYaw, -16.0f, 16.0f);
        velocityPitch = MathHelper.clamp(velocityPitch, -10.0f, 10.0f);

        currentYaw += velocityYaw;
        currentPitch += velocityPitch;
        currentPitch = MathHelper.clamp(currentPitch, prevPitch - MAX_PITCH_DELTA, prevPitch + MAX_PITCH_DELTA);
        prevPitch = currentPitch;

        float yawDelta = Math.abs(MathHelper.wrapDegrees(wantYaw - currentYaw));
        float pitchDelta = Math.abs(wantPitch - currentPitch);

        boolean hardAcquire = yawDelta > 70.0f || (yawDelta > 42.0f && mc.player.squaredDistanceTo(target) < 9.0);

        float yawSpeed = hardAcquire
                ? Math.max(255.0f, Math.min(480.0f, yawDelta * 3.75f))
                : (focusRotation ? 57.0f : 25.5f);

        float pitchSpeed = hardAcquire
                ? Math.max(144.0f, Math.min(285.0f, Math.max(48.0f, pitchDelta * 3.45f)))
                : (focusRotation ? 43.5f : 21.0f);

        float yawReturnSpeed = hardAcquire
                ? yawSpeed
                : (focusRotation ? 66.0f : 36.0f);

        float pitchReturnSpeed = hardAcquire
                ? pitchSpeed
                : (focusRotation ? 54.0f : 30.0f);

        if (hardAcquire) {
            // dataSystem.resetState();
        }

        float sway = calcSway();
        float outY = currentYaw + sway;
        float outP = MathHelper.clamp(currentPitch, -89.0F, 89.0F);

        float[] out = {outY, outP};
        applyGcd(out);
        sendRotation(out[0], out[1], yawSpeed, pitchSpeed, yawReturnSpeed, pitchReturnSpeed);
    }
}
