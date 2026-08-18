package sky.core.utils.rotations;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.vector.Vector3d;
import sky.core.SkyCore;
import sky.core.handlers.impl.AuraRotationHandler;
import sky.core.handlers.impl.Rotation;
import sky.core.modules.impl.combat.AttackAura;
import sky.core.utils.Wrapper;

public final class AuraRotations implements Wrapper {
    private static final ThreadLocal<Boolean> RESOLVING_LOOK_VEC = ThreadLocal.withInitial(() -> false);

    private AuraRotations() {
    }

    public static boolean isHolyWorldActive() {
        AttackAura aura = getAura();
        return aura != null && aura.isEnabled() && aura.isHolyWorldRotationSelected() && AttackAura.getTarget() != null;
    }

    public static Rotation getMovementRotation() {
        if (mc.player == null || SkyCore.getInstance() == null) {
            return null;
        }

        AttackAura aura = getAura();
        if (aura != null && aura.isHolyWorldRotationSelected() && aura.isEnabled() && AttackAura.getTarget() != null) {
            if (aura.getAuraMoveCorrection() != MoveCorrection.NONE) {
                return new Rotation(mc.player.rotationYaw, mc.player.rotationPitch);
            }
            return null;
        }

        AuraRotationHandler handler = SkyCore.getInstance().getAuraRotationHandler();
        if (handler == null || handler.isIdling()) {
            return null;
        }

        AuraRotationTask task = handler.getCurrentTask();
        if (task == null || task.getMoveCorrection() == MoveCorrection.NONE) {
            return null;
        }

        return handler.getCurrentRotation();
    }

    public static float getMovementYaw(LivingEntity entity) {
        Rotation rotation = entity instanceof ClientPlayerEntity ? getMovementRotation() : null;
        return rotation != null ? rotation.getYaw() : entity.rotationYaw;
    }

    public static float getMovementPitch(LivingEntity entity) {
        Rotation rotation = entity instanceof ClientPlayerEntity ? getMovementRotation() : null;
        return rotation != null ? rotation.getPitch() : entity.rotationPitch;
    }

    public static Vector3d getMovementLookVec(LivingEntity entity) {
        if (RESOLVING_LOOK_VEC.get()) {
            return RotationMath.toLookVector(entity.rotationPitch, entity.rotationYaw);
        }

        RESOLVING_LOOK_VEC.set(true);
        try {
            return RotationMath.toLookVector(getMovementPitch(entity), getMovementYaw(entity));
        } finally {
            RESOLVING_LOOK_VEC.set(false);
        }
    }

    private static AttackAura getAura() {
        if (SkyCore.getInstance() == null || SkyCore.getInstance().getModuleManager() == null) {
            return null;
        }
        return SkyCore.getInstance().getModuleManager().attackAura;
    }
}
