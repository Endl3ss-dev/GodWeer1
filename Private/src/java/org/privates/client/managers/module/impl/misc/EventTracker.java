package org.privates.client.managers.module.impl.misc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PotionEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.UseAction;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SEntityStatusPacket;
import net.minecraft.potion.*;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.TextFormatting;
import org.privates.client.Excellent;
import org.privates.client.api.annotations.Category2;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.other.PacketEvent;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.MultiBooleanSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.screen.notification.Notification;
import org.privates.client.screen.notification.NotificationType;
import org.privates.client.utils.chat.ChatUtil;
import org.privates.client.utils.other.Instance;
import org.privates.lib.util.time.StopWatch;

import java.util.*;
import java.util.stream.Collectors;

@Category2
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "EventTracker", category = Category.PLAYER, description = "Отслеживает события: зелья, использование предметов, уведомления")
public class EventTracker extends Module {

    public static EventTracker getInstance() {
        return Instance.get(EventTracker.class);
    }

    // === КАТЕГОРИИ ФУНКЦИЙ ===
    private final MultiBooleanSetting trackerFeatures = new MultiBooleanSetting(this, "Трекеры",
            BooleanSetting.of("Отслеживание зелий", true),
            BooleanSetting.of("Отслеживание использования", true),
            BooleanSetting.of("Уведомления", true),
            BooleanSetting.of("Помощник уведомлений", true));

    // === НАСТРОЙКИ ЗЕЛИЙ ===
    private final SliderSetting potionRadius = new SliderSetting(this, "Радиус зелий", 50.0f, 10.0f, 100.0f, 1.0f)
            .setVisible(() -> trackerFeatures.getValue("Отслеживание зелий"));
    private final BooleanSetting showHitChance = new BooleanSetting(this, "Показывать шанс попадания", true)
            .setVisible(() -> trackerFeatures.getValue("Отслеживание зелий"));
    private final BooleanSetting showEffects = new BooleanSetting(this, "Показывать эффекты", true)
            .setVisible(() -> trackerFeatures.getValue("Отслеживание зелий"));

    // === НАСТРОЙКИ ИСПОЛЬЗОВАНИЯ ===
    private final BooleanSetting ignoreFriends = new BooleanSetting(this, "Игнорировать друзей", true)
            .setVisible(() -> trackerFeatures.getValue("Отслеживание использования"));
    private final BooleanSetting showSelf = new BooleanSetting(this, "Показывать свои действия", true)
            .setVisible(() -> trackerFeatures.getValue("Отслеживание использования"));

    // === НАСТРОЙКИ УВЕДОМЛЕНИЙ ===
    private final BooleanSetting useNotifications = new BooleanSetting(this, "Использовать уведомления", false)
            .setVisible(() -> trackerFeatures.getValue("Уведомления"));
    private final BooleanSetting sound = new BooleanSetting(this, "Звук", true)
            .setVisible(() -> trackerFeatures.getValue("Уведомления"));
    private final SliderSetting volume = new SliderSetting(this, "Громкость", 50, 1, 100, 1)
            .setVisible(() -> trackerFeatures.getValue("Уведомления") && sound.getValue());

    // === НАСТРОЙКИ ПОМОЩНИКА УВЕДОМЛЕНИЙ ===
    private final MultiBooleanSetting helperNotifications = new MultiBooleanSetting(this, "Уведомления помощника",
            BooleanSetting.of("Окончание эффектов", true),
            BooleanSetting.of("Подбор предметов", true),
            BooleanSetting.of("Низкое здоровье", true),
            BooleanSetting.of("Предупреждение о зельях", true),
            BooleanSetting.of("Элитра сломается", true),
            BooleanSetting.of("Низкий голод", true),
            BooleanSetting.of("Враг держит арбалет", true),
            BooleanSetting.of("Враг держит эндер перл", true),
            BooleanSetting.of("Враг держит лук", true),
            BooleanSetting.of("Враг целится луком", true))
            .setVisible(() -> trackerFeatures.getValue("Помощник уведомлений"));

    // Данные для всех модулей объединены здесь
    // Данные зелий, использования, помощника уведомлений
    private final Map<Integer, PotionData> trackedPotions = new HashMap<>();
    private final Map<String, Set<String>> playerNotifiedPotions = new HashMap<>();
    private final LinkedList<Long> recentNotifications = new LinkedList<>();
    private final Map<String, Long> lastNotificationByText = new HashMap<>(); // Для предотвращения дублирования
    private final Map<String, PlayerHandState> playerHandStates = new HashMap<>();
    private final Map<Effect, Integer> activeEffects = new HashMap<>();
    private final Map<String, Integer> inventoryItems = new HashMap<>();
    private final Map<UUID, Set<Effect>> knownEffects = new HashMap<>();

