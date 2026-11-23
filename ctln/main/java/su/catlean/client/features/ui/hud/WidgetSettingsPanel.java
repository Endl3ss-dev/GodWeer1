// deobf by pusia
package su.catlean.client.features.ui.hud;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2f;
import su.catlean.api.data.Rectangle;
import su.catlean.client.render.SvgTexture;
import su.catlean.client.render.TexturedQuadPass;
import su.catlean.client.render.font.Fonts;
import su.catlean.client.services.NotificationManager;
import su.catlean.client.services.ThemeManager;
import su.catlean.utils.animation.Animation;
import su.catlean.utils.render.Render2D;

public final class WidgetSettingsPanel {
   @NotNull
   private final Widget widget;
   @NotNull
   private final Animation animation;
   @NotNull
   private final Animation animation2;
   @NotNull
   private final SvgTexture svgTexture;
   private float amount;
   private float amount2;
   private float amount3;
   private float amount4;

   public WidgetSettingsPanel(Widget widget) {
      this.widget = widget;
      this.animation = new Animation(NotificationManager.Notification.Type.OUT_QUINT, 400L);
      this.animation2 = new Animation(NotificationManager.Notification.Type.OUT_QUINT, 200L);
      this.svgTexture = new SvgTexture("check", 12, 12);
      this.amount3 = 116.0F;
      this.amount4 = 13.0F;
   }

   @NotNull
   public final Widget getWidget() {
      return this.widget;
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

   public final float getAmount3() {
      return this.amount3;
   }

   public final void setAmount3(float amount) {
      this.amount3 = amount;
   }

   public final float getAmount4() {
      return this.amount4;
   }

   public final void setAmount4(float amount) {
      this.amount4 = amount;
   }

   public final void render(GuiGraphicsExtractor context, int value, int value2, float amount, float amount2) {
      float value3;
      label41: {
         this.amount = amount;
         this.amount2 = amount2;
         this.animation.applyDoubleValue(this.widget.getEnabled() ? 1.0F : 0.0F);
         float amount5 = this.amount + this.amount3 - 18.0F;
         float amount6 = this.amount + this.amount3 - 7.0F;
         float value4 = value;
         if (amount5 <= value4 ? value4 <= amount6 : false) {
            amount5 = this.amount2;
            amount6 = this.amount2 + 11.0F;
            value4 = value2;
            if (amount5 <= value4 ? value4 <= amount6 : false) {
               value3 = 1.0F;
               break label41;
            }
         }

         value3 = 0.0F;
      }

      this.animation2.applyDoubleValue(value3);
      if (this.animation.getAmount() != 0.0F) {
         Render2D instance = Render2D.INSTANCE;
         Rectangle rectangle = new Rectangle(
            this.amount + this.amount3 - 20.0F, this.amount2, this.amount + this.amount3 - 20.0F + this.animation.getAmount() * 11, this.amount2 + 11.0F
         );
         instance.render(context, rectangle);
         SvgTexture.defaultImpl(
            this.svgTexture,
            (Matrix3x2f)context.pose(),
            this.amount + this.amount3 - 20.5,
            this.amount2 - 1.5,
            0.0F,
            0.0F,
            ThemeManager.INSTANCE.getColor2(),
            null,
            0.0F,
            216,
            null
         );
         Render2D.INSTANCE.render(context);
      }

      Fonts.getFontRenderer3()
         .render(
            context,
            this.widget.getText(),
            this.amount + 2.0F,
            this.amount2 - 1.0F,
            Render2D.INSTANCE.fetchAlpha(ThemeManager.INSTANCE.getColor8(), ThemeManager.INSTANCE.getColor7(), this.animation.getAmount())
         );
      TexturedQuadPass.defaultImpl(
         context.pose(),
         this.amount + this.amount3 - 20.0F,
         this.amount2,
         11.0F,
         11.0F,
         3.0F,
         2.5F + this.animation2.getAmount(),
         0.15F * this.animation.getAmount(),
         1.0F,
         ThemeManager.INSTANCE.getColor11(),
         ThemeManager.INSTANCE.getColor12(),
         0.0F,
         0.0F,
         0.0F,
         14336,
         null
      );
   }

   public final void updateAmount(double amount, double amount2) {
      float amount5 = this.amount + this.amount3 - 20.0F;
      if (amount <= this.amount + this.amount3 - 7.0F ? amount5 <= amount : false) {
         amount5 = this.amount2;
         if (amount2 <= this.amount2 + 11.0F ? amount5 <= amount2 : false) {
            this.widget.updateFlag(!this.widget.getEnabled());
         }
      }
   }
}