package org.privates.baritone.behavior;

import org.privates.baritone.Baritone;
import org.privates.baritone.api.behavior.ILookBehavior;
import org.privates.baritone.api.event.events.PlayerUpdateEvent;
import org.privates.baritone.api.event.events.RotationMoveEvent;
import org.privates.baritone.api.utils.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

public final class LookBehavior extends Behavior implements ILookBehavior {

    private Rotation target;
    private boolean force;
    private float lastYaw;

    public LookBehavior(Baritone baritone) {
        super(baritone);
    }

    @Override
    public void updateTarget(Rotation target, boolean force) {
        double randYaw = (Math.random() - 0.5D) * Baritone.settings().randomLooking.value;
        double randPitch = (Math.random() - 0.5D) * Baritone.settings().randomLooking.value;
        this.target = new Rotation(target.getYaw() + (float) randYaw, target.getPitch() + (float) randPitch);
        this.force = force || !Baritone.settings().freeLook.value;
    }

    public void updateTarget(BlockPos pos, boolean force) {
        Rotation rot = calculateTargetRotation(pos);
        updateTarget(rot, force);
    }

    private Rotation calculateTargetRotation(BlockPos pos) {
        Vector3d eye = ctx.playerHead();
        Vector3d blockCenter = new Vector3d(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
        Vector3d directionVec = blockCenter.subtract(eye).normalize();

        float baseYaw = (float) Math.toDegrees(Math.atan2(-directionVec.x, directionVec.z));
        float basePitch = MathHelper.clamp(
                -(float) Math.toDegrees(Math.atan2(directionVec.y, Math.hypot(directionVec.x, directionVec.z))),
                -90.0F,
                90.0F
        );

        return new Rotation(MathHelper.wrapDegrees(baseYaw), basePitch);
    }

    @Override
    public void onPlayerUpdate(PlayerUpdateEvent event) {
        if (target == null) return;

        boolean silent = Baritone.settings().antiCheatCompatibility.value && !force;

        switch (event.getState()) {
            case PRE:
                if (force) {
                    applyRotation(target);
                    target = null;
                } else if (silent) {
                    lastYaw = ctx.player().packetYaw;
                    applyRotation(target);
                }
                break;
            case POST:
                if (silent) {
                    ctx.player().packetYaw = lastYaw;
                    target = null;
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onPlayerRotationMove(RotationMoveEvent event) {
        if (target == null) {
            return;
        }
        smoothApplyRotation(target, event);
        if (!Baritone.settings().antiCheatCompatibility.value && event.getType() == RotationMoveEvent.Type.MOTION_UPDATE && !force) {
            target = null;
        }
    }

    private void applyRotation(Rotation rot) {
        float yaw = MathHelper.wrapDegrees(rot.getYaw());
        float pitch = MathHelper.clamp(rot.getPitch(), -90.0F, 90.0F);
        ctx.player().rotationYaw = yaw;
        ctx.player().rotationYawHead = yaw;
        ctx.player().renderYawOffset = yaw;
        ctx.player().packetYaw = yaw;
        ctx.player().rotationPitch = pitch;
        ctx.player().packetPitch = pitch;
    }

    private void smoothApplyRotation(Rotation rot, RotationMoveEvent event) {
        float smoothedYaw = calculateSmoothedYaw(rot.getYaw());
        float smoothedPitch = calculateSmoothedPitch(rot.getPitch());

        ctx.player().rotationYaw = smoothedYaw;
        ctx.player().rotationYawHead = smoothedYaw;
        ctx.player().renderYawOffset = smoothedYaw;
        ctx.player().packetYaw = smoothedYaw;
        ctx.player().rotationPitch = smoothedPitch;
        ctx.player().packetPitch = smoothedPitch;

        event.setYaw(smoothedYaw);
    }

    private float calculateSmoothedYaw(float targetYaw) {
        float currentYaw = ctx.player().rotationYaw;
        float deltaYaw = MathHelper.wrapDegrees(targetYaw - currentYaw);

        float maxStep = force ? 120.0F : (Baritone.settings().antiCheatCompatibility.value ? 45.0F : 90.0F);
        deltaYaw = MathHelper.clamp(deltaYaw, -maxStep, maxStep);
        return MathHelper.wrapDegrees(currentYaw + deltaYaw);
    }

    private float calculateSmoothedPitch(float targetPitch) {
        float currentPitch = ctx.player().rotationPitch;
        float deltaPitch = targetPitch - currentPitch;

        float maxStep = force ? 60.0F : (Baritone.settings().antiCheatCompatibility.value ? 30.0F : 45.0F);
        deltaPitch = MathHelper.clamp(deltaPitch, -maxStep, maxStep);
        return MathHelper.clamp(currentPitch + deltaPitch, -90.0F, 90.0F);
    }
}
