package org.privates.client.managers.module.impl.misc;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ChestScreen;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.glfw.GLFW;
import org.privates.client.api.annotations.Category1;
import org.privates.client.api.annotations.PVE;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BindSetting;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.chat.ChatUtil;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.player.InventoryUtility;
import org.privates.lib.util.time.StopWatch;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Category1
@PVE
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "AutoBuy", category = Category.MISC, description = "Авто покупка предметов на /ah для серверов funtime & spookytime")
public class AutoBuySpooky extends Module {

    private static final String THEME_COLOR = "§с";
    private static final String TEXT_COLOR = "§4";
    private static final String ACCENT_COLOR_1 = "§3";
    private static final String ACCENT_COLOR_2 = "§3";
    private static final String SUCCESS_COLOR = "§4";
    private static final String ERROR_COLOR = "§c";
    private static final String DEBUG_COLOR = "§3";

    public static AutoBuySpooky getInstance() {
        return Instance.get(AutoBuySpooky.class);
    }

    final ModeSetting serverMode = new ModeSetting(this, "Режим сервера", "SpookyTime", "FunTime");
    final SliderSetting funtimeReconnectDelay = new SliderSetting(this, "Перезаходить раз в (мин)", 2.5f, 0.5f, 10, 0.1f);
    final BindSetting openGui = new BindSetting(this, "Открыть GUI");
    final SliderSetting delay = new SliderSetting(this, "Задержка (мс)", 500, 100, 2000, 50);
    final BooleanSetting debugLogging = new BooleanSetting(this, "Не включать автобай работать не будет", (false));
    final SliderSetting parserDiscount = new SliderSetting(this, "Уменьшать цены на", 0, 0, 1, 0.0f);

    final BooleanSetting autoInventoryBuy = new BooleanSetting(this, "AutoInventoryBuy", false);
    final BooleanSetting useCrusherSword = new BooleanSetting(this, "Use Crusher Sword", true).setVisible(autoInventoryBuy::getValue);
    final BooleanSetting useCrusherPickaxe = new BooleanSetting(this, "Use Crusher Pickaxe", true).setVisible(autoInventoryBuy::getValue);
    final BooleanSetting useCrusherArmor = new BooleanSetting(this, "Use Crusher Armor", true).setVisible(autoInventoryBuy::getValue);
    final BooleanSetting useCrusherCrossbow = new BooleanSetting(this, "Use Crusher Crossbow", false).setVisible(autoInventoryBuy::getValue);
    final SliderSetting minElytraDurability = new SliderSetting(this, "Elytra Durability %", 75, 1, 100, 1).setVisible(autoInventoryBuy::getValue);
    final SliderSetting qtyEnchantedGapple = new SliderSetting(this, "EnchGapple Count", 16, 1, 256, 1).setVisible(autoInventoryBuy::getValue);
    final SliderSetting qtyGapple = new SliderSetting(this, "Gapple Count", 64, 1, 512, 1).setVisible(autoInventoryBuy::getValue);
    final SliderSetting qtyGoldenCarrot = new SliderSetting(this, "GoldenCarrot Count", 64, 1, 512, 1).setVisible(autoInventoryBuy::getValue);
    final SliderSetting qtyFirework = new SliderSetting(this, "Firework Count", 64, 1, 512, 1).setVisible(autoInventoryBuy::getValue);
    final SliderSetting qtyStrengthPotion = new SliderSetting(this, "Strength III Pots", 6, 0, 64, 1).setVisible(autoInventoryBuy::getValue);
    final SliderSetting qtySpeedPotion = new SliderSetting(this, "Speed III Pots", 6, 0, 64, 1).setVisible(autoInventoryBuy::getValue);
    final SliderSetting qtyInvisibilityPotion = new SliderSetting(this, "Invisibility Pots", 4, 0, 64, 1).setVisible(autoInventoryBuy::getValue);
    final SliderSetting qtyHealingPotion = new SliderSetting(this, "Healing Pots", 6, 0, 64, 1).setVisible(autoInventoryBuy::getValue);
    final SliderSetting qtyDisorientation = new SliderSetting(this, "Disorientation Count", 2, 0, 64, 1).setVisible(autoInventoryBuy::getValue);
    final SliderSetting qtyYavnayaDust = new SliderSetting(this, "Yavnaya Dust Count", 2, 0, 64, 1).setVisible(autoInventoryBuy::getValue);
    final SliderSetting qtyTrapka = new SliderSetting(this, "Trapka Count", 2, 0, 64, 1).setVisible(autoInventoryBuy::getValue);
    final SliderSetting qtyGodAura = new SliderSetting(this, "God Aura Count", 2, 0, 64, 1).setVisible(autoInventoryBuy::getValue);
    final SliderSetting qtyPlast = new SliderSetting(this, "Plast Count", 2, 0, 64, 1).setVisible(autoInventoryBuy::getValue);
    final SliderSetting qtyFreezeSnow = new SliderSetting(this, "Freeze Snow Count", 2, 0, 64, 1).setVisible(autoInventoryBuy::getValue);
    final SliderSetting qtyExpBottleStacks = new SliderSetting(this, "Exp Bottle Stacks", 2, 0, 12, 1).setVisible(autoInventoryBuy::getValue);
    final SliderSetting qtyShulker = new SliderSetting(this, "Shulker Count", 1, 1, 3, 1).setVisible(autoInventoryBuy::getValue);
    final BooleanSetting buyDonorPack = new BooleanSetting(this, "Buy Donor Potions/Spheres/Talismans", true).setVisible(autoInventoryBuy::getValue);

    final Path priceFile = Paths.get("autobuy_spooky.json");
    final Path itemsFile = Paths.get("autobuy_items_persistent.json");
    final Gson gson = new Gson();
    boolean wasGuiKeyPressed = false;
    public final List<AutoBuyItemData> itemsData = new CopyOnWriteArrayList<>();
    final StopWatch timer = new StopWatch();
    final StopWatch autoSaveTimer = new StopWatch();
    final StopWatch itemsSaveTimer = new StopWatch();
    final StopWatch reconnectTimer = new StopWatch();
    static final long AUTO_SAVE_INTERVAL = 30000;
    static final long ITEMS_SAVE_INTERVAL = 10000;
    private volatile boolean pricesModified = false;
    private volatile boolean itemsModified = false;
    private final Pattern funTimePricePattern = Pattern.compile("\\$(\\d{1,3}(?:,? ?\\d{3})*)");

    private final List<BuyedItem> persistentBuyedItems = new CopyOnWriteArrayList<>();
    private final Map<String, ItemStack> persistentSeenItems = new ConcurrentHashMap<>();
    private final Map<String, Long> itemLastSeenTime = new ConcurrentHashMap<>();
    private static final long ITEM_EXPIRE_TIME = 300000;
    private static final long DUPLICATE_TIME_THRESHOLD_MS = 2000;

    AutoBuyItemData itemWaitingForPrice = null;
    long priceCheckStartTime = 0;
    static final long PRICE_CHECK_TIMEOUT = 10000;

    private final List<String> reconnectCommands = new ArrayList<>();
    private final Random random = new Random();
    private static final long SEARCH_WAIT_MS = 3000L;
    private static final long SEARCH_RESEND_MS = 7000L;
    private static final int MAX_REFRESH_ATTEMPTS = 5;
    private long inventorySearchSentAt = 0L;
    private int inventoryRefreshAttempts = 0;
    private AutoBuyItemData activeInventoryTarget;
    private boolean inventorySearchSent = false;
    private final List<InventoryRequirement> inventoryRequirements = new ArrayList<>();

    public AutoBuySpooky() {
        initItemsData();
        initInventoryRequirements();
        initReconnectCommands();
        loadPrices();
        loadPersistentItems();
    }

    private void initReconnectCommands() {
        for (int i = 103; i <= 107; i++) reconnectCommands.add("/an" + i);
        for (int i = 8; i <= 11; i++) reconnectCommands.add("/an" + i);
        for (int i = 203; i <= 220; i++) reconnectCommands.add("/an" + i);
        for (int i = 303; i <= 308; i++) reconnectCommands.add("/an" + i);
        for (int i = 503; i <= 506; i++) reconnectCommands.add("/an" + i);
        for (int i = 603; i <= 605; i++) reconnectCommands.add("/an" + i);
    }

    @Override
    public void onEnable() {
        timer.reset();
        autoSaveTimer.reset();
        itemsSaveTimer.reset();
        reconnectTimer.reset();
        inventorySearchSent = false;
        inventoryRefreshAttempts = 0;
        activeInventoryTarget = null;
        ChatUtil.addText(THEME_COLOR + "[AutoBuy] " + SUCCESS_COLOR + "Модуль включен.");
    }

    @Override
    public void onDisable() {
        savePrices();
        savePersistentItems();
        ChatUtil.addText(THEME_COLOR + "[AutoBuy] " + ERROR_COLOR + "Модуль выключен.");
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (autoSaveTimer.finished(AUTO_SAVE_INTERVAL) && pricesModified) {
            savePrices();
        }

        if (itemsSaveTimer.finished(ITEMS_SAVE_INTERVAL) && itemsModified) {
            savePersistentItems();
        }

        cleanupExpiredItems();

        boolean isGuiKeyPressed = GLFW.glfwGetKey(Minecraft.getInstance().getMainWindow().getHandle(), openGui.getValue()) == GLFW.GLFW_PRESS;
        if (isGuiKeyPressed && !wasGuiKeyPressed && mc.currentScreen == null) {
            mc.displayScreen(new AutoBuySpookyGui());
        }
        wasGuiKeyPressed = isGuiKeyPressed;

        if (isEnabled() && serverMode.is("FunTime") && reconnectTimer.finished((long) (funtimeReconnectDelay.getValue().doubleValue() * 60 * 1000))) {
            if (mc.player != null) {
                mc.player.closeScreen();
                String randomCommand = reconnectCommands.get(random.nextInt(reconnectCommands.size()));
                mc.player.sendChatMessage(randomCommand);
                mc.player.sendChatMessage("/ah");
                reconnectTimer.reset();
                timer.reset();
                return;
            }
        }

        if (itemWaitingForPrice != null) {
            handlePriceCheck();
            return;
        }

        if (!autoInventoryBuy.getValue() && inventorySearchSent) {
            inventorySearchSent = false;
            inventoryRefreshAttempts = 0;
            activeInventoryTarget = null;
        }

        if (autoInventoryBuy.getValue()) {
            handleAutoInventoryBuy();
            return;
        }

        if (mc.player == null || mc.currentScreen == null || !isEnabled() || !timer.finished(delay.getValue().longValue())) {
            return;
        }

        if (isAuctionOpened()) {
            processAuction();
            timer.reset();
        }
    }

