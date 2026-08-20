package org.privates.client.managers.module.impl.misc;

import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CCloseWindowPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TextFormatting;
import org.privates.client.api.annotations.Category2;
import org.privates.client.api.annotations.HolyWorld;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.input.KeyboardPressEvent;
import org.privates.client.managers.events.input.MousePressEvent;
import org.privates.client.managers.events.player.MotionEvent;
import org.privates.client.managers.events.player.MovementManager;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BindSetting;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.utils.chat.ChatUtil;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.other.InventoryUtil;
import org.privates.lib.util.time.StopWatch;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Category2
@HolyWorld
@ModuleInfo(
        name = "HolyWorldHelper",
        category = Category.MISC,
        description = "Помощник для сервера HolyWorld"
)
public class HolyWorldAssist extends Module {
    final BindSetting throwKey = new BindSetting(this, "Взрывная трапка", -1);
    final BindSetting secondaryKey = new BindSetting(this, "Стан", -1);
    final BindSetting firebollKey = new BindSetting(this, "Взрывная штучка", -1);
    final BindSetting trapkaKey = new BindSetting(this, "Трапка", -1);
    final BindSetting sneshokKey = new BindSetting(this, "Ком снега", -1);
    final BooleanSetting notif = new BooleanSetting(this, "Чат свап", true);
    final BooleanSetting megabypass = new BooleanSetting(this, "Обход HW", true);
    SwapState swapState;
    boolean actionPending;
    long lastItemThrowTime;
    int itemSlot;
    Item currentItem;
    int swappedFromSlot;
    boolean isSwappingItem;
    final StopWatch movementStopWatch;
    final StopWatch actionTimer;

    private enum ActionType {IDLE, SWITCH, USE, SWAP_BACK}

    private final Random random = new Random();
    private ActionType actionType = ActionType.IDLE;
    private int oldHotbarSlot = -1;
    private int targetHotbarSlot = -1;
    private long actionStartTime;

    private static final float ITEM_SPACING = 2;
    private static final float TEXT_SIZE = 6.5F;
    private final long lastTimeNs = 0L;
    private final Map<String, Float> itemAnimations = new HashMap<>();

    public HolyWorldAssist() {
        this.swapState = SwapState.IDLE;
        this.actionPending = false;
        this.lastItemThrowTime = 0L;
        this.itemSlot = -1;
        this.swappedFromSlot = -1;
        this.isSwappingItem = false;
        this.movementStopWatch = new StopWatch();
        this.actionTimer = new StopWatch();
    }

    public static HolyWorldAssist getInstance() {
        return Instance.get(HolyWorldAssist.class);
    }

    void printIfEnabled(String var1) {
        if (this.notif.getValue()) {
            ChatUtil.addText(var1);
        }

    }

    String getItemIcon(String var1) {
        switch (var1.toLowerCase()) {
            case "взрывная трапка" -> {
                return "j";
            }
            case "стан" -> {
                return "j";
            }
            case "взрывная штучка" -> {
                return "j";
            }
            case "трапка" -> {
                return "j";
            }
            case "ком снега" -> {
                return "j";
            }
            default -> {
                return "j";
            }
        }
    }

    String getItemNameByItem(Item var1) {
        if (var1 == Items.PRISMARINE_SHARD) {
            return "Взрывная трапка";
        } else if (var1 == Items.NETHER_STAR) {
            return "Стан";
        } else if (var1 == Items.FIRE_CHARGE) {
            return "Взрывная штучка";
        } else if (var1 == Items.POPPED_CHORUS_FRUIT) {
            return "Трапка";
        } else {
            return var1 == Items.SNOWBALL ? "Ком снега" : "Неизвестный предмет";
        }
    }

