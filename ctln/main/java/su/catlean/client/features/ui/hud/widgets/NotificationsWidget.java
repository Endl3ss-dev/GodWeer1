// deobf by pusia
package su.catlean.client.features.ui.hud.widgets;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jetbrains.annotations.NotNull;
import su.catlean.client.features.ui.hud.Widget;

public final class NotificationsWidget extends Widget {
   @NotNull
   public static final NotificationsWidget INSTANCE = new NotificationsWidget();

   private NotificationsWidget() {
      super("notifications");
   }

   @Override
   public void render(GuiGraphicsExtractor context) {
   }
}