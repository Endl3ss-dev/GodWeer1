// deobf by pusia
package su.catlean.api.setting.settings;

import kotlin.jvm.functions.Function0;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.setting.Setting;
import su.catlean.api.setting.values.GroupValue;

public final class BooleanSetting extends Setting {
   public BooleanSetting(String id, boolean flag, GroupValue groupValue, Function0 visible) {
      super(id, flag, null, groupValue, visible, 4, null);
   }

   @Override
   public void applyDecodeFromString(String setting) {
      this.setObject2(Boolean.parseBoolean(setting));
   }

   @NotNull
   @Override
   public String toString() {
      return String.valueOf(((Boolean)this.getObject2()).booleanValue());
   }

   private static final boolean isFlag() {
      return true;
   }
}