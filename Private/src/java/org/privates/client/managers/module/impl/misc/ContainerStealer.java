package org.privates.client.managers.module.impl.misc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.ClientBossInfo;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import org.privates.client.api.annotations.Category1;
import org.privates.client.api.annotations.PVE;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.component.impl.rotation.Rotation;
import org.privates.client.managers.component.impl.rotation.RotationComponent;
import org.privates.client.managers.events.other.EntityRayTraceEvent;
import org.privates.client.managers.events.other.ScreenCloseEvent;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.impl.combat.NoEntityTrace;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.client.managers.module.settings.impl.MultiItemSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.player.PlayerUtil;
import org.privates.client.utils.rotation.RotationUtil;
import org.privates.lib.util.time.StopWatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Category1
@PVE
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "ContainerStealer", category = Category.MISC, description = "Авто забирает лут с сундуков можно выбрать людей айтем")
public class ContainerStealer extends Module {
    public static ContainerStealer getInstance() {
        return Instance.get(ContainerStealer.class);
    }

    private final ModeSetting mode = new ModeSetting(this, "Режим", "Selected Items", "FunTime", "Steal All");
    private final SliderSetting stealDelay = new SliderSetting(this, "Задержка", 100, 0, 1000, 1);
    private final BooleanSetting autoOpen = new BooleanSetting(this, "Авто-открытие", true).setVisible(() -> mode.is("FunTime"));
    private final BooleanSetting disableIfEmpty = new BooleanSetting(this, "Закрыть если пустой", true);

    private final MultiItemSetting selectedItems = new MultiItemSetting(
            this,
            "Предметы для кражи",
            new ArrayList<>()
    ).setVisible(() -> mode.is("Selected Items"));

    private final BooleanSetting alsoStealRest = new BooleanSetting(
            this,
            "Забирать остальное",
            false,
            "Если включено, будет забирать все предметы, а не только выбранные"
    ).setVisible(() -> mode.is("Selected Items"));

    private final BooleanSetting prioritizeSelected = new BooleanSetting(
            this,
            "Приоритет выбранным",
            true,
            "Сначала забирать выбранные предметы, потом остальные"
    ).setVisible(() -> mode.is("Selected Items") && alsoStealRest.getValue());

    private final StopWatch timeClick = new StopWatch();
    private final StopWatch timeOpen = new StopWatch();
    int containerEmptyTick = 0;

    @EventHandler
    public void onScreenCloseEvent(ScreenCloseEvent event) {
        containerEmptyTick = 0;
        timeOpen.reset();
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (mc.currentScreen == null) {
            if (mode.is("FunTime")) {
                timeOpen.reset();
            }
            return;
        }

        if (mode.is("FunTime") && atTheEvent()) {
            handleFunTimeMode();
        } else if (mc.player.openContainer instanceof ChestContainer) {
            if (mode.is("Steal All")) {
                handleStealAllMode();
            } else if (mode.is("Selected Items")) {
                handleSelectedItemsMode();
            }
        }
    }

    @EventHandler
    public void onEntityRayTrace(EntityRayTraceEvent event) {
        if (!NoEntityTrace.getInstance().isEnabled()) {
            event.cancel();
        }
    }

