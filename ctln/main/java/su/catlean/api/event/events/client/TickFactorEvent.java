// deobf by pusia
package su.catlean.api.event.events.client;

import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.Event;

public final class TickFactorEvent extends Event {
   @NotNull
   public static final TickFactorEvent INSTANCE = new TickFactorEvent();
   private static float factor = 1.0F;

   private TickFactorEvent() {
   }

   public final float getFactor() {
      return factor;
   }

   public final void setFactor(float amount) {
      factor = amount;
   }

   @Override
   public boolean call() {
      factor = 1.0F;
      return super.call();
   }
}