package fun.volon.client.modules.impl.combat.components.rotations;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import fun.volon.api.storages.implement.RotationStorage;
import fun.volon.api.utils.rotate.Rotation;
import fun.volon.api.utils.rotate.RotationUtils;
import fun.volon.client.modules.impl.combat.Aura;
import fun.volon.client.modules.impl.combat.components.RotationsSystem;
import fun.volon.client.modules.impl.combat.components.gcd.GCDUtil;
import fun.volon.client.modules.impl.combat.components.interpolation.BestPoint;

import java.util.concurrent.ThreadLocalRandom;

/**
 * FunTime ротация — snap с плавным snap-back и multi-point для обхода Polar.
 *
 * Принцип:
 *  - Атака:   snap к цели с ускорением через speedAcceleration (как WhiteRise),
 *             multi-point через BestPoint.getMultipoint для Polar bypass.
 *  - Snap-back: после потери цели плавный возврат к взгляду игрока через
 *              отдельный backAcceleration с fade-out эффектом.
 *  - GCD:     применяется для легитности.
 */
public class FunTimeRotation extends RotationsSystem {

    private final Aura aura;

    // Snap-forward состояние
    private float speedAcceleration = 0.0f;
    private boolean back = false;

    // Snap-back состояние
    private float backAcceleration = 0.0f;
    private boolean returning = false;
    private long snapBackStartMs = -1L;

    // Текущие применённые углы
    private float lastYaw;
    private float lastPitch;

    // Для сброса при смене цели
    private int lastTargetId = Integer.MIN_VALUE;

    // Jitter счётчик для tick-based вариации
    private int tickCounter = 0;
    private float jitterOffset = 0.0f;

    public FunTimeRotation(Aura aura) {
        this.aura = aura;
        if (mc.player != null) {
            lastYaw = mc.player.getYaw();
            lastPitch = mc.player.getPitch();
        }
    }

    public void reset() {
        speedAcceleration = 0.0f;
        backAcceleration = 0.0f;
        back = false;
        returning = false;
        snapBackStartMs = -1L;
        tickCounter = 0;
        jitterOffset = 0.0f;
        lastTargetId = Integer.MIN_VALUE;
        if (mc.player != null) {
            lastYaw = mc.player.getYaw();
            lastPitch = mc.player.getPitch();
        }
    }

    public void onAttack() {
        aura.getAttackTimer().reset();
    }

    @Override
    public void updateRotations(LivingEntity target) {
        if (mc.player == null) return;

        boolean hasTarget = target != null && aura.getTarget() != null;

        if (hasTarget) {
            // Сброс snap-back при появлении цели
            returning = false;
            snapBackStartMs = -1L;
            backAcceleration = 0.0f;

            // Сброс при смене цели
            if (target.getId() != lastTargetId) {
                lastTargetId = target.getId();
                speedAcceleration = 0.0f;
                back = false;
                tickCounter = 0;
            }

            rotateToTarget(target);
        } else {
            // Snap-back к взгляду игрока
            lastTargetId = Integer.MIN_VALUE;
            speedAcceleration = 0.0f;
            back = false;

            snapBack();
        }
    }

