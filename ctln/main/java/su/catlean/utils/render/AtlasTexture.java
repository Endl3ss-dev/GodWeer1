// deobf by pusia
package su.catlean.utils.render;

import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public final class AtlasTexture {
   @NotNull
   private static final Identifier catlean = Identifiers.computeIdentifier("catlean", "textures/atlas.png");

   @NotNull
   public static final Identifier getCatlean() {
      return catlean;
   }
}