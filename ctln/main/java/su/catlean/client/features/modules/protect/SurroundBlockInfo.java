// deobf by pusia
package su.catlean.client.features.modules.protect;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.enums.HoleType;
import su.catlean.api.enums.Safety;

public final class SurroundBlockInfo {
   @NotNull
   private final BlockPos pos;
   @NotNull
   private final Safety safety;
   @NotNull
   private final HoleType shape;

   public SurroundBlockInfo(BlockPos pos, Safety safety, HoleType shape) {
      this.pos = pos;
      this.safety = safety;
      this.shape = shape;
   }

   @NotNull
   public final BlockPos getPos() {
      return this.pos;
   }

   @NotNull
   public final Safety getSafety() {
      return this.safety;
   }

   @NotNull
   public final HoleType getShape() {
      return this.shape;
   }

   @NotNull
   public final AABB fetchX(float amount) {
      double value = this.pos.getX();
      double value2 = this.pos.getY();
      double value3 = this.pos.getZ();

      return switch (SurroundBlockInfo.WhenMappings.values[this.shape.ordinal()]) {
         case 1 -> new AABB(value, value2, value3, value + 2.0, value2 + amount, value3 + 1.0);
         case 2 -> new AABB(value, value2, value3, value + 1.0, value2 + amount, value3 + 2.0);
         case 3 -> new AABB(value, value2, value3, value + 2.0, value2 + amount, value3 + 2.0);
         default -> new AABB(value, value2, value3, value + 1.0, value2 + amount, value3 + 1.0);
      };
   }

   public static final class WhenMappings {
      public static final int[] values;

      static {
         int[] values2 = new int[HoleType.values().length];

         try {
            values2[HoleType.DOUBLE_X.ordinal()] = 1;
         } catch (NoSuchFieldError exception) {
         }

         try {
            values2[HoleType.DOUBLE_Z.ordinal()] = 2;
         } catch (NoSuchFieldError exception2) {
         }

         try {
            values2[HoleType.QUAD.ordinal()] = 3;
         } catch (NoSuchFieldError exception3) {
         }

         values = values2;
      }
   }
}