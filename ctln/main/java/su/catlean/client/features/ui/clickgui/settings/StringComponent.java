// deobf by pusia
package su.catlean.client.features.ui.clickgui.settings;

import kotlin.text.StringsKt;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import su.catlean.api.data.Rectangle;
import su.catlean.api.setting.Setting;
import su.catlean.client.features.ui.ClickGuiScreen;
import su.catlean.client.render.TexturedQuadPass;
import su.catlean.client.render.font.FontRenderer;
import su.catlean.client.render.font.Fonts;
import su.catlean.client.services.NotificationManager;
import su.catlean.client.services.ThemeManager;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.animation.Animation;
import su.catlean.utils.render.Render2D;

public final class StringComponent extends SettingComponent {
   @NotNull
   private final Setting setting;
   @NotNull
   private final Animation animation;
   private boolean flag;

   public StringComponent(Setting setting) {
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
      String text;
      float amount2;
      Animation animation2;
      float value3;
      label60: {
         text = this.flag && System.currentTimeMillis() % 400 > 200L ? "|" : "";
         amount2 = Fonts.getFontRenderer3().computeAmount(this.getSetting().getText()) + 6.0F;
         FontRenderer.defaultImpl(
            Fonts.getFontRenderer3(),
            context.pose(),
            this.getSetting().getText(),
            this.getAmount3() - 1.0F,
            this.getAmount4() - 1.0F,
            ThemeManager.INSTANCE.getColor7(),
            false,
            0,
            96,
            null
         );
         animation2 = this.animation;
         float amount3 = this.getAmount3() + amount2;
         float amount32 = this.getAmount3() + amount2 + this.getAmount5() - 7.0F - amount2;
         float value4 = value;
         if (amount3 <= value4 ? value4 <= amount32 : false) {
            amount3 = this.getAmount4();
            amount32 = this.getAmount4() + 12.0F;
            value4 = value2;
            if (amount3 <= value4 ? value4 <= amount32 : false) {
               value3 = 1.0F;
               break label60;
            }
         }

         value3 = 0.0F;
      }

      animation2.applyDoubleValue(value3);
      TexturedQuadPass.defaultImpl(
         context.pose(),
         this.getAmount3() + amount2,
         this.getAmount4(),
         this.getAmount5() - 7.0F - amount2,
         12.0F,
         3.0F,
         2.5F + this.animation.getAmount(),
         0.0F,
         1.0F,
         ThemeManager.INSTANCE.getColor11(),
         ThemeManager.INSTANCE.getColor12(),
         0.0F,
         0.0F,
         0.0F,
         14336,
         null
      );
      Render2D instance = Render2D.INSTANCE;
      Rectangle rectangle = new Rectangle(
         this.getAmount3() + amount2 + 1.0F, this.getAmount4(), this.getAmount3() + this.getAmount5() - 9.0F, this.getAmount4() + 12.0F
      );
      instance.render(context, rectangle);
      float amount4 = Fonts.getFontRenderer4().computeAmount((String)this.getSetting().getObject2()) + 2.0F;
      if (amount4 < this.getAmount5() - amount4) {
         Fonts.getFontRenderer4()
            .render(
               context, this.getSetting().getObject2() + text, this.getAmount3() + amount2 + 3.0F, this.getAmount4() + 1.0F, ThemeManager.INSTANCE.getColor7()
            );
      } else {
         Fonts.getFontRenderer4()
            .render(
               context,
               this.getSetting().getObject2() + text,
               this.getAmount3() + this.getAmount5() - 10.0F - amount4,
               this.getAmount4() + 1.0F,
               ThemeManager.INSTANCE.getColor7()
            );
      }

      float amount33 = this.getAmount3() + amount2;
      float amount34 = this.getAmount3() + amount2 + this.getAmount5() - 7.0F - amount2;
      float value5 = value;
      if (amount33 <= value5 ? value5 <= amount34 : false) {
         amount33 = this.getAmount4();
         amount34 = this.getAmount4() + 12.0F;
         value5 = value2;
         if ((amount33 <= value5 ? value5 <= amount34 : false) && GLFW.glfwGetPlatform() != 393219) {
            GLFW.glfwSetCursor(MinecraftHolder.getMinecraft().getWindow().handle(), GLFW.glfwCreateStandardCursor(221186));
            ClickGuiScreen.INSTANCE.setFlag(false);
         }
      }

      Render2D.INSTANCE.render(context);
   }

   @Override
   public void updateAmount(double amount, double amount2, int value) {
      float amount3 = Fonts.getFontRenderer3().computeAmount(this.getSetting().getId()) + 6.0F;
      float amount32 = this.getAmount3() + amount3;
      if (amount <= this.getAmount3() + amount3 + this.getAmount5() - 7.0F - amount3 ? amount32 <= amount : false) {
         amount32 = this.getAmount4();
         if (amount2 <= this.getAmount4() + 12.0F ? amount32 <= amount2 : false) {
            this.flag = !this.flag;
         }
      }
   }

   @Override
   public void updateAmount2(double amount, double amount2, int value) {
   }

   @Override
   public void updateValue(int value) {
      if (this.flag) {
         switch (value) {
            case 256:
               this.flag = false;
               break;
            case 257:
               this.flag = false;
            case 258:
            default:
               break;
            case 259:
               this.getSetting().setObject2(StringsKt.dropLast((String)this.getSetting().getObject2(), 1));
         }
      }
   }

   @Override
   public void updateSymbol(char symbol) {
      if (this.flag) {
         this.getSetting().setObject2("" + this.getSetting().getObject2() + symbol);
      }
   }

   @Override
   public boolean testAmount(double amount, double amount2, double amount3) {
      return false;
   }

   @Override
   public void update() {
      this.flag = false;
   }
}