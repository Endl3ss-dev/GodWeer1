// deobf by pusia
package su.catlean.client.services;

import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.client.TickFactorEvent;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.service.Service;
import su.catlean.gofra.Flow;

public final class TickService implements Service {
   @NotNull
   public static final TickService INSTANCE = new TickService();
   private static float amount = 1.0F;

   private TickService() {
   }

   public final float getAmount() {
      return amount;
   }

   public final void setAmount(float amount) {
      TickService.amount = amount;
   }

   public final void setAmount2(float amount) {
      TickService.amount = amount;
   }

   @Flow(
      priority = 20
   )
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      this.setAmount2(1.0F);
   }

   @Flow
   private final void onTickFactor(TickFactorEvent tickFactorEvent) {
      if (amount != 1.0F) {
         tickFactorEvent.setFactor(amount);
         tickFactorEvent.cancel();
      }
   }

   static {
   }
}