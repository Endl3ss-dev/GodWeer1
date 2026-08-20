package org.privates.client.managers.module.impl.misc;

import com.adl.nativeprotect.Native;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.gui.screen.inventory.ChestScreen;
import net.minecraft.client.gui.screen.inventory.CraftingScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.play.server.SChatPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import org.privates.client.api.annotations.Category1;
import org.privates.client.api.annotations.PVE;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.other.PacketEvent;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BindSetting;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.client.utils.chat.ChatUtil;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.player.InvUtil;
import org.privates.client.utils.player.InventoryUtility;
import org.privates.lib.util.time.StopWatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Category1
@PVE
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "SpookyFarm", category = Category.MISC)
public class SpookyFarm extends Module {

    private final ModeSetting serverMode = new ModeSetting(this, "Режим сервера", "FunTime", "SpookyTime");
    private final ModeSetting farmMode = new ModeSetting(this, "Режим фарма", "ExpFarm");
    private final BindSetting openGuiBind = new BindSetting(this, "nothing", -1);


    public static SpookyFarm getInstance() {
        return Instance.get(SpookyFarm.class);
    }

    public void openGui() {
        mc.displayScreen(new SpookyFarmGui());
    }

    private int diamondMaxPrice = 600;
    private int goldIngotMaxPrice = 145;
    private int sellExpPrice = 129;
    private int goldBlockPrice = 2000;
    private int applePrice = 2500;
    private int sellCharkaPrice = 200;
    private int craftDelay = 200;

    public int getDiamondMaxPrice() {
        return diamondMaxPrice;
    }

    public void setDiamondMaxPrice(int value) {
        this.diamondMaxPrice = value;
    }

    public int getGoldIngotMaxPrice() {
        return goldIngotMaxPrice;
    }

    public void setGoldIngotMaxPrice(int value) {
        this.goldIngotMaxPrice = value;
    }

    public int getSellExpPrice() {
        return sellExpPrice;
    }

    public void setSellExpPrice(int value) {
        this.sellExpPrice = value;
    }

    public int getGoldBlockPrice() {
        return goldBlockPrice;
    }

    public void setGoldBlockPrice(int value) {
        this.goldBlockPrice = value;
    }

    public int getApplePrice() {
        return applePrice;
    }

    public void setApplePrice(int value) {
        this.applePrice = value;
    }

    public int getSellCharkaPrice() {
        return sellCharkaPrice;
    }

    public void setSellCharkaPrice(int value) {
        this.sellCharkaPrice = value;
    }

    public int getCraftDelay() {
        return craftDelay;
    }

    public void setCraftDelay(int value) {
        this.craftDelay = value;
    }

    private final StopWatch timer = new StopWatch();
    private final StopWatch delayTimer = new StopWatch();
    private final StopWatch humanizeTimer = new StopWatch();

    private enum MainState {
        BUY_PHASE,
        CRAFT_PHASE,
        SELL_PHASE
    }

    private enum BuyState {
        SEARCH_ITEM1,
        BUY_ITEM1,
        SEARCH_ITEM2,
        BUY_ITEM2,
        FINISHED
    }

    private enum CraftState {
        OPEN_CRAFT,
        WAIT_BEFORE_INGOT,
        PLACE_INGOT_STACK,
        WAIT_AFTER_INGOT,
        WAIT_BEFORE_INGOT_RESULT,
        TAKE_INGOT_RESULT,
        WAIT_AFTER_INGOT_RESULT,
        CLEAR_INGOT_GRID,
        CRAFT_INGOT_TO_NUGGETS,
        TAKE_NUGGETS_RESULT,
        WAIT_BEFORE_DIAMOND,
        PLACE_DIAMOND_STACK,
        WAIT_AFTER_DIAMOND,
        WAIT_BEFORE_NUGGET_1,
        PLACE_NUGGET_STACK_1,
        WAIT_BEFORE_NUGGET_2,
        PLACE_NUGGET_STACK_2,
        WAIT_BEFORE_NUGGET_3,
        PLACE_NUGGET_STACK_3,
        WAIT_BEFORE_NUGGET_4,
        PLACE_NUGGET_STACK_4,
        WAIT_BEFORE_RESULT,
        TAKE_RESULT,
        WAIT_AFTER_RESULT,
        CLEAR_GRID,
        WAIT_BEFORE_CHECK,
        CHECK_CONTINUE,
        FINISHED,
        FIND_APPLE_SLOT,
        PICKUP_APPLE,
        PLACE_APPLE,
        PUT_BACK_APPLE,
        FIND_GB_SLOT,
        PICKUP_GB,
        PLACE_GB1,
        PLACE_GB2,
        PLACE_GB3,
        PLACE_GB4,
        PLACE_GB5,
        PLACE_GB6,
        PLACE_GB7,
        PLACE_GB8,
        TAKE_CHARKA,
        PUT_BACK_GB
    }

    private enum SellState {
        UNSTACK_ITEMS,
        FIND_ITEM,
        WAIT_CONFIRM,
        SELL_ITEM,
        RELIST,
        FINISHED
    }

    private enum RelistState {
        SEND_AH,
        CLICK_SLOT_46,
        TAKE_FROM_SLOT_9,
        CLOSE_SCREEN
    }

    private MainState mainState = MainState.BUY_PHASE;
    private BuyState buyState = BuyState.SEARCH_ITEM1;
    private CraftState craftState = CraftState.OPEN_CRAFT;
    private SellState sellState = SellState.FIND_ITEM;
    private RelistState relistState = RelistState.SEND_AH;
    private boolean hasItemsToTake = false;

    private int bought1 = 0;
    private int bought2 = 0;
    private static final int TARGET1_EXP = 2;
    private static final int TARGET2_EXP = 1;
    private static final int TARGET1_CHARKA = 1;
    private static final int TARGET2_CHARKA = 1;

    private final List<Integer> diamondSlots = new ArrayList<>();
    private final List<Integer> nuggetSlots = new ArrayList<>();
    private final List<Integer> ingotSlots = new ArrayList<>();
    private int currentDiamondIndex = 0;
    private int currentNuggetIndex = 0;
    private int currentIngotIndex = 0;
    private int appleSlot = -1;
    private int gbSlot = -1;

    private boolean sellPaused = false;
    private boolean isExpFarm = true;
    private net.minecraft.item.Item sellItem;
    private net.minecraft.item.Item buyItem1;
    private net.minecraft.item.Item buyItem2;
    private String searchItem1;
    private String searchItem2;
    private int targetItem1;
    private int targetItem2;
    private int currentRelistIndex = 0;


    private final Random random = new Random();
    private boolean wasGuiKeyPressed = false;

