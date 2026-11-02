package swerve.modules.impl.combat.rotation;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import swerve.utils.rotation.GcdUtil;
import swerve.utils.rotation.RotationController;

/**
 * Ротация для Matrix.
 *
 * <p>Порт из Private/src/java/org/privates/client/managers/module/impl/combat/auraelement/impl/MatrixRot.java.
 *
 * <p>Сохранена вся механика: собственное сглаживание в полях элемента, заморозка
 * при дистанции ≤ 0.85 блока и при перекрытой видимости точки прицела, прицел на
 * высоте 0.65 от роста, ограничение pitch ±75°, масштаб по времени кадра
 * ({@code dt * 60}), полосы скорости 7.5..12.5 yaw и 1.2..2.8 pitch, шум каждые
 * 150 мс и шаг 60/14 с приоритетом 3. При выключении — ускоренный возврат
 * камеры ({@code smoothResetRotation}).
 *
 * <p>Отличия адаптации: {@code getDistance} → {@code distanceTo}, {@code Vector2f}
 * заменён парой float-полей (в тике нет аллокаций), {@code isPointVisible}
 * использует {@code ClipContext} 1.21.4.
 */
public final class MatrixRotation extends AbstractRotationElement {

    private static final float FREEZE_DISTANCE = 0.85F;
    private static final float AIM_HEIGHT_FACTOR = 0.65F;
    private static final float PITCH_MIN = -75.0F;
    private static final float PITCH_MAX = 75.0F;
    private static final float PITCH_THRESHOLD = 5.0F;
    private static final long NOISE_INTERVAL_MS = 150L;

    private float smoothYaw;
    private float smoothPitch;
    private long lastUpdateMs;

    private float noiseYaw;
    private float noisePitch;
    private long noiseTimer;

    @Override
    public void update(LivingEntity target) {
        if (target == null || mc().player == null) {
            return;
        }

        long now = System.currentTimeMillis();
        if (this.lastUpdateMs == 0L) {
            this.lastUpdateMs = now;
            this.smoothYaw = this.lerpYaw;
            this.smoothPitch = this.lerpPitch;
        }

        float distance = mc().player.distanceTo(target);

        if (distance <= FREEZE_DISTANCE) {
            float frozenYaw = GcdUtil.getSensitivity(this.smoothYaw);
            float frozenPitch = GcdUtil.getSensitivity(this.smoothPitch);
            this.lerpYaw = frozenYaw;
            this.lerpPitch = frozenPitch;
            applyRotation(frozenYaw, frozenPitch, 40.0F, 28.0F, 2, 3);
            return;
        }

        float dt = Mth.clamp((now - this.lastUpdateMs) / 1000.0F, 0.001F, 0.05F);
        float dtScale = dt * 60.0F;
        this.lastUpdateMs = now;

        Vec3 eyes = mc().player.getEyePosition(1.0F);
        Vec3 targetPoint = new Vec3(
                target.getX(),
                target.getY() + target.getEyeHeight() * AIM_HEIGHT_FACTOR,
                target.getZ());

        if (!isPointVisible(targetPoint)) {
            float frozenYaw = GcdUtil.getSensitivity(this.smoothYaw);
            float frozenPitch = GcdUtil.getSensitivity(this.smoothPitch);
            this.lerpYaw = frozenYaw;
            this.lerpPitch = frozenPitch;
            applyRotation(frozenYaw, frozenPitch, 12.0F, 3.0F, 2, 3);
            return;
        }

        double dx = targetPoint.x - eyes.x;
        double dy = targetPoint.y - eyes.y;
        double dz = targetPoint.z - eyes.z;
        double horizontal = Math.sqrt(dx * dx + dz * dz);

        float targetYaw = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90.0F;
        float targetPitch = (float) -Math.toDegrees(Math.atan2(dy, horizontal));
        targetPitch = Mth.clamp(targetPitch, PITCH_MIN, PITCH_MAX);

        float yawDiff = Mth.wrapDegrees(targetYaw - this.smoothYaw);
        float pitchDiff = targetPitch - this.smoothPitch;

        float yawSpeed = Mth.clamp(2.8F + Math.abs(yawDiff) * 0.19F, 7.5F, 12.5F) * dtScale;

        float pitchSpeed;
        if (Math.abs(pitchDiff) > PITCH_THRESHOLD) {
            pitchSpeed = Mth.clamp(2.0F + Math.abs(pitchDiff) * 0.15F, 1.2F, 2.8F) * dtScale;
        } else {
            pitchSpeed = 0.08F * dtScale;
        }

        this.smoothYaw += Mth.clamp(yawDiff, -yawSpeed, yawSpeed);
        this.smoothPitch = Mth.clamp(
                this.smoothPitch + Mth.clamp(pitchDiff, -pitchSpeed, pitchSpeed),
                PITCH_MIN, PITCH_MAX);

        long nowNoise = System.currentTimeMillis();
        if (nowNoise - this.noiseTimer > NOISE_INTERVAL_MS) {
            this.noiseTimer = nowNoise;
            this.noiseYaw = (float) (this.random.nextDouble() * 0.36D - 0.18D);
            this.noisePitch = (float) (this.random.nextDouble() * 0.22D - 0.11D);
        }

        float finalYaw = GcdUtil.getSensitivity(this.smoothYaw + this.noiseYaw * 0.3F);
        float finalPitch = GcdUtil.getSensitivity(this.smoothPitch + this.noisePitch * 0.2F);

        this.lerpYaw = finalYaw;
        this.lerpPitch = finalPitch;
        applyRotation(finalYaw, finalPitch, 60.0F, 14.0F, 2, 3);
    }

    @Override
    public void onEnable() {
        this.lastUpdateMs = 0L;
        if (mc().player != null) {
            this.smoothYaw = mc().player.getYRot();
            this.smoothPitch = mc().player.getXRot();
        }
        this.noiseYaw = 0.0F;
        this.noisePitch = 0.0F;
        this.noiseTimer = 0L;
    }

    @Override
    public void onDisable() {
        RotationController.smoothResetRotation();
        this.lastUpdateMs = 0L;
    }
}
