// deobf by pusia
package su.catlean.client.services;

import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.service.Service;
import su.catlean.client.services.proxy.ProxyManager;

public final class FriendManager implements Service {
   @NotNull
   public static final FriendManager INSTANCE = new FriendManager();

   private FriendManager() {
   }

   public final boolean checkContains(String name) {
      return ProxyManager.INSTANCE.getFriendsDataSource().getItems().contains(name);
   }

   public final boolean checkGetGameProfile(Player player) {
      return this.testText(player.getGameProfile().name());
   }

   public final boolean testText(String name) {
      return ProxyManager.INSTANCE.getFriendsDataSource().getItems().add(name);
   }

   public final boolean checkGetGameProfile2(Player player) {
      return this.checkRemove(player.getGameProfile().name());
   }

   public final boolean checkRemove(String name) {
      return ProxyManager.INSTANCE.getFriendsDataSource().getItems().remove(name);
   }

   public final void applyClear() {
      ProxyManager.INSTANCE.getFriendsDataSource().getItems().clear();
   }
}