    @Override
    public void onEnable() {
        super.onEnable();
        isExpFarm = "ExpFarm".equals(farmMode.getValue());
        sellItem = isExpFarm ? Items.EXPERIENCE_BOTTLE : Items.ENCHANTED_GOLDEN_APPLE;
        if (isExpFarm) {
            searchItem1 = "Алмаз";
            buyItem1 = Items.DIAMOND;
            currentRelistIndex = 0;
            targetItem1 = TARGET1_EXP;
            searchItem2 = "золотой слиток";
            buyItem2 = Items.GOLD_INGOT;
            targetItem2 = TARGET2_EXP;
        } else {
            searchItem1 = "золотой блок";
            buyItem1 = Items.GOLD_BLOCK;
            targetItem1 = TARGET1_CHARKA;
            searchItem2 = "яблоко";
            buyItem2 = Items.APPLE;
            targetItem2 = TARGET2_CHARKA;
        }
        mainState = MainState.BUY_PHASE;
        hasItemsToTake = false;
        buyState = BuyState.SEARCH_ITEM1;
        craftState = CraftState.OPEN_CRAFT;
        sellState = SellState.FIND_ITEM;
        relistState = RelistState.SEND_AH;
        bought1 = 0;
        bought2 = 0;
        diamondSlots.clear();
        nuggetSlots.clear();
        ingotSlots.clear();
        currentDiamondIndex = 0;
        currentNuggetIndex = 0;
        currentIngotIndex = 0;
        appleSlot = -1;
        gbSlot = -1;
        sellPaused = false;
        timer.reset();
        delayTimer.reset();
        humanizeTimer.reset();
        determineInitialState();
        ChatUtil.addText("Начинаем фарм на сервере: " + serverMode.getValue() + " в режиме: " + farmMode.getValue());
        if (mainState == MainState.BUY_PHASE) {
            if (buyState == BuyState.SEARCH_ITEM1) {
                mc.player.sendChatMessage("/ah search " + searchItem1);
                delayTimer.reset();
            } else if (buyState == BuyState.SEARCH_ITEM2) {
                mc.player.sendChatMessage("/ah search " + searchItem2);
                delayTimer.reset();
            }
        }
    }

    @Override
    public void onDisable() {
        ChatUtil.addText("Фарм завершен");
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (mc.player == null || mc.world == null) return;

        boolean isGuiKeyPressed = org.lwjgl.glfw.GLFW.glfwGetKey(net.minecraft.client.Minecraft.getInstance().getMainWindow().getHandle(), openGuiBind.getValue()) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
        if (isGuiKeyPressed && !wasGuiKeyPressed && mc.currentScreen == null) {
            mc.displayScreen(new SpookyFarmGui());
        }
        wasGuiKeyPressed = isGuiKeyPressed;

        switch (mainState) {
            case BUY_PHASE:
                handleBuyPhase();
                break;
            case CRAFT_PHASE:
                handleCraftPhase();
                break;
            case SELL_PHASE:
                handleSellPhase();
                break;
        }
    }

    @EventHandler
    public void onChat(PacketEvent event) {
        if (!(event.getPacket() instanceof SChatPacket)) return;
        ITextComponent comp = ((SChatPacket) event.getPacket()).getChatComponent();
        String msg = comp.getString();

        if (mainState == MainState.SELL_PHASE) {
            if (msg.contains("Освободите хранилище")) {
                sellPaused = true;
                sellState = SellState.RELIST;
                relistState = RelistState.SEND_AH;
                ChatUtil.addText("Перевыставляем");
            }

            if (msg.contains("У Вас купили")) {
                if (sellPaused) {
                    sellPaused = false;
                    ChatUtil.addText("Продолжаем продажу");
                    timer.reset();
                }
            }

            if (msg.contains("воздух")) {
                handleAirSellError();
            }
        }
    }

    private void handleAirSellError() {
        mc.displayScreen(new InventoryScreen(mc.player));
        timer.reset();
        while (!timer.finished(150)) {
            try {
                Thread.sleep(0);
            } catch (InterruptedException ignored) {
            }
        }
        mc.player.closeScreen();
        sellState = SellState.FIND_ITEM;
        timer.reset();
    }

    private void determineInitialState() {
        int sellCount = getTotalCount(sellItem);
        int goldIngots = getTotalCount(Items.GOLD_INGOT);
        int diamonds = getStackCount(Items.DIAMOND);
        int nuggets = getTotalCount(Items.GOLD_NUGGET);
        int apples = getTotalCount(Items.APPLE);
        int gbs = getTotalCount(Items.GOLD_BLOCK);

        if (sellCount >= 1) {
            mainState = MainState.SELL_PHASE;
            if (!isExpFarm) {
                sellState = SellState.UNSTACK_ITEMS;
            } else {
                sellState = SellState.FIND_ITEM;
            }
            ChatUtil.addText("Начинаем продажу");
        } else if (isExpFarm) {
            boolean hasEnoughNuggets = hasEnoughGoldNuggets();

            if (goldIngots >= 1 && !hasEnoughNuggets) {
                mainState = MainState.CRAFT_PHASE;
                craftState = CraftState.OPEN_CRAFT;
                ChatUtil.addText("Переделываем слитки в самородки");
            } else if (diamonds >= 1 && hasEnoughNuggets) {
                mainState = MainState.CRAFT_PHASE;
                craftState = CraftState.OPEN_CRAFT;
                ChatUtil.addText("Крафтим опыт");
            } else {
                mainState = MainState.BUY_PHASE;
                if (diamonds < targetItem1) {
                    buyState = BuyState.SEARCH_ITEM1;
                    ChatUtil.addText("Покупаем алмазы");
                } else if (!hasEnoughNuggets) {
                    buyState = BuyState.SEARCH_ITEM2;
                    ChatUtil.addText("Покупаем золотые слитки");
                } else {
                    buyState = BuyState.FINISHED;
                }
                bought1 = 0;
                bought2 = 0;
            }
        } else {
            if (apples >= 1 && gbs >= 8) {
                mainState = MainState.CRAFT_PHASE;
                craftState = CraftState.OPEN_CRAFT;
                ChatUtil.addText("Крафтим чарку");
            } else {
                mainState = MainState.BUY_PHASE;
                if (gbs < 8) {
                    buyState = BuyState.SEARCH_ITEM1;
                    ChatUtil.addText("Покупаем золотые блоки");
                } else if (apples < 1) {
                    buyState = BuyState.SEARCH_ITEM2;
                    ChatUtil.addText("Покупаем яблоки");
                } else {
                    buyState = BuyState.FINISHED;
                }
                bought1 = 0;
                bought2 = 0;
            }
        }
    }

    private boolean hasEnoughGoldNuggets() {
        int totalNuggets = getTotalCount(Items.GOLD_NUGGET);
        int nuggetsInStacks = (totalNuggets / 64) * 64;
        int fullStacks = nuggetsInStacks / 64;
        return fullStacks >= 4;
    }