    @EventHandler
    public void onKeyPress(KeyboardPressEvent e) {
        if (e.getScreen() != null) return;
        if (actionType != ActionType.IDLE) return;

        int key = e.getKey();
        Item selectedItem = null;

        if (key == this.throwKey.getValue()) {
            selectedItem = Items.PRISMARINE_SHARD;
        } else if (key == this.secondaryKey.getValue()) {
            selectedItem = Items.NETHER_STAR;
        } else if (key == this.firebollKey.getValue()) {
            selectedItem = Items.FIRE_CHARGE;
        } else if (key == this.trapkaKey.getValue()) {
            selectedItem = Items.POPPED_CHORUS_FRUIT;
        } else if (key == this.sneshokKey.getValue()) {
            selectedItem = Items.SNOWBALL;
        }

        if (selectedItem != null) {
            if (mc.player.getCooldownTracker().hasCooldown(selectedItem)) {
                ChatUtil.addText(TextFormatting.GRAY + "Предмет в кулдауне");
                return;
            }

            int hotbarSlot = InventoryUtil.getInstance().getSlotInInventoryOrHotbar(selectedItem, true);
            if (hotbarSlot != -1) {
                currentItem = selectedItem;
                oldHotbarSlot = mc.player.inventory.currentItem;
                targetHotbarSlot = hotbarSlot;
                actionType = ActionType.SWITCH;
                actionStartTime = System.currentTimeMillis();
            } else {
                int itemSlot = InventoryUtil.getInstance().getSlotInInventoryOrHotbar(selectedItem, false);
                if (itemSlot == -1) {
                    ChatUtil.addText(TextFormatting.RED + "нету предмета!");
                    resetState();
                    return;
                }

                currentItem = selectedItem;
                actionPending = true;
                isSwappingItem = true;
                swapState = SwapState.IDLE;
                actionTimer.reset();
                movementStopWatch.reset();
                MovementManager.getInstance().lockMovement("HWHelper");
            }
        }
    }

    @EventHandler
    public void onMousePress(MousePressEvent e) {
        if (e.getScreen() != null) return;
        if (actionType != ActionType.IDLE) return;

        int bindValue = e.getKey() + 500;
        Item selectedItem = null;

        if (bindValue == this.throwKey.getValue()) {
            selectedItem = Items.PRISMARINE_SHARD;
        } else if (bindValue == this.secondaryKey.getValue()) {
            selectedItem = Items.NETHER_STAR;
        } else if (bindValue == this.firebollKey.getValue()) {
            selectedItem = Items.FIRE_CHARGE;
        } else if (bindValue == this.trapkaKey.getValue()) {
            selectedItem = Items.POPPED_CHORUS_FRUIT;
        } else if (bindValue == this.sneshokKey.getValue()) {
            selectedItem = Items.SNOWBALL;
        }

        if (selectedItem != null) {
            if (mc.player.getCooldownTracker().hasCooldown(selectedItem)) {
                ChatUtil.addText(TextFormatting.GRAY + "Предмет в кулдауне");
                return;
            }

            int hotbarSlot = InventoryUtil.getInstance().getSlotInInventoryOrHotbar(selectedItem, true);
            if (hotbarSlot != -1) {
                currentItem = selectedItem;
                oldHotbarSlot = mc.player.inventory.currentItem;
                targetHotbarSlot = hotbarSlot;
                actionType = ActionType.SWITCH;
                actionStartTime = System.currentTimeMillis();
            } else {
                int itemSlot = InventoryUtil.getInstance().getSlotInInventoryOrHotbar(selectedItem, false);
                if (itemSlot == -1) {
                    ChatUtil.addText(TextFormatting.RED + "нету предмета!");
                    resetState();
                    return;
                }

                currentItem = selectedItem;
                actionPending = true;
                isSwappingItem = true;
                swapState = SwapState.IDLE;
                actionTimer.reset();
                movementStopWatch.reset();
                MovementManager.getInstance().lockMovement("HWHelper");
            }
        }
    }

    @EventHandler
    void onMotion(MotionEvent var1) {
        if (actionType != ActionType.IDLE && currentItem != null) {
            long now = System.currentTimeMillis();
            switch (actionType) {
                case SWITCH:
                    if (targetHotbarSlot != mc.player.inventory.currentItem) {
                        mc.player.inventory.currentItem = targetHotbarSlot;
                    }
                    if (now - actionStartTime >= 30 + random.nextInt(20)) {
                        actionType = ActionType.USE;
                        actionStartTime = now;
                    }
                    break;
                case USE:
                    mc.playerController.processRightClick(mc.player, mc.world, Hand.MAIN_HAND);
                    mc.player.swingArm(Hand.MAIN_HAND);
                    String itemName = getItemNameByItem(currentItem);
                    ChatUtil.addText(TextFormatting.GRAY + "использовал предмет");
                    actionType = ActionType.SWAP_BACK;
                    actionStartTime = now;
                    break;
                case SWAP_BACK:
                    if (now - actionStartTime >= 45 + random.nextInt(15)) {
                        if (oldHotbarSlot != mc.player.inventory.currentItem) {
                            mc.player.inventory.currentItem = oldHotbarSlot;
                        }
                        currentItem = null;
                        oldHotbarSlot = -1;
                        targetHotbarSlot = -1;
                        actionType = ActionType.IDLE;
                    }
                    break;
                default:
                    break;
            }
        }

        if (this.actionPending && System.currentTimeMillis() - this.lastItemThrowTime >= 80L) {
            if (this.currentItem != null) {
                if (this.swapState == SwapState.IDLE) {
                    this.itemSlot = InventoryUtil.getInstance().getSlotInInventoryOrHotbar(this.currentItem, false);
                    if (this.itemSlot != -1) {
                        ItemStack var2 = mc.player.inventory.getStackInSlot(this.itemSlot);
                        this.startSwap(this.itemSlot);
                        String var3 = this.getItemNameByItem(this.currentItem);
                        ChatUtil.addText(TextFormatting.GRAY + "использовал предмет");
                    } else {
                        String var4 = this.getItemNameByItem(this.currentItem);
                        ChatUtil.addText(TextFormatting.RED + "нету предмета!");
                        this.resetState();
                    }
                } else if (this.swapState == SwapState.SWAPPED && this.actionTimer.finished(60L)) {
                    this.useItem();
                } else if (this.swapState == SwapState.USING && this.actionTimer.finished(70L)) {
                    this.returnItem();
                }
            }
        }
    }

