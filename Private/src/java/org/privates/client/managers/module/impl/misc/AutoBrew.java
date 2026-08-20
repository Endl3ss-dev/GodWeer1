package org.privates.client.managers.module.impl.misc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.BrewingStandBlock;
import net.minecraft.client.gui.screen.inventory.ChestScreen;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.BrewingStandContainer;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.AirItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.api.annotations.Category1;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.component.impl.rotation.Rotation;
import org.privates.client.managers.component.impl.rotation.RotationComponent;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.events.render.Render3DLastEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BindSetting;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.chat.ChatUtil;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.player.PlayerUtil;
import org.privates.client.utils.render.draw.RenderUtil3D;
import org.privates.client.utils.rotation.RotationUtil;
import org.privates.lib.util.time.StopWatch;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

@Category1
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "AutoBrew", category = Category.MISC, description = "АвтоВарит зелья скорости силы и т.д")
public class AutoBrew extends Module {

    public static AutoBrew getInstance() {
        return Instance.get(AutoBrew.class);
    }

    private final ModeSetting potionType = new ModeSetting(this, "Тип зелья", "Скорость", "Сила", "Огнестойкость");
    private final SliderSetting delay = new SliderSetting(this, "Задержка (мс)", 25, 25, 1000, 5);
    private final BooleanSetting autoLoot = new BooleanSetting(this, "Забирать в инвентарь", true);
    private final BooleanSetting addGunpowder = new BooleanSetting(this, "Добавлять порох", false);
    private final BooleanSetting afkFarm = new BooleanSetting(this, "Afk Farm", false);
    private final BindSetting resetProgress = new BindSetting(this, "Сбросить прогресс");

    private final StopWatch timer = new StopWatch();
    private final StopWatch brewWaitTimer = new StopWatch();
    private final Random random = new Random();
    private final List<BlockPos> brewingStands = new ArrayList<>();
    private final Set<BlockPos> processedStands = new HashSet<>();
    private BlockPos targetStand;
    private BlockPos targetChest;
    private AfkState afkState = AfkState.IDLE;
    private int currentStandIndex;
    private int collectedPotionsCount = 0;
    private AfkCyclePhase cyclePhase = AfkCyclePhase.WATER_WART;
    private static final String SAVE_FILE = "autobrew_progress.dat";

    private enum AfkState {
        IDLE, FIND_STANDS, ROTATE_TO_STAND, OPEN_STAND, FILL_STAND,
        WAIT_BREW, COLLECT_POTIONS, FIND_CHEST, OPEN_CHEST, DEPOSIT_POTIONS, CLOSE_CHEST
    }

    private enum AfkCyclePhase {WATER_WART, GOLDEN_CARROT, FERMENTED_SPIDER_EYE, REDSTONE}

    @Override
    public void onEnable() {
        super.onEnable();
        timer.reset();
        brewWaitTimer.reset();
        collectedPotionsCount = 0;
        if (afkFarm.getValue()) loadProgress();
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        if (afkFarm.getValue()) saveProgress();
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (mc.player == null || mc.world == null) return;

        if (resetProgress.getValue() > 0) {
            resetAfkState();
            deleteProgress();
            ChatUtil.addText("§a[AutoBrew] Прогресс сброшен!");
        }

        if (afkFarm.getValue()) handleAfkFarm();
        else handleRegularBrew();
    }

    @EventHandler
    public void onRender3D(Render3DLastEvent event) {
        if (!afkFarm.getValue() || brewingStands.isEmpty()) return;
        MatrixStack stack = event.getMatrix();
        for (BlockPos pos : brewingStands) {
            int color = (targetStand != null && targetStand.equals(pos)) ? Color.GREEN.getRGB() : Color.RED.getRGB();
            RenderUtil3D.drawBoundingBox(stack, new AxisAlignedBB(pos).grow(0.02), color, 2.0f, false, false);
        }
    }


