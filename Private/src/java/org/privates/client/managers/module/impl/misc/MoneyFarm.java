package org.privates.client.managers.module.impl.misc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.block.Blocks;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CPlayerTryUseItemOnBlockPacket;
import net.minecraft.network.play.server.SChatPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TextFormatting;
import org.privates.baritone.api.event.events.ChatEvent;
import org.privates.client.api.annotations.Category1;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.other.ContainerRenderEvent;
import org.privates.client.managers.events.other.PacketEvent;
import org.privates.client.managers.events.other.ScoreBoardEvent;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.chat.ChatUtil;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.player.InvUtil;
import org.privates.lib.util.time.StopWatch;

import java.util.ArrayList;
import java.util.List;

@Category1
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "MoneyFarm", category = Category.MISC, description = "Автоматическая ферма денег через изумрудные предметы")
public class MoneyFarm extends Module {

    public static MoneyFarm getInstance() {
        return Instance.get(MoneyFarm.class);
    }

    // Настройки
    private final ModeSetting itemType = new ModeSetting(this, "Предмет", "Меч", "Кирка", "Топор");
    private final SliderSetting price = new SliderSetting(this, "Цена", 25000, 15000, 50000, 1000);
    private final SliderSetting delay = new SliderSetting(this, "Задержка повторного выставления", 30, 10, 60, 1);
    private final SliderSetting auctionSlots = new SliderSetting(this, "Слоты аукциона", 10, 3, 50, 1);

    // Состояние модуля
    private int balance = 0;
    private State currentState = State.CHECKING_ITEMS;
    private final StopWatch actionTimer = new StopWatch();
    private final StopWatch sellTimer = new StopWatch();
    private final StopWatch buyTimer = new StopWatch();

    // Счетчики
    private int soldItems = 0;
    private int expectedSales = 0;
    private final List<String> soldMessages = new ArrayList<>();

    // Флаги состояния
    private boolean shopCommandSent = false;
    private boolean serverShopFound = false;
    private boolean isCrafting = false;
    private boolean isSellingPhase = false;
    private boolean isRemovingItems = false;
    private int removedItemsCount = 0;
    private int sellFailCount = 0; // Счетчик неудачных попыток продажи
    private int currentItemSlot = -1; // Текущий слот с предметом
    private BlockPos craftingTablePos = null;

    // Новые переменные для улучшенного крафта
    private boolean itemsPlaced = false; // Флаг размещения предметов
    private final StopWatch craftingTimer = new StopWatch(); // Таймер для крафта
    private int craftedItemsCount = 0; // Счетчик скрафченных предметов

    private enum State {
        CHECKING_EXISTING_SWORDS, // Проверка уже имеющихся предметов
        CHECKING_ITEMS,      // Проверка палок в инвентаре
        CHECKING_WORKBENCH,  // Проверка верстака рядом
        CHECKING_EMERALDS,   // Проверка изумрудов
        OPENING_SHOP,        // Открытие /shop
        BUYING_EMERALDS,     // Покупка изумрудов
        OPENING_WORKBENCH,   // Открытие верстака
        CRAFTING_SWORDS,     // Крафт предметов
        SELLING_SWORDS,      // Продажа предметов
        WAITING_FOR_SALES,   // Ожидание продаж
        REMOVING_UNSOLD      // Снятие непроданных предметов
    }

    @Override
    protected void onEnable() {
        currentState = State.CHECKING_EXISTING_SWORDS;
        balance = 0;
        soldItems = 0;
        expectedSales = 0;
        soldMessages.clear();
        shopCommandSent = false;
        serverShopFound = false;
        isCrafting = false;
        isSellingPhase = false;
        isRemovingItems = false;
        removedItemsCount = 0;
        sellFailCount = 0;
        currentItemSlot = -1;
        craftingTablePos = null;
        itemsPlaced = false;
        craftedItemsCount = 0;
        actionTimer.reset();
        sellTimer.reset();
        buyTimer.reset();
        craftingTimer.reset();

    }

