// deobf by pusia
package su.catlean.api.addon;

import org.jetbrains.annotations.NotNull;
import su.catlean.api.addon.registry.CatLeanEventBus;
import su.catlean.api.addon.registry.CatLeanRegistry;

public interface CatLeanApi {
   @NotNull
   CatLeanRegistry getRegistry();

   @NotNull
   CatLeanEventBus getEvents();

   @NotNull
   CatLeanAddonLogger logger(String text);
}