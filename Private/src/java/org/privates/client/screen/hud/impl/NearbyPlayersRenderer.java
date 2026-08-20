package org.privates.client.screen.hud.impl;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.mojang.blaze3d.platform.GlStateManager;
import net.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.opengl.GL11;
import org.privates.client.managers.component.impl.drag.DragComponent;
import org.privates.client.managers.events.render.Render2DEvent;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.impl.combat.AuraNew;
import org.privates.client.managers.module.settings.impl.DragSetting;
import org.privates.client.screen.hud.AbstractHud;
import org.privates.client.screen.hud.IConfigurableHud;
import org.privates.client.screen.hud.IRenderer;
import org.privates.client.utils.animation.Animation;
import org.privates.client.utils.animation.util.Easings;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.draw.ScissorUtil;
import org.privates.client.utils.render.font.Fonts;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.StreamSupport;

public class NearbyPlayersRenderer extends AbstractHud implements IRenderer, IConfigurableHud {
    private final DragSetting drag;
    private final Map<UUID, Float> animatedHealthBars = new HashMap<>();
    private final Map<UUID, Animation> playerAnimations = new HashMap<>();
    private final Map<UUID, List<ItemStack>> cachedArmorItems = new HashMap<>();

    public float scale = 0.90f;
    public float titleDarkness = 0.0f;
    public boolean showTitle = true;
    public boolean showTitleIcon = true;
    public boolean showDistance = true;
    public boolean showHealth = true;
    public boolean showHealthBar = true;
    public boolean showArmor = true;
    public boolean showTrackedItems = true;
    public boolean dynamicRects = false;
    public boolean multiColumn = false;
    public boolean compactMode = false;
    public int maxPlayersPerColumn = 5;
    public float columnGap = 5.0f;

    @Getter
    @Setter
    private boolean enableOutline = false;
    @Getter
    @Setter
    private float outlineWidth = 1.0f;

    private ItemRenderMode itemRenderMode = ItemRenderMode.NEW;
    private PlayerRenderMode playerRenderMode = PlayerRenderMode.FULL;

    private float animatedTitleWidth;
    private float animatedTitleHeight;
    private float animatedContainerWidth;
    private float animatedContainerHeight;

    public enum ItemRenderMode {
        NEW, OLD, STYLED
    }

    public enum PlayerRenderMode {
        FULL, COMPACT, MINIMAL
    }

