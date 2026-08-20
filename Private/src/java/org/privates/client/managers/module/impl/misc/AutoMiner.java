package org.privates.client.managers.module.impl.misc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import org.privates.baritone.api.BaritoneAPI;
import org.privates.baritone.api.IBaritone;
import org.privates.baritone.api.pathing.goals.GoalXZ;
import org.privates.baritone.api.pathing.movement.IMovement;
import org.privates.baritone.api.pathing.path.IPathExecutor;
import org.privates.baritone.api.process.IMineProcess;
import org.privates.baritone.pathing.movement.movements.MovementPillar;
import org.privates.client.api.annotations.Category1;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.other.GameUpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.MultiBooleanSetting;
import org.privates.lib.util.time.StopWatch;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Category1
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "FTNuker", category = Category.MISC, description = "Auto mine selected ores with smart chain logic")
public class AutoMiner extends Module {

    final MultiBooleanSetting ores = new MultiBooleanSetting(this, "Добывать",
            BooleanSetting.of("Алмазная руда", true),
            BooleanSetting.of("Железная руда", false),
            BooleanSetting.of("Золотая руда", false),
            BooleanSetting.of("Угольная руда", false)
    );

    IMineProcess mineProcess;
    static final int DEFAULT_RADIUS = 30;
    static final int EAT_THRESHOLD = 18;
    static final double PICKAXE_MIN_PCT = 0.20;

    boolean isEating = false;
    int previousSlot = -1;

    final String[] anCommands = new String[]{
            "/an217", "/an218", "/an216", "/an313", "/an312", "/an308", "/an309",
            "/an211", "/an115", "/an602", "/an505", "/an311", "/an312", "/an104",
            "/an212", "/an213", "/an503", "/an504", "/an507", "/an506"
    };

    int anIndex = 0;
    boolean chainInProgress = false;
    int chainStage = 0; // 0 idle, 1 wait 5s after /an, 2 wait 10s after /warp, 3 moving forward, 21 pillar warp flow, 22 pillar moving
    final StopWatch chainTimer = new StopWatch();
    boolean prevMineActive = false;
    boolean haltedFromHub = false;

    boolean isUnstucking = false;
    BlockPos unstuckBreakingPos = null;

    Boolean prevRenderPath = null;

    BlockPos lastStuckCheckPos = null;
    final StopWatch stuckTimer = new StopWatch();

    @Override
    protected void onEnable() {
        super.onEnable();
        IBaritone baritone = getBaritone();
        if (baritone == null) {
            setEnabled(false);
            return;
        }
        mineProcess = baritone.getMineProcess();
        haltedFromHub = false;
        prevMineActive = false;
        chainInProgress = false;
        chainStage = 0;
        isEating = false;
        previousSlot = -1;
        lastStuckCheckPos = null;
        stuckTimer.reset();

        try {
            var settings = BaritoneAPI.getSettings();
            if (prevRenderPath == null) prevRenderPath = settings.renderPath.value;
            settings.renderPath.value = false;
        } catch (Throwable ignored) {
        }

        List<Block> targets = getSelectedTargets();
        if (!targets.isEmpty()) {
            mineProcess.mine(targets.toArray(new Block[0]));
            filterTargetsWithinRadius(baritone, DEFAULT_RADIUS);
        }
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        if (mineProcess != null) {
            mineProcess.cancel();
        }
        try {
            var settings = BaritoneAPI.getSettings();
            if (prevRenderPath != null) {
                settings.renderPath.value = prevRenderPath;
                prevRenderPath = null;
            }
        } catch (Throwable ignored) {
        }

        try {
            mc.gameSettings.keyBindAttack.setPressed(false);
            mc.gameSettings.keyBindUseItem.setPressed(false);
            if (isUnstucking && mc.playerController != null) {
                mc.playerController.resetBlockRemoving();
            }
        } catch (Throwable ignored) {
        }

        isUnstucking = false;
        unstuckBreakingPos = null;
    }

