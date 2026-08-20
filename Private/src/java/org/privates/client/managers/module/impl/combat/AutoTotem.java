package org.privates.client.managers.module.impl.combat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.entity.item.minecart.TNTMinecartEntity;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.play.server.SEntityStatusPacket;
import net.minecraft.potion.Effects;
import net.minecraft.util.Hand;
import org.privates.client.api.annotations.Category3;
import org.privates.client.api.annotations.Customize;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.other.PacketEvent;
import org.privates.client.managers.events.player.MovementManager;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.client.managers.module.settings.impl.MultiBooleanSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.player.InvUtil;
import org.privates.client.utils.player.PlayerUtil;
import org.privates.lib.util.time.StopWatch;


@Category3
@Customize
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "AutoTotem", category = Category.COMBAT, description = "Берёт тотем в руку на лоу хп")
public class AutoTotem extends Module {
    public static AutoTotem getInstance() {
        return Instance.get(AutoTotem.class);
    }

    private final SliderSetting health = new SliderSetting(this, "Уровень здоровья", 4, 4, 20, 1);
    private final SliderSetting elytraHealth = new SliderSetting(this, "Уровень здоровья на элитре", 10, 4, 20, 1);
    private final BooleanSetting swapBack = new BooleanSetting(this, "Возвращать предмет", true);
    private final BooleanSetting noBallSwitch = new BooleanSetting(this, "Не сменять шар", false);
    private final BooleanSetting saveEnchanted = new BooleanSetting(this, "Сохранять зачарованный", true);
    private final BooleanSetting holyWorldOnly = new BooleanSetting(this, "Holyworld Easy", false);
    private final MultiBooleanSetting mode = new MultiBooleanSetting(this, "Проверки на",
            BooleanSetting.of("Золотые сердца", true),
            BooleanSetting.of("Кристаллы", false),
            BooleanSetting.of("Якорь возрождения", false),
            BooleanSetting.of("Падение", true));

    private final ModeSetting legitMode = new ModeSetting(this, "Режим", "Обычный", "HolyWorld", "Легитный").set("Обычный");
    private final BooleanSetting onlyHotbar = new BooleanSetting(this, "Только хотбар", false).setVisible(() -> legitMode.getValue().equals("Легитный"));
    private final BooleanSetting openInventory = new BooleanSetting(this, "Открывать инвентарь", false).setVisible(() -> legitMode.getValue().equals("Легитный"));
    private final BooleanSetting stopMovement = new BooleanSetting(this, "Останавливать движение", true).setVisible(() -> !legitMode.getValue().equals("Обычный"));
    private final BooleanSetting pressShift = new BooleanSetting(this, "Нажимать Shift", false).setVisible(() -> legitMode.getValue().equals("Легитный"));
    private final BooleanSetting stopForwardMovement = new BooleanSetting(this, "Останавливать движение вперед", false);
    private final BooleanSetting resetSprint = new BooleanSetting(this, "Сбрасывать спринт", false);

    private final SliderSetting delayBeforeSwap = new SliderSetting(this, "Задержка до свапа (мс)", 150, 0, 1000, 10).setVisible(() -> !legitMode.getValue().equals("Обычный"));
    private final SliderSetting delayAfterSwap = new SliderSetting(this, "Задержка после свапа (мс)", 150, 0, 1000, 10).setVisible(() -> !legitMode.getValue().equals("Обычный"));
    private final SliderSetting stopMovementBefore = new SliderSetting(this, "Остановка движения до свапа (мс)", 200, 0, 1000, 10).setVisible(() -> stopMovement.getValue() && !legitMode.getValue().equals("Обычный"));
    private final SliderSetting stopMovementAfter = new SliderSetting(this, "Остановка движения после свапа (мс)", 200, 0, 1000, 10).setVisible(() -> stopMovement.getValue() && !legitMode.getValue().equals("Обычный"));

    private int nonEnchantedTotems;
    private int oldItem = -1;
    private int totemCount = 0;
    private boolean totemIsUsed;
    public boolean isActive;
    private final StopWatch time = new StopWatch();

    private long lastUseTime = 0;
    private static final long USE_DELAY = 50;

    private boolean isBlockingMovement = false;
    private long blockEndTime = 0;
    private long swapTime = 0;
    private boolean swapQueued = false;
    private boolean inventoryOpened = false;
    private boolean shiftPressed = false;
    private boolean isSwappingBack = false;
    private boolean wasForwardPressed = false;
    private boolean wasSprintingBeforeBlock = false;

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (isBlockingMovement) {
            if (stopMovement.getValue()) {
                MovementManager.getInstance().lockMovement("AutoTotem");
            }
            long now = System.currentTimeMillis();
            if (swapQueued && now >= swapTime) {
                performTotemSwap();
                swapQueued = false;
            }
            if (now >= blockEndTime) {
                isBlockingMovement = false;
                restoreLegitMovement();
            }
        }

