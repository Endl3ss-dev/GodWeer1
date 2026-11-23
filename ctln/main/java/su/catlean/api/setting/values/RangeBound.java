// deobf by pusia
package su.catlean.api.setting.values;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.SettingNameOverride;

public final class RangeBound {
   @NotNull
   public static final RangeBound INSTANCE = new RangeBound();
   @NotNull
   private static final List items = new ArrayList();

   private RangeBound() {
   }

   @NotNull
   public final List getItems() {
      return items;
   }

   @NotNull
   public final String computeText(String id, Number minValue, String orElse) {
      Iterable items2 = items;
      Iterator iterator = items2.iterator();

      Object object;
      while (true) {
         if (!iterator.hasNext()) {
            object = null;
            break;
         }

         Object object2 = iterator.next();
         SettingNameOverride settingNameOverride = (SettingNameOverride)object2;
         if (Intrinsics.areEqual(settingNameOverride.getName(), id) && Intrinsics.areEqual(minValue, settingNameOverride.getValue())) {
            object = (SettingNameOverride)object2;
            break;
         }
      }

      object = object;
      if (object != null) {
         String overrideName = ((SettingNameOverride)object).getOverrideName();
         if (overrideName != null) {
            return overrideName;
         }
      }

      return orElse;
   }
}