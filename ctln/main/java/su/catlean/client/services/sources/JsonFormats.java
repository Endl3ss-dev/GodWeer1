// deobf by pusia
package su.catlean.client.services.sources;

import su.catlean.utils.KotlinCompat;
import kotlin.Unit;
import kotlinx.serialization.json.Json;
import kotlinx.serialization.json.JsonKt;
import kotlinx.serialization.json.JsonNamingStrategy;
import org.jetbrains.annotations.NotNull;

public final class JsonFormats {
   @NotNull
   private static final Json json = KotlinCompat.Json(null, arg -> {
      arg.setIgnoreUnknownKeys(true);
      arg.setExplicitNulls(false);
      arg.setNamingStrategy(JsonNamingStrategy.Builtins.getSnakeCase());
      return Unit.INSTANCE;
   }, 1, null);

   @NotNull
   public static final Json getJson() {
      return json;
   }
}