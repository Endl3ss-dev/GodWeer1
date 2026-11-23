// deobf by pusia
package su.catlean.api.enums;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import org.jetbrains.annotations.NotNull;

public enum HoleType {
   SINGLE,
   DOUBLE_X,
   DOUBLE_Z,
   QUAD;

   private static final HoleType[] holeTypes = $values();
   private static final EnumEntries<HoleType> enumEntries = EnumEntriesKt.enumEntries(holeTypes);

   private HoleType() {
   }

   @NotNull
   public static EnumEntries<HoleType> getEnumEntries() {
      return enumEntries;
   }

   private static final HoleType[] $values() {
      return new HoleType[]{SINGLE, DOUBLE_X, DOUBLE_Z, QUAD};
   }
}