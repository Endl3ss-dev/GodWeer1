// deobf by pusia
package su.catlean.utils.async;

import kotlin.jvm.internal.DefaultConstructorMarker;
import org.jetbrains.annotations.NotNull;
import su.catlean.utils.MinecraftHolder;

public final class ScheduledTask {
   @NotNull
   private final Runnable action;
   private int value;
   private byte value2;
   private boolean flag;
   private long time;

   public ScheduledTask(Runnable action, int value, byte value2, boolean flag, long time) {
      this.action = action;
      this.value = value;
      this.value2 = value2;
      this.flag = flag;
      this.time = time;
   }

   public ScheduledTask(Runnable runnable, int value, byte value2, boolean flag, long time, int value3, DefaultConstructorMarker defaultConstructorMarker) {
      if ((value3 & 4) != 0) {
         value2 = 10;
      }

      if ((value3 & 8) != 0) {
         flag = true;
      }

      if ((value3 & 16) != 0) {
         time = MinecraftHolder.getTime();
      }

      this(runnable, value, value2, flag, time);
   }

   @NotNull
   public final Runnable getAction() {
      return this.action;
   }

   public final int getValue() {
      return this.value;
   }

   public final void setValue(int value) {
      this.value = value;
   }

   public final byte getValue2() {
      return this.value2;
   }

   public final void setValue2(byte value) {
      this.value2 = value;
   }

   public final boolean isFlag() {
      return this.flag;
   }

   public final void setFlag(boolean flag) {
      this.flag = flag;
   }

   public final long getTime() {
      return this.time;
   }

   public final void setTime(long time) {
      this.time = time;
   }

   public final boolean isFlag2() {
      this.value = this.value - (this.flag ? 1 : (int)(MinecraftHolder.getTime() - this.time));
      if (this.value < 0) {
         this.action.run();
         return true;
      } else {
         this.time = MinecraftHolder.getTime();
         return false;
      }
   }
}