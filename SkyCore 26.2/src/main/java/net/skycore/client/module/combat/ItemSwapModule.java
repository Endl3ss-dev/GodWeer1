package net.skycore.client.module.combat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.skycore.client.module.ClientModule;
import net.skycore.client.module.KeyBinds;
import net.skycore.client.module.ModuleCategory;
import net.skycore.client.setting.BooleanSetting;
import net.skycore.client.setting.KeyBindSetting;
import net.skycore.client.setting.ModeSetting;
import net.skycore.client.setting.NumberSetting;
import net.skycore.client.command.ChatMessages;

/**
 * ItemSwap — быстрый свап предмета в левую руку по бинду.
 *
 * <p>Портировано из {@code Swerve/modules/impl/combat/ItemSwap.java}. Логика
 * источника сохранена: два выбранных предмета, режим «Цикл» (каждое нажатие берёт
 * следующий доступный) и «Удержание» (первый предмет на нажатии, второй — на
 * отпускании), задержка перед отправкой клика и поиск только по хотбару или по всему
 * инвентарю.
 *
 * <p>Адаптация под 26.2:
 * <ul>
 *   <li>свап выполняется через {@code gameMode.handleContainerInput(..., 40,
 *       ContainerInput.SWAP, ...)} — в 26.2 {@code ClickType} заменён на
 *       {@code ContainerInput}, кнопка 40 означает левую руку;</li>
 *   <li>слот ищется в {@code containerMenu.slots} по индексам 9…44, поэтому
 *       смещение «инвентарь → меню» не нужно считать вручную: {@code slot.index} уже
 *       индекс контейнера;</li>
 *   <li>экран проверяется через {@code gui.screen()} (в 26.2 поле {@code screen}
 *       убрано);</li>
 *   <li>уведомление выводится в чат через {@link ChatMessages}.</li>
 * </ul>
 */
public final class ItemSwapModule extends ClientModule {

    public static final ItemSwapModule INSTANCE = new ItemSwapModule();

    private static final int NOT_FOUND = -1;
    private static final int OFFHAND_SWAP_BUTTON = 40;
    private static final String[] SWAP_ITEMS = {"Чарка", "Тотем", "Щит", "Арбалет"};

    private final ModeSetting firstItem = this.add(new ModeSetting("Предмет 1", "Чарка", SWAP_ITEMS));
    private final ModeSetting secondItem = this.add(new ModeSetting("Предмет 2", "Тотем", SWAP_ITEMS));
    private final KeyBindSetting swapBind = this.add(new KeyBindSetting("Бинд свапа", -1));
    private final ModeSetting swapMode = this.add(
            new ModeSetting("Режим", "Цикл", "Цикл", "Удержание"));
    private final NumberSetting delay = this.add(new NumberSetting("Задержка", 50.0, 0.0, 500.0, 10.0));
    private final ModeSetting searchMode = this.add(
            new ModeSetting("Поиск", "Весь инвентарь", "Хотбар", "Весь инвентарь"));
    private final BooleanSetting skipIfEquipped = this.add(new BooleanSetting("Пропускать если в руке", true));
    private final BooleanSetting noSwapWhileUsing = this.add(new BooleanSetting("Не свапать при использовании", true));
    private final BooleanSetting notify = this.add(new BooleanSetting("Уведомление", false));

    private int cycleIndex;
    private boolean bindDown;
    private String pendingItem;
    private long pendingAt;

    private ItemSwapModule() {
        super("Item Swap", "Свап предметов в левую руку по бинду", ModuleCategory.COMBAT);
    }

    @Override
    protected void onEnable() {
        this.cycleIndex = 0;
        this.bindDown = false;
        this.pendingItem = null;
        this.pendingAt = 0L;
    }

    @Override
    protected void onDisable() {
        this.bindDown = false;
        this.pendingItem = null;
        this.pendingAt = 0L;
    }

    @Override
    protected void onTick() {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null || minecraft.level == null || minecraft.gameMode == null) {
            this.bindDown = false;
            this.pendingItem = null;
            return;
        }

