package swerve.modules.impl.combat.rotation;

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import swerve.utils.rotation.Rotation;
import swerve.utils.rotation.RotationController;

import java.util.Random;

/**
 * Общая база элементов ротации.
 *
 * <p>Порт из Private/src/java/org/privates/client/managers/module/impl/combat/auraelement/AbstractRotationElement.java
 * в объёме, нужном трём портированным ротациям (Intave/Matrix/HW): запрос
 * ротации, проверка видимости точки хитбокса и сглаживание по кратчайшей дуге.
 * Custom-специфичные хелперы (мультипоинты, jitter, Gaussian) не переносились —
 * их использует только непортированная CustomRot.
 */
public abstract class AbstractRotationElement implements RotationElement {

    /** Лениво: классы элементов создаются в конструкторе модуля, когда инстанс игры ещё не гарантирован. */
    protected static Minecraft mc() {
        return Minecraft.getInstance();
    }

    /** Свой ГПСЧ на элемент — как поле {@code random} в Private (без гонки за глобальный Math.random). */
    protected final Random random = new Random();

    /** Предыдущий выход элемента ({@code lerpRotation} в Private, изначально (0, 0)). */
    protected float lerpYaw;
    protected float lerpPitch;

    /** Переиспользуемый запрос ротации: в тике не аллоцируем. */
    private final Rotation request = new Rotation();

    /**
     * Запрос ротации (AbstractRotationElement.applyRotation).
     *
     * <p>В Private четвёртый аргумент называется {@code smoothTicks}, но по факту
     * уходит в {@code RotationComponent.update} как {@code priority} — сохраняем
     * исходную нумерацию, чтобы значения 4/3 в элементах не «поплыли».
     */
    protected void applyRotation(float yaw, float pitch, float yawSpeed, float pitchSpeed,
                                 int ticks, int priority) {
        this.request.set(yaw, pitch);
        RotationController.update(this.request, yawSpeed, pitchSpeed, ticks, priority);
    }

    /** Точка видна, если между глазами и ней нет коллизий блоков (isPointVisible из Private). */
    protected boolean isPointVisible(Vec3 point) {
        if (mc().player == null || mc().level == null) {
            return false;
        }
        Vec3 eye = mc().player.getEyePosition(1.0F);
        BlockHitResult result = mc().level.clip(new ClipContext(
                eye, point, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, mc().player));
        return result.getType() == HitResult.Type.MISS;
    }

    @Override
    public void resetLerp() {
        // AuraNew.updateTarget: при потере цели lerpRotation = Vector2f.ZERO.
        this.lerpYaw = 0.0F;
        this.lerpPitch = 0.0F;
    }

    @Override
    public void onEnable() {
    }

    /** По умолчанию Private останавливает ротацию; Intave/HW перекрывают это пустым телом. */
    @Override
    public void onDisable() {
        RotationController.stopRotation();
    }
}
