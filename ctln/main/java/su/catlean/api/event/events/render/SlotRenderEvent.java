// deobf by pusia
package su.catlean.api.event.events.render;

import kotlin.jvm.internal.Intrinsics;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.Event;

public final class SlotRenderEvent extends Event {
   @NotNull
   public static final SlotRenderEvent INSTANCE = new SlotRenderEvent();
   public static GuiGraphicsExtractor context;
   public static Slot slot;

   private SlotRenderEvent() {
   }

   @NotNull
   public final GuiGraphicsExtractor getContext() {
      if (context != null) {
         return context;
      } else {
         Intrinsics.throwUninitializedPropertyAccessException("context");
         return null;
      }
   }

   public final void setContext(GuiGraphicsExtractor guiGraphicsExtractor) {
      context = guiGraphicsExtractor;
   }

   @NotNull
   public final Slot getSlot() {
      if (slot != null) {
         return slot;
      } else {
         Intrinsics.throwUninitializedPropertyAccessException("slot");
         return null;
      }
   }

   public final void setSlot(Slot slot) {
      SlotRenderEvent.slot = slot;
   }
}