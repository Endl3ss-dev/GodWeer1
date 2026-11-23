// deobf by pusia
package su.catlean.utils.async;

import java.util.ArrayDeque;
import java.util.Iterator;
import kotlin.jvm.functions.Function1;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.network.PostTasksProcessEvent;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.gofra.Flow;

public final class TaskScheduler {
   @NotNull
   public static final TaskScheduler INSTANCE = new TaskScheduler();
   @NotNull
   private static final ArrayDeque<Object> arrayDeque = new ArrayDeque();
   @NotNull
   private static final ArrayDeque<ScheduledTask> arrayDeque2 = new ArrayDeque();
   private static boolean flag;

   private TaskScheduler() {
   }

   @Flow(
      priority = 10
   )
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      this.applyAddLast(arg -> ((ScheduledTask)arg).isFlag() && ((ScheduledTask)arg).getValue2() == 10);
   }

   @Flow(
      priority = -10
   )
   private final void onPlayerUpdate2(PlayerUpdateEvent playerUpdateEvent) {
      this.applyAddLast(arg -> ((ScheduledTask)arg).isFlag() && ((ScheduledTask)arg).getValue2() != 10);
   }

   @Flow
   private final void onPostTasksProcess(PostTasksProcessEvent postTasksProcessEvent) {
      this.applyAddLast(arg -> !((ScheduledTask)arg).isFlag());
   }

   public final void applyAddLast(int value, byte value2, boolean flag, Runnable runnable) {
      ScheduledTask scheduledTask = new ScheduledTask(runnable, value, value2, flag, 0L, 16, null);
      if (TaskScheduler.flag) {
         arrayDeque2.addLast(scheduledTask);
      } else {
         arrayDeque.addLast(scheduledTask);
      }
   }

   public static void defaultImpl(TaskScheduler taskScheduler, int value, byte value2, boolean flag, Runnable runnable, int value3, Object object) {
      if ((value3 & 1) != 0) {
         value = 0;
      }

      if ((value3 & 2) != 0) {
         value2 = 10;
      }

      if ((value3 & 4) != 0) {
         flag = true;
      }

      taskScheduler.applyAddLast(value, value2, flag, runnable);
   }

   private final void applyAddLast(Function1 callback) {
      flag = true;

      try {
         Iterator<Object> iterator = arrayDeque.iterator();

         while (iterator.hasNext()) {
            ScheduledTask scheduledTask = (ScheduledTask)iterator.next();
            if ((Boolean)callback.invoke(scheduledTask) && scheduledTask.isFlag2()) {
               iterator.remove();
            }
         }
      } finally {
         flag = false;
      }

      while (!arrayDeque2.isEmpty()) {
         arrayDeque.addLast(arrayDeque2.removeFirst());
      }
   }
}