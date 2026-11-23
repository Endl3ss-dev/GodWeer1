// deobf by pusia
package su.catlean.client.features.ui.panel.panels.macro;

import java.awt.Color;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jetbrains.annotations.NotNull;
import su.catlean.client.features.ui.panel.components.PanelComponent;
import su.catlean.client.features.ui.panel.panels.MacroPanel;
import su.catlean.client.render.TexturedQuadPass;
import su.catlean.client.render.font.Fonts;
import su.catlean.client.services.NotificationManager;
import su.catlean.client.services.ThemeManager;
import su.catlean.client.services.client.macro.ChatMacro;
import su.catlean.client.services.client.macro.ItemMacro;
import su.catlean.client.services.client.macro.Macro;
import su.catlean.utils.KeyNames;
import su.catlean.utils.animation.Animation;
import su.catlean.utils.render.Render2D;

public final class MacroItem extends PanelComponent {
   @NotNull
   private Macro macros;
   @NotNull
   private final Animation animation;

   public MacroItem(Macro macros) {
      this.macros = macros;
      this.animation = new Animation(NotificationManager.Notification.Type.OUT_QUINT, 200L);
   }

   @NotNull
   public final Macro getMacros() {
      return this.macros;
   }

   @Override
   public void render(GuiGraphicsExtractor context, float amount, float amount2, float amount3, float amount4) {
      boolean flag = (amount <= amount3 ? amount3 <= amount + 70.0F : false) && (amount2 <= amount4 ? amount4 <= amount2 + 25.0F : false);
      MacroPanel.INSTANCE.setPanelComponent3(flag ? this : MacroPanel.INSTANCE.getPanelComponent3());
      this.animation.applyDoubleValue(flag ? 1.0F : 0.0F);
      Color alpha = Render2D.INSTANCE.fetchAlpha(ThemeManager.INSTANCE.getColor10(), ThemeManager.INSTANCE.getColor11(), this.macros.isFlag() ? 1.0F : 0.0F);
      Color alpha2 = Render2D.INSTANCE.fetchAlpha(ThemeManager.INSTANCE.getColor13(), ThemeManager.INSTANCE.getColor12(), this.macros.isFlag() ? 1.0F : 0.0F);
      TexturedQuadPass.defaultImpl(
         context.pose(), amount + 4.0F, amount2, 70.0F, 25.0F, 2.0F, 2.5F + this.animation.getAmount(), 1.0F, 1.0F, alpha, alpha2, 0.0F, 0.0F, 0.0F, 14336, null
      );
      TexturedQuadPass.defaultImpl(
         context.pose(),
         amount + 54.0F,
         amount2 + 5.0F,
         15.0F,
         15.0F,
         2.0F,
         2.0F,
         0.0F,
         1.0F,
         ThemeManager.INSTANCE.getColor5(),
         ThemeManager.INSTANCE.getColor6(),
         0.0F,
         0.0F,
         0.0F,
         14336,
         null
      );
      float amount5 = amount + 54.0F;
      if (amount3 <= amount + 69.0F ? amount5 <= amount3 : false) {
         amount5 = amount2 + 5.0F;
         if (amount4 <= amount2 + 20.0F ? amount5 <= amount4 : false) {
            MacroPanel.INSTANCE.setFlag(true);
         }
      }

      Fonts.getFontRenderer().render(context, this.macros.getId(), amount + 6.0F, amount2, Color.WHITE);
      Macro macros2 = this.macros;
      String text = macros2 instanceof ChatMacro ? "chat" : (macros2 instanceof ItemMacro ? "item" : "setting");
      Fonts.getFontRenderer().render(context, text, amount + 6.0F, amount2 + 11.0F, Render2D.INSTANCE.computeColor(Color.WHITE, 0.7F));
      context.pose().pushMatrix();
      String text2 = KeyNames.INSTANCE.computeText(this.macros.getBind().getValue());
      String text3 = text2;
      if (text2.length() > 1) {
         context.pose().translate(amount + 54.0F + 7.5F, amount2 + 5.0F + 7.5F);
         context.pose().scale(0.5F, 0.5F);
         context.pose().translate(-(amount + 54.0F + 7.5F), -(amount2 + 5.0F + 7.5F));
         if (text2.length() > 5) {
            text3 = text2.substring(0, 4);
         }
      }

      Fonts.getFontRenderer2()
         .render2(
            context,
            Intrinsics.areEqual(MacroPanel.INSTANCE.getMacroItem2(), this)
               ? StringsKt.dropLast("...", 3 - (int)(4 * (System.currentTimeMillis() % 700 / 700.0)))
               : text3,
            amount + 61.5F,
            amount2 + 11.0F,
            Render2D.INSTANCE.computeColor(Color.WHITE, 0.7F)
         );
      context.pose().popMatrix();
   }

   @Override
   public boolean checkIntValue(float amount, float amount2, int value) {
      return false;
   }
}