    @Override
    protected void onDisable() {
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (!isEnabled()) return;
        if (mc.player == null || mc.world == null) return;

        // Основная логика работы модуля
        switch (currentState) {
            case CHECKING_EXISTING_SWORDS:
                checkExistingSwords();
                break;
            case CHECKING_ITEMS:
                checkSticks();
                break;
            case CHECKING_WORKBENCH:
                checkWorkbench();
                break;
            case CHECKING_EMERALDS:
                checkEmeralds();
                break;
            case OPENING_SHOP:
                if (!shopCommandSent && actionTimer.finished(300)) {
                    ChatUtil.sendText("/shop");
                    shopCommandSent = true;
                    serverShopFound = false;
                    currentState = State.BUYING_EMERALDS;
                    actionTimer.reset();
                }
                break;
            case OPENING_WORKBENCH:
                if (actionTimer.finished(300) && craftingTablePos != null) { // +2 сек
                    openWorkbench();
                    actionTimer.reset();
                }
                break;
            case CRAFTING_SWORDS:
                if (actionTimer.finished(300)) { // +2 сек
                    craftSwords();
                    actionTimer.reset();
                }
                break;
            case SELLING_SWORDS:
                if (actionTimer.finished(300)) { // +2 сек
                    sellSword();
                    actionTimer.reset();
                }
                break;
            case WAITING_FOR_SALES:
                checkSalesTimeout();
                break;
            case REMOVING_UNSOLD:
                if (actionTimer.finished(300)) { // +2 сек
                    ChatUtil.sendText("/ah me");
                    actionTimer.reset();
                }
                break;
        }
    }

    private void checkExistingSwords() {
        net.minecraft.item.Item currentItem = getCurrentItem();
        String itemName = getCurrentItemName();
        int itemCount = getItemCount(currentItem);

        if (itemCount > 0) {
            currentState = State.SELLING_SWORDS;
            isSellingPhase = true;
            expectedSales = Math.min(itemCount, auctionSlots.getValue().intValue());
            soldItems = 0;
            soldMessages.clear();
            sellFailCount = 0;
            sellTimer.reset();
            return;
        }
        currentState = State.CHECKING_ITEMS;
    }

    private void checkSticks() {
        int stickCount = getItemCount(Items.STICK);
        if (stickCount == 0) {
            ChatUtil.addText("Нужны палки для крафта!");
            setEnabled(false);
            return;
        }
        currentState = State.CHECKING_WORKBENCH;
    }

    private void checkWorkbench() {
        craftingTablePos = findNearbyWorkbench();
        if (craftingTablePos == null) {
            ChatUtil.addText("Рядом нет верстака!");
            setEnabled(false);
            return;
        }

        currentState = State.CHECKING_EMERALDS;
    }

    private void checkEmeralds() {
        int emeraldCount = getItemCount(Items.EMERALD);
        int stickCount = getItemCount(Items.STICK);
        int requiredEmeralds = getRequiredEmeralds();
        int requiredSticks = getRequiredSticks();
        net.minecraft.item.Item currentItem = getCurrentItem();
        int currentItemCount = getItemCount(currentItem);

        if (currentItemCount > 0) {
            currentState = State.SELLING_SWORDS;
            isSellingPhase = true;
            expectedSales = Math.min(currentItemCount, auctionSlots.getValue().intValue());
            soldItems = 0;
            soldMessages.clear();
            sellFailCount = 0;
            sellTimer.reset();
            return;
        }

        // ПРИОРИТЕТ 2: Если изумрудов 1 или меньше - покупаем на ВСЕ деньги
        if (emeraldCount <= 1) {
            if (balance >= 5000) {
                currentState = State.OPENING_SHOP;
                shopCommandSent = false;
                serverShopFound = false;
                actionTimer.reset();
                return;
            } else {
                actionTimer.reset();
                return;
            }
        }

        // ПРИОРИТЕТ 3: Если есть материалы для крафта - КРАФТИМ СРАЗУ!
        if (emeraldCount >= requiredEmeralds && stickCount >= requiredSticks) {
            isCrafting = false;
            itemsPlaced = false;
            craftedItemsCount = 0;
            craftingTimer.reset();
            currentState = State.OPENING_WORKBENCH;
            actionTimer.reset();
            return;
        }

        // ПРИОРИТЕТ 4: Если не хватает палок
        if (emeraldCount >= requiredEmeralds && stickCount < requiredSticks) {
            actionTimer.reset();
            return;
        }

        // ПРИОРИТЕТ 5: Если не хватает изумрудов но есть деньги - покупаем
        if (emeraldCount < requiredEmeralds && balance >= 5000) {
            currentState = State.OPENING_SHOP;
            shopCommandSent = false;
            serverShopFound = false;
            actionTimer.reset();
            return;
        }
        actionTimer.reset();
    }

