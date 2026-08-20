package org.privates.client.managers.module.impl.player;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CPickItemPacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TextFormatting;
import org.privates.client.Excellent;
import org.privates.client.api.annotations.Category2;
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
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.player.InvUtil;
import org.privates.client.utils.player.PlayerUtil;
import org.privates.common.impl.taskript.Script;
import org.privates.lib.util.time.StopWatch;

import java.util.HashMap;
import java.util.Map;

@Category2
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "ClickAction", category = Category.PLAYER, description = "Помогает вам с использванием ванильных предметов и добавлением в друзья")
public class ClickAction extends Module {
    public static ClickAction getInstance() {
        return Instance.get(ClickAction.class);
    }

    private final Map<Item, BindSetting> keyBindings = new HashMap<>();
    private final ModeSetting mode = new ModeSetting(this, "Режим", "Обычный", "HolyWorld", "Легитный", "Улучшеный").set("Обычный");
    private final ModeSetting useMode = new ModeSetting(this, "Режим использования", "Пакетное", "Обычное", "Новое", "Новое пакетное", "Улучшеное").set("Пакетное");
    private final BooleanSetting onlyHotbar = new BooleanSetting(this, "Только хотбар", false).setVisible(() -> mode.getValue().equals("Легитный"));
    private final BooleanSetting openInventory = new BooleanSetting(this, "Открывать инвентарь", false).setVisible(() -> mode.getValue().equals("Легитный"));
    private final BooleanSetting stopMovement = new BooleanSetting(this, "Останавливать движение", true).setVisible(() -> !mode.getValue().equals("Обычный") && !mode.getValue().equals("Улучшеный"));
    private final BooleanSetting pressShift = new BooleanSetting(this, "Нажимать Shift", false).setVisible(() -> mode.getValue().equals("Легитный"));
    private final BooleanSetting stopForwardMovement = new BooleanSetting(this, "Останавливать движение вперед", false);
    private final BooleanSetting resetSprint = new BooleanSetting(this, "Сбрасывать спринт", false);

    private final SliderSetting delayBeforeUse = new SliderSetting(this, "Задержка до использования (мс)", 150, 0, 1000, 10).setVisible(() -> !mode.getValue().equals("Обычный") && !mode.getValue().equals("Улучшеный"));
    private final SliderSetting delayAfterUse = new SliderSetting(this, "Задержка после использования (мс)", 150, 0, 1000, 10).setVisible(() -> !mode.getValue().equals("Обычный") && !mode.getValue().equals("Улучшеный"));
    private final SliderSetting stopMovementBefore = new SliderSetting(this, "Остановка движения до использования (мс)", 200, 0, 1000, 10).setVisible(() -> stopMovement.getValue() && !mode.getValue().equals("Обычный") && !mode.getValue().equals("Улучшеный"));
    private final SliderSetting stopMovementAfter = new SliderSetting(this, "Остановка движения после использования (мс)", 200, 0, 1000, 10).setVisible(() -> stopMovement.getValue() && !mode.getValue().equals("Обычный") && !mode.getValue().equals("Улучшеный"));

    private final StopWatch time = new StopWatch();
    private final Script script = new Script();
    private boolean isProcessing = false;
    private boolean inventoryOpened = false;
    private boolean shiftPressed = false;
    private long processEndTime = 0;
    private boolean useQueued = false;
    private long useTime = 0;
    private Item queuedItem = null;

    private boolean wasForwardPressed = false;
    private boolean needRestoreMovement = false;
    private boolean wasSprintingBeforeBlock = false;

    private boolean wasForwardPressedImproved = false;
    private boolean movementStateSaved = false;
    private boolean isBlockingW = false;
    private long improvedUseStartTime = 0;
    private Item improvedItem = null;
    private Slot improvedItemSlot = null;
    private net.minecraft.item.ItemStack originalOffhandItem = null;
    private boolean improvedSwapped = false;
    private boolean improvedUsed = false;
    private boolean needPressWOnce = false;
    private final long wPressTime = 0;

    public ClickAction() {
        keyBindings.put(Items.ENDER_PEARL, new BindSetting(this, "Кнопка пёрки"));
        keyBindings.put(Items.EXPERIENCE_BOTTLE, new BindSetting(this, "Кнопка опыта"));
        keyBindings.put(Items.CROSSBOW, new BindSetting(this, "Кнопка арбалета"));
        keyBindings.put(Items.AIR, new BindSetting(this, "Добавить в друзья"));
    }

