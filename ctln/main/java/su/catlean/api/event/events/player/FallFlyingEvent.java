// deobf by pusia
package su.catlean.api.event.events.player;

import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.Event;
import su.catlean.gofra.Gofra;

public final class FallFlyingEvent extends Event {
   @NotNull
   public static final FallFlyingEvent INSTANCE = new FallFlyingEvent();
   private static boolean value;

   private FallFlyingEvent() {
   }

   public final boolean getValue() {
      return value;
   }

   public final void setValue(boolean flag) {
      value = flag;
   }

   public final boolean call(boolean flag) {
      this.setCancelled(false);
      value = flag;
      Gofra.INSTANCE.drain(this);
      return this.getCancelled();
   }
}