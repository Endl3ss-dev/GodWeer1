// deobf by pusia
package su.catlean.client.features.ui.clickgui.settings;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.setting.Setting;

public abstract class SettingComponent {
   @NotNull
   private final Setting setting;
   private float amount;
   private float amount2;
   private float amount3;
   private float amount4;

   public SettingComponent(Setting setting) {
      this.setting = setting;
      this.amount3 = 116.0F;
      this.amount4 = 13.0F;
   }

   @NotNull
   public Setting getSetting() {
      return this.setting;
   }

   public final float getAmount3() {
      return this.amount;
   }

   public final void setAmount2(float amount) {
      this.amount = amount;
   }

   public final float getAmount4() {
      return this.amount2;
   }

   public final void setAmount3(float amount) {
      this.amount2 = amount;
   }

   public final float getAmount5() {
      return this.amount3;
   }

   public final void setAmount4(float amount) {
      this.amount3 = amount;
   }

   public final float getAmount6() {
      return this.amount4;
   }

   public final void setAmount5(float amount) {
      this.amount4 = amount;
   }

   public abstract void render(GuiGraphicsExtractor guiGraphicsExtractor, int value, int value2, float value3);

   public abstract void updateAmount(double value, double value2, int value3);

   public abstract void updateAmount2(double value, double value2, int value3);

   public abstract void updateValue(int value);

   public abstract void updateSymbol(char ch);

   public abstract boolean testAmount(double value, double value2, double value3);

   public abstract void update();
}