    private void openWorkbench() {
        if (craftingTablePos != null) {

            // Закрываем любые открытые GUI
            if (mc.player.openContainer != mc.player.container) {
                mc.player.closeScreen();
            }

            // ПКМ по верстаку используя пакет
            BlockRayTraceResult result = new BlockRayTraceResult(
                    Vector3d.copyCentered(craftingTablePos),
                    Direction.UP,
                    craftingTablePos,
                    false
            );
            mc.player.connection.sendPacket(new CPlayerTryUseItemOnBlockPacket(Hand.MAIN_HAND, result));

            // Устанавливаем флаги
            currentState = State.CRAFTING_SWORDS;
            isCrafting = true;
            itemsPlaced = false;
            craftingTimer.reset();
        } else {

            currentState = State.CHECKING_WORKBENCH;
        }
    }

    private void craftSwords() {
        // Проверяем, что верстак открыт
        if (!isCrafting) {

            currentState = State.OPENING_WORKBENCH;
            actionTimer.reset();
            return;
        }

        // Проверяем материалы
        int emeraldCount = getItemCount(Items.EMERALD);
        int stickCount = getItemCount(Items.STICK);
        int requiredEmeralds = getRequiredEmeralds();
        int requiredSticks = getRequiredSticks();
        net.minecraft.item.Item currentItem = getCurrentItem();
        int itemCount = getItemCount(currentItem);
        int freeSlots = getFreeInventorySlots();


        // Если материалов не хватает - закрываем верстак и идем покупать/проверять
        if (emeraldCount < requiredEmeralds || stickCount < requiredSticks) {
            if (mc.player.openContainer != mc.player.container) {
                mc.player.closeScreen();
            }
            isCrafting = false;
            itemsPlaced = false;
            currentState = State.CHECKING_EMERALDS;
            actionTimer.reset();
            return;
        }

        // Если инвентарь заполнен или достаточно предметов - идем продавать
        if (freeSlots <= 2 || itemCount >= auctionSlots.getValue().intValue()) {
            if (mc.player.openContainer != mc.player.container) {
                mc.player.closeScreen();
            }
            isCrafting = false;
            itemsPlaced = false;
            currentState = State.SELLING_SWORDS;
            isSellingPhase = true;
            expectedSales = Math.min(itemCount, auctionSlots.getValue().intValue());
            soldItems = 0;
            soldMessages.clear();
            sellFailCount = 0;
            sellTimer.reset();
        }

        // Продолжаем крафт (логика в ContainerRenderEvent)
    }

    private void sellSword() {
        if (!isSellingPhase) return;

        // ВАЖНО: Закрываем любые открытые GUI перед продажей
        if (mc.player.openContainer != mc.player.container) {
            mc.player.closeScreen();
            actionTimer.reset();
            return;
        }

        net.minecraft.item.Item currentItem = getCurrentItem();
        int itemCount = getItemCount(currentItem);

        // Если предметов нет - возвращаемся к проверке
        if (itemCount == 0) {
            isSellingPhase = false;
            currentState = State.CHECKING_EMERALDS;
            return;
        }

        // Если слишком много неудачных попыток - пробуем другой слот
        if (sellFailCount >= 3) {
            currentItemSlot = findNextSwordSlot(currentItemSlot);
            sellFailCount = 0;

            if (currentItemSlot == -1) {
                isSellingPhase = false;
                currentState = State.CHECKING_EMERALDS;
                return;
            }
        }

        // Берем предмет в активный слот
        Slot itemSlot = findItemSlot();
        if (itemSlot == null) {
            isSellingPhase = false;
            currentState = State.CHECKING_EMERALDS;
            return;
        }

        // Перемещаем предмет в активный слот
        if (itemSlot.slotNumber != mc.player.inventory.currentItem) {
            InvUtil.swapHand(itemSlot, Hand.MAIN_HAND, false);
            currentItemSlot = itemSlot.slotNumber;
        }

        // Выставляем на аукцион
        if (soldItems < expectedSales) {
            ChatUtil.sendText("/ah sell " + price.getValue().intValue());
            soldItems++;

            if (soldItems >= expectedSales) {
                currentState = State.WAITING_FOR_SALES;
                sellTimer.reset();
            }
        }
    }

