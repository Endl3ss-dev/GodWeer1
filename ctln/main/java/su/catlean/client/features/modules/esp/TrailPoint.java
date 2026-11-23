// deobf by pusia
package su.catlean.client.features.modules.esp;

import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import su.catlean.utils.StopWatch;

public final class TrailPoint {
   @NotNull
   private final Vec3 vec;
   private final float amount;
   private final float amount2;
   @NotNull
   private final StopWatch life;

   public TrailPoint(Vec3 vec, float amount, float amount2, StopWatch life) {
      this.vec = vec;
      this.amount = amount;
      this.amount2 = amount2;
      this.life = life;
   }

   @NotNull
   public final Vec3 getVec() {
      return this.vec;
   }

   public final float getAmount() {
      return this.amount;
   }

   public final float getAmount2() {
      return this.amount2;
   }

   @NotNull
   public final StopWatch getLife() {
      return this.life;
   }
}