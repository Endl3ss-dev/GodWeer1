package org.privates.client.screen.hud.impl;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.player.PlayerEntity;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.managers.events.render.RenderArmorEvent;
import org.privates.client.screen.hud.AbstractHud;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;

import static org.privates.client.api.interfaces.IMinecraft.mc;
import static org.privates.client.screen.hud.IRenderer.font;

public class ArmorRenderer extends AbstractHud {

    @Getter
    @Setter
    private boolean blurEnabled = true;
    @Getter
    @Setter
    private float blurStrength = 10f;

    public ArmorRenderer() {
    }

    public void renderArmor(RenderArmorEvent event) {
        MatrixStack matrix = event.getMatrix();
        PlayerEntity player = mc.player;
        if (player == null) return;

        int armorValue = player.getTotalArmorValue();
        if (armorValue <= 0) return; // Don't render if no armor

        int screenWidth = mc.getMainWindow().getScaledWidth();
        int screenHeight = mc.getMainWindow().getScaledHeight();

        // Position at bottom left, above health bar (vanilla position)
        float barWidth = 81;
        float barHeight = 9;
        float barX = screenWidth / 2f - 91f;
        float barY = screenHeight - 39 - 10; // 10 pixels above health bar

        // Background
        RenderUtil.CustomStyled.draw(matrix, barX, barY, barWidth, barHeight, Round.of(barHeight / 2f), blurEnabled, blurStrength);

        // Armor bar - GRAY color
        float armorPercent = Math.min(1f, armorValue / 20f); // Max armor is 20
        if (armorPercent > 0) {
            int armorColor = ColorUtil.getColor(150, 150, 150, 255); // Gray color
            RenderUtil.Rounded.roundedRect(matrix, barX, barY, barWidth * armorPercent, barHeight, armorColor, Round.of(barHeight / 2f));
        }

        // Armor value text
        String text = String.valueOf(armorValue);
        font.drawCenter(matrix, text, barX + barWidth / 2f, barY + barHeight / 2f - 3.5f, -1, 7f);
    }
}
