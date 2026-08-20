package org.privates.client.managers.module.impl.player;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.registry.Registry;
import org.privates.client.Excellent;
import org.privates.client.api.annotations.Beta;
import org.privates.client.api.annotations.Category1;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.MultiBooleanSetting;
import org.privates.client.screen.notification.Notification;
import org.privates.client.screen.notification.NotificationType;
import org.privates.client.utils.other.Instance;
import org.privates.lib.util.time.StopWatch;

import java.util.*;
import java.util.stream.Collectors;

@Category1
@Beta
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "NotifyHelper", category = Category.PLAYER, description = "Помогает лучше понимать обычные действия через Notify то есть уведомления")
public class NotifyHelper extends Module {

    public static NotifyHelper getInstance() {
        return Instance.get(NotifyHelper.class);
    }

    private final MultiBooleanSetting notifications = new MultiBooleanSetting(this, "Уведомления",
            BooleanSetting.of("Окончание эффектов", true),
            BooleanSetting.of("Подбор предметов", true),
            BooleanSetting.of("Свапы предметов", true),
            BooleanSetting.of("Низкое здоровье", true),
            BooleanSetting.of("Предупреждение о зельях", true),
            BooleanSetting.of("Элитра сломается", true),
            BooleanSetting.of("Низкий голод", true),
            BooleanSetting.of("Враг пьет зелье", true),
            BooleanSetting.of("Враг получает эффект", true),
            BooleanSetting.of("Враг ест", true),
            BooleanSetting.of("Враг держит сплеш", true),
            BooleanSetting.of("Враг держит арбалет", true),
            BooleanSetting.of("Враг держит эндер перл", true),
            BooleanSetting.of("Враг держит лук", true),
            BooleanSetting.of("Враг целится луком", true)
    );

    private final Map<Effect, Integer> activeEffects = new HashMap<>();
    private final Map<String, Integer> inventoryItems = new HashMap<>();
    private final Map<UUID, Set<Effect>> knownEffects = new HashMap<>();
    private final StopWatch previewTimer = new StopWatch();
    private int previewIndex = 0;
    private int previousSlot = -1;

    private boolean lowHealthActive = false;
    private boolean potionActive = false;
    private boolean elytraActive = false;
    private boolean hungerActive = false;
    private boolean enemyPotionActive = false;
    private boolean enemyEffectActive = false;
    private boolean enemyEatActive = false;
    private boolean enemySplashActive = false;
    private boolean enemyCrossbowActive = false;
    private boolean enemyEnderpearlActive = false;
    private boolean enemyBowActive = false;
    private boolean enemyAimingActive = false;

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

    private static final Set<Item> TRACKED_ITEMS = new HashSet<>(Arrays.asList(
            Items.NETHERITE_HELMET, Items.NETHERITE_CHESTPLATE, Items.NETHERITE_LEGGINGS, Items.NETHERITE_BOOTS,
            Items.NETHERITE_SWORD, Items.NETHERITE_PICKAXE, Items.NETHERITE_AXE, Items.NETHERITE_SHOVEL, Items.NETHERITE_HOE,
            Items.NETHERITE_INGOT, Items.NETHERITE_SCRAP,
            Items.ELYTRA, Items.TOTEM_OF_UNDYING, Items.PLAYER_HEAD,
            Items.FIREWORK_ROCKET, Items.END_CRYSTAL, Items.ENDER_EYE,
            Items.NETHER_STAR, Items.PRISMARINE_SHARD, Items.ENCHANTED_GOLDEN_APPLE,
            Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION, Items.SUGAR
    ));

    private static final List<PreviewData> PREVIEW_ITEMS = Arrays.asList(
            new PreviewData(new ItemStack(Items.NETHERITE_SWORD, 1), "Вы подобрали Netherite Sword"),
            new PreviewData(new ItemStack(Items.TOTEM_OF_UNDYING, 1), "Вы подобрали Totem of Undying"),
            new PreviewData(new ItemStack(Items.ENCHANTED_GOLDEN_APPLE, 3), "Вы подобрали 3x Enchanted Golden Apple"),
            new PreviewData(new ItemStack(Items.FIREWORK_ROCKET, 186), "Вы подобрали 186x Firework Rocket"),
            new PreviewData(new ItemStack(Items.END_CRYSTAL, 1), "Вы подобрали End Crystal"),
            new PreviewData(new ItemStack(Items.ELYTRA, 1), "Вы подобрали Elytra")
    );

    private static final List<PreviewEffectData> PREVIEW_EFFECTS = Arrays.asList(
            new PreviewEffectData(Effects.SPEED, 1, "У вас закончился эффект Speed 2"),
            new PreviewEffectData(Effects.STRENGTH, 0, "У вас закончился эффект Strength"),
            new PreviewEffectData(Effects.REGENERATION, 1, "У вас закончился эффект Regeneration 2"),
            new PreviewEffectData(Effects.FIRE_RESISTANCE, 0, "У вас закончился эффект Fire Resistance")
    );

