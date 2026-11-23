// deobf by pusia
package su.catlean.client.services;

import kotlin.collections.ArrayDeque;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.render.Render3DEvent;
import su.catlean.api.service.Service;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;

public final class ChestOpenTracker implements Service {
   @NotNull
   public static final ChestOpenTracker INSTANCE = new ChestOpenTracker();
   @NotNull
   private static final ArrayDeque<Long> arrayDeque = new ArrayDeque();
   private static int value = 5;

   private ChestOpenTracker() {
   }

   public final int getValue() {
      return value;
   }

   public final void setValue(int value) {
      ChestOpenTracker.value = value;
   }

   @Flow
   public final void onRender3D(Render3DEvent event) {
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
            value = Math.max(arrayDeque.size(), 4);
            return;
         }

         arrayDeque.removeFirst();
      }
   }
}