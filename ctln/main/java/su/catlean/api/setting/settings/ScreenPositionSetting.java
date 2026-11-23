// deobf by pusia
package su.catlean.api.setting.settings;

import kotlin.jvm.functions.Function0;
import kotlinx.serialization.DeserializationStrategy;
import kotlinx.serialization.SerializationStrategy;
import kotlinx.serialization.json.Json;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.ScreenPosition;
import su.catlean.api.setting.Setting;
import su.catlean.api.setting.values.GroupValue;

public final class ScreenPositionSetting extends Setting {
   public ScreenPositionSetting(String id, ScreenPosition defaultValue, GroupValue groupValue, Function0 visible) {
      super(id, defaultValue, null, groupValue, visible, 4, null);
   }

   @Override
   public void applyDecodeFromString(String setting) {
      Json json = Setting.companion.getJson();
      json.getSerializersModule();
      this.setObject2(json.decodeFromString((DeserializationStrategy)ScreenPosition.companion.getKSerializer(), setting));
   }

   @NotNull
   @Override
   public String toString() {
      Json json = Setting.companion.getJson();
      Object object2 = this.getObject2();
      json.getSerializersModule();
      return json.encodeToString((SerializationStrategy)ScreenPosition.companion.getKSerializer(), object2);
   }

   private static final boolean isFlag() {
      return true;
   }
}