    private void handleAfkFarm() {
        switch (afkState) {
            case IDLE -> {
                if (timer.finished(500)) {
                    afkState = AfkState.FIND_STANDS;
                    timer.reset();
                }
            }
            case FIND_STANDS -> {
                scanBrewingStands();
                if (!brewingStands.isEmpty()) {
                    targetStand = brewingStands.get(0);
                    afkState = AfkState.ROTATE_TO_STAND;
                }
                timer.reset();
            }
            case ROTATE_TO_STAND -> {
                if (targetStand == null) {
                    afkState = AfkState.IDLE;
                    return;
                }
                rotateToBlock(targetStand);
                if (timer.finished(200)) {
                    afkState = AfkState.OPEN_STAND;
                    timer.reset();
                }
            }
            case OPEN_STAND -> {
                if (targetStand == null) {
                    afkState = AfkState.IDLE;
                    return;
                }
                interactWithBlock(targetStand);
                if (timer.finished(300)) {
                    afkState = AfkState.FILL_STAND;
                    timer.reset();
                }
            }
            case FILL_STAND -> {
                if (mc.player.openContainer instanceof BrewingStandContainer) {
                    fillBrewingStandAfk();
                    if (allSlotsFilledAfk()) {
                        mc.player.closeScreen();
                        processedStands.add(targetStand);
                        currentStandIndex++;
                        if (currentStandIndex >= brewingStands.size()) {
                            if (cyclePhase == AfkCyclePhase.REDSTONE) {
                                afkState = AfkState.WAIT_BREW;
                                currentStandIndex = 0;
                                brewWaitTimer.reset();
                            } else {
                                cyclePhase = getNextPhase();
                                currentStandIndex = 0;
                                processedStands.clear();
                                targetStand = brewingStands.get(0);
                                afkState = AfkState.ROTATE_TO_STAND;
                            }
                        } else {
                            targetStand = brewingStands.get(currentStandIndex);
                            afkState = AfkState.ROTATE_TO_STAND;
                        }
                        timer.reset();
                        saveProgress();
                    }
                }
            }
            case WAIT_BREW -> {
                if (brewWaitTimer.finished(10000)) {
                    targetStand = brewingStands.get(currentStandIndex);
                    afkState = AfkState.COLLECT_POTIONS;
                    timer.reset();
                }
            }
            case COLLECT_POTIONS -> {
                if (targetStand == null) {
                    afkState = AfkState.IDLE;
                    return;
                }
                rotateToBlock(targetStand);
                if (timer.finished(200)) {
                    interactWithBlock(targetStand);
                }
                if (timer.finished(500) && mc.player.openContainer instanceof BrewingStandContainer) {
                    lootPotionsAfk();
                    if (allPotionsCollected()) {
                        mc.player.closeScreen();
                        collectedPotionsCount += 3;
                        currentStandIndex++;

                        if (currentStandIndex >= brewingStands.size()) {
                            ChatUtil.addText("§a[AutoBrew] Все зелья собраны, укладываем в сундук");
                            afkState = AfkState.FIND_CHEST;
                        } else {
                            targetStand = brewingStands.get(currentStandIndex);
                            afkState = AfkState.WAIT_BREW;
                            brewWaitTimer.reset();
                        }
                        timer.reset();
                        saveProgress();
                    }
                }
            }
            case FIND_CHEST -> {
                targetChest = findNearestChest();
                if (targetChest == null) {
                    ChatUtil.addText("§c[AutoBrew] Сундук не найден!");
                    resetAfkState();
                } else {
                    afkState = AfkState.OPEN_CHEST;
                }
                timer.reset();
            }
            case OPEN_CHEST -> {
                if (!(mc.currentScreen instanceof ChestScreen)) {
                    Vector3d hitVec = new Vector3d(targetChest.getX() + 0.5, targetChest.getY() + 0.5, targetChest.getZ() + 0.5);
                    BlockRayTraceResult result = new BlockRayTraceResult(hitVec, Direction.UP, targetChest, false);

                    float[] rotation = RotationUtil.calculateAngle(Vector3d.copyCentered(targetChest).add(new Vector3d(Direction.UP.toVector3f()).mul(0.5)));
                    RotationComponent.update(new Rotation(rotation[0], rotation[1]), 360, 360, 1, 10);
                    mc.playerController.rightClickBlock(mc.player, mc.world, Hand.MAIN_HAND, result);
                    mc.player.swingArm(Hand.MAIN_HAND);
                    timer.reset();
                } else {
                    afkState = AfkState.DEPOSIT_POTIONS;
                    timer.reset();
                }
            }
            case DEPOSIT_POTIONS -> {
                if (!(mc.currentScreen instanceof ChestScreen)) {
                    afkState = AfkState.FIND_CHEST;
                    timer.reset();
                    return;
                }

                if (!(mc.player.openContainer instanceof ChestContainer container)) {
                    timer.reset();
                    return;
                }

                if (timer.finished(100)) {
                    IInventory chestInv = container.getLowerChestInventory();
                    int chestSlots = chestInv.getSizeInventory();
                    int totalSlots = container.inventorySlots.size();

                    boolean hasInvisibilityPotion = false;
                    for (int i = chestSlots; i < totalSlots; i++) {
                        Slot slot = container.getSlot(i);
                        if (slot.getHasStack() && isLongInvisibilityPotion(slot.getStack())) {
                            mc.playerController.windowClick(container.windowId, slot.slotNumber, 0, ClickType.QUICK_MOVE, mc.player);
                            hasInvisibilityPotion = true;
                            timer.reset();
                            return;
                        }
                    }

                    if (!hasInvisibilityPotion) {
                        afkState = AfkState.CLOSE_CHEST;
                        timer.reset();
                    }
                }
            }
            case CLOSE_CHEST -> {
                mc.player.closeScreen();
                ChatUtil.addText("§a[AutoBrew] Зелья уложены в сундук, цикл завершён");
                resetAfkState();
                timer.reset();
            }
        }
    }

