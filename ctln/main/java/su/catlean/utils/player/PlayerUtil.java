// deobf by pusia
package su.catlean.utils.player;

import su.catlean.utils.MinecraftHolder;

public final class PlayerUtil {
   private int value;

   public PlayerUtil() {
      this.update();
   }

   public final boolean testValue(int value) {
      if (this.getValue() < 0) {
         this.update();
      }

      return this.getValue() >= value;
   }

   public final boolean testValue2(int value) {
      if (this.getValue() < 0) {
         this.update();
      }

      boolean flag = this.getValue() >= value;
      if (flag) {
         this.update();
      }

      return flag;
   }

   public final void updateValue(int value) {
      this.value = (MinecraftHolder.getMinecraft().player == null ? 0 : MinecraftHolder.getMinecraft().player.tickCount) + value;
   }

   public final void update() {
      this.value = MinecraftHolder.getMinecraft().player == null ? 0 : MinecraftHolder.getMinecraft().player.tickCount;
   }

   private final int getValue() {
      return MinecraftHolder.getMinecraft().player == null ? 0 : MinecraftHolder.getMinecraft().player.tickCount - this.value;
   }
}