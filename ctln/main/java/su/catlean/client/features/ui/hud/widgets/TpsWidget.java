// deobf by pusia
package su.catlean.client.features.ui.hud.widgets;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jetbrains.annotations.NotNull;
import su.catlean.client.features.ui.hud.Widget;
import su.catlean.client.services.ServerInfoService;

public final class TpsWidget extends Widget {
   @NotNull
   public static final TpsWidget INSTANCE = new TpsWidget();

   private TpsWidget() {
      super("tps-widget");
   }

   @Override
   public void render(GuiGraphicsExtractor context) {
      this.render(
         context,
         ServerInfoService.INSTANCE.getAmount2() + " (" + (ServerInfoService.INSTANCE.isFlag2() ? "--.--" : ServerInfoService.INSTANCE.getAmount()) + ") t/s"
      );
   }
}