    private void resetCycle() {
        mainState = MainState.BUY_PHASE;
        buyState = BuyState.SEARCH_ITEM1;
        craftState = CraftState.OPEN_CRAFT;
        currentRelistIndex = 0;
        hasItemsToTake = false;
        sellState = SellState.FIND_ITEM;
        relistState = RelistState.SEND_AH;
        bought1 = 0;
        bought2 = 0;
        diamondSlots.clear();
        nuggetSlots.clear();
        ingotSlots.clear();
        currentDiamondIndex = 0;
        currentNuggetIndex = 0;
        currentIngotIndex = 0;
        appleSlot = -1;
        gbSlot = -1;
        sellPaused = false;
        timer.reset();
        delayTimer.reset();
        humanizeTimer.reset();
    }

    private void handleBuyPhase() {
        if (!timer.finished(getBuyDelay())) return;

        boolean isAuctionOpen = isAuctionOpened();
        switch (buyState) {
            case SEARCH_ITEM1:
                int currentItem1Count = isExpFarm ? getStackCount(Items.DIAMOND) : getTotalCount(Items.GOLD_BLOCK) / 64;

                if (currentItem1Count >= targetItem1) {
                    if (isAuctionOpen) {
                        mc.player.closeScreen();
                    }
                    buyState = BuyState.SEARCH_ITEM2;
                    bought2 = 0;
                    ChatUtil.addText("Первый предмет уже есть в инвентаре, переходим ко второму");
                    timer.reset();
                    break;
                }

                if (delayTimer.finished(2000)) {
                    if (isAuctionOpen) {
                        buyState = BuyState.BUY_ITEM1;
                        timer.reset();
                    } else {
                        mc.player.sendChatMessage("/ah search " + searchItem1);
                        delayTimer.reset();
                    }
                }
                break;

            case BUY_ITEM1:
                if (isAuctionOpen) {
                    currentItem1Count = isExpFarm ? getStackCount(Items.DIAMOND) : getTotalCount(Items.GOLD_BLOCK) / 64;

                    if (currentItem1Count >= targetItem1) {
                        mc.player.closeScreen();
                        buyState = BuyState.SEARCH_ITEM2;
                        bought2 = 0;
                        ChatUtil.addText("Первый предмет уже есть в инвентаре, переходим ко второму");
                        timer.reset();
                        break;
                    }

                    int maxP = isExpFarm ? diamondMaxPrice * 1000 : goldBlockPrice * 1000;
                    int minP = isExpFarm ? 0 : 500000;
                    buyItem(buyItem1, maxP, minP, targetItem1, bought1, true, searchItem1);
                } else {
                    currentItem1Count = isExpFarm ? getStackCount(Items.DIAMOND) : getTotalCount(Items.GOLD_BLOCK) / 64;

                    if (currentItem1Count >= targetItem1) {
                        buyState = BuyState.SEARCH_ITEM2;
                        bought2 = 0;
                        ChatUtil.addText("Первый предмет уже есть в инвентаре, переходим ко второму");
                        timer.reset();
                        break;
                    }

                    buyState = BuyState.SEARCH_ITEM1;
                    timer.reset();
                }
                break;

            case SEARCH_ITEM2:
                mc.player.closeScreen();
                mc.player.sendChatMessage("/ah search " + searchItem2);
                delayTimer.reset();
                buyState = BuyState.BUY_ITEM2;
                timer.reset();
                break;

            case BUY_ITEM2:
                if (delayTimer.finished(2000)) {
                    if (isAuctionOpen) {
                        if (isExpFarm && hasEnoughGoldNuggets()) {
                            buyState = BuyState.FINISHED;
                            ChatUtil.addText("У нас уже есть 4 стака золотых самородков, пропускаем покупку слитков");
                            timer.reset();
                            break;
                        }

                        if (!isExpFarm && getTotalCount(Items.APPLE) > 0) {
                            buyState = BuyState.FINISHED;
                            ChatUtil.addText("Яблоко уже есть, пропускаем покупку");
                            timer.reset();
                            break;
                        }

                        int maxP = isExpFarm ? goldIngotMaxPrice * 1000 : applePrice * 1000;
                        int minP = isExpFarm ? 0 : 400000;
                        buyItem(buyItem2, maxP, minP, targetItem2, bought2, false, searchItem2);
                    } else {
                        mc.player.sendChatMessage("/ah search " + searchItem2);
                        delayTimer.reset();
                    }
                }
                break;

            case FINISHED:
                mc.player.closeScreen();
                ChatUtil.addText("Покупка завершена");
                mainState = MainState.CRAFT_PHASE;
                craftState = CraftState.OPEN_CRAFT;
                timer.reset();
                break;
        }

        timer.reset();
    }

    private void handleCraftPhase() {
        long baseDelay = craftDelay;
        long humanDelay = getHumanizedDelay(baseDelay);

        if (!timer.finished(humanDelay)) return;

        if (isExpFarm) {
            handleExpCraft();
        } else {
            handleCharkaCraft();
        }
    }

