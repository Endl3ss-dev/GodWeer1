package org.privates.client.screen.hud.impl;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.CooldownTracker;
import net.minecraft.util.text.TextFormatting;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.mojang.blaze3d.platform.GlStateManager;
import org.privates.client.Excellent;
import org.privates.client.managers.component.impl.drag.DragComponent;
import org.privates.client.managers.events.render.Render2DEvent;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.impl.misc.FunTimeAssist;
import org.privates.client.managers.module.settings.impl.DragSetting;
import org.privates.client.screen.hud.AbstractHud;
import org.privates.client.screen.hud.IConfigurableHud;
import org.privates.client.screen.hud.IRenderer;
import org.privates.client.utils.keyboard.Keyboard;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RectUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.font.Fonts;

import java.awt.*;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ItemBindRenderer extends AbstractHud implements IRenderer, IConfigurableHud {

    private static final float ITEM_SPACING = 4.0f;
    private final DragSetting drag;
    private final Map<Item, ItemBindInfo> boundItems = new LinkedHashMap<>();
    private final Map<Item, Float> itemAnimations = new LinkedHashMap<>();

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
    public boolean showCount = true;

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

    private static class ItemBindInfo {
        final int count;
        final String keybind;
        final String nameFilter;
        final Integer tintRGB;
        float alphaAnimation = 0.0f;

        ItemBindInfo(int count, String keybind, String nameFilter, Integer tintRGB) {
            this.count = count;
            this.keybind = keybind;
            this.nameFilter = nameFilter;
            this.tintRGB = tintRGB;
        }
    }

    public ItemBindRenderer(DragSetting drag) {
        this.drag = drag;
    }

    @Getter
    @Setter
    private boolean enableOutline = false;
    @Getter
    @Setter
    private float outlineWidth = 1.0f;

    @Override
    public List<DragComponent.HudSetting> getHudSettings() {
        return Arrays.asList(
                new DragComponent.HudSetting("Scale", DragComponent.SettingType.SLIDER,
                        () -> this.scale,
                        v -> this.scale = v,
                        0.5f, 2.0f, 0.1f),

                new DragComponent.HudSetting("Show Count", DragComponent.SettingType.BOOLEAN,
                        () -> this.showCount ? 1f : 0f,
                        v -> this.showCount = v > 0.5f),

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

        if (width >= 3840 && height >= 2160) {
            return 1.5f;
        } else if (width >= 2560 && height >= 1440) {
            return 1.0f;
        } else if (width >= 1920 || height >= 1080) {
            return 1f;
        } else {
            return 1f;
        }
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
        float centerY = y + height / 2f;
        float pivotX = centerX;
        float pivotY = y + height * 0.15f;

        matrix.translate(centerX, centerY, 0);
        matrix.scale(dragScaleEffect, dragScaleEffect, 1);
        matrix.translate(-centerX, -centerY, 0);

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

    private int getPlayerItemCount(Item item, String nameFilter) {
        if (mc.player == null) return 0;
        return mc.player.inventory.mainInventory.stream()
                .filter(stack -> {
                    if (stack.isEmpty()) return false;
                    if (stack.getItem() != item) return false;
                    if (nameFilter != null) {
                        String name = TextFormatting.getTextWithoutFormattingCodes(stack.getDisplayName().getString());
                        return name != null && name.toLowerCase().contains(nameFilter.toLowerCase());
                    }
                    return true;
                })
                .mapToInt(ItemStack::getCount)
                .sum();
    }

    private void updateItems() {
        Map<Item, ItemBindInfo> newBoundItems = new LinkedHashMap<>();
        if (mc.player == null) return;

        FunTimeAssist funTimeAssist = Excellent.inst().moduleManager().get(FunTimeAssist.class);
        if (funTimeAssist != null && funTimeAssist.isEnabled()) {
            Map<Item, FunTimeAssist.ItemBindData> keyBindings = funTimeAssist.getKeyBindings();
            if (keyBindings != null) {
                for (Map.Entry<Item, FunTimeAssist.ItemBindData> entry : keyBindings.entrySet()) {
                    FunTimeAssist.ItemBindData bindData = entry.getValue();
                    if (bindData.bind().getValue() != -1) {
                        int count = getPlayerItemCount(entry.getKey(), bindData.nameFilter());
                        String keyName = Keyboard.keyName(bindData.bind().getValue());
                        newBoundItems.put(entry.getKey(), new ItemBindInfo(count, keyName, bindData.nameFilter(), bindData.tintRGB()));
                    }
                }
            }
        }

        for (Item item : newBoundItems.keySet()) {
            if (!itemAnimations.containsKey(item)) {
                itemAnimations.put(item, 0f);
            }
        }

        itemAnimations.keySet().removeIf(item -> !newBoundItems.containsKey(item));

        boundItems.clear();
        boundItems.putAll(newBoundItems);
    }

    private void addPreviewItems() {
        boundItems.clear();
        itemAnimations.clear();
        boundItems.put(Items.ENDER_EYE, new ItemBindInfo(5, "G", null, null));
        boundItems.put(Items.SUGAR, new ItemBindInfo(32, "B", null, null));
        boundItems.put(Items.NETHERITE_SCRAP, new ItemBindInfo(0, "V", null, null));
        boundItems.put(Items.DRIED_KELP, new ItemBindInfo(10, "C", null, null));
        boundItems.put(Items.PHANTOM_MEMBRANE, new ItemBindInfo(3, "H", null, null));
        boundItems.put(Items.SNOWBALL, new ItemBindInfo(16, "N", null, null));

        for (Item item : boundItems.keySet()) {
            itemAnimations.put(item, 1f);
        }
    }

    public void render(Render2DEvent event, boolean glow) {
        boolean isPreview = mc.currentScreen instanceof ChatScreen;
        if (isPreview) {
            addPreviewItems();
        } else {
            updateItems();
        }

        MatrixStack matrix = event.getMatrix();
        float x = drag.position.x;
        float y = drag.position.y;

        CooldownTracker cooldownTracker = mc.player != null ? mc.player.getCooldownTracker() : null;
        float partialTicks = mc.getRenderPartialTicks();

        float offsetX = 0f;
        float maxHeight = 16f * scale;

        if (boundItems.isEmpty() && itemAnimations.isEmpty()) {
            return;
        }

        if (x == 0 && y == 0) {
            float screenWidth = mc.getMainWindow().getScaledWidth();
            float screenHeight = mc.getMainWindow().getScaledHeight();
            float newX = screenWidth / 2f - 100;
            float newY = screenHeight - 50;
            drag.position.set(newX, newY);
            x = newX;
            y = newY;
            lastX = x;
            lastY = y;
        }

        for (Map.Entry<Item, ItemBindInfo> entry : boundItems.entrySet()) {
            Item item = entry.getKey();
            ItemBindInfo info = entry.getValue();

            float targetAnim = boundItems.containsKey(item) ? 1f : 0f;
            float currentAnim = itemAnimations.getOrDefault(item, 0f);
            currentAnim = Mathf.fast(currentAnim, targetAnim, 14);
            itemAnimations.put(item, currentAnim);

            if (currentAnim < 0.01f) continue;

            float elementWidth = (Fonts.SF_BOLD.getWidth(prettyKey(info.keybind), 6.5F * scale) + 26) * scale;
            offsetX += elementWidth + (ITEM_SPACING + 2) * scale;
        }

        if (offsetX > 0) {
            offsetX -= (ITEM_SPACING + 2) * scale;
        }
        drag.size.set(offsetX, maxHeight);

        updateDragAnimation(x, y, offsetX, maxHeight);

        matrix.push();
        applyDragTransform(matrix, x, y, offsetX, maxHeight);

        offsetX = 0f;
        for (Map.Entry<Item, ItemBindInfo> entry : boundItems.entrySet()) {
            Item item = entry.getKey();
            ItemBindInfo info = entry.getValue();

            float currentAnim = itemAnimations.get(item);
            if (currentAnim < 0.01f) continue;

            float smoothAnim = easeOutCubic(currentAnim);
            float scaleAnim = 0.3f + (smoothAnim * 0.7f);

            matrix.push();

            int textColor = ColorUtil.WHITE;
            String prettyKeyName = prettyKey(info.keybind);
            float keyWidth = Fonts.SF_BOLD.getWidth(prettyKeyName, 6.5F * scale);

            float currentX = x + offsetX;
            float currentY = y;

            float elementWidth = (keyWidth + 26) * scale;

            matrix.translate(currentX + elementWidth / 2f, currentY + 8 * scale, 0);
            matrix.scale(scaleAnim, scaleAnim, 1f);
            matrix.translate(-(currentX + elementWidth / 2f), -(currentY + 8 * scale), 0);

            int bgAlpha = (int) (255 * currentAnim);
            RenderUtil.CustomStyled.draw(matrix, currentX, currentY - 1 * scale, elementWidth, 16 * scale, Round.of(4 * scale), blurEnabled, blurStrength);

            if (enableOutline) {
                int themeColor = Theme.getInstance().clientColor();
                int outlineColor = ColorUtil.interpolateColor(ColorUtil.getColor(255, 255, 255, 255), themeColor, 0.5f);
                float halfWidth = outlineWidth * scale / 2f;
                RenderUtil.Rounded.roundedOutline(matrix,
                        currentX - halfWidth,
                        (currentY - 1 * scale) - halfWidth,
                        elementWidth + outlineWidth * scale,
                        16 * scale + outlineWidth * scale,
                        outlineWidth * scale,
                        outlineColor,
                        Round.of(4 * scale));
            }

            drawItemIconMinimal(matrix, item, info, currentX, currentY - 1 * scale, cooldownTracker, partialTicks, currentAnim);

            RenderUtil.Rounded.smooth(matrix, currentX, currentY - 1 * scale, 16 * scale, 16 * scale, ColorUtil.replAlpha(new Color(0xFF000000, true).getRGB(), (int) (0.15F * 255 * currentAnim)), Round.of(4 * scale));
            RectUtil.drawRect(matrix, currentX + 16.5F * scale, currentY - 1 * scale, 0.8F * scale, 16 * scale, ColorUtil.getColor(200, (int) (3 * currentAnim)));

            int textAlpha = (int) (255 * currentAnim);
            Fonts.SF_BOLD.draw(matrix, prettyKeyName, currentX + 21 * scale, currentY + 3.6F * scale, ColorUtil.replAlpha(textColor, textAlpha), 6.5F * scale);

            matrix.pop();

            offsetX += elementWidth + (ITEM_SPACING + 2) * scale;
        }

        matrix.pop();
    }

    private void drawItemIconMinimal(MatrixStack matrix, Item item, ItemBindInfo info, float x, float y, CooldownTracker cooldownTracker, float partialTicks, float animProgress) {
        ItemStack stack = new ItemStack(item);

        float alpha = animProgress;

        if (cooldownTracker != null) {
            float cd = cooldownTracker.getCooldown(item, partialTicks);
            if (cd > 0f) {
                alpha *= (1.0f - cd * 0.5f);
            }
        }

        boolean noItems = info.count == 0;
        if (noItems) {
            alpha *= 0.5f;
        }

        float boxWidth = 16f * scale;
        float boxHeight = 16f * scale;
        float itemSize = 12f * scale;

        float itemX = x + (boxWidth - itemSize) / 2f;
        float itemY = y + (boxHeight - itemSize) / 2f;

        if (noItems) {
            int redAlpha = (int) (100 * animProgress);
            RenderUtil.Rounded.smooth(matrix, x, y, boxWidth, boxHeight, ColorUtil.getColor(255, 0, 0, redAlpha), Round.of(4 * scale));
        }

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, alpha);
        GlStateManager.translatef(itemX + itemSize / 2f, itemY + itemSize / 2f, 0);
        GlStateManager.scalef((itemSize / 16f), (itemSize / 16f), 1f);
        mc.getItemRenderer().renderItemIntoGUI(stack, -8, -8);
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();

        if (showCount) {
            matrix.push();
            matrix.translate(0, 0, 200);
            if (info.count > 0) {
                String countStr = String.valueOf(info.count);
                float countX = x + boxWidth - Fonts.SF_BOLD.getWidth(countStr, 5.5F * scale) - 1 * scale;
                float countY = y + boxHeight - 6 * scale;
                int countAlpha = (int) (255 * animProgress);
                Fonts.SF_BOLD.draw(matrix, countStr, countX, countY, ColorUtil.replAlpha(ColorUtil.WHITE, countAlpha), 5.5F * scale);
            }
            matrix.pop();
        }
    }

    private float easeOutCubic(float t) {
        return 1 - (float) Math.pow(1 - t, 3);
    }

    private String prettyKey(String raw) {
        if (raw == null || raw.isEmpty()) return "?";
        if (raw.length() == 1) return raw;
        return switch (raw.toUpperCase()) {
            case "SPACE" -> "SPC";
            case "LSHIFT", "RSHIFT" -> "SHIFT";
            case "LCTRL", "RCTRL" -> "CTRL";
            case "LALT", "RALT" -> "ALT";
            default -> raw.length() > 5 ? raw.substring(0, 5) : raw;
        };
    }
}