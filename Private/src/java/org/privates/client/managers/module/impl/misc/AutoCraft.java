package org.privates.client.managers.module.impl.misc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.gui.screen.inventory.CraftingScreen;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.privates.client.api.annotations.Category1;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.chat.ChatUtil;
import org.privates.client.utils.other.Instance;
import org.privates.lib.util.time.StopWatch;

import java.util.ArrayList;
import java.util.List;

@Category1
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "AutoCraft", category = Category.MISC, description = "Крафт залуп")
public class AutoCraft extends Module {

    public static AutoCraft getInstance() {
        return Instance.get(AutoCraft.class);
    }

    private final SliderSetting delay = new SliderSetting(this, "Задержка (мс)", 200, 100, 1000, 50);
    private final StopWatch timer = new StopWatch();

    private CraftState state = CraftState.OPEN_CRAFT;
    private final List<Integer> diamondSlots = new ArrayList<>();
    private final List<Integer> nuggetSlots = new ArrayList<>();
    private final List<Integer> ingotSlots = new ArrayList<>();
    private int currentDiamondIndex = 0;
    private int currentNuggetIndex = 0;
    private int currentIngotIndex = 0;

    @Override
    public void onEnable() {
        super.onEnable();
        state = CraftState.OPEN_CRAFT;
        diamondSlots.clear();
        nuggetSlots.clear();
        ingotSlots.clear();
        currentDiamondIndex = 0;
        currentNuggetIndex = 0;
        currentIngotIndex = 0;
        timer.reset();
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (mc.player == null || mc.world == null) return;

        if (!timer.finished(delay.getValue().longValue())) return;

        switch (state) {
            case OPEN_CRAFT:
                if (!(mc.currentScreen instanceof CraftingScreen)) {
                    mc.player.sendChatMessage("/craft");
                    timer.reset();
                } else {
                    findItems();
                    if (!ingotSlots.isEmpty()) {
                        currentIngotIndex = 0;
                        state = CraftState.CRAFT_INGOT_TO_NUGGETS;
                        timer.reset();
                    } else if (diamondSlots.isEmpty() || nuggetSlots.isEmpty()) {
                        ChatUtil.addText("§c[AutoCraft] Не найдены алмазы или золотые самородки!");
                        toggle();
                        return;
                    } else {
                        currentDiamondIndex = 0;
                        currentNuggetIndex = 0;
                        state = CraftState.PLACE_DIAMOND_STACK;
                        timer.reset();
                    }
                }
                break;

            case CRAFT_INGOT_TO_NUGGETS:
                if (mc.currentScreen instanceof CraftingScreen) {
                    if (currentIngotIndex < ingotSlots.size()) {
                        int slot = ingotSlots.get(currentIngotIndex);
                        ItemStack stack = mc.player.openContainer.getSlot(slot).getStack();

                        if (!stack.isEmpty() && stack.getCount() > 0) {
                            mc.playerController.windowClick(
                                    mc.player.openContainer.windowId,
                                    slot,
                                    0,
                                    ClickType.PICKUP,
                                    mc.player
                            );
                            mc.playerController.windowClick(
                                    mc.player.openContainer.windowId,
                                    5,
                                    0,
                                    ClickType.PICKUP,
                                    mc.player
                            );
                            state = CraftState.TAKE_NUGGETS_RESULT;
                            timer.reset();
                        } else {
                            currentIngotIndex++;
                            timer.reset();
                        }
                    } else {
                        findItems();
                        if (diamondSlots.isEmpty() || nuggetSlots.isEmpty()) {
                            ChatUtil.addText("§c[AutoCraft] Не найдены алмазы или золотые самородки!");
                            toggle();
                            return;
                        }
                        currentDiamondIndex = 0;
                        currentNuggetIndex = 0;
                        state = CraftState.PLACE_DIAMOND_STACK;
                        timer.reset();
                    }
                }
                break;

            case TAKE_NUGGETS_RESULT:
                if (mc.currentScreen instanceof CraftingScreen screen) {
                    Slot resultSlot = screen.getContainer().getSlot(0);
                    if (!resultSlot.getStack().isEmpty()) {
                        mc.playerController.windowClick(
                                mc.player.openContainer.windowId,
                                0,
                                0,
                                ClickType.QUICK_MOVE,
                                mc.player
                        );
                        state = CraftState.CLEAR_INGOT_GRID;
                        timer.reset();
                    }
                }
                break;

            case CLEAR_INGOT_GRID:
                if (mc.currentScreen instanceof CraftingScreen screen) {
                    boolean hasItems = false;
                    for (int i = 1; i <= 9; i++) {
                        Slot slot = screen.getContainer().getSlot(i);
                        if (!slot.getStack().isEmpty()) {
                            mc.playerController.windowClick(
                                    mc.player.openContainer.windowId,
                                    i,
                                    0,
                                    ClickType.QUICK_MOVE,
                                    mc.player
                            );
                            hasItems = true;
                            break;
                        }
                    }
                    if (!hasItems) {
                        currentIngotIndex++;
                        state = CraftState.CRAFT_INGOT_TO_NUGGETS;
                    }
                    timer.reset();
                }
                break;

            case PLACE_DIAMOND_STACK:
                if (mc.currentScreen instanceof CraftingScreen) {
                    if (currentDiamondIndex < diamondSlots.size()) {
                        int slot = diamondSlots.get(currentDiamondIndex);
                        ItemStack stack = mc.player.openContainer.getSlot(slot).getStack();

                        if (!stack.isEmpty() && stack.getCount() > 0) {
                            mc.playerController.windowClick(
                                    mc.player.openContainer.windowId,
                                    slot,
                                    0,
                                    ClickType.PICKUP,
                                    mc.player
                            );
                            mc.playerController.windowClick(
                                    mc.player.openContainer.windowId,
                                    5,
                                    0,
                                    ClickType.PICKUP,
                                    mc.player
                            );
                            state = CraftState.PLACE_NUGGET_STACK_1;
                            timer.reset();
                        } else {
                            currentDiamondIndex++;
                            timer.reset();
                        }
                    } else {
                        ChatUtil.addText("§a[AutoCraft] Крафт завершён! Алмазы закончились.");
                        toggle();
                    }
                }
                break;

            case PLACE_NUGGET_STACK_1:
                if (mc.currentScreen instanceof CraftingScreen) {
                    if (currentNuggetIndex < nuggetSlots.size()) {
                        int slot = nuggetSlots.get(currentNuggetIndex);
                        ItemStack stack = mc.player.openContainer.getSlot(slot).getStack();

                        if (!stack.isEmpty() && stack.getCount() > 0) {
                            mc.playerController.windowClick(
                                    mc.player.openContainer.windowId,
                                    slot,
                                    0,
                                    ClickType.PICKUP,
                                    mc.player
                            );
                            mc.playerController.windowClick(
                                    mc.player.openContainer.windowId,
                                    2,
                                    0,
                                    ClickType.PICKUP,
                                    mc.player
                            );
                            state = CraftState.PLACE_NUGGET_STACK_2;
                            timer.reset();
                        } else {
                            currentNuggetIndex++;
                            timer.reset();
                        }
                    } else {
                        ChatUtil.addText("§a[AutoCraft] Крафт завершён! Самородки закончились.");
                        toggle();
                    }
                }
                break;

            case PLACE_NUGGET_STACK_2:
                if (mc.currentScreen instanceof CraftingScreen) {
                    int nextIndex = currentNuggetIndex + 1;
                    if (nextIndex < nuggetSlots.size()) {
                        int slot = nuggetSlots.get(nextIndex);
                        ItemStack stack = mc.player.openContainer.getSlot(slot).getStack();

                        if (!stack.isEmpty() && stack.getCount() > 0) {
                            mc.playerController.windowClick(
                                    mc.player.openContainer.windowId,
                                    slot,
                                    0,
                                    ClickType.PICKUP,
                                    mc.player
                            );
                            mc.playerController.windowClick(
                                    mc.player.openContainer.windowId,
                                    4,
                                    0,
                                    ClickType.PICKUP,
                                    mc.player
                            );
                            state = CraftState.PLACE_NUGGET_STACK_3;
                            timer.reset();
                        } else {
                            currentNuggetIndex = nextIndex;
                            state = CraftState.PLACE_NUGGET_STACK_2;
                            timer.reset();
                        }
                    } else {
                        ChatUtil.addText("§a[AutoCraft] Крафт завершён! Самородки закончились.");
                        toggle();
                    }
                }
                break;

            case PLACE_NUGGET_STACK_3:
                if (mc.currentScreen instanceof CraftingScreen) {
                    int nextIndex = currentNuggetIndex + 2;
                    if (nextIndex < nuggetSlots.size()) {
                        int slot = nuggetSlots.get(nextIndex);
                        ItemStack stack = mc.player.openContainer.getSlot(slot).getStack();

                        if (!stack.isEmpty() && stack.getCount() > 0) {
                            mc.playerController.windowClick(
                                    mc.player.openContainer.windowId,
                                    slot,
                                    0,
                                    ClickType.PICKUP,
                                    mc.player
                            );
                            mc.playerController.windowClick(
                                    mc.player.openContainer.windowId,
                                    6,
                                    0,
                                    ClickType.PICKUP,
                                    mc.player
                            );
                            state = CraftState.PLACE_NUGGET_STACK_4;
                            timer.reset();
                        } else {
                            currentNuggetIndex = nextIndex;
                            state = CraftState.PLACE_NUGGET_STACK_3;
                            timer.reset();
                        }
                    } else {
                        ChatUtil.addText("§a[AutoCraft] Крафт завершён! Самородки закончились.");
                        toggle();
                    }
                }
                break;

            case PLACE_NUGGET_STACK_4:
                if (mc.currentScreen instanceof CraftingScreen) {
                    int nextIndex = currentNuggetIndex + 3;
                    if (nextIndex < nuggetSlots.size()) {
                        int slot = nuggetSlots.get(nextIndex);
                        ItemStack stack = mc.player.openContainer.getSlot(slot).getStack();

                        if (!stack.isEmpty() && stack.getCount() > 0) {
                            mc.playerController.windowClick(
                                    mc.player.openContainer.windowId,
                                    slot,
                                    0,
                                    ClickType.PICKUP,
                                    mc.player
                            );
                            mc.playerController.windowClick(
                                    mc.player.openContainer.windowId,
                                    8,
                                    0,
                                    ClickType.PICKUP,
                                    mc.player
                            );
                            state = CraftState.TAKE_RESULT;
                            currentNuggetIndex += 4;
                            timer.reset();
                        } else {
                            currentNuggetIndex = nextIndex;
                            state = CraftState.PLACE_NUGGET_STACK_4;
                            timer.reset();
                        }
                    } else {
                        ChatUtil.addText("§a[AutoCraft] Крафт завершён! Самородки закончились.");
                        toggle();
                    }
                }
                break;

            case TAKE_RESULT:
                if (mc.currentScreen instanceof CraftingScreen screen) {
                    Slot resultSlot = screen.getContainer().getSlot(0);
                    if (!resultSlot.getStack().isEmpty()) {
                        mc.playerController.windowClick(
                                mc.player.openContainer.windowId,
                                0,
                                0,
                                ClickType.QUICK_MOVE,
                                mc.player
                        );
                        state = CraftState.CLEAR_GRID;
                        timer.reset();
                    }
                }
                break;

            case CLEAR_GRID:
                if (mc.currentScreen instanceof CraftingScreen screen) {
                    boolean hasItems = false;
                    for (int i = 1; i <= 9; i++) {
                        Slot slot = screen.getContainer().getSlot(i);
                        if (!slot.getStack().isEmpty()) {
                            mc.playerController.windowClick(
                                    mc.player.openContainer.windowId,
                                    i,
                                    0,
                                    ClickType.QUICK_MOVE,
                                    mc.player
                            );
                            hasItems = true;
                            break;
                        }
                    }
                    if (!hasItems) {
                        state = CraftState.CHECK_CONTINUE;
                    }
                    timer.reset();
                }
                break;

            case CHECK_CONTINUE:
                findItems();
                if (currentDiamondIndex < diamondSlots.size() && currentNuggetIndex + 3 < nuggetSlots.size()) {
                    state = CraftState.PLACE_DIAMOND_STACK;
                    timer.reset();
                } else {
                    ChatUtil.addText("§a[AutoCraft] Крафт завершён! Недостаточно ресурсов.");
                    toggle();
                }
                break;
        }
    }