    private void checkSalesTimeout() {
        long delayMs = (long) (delay.getValue() * 1000);

        if (sellTimer.finished(delayMs)) {
            int actualSales = soldMessages.size();
            int expectedSalesCount = auctionSlots.getValue().intValue();

            if (actualSales < expectedSalesCount) {
                currentState = State.REMOVING_UNSOLD;
                actionTimer.reset();
            } else {
                resetSalesState();
                // ИСПРАВЛЕНИЕ: Сбрасываем флаги крафта перед возвратом к крафту
                isCrafting = false;
                itemsPlaced = false;
                craftedItemsCount = 0;
                craftingTimer.reset();
                currentState = State.CHECKING_EMERALDS;
            }
        }
    }

    @EventHandler
    public void onScoreBoard(ScoreBoardEvent event) {
        if (!isEnabled()) return;

        if (event.getList().isEmpty()) {
            balance = 0;
            return;
        }

        for (var pair : event.getList()) {
            String component = TextFormatting.removeFormatting(pair.getSecond().getString());
            if (component.contains("Монет:")) {
                String[] splitted = component.split(":");
                if (splitted.length > 1) {
                    try {
                        this.balance = Integer.parseInt(splitted[1].trim());
                        break;
                    } catch (NumberFormatException ignored) {
                        balance = 0;
                    }
                } else {
                    balance = 0;
                }
            }
        }
    }

    @EventHandler
    public void onPacket(PacketEvent event) {
        if (!isEnabled()) return;

        if (event.isReceive() && event.getPacket() instanceof SChatPacket chatPacket) {
            String message = chatPacket.getChatComponent().getString();
            String cleanMessage = TextFormatting.removeFormatting(message).trim();

            // Проверяем сообщение о необходимости освободить хранилище
            if (cleanMessage.contains("Освободите хранилище") || cleanMessage.contains("уберите предметы с продажи")) {
                currentState = State.REMOVING_UNSOLD;
                actionTimer.reset();
                return;
            }

            // УЛУЧШЕННАЯ обработка "воздуха"
            if (cleanMessage.contains("Вы не можете продать воздух!") || cleanMessage.contains("не можете продать пустоту")) {
                sellFailCount++;

                if (sellFailCount >= 3) {
                    currentItemSlot = findNextSwordSlot(currentItemSlot);
                    sellFailCount = 0;
                }

                // Сбрасываем счетчик проданных предметов, чтобы повторить попытку
                if (soldItems > 0) {
                    soldItems--;
                }
                currentState = State.SELLING_SWORDS;
                actionTimer.reset();
                return;
            }

            // УЛУЧШЕННАЯ проверка успешного выставления
            if (cleanMessage.contains("выставлен на продажу") || cleanMessage.contains("Предмет") && cleanMessage.contains("выставлен")) {
                sellFailCount = 0; // Сбрасываем счетчик неудач
                return;
            }

            // Проверяем сообщения о продаже (поддержка всех предметов)
            boolean isSaleMessage = false;
            String itemNameSingle = getCurrentItemNameSingle();

            if ((cleanMessage.contains("купили") && cleanMessage.contains("Изумрудный меч") && cleanMessage.contains("за")) ||
                    (cleanMessage.contains("купили") && cleanMessage.contains("Изумрудная кирка") && cleanMessage.contains("за")) ||
                    (cleanMessage.contains("купили") && cleanMessage.contains("Изумрудный топор") && cleanMessage.contains("за")) ||
                    (cleanMessage.contains("купили") && cleanMessage.contains("Diamond Sword") && cleanMessage.contains("за")) ||
                    (cleanMessage.contains("купили") && cleanMessage.contains("Diamond Pickaxe") && cleanMessage.contains("за")) ||
                    (cleanMessage.contains("купили") && cleanMessage.contains("Diamond Axe") && cleanMessage.contains("за"))) {
                isSaleMessage = true;
            }

            if (isSaleMessage) {
                soldMessages.add(cleanMessage);

                // СРАЗУ выставляем новый предмет если он есть в инвентаре
                net.minecraft.item.Item currentItem = getCurrentItem();
                int itemCount = getItemCount(currentItem);
                if (itemCount > 0 && soldItems < auctionSlots.getValue().intValue()) {
                    currentState = State.SELLING_SWORDS;
                    sellFailCount = 0;
                    actionTimer.reset();
                    return;
                }

                // Если предметов нет, но можем крафтить - идем крафтить
                if (itemCount == 0) {
                    int emeraldCount = getItemCount(Items.EMERALD);
                    int stickCount = getItemCount(Items.STICK);
                    int requiredEmeralds = getRequiredEmeralds();
                    int requiredSticks = getRequiredSticks();

                    if (emeraldCount >= requiredEmeralds && stickCount >= requiredSticks) {
                        resetSalesState();
                        // ИСПРАВЛЕНИЕ: Сбрасываем флаги крафта
                        isCrafting = false;
                        itemsPlaced = false;
                        craftedItemsCount = 0;
                        craftingTimer.reset();
                        currentState = State.OPENING_WORKBENCH;
                        actionTimer.reset();
                        return;
                    }
                }

                // Если все слоты заполнены, переходим в режим ожидания
                if (soldMessages.size() >= auctionSlots.getValue().intValue()) {
                    currentState = State.WAITING_FOR_SALES;
                    sellTimer.reset();
                }
            }
        }
    }

