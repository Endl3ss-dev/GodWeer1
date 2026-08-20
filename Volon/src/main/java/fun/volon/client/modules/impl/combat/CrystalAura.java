package fun.volon.client.modules.impl.combat;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ShovelItem;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import fun.volon.Volon;
import fun.volon.api.events.EventLink;
import fun.volon.api.events.implement.EventUpdate;
import fun.volon.api.storages.implement.RotationStorage;
import fun.volon.api.utils.combat.CrystalAuraDamage;
import fun.volon.api.utils.rotate.Rotation;
import fun.volon.api.utils.rotate.RotationUtils;
import fun.volon.client.modules.Module;
import fun.volon.client.modules.impl.combat.components.gcd.GCDUtil;
import fun.volon.client.modules.settings.implement.BooleanSetting;
import fun.volon.client.modules.settings.implement.FloatSetting;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CrystalAura extends Module {

    public static CrystalAura INSTANCE = new CrystalAura();

    private final BooleanSetting autoPlace = new BooleanSetting("Размещение", true);
    private final BooleanSetting autoBreak = new BooleanSetting("Взрыв", true);
    private final BooleanSetting noPlayer = new BooleanSetting("Не взрывать себя", true);
    private final BooleanSetting autoObsidian = new BooleanSetting("Авто обсидиан", true);
    private final BooleanSetting silentRotations = new BooleanSetting("Тихие повороты", true);
    private final FloatSetting range = new FloatSetting("Range", 4.0f, 1.0f, 6.0f, 0.1f);
    private final FloatSetting placeDelay = new FloatSetting("Place Delay", 0f, 0f, 20f, 1f);
    private final FloatSetting breakDelay = new FloatSetting("Break Delay", 0f, 0f, 20f, 1f);

    private static final int BURST_PLACES_PER_TICK = 3;
    private static final long BURST_DURATION_MS = 900L;

    private EndCrystalEntity targetCrystal;
    private BlockPos pendingPrepPos;
    private int breakTimer;
    private int placeTimer;
    private final List<BlockPos> placeQueue = new ArrayList<>();
    private long burstUntil;
    private int queueRefreshTicks;

    private Vec3d pendingRotateVec;
    private int rotationTicks;
    private BlockPos miningPos;
    private Direction miningFace;

    public CrystalAura() {
        super("CrystalAura", "Ставит и взрывает кристаллы", ModuleCategory.COMBAT);
        addSettings(autoPlace, autoBreak, noPlayer, autoObsidian, silentRotations, range, placeDelay, breakDelay);
    }

    @Override
    public void onDisable() {
        targetCrystal = null;
        pendingPrepPos = null;
        placeQueue.clear();
        burstUntil = 0L;
        queueRefreshTicks = 0;
        pendingRotateVec = null;
        rotationTicks = 0;
        breakTimer = 0;
        placeTimer = 0;
        stopMining();
        super.onDisable();
    }

    @EventLink
    public void onUpdate(EventUpdate event) {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) {
            return;
        }

        PlayerEntity target = findTarget();
        if (target == null) {
            pendingRotateVec = null;
            rotationTicks = 0;
            stopMining();
            return;
        }

        targetCrystal = null;

        if (autoBreak.isState()) {
            EndCrystalEntity crystal = findBestCrystal(target);
            if (crystal != null) {
                targetCrystal = crystal;
                Vec3d lookVec = crystal.getBoundingBox().getCenter();

                if (!prepareRotation(lookVec)) {
                    return;
                }

                if (breakTimer >= breakDelay.get()) {
                    attackCrystal(crystal);
                    breakTimer = 0;
                    pendingRotateVec = null;
                    rotationTicks = 0;
                } else {
                    breakTimer++;
                }
                return;
            }
        }

        if (autoPlace.isState()) {
            if (autoObsidian.isState()) {
                if (prepareObsidian(target)) {
                    return;
                }
            }

            processCrystalPlace(target);
        }
    }

    private void processCrystalPlace(PlayerEntity target) {
        boolean burst = System.currentTimeMillis() < burstUntil;

        if (placeQueue.isEmpty() || ++queueRefreshTicks >= (burst ? 2 : 8)) {
            refreshPlaceQueue(target);
            queueRefreshTicks = 0;
        }

        if (placeQueue.isEmpty()) {
            return;
        }

        if (!hasCrystalInHand()) {
            switchToCrystal();
        }

        if (!hasCrystalInHand()) {
            return;
        }

        int maxPerTick = burst ? BURST_PLACES_PER_TICK : 1;
        int requiredDelay = burst ? 0 : Math.max(0, (int) placeDelay.get());
        int placed = 0;

        Iterator<BlockPos> iterator = placeQueue.iterator();
        while (iterator.hasNext() && placed < maxPerTick) {
            BlockPos pos = iterator.next();

            if (!canPlaceCrystal(pos)) {
                iterator.remove();
                continue;
            }

            Vec3d placeVec = pos.toCenterPos().add(0, 1, 0);
            int rotationWait = burst ? 1 : 2;

            if (placed == 0 && !prepareRotation(placeVec, rotationWait)) {
                break;
            }

            if (placeTimer < requiredDelay) {
                placeTimer++;
                break;
            }

            placeCrystal(pos);
            iterator.remove();
            placed++;
            placeTimer = 0;
        }

        if (placed > 0) {
            pendingRotateVec = null;
            rotationTicks = 0;
        }
    }

    private void refreshPlaceQueue(PlayerEntity target) {
        placeQueue.clear();
        placeQueue.addAll(collectPlacePositions(target));
    }

    private void startBurstPlace(PlayerEntity target) {
        burstUntil = System.currentTimeMillis() + BURST_DURATION_MS;
        placeTimer = Math.max(placeTimer, (int) placeDelay.get());
        queueRefreshTicks = 8;
        refreshPlaceQueue(target);
    }

    private List<BlockPos> collectPlacePositions(PlayerEntity target) {
        List<BlockPos> positions = new ArrayList<>();
        int r = (int) Math.ceil(range.get());
        BlockPos origin = target.getBlockPos();

        for (int x = -r; x <= r; x++) {
            for (int y = -r; y <= r; y++) {
                for (int z = -r; z <= r; z++) {
                    BlockPos pos = origin.add(x, y, z);
                    if (!isValidPlaceForTarget(pos, target)) {
                        continue;
                    }
                    positions.add(pos.toImmutable());
                }
            }
        }

        positions.sort((a, b) -> {
            float damageA = getTargetDamage(a, target);
            float damageB = getTargetDamage(b, target);
            if (damageA != damageB) {
                return Float.compare(damageB, damageA);
            }
            double distA = target.squaredDistanceTo(a.toCenterPos());
            double distB = target.squaredDistanceTo(b.toCenterPos());
            return Double.compare(distA, distB);
        });

        return positions;
    }

    private boolean isValidPlaceForTarget(BlockPos pos, PlayerEntity target) {
        if (!canPlaceCrystal(pos)) {
            return false;
        }

        if (mc.player.getPos().distanceTo(pos.toCenterPos()) > range.get()) {
            return false;
        }

        if (target.getY() < pos.getY() - 0.2) {
            return false;
        }

        float targetDamage = getTargetDamage(pos, target);
        if (targetDamage < 1.0f) {
            return false;
        }

        if (noPlayer.isState()) {
            float selfDamage = getSelfDamage(pos);
            if (selfDamage >= targetDamage) {
                return false;
            }
        }

        return true;
    }

    private float getTargetDamage(BlockPos pos, PlayerEntity target) {
        return CrystalAuraDamage.getCrystalDamage(getCrystalExplosionPos(pos), target);
    }

    private float getSelfDamage(BlockPos pos) {
        return CrystalAuraDamage.getSelfDamage(getCrystalExplosionPos(pos));
    }

    private Vec3d getCrystalExplosionPos(BlockPos pos) {
        return pos.toCenterPos().add(0, 1, 0);
    }

    private boolean prepareRotation(Vec3d vec) {
        return prepareRotation(vec, 2);
    }

    private boolean prepareRotation(Vec3d vec, int minTicks) {
        rotateTo(vec);

        if (pendingRotateVec == null || !pendingRotateVec.equals(vec)) {
            pendingRotateVec = vec;
            rotationTicks = 0;
            return false;
        }

        rotationTicks++;
        if (!isRotatedTo(vec) || rotationTicks < minTicks) {
            return false;
        }

        return true;
    }

    private void rotateTo(Vec3d vec) {
        Vec2f targetRot = RotationUtils.getRotations(vec);

        if (silentRotations.isState()) {
            float gcd = GCDUtil.getGCD();
            float yaw = targetRot.x - (targetRot.x - mc.player.getYaw()) % gcd;
            float pitch = targetRot.y - (targetRot.y - mc.player.getPitch()) % gcd;
            RotationStorage.update(new Rotation(yaw, pitch), 180f, 180f, 45f, 45f, 0, 2, false);
        } else {
            mc.player.setYaw(targetRot.x);
            mc.player.setPitch(targetRot.y);
        }
    }

    private boolean isRotatedTo(Vec3d vec) {
        Vec2f targetRot = RotationUtils.getRotations(vec);
        float yawDiff = Math.abs(MathHelper.wrapDegrees(targetRot.x - mc.player.getYaw()));
        float pitchDiff = Math.abs(targetRot.y - mc.player.getPitch());
        return yawDiff < 12f && pitchDiff < 12f;
    }

    private boolean prepareObsidian(PlayerEntity target) {
        if (pendingPrepPos != null) {
            if (canPlaceCrystal(pendingPrepPos)) {
                pendingPrepPos = null;
                stopMining();
                startBurstPlace(target);
                return false;
            }
            if (!isValidPrepPos(pendingPrepPos, target)) {
                pendingPrepPos = null;
                stopMining();
            }
        }

        if (pendingPrepPos == null) {
            pendingPrepPos = findPrepPos(target);
            if (pendingPrepPos == null) {
                return false;
            }
        }

        BlockPos pos = pendingPrepPos;
        BlockState state = mc.world.getBlockState(pos);

        if (state.isOf(Blocks.OBSIDIAN) || state.isOf(Blocks.BEDROCK)) {
            pendingPrepPos = null;
            startBurstPlace(target);
            return false;
        }

        Vec3d lookVec = Vec3d.ofCenter(pos);
        if (!prepareRotation(lookVec)) {
            return true;
        }

        if (!state.isAir() && isBreakable(pos)) {
            continueMining(pos);
            return true;
        }

        stopMining();

        if (state.isAir() && getPlaceSide(pos) != null) {
            if (!hasObsidianInHand()) {
                switchToObsidian();
            }
            if (hasObsidianInHand()) {
                placeObsidianBlock(pos);
                pendingRotateVec = null;
                rotationTicks = 0;
                startBurstPlace(target);
            }
            return true;
        }

        return false;
    }

    private PlayerEntity findTarget() {
        if (mc.world == null || mc.player == null) {
            return null;
        }

        PlayerEntity closest = null;
        double closestDist = Double.MAX_VALUE;

        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == mc.player) continue;
            if (!player.isAlive()) continue;
            if (player.getHealth() <= 0) continue;

            if (Volon.INSTANCE != null && Volon.INSTANCE.friendStorage != null
                    && Volon.INSTANCE.friendStorage.isFriend(player.getName().getString())) {
                continue;
            }

            double dist = mc.player.distanceTo(player);
            if (dist > range.get()) continue;

            if (dist < closestDist) {
                closestDist = dist;
                closest = player;
            }
        }

        return closest;
    }

    private EndCrystalEntity findBestCrystal(PlayerEntity target) {
        if (mc.world == null || mc.player == null) {
            return null;
        }

        EndCrystalEntity bestCrystal = null;
        double closestDistance = Double.MAX_VALUE;

        for (Entity entity : mc.world.getEntities()) {
            if (!(entity instanceof EndCrystalEntity crystal)) continue;
            if (!crystal.isAlive()) continue;

            double distance = mc.player.distanceTo(crystal);
            if (distance > range.get()) continue;

            if (target != null) {
                if (target.getY() < crystal.getY() - 1.0) continue;
            }

            float crystalY = (float) crystal.getY();
            float playerY = (float) mc.player.getY() + 0.5f;
            if (noPlayer.isState() && crystalY < playerY) {
                continue;
            }

            if (distance < closestDistance) {
                closestDistance = distance;
                bestCrystal = crystal;
            }
        }

        return bestCrystal;
    }

    private BlockPos findPrepPos(PlayerEntity target) {
        BlockPos playerPos = mc.player.getBlockPos();
        BlockPos bestPos = null;
        double closestToTarget = Double.MAX_VALUE;
        int r = (int) Math.ceil(range.get());

        for (int x = -r; x <= r; x++) {
            for (int y = -r; y <= r; y++) {
                for (int z = -r; z <= r; z++) {
                    BlockPos pos = playerPos.add(x, y, z);
                    if (!isValidPrepPos(pos, target)) continue;

                    double distToTarget = target.getPos().distanceTo(pos.toCenterPos());
                    if (distToTarget < closestToTarget) {
                        closestToTarget = distToTarget;
                        bestPos = pos;
                    }
                }
            }
        }

        return bestPos;
    }

    private boolean isValidPrepPos(BlockPos pos, PlayerEntity target) {
        BlockState state = mc.world.getBlockState(pos);

        if (state.isOf(Blocks.OBSIDIAN) || state.isOf(Blocks.BEDROCK)) {
            return false;
        }

        if (!canPlaceCrystalAfterPrep(pos)) {
            return false;
        }

        if (mc.player.getPos().distanceTo(pos.toCenterPos()) > range.get()) {
            return false;
        }

        if (target.getY() < pos.getY() - 0.2) {
            return false;
        }

        if (state.isAir()) {
            return getPlaceSide(pos) != null;
        }

        return isBreakable(pos);
    }

    private boolean canPlaceCrystal(BlockPos pos) {
        if (!mc.world.getBlockState(pos).isOf(Blocks.OBSIDIAN)
                && !mc.world.getBlockState(pos).isOf(Blocks.BEDROCK)) {
            return false;
        }

        if (!mc.world.getBlockState(pos.up()).isAir()) return false;
        if (!mc.world.getBlockState(pos.up(2)).isAir()) return false;

        Box box = new Box(pos.up()).expand(0, 1, 0);
        for (Entity entity : mc.world.getOtherEntities(null, box)) {
            if (entity instanceof EndCrystalEntity) continue;
            return false;
        }

        return true;
    }

    private boolean canPlaceCrystalAfterPrep(BlockPos pos) {
        if (!mc.world.getBlockState(pos.up()).isAir()) return false;
        if (!mc.world.getBlockState(pos.up(2)).isAir()) return false;

        Box box = new Box(pos.up()).expand(0, 1, 0);
        for (Entity entity : mc.world.getOtherEntities(null, box)) {
            if (entity instanceof EndCrystalEntity) continue;
            return false;
        }

        return true;
    }

    private boolean isBreakable(BlockPos pos) {
        BlockState state = mc.world.getBlockState(pos);
        if (state.isAir()) return false;
        return state.getHardness(mc.world, pos) >= 0.0f && !state.isOf(Blocks.BEDROCK);
    }

    private void placeCrystal(BlockPos pos) {
        boolean offhand = mc.player.getOffHandStack().isOf(Items.END_CRYSTAL);
        boolean mainhand = mc.player.getMainHandStack().isOf(Items.END_CRYSTAL);
        if (!offhand && !mainhand) return;

        Hand hand = offhand ? Hand.OFF_HAND : Hand.MAIN_HAND;
        Vec3d hitVec = new Vec3d(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5);
        BlockHitResult hitResult = new BlockHitResult(hitVec, Direction.UP, pos, false);

        mc.interactionManager.interactBlock(mc.player, hand, hitResult);
        mc.player.swingHand(hand);
    }

    private void placeObsidianBlock(BlockPos pos) {
        Direction side = getPlaceSide(pos);
        if (side == null) return;

        BlockPos neighbor = pos.offset(side);
        Direction opposite = side.getOpposite();
        Vec3d hitVec = Vec3d.ofCenter(neighbor).add(
                opposite.getOffsetX() * 0.5,
                opposite.getOffsetY() * 0.5,
                opposite.getOffsetZ() * 0.5
        );

        BlockHitResult result = new BlockHitResult(hitVec, opposite, neighbor, false);
        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, result);
        mc.player.swingHand(Hand.MAIN_HAND);
    }

    private Direction getPlaceSide(BlockPos pos) {
        Direction[] priority = {
                Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST
        };
        for (Direction dir : priority) {
            BlockPos neighbor = pos.offset(dir);
            BlockState state = mc.world.getBlockState(neighbor);
            if (!state.isReplaceable() && !state.isLiquid() && state.isSolidBlock(mc.world, neighbor)) {
                return dir;
            }
        }
        for (Direction dir : priority) {
            BlockPos neighbor = pos.offset(dir);
            BlockState state = mc.world.getBlockState(neighbor);
            if (!state.isReplaceable() && !state.isLiquid()) {
                return dir;
            }
        }
        return null;
    }

    private boolean hasCrystalInHand() {
        return mc.player.getOffHandStack().isOf(Items.END_CRYSTAL)
                || mc.player.getMainHandStack().isOf(Items.END_CRYSTAL);
    }

    private boolean hasObsidianInHand() {
        return mc.player.getMainHandStack().isOf(Items.OBSIDIAN)
                || mc.player.getOffHandStack().isOf(Items.OBSIDIAN);
    }

    private void switchToCrystal() {
        if (mc.player.getOffHandStack().isOf(Items.END_CRYSTAL)) return;

        for (int i = 0; i < 9; i++) {
            if (mc.player.getInventory().getStack(i).isOf(Items.END_CRYSTAL)) {
                mc.player.getInventory().selectedSlot = i;
                return;
            }
        }
    }

    private void switchToObsidian() {
        for (int i = 0; i < 9; i++) {
            if (mc.player.getInventory().getStack(i).isOf(Items.OBSIDIAN)) {
                mc.player.getInventory().selectedSlot = i;
                return;
            }
        }
    }

    private void switchToTool(BlockPos pos) {
        BlockState state = mc.world.getBlockState(pos);

        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() instanceof ShovelItem && stack.isSuitableFor(state)) {
                mc.player.getInventory().selectedSlot = i;
                return;
            }
        }

        for (int i = 0; i < 9; i++) {
            if (mc.player.getInventory().getStack(i).getItem() instanceof PickaxeItem) {
                mc.player.getInventory().selectedSlot = i;
                return;
            }
        }
    }

    private void attackCrystal(EndCrystalEntity crystal) {
        mc.interactionManager.attackEntity(mc.player, crystal);
        mc.player.swingHand(Hand.MAIN_HAND);
    }

    private void continueMining(BlockPos pos) {
        Direction face = getBreakFace(pos);
        switchToTool(pos);

        if (miningPos == null || !miningPos.equals(pos) || miningFace != face) {
            stopMining();
            mc.interactionManager.attackBlock(pos, face);
            miningPos = pos.toImmutable();
            miningFace = face;
        }

        mc.interactionManager.updateBlockBreakingProgress(pos, face);
    }

    private void stopMining() {
        miningPos = null;
        miningFace = null;
        if (mc.interactionManager != null) {
            mc.interactionManager.cancelBlockBreaking();
        }
    }

    private Direction getBreakFace(BlockPos pos) {
        Vec3d eyes = mc.player.getEyePos();
        Vec3d center = Vec3d.ofCenter(pos);
        double dx = eyes.x - center.x;
        double dy = eyes.y - center.y;
        double dz = eyes.z - center.z;
        double ax = Math.abs(dx);
        double ay = Math.abs(dy);
        double az = Math.abs(dz);

        if (ay >= ax && ay >= az) {
            return dy > 0 ? Direction.UP : Direction.DOWN;
        }
        if (ax >= az) {
            return dx > 0 ? Direction.EAST : Direction.WEST;
        }
        return dz > 0 ? Direction.SOUTH : Direction.NORTH;
    }
}
