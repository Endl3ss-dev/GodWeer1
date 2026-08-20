package org.privates.client.managers.module.impl.misc;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.TrapDoorBlock;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import org.privates.client.api.annotations.Category2;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.component.impl.rotation.Rotation;
import org.privates.client.managers.component.impl.rotation.RotationComponent;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.rotation.RotationUtil;
import org.privates.lib.util.time.StopWatch;

import java.util.concurrent.ThreadLocalRandom;

@Category2
@Getter
@Accessors(fluent = true)
@ModuleInfo(name = "FarmBuilder", category = Category.MISC)
public class FarmBuilder extends Module {

    public static FarmBuilder getInstance() {
        return Instance.get(FarmBuilder.class);
    }

    private final ModeSetting mode = new ModeSetting(this, "Режим", "Люки", "Люки", "Полублоки").set("Люки");

    private final StopWatch placeTimer = new StopWatch();
    private final StopWatch rotationTimer = new StopWatch();
    private BlockPos currentTarget = null;
    private boolean isRotating = false;
    private boolean needsSneak = false;
    private int currentX = -3, currentZ = -3;
    private boolean buildingActive = false;

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        // Find the appropriate item in inventory
        int slotIndex = findBlockInHotbar();
        if (slotIndex == -1) return;

        // Legit slot switching with realistic delay
        if (mc.player.inventory.currentItem != slotIndex) {
            if (placeTimer.finished(100 + ThreadLocalRandom.current().nextInt(50))) {
                mc.player.inventory.currentItem = slotIndex;
                placeTimer.reset();
            }
            return;
        }

        // Realistic delay between actions (200-400ms for legit behavior)
        long delay = 200 + ThreadLocalRandom.current().nextInt(200);
        if (!placeTimer.finished(delay)) {
            return;
        }

        // Initialize building process
        if (!buildingActive) {
            currentX = -3;
            currentZ = -3;
            buildingActive = true;
        }

        // Find next position to place (one at a time for legit behavior)
        BlockPos targetPos = findNextPlacePosition();
        if (targetPos == null) {
            // Reset building when done
            buildingActive = false;
            return;
        }

        // Handle rotation to target
        if (currentTarget == null || !currentTarget.equals(targetPos)) {
            currentTarget = targetPos;
            isRotating = true;
            rotationTimer.reset();
        }

        // Smooth, human-like rotation
        if (isRotating) {
            if (!rotateToTargetLegit(targetPos)) {
                return; // Still rotating
            }
            isRotating = false;
        }

