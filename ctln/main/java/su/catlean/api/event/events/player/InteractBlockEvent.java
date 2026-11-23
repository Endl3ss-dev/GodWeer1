// deobf by pusia
package su.catlean.api.event.events.player;

import kotlin.jvm.internal.Intrinsics;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.Event;
import su.catlean.gofra.Gofra;

public final class InteractBlockEvent extends Event {
   @NotNull
   public static final InteractBlockEvent INSTANCE = new InteractBlockEvent();
   public static InteractionHand hand;
   public static BlockHitResult hitResult;

   private InteractBlockEvent() {
   }

   @NotNull
   public final InteractionHand getHand() {
      if (hand != null) {
         return hand;
      } else {
         Intrinsics.throwUninitializedPropertyAccessException("hand");
         return null;
      }
   }

   public final void setHand(InteractionHand interactionHand) {
      hand = interactionHand;
   }

   @NotNull
   public final BlockHitResult getHitResult() {
      if (hitResult != null) {
         return hitResult;
      } else {
         Intrinsics.throwUninitializedPropertyAccessException("hitResult");
         return null;
      }
   }

   public final void setHitResult(BlockHitResult blockHitResult) {
      hitResult = blockHitResult;
   }

   public final boolean call(InteractionHand hand, BlockHitResult hitResult) {
      this.setHand(hand);
      this.setHitResult(hitResult);
      this.setCancelled(false);
      Gofra.INSTANCE.drain(this);
      return this.getCancelled();
   }
}