        totemCount = InvUtil.getInventoryCount(Items.TOTEM_OF_UNDYING);
        nonEnchantedTotems = (int) mc.player.openContainer.inventorySlots.stream().filter(s -> s.getStack().getItem().equals(Items.TOTEM_OF_UNDYING) && !s.getStack().isEnchanted()).count();
        Slot slot = getTotemSlot();

        if (time.finished(400)) {
            if (shouldToSwapTotem()) {
                if (slot != null && !isTotemInHands()) {
                    if (!mc.player.getHeldItemOffhand().isEmpty() && oldItem == -1) oldItem = slot.slotNumber;

                    if (legitMode.getValue().equals("Обычный")) {
                        useTotemWithBypass(slot);
                    } else {
                        handleLegitTotemSwap(slot);
                    }
                    time.reset();
                }
            } else if (oldItem != -1 && swapBack.getValue()) {
                if (!legitMode.getValue().equals("Обычный")) {
                    handleLegitSwapBack();
                } else {
                    InvUtil.swapHand(oldItem, Hand.OFF_HAND, false);
                    oldItem = -1;
                }
                time.reset();
            }
        }
    }

    private Slot getTotemSlot() {
        if (onlyHotbar.getValue() && legitMode.getValue().equals("Легитный")) {
            for (int i = 0; i < 9; i++) {
                ItemStack stack = mc.player.inventory.getStackInSlot(i);
                if (stack.getItem() == Items.TOTEM_OF_UNDYING && isNotSaveEnchanted(stack)) {
                    return mc.player.container.inventorySlots.get(i + 36);
                }
            }
            return null;
        }

        return mc.player.openContainer.inventorySlots.stream()
                .filter(s -> s.getStack().getItem().equals(Items.TOTEM_OF_UNDYING) && this.isNotSaveEnchanted(s.getStack()))
                .findFirst().orElse(null);
    }

    private void handleLegitTotemSwap(Slot slot) {
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

        if (openInventory.getValue() && mc.currentScreen == null) {
            mc.displayScreen(new InventoryScreen(mc.player));
            inventoryOpened = true;
        }

        if (legitMode.getValue().equals("HolyWorld")) {
            handleTimedMode(slot);
        } else if (legitMode.getValue().equals("Легитный")) {
            handleFullLegitMode(slot);
        }
    }

    private void handleTimedMode(Slot slot) {
        isBlockingMovement = true;
        long now = System.currentTimeMillis();
        swapTime = now + delayBeforeSwap.getValue().intValue();
        blockEndTime = now + stopMovementBefore.getValue().intValue() + delayBeforeSwap.getValue().intValue() + delayAfterSwap.getValue().intValue() + stopMovementAfter.getValue().intValue();
        swapQueued = true;
        currentSwapSlot = slot;
    }

    private void handleFullLegitMode(Slot slot) {
        isBlockingMovement = true;
        long now = System.currentTimeMillis();
        int randomDelay = (int) (Math.random() * 100);
        swapTime = now + delayBeforeSwap.getValue().intValue() + randomDelay;
        blockEndTime = now + stopMovementBefore.getValue().intValue() + delayBeforeSwap.getValue().intValue() + randomDelay + delayAfterSwap.getValue().intValue() + stopMovementAfter.getValue().intValue();
        swapQueued = true;
        currentSwapSlot = slot;
    }

    private void handleLegitSwapBack() {
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

        if (openInventory.getValue() && mc.currentScreen == null) {
            mc.displayScreen(new InventoryScreen(mc.player));
            inventoryOpened = true;
        }

        isBlockingMovement = true;
        long now = System.currentTimeMillis();
        int randomDelay = legitMode.getValue().equals("Легитный") ? (int) (Math.random() * 100) : 0;
        swapTime = now + delayBeforeSwap.getValue().intValue() + randomDelay;
        blockEndTime = now + stopMovementBefore.getValue().intValue() + delayBeforeSwap.getValue().intValue() + randomDelay + delayAfterSwap.getValue().intValue() + stopMovementAfter.getValue().intValue();
        swapQueued = true;
        isSwappingBack = true;
    }

    private Slot currentSwapSlot;

    private void performTotemSwap() {
        if (isSwappingBack) {
            InvUtil.swapHand(oldItem, Hand.OFF_HAND, false);
            oldItem = -1;
            isSwappingBack = false;
        } else if (currentSwapSlot != null) {
            InvUtil.swapHand(currentSwapSlot, Hand.OFF_HAND, false);
            lastUseTime = System.currentTimeMillis();
            currentSwapSlot = null;
        }
    }

    private void restoreLegitMovement() {
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
            MovementManager.getInstance().unlockMovement("AutoTotem");
        }
    }

    private void useTotemWithBypass(Slot slot) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastUseTime < USE_DELAY) return;

        boolean shouldStopMovement = !holyWorldOnly.getValue() || PlayerUtil.isHoly();

        if (shouldStopMovement) {
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
                MovementManager.getInstance().lockMovement("AutoTotem");
                new Thread(() -> {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                    MovementManager.getInstance().unlockMovement("AutoTotem");
                    
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
                }).start();
            }
        }

        InvUtil.swapHand(slot, Hand.OFF_HAND, false);
        lastUseTime = currentTime;
    }

    @EventHandler
    public void onPacket(PacketEvent event) {
        if (event.isReceive()) {
            if (event.getPacket() instanceof SEntityStatusPacket statusPacket && statusPacket.getOpCode() == 35 && statusPacket.getEntity(mc.world) == mc.player) {
                this.totemIsUsed = true;
            }
        }
    }

    private boolean isTotemInHands() {
        Hand[] hands = Hand.values();

        for (Hand hand : hands) {
            ItemStack heldItem = mc.player.getHeldItem(hand);
            if (heldItem.getItem() == Items.TOTEM_OF_UNDYING && this.isNotSaveEnchanted(heldItem)) {
                return true;
            }
        }

        return false;
    }

    private boolean isNotSaveEnchanted(ItemStack itemStack) {
        return !this.saveEnchanted.getValue() || !itemStack.isEnchanted() || this.nonEnchantedTotems <= 0;
    }

    private boolean shouldToSwapTotem() {
        final float absorptionAmount = mc.player.isPotionActive(Effects.ABSORPTION) ? mc.player.getAbsorptionAmount() : 0.0f;
        float currentHealth = mc.player.getHealth();
        if (mode.getValue("Золотые сердца")) {
            currentHealth += absorptionAmount;
        }
        if (!isOffhandItemBall()) {
            if (isInDangerousSituation()) {
                return true;
            }
        }

        float healthThreshold = mc.player.isElytraFlying() ? elytraHealth.getValue() : health.getValue();
        return currentHealth <= healthThreshold;
    }

    private boolean isInDangerousSituation() {
        return checkCrystal() || checkAnchor() || checkFall();
    }

    private boolean checkFall() {
        if (!this.mode.getValue("Падение")) {
            return false;
        }
        if (mc.player.isInWater()) {
            return false;
        }
        if (mc.player.isElytraFlying()) {
            return false;
        }
        return mc.player.fallDistance > 10.0f;
    }

    private boolean checkAnchor() {
        if (!mode.getValue("Якорь возрождения")) return false;
        return PlayerUtil.getBlock(6.0F, Blocks.RESPAWN_ANCHOR) != null;
    }

    private boolean checkCrystal() {
        if (!mode.getValue("Кристаллы")) {
            return false;
        }
        for (Entity entity : mc.world.getAllEntities()) {
            if (isDangerousEntityNearPlayer(entity)) {
                return true;
            }
        }
        return false;
    }

    private boolean isOffhandItemBall() {
        boolean isFallingConditionMet = this.mode.getValue("Падение") && mc.player.fallDistance > 5.0f;
        if (isFallingConditionMet) {
            return false;
        }
        return this.noBallSwitch.getValue() && mc.player.getHeldItemOffhand().getItem() == Items.PLAYER_HEAD;
    }

    private boolean isDangerousEntityNearPlayer(Entity entity) {
        return (entity instanceof TNTEntity || entity instanceof TNTMinecartEntity || entity instanceof EnderCrystalEntity) && mc.player.getDistance(entity) <= 6.0F;
    }

    public void reset() {
        this.oldItem = -1;
        this.isBlockingMovement = false;
        this.swapQueued = false;
        this.currentSwapSlot = null;
        this.isSwappingBack = false;
        this.wasForwardPressed = false;
        this.wasSprintingBeforeBlock = false;

        if (resetSprint.getValue()) {
            mc.gameSettings.keyBindBack.setPressed(false);
        }

        if (stopForwardMovement.getValue() && wasForwardPressed) {
            mc.gameSettings.keyBindForward.setPressed(true);
        }

        if (shiftPressed) {
            mc.gameSettings.keyBindSneak.setPressed(false);
            shiftPressed = false;
        }

        if (inventoryOpened && mc.currentScreen instanceof InventoryScreen) {
            mc.displayScreen(null);
            inventoryOpened = false;
        }

        MovementManager.getInstance().unlockMovement("AutoTotem");
    }

    @Override
    public void toggle() {
        super.toggle();
        reset();
    }
}