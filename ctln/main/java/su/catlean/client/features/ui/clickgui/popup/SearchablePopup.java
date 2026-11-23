// deobf by pusia
package su.catlean.client.features.ui.clickgui.popup;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import kotlin.text.StringsKt;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2fStack;
import su.catlean.api.data.Rectangle;
import su.catlean.client.render.TexturedQuadPass;
import su.catlean.client.render.font.FontRenderer;
import su.catlean.client.render.font.Fonts;
import su.catlean.client.services.ThemeManager;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.render.Render2D;

public abstract class SearchablePopup extends PopupWindow {
   @NotNull
   private final String title;
   @Nullable
   private Object object;
   private boolean flag;
   private boolean flag2;
   @NotNull
   private String text;

   public SearchablePopup(String title) {
      this.title = title;
      this.flag = true;
      this.text = "";
   }

   @NotNull
   public final String getText() {
      return this.text;
   }

   @Override
   public void render(GuiGraphicsExtractor context, int value, int value2) {
      float guiScaledWidth = MinecraftHolder.getMinecraft().getWindow().getGuiScaledWidth() / 2.0F - this.getAmount() / 2.0F;
      float guiScaledHeight = MinecraftHolder.getMinecraft().getWindow().getGuiScaledHeight() / 2.0F - this.getAmount2() / 2.0F - 50.0F;
      Fonts.getFontRenderer9().render2(context, this.title, guiScaledWidth + this.getAmount() / 2.0F, guiScaledHeight + 3, 10);
      this.render(context, guiScaledWidth, guiScaledHeight);
      this.render2(context, guiScaledWidth, guiScaledHeight);
      this.render(context, guiScaledWidth, guiScaledHeight, value, value2);
   }

   private final void render(GuiGraphicsExtractor guiGraphicsExtractor, float amount, float amount2) {
      TexturedQuadPass.defaultImpl(
         guiGraphicsExtractor.pose(),
         amount + 4.0F,
         amount2 + 28.0F,
         this.getAmount() - 8.0F,
         20.0F,
         4.0F,
         0.0F,
         0.1F,
         1.0F,
         ThemeManager.INSTANCE.getColor4().darker(),
         ThemeManager.INSTANCE.getColor6(),
         0.0F,
         0.0F,
         0.0F,
         14336,
         null
      );
      FontRenderer fontRenderer9 = Fonts.getFontRenderer9();
      String text2 = this.flag2 ? this.text + (MinecraftHolder.getTime() % 700 > 350L ? "_" : "") : "Search";
      float amount3 = amount + 8.0F;
      float amount22 = amount2 + 29.5F;
      Color color7 = this.flag2 ? ThemeManager.INSTANCE.getColor7() : ThemeManager.INSTANCE.getColor8();
      float value = amount22;
      float value2 = amount3;
      String text3 = text2;
      fontRenderer9.render(guiGraphicsExtractor, text3, value2, value, color7);
   }

