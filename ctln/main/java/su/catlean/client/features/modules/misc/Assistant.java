// deobf by pusia
package su.catlean.client.features.modules.misc;

import org.jetbrains.annotations.NotNull;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;

public final class Assistant extends Module {
   @NotNull
   public static final Assistant INSTANCE = new Assistant();

   private Assistant() {
      super("assistant", SubCategories.getSubCategory11(), null, 4, null);
   }
}