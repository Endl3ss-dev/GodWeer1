package never.lose.modules.impl.Player;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import never.lose.modules.api.Module;
import never.lose.modules.api.ModuleCategory;
import never.lose.modules.api.ModuleInfo;
import never.lose.modules.api.setting.BindSetting;
import never.lose.modules.api.setting.BooleanSetting;
import never.lose.modules.api.setting.ModeSetting;
import never.lose.modules.api.setting.SliderSetting;
import never.lose.utils.item.DrinkUtil;
import org.lwjgl.glfw.GLFW;

@ModuleInfo(name = "DrinkUse", desc = "Использует абсент, медовуху или грог", category = ModuleCategory.PLAYER)
public class DrinkUse extends Module {

    private static final int NO_SLOT = -1;
    private static final int OFFHAND_INV_SLOT = 40;
    private static final int OFFHAND_SCREEN_SLOT = 45;
    private static final int OFFHAND_SWAP_BUTTON = 40;

    public final ModeSetting drink = new ModeSetting("Напиток", "Абсент", "Абсент", "Медовуха", "Грог");
    public final ModeSetting mode = new ModeSetting("Режим", "По бинду", "По бинду", "По окончанию кд");
    public final ModeSetting action = new ModeSetting("Действие", "ПКМ", "ПКМ", "ЛКМ");

    public final BindSetting bind = new BindSetting("Бинд", -1)
            .setVisible(() -> mode.is("По бинду"));
    public final BooleanSetting anyScreen = new BooleanSetting("Работать в GUI", true)
            .setVisible(() -> mode.is("По бинду"));
    public final SliderSetting startDelay = new SliderSetting("Задержка старта (мс)", 500, 0, 5000, 100)
            .setVisible(() -> mode.is("По окончанию кд"));

    public final SliderSetting cooldown = new SliderSetting("Кулдаун (мс)", 17000, 16000, 30000, 250);

    public final BooleanSetting packet = new BooleanSetting("Пакетный", true);
    public final ModeSetting hand = new ModeSetting("Рука", "Правая", "Правая", "Левая")
            .setVisible(() -> !packet.getValue());

    public final BooleanSetting notify = new BooleanSetting("Уведомления", false);

    private enum State {
        IDLE,
        PREPARE,
        USE,
        RESTORE
    }

    private State state = State.IDLE;
    private int stateTicks;

    private int restoreScreenSlot = NO_SLOT;
    private int restoreButton = NO_SLOT;
    private Hand pendingHand = Hand.MAIN_HAND;

    private long nextUseTime;
    private boolean bindDown;

    @Override
    public void onEnable() {
        reset();
        nextUseTime = System.currentTimeMillis() + (long) startDelay.getValue();
    }

    @Override
    public void onDisable() {
        reset();
    }

    private void reset() {
        state = State.IDLE;
        stateTicks = 0;
        restoreScreenSlot = NO_SLOT;
        restoreButton = NO_SLOT;
        pendingHand = Hand.MAIN_HAND;
        bindDown = false;
    }

    @Override
    public void onTick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        ClientPlayerEntity player = mc.player;
        if (player == null || mc.interactionManager == null || mc.world == null) {
            reset();
            return;
        }

        if (state != State.IDLE) {
            processState(mc, player);
            return;
        }

        if (mode.is("По бинду")) {
            if (!consumeBindPress(mc)) {
                return;
            }
            if (!anyScreen.getValue() && mc.currentScreen != null) {
                return;
            }
        } else if (mc.currentScreen != null) {
            return;
        }

        if (System.currentTimeMillis() < nextUseTime) {
            return;
        }

