package org.privates.client.managers.module.impl.combat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CPlayerTryUseItemOnBlockPacket;
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
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.other.Instance;
import org.privates.lib.util.time.StopWatch;

import java.util.ArrayList;
import java.util.List;

@Category1
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "CrystalSafe", category = Category.COMBAT, description = "Защищает от кристалов, застраивая игрока блоками")
public class CrystalSafe extends Module {

    public static CrystalSafe getInstance() {
        return Instance.get(CrystalSafe.class);
    }

    private final SliderSetting delay = new SliderSetting(this, "Задержка (мс)", 50, 0, 500, 10);
    private final BooleanSetting rotate = new BooleanSetting(this, "Ротация", true);
    private final ModeSetting rotateMode = new ModeSetting(this, "Режим ротации", "Моментальная", "Плавная");
    private final SliderSetting rotateSpeed = new SliderSetting(this, "Скорость ротации", 10.0F, 1.0F, 20.0F, 0.5F);
    private final BooleanSetting raytrace = new BooleanSetting(this, "Raytrace", true);
    private final BooleanSetting swing = new BooleanSetting(this, "Анимация руки", true);
    private final BooleanSetting onlyHotbar = new BooleanSetting(this, "Только хотбар", true);
    private final ModeSetting mode = new ModeSetting(this, "Режим", "Полная защита", "Только низ", "Только верх");
    private final BooleanSetting topBlock = new BooleanSetting(this, "Блок сверху", true);
    private final SliderSetting blocksPerTick = new SliderSetting(this, "Блоков за тик", 2, 1, 8, 1);
    private final BooleanSetting checkPlacement = new BooleanSetting(this, "Проверка размещения", true);

    private final StopWatch placeTimer = new StopWatch();
    private final List<BlockPos> placedPositions = new ArrayList<>();
    private final List<BlockPos> pendingPlacements = new ArrayList<>();
    private int lastSlot = -1;
    private int blocksPlaced = 0;
    private Vector2f currentRotation = new Vector2f(0, 0);
    private Vector2f targetRotation = null;
    private boolean isRotating = false;
    private BlockPos currentPlacingPos = null;