    private ItemStack lastActiveItem = ItemStack.EMPTY;
    private boolean wasHandActive = false;
    private int lastUseMaxCount = 0;
    private Hand lastActiveHand = null;
    private int previousSlot = -1;

    private static final int MAX_NOTIFICATIONS_PER_SECOND = 5;
    private static final long ONE_SECOND_MS = 1000;

    private static final Set<Item> TRACKED_ITEMS = new HashSet<>(Arrays.asList(
            Items.NETHERITE_HELMET, Items.NETHERITE_CHESTPLATE, Items.NETHERITE_LEGGINGS, Items.NETHERITE_BOOTS,
            Items.NETHERITE_SWORD, Items.ELYTRA, Items.TOTEM_OF_UNDYING, Items.PLAYER_HEAD,
            Items.FIREWORK_ROCKET, Items.END_CRYSTAL, Items.ENCHANTED_GOLDEN_APPLE,
            Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION
    ));

    // Флаги состояния для NotifyHelper
    private boolean lowHealthActive, potionActive, elytraActive, hungerActive;
    private boolean enemyPotionActive, enemyEffectActive, enemyEatActive, enemySplashActive;
    private boolean enemyCrossbowActive, enemyEnderpearlActive, enemyBowActive, enemyAimingActive;

    private final StopWatch lowHealthTimer = new StopWatch();
    private final StopWatch potionTimer = new StopWatch();
    private final StopWatch elytraTimer = new StopWatch();
    private final StopWatch hungerTimer = new StopWatch();
    private final StopWatch enemyPotionTimer = new StopWatch();
    private final StopWatch enemyEatTimer = new StopWatch();
    private final StopWatch enemySplashTimer = new StopWatch();
    private final StopWatch enemyCrossbowTimer = new StopWatch();
    private final StopWatch enemyEnderpearlTimer = new StopWatch();
    private final StopWatch enemyBowTimer = new StopWatch();
    private final StopWatch enemyAimingTimer = new StopWatch();

    private String enemyPotionName = "";
    private String enemyEffectName = "";
    private String enemyEatenItemName = "";
    private String enemySplashPotionName = "";

    @Override
    public void onEnable() {
        super.onEnable();
        trackedPotions.clear();
        playerNotifiedPotions.clear();
        recentNotifications.clear();
        playerHandStates.clear();
        activeEffects.clear();
        inventoryItems.clear();
        knownEffects.clear();
        lastActiveItem = ItemStack.EMPTY;
        wasHandActive = false;
        lastUseMaxCount = 0;
        lastActiveHand = null;
        previousSlot = mc.player != null ? mc.player.inventory.currentItem : -1;
        resetHelperFlags();
        updateInventorySnapshot();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        trackedPotions.clear();
        playerNotifiedPotions.clear();
        recentNotifications.clear();
        lastNotificationByText.clear();
        playerHandStates.clear();
        activeEffects.clear();
        inventoryItems.clear();
        knownEffects.clear();
    }

    private void resetHelperFlags() {
        lowHealthActive = potionActive = elytraActive = hungerActive = false;
        enemyPotionActive = enemyEffectActive = enemyEatActive = enemySplashActive = false;
        enemyCrossbowActive = enemyEnderpearlActive = enemyBowActive = enemyAimingActive = false;
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (mc.world == null || mc.player == null) return;

        if (trackerFeatures.getValue("Отслеживание зелий")) {
            updatePotionTracking();
        }

        if (trackerFeatures.getValue("Отслеживание использования")) {
            trackSelfUsage();
            trackOtherPlayersUsage();
            cleanupOfflinePlayers();
        }

        if (trackerFeatures.getValue("Помощник уведомлений")) {
            updateNotifyHelper();
        }
    }

    @EventHandler
    public void onPacket(PacketEvent event) {
        if (!event.isReceive() || !trackerFeatures.getValue("Отслеживание использования")) return;

        IPacket<?> packet = event.getPacket();
        if (packet instanceof SEntityStatusPacket status && status.getOpCode() == 35) {
            if (mc.world == null) return;
            PlayerEntity player = (PlayerEntity) status.getEntity(mc.world);
            if (player == null || (player == mc.player && !showSelf.getValue())) return;
            String name = player.getName().getString();
            if (ignoreFriends.getValue() && isFriend(name)) return;
            sendPopMessage(name, getTotemStack(player), player == mc.player);
        }
    }

