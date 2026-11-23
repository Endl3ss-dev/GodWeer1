// deobf by pusia
package su.catlean.api.data;

import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.math.MathKt;
import kotlin.ranges.RangesKt;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.enums.SwitchMode;
import su.catlean.mixins.accessors.LocalPlayerAccessor;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.math.VectorUtil;

public final class Rotation {
   @NotNull
   public static final VectorUtil vectorUtil = new VectorUtil(null);
   private float amount;
   private float amount2;
   private boolean flag;
   @NotNull
   private SwitchMode moveFix;

   public Rotation(float amount, float amount2, boolean flag, SwitchMode moveFix) {
      this.amount = amount;
      this.amount2 = amount2;
      this.flag = flag;
      this.moveFix = moveFix;
   }

   public Rotation(float amount, float amount2, boolean flag, SwitchMode switchMode, int value, DefaultConstructorMarker defaultConstructorMarker) {
      if ((value & 4) != 0) {
         flag = false;
      }

      if ((value & 8) != 0) {
         switchMode = SwitchMode.NONE;
      }

      this(amount, amount2, flag, switchMode);
   }

   public final float getAmount() {
      return this.amount;
   }

   public final void setAmount(float amount) {
      this.amount = amount;
   }

   public final float getAmount2() {
      return this.amount2;
   }

   public final void setAmount2(float amount) {
      this.amount2 = amount;
   }

   public final boolean isFlag() {
      return this.flag;
   }

   public final void setFlag(boolean flag) {
      this.flag = flag;
   }

   @NotNull
   public final SwitchMode getMoveFix() {
      return this.moveFix;
   }

   @NotNull
   public final Rotation getRotation() {
      double value = Math.pow(((Number)MinecraftHolder.getMinecraft().options.sensitivity().get()).doubleValue() * 0.6 + 0.2, 3) * 8.0 * 0.15F;
      float wrapDegrees = Mth.wrapDegrees(this.amount - ((LocalPlayerAccessor)MinecraftHolder.getLocalPlayer()).getLastYaw());
      float wrapDegrees2 = Mth.wrapDegrees(this.amount2 - ((LocalPlayerAccessor)MinecraftHolder.getLocalPlayer()).getLastPitch());
      double roundToInt = MathKt.roundToInt(wrapDegrees / value) * value;
      double roundToInt2 = MathKt.roundToInt(wrapDegrees2 / value) * value;
      this.amount = ((LocalPlayerAccessor)MinecraftHolder.getLocalPlayer()).getLastYaw() + (float)roundToInt;
      this.amount2 = RangesKt.coerceIn(((LocalPlayerAccessor)MinecraftHolder.getLocalPlayer()).getLastPitch() + (float)roundToInt2, -90.0F, 90.0F);
      return this;
   }

   @NotNull
   public final Rotation computeRotation(SwitchMode v) {
      this.moveFix = v;
      return this;
   }

   @NotNull
   public final Rotation computeRotation(boolean flag) {
      this.flag = flag;
      return this;
   }

   public final float getAmount3() {
      return this.amount;
   }

   public final float getAmount4() {
      return this.amount2;
   }

   public final boolean isFlag2() {
      return this.flag;
   }

   @NotNull
   public final SwitchMode getMoveFix2() {
      return this.moveFix;
   }

   @NotNull
   @Override
   public String toString() {
      SwitchMode moveFix2 = this.moveFix;
      boolean flag2 = this.flag;
      float amount22 = this.amount2;
      float amount3 = this.amount;
      return "Rotation(yaw=" + amount3 + ", pitch=" + amount22 + ", grim=" + flag2 + ", moveFix=" + moveFix2 + ")";
   }

   @Override
   public int hashCode() {
      int hashCode = Float.hashCode(this.amount);
      hashCode = hashCode * 31 + Float.hashCode(this.amount2);
      hashCode = hashCode * 31 + Boolean.hashCode(this.flag);
      return hashCode * 31 + this.moveFix.hashCode();
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (!(object instanceof Rotation rotation)) {
         return false;
      } else if (Float.compare(this.amount, rotation.amount) != 0) {
         return false;
      } else if (Float.compare(this.amount2, rotation.amount2) != 0) {
         return false;
      } else {
         return this.flag != rotation.flag ? false : this.moveFix == rotation.moveFix;
      }
   }
}