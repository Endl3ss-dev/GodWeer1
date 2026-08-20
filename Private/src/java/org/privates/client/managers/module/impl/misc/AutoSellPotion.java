package org.privates.client.managers.module.impl.misc;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.inventory.ChestScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.play.server.SChatPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import org.privates.client.api.annotations.Category1;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.component.impl.rotation.Rotation;
import org.privates.client.managers.component.impl.rotation.RotationComponent;
import org.privates.client.managers.events.other.PacketEvent;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.chat.ChatUtil;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.player.InvUtil;
import org.privates.client.utils.player.InventoryUtility;
import org.privates.client.utils.player.PlayerUtil;
import org.privates.client.utils.rotation.RotationUtil;
import org.privates.lib.util.time.StopWatch;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
@Category1
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "AutoSellPotion", category = Category.MISC, description = "Автопродажа зелий с авто-покупкой")
public class AutoSellPotion extends Module {

    public static AutoSellPotion getInstance() {
        return Instance.get(AutoSellPotion.class);
    }

    private final ModeSetting itemType = new ModeSetting(this, "Тип предмета", "Зелья невидимости", "Эндер жемчуг");
    private final ModeSetting serverMode = new ModeSetting(this, "Режим сервера", "FunTime", "SpookyTime");
    private final SliderSetting sellPrice = new SliderSetting(this, "Цена продажи", 129, 5, 999, 1);
    private final SliderSetting buyMaxPrice = new SliderSetting(this, "Макс цена покупки", 100, 1, 100, 1);
    private final SliderSetting maxPotionStack = new SliderSetting(this, "Макс предметов в стаке", 64, 16, 64, 1);
    private final SliderSetting auctionSlots = new SliderSetting(this, "Слотов на аукционе", 3, 3, 15, 1);
    private final BooleanSetting autoUnstack = new BooleanSetting(this, "Авто расстак", true);
    private final BooleanSetting autoBuy = new BooleanSetting(this, "Авто покупка", true);

    private final StopWatch timer = new StopWatch();
    private final StopWatch humanizeTimer = new StopWatch();
    private final StopWatch timeClick = new StopWatch();
    private final Random random = new Random();

    enum MainState {CHECK_INVENTORY, BUY_PHASE, CHEST_DEPOSIT, SELL_PHASE, CHEST_WITHDRAW, FINISHED}

    enum BuyState {SEARCH_POTION, BUY_POTION, CLOSE_AH}

    enum SellState {UNSTACK_ITEMS, FIND_ITEM, WAIT_CONFIRM, SELL_ITEM, RELIST, FINISHED}

    enum RelistState {SEND_AH, CLICK_SLOT_46, TAKE_FROM_SLOT_9, CLOSE_SCREEN}

    enum UnstackState {OPEN_INVENTORY, PICKUP_STACK, PLACE_ITEMS, PUT_BACK_CURSOR, NEXT_STACK, CLOSE_INVENTORY}

    enum ChestState {FIND_CHEST, OPEN_CHEST, DEPOSIT_POTIONS, CLOSE_CHEST, WITHDRAW_POTIONS}

    private MainState mainState = MainState.CHECK_INVENTORY;
    private BuyState buyState = BuyState.SEARCH_POTION;
    private SellState sellState = SellState.UNSTACK_ITEMS;
    private RelistState relistState = RelistState.SEND_AH;
    private UnstackState unstackState = UnstackState.OPEN_INVENTORY;
    private ChestState chestState = ChestState.FIND_CHEST;

    private boolean sellPaused = false;
    private int currentRelistIndex = 0;
    private int currentUnstackSlot = -1;
    private int itemsPlaced = 0;
    private int totalItemsInCursor = 0;
    private int boughtPotions = 0;
    private BlockPos targetChest = null;
    private final Pattern funTimePricePattern = Pattern.compile("\\$(\\d{1,3}(?:,? ?\\d{3})*)");

    @Override
    public void onEnable() {
        super.onEnable();
        resetStates();
        mainState = MainState.CHECK_INVENTORY;
        timer.reset();
        humanizeTimer.reset();
        timeClick.reset();
        ChatUtil.addText("§a[AutoSellPotion] Запуск системы продажи и покупки: " + itemType.getValue());
    }

