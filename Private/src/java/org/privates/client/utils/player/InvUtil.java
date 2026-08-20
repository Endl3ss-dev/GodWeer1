package org.privates.client.utils.player;

import lombok.experimental.UtilityClass;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CClickWindowPacket;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.potion.Effect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.Hand;
import org.privates.client.api.interfaces.IMinecraft;
import org.privates.client.managers.component.impl.inventory.InvComponent;
import org.privates.client.utils.chat.ChatUtil;
import org.privates.client.utils.other.ViaUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.IntStream;

@UtilityClass
public class InvUtil implements IMinecraft {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public void moveItem(int from, int to) {
        if (from == to || from == -1)
            return;

        from = from < 9 ? from + 36 : from;
        if (PlayerUtil.isFuntime()) {
            int finalFrom = from;
            InvComponent.addTask(() -> {
                clickSlotId(finalFrom, 0, ClickType.SWAP, false);
                clickSlotId(to, 0, ClickType.SWAP, false);
                clickSlotId(finalFrom, 0, ClickType.SWAP, false);
            }, 0);
        } else {
            clickSlotId(from, 0, ClickType.SWAP, false);
            clickSlotId(to, 0, ClickType.SWAP, false);
            clickSlotId(from, 0, ClickType.SWAP, false);
        }
    }

    public void throwItem(int slotId, int count) {
        if (count <= 0 || slotId < 0) return;

        int windowId = mc.player.openContainer.windowId;
        ItemStack stack = mc.player.openContainer.getSlot(slotId).getStack();
        if (stack.isEmpty() || stack.getCount() <= 0) return;

        int actualCount = Math.min(count, stack.getCount());

        if (PlayerUtil.isFuntime()) {
            InvComponent.addTask(() -> {
                clickSlotId(windowId, slotId, 0, ClickType.PICKUP, false);
                if (actualCount < stack.getCount()) {
                    clickSlotId(windowId, slotId, stack.getCount() - actualCount, ClickType.PICKUP, false);
                }
                clickSlotId(windowId, 0, 0, ClickType.THROW, false);
            }, 0);
        } else {
            mc.playerController.windowClick(windowId, slotId, 0, ClickType.PICKUP, mc.player);
            if (actualCount < stack.getCount()) {
                mc.playerController.windowClick(windowId, slotId, stack.getCount() - actualCount, ClickType.PICKUP, mc.player);
            }
            mc.playerController.windowClick(windowId, 0, 0, ClickType.THROW, mc.player);
        }
    }

    public void swapHand(Slot slot, Hand hand, boolean packet) {
        if (slot != null) swapHand(slot.slotNumber, hand, packet);
    }

    public void swapHand(int slotId, Hand hand, boolean packet) {
        if (slotId == -1 || !InvComponent.getInstance().script().isFinished()) return;
        int button = hand.equals(Hand.MAIN_HAND) ? mc.player.inventory.currentItem : 40;

        if (PlayerUtil.isFuntime() && MoveUtil.isMoving())
            InvComponent.addTask(() -> clickSlotId(slotId, button, ClickType.SWAP, packet), 0);
        else clickSlotId(slotId, button, ClickType.SWAP, packet);
    }

    public Slot getSlot(Item item) {
        return mc.player.openContainer.inventorySlots.stream().filter(s -> s.getStack().getItem().equals(item)).findFirst().orElse(null);
    }

    public Slot getInventorySlot(Item item) {
        return mc.player.openContainer.inventorySlots.stream().filter(s -> s.getStack().getItem().equals(item) && s.slotNumber >= mc.player.openContainer.inventorySlots.size() - 37).findFirst().orElse(null);
    }

    public Slot getInventorySlot(List<Item> item) {
        return mc.player.openContainer.inventorySlots.stream().filter(s -> item.contains(s.getStack().getItem()) && s.slotNumber >= mc.player.openContainer.inventorySlots.size() - 37).findFirst().orElse(null);
    }

    public int getInventoryCount(Item item) {
        return IntStream.range(0, 45).filter(i -> mc.player.inventory.getStackInSlot(i).getItem().equals(item)).map(i -> mc.player.inventory.getStackInSlot(i).getCount()).sum();
    }

    public Slot getFoodMaxSaturationSlot() {
        return mc.player.openContainer.inventorySlots.stream().filter(s -> s.getStack().getItem().getFood() != null && !s.getStack().getItem().getFood().canEatWhenFull()).max(Comparator.comparingDouble(s -> s.getStack().getItem().getFood().getSaturation())).orElse(null);
    }

    public Slot getSlot(List<Item> item) {
        return mc.player.openContainer.inventorySlots.stream().filter(s -> item.contains(s.getStack().getItem())).findFirst().orElse(null);
    }

