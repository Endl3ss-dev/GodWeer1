package tech.lightclient.modules.impl.combat.aura.rotations;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import tech.lightclient.client.utils.player.rotation.RotateVector;
import tech.lightclient.modules.impl.combat.AuraModule;
import tech.lightclient.modules.impl.combat.aura.rotations.ft360.LunoTOPPacketRotation;
import tech.lightclient.modules.impl.combat.aura.rotations.ft360.Rotation;

/**
 * Ротация LunoTOP FunTime 360.
 *
 * <p>Контракт частот соблюдается внутри класса, поэтому вызывающая сторона
 * может дергать {@link #update} с любой частотой:
 * <ol>
 *   <li>{@code aim}/{@code beginReturn} выполняются ровно один раз за игровой
 *       тик (20 Гц) — это планирование прицела;</li>
 *   <li>{@code frameStep} двигает клиентный мотор фиксированными шагами
 *       1/240 с и пакеты сам не отправляет;</li>
 *   <li>пакетный угол снимает RotationTask со своих 20 Гц.</li>
 * </ol>
 */
public final class Ft360Rotation extends RotateModel {
    /** Окно подготовки удара — примерно 200 мс до разрешённого хита. */
    private static final long ATTACK_WINDOW_MS = 200L;

    private final LunoTOPPacketRotation rotation = new LunoTOPPacketRotation();
    private int seededTargetId = Integer.MIN_VALUE;
    private int lastAimTick = Integer.MIN_VALUE;

    @Override
    public RotateVector update(RotateVector current, LivingEntity target) {
        if (current == null) {
            return null;
        }
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) {
            return current;
        }

        boolean hasTarget = target != null && target.isAlive();
        if (hasTarget && this.seededTargetId != target.getId()) {
            // Новый engagement начинается с текущего видимого угла, чтобы
            // первый шаг не был прыжком с прошлой позиции.
            this.rotation.seed(current.getYaw(), current.getPitch());
            this.seededTargetId = target.getId();
        }

        int tick = client.player.age;
        if (this.lastAimTick != tick) {
            this.lastAimTick = tick;
            if (hasTarget) {
                AuraModule aura = AuraModule.getInstance();
                boolean attackWindow = aura != null
                        && aura.getTimeUntilNextAttackMs() <= ATTACK_WINDOW_MS;
                this.rotation.aim(target, attackWindow);
            } else {
                // Цель потеряна: скрытый угол уходит обратно к камере, мотор
                // продолжает работать, пока isActive() не станет false.
                this.seededTargetId = Integer.MIN_VALUE;
                this.rotation.beginReturn();
            }
        }

        this.rotation.frameStep();
        return this.toVector();
    }

    /** Подтверждённый удар. */
    public void onAttack() {
        this.rotation.onAttack();
    }

    /** Активна ли ротация: есть цель либо ещё идёт возврат скрытого угла. */
    public boolean isActive() {
        return this.rotation.isActive();
    }

    private RotateVector toVector() {
        Rotation next = this.rotation.currentRotation();
        return new RotateVector(next.getYaw(), next.getPitch());
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.seededTargetId = Integer.MIN_VALUE;
        this.lastAimTick = Integer.MIN_VALUE;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            this.rotation.seed(client.player.getYaw(), client.player.getPitch());
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.seededTargetId = Integer.MIN_VALUE;
        this.lastAimTick = Integer.MIN_VALUE;
        this.rotation.stop();
    }
}
