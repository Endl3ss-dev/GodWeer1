package net.skycore.client.module.player;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.skycore.client.module.ClientModule;
import net.skycore.client.module.KeyBinds;
import net.skycore.client.module.ModuleCategory;
import net.skycore.client.setting.BooleanSetting;
import net.skycore.client.setting.KeyBindSetting;
import net.skycore.client.setting.ModeSetting;
import net.skycore.client.setting.NumberSetting;
import net.skycore.client.command.ChatMessages;
import net.skycore.client.util.DrinkItems;
import net.skycore.client.util.InventoryHelper;

/**
 * DrinkUse — использование голов-напитков (абсент, медовуха, грог).
 *
 * <p>Портировано из {@code Swerve/modules/impl/player/DrinkUse.java}. Поведение
 * источника сохранено: напиток ищется в инвентаре по текстуре головы
 * ({@link DrinkItems}), затем берётся в руку и используется, а предмет возвращается
 * на место.
 *
 * <p>Переписано под 26.2 и под правила SkyCore:
 * <ul>
 *   <li>клик по слоту — {@code gameMode.handleContainerInput(..., ContainerInput.SWAP, ...)},
 *       как в {@code InventoryHelper}: в 26.2 {@code ClickType} заменён на
 *       {@code ContainerInput};</li>
 *   <li>смена слота — {@code InventoryHelper.selectHotbarSlot}, а не прямое
 *       присваивание {@code inventory.selected} (в 26.2 поле убрано в пользу
 *       {@code getSelectedSlot()/setSelectedSlot()});</li>
 *   <li>уведомления идут через {@link ChatMessages}, а не {@code gui.getChat()};</li>
 *   <li>бинды читаются штатным {@link KeyBinds} по хендлу окна.</li>
 * </ul>
 *
 * <p>Отличие от источника: режим «Пакетный» здесь означает «один клик без
 * промежуточных тиков», а «Рука» — работу через выбранную руку с паузой в тик
 * между свапом и использованием. Так оба режима используют один и тот же код
 * возврата предмета, и предмет не остаётся в левой руке при выключении модуля.
 */
public final class DrinkUseModule extends ClientModule {

    private static final int NO_SLOT = -1;
    private static final int OFFHAND_SWAP_BUTTON = 40;

    public static final DrinkUseModule INSTANCE = new DrinkUseModule();
    private final ModeSetting drink = this.add(
            new ModeSetting("Напиток", "Абсент", "Абсент", "Медовуха", "Грог"));
    private final ModeSetting mode = this.add(
            new ModeSetting("Режим", "По бинду", "По бинду", "По окончанию кд"));
    private final ModeSetting action = this.add(
            new ModeSetting("Действие", "ПКМ", "ПКМ", "ЛКМ"));
    private final KeyBindSetting bind = this.add(new KeyBindSetting("Бинд", NO_SLOT));
    private final BooleanSetting anyScreen = this.add(new BooleanSetting("Работать в GUI", true));
    private final NumberSetting startDelay = this.add(
            new NumberSetting("Задержка старта", 500.0, 0.0, 5000.0, 100.0));
    private final NumberSetting cooldown = this.add(
            new NumberSetting("Кулдаун", 17000.0, 16000.0, 30000.0, 250.0));
    private final BooleanSetting packet = this.add(new BooleanSetting("Пакетный", true));
    private final ModeSetting hand = this.add(new ModeSetting("Рука", "Правая", "Правая", "Левая"));
    private final BooleanSetting notify = this.add(new BooleanSetting("Уведомления", false));

    private State state = State.IDLE;
    private int stateTicks;
    private int restoreSlot = NO_SLOT;
    private InteractionHand pendingHand = InteractionHand.MAIN_HAND;
    private long nextUseTime;
    private boolean bindDown;

    private DrinkUseModule() {
        super("Drink Use", "Пьёт абсент, медовуху или грог с голов", ModuleCategory.PLAYER);
        this.hand.visibleWhen(() -> !this.packet.get());
        this.bind.visibleWhen(() -> this.mode.is("По бинду"));
        this.anyScreen.visibleWhen(() -> this.mode.is("По бинду"));
        this.startDelay.visibleWhen(() -> this.mode.is("По окончанию кд"));
    }

