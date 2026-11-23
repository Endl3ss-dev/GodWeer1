// deobf by pusia
package su.catlean.api.addon.feature;

import org.jetbrains.annotations.NotNull;

public final class Rotation {
   private final float yaw;
   private final float pitch;

   public Rotation(float amount, float amount2) {
      this.yaw = amount;
      this.pitch = amount2;
   }

   public final float getYaw() {
      return this.yaw;
   }

   public final float getPitch() {
      return this.pitch;
   }

   public final float component1() {
      return this.yaw;
   }

   public final float component2() {
      return this.pitch;
   }

   @NotNull
   public final Rotation copy(float amount, float amount2) {
      return new Rotation(amount, amount2);
   }

   @NotNull
   @Override
   public String toString() {
      return "Rotation(yaw=" + this.yaw + ", pitch=" + this.pitch + ")";
   }

   @Override
   public int hashCode() {
      int hashCode = Float.hashCode(this.yaw);
      return hashCode * 31 + Float.hashCode(this.pitch);
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (!(object instanceof Rotation rotation)) {
         return false;
      } else {
         return Float.compare(this.yaw, rotation.yaw) != 0 ? false : Float.compare(this.pitch, rotation.pitch) == 0;
      }
   }
}