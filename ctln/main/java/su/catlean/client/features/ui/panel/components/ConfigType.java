// deobf by pusia
package su.catlean.client.features.ui.panel.components;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import org.jetbrains.annotations.NotNull;

public enum ConfigType {
   MODULES,
   WIDGETS;

   private static final ConfigType[] configTypes = $values();
   private static final EnumEntries<ConfigType> enumEntries = EnumEntriesKt.enumEntries(configTypes);

   private ConfigType() {
   }

   @NotNull
   public static EnumEntries<ConfigType> getEnumEntries() {
      return enumEntries;
   }

   private static final ConfigType[] $values() {
      return new ConfigType[]{MODULES, WIDGETS};
   }
}