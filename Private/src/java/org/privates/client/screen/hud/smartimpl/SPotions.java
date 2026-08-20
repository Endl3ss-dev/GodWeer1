package org.privates.client.screen.hud.smartimpl;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.texture.PotionSpriteUploader;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.StringUtils;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.managers.events.render.Render2DEvent;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.settings.impl.DragSetting;
import org.privates.client.screen.hud.AbstractHud;
import org.privates.client.screen.hud.IRenderer;
import org.privates.client.utils.animation.Animation;
import org.privates.client.utils.animation.util.Easings;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.BlurRenderer;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.font.Fonts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class SPotions extends AbstractHud implements IRenderer {
    private final DragSetting drag;
    private final Animation scaleAnimation = new Animation();

    @Getter
    @Setter
    private boolean blurEnabled = true;

    private static final Set<Effect> NEGATIVE_EFFECTS = Set.of(
            Effects.BLINDNESS, Effects.HUNGER, Effects.MINING_FATIGUE,
            Effects.NAUSEA, Effects.POISON, Effects.SLOWNESS, Effects.WEAKNESS,
            Effects.WITHER, Effects.LEVITATION, Effects.UNLUCK, Effects.GLOWING
    );

    public SPotions(DragSetting drag) {
        this.drag = drag;
    }

    @Override
    public void render(Render2DEvent event) {
        MatrixStack matrix = event.getMatrix();
        Collection<EffectInstance> activePotions = mc.player.getActivePotionEffects();

        List<EffectInstance> potions = new ArrayList<>();
        for (EffectInstance potion : activePotions) {
            if (potion.getPotion() != Effects.UNLUCK) {
                potions.add(potion);
            }
        }

        scaleAnimation.update();
        boolean shouldShow = !potions.isEmpty() || mc.currentScreen != null;
        scaleAnimation.run(shouldShow ? 1.0 : 0.0, 0.3, Easings.EXPO_OUT, false);

        if (scaleAnimation.getValue() <= 0.01) return;

        float x = drag.position.x;
        float y = drag.position.y;
        final float width = 87f; // 60 * 1.45
        final float titleHeight = 16f;
        final float itemHeight = 18f;
        final float spacing = 4f;
        final float totalHeight = titleHeight + spacing + potions.size() * itemHeight;

        if (x == 0 || y == 0) {
            drag.position.set(10, 150);
            x = 10;
            y = 150;
        }

        final float finalX = x;
        final float finalY = y;

        drag.size.set(width, totalHeight);

        matrix.push();
        matrix.translate(finalX + width / 2, finalY + totalHeight / 2, 0);
        matrix.scale((float) scaleAnimation.getValue(), (float) scaleAnimation.getValue(), 1);
        matrix.translate(-(finalX + width / 2), -(finalY + totalHeight / 2), 0);

        if (blurEnabled) {
            BlurRenderer.registerRenderCall(() -> {
                RenderUtil.Rounded.roundedRect(matrix, finalX, finalY, width, titleHeight,
                        ColorUtil.replAlpha(Theme.getInstance().clientColor(), 0.05f), Round.of(4));
                float offsetY = finalY + titleHeight + spacing;
                for (int i = 0; i < potions.size(); i++) {
                    RenderUtil.Rounded.roundedRect(matrix, finalX, offsetY, width, itemHeight,
                            ColorUtil.replAlpha(Theme.getInstance().clientColor(), 0.05f), Round.of(3));
                    offsetY += itemHeight;
                }
            });
            BlurRenderer.draw(2);
        }

        RenderUtil.Rounded.roundedRect(matrix, finalX, finalY, width, titleHeight,
                ColorUtil.replAlpha(Theme.getInstance().clientColor(), 0.85f), Round.of(4));

        String title = "Active Potions";
        Fonts.SF_MEDIUM.drawCenter(matrix, title, finalX + width / 2, finalY + 6,
                ColorUtil.getColor(255, 255, 255), 6);

        float offsetY = finalY + titleHeight + spacing;
        for (EffectInstance potion : potions) {
            renderPotionEffect(matrix, potion, finalX, offsetY, width, itemHeight);
            offsetY += itemHeight;
        }

        matrix.pop();
    }

    private void renderPotionEffect(MatrixStack matrix, EffectInstance potion, float x, float y, float width, float height) {
        RenderUtil.Rounded.roundedRect(matrix, x, y, width, height,
                ColorUtil.replAlpha(Theme.getInstance().clientColor(), 0.85f), Round.of(3));

        Effect effect = potion.getPotion();
        String name = effect.getDisplayName().getString();
        if (name.length() > 8) name = name.substring(0, 8);
        if (potion.getAmplifier() > 0) {
            name += " " + (potion.getAmplifier() + 1);
        }
        String duration = StringUtils.ticksToElapsedTime(potion.getDuration());

        float iconSize = 12f;
        float iconX = x + 3;
        float iconY = y + (height - iconSize) / 2;

        PotionSpriteUploader uploader = mc.getPotionSpriteUploader();
        TextureAtlasSprite sprite = uploader.getSprite(effect);
        mc.getTextureManager().bindTexture(sprite.getAtlasTexture().getTextureLocation());
        AbstractGui.blit(matrix, (int) iconX, (int) iconY, 0, (int) iconSize, (int) iconSize, sprite);

        float nameX = iconX + iconSize + 3;
        float nameY = y + 3;
        Fonts.SF_MEDIUM.draw(matrix, name, nameX, nameY, ColorUtil.getColor(255, 255, 255), 7.5f); // 5 * 1.5

        float durationY = y + height - 8;
        Fonts.SF_MEDIUM.draw(matrix, duration, nameX, durationY, ColorUtil.getColor(255, 255, 255), 6); // 4 * 1.5
    }
}