    // === МЕТОДЫ ОТСЛЕЖИВАНИЯ ЗЕЛИЙ ===
    private void updatePotionTracking() {
        cleanupOldNotificationTimestamps();
        Set<Integer> currentPotions = new HashSet<>();

        for (Entity entity : mc.world.getAllEntities()) {
            if (entity instanceof PotionEntity potionEntity) {
                int entityId = entity.getEntityId();
                if (mc.player.getDistance(entity) > potionRadius.getValue()) continue;
                currentPotions.add(entityId);

                if (!trackedPotions.containsKey(entityId)) {
                    trackedPotions.put(entityId, new PotionData(potionEntity.getItem().copy(),
                            entity.getPosX(), entity.getPosY(), entity.getPosZ()));
                } else {
                    PotionData data = trackedPotions.get(entityId);
                    data.lastX = entity.getPosX();
                    data.lastY = entity.getPosY();
                    data.lastZ = entity.getPosZ();
                }
            }
        }

        Set<Integer> removedPotions = new HashSet<>(trackedPotions.keySet());
        removedPotions.removeAll(currentPotions);

        for (int entityId : removedPotions) {
            processPotionHit(trackedPotions.get(entityId));
            trackedPotions.remove(entityId);
        }

        cleanupOldPlayerData();
    }

    private void processPotionHit(PotionData data) {
        AxisAlignedBB potionBB = new AxisAlignedBB(
                data.lastX - 4.0, data.lastY - 2.0, data.lastZ - 4.0,
                data.lastX + 4.0, data.lastY + 2.0, data.lastZ + 4.0);

        for (LivingEntity hitEntity : mc.world.getEntitiesWithinAABB(LivingEntity.class, potionBB)) {
            if (!(hitEntity instanceof PlayerEntity player)) continue;
            double distance = Math.sqrt(Math.pow(player.getPosX() - data.lastX, 2) + Math.pow(player.getPosZ() - data.lastZ, 2));
            if (distance > 4.0) continue;

            String potionKey = getPotionKey(data.stack);
            String playerName = player.getName().getString();

            if (wasAlreadyNotified(playerName, potionKey) || !canSendNotification()) continue;

            sendPotionHitMessage(player, data.stack, distance);
            addNotificationTimestamp();
            markAsNotified(playerName, potionKey);
        }
    }

    private void sendPotionHitMessage(PlayerEntity player, ItemStack potionStack, double distance) {
        double hitChance = Math.max(0.0, 1.0 - distance / 4.0) * 100.0;
        
        // Формат: обычный текст + §h§ключевое слово§h§ + обычный текст
        // §h§ - маркер для выделения цветом
        String message = player.getName().getString() + " получил §h§" + 
                potionStack.getDisplayName().getString() + "§h§";

        sendNotification(message, potionStack);

        if (showHitChance.getValue()) {
            ChatUtil.addText(TextFormatting.DARK_GRAY + "• " + TextFormatting.WHITE + "Успешность: " +
                    TextFormatting.GREEN + String.format("%.0f%%", hitChance));
        }

        if (showEffects.getValue()) {
            for (EffectInstance effect : PotionUtils.getEffectsFromStack(potionStack)) {
                int duration = (int) (effect.getDuration() / 20 * (hitChance / 100.0));
                ChatUtil.addText(TextFormatting.DARK_GRAY + "• " + TextFormatting.RED +
                        effect.getPotion().getDisplayName().getString() + " " +
                        toRoman(effect.getAmplifier() + 1) + TextFormatting.GRAY +
                        " (" + duration / 60 + ":" + String.format("%02d", duration % 60) + ")");
            }
        }
    }

    // === МЕТОДЫ ОТСЛЕЖИВАНИЯ ИСПОЛЬЗОВАНИЯ ===
    private void trackSelfUsage() {
        boolean isHandActive = mc.player.isHandActive();
        ItemStack activeItem = mc.player.getActiveItemStack();
        Hand activeHand = mc.player.getActiveHand();

        if (isHandActive && !activeItem.isEmpty() &&
                (activeItem.getUseAction() == UseAction.EAT || activeItem.getUseAction() == UseAction.DRINK)) {
            if (!wasHandActive || lastActiveHand != activeHand) {
                lastActiveItem = activeItem.copy();
                lastActiveHand = activeHand;
                lastUseMaxCount = 0;
            }
            lastUseMaxCount = Math.max(lastUseMaxCount, mc.player.getItemInUseMaxCount());
        }

        if (wasHandActive && !isHandActive && !lastActiveItem.isEmpty()) {
            ItemStack currentItem = lastActiveHand == Hand.MAIN_HAND ?
                    mc.player.getHeldItemMainhand() : mc.player.getHeldItemOffhand();
            if (checkIfItemWasUsed(lastActiveItem, currentItem)) {
                String name = mc.player.getName().getString();
                if (showSelf.getValue() && (!ignoreFriends.getValue() || !isFriend(name))) {
                    sendEatMessage(name, lastActiveItem, true);
                }
            }
            lastActiveItem = ItemStack.EMPTY;
            lastUseMaxCount = 0;
            lastActiveHand = null;
        }
        wasHandActive = isHandActive;
    }

