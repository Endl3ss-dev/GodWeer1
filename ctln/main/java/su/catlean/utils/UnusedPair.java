// deobf by pusia
package su.catlean.utils;

import org.jetbrains.annotations.NotNull;

public final class UnusedPair {
   @NotNull
   public static final UnusedPair INSTANCE = new UnusedPair();

   private UnusedPair() {
   }

   public final double computeAmount(Number a) {
      return Math.sqrt(1.0 - (a.doubleValue() - 1.0) * (a.doubleValue() - 1.0));
   }

   public final double computeAmount2(Number x) {
      return 1.0 - (0.5 * Math.sin(Math.PI * x.doubleValue() + (Math.PI / 2)) + 0.5);
   }
}