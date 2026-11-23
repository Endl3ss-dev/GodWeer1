// deobf by pusia
package su.catlean.client.services;

import su.catlean.utils.KotlinCompat;
import kotlin.Unit;
import kotlinx.serialization.json.Json;
import kotlinx.serialization.json.JsonKt;
import org.jetbrains.annotations.NotNull;

public final class JsonProvider {
   @NotNull
   private static final Json json = KotlinCompat.Json(null, arg -> {
      arg.setIgnoreUnknownKeys(true);
      arg.setLenient(true);
      return Unit.INSTANCE;
   }, 1, null);

   @NotNull
   public static final Json getJson() {
      return json;
   }
}