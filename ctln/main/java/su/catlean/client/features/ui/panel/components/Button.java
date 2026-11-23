// deobf by pusia
package su.catlean.client.features.ui.panel.components;

import java.awt.Color;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.DefaultConstructorMarker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jetbrains.annotations.NotNull;
import su.catlean.client.render.TexturedQuadPass;
import su.catlean.client.render.font.Fonts;
import su.catlean.client.services.NotificationManager;
import su.catlean.client.services.ThemeManager;
import su.catlean.utils.animation.Animation;

public final class Button {
   private float amount;
   private float amount2;
   private float amount3;
   private float amount4;
   @NotNull
   private String label;
   @NotNull
   private Function0 clickActionX;
   private final int value;
   @NotNull
   private final Color fill;
   @NotNull
   private final Color outline;
   private final float amount5;
   private final float amount6;
   @NotNull
   private final Animation animation;

   public Button(
      float amount,
      float amount2,
      float amount3,
      float amount4,
      String label,
      Function0 clickActionX,
      int value,
      Color fill,
      Color outline,
      float amount5,
      float amount6
   ) {
      this.amount = amount;
      this.amount2 = amount2;
      this.amount3 = amount3;
      this.amount4 = amount4;
      this.label = label;
      this.clickActionX = clickActionX;
      this.value = value;
      this.fill = fill;
      this.outline = outline;
      this.amount5 = amount5;
      this.amount6 = amount6;
      this.animation = new Animation(NotificationManager.Notification.Type.OUT_QUINT, 200L);
   }

   public Button(
      float amount,
      float amount2,
      float amount3,
      float amount4,
      String text,
      Function0 callback,
      int value,
      Color color,
      Color color2,
      float amount5,
      float amount6,
      int value2,
      DefaultConstructorMarker defaultConstructorMarker
   ) {
      if ((value2 & 64) != 0) {
         value = 0;
      }

      if ((value2 & 128) != 0) {
         color = ThemeManager.INSTANCE.getColor4();
      }

      if ((value2 & 256) != 0) {
         color2 = Color.WHITE;
      }

      if ((value2 & 512) != 0) {
         amount5 = 2.0F;
      }

      if ((value2 & 1024) != 0) {
         amount6 = 3.0F;
      }

      this(amount, amount2, amount3, amount4, text, callback, value, color, color2, amount5, amount6);
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

   @NotNull
   public final Function0 getClickActionX() {
      return this.clickActionX;
   }

   public final int getValue() {
      return this.value;
   }

   @NotNull
   public final Color getFill() {
      return this.fill;
   }

   @NotNull
   public final Color getOutline() {
      return this.outline;
   }

   public final float getAmount6() {
      return this.amount6;
   }

   public final void render(float amount, float amount2, float amount3, float amount4, GuiGraphicsExtractor context) {
      if (amount != -1.0F && amount2 != -1.0F) {
         this.amount = amount;
         this.amount2 = amount2;
      }

      this.animation.applyDoubleValue(this.testAmount(amount3, amount4) ? 1.0F : 0.0F);
      TexturedQuadPass.defaultImpl(
         context.pose(),
         this.amount,
         this.amount2,
         this.amount3,
         this.amount4,
         this.amount6,
         this.amount5,
         0.25F + this.animation.getAmount() / 3.0F,
         1.0F,
         this.fill,
         this.outline,
         0.0F,
         0.0F,
         0.0F,
         14336,
         null
      );
      Fonts.getFontRenderer5().render2(context, this.label, this.amount + this.amount3 / 2.0F, this.amount2 + this.amount4 / 2.0F, Color.WHITE);
   }

   public final void updateAmount(float amount, float amount2) {
      if (this.testAmount(amount, amount2)) {
         this.clickActionX.invoke();
      }
   }

   private final boolean testAmount(float amount, float amount2) {
      float amount7 = this.amount;
      if (amount <= this.amount + this.amount3 ? amount7 <= amount : false) {
         amount7 = this.amount2;
         if (amount2 <= this.amount2 + this.amount4 ? amount7 <= amount2 : false) {
            return true;
         }
      }

      return false;
   }
}