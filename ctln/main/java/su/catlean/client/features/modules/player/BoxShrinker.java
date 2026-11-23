// deobf by pusia
package su.catlean.client.features.modules.player;

import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.player.BoxContractEvent;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;

public final class BoxShrinker extends Module {
   @NotNull
   public static final BoxShrinker INSTANCE = new BoxShrinker();
   private static boolean flag;

   private BoxShrinker() {
      super("box-shrinker", SubCategories.getSubCategory10(), null, 4, null);
   }

   public final boolean isFlag() {
      return flag;
   }

   public final void setFlag(boolean flag) {
      BoxShrinker.flag = flag;
   }

   @Flow
   private final void onBoxContract(BoxContractEvent boxContractEvent) {
      if (!flag) {
         flag = true;
         boxContractEvent.setValue(MinecraftHolder.getLocalPlayer().getBoundingBox().inflate(-1.0E-12, 0.0, -1.0E-12));
         flag = false;
         boxContractEvent.cancel();
      }
   }
}