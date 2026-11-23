// deobf by pusia
package su.catlean.api.addon.impl;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.addon.feature.AddonWidget;
import su.catlean.client.features.ui.hud.Widget;
import su.catlean.client.render.ShaderProgramWrapper;

public final class AddonWidgetAdapter extends Widget {
   @NotNull
   private final AddonWidget addon;
   @NotNull
   private final ShaderProgramWrapper shaderProgramWrapper;

   public AddonWidgetAdapter(AddonWidget addon) {
      super(addon.getId());
      this.addon = addon;
      this.shaderProgramWrapper = new ShaderProgramWrapper(this, this.addon.getSettings());
   }

   @Override
   public void render(GuiGraphicsExtractor context) {
      this.shaderProgramWrapper.update();
      this.addon.render(new AddonWidgetContextImpl(this, context));
      this.shaderProgramWrapper.update2();
   }

   @Override
   public void update() {
      this.shaderProgramWrapper.update();
      this.addon.update();
      this.shaderProgramWrapper.update2();
   }

   public static final void updateAddonWidgetAdapter(AddonWidgetAdapter addonWidgetAdapter, float amount, float amount2) {
      addonWidgetAdapter.updateAmount(amount, amount2);
   }
}