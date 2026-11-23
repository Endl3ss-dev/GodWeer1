// deobf by pusia
package su.catlean.api.event.events.render;

import kotlin.jvm.internal.Intrinsics;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.Event;
import su.catlean.gofra.Gofra;

public final class ShowFloatingItemEvent extends Event {
   @NotNull
   public static final ShowFloatingItemEvent INSTANCE = new ShowFloatingItemEvent();
   public static ItemStack item;

   private ShowFloatingItemEvent() {
   }

   @NotNull
   public final ItemStack getItem() {
      if (item != null) {
         return item;
      } else {
         Intrinsics.throwUninitializedPropertyAccessException("item");
         return null;
      }
   }

   public final void setItem(ItemStack itemStack) {
      item = itemStack;
   }

   public final boolean call(ItemStack item) {
      this.setCancelled(false);
      this.setItem(item);
      Gofra.INSTANCE.drain(this);
      return this.getCancelled();
   }
}