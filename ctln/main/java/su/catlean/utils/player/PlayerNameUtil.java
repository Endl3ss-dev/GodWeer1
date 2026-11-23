// deobf by pusia
package su.catlean.utils.player;

import net.minecraft.world.entity.player.Player;
import su.catlean.client.services.FriendManager;

public final class PlayerNameUtil {
   public static final boolean checkGetGameProfile(Player self) {
      return FriendManager.INSTANCE.checkContains(self.getGameProfile().name());
   }
}