// deobf by pusia
package su.catlean.api.event.events.render;

import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.Event;
import su.catlean.gofra.Gofra;

public final class ScreenMouseCoordsEvent extends Event {
   @NotNull
   public static final ScreenMouseCoordsEvent INSTANCE = new ScreenMouseCoordsEvent();
   private static int value;
   private static int value2;

   private ScreenMouseCoordsEvent() {
   }

   public final int getX() {
      return value;
   }

   public final void setX(int value) {
      ScreenMouseCoordsEvent.value = value;
   }

   public final int getY() {
      return value2;
   }

   public final void setY(int value) {
      value2 = value;
   }

   public final boolean call(int value, int value2) {
      this.setCancelled(false);
      Gofra.INSTANCE.drain(this);
      ScreenMouseCoordsEvent.value = value;
      ScreenMouseCoordsEvent.value2 = value2;
      return this.getCancelled();
   }
}