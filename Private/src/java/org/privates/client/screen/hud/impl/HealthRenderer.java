package org.privates.client.screen.hud.impl;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.MathHelper;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.managers.events.render.RenderHealthEvent;
import org.privates.client.screen.hud.AbstractHud;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;

import static org.privates.client.api.interfaces.IMinecraft.mc;
import static org.privates.client.screen.hud.IRenderer.font;

public class HealthRenderer extends AbstractHud {

    public HealthRenderer() {
    }

    public void renderHealth(RenderHealthEvent event) {
        MatrixStack matrix = event.getMatrix();
        PlayerEntity player = mc.player;
        if (player == null) return;

        int screenWidth = mc.getMainWindow().getScaledWidth();
        int screenHeight = mc.getMainWindow().getScaledHeight();

        // Vanilla health bar position (bottom left, lower)
        int leftX = screenWidth / 2 - 91;
        int bottomY = screenHeight - 39;

        float health = player.getHealth();
        float maxHealth = player.getMaxHealth();
        float absorb = player.getAbsorptionAmount();

        // Position at vanilla location
        float barWidth = 81;
        float barHeight = 9;
        float barX = leftX;
        float barY = bottomY;

        RenderUtil.CustomStyled.draw(matrix, barX, barY, barWidth, barHeight, Round.of(barHeight / 2f), true, 10f);

        // Draw health fill - RED color
        float healthPercent = Math.min(1.0f, health / maxHealth);
        if (healthPercent > 0) {
            int healthColor = ColorUtil.RED; // Pure red color
            RenderUtil.Rounded.roundedRect(matrix, barX, barY, barWidth * healthPercent, barHeight, healthColor, Round.of(barHeight / 2f));
        }

        // Draw absorption overlay
        if (absorb > 0) {
            float absorbPercent = Math.min(1.0f, absorb / maxHealth);
            int absorbColor = ColorUtil.getColor(255, 215, 0, 150); // Gold color
            RenderUtil.Rounded.roundedRect(matrix, barX, barY, barWidth * absorbPercent, barHeight, absorbColor, Round.of(barHeight / 2f));
        }

        // Draw regeneration indicator
        if (player.isPotionActive(Effects.REGENERATION)) {
            int regenColor = ColorUtil.getColor(255, 100, 255, 150);
            float pulse = (float) (1.0f + 0.3f * Math.sin(System.currentTimeMillis() / 200.0));
            RenderUtil.Rounded.roundedOutline(matrix, barX - 1, barY - 1, barWidth + 2, barHeight + 2, 1.5f * pulse, regenColor, Round.of(barHeight / 2f));
        }

        // Draw health text
        String healthText = String.format("%.1f", health);
        font.drawCenter(matrix, healthText, barX + barWidth / 2f, barY + barHeight / 2f - 3.5f, -1, 7f);
    }
}
