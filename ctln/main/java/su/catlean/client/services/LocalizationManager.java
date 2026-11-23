// deobf by pusia
package su.catlean.client.services;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.IllegalFormatException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.io.TextStreamsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.Charsets;
import kotlinx.serialization.json.Json;
import kotlinx.serialization.json.JsonElement;
import kotlinx.serialization.json.JsonElementKt;
import kotlinx.serialization.json.JsonObject;
import kotlinx.serialization.json.Json.Default;
import net.minecraft.client.resources.language.I18n;
import org.jetbrains.annotations.NotNull;
import su.catlean.CatLean;
import su.catlean.api.event.events.client.ScreenEvent;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;

public final class LocalizationManager {
   @NotNull
   public static final LocalizationManager INSTANCE = new LocalizationManager();
   @NotNull
   private static LocalizationManager.Language language = LocalizationManager.Language.EN;

   private LocalizationManager() {
   }

   @Flow
   private final void onScreen(ScreenEvent screenEvent) {
      if (!Intrinsics.areEqual(MinecraftHolder.getMinecraft().options.languageCode, language.getText())) {
         Iterable enumEntries = (Iterable)LocalizationManager.Language.getEnumEntries();
         Iterator iterator = enumEntries.iterator();

         Object object;
         while (true) {
            if (!iterator.hasNext()) {
               object = null;
               break;
            }

            Object object2 = iterator.next();
            LocalizationManager.Language language2 = (LocalizationManager.Language)object2;
            if (Intrinsics.areEqual(language2.getText(), MinecraftHolder.getMinecraft().options.languageCode)) {
               object = object2;
               break;
            }
         }

         object = (LocalizationManager.Language)object;
         if ((LocalizationManager.Language)object == null) {
            object = LocalizationManager.Language.EN;
         }

         language = (LocalizationManager.Language)object;
      }
   }

   @NotNull
   public final String computeText(String key, Object[] args) {
      String key2 = (String)language.getMap().get(key);
      if (key2 == null) {
         key2 = (String)LocalizationManager.Language.EN.getMap().get(key);
         if (key2 == null) {
            key2 = I18n.get(key, new Object[0]);
         }
      }

      String text = key2;

      String text2;
      try {
         Object[] copyOf = Arrays.copyOf(args, args.length);
         text2 = String.format(text, Arrays.copyOf(copyOf, copyOf.length));
      } catch (IllegalFormatException exception) {
         text2 = "Format error: " + text;
      }

      return text2;
   }

   public final void applyEntrySet() {
      LocalizationManager thisValue = this;

      try {
         LocalizationManager localizationManager = thisValue;

         for (LocalizationManager.Language enumEntry : (Iterable<su.catlean.client.services.LocalizationManager.Language>)LocalizationManager.Language.getEnumEntries()) {
            InputStream resourceAsStream = CatLean.class
               .getClassLoader()
               .getResourceAsStream("assets/catlean/translations/" + enumEntry.name().toLowerCase(Locale.ROOT) + ".ctlang");
            Default defaultValue = Json.Default;
            Charset utF_8 = Charsets.UTF_8;
            Reader inputStreamReader = new InputStreamReader(resourceAsStream, utF_8);
            short shortValue = 8192;
            JsonElement toJsonElement = defaultValue.parseToJsonElement(
               TextStreamsKt.readText(inputStreamReader instanceof BufferedReader ? (BufferedReader)inputStreamReader : new BufferedReader(inputStreamReader, shortValue))
            );
            if (toJsonElement instanceof JsonObject) {
               for (Entry entrySetItem : (Iterable<java.util.Map.Entry>)((Map)toJsonElement).entrySet()) {
                  String key = (String)entrySetItem.getKey();
                  JsonElement value = (JsonElement)entrySetItem.getValue();
                  enumEntry.getMap().put(key, JsonElementKt.getJsonPrimitive(value).getContent());
               }
            }
         }

         Unit instance = Unit.INSTANCE;
      } catch (Throwable exception) {
         Object failure = ResultKt.createFailure(exception);
      }
   }

   public static enum Language {
      EN("en_us", new LinkedHashMap()),
      RU("ru_ru", new LinkedHashMap()),
      PL("pl_pl", new LinkedHashMap());

      @NotNull
      private final String text;
      @NotNull
      private final Map map;
      private static final LocalizationManager.Language[] languages = $values();
      private static final EnumEntries<LocalizationManager.Language> enumEntries = EnumEntriesKt.enumEntries(languages);

      private Language(String text2, Map map) {
         this.text = text2;
         this.map = map;
      }

      @NotNull
      public final String getText() {
         return this.text;
      }

      @NotNull
      public final Map getMap() {
         return this.map;
      }

      @NotNull
      public static EnumEntries<LocalizationManager.Language> getEnumEntries() {
         return enumEntries;
      }

      private static final LocalizationManager.Language[] $values() {
         return new LocalizationManager.Language[]{EN, RU, PL};
      }
   }
}