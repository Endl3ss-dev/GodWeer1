// deobf by pusia
package su.catlean.api.setting.settings;

import su.catlean.utils.KotlinCompat;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlinx.serialization.DeserializationStrategy;
import kotlinx.serialization.SerializationStrategy;
import kotlinx.serialization.json.Json;
import kotlinx.serialization.json.JsonKt;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.setting.Setting;
import su.catlean.api.setting.values.Bind;

public final class BindSetting extends Setting {
   @NotNull
   private final Json json = KotlinCompat.Json(null, arg -> {
      arg.setIgnoreUnknownKeys(true);
      arg.setEncodeDefaults(true);
      return Unit.INSTANCE;
   }, 1, null);

   public BindSetting(String id, Bind defaultValue, Function0 visible) {
      super(id, defaultValue, null, null, visible, 12, null);
   }

   @Override
   public void applyDecodeFromString(String setting) {
      Json json2 = this.json;
      json2.getSerializersModule();
      this.setObject2(json2.decodeFromString((DeserializationStrategy)Bind.companion.getKSerializer(), setting));
   }

   @NotNull
   @Override
   public String toString() {
      Json json2 = this.json;
      Object object2 = this.getObject2();
      json2.getSerializersModule();
      return json2.encodeToString((SerializationStrategy)Bind.companion.getKSerializer(), object2);
   }

   private static final boolean isFlag() {
      return true;
   }
}