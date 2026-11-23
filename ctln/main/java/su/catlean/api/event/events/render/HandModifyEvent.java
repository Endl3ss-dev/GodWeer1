// deobf by pusia
package su.catlean.api.event.events.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.Event;
import su.catlean.gofra.Gofra;

public final class HandModifyEvent extends Event {
   @NotNull
   public static final HandModifyEvent INSTANCE = new HandModifyEvent();
   @NotNull
   private static InteractionHand hand = InteractionHand.MAIN_HAND;
   @NotNull
   private static PoseStack stack = new PoseStack();
   @NotNull
   private static ItemStack item = Items.AIR.getDefaultInstance();
   private static boolean eating;

   private HandModifyEvent() {
   }

   @NotNull
   public final InteractionHand getHand() {
      return hand;
   }

   public final void setHand(InteractionHand interactionHand) {
      hand = interactionHand;
   }

   @NotNull
   public final PoseStack getStack() {
      return stack;
   }

   public final void setStack(PoseStack poseStack) {
      stack = poseStack;
   }

   @NotNull
   public final ItemStack getItem() {
      return item;
   }

   public final void setItem(ItemStack itemStack) {
      item = itemStack;
   }

   public final boolean getEating() {
      return eating;
   }

   public final void setEating(boolean flag) {
      eating = flag;
   }

   public final boolean call(InteractionHand hand, PoseStack stack, ItemStack item, boolean flag) {
      this.setCancelled(false);
      HandModifyEvent.stack = stack;
      HandModifyEvent.hand = hand;
      HandModifyEvent.item = item;
      eating = flag;
      Gofra.INSTANCE.drain(this);
      return this.getCancelled();
   }
}