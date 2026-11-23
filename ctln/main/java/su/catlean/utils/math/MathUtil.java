// deobf by pusia
package su.catlean.utils.math;

import java.util.Random;
import kotlin.math.MathKt;
import kotlin.ranges.ClosedRange;
import org.jetbrains.annotations.NotNull;

public final class MathUtil {
   @NotNull
   private static final Random random = new Random(88005553535L);

   @NotNull
   public static final Random getRandom() {
      return random;
   }

   public static final int computeValue(int value, int value2, boolean flag) {
      return MathKt.roundToInt(value + (value2 - value) * (float)computeAmount(flag));
   }

   public static int defaultImpl(int value, int value2, boolean flag, int value3, Object object) {
      if ((value3 & 4) != 0) {
         flag = true;
      }

      return computeValue(value, value2, flag);
   }

   public static final float computeAmount(float amount, float amount2, boolean flag) {
      return amount + (amount2 - amount) * (float)computeAmount(flag);
   }

   public static float defaultImpl(float amount, float amount2, boolean flag, int value, Object object) {
      if ((value & 4) != 0) {
         flag = true;
      }

      return computeAmount(amount, amount2, flag);
   }

   public static final double computeAmount(double amount, double amount2, boolean flag) {
      return amount + (amount2 - amount) * computeAmount(flag);
   }

   public static double defaultImpl(double amount, double amount2, boolean flag, int value, Object object) {
      if ((value & 4) != 0) {
         flag = true;
      }

      return computeAmount(amount, amount2, flag);
   }

   public static final double computeAmount(ClosedRange range, boolean flag) {
      return computeAmount(((Number)range.getStart()).doubleValue(), ((Number)range.getEndInclusive()).doubleValue(), flag);
   }

   public static double defaultImpl(ClosedRange closedRange, boolean flag, int value, Object object) {
      if ((value & 2) != 0) {
         flag = true;
      }

      return computeAmount(closedRange, flag);
   }

   public static final float computeAmount2(ClosedRange range, boolean flag) {
      return computeAmount(((Number)range.getStart()).floatValue(), ((Number)range.getEndInclusive()).floatValue(), flag);
   }

   public static float defaultImpl2(ClosedRange closedRange, boolean flag, int value, Object object) {
      if ((value & 2) != 0) {
         flag = true;
      }

      return computeAmount2(closedRange, flag);
   }

   public static final int computeValue(ClosedRange range, boolean flag) {
      return computeValue(((Number)range.getStart()).intValue(), ((Number)range.getEndInclusive()).intValue(), flag);
   }

   public static int defaultImpl3(ClosedRange closedRange, boolean flag, int value, Object object) {
      if ((value & 2) != 0) {
         flag = true;
      }

      return computeValue(closedRange, flag);
   }

   public static final double computeAmount(boolean flag) {
      return random.nextDouble();
   }

   public static final double getRandom2() {
      return random.nextGaussian();
   }
}