    public NearbyPlayersRenderer(DragSetting drag) {
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

                new DragComponent.HudSetting("Show Title", DragComponent.SettingType.BOOLEAN,
                        () -> this.showTitle ? 1f : 0f,
                        v -> this.showTitle = v > 0.5f),

                new DragComponent.HudSetting("Title Icon", DragComponent.SettingType.BOOLEAN,
                        () -> this.showTitleIcon ? 1f : 0f,
                        v -> this.showTitleIcon = v > 0.5f),

                new DragComponent.HudSetting("Show Distance", DragComponent.SettingType.BOOLEAN,
                        () -> this.showDistance ? 1f : 0f,
                        v -> this.showDistance = v > 0.5f),

                new DragComponent.HudSetting("Show Health", DragComponent.SettingType.BOOLEAN,
                        () -> this.showHealth ? 1f : 0f,
                        v -> this.showHealth = v > 0.5f),

                new DragComponent.HudSetting("Show Health Bar", DragComponent.SettingType.BOOLEAN,
                        () -> this.showHealthBar ? 1f : 0f,
                        v -> this.showHealthBar = v > 0.5f),

                new DragComponent.HudSetting("Show Armor", DragComponent.SettingType.BOOLEAN,
                        () -> this.showArmor ? 1f : 0f,
                        v -> this.showArmor = v > 0.5f),

                new DragComponent.HudSetting("Show Tracked Items", DragComponent.SettingType.BOOLEAN,
                        () -> this.showTrackedItems ? 1f : 0f,
                        v -> this.showTrackedItems = v > 0.5f),

                new DragComponent.HudSetting("Item Render: NEW", DragComponent.SettingType.BOOLEAN,
                        () -> itemRenderMode == ItemRenderMode.NEW ? 1f : 0f,
                        v -> {
                            if (v > 0.5f) itemRenderMode = ItemRenderMode.NEW;
                        }),

                new DragComponent.HudSetting("Item Render: OLD", DragComponent.SettingType.BOOLEAN,
                        () -> itemRenderMode == ItemRenderMode.OLD ? 1f : 0f,
                        v -> {
                            if (v > 0.5f) itemRenderMode = ItemRenderMode.OLD;
                        }),

                new DragComponent.HudSetting("Item Render: STYLED", DragComponent.SettingType.BOOLEAN,
                        () -> itemRenderMode == ItemRenderMode.STYLED ? 1f : 0f,
                        v -> {
                            if (v > 0.5f) itemRenderMode = ItemRenderMode.STYLED;
                        }),

                new DragComponent.HudSetting("Player Render: FULL", DragComponent.SettingType.BOOLEAN,
                        () -> playerRenderMode == PlayerRenderMode.FULL ? 1f : 0f,
                        v -> {
                            if (v > 0.5f) playerRenderMode = PlayerRenderMode.FULL;
                        }),

                new DragComponent.HudSetting("Player Render: COMPACT", DragComponent.SettingType.BOOLEAN,
                        () -> playerRenderMode == PlayerRenderMode.COMPACT ? 1f : 0f,
                        v -> {
                            if (v > 0.5f) playerRenderMode = PlayerRenderMode.COMPACT;
                        }),

                new DragComponent.HudSetting("Player Render: MINIMAL", DragComponent.SettingType.BOOLEAN,
                        () -> playerRenderMode == PlayerRenderMode.MINIMAL ? 1f : 0f,
                        v -> {
                            if (v > 0.5f) playerRenderMode = PlayerRenderMode.MINIMAL;
                        }),

                new DragComponent.HudSetting("Dynamic Rects", DragComponent.SettingType.BOOLEAN,
                        () -> this.dynamicRects ? 1f : 0f,
                        v -> this.dynamicRects = v > 0.5f),

                new DragComponent.HudSetting("Multi Column", DragComponent.SettingType.BOOLEAN,
                        () -> this.multiColumn ? 1f : 0f,
                        v -> this.multiColumn = v > 0.5f),

                new DragComponent.HudSetting("Compact Mode", DragComponent.SettingType.BOOLEAN,
                        () -> this.compactMode ? 1f : 0f,
                        v -> this.compactMode = v > 0.5f),

                new DragComponent.HudSetting("Max Per Column", DragComponent.SettingType.SLIDER,
                        () -> (float) this.maxPlayersPerColumn,
                        v -> this.maxPlayersPerColumn = (int) Math.floor(v),
                        1f, 10f, 1f),

                new DragComponent.HudSetting("Column Gap", DragComponent.SettingType.SLIDER,
                        () -> this.columnGap,
                        v -> this.columnGap = v,
                        0f, 20f, 1f),

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

        if (width >= 3840 || height >= 2160) {
            return 1f;
        } else if (width >= 2560 || height >= 1440) {
            return 1.0f;
        } else if (width >= 1920 || height >= 1080) {
            return 1f;
        } else {
            return 1f;
        }
    }

