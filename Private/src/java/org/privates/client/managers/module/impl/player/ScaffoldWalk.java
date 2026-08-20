package org.privates.client.managers.module.impl.player;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CPlayerTryUseItemOnBlockPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import org.privates.client.api.annotations.Category2;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.component.impl.rotation.Rotation;
import org.privates.client.managers.component.impl.rotation.RotationComponent;
import org.privates.client.managers.events.player.MoveEvent;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.other.Instance;
import org.privates.lib.util.time.StopWatch;

@Category2
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "ScaffoldWalk", category = Category.PLAYER, description = "Автоматически ставит блоки под ногами")
public class ScaffoldWalk extends Module {

    public static ScaffoldWalk getInstance() {
        return Instance.get(ScaffoldWalk.class);
    }

    private final ModeSetting mode = new ModeSetting(this, "Режим", "Обычный", "Быстрый", "Безопасный");
    private final BooleanSetting rotate = new BooleanSetting(this, "Ротация", true);
    private final BooleanSetting safeWalk = new BooleanSetting(this, "Безопасная ходьба", true);
    private final BooleanSetting tower = new BooleanSetting(this, "Башня", false);
    private final BooleanSetting swing = new BooleanSetting(this, "Анимация руки", true);
    private final SliderSetting delay = new SliderSetting(this, "Задержка (мс)", 50, 0, 200, 10);
    private final BooleanSetting onlyHotbar = new BooleanSetting(this, "Только хотбар", true);

    private final StopWatch placeTimer = new StopWatch();
    private int lastSlot = -1;
    private BlockPos lastPlacedPos = null;

    @Override
    public void onEnable() {
        super.onEnable();
        lastSlot = -1;
        lastPlacedPos = null;
        placeTimer.reset();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (lastSlot != -1 && mc.player != null) {
            mc.player.inventory.currentItem = lastSlot;
            mc.player.connection.sendPacket(new CHeldItemChangePacket(lastSlot));
            lastSlot = -1;
        }
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (mc.player == null || mc.world == null) return;

        if (!placeTimer.finished(delay.getValue().intValue())) {
            return;
        }

        int blockSlot = findBlockSlot();
        if (blockSlot == -1) {
            return;
        }

        BlockPos placePos = getPlacePosition();
        if (placePos == null) {
            return;
        }

        if (lastPlacedPos != null && lastPlacedPos.equals(placePos)) {
            return;
        }

        PlaceInfo placeInfo = getPlaceInfo(placePos);
        if (placeInfo == null) {
            return;
        }

        if (rotate.getValue()) {
            float[] rotations = getRotations(placeInfo.hitVec);
            RotationComponent.update(new Rotation(rotations[0], rotations[1]), 180, 90, 0, 5);
        }

        if (tower.getValue() && !isMoving() && mc.gameSettings.keyBindJump.isKeyDown()) {
            if (mc.player.isOnGround()) {
                mc.player.setMotion(mc.player.getMotion().x, 0.42, mc.player.getMotion().z);
            }
        }

        placeBlock(placePos, placeInfo, blockSlot);
        lastPlacedPos = placePos;
        placeTimer.reset();
    }

    @EventHandler
    public void onMove(MoveEvent event) {
        if (!safeWalk.getValue()) return;
        if (mc.player == null || mc.world == null) return;

        if (mc.player.isOnGround() && !mc.gameSettings.keyBindJump.isKeyDown()) {
            double x = mc.player.getPosX() + event.getMotion().x;
            double z = mc.player.getPosZ() + event.getMotion().z;
            BlockPos checkPos = new BlockPos(x, mc.player.getPosY() - 1, z);

            if (mc.world.isAirBlock(checkPos)) {
                Vector3d motion = event.getMotion();
                event.setMotion(new Vector3d(motion.x * 0.3, motion.y, motion.z * 0.3));
            }
        }
    }

    private BlockPos getPlacePosition() {
        BlockPos playerPos = mc.player.getPosition();
        BlockPos below = playerPos.down();

        if (mc.world.isAirBlock(below)) {
            return below;
        }

        if (isMoving()) {
            double yaw = Math.toRadians(mc.player.rotationYaw);
            double x = -Math.sin(yaw) * 0.5;
            double z = Math.cos(yaw) * 0.5;

            BlockPos forward = new BlockPos(
                    mc.player.getPosX() + x,
                    mc.player.getPosY() - 1,
                    mc.player.getPosZ() + z
            );

            if (mc.world.isAirBlock(forward)) {
                return forward;
            }
        }

        return null;
    }

    private PlaceInfo getPlaceInfo(BlockPos pos) {
        Direction[] directions = {
                Direction.DOWN, Direction.UP,
                Direction.NORTH, Direction.SOUTH,
                Direction.EAST, Direction.WEST
        };

        for (Direction direction : directions) {
            BlockPos neighbor = pos.offset(direction);
            BlockState state = mc.world.getBlockState(neighbor);

            if (!state.isAir() && state.getBlock() != Blocks.WATER && state.getBlock() != Blocks.LAVA) {
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

        if (mode.is("Быстрый")) {
            mc.player.inventory.currentItem = previousSlot;
            mc.player.connection.sendPacket(new CHeldItemChangePacket(previousSlot));
            lastSlot = -1;
        }
    }

    private int findBlockSlot() {
        for (int i = 0; i < (onlyHotbar.getValue() ? 9 : 36); i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);

            if (!stack.isEmpty() && stack.getItem() instanceof BlockItem blockItem) {

                if (blockItem.getBlock() != Blocks.AIR &&
                        blockItem.getBlock() != Blocks.WATER &&
                        blockItem.getBlock() != Blocks.LAVA &&
                        blockItem.getBlock() != Blocks.TNT &&
                        blockItem.getBlock().getDefaultState().isOpaqueCube(mc.world, BlockPos.ZERO)) {
                    return i;
                }
            }
        }

        return -1;
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

    private boolean isMoving() {
        return mc.player.movementInput.moveForward != 0 || mc.player.movementInput.moveStrafe != 0;
    }

    private record PlaceInfo(BlockPos neighbor, Direction direction, Vector3d hitVec) {
    }
}
