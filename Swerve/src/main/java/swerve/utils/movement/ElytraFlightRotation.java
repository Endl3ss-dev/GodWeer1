package swerve.utils.movement;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import swerve.events.movement.MoveInputEvent;

// Adapted from AlphaDLC/src/main/java/zov/alphadlc/util/rotation/RotationComponent.java,
// FreeLookComponent.java and util/render/math/GCDFixer.java.
// Only the task used by ElytraFlight: speeds 360/360, return 360/360, timeout 0, priority 3.
public final class ElytraFlightRotation {

    private static boolean active;
    private static boolean resetting;
    private static int idleTicks;
    private static float freeYaw;
    private static float freePitch;
    private static float targetYaw;
    private static float targetPitch;
    private static LocalPlayer owner;

    private ElytraFlightRotation() {
    }

    public static boolean isActive() {
        return active;
    }

    public static float getFreeYaw() {
        return freeYaw;
    }

    public static float getFreePitch() {
        return freePitch;
    }

    public static void captureCamera(float yaw, float pitch) {
        if (!active) {
            freeYaw = yaw;
            freePitch = pitch;
        }
    }

    public static void turn(double yaw, double pitch) {
        freePitch = Mth.clamp((float) (freePitch + pitch * 0.15D), -90, 90);
        freeYaw = (float) (freeYaw + yaw * 0.15D);
    }

    public static void update(float yaw, float pitch) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        owner = player;
        active = true;
        resetting = false;
        targetYaw = yaw;
        targetPitch = pitch;
        rotate(player, targetYaw, targetPitch);
    }

    // EventTick in AlphaDLC is Minecraft.tick HEAD, before EventPlayerUpdate.
    public static void tick(Minecraft mc) {
        LocalPlayer player = mc.player;
        if (player == null || player != owner || mc.level == null) {
            active = false;
            resetting = false;
            owner = null;
            return;
        }
        if (!active) return;
        if (!resetting && idleTicks > 0) resetting = true;
        if (resetting && rotate(player, freeYaw, freePitch)) {
            active = false;
            resetting = false;
        }
        idleTicks++;
    }

    private static boolean rotate(LocalPlayer player, float yaw, float pitch) {
        float yawDelta = Mth.wrapDegrees(yaw - player.getYRot());
        float pitchDelta = pitch - player.getXRot();
        float clampedYaw = Math.min(Math.abs(yawDelta), 360);
        float clampedPitch = Math.min(Math.abs(pitchDelta), 360);
        player.setYRot(player.getYRot() + fixRotate(Mth.clamp(yawDelta, -clampedYaw, clampedYaw)));
        player.setXRot(Mth.clamp(player.getXRot()
                + fixRotate(Mth.clamp(pitchDelta, -clampedPitch, clampedPitch)), -90, 90));
        idleTicks = 0;
        return Math.hypot(Math.abs(Mth.wrapDegrees(yaw - player.getYRot())),
                Math.abs(pitch - player.getXRot())) < 1;
    }

    public static float getGCDValue() {
        double sensitivity = Minecraft.getInstance().options.sensitivity().get() / 0.15F / 8.0D;
        double root = Math.cbrt(sensitivity);
        float factor = (float) ((root - 0.2F) / 0.6F * 0.6 + 0.2);
        return (float) (factor * factor * factor * 8 * 0.15);
    }

    private static float fixRotate(float delta) {
        float gcd = getGCDValue();
        // [+] Чувствительность мыши 0% — нижнее значение ползунка, и шаг обнуляется:
        // round(delta/0)*0 даёт 0.0 по всем осям, rotate() никогда не «догоняет» цель,
        // а resetting остаётся true — свободная камера и поворот элитры залипают.
        // Без квантования при нулевом шаге ротация работает как обычно (GcdUtil в
        // таком состоянии остаётся в безопасности: у него шаг = 0.2^3*8*0.15 > 0).
        if (!(gcd > 0.0F)) {
            return delta;
        }
        return Math.round(delta / gcd) * gcd;
    }

    public static void onMoveInput(MoveInputEvent event) {
        if (active) {
            fixMovement(event, Mth.wrapDegrees(Minecraft.getInstance().gameRenderer.getMainCamera().getYRot()));
        }
    }

    public static void fixMovement(MoveInputEvent event, float yaw) {
        float forward = event.forward;
        float strafe = event.strafe;
        if (forward == 0 && strafe == 0) return;

        // Preserve the source algorithm: both angles deliberately use the supplied yaw.
        double targetAngle = Mth.wrapDegrees(Math.toDegrees(direction(yaw, forward, strafe)));
        float bestForward = 0;
        float bestStrafe = 0;
        float smallestDifference = Float.MAX_VALUE;
        for (float testForward = -1; testForward <= 1; testForward++) {
            for (float testStrafe = -1; testStrafe <= 1; testStrafe++) {
                if (testForward == 0 && testStrafe == 0) continue;
                double testAngle = Mth.wrapDegrees(Math.toDegrees(direction(yaw, testForward, testStrafe)));
                float difference = Math.abs(Mth.wrapDegrees((float) (targetAngle - testAngle)));
                if (difference < smallestDifference) {
                    smallestDifference = difference;
                    bestForward = testForward;
                    bestStrafe = testStrafe;
                }
            }
        }
        event.forward = bestForward;
        event.strafe = bestStrafe;
    }

    private static double direction(float yaw, float forward, float strafe) {
        if (forward < 0) yaw += 180;
        float factor = 1;
        if (forward < 0) factor = -0.5F;
        if (forward > 0) factor = 0.5F;
        if (strafe > 0) yaw -= 90 * factor;
        if (strafe < 0) yaw += 90 * factor;
        return Math.toRadians(yaw);
    }
}
