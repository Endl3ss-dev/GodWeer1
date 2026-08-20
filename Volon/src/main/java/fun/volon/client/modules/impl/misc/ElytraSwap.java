package fun.volon.client.modules.impl.misc;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import fun.volon.api.events.EventLink;
import fun.volon.api.events.implement.EventBinding;
import fun.volon.api.events.implement.EventMoveInput;
import fun.volon.api.events.implement.EventUpdate;
import fun.volon.api.utils.chat.ChatUtils;
import fun.volon.api.utils.player.InventoryUtils;
import fun.volon.client.modules.Module;
import fun.volon.client.modules.impl.movement.Sprint;
import fun.volon.client.modules.settings.implement.BindSetting;
import fun.volon.client.modules.settings.implement.BooleanSetting;
import fun.volon.client.modules.settings.implement.FloatSetting;

import java.util.Set;

public class ElytraSwap extends Module {

    public static ElytraSwap INSTANCE = new ElytraSwap();

    private final BindSetting elytraBind = new BindSetting("Бинд элитры", -1);
    private final BindSetting fireworkBind = new BindSetting("Бинд фейерверка", -1);
    private final BooleanSetting autofly = new BooleanSetting("Авто-взлёт", true);
    private final BooleanSetting legitMode = new BooleanSetting("Легит режим", true).visible(autofly::isState);
    private final BooleanSetting noPhantom = new BooleanSetting("Не фантомить", true);
    private final FloatSetting toSlot = new FloatSetting("В слот", 9f, 1f, 9f, 1f)
            .visible(noPhantom::isState);
    private final BooleanSetting bypassgrim = new BooleanSetting("Обходить Grim", true);
    private final BooleanSetting bypassGround = new BooleanSetting("Обходить Граунд", true);
    private final BooleanSetting rightClickFirework = new BooleanSetting("ПКМ фейерверк", true);

    private boolean swapElytraQueued;
    private boolean useFirework;
    private int bypassTicks;
    private boolean sprintPaused;
    private int swapCooldown;
    private int fireworkReturnSlot = -1;
    private int fireworkReturnTicks = -1;
    private boolean packetSwapActive;
    private int packetSwapStage;
    private int packetSwapSlot;
    private int autoflyJumpTicks = 0;
    private boolean autoflyJumped = false;
    private boolean rightClickPressed = false; // Для отслеживания ПКМ

    public ElytraSwap() {
        super("ElytraSwap", "Автоматический свап элитр", ModuleCategory.MISC);
        addSettings(elytraBind, fireworkBind, autofly, legitMode, noPhantom, toSlot, bypassgrim, bypassGround, rightClickFirework);
    }

    @EventLink
    public void onInput(final EventMoveInput e) {
        if (bypassgrim.isState() && bypassTicks > 0) {
            if (mc.player == null) return;
            mc.player.setSprinting(false);
            e.setForward(0);
            e.setStrafe(0);
            e.setJump(false);
            e.setSneak(false);
        }
    }

    @EventLink
    public void onEvent(final EventUpdate event) {
        if (mc.player == null) return;

        // Обработка ПКМ через проверку нажатия кнопки мыши
        if (rightClickFirework.isState() && mc.player.isGliding()) {
            // В Minecraft кнопка 2 - это ПКМ
            if (mc.options.useKey.isPressed()) {
                if (!rightClickPressed) {
                    rightClickPressed = true;
                    useFirework = true;
                    ChatUtils.sendMessage(Formatting.GRAY + "[ElytraSwap] " + Formatting.GREEN + "Использую фейерверк!");
                }
            } else {
                rightClickPressed = false;
            }
        } else {
            rightClickPressed = false;
        }

        if (swapCooldown > 0) swapCooldown--;
        handleFireworkReturn();
        handlePacketSwap();

        if (bypassTicks > 0) {
            mc.player.setSprinting(false);
            bypassTicks--;
            if (bypassTicks == 1) performSwap();
            if (bypassTicks == 0) restoreSprint();
            return;
        }

        if (swapElytraQueued) {
            if (swapCooldown > 0) { swapElytraQueued = false; return; }
            if (bypassgrim.isState()) { disableSprint(); bypassTicks = 3; swapCooldown = 1; }
            else { performSwap(); swapCooldown = 1; }
            swapElytraQueued = false;
        }

        if (useFirework) {
            if (mc.player.isGliding()) {
                if (noPhantom.isState()) {
                    useFireworkFromHotbar();
                } else {
                    int slotFirework = InventoryUtils.getItemSlot(Items.FIREWORK_ROCKET);
                    if (slotFirework != -1) {
                        if (bypassGround.isState()) {
                            executePacketFireworkSwap(slotFirework);
                        } else {
                            InventoryUtils.swapAndUseHvH(Items.FIREWORK_ROCKET);
                        }
                    } else {
                        ChatUtils.sendMessage(Formatting.RED + "" + Formatting.BOLD + "Нет Фейерверков!");
                    }
                }
            }
            useFirework = false;
        }

        if (autofly.isState() && bypassTicks == 0) {
            ItemStack chestStack = mc.player.getEquippedStack(EquipmentSlot.CHEST);

            if (legitMode.isState()) {
                if (autoflyJumpTicks > 0) {
                    autoflyJumpTicks--;
                }

                if (chestStack.isOf(Items.ELYTRA) && !mc.player.isTouchingWater() && !mc.player.isInLava()
                        && mc.player.isOnGround() && !mc.options.jumpKey.isPressed() && !autoflyJumped) {
                    mc.player.jump();
                    autoflyJumped = true;
                    autoflyJumpTicks = 4;
                }
                else if (chestStack.isOf(Items.ELYTRA) && isElytraUsable(chestStack)
                        && !mc.player.isGliding() && !mc.player.isOnGround()) {

                    boolean canActivate = autoflyJumpTicks <= 1 ||
                            (autoflyJumpTicks <= 3 && mc.player.getVelocity().y < 0);

                    if (canActivate) {
                        mc.player.startGliding();
                        mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
                        autoflyJumped = false;
                        autoflyJumpTicks = 0;
                    }
                }
                else if (mc.player.isOnGround()) {
                    autoflyJumped = false;
                    autoflyJumpTicks = 0;
                }
            }
            else {
                if (chestStack.isOf(Items.ELYTRA) && !mc.player.isTouchingWater() && !mc.player.isInLava()
                        && mc.player.isOnGround() && !mc.options.jumpKey.isPressed()) {
                    mc.player.jump();
                } else if (chestStack.isOf(Items.ELYTRA) && isElytraUsable(chestStack)
                        && !mc.player.isGliding() && !mc.player.isOnGround()) {
                    mc.player.startGliding();
                    mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
                }
            }
        }
    }

