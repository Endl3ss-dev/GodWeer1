package org.privates.client.managers.module.impl.combat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
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
import java.util.Comparator;
import java.util.List;

@Category1
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "AutoWeb", category = Category.COMBAT, description = "Трапит противника паутиной")
public class AutoWeb extends Module {

    public static AutoWeb getInstance() {
        return Instance.get(AutoWeb.class);
    }

    private final SliderSetting range = new SliderSetting(this, "Дистанция", 5.0F, 1.0F, 10.0F, 0.5F);
    private final SliderSetting delay = new SliderSetting(this, "Задержка (мс)", 50, 0, 500, 10);
    private final BooleanSetting rotate = new BooleanSetting(this, "Ротация", true);
    private final ModeSetting rotateMode = new ModeSetting(this, "Режим ротации", "Моментальная", "Плавная");
    private final SliderSetting rotateSpeed = new SliderSetting(this, "Скорость ротации", 10.0F, 1.0F, 20.0F, 0.5F);
    private final BooleanSetting strictRotation = new BooleanSetting(this, "Строгая ротация", true);
    private final BooleanSetting swing = new BooleanSetting(this, "Анимация руки", true);
    private final BooleanSetting onlyHotbar = new BooleanSetting(this, "Только хотбар", true);
    private final ModeSetting mode = new ModeSetting(this, "Режим", "Полный", "Только ноги", "Только голова", "Ноги + голова");
    private final BooleanSetting headWeb = new BooleanSetting(this, "Паутина над головой", true);
    private final SliderSetting websPerTick = new SliderSetting(this, "Паутин за тик", 2, 1, 8, 1);
    private final BooleanSetting antiEscape = new BooleanSetting(this, "Анти-побег", true);
    private final BooleanSetting checkPlacement = new BooleanSetting(this, "Проверка размещения", true);
    private final BooleanSetting legitMode = new BooleanSetting(this, "Легит режим", true);

    private final StopWatch placeTimer = new StopWatch();
    private final List<BlockPos> placedPositions = new ArrayList<>();
    private final List<BlockPos> pendingPlacements = new ArrayList<>();
    private PlayerEntity target = null;
    private int lastSlot = -1;
    private int websPlaced = 0;
    private Vector2f currentRotation = new Vector2f(0, 0);
    private Vector2f targetRotation = null;
    private boolean isRotating = false;
    private BlockPos currentPlacingPos = null;

    @Override
    public void onEnable() {
        super.onEnable();
        placedPositions.clear();
        pendingPlacements.clear();
        target = null;
        lastSlot = -1;
        websPlaced = 0;
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

        // Optimize placement checking - only check when needed
        if (checkPlacement.getValue() && !pendingPlacements.isEmpty()) {
            pendingPlacements.removeIf(pos -> mc.world.getBlockState(pos).getBlock() == Blocks.COBWEB);
            if (currentPlacingPos != null && mc.world.getBlockState(currentPlacingPos).getBlock() == Blocks.COBWEB) {
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

        target = findTarget();
        if (target == null) {
            return;
        }

        int webSlot = findWebSlot();
        if (webSlot == -1) {
            return;
        }

        websPlaced = 0;
        List<BlockPos> positions = getWebPositions(target);

        for (BlockPos pos : positions) {
            if (websPlaced >= websPerTick.getValue().intValue()) {
                break;
            }

            if (canPlace(pos)) {
                PlaceInfo placeInfo = getPlaceInfo(pos);
                if (placeInfo != null) {
                    if (rotate.getValue()) {
                        Vector3d targetVec = placeInfo.hitVec;
                        Vector2f rot = calculateRotation(targetVec);
                        
                        if (strictRotation.getValue()) {
                            if (!isLookingAt(targetVec, rot)) {
                                targetRotation = rot;
                                isRotating = true;
                                currentPlacingPos = pos;
                                return;
                            }
                        }
                        
                        currentRotation = rot;
                        RotationComponent.update(new Rotation(currentRotation.x, currentRotation.y), 360, 360, 0, 5);
                    }

                    placeWeb(pos, placeInfo, webSlot);
                    
                    if (checkPlacement.getValue()) {
                        pendingPlacements.add(pos);
                        currentPlacingPos = pos;
                    } else {
                        placedPositions.add(pos);
                    }
                    
                    websPlaced++;
                    
                    if (strictRotation.getValue() && rotate.getValue()) {
                        break;
                    }
                }
            }
        }

        if (websPlaced > 0) {
            placeTimer.reset();
        }
    }

    private PlayerEntity findTarget() {
        PlayerEntity closest = null;
        double closestDist = range.getValue();
        
        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == mc.player || player.isSpectator() || !player.isAlive()) continue;
            
            double dist = mc.player.getDistance(player);
            if (dist <= closestDist) {
                closestDist = dist;
                closest = player;
            }
        }
        
        return closest;
    }

