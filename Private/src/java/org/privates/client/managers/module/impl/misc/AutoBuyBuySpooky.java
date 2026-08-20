package org.privates.client.managers.module.impl.misc;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.gui.screen.inventory.ChestScreen;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.text.TextFormatting;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.chat.ChatUtil;
import org.privates.client.utils.player.InventoryUtility;
import org.privates.lib.util.time.StopWatch;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ModuleInfo(name = "AutoBuyBuySpooky", category = Category.MISC, description = "Покупка через /ah search по минимальной цене")
public class AutoBuyBuySpooky extends Module {
    private static final long SEARCH_WAIT_MS = 3000L;
    private static final long SEARCH_RESEND_MS = 7000L;
    private static final int MAX_REFRESH_ATTEMPTS = 5;

    private final SliderSetting delay = new SliderSetting(this, "Задержка", 450, 100, 2000, 50);
    private final BooleanSetting debug = new BooleanSetting(this, "Debug", true);

    private final StopWatch tickWatch = new StopWatch();
    private final StopWatch buyWatch = new StopWatch();
    private final Pattern funTimePricePattern = Pattern.compile("\\$(\\d{1,3}(?:,? ?\\d{3})*)");

    private long searchSentAt = 0L;
    private boolean searchSent = false;
    private int refreshAttempts = 0;
    private int targetIndex = 0;
    private AutoBuySpooky.AutoBuyItemData activeTarget;

    @Override
    public void onEnable() {
        searchSent = false;
        refreshAttempts = 0;
        searchSentAt = 0L;
        targetIndex = 0;
        activeTarget = null;
        tickWatch.reset();
        buyWatch.reset();
    }

    @EventHandler
    public void onUpdate(UpdateEvent e) {
        if (mc.player == null || !tickWatch.finished(delay.getValue().longValue())) {
            return;
        }

        AutoBuySpooky autoBuy = AutoBuySpooky.getInstance();
        List<AutoBuySpooky.AutoBuyItemData> targets = getTargets(autoBuy);
        if (targets.isEmpty()) {
            activeTarget = null;
            tickWatch.reset();
            return;
        }

        if (targetIndex >= targets.size()) {
            targetIndex = 0;
        }
        activeTarget = targets.get(targetIndex);

        if (!(mc.currentScreen instanceof ChestScreen) || !isAuctionScreen()) {
            if (!searchSent || System.currentTimeMillis() - searchSentAt > SEARCH_RESEND_MS) {
                sendSearch(activeTarget);
            }
            tickWatch.reset();
            return;
        }

        if (isConfirmScreen()) {
            if (buyWatch.finished(650L)) {
                InventoryUtility.clickSlotId(0, 0, ClickType.QUICK_MOVE, true);
                buyWatch.reset();
                dbg("confirm click slot 0");
            }
            tickWatch.reset();
            return;
        }

        long elapsed = System.currentTimeMillis() - searchSentAt;
        if (elapsed < SEARCH_WAIT_MS) {
            dbg("wait results " + elapsed + "ms: " + activeTarget.displayName);
            tickWatch.reset();
            return;
        }
        if (elapsed > SEARCH_RESEND_MS) {
            sendSearch(activeTarget);
            tickWatch.reset();
            return;
        }

        if (buyBestLot(autoBuy, activeTarget)) {
            refreshAttempts = 0;
            searchSent = false;
            nextTarget(targets);
            tickWatch.reset();
            return;
        }

        refreshAuction();
        tickWatch.reset();
    }

    private List<AutoBuySpooky.AutoBuyItemData> getTargets(AutoBuySpooky autoBuy) {
        List<AutoBuySpooky.AutoBuyItemData> list = new ArrayList<>();
        for (AutoBuySpooky.AutoBuyItemData d : autoBuy.itemsData()) {
            if (d != null && d.item != null && d.price > 0) {
                list.add(d);
            }
        }
        return list;
    }

    private void nextTarget(List<AutoBuySpooky.AutoBuyItemData> targets) {
        targetIndex++;
        if (targetIndex >= targets.size()) {
            targetIndex = 0;
        }
    }

    private void sendSearch(AutoBuySpooky.AutoBuyItemData target) {
        String query = (target.searchAlias != null && !target.searchAlias.isEmpty()) ? target.searchAlias : target.displayName;
        mc.player.sendChatMessage("/ah search " + query);
        searchSentAt = System.currentTimeMillis();
        searchSent = true;
        refreshAttempts = 0;
        dbg("search: " + query + " / max unit " + target.price);
    }

