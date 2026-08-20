package org.privates.client.managers.module.impl.misc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import org.privates.client.api.annotations.Category1;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.chat.ChatUtil;
import org.privates.client.utils.other.GCDUtils;
import org.privates.client.utils.other.Instance;
import org.privates.lib.util.time.StopWatch;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Category1
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "AppleFarm", category = Category.MISC, description = "Auto plant, bonemeal and chop trees")
public class AppleFarm extends Module {

    public static AppleFarm getInstance() {
        return Instance.get(AppleFarm.class);
    }

    private static final String LEAVES_TYPES = "oak_leaves spruce_leaves birch_leaves jungle_leaves acacia_leaves dark_oak_leaves";
    private static final String LOG_TYPES = "oak_log spruce_log birch_log jungle_log acacia_log dark_oak_log";

    private final ModeSetting breakMode = new ModeSetting(this, "Режим рубки", "На месте", "Baritone");
    private final BooleanSetting autoStop = new BooleanSetting(this, "Авто-стоп", true).setVisible(() -> breakMode.is("Baritone"));

    private final SliderSetting actionDelay = new SliderSetting(this, "Задержка действий", 80F, 0F, 700F, 10F);
    private final SliderSetting commandDelay = new SliderSetting(this, "Задержка команд", 600F, 0F, 2500F, 50F).setVisible(() -> breakMode.is("Baritone"));
    private final SliderSetting breakTickDelay = new SliderSetting(this, "Задержка ломания", 70F, 0F, 500F, 5F);
    private final SliderSetting toolSwitchDelay = new SliderSetting(this, "Задержка смены инструмента", 80F, 0F, 1000F, 5F);

    private final SliderSetting scanRadius = new SliderSetting(this, "Радиус сканирования", 6F, 1F, 12F, 1F);
    private final SliderSetting scanHeight = new SliderSetting(this, "Высота сканирования", 20F, 1F, 48F, 1F);
    private final SliderSetting maxFarmDistance = new SliderSetting(this, "Макс. дистанция", 5F, 1F, 15F, 0.5F);
    private final SliderSetting reachDistance = new SliderSetting(this, "Дистанция ломания", 4.8F, 1F, 6F, 0.1F);

    private final SliderSetting maxYawSpeed = new SliderSetting(this, "Макс скорость Yaw", 180F, 50F, 360F, 5F);
    private final SliderSetting maxPitchSpeed = new SliderSetting(this, "Макс скорость Pitch", 140F, 50F, 360F, 5F);
    private final SliderSetting breakTolerance = new SliderSetting(this, "Допуск ломания", 18F, 1F, 45F, 1F);
    private final SliderSetting interactTolerance = new SliderSetting(this, "Допуск взаимодействия", 14F, 1F, 45F, 1F);

    private final StopWatch actionTimer = new StopWatch();
    private final StopWatch commandTimer = new StopWatch();
    private final StopWatch notifyTimer = new StopWatch();
    private final StopWatch toolSwitchTimer = new StopWatch();
    private final StopWatch breakTimer = new StopWatch();

    BlockPos farmLocation;
    BlockPos currentTargetBlock;
    Direction targetBlockSide;
    boolean isBaritoneMining;
    boolean hasBaritone;
    long lastRotationUpdateMs;

