// deobf by pusia
package su.catlean.api.data;

import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import org.jetbrains.annotations.NotNull;

public final class CrystalWrapper {
   @NotNull
   private final EndCrystal crystal;
   private final boolean flag;

   public CrystalWrapper(EndCrystal crystal, boolean flag) {
      this.crystal = crystal;
      this.flag = flag;
   }

   public CrystalWrapper(EndCrystal endCrystal, boolean flag, int value, DefaultConstructorMarker defaultConstructorMarker) {
      if ((value & 2) != 0) {
         flag = false;
      }

      this(endCrystal, flag);
   }

   @NotNull
   public final EndCrystal getCrystal() {
      return this.crystal;
   }

   public final boolean isFlag() {
      return this.flag;
   }

   @NotNull
   public final EndCrystal getCrystal2() {
      return this.crystal;
   }

   public final boolean isFlag2() {
      return this.flag;
   }

   @NotNull
   @Override
   public String toString() {
      return "CrystalWrapper(crystal=" + this.crystal + ", block=" + this.flag + ")";
   }

   @Override
   public int hashCode() {
      int hashCode = this.crystal.hashCode();
      return hashCode * 31 + Boolean.hashCode(this.flag);
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (!(object instanceof CrystalWrapper crystalWrapper)) {
         return false;
      } else {
         return !Intrinsics.areEqual(this.crystal, crystalWrapper.crystal) ? false : this.flag == crystalWrapper.flag;
      }
   }
}