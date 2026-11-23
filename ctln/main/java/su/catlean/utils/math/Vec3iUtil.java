// deobf by pusia
package su.catlean.utils.math;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public final class Vec3iUtil {
   @NotNull
   public static final Vec3 computeVec3(BlockPos self) {
      return Vec3.atCenterOf((Vec3i)self);
   }
}