    @EventHandler
    public void onUpdate(GameUpdateEvent event) {
        if (mc.player == null || mc.world == null) return;
        IBaritone baritone = getBaritone();
        if (baritone == null) return;
        if (mineProcess == null) mineProcess = baritone.getMineProcess();
        if (haltedFromHub) return;

        boolean hungry = mc.player.getFoodStats().getFoodLevel() < EAT_THRESHOLD;
        if (hungry) {
            if (mineProcess != null && mineProcess.isActive()) mineProcess.cancel();
            prevMineActive = false;
            if (!isEating) {
                int foodSlot = findFoodHotbarSlot();
                if (foodSlot != -1 || hasFoodInHands()) startEating(foodSlot);
            } else {
                continueEating();
            }
            return;
        } else if (isEating) {
            stopEating();
        }

        if (ensurePickaxeDurabilityOrHub()) {
            if (haltedFromHub) {
                if (mineProcess != null && mineProcess.isActive()) mineProcess.cancel();
                chainInProgress = false;
                return;
            }
        }

        if (handleSuffocationUnstuck()) {
            prevMineActive = mineProcess != null && mineProcess.isActive();
            return;
        }

        List<Block> targets = getSelectedTargets();
        boolean currentMineActive = mineProcess != null && mineProcess.isActive();

        BlockPos cur = mc.player.getPosition();
        if (lastStuckCheckPos == null || !lastStuckCheckPos.equals(cur)) {
            lastStuckCheckPos = cur;
            stuckTimer.reset();
        } else if (currentMineActive && !chainInProgress && !isEating && stuckTimer.finished(5000)) {
            if (mineProcess != null && mineProcess.isActive()) {
                mineProcess.cancel();
            }
            mc.player.sendChatMessage("/warp mine");
            chainInProgress = true;
            chainStage = 21;
            chainTimer.reset();
            prevMineActive = currentMineActive;
            return;
        }

        if (!chainInProgress && !isEating && isCurrentMovementPillar(baritone)) {
            if (mineProcess != null && mineProcess.isActive()) {
                mineProcess.cancel();
            }
            mc.player.sendChatMessage("/warp mine");
            chainInProgress = true;
            chainStage = 21;
            chainTimer.reset();
            prevMineActive = currentMineActive;
            return;
        }

        if (chainInProgress) {
            switch (chainStage) {
                case 1 -> {
                    if (chainTimer.finished(5000)) {
                        mc.player.sendChatMessage("/warp mine");
                        chainStage = 2;
                        chainTimer.reset();
                    }
                }
                case 2 -> {
                    if (chainTimer.finished(10000)) {
                        Vector3d origin = mc.player.getPositionVec();
                        float yaw = mc.player.rotationYaw;
                        baritone.getCustomGoalProcess().setGoalAndPath(GoalXZ.fromDirection(origin, yaw, 9.0));
                        chainStage = 3;
                    }
                }
                case 3 -> {
                    if (!baritone.getCustomGoalProcess().isActive()) {
                        chainInProgress = false;
                        chainStage = 0;
                    }
                }
                case 21 -> {
                    if (chainTimer.finished(10000)) {
                        Vector3d origin = mc.player.getPositionVec();
                        float yaw = mc.player.rotationYaw;
                        baritone.getCustomGoalProcess().setGoalAndPath(GoalXZ.fromDirection(origin, yaw, 9.0));
                        chainStage = 22;
                    }
                }
                case 22 -> {
                    if (!baritone.getCustomGoalProcess().isActive()) {
                        chainInProgress = false;
                        chainStage = 0;
                    }
                }
                default -> {
                }
            }
            filterTargetsWithinRadius(baritone, DEFAULT_RADIUS);
            prevMineActive = currentMineActive;
            return;
        }

        if (targets.isEmpty()) {
            if (mineProcess != null && mineProcess.isActive()) mineProcess.cancel();
            prevMineActive = currentMineActive;
            return;
        }

        if (prevMineActive && !currentMineActive && !isEating) {
            String cmd = anCommands[anIndex];
            anIndex = (anIndex + 1) % anCommands.length;
            mc.player.sendChatMessage(cmd);
            chainInProgress = true;
            chainStage = 1;
            chainTimer.reset();
            prevMineActive = currentMineActive;
            return;
        }

        if (mineProcess != null && !currentMineActive && !chainInProgress) {
            mineProcess.mine(targets.toArray(new Block[0]));
        }

        filterTargetsWithinRadius(baritone, DEFAULT_RADIUS);

        if (!chainInProgress && !isEating && currentMineActive && noTargetsInRadius(baritone)) {
            if (mineProcess != null && mineProcess.isActive()) {
                mineProcess.cancel();
            }
            String cmd = anCommands[anIndex];
            anIndex = (anIndex + 1) % anCommands.length;
            mc.player.sendChatMessage(cmd);
            chainInProgress = true;
            chainStage = 1;
            chainTimer.reset();
            prevMineActive = currentMineActive;
            return;
        }

        prevMineActive = currentMineActive;
    }

