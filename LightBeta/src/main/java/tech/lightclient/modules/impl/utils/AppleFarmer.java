package tech.lightclient.modules.impl.utils;
import tech.lightclient.client.events.impl.client.UpdateEvent;
import tech.lightclient.client.utils.PriceParser;
import tech.lightclient.client.utils.eventbus.EventHandler;
import tech.lightclient.client.utils.player.PlayerInventoryUtil;
import tech.lightclient.client.utils.player.rotation.Rotation;
import tech.lightclient.client.utils.player.rotation.RotationTask;
import tech.lightclient.modules.api.Category;
import tech.lightclient.modules.api.Function;
import tech.lightclient.modules.api.FunctionAdd;
import tech.lightclient.modules.api.settings.impl.BooleanSetting;
import tech.lightclient.modules.api.settings.impl.SliderSetting;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.AxeItem;
import net.minecraft.item.HoeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@FunctionAdd(name = "Apple Farmer", alias = "Apple Farmer", category = Category.Utilities, description = "modules.descriptions.apple_farmer")
public class AppleFarmer extends Function {
    private static final long ACTION_DELAY_MS = 90L;
    private static final long AUCTION_RETRY_MS = 2400L;
    private static final long BREAK_RETARGET_DELAY_MS = 12L;
    private static final long LEAF_BREAK_DELAY_MS = 0L;
    private static final double BREAK_REACH = 4.35D;
    private static final double REPAIR_START_PERCENT = 10.0D;
    private static final int REPAIR_FINISH_DAMAGE = 2;
    private static final float REPAIR_PITCH = 88.0F;
    private static final float LOOK_YAW_STEP = 38.0F;
    private static final float LOOK_PITCH_STEP = 34.0F;
    private static final float BREAK_LOOK_YAW_STEP = 72.0F;
    private static final float BREAK_LOOK_PITCH_STEP = 64.0F;
    private static final float LEAF_LOOK_YAW_STEP = 120.0F;
    private static final float LEAF_LOOK_PITCH_STEP = 100.0F;
    private static final float LOOK_RETURN_STEP = 42.0F;
    private static final float LOOK_YAW_EPSILON = 4.0F;
    private static final float LOOK_PITCH_EPSILON = 4.0F;
    private static final float LEAF_LOOK_EPSILON = 6.0F;
    private static final long LEAF_RETARGET_DELAY_MS = 18L;
    private static final float BREAK_SHAKE_YAW = 2.1F;
    private static final float BREAK_SHAKE_PITCH = 1.2F;

    private final BooleanSetting autoSellLogs = new BooleanSetting("\u0410\u0432\u0442\u043e\u043f\u0440\u043e\u0434\u0430\u0436\u0430 \u0431\u0440\u0435\u0432\u0435\u043d", false);
    private final BooleanSetting autoRepair = new BooleanSetting("\u0410\u0432\u0442\u043e\u043f\u043e\u0447\u0438\u043d\u043a\u0430", false);
    private final SliderSetting scanRadius = new SliderSetting("\u0420\u0430\u0434\u0438\u0443\u0441", 7.0F, 3.0F, 12.0F, 1.0F)
        .setPostfix(" \u0431\u043b\u043e\u043a\u043e\u0432");

    private final PriceParser priceParser = new PriceParser();
    private final tech.lightclient.client.utils.math.time.StopWatch actionTimer = new tech.lightclient.client.utils.math.time.StopWatch();
    private AppleFarmerStage stage = AppleFarmerStage.FIND_SPOT;
    private BlockPos anchorPos;
    private BlockPos plantPos;
    private BlockPos chestPos;
    private long stageStartedAt;
    private final Set<BlockPos> skippedBreakTargets = new HashSet<>();
    private int cachedLogSellPrice;
    private boolean sellSearchIssued;
    private int repairTargetSlot = -1;
    private boolean repairSearchIssued;
    private BlockPos currentBreakTarget;
    private Direction currentBreakDirection;
    private boolean currentBreakLeaves;
    private BlockPos aimedBreakTarget;
    private long aimedBreakTargetAt;
    private long breakPausedUntil;
    private long lastLeafBreakAt;
    private Rotation returnRotation;
    private boolean usedRotationThisTick;
    private long lastRotationUseAt;

    public AppleFarmer() {
        this.addSettings(this.autoSellLogs, this.autoRepair, this.scanRadius);
    }

    @Override
    public void onEnable() {
        if (mc.player == null || mc.world == null) {
            this.setState(false);
            return;
        }

        super.onEnable();
        this.anchorPos = mc.player.getBlockPos().toImmutable();
        this.chestPos = this.findNearestChest();
        this.switchStage(AppleFarmerStage.FIND_SPOT);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.setBreakingMovement(false);
        this.stage = AppleFarmerStage.FIND_SPOT;
        this.anchorPos = null;
        this.plantPos = null;
        this.resetBreaking();
        this.skippedBreakTargets.clear();
        this.returnRotation = null;
        this.usedRotationThisTick = false;
        this.lastRotationUseAt = 0L;
        this.cachedLogSellPrice = 0;
        this.sellSearchIssued = false;
        this.repairTargetSlot = -1;
        this.repairSearchIssued = false;
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) {
            return;
        }

        this.setBreakingMovement(false);
        this.usedRotationThisTick = false;

