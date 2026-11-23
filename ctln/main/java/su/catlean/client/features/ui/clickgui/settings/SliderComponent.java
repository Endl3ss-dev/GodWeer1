// deobf by pusia
package su.catlean.client.features.ui.clickgui.settings;

import java.awt.Color;
import kotlin.ranges.ClosedFloatingPointRange;
import kotlin.ranges.IntRange;
import kotlin.text.StringsKt;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import su.catlean.api.setting.Setting;
import su.catlean.api.setting.settings.FloatSetting;
import su.catlean.api.setting.settings.IntSetting;
import su.catlean.api.setting.values.RangeBound;
import su.catlean.client.features.ui.ClickGuiScreen;
import su.catlean.client.render.BoxPass;
import su.catlean.client.render.TexturedQuadPass;
import su.catlean.client.render.font.FontRenderer;
import su.catlean.client.render.font.Fonts;
import su.catlean.client.services.ChestOpenTracker;
import su.catlean.client.services.NotificationManager;
import su.catlean.client.services.ThemeManager;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.animation.Animation;
import su.catlean.utils.render.Render2D;

public abstract class SliderComponent extends SettingComponent {
   @NotNull
   private final Setting setting;
   @NotNull
   private final Animation animation;
   private boolean flag;
   private float amount;
   private boolean flag2;
   @NotNull
   private String text;

   public SliderComponent(Setting setting) {
      super(setting);
      this.setting = setting;
      this.setAmount5(24.0F);
      this.animation = new Animation(NotificationManager.Notification.Type.OUT_QUINT, 200L);
      this.text = "-";
   }

   @NotNull
   @Override
   public Setting getSetting() {
      return this.setting;
   }

   public final boolean isFlag() {
      return this.flag;
   }

   public final void setFlag(boolean flag) {
      this.flag = flag;
   }

   public final float getAmount2() {
      return this.amount;
   }

   public final void setAmount(float amount) {
      this.amount = amount;
   }

   @Override
   public void render(GuiGraphicsExtractor context, int value, int value2, float amount) {
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
      this.animation.applyDoubleValue(!this.flag && !this.testValue(value, value2) ? 0.0F : 1.0F);
      this.amount = this.computeAmount(this.amount, this.getAmount(), 15.0F);
      if (this.testValue(value, value2) && GLFW.glfwGetPlatform() != 393219) {
         GLFW.glfwSetCursor(MinecraftHolder.getMinecraft().getWindow().handle(), GLFW.glfwCreateStandardCursor(221189));
         ClickGuiScreen.INSTANCE.setFlag(false);
      }

      if (this.testValue2(value, value2) && GLFW.glfwGetPlatform() != 393219) {
         GLFW.glfwSetCursor(MinecraftHolder.getMinecraft().getWindow().handle(), GLFW.glfwCreateStandardCursor(221186));
         ClickGuiScreen.INSTANCE.setFlag(false);
      }

      this.render(context);
      this.render2(context);
      this.applyClamp(value);
   }

   private final boolean testValue(int value, int value2) {
      float amount3 = this.getAmount3() + 2;
      float amount32 = this.getAmount3() + this.getAmount5() - 9.0F;
      float value3 = value;
      if (amount3 <= value3 ? value3 <= amount32 : false) {
         amount3 = this.getAmount4() + 9.0F;
         amount32 = this.getAmount4() + 21.0F;
         value3 = value2;
         if (amount3 <= value3 ? value3 <= amount32 : false) {
            return true;
         }
      }

      return false;
   }

   private final boolean testValue2(int value, int value2) {
      float amount2 = Fonts.getFontRenderer().computeAmount(this.flag2 ? this.text : this.getSetting().getObject2().toString());
      float amount3 = this.getAmount3() + this.getAmount5() - amount2 - 13;
      float amount32 = this.getAmount3() + this.getAmount5() - amount2 - 13 + amount2 + 6.0F;
      float value3 = value;
      if (amount3 <= value3 ? value3 <= amount32 : false) {
         amount3 = this.getAmount4() + 1.0F;
         amount32 = this.getAmount4() + 11.0F;
         value3 = value2;
         if (amount3 <= value3 ? value3 <= amount32 : false) {
            return true;
         }
      }

      return false;
   }