    @Override
    protected void onEnable() {
        super.onEnable();
        isBaritoneMining = false;
        currentTargetBlock = null;
        targetBlockSide = null;

        actionTimer.reset();
        commandTimer.reset();
        notifyTimer.setMs(3000L);
        toolSwitchTimer.reset();
        breakTimer.reset();
        lastRotationUpdateMs = 0L;

        hasBaritone = detectBaritone();
        if (breakMode.is("Baritone") && !hasBaritone) {
            log("Baritone не найден, переключись на режим 'На месте'");
            return;
        }

        farmLocation = getLookTargetBlock();
        if (farmLocation == null) {
            log("Посмотри на блок земли и включи модуль снова");
            return;
        }

        if (breakMode.is("Baritone")) {
            runBaritoneCommand("#set autoTool true");
            runBaritoneCommand("#set allowBreak true");
        }

        log("Ферма запущена: " + farmLocation.getX() + " " + farmLocation.getY() + " " + farmLocation.getZ());
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        if (breakMode.is("Baritone") && hasBaritone && isBaritoneMining && autoStop.getValue()) {
            runBaritoneCommand("#stop");
        }
        isBaritoneMining = false;
        currentTargetBlock = null;
        targetBlockSide = null;
        farmLocation = null;
        lastRotationUpdateMs = 0L;
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (mc.player == null || mc.world == null) return;

        if (farmLocation == null) {
            farmLocation = getLookTargetBlock();
            if (farmLocation == null) return;
        }

        if (mc.player.getDistanceSq(Vector3d.copyCentered(farmLocation)) > maxFarmDistance.getValue() * maxFarmDistance.getValue()) {
            if (notifyTimer.finished(2500)) {
                log("Слишком далеко от точки фермы");
                notifyTimer.reset();
            }
            if (breakMode.is("Baritone") && isBaritoneMining && autoStop.getValue()) {
                runBaritoneCommand("#stop");
                isBaritoneMining = false;
            }
            return;
        }

        if (isTreeGrown()) {
            if (breakMode.is("Baritone")) {
                updateBaritoneMining();
            }
            performManualChop();
            return;
        }

        currentTargetBlock = null;
        targetBlockSide = null;

        if (breakMode.is("Baritone") && isBaritoneMining && autoStop.getValue()) {
            runBaritoneCommand("#stop");
            isBaritoneMining = false;
        }

        if (!actionTimer.finished(actionDelay.getValue())) return;
        actionTimer.reset();

        if (!isSaplingPlanted()) {
            if (!tryPlantSapling() && notifyTimer.finished(2500)) {
                log("Не удалось посадить саженец (проверь саженцы в инвентаре)");
                notifyTimer.reset();
            }
            return;
        }

        if (!tryApplyBoneMeal() && notifyTimer.finished(2500)) {
            log("Нет костной муки, ждём естественного роста");
            notifyTimer.reset();
        }
    }

    private void updateBaritoneMining() {
        if (!commandTimer.finished(commandDelay.getValue())) return;
        String targets = anyLeavesNearby() ? LEAVES_TYPES : LOG_TYPES;
        runBaritoneCommand("#mine 1 " + targets);
        isBaritoneMining = true;
        commandTimer.reset();
    }

    private void performManualChop() {
        if (!breakTimer.finished(breakTickDelay.getValue())) return;

        if (!isValidChopTarget(currentTargetBlock)) {
            BlockInfo best = findBestBlockToChop();
            if (best == null) {
                currentTargetBlock = null;
                targetBlockSide = null;
                return;
            }
            currentTargetBlock = best.pos;
            targetBlockSide = best.side;
        }

        if (currentTargetBlock == null || targetBlockSide == null) return;
        if (!aimAtBlock(currentTargetBlock, targetBlockSide)) return;

        switchToBestTool(currentTargetBlock);
        mc.playerController.onPlayerDamageBlock(currentTargetBlock, targetBlockSide);
        mc.player.swingArm(Hand.MAIN_HAND);
        breakTimer.reset();
    }

    private boolean tryPlantSapling() {
        Item sapling = findSaplingInInventory();
        if (sapling == Items.AIR) return false;
        if (!canPlantAt(farmLocation)) return false;

        int slot = ensureHotbarItem(sapling);
        if (slot == -1) return false;

        BlockPos plantPos = farmLocation.up();
        BlockRayTraceResult hit = new BlockRayTraceResult(Vector3d.copyCentered(farmLocation), Direction.UP, farmLocation, false);
        return interactWithSlot(slot, hit, interactTolerance.getValue());
    }

