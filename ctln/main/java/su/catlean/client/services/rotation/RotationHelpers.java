// deobf by pusia
package su.catlean.client.services.rotation;

import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import su.catlean.utils.chat.TextStyleSerializer;

public final class RotationHelpers {
   @NotNull
   public static final String computeText(Component self) {
      return TextStyleSerializer.INSTANCE.computeText(self);
   }
}