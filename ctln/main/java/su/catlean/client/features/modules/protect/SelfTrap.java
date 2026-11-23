// deobf by pusia
package su.catlean.client.features.modules.protect;

import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.features.modules.misc.Blocks;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;

public final class SelfTrap extends Blocks {
   @NotNull
   public static final SelfTrap INSTANCE = new SelfTrap();

   private SelfTrap() {
      super("self-trap", SubCategories.getSubCategory());
   }

   @Flow
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      this.applyGetBoundingBox((Player)MinecraftHolder.getLocalPlayer());
   }
}