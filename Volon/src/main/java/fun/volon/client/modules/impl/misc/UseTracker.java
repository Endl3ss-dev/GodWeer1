package fun.volon.client.modules.impl.misc;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import fun.volon.api.events.EventLink;
import fun.volon.api.events.implement.EventUpdate;
import fun.volon.api.utils.notification.NotificationManager;
import fun.volon.client.modules.Module;
import fun.volon.client.modules.settings.implement.BooleanSetting;

import java.util.HashMap;
import java.util.Map;

public class UseTracker extends Module {

    public static UseTracker INSTANCE = new UseTracker();

    private final BooleanSetting trackGoldenApple = new BooleanSetting("Золотое яблоко", true);
    private final BooleanSetting trackEnchantedApple = new BooleanSetting("Зачарованное яблоко", true);
    private final BooleanSetting trackEnderPearl = new BooleanSetting("Эндер перл", true);
    private final BooleanSetting trackPotion = new BooleanSetting("Зелья", true);

    // Отслеживание использования предметов игроками
    private final Map<String, PlayerItemState> playerStates = new HashMap<>();
    
    // Cooldown для предотвращения спама уведомлений
    private final Map<String, Long> notificationCooldowns = new HashMap<>();
    private static final long NOTIFICATION_COOLDOWN_MS = 1000L;

    public UseTracker() {
        super("UseTracker", "Отслеживает использование предметов игроками", ModuleCategory.MISC);
        addSettings(trackGoldenApple, trackEnchantedApple, trackEnderPearl, trackPotion);
    }

    @Override
    public void onDisable() {
        playerStates.clear();
        notificationCooldowns.clear();
        super.onDisable();
    }

    @EventLink
    public void onUpdate(EventUpdate event) {
        if (mc.player == null || mc.world == null) {
            playerStates.clear();
            notificationCooldowns.clear();
            return;
        }

        long currentTime = System.currentTimeMillis();

        // Проверяем всех игроков в радиусе
        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == null || player == mc.player) continue;
            if (!player.isAlive()) continue;
            
            // Проверяем дистанцию (50 блоков)
            if (mc.player.squaredDistanceTo(player) > 50 * 50) continue;

            String playerName = player.getName().getString();
            PlayerItemState state = playerStates.computeIfAbsent(playerName, k -> new PlayerItemState());

            // Проверяем активное использование предмета
            if (player.isUsingItem()) {
                ItemStack activeStack = player.getActiveItem();
                Hand activeHand = player.getActiveHand();
                
                if (!activeStack.isEmpty()) {
                    Item item = activeStack.getItem();
                    
                    // Если игрок начал использовать новый предмет
                    if (!state.isUsing || !ItemStack.areEqual(state.lastUsedStack, activeStack)) {
                        state.isUsing = true;
                        state.lastUsedStack = activeStack.copy();
                        state.useStartTime = currentTime;
                    }
                }
            } else {
                // Игрок перестал использовать предмет
                if (state.isUsing && state.lastUsedStack != null) {
                    long useDuration = currentTime - state.useStartTime;
                    
                    // Проверяем что предмет был использован достаточно долго
                    if (useDuration > 100) { // минимум 100ms использования
                        checkItemUsage(playerName, state.lastUsedStack, currentTime);
                    }
                    
                    state.isUsing = false;
                    state.lastUsedStack = null;
                }
            }

            // Отдельная проверка для эндер перлов (они используются мгновенно)
            if (trackEnderPearl.isState()) {
                ItemStack mainHand = player.getMainHandStack();
                ItemStack offHand = player.getOffHandStack();
                
                // Проверяем изменение количества эндер перлов
                if (!mainHand.isEmpty() && mainHand.getItem() == Items.ENDER_PEARL) {
                    int currentCount = mainHand.getCount();
                    if (state.lastPearlCount > currentCount) {
                        sendNotification(playerName, "Эндер перл", new ItemStack(Items.ENDER_PEARL), currentTime);
                    }
                    state.lastPearlCount = currentCount;
                } else if (!offHand.isEmpty() && offHand.getItem() == Items.ENDER_PEARL) {
                    int currentCount = offHand.getCount();
                    if (state.lastPearlCount > currentCount) {
                        sendNotification(playerName, "Эндер перл", new ItemStack(Items.ENDER_PEARL), currentTime);
                    }
                    state.lastPearlCount = currentCount;
                } else {
                    state.lastPearlCount = 0;
                }
            }
        }

        // Очистка старых cooldown'ов
        notificationCooldowns.entrySet().removeIf(entry -> 
            currentTime - entry.getValue() > NOTIFICATION_COOLDOWN_MS * 2
        );
    }

    private void checkItemUsage(String playerName, ItemStack stack, long currentTime) {
        if (stack == null || stack.isEmpty()) return;

        Item item = stack.getItem();
        String itemName = null;
        ItemStack iconStack = null;

        // Золотое яблоко
        if (trackGoldenApple.isState() && item == Items.GOLDEN_APPLE) {
            itemName = "Золотое яблоко";
            iconStack = new ItemStack(Items.GOLDEN_APPLE);
        }
        // Зачарованное яблоко
        else if (trackEnchantedApple.isState() && item == Items.ENCHANTED_GOLDEN_APPLE) {
            itemName = "Зачарованное яблоко";
            iconStack = new ItemStack(Items.ENCHANTED_GOLDEN_APPLE);
        }
        // Зелья
        else if (trackPotion.isState() && (item == Items.POTION || item == Items.SPLASH_POTION || item == Items.LINGERING_POTION)) {
            itemName = "Зелье";
            iconStack = stack.copy(); // Используем сам стек зелья чтобы сохранить тип
        }

        if (itemName != null && iconStack != null) {
            sendNotification(playerName, itemName, iconStack, currentTime);
        }
    }

    private void sendNotification(String playerName, String itemName, ItemStack iconStack, long currentTime) {
        String key = playerName + ":" + itemName;
        
        // Проверяем cooldown
        Long lastNotification = notificationCooldowns.get(key);
        if (lastNotification != null && currentTime - lastNotification < NOTIFICATION_COOLDOWN_MS) {
            return;
        }

        // Отправляем уведомление с иконкой предмета
        String message = playerName + " использовал " + itemName;
        NotificationManager.pushItemNotification(message, iconStack);
        
        // Обновляем cooldown
        notificationCooldowns.put(key, currentTime);
    }

    private static class PlayerItemState {
        boolean isUsing = false;
        ItemStack lastUsedStack = null;
        long useStartTime = 0;
        int lastPearlCount = 0;
    }
}
