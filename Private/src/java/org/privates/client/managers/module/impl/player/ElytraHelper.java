package org.privates.client.managers.module.impl.player;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TextFormatting;
import org.privates.client.api.annotations.Category1;
import org.privates.client.api.annotations.Customize;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.input.KeyboardPressEvent;
import org.privates.client.managers.events.input.MousePressEvent;
import org.privates.client.managers.events.player.MovementManager;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BindSetting;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.chat.ChatUtil;
import org.privates.client.utils.player.InvUtil;
import org.privates.client.utils.player.MoveUtil;
import org.privates.common.impl.taskript.Script;

import java.util.HashMap;
import java.util.Map;

@Category1
@Customize
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "ElytraHelper", category = Category.PLAYER, description = "ElytraSwap то есть помогает вам с быстрым надеванием элитры и использованием феерверков")
public class ElytraHelper extends Module {
    private final Map<String, BindSetting> keyBindings = new HashMap<>();
    private final ModeSetting mode = new ModeSetting(this, "Режим", "Обычный", "HolyWorld", "Легитный").set("Обычный");
    private final ModeSetting fireworkMode = new ModeSetting(this, "Режим фейерверка", "Пакетное", "Обычное", "Новое", "Новое пакетное", "Улучшеное").set("Пакетное");
    private final BooleanSetting onlyHotbar = new BooleanSetting(this, "Только хотбар", false).setVisible(() -> mode.getValue().equals("Легитный"));
    private final BooleanSetting openInventory = new BooleanSetting(this, "Открывать инвентарь", false).setVisible(() -> mode.getValue().equals("Легитный"));
    private final BooleanSetting stopMovement = new BooleanSetting(this, "Останавливать движение", true).setVisible(() -> !mode.getValue().equals("Обычный"));
    private final BooleanSetting stopInFlight = new BooleanSetting(this, "Не останавливать в полете", true).setVisible(() -> stopMovement.getValue() && !mode.getValue().equals("Обычный"));
    private final BooleanSetting pressShift = new BooleanSetting(this, "Нажимать Shift", false).setVisible(() -> mode.getValue().equals("Легитный"));
    private final BooleanSetting stopForwardMovement = new BooleanSetting(this, "Останавливать движение вперед", false);
    private final BooleanSetting resetSprint = new BooleanSetting(this, "Сбрасывать спринт", false);
    private final BooleanSetting fastFly = new BooleanSetting(this, "Быстрый взлёт", false);

    private final SliderSetting delayBeforeAction = new SliderSetting(this, "Задержка до действия (мс)", 150, 0, 1000, 10).setVisible(() -> !mode.getValue().equals("Обычный"));
    private final SliderSetting delayAfterAction = new SliderSetting(this, "Задержка после действия (мс)", 150, 0, 1000, 10).setVisible(() -> !mode.getValue().equals("Обычный"));
    private final SliderSetting stopMovementBefore = new SliderSetting(this, "Остановка движения до действия (мс)", 200, 0, 1000, 10).setVisible(() -> stopMovement.getValue() && !mode.getValue().equals("Обычный"));
    private final SliderSetting stopMovementAfter = new SliderSetting(this, "Остановка движения после действия (мс)", 200, 0, 1000, 10).setVisible(() -> stopMovement.getValue() && !mode.getValue().equals("Обычный"));

    private final BooleanSetting autoFirework = new BooleanSetting(this, "Авто фейерверк", false);
    private final ModeSetting autoFireworkMode = new ModeSetting(this, "Режим авто", "Обычный", "Умный").setVisible(autoFirework::getValue);
    private final SliderSetting autoFireworkDelay = new SliderSetting(this, "Задержка авто (мс)", 1000, 100, 2500, 50).setVisible(() -> autoFirework.getValue() && autoFireworkMode.is("Обычный"));
    private final SliderSetting smartBpsThreshold = new SliderSetting(this, "BPS порог", 25, 5, 50, 1).setVisible(() -> autoFirework.getValue() && autoFireworkMode.is("Умный"));

    private final Script script = new Script();
    private boolean isProcessing = false;
    private boolean inventoryOpened = false;
    private boolean shiftPressed = false;
    private long processEndTime = 0;
    private boolean actionQueued = false;
    private long actionTime = 0;
    private String queuedAction = null;
    private boolean wasForwardPressed = false;
    private boolean wasSprintingBeforeBlock = false;

    private long lastAutoFireworkTime = 0;

    public ElytraHelper() {
        keyBindings.put("chest-swap", new BindSetting(this, "Свапнуть нагрудник"));
        keyBindings.put("firework", new BindSetting(this, "Использовать феерверк"));
    }

