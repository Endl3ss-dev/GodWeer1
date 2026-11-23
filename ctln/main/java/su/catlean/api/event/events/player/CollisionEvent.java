// deobf by pusia
package su.catlean.api.event.events.player;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.Event;
import su.catlean.gofra.Gofra;

public final class CollisionEvent extends Event {
   @NotNull
   private BlockState state = Blocks.AIR.defaultBlockState();
   @NotNull
   private BlockPos pos = BlockPos.ZERO;

   public CollisionEvent() {
   }

   @NotNull
   public final BlockState getState() {
      return this.state;
   }

   public final void setState(BlockState blockState) {
      this.state = blockState;
   }

   @NotNull
   public final BlockPos getPos() {
      return this.pos;
   }

   public final void setPos(BlockPos blockPos) {
      this.pos = blockPos;
   }

   public final boolean call(BlockState state, BlockPos pos) {
      this.setCancelled(false);
      this.pos = pos;
      this.state = state;
      Gofra.INSTANCE.drain(this);
      return this.getCancelled();
   }
}