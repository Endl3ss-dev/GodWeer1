// deobf by pusia
package su.catlean.client.features.ui.panel;

import kotlin.jvm.internal.DefaultConstructorMarker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.catlean.api.data.Rectangle;
import su.catlean.client.features.ui.panel.dialogs.Dialog;
import su.catlean.client.render.TexturedQuadPass;
import su.catlean.client.render.font.Fonts;
import su.catlean.client.services.ThemeManager;
import su.catlean.utils.render.Render2D;

public abstract class Panel {
   @NotNull
   private String name;
   private float amount;
   private float amount2;
   private float amount3;
   private float amount4;
   private float amount5;
   private float amount6;
   @Nullable
   private Dialog dialog;
   private float amount7;
   private float amount8;
   private boolean flag;
   private boolean flag2;
   private boolean flag3;
   private boolean flag4;

   public Panel(String name, float amount, float amount2, float amount3, float amount4) {
      this.name = name;
      this.amount = amount;
      this.amount2 = amount2;
      this.amount3 = amount3;
      this.amount4 = amount4;
      this.amount5 = this.amount3;
      this.amount6 = this.amount4;
   }

   public Panel(String text, float amount, float amount2, float amount3, float amount4, int value, DefaultConstructorMarker defaultConstructorMarker) {
      if ((value & 2) != 0) {
         amount = 50.0F;
      }

      if ((value & 4) != 0) {
         amount2 = 50.0F;
      }

      if ((value & 8) != 0) {
         amount3 = 230.0F;
      }

      if ((value & 16) != 0) {
         amount4 = 180.0F;
      }

      this(text, amount, amount2, amount3, amount4);
   }

   @NotNull
   public final String getName() {
      return this.name;
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

   @Nullable
   public final Dialog getDialog() {
      return this.dialog;
   }

   public final void setDialog(Dialog dialog) {
      this.dialog = dialog;
   }

   public final void update() {
      this.flag4 = true;
   }

   public abstract void applyClear();

   public void render(GuiGraphicsExtractor context, int value, int value2) {
      if (this.flag4) {
         this.applyClear();
         this.flag4 = false;
      }

      TexturedQuadPass.defaultImpl(
         context.pose(),
         this.amount,
         this.amount2,
         this.amount3,
         this.amount4,
         5.0F,
         1.0F,
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
      Rectangle rectangle = new Rectangle(this.amount + 2, this.amount2 + 14, this.amount + this.amount3 - 2, this.amount2 + this.amount4);
      instance.render(context, rectangle);
      TexturedQuadPass.defaultImpl(
         context.pose(),
         this.amount + 1,
         this.amount2 + 12,
         this.amount3 - 2,
         this.amount4 - 14.0F,
         5.0F,
         1.0F,
         0.1F,
         0.5F,
         ThemeManager.INSTANCE.getColor5(),
         ThemeManager.INSTANCE.getColor6(),
         0.0F,
         0.0F,
         0.0F,
         14336,
         null
      );
      Render2D.INSTANCE.render(context);
      TexturedQuadPass.defaultImpl(
         context.pose(),
         this.amount + this.amount3 - 10,
         this.amount2 + 3,
         8.0F,
         8.0F,
         4.0F,
         0.0F,
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
      TexturedQuadPass.defaultImpl(
         context.pose(),
         this.amount + this.amount3 - 20,
         this.amount2 + 3,
         8.0F,
         8.0F,
         4.0F,
         0.0F,
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
      TexturedQuadPass.defaultImpl(
         context.pose(),
         this.amount + this.amount3 - 30,
         this.amount2 + 3,
         8.0F,
         8.0F,
         4.0F,
         0.0F,
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
      Fonts.getFontRenderer().render2(context, this.name, this.amount + this.amount3 / 2, this.amount2 + 6.0F, ThemeManager.INSTANCE.getColor5());
      if (this.flag) {
         this.amount = value - this.amount7;
         this.amount2 = value2 - this.amount8;
      }

      if (this.flag2) {
         this.amount3 = Math.max(this.amount5, value - this.amount7);
      }

      if (this.flag3) {
         this.amount4 = Math.max(this.amount6, value2 - this.amount8);
      }
   }

   public void updateAmount(float amount, float amount2, int value) {
      float amount9 = this.amount;
      if (amount <= this.amount + this.amount3 ? amount9 <= amount : false) {
         amount9 = this.amount2;
         if ((amount2 <= this.amount2 + 15 ? amount9 <= amount2 : false) && PanelScreen.INSTANCE.getPanel() == null) {
            this.flag = true;
            this.amount7 = amount - this.amount;
            this.amount8 = amount2 - this.amount2;
         }
      }

      amount9 = this.amount;
      if (amount <= this.amount + this.amount3 ? amount9 <= amount : false) {
         amount9 = this.amount2 + this.amount4 - 5;
         if ((amount2 <= this.amount2 + this.amount4 ? amount9 <= amount2 : false) && PanelScreen.INSTANCE.getPanel() == null) {
            this.flag3 = true;
            this.amount8 = amount2 - this.amount4;
         }
      }

      amount9 = this.amount + this.amount3 - 5;
      if (amount <= this.amount + this.amount3 ? amount9 <= amount : false) {
         amount9 = this.amount2;
         if ((amount2 <= this.amount2 + this.amount4 ? amount9 <= amount2 : false) && PanelScreen.INSTANCE.getPanel() == null) {
            this.flag2 = true;
            this.amount7 = amount - this.amount3;
         }
      }

      if (this.flag || this.flag2 || this.flag3) {
         PanelScreen.INSTANCE.setPanel(this);
      }
   }

   public void updateSymbol(char symbol) {
      if (this.dialog != null) {
         this.dialog.applyLength(symbol);
      }
   }

   public void updateValue(int value, int value2, int value3) {
      if (this.dialog != null) {
         this.dialog.applyGetWindow(value);
      }
   }

   public void applyClamp(double amount, double amount2, double amount3) {
   }

   public final void update2() {
      this.flag = false;
      this.flag2 = false;
      this.flag3 = false;
      PanelScreen.INSTANCE.setPanel(null);
   }
}