    @Override
    public void onDisable() {
        super.onDisable();
        ChatUtil.addText("§c[AutoSellPotion] Модуль остановлен");
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (mc.player == null || mc.world == null) return;

        switch (mainState) {
            case CHECK_INVENTORY -> handleCheckInventory();
            case BUY_PHASE -> handleBuyPhase();
            case CHEST_DEPOSIT -> handleChestDeposit();
            case SELL_PHASE -> handleSellPhase();
            case CHEST_WITHDRAW -> handleChestWithdraw();
            case FINISHED -> {
                ChatUtil.addText("§e[AutoSellPotion] Цикл завершён");
                toggle();
            }
        }
    }

    @EventHandler
    public void onChat(PacketEvent event) {
        if (!(event.getPacket() instanceof SChatPacket)) return;
        ITextComponent comp = ((SChatPacket) event.getPacket()).getChatComponent();
        String msg = comp.getString();

        if (mainState == MainState.SELL_PHASE && msg.contains("Освободите хранилище")) {
            sellPaused = true;
            sellState = SellState.RELIST;
            relistState = RelistState.SEND_AH;
            ChatUtil.addText("§6[AutoSellPotion] Перевыставление");
        }

        if (msg.contains("У Вас купили") && sellPaused) {
            sellPaused = false;
            ChatUtil.addText("§a[AutoSellPotion] Продолжаем продажу");
            timer.reset();
        }

        if (msg.contains("воздух")) handleAirSellError();
    }

    private void handleCheckInventory() {
        if (!timer.finished(200)) return;

        int itemCount = getTotalCount();

        if (itemCount == 0 && autoBuy.getValue()) {
            mainState = MainState.BUY_PHASE;
            buyState = BuyState.SEARCH_POTION;
            boughtPotions = 0;
            ChatUtil.addText("§b[AutoSellPotion] Предметы закончились, начинаем покупку");
        } else if (itemCount > 32) {
            mainState = MainState.CHEST_DEPOSIT;
            chestState = ChestState.FIND_CHEST;
            ChatUtil.addText("§d[AutoSellPotion] Найдено " + itemCount + " предметов, укладываем в сундук");
        } else if (itemCount > 0) {
            mainState = MainState.SELL_PHASE;
            if (autoUnstack.getValue()) {
                sellState = SellState.UNSTACK_ITEMS;
                unstackState = UnstackState.OPEN_INVENTORY;
            } else {
                sellState = SellState.FIND_ITEM;
            }
            ChatUtil.addText("§a[AutoSellPotion] Начинаем продажу " + itemCount + " предметов");
        } else {
            mainState = MainState.FINISHED;
        }
        timer.reset();
    }

    private void handleBuyPhase() {
        if (!timer.finished(getBuyDelay())) return;

        boolean isAuctionOpen = isAuctionOpened();

        switch (buyState) {
            case SEARCH_POTION -> {
                if (!isAuctionOpen) {
                    mc.player.sendChatMessage("/ah search " + getSearchQuery());
                    timer.reset();
                } else {
                    buyState = BuyState.BUY_POTION;
                    timer.reset();
                }
            }
            case BUY_POTION -> {
                if (!isAuctionOpened()) {
                    buyState = BuyState.SEARCH_POTION;
                    timer.reset();
                    return;
                }

                if (!(mc.player.openContainer instanceof ChestContainer container)) {
                    timer.reset();
                    return;
                }

                int maxPricePerItem = buyMaxPrice.getValue().intValue() * 1000;
                boolean foundAndBought = false;

                for (Slot slot : container.inventorySlots) {
                    if (slot.slotNumber > 44 || !slot.getHasStack() || slot.getStack().isEmpty()) continue;

                    ItemStack stack = slot.getStack();
                    if (!isTargetItem(stack)) continue;
                    if (stack.getCount() < 16) continue;

                    int totalPrice = getPrice(stack);
                    if (totalPrice == -1) continue;

                    int pricePerItem = totalPrice / stack.getCount();
                    if (pricePerItem > maxPricePerItem) continue;

                    InventoryUtility.clickSlotId(slot.slotNumber, 0, ClickType.QUICK_MOVE, false);
                    boughtPotions += stack.getCount();
                    sleepRandom(50, 100);
                    ChatUtil.addText("§a[AutoSellPotion] Куплено " + stack.getCount() + " предметов за " + pricePerItem + " каждый");
                    foundAndBought = true;
                    buyState = BuyState.CLOSE_AH;
                    timer.reset();
                    break;
                }

                if (!foundAndBought) {
                    Slot updateSlot = container.getSlot(49);
                    if (updateSlot != null && updateSlot.getHasStack() && updateSlot.getStack().getItem() == Items.NETHER_STAR) {
                        InventoryUtility.clickSlotId(49, 0, ClickType.PICKUP, false);
                        sleepRandom(50, 100);
                    }
                }
                timer.reset();
            }
            case CLOSE_AH -> {
                mc.player.closeScreen();
                mainState = MainState.CHECK_INVENTORY;
                ChatUtil.addText("§a[AutoSellPotion] Закупка завершена, куплено: " + boughtPotions);
                timer.reset();
            }
        }
    }

