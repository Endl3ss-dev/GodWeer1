// deobf by pusia
package su.catlean.client.features.modules.cosmetic;

import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.render.LightMapUpdateEvent;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.gofra.Flow;

public final class FullBright extends Module {
   @NotNull
   public static final FullBright INSTANCE = new FullBright();

   private FullBright() {
      super("full-bright", SubCategories.getSubCategory6(), null, 4, null);
   }

   @Flow
   private final void onLightMapUpdate(LightMapUpdateEvent lightMapUpdateEvent) {
      lightMapUpdateEvent.cancel();
   }
}