package swerve.modules.impl.combat.rotation;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

import swerve.utils.rotation.GcdUtil;

/**
 * Ротация для Intave.
 *
 * <p>Порт из Private/src/java/org/privates/client/managers/module/impl/combat/auraelement/impl/IntaveRot.java.
 *
 * <p>Поведение сохранено: цель берётся на высоте глаза со случайным множителем
 * 0.6..0.8, к углу добавляется синусоидальное отклонение от времени (yaw ±1.5°,
 * pitch ±1°), затем упреждение по скорости цели (0.3 к yaw, 0.2 к pitch),
 * квантование по GCD и шаг со скоростью 95/35 с приоритетом 4.
 *
 * <p>Отличия адаптации: {@code Math.random()} заменён на ГПСЧ элемента (та же
 * равномерная выборка, но без общей блокировки), {@code getDistance} → {@code distanceTo},
 * {@code prevPos} → {@code xo/yo/zo}. Мёртвая ветка оригинала с
 * {@code checkIfPassedTarget}/{@code getDistance} внутри {@code isElytraFlying()}
 * не переносилась: её результат нигде не использовался.
 */
public final class IntaveRotation extends AbstractRotationElement {

    @Override
    public void update(LivingEntity target) {
        if (target == null || mc().player == null) {
            return;
        }

        // Точка прицела и глаза — скалярами: значения те же, Vec3 на тик не нужен.
        double targetX = target.getX();
        double targetY = target.getY() + target.getEyeHeight() * (0.6D + this.random.nextDouble() * 0.2D);
        double targetZ = target.getZ();
        double dx = targetX - mc().player.getX();
        double dy = targetY - mc().player.getEyeY();
        double dz = targetZ - mc().player.getZ();

        float baseYaw = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90.0F;
        float basePitch = (float) -Math.toDegrees(Math.atan2(dy, Math.sqrt(dx * dx + dz * dz)));

        long now = System.currentTimeMillis();
        float yawDeviation = (float) (Math.sin(now * 0.015D) * 1.5D);
        float pitchDeviation = (float) Math.cos(now * 0.015D);

        float targetYaw = baseYaw + yawDeviation;
        float targetPitch = Mth.clamp(basePitch + pitchDeviation, -90.0F, 90.0F);

        double motionX = target.getX() - target.xo;
        double motionY = target.getY() - target.yo;
        double motionZ = target.getZ() - target.zo;

        targetYaw += Math.toDegrees(Math.atan2(motionZ, motionX)) * 0.3D;
        targetPitch += Math.toDegrees(motionY) * 0.2D;

        float smoothYaw = GcdUtil.getSensitivity(targetYaw);
        float smoothPitch = GcdUtil.getSensitivity(targetPitch);

        this.lerpYaw = smoothYaw;
        this.lerpPitch = smoothPitch;
        applyRotation(smoothYaw, smoothPitch, 95.0F, 35.0F, 0, 4);
    }

    @Override
    public void onDisable() {
        // IntaveRot в Private перекрывает onDisable пустым телом.
    }
}
