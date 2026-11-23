// deobf by pusia
package su.catlean.api.event.events.render;

import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.Event;

public final class Release2DEvent extends Event {
   @NotNull
   public static final Release2DEvent INSTANCE = new Release2DEvent();

   private Release2DEvent() {
   }
}