package org.privates.client.managers.module.impl.combat.auraelement.impl;

import com.adl.nativeprotect.Native;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import org.privates.client.managers.component.impl.rotation.Rotation;
import org.privates.client.managers.module.impl.combat.AuraNew;
import org.privates.client.managers.module.impl.combat.auraelement.AbstractRotationElement;
import org.privates.client.utils.other.GCDUtils;

import java.util.*;


public class RepetitiveRot extends AbstractRotationElement {

    private enum BodyPart {
        HEAD(0.9),
        CHEST(0.5),
        LEGS(0.1);

        private final double heightModifier;

        BodyPart(double heightModifier) {
            this.heightModifier = heightModifier;
        }

        public Vector3d getOffset(LivingEntity entity) {
            return new Vector3d(0, entity.getHeight() * this.heightModifier, 0);
        }
    }

    private static final int ROTATION_HISTORY_SIZE = 20;
    private static final int ENEMY_AIM_HISTORY_SIZE = 8;
    private static final int REPETITION_THRESHOLD = 4;
    private static final long MULTIPOINT_SWITCH_INTERVAL = 600L;
    private static final float AIM_TRANSITION_SPEED = 0.15f;
    private static final float ROTATION_COPY_STRENGTH = 0.85f;
    private static final float RANDOMIZATION_FACTOR = 0.6f;
    private static final float CONVERGENCE_FACTOR = 0.35f;

    private final Deque<RotationSnapshot> targetRotationHistory = new ArrayDeque<>();
    private final Deque<BodyPart> enemyAimHistory = new ArrayDeque<>();
    private final Random random = new Random();

    private boolean isMultiPointActive = false;
    private final List<Vector3d> multiPointOffsets = new ArrayList<>();
    private int multiPointIndex = 0;
    private long lastMultiPointSwitchTime = 0;

    private Vector3d currentAimPoint = null;
    private Vector2f previousRotation = null;

    @Override
    @Native
    public Vector2f updateRotation(AuraNew parentModule, LivingEntity target, Minecraft mc, Vector2f currentLerpRotation) {
        if (target == null || mc.player == null || mc.world == null) return currentLerpRotation;

        long currentTime = System.currentTimeMillis();

        if (!(target instanceof PlayerEntity playerTarget)) {
            return getFallbackRotation(target, mc, currentLerpRotation);
        }

        updateTargetRotationTracking(playerTarget, currentTime);
        updateEnemyAimHistory(mc);
        decideRotationStrategy(target);

        Vector3d goalAimPoint;

        if (isMultiPointActive) {
            goalAimPoint = getNextMultiPoint(target, currentTime);
        } else {
            BodyPart targetPart = enemyAimHistory.isEmpty() ? BodyPart.CHEST : enemyAimHistory.peekLast();
            goalAimPoint = target.getPositionVec().add(targetPart.getOffset(target));
        }

        if (currentAimPoint == null) {
            currentAimPoint = target.getPositionVec().add(0, target.getEyeHeight() / 2, 0);
        }

        currentAimPoint = currentAimPoint.lerp(goalAimPoint, AIM_TRANSITION_SPEED);

        Vector2f mimickedRotation = calculateMimickedRotation(target, mc, currentAimPoint);

        if (mimickedRotation == null) {
            return currentLerpRotation;
        }


        applyRotation(new Rotation(mimickedRotation.x, mimickedRotation.y), 180, 30, 0, 4);
        parentModule.setLerpRotation(mimickedRotation);

        return mimickedRotation;
    }

    @Native
    private void updateTargetRotationTracking(PlayerEntity target, long currentTime) {
        RotationSnapshot snapshot = new RotationSnapshot(
                target.rotationYaw,
                target.rotationPitch,
                target.rotationYawHead,
                currentTime
        );

        targetRotationHistory.addLast(snapshot);
        while (targetRotationHistory.size() > ROTATION_HISTORY_SIZE) {
            targetRotationHistory.removeFirst();
        }
    }

