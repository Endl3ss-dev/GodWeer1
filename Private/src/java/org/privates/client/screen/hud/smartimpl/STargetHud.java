package org.privates.client.screen.hud.smartimpl;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.entity.LivingEntity;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.mojang.blaze3d.platform.GlStateManager;
import org.privates.client.managers.events.render.Render2DEvent;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.impl.combat.AuraNew;
import org.privates.client.managers.module.settings.impl.DragSetting;
import org.privates.client.screen.hud.IRenderer;
import org.privates.client.utils.animation.Animation;
import org.privates.client.utils.animation.util.Easings;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.BlurRenderer;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.font.Fonts;
import org.privates.lib.util.time.StopWatch;

public class STargetHud implements IRenderer {
    private final DragSetting drag;
    private final StopWatch time = new StopWatch();
    private final Animation scaleAnimation = new Animation();
    private final Animation healthAnimation = new Animation();
    private LivingEntity target;

    @Getter
    @Setter
    private boolean blurEnabled = true;

    public STargetHud(DragSetting drag) {
        this.drag = drag;
    }

    @Override
    public void render(Render2DEvent event) {
        AuraNew auraNew = AuraNew.getInstance();

        if (mc.pointedEntity instanceof LivingEntity entity) {
            target = entity;
            time.reset();
        }

        if (auraNew.isEnabled() && auraNew.target() != null) {
            target = auraNew.target();
            time.reset();
        }

        scaleAnimation.update();
        healthAnimation.update();

        boolean shouldShow = target != null && !time.finished(1500);
        scaleAnimation.run(shouldShow ? 1.0 : 0.0, 0.3, Easings.EXPO_OUT, false);

        if (scaleAnimation.getValue() <= 0.01) {
            return;
        }

        MatrixStack matrix = event.getMatrix();
        float x = drag.position.x;
        float y = drag.position.y;
        final float width = 174f; // 120 * 1.45
        final float height = 36f;

        if (x == 0 || y == 0) {
            drag.position.set(mc.getMainWindow().getScaledWidth() / 2 - width / 2, 200);
            x = mc.getMainWindow().getScaledWidth() / 2 - width / 2;
            y = 200;
        }

        final float finalX = x;
        final float finalY = y;

        drag.size.set(width, height);

        matrix.push();
        matrix.translate(finalX + width / 2, finalY + height / 2, 0);
        matrix.scale((float) scaleAnimation.getValue(), (float) scaleAnimation.getValue(), 1);
        matrix.translate(-(finalX + width / 2), -(finalY + height / 2), 0);

        if (blurEnabled) {
            BlurRenderer.registerRenderCall(() -> {
                RenderUtil.Rounded.roundedRect(matrix, finalX, finalY, width, height,
                        ColorUtil.replAlpha(Theme.getInstance().clientColor(), 0.05f), Round.of(6));
            });
            BlurRenderer.draw(2);
        }

        RenderUtil.Rounded.roundedRect(matrix, finalX, finalY, width, height,
                ColorUtil.replAlpha(Theme.getInstance().clientColor(), 0.85f), Round.of(6));

        boolean isPlayer = target instanceof AbstractClientPlayerEntity;
        final float headSize = height - 4f;
        final float margin = 2f;

        if (isPlayer) {
            drawHead(matrix, (AbstractClientPlayerEntity) target, finalX + margin, finalY + margin, headSize);
        } else {
            RenderUtil.Rounded.roundedRect(matrix, finalX + margin, finalY + margin, headSize, headSize,
                    ColorUtil.getColor(60, 60, 60), Round.of(3));
            Fonts.SF_MEDIUM.drawCenter(matrix, "?", finalX + margin + headSize / 2, finalY + margin + headSize / 2 - 3,
                    ColorUtil.getColor(120, 120, 120), 8);
        }

        float infoX = finalX + margin + headSize + 4;
        float nameY = finalY + 4;
        String name = target.getName().getString();
        if (name.length() > 12) {
            name = name.substring(0, 12) + "...";
        }
        Fonts.SF_MEDIUM.draw(matrix, name, infoX, nameY, ColorUtil.getColor(255, 255, 255), 9); // 6 * 1.5

        float health = target.getHealth();
        float maxHealth = target.getMaxHealth();
        float currentHealthPercent = Math.min(1.0f, health / maxHealth);

        healthAnimation.run(currentHealthPercent, 0.2, Easings.EXPO_OUT, false);

        String healthText = String.format("HP: %.1f (%.0f%%)", health, healthAnimation.getValue() * 100);
        Fonts.SF_MEDIUM.draw(matrix, healthText, infoX, nameY + 10,
                ColorUtil.getColor(255, 255, 255), 7.5f); // 5 * 1.5

        float barX = infoX;
        float barY = finalY + height - margin - 6;
        float barWidth = width - margin - headSize - 8;
        float barHeight = 6f;

        RenderUtil.Rounded.roundedRect(matrix, barX, barY, barWidth, barHeight,
                ColorUtil.getColor(30, 30, 30, 150), Round.of(3));

        float filledWidth = barWidth * (float) healthAnimation.getValue();
        int healthColor = ColorUtil.overCol(ColorUtil.getColor(255, 50, 50),
                ColorUtil.getColor(50, 255, 50), (float) healthAnimation.getValue());

        RenderUtil.Rounded.roundedRect(matrix, barX, barY, filledWidth, barHeight,
                healthColor, Round.of(3));

        matrix.pop();
    }

    private void drawHead(MatrixStack matrix, AbstractClientPlayerEntity player, float x, float y, float size) {
        RenderUtil.Rounded.roundedRect(matrix, x, y, size, size,
                ColorUtil.replAlpha(Theme.getInstance().clientColor(), 0.6f), Round.of(3));

        mc.getTextureManager().bindTexture(player.getLocationSkin());
        float padding = 1;
        AbstractGui.blit(matrix, (int) (x + padding), (int) (y + padding),
                (int) (size - padding * 2), (int) (size - padding * 2),
                8F, 8F, 8F, 8F, 64F, 64F);

        GlStateManager.pushMatrix();
        GlStateManager.translatef(x + size / 2F, y + size / 2F, 0);
        GlStateManager.scalef(1.05F, 1.05F, 1.05F);
        GlStateManager.translatef(-(x + size / 2F), -(y + size / 2F), 0);
        AbstractGui.blit(matrix, (int) (x + padding), (int) (y + padding),
                (int) (size - padding * 2), (int) (size - padding * 2),
                40, 8, 8, 8, 64, 64);
        GlStateManager.popMatrix();
    }
}