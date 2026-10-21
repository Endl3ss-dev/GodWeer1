package swerve.modules.impl.combat.rotation;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.util.math.vector.Vector2f;

/**
 * Интерфейс ротации для {@link swerve.modules.impl.combat.Aura}.
 * Решает, под каким углом смотреть на цель в текущем тике.
 * <p>
 * Реализации не должны содержать игровой логики (выбор цели, атака, криты) —
 * только вычисление целевых углов yaw/pitch.
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
     * @param target  цель (может быть null, если цели нет)
     * @param mc      экземпляр Minecraft
     * @param current текущие углы игрока (yaw, pitch) в градусах
     * @return Vector2f с целевыми yaw и pitch в градусах
     */
    Vector2f calculateRotation(LivingEntity target, Minecraft mc, Vector2f current);
}
