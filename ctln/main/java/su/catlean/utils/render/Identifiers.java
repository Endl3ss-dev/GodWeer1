// deobf by pusia
package su.catlean.utils.render;

import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public final class Identifiers {
   @NotNull
   public static final Identifier computeIdentifier(String string) {
      return Identifier.parse(string);
   }

   @NotNull
   public static final Identifier computeIdentifier(String id, String string) {
      return Identifier.fromNamespaceAndPath(id, string);
   }
}