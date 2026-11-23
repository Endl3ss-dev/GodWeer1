// deobf by pusia
package su.catlean.api.event.events.render;

import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.Event;
import su.catlean.gofra.Gofra;

public final class LookRenderEvent extends Event {
   @NotNull
   public static final LookRenderEvent INSTANCE = new LookRenderEvent();
   private static float yaw;
   private static float pitch;
   private static float bodyYaw;

   private LookRenderEvent() {
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

   public final float getBodyYaw() {
      return bodyYaw;
   }

   public final void setBodyYaw(float amount) {
      bodyYaw = amount;
   }

   public final boolean call(float amount, float amount2, float amount3) {
      this.setCancelled(false);
      yaw = amount;
      pitch = amount2;
      bodyYaw = amount3;
      Gofra.INSTANCE.drain(this);
      return this.getCancelled();
   }
}