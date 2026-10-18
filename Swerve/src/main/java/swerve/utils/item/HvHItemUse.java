package swerve.utils.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.Items;
import swerve.mixins.accessor.LocalPlayerAccessor;

// Adapted from AlphaDLC/src/main/java/zov/alphadlc/util/player/other/InventoryUtil.java
// (swapAndUseHvH). Keep packet-only hotbar selection, input suppression and slot-8 fallback.
public final class HvHItemUse {

    private static final ItemStack FIREWORK_COOLDOWN_STACK = new ItemStack(Items.FIREWORK_ROCKET);
    private static final ServerboundPlayerInputPacket EMPTY_INPUT = new ServerboundPlayerInputPacket(
            new Input(false, false, false, false, false, false, false));

    private HvHItemUse() {
    }

    public static void swapAndUseFirework() {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || mc.gameMode == null || mc.getConnection() == null) return;
        if (player.getCooldowns().isOnCooldown(FIREWORK_COOLDOWN_STACK)) return;

        Inventory inventory = player.getInventory();
        int slot = search(inventory, Items.FIREWORK_ROCKET, 9, 45);
        int hotbarSlot = search(inventory, Items.FIREWORK_ROCKET, 0, 9);
        int previousSlot = inventory.selected;
        if (player.getMainHandItem().is(Items.FIREWORK_ROCKET)) {
            mc.gameMode.useItem(player, InteractionHand.MAIN_HAND);
            return;
        }
        if (player.getOffhandItem().is(Items.FIREWORK_ROCKET)) {
            mc.gameMode.useItem(player, InteractionHand.OFF_HAND);
            return;
        }
        if (hotbarSlot != -1) {
            player.connection.send(new ServerboundSetCarriedItemPacket(hotbarSlot));
            mc.gameMode.useItem(player, InteractionHand.MAIN_HAND);
            player.connection.send(new ServerboundSetCarriedItemPacket(previousSlot));
        }
        if (hotbarSlot == -1 && slot != -1) {
            int correctableSlot = -1;
            for (int i = 0; i < 8; i++) {
                ItemStack stack = inventory.getItem(i);
                if (stack.isEmpty()) correctableSlot = i;
                if (stack.getUseAnimation() == ItemUseAnimation.NONE) correctableSlot = i;
            }
            player.connection.send(EMPTY_INPUT);
            // AlphaDLC reads ServerManager's outgoing sprint state; vanilla stores it here.
            if (((LocalPlayerAccessor) player).swerve$wasSprinting()) {
                player.setSprinting(false);
                player.connection.send(new ServerboundPlayerCommandPacket(player,
                        ServerboundPlayerCommandPacket.Action.STOP_SPRINTING));
                // Swerve has no Sprint module to keep this binding pressed.
                mc.options.keySprint.setDown(false);
            }
            int swapSlot = correctableSlot == -1 ? 8 : correctableSlot;
            mc.gameMode.handleInventoryMouseClick(0, slot, swapSlot, ClickType.SWAP, player);
            player.connection.send(new ServerboundContainerClosePacket(0));
            player.connection.send(new ServerboundSetCarriedItemPacket(swapSlot));
            mc.gameMode.useItem(player, InteractionHand.MAIN_HAND);
            player.connection.send(new ServerboundSetCarriedItemPacket(previousSlot));
            // The source deliberately leaves rockets in slot 8 when slots 0..7 are all usable.
            if (correctableSlot != -1) {
                mc.gameMode.handleInventoryMouseClick(0, slot, swapSlot, ClickType.SWAP, player);
                player.connection.send(new ServerboundContainerClosePacket(0));
            }
            player.connection.send(new ServerboundPlayerInputPacket(player.input.keyPresses));
        }
    }

    private static int search(Inventory inventory, Item item, int start, int end) {
        for (int i = start; i < end; i++) {
            if (inventory.getItem(i).is(item)) return i;
        }
        return -1;
    }
}
