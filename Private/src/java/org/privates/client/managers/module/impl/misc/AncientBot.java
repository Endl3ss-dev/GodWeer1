package org.privates.client.managers.module.impl.misc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CAnimateHandPacket;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CPlayerTryUseItemOnBlockPacket;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import org.privates.baritone.api.BaritoneAPI;
import org.privates.baritone.api.IBaritone;
import org.privates.baritone.api.pathing.goals.GoalBlock;
import org.privates.client.api.annotations.Category1;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.impl.render.XRay;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.chat.ChatUtil;
import org.privates.client.utils.other.Instance;
import org.privates.lib.util.time.StopWatch;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Category1
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "AncientBot", category = Category.MISC, description = "Smart ancient debris TNT cycle bot for nether")
public class AncientBot extends Module {

    public static AncientBot getInstance() {
        return Instance.get(AncientBot.class);
    }

    enum State {
        CHECK,
        PLACE_TNT,
        IGNITE_TNT,
        RETREAT,
        WAIT_XRAY,
        MINE_DEBRIS,
        MOVE_NEXT,
        EAT
    }

    final SliderSetting minY = new SliderSetting(this, "Min Y", 12F, 1F, 120F, 1F);
    final SliderSetting maxY = new SliderSetting(this, "Max Y", 25F, 1F, 120F, 1F);
    final SliderSetting retreatDistance = new SliderSetting(this, "Retreat Distance", 8F, 4F, 16F, 1F);
    final SliderSetting nextMinDistance = new SliderSetting(this, "Next Min Distance", 30F, 10F, 80F, 1F);
    final SliderSetting nextMaxDistance = new SliderSetting(this, "Next Max Distance", 50F, 15F, 120F, 1F);
    final SliderSetting xrayWaitMs = new SliderSetting(this, "Xray Wait (ms)", 2200F, 500F, 6000F, 100F);
    final SliderSetting mineReach = new SliderSetting(this, "Mine Reach", 4.2F, 2.0F, 6.0F, 0.1F);
    final SliderSetting searchRadius = new SliderSetting(this, "Debris Radius", 28F, 10F, 64F, 1F);
    final BooleanSetting debug = new BooleanSetting(this, "Debug", true);

    State state = State.CHECK;
    State stateBeforeEat = State.CHECK;
    final StopWatch actionTimer = new StopWatch();
    final StopWatch logTimer = new StopWatch();
    final StopWatch mineHitTimer = new StopWatch();
    final StopWatch mineRetryTimer = new StopWatch();
    final StopWatch stuckTimer = new StopWatch();

    BlockPos tntPos;
    BlockPos retreatGoal;
    BlockPos nextBlastPos;
    BlockPos lastBlastPos;
    BlockPos currentDebris;
    final List<BlockPos> debrisQueue = new ArrayList<>();
    final Set<BlockPos> allowedDebris = new HashSet<>();
    boolean mineCommandActive = false;
    int totalDebrisMined = 0;
    BlockPos progressPos;

    boolean originalCaptured = false;
    boolean prevAllowBreak;
    boolean prevAllowPlace;
    boolean prevAllowParkourAscend;
    boolean prevAllowWaterBucketFall;
    List<Item> prevThrowawayItems = new ArrayList<>();

    boolean xrayWasEnabled;
    String xrayPrevMode = "Funtime-Spooky";
    boolean xrayStateCaptured;

    final Random random = new Random();

    @Override
    protected void onEnable() {
        super.onEnable();
        state = State.CHECK;
        tntPos = null;
        retreatGoal = null;
        nextBlastPos = null;
        lastBlastPos = null;
        currentDebris = null;
        debrisQueue.clear();
        allowedDebris.clear();
        mineCommandActive = false;
        totalDebrisMined = 0;
        progressPos = mc.player != null ? mc.player.getPosition() : BlockPos.ZERO;
        actionTimer.reset();
        logTimer.reset();
        mineHitTimer.reset();
        mineRetryTimer.reset();
        stuckTimer.reset();
        captureOriginalBaritoneSettings();
        ensureXraySpookyEnabled();
        log("AncientBot enabled");
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        stopEverything();
        restoreOriginalBaritoneSettings();
        restoreXrayState();
        mc.gameSettings.keyBindUseItem.setPressed(false);
        log("AncientBot disabled");
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (mc.player == null || mc.world == null) return;

        if (isHungry()) {
            enterEatState();
        }

        handleStuckAndLava();

        switch (state) {
            case CHECK -> handleCheck();
            case PLACE_TNT -> handlePlaceTnt();
            case IGNITE_TNT -> handleIgniteTnt();
            case RETREAT -> handleRetreat();
            case WAIT_XRAY -> handleWaitXray();
            case MINE_DEBRIS -> handleMineDebris();
            case MOVE_NEXT -> handleMoveNext();
            case EAT -> handleEat();
        }
    }

