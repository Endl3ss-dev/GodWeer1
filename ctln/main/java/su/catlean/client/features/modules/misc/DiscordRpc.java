// deobf by pusia
package su.catlean.client.features.modules.misc;

import org.jetbrains.annotations.NotNull;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;

public final class DiscordRpc extends Module {
   @NotNull
   public static final DiscordRpc INSTANCE = new DiscordRpc();

   private DiscordRpc() {
      super("RPC", SubCategories.getSubCategory11(), null, 4, null);
   }
}