    @Native
    private void handleExpCraft() {
        switch (craftState) {
            case OPEN_CRAFT:
                if (!(mc.currentScreen instanceof CraftingScreen)) {
                    mc.player.sendChatMessage("/craft");
                    timer.reset();
                } else {
                    findItems();
                    boolean hasEnoughNuggets = hasEnoughGoldNuggets();

                    if (!ingotSlots.isEmpty() && !hasEnoughNuggets) {
                        currentIngotIndex = 0;
                        craftState = CraftState.WAIT_BEFORE_INGOT;
                        humanizeTimer.reset();
                        ChatUtil.addText("Переделываем слитки в самородки");
                        timer.reset();
                    } else if (diamondSlots.isEmpty()) {
                        ChatUtil.addText("Нет алмазов, возвращаемся к покупке");
                        mc.player.closeScreen();
                        mainState = MainState.BUY_PHASE;
                        buyState = BuyState.SEARCH_ITEM1;
                        timer.reset();
                        return;
                    } else if (!hasEnoughNuggets) {
                        ChatUtil.addText("Нет 4 стаков самородков, возвращаемся к покупке слитков");
                        mc.player.closeScreen();
                        mainState = MainState.BUY_PHASE;
                        buyState = BuyState.SEARCH_ITEM2;
                        timer.reset();
                        return;
                    } else {
                        currentDiamondIndex = 0;
                        currentNuggetIndex = 0;
                        craftState = CraftState.WAIT_BEFORE_DIAMOND;
                        humanizeTimer.reset();
                        ChatUtil.addText("Начинаем крафт опыта");
                        timer.reset();
                    }
                }
                break;

            case WAIT_BEFORE_INGOT:
                if (humanizeTimer.finished(getRandomHumanPause())) {
                    craftState = CraftState.PLACE_INGOT_STACK;
                    timer.reset();
                }
                break;

            case PLACE_INGOT_STACK:
                if (mc.currentScreen instanceof CraftingScreen) {
                    if (currentIngotIndex < ingotSlots.size()) {
                        int slot = ingotSlots.get(currentIngotIndex);
                        ItemStack stack = mc.player.openContainer.getSlot(slot).getStack();

                        if (!stack.isEmpty() && stack.getCount() > 0) {
                            humanizedClick(mc.player.openContainer.windowId, slot, 0, ClickType.PICKUP);
                            currentIngotIndex++;
                            craftState = CraftState.WAIT_AFTER_INGOT;
                            humanizeTimer.reset();
                            timer.reset();
                        } else {
                            currentIngotIndex++;
                            timer.reset();
                        }
                    } else {
                        findItems();
                        boolean hasEnoughNuggets = hasEnoughGoldNuggets();

                        if (diamondSlots.isEmpty()) {
                            ChatUtil.addText("Нет алмазов после переделки слитков, возвращаемся к покупке");
                            mc.player.closeScreen();
                            mainState = MainState.BUY_PHASE;
                            buyState = BuyState.SEARCH_ITEM1;
                            timer.reset();
                            return;
                        }

                        if (!hasEnoughNuggets) {
                            ChatUtil.addText("Нет 4 стаков самородков после переделки, возвращаемся к покупке");
                            mc.player.closeScreen();
                            mainState = MainState.BUY_PHASE;
                            buyState = BuyState.SEARCH_ITEM2;
                            timer.reset();
                            return;
                        }

                        currentDiamondIndex = 0;
                        currentNuggetIndex = 0;
                        craftState = CraftState.WAIT_BEFORE_DIAMOND;
                        humanizeTimer.reset();
                        ChatUtil.addText("Начинаем крафт опыта");
                        timer.reset();
                    }
                }
                break;

            case WAIT_AFTER_INGOT:
                if (humanizeTimer.finished(getRandomHumanPause())) {
                    humanizedClick(mc.player.openContainer.windowId, 5, 0, ClickType.PICKUP);
                    craftState = CraftState.WAIT_BEFORE_INGOT_RESULT;
                    humanizeTimer.reset();
                    timer.reset();
                }
                break;

            case WAIT_BEFORE_INGOT_RESULT:
                if (humanizeTimer.finished(getRandomHumanPause())) {
                    craftState = CraftState.TAKE_INGOT_RESULT;
                    timer.reset();
                }
                break;

            case TAKE_INGOT_RESULT:
                if (mc.currentScreen instanceof CraftingScreen) {
                    if (humanizeTimer.finished(getRandomHumanPause())) {
                        Slot resultSlot = mc.player.openContainer.getSlot(0);
                        if (!resultSlot.getStack().isEmpty()) {
                            humanizedClick(mc.player.openContainer.windowId, 0, 0, ClickType.QUICK_MOVE);
                            craftState = CraftState.WAIT_AFTER_INGOT_RESULT;
                            humanizeTimer.reset();
                            timer.reset();
                        }
                    }
                }
                break;

            case WAIT_AFTER_INGOT_RESULT:
                if (humanizeTimer.finished(getRandomHumanPause())) {
                    craftState = CraftState.CLEAR_INGOT_GRID;
                    timer.reset();
                }
                break;

            case CLEAR_INGOT_GRID:
                if (mc.currentScreen instanceof CraftingScreen) {
                    boolean hasItems = false;
                    for (int i = 1; i <= 9; i++) {
                        Slot slot = mc.player.openContainer.getSlot(i);
                        if (!slot.getStack().isEmpty()) {
                            humanizedClick(mc.player.openContainer.windowId, i, 0, ClickType.QUICK_MOVE);
                            hasItems = true;
                            break;
                        }
                    }
                    if (!hasItems) {
                        craftState = CraftState.WAIT_BEFORE_INGOT;
                        humanizeTimer.reset();
                    }
                    timer.reset();
                }
                break;

            case WAIT_BEFORE_DIAMOND:
                if (humanizeTimer.finished(getRandomHumanPause())) {
                    craftState = CraftState.PLACE_DIAMOND_STACK;
                    timer.reset();
                }
                break;

            case PLACE_DIAMOND_STACK:
                if (mc.currentScreen instanceof CraftingScreen) {
                    if (currentDiamondIndex < diamondSlots.size()) {
                        int slot = diamondSlots.get(currentDiamondIndex);
                        ItemStack stack = mc.player.openContainer.getSlot(slot).getStack();

                        if (!stack.isEmpty() && stack.getCount() > 0) {
                            humanizedClick(mc.player.openContainer.windowId, slot, 0, ClickType.PICKUP);
                            currentDiamondIndex++;
                            craftState = CraftState.WAIT_AFTER_DIAMOND;
                            humanizeTimer.reset();
                            timer.reset();
                        } else {
                            currentDiamondIndex++;
                            timer.reset();
                        }
                    } else {
                        ChatUtil.addText("Алмазы закончились, завершаем крафт");
                        craftState = CraftState.FINISHED;
                        timer.reset();
                    }
                }
                break;

            case WAIT_AFTER_DIAMOND:
                if (humanizeTimer.finished(getRandomHumanPause())) {
                    humanizedClick(mc.player.openContainer.windowId, 5, 0, ClickType.PICKUP);
                    craftState = CraftState.WAIT_BEFORE_NUGGET_1;
                    humanizeTimer.reset();
                    timer.reset();
                }
                break;

            case WAIT_BEFORE_NUGGET_1:
                if (humanizeTimer.finished(getRandomHumanPause())) {
                    craftState = CraftState.PLACE_NUGGET_STACK_1;
                    timer.reset();
                }
                break;

            case PLACE_NUGGET_STACK_1:
                if (mc.currentScreen instanceof CraftingScreen) {
                    if (currentNuggetIndex < nuggetSlots.size()) {
                        int slot = nuggetSlots.get(currentNuggetIndex);
                        ItemStack stack = mc.player.openContainer.getSlot(slot).getStack();

                        if (!stack.isEmpty() && stack.getCount() > 0) {
                            humanizedClick(mc.player.openContainer.windowId, slot, 0, ClickType.PICKUP);
                            currentNuggetIndex++;
                            humanizeTimer.reset();
                            craftState = CraftState.WAIT_BEFORE_NUGGET_2;
                            timer.reset();
                        } else {
                            currentNuggetIndex++;
                            timer.reset();
                        }
                    } else {
                        ChatUtil.addText("Самородки закончились, возвращаемся к покупке");
                        mc.player.closeScreen();
                        mainState = MainState.BUY_PHASE;
                        buyState = BuyState.SEARCH_ITEM2;
                        timer.reset();
                    }
                }
                break;

            case WAIT_BEFORE_NUGGET_2:
                if (humanizeTimer.finished(getRandomHumanPause())) {
                    humanizedClick(mc.player.openContainer.windowId, 2, 0, ClickType.PICKUP);
                    craftState = CraftState.PLACE_NUGGET_STACK_2;
                    humanizeTimer.reset();
                    timer.reset();
                }
                break;

            case PLACE_NUGGET_STACK_2:
                if (mc.currentScreen instanceof CraftingScreen) {
                    if (currentNuggetIndex < nuggetSlots.size()) {
                        int slot = nuggetSlots.get(currentNuggetIndex);
                        ItemStack stack = mc.player.openContainer.getSlot(slot).getStack();

                        if (!stack.isEmpty() && stack.getCount() > 0) {
                            humanizedClick(mc.player.openContainer.windowId, slot, 0, ClickType.PICKUP);
                            currentNuggetIndex++;
                            humanizeTimer.reset();
                            craftState = CraftState.WAIT_BEFORE_NUGGET_3;
                            timer.reset();
                        } else {
                            currentNuggetIndex++;
                            timer.reset();
                        }
                    } else {
                        craftState = CraftState.FINISHED;
                        timer.reset();
                    }
                }
                break;

            case WAIT_BEFORE_NUGGET_3:
                if (humanizeTimer.finished(getRandomHumanPause())) {
                    humanizedClick(mc.player.openContainer.windowId, 4, 0, ClickType.PICKUP);
                    craftState = CraftState.PLACE_NUGGET_STACK_3;
                    humanizeTimer.reset();
                    timer.reset();
                }
                break;

            case PLACE_NUGGET_STACK_3:
                if (mc.currentScreen instanceof CraftingScreen) {
                    if (currentNuggetIndex < nuggetSlots.size()) {
                        int slot = nuggetSlots.get(currentNuggetIndex);
                        ItemStack stack = mc.player.openContainer.getSlot(slot).getStack();

                        if (!stack.isEmpty() && stack.getCount() > 0) {
                            humanizedClick(mc.player.openContainer.windowId, slot, 0, ClickType.PICKUP);
                            currentNuggetIndex++;
                            humanizeTimer.reset();
                            craftState = CraftState.WAIT_BEFORE_NUGGET_4;
                            timer.reset();
                        } else {
                            currentNuggetIndex++;
                            timer.reset();
                        }
                    } else {
                        craftState = CraftState.FINISHED;
                        timer.reset();
                    }
                }
                break;

            case WAIT_BEFORE_NUGGET_4:
                if (humanizeTimer.finished(getRandomHumanPause())) {
                    humanizedClick(mc.player.openContainer.windowId, 6, 0, ClickType.PICKUP);
                    craftState = CraftState.PLACE_NUGGET_STACK_4;
                    humanizeTimer.reset();
                    timer.reset();
                }
                break;

            case PLACE_NUGGET_STACK_4:
                if (mc.currentScreen instanceof CraftingScreen) {
                    if (currentNuggetIndex < nuggetSlots.size()) {
                        int slot = nuggetSlots.get(currentNuggetIndex);
                        ItemStack stack = mc.player.openContainer.getSlot(slot).getStack();

                        if (!stack.isEmpty() && stack.getCount() > 0) {
                            humanizedClick(mc.player.openContainer.windowId, slot, 0, ClickType.PICKUP);
                            currentNuggetIndex++;
                            humanizeTimer.reset();
                            craftState = CraftState.WAIT_BEFORE_RESULT;
                            timer.reset();
                        } else {
                            currentNuggetIndex++;
                            timer.reset();
                        }
                    } else {
                        craftState = CraftState.FINISHED;
                        timer.reset();
                    }
                }
                break;

            case WAIT_BEFORE_RESULT:
                if (humanizeTimer.finished(getRandomHumanPause())) {
                    humanizedClick(mc.player.openContainer.windowId, 8, 0, ClickType.PICKUP);
                    craftState = CraftState.TAKE_RESULT;
                    humanizeTimer.reset();
                    timer.reset();
                }
                break;

            case TAKE_RESULT:
                if (mc.currentScreen instanceof CraftingScreen) {
                    if (humanizeTimer.finished(getRandomHumanPause())) {
                        Slot resultSlot = mc.player.openContainer.getSlot(0);
                        if (!resultSlot.getStack().isEmpty()) {
                            humanizedClick(mc.player.openContainer.windowId, 0, 0, ClickType.QUICK_MOVE);
                            craftState = CraftState.WAIT_AFTER_RESULT;
                            humanizeTimer.reset();
                            timer.reset();
                        }
                    }
                }
                break;

            case WAIT_AFTER_RESULT:
                if (humanizeTimer.finished(getRandomHumanPause())) {
                    craftState = CraftState.CLEAR_GRID;
                    timer.reset();
                }
                break;

            case CLEAR_GRID:
                if (mc.currentScreen instanceof CraftingScreen) {
                    boolean hasItems = false;
                    for (int i = 1; i <= 9; i++) {
                        Slot slot = mc.player.openContainer.getSlot(i);
                        if (!slot.getStack().isEmpty()) {
                            humanizedClick(mc.player.openContainer.windowId, i, 0, ClickType.QUICK_MOVE);
                            hasItems = true;
                            break;
                        }
                    }
                    if (!hasItems) {
                        craftState = CraftState.WAIT_BEFORE_CHECK;
                        humanizeTimer.reset();
                    }
                    timer.reset();
                }
                break;

            case WAIT_BEFORE_CHECK:
                if (humanizeTimer.finished(getRandomHumanPause())) {
                    craftState = CraftState.CHECK_CONTINUE;
                    timer.reset();
                }
                break;

            case CHECK_CONTINUE:
                findItems();

                boolean hasEnoughNuggetsForCraft = hasEnoughGoldNuggets();
                boolean hasDiamonds = currentDiamondIndex < diamondSlots.size();

                if (hasDiamonds && hasEnoughNuggetsForCraft && currentNuggetIndex < nuggetSlots.size()) {
                    craftState = CraftState.WAIT_BEFORE_DIAMOND;
                    humanizeTimer.reset();
                    ChatUtil.addText("Продолжаем крафт");
                    timer.reset();
                } else if (!hasDiamonds) {
                    ChatUtil.addText("Алмазы закончились");
                    craftState = CraftState.FINISHED;
                    timer.reset();
                } else if (!hasEnoughNuggetsForCraft) {
                    ChatUtil.addText("Недостаточно самородков");
                    craftState = CraftState.FINISHED;
                    timer.reset();
                } else {
                    ChatUtil.addText("Материалы для крафта закончились");
                    craftState = CraftState.FINISHED;
                    timer.reset();
                }
                break;

            case FINISHED:
                mc.player.closeScreen();
                discardExcessNuggets();
                ChatUtil.addText("Крафт завершен");
                mainState = MainState.SELL_PHASE;
                sellState = SellState.FIND_ITEM;
                timer.reset();
                break;
        }
    }

