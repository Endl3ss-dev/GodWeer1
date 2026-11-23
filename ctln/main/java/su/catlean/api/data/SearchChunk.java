// deobf by pusia
package su.catlean.api.data;

import java.util.List;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

public final class SearchChunk {
   private final int value;
   private final int value2;
   @NotNull
   private final List list;
   private boolean flag;

   public SearchChunk(int value, int value2, List list, boolean flag) {
      this.value = value;
      this.value2 = value2;
      this.list = list;
      this.flag = flag;
   }

   public SearchChunk(int value, int value2, List items, boolean flag, int value3, DefaultConstructorMarker defaultConstructorMarker) {
      if ((value3 & 8) != 0) {
         flag = false;
      }

      this(value, value2, items, flag);
   }

   public final int getValue() {
      return this.value;
   }

   public final int getValue2() {
      return this.value2;
   }

   @NotNull
   public final List getList() {
      return this.list;
   }

   public final boolean isFlag() {
      return this.flag;
   }

   public final void setFlag(boolean flag) {
      this.flag = flag;
   }

   public final int getValue3() {
      return this.value;
   }

   public final int getValue4() {
      return this.value2;
   }

   @NotNull
   public final List getList2() {
      return this.list;
   }

   public final boolean isFlag2() {
      return this.flag;
   }

   @NotNull
   @Override
   public String toString() {
      boolean flag2 = this.flag;
      List list2 = this.list;
      int value22 = this.value2;
      int value3 = this.value;
      return "SearchChunk(x=" + value3 + ", z=" + value22 + ", list=" + list2 + ", recalc=" + flag2 + ")";
   }

   @Override
   public int hashCode() {
      int hashCode = Integer.hashCode(this.value);
      hashCode = hashCode * 31 + Integer.hashCode(this.value2);
      hashCode = hashCode * 31 + this.list.hashCode();
      return hashCode * 31 + Boolean.hashCode(this.flag);
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (!(object instanceof SearchChunk searchChunk)) {
         return false;
      } else if (this.value != searchChunk.value) {
         return false;
      } else if (this.value2 != searchChunk.value2) {
         return false;
      } else {
         return !Intrinsics.areEqual(this.list, searchChunk.list) ? false : this.flag == searchChunk.flag;
      }
   }
}