    private IBaritone getBaritone() {
        if (mc.player == null) return null;
        return BaritoneAPI.getProvider().getBaritoneForPlayer(mc.player);
    }

    private List<Block> getSelectedTargets() {
        List<Block> targets = new ArrayList<>();
        if (ores.getValue("Алмазная руда")) targets.add(Blocks.DIAMOND_ORE);
        if (ores.getValue("Железная руда")) targets.add(Blocks.IRON_ORE);
        if (ores.getValue("Золотая руда")) targets.add(Blocks.GOLD_ORE);
        if (ores.getValue("Угольная руда")) targets.add(Blocks.COAL_ORE);
        return targets;
    }

    private boolean isCurrentMovementPillar(IBaritone baritone) {
        try {
            IPathExecutor exec = baritone.getPathingBehavior().getCurrent();
            if (exec == null || exec.getPath() == null || exec.getPath().movements() == null || exec.getPath().movements().isEmpty()) {
                return false;
            }
            int idx = Math.min(Math.max(exec.getPosition(), 0), exec.getPath().movements().size() - 1);
            IMovement mv = exec.getPath().movements().get(idx);
            return mv instanceof MovementPillar;
        } catch (Throwable ignored) {
            return false;
        }
    }

    private boolean ensurePickaxeDurabilityOrHub() {
        if (mc.player == null) return false;
        ItemStack main = mc.player.getHeldItemMainhand();
        boolean hasPickaxeInHand = main != null && main.getItem() instanceof PickaxeItem;
        if (hasPickaxeInHand) {
            double pct = remainingPct(main);
            if (pct <= PICKAXE_MIN_PCT) {
                int best = findBestPickaxeSlotAboveThreshold(PICKAXE_MIN_PCT);
                if (best != -1) {
                    switchToSlot(best);
                    return true;
                } else {
                    mc.player.sendChatMessage("/hub");
                    haltedFromHub = true;
                    return true;
                }
            }
        } else {
            int best = findBestPickaxeSlotAboveThreshold(PICKAXE_MIN_PCT);
            if (best != -1) {
                switchToSlot(best);
                return true;
            }
            mc.player.sendChatMessage("/hub");
            haltedFromHub = true;
            return true;
        }
        return false;
    }

    private int findBestPickaxeSlotAboveThreshold(double minPct) {
        if (mc.player == null) return -1;
        int bestSlot = -1;
        double bestPct = -1.0;
        for (int i = 0; i < 36; i++) {
            ItemStack s = mc.player.inventory.getStackInSlot(i);
            if (s == null || s.isEmpty() || !(s.getItem() instanceof PickaxeItem)) continue;
            double pct = remainingPct(s);
            if (pct > minPct && pct > bestPct) {
                bestPct = pct;
                bestSlot = i;
            }
        }
        return bestSlot;
    }

    private double remainingPct(ItemStack stack) {
        int max = stack.getMaxDamage();
        if (max <= 0) return 1.0;
        int used = stack.getDamage();
        int remaining = Math.max(0, max - used);
        return (double) remaining / (double) max;
    }

    private void switchToSlot(int invIndex) {
        if (mc.player == null || mc.playerController == null) return;
        if (invIndex < 0 || invIndex >= 36) return;
        int currentHotbar = mc.player.inventory.currentItem;
        if (invIndex <= 8) {
            if (currentHotbar != invIndex) {
                mc.player.inventory.currentItem = invIndex;
            }
        } else {
            int windowId = mc.player.openContainer.windowId;
            mc.playerController.windowClick(windowId, invIndex, currentHotbar, ClickType.SWAP, mc.player);
        }
    }

