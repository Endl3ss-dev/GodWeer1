// deobf by pusia
package su.catlean.api.setting.settings;

import kotlin.jvm.functions.Function0;
import kotlin.ranges.ClosedFloatingPointRange;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.setting.Setting;
import su.catlean.api.setting.values.GroupValue;

public final class FloatSetting extends Setting {
   @NotNull
   private final ClosedFloatingPointRange range;

   public FloatSetting(String id, float amount, ClosedFloatingPointRange range, GroupValue groupValue, Function0 visible) {
      super(id, amount, null, groupValue, visible, 4, null);
      this.range = range;
   }

   @NotNull
   public final ClosedFloatingPointRange getRange() {
      return this.range;
   }

   @Override
   public void applyDecodeFromString(String setting) {
      this.setObject2(Float.parseFloat(setting));
   }

   @NotNull
   @Override
   public String toString() {
      return String.valueOf(((Number)this.getObject2()).floatValue());
   }

   private static final boolean isFlag() {
      return true;
   }
}