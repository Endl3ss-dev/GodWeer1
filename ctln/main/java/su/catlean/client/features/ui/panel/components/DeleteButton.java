// deobf by pusia
package su.catlean.client.features.ui.panel.components;

import java.awt.Color;
import kotlin.jvm.functions.Function0;
import kotlin.reflect.KMutableProperty0;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jetbrains.annotations.NotNull;
import su.catlean.client.render.TexturedQuadPass;
import su.catlean.client.render.font.Fonts;
import su.catlean.utils.render.Render2D;

public final class DeleteButton extends PanelComponent {
   @NotNull
   private Function0 clickAction;
   private float amount;
   @NotNull
   private KMutableProperty0 hoverRef;

   public DeleteButton(Function0 clickAction, float amount, KMutableProperty0 hoverRef) {
      this.clickAction = clickAction;
      this.amount = amount;
      this.hoverRef = hoverRef;
   }

   public final float getAmount() {
      return this.amount;
   }

   public final void setAmount(float amount) {
      this.amount = amount;
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
         flag ? 2.0F : 1.0F,
         0.1F,
         1.0F,
         Render2D.INSTANCE.computeColor(Color.RED, 0.5F),
         Color.RED,
         0.0F,
         0.0F,
         0.0F,
         14336,
         null
      );
      Fonts.getFontRenderer().render2(context, "Delete", amount + (this.amount - 88.0F) / 2.0F, amount2 + 5.0F, Color.WHITE);
   }

   @Override
   public boolean checkIntValue(float amount, float amount2, int value) {
      this.clickAction.invoke();
      return true;
   }
}