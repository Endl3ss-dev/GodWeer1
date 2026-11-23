// deobf by pusia
package su.catlean.api.addon.impl;

import java.util.List;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.addon.CatLeanAddon;
import su.catlean.utils.MinecraftHolder;

public final class AddonLoader {
   @NotNull
   public static final AddonLoader INSTANCE = new AddonLoader();
   private static boolean flag;

   private AddonLoader() {
   }

   public final void applyError() {
      if (!flag) {
         flag = true;
         List<CatLeanAddon> entrypoints = FabricLoader.getInstance().getEntrypoints("catlean:addon", CatLeanAddon.class);

         for (Object object : entrypoints) {
            CatLeanAddon catLeanAddon = (CatLeanAddon)object;

            try {
               catLeanAddon.onCatLeanAddon(CatLeanApiImpl.INSTANCE);
            } catch (Throwable exception) {
               MinecraftHolder.getCatlean().error("Could not load CatLean addon: " + catLeanAddon.getClass().getName(), exception);
            }
         }
      }
   }
}