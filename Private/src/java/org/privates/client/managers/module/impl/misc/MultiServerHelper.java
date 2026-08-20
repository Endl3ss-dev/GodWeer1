package org.privates.client.managers.module.impl.misc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.Item;
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
import org.privates.client.managers.module.settings.impl.*;
import org.privates.client.utils.other.Instance;
import org.privates.lib.util.time.StopWatch;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Category2
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "MultiServerHelper", category = Category.MISC, description = "Помогает с использванием любого спец предмета для любого сервера")
public class MultiServerHelper extends Module {
    public static MultiServerHelper getInstance() {
        return Instance.get(MultiServerHelper.class);
    }

    private final ModeSetting mode = new ModeSetting(this, "Режим", "Обычный", "HolyWorld", "Легитный").set("Обычный");

    private final BooleanSetting onlyHotbar = new BooleanSetting(this, "Только хотбар", false).setVisible(() -> mode.getValue().equals("Легитный"));
    private final BooleanSetting openInventory = new BooleanSetting(this, "Открывать инвентарь", false).setVisible(() -> mode.getValue().equals("Легитный"));
    private final BooleanSetting stopMovement = new BooleanSetting(this, "Останавливать движение", true).setVisible(() -> !mode.getValue().equals("Обычный"));
    private final BooleanSetting pressShift = new BooleanSetting(this, "Нажимать Shift", false).setVisible(() -> mode.getValue().equals("Легитный"));
    private final BooleanSetting stopForwardMovement = new BooleanSetting(this, "Останавливать движение вперед", false);
    private final BooleanSetting resetSprint = new BooleanSetting(this, "Сбрасывать спринт", false);

    private final SliderSetting delayBeforeUse = new SliderSetting(this, "Задержка до использования (мс)", 150, 0, 1000, 10).setVisible(() -> !mode.getValue().equals("Обычный"));
    private final SliderSetting delayAfterUse = new SliderSetting(this, "Задержка после использования (мс)", 150, 0, 1000, 10).setVisible(() -> !mode.getValue().equals("Обычный"));
    private final SliderSetting stopMovementBefore = new SliderSetting(this, "Остановка движения до использования (мс)", 200, 0, 1000, 10).setVisible(() -> stopMovement.getValue() && !mode.getValue().equals("Обычный"));
    private final SliderSetting stopMovementAfter = new SliderSetting(this, "Остановка движения после использования (мс)", 200, 0, 1000, 10).setVisible(() -> stopMovement.getValue() && !mode.getValue().equals("Обычный"));

    private final DelimiterSetting itemDelimiter = new DelimiterSetting(this, "Предметы для биндов");

    private final ItemSetting item1 = new ItemSetting(this, "Предмет 1", Items.SNOWBALL);
    private final BindSetting bind1 = new BindSetting(this, "Бинд для предмета 1");
    private final ItemSetting item2 = new ItemSetting(this, "Предмет 2", Items.ENDER_PEARL);
    private final BindSetting bind2 = new BindSetting(this, "Бинд для предмета 2");
    private final ItemSetting item3 = new ItemSetting(this, "Предмет 3", Items.EXPERIENCE_BOTTLE);
    private final BindSetting bind3 = new BindSetting(this, "Бинд для предмета 3");
    private final ItemSetting item4 = new ItemSetting(this, "Предмет 4", Items.AIR);
    private final BindSetting bind4 = new BindSetting(this, "Бинд для предмета 4");
    private final ItemSetting item5 = new ItemSetting(this, "Предмет 5", Items.AIR);
    private final BindSetting bind5 = new BindSetting(this, "Бинд для предмета 5");
    private final ItemSetting item6 = new ItemSetting(this, "Предмет 6", Items.AIR);
    private final BindSetting bind6 = new BindSetting(this, "Бинд для предмета 6");
    private final ItemSetting item7 = new ItemSetting(this, "Предмет 7", Items.AIR);
    private final BindSetting bind7 = new BindSetting(this, "Бинд для предмета 7");
    private final ItemSetting item8 = new ItemSetting(this, "Предмет 8", Items.AIR);
    private final BindSetting bind8 = new BindSetting(this, "Бинд для предмета 8");
    private final ItemSetting item9 = new ItemSetting(this, "Предмет 9", Items.AIR);
    private final BindSetting bind9 = new BindSetting(this, "Бинд для предмета 9");
    private final ItemSetting item10 = new ItemSetting(this, "Предмет 10", Items.AIR);
    private final BindSetting bind10 = new BindSetting(this, "Бинд для предмета 10");

    private final Map<BindSetting, ItemSetting> itemBinds = new HashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private static final long USE_DELAY = 50;
    private boolean needRestoreMovement = false;
    private boolean wasForwardPressed = false;
    private final StopWatch moveTimer = new StopWatch();
    private final StopWatch cooldownTimer = new StopWatch();