    private void handleCharkaCraft() {
        long baseDelay = craftDelay;
        long humanDelay = getHumanizedDelay(baseDelay);

        if (!timer.finished(humanDelay)) return;

        switch (craftState) {
            case OPEN_CRAFT:
                if (!(mc.currentScreen instanceof CraftingScreen)) {
                    mc.player.sendChatMessage("/craft");
                    timer.reset();
                } else {
                    craftState = CraftState.FIND_APPLE_SLOT;
                    timer.reset();
                }
                break;

            case FIND_APPLE_SLOT:
                if (mc.currentScreen instanceof CraftingScreen) {
                    appleSlot = -1;
                    for (Slot slot : mc.player.openContainer.inventorySlots) {
                        if (slot.slotNumber < 10) continue;
                        ItemStack stack = slot.getStack();
                        if (!stack.isEmpty() && stack.getItem() == Items.APPLE && stack.getCount() >= 1) {
                            appleSlot = slot.slotNumber;
                            break;
                        }
                    }
                    if (appleSlot == -1) {
                        ChatUtil.addText("Нет яблок, возвращаемся к покупке");
                        mc.player.closeScreen();
                        mainState = MainState.BUY_PHASE;
                        buyState = BuyState.SEARCH_ITEM2;
                        timer.reset();
                        return;
                    }
                    craftState = CraftState.PICKUP_APPLE;
                    timer.reset();
                }
                break;

            case PICKUP_APPLE:
                if (mc.currentScreen instanceof CraftingScreen) {
                    humanizedClick(mc.player.openContainer.windowId, appleSlot, 0, ClickType.PICKUP);
                    craftState = CraftState.PLACE_APPLE;
                    humanizeTimer.reset();
                    timer.reset();
                }
                break;

            case PLACE_APPLE:
                if (mc.currentScreen instanceof CraftingScreen) {
                    if (humanizeTimer.finished(getRandomHumanPause())) {
                        humanizedClick(mc.player.openContainer.windowId, 5, 1, ClickType.PICKUP);
                        craftState = CraftState.PUT_BACK_APPLE;
                        humanizeTimer.reset();
                        timer.reset();
                    }
                }
                break;

            case PUT_BACK_APPLE:
                if (mc.currentScreen instanceof CraftingScreen) {
                    if (humanizeTimer.finished(getRandomHumanPause())) {
                        humanizedClick(mc.player.openContainer.windowId, appleSlot, 0, ClickType.PICKUP);
                        craftState = CraftState.FIND_GB_SLOT;
                        humanizeTimer.reset();
                        timer.reset();
                    }
                }
                break;

            case FIND_GB_SLOT:
                if (mc.currentScreen instanceof CraftingScreen) {
                    gbSlot = -1;
                    for (Slot slot : mc.player.openContainer.inventorySlots) {
                        if (slot.slotNumber < 10) continue;
                        ItemStack stack = slot.getStack();
                        if (!stack.isEmpty() && stack.getItem() == Items.GOLD_BLOCK && stack.getCount() >= 8) {
                            gbSlot = slot.slotNumber;
                            break;
                        }
                    }
                    if (gbSlot == -1) {
                        ChatUtil.addText("Нет золотых блоков, возвращаемся к покупке");
                        mc.player.closeScreen();
                        mainState = MainState.BUY_PHASE;
                        buyState = BuyState.SEARCH_ITEM1;
                        timer.reset();
                        return;
                    }
                    craftState = CraftState.PICKUP_GB;
                    timer.reset();
                }
                break;

            case PICKUP_GB:
                if (mc.currentScreen instanceof CraftingScreen) {
                    humanizedClick(mc.player.openContainer.windowId, gbSlot, 0, ClickType.PICKUP);
                    craftState = CraftState.PLACE_GB1;
                    timer.reset();
                }
                break;

            case PLACE_GB1:
                if (mc.currentScreen instanceof CraftingScreen) {
                    humanizedClick(mc.player.openContainer.windowId, 1, 1, ClickType.PICKUP);
                    craftState = CraftState.PLACE_GB2;
                    timer.reset();
                }
                break;

            case PLACE_GB2:
                if (mc.currentScreen instanceof CraftingScreen) {
                    humanizedClick(mc.player.openContainer.windowId, 2, 1, ClickType.PICKUP);
                    craftState = CraftState.PLACE_GB3;
                    timer.reset();
                }
                break;

            case PLACE_GB3:
                if (mc.currentScreen instanceof CraftingScreen) {
                    humanizedClick(mc.player.openContainer.windowId, 3, 1, ClickType.PICKUP);
                    craftState = CraftState.PLACE_GB4;
                    timer.reset();
                }
                break;

            case PLACE_GB4:
                if (mc.currentScreen instanceof CraftingScreen) {
                    humanizedClick(mc.player.openContainer.windowId, 4, 1, ClickType.PICKUP);
                    craftState = CraftState.PLACE_GB5;
                    timer.reset();
                }
                break;

            case PLACE_GB5:
                if (mc.currentScreen instanceof CraftingScreen) {
                    humanizedClick(mc.player.openContainer.windowId, 6, 1, ClickType.PICKUP);
                    craftState = CraftState.PLACE_GB6;
                    timer.reset();
                }
                break;

            case PLACE_GB6:
                if (mc.currentScreen instanceof CraftingScreen) {
                    humanizedClick(mc.player.openContainer.windowId, 7, 1, ClickType.PICKUP);
                    craftState = CraftState.PLACE_GB7;
                    timer.reset();
                }
                break;

            case PLACE_GB7:
                if (mc.currentScreen instanceof CraftingScreen) {
                    humanizedClick(mc.player.openContainer.windowId, 8, 1, ClickType.PICKUP);
                    craftState = CraftState.PLACE_GB8;
                    timer.reset();
                }
                break;

            case PLACE_GB8:
                if (mc.currentScreen instanceof CraftingScreen) {
                    humanizedClick(mc.player.openContainer.windowId, 9, 1, ClickType.PICKUP);
                    craftState = CraftState.TAKE_CHARKA;
                    timer.reset();
                }
                break;

            case TAKE_CHARKA:
                if (mc.currentScreen instanceof CraftingScreen) {
                    humanizedClick(mc.player.openContainer.windowId, 0, 0, ClickType.QUICK_MOVE);
                    craftState = CraftState.PUT_BACK_GB;
                    timer.reset();
                }
                break;

            case PUT_BACK_GB:
                if (mc.currentScreen instanceof CraftingScreen) {
                    humanizedClick(mc.player.openContainer.windowId, gbSlot, 0, ClickType.PICKUP);
                    craftState = CraftState.FINISHED;
                    timer.reset();
                }
                break;

            case FINISHED:
                mc.player.closeScreen();
                ChatUtil.addText("Крафт завершен");
                mainState = MainState.SELL_PHASE;
                sellState = SellState.FIND_ITEM;
                timer.reset();
                break;
        }
    }

