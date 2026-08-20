package org.privates.client.screen.hud.impl;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.mojang.blaze3d.platform.GlStateManager;
import org.privates.client.managers.component.impl.drag.DragComponent;
import org.privates.client.managers.events.render.Render2DEvent;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.settings.impl.DragSetting;
import org.privates.client.screen.hud.AbstractHud;
import org.privates.client.screen.hud.IConfigurableHud;
import org.privates.client.screen.hud.IRenderer;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.draw.ScissorUtil;
import org.privates.client.utils.render.font.Fonts;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ArmorHudRenderer extends AbstractHud implements IRenderer, IConfigurableHud {
    private final DragSetting drag;
    private final List<ItemStack> currentArmor = new ArrayList<>();
    private final float[] percent = new float[]{0, 0, 0, 0};

    private float animatedWidth;
    private float animatedHeight;
    @Getter
    @Setter
    private float scaleV;
    @Getter
    @Setter
    private boolean blurEnabled = true;
    @Getter
    @Setter
    private float blurStrength = 10f;

    public float scale = 0.9f;
    public float titleDarkness = 0.0f;
    public boolean showTitleIcon = true;
    public boolean showCount = true;
    public boolean showTitle = true;
    public boolean dynamicRects = false;

    public ArmorHudRenderer(DragSetting drag) {
        this.drag = drag;
    }

    @Override
    public List<DragComponent.HudSetting> getHudSettings() {
        return Arrays.asList(
                new DragComponent.HudSetting("Scale", DragComponent.SettingType.SLIDER,
                        () -> this.scale,
                        v -> this.scale = v,
                        0.5f, 2.0f, 0.1f),

                new DragComponent.HudSetting("Title Darkness", DragComponent.SettingType.SLIDER,
                        () -> this.titleDarkness,
                        v -> this.titleDarkness = v,
                        0.0f, 1.0f, 0.05f),

                new DragComponent.HudSetting("Title Icon", DragComponent.SettingType.BOOLEAN,
                        () -> this.showTitleIcon ? 1f : 0f,
                        v -> this.showTitleIcon = v > 0.5f),

                new DragComponent.HudSetting("Show Count", DragComponent.SettingType.BOOLEAN,
                        () -> this.showCount ? 1f : 0f,
                        v -> this.showCount = v > 0.5f),

                new DragComponent.HudSetting("Show Title", DragComponent.SettingType.BOOLEAN,
                        () -> this.showTitle ? 1f : 0f,
                        v -> this.showTitle = v > 0.5f),

                new DragComponent.HudSetting("Dynamic Rects", DragComponent.SettingType.BOOLEAN,
                        () -> this.dynamicRects ? 1f : 0f,
                        v -> this.dynamicRects = v > 0.5f)
        );
    }

    private float getResolutionScale() {
        int width = mc.getMainWindow().getWidth();
        int height = mc.getMainWindow().getHeight();

        if (width >= 3840 && height >= 2160) {
            return 1f;
        } else if (width >= 2560 && height >= 1440) {
            return 1.0f;
        } else if (width >= 1920 && height >= 1080) {
            return 1f;
        } else {
            return 1f;
        }
    }

    @Override
    public void render(Render2DEvent event) {
        render(event, false);
    }

    private void addPreviewArmor() {
        currentArmor.clear();
        currentArmor.add(new ItemStack(Items.NETHERITE_HELMET));
        currentArmor.add(new ItemStack(Items.NETHERITE_CHESTPLATE));
        currentArmor.add(new ItemStack(Items.NETHERITE_LEGGINGS));
        currentArmor.add(new ItemStack(Items.NETHERITE_BOOTS));
        currentArmor.forEach(item -> {
            if (item.isDamageable()) {
                item.setDamage(item.getMaxDamage() / 3);
            }
        });
    }

    private void updateArmorList() {
        currentArmor.clear();
        if (mc.player != null) {
            List<ItemStack> armor = mc.player.inventory.armorInventory.stream()
                    .filter(stack -> !stack.isEmpty())
                    .collect(Collectors.toList());
            Collections.reverse(armor);
            currentArmor.addAll(armor);
        }
    }

    public void render(Render2DEvent event, boolean glow) {
        MatrixStack matrix = event.getMatrix();
        boolean isPreview = mc.currentScreen instanceof ChatScreen;

        if (isPreview) {
            addPreviewArmor();
        } else {
            updateArmorList();
        }

        float resScale = getResolutionScale();
        float finalScale = resScale * scale;
        float x = drag.position.x;
        float y = drag.position.y;

        String name = "Armor";
        float padding = 6f * finalScale;
        float fontSize = 7f * finalScale;
        float iconSize = 10f * finalScale;
        float containerRadius = 6f * finalScale;
        float titleBoxHeight = showTitle ? 18f * finalScale : 0f;
        float itemRowHeight = 18f * finalScale;

        float titleIconSize = 10f * finalScale;
        float titleIconWidth = showTitleIcon ? (titleIconSize + 6f * finalScale) : 0f;
        float titleTextWidth = font.getWidth(name, fontSize);
        float titleIconTextGap = 7.5f * finalScale;
        float countBoxWidth = showCount ? (font.getWidth(String.valueOf(currentArmor.size()), 6f * finalScale) + 8f * finalScale) : 0f;

        float titleWidth = padding + titleIconWidth + titleTextWidth + (showCount && dynamicRects ? (titleIconTextGap + countBoxWidth) : 0f) + padding;
        float maxWidth = titleWidth;
        float durabilityBarWidth = 45 * finalScale;
        float durabilityTextWidth = font.getWidth("100%", fontSize);

        if (!dynamicRects) {
            maxWidth = Math.max(maxWidth, padding + iconSize + padding + durabilityBarWidth + padding + durabilityTextWidth + padding);
        }

        float targetHeight = titleBoxHeight;
        if (!currentArmor.isEmpty()) {
            if (dynamicRects) {
                targetHeight += currentArmor.size() * (itemRowHeight + 3f * finalScale);
            } else {
                targetHeight += currentArmor.size() * itemRowHeight;
            }
        }

        animatedWidth = Mathf.fast(animatedWidth, maxWidth, 14);
        animatedHeight = Mathf.fast(animatedHeight, targetHeight, 14);

        boolean hasContent = !currentArmor.isEmpty();
        scaleV = Mathf.fast(scaleV, hasContent ? 1 : 0, 16);

        if (Mathf.round(scaleV, .1d) <= 0) return;

        boolean alignRight = x > mc.getMainWindow().getScaledWidth() / 2f;

        matrix.push();
        matrix.translate((x + animatedWidth / 2F), (y + animatedHeight / 2f), 0);
        matrix.scale(scaleV, scaleV, scaleV);
        matrix.translate(-(x + animatedWidth / 2F), -(y + animatedHeight / 2f), 0);

        if (!dynamicRects) {
            RenderUtil.CustomStyled.draw(matrix, x, y, animatedWidth, animatedHeight, Round.of(containerRadius), blurEnabled, blurStrength);
        }

        float currentY = y;

        if (showTitle) {
            if (dynamicRects) {
                float titleX = alignRight ? x + (animatedWidth - titleWidth) : x;
                RenderUtil.CustomStyled.draw(matrix, titleX, currentY, titleWidth, titleBoxHeight, Round.of(containerRadius), blurEnabled, blurStrength);
                ScissorUtil.enableScissor(titleX, currentY, titleWidth, titleBoxHeight);
                RenderUtil.Rounded.smooth(matrix, titleX, currentY, titleWidth, titleBoxHeight, ColorUtil.replAlpha(new Color(0xFF000000, true).getRGB(), titleDarkness), Round.of(containerRadius));
                ScissorUtil.disableScissor();
                renderTitleContent(matrix, titleX, currentY, titleWidth, titleBoxHeight, name, fontSize, titleIconSize, finalScale, currentArmor.size(), titleIconTextGap, true);
            } else {
                ScissorUtil.enableScissor(x, currentY, animatedWidth, titleBoxHeight);
                RenderUtil.Rounded.smooth(matrix, x, currentY, animatedWidth, animatedHeight, ColorUtil.replAlpha(new Color(0xFF000000, true).getRGB(), titleDarkness), Round.of(containerRadius, containerRadius, containerRadius, containerRadius));
                ScissorUtil.disableScissor();
                renderTitleContent(matrix, x, currentY, animatedWidth, titleBoxHeight, name, fontSize, titleIconSize, finalScale, currentArmor.size(), titleIconTextGap, false);
            }
        }

        currentY += titleBoxHeight;
        if (dynamicRects) {
            currentY += 3f * finalScale;
        }

        int id = 0;
        for (ItemStack armor : currentArmor) {
            if (dynamicRects) {
                float itemWidth = padding + iconSize + padding + durabilityBarWidth + padding + durabilityTextWidth + padding;
                float itemX = alignRight ? x + (animatedWidth - itemWidth) : x;
                renderArmorItemDynamic(matrix, itemX, currentY, itemWidth, itemRowHeight, armor, fontSize, iconSize, id, finalScale, containerRadius, alignRight);
                currentY += itemRowHeight + 3f * finalScale;
            } else {
                renderArmorItem(matrix, x, currentY, animatedWidth, itemRowHeight, armor, fontSize, iconSize, id, finalScale);
                currentY += itemRowHeight;
            }
            id++;
        }

        drag.size.x = animatedWidth;
        drag.size.y = animatedHeight;
        drag.size.set(animatedWidth, animatedHeight);
        matrix.pop();
    }

    private void renderTitleContent(MatrixStack matrix, float x, float y, float width, float height, String title, float fontSize, float iconSize, float finalScale, int count, float titleIconTextGap, boolean isDynamic) {
        float padding = 6f * finalScale;
        float offsetY = 0.25f * finalScale;

        int hud = Theme.getInstance().clientColor();

        if (isDynamic) {
            float currentX = x + padding;

            if (showTitleIcon) {
                float iconY = y + (height - iconSize) / 2f - offsetY;
                Fonts.iconf.draw(matrix, "d", currentX, iconY, hud, iconSize);
                currentX += iconSize + 6f * finalScale;
            }

            float textY = y + (height - fontSize) / 2f - offsetY;
            font.draw(matrix, title, currentX, textY, -1, fontSize);
            currentX += font.getWidth(title, fontSize);

            if (showCount) {
                currentX += titleIconTextGap;
                float countFontSize = 6f * finalScale;
                String countText = String.valueOf(count);
                float countWidth = font.getWidth(countText, countFontSize);
                float countHeight = countFontSize;
                float countRectX = currentX;
                float countRectY = y + (height - countHeight) / 2f;
                float countRectWidth = countWidth + 8f * finalScale;
                float countRectHeight = countHeight + 4f * finalScale;
                float countRectRadius = 3f * finalScale;
                RenderUtil.CustomStyled.draw(matrix, countRectX - 0.05f, countRectY - 0.75f, countRectWidth - 1, countRectHeight - 1, Round.of(countRectRadius), blurEnabled, blurStrength * 0.6f);

                font.draw(matrix, countText, countRectX + 3.25f * finalScale, countRectY - 0.45f + finalScale, hud, countFontSize);
            }
        } else {
            float offsetX = finalScale;
            float currentX = x + padding - offsetX;

            if (showTitleIcon) {
                float iconY = y + (height - iconSize) / 2f - offsetY;
                Fonts.iconf.draw(matrix, "d", currentX, iconY, hud, iconSize);
                currentX += iconSize + 6f * finalScale;
            }

            float textY = y + (height - fontSize) / 2f - offsetY;
            font.draw(matrix, title, currentX + 2f * finalScale, textY, -1, fontSize);

            if (showCount) {
                float countFontSize = 6f * finalScale;
                String countText = String.valueOf(count);
                float countWidth = font.getWidth(countText, countFontSize);
                float countHeight = countFontSize;
                float countRectX = x + width - padding - countWidth - 4f * finalScale;
                float countRectY = y + (height - countHeight) / 2f;
                float countRectWidth = countWidth + 8f * finalScale;
                float countRectHeight = countHeight + 4f * finalScale;
                float countRectRadius = 3f * finalScale;
                RenderUtil.CustomStyled.draw(matrix, countRectX - 2.5f, countRectY - 1.5f, countRectWidth, countRectHeight, Round.of(countRectRadius), blurEnabled, blurStrength * 0.6f);

                font.draw(matrix, countText, countRectX + 1.5f * finalScale, countRectY - 1 + finalScale, hud, countFontSize);
            }
        }
    }

    private void renderArmorItemDynamic(MatrixStack matrix, float x, float y, float width, float height, ItemStack armor, float fontSize, float iconSize, int id, float finalScale, float radius, boolean alignRight) {
        float padding = 6f * finalScale;
        float durability = (float) (armor.getMaxDamage() - armor.getDamage()) / armor.getMaxDamage();
        percent[id] = Mathf.fast(percent[id], durability * 100f, 10);
        String durabilityText = String.format("%d%%", (int) percent[id]);

        RenderUtil.CustomStyled.draw(matrix, x, y, width, height, Round.of(radius), blurEnabled, blurStrength);

        matrix.push();
        matrix.translate(x, y + height / 2f, 0);
        matrix.scale(1f, 1f, 1f);
        matrix.translate(-x, -(y + height / 2f), 0);

        float armorX = x + padding;
        float armorY = y + (height - iconSize) / 2f;

        GlStateManager.pushMatrix();
        GlStateManager.translatef(armorX + iconSize / 2f, armorY + iconSize / 2f, 0);
        float itemScale = iconSize / 16f;
        GlStateManager.scalef(itemScale, itemScale, 1);
        mc.getItemRenderer().renderItemIntoGUI(armor, -8, -8);
        GlStateManager.popMatrix();

        float separatorX = armorX + iconSize + 6f * finalScale;
        float separatorY = y + (height - iconSize + 2.5f * finalScale) / 2f;
        float separatorHeight = iconSize - 2.5f * finalScale;
        RenderUtil.Rounded.roundedRect(matrix, separatorX - 3.5f, separatorY, 0.25f * finalScale, separatorHeight,
                ColorUtil.getColor(255, 255, 255, 13), Round.of(0.25f * finalScale));

        float durabilityTextWidth = font.getWidth(durabilityText, fontSize);
        float durabilityX = x + width - durabilityTextWidth - padding;
        float durabilityY = y + (height - fontSize) / 2f;
        font.draw(matrix, durabilityText, durabilityX, durabilityY, -1, fontSize);

        float barX = armorX + iconSize + padding;
        float barY = y + (height - 2 * finalScale) / 2f;
        float availableBarWidth = durabilityX - barX - padding;

        RenderUtil.Rounded.roundedRect(matrix, barX, barY, availableBarWidth, 2 * finalScale, ColorUtil.getColor(10, 10, 10, 80), Round.of(1 * finalScale));
        int barColor = ColorUtil.getHealthColor(percent[id], 100f);
        RenderUtil.Rounded.roundedRect(matrix, barX, barY, (percent[id] / 100f) * availableBarWidth, 2 * finalScale, barColor, Round.of(3 * finalScale));

        matrix.pop();
    }

    private void renderArmorItem(MatrixStack matrix, float x, float y, float width, float height, ItemStack armor, float fontSize, float iconSize, int id, float finalScale) {
        float padding = 6f * finalScale;
        float durability = (float) (armor.getMaxDamage() - armor.getDamage()) / armor.getMaxDamage();
        percent[id] = Mathf.fast(percent[id], durability * 100f, 10);
        String durabilityText = String.format("%d%%", (int) percent[id]);

        float armorX = x + padding;
        float armorY = y + (height - iconSize) / 2f;

        GlStateManager.pushMatrix();
        GlStateManager.translatef(armorX + iconSize / 2f, armorY + iconSize / 2f, 0);
        float itemScale = iconSize / 16f;
        GlStateManager.scalef(itemScale, itemScale, 1);
        mc.getItemRenderer().renderItemIntoGUI(armor, -8, -8);
        GlStateManager.popMatrix();

        float separatorX = armorX + iconSize + 6f * finalScale;
        float separatorY = y + (height - iconSize + 2.5f * finalScale) / 2f;
        float separatorHeight = iconSize - 2.5f * finalScale;
        RenderUtil.Rounded.roundedRect(matrix, separatorX - 3.5f, separatorY, 0.25f * finalScale, separatorHeight,
                ColorUtil.getColor(255, 255, 255, 13), Round.of(0.25f * finalScale));

        float durabilityTextWidth = font.getWidth(durabilityText, fontSize);
        float durabilityX = x + width - durabilityTextWidth - padding;
        float durabilityY = y + (height - fontSize) / 2f;
        font.draw(matrix, durabilityText, durabilityX, durabilityY, -1, fontSize);

        float barX = armorX + iconSize + padding;
        float barY = y + (height - 2 * finalScale) / 2f;
        float availableBarWidth = durabilityX - barX - padding;

        RenderUtil.Rounded.roundedRect(matrix, barX, barY, availableBarWidth, 2 * finalScale, ColorUtil.getColor(10, 10, 10, 80), Round.of(1 * finalScale));
        int barColor = ColorUtil.getHealthColor(percent[id], 100f);
        RenderUtil.Rounded.roundedRect(matrix, barX, barY, (percent[id] / 100f) * availableBarWidth, 2 * finalScale, barColor, Round.of(3 * finalScale));
    }
}