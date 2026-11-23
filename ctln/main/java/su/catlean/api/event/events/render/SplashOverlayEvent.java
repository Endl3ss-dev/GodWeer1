// deobf by pusia
package su.catlean.api.event.events.render;

import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.server.packs.resources.ReloadInstance;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.catlean.api.event.Event;
import su.catlean.gofra.Gofra;

public final class SplashOverlayEvent extends Event {
   @NotNull
   public static final SplashOverlayEvent INSTANCE = new SplashOverlayEvent();
   private static boolean reloading;
   private static float progress;
   private static long reloadCompleteTime = -1L;
   private static long reloadStartTime = -1L;
   @Nullable
   private static ReloadInstance reload;
   @Nullable
   private static Consumer<Optional<Throwable>> exceptionHandler;
   @Nullable
   private static GuiGraphicsExtractor context;

   private SplashOverlayEvent() {
   }

   public final boolean getReloading() {
      return reloading;
   }

   public final void setReloading(boolean flag) {
      reloading = flag;
   }

   public final float getProgress() {
      return progress;
   }

   public final void setProgress(float amount) {
      progress = amount;
   }

   public final long getReloadCompleteTime() {
      return reloadCompleteTime;
   }

   public final void setReloadCompleteTime(long time) {
      reloadCompleteTime = time;
   }

   public final long getReloadStartTime() {
      return reloadStartTime;
   }

   public final void setReloadStartTime(long time) {
      reloadStartTime = time;
   }

   @Nullable
   public final ReloadInstance getReload() {
      return reload;
   }

   public final void setReload(ReloadInstance reloadInstance) {
      reload = reloadInstance;
   }

   @Nullable
   public final Consumer<Optional<Throwable>> getExceptionHandler() {
      return exceptionHandler;
   }

   public final void setExceptionHandler(Consumer<Optional<Throwable>> consumer) {
      exceptionHandler = consumer;
   }

   @Nullable
   public final GuiGraphicsExtractor getContext() {
      return context;
   }

   public final void setContext(GuiGraphicsExtractor guiGraphicsExtractor) {
      context = guiGraphicsExtractor;
   }

   public final boolean call(
      GuiGraphicsExtractor context, boolean flag, float amount, long time, long time2, ReloadInstance reloadInstance, Consumer<Optional<Throwable>> consumer
   ) {
      SplashOverlayEvent.context = context;
      reloading = flag;
      progress = amount;
      reloadCompleteTime = time;
      reloadStartTime = time2;
      reload = reloadInstance;
      exceptionHandler = consumer;
      this.setCancelled(false);
      Gofra.INSTANCE.drain(this);
      return this.getCancelled();
   }
}