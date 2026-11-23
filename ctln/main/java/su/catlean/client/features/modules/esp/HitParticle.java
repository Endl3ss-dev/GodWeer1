// deobf by pusia
package su.catlean.client.features.modules.esp;

import kotlin.jvm.internal.DefaultConstructorMarker;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public final class HitParticle {
   @NotNull
   private final Entity entity;
   private final float amount;
   private final double amount2;
   private final double amount3;
   private final double amount4;
   private int value;

   public HitParticle(Entity entity, float amount, double amount2, double amount3, double amount4, int value) {
      this.entity = entity;
      this.amount = amount;
      this.amount2 = amount2;
      this.amount3 = amount3;
      this.amount4 = amount4;
      this.value = value;
   }

   public HitParticle(
      Entity entity, float amount, double amount2, double amount3, double amount4, int value, int value2, DefaultConstructorMarker defaultConstructorMarker
   ) {
      if ((value2 & 32) != 0) {
         value = 0;
      }

      this(entity, amount, amount2, amount3, amount4, value);
   }

   @NotNull
   public final Entity getEntity() {
      return this.entity;
   }

   public final float getAmount() {
      return this.amount;
   }

   public final double getAmount2() {
      return this.amount2;
   }

   public final double getAmount3() {
      return this.amount3;
   }

   public final double getAmount4() {
      return this.amount4;
   }

   public final int getValue() {
      return this.value;
   }

   public final void setValue(int value) {
      this.value = value;
   }
}