    @Override
    public void render(Render2DEvent event) {
        boolean isPreview = mc.currentScreen instanceof ChatScreen;
        List<PlayerEntity> nearbyPlayers = getNearbyPlayers(isPreview);

        if (!isPreview) {
            playerAnimations.keySet().removeIf(uuid ->
                    nearbyPlayers.stream().noneMatch(p -> p.getUniqueID().equals(uuid))
            );

            for (PlayerEntity player : nearbyPlayers) {
                playerAnimations.computeIfAbsent(player.getUniqueID(), k -> new Animation());
                cachedArmorItems.computeIfAbsent(player.getUniqueID(), k -> new ArrayList<>());
            }

            playerAnimations.values().forEach(Animation::update);
        }

        float resScale = getResolutionScale();
        float finalScale = resScale * scale;
        MatrixStack matrix = event.getMatrix();
        float x = drag.position.x;
        float y = drag.position.y;

        String title = "Nearby Players";
        float padding = 6f * finalScale;
        float fontSize = 7f * finalScale;
        float containerRadius = 6f * finalScale;

        float titleIconSize = 10f * finalScale;
        float titleTextWidth = font.getWidth(title, fontSize);
        float titleIconTextGap = 7.5f * finalScale;
        float countBoxWidth = font.getWidth(String.valueOf(nearbyPlayers.size()), 6f * finalScale) + 8f * finalScale;

        float targetTitleWidth = padding + (showTitleIcon ? (titleIconSize + 6f * finalScale) : 0f) + titleTextWidth + titleIconTextGap + countBoxWidth + padding;
        float targetTitleHeight = showTitle ? 18f * finalScale : 0f;

        animatedTitleWidth = Mathf.fast(animatedTitleWidth, targetTitleWidth, 14);
        animatedTitleHeight = Mathf.fast(animatedTitleHeight, targetTitleHeight, 14);

        int columns = multiColumn ? (int) Math.ceil((double) nearbyPlayers.size() / maxPlayersPerColumn) : 1;
        float maxPlayerWidth = 0f;
        float totalHeight = targetTitleHeight;

        List<List<PlayerEntity>> columnsPlayers = new ArrayList<>();
        for (int i = 0; i < columns; i++) {
            int startIdx = i * maxPlayersPerColumn;
            int endIdx = Math.min(startIdx + maxPlayersPerColumn, nearbyPlayers.size());
            columnsPlayers.add(nearbyPlayers.subList(startIdx, endIdx));
        }

        for (List<PlayerEntity> columnPlayers : columnsPlayers) {
            float columnHeight = 0f;
            for (PlayerEntity player : columnPlayers) {
                float anim = isPreview ? 1.0f : (float) playerAnimations.get(player.getUniqueID()).getValue();
                float rowHeight = calculateRowHeight(finalScale);
                float playerWidth = calculateRequiredWidth(player, rowHeight, finalScale, isPreview);
                maxPlayerWidth = Math.max(maxPlayerWidth, playerWidth);
                columnHeight += rowHeight * anim;
                if (dynamicRects && columnPlayers.indexOf(player) < columnPlayers.size() - 1) {
                    columnHeight += 3f * finalScale;
                }
            }
            totalHeight = Math.max(totalHeight, targetTitleHeight + (showTitle && dynamicRects ? 3f * finalScale : 0f) + columnHeight);
        }

        float totalWidth = maxPlayerWidth * columns + (columnGap * finalScale * (columns - 1));
        float targetContainerWidth = Math.max(animatedTitleWidth, totalWidth);
        float targetContainerHeight = totalHeight;

        animatedContainerWidth = Mathf.fast(animatedContainerWidth, targetContainerWidth, 14);
        animatedContainerHeight = Mathf.fast(animatedContainerHeight, targetContainerHeight, 14);

        if (x == 0 && y == 0) {
            drag.position.set(10, 200);
            x = 10;
            y = 200;
        }

        drag.size.set(animatedContainerWidth, animatedContainerHeight);

        float currentY = y;

        if (!dynamicRects && !multiColumn) {
            RenderUtil.CustomStyled.draw(matrix, x, y, animatedContainerWidth, animatedContainerHeight, Round.of(containerRadius), true, 10f);
        }

        if (showTitle) {
            if (dynamicRects) {
                RenderUtil.CustomStyled.draw(matrix, x, currentY, animatedTitleWidth, animatedTitleHeight, Round.of(containerRadius), true, 10f);
                ScissorUtil.enableScissor(x, currentY, animatedTitleWidth, animatedTitleHeight);
                RenderUtil.Rounded.smooth(matrix, x, currentY, animatedTitleWidth, animatedTitleHeight,
                        ColorUtil.replAlpha(new Color(0xFF000000, true).getRGB(), titleDarkness), Round.of(containerRadius));
                ScissorUtil.disableScissor();
                renderTitleContent(matrix, x, currentY, animatedTitleWidth, animatedTitleHeight, title, fontSize, titleIconSize, finalScale, nearbyPlayers.size(), titleIconTextGap);

                if (enableOutline) {
                    int themeColor = Theme.getInstance().clientColor();
                    int outlineColor = ColorUtil.interpolateColor(ColorUtil.getColor(255, 255, 255, 255), themeColor, 0.5f);
                    RenderUtil.Rounded.roundedOutline(matrix,
                            x,
                            currentY,
                            animatedTitleWidth,
                            animatedTitleHeight,
                            outlineWidth * finalScale,
                            outlineColor,
                            Round.of(containerRadius));
                }

                currentY += animatedTitleHeight + 3f * finalScale;
            } else {
                ScissorUtil.enableScissor(x, currentY, animatedContainerWidth, animatedTitleHeight);
                RenderUtil.Rounded.smooth(matrix, x, currentY, animatedContainerWidth, animatedTitleHeight,
                        ColorUtil.replAlpha(new Color(0xFF000000, true).getRGB(), titleDarkness), Round.of(containerRadius, containerRadius, 0, 0));
                ScissorUtil.disableScissor();
                renderTitleContent(matrix, x, currentY, animatedContainerWidth, animatedTitleHeight, title, fontSize, titleIconSize, finalScale, nearbyPlayers.size(), titleIconTextGap);

                RenderUtil.Rounded.roundedRect(matrix, x, currentY + animatedTitleHeight, animatedContainerWidth, 0.5f * finalScale,
                        ColorUtil.getColor(255, 255, 255, 20), Round.of(0));

                currentY += animatedTitleHeight;
            }
        }

        float startY = currentY;
        float columnX = x;

        for (int col = 0; col < columnsPlayers.size(); col++) {
            List<PlayerEntity> columnPlayers = columnsPlayers.get(col);
            currentY = startY;

            for (int i = 0; i < columnPlayers.size(); i++) {
                PlayerEntity player = columnPlayers.get(i);
                float anim = isPreview ? 1.0f : (float) playerAnimations.get(player.getUniqueID()).getValue();
                if (anim <= 0.01f && !isPreview) continue;

                float rowHeight = calculateRowHeight(finalScale);
                float playerWidth = calculateRequiredWidth(player, rowHeight, finalScale, isPreview);
                float animatedHeight = rowHeight * anim;

                if (dynamicRects || multiColumn) {
                    renderPlayerEntry(matrix, player, columnX, currentY, playerWidth, animatedHeight, isPreview, i, finalScale);
                    currentY += animatedHeight + (dynamicRects ? 3f * finalScale : 0f);
                } else {
                    renderPlayerEntry(matrix, player, columnX, currentY, animatedContainerWidth, animatedHeight, isPreview, i, finalScale);
                    currentY += animatedHeight;
                }
            }

            columnX += maxPlayerWidth + columnGap * finalScale;
        }

        if (enableOutline && !dynamicRects && !multiColumn) {
            int themeColor = Theme.getInstance().clientColor();
            int outlineColor = ColorUtil.interpolateColor(ColorUtil.getColor(255, 255, 255, 255), themeColor, 0.5f);
            RenderUtil.Rounded.roundedOutline(matrix,
                    x,
                    y,
                    animatedContainerWidth,
                    animatedContainerHeight,
                    outlineWidth * finalScale,
                    outlineColor,
                    Round.of(containerRadius));
        }
    }

