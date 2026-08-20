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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class ArmorSmartRenderer extends AbstractHud implements IRenderer, IConfigurableHud {
    private final DragSetting drag;
    private final List<ItemStack> currentArmor = new ArrayList<>();
    private final List<ItemStack> previewArmor = new ArrayList<>();
    private final float[] percent = new float[]{0, 0, 0, 0};

    private float animatedWidth;
    private float animatedHeight;
    private long lastPreviewUpdate = 0;
    private long lastArmorChange = 0;
    private int previewIndex = 0;
    private final Random random = new Random();

    @Getter
    @Setter
    private float scaleV;
    @Getter
    @Setter
    private boolean blurEnabled = true;
    @Getter
    @Setter
    private float blurStrength = 10f;

    @Getter
    @Setter
    private boolean enableOutline = false;
    @Getter
    @Setter
    private float outlineWidth = 1.0f;

    public float scale = 0.9f;

    private float lastX = 0;
    private float lastY = 0;
    private float smoothVelocityX = 0;
    private float smoothVelocityY = 0;
    private float currentRotation = 0;
    private float targetRotation = 0;
    private float springOffsetX = 0;
    private float springOffsetY = 0;
    private float dragScaleEffect = 1.0f;
    private boolean isDragging = false;
    private long lastMoveTime = 0;

    public ArmorSmartRenderer(DragSetting drag) {
        this.drag = drag;
        if (drag.position.x == 0 && drag.position.y == 0) {
            float screenWidth = mc.getMainWindow().getScaledWidth();
            float screenHeight = mc.getMainWindow().getScaledHeight();
            drag.position.set(screenWidth / 2f + 10, screenHeight - 60);
        }
    }

    @Override
    public List<DragComponent.HudSetting> getHudSettings() {
        return List.of(
                new DragComponent.HudSetting("Scale", DragComponent.SettingType.SLIDER,
                        () -> this.scale,
                        v -> this.scale = v,
                        0.5f, 2.0f, 0.1f),
                new DragComponent.HudSetting("Enable Outline", DragComponent.SettingType.BOOLEAN,
                        () -> this.enableOutline ? 1f : 0f,
                        v -> this.enableOutline = v > 0.5f),
                new DragComponent.HudSetting("Outline Width", DragComponent.SettingType.SLIDER,
                        () -> this.outlineWidth,
                        v -> this.outlineWidth = v,
                        0.5f, 3.0f, 0.1f)
        );
    }

    private float getResolutionScale() {
        int width = mc.getMainWindow().getWidth();
        int height = mc.getMainWindow().getHeight();
        if (width >= 3840 && height >= 2160) return 1f;
        else if (width >= 2560 && height >= 1440) return 1.0f;
        else if (width >= 1920 && height >= 1080) return 1f;
        else return 1f;
    }

    private float smoothLerp(float current, float target, float speed) {
        float diff = target - current;
        float factor = 1.0f - (float) Math.pow(1.0f - speed, 1.5f);
        return current + diff * factor;
    }

    private float elasticLerp(float current, float target, float speed, float elasticity) {
        float diff = target - current;
        float velocity = diff * speed;
        if (Math.abs(diff) < 0.5f) {
            velocity += diff * elasticity * 0.3f;
        }
        return current + velocity;
    }

    private void updateDragAnimation(float x, float y, float width, float height) {
        float instantVelocityX = x - lastX;
        float instantVelocityY = y - lastY;

        boolean isMoving = Math.abs(instantVelocityX) > 0.01f || Math.abs(instantVelocityY) > 0.01f;

        if (isMoving) {
            lastMoveTime = System.currentTimeMillis();
            isDragging = true;
        } else if (System.currentTimeMillis() - lastMoveTime > 50) {
            isDragging = false;
        }

        float velocitySmoothing = isDragging ? 0.25f : 0.08f;
        smoothVelocityX = smoothLerp(smoothVelocityX, instantVelocityX, velocitySmoothing);
        smoothVelocityY = smoothLerp(smoothVelocityY, instantVelocityY, velocitySmoothing);

        float tiltSensitivity = 1.8f;
        float maxTilt = 15.0f;

        float horizontalInfluence = smoothVelocityX * tiltSensitivity;
        float verticalInfluence = smoothVelocityY * 0.3f;

        targetRotation = horizontalInfluence - verticalInfluence;
        targetRotation = Math.max(-maxTilt, Math.min(maxTilt, targetRotation));

        float rotationSpeed = isDragging ? 0.18f : 0.06f;
        currentRotation = elasticLerp(currentRotation, targetRotation, rotationSpeed, 0.15f);

        if (!isDragging && Math.abs(currentRotation) < 0.1f) {
            currentRotation *= 0.9f;
        }

        float springStrength = 0.12f;
        float springDamping = 0.85f;

        float targetSpringX = -smoothVelocityX * 0.3f;
        float targetSpringY = -smoothVelocityY * 0.2f;

        springOffsetX = springOffsetX * springDamping + (targetSpringX - springOffsetX) * springStrength;
        springOffsetY = springOffsetY * springDamping + (targetSpringY - springOffsetY) * springStrength;

        float maxSpring = 5.0f;
        springOffsetX = Math.max(-maxSpring, Math.min(maxSpring, springOffsetX));
        springOffsetY = Math.max(-maxSpring, Math.min(maxSpring, springOffsetY));

        float targetDragScale = isDragging ? 1.02f : 1.0f;
        dragScaleEffect = smoothLerp(dragScaleEffect, targetDragScale, 0.15f);

        lastX = x;
        lastY = y;
    }

    private void applyDragTransform(MatrixStack matrix, float x, float y, float width, float height) {
        float centerX = x + width / 2f;
        float pivotX = centerX;
        float pivotY = y + height * 0.15f;

        matrix.translate(springOffsetX, springOffsetY, 0);

        if (Math.abs(currentRotation) > 0.01f) {
            matrix.translate(pivotX, pivotY, 0);
            matrix.rotate(net.minecraft.util.math.vector.Vector3f.ZP.rotationDegrees(currentRotation));
            matrix.translate(-pivotX, -pivotY, 0);
        }
    }

    @Override
    public void render(Render2DEvent event) {
        render(event, false);
    }

    private void addPreviewArmor() {
        if (System.currentTimeMillis() - lastPreviewUpdate > 1000) {
            previewIndex++;
            if (previewIndex > 3) previewIndex = 0;
            lastPreviewUpdate = System.currentTimeMillis();
        }

        if (System.currentTimeMillis() - lastArmorChange > 500 || previewArmor.isEmpty()) {
            previewArmor.clear();
            previewArmor.add(getRandomHelmet());
            previewArmor.add(getRandomChestplate());
            previewArmor.add(getRandomLeggings());
            previewArmor.add(getRandomBoots());

            for (ItemStack item : previewArmor) {
                if (item.isDamageable()) {
                    item.setDamage(random.nextInt(item.getMaxDamage()));
                }
            }
            lastArmorChange = System.currentTimeMillis();
        }

        currentArmor.clear();
        for (int i = 0; i <= previewIndex && i < previewArmor.size(); i++) {
            currentArmor.add(previewArmor.get(i));
        }
    }

    private ItemStack getRandomHelmet() {
        ItemStack[] helmets = {
                new ItemStack(Items.LEATHER_HELMET),
                new ItemStack(Items.CHAINMAIL_HELMET),
                new ItemStack(Items.IRON_HELMET),
                new ItemStack(Items.GOLDEN_HELMET),
                new ItemStack(Items.DIAMOND_HELMET),
                new ItemStack(Items.NETHERITE_HELMET)
        };
        return helmets[random.nextInt(helmets.length)];
    }

    private ItemStack getRandomChestplate() {
        ItemStack[] chestplates = {
                new ItemStack(Items.LEATHER_CHESTPLATE),
                new ItemStack(Items.CHAINMAIL_CHESTPLATE),
                new ItemStack(Items.IRON_CHESTPLATE),
                new ItemStack(Items.GOLDEN_CHESTPLATE),
                new ItemStack(Items.DIAMOND_CHESTPLATE),
                new ItemStack(Items.NETHERITE_CHESTPLATE)
        };
        return chestplates[random.nextInt(chestplates.length)];
    }

    private ItemStack getRandomLeggings() {
        ItemStack[] leggings = {
                new ItemStack(Items.LEATHER_LEGGINGS),
                new ItemStack(Items.CHAINMAIL_LEGGINGS),
                new ItemStack(Items.IRON_LEGGINGS),
                new ItemStack(Items.GOLDEN_LEGGINGS),
                new ItemStack(Items.DIAMOND_LEGGINGS),
                new ItemStack(Items.NETHERITE_LEGGINGS)
        };
        return leggings[random.nextInt(leggings.length)];
    }

    private ItemStack getRandomBoots() {
        ItemStack[] boots = {
                new ItemStack(Items.LEATHER_BOOTS),
                new ItemStack(Items.CHAINMAIL_BOOTS),
                new ItemStack(Items.IRON_BOOTS),
                new ItemStack(Items.GOLDEN_BOOTS),
                new ItemStack(Items.DIAMOND_BOOTS),
                new ItemStack(Items.NETHERITE_BOOTS)
        };
        return boots[random.nextInt(boots.length)];
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

    private boolean isVertical() {
        float screenWidth = mc.getMainWindow().getScaledWidth();
        float screenHeight = mc.getMainWindow().getScaledHeight();
        float x = drag.position.x;
        float y = drag.position.y;

        float leftDistance = x;
        float rightDistance = screenWidth - (x + animatedWidth);
        float topDistance = y;
        float bottomDistance = screenHeight - (y + animatedHeight);

        float horizontalMin = Math.min(leftDistance, rightDistance);
        float verticalMin = Math.min(topDistance, bottomDistance);

        return horizontalMin < verticalMin;
    }

    private boolean isDamaged(ItemStack stack) {
        return stack.isDamageable() && stack.getDamage() > 0;
    }

    private boolean hasAnyDamagedArmor() {
        for (ItemStack armor : currentArmor) {
            if (isDamaged(armor)) {
                return true;
            }
        }
        return false;
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

        float padding = 5f * finalScale;
        float iconSize = 16f * finalScale;
        float containerRadius = 6f * finalScale;
        float itemSpacing = 2f * finalScale;
        float durabilityBarHeight = 2f * finalScale;
        float durabilityBarWidth = 40f * finalScale;
        float textSpacing = 2f * finalScale;
        float textWidth = font.getWidth("999", 8f * finalScale);

        boolean vertical = isVertical();
        boolean showDurability = hasAnyDamagedArmor();
        float targetWidth, targetHeight;
        int armorCount = currentArmor.size();

        if (armorCount == 0) {
            targetWidth = 0;
            targetHeight = 0;
        } else if (vertical) {
            if (showDurability) {
                targetWidth = padding * 2 + iconSize + itemSpacing + durabilityBarWidth + textSpacing + textWidth;
            } else {
                targetWidth = padding * 2 + iconSize;
            }
            targetHeight = padding * 2 + armorCount * iconSize + (armorCount - 1) * itemSpacing;
        } else {
            targetWidth = padding * 2 + armorCount * iconSize + (armorCount - 1) * itemSpacing;
            if (showDurability) {
                targetHeight = padding * 2 + iconSize + itemSpacing + durabilityBarHeight;
            } else {
                targetHeight = padding * 2 + iconSize;
            }
        }

        animatedWidth = Mathf.fast(animatedWidth, targetWidth, 14);
        animatedHeight = Mathf.fast(animatedHeight, targetHeight, 14);

        if (x == 0 && y == 0) {
            float screenWidth = mc.getMainWindow().getScaledWidth();
            float screenHeight = mc.getMainWindow().getScaledHeight();
            float newX = screenWidth / 2f + 10 * finalScale;
            float newY = screenHeight - (animatedHeight > 0 ? animatedHeight : 40 * finalScale) - 25 * finalScale;
            drag.position.set(newX, newY);
            x = newX;
            y = newY;
            lastX = x;
            lastY = y;
        }

        updateDragAnimation(x, y, animatedWidth, animatedHeight);

        boolean hasContent = !currentArmor.isEmpty();
        scaleV = Mathf.fast(scaleV, hasContent ? 1 : 0, 16);

        if (Mathf.round(scaleV, .1d) <= 0) return;

        matrix.push();
        matrix.translate((x + animatedWidth / 2F), (y + animatedHeight / 2f), 0);
        matrix.scale(scaleV * dragScaleEffect, scaleV * dragScaleEffect, scaleV);
        matrix.translate(-(x + animatedWidth / 2F), -(y + animatedHeight / 2f), 0);

        applyDragTransform(matrix, x, y, animatedWidth, animatedHeight);

        RenderUtil.CustomStyled.draw(matrix, x, y, animatedWidth, animatedHeight, Round.of(containerRadius), blurEnabled, blurStrength);
        if (enableOutline) {
            int themeColor = Theme.getInstance().clientColor();
            int outlineColor = ColorUtil.interpolateColor(ColorUtil.getColor(255, 255, 255, 255), themeColor, 0.5f);
            float halfWidth = outlineWidth * finalScale / 2f;
            RenderUtil.Rounded.roundedOutline(matrix,
                    x - halfWidth,
                    y - halfWidth,
                    animatedWidth + outlineWidth * finalScale,
                    animatedHeight + outlineWidth * finalScale,
                    outlineWidth * finalScale,
                    outlineColor,
                    Round.of(containerRadius));
        }
        if (vertical) {
            renderVertical(matrix, x, y, padding, iconSize, durabilityBarHeight, durabilityBarWidth, itemSpacing, textSpacing, textWidth, showDurability, finalScale);
        } else {
            renderHorizontal(matrix, x, y, padding, iconSize, durabilityBarHeight, itemSpacing, showDurability, finalScale);
        }

        drag.size.x = animatedWidth;
        drag.size.y = animatedHeight;
        drag.size.set(animatedWidth, animatedHeight);
        matrix.pop();
    }

    private void renderVertical(MatrixStack matrix, float x, float y, float padding, float iconSize, float durabilityBarHeight, float durabilityBarWidth, float itemSpacing, float textSpacing, float textWidth, boolean showDurability, float finalScale) {
        float currentY = y + padding;

        int id = 0;
        for (ItemStack armor : currentArmor) {
            float itemX = x + padding;

            GlStateManager.pushMatrix();
            GlStateManager.translatef(itemX + iconSize / 2f, currentY + iconSize / 2f, 0);
            float itemScale = iconSize / 16f;
            GlStateManager.scalef(itemScale, itemScale, 1);
            mc.getItemRenderer().renderItemIntoGUI(armor, -8, -8);
            GlStateManager.popMatrix();

            if (showDurability && isDamaged(armor)) {
                float durability = (float) (armor.getMaxDamage() - armor.getDamage()) / armor.getMaxDamage();
                percent[id] = Mathf.fast(percent[id], durability * 100f, 14);

                float barX = itemX + iconSize + itemSpacing;
                float barY = currentY + (iconSize - durabilityBarHeight) / 2f;
                renderDurabilityBar(matrix, barX, barY, durabilityBarWidth, durabilityBarHeight, percent[id], finalScale);

                String durabilityText = String.valueOf(armor.getMaxDamage() - armor.getDamage());
                float textX = barX + durabilityBarWidth + textSpacing;
                float textY = currentY + (iconSize - font.getHeight(durabilityText, 8f * finalScale)) / 2f;
                font.draw(matrix, durabilityText, textX, textY, ColorUtil.WHITE, 8f * finalScale);
            }

            currentY += iconSize + itemSpacing;
            id++;
        }
    }

    private void renderHorizontal(MatrixStack matrix, float x, float y, float padding, float iconSize, float durabilityBarHeight, float itemSpacing, boolean showDurability, float finalScale) {
        float currentX = x + padding;
        float itemY = y + padding;

        int id = 0;
        for (ItemStack armor : currentArmor) {
            GlStateManager.pushMatrix();
            GlStateManager.translatef(currentX + iconSize / 2f, itemY + iconSize / 2f, 0);
            float itemScale = iconSize / 16f;
            GlStateManager.scalef(itemScale, itemScale, 1);
            mc.getItemRenderer().renderItemIntoGUI(armor, -8, -8);
            GlStateManager.popMatrix();

            if (showDurability && isDamaged(armor)) {
                float durability = (float) (armor.getMaxDamage() - armor.getDamage()) / armor.getMaxDamage();
                percent[id] = Mathf.fast(percent[id], durability * 100f, 14);
                float barX = currentX;
                float barY = itemY + iconSize + itemSpacing;
                renderDurabilityBar(matrix, barX, barY, iconSize, durabilityBarHeight, percent[id], finalScale);
            }

            currentX += iconSize + itemSpacing;
            id++;
        }
    }

    private void renderDurabilityBar(MatrixStack matrix, float x, float y, float width, float height, float durability, float finalScale) {
        float barRadius = 2f * finalScale;

        RenderUtil.Rounded.roundedRect(matrix, x, y, width, height, ColorUtil.getColor(10, 10, 10, 80), Round.of(barRadius));

        float smoothedDurability = Mathf.clamp(durability, 0, 100);
        int barColor;

        if (smoothedDurability >= 75) {
            barColor = ColorUtil.getColor(0, 255, 0, 255);
        } else if (smoothedDurability >= 50) {
            barColor = ColorUtil.getColor(255, 255, 0, 255);
        } else if (smoothedDurability >= 25) {
            barColor = ColorUtil.getColor(255, 165, 0, 255);
        } else {
            barColor = ColorUtil.getColor(255, 0, 0, 255);
        }

        float barWidth = (smoothedDurability / 100f) * width;
        if (barWidth > 0) {
            RenderUtil.Rounded.roundedRect(matrix, x, y, barWidth, height, barColor, Round.of(barRadius));
        }
    }
}