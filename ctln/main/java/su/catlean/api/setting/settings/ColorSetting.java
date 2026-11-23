// deobf by pusia
package su.catlean.api.setting.settings;

import java.awt.Color;
import kotlin.jvm.functions.Function0;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.setting.Setting;
import su.catlean.api.setting.values.GroupValue;

public final class ColorSetting extends Setting {
   public ColorSetting(String id, Color defaultValue, GroupValue groupValue, Function0 visible) {
      super(id, defaultValue, null, groupValue, visible, 4, null);
   }

   @Override
   public void applyDecodeFromString(String setting) {
      this.setObject2(new Color(Integer.parseInt(setting), true));
   }

   @NotNull
   @Override
   public String toString() {
      return String.valueOf(((Color)this.getObject2()).getRGB());
   }

   private static final boolean isFlag() {
      return true;
   }
}