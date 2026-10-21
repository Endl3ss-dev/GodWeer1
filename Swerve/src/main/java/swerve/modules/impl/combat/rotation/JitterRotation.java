package swerve.modules.impl.combat.rotation;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Ротация "Джиттер" — мгновенный поворот к цели с небольшим случайным отклонением.
 * Помогает обходить простые античиты, которые ищут идеально ровный наводку.
 */
public final class JitterRotation implements AuraRotation {

    @Override
    public String getName() {
        return "Джиттер";
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

        // Добавляем случайный джиттер в пределах ±2 градусов
        float jitterYaw = (ThreadLocalRandom.current().nextFloat() - 0.5f) * 4.0f;
        float jitterPitch = (ThreadLocalRandom.current().nextFloat() - 0.5f) * 4.0f;

        yaw = Mth.wrapDegrees(yaw + jitterYaw);
        pitch = Mth.clamp(pitch + jitterPitch, -90.0f, 90.0f);

        out[0] = yaw;
        out[1] = pitch;
    }
}