    private void handleChestDeposit() {
        if (!timer.finished(getRandomHumanPause())) return;

        switch (chestState) {
            case FIND_CHEST -> {
                targetChest = findNearestChest();
                if (targetChest == null) {
                    ChatUtil.addText("§c[AutoSellPotion] Сундук не найден, пропускаем депозит");
                    mainState = MainState.SELL_PHASE;
                    sellState = autoUnstack.getValue() ? SellState.UNSTACK_ITEMS : SellState.FIND_ITEM;
                } else {
                    chestState = ChestState.OPEN_CHEST;
                }
                timer.reset();
            }
            case OPEN_CHEST -> {
                if (!(mc.currentScreen instanceof ChestScreen)) {
                    Vector3d hitVec = new Vector3d(targetChest.getX() + 0.5, targetChest.getY() + 0.5, targetChest.getZ() + 0.5);
                    BlockRayTraceResult result = new BlockRayTraceResult(hitVec, Direction.UP, targetChest, false);

                    float[] rotation = RotationUtil.calculateAngle(Vector3d.copyCentered(targetChest).add(new Vector3d(Direction.UP.toVector3f()).mul(0.5)));
                    RotationComponent.update(new Rotation(rotation[0], rotation[1]), 360, 360, 1, 10);
                    mc.playerController.rightClickBlock(mc.player, mc.world, Hand.MAIN_HAND, result);
                    mc.player.swingArm(Hand.MAIN_HAND);
                    timer.reset();
                } else {
                    chestState = ChestState.DEPOSIT_POTIONS;
                    timer.reset();
                }
            }
            case DEPOSIT_POTIONS -> {
                if (!(mc.currentScreen instanceof ChestScreen)) {
                    chestState = ChestState.FIND_CHEST;
                    timer.reset();
                    return;
                }

                if (!(mc.player.openContainer instanceof ChestContainer container)) {
                    timer.reset();
                    return;
                }

                int currentCount = getTotalCount();
                if (currentCount <= 32) {
                    chestState = ChestState.CLOSE_CHEST;
                    timer.reset();
                    return;
                }

                if (timeClick.finished(100)) {
                    IInventory chestInv = container.getLowerChestInventory();
                    int chestSlots = chestInv.getSizeInventory();
                    int totalSlots = container.inventorySlots.size();

                    for (int i = chestSlots; i < totalSlots; i++) {
                        Slot slot = container.getSlot(i);
                        if (slot.getHasStack() && isTargetItem(slot.getStack())) {
                            mc.playerController.windowClick(container.windowId, slot.slotNumber, 0, ClickType.QUICK_MOVE, mc.player);
                            timeClick.reset();
                            timer.reset();
                            return;
                        }
                    }

                    chestState = ChestState.CLOSE_CHEST;
                    timer.reset();
                }
            }
            case CLOSE_CHEST -> {
                mc.player.closeScreen();
                mainState = MainState.SELL_PHASE;
                sellState = autoUnstack.getValue() ? SellState.UNSTACK_ITEMS : SellState.FIND_ITEM;
                ChatUtil.addText("§a[AutoSellPotion] Предметы уложены в сундук, начинаем продажу");
                timer.reset();
            }
        }
    }

