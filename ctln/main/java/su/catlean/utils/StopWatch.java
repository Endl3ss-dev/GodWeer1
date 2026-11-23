// deobf by pusia
package su.catlean.utils;

public final class StopWatch {
   private long time;

   public StopWatch() {
      this.update();
   }

   public final boolean testValue(int value) {
      return MinecraftHolder.getTime() - this.time >= value;
   }

   public final boolean testValue2(int value) {
      boolean flag = MinecraftHolder.getTime() - this.time >= value;
      if (flag) {
         this.update();
      }

      return flag;
   }

   public final void applyCurrentTimeMillis(int value) {
      this.time = System.currentTimeMillis() - value;
   }

   public final long getTime() {
      return MinecraftHolder.getTime() - this.time;
   }

   public final void update() {
      this.time = MinecraftHolder.getTime();
   }
}