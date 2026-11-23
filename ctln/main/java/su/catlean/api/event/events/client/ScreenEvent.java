// deobf by pusia
package su.catlean.api.event.events.client;

import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.catlean.api.event.Event;
import su.catlean.gofra.Gofra;

public final class ScreenEvent extends Event {
   @NotNull
   public static final ScreenEvent INSTANCE = new ScreenEvent();
   @Nullable
   private static Screen screen;

   private ScreenEvent() {
   }

   @Nullable
   public final Screen getScreen() {
      return screen;
   }

   public final void setScreen(Screen screen) {
      ScreenEvent.screen = screen;
   }

   public final boolean call(Screen screen) {
      this.setCancelled(false);
      ScreenEvent.screen = screen;
      Gofra.INSTANCE.drain(this);
      return this.getCancelled();
   }
}