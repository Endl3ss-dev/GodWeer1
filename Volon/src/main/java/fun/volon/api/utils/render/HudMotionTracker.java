package fun.volon.api.utils.render;

import net.minecraft.util.math.MathHelper;
import fun.volon.api.QClient;

/**
 * Tracks camera / player movement for HUD liquid-glass motion blur.
 */
public final class HudMotionTracker implements QClient {

    private static float lastYaw;
    private static float lastPitch;
    private static double lastX;
    private static double lastY;
    private static double lastZ;
    private static boolean initialized;

    private static float motionAmount;
    private static float motionVelX;
    private static float motionVelY;

    private HudMotionTracker() {
    }

    public static void update() {
        if (mc == null || mc.player == null) {
            motionAmount *= 0.9f;
            motionVelX *= 0.85f;
            motionVelY *= 0.85f;
            return;
        }

        float yaw = mc.player.getYaw();
        float pitch = mc.player.getPitch();
        double x = mc.player.getX();
        double y = mc.player.getY();
        double z = mc.player.getZ();

        if (!initialized) {
            lastYaw = yaw;
            lastPitch = pitch;
            lastX = x;
            lastY = y;
            lastZ = z;
            initialized = true;
            return;
        }

        float dyaw = MathHelper.wrapDegrees(yaw - lastYaw);
        float dpitch = pitch - lastPitch;
        double dx = x - lastX;
        double dy = y - lastY;
        double dz = z - lastZ;

        float rotSpeed = (float) Math.sqrt(dyaw * dyaw + dpitch * dpitch);
        float moveSpeed = (float) Math.sqrt(dx * dx + dy * dy + dz * dz) * 20.0f;

        float instant = MathHelper.clamp(rotSpeed * 0.055f + moveSpeed * 0.42f, 0.0f, 1.35f);
        motionAmount = motionAmount * 0.78f + instant * 0.22f;
        motionAmount = MathHelper.clamp(motionAmount, 0.0f, 1.0f);

        float targetVelX = dyaw * 0.00042f + (float) dx * 0.0018f;
        float targetVelY = -dpitch * 0.00042f + (float) dy * 0.0024f - (float) dz * 0.0006f;
        motionVelX = motionVelX * 0.72f + targetVelX * 0.28f;
        motionVelY = motionVelY * 0.72f + targetVelY * 0.28f;

        lastYaw = yaw;
        lastPitch = pitch;
        lastX = x;
        lastY = y;
        lastZ = z;
    }

    public static float getMotionAmount() {
        return motionAmount;
    }

    /** Normalized screen-space blur direction (roughly -1..1). */
    public static float getMotionVelX() {
        return motionVelX;
    }

    public static float getMotionVelY() {
        return motionVelY;
    }

    public static float getBlurOffsetBoost() {
        return 1.0f + motionAmount * 3.2f;
    }
}
