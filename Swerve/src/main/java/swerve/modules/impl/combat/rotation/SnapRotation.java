package swerve.modules.impl.combat.rotation;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

/**
 * Ротация "Снап" — мгновенный поворот к цели.
 * Угол устанавливается напрямую, без интерполяции.
 * Подходит для большинства случаев, но может быть заметен античитам.
 */
public final class SnapRotation implements AuraRotation {

    @Override
    public String getName() {
        return "Снап";
    }

    @Override
    public void calculateRotation(LivingEntity target, Minecraft mc, float currentYaw, float currentPitch, float[] out) {
        if (target == null || mc.player == null) {
            out[0] = currentYaw;
            out[1] = currentPitch;
            return;
        }

        Vec3 eyes = mc.player.getEyePosition(1.0F);
        Vec3 targetPos = target.getEyePosition(1.0F);
        Vec3 direction = targetPos.subtract(eyes);

        float yaw = (float) Math.toDegrees(Math.atan2(direction.z, direction.x)) - 90.0f;
        float pitch = (float) -Math.toDegrees(
                Math.atan2(direction.y, Math.sqrt(direction.x * direction.x + direction.z * direction.z))
        );

        yaw = Mth.wrapDegrees(yaw);
        pitch = Mth.clamp(pitch, -90.0f, 90.0f);

        out[0] = yaw;
        out[1] = pitch;
    }
}
