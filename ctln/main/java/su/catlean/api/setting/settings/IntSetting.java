// deobf by pusia
package su.catlean.api.setting.settings;

import kotlin.jvm.functions.Function0;
import kotlin.ranges.IntRange;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.setting.Setting;
import su.catlean.api.setting.values.GroupValue;

public final class IntSetting extends Setting {
   @NotNull
   private final IntRange range;

   public IntSetting(String id, int value, IntRange range, GroupValue groupValue, Function0 visible) {
      super(id, value, null, groupValue, visible, 4, null);
      this.range = range;
   }

   @NotNull
   public final IntRange getRange() {
      return this.range;
   }

   @Override
   public void applyDecodeFromString(String setting) {
      this.setObject2(Integer.parseInt(setting));
   }

   @NotNull
   @Override
   public String toString() {
      return String.valueOf(((Number)this.getObject2()).intValue());
   }

   private static final boolean isFlag() {
      return true;
   }
}