// deobf by pusia
package su.catlean.api.setting.settings;

import kotlin.jvm.functions.Function0;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.setting.Setting;
import su.catlean.api.setting.values.ActionValue;
import su.catlean.api.setting.values.GroupValue;

public final class ActionSetting extends Setting {
   public ActionSetting(String id, ActionValue defaultValue, GroupValue groupValue, Function0 visible) {
      super(id, defaultValue, null, groupValue, visible, 4, null);
   }

   @Override
   public void applyDecodeFromString(String setting) {
   }

   @NotNull
   @Override
   public String toString() {
      return "action";
   }

   private static final boolean isFlag() {
      return true;
   }
}