    // перезапустить? и чё делать дай интернет почекаю узнаю
    private Vector2f calculateMimickedRotation(LivingEntity target, Minecraft mc, Vector3d aimPoint) {
        Vector3d playerEyes = mc.player.getEyePosition(1.0f);
        Vector3d direction = aimPoint.subtract(playerEyes);

        float perfectYaw = (float) Math.toDegrees(Math.atan2(direction.z, direction.x)) - 90.0f;
        float perfectPitch = (float) -Math.toDegrees(Math.atan2(direction.y, Math.sqrt(direction.x * direction.x + direction.z * direction.z)));

        if (targetRotationHistory.size() < 2) {
            return getFallbackRotation(target, mc, new Vector2f(perfectYaw, perfectPitch));
        }

        if (previousRotation == null) {
            previousRotation = new Vector2f(perfectYaw, perfectPitch);
        }

        Iterator<RotationSnapshot> iterator = targetRotationHistory.descendingIterator();
        RotationSnapshot latest = iterator.next();
        RotationSnapshot previous = iterator.next();

        float targetYawDelta = normalizeAngle(latest.yaw - previous.yaw);
        float targetPitchDelta = latest.pitch - previous.pitch;
        float targetYawHeadDelta = normalizeAngle(latest.yawHead - previous.yawHead);

        float avgYawVelocity = (targetYawDelta + targetYawHeadDelta) / 2.0f;

        float copiedYawDelta = avgYawVelocity * ROTATION_COPY_STRENGTH;
        float copiedPitchDelta = targetPitchDelta * ROTATION_COPY_STRENGTH;

        float randomYawNoise = (random.nextFloat() - 0.5f) * RANDOMIZATION_FACTOR;
        float randomPitchNoise = (random.nextFloat() - 0.5f) * RANDOMIZATION_FACTOR * 0.5f;

        float mimickedYaw = previousRotation.x + copiedYawDelta + randomYawNoise;
        float mimickedPitch = previousRotation.y + copiedPitchDelta + randomPitchNoise;

        float yawCorrection = normalizeAngle(perfectYaw - mimickedYaw) * CONVERGENCE_FACTOR;
        float pitchCorrection = (perfectPitch - mimickedPitch) * CONVERGENCE_FACTOR;

        float finalYaw = mimickedYaw + yawCorrection;
        float finalPitch = mimickedPitch + pitchCorrection;

        finalPitch = MathHelper.clamp(finalPitch, -90.0f, 90.0f);

        previousRotation = new Vector2f(finalYaw, finalPitch);

        return new Vector2f(
                GCDUtils.getSensitivity(finalYaw),
                GCDUtils.getSensitivity(finalPitch)
        );
    }

    private void updateEnemyAimHistory(Minecraft mc) {
        for (PlayerEntity enemy : mc.world.getPlayers()) {
            if (enemy == mc.player || enemy.isSpectator()) continue;

            Vector3d enemyEyePos = enemy.getEyePosition(1.0f);
            Vector3d lookVec = enemy.getLook(1.0f);
            Vector3d rayEnd = enemyEyePos.add(lookVec.scale(8.0));

            Optional<Vector3d> hitVec = mc.player.getBoundingBox().rayTrace(enemyEyePos, rayEnd);

            if (hitVec.isPresent()) {
                double hitY = hitVec.get().y - mc.player.getPosY();
                double playerHeight = mc.player.getHeight();

                BodyPart aimedPart;
                if (hitY > playerHeight * 0.75) {
                    aimedPart = BodyPart.HEAD;
                } else if (hitY > playerHeight * 0.3) {
                    aimedPart = BodyPart.CHEST;
                } else {
                    aimedPart = BodyPart.LEGS;
                }

                enemyAimHistory.addLast(aimedPart);
                while (enemyAimHistory.size() > ENEMY_AIM_HISTORY_SIZE) {
                    enemyAimHistory.removeFirst();
                }
                return;
            }
        }
    }

