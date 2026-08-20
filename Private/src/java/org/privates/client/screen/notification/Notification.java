package org.privates.client.screen.notification;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.PotionSpriteUploader;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.mojang.blaze3d.systems.RenderSystem;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.impl.misc.Notifications;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.draw.ScissorUtil;
import org.privates.client.utils.render.font.Fonts;

import java.awt.*;

@Getter
@Setter
public class Notification {

    private final String text;
    private final NotificationType type;
    private final long time;
    private final float ms;
    private final Object data;
    private final Integer highlightColor;

    private float scaleV, scale;
    private float slideAnim = 0f;
    private float fadeAnim = 0f;
    private float animatedWidth = 0f;
    private float targetWidth;
    private float height;
    private float x;

    public Notification(NotificationType type, String text, float ms) {
        this(type, text, ms, null, null);
    }

    public Notification(NotificationType type, String text, float ms, Object data) {
        this(type, text, ms, data, null);
    }

    public Notification(NotificationType type, String text, float ms, Object data, Integer highlightColor) {
        this.type = type;
        this.text = text;
        this.time = System.currentTimeMillis();
        this.ms = ms;
        this.data = data;
        this.highlightColor = highlightColor;

        float iconSize = 10f;
        float textSize = 7f;
        float padding = 6f;

        float iconWidth = type.hasCustomIcon() ? 10f : Fonts.HUD.getWidth(String.valueOf(type.icon), iconSize);
        float separatorWidth = Notifications.getInstance().showSeparators().getValue() ? (0.25f + 6f) : 0f;

        // Убираем маркеры §h§ из текста для правильного расчета ширины
        String cleanText = text.replace("§h§", "");
        float textWidth = Fonts.SF_MEDIUM.getWidth(cleanText, textSize);

        float iconPart = Notifications.getInstance().showIcons().getValue() ? (iconWidth + 6f) : 0f;

        this.targetWidth = padding + iconPart + separatorWidth + textWidth + padding;
        this.animatedWidth = 0f;
        this.height = 18f;
        this.x = Minecraft.getInstance().getMainWindow().getScaledWidth() / 2f;
        scale = Notifications.getInstance().scale().getValue();
        slideAnim = 0f;
        fadeAnim = 0f;
    }

