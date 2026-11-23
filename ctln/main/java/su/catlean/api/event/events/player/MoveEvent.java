// deobf by pusia
package su.catlean.api.event.events.player;

import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.Event;
import su.catlean.gofra.Gofra;

public final class MoveEvent extends Event {
   @NotNull
   public static final MoveEvent INSTANCE = new MoveEvent();
   private static double amount;
   private static double amount2;
   private static double amount3;

   private MoveEvent() {
   }

   public final double getX() {
      return amount;
   }

   public final void setX(double amount) {
      MoveEvent.amount = amount;
   }

   public final double getY() {
      return amount2;
   }

   public final void setY(double amount) {
      amount2 = amount;
   }

   public final double getZ() {
      return amount3;
   }

   public final void setZ(double amount) {
      amount3 = amount;
   }

   public final boolean call(double amount, double amount2, double amount3) {
      this.setCancelled(false);
      MoveEvent.amount = amount;
      MoveEvent.amount2 = amount2;
      MoveEvent.amount3 = amount3;
      Gofra.INSTANCE.drain(this);
      return this.getCancelled();
   }
}