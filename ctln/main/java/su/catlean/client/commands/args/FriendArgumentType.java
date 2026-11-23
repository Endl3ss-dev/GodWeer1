// deobf by pusia
package su.catlean.client.commands.args;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import kotlin.jvm.internal.DefaultConstructorMarker;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import su.catlean.client.services.FriendManager;
import su.catlean.client.services.LocalizationManager;
import su.catlean.client.services.proxy.ProxyManager;

public final class FriendArgumentType implements ArgumentType {
   @NotNull
   public static final FriendArgumentType.Companion companion = new FriendArgumentType.Companion(null);

   public FriendArgumentType() {
   }

   @NotNull
   public String computeText(StringReader reader) throws CommandSyntaxException {
      String string = reader.readString();
      if (!FriendManager.INSTANCE.checkContains(string)) {
         throw new DynamicCommandExceptionType(arg -> {
            LocalizationManager instance = LocalizationManager.INSTANCE;
            Object[] objects = new Object[]{arg};
            return (Message)Component.literal(instance.computeText("commands.friend.not-exist", objects));
         }).create(string);
      } else {
         return string;
      }
   }

   @NotNull
   public CompletableFuture listSuggestions(CommandContext context, SuggestionsBuilder builder) {
      return SharedSuggestionProvider.suggest(ProxyManager.INSTANCE.getFriendsDataSource().getItems(), builder);
   }

   @NotNull
   public List getItems() {
      return ProxyManager.INSTANCE.getFriendsDataSource().getItems().stream().limit(5L).toList();
   }

   public Object parse(StringReader stringReader) throws CommandSyntaxException {
      return this.computeText(stringReader);
   }

   public Collection getExamples() {
      return this.getItems();
   }

   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final FriendArgumentType getFriendArgumentType() {
         return new FriendArgumentType();
      }

      public Companion(DefaultConstructorMarker defaultConstructorMarker) {
         this();
      }
   }
}