   private final void render2(GuiGraphicsExtractor guiGraphicsExtractor, float amount, float amount2) {
      Matrix3x2fStack pose = guiGraphicsExtractor.pose();
      float amount3 = amount + 4.0F;
      float amount22 = amount2 + 52.0F;
      float amount4 = this.getAmount() - 8.0F;
      Color darker = ThemeManager.INSTANCE.getColor4().darker();
      Color color6 = ThemeManager.INSTANCE.getColor6();
      Object object2 = null;
      short shortValue = 14336;
      float value = 0.0F;
      float value2 = 0.0F;
      float value3 = 0.0F;
      Color color = color6;
      Color color2 = darker;
      float value4 = 1.0F;
      TexturedQuadPass.defaultImpl(pose, amount3, amount22, amount4, 17.0F, 4.0F, 2.0F, 0.1F, value4, color2, color, value3, value2, value, shortValue, object2);
      if (this.flag) {
         TexturedQuadPass.defaultImpl(
            guiGraphicsExtractor.pose(),
            amount + 6.0F,
            amount2 + 54.0F,
            this.getAmount() / 2.0F - 8.0F,
            13.0F,
            3.0F,
            2.0F,
            0.1F,
            1.0F,
            ThemeManager.INSTANCE.getColor4(),
            ThemeManager.INSTANCE.getColor6(),
            0.0F,
            0.0F,
            0.0F,
            14336,
            null
         );
      } else {
         pose = guiGraphicsExtractor.pose();
         amount3 = amount + 6.0F + this.getAmount() / 2.0F - 4;
         amount22 = amount2 + 54.0F;
         amount4 = this.getAmount() / 2.0F - 8.0F;
         darker = ThemeManager.INSTANCE.getColor4();
         color6 = ThemeManager.INSTANCE.getColor6();
         object2 = null;
         shortValue = 14336;
         value = 0.0F;
         value2 = 0.0F;
         value3 = 0.0F;
         color = color6;
         color2 = darker;
         value4 = 1.0F;
         TexturedQuadPass.defaultImpl(pose, amount3, amount22, amount4, 13.0F, 3.0F, 2.0F, 0.1F, value4, color2, color, value3, value2, value, shortValue, object2);
      }

      Fonts.getFontRenderer2()
         .render2(
            guiGraphicsExtractor,
            "All",
            amount + this.getAmount() / 4.0F,
            amount2 + 60.5F,
            this.flag ? ThemeManager.INSTANCE.getColor2() : ThemeManager.INSTANCE.getColor8()
         );
      Fonts.getFontRenderer2()
         .render2(
            guiGraphicsExtractor,
            "Selected",
            amount + 3 * this.getAmount() / 4.0F,
            amount2 + 60.5F,
            !this.flag ? ThemeManager.INSTANCE.getColor2() : ThemeManager.INSTANCE.getColor8()
         );
   }

   @NotNull
   public abstract List getItems();

   @NotNull
   public abstract String computeText(Object object2);

   public abstract boolean testObject(Object object2);

   public abstract void updateObject(Object object2);

   private final void render(GuiGraphicsExtractor guiGraphicsExtractor, float amount, float amount2, int value, int value2) {
      boolean var52 = false;
      this.object = null;
      Render2D instance = Render2D.INSTANCE;
      Rectangle rectangle = new Rectangle(amount, amount2 + 71.0F, amount + this.getAmount(), amount2 + this.getAmount2());
      instance.render(guiGraphicsExtractor, rectangle);
      Iterable items = this.getItems();
      int value3 = 0;
      var arrayList = new ArrayList();

      for (Object object2 : items) {
         if ((this.flag || this.testObject(object2)) && (this.text.length() == 0 || StringsKt.contains(this.computeText(object2), this.text, true))) {
            arrayList.add(object2);
         }
      }

      List list = arrayList;
      Iterator iterator = list.iterator();
      value3 = 0;

      while (iterator.hasNext()) {
         int value4 = value3++;
         Object object3 = iterator.next();
         float amount22 = amount2 + 78.0F + value4 * 20.0F + this.getAmount3();
         if (!(amount22 < amount2 + 40) && !(amount22 > amount2 + this.getAmount2())) {
            label66: {
               float amount3 = amount + this.getAmount();
               float value5 = value;
               if ((amount <= value5 ? value5 <= amount3 : false) && value2 > amount2 + 74.0F) {
                  amount3 = amount22 + 18;
                  value5 = value2;
                  if (amount22 <= value5 ? value5 <= amount3 : false) {
                     var52 = true;
                     break label66;
                  }
               }

               var52 = false;
            }

            boolean flag4 = var52;
            if (flag4) {
               this.object = object3;
               TexturedQuadPass.defaultImpl(
                  guiGraphicsExtractor.pose(),
                  amount + 6.0F,
                  amount22 - 2.0F,
                  this.getAmount() - 12.0F,
                  20.0F,
                  4.0F,
                  2.0F,
                  0.1F,
                  1.0F,
                  ThemeManager.INSTANCE.getColor4(),
                  ThemeManager.INSTANCE.getColor6(),
                  0.0F,
                  0.0F,
                  0.0F,
                  14336,
                  null
               );
            }

            FontRenderer fontRenderer2 = Fonts.getFontRenderer2();
            String take = StringsKt.take(this.computeText(object3), 28) + (this.computeText(object3).length() > 28 ? "..." : "");
            float amount4 = amount + 27.0F;
            float value6 = amount22 + 1.5F;
            Color color2 = this.testObject(object3) ? ThemeManager.INSTANCE.getColor2() : ThemeManager.INSTANCE.getColor8();
            float value7 = value6;
            float value8 = amount4;
            String text2 = take;
            fontRenderer2.render(guiGraphicsExtractor, text2, value8, value7, color2);
            this.render(guiGraphicsExtractor, amount, amount22, object3);
         }
      }

      Render2D.INSTANCE.render(guiGraphicsExtractor);
   }