    private void handleChestWithdraw() {
        if (!timer.finished(getRandomHumanPause())) return;

        switch (chestState) {
            case FIND_CHEST -> {
                targetChest = findNearestChest();
                if (targetChest == null) {
                    ChatUtil.addText("§c[AutoSellPotion] Сундук не найден");
                    mainState = MainState.FINISHED;
                } else {
                    chestState = ChestState.OPEN_CHEST;
                }
                timer.reset();
            }
            case OPEN_CHEST -> {
                if (!(mc.currentScreen instanceof ChestScreen)) {
                    Vector3d hitVec = new Vector3d(targetChest.getX() + 0.5, targetChest.getY() + 0.5, targetChest.getZ() + 0.5);
                    BlockRayTraceResult result = new BlockRayTraceResult(hitVec, Direction.UP, targetChest, false);

                    float[] rotation = RotationUtil.calculateAngle(Vector3d.copyCentered(targetChest).add(new Vector3d(Direction.UP.toVector3f()).mul(0.5)));
                    RotationComponent.update(new Rotation(rotation[0], rotation[1]), 360, 360, 1, 10);
                    mc.playerController.rightClickBlock(mc.player, mc.world, Hand.MAIN_HAND, result);
                    mc.player.swingArm(Hand.MAIN_HAND);
                    timer.reset();
                } else {
                    chestState = ChestState.WITHDRAW_POTIONS;
                    timer.reset();
                }
            }
            case WITHDRAW_POTIONS -> {
                if (!(mc.currentScreen instanceof ChestScreen)) {
                    chestState = ChestState.FIND_CHEST;
                    timer.reset();
                    return;
                }

                if (!(mc.player.openContainer instanceof ChestContainer container)) {
                    timer.reset();
                    return;
                }

                int slotsReserved = auctionSlots.getValue().intValue();
                int targetAmount = 32 - slotsReserved;
                int currentInv = getTotalCount();

                if (currentInv >= targetAmount) {
                    chestState = ChestState.CLOSE_CHEST;
                    timer.reset();
                    return;
                }

                if (timeClick.finished(100)) {
                    for (int i = 0; i < container.getLowerChestInventory().getSizeInventory(); i++) {
                        Slot slot = container.getSlot(i);
                        if (slot.getHasStack() && isTargetItem(slot.getStack())) {
                            mc.playerController.windowClick(container.windowId, i, 0, ClickType.QUICK_MOVE, mc.player);
                            timeClick.reset();
                            timer.reset();
                            return;
                        }
                    }
                }
            }
            case CLOSE_CHEST -> {
                mc.player.closeScreen();
                mainState = MainState.CHECK_INVENTORY;
                ChatUtil.addText("§a[AutoSellPotion] Предметы изъяты из сундука");
                timer.reset();
            }
        }
    }

    private void handleSellPhase() {
        if (sellState == SellState.RELIST) {
            handleRelistPhase();
            return;
        }

        if (sellPaused) return;

        switch (sellState) {
            case UNSTACK_ITEMS -> handleUnstackItems();
            case FIND_ITEM -> {
                if (!timer.finished(200 + random.nextInt(150))) return;

                boolean found = false;
                for (Slot slot : InvUtil.getMainInventorySlots()) {
                    ItemStack stack = slot.getStack();
                    if (stack.isEmpty() || !isTargetItem(stack)) continue;

                    InvUtil.swapHand(slot, Hand.MAIN_HAND, false);
                    sellState = SellState.WAIT_CONFIRM;
                    timer.reset();
                    found = true;
                    break;
                }

                if (!found) {
                    sellState = SellState.FINISHED;
                }
            }
            case WAIT_CONFIRM -> {
                if (!timer.finished(400 + random.nextInt(200))) return;
                sellState = SellState.SELL_ITEM;
                timer.reset();
            }
            case SELL_ITEM -> {
                int priceValue = sellPrice.getValue().intValue() * 1000;
                mc.player.sendChatMessage("/ah sell " + priceValue);
                sellState = SellState.FIND_ITEM;
                timer.reset();
            }
            case FINISHED -> {
                ChatUtil.addText("§e[AutoSellPotion] Все предметы проданы");
                if (autoBuy.getValue()) {
                    mainState = MainState.CHEST_WITHDRAW;
                    chestState = ChestState.FIND_CHEST;
                } else {
                    mainState = MainState.FINISHED;
                }
                timer.reset();
            }
        }
    }

