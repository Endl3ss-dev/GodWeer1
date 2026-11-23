// deobf by pusia
package su.catlean.api.enums;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import org.jetbrains.annotations.NotNull;

public enum AutoSwitchMode {
   SWITCH,
   TOGGLE,
   EATING;

   private static final AutoSwitchMode[] autoSwitchModes = $values();
   private static final EnumEntries<AutoSwitchMode> enumEntries = EnumEntriesKt.enumEntries(autoSwitchModes);

   private AutoSwitchMode() {
   }

   @NotNull
   public static EnumEntries<AutoSwitchMode> getEnumEntries() {
      return enumEntries;
   }

   private static final AutoSwitchMode[] $values() {
      return new AutoSwitchMode[]{SWITCH, TOGGLE, EATING};
   }
}