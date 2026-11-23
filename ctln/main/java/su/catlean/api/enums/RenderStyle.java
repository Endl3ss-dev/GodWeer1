// deobf by pusia
package su.catlean.api.enums;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import org.jetbrains.annotations.NotNull;

public enum RenderStyle {
   DEFAULT,
   SHADER,
   OFF;

   private static final RenderStyle[] renderStyles = $values();
   private static final EnumEntries<RenderStyle> enumEntries = EnumEntriesKt.enumEntries(renderStyles);

   private RenderStyle() {
   }

   @NotNull
   public static EnumEntries<RenderStyle> getEnumEntries() {
      return enumEntries;
   }

   private static final RenderStyle[] $values() {
      return new RenderStyle[]{DEFAULT, SHADER, OFF};
   }
}