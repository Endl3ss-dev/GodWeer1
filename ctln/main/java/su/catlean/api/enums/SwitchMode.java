// deobf by pusia
package su.catlean.api.enums;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import org.jetbrains.annotations.NotNull;

public enum SwitchMode {
   NONE,
   SILENT,
   FORCE;

   private static final SwitchMode[] switchModes = $values();
   private static final EnumEntries<SwitchMode> enumEntries = EnumEntriesKt.enumEntries(switchModes);

   private SwitchMode() {
   }

   @NotNull
   public static EnumEntries<SwitchMode> getEnumEntries() {
      return enumEntries;
   }

   private static final SwitchMode[] $values() {
      return new SwitchMode[]{NONE, SILENT, FORCE};
   }
}