    private void trackOtherPlayersUsage() {
        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == null || player == mc.player) continue;
            String playerName = player.getName().getString();
            if (ignoreFriends.getValue() && isFriend(playerName)) continue;

            PlayerHandState state = playerHandStates.computeIfAbsent(playerName, k -> new PlayerHandState());
            trackHandUsage(playerName, player.getHeldItemMainhand(), state, true);
            trackHandUsage(playerName, player.getHeldItemOffhand(), state, false);

            state.mainHand = player.getHeldItemMainhand().copy();
            state.offHand = player.getHeldItemOffhand().copy();
        }
    }

    private void trackHandUsage(String playerName, ItemStack currentHand, PlayerHandState state, boolean isMainHand) {
        ItemStack lastHand = isMainHand ? state.mainHand : state.offHand;
        int useTicks = isMainHand ? state.mainHandUseTicks : state.offHandUseTicks;
        boolean wasUsing = isMainHand ? state.mainHandWasUsing : state.offHandWasUsing;

        boolean isUsing = !currentHand.isEmpty() &&
                (currentHand.getUseAction() == UseAction.EAT || currentHand.getUseAction() == UseAction.DRINK) &&
                ItemStack.areItemsEqual(lastHand, currentHand) && currentHand.getCount() == lastHand.getCount();

        if (isUsing) {
            if (isMainHand) {
                state.mainHandUseTicks++;
                state.mainHandWasUsing = true;
            } else {
                state.offHandUseTicks++;
                state.offHandWasUsing = true;
            }
        } else if (wasUsing && useTicks >= 10 && !lastHand.isEmpty() && checkIfItemWasUsed(lastHand, currentHand)) {
            sendEatMessage(playerName, lastHand.copy(), false);
            if (isMainHand) {
                state.mainHandUseTicks = 0;
                state.mainHandWasUsing = false;
            } else {
                state.offHandUseTicks = 0;
                state.offHandWasUsing = false;
            }
        }
    }

    private boolean checkIfItemWasUsed(ItemStack lastItem, ItemStack currentItem) {
        if (currentItem.isEmpty()) return true;
        if (lastItem.getItem() != currentItem.getItem()) {
            return (lastItem.getItem() == Items.MILK_BUCKET && currentItem.getItem() == Items.BUCKET) ||
                    (lastItem.getItem() == Items.HONEY_BOTTLE && currentItem.getItem() == Items.GLASS_BOTTLE);
        }
        return currentItem.getCount() < lastItem.getCount();
    }

    private void sendPopMessage(String playerName, ItemStack totemStack, boolean isSelf) {
        boolean isEnchanted = totemStack != null && !totemStack.isEmpty() && totemStack.isEnchanted();
        String totemName = totemStack != null && !totemStack.isEmpty() ? totemStack.getDisplayName().getString() : "Неизвестный тотем";
        
        String message = (isSelf ? "[СЕБЯ] " : "") + playerName + " попнул §h§" + totemName + "§h§!";
        sendNotification(message, totemStack);
    }

    private void sendEatMessage(String playerName, ItemStack itemStack, boolean isSelf) {
        String action = itemStack.getUseAction() == UseAction.EAT ? "съел" : "выпил";
        String message = (isSelf ? "[СЕБЯ] " : "") + playerName + " " + action + " §h§" + 
                itemStack.getDisplayName().getString() + "§h§!";
        sendNotification(message, itemStack);
    }

    private ItemStack getTotemStack(PlayerEntity player) {
        ItemStack mainHand = player.getHeldItemMainhand();
        ItemStack offHand = player.getHeldItemOffhand();
        if (mainHand.getItem() == Items.TOTEM_OF_UNDYING) return mainHand;
        if (offHand.getItem() == Items.TOTEM_OF_UNDYING) return offHand;
        return ItemStack.EMPTY;
    }

    private void cleanupOfflinePlayers() {
        Set<String> activePlayers = mc.world.getPlayers().stream()
                .filter(Objects::nonNull)
                .map(p -> p.getName().getString())
                .collect(Collectors.toSet());
        playerHandStates.keySet().retainAll(activePlayers);
    }

    // === МЕТОДЫ ПОМОЩНИКА УВЕДОМЛЕНИЙ ===
    private void updateNotifyHelper() {
        if (helperNotifications.getValue("Свапы предметов")) checkSwaps();
        if (helperNotifications.getValue("Окончание эффектов")) checkEffects();
        if (helperNotifications.getValue("Подбор предметов")) checkInventory();
        if (helperNotifications.getValue("Низкое здоровье")) checkLowHealth();
        if (helperNotifications.getValue("Предупреждение о зельях")) checkPotionWarning();
        if (helperNotifications.getValue("Элитра сломается")) checkElytraWarning();
        if (helperNotifications.getValue("Низкий голод")) checkHungerWarning();
        if (helperNotifications.getValue("Враг пьет зелье")) checkEnemyPotionDrink();
        if (helperNotifications.getValue("Враг получает эффект")) checkEnemyEffectGained();
        if (helperNotifications.getValue("Враг ест")) checkEnemyEating();
        if (helperNotifications.getValue("Враг держит сплеш")) checkEnemyHoldingSplash();
        if (helperNotifications.getValue("Враг держит арбалет"))
            checkEnemyHolding(Items.CROSSBOW, "арбалет", enemyCrossbowActive, enemyCrossbowTimer);
        if (helperNotifications.getValue("Враг держит эндер перл"))
            checkEnemyHolding(Items.ENDER_PEARL, "эндер жемчуг", enemyEnderpearlActive, enemyEnderpearlTimer);
        if (helperNotifications.getValue("Враг держит лук"))
            checkEnemyHolding(Items.BOW, "лук", enemyBowActive, enemyBowTimer);
        if (helperNotifications.getValue("Враг целится луком")) checkEnemyAiming();
    }

    private void checkSwaps() {
        int currentSlot = mc.player.inventory.currentItem;
        if (currentSlot != previousSlot) {
            ItemStack swappedItem = mc.player.inventory.getCurrentItem();
            if (!swappedItem.isEmpty()) {
                notifySwap(swappedItem.copy(), "Свапнули на §h§" + swappedItem.getDisplayName().getString() + "§h§");
            }
            previousSlot = currentSlot;
        }
    }

    private void checkEffects() {
        Map<Effect, Integer> currentEffects = new HashMap<>();
        for (EffectInstance effect : mc.player.getActivePotionEffects()) {
            currentEffects.put(effect.getPotion(), effect.getAmplifier());
        }

        for (Map.Entry<Effect, Integer> entry : activeEffects.entrySet()) {
            if (!currentEffects.containsKey(entry.getKey())) {
                String effectName = entry.getKey().getDisplayName().getString();
                String level = entry.getValue() > 0 ? " " + (entry.getValue() + 1) : "";
                notifyEffectEnd(entry.getKey(), entry.getValue(), "У вас закончился эффект §h§" + effectName + level + "§h§");
            }
        }

        activeEffects.clear();
        activeEffects.putAll(currentEffects);
    }

    private void checkInventory() {
        Map<String, Integer> currentItems = new HashMap<>();
        for (int i = 0; i < mc.player.inventory.getSizeInventory(); i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (!stack.isEmpty() && TRACKED_ITEMS.contains(stack.getItem())) {
                String key = Registry.ITEM.getKey(stack.getItem()).toString();
                currentItems.put(key, currentItems.getOrDefault(key, 0) + stack.getCount());
            }
        }

        for (Map.Entry<String, Integer> entry : currentItems.entrySet()) {
            int newCount = entry.getValue();
            int oldCount = inventoryItems.getOrDefault(entry.getKey(), 0);
            if (newCount > oldCount) {
                ItemStack stack = getItemStackByKey(entry.getKey());
                if (stack != null) {
                    int difference = newCount - oldCount;
                    ItemStack notifyStack = stack.copy();
                    notifyStack.setCount(difference);
                    String text = difference > 1 ?
                            "Вы подобрали " + difference + "x §h§" + stack.getDisplayName().getString() + "§h§" :
                            "Вы подобрали §h§" + stack.getDisplayName().getString() + "§h§";
                    notifyItemPickup(notifyStack, text);
                }
            }
        }

        inventoryItems.clear();
        inventoryItems.putAll(currentItems);
    }

    private void checkLowHealth() {
        boolean shouldShow = mc.player.getHealth() <= 6.0f;
        if (shouldShow && !lowHealthActive) {
            notifyWarning("У вас мало здоровья");
            lowHealthTimer.reset();
        }
        lowHealthActive = shouldShow || (lowHealthActive && !lowHealthTimer.finished(3000));
    }

    private void checkPotionWarning() {
        Effect targetEffect = null;
        for (EffectInstance effect : mc.player.getActivePotionEffects()) {
            if ((effect.getPotion() == Effects.STRENGTH || effect.getPotion() == Effects.SPEED) &&
                    effect.getDuration() <= 15 * 20) {
                targetEffect = effect.getPotion();
                break;
            }
        }
        boolean shouldShow = targetEffect != null;
        if (shouldShow && !potionActive) {
            String effectName = targetEffect == Effects.STRENGTH ? "силы" : "скорости";
            notifyWarning("Заканчивается зелье " + effectName);
            potionTimer.reset();
        }
        potionActive = shouldShow || (potionActive && !potionTimer.finished(3000));
    }

    private void checkElytraWarning() {
        ItemStack elytraStack = mc.player.inventory.armorItemInSlot(2);
        boolean shouldShow = false;
        int durability = 0;
        if (elytraStack.getItem() == Items.ELYTRA) {
            durability = elytraStack.getMaxDamage() - elytraStack.getDamage();
            shouldShow = durability > 0 && durability <= 15;
        }
        if (shouldShow && !elytraActive) {
            notifyWarning("Элитра почти сломана: " + durability);
            elytraTimer.reset();
        }
        elytraActive = shouldShow || (elytraActive && !elytraTimer.finished(3000));
    }

    private void checkHungerWarning() {
        boolean shouldShow = mc.player.getFoodStats().getFoodLevel() <= 12;
        if (shouldShow && !hungerActive) {
            notifyWarning("У вас мало голода");
            hungerTimer.reset();
        }
        hungerActive = shouldShow || (hungerActive && !hungerTimer.finished(3000));
    }

    private void checkEnemyPotionDrink() {
        boolean shouldShow = false;
        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == mc.player || player.isSpectator() || mc.player.getDistanceSq(player) > 64) continue;
            if (player.isHandActive() && player.getActiveItemStack().getItem() == Items.POTION) {
                shouldShow = true;
                enemyPotionName = player.getActiveItemStack().getDisplayName().getString();
                break;
            }
        }
        if (shouldShow && !enemyPotionActive) {
            notifyWarning("Противник взял §h§" + enemyPotionName + "§h§");
            enemyPotionTimer.reset();
        }
        enemyPotionActive = shouldShow || (enemyPotionActive && !enemyPotionTimer.finished(3000));
    }

    private void checkEnemyEffectGained() {
        knownEffects.keySet().retainAll(mc.world.getPlayers().stream()
                .filter(Objects::nonNull)
                .map(PlayerEntity::getUniqueID)
                .collect(Collectors.toSet()));

        boolean shouldShow = false;
        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == mc.player || player.isSpectator() || mc.player.getDistanceSq(player) > 64) continue;

            Set<Effect> currentEffects = player.getActivePotionEffects().stream()
                    .map(EffectInstance::getPotion)
                    .collect(Collectors.toSet());
            Set<Effect> lastKnown = knownEffects.getOrDefault(player.getUniqueID(), Collections.emptySet());
            Set<Effect> newEffects = new HashSet<>(currentEffects);
            newEffects.removeAll(lastKnown);

            if (!newEffects.isEmpty()) {
                enemyEffectName = newEffects.iterator().next().getDisplayName().getString();
                knownEffects.put(player.getUniqueID(), currentEffects);
                shouldShow = true;
                break;
            }
            if (!currentEffects.equals(lastKnown)) {
                knownEffects.put(player.getUniqueID(), currentEffects);
            }
        }
        if (shouldShow && !enemyEffectActive) {
            notifyWarning("Противник получил эффект §h§" + enemyEffectName + "§h§");
        }
        enemyEffectActive = shouldShow;
    }

    private void checkEnemyEating() {
        boolean shouldShow = false;
        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == mc.player || player.isSpectator() || mc.player.getDistanceSq(player) > 64) continue;
            if (player.isHandActive() && player.getActiveItemStack().isFood()) {
                shouldShow = true;
                enemyEatenItemName = player.getActiveItemStack().getDisplayName().getString();
                break;
            }
        }
        if (shouldShow && !enemyEatActive) {
            notifyWarning("Противник взял §h§" + enemyEatenItemName + "§h§");
            enemyEatTimer.reset();
        }
        enemyEatActive = shouldShow || (enemyEatActive && !enemyEatTimer.finished(3000));
    }

    private void checkEnemyHoldingSplash() {
        boolean shouldShow = false;
        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == mc.player || player.isSpectator() || mc.player.getDistanceSq(player) > 64) continue;
            if (player.getHeldItemMainhand().getItem() == Items.SPLASH_POTION ||
                    player.getHeldItemOffhand().getItem() == Items.SPLASH_POTION) {
                shouldShow = true;
                enemySplashPotionName = player.getHeldItemMainhand().getItem() == Items.SPLASH_POTION ?
                        player.getHeldItemMainhand().getDisplayName().getString() :
                        player.getHeldItemOffhand().getDisplayName().getString();
                break;
            }
        }
        if (shouldShow && !enemySplashActive) {
            notifyWarning("Противник взял §h§" + enemySplashPotionName + "§h§");
            enemySplashTimer.reset();
        }
        enemySplashActive = shouldShow || (enemySplashActive && !enemySplashTimer.finished(3000));
    }

    private void checkEnemyHolding(Item item, String itemName, boolean activeFlag, StopWatch timer) {
        boolean shouldShow = false;
        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == mc.player || player.isSpectator() || mc.player.getDistanceSq(player) > 64) continue;
            if (player.getHeldItemMainhand().getItem() == item || player.getHeldItemOffhand().getItem() == item) {
                shouldShow = true;
                break;
            }
        }
        if (shouldShow && !activeFlag) {
            notifyWarning("Противник взял §h§" + itemName + "§h§");
            timer.reset();
        }
        if (item == Items.CROSSBOW) enemyCrossbowActive = shouldShow || (activeFlag && !timer.finished(3000));
        else if (item == Items.ENDER_PEARL) enemyEnderpearlActive = shouldShow || (activeFlag && !timer.finished(3000));
        else enemyBowActive = shouldShow || (activeFlag && !timer.finished(3000));
    }

    private void checkEnemyAiming() {
        boolean shouldShow = false;
        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == mc.player || player.isSpectator() || mc.player.getDistanceSq(player) > 64) continue;
            if (player.isHandActive() && player.getActiveItemStack().getItem() == Items.BOW) {
                shouldShow = true;
                break;
            }
        }
        if (shouldShow && !enemyAimingActive) {
            notifyWarning("Противник целится с лука");
            enemyAimingTimer.reset();
        }
        enemyAimingActive = shouldShow || (enemyAimingActive && !enemyAimingTimer.finished(3000));
    }

    private ItemStack getItemStackByKey(String key) {
        for (int i = 0; i < mc.player.inventory.getSizeInventory(); i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (!stack.isEmpty() && Registry.ITEM.getKey(stack.getItem()).toString().equals(key)) {
                return stack;
            }
        }
        return null;
    }

    private void updateInventorySnapshot() {
        if (mc.player == null) return;
        inventoryItems.clear();
        for (int i = 0; i < mc.player.inventory.getSizeInventory(); i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (!stack.isEmpty() && TRACKED_ITEMS.contains(stack.getItem())) {
                String key = Registry.ITEM.getKey(stack.getItem()).toString();
                inventoryItems.put(key, inventoryItems.getOrDefault(key, 0) + stack.getCount());
            }
        }
    }

    // === ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ===
    private String getPotionKey(ItemStack potionStack) {
        Potion potion = PotionUtils.getPotionFromItem(potionStack);
        StringBuilder key = new StringBuilder(Registry.POTION.getKey(potion).toString());
        for (EffectInstance effect : PotionUtils.getEffectsFromStack(potionStack)) {
            key.append("_").append(Registry.EFFECTS.getKey(effect.getPotion()).toString())
                    .append("_").append(effect.getAmplifier());
        }
        return key.toString();
    }

    private boolean wasAlreadyNotified(String playerName, String potionKey) {
        Set<String> notifiedPotions = playerNotifiedPotions.get(playerName);
        return notifiedPotions != null && notifiedPotions.contains(potionKey);
    }

    private void markAsNotified(String playerName, String potionKey) {
        playerNotifiedPotions.computeIfAbsent(playerName, k -> new HashSet<>()).add(potionKey);
    }

    private boolean canSendNotification() {
        long currentTime = System.currentTimeMillis();
        long oneSecondAgo = currentTime - ONE_SECOND_MS;
        return recentNotifications.stream().filter(t -> t > oneSecondAgo).count() < MAX_NOTIFICATIONS_PER_SECOND;
    }

    private void addNotificationTimestamp() {
        recentNotifications.add(System.currentTimeMillis());
    }

    private void cleanupOldNotificationTimestamps() {
        long oneSecondAgo = System.currentTimeMillis() - ONE_SECOND_MS;
        recentNotifications.removeIf(timestamp -> timestamp <= oneSecondAgo);
    }

    private void cleanupOldPlayerData() {
        if (mc.world == null) return;
        Set<String> activePlayers = mc.world.getPlayers().stream()
                .filter(Objects::nonNull)
                .map(p -> p.getName().getString())
                .collect(Collectors.toSet());
        playerNotifiedPotions.keySet().retainAll(activePlayers);
        playerNotifiedPotions.values().forEach(set -> {
            if (set.size() > 50) set.clear();
        });
    }

    private TextFormatting getColorFromInt(int color) {
        int red = (color >> 16) & 0xFF;
        int green = (color >> 8) & 0xFF;
        int blue = color & 0xFF;
        if (red > green && red > blue) return TextFormatting.RED;
        if (green > red && green > blue) return TextFormatting.GREEN;
        if (blue > red && blue > green) return TextFormatting.BLUE;
        if (red > 240 && green > 100 && blue < 100) return TextFormatting.GOLD;
        if (red > 174 && green < 100 && blue > 174) return TextFormatting.LIGHT_PURPLE;
        if (red < 100 && green > 174 && blue > 174) return TextFormatting.AQUA;
        return TextFormatting.WHITE;
    }

    private String toRoman(int number) {
        return switch (number) {
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            case 4 -> "IV";
            case 5 -> "V";
            case 6 -> "VI";
            case 7 -> "VII";
            case 8 -> "VIII";
            case 9 -> "IX";
            case 10 -> "X";
            default -> String.valueOf(number);
        };
    }

    private boolean isFriend(String playerName) {
        return Excellent.inst().friendManager().isFriend(playerName);
    }

    private void sendNotification(String message, ItemStack stack) {
        if (useNotifications.getValue()) {
            // Проверка на дубликат - не отправлять одинаковые уведомления чаще чем раз в секунду
            if (isDuplicateNotification(message)) {
                return;
            }
            
            if (!canSendNotification()) {
                return;
            }
            
            Integer color = stack != null && !stack.isEmpty() ? 
                    org.privates.client.screen.notification.NotificationColors.getItemColor(stack.getItem()) : null;
            Excellent.inst().notifManager().add(
                    new Notification(NotificationType.INFO, message, 3000, stack != null ? stack.copy() : ItemStack.EMPTY, color)
            );
            
            addNotificationTimestamp();
            markNotificationSent(message);
        } else {
            ChatUtil.addText(message);
        }
    }

    private void notifyEffectEnd(Effect effect, int amplifier, String text) {
        if (isDuplicateNotification(text)) {
            return;
        }
        
        if (!canSendNotification()) {
            return;
        }
        
        Integer color = org.privates.client.screen.notification.NotificationColors.getEffectColor(effect);
        Excellent.inst().notifManager().add(
                new Notification(NotificationType.EFFECT_END, text, 3000, effect, color)
        );
        
        addNotificationTimestamp();
        markNotificationSent(text);
    }

    private void notifyItemPickup(ItemStack stack, String text) {
        if (isDuplicateNotification(text)) {
            return;
        }
        
        if (!canSendNotification()) {
            return;
        }
        
        Integer color = org.privates.client.screen.notification.NotificationColors.getItemColor(stack.getItem());
        Excellent.inst().notifManager().add(
                new Notification(NotificationType.ITEM_PICKUP, text, 3000, stack.copy(), color)
        );
        
        addNotificationTimestamp();
        markNotificationSent(text);
    }

    private void notifySwap(ItemStack stack, String text) {
        if (isDuplicateNotification(text)) {
            return;
        }
        
        if (!canSendNotification()) {
            return;
        }
        
        Integer color = org.privates.client.screen.notification.NotificationColors.getItemColor(stack.getItem());
        Excellent.inst().notifManager().add(
                new Notification(NotificationType.SWAP, text, 3000, stack.copy(), color)
        );
        
        addNotificationTimestamp();
        markNotificationSent(text);
    }

    private void notifyWarning(String text) {
        if (isDuplicateNotification(text)) {
            return;
        }
        
        if (!canSendNotification()) {
            return;
        }
        
        Integer color = org.privates.client.screen.notification.NotificationColors.getDefaultColor(NotificationType.WARNING);
        Excellent.inst().notifManager().add(
                new Notification(NotificationType.WARNING, text, 3000, null, color)
        );
        
        addNotificationTimestamp();
        markNotificationSent(text);
    }
    
    /**
     * Проверяет, было ли уже отправлено такое же уведомление недавно
     */
    private boolean isDuplicateNotification(String text) {
        long currentTime = System.currentTimeMillis();
        Long lastTime = lastNotificationByText.get(text);
        
        // Если такое уведомление уже было отправлено менее секунды назад - это дубликат
        if (lastTime != null && (currentTime - lastTime) < ONE_SECOND_MS) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Отмечает что уведомление с таким текстом было отправлено
     */
    private void markNotificationSent(String text) {
        lastNotificationByText.put(text, System.currentTimeMillis());
        
        // Очистка старых записей (старше 2 секунд)
        long twoSecondsAgo = System.currentTimeMillis() - (ONE_SECOND_MS * 2);
        lastNotificationByText.entrySet().removeIf(entry -> entry.getValue() < twoSecondsAgo);
    }

    // === ВСПОМОГАТЕЛЬНЫЕ КЛАССЫ ===
    private static class PotionData {
        ItemStack stack;
        double lastX, lastY, lastZ;

        PotionData(ItemStack stack, double x, double y, double z) {
            this.stack = stack;
            this.lastX = x;
            this.lastY = y;
            this.lastZ = z;
        }
    }

    private static class PlayerHandState {
        ItemStack mainHand = ItemStack.EMPTY;
        ItemStack offHand = ItemStack.EMPTY;
        int mainHandUseTicks = 0;
        int offHandUseTicks = 0;
        boolean mainHandWasUsing = false;
        boolean offHandWasUsing = false;
    }
}
