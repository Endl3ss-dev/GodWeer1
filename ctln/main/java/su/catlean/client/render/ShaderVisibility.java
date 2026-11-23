// deobf by pusia
package su.catlean.client.render;

import org.jetbrains.annotations.NotNull;
import su.catlean.api.addon.feature.AddonSetting;
import su.catlean.api.setting.Setting;

public final class ShaderVisibility {
   @NotNull
   private final AddonSetting addonSetting;
   @NotNull
   private final Setting internal;

   public ShaderVisibility(AddonSetting addonSetting, Setting internal) {
      this.addonSetting = addonSetting;
      this.internal = internal;
   }

   public final void update() {
      this.applySet(this.addonSetting, this.internal.getObject2());
   }

   public final void applyGetValue() {
      this.updateSetting(this.internal, this.addonSetting.getValue());
   }

   private final void applySet(AddonSetting addonSetting, Object object) {
      addonSetting.set(object);
   }

   private final void updateSetting(Setting setting, Object object) {
      setting.setObject2(object);
   }
}