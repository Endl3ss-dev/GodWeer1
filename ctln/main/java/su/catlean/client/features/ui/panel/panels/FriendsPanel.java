// deobf by pusia
package su.catlean.client.features.ui.panel.panels;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import kotlin.Unit;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.Rectangle;
import su.catlean.client.features.ui.panel.Panel;
import su.catlean.client.features.ui.panel.components.EditableLine;
import su.catlean.client.features.ui.panel.components.FriendEntry;
import su.catlean.client.features.ui.panel.dialogs.Dialog;
import su.catlean.client.features.ui.panel.dialogs.NameInputDialog;
import su.catlean.client.render.SvgTexture;
import su.catlean.client.render.TexturedQuadPass;
import su.catlean.client.render.font.Fonts;
import su.catlean.client.services.FriendManager;
import su.catlean.client.services.NotificationManager;
import su.catlean.client.services.ThemeManager;
import su.catlean.client.services.proxy.ProxyManager;
import su.catlean.utils.animation.Animation;
import su.catlean.utils.render.Render2D;

public final class FriendsPanel extends Panel {
   @NotNull
   public static final FriendsPanel INSTANCE = new FriendsPanel();
   @NotNull
   private static final SvgTexture svgTexture = new SvgTexture("panels/trash", 19, 19);
   @NotNull
   private static final List<FriendEntry> items = new ArrayList();
   private static boolean flag;
   @NotNull
   private static Animation animation = new Animation(NotificationManager.Notification.Type.OUT_QUINT, 100L);
   @NotNull
   private static Animation animation2 = new Animation(NotificationManager.Notification.Type.OUT_QUINT, 300L);
   private static float amount;

   private FriendsPanel() {
      super("homies-panel", 0.0F, 0.0F, 0.0F, 0.0F, 30, null);
   }

   @NotNull
   public final SvgTexture getSvgTexture() {
      return svgTexture;
   }

   @Override
   public void applyClear() {
      items.clear();

      for (Object item : ProxyManager.INSTANCE.getFriendsDataSource().getItems()) {
         String text = (String)item;
         items.add(new FriendEntry(text));
      }
   }

   @Override
   public void render(GuiGraphicsExtractor context, int value, int value2) {
      boolean flag2;
      label56: {
         this.setAmount3(110.0F);
         super.render(context, value, value2);
         float amount2 = this.getAmount() + 3.0F;
         float amount3 = this.getAmount() + 27.0F;
         float value3 = value;
         if (amount2 <= value3 ? value3 <= amount3 : false) {
            amount2 = this.getAmount2() + 5.0F;
            amount3 = this.getAmount2() + 13.0F;
            value3 = value2;
            if (amount2 <= value3 ? value3 <= amount3 : false) {
               flag2 = true;
               break label56;
            }
         }

         flag2 = false;
      }

      flag = flag2;
      animation.applyDoubleValue(amount);
      animation2.applyDoubleValue(this.getDialog() != null ? 0.5F : 0.0F);
      TexturedQuadPass.defaultImpl(
         context.pose(),
         this.getAmount() + 5.0F,
         this.getAmount2() + 3.0F,
         22.0F,
         8.0F,
         2.0F,
         flag ? 2.5F : 2.0F,
         flag ? 0.1F : 0.0F,
         1.0F,
         ThemeManager.INSTANCE.getColor4(),
         ThemeManager.INSTANCE.getColor6(),
         0.0F,
         0.0F,
         0.0F,
         14336,
         null
      );
      Fonts.getFontRenderer().render(context, "+", this.getAmount() + 13.5F, this.getAmount2() + 1.0F, Color.WHITE);
      Render2D instance = Render2D.INSTANCE;
      Rectangle rectangle = new Rectangle(
         this.getAmount(), this.getAmount2() + 14.0F, this.getAmount() + this.getAmount3() - 4.0F, this.getAmount2() + this.getAmount4() - 3.0F
      );
      instance.render(context, rectangle);
      float amount4 = 0.0F;
      amount4 = 17.0F + animation.getAmount();
      Iterable<FriendEntry> items2 = items;

      for (Object object : items2) {
         FriendEntry friendEntry = (FriendEntry)object;
         friendEntry.render(context, INSTANCE.getAmount(), INSTANCE.getAmount2() + amount4, value, value2);
         amount4 += 29.0F;
      }

      Render2D.INSTANCE.render(context);
      Dialog dialog = this.getDialog();
      if (dialog != null) {
         dialog.render(context, this.getAmount() + this.getAmount3() / 2.0F - 60.0F, this.getAmount2() + this.getAmount4() / 2 - 35.0F, value, value2);
      }
   }

   @Override
   public void updateAmount(float amount, float amount2, int value) {
      super.updateAmount(amount, amount2, value);
      Dialog dialog = this.getDialog();
      if (dialog != null ? !dialog.testAmount(amount, amount2, value) : true) {
         if (flag) {
            EditableLine.itemSearchCompanion.updateEditableLine(null);
            this.setDialog(new NameInputDialog(arg -> {
               FriendManager.INSTANCE.testText((String)arg);
               INSTANCE.applyClear();
               INSTANCE.setDialog(null);
               return Unit.INSTANCE;
            }));
         } else {
            boolean flag2 = false;

            for (Object item : items) {
               FriendEntry friendEntry = (FriendEntry)item;
               if (friendEntry.checkIntValue(amount, amount2, value)) {
                  flag2 = true;
               }
            }

            if (flag2) {
               this.applyClear();
            }

            this.setDialog(null);
         }
      }
   }

   @Override
   public void applyClamp(double amount, double amount2, double amount3) {
      float amount4 = this.getAmount();
      if (amount <= this.getAmount() + 76.0F ? amount4 <= amount : false) {
         amount4 = this.getAmount2();
         if (amount2 <= this.getAmount2() + this.getAmount4() - 2.0F ? amount4 <= amount2 : false) {
            FriendsPanel.amount += (float)amount3 * 7.0F;
            FriendsPanel.amount = Math.clamp(FriendsPanel.amount, items.size() * -29.0F, 0.0F);
         }
      }
   }

   static {
      INSTANCE.setAmount2(240.0F);
      INSTANCE.setAmount(290.0F);
      INSTANCE.setAmount3(110.0F);
      INSTANCE.applyClear();
   }
}