    private void cleanupExpiredItems() {
        long currentTime = System.currentTimeMillis();
        boolean removed = false;

        Iterator<Map.Entry<String, Long>> iterator = itemLastSeenTime.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Long> entry = iterator.next();
            if (currentTime - entry.getValue() > ITEM_EXPIRE_TIME) {
                iterator.remove();
                persistentSeenItems.remove(entry.getKey());
                removed = true;
            }
        }

        if (removed) {
            itemsModified = true;
        }
    }

    public void addBuyedItem(BuyedItem item) {
        for (BuyedItem existingItem : persistentBuyedItems) {
            boolean isSameItem = existingItem.abItem.displayName.equals(item.abItem.displayName);
            boolean isSamePrice = existingItem.price == item.price;
            boolean isRecent = Math.abs(Duration.between(existingItem.time, item.time).toMillis()) < DUPLICATE_TIME_THRESHOLD_MS;

            if (isSameItem && isSamePrice && isRecent) {
                return;
            }
        }

        persistentBuyedItems.add(item);
        itemsModified = true;
    }

    public void removeBuyedItem(BuyedItem item) {
        persistentBuyedItems.remove(item);
        itemsModified = true;
    }

    public void clearBuyedItems() {
        persistentBuyedItems.clear();
        itemsModified = true;
    }

    public List<BuyedItem> getBuyedItems() {
        return new ArrayList<>(persistentBuyedItems);
    }

    public void addSeenItem(String key, ItemStack item) {
        persistentSeenItems.put(key, item.copy());
        itemLastSeenTime.put(key, System.currentTimeMillis());
        itemsModified = true;
    }

    public Map<String, ItemStack> getSeenItems() {
        return new HashMap<>(persistentSeenItems);
    }

    public void startPriceCheck(AutoBuyItemData itemData) {
        if (itemWaitingForPrice != null) {
            ChatUtil.addText(THEME_COLOR + "[AutoBuy] " + ERROR_COLOR + "Предыдущая проверка цены еще не завершена.");
            return;
        }
        this.itemWaitingForPrice = itemData;
        this.priceCheckStartTime = System.currentTimeMillis();
        ChatUtil.addText(THEME_COLOR + "[AutoBuy] " + TEXT_COLOR + "Начинаю проверку цены для: " + ACCENT_COLOR_1 + itemData.displayName);
        mc.player.sendChatMessage("/ah search " + itemData.displayName);
    }

    private void handlePriceCheck() {
        if (System.currentTimeMillis() - priceCheckStartTime > PRICE_CHECK_TIMEOUT) {
            ChatUtil.addText(THEME_COLOR + "[AutoBuy] " + ERROR_COLOR + "Тайм-аут проверки цены для: " + ACCENT_COLOR_1 + itemWaitingForPrice.displayName);
            itemWaitingForPrice = null;
            return;
        }

        if (mc.currentScreen instanceof ChestScreen chestScreen) {
            if (chestScreen.getTitle().getString().contains("Аукцион")) {
                int minPrice = Integer.MAX_VALUE;
                boolean foundAnyItem = false;
                Container container = chestScreen.getContainer();

                for (int i = 0; i < container.inventorySlots.size(); i++) {
                    ItemStack stack = container.getSlot(i).getStack();
                    if (!stack.isEmpty()) {
                        int price = -1;
                        if (serverMode.is("SpookyTime")) {
                            price = getPriceSpookyTime(stack);
                        } else if (serverMode.is("FunTime")) {
                            price = getPriceFunTime(stack);
                        }

                        if (price > 0) {
                            foundAnyItem = true;
                            int pricePerItem = price / Math.max(1, stack.getCount());
                            if (pricePerItem < minPrice) {
                                minPrice = pricePerItem;
                            }
                        }
                    }
                }

                if (foundAnyItem) {
                    ChatUtil.addText(THEME_COLOR + "[AutoBuy] " + TEXT_COLOR + "Минимальная цена = " + ACCENT_COLOR_2 + minPrice + TEXT_COLOR + " монет.");
                    float discount = 1.0f - (parserDiscount.getValue().floatValue() / 100.0f);
                    int newPrice = (int) (minPrice * discount);
                    ChatUtil.addText(THEME_COLOR + "[AutoBuy] " + TEXT_COLOR + "Вычел -" + parserDiscount.getValue().floatValue() + "%. Новая цена = " + SUCCESS_COLOR + newPrice + TEXT_COLOR + " монет.");

                    itemWaitingForPrice.price = newPrice;
                    pricesModified = true;
                    savePrices();
                    ChatUtil.addText(String.format("%s[AutoBuy] %sЦена для %s%s%s установлена на %s%d",
                            THEME_COLOR, TEXT_COLOR, ACCENT_COLOR_1, itemWaitingForPrice.displayName, TEXT_COLOR, SUCCESS_COLOR, newPrice));
                } else {
                    ChatUtil.addText(THEME_COLOR + "[AutoBuy] " + ERROR_COLOR + "Не удалось найти лоты с ценой для: " + ACCENT_COLOR_1 + itemWaitingForPrice.displayName);
                }

                itemWaitingForPrice = null;
                mc.player.closeScreen();
                mc.displayScreen(new AutoBuySpookyGui());
            }
        }
    }

    private void initInventoryRequirements() {
        inventoryRequirements.clear();
        inventoryRequirements.add(new InventoryRequirement(
                () -> useCrusherArmor.getValue() ? getItemByDisplayName("INV Crusher Helmet") : getItemByDisplayName("INV Netherite Helmet"),
                () -> 1
        ));
        inventoryRequirements.add(new InventoryRequirement(
                () -> useCrusherArmor.getValue() ? getItemByDisplayName("INV Crusher Chestplate") : getItemByDisplayName("INV Netherite Chestplate"),
                () -> 1
        ));
        inventoryRequirements.add(new InventoryRequirement(
                () -> useCrusherArmor.getValue() ? getItemByDisplayName("INV Crusher Leggings") : getItemByDisplayName("INV Netherite Leggings"),
                () -> 1
        ));
        inventoryRequirements.add(new InventoryRequirement(
                () -> useCrusherArmor.getValue() ? getItemByDisplayName("INV Crusher Boots") : getItemByDisplayName("INV Netherite Boots"),
                () -> 1
        ));
        inventoryRequirements.add(new InventoryRequirement(
                () -> useCrusherSword.getValue() ? getItemByDisplayName("INV Crusher Sword") : getItemByDisplayName("INV Netherite Sword"),
                () -> 1
        ));
        inventoryRequirements.add(new InventoryRequirement(
                () -> useCrusherPickaxe.getValue() ? getItemByDisplayName("INV Crusher Pickaxe") : getItemByDisplayName("INV Netherite Pickaxe"),
                () -> 1
        ));
        inventoryRequirements.add(new InventoryRequirement(
                () -> getItemByDisplayName("INV Netherite Elytra"),
                () -> 1
        ));
        inventoryRequirements.add(new InventoryRequirement(
                () -> useCrusherCrossbow.getValue() ? getItemByDisplayName("INV Crusher Crossbow") : getItemByDisplayName("INV Crossbow"),
                () -> 1
        ));
        inventoryRequirements.add(new InventoryRequirement(
                () -> getItemByDisplayName("INV Bow"),
                () -> 1
        ));
        inventoryRequirements.add(new InventoryRequirement(
                () -> getItemByDisplayName("INV Enchanted Golden Apple"),
                () -> qtyEnchantedGapple.getValue().intValue()
        ));
        inventoryRequirements.add(new InventoryRequirement(
                () -> getItemByDisplayName("INV Golden Apple"),
                () -> qtyGapple.getValue().intValue()
        ));
        inventoryRequirements.add(new InventoryRequirement(
                () -> getItemByDisplayName("INV Golden Carrot"),
                () -> qtyGoldenCarrot.getValue().intValue()
        ));
        inventoryRequirements.add(new InventoryRequirement(
                () -> getItemByDisplayName("INV Firework Rocket"),
                () -> qtyFirework.getValue().intValue()
        ));
        inventoryRequirements.add(new InventoryRequirement(
                () -> getItemByDisplayName("INV Splash Strength III"),
                () -> qtyStrengthPotion.getValue().intValue()
        ));
        inventoryRequirements.add(new InventoryRequirement(
                () -> getItemByDisplayName("INV Splash Speed III"),
                () -> qtySpeedPotion.getValue().intValue()
        ));
        inventoryRequirements.add(new InventoryRequirement(
                () -> getItemByDisplayName("INV Splash Invisibility"),
                () -> qtyInvisibilityPotion.getValue().intValue()
        ));
        inventoryRequirements.add(new InventoryRequirement(
                () -> getItemByDisplayName("INV Splash Healing"),
                () -> qtyHealingPotion.getValue().intValue()
        ));
        inventoryRequirements.add(new InventoryRequirement(
                () -> getItemByDisplayName("INV Disorientation"),
                () -> qtyDisorientation.getValue().intValue()
        ));
        inventoryRequirements.add(new InventoryRequirement(
                () -> getItemByDisplayName("INV Yavnaya Dust"),
                () -> qtyYavnayaDust.getValue().intValue()
        ));
        inventoryRequirements.add(new InventoryRequirement(
                () -> getItemByDisplayName("INV Trapka"),
                () -> qtyTrapka.getValue().intValue()
        ));
        inventoryRequirements.add(new InventoryRequirement(
                () -> getItemByDisplayName("INV God Aura"),
                () -> qtyGodAura.getValue().intValue()
        ));
        inventoryRequirements.add(new InventoryRequirement(
                () -> getItemByDisplayName("INV Plast"),
                () -> qtyPlast.getValue().intValue()
        ));
        inventoryRequirements.add(new InventoryRequirement(
                () -> getItemByDisplayName("INV Freeze Snow"),
                () -> qtyFreezeSnow.getValue().intValue()
        ));
        inventoryRequirements.add(new InventoryRequirement(
                () -> getItemByDisplayName("INV Experience Bottle"),
                () -> qtyExpBottleStacks.getValue().intValue() * 64
        ));
        inventoryRequirements.add(new InventoryRequirement(
                () -> getItemByDisplayName("INV Shulker Box"),
                () -> qtyShulker.getValue().intValue()
        ));
    }

    private void handleAutoInventoryBuy() {
        if (mc.player == null || !isEnabled() || !timer.finished(delay.getValue().longValue())) {
            return;
        }

        InventoryRequirement requirement = getNextMissingRequirement();
        AutoBuyItemData target = requirement != null ? requirement.itemSupplier.get() : null;

        if (buyDonorPack.getValue()) {
            AutoBuyItemData donorItem = getMissingDonorItem();
            if (donorItem != null) {
                target = donorItem;
            }
        }

        if (target == null) {
            activeInventoryTarget = null;
            inventorySearchSent = false;
            return;
        }

        activeInventoryTarget = target;

        if (!(mc.currentScreen instanceof ChestScreen) || !isAuctionOpened()) {
            if (!inventorySearchSent || System.currentTimeMillis() - inventorySearchSentAt > SEARCH_RESEND_MS) {
                mc.player.sendChatMessage("/ah search " + getSearchQuery(target));
                inventorySearchSentAt = System.currentTimeMillis();
                inventorySearchSent = true;
                inventoryRefreshAttempts = 0;
            }
            return;
        }

        if (System.currentTimeMillis() - inventorySearchSentAt < SEARCH_WAIT_MS) {
            return;
        }

        if (buyBestAuctionLot(target)) {
            inventorySearchSent = false;
            inventoryRefreshAttempts = 0;
            timer.reset();
            return;
        }

        if (mc.player.openContainer instanceof ChestContainer chest) {
            Slot updateSlot = chest.getSlot(49);
            if (updateSlot != null && updateSlot.getHasStack() && updateSlot.getStack().getItem() == Items.NETHER_STAR) {
                InventoryUtility.clickSlotId(49, 0, ClickType.PICKUP, true);
                inventoryRefreshAttempts++;
            }
        }

        if (inventoryRefreshAttempts >= MAX_REFRESH_ATTEMPTS) {
            inventoryRefreshAttempts = 0;
            inventorySearchSent = false;
            mc.player.closeScreen();
        }

        timer.reset();
    }

    private boolean buyBestAuctionLot(AutoBuyItemData target) {
        if (!(mc.player.openContainer instanceof ChestContainer auctionContainer)) {
            return false;
        }

        String titleTLC = mc.currentScreen.getTitle().getString().toLowerCase();
        if (titleTLC.contains("РїРѕРґРѕР·СЂРёС‚РµР»СЊРЅР°СЏ С†РµРЅР°")) {
            if (buyWatch.finished(buyDelay)) {
                InventoryUtility.clickSlotId(0, 0, ClickType.QUICK_MOVE, true);
                buyWatch.reset();
            }
            return true;
        }

        Slot bestSlot = null;
        int bestPricePerUnit = Integer.MAX_VALUE;
        int bestStackCount = 0;

        for (Slot slot : auctionContainer.inventorySlots) {
            if (slot.slotNumber > 44 || !slot.getHasStack() || slot.getStack().isEmpty()) {
                continue;
            }

            ItemStack stack = slot.getStack();
            if (!matchesItemData(stack, target)) {
                continue;
            }

            int price = serverMode.is("SpookyTime") ? getPriceSpookyTime(stack) : getPriceFunTime(stack);
            if (price <= 0) {
                continue;
            }

            int unitPrice = price / Math.max(1, stack.getCount());
            if (target.price > 0 && unitPrice > target.price) {
                continue;
            }

            if (bestSlot == null || unitPrice < bestPricePerUnit || (unitPrice == bestPricePerUnit && stack.getCount() > bestStackCount)) {
                bestSlot = slot;
                bestPricePerUnit = unitPrice;
                bestStackCount = stack.getCount();
            }
        }

        if (bestSlot == null) {
            return false;
        }

        ItemStack boughtStack = bestSlot.getStack().copy();
        int countBefore = countMatchingInInventory(target);
        InventoryUtility.clickSlotId(bestSlot.slotNumber, 0, ClickType.QUICK_MOVE, true);
        addBuyedItem(new BuyedItem(
                boughtStack.copy(),
                boughtStack.copy(),
                bestPricePerUnit,
                boughtStack.getCount(),
                target,
                true,
                LocalDateTime.now()
        ));
        int countAfter = countMatchingInInventory(target);
        if (countAfter <= countBefore && debugLogging.getValue()) {
            ChatUtil.addText(THEME_COLOR + "[AutoInventoryBuy] " + ERROR_COLOR + "Предмет не добавился в инвентарь сразу, продолжаю проверку.");
        }
        return true;
    }

    private AutoBuyItemData getMissingDonorItem() {
        for (AutoBuyItemData item : itemsData) {
            if (item == null || item.item == null) {
                continue;
            }
            boolean donorSphereOrTalisman = item.spookyItemType != null
                    && (item.spookyItemType.startsWith("attribute-item-t") || item.spookyItemType.startsWith("attribute-item-s"));
            boolean donorPotion = item.potionEffects != null && !item.potionEffects.isEmpty() && item.item == Items.SPLASH_POTION;
            if (!(donorSphereOrTalisman || donorPotion)) {
                continue;
            }
            if (countMatchingInInventory(item) <= 0) {
                return item;
            }
        }
        return null;
    }

    private InventoryRequirement getNextMissingRequirement() {
        for (InventoryRequirement requirement : inventoryRequirements) {
            AutoBuyItemData item = requirement.itemSupplier.get();
            if (item == null) {
                continue;
            }
            int targetCount = Math.max(0, requirement.targetSupplier.getAsInt());
            if (targetCount <= 0) {
                continue;
            }
            if (countMatchingInInventory(item) < targetCount) {
                return requirement;
            }
        }
        return null;
    }

    private int countMatchingInInventory(AutoBuyItemData item) {
        if (mc.player == null) {
            return 0;
        }
        int count = 0;
        for (int i = 0; i < 36; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (!stack.isEmpty() && matchesItemData(stack, item)) {
                count += stack.getCount();
            }
        }
        return count;
    }

    private String getSearchQuery(AutoBuyItemData item) {
        if (item.searchAlias != null && !item.searchAlias.isEmpty()) {
            return item.searchAlias;
        }
        return item.displayName;
    }

    private AutoBuyItemData getItemByDisplayName(String displayName) {
        for (AutoBuyItemData data : itemsData) {
            if (data.displayName.equalsIgnoreCase(displayName)) {
                return data;
            }
        }
        return null;
    }

    private final StopWatch buyWatch = new StopWatch();
    protected long buyDelay = 700L;


    private void processAuction() {
        ChestContainer auctionContainer = (ChestContainer) mc.player.openContainer;
        boolean itemPurchasedInLoop = false;
        boolean hasSuspiciousPrice = false;
        String titleTLC = mc.currentScreen.getTitle().getString().toLowerCase();
        if (titleTLC.contains("подозрительная цена")) {
            if (buyWatch.finished(buyDelay)) {
                InventoryUtility.clickSlotId(0, 0, ClickType.QUICK_MOVE, true);
                buyWatch.reset();
            }
            return;
        }
        for (Slot slot : auctionContainer.inventorySlots) {
            if (slot.slotNumber > 44 || !slot.getHasStack() || slot.getStack().isEmpty()) {
                continue;
            }
            ItemStack stack = slot.getStack();
            String containerTitle = mc.currentScreen.getTitle().getString();
            int price = -1;
            if (serverMode.is("SpookyTime")) {
                price = getPriceSpookyTime(stack);
            } else if (serverMode.is("FunTime")) {
                price = getPriceFunTime(stack);
            }
            String displayName = stack.getDisplayName().getString();
            String itemKey = generateItemKey(stack);
            addSeenItem(itemKey, stack);
            if (price == -1) {
                if (debugLogging.getValue()) {
                    ChatUtil.addText(String.format("%s[Debug] %sВижу %s%s%s (с ценой все плохо мы дебилы)", DEBUG_COLOR, TEXT_COLOR, ACCENT_COLOR_1, displayName, TEXT_COLOR));
                }
                continue;
            }
            int finalPrice = price / Math.max(1, stack.getCount());
            AutoBuyItemData itemToBuy = findItemToBuy(stack);
            if (itemToBuy != null) {
                int limitPrice = itemToBuy.price;
                String message = String.format("%s[AutoBuy] %sВижу предмет %s%s%s, он в гуишке (цена: %s%d%s). продают за: %s%d",
                        THEME_COLOR, TEXT_COLOR, ACCENT_COLOR_1, itemToBuy.displayName, TEXT_COLOR, ACCENT_COLOR_2, limitPrice, TEXT_COLOR, ACCENT_COLOR_2, finalPrice);
                boolean shouldBuy = false;
                if (serverMode.is("SpookyTime")) {
                    if (limitPrice > 0 && finalPrice <= limitPrice) {
                        shouldBuy = true;
                    }
                } else if (serverMode.is("FunTime")) {
                    int minPriceToBuy = (int) (limitPrice * 0.41);
                    if (limitPrice > 0 && finalPrice <= limitPrice && finalPrice >= minPriceToBuy) {
                        shouldBuy = true;
                    }
                }
                if (shouldBuy) {
                    ChatUtil.addText(message + SUCCESS_COLOR + " [buy epta]");
                    ItemStack purchasedItem = stack.copy();
                    int countBefore = getItemCountInInventory(stack.getItem());
                    InventoryUtility.clickSlotId(slot.slotNumber, 0, ClickType.QUICK_MOVE, true);
                    new Thread(() -> {
                        try {
                            Thread.sleep(1000);
                            int countAfter = getItemCountInInventory(purchasedItem.getItem());
                            if (countAfter > countBefore) {
                                BuyedItem buyedItem = new BuyedItem(purchasedItem.copy(), purchasedItem.copy(),
                                        finalPrice, purchasedItem.getCount(), itemToBuy, true, LocalDateTime.now());
                                addBuyedItem(buyedItem);
                                ChatUtil.addText(THEME_COLOR + "[AutoBuy] " + SUCCESS_COLOR + "Предмет получен и добавлен в историю");
                            } else {
                                ChatUtil.addText(THEME_COLOR + "[AutoBuy] " + ERROR_COLOR + "Предмет не появился в инвентаре");
                            }
                        } catch (InterruptedException ignored) {
                        }
                    }).start();
                    itemPurchasedInLoop = true;
                    break;
                } else {
                    if (debugLogging.getValue()) {
                        ChatUtil.addText(message + ERROR_COLOR + " [не наша ценовая категория]");
                    }
                }
            } else {
                if (debugLogging.getValue()) {
                    ChatUtil.addText(String.format("%с[Debug] %sВижу %s%s%s (не настроен, цена: %s%d%s)", DEBUG_COLOR, TEXT_COLOR, ACCENT_COLOR_1, displayName, TEXT_COLOR, ACCENT_COLOR_2, finalPrice, TEXT_COLOR));
                }
            }
        }
        if (!itemPurchasedInLoop && !hasSuspiciousPrice) {
            Slot updateSlot = auctionContainer.getSlot(49);
            if (updateSlot != null && updateSlot.getHasStack() && updateSlot.getStack().getItem() == Items.NETHER_STAR) {
                InventoryUtility.clickSlotId(49, 0, ClickType.PICKUP, true);
            }
        }
    }

    private int getItemCountInInventory(net.minecraft.item.Item item) {
        int count = 0;
        for (int i = 0; i < 36; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack.getItem() == item) {
                count += stack.getCount();
            }
        }
        return count;
    }

    private String generateItemKey(ItemStack stack) {
        StringBuilder key = new StringBuilder();
        key.append(stack.getItem());
        key.append("_").append(stack.getDisplayName().getString());
        key.append("_").append(stack.getCount());
        if (stack.hasTag()) {
            key.append("_").append(stack.getTag().toString().hashCode());
        }
        return key.toString();
    }

    public AutoBuyItemData findItemToBuy(ItemStack stack) {
        for (AutoBuyItemData item : itemsData) {
            if (matchesItemData(stack, item)) {
                return item;
            }
        }
        return null;
    }

    private boolean matchesItemData(ItemStack stack, AutoBuyItemData item) {
        if (stack.getItem() != item.item) {
            return false;
        }

        int minDurability = item.minDurabilityPercentSupplier != null
                ? Math.max(0, item.minDurabilityPercentSupplier.get())
                : item.minDurabilityPercent;
        if (minDurability > 0) {
            if (!stack.isDamageable()) {
                return false;
            }
            int maxDamage = stack.getMaxDamage();
            int currentDamage = stack.getDamage();
            int durabilityPercent = (int) (((maxDamage - currentDamage) / (float) maxDamage) * 100.0f);
            if (durabilityPercent < minDurability) {
                return false;
            }
        }

        if (item.enchantmentRanges != null && !item.enchantmentRanges.isEmpty()) {
            if (!isEnchantedInRanges(stack, item.enchantmentRanges)) {
                return false;
            }
        } else if (item.enchantments != null && !item.enchantments.isEmpty()) {
            if (!isEnchantedCorrectly(stack, item.enchantments)) {
                return false;
            }
        }

        if (item.attributes != null && !item.attributes.isEmpty()) {
            if (!hasCorrectAttributes(stack, item.attributes)) {
                return false;
            }
        }

        if (item.potionEffects != null && !item.potionEffects.isEmpty()) {
            if (!hasPotionEffects(stack, item.potionEffects)) {
                return false;
            }
        }

        if (item.spookyItemType != null && !item.spookyItemType.isEmpty()) {
            if (item.spookyItemType.equals("bracket-check")) {
                return hasBracketInName(stack);
            }
            if (item.spookyItemType.startsWith("bracket-")) {
                String keyword = item.spookyItemType.substring(8);
                return hasBracketAndKeyword(stack, keyword);
            }
            String itemType = getSpookyItemType(stack);
            return itemType != null && itemType.equals(item.spookyItemType);
        }

        return true;
    }

    public void savePrices() {
        try {
            List<PriceEntry> dataToSave = new ArrayList<>();
            for (AutoBuyItemData item : itemsData) {
                if (item.price > 0) {
                    dataToSave.add(new PriceEntry(item.displayName, item.price));
                }
            }
            if (priceFile.getParent() != null && !Files.exists(priceFile.getParent())) {
                Files.createDirectories(priceFile.getParent());
            }
            String json = gson.toJson(dataToSave);
            Files.write(priceFile, json.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            pricesModified = false;
            autoSaveTimer.reset();
        } catch (IOException e) {
            ChatUtil.addText(THEME_COLOR + "[AutoBuy] " + ERROR_COLOR + "Ошибка сохранения цен: " + e.getMessage());
        }
    }

    public void savePersistentItems() {
        try {
            PersistentItemsData data = new PersistentItemsData();
            data.buyedItems = new ArrayList<>();
            data.seenItems = new HashMap<>();
            data.itemLastSeenTime = new HashMap<>(itemLastSeenTime);

            for (BuyedItem item : persistentBuyedItems) {
                SerializableBuyedItem serializable = new SerializableBuyedItem();
                serializable.displayName = item.abItem.displayName;
                serializable.price = item.price;
                serializable.count = item.count;
                serializable.buyed = item.buyed;
                serializable.time = item.time.toString();
                serializable.itemStackNBT = item.itemStack.hasTag() ? item.itemStack.getTag().toString() : "";
                serializable.itemType = item.itemStack.getItem().toString();
                data.buyedItems.add(serializable);
            }

            for (Map.Entry<String, ItemStack> entry : persistentSeenItems.entrySet()) {
                SerializableItemStack serializable = new SerializableItemStack();
                serializable.itemType = entry.getValue().getItem().toString();
                serializable.displayName = entry.getValue().getDisplayName().getString();
                serializable.count = entry.getValue().getCount();
                serializable.nbtData = entry.getValue().hasTag() ? entry.getValue().getTag().toString() : "";
                data.seenItems.put(entry.getKey(), serializable);
            }

            if (itemsFile.getParent() != null && !Files.exists(itemsFile.getParent())) {
                Files.createDirectories(itemsFile.getParent());
            }
            String json = gson.toJson(data);
            Files.write(itemsFile, json.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            itemsModified = false;
            itemsSaveTimer.reset();
        } catch (IOException e) {
            ChatUtil.addText(THEME_COLOR + "[AutoBuy] " + ERROR_COLOR + "Ошибка сохранения предметов: " + e.getMessage());
        }
    }

    public void loadPrices() {
        try {
            if (!Files.exists(priceFile)) return;
            String json = new String(Files.readAllBytes(priceFile));
            if (json.trim().isEmpty()) return;

            Type type = new TypeToken<List<PriceEntry>>() {
            }.getType();
            List<PriceEntry> loadedData = gson.fromJson(json, type);

            if (loadedData != null) {
                Map<String, AutoBuyItemData> itemMap = new HashMap<>();
                for (AutoBuyItemData item : itemsData) {
                    itemMap.put(item.displayName, item);
                }

                for (PriceEntry entry : loadedData) {
                    AutoBuyItemData itemToUpdate = itemMap.get(entry.displayName);
                    if (itemToUpdate != null) {
                        itemToUpdate.price = entry.price;
                    }
                }
            }
            pricesModified = false;
        } catch (Exception e) {
            ChatUtil.addText(THEME_COLOR + "[AutoBuy] " + ERROR_COLOR + "Ошибка загрузки цен: " + e.getMessage());
        }
    }

    public void loadPersistentItems() {
        try {
            if (!Files.exists(itemsFile)) return;
            String json = new String(Files.readAllBytes(itemsFile));
            if (json.trim().isEmpty()) return;

            PersistentItemsData data = gson.fromJson(json, PersistentItemsData.class);
            if (data != null) {
                if (data.buyedItems != null) {
                    persistentBuyedItems.clear();
                    Map<String, AutoBuyItemData> itemMap = new HashMap<>();
                    for (AutoBuyItemData item : itemsData) {
                        itemMap.put(item.displayName, item);
                    }

                    for (SerializableBuyedItem serializable : data.buyedItems) {
                        AutoBuyItemData abItem = itemMap.get(serializable.displayName);
                        if (abItem != null) {
                            try {
                                Item item = Registry.ITEM.getOrDefault(new ResourceLocation(serializable.itemType));
                                if (item == Items.AIR) continue;

                                ItemStack stack = new ItemStack(item, serializable.count);
                                if (serializable.itemStackNBT != null && !serializable.itemStackNBT.isEmpty()) {
                                    try {
                                        CompoundNBT nbt = JsonToNBT.getTagFromJson(serializable.itemStackNBT);
                                        stack.setTag(nbt);
                                    } catch (CommandSyntaxException ignored) {
                                    }
                                }
                                BuyedItem buyedItem = new BuyedItem(
                                        stack, stack.copy(), serializable.price, serializable.count,
                                        abItem, serializable.buyed, LocalDateTime.parse(serializable.time)
                                );
                                persistentBuyedItems.add(buyedItem);
                            } catch (Exception ignored) {
                            }
                        }
                    }
                }

                if (data.seenItems != null) {
                    persistentSeenItems.clear();
                    for (Map.Entry<String, SerializableItemStack> entry : data.seenItems.entrySet()) {
                        try {
                            Item item = Registry.ITEM.getOrDefault(new ResourceLocation(entry.getValue().itemType));
                            if (item == Items.AIR) continue;

                            ItemStack stack = new ItemStack(item, entry.getValue().count);
                            if (entry.getValue().nbtData != null && !entry.getValue().nbtData.isEmpty()) {
                                try {
                                    CompoundNBT nbt = JsonToNBT.getTagFromJson(entry.getValue().nbtData);
                                    stack.setTag(nbt);
                                } catch (CommandSyntaxException ignored) {
                                }
                            }
                            persistentSeenItems.put(entry.getKey(), stack);
                        } catch (Exception ignored) {
                        }
                    }
                }

                if (data.itemLastSeenTime != null) {
                    itemLastSeenTime.putAll(data.itemLastSeenTime);
                }
            }
            itemsModified = false;
        } catch (Exception e) {
            ChatUtil.addText(THEME_COLOR + "[AutoBuy] " + ERROR_COLOR + "Ошибка загрузки предметов: " + e.getMessage());
        }
    }


    public void setPriceForItem(int index, int price) {
        if (index >= 0 && index < itemsData.size()) {
            if (itemsData.get(index).price != price) {
                itemsData.get(index).price = price;
                pricesModified = true;
            }
        }
    }

    private boolean isAuctionOpened() {
        return mc.currentScreen instanceof ChestScreen && mc.currentScreen.getTitle().getString().toLowerCase().contains("аукцион");
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

    private void initItemsData() {
        itemsData.clear();

        Map<Enchantment, Integer> krushhelmet = Map.of(
                Enchantments.AQUA_AFFINITY, -1,
                Enchantments.BLAST_PROTECTION, 5,
                Enchantments.FIRE_PROTECTION, 5,
                Enchantments.MENDING, -1,
                Enchantments.PROJECTILE_PROTECTION, 5,
                Enchantments.PROTECTION, 5,
                Enchantments.RESPIRATION, 3,
                Enchantments.UNBREAKING, 5
        );

        Map<Enchantment, Integer> krushChestplateEnchants = Map.of(
                Enchantments.BLAST_PROTECTION, 5,
                Enchantments.FIRE_PROTECTION, 5,
                Enchantments.MENDING, -1,
                Enchantments.PROJECTILE_PROTECTION, 5,
                Enchantments.PROTECTION, 5,
                Enchantments.UNBREAKING, 5
        );

        Map<Enchantment, Integer> krushLegginsEnchants = Map.of(
                Enchantments.BLAST_PROTECTION, 5,
                Enchantments.FIRE_PROTECTION, 5,
                Enchantments.MENDING, -1,
                Enchantments.PROJECTILE_PROTECTION, 5,
                Enchantments.PROTECTION, 5,
                Enchantments.UNBREAKING, 5
        );

        Map<Enchantment, Integer> krushBootsEnchants = Map.of(
                Enchantments.BLAST_PROTECTION, 5,
                Enchantments.DEPTH_STRIDER, 3,
                Enchantments.FEATHER_FALLING, 4,
                Enchantments.FIRE_PROTECTION, 5,
                Enchantments.MENDING, -1,
                Enchantments.PROJECTILE_PROTECTION, 5,
                Enchantments.PROTECTION, 5,
                Enchantments.SOUL_SPEED, 3,
                Enchantments.UNBREAKING, 5
        );

        Map<Enchantment, Integer> krushSwordEnchants = Map.of(
                Enchantments.BANE_OF_ARTHROPODS, 7,
                Enchantments.FIRE_ASPECT, 2,
                Enchantments.LOOTING, 5,
                Enchantments.MENDING, -1,
                Enchantments.SHARPNESS, 7,
                Enchantments.SMITE, 7,
                Enchantments.SWEEPING, 3,
                Enchantments.UNBREAKING, 5
        );

        Map<Enchantment, Integer> krushTrebEnchants = Map.of(
                Enchantments.CHANNELING, -1,
                Enchantments.FIRE_ASPECT, 2,
                Enchantments.IMPALING, 5,
                Enchantments.LOYALTY, 3,
                Enchantments.MENDING, -1,
                Enchantments.SHARPNESS, 7,
                Enchantments.UNBREAKING, 5
        );

        Map<Enchantment, Integer> krushArbEnchants = Map.of(
                Enchantments.MENDING, -1,
                Enchantments.MULTISHOT, -1,
                Enchantments.PIERCING, 5,
                Enchantments.QUICK_CHARGE, 3,
                Enchantments.UNBREAKING, 3
        );

        Map<Enchantment, Integer> krushPickaxeEnchants = Map.of(
                Enchantments.EFFICIENCY, 10,
                Enchantments.FORTUNE, 5,
                Enchantments.MENDING, -1,
                Enchantments.UNBREAKING, 5
        );

        Map<Enchantment, Integer> krushElytraEnchants = Map.of(
                Enchantments.MENDING, -1,
                Enchantments.UNBREAKING, 5
        );

        itemsData.addAll(Arrays.asList(
                new AutoBuyItemData("Шлем Крушителя", Items.NETHERITE_HELMET, krushhelmet, null, ""),
                new AutoBuyItemData("Нагрудник Крушителя", Items.NETHERITE_CHESTPLATE, krushChestplateEnchants, null, ""),
                new AutoBuyItemData("Поножи Крушителя", Items.NETHERITE_LEGGINGS, krushLegginsEnchants, null, ""),
                new AutoBuyItemData("Ботинки Крушителя", Items.NETHERITE_BOOTS, krushBootsEnchants, null, ""),
                new AutoBuyItemData("Меч Крушителя", Items.NETHERITE_SWORD, krushSwordEnchants, null, ""),
                new AutoBuyItemData("Трезубец Крушителя", Items.TRIDENT, krushTrebEnchants, null, ""),
                new AutoBuyItemData("Арбалет Крушителя", Items.CROSSBOW, krushArbEnchants, null, ""),
                new AutoBuyItemData("Кирка Крушителя", Items.NETHERITE_PICKAXE, krushPickaxeEnchants, null, ""),
                new AutoBuyItemData("Элитры Крушителя", Items.ELYTRA, krushElytraEnchants, null, ""),
                new AutoBuyItemData("Талисман Карателя", Items.TOTEM_OF_UNDYING, null, createKaratelAttributes(), "attribute-item-tkaratela"),
                new AutoBuyItemData("Талисман Крушителя", Items.TOTEM_OF_UNDYING, null, createCrusherAttributes(), "attribute-item-tkryshitela"),
                new AutoBuyItemData("Талисман Раздора", Items.TOTEM_OF_UNDYING, null, createRazdorAttributes(), "attribute-item-trazdora"),
                new AutoBuyItemData("Талисман Тирана", Items.TOTEM_OF_UNDYING, null, createTiranaAttributes(), "attribute-item-ttirana"),
                new AutoBuyItemData("Талисман Ярости", Items.TOTEM_OF_UNDYING, null, createYarostiAttributes(), "attribute-item-tyarosti"),
                new AutoBuyItemData("Талисман Вихря", Items.TOTEM_OF_UNDYING, null, createVihryaAttributes(), "attribute-item-tvihrya"),
                new AutoBuyItemData("Талисман Мрака", Items.TOTEM_OF_UNDYING, null, createMrakaAttributes(), "attribute-item-tmraka"),
                new AutoBuyItemData("Талисман Демона", Items.TOTEM_OF_UNDYING, null, createDemonaAttributes(), "attribute-item-tdemona"),
                new AutoBuyItemData("Сфера Хаоса", Items.PLAYER_HEAD, null, createChaosAttributes(), "attribute-item-shaosa"),
                new AutoBuyItemData("Сфера Бестии", Items.PLAYER_HEAD, null, createBestiiAttributes(), "attribute-item-sbestii"),
                new AutoBuyItemData("Сфера Преса", Items.PLAYER_HEAD, null, createPresaAttributes(), "attribute-item-spresa"),
                new AutoBuyItemData("Сфера Гидры", Items.PLAYER_HEAD, null, createGidriAttributes(), "attribute-item-sgidri"),
                new AutoBuyItemData("Сфера Икара", Items.PLAYER_HEAD, null, createIkaraAttributes(), "attribute-item-sikara"),
                new AutoBuyItemData("Сфера Эрида", Items.PLAYER_HEAD, null, createEridaAttributes(), "attribute-item-serida"),
                new AutoBuyItemData("Пузырёк опыта", Items.EXPERIENCE_BOTTLE),
                new AutoBuyItemData("Чарка", Items.ENCHANTED_GOLDEN_APPLE),
                new AutoBuyItemData("Золотое яблоко", Items.GOLDEN_APPLE),
                new AutoBuyItemData("Яблоко", Items.APPLE),
                new AutoBuyItemData("Перка", Items.ENDER_PEARL),
                new AutoBuyItemData("Незеритовый слиток", Items.NETHERITE_INGOT),
                new AutoBuyItemData("Незеритовый лом", Items.NETHERITE_SCRAP),
                new AutoBuyItemData("Алмаз", Items.DIAMOND),
                new AutoBuyItemData("Изумруд", Items.EMERALD),
                new AutoBuyItemData("Золотой слиток", Items.GOLD_INGOT),
                new AutoBuyItemData("Алмазный блок", Items.DIAMOND_BLOCK),
                new AutoBuyItemData("Изумрудный блок", Items.EMERALD_BLOCK),
                new AutoBuyItemData("Золотой блок", Items.GOLD_BLOCK),
                new AutoBuyItemData("Обсидиан", Items.OBSIDIAN),
                new AutoBuyItemData("Голова дракона", Items.DRAGON_HEAD),
                new AutoBuyItemData("Голова визер-скелета", Items.WITHER_SKELETON_SKULL),
                new AutoBuyItemData("Древние обломки", Items.ANCIENT_DEBRIS),
                new AutoBuyItemData("Яйцо призыва крестьянина", Items.VILLAGER_SPAWN_EGG),
                new AutoBuyItemData("Яйцо зомби-крестьянина", Items.ZOMBIE_VILLAGER_SPAWN_EGG),
                new AutoBuyItemData("Тотем бессмертия", Items.TOTEM_OF_UNDYING),
                new AutoBuyItemData("Элитры", Items.ELYTRA),
                new AutoBuyItemData("Золотая морковь", Items.GOLDEN_CARROT),
                new AutoBuyItemData("Шалкер", Items.SHULKER_BOX),
                new AutoBuyItemData("Маяк", Items.BEACON),
                new AutoBuyItemData("Алмазная руда", Items.DIAMOND_ORE),
                new AutoBuyItemData("Изумрудная руда", Items.EMERALD_ORE),
                new AutoBuyItemData("Спавнер", Items.SPAWNER),
                new AutoBuyItemData("Порох", Items.GUNPOWDER),
                new AutoBuyItemData("Проклятая душа", Items.SOUL_LANTERN, null, null, "bracket-check"),
                new AutoBuyItemData("Трапка", Items.NETHERITE_SCRAP, null, null, "bracket-check"),
                new AutoBuyItemData("Дезориентация", Items.ENDER_EYE, null, null, "bracket-check"),
                new AutoBuyItemData("Явная пыль", Items.SUGAR, null, null, "bracket-check"),
                new AutoBuyItemData("Пласт", Items.DRIED_KELP, null, null, "bracket-check"),
                new AutoBuyItemData("Божья аура", Items.PHANTOM_MEMBRANE, null, null, "bracket-check"),
                new AutoBuyItemData("Динамит", Items.TNT),
                new AutoBuyItemData("Таер вайт", Items.TNT, null, null, "bracket-white"),
                new AutoBuyItemData("Таер блэк", Items.TNT, null, null, "bracket-black"),
                new AutoBuyItemData("Зелье Гнева", Items.SPLASH_POTION, createAngerPotionEffects()),
                new AutoBuyItemData("Зелье Паладина", Items.SPLASH_POTION, createPalladinPotionEffects()),
                new AutoBuyItemData("Зелье Ассасина", Items.SPLASH_POTION, createAssassinPotionEffects()),
                new AutoBuyItemData("Зелье Радиации", Items.SPLASH_POTION, createRadiationPotionEffects()),
                new AutoBuyItemData("Сноворное", Items.SPLASH_POTION, createCursedPotionEffects()),
                new AutoBuyItemData("Молот Тора", Items.NETHERITE_PICKAXE, null, null, "bracket-тора"),
                new AutoBuyItemData("Божье касание", Items.GOLDEN_PICKAXE, null, null, "bracket-божье"),
                new AutoBuyItemData("Мощный удар", Items.GOLDEN_PICKAXE, null, null, "bracket-удар"),
                new AutoBuyItemData("Книга починка", Items.ENCHANTED_BOOK, Map.of(Enchantments.MENDING, 1)),
                new AutoBuyItemData("Отмычка к сферам", Items.TRIPWIRE_HOOK, null, null, "bracket-сферам"),
                new AutoBuyItemData("Отмычка к броне", Items.TRIPWIRE_HOOK, null, null, "bracket-броне"),
                new AutoBuyItemData("Отмычка к оружию", Items.TRIPWIRE_HOOK, null, null, "bracket-оружию"),
                new AutoBuyItemData("Отмычка к инструментам", Items.TRIPWIRE_HOOK, null, null, "bracket-инструментам"),
                new AutoBuyItemData("Отмычка к ресурсам", Items.TRIPWIRE_HOOK, null, null, "bracket-ресурсам"),
                new AutoBuyItemData("Обычный мист", Items.CAMPFIRE, null, null, "MILD"),
                new AutoBuyItemData("Богатый мист", Items.CAMPFIRE, null, null, "WEAK"),
                new AutoBuyItemData("Легендарный мист", Items.CAMPFIRE, null, null, "1"),
                new AutoBuyItemData("Прогрузчик чанков 1x1", Items.STRUCTURE_BLOCK, null, null, "bracket-1x1"),
                new AutoBuyItemData("Прогрузчик чанков 2x2", Items.STRUCTURE_BLOCK, null, null, "bracket-2x2"),
                new AutoBuyItemData("Прогрузчик чанков 3x3", Items.STRUCTURE_BLOCK, null, null, "bracket-3x3"),
                new AutoBuyItemData("Дамагер", Items.JIGSAW, null, null, "bracket-check")
        ));
        addInventoryItems();
    }

    private void addInventoryItems() {
        Map<Enchantment, Integer> krushhelmet = Map.of(
                Enchantments.AQUA_AFFINITY, -1,
                Enchantments.BLAST_PROTECTION, 5,
                Enchantments.FIRE_PROTECTION, 5,
                Enchantments.MENDING, -1,
                Enchantments.PROJECTILE_PROTECTION, 5,
                Enchantments.PROTECTION, 5,
                Enchantments.RESPIRATION, 3,
                Enchantments.UNBREAKING, 5
        );
        Map<Enchantment, Integer> krushChestplateEnchants = Map.of(
                Enchantments.BLAST_PROTECTION, 5,
                Enchantments.FIRE_PROTECTION, 5,
                Enchantments.MENDING, -1,
                Enchantments.PROJECTILE_PROTECTION, 5,
                Enchantments.PROTECTION, 5,
                Enchantments.UNBREAKING, 5
        );
        Map<Enchantment, Integer> krushLegginsEnchants = Map.of(
                Enchantments.BLAST_PROTECTION, 5,
                Enchantments.FIRE_PROTECTION, 5,
                Enchantments.MENDING, -1,
                Enchantments.PROJECTILE_PROTECTION, 5,
                Enchantments.PROTECTION, 5,
                Enchantments.UNBREAKING, 5
        );
        Map<Enchantment, Integer> krushBootsEnchants = Map.of(
                Enchantments.BLAST_PROTECTION, 5,
                Enchantments.DEPTH_STRIDER, 3,
                Enchantments.FEATHER_FALLING, 4,
                Enchantments.FIRE_PROTECTION, 5,
                Enchantments.MENDING, -1,
                Enchantments.PROJECTILE_PROTECTION, 5,
                Enchantments.PROTECTION, 5,
                Enchantments.SOUL_SPEED, 3,
                Enchantments.UNBREAKING, 5
        );
        Map<Enchantment, Integer> krushSwordEnchants = Map.of(
                Enchantments.BANE_OF_ARTHROPODS, 7,
                Enchantments.FIRE_ASPECT, 2,
                Enchantments.LOOTING, 5,
                Enchantments.MENDING, -1,
                Enchantments.SHARPNESS, 7,
                Enchantments.SMITE, 7,
                Enchantments.SWEEPING, 3,
                Enchantments.UNBREAKING, 5
        );
        Map<Enchantment, Integer> krushArbEnchants = Map.of(
                Enchantments.MENDING, -1,
                Enchantments.MULTISHOT, -1,
                Enchantments.PIERCING, 5,
                Enchantments.QUICK_CHARGE, 3,
                Enchantments.UNBREAKING, 3
        );
        Map<Enchantment, Integer> krushPickaxeEnchants = Map.of(
                Enchantments.EFFICIENCY, 10,
                Enchantments.FORTUNE, 5,
                Enchantments.MENDING, -1,
                Enchantments.UNBREAKING, 5
        );
        itemsData.addAll(Arrays.asList(
                new AutoBuyItemData("INV Crusher Helmet", Items.NETHERITE_HELMET, krushhelmet, null, "").withSearchAlias("шлем крушителя"),
                new AutoBuyItemData("INV Crusher Chestplate", Items.NETHERITE_CHESTPLATE, krushChestplateEnchants, null, "").withSearchAlias("нагрудник крушителя"),
                new AutoBuyItemData("INV Crusher Leggings", Items.NETHERITE_LEGGINGS, krushLegginsEnchants, null, "").withSearchAlias("поножи крушителя"),
                new AutoBuyItemData("INV Crusher Boots", Items.NETHERITE_BOOTS, krushBootsEnchants, null, "").withSearchAlias("ботинки крушителя"),
                new AutoBuyItemData("INV Crusher Sword", Items.NETHERITE_SWORD, krushSwordEnchants, null, "").withSearchAlias("меч крушителя"),
                new AutoBuyItemData("INV Crusher Pickaxe", Items.NETHERITE_PICKAXE, krushPickaxeEnchants, null, "").withSearchAlias("кирка крушителя"),
                new AutoBuyItemData("INV Crusher Crossbow", Items.CROSSBOW, krushArbEnchants, null, "").withSearchAlias("арбалет крушителя"),
                new AutoBuyItemData("INV Netherite Helmet", Items.NETHERITE_HELMET)
                        .withSearchAlias("незеритовый шлем")
                        .withEnchantmentRanges(Map.of(
                                Enchantments.UNBREAKING, new EnchantRange(2, 5),
                                Enchantments.PROTECTION, new EnchantRange(4, 5),
                                Enchantments.MENDING, new EnchantRange(1, 1)
                        )),
                new AutoBuyItemData("INV Netherite Chestplate", Items.NETHERITE_CHESTPLATE)
                        .withSearchAlias("незеритовый нагрудник")
                        .withEnchantmentRanges(Map.of(
                                Enchantments.UNBREAKING, new EnchantRange(2, 5),
                                Enchantments.PROTECTION, new EnchantRange(4, 5),
                                Enchantments.MENDING, new EnchantRange(1, 1)
                        )),
                new AutoBuyItemData("INV Netherite Leggings", Items.NETHERITE_LEGGINGS)
                        .withSearchAlias("незеритовые поножи")
                        .withEnchantmentRanges(Map.of(
                                Enchantments.UNBREAKING, new EnchantRange(2, 5),
                                Enchantments.PROTECTION, new EnchantRange(4, 5),
                                Enchantments.MENDING, new EnchantRange(1, 1)
                        )),
                new AutoBuyItemData("INV Netherite Boots", Items.NETHERITE_BOOTS)
                        .withSearchAlias("незеритовые ботинки")
                        .withEnchantmentRanges(Map.of(
                                Enchantments.UNBREAKING, new EnchantRange(2, 5),
                                Enchantments.PROTECTION, new EnchantRange(4, 5),
                                Enchantments.MENDING, new EnchantRange(1, 1),
                                Enchantments.DEPTH_STRIDER, new EnchantRange(1, 3)
                        )),
                new AutoBuyItemData("INV Netherite Sword", Items.NETHERITE_SWORD)
                        .withSearchAlias("незеритовый меч")
                        .withEnchantmentRanges(Map.of(
                                Enchantments.SHARPNESS, new EnchantRange(5, 7),
                                Enchantments.FIRE_ASPECT, new EnchantRange(1, 3),
                                Enchantments.UNBREAKING, new EnchantRange(1, 5)
                        )),
                new AutoBuyItemData("INV Netherite Pickaxe", Items.NETHERITE_PICKAXE)
                        .withSearchAlias("незеритовая кирка")
                        .withEnchantmentRanges(Map.of(
                                Enchantments.EFFICIENCY, new EnchantRange(3, 10),
                                Enchantments.UNBREAKING, new EnchantRange(1, 5)
                        )),
                new AutoBuyItemData("INV Netherite Elytra", Items.ELYTRA, Map.of(Enchantments.UNBREAKING, -1), null, "")
                        .withSearchAlias("элитры")
                        .withMinDurabilitySupplier(() -> minElytraDurability.getValue().intValue()),
                new AutoBuyItemData("INV Crossbow", Items.CROSSBOW)
                        .withSearchAlias("арбалет")
                        .withEnchantmentRanges(Map.of(
                                Enchantments.QUICK_CHARGE, new EnchantRange(1, 5),
                                Enchantments.UNBREAKING, new EnchantRange(1, 5)
                        )),
                new AutoBuyItemData("INV Bow", Items.BOW)
                        .withSearchAlias("лук")
                        .withEnchantmentRanges(Map.of(
                                Enchantments.POWER, new EnchantRange(1, 5),
                                Enchantments.FLAME, new EnchantRange(1, 1),
                                Enchantments.UNBREAKING, new EnchantRange(1, 5)
                        )),
                new AutoBuyItemData("INV Enchanted Golden Apple", Items.ENCHANTED_GOLDEN_APPLE).withSearchAlias("зачарованное золотое яблоко"),
                new AutoBuyItemData("INV Golden Apple", Items.GOLDEN_APPLE).withSearchAlias("золотое яблоко"),
                new AutoBuyItemData("INV Golden Carrot", Items.GOLDEN_CARROT).withSearchAlias("золотая морковь"),
                new AutoBuyItemData("INV Firework Rocket", Items.FIREWORK_ROCKET).withSearchAlias("фейерверк"),
                new AutoBuyItemData("INV Experience Bottle", Items.EXPERIENCE_BOTTLE).withSearchAlias("пузырёк опыта"),
                new AutoBuyItemData("INV Shulker Box", Items.SHULKER_BOX).withSearchAlias("шалкер"),
                new AutoBuyItemData("INV Disorientation", Items.ENDER_EYE, null, null, "bracket-check").withSearchAlias("дезориентация"),
                new AutoBuyItemData("INV Yavnaya Dust", Items.SUGAR, null, null, "bracket-check").withSearchAlias("явная пыль"),
                new AutoBuyItemData("INV Trapka", Items.NETHERITE_SCRAP, null, null, "bracket-check").withSearchAlias("трапка"),
                new AutoBuyItemData("INV God Aura", Items.PHANTOM_MEMBRANE, null, null, "bracket-check").withSearchAlias("божья аура"),
                new AutoBuyItemData("INV Plast", Items.DRIED_KELP, null, null, "bracket-check").withSearchAlias("пласт"),
                new AutoBuyItemData("INV Freeze Snow", Items.SNOWBALL).withSearchAlias("снежок заморозки"),
                new AutoBuyItemData("INV Splash Strength III", Items.SPLASH_POTION, Arrays.asList(new PotionEffectMatcher(5, 2, -1))).withSearchAlias("зелье силы 3"),
                new AutoBuyItemData("INV Splash Speed III", Items.SPLASH_POTION, Arrays.asList(new PotionEffectMatcher(1, 2, -1))).withSearchAlias("зелье скорости 3"),
                new AutoBuyItemData("INV Splash Invisibility", Items.SPLASH_POTION, Arrays.asList(new PotionEffectMatcher(14, 0, -1))).withSearchAlias("зелье невидимости"),
                new AutoBuyItemData("INV Splash Healing", Items.SPLASH_POTION, Arrays.asList(new PotionEffectMatcher(6, 1, -1))).withSearchAlias("зелье исцеления")
        ));
    }

    private boolean isEnchantedCorrectly(ItemStack a, Map<Enchantment, Integer> b) {
        if (!a.isEnchanted()) return false;
        Map<Enchantment, Integer> c = EnchantmentHelper.getEnchantments(a);
        for (Map.Entry<Enchantment, Integer> d : b.entrySet()) {
            int e = c.getOrDefault(d.getKey(), 0);
            if (d.getValue() == -1) {
                if (e == 0) return false;
            } else {
                if (e < d.getValue()) return false;
            }
        }
        return true;
    }

    private boolean isEnchantedInRanges(ItemStack stack, Map<Enchantment, EnchantRange> ranges) {
        if (!stack.isEnchanted()) {
            return false;
        }
        Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(stack);
        for (Map.Entry<Enchantment, EnchantRange> entry : ranges.entrySet()) {
            int level = enchants.getOrDefault(entry.getKey(), 0);
            EnchantRange range = entry.getValue();
            if (level < range.min || level > range.max) {
                return false;
            }
        }
        return true;
    }

    private boolean hasBracketInName(ItemStack stack) {
        String displayName = stack.getDisplayName().getString();
        return displayName.contains("[") || displayName.contains("]");
    }

    private boolean hasBracketAndKeyword(ItemStack stack, String keyword) {
        String displayName = stack.getDisplayName().getString().toLowerCase();
        boolean hasBracket = displayName.contains("[") || displayName.contains("]");
        boolean hasKeyword = displayName.contains(keyword.toLowerCase());
        return hasBracket && hasKeyword;
    }

    private boolean hasCorrectAttributes(ItemStack a, Map<Attribute, Map.Entry<Float, AttributeModifier.Operation>> b) {
        if (!a.hasTag()) return false;
        Map<Attribute, Map.Entry<Float, AttributeModifier.Operation>> c = getAttributes(a);
        return c != null && !c.isEmpty() && c.equals(b);
    }

    private boolean hasPotionEffects(ItemStack a, List<PotionEffectMatcher> b) {
        List<EffectInstance> c = PotionUtils.getFullEffectsFromItem(a);
        if (c.size() != b.size()) return false;
        for (PotionEffectMatcher d : b) {
            boolean e = false;
            for (EffectInstance f : c) {
                int g = Effect.getId(f.getPotion());
                int h = f.getAmplifier();
                int i = f.getDuration() / 20;
                if (g == d.id && h == d.amplifier) {
                    if (d.duration == -1 || i == d.duration) {
                        e = true;
                        break;
                    }
                }
            }
            if (!e) return false;
        }
        return true;
    }

    private String getSpookyItemType(ItemStack a) {
        if (!a.hasTag()) return null;
        CompoundNBT b = a.getTag();
        return b.contains("don-item") ? b.getString("don-item") : null;
    }

    private Map<Attribute, Map.Entry<Float, AttributeModifier.Operation>> getAttributes(ItemStack a) {
        if (!a.hasTag() || !a.getTag().contains("AttributeModifiers", 9)) {
            return Collections.emptyMap();
        }
        Map<Attribute, Map.Entry<Float, AttributeModifier.Operation>> c = new HashMap<>();
        ListNBT d = a.getTag().getList("AttributeModifiers", 10);
        for (int i = 0; i < d.size(); ++i) {
            CompoundNBT e = d.getCompound(i);
            String f = e.getString("AttributeName");
            Optional<Attribute> g = Registry.ATTRIBUTE.getOptional(new ResourceLocation(f));
            if (g.isPresent()) {
                Attribute h = g.get();
                float j = e.getFloat("Amount");
                int k = e.getInt("Operation");
                AttributeModifier.Operation l = AttributeModifier.Operation.byId(k);
                if (l != null) {
                    c.put(h, Map.entry(j, l));
                }
            }
        }
        return c;
    }

    private Map<Attribute, Map.Entry<Float, AttributeModifier.Operation>> createCrusherAttributes() {
        Map<Attribute, Map.Entry<Float, AttributeModifier.Operation>> a = new HashMap<>();
        a.put(Attributes.ARMOR, Map.entry(2.0F, AttributeModifier.Operation.ADDITION));
        a.put(Attributes.ARMOR_TOUGHNESS, Map.entry(2.0F, AttributeModifier.Operation.ADDITION));
        a.put(Attributes.ATTACK_DAMAGE, Map.entry(3.0F, AttributeModifier.Operation.ADDITION));
        a.put(Attributes.MAX_HEALTH, Map.entry(4.0F, AttributeModifier.Operation.ADDITION));
        return a;
    }

    private Map<Attribute, Map.Entry<Float, AttributeModifier.Operation>> createKaratelAttributes() {
        Map<Attribute, Map.Entry<Float, AttributeModifier.Operation>> a = new HashMap<>();
        a.put(Attributes.MOVEMENT_SPEED, Map.entry(0.1F, AttributeModifier.Operation.MULTIPLY_BASE));
        a.put(Attributes.MAX_HEALTH, Map.entry(-4.0F, AttributeModifier.Operation.ADDITION));
        a.put(Attributes.ATTACK_DAMAGE, Map.entry(7.0F, AttributeModifier.Operation.ADDITION));
        return a;
    }

    private Map<Attribute, Map.Entry<Float, AttributeModifier.Operation>> createRazdorAttributes() {
        Map<Attribute, Map.Entry<Float, AttributeModifier.Operation>> a = new HashMap<>();
        a.put(Attributes.ARMOR, Map.entry(-3.0F, AttributeModifier.Operation.ADDITION));
        a.put(Attributes.ATTACK_SPEED, Map.entry(0.1F, AttributeModifier.Operation.MULTIPLY_BASE));
        a.put(Attributes.MOVEMENT_SPEED, Map.entry(0.1F, AttributeModifier.Operation.MULTIPLY_BASE));
        a.put(Attributes.MAX_HEALTH, Map.entry(2.0F, AttributeModifier.Operation.ADDITION));
        a.put(Attributes.ATTACK_DAMAGE, Map.entry(4.0F, AttributeModifier.Operation.ADDITION));
        return a;
    }

    private Map<Attribute, Map.Entry<Float, AttributeModifier.Operation>> createTiranaAttributes() {
        Map<Attribute, Map.Entry<Float, AttributeModifier.Operation>> a = new HashMap<>();
        a.put(Attributes.ARMOR, Map.entry(2.0F, AttributeModifier.Operation.ADDITION));
        a.put(Attributes.MAX_HEALTH, Map.entry(-4.0F, AttributeModifier.Operation.ADDITION));
        a.put(Attributes.ATTACK_DAMAGE, Map.entry(2.0F, AttributeModifier.Operation.ADDITION));
        return a;
    }

    private Map<Attribute, Map.Entry<Float, AttributeModifier.Operation>> createYarostiAttributes() {
        Map<Attribute, Map.Entry<Float, AttributeModifier.Operation>> a = new HashMap<>();
        a.put(Attributes.MAX_HEALTH, Map.entry(-4.0F, AttributeModifier.Operation.ADDITION));
        a.put(Attributes.ATTACK_DAMAGE, Map.entry(5.0F, AttributeModifier.Operation.ADDITION));
        return a;
    }

    private Map<Attribute, Map.Entry<Float, AttributeModifier.Operation>> createVihryaAttributes() {
        Map<Attribute, Map.Entry<Float, AttributeModifier.Operation>> a = new HashMap<>();
        a.put(Attributes.ATTACK_SPEED, Map.entry(0.15F, AttributeModifier.Operation.MULTIPLY_BASE));
        a.put(Attributes.MOVEMENT_SPEED, Map.entry(0.15F, AttributeModifier.Operation.MULTIPLY_BASE));
        a.put(Attributes.MAX_HEALTH, Map.entry(2.0F, AttributeModifier.Operation.ADDITION));
        return a;
    }

    private Map<Attribute, Map.Entry<Float, AttributeModifier.Operation>> createMrakaAttributes() {
        Map<Attribute, Map.Entry<Float, AttributeModifier.Operation>> a = new HashMap<>();
        a.put(Attributes.ARMOR, Map.entry(1.5F, AttributeModifier.Operation.ADDITION));
        a.put(Attributes.MAX_HEALTH, Map.entry(1.5F, AttributeModifier.Operation.ADDITION));
        return a;
    }

    private Map<Attribute, Map.Entry<Float, AttributeModifier.Operation>> createDemonaAttributes() {
        Map<Attribute, Map.Entry<Float, AttributeModifier.Operation>> a = new HashMap<>();
        a.put(Attributes.ATTACK_SPEED, Map.entry(0.1F, AttributeModifier.Operation.MULTIPLY_BASE));
        a.put(Attributes.ATTACK_DAMAGE, Map.entry(2.5F, AttributeModifier.Operation.ADDITION));
        return a;
    }

    private Map<Attribute, Map.Entry<Float, AttributeModifier.Operation>> createChaosAttributes() {
        Map<Attribute, Map.Entry<Float, AttributeModifier.Operation>> a = new HashMap<>();
        a.put(Attributes.ARMOR, Map.entry(2.0F, AttributeModifier.Operation.ADDITION));
        a.put(Attributes.ATTACK_SPEED, Map.entry(0.13F, AttributeModifier.Operation.MULTIPLY_BASE));
        a.put(Attributes.MOVEMENT_SPEED, Map.entry(0.07F, AttributeModifier.Operation.MULTIPLY_BASE));
        a.put(Attributes.MAX_HEALTH, Map.entry(-4.0F, AttributeModifier.Operation.ADDITION));
        a.put(Attributes.ATTACK_DAMAGE, Map.entry(3.0F, AttributeModifier.Operation.ADDITION));
        return a;
    }

    private Map<Attribute, Map.Entry<Float, AttributeModifier.Operation>> createBestiiAttributes() {
        Map<Attribute, Map.Entry<Float, AttributeModifier.Operation>> a = new HashMap<>();
        a.put(Attributes.ARMOR, Map.entry(1.0F, AttributeModifier.Operation.ADDITION));
        a.put(Attributes.ATTACK_SPEED, Map.entry(0.1F, AttributeModifier.Operation.MULTIPLY_BASE));
        a.put(Attributes.MOVEMENT_SPEED, Map.entry(0.1F, AttributeModifier.Operation.MULTIPLY_BASE));
        a.put(Attributes.MAX_HEALTH, Map.entry(-4.0F, AttributeModifier.Operation.ADDITION));
        return a;
    }

    private Map<Attribute, Map.Entry<Float, AttributeModifier.Operation>> createPresaAttributes() {
        Map<Attribute, Map.Entry<Float, AttributeModifier.Operation>> a = new HashMap<>();
        a.put(Attributes.ARMOR, Map.entry(-2.0F, AttributeModifier.Operation.ADDITION));
        a.put(Attributes.MAX_HEALTH, Map.entry(-2.0F, AttributeModifier.Operation.ADDITION));
        a.put(Attributes.ATTACK_DAMAGE, Map.entry(6.0F, AttributeModifier.Operation.ADDITION));
        return a;
    }

    private Map<Attribute, Map.Entry<Float, AttributeModifier.Operation>> createGidriAttributes() {
        Map<Attribute, Map.Entry<Float, AttributeModifier.Operation>> a = new HashMap<>();
        a.put(Attributes.ARMOR, Map.entry(2.0F, AttributeModifier.Operation.ADDITION));
        a.put(Attributes.MAX_HEALTH, Map.entry(4.0F, AttributeModifier.Operation.ADDITION));
        return a;
    }

    private Map<Attribute, Map.Entry<Float, AttributeModifier.Operation>> createIkaraAttributes() {
        Map<Attribute, Map.Entry<Float, AttributeModifier.Operation>> a = new HashMap<>();
        a.put(Attributes.MAX_HEALTH, Map.entry(2.0F, AttributeModifier.Operation.ADDITION));
        a.put(Attributes.ATTACK_DAMAGE, Map.entry(2.0F, AttributeModifier.Operation.ADDITION));
        return a;
    }

    private Map<Attribute, Map.Entry<Float, AttributeModifier.Operation>> createEridaAttributes() {
        Map<Attribute, Map.Entry<Float, AttributeModifier.Operation>> a = new HashMap<>();
        a.put(Attributes.LUCK, Map.entry(1.0F, AttributeModifier.Operation.ADDITION));
        a.put(Attributes.MAX_HEALTH, Map.entry(2.0F, AttributeModifier.Operation.ADDITION));
        return a;
    }

    private List<PotionEffectMatcher> createAngerPotionEffects() {
        return Arrays.asList(
                new PotionEffectMatcher(5, 4, 18),
                new PotionEffectMatcher(2, 3, 18)
        );
    }

    private List<PotionEffectMatcher> createPalladinPotionEffects() {
        return Arrays.asList(
                new PotionEffectMatcher(11, 9, 600),
                new PotionEffectMatcher(12, 9, 600),
                new PotionEffectMatcher(10, 2, 900),
                new PotionEffectMatcher(14, 2, 900)
        );
    }

    private List<PotionEffectMatcher> createAssassinPotionEffects() {
        return Arrays.asList(
                new PotionEffectMatcher(5, 3, 300),
                new PotionEffectMatcher(1, 2, 60),
                new PotionEffectMatcher(7, 1, 10)
        );
    }

    private List<PotionEffectMatcher> createRadiationPotionEffects() {
        return Arrays.asList(
                new PotionEffectMatcher(19, 1, 12),
                new PotionEffectMatcher(27, 1, 12),
                new PotionEffectMatcher(17, 4, 12),
                new PotionEffectMatcher(25, 0, 12)
        );
    }

    private List<PotionEffectMatcher> createCursedPotionEffects() {
        return Arrays.asList(
                new PotionEffectMatcher(18, 1, 78),
                new PotionEffectMatcher(20, 0, 6),
                new PotionEffectMatcher(27, 2, 78),
                new PotionEffectMatcher(15, 0, 6)
        );
    }

    public static class AutoBuyItemData {
        public final String displayName;
        public final Item item;
        public int price;
        public final Map<Enchantment, Integer> enchantments;
        public final Map<Attribute, Map.Entry<Float, AttributeModifier.Operation>> attributes;
        public final String spookyItemType;
        public final List<PotionEffectMatcher> potionEffects;
        public String searchAlias;
        public Map<Enchantment, EnchantRange> enchantmentRanges;
        public Supplier<Integer> minDurabilityPercentSupplier;
        public int minDurabilityPercent;

        public AutoBuyItemData(String a, Item b) {
            this(a, b, null, null, null, null);
        }

        public AutoBuyItemData(String a, Item b, Map<Enchantment, Integer> c) {
            this(a, b, c, null, null, null);
        }

        public AutoBuyItemData(String a, Item b, Map<Enchantment, Integer> c, Map<Attribute, Map.Entry<Float, AttributeModifier.Operation>> d, String e) {
            this(a, b, c, d, e, null);
        }

        public AutoBuyItemData(String a, Item b, List<PotionEffectMatcher> c) {
            this(a, b, null, null, null, c);
        }

        public AutoBuyItemData(String a, Item b, Map<Enchantment, Integer> c, Map<Attribute, Map.Entry<Float, AttributeModifier.Operation>> d, String e, List<PotionEffectMatcher> f) {
            this.displayName = a;
            this.item = b;
            this.price = 0;
            this.enchantments = c;
            this.attributes = d;
            this.spookyItemType = e;
            this.potionEffects = f;
            this.searchAlias = null;
            this.enchantmentRanges = null;
            this.minDurabilityPercentSupplier = null;
            this.minDurabilityPercent = 0;
        }

        public AutoBuyItemData withSearchAlias(String searchAlias) {
            this.searchAlias = searchAlias;
            return this;
        }

        public AutoBuyItemData withEnchantmentRanges(Map<Enchantment, EnchantRange> ranges) {
            this.enchantmentRanges = ranges;
            return this;
        }

        public AutoBuyItemData withMinDurabilitySupplier(Supplier<Integer> supplier) {
            this.minDurabilityPercentSupplier = supplier;
            return this;
        }
    }

    public static class EnchantRange {
        public final int min;
        public final int max;

        public EnchantRange(int min, int max) {
            this.min = min;
            this.max = max;
        }
    }

    private static class InventoryRequirement {
        private final Supplier<AutoBuyItemData> itemSupplier;
        private final IntSupplier targetSupplier;

        private InventoryRequirement(Supplier<AutoBuyItemData> itemSupplier, IntSupplier targetSupplier) {
            this.itemSupplier = itemSupplier;
            this.targetSupplier = targetSupplier;
        }
    }

    public record PotionEffectMatcher(int id, int amplifier, int duration) {
    }

    public static class BuyedItem {
        public ItemStack itemStack;
        public ItemStack originalStack;
        public int price;
        public int count;
        public AutoBuyItemData abItem;
        public boolean buyed;
        public LocalDateTime time;

        public BuyedItem(ItemStack a, ItemStack b, int c, int d, AutoBuyItemData e, boolean f, LocalDateTime g) {
            this.itemStack = a;
            this.originalStack = b;
            this.price = c;
            this.count = d;
            this.abItem = e;
            this.buyed = f;
            this.time = g;
        }

        @Override
        public boolean equals(Object a) {
            if (this == a) return true;
            if (a == null || getClass() != a.getClass()) return false;
            BuyedItem b = (BuyedItem) a;
            return price == b.price && count == b.count && Objects.equals(abItem.displayName, b.abItem.displayName);
        }
    }

    private static class PriceEntry {
        String displayName;
        int price;

        public PriceEntry() {
        }

        public PriceEntry(String displayName, int price) {
            this.displayName = displayName;
            this.price = price;
        }
    }

    private static class PersistentItemsData {
        List<SerializableBuyedItem> buyedItems;
        Map<String, SerializableItemStack> seenItems;
        Map<String, Long> itemLastSeenTime;
    }

    private static class SerializableBuyedItem {
        String displayName;
        int price;
        int count;
        boolean buyed;
        String time;
        String itemStackNBT;
        String itemType;
    }

    private static class SerializableItemStack {
        String itemType;
        String displayName;
        int count;
        String nbtData;
    }
}
