package tech.lightclient.modules.impl.movement;

import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import tech.lightclient.client.events.impl.client.EventPacket;
import tech.lightclient.client.events.impl.client.TickEvent;
import tech.lightclient.client.events.impl.player.ElytraVelocityEvent;
import tech.lightclient.client.utils.eventbus.EventHandler;
import tech.lightclient.modules.api.Category;
import tech.lightclient.modules.api.Function;
import tech.lightclient.modules.api.FunctionAdd;
import tech.lightclient.modules.api.settings.impl.SliderSetting;

/**
 * Grim-compatible elytra glide. The packet part keeps the client and server's
 * movement state synchronized, while the velocity part applies the glide
 * acceleration used by the original implementation.
 */
@FunctionAdd(
        name = "GrimGlide",
        alias = "Grim Glide",
        category = Category.Movement,
        description = "Обход Grim при полёте на элитрах"
)
public class GrimGlide extends Function {
    public final SliderSetting speed = new SliderSetting("Скорость", 1.0F, 1.0F, 2.0F, 0.05F);

    private int teleportCooldown;
    private boolean positionLookReceived;
    private boolean sendingGroundPacket;

    public GrimGlide() {
        addSettings(speed);
    }

    @Override
    public void onDisable() {
        teleportCooldown = 0;
        positionLookReceived = false;
        sendingGroundPacket = false;
        super.onDisable();
    }

    @EventHandler
    public void onPacket(EventPacket event) {
        if (mc.player == null || sendingGroundPacket) {
            return;
        }

        if (event.isReceive() && event.getPacket() instanceof PlayerPositionLookS2CPacket) {
            teleportCooldown = 2;
            positionLookReceived = true;
            return;
        }

        if (event.isSend() && event.getPacket() instanceof PlayerMoveC2SPacket) {
            if (mc.player.isGliding() && teleportCooldown == 0 && !positionLookReceived) {
                sendingGroundPacket = true;
                try {
                    mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(true, true));
                } finally {
                    sendingGroundPacket = false;
                }
                event.cancel();
            }

            positionLookReceived = false;
        }
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (mc.player != null && teleportCooldown > 0) {
            teleportCooldown--;
        }
    }

    @EventHandler
    public void onElytraVelocity(ElytraVelocityEvent event) {
        if (mc.player == null || !mc.player.isGliding()) {
            return;
        }

        Vec3d velocity = event.getOriginalVelocity();
        Vec3d rotation = mc.player.getRotationVector();
        float pitch = mc.player.getPitch() * ((float) Math.PI / 180.0F);
        double horizontalRotation = Math.sqrt(rotation.x * rotation.x + rotation.z * rotation.z);
        double horizontalVelocity = velocity.horizontalLength();

        boolean falling = velocity.y <= 0.0D;
        double gravity = falling && mc.player.hasStatusEffect(StatusEffects.SLOW_FALLING)
                ? Math.min(mc.player.getFinalGravity(), 0.01D)
                : mc.player.getFinalGravity();

        double pitchCosSquared = MathHelper.square(Math.cos(pitch));
        velocity = velocity.add(0.0D, gravity * (-1.0D + pitchCosSquared * 0.75D), 0.0D);

        if (velocity.y < 0.0D && horizontalRotation > 0.0D) {
            double lift = velocity.y * -0.1D * pitchCosSquared;
            velocity = velocity.add(
                    rotation.x * lift / horizontalRotation,
                    lift,
                    rotation.z * lift / horizontalRotation
            );
        }

        if (pitch < 0.0F && horizontalRotation > 0.0D) {
            double dive = horizontalVelocity * -Math.sin(pitch) * 0.04D;
            velocity = velocity.add(
                    -rotation.x * dive / horizontalRotation,
                    dive * 3.2D,
                    -rotation.z * dive / horizontalRotation
            );
        }

        if (horizontalRotation > 0.0D) {
            velocity = velocity.add(
                    (rotation.x / horizontalRotation * horizontalVelocity - velocity.x) * 0.1D,
                    0.0D,
                    (rotation.z / horizontalRotation * horizontalVelocity - velocity.z) * 0.1D
            );
        }

        double yaw = Math.toRadians(mc.player.getYaw());
        double yawX = -Math.sin(yaw);
        double yawZ = Math.cos(yaw);
        float glideSpeed = speed.get();

        if (teleportCooldown >= 1) {
            double acceleration = 0.09D * glideSpeed;
            velocity = velocity.multiply(0.99D, 0.98D, 0.99D)
                    .add(yawX * acceleration, 0.03D * glideSpeed, yawZ * acceleration);
        } else {
            float drag = MathHelper.clamp(0.3F * glideSpeed, 0.3F, 0.85F);
            velocity = velocity.multiply(drag, drag, drag);
        }

        event.setVelocity(velocity);
    }
}
