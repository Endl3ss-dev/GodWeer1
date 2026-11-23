// deobf by pusia
package su.catlean.client.services.discord.ipc;

import org.jetbrains.annotations.NotNull;
import su.catlean.client.render.font.GlyphRasterizer;

public final class IpcUser {
   private final int value;
   private final int value2;
   private final int value3;
   private final int value4;
   private final char symbol;
   @NotNull
   private final GlyphRasterizer owner;

   public IpcUser(int value, int value2, int value3, int value4, char symbol, GlyphRasterizer owner) {
      this.value = value;
      this.value2 = value2;
      this.value3 = value3;
      this.value4 = value4;
      this.symbol = symbol;
      this.owner = owner;
   }

   public final int getValue() {
      return this.value;
   }

   public final int getValue2() {
      return this.value2;
   }

   public final int getValue3() {
      return this.value3;
   }

   public final int getValue4() {
      return this.value4;
   }

   public final char getSymbol() {
      return this.symbol;
   }

   @NotNull
   public final GlyphRasterizer getOwner() {
      return this.owner;
   }
}