        switch (this.stage) {
            case FIND_SPOT -> this.tickFindSpot();
            case PLANT -> this.tickPlant();
            case GROW -> this.tickGrow();
            case BREAK_LEAVES -> this.tickBreakLeaves();
            case BREAK_LOGS -> this.tickBreakLogs();
            case STORE_AND_RESTOCK -> this.tickStoreAndRestock();
            case AUTO_SELL_LOGS, AUTO_REPAIR -> this.switchStage(AppleFarmerStage.FIND_SPOT);
        }

        this.returnRotationIfIdle();
    }

    private void tickFindSpot() {
        if (this.anchorPos == null) {
            this.anchorPos = mc.player.getBlockPos().toImmutable();
        }

        if (!this.hasItem(Items.OAK_SAPLING)) {
            sendMessage("modules.messages.apple_farmer.sapling_required");
            this.setState(false);
            return;
        }

        if (!this.hasItem(Items.BONE_MEAL)) {
            if (this.chestPos != null) {
                this.switchStage(AppleFarmerStage.STORE_AND_RESTOCK);
                return;
            }
            sendMessage("modules.messages.apple_farmer.bone_meal_required");
            this.setState(false);
            return;
        }

        this.plantPos = this.findPlantSpot();
        if (this.plantPos == null) {
            sendMessage("modules.messages.apple_farmer.no_space");
            this.setState(false);
            return;
        }

        this.switchStage(AppleFarmerStage.PLANT);
    }

    private void tickPlant() {
        if (this.plantPos == null) {
            this.switchStage(AppleFarmerStage.FIND_SPOT);
            return;
        }

        BlockState state = mc.world.getBlockState(this.plantPos);
        if (state.getBlock() == Blocks.OAK_SAPLING) {
            this.switchStage(AppleFarmerStage.GROW);
            return;
        }

        if (!state.isAir()) {
            this.switchStage(AppleFarmerStage.FIND_SPOT);
            return;
        }

        if (!this.actionTimer.hasReached(ACTION_DELAY_MS) || !this.selectItem(Items.OAK_SAPLING)) {
            return;
        }

        BlockPos ground = this.plantPos.down();
        if (!this.rotateToBlock(ground)) {
            return;
        }
        BlockHitResult hit = new BlockHitResult(Vec3d.ofCenter(ground).add(0.0D, 0.5D, 0.0D), Direction.UP, ground, false);
        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hit);
        mc.player.swingHand(Hand.MAIN_HAND);
        this.actionTimer.reset();
    }

    private void tickGrow() {
        if (this.plantPos == null) {
            this.switchStage(AppleFarmerStage.FIND_SPOT);
            return;
        }

        if (this.hasTreeAtPlant()) {
            this.switchStage(AppleFarmerStage.BREAK_LEAVES);
            return;
        }

        if (!this.hasItem(Items.BONE_MEAL)) {
            this.switchStage(AppleFarmerStage.STORE_AND_RESTOCK);
            return;
        }

        if (!this.actionTimer.hasReached(ACTION_DELAY_MS) || !this.selectItem(Items.BONE_MEAL)) {
            return;
        }

        if (!this.rotateToBlock(this.plantPos)) {
            return;
        }
        BlockHitResult hit = new BlockHitResult(Vec3d.ofCenter(this.plantPos), Direction.UP, this.plantPos, false);
        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hit);
        mc.player.swingHand(Hand.MAIN_HAND);
        this.actionTimer.reset();

        if (System.currentTimeMillis() - this.stageStartedAt > 8000L && !this.hasTreeAtPlant()) {
            this.switchStage(AppleFarmerStage.FIND_SPOT);
        }
    }

    private void tickBreakLeaves() {
        this.setBreakingMovement(false);
        if (this.isBreakPaused()) {
            return;
        }

        BlockPos activeLeaf = this.getActiveBreakTarget(true);
        if (activeLeaf != null) {
            this.breakTreeBlock(activeLeaf, HoeItem.class, true);
            return;
        }

        BlockPos leaf = this.findClosestTreeBlock(true);
        if (leaf == null) {
            this.switchStage(AppleFarmerStage.BREAK_LOGS);
            return;
        }

        this.breakTreeBlock(leaf, HoeItem.class, true);
    }

    private void tickBreakLogs() {
        this.setBreakingMovement(false);
        if (this.isBreakPaused()) {
            return;
        }

        BlockPos activeLog = this.getActiveBreakTarget(false);
        if (activeLog != null) {
            this.breakTreeBlock(activeLog, AxeItem.class, false);
            return;
        }

        BlockPos log = this.findClosestTreeBlock(false);
        if (log == null) {
            this.switchStage(AppleFarmerStage.FIND_SPOT);
            return;
        }

        this.breakTreeBlock(log, AxeItem.class, false);
    }

    private void tickStoreAndRestock() {
        this.chestPos = this.chestPos != null ? this.chestPos : this.findNearestChest();
        if (this.chestPos == null) {
            this.switchStage(AppleFarmerStage.FIND_SPOT);
            return;
        }

        if (!this.canReachBlock(this.chestPos)) {
            this.switchStage(AppleFarmerStage.FIND_SPOT);
            return;
        }

        if (!(mc.currentScreen instanceof GenericContainerScreen) || !(mc.player.currentScreenHandler instanceof GenericContainerScreenHandler handler)) {
            this.openChest(this.chestPos);
            return;
        }

        if (!this.actionTimer.hasReached(ACTION_DELAY_MS)) {
            return;
        }

        boolean moved = false;
        int containerSlots = Math.max(0, handler.slots.size() - 36);
        for (int i = containerSlots; i < handler.slots.size(); i++) {
            Slot slot = handler.getSlot(i);
            if (slot != null && slot.hasStack() && slot.getStack().getItem() == Items.APPLE) {
                mc.interactionManager.clickSlot(handler.syncId, slot.id, 0, SlotActionType.QUICK_MOVE, mc.player);
                moved = true;
                break;
            }
        }

        if (!moved && !this.hasItem(Items.BONE_MEAL)) {
            for (int i = 0; i < containerSlots; i++) {
                Slot slot = handler.getSlot(i);
                if (slot != null && slot.hasStack() && slot.getStack().getItem() == Items.BONE_MEAL) {
                    mc.interactionManager.clickSlot(handler.syncId, slot.id, 0, SlotActionType.QUICK_MOVE, mc.player);
                    moved = true;
                    break;
                }
            }
        }

        this.actionTimer.reset();
        if (!moved) {
            mc.player.closeHandledScreen();
            this.switchStage(AppleFarmerStage.FIND_SPOT);
        }
    }

    private void tickAutoSellLogs() {
        if (!this.autoSellLogs.get() || !this.hasFullLogStack()) {
            this.resetSellState();
            this.switchStage(AppleFarmerStage.STORE_AND_RESTOCK);
            return;
        }

        if (this.cachedLogSellPrice <= 0) {
            if (!this.sellSearchIssued || System.currentTimeMillis() - this.stageStartedAt > AUCTION_RETRY_MS) {
                ItemStack log = this.findFirstFullLogStack();
                if (log == null || mc.player.networkHandler == null) {
                    this.switchStage(AppleFarmerStage.STORE_AND_RESTOCK);
                    return;
                }
                mc.player.networkHandler.sendChatCommand("ah search " + this.cleanSearchName(log));
                this.sellSearchIssued = true;
                this.stageStartedAt = System.currentTimeMillis();
                return;
            }

            if (mc.player.currentScreenHandler instanceof GenericContainerScreenHandler handler) {
                int price = this.findCheapestFullStackPrice(handler, true);
                if (price > 0) {
                    this.cachedLogSellPrice = price;
                    mc.player.closeHandledScreen();
                    this.actionTimer.reset();
                }
            }
            return;
        }

        if (mc.currentScreen != null) {
            mc.player.closeHandledScreen();
            return;
        }

        if (!this.actionTimer.hasReached(650L)) {
            return;
        }

        int stackSlot = this.findFullLogStackSlot();
        if (stackSlot == -1) {
            this.resetSellState();
            this.switchStage(AppleFarmerStage.STORE_AND_RESTOCK);
            return;
        }

        this.selectServerSlot(stackSlot);
        mc.player.networkHandler.sendChatCommand("ah sell " + this.cachedLogSellPrice);
        this.actionTimer.reset();
    }

    private void tickAutoRepair() {
        if (!this.autoRepair.get()) {
            this.switchStage(AppleFarmerStage.FIND_SPOT);
            return;
        }

        this.repairTargetSlot = this.findRepairToolSlotToFinish();
        if (this.repairTargetSlot == -1) {
            this.resetRepairState();
            this.switchStage(AppleFarmerStage.FIND_SPOT);
            return;
        }

        if (!this.hasItem(Items.EXPERIENCE_BOTTLE)) {
            this.buyRepairBottles();
            return;
        }

        if (mc.currentScreen != null) {
            mc.player.closeHandledScreen();
            return;
        }

        if (!this.actionTimer.hasReached(155L)) {
            return;
        }

        this.selectServerSlot(this.repairTargetSlot);
        if (!this.ensureBottlesInOffhand()) {
            return;
        }

        mc.player.setPitch(REPAIR_PITCH);
        mc.interactionManager.sendSequencedPacket(
            mc.world,
            sequence -> new PlayerInteractItemC2SPacket(Hand.OFF_HAND, sequence, mc.player.getYaw(), REPAIR_PITCH)
        );
        mc.player.swingHand(Hand.OFF_HAND);
        this.actionTimer.reset();
    }

    private void buyRepairBottles() {
        if (mc.player == null || mc.player.networkHandler == null) {
            return;
        }

        if (!this.repairSearchIssued || System.currentTimeMillis() - this.stageStartedAt > AUCTION_RETRY_MS) {
            mc.player.networkHandler.sendChatCommand("ah search Опыт");
            this.repairSearchIssued = true;
            this.stageStartedAt = System.currentTimeMillis();
            return;
        }

        if (!(mc.currentScreen instanceof GenericContainerScreen) || !(mc.player.currentScreenHandler instanceof GenericContainerScreenHandler handler)) {
            return;
        }

        if (!this.actionTimer.hasReached(ACTION_DELAY_MS)) {
            return;
        }

        int offerSlot = this.findCheapestExperienceBottleSlot(handler);
        if (offerSlot != -1) {
            mc.interactionManager.clickSlot(handler.syncId, offerSlot, 0, SlotActionType.PICKUP, mc.player);
            this.actionTimer.reset();
            return;
        }

        if (handler.slots.size() > 32 && this.isConfirmLikeScreen(handler)) {
            mc.interactionManager.clickSlot(handler.syncId, 32, 0, SlotActionType.PICKUP, mc.player);
            this.resetRepairState();
            this.actionTimer.reset();
        }
    }

    private boolean shouldStartRepair() {
        return this.findDamagedRepairToolSlot() != -1 && this.stage != AppleFarmerStage.AUTO_REPAIR;
    }

    private int findDamagedRepairToolSlot() {
        int axe = this.findDamagedRepairToolSlot(AxeItem.class);
        if (axe != -1) {
            return axe;
        }
        return this.findDamagedRepairToolSlot(HoeItem.class);
    }

    private int findRepairToolSlotToFinish() {
        int axe = this.findRepairToolSlotToFinish(AxeItem.class);
        if (axe != -1) {
            return axe;
        }
        return this.findRepairToolSlotToFinish(HoeItem.class);
    }

    private int findDamagedRepairToolSlot(Class<?> toolType) {
        for (int slot = 0; slot < 36; slot++) {
            ItemStack stack = mc.player.getInventory().getStack(slot);
            if (!stack.isEmpty()
                && toolType.isInstance(stack.getItem())
                && stack.isDamageable()
                && stack.getDamage() > REPAIR_FINISH_DAMAGE
                && this.hasMending(stack)
                && this.getDurabilityPercent(stack) <= REPAIR_START_PERCENT) {
                return this.toServerSlot(slot);
            }
        }
        return -1;
    }

    private int findRepairToolSlotToFinish(Class<?> toolType) {
        for (int slot = 0; slot < 36; slot++) {
            ItemStack stack = mc.player.getInventory().getStack(slot);
            if (!stack.isEmpty()
                && toolType.isInstance(stack.getItem())
                && stack.isDamageable()
                && stack.getDamage() > REPAIR_FINISH_DAMAGE
                && this.hasMending(stack)) {
                return this.toServerSlot(slot);
            }
        }
        return -1;
    }

    private boolean hasMending(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        net.minecraft.component.type.ItemEnchantmentsComponent enchantments = stack.get(net.minecraft.component.DataComponentTypes.ENCHANTMENTS);
        if (enchantments == null) {
            return false;
        }
        for (net.minecraft.registry.entry.RegistryEntry<net.minecraft.enchantment.Enchantment> enchantment : enchantments.getEnchantments()) {
            if (enchantment.matchesKey(Enchantments.MENDING)) {
                return true;
            }
        }
        return false;
    }

    private double getDurabilityPercent(ItemStack stack) {
        if (stack == null || stack.isEmpty() || !stack.isDamageable() || stack.getMaxDamage() <= 0) {
            return 100.0D;
        }
        return (double) (stack.getMaxDamage() - stack.getDamage()) / (double) stack.getMaxDamage() * 100.0D;
    }

    private boolean ensureBottlesInOffhand() {
        if (mc.player.getOffHandStack().getItem() == Items.EXPERIENCE_BOTTLE) {
            return true;
        }

        int bottleSlot = this.findInventorySlot(Items.EXPERIENCE_BOTTLE);
        if (bottleSlot == -1) {
            return false;
        }

        PlayerInventoryUtil.moveItem(bottleSlot, 45, false, false);
        return mc.player.getOffHandStack().getItem() == Items.EXPERIENCE_BOTTLE;
    }

    private int findCheapestExperienceBottleSlot(GenericContainerScreenHandler handler) {
        int bestSlot = -1;
        int bestPrice = Integer.MAX_VALUE;
        int containerSlots = Math.max(0, handler.slots.size() - 36);

        for (int i = 0; i < containerSlots; i++) {
            Slot slot = handler.getSlot(i);
            if (slot == null || !slot.hasStack()) {
                continue;
            }

            ItemStack stack = slot.getStack();
            if (stack.getItem() != Items.EXPERIENCE_BOTTLE || stack.getCount() < 64 || !this.isExperienceBottle(stack)) {
                continue;
            }

            int price = this.getAuctionPrice(handler, i, stack);
            if (price > 0 && price < bestPrice) {
                bestPrice = price;
                bestSlot = slot.id;
            }
        }

        return bestSlot;
    }

    private boolean isExperienceBottle(ItemStack stack) {
        String name = this.normalize(stack.getName().getString());
        if ((name.contains("пуз") && name.contains("опыт")) || (name.contains("experience") && name.contains("bottle"))) {
            return true;
        }
        return this.getTooltipLines(stack).stream().anyMatch(line ->
            (line.contains("пуз") && line.contains("опыт")) || (line.contains("experience") && line.contains("bottle"))
        );
    }

    private int findCheapestFullStackPrice(GenericContainerScreenHandler handler, boolean logsOnly) {
        int best = Integer.MAX_VALUE;
        int containerSlots = Math.max(0, handler.slots.size() - 36);
        for (int i = 0; i < containerSlots; i++) {
            Slot slot = handler.getSlot(i);
            if (slot == null || !slot.hasStack()) {
                continue;
            }

            ItemStack stack = slot.getStack();
            if (stack.getCount() < 64 || (logsOnly && !this.isOakLog(stack))) {
                continue;
            }

            int price = this.getAuctionPrice(handler, i, stack);
            if (price > 0 && price < best) {
                best = price;
            }
        }
        return best == Integer.MAX_VALUE ? -1 : best;
    }

    private int getAuctionPrice(GenericContainerScreenHandler handler, int slotIndex, ItemStack stack) {
        int price = this.priceParser.getPrice(stack);
        if (price > 0) {
            return price;
        }
        return this.priceParser.getPrice(handler.getSlot(slotIndex).getStack());
    }

    private boolean isConfirmLikeScreen(GenericContainerScreenHandler handler) {
        for (Slot slot : handler.slots) {
            if (slot != null && slot.hasStack()) {
                String name = this.normalize(slot.getStack().getName().getString());
                if (name.contains("подтверд") || name.contains("confirm") || name.contains("купить")) {
                    return true;
                }
            }
        }
        return false;
    }

    private BlockPos findPlantSpot() {
        BlockPos origin = this.anchorPos != null ? this.anchorPos : mc.player.getBlockPos();
        int radius = this.scanRadius.get().intValue();
        BlockPos nearest = null;
        double nearestDistance = Double.MAX_VALUE;

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = -2; y <= 2; y++) {
                    BlockPos pos = origin.add(x, y, z);
                    if (!this.isPlantSpotStateValid(pos) || !this.isPlantGround(pos.down())) {
                        continue;
                    }
                    if (!this.canReachPlantGround(pos.down())) {
                        continue;
                    }

                    double distance = pos.getSquaredDistance(origin);
                    if (distance < nearestDistance) {
                        nearestDistance = distance;
                        nearest = pos.toImmutable();
                    }
                }
            }
        }

        return nearest;
    }

    private boolean isPlantSpotStateValid(BlockPos pos) {
        Block block = mc.world.getBlockState(pos).getBlock();
        return block == Blocks.AIR || block == Blocks.OAK_SAPLING;
    }

    private boolean canReachPlantGround(BlockPos ground) {
        Vec3d eye = mc.player.getEyePos();
        Vec3d center = Vec3d.ofCenter(ground);
        if (eye.squaredDistanceTo(center) <= BREAK_REACH * BREAK_REACH) {
            return true;
        }

        Vec3d top = new Vec3d(ground.getX() + 0.5D, ground.getY() + 1.0D, ground.getZ() + 0.5D);
        return eye.squaredDistanceTo(top) <= (BREAK_REACH + 0.75D) * (BREAK_REACH + 0.75D);
    }

    private boolean isPlantGround(BlockPos pos) {
        Block block = mc.world.getBlockState(pos).getBlock();
        return block == Blocks.GRASS_BLOCK
            || block == Blocks.DIRT
            || block == Blocks.COARSE_DIRT
            || block == Blocks.ROOTED_DIRT
            || block == Blocks.PODZOL
            || block == Blocks.MOSS_BLOCK;
    }

    private boolean hasTreeAtPlant() {
        if (this.plantPos == null) {
            return false;
        }

        for (int y = 0; y <= 7; y++) {
            if (mc.world.getBlockState(this.plantPos.up(y)).getBlock() == Blocks.OAK_LOG) {
                return true;
            }
        }
        return false;
    }

    private BlockPos findClosestTreeBlock(boolean leaves) {
        if (this.plantPos == null) {
            return null;
        }

        int radius = leaves ? 5 : 2;
        List<BlockPos> targets = new ArrayList<>();
        for (int x = -radius; x <= radius; x++) {
            for (int y = 0; y <= 8; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos pos = this.plantPos.add(x, y, z);
                    if (this.skippedBreakTargets.contains(pos)) {
                        continue;
                    }

                    Block block = mc.world.getBlockState(pos).getBlock();
                    if (leaves ? block == Blocks.OAK_LEAVES : block == Blocks.OAK_LOG || block == Blocks.OAK_WOOD) {
                        targets.add(pos.toImmutable());
                    }
                }
            }
        }

        return targets.stream()
            .filter(this::canReachBlock)
            .min(Comparator.comparingDouble(pos -> pos.getSquaredDistance(mc.player.getBlockPos())))
            .orElse(null);
    }

    private boolean isBreakPaused() {
        if (System.currentTimeMillis() < this.breakPausedUntil) {
            this.setAttackPressed(false);
            return true;
        }
        return false;
    }

    private BlockPos getActiveBreakTarget(boolean leaves) {
        if (this.currentBreakTarget == null) {
            return null;
        }

        BlockState state = mc.world.getBlockState(this.currentBreakTarget);
        boolean validTarget = leaves ? state.getBlock() == Blocks.OAK_LEAVES : state.getBlock() == Blocks.OAK_LOG || state.getBlock() == Blocks.OAK_WOOD;
        if (validTarget && this.canReachBlock(this.currentBreakTarget)) {
            return this.currentBreakTarget;
        }

        this.finishCurrentBreak();
        return null;
    }

    private void breakTreeBlock(BlockPos pos, Class<?> toolClass, boolean leaves) {
        if (pos == null || mc.player == null || mc.world == null || mc.interactionManager == null) {
            return;
        }

        if (leaves && System.currentTimeMillis() - this.lastLeafBreakAt < LEAF_BREAK_DELAY_MS) {
            this.setAttackPressed(false);
            return;
        }

        int toolSlot = this.findToolSlot(toolClass);
        if (toolSlot == -1) {
            return;
        }

        this.selectServerSlot(toolSlot);
        this.currentBreakLeaves = leaves;
        if (!this.prepareBreakTarget(pos)) {
            return;
        }

        this.breakBlock(pos);
    }

    private void breakBlock(BlockPos pos) {
        if (!this.rotateToBlock(pos)) {
            this.setAttackPressed(false);
            return;
        }
        this.applyBreakMovementJitter();
        BlockHitResult hit = this.updateCrosshairTarget(pos);
        if (hit == null) {
            this.aimedBreakTarget = null;
            this.aimedBreakTargetAt = 0L;
            this.setAttackPressed(false);
            return;
        }
        long now = System.currentTimeMillis();
        if (!pos.equals(this.aimedBreakTarget)) {
            this.aimedBreakTarget = pos.toImmutable();
            this.aimedBreakTargetAt = now;
            if (!this.currentBreakLeaves) {
                this.setAttackPressed(false);
                return;
            }
        }
        if (!this.currentBreakLeaves && now - this.aimedBreakTargetAt < 12L) {
            this.setAttackPressed(false);
            return;
        }

        this.currentBreakTarget = pos.toImmutable();
        this.currentBreakDirection = hit.getSide();
        this.setAttackPressed(false);
        mc.interactionManager.updateBlockBreakingProgress(pos, hit.getSide());
        mc.player.swingHand(Hand.MAIN_HAND);
    }

    private void resetBreaking() {
        this.finishCurrentBreak();
        this.aimedBreakTarget = null;
        this.aimedBreakTargetAt = 0L;
    }

    private void finishCurrentBreak() {
        this.setAttackPressed(false);
        if (this.currentBreakTarget != null) {
            if (this.currentBreakLeaves) {
                this.lastLeafBreakAt = System.currentTimeMillis();
            } else {
                this.setAttackPressed(false);
            }
            this.breakPausedUntil = this.currentBreakLeaves
                ? System.currentTimeMillis() + LEAF_RETARGET_DELAY_MS
                : System.currentTimeMillis() + BREAK_RETARGET_DELAY_MS;
        }
        this.currentBreakTarget = null;
        this.currentBreakDirection = null;
        this.currentBreakLeaves = false;
    }

    private BlockHitResult updateCrosshairTarget(BlockPos pos) {
        Vec3d start = mc.player.getEyePos();
        Vec3d end = start.add(Vec3d.fromPolar(this.getActiveRotation().pitch, this.getActiveRotation().yaw).multiply(BREAK_REACH));
        HitResult hit = mc.world.raycast(new RaycastContext(
            start,
            end,
            RaycastContext.ShapeType.OUTLINE,
            RaycastContext.FluidHandling.NONE,
            mc.player
        ));
        if (hit instanceof BlockHitResult blockHit && blockHit.getBlockPos().equals(pos)) {
            mc.crosshairTarget = blockHit;
            return blockHit;
        }
        return null;
    }

    private boolean prepareBreakTarget(BlockPos pos) {
        this.rotateToBlock(pos);
        if (this.canReachBlock(pos)) {
            return true;
        }

        this.resetBreaking();
        this.skipBreakTarget(pos);
        return false;
    }

    private boolean canReachBlock(BlockPos pos) {
        Vec3d eye = mc.player.getEyePos();
        for (Vec3d sample : this.getBlockAimSamples(pos)) {
            if (eye.squaredDistanceTo(sample) > BREAK_REACH * BREAK_REACH) {
                continue;
            }

            HitResult hit = mc.world.raycast(new RaycastContext(
                eye,
                sample,
                RaycastContext.ShapeType.OUTLINE,
                RaycastContext.FluidHandling.NONE,
                mc.player
            ));
            if (hit.getType() == HitResult.Type.MISS || hit instanceof BlockHitResult blockHit && blockHit.getBlockPos().equals(pos)) {
                return true;
            }
        }
        return false;
    }

    private void skipBreakTarget(BlockPos pos) {
        this.skippedBreakTargets.add(pos.toImmutable());
    }

    private boolean rotateToBlock(BlockPos pos) {
        Vec3d aimPoint = this.getBestBlockAimPoint(pos);
        Rotation rotation = this.getRotationTo(aimPoint);
        if (this.stage == AppleFarmerStage.BREAK_LEAVES || this.stage == AppleFarmerStage.BREAK_LOGS || this.currentBreakTarget != null) {
            rotation = this.applyBreakShake(rotation);
        }

        if (mc.player == null) {
            return false;
        }
        if (this.returnRotation == null) {
            this.returnRotation = new Rotation(mc.player.getYaw(), mc.player.getPitch());
        }
        this.usedRotationThisTick = true;
        this.lastRotationUseAt = System.currentTimeMillis();
        float yawStep = this.currentBreakLeaves ? LEAF_LOOK_YAW_STEP : BREAK_LOOK_YAW_STEP;
        float pitchStep = this.currentBreakLeaves ? LEAF_LOOK_PITCH_STEP : BREAK_LOOK_PITCH_STEP;
        float epsilon = this.currentBreakLeaves ? LEAF_LOOK_EPSILON : LOOK_YAW_EPSILON;
        RotationTask.setTargetRotation(rotation.yaw, rotation.pitch, yawStep, pitchStep, LOOK_RETURN_STEP, LOOK_RETURN_STEP, 40.0D, 80, 0L);
        Rotation current = new Rotation(RotationTask.visualHeadYaw, RotationTask.visualHeadPitch);
        return Math.abs(MathHelper.wrapDegrees(current.yaw - rotation.yaw)) <= epsilon
            && Math.abs(current.pitch - rotation.pitch) <= (this.currentBreakLeaves ? LEAF_LOOK_EPSILON : LOOK_PITCH_EPSILON);
    }

    private void returnRotationIfIdle() {
        if (this.returnRotation == null || this.usedRotationThisTick || mc.player == null) {
            return;
        }
        if (System.currentTimeMillis() - this.lastRotationUseAt < 80L) {
            return;
        }
        Rotation target = this.returnRotation;
        RotationTask.setTargetRotation(target.yaw, target.pitch, LOOK_RETURN_STEP, 80, 0L);
        Rotation current = new Rotation(RotationTask.visualHeadYaw, RotationTask.visualHeadPitch);
        if (Math.abs(MathHelper.wrapDegrees(current.yaw - target.yaw)) <= LOOK_YAW_EPSILON
            && Math.abs(current.pitch - target.pitch) <= LOOK_PITCH_EPSILON) {
            this.returnRotation = null;
        }
    }

    private Rotation getActiveRotation() {
        if (mc.player == null) {
            return new Rotation(0.0F, 0.0F);
        }
        return new Rotation(RotationTask.isRotating() ? RotationTask.visualHeadYaw : mc.player.getYaw(),
            RotationTask.isRotating() ? RotationTask.visualHeadPitch : mc.player.getPitch());
    }

    private Rotation getRotationTo(Vec3d target) {
        Vec3d eye = mc.player.getEyePos();
        double dx = target.x - eye.x;
        double dy = target.y - eye.y;
        double dz = target.z - eye.z;
        double horizontal = Math.sqrt(dx * dx + dz * dz);
        float yaw = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0D);
        float pitch = (float) -Math.toDegrees(Math.atan2(dy, horizontal));
        return new Rotation(MathHelper.wrapDegrees(yaw), MathHelper.clamp(pitch, -90.0F, 90.0F));
    }

    private Vec3d getBestBlockAimPoint(BlockPos pos) {
        Vec3d eye = mc.player.getEyePos();
        Vec3d best = Vec3d.ofCenter(pos);
        double bestDistance = Double.MAX_VALUE;

        for (Vec3d sample : this.getBlockAimSamples(pos)) {
            double distance = eye.squaredDistanceTo(sample);
            if (distance > BREAK_REACH * BREAK_REACH) {
                continue;
            }

            HitResult hit = mc.world.raycast(new RaycastContext(
                eye,
                sample,
                RaycastContext.ShapeType.OUTLINE,
                RaycastContext.FluidHandling.NONE,
                mc.player
            ));
            if (hit.getType() == HitResult.Type.MISS || hit instanceof BlockHitResult blockHit && blockHit.getBlockPos().equals(pos)) {
                if (distance < bestDistance) {
                    bestDistance = distance;
                    best = sample;
                }
            }
        }

        return best;
    }

    private List<Vec3d> getBlockAimSamples(BlockPos pos) {
        List<Vec3d> samples = new ArrayList<>();
        Vec3d center = Vec3d.ofCenter(pos);
        samples.add(center);

        for (Direction direction : Direction.values()) {
            samples.add(center.add(
                direction.getOffsetX() * 0.48D,
                direction.getOffsetY() * 0.48D,
                direction.getOffsetZ() * 0.48D
            ));
        }

        double[] offsets = new double[]{-0.28D, 0.0D, 0.28D};
        for (double ox : offsets) {
            for (double oy : offsets) {
                for (double oz : offsets) {
                    samples.add(center.add(ox, oy, oz));
                }
            }
        }

        return samples;
    }

    private void setBreakingMovement(boolean pressed) {
        if (mc.options == null) {
            return;
        }
        if (mc.options.forwardKey != null) {
            mc.options.forwardKey.setPressed(pressed);
        }
        if (mc.options.sprintKey != null) {
            mc.options.sprintKey.setPressed(pressed);
        }
        if (!pressed) {
            if (mc.options.leftKey != null) mc.options.leftKey.setPressed(false);
            if (mc.options.rightKey != null) mc.options.rightKey.setPressed(false);
        }
    }

    private Rotation applyBreakShake(Rotation rotation) {
        long now = System.currentTimeMillis();
        float yawOffset = (float) Math.sin(now / 72.0D) * BREAK_SHAKE_YAW;
        float pitchOffset = (float) Math.cos(now / 91.0D) * BREAK_SHAKE_PITCH;
        return new Rotation(rotation.yaw + yawOffset, MathHelper.clamp(rotation.pitch + pitchOffset, -90.0F, 90.0F));
    }

    private void applyBreakMovementJitter() {
        if (mc.options == null) {
            return;
        }
        boolean left = (System.currentTimeMillis() / 120L) % 2L == 0L;
        if (mc.options.leftKey != null) mc.options.leftKey.setPressed(left);
        if (mc.options.rightKey != null) mc.options.rightKey.setPressed(!left);
    }

    private void setAttackPressed(boolean pressed) {
        if (mc.options == null || mc.options.attackKey == null) {
            return;
        }
        mc.options.attackKey.setPressed(pressed);
    }

    private BlockPos findNearestChest() {
        if (mc.player == null || mc.world == null) {
            return null;
        }

        BlockPos origin = mc.player.getBlockPos();
        int radius = this.scanRadius.get().intValue() + 4;
        BlockPos best = null;
        double bestDistance = Double.MAX_VALUE;
        for (int x = -radius; x <= radius; x++) {
            for (int y = -3; y <= 3; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos pos = origin.add(x, y, z);
                    Block block = mc.world.getBlockState(pos).getBlock();
                    if (block != Blocks.CHEST && block != Blocks.TRAPPED_CHEST && block != Blocks.BARREL) {
                        continue;
                    }
                    double distance = pos.getSquaredDistance(origin);
                    if (distance < bestDistance) {
                        bestDistance = distance;
                        best = pos.toImmutable();
                    }
                }
            }
        }
        return best;
    }

    private void openChest(BlockPos pos) {
        if (!this.actionTimer.hasReached(350L)) {
            return;
        }
        if (!this.rotateToBlock(pos)) {
            return;
        }
        Direction side = Direction.getFacing(
            pos.getX() + 0.5D - mc.player.getX(),
            pos.getY() + 0.5D - mc.player.getEyeY(),
            pos.getZ() + 0.5D - mc.player.getZ()
        );
        BlockHitResult hit = new BlockHitResult(Vec3d.ofCenter(pos), side, pos, false);
        ActionResult result = mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hit);
        if (result.isAccepted()) {
            mc.player.swingHand(Hand.MAIN_HAND);
        }
        this.actionTimer.reset();
    }

    private boolean hasItem(Item item) {
        return this.findInventorySlot(item) != -1;
    }

    private boolean selectItem(Item item) {
        int slot = this.findInventorySlot(item);
        if (slot == -1) {
            return false;
        }
        this.selectServerSlot(slot);
        return true;
    }

    private int findInventorySlot(Item item) {
        if (mc.player == null) {
            return -1;
        }

        for (int i = 0; i < 36; i++) {
            if (mc.player.getInventory().getStack(i).getItem() == item) {
                return this.toServerSlot(i);
            }
        }
        return -1;
    }

    private int findToolSlot(Class<?> toolClass) {
        int best = -1;
        int bestDurability = -1;
        for (int i = 0; i < 36; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (!stack.isEmpty() && toolClass.isInstance(stack.getItem())) {
                int durability = stack.isDamageable() ? stack.getMaxDamage() - stack.getDamage() : Integer.MAX_VALUE;
                if (durability > bestDurability) {
                    bestDurability = durability;
                    best = this.toServerSlot(i);
                }
            }
        }
        return best;
    }

    private void selectServerSlot(int serverSlot) {
        if (serverSlot >= 36 && serverSlot <= 44) {
            mc.player.getInventory().setSelectedSlot(serverSlot - 36);
            return;
        }

        int selected = mc.player.getInventory().getSelectedSlot();
        PlayerInventoryUtil.swapItemsDirect(serverSlot, selected + 36, false);
    }

    private int toServerSlot(int inventorySlot) {
        return inventorySlot < 9 ? inventorySlot + 36 : inventorySlot;
    }

    private boolean hasFullLogStack() {
        return this.findFullLogStackSlot() != -1;
    }

    private int findFullLogStackSlot() {
        for (int i = 0; i < 36; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (this.isOakLog(stack) && stack.getCount() >= 64) {
                return this.toServerSlot(i);
            }
        }
        return -1;
    }

    private ItemStack findFirstFullLogStack() {
        for (int i = 0; i < 36; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (this.isOakLog(stack) && stack.getCount() >= 64) {
                return stack;
            }
        }
        return null;
    }

    private boolean isOakLog(ItemStack stack) {
        return stack != null && !stack.isEmpty() && (stack.getItem() == Items.OAK_LOG || stack.getItem() == Items.OAK_WOOD);
    }

    private String cleanSearchName(ItemStack stack) {
        String name = stack.getName().getString();
        name = name.replaceAll("[§&][0-9A-FK-ORa-fk-or]", "");
        name = name.replaceAll("[^\\p{L}\\p{N} ]+", " ");
        return name.replaceAll("\\s+", " ").trim();
    }

    private List<String> getTooltipLines(ItemStack stack) {
        if (stack == null || stack.isEmpty() || mc.world == null || mc.player == null) {
            return List.of();
        }

        List<String> lines = new ArrayList<>();
        stack.getTooltip(Item.TooltipContext.create(mc.world), mc.player, net.minecraft.item.tooltip.TooltipType.BASIC)
            .forEach(text -> lines.add(this.normalize(text.getString())));
        return lines;
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }
        return value.toLowerCase(Locale.ROOT).replaceAll("[§&][0-9a-fk-or]", "").trim();
    }

    private void switchStage(AppleFarmerStage nextStage) {
        this.stage = nextStage;
        this.stageStartedAt = System.currentTimeMillis();
        this.actionTimer.reset();
        this.resetBreaking();
        if (nextStage == AppleFarmerStage.FIND_SPOT || nextStage == AppleFarmerStage.BREAK_LEAVES || nextStage == AppleFarmerStage.BREAK_LOGS) {
            this.skippedBreakTargets.clear();
        }
        if (nextStage != AppleFarmerStage.AUTO_SELL_LOGS) {
            this.resetSellState();
        }
        if (nextStage != AppleFarmerStage.AUTO_REPAIR) {
            this.resetRepairState();
        }
    }

    private void resetSellState() {
        this.cachedLogSellPrice = 0;
        this.sellSearchIssued = false;
    }

    private void resetRepairState() {
        this.repairSearchIssued = false;
        this.repairTargetSlot = -1;
    }

    private enum AppleFarmerStage {
        FIND_SPOT,
        PLANT,
        GROW,
        BREAK_LEAVES,
        BREAK_LOGS,
        STORE_AND_RESTOCK,
        AUTO_SELL_LOGS,
        AUTO_REPAIR
    }
}
