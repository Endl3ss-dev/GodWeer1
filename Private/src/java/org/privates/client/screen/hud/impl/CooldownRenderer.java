package org.privates.client.screen.hud.impl;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.item.Item;
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
import org.privates.client.utils.render.draw.RectUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.draw.ScissorUtil;
import org.privates.client.utils.render.font.Fonts;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class CooldownRenderer extends AbstractHud implements IRenderer, IConfigurableHud {
    private static final String POINT = " • ";
    private static final int DOT_COLOR = new Color(180, 180, 200, 255).getRGB();

    private final DragSetting drag;
    private final Map<Item, CooldownEntry> cooldowns = new HashMap<>();
    private final Map<Item, Float> animatedProgressMap = new HashMap<>();
    private final Map<Item, Float> rectScaleMap = new HashMap<>();
    private final Map<Item, Float> iconScaleMap = new HashMap<>();
    private final Map<Item, Long> cooldownStartTime = new HashMap<>();
    private final List<PreviewCooldown> previewCooldowns = new ArrayList<>();

    private float animatedWidth, animatedHeight;
    @Getter
    @Setter
    private float scaleV;
    @Getter
    @Setter
    private boolean blurEnabled = true;
    @Getter
    @Setter
    private float blurStrength = 15f;
    @Getter
    @Setter
    private boolean enableOutline = false;
    @Getter
    @Setter
    private float outlineWidth = 1.0f;

    public float scale = 0.9f, titleDarkness = 0.0f, dynamicRectGap = 3.0f, newRectGap = 3.0f;
    public boolean showTitleIcon = true, showItemIcon = true, showCount = true, showTitle = true;
    public boolean dynamicRects = false, newRect = false, newRender = false;

    private long previewStartTime = 0;
    private float lastX = 0, lastY = 0, smoothVelocityX = 0, smoothVelocityY = 0;
    private float currentRotation = 0, targetRotation = 0, springOffsetX = 0, springOffsetY = 0;
    private float dragScaleEffect = 1.0f;
    private boolean isDragging = false;
    private long lastMoveTime = 0;

    private static class CooldownEntry {
        ItemStack stack;
        float progress;
        float maxCooldown = 0f;

        CooldownEntry(ItemStack stack, float progress, float maxCooldown) {
            this.stack = stack;
            this.progress = progress;
            this.maxCooldown = maxCooldown;
        }
    }

    private static class PreviewCooldown {
        ItemStack stack;
        float maxCooldown;
        long cycleStartTime;
        long cycleDuration;

        PreviewCooldown(ItemStack stack, float maxCooldown, long cycleStartTime, long cycleDuration) {
            this.stack = stack;
            this.maxCooldown = maxCooldown;
            this.cycleStartTime = cycleStartTime;
            this.cycleDuration = cycleDuration;
        }

        float getCurrentProgress(long currentTime) {
            long elapsed = currentTime - cycleStartTime;
            if (elapsed >= cycleDuration) {
                cycleStartTime = currentTime;
                elapsed = 0;
            }
            return 1.0f - (float) elapsed / cycleDuration;
        }
    }

    public CooldownRenderer(DragSetting drag) {
        this.drag = drag;
        if (drag.position.x == 0 && drag.position.y == 0) {
            float screenHeight = mc.getMainWindow().getScaledHeight();
            drag.position.set(10, screenHeight / 2f);
        }
    }

    @Override
    public List<DragComponent.HudSetting> getHudSettings() {
        return Arrays.asList(
                new DragComponent.HudSetting("New Render", DragComponent.SettingType.BOOLEAN, () -> this.newRender ? 1f : 0f, v -> this.newRender = v > 0.5f),
                new DragComponent.HudSetting("Scale", DragComponent.SettingType.SLIDER, () -> this.scale, v -> this.scale = v, 0.5f, 2.0f, 0.1f),
                new DragComponent.HudSetting("Title Darkness", DragComponent.SettingType.SLIDER, () -> this.titleDarkness, v -> this.titleDarkness = v, 0.0f, 1.0f, 0.05f),
                new DragComponent.HudSetting("Title Icon", DragComponent.SettingType.BOOLEAN, () -> this.showTitleIcon ? 1f : 0f, v -> this.showTitleIcon = v > 0.5f),
                new DragComponent.HudSetting("Item Icon", DragComponent.SettingType.BOOLEAN, () -> this.showItemIcon ? 1f : 0f, v -> this.showItemIcon = v > 0.5f),
                new DragComponent.HudSetting("Show Count", DragComponent.SettingType.BOOLEAN, () -> this.showCount ? 1f : 0f, v -> this.showCount = v > 0.5f),
                new DragComponent.HudSetting("Show Title", DragComponent.SettingType.BOOLEAN, () -> this.showTitle ? 1f : 0f, v -> this.showTitle = v > 0.5f),
                new DragComponent.HudSetting("Dynamic Rects", DragComponent.SettingType.BOOLEAN, () -> this.dynamicRects ? 1f : 0f, v -> this.dynamicRects = v > 0.5f),
                new DragComponent.HudSetting("Dynamic Rect Gap", DragComponent.SettingType.SLIDER, () -> this.dynamicRectGap, v -> this.dynamicRectGap = v, 0f, 10f, 0.5f),
                new DragComponent.HudSetting("New Rect", DragComponent.SettingType.BOOLEAN, () -> this.newRect ? 1f : 0f, v -> this.newRect = v > 0.5f),
                new DragComponent.HudSetting("New Rect Gap", DragComponent.SettingType.SLIDER, () -> this.newRectGap, v -> this.newRectGap = v, 0f, 10f, 0.5f),
                new DragComponent.HudSetting("Enable Outline", DragComponent.SettingType.BOOLEAN, () -> this.enableOutline ? 1f : 0f, v -> this.enableOutline = v > 0.5f),
                new DragComponent.HudSetting("Outline Width", DragComponent.SettingType.SLIDER, () -> this.outlineWidth, v -> this.outlineWidth = v, 0.5f, 3.0f, 0.1f)
        );
    }

    private float getResolutionScale() {
        int width = mc.getMainWindow().getWidth();
        int height = mc.getMainWindow().getHeight();
        if (width >= 3840 && height >= 2160) return 1f;
        else if (width >= 2560 && height >= 1440) return 1.0f;
        else if (width >= 1920 || height >= 1080) return 1f;
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

        targetRotation = Math.max(-15f, Math.min(15f, smoothVelocityX * 1.8f - smoothVelocityY * 0.3f));
        currentRotation = elasticLerp(currentRotation, targetRotation, isDragging ? 0.18f : 0.06f, 0.15f);

        if (!isDragging && Math.abs(currentRotation) < 0.1f) {
            currentRotation *= 0.9f;
        }

        springOffsetX = Math.max(-5f, Math.min(5f, springOffsetX * 0.85f + (-smoothVelocityX * 0.3f - springOffsetX) * 0.12f));
        springOffsetY = Math.max(-5f, Math.min(5f, springOffsetY * 0.85f + (-smoothVelocityY * 0.2f - springOffsetY) * 0.12f));

        dragScaleEffect = smoothLerp(dragScaleEffect, isDragging ? 1.02f : 1.0f, 0.15f);
        lastX = x;
        lastY = y;
    }

    private void applyDragTransform(MatrixStack matrix, float x, float y, float width, float height) {
        float pivotX = x + width / 2f;
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

    private void addPreviewCooldowns() {
        long currentTime = System.currentTimeMillis();
        if (previewStartTime == 0) {
            previewStartTime = currentTime;
            previewCooldowns.clear();
            previewCooldowns.add(new PreviewCooldown(new ItemStack(Items.ENDER_PEARL), 20f, currentTime, 5000));
            previewCooldowns.add(new PreviewCooldown(new ItemStack(Items.SHIELD), 100f, currentTime, 8000));
            previewCooldowns.add(new PreviewCooldown(new ItemStack(Items.GOLDEN_APPLE), 80f, currentTime, 10000));
        }
        cooldowns.clear();

        for (PreviewCooldown preview : previewCooldowns) {
            float currentProgress = preview.getCurrentProgress(currentTime);
            Item itemKey = preview.stack.getItem();

            cooldowns.put(itemKey, new CooldownEntry(preview.stack, currentProgress, preview.maxCooldown));
            animatedProgressMap.put(itemKey, currentProgress);

            if (!rectScaleMap.containsKey(itemKey)) {
                rectScaleMap.put(itemKey, 0f);
            }

            if (!iconScaleMap.containsKey(itemKey)) {
                iconScaleMap.put(itemKey, 0f);
            }

            if (!cooldownStartTime.containsKey(itemKey)) {
                cooldownStartTime.put(itemKey, currentTime);
            }
        }
    }

    private void updateCooldowns() {
        if (mc.player == null) return;
        Map<Item, ItemStack> currentItems = new HashMap<>();
        Map<Item, Float> currentProgs = new HashMap<>();
        Map<Item, Float> maxCooldowns = new HashMap<>();

        for (ItemStack stack : mc.player.inventory.mainInventory) {
            Item item = stack.getItem();
            if (mc.player.getCooldownTracker().hasCooldown(item)) {
                float prog = mc.player.getCooldownTracker().getCooldown(item, mc.getRenderPartialTicks());
                if (!currentItems.containsKey(item)) currentItems.put(item, stack);
                currentProgs.put(item, prog);
                if (!cooldowns.containsKey(item) || cooldowns.get(item).maxCooldown == 0f)
                    maxCooldowns.put(item, getItemMaxCooldown(item));
                else maxCooldowns.put(item, cooldowns.get(item).maxCooldown);
            }
        }

        for (ItemStack stack : mc.player.inventory.offHandInventory) {
            Item item = stack.getItem();
            if (mc.player.getCooldownTracker().hasCooldown(item)) {
                float prog = mc.player.getCooldownTracker().getCooldown(item, mc.getRenderPartialTicks());
                if (!currentItems.containsKey(item)) currentItems.put(item, stack);
                currentProgs.put(item, prog);
                if (!cooldowns.containsKey(item) || cooldowns.get(item).maxCooldown == 0f)
                    maxCooldowns.put(item, getItemMaxCooldown(item));
                else maxCooldowns.put(item, cooldowns.get(item).maxCooldown);
            }
        }

        Set<Item> toRemove = new HashSet<>();
        for (Item item : cooldowns.keySet()) {
            if (!currentProgs.containsKey(item)) {
                toRemove.add(item);
            }
        }
        for (Item item : toRemove) {
            cooldowns.remove(item);
            animatedProgressMap.remove(item);
            rectScaleMap.remove(item);
            iconScaleMap.remove(item);
            cooldownStartTime.remove(item);
        }

        for (Item item : currentProgs.keySet()) {
            Float newProg = currentProgs.get(item);
            if (cooldowns.containsKey(item)) {
                CooldownEntry entry = cooldowns.get(item);
                entry.progress = newProg;
                entry.stack = currentItems.get(item);
                entry.maxCooldown = maxCooldowns.getOrDefault(item, entry.maxCooldown);
            } else {
                ItemStack stack = currentItems.get(item);
                float maxCd = maxCooldowns.get(item);
                cooldowns.put(item, new CooldownEntry(stack, newProg, maxCd));
                rectScaleMap.put(item, 0f);
                iconScaleMap.put(item, 0f);
                cooldownStartTime.put(item, System.currentTimeMillis());
                animatedProgressMap.put(item, newProg);
            }
        }
    }

    private float getItemMaxCooldown(Item item) {
        if (item == Items.ENDER_PEARL) return 20f;
        if (item == Items.CHORUS_FRUIT) return 20f;
        if (item == Items.SHIELD) return 100f;
        if (item == Items.GOLDEN_APPLE) return 80f;
        if (item == Items.ENCHANTED_GOLDEN_APPLE) return 120f;
        return 20f;
    }

    private String formatTime(float progress, float maxCooldown) {
        if (progress <= 0.01f) return "00:00";
        float remainingSeconds = maxCooldown * progress;
        int totalSeconds = (int) Math.ceil(remainingSeconds);
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    public void render(Render2DEvent event, boolean glow) {
        boolean isPreview = mc.currentScreen instanceof ChatScreen;
        if (isPreview) addPreviewCooldowns();
        else {
            previewStartTime = 0;
            previewCooldowns.clear();
            updateCooldowns();
        }

        long currentTime = System.currentTimeMillis();
        List<Item> toRemove = new ArrayList<>();
        for (Item item : cooldowns.keySet()) {
            CooldownEntry data = cooldowns.get(item);
            float progress = data.progress;
            float animatedProgress = animatedProgressMap.getOrDefault(item, progress);
            animatedProgress = Mathf.fast(animatedProgress, progress, 10);
            animatedProgressMap.put(item, animatedProgress);

            float rectTarget = progress > 0.01f ? 1f : 0f;
            float rectScale = rectScaleMap.getOrDefault(item, 0f);
            rectScale = Mathf.fast(rectScale, rectTarget, 16f);
            rectScaleMap.put(item, rectScale);

            float iconTarget;
            if (isPreview) {
                iconTarget = progress > 0.01f ? 1f : 0f;
            } else {
                long start = cooldownStartTime.getOrDefault(item, 0L);
                iconTarget = (currentTime - start > 150L) && progress > 0.01f ? 1f : 0f;
            }
            float iconScale = iconScaleMap.getOrDefault(item, 0f);
            iconScale = Mathf.fast(iconScale, iconTarget, 12f);
            iconScaleMap.put(item, iconScale);

            if (rectScale <= 0.01f && progress <= 0.01f) {
                toRemove.add(item);
            }
        }
        for (Item item : toRemove) {
            cooldowns.remove(item);
            animatedProgressMap.remove(item);
            rectScaleMap.remove(item);
            iconScaleMap.remove(item);
            cooldownStartTime.remove(item);
        }

        MatrixStack matrix = event.getMatrix();
        float x = drag.position.x;
        float y = drag.position.y;
        float resScale = getResolutionScale();
        float finalScale = resScale * scale;

        boolean hasActiveCooldowns = !cooldowns.isEmpty();
        scaleV = Mathf.fast(scaleV, hasActiveCooldowns || isPreview ? 1 : 0, 12);
        if (Mathf.round(scaleV, .1d) <= 0) return;

        if (newRender) {
            renderNewStyle(matrix, x, y, finalScale, isPreview);
            return;
        }

        String title = "Cooldowns";
        float padding = 6f * finalScale;
        float fontSize = 7f * finalScale;
        float containerRadius = 6f * finalScale;
        float titleBoxHeight = showTitle ? 18f * finalScale : 0f;
        float itemRowHeight = 18f * finalScale;
        float iconSize = 10f * finalScale;

        float titleIconSize = 10f * finalScale;
        float titleIconWidth = showTitleIcon ? (titleIconSize + 6f * finalScale) : 0f;
        float titleTextWidth = font.getWidth(title, fontSize);
        float titleIconTextGap = 7.5f * finalScale;
        float countBoxWidth = showCount ? (font.getWidth(String.valueOf(cooldowns.size()), 6f * finalScale) + 8f * finalScale) : 0f;
        float pointWidth = font.getWidth(POINT, fontSize);

        float titleWidth = padding + titleIconWidth + (showTitleIcon ? pointWidth : 0) + titleTextWidth + (showCount && (dynamicRects || newRect) ? (titleIconTextGap + countBoxWidth) : 0f) + padding;
        float targetWidth = titleWidth;

        // Sort cooldowns by width for dynamic rects
        List<Map.Entry<Item, CooldownEntry>> sortedCooldowns;
        if (dynamicRects) {
            sortedCooldowns = cooldowns.entrySet().stream()
                    .sorted((e1, e2) -> {
                        String name1 = e1.getValue().stack.getDisplayName().getString();
                        String time1 = formatTime(e1.getValue().progress, e1.getValue().maxCooldown);
                        float iconW1 = showItemIcon ? (iconSize + 6f * finalScale) : 0f;
                        float width1 = padding + iconW1 + (showItemIcon ? pointWidth : 0) + font.getWidth(name1, fontSize) + pointWidth + font.getWidth(time1, fontSize) + padding;
                        
                        String name2 = e2.getValue().stack.getDisplayName().getString();
                        String time2 = formatTime(e2.getValue().progress, e2.getValue().maxCooldown);
                        float iconW2 = showItemIcon ? (iconSize + 6f * finalScale) : 0f;
                        float width2 = padding + iconW2 + (showItemIcon ? pointWidth : 0) + font.getWidth(name2, fontSize) + pointWidth + font.getWidth(time2, fontSize) + padding;
                        
                        return Float.compare(width2, width1); // Descending order (widest first)
                    })
                    .collect(Collectors.toList());
            
            // Make title width match the widest item (first in sorted list)
            if (!sortedCooldowns.isEmpty()) {
                CooldownEntry widest = sortedCooldowns.get(0).getValue();
                String name = widest.stack.getDisplayName().getString();
                String time = formatTime(widest.progress, widest.maxCooldown);
                float iconW = showItemIcon ? (iconSize + 6f * finalScale) : 0f;
                float widestWidth = padding + iconW + (showItemIcon ? pointWidth : 0) + font.getWidth(name, fontSize) + pointWidth + font.getWidth(time, fontSize) + padding;
                titleWidth = widestWidth;
                targetWidth = widestWidth;
            }
        } else {
            sortedCooldowns = cooldowns.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey(Comparator.comparing(Item::getTranslationKey)))
                    .collect(Collectors.toList());
        }

        if (newRect) {
            float maxItemWidth = 0f;
            for (CooldownEntry data : cooldowns.values()) {
                String itemName = data.stack.getDisplayName().getString();
                String timeText = formatTime(data.progress, data.maxCooldown);
                float itemIconWidth = showItemIcon ? (iconSize + 6f * finalScale) : 0f;
                float itemWidth = padding + itemIconWidth + (showItemIcon ? pointWidth : 0) + font.getWidth(itemName, fontSize) + pointWidth + font.getWidth(timeText, fontSize) + padding;
                maxItemWidth = Math.max(maxItemWidth, itemWidth);
            }
            targetWidth = Math.max(titleWidth, maxItemWidth);
        } else if (!dynamicRects) {
            for (CooldownEntry data : cooldowns.values()) {
                String itemName = data.stack.getDisplayName().getString();
                String timeText = formatTime(data.progress, data.maxCooldown);
                float itemIconWidth = showItemIcon ? (iconSize + 6f * finalScale) : 0f;
                float itemWidth = padding + itemIconWidth + (showItemIcon ? pointWidth : 0) + font.getWidth(itemName, fontSize) + pointWidth + font.getWidth(timeText, fontSize) + padding;
                targetWidth = Math.max(targetWidth, itemWidth);
            }
        }

        float targetHeight = showTitle ? titleBoxHeight : 0f;
        for (Item item : cooldowns.keySet()) {
            float thisAnim = rectScaleMap.get(item);
            if (dynamicRects) {
                targetHeight += (itemRowHeight + dynamicRectGap * finalScale) * thisAnim;
            } else if (newRect) {
                targetHeight += (itemRowHeight + newRectGap * finalScale) * thisAnim;
            } else {
                targetHeight += itemRowHeight * thisAnim;
            }
        }

        animatedWidth = Mathf.fast(animatedWidth, targetWidth, 14);
        animatedHeight = Mathf.fast(animatedHeight, targetHeight, 14);

        if (x == 0 && y == 0) {
            float screenHeight = mc.getMainWindow().getScaledHeight();
            float newX = 10 * finalScale;
            float newY = screenHeight / 2f - animatedHeight / 2f;
            drag.position.set(newX, newY);
            x = newX;
            y = newY;
            lastX = x;
            lastY = y;
        }

        updateDragAnimation(x, y, animatedWidth, animatedHeight);

        boolean alignRight = x > mc.getMainWindow().getScaledWidth() / 2f;

        matrix.push();
        matrix.translate((x + animatedWidth / 2F), (y + animatedHeight / 2f), 0);
        matrix.scale(scaleV * dragScaleEffect, scaleV * dragScaleEffect, scaleV);
        matrix.translate(-(x + animatedWidth / 2F), -(y + animatedHeight / 2f), 0);

        applyDragTransform(matrix, x, y, animatedWidth, animatedHeight);

        if (!dynamicRects && !newRect)
            RenderUtil.CustomStyled.draw(matrix, x, y, animatedWidth, animatedHeight, Round.of(containerRadius), blurEnabled, blurStrength);

        float currentY = y;

        if (showTitle) {
            if (dynamicRects || newRect) {
                float titleX = alignRight ? x + (animatedWidth - targetWidth) : x;
                RenderUtil.CustomStyled.draw(matrix, titleX, currentY, targetWidth, titleBoxHeight, Round.of(containerRadius), blurEnabled, blurStrength);
                ScissorUtil.enableScissor(titleX, currentY, targetWidth, titleBoxHeight);
                RenderUtil.Rounded.smooth(matrix, titleX, currentY, targetWidth, titleBoxHeight, ColorUtil.replAlpha(new Color(0xFF000000, true).getRGB(), titleDarkness), Round.of(0, containerRadius, 0, containerRadius));
                ScissorUtil.disableScissor();
                renderTitleContent(matrix, titleX, currentY, targetWidth, titleBoxHeight, title, fontSize, titleIconSize, finalScale, cooldowns.size(), titleIconTextGap, pointWidth, true);
                if (enableOutline) {
                    int themeColor = Theme.getInstance().clientColor();
                    int outlineColor = ColorUtil.interpolateColor(ColorUtil.getColor(255, 255, 255, 255), themeColor, 0.5f);
                    float halfWidth = outlineWidth * finalScale / 2f;
                    RenderUtil.Rounded.roundedOutline(matrix,
                            titleX - halfWidth,
                            currentY - halfWidth,
                            targetWidth + outlineWidth * finalScale,
                            titleBoxHeight + outlineWidth * finalScale,
                            outlineWidth * finalScale,
                            outlineColor,
                            Round.of(containerRadius));
                }
            } else {
                ScissorUtil.enableScissor(x, currentY, animatedWidth, titleBoxHeight);
                RenderUtil.Rounded.smooth(matrix, x, currentY, animatedWidth, titleBoxHeight, ColorUtil.replAlpha(new Color(0xFF000000, true).getRGB(), titleDarkness), Round.of(0, containerRadius, 0, containerRadius));
                ScissorUtil.disableScissor();
                renderTitleContent(matrix, x, currentY, animatedWidth, titleBoxHeight, title, fontSize, titleIconSize, finalScale, cooldowns.size(), titleIconTextGap, pointWidth, false);
            }
        }

        currentY += titleBoxHeight;
        if (dynamicRects) currentY += dynamicRectGap * finalScale;
        else if (newRect) currentY += newRectGap * finalScale;

        for (Map.Entry<Item, CooldownEntry> entry : sortedCooldowns) {
            Item item = entry.getKey();
            CooldownEntry data = entry.getValue();
            float rectScale = rectScaleMap.get(item);
            if (rectScale <= 0.01f) {
                if (dynamicRects) currentY += itemRowHeight + dynamicRectGap * finalScale;
                else if (newRect) currentY += itemRowHeight + newRectGap * finalScale;
                else currentY += itemRowHeight;
                continue;
            }
            float itemHeight = itemRowHeight * rectScale;
            if (dynamicRects) {
                String itemName = data.stack.getDisplayName().getString();
                String timeText = formatTime(data.progress, data.maxCooldown);
                float itemIconWidth = showItemIcon ? (iconSize + 6f * finalScale) : 0f;
                float itemWidth = padding + itemIconWidth + (showItemIcon ? pointWidth : 0) + font.getWidth(itemName, fontSize) + pointWidth + font.getWidth(timeText, fontSize) + padding;
                float itemX = alignRight ? x + (animatedWidth - itemWidth) : x;
                renderItemCooldownDynamic(matrix, itemX, currentY, itemWidth, itemRowHeight, item, data, fontSize, iconSize, finalScale, isPreview, containerRadius, alignRight, rectScale, iconScaleMap.get(item), pointWidth);
                currentY += itemHeight + dynamicRectGap * finalScale;
            } else if (newRect) {
                renderItemCooldownNew(matrix, x, currentY, animatedWidth, itemRowHeight, item, data, fontSize, iconSize, finalScale, isPreview, containerRadius, alignRight, rectScale, iconScaleMap.get(item), pointWidth);
                currentY += itemHeight + newRectGap * finalScale;
            } else {
                renderItemCooldown(matrix, x, currentY, animatedWidth, itemRowHeight, item, data, fontSize, iconSize, finalScale, isPreview, rectScale, iconScaleMap.get(item), pointWidth);
                currentY += itemHeight;
            }
        }

        if (enableOutline && !dynamicRects && !newRect) {
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

        drag.size.set(animatedWidth, animatedHeight);
        matrix.pop();
    }

    private void renderNewStyle(MatrixStack matrix, float x, float y, float finalScale, boolean isPreview) {
        float itemHeight = 24f * finalScale;
        float gap = 4f * finalScale;
        float radius = 5f * finalScale;
        float iconSize = 16f * finalScale;
        float padding = 4f * finalScale;

        List<Map.Entry<Item, CooldownEntry>> sortedCooldowns = cooldowns.entrySet().stream()
                .sorted(Map.Entry.comparingByKey(Comparator.comparing(Item::getTranslationKey)))
                .collect(Collectors.toList());

        float maxWidth = 0f;
        for (Map.Entry<Item, CooldownEntry> entry : sortedCooldowns) {
            CooldownEntry data = entry.getValue();
            String itemName = data.stack.getDisplayName().getString();
            String timeText = formatTime(data.progress, data.maxCooldown);
            float nameWidth = font.getWidth(itemName, 7f * finalScale);
            float timeWidth = font.getWidth(timeText, 6.5f * finalScale);
            float itemWidth = padding + iconSize + padding + nameWidth + padding * 2 + timeWidth + padding;
            maxWidth = Math.max(maxWidth, itemWidth);
        }

        int count = sortedCooldowns.size();
        float totalWidth = maxWidth;
        float totalHeight = count * itemHeight + (count - 1) * gap;
        if (count == 0) {
            totalWidth = 100f * finalScale;
            totalHeight = itemHeight;
        }

        animatedWidth = Mathf.fast(animatedWidth, totalWidth, 14);
        animatedHeight = Mathf.fast(animatedHeight, totalHeight, 14);

        if (x == 0 && y == 0) {
            lastX = x;
            lastY = y;
        }

        updateDragAnimation(x, y, animatedWidth, animatedHeight);

        drag.size.set(animatedWidth, animatedHeight);

        matrix.push();
        matrix.translate(x + animatedWidth / 2f, y + animatedHeight / 2f, 0);
        matrix.scale(scaleV * dragScaleEffect, scaleV * dragScaleEffect, scaleV);
        matrix.translate(-(x + animatedWidth / 2f), -(y + animatedHeight / 2f), 0);

        applyDragTransform(matrix, x, y, animatedWidth, animatedHeight);

        float currentY = y;

        for (Map.Entry<Item, CooldownEntry> entry : sortedCooldowns) {
            Item item = entry.getKey();
            CooldownEntry data = entry.getValue();
            float progress = data.progress;
            float animatedProgress = animatedProgressMap.getOrDefault(item, progress);

            float rectScale = rectScaleMap.get(item);
            if (rectScale <= 0.01f) {
                currentY += itemHeight + gap;
                continue;
            }

            float iconScale = iconScaleMap.get(item);
            float smoothScale = easeOutCubic(rectScale);
            float currentItemHeight = itemHeight * smoothScale;

            RenderUtil.CustomStyled.draw(matrix, x, currentY, animatedWidth, currentItemHeight, Round.of(radius), blurEnabled, blurStrength);

            if (animatedProgress > 0.01f) {
                float hue = Math.max(0f, (1.0f - animatedProgress) * 0.33f);
                int baseColor = Color.HSBtoRGB(hue, 0.5f, 0.6f);
                int bgColor = ColorUtil.multAlpha(baseColor, 0.15f);
                RenderUtil.Rounded.roundedRect(matrix, x, currentY, animatedWidth, currentItemHeight, bgColor, Round.of(radius));
            }

            if (enableOutline) {
                int themeColor = Theme.getInstance().clientColor();
                int outlineColor = ColorUtil.interpolateColor(ColorUtil.getColor(255, 255, 255, 255), themeColor, 0.5f);
                float halfWidth = outlineWidth * finalScale / 2f;
                RenderUtil.Rounded.roundedOutline(matrix,
                        x - halfWidth,
                        currentY - halfWidth,
                        animatedWidth + outlineWidth * finalScale,
                        currentItemHeight + outlineWidth * finalScale,
                        outlineWidth * finalScale,
                        outlineColor,
                        Round.of(radius));
            }

            if (animatedProgress > 0.01f) {
                float hue = Math.max(0f, (1.0f - animatedProgress) * 0.33f);
                int baseColor = Color.HSBtoRGB(hue, 0.8f, 0.9f);
                int barColor = ColorUtil.multAlpha(baseColor, 0.7f);
                float barWidth = animatedWidth * animatedProgress;
                RenderUtil.Rounded.roundedRect(matrix, x, currentY, barWidth, currentItemHeight, barColor, Round.of(radius, 0, 0, radius));
            }

            matrix.push();
            matrix.translate(x + animatedWidth / 2f, currentY + currentItemHeight / 2f, 0);
            matrix.scale(1f, smoothScale, 1f);
            matrix.translate(-(x + animatedWidth / 2f), -(currentY + currentItemHeight / 2f), 0);

            float itemX = x + padding;
            float itemY = currentY + (currentItemHeight - iconSize) / 2f;

            GlStateManager.pushMatrix();
            GlStateManager.translatef(itemX + iconSize / 2, itemY + iconSize / 2, 0);
            GlStateManager.scalef((iconSize / 16f) * iconScale, (iconSize / 16f) * iconScale, 1f);
            mc.getItemRenderer().renderItemIntoGUI(data.stack, -8, -8);
            GlStateManager.popMatrix();

            String itemName = data.stack.getDisplayName().getString();
            float nameSize = 7f * finalScale;
            float nameX = itemX + iconSize + padding;
            float nameY = currentY + (currentItemHeight - nameSize) / 2f;
            int nameColor = ColorUtil.getColor(255, 255, 255, (int)(220 * smoothScale));
            font.draw(matrix, itemName, nameX, nameY, nameColor, nameSize);

            String timeText = formatTime(animatedProgress, data.maxCooldown);
            float timeSize = 6.5f * finalScale;
            float timeWidth = font.getWidth(timeText, timeSize);
            float timeX = x + animatedWidth - timeWidth - padding;
            float timeY = currentY + (currentItemHeight - timeSize) / 2f;
            int themeColor = Theme.getInstance().clientColor();
            int timeColor = ColorUtil.replAlpha(themeColor, (int)(255 * smoothScale));
            font.draw(matrix, timeText, timeX, timeY, timeColor, timeSize);

            matrix.pop();

            currentY += itemHeight + gap;
        }

        matrix.pop();
    }

    private void renderTitleContent(MatrixStack matrix, float x, float y, float width, float height, String title, float fontSize, float iconSize, float finalScale, int count, float titleIconTextGap, float pointWidth, boolean isDynamic) {
        float padding = 6f * finalScale;
        float offsetY = 0.25f * finalScale;
        int hud = Theme.getInstance().clientColor();

        if (isDynamic) {
            // Align with item layout: icon at padding, text after icon+gap+point, count at right
            float currentX = x + padding;
            if (showTitleIcon) {
                float iconY = y + (height - iconSize) / 2f - offsetY;
                Fonts.icon.draw(matrix, "O", currentX, iconY - 1, hud, iconSize);
                currentX += iconSize + 6f * finalScale;
            }
            
            // Add point spacing if item icons are shown (to align with item text)
            if (showItemIcon) {
                currentX += pointWidth;
            }
            
            float textY = y + (height - fontSize) / 2f - offsetY;
            font.draw(matrix, title, currentX, textY, -1, fontSize);
            
            // Count aligned with time text position (right side)
            if (showCount) {
                float countFontSize = 6f * finalScale;
                String countText = String.valueOf(count);
                float countWidth = font.getWidth(countText, countFontSize);
                float countRectX = x + width - countWidth - padding;
                float countRectY = y + (height - countFontSize) / 2f;
                font.draw(matrix, countText, countRectX, countRectY - 0.45f + finalScale, hud, countFontSize);
            }
        } else {
            float offsetX = 2f * finalScale;
            float currentX = x + padding - offsetX;
            if (showTitleIcon) {
                float iconY = y + (height - iconSize) / 2f - offsetY;
                Fonts.icon.draw(matrix, "O", currentX, iconY - 1, hud, iconSize);
                currentX += iconSize + 6f * finalScale;
                currentX += pointWidth;
            }
            float textY = y + (height - fontSize) / 2f - offsetY;
            font.draw(matrix, title, currentX + 4f * finalScale, textY, -1, fontSize);
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

    private void renderItemCooldownDynamic(MatrixStack matrix, float x, float y, float width, float height, Item item, CooldownEntry data, float fontSize, float iconSize, float finalScale, boolean isPreview, float radius, boolean alignRight, float rectScale, float iconScale, float pointWidth) {
        float padding = 6f * finalScale;
        String itemName = data.stack.getDisplayName().getString();
        float smoothAnim = easeOutCubic(rectScale);
        float textAnim = easeOutQuart(rectScale);

        if (rectScale > 0.01f) {
            RenderUtil.CustomStyled.draw(matrix, x, y, width, height * rectScale, Round.of(radius), blurEnabled, blurStrength);
            if (enableOutline) {
                int themeColor = Theme.getInstance().clientColor();
                int outlineColor = ColorUtil.interpolateColor(ColorUtil.getColor(255, 255, 255, 255), themeColor, 0.5f);
                float halfWidth = outlineWidth * finalScale / 2f;
                RenderUtil.Rounded.roundedOutline(matrix,
                        x - halfWidth,
                        y - halfWidth,
                        width + outlineWidth * finalScale,
                        (height * rectScale) + outlineWidth * finalScale,
                        outlineWidth * finalScale,
                        outlineColor,
                        Round.of(radius));
            }
        }

        matrix.push();
        float translateX = alignRight ? (smoothAnim - 1) * 10f * finalScale : (1 - smoothAnim) * 10f * finalScale;
        matrix.translate(translateX, 0, 0);
        matrix.translate(x, y + (height * rectScale) / 2f, 0);
        matrix.scale(1f, 0.3f + (smoothAnim * 0.7f), 1f);
        matrix.translate(-x, -(y + (height * rectScale) / 2f), 0);

        float currentX = x + padding;

        if (showItemIcon && rectScale > 0.01f) {
            float itemRenderSize = iconSize * rectScale;
            float itemY = y + ((height * rectScale) - itemRenderSize) / 2f;

            GlStateManager.pushMatrix();
            GlStateManager.translatef(currentX + itemRenderSize / 2, itemY + itemRenderSize / 2, 0);
            GlStateManager.scalef((itemRenderSize / 16f) * iconScale, (itemRenderSize / 16f) * iconScale, 1f);
            mc.getItemRenderer().renderItemIntoGUI(data.stack, -8, -8);
            GlStateManager.popMatrix();
            currentX += itemRenderSize + 6f * finalScale;

            font.draw(matrix, POINT, currentX - 2.5f, y + (height * rectScale - fontSize) / 2f, ColorUtil.getColor(DOT_COLOR >> 16 & 0xFF, DOT_COLOR >> 8 & 0xFF, DOT_COLOR & 0xFF, (int) (((DOT_COLOR >> 24 & 0xFF) * textAnim))), fontSize);
            currentX += pointWidth;
        }

        float textY = y + ((height * rectScale) - fontSize) / 2f;
        String timeText = formatTime(data.progress, data.maxCooldown);
        float timeWidth = font.getWidth(timeText, fontSize);
        float timeX = x + width - timeWidth - padding;
        float nameMaxWidth = timeX - currentX - padding;

        int nameColor = ColorUtil.getColor(255, 255, 255, (int) (235 * textAnim));
        ScissorUtil.enableScissor(currentX, y, nameMaxWidth + 2, height * rectScale);
        font.draw(matrix, itemName, currentX, textY, nameColor, fontSize);
        ScissorUtil.disableScissor();

        currentX += font.getWidth(itemName, fontSize);

        int themeColor = Theme.getInstance().clientColor();
        int r = (themeColor >> 16) & 0xFF;
        int g = (themeColor >> 8) & 0xFF;
        int b = themeColor & 0xFF;
        int timeColor = ColorUtil.getColor(r, g, b, (int) (255 * textAnim));
        font.draw(matrix, timeText, timeX, textY, timeColor, fontSize);

        matrix.pop();
    }

    private void renderItemCooldownNew(MatrixStack matrix, float x, float y, float width, float height, Item item, CooldownEntry data, float fontSize, float iconSize, float finalScale, boolean isPreview, float radius, boolean alignRight, float rectScale, float iconScale, float pointWidth) {
        float padding = 6f * finalScale;
        String itemName = data.stack.getDisplayName().getString();
        float smoothAnim = easeOutCubic(rectScale);
        float textAnim = easeOutQuart(rectScale);

        if (rectScale > 0.01f) {
            RenderUtil.CustomStyled.draw(matrix, x, y, width, height * rectScale, Round.of(radius), blurEnabled, blurStrength);
            if (enableOutline) {
                int themeColor = Theme.getInstance().clientColor();
                int outlineColor = ColorUtil.interpolateColor(ColorUtil.getColor(255, 255, 255, 255), themeColor, 0.5f);
                float halfWidth = outlineWidth * finalScale / 2f;
                RenderUtil.Rounded.roundedOutline(matrix,
                        x - halfWidth,
                        y - halfWidth,
                        width + outlineWidth * finalScale,
                        (height * rectScale) + outlineWidth * finalScale,
                        outlineWidth * finalScale,
                        outlineColor,
                        Round.of(radius));
            }
        }

        matrix.push();
        matrix.translate((1 - smoothAnim) * 10f * finalScale, 0, 0);
        matrix.translate(x, y + (height * rectScale) / 2f, 0);
        matrix.scale(1f, 0.3f + (smoothAnim * 0.7f), 1f);
        matrix.translate(-x, -(y + (height * rectScale) / 2f), 0);

        float currentX = x + padding;

        if (showItemIcon && rectScale > 0.01f) {
            float itemRenderSize = iconSize * rectScale;
            float itemY = y + ((height * rectScale) - itemRenderSize) / 2f;

            GlStateManager.pushMatrix();
            GlStateManager.translatef(currentX + itemRenderSize / 2, itemY + itemRenderSize / 2, 0);
            GlStateManager.scalef((itemRenderSize / 16f) * iconScale, (itemRenderSize / 16f) * iconScale, 1f);
            mc.getItemRenderer().renderItemIntoGUI(data.stack, -8, -8);
            GlStateManager.popMatrix();
            currentX += itemRenderSize + 6f * finalScale;

            font.draw(matrix, POINT, currentX - 2.5f, y + (height * rectScale - fontSize) / 2f, ColorUtil.getColor(DOT_COLOR >> 16 & 0xFF, DOT_COLOR >> 8 & 0xFF, DOT_COLOR & 0xFF, (int) (((DOT_COLOR >> 24 & 0xFF) * textAnim))), fontSize);
            currentX += pointWidth;
        }

        float textY = y + ((height * rectScale) - fontSize) / 2f;
        String timeText = formatTime(data.progress, data.maxCooldown);
        float timeWidth = font.getWidth(timeText, fontSize);
        float timeX = x + width - timeWidth - padding;
        float nameMaxWidth = timeX - currentX - padding;

        int nameColor = ColorUtil.getColor(255, 255, 255, (int) (235 * textAnim));
        ScissorUtil.enableScissor(currentX, y, nameMaxWidth + 2, height * rectScale);
        font.draw(matrix, itemName, currentX, textY, nameColor, fontSize);
        ScissorUtil.disableScissor();

        currentX += font.getWidth(itemName, fontSize);

        int themeColor = Theme.getInstance().clientColor();
        int r = (themeColor >> 16) & 0xFF;
        int g = (themeColor >> 8) & 0xFF;
        int b = themeColor & 0xFF;
        int timeColor = ColorUtil.getColor(r, g, b, (int) (255 * textAnim));
        font.draw(matrix, timeText, timeX, textY, timeColor, fontSize);

        matrix.pop();
    }

    private void renderItemCooldown(MatrixStack matrix, float x, float y, float width, float height, Item item, CooldownEntry data, float fontSize, float iconSize, float finalScale, boolean isPreview, float rectScale, float iconScale, float pointWidth) {
        float padding = 6f * finalScale;
        String itemName = data.stack.getDisplayName().getString();
        float smoothAnim = easeOutCubic(rectScale);
        float textAnim = easeOutQuart(rectScale);

        matrix.push();
        matrix.translate((1 - smoothAnim) * 10f * finalScale, 0, 0);
        matrix.translate(x, y + (height * rectScale) / 2f, 0);
        matrix.scale(1f, 0.3f + (smoothAnim * 0.7f), 1f);
        matrix.translate(-x, -(y + (height * rectScale) / 2f), 0);

        float currentX = x + padding;

        if (showItemIcon && rectScale > 0.01f) {
            float itemRenderSize = iconSize * rectScale;
            float itemY = y + ((height * rectScale) - itemRenderSize) / 2f;

            GlStateManager.pushMatrix();
            GlStateManager.translatef(currentX + itemRenderSize / 2, itemY + itemRenderSize / 2, 0);
            GlStateManager.scalef((itemRenderSize / 16f) * iconScale, (itemRenderSize / 16f) * iconScale, 1f);
            mc.getItemRenderer().renderItemIntoGUI(data.stack, -8, -8);
            GlStateManager.popMatrix();
            currentX += itemRenderSize + 6f * finalScale;

            font.draw(matrix, POINT, currentX - 2.5f, y + (height * rectScale - fontSize) / 2f, ColorUtil.getColor(DOT_COLOR >> 16 & 0xFF, DOT_COLOR >> 8 & 0xFF, DOT_COLOR & 0xFF, (int) (((DOT_COLOR >> 24 & 0xFF) * textAnim))), fontSize);
            currentX += pointWidth;
        }

        float textY = y + ((height * rectScale) - fontSize) / 2f;
        String timeText = formatTime(data.progress, data.maxCooldown);
        float timeWidth = font.getWidth(timeText, fontSize);
        float timeX = x + width - timeWidth - padding;
        float nameMaxWidth = timeX - currentX - padding;

        int nameColor = ColorUtil.getColor(255, 255, 255, (int) (235 * textAnim));
        ScissorUtil.enableScissor(currentX, y, nameMaxWidth + 2, height * rectScale);
        font.draw(matrix, itemName, currentX, textY, nameColor, fontSize);
        ScissorUtil.disableScissor();

        currentX += font.getWidth(itemName, fontSize);

        int themeColor = Theme.getInstance().clientColor();
        int r = (themeColor >> 16) & 0xFF;
        int g = (themeColor >> 8) & 0xFF;
        int b = themeColor & 0xFF;
        int timeColor = ColorUtil.getColor(r, g, b, (int) (255 * textAnim));
        font.draw(matrix, timeText, timeX, textY, timeColor, fontSize);

        matrix.pop();
    }

    private float easeOutCubic(float t) {
        return 1 - (float) Math.pow(1 - t, 3);
    }

    private float easeOutQuart(float t) {
        return 1 - (float) Math.pow(1 - t, 4);
    }
}