    public Slot getSlotPotion(Effect effect) {
        return mc.player.openContainer.inventorySlots.stream().filter(s -> s.getStack().getItem().equals(Items.POTION) && PotionUtils.getEffectsFromStack(s.getStack()).stream().filter(e -> e.getPotion().equals(effect)).isParallel()).findFirst().orElse(null);
    }

    public int getCount(Item item) {
        return mc.player.openContainer.inventorySlots.stream().filter(s -> s.getStack().getItem().equals(item)).mapToInt(s -> s.getStack().getCount()).sum();
    }

    public int getItemInHotBar(Item item) {
        return IntStream.range(0, 9).filter(i -> mc.player.inventory.getStackInSlot(i).getItem().equals(item)).findFirst().orElse(-1);
    }

    public Slot getAxeSlot() {
        return mc.player.openContainer.inventorySlots.stream().filter(s -> s.getStack().getItem() instanceof AxeItem).findFirst().orElse(null);
    }

    public boolean findItemAndThrow(Item item, float yaw, float pitch) {
        return findItemAndThrow(item, yaw, pitch, Hand.MAIN_HAND, true);
    }

    public boolean findItemAndThrow(Item item, float yaw, float pitch, Hand hand, boolean packet) {
        if (mc.player.getCooldownTracker().hasCooldown(item)) {
            ChatUtil.addTextWithError(item.getName().getString() + " - в кд");
            return false;
        }

        Slot slot = InvUtil.getSlot(item);
        if (slot == null) {
            ChatUtil.addTextWithError(item.getName().getString() + " - нету");
            return false;
        }

        findItemAndThrow(slot, yaw, pitch, hand, packet);
        return true;
    }

    public void findItemAndThrow(Slot slot, float yaw, float pitch) {
        findItemAndThrow(slot, yaw, pitch, Hand.MAIN_HAND, true);
    }

    public void findItemAndThrow(Slot slot, float yaw, float pitch, Hand hand, boolean packet) {
        if (PlayerUtil.isFuntime()) {
            InvComponent.addTask(() -> {
                swapHand(slot, hand, true);
                if (packet) {
                    useItem(hand, yaw, pitch);
                } else {
                    useItemNormal(hand, yaw, pitch);
                }
                swapHand(slot, hand, true);
            }, 0, true);
        } else {
            swapHand(slot, hand, true);
            if (packet) {
                useItem(hand, yaw, pitch);
            } else {
                useItemNormal(hand, yaw, pitch);
            }
            swapHand(slot, hand, true);
        }
    }

    public void clickSlot(Slot slot, int button, ClickType clickType, boolean packet) {
        if (slot != null) clickSlotId(slot.slotNumber, button, clickType, packet);
    }

    public void clickSlotId(int slotId, int buttonId, ClickType clickType, boolean packet) {
        clickSlotId(mc.player.openContainer.windowId, slotId, buttonId, clickType, packet);
    }

    public void clickSlotId(int windowId, int slotId, int buttonId, ClickType clickType, boolean packet) {
        if (packet) {
            mc.player.connection.sendPacket(new CClickWindowPacket(windowId, slotId, buttonId, clickType, ItemStack.EMPTY, mc.player.openContainer.getNextTransactionID(mc.player.inventory)));
        } else {
            mc.playerController.windowClick(windowId, slotId, buttonId, clickType, mc.player);
        }
    }

    public void useItem(Hand hand, float yaw, float pitch) {
        if (ViaUtil.allowedBypass()) {
            mc.player.connection.sendPacketWithoutEvent(new CPlayerPacket.PositionRotationPacket(mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ(), yaw, pitch, mc.player.isOnGround()));
        }
        mc.player.connection.sendPacketWithoutEvent(new CPlayerTryUseItemPacket(hand));
    }

    public void useItemNormal(Hand hand, float yaw, float pitch) {
        if (ViaUtil.allowedBypass()) {
            mc.player.connection.sendPacketWithoutEvent(new CPlayerPacket.PositionRotationPacket(mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ(), yaw, pitch, mc.player.isOnGround()));
        }
        mc.playerController.processRightClick(mc.player, mc.world, hand);
    }

    public long noEmptyHotBarSlots() {
        return getHotBarSlots().stream().filter(s -> s.getStack().isEmpty()).count();
    }

    public List<Slot> getHotBarSlots() {
        List<Slot> slots = mc.player.openContainer.inventorySlots;
        return new ArrayList<>(slots.stream().filter(i -> i.slotNumber > slots.size() - 10).toList());
    }

    public List<Slot> getMainInventorySlots() {
        List<Slot> list = new ArrayList<>();
        for (int i = mc.player.openContainer.inventorySlots.size() - 37; i < mc.player.openContainer.inventorySlots.size(); i++) {
            list.add(mc.player.openContainer.getSlot(i));
        }
        return list;
    }

    public int getFireWorks() {
        return getItemInHotBar(Items.FIREWORK_ROCKET);
    }
}