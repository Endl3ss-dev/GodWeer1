// deobf by pusia
package su.catlean.client.features.ui.panel.dialogs;

import java.awt.Color;
import kotlin.text.StringsKt;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2fStack;
import su.catlean.client.features.ui.panel.components.ConfigType;
import su.catlean.client.features.ui.panel.panels.ConfigPanel;
import su.catlean.client.render.TexturedQuadPass;
import su.catlean.client.render.font.FontRenderer;
import su.catlean.client.render.font.Fonts;
import su.catlean.client.services.ConfigFileHelper;
import su.catlean.client.services.SoundManager;
import su.catlean.client.services.ThemeManager;
import su.catlean.utils.render.Render2D;

public final class ConfigNameDialog implements Dialog {
   @NotNull
   private ConfigType type;
   @NotNull
   private String text;
   private boolean flag;
   private boolean flag2;

   public ConfigNameDialog(ConfigType type) {
      this.type = type;
      this.text = "";
   }

   @NotNull
   public final ConfigType getType() {
      return this.type;
   }

   @NotNull
   public final String getText() {
      return this.text;
   }

   @Override
   public void render(GuiGraphicsExtractor context, float amount, float amount2, float amount3, float amount4) {
      boolean flag3;
      label62: {
         float amount5 = amount + 43.0F;
         if (amount3 <= amount + 77.0F ? amount5 <= amount3 : false) {
            amount5 = amount2 + 25.0F;
            if (amount4 <= amount2 + 38.0F ? amount5 <= amount4 : false) {
               flag3 = true;
               break label62;
            }
         }

         flag3 = false;
      }

      this.flag = flag3;
      this.flag2 = (amount <= amount3 ? amount3 <= amount + 120.0F : false) && (amount2 <= amount4 ? amount4 <= amount2 + 46.0F : false);
      TexturedQuadPass.defaultImpl(
         context.pose(),
         amount,
         amount2,
         120.0F,
         46.0F,
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
      float value = amount22;
      float value2 = amount6;
      String text3 = text2;
      fontRenderer.render(context, text3, value2, value, white);
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
      amount6 = amount2 + 8.0F;
      Color[] colors = new Color[]{new Color(1157627903, true)};
      value = 7.0F;
      value2 = 0.5F;
      float value3 = amount6;
      float value4 = amount7;
      Matrix3x2fStack matrix3x2fStack = pose;
      instance.render(matrix3x2fStack, value4, value3, value2, value, colors);
      Fonts.getFontRenderer().render(context, "Name", amount + 8.0F, amount2 + 6.0F, Color.WHITE);
      Fonts.getFontRenderer()
         .render(
            context,
            this.text + (System.currentTimeMillis() % 700 > 350L ? "" : "|"),
            amount + 37.0F,
            amount2 + 6.0F,
            Render2D.INSTANCE.computeColor(Color.WHITE, 0.8F)
         );
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
         case 256:
            ConfigPanel.INSTANCE.setDialog(null);
            break;
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
      } else {
         if (!this.flag2) {
            ConfigPanel.INSTANCE.setDialog(null);
         }

         return false;
      }
   }

   private final void update() {
      if (!StringsKt.isBlank(this.text)) {
         ConfigFileHelper.applyOrdinal2(this.text, this.type);
         SoundManager.INSTANCE.getSound19().applyExecuteIfPossible();
         ConfigPanel.INSTANCE.applyClear();
      }

      ConfigPanel.INSTANCE.setDialog(null);
   }
}