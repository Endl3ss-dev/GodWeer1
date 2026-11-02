package swerve.modules.impl.combat.rotation;

import net.minecraft.world.entity.LivingEntity;

/**
 * Элемент ротации ауры (IRotationElement из Private).
 *
 * <p>В Private элемент вызывается каждый тик, пока цель жива, и сам решает,
 * как довернуть тело; состояние сглаживания живёт внутри элемента, а
 * {@link #resetLerp()} повторяет обнуление {@code lerpRotation} в
 * {@code AuraNew.reset()} / {@code updateTarget()} (там оно сбрасывается в (0, 0)).
 */
public interface RotationElement {

    void update(LivingEntity target);

    void onEnable();

    void onDisable();

    void resetLerp();
}