    @EventHandler
    public void onContainerRender(ContainerRenderEvent event) {
        if (!isEnabled()) return;

        Container container = event.getContainer();

        // Обработка магазина
        if (currentState == State.BUYING_EMERALDS) {
            handleShopInteraction(container);
        }

        // Обработка крафта
        if (currentState == State.CRAFTING_SWORDS && isCrafting) {
            handleCraftingInteraction(container);
        }

        // Обработка снятия с аукциона
        if (currentState == State.REMOVING_UNSOLD) {
            handleAuctionRemoval(container);
        }
    }

    private void handleShopInteraction(Container container) {
        if (buyTimer.finished(300)) { // 300мс задержка между действиями
            // Сначала ищем "магазин сервера" если еще не нашли
            if (!serverShopFound) {
                for (Slot slot : container.inventorySlots) {
                    ItemStack stack = slot.getStack();
                    if (!stack.isEmpty()) {
                        String displayName = stack.getDisplayName().getString().toLowerCase();
                        if (displayName.contains("магазин сервера") || displayName.contains("server shop") || stack.getItem() == Items.GOLD_INGOT) {
                            clickSlot(container.windowId, slot.slotNumber);
                            serverShopFound = true;
                            buyTimer.reset();
                            return;
                        }
                    }
                }
                buyTimer.reset();
                return;
            }

            // Если магазин сервера найден, ищем изумруды
            for (Slot slot : container.inventorySlots) {
                ItemStack stack = slot.getStack();
                if (!stack.isEmpty() && stack.getItem() == Items.EMERALD) {
                    int currentEmeralds = getItemCount(Items.EMERALD);

                    // ПОКУПАЕМ НА ВСЕ ДЕНЬГИ ПОКА ЕСТЬ ДЕНЬГИ!
                    if (balance >= 5000) {
                        clickSlot(container.windowId, slot.slotNumber);
                        buyTimer.reset();
                        return; // НЕ ЗАКРЫВАЕМ МАГАЗИН! Продолжаем покупать
                    } else {
                        // Денег больше нет - закрываем магазин
                        mc.player.closeScreen();
                        shopCommandSent = false;
                        serverShopFound = false;
                        currentState = State.CHECKING_EMERALDS;
                        actionTimer.reset();
                        buyTimer.reset();
                        return;
                    }
                }
            }

            buyTimer.reset();
        }
    }

