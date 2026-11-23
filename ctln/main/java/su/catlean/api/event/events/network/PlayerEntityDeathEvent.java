// deobf by pusia
package su.catlean.api.event.events.network;

import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.catlean.api.event.Event;

public final class PlayerEntityDeathEvent extends Event {
   @NotNull
   public static final PlayerEntityDeathEvent INSTANCE = new PlayerEntityDeathEvent();
   @Nullable
   private static Player player;
   private static int totemsPopped;

   private PlayerEntityDeathEvent() {
   }

   @Nullable
   public final Player getPlayer() {
      return player;
   }

   public final void setPlayer(Player player) {
      PlayerEntityDeathEvent.player = player;
   }

   public final int getTotemsPopped() {
      return totemsPopped;
   }

   public final void setTotemsPopped(int value) {
      totemsPopped = value;
   }
}