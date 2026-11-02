package swerve.ui.hud;

import net.minecraft.client.gui.GuiGraphics;

/**
 * Неперетаскиваемый HUD-слой (индикаторы поверх экрана, привязанные
 * к центру/краям): рисуется в общем HudRenderCallback после элементов.
 */
@FunctionalInterface
public interface HudOverlay {

    /** Рисовка слоя; сам слой решает, активен ли он сейчас. */
    void renderOverlay(GuiGraphics context);
}