    private boolean tryApplyBoneMeal() {
        int slot = ensureHotbarItem(Items.BONE_MEAL);
        if (slot == -1) return false;

        BlockPos saplingPos = farmLocation.up();
        BlockRayTraceResult hit = new BlockRayTraceResult(Vector3d.copyCentered(saplingPos), Direction.UP, saplingPos, false);
        return interactWithSlot(slot, hit, interactTolerance.getValue());
    }

    private boolean interactWithSlot(int slot, BlockRayTraceResult hit, float tolerance) {
        if (slot < 0) return false;
        if (!rotateAndCheckAim(hit.getHitVec(), tolerance)) return false;

        int oldSlot = mc.player.inventory.currentItem;
        if (oldSlot != slot) {
            mc.player.inventory.currentItem = slot;
            mc.playerController.syncCurrentPlayItem();
        }

        ActionResultType result = mc.playerController.processRightClickBlock(mc.player, mc.world, Hand.MAIN_HAND, hit);
        mc.player.swingArm(Hand.MAIN_HAND);
        if (result == ActionResultType.PASS) {
            mc.playerController.processRightClick(mc.player, mc.world, Hand.MAIN_HAND);
        }

        if (oldSlot != slot) {
            mc.player.inventory.currentItem = oldSlot;
            mc.playerController.syncCurrentPlayItem();
        }

        return result.isSuccessOrConsume();
    }

    private int ensureHotbarItem(Item target) {
        int hotbarSlot = getHotbarSlot(target);
        if (hotbarSlot != -1) return hotbarSlot;

        int invSlot = getInventorySlot(target);
        if (invSlot == -1) return -1;

        int swapTo = findTrashHotbarSlot();
        if (swapTo == -1) return -1;

        mc.playerController.windowClick(mc.player.openContainer.windowId, invSlot, swapTo, ClickType.SWAP, mc.player);
        return swapTo;
    }

    private int findTrashHotbarSlot() {
        for (int i = 0; i < 9; i++) {
            Item item = mc.player.inventory.getStackInSlot(i).getItem();
            if (item == Items.AIR) return i;
            if (!isProtectedTool(item)) return i;
        }
        return -1;
    }

    private boolean isProtectedTool(Item item) {
        String n = item.getName().getString().toLowerCase();
        return item == Items.BONE_MEAL
                || n.contains("axe")
                || n.contains("топор")
                || n.contains("pickaxe")
                || n.contains("кирк");
    }

