// deobf by pusia
package su.catlean.api.event.events.player;

import net.minecraft.world.InteractionHand;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.Event;
import su.catlean.gofra.Gofra;

public final class PreInteractItemEvent extends Event {
   @NotNull
   public static final PreInteractItemEvent INSTANCE = new PreInteractItemEvent();
   @NotNull
   private static InteractionHand hand = InteractionHand.MAIN_HAND;

   private PreInteractItemEvent() {
   }

   @NotNull
   public final InteractionHand getHand() {
      return hand;
   }

   public final void setHand(InteractionHand interactionHand) {
      hand = interactionHand;
   }

   public final boolean call(InteractionHand hand) {
      PreInteractItemEvent.hand = hand;
      this.setCancelled(false);
      Gofra.INSTANCE.drain(this);
      return this.getCancelled();
   }
}