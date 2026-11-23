// deobf by pusia
package su.catlean.api.addon.impl;

import org.jetbrains.annotations.NotNull;
import su.catlean.api.addon.feature.AddonModule;
import su.catlean.api.category.SubCategory;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.client.features.modules.Module;
import su.catlean.client.render.ShaderProgramWrapper;
import su.catlean.gofra.Flow;
import su.catlean.gofra.Gofra;

public final class AddonModuleAdapter extends Module {
   @NotNull
   private final AddonModule addon;
   @NotNull
   private final ShaderProgramWrapper shaderProgramWrapper;

   public AddonModuleAdapter(AddonModule addon, SubCategory category) {
      super(addon.getId(), category, addon.getAliases());
      this.addon = addon;
      this.shaderProgramWrapper = new ShaderProgramWrapper(this, this.addon.getSettings());
   }

   @Override
   public void update2() {
      this.shaderProgramWrapper.update();
      Gofra.INSTANCE.unplug(this.addon);
      Gofra.INSTANCE.plug(this.addon);
      this.addon.onEnable();
      this.shaderProgramWrapper.update2();
   }

   @Override
   public void update4() {
      Gofra.INSTANCE.unplug(this.addon);
      this.shaderProgramWrapper.update();
      this.addon.onDisable();
      this.shaderProgramWrapper.update2();
   }

   @Flow
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      this.shaderProgramWrapper.update();
      this.addon.onPlayerUpdate();
      this.shaderProgramWrapper.update2();
   }
}