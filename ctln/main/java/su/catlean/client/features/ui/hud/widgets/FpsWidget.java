// deobf by pusia
package su.catlean.client.features.ui.hud.widgets;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jetbrains.annotations.NotNull;
import su.catlean.client.features.ui.hud.Widget;
import su.catlean.client.services.ChestOpenTracker;
import su.catlean.utils.MinecraftHolder;

public final class FpsWidget extends Widget {
   @NotNull
   public static final FpsWidget INSTANCE = new FpsWidget();
   private static int value;

   private FpsWidget() {
      super("fps-widget");
   }

   @Override
   public void render(GuiGraphicsExtractor context) {
      this.render(context, value + " " + (MinecraftHolder.getMinecraft().options.enableVsync().get() ? "v-sync" : "fps"));
   }

   @Override
   public void update() {
      if (MinecraftHolder.getLocalPlayer().tickCount % 4 == 0) {
         value = ChestOpenTracker.INSTANCE.getValue();
      }
   }
}