    private void handleRegularBrew() {
        if (!(mc.player.openContainer instanceof BrewingStandContainer)) return;

        if (isFuelEmpty()) {
            if (findItemInBrewingStand(Items.BLAZE_POWDER) == -1) {
                ChatUtil.addText("§c[AutoBrew] Нет огненного порошка!");
                toggle();
                return;
            }
            swapOneItem(Items.BLAZE_POWDER, 4);
        }

        for (int i = 0; i < 3; i++) {
            if (getItem(i) instanceof AirItem) {
                int waterPotionSlot = findPotionInBrewingStand("water");
                if (waterPotionSlot == -1) {
                    ChatUtil.addText("§c[AutoBrew] Нет бутылок с водой!");
                    toggle();
                    return;
                }
                if (timer.finished((long) (delay.getValue() * 3))) {
                    swapOneItem(waterPotionSlot, i);
                    timer.reset();
                }
            }
        }

        if (getItem(3) instanceof AirItem) {
            if (identicalPotionsCheck("water")) {
                if (findItemInBrewingStand(Items.NETHER_WART) == -1) {
                    ChatUtil.addText("§c[AutoBrew] Нет нароста!");
                    toggle();
                    return;
                }
                swapOneItem(Items.NETHER_WART, 3);
            }

            if (identicalPotionsCheck("awkward")) {
                switch (potionType.getValue()) {
                    case "Скорость":
                        if (findItemInBrewingStand(Items.SUGAR) == -1) {
                            ChatUtil.addText("§c[AutoBrew] Нет сахара для зелья скорости!");
                            toggle();
                            return;
                        }
                        swapOneItem(Items.SUGAR, 3);
                        break;

                    case "Сила":
                        if (findItemInBrewingStand(Items.BLAZE_POWDER) == -1) {
                            ChatUtil.addText("§c[AutoBrew] Нет огненного порошка для зелья силы!");
                            toggle();
                            return;
                        }
                        swapOneItem(Items.BLAZE_POWDER, 3);
                        break;

                    case "Огнестойкость":
                        if (findItemInBrewingStand(Items.MAGMA_CREAM) == -1) {
                            ChatUtil.addText("§c[AutoBrew] Нет слизи магмы для зелья огнестойкости!");
                            toggle();
                            return;
                        }
                        swapOneItem(Items.MAGMA_CREAM, 3);
                        break;
                }
            }

            if (identicalPotionsCheck("strength") || identicalPotionsCheck("swiftness")) {
                if (findItemInBrewingStand(Items.GLOWSTONE_DUST) == -1) {
                    ChatUtil.addText("§c[AutoBrew] Нет светокамня для усиления зелья!");
                    toggle();
                    return;
                }
                swapOneItem(Items.GLOWSTONE_DUST, 3);
            }

            if (identicalPotionsCheck("fire_resistance")) {
                if (findItemInBrewingStand(Items.REDSTONE) == -1) {
                    ChatUtil.addText("§c[AutoBrew] Нет редстоуна для увеличения длительности зелья!");
                    toggle();
                    return;
                }
                swapOneItem(Items.REDSTONE, 3);
            }

            if (identicalPotionsCheck("strong_strength") ||
                    identicalPotionsCheck("strong_swiftness") ||
                    identicalPotionsCheck("long_fire_resistance")) {

                if (addGunpowder.getValue()) {
                    if (findItemInBrewingStand(Items.GUNPOWDER) == -1) {
                        ChatUtil.addText("§c[AutoBrew] Нет пороха для создания взрывных зелий!");
                        toggle();
                        return;
                    }
                    if (identicalSplashPotionsCheck()) {
                        if (autoLoot.getValue()) {
                            lootPotions();
                        }
                    } else {
                        swapOneItem(Items.GUNPOWDER, 3);
                    }
                } else {
                    if (autoLoot.getValue()) {
                        lootPotions();
                    }
                }
            }
        }
    }

