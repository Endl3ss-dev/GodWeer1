// deobf by pusia
package su.catlean.api.addon.impl;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import su.catlean.api.addon.feature.AddonWidgetContext;

public final class AddonWidgetContextImpl implements AddonWidgetContext {
   public final AddonWidgetAdapter addonWidgetAdapter;
   public final GuiGraphicsExtractor guiGraphicsExtractor;

   public AddonWidgetContextImpl(AddonWidgetAdapter addonWidgetAdapter, GuiGraphicsExtractor guiGraphicsExtractor) {
      this.addonWidgetAdapter = addonWidgetAdapter;
      this.guiGraphicsExtractor = guiGraphicsExtractor;
   }

   @Override
   public void drawSingleLine(String text) {
      this.addonWidgetAdapter.render(this.guiGraphicsExtractor, text);
   }

   @Override
   public void setBounds(float amount, float amount2) {
      AddonWidgetAdapter.updateAddonWidgetAdapter(this.addonWidgetAdapter, amount, amount2);
   }
}