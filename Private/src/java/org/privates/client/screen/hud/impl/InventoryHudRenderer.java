package org.privates.client.screen.hud.impl;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.item.ItemStack;
import net.mojang.blaze3d.matrix.MatrixStack;
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
import java.util.Arrays;
import java.util.List;

@Getter
public class InventoryHudRenderer extends AbstractHud implements IRenderer, IConfigurableHud {
    private final DragSetting drag;
    private final Minecraft mc = Minecraft.getInstance();

    @Setter
    @Getter
    private boolean blurEnabled = true;
    @Setter
    @Getter
    private float blurStrength = 10f;

    @Setter
    @Getter
    private float itemScale = 0.3f;

    private float scaleV;

    public float scale = 0.9f;
    public float titleDarkness = 0.0f;
    public boolean showTitleIcon = true;
    public boolean showCount = true;
    public boolean showTitle = true;

    // === Новые переменные для плавной анимации наклона ===
    private float lastX = 0;
    private float lastY = 0;
    private float prevX = 0;
    private float prevY = 0;

    // Сглаженные скорости для плавности
    private float smoothVelocityX = 0;
    private float smoothVelocityY = 0;

    // Текущий и целевой наклон
    private float currentRotation = 0;
    private float targetRotation = 0;

    // Эффект "пружины" - небольшое смещение при движении
    private float springOffsetX = 0;
    private float springOffsetY = 0;

    // Эффект масштабирования при drag
    private float dragScaleEffect = 1.0f;

    // Флаг активного перетаскивания
    private boolean isDragging = false;
    private long lastMoveTime = 0;

    public InventoryHudRenderer(DragSetting drag) {
        this.drag = drag;
    }

