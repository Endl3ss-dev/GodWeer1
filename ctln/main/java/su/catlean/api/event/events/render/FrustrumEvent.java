// deobf by pusia
package su.catlean.api.event.events.render;

import kotlin.jvm.internal.Intrinsics;
import net.minecraft.client.renderer.culling.Frustum;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.Event;

public final class FrustrumEvent extends Event {
   @NotNull
   public static final FrustrumEvent INSTANCE = new FrustrumEvent();
   public static Frustum frustrum;

   private FrustrumEvent() {
   }

   @NotNull
   public final Frustum getFrustrum() {
      if (frustrum != null) {
         return frustrum;
      } else {
         Intrinsics.throwUninitializedPropertyAccessException("frustrum");
         return null;
      }
   }

   public final void setFrustrum(Frustum frustum) {
      frustrum = frustum;
   }
}