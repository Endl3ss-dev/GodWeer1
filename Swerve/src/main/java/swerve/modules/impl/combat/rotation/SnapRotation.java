package swerve.modules.impl.combat.rotation;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vec3;

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
    public Vector2f calculateRotation(LivingEntity target, Minecraft mc, Vector2f current) {
        if (target == null || mc.player == null) {
            return current;
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

        return new Vector2f(yaw, pitch);
    }
}
