// deobf by pusia
package su.catlean.client.features.ui.clickgui.settings;

import kotlin.text.StringsKt;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import su.catlean.api.setting.Setting;
import su.catlean.api.setting.values.Bind;
import su.catlean.client.features.ui.ClickGuiScreen;
import su.catlean.client.render.TexturedQuadPass;
import su.catlean.client.render.font.FontRenderer;
import su.catlean.client.render.font.Fonts;
import su.catlean.client.services.NotificationManager;
import su.catlean.client.services.ThemeManager;
import su.catlean.utils.KeyNames;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.animation.Animation;

public final class BindComponent extends SettingComponent {
   @NotNull
   private final Setting setting;
   @NotNull
   private final Animation animation;
   private boolean flag;

   public BindComponent(Setting setting) {
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
      String dropLast;
      float amount2;
      Animation animation2;
      float value3;
      label56: {
         dropLast = this.flag
            ? StringsKt.dropLast("...", 3 - (int)(4 * (System.currentTimeMillis() % 700 / 700.0)))
            : KeyNames.INSTANCE.computeText(((Bind)this.getSetting().getObject2()).getValue());
         amount2 = Fonts.getFontRenderer4().computeAmount(KeyNames.INSTANCE.computeText(((Bind)this.getSetting().getObject2()).getValue()));
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
         animation2 = this.animation;
         float amount3 = this.getAmount3() + this.getAmount5() - amount2 - 15.0F;
         float amount32 = this.getAmount3() + this.getAmount5() - 7.0F;
         float value4 = value;
         if (amount3 <= value4 ? value4 <= amount32 : false) {
            amount3 = this.getAmount4();
            amount32 = this.getAmount4() + 12.0F;
            value4 = value2;
            if (amount3 <= value4 ? value4 <= amount32 : false) {
               value3 = 1.0F;
               break label56;
            }
         }

         value3 = 0.0F;
      }

      animation2.applyDoubleValue(value3);
      TexturedQuadPass.defaultImpl(
         context.pose(),
         this.getAmount3() + this.getAmount5() - amount2 - 15.0F,
         this.getAmount4(),
         amount2 + 8.0F,
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
      Fonts.getFontRenderer4()
         .render2(context, dropLast, this.getAmount3() + this.getAmount5() - amount2 / 2.0F - 11.0F, this.getAmount4() + 5.0F, ThemeManager.INSTANCE.getColor7());
      float amount33 = this.getAmount3() + this.getAmount5() - amount2 - 15.0F;
      float amount34 = this.getAmount3() + this.getAmount5() - 7.0F;
      float value5 = value;
      if (amount33 <= value5 ? value5 <= amount34 : false) {
         amount33 = this.getAmount4();
         amount34 = this.getAmount4() + 12.0F;
         value5 = value2;
         if ((amount33 <= value5 ? value5 <= amount34 : false) && GLFW.glfwGetPlatform() != 393219) {
            GLFW.glfwSetCursor(MinecraftHolder.getMinecraft().getWindow().handle(), GLFW.glfwCreateStandardCursor(221188));
            ClickGuiScreen.INSTANCE.setFlag(false);
         }
      }
   }

   @Override
   public void updateAmount(double amount, double amount2, int value) {
      float amount3 = Fonts.getFontRenderer3().computeAmount(this.getSetting().getId()) + 6.0F;
      if (this.flag) {
         this.getSetting().setObject2(new Bind(value, false, true));
         this.flag = false;
      } else {
         float amount32 = this.getAmount3() + this.getAmount5() - amount3 - 15.0F;
         if (amount <= this.getAmount3() + this.getAmount5() - 7.0F ? amount32 <= amount : false) {
            amount32 = this.getAmount4();
            if (amount2 <= this.getAmount4() + 12.0F ? amount32 <= amount2 : false) {
               this.flag = !this.flag;
            }
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
            case 261:
               this.getSetting().setObject2(new Bind(0, false, false, 7, null));
               break;
            default:
               this.getSetting().setObject2(new Bind(value, false, false));
         }

         this.flag = false;
      }
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
      this.flag = false;
   }
}