    private void filterTargetsWithinRadius(IBaritone baritone, int radius) {
        if (baritone == null || mc.player == null || mineProcess == null) return;
        try {
            Object impl = baritone.getMineProcess();
            Field known = impl.getClass().getDeclaredField("knownOreLocations");
            known.setAccessible(true);
            Object val = known.get(impl);
            if (!(val instanceof List)) return;
            @SuppressWarnings("unchecked")
            List<BlockPos> locs = (List<BlockPos>) val;
            if (locs == null || locs.isEmpty()) return;

            BlockPos playerPos = mc.player.getPosition();
            double maxSq = (double) radius * radius;
            List<BlockPos> filtered = locs.stream()
                    .filter(p -> p != null && playerPos.distanceSq(p) <= maxSq)
                    .collect(Collectors.toCollection(ArrayList::new));
            known.set(impl, filtered);
        } catch (Throwable ignored) {
        }
    }

    private boolean noTargetsInRadius(IBaritone baritone) {
        try {
            Object impl = baritone.getMineProcess();
            Field known = impl.getClass().getDeclaredField("knownOreLocations");
            known.setAccessible(true);
            Object val = known.get(impl);
            if (!(val instanceof List)) return true;
            @SuppressWarnings("unchecked")
            List<BlockPos> locs = (List<BlockPos>) val;
            return locs == null || locs.isEmpty();
        } catch (Throwable ignored) {
            return false;
        }
    }

    private boolean hasFoodInHands() {
        if (mc.player == null) return false;
        ItemStack main = mc.player.getHeldItemMainhand();
        ItemStack off = mc.player.getHeldItemOffhand();
        return (!main.isEmpty() && main.getItem().isFood()) || (!off.isEmpty() && off.getItem().isFood());
    }

    private int findFoodHotbarSlot() {
        if (mc.player == null) return -1;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem().isFood()) {
                return i;
            }
        }
        return -1;
    }

    private void startEating(int foodSlot) {
        isEating = true;
        if (!hasFoodInHands()) {
            if (foodSlot == -1) {
                isEating = false;
                return;
            }
            previousSlot = mc.player.inventory.currentItem;
            mc.player.inventory.currentItem = foodSlot;
        }
        continueEating();
    }

    private void continueEating() {
        if (mc.currentScreen == null) {
            mc.gameSettings.keyBindUseItem.setPressed(true);
        }
        if (mc.player.getFoodStats().getFoodLevel() >= EAT_THRESHOLD || !hasFoodInHands()) {
            stopEating();
        }
    }

    private void stopEating() {
        mc.gameSettings.keyBindUseItem.setPressed(false);
        isEating = false;
        if (previousSlot != -1 && mc.player != null) {
            mc.player.inventory.currentItem = previousSlot;
            previousSlot = -1;
        }
    }

    private boolean handleSuffocationUnstuck() {
        if (mc.player == null || mc.world == null || mc.playerController == null) return false;
        boolean inside = mc.player.isEntityInsideOpaqueBlock();
        if (!inside) {
            if (isUnstucking) {
                try {
                    mc.gameSettings.keyBindAttack.setPressed(false);
                    mc.playerController.resetBlockRemoving();
                } catch (Throwable ignored) {
                }
                isUnstucking = false;
                unstuckBreakingPos = null;
            }
            return false;
        }

        if (mineProcess != null && mineProcess.isActive()) {
            mineProcess.cancel();
        }

        RayTraceResult rt = mc.player.pick(2.5D, 1.0F, false);
        BlockPos targetPos = null;
        Direction face = Direction.UP;
        if (rt != null && rt.getType() == RayTraceResult.Type.BLOCK && rt instanceof net.minecraft.util.math.BlockRayTraceResult brt) {
            targetPos = brt.getPos();
            face = brt.getFace();
        }
        if (targetPos == null) {
            targetPos = new BlockPos(mc.player.getEyePosition(1.0f));
            face = Direction.UP;
        }
        if (mc.world.isAirBlock(targetPos)) {
            targetPos = mc.player.getPosition();
        }

        if (!mc.world.isAirBlock(targetPos)) {
            try {
                if (unstuckBreakingPos == null || !unstuckBreakingPos.equals(targetPos)) {
                    mc.playerController.syncCurrentPlayItem();
                    mc.playerController.clickBlock(targetPos, face);
                    unstuckBreakingPos = targetPos;
                }
                mc.playerController.onPlayerDamageBlock(targetPos, face);
                mc.player.swingArm(Hand.MAIN_HAND);
                mc.gameSettings.keyBindAttack.setPressed(true);
                isUnstucking = true;
            } catch (Throwable ignored) {
            }
        }
        return true;
    }
}
