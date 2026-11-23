// deobf by pusia
package su.catlean.client.features.ui.panel;

import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import org.jetbrains.annotations.NotNull;

public final class ThemeColorMap {
   @NotNull
   private final Long2ByteOpenHashMap map;
   private int value;

   public ThemeColorMap(Long2ByteOpenHashMap map, int value) {
      this.map = map;
      this.value = value;
   }

   @NotNull
   public final Long2ByteOpenHashMap getMap() {
      return this.map;
   }

   public final int getValue() {
      return this.value;
   }

   public final void setValue(int value) {
      this.value = value;
   }
}