    private void renderTitleContent(MatrixStack matrix, float x, float y, float width, float height, String title, float fontSize, float iconSize, float finalScale, int playerCount, float titleIconTextGap) {
        float padding = 6f * finalScale;
        float offsetY = 0.25f * finalScale;
        int hud = Theme.getInstance().clientColor();

        float currentX = x + padding;

        if (showTitleIcon) {
            float iconY = y + (height - iconSize) / 2f - offsetY;
            Fonts.icon_new.draw(matrix, "g", currentX, iconY, hud, iconSize);
            currentX += iconSize + 6f * finalScale;
        }

        float textY = y + (height - fontSize) / 2f - offsetY;
        font.draw(matrix, title, currentX, textY, -1, fontSize);
        currentX += font.getWidth(title, fontSize);

        currentX += titleIconTextGap;
        float countFontSize = 6f * finalScale;
        String countText = String.valueOf(playerCount);
        float countWidth = font.getWidth(countText, countFontSize);
        float countHeight = countFontSize;
        float countRectX = currentX;
        float countRectY = y + (height - countHeight) / 2f;
        float countRectWidth = countWidth + 8f * finalScale;
        float countRectHeight = countHeight + 4f * finalScale;
        float countRectRadius = 3f * finalScale;
        RenderUtil.CustomStyled.draw(matrix, countRectX - 0.05f, countRectY - 0.75f, countRectWidth - 1, countRectHeight - 1, Round.of(countRectRadius), true, 10f * 0.6f);

        font.draw(matrix, countText, countRectX + 3.25f * finalScale, countRectY - 0.45f + finalScale, hud, countFontSize);
    }

    private float calculateRowHeight(float finalScale) {
        return switch (playerRenderMode) {
            case FULL -> 30f * finalScale + 10f * finalScale;
            case COMPACT -> 25f * finalScale + 8f * finalScale;
            case MINIMAL -> 20f * finalScale + 6f * finalScale;
        };
    }

    private float calculateRequiredWidth(PlayerEntity player, float height, float finalScale, boolean isPreview) {
        float padding = 4f * finalScale;
        float headSize = height - 10f * finalScale;
        float leftWidth = headSize + padding;
        float rightWidth = 0f;

        List<ItemStack> armorItems = getPrioritizedArmorItems(player, isPreview);
        int nonEmptyCount = (int) armorItems.stream().filter(stack -> !stack.isEmpty()).count();

        if (compactMode) {
            return padding + leftWidth + 80f * finalScale + padding;
        }

        if (showArmor || showTrackedItems) {
            switch (itemRenderMode) {
                case NEW -> rightWidth = 0f;
                case OLD -> {
                    float itemSize = 10f * finalScale;
                    float dynamicSpacing = finalScale;
                    rightWidth = nonEmptyCount * itemSize + Math.max(0, nonEmptyCount - 1) * dynamicSpacing;
                }
                case STYLED -> rightWidth = headSize;
            }
        }

        float circleRadius = showHealthBar ? 15f * finalScale : 0f;
        float circleDiameter = showHealthBar ? circleRadius * 2f : 0f;

        float centerMinWidth = 65f * finalScale;
        return padding + leftWidth + centerMinWidth + circleDiameter + rightWidth + padding;
    }

