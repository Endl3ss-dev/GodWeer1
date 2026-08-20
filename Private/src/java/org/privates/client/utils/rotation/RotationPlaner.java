package org.privates.client.utils.rotation;

import net.minecraft.client.Minecraft;
import org.privates.client.managers.module.impl.movement.Rotation;

public class RotationPlaner {
    private static final Minecraft mc = Minecraft.getInstance();
    private static Rotation targetRotation;
    private static long startTime;
    private static int rotationSpeed;
    private static int minSpeed;
    private static int maxSpeed;
    private static boolean rotating = false;

    /**
     * Устанавливает серверное вращение с заданными параметрами
     *
     * @param rotation целевое вращение
     * @param speed    скорость вращения
     * @param minSpeed минимальная скорость вращения
     * @param maxSpeed максимальная скорость вращения
     */
    public static void setRotationServere(Rotation rotation, int speed, int minSpeed, int maxSpeed) {
        targetRotation = rotation;
        rotationSpeed = speed;
        RotationPlaner.minSpeed = minSpeed;
        RotationPlaner.maxSpeed = maxSpeed;
        startTime = System.currentTimeMillis();
        rotating = true;

        // Применяем вращение сразу к игроку
        mc.player.rotationYaw = rotation.getYaw();
    }

    /**
     * Обновляет вращение игрока, плавно поворачивая к целевому вращению
     * Вызывается каждый тик
     */
    public static void update() {
        if (!rotating || mc.player == null) return;

        long elapsed = System.currentTimeMillis() - startTime;
        if (elapsed > rotationSpeed) {
            // Вращение завершено
            rotating = false;
            return;
        }

        float progress = (float) elapsed / rotationSpeed;
        float yawDiff = targetRotation.getYaw() - mc.player.prevRotationYaw;

        // Нормализуем разницу yaw
        while (yawDiff > 180) yawDiff -= 360;
        while (yawDiff < -180) yawDiff += 360;

        // Ограничиваем скорость вращения
        float yawStep = Math.max(minSpeed, Math.min(maxSpeed, Math.abs(yawDiff))) * Math.signum(yawDiff);

        // Применяем вращение с учетом прогресса
        float newYaw = mc.player.prevRotationYaw + yawStep * progress;

        // Ограничиваем pitch в пределах -90 до 90 градусов

        mc.player.rotationYaw = newYaw;
    }

    /**
     * Проверяет, выполняется ли в данный момент вращение
     *
     * @return true если выполняется вращение, иначе false
     */
    public static boolean isRotating() {
        return rotating;
    }

    /**
     * Останавливает текущее вращение
     */
    public static void stopRotation() {
        rotating = false;
    }
}