    private void handleCraftingInteraction(Container container) {
        // Проверяем, что это верстак (3x3 крафт)
        if (container.inventorySlots.size() < 10) return;

        // Проверяем материалы
        int emeraldCount = getItemCount(Items.EMERALD);
        int stickCount = getItemCount(Items.STICK);
        net.minecraft.item.Item currentItem = getCurrentItem();
        String itemName = getCurrentItemName();
        String itemNameSingle = getCurrentItemNameSingle();
        int currentItems = getItemCount(currentItem);
        int freeSlots = getFreeInventorySlots();
        int requiredEmeralds = getRequiredEmeralds();
        int requiredSticks = getRequiredSticks();

        // Максимальное количество предметов, которое можем сделать из материалов
        int maxPossibleItems = Math.min(emeraldCount / requiredEmeralds, stickCount / requiredSticks);

        // Если материалов нет или достаточно предметов - закрываем верстак
        if (maxPossibleItems <= 0 || currentItems >= auctionSlots.getValue().intValue() || freeSlots <= 2) {
            mc.player.closeScreen();
            isCrafting = false;
            itemsPlaced = false;
            craftedItemsCount = 0;
            currentState = State.SELLING_SWORDS;
            isSellingPhase = true;
            expectedSales = Math.min(currentItems, auctionSlots.getValue().intValue());
            soldItems = 0;
            soldMessages.clear();
            sellFailCount = 0;
            sellTimer.reset();
            return;
        }

        // Этап 1: Размещение предметов с задержкой
        if (!itemsPlaced) {
            if (craftingTimer.finished(300)) { // Задержка 1 секунда на размещение предметов
                if (placeCraftingItems(container)) {
                    itemsPlaced = true;
                    craftingTimer.reset();
                }
            }
            return;
        }

        // Этап 2: Забираем результат с задержкой
        if (itemsPlaced && craftingTimer.finished(300)) { // Задержка 300мс перед забиранием результата
            Slot resultSlot = container.getSlot(0); // Результат крафта

            if (!resultSlot.getStack().isEmpty() && resultSlot.getStack().getItem() == currentItem) {
                // Забираем результат с Shift+клик чтобы сразу переместить в инвентарь
                clickSlotWithShift(container.windowId, 0);
                craftedItemsCount++;
                itemsPlaced = false; // Сбрасываем флаг для следующего крафта
                craftingTimer.reset();


                // Проверяем, нужно ли крафтить еще
                int newItemCount = getItemCount(currentItem);
                int newFreeSlots = getFreeInventorySlots();

                if (newFreeSlots <= 2 || newItemCount >= auctionSlots.getValue().intValue()) {
                    mc.player.closeScreen();
                    isCrafting = false;
                    itemsPlaced = false;
                    craftedItemsCount = 0;
                    currentState = State.SELLING_SWORDS;
                    isSellingPhase = true;
                    expectedSales = Math.min(newItemCount, auctionSlots.getValue().intValue());
                    soldItems = 0;
                    soldMessages.clear();
                    sellFailCount = 0;
                    sellTimer.reset();
                }
            } else {
                // Если результата нет, возможно нужно разместить предметы заново
                itemsPlaced = false;
                craftingTimer.reset();
            }
        }
    }

    private void handleAuctionRemoval(Container container) {
        if (!isRemovingItems) {
            // Начинаем процесс снятия предметов
            isRemovingItems = true;
            removedItemsCount = 0;
            actionTimer.reset();
            return;
        }

        // Проверяем, не сняли ли уже достаточно предметов
        int maxItemsToRemove = auctionSlots.getValue().intValue();
        if (removedItemsCount >= maxItemsToRemove) {
            mc.player.closeScreen();
            isRemovingItems = false;
            removedItemsCount = 0;

            // Проверяем есть ли предметы для выставления
            net.minecraft.item.Item currentItem = getCurrentItem();
            int itemCount = getItemCount(currentItem);
            if (itemCount > 0) {
                resetSalesState();
                currentState = State.SELLING_SWORDS;
                isSellingPhase = true;
                expectedSales = Math.min(itemCount, auctionSlots.getValue().intValue());
                sellTimer.reset();
            } else {
                // ИСПРАВЛЕНИЕ: Сбрасываем флаги крафта перед возвратом к крафту
                isCrafting = false;
                itemsPlaced = false;
                craftedItemsCount = 0;
                craftingTimer.reset();
                currentState = State.CHECKING_EMERALDS;
            }
            actionTimer.reset();
            return;
        }

        // Снимаем предметы с задержкой
        if (actionTimer.finished(300)) { // +2 сек задержка между кликами
            boolean foundItem = false;

            // Определяем количество слотов аукциона более точно
            int totalSlots = container.inventorySlots.size();
            int playerInventorySlots = 36; // 27 основных + 9 хотбар
            int auctionSlotsCount = Math.max(0, totalSlots - playerInventorySlots);

            // Ограничиваем поиск только слотами аукциона
            net.minecraft.item.Item currentItem = getCurrentItem();
            String itemNameSingle = getCurrentItemNameSingle();

            for (int i = 0; i < Math.min(auctionSlotsCount, 54); i++) { // Максимум 54 слота аукциона
                if (i >= container.inventorySlots.size()) break;

                Slot slot = container.getSlot(i);
                ItemStack stack = slot.getStack();
                if (!stack.isEmpty() && stack.getItem() == currentItem) {
                    clickSlot(container.windowId, slot.slotNumber);
                    removedItemsCount++;
                    foundItem = true;
                    actionTimer.reset();
                    return; // Снимаем по одному за раз
                }
            }

            // Если предметов больше нет в аукционе, закрываем
            if (!foundItem) {
                mc.player.closeScreen();
                isRemovingItems = false;
                removedItemsCount = 0;

                // Проверяем есть ли предметы для выставления
                int itemCount = getItemCount(currentItem);
                if (itemCount > 0) {
                    resetSalesState();
                    currentState = State.SELLING_SWORDS;
                    isSellingPhase = true;
                    expectedSales = Math.min(itemCount, auctionSlots.getValue().intValue());
                    sellTimer.reset();
                } else {
                    currentState = State.CHECKING_EMERALDS;
                }
                actionTimer.reset();
            }
        }
    }

