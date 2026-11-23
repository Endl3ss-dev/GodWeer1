// deobf by pusia
package su.catlean.api.event.events.player;

import kotlin.jvm.internal.Intrinsics;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.Event;
import su.catlean.gofra.Gofra;

public final class CalcBlockBreakingDeltaEvent extends Event {
   @NotNull
   public static final CalcBlockBreakingDeltaEvent INSTANCE = new CalcBlockBreakingDeltaEvent();
   public static BlockPos blockPos;
   public static BlockState state;
   private static float delta;

   private CalcBlockBreakingDeltaEvent() {
   }

   @NotNull
   public final BlockPos getBlockPos() {
      if (blockPos != null) {
         return blockPos;
      } else {
         Intrinsics.throwUninitializedPropertyAccessException("blockPos");
         return null;
      }
   }

   public final void setBlockPos(BlockPos blockPos) {
      CalcBlockBreakingDeltaEvent.blockPos = blockPos;
   }

   @NotNull
   public final BlockState getState() {
      if (state != null) {
         return state;
      } else {
         Intrinsics.throwUninitializedPropertyAccessException("state");
         return null;
      }
   }

   public final void setState(BlockState blockState) {
      state = blockState;
   }

   public final float getDelta() {
      return delta;
   }

   public final void setDelta(float amount) {
      delta = amount;
   }

   public final boolean call(BlockPos pos, BlockState state) {
      this.setCancelled(false);
      this.setBlockPos(pos);
      INSTANCE.setState(state);
      Gofra.INSTANCE.drain(this);
      return this.getCancelled();
   }
}