    /** Выбранный напиток из режима настройки. */
    public DrinkItems.Drink selectedDrink() {
        return switch (this.drink.get()) {
            case "Медовуха" -> DrinkItems.Drink.MEAD;
            case "Грог" -> DrinkItems.Drink.GROG;
            default -> DrinkItems.Drink.ABSINTHE;
        };
    }

    @Override
    protected void onEnable() {
        this.state = State.IDLE;
        this.stateTicks = 0;
        this.restoreSlot = NO_SLOT;
        this.bindDown = false;
        this.nextUseTime = System.currentTimeMillis() + Math.round(this.startDelay.get());
    }

    @Override
    protected void onDisable() {
        restore(this.currentPlayer());
        this.state = State.IDLE;
        this.stateTicks = 0;
        this.restoreSlot = NO_SLOT;
        this.bindDown = false;
    }

    @Override
    protected void onTick() {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null || minecraft.gameMode == null || minecraft.level == null) {
            this.state = State.IDLE;
            this.restoreSlot = NO_SLOT;
            return;
        }

        // предмет уже используется (едой, щитом, другим модулем) — не мешаем
        if (player.isUsingItem()) {
            restore(player);
            this.state = State.IDLE;
            return;
        }

        if (this.state != State.IDLE) {
            this.processState(minecraft, player);
            return;
        }

        if (this.mode.is("По бинду")) {
            boolean down = isBindHeld(minecraft);
            if (down == this.bindDown) {
                return;
            }
            this.bindDown = down;
            if (!down) {
                return;
            }
            if (!this.anyScreen.get() && minecraft.gui.screen() != null) {
                return;
            }
        } else if (minecraft.gui.screen() != null) {
            return;
        }

        if (System.currentTimeMillis() < this.nextUseTime) {
            return;
        }
        if (InventoryHelper.isServerScreen()) {
            return;
        }

