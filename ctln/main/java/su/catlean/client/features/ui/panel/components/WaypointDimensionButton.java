// deobf by pusia
package su.catlean.client.features.ui.panel.components;

import java.awt.Color;
import kotlin.reflect.KMutableProperty0;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2fStack;
import su.catlean.client.render.TexturedQuadPass;
import su.catlean.client.render.font.FontRenderer;
import su.catlean.client.render.font.Fonts;
import su.catlean.client.services.ThemeManager;
import su.catlean.utils.render.Render2D;

public final class WaypointDimensionButton extends PanelComponent {
   private final float amount;
   @NotNull
   private KMutableProperty0 ref;
   @NotNull
   private KMutableProperty0 hoverRef;

   public WaypointDimensionButton(float amount, KMutableProperty0 ref, KMutableProperty0 hoverRef) {
      this.amount = amount;
      this.ref = ref;
      this.hoverRef = hoverRef;
   }

   @Override
   public void render(GuiGraphicsExtractor context, float amount, float amount2, float amount3, float amount4) {
      boolean flag = (amount <= amount3 ? amount3 <= amount + (this.amount - 88.0F) : false) && (amount2 <= amount4 ? amount4 <= amount2 + 13.0F : false);
      if (flag) {
         this.hoverRef.set(this);
      }

      TexturedQuadPass.defaultImpl(
         context.pose(),
         amount,
         amount2,
         this.amount - 88.0F,
         13.0F,
         2.0F,
         flag ? 2.0F : 0.0F,
         0.0F,
         1.0F,
         ThemeManager.INSTANCE.getColor5().darker(),
         ThemeManager.INSTANCE.getColor6(),
         0.0F,
         0.0F,
         0.0F,
         14336,
         null
      );
      Render2D instance = Render2D.INSTANCE;
      Matrix3x2fStack pose = context.pose();
      float amount5 = amount + (this.amount - 88.0F) * 0.33F;
      float amount22 = amount2 + 2.0F;
      Color[] colors = new Color[]{new Color(1157627903, true)};
      float value = 9.0F;
      float value2 = 0.5F;
      float value3 = amount22;
      float value4 = amount5;
      Matrix3x2fStack matrix3x2fStack = pose;
      instance.render(matrix3x2fStack, value4, value3, value2, value, colors);
      instance = Render2D.INSTANCE;
      pose = context.pose();
      amount5 = amount + (this.amount - 88.0F) * 0.66F;
      amount22 = amount2 + 2.0F;
      colors = new Color[]{new Color(1157627903, true)};
      value = 9.0F;
      value2 = 0.5F;
      value3 = amount22;
      value4 = amount5;
      matrix3x2fStack = pose;
      instance.render(matrix3x2fStack, value4, value3, value2, value, colors);
      FontRenderer fontRenderer = Fonts.getFontRenderer();
      amount22 = amount + (this.amount - 88.0F) * 0.16F;
      float amount23 = amount2 + 5.0F;
      Color color = ((Number)this.ref.get()).intValue() != 0 ? Render2D.INSTANCE.computeColor(Color.WHITE, 0.5F) : Color.WHITE;
      fontRenderer.render2(context, "Over", amount22, amount23, color);
      FontRenderer fontRenderer2 = Fonts.getFontRenderer();
      amount22 = amount + (this.amount - 88.0F) * 0.49F;
      amount23 = amount2 + 5.0F;
      Color color2 = ((Number)this.ref.get()).intValue() != 1 ? Render2D.INSTANCE.computeColor(Color.WHITE, 0.5F) : Color.WHITE;
      fontRenderer2.render2(context, "Nether", amount22, amount23, color2);
      FontRenderer fontRenderer3 = Fonts.getFontRenderer();
      amount22 = amount + (this.amount - 88.0F) * 0.81F;
      amount23 = amount2 + 5.0F;
      Color color3 = ((Number)this.ref.get()).intValue() != 2 ? Render2D.INSTANCE.computeColor(Color.WHITE, 0.5F) : Color.WHITE;
      fontRenderer3.render2(context, "End", amount22, amount23, color3);
   }

   @Override
   public boolean checkIntValue(float amount, float amount2, int value) {
      this.ref.set((((Number)this.ref.get()).intValue() + 1) % 3);
      return false;
   }
}