    // ─────────────────────────────────────────────────────────
    //  Snap к цели (режим атаки)
    // ─────────────────────────────────────────────────────────
    private void rotateToTarget(LivingEntity target) {
        tickCounter++;

        // Tick-based jitter — маскирует паттерн под человека
        jitterOffset = (float) (
            Math.sin(tickCounter * 0.19) * 0.10
            + (ThreadLocalRandom.current().nextDouble() * 0.06 - 0.03)
        ) * 0.65f;

        // Multi-point для обхода Polar: BestPoint.getMultipoint двигает точку прицела
        // по hitbox'у плавно и непредсказуемо, что ломает паттерн-анализ Polar.
        double dist = mc.player.distanceTo(target) + 2.5;
        Vec3d aimPoint = BestPoint.getMultipoint(target, Math.max(6.0, dist));
        if (aimPoint == null) {
            aimPoint = target.getBoundingBox().getCenter();
        }

        if (shouldUseElytraPredict(target)) {
            aimPoint = getPredictedPoint(target, aimPoint);
        }

        Vec2f angle = RotationUtils.getRotations(aimPoint);
        float targetYaw = angle.x;
        float targetPitch = angle.y;

        float yawDiff = Math.abs(MathHelper.wrapDegrees(targetYaw - lastYaw));
        boolean readyToAttack = mc.player.getAttackCooldownProgress(1.0F) > 0.9F;

        // ── Snap ускорение/замедление (аналог WhiteRise) ──
        if (!back) {
            float gain = 0.006f;
            if (yawDiff > 55.0f)      gain += 0.020f;
            else if (yawDiff > 25.0f) gain += 0.010f;
            else                      gain += 0.005f;

            if (readyToAttack) gain += 0.015f;

            speedAcceleration += gain * (1.5f + jitterOffset);

            if (speedAcceleration >= 0.24f) back = true;
        } else {
            float loss = readyToAttack ? 0.048f : 0.009f;
            speedAcceleration -= loss * (2.0f + jitterOffset);
            if (speedAcceleration <= -0.04f) back = false;
        }

        float smooth = MathHelper.clamp(
            speedAcceleration,
            0.0f,
            mc.player.isGliding() ? 0.40f : 0.28f
        );
        if (readyToAttack) {
            smooth = Math.min(smooth + 0.12f, mc.player.isGliding() ? 0.48f : 0.36f);
        }
        smooth += jitterOffset * 0.4f;
        if (tickCounter % 8 == 0) smooth += 0.025f;

        float deltaYaw = MathHelper.wrapDegrees(targetYaw - lastYaw);
        float deltaPitch = targetPitch - lastPitch;

        // Snap лимиты — резкий поворот, но с ограничением чтобы не телепортировало
        float yawLimit = mc.player.isGliding() ? 45.0f : (readyToAttack ? 32.0f : 22.0f);
        float pitchLimit = mc.player.isGliding() ? 14.0f : (readyToAttack ? 5.5f : 3.2f);

        deltaYaw = MathHelper.clamp(deltaYaw, -yawLimit, yawLimit);
        deltaPitch = MathHelper.clamp(deltaPitch, -pitchLimit, pitchLimit);

        float yawSpeed = smooth * (0.88f + jitterOffset * 0.35f);
        float pitchSpeed = smooth * 0.30f;

        float newYaw = lastYaw + deltaYaw * yawSpeed;
        float newPitch = lastPitch + deltaPitch * pitchSpeed;

        // GCD корrekция
        float gcd = GCDUtil.getGCDValue();
        if (gcd > 0.0f) {
            newYaw = lastYaw + Math.round((newYaw - lastYaw) / gcd) * gcd;
            newPitch = lastPitch + Math.round((newPitch - lastPitch) / gcd) * gcd;
        }

        newPitch = MathHelper.clamp(newPitch, -89.0f, 89.0f);

        lastYaw = newYaw;
        lastPitch = newPitch;

        RotationStorage.update(
            new Rotation(newYaw, newPitch),
            360.0f, 360.0f, 360.0f, 360.0f,
            0, 1,
            Aura.clientLook.isState()
        );
        rotate = new Vec2f(newYaw, newPitch);
    }

    // ─────────────────────────────────────────────────────────
    //  Snap-back к взгляду игрока (плавный возврат)
    // ─────────────────────────────────────────────────────────
    private void snapBack() {
        if (mc.player == null) return;

        float playerYaw = mc.player.getYaw();
        float playerPitch = mc.player.getPitch();

        float diffYaw = MathHelper.wrapDegrees(playerYaw - lastYaw);
        float diffPitch = playerPitch - lastPitch;
        float totalDiff = (float) Math.hypot(diffYaw, diffPitch);

        // Если уже практически на месте — не делаем ничего
        if (totalDiff < 0.5f) {
            lastYaw = playerYaw;
            lastPitch = playerPitch;
            returning = false;
            return;
        }

        // Старт отсчёта fade-out
        if (!returning) {
            returning = true;
            snapBackStartMs = System.currentTimeMillis();
            backAcceleration = 0.0f;
        }

        // Fade-out: через 800мс snap-back полностью затухает
        float elapsed = (float)(System.currentTimeMillis() - snapBackStartMs);
        float fadeRatio = MathHelper.clamp(1.0f - elapsed / 800.0f, 0.0f, 1.0f);

        if (fadeRatio <= 0.0f) {
            // Snap прямо на позицию игрока в конце
            lastYaw = playerYaw;
            lastPitch = playerPitch;
            returning = false;
            return;
        }

        // Ускорение snap-back (быстрее чем forward для естественного ощущения)
        backAcceleration = MathHelper.clamp(backAcceleration + 0.08f, 0.0f, 0.6f);
        float smooth = backAcceleration * fadeRatio;

        // Небольшой shake для реалистичности
        float shakeYaw = (float)(Math.sin(elapsed / 55.0) * 1.4f * fadeRatio);
        float shakePitch = (float)(Math.cos(elapsed / 55.0) * 0.6f * fadeRatio);

        float newYaw = lastYaw + diffYaw * smooth + shakeYaw;
        float newPitch = lastPitch + diffPitch * smooth + shakePitch;

        // GCD
        float gcd = GCDUtil.getGCDValue();
        if (gcd > 0.0f) {
            newYaw = lastYaw + Math.round((newYaw - lastYaw) / gcd) * gcd;
            newPitch = lastPitch + Math.round((newPitch - lastPitch) / gcd) * gcd;
        }

        newPitch = MathHelper.clamp(newPitch, -89.0f, 89.0f);

        lastYaw = newYaw;
        lastPitch = newPitch;

        RotationStorage.update(
            new Rotation(newYaw, newPitch),
            360.0f, 360.0f, 360.0f, 360.0f,
            0, 1,
            Aura.clientLook.isState()
        );
        rotate = new Vec2f(newYaw, newPitch);
    }
}
