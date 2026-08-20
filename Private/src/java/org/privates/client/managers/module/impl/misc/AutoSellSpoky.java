package org.privates.client.managers.module.impl.misc;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.gui.screen.inventory.ChestScreen;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import org.privates.client.api.annotations.Category1;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.chat.ChatUtil;
import org.privates.client.utils.other.Instance;
import org.privates.lib.util.time.StopWatch;

import java.util.List;

@Category1
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "AutoSellSpoky", category = Category.MISC, description = "Автопродажа предметов по оптимальной цене")
public class AutoSellSpoky extends Module {

    private static final String THEME_COLOR = "§d";
    private static final String TEXT_COLOR = "§4";
    private static final String ACCENT_COLOR_1 = "§b";
    private static final String ACCENT_COLOR_2 = "§4";
    private static final String SUCCESS_COLOR = "§4";
    private static final String ERROR_COLOR = "§c";

    public static AutoSellSpoky getInstance() {
        return Instance.get(AutoSellSpoky.class);
    }

    final SliderSetting delaySlider = new SliderSetting(this, "Задержка между", 900, 500, 10000, 100);

    final StopWatch timer = new StopWatch();
    int currentItemIndex = 0;
    List<AutoBuySpooky.AutoBuyItemData> itemsToSell;
    boolean waitingForAuction = false;
    boolean waitingForSell = false;
    long searchStartTime = 0;
    static final long SEARCH_TIMEOUT = 950;
    ItemStack currentItemStack = null;
    int currentItemSlot = -1;
    int calculatedSellPrice = 0;
    AutoBuySpooky.AutoBuyItemData currentSearchItem = null;

    @Override
    protected void onEnable() {
        currentItemIndex = 0;
        itemsToSell = AutoBuySpooky.getInstance().itemsData();
        waitingForAuction = false;
        waitingForSell = false;
        timer.reset();
        searchStartTime = 0;
        currentItemStack = null;
        currentItemSlot = -1;
        calculatedSellPrice = 0;
        currentSearchItem = null;

        if (itemsToSell == null || itemsToSell.isEmpty()) {
            ChatUtil.addText(THEME_COLOR + "[AutoSell] " + ERROR_COLOR + "Список предметов пуст!");
            setEnabled(false);
            return;
        }

        ChatUtil.addText(THEME_COLOR + "[AutoSell] " + SUCCESS_COLOR + "Начало автоматической продажи.");
    }

    @Override
    protected void onDisable() {
        ChatUtil.addText(THEME_COLOR + "[AutoSell] " + TEXT_COLOR + "Продажа завершена.");
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (!timer.finished(delaySlider.getValue().longValue())) {
            return;
        }

        if (waitingForAuction && System.currentTimeMillis() - searchStartTime > SEARCH_TIMEOUT) {
            moveToNextItem();
            return;
        }

        if (waitingForSell) {
            sellCurrentItem();
            return;
        }

        if (!waitingForAuction) {
            findAndSearchNextItem();
        } else if (mc.currentScreen instanceof ChestScreen chestScreen) {
            processAuctionResults(chestScreen.getContainer());
        }
    }

    private void findAndSearchNextItem() {
        if (currentItemStack == null) {
            boolean found = false;
            for (int i = 0; i < itemsToSell.size(); i++) {
                AutoBuySpooky.AutoBuyItemData itemData = itemsToSell.get(i);
                for (int slot = 0; slot < 36; slot++) {
                    ItemStack stack = mc.player.inventory.getStackInSlot(slot);
                    if (!stack.isEmpty() && matchesItemData(stack, itemData)) {
                        currentItemStack = stack;
                        currentItemSlot = slot;
                        currentItemIndex = i;
                        currentSearchItem = itemData;
                        found = true;
                        break;
                    }
                }
                if (found) break;
            }

            if (!found) {
                ChatUtil.addText(THEME_COLOR + "[AutoSell] " + SUCCESS_COLOR + "Все предметы проданы!");
                setEnabled(false);
                return;
            }
        }

        ChatUtil.addText(THEME_COLOR + "[AutoSell] " + TEXT_COLOR + "Поиск: " + ACCENT_COLOR_1 + currentSearchItem.displayName);
        mc.player.sendChatMessage("/ah search " + currentSearchItem.displayName);
        waitingForAuction = true;
        searchStartTime = System.currentTimeMillis();
        timer.reset();
    }

