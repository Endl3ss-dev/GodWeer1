package antileak.base.client.modules.impl.player;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.PlayerInput;
import net.minecraft.item.consume.UseAction;
import antileak.base.api.events.EventLink;
import antileak.base.api.events.implement.EventBinding;
import antileak.base.api.events.implement.EventMoveInput;
import antileak.base.api.events.implement.EventUpdate;
import antileak.base.api.utils.notification.NotificationManager;
import antileak.base.api.utils.player.InventoryUtils;
import antileak.base.client.modules.Module;
import antileak.base.client.modules.settings.implement.BindSetting;
import antileak.base.client.modules.settings.implement.ModeSetting;

public class ClickPearl extends Module {

    public static ClickPearl INSTANCE = new ClickPearl();

    private final ModeSetting mode = new ModeSetting("Мод", "Обычный", "Обычный", "Легитный");
    private final BindSetting key = new BindSetting("Клавиша броска", -1);

    private boolean pearlUsed;
    private int ticksExisted;
    private int activeStage;
    private Hand activeHand;
    private int activeInventorySlot = -1;
    private int activeHotbarSlot = -1;
    private int activePreviousSlot = -1;
    private int slowingTicks;

    public ClickPearl() {
        super("ClickPearl", "Кидает перку по внутреннему бинду", ModuleCategory.MISC);
        addSettings(key, mode);
    }

    @Override
    public void onEnable() {
        pearlUsed = false;
        ticksExisted = 0;
        resetState();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        pearlUsed = false;
        ticksExisted = 0;
        resetState();
        super.onDisable();
    }

    @EventLink
    public void onKey(final EventBinding event) {
        if (event.getKey() != key.getKey()) return;
        if (mode.is("Обычный")) {
            int slot = InventoryUtils.find(Items.ENDER_PEARL, 0, 36);
            if (slot == -1) return;
            InventoryUtils.swapAndUseHvH(Items.ENDER_PEARL);
        } else {
            pearlUsed = true;
        }
    }

    @EventLink
    public void onInput(EventMoveInput e) {
        if (slowingTicks > 0) {
            slowingTicks--;
            if (mc.player != null) {
                mc.player.setSprinting(false);
            }
            e.setForward(0);
            e.setStrafe(0);
            e.setJump(false);
            e.setSneak(false);
        }
    }

    @EventLink
    public void onEvent(final EventUpdate event) {
        if (mc.player == null || mc.world == null) {
            resetState();
            return;
        }

        if (activeStage > 0) {
            processLegitUse();
            return;
        }

        if (pearlUsed) {
            startLegitPearl();
            pearlUsed = false;
        }

        if (ticksExisted > 0) ticksExisted--;
    }

    private void startLegitPearl() {
        if (mc.player.getItemCooldownManager().isCoolingDown(Items.ENDER_PEARL.getDefaultStack())) {
            NotificationManager.pushCustom("У жемчуга есть кд", getCategory().getIcons());
            return;
        }

        if (mc.player.getMainHandStack().isOf(Items.ENDER_PEARL)) {
            startLegitUse(Hand.MAIN_HAND, -1);
            return;
        }

        if (mc.player.getOffHandStack().isOf(Items.ENDER_PEARL)) {
            startLegitUse(Hand.OFF_HAND, -1);
            return;
        }

        int hotbarSlot = InventoryUtils.find(Items.ENDER_PEARL, 0, 9);
        if (hotbarSlot != -1) {
            startLegitUse(Hand.MAIN_HAND, hotbarSlot);
            return;
        }

        int inventorySlot = InventoryUtils.find(Items.ENDER_PEARL, 9, 36);
        if (inventorySlot != -1) {
            int tempSlot = findTemporaryHotbarSlot();
            startLegitUseFromInventory(inventorySlot, tempSlot);
            return;
        }

        NotificationManager.pushCustom("Жемчуг не найден!", getCategory().getIcons());
    }

    private void startLegitUse(Hand hand, int hotbarSlot) {
        activeHand = hand;
        activeInventorySlot = -1;
        activeHotbarSlot = hotbarSlot;
        activePreviousSlot = mc.player.getInventory().selectedSlot;
        activeStage = 1;
        beginLegitUsePause();
    }

    private void startLegitUseFromInventory(int inventorySlot, int tempSlot) {
        activeHand = Hand.MAIN_HAND;
        activeInventorySlot = inventorySlot;
        activeHotbarSlot = tempSlot;
        activePreviousSlot = mc.player.getInventory().selectedSlot;
        activeStage = 1;
        beginLegitUsePause();
    }

    private void processLegitUse() {
        if (mc.player == null || mc.interactionManager == null) {
            resetState();
            return;
        }

        if (activeStage == 1) {
            beginLegitUsePause();

            if (activeInventorySlot != -1) {
                mc.interactionManager.clickSlot(0, activeInventorySlot, activeHotbarSlot, SlotActionType.SWAP, mc.player);
                mc.player.networkHandler.sendPacket(new CloseHandledScreenC2SPacket(0));
            }

            if (activeHand == Hand.MAIN_HAND && activeHotbarSlot != -1) {
                selectHotbarSlot(activeHotbarSlot);
            }

            activeStage = 2;
            return;
        }

        if (activeStage == 2) {
            beginLegitUsePause();
            mc.interactionManager.interactItem(mc.player, activeHand);
            mc.player.swingHand(activeHand);
            NotificationManager.pushCustom("Кинул жемчуг!", getCategory().getIcons());
            activeStage = 3;
            return;
        }

        finishLegitUse();
    }

    private void finishLegitUse() {
        restoreLegitUseState();
        slowingTicks = 0;
        clearLegitUse();
    }

    private void resetState() {
        clearLegitUse();
    }

    private void restoreLegitUseState() {
        if (mc.player == null) return;

        if (activeHand == Hand.MAIN_HAND && activePreviousSlot != -1 && activeStage > 0) {
            selectHotbarSlot(activePreviousSlot);
        }

        if (activeInventorySlot != -1 && activeHotbarSlot != -1 && activeStage > 0 && mc.interactionManager != null) {
            mc.interactionManager.clickSlot(0, activeInventorySlot, activeHotbarSlot, SlotActionType.SWAP, mc.player);
            mc.player.networkHandler.sendPacket(new CloseHandledScreenC2SPacket(0));
        }
    }

    private void clearLegitUse() {
        activeStage = 0;
        activeHand = null;
        activeInventorySlot = -1;
        activeHotbarSlot = -1;
        activePreviousSlot = -1;
    }

    private void beginLegitUsePause() {
        slowingTicks = Math.max(slowingTicks, 2);

        if (mc.player == null || mc.player.networkHandler == null) return;

        mc.player.networkHandler.sendPacket(new PlayerInputC2SPacket(new PlayerInput(false, false, false, false, false, false, false)));

        if (mc.player.isSprinting()) {
            mc.player.setSprinting(false);
            mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.STOP_SPRINTING));
        }
    }

    private void selectHotbarSlot(int slot) {
        if (slot < 0 || slot > 8 || mc.player == null || mc.player.networkHandler == null) return;

        mc.player.getInventory().selectedSlot = slot;
        mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(slot));
    }

    private int findTemporaryHotbarSlot() {
        int fallback = 8;

        for (int slot = 0; slot < 9; slot++) {
            if (slot == mc.player.getInventory().selectedSlot) continue;

            ItemStack stack = mc.player.getInventory().getStack(slot);
            if (stack.isEmpty()) return slot;

            if (stack.getUseAction() == UseAction.NONE) {
                fallback = slot;
            }
        }

        return fallback;
    }
}
