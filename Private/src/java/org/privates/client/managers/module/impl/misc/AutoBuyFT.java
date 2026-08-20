package org.privates.client.managers.module.impl.misc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.gui.screen.inventory.ChestScreen;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.utils.other.Instance;

import java.util.*;

@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "AutoBuyFT", category = Category.MISC)
public class AutoBuyFT extends Module {

    public static AutoBuyFT getInstance() {
        return Instance.get(AutoBuyFT.class);
    }

    private static final Set<Item> BUY_ITEMS = new HashSet<>(Arrays.asList(
            Items.STONE,
            Items.DIAMOND,
            Items.GOLD_INGOT,
            Items.IRON_INGOT,
            Items.NETHERITE_SWORD,
            Items.NETHERITE_HELMET,
            Items.NETHERITE_CHESTPLATE,
            Items.NETHERITE_LEGGINGS,
            Items.NETHERITE_BOOTS
    ));

    private static final int MAX_PRICE = 1000000;

    private static final List<String> RANDOM_COMMANDS = new ArrayList<>();

    static {
        for (int i = 103; i <= 111; i++) RANDOM_COMMANDS.add("/an" + i);
        for (int i = 203; i <= 220; i++) RANDOM_COMMANDS.add("/an" + i);
        for (int i = 303; i <= 309; i++) RANDOM_COMMANDS.add("/an" + i);
        for (int i = 503; i <= 506; i++) RANDOM_COMMANDS.add("/an" + i);
        for (int i = 603; i <= 608; i++) RANDOM_COMMANDS.add("/an" + i);
    }

    private int tickCooldown = 0;

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (tickCooldown > 0) {
            tickCooldown--;
            return;
        }
        if (mc.currentScreen instanceof ChestScreen chestScreen) {
            Container container = chestScreen.getContainer();
            for (Slot slot : container.inventorySlots) {
                ItemStack stack = slot.getStack();
                if (isBuyItem(stack)) {
                    int price = getPrice(stack);
                    if (price != -1 && price <= MAX_PRICE) {
                        // Покупка: shift + левая кнопка мыши по предмету
                        mc.playerController.windowClick(
                                container.windowId,
                                slot.slotNumber,
                                1, // shift click
                                ClickType.QUICK_MOVE,
                                mc.player
                        );
                        tickCooldown = 5;
                        // После покупки делаем closescreen и отправляем команды
                        mc.player.closeScreen();
                        sendRandomCommands();
                        return;
                    }
                }
            }
        } else {
            // Если не в сундуке - если слот 49 - это nether_star, жмем mouse2
            if (mc.currentScreen != null && mc.player != null && mc.player.openContainer != null) {
                Container container = mc.player.openContainer;
                if (container.inventorySlots.size() > 49) {
                    Slot slot = container.inventorySlots.get(49);
                    ItemStack stack = slot.getStack();
                    if (!stack.isEmpty() && stack.getItem() == Items.NETHER_STAR) {
                        mc.playerController.windowClick(
                                container.windowId,
                                49,
                                1, // right click
                                ClickType.PICKUP,
                                mc.player
                        );
                    }
                }
            }
        }
    }

    private boolean isBuyItem(ItemStack stack) {
        if (BUY_ITEMS.contains(stack.getItem())) return true;
        // Шалкеры любого цвета (имя содержит shulker_box)
        String name = stack.getItem().getTranslationKey();
        return name != null && name.contains("shulker_box");
    }

    public static int getPrice(ItemStack stack) {
        if (stack.hasTag()) {
            CompoundNBT tag = stack.getTag();
            if (tag.contains("display", 10)) {
                CompoundNBT display = tag.getCompound("display");
                if (display.contains("Lore", 9)) {
                    ListNBT lore = display.getList("Lore", 8);
                    for (int i = 0; i < lore.size(); i++) {
                        String loreLine = lore.getString(i);
                        if (loreLine.contains("Ценa")) {
                            String priceText = loreLine.replaceAll(".*\\$", "").replaceAll("[^0-9]", "");
                            try {
                                return Integer.parseInt(priceText);
                            } catch (NumberFormatException ignored) {
                            }
                        }
                    }
                }
            }
        }
        return -1;
    }

    private void sendRandomCommands() {
        Random random = new Random();
        for (int i = 0; i < 5; i++) {
            int idx = random.nextInt(RANDOM_COMMANDS.size());
            sendCommand(RANDOM_COMMANDS.get(idx));
        }
    }

    private void sendCommand(String cmd) {
        if (mc.player != null) {
            mc.player.sendChatMessage(cmd);
        }
    }
}