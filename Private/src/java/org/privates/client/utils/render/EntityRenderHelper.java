package org.privates.client.utils.render;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

public class EntityRenderHelper {
    private static final Minecraft mc = Minecraft.getInstance();

    /**
     * Патч для исправления краша с рендером стрел в игроках
     */
    public static void patchArrowRender() {
        if (mc.world == null) return;

        try {
            // Очищаем стрелы у всех игроков в радиусе рендера
            for (Entity entity : mc.world.getAllEntities()) {
                if (entity instanceof PlayerEntity) {
                    PlayerEntity player = (PlayerEntity) entity;
                    // Устанавливаем количество стрел в 0 чтобы избежать краша
                    player.setArrowCountInEntity(0);
                }
            }
        } catch (Exception e) {
            // Игнорируем ошибки
        }
    }
} 