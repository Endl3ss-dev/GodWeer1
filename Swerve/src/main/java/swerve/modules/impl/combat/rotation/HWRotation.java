package swerve.modules.impl.combat.rotation;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import swerve.utils.rotation.GcdUtil;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Ротация HW.
 *
 * <p>Порт из Private/src/java/org/privates/client/managers/module/impl/combat/auraelement/impl/HWRot.java.
 *
 * <p>Сохранено: адаптация по времени кадра ({@code dt * 60}, зажатая в 0.5..2),
 * прицел на высоте 0.5 роста, микродрожь каждые 50..100 мс, полосы скорости по
 * угловому расстоянию (0.6 при &lt;5°, 1.3 при &gt;30°), доля сглаживания
 * {@code 1 - smoothness}, потолок шага {@code speed / 20} за тик, случайное
 * дрожание шага 0.95..1.05, GCD-квантование и шаг с приоритетом 3. Базовые
 * скорости и smoothness рандомизируются при включении.
 *
 * <p>Адаптация: аннотация {@code @Native} из {@code com.adl.nativeprotect}
 * выброшена (нативной защиты в Swerve нет), {@code MathHelper} → {@code Mth},
 * {@code rotationYaw/Pitch} → {@code getYRot()/getXRot()}.
 */
public final class HWRotation extends AbstractRotationElement {

    private float baseYawSpeed = 120.0F;
    private float basePitchSpeed = 90.0F;
    private float smoothness = 0.4F;

    private long lastUpdateTime;
    private float fpsMultiplier = 1.0F;

    private float microJitterYaw;
    private float microJitterPitch;
    private long lastJitterUpdate;

    private float currentYaw;
    private float currentPitch;

    @Override
    public void update(LivingEntity target) {
        if (target == null || mc().player == null) {
            return;
        }

        long currentTime = System.currentTimeMillis();
        ThreadLocalRandom rng = ThreadLocalRandom.current();

        if (this.lastUpdateTime == 0L) {
            this.lastUpdateTime = currentTime;
            this.currentYaw = this.lerpYaw;
            this.currentPitch = this.lerpPitch;
        }

        float deltaTime = (currentTime - this.lastUpdateTime) / 1000.0F;
        this.lastUpdateTime = currentTime;

        this.fpsMultiplier = Mth.clamp(deltaTime * 60.0F, 0.5F, 2.0F);

        Vec3 playerEyes = mc().player.getEyePosition(1.0F);
        Vec3 targetPos = new Vec3(
                target.getX(),
                target.getY() + target.getEyeHeight() * 0.5D,
                target.getZ());
        double dx = targetPos.x - playerEyes.x;
        double dy = targetPos.y - playerEyes.y;
        double dz = targetPos.z - playerEyes.z;
        double horizontal = Math.sqrt(dx * dx + dz * dz);

        float targetYaw = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90.0F;
        float targetPitch = (float) -Math.toDegrees(Math.atan2(dy, horizontal));
        targetPitch = Mth.clamp(targetPitch, -89.9F, 89.9F);

        if (currentTime - this.lastJitterUpdate > 50 + rng.nextInt(50)) {
            this.microJitterYaw = rng.nextFloat(-0.3F, 0.3F);
            this.microJitterPitch = rng.nextFloat(-0.2F, 0.2F);
            this.lastJitterUpdate = currentTime;
        }

        targetYaw += this.microJitterYaw;
        targetPitch += this.microJitterPitch;

        float yawDiff = Mth.wrapDegrees(targetYaw - this.currentYaw);
        float pitchDiff = targetPitch - this.currentPitch;

        float angleDistance = (float) Math.sqrt(yawDiff * yawDiff + pitchDiff * pitchDiff);

        float speedMultiplier = 1.0F;
        if (angleDistance < 5.0F) {
            speedMultiplier = 0.6F;
        } else if (angleDistance > 30.0F) {
            speedMultiplier = 1.3F;
        }

        float yawSpeed = this.baseYawSpeed * speedMultiplier * this.fpsMultiplier;
        float pitchSpeed = this.basePitchSpeed * speedMultiplier * this.fpsMultiplier;

        float lerpFactor = 1.0F - this.smoothness;
        float yawStep = yawDiff * lerpFactor;
        float pitchStep = pitchDiff * lerpFactor;

        float maxYawStep = yawSpeed / 20.0F;
        float maxPitchStep = pitchSpeed / 20.0F;

        yawStep = Mth.clamp(yawStep, -maxYawStep, maxYawStep);
        pitchStep = Mth.clamp(pitchStep, -maxPitchStep, maxPitchStep);

        yawStep *= rng.nextFloat(0.95F, 1.05F);
        pitchStep *= rng.nextFloat(0.95F, 1.05F);

        this.currentYaw += yawStep;
        this.currentPitch = Mth.clamp(this.currentPitch + pitchStep, -89.9F, 89.9F);

        float finalYaw = GcdUtil.getSensitivity(this.currentYaw);
        float finalPitch = GcdUtil.getSensitivity(this.currentPitch);

        this.lerpYaw = finalYaw;
        this.lerpPitch = finalPitch;
        applyRotation(finalYaw, finalPitch,
                (int) (this.baseYawSpeed * speedMultiplier),
                (int) (this.basePitchSpeed * speedMultiplier),
                0, 3);
    }

    @Override
    public void onEnable() {
        ThreadLocalRandom rng = ThreadLocalRandom.current();

        this.baseYawSpeed = 110.0F + rng.nextFloat(-10.0F, 10.0F);
        this.basePitchSpeed = 80.0F + rng.nextFloat(-10.0F, 10.0F);
        this.smoothness = 0.35F + rng.nextFloat(-0.05F, 0.05F);

        this.lastUpdateTime = 0L;
        this.fpsMultiplier = 1.0F;
        this.microJitterYaw = 0.0F;
        this.microJitterPitch = 0.0F;
        this.lastJitterUpdate = 0L;

        if (mc().player != null) {
            this.currentYaw = mc().player.getYRot();
            this.currentPitch = mc().player.getXRot();
        }
    }

    @Override
    public void onDisable() {
        // HWRot в Private перекрывает onDisable пустым телом.
        this.lastUpdateTime = 0L;
    }
}
