// deobf by pusia
package su.catlean.api.event.events.player;

import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.Event;

public final class FreecamStateEvent extends Event {
   @NotNull
   public static final FreecamStateEvent INSTANCE = new FreecamStateEvent();
   private static double amount;
   private static double amount2;
   private static double amount3;
   private static float yaw;
   private static float pitch;

   private FreecamStateEvent() {
   }

   public final double getX() {
      return amount;
   }

   public final void setX(double amount) {
      FreecamStateEvent.amount = amount;
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

   public final float getYaw() {
      return yaw;
   }

   public final void setYaw(float amount) {
      yaw = amount;
   }

   public final float getPitch() {
      return pitch;
   }

   public final void setPitch(float amount) {
      pitch = amount;
   }
}