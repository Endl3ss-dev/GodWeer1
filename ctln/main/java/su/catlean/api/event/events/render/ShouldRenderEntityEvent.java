// deobf by pusia
package su.catlean.api.event.events.render;

import kotlin.jvm.internal.Intrinsics;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.Event;
import su.catlean.gofra.Gofra;

public final class ShouldRenderEntityEvent extends Event {
   @NotNull
   public static final ShouldRenderEntityEvent INSTANCE = new ShouldRenderEntityEvent();
   public static Entity entity;

   private ShouldRenderEntityEvent() {
   }

   @NotNull
   public final Entity getEntity() {
      if (entity != null) {
         return entity;
      } else {
         Intrinsics.throwUninitializedPropertyAccessException("entity");
         return null;
      }
   }

   public final void setEntity(Entity entity) {
      ShouldRenderEntityEvent.entity = entity;
   }

   public final boolean call(Entity entity) {
      this.setCancelled(false);
      this.setEntity(entity);
      Gofra.INSTANCE.drain(this);
      return this.getCancelled();
   }
}