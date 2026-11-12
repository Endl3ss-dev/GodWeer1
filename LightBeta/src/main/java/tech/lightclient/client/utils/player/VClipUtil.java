package tech.lightclient.client.utils.player;

import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.math.BlockPos;

public final class VClipUtil {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private static final int CHEST_SCREEN_SLOT = 6;

    private VClipUtil() {
    }

    public static boolean clip(double yOffset) {
        if (mc.player == null || mc.world == null || mc.player.networkHandler == null
                || Math.abs(yOffset) < 1.0E-4) {
            return false;
        }
        return clipToY(mc.player.getY() + yOffset);
    }

    public static boolean clipToY(double destinationY) {
        if (mc.player == null || mc.world == null || mc.player.networkHandler == null
                || !Double.isFinite(destinationY)) {
            return false;
        }
        int elytraSlot = findInventoryElytraScreenSlot();
        boolean swappedElytra = elytraSlot != -1;
        if (swappedElytra) {
            swapChest(elytraSlot);
        }

        double x = mc.player.getX();
        double y = mc.player.getY();
        double z = mc.player.getZ();
        double yOffset = destinationY - y;
        boolean collision = mc.player.horizontalCollision;

        if (swappedElytra) {
            for (int i = 0; i < 2; i++) {
                mc.player.networkHandler.sendPacket(
                        new PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, false, collision));
            }
            startFallFlying();
            mc.player.networkHandler.sendPacket(
                    new PlayerMoveC2SPacket.PositionAndOnGround(x, destinationY, z, false, collision));
            startFallFlying();
        } else {
            int packets = Math.max((int) (Math.abs(yOffset) / 1000.0), 3);
            for (int i = 0; i < packets; i++) {
                mc.player.networkHandler.sendPacket(
                        new PlayerMoveC2SPacket.OnGroundOnly(mc.player.isOnGround(), collision));
            }
            mc.player.networkHandler.sendPacket(
                    new PlayerMoveC2SPacket.PositionAndOnGround(x, destinationY, z, false, collision));
        }

        mc.player.setPosition(x, destinationY, z);
        mc.player.fallDistance = 0.0F;

        if (swappedElytra) {
            swapChest(elytraSlot);
        }
        return true;
    }

    public static double scan(boolean up) {
        if (mc.player == null || mc.world == null) {
            return 0.0;
        }

        BlockPos origin = mc.player.getBlockPos();
        int start = up ? 3 : -1;
        int end = up ? 320 : mc.world.getBottomY() - origin.getY();
        int step = up ? 1 : -1;
        for (int offset = start; up ? offset < end : offset > end; offset += step) {
            BlockPos target = origin.up(offset);
            if (mc.world.getBlockState(target).isAir()) {
                return offset + (up ? 1 : -1);
            }
            if (!up && mc.world.getBlockState(target).isOf(net.minecraft.block.Blocks.BEDROCK)) {
                return 0.0;
            }
        }
        return 0.0;
    }

    private static void startFallFlying() {
        mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(
                mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
    }

    private static int findInventoryElytraScreenSlot() {
        if (mc.player.getEquippedStack(EquipmentSlot.CHEST).get(DataComponentTypes.GLIDER) != null) {
            return -1;
        }
        for (int inventorySlot = 0; inventorySlot < 36; inventorySlot++) {
            if (mc.player.getInventory().getStack(inventorySlot).isOf(Items.ELYTRA)) {
                return inventorySlot < 9 ? inventorySlot + 36 : inventorySlot;
            }
        }
        return -1;
    }

    private static void swapChest(int inventoryScreenSlot) {
        if (mc.interactionManager == null) {
            return;
        }
        int syncId = mc.player.playerScreenHandler.syncId;
        mc.interactionManager.clickSlot(syncId, inventoryScreenSlot, 0, SlotActionType.PICKUP, mc.player);
        mc.interactionManager.clickSlot(syncId, CHEST_SCREEN_SLOT, 0, SlotActionType.PICKUP, mc.player);
        mc.interactionManager.clickSlot(syncId, inventoryScreenSlot, 0, SlotActionType.PICKUP, mc.player);
    }
}
