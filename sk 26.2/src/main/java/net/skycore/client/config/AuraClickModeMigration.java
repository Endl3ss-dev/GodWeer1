package net.skycore.client.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.Map;

/** Compatibility mapping for the Attack Aura click mode that became the retained 1.8 burst mode. */
public final class AuraClickModeMigration {
   public static final String SCHEMA_KEY = "_auraClickModeSchema";
   public static final int CURRENT_SCHEMA = 1;

   private AuraClickModeMigration() {
   }

   public static void migrate(JsonObject settings) {
      if (settings == null || hasCurrentSchema(settings.get(SCHEMA_KEY))) {
         return;
      }

      JsonElement oldMode = findClickMode(settings);
      if (oldMode instanceof JsonPrimitive primitive && primitive.isString()) {
         String mode = Translations.english(primitive.getAsString());
         String migrated = migrateLegacyMode(mode);
         if (!migrated.equals(mode)) {
            // Add the canonical English key so it takes precedence over translated legacy keys.
            settings.addProperty("Click mode", migrated);
         }
      }
      settings.addProperty(SCHEMA_KEY, CURRENT_SCHEMA);
   }

   public static String migrateLegacyMode(String mode) {
      if (mode == null) return null;
      if (mode.equalsIgnoreCase("1.8 NoDelay")) return "1.8";
      // Before the schema marker, 1.8 meant CPS; preserve the user's old cadence rather than
      // silently turning it into a multi-attack burst.
      if (mode.equalsIgnoreCase("1.8")) return "1.12";
      return mode;
   }

   private static boolean hasCurrentSchema(JsonElement value) {
      if (value == null || !value.isJsonPrimitive()) return false;
      try {
         return value.getAsInt() >= CURRENT_SCHEMA;
      } catch (RuntimeException ignored) {
         return false;
      }
   }

   private static JsonElement findClickMode(JsonObject settings) {
      JsonElement direct = settings.get("Click mode");
      if (direct != null) return direct;
      for (Map.Entry<String, JsonElement> entry : settings.entrySet()) {
         if ("Click mode".equalsIgnoreCase(Translations.english(entry.getKey()))) return entry.getValue();
      }
      return null;
   }
}
