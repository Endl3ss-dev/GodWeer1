// deobf by pusia
package su.catlean.client.render;

import kotlin.jvm.internal.DefaultConstructorMarker;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.NotNull;
import su.catlean.utils.MinecraftHolder;

public final class BlockAnimation {
   @NotNull
   private final BlockPos pos;
   private float amount;
   private float amount2;

   public BlockAnimation(BlockPos pos, float amount, float amount2) {
      this.pos = pos;
      this.amount = amount;
      this.amount2 = amount2;
   }

   public BlockAnimation(BlockPos blockPos, float amount, float amount2, int value, DefaultConstructorMarker defaultConstructorMarker) {
      if ((value & 2) != 0) {
         amount = 0.0F;
      }

      if ((value & 4) != 0) {
         amount2 = 0.0F;
      }

      this(blockPos, amount, amount2);
   }

   @NotNull
   public final BlockPos getPos() {
      return this.pos;
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
      this.amount2 = this.amount;
      this.amount += 0.025F;
      return MinecraftHolder.getClientLevel().isEmptyBlock(this.pos) ? true : this.amount > 2.0F;
   }
}