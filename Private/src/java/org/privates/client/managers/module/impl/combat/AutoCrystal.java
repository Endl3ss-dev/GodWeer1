package org.privates.client.managers.module.impl.combat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import org.privates.client.api.annotations.Category1;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.component.impl.rotation.Rotation;
import org.privates.client.managers.component.impl.rotation.RotationComponent;
import org.privates.client.managers.events.block.EventPlace;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.player.InvUtil;

import java.util.ArrayList;
import java.util.List;

@Category1

@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "AutoCrystal", category = Category.COMBAT, description = "Размещает кристаллы на обсидиане")
public class AutoCrystal extends Module {

    public static AutoCrystal getInstance() {
        return Instance.get(AutoCrystal.class);
    }

    private final SliderSetting range = new SliderSetting(this, "Дистанция", 5.0F, 1.0F, 6.0F, 0.1F);
    private final BooleanSetting rotate = new BooleanSetting(this, "Ротация", true);
    private final SliderSetting delay = new SliderSetting(this, "Задержка (мс)", 50, 5, 500, 5);
    private final BooleanSetting onlyHotbar = new BooleanSetting(this, "Только хотбар", true);
    private final BooleanSetting dontBreakSelf = new BooleanSetting(this, "Не взрывать себя", true);
    private final BooleanSetting rotateOnBreak = new BooleanSetting(this, "Ротация на удар", true);
    private final BooleanSetting oneCrystalPerBlock = new BooleanSetting(this, "1 кристалл на 1 блок", true);

    private final List<BlockPos> placedBlocks = new ArrayList<>();
    private final List<BlockPos> processedBlocks = new ArrayList<>();
    private long lastPlaceTime = 0;
    private final long lastBreakTime = 0;
    private Rotation currentRotation = null;
    private Vector2f currentLerpRotation = new Vector2f(0, 0);
    private final BlockPos lastPlacedBlock = null;
    private final boolean waitingForCrystalBreak = false;

    @Override
    public void onEnable() {
        super.onEnable();
        lastPlaceTime = 0;
        placedBlocks.clear();
        processedBlocks.clear();
        currentRotation = null;
        currentLerpRotation = new Vector2f(mc.player.rotationYaw, mc.player.rotationPitch);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        placedBlocks.clear();
        processedBlocks.clear();
        currentRotation = null;
    }

    @EventHandler
    public void onBlockPlace(EventPlace event) {
        BlockPos pos = event.getPos();
        Block block = mc.world.getBlockState(pos).getBlock();

        if (isValidBlock(block, pos) && !processedBlocks.contains(pos)) {
            placedBlocks.add(pos);
        }
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (mc.player == null || mc.world == null) return;

        attackNearbyCrystals();

        long now = System.currentTimeMillis();
        if (now - lastPlaceTime < delay.getValue().intValue()) return;

        if (placedBlocks.isEmpty()) {
            BlockPos nearestObsidian = findNearestObsidian();
            if (nearestObsidian != null && !processedBlocks.contains(nearestObsidian)) {
                placedBlocks.add(nearestObsidian);
            }
        }

        if (placedBlocks.isEmpty()) return;

        BlockPos blockPos = placedBlocks.get(0);

        if (mc.world.getBlockState(blockPos).getBlock() == Blocks.AIR) {
            placedBlocks.remove(0);
            return;
        }

        if (!canPlaceCrystal(blockPos)) {
            placedBlocks.remove(0);
            return;
        }

        Slot crystalSlot = findCrystalSlot();
        if (crystalSlot == null) return;

        Vector3d placePos = new Vector3d(blockPos.getX() + 0.5, blockPos.getY() + 1, blockPos.getZ() + 0.5);

        if (rotate.getValue()) {
            currentLerpRotation = smoothRotate(placePos, currentLerpRotation);
            currentRotation = new Rotation(currentLerpRotation.x, currentLerpRotation.y);
            RotationComponent.update(currentRotation, 180, 90, 0, 5);
        }

        placeCrystal(blockPos, crystalSlot);
        placedBlocks.remove(0);
        processedBlocks.add(blockPos);
        lastPlaceTime = now;
    }

    private BlockPos findNearestObsidian() {
        Vector3d playerPos = mc.player.getPositionVec();
        BlockPos playerBlockPos = new BlockPos(playerPos);
        BlockPos minPos = playerBlockPos.add(-range.getValue(), -range.getValue(), -range.getValue());
        BlockPos maxPos = playerBlockPos.add(range.getValue(), range.getValue(), range.getValue());

        BlockPos nearest = null;
        double minDist = Double.MAX_VALUE;

        for (BlockPos pos : BlockPos.getAllInBoxMutable(minPos, maxPos)) {
            Block block = mc.world.getBlockState(pos).getBlock();
            if (isValidBlock(block, pos) && canPlaceCrystal(pos)) {
                double dist = playerPos.distanceTo(new Vector3d(pos.getX(), pos.getY(), pos.getZ()));
                if (dist < minDist) {
                    minDist = dist;
                    nearest = pos.toImmutable();
                }
            }
        }

        return nearest;
    }

