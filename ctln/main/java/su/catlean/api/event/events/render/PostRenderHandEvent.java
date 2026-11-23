// deobf by pusia
package su.catlean.api.event.events.render;

import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.Event;

public final class PostRenderHandEvent extends Event {
   @NotNull
   public static final PostRenderHandEvent INSTANCE = new PostRenderHandEvent();

   private PostRenderHandEvent() {
   }
}