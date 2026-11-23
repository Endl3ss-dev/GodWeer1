// deobf by pusia
package su.catlean.client.features.ui.clickgui.settings;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import kotlin.collections.CollectionsKt;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2fStack;
import su.catlean.api.setting.Setting;
import su.catlean.client.render.TexturedQuadPass;
import su.catlean.client.render.font.FontRenderer;
import su.catlean.client.render.font.Fonts;
import su.catlean.client.services.ThemeManager;
import su.catlean.utils.render.Render2D;

public final class ColorPicker {
   @NotNull
   private final Setting setting;
   private float amount;
   private float amount2;
   private final int value;
   private boolean flag;
   @NotNull
   private String text;

   public ColorPicker(Setting setting, float amount, float amount2, int value) {
      this.setting = setting;
      this.amount = amount;
      this.amount2 = amount2;
      this.value = value;
      this.text = "-";
   }

   @NotNull
   public final Setting getSetting() {
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
            ((Color)this.setting.getObject2()).getRed(),
            ((Color)this.setting.getObject2()).getGreen(),
            ((Color)this.setting.getObject2()).getBlue(),
            ((Color)this.setting.getObject2()).getAlpha()
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
               ((Color)this.setting.getObject2()).getRed(),
               ((Color)this.setting.getObject2()).getGreen(),
               ((Color)this.setting.getObject2()).getBlue(),
               ((Color)this.setting.getObject2()).getAlpha()
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
                  Color object2 = (Color)this.setting.getObject2();
                  this.setting
                     .setObject2(
                        new Color(
                           this.value == 0 ? text2 : object2.getRed(),
                           this.value == 1 ? text2 : object2.getGreen(),
                           this.value == 2 ? text2 : object2.getBlue(),
                           this.value == 3 ? text2 : object2.getAlpha()
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

   public final boolean checkIntValue(double amount, double amount2, double amount3) {
      float amount4 = this.amount + 40.0F;
      float amount5 = this.amount + 55.0F;
      float amount6 = (float)amount;
      if (amount4 <= amount6 ? amount6 <= amount5 : false) {
         amount4 = this.amount2 + 10.0F + this.value * 10.0F;
         amount5 = this.amount2 + 18.0F + this.value * 10.0F;
         amount6 = (float)amount2;
         if (amount4 <= amount6 ? amount6 <= amount5 : false) {
            Integer[] integers = new Integer[]{
               ((Color)this.setting.getObject2()).getRed(),
               ((Color)this.setting.getObject2()).getGreen(),
               ((Color)this.setting.getObject2()).getBlue(),
               ((Color)this.setting.getObject2()).getAlpha()
            };
            List<Integer> listOf = CollectionsKt.listOf(integers);
            Iterable<Integer> iterable = listOf;
            var arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10));
            int value2 = 0;

            for (Object object : iterable) {
               int value3 = value2++;
               if (value3 < 0) {
                  CollectionsKt.throwIndexOverflow();
               }

               int value4 = ((Number)object).intValue();
               arrayList.add(value3 == this.value ? Mth.clamp(value4 + (int)amount3, 0, 255) : value4);
            }

            List list = arrayList;
            this.setting
               .setObject2(
                  new Color(
                     ((Number)list.get(0)).intValue(), ((Number)list.get(1)).intValue(), ((Number)list.get(2)).intValue(), ((Number)list.get(3)).intValue()
                  )
               );
            return true;
         }
      }

      return false;
   }
}