    private List<ItemStack> getPrioritizedArmorItems(PlayerEntity player, boolean isPreview) {
        if (isPreview) {
            ItemStack[] previewItems = {
                    new ItemStack(Items.NETHERITE_HELMET),
                    new ItemStack(Items.NETHERITE_CHESTPLATE),
                    new ItemStack(Items.NETHERITE_LEGGINGS),
                    new ItemStack(Items.NETHERITE_BOOTS),
                    new ItemStack(Items.NETHERITE_SWORD),
                    new ItemStack(Items.SHIELD)
            };
            return Arrays.asList(previewItems);
        }

        UUID uuid = player.getUniqueID();
        List<ItemStack> cached = cachedArmorItems.get(uuid);
        if (cached != null && !cached.isEmpty()) {
            return cached;
        }

        List<ItemStack> items = new ArrayList<>();
        if (player instanceof AbstractClientPlayerEntity acp) {
            items.add(acp.getItemStackFromSlot(EquipmentSlotType.HEAD));
            items.add(acp.getItemStackFromSlot(EquipmentSlotType.CHEST));
            items.add(acp.getItemStackFromSlot(EquipmentSlotType.LEGS));
            items.add(acp.getItemStackFromSlot(EquipmentSlotType.FEET));
            items.add(acp.getHeldItemMainhand());
            items.add(acp.getHeldItemOffhand());
        }

        items.sort((a, b) -> Integer.compare(getItemPriority(b), getItemPriority(a)));
        cachedArmorItems.put(uuid, items);
        return items;
    }

    private int getItemPriority(ItemStack stack) {
        if (stack.isEmpty()) return -1;
        Item item = stack.getItem();
        int basePriority = 0;
        int enchantLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, stack);

        if (item instanceof ArmorItem armor) {
            IArmorMaterial material = armor.getArmorMaterial();
            if (material == ArmorMaterial.NETHERITE) basePriority = 5;
            else if (material == ArmorMaterial.DIAMOND) basePriority = 4;
            else if (material == ArmorMaterial.IRON) basePriority = 3;
            else if (material == ArmorMaterial.GOLD) basePriority = 2;
            else if (material == ArmorMaterial.CHAIN) basePriority = 1;
            else if (material == ArmorMaterial.LEATHER) basePriority = 0;
        } else if (isNetheriteWeapon(item)) {
            basePriority = 5;
        } else if (isDiamondWeapon(item)) {
            basePriority = 4;
        } else if (isIronWeapon(item)) {
            basePriority = 3;
        } else if (isGoldWeapon(item)) {
            basePriority = 2;
        } else if (item instanceof SwordItem) {
            basePriority = 1;
        }

