// deobf by pusia
package su.catlean.client.render;

import java.util.ArrayList;
import java.util.List;
import kotlin.NoWhenBranchMatchedException;
import kotlin.ranges.ClosedFloatingPointRange;
import kotlin.ranges.IntRange;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.addon.feature.AddonSetting;
import su.catlean.api.addon.feature.AddonSettings;
import su.catlean.api.setting.Setting;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.SettingHolder;

public final class ShaderProgramWrapper {
   @NotNull
   private final List items;

   public ShaderProgramWrapper(SettingHolder wrapper, AddonSettings settings) {
      Iterable<AddonSetting<?>> entriessu_catlean = settings.getEntries$su_catlean();
      var arrayList = new ArrayList();

      for (Object object : entriessu_catlean) {
         ShaderVisibility shaderVisibility;
         label33: {
            AddonSetting addonSetting = (AddonSetting)object;
            Setting defaultImpl;
            switch (ShaderProgramWrapper.WhenMappings.values[addonSetting.getKind$su_catlean().ordinal()]) {
               case 1:
                  defaultImpl = SettingFactory.defaultImpl(wrapper, addonSetting.getId(), (Boolean)addonSetting.getDefaultValue(), null, null, 12, null);
                  break;
               case 2:
                  ClosedFloatingPointRange rangesu_catlean = addonSetting.getRange$su_catlean();
                  if (rangesu_catlean == null) {
                     shaderVisibility = null;
                     break label33;
                  }

                  ClosedFloatingPointRange closedFloatingPointRange = rangesu_catlean;
                  defaultImpl = SettingFactory.defaultImpl(
                     wrapper,
                     addonSetting.getId(),
                     (Integer)addonSetting.getDefaultValue(),
                     new IntRange((int)((Number)closedFloatingPointRange.getStart()).floatValue(), (int)((Number)closedFloatingPointRange.getEndInclusive()).floatValue()),
                     null,
                     null,
                     24,
                     null
                  );
                  break;
               case 3:
                  ClosedFloatingPointRange rangesu_catlean2 = addonSetting.getRange$su_catlean();
                  if (rangesu_catlean2 == null) {
                     shaderVisibility = null;
                     break label33;
                  }

                  ClosedFloatingPointRange closedFloatingPointRange2 = rangesu_catlean2;
                  defaultImpl = SettingFactory.defaultImpl(wrapper, addonSetting.getId(), (Float)addonSetting.getDefaultValue(), closedFloatingPointRange2, null, null, 24, null);
                  break;
               case 4:
                  defaultImpl = SettingFactory.defaultImpl(wrapper, addonSetting.getId(), (String)addonSetting.getDefaultValue(), null, null, 12, null);
                  break;
               case 5:
                  defaultImpl = SettingFactory.defaultImpl(wrapper, addonSetting.getId(), (Enum)addonSetting.getDefaultValue(), null, null, 12, null);
                  break;
               default:
                  throw new NoWhenBranchMatchedException();
            }

            Setting setting = defaultImpl;
            shaderVisibility = new ShaderVisibility(addonSetting, setting);
         }

         if (shaderVisibility != null) {
            ShaderVisibility shaderVisibility2 = shaderVisibility;
            arrayList.add(shaderVisibility2);
         }
      }

      this.items = arrayList;
   }

   public final void update() {
      for (Object item : this.items) {
         ShaderVisibility shaderVisibility = (ShaderVisibility)item;
         shaderVisibility.update();
      }
   }

   public final void update2() {
      for (Object item : this.items) {
         ShaderVisibility shaderVisibility = (ShaderVisibility)item;
         shaderVisibility.applyGetValue();
      }
   }

   public static final class WhenMappings {
      public static final int[] values;

      static {
         int[] values2 = new int[AddonSetting.Kind.values().length];

         try {
            values2[AddonSetting.Kind.BOOLEAN.ordinal()] = 1;
         } catch (NoSuchFieldError exception) {
         }

         try {
            values2[AddonSetting.Kind.INT.ordinal()] = 2;
         } catch (NoSuchFieldError exception2) {
         }

         try {
            values2[AddonSetting.Kind.FLOAT.ordinal()] = 3;
         } catch (NoSuchFieldError exception3) {
         }

         try {
            values2[AddonSetting.Kind.STRING.ordinal()] = 4;
         } catch (NoSuchFieldError exception4) {
         }

         try {
            values2[AddonSetting.Kind.ENUM.ordinal()] = 5;
         } catch (NoSuchFieldError exception5) {
         }

         values = values2;
      }
   }
}