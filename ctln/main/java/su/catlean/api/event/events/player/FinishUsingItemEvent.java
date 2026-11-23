// deobf by pusia
package su.catlean.api.event.events.player;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.catlean.api.event.Event;
import su.catlean.gofra.Gofra;

public final class FinishUsingItemEvent extends Event {
   @NotNull
   public static final FinishUsingItemEvent INSTANCE = new FinishUsingItemEvent();
   @Nullable
   private static ItemStack item;

   private FinishUsingItemEvent() {
   }

   @Nullable
   public final ItemStack getItem() {
      return item;
   }

   public final void setItem(ItemStack itemStack) {
      item = itemStack;
   }

   public final boolean call(ItemStack item) {
      this.setCancelled(false);
      FinishUsingItemEvent.item = item;
      Gofra.INSTANCE.drain(this);
      return this.getCancelled();
   }
}