    // Методы для работы с разными типами предметов
    private net.minecraft.item.Item getCurrentItem() {
        String type = itemType.getValue();
        switch (type) {
            case "Меч":
                return Items.DIAMOND_SWORD;
            case "Кирка":
                return Items.DIAMOND_PICKAXE;
            case "Топор":
                return Items.DIAMOND_AXE;
            default:
                return Items.DIAMOND_SWORD;
        }
    }

    private String getCurrentItemName() {
        String type = itemType.getValue();
        switch (type) {
            case "Меч":
                return "мечей";
            case "Кирка":
                return "кирок";
            case "Топор":
                return "топоров";
            default:
                return "мечей";
        }
    }

    private String getCurrentItemNameSingle() {
        String type = itemType.getValue();
        switch (type) {
            case "Меч":
                return "меч";
            case "Кирка":
                return "кирку";
            case "Топор":
                return "топор";
            default:
                return "меч";
        }
    }

    private boolean placeCraftingItems(Container container) {
        String type = itemType.getValue();

        switch (type) {
            case "Меч":
                // Изумрудный меч: изумруд в слот 2, изумруд в слот 5, палка в слот 8
                if (container.getSlot(2).getStack().isEmpty()) {
                    placeItemInCraftingSlot(Items.EMERALD, 2, container);
                    return false; // Еще не все предметы размещены
                }
                if (container.getSlot(5).getStack().isEmpty()) {
                    placeItemInCraftingSlot(Items.EMERALD, 5, container);
                    return false; // Еще не все предметы размещены
                }
                if (container.getSlot(8).getStack().isEmpty()) {
                    placeItemInCraftingSlot(Items.STICK, 8, container);
                    return false; // Еще не все предметы размещены
                }
                return true; // Все предметы размещены
            case "Кирка":
                // Изумрудная кирка: изумруды в слоты 1,2,3 и палки в слоты 5,8
                if (container.getSlot(1).getStack().isEmpty()) {
                    placeItemInCraftingSlot(Items.EMERALD, 1, container);
                    return false;
                }
                if (container.getSlot(2).getStack().isEmpty()) {
                    placeItemInCraftingSlot(Items.EMERALD, 2, container);
                    return false;
                }
                if (container.getSlot(3).getStack().isEmpty()) {
                    placeItemInCraftingSlot(Items.EMERALD, 3, container);
                    return false;
                }
                if (container.getSlot(5).getStack().isEmpty()) {
                    placeItemInCraftingSlot(Items.STICK, 5, container);
                    return false;
                }
                if (container.getSlot(8).getStack().isEmpty()) {
                    placeItemInCraftingSlot(Items.STICK, 8, container);
                    return false;
                }
                return true; // Все предметы размещены
            case "Топор":
                // Изумрудный топор: изумруды в слоты 1,2,4 и палки в слотах 5,8
                if (container.getSlot(1).getStack().isEmpty()) {
                    placeItemInCraftingSlot(Items.EMERALD, 1, container);
                    return false;
                }
                if (container.getSlot(2).getStack().isEmpty()) {
                    placeItemInCraftingSlot(Items.EMERALD, 2, container);
                    return false;
                }
                if (container.getSlot(4).getStack().isEmpty()) {
                    placeItemInCraftingSlot(Items.EMERALD, 4, container);
                    return false;
                }
                if (container.getSlot(5).getStack().isEmpty()) {
                    placeItemInCraftingSlot(Items.STICK, 5, container);
                    return false;
                }
                if (container.getSlot(8).getStack().isEmpty()) {
                    placeItemInCraftingSlot(Items.STICK, 8, container);
                    return false;
                }
                return true; // Все предметы размещены
        }
        return false;
    }


    private int getRequiredEmeralds() {
        String type = itemType.getValue();
        switch (type) {
            case "Меч":
                return 2; // 2 изумруда на меч
            case "Кирка":
                return 3; // 3 изумруда на кирку
            case "Топор":
                return 3; // 3 изумруда на топор
            default:
                return 2;
        }
    }

