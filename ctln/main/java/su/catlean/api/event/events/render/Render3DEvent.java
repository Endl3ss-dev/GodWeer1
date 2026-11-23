// deobf by pusia
package su.catlean.api.event.events.render;

import com.mojang.blaze3d.vertex.PoseStack;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import su.catlean.api.event.Event;
import su.catlean.gofra.Gofra;

public final class Render3DEvent extends Event {
   @NotNull
   public static final Render3DEvent INSTANCE = new Render3DEvent();
   private static PoseStack stack;
   private static Matrix4f projection;

   private Render3DEvent() {
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

   @NotNull
   public final Matrix4f getProjection() {
      if (projection != null) {
         return projection;
      } else {
         Intrinsics.throwUninitializedPropertyAccessException("projection");
         return null;
      }
   }

   public final boolean call(PoseStack stack, Matrix4f projection) {
      this.setCancelled(false);
      Render3DEvent.stack = stack;
      Render3DEvent.projection = projection;
      Gofra.INSTANCE.drain(this);
      return this.getCancelled();
   }
}