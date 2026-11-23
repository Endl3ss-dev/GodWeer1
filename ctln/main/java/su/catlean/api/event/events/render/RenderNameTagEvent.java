// deobf by pusia
package su.catlean.api.event.events.render;

import kotlin.jvm.internal.Intrinsics;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.Event;
import su.catlean.gofra.Gofra;

public final class RenderNameTagEvent extends Event {
   @NotNull
   public static final RenderNameTagEvent INSTANCE = new RenderNameTagEvent();
   public static EntityRenderState state;

   private RenderNameTagEvent() {
   }

   @NotNull
   public final EntityRenderState getState() {
      if (state != null) {
         return state;
      } else {
         Intrinsics.throwUninitializedPropertyAccessException("state");
         return null;
      }
   }

   public final void setState(EntityRenderState entityRenderState) {
      state = entityRenderState;
   }

   public final boolean call(EntityRenderState state) {
      this.setState(state);
      this.setCancelled(false);
      Gofra.INSTANCE.drain(this);
      return this.getCancelled();
   }
}