    private List<BlockPos> getWebPositions(PlayerEntity target) {
        List<BlockPos> positions = new ArrayList<>();
        BlockPos targetPos = target.getPosition();

        String currentMode = mode.getValue();

        if (currentMode.equals("Полный") || currentMode.equals("Только ноги") || currentMode.equals("Ноги + голова")) {
            positions.add(targetPos);
        }

        if (currentMode.equals("Полный") || currentMode.equals("Только голова") || currentMode.equals("Ноги + голова")) {
            positions.add(targetPos.add(0, 1, 0));
        }

        if (headWeb.getValue() && !currentMode.equals("Только ноги")) {
            positions.add(targetPos.add(0, 2, 0));
        }

        if (antiEscape.getValue() && (currentMode.equals("Полный") || currentMode.equals("Только ноги"))) {
            positions.add(targetPos.add(1, 0, 0));
            positions.add(targetPos.add(-1, 0, 0));
            positions.add(targetPos.add(0, 0, 1));
            positions.add(targetPos.add(0, 0, -1));
        }

        // Optimize: remove already placed positions without creating iterator
        positions.removeIf(placedPositions::contains);

        return positions;
    }

    private boolean canPlace(BlockPos pos) {
        BlockState blockState = mc.world.getBlockState(pos);
        Block block = blockState.getBlock();

        if (block == Blocks.COBWEB) {
            return false;
        }

        if (!blockState.isAir() &&
                block != Blocks.WATER &&
                block != Blocks.LAVA &&
                block != Blocks.FIRE) {
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

            if (block != Blocks.AIR && block != Blocks.WATER && block != Blocks.LAVA && block != Blocks.FIRE) {
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

    private void placeWeb(BlockPos pos, PlaceInfo info, int slot) {
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

    private int findWebSlot() {
        for (int i = 0; i < (onlyHotbar.getValue() ? 9 : 36); i++) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == Items.COBWEB) {
                return i;
            }
        }
        return -1;
    }

    private Vector2f smoothRotate(Vector3d targetPos, Vector2f currentRot) {
        Vector3d playerEyes = mc.player.getEyePosition(1.0F);
        Vector3d toTarget = targetPos.subtract(playerEyes).normalize();

        double horizontalLength = Math.sqrt(toTarget.x * toTarget.x + toTarget.z * toTarget.z);
        float targetYaw = (float) Math.toDegrees(Math.atan2(toTarget.z, toTarget.x)) - 90.0F;
        float targetPitch = (float) -Math.toDegrees(Math.atan2(toTarget.y, horizontalLength));

        targetPitch = MathHelper.clamp(targetPitch, -90.0F, 90.0F);

        float smoothing = 1f;
        float smoothedYaw = lerpAngle(smoothing, currentRot.x, targetYaw);
        float smoothedPitch = MathHelper.lerp(smoothing, currentRot.y, targetPitch);

        return new Vector2f(smoothedYaw, smoothedPitch);
    }

    private float lerpAngle(float delta, float start, float end) {
        float diff = MathHelper.wrapDegrees(end - start);
        return start + diff * delta;
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

    private float[] getRotations(Vector3d target) {
        Vector3d eyePos = mc.player.getEyePosition(1.0f);
        Vector3d diff = target.subtract(eyePos);

        double dist = Math.sqrt(diff.x * diff.x + diff.z * diff.z);
        float yaw = (float) Math.toDegrees(Math.atan2(diff.z, diff.x)) - 90.0f;
        float pitch = (float) -Math.toDegrees(Math.atan2(diff.y, dist));

        pitch = MathHelper.clamp(pitch, -90.0f, 90.0f);

        return new float[]{yaw, pitch};
    }

    private record PlaceInfo(BlockPos neighbor, Direction direction, Vector3d hitVec) {
    }
}
