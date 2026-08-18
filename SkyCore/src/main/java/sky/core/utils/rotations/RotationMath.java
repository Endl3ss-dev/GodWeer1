package sky.core.utils.rotations;

import lombok.experimental.UtilityClass;
import net.minecraft.util.math.vector.Vector3d;
import sky.core.utils.Wrapper;

@UtilityClass
public class RotationMath implements Wrapper {
    public static Vector3d toLookVector(float pitch, float yaw) {
        if (mc.player == null) {
            float pitchRad = pitch * ((float) Math.PI / 180.0F);
            float yawRad = -yaw * ((float) Math.PI / 180.0F);
            float cosPitch = (float) Math.cos(pitchRad);
            return new Vector3d(
                    Math.sin(yawRad) * cosPitch,
                    -Math.sin(pitchRad),
                    Math.cos(yawRad) * cosPitch
            );
        }
        return mc.player.getVectorForRotation(pitch, yaw);
    }
}