    private static final List<PreviewSwapData> PREVIEW_SWAPS = Arrays.asList(
            new PreviewSwapData(new ItemStack(Items.TOTEM_OF_UNDYING), "Свапнули на Totem of Undying"),
            new PreviewSwapData(new ItemStack(Items.ENDER_PEARL), "Свапнули на Ender Pearl"),
            new PreviewSwapData(new ItemStack(Items.BOW), "Свапнули на Bow")
    );

    private static final List<String> PREVIEW_WARNINGS = Arrays.asList(
            "У вас мало здоровья",
            "Заканчивается зелье силы",
            "Элитра почти сломана: 12",
            "У вас мало голода",
            "Противник взял Зелье силы",
            "Противник получил эффект скорости",
            "Противник взял золотое яблоко",
            "Противник взял Взрывное зелье вреда",
            "Противник взял арбалет",
            "Противник взял эндер жемчуг",
            "Противник взял лук",
            "Противник целится с лука"
    );

    @Override
    public void onEnable() {
        super.onEnable();
        activeEffects.clear();
        inventoryItems.clear();
        knownEffects.clear();
        previewTimer.reset();
        previewIndex = 0;
        previousSlot = mc.player != null ? mc.player.inventory.currentItem : -1;
        lowHealthActive = false;
        potionActive = false;
        elytraActive = false;
        hungerActive = false;
        enemyPotionActive = false;
        enemyEffectActive = false;
        enemyEatActive = false;
        enemySplashActive = false;
        enemyCrossbowActive = false;
        enemyEnderpearlActive = false;
        enemyBowActive = false;
        enemyAimingActive = false;
        updateInventorySnapshot();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        activeEffects.clear();
        inventoryItems.clear();
        knownEffects.clear();
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (mc.player == null || mc.world == null) return;

        if (mc.currentScreen instanceof ChatScreen) {
            handlePreview();
            return;
        }

        if (notifications.getValue("Свапы предметов")) {
            checkSwaps();
        }

        if (notifications.getValue("Окончание эффектов")) {
            checkEffects();
        }

        if (notifications.getValue("Подбор предметов")) {
            checkInventory();
        }

        if (notifications.getValue("Низкое здоровье")) {
            checkLowHealth();
        }

        if (notifications.getValue("Предупреждение о зельях")) {
            checkPotionWarning();
        }

        if (notifications.getValue("Элитра сломается")) {
            checkElytraWarning();
        }

        if (notifications.getValue("Низкий голод")) {
            checkHungerWarning();
        }

        if (notifications.getValue("Враг пьет зелье")) {
            checkEnemyPotionDrink();
        }

        if (notifications.getValue("Враг получает эффект")) {
            checkEnemyEffectGained();
        }

        if (notifications.getValue("Враг ест")) {
            checkEnemyEating();
        }

        if (notifications.getValue("Враг держит сплеш")) {
            checkEnemyHoldingSplash();
        }

        if (notifications.getValue("Враг держит арбалет")) {
            checkEnemyHolding(Items.CROSSBOW);
        }

        if (notifications.getValue("Враг держит эндер перл")) {
            checkEnemyHolding(Items.ENDER_PEARL);
        }

        if (notifications.getValue("Враг держит лук")) {
            checkEnemyHolding(Items.BOW);
        }

        if (notifications.getValue("Враг целится луком")) {
            checkEnemyAiming();
        }
    }

    private void handlePreview() {
        if (previewTimer.finished(2500)) {
            int totalPreviews = PREVIEW_ITEMS.size() + PREVIEW_EFFECTS.size() + PREVIEW_SWAPS.size() + PREVIEW_WARNINGS.size();
            int index = previewIndex % totalPreviews;
            if (index < PREVIEW_ITEMS.size()) {
                PreviewData preview = PREVIEW_ITEMS.get(index);
                notifyItemPickup(preview.stack, preview.text);
            } else if (index < PREVIEW_ITEMS.size() + PREVIEW_EFFECTS.size()) {
                int effectIndex = index - PREVIEW_ITEMS.size();
                PreviewEffectData preview = PREVIEW_EFFECTS.get(effectIndex);
                notifyEffectEnd(preview.effect, preview.amplifier, preview.text);
            } else if (index < PREVIEW_ITEMS.size() + PREVIEW_EFFECTS.size() + PREVIEW_SWAPS.size()) {
                int swapIndex = index - PREVIEW_ITEMS.size() - PREVIEW_EFFECTS.size();
                PreviewSwapData preview = PREVIEW_SWAPS.get(swapIndex);
                notifySwap(preview.stack, preview.text);
            } else {
                int warningIndex = index - PREVIEW_ITEMS.size() - PREVIEW_EFFECTS.size() - PREVIEW_SWAPS.size();
                String text = PREVIEW_WARNINGS.get(warningIndex);
                notifyWarning(text);
            }
            previewIndex++;
            previewTimer.reset();
        }
    }

