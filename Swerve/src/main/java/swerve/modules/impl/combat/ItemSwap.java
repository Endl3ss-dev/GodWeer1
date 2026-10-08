package swerve.modules.impl.combat;

import swerve.modules.Module;
import swerve.modules.ModuleCategory;
import swerve.modules.api.setting.BindSetting;
import swerve.modules.api.setting.BooleanSetting;
import swerve.modules.api.setting.FloatSetting;
import swerve.modules.api.setting.ModeSetting;
import swerve.utils.input.KeyBoardUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class ItemSwap extends Module {

    private static final int NOT_FOUND = -1;
    private static final int OFFHAND_SWAP_BUTTON = 40;
    private static final String FIRST_ITEM = "Чарка";
    private static final String SECOND_ITEM = "Тотем";
    private static final String[] SWAP_ITEMS = {"Чарка", "Тотем", "Щит", "Арбалет"};

    private final ModeSetting firstItem = new ModeSetting("Предмет 1", FIRST_ITEM, SWAP_ITEMS);
    private final ModeSetting secondItem = new ModeSetting("Предмет 2", SECOND_ITEM, SWAP_ITEMS);
    private final BindSetting swapBind = new BindSetting("Бинд свапа", -1);
    private final ModeSetting swapMode = new ModeSetting("Режим", "Цикл", "Цикл", "Удержание");
    private final FloatSetting delay = new FloatSetting("Задержка", 50.0F, 0.0F, 500.0F, 10.0F);
    private final ModeSetting searchMode = new ModeSetting("Поиск", "Весь инвентарь", "Хотбар", "Весь инвентарь");
    private final BooleanSetting skipIfEquipped = new BooleanSetting("Пропускать если в руке", true);
    private final BooleanSetting noSwapWhileUsing = new BooleanSetting("Не свапать при использовании", true);
    private final BooleanSetting notify = new BooleanSetting("Уведомление", false);

    private int cycleIndex;
    private boolean bindDown;
    private String pendingItem;
    private long pendingAt;

    public ItemSwap() {
        super("ItemSwap", "Циклический свап предметов в левую руку", ModuleCategory.COMBAT);
        addSettings(firstItem, secondItem, swapBind, swapMode, delay, searchMode,
                skipIfEquipped, noSwapWhileUsing, notify);
    }

    @Override
    public void onEnable() {
        cycleIndex = 0;
        bindDown = false;
        pendingItem = null;
        pendingAt = 0L;
    }

    @Override
    public void onDisable() {
        bindDown = false;
        pendingItem = null;
        pendingAt = 0L;
    }

    @Override
    public void onTick() {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null || minecraft.level == null || minecraft.gameMode == null) {
            bindDown = false;
            pendingItem = null;
            return;
        }

        long now = System.currentTimeMillis();
        if (pendingItem != null && now >= pendingAt) {
            String item = pendingItem;
            pendingItem = null;
            swapNow(minecraft, player, item);
        }

        if (minecraft.screen != null) {
            bindDown = false;
            return;
        }

        boolean pressed = KeyBoardUtils.isBindHeld(swapBind.getKey());
        if (pressed == bindDown) {
            return;
        }

        boolean rising = pressed;
        boolean falling = !pressed;
        bindDown = pressed;

        if (swapMode.is("Удержание")) {
            if (rising) {
                queueSwap(firstItem.getCurrent());
            } else if (falling) {
                queueSwap(secondItem.getCurrent());
            }
            return;
        }

        if (rising) {
            cycleSwap();
        }
    }

    private void cycleSwap() {
        int start = cycleIndex;
        for (int step = 0; step < 2; step++) {
            int index = (start + step) & 1;
            String name = index == 0 ? firstItem.getCurrent() : secondItem.getCurrent();
            if (queueSwap(name)) {
                cycleIndex = (index + 1) & 1;
                return;
            }
        }
    }

    private boolean queueSwap(String itemName) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null || minecraft.gameMode == null || !canSwap(player, itemName)) {
            return false;
        }

        int delayMilliseconds = Math.round(delay.get());
        if (delayMilliseconds <= 0) {
            return swapNow(minecraft, player, itemName);
        }

        pendingItem = itemName;
        pendingAt = System.currentTimeMillis() + delayMilliseconds;
        return true;
    }

    private boolean canSwap(LocalPlayer player, String itemName) {
        if (player.containerMenu != player.inventoryMenu) {
            return false;
        }
        if (noSwapWhileUsing.get() && player.isUsingItem()) {
            return false;
        }

        Item item = resolveItem(itemName);
        if (item == null) {
            return false;
        }
        if (skipIfEquipped.get() && isOffhand(player, item)) {
            return false;
        }
        return findItemSlot(player, item) != NOT_FOUND;
    }

    private boolean swapNow(Minecraft minecraft, LocalPlayer player, String itemName) {
        if (player.containerMenu != player.inventoryMenu || minecraft.gameMode == null) {
            sendNotification(minecraft, "§cНельзя свапнуть " + itemName);
            return false;
        }
        if (noSwapWhileUsing.get() && player.isUsingItem()) {
            return false;
        }

        Item item = resolveItem(itemName);
        if (item == null) {
            return false;
        }
        if (isOffhand(player, item)) {
            return true;
        }

        int slot = findItemSlot(player, item);
        if (slot == NOT_FOUND) {
            sendNotification(minecraft, "§cНет в инвентаре: " + itemName);
            return false;
        }

        minecraft.gameMode.handleInventoryMouseClick(
                player.inventoryMenu.containerId,
                toHandlerSlot(slot),
                OFFHAND_SWAP_BUTTON,
                ClickType.SWAP,
                player
        );
        sendNotification(minecraft, "§f" + itemName + " §7→ левая рука");
        return true;
    }

    private int findItemSlot(LocalPlayer player, Item item) {
        Inventory inventory = player.getInventory();
        int end = searchMode.is("Хотбар") ? 9 : 36;
        for (int i = 0; i < end; i++) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty() && stack.is(item)) {
                return i;
            }
        }
        return NOT_FOUND;
    }

    private boolean isOffhand(LocalPlayer player, Item item) {
        ItemStack offhand = player.getOffhandItem();
        return !offhand.isEmpty() && offhand.is(item);
    }

    private static int toHandlerSlot(int inventorySlot) {
        return inventorySlot < 9 ? inventorySlot + 36 : inventorySlot;
    }

    private static Item resolveItem(String name) {
        return switch (name) {
            case "Чарка" -> Items.ENCHANTED_GOLDEN_APPLE;
            case "Тотем" -> Items.TOTEM_OF_UNDYING;
            case "Щит" -> Items.SHIELD;
            case "Арбалет" -> Items.CROSSBOW;
            default -> null;
        };
    }

    private void sendNotification(Minecraft minecraft, String message) {
        if (notify.get()) {
            minecraft.gui.setOverlayMessage(Component.literal("§7ItemSwap §8» " + message), false);
        }
    }
}
