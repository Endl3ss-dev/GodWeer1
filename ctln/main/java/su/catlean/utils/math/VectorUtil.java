// deobf by pusia
package su.catlean.utils.math;

import kotlin.jvm.internal.DefaultConstructorMarker;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.Rotation;
import su.catlean.client.services.RotationManager;

public final class VectorUtil {
   private VectorUtil() {
   }

   @NotNull
   public final Rotation computeRotation(Vec3 vector) {
      return RotationManager.INSTANCE.computeRotation(vector);
   }

   public VectorUtil(DefaultConstructorMarker defaultConstructorMarker) {
      this();
   }
}