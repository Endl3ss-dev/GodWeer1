package sky.core.handlers.impl;

import com.darkmagician6.eventapi.eventapinew.EventTarget;
import lombok.Getter;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import sky.core.SkyCore;
import sky.core.events.EventInput;
import sky.core.events.EventMotion;
import sky.core.modules.impl.combat.AttackAura;
import sky.core.modules.impl.miscellaneous.TargetPearl;
import sky.core.modules.impl.movement.Speed;
import sky.core.utils.component.impl.MoveComponent;
import sky.core.utils.component.impl.RotationComponent;
import sky.core.utils.component.impl.SmoothRotationComponent;
import sky.core.utils.math.AuraUtil;
import sky.core.utils.player.MoveUtil;
import sky.core.utils.rotations.AuraRotationTask;
import sky.core.utils.rotations.AuraRotations;
import sky.core.utils.rotations.MoveCorrection;
import sky.core.utils.Wrapper;

@Getter
public class AuraRotationHandler implements Wrapper {
    private final Rotation serverRotation = new Rotation(0.0F, 0.0F);

    public boolean isIdling() {
        return !RotationComponent.getInstance().isRotating()
                && !SmoothRotationComponent.getInstance().isRotating();
    }

    public Rotation getCurrentRotation() {
        if (mc.player == null) {
            return new Rotation(0.0F, 0.0F);
        }
        return new Rotation(mc.player.rotationYaw, mc.player.rotationPitch);
    }

    public AuraRotationTask getCurrentTask() {
        if (isIdling()) {
            return null;
        }

        AttackAura aura = getAura();
        if (aura == null || !aura.isEnabled()) {
            return null;
        }

        return new AuraRotationTask(aura.getAuraMoveCorrection());
    }

    @EventTarget
    public void onInput(EventInput event) {
        if (TargetPearl.isRotationActive()) {
            return;
        }

        AttackAura aura = getAura();
        AuraRotationTask currentTask = getCurrentTask();
        if (!isIdling() && currentTask != null && currentTask.getMoveCorrection() == MoveCorrection.SILENT) {
            MoveUtil.fixMovement(event, getCurrentRotation().getYaw());
            return;
        }

        if (AuraRotations.isHolyWorldActive()) {
            if (aura != null && aura.getAuraMoveCorrection() == MoveCorrection.SILENT) {
                MoveUtil.fixMovement(event, mc.player.rotationYaw);
            }
            return;
        }

        if (aura == null || !aura.isEnabled() || AttackAura.getTarget() == null) {
            return;
        }

        if (!RotationComponent.getInstance().isRotating() && !SmoothRotationComponent.getInstance().isRotating()) {
            return;
        }

        MoveCorrection correction = aura.getAuraMoveCorrection();
        LivingEntity target = AttackAura.getTarget();
        if (correction == MoveCorrection.SILENT) {
            MoveUtil.fixMovement(event, LookHandler.getFreeYaw());
        } else if (correction == MoveCorrection.TARGET) {
            Vector3d targetVector = target.getPositionVec();
            Speed speedModule = (Speed) SkyCore.getInstance().getModuleManager().getModule(Speed.class);
            if (speedModule != null && speedModule.isEnabled() && aura.getEntitySpeed(target) > 5.0D) {
                targetVector = target.getPositionVec().add(target.getForward().normalize().scale(2.0D));
            }
            if (!MoveComponent.isStop()) {
                MoveUtil.moveToPosition(event, targetVector, mc.player.rotationYaw);
            }
        } else if (correction == MoveCorrection.FOCUSED) {
            Vector3d direction = AuraUtil.getClosestVec(target);
            float targetYaw = (float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(direction.z, direction.x)) - 90.0D);
            MoveUtil.fixMovement(event, targetYaw);
        }
    }

    @EventTarget
    public void onMotion(EventMotion event) {
        if (!isIdling()) {
            event.setYaw(getCurrentRotation().getYaw());
            event.setPitch(getCurrentRotation().getPitch());
            serverRotation.setYaw(event.getYaw());
            serverRotation.setPitch(event.getPitch());
            return;
        }

        if (AuraRotations.isHolyWorldActive() && mc.player != null) {
            event.setYaw(mc.player.rotationYaw);
            event.setPitch(mc.player.rotationPitch);
            serverRotation.setYaw(event.getYaw());
            serverRotation.setPitch(event.getPitch());
        }
    }

    private AttackAura getAura() {
        if (SkyCore.getInstance() == null || SkyCore.getInstance().getModuleManager() == null) {
            return null;
        }
        return SkyCore.getInstance().getModuleManager().attackAura;
    }
}
