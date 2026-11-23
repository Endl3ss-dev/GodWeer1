// deobf by pusia
package su.catlean.api.setting.settings;

import su.catlean.utils.KotlinCompat;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlinx.serialization.json.Json;
import kotlinx.serialization.json.JsonKt;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.setting.Setting;
import su.catlean.api.setting.values.GroupValue;

public final class GroupSetting extends Setting {
   @NotNull
   private final Json json = KotlinCompat.Json(null, arg -> {
      arg.setIgnoreUnknownKeys(true);
      arg.setEncodeDefaults(true);
      return Unit.INSTANCE;
   }, 1, null);

   public GroupSetting(String id, GroupValue defaultValue, Function0 visible) {
      super(id, defaultValue, null, null, visible, 12, null);
   }

   @Override
   public void applyDecodeFromString(String setting) {
      this.setObject2(this.getObject());
   }

   @NotNull
   @Override
   public String toString() {
      return "group-" + this.getId();
   }

   private static final boolean isFlag() {
      return true;
   }
}