    private boolean isProcessing = false;
    private boolean inventoryOpened = false;
    private boolean shiftPressed = false;
    private long processEndTime = 0;
    private boolean useQueued = false;
    private long useTime = 0;
    private Item queuedItem = null;
    private int originalHotbarSlot = -1;
    private int tempHotbarSlot = -1;
    private int originalInvSlot = -1;
    private boolean wasSprintingBeforeBlock = false;

    public MultiServerHelper() {
        itemBinds.put(bind1, item1);
        itemBinds.put(bind2, item2);
        itemBinds.put(bind3, item3);
        itemBinds.put(bind4, item4);
        itemBinds.put(bind5, item5);
        itemBinds.put(bind6, item6);
        itemBinds.put(bind7, item7);
        itemBinds.put(bind8, item8);
        itemBinds.put(bind9, item9);
        itemBinds.put(bind10, item10);
    }

    @EventHandler
    public void onUpdate(UpdateEvent e) {
        if (isProcessing) {
            if (stopMovement.getValue()) {
                stopMovementKeys();
            }
            long now = System.currentTimeMillis();
            if (useQueued && now >= useTime) {
                executeItemUse(queuedItem);
                useQueued = false;
                queuedItem = null;
            }
            if (now >= processEndTime) {
                isProcessing = false;
                restoreMovement();
            }
        }

        if (needRestoreMovement && moveTimer.finished(450)) {
            restoreOldMovement();
            needRestoreMovement = false;
        }
    }

    @EventHandler
    private void onKeyboardPress(KeyboardPressEvent event) {
        if (event.getScreen() != null && !inventoryOpened) return;

        for (Map.Entry<BindSetting, ItemSetting> entry : itemBinds.entrySet()) {
            BindSetting bind = entry.getKey();
            ItemSetting itemSetting = entry.getValue();

            if (itemSetting.getValue() != Items.AIR && event.isKey(bind.getValue())) {
                performActionWithMode(itemSetting.getValue());
                return;
            }
        }
    }

    @EventHandler
    private void onMousePress(MousePressEvent event) {
        if (event.getScreen() != null && !inventoryOpened) return;

        for (Map.Entry<BindSetting, ItemSetting> entry : itemBinds.entrySet()) {
            BindSetting bind = entry.getKey();
            ItemSetting itemSetting = entry.getValue();
            int bindValue = bind.getValue();

            if (itemSetting.getValue() != Items.AIR && bindValue >= 500 && event.isKey(bindValue - 500)) {
                performActionWithMode(itemSetting.getValue());
                return;
            }
        }
    }

    private void performActionWithMode(Item item) {
        String currentMode = mode.getValue();

        if (isProcessing || !cooldownTimer.finished(USE_DELAY)) return;

        if ("Обычный".equals(currentMode)) {
            useItem(item);
            return;
        }

        if ("HolyWorld".equals(currentMode)) {
            handleTimedMode(item);
            return;
        }

        if ("Легитный".equals(currentMode)) {
            handleLegitMode(item);
        }
    }

    private void handleTimedMode(Item item) {
        if (resetSprint.getValue()) {
            wasSprintingBeforeBlock = mc.player.isSprinting();
            mc.gameSettings.keyBindBack.setPressed(true);
            mc.player.setSprinting(false);
        }

        if (stopForwardMovement.getValue()) {
            wasForwardPressed = mc.gameSettings.keyBindForward.isKeyDown();
            mc.gameSettings.keyBindForward.setPressed(false);
        }

        if (stopMovement.getValue()) {
            saveMovementState();
        }

        isProcessing = true;
        long now = System.currentTimeMillis();
        useTime = now + delayBeforeUse.getValue().intValue();
        processEndTime = now + stopMovementBefore.getValue().intValue()
                + delayBeforeUse.getValue().intValue()
                + delayAfterUse.getValue().intValue()
                + stopMovementAfter.getValue().intValue();
        useQueued = true;
        queuedItem = item;
        cooldownTimer.reset();
    }

    private void handleLegitMode(Item item) {
        if (resetSprint.getValue()) {
            wasSprintingBeforeBlock = mc.player.isSprinting();
            mc.gameSettings.keyBindBack.setPressed(true);
            mc.player.setSprinting(false);
        }

        if (stopForwardMovement.getValue()) {
            wasForwardPressed = mc.gameSettings.keyBindForward.isKeyDown();
            mc.gameSettings.keyBindForward.setPressed(false);
        }

        if (stopMovement.getValue()) {
            saveMovementState();
        }

        if (pressShift.getValue()) {
            mc.gameSettings.keyBindSneak.setPressed(true);
            shiftPressed = true;
        }

        if (openInventory.getValue() && mc.currentScreen == null) {
            mc.displayScreen(new InventoryScreen(mc.player));
            inventoryOpened = true;
        }

        isProcessing = true;
        long now = System.currentTimeMillis();
        int randomDelay = (int) (Math.random() * 100);
        useTime = now + delayBeforeUse.getValue().intValue() + randomDelay;
        processEndTime = now + stopMovementBefore.getValue().intValue()
                + delayBeforeUse.getValue().intValue() + randomDelay
                + delayAfterUse.getValue().intValue()
                + stopMovementAfter.getValue().intValue();
        useQueued = true;
        queuedItem = item;
        cooldownTimer.reset();
    }

