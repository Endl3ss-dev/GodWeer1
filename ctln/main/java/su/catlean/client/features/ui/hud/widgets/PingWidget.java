// deobf by pusia
package su.catlean.client.features.ui.hud.widgets;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jetbrains.annotations.NotNull;
import su.catlean.client.features.ui.hud.Widget;
import su.catlean.client.services.ServerInfoService;

public final class PingWidget extends Widget {
   @NotNull
   public static final PingWidget INSTANCE = new PingWidget();

   private PingWidget() {
      super("ping-widget");
   }

   @Override
   public void render(GuiGraphicsExtractor context) {
      this.render(context, ServerInfoService.defaultImpl(ServerInfoService.INSTANCE, null, 1, null) + " ms");
   }
}