        // Handle sneaking for trapdoors
        if (mode.is("Люки") && !needsSneak) {
            mc.gameSettings.keyBindSneak.setPressed(true);
            mc.getConnection().sendPacketWithoutEvent(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.PRESS_SHIFT_KEY));
            needsSneak = true;
            placeTimer.reset();
            return; // Wait for sneak to register
        }

        // Place block with legit checks
        if (placeBlockLegit(targetPos)) {
            placeTimer.reset();
            moveToNextPosition();
        }
    }

    private int findBlockInHotbar() {
        for (int i = 0; i < 9; i++) {
            net.minecraft.item.ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (!stack.isEmpty()) {
                Block block = Block.getBlockFromItem(stack.getItem());
                if (mode.is("Люки") && block instanceof TrapDoorBlock) {
                    return i;
                } else if (mode.is("Полублоки") && block instanceof SlabBlock) {
                    return i;
                }
            }
        }
        return -1;
    }

    private BlockPos findNextPlacePosition() {
        int yOffset = mode.is("Люки") ? 2 : 0;

        // Check current position in the grid
        for (int attempts = 0; attempts < 49; attempts++) {
            BlockPos pos = mc.player.getPosition().add(currentX, yOffset, currentZ);

            if (mode.is("Люки")) {
                BlockState blockState = mc.world.getBlockState(pos);
                BlockState blockBelow = mc.world.getBlockState(pos.add(0, -1, 0));

                if (!blockBelow.isAir() && blockState.isAir()) {
                    // Check if position is reachable
                    double distance = mc.player.getDistanceSq(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                    if (distance <= 20.25) { // 4.5 blocks reach
                        return pos;
                    }
                }
            } else {
                if (currentX % 3 == 0 && currentZ % 3 == 0) {
                    BlockState blockState = mc.world.getBlockState(pos);
                    if (blockState.isAir()) {
                        double distance = mc.player.getDistanceSq(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                        if (distance <= 20.25) {
                            return pos;
                        }
                    }
                }
            }

            // Move to next position if current is not valid
            moveToNextPosition();
            if (currentX > 3) {
                return null; // Done with all positions
            }
        }
        return null;
    }

    private void moveToNextPosition() {
        currentZ++;
        if (currentZ > 3) {
            currentZ = -3;
            currentX++;
        }
    }

    private boolean rotateToTargetLegit(BlockPos pos) {
        Vector3d targetVec = new Vector3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        Vector3d eyePos = mc.player.getEyePosition(1);

        float[] rotation = RotationUtil.calculateAngle(eyePos, targetVec);
        float targetYaw = rotation[0];
        float targetPitch = rotation[1];

        // Add human-like imperfection
        targetYaw += ThreadLocalRandom.current().nextFloat(-1.5f, 1.5f);
        targetPitch += ThreadLocalRandom.current().nextFloat(-1f, 1f);

        // Smooth rotation with realistic speed
        float currentYaw = mc.player.rotationYaw;
        float currentPitch = mc.player.rotationPitch;

        float yawDiff = targetYaw - currentYaw;
        float pitchDiff = targetPitch - currentPitch;

        // Normalize yaw difference
        while (yawDiff > 180) yawDiff -= 360;
        while (yawDiff < -180) yawDiff += 360;

        // Human-like rotation speed (slower for legit behavior)
        float rotationSpeed = 3f + ThreadLocalRandom.current().nextFloat(0, 2f);
        float maxRotationPerTick = rotationSpeed;

        float newYaw = currentYaw;
        float newPitch = currentPitch;

        if (Math.abs(yawDiff) > maxRotationPerTick) {
            newYaw += Math.signum(yawDiff) * maxRotationPerTick;
        } else {
            newYaw = targetYaw;
        }

        if (Math.abs(pitchDiff) > maxRotationPerTick) {
            newPitch += Math.signum(pitchDiff) * maxRotationPerTick;
        } else {
            newPitch = targetPitch;
        }

        // Apply rotation
        Rotation rot = new Rotation(newYaw, newPitch);
        RotationComponent.update(rot, 180, 180, 1, 1);

        // Check if rotation is complete
        return Math.abs(yawDiff) <= maxRotationPerTick && Math.abs(pitchDiff) <= maxRotationPerTick;
    }

    private boolean placeBlockLegit(BlockPos pos) {
        RayTraceResult raycast = mc.player.pick(4.5, 1, true);
        if (!(raycast instanceof BlockRayTraceResult hitResult)) {
            return false;
        }

        BlockPos hitPos = hitResult.getPos();
        boolean validPlacement = false;

        if (mode.is("Люки")) {
            // For trapdoors, we need to click on the block below
            validPlacement = hitPos.equals(pos.add(0, -1, 0));
        } else {
            // For slabs, we need to click on the block below
            validPlacement = hitPos.equals(pos.add(0, -1, 0));
        }

        if (validPlacement) {
            // Add small delay before clicking for realism
            if (rotationTimer.finished(50 + ThreadLocalRandom.current().nextInt(50))) {
                if (mc.playerController.rightClickBlock(mc.player, mc.world, Hand.MAIN_HAND, hitResult).isSuccessOrConsume()) {
                    // Delayed swing for realism
                    new Thread(() -> {
                        try {
                            Thread.sleep(ThreadLocalRandom.current().nextInt(10, 30));
                            mc.player.swingArm(Hand.MAIN_HAND);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }).start();
                    return true;
                }
            }
        }
        return false;
    }

    private Vector3d ofBottomCenter(BlockPos pos) {
        return new Vector3d(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        placeTimer.reset();
        rotationTimer.reset();
        currentTarget = null;
        isRotating = false;
        needsSneak = false;
        buildingActive = false;
        currentX = -3;
        currentZ = -3;
    }

    @Override
    public void onDisable() {
        mc.gameSettings.keyBindSneak.setPressed(false);
        if (mc.player != null) {
            mc.getConnection().sendPacketWithoutEvent(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.RELEASE_SHIFT_KEY));
        }
        currentTarget = null;
        isRotating = false;
        needsSneak = false;
        buildingActive = false;
        super.onDisable();
    }
}