    private int getRequiredSticks() {
        String type = itemType.getValue();
        switch (type) {
            case "Меч":
                return 1; // 1 палка на меч
            case "Кирка":
                return 2; // 2 палки на кирку
            case "Топор":
                return 2; // 2 палки на топор
            default:
                return 1;
        }
    }

    // Вспомогательные методы
    private int getItemCount(net.minecraft.item.Item item) {
        int count = 0;
        for (int i = 0; i < mc.player.inventory.getSizeInventory(); i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() == item) {
                count += stack.getCount();
            }
        }
        return count;
    }

    private int getFreeInventorySlots() {
        int free = 0;
        for (int i = 9; i < 36; i++) { // Только основной инвентарь
            if (mc.player.inventory.getStackInSlot(i).isEmpty()) {
                free++;
            }
        }
        return free;
    }

    private BlockPos findNearbyWorkbench() {
        BlockPos playerPos = mc.player.getPosition();
        for (int x = -4; x <= 4; x++) {
            for (int y = -2; y <= 2; y++) {
                for (int z = -4; z <= 4; z++) {
                    BlockPos pos = playerPos.add(x, y, z);
                    if (mc.world.getBlockState(pos).getBlock() == Blocks.CRAFTING_TABLE) {
                        return pos;
                    }
                }
            }
        }
        return null;
    }

    private Slot findSwordSlot() {
        net.minecraft.item.Item currentItem = getCurrentItem();
        for (Slot slot : mc.player.container.inventorySlots) {
            ItemStack stack = slot.getStack();
            if (!stack.isEmpty() && stack.getItem() == currentItem) {
                return slot;
            }
        }
        return null;
    }

    private int findNextSwordSlot(int currentSlot) {
        net.minecraft.item.Item currentItem = getCurrentItem();
        for (int i = 9; i < 36; i++) { // Основной инвентарь
            if (i == currentSlot) continue; // Пропускаем текущий слот

            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() == currentItem) {
                return i;
            }
        }
        return -1; // Не найден
    }

    private void placeItemInCraftingSlot(net.minecraft.item.Item item, int craftingSlot, Container container) {
        // Ищем предмет в инвентаре
        for (int i = 10; i < container.inventorySlots.size(); i++) {
            Slot slot = container.getSlot(i);
            if (!slot.getStack().isEmpty() && slot.getStack().getItem() == item) {
                // Берем предмет (весь стак)
                clickSlot(container.windowId, i);
                // Кладем ОДИН предмет в слот крафта (ПКМ)
                clickSlotRightClick(container.windowId, craftingSlot);
                // Возвращаем остаток обратно в инвентарь
                clickSlot(container.windowId, i);
                break;
            }
        }
    }

    private void clickSlot(int windowId, int slotId) {
        if (mc.playerController != null && mc.player != null) {
            mc.playerController.windowClick(windowId, slotId, 0, ClickType.PICKUP, mc.player);
        }
    }

    private void clickSlotWithShift(int windowId, int slotId) {
        if (mc.playerController != null && mc.player != null) {
            mc.playerController.windowClick(windowId, slotId, 0, ClickType.QUICK_MOVE, mc.player);
        }
    }

    private void clickSlotRightClick(int windowId, int slotId) {
        if (mc.playerController != null && mc.player != null) {
            mc.playerController.windowClick(windowId, slotId, 1, ClickType.PICKUP, mc.player);
        }
    }

    private void resetSalesState() {
        isSellingPhase = false;
        soldItems = 0;
        expectedSales = 0;
        soldMessages.clear();
        sellFailCount = 0;
        currentItemSlot = -1;
    }

    @EventHandler
    public void onChat(ChatEvent event) {
        if (!isEnabled()) return;

        String message = event.getMessage().toLowerCase();

        // Определяем, что открылся магазин
        if (message.contains("магазин") || message.contains("shop")) {
            if (currentState == State.BUYING_EMERALDS) {
                buyTimer.reset();
            }
        }
    }

    private Slot findItemSlot() {
        net.minecraft.item.Item currentItem = getCurrentItem();
        for (Slot slot : mc.player.container.inventorySlots) {
            ItemStack stack = slot.getStack();
            if (!stack.isEmpty() && stack.getItem() == currentItem) {
                return slot;
            }
        }
        return null;
    }
}