// deobf by pusia
package su.catlean.api.setting.settings;

import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.setting.Setting;
import su.catlean.api.setting.values.GroupValue;

public final class EnumSetting extends Setting {
   public EnumSetting(String id, Enum defaultValue, GroupValue groupValue, Function0 visible) {
      super(id, defaultValue, null, groupValue, visible, 4, null);
   }

   @Override
   public void applyDecodeFromString(String setting) {
      Object[] enumConstants = this.getObject().getClass().getEnumConstants();
      int value = 0;
      int length = enumConstants.length;

      Object object;
      while (true) {
         if (value >= length) {
            object = null;
            break;
         }

         Object object2 = enumConstants[value];
         Enum enumValue = (Enum)object2;
         if (Intrinsics.areEqual(enumValue.name(), setting)) {
            object = object2;
            break;
         }

         value++;
      }

      Enum object3 = (Enum)object;
      if ((Enum)object == null) {
         object3 = (Enum)this.getObject();
      }

      this.setObject2(object3);
   }

   @NotNull
   @Override
   public String toString() {
      return ((Enum)this.getObject2()).name();
   }

   private static final boolean isFlag() {
      return true;
   }
}