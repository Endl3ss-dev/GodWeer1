// deobf by pusia
package su.catlean.api.data;

import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

public final class DamageData {
   private final Object object;
   @NotNull
   private final DamageComparatorResult damageData;

   public DamageData(Object object, DamageComparatorResult damageData) {
      this.object = object;
      this.damageData = damageData;
   }

   public final Object getObject() {
      return this.object;
   }

   @NotNull
   public final DamageComparatorResult getDamageData() {
      return this.damageData;
   }

   public final Object getObject2() {
      return this.object;
   }

   @NotNull
   public final DamageComparatorResult getDamageData2() {
      return this.damageData;
   }

   @NotNull
   @Override
   public String toString() {
      return "DamageData(data=" + this.object + ", damageData=" + this.damageData + ")";
   }

   @Override
   public int hashCode() {
      int hashCode = this.object == null ? 0 : this.object.hashCode();
      return hashCode * 31 + this.damageData.hashCode();
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (!(object instanceof DamageData damageData2)) {
         return false;
      } else {
         return !Intrinsics.areEqual(this.object, damageData2.object) ? false : Intrinsics.areEqual(this.damageData, damageData2.damageData);
      }
   }
}