    private void handleUnstackItems() {
        if (!timer.finished(getRandomHumanPause())) return;

        switch (unstackState) {
            case OPEN_INVENTORY -> {
                if (!(mc.currentScreen instanceof InventoryScreen)) {
                    mc.displayScreen(new InventoryScreen(mc.player));
                } else {
                    unstackState = UnstackState.PICKUP_STACK;
                }
                timer.reset();
            }
            case PICKUP_STACK -> {
                if (mc.currentScreen instanceof InventoryScreen) {
                    currentUnstackSlot = findNextStackedItem();
                    if (currentUnstackSlot == -1) {
                        unstackState = UnstackState.CLOSE_INVENTORY;
                        timer.reset();
                        return;
                    }

                    Slot slot = mc.player.openContainer.getSlot(currentUnstackSlot);
                    int stackSize = slot.getStack().getCount();
                    int emptySlots = countEmptySlots();

                    if (emptySlots < stackSize - 1) {
                        unstackState = UnstackState.CLOSE_INVENTORY;
                        ChatUtil.addText("§c[AutoSellPotion] Недостаточно места для расстака");
                        timer.reset();
                        return;
                    }

                    totalItemsInCursor = stackSize;
                    itemsPlaced = 0;
                    humanizedClick(mc.player.openContainer.windowId, currentUnstackSlot, 0, ClickType.PICKUP);
                    unstackState = UnstackState.PLACE_ITEMS;
                }
                timer.reset();
            }
            case PLACE_ITEMS -> {
                if (mc.currentScreen instanceof InventoryScreen) {
                    if (itemsPlaced >= totalItemsInCursor - 1) {
                        unstackState = UnstackState.PUT_BACK_CURSOR;
                        timer.reset();
                        return;
                    }

                    int freeSlot = findFreeSlot();
                    if (freeSlot != -1) {
                        humanizedClick(mc.player.openContainer.windowId, freeSlot, 1, ClickType.PICKUP);
                        itemsPlaced++;
                    } else {
                        InvUtil.throwItem(currentUnstackSlot, 1);
                        itemsPlaced++;
                    }
                }
                timer.reset();
            }
            case PUT_BACK_CURSOR -> {
                if (mc.currentScreen instanceof InventoryScreen) {
                    int freeSlot = findFreeSlot();
                    if (freeSlot != -1) {
                        humanizedClick(mc.player.openContainer.windowId, freeSlot, 0, ClickType.PICKUP);
                    } else {
                        humanizedClick(mc.player.openContainer.windowId, currentUnstackSlot, 0, ClickType.PICKUP);
                    }
                    unstackState = UnstackState.NEXT_STACK;
                }
                timer.reset();
            }
            case NEXT_STACK -> {
                currentUnstackSlot = -1;
                itemsPlaced = 0;
                totalItemsInCursor = 0;
                unstackState = UnstackState.PICKUP_STACK;
                timer.reset();
            }
            case CLOSE_INVENTORY -> {
                if (mc.currentScreen instanceof InventoryScreen) {
                    mc.player.closeScreen();
                    sellState = SellState.FIND_ITEM;
                    unstackState = UnstackState.OPEN_INVENTORY;
                    ChatUtil.addText("§a[AutoSellPotion] Расстак завершён");
                }
                timer.reset();
            }
        }
    }

    private void handleRelistPhase() {
        long delay = 400 + random.nextInt(200);
        if (!timer.finished(delay)) return;

        switch (relistState) {
            case SEND_AH -> {
                mc.player.sendChatMessage("/ah");
                relistState = RelistState.CLICK_SLOT_46;
                timer.reset();
            }
            case CLICK_SLOT_46 -> {
                if (isAuctionOpened()) {
                    humanizedClick(mc.player.openContainer.windowId, 46, 0, ClickType.PICKUP);
                    relistState = RelistState.TAKE_FROM_SLOT_9;
                    currentRelistIndex = 0;
                }
                timer.reset();
            }
            case TAKE_FROM_SLOT_9 -> {
                if (!isAuctionOpened()) {
                    relistState = RelistState.SEND_AH;
                    timer.reset();
                    return;
                }

                ChestContainer container = (ChestContainer) mc.player.openContainer;
                if (!timer.finished(450 + random.nextInt(150))) return;

                ItemStack stack = container.getSlot(0).getStack();
                if (!stack.isEmpty() && isTargetItem(stack)) {
                    humanizedClick(mc.player.openContainer.windowId, 0, 0, ClickType.QUICK_MOVE);
                    if (isInventoryFull()) relistState = RelistState.CLOSE_SCREEN;
                } else {
                    relistState = RelistState.CLOSE_SCREEN;
                    currentRelistIndex = 0;
                }
                timer.reset();
            }
            case CLOSE_SCREEN -> {
                mc.player.closeScreen();
                sellState = autoUnstack.getValue() ? SellState.UNSTACK_ITEMS : SellState.FIND_ITEM;
                sellPaused = false;
                ChatUtil.addText("§a[AutoSellPotion] Продолжаем продажу");
                timer.reset();
            }
        }
    }

    private BlockPos findNearestChest() {
        return PlayerUtil.getSphere(mc.player.getPosition(), 5, 5, false, true, 0, true).stream()
                .filter(pos -> {
                    Block block = mc.world.getBlockState(pos).getBlock();
                    return block == Blocks.CHEST || block == Blocks.ENDER_CHEST;
                })
                .min((p1, p2) -> Double.compare(
                        mc.player.getDistanceSq(p1.getX(), p1.getY(), p1.getZ()),
                        mc.player.getDistanceSq(p2.getX(), p2.getY(), p2.getZ())
                ))
                .orElse(null);
    }

