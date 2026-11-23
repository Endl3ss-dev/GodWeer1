// deobf by pusia
package su.catlean.api.event.events.render;

import kotlin.jvm.internal.Intrinsics;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.Event;
import su.catlean.gofra.Gofra;

public final class Render2DEvent extends Event {
   @NotNull
   public static final Render2DEvent INSTANCE = new Render2DEvent();
   private static GuiGraphicsExtractor context;

   private Render2DEvent() {
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

   public final boolean call(GuiGraphicsExtractor context) {
      this.setCancelled(false);
      Render2DEvent.context = context;
      Gofra.INSTANCE.drain(this);
      return this.getCancelled();
   }
}