    @EventHandler
    public void onKeyboardPress(KeyboardPressEvent event) {
        if (event.getScreen() != null && !inventoryOpened) return;

        keyBindings.forEach((key, bindSetting) -> {
            if (event.isKey(bindSetting.getValue())) {
                performActionWithMode(key);
            }
        });
    }

    @EventHandler
    public void onMousePress(MousePressEvent event) {
        if (event.getScreen() != null && !inventoryOpened) return;

        keyBindings.forEach((key, bindSetting) -> {
            int bindValue = bindSetting.getValue();
            if (bindValue >= 500 && event.isKey(bindValue - 500)) {
                performActionWithMode(key);
            }
        });
    }

    @EventHandler
    public void onUpdate(UpdateEvent e) {
        script.update();

        if (isProcessing) {
            if (shouldStopMovement()) {
                MovementManager.getInstance().lockMovement("ElytraHelper");
            }
            long now = System.currentTimeMillis();
            if (actionQueued && now >= actionTime) {
                performAction(queuedAction);
                actionQueued = false;
                queuedAction = null;
            }
            if (now >= processEndTime) {
                isProcessing = false;
                restoreMovement();
            }
        }

        if (autoFirework.getValue() && mc.player.isElytraFlying()) {
            handleAutoFirework();
        }
    }

    private void handleAutoFirework() {
        long now = System.currentTimeMillis();

        if (autoFireworkMode.is("Обычный")) {
            if (now - lastAutoFireworkTime >= autoFireworkDelay.getValue().longValue()) {
                useFirework();
                lastAutoFireworkTime = now;
            }
        } else if (autoFireworkMode.is("Умный")) {
            float currentBps = (float) (MoveUtil.speed() * 20);
            if (currentBps < smartBpsThreshold.getValue()) {
                if (now - lastAutoFireworkTime >= 100) {
                    useFirework();
                    lastAutoFireworkTime = now;
                }
            }
        }
    }

    private void useFirework() {
        if (mode.getValue().equals("Легитный") && onlyHotbar.getValue()) {
            performHotbarFirework();
        } else {
            String fwMode = fireworkMode.getValue();
            if (fwMode.equals("Пакетное")) {
                InvUtil.findItemAndThrow(Items.FIREWORK_ROCKET, mc.player.rotationYaw, mc.player.rotationPitch, Hand.MAIN_HAND, true);
            } else if (fwMode.equals("Обычное")) {
                InvUtil.findItemAndThrow(Items.FIREWORK_ROCKET, mc.player.rotationYaw, mc.player.rotationPitch, Hand.MAIN_HAND, false);
            } else if (fwMode.equals("Новое")) {
                InvUtil.findItemAndThrow(Items.FIREWORK_ROCKET, mc.player.rotationYaw, mc.player.rotationPitch, Hand.OFF_HAND, false);
            } else if (fwMode.equals("Новое пакетное")) {
                InvUtil.findItemAndThrow(Items.FIREWORK_ROCKET, mc.player.rotationYaw, mc.player.rotationPitch, Hand.OFF_HAND, true);
            } else if (fwMode.equals("Улучшеное")) {
                useItemImproved(Items.FIREWORK_ROCKET);
            }
        }
    }

    private boolean shouldStopMovement() {
        if (!stopMovement.getValue()) return false;
        return !stopInFlight.getValue() || !mc.player.isElytraFlying();
    }

    private void performActionWithMode(String action) {
        String currentMode = mode.getValue();

        if (currentMode.equals("Обычный")) {
            performAction(action);
            return;
        }

        if (currentMode.equals("HolyWorld")) {
            handleTimedMode(action);
            return;
        }

        if (currentMode.equals("Легитный")) {
            handleLegitMode(action);
        }
    }

    private void handleTimedMode(String action) {
        if (resetSprint.getValue()) {
            wasSprintingBeforeBlock = mc.player.isSprinting();
            mc.gameSettings.keyBindBack.setPressed(true);
            mc.player.setSprinting(false);
        }

        if (stopForwardMovement.getValue()) {
            wasForwardPressed = mc.gameSettings.keyBindForward.isKeyDown();
            mc.gameSettings.keyBindForward.setPressed(false);
        }

        isProcessing = true;
        long now = System.currentTimeMillis();
        actionTime = now + delayBeforeAction.getValue().intValue();
        processEndTime = now + stopMovementBefore.getValue().intValue() + delayBeforeAction.getValue().intValue() + delayAfterAction.getValue().intValue() + stopMovementAfter.getValue().intValue();
        actionQueued = true;
        queuedAction = action;
    }

