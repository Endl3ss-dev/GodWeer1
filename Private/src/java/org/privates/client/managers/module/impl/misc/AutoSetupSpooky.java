package org.privates.client.managers.module.impl.misc;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.gui.screen.inventory.ChestScreen;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import org.privates.client.api.annotations.Category1;
import org.privates.client.api.annotations.PVE;
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
@PVE
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "AutoSetupSpooky", category = Category.MISC, description = "Авто парсер или авто поставка самых дешевых цен для autobuy")
public class AutoSetupSpooky extends Module {

    private static final String THEME_COLOR = "§d";
    private static final String TEXT_COLOR = "§4";
    private static final String ACCENT_COLOR_1 = "§b";
    private static final String ACCENT_COLOR_2 = "§4";
    private static final String SUCCESS_COLOR = "§4";
    private static final String ERROR_COLOR = "§c";

    public static AutoSetupSpooky getInstance() {
        return Instance.get(AutoSetupSpooky.class);
    }

    final SliderSetting delaySlider = new SliderSetting(this, "Задержка между", 900, 500, 10000, 100);
    final SliderSetting percentageSlider = new SliderSetting(this, "Скидка %", 30, 1, 99, 1);

    final StopWatch timer = new StopWatch();
    int currentItemIndex = 0;
    List<AutoBuySpooky.AutoBuyItemData> itemsToSetup;
    boolean waitingForAuction = false;
    long searchStartTime = 0;
    static final long SEARCH_TIMEOUT = 950;

    @Override
    protected void onEnable() {
        currentItemIndex = 0;
        itemsToSetup = AutoBuySpooky.getInstance().itemsData();
        waitingForAuction = false;
        timer.reset();
        searchStartTime = 0;

        if (itemsToSetup == null || itemsToSetup.isEmpty()) {
            ChatUtil.addText(THEME_COLOR + "[AutoSetup] " + ERROR_COLOR + "Список предметов пуст!");
            setEnabled(false);
            return;
        }

        ChatUtil.addText(THEME_COLOR + "[AutoSetup] " + SUCCESS_COLOR + "Начало автоматической настройки цен.");
    }

    @Override
    protected void onDisable() {
        AutoBuySpooky.getInstance().savePrices();
        ChatUtil.addText(THEME_COLOR + "[AutoSetup] " + TEXT_COLOR + "Настройка завершена. Цены сохранены.");
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (waitingForAuction && System.currentTimeMillis() - searchStartTime > SEARCH_TIMEOUT) {
            moveToNextItem();
            return;
        }

        if (!timer.finished(100)) {
            return;
        }

        if (currentItemIndex >= itemsToSetup.size()) {
            setEnabled(false);
            return;
        }

        AutoBuySpooky.AutoBuyItemData currentItem = itemsToSetup.get(currentItemIndex);

        if (!waitingForAuction) {
            ChatUtil.addText(THEME_COLOR + "[AutoSetup] " + TEXT_COLOR + "Поиск: " + ACCENT_COLOR_1 + currentItem.displayName);
            mc.player.sendChatMessage("/ah search " + currentItem.displayName);
            waitingForAuction = true;
            searchStartTime = System.currentTimeMillis();
            timer.reset();
            return;
        }

        if (mc.currentScreen instanceof ChestScreen chestScreen) {
            int minPrice = findMinPriceOnAuction(chestScreen.getContainer());

            if (minPrice != Integer.MAX_VALUE) {
                ChatUtil.addText(THEME_COLOR + "[AutoSetup] " + TEXT_COLOR + "Найдена минимальная цена: " + ACCENT_COLOR_2 + minPrice);
                int discountedPrice = calculateDiscountedPrice(minPrice);
                currentItem.price = discountedPrice;

                int itemIndex = AutoBuySpooky.getInstance().itemsData().indexOf(currentItem);
                if (itemIndex != -1) {
                    AutoBuySpooky.getInstance().setPriceForItem(itemIndex, discountedPrice);
                }

                ChatUtil.addText(THEME_COLOR + "[AutoSetup] " + TEXT_COLOR + "Вычел " + percentageSlider.getValue().intValue() + "%, новая цена: " + SUCCESS_COLOR + discountedPrice);
            } else {
                ChatUtil.addText(THEME_COLOR + "[AutoSetup] " + ERROR_COLOR + "Предмет не найден на аукционе, цена не изменена.");
            }

            mc.player.closeScreen();
            moveToNextItem();
        }
    }

    private void moveToNextItem() {
        currentItemIndex++;
        waitingForAuction = false;
        timer.reset();
    }

    private int findMinPriceOnAuction(Container container) {
        int minPrice = Integer.MAX_VALUE;
        for (int i = 0; i < container.inventorySlots.size(); i++) {
            if (i >= 54) break;

            ItemStack stack = container.getSlot(i).getStack();
            if (stack != null && !stack.isEmpty()) {
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
        double multiplier = 1.0 - (percentageSlider.getValue().doubleValue() / 100.0);
        return (int) (originalPrice * multiplier);
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