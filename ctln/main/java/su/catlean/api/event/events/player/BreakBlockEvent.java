// deobf by pusia
package su.catlean.api.event.events.player;

import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.Event;
import su.catlean.gofra.Gofra;

public final class BreakBlockEvent extends Event {
   @NotNull
   public static final BreakBlockEvent INSTANCE = new BreakBlockEvent();
   @NotNull
   private static BlockPos pos = BlockPos.ZERO;

   private BreakBlockEvent() {
   }

   @NotNull
   public final BlockPos getPos() {
      return pos;
   }

   public final void setPos(BlockPos blockPos) {
      pos = blockPos;
   }

   public final boolean call(BlockPos pos) {
      this.setCancelled(false);
      BreakBlockEvent.pos = pos;
      Gofra.INSTANCE.drain(this);
      return this.getCancelled();
   }
}