    private void handlePacketSwap() {
        if (!packetSwapActive || mc.player == null) return;

        if (packetSwapStage == 0) {
            int currentSlot = mc.player.getInventory().selectedSlot;
            int nextSlot = (currentSlot + 1) % 9;
            mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(nextSlot));
            packetSwapStage = 1;
        } else if (packetSwapStage == 1) {
            mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(packetSwapSlot));
            packetSwapActive = false;
            packetSwapStage = 0;
        }
    }

    private void executePacketFireworkSwap(int fireworkSlot) {
        int currentSlot = mc.player.getInventory().selectedSlot;
        packetSwapSlot = currentSlot;

        if (fireworkSlot < 9) {
            mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(fireworkSlot));
            mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
            mc.player.swingHand(Hand.MAIN_HAND);
            mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(currentSlot));
        } else {
            int targetSlot = fireworkSlot >= 36 ? fireworkSlot - 36 : fireworkSlot;
            mc.interactionManager.clickSlot(0, fireworkSlot, 0, SlotActionType.SWAP, mc.player);
            mc.interactionManager.clickSlot(0, 36 + currentSlot, 0, SlotActionType.SWAP, mc.player);
            mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
            mc.player.swingHand(Hand.MAIN_HAND);
            mc.interactionManager.clickSlot(0, 36 + currentSlot, 0, SlotActionType.SWAP, mc.player);
            mc.interactionManager.clickSlot(0, fireworkSlot, 0, SlotActionType.SWAP, mc.player);
            mc.player.networkHandler.sendPacket(new CloseHandledScreenC2SPacket(0));
        }
        packetSwapActive = true;
        packetSwapStage = 0;
    }

    private void performSwap() {
        final int slotElytra = InventoryUtils.findBestElytraSlot();
        final int chestSlot = InventoryUtils.findBestChestplateSlot();

        boolean needChestplate = mc.player.getEquippedStack(EquipmentSlot.CHEST).isOf(Items.ELYTRA)
                || mc.player.getEquippedStack(EquipmentSlot.CHEST).isEmpty()
                || !Set.of(Items.NETHERITE_CHESTPLATE, Items.DIAMOND_CHESTPLATE, Items.IRON_CHESTPLATE,
                        Items.GOLDEN_CHESTPLATE, Items.CHAINMAIL_CHESTPLATE, Items.LEATHER_CHESTPLATE)
                .contains(mc.player.getEquippedStack(EquipmentSlot.CHEST).getItem());

        if (needChestplate) {
            if (chestSlot == -1) {
                ChatUtils.sendMessage(Formatting.RED + "" + Formatting.BOLD + "Нет нагрудника!");
                bypassTicks = 0;
                restoreSprint();
                return;
            }
            doSwap(resolveSwapSlot(chestSlot));
        } else {
            if (slotElytra == -1) {
                ChatUtils.sendMessage(Formatting.RED + "" + Formatting.BOLD + "Нет элитры!");
                bypassTicks = 0;
                restoreSprint();
                return;
            }
            doSwap(resolveSwapSlot(slotElytra));
        }
        mc.player.networkHandler.sendPacket(new CloseHandledScreenC2SPacket(0));
    }

    private int resolveSwapSlot(int inventorySlot) {
        if (!noPhantom.isState() || inventorySlot < 0) {
            return inventorySlot;
        }
        return moveToTargetHotbar(inventorySlot);
    }

    private int moveToTargetHotbar(int inventorySlot) {
        int target = getTargetHotbarSlot();
        if (inventorySlot >= 0 && inventorySlot < 9) {
            if (inventorySlot != target) {
                mc.interactionManager.clickSlot(0, 36 + inventorySlot, target, SlotActionType.SWAP, mc.player);
            }
            return target;
        }
        if (inventorySlot >= 9 && inventorySlot < 36) {
            mc.interactionManager.clickSlot(0, inventorySlot, target, SlotActionType.SWAP, mc.player);
            return target;
        }
        return inventorySlot;
    }

    private int getTargetHotbarSlot() {
        return MathHelper.clamp((int) toSlot.get() - 1, 0, 8);
    }

    private void doSwap(int slot) {
        if (noPhantom.isState() && (slot < 0 || slot >= 9)) {
            return;
        }
        if (slot >= 0 && slot < 9) {
            mc.interactionManager.clickSlot(0, 6, slot, SlotActionType.SWAP, mc.player);
        } else {
            mc.interactionManager.clickSlot(0, slot, 0, SlotActionType.SWAP, mc.player);
            mc.interactionManager.clickSlot(0, 6, 0, SlotActionType.SWAP, mc.player);
            mc.interactionManager.clickSlot(0, slot, 0, SlotActionType.SWAP, mc.player);
        }
    }

    private void useFireworkFromHotbar() {
        int hotbarSlot = InventoryUtils.find(Items.FIREWORK_ROCKET, 0, 8);
        if (hotbarSlot == -1) {
            int inventorySlot = InventoryUtils.find(Items.FIREWORK_ROCKET, 9, 35);
            if (inventorySlot == -1) {
                ChatUtils.sendMessage(Formatting.RED + "" + Formatting.BOLD + "Нет Фейерверков!");
                return;
            }
            hotbarSlot = moveToTargetHotbar(inventorySlot);
            mc.player.networkHandler.sendPacket(new CloseHandledScreenC2SPacket(0));
        }

        int previousSlot = mc.player.getInventory().selectedSlot;
        if (bypassGround.isState()) {
            executePacketFireworkSwap(hotbarSlot);
            return;
        }

        if (hotbarSlot != previousSlot) {
            mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(hotbarSlot));
        }
        mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
        mc.player.swingHand(Hand.MAIN_HAND);
        if (hotbarSlot != previousSlot) {
            mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(previousSlot));
        }
    }

    private void handleFireworkReturn() {
        if (noPhantom.isState()) {
            fireworkReturnSlot = -1;
            fireworkReturnTicks = -1;
            return;
        }
        if (fireworkReturnTicks < 0) return;
        if (fireworkReturnTicks > 0) {
            fireworkReturnTicks--;
            return;
        }
        if (fireworkReturnSlot != -1) {
            swapSlotToOffhand(fireworkReturnSlot);
            mc.player.networkHandler.sendPacket(new CloseHandledScreenC2SPacket(0));
        }
        fireworkReturnSlot = -1;
        fireworkReturnTicks = -1;
    }

    private int findScreenSlot(net.minecraft.item.Item item) {
        for (int slot = 9; slot < 45; slot++) {
            ItemStack stack = mc.player.playerScreenHandler.getSlot(slot).getStack();
            if (stack.isOf(item)) return slot;
        }
        return -1;
    }

    private void swapSlotToOffhand(int slot) {
        if (slot >= 36 && slot <= 44) {
            mc.interactionManager.clickSlot(0, 45, slot - 36, SlotActionType.SWAP, mc.player);
            return;
        }
        mc.interactionManager.clickSlot(0, slot, 0, SlotActionType.SWAP, mc.player);
        mc.interactionManager.clickSlot(0, 45, 0, SlotActionType.SWAP, mc.player);
        mc.interactionManager.clickSlot(0, slot, 0, SlotActionType.SWAP, mc.player);
    }

    private void disableSprint() {
        if (sprintPaused) return;
        Sprint.pushPause(1000);
        sprintPaused = true;
    }

    private void restoreSprint() {
        if (!sprintPaused) return;
        sprintPaused = false;
        Sprint.popPause();
    }

    private boolean isElytraUsable(ItemStack stack) {
        return stack.getDamage() < stack.getMaxDamage() - 1;
    }

    @EventLink
    public void onEvent(final EventBinding event) {
        if (event.getKey() == elytraBind.getKey()) swapElytraQueued = true;
        if (event.getKey() == fireworkBind.getKey()) {
            useFirework = true;
        }
    }

    @Override
    public void onDisable() {
        bypassTicks = 0;
        swapCooldown = 0;
        fireworkReturnSlot = -1;
        fireworkReturnTicks = -1;
        packetSwapActive = false;
        packetSwapStage = 0;
        autoflyJumpTicks = 0;
        autoflyJumped = false;
        rightClickPressed = false;
        restoreSprint();
        super.onDisable();
    }
}