        if (this.packet.get()) {
            this.useImmediate(minecraft, player);
        } else {
            this.useSequenced(minecraft, player);
        }
    }

    // ---- режим «Пакетный»

    private void useImmediate(Minecraft minecraft, LocalPlayer player) {
        if (DrinkItems.matches(player.getOffhandItem(), this.selectedDrink())) {
            this.use(minecraft, player, InteractionHand.OFF_HAND);
            return;
        }

        int hotbar = this.findHotbarSlot(player);
        if (hotbar != NO_SLOT) {
            // предмет в хотбаре: берём слот, используем и возвращаем прежний —
            // как в источнике, иначе после каждого напитка меняется активный слот
            int previous = player.getInventory().getSelectedSlot();
            InventoryHelper.selectHotbarSlot(hotbar);
            this.use(minecraft, player, InteractionHand.MAIN_HAND);
            if (previous != hotbar) {
                InventoryHelper.selectHotbarSlot(previous);
            }
            return;
        }

        Slot slot = this.findInventorySlot(player);
        if (slot == null) {
            this.scheduleNext();
            return;
        }
        int index = slot.index;
        // два свапа подряд: предмет уходит в левую руку и сразу возвращается,
        // поэтому restoreSlot здесь намеренно не выставляется
        this.swapWithOffhand(minecraft, player, index);
        this.use(minecraft, player, InteractionHand.OFF_HAND);
        this.swapWithOffhand(minecraft, player, index);
    }

    // ---- режим «Рука» (с паузами между пакетами)

    private void useSequenced(Minecraft minecraft, LocalPlayer player) {
        boolean offhand = this.hand.is("Левая");
        if (!offhand) {
            int hotbar = this.findHotbarSlot(player);
            if (hotbar != NO_SLOT) {
                InventoryHelper.selectHotbarSlot(hotbar);
                this.pendingHand = InteractionHand.MAIN_HAND;
                this.state = State.USE;
                this.stateTicks = 0;
                return;
            }
            if (DrinkItems.matches(player.getMainHandItem(), this.selectedDrink())) {
                this.pendingHand = InteractionHand.MAIN_HAND;
                this.use(minecraft, player, InteractionHand.MAIN_HAND);
                return;
            }
        } else if (DrinkItems.matches(player.getOffhandItem(), this.selectedDrink())) {
            this.use(minecraft, player, InteractionHand.OFF_HAND);
            return;
        }

        Slot slot = this.findInventorySlot(player);
        if (slot == null) {
            this.scheduleNext();
            return;
        }
        this.pendingHand = offhand ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
        this.restoreSlot = slot.index;
        this.swapWithOffhand(minecraft, player, slot.index);
        this.state = State.USE;
        this.stateTicks = 0;
    }

    private void processState(Minecraft minecraft, LocalPlayer player) {
        this.stateTicks++;
        switch (this.state) {
            case USE -> {
                if (this.stateTicks < 2) {
                    return;
                }
                this.use(minecraft, player, this.pendingHand);
                this.state = this.restoreSlot == NO_SLOT ? State.IDLE : State.RESTORE;
                this.stateTicks = 0;
            }
            case RESTORE -> {
                if (this.stateTicks < 2) {
                    return;
                }
                this.restore(player);
                this.state = State.IDLE;
                this.stateTicks = 0;
            }
            default -> this.state = State.IDLE;
        }
    }

    // ---- действия

    private void use(Minecraft minecraft, LocalPlayer player, InteractionHand usedHand) {
        if (this.action.is("ЛКМ")) {
            player.swing(usedHand);
        } else if (minecraft.gameMode != null) {
            minecraft.gameMode.useItem(player, usedHand);
            player.swing(usedHand);
        }
        this.scheduleNext();
        if (this.notify.get()) {
            ChatMessages.displayInfo("DrinkUse: использован " + this.drink.get());
        }
    }

    /**
     * Меняет местами слот инвентаря и левую руку.
     *
     * <p>Клик идёт по {@code ContainerInput.SWAP} с кнопкой 40 — так же, как это
     * делает {@code InventoryHelper.swapHand} для левой руки.
     */
    private void swapWithOffhand(Minecraft minecraft, LocalPlayer player, int slotIndex) {
        if (minecraft.gameMode == null || slotIndex < 0) {
            return;
        }
        minecraft.gameMode.handleContainerInput(player.containerMenu.containerId, slotIndex,
                OFFHAND_SWAP_BUTTON, ContainerInput.SWAP, player);
        player.containerMenu.broadcastChanges();
    }

    /** Возвращает предмет из левой руки на место, если мы его туда перекладывали. */
    private void restore(LocalPlayer player) {
        if (this.restoreSlot == NO_SLOT || player == null) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        int slotIndex = this.restoreSlot;
        this.restoreSlot = NO_SLOT;
        this.state = State.IDLE;
        if (minecraft.gameMode == null) {
            return;
        }
        if (player.containerMenu != player.inventoryMenu) {
            // открыт чужой контейнер — свап в левую руку сейчас небезопасен
            return;
        }
        minecraft.gameMode.handleContainerInput(player.containerMenu.containerId, slotIndex,
                OFFHAND_SWAP_BUTTON, ContainerInput.SWAP, player);
        player.containerMenu.broadcastChanges();
    }

    private void scheduleNext() {
        this.nextUseTime = System.currentTimeMillis() + Math.round(this.cooldown.get());
    }

    private boolean isBindHeld(Minecraft minecraft) {
        Integer key = this.bind.get();
        if (key == null || key < 0 || minecraft.getWindow() == null) {
            return false;
        }
        return KeyBinds.isPressed(minecraft.getWindow().handle(), key);
    }

    private LocalPlayer currentPlayer() {
        return Minecraft.getInstance().player;
    }

    private int findHotbarSlot(LocalPlayer player) {
        for (int i = 0; i < 9; i++) {
            if (DrinkItems.matches(player.getInventory().getItem(i), this.selectedDrink())) {
                return i;
            }
        }
        return NO_SLOT;
    }

    /** Слот основного инвентаря (9…35) с нужной головой. */
    private Slot findInventorySlot(LocalPlayer player) {
        for (Slot slot : player.containerMenu.slots) {
            if (slot.index >= 9 && slot.index <= 35
                    && DrinkItems.matches(slot.getItem(), this.selectedDrink())) {
                return slot;
            }
        }
        return null;
    }

    private enum State {
        IDLE,
        USE,
        RESTORE
    }
}
