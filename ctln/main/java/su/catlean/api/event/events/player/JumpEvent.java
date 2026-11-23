// deobf by pusia
package su.catlean.api.event.events.player;

import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.Event;
import su.catlean.gofra.Gofra;

public final class JumpEvent extends Event {
   @NotNull
   public static final JumpEvent INSTANCE = new JumpEvent();
   private static float yaw;
   private static float amount;
   private static float amount2;

   private JumpEvent() {
   }

   public final float getYaw() {
      return yaw;
   }

   public final void setYaw(float amount) {
      yaw = amount;
   }

   public final float getY() {
      return amount;
   }

   public final void setY(float amount) {
      JumpEvent.amount = amount;
   }

   public final float getXz() {
      return amount2;
   }

   public final void setXz(float amount) {
      amount2 = amount;
   }

   public final boolean call(float amount, float amount2, float amount3) {
      this.setCancelled(false);
      yaw = amount;
      JumpEvent.amount = amount2;
      JumpEvent.amount2 = amount3;
      Gofra.INSTANCE.drain(this);
      return this.getCancelled();
   }
}