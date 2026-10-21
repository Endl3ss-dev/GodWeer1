package swerve.modules.impl.combat.rotation;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;

/**
 * Интерфейс ротации для {@link swerve.modules.impl.combat.Aura}.
 * Решает, под каким углом смотреть на цель в текущем тике.
 * <p>
 * Реализации не должны содержать игровой логики (выбор цели, атака, криты) —
 * только вычисление целевых углов yaw/pitch.
 * <p>
 * Выходные значения записываются в {@code out}: out[0] = yaw, out[1] = pitch.
 */
public interface AuraRotation {

    /** Отображаемое имя режима ротации (для настройки в GUI). */
    String getName();

    /**
     * Вызывается при включении модуля. Можно использовать для сброса состояния.
     */
    default void onEnable() {
    }

    /**
     * Вызывается при выключении модуля.
     */
    default void onDisable() {
    }

    /**
     * Вычисляет целевые углы поворота к цели.
     *
     * @param target цель (может быть null, если цели нет)
     * @param mc     экземпляр Minecraft
     * @param currentYaw  текущий yaw игрока в градусах
     * @param currentPitch текущий pitch игрока в градусах
     * @param out   массив из 2 элементов: out[0] = целевой yaw, out[1] = целевой pitch
     */
    void calculateRotation(LivingEntity target, Minecraft mc, float currentYaw, float currentPitch, float[] out);
}
