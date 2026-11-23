// deobf by pusia
package su.catlean.api.event.events;

import java.nio.file.Path;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.Event;
import su.catlean.gofra.Gofra;

public final class FilesDraggedEvent extends Event {
   @NotNull
   public static final FilesDraggedEvent INSTANCE = new FilesDraggedEvent();
   public static List<? extends Path> paths;

   private FilesDraggedEvent() {
   }

   @NotNull
   public final List<Path> getPaths() {
      if (paths != null) {
         return (List<Path>)paths;
      } else {
         Intrinsics.throwUninitializedPropertyAccessException("paths");
         return null;
      }
   }

   public final void setPaths(List<? extends Path> items) {
      paths = items;
   }

   public final boolean call(List<? extends Path> paths) {
      this.setPaths(paths);
      this.setCancelled(false);
      Gofra.INSTANCE.drain(this);
      return this.getCancelled();
   }
}