// deobf by pusia
package su.catlean.api.event.events.player;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.Event;

public final class InsertStackEvent extends Event {
   @NotNull
   public static final InsertStackEvent INSTANCE = new InsertStackEvent();
   @NotNull
   private static ItemStack stack = ItemStack.EMPTY;

   private InsertStackEvent() {
   }

   @NotNull
   public final ItemStack getStack() {
      return stack;
   }

   public final void setStack(ItemStack itemStack) {
      stack = itemStack;
   }
}