    private boolean isTargetItem(ItemStack stack) {
        if (itemType.is("Зелья невидимости")) {
            if (stack.getItem() != Items.POTION && stack.getItem() != Items.SPLASH_POTION) return false;
            String displayName = stack.getDisplayName().getString().toLowerCase();
            return displayName.contains("невидимост") || displayName.contains("invisibility");
        } else if (itemType.is("Эндер жемчуг")) {
            return stack.getItem() == Items.ENDER_PEARL;
        }
        return false;
    }

    private String getSearchQuery() {
        if (itemType.is("Зелья невидимости")) {
            return "зелье невидимости";
        } else if (itemType.is("Эндер жемчуг")) {
            return "эндер жемчуг";
        }
        return "";
    }

    private int findNextStackedItem() {
        for (int slotId = 9; slotId <= 44; slotId++) {
            Slot slot = mc.player.openContainer.getSlot(slotId);
            if (slot.getHasStack() && isTargetItem(slot.getStack()) && slot.getStack().getCount() > 1) {
                return slotId;
            }
        }
        return -1;
    }

    private int findFreeSlot() {
        for (int i = 9; i <= 44; i++) {
            if (mc.player.openContainer.getSlot(i).getStack().isEmpty()) return i;
        }
        return -1;
    }

    private int countEmptySlots() {
        int count = 0;
        for (int i = 9; i <= 44; i++) {
            if (mc.player.openContainer.getSlot(i).getStack().isEmpty()) count++;
        }
        return count;
    }

    private int getTotalCount() {
        int count = 0;
        for (int i = 0; i < 36; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (isTargetItem(stack)) count += stack.getCount();
        }
        return count;
    }

    private void handleAirSellError() {
        mc.displayScreen(new InventoryScreen(mc.player));
        sleepRandom(100, 200);
        mc.player.closeScreen();
        sellState = SellState.FIND_ITEM;
        timer.reset();
    }

    private int getPrice(ItemStack stack) {
        if (serverMode.is("SpookyTime")) return getPriceSpookyTime(stack);
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

    private int getPriceFunTime(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        if (tag != null && tag.contains("display", 10)) {
            CompoundNBT display = tag.getCompound("display");
            if (display.contains("Lore", 9)) {
                ListNBT lore = display.getList("Lore", 8);
                for (int j = 0; j < lore.size(); ++j) {
                    String loreLine = TextFormatting.removeFormatting(lore.getString(j));
                    if (loreLine == null) continue;

                    Matcher matcher = funTimePricePattern.matcher(loreLine);
                    if (matcher.find()) {
                        try {
                            String priceString = matcher.group(1).replaceAll(",", "").replaceAll(" ", "");
                            if (!priceString.isEmpty()) return Integer.parseInt(priceString);
                        } catch (NumberFormatException ignored) {
                        }
                    }
                }
            }
        }
        return -1;
    }

    private long getBuyDelay() {
        return serverMode.is("SpookyTime") ? 120 : 850;
    }

    private long getRandomHumanPause() {
        int baseMin = 80;
        int baseMax = 180;
        int extraVariation = random.nextInt(100);
        return baseMin + random.nextInt(baseMax - baseMin) + extraVariation;
    }

    private void humanizedClick(int windowId, int slotId, int mouseButton, ClickType clickType) {
        try {
            Thread.sleep(10 + random.nextInt(30));
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

    private boolean isAuctionOpened() {
        return mc.currentScreen instanceof ChestScreen;
    }

    private boolean isInventoryFull() {
        for (int i = 9; i <= 35; i++) {
            if (mc.player.inventory.getStackInSlot(i).isEmpty()) return false;
        }
        return true;
    }

    private void resetStates() {
        mainState = MainState.CHECK_INVENTORY;
        buyState = BuyState.SEARCH_POTION;
        sellState = SellState.UNSTACK_ITEMS;
        relistState = RelistState.SEND_AH;
        unstackState = UnstackState.OPEN_INVENTORY;
        chestState = ChestState.FIND_CHEST;
        sellPaused = false;
        currentRelistIndex = 0;
        currentUnstackSlot = -1;
        itemsPlaced = 0;
        totalItemsInCursor = 0;
        boughtPotions = 0;
        targetChest = null;
    }
}