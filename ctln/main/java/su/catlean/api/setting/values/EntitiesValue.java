// deobf by pusia
package su.catlean.api.setting.values;

import java.util.ArrayList;
import java.util.List;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

public final class EntitiesValue {
   @NotNull
   private final List entities;

   public EntitiesValue(List entities) {
      this.entities = entities;
   }

   public EntitiesValue(List items, int value, DefaultConstructorMarker defaultConstructorMarker) {
      if ((value & 1) != 0) {
         items = new ArrayList();
      }

      this(items);
   }

   @NotNull
   public final List getEntities() {
      return this.entities;
   }

   @NotNull
   public final List getEntities2() {
      return this.entities;
   }

   @NotNull
   @Override
   public String toString() {
      return "EntitiesValue(entities=" + this.entities + ")";
   }

   @Override
   public int hashCode() {
      return this.entities.hashCode();
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else {
         return !(object instanceof EntitiesValue entitiesValue) ? false : Intrinsics.areEqual(this.entities, entitiesValue.entities);
      }
   }
}