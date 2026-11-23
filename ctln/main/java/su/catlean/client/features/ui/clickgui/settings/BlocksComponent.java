// deobf by pusia
package su.catlean.client.features.ui.clickgui.settings;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.setting.Setting;
import su.catlean.api.setting.values.BlocksValue;
import su.catlean.client.features.ui.ClickGuiScreen;
import su.catlean.client.render.TexturedQuadPass;
import su.catlean.client.render.font.Fonts;
import su.catlean.client.services.NotificationManager;
import su.catlean.client.services.ThemeManager;
import su.catlean.utils.animation.Animation;
import su.catlean.utils.render.Render2D;

public final class BlocksComponent extends SettingComponent {
   @NotNull
   private final Setting setting;
   @NotNull
   private final Animation animation;
   private boolean flag;

   public BlocksComponent(Setting setting) {
      super(setting);
      this.setting = setting;
      this.animation = new Animation(NotificationManager.Notification.Type.OUT_QUINT, 300L);
   }

   @NotNull
   @Override
   public Setting getSetting() {
      return this.setting;
   }

   @Override
   public void render(GuiGraphicsExtractor context, int value, int value2, float amount) {
      this.setAmount5(14.0F);
      this.animation.applyDoubleValue(this.flag ? 1.0F : 0.0F);
      TexturedQuadPass.defaultImpl(
         context.pose(),
         this.getAmount3(),
         this.getAmount4() - 1.0F,
         this.getAmount5() - 7.0F,
         this.animation.getAmount() * (this.getAmount6() - 14.0F) + 14.0F - 1.0F,
         3.0F,
         2.5F,
         0.0F,
         1.0F,
         ThemeManager.INSTANCE.getColor11(),
         ThemeManager.INSTANCE.getColor12(),
         0.0F,
         0.0F,
         0.0F,
         14336,
         null
      );
      Fonts.getFontRenderer3()
         .render(
            context,
            this.getSetting().getText(),
            this.getAmount3() + 2.0F,
            this.getAmount4() - 0.5F,
            Render2D.INSTANCE.computeColor(ThemeManager.INSTANCE.getColor2(), 1.0F - this.animation.getAmount())
         );
      String text = ((BlocksValue)this.getSetting().getObject2()).getBlocks().isEmpty()
         ? "empty"
         : String.valueOf(((BlocksValue)this.getSetting().getObject2()).getBlocks().size());
      float amount2 = Fonts.getFontRenderer3().computeAmount(text);
      Fonts.getFontRenderer3()
         .render(
            context,
            text,
            this.getAmount3() + this.getAmount5() - amount2 - 11.0F,
            this.getAmount4() - 0.5F,
            Render2D.INSTANCE.computeColor(ThemeManager.INSTANCE.getColor2(), 1.0F - this.animation.getAmount())
         );
   }

   @Override
   public void updateAmount(double amount, double amount2, int value) {
      float amount3 = this.getAmount3();
      if (amount <= this.getAmount3() + this.getAmount5() - 7.0F ? amount3 <= amount : false) {
         amount3 = this.getAmount4();
         if (amount2 <= this.getAmount4() + 12 ? amount3 <= amount2 : false) {
            ClickGuiScreen.INSTANCE.setPopupWindow(new BlockPicker(this.getSetting()));
         }
      }
   }

   @Override
   public void updateAmount2(double amount, double amount2, int value) {
   }

   @Override
   public void updateValue(int value) {
   }

   @Override
   public void updateSymbol(char symbol) {
   }

   @Override
   public boolean testAmount(double amount, double amount2, double amount3) {
      return false;
   }

   @Override
   public void update() {
   }
}