    private void findItems() {
        diamondSlots.clear();
        nuggetSlots.clear();
        ingotSlots.clear();

        if (!(mc.currentScreen instanceof CraftingScreen screen)) return;

        for (Slot slot : screen.getContainer().inventorySlots) {
            if (slot.slotNumber < 10) continue;

            ItemStack stack = slot.getStack();
            if (stack.isEmpty()) continue;

            if (stack.getItem() == Items.DIAMOND) {
                diamondSlots.add(slot.slotNumber);
            }
            if (stack.getItem() == Items.GOLD_NUGGET) {
                nuggetSlots.add(slot.slotNumber);
            }
            if (stack.getItem() == Items.GOLD_INGOT) {
                ingotSlots.add(slot.slotNumber);
            }
        }
    }

    private enum CraftState {
        OPEN_CRAFT,
        CRAFT_INGOT_TO_NUGGETS,
        TAKE_NUGGETS_RESULT,
        CLEAR_INGOT_GRID,
        PLACE_DIAMOND_STACK,
        PLACE_NUGGET_STACK_1,
        PLACE_NUGGET_STACK_2,
        PLACE_NUGGET_STACK_3,
        PLACE_NUGGET_STACK_4,
        TAKE_RESULT,
        CLEAR_GRID,
        CHECK_CONTINUE
    }
}