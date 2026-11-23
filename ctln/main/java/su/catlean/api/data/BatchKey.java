// deobf by pusia
package su.catlean.api.data;

import kotlin.jvm.internal.Intrinsics;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2f;

public final class BatchKey {
   // [deobf] final fields had no constructor in the original bytecode (class never instantiated);
   // dropped final to keep the original shape compilable.
   @NotNull
   private Identifier identifier;
   @NotNull
   private Matrix3x2f matrix3x2f;

   @NotNull
   public final Identifier getIdentifier() {
      return this.identifier;
   }

   @NotNull
   public final Matrix3x2f getMatrix3x2f() {
      return this.matrix3x2f;
   }

   @NotNull
   public final Identifier getIdentifier2() {
      return this.identifier;
   }

   @NotNull
   public final Matrix3x2f getMatrix3x2f2() {
      return this.matrix3x2f;
   }

   @NotNull
   @Override
   public String toString() {
      return "BatchKey(texture=" + this.identifier + ", matrix=" + this.matrix3x2f + ")";
   }

   @Override
   public int hashCode() {
      int hashCode = this.identifier.hashCode();
      return hashCode * 31 + this.matrix3x2f.hashCode();
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (!(object instanceof BatchKey batchKey)) {
         return false;
      } else {
         return !Intrinsics.areEqual(this.identifier, batchKey.identifier) ? false : Intrinsics.areEqual(this.matrix3x2f, batchKey.matrix3x2f);
      }
   }
}