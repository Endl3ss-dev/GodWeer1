// deobf by pusia
package su.catlean.client.features.ui.panel.dialogs;

import java.awt.Color;
import kotlin.jvm.functions.Function1;
import kotlin.text.StringsKt;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2fStack;
import org.lwjgl.glfw.GLFW;
import su.catlean.client.render.TexturedQuadPass;
import su.catlean.client.render.font.FontRenderer;
import su.catlean.client.render.font.Fonts;
import su.catlean.client.services.ThemeManager;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.input.InputUtil;
import su.catlean.utils.render.Render2D;

public final class NameInputDialog implements Dialog {
   @NotNull
   private final Function1 action;
   @NotNull
   private String text;
   private int value;
   private boolean flag;

   public NameInputDialog(Function1 action) {
      this.action = action;
      this.text = "";
   }

   @NotNull
   public final Function1 getAction() {
      return this.action;
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
      boolean flag2;
      label37: {
         float amount5 = amount + 43.0F;
         if (amount3 <= amount + 77.0F ? amount5 <= amount3 : false) {
            amount5 = amount2 + 25.0F;
            if (amount4 <= amount2 + 38.0F ? amount5 <= amount4 : false) {
               flag2 = true;
               break label37;
            }
         }

         flag2 = false;
      }

      this.flag = flag2;
      TexturedQuadPass.defaultImpl(
         context.pose(),
         amount,
         amount2,
         120.0F,
         60.0F,
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
         amount2 + 25.0F,
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
      float amount22 = amount2 + 26.0F;
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
      Fonts.getFontRenderer().render2(context, "Or drag file there", amount + 60.0F, amount2 + 46.0F, Render2D.INSTANCE.computeColor(Color.WHITE, 0.4F));
   }

   @Override
   public void applyLength(char symbol) {
      if (this.text.length() < 16) {
         this.text = this.text + symbol;
      }
   }

   @Override
   public void applyGetWindow(int value) {
      switch (value) {
         case 86:
            if (InputUtil.checkContains(341)) {
               String glfwGetClipboardString = GLFW.glfwGetClipboardString(MinecraftHolder.getMinecraft().getWindow().handle());
               if (glfwGetClipboardString == null) {
                  glfwGetClipboardString = "";
               }

               String text2 = glfwGetClipboardString;
               this.text = this.text + text2;
            }
            break;
         case 257:
            this.update();
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
      } else {
         return false;
      }
   }

   private final void update() {
      if (!StringsKt.isBlank(this.text)) {
         this.action.invoke(this.text);
      }
   }
}