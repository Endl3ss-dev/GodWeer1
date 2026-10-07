package swerve.modules.impl.player;

import swerve.modules.Module;
import swerve.modules.ModuleCategory;
import swerve.modules.api.setting.BindSetting;
import swerve.modules.api.setting.BooleanSetting;
import swerve.modules.api.setting.FloatSetting;
import swerve.modules.api.setting.ModeSetting;
import swerve.utils.input.KeyBoardUtils;
import swerve.utils.item.DrinkUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;

public final class DrinkUse extends Module {

    private static final int NO_SLOT = -1;
    private static final int OFFHAND_INVENTORY_SLOT = 40;
    private static final int OFFHAND_MENU_SLOT = 45;
    private static final int OFFHAND_SWAP_BUTTON = 40;

    private final ModeSetting drink = new ModeSetting("Напиток", "Абсент", "Абсент", "Медовуха", "Грог");
    private final ModeSetting mode = new ModeSetting("Режим", "По бинду", "По бинду", "По окончанию кд");
    private final ModeSetting action = new ModeSetting("Действие", "ПКМ", "ПКМ", "ЛКМ");
    private final BindSetting bind = new BindSetting("Бинд", -1).visible(() -> mode.is("По бинду"));
    private final BooleanSetting anyScreen = new BooleanSetting("Работать в GUI", true)
            .visible(() -> mode.is("По бинду"));
    private final FloatSetting startDelay = new FloatSetting(
            "Задержка старта (мс)", 500.0F, 0.0F, 5000.0F, 100.0F
    ).visible(() -> mode.is("По окончанию кд"));
    private final FloatSetting cooldown = new FloatSetting(
            "Кулдаун (мс)", 17000.0F, 16000.0F, 30000.0F, 250.0F
    );
    private final BooleanSetting packet = new BooleanSetting("Пакетный", true);
    private final ModeSetting hand = new ModeSetting("Рука", "Правая", "Правая", "Левая")
            .visible(() -> !packet.get());
    private final BooleanSetting notify = new BooleanSetting("Уведомления", false);

    private State state = State.IDLE;
    private int stateTicks;
    private int restoreMenuSlot = NO_SLOT;
    private int restoreButton = NO_SLOT;
    private InteractionHand pendingHand = InteractionHand.MAIN_HAND;
    private long nextUseTime;
    private boolean bindDown;

    public DrinkUse() {
        super("DrinkUse", "Использует абсент, медовуху или грог", ModuleCategory.PLAYER);
        addSettings(drink, mode, action, bind, anyScreen, startDelay, cooldown, packet, hand, notify);
    }

    @Override
    public void onEnable() {
        resetState();
        nextUseTime = System.currentTimeMillis() + (long) startDelay.get();
    }

    @Override
    public void onDisable() {
        resetState();
    }

    @Override
    public void onTick() {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null || minecraft.gameMode == null || minecraft.level == null) {
            resetState();
            return;
        }

        if (state != State.IDLE) {
            processState(minecraft, player);
            return;
        }

        if (mode.is("По бинду")) {
            if (!consumeBindPress()) {
                return;
            }
            if (!anyScreen.get() && minecraft.screen != null) {
                return;
            }
        } else if (minecraft.screen != null) {
            return;
        }

        if (System.currentTimeMillis() < nextUseTime) {
            return;
        }

