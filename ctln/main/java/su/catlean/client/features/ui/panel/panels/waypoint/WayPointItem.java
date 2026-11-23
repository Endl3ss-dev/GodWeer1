// deobf by pusia
package su.catlean.client.features.ui.panel.panels.waypoint;

import java.awt.Color;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.WayPoint;
import su.catlean.client.features.ui.panel.components.PanelComponent;
import su.catlean.client.features.ui.panel.panels.WaypointPanel;
import su.catlean.client.render.TexturedQuadPass;
import su.catlean.client.render.font.Fonts;
import su.catlean.client.services.NotificationManager;
import su.catlean.client.services.ThemeManager;
import su.catlean.utils.animation.Animation;
import su.catlean.utils.render.Render2D;

public final class WayPointItem extends PanelComponent {
   @NotNull
   private WayPoint point;
   @NotNull
   private final Animation animation;

   public WayPointItem(WayPoint point) {
      this.point = point;
      this.animation = new Animation(NotificationManager.Notification.Type.OUT_QUINT, 200L);
   }

   @NotNull
   public final WayPoint getPoint() {
      return this.point;
   }

   @Override
   public void render(GuiGraphicsExtractor context, float amount, float amount2, float amount3, float amount4) {
      boolean flag = (amount <= amount3 ? amount3 <= amount + 70.0F : false) && (amount2 <= amount4 ? amount4 <= amount2 + 25.0F : false);
      this.animation.applyDoubleValue(flag ? 1.0F : 0.0F);
      if (flag) {
         WaypointPanel.INSTANCE.setPanelComponent3(this);
      }

      Color alpha = Render2D.INSTANCE.fetchAlpha(ThemeManager.INSTANCE.getColor10(), ThemeManager.INSTANCE.getColor11(), this.point.isFlag() ? 1.0F : 0.0F);
      Color alpha2 = Render2D.INSTANCE.fetchAlpha(ThemeManager.INSTANCE.getColor13(), ThemeManager.INSTANCE.getColor12(), this.point.isFlag() ? 1.0F : 0.0F);
      TexturedQuadPass.defaultImpl(
         context.pose(), amount + 4.0F, amount2, 70.0F, 25.0F, 2.0F, 2.5F + this.animation.getAmount(), 1.0F, 1.0F, alpha, alpha2, 0.0F, 0.0F, 0.0F, 14336, null
      );
      Fonts.getFontRenderer().render(context, this.point.getId(), amount + 6.0F, amount2, Color.WHITE);
      Fonts.getFontRenderer().render(context, this.point.getServer(), amount + 6.0F, amount2 + 11.0F, Render2D.INSTANCE.computeColor(Color.WHITE, 0.7F));
   }

   @Override
   public boolean checkIntValue(float amount, float amount2, int value) {
      return false;
   }
}