    private void handleSellPhase() {
        if (sellState == SellState.RELIST) {
            handleRelistPhase();
            return;
        }

        if (sellPaused) return;

        switch (sellState) {
            case FIND_ITEM:
                if (!timer.finished(200 + random.nextInt(150))) return;

                boolean found = false;
                for (Slot slot : InvUtil.getMainInventorySlots()) {
                    ItemStack stack = slot.getStack();
                    if (stack.isEmpty()) continue;

                    if (stack.getItem() == sellItem) {
                        InvUtil.swapHand(slot, Hand.MAIN_HAND, false);
                        sellState = SellState.WAIT_CONFIRM;
                        timer.reset();
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    sellState = SellState.FINISHED;
                }
                break;

            case WAIT_CONFIRM:
                if (!timer.finished(400 + random.nextInt(200))) return;
                sellState = SellState.SELL_ITEM;
                timer.reset();
                break;

            case SELL_ITEM:
                int priceValue = (isExpFarm ? sellExpPrice : sellCharkaPrice) * 1000;
                mc.player.sendChatMessage("/ah sell " + priceValue);
                sellState = SellState.FIND_ITEM;
                timer.reset();
                break;

            case FINISHED:
                ChatUtil.addText("Продажа завершена");
                determineInitialState();
                timer.reset();
                break;
        }
    }

    private long getRandomHumanPause(int min, int max) {
        return min + random.nextInt(max - min);
    }

    private void handleRelistPhase() {
        long delay = 400 + random.nextInt(200) + random.nextInt(150);
        if (!timer.finished(delay)) return;

        switch (relistState) {
            case SEND_AH:
                mc.player.sendChatMessage("/ah");
                relistState = RelistState.CLICK_SLOT_46;
                timer.reset();
                break;

            case CLICK_SLOT_46:
                if (isAuctionOpened()) {
                    humanizedClick(mc.player.openContainer.windowId, 46, 0, ClickType.PICKUP);
                    relistState = RelistState.TAKE_FROM_SLOT_9;
                    currentRelistIndex = 0;
                    timer.reset();
                } else {
                    timer.reset();
                }
                break;

            case TAKE_FROM_SLOT_9:
                if (!isAuctionOpened()) {
                    relistState = RelistState.SEND_AH;
                    timer.reset();
                    return;
                }

                ChestContainer container = (ChestContainer) mc.player.openContainer;

                if (!timer.finished(450 + random.nextInt(150))) return;

                if (isExpFarm) {
                    ItemStack stack = container.getSlot(0).getStack();
                    if (!stack.isEmpty() && stack.getItem() == sellItem) {
                        humanizedClick(mc.player.openContainer.windowId, 0, 0, ClickType.QUICK_MOVE);
                        if (isInventoryFull()) {
                            relistState = RelistState.CLOSE_SCREEN;
                        }
                        timer.reset();
                    } else {
                        relistState = RelistState.CLOSE_SCREEN;
                        currentRelistIndex = 0;
                    }
                } else {
                    int nextSlot = -1;
                    for (int i = currentRelistIndex; i < 54; i++) {
                        ItemStack stack = container.getSlot(i).getStack();
                        if (!stack.isEmpty() && stack.getItem() == sellItem) {
                            nextSlot = i;
                            break;
                        }
                    }

                    if (nextSlot != -1) {
                        humanizedClick(mc.player.openContainer.windowId, nextSlot, 0, ClickType.QUICK_MOVE);
                        currentRelistIndex = nextSlot + 1;

                        if (isInventoryFull()) {
                            relistState = RelistState.CLOSE_SCREEN;
                        }
                        timer.reset();
                    } else {
                        relistState = RelistState.CLOSE_SCREEN;
                        currentRelistIndex = 0;
                    }
                }
                break;

            case CLOSE_SCREEN:
                mc.player.closeScreen();
                if (!isExpFarm) {
                    unstackEnchantedApples();
                }
                sellState = SellState.FIND_ITEM;
                sellPaused = false;
                ChatUtil.addText("Продолжаем продажу");
                timer.reset();
                break;
        }
    }

    private void unstackEnchantedApples() {
        mc.displayScreen(new InventoryScreen(mc.player));
        humanizeTimer.reset();

        if (mc.currentScreen instanceof InventoryScreen) {
            for (int slotId = 9; slotId <= 44; slotId++) {
                Slot slot = mc.player.openContainer.getSlot(slotId);
                if (slot.getHasStack() && slot.getStack().getItem() == Items.ENCHANTED_GOLDEN_APPLE && slot.getStack().getCount() > 1) {
                    int stackSize = slot.getStack().getCount();

                    humanizedClick(mc.player.openContainer.windowId, slotId, 0, ClickType.PICKUP);

                    int placed = 0;
                    for (int emptySlot = 9; emptySlot <= 44 && placed < stackSize - 1; emptySlot++) {
                        Slot empty = mc.player.openContainer.getSlot(emptySlot);
                        if (empty.getStack().isEmpty()) {
                            if (humanizeTimer.finished(getRandomHumanPause())) {
                                humanizedClick(mc.player.openContainer.windowId, emptySlot, 1, ClickType.PICKUP);
                                placed++;
                            }
                        }
                    }

                    humanizedClick(mc.player.openContainer.windowId, slotId, 0, ClickType.PICKUP);
                }
            }

            mc.player.closeScreen();
        }
    }

    private boolean isStorageInventory() {
        if (mc.currentScreen instanceof ChestScreen screen) {
            return screen.getTitle().getString().contains("хранилище");
        }
        return false;
    }

    private long getHumanizedDelay(long baseDelay) {
        double variance = 0.3 + (random.nextDouble() * 0.4);
        return (long) (baseDelay * variance) + random.nextInt(150);
    }

    private long getRandomHumanPause() {
        int baseMin = 80;
        int baseMax = 180;
        int extraVariation = random.nextInt(100);
        return baseMin + random.nextInt(baseMax - baseMin) + extraVariation;
    }

    private void humanizedClick(int windowId, int slotId, int mouseButton, ClickType clickType) {
        try {
            long microDelay = 10 + random.nextInt(30);
            Thread.sleep(microDelay);
        } catch (InterruptedException ignored) {
        }
        mc.playerController.windowClick(windowId, slotId, mouseButton, clickType, mc.player);
    }

    private void sleepRandom(int min, int max) {
        long delay = min + random.nextInt(max - min);
        humanizeTimer.reset();
        while (!humanizeTimer.finished(delay)) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException ignored) {
            }
        }
    }

    private void buyItem(net.minecraft.item.Item item, int maxPrice, int minPrice, int targetStacks, int boughtStacks, boolean isItem1, String itemName) {
        if (mc.currentScreen instanceof ChestScreen) {
            ChestContainer container = (ChestContainer) mc.player.openContainer;
            boolean purchased = false;

            int currentStacks = getStackCount(item);
            if (currentStacks >= targetStacks) {
                mc.player.closeScreen();
                if (isItem1) {
                    buyState = BuyState.SEARCH_ITEM2;
                    bought2 = 0;
                    ChatUtil.addText("Уже есть " + currentStacks + " стаков " + itemName + " в инвентаре, переходим ко второму");
                } else {
                    buyState = BuyState.FINISHED;
                    ChatUtil.addText("Уже есть " + currentStacks + " стаков " + itemName + " в инвентаре, покупка завершена");
                }
                timer.reset();
                return;
            }

            if (!isExpFarm && item == Items.APPLE && getTotalCount(Items.APPLE) > 0) {
                mc.player.closeScreen();
                buyState = BuyState.FINISHED;
                timer.reset();
                return;
            }

            for (Slot slot : container.inventorySlots) {
                if (slot.slotNumber > 44 || !slot.getHasStack() || slot.getStack().isEmpty()) {
                    continue;
                }

                ItemStack stack = slot.getStack();
                if (stack.getItem() != item || stack.getCount() != 64) {
                    continue;
                }

                int price = getPrice(stack);
                if (price == -1 || price < minPrice || price > maxPrice) {
                    continue;
                }

                currentStacks = getStackCount(item);
                if (currentStacks >= targetStacks) {
                    mc.player.closeScreen();
                    if (isItem1) {
                        buyState = BuyState.SEARCH_ITEM2;
                        bought2 = 0;
                        ChatUtil.addText("Достигнуто нужное количество " + itemName + ", переходим ко второму");
                    } else {
                        buyState = BuyState.FINISHED;
                        ChatUtil.addText("Достигнуто нужное количество " + itemName + ", покупка завершена");
                    }
                    timer.reset();
                    return;
                }

                InventoryUtility.clickSlotId(slot.slotNumber, 0, ClickType.QUICK_MOVE, false);
                sleepRandom(50, 100);
                ChatUtil.addText("Куплен " + itemName + " за " + price);
                if (isItem1) {
                    bought1++;
                } else {
                    bought2++;
                }
                purchased = true;
                timer.reset();
                break;
            }

            if (!purchased) {
                currentStacks = getStackCount(item);
                if (currentStacks >= targetStacks) {
                    mc.player.closeScreen();
                    if (isItem1) {
                        buyState = BuyState.SEARCH_ITEM2;
                        bought2 = 0;
                        ChatUtil.addText("Уже есть " + currentStacks + " стаков " + itemName + " в инвентаре, переходим ко второму");
                    } else {
                        buyState = BuyState.FINISHED;
                        ChatUtil.addText("Уже есть " + currentStacks + " стаков " + itemName + " в инвентаре, покупка завершена");
                    }
                    timer.reset();
                    return;
                }

                Slot updateSlot = container.getSlot(49);
                if (updateSlot != null && updateSlot.getHasStack() && updateSlot.getStack().getItem() == Items.NETHER_STAR) {
                    InventoryUtility.clickSlotId(49, 0, ClickType.PICKUP, false);
                    sleepRandom(50, 100);
                }
                timer.reset();
            }
        }
    }

    private void discardExcessNuggets() {
        int totalNuggets = 0;
        for (int i = 0; i < 36; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack.getItem() == Items.GOLD_NUGGET) {
                totalNuggets += stack.getCount();
            }
        }
        int totalStacks = totalNuggets / 64;
        int maxStacks = (totalStacks / 4) * 4;
        int keepNuggets = maxStacks * 64;
        int excess = totalNuggets - keepNuggets;
        if (excess > 0) {
            for (int i = 0; i < 36; i++) {
                ItemStack stack = mc.player.inventory.getStackInSlot(i);
                if (stack.getItem() == Items.GOLD_NUGGET && excess > 0) {
                    int dropCount = Math.min(stack.getCount(), excess);
                    InvUtil.throwItem(i + 9, dropCount);
                    excess -= dropCount;
                }
            }
        }
    }

    private void findItems() {
        diamondSlots.clear();
        nuggetSlots.clear();
        ingotSlots.clear();

        if (!(mc.currentScreen instanceof CraftingScreen)) return;

        for (Slot slot : mc.player.openContainer.inventorySlots) {
            if (slot.slotNumber < 10) continue;

            ItemStack stack = slot.getStack();
            if (stack.isEmpty()) continue;

            if (stack.getItem() == Items.DIAMOND) {
                diamondSlots.add(slot.slotNumber);
            }
            if (stack.getItem() == Items.GOLD_NUGGET) {
                nuggetSlots.add(slot.slotNumber);
            }
            if (stack.getItem() == Items.GOLD_INGOT) {
                ingotSlots.add(slot.slotNumber);
            }
        }
    }

    private int getStackCount(net.minecraft.item.Item item) {
        int count = 0;
        for (int i = 0; i < 36; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack.getItem() == item) {
                count += stack.getCount() / 64;
            }
        }
        return count;
    }

    private int getTotalCount(net.minecraft.item.Item item) {
        int count = 0;
        for (int i = 0; i < 36; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack.getItem() == item) {
                count += stack.getCount();
            }
        }
        return count;
    }

    private long getBuyDelay() {
        if ("SpookyTime".equals(serverMode.getValue())) {
            return 120;
        }
        return 850;
    }

    private int getPrice(ItemStack stack) {
        if ("SpookyTime".equals(serverMode.getValue())) {
            return getPriceSpookyTime(stack);
        }
        return getPriceFunTime(stack);
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

    private final Pattern funTimePricePattern = Pattern.compile("\\$(\\d{1,3}(?:,? ?\\d{3})*)");

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
                        try {
                            String priceString = matcher.group(1).replaceAll(",", "").replaceAll(" ", "");
                            if (!priceString.isEmpty()) {
                                return Integer.parseInt(priceString);
                            }
                        } catch (NumberFormatException ignored) {
                        }
                    }
                }
            }
        }
        return -1;
    }

    private boolean isAuctionOpened() {
        return mc.currentScreen instanceof ChestScreen;
    }

    private boolean isInventoryFull() {
        for (int i = 9; i <= 35; i++) {
            if (mc.player.inventory.getStackInSlot(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }
}