   @Override
   public void applyEntrySet(int value, int value2, int value3) {
      label84: {
         float guiScaledWidth = MinecraftHolder.getMinecraft().getWindow().getGuiScaledWidth() / 2.0F - this.getAmount() / 2.0F;
         float guiScaledHeight = MinecraftHolder.getMinecraft().getWindow().getGuiScaledHeight() / 2.0F - this.getAmount2() / 2.0F - 50.0F;
         float amount = guiScaledWidth + 6.0F;
         float amount2 = guiScaledWidth + this.getAmount() / 2.0F - 2.0F;
         float value4 = value;
         if (amount <= value4 ? value4 <= amount2 : false) {
            amount = guiScaledHeight + 54.0F;
            amount2 = guiScaledHeight + 67.0F;
            value4 = value2;
            if (amount <= value4 ? value4 <= amount2 : false) {
               this.flag = true;
               this.setAmount3(0.0F);
               break label84;
            }
         }

         amount = guiScaledWidth + this.getAmount() / 2.0F;
         amount2 = guiScaledWidth + this.getAmount() - 12.0F;
         value4 = value;
         if (amount <= value4 ? value4 <= amount2 : false) {
            amount = guiScaledHeight + 54.0F;
            amount2 = guiScaledHeight + 67.0F;
            value4 = value2;
            if (amount <= value4 ? value4 <= amount2 : false) {
               this.flag = false;
               this.setAmount3(0.0F);
               break label84;
            }
         }

         amount = guiScaledHeight + 28.0F;
         amount2 = guiScaledHeight + 48.0F;
         value4 = value2;
         if (amount <= value4 ? value4 <= amount2 : false) {
            amount = guiScaledWidth + 4.0F;
            amount2 = guiScaledWidth + 4.0F + this.getAmount() - 8.0F;
            value4 = value;
            if (amount <= value4 ? value4 <= amount2 : false) {
               this.flag2 = !this.flag2;
            }
         }
      }

      if (this.object != null) {
         Object object2 = this.object;
         this.updateObject(object2);
      }

      super.applyEntrySet(value, value2, value3);
   }

   @Override
   public void updateValue(int value) {
      switch (value) {
         case 256:
         case 257:
            this.flag2 = false;
         case 258:
         default:
            break;
         case 259:
            this.text = StringsKt.dropLast(this.text, 1);
            if (this.text.length() == 0) {
               this.flag2 = false;
            }
      }

      super.updateValue(value);
   }

   @Override
   public void updateSymbol(char symbol) {
      if (this.flag2 && this.text.length() < 20) {
         this.text = this.text + symbol;
         this.setAmount3(0.0F);
      }
   }

   public void render(GuiGraphicsExtractor context, float amount, float amount2, Object object) {
   }
}