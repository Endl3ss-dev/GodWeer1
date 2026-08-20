package org.privates.client.managers.component.impl.rotation;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import org.privates.baritone.api.BaritoneAPI;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.component.Component;
import org.privates.client.managers.events.player.MoveInputEvent;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.events.render.Render2DEvent;
import org.privates.client.managers.module.impl.combat.AuraNew;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.player.MoveUtil;
import org.privates.client.utils.rotation.SensUtil;

@Getter
@Setter
@Accessors(fluent = true)
public class RotationComponent extends Component {
    public static RotationComponent getInstance() {
        return Instance.getComponent(RotationComponent.class);
    }

    private RotationTask currentTask = RotationTask.IDLE;
    private float currentYawSpeed;
    private float currentPitchSpeed;
    private float currentYawReturnSpeed;
    private float currentPitchReturnSpeed;
    private int currentPriority;
    private int currentTimeout;
    private int idleTicks;
    private Rotation targetRotation;

    private float pullStrafe = 0f;
    private float pullForward = 0f;
    private long lastPullTime = 0;
    private float pullIntensity = 1f;
    private float prevPullStrafe = 0f;
    private float prevPullForward = 0f;

    private long lastFpsUpdate = 0;
    private int targetFps = 240;

    @EventHandler
    public void onEvent(MoveInputEvent event) {
        if (isBaritonePathing()) {
            return;
        }
        String mode = AuraNew.getInstance().movementCorrection().getValue();

        if (mode.equals("Грубая")) {
            applyGrubayaPull(event);
        } else if (isRotating()) {
            float yaw = MathHelper.wrapDegrees(mc.gameRenderer.getActiveRenderInfo().getYaw());

            if (mode.equals("Незаметная")) {
                MoveUtil.fixMovement(event, yaw);
            } else if (mode.equals("Сфокусированная")) {
                MoveUtil.fixMovement2(event, yaw);
            }
        }
    }

    private void applyGrubayaPull(MoveInputEvent event) {
        LivingEntity target = AuraNew.getInstance().target;

        if (target == null || mc.player == null) {
            resetPull();
            return;
        }

        Vector3d playerPos = mc.player.getPositionVec();
        Vector3d targetPos = target.getPositionVec();

        double dx = targetPos.x - playerPos.x;
        double dz = targetPos.z - playerPos.z;
        double distance = Math.sqrt(dx * dx + dz * dz);

        if (distance < 0.2) {
            resetPull();
            return;
        }

        float originalForward = event.getForward();
        float originalStrafe = event.getStrafe();

        float inputMagnitude = (float) Math.sqrt(originalForward * originalForward + originalStrafe * originalStrafe);

        if (inputMagnitude < 0.01f) {
            resetPull();
            return;
        }

        float yawToTarget = MathHelper.wrapDegrees((float) Math.toDegrees(Math.atan2(dz, dx)) - 90.0F);
        float yaw = mc.player.rotationYaw;

        fixMovementToTarget(event, yaw, yawToTarget);
    }

    public static void fixMovementToTarget(final MoveInputEvent event, float yaw, float target) {
        final float forward = event.getForward();
        final float strafe = event.getStrafe();
        final double angle = MathHelper.wrapDegrees(Math.toDegrees(direction(mc.player.isElytraFlying() ? yaw : target, forward, strafe)));
        if (forward == 0 && strafe == 0) {
            return;
        }
        float closestForward = 0, closestStrafe = 0, closestDifference = Float.MAX_VALUE;
        for (float predictedForward = -1F; predictedForward <= 1F; predictedForward += 1F) {
            for (float predictedStrafe = -1F; predictedStrafe <= 1F; predictedStrafe += 1F) {
                if (predictedStrafe == 0 && predictedForward == 0) continue;
                final double predictedAngle = MathHelper.wrapDegrees(Math.toDegrees(direction(yaw, predictedForward, predictedStrafe)));
                final double difference = Math.abs(angle - predictedAngle);
                if (difference < closestDifference) {
                    closestDifference = (float) difference;
                    closestForward = predictedForward;
                    closestStrafe = predictedStrafe;
                }
            }
        }
        event.setForward(closestForward);
        event.setStrafe(closestStrafe);
    }

    private static double direction(float yaw, double moveForward, double moveStrafe) {
        if (moveForward < 0.0) {
            yaw += 180.0F;
        }

        float forward = 1.0F;
        if (moveForward < 0.0) {
            forward = -0.5F;
        } else if (moveForward > 0.0) {
            forward = 0.5F;
        }

        if (moveStrafe > 0.0) {
            yaw -= 90.0F * forward;
        }

        if (moveStrafe < 0.0) {
            yaw += 90.0F * forward;
        }

        return Math.toRadians(yaw);
    }

    private void resetPull() {
        pullStrafe = (float) Mathf.lerp(pullStrafe, 0f, 0.4f);
        pullForward = (float) Mathf.lerp(pullForward, 0f, 0.4f);
        pullIntensity = (float) Mathf.lerp(pullIntensity, 1f, 0.3f);
    }

    public void fullResetPull() {
        pullStrafe = 0f;
        pullForward = 0f;
        pullIntensity = 1f;
        lastPullTime = 0;
        prevPullStrafe = 0f;
        prevPullForward = 0f;
    }

    public void resetRotation() {
        Rotation targetRotation = new Rotation(FreeLookComponent.getFreeYaw(), FreeLookComponent.getFreePitch());
        if (updateRotation(targetRotation, currentYawReturnSpeed() * 0.6f, currentPitchReturnSpeed() * 0.6f)) {
            stopRotation();
        }
    }