    @Override
    public void onEnable() {
        super.onEnable();
        placedPositions.clear();
        pendingPlacements.clear();
        lastSlot = -1;
        blocksPlaced = 0;
        placeTimer.reset();
        isRotating = false;
        targetRotation = null;
        currentPlacingPos = null;
        if (mc.player != null) {
            currentRotation = new Vector2f(mc.player.rotationYaw, mc.player.rotationPitch);
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (lastSlot != -1 && mc.player != null) {
            mc.player.inventory.currentItem = lastSlot;
            mc.player.connection.sendPacket(new CHeldItemChangePacket(lastSlot));
            lastSlot = -1;
        }
        placedPositions.clear();
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (mc.player == null || mc.world == null) return;

        // Optimize placement checking
        if (checkPlacement.getValue() && !pendingPlacements.isEmpty()) {
            pendingPlacements.removeIf(pos -> !mc.world.getBlockState(pos).isAir());
            if (currentPlacingPos != null && !mc.world.getBlockState(currentPlacingPos).isAir()) {
                placedPositions.add(currentPlacingPos);
                currentPlacingPos = null;
                isRotating = false;
            }
        }

        if (isRotating && targetRotation != null) {
            if (rotateMode.is("Плавная")) {
                float speed = rotateSpeed.getValue() / 100f;
                currentRotation = smoothRotate(targetRotation, currentRotation, speed);
                RotationComponent.update(new Rotation(currentRotation.x, currentRotation.y), 360, 360, 0, 5);
                
                float yawDiff = Math.abs(MathHelper.wrapDegrees(targetRotation.x - currentRotation.x));
                float pitchDiff = Math.abs(targetRotation.y - currentRotation.y);
                
                if (yawDiff < 1.0f && pitchDiff < 1.0f) {
                    isRotating = false;
                }
            } else {
                currentRotation = targetRotation;
                RotationComponent.update(new Rotation(currentRotation.x, currentRotation.y), 360, 360, 0, 5);
                isRotating = false;
            }
            
            if (isRotating) return;
        }

        if (!placeTimer.finished(delay.getValue().intValue())) {
            return;
        }

        int blockSlot = findBlockSlot();
        if (blockSlot == -1) {
            return;
        }

        blocksPlaced = 0;
        List<BlockPos> positions = getSafePositions();

        for (BlockPos pos : positions) {
            if (blocksPlaced >= blocksPerTick.getValue().intValue()) {
                break;
            }

            if (canPlace(pos)) {
                PlaceInfo placeInfo = getPlaceInfo(pos);
                if (placeInfo != null) {
                    // Raytrace check
                    if (raytrace.getValue() && !canSeeBlock(placeInfo.hitVec)) {
                        continue;
                    }

                    if (rotate.getValue()) {
                        Vector3d targetVec = placeInfo.hitVec;
                        Vector2f rot = calculateRotation(targetVec);
                        
                        currentRotation = rot;
                        RotationComponent.update(new Rotation(currentRotation.x, currentRotation.y), 360, 360, 0, 5);
                    }

                    placeBlock(pos, placeInfo, blockSlot);
                    
                    if (checkPlacement.getValue()) {
                        pendingPlacements.add(pos);
                        currentPlacingPos = pos;
                    } else {
                        placedPositions.add(pos);
                    }
                    
                    blocksPlaced++;
                }
            }
        }

        if (blocksPlaced > 0) {
            placeTimer.reset();
        }
    }

    private List<BlockPos> getSafePositions() {
        List<BlockPos> positions = new ArrayList<>();
        BlockPos playerPos = mc.player.getPosition();

        String currentMode = mode.getValue();

        // Защита на уровне ног
        if (currentMode.equals("Полная защита") || currentMode.equals("Только низ")) {
            positions.add(playerPos.add(1, 0, 0));
            positions.add(playerPos.add(-1, 0, 0));
            positions.add(playerPos.add(0, 0, 1));
            positions.add(playerPos.add(0, 0, -1));
        }

        // Защита на уровне головы
        if (currentMode.equals("Полная защита") || currentMode.equals("Только верх")) {
            positions.add(playerPos.add(1, 1, 0));
            positions.add(playerPos.add(-1, 1, 0));
            positions.add(playerPos.add(0, 1, 1));
            positions.add(playerPos.add(0, 1, -1));
        }

        // Блок сверху
        if (topBlock.getValue() && !currentMode.equals("Только низ")) {
            positions.add(playerPos.add(0, 2, 0));
        }

        // Удаляем уже размещённые позиции
        positions.removeIf(placedPositions::contains);

        return positions;
    }

    private boolean canPlace(BlockPos pos) {
        if (!mc.world.getBlockState(pos).isAir()) {
            return false;
        }

        // Не ставим блоки там, где стоит игрок
        BlockPos playerPos = mc.player.getPosition();
        if (pos.equals(playerPos) || pos.equals(playerPos.up())) {
            return false;
        }

        return true;
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

            if (block != Blocks.AIR && block != Blocks.WATER && block != Blocks.LAVA) {
                Vector3d hitVec = new Vector3d(
                        neighbor.getX() + 0.5 + direction.getOpposite().getXOffset() * 0.5,
                        neighbor.getY() + 0.5 + direction.getOpposite().getYOffset() * 0.5,
                        neighbor.getZ() + 0.5 + direction.getOpposite().getZOffset() * 0.5
                );

                return new PlaceInfo(neighbor, direction.getOpposite(), hitVec);
            }
        }

        return null;
    }

