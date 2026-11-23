// deobf by pusia
package su.catlean.api.event.events.render;

import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.catlean.api.event.Event;
import su.catlean.gofra.Gofra;

public final class RenderEntityEvent extends Event {
   @NotNull
   public static final RenderEntityEvent INSTANCE = new RenderEntityEvent();
   @Nullable
   private static Entity state;

   private RenderEntityEvent() {
   }

   @Nullable
   public final Entity getState() {
      return state;
   }

   public final void setState(Entity entity) {
      state = entity;
   }

   public final boolean call(Entity state) {
      RenderEntityEvent.state = state;
      this.setCancelled(false);
      Gofra.INSTANCE.drain(this);
      return this.getCancelled();
   }
}