        long now = System.currentTimeMillis();
        if (this.pendingItem != null && now >= this.pendingAt) {
            String item = this.pendingItem;
            this.pendingItem = null;
            this.swapNow(minecraft, player, item);
        }

        if (minecraft.gui.screen() != null) {
            this.bindDown = false;
            return;
        }

        boolean pressed = this.isBindHeld(minecraft);
        if (pressed == this.bindDown) {
            return;
        }
        this.bindDown = pressed;

        if (this.swapMode.is("Удержание")) {
            this.queueSwap(pressed ? this.firstItem.get() : this.secondItem.get());
            return;
        }
        if (pressed) {
            this.cycleSwap();
        }
    }

    private boolean isBindHeld(Minecraft minecraft) {
        Integer key = this.swapBind.get();
        if (key == null || key < 0 || minecraft.getWindow() == null) {
            return false;
        }
        return KeyBinds.isPressed(minecraft.getWindow().handle(), key);
    }

    private void cycleSwap() {
        int start = this.cycleIndex;
        for (int step = 0; step < 2; step++) {
            int index = (start + step) & 1;
            String name = index == 0 ? this.firstItem.get() : this.secondItem.get();
            if (this.queueSwap(name)) {
                this.cycleIndex = (index + 1) & 1;
                return;
            }
        }
    }

    private boolean queueSwap(String itemName) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null || minecraft.gameMode == null || itemName == null || !this.canSwap(player, itemName)) {
            return false;
        }
        int delayMilliseconds = (int) Math.round(this.delay.get());
        if (delayMilliseconds <= 0) {
            return this.swapNow(minecraft, player, itemName);
        }
        this.pendingItem = itemName;
        this.pendingAt = System.currentTimeMillis() + delayMilliseconds;
        return true;
    }

    private boolean canSwap(LocalPlayer player, String itemName) {
        if (player.containerMenu != player.inventoryMenu) {
            return false;
        }
        if (this.noSwapWhileUsing.get() && player.isUsingItem()) {
            return false;
        }
        Item item = resolveItem(itemName);
        if (item == null) {
            return false;
        }
        if (this.skipIfEquipped.get() && this.isOffhand(player, item)) {
            return false;
        }
        return this.findSlot(player, item) != NOT_FOUND;
    }

    private boolean swapNow(Minecraft minecraft, LocalPlayer player, String itemName) {
        if (player.containerMenu != player.inventoryMenu || minecraft.gameMode == null) {
            return false;
        }
        if (this.noSwapWhileUsing.get() && player.isUsingItem()) {
            return false;
        }
        Item item = resolveItem(itemName);
        if (item == null) {
            return false;
        }
        if (this.isOffhand(player, item)) {
            return true;
        }

        int slot = this.findSlot(player, item);
        if (slot == NOT_FOUND) {
            this.sendNotification("Нет в инвентаре: " + itemName);
            return false;
        }

        minecraft.gameMode.handleContainerInput(player.containerMenu.containerId, slot,
                OFFHAND_SWAP_BUTTON, ContainerInput.SWAP, player);
        player.containerMenu.broadcastChanges();
        this.sendNotification(itemName + " → левая рука");
        return true;
    }

    /** Индекс слота контейнера (9…44) с нужным предметом. */
    private int findSlot(LocalPlayer player, Item item) {
        int min = this.searchMode.is("Хотбар") ? 36 : 9;
        for (Slot slot : player.containerMenu.slots) {
            if (slot.index < min || slot.index > 44) {
                continue;
            }
            ItemStack stack = slot.getItem();
            if (!stack.isEmpty() && stack.is(item)) {
                return slot.index;
            }
        }
        return NOT_FOUND;
    }

    private boolean isOffhand(LocalPlayer player, Item item) {
        ItemStack offhand = player.getOffhandItem();
        return !offhand.isEmpty() && offhand.is(item);
    }

    private void sendNotification(String message) {
        if (this.notify.get()) {
            ChatMessages.displayInfo("ItemSwap: " + message);
        }
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
}
