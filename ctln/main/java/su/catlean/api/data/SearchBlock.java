// deobf by pusia
package su.catlean.api.data;

import java.awt.Color;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.NotNull;

public final class SearchBlock {
   @NotNull
   private final BlockPos pos;
   @NotNull
   private final Color color;

   public SearchBlock(BlockPos pos, Color color) {
      this.pos = pos;
      this.color = color;
   }

   @NotNull
   public final BlockPos getPos() {
      return this.pos;
   }

   @NotNull
   public final Color getColor() {
      return this.color;
   }

   @NotNull
   public final BlockPos getPos2() {
      return this.pos;
   }

   @NotNull
   public final Color getColor2() {
      return this.color;
   }

   @NotNull
   @Override
   public String toString() {
      return "SearchBlock(pos=" + this.pos + ", color=" + this.color + ")";
   }

   @Override
   public int hashCode() {
      int hashCode = this.pos.hashCode();
      return hashCode * 31 + this.color.hashCode();
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (!(object instanceof SearchBlock searchBlock)) {
         return false;
      } else {
         return !Intrinsics.areEqual(this.pos, searchBlock.pos) ? false : Intrinsics.areEqual(this.color, searchBlock.color);
      }
   }
}