    private void handleCheck() {
        applyGotoRestrictions();
        String fail = validateRequirements();
        if (fail != null) {
            if (logTimer.finished(1200)) {
                log("Waiting requirements: " + fail);
                logTimer.reset();
            }
            stopPathingOnly();
            return;
        }

        ensureXraySpookyEnabled();
        state = State.PLACE_TNT;
        actionTimer.reset();
        log("Requirements OK. Start cycle.");
    }

    private void handlePlaceTnt() {
        applyGotoRestrictions();
        if (tntPos == null || !canPlaceTntAt(tntPos)) {
            tntPos = findBestTntPlacement();
        }
        if (tntPos == null) {
            if (logTimer.finished(1000)) {
                log("No valid TNT place position near player.");
                logTimer.reset();
            }
            return;
        }

        if (placeTntAt(tntPos)) {
            state = State.IGNITE_TNT;
            actionTimer.reset();
            log("TNT placed at " + posToStr(tntPos));
            return;
        }
        if (actionTimer.finished(1800)) {
            log("Failed to place TNT, recheck requirements.");
            state = State.CHECK;
            actionTimer.reset();
        }
    }

    private void handleIgniteTnt() {
        applyGotoRestrictions();
        if (tntPos == null) {
            state = State.CHECK;
            return;
        }

        if (igniteTntAt(tntPos)) {
            lastBlastPos = tntPos.toImmutable();
            retreatGoal = computeRetreatGoal(retreatDistance.getValue().intValue());
            state = State.RETREAT;
            actionTimer.reset();
            log("TNT ignited, retreating.");
            return;
        }

        if (actionTimer.finished(2200)) {
            log("Ignite timeout, restart cycle.");
            state = State.CHECK;
            actionTimer.reset();
        }
    }

    private void handleRetreat() {
        applyGotoRestrictions();
        if (retreatGoal == null) {
            state = State.CHECK;
            return;
        }

        IBaritone baritone = getBaritone();
        if (baritone == null) return;
        baritone.getCustomGoalProcess().setGoalAndPath(new GoalBlock(retreatGoal));

        double distSq = mc.player.getDistanceSq(Vector3d.copyCentered(retreatGoal));
        if (distSq <= 6.25D || actionTimer.finished(7000)) {
            baritone.getPathingBehavior().cancelEverything();
            state = State.WAIT_XRAY;
            actionTimer.reset();
            log("Retreat done, waiting XRay packets.");
        }
    }

    private void handleWaitXray() {
        applyMiningRestrictions();
        if (!actionTimer.finished(xrayWaitMs.getValue())) return;

        collectDebrisFromXray();
        log("Ancient debris found: " + debrisQueue.size());

        if (debrisQueue.isEmpty()) {
            nextBlastPos = chooseNextBlastPosition();
            state = State.MOVE_NEXT;
            actionTimer.reset();
            return;
        }

        state = State.MINE_DEBRIS;
        actionTimer.reset();
    }

