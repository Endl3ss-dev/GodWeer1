package org.privates.client.managers.module.impl.player;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.privates.client.api.annotations.Category2;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BindSetting;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.MultiBooleanSetting;
import org.privates.client.utils.other.Instance;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Category2
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "InventoryCleaner", category = Category.PLAYER, description = "Отчистка мусора с инвентаря при его открытии")
public class InventoryCleaner extends Module {

    public static InventoryCleaner getInstance() {
        return Instance.get(InventoryCleaner.class);
    }

    private final MultiBooleanSetting cleanSettings = new MultiBooleanSetting(this, "Удалять",
            BooleanSetting.of("Растения", true),
            BooleanSetting.of("Блоки", false),
            BooleanSetting.of("Слабый арсенал", false)
    );

    private final BooleanSetting bindMode = new BooleanSetting(this, "По бинду", false);
    private final BindSetting cleanBind = new BindSetting(this, "Бинд очистки", -1);
    private final BooleanSetting bypassFT = new BooleanSetting(this, "Обход FT", false);

    private final Set<Item> plantItems = new HashSet<>();
    private final Set<Item> blockItems = new HashSet<>();
    private final Set<Item> weakArmorItems = new HashSet<>();
    private final Set<Item> weakToolItems = new HashSet<>();

    private int cleanDelay = 0;
    private int currentSlot = 9;
    private boolean wasBindPressed = false;
    private boolean inventoryOpened = false;

    @Override
    public void onEnable() {
        super.onEnable();
        initializeItemLists();
        currentSlot = 9;
        cleanDelay = 0;
    }

