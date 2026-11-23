// deobf by pusia
package su.catlean.api.data;

import kotlin.jvm.internal.Intrinsics;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public final class BreakData {
   @NotNull
   private final Direction dir;
   @NotNull
   private final Vec3 vector;

   public BreakData(Direction dir, Vec3 vector) {
      this.dir = dir;
      this.vector = vector;
   }

   @NotNull
   public final Direction getDir() {
      return this.dir;
   }

   @NotNull
   public final Vec3 getVector() {
      return this.vector;
   }

   @NotNull
   public final Direction getDir2() {
      return this.dir;
   }

   @NotNull
   public final Vec3 getVector2() {
      return this.vector;
   }

   @NotNull
   @Override
   public String toString() {
      return "BreakData(dir=" + this.dir + ", vector=" + this.vector + ")";
   }

   @Override
   public int hashCode() {
      int hashCode = this.dir.hashCode();
      return hashCode * 31 + this.vector.hashCode();
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (!(object instanceof BreakData breakData)) {
         return false;
      } else {
         return this.dir != breakData.dir ? false : Intrinsics.areEqual(this.vector, breakData.vector);
      }
   }
}