    private void handleMineDebris() {
        applyMiningRestrictions();
        ensurePickaxeInHand();
        cleanupDebrisQueue();

        if (debrisQueue.isEmpty()) {
            nextBlastPos = chooseNextBlastPosition();
            state = State.MOVE_NEXT;
            actionTimer.reset();
            mineCommandActive = false;
            cancelMineProcessOnly();
            log("Debris mined in cycle. Total: " + totalDebrisMined + ". Moving to next blast point.");
            return;
        }

        if (currentDebris == null || !debrisQueue.contains(currentDebris)) {
            currentDebris = debrisQueue.get(0);
            mineCommandActive = false;
            mineRetryTimer.reset();
        }

        if (mc.world.getBlockState(currentDebris).getBlock() != Blocks.ANCIENT_DEBRIS) {
            debrisQueue.remove(currentDebris);
            allowedDebris.remove(currentDebris);
            currentDebris = null;
            mineCommandActive = false;
            totalDebrisMined++;
            cancelMineProcessOnly();
            return;
        }

        IBaritone baritone = getBaritone();
        if (baritone == null) return;

        double reach = mineReach.getValue();
        double distSq = mc.player.getDistanceSq(Vector3d.copyCentered(currentDebris));
        if (distSq > reach * reach) {
            cancelMineProcessOnly();
            mineCommandActive = false;
            baritone.getCustomGoalProcess().setGoalAndPath(new GoalBlock(currentDebris));
            return;
        }

        // We reached the highlighted debris: stop travel path once, then let mine process run.
        if (!mineCommandActive) {
            baritone.getPathingBehavior().cancelEverything();
        }

        if (!mineCommandActive || mineRetryTimer.finished(2200)) {
            startMineForAllowedDebris();
            mineCommandActive = true;
            mineRetryTimer.reset();
        }

        if (mineHitTimer.finished(1800)) {
            if (logTimer.finished(1200)) {
                log("Mining by #mine at " + posToStr(currentDebris));
                logTimer.reset();
            }
            mineHitTimer.reset();
        }
    }

    private void handleMoveNext() {
        applyGotoRestrictions();

        if (nextBlastPos == null) {
            nextBlastPos = chooseNextBlastPosition();
            if (nextBlastPos == null) {
                state = State.CHECK;
                return;
            }
        }

        IBaritone baritone = getBaritone();
        if (baritone == null) return;

        baritone.getCustomGoalProcess().setGoalAndPath(new GoalBlock(nextBlastPos));
        double distSq = mc.player.getDistanceSq(Vector3d.copyCentered(nextBlastPos));
        if (distSq <= 10.0D || actionTimer.finished(12000)) {
            baritone.getPathingBehavior().cancelEverything();
            mc.player.sendChatMessage("#stop");
            state = State.CHECK;
            actionTimer.reset();
            log("Arrived next point " + posToStr(nextBlastPos));
        }
    }

    private void handleStuckAndLava() {
        if (state == State.EAT || mc.player == null) return;

        BlockPos now = mc.player.getPosition();
        if (progressPos == null) {
            progressPos = now;
            stuckTimer.reset();
            return;
        }

        if (progressPos.distanceSq(now) > 1.0D) {
            progressPos = now;
            stuckTimer.reset();
            return;
        }

        boolean movingState = state == State.RETREAT || state == State.MOVE_NEXT || state == State.MINE_DEBRIS;
        if (!movingState) return;

        if (mc.player.isInLava() && stuckTimer.finished(1200)) {
            BlockPos safe = findNearestSafePos(8);
            if (safe != null) {
                cancelMineProcessOnly();
                IBaritone b = getBaritone();
                if (b != null) b.getCustomGoalProcess().setGoalAndPath(new GoalBlock(safe));
                state = State.MOVE_NEXT;
                nextBlastPos = safe;
                mineCommandActive = false;
                log("In lava -> escape to " + posToStr(safe));
                stuckTimer.reset();
                return;
            }
        }

        if (stuckTimer.finished(7000)) {
            cancelMineProcessOnly();
            IBaritone b = getBaritone();
            if (b != null) b.getPathingBehavior().cancelEverything();
            state = State.MOVE_NEXT;
            nextBlastPos = chooseNextBlastPosition();
            mineCommandActive = false;
            actionTimer.reset();
            log("Anti-stuck: reroute to next position.");
            stuckTimer.reset();
        }
    }

    private void handleEat() {
        applyGotoRestrictions();
        stopPathingOnly();

        int foodSlot = findEdibleHotbar();
        if (foodSlot != -1) {
            selectHotbarSlot(foodSlot);
            mc.gameSettings.keyBindUseItem.setPressed(true);
        } else {
            mc.gameSettings.keyBindUseItem.setPressed(false);
            if (logTimer.finished(1200)) {
                log("No food in hotbar.");
                logTimer.reset();
            }
        }

        if (!isHungry()) {
            mc.gameSettings.keyBindUseItem.setPressed(false);
            state = (stateBeforeEat == State.EAT ? State.CHECK : stateBeforeEat);
            actionTimer.reset();
            log("Food restored. Resume from state: " + state.name());
        }
    }

