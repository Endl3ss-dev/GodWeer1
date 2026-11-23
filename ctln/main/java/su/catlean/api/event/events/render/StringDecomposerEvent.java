// deobf by pusia
package su.catlean.api.event.events.render;

import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.Event;
import su.catlean.gofra.Gofra;

public final class StringDecomposerEvent extends Event {
   @NotNull
   public static final StringDecomposerEvent INSTANCE = new StringDecomposerEvent();
   @NotNull
   private static String string = "";

   private StringDecomposerEvent() {
   }

   @NotNull
   public final String getString() {
      return string;
   }

   public final void setString(String text) {
      string = text;
   }

   @NotNull
   public final String call(String string) {
      StringDecomposerEvent.string = string;
      Gofra.INSTANCE.drain(this);
      return StringDecomposerEvent.string;
   }
}