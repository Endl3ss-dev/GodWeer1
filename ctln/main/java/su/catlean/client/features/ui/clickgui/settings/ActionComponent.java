// deobf by pusia
package su.catlean.client.features.ui.clickgui.settings;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import su.catlean.api.setting.Setting;
import su.catlean.api.setting.values.ActionValue;
import su.catlean.client.features.ui.ClickGuiScreen;
import su.catlean.client.render.TexturedQuadPass;
import su.catlean.client.render.font.Fonts;
import su.catlean.client.services.NotificationManager;
import su.catlean.client.services.ThemeManager;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.animation.Animation;
import su.catlean.utils.render.Render2D;

public final class ActionComponent extends SettingComponent {
   @NotNull
   private final Setting setting;
   @NotNull
   private final Animation animation;
   @NotNull
   private final Animation animation2;

   public ActionComponent(Setting setting) {
      super(setting);
      this.setting = setting;
      this.animation = new Animation(NotificationManager.Notification.Type.OUT_QUINT, 200L);
      this.animation2 = new Animation(NotificationManager.Notification.Type.OUT_QUINT, 100L);
   }

   @NotNull
   @Override
   public Setting getSetting() {
      return this.setting;
   }

   @Override
   public void render(GuiGraphicsExtractor context, int value, int value2, float amount) {
      Animation animation3;
      float value3;
      label51: {
         this.setAmount5(14.0F);
         animation3 = this.animation;
         float amount3 = this.getAmount3();
         float amount32 = this.getAmount3() + this.getAmount5() - 7.0F;
         float value4 = value;
         if (amount3 <= value4 ? value4 <= amount32 : false) {
            amount3 = this.getAmount4();
            amount32 = this.getAmount4() + 12;
            value4 = value2;
            if (amount3 <= value4 ? value4 <= amount32 : false) {
               value3 = 1.0F;
               break label51;
            }
         }

         value3 = 0.0F;
      }

      animation3.applyDoubleValue(value3);
      float amount33 = this.getAmount3();
      float amount34 = this.getAmount3() + this.getAmount5() - 7.0F;
      float value5 = value;
      if (amount33 <= value5 ? value5 <= amount34 : false) {
         amount33 = this.getAmount4();
         amount34 = this.getAmount4() + 12;
         value5 = value2;
         if ((amount33 <= value5 ? value5 <= amount34 : false) && GLFW.glfwGetPlatform() != 393219) {
            GLFW.glfwSetCursor(MinecraftHolder.getMinecraft().getWindow().handle(), GLFW.glfwCreateStandardCursor(221188));
            ClickGuiScreen.INSTANCE.setFlag(false);
         }
      }

      this.animation2.applyDoubleValue(0.0F);
      TexturedQuadPass.defaultImpl(
         context.pose(),
         this.getAmount3(),
         this.getAmount4() - 1.0F,
         this.getAmount5() - 7.0F,
         13.0F,
         3.0F,
         2.5F,
         this.animation.getAmount() / 2.0F + this.animation2.getAmount() / 2.0F,
         1.0F,
         ThemeManager.INSTANCE.getColor11(),
         ThemeManager.INSTANCE.getColor12(),
         0.0F,
         0.0F,
         0.0F,
         14336,
         null
      );
      Fonts.getFontRenderer3()
         .render2(
            context,
            this.getSetting().getText(),
            this.getAmount3() + (this.getAmount5() - 7.0F) / 2.0F,
            this.getAmount4() - 2.0F + this.getAmount6() / 2.0F,
            Render2D.INSTANCE.computeColor(ThemeManager.INSTANCE.getColor2(), 1.0F)
         );
   }

   @Override
   public void updateAmount(double amount, double amount2, int value) {
      float amount3 = this.getAmount3();
      if (amount <= this.getAmount3() + this.getAmount5() - 7.0F ? amount3 <= amount : false) {
         amount3 = this.getAmount4();
         if (amount2 <= this.getAmount4() + 12 ? amount3 <= amount2 : false) {
            ((ActionValue)this.getSetting().getObject2()).getAction().invoke();
            this.animation2.updateAmount(1.0F);
         }
      }
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