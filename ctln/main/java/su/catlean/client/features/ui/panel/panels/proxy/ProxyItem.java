// deobf by pusia
package su.catlean.client.features.ui.panel.panels.proxy;

import java.awt.Color;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.Proxy;
import su.catlean.client.features.ui.panel.components.PanelComponent;
import su.catlean.client.features.ui.panel.panels.ProxyPanel;
import su.catlean.client.render.TexturedQuadPass;
import su.catlean.client.render.font.Fonts;
import su.catlean.client.services.NotificationManager;
import su.catlean.client.services.ThemeManager;
import su.catlean.client.services.proxy.ProxyChecker;
import su.catlean.utils.animation.Animation;
import su.catlean.utils.render.Render2D;

public final class ProxyItem extends PanelComponent {
   @NotNull
   private Proxy proxy;
   @NotNull
   private final Animation animation;

   public ProxyItem(Proxy proxy) {
      this.proxy = proxy;
      this.animation = new Animation(NotificationManager.Notification.Type.OUT_QUINT, 200L);
   }

   @NotNull
   public final Proxy getProxy() {
      return this.proxy;
   }

   @Override
   public void render(GuiGraphicsExtractor context, float amount, float amount2, float amount3, float amount4) {
      boolean flag = (amount <= amount3 ? amount3 <= amount + 70.0F : false) && (amount2 <= amount4 ? amount4 <= amount2 + 25.0F : false);
      this.animation.applyDoubleValue(flag ? 1.0F : 0.0F);
      if (flag) {
         ProxyPanel.INSTANCE.setPanelComponent3(this);
      }

      Color alpha = Render2D.INSTANCE
         .fetchAlpha(
            ThemeManager.INSTANCE.getColor10(),
            ThemeManager.INSTANCE.getColor11(),
            Intrinsics.areEqual(this.proxy, ProxyChecker.INSTANCE.getProxy()) ? 1.0F : 0.0F
         );
      Color alpha2 = Render2D.INSTANCE
         .fetchAlpha(
            ThemeManager.INSTANCE.getColor13(),
            ThemeManager.INSTANCE.getColor12(),
            Intrinsics.areEqual(this.proxy, ProxyChecker.INSTANCE.getProxy()) ? 1.0F : 0.0F
         );
      TexturedQuadPass.defaultImpl(
         context.pose(), amount + 4.0F, amount2, 70.0F, 25.0F, 2.0F, 2.5F + this.animation.getAmount(), 1.0F, 1.0F, alpha, alpha2, 0.0F, 0.0F, 0.0F, 14336, null
      );
      Fonts.getFontRenderer().render(context, this.proxy.getId(), amount + 6.0F, amount2, Color.WHITE);
      Fonts.getFontRenderer().render(context, this.proxy.getIp(), amount + 6.0F, amount2 + 11.0F, Render2D.INSTANCE.computeColor(Color.WHITE, 0.7F));

      String text = switch (this.proxy.getValue2()) {
         case -2 -> "...";
         case -1 -> "-";
         default -> this.proxy.getValue2() + "ms";
      };
      float amount5 = Fonts.getFontRenderer4().computeAmount(text);
      TexturedQuadPass.defaultImpl(
         context.pose(),
         amount + 67.0F - amount5,
         amount2 + 2.0F,
         amount5 + 4.0F,
         8.0F,
         2.0F,
         2.0F,
         0.15F,
         1.0F,
         ThemeManager.INSTANCE.getColor5(),
         ThemeManager.INSTANCE.getColor6(),
         0.0F,
         0.0F,
         0.0F,
         14336,
         null
      );
      Fonts.getFontRenderer4().render(context, text, amount + 68.0F - amount5, amount2 + 0.5F, Color.WHITE);
   }

   @Override
   public boolean checkIntValue(float amount, float amount2, int value) {
      return false;
   }
}