    private void placeBlock(BlockPos pos, PlaceInfo info, int slot) {
        int previousSlot = mc.player.inventory.currentItem;

        if (previousSlot != slot) {
            if (lastSlot == -1) {
                lastSlot = previousSlot;
            }
            mc.player.inventory.currentItem = slot;
            mc.player.connection.sendPacket(new CHeldItemChangePacket(slot));
        }

        BlockRayTraceResult result = new BlockRayTraceResult(
                info.hitVec, info.direction, info.neighbor, false
        );

        mc.player.connection.sendPacket(new CPlayerTryUseItemOnBlockPacket(Hand.MAIN_HAND, result));

        if (swing.getValue()) {
            mc.player.swingArm(Hand.MAIN_HAND);
        }

        mc.player.inventory.currentItem = previousSlot;
        mc.player.connection.sendPacket(new CHeldItemChangePacket(previousSlot));
        lastSlot = -1;
    }

    private int findBlockSlot() {
        for (int i = 0; i < (onlyHotbar.getValue() ? 9 : 36); i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);

            if (!stack.isEmpty() && stack.getItem() instanceof BlockItem blockItem) {
                Block block = blockItem.getBlock();

                if (block != Blocks.AIR &&
                        block != Blocks.WATER &&
                        block != Blocks.LAVA &&
                        block != Blocks.TNT &&
                        block.getDefaultState().isOpaqueCube(mc.world, BlockPos.ZERO)) {
                    return i;
                }
            }
        }

        return -1;
    }

    private Vector2f calculateRotation(Vector3d targetPos) {
        Vector3d playerEyes = mc.player.getEyePosition(1.0F);
        Vector3d toTarget = targetPos.subtract(playerEyes).normalize();

        double horizontalLength = Math.sqrt(toTarget.x * toTarget.x + toTarget.z * toTarget.z);
        float targetYaw = (float) Math.toDegrees(Math.atan2(toTarget.z, toTarget.x)) - 90.0F;
        float targetPitch = (float) -Math.toDegrees(Math.atan2(toTarget.y, horizontalLength));

        targetPitch = MathHelper.clamp(targetPitch, -90.0F, 90.0F);

        return new Vector2f(targetYaw, targetPitch);
    }

    private Vector2f smoothRotate(Vector2f target, Vector2f current, float speed) {
        float yawDiff = MathHelper.wrapDegrees(target.x - current.x);
        float pitchDiff = target.y - current.y;

        float smoothedYaw = current.x + yawDiff * speed;
        float smoothedPitch = current.y + pitchDiff * speed;

        return new Vector2f(smoothedYaw, smoothedPitch);
    }

    private boolean isLookingAt(Vector3d target, Vector2f rotation) {
        Vector3d playerEyes = mc.player.getEyePosition(1.0F);
        double distance = playerEyes.distanceTo(target);
        
        double yawRad = Math.toRadians(rotation.x + 90);
        double pitchRad = Math.toRadians(rotation.y);
        
        double x = -Math.sin(yawRad) * Math.cos(pitchRad);
        double y = -Math.sin(pitchRad);
        double z = Math.cos(yawRad) * Math.cos(pitchRad);
        
        Vector3d lookVec = new Vector3d(x, y, z).normalize();
        Vector3d targetVec = target.subtract(playerEyes).normalize();
        
        double dot = lookVec.dotProduct(targetVec);
        return dot > 0.99;
    }

    private boolean canSeeBlock(Vector3d targetVec) {
        if (mc.player == null || mc.world == null) return false;
        
        Vector3d eyePos = mc.player.getEyePosition(1.0f);
        
        // Raytrace from player eyes to target position
        BlockRayTraceResult result = mc.world.rayTraceBlocks(
            new net.minecraft.util.math.RayTraceContext(
                eyePos,
                targetVec,
                net.minecraft.util.math.RayTraceContext.BlockMode.OUTLINE,
                net.minecraft.util.math.RayTraceContext.FluidMode.NONE,
                mc.player
            )
        );
        
        // If we hit nothing or hit the target position, we can see it
        if (result == null || result.getType() == BlockRayTraceResult.Type.MISS) {
            return true;
        }
        
        // Check if the hit position is close to our target
        Vector3d hitVec = result.getHitVec();
        return hitVec.distanceTo(targetVec) < 0.1;
    }

    private record PlaceInfo(BlockPos neighbor, Direction direction, Vector3d hitVec) {
    }
}
