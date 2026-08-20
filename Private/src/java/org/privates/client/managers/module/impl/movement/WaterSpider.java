package org.privates.client.managers.module.impl.movement;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import org.privates.client.api.annotations.Category3;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BooleanSetting;

@Category3
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "WaterSpider", category = Category.MOVEMENT, description = "Спидор вода")
public class WaterSpider extends Module {

    final BooleanSetting holdShift = new BooleanSetting(this, "Hold Shift", true);
    final BooleanSetting silentUse = new BooleanSetting(this, "Silent Use", true);
    final BooleanSetting holdSpace = new BooleanSetting(this, "Jump at Start", true);

    int previousSlot = -1;
    boolean wasJumping = false;
    long lastUseTime = 0L;
    long lastWallJumpMs = 0L;
    long lastGroundTime = 0L;

    static final long WALL_JUMP_COOLDOWN_MS = 350L;
    static final long USE_COOLDOWN_LOW = 500L;
    static final long USE_COOLDOWN_HIGH = 500L;
    static final long AIR_DELAY_MS = 120L;

    float currentPitch = 0.0F;
    float targetPitch = 75.0F;
    final float pitchLerpFactor = 0.827F;

    public WaterSpider() {
    }

    @Override
    public void onEnable() {
        super.onEnable();
        previousSlot = mc.player.inventory.currentItem;
        wasJumping = false;
        lastUseTime = 0L;
        lastGroundTime = System.currentTimeMillis();
        currentPitch = mc.player.rotationPitch;
        targetPitch = 75.0F;

        if (!silentUse.getValue()) {
            for (int i = 0; i < 9; i++) {
                ItemStack stack = mc.player.inventory.getStackInSlot(i);
                if (!stack.isEmpty() && stack.getItem() == Items.WATER_BUCKET) {
                    mc.player.inventory.currentItem = i;
                    break;
                }
            }
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (previousSlot != -1) {
            mc.player.inventory.currentItem = previousSlot;
            previousSlot = -1;
        }

        if (mc.gameSettings != null) {
            mc.gameSettings.keyBindSneak.setPressed(false);
            mc.gameSettings.keyBindJump.setPressed(false);
        }

        currentPitch = mc.player.rotationPitch;
        targetPitch = mc.player.rotationPitch;
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        handleWaterBucketMode();
    }

    private void handleWaterBucketMode() {
        if (mc.player.isInWater()) {
            mc.player.motion.y = 0.46;
        } else {
            if (mc.player.isOnGround()) {
                lastGroundTime = System.currentTimeMillis();
            } else {
                long timeInAir = System.currentTimeMillis() - lastGroundTime;
                if (timeInAir >= AIR_DELAY_MS) {
                    if (silentUse.getValue()) {
                        handleSilentWaterBucket();
                    } else {
                        handleVisibleWaterBucket();
                    }

                    if (!mc.player.collidedHorizontally) {
                        if (mc.gameSettings.keyBindSneak.isKeyDown() && holdShift.getValue()) {
                            mc.gameSettings.keyBindSneak.setPressed(false);
                        }

                        if (mc.player.motion.y <= 0.0F && mc.gameSettings.keyBindJump.isKeyDown() && holdSpace.getValue()) {
                            mc.gameSettings.keyBindJump.setPressed(false);
                        }
                    }
                }
            }
        }
    }

    private long getUseCooldown() {
        double distanceToGround = getDistanceToGround();
        return distanceToGround <= 9.9 ? 450L : 1L;
    }

    private double getDistanceToGround() {
        if (mc.player != null && mc.world != null) {
            double distance = 0.0F;
            double playerY = mc.player.getPosY();

            for (double y = playerY; y > 0.0F; y -= 0.1) {
                BlockPos pos = new BlockPos(mc.player.getPosX(), y, mc.player.getPosZ());
                if (!mc.world.isAirBlock(pos)) {
                    distance = playerY - (y + 1.0F);
                    break;
                }
            }

            return Math.max(distance, 0.0F);
        }

        return 0.0F;
    }

    private void handleSilentWaterBucket() {
        int waterSlot = -1;

        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() == Items.WATER_BUCKET) {
                waterSlot = i;
                break;
            }
        }

        if (waterSlot != -1) {
            if (mc.player.collidedHorizontally) {
                handleWallClimb(waterSlot);
            }
        }
    }

    private void handleVisibleWaterBucket() {
        if (mc.player.getHeldItemMainhand().getItem() == Items.WATER_BUCKET) {
            if (mc.player.collidedHorizontally) {
                handleWallClimb(mc.player.inventory.currentItem);
            }
        }
    }

    private void handleWallClimb(int waterSlot) {
        if (!mc.player.isOnGround()) {
            long timeInAir = System.currentTimeMillis() - lastGroundTime;
            if (timeInAir >= AIR_DELAY_MS) {
                if (holdSpace.getValue()) {
                    if (mc.player.isOnGround()) {
                        long now = System.currentTimeMillis();
                        if (now - lastWallJumpMs > WALL_JUMP_COOLDOWN_MS) {
                            mc.player.jump();
                            lastWallJumpMs = now;
                        }
                    }

                    mc.gameSettings.keyBindJump.setPressed(true);
                }

                long currentTime = System.currentTimeMillis();
                long cooldown = getUseCooldown();
                if (currentTime - lastUseTime >= cooldown) {
                    int clientSlot = mc.player.inventory.currentItem;
                    updatePitchInterpolation();
                    mc.player.rotationPitch = currentPitch;

                    if (silentUse.getValue() && waterSlot != clientSlot) {
                        mc.player.connection.sendPacket(new CHeldItemChangePacket(waterSlot));
                    }

                    mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
                    mc.player.motion.y = 0.45;

                    if (silentUse.getValue() && waterSlot != clientSlot) {
                        mc.player.connection.sendPacket(new CHeldItemChangePacket(clientSlot));
                    }

                    lastUseTime = currentTime;
                } else {
                    updatePitchInterpolation();
                    mc.player.rotationPitch = currentPitch;
                }

                if (holdShift.getValue()) {
                    mc.gameSettings.keyBindSneak.setPressed(true);
                }
            }
        }
    }

    private void updatePitchInterpolation() {
        if (mc.player.collidedHorizontally) {
            targetPitch = 75.0F;
        } else {
            targetPitch = mc.player.rotationPitch;
        }

        float pitchDelta = targetPitch - currentPitch;
        currentPitch += pitchDelta * pitchLerpFactor;
        currentPitch = Math.max(-90.0F, Math.min(90.0F, currentPitch));
    }
}