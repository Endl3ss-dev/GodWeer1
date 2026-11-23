// deobf by pusia
package su.catlean.client.features.ui.clickgui.settings;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2f;
import org.lwjgl.glfw.GLFW;
import su.catlean.api.data.Rectangle;
import su.catlean.api.setting.Setting;
import su.catlean.client.features.ui.ClickGuiScreen;
import su.catlean.client.render.SvgTexture;
import su.catlean.client.render.TexturedQuadPass;
import su.catlean.client.render.font.FontRenderer;
import su.catlean.client.render.font.Fonts;
import su.catlean.client.services.NotificationManager;
import su.catlean.client.services.ThemeManager;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.animation.Animation;
import su.catlean.utils.render.Render2D;

public final class BooleanComponent extends SettingComponent {
   @NotNull
   private final Setting setting;
   @NotNull
   private final Animation animation;
   @NotNull
   private final Animation animation2;
   @NotNull
   private final SvgTexture svgTexture;

   public BooleanComponent(Setting setting) {
      super(setting);
      this.setting = setting;
      this.animation = new Animation(NotificationManager.Notification.Type.OUT_QUINT, 400L);
      this.animation2 = new Animation(NotificationManager.Notification.Type.OUT_QUINT, 200L);
      this.svgTexture = new SvgTexture("check", 12, 12);
   }

   @NotNull
   @Override
   public Setting getSetting() {
      return this.setting;
   }

   @Override
   public void render(GuiGraphicsExtractor context, int value, int value2, float amount) {
      Animation animation22;
      float value3;
      label65: {
         this.animation.applyDoubleValue((boolean)this.getSetting().getObject2() ? 1.0F : 0.0F);
         animation22 = this.animation2;
         float amount3 = this.getAmount3() + this.getAmount5() - 18.0F;
         float amount32 = this.getAmount3() + this.getAmount5() - 7.0F;
         float value4 = value;
         if (amount3 <= value4 ? value4 <= amount32 : false) {
            amount3 = this.getAmount4();
            amount32 = this.getAmount4() + 11.0F;
            value4 = value2;
            if (amount3 <= value4 ? value4 <= amount32 : false) {
               value3 = 1.0F;
               break label65;
            }
         }

         value3 = 0.0F;
      }

      animation22.applyDoubleValue(value3);
      if (this.animation.getAmount() != 0.0F) {
         Render2D instance = Render2D.INSTANCE;
         Rectangle rectangle = new Rectangle(
            this.getAmount3() + this.getAmount5() - 18.0F,
            this.getAmount4(),
            this.getAmount3() + this.getAmount5() - 18.0F + this.animation.getAmount() * 11,
            this.getAmount4() + 11.0F
         );
         instance.render(context, rectangle);
         SvgTexture.defaultImpl(
            this.svgTexture,
            (Matrix3x2f)context.pose(),
            this.getAmount3() + this.getAmount5() - 18.5,
            this.getAmount4() - 1.5,
            0.0F,
            0.0F,
            ThemeManager.INSTANCE.getColor2(),
            null,
            0.0F,
            216,
            null
         );
         Render2D.INSTANCE.render(context);
      }

      FontRenderer.defaultImpl(
         Fonts.getFontRenderer3(),
         context.pose(),
         this.getSetting().getText(),
         this.getAmount3(),
         this.getAmount4(),
         ThemeManager.INSTANCE.getColor7(),
         false,
         0,
         96,
         null
      );
      TexturedQuadPass.defaultImpl(
         context.pose(),
         this.getAmount3() + this.getAmount5() - 18.0F,
         this.getAmount4(),
         11.0F,
         11.0F,
         3.0F,
         2.5F + this.animation2.getAmount(),
         0.15F * this.animation.getAmount(),
         1.0F,
         ThemeManager.INSTANCE.getColor11(),
         ThemeManager.INSTANCE.getColor12(),
         0.0F,
         0.0F,
         0.0F,
         14336,
         null
      );
      float amount33 = this.getAmount3() + this.getAmount5() - 18.0F;
      float amount34 = this.getAmount3() + this.getAmount5() - 7.0F;
      float value5 = value;
      if (amount33 <= value5 ? value5 <= amount34 : false) {
         amount33 = this.getAmount4();
         amount34 = this.getAmount4() + 11.0F;
         value5 = value2;
         if ((amount33 <= value5 ? value5 <= amount34 : false) && GLFW.glfwGetPlatform() != 393219) {
            GLFW.glfwSetCursor(MinecraftHolder.getMinecraft().getWindow().handle(), GLFW.glfwCreateStandardCursor(221188));
            ClickGuiScreen.INSTANCE.setFlag(false);
         }
      }
   }

   @Override
   public void updateAmount(double amount, double amount2, int value) {
      float amount3 = this.getAmount3() + this.getAmount5() - 18.0F;
      if (amount <= this.getAmount3() + this.getAmount5() - 7.0F ? amount3 <= amount : false) {
         amount3 = this.getAmount4();
         if (amount2 <= this.getAmount4() + 11.0F ? amount3 <= amount2 : false) {
            this.getSetting().setObject2(!(Boolean)this.getSetting().getObject2());
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