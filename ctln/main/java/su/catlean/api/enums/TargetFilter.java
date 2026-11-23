// deobf by pusia
package su.catlean.api.enums;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import org.jetbrains.annotations.NotNull;

public enum TargetFilter {
   NONE,
   ALL,
   ONLY_CRYSTALS;

   private static final TargetFilter[] targetFilters = $values();
   private static final EnumEntries<TargetFilter> enumEntries = EnumEntriesKt.enumEntries(targetFilters);

   private TargetFilter() {
   }

   @NotNull
   public static EnumEntries<TargetFilter> getEnumEntries() {
      return enumEntries;
   }

   private static final TargetFilter[] $values() {
      return new TargetFilter[]{NONE, ALL, ONLY_CRYSTALS};
   }
}