    private void enterEatState() {
        if (state == State.EAT) return;
        stateBeforeEat = state;
        mc.player.sendChatMessage("#stop");
        state = State.EAT;
        actionTimer.reset();
        log("Hunger detected. Pause and eat.");
    }

    private String validateRequirements() {
        if (!isInNether()) return "not in Nether";
        if (isBasaltDeltas()) return "Basalt Deltas biome";

        int y = mc.player.getPosition().getY();
        int min = Math.min(minY.getValue().intValue(), maxY.getValue().intValue());
        int max = Math.max(minY.getValue().intValue(), maxY.getValue().intValue());
        if (y < min || y > max) return "Y out of range (" + min + "-" + max + ")";

        if (!hasFullDiamondOrNetheriteArmor()) return "need full diamond/netherite armor";
        if (!mc.player.isPotionActive(Effects.FIRE_RESISTANCE)) return "need fire resistance";
        if (!hasBlockStackInHotbar()) return "need stack of blocks in hotbar";
        if (findPickaxeHotbar() == -1) return "need diamond/netherite pickaxe in hotbar";
        if (findTntHotbar() == -1) return "need TNT in hotbar";
        if (findFlintHotbar() == -1) return "need flint and steel in hotbar";
        return null;
    }

    private boolean hasFullDiamondOrNetheriteArmor() {
        ItemStack head = mc.player.getItemStackFromSlot(EquipmentSlotType.HEAD);
        ItemStack chest = mc.player.getItemStackFromSlot(EquipmentSlotType.CHEST);
        ItemStack legs = mc.player.getItemStackFromSlot(EquipmentSlotType.LEGS);
        ItemStack feet = mc.player.getItemStackFromSlot(EquipmentSlotType.FEET);
        return isDiamondOrNetheriteArmor(head.getItem())
                && isDiamondOrNetheriteArmor(chest.getItem())
                && isDiamondOrNetheriteArmor(legs.getItem())
                && isDiamondOrNetheriteArmor(feet.getItem());
    }

    private boolean isDiamondOrNetheriteArmor(Item item) {
        return item == Items.DIAMOND_HELMET
                || item == Items.DIAMOND_CHESTPLATE
                || item == Items.DIAMOND_LEGGINGS
                || item == Items.DIAMOND_BOOTS
                || item == Items.NETHERITE_HELMET
                || item == Items.NETHERITE_CHESTPLATE
                || item == Items.NETHERITE_LEGGINGS
                || item == Items.NETHERITE_BOOTS;
    }

