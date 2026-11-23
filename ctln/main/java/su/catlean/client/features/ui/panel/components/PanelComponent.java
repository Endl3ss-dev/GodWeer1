// deobf by pusia
package su.catlean.client.features.ui.panel.components;

import net.minecraft.client.gui.GuiGraphicsExtractor;

public abstract class PanelComponent {
   public PanelComponent() {
   }

   public abstract void render(GuiGraphicsExtractor guiGraphicsExtractor, float value, float value2, float value3, float value4);

   public abstract boolean checkIntValue(float value, float value2, int value3);
}