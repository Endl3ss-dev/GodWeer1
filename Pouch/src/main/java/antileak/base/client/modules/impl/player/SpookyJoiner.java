package antileak.base.client.modules.impl.player;

import antileak.base.Punch;
import antileak.base.api.events.EventLink;
import antileak.base.api.events.implement.EventUpdate;
import antileak.base.api.utils.player.InventoryUtils;
import antileak.base.client.modules.Module;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;

import java.util.Locale;

/**
 * Auto-joiner module for SpookyTime server minigames
 * Automatically uses compass to open game selector and clicks netherite sword to join
 */
public class SpookyJoiner extends Module {

    public static SpookyJoiner INSTANCE = new SpookyJoiner();

    private static final int OPEN_COMPASS_DELAY_TICKS = 5;
    private static final int RETRY_DELAY_TICKS = 6;

    private boolean hadCompass;
    private int compassCooldown;

    public SpookyJoiner() {
        super("SpookyJoiner", "Автоматически подключается к мини-играм на SpookyTime", ModuleCategory.PLAYER);
    }

    @Override
    public void onEnable() {
        super.onEnable();

        if (!isOnSpookyTimeServer()) {
            disableModule();
        } else {
            this.hadCompass = hasCompassInInventory();
            this.compassCooldown = 0;
        }
    }

    @Override
    public void onDisable() {
        this.hadCompass = false;
        this.compassCooldown = 0;
        super.onDisable();
    }

    @EventLink
    public void onEvent(EventUpdate event) {
        if (mc.player == null || mc.world == null) {
            return;
        }

        boolean currentlyHasCompass = hasCompassInInventory();

        // If compass disappeared, disable module
        if (this.hadCompass && !currentlyHasCompass) {
            disableModule();
            return;
        }

        this.hadCompass = currentlyHasCompass;

        // Handle cooldown
        if (this.compassCooldown > 0) {
            this.compassCooldown--;
            return;
        }

        Screen currentScreen = mc.currentScreen;

        if (currentScreen instanceof GenericContainerScreen containerScreen) {
            // We're in a GUI - look for netherite sword to click
            handleContainerScreen(containerScreen);
        } else if (currentScreen != null) {
            // Close any other screens
            closeScreen();
            this.compassCooldown = RETRY_DELAY_TICKS;
        } else {
            // No screen open - use compass to open game selector
            useCompass();
            this.compassCooldown = OPEN_COMPASS_DELAY_TICKS;

            // If we have emerald, disable (already in a game)
            if (hasEmeraldInInventory()) {
                disableModule();
            }
        }
    }

    /**
     * Checks if player has compass in inventory
     */
    private boolean hasCompassInInventory() {
        if (mc.player == null) {
            return false;
        }

        // Check main inventory
        for (int i = 0; i < mc.player.getInventory().size(); i++) {
            if (mc.player.getInventory().getStack(i).getItem() == Items.COMPASS) {
                return true;
            }
        }

        // Check offhand
        return mc.player.getOffHandStack().getItem() == Items.COMPASS;
    }

    /**
     * Checks if player has emerald in inventory (indicates already in game)
     */
    private boolean hasEmeraldInInventory() {
        if (mc.player == null) {
            return false;
        }

        // Check main inventory
        for (int i = 0; i < mc.player.getInventory().size(); i++) {
            if (mc.player.getInventory().getStack(i).getItem() == Items.EMERALD) {
                return true;
            }
        }

        // Check offhand
        return mc.player.getOffHandStack().getItem() == Items.EMERALD;
    }

    /**
     * Uses compass item to open game selector GUI
     */
    private void useCompass() {
        if (mc.player == null || mc.interactionManager == null) {
            return;
        }

        // Find compass in hotbar
        int compassSlot = findCompassInHotbar();

        if (compassSlot != -1) {
            // Compass in hotbar - switch to it and use
            mc.player.getInventory().selectedSlot = compassSlot;
            mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
        } else if (mc.player.getOffHandStack().getItem() == Items.COMPASS) {
            // Compass in offhand - use it
            mc.interactionManager.interactItem(mc.player, Hand.OFF_HAND);
        } else {
            // Compass not in hotbar - try to move it there
            moveCompassToHotbar();

            if (mc.player.getMainHandStack().getItem() == Items.COMPASS) {
                mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
            }
        }
    }

    /**
     * Finds compass in hotbar slots (0-8)
     */
    private int findCompassInHotbar() {
        if (mc.player == null) return -1;

        for (int i = 0; i < 9; i++) {
            if (mc.player.getInventory().getStack(i).getItem() == Items.COMPASS) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Moves compass from inventory to hotbar
     */
    private void moveCompassToHotbar() {
        if (mc.player == null || mc.interactionManager == null) return;

        int compassSlot = InventoryUtils.getSlot(Items.COMPASS);
        if (compassSlot == -1 || compassSlot < 9) return;

        // Find empty hotbar slot or use slot 8
        int targetHotbarSlot = 8;
        for (int i = 0; i < 9; i++) {
            if (mc.player.getInventory().getStack(i).isEmpty()) {
                targetHotbarSlot = i;
                break;
            }
        }

        int containerSlot = InventoryUtils.toContainerSlot(compassSlot);
        mc.interactionManager.clickSlot(0, containerSlot, targetHotbarSlot, SlotActionType.SWAP, mc.player);
        mc.player.networkHandler.sendPacket(new CloseHandledScreenC2SPacket(0));
        mc.player.getInventory().selectedSlot = targetHotbarSlot;
    }

    /**
     * Handles clicking netherite sword in container screen
     */
    private void handleContainerScreen(GenericContainerScreen containerScreen) {
        GenericContainerScreenHandler handler = containerScreen.getScreenHandler();
        boolean foundSword = false;

        // Look for netherite sword in GUI
        for (Slot slot : handler.slots) {
            ItemStack stack = slot.getStack();

            if (stack.getItem() == Items.NETHERITE_SWORD) {
                // Click the netherite sword to join game
                if (mc.interactionManager != null && mc.player != null) {
                    mc.interactionManager.clickSlot(handler.syncId, slot.id, 0, SlotActionType.PICKUP, mc.player);
                    this.compassCooldown = RETRY_DELAY_TICKS;
                    foundSword = true;
                    break;
                }
            }
        }

        if (!foundSword) {
            // No sword found, close GUI
            closeScreen();
            this.compassCooldown = RETRY_DELAY_TICKS;
        }
    }

    /**
     * Closes current screen
     */
    private void closeScreen() {
        if (mc.player != null) {
            mc.player.closeHandledScreen();
        }
    }

    /**
     * Checks if connected to SpookyTime server
     */
    private boolean isOnSpookyTimeServer() {
        // Check if in singleplayer
        if (mc.isInSingleplayer()) {
            return true;
        }

        // Check server address
        var serverInfo = mc.getCurrentServerEntry();
        if (serverInfo != null && serverInfo.address != null) {
            return serverInfo.address.toLowerCase(Locale.ROOT).contains("spookytime");
        }

        return false;
    }

    /**
     * Disables the module if not on correct server or conditions not met
     */
    private void disableModule() {
        if (this.isEnable()) {
            this.toggle();
        }
    }
}
