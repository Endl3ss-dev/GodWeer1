// deobf by pusia
package su.catlean.client.render;

import kotlin.collections.ArrayDeque;
import org.jetbrains.annotations.NotNull;
import su.catlean.utils.MinecraftHolder;

public final class GradientQuad {
   @NotNull
   public static final GradientQuad INSTANCE = new GradientQuad();
   @NotNull
   private static final ArrayDeque<Long> arrayDeque = new ArrayDeque();
   private static int value;

   private GradientQuad() {
   }

   public final int getValue() {
      return value;
   }

   public final void applyLongValue() {
      long time = MinecraftHolder.getTime();
      arrayDeque.addLast(time);

      while (true) {
         Long firstOrNull = (Long)arrayDeque.firstOrNull();
         boolean flag;
         if (firstOrNull != null) {
            long value2 = firstOrNull.longValue();
            flag = value2 + 1000 < time;
         } else {
            flag = false;
         }

         if (!flag) {
            value = arrayDeque.size();
            return;
         }

         arrayDeque.removeFirst();
      }
   }
}