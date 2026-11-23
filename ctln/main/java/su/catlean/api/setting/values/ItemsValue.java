// deobf by pusia
package su.catlean.api.setting.values;

import java.util.ArrayList;
import java.util.List;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

public final class ItemsValue {
   @NotNull
   private final List items;

   public ItemsValue(List items) {
      this.items = items;
   }

   public ItemsValue(List items, int value, DefaultConstructorMarker defaultConstructorMarker) {
      if ((value & 1) != 0) {
         items = new ArrayList();
      }

      this(items);
   }

   @NotNull
   public final List getItems() {
      return this.items;
   }

   @NotNull
   public final List getItems2() {
      return this.items;
   }

   @NotNull
   @Override
   public String toString() {
      return "ItemsValue(items=" + this.items + ")";
   }

   @Override
   public int hashCode() {
      return this.items.hashCode();
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else {
         return !(object instanceof ItemsValue itemsValue) ? false : Intrinsics.areEqual(this.items, itemsValue.items);
      }
   }
}