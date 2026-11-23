// deobf by pusia
package su.catlean.api.event.events.player;

import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.Event;

public class ElytraEvent extends Event {
   @NotNull
   private Vec3 vec = Vec3.ZERO;

   public ElytraEvent() {
   }

   @NotNull
   public final Vec3 getVec() {
      return this.vec;
   }

   public final void setVec(Vec3 vec3) {
      this.vec = vec3;
   }
}