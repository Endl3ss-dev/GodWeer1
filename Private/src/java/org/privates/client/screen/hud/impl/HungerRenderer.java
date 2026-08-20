package org.privates.client.screen.hud.impl;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effects;
import net.minecraft.util.FoodStats;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.managers.events.render.RenderHungerEvent;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.screen.hud.AbstractHud;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;

import static org.privates.client.api.interfaces.IMinecraft.mc;
import static org.privates.client.screen.hud.IRenderer.font;

public class HungerRenderer extends AbstractHud {

    public HungerRenderer() {
    }

    public void renderHunger(RenderHungerEvent event) {
        MatrixStack matrix = event.getMatrix();
        PlayerEntity player = mc.player;
        if (player == null) return;

        int screenWidth = mc.getMainWindow().getScaledWidth();
        int screenHeight = mc.getMainWindow().getScaledHeight();

        // Vanilla hunger bar position (bottom right, lower)
        int rightX = screenWidth / 2 + 91;
        int bottomY = screenHeight - 39;

        FoodStats foodStats = player.getFoodStats();
        int foodLevel = foodStats.getFoodLevel();
        float saturation = foodStats.getSaturationLevel();

        // Draw hunger bar background
        float barWidth = 81;
        float barHeight = 9;
        float barX = rightX - barWidth;
        float barY = bottomY;

        RenderUtil.CustomStyled.draw(matrix, barX, barY, barWidth, barHeight, Round.of(barHeight / 2f), true, 10f);

        // Draw hunger fill
        float hungerPercent = foodLevel / 20.0f;
        if (hungerPercent > 0) {
            int hungerColor = ColorUtil.interpolateColor(ColorUtil.RED, Theme.getInstance().clientColor(), hungerPercent);
            RenderUtil.Rounded.roundedRect(matrix, barX, barY, barWidth * hungerPercent, barHeight, hungerColor, Round.of(barHeight / 2f));
        }

        // Draw saturation overlay
        if (saturation > 0) {
            float saturationPercent = Math.min(1.0f, saturation / 20.0f);
            int saturationColor = ColorUtil.getColor(255, 165, 0, 100); // Orange color
            RenderUtil.Rounded.roundedRect(matrix, barX, barY, barWidth * saturationPercent, barHeight, saturationColor, Round.of(barHeight / 2f));
        }

        // Draw hunger effect indicator
        if (player.isPotionActive(Effects.HUNGER)) {
            int hungerEffectColor = ColorUtil.getColor(88, 118, 83, 150);
            RenderUtil.Rounded.roundedOutline(matrix, barX - 1, barY - 1, barWidth + 2, barHeight + 2, 1.5f, hungerEffectColor, Round.of(barHeight / 2f));
        }

        // Draw hunger text
        String hungerText = String.valueOf(foodLevel);
        font.drawCenter(matrix, hungerText, barX + barWidth / 2f, barY + barHeight / 2f - 3.5f, -1, 7f);
    }
}
