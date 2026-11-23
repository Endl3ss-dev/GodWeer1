// deobf by pusia
package su.catlean.client.commands.args;

import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.commands.SharedSuggestionProvider;
import org.jetbrains.annotations.NotNull;
import su.catlean.utils.MinecraftHolder;

public final class PlayerArgumentType implements ArgumentType {
   @NotNull
   public static final PlayerArgumentType.Companion companion = new PlayerArgumentType.Companion(null);

   public PlayerArgumentType() {
   }

   @NotNull
   public String computeText(StringReader reader) throws CommandSyntaxException {
      String string = reader.readString();
      PlayerInfo playerInfo = MinecraftHolder.getMinecraft()
         .getConnection()
         .getOnlinePlayers()
         .stream()
         .filter(arg -> Intrinsics.areEqual(string, arg.getProfile().name()))
         .findFirst()
         .orElse(null);
      return playerInfo == null ? string : playerInfo.getProfile().name();
   }

   @NotNull
   public CompletableFuture<Suggestions> listSuggestions(CommandContext context, SuggestionsBuilder builder) {
      return SharedSuggestionProvider.suggest(
         MinecraftHolder.getMinecraft().getConnection().getOnlinePlayers().stream().map(arg -> arg.getProfile().name()), builder
      );
   }

   @NotNull
   public List<String> getItems() {
      return MinecraftHolder.getMinecraft().getConnection().getOnlinePlayers().stream().map(arg -> arg.getProfile().name()).limit(5L).toList();
   }

   public Object parse(StringReader stringReader) throws CommandSyntaxException {
      return this.computeText(stringReader);
   }

   public Collection<String> getExamples() {
      return this.getItems();
   }

   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final PlayerArgumentType getPlayerArgumentType() {
         return new PlayerArgumentType();
      }

      public Companion(DefaultConstructorMarker defaultConstructorMarker) {
         this();
      }
   }
}