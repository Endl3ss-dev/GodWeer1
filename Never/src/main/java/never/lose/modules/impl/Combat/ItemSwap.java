package never.lose.modules.impl.Combat;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import never.lose.modules.api.Module;
import never.lose.modules.api.ModuleCategory;
import never.lose.modules.api.ModuleInfo;
import never.lose.modules.api.setting.BindSetting;
import never.lose.modules.api.setting.BooleanSetting;
import never.lose.modules.api.setting.ModeSetting;
import never.lose.modules.api.setting.SliderSetting;
import org.lwjgl.glfw.GLFW;

@ModuleInfo(name = "ItemSwap", desc = "Циклический свап предметов в левую руку", category = ModuleCategory.COMBAT)
public class ItemSwap extends Module {

    private static final String[] SWAP_ITEMS = {"Чарка", "Тотем", "Щит", "Арбалет"};

    public final ModeSetting firstItem = new ModeSetting("Предмет 1", "Чарка", SWAP_ITEMS);
    public final ModeSetting secondItem = new ModeSetting("Предмет 2", "Тотем", SWAP_ITEMS);
    public final BindSetting swapBind = new BindSetting("Бинд свапа", -1);
    public final ModeSetting swapMode = new ModeSetting("Режим", "Цикл", "Цикл", "Удержание");
    public final SliderSetting delay = new SliderSetting("Задержка", 50, 0, 500, 10);
    public final ModeSetting searchMode = new ModeSetting("Поиск", "Весь инвентарь", "Хотбар", "Весь инвентарь");
    public final BooleanSetting skipIfEquipped = new BooleanSetting("Пропускать если в руке", true);
    public final BooleanSetting noSwapWhileUsing = new BooleanSetting("Не свапать при использовании", true);
    public final BooleanSetting notify = new BooleanSetting("Уведомление", false);

    private int cycleIndex;
    private boolean bindDown;
    private String pendingItem;
    private long pendingAt;

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
        MinecraftClient mc = MinecraftClient.getInstance();
        ClientPlayerEntity player = mc.player;
        if (player == null || mc.world == null || mc.interactionManager == null) {
            bindDown = false;
            pendingItem = null;
            return;
        }

        if (pendingItem != null && System.currentTimeMillis() >= pendingAt) {
            String item = pendingItem;
            pendingItem = null;
            swapNow(mc, player, item);
        }

        if (mc.currentScreen != null) {
            bindDown = false;
            return;
        }

        boolean pressed = isBindPressed(mc);
        if (pressed == bindDown) {
            return;
        }

        boolean rising = pressed && !bindDown;
        boolean falling = !pressed && bindDown;
        bindDown = pressed;

        if (swapMode.is("Удержание")) {
            if (rising) {
                queueSwap(firstItem.getValue());
            } else if (falling) {
                queueSwap(secondItem.getValue());
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
            String name = index == 0 ? firstItem.getValue() : secondItem.getValue();
            if (queueSwap(name)) {
                cycleIndex = (index + 1) & 1;
                return;
            }
        }
    }

    private boolean queueSwap(String itemName) {
        MinecraftClient mc = MinecraftClient.getInstance();
        ClientPlayerEntity player = mc.player;
        if (player == null || mc.interactionManager == null) {
            return false;
        }
        if (!canSwap(player, itemName)) {
            return false;
        }

        int delayMs = delay.getIntValue();
        if (delayMs <= 0) {
            return swapNow(mc, player, itemName);
        }

        pendingItem = itemName;
        pendingAt = System.currentTimeMillis() + delayMs;
        return true;
    }

    private boolean canSwap(ClientPlayerEntity player, String itemName) {
        if (player.currentScreenHandler != player.playerScreenHandler) {
            return false;
        }
        if (noSwapWhileUsing.getValue() && player.isUsingItem()) {
            return false;
        }

        Item item = resolveItem(itemName);
        if (item == null) {
            return false;
        }
        if (skipIfEquipped.getValue() && isOffhand(player, item)) {
            return false;
        }
        return findItemSlot(player, item) != -1;
    }

    private boolean swapNow(MinecraftClient mc, ClientPlayerEntity player, String itemName) {
        if (player.currentScreenHandler != player.playerScreenHandler || mc.interactionManager == null) {
            notify(mc, "§cНельзя свапнуть " + itemName);
            return false;
        }
        if (noSwapWhileUsing.getValue() && player.isUsingItem()) {
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
        if (slot == -1) {
            notify(mc, "§cНет в инвентаре: " + itemName);
            return false;
        }

        mc.interactionManager.clickSlot(
                player.playerScreenHandler.syncId,
                toHandlerSlot(slot),
                PlayerInventory.OFF_HAND_SLOT,
                SlotActionType.SWAP,
                player
        );
        notify(mc, "§f" + itemName + " §7→ левая рука");
        return true;
    }

    private int findItemSlot(ClientPlayerEntity player, Item item) {
        PlayerInventory inventory = player.getInventory();
        int end = searchMode.is("Хотбар") ? 9 : 36;
        for (int i = 0; i < end; i++) {
            ItemStack stack = inventory.getStack(i);
            if (!stack.isEmpty() && stack.isOf(item)) {
                return i;
            }
        }
        return -1;
    }

    private boolean isOffhand(ClientPlayerEntity player, Item item) {
        ItemStack offhand = player.getOffHandStack();
        return !offhand.isEmpty() && offhand.isOf(item);
    }

    private boolean isBindPressed(MinecraftClient mc) {
        int bind = swapBind.getValue();
        if (bind == -1) {
            return false;
        }
        long handle = mc.getWindow().getHandle();
        if (bind >= 0 && bind <= 7) {
            return GLFW.glfwGetMouseButton(handle, bind) == GLFW.GLFW_PRESS;
        }
        return GLFW.glfwGetKey(handle, bind) == GLFW.GLFW_PRESS;
    }

    // PlayerScreenHandler: hotbar 0-8 -> 36-44, main inventory 9-35 -> 9-35
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

    private void notify(MinecraftClient mc, String message) {
        if (!notify.getValue() || mc.inGameHud == null) {
            return;
        }
        mc.inGameHud.setOverlayMessage(Text.literal("§7ItemSwap §8» " + message), false);
    }
}
