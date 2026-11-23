// deobf by pusia
package su.catlean.api.event.events.render;

import kotlin.jvm.internal.Intrinsics;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.Event;
import su.catlean.gofra.Gofra;

public final class RenderScreenEvent extends Event {
   @NotNull
   public static final RenderScreenEvent INSTANCE = new RenderScreenEvent();
   private static GuiGraphicsExtractor context;
   private static int mouseX;
   private static int mouseY;
   private static float delta;

   private RenderScreenEvent() {
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

   public final int getMouseX() {
      return mouseX;
   }

   public final int getMouseY() {
      return mouseY;
   }

   public final float getDelta() {
      return delta;
   }

   public final boolean call(GuiGraphicsExtractor context) {
      return this.call(context, 0, 0, 0.0F);
   }

   public final boolean call(GuiGraphicsExtractor context, int value, int value2, float amount) {
      this.setCancelled(false);
      RenderScreenEvent.context = context;
      mouseX = value;
      mouseY = value2;
      delta = amount;
      Gofra.INSTANCE.drain(this);
      return this.getCancelled();
   }
}