   private final void render(GuiGraphicsExtractor guiGraphicsExtractor) {
      TexturedQuadPass.defaultImpl(
         guiGraphicsExtractor.pose(),
         this.getAmount3(),
         this.getAmount4() + 15.0F,
         this.getAmount5() - 7.0F,
         4.0F,
         2.0F,
         2.0F,
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
      BoxPass.render(
         this.getAmount3(),
         this.getAmount4() + 15.0F,
         (this.getAmount5() - 7.0F) * this.amount,
         4.0F,
         2.0F,
         ThemeManager.INSTANCE.getColor9(),
         ThemeManager.INSTANCE.getColor9(),
         ThemeManager.INSTANCE.getColor9(),
         ThemeManager.INSTANCE.getColor9(),
         guiGraphicsExtractor.pose(),
         1.0F,
         ThemeManager.INSTANCE.getColor12()
      );
      BoxPass.render(
         this.getAmount3() - 2 + (this.getAmount5() - 11.0F) * this.amount,
         this.getAmount4() + 15.0F - 1.5F,
         7.0F,
         7.0F,
         2.5F,
         ThemeManager.INSTANCE.getColor9(),
         ThemeManager.INSTANCE.getColor9(),
         ThemeManager.INSTANCE.getColor9(),
         ThemeManager.INSTANCE.getColor9(),
         guiGraphicsExtractor.pose(),
         1.0F + this.animation.getAmount(),
         ThemeManager.INSTANCE.getColor12()
      );
   }

   private final void render2(GuiGraphicsExtractor guiGraphicsExtractor) {
      RangeBound instance = RangeBound.INSTANCE;
      String text2 = this.getSetting().getId();
      Number object2 = (Number)this.getSetting().getObject2();
      String text3 = this.flag2 ? this.text : this.getSetting().getObject2().toString();
      Number number = object2;
      String text4 = instance.computeText(text2, number, text3);
      float amount2 = Fonts.getFontRenderer().computeAmount(text4);
      TexturedQuadPass.defaultImpl(
         guiGraphicsExtractor.pose(),
         this.getAmount3() + this.getAmount5() - amount2 - 13,
         this.getAmount4() + 1.0F,
         amount2 + 6.0F,
         10.0F,
         3.0F,
         2.0F,
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
      FontRenderer fontRenderer = Fonts.getFontRenderer();
      float amount3 = this.getAmount3() + this.getAmount5() - 10 - amount2;
      float amount4 = this.getAmount4() + 0.5F;
      Color color = Render2D.INSTANCE
         .computeColor(ThemeManager.INSTANCE.getColor7(), this.flag2 ? (float)Math.sin(System.currentTimeMillis() / 50.0) + 0.5F : 1.0F);
      float value = amount4;
      float value2 = amount3;
      fontRenderer.render(guiGraphicsExtractor, text4, value2, value, color);
   }

   public abstract void applyClamp(int value);

   public abstract float getAmount();

   @Override
   public void updateAmount(double amount, double amount2, int value) {
      float amount3 = Fonts.getFontRenderer().computeAmount(this.getSetting().getObject2().toString());
      if (value == 0 && this.testValue((int)amount, (int)amount2)) {
         this.flag = true;
      }

      if (value == 0) {
         float amount32 = this.getAmount3() + this.getAmount5() - amount3 - 13;
         if (amount <= this.getAmount3() + this.getAmount5() - 7.0F ? amount32 <= amount : false) {
            amount32 = this.getAmount4() + 1.0F;
            if (amount2 <= this.getAmount4() + 11.0F ? amount32 <= amount2 : false) {
               this.flag2 = !this.flag2;
               this.text = this.getSetting().getObject2().toString();
            }
         }
      }
   }

   @Override
   public void updateAmount2(double amount, double amount2, int value) {
      this.flag = false;
   }

   @Override
   public void updateValue(int value) {
      if (this.flag2) {
         switch (value) {
            case 257:
               this.flag2 = false;

               try {
                  this.getSetting().setObject2(this.computeNumber(this.text));
               } catch (Exception exception) {
               }
            case 258:
            default:
               break;
            case 259:
               if (this.text.length() > 0) {
                  this.text = StringsKt.dropLast(this.text, 1);
               }
         }
      }
   }

   @Override
   public void updateSymbol(char symbol) {
      if (this.flag2) {
         this.text = this.text + symbol;
      }
   }

   @Override
   public boolean testAmount(double amount, double amount2, double amount3) {
      if (this.testValue((int)amount, (int)amount2)) {
         this.getSetting().setObject2(this.computeNumber((Number)this.getSetting().getObject2(), (int)amount3));
         return true;
      } else {
         return false;
      }
   }

   @NotNull
   public final IntRange getIntRange() {
      return ((IntSetting)this.getSetting()).getRange();
   }

   @NotNull
   public final ClosedFloatingPointRange getClosedFloatingPointRange() {
      return ((FloatSetting)this.getSetting()).getRange();
   }

   @NotNull
   public abstract Number computeNumber(String text2);

   @NotNull
   public abstract Number computeNumber(Number number, int value);

   private final float computeAmount(float amount, float amount2, float amount3) {
      float value = Math.clamp((ChestOpenTracker.INSTANCE.getValue() > 5 ? 1.0F / ChestOpenTracker.INSTANCE.getValue() : 0.016F) * amount3, 0.0F, 1.0F);
      return (1.0F - value) * amount + value * amount2;
   }

   @Override
   public void update() {
      this.flag = false;
      this.flag2 = false;
   }
}