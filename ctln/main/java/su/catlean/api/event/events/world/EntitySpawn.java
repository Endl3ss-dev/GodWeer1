// deobf by pusia
package su.catlean.api.event.events.world;

import kotlin.jvm.internal.Intrinsics;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.Event;
import su.catlean.gofra.Gofra;

public final class EntitySpawn extends Event {
   @NotNull
   public static final EntitySpawn INSTANCE = new EntitySpawn();
   private static Entity entity;

   private EntitySpawn() {
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

   public final boolean call(Entity entity) {
      this.setCancelled(false);
      EntitySpawn.entity = entity;
      Gofra.INSTANCE.drain(this);
      return this.getCancelled();
   }
}