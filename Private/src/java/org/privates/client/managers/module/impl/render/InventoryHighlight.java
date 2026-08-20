package org.privates.client.managers.module.impl.render;

import lombok.AccessLevel;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.mojang.blaze3d.systems.RenderSystem;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.render.Render2DEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.settings.impl.BooleanSetting;

import java.awt.*;

@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "InventoryHighlight", category = Category.RENDER)
public class InventoryHighlight extends Module {
    private final BooleanSetting goldenAppleSetting = new BooleanSetting(this, "Золотое Яблоко", true);
    private final BooleanSetting enchantedGoldenAppleSetting = new BooleanSetting(this, "Чарка", true);
    private final BooleanSetting potionsSetting = new BooleanSetting(this, "Бафы", true);
    private final BooleanSetting oldTrapSetting = new BooleanSetting(this, "Трапка Old", true);
    private final BooleanSetting newTrapSetting = new BooleanSetting(this, "Трапка New", true);
    private final BooleanSetting netherStarSetting = new BooleanSetting(this, "Стан", true);
    private final BooleanSetting swordSetting = new BooleanSetting(this, "Незеритовый меч", true);
    private final BooleanSetting shulkerBoxSetting = new BooleanSetting(this, "Шалкер", true);

    @EventHandler
    public void onRender(Render2DEvent event) {
        if (mc.player == null) return;
        MatrixStack matrixStack = event.getMatrix();
        for (int i = 0; i < 9; i++) {
            ItemStack itemStack = mc.player.inventory.getStackInSlot(i);

            if (shouldHighlight(itemStack)) {
                renderItemHighlight(matrixStack, i, itemStack);
            }
        }
    }

    private boolean shouldHighlight(ItemStack itemStack) {
        if (itemStack.isEmpty()) return false;
        Item item = itemStack.getItem();

        // Золотые яблоки: только если здоровье ниже 12
        if ((item == Items.GOLDEN_APPLE || item == Items.ENCHANTED_GOLDEN_APPLE)
                && goldenAppleSetting.getValue()) {
            if (mc.player.getHealth() < 12) {
                return true;
            }
        }

        if (potionsSetting.getValue() && isPotionOfInterest(itemStack)) {
            return true;
        }
        if (oldTrapSetting.getValue() && item == Items.PRISMARINE_SHARD) {
            return true;
        }
        if (newTrapSetting.getValue() && item == Items.POPPED_CHORUS_FRUIT) {
            return true;
        }
        if (netherStarSetting.getValue() && item == Items.NETHER_STAR) {
            return true;
        }
        if (swordSetting.getValue() && item == Items.NETHERITE_SWORD) {
            return true;
        }
        return shulkerBoxSetting.getValue() && isShulkerBox(item);
    }

    private boolean isShulkerBox(Item item) {
        return item == Items.SHULKER_BOX ||
                item == Items.BLACK_SHULKER_BOX ||
                item == Items.BLUE_SHULKER_BOX ||
                item == Items.BROWN_SHULKER_BOX ||
                item == Items.CYAN_SHULKER_BOX ||
                item == Items.GRAY_SHULKER_BOX ||
                item == Items.GREEN_SHULKER_BOX ||
                item == Items.LIGHT_BLUE_SHULKER_BOX ||
                item == Items.LIGHT_GRAY_SHULKER_BOX ||
                item == Items.LIME_SHULKER_BOX ||
                item == Items.MAGENTA_SHULKER_BOX ||
                item == Items.ORANGE_SHULKER_BOX ||
                item == Items.PINK_SHULKER_BOX ||
                item == Items.PURPLE_SHULKER_BOX ||
                item == Items.RED_SHULKER_BOX ||
                item == Items.WHITE_SHULKER_BOX ||
                item == Items.YELLOW_SHULKER_BOX;
    }

    private boolean isPotionOfInterest(ItemStack itemStack) {
        return itemStack.getItem() == Items.POTION ||
                itemStack.getItem() == Items.SPLASH_POTION ||
                itemStack.getItem() == Items.LINGERING_POTION;
    }

    private void renderItemHighlight(MatrixStack matrixStack, int slot, ItemStack itemStack) {
        Color themeColor = new Color(Theme.getInstance().clientColor());
        int x = mc.getMainWindow().getScaledWidth() / 2 - 90 + slot * 20;
        int y = mc.getMainWindow().getScaledHeight() - 16 - 3;
        float glowFactor = 0.5f + 0.5f * (float) Math.sin(System.currentTimeMillis() * 0.01);
        renderGlow(matrixStack, x, y, themeColor, glowFactor);
    }

    private void renderGlow(MatrixStack matrixStack, int x, int y, Color color, float glowFactor) {
        float glowMultiplier = Math.min(1f, glowFactor * 2f);

        float red = Math.min(1f, color.getRed() / 255f * glowMultiplier);
        float green = Math.min(1f, color.getGreen() / 255f * glowMultiplier);
        float blue = Math.min(1f, color.getBlue() / 255f * glowMultiplier);
        float alpha = 0.5f * glowMultiplier;

        int padding = 1;

        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();

        AbstractGui.fill(matrixStack,
                x + padding,
                y + padding,
                x + 19 - padding,
                y + 19 - padding,
                new Color(red, green, blue, alpha).getRGB()
        );

        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }
}