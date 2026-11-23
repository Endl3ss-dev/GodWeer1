// deobf by pusia
package su.catlean.api.data;

import kotlin.jvm.internal.Intrinsics;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;

public final class BlockPosWithFacing {
   @NotNull
   private final BlockPos position;
   @NotNull
   private final Direction facing;

   public BlockPosWithFacing(BlockPos position, Direction facing) {
      this.position = position;
      this.facing = facing;
   }

   @NotNull
   public final BlockPos getPosition() {
      return this.position;
   }

   @NotNull
   public final Direction getFacing() {
      return this.facing;
   }

   @NotNull
   public final BlockPos getPosition2() {
      return this.position;
   }

   @NotNull
   public final Direction getFacing2() {
      return this.facing;
   }

   @NotNull
   @Override
   public String toString() {
      return "BlockPosWithFacing(position=" + this.position + ", facing=" + this.facing + ")";
   }

   @Override
   public int hashCode() {
      int hashCode = this.position.hashCode();
      return hashCode * 31 + this.facing.hashCode();
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (!(object instanceof BlockPosWithFacing blockPosWithFacing)) {
         return false;
      } else {
         return !Intrinsics.areEqual(this.position, blockPosWithFacing.position) ? false : this.facing == blockPosWithFacing.facing;
      }
   }
}