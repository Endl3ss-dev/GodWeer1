// deobf by pusia
package su.catlean.client.features.ui;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import su.catlean.client.render.SvgTexture;

public final class ShutdownScreen extends Screen {
   @NotNull
   public static final ShutdownScreen INSTANCE = new ShutdownScreen();
   @NotNull
   private static final SvgTexture svgTexture = new SvgTexture("logo", 230, 50);

   private ShutdownScreen() {
      super(Component.nullToEmpty("catlean-sutdown"));
   }
}