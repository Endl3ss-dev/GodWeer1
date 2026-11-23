// deobf by pusia
package su.catlean.client.features.ui.clickgui.settings;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import su.catlean.api.setting.Setting;
import su.catlean.api.setting.values.NotationValue;
import su.catlean.client.features.ui.ClickGuiScreen;
import su.catlean.client.render.TexturedQuadPass;
import su.catlean.client.render.font.Fonts;
import su.catlean.client.services.NotificationManager;
import su.catlean.client.services.ThemeManager;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.animation.Animation;

public final class NotationComponent extends SettingComponent {
   @NotNull
   private final Setting setting;
   @NotNull
   private final Animation animation;

   public NotationComponent(Setting setting) {
      super(setting);
      this.setting = setting;
      this.animation = new Animation(NotificationManager.Notification.Type.OUT_QUINT, 200L);
   }

   @NotNull
   @Override
   public Setting getSetting() {
      return this.setting;
   }

   @Override
   public void render(GuiGraphicsExtractor context, int value, int value2, float amount) {
      this.setAmount5(14.0F);
      String text = "";
      float value3 = 0.0F;
      String text2 = ((NotationValue)this.getSetting().getObject2()).getText();
      int value4 = 0;

      for (int i = text2.length(); value4 < i; value4++) {
         char charAt = text2.charAt(value4);
         if (value3 != 0.0F || charAt != ' ') {
            text = text + charAt;
            value3 += Fonts.getFontRenderer3().computeAmount(String.valueOf(charAt));
            if (value3 >= this.getAmount5() - 16.0F) {
               text = text + "\n";
               value3 = 0.0F;
               this.setAmount5(this.getAmount6() + 12);
            }
         }
      }

      Animation animation2;
      float value5;
      label64: {
         animation2 = this.animation;
         float amount3 = this.getAmount3();
         float amount32 = this.getAmount3() + this.getAmount5() - 7.0F;
         float value6 = value;
         if (amount3 <= value6 ? value6 <= amount32 : false) {
            float amount4 = this.getAmount4();
            float amount42 = this.getAmount4() + this.getAmount6() - 2.0F;
            value6 = value2;
            if (amount4 <= value6 ? value6 <= amount42 : false) {
               value5 = 1.0F;
               break label64;
            }
         }

         value5 = 0.0F;
      }

      animation2.applyDoubleValue(value5);
      float amount33 = this.getAmount3();
      float amount34 = this.getAmount3() + this.getAmount5() - 7.0F;
      float value7 = value;
      if (amount33 <= value7 ? value7 <= amount34 : false) {
         float amount43 = this.getAmount4();
         float amount44 = this.getAmount4() + this.getAmount6() - 2.0F;
         value7 = value2;
         if ((amount43 <= value7 ? value7 <= amount44 : false) && GLFW.glfwGetPlatform() != 393219) {
            GLFW.glfwSetCursor(MinecraftHolder.getMinecraft().getWindow().handle(), GLFW.glfwCreateStandardCursor(221194));
            ClickGuiScreen.INSTANCE.setFlag(false);
         }
      }

      TexturedQuadPass.defaultImpl(
         context.pose(),
         this.getAmount3(),
         this.getAmount4() - 1.0F,
         this.getAmount5() - 7.0F,
         this.getAmount6() - 1.0F,
         3.0F,
         2.5F,
         this.animation.getAmount() / 2.0F,
         1.0F,
         ThemeManager.INSTANCE.getColor11(),
         ThemeManager.INSTANCE.getColor12(),
         0.0F,
         0.0F,
         0.0F,
         14336,
         null
      );
      Fonts.getFontRenderer3().render(context, text, this.getAmount3() + 3.0F, this.getAmount4(), ThemeManager.INSTANCE.getColor8());
   }

   @Override
   public void updateAmount(double amount, double amount2, int value) {
   }

   @Override
   public void updateAmount2(double amount, double amount2, int value) {
   }

   @Override
   public void updateValue(int value) {
   }

   @Override
   public void updateSymbol(char symbol) {
   }

   @Override
   public boolean testAmount(double amount, double amount2, double amount3) {
      return false;
   }

   @Override
   public void update() {
   }
}