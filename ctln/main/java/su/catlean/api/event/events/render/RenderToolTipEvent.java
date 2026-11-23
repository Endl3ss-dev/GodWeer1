// deobf by pusia
package su.catlean.api.event.events.render;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.catlean.api.event.Event;

public final class RenderToolTipEvent extends Event {
   @NotNull
   public static final RenderToolTipEvent INSTANCE = new RenderToolTipEvent();
   @Nullable
   private static ItemStack stack;
   @Nullable
   private static GuiGraphicsExtractor context;
   private static int mouseX;
   private static int mouseY;

   private RenderToolTipEvent() {
   }

   @Nullable
   public final ItemStack getStack() {
      return stack;
   }

   public final void setStack(ItemStack itemStack) {
      stack = itemStack;
   }

   @Nullable
   public final GuiGraphicsExtractor getContext() {
      return context;
   }

   public final void setContext(GuiGraphicsExtractor guiGraphicsExtractor) {
      context = guiGraphicsExtractor;
   }

   public final int getMouseX() {
      return mouseX;
   }

   public final void setMouseX(int value) {
      mouseX = value;
   }

   public final int getMouseY() {
      return mouseY;
   }

   public final void setMouseY(int value) {
      mouseY = value;
   }

   public final void call(ItemStack stack, GuiGraphicsExtractor context, int value, int value2) {
      RenderToolTipEvent.stack = stack;
      RenderToolTipEvent.context = context;
      mouseX = value;
      mouseY = value2;
      this.call();
   }
}