    private void handleFunTimeMode() {
        if (mc.player.openContainer instanceof ChestContainer container) {
            IInventory inventory = container.getLowerChestInventory();
            boolean isEmpty = true;
            for (int i = 0; i < inventory.getSizeInventory(); i++) {
                if (!inventory.getStackInSlot(i).isEmpty()) {
                    isEmpty = false;
                    break;
                }
            }

            if (isEmpty) {
                containerEmptyTick++;
            } else {
                containerEmptyTick = 0;
            }

            if (disableIfEmpty.getValue() && containerEmptyTick > 25) {
                mc.player.closeScreen();
                timeOpen.reset();
                return;
            }

            if (!mc.player.getCooldownTracker().hasCooldown(Items.GUNPOWDER) && timeClick.finished(200)) {
                for (int i = 0; i < inventory.getSizeInventory(); i++) {
                    if (!container.getSlot(i).getStack().isEmpty()) {
                        mc.playerController.windowClick(container.windowId, i, 0, ClickType.QUICK_MOVE, mc.player);
                        timeClick.reset();
                        break;
                    }
                }
            }
        } else if (autoOpen.getValue() && timeOpen.finished(5000)) {
            BlockPos enderChestBP = PlayerUtil.getSphere(mc.player.getPosition(), 4, 4, false, true, 0, true).stream()
                    .filter(blockPos -> mc.world.getBlockState(blockPos).getBlock() == Blocks.ENDER_CHEST)
                    .findFirst()
                    .orElse(null);

            if (enderChestBP != null) {
                float[] rotation = RotationUtil.calculateAngle(Vector3d.copyCentered(enderChestBP).add(new Vector3d(Direction.UP.toVector3f()).mul(0.5)));
                RotationComponent.update(new Rotation(rotation[0], rotation[1]), 360, 360, 1, 10);
                mc.playerController.rightClickBlock(mc.player, mc.world, Hand.MAIN_HAND, new BlockRayTraceResult(Vector3d.copy(enderChestBP), Direction.UP, enderChestBP, false));
                mc.player.swingArm(Hand.MAIN_HAND);
            }
        }
    }

    private void handleStealAllMode() {
        if (!(mc.player.openContainer instanceof ChestContainer container)) return;

        if (timeClick.finished(stealDelay.getValue().longValue())) {
            for (int i = 0; i < container.getLowerChestInventory().getSizeInventory(); i++) {
                if (!container.getSlot(i).getStack().isEmpty()) {
                    mc.playerController.windowClick(container.windowId, i, 0, ClickType.QUICK_MOVE, mc.player);
                    timeClick.reset();
                    return;
                }
            }
            if (disableIfEmpty.getValue()) {
                mc.player.closeScreen();
            }
        }
    }

    private void handleSelectedItemsMode() {
        if (!(mc.player.openContainer instanceof ChestContainer container)) {
            return;
        }

        IInventory inventory = container.getLowerChestInventory();
        List<Slot> prioritySlots = new ArrayList<>();
        List<Slot> otherSlots = new ArrayList<>();

        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            Slot slot = container.getSlot(i);
            if (slot.getStack().isEmpty()) continue;

            Item item = slot.getStack().getItem();

            if (selectedItems.getValue().contains(item)) {
                prioritySlots.add(slot);
            } else if (alsoStealRest.getValue()) {
                otherSlots.add(slot);
            }
        }

        List<Slot> slotsToProcess = new ArrayList<>();
        if (prioritizeSelected.getValue() && alsoStealRest.getValue()) {
            slotsToProcess.addAll(prioritySlots);
            slotsToProcess.addAll(otherSlots);
        } else {
            slotsToProcess.addAll(prioritySlots);
            slotsToProcess.addAll(otherSlots);
        }

        if (slotsToProcess.isEmpty()) {
            if (disableIfEmpty.getValue()) {
                mc.player.closeScreen();
            }
            return;
        }

        if (timeClick.finished(stealDelay.getValue().longValue())) {
            Slot slotToSteal = slotsToProcess.get(0);
            mc.playerController.windowClick(
                    container.windowId,
                    slotToSteal.slotNumber,
                    0,
                    ClickType.QUICK_MOVE,
                    mc.player
            );
            timeClick.reset();
        }
    }

    public boolean atTheEvent() {
        for (Map.Entry<UUID, ClientBossInfo> bossInfo : mc.ingameGUI.getBossOverlay().getMapBossInfos().entrySet()) {
            String bossBarName = bossInfo.getValue().getName().getString().toLowerCase();
            if (bossBarName.contains("активации") || bossBarName.contains("закрыт") || bossBarName.contains("открыт")) {
                return true;
            }
        }
        return false;
    }
}