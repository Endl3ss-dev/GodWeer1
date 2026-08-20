package org.privates.client.managers.module.impl.misc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.util.Hand;
import org.privates.client.api.annotations.Category2;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.input.KeyboardPressEvent;
import org.privates.client.managers.events.input.MousePressEvent;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BindSetting;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.MultiBooleanSetting;
import org.privates.client.utils.chat.ChatUtil;
import org.privates.client.utils.player.InvUtil;
import org.privates.lib.util.time.StopWatch;

import java.util.HashMap;
import java.util.Map;

@Category2
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "WellMineHelper", category = Category.PLAYER, description = "Помогает использвать предметы пред назначеные для wellmine")
public class WellMineHelper extends Module {

    private final MultiBooleanSetting features = new MultiBooleanSetting(this, "Функции",
            BooleanSetting.of("Горячие клавиши", true),
            BooleanSetting.of("Авто-хил от шара лета", false));

    private final Map<Item, BindSetting> itemBindings = new HashMap<>();
    private final StopWatch slotSwitchDelay = new StopWatch();
    private final StopWatch autoHealDelay = new StopWatch();

    private long lastActionTime = 0L;
    private static final long ACTION_DELAY = 100L;
    private static final long SLOT_SWITCH_DELAY = 50L;
    private static final long AUTO_HEAL_DELAY = 500L;

    private int originalSlot = -1;
    private boolean awaitingSlotSwitch = false;

    public WellMineHelper() {
        initializeItemBindings();
        autoHealDelay.reset();
    }

    private void initializeItemBindings() {
        // Ливалка (clay_ball или magma_cream)
        itemBindings.put(Items.CLAY_BALL, new BindSetting(this, "Ливалка (Глина)")
                .setVisible(() -> features.getValue("Горячие клавиши")));
        itemBindings.put(Items.MAGMA_CREAM, new BindSetting(this, "Ливалка (Магма)")
                .setVisible(() -> features.getValue("Горячие клавиши")));
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        // Обработка задержки переключения слотов
        if (mc.currentScreen == null && features.getValue("Горячие клавиши")) {
            if (awaitingSlotSwitch && slotSwitchDelay.finished(SLOT_SWITCH_DELAY)) {
                mc.rightClickMouse();
                mc.player.swingArm(Hand.MAIN_HAND);
                awaitingSlotSwitch = false;
            }

            if (originalSlot != -1 && System.currentTimeMillis() - lastActionTime >= ACTION_DELAY) {
                mc.player.inventory.currentItem = originalSlot;
                mc.gameRenderer.itemRenderer.resetEquippedProgress(Hand.MAIN_HAND);
                originalSlot = -1;
            }
        }

        // Авто-хил от шара лета
        if (features.getValue("Авто-хил от шара лета") && autoHealDelay.finished(AUTO_HEAL_DELAY)) {
            checkAndUseAutoHeal();
            autoHealDelay.reset();
        }
    }