        return basePriority + enchantLevel;
    }

    private boolean isNetheriteWeapon(Item item) {
        return item == Items.NETHERITE_SWORD || item == Items.NETHERITE_AXE || item == Items.NETHERITE_HOE ||
                item == Items.NETHERITE_SHOVEL || item == Items.NETHERITE_PICKAXE;
    }

    private boolean isDiamondWeapon(Item item) {
        return item == Items.DIAMOND_SWORD || item == Items.DIAMOND_AXE || item == Items.DIAMOND_HOE ||
                item == Items.DIAMOND_SHOVEL || item == Items.DIAMOND_PICKAXE;
    }

    private boolean isIronWeapon(Item item) {
        return item == Items.IRON_SWORD || item == Items.IRON_AXE || item == Items.IRON_HOE ||
                item == Items.IRON_SHOVEL || item == Items.IRON_PICKAXE;
    }

    private boolean isGoldWeapon(Item item) {
        return item == Items.GOLDEN_SWORD || item == Items.GOLDEN_AXE || item == Items.GOLDEN_HOE ||
                item == Items.GOLDEN_SHOVEL || item == Items.GOLDEN_PICKAXE;
    }

    private void renderPlayerEntry(MatrixStack matrix, PlayerEntity player, float x, float y, float width, float height, boolean isPreview, int index, float finalScale) {
        float padding = 5f * finalScale;
        float containerRadius = 6f * finalScale;

        if (dynamicRects || multiColumn) {
            RenderUtil.CustomStyled.draw(matrix, x, y - 2, width, height, Round.of(containerRadius), true, 10f);

            if (enableOutline) {
                int themeColor = Theme.getInstance().clientColor();
                int outlineColor = ColorUtil.interpolateColor(ColorUtil.getColor(255, 255, 255, 255), themeColor, 0.5f);
                RenderUtil.Rounded.roundedOutline(matrix,
                        x,
                        y - 2,
                        width,
                        height,
                        outlineWidth * finalScale,
                        outlineColor,
                        Round.of(containerRadius));
            }
        }

        float headSize = height - 10f * finalScale;
        float headX = x + padding;
        float headY = y + (height - headSize) / 2f;

        if (player instanceof AbstractClientPlayerEntity) {
            drawHead(matrix, (AbstractClientPlayerEntity) player, headX, headY + 2.3f, headSize, finalScale);
        }

        if (compactMode) {
            renderCompactMode(matrix, player, x, y, width, height, headSize, headX, finalScale, isPreview);
        } else {
            renderFullMode(matrix, player, x, y, width, height, headSize, headX, padding, finalScale, isPreview, index);
        }
    }

    private void renderCompactMode(MatrixStack matrix, PlayerEntity player, float x, float y, float width, float height, float headSize, float headX, float finalScale, boolean isPreview) {
        float padding = 5f * finalScale;
        float infoStartX = headX + headSize + padding;
        float infoFontSize = 7f * finalScale;

        String playerName = player.getName().getString();
        font.draw(matrix, playerName, infoStartX, y + 4f * finalScale, -1, infoFontSize);

        if (showArmor || showTrackedItems) {
            float itemY = y + height / 2f - 4f * finalScale;
            List<ItemStack> armorItems = getPrioritizedArmorItems(player, isPreview);
            float itemSize = 8f * finalScale;
            float itemSpacing = finalScale;
            float currentItemX = infoStartX;

            for (int i = 0; i < Math.min(4, armorItems.size()); i++) {
                ItemStack stack = armorItems.get(i);
                if (!stack.isEmpty()) {
                    renderItem(matrix, stack, currentItemX, itemY, finalScale * 0.8f);
                    currentItemX += itemSize + itemSpacing;
                }
            }
        }
    }

    private void renderFullMode(MatrixStack matrix, PlayerEntity player, float x, float y, float width, float height, float headSize, float headX, float padding, float finalScale, boolean isPreview, int index) {
        float circleRadius = showHealthBar ? 15f * finalScale : 0f;
        float circleX = x + width - circleRadius - padding * 2;
        float circleY = y + height / 2f;

        float centerStartX = headX + headSize + padding;
        float centerWidth;

        List<ItemStack> armorItems = getPrioritizedArmorItems(player, isPreview);
        int nonEmptyCount = (int) armorItems.stream().filter(stack -> !stack.isEmpty()).count();
        float itemSize = 10f * finalScale;
        float dynamicSpacing = finalScale;
        float itemsWidth = nonEmptyCount * itemSize + Math.max(0, nonEmptyCount - 1) * dynamicSpacing;

        if (showArmor || showTrackedItems) {
            switch (itemRenderMode) {
                case NEW -> centerWidth = circleX - centerStartX - padding * 2;
                case OLD -> centerWidth = circleX - centerStartX - itemsWidth - padding * 2;
                case STYLED -> {
                    float styledWidth = headSize;
                    centerWidth = circleX - centerStartX - styledWidth - padding * 2;
                }
                default -> centerWidth = circleX - centerStartX - padding * 2;
            }
        } else {
            centerWidth = circleX - centerStartX - padding * 2;
        }

        renderCenterInfo(matrix, player, centerStartX, y, centerWidth, height, finalScale);

        if (showHealthBar) {
            renderHealthCircle(matrix, player, circleX, circleY, circleRadius, finalScale);
        }

        if (showArmor || showTrackedItems) {
            switch (itemRenderMode) {
                case NEW ->
                        renderItemsNew(matrix, player, circleX + circleRadius + padding, y, height, isPreview, index, finalScale);
                case OLD ->
                        renderItemsOld(matrix, player, circleX - padding, y, height, isPreview, index, finalScale, armorItems, itemsWidth);
                case STYLED ->
                        renderItemsStyled(matrix, player, circleX - padding - headSize, y + (height - headSize) / 2f, headSize, isPreview, index, finalScale);
            }
        }
    }

    private void renderCenterInfo(MatrixStack matrix, PlayerEntity player, float x, float y, float width, float height, float finalScale) {
        float infoFontSize = 7f * finalScale;
        float subInfoFontSize = 6f * finalScale;

        String playerName = player.getName().getString();
        float nameWidth = font.getWidth(playerName, infoFontSize);

        float totalTextHeight = infoFontSize;
        if (showDistance) totalTextHeight += subInfoFontSize + 2f * finalScale;
        if (showHealth) totalTextHeight += subInfoFontSize + 2f * finalScale;

        float startY = y + (height - totalTextHeight) / 2f;

        float nameX = x + (width - nameWidth) / 2f;
        font.draw(matrix, playerName, nameX, startY, -1, infoFontSize);
        startY += infoFontSize + 2f * finalScale;

        if (showDistance) {
            String distanceText = (int) mc.player.getDistance(player) + "m";
            float distWidth = font.getWidth(distanceText, subInfoFontSize);
            float distX = x + (width - distWidth) / 2f;
            font.draw(matrix, distanceText, distX, startY, ColorUtil.getColor(180, 180, 180), subInfoFontSize);
            startY += subInfoFontSize + 2f * finalScale;
        }

        if (showHealth) {
            float currentHealth = player.getHealth() + player.getAbsorptionAmount();
            String healthText = String.format("%.1f HP", currentHealth);
            float healthWidth = font.getWidth(healthText, subInfoFontSize);
            float healthX = x + (width - healthWidth) / 2f;
            font.draw(matrix, healthText, healthX, startY, ColorUtil.getColor(255, 255, 255), subInfoFontSize);
        }
    }

    private void renderHealthCircle(MatrixStack matrix, PlayerEntity player, float x, float y, float radius, float finalScale) {
        float maxDisplayHealth = 20f;
        float currentHealth = player.getHealth() + player.getAbsorptionAmount();
        float targetHealthPercent = Mathf.clamp(currentHealth / maxDisplayHealth, 0, 1);

        UUID playerId = player.getUniqueID();
        float currentAnimatedPercent = animatedHealthBars.getOrDefault(playerId, targetHealthPercent);
        currentAnimatedPercent = Mathf.fast(currentAnimatedPercent, targetHealthPercent, 8);
        animatedHealthBars.put(playerId, currentAnimatedPercent);

        int start = 90;
        int end = (int) (start + (360 * currentAnimatedPercent));

        float bgWidth = 3f * finalScale;
        drawClientCircle(x + 5.5f, y - 1, radius - 3, 0, 360, bgWidth, Theme.getInstance().clientColor(), 0.2f);

        float healthWidth = 4f * finalScale;
        drawClientCircle(x + 5.5f, y - 1, radius - 3, start, end, healthWidth, Theme.getInstance().clientColor(), 1.0f);

        int healthPercentage = (int) (currentAnimatedPercent * 100);
        String healthText = healthPercentage + "%";
        float fontSize = (5f + 0.6f) * finalScale;
        float textWidth = font.getWidth(healthText, fontSize);
        float textX = x - textWidth / 2f;
        float textY = y - fontSize + 10 / 2f;
        font.draw(matrix, healthText, textX + 5.3f, textY - 3.5f, -1, fontSize);
    }

    private void drawClientCircle(final float x, final float y, final float radius, final float start, final int end, float width, int color, float alpha) {
        float[] rgba = ColorUtil.getRGBAf(color);

        GlStateManager.enableBlend();
        GlStateManager.disableTexture();
        GlStateManager.blendFuncSeparate(770, 771, 1, 0);
        GL11.glEnable(2848);
        GL11.glLineWidth(width);
        GL11.glBegin(3);

        RenderSystem.color4f(rgba[0], rgba[1], rgba[2], alpha);

        for (float i = (float) end; i >= start; i -= 2.0f) {
            GL11.glVertex2f((float) (x + Math.cos(i * Math.PI / 180.0) * (radius * 1.001f)),
                    (float) (y + Math.sin(i * Math.PI / 180.0) * (radius * 1.001f)));
        }
        GL11.glEnd();
        GL11.glDisable(2848);
        GlStateManager.enableTexture();
        GlStateManager.disableBlend();
    }

    private void renderItemsNew(MatrixStack matrix, PlayerEntity player, float rectRightEdge, float y, float height, boolean isPreview, int index, float finalScale) {
        List<ItemStack> armorItems = getPrioritizedArmorItems(player, isPreview);

        float itemSize = 10f * finalScale;
        float gap = 6f * finalScale;
        float startX = rectRightEdge + gap;

        float topRowY = y + (height / 2f) - itemSize - finalScale;

        float currentX = startX;
        for (ItemStack stack : armorItems) {
            if (!stack.isEmpty()) {
                renderItem(matrix, stack, currentX, topRowY, finalScale);
                currentX += itemSize + finalScale;
            }
        }
    }

    private void renderItemsOld(MatrixStack matrix, PlayerEntity player, float rightX, float y, float height, boolean isPreview, int index, float finalScale, List<ItemStack> armorItems, float itemsWidth) {
        List<ItemStack> nonEmptyItems = armorItems.stream().filter(stack -> !stack.isEmpty()).collect(java.util.stream.Collectors.toList());

        float itemSize = 10f * finalScale;
        float dynamicSpacing = finalScale;

        float topRowY = y + (height / 2f) - itemSize - finalScale;
        float startRenderX = rightX - itemsWidth;

        float currentX = startRenderX;
        for (ItemStack stack : nonEmptyItems) {
            renderItem(matrix, stack, currentX, topRowY, finalScale);
            currentX += itemSize + dynamicSpacing;
        }
    }

    private void renderItemsStyled(MatrixStack matrix, PlayerEntity player, float boxX, float boxY, float boxSize, boolean isPreview, int index, float finalScale) {
        List<ItemStack> items = getPrioritizedArmorItems(player, isPreview).stream().filter(stack -> !stack.isEmpty()).collect(java.util.stream.Collectors.toList());

        if (items.isEmpty()) return;

        int gridSize = 2;
        float itemSize = boxSize / gridSize - 2f * finalScale;
        float padding = finalScale;

        for (int i = 0; i < Math.min(items.size(), 4); i++) {
            int row = i / gridSize;
            int col = i % gridSize;
            float itemX = boxX + col * (itemSize + padding) + padding - 5;
            float itemY = boxY + row * (itemSize + padding) + padding;
            renderItem(matrix, items.get(i), itemX, itemY, finalScale);
        }
    }

    private void renderItem(MatrixStack matrix, ItemStack itemStack, float x, float y, float scale) {
        GlStateManager.pushMatrix();
        GlStateManager.translatef(x, y, 1);
        GlStateManager.scalef(0.595F * scale, 0.595F * scale, 0.595F * scale);
        mc.getItemRenderer().renderItemIntoGUI(itemStack, 0, 0);
        GlStateManager.popMatrix();
    }

    private void drawHead(MatrixStack matrix, final AbstractClientPlayerEntity player, final float x, final float y, final float size, float scale) {
        float hurtPC = (float) Math.sin(player.hurtTime * (18F * Math.PI / 180F));
        int headColor = ColorUtil.overCol(ColorUtil.WHITE, ColorUtil.RED, hurtPC);

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.alphaFunc(GL11.GL_GREATER, 0.0F);
        RenderSystem.enableTexture();

        mc.getTextureManager().bindTexture(player.getLocationSkin());

        float[] rgba = ColorUtil.getRGBAf(headColor);
        RenderSystem.color4f(rgba[0], rgba[1], rgba[2], rgba[3]);

        RenderSystem.pushMatrix();
        RenderSystem.multMatrix(matrix.getLast().getMatrix());

        float pivotX = x + size / 2.0F;
        float pivotY = y + size / 2.0F;

        RenderSystem.translatef(pivotX, pivotY, 0);
        RenderSystem.rotatef(180, 0, 0, 1);
        RenderSystem.translatef(-pivotX, -pivotY, 0);

        RenderUtil.Texture.roundedHead(player.getLocationSkin(), x, y, size, size, size / 7F * scale, rgba[3], 0);

        RenderSystem.popMatrix();

        RenderSystem.color4f(1F, 1F, 1F, 1F);
        RenderSystem.disableBlend();
    }

    private List<PlayerEntity> getNearbyPlayers(boolean isPreview) {
        if (isPreview) {
            return Collections.singletonList(mc.player);
        }

        AuraNew auraNew = AuraNew.getInstance();
        List<PlayerEntity> players = new ArrayList<>();

        if (mc.world != null) {
            players = StreamSupport.stream(mc.world.getPlayers().spliterator(), false)
                    .filter(player -> player != mc.player)
                    .filter(player -> mc.player.getDistance(player) <= 100)
                    .sorted(Comparator.comparingDouble(p -> mc.player.getDistance(p)))
                    .limit(multiColumn ? maxPlayersPerColumn * 3L : 5)
                    .collect(java.util.stream.Collectors.toList());
        }

        if (auraNew.isEnabled() && auraNew.target() instanceof PlayerEntity auraTarget) {
            if (!players.contains(auraTarget)) {
                players.add(0, auraTarget);
            }
        }

        for (PlayerEntity player : players) {
            Animation anim = playerAnimations.computeIfAbsent(player.getUniqueID(), k -> new Animation());
            anim.run(1.0, 0.3, Easings.CUBIC_OUT, true);
            cachedArmorItems.remove(player.getUniqueID());
        }

        return players;
    }
}