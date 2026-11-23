// deobf by pusia
package su.catlean.api.event.events.render;

import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.Event;
import su.catlean.gofra.Gofra;

public final class EntityAlphaEvent extends Event {
   @NotNull
   public static final EntityAlphaEvent INSTANCE = new EntityAlphaEvent();
   @NotNull
   private static Vec3 pos = Vec3.ZERO;
   private static float alpha = 1.0F;

   private EntityAlphaEvent() {
   }

   @NotNull
   public final Vec3 getPos() {
      return pos;
   }

   public final void setPos(Vec3 vec3) {
      pos = vec3;
   }

   public final float getAlpha() {
      return alpha;
   }

   public final void setAlpha(float amount) {
      alpha = amount;
   }

   public final boolean call(Vec3 pos) {
      EntityAlphaEvent.pos = pos;
      alpha = 1.0F;
      this.setCancelled(false);
      Gofra.INSTANCE.drain(this);
      return this.getCancelled();
   }
}