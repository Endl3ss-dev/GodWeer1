// deobf by pusia
package su.catlean.client.features.ui.panel.dialogs;

import java.awt.Color;
import kotlin.text.StringsKt;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2fStack;
import su.catlean.api.setting.values.Bind;
import su.catlean.client.features.ui.panel.panels.MacroPanel;
import su.catlean.client.render.TexturedQuadPass;
import su.catlean.client.render.font.FontRenderer;
import su.catlean.client.render.font.Fonts;
import su.catlean.client.services.MacroManager;
import su.catlean.client.services.ThemeManager;
import su.catlean.client.services.client.macro.ChatMacro;
import su.catlean.client.services.client.macro.ItemMacro;
import su.catlean.client.services.client.macro.Macro;
import su.catlean.client.services.client.macro.SettingMacro;
import su.catlean.utils.render.Render2D;

public final class MacroDialog implements Dialog {
   @NotNull
   private String text = "";
   private int value;
   private boolean flag;
   private boolean flag2;
   private boolean flag3;
   private boolean flag4;

   public MacroDialog() {
   }

   @NotNull
   public final String getText() {
      return this.text;
   }

   public final int getValue() {
      return this.value;
   }

   public final void setValue(int value) {
      this.value = value;
   }

   @Override
   public void render(GuiGraphicsExtractor context, float amount, float amount2, float amount3, float amount4) {
      boolean flag5;
      label127: {
         float amount5 = amount + 43.0F;
         if (amount3 <= amount + 77.0F ? amount5 <= amount3 : false) {
            amount5 = amount2 + 45.0F;
            if (amount4 <= amount2 + 58.0F ? amount5 <= amount4 : false) {
               flag5 = true;
               break label127;
            }
         }

         flag5 = false;
      }

      label120: {
         this.flag = flag5;
         TexturedQuadPass.defaultImpl(
            context.pose(),
            amount,
            amount2,
            120.0F,
            66.0F,
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
         TexturedQuadPass.defaultImpl(
            context.pose(),
            amount + 43.0F,
            amount2 + 45.0F,
            34.0F,
            13.0F,
            4.0F,
            2.0F,
            0.1F,
            1.0F,
            ThemeManager.INSTANCE.getColor5(),
            ThemeManager.INSTANCE.getColor6(),
            0.0F,
            0.0F,
            0.0F,
            14336,
            null
         );
         FontRenderer fontRenderer = Fonts.getFontRenderer();
         String text2 = StringsKt.isBlank(this.text) ? "Cancel" : "Create";
         float amount6 = amount + 48;
         float amount22 = amount2 + 46.0F;
         Color white = Color.WHITE;
         float value2 = amount22;
         float value3 = amount6;
         String text3 = text2;
         fontRenderer.render(context, text3, value3, value2, white);
         TexturedQuadPass.defaultImpl(
            context.pose(),
            amount + 5.0F,
            amount2 + 5.0F,
            110.0F,
            13.0F,
            3.0F,
            2.0F,
            0.1F,
            1.0F,
            ThemeManager.INSTANCE.getColor5(),
            ThemeManager.INSTANCE.getColor6(),
            0.0F,
            0.0F,
            0.0F,
            14336,
            null
         );
         Render2D instance = Render2D.INSTANCE;
         Matrix3x2fStack pose = context.pose();
         float amount7 = amount + 33.0F;
         amount6 = amount2 + 9.0F;
         Color[] colors = new Color[]{new Color(1157627903, true)};
         value2 = 7.0F;
         value3 = 0.5F;
         float value4 = amount6;
         float value5 = amount7;
         Matrix3x2fStack matrix3x2fStack = pose;
         instance.render(matrix3x2fStack, value5, value4, value3, value2, colors);
         Fonts.getFontRenderer().render(context, "Name", amount + 8.0F, amount2 + 6.0F, Color.WHITE);
         Fonts.getFontRenderer()
            .render(
               context,
               this.text + (System.currentTimeMillis() % 700 > 350L ? "" : "|"),
               amount + 37.0F,
               amount2 + 6.0F,
               Render2D.INSTANCE.computeColor(Color.WHITE, 0.8F)
            );
         TexturedQuadPass.defaultImpl(
            context.pose(),
            amount + 5.0F,
            amount2 + 25.0F,
            110.0F,
            13.0F,
            3.0F,
            2.0F,
            0.1F,
            1.0F,
            ThemeManager.INSTANCE.getColor5(),
            ThemeManager.INSTANCE.getColor6(),
            0.0F,
            0.0F,
            0.0F,
            14336,
            null
         );
         float amount8 = amount + 5.0F;
         if (amount3 <= amount + 41.0F ? amount8 <= amount3 : false) {
            float amount23 = amount2 + 25.0F;
            if (amount4 <= amount2 + 38.0F ? amount23 <= amount4 : false) {
               flag5 = true;
               break label120;
            }
         }

         flag5 = false;
      }

      label113: {
         this.flag2 = flag5;
         float amount9 = amount + 42.0F;
         if (amount3 <= amount + 77.0F ? amount9 <= amount3 : false) {
            amount9 = amount2 + 25.0F;
            if (amount4 <= amount2 + 38.0F ? amount9 <= amount4 : false) {
               flag5 = true;
               break label113;
            }
         }

         flag5 = false;
      }

      label106: {
         this.flag3 = flag5;
         float amount10 = amount + 78.0F;
         if (amount3 <= amount + 100.0F ? amount10 <= amount3 : false) {
            amount10 = amount2 + 25.0F;
            if (amount4 <= amount2 + 38.0F ? amount10 <= amount4 : false) {
               flag5 = true;
               break label106;
            }
         }

         flag5 = false;
      }

      this.flag4 = flag5;
      FontRenderer fontRenderer2 = Fonts.getFontRenderer();
      float amount11 = amount + 23.0F;
      float amount24 = amount2 + 30.0F;
      Color white2 = this.value == 0 ? Color.WHITE : Render2D.INSTANCE.computeColor(Color.WHITE, 0.3F);
      fontRenderer2.render2(context, "Chat", amount11, amount24, white2);
      Render2D instance2 = Render2D.INSTANCE;
      Matrix3x2fStack pose2 = context.pose();
      float amount12 = amount + 41.0F;
      amount11 = amount2 + 29.0F;
      Color[] colors2 = new Color[]{new Color(1157627903, true)};
      float value6 = 7.0F;
      float value7 = 0.5F;
      float value8 = amount11;
      float value9 = amount12;
      Matrix3x2fStack matrix3x2fStack2 = pose2;
      instance2.render(matrix3x2fStack2, value9, value8, value7, value6, colors2);
      fontRenderer2 = Fonts.getFontRenderer();
      amount11 = amount + 58.0F;
      amount24 = amount2 + 30.0F;
      white2 = this.value == 1 ? Color.WHITE : Render2D.INSTANCE.computeColor(Color.WHITE, 0.3F);
      fontRenderer2.render2(context, "Item", amount11, amount24, white2);
      Render2D instance3 = Render2D.INSTANCE;
      Matrix3x2fStack pose3 = context.pose();
      amount12 = amount + 77.0F;
      amount11 = amount2 + 29.0F;
      Color[] colors3 = new Color[]{new Color(1157627903, true)};
      value6 = 7.0F;
      value7 = 0.5F;
      value8 = amount11;
      value9 = amount12;
      matrix3x2fStack2 = pose3;
      instance3.render(matrix3x2fStack2, value9, value8, value7, value6, colors3);
      fontRenderer2 = Fonts.getFontRenderer();
      amount11 = amount + 94.0F;
      amount24 = amount2 + 30.0F;
      white2 = this.value == 2 ? Color.WHITE : Render2D.INSTANCE.computeColor(Color.WHITE, 0.3F);
      fontRenderer2.render2(context, "Setting", amount11, amount24, white2);
   }

   @Override
   public void applyLength(char symbol) {
      if (this.text.length() < 10) {
         this.text = this.text + symbol;
      }
   }

   @Override
   public void applyGetWindow(int value) {
      switch (value) {
         case 257:
            this.update();
         case 258:
         default:
            break;
         case 259:
            this.text = StringsKt.dropLast(this.text, 1);
      }
   }

   @Override
   public boolean testAmount(float amount, float amount2, int value) {
      if (this.flag) {
         this.update();
         return true;
      } else if (this.flag2) {
         this.value = 0;
         return true;
      } else if (this.flag3) {
         this.value = 1;
         return true;
      } else if (this.flag4) {
         this.value = 2;
         return true;
      } else {
         return false;
      }
   }

   private final void update() {
      if (!StringsKt.isBlank(this.text)) {
         Macro macro = switch (this.value) {
            case 0 -> (Macro)(new ChatMacro(this.text, new Bind(0, false, false, 7, null), true, ""));
            case 1 -> (Macro)(new ItemMacro(this.text, new Bind(0, false, false, 7, null), true, "", false));
            default -> {
               String text2 = this.text;
               Bind bind = new Bind(0, false, false, 7, null);
               boolean flag5 = false;
               String text3 = "0";
               String text4 = "0";
               String text5 = "";
               String text6 = "";
               boolean flag6 = true;
               Bind bind2 = bind;
               yield new SettingMacro(text2, bind2, flag6, text6, text5, text4, text3, flag5);
            }
         };
         MacroManager.INSTANCE.testMacro(macro);
         MacroPanel.INSTANCE.applyClear();
      }

      MacroPanel.INSTANCE.setDialog(null);
   }
}