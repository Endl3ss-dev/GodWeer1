package tech.lightclient.scripting.api;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

/** Working out where to look, mirroring the {@code rotations} object in the docs. */
public final class ScriptRotations {

    /** A yaw/pitch pair. */
    public static final class Rotation {
        private final float yaw;
        private final float pitch;

        public Rotation(float yaw, float pitch) {
            this.yaw = yaw;
            this.pitch = MathHelper.clamp(pitch, -90f, 90f);
        }

        public float yaw() { return yaw; }

        public float pitch() { return pitch; }

        /** Direction unit vector for this rotation. */
        public Vec3d direction() {
            return Vec3d.fromPolar(pitch, yaw);
        }

        @Override
        public String toString() {
            return "Rotation(" + yaw + ", " + pitch + ")";
        }
    }

    private static MinecraftClient mc() {
        return MinecraftClient.getInstance();
    }

    /** Rotation that points from your eyes at a world position. */
    public Rotation lookAt(Vec3d target) {
        MinecraftClient mc = mc();
        if (mc.player == null || target == null) return new Rotation(0f, 0f);
        Vec3d eyes = mc.player.getEyePos();
        double dx = target.x - eyes.x;
        double dy = target.y - eyes.y;
        double dz = target.z - eyes.z;
        double horizontal = Math.sqrt(dx * dx + dz * dz);
        float yaw = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0);
        float pitch = (float) -Math.toDegrees(Math.atan2(dy, horizontal));
        return new Rotation(MathHelper.wrapDegrees(yaw), pitch);
    }

    /** Rotation that points at an entity's middle. */
    public Rotation lookAt(WorldApi.ScriptEntity entity) {
        if (entity == null) return new Rotation(0f, 0f);
        Entity raw = entity.raw();
        return lookAt(raw.getEntityPos().add(0, raw.getHeight() / 2.0, 0));
    }

    /** Where you are actually looking right now. */
    public Rotation current() {
        MinecraftClient mc = mc();
        return mc.player == null ? new Rotation(0f, 0f) : new Rotation(mc.player.getYaw(), mc.player.getPitch());
    }

    /** Shortest signed angle between two yaws, in degrees. */
    public float yawDifference(float from, float to) {
        return MathHelper.wrapDegrees(to - from);
    }

    /** Total angular distance between two rotations, in degrees. */
    public float distance(Rotation a, Rotation b) {
        if (a == null || b == null) return 0f;
        float dy = Math.abs(MathHelper.wrapDegrees(b.yaw() - a.yaw()));
        float dp = Math.abs(b.pitch() - a.pitch());
        return (float) Math.sqrt(dy * dy + dp * dp);
    }
}