    private boolean hasBlockStackInHotbar() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getCount() >= 64 && stack.getItem() instanceof BlockItem) {
                return true;
            }
        }
        return false;
    }

    private int findPickaxeHotbar() {
        for (int i = 0; i < 9; i++) {
            Item item = mc.player.inventory.getStackInSlot(i).getItem();
            if (item == Items.DIAMOND_PICKAXE || item == Items.NETHERITE_PICKAXE) return i;
        }
        return -1;
    }

    private int findTntHotbar() {
        return findHotbarSlot(stack -> stack.getItem() == Items.TNT);
    }

    private int findFlintHotbar() {
        return findHotbarSlot(stack -> stack.getItem() == Items.FLINT_AND_STEEL);
    }

    private int findEdibleHotbar() {
        return findHotbarSlot(stack -> !stack.isEmpty() && stack.getItem().isFood());
    }

    private BlockPos findBestTntPlacement() {
        BlockPos base = mc.player.getPosition();
        BlockPos[] candidates = new BlockPos[]{
                base.add(1, 0, 0),
                base.add(-1, 0, 0),
                base.add(0, 0, 1),
                base.add(0, 0, -1),
                base.add(1, 0, 1),
                base.add(-1, 0, -1),
                base.add(1, 0, -1),
                base.add(-1, 0, 1)
        };

        BlockPos best = null;
        double bestDistSq = Double.MAX_VALUE;
        for (BlockPos pos : candidates) {
            if (!canPlaceTntAt(pos)) continue;
            double d = mc.player.getDistanceSq(Vector3d.copyCentered(pos));
            if (d < bestDistSq) {
                bestDistSq = d;
                best = pos.toImmutable();
            }
        }
        return best;
    }

    private boolean canPlaceTntAt(BlockPos pos) {
        if (!mc.world.getBlockState(pos).isAir()) return false;
        if (getPlaceInfo(pos) == null) return false;
        if (!mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos)).isEmpty()) return false;
        return !mc.player.getBoundingBox().intersects(new AxisAlignedBB(pos));
    }

    private PlaceInfo getPlaceInfo(BlockPos pos) {
        Direction[] directions = {
                Direction.DOWN, Direction.UP,
                Direction.NORTH, Direction.SOUTH,
                Direction.EAST, Direction.WEST
        };

        for (Direction direction : directions) {
            BlockPos neighbor = pos.offset(direction);
            Block block = mc.world.getBlockState(neighbor).getBlock();
            if (block == Blocks.AIR || block == Blocks.WATER || block == Blocks.LAVA || block == Blocks.FIRE) {
                continue;
            }

            Vector3d hitVec = new Vector3d(
                    neighbor.getX() + 0.5 + direction.getOpposite().getXOffset() * 0.5,
                    neighbor.getY() + 0.5 + direction.getOpposite().getYOffset() * 0.5,
                    neighbor.getZ() + 0.5 + direction.getOpposite().getZOffset() * 0.5
            );
            return new PlaceInfo(neighbor, direction.getOpposite(), hitVec);
        }

        return null;
    }

    private int findHotbarSlot(java.util.function.Predicate<ItemStack> predicate) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (predicate.test(stack)) return i;
        }
        return -1;
    }

    private void ensurePickaxeInHand() {
        int pickaxeSlot = findPickaxeHotbar();
        if (pickaxeSlot != -1) selectHotbarSlot(pickaxeSlot);
    }

    private void selectHotbarSlot(int slot) {
        if (slot < 0 || slot > 8) return;
        if (mc.player.inventory.currentItem != slot) {
            mc.player.inventory.currentItem = slot;
        }
    }

    private boolean placeTntAt(BlockPos placePos) {
        if (placePos == null) return false;
        if (!canPlaceTntAt(placePos)) return false;

        int tntSlot = findTntHotbar();
        if (tntSlot == -1) return false;
        PlaceInfo info = getPlaceInfo(placePos);
        if (info == null) return false;

        int previousSlot = mc.player.inventory.currentItem;
        if (previousSlot != tntSlot) {
            mc.player.inventory.currentItem = tntSlot;
            mc.player.connection.sendPacket(new CHeldItemChangePacket(tntSlot));
        }

        BlockRayTraceResult result = new BlockRayTraceResult(info.hitVec, info.direction, info.neighbor, false);
        faceVec(info.hitVec);
        ActionResultType action = mc.playerController.processRightClickBlock(mc.player, mc.world, Hand.MAIN_HAND, result);
        if (action == ActionResultType.FAIL) {
            mc.player.connection.sendPacket(new CPlayerTryUseItemOnBlockPacket(Hand.MAIN_HAND, result));
        }
        mc.player.connection.sendPacket(new CAnimateHandPacket(Hand.MAIN_HAND));
        mc.player.swingArm(Hand.MAIN_HAND);

        if (previousSlot != tntSlot) {
            mc.player.inventory.currentItem = previousSlot;
            mc.player.connection.sendPacket(new CHeldItemChangePacket(previousSlot));
        }

        return mc.world.getBlockState(placePos).getBlock() == Blocks.TNT;
    }

    private boolean igniteTntAt(BlockPos pos) {
        int flintSlot = findFlintHotbar();
        if (flintSlot == -1) return false;

        BlockState state = mc.world.getBlockState(pos);
        if (state.getBlock() != Blocks.TNT) {
            return hasNearbyPrimedTnt(pos);
        }

        int previousSlot = mc.player.inventory.currentItem;
        if (previousSlot != flintSlot) {
            mc.player.inventory.currentItem = flintSlot;
            mc.player.connection.sendPacket(new CHeldItemChangePacket(flintSlot));
        }

        Vector3d hitVec = new Vector3d(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
        BlockRayTraceResult ray = new BlockRayTraceResult(hitVec, Direction.UP, pos, false);
        faceVec(hitVec);
        ActionResultType result = mc.playerController.processRightClickBlock(mc.player, mc.world, Hand.MAIN_HAND, ray);
        if (result == ActionResultType.FAIL) {
            mc.player.connection.sendPacket(new CPlayerTryUseItemOnBlockPacket(Hand.MAIN_HAND, ray));
        }
        mc.player.connection.sendPacket(new CAnimateHandPacket(Hand.MAIN_HAND));
        mc.player.swingArm(Hand.MAIN_HAND);

        if (previousSlot != flintSlot) {
            mc.player.inventory.currentItem = previousSlot;
            mc.player.connection.sendPacket(new CHeldItemChangePacket(previousSlot));
        }

        return result != ActionResultType.FAIL || hasNearbyPrimedTnt(pos);
    }

    private boolean hasNearbyPrimedTnt(BlockPos origin) {
        for (net.minecraft.entity.Entity entity : mc.world.getAllEntities()) {
            if (entity instanceof TNTEntity && entity.getDistanceSq(Vector3d.copyCentered(origin)) < 16.0D) {
                return true;
            }
        }
        return false;
    }

    private void collectDebrisFromXray() {
        debrisQueue.clear();
        allowedDebris.clear();
        currentDebris = null;
        mineCommandActive = false;
        cancelMineProcessOnly();

        XRay xray = Instance.get(XRay.class);
        if (xray == null) return;

        BlockPos center = lastBlastPos != null ? lastBlastPos : mc.player.getPosition();
        int radius = searchRadius.getValue().intValue();
        int radiusSq = radius * radius;

        Set<BlockPos> unique = new HashSet<>();
        for (BlockPos pos : xray.spookyBlocks()) {
            if (pos == null) continue;
            if (pos.distanceSq(center) > radiusSq) continue;
            if (mc.world.getBlockState(pos).getBlock() != Blocks.ANCIENT_DEBRIS) continue;
            unique.add(pos.toImmutable());
        }

        debrisQueue.addAll(unique);
        allowedDebris.addAll(unique);
        debrisQueue.sort(Comparator.comparingDouble(p -> mc.player.getDistanceSq(Vector3d.copyCentered(p))));
    }

    private void cleanupDebrisQueue() {
        debrisQueue.removeIf(pos -> mc.world.getBlockState(pos).getBlock() != Blocks.ANCIENT_DEBRIS || !allowedDebris.contains(pos));
        debrisQueue.sort(Comparator.comparingDouble(p -> mc.player.getDistanceSq(Vector3d.copyCentered(p))));
    }

    private void startMineForAllowedDebris() {
        if (currentDebris == null || !allowedDebris.contains(currentDebris)) return;
        if (mc.world.getBlockState(currentDebris).getBlock() != Blocks.ANCIENT_DEBRIS) return;

        // Important: quantity must be 0, otherwise mine instantly stops if inventory already has debris.
        if (!mineCommandActive) {
            mc.player.sendChatMessage("#mine ancient_debris");
        }
        IBaritone baritone = getBaritone();
        if (baritone != null) {
            baritone.getMineProcess().mine(0, Blocks.ANCIENT_DEBRIS);
        }
    }

    private BlockPos computeRetreatGoal(int dist) {
        float yaw = mc.player.rotationYaw;
        double yawRad = Math.toRadians(yaw);
        int dx = (int) Math.round(-Math.sin(yawRad) * dist);
        int dz = (int) Math.round(Math.cos(yawRad) * dist);
        BlockPos from = mc.player.getPosition();
        return new BlockPos(from.getX() - dx, from.getY(), from.getZ() - dz);
    }

    private BlockPos chooseNextBlastPosition() {
        BlockPos from = lastBlastPos != null ? lastBlastPos : mc.player.getPosition();
        int minDist = Math.min(nextMinDistance.getValue().intValue(), nextMaxDistance.getValue().intValue());
        int maxDist = Math.max(nextMinDistance.getValue().intValue(), nextMaxDistance.getValue().intValue());

        BlockPos best = null;
        int bestScore = Integer.MAX_VALUE;

        for (int i = 0; i < 40; i++) {
            double angle = random.nextDouble() * (Math.PI * 2.0D);
            int dist = minDist + random.nextInt(Math.max(1, maxDist - minDist + 1));
            int x = from.getX() + (int) Math.round(Math.cos(angle) * dist);
            int z = from.getZ() + (int) Math.round(Math.sin(angle) * dist);

            int y = MathHelper.clamp(from.getY(), minY.getValue().intValue(), maxY.getValue().intValue());
            BlockPos candidate = new BlockPos(x, y, z);
            if (!isBlastCandidateValid(candidate)) continue;

            int lavaScore = countNearbyLava(candidate, 6);
            if (lavaScore < bestScore) {
                bestScore = lavaScore;
                best = candidate;
            }
        }

        if (best == null) {
            int fallbackX = from.getX() + 35 + random.nextInt(16);
            int fallbackZ = from.getZ() + 35 + random.nextInt(16);
            int y = MathHelper.clamp(from.getY(), minY.getValue().intValue(), maxY.getValue().intValue());
            best = new BlockPos(fallbackX, y, fallbackZ);
        }
        return best;
    }

    private boolean isBlastCandidateValid(BlockPos pos) {
        BlockState at = mc.world.getBlockState(pos);
        BlockState below = mc.world.getBlockState(pos.down());
        return at.isAir() && !below.isAir() && !below.getFluidState().isSource();
    }

    private BlockPos findNearestSafePos(int radius) {
        BlockPos base = mc.player.getPosition();
        BlockPos best = null;
        double bestDist = Double.MAX_VALUE;
        for (int x = base.getX() - radius; x <= base.getX() + radius; x++) {
            for (int y = base.getY() - 2; y <= base.getY() + 2; y++) {
                for (int z = base.getZ() - radius; z <= base.getZ() + radius; z++) {
                    BlockPos p = new BlockPos(x, y, z);
                    if (!mc.world.getBlockState(p).isAir()) continue;
                    if (mc.world.getBlockState(p).getFluidState().isSource()) continue;
                    BlockState below = mc.world.getBlockState(p.down());
                    if (below.isAir() || below.getBlock() == Blocks.LAVA) continue;
                    if (mc.world.getBlockState(p.up()).getBlock() == Blocks.LAVA) continue;
                    double d = mc.player.getDistanceSq(Vector3d.copyCentered(p));
                    if (d < bestDist) {
                        bestDist = d;
                        best = p.toImmutable();
                    }
                }
            }
        }
        return best;
    }

    private int countNearbyLava(BlockPos center, int radius) {
        int count = 0;
        for (int x = center.getX() - radius; x <= center.getX() + radius; x++) {
            for (int y = center.getY() - 2; y <= center.getY() + 2; y++) {
                for (int z = center.getZ() - radius; z <= center.getZ() + radius; z++) {
                    Block block = mc.world.getBlockState(new BlockPos(x, y, z)).getBlock();
                    if (block == Blocks.LAVA) count++;
                }
            }
        }
        return count;
    }

    private boolean isHungry() {
        return mc.player.getFoodStats().getFoodLevel() < 20;
    }

    private boolean isInNether() {
        return mc.world.getDimensionKey() != null
                && mc.world.getDimensionKey().getLocation() != null
                && mc.world.getDimensionKey().getLocation().getPath().contains("nether");
    }

    private boolean isBasaltDeltas() {
        Biome biome = mc.world.getBiome(mc.player.getPosition());
        ResourceLocation id = mc.world.func_241828_r().getRegistry(Registry.BIOME_KEY).getKey(biome);
        if (id == null) return false;
        return id.getPath().contains("basalt_deltas");
    }

    private void ensureXraySpookyEnabled() {
        XRay xray = Instance.get(XRay.class);
        if (xray == null) return;

        if (!xrayStateCaptured) {
            xrayWasEnabled = xray.isEnabled();
            xrayPrevMode = xray.mode().getValue();
            xrayStateCaptured = true;
        }

        if (!xray.isEnabled()) {
            xray.setEnabled(true, false);
        }
        if (!xray.mode().is("Funtime-Spooky")) {
            xray.mode().set("Funtime-Spooky");
        }
    }

    private void restoreXrayState() {
        if (!xrayStateCaptured) return;
        XRay xray = Instance.get(XRay.class);
        if (xray == null) return;

        xray.mode().set(xrayPrevMode);
        if (!xrayWasEnabled && xray.isEnabled()) {
            xray.setEnabled(false, false);
        }
    }

    private IBaritone getBaritone() {
        return BaritoneAPI.getProvider().getBaritoneForPlayer(mc.player);
    }

    private void stopPathingOnly() {
        IBaritone baritone = getBaritone();
        if (baritone != null) {
            baritone.getMineProcess().cancel();
            baritone.getPathingBehavior().cancelEverything();
        }
    }

    private void cancelMineProcessOnly() {
        IBaritone baritone = getBaritone();
        if (baritone != null) {
            baritone.getMineProcess().cancel();
        }
    }

    private void stopEverything() {
        stopPathingOnly();
        if (mc.player != null) {
            mc.player.sendChatMessage("#stop");
        }
    }

    private void captureOriginalBaritoneSettings() {
        if (originalCaptured) return;
        prevAllowBreak = BaritoneAPI.getSettings().allowBreak.value;
        prevAllowPlace = BaritoneAPI.getSettings().allowPlace.value;
        prevAllowParkourAscend = BaritoneAPI.getSettings().allowParkourAscend.value;
        prevAllowWaterBucketFall = BaritoneAPI.getSettings().allowWaterBucketFall.value;
        prevThrowawayItems = new ArrayList<>(BaritoneAPI.getSettings().acceptableThrowawayItems.value);
        originalCaptured = true;
    }

    private void applyGotoRestrictions() {
        BaritoneAPI.getSettings().allowBreak.value = false;
        BaritoneAPI.getSettings().allowPlace.value = false;
        BaritoneAPI.getSettings().allowParkourAscend.value = false;
        BaritoneAPI.getSettings().allowWaterBucketFall.value = false;
        BaritoneAPI.getSettings().acceptableThrowawayItems.value.clear();
    }

    private void applyMiningRestrictions() {
        BaritoneAPI.getSettings().allowBreak.value = true;
        BaritoneAPI.getSettings().allowPlace.value = false;
        BaritoneAPI.getSettings().allowParkourAscend.value = false;
        BaritoneAPI.getSettings().allowWaterBucketFall.value = false;
        BaritoneAPI.getSettings().acceptableThrowawayItems.value.clear();
    }

    private void restoreOriginalBaritoneSettings() {
        if (!originalCaptured) return;
        BaritoneAPI.getSettings().allowBreak.value = prevAllowBreak;
        BaritoneAPI.getSettings().allowPlace.value = prevAllowPlace;
        BaritoneAPI.getSettings().allowParkourAscend.value = prevAllowParkourAscend;
        BaritoneAPI.getSettings().allowWaterBucketFall.value = prevAllowWaterBucketFall;
        BaritoneAPI.getSettings().acceptableThrowawayItems.value.clear();
        BaritoneAPI.getSettings().acceptableThrowawayItems.value.addAll(prevThrowawayItems);
    }

    private String posToStr(BlockPos pos) {
        return pos.getX() + " " + pos.getY() + " " + pos.getZ();
    }

    private void log(String text) {
        if (!debug.getValue()) return;
        ChatUtil.addText("[AncientBot] " + text);
    }

    private void faceVec(Vector3d target) {
        Vector3d eyes = mc.player.getEyePosition(1.0F);
        Vector3d diff = target.subtract(eyes);
        double h = Math.sqrt(diff.x * diff.x + diff.z * diff.z);
        float yaw = (float) (Math.toDegrees(Math.atan2(diff.z, diff.x)) - 90.0D);
        float pitch = (float) -Math.toDegrees(Math.atan2(diff.y, h));
        pitch = MathHelper.clamp(pitch, -90.0F, 90.0F);
        mc.player.rotationYaw = yaw;
        mc.player.rotationPitch = pitch;
        mc.player.rotationYawHead = yaw;
    }

    private record PlaceInfo(BlockPos neighbor, Direction direction, Vector3d hitVec) {
    }
}