    public void smoothResetRotation() {
        Rotation targetRotation = new Rotation(FreeLookComponent.getFreeYaw(), FreeLookComponent.getFreePitch());

        float fastYawSpeed = currentYawReturnSpeed() * 1.5f;
        float fastPitchSpeed = currentPitchReturnSpeed() * 1.5f;

        if (updateRotation(targetRotation, fastYawSpeed, fastPitchSpeed)) {
            stopRotation();
        }
    }

    @EventHandler
    public void onEvent(UpdateEvent event) {
        if (isBaritonePathing()) {
            if (isRotating()) {
                stopRotation();
            }
            return;
        }
        if (currentTask().equals(RotationTask.AIM) && idleTicks() > currentTimeout()) {
            currentTask(RotationTask.RESET);
        }

        if (currentTask().equals(RotationTask.RESET)) {
            resetRotation();
        }

        AuraNew aura = AuraNew.getInstance();
        if (aura.isEnabled() && aura.componentMode().is("Custom")) {
            String mode = aura.rotationUpdateMode().getValue();
            if (mode.equals("Продвинутая") && targetRotation != null) {
                updateRotation(targetRotation, currentYawSpeed, currentPitchSpeed);
            }
        }

        idleTicks++;
    }

    @EventHandler
    public void onEvent(Render2DEvent event) {
        if (isBaritonePathing()) {
            return;
        }
        AuraNew aura = AuraNew.getInstance();
        if (!aura.isEnabled()) return;

        boolean isCustomMode = aura.componentMode().is("Custom");
        boolean isLonyGriefMode = aura.componentMode().is("LonyGrief");
        boolean isWellmineMode = aura.componentMode().is("Wellmine");

        if (!isCustomMode && !isLonyGriefMode && !isWellmineMode) return;

        if (isCustomMode) {
            String mode = aura.rotationUpdateMode().getValue();
            if (!mode.equals("FPS") && !mode.equals("Продвинутая")) return;
        }

        long currentTime = System.currentTimeMillis();
        int currentFps = Minecraft.getDebugFPS();
        if (currentFps <= 0) currentFps = 60;
        if (currentFps > 240) currentFps = 240;

        long fpsInterval = 1000L / currentFps;

        if (currentTime - lastFpsUpdate >= fpsInterval) {
            if (targetRotation != null) {
                updateRotation(targetRotation, currentYawSpeed, currentPitchSpeed);
            }
            lastFpsUpdate = currentTime;
        }
    }

    public static void update(Rotation target, float yawSpeed, float pitchSpeed, float yawReturnSpeed, float pitchReturnSpeed, int timeout, int priority, boolean clientRotation) {
        final RotationComponent instance = RotationComponent.getInstance();
        if (instance.currentPriority() > priority) {
            return;
        }

        if (instance.currentTask().equals(RotationTask.IDLE) && !clientRotation) {
            FreeLookComponent.setActive(true);
        }

        instance.currentYawSpeed(yawSpeed);
        instance.currentPitchSpeed(pitchSpeed);
        instance.currentYawReturnSpeed(yawReturnSpeed);
        instance.currentPitchReturnSpeed(pitchReturnSpeed);
        instance.currentTimeout(timeout);
        instance.currentPriority(priority);
        instance.currentTask(RotationTask.AIM);
        instance.targetRotation(target);

        AuraNew aura = AuraNew.getInstance();
        String mode = aura.isEnabled() && aura.componentMode().is("Custom") ? aura.rotationUpdateMode().getValue() : "Тики";

        if (mode.equals("Тики")) {
            instance.updateRotation(target, yawSpeed, pitchSpeed);
        }
    }

    public static void update(Rotation targetRotation, float turnSpeed, float returnSpeed, int timeout, int priority) {
        update(targetRotation, turnSpeed, turnSpeed, returnSpeed, returnSpeed, timeout, priority, false);
    }

    public boolean updateRotation(Rotation targetRotation, float yawSpeed, float pitchSpeed) {
        if (mc.player == null) return false;

        Rotation currentRotation = new Rotation(mc.player);
        float yawDelta = MathHelper.wrapDegrees(targetRotation.getYaw() - currentRotation.getYaw());
        float pitchDelta = targetRotation.getPitch() - currentRotation.getPitch();

        float clampedYaw = Math.min(Math.abs(yawDelta), yawSpeed);
        float clampedPitch = Math.min(Math.abs(pitchDelta), pitchSpeed);

        mc.player.rotationYaw += SensUtil.getSens(MathHelper.clamp(yawDelta, -clampedYaw, clampedYaw));
        mc.player.rotationPitch = MathHelper.clamp(mc.player.rotationPitch + SensUtil.getSens(MathHelper.clamp(pitchDelta, -clampedPitch, clampedPitch)), -90F, 90F);

        idleTicks(0);
        return new Rotation(mc.player).getDelta(targetRotation) < 1F;
    }

    public void stopRotation() {
        currentTask(RotationTask.IDLE);
        currentPriority(0);
        targetRotation = null;
        if (!SmoothRotationComponent.getInstance().isRotating()) {
            FreeLookComponent.setActive(false);
        }
    }

    public boolean isRotating() {
        return !currentTask.equals(RotationTask.IDLE);
    }

    private boolean isBaritonePathing() {
        try {
            return BaritoneAPI.getProvider() != null
                    && BaritoneAPI.getProvider().getPrimaryBaritone() != null
                    && BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior() != null
                    && BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().isPathing();
        } catch (Throwable ignored) {
            return false;
        }
    }

    public enum RotationTask {
        AIM,
        RESET,
        IDLE
    }
}
