package org.privates.client.utils.other;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

public class FallingPlayer {
    private final double motionX;
    private final double motionY;
    private final double motionZ;
    private final ClientPlayerEntity player;
    private final int simulatedTicks;
    private final double x;
    private final double y;
    private final float yaw;
    private final double z;

    public FallingPlayer(ClientPlayerEntity player, double x, double y, double z,
                         double motionX, double motionY, double motionZ, float yaw) {
        this.player = player;
        this.x = x;
        this.y = y;
        this.z = z;
        this.motionX = motionX;
        this.motionY = motionY;
        this.motionZ = motionZ;
        this.yaw = yaw;
        this.simulatedTicks = 0;
    }

    public static FallingPlayer fromPlayer(ClientPlayerEntity player) {
        return new FallingPlayer(
                player,
                player.getPositionVec().getX(),
                player.getPositionVec().getY(),
                player.getPositionVec().getZ(),
                player.getMotion().x,
                player.getMotion().y,
                player.getMotion().z,
                player.rotationYaw
        );
    }

    public boolean findFall(float threshold) {
        Vector3d lookVec = this.player.getLookVec();
        double motionX = this.motionX;
        double motionY = this.motionY;
        double motionZ = this.motionZ;

        float pitchRadians = this.player.rotationPitch * 0.017453292f; // degrees to radians
        float cosPitch = MathHelper.cos(pitchRadians);
        float pitchFactor = cosPitch * cosPitch;

        double lookLengthFactor = Math.min(lookVec.length() / 0.4, 1.0);
        float finalFactor = (float) (pitchFactor * lookLengthFactor);

        Vector3d motionVec = new Vector3d(motionX, motionY, motionZ);
        double adjustedY = motionVec.add(0.0, -1.0 + 0.75 * finalFactor, 0.0).y * 0.08;
        double resultY = adjustedY * 0.9800000190734863;

        return resultY < (double) threshold;
    }

    public boolean findFall(float threshold, int iterations) {
        Vector3d lookVec = this.player.getLookVec();
        double motionX = this.motionX;
        double motionY = this.motionY;
        double motionZ = this.motionZ;

        float pitchRadians = this.player.rotationPitch * 0.017453292f;
        float cosPitch = MathHelper.cos(pitchRadians);
        float pitchFactor = cosPitch * cosPitch;

        double lookLengthFactor = Math.min(lookVec.length() / 0.4, 1.0);
        float finalFactor = (float) (pitchFactor * lookLengthFactor);

        for (int i = 0; i < iterations; i++) {
            Vector3d motionVec = new Vector3d(motionX, motionY, motionZ);
            double adjustedY = motionVec.add(0.0, -1.0 + 0.75 * finalFactor, 0.0).y * 0.08;
            double resultY = adjustedY * 0.9800000190734863;

            if (resultY >= (double) threshold) {
                return false;
            }
        }
        return true;
    }
}