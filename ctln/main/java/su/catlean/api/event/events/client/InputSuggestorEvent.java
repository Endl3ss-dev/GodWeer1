// deobf by pusia
package su.catlean.api.event.events.client;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.commands.SharedSuggestionProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.catlean.api.event.Event;

public final class InputSuggestorEvent extends Event {
   @NotNull
   public static final InputSuggestorEvent INSTANCE = new InputSuggestorEvent();
   @NotNull
   private static String prefix = "";
   @Nullable
   private static CommandDispatcher<SharedSuggestionProvider> dispatcher;
   @Nullable
   private static ClientSuggestionProvider source;

   private InputSuggestorEvent() {
   }

   @NotNull
   public final String getPrefix() {
      return prefix;
   }

   public final void setPrefix(String text) {
      prefix = text;
   }

   @Nullable
   public final CommandDispatcher<SharedSuggestionProvider> getDispatcher() {
      return dispatcher;
   }

   public final void setDispatcher(CommandDispatcher<SharedSuggestionProvider> commandDispatcher) {
      dispatcher = commandDispatcher;
   }

   @Nullable
   public final ClientSuggestionProvider getSource() {
      return source;
   }

   public final void setSource(ClientSuggestionProvider clientSuggestionProvider) {
      source = clientSuggestionProvider;
   }
}