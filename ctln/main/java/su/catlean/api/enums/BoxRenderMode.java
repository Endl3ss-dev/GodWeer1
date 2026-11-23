// deobf by pusia
package su.catlean.api.enums;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import org.jetbrains.annotations.NotNull;

public enum BoxRenderMode {
   BOX,
   FRAME,
   SHADER,
   DEFAULT;

   private static final BoxRenderMode[] boxRenderModes = $values();
   private static final EnumEntries<BoxRenderMode> enumEntries = EnumEntriesKt.enumEntries(boxRenderModes);

   private BoxRenderMode() {
   }

   @NotNull
   public static EnumEntries<BoxRenderMode> getEnumEntries() {
      return enumEntries;
   }

   private static final BoxRenderMode[] $values() {
      return new BoxRenderMode[]{BOX, FRAME, SHADER, DEFAULT};
   }
}