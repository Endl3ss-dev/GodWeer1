// deobf by pusia
package su.catlean.utils.animation;

import org.jetbrains.annotations.NotNull;
import su.catlean.client.services.NotificationManager;
import su.catlean.utils.MinecraftHolder;

public final class Animation {
   @NotNull
   private final NotificationManager.Notification.Type easing;
   private final long time;
   private float amount;
   private long time2;
   private float amount2;
   private float amount3;
   private boolean flag;

   public Animation(NotificationManager.Notification.Type easing, long time) {
      this.easing = easing;
      this.time = time;
      this.time2 = MinecraftHolder.getTime();
   }

   public final float getAmount() {
      return this.amount;
   }

   public final void setAmount(float amount) {
      this.amount = amount;
   }

   public final boolean isFlag() {
      return this.flag;
   }

   public final void setFlag(boolean flag) {
      this.flag = flag;
   }

   public final void applyDoubleValue(float amount) {
      if (this.amount3 != amount) {
         this.amount3 = amount;
         this.update();
      } else {
         this.flag = MinecraftHolder.getTime() - this.time > this.time2;
         if (this.flag) {
            this.amount = amount;
            return;
         }
      }

      double value = ((Number)this.easing.getCallback().invoke((double)(MinecraftHolder.getTime() - this.time2) / this.time)).doubleValue();
      this.amount = this.amount > this.amount3
         ? (float)(this.amount2 - (this.amount2 - this.amount3) * value)
         : (float)(this.amount2 + (this.amount3 - this.amount2) * value);
   }

   public final void updateAmount(float amount) {
      this.time2 = MinecraftHolder.getTime();
      this.amount2 = amount;
      this.flag = false;
   }

   public final void update() {
      this.updateAmount(this.amount);
   }
}