    @EventHandler
    public void onUpdate(UpdateEvent e) {
        if (mc.currentScreen != null && !inventoryOpened) return;

        if (needPressWOnce) {
            long now = System.currentTimeMillis();
            if (now - wPressTime < 50) {
                mc.gameSettings.keyBindForward.setPressed(true);
            } else {
                mc.gameSettings.keyBindForward.setPressed(false);
                needPressWOnce = false;
            }
        }

        if (isBlockingW) {
            mc.gameSettings.keyBindForward.setPressed(false);
        }

        if (isProcessing) {
            if (stopMovement.getValue()) {
                mc.gameSettings.keyBindForward.setPressed(false);
                MovementManager.getInstance().lockMovement("ClickAction");
            }
            long now = System.currentTimeMillis();
            if (useQueued && now >= useTime) {
                performAction(queuedItem);
                useQueued = false;
                queuedItem = null;
            }
            if (now >= processEndTime) {
                isProcessing = false;
                restoreMovement();
            }
        }

        if (improvedItem != null && improvedItemSlot != null) {
            long now = System.currentTimeMillis();
            long elapsed = now - improvedUseStartTime;

            if (!improvedSwapped) {
                originalOffhandItem = mc.player.getHeldItemOffhand().copy();
                InvUtil.swapHand(improvedItemSlot, Hand.OFF_HAND, false);
                improvedSwapped = true;
            } else if (elapsed >= 50 && !improvedUsed) {
                mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.OFF_HAND));
                mc.player.swingArm(Hand.OFF_HAND);
                improvedUsed = true;
            } else if (elapsed >= 100 && improvedUsed) {
                InvUtil.swapHand(improvedItemSlot, Hand.OFF_HAND, false);
                isBlockingW = false;
                movementStateSaved = false;
                improvedItem = null;
                improvedItemSlot = null;
                originalOffhandItem = null;
                improvedSwapped = false;
                improvedUsed = false;
            }
        }

        if (needRestoreMovement) {
            restoreOldMovement();
            needRestoreMovement = false;
        }

        keyBindings.forEach((item, bindSetting) -> {
            int bindValue = bindSetting.getValue();
            boolean isMouseBind = bindValue >= 500;
            if (item == Items.EXPERIENCE_BOTTLE) {
                if (!isMouseBind && InputMappings.isKeyDown(mw.getHandle(), bindValue)) {
                    if (PlayerUtil.isFuntime()) throwEXP(item);
                    else performActionWithMode(item);
                }
            }
        });
    }

    @EventHandler
    public void onKeyPress(KeyboardPressEvent event) {
        if (event.getScreen() != null && !inventoryOpened) return;

        keyBindings.forEach((item, bindSetting) -> {
            if (event.isKey(bindSetting.getValue())) {
                performActionWithMode(item);
            }
        });
    }

    @EventHandler
    public void onMousePress(MousePressEvent event) {
        if (event.getScreen() != null && !inventoryOpened) return;

        keyBindings.forEach((item, bindSetting) -> {
            int bindValue = bindSetting.getValue();
            if (bindValue >= 500 && event.isKey(bindValue - 500)) {
                if (event.getKey() == 0) {
                    return;
                }
                performActionWithMode(item);
            }
        });
    }

    private void performActionWithMode(Item item) {
        String currentMode = mode.getValue();

        if (currentMode.equals("Обычный")) {
            performAction(item);
            return;
        }

        if (currentMode.equals("HolyWorld")) {
            handleTimedMode(item);
            return;
        }

        if (currentMode.equals("Легитный")) {
            handleLegitMode(item);
            return;
        }

        if (currentMode.equals("Улучшеный")) {
            handleImprovedMode(item);
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
        processEndTime = now + stopMovementBefore.getValue().intValue() + delayBeforeUse.getValue().intValue() + delayAfterUse.getValue().intValue() + stopMovementAfter.getValue().intValue();
        useQueued = true;
        queuedItem = item;
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
        processEndTime = now + stopMovementBefore.getValue().intValue() + delayBeforeUse.getValue().intValue() + randomDelay + delayAfterUse.getValue().intValue() + stopMovementAfter.getValue().intValue();
        useQueued = true;
        queuedItem = item;
    }


    private void handleImprovedMode(Item item) {
        if (!movementStateSaved) {
            wasForwardPressedImproved = mc.gameSettings.keyBindForward.isPressed();
            movementStateSaved = true;
        }

        isBlockingW = true;
        useItemImproved(item);
    }

    private void saveMovementState() {
        wasForwardPressed = mc.gameSettings.keyBindForward.isPressed();
    }

    private void useItemImproved(Item item) {
        Slot itemSlot = InvUtil.getInventorySlot(item);
        if (itemSlot == null) {
            isBlockingW = false;
            movementStateSaved = false;
            return;
        }

        improvedItem = item;
        improvedItemSlot = itemSlot;
        improvedUseStartTime = System.currentTimeMillis();
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
            MovementManager.getInstance().unlockMovement("ClickAction");
        }

        movementStateSaved = false;
    }

    private void restoreOldMovement() {
    }

    public void performAction(Item item) {
        if (item == Items.AIR) {
            clickFriend();
            return;
        }

        if (mode.getValue().equals("Легитный") && onlyHotbar.getValue()) {
            performHotbarAction(item);
        } else {
            String useModeValue = useMode.getValue();
            if (useModeValue.equals("Пакетное")) {
                InvUtil.findItemAndThrow(item, mc.player.rotationYaw, mc.player.rotationPitch, Hand.MAIN_HAND, true);
            } else if (useModeValue.equals("Обычное")) {
                InvUtil.findItemAndThrow(item, mc.player.rotationYaw, mc.player.rotationPitch, Hand.MAIN_HAND, false);
            } else if (useModeValue.equals("Новое")) {
                InvUtil.findItemAndThrow(item, mc.player.rotationYaw, mc.player.rotationPitch, Hand.OFF_HAND, false);
            } else if (useModeValue.equals("Новое пакетное")) {
                InvUtil.findItemAndThrow(item, mc.player.rotationYaw, mc.player.rotationPitch, Hand.OFF_HAND, true);
            } else if (useModeValue.equals("Улучшеное")) {
                useItemImproved(item);
            }
        }
    }

    private void performHotbarAction(Item item) {
        for (int i = 0; i < 9; i++) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == item) {
                int previousSlot = mc.player.inventory.currentItem;
                mc.player.inventory.currentItem = i;
                mc.player.connection.sendPacket(new CHeldItemChangePacket(i));
                InvUtil.useItem(Hand.MAIN_HAND, mc.player.rotationYaw, mc.player.rotationPitch);
                mc.player.inventory.currentItem = previousSlot;
                mc.player.connection.sendPacket(new CHeldItemChangePacket(previousSlot));
                return;
            }
        }
    }

    public void clickFriend() {
        if (mc.pointedEntity instanceof PlayerEntity player) {
            final String name = TextFormatting.removeFormatting(player.getGameProfile().getName());
            final boolean friend = Excellent.inst().friendManager().isFriend(name);
            if (friend) {
                Excellent.inst().friendManager().removeFriend(name);
                ChatUtil.addText(TextFormatting.RED + "\"" + name + "\" удалён из списка друзей.");
            } else {
                Excellent.inst().friendManager().addFriend(name);
                ChatUtil.addText(TextFormatting.GREEN + "\"" + name + "\" добавлен в список друзей.");
            }
        }
    }

    public void throwEXP(Item item) {
        Slot slot = InvUtil.getInventorySlot(item);
        if (slot == null) {
            if (time.finished(2000)) {
                ChatUtil.addTextWithError("Нету опыта");
                time.reset();
            }
            return;
        }

        if (slot.slotNumber >= 36) {
            mc.player.connection.sendPacket(new CHeldItemChangePacket(slot.slotNumber));
            InvUtil.useItem(Hand.MAIN_HAND, mc.player.rotationYaw, 80);
            mc.player.connection.sendPacket(new CHeldItemChangePacket(mc.player.inventory.currentItem));
        } else if (InvUtil.noEmptyHotBarSlots() == 9) {
            mc.player.connection.sendPacket(new CPickItemPacket(slot.slotNumber));
            InvUtil.useItem(Hand.MAIN_HAND, mc.player.rotationYaw, 80);
            mc.player.connection.sendPacket(new CPickItemPacket(slot.slotNumber));
        }
    }

    @Override
    public void onDisable() {
        MovementManager.getInstance().unlockMovement("ClickAction");
        isProcessing = false;
        isBlockingW = false;
        useQueued = false;
        improvedItem = null;
        improvedItemSlot = null;
        wasForwardPressed = false;
        wasSprintingBeforeBlock = false;
        
        if (resetSprint.getValue()) {
            mc.gameSettings.keyBindBack.setPressed(false);
        }

        if (stopForwardMovement.getValue() && wasForwardPressed) {
            mc.gameSettings.keyBindForward.setPressed(true);
        }
        
        super.onDisable();
    }
}