// deobf by pusia
package su.catlean.api.event.events.render;

import com.mojang.blaze3d.vertex.PoseStack;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.Event;
import su.catlean.gofra.Gofra;

public final class EntitiesRenderEvent extends Event {
   @NotNull
   public static final EntitiesRenderEvent INSTANCE = new EntitiesRenderEvent();
   private static PoseStack stack;

   private EntitiesRenderEvent() {
   }

   @NotNull
   public final PoseStack getStack() {
      if (stack != null) {
         return stack;
      } else {
         Intrinsics.throwUninitializedPropertyAccessException("stack");
         return null;
      }
   }

   public final boolean call(PoseStack stack) {
      this.setCancelled(false);
      EntitiesRenderEvent.stack = stack;
      Gofra.INSTANCE.drain(this);
      return this.getCancelled();
   }
}