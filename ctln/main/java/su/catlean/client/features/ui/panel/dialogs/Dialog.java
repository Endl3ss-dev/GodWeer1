// deobf by pusia
package su.catlean.client.features.ui.panel.dialogs;

import net.minecraft.client.gui.GuiGraphicsExtractor;

public interface Dialog {
   void render(GuiGraphicsExtractor guiGraphicsExtractor, float value, float value2, float value3, float value4);

   void applyLength(char ch);

   void applyGetWindow(int value);

   boolean testAmount(float value, float value2, int value3);
}