    private boolean buyBestLot(AutoBuySpooky autoBuy, AutoBuySpooky.AutoBuyItemData target) {
        if (!(mc.player.openContainer instanceof ChestContainer chest)) {
            return false;
        }

        List<Lot> lots = new ArrayList<>();
        for (Slot slot : chest.inventorySlots) {
            if (slot.slotNumber > 44 || !slot.getHasStack() || slot.getStack().isEmpty()) {
                continue;
            }

            ItemStack stack = slot.getStack();
            AutoBuySpooky.AutoBuyItemData found = autoBuy.findItemToBuy(stack);
            if (found == null || !found.displayName.equalsIgnoreCase(target.displayName)) {
                continue;
            }

            int total = autoBuy.serverMode().is("SpookyTime") ? getPriceSpookyTime(stack) : getPriceFunTime(stack);
            if (total <= 0) {
                continue;
            }

            int unit = total / Math.max(1, stack.getCount());
            if (unit > target.price) {
                continue;
            }

            lots.add(new Lot(slot.slotNumber, unit, stack.getCount()));
        }

        Lot best = lots.stream()
                .min(Comparator.comparingInt((Lot l) -> l.unitPrice).thenComparingInt(l -> -l.count))
                .orElse(null);
        if (best == null) {
            dbg("no lot: " + target.displayName);
            return false;
        }

        InventoryUtility.clickSlotId(best.slot, 0, ClickType.QUICK_MOVE, true);
        dbg("buy: " + target.displayName + " slot=" + best.slot + " unit=" + best.unitPrice + " count=" + best.count);
        return true;
    }

    private void refreshAuction() {
        if (mc.player.openContainer instanceof ChestContainer chest) {
            Slot updateSlot = chest.getSlot(49);
            if (updateSlot != null) {
                InventoryUtility.clickSlotId(49, 0, ClickType.PICKUP, true);
                refreshAttempts++;
                dbg("refresh #" + refreshAttempts);
            }
        }

        if (refreshAttempts >= MAX_REFRESH_ATTEMPTS) {
            refreshAttempts = 0;
            searchSent = false;
            targetIndex++;
            mc.player.closeScreen();
            dbg("switch target");
        }
    }

    private boolean isAuctionScreen() {
        if (!(mc.currentScreen instanceof ChestScreen)) {
            return false;
        }
        String t = TextFormatting.removeFormatting(mc.currentScreen.getTitle().getString()).toLowerCase();
        return t.contains("аукцион") || t.contains("поиск") || t.contains("auction") || t.contains("search");
    }

    private boolean isConfirmScreen() {
        if (!(mc.currentScreen instanceof ChestScreen)) {
            return false;
        }
        String t = TextFormatting.removeFormatting(mc.currentScreen.getTitle().getString()).toLowerCase();
        return t.contains("подозрительная цена") || t.contains("confirm");
    }

    private int getPriceSpookyTime(ItemStack stack) {
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

    private int getPriceFunTime(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        if (tag != null && tag.contains("display", 10)) {
            CompoundNBT display = tag.getCompound("display");
            if (display.contains("Lore", 9)) {
                ListNBT lore = display.getList("Lore", 8);
                for (int j = 0; j < lore.size(); ++j) {
                    String loreLine = TextFormatting.removeFormatting(lore.getString(j));
                    if (loreLine == null) {
                        continue;
                    }

                    Matcher matcher = funTimePricePattern.matcher(loreLine);
                    if (matcher.find()) {
                        String priceString = matcher.group(1).replaceAll(",", "").replaceAll(" ", "");
                        if (!priceString.isEmpty()) {
                            return Integer.parseInt(priceString);
                        }
                    }

                    String lower = loreLine.toLowerCase();
                    if (lower.contains("цена") || lower.contains("price") || lower.contains("монет")) {
                        String clean = loreLine.replaceAll("[^0-9]", "");
                        if (!clean.isEmpty()) {
                            return Integer.parseInt(clean);
                        }
                    }
                }
            }
        }
        return -1;
    }

    private void dbg(String s) {
        if (!debug.getValue()) {
            return;
        }
        ChatUtil.addText("[AutoBuyBuySpooky] " + s);
    }

    private record Lot(int slot, int unitPrice, int count) {
    }
}

