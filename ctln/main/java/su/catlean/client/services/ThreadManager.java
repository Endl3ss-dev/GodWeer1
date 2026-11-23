// deobf by pusia
package su.catlean.client.services;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.client.PostTickEvent;
import su.catlean.api.event.events.client.TickEvent;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.service.Service;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;

public final class ThreadManager implements Service {
   @NotNull
   public static final ThreadManager INSTANCE = new ThreadManager();
   @NotNull
   private static AtomicBoolean atomicBoolean = new AtomicBoolean(false);
   @NotNull
   private static ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
   @NotNull
   private static List<Object> items = CollectionsKt.emptyList();

   private ThreadManager() {
   }

   @NotNull
   public final AtomicBoolean getAtomicBoolean() {
      return atomicBoolean;
   }

   @NotNull
   public final ExecutorService getExecutorService() {
      return executorService;
   }

   @NotNull
   public final List getItems() {
      return items;
   }

   @Flow(
      priority = 20
   )
   private final void onTick(TickEvent tickEvent) {
      atomicBoolean.set(true);
   }

   @Flow(
      priority = -20
   )
   private final void onPostTick(PostTickEvent postTickEvent) {
      atomicBoolean.set(false);
   }

   public final boolean isFlag() {
      return MinecraftHolder.getMinecraft().isSameThread() || !atomicBoolean.get();
   }

   @Flow(
      priority = 20
   )
   public final void onPlayerUpdate(PlayerUpdateEvent e) {
      items = Lists.newArrayList(MinecraftHolder.getClientLevel().players());
   }

   public static final class WorkerTask implements Runnable {
      public final int value;
      public final Function0 callback;

      public WorkerTask(int value, Function0 callback) {
         this.value = value;
         this.callback = callback;
      }

      @Override
      public final void run() {
         try {
            Thread.sleep(this.value);
         } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return;
         }
         this.callback.invoke();
      }
   }
}