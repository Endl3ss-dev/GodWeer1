// deobf by pusia
package su.catlean.client.features.modules.misc;

import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.network.PlayerListSizeEvent;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.gofra.Flow;

public final class ExtraTab extends Module {
   @NotNull
   public static final ExtraTab INSTANCE = new ExtraTab();

   private ExtraTab() {
      super("extra-tab", SubCategories.getSubCategory11(), null, 4, null);
   }

   @Flow
   private final void onPlayerListSize(PlayerListSizeEvent playerListSizeEvent) {
      playerListSizeEvent.cancel();
   }
}