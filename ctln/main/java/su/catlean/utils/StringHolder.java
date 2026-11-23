// deobf by pusia
package su.catlean.utils;

import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.SettingNameOverride;
import su.catlean.api.setting.settings.FloatSetting;
import su.catlean.api.setting.settings.IntSetting;
import su.catlean.api.setting.values.RangeBound;

public final class StringHolder {
   @NotNull
   public static final IntSetting computeIntSetting(IntSetting self, int value, String string) {
      RangeBound.INSTANCE.getItems().add(new SettingNameOverride(self.getId(), value, string));
      return self;
   }

   @NotNull
   public static final FloatSetting computeFloatSetting(FloatSetting self, float amount, String string) {
      RangeBound.INSTANCE.getItems().add(new SettingNameOverride(self.getId(), amount, string));
      return self;
   }
}