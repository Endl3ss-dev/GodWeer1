// deobf by pusia
package su.catlean.api.event.events.player;

import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.Event;

public final class ReachStateEvent extends Event {
   @NotNull
   public static final ReachStateEvent INSTANCE = new ReachStateEvent();
   private static float blockRange;
   private static float entityRange;

   private ReachStateEvent() {
   }

   public final float getBlockRange() {
      return blockRange;
   }

   public final void setBlockRange(float amount) {
      blockRange = amount;
   }

   public final float getEntityRange() {
      return entityRange;
   }

   public final void setEntityRange(float amount) {
      entityRange = amount;
   }
}