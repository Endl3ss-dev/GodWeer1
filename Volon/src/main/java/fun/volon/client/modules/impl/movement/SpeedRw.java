package fun.volon.client.modules.impl.movement;

import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.Vec3d;

import fun.volon.api.QClient;
import fun.volon.api.events.EventLink;
import fun.volon.api.events.implement.EventMoveInput;
import fun.volon.api.events.implement.EventOnMovePost;
import fun.volon.api.events.implement.EventPacket;
import fun.volon.api.events.implement.EventPostMotion;
import fun.volon.api.utils.network.NetworkUtils;
import fun.volon.api.utils.player.TimerManager;
import fun.volon.client.modules.Module;

public class SpeedRw extends Module implements QClient {

    public static SpeedRw INSTANCE = new SpeedRw();

    private int ticks = 0;
    private int groundTicks = 0;

    public SpeedRw() {
        super("SpeedRw", "Ускорение полёта на элитре (ReallyWorld)", ModuleCategory.MOVEMENT);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        ticks = 0;
        groundTicks = 0;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        TimerManager.reset();
    }

    @EventLink
    private void onMovePost(EventOnMovePost event) {
        if (mc.player == null || mc.world == null) return;

        TimerManager.setTimer(1.7F);
        if (ticks > 3) {
            double bst = 0.03;
            if (ticks % 2 == 0) {
                mc.player.addVelocityInternal(new Vec3d(0, 0.03F, 0));
                if (mc.player.isOnGround()) {
                    bst = 0.085;
                } else {
                    bst = 0.03;
                }
            }
            double yaw = Math.toRadians(getMovementDirection());
            double xt = -Math.sin(yaw);
            double zt = Math.cos(yaw);
            if (getMovementDirection() == -1.0F) {
                xt = 0.0;
                zt = 0.0;
            }
            mc.player.addVelocityInternal(new Vec3d(xt * bst, 0, zt * bst));
        }
        ticks++;
    }

    @EventLink
    private void onMoveInput(EventMoveInput event) {
        if (mc.player == null || mc.world == null) return;

        if (mc.player.verticalCollision) {
            groundTicks++;
        } else {
            groundTicks = 0;
        }
        if (groundTicks >= 1) {
            mc.player.jump();
        }
    }

    @EventLink
    private void onPostMotion(EventPostMotion event) {
        if (mc.player == null || mc.world == null) return;

        if (ticks % 2 == 0) {
            TimerManager.setTimer(0.3F);
            NetworkUtils.sendSilentPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
        }
    }

    @EventLink
    private void onPacket(EventPacket event) {
        if (mc.player == null || mc.world == null) return;

        if (event.getPacket() instanceof PlayerPositionLookS2CPacket) {
            if (ticks % 2 == 1) {
                ticks++;
            }
            TimerManager.setTimer(1.0F);
        }
    }

    private float getMovementDirection() {
        if (mc.player == null) return -1.0F;

        float forward = mc.player.input.movementForward;
        float strafe = mc.player.input.movementSideways;
        float yaw = mc.player.getYaw();

        if (forward == 0.0F && strafe == 0.0F) {
            return -1.0F;
        }

        if (forward != 0.0F) {
            if (strafe > 0.0F) {
                yaw += forward > 0.0F ? -45.0F : 45.0F;
            } else if (strafe < 0.0F) {
                yaw += forward > 0.0F ? 45.0F : -45.0F;
            }
            strafe = 0.0F;
            forward = forward > 0.0F ? 1.0F : -1.0F;
        }

        return yaw + 90.0F;
    }
}
