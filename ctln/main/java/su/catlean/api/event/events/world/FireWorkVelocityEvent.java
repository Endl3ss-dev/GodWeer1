// deobf by pusia
package su.catlean.api.event.events.world;

import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.catlean.api.event.Event;
import su.catlean.gofra.Gofra;

public final class FireWorkVelocityEvent extends Event {
   @NotNull
   public static final FireWorkVelocityEvent INSTANCE = new FireWorkVelocityEvent();
   @Nullable
   private static Vec3 velocity;

   private FireWorkVelocityEvent() {
   }

   @Nullable
   public final Vec3 getVelocity() {
      return velocity;
   }

   public final void setVelocity(Vec3 vec3) {
      velocity = vec3;
   }

   public final boolean call(Vec3 vec) {
      velocity = vec;
      this.setCancelled(false);
      Gofra.INSTANCE.drain(this);
      return this.getCancelled();
   }
}