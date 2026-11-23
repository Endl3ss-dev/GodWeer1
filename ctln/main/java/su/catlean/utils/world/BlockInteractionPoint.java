// deobf by pusia
package su.catlean.utils.world;

import java.util.Optional;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import su.catlean.utils.MinecraftHolder;

public final class BlockInteractionPoint {
   @NotNull
   public static final BlockInteractionPoint INSTANCE = new BlockInteractionPoint();
   @NotNull
   private static final Float[] floatValues;

   private BlockInteractionPoint() {
   }

   @NotNull
   public final Float[] getFloatValues() {
      return floatValues;
   }

   public final boolean checkContains(Vec3 pos, Vec3 interactVec) {
      AABB aabb = new AABB(pos.subtract(1.0, 0.0, 1.0), pos.add(1.0, 2.0, 1.0));
      double entityInteractionRange = MinecraftHolder.getLocalPlayer().entityInteractionRange();
      double maX_VALUE = 0.0;
      maX_VALUE = Double.MAX_VALUE;
      Vec3 scale = interactVec.subtract(MinecraftHolder.getLocalPlayer().getEyePosition()).normalize().scale(entityInteractionRange);
      Vec3 vec3 = WorldUtil.INSTANCE.getVec3();
      Float[] floatValues2 = floatValues;
      int value = 0;

      for (int i = floatValues2.length; value < i; value++) {
         float value2 = floatValues2[value];
         Vec3 vec32 = vec3.add(0.0, value2, 0.0);
         Vec3 vec33 = vec32.add(scale);
         if (aabb.contains(vec33) || aabb.contains(vec32)) {
            return true;
         }

         Optional<Vec3> clip = aabb.clip(vec32, vec33);
         if (clip.isPresent()) {
            maX_VALUE = Math.min(maX_VALUE, vec32.distanceToSqr((Vec3)clip.get()));
         }
      }

      return maX_VALUE <= entityInteractionRange * entityInteractionRange;
   }

   static {
      Float[] floatValues2 = new Float[]{1.62F, 0.4F};
      floatValues = floatValues2;
   }
}