        if (packet.getValue()) {
            packetUse(mc, player);
        } else {
            handUse(mc, player);
        }
    }

    private boolean consumeBindPress(MinecraftClient mc) {
        int key = bind.getValue();
        if (key == -1 || mc.getWindow() == null) {
            return false;
        }

        long window = mc.getWindow().getHandle();
        boolean down = key < 8
                ? GLFW.glfwGetMouseButton(window, key) == GLFW.GLFW_PRESS
                : InputUtil.isKeyPressed(window, key);

        if (down == bindDown) {
            return false;
        }
        bindDown = down;
        return down;
    }

    /**
     * Пакетное использование: слот подменяется и возвращается внутри одного тика,
     * между подменой и восстановлением кадр не рендерится — предмет в руке не появляется.
     */
    private void packetUse(MinecraftClient mc, ClientPlayerEntity player) {
        PlayerInventory inventory = player.getInventory();

        if (matches(player.getOffHandStack())) {
            use(mc, player, Hand.OFF_HAND);
            return;
        }

        int slot = findSlot(inventory);
        if (slot == NO_SLOT) {
            scheduleNext();
            return;
        }

        int previous = inventory.selectedSlot;

        if (slot < 9) {
            if (slot != previous) {
                inventory.selectedSlot = slot;
                use(mc, player, Hand.MAIN_HAND);
                inventory.selectedSlot = previous;
                player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(previous));
            } else {
                use(mc, player, Hand.MAIN_HAND);
            }
            return;
        }

        int screenSlot = toScreenSlot(slot);
        clickSwap(mc, player, screenSlot, previous);
        use(mc, player, Hand.MAIN_HAND);
        clickSwap(mc, player, screenSlot, previous);
    }

    private void handUse(MinecraftClient mc, ClientPlayerEntity player) {
        PlayerInventory inventory = player.getInventory();
        boolean offHand = hand.is("Левая");
        pendingHand = offHand ? Hand.OFF_HAND : Hand.MAIN_HAND;

        ItemStack held = offHand ? player.getOffHandStack() : player.getMainHandStack();
        if (matches(held)) {
            use(mc, player, pendingHand);
            return;
        }

        int slot = findSlot(inventory);
        if (slot == NO_SLOT) {
            scheduleNext();
            return;
        }

        if (!offHand && slot < 9) {
            selectSlot(player, inventory, slot);
            restoreScreenSlot = NO_SLOT;
            restoreButton = NO_SLOT;
            state = State.USE;
            stateTicks = 0;
            return;
        }

        restoreScreenSlot = toScreenSlot(slot);
        restoreButton = offHand ? OFFHAND_SWAP_BUTTON : inventory.selectedSlot;

        clickSwap(mc, player, restoreScreenSlot, restoreButton);
        state = State.PREPARE;
        stateTicks = 0;
    }

    private void processState(MinecraftClient mc, ClientPlayerEntity player) {
        stateTicks++;
        switch (state) {
            case PREPARE -> {
                if (stateTicks >= 1) {
                    state = State.USE;
                    stateTicks = 0;
                }
            }
            case USE -> {
                use(mc, player, pendingHand);
                if (restoreScreenSlot != NO_SLOT) {
                    state = State.RESTORE;
                } else {
                    state = State.IDLE;
                }
                stateTicks = 0;
            }
            case RESTORE -> {
                if (stateTicks >= 1) {
                    clickSwap(mc, player, restoreScreenSlot, restoreButton);
                    restoreScreenSlot = NO_SLOT;
                    restoreButton = NO_SLOT;
                    state = State.IDLE;
                    stateTicks = 0;
                }
            }
            default -> state = State.IDLE;
        }
    }

    private void selectSlot(ClientPlayerEntity player, PlayerInventory inventory, int slot) {
        if (inventory.selectedSlot == slot) {
            return;
        }
        inventory.selectedSlot = slot;
        // syncSelectedSlot() приватный в 1.21.1, отправляем тот же пакет вручную
        player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(slot));
    }

    private void clickSwap(MinecraftClient mc, ClientPlayerEntity player, int screenSlot, int button) {
        if (screenSlot == NO_SLOT || button == NO_SLOT || mc.interactionManager == null) {
            return;
        }
        int syncId = player.currentScreenHandler.syncId;
        mc.interactionManager.clickSlot(syncId, screenSlot, button, SlotActionType.SWAP, player);
    }

    private void use(MinecraftClient mc, ClientPlayerEntity player, Hand usedHand) {
        if (action.is("Ударить (ЛКМ)")) {
            player.swingHand(usedHand);
        } else {
            ActionResult result = mc.interactionManager.interactItem(player, usedHand);
            if (result.shouldSwingHand()) {
                player.swingHand(usedHand);
            }
        }
        scheduleNext();

        if (notify.getValue() && mc.inGameHud != null) {
            mc.inGameHud.getChatHud().addMessage(Text.of("§8[§fDrinkUse§8] §7Использован: §f" + drink.getValue()));
        }
    }

    private void scheduleNext() {
        nextUseTime = System.currentTimeMillis() + (long) cooldown.getValue();
    }

    private boolean matches(ItemStack stack) {
        if (!DrinkUtil.isHead(stack)) {
            return false;
        }
        DrinkUtil.Drink found = DrinkUtil.getDrink(stack);
        return found != null && found.getDisplayName().equals(drink.getValue());
    }

    private int findSlot(PlayerInventory inventory) {
        for (int i = 0; i < 36; i++) {
            if (matches(inventory.getStack(i))) {
                return i;
            }
        }
        return NO_SLOT;
    }

    private int toScreenSlot(int inventorySlot) {
        if (inventorySlot == OFFHAND_INV_SLOT) {
            return OFFHAND_SCREEN_SLOT;
        }
        return inventorySlot < 9 ? 36 + inventorySlot : inventorySlot;
    }
}