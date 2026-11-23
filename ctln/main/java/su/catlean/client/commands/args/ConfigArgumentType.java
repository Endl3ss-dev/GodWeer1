// deobf by pusia
package su.catlean.client.commands.args;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.ServiceConfig;
import su.catlean.client.services.LocalizationManager;
import su.catlean.client.services.proxy.ProxyManager;

public final class ConfigArgumentType implements ArgumentType {
   @NotNull
   public static final ConfigArgumentType.Companion companion = new ConfigArgumentType.Companion(null);

   public ConfigArgumentType() {
   }

   @NotNull
   public String computeText(StringReader reader) throws CommandSyntaxException {
      String string = reader.readString();
      Iterable items = ProxyManager.INSTANCE.getModuleDataSource().getItems();
      var arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(items, 10));

      for (Object object : items) {
         ServiceConfig serviceConfig = (ServiceConfig)object;
         arrayList.add(serviceConfig.getId());
      }

      if (!arrayList.contains(string)) {
         throw new DynamicCommandExceptionType(arg -> {
            LocalizationManager instance = LocalizationManager.INSTANCE;
            Object[] objects = new Object[]{arg};
            return (Message)Component.literal(instance.computeText("commands.config.not-exist", objects));
         }).create(string);
      } else {
         return string;
      }
   }

   @NotNull
   public CompletableFuture listSuggestions(CommandContext context, SuggestionsBuilder builder) {
      Iterable items = ProxyManager.INSTANCE.getModuleDataSource().getItems();
      var arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(items, 10));

      for (Object object : items) {
         ServiceConfig serviceConfig = (ServiceConfig)object;
         arrayList.add(serviceConfig.getId());
      }

      return SharedSuggestionProvider.suggest(arrayList, builder);
   }

   @NotNull
   public List getItems() {
      Iterable items = ProxyManager.INSTANCE.getModuleDataSource().getItems();
      var arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(items, 10));

      for (Object object : items) {
         ServiceConfig serviceConfig = (ServiceConfig)object;
         arrayList.add(serviceConfig.getId());
      }

      return arrayList.stream().limit(5L).toList();
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
      public final ConfigArgumentType getConfigArgumentType() {
         return new ConfigArgumentType();
      }

      public Companion(DefaultConstructorMarker defaultConstructorMarker) {
         this();
      }
   }
}