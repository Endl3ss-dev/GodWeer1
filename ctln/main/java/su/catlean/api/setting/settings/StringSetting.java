// deobf by pusia
package su.catlean.api.setting.settings;

import kotlin.jvm.functions.Function0;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.setting.Setting;
import su.catlean.api.setting.values.GroupValue;

public final class StringSetting extends Setting {
   public StringSetting(String settingId, String value, GroupValue groupValue, Function0 visible) {
      super(settingId, value, null, groupValue, visible, 4, null);
   }

   @Override
   public void applyDecodeFromString(String setting) {
      this.setObject2(setting);
   }

   @NotNull
   @Override
   public String toString() {
      return (String)this.getObject2();
   }

   private static final boolean isFlag() {
      return true;
   }
}