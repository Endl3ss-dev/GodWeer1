package swerve.modules.impl.movement;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Items;
import swerve.events.movement.MoveInputEvent;
import swerve.managers.ModuleManager;
import swerve.modules.Module;
import swerve.modules.ModuleCategory;
import swerve.modules.api.setting.FloatSetting;
import swerve.modules.impl.combat.Aura;
import swerve.utils.item.HvHItemUse;
import swerve.utils.movement.ElytraFlightRotation;

// Adapted from AlphaDLC/src/main/java/zov/alphadlc/module/list/movement/ElytraFlight.java
public final class ElytraFlight extends Module {

    private final FloatSetting fireworkDelay = new FloatSetting("Задержка феера", 750, 100, 2000, 50);

    private long lastFireworkTime;
    private float rotationYaw;
    private float rotationPitch;
    private float rotationYawOffset = Integer.MIN_VALUE;
    private float rotationPitchOffset = Integer.MIN_VALUE;
    private boolean hasInput;

    public ElytraFlight() {
        super("ElytraFlight", "Быстрое и четкое передвижение на элитрах", ModuleCategory.MOVEMENT);
        addSettings(fireworkDelay);
    }

    public void onMoveInput(MoveInputEvent event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || !player.isFallFlying() || !hasInput) return;
        if (rotationYawOffset == Integer.MIN_VALUE || rotationPitchOffset == Integer.MIN_VALUE) return;
        ElytraFlightRotation.fixMovement(event, rotationYawOffset);
    }

    // EventPlayerUpdate in AlphaDLC fires at LocalPlayer.tick HEAD, not END_CLIENT_TICK.
    // Called by MixinLocalPlayer; onTick intentionally remains the base no-op.
    public void onPlayerUpdate() {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || mc.level == null || mc.gameMode == null) return;
        if (!player.isFallFlying()) {
            if (player.getItemBySlot(EquipmentSlot.CHEST).is(Items.ELYTRA)) {
                if (player.onGround()) {
                    player.jumpFromGround();
                } else {
                    player.connection.send(new ServerboundPlayerCommandPacket(player,
                            ServerboundPlayerCommandPacket.Action.START_FALL_FLYING));
                    player.startFallFlying();
                    HvHItemUse.swapAndUseFirework();
                    lastFireworkTime = System.currentTimeMillis();
                }
                return;
            }
        }
        if (!player.isFallFlying()) return;

        Aura aura = ModuleManager.get(Aura.class);
        boolean killauraActive = aura != null && aura.isEnable() && aura.getTarget() != null;

        if (System.currentTimeMillis() - lastFireworkTime > (long) fireworkDelay.get()) {
            HvHItemUse.swapAndUseFirework();
            lastFireworkTime = System.currentTimeMillis();
        }

        boolean forward = mc.options.keyUp.isDown();
        boolean back = mc.options.keyDown.isDown();
        boolean left = mc.options.keyLeft.isDown();
        boolean right = mc.options.keyRight.isDown();
        boolean jump = mc.options.keyJump.isDown();
        boolean sneak = mc.options.keyShift.isDown();
        boolean isMoving = forward || back || left || right;

        float cameraYaw = mc.gameRenderer.getMainCamera().getYRot();
        float yaw;
        float pitch = 0;
        if (isMoving) {
            float yawOffset = 0;
            if (forward) {
                if (left) yawOffset = -45;
                else if (right) yawOffset = 45;
            } else if (back) {
                yawOffset = 180;
                if (left) yawOffset = -135;
                else if (right) yawOffset = 135;
            } else {
                if (left) yawOffset = -90;
                else if (right) yawOffset = 90;
            }
            yaw = cameraYaw + yawOffset;
        } else {
            yaw = cameraYaw;
        }
        if (jump) pitch = isMoving ? -25 : -90;
        else if (sneak) pitch = isMoving ? 25 : 90;

        boolean hasAnyInput = isMoving || jump || sneak;
        hasInput = hasAnyInput;
        if (!killauraActive && isMoving) {
            float motion = (pitch == -25 || pitch == 25) ? 0.5F
                    : (pitch == 90 || pitch == -90) ? 0.25F : 1;
            double rad = Math.toRadians(yaw + 90);
            double motionX = Math.cos(rad) * motion;
            double motionZ = Math.sin(rad) * motion;
            double vy = 0;
            if (pitch == -25) vy = 0.5;
            else if (pitch == -90) vy = 1;
            else if (pitch == 25) vy = -0.5;
            else if (pitch == 90) vy = -1;
            player.setDeltaMovement(motionX, vy, motionZ);
        } else if (!killauraActive && !isMoving && (jump || sneak)) {
            player.setDeltaMovement(0, jump ? 1 : -1, 0);
        } else if (!killauraActive && !hasAnyInput) {
            player.setDeltaMovement(0, 0, 0);
        }

        if (hasAnyInput && !killauraActive) {
            float gcd = ElytraFlightRotation.getGCDValue();
            yaw -= (yaw - rotationYaw) % gcd;
            pitch -= (pitch - rotationPitch) % gcd;
            rotationYaw = yaw;
            rotationPitch = Mth.clamp(pitch, -90, 90);
            rotationYawOffset = rotationYaw;
            rotationPitchOffset = rotationPitch;
            ElytraFlightRotation.update(rotationYaw, rotationPitch);
            player.setYRot(rotationYaw);
            player.setXRot(rotationPitch);
            player.yHeadRot = rotationYaw;
            player.yBodyRot = rotationYaw;
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        reset(Minecraft.getInstance().player);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        clearInput();
        // Like AlphaDLC, let the rotation component return to the free camera next tick.
    }

    private void reset(LocalPlayer player) {
        lastFireworkTime = System.currentTimeMillis();
        rotationYaw = player != null ? player.getYRot() : 0;
        rotationPitch = 0;
        clearInput();
    }

    private void clearInput() {
        rotationYawOffset = Integer.MIN_VALUE;
        rotationPitchOffset = Integer.MIN_VALUE;
        hasInput = false;
    }
}
