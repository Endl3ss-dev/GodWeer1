// deobf by pusia
package su.catlean.api.setting.values;

import java.util.ArrayList;
import java.util.List;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

public final class BlocksValue {
   @NotNull
   private final List blocks;

   public BlocksValue(List blocks) {
      this.blocks = blocks;
   }

   public BlocksValue(List items, int value, DefaultConstructorMarker defaultConstructorMarker) {
      if ((value & 1) != 0) {
         items = new ArrayList();
      }

      this(items);
   }

   @NotNull
   public final List getBlocks() {
      return this.blocks;
   }

   @NotNull
   public final List getBlocks2() {
      return this.blocks;
   }

   @NotNull
   @Override
   public String toString() {
      return "BlocksValue(blocks=" + this.blocks + ")";
   }

   @Override
   public int hashCode() {
      return this.blocks.hashCode();
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else {
         return !(object instanceof BlocksValue blocksValue) ? false : Intrinsics.areEqual(this.blocks, blocksValue.blocks);
      }
   }
}