    private void interactWithBlock(BlockPos pos) {
        Vector3d hitVec = new Vector3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        mc.player.connection.sendPacket(new net.minecraft.network.play.client.CPlayerTryUseItemOnBlockPacket(Hand.MAIN_HAND, new BlockRayTraceResult(hitVec, Direction.UP, pos, false)));
    }

    private void scanBrewingStands() {
        brewingStands.clear();
        processedStands.clear();
        BlockPos playerPos = mc.player.getPosition();
        for (int x = -10; x <= 10; x++)
            for (int y = -5; y <= 5; y++)
                for (int z = -10; z <= 10; z++) {
                    BlockPos pos = playerPos.add(x, y, z);
                    if (mc.world.getBlockState(pos).getBlock() instanceof BrewingStandBlock) brewingStands.add(pos);
                }
    }

    private void fillBrewingStandAfk() {
        if (isFuelEmpty()) {
            int fuelSlot = findItemInBrewingStand(Items.BLAZE_POWDER);
            if (fuelSlot != -1 && timer.finished(delay.getValue().longValue())) {
                swapOneItem(fuelSlot, 4);
                timer.reset();
                return;
            }
        }

        for (int i = 0; i < 3; i++) {
            if (getItem(i) instanceof AirItem) {
                int waterSlot = findPotionInBrewingStand("water");
                if (waterSlot != -1 && timer.finished(delay.getValue().longValue())) {
                    swapOneItem(waterSlot, i);
                    timer.reset();
                    return;
                }
            }
        }

        if (getItem(3) instanceof AirItem) {
            if (identicalPotionsCheck("water")) {
                int wartSlot = findItemInBrewingStand(Items.NETHER_WART);
                if (wartSlot != -1 && timer.finished(delay.getValue().longValue())) {
                    swapOneItem(wartSlot, 3);
                    timer.reset();
                    return;
                }
            }

            if (identicalPotionsCheck("awkward")) {
                int carrotSlot = findItemInBrewingStand(Items.GOLDEN_CARROT);
                if (carrotSlot != -1 && timer.finished(delay.getValue().longValue())) {
                    swapOneItem(carrotSlot, 3);
                    timer.reset();
                    return;
                }
            }

            if (identicalPotionsCheck("night_vision")) {
                int eyeSlot = findItemInBrewingStand(Items.FERMENTED_SPIDER_EYE);
                if (eyeSlot != -1 && timer.finished(delay.getValue().longValue())) {
                    swapOneItem(eyeSlot, 3);
                    timer.reset();
                    return;
                }
            }

            if (identicalPotionsCheck("invisibility")) {
                int redstoneSlot = findItemInBrewingStand(Items.REDSTONE);
                if (redstoneSlot != -1 && timer.finished(delay.getValue().longValue())) {
                    swapOneItem(redstoneSlot, 3);
                    timer.reset();
                }
            }
        }
    }

    private boolean allSlotsFilledAfk() {
        for (int i = 0; i < 3; i++) {
            if (getItem(i) instanceof AirItem) return false;
        }
        return !(getItem(3) instanceof AirItem);
    }

    private void lootPotions() {
        for (int i = 0; i < 3; i++) {
            if (timer.finished((long) (delay.getValue() * 2))) {
                mc.playerController.windowClick(
                        mc.player.openContainer.windowId,
                        i,
                        0,
                        ClickType.QUICK_MOVE,
                        mc.player
                );
            }
        }
    }

    private void lootPotionsAfk() {
        for (int i = 0; i < 3; i++) {
            mc.playerController.windowClick(
                    mc.player.openContainer.windowId,
                    i,
                    0,
                    ClickType.QUICK_MOVE,
                    mc.player
            );
        }
        timer.reset();
    }

    private boolean allPotionsCollected() {
        for (int i = 0; i < 3; i++) {
            if (!mc.player.openContainer.inventorySlots.get(i).getStack().isEmpty()) return false;
        }
        return true;
    }

    private void rotateToBlock(BlockPos pos) {
        Vector3d blockCenter = new Vector3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        Vector3d direction = blockCenter.subtract(mc.player.getEyePosition(1.0F));
        mc.player.rotationYaw = (float) Math.toDegrees(Math.atan2(direction.z, direction.x)) - 90.0f;
        mc.player.rotationPitch = MathHelper.clamp((float) -Math.toDegrees(Math.atan2(direction.y, Math.sqrt(direction.x * direction.x + direction.z * direction.z))), -90.0f, 90.0f);
    }