    private boolean canPlaceCrystal(BlockPos pos) {
        BlockPos above = pos.up();
        BlockPos above2 = pos.up(2);
        return mc.world.getBlockState(above).getBlock() == Blocks.AIR &&
                mc.world.getBlockState(above2).getBlock() == Blocks.AIR &&
                mc.world.getEntitiesWithinAABB(Entity.class, new net.minecraft.util.math.AxisAlignedBB(above)).isEmpty();
    }

    private boolean isValidBlock(Block block, BlockPos pos) {
        if (block != Blocks.OBSIDIAN && block != Blocks.BEDROCK) return false;

        if (dontBreakSelf.getValue()) {
            double crystalY = pos.getY() + 1.0;
            return !(mc.player.getPosY() >= crystalY);
        }

        return true;
    }

    private Slot findCrystalSlot() {
        if (onlyHotbar.getValue()) {
            int hotbarIndex = InvUtil.getItemInHotBar(Items.END_CRYSTAL);
            if (hotbarIndex != -1) {
                return mc.player.container.inventorySlots.get(hotbarIndex + 36);
            }
            return null;
        }
        return InvUtil.getSlot(Items.END_CRYSTAL);
    }

    private void placeCrystal(BlockPos pos, Slot slot) {
        if (mc.world.getBlockState(pos).getBlock() == Blocks.AIR) return;

        int slotId = slot.slotNumber;
        int previousSlot = mc.player.inventory.currentItem;

        if (slotId >= 36 && slotId < 45) {
            mc.player.connection.sendPacket(new CHeldItemChangePacket(slotId - 36));
        }

        Vector3d hitVec = new Vector3d(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5);
        BlockRayTraceResult result = new BlockRayTraceResult(hitVec, Direction.UP, pos, false);

        mc.player.connection.sendPacket(new net.minecraft.network.play.client.CPlayerTryUseItemOnBlockPacket(Hand.MAIN_HAND, result));
        mc.player.connection.sendPacket(new net.minecraft.network.play.client.CAnimateHandPacket(Hand.MAIN_HAND));

        if (slotId >= 36 && slotId < 45) {
            mc.player.connection.sendPacket(new CHeldItemChangePacket(previousSlot));
        }
    }

    private void attackNearbyCrystals() {
        for (Entity entity : mc.world.getAllEntities()) {
            if (entity instanceof EnderCrystalEntity) {
                if (mc.player.getDistance(entity) < range.getValue()) {
                    if (mc.player.getCooledAttackStrength(0) > 0.9f) {
                        if (rotateOnBreak.getValue()) {
                            Vector3d crystalPos = entity.getPositionVec().add(0, entity.getEyeHeight() * 0.5, 0);
                            currentLerpRotation = smoothRotate(crystalPos, currentLerpRotation);
                            currentRotation = new Rotation(currentLerpRotation.x, currentLerpRotation.y);
                            RotationComponent.update(currentRotation, 180, 90, 0, 5);
                        }
                        mc.playerController.attackEntity(mc.player, entity);
                        mc.player.swingArm(Hand.MAIN_HAND);
                    }
                }
            }
        }
    }

    private Vector2f smoothRotate(Vector3d targetPos, Vector2f currentRotation) {
        Vector3d playerEyes = mc.player.getEyePosition(1.0F);
        Vector3d toTarget = targetPos.subtract(playerEyes).normalize();

        double horizontalLength = Math.sqrt(toTarget.x * toTarget.x + toTarget.z * toTarget.z);
        float targetYaw = (float) Math.toDegrees(Math.atan2(toTarget.z, toTarget.x)) - 90.0F;
        float targetPitch = (float) -Math.toDegrees(Math.atan2(toTarget.y, horizontalLength));

        targetPitch = MathHelper.clamp(targetPitch, -90.0F, 90.0F);

        float smoothing = 0.6f;
        float smoothedYaw = lerpAngle(smoothing, currentRotation.x, targetYaw);
        float smoothedPitch = MathHelper.lerp(smoothing, currentRotation.y, targetPitch);

        return new Vector2f(smoothedYaw, smoothedPitch);
    }

    private float lerpAngle(float delta, float start, float end) {
        float diff = MathHelper.wrapDegrees(end - start);
        return start + diff * delta;
    }
}