    void startSwap(int var1) {
        if (mc.player.container == null) {
            this.resetState();
        } else {
            MovementManager.getInstance().lockMovement("HWHelper");
            this.swappedFromSlot = -1;

            try {
                if (var1 >= 9 && MovementManager.getInstance().lockRequests.contains("HWHelper")) {
                    this.swappedFromSlot = var1;
                    mc.playerController.windowClick(mc.player.container.windowId, var1 < 9 ? var1 + 36 : var1, 40, ClickType.SWAP, mc.player);
                    mc.player.connection.sendPacket(new CCloseWindowPacket(mc.player.container.windowId));
                    this.swapState = SwapState.SWAPPING;
                } else {
                    mc.playerController.windowClick(mc.player.container.windowId, var1 < 9 ? var1 + 36 : var1, 40, ClickType.SWAP, mc.player);
                    mc.player.connection.sendPacket(new CCloseWindowPacket(mc.player.container.windowId));
                    this.swapState = SwapState.SWAPPING;
                }

                this.swapState = SwapState.SWAPPED;
                this.actionTimer.reset();
            } catch (Exception var3) {
                this.resetState();
            }

        }
    }

    void useItem() {
        MovementManager.getInstance().lockMovement("HWHelper");
        mc.playerController.processRightClick(mc.player, mc.world, Hand.OFF_HAND);
        mc.player.swingArm(Hand.OFF_HAND);
        this.swapState = SwapState.USING;
        this.lastItemThrowTime = System.currentTimeMillis();
        this.actionTimer.reset();
    }

    void returnItem() {
        if (mc.player.container != null && MovementManager.getInstance().lockRequests.contains("HWHelper")) {
            MovementManager.getInstance().lockMovement("HWHelper");

            try {
                if (this.swappedFromSlot != -1) {
                    mc.playerController.windowClick(mc.player.container.windowId, this.swappedFromSlot < 9 ? this.swappedFromSlot + 36 : this.swappedFromSlot, 40, ClickType.SWAP, mc.player);
                    mc.player.connection.sendPacket(new CCloseWindowPacket(mc.player.container.windowId));
                }
            } catch (Exception var2) {
            }

            this.swapState = SwapState.RETURNING;
            this.actionTimer.reset();
            this.resetState();
        }

    }

    @EventHandler
    void onUpdate(UpdateEvent var1) {
        if (this.swapState == SwapState.IDLE && !this.isSwappingItem && this.actionType == ActionType.IDLE) {
            MovementManager.getInstance().unlockMovement("HWHelper");
        } else if (this.megabypass.getValue()) {
            MovementManager.getInstance().lockMovement("HWHelper");
        }
    }

    void resetState() {
        this.actionPending = false;
        this.swapState = SwapState.IDLE;
        this.swappedFromSlot = -1;
        this.currentItem = null;
        this.isSwappingItem = false;
        this.actionType = ActionType.IDLE;
        this.oldHotbarSlot = -1;
        this.targetHotbarSlot = -1;
        MovementManager.getInstance().unlockMovement("HWHelper");
    }


    public void onDisable() {
        this.resetState();
        super.onDisable();
    }


    enum SwapState {
        IDLE,
        SWAPPING,
        SWAPPED,
        USING,
        RETURNING
    }

    record HudEntry(String title, Item item, BindSetting bind) {
    }
}