    private void initializeItemLists() {
        plantItems.addAll(Arrays.asList(
                Items.WHEAT_SEEDS, Items.BEETROOT_SEEDS, Items.PUMPKIN_SEEDS, Items.MELON_SEEDS,
                Items.WHEAT, Items.CARROT, Items.POTATO, Items.BEETROOT,
                Items.DANDELION, Items.POPPY, Items.BLUE_ORCHID, Items.ALLIUM, Items.AZURE_BLUET,
                Items.RED_TULIP, Items.ORANGE_TULIP, Items.WHITE_TULIP, Items.PINK_TULIP,
                Items.OXEYE_DAISY, Items.CORNFLOWER, Items.LILY_OF_THE_VALLEY, Items.WITHER_ROSE,
                Items.SUNFLOWER, Items.LILAC, Items.ROSE_BUSH, Items.PEONY,
                Items.TALL_GRASS, Items.LARGE_FERN, Items.FERN, Items.GRASS,
                Items.DEAD_BUSH, Items.SEAGRASS, Items.SEA_PICKLE, Items.KELP,
                Items.BAMBOO, Items.SUGAR_CANE, Items.CACTUS, Items.BROWN_MUSHROOM, Items.RED_MUSHROOM,
                Items.VINE, Items.LILY_PAD, Items.COCOA_BEANS, Items.SWEET_BERRIES,
                Items.APPLE, Items.MELON_SLICE, Items.PUMPKIN, Items.CARVED_PUMPKIN,
                Items.INK_SAC, Items.ROSE_BUSH, Items.CACTUS, Items.COCOA_BEANS,
                Items.LAPIS_LAZULI, Items.PURPLE_DYE, Items.CYAN_DYE, Items.LIGHT_GRAY_DYE,
                Items.GRAY_DYE, Items.PINK_DYE, Items.LIME_DYE, Items.YELLOW_DYE,
                Items.LIGHT_BLUE_DYE, Items.MAGENTA_DYE, Items.ORANGE_DYE, Items.BONE_MEAL,
                Items.BROWN_DYE, Items.BLACK_DYE, Items.BLUE_DYE, Items.GREEN_DYE, Items.RED_DYE, Items.WHITE_DYE,
                Items.DIRT, Items.GRASS_BLOCK, Items.COARSE_DIRT, Items.PODZOL,
                Items.SAND, Items.RED_SAND, Items.GRAVEL, Items.CLAY,
                Items.MYCELIUM, Items.GRASS_PATH, Items.FARMLAND
        ));

        blockItems.addAll(Arrays.asList(
                Items.COBBLESTONE, Items.STONE, Items.GRANITE, Items.DIORITE, Items.ANDESITE,
                Items.POLISHED_GRANITE, Items.POLISHED_DIORITE, Items.POLISHED_ANDESITE,
                Items.SANDSTONE, Items.RED_SANDSTONE, Items.SMOOTH_SANDSTONE, Items.SMOOTH_RED_SANDSTONE,
                Items.OAK_PLANKS, Items.SPRUCE_PLANKS, Items.BIRCH_PLANKS, Items.JUNGLE_PLANKS,
                Items.ACACIA_PLANKS, Items.DARK_OAK_PLANKS, Items.CRIMSON_PLANKS, Items.WARPED_PLANKS,
                Items.OAK_LOG, Items.SPRUCE_LOG, Items.BIRCH_LOG, Items.JUNGLE_LOG,
                Items.ACACIA_LOG, Items.DARK_OAK_LOG, Items.CRIMSON_STEM, Items.WARPED_STEM,
                Items.STRIPPED_OAK_LOG, Items.STRIPPED_SPRUCE_LOG, Items.STRIPPED_BIRCH_LOG,
                Items.STRIPPED_JUNGLE_LOG, Items.STRIPPED_ACACIA_LOG, Items.STRIPPED_DARK_OAK_LOG,
                Items.GLASS, Items.WHITE_STAINED_GLASS, Items.ORANGE_STAINED_GLASS, Items.MAGENTA_STAINED_GLASS,
                Items.LIGHT_BLUE_STAINED_GLASS, Items.YELLOW_STAINED_GLASS, Items.LIME_STAINED_GLASS,
                Items.PINK_STAINED_GLASS, Items.GRAY_STAINED_GLASS, Items.LIGHT_GRAY_STAINED_GLASS,
                Items.CYAN_STAINED_GLASS, Items.PURPLE_STAINED_GLASS, Items.BLUE_STAINED_GLASS,
                Items.BROWN_STAINED_GLASS, Items.GREEN_STAINED_GLASS, Items.RED_STAINED_GLASS, Items.BLACK_STAINED_GLASS,
                Items.BRICKS, Items.MOSSY_COBBLESTONE, Items.OBSIDIAN, Items.NETHERRACK,
                Items.SOUL_SAND, Items.SOUL_SOIL, Items.BASALT, Items.BLACKSTONE,
                Items.END_STONE, Items.PURPUR_BLOCK, Items.QUARTZ_BLOCK
        ));

        weakArmorItems.addAll(Arrays.asList(
                Items.LEATHER_HELMET, Items.LEATHER_CHESTPLATE, Items.LEATHER_LEGGINGS, Items.LEATHER_BOOTS,
                Items.CHAINMAIL_HELMET, Items.CHAINMAIL_CHESTPLATE, Items.CHAINMAIL_LEGGINGS, Items.CHAINMAIL_BOOTS,
                Items.IRON_HELMET, Items.IRON_CHESTPLATE, Items.IRON_LEGGINGS, Items.IRON_BOOTS,
                Items.GOLDEN_HELMET, Items.GOLDEN_CHESTPLATE, Items.GOLDEN_LEGGINGS, Items.GOLDEN_BOOTS
        ));

        weakToolItems.addAll(Arrays.asList(
                Items.WOODEN_SWORD, Items.WOODEN_PICKAXE, Items.WOODEN_AXE, Items.WOODEN_SHOVEL, Items.WOODEN_HOE,
                Items.STONE_SWORD, Items.STONE_PICKAXE, Items.STONE_AXE, Items.STONE_SHOVEL, Items.STONE_HOE,
                Items.IRON_SWORD, Items.IRON_PICKAXE, Items.IRON_AXE, Items.IRON_SHOVEL, Items.IRON_HOE,
                Items.GOLDEN_SWORD, Items.GOLDEN_PICKAXE, Items.GOLDEN_AXE, Items.GOLDEN_SHOVEL, Items.GOLDEN_HOE
        ));
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (mc.player == null || mc.world == null) return;

        if (inventoryOpened) {
            return;
        }

        if (mc.currentScreen instanceof ContainerScreen) return;

        if (bindMode.getValue()) {
            boolean isBindPressed = isKeyPressed(cleanBind.getValue());
            if (isBindPressed && !wasBindPressed) {
                cleanInventory();
            }
            wasBindPressed = isBindPressed;
        } else {
            if (cleanDelay > 0) {
                cleanDelay--;
                return;
            }
            autoCleanInventory();
        }
    }

