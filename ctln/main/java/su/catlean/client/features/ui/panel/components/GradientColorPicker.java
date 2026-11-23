// deobf by pusia
package su.catlean.client.features.ui.panel.components;

import java.awt.Color;
import kotlin.collections.CollectionsKt;
import kotlin.reflect.KMutableProperty0;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2fStack;
import su.catlean.client.render.TexturedQuadPass;
import su.catlean.client.render.font.FontRenderer;
import su.catlean.client.render.font.Fonts;
import su.catlean.client.services.ThemeManager;
import su.catlean.utils.render.Render2D;

public final class GradientColorPicker {
   @NotNull
   private KMutableProperty0 setting;
   private float amount;
   private float amount2;
   private final int value;
   private boolean flag;
   @NotNull
   private String text;

   public GradientColorPicker(KMutableProperty0 setting, float amount, float amount2, int value) {
      this.setting = setting;
      this.amount = amount;
      this.amount2 = amount2;
      this.value = value;
      this.text = "-";
   }

   @NotNull
   public final KMutableProperty0 getSetting() {
      return this.setting;
   }

   public final float getAmount() {
      return this.amount;
   }

   public final void setAmount(float amount) {
      this.amount = amount;
   }

   public final float getAmount2() {
      return this.amount2;
   }

   public final void setAmount2(float amount) {
      this.amount2 = amount;
   }

   public final int getValue() {
      return this.value;
   }

   public final void render(Matrix3x2fStack matrix3x2fStack, GuiGraphicsExtractor context) {
      TexturedQuadPass.defaultImpl(
         context.pose(),
         this.amount + 40.0F,
         this.amount2 + 10.0F + this.value * 10.0F,
         15.0F,
         8.0F,
         2.0F,
         2.0F,
         0.0F,
         1.0F,
         ThemeManager.INSTANCE.getColor11(),
         ThemeManager.INSTANCE.getColor9(),
         0.0F,
         0.0F,
         0.0F,
         14336,
         null
      );
      Fonts.getFontRenderer4()
         .render2(
            context,
            String.valueOf("RGBA".charAt(this.value)),
            this.amount + 34.0F,
            this.amount2 + 13.5F + this.value * 10.0F,
            ThemeManager.INSTANCE.getColor7()
         );
      FontRenderer fontRenderer4 = Fonts.getFontRenderer4();
      String text2;
      if (this.flag) {
         text2 = this.text;
      } else {
         Integer[] integers = new Integer[]{
            ((Color)this.setting.get()).getRed(),
            ((Color)this.setting.get()).getGreen(),
            ((Color)this.setting.get()).getBlue(),
            ((Color)this.setting.get()).getAlpha()
         };
         text2 = String.valueOf(((Number)CollectionsKt.listOf(integers).get(this.value)).intValue());
      }

      fontRenderer4.render2(
         context,
         text2,
         this.amount + 47.0F,
         this.amount2 + 13.0F + this.value * 10.0F,
         Render2D.INSTANCE.computeColor(ThemeManager.INSTANCE.getColor8(), this.flag ? (float)Math.sin(System.currentTimeMillis() / 50.0) + 0.5F : 1.0F)
      );
   }

   public final void applyGetAlpha(int value, int value2) {
      float amount3 = this.amount + 40.0F;
      float amount4 = this.amount + 55.0F;
      float value3 = value;
      if (amount3 <= value3 ? value3 <= amount4 : false) {
         amount3 = this.amount2 + 10.0F + this.value * 10.0F;
         amount4 = this.amount2 + 18.0F + this.value * 10.0F;
         value3 = value2;
         if (amount3 <= value3 ? value3 <= amount4 : false) {
            this.flag = !this.flag;
            Integer[] integers = new Integer[]{
               ((Color)this.setting.get()).getRed(),
               ((Color)this.setting.get()).getGreen(),
               ((Color)this.setting.get()).getBlue(),
               ((Color)this.setting.get()).getAlpha()
            };
            this.text = String.valueOf(((Number)CollectionsKt.listOf(integers).get(this.value)).intValue());
         }
      }
   }

   public final void applyLength(char symbol) {
      if (this.text.length() < 3 && this.flag) {
         this.text = this.text + symbol;
      }
   }

   public final void applyLength(int value) {
      if (this.flag) {
         switch (value) {
            case 257:
               this.flag = false;

               try {
                  int text2 = Mth.clamp(Integer.parseInt(this.text), 0, 255);
                  Color settingItem = (Color)this.setting.get();
                  this.setting
                     .set(
                        new Color(
                           this.value == 0 ? text2 : settingItem.getRed(),
                           this.value == 1 ? text2 : settingItem.getGreen(),
                           this.value == 2 ? text2 : settingItem.getBlue(),
                           this.value == 3 ? text2 : settingItem.getAlpha()
                        )
                     );
               } catch (Exception exception) {
               }
            case 258:
            default:
               break;
            case 259:
               if (this.text.length() > 0) {
                  this.text = this.text.substring(0, this.text.length() - 1);
               }
         }
      }
   }
}