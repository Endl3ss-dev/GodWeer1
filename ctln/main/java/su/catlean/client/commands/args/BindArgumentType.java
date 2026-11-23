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
import net.minecraft.commands.SharedSuggestionProvider;
import org.jetbrains.annotations.NotNull;
import su.catlean.client.features.modules.Module;
import su.catlean.client.services.ModuleManager;

public final class BindArgumentType implements ArgumentType {
   @NotNull
   public static final BindArgumentType.Companion companion = new BindArgumentType.Companion(null);
   @NotNull
   private static final Collection items;

   public BindArgumentType() {
   }

   @NotNull
   public Module computeModule(StringReader reader) throws CommandSyntaxException {
      ModuleManager instance = ModuleManager.INSTANCE;
      String string = reader.readString();
      return instance.computeModule(string);
   }

   @NotNull
   public CompletableFuture<Suggestions> listSuggestions(CommandContext context, SuggestionsBuilder builder) {
      return SharedSuggestionProvider.suggest(ModuleManager.INSTANCE.getItems2().stream().map(arg -> ((Module)arg).getId()), builder);
   }

   @NotNull
   public Collection getExamples() {
      return items;
   }

   public Object parse(StringReader stringReader) throws CommandSyntaxException {
      return this.computeModule(stringReader);
   }

   static {
      List<String> items2 = ModuleManager.INSTANCE.getItems2().stream().map(arg -> ((Module)arg).getId()).limit(5L).toList();
      items = items2;
   }

   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final BindArgumentType getBindArgumentType() {
         return new BindArgumentType();
      }

      public Companion(DefaultConstructorMarker defaultConstructorMarker) {
         this();
      }
   }
}