    public void render(MatrixStack stack, float y) {
        long currentTime = System.currentTimeMillis();
        long elapsed = currentTime - time;

        float fadeInDuration = 200f;
        float fadeOutDuration = 200f;
        float totalDuration = ms;

        float targetFade = 1f;
        float targetSlide = 1f;

        if (elapsed < fadeInDuration) {
            targetFade = elapsed / fadeInDuration;
            targetSlide = elapsed / fadeInDuration;
        } else if (elapsed > totalDuration - fadeOutDuration) {
            float remaining = totalDuration - elapsed;
            targetFade = remaining / fadeOutDuration;
            targetSlide = remaining / fadeOutDuration;
        }

        float animSpeed = Notifications.getInstance().animationSpeed().getValue() * 1.5f;
        fadeAnim = Mathf.fast(fadeAnim, targetFade, animSpeed);
        slideAnim = Mathf.fast(slideAnim, targetSlide, animSpeed);
        scaleV = Mathf.fast(scaleV, scale, 16);
        animatedWidth = Mathf.fast(animatedWidth, targetWidth * slideAnim, 18);

        float smoothSlide = easeOutBack(slideAnim);
        float smoothFade = easeOutQuint(fadeAnim);

        float currentX = x - animatedWidth / 2f;

        RenderSystem.pushMatrix();

        float yOffset = (1f - smoothSlide) * 15f;

        RenderSystem.translatef((currentX + animatedWidth / 2f), (y + height / 2f + yOffset), 1);
        RenderSystem.scalef(scaleV, scaleV, scaleV);
        RenderSystem.translatef(-(currentX + animatedWidth / 2f), -(y + height / 2f + yOffset), 1);

        float currentY = y + yOffset;

        float containerRadius = 6f;
        float blurStrength = 10f * smoothFade;

        RenderUtil.CustomStyled.draw(stack, currentX, currentY, animatedWidth, height, Round.of(containerRadius), true, blurStrength);

        float titleHeight = 18f;
        int bgAlpha = (int) (0.2F * 255 * smoothFade);
        RenderUtil.Rounded.smooth(stack, currentX, currentY, animatedWidth, height,
                ColorUtil.replAlpha(new Color(0xFF000000, true).getRGB(), bgAlpha),
                Round.of(containerRadius, containerRadius, containerRadius, containerRadius));

        float iconSize = 10f;
        float textSize = 7f;
        float padding = 6f;
        float currentContentX = currentX + padding;
        float iconY = currentY + (height - iconSize) / 2f;

        int iconAlpha = (int) (255 * smoothFade);

        boolean showIcons = Notifications.getInstance().showIcons().getValue();
        boolean showSeparators = Notifications.getInstance().showSeparators().getValue();

        if (showIcons) {
            if (type == NotificationType.EFFECT_END && data instanceof Effect) {
                renderEffectIcon(stack, (Effect) data, currentContentX, iconY, smoothFade);
            } else if ((type == NotificationType.ITEM_PICKUP || type == NotificationType.SWAP) && data instanceof ItemStack) {
                renderItemIcon(stack, (ItemStack) data, currentContentX, iconY, smoothFade);
            } else {
                int iconColor = Theme.getInstance().clientColor();
                int finalIconColor = (iconAlpha << 24) | (iconColor & 0x00FFFFFF);
                Fonts.HUD.draw(stack, String.valueOf(type.icon),
                        currentContentX,
                        iconY,
                        finalIconColor,
                        iconSize);
            }

            float iconWidth = type.hasCustomIcon() ? 10f : Fonts.HUD.getWidth(String.valueOf(type.icon), iconSize);
            currentContentX += iconWidth + 6f;
        }

        if (showSeparators && showIcons) {
            float separatorY = currentY + (height - iconSize + 2.5f) / 2f;
            float separatorHeight = iconSize - 2.5f;

            int separatorAlpha = (int) (13 * smoothFade);
            RenderUtil.Rounded.roundedRect(stack, currentContentX, separatorY, 0.25f, separatorHeight,
                    ColorUtil.getColor(255, 255, 255, separatorAlpha), Round.of(0.25f));

            currentContentX += 0.25f + 6f;
        }

        float textX = currentContentX;
        float textY = currentY + (height - textSize) / 2f;

        float maxTextWidth = animatedWidth - (textX - currentX) - padding;

        // Парсим текст для выделения ключевых слов
        // Формат: обычный текст + §h§ключевое слово§h§ + обычный текст
        String[] parts = text.split("§h§");

        int textAlpha = (int) (235 * smoothFade);
        int whiteColor = ColorUtil.getColor(255, 255, 255, textAlpha);

        // Яркий цвет для ключевого слова
        int highlightTextColor = highlightColor != null ?
                ColorUtil.replAlpha(highlightColor, textAlpha) :
                ColorUtil.replAlpha(Theme.getInstance().clientColor(), textAlpha);

        float currentTextX = textX;

        if (parts.length > 1) {
            // Есть выделенные части
            for (int i = 0; i < parts.length; i++) {
                if (parts[i].isEmpty()) continue;

                // Нечетные индексы (1, 3, 5...) - это выделенный текст
                boolean isHighlighted = (i % 2 == 1);
                int color = isHighlighted ? highlightTextColor : whiteColor;

                float partWidth = Fonts.SF_MEDIUM.getWidth(parts[i], textSize);

                if (currentTextX - textX + partWidth > maxTextWidth && maxTextWidth > 0) {
                    ScissorUtil.enableScissor(textX, currentY, maxTextWidth + 5, height);
                    Fonts.SF_MEDIUM.draw(stack, parts[i], currentTextX, textY, color, textSize);
                    ScissorUtil.disableScissor();
                    break;
                } else if (maxTextWidth > 0) {
                    Fonts.SF_MEDIUM.draw(stack, parts[i], currentTextX, textY, color, textSize);
                    currentTextX += partWidth;
                }
            }
        } else {
            // Нет выделенных частей - рендерим как обычно белым цветом
            float textWidth = Fonts.SF_MEDIUM.getWidth(text, textSize);

            if (textWidth > maxTextWidth && maxTextWidth > 0) {
                ScissorUtil.enableScissor(textX, currentY, maxTextWidth + 5, height);
                Fonts.SF_MEDIUM.draw(stack, text, textX, textY, whiteColor, textSize);
                ScissorUtil.disableScissor();
            } else if (maxTextWidth > 0) {
                Fonts.SF_MEDIUM.draw(stack, text, textX, textY, whiteColor, textSize);
            }
        }

        RenderSystem.popMatrix();
    }

    private void renderEffectIcon(MatrixStack stack, Effect effect, float x, float y, float alpha) {
        Minecraft mc = Minecraft.getInstance();
        float iconSize = 10f;

        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, alpha);

        PotionSpriteUploader uploader = mc.getPotionSpriteUploader();
        TextureAtlasSprite sprite = uploader.getSprite(effect);
        mc.getTextureManager().bindTexture(sprite.getAtlasTexture().getTextureLocation());

        AbstractGui.blit(stack, (int) x, (int) y, 0, (int) iconSize, (int) iconSize, sprite);

        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
        RenderSystem.popMatrix();
    }

    private void renderItemIcon(MatrixStack stack, ItemStack itemStack, float x, float y, float alpha) {
        if (itemStack.isEmpty()) return;

        Minecraft mc = Minecraft.getInstance();

        RenderSystem.pushMatrix();
        RenderHelper.enableStandardItemLighting();
        RenderSystem.enableBlend();
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, alpha);

        float scale = 0.625f;
        RenderSystem.translatef(x, y, 0);
        RenderSystem.scalef(scale, scale, 1);

        mc.getItemRenderer().renderItemAndEffectIntoGUI(itemStack, 0, 0);

        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
        RenderHelper.disableStandardItemLighting();
        RenderSystem.popMatrix();
    }

    private float easeOutBack(float t) {
        float c1 = 1.70158f;
        float c3 = c1 + 1;
        return 1 + c3 * (float) Math.pow(t - 1, 3) + c1 * (float) Math.pow(t - 1, 2);
    }

    private float easeOutQuint(float t) {
        return 1 - (float) Math.pow(1 - t, 5);
    }

    public float getWidth() {
        return animatedWidth;
    }
}