package org.privates.client.utils.rotation;

import net.minecraft.client.Minecraft;

public class FreeLookHandler {
    private static final Minecraft mc = Minecraft.getInstance();
    private static float freeYaw;
    private static boolean enabled = false;

    /**
     * Получает текущий yaw для свободного обзора
     *
     * @return текущий yaw игрока или сохраненный yaw если включен режим свободного обзора
     */
    public static float getFreeYaw() {
        return enabled ? freeYaw : mc.player.rotationYaw;
    }

    /**
     * Получает текущий pitch для свободного обзора
     *
     * @return текущий pitch игрока или сохраненный pitch если включен режим свободного обзора
     */


    /**
     * Устанавливает yaw для свободного обзора
     *
     * @param yaw новое значение yaw
     */
    public static void setFreeYaw(float yaw) {
        freeYaw = yaw;
    }

    /**
     * Устанавливает pitch для свободного обзора
     *
     * @param pitch новое значение pitch
     */


    /**
     * Включает или выключает режим свободного обзора
     *
     * @param state новое состояние
     */
    public static void setEnabled(boolean state) {
        if (state && !enabled) {
            // Сохраняем текущие значения при включении
            freeYaw = mc.player.rotationYaw;
        }
        enabled = state;
    }

    /**
     * Проверяет, включен ли режим свободного обзора
     *
     * @return true если режим включен, иначе false
     */
    public static boolean isEnabled() {
        return enabled;
    }
}