    private void checkAndUseAutoHeal() {
        if (mc.player == null || mc.player.getHealth() > 8.0f) { // 4 сердца = 8 HP
            return;
        }

        // Поиск шара лета в инвентаре
        for (int i = 0; i < mc.player.inventory.getSizeInventory(); i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack.getItem() == Items.PLAYER_HEAD && stack.hasDisplayName()) {
                String displayName = stack.getDisplayName().getString();
                if (displayName.contains("Шар Лета")) {
                    useAutoHeal(i);
                    return;
                }
            }
        }
    }

    private void useAutoHeal(int slot) {
        int currentSlot = mc.player.inventory.currentItem;

        // Если предмет в хотбаре
        if (slot < 9) {
            mc.player.inventory.currentItem = slot;
            mc.player.connection.sendPacket(new CHeldItemChangePacket(slot));

            // Отправляем Shift + ПКМ (использование с sneaking)
            boolean wasSneaking = mc.player.isSneaking();
            mc.player.setSneaking(true);
            mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
            mc.player.setSneaking(wasSneaking);

            mc.player.inventory.currentItem = currentSlot;
            mc.player.connection.sendPacket(new CHeldItemChangePacket(currentSlot));
        } else {
            // Если предмет в основном инвентаре, нужно переместить в хотбар
            // Находим свободный слот в хотбаре или заменяем текущий
            int targetHotbarSlot = findEmptyHotbarSlot();
            if (targetHotbarSlot == -1) {
                targetHotbarSlot = currentSlot;
            }

            // Перемещаем предмет в хотбар
            swapItems(slot, targetHotbarSlot + 36);

            // Используем предмет
            mc.player.inventory.currentItem = targetHotbarSlot;
            mc.player.connection.sendPacket(new CHeldItemChangePacket(targetHotbarSlot));

            boolean wasSneaking = mc.player.isSneaking();
            mc.player.setSneaking(true);
            mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
            mc.player.setSneaking(wasSneaking);

            mc.player.inventory.currentItem = currentSlot;
            mc.player.connection.sendPacket(new CHeldItemChangePacket(currentSlot));
        }
    }

    private int findEmptyHotbarSlot() {
        for (int i = 0; i < 9; i++) {
            if (mc.player.inventory.getStackInSlot(i).isEmpty()) {
                return i;
            }
        }
        return -1;
    }

    @EventHandler
    public void onKeyPress(KeyboardPressEvent event) {
        if (event.getScreen() != null || !features.getValue("Горячие клавиши")) {
            return;
        }
        handleInput(event.getKey());
    }

    @EventHandler
    public void onMousePress(MousePressEvent event) {
        if (event.getScreen() != null || !features.getValue("Горячие клавиши")) {
            return;
        }
        handleInput(event.getKey());
    }

    private void handleInput(int key) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastActionTime < ACTION_DELAY) {
            return;
        }

        itemBindings.forEach((item, bind) -> {
            if (bind.getValue() == key) {
                if (mc.player.getCooldownTracker().hasCooldown(item)) {
                    ChatUtil.addTextWithError(item.getName().getString() + " - в кулдауне");
                    return;
                }

                // Специальная обработка для ливалки
                if (item == Items.CLAY_BALL || item == Items.MAGMA_CREAM) {
                    useLivalka(item);
                } else {
                    int hotbarSlot = findItemInHotbar(item);
                    int inventorySlot = findItemInInventory(item);

                    if (hotbarSlot == -1 && inventorySlot == -1) {
                        ChatUtil.addTextWithError(item.getName().getString() + " - нет в инвентаре");
                        return;
                    }

                    useItem(hotbarSlot, inventorySlot);
                }

                lastActionTime = currentTime;
            }
        });
    }

    private void useLivalka(Item item) {
        if (!mc.player.getCooldownTracker().hasCooldown(item)) {
            int livalkaSlot = InvUtil.getItemInHotBar(item);
            if (livalkaSlot == -1) {
                String itemName = item == Items.CLAY_BALL ? "глиняной ливалки" : "магмовой ливалки";
                ChatUtil.addTextWithError("Нет " + itemName + " в хотбаре");
                return;
            }

            originalSlot = mc.player.inventory.currentItem;

            if (livalkaSlot >= 0 && livalkaSlot <= 8) {
                mc.player.inventory.currentItem = livalkaSlot;
                slotSwitchDelay.reset();
                awaitingSlotSwitch = true;
            } else {
                String itemName = item == Items.CLAY_BALL ? "глиняной ливалки" : "магмовой ливалки";
                ChatUtil.addTextWithError("Нет " + itemName + " в хотбаре");
                return;
            }

            lastActionTime = System.currentTimeMillis();
        }
    }

    private void useItem(int hotbarSlot, int inventorySlot) {
        originalSlot = mc.player.inventory.currentItem;

        if (hotbarSlot != -1) {
            mc.player.connection.sendPacket(new CHeldItemChangePacket(hotbarSlot));
            mc.player.inventory.currentItem = hotbarSlot;
            mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
            mc.player.connection.sendPacket(new CHeldItemChangePacket(originalSlot));
            mc.player.inventory.currentItem = originalSlot;
        } else if (inventorySlot != -1) {
            swapItems(inventorySlot, originalSlot);
            mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
            swapItems(inventorySlot, originalSlot);
        }

        mc.gameRenderer.itemRenderer.resetEquippedProgress(Hand.MAIN_HAND);
    }

    private void swapItems(int slot1, int slot2) {
        mc.playerController.windowClick(0, slot1, 0, net.minecraft.inventory.container.ClickType.PICKUP, mc.player);
        mc.playerController.windowClick(0, slot2, 0, net.minecraft.inventory.container.ClickType.PICKUP, mc.player);
        mc.playerController.windowClick(0, slot1, 0, net.minecraft.inventory.container.ClickType.PICKUP, mc.player);
    }

    private int findItemInHotbar(Item item) {
        for (int i = 0; i < 9; i++) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == item) {
                return i;
            }
        }
        return -1;
    }

    private int findItemInInventory(Item item) {
        for (int i = 9; i < 36; i++) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == item) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        originalSlot = -1;
        lastActionTime = 0L;
        awaitingSlotSwitch = false;
        autoHealDelay.reset();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        originalSlot = -1;
        lastActionTime = 0L;
        awaitingSlotSwitch = false;
    }
}
