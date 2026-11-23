// deobf by pusia
package su.catlean;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import org.jetbrains.annotations.NotNull;

public final class CatLeanKt {
   // [deobf] field `text` was declared but never initialized or read in the original bytecode; removed.
   @NotNull
   private static final ModContainer modContainer = (ModContainer)FabricLoader.getInstance().getModContainer("catlean").orElseThrow();

   @NotNull
   public static final ModContainer getModContainer() {
      return modContainer;
   }
}