    private void decideRotationStrategy(LivingEntity target) {
        if (enemyAimHistory.size() < REPETITION_THRESHOLD) {
            if (isMultiPointActive) {
                isMultiPointActive = false;
            }
            return;
        }

        BodyPart firstPart = enemyAimHistory.peekLast();
        boolean isRepetitive = true;
        int i = 0;
        for (BodyPart part : (Iterable<BodyPart>) enemyAimHistory::descendingIterator) {
            if (i++ >= REPETITION_THRESHOLD) break;
            if (part != firstPart) {
                isRepetitive = false;
                break;
            }
        }

        if (isRepetitive && !isMultiPointActive) {
            isMultiPointActive = true;
            initializeMultiPointTargets(target);
        } else if (!isRepetitive && isMultiPointActive) {
            isMultiPointActive = false;
        }
    }

    private void initializeMultiPointTargets(LivingEntity target) {
        multiPointOffsets.clear();

        multiPointOffsets.add(new Vector3d(0, target.getHeight() * 0.9, 0));
        multiPointOffsets.add(new Vector3d(0, target.getHeight() * 0.65, 0));
        multiPointOffsets.add(new Vector3d(0, target.getHeight() * 0.45, 0));
        multiPointOffsets.add(new Vector3d(0, target.getHeight() * 0.2, 0));

        for (int i = 0; i < 4; i++) {
            Vector3d baseOffset = multiPointOffsets.get(i);
            double rX = (random.nextDouble() - 0.5) * 0.3;
            double rY = (random.nextDouble() - 0.5) * 0.3;
            double rZ = (random.nextDouble() - 0.5) * 0.3;
            multiPointOffsets.add(baseOffset.add(rX, rY, rZ));
        }

        Collections.shuffle(multiPointOffsets);
        multiPointIndex = 0;
    }

    private Vector3d getNextMultiPoint(LivingEntity target, long currentTime) {
        if (multiPointOffsets.isEmpty()) {
            initializeMultiPointTargets(target);
        }

        if (currentTime - lastMultiPointSwitchTime > MULTIPOINT_SWITCH_INTERVAL) {
            multiPointIndex = (multiPointIndex + 1) % multiPointOffsets.size();
            lastMultiPointSwitchTime = currentTime;
        }

        Vector3d offset = multiPointOffsets.get(multiPointIndex);
        return target.getPositionVec().add(offset);
    }

    private Vector2f getFallbackRotation(LivingEntity target, Minecraft mc, Vector2f currentLerpRotation) {
        Vector3d playerEyes = mc.player.getEyePosition(1.0f);
        Vector3d targetPos = target.getPositionVec().add(0, target.getEyeHeight() / 2, 0);
        Vector3d direction = targetPos.subtract(playerEyes);

        float yaw = (float) Math.toDegrees(Math.atan2(direction.z, direction.x)) - 90.0f;
        float pitch = (float) -Math.toDegrees(Math.atan2(direction.y, Math.sqrt(direction.x * direction.x + direction.z * direction.z)));

        return new Vector2f(
                GCDUtils.getSensitivity(yaw),
                GCDUtils.getSensitivity(MathHelper.clamp(pitch, -89.9f, 89.9f))
        );
    }

    private float normalizeAngle(float angle) {
        while (angle > 180.0f) angle -= 360.0f;
        while (angle < -180.0f) angle += 360.0f;
        return angle;
    }

    @Override
    public void onEnable(AuraNew parentModule) {
        reset();
    }

    @Override
    public void onDisable(AuraNew parentModule) {
        super.onDisable(parentModule);
        reset();
    }

    private void reset() {
        targetRotationHistory.clear();
        enemyAimHistory.clear();
        isMultiPointActive = false;
        multiPointOffsets.clear();
        multiPointIndex = 0;
        lastMultiPointSwitchTime = 0;
        currentAimPoint = null;
        previousRotation = null;
    }

    private record RotationSnapshot(float yaw, float pitch, float yawHead, long timestamp) {
    }
}