    private int getInventorySlot(Item target) {
        for (int i = 9; i < 36; i++) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == target) return i;
        }
        return -1;
    }

    private int getHotbarSlot(Item target) {
        for (int i = 0; i < 9; i++) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == target) return i;
        }
        return -1;
    }

    private boolean isTreeGrown() {
        int r = scanRadius.getValue().intValue();
        int h = scanHeight.getValue().intValue();
        for (int x = -r; x <= r; x++) {
            for (int z = -r; z <= r; z++) {
                for (int y = 0; y <= h; y++) {
                    BlockPos pos = farmLocation.add(x, y, z);
                    BlockState state = mc.world.getBlockState(pos);
                    if (state.isIn(BlockTags.LOGS) || state.isIn(BlockTags.LEAVES)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean anyLeavesNearby() {
        int r = scanRadius.getValue().intValue();
        int h = scanHeight.getValue().intValue();
        for (int x = -r; x <= r; x++) {
            for (int z = -r; z <= r; z++) {
                for (int y = 0; y <= h; y++) {
                    if (mc.world.getBlockState(farmLocation.add(x, y, z)).isIn(BlockTags.LEAVES)) return true;
                }
            }
        }
        return false;
    }

    private boolean isSaplingPlanted() {
        return mc.world.getBlockState(farmLocation.up()).isIn(BlockTags.SAPLINGS);
    }

    private boolean canPlantAt(BlockPos pos) {
        BlockState state = mc.world.getBlockState(pos);
        BlockPos up = pos.up();
        if (!mc.world.getBlockState(up).isAir()) return false;
        return state.getBlock() == Blocks.GRASS_BLOCK
                || state.getBlock() == Blocks.DIRT
                || state.getBlock() == Blocks.COARSE_DIRT
                || state.getBlock() == Blocks.PODZOL
                || state.getBlock() == Blocks.FARMLAND;
    }

    private BlockPos getLookTargetBlock() {
        if (mc.objectMouseOver instanceof BlockRayTraceResult hit && hit.getType() == RayTraceResult.Type.BLOCK) {
            BlockPos pos = hit.getPos();
            if (canPlantAt(pos)) return pos;
            if (canPlantAt(pos.down())) return pos.down();
        }
        return null;
    }

    private Item findSaplingInInventory() {
        List<Item> types = List.of(
                Items.OAK_SAPLING, Items.SPRUCE_SAPLING, Items.BIRCH_SAPLING,
                Items.JUNGLE_SAPLING, Items.ACACIA_SAPLING, Items.DARK_OAK_SAPLING
        );
        for (int i = 0; i < 36; i++) {
            Item it = mc.player.inventory.getStackInSlot(i).getItem();
            if (types.contains(it)) return it;
        }
        return Items.AIR;
    }

    private boolean isValidChopTarget(BlockPos pos) {
        if (pos == null) return false;

        BlockState state = mc.world.getBlockState(pos);
        if (!state.isIn(BlockTags.LOGS) && !state.isIn(BlockTags.LEAVES)) return false;
        if (mc.player.getDistanceSq(Vector3d.copyCentered(pos)) > reachDistance.getValue() * reachDistance.getValue())
            return false;

        Direction side = getVisibleSide(pos);
        if (side == null) return false;
        targetBlockSide = side;
        return true;
    }

    private BlockInfo findBestBlockToChop() {
        List<BlockInfo> logs = new ArrayList<>();
        List<BlockInfo> leaves = new ArrayList<>();
        int r = scanRadius.getValue().intValue();
        int h = scanHeight.getValue().intValue();
        double maxDistSq = reachDistance.getValue() * reachDistance.getValue();

        for (int x = -r; x <= r; x++) {
            for (int z = -r; z <= r; z++) {
                for (int y = 0; y <= h; y++) {
                    BlockPos pos = farmLocation.add(x, y, z);
                    BlockState state = mc.world.getBlockState(pos);
                    double distSq = mc.player.getDistanceSq(Vector3d.copyCentered(pos));

                    if (distSq > maxDistSq) continue;
                    if (!state.isIn(BlockTags.LOGS) && !state.isIn(BlockTags.LEAVES)) continue;

                    Direction side = getVisibleSide(pos);
                    if (side == null) continue;

                    BlockInfo info = new BlockInfo(pos, side, distSq);
                    if (state.isIn(BlockTags.LEAVES)) leaves.add(info);
                    else logs.add(info);
                }
            }
        }

        Comparator<BlockInfo> byDist = Comparator.comparingDouble(v -> v.distSq);
        if (!leaves.isEmpty()) return leaves.stream().min(byDist).orElse(null);
        return logs.stream().min(byDist).orElse(null);
    }

    private Direction getVisibleSide(BlockPos pos) {
        Vector3d eyes = mc.player.getEyePosition(1.0F);
        Vector3d center = Vector3d.copyCentered(pos);
        for (Direction side : Direction.values()) {
            Vector3d point = center.add(
                    side.getXOffset() * 0.49D,
                    side.getYOffset() * 0.49D,
                    side.getZOffset() * 0.49D
            );
            BlockRayTraceResult hit = mc.world.rayTraceBlocks(new RayTraceContext(
                    eyes, point, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, mc.player
            ));
            if (hit != null && hit.getType() == RayTraceResult.Type.BLOCK && hit.getPos().equals(pos)) {
                return hit.getFace();
            }
        }
        return null;
    }

    private boolean aimAtBlock(BlockPos pos, Direction side) {
        Vector3d target = Vector3d.copyCentered(pos).add(
                side.getXOffset() * 0.5D,
                side.getYOffset() * 0.5D,
                side.getZOffset() * 0.5D
        );
        return rotateAndCheckAim(target, breakTolerance.getValue());
    }

    private boolean rotateAndCheckAim(Vector3d target, float tolerance) {
        long now = System.currentTimeMillis();
        if (lastRotationUpdateMs == 0L) {
            lastRotationUpdateMs = now;
        }
        float dt = MathHelper.clamp((now - lastRotationUpdateMs) / 1000.0f, 0.001f, 0.10f);
        lastRotationUpdateMs = now;

        Vector3d eyes = mc.player.getEyePosition(1.0F);
        double dx = target.x - eyes.x;
        double dy = target.y - eyes.y;
        double dz = target.z - eyes.z;
        double flat = Math.sqrt(dx * dx + dz * dz);

        float targetYaw = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0D);
        float targetPitch = (float) (-Math.toDegrees(Math.atan2(dy, flat)));

        float currentYaw = mc.player.rotationYaw;
        float currentPitch = mc.player.rotationPitch;

        float diffYaw = MathHelper.wrapDegrees(targetYaw - currentYaw);
        float diffPitch = targetPitch - currentPitch;

        float stepYaw = maxYawSpeed.getValue() * dt;
        float stepPitch = maxPitchSpeed.getValue() * dt;

        float nextYaw = currentYaw + MathHelper.clamp(diffYaw, -stepYaw, stepYaw);
        float nextPitch = MathHelper.clamp(currentPitch + MathHelper.clamp(diffPitch, -stepPitch, stepPitch), -89.9F, 89.9F);

        // Same visible/head rotation style as combat rotations (non-silent + sensitivity snapping).
        nextYaw = GCDUtils.getSensitivity(nextYaw);
        nextPitch = GCDUtils.getSensitivity(nextPitch);

        mc.player.rotationYaw = nextYaw;
        mc.player.rotationYawHead = nextYaw;
        mc.player.renderYawOffset = nextYaw;
        mc.player.rotationPitch = nextPitch;

        float remainYaw = Math.abs(MathHelper.wrapDegrees(targetYaw - nextYaw));
        float remainPitch = Math.abs(targetPitch - nextPitch);
        return remainYaw <= tolerance && remainPitch <= tolerance;
    }

    private void switchToBestTool(BlockPos pos) {
        if (!toolSwitchTimer.finished(toolSwitchDelay.getValue())) return;

        BlockState state = mc.world.getBlockState(pos);
        int bestSlot = -1;
        float bestSpeed = 1.0F;

        for (int i = 0; i < 9; i++) {
            float speed = mc.player.inventory.getStackInSlot(i).getDestroySpeed(state);
            if (speed > bestSpeed) {
                bestSpeed = speed;
                bestSlot = i;
            }
        }

        if (bestSlot != -1 && bestSlot != mc.player.inventory.currentItem) {
            mc.player.inventory.currentItem = bestSlot;
            mc.playerController.syncCurrentPlayItem();
            toolSwitchTimer.reset();
        }
    }

    private void runBaritoneCommand(String cmd) {
        if (mc.player == null) return;
        mc.player.sendChatMessage(cmd);
    }

    private boolean detectBaritone() {
        try {
            Class.forName("org.privates.baritone.api.BaritoneAPI");
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    private void log(String text) {
        ChatUtil.addText("§7[§dAppleFarm§7] §f" + text);
    }

    private record BlockInfo(BlockPos pos, Direction side, double distSq) {
    }
}