    private void processAuctionResults(Container container) {
        int minPrice = findMinPriceOnAuction(container);

        if (minPrice != Integer.MAX_VALUE) {
            ChatUtil.addText(THEME_COLOR + "[AutoSell] " + TEXT_COLOR + "Найдена минимальная цена: " + ACCENT_COLOR_2 + minPrice);
            int stackSize = currentItemStack.getCount();
            int totalPrice = minPrice * stackSize;
            calculatedSellPrice = calculateDiscountedPrice(totalPrice);

            ChatUtil.addText(THEME_COLOR + "[AutoSell] " + TEXT_COLOR + "Итоговая цена за " + stackSize + " шт: " + SUCCESS_COLOR + calculatedSellPrice);

            mc.player.closeScreen();
            waitingForAuction = false;
            waitingForSell = true;
            timer.reset();
        } else {
            ChatUtil.addText(THEME_COLOR + "[AutoSell] " + ERROR_COLOR + "Предмет не найден на аукционе, пропускаем.");
            mc.player.closeScreen();
            moveToNextItem();
        }
    }

    private void sellCurrentItem() {
        if (currentItemStack != null && currentItemSlot != -1 && calculatedSellPrice > 0) {
            int targetSlot = currentItemSlot + 9;

            for (Slot slot : mc.player.openContainer.inventorySlots) {
                if (slot.slotNumber == targetSlot) {
                    ItemStack slotStack = slot.getStack();
                    if (!slotStack.isEmpty() && matchesItemData(slotStack, currentSearchItem)) {
                        mc.playerController.windowClick(
                                mc.player.openContainer.windowId,
                                targetSlot,
                                0,
                                ClickType.PICKUP,
                                mc.player
                        );

                        timer.reset();
                        while (!timer.finished(300)) {
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException ignored) {
                            }
                        }

                        mc.player.sendChatMessage("/ah sell " + calculatedSellPrice);
                        ChatUtil.addText(THEME_COLOR + "[AutoSell] " + SUCCESS_COLOR + "Продан предмет за " + calculatedSellPrice);

                        timer.reset();
                        while (!timer.finished(400)) {
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException ignored) {
                            }
                        }
                    }
                    break;
                }
            }

            waitingForSell = false;
            moveToNextItem();
        }
    }

    private void moveToNextItem() {
        currentItemStack = null;
        currentItemSlot = -1;
        currentSearchItem = null;
        calculatedSellPrice = 0;
        waitingForAuction = false;
        waitingForSell = false;
        timer.reset();
    }

    private int findMinPriceOnAuction(Container container) {
        int minPrice = Integer.MAX_VALUE;
        for (int i = 0; i < container.inventorySlots.size(); i++) {
            if (i >= 54) break;

            ItemStack stack = container.getSlot(i).getStack();
            if (stack != null && !stack.isEmpty() && matchesItemData(stack, currentSearchItem)) {
                int totalPrice = getPrice(stack);
                if (totalPrice > 0) {
                    int pricePerItem = totalPrice / Math.max(1, stack.getCount());
                    if (pricePerItem < minPrice) {
                        minPrice = pricePerItem;
                    }
                }
            }
        }
        return minPrice;
    }

    private int calculateDiscountedPrice(int originalPrice) {
        double discount;

        if (originalPrice < 100000) {
            discount = 0.03;
        } else if (originalPrice < 500000) {
            discount = 0.04;
        } else if (originalPrice < 1000000) {
            discount = 0.05;
        } else if (originalPrice < 5000000) {
            discount = 0.07;
        } else if (originalPrice < 10000000) {
            discount = 0.08;
        } else if (originalPrice < 15000000) {
            discount = 0.10;
        } else {
            discount = 0.10;
        }

        return (int) (originalPrice * (1.0 - discount));
    }

    private boolean matchesItemData(ItemStack stack, AutoBuySpooky.AutoBuyItemData itemData) {
        String stackName = stack.getDisplayName().getString().toLowerCase().replaceAll("§.", "");
        String itemName = itemData.displayName.toLowerCase();
        return stackName.contains(itemName) || itemName.contains(stackName);
    }

    private int getPrice(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        if (tag != null && tag.contains("display", 10)) {
            CompoundNBT display = tag.getCompound("display");
            if (display.contains("Lore", 9)) {
                ListNBT lore = display.getList("Lore", 8);
                for (int j = 0; j < lore.size(); ++j) {
                    String loreLine = lore.getString(j);
                    try {
                        JsonObject object = new JsonParser().parse(loreLine).getAsJsonObject();
                        if (object.has("extra") && object.getAsJsonArray("extra").size() > 2) {
                            JsonArray array = object.getAsJsonArray("extra");
                            JsonObject title = array.get(1).getAsJsonObject();
                            if (title.has("text") && title.get("text").getAsString().trim().toLowerCase().contains("цена")) {
                                String line = array.get(2).getAsJsonObject().get("text").getAsString().trim().substring(1).replaceAll("[ ,]", "");
                                return Integer.parseInt(line);
                            }
                        }
                    } catch (Exception ignored) {
                    }
                }
            }
        }
        return -1;
    }
}