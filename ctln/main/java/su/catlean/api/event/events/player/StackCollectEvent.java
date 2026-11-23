// deobf by pusia
package su.catlean.api.event.events.player;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.Event;

public final class StackCollectEvent extends Event {
   @NotNull
   public static final StackCollectEvent INSTANCE = new StackCollectEvent();
   @NotNull
   private static ItemStack itemStack = Items.DIRT.getDefaultInstance();

   private StackCollectEvent() {
   }

   @NotNull
   public final ItemStack getItemStack() {
      return itemStack;
   }

   public final void setItemStack(ItemStack itemStack) {
      StackCollectEvent.itemStack = itemStack;
   }
}