package swerve.utils.rotation;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;

/**
 * Пара углов взгляда (yaw/pitch).
 *
 * <p>Порт из Private/src/java/org/privates/client/managers/component/impl/rotation/Rotation.java
 * в минимальном виде: ломбок-аннотации заменены явными методами, третий-лицевой
 * разворот камеры из 1.16.5 не нужен (в 1.21.4 им занимается сам {@code Camera}).
 */
public final class Rotation {

    private float yaw;
    private float pitch;

    public Rotation() {
    }

    public Rotation(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public float getYaw() {
        return this.yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    /** Переиспользование объекта в тике вместо аллокации новой пары углов. */
    public void set(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public static float cameraYaw() {
        // При молчаливой ротации камера отвязана от тела: коррекция движения обязана
        // считать ввод от свободного обзора, а не от разворота на цель.
        if (FreeLook.active()) {
            return Mth.wrapDegrees(FreeLook.yaw());
        }
        return Mth.wrapDegrees(Minecraft.getInstance().gameRenderer.getMainCamera().getYRot());
    }
}
