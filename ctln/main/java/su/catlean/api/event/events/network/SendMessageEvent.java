// deobf by pusia
package su.catlean.api.event.events.network;

import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.Event;
import su.catlean.gofra.Gofra;

public final class SendMessageEvent extends Event {
   @NotNull
   public static final SendMessageEvent INSTANCE = new SendMessageEvent();
   @NotNull
   private static String message = "";

   private SendMessageEvent() {
   }

   @NotNull
   public final String getMessage() {
      return message;
   }

   public final void setMessage(String text) {
      message = text;
   }

   public final boolean call(String message) {
      SendMessageEvent.message = message;
      this.setCancelled(false);
      Gofra.INSTANCE.drain(this);
      return this.getCancelled();
   }
}