    @Setter
    @Getter
    private boolean enableOutline = false;
    @Setter
    @Getter
    private float outlineWidth = 1.0f;

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
            return 1f;
        } else if (width >= 2560 && height >= 1440) {
            return 1.0f;
        } else if (width >= 1920 || height >= 1080) {
            return 1f;
        } else {
            return 1f;
        }
    }

    /**
     * Плавная интерполяция с эффектом easing
     */
    private float smoothLerp(float current, float target, float speed) {
        float diff = target - current;
        // Используем кубическое сглаживание для более естественного движения
        float factor = 1.0f - (float) Math.pow(1.0f - speed, 1.5f);
        return current + diff * factor;
    }

    /**
     * Эластичное сглаживание для эффекта "пружины"
     */
    private float elasticLerp(float current, float target, float speed, float elasticity) {
        float diff = target - current;
        float velocity = diff * speed;
        // Добавляем небольшое "перелетание" для эластичности
        if (Math.abs(diff) < 0.5f) {
            velocity += diff * elasticity * 0.3f;
        }
        return current + velocity;
    }

    @Override
    public void render(Render2DEvent event) {
        float resScale = getResolutionScale();
        float finalScale = resScale * scale;
        MatrixStack matrix = event.getMatrix();
        if (mc.player == null || mc.world == null) return;

        int itemCount = 0;
        for (int i = 9; i < 36; i++) {
            if (!mc.player.inventory.mainInventory.get(i).isEmpty()) {
                itemCount++;
            }
        }

        float x = drag.position.x;
        float y = drag.position.y;

        float itemSpacing = 19.5f * finalScale;
        float padding = 5 * finalScale;
        float titleHeight = showTitle ? 18f * finalScale : 0f;
        float containerRadius = 6f * finalScale;
        float width = padding * 2 + 9 * itemSpacing;
        float height = titleHeight + padding + 3.15f * itemSpacing - 5 * finalScale;

        drag.size.set(width, height);

        if (x == 0 && y == 0) {
            float screenWidth = mc.getMainWindow().getScaledWidth();
            float screenHeight = mc.getMainWindow().getScaledHeight();
            float newX = screenWidth - width - 4 * finalScale;
            float newY = screenHeight - height - 4 * finalScale;
            drag.position.set(newX, newY);
            x = newX;
            y = newY;
            lastX = x;
            lastY = y;
            prevX = x;
            prevY = y;
        }

        // === Вычисляем мгновенную скорость движения ===
        float instantVelocityX = x - lastX;
        float instantVelocityY = y - lastY;

        // Определяем, происходит ли перетаскивание
        boolean isMoving = Math.abs(instantVelocityX) > 0.01f || Math.abs(instantVelocityY) > 0.01f;

        if (isMoving) {
            lastMoveTime = System.currentTimeMillis();
            isDragging = true;
        } else if (System.currentTimeMillis() - lastMoveTime > 50) {
            isDragging = false;
        }

        // === Сглаживаем скорость для плавности анимации ===
        float velocitySmoothing = isDragging ? 0.25f : 0.08f;
        smoothVelocityX = smoothLerp(smoothVelocityX, instantVelocityX, velocitySmoothing);
        smoothVelocityY = smoothLerp(smoothVelocityY, instantVelocityY, velocitySmoothing);

        // === Вычисляем целевой угол наклона ===
        // Наклон основан на горизонтальной скорости
        // Тянем вправо - наклоняется вправо (по часовой)
        // Тянем влево - наклоняется влево (против часовой)
        float tiltSensitivity = 1.8f;
        float maxTilt = 15.0f; // Максимальный угол наклона в градусах

        // Также добавляем небольшое влияние вертикальной скорости
        float horizontalInfluence = smoothVelocityX * tiltSensitivity;
        float verticalInfluence = smoothVelocityY * 0.3f; // Меньшее влияние от вертикального движения

        targetRotation = horizontalInfluence - verticalInfluence;
        targetRotation = Math.max(-maxTilt, Math.min(maxTilt, targetRotation));

        // === Плавно интерполируем текущий угол к целевому ===
        float rotationSpeed = isDragging ? 0.18f : 0.06f; // Быстрее при drag, медленнее при возврате
        currentRotation = elasticLerp(currentRotation, targetRotation, rotationSpeed, 0.15f);

        // Очень мягкое затухание когда близко к нулю
        if (!isDragging && Math.abs(currentRotation) < 0.1f) {
            currentRotation *= 0.9f;
        }

        // === Эффект "пружины" - небольшое отставание элемента от курсора ===
        float springStrength = 0.12f;
        float springDamping = 0.85f;

        float targetSpringX = -smoothVelocityX * 0.3f;
        float targetSpringY = -smoothVelocityY * 0.2f;

        springOffsetX = springOffsetX * springDamping + (targetSpringX - springOffsetX) * springStrength;
        springOffsetY = springOffsetY * springDamping + (targetSpringY - springOffsetY) * springStrength;

        // Ограничиваем смещение пружины
        float maxSpring = 5.0f;
        springOffsetX = Math.max(-maxSpring, Math.min(maxSpring, springOffsetX));
        springOffsetY = Math.max(-maxSpring, Math.min(maxSpring, springOffsetY));

        // === Эффект масштаба при drag (небольшое увеличение при подхвате) ===
        float targetDragScale = isDragging ? 1.02f : 1.0f;
        dragScaleEffect = smoothLerp(dragScaleEffect, targetDragScale, 0.15f);

        // Обновляем предыдущие позиции
        prevX = lastX;
        prevY = lastY;
        lastX = x;
        lastY = y;

        scaleV = Mathf.fast(scaleV, itemCount > 0 || mc.currentScreen instanceof ChatScreen ? 1 : 0, 12);

        if (Mathf.round(scaleV, .1d) <= 0) return;

        matrix.push();

        // === Применяем все трансформации ===

        // Центр элемента для трансформаций
        float centerX = x + width / 2f;
        float centerY = y + height / 2f;

        // Точка "подвеса" - сверху по центру (как будто держим за верхнюю часть)
        float pivotX = centerX;
        float pivotY = y + height * 0.15f; // Немного ниже верхнего края для более естественного эффекта

        // Базовое масштабирование (show/hide анимация)
        matrix.translate(centerX, centerY, 0);
        matrix.scale(scaleV * dragScaleEffect, scaleV * dragScaleEffect, 1);
        matrix.translate(-centerX, -centerY, 0);

        // Применяем смещение пружины
        matrix.translate(springOffsetX, springOffsetY, 0);

        // Применяем наклон вокруг точки подвеса
        if (Math.abs(currentRotation) > 0.01f) {
            matrix.translate(pivotX, pivotY, 0);
            matrix.rotate(net.minecraft.util.math.vector.Vector3f.ZP.rotationDegrees(currentRotation));
            matrix.translate(-pivotX, -pivotY, 0);
        }

        // === Рендер основного контента ===
        RenderUtil.CustomStyled.draw(matrix, x, y, width, height, Round.of(containerRadius), blurEnabled, blurStrength);

        if (enableOutline) {
            int themeColor = Theme.getInstance().clientColor();
            int outlineColor = ColorUtil.interpolateColor(ColorUtil.getColor(255, 255, 255, 255), themeColor, 0.5f);
            float halfWidth = outlineWidth * finalScale / 2f;
            RenderUtil.Rounded.roundedOutline(matrix,
                    x - halfWidth,
                    y - halfWidth,
                    width + outlineWidth * finalScale,
                    height + outlineWidth * finalScale,
                    outlineWidth * finalScale,
                    outlineColor,
                    Round.of(containerRadius));
        }

        if (showTitle) {
            ScissorUtil.enableScissor(x, y, width, titleHeight);
            RenderUtil.Rounded.smooth(matrix, x, y, width, height,
                    ColorUtil.replAlpha(new Color(0xFF000000, true).getRGB(), titleDarkness),
                    Round.of(containerRadius, containerRadius, containerRadius, containerRadius));
            ScissorUtil.disableScissor();

            renderTitleContent(matrix, x, y, width, titleHeight, finalScale, itemCount);
        }

        float itemStartY = y + titleHeight + padding + 2 * finalScale;
        for (int i = 0; i < 27; i++) {
            float itemX = x + padding + (i % 9) * itemSpacing;
            float itemY = itemStartY + (i / 9) * itemSpacing - 5 * finalScale;

            ItemStack stack = mc.player.inventory.mainInventory.get(i + 9);

            matrix.push();
            matrix.translate(itemX + 8 * finalScale, itemY + 8 * finalScale, 0);
            matrix.scale(itemScale, itemScale, 1);
            matrix.translate(-(itemX + 8 * finalScale), -(itemY + 8 * finalScale), 0);

            mc.getItemRenderer().renderItemIntoGUI(stack, (int) itemX, (int) itemY);
            mc.getItemRenderer().renderItemOverlays(mc.fontRenderer, stack, (int) itemX, (int) itemY);

            matrix.pop();
        }
        matrix.pop();
    }

    private void renderTitleContent(MatrixStack matrix, float x, float y, float width, float height, float finalScale, int count) {
        float padding = 6f * finalScale;
        float fontSize = 7f * finalScale;
        float iconSize = 10f * finalScale;
        float offsetX = 0.1f * finalScale;
        float offsetY = 0.25f * finalScale;

        float currentX = x + padding - offsetX;
        int hud = theme().clientColor();

        if (showTitleIcon) {
            float iconY = y + (height - iconSize) / 2f - offsetY;
            Fonts.icon.draw(matrix, "X", currentX, iconY - 1, hud, iconSize);
            currentX += iconSize + 6f * finalScale;
        }

        float textY = y + (height - fontSize) / 2f - offsetY;
        font.draw(matrix, "Inventory", currentX + 7.5f * finalScale, textY, -1, fontSize);

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

            font.draw(matrix, countText, countRectX + 1.5f * finalScale, countRectY - 1 + finalScale, hud, countFontSize);
        }
    }
}