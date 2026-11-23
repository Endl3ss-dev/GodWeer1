// deobf by pusia
package su.catlean.api.event.events.world;

import kotlin.jvm.internal.Intrinsics;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.catlean.api.event.Event;
import su.catlean.gofra.Gofra;

public final class BlockStateEvent extends Event {
   @NotNull
   public static final BlockStateEvent INSTANCE = new BlockStateEvent();
   private static BlockPos pos;
   @Nullable
   private static BlockState prevState;
   @Nullable
   private static BlockState state;

   private BlockStateEvent() {
   }

   @NotNull
   public final BlockPos getPos() {
      if (pos != null) {
         return pos;
      } else {
         Intrinsics.throwUninitializedPropertyAccessException("pos");
         return null;
      }
   }

   @Nullable
   public final BlockState getPrevState() {
      return prevState;
   }

   public final void setPrevState(BlockState blockState) {
      prevState = blockState;
   }

   @Nullable
   public final BlockState getState() {
      return state;
   }

   public final boolean call(BlockPos pos, BlockState blockState, BlockState blockState2) {
      this.setCancelled(false);
      BlockStateEvent.pos = pos;
      state = blockState2;
      prevState = blockState;
      Gofra.INSTANCE.drain(this);
      return this.getCancelled();
   }
}