        if (packet.get()) {
            usePacketMode(minecraft, player);
        } else {
            useHandMode(minecraft, player);
        }
    }

    private boolean consumeBindPress() {
        boolean down = KeyBoardUtils.isBindHeld(bind.getKey());
        if (down == bindDown) {
            return false;
        }
        bindDown = down;
        return down;
    }

    private void usePacketMode(Minecraft minecraft, LocalPlayer player) {
        Inventory inventory = player.getInventory();

        if (matches(player.getOffhandItem())) {
            use(minecraft, player, InteractionHand.OFF_HAND);
            return;
        }

        int slot = findSlot(inventory);
        if (slot == NO_SLOT) {
            scheduleNextUse();
            return;
        }

        int previousSlot = inventory.selected;
        if (slot < 9) {
            if (slot != previousSlot) {
                selectSlot(player, inventory, slot);
                use(minecraft, player, InteractionHand.MAIN_HAND);
                selectSlot(player, inventory, previousSlot);
            } else {
                use(minecraft, player, InteractionHand.MAIN_HAND);
            }
            return;
        }

        int menuSlot = toMenuSlot(slot);
        clickSwap(minecraft, player, menuSlot, previousSlot);
        use(minecraft, player, InteractionHand.MAIN_HAND);
        clickSwap(minecraft, player, menuSlot, previousSlot);
    }

    private void useHandMode(Minecraft minecraft, LocalPlayer player) {
        Inventory inventory = player.getInventory();
        boolean useOffhand = hand.is("Левая");
        pendingHand = useOffhand ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;

        ItemStack heldItem = useOffhand ? player.getOffhandItem() : player.getMainHandItem();
        if (matches(heldItem)) {
            use(minecraft, player, pendingHand);
            return;
        }

        int slot = findSlot(inventory);
        if (slot == NO_SLOT) {
            scheduleNextUse();
            return;
        }

        if (!useOffhand && slot < 9) {
            selectSlot(player, inventory, slot);
            restoreMenuSlot = NO_SLOT;
            restoreButton = NO_SLOT;
            state = State.USE;
            stateTicks = 0;
            return;
        }

        restoreMenuSlot = toMenuSlot(slot);
        restoreButton = useOffhand ? OFFHAND_SWAP_BUTTON : inventory.selected;
        clickSwap(minecraft, player, restoreMenuSlot, restoreButton);
        state = State.PREPARE;
        stateTicks = 0;
    }

    private void processState(Minecraft minecraft, LocalPlayer player) {
        stateTicks++;
        switch (state) {
            case PREPARE -> {
                if (stateTicks >= 1) {
                    state = State.USE;
                    stateTicks = 0;
                }
            }
            case USE -> {
                use(minecraft, player, pendingHand);
                state = restoreMenuSlot == NO_SLOT ? State.IDLE : State.RESTORE;
                stateTicks = 0;
            }
            case RESTORE -> {
                if (stateTicks >= 1) {
                    clickSwap(minecraft, player, restoreMenuSlot, restoreButton);
                    restoreMenuSlot = NO_SLOT;
                    restoreButton = NO_SLOT;
                    state = State.IDLE;
                    stateTicks = 0;
                }
            }
            default -> state = State.IDLE;
        }
    }

    private void selectSlot(LocalPlayer player, Inventory inventory, int slot) {
        if (inventory.selected == slot) {
            return;
        }
        inventory.selected = slot;
        player.connection.send(new ServerboundSetCarriedItemPacket(slot));
    }

    private void clickSwap(Minecraft minecraft, LocalPlayer player, int menuSlot, int button) {
        if (menuSlot == NO_SLOT || button == NO_SLOT || minecraft.gameMode == null) {
            return;
        }
        minecraft.gameMode.handleInventoryMouseClick(
                player.containerMenu.containerId,
                menuSlot,
                button,
                ClickType.SWAP,
                player
        );
    }

    private void use(Minecraft minecraft, LocalPlayer player, InteractionHand usedHand) {
        if (action.is("ЛКМ")) {
            player.swing(usedHand);
        } else {
            minecraft.gameMode.useItem(player, usedHand);
            player.swing(usedHand);
        }

        scheduleNextUse();
        if (notify.get()) {
            minecraft.gui.getChat().addMessage(Component.literal(
                    "§8[§fDrinkUse§8] §7Использован: §f" + drink.getCurrent()
            ));
        }
    }

    private void scheduleNextUse() {
        nextUseTime = System.currentTimeMillis() + (long) cooldown.get();
    }

    private boolean matches(ItemStack stack) {
        if (!DrinkUtil.isHead(stack)) {
            return false;
        }
        DrinkUtil.Drink foundDrink = DrinkUtil.getDrink(stack);
        return foundDrink != null && foundDrink.getDisplayName().equals(drink.getCurrent());
    }

    private int findSlot(Inventory inventory) {
        for (int i = 0; i < 36; i++) {
            if (matches(inventory.getItem(i))) {
                return i;
            }
        }
        return NO_SLOT;
    }

    private int toMenuSlot(int inventorySlot) {
        if (inventorySlot == OFFHAND_INVENTORY_SLOT) {
            return OFFHAND_MENU_SLOT;
        }
        return inventorySlot < 9 ? inventorySlot + 36 : inventorySlot;
    }

    private void resetState() {
        state = State.IDLE;
        stateTicks = 0;
        restoreMenuSlot = NO_SLOT;
        restoreButton = NO_SLOT;
        pendingHand = InteractionHand.MAIN_HAND;
        bindDown = false;
    }

    private enum State {
        IDLE,
        PREPARE,
        USE,
        RESTORE
    }
}