    private void cleanInventory() {
        cleanDelay = 3;

        for (int i = 9; i < 36; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack.isEmpty()) continue;

            if (shouldDropItem(stack)) {
                dropItem(i);
                break;
            }
        }
    }

    private void autoCleanInventory() {
        if (cleanDelay > 0) {
            cleanDelay--;
            return;
        }

        if (currentSlot >= 36) {
            currentSlot = 9;
        }

        ItemStack stack = mc.player.inventory.getStackInSlot(currentSlot);
        if (!stack.isEmpty() && shouldDropItem(stack)) {
            dropItem(currentSlot);
            cleanDelay = 2;
        }

        currentSlot++;
    }

    private boolean shouldDropItem(ItemStack stack) {
        Item item = stack.getItem();

        if (cleanSettings.getValue("Растения") && plantItems.contains(item)) {
            return true;
        }

        if (cleanSettings.getValue("Блоки") && (blockItems.contains(item) || Block.getBlockFromItem(item) != Blocks.AIR)) {
            return true;
        }

        if (cleanSettings.getValue("Слабый арсенал")) {
            return weakArmorItems.contains(item) || weakToolItems.contains(item);
        }

        return false;
    }

    private void dropItem(int slot) {
        if (mc.playerController == null) return;

        // Если включен обход FT - открываем инвентарь
        if (bypassFT.getValue()) {
            openInventory(slot);
            return;
        }

        performDrop(slot);
    }

    private void performDrop(int slot) {
        mc.playerController.windowClick(
                mc.player.container.windowId,
                slot,
                0,
                ClickType.PICKUP,
                mc.player
        );

        mc.playerController.windowClick(
                mc.player.container.windowId,
                -999,
                0,
                ClickType.PICKUP,
                mc.player
        );
    }

    private void openInventory(int slot) {
        mc.displayScreen(new InventoryScreen(mc.player));
        inventoryOpened = true;

        new Thread(() -> {
            try {
                Thread.sleep(5);
                if (mc.player != null) {
                    mc.execute(() -> {
                        if (mc.currentScreen != null) {
                            performDrop(slot);
                        }
                        mc.displayScreen(null);
                        inventoryOpened = false;
                    });
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                inventoryOpened = false;
            }
        }).start();
    }

    private boolean isKeyPressed(int keyCode) {
        if (keyCode == -1) return false;

        try {
            if (keyCode >= 0 && keyCode < 512) {
                return org.lwjgl.glfw.GLFW.glfwGetKey(mc.getMainWindow().getHandle(), keyCode) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
            }

            if (keyCode >= 1000) {
                int mouseButton = keyCode - 1000;
                return org.lwjgl.glfw.GLFW.glfwGetMouseButton(mc.getMainWindow().getHandle(), mouseButton) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
            }
        } catch (Exception e) {
            return false;
        }

        return false;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        cleanDelay = 0;
        currentSlot = 9;
        wasBindPressed = false;

        if (inventoryOpened) {
            mc.displayScreen(null);
            inventoryOpened = false;
        }
    }
}