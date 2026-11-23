// deobf by pusia
package su.catlean.client.features.modules.attack;

import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.features.modules.misc.Blocks;
import su.catlean.gofra.Flow;
import su.catlean.utils.player.PlayerTargetUtil;

public final class Trap extends Blocks {
   @NotNull
   public static final Trap INSTANCE = new Trap();

   private Trap() {
      super("trap", SubCategories.getSubCategory2());
   }

   @Flow
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      this.applyGetBoundingBox(PlayerTargetUtil.defaultImpl(PlayerTargetUtil.INSTANCE, this.getRange(), AutoCrystal.TargetSort.FOV, false, false, 12, null));
   }
}