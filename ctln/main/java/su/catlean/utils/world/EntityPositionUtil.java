// deobf by pusia
package su.catlean.utils.world;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import su.catlean.interfaces.IEntity;

public final class EntityPositionUtil {
   @NotNull
   public static final Vec3 computeVec3(Entity self) {
      return ((IEntity)self).catLean$getPosition();
   }
}