    private AfkCyclePhase getNextPhase() {
        return switch (cyclePhase) {
            case WATER_WART -> AfkCyclePhase.GOLDEN_CARROT;
            case GOLDEN_CARROT -> AfkCyclePhase.FERMENTED_SPIDER_EYE;
            case FERMENTED_SPIDER_EYE -> AfkCyclePhase.REDSTONE;
            case REDSTONE -> AfkCyclePhase.WATER_WART;
        };
    }

    private void resetAfkState() {
        afkState = AfkState.IDLE;
        cyclePhase = AfkCyclePhase.WATER_WART;
        currentStandIndex = 0;
        collectedPotionsCount = 0;
        brewingStands.clear();
        processedStands.clear();
        targetStand = null;
        targetChest = null;
    }

    private boolean identicalSplashPotionsCheck() {
        boolean allSplash = true;
        for (int i = 0; i < 3; i++) {
            if (mc.player.openContainer.inventorySlots.get(i).getStack().getItem() != Items.SPLASH_POTION) {
                allSplash = false;
            }
        }
        return allSplash;
    }

    private boolean identicalPotionsCheck(String potionType) {
        boolean allMatch = true;
        for (int i = 0; i < 3; i++) {
            if (PotionUtils.getPotionFromItem(mc.player.openContainer.inventorySlots.get(i).getStack())
                    != Potion.getPotionTypeForName(potionType)) {
                allMatch = false;
            }
        }
        return allMatch;
    }

    private boolean isLongInvisibilityPotion(ItemStack stack) {
        if (stack.getItem() != Items.POTION && stack.getItem() != Items.SPLASH_POTION) return false;
        Potion potion = PotionUtils.getPotionFromItem(stack);
        return potion == Potion.getPotionTypeForName("long_invisibility");
    }

    private BlockPos findNearestChest() {
        return PlayerUtil.getSphere(mc.player.getPosition(), 5, 5, false, true, 0, true).stream()
                .filter(pos -> {
                    Block block = mc.world.getBlockState(pos).getBlock();
                    return block == Blocks.CHEST || block == Blocks.ENDER_CHEST;
                })
                .min((p1, p2) -> Double.compare(
                        mc.player.getDistanceSq(p1.getX(), p1.getY(), p1.getZ()),
                        mc.player.getDistanceSq(p2.getX(), p2.getY(), p2.getZ())
                ))
                .orElse(null);
    }

    public void swapOneItem(Item item, int to) {
        if (timer.finished((long) (delay.getValue() * 2))) {
            int slot = findItemInBrewingStand(item);
            if (slot != -1) {
                swapOneItem(slot, to);
                timer.reset();
            }
        }
    }

    public void swapOneItem(int from, int to) {
        mc.playerController.windowClick(mc.player.openContainer.windowId, from, 0, ClickType.PICKUP, mc.player);
        mc.playerController.windowClick(mc.player.openContainer.windowId, to, 1, ClickType.PICKUP, mc.player);
        mc.playerController.windowClick(mc.player.openContainer.windowId, from, 0, ClickType.PICKUP, mc.player);
    }

    private Item getItem(int slotId) {
        return mc.player.openContainer.inventorySlots.get(slotId).getStack().getItem();
    }

    private boolean isFuelEmpty() {
        return getItem(4) instanceof AirItem;
    }

    private int findPotionInBrewingStand(String potionType) {
        for (int i = 5; i < 41; i++) {
            if (PotionUtils.getPotionFromItem(mc.player.openContainer.inventorySlots.get(i).getStack())
                    == Potion.getPotionTypeForName(potionType)) {
                return i;
            }
        }
        return -1;
    }

    private int findItemInBrewingStand(Item item) {
        for (int i = 5; i < 41; i++) {
            if (getItem(i) == item) {
                return i;
            }
        }
        return -1;
    }

    private void saveProgress() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SAVE_FILE))) {
            oos.writeObject(afkState.name());
            oos.writeObject(cyclePhase.name());
            oos.writeInt(currentStandIndex);
            oos.writeInt(collectedPotionsCount);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadProgress() {
        File file = new File(SAVE_FILE);
        if (!file.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            afkState = AfkState.valueOf((String) ois.readObject());
            cyclePhase = AfkCyclePhase.valueOf((String) ois.readObject());
            currentStandIndex = ois.readInt();
            collectedPotionsCount = ois.readInt();
            ChatUtil.addText("§a[AutoBrew] Прогресс загружен!");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void deleteProgress() {
        new File(SAVE_FILE).delete();
    }
}