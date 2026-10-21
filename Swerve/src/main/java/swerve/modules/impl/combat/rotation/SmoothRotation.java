package swerve.modules.impl.combat.rotation;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vec3;

/**
 * Ротация "Плавная" — плавная интерполяция текущих углов к целевым.
 * Скорость поворота настраивается и не вызывает резких рывков камеры.
 */
public final class SmoothRotation implements AuraRotation {

    private float smoothYaw;
    private float smoothPitch;
    private boolean initialized;
    private float speed = 0.15f;

    @Override
    public String getName() {
        return "Плавная";
    }

    @Override
    public void onEnable() {
        initialized = false;
    }

    /** Устанавливает скорость интерполяции (0.0 — без поворота, 1.0 — мгновенно). */
    public void setSpeed(float speed) {
        this.speed = Mth.clamp(speed, 0.01f, 1.0f);
    }

    @Override
    public Vector2f calculateRotation(LivingEntity target, Minecraft mc, Vector2f current) {
        if (target == null || mc.player == null) {
            initialized = false;
            return current;
        }

        // Инициализируем плавные углы текущими углами игрока
        if (!initialized) {
            smoothYaw = current.x;
            smoothPitch = current.y;
            initialized = true;
        }

        Vec3 eyes = mc.player.getEyePosition(1.0F);
        Vec3 targetPos = target.getEyePosition(1.0F);
        Vec3 direction = targetPos.subtract(eyes);

        float targetYaw = (float) Math.toDegrees(Math.atan2(direction.z, direction.x)) - 90.0f;
        float targetPitch = (float) -Math.toDegrees(
                Math.atan2(direction.y, Math.sqrt(direction.x * direction.x + direction.z * direction.z))
        );
        targetYaw = Mth.wrapDegrees(targetYaw);
        targetPitch = Mth.clamp(targetPitch, -90.0f, 90.0f);

        // Скорость интерполяции (0.0 — без поворота, 1.0 — мгновенно)

        float deltaYaw = Mth.wrapDegrees(targetYaw - smoothYaw);
        float deltaPitch = targetPitch - smoothPitch;

        smoothYaw += deltaYaw * speed;
        smoothPitch += deltaPitch * speed;

        smoothYaw = Mth.wrapDegrees(smoothYaw);
        smoothPitch = Mth.clamp(smoothPitch, -90.0f, 90.0f);

        return new Vector2f(smoothYaw, smoothPitch);
    }
}
