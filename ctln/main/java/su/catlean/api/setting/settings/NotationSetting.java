// deobf by pusia
package su.catlean.api.setting.settings;

import kotlin.jvm.functions.Function0;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.setting.Setting;
import su.catlean.api.setting.values.GroupValue;
import su.catlean.api.setting.values.NotationValue;

public final class NotationSetting extends Setting {
   public NotationSetting(String id, NotationValue defaultValue, GroupValue groupValue, Function0 visible) {
      super(id, defaultValue, null, groupValue, visible, 4, null);
   }

   @Override
   public void applyDecodeFromString(String setting) {
   }

   @NotNull
   @Override
   public String toString() {
      return "notation";
   }

   private static final boolean isFlag() {
      return true;
   }
}