// deobf by pusia
package su.catlean.api.enums;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import org.jetbrains.annotations.NotNull;

public enum Safety {
   UNSAFE,
   SAFE;

   private static final Safety[] safetys = $values();
   private static final EnumEntries<Safety> enumEntries = EnumEntriesKt.enumEntries(safetys);

   private Safety() {
   }

   @NotNull
   public static EnumEntries<Safety> getEnumEntries() {
      return enumEntries;
   }

   private static final Safety[] $values() {
      return new Safety[]{UNSAFE, SAFE};
   }
}