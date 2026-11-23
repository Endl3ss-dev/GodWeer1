// deobf by pusia
package su.catlean.client.features.ui.clickgui.popup;

import java.awt.Color;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;
import su.catlean.client.features.ui.ClickGuiScreen;
import su.catlean.client.features.ui.panel.ThemeEditor;
import su.catlean.client.features.ui.panel.components.KitSelector;
import su.catlean.client.render.SvgTexture;
import su.catlean.client.render.TexturedQuadPass;
import su.catlean.client.services.NotificationManager;
import su.catlean.client.services.ThemeManager;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.animation.Animation;
import su.catlean.utils.render.Render2D;

public class PopupWindow {
   @NotNull
   private final Animation animation = new Animation(NotificationManager.Notification.Type.OUT_QUINT, 200L);
   private float amount = 200.0F;
   private float amount2 = 250.0F;
   private float amount3;
   private boolean flag;

   public PopupWindow() {
   }

   @NotNull
   public final Animation getAnimation() {
      return this.animation;
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

   public final boolean isFlag() {
      return this.flag;
   }

   public final void setFlag(boolean flag) {
      this.flag = flag;
   }

   public final void render2(GuiGraphicsExtractor context, int value, int value2) {
      label41: {
         this.animation.applyDoubleValue(!this.flag ? 1.0F : 0.0F);
         float guiScaledWidth = MinecraftHolder.getMinecraft().getWindow().getGuiScaledWidth() / 2.0F - this.amount / 2.0F;
         float guiScaledHeight = MinecraftHolder.getMinecraft().getWindow().getGuiScaledHeight() / 2.0F - this.amount2 / 2.0F - 50.0F;
         context.pose().pushMatrix();
         context.pose().translate(guiScaledWidth + this.amount / 2.0F, guiScaledHeight + this.amount2 / 2.0F);
         context.pose().scale(Math.clamp(this.animation.getAmount(), 0.0F, 2.0F), Math.clamp(this.animation.getAmount(), 0.0F, 2.0F));
         context.pose().translate(-(guiScaledWidth + this.amount / 2.0F), -(guiScaledHeight + this.amount2 / 2.0F));
         TexturedQuadPass.defaultImpl(
            context.pose(),
            guiScaledWidth,
            guiScaledHeight,
            this.amount,
            this.amount2,
            7.0F,
            2.0F,
            0.0F,
            1.0F,
            ThemeManager.INSTANCE.getColor4(),
            ThemeManager.INSTANCE.getColor6(),
            0.0F,
            0.0F,
            0.0F,
            14336,
            null
         );
         Render2D instance = Render2D.INSTANCE;
         Matrix3x2fStack pose = context.pose();
         float value3 = guiScaledWidth + 6;
         float value4 = guiScaledHeight + 24;
         float amount4 = this.amount - 12;
         Color[] colors = new Color[]{new Color(1157627903, true)};
         float value5 = 0.5F;
         float value6 = amount4;
         float value7 = value4;
         float value8 = value3;
         Matrix3x2fStack matrix3x2fStack = pose;
         instance.render(matrix3x2fStack, value8, value7, value6, value5, colors);
         float amount5 = guiScaledWidth + this.amount - 25;
         float amount6 = guiScaledWidth + this.amount - 5;
         float value9 = value;
         if (amount5 <= value9 ? value9 <= amount6 : false) {
            float value10 = guiScaledHeight + 5.0F;
            amount6 = guiScaledHeight + 25.0F;
            value9 = value2;
            if (value10 <= value9 ? value9 <= amount6 : false) {
               TexturedQuadPass.defaultImpl(
                  context.pose(),
                  guiScaledWidth + this.amount - 25,
                  guiScaledHeight + 5,
                  20.0F,
                  15.0F,
                  4.0F,
                  2.0F,
                  0.0F,
                  1.0F,
                  ThemeManager.INSTANCE.getColor4().brighter(),
                  ThemeManager.INSTANCE.getColor6(),
                  0.0F,
                  0.0F,
                  0.0F,
                  14336,
                  null
               );
               SvgTexture.defaultImpl(
                  ThemeEditor.INSTANCE.getSvgTexture2(),
                  (Matrix3x2f)context.pose(),
                  guiScaledWidth + this.amount - 19.5,
                  guiScaledHeight + 5.5,
                  0.0F,
                  0.0F,
                  ThemeManager.INSTANCE.getColor7(),
                  null,
                  0.0F,
                  216,
                  null
               );
               break label41;
            }
         }

         TexturedQuadPass.defaultImpl(
            context.pose(),
            guiScaledWidth + this.amount - 25,
            guiScaledHeight + 5,
            20.0F,
            15.0F,
            4.0F,
            2.0F,
            0.0F,
            1.0F,
            ThemeManager.INSTANCE.getColor4(),
            ThemeManager.INSTANCE.getColor6(),
            0.0F,
            0.0F,
            0.0F,
            14336,
            null
         );
         SvgTexture.defaultImpl(
            ThemeEditor.INSTANCE.getSvgTexture2(),
            (Matrix3x2f)context.pose(),
            guiScaledWidth + this.amount - 19.5,
            guiScaledHeight + 5.5,
            0.0F,
            0.0F,
            ThemeManager.INSTANCE.getColor8(),
            null,
            0.0F,
            216,
            null
         );
      }

      this.render(context, value, value2);
      context.pose().popMatrix();
      if (this.flag && this.animation.getAmount() <= 0.0F) {
         if (KitSelector.companion.getPopupWindow() == null) {
            ClickGuiScreen.INSTANCE.setPopupWindow(null);
         } else {
            KitSelector.companion.updatePopupWindow(null);
         }
      }
   }

   public void render(GuiGraphicsExtractor context, int value, int value2) {
   }

   public void applyEntrySet(int value, int value2, int value3) {
      float guiScaledWidth = MinecraftHolder.getMinecraft().getWindow().getGuiScaledWidth() / 2.0F - this.amount / 2.0F;
      float guiScaledHeight = MinecraftHolder.getMinecraft().getWindow().getGuiScaledHeight() / 2.0F - this.amount2 / 2.0F - 50.0F;
      float amount4 = guiScaledWidth + this.amount - 25;
      float amount5 = guiScaledWidth + this.amount - 5;
      float value4 = value;
      if (amount4 <= value4 ? value4 <= amount5 : false) {
         amount4 = guiScaledHeight + 5.0F;
         amount5 = guiScaledHeight + 25.0F;
         value4 = value2;
         if (amount4 <= value4 ? value4 <= amount5 : false) {
            this.flag = true;
         }
      }
   }

   public void update() {
   }

   public void updateValue(int value) {
      if (value == 256) {
         this.flag = true;
      }
   }

   public void updateSymbol(char symbol) {
   }

   public void updateAmount(double amount, double amount2, double amount3) {
      this.amount3 += (float)amount3 * 10.0F;
      this.amount3 = Math.min(this.amount3, 0.0F);
   }
}