    private void checkSwaps() {
        int currentSlot = mc.player.inventory.currentItem;
        if (currentSlot != previousSlot) {
            ItemStack swappedItem = mc.player.inventory.getCurrentItem();
            if (!swappedItem.isEmpty()) {
                String itemName = swappedItem.getDisplayName().getString();
                String text = "Свапнули на " + itemName;
                notifySwap(swappedItem.copy(), text);
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
            Effect effect = entry.getKey();
            int amplifier = entry.getValue();

            if (!currentEffects.containsKey(effect)) {
                String effectName = effect.getDisplayName().getString();
                String level = amplifier > 0 ? " " + (amplifier + 1) : "";
                String text = "У вас закончился эффект " + effectName + level;
                notifyEffectEnd(effect, amplifier, text);
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
            String itemKey = entry.getKey();
            int newCount = entry.getValue();
            int oldCount = inventoryItems.getOrDefault(itemKey, 0);

            if (newCount > oldCount) {
                ItemStack stack = getItemStackByKey(itemKey);
                if (stack != null) {
                    int difference = newCount - oldCount;
                    ItemStack notifyStack = stack.copy();
                    notifyStack.setCount(difference);

                    String itemName = stack.getDisplayName().getString();
                    String text;
                    if (difference > 64) {
                        text = "Вы подобрали " + difference + "x " + itemName;
                    } else if (difference > 1) {
                        text = "Вы подобрали " + difference + "x " + itemName;
                    } else {
                        text = "Вы подобрали " + itemName;
                    }

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
        boolean active = shouldShow || (lowHealthActive && !lowHealthTimer.finished(3000));
        lowHealthActive = active;
    }

    private void checkPotionWarning() {
        Effect targetEffect = null;
        for (EffectInstance effect : mc.player.getActivePotionEffects()) {
            if ((effect.getPotion() == Effects.STRENGTH || effect.getPotion() == Effects.SPEED) && effect.getDuration() <= 15 * 20) {
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
        boolean active = shouldShow || (potionActive && !potionTimer.finished(3000));
        potionActive = active;
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
        boolean active = shouldShow || (elytraActive && !elytraTimer.finished(3000));
        elytraActive = active;
    }

    private void checkHungerWarning() {
        boolean shouldShow = mc.player.getFoodStats().getFoodLevel() <= 12;
        if (shouldShow && !hungerActive) {
            notifyWarning("У вас мало голода");
            hungerTimer.reset();
        }
        boolean active = shouldShow || (hungerActive && !hungerTimer.finished(3000));
        hungerActive = active;
    }

    private void checkEnemyPotionDrink() {
        boolean shouldShow = false;
        enemyPotionName = "";
        if (mc.world == null || mc.player == null) return;
        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == mc.player || player.isSpectator() || mc.player.getDistanceSq(player) > 64) continue;
            if (player.isHandActive() && player.getActiveItemStack().getItem() == Items.POTION) {
                shouldShow = true;
                enemyPotionName = player.getActiveItemStack().getDisplayName().getString();
                break;
            }
        }
        if (shouldShow && !enemyPotionActive) {
            notifyWarning("Противник взял " + enemyPotionName);
            enemyPotionTimer.reset();
        }
        boolean active = shouldShow || (enemyPotionActive && !enemyPotionTimer.finished(3000));
        enemyPotionActive = active;
    }

    private void checkEnemyEffectGained() {
        if (mc.world == null || mc.player == null) return;

        knownEffects.keySet().removeIf(uuid -> mc.world.getPlayerByUuid(uuid) == null);

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
                Effect firstNew = newEffects.iterator().next();
                enemyEffectName = firstNew.getDisplayName().getString();
                knownEffects.put(player.getUniqueID(), currentEffects);
                shouldShow = true;
                break;
            }
            if (!currentEffects.equals(lastKnown)) {
                knownEffects.put(player.getUniqueID(), currentEffects);
            }
        }
        if (shouldShow && !enemyEffectActive) {
            notifyWarning("Противник получил эффект " + enemyEffectName);
            enemyEffectActive = true;
        } else if (!shouldShow) {
            enemyEffectActive = false;
        }
    }

    private void checkEnemyEating() {
        boolean shouldShow = false;
        enemyEatenItemName = "";
        if (mc.world == null || mc.player == null) return;
        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == mc.player || player.isSpectator() || mc.player.getDistanceSq(player) > 64) continue;
            if (player.isHandActive() && player.getActiveItemStack().isFood()) {
                shouldShow = true;
                enemyEatenItemName = player.getActiveItemStack().getDisplayName().getString();
                break;
            }
        }
        if (shouldShow && !enemyEatActive) {
            notifyWarning("Противник взял " + enemyEatenItemName);
            enemyEatTimer.reset();
        }
        boolean active = shouldShow || (enemyEatActive && !enemyEatTimer.finished(3000));
        enemyEatActive = active;
    }

    private void checkEnemyHoldingSplash() {
        boolean shouldShow = false;
        enemySplashPotionName = "";
        if (mc.world == null || mc.player == null) return;
        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == mc.player || player.isSpectator() || mc.player.getDistanceSq(player) > 64) continue;
            ItemStack mainHand = player.getHeldItemMainhand();
            ItemStack offHand = player.getHeldItemOffhand();
            if (mainHand.getItem() == Items.SPLASH_POTION) {
                shouldShow = true;
                enemySplashPotionName = mainHand.getDisplayName().getString();
                break;
            }
            if (offHand.getItem() == Items.SPLASH_POTION) {
                shouldShow = true;
                enemySplashPotionName = offHand.getDisplayName().getString();
                break;
            }
        }
        if (shouldShow && !enemySplashActive) {
            notifyWarning("Противник взял " + enemySplashPotionName);
            enemySplashTimer.reset();
        }
        boolean active = shouldShow || (enemySplashActive && !enemySplashTimer.finished(3000));
        enemySplashActive = active;
    }

    private void checkEnemyHolding(Item item) {
        boolean shouldShow = false;
        String itemName = item == Items.CROSSBOW ? "арбалет" : item == Items.ENDER_PEARL ? "эндер жемчуг" : "лук";
        if (mc.world == null || mc.player == null) return;
        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == mc.player || player.isSpectator() || mc.player.getDistanceSq(player) > 64) continue;
            if (player.getHeldItemMainhand().getItem() == item || player.getHeldItemOffhand().getItem() == item) {
                shouldShow = true;
                break;
            }
        }
        boolean activeFlag = item == Items.CROSSBOW ? enemyCrossbowActive : item == Items.ENDER_PEARL ? enemyEnderpearlActive : enemyBowActive;
        StopWatch timer = item == Items.CROSSBOW ? enemyCrossbowTimer : item == Items.ENDER_PEARL ? enemyEnderpearlTimer : enemyBowTimer;
        if (shouldShow && !activeFlag) {
            notifyWarning("Противник взял " + itemName);
            timer.reset();
        }
        boolean active = shouldShow || (activeFlag && !timer.finished(3000));
        if (item == Items.CROSSBOW) enemyCrossbowActive = active;
        else if (item == Items.ENDER_PEARL) enemyEnderpearlActive = active;
        else enemyBowActive = active;
    }

    private void checkEnemyAiming() {
        boolean shouldShow = false;
        if (mc.world == null || mc.player == null) return;
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
        boolean active = shouldShow || (enemyAimingActive && !enemyAimingTimer.finished(3000));
        enemyAimingActive = active;
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

    private void notifyEffectEnd(Effect effect, int amplifier, String text) {
        Notification notification = new Notification(
                NotificationType.EFFECT_END,
                text,
                3000,
                effect
        );
        Excellent.inst().notifManager().add(notification);
    }

    private void notifyItemPickup(ItemStack stack, String text) {
        Notification notification = new Notification(
                NotificationType.ITEM_PICKUP,
                text,
                3000,
                stack.copy()
        );
        Excellent.inst().notifManager().add(notification);
    }

    private void notifySwap(ItemStack stack, String text) {
        Notification notification = new Notification(
                NotificationType.SWAP,
                text,
                3000,
                stack.copy()
        );
        Excellent.inst().notifManager().add(notification);
    }

    public void notifyWarning(String text) {
        Notification notification = new Notification(
                NotificationType.WARNING,
                text,
                3000
        );
        Excellent.inst().notifManager().add(notification);
    }

    public void notifyError(String text) {
        Notification notification = new Notification(
                NotificationType.ERROR,
                text,
                3000
        );
        Excellent.inst().notifManager().add(notification);
    }

    public void notifyInfo(String text) {
        Notification notification = new Notification(
                NotificationType.INFO,
                text,
                3000
        );
        Excellent.inst().notifManager().add(notification);
    }

    private record PreviewData(ItemStack stack, String text) {
    }

    private record PreviewEffectData(Effect effect, int amplifier, String text) {
    }

    private record PreviewSwapData(ItemStack stack, String text) {
    }
}