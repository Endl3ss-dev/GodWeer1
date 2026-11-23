// deobf by pusia
package su.catlean.api.data;

import kotlin.jvm.internal.Intrinsics;
import net.minecraft.ChatFormatting;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import su.catlean.client.commands.impl.BindCommand;
import su.catlean.client.features.modules.attack.AutoCrystal;
import su.catlean.client.services.ServerInfoService;
import su.catlean.utils.MinecraftHolder;

public final class Attempt {
   private long time;
   private int value;
   @NotNull
   private Vec3 pos;
   private boolean flag;
   private double amount;

   public Attempt(long time, int value, Vec3 pos, boolean flag) {
      this.time = time;
      this.value = value;
      this.pos = pos;
      this.flag = flag;
      this.amount = MinecraftHolder.getLocalPlayer().distanceToSqr(this.pos);
   }

   public final long getTime() {
      return this.time;
   }

   public final void setTime(long time) {
      this.time = time;
   }

   @NotNull
   public final Vec3 getPos() {
      return this.pos;
   }

   public final boolean isFlag() {
      return this.flag;
   }

   public final void setFlag(boolean flag) {
      this.flag = flag;
   }

   public final double getAmount() {
      return this.amount;
   }

   public final void setAmount(double amount) {
      this.amount = amount;
   }

   public final boolean isFlag2() {
      return Math.abs(this.amount - MinecraftHolder.getLocalPlayer().distanceToSqr(this.pos)) >= (this.isFlag3() ? 1.0F : 10.0F)
         || MinecraftHolder.getTime() - this.time > 3000L;
   }

   public final void update() {
      int value2 = this.value++;
      if (AutoCrystal.INSTANCE.getFailNotify() && this.isFlag3()) {
         BindCommand.INSTANCE.updateText(ChatFormatting.RED + "[Auto Crystal] Failed to " + (this.flag ? "place" : "break") + ", change your config!");
      }
   }

   public final boolean isFlag3() {
      return this.value >= Math.max(5, (int)((float)Math.ceil(ServerInfoService.defaultImpl(ServerInfoService.INSTANCE, null, 1, null) / 25.0F)));
   }

   public final long getTime2() {
      return this.time;
   }

   private final int getValue() {
      return this.value;
   }

   @NotNull
   public final Vec3 getPos2() {
      return this.pos;
   }

   public final boolean isFlag4() {
      return this.flag;
   }

   @NotNull
   @Override
   public String toString() {
      boolean flag2 = this.flag;
      Vec3 pos2 = this.pos;
      int value2 = this.value;
      long time2 = this.time;
      return "Attempt(time=" + time2 + ", attempts=" + value2 + ", pos=" + pos2 + ", place=" + flag2 + ")";
   }

   @Override
   public int hashCode() {
      int hashCode = Long.hashCode(this.time);
      hashCode = hashCode * 31 + Integer.hashCode(this.value);
      hashCode = hashCode * 31 + this.pos.hashCode();
      return hashCode * 31 + Boolean.hashCode(this.flag);
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (!(object instanceof Attempt attempt)) {
         return false;
      } else if (this.time != attempt.time) {
         return false;
      } else if (this.value != attempt.value) {
         return false;
      } else {
         return !Intrinsics.areEqual(this.pos, attempt.pos) ? false : this.flag == attempt.flag;
      }
   }
}