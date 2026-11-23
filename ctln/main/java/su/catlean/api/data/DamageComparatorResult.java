// deobf by pusia
package su.catlean.api.data;

import org.jetbrains.annotations.NotNull;

public final class DamageComparatorResult {
   private final float amount;
   private final float amount2;
   private final boolean flag;
   private final boolean flag2;

   public DamageComparatorResult(float amount, float amount2, boolean flag, boolean flag2) {
      this.amount = amount;
      this.amount2 = amount2;
      this.flag = flag;
      this.flag2 = flag2;
   }

   public final float getAmount() {
      return this.amount;
   }

   public final float getAmount2() {
      return this.amount2;
   }

   public final boolean isFlag() {
      return this.flag;
   }

   public final boolean isFlag2() {
      return this.flag2;
   }

   public final float getAmount3() {
      return this.amount;
   }

   public final float getAmount4() {
      return this.amount2;
   }

   public final boolean isFlag3() {
      return this.flag;
   }

   public final boolean isFlag4() {
      return this.flag2;
   }

   @NotNull
   public final DamageComparatorResult computeDamageComparatorResult(float amount, float amount2, boolean flag, boolean flag2) {
      return new DamageComparatorResult(amount, amount2, flag, flag2);
   }

   @NotNull
   @Override
   public String toString() {
      boolean flag22 = this.flag2;
      boolean flag3 = this.flag;
      float amount22 = this.amount2;
      float amount3 = this.amount;
      return "DamageComparatorResult(damage=" + amount3 + ", selfDamage=" + amount22 + ", override=" + flag3 + ", lethal=" + flag22 + ")";
   }

   @Override
   public int hashCode() {
      int hashCode = Float.hashCode(this.amount);
      hashCode = hashCode * 31 + Float.hashCode(this.amount2);
      hashCode = hashCode * 31 + Boolean.hashCode(this.flag);
      return hashCode * 31 + Boolean.hashCode(this.flag2);
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (!(object instanceof DamageComparatorResult damageComparatorResult)) {
         return false;
      } else if (Float.compare(this.amount, damageComparatorResult.amount) != 0) {
         return false;
      } else if (Float.compare(this.amount2, damageComparatorResult.amount2) != 0) {
         return false;
      } else {
         return this.flag != damageComparatorResult.flag ? false : this.flag2 == damageComparatorResult.flag2;
      }
   }
}