    private void handleLegitMode(String action) {
        if (resetSprint.getValue()) {
            wasSprintingBeforeBlock = mc.player.isSprinting();
            mc.gameSettings.keyBindBack.setPressed(true);
            mc.player.setSprinting(false);
        }

        if (stopForwardMovement.getValue()) {
            wasForwardPressed = mc.gameSettings.keyBindForward.isKeyDown();
            mc.gameSettings.keyBindForward.setPressed(false);
        }

        if (pressShift.getValue()) {
            mc.gameSettings.keyBindSneak.setPressed(true);
            shiftPressed = true;
        }

        if (openInventory.getValue() && mc.currentScreen == null && action.equals("chest-swap")) {
            mc.displayScreen(new InventoryScreen(mc.player));
            inventoryOpened = true;
        }

        isProcessing = true;
        long now = System.currentTimeMillis();
        int randomDelay = (int) (Math.random() * 100);
        actionTime = now + delayBeforeAction.getValue().intValue() + randomDelay;
        processEndTime = now + stopMovementBefore.getValue().intValue() + delayBeforeAction.getValue().intValue() + randomDelay + delayAfterAction.getValue().intValue() + stopMovementAfter.getValue().intValue();
        actionQueued = true;
        queuedAction = action;
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
        if (shouldStopMovement()) {
            MovementManager.getInstance().unlockMovement("ElytraHelper");
        }
    }

    private void performAction(String action) {
        switch (action) {
            case "chest-swap":
                SwapChest();
                break;
            case "firework":
                if (mc.player.isElytraFlying()) {
                    useFirework();
                }
                break;
        }
    }

    private void useItemImproved(Item item) {
        int hbSlot = findItemSlot(item, 0, 9);
        int invSlot = -1;
        if (hbSlot == -1) invSlot = findItemSlot(item, 9, 36);

        executeItemAction(hbSlot, invSlot);
    }

    private int findItemSlot(Item item, int start, int end) {
        for (int i = start; i < end; i++) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == item) {
                return i;
            }
        }
        return -1;
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
            mc.playerController.pickItem(invSlot);
            mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
            mc.player.swingArm(Hand.MAIN_HAND);
            mc.playerController.pickItem(invSlot);
        }
    }

    private void performHotbarFirework() {
        for (int i = 0; i < 9; i++) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == Items.FIREWORK_ROCKET) {
                int previousSlot = mc.player.inventory.currentItem;
                mc.player.inventory.currentItem = i;
                InvUtil.useItemNormal(Hand.MAIN_HAND, mc.player.rotationYaw, mc.player.rotationPitch);
                mc.player.inventory.currentItem = previousSlot;
                return;
            }
        }
    }

    private void SwapChest() {
        boolean elytraOnChest = mc.player.getItemStackFromSlot(EquipmentSlotType.CHEST).getItem().equals(Items.ELYTRA);
        Slot chestPlate = elytraOnChest ? findChestPlate() : InvUtil.getSlot(Items.ELYTRA);

        if (chestPlate != null) {
            InvUtil.moveItem(chestPlate.slotNumber, 6);
            ChatUtil.addText("Свапаю на " + (elytraOnChest ? (TextFormatting.AQUA + "Нагрудник") : (TextFormatting.RED + "Элитры")));

            if (!mc.player.isElytraFlying() && mc.player.inventory.getStackInSlot(38).getItem() != Items.ELYTRA && fastFly.getValue()) {
                Slot firework = InvUtil.getSlot(Items.FIREWORK_ROCKET);
                if (firework != null) {
                    script.cleanup().addTickStep(10, () -> {
                        if (mc.player.isOnGround()) mc.player.jump();
                        mc.player.startFallFlying();
                        mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.START_FALL_FLYING));
                        InvUtil.findItemAndThrow(Items.FIREWORK_ROCKET, mc.player.rotationYaw, mc.player.rotationPitch, Hand.MAIN_HAND, true);
                    }, 10);
                }
            }
        }
    }

    private Slot findChestPlate() {
        Item[] items = {Items.NETHERITE_CHESTPLATE, Items.DIAMOND_CHESTPLATE, Items.CHAINMAIL_CHESTPLATE, Items.IRON_CHESTPLATE, Items.GOLDEN_CHESTPLATE, Items.LEATHER_CHESTPLATE};
        for (Item item : items) {
            Slot slot = InvUtil.getSlot(item);
            if (slot != null) {
                return slot;
            }
        }
        return null;
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        MovementManager.getInstance().unlockMovement("ElytraHelper");
        isProcessing = false;
        lastAutoFireworkTime = 0;
        wasForwardPressed = false;
        wasSprintingBeforeBlock = false;
        
        if (resetSprint.getValue()) {
            mc.gameSettings.keyBindBack.setPressed(false);
        }

        if (stopForwardMovement.getValue() && wasForwardPressed) {
            mc.gameSettings.keyBindForward.setPressed(true);
        }
    }
}