    private void saveMovementState() {
        wasForwardPressed = mc.gameSettings.keyBindForward.isPressed();
    }

    private void restoreMovement() {
        if (resetSprint.getValue()) {
            mc.gameSettings.keyBindBack.setPressed(false);
            if (wasSprintingBeforeBlock) {
                mc.player.setSprinting(true);
                wasSprintingBeforeBlock = false;
            }
        }

        if (stopForwardMovement.getValue() && wasForwardPressed) {
            mc.gameSettings.keyBindForward.setPressed(true);
            wasForwardPressed = false;
        }

        if (shiftPressed) {
            mc.gameSettings.keyBindSneak.setPressed(false);
            shiftPressed = false;
        }
        if (inventoryOpened && mc.currentScreen instanceof InventoryScreen) {
            mc.displayScreen(null);
            inventoryOpened = false;
        }
        if (stopMovement.getValue()) {
            mc.gameSettings.keyBindForward.setPressed(true);
        }
        if (originalHotbarSlot != -1 && tempHotbarSlot != -1 && originalInvSlot != -1) {
            restoreItemToSlot();
        }
    }

    private void executeItemUse(Item item) {
        int hbSlot = findItemSlot(item, 0, 9);
        int invSlot = -1;
        if (hbSlot == -1 && !onlyHotbar.getValue()) {
            invSlot = findItemSlot(item, 9, 36);
        }

        if (hbSlot != -1 || invSlot != -1) {
            executeItemAction(hbSlot, invSlot);
        }
    }

    private boolean isShulkerBox(Item item) {
        String name = item.getTranslationKey();
        return name.contains("shulker_box");
    }

    private void stopMovementKeys() {
        mc.gameSettings.keyBindForward.setPressed(false);

    }

    private void restoreOldMovement() {
    }

    private int findItemSlot(Item item, int start, int end) {
        if (isShulkerBox(item)) {
            for (int i = start; i < end; i++) {
                if (isShulkerBox(mc.player.inventory.getStackInSlot(i).getItem())) {
                    return i;
                }
            }
        } else {
            for (int i = start; i < end; i++) {
                if (mc.player.inventory.getStackInSlot(i).getItem() == item) {
                    return i;
                }
            }
        }
        return -1;
    }

    private void useItem(Item item) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - cooldownTimer.elapsedTime() < USE_DELAY) return;

        wasForwardPressed = mc.gameSettings.keyBindForward.isKeyDown();

        stopMovement();
        moveTimer.reset();
        needRestoreMovement = true;

        int hbSlot = findItemSlot(item, 0, 9);
        int invSlot = -1;
        if (hbSlot == -1) invSlot = findItemSlot(item, 9, 36);

        executeItemAction(hbSlot, invSlot);
        cooldownTimer.reset();
    }

    private void executeItemAction(int hbSlot, int invSlot) {
        if (hbSlot != -1) {
            int previousSlot = mc.player.inventory.currentItem;
            mc.player.inventory.currentItem = hbSlot;
            mc.player.connection.sendPacket(new CHeldItemChangePacket(hbSlot));
            mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
            mc.player.swingArm(Hand.MAIN_HAND);
            mc.player.inventory.currentItem = previousSlot;
            mc.player.connection.sendPacket(new CHeldItemChangePacket(previousSlot));
        } else if (invSlot != -1) {
            originalHotbarSlot = mc.player.inventory.currentItem;
            tempHotbarSlot = findEmptyHotbarSlot();
            if (tempHotbarSlot != -1) {
                originalInvSlot = invSlot;
                moveItemToHotbar(invSlot, tempHotbarSlot);
                mc.player.inventory.currentItem = tempHotbarSlot;
                mc.player.connection.sendPacket(new CHeldItemChangePacket(tempHotbarSlot));
                mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
                mc.player.swingArm(Hand.MAIN_HAND);
                mc.player.inventory.currentItem = originalHotbarSlot;
                mc.player.connection.sendPacket(new CHeldItemChangePacket(originalHotbarSlot));
            }
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

    private void moveItemToHotbar(int invSlot, int hotbarSlot) {
        int windowId = mc.player.openContainer.windowId;
        mc.playerController.windowClick(windowId, invSlot, 0, ClickType.PICKUP, mc.player);
        mc.playerController.windowClick(windowId, hotbarSlot + 36, 0, ClickType.PICKUP, mc.player);
    }

    private void restoreItemToSlot() {
        int windowId = mc.player.openContainer.windowId;
        mc.playerController.windowClick(windowId, tempHotbarSlot + 36, 0, ClickType.PICKUP, mc.player);
        mc.playerController.windowClick(windowId, originalInvSlot, 0, ClickType.PICKUP, mc.player);
        tempHotbarSlot = -1;
        originalInvSlot = -1;
    }

    private void stopMovement() {
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.keyCode, false);
        if (mc.player.getMotion().y > 0)
            mc.player.setMotion(0, mc.player.getMotion().y, 0);
    }
}