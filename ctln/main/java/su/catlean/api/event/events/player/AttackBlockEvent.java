// deobf by pusia
package su.catlean.api.event.events.player;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.Event;
import su.catlean.gofra.Gofra;

public final class AttackBlockEvent extends Event {
   @NotNull
   public static final AttackBlockEvent INSTANCE = new AttackBlockEvent();
   @NotNull
   private static Direction direction = Direction.UP;
   @NotNull
   private static BlockPos pos = BlockPos.ZERO;

   private AttackBlockEvent() {
   }

   @NotNull
   public final Direction getDirection() {
      return direction;
   }

   public final void setDirection(Direction direction) {
      AttackBlockEvent.direction = direction;
   }

   @NotNull
   public final BlockPos getPos() {
      return pos;
   }

   public final void setPos(BlockPos blockPos) {
      pos = blockPos;
   }

   public final boolean call(Direction direction, BlockPos pos) {
      this.setCancelled(false);
      AttackBlockEvent